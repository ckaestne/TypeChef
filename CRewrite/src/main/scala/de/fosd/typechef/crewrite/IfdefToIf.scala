package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.crewrite.CASTEnv._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.featureexpr.sat._
import collection.mutable.ListBuffer
import java.util
import util.IdentityHashMap
import java.io.FileWriter
import scala.Some
import de.fosd.typechef.conditional.One
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.conditional.Choice
import scala.Tuple4
import scala.Tuple2

/**
 * strategies to rewrite ifdefs to ifs
 */

class IfdefToIf extends ASTNavigation with ConditionalNavigation {

  val CONFIGPREFIX = "v_"
  var counter = 0
  var IdMap: Map[FeatureExpr, Int] = Map()
  var IdMap2: Map[FeatureExpr, Int] = Map()
  var IdSet: Set[FeatureExpr] = Set()
  var fctMap: Map[Id, Map[FeatureExpr, String]] = Map()
  var jmpMap: Map[String, Map[FeatureExpr, String]] = Map()
  var replaceId: IdentityHashMap[Id, FeatureExpr] = new IdentityHashMap()
  var typeDefs: ListBuffer[Id] = ListBuffer()
  var alreadyReplaced: ListBuffer[Id] = ListBuffer()
  val toBeReplaced: util.IdentityHashMap[Product, Product] = new IdentityHashMap()
  var liftOptReplaceMap: Map[Opt[_], List[Opt[_]]] = Map()
  val idsToBeReplaced: IdentityHashMap[Id, List[FeatureExpr]] = new IdentityHashMap()

  //    private val rewriteStrategy = everywherebu(rule {
  //        case Opt(f, stmt: Statement) if (!f.isTautology) =>
  //            Opt(base, IfStatement(featureToCExpr(f), One(stmt), List(), None))
  //        case Choice(f, One(a: Statement), One(b: Statement)) =>
  //            One(IfStatement(featureToCExpr(f), One(a), List(), Some(One(b))))
  //    })
  //
  //    def rewrite(ast: AST): AST = {
  //        rewriteStrategy(ast).get.asInstanceOf[AST]
  //    }

  // this method replaces a given element e within a structure t with the elements
  // n; we only match elements inside lists (case l: List[Opt[T]] => )
  def replace[T <: Product](t: T, e: Opt[_], n: List[Opt[_]]): T = {
    val r = all(rule {
      case l: List[Opt[_]] => l.flatMap({
        x => if (x.eq(e)) n else x :: Nil
      })
    })

    r(t).get.asInstanceOf[T]
  }

  def transformAsts(asts: List[Tuple4[TranslationUnit, ASTEnv, IdentityHashMap[Id, List[Id]], String]]): List[Tuple2[AST, String]] = {
    asts.foreach(x =>
      fillIdMap(x._1, x._2)
    )
    asts.map(x => (transformAst(x._1, x._2, x._3), x._4))
  }

  def needTrueExpression(featureSet: List[FeatureExpr]): Boolean = {
    featureSet.foreach(x => if (!featureSet.exists(y => x.&(y).isContradiction())) {
      return true
    })
    return false
  }

  def replaceId[T <: Product](t: T, n: Id, o: Id): T = {
    val r = manybu(rule {
      case x: Id =>
        if (o.eq(x)) {
          n
        } else {
          o
        }
    })
    r(t).get.asInstanceOf[T]
  }

  def replaceAny[T <: Product, U <: Product](t: T, n: U, o: U)(implicit m: ClassManifest[U]): T = {
    println("Start!")
    val r = oncebu(rule {
      case x: Product if (x.eq(o)) =>
        println("Replace!")
        n
    })
    r(t).get.asInstanceOf[T]
  }

  def replaceAnyIdHashMap[T <: Product, U <: Product](t: T, ihm: util.IdentityHashMap[Product, Product])(implicit m: ClassManifest[U]): T = {
    val r = manytd(rule {
      case x: U =>
        if (ihm.containsKey(x)) {
          //println("Replacing " + x + " with " + ihm.get(x))
          ihm.get(x)
        } else {
          x
        }
    })
    if (ihm.size() > 0) {
      r(t).get.asInstanceOf[T]
    } else {
      t
    }

  }

  def replaceOpt[T <: Product](t: T, n: Opt[_], o: Opt[_]): T = {
    println("Start!")
    val r = manybu(rule {
      case x: Opt[_] =>
        if (x.eq(o)) {
          println("Replace!")
          n
        } else {
          o
        }
    })
    r(t).get.asInstanceOf[T]
  }

  def replaceOnce[T <: Product](t: T, e: Opt[_], n: Opt[_]): T = {
    val r = oncetd(rule {
      case l: List[Opt[_]] => l.flatMap({
        x => if (x.eq(e)) n :: Nil else x :: Nil
      })
    })
    r(t).get.asInstanceOf[T]
  }

  def replaceSame[T <: Product](t: T, e: Opt[_], n: Opt[_]): T = {
    val r = manytd(rule {
      case l: List[Opt[_]] => l.flatMap({
        x => if (x.eq(e)) n :: Nil else x :: Nil
      })
    })
    r(t).get.asInstanceOf[T]
  }

  def replaceOptByOpts[T <: Product](t: T, e: Opt[_], n: List[Opt[_]]): T = {
    // println("Replacing\n" + e + "\nwith\n" + n + "\n\nin:\n" + t +"\n\n")
    val r = manytd(rule {
      case l: List[Opt[_]] => l.flatMap({
        x => if (x.eq(e)) n else x :: Nil
      })
    })
    r(t).get.asInstanceOf[T]
  }

  def replaceWithListOnce[T <: Product](t: T, e: Opt[_], n: List[Opt[_]]): T = {
    val r = oncetd(rule {
      case l: List[Opt[_]] => l.flatMap({
        x => if (x.eq(e)) n else x :: Nil
      })
    })
    r(t).get.asInstanceOf[T]
  }

  def replaceWithMap[T <: Product](t: T, map: Map[Opt[_], List[Opt[_]]]): T = {
    val r = manytd(rule {
      case l: List[Opt[_]] => l.flatMap({
        x => if (map.contains(x)) map.get(x).get else x :: Nil
      })
    })
    r(t).get.asInstanceOf[T]
  }

  def insertBefore[T <: Product](t: T, mark: Opt[_], insert: Opt[_]): T = {
    val r = oncetd(rule {
      case l: List[Opt[_]] => l.flatMap({
        x => if (x.eq(mark)) insert :: x :: Nil else x :: Nil
      })
    })
    r(t).get.asInstanceOf[T]
  }

  /*
  Converts a feature expression to a condition in the c programming language. def(x64) becomes options.x64
   */
  def featureToCExpr(feature: FeatureExpr): Expr = feature match {
    case d: DefinedExternal => PostfixExpr(Id("options"), PointerPostfixSuffix(".", Id(d.feature.toLowerCase())))
    case a: And =>
      val l = a.clauses.toList
      var del = List[Opt[NArySubExpr]]()
      for (e <- l.tail)
        del = del ++ List(Opt(True, NArySubExpr("&&", featureToCExpr(e))))
      NAryExpr(featureToCExpr(l.head), del)
    case o: Or =>
      val l = o.clauses.toList
      var del = List[Opt[NArySubExpr]]()
      for (e <- l.tail)
        del = del ++ List(Opt(FeatureExprFactory.True, NArySubExpr("||", featureToCExpr(e))))
      NAryExpr(featureToCExpr(l.head), del)
    case Not(n) => UnaryOpExpr("!", featureToCExpr(n))
  }

  /*
 Creates a file including an external int, a function, a struct with all features and an init function for that struct
  */
  def writeOptionFile(ast: AST) = {
    val features = filterFeatures(ast)
    val optionsAst = definedExternalToAst(features)

    val fw = new FileWriter("opt.h")
    fw.write(PrettyPrinter.print(optionsAst))
    fw.close()
  }

  /*
  Creates an AST including an external int, a function, a struct with all features and an init function for that struct
   */
  def getOptionFile(ast: AST): TranslationUnit = {
    val features = filterFeatures(ast)
    val optionsAst = definedExternalToAst(features)
    optionsAst
  }

  def definedExternalToAst(defExSet: Set[DefinedExternal]): TranslationUnit = {
    val externDeclaration = Opt(True, Declaration(List(Opt(True, ExternSpecifier())), List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("int__VERIFIER_nondet_int"), List(Opt(True, DeclParameterDeclList(List(Opt(True, PlainParameterDeclaration(List(Opt(True, VoidSpecifier()))))))))), List(), None)))))

    val function = Opt(True, FunctionDef(List(Opt(True, IntSpecifier())), AtomicNamedDeclarator(List(), Id("select_one"), List(Opt(True, DeclIdentifierList(List())))), List(), CompoundStatement(List(Opt(True, IfStatement(One(PostfixExpr(Id("__VERIFIER_NONDET_INT"), FunctionCall(ExprList(List())))), One(CompoundStatement(List(Opt(True, ReturnStatement(Some(Constant("1"))))))), List(), Some(One(CompoundStatement(List(Opt(True, ReturnStatement(Some(Constant("0"))))))))))))))

    val structDeclList = defExSet.map(x => {
      Opt(True, StructDeclaration(List(Opt(True, IntSpecifier())), List(Opt(True, StructDeclarator(AtomicNamedDeclarator(List(), Id(x.feature.toLowerCase), List()), None, List())))))
    }).toList
    val structDeclaration = Opt(True, Declaration(List(Opt(True, StructOrUnionSpecifier(false, Some(Id("options")), Some(structDeclList)))), List(Opt(True, InitDeclaratorI(AtomicNamedDeclarator(List(), Id("ifdef_options"), List()), List(), None)))))

    val cmpStmt = defExSet.map(x => {
      Opt(True, ExprStatement(AssignExpr(PostfixExpr(Id("ifdef_options"), PointerPostfixSuffix(".", Id(x.feature.toLowerCase()))), "=", PostfixExpr(Id("select_one"), FunctionCall(ExprList(List()))))))
    }).toList
    val initFunction = Opt(True, FunctionDef(List(Opt(True, VoidSpecifier())), AtomicNamedDeclarator(List(), Id("initOptions"), List(Opt(True, DeclIdentifierList(List())))), List(), CompoundStatement(cmpStmt)))

    val result = TranslationUnit(List(externDeclaration, function, structDeclaration, initFunction))
    result
  }

  /*
  Filteres a given product for feature expressions which are not True and returns a set including each single feature
   */
  def filterFeatures(a: Any): Set[DefinedExternal] = {
    def getFeatureExpressions(a: Any): List[FeatureExpr] = {
      a match {
        case o: Opt[_] => (if (o.feature == FeatureExprFactory.True) List() else List(o.feature)) ++ o.productIterator.toList.flatMap(getFeatureExpressions(_))
        case l: List[_] => l.flatMap(getFeatureExpressions(_))
        case p: Product => p.productIterator.toList.flatMap(getFeatureExpressions(_))
        case t: FeatureExpr => if (t == FeatureExprFactory.True) List() else List(t)
        case _ => List()
      }
    }
    getFeatureExpressions(a).flatMap(x => x.collectDistinctFeatures2).toSet
  }

  /*
 Filteres a given product for feature expressions which are not True and returns a set of all different feature expressions
  */
  def getSingleFeatureSet(a: Any, env: ASTEnv): Set[FeatureExpr] = {
    def getFeatureExpressions(a: Any, env: ASTEnv): List[FeatureExpr] = {
      a match {
        case o: Opt[_] => (if (o.feature == FeatureExprFactory.True) List() else List(o.feature)) ++ o.productIterator.toList.flatMap(getFeatureExpressions(_, env))
        case l: List[_] => l.flatMap(getFeatureExpressions(_, env))
        case p: Product => p.productIterator.toList.flatMap(getFeatureExpressions(_, env))
        case t: FeatureExpr => if (t == FeatureExprFactory.True) List() else List(t)
        case _ => List()
      }
    }
    getFeatureExpressions(a, env).toSet
  }

  def getFeatureExpressions(a: Any, env: ASTEnv): List[FeatureExpr] = {
    var lst: ListBuffer[FeatureExpr] = ListBuffer()
    val r = breadthfirst(query {
      case Opt(f, _) =>
        if (!f.equivalentTo(FeatureExprFactory.True) && !lst.contains(f)) {
          lst += f
        }
    })
    r(a).get
    lst.toList
  }

  def filterInvariableOpts(a: Any, env: ASTEnv): List[Opt[_]] = {
    a match {
      case o: Opt[_] => if (isVariable(o, env)) List(o) else List() ++ o.productIterator.toList.flatMap(filterInvariableOpts(_, env))
      case l: List[_] => l.flatMap(filterInvariableOpts(_, env))
      case p: Product => p.productIterator.toList.flatMap(filterInvariableOpts(_, env))
      case _ => List()
    }
  }

  /*
  This method fills the IdMap which is used to map a feature expression to a number.
   */
  def fillIdMap(a: Any, env: ASTEnv) = {
    if (IdMap.size == 0) {
      IdMap += (FeatureExprFactory.True -> IdMap.size)
    }
    getSingleFeatureSet(a, env).foreach(x => if (!IdMap.contains(x)) {
      IdMap += (x -> IdMap.size)
    })
  }

  /*
  Creates all possible 2^n combinations for a list of n raw feature expressions. List(def(x64), def(x86)) becomes
  List(def(x64)&def(x86),!def(x64)&def(x86),def(x64)&!def(x86),!def(x64)&!def(x86).
   */
  def getFeatureCombinations(lst: List[FeatureExpr]): List[FeatureExpr] = {
    if (lst.size == 0) {
      List()
    } else if (lst.size == 1) {
      lst.head :: List(lst.head.not())
    } else {
      getFeatureCombinations(lst.tail).flatMap(x => x.&(lst.head) :: List(x.&(lst.head.not())))
    }
  }

  def filterVariableOpts(a: Any, env: ASTEnv): List[Opt[_]] = {
    a match {
      case o: Opt[_] => if (o.feature != FeatureExprFactory.True) List(o) else List() ++ o.productIterator.toList.flatMap(filterVariableOpts(_, env))
      case l: List[_] => l.flatMap(filterVariableOpts(_, env))
      case p: Product => p.productIterator.toList.flatMap(filterVariableOpts(_, env))
      case _ => List()
    }
  }

  def filterVariableFunctionDef(a: Any, env: ASTEnv): List[Opt[_]] = {
    a match {
      case o: Opt[_] => if (o.feature != FeatureExprFactory.True && o.entry.isInstanceOf[FunctionDef]) List(o) else List() ++ o.productIterator.toList.flatMap(filterVariableFunctionDef(_, env))
      case l: List[_] => l.flatMap(filterVariableFunctionDef(_, env))
      case p: Product => p.productIterator.toList.flatMap(filterVariableFunctionDef(_, env))
      case _ => List()
    }
  }

  def filterVariableDeclarations(a: Any, env: ASTEnv): List[Opt[Declaration]] = {
    a match {
      case d: Opt[_] => if (d.feature != FeatureExprFactory.True && d.entry.isInstanceOf[Declaration]) List(d.asInstanceOf[Opt[Declaration]]) else List() ++ d.productIterator.toList.flatMap(filterVariableDeclarations(_, env))
      case l: List[_] => l.flatMap(filterVariableDeclarations(_, env))
      case p: Product => p.productIterator.toList.flatMap(filterVariableDeclarations(_, env))
      case _ => List()
    }
  }

  def getNewFunctionDef(a: AST): List[List[FunctionDef]] = {
    val tempLst = filterASTElems[FunctionDef](a)
    val lst = tempLst.filter(x => x.specifiers.length > 1)
    def rename(list: List[FunctionDef]): List[List[FunctionDef]] = {
      def getAll(fd: FunctionDef): List[FunctionDef] = {
        val map = functionMap(a)
        def getNewId(name: Id, specifiers: List[Opt[_]]): List[Id] = {
          if (specifiers.length > 0) {
            List(Id(map.get(name).get(specifiers(0).feature))) ++ getNewId(name, specifiers.tail)
          } else {
            List()
          }
        }
        def getSpecs(specs: List[Opt[Specifier]]): List[Specifier] = {
          if (specs.length > 0) {
            List(specs(0).entry) ++ getSpecs(specs.tail)
          } else {
            List()
          }
        }
        def getDeclarator(decl: Declarator, ids: List[Id]): List[AtomicNamedDeclarator] = {
          if (ids.length > 0) {
            List(AtomicNamedDeclarator(decl.pointers, ids(0), decl.extensions)) ++ getDeclarator(decl, ids.tail)
          } else {
            List()
          }
        }
        def getNewFunctions(decl: List[Declarator], spec: List[Specifier], fd: FunctionDef): List[FunctionDef] = {
          if (decl.length > 0) {
            List(FunctionDef(List(Opt(FeatureExprFactory.True, spec(0))), decl(0), fd.oldStyleParameters, fd.stmt)) ++
              getNewFunctions(decl.tail, spec.tail, fd)
          } else {
            List()
          }
        }
        getNewFunctions(getDeclarator(fd.declarator,
          getNewId(fd.declarator.asInstanceOf[AtomicNamedDeclarator].id, fd.specifiers)), getSpecs(fd.specifiers), fd)
      }
      if (list.size > 0) {
        List(getAll(list(0))) ++ rename(list.tail)
      } else {
        List()
      }
    }
    rename(lst)
  }

  def replaceConvertFunctions(a: AST, e: ASTEnv, idHashMap: util.IdentityHashMap[Id, List[Id]]): AST = {
    val tempAST = replaceFunctionDef(a, e)
    val finalAST = convertFunctionCalls(tempAST, e, idHashMap)
    finalAST
  }

  def replaceConvertFunctionsNew(a: AST, e: ASTEnv): AST = {
    val tempAST = replaceFunctionDef(a, e)
    val finalAST = convertFunctionCallsNew(tempAST, e)
    finalAST
  }

  def replaceFunctionDef(a: AST, e: ASTEnv): AST = {
    var replaceMap: Map[Opt[_], List[Opt[_]]] = Map()
    val tempLst = filterASTElems[FunctionDef](a)
    val lst = tempLst.filter(x => x.specifiers.length > 1)
    val lstLst = getNewFunctionDef(a)
    if (lst.size == lstLst.size) {
      for (i <- 0 to (lst.size - 1)) {
        val newOpts = listFDefToOpt(lstLst(i))
        val oldOpt = getFunctionOpt(e, lst(i))
        replaceMap += (oldOpt -> newOpts)
      }
    }
    replaceWithMap(a, replaceMap)
  }

  def getFunctionOpt(env: ASTEnv, fd: FunctionDef): Opt[FunctionDef] = {
    env.parent(fd).asInstanceOf[Opt[FunctionDef]]
  }


  def listFDefToOpt(lst: List[FunctionDef]): List[Opt[FunctionDef]] = {
    if (lst.size > 0) {
      List(Opt(FeatureExprFactory.True, lst.head)) ++ listFDefToOpt(lst.tail)
    } else {
      List()
    }
  }


  def functionMap(a: AST): Map[Id, Map[FeatureExpr, String]] = {
    val functions = filterASTElems[FunctionDef](a)
    val fct = functions.filter(s => s.specifiers.length > 1)
    var M: Map[Id, Map[FeatureExpr, String]] = Map()
    var M2: Map[FeatureExpr, String] = Map()
    def addMap(lst: List[FunctionDef]) = {
      def addSMap(name: Id, specs: List[Opt[Specifier]]) = {
        specs.foreach(x => if (!IdMap.contains(x.feature)) {
          IdMap += (x.feature -> IdMap.size)
        })
        specs.foreach(x => if (IdMap.contains(x.feature)) {
          M2 += (x.feature -> ("_" + IdMap.get(x.feature).get + "_" + name.name))
        })
        M += (name -> M2)
        M2 = Map()
      }
      lst.foreach(y => addSMap(y.declarator.asInstanceOf[AtomicNamedDeclarator].id, y.specifiers))
    }
    // println("Found " + fct.length + " functions with more than one specifier.")
    addMap(fct)
    println("\nMap:\n" + M + "\n\n")
    fctMap = M
    M
  }

  def convertLabelStmt(lStmt: Opt[LabelStatement]): Opt[IfStatement] = {
    val ifBranch = featureToCExpr(lStmt.feature)
    val thenBranch = One(CompoundStatement(List(Opt(FeatureExprFactory.True, LabelStatement(lStmt.entry.id, None)))))
    val ifStmt = Opt(FeatureExprFactory.True, IfStatement(One(ifBranch), thenBranch, List(), None))
    ifStmt
  }


  def replaceLabelsGotos(a: AST, env: ASTEnv): AST = {
    def getParent(lst: List[LabelStatement], e: ASTEnv): List[Opt[LabelStatement]] = {
      if (lst.length > 0) {
        List(e.parent(lst(0)).asInstanceOf[Opt[LabelStatement]]) ++ getParent(lst.tail, e)
      } else {
        List()
      }
    }

    def getMap(oldMap: Map[Id, List[Opt[LabelStatement]]]): Map[String, Map[FeatureExpr, String]] = {
      var M: Map[String, Map[FeatureExpr, String]] = Map()
      def getInnerMap(lst: List[Opt[LabelStatement]]): Map[FeatureExpr, String] = {
        var M2: Map[FeatureExpr, String] = Map()
        lst.foreach(x => if (IdMap.contains(x.feature)) {
          M2 += (x.feature -> ("_" + IdMap.get(x.feature).get + "_" + x.entry.id.name))
        } else {
          IdMap += (x.feature -> IdMap.size)
          M2 += (x.feature -> ("_" + IdMap.get(x.feature).get + "_" + x.entry.id.name))
        })
        M2
      }
      oldMap.foreach(x => M += (x._1.name -> getInnerMap(x._2)))
      M
    }

    def replaceLabels(lst: List[Opt[LabelStatement]], ast: AST, env: ASTEnv): AST = {
      if (lst.size > 0) {
        val ifBranch = featureToCExpr(lst(0).feature)
        val thenBranch = One(CompoundStatement(List(Opt(FeatureExprFactory.True, LabelStatement(Id("_" + IdMap.get(lst(0).feature).get + "_" + lst(0).entry.id.name), None)))))
        val ifStmt = Opt(FeatureExprFactory.True, IfStatement(One(ifBranch), thenBranch, List(), None))
        val newAst = replaceSame(ast, lst(0), ifStmt)
        val optLabels = getParent(filterASTElems[LabelStatement](ast), env).filter(s => s.feature != FeatureExprFactory.True)
        replaceLabels(optLabels, newAst, createASTEnv(newAst))
      } else {
        ast
      }
    }


    val labelList = filterASTElems[LabelStatement](a)
    val optLabels = getParent(labelList, env)
    val filteredOptLabels = optLabels.filter(s => s.feature != FeatureExprFactory.True)
    val groupedOptLabels = filteredOptLabels.groupBy(x => x.entry.id)
    val map = getMap(groupedOptLabels)


    def replaceGotos(lst: List[GotoStatement], ast: AST, env: ASTEnv): AST = {
      if (lst.size > 0) {
        val name = lst(0).target.asInstanceOf[Id].name
        val innerMap = map.get(name).get
        val optBuffer: ListBuffer[Opt[IfStatement]] = ListBuffer()
        innerMap.foreach(x => optBuffer += Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(x._1)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, GotoStatement(Id(x._2)))))), List(), None)))
        val optList = optBuffer.toList
        val newAst = replaceOptByOpts(ast, env.parent(lst(0)).asInstanceOf[Opt[GotoStatement]], optList)
        val newLst = filterASTElems[GotoStatement](newAst).filter(x => map.contains(x.target.asInstanceOf[Id].name))
        replaceGotos(newLst, newAst, createASTEnv(newAst))
      } else {
        ast
      }
    }

    var newAst = replaceLabels(filteredOptLabels, a, env)
    val filteredGotoStmts = filterASTElems[GotoStatement](a).filter(x => map.contains(x.target.asInstanceOf[Id].name))

    newAst = replaceGotos(filteredGotoStmts, newAst, createASTEnv(newAst))


    newAst
  }

  def getFeatureForId(id: Int): FeatureExpr = {
    if (IdMap.size < id || id < 0) {
      null
    } else {
      val it = IdMap.iterator
      while (it.hasNext) {
        val next = it.next()
        if (next._2.equals(id)) {
          return next._1
        }
      }
      return null
    }
  }

  def convertFunctionCalls(a: AST, env: ASTEnv, defUseMap: util.IdentityHashMap[Id, List[Id]]): AST = {
    val map = fctMap
    var replaceMap: Map[Opt[_], List[Opt[_]]] = Map()

    def convertFunctCall(ft: FeatureExpr, newName: String, fct: FunctionCall): Opt[IfStatement] = {
      val ifBranch = featureToCExpr(ft)
      val thenBranch = One(CompoundStatement(List(Opt(FeatureExprFactory.True, ExprStatement(PostfixExpr(Id(newName), fct))))))
      val ifStmt = IfStatement(One(ifBranch), thenBranch, List(), None)
      Opt(FeatureExprFactory.True, ifStmt)
    }
    def mapToOptList(map: Map[FeatureExpr, String], fct: FunctionCall): List[Opt[_]] = {
      val it = map.iterator
      val optBuffer = new ListBuffer[Opt[IfStatement]]
      while (it.hasNext) {
        val current = it.next()
        optBuffer += convertFunctCall(current._1, current._2, fct)
      }
      val optLst = optBuffer.toList
      optLst
    }

    fctMap.keySet.foreach(x => if (defUseMap.containsKey(x)) {
      val replaceList = defUseMap.get(x)
      if (replaceList.size > 0) {
        replaceList.foreach(y => replaceMap += (idToOptExpr(y, env) -> mapToOptList(fctMap.get(x).get, idToFunctionCall(y, env))))
      }
    })
    replaceWithMap(a, replaceMap)
  }

  def convertFunctionCallsNew(a: AST, env: ASTEnv): AST = {
    val map = fctMap
    val filteredFCT = filterASTElems[FunctionCall](a).filter(s => (env.parent(s).isInstanceOf[PostfixExpr] && env.parent(s).asInstanceOf[PostfixExpr].p.isInstanceOf[Id] && map.contains(env.parent(s).asInstanceOf[PostfixExpr].p.asInstanceOf[Id])))
    var replaceMap: Map[Opt[_], List[Opt[_]]] = Map()

    def convertFC(ast: AST, fct: FunctionCall) {
      val toBeReplaced = env.parent(env.parent(env.parent(fct))).asInstanceOf[Opt[_]]
      val parent = env.parent(fct).asInstanceOf[PostfixExpr]
      val expr = parent.p
      val innerMap = map.get(expr.asInstanceOf[Id]).get

      def convertSingleFunctionCall(ft: FeatureExpr, functName: String, functCall: FunctionCall): Opt[IfStatement] = {
        val ifBranch = featureToCExpr(ft)
        val thenBranch = One(CompoundStatement(List(Opt(FeatureExprFactory.True, ExprStatement(PostfixExpr(Id(functName), functCall))))))
        val ifStmt = IfStatement(One(ifBranch), thenBranch, List(), None)
        Opt(FeatureExprFactory.True, ifStmt)
      }

      val it = innerMap.iterator
      val optBuffer = new ListBuffer[Opt[IfStatement]]
      while (it.hasNext) {
        val current = it.next()
        optBuffer += convertSingleFunctionCall(current._1, current._2, fct)
      }
      val optLst = optBuffer.toList
      replaceMap += (toBeReplaced -> optLst)
    }
    filteredFCT.foreach(x => convertFC(a, x))
    println(replaceMap)
    return replaceWithMap(a, replaceMap)
  }

  def filterCaseOpt(a: AST, env: ASTEnv): List[Opt[CaseStatement]] = {
    val caseLst = filterASTElems[CaseStatement](a)
    val filteredList = caseLst.filter(x => env.parent(x).asInstanceOf[Opt[CaseStatement]].feature != FeatureExprFactory.True)
    def getCaseParent(lst: List[CaseStatement]): List[Opt[CaseStatement]] = {
      if (lst.size > 0) {
        List(env.parent(lst(0)).asInstanceOf[Opt[CaseStatement]]) ++ getCaseParent(lst.tail)
      } else {
        List()
      }
    }
    getCaseParent(filteredList)
  }

  /*
  def setGoToFlags(a: AST, env: ASTEnv): AST = {
    var switchStatements = filterASTElems[SwitchStatement](a)
    var replaceMap: Map[Opt[_], List[Opt[_]]] = Map()
    printf("There are " + switchStatements.size + " switch statements.\n")

    // Indicates if a CaseStatement has a BreakStatement
    def hasBreakStmt(caseStmt: CaseStatement): Boolean = {
      //val toCheck = caseStmt.s.get.toOptList(0).entry
      //println("\nNext from " + caseStmt + " is " + e.next(e.parent(caseStmt)) + "\n")
      val parent = env.parent(caseStmt).asInstanceOf[Opt[CaseStatement]]
      val next = env.next(parent)
      /*
      if (next.isInstanceOf[Opt[CaseStatement]]) {
        val nextCase = next.asInstanceOf[Opt[CaseStatement]].entry
        if (nextCase.c == caseStmt.c) {
          return hasBreakStmt(nextCase)
        }
      }
      */
      return next.isInstanceOf[Opt[BreakStatement]] && next.asInstanceOf[Opt[BreakStatement]].feature == parent.feature
    }

    // Indicates if a SwitchStatement has a default statement
    def hasDefaultStmt(swstmt: SwitchStatement): Boolean = {
      val defaultstatement = filterASTElems[DefaultStatement](swstmt)
      val result = (defaultstatement.size == 1)
      // println("Contains DefaultStatement: " + result + "\n")
      return result
    }

    def getDefaultStmt(swStmt: SwitchStatement): DefaultStatement = {
      val defaultStatements = filterASTElems[DefaultStatement](swStmt)
      if (defaultStatements.size == 1) {
        return defaultStatements(0)
      } else {
        return null
      }
    }
    // Indicates of a SwitchStatement includes variable opts
    def hasOpts(swstmt: SwitchStatement): Boolean = {
      val opts = swstmt.s.toOptList(0).entry.asInstanceOf[CompoundStatement].innerStatements
      opts.foreach(x => if (x.feature != FeatureExprFactory.True) return true)
      return false
    }

    def getOpts(swstmt: SwitchStatement): List[Opt[_]] = {
      val opts = swstmt.s.toOptList(0).entry.asInstanceOf[CompoundStatement].innerStatements
      val optBuffer = new ListBuffer[Opt[_]]
      opts.foreach(x => if (x.feature != FeatureExprFactory.True) optBuffer += x)
      println("Opts are:\n" + optBuffer)
      return optBuffer.toList
    }

    def setDefaultGoTo(swStmt: Opt[SwitchStatement]) = {
      val optLabel = Opt(FeatureExprFactory.True, LabelStatement(Id("sdefault"), None))
      if (hasDefaultStmt(swStmt.entry)) {
        //println("SetDefaultGoTo1\n")
        val dfltStmt = getDefaultStmt(swStmt.entry)
        replaceMap += (env.parent(dfltStmt).asInstanceOf[Opt[DefaultStatement]] -> List(optLabel, env.parent(dfltStmt).asInstanceOf[Opt[DefaultStatement]]))
      }
    }

    def checkDefaultGoTo(swStmt: Opt[SwitchStatement]): Tuple2[Opt[_], List[Opt[_]]] = {
      val optLabel = Opt(FeatureExprFactory.True, LabelStatement(Id("sdefault"), None))
      if (hasDefaultStmt(swStmt.entry)) {
        //println("SetDefaultGoTo1\n")
        val dfltStmt = getDefaultStmt(swStmt.entry)
        return (env.parent(dfltStmt).asInstanceOf[Opt[DefaultStatement]], List(optLabel, env.parent(dfltStmt).asInstanceOf[Opt[DefaultStatement]]))
      } else {
        return null
      }
    }
    def transformOptToIf(optLst: List[Opt[_]]): Map[Opt[_], List[Opt[_]]] = {

      def replaceSingleOpt(opt: Opt[_]): List[Opt[_]] = {
        def replaceOptFeatures(caseStmt: CaseStatement, ft: FeatureExpr): CaseStatement = {
          //val core = caseStmt.s
          val r = manytd(rule {
            case l: List[Opt[_]] => l.flatMap({
              x => if (x.feature == ft) Opt(FeatureExprFactory.True, x.entry) :: Nil else x :: Nil
            })
          })
          r(caseStmt).get.asInstanceOf[CaseStatement]
        }
        val condition = featureToCExpr(opt.feature)
        val entry = opt.entry
        //println("Entry:\n" + entry)

        if (entry.isInstanceOf[BreakStatement]) {
          val newS = List(Opt(FeatureExprFactory.True, IfStatement(One(condition), One(CompoundStatement(List(Opt(FeatureExprFactory.True, entry.asInstanceOf[BreakStatement])))), List(), None)))
          newS
        } else if (entry.isInstanceOf[CaseStatement]) {
          val sameCaseStatements = replaceMap.filterKeys(x => x.entry.isInstanceOf[CaseStatement] && x.entry.asInstanceOf[CaseStatement].c == entry.asInstanceOf[CaseStatement].c)
          if (sameCaseStatements.size > 0) {
            // val currentKey = sameCaseStatements.head._1
            //val targetCase = sameCaseStatements.head._2
            // replaceMap -= currentKey
            val caseStmt = entry.asInstanceOf[CaseStatement]
            val alteredCaseStmt = replaceOptFeatures(caseStmt, opt.feature)
            val then = alteredCaseStmt.s.get.toOptList(0).entry
            val ifStmt = IfStatement(One(condition), One(CompoundStatement(List(Opt(FeatureExprFactory.True, then)))), List(), None)

            List(Opt(FeatureExprFactory.True, ifStmt))


          } else {
            val caseStmt = entry.asInstanceOf[CaseStatement]
            //println("\nHas BreakStatement: " + hasBreakStmt(caseStmt) + "\n")
            val alteredCaseStmt = replaceOptFeatures(caseStmt, opt.feature)
            val then = alteredCaseStmt.s.get.toOptList(0).entry
            val ifStmt = IfStatement(One(condition), One(CompoundStatement(List(Opt(FeatureExprFactory.True, then)))), List(), None)
            val newOpt = Opt(FeatureExprFactory.True, CaseStatement(alteredCaseStmt.c, Some(One(ifStmt))))

            List(newOpt)
          }
          //println("New Opt is:\n" + PrettyPrinter.print(CompoundStatement(List(newOpt))))
        } else {
          List(opt)
        }
      }
      optLst.foreach(x => replaceMap += (x -> replaceSingleOpt(x)))
      replaceMap
      //println(PrettyPrinter.print(replaceWithMap(a, replaceMap)))
    }


    //val swtParent = env.parent(switchStatements(0)).asInstanceOf[Opt[SwitchStatement]]
    //val newSwtStmt = setDefaultGoTo(swtParent)

    val opts = getOpts(switchStatements(0))
    transformOptToIf(opts)

    if (hasDefaultStmt(switchStatements(0))) {
      val optSwitch = env.parent(switchStatements(0)).asInstanceOf[Opt[SwitchStatement]]
      setDefaultGoTo(optSwitch)
      println("Want to replace:\n" + checkDefaultGoTo(optSwitch)._1 + "\nwith:\n" + checkDefaultGoTo(optSwitch)._2 + "\n")
    }
    val newAst = replaceWithMap(a, replaceMap)
    newAst
  }
  */

  def replaceIfs(ast: AST, env: ASTEnv): AST = {
    val exprStmts = filterASTElems[ExprStatement](ast)
    val varExprStmts = exprStmts.filter(x => env.parent(x).asInstanceOf[Opt[ExprStatement]].feature != FeatureExprFactory.True)
    def exprToIf(a: AST, e: ASTEnv, lst: List[ExprStatement]): AST = {
      if (lst.size > 0) {
        val oldS = e.parent(lst(0)).asInstanceOf[Opt[_]]
        val newS = Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(oldS.feature)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, lst(0))))), List(), None))
        println("old: " + oldS + "\n")
        println("new: " + newS + "\n" + PrettyPrinter.print(CompoundStatement(List(newS))))
        //println("Target is:\n" + PrettyPrinter.print(CompoundStatement(List(tar))) + "\n")
        return exprToIf(replaceSame(a, oldS, newS), e, lst.tail)
        //return replaceSame(a, oldS, newS)
      } else {
        return a;
      }
    }
    return exprToIf(ast, env, varExprStmts)
  }

  def idToOptExpr(id: Id, env: ASTEnv): Opt[ExprStatement] = {
    val optFC = env.parent(env.parent(env.parent(id)))
    if (optFC.isInstanceOf[Opt[ExprStatement]]) {
      return optFC.asInstanceOf[Opt[ExprStatement]]
    } else {
      return null
    }
  }

  def idToFunctionCall(id: Id, env: ASTEnv): FunctionCall = {
    val pstFxExpr = env.parent(id)
    if (pstFxExpr.isInstanceOf[PostfixExpr]) {
      return pstFxExpr.asInstanceOf[PostfixExpr].s.asInstanceOf[FunctionCall]
    } else {
      return null
    }
  }

  def transformVariableDeclarations(ast: AST, env: ASTEnv): AST = {
    var replaceMap: Map[Opt[_], List[Opt[_]]] = Map()

    def isVariableDeclaration(decl: Declaration): Boolean = {
      decl match {
        case Declaration(specs, List(Opt(feature, i: InitDeclaratorI))) =>
          if (feature != FeatureExprFactory.True) {
            return true
          }
          i match {
            case InitDeclaratorI(_, _, Some(Initializer(_, lcurly: LcurlyInitializer))) =>
              specs.foreach(x => if (x.feature != FeatureExprFactory.True) return true)
              lcurly.inits.foreach(x => if (x.feature != FeatureExprFactory.True) return true)
            case _ =>
          }
        case _ =>
      }
      return false
    }
    def convertSingleDeclaration(decl: Declaration /*, ft: FeatureExpr */) /*: Opt[Declaration] */ = {
      val secondParent = env.parent(env.parent(decl))
      val firstParent = env.parent(decl)

      if (secondParent.isInstanceOf[Opt[DeclarationStatement]]) {
        secondParent match {
          case Opt(feature, declStmt: DeclarationStatement) =>
            if (feature != FeatureExprFactory.True) {
              if (!IdMap.contains(feature)) {
                IdMap += (feature -> IdMap.size)
              }
              declStmt match {
                case DeclarationStatement(Declaration(specs, init: List[Opt[InitDeclaratorI]])) =>
                  //init match {
                  // case InitDeclaratorI(a: AtomicNamedDeclarator, c, d) =>
                  val namePrefix = "_" + IdMap.get(feature).get + "_"
                  val newInit = init.map(s => Opt(FeatureExprFactory.True, s.entry.copy(declarator = s.entry.declarator.asInstanceOf[AtomicNamedDeclarator].copy(id = Id(namePrefix + s.entry.declarator.asInstanceOf[AtomicNamedDeclarator].getName)))))
                  val newSpec = specs.map(s => s.copy(feature = FeatureExprFactory.True))
                  val newDeclStmt = declStmt.copy(decl = Declaration(newSpec, newInit))
                  replaceMap += (secondParent.asInstanceOf[Opt[DeclarationStatement]] -> List(Opt(FeatureExprFactory.True, newDeclStmt)))
                //}
                case _ =>
              }
            }
          case _ =>
        }
      }
      if (firstParent.isInstanceOf[Opt[Declaration]]) {
        firstParent match {
          case opt@Opt(ft, decl@Declaration(specs, init)) =>
            if (ft == FeatureExprFactory.True) {
              val newInit = init.map(i => i match {
                case Opt(ftt, InitDeclaratorI(_, _, Some(Initializer(_, lcurly: LcurlyInitializer)))) =>
                  if (ftt == FeatureExprFactory.True) {
                    val newCurly = lcurly.copy(inits = lcurly.inits.map(s => if (s.feature != FeatureExprFactory.True) {
                      if (!IdMap.contains(s.feature)) {
                        IdMap += (s.feature -> IdMap.size)
                      }
                      s.entry match {
                        case ini@Initializer(_, i: Id) =>
                          s.copy(feature = FeatureExprFactory.True).copy(entry = ini.copy(expr = Id("_" + IdMap.get(s.feature).get + "_" + i.name)))
                        case _ =>
                      }
                    } else {
                      s
                    }).asInstanceOf[List[Opt[Initializer]]])
                    i.copy(entry = i.entry.asInstanceOf[InitDeclaratorI].copy(i = Some(i.entry.asInstanceOf[InitDeclaratorI].i.get.copy(expr = newCurly))))
                  }
                case _ =>
              })
              //replaceMap += (firstParent.asInstanceOf[Opt[_]] -> List(opt.copy(entry = decl.copy(init = newInit.asInstanceOf[List[Opt[InitDeclaratorI]]]))))
            } else {
              if (!IdMap.contains(ft)) {
                IdMap += (ft -> IdMap.size)
              }
              val newSpec = specs.map(s => if (s.feature == ft) {
                s.copy(feature = FeatureExprFactory.True)
              } else {
                s
              })
              val newInit = init.map(s => s match {
                case Opt(feat, i: InitDeclaratorI) =>
                  if (feat == ft) {
                    i match {
                      case InitDeclaratorI(and: AtomicNamedDeclarator, _, Some(Initializer(_, lcurly: LcurlyInitializer))) =>
                        val newAnd = and.copy(id = Id("_" + IdMap.get(ft).get + "_" + and.id.name)).copy(extensions = and.extensions.map(g =>
                          if (g.feature == ft) {
                            g.copy(feature = FeatureExprFactory.True)
                          } else {
                            g
                          }))
                        val newCurly = lcurly.inits.map(u => u match {
                          case o@Opt(feature, Initializer(_, id: Id)) =>
                            if (feature == ft) {
                              o.copy(feature = FeatureExprFactory.True)
                            } else {
                              o
                            }
                          case _ =>
                        })
                        s.copy(feature = FeatureExprFactory.True).copy(entry = i.copy(declarator = newAnd).copy(i = Some(i.i.get.copy(expr = LcurlyInitializer(newCurly.asInstanceOf[List[Opt[Initializer]]])))))
                      case _ =>
                    }
                  } else {
                    s
                  }
                case _ =>
              })
              val newOpt = Opt(FeatureExprFactory.True, Declaration(newSpec, newInit.asInstanceOf[List[Opt[InitDeclarator]]]))
              replaceMap += (firstParent.asInstanceOf[Opt[_]] -> List(newOpt))
            }
          case _ =>
          /*
          init match {
            case List(Opt(ft, i: InitDeclaratorI)) =>
             i match {
              case InitDeclaratorI(_, _, Some(Initializer(_, lcurly: LcurlyInitializer))) =>
                lcurly.inits.foreach(y => y match {
                  case Opt(ft, in: Initializer) => lcurlyinits += Opt(FeatureExprFactory.True, in)
                  case Opt(FeatureExprFactory.True, in: Initializer) => specifiers += y
                  case Opt(feature, in: Initializer) => if (featureMap.contains(feature)) {
                    featureMap += (feature -> (featureMap.get(feature).get ++ List(Opt(FeatureExprFactory.True, in))))
                  }
                })
            }
          } */
        }
      }
    }
    val declarations = filterASTElems[Declaration](ast)
    declarations.foreach(x => if (isVariableDeclaration(x)) convertSingleDeclaration(x))
    //println("\nReplaceMap is:\n" + replaceMap)
    replaceWithMap(ast, replaceMap)
  }

  def convertTranslationUnit(ast: TranslationUnit, env: ASTEnv, defuse: IdentityHashMap[Id, List[Id]]): TranslationUnit = {
    def convertOptList(lOp: List[Opt[_]]): List[Opt[_]] = {
      lOp.flatMap(x => convertOpt(x) :: Nil)
    }
    def convertOpt(op: Opt[_]): Opt[_] = {
      if (op.feature != FeatureExprFactory.True) {
        println("Current:\n" + PrettyPrinter.print(TranslationUnit(List(op.asInstanceOf[Opt[ExternalDef]]))))
      }
      op
    }
    //TranslationUnit(convertOptList(ast.defs).asInstanceOf[List[Opt[ExternalDef]]])
    fillIdMap(ast, env)
    convertDeclarations(ast, env, defuse)


    ast
  }

  def convertDeclarations(ast: AST, env: ASTEnv, defuse: IdentityHashMap[Id, List[Id]]): AST = {
    val declarationList = filterVariableDeclarations(ast, env)
    def getIdFromDeclaration(decl: Opt[Declaration]): List[Id] = {
      decl.entry match {
        case Declaration(declSpecs, init) =>
          init.flatMap(x => x.entry match {
            case in@InitDeclaratorI(a@AtomicNamedDeclarator(pointers, id, extensions), attribute, i) =>
              id :: Nil
            case _ => List()
          })
      }
    }
    def convertSingleDeclaration(decl: Opt[Declaration]): Opt[Declaration] = {
      def convertInitDeclarator(init: Opt[InitDeclarator]): Opt[InitDeclarator] = {
        //println("Current:\n" + init)
        init.entry match {
          case in@InitDeclaratorI(a@AtomicNamedDeclarator(pointers, id, extensions), attribute, i) =>
            init.copy(feature = FeatureExprFactory.True) //.copy(entry = replaceFeatureByTrue(in.copy(declarator = a.copy(id = Id("_" + IdMap.get(init.feature).get + "_" + id.name))), init.feature).asInstanceOf[InitDeclarator])
        }
      }
      decl.copy(feature = FeatureExprFactory.True).copy(entry = Declaration(decl.entry.declSpecs.flatMap(x => x.copy(feature = FeatureExprFactory.True) :: Nil), decl.entry.init.flatMap(x => convertInitDeclarator(x) :: Nil)))
    }
    declarationList.foreach(x => println("Converting:\n" + x + "\nto: " + convertSingleDeclaration(x) + "\nwith usages: " + defuse.get(getIdFromDeclaration(x).head) + "\n"))
    //println("Converting:\n" + declarationList.head + "\nto: " + convertSingleDeclaration(declarationList.head) + "\nwith usages: " + defuse.get(getIdFromDeclaration(declarationList.head).head))

    ast
  }

  /*def replaceFeatureByTrue(ast: AST, feat: FeatureExpr): AST = {
    ast match {
      case t@TranslationUnit(ext) =>
        println("This should not happen!")
        t
      case i@Id(name) => i
      case c@Constant(v) => c
      case c@StringLit(v) => c.copy(name = v.flatMap(x => if (x.feature == feat) {
        x.copy(feature = FeatureExprFactory.True) :: Nil
      } else {
        x :: Nil
      }))
      case c@SimplePostfixSuffix(t) => c
      case c@PointerPostfixSuffix(kind, id) => c
      case c@FunctionCall(params) => c.copy(params = c.params.copy(exprs = params.exprs.flatMap(x => if (x.feature == feat) {
        x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Expr]) :: Nil
      } else {
        x :: Nil
      })))
      case c@ArrayAccess(e) => c.copy(expr = replaceFeatureByTrue(e, feat).asInstanceOf[Expr])
      case c@PostfixExpr(p, s) => c.copy(p = replaceFeatureByTrue(p, feat).asInstanceOf[Expr]).copy(s = replaceFeatureByTrue(s, feat).asInstanceOf[PostfixSuffix])
      case c@UnaryExpr(p, s) => c.copy(e = replaceFeatureByTrue(s, feat).asInstanceOf[Expr])
      case c@SizeOfExprT(typeName) => c.copy(typeName = replaceFeatureByTrue(typeName, feat).asInstanceOf[TypeName])
      case c@SizeOfExprU(e) => c.copy(expr = replaceFeatureByTrue(e, feat).asInstanceOf[Expr])
      case c@CastExpr(typeName, expr) => c.copy(typeName = replaceFeatureByTrue(typeName, feat).asInstanceOf[TypeName]).copy(expr = replaceFeatureByTrue(expr, feat).asInstanceOf[Expr])

      case c@PointerDerefExpr(castExpr) => c.copy(castExpr = replaceFeatureByTrue(castExpr, feat).asInstanceOf[Expr])
      case c@PointerCreationExpr(castExpr) => c.copy(castExpr = replaceFeatureByTrue(castExpr, feat).asInstanceOf[Expr])

      case c@UnaryOpExpr(kind, castExpr) => c.copy(castExpr = replaceFeatureByTrue(castExpr, feat).asInstanceOf[Expr])
      case c@NAryExpr(e, others) => c.copy(e = replaceFeatureByTrue(e, feat).asInstanceOf[Expr]).copy(others = others.flatMap(x => if (x.feature == feat) {
        x.copy(feature = FeatureExprFactory.True) :: Nil
      } else {
        x :: Nil
      }))
      case c@NArySubExpr(op: String, e: Expr) => c.copy(e = replaceFeatureByTrue(e, feat).asInstanceOf[Expr])
      case c@ConditionalExpr(condition: Expr, thenExpr, elseExpr: Expr) => c.copy(condition = replaceFeatureByTrue(condition, feat).asInstanceOf[Expr]).copy(elseExpr = replaceFeatureByTrue(elseExpr, feat).asInstanceOf[Expr]).copy(thenExpr = if (thenExpr.isDefined) {
        Some(replaceFeatureByTrue(thenExpr.get, feat).asInstanceOf[Expr])
      } else {
        thenExpr
      })
      case c@AssignExpr(target: Expr, operation: String, source: Expr) => c.copy(target = replaceFeatureByTrue(target, feat).asInstanceOf[Expr]).copy(source = replaceFeatureByTrue(source, feat).asInstanceOf[Expr])
      case c@ExprList(exprs) => c.copy(exprs = exprs.flatMap(x => if (x.feature == feat) {
        x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Expr]) :: Nil
      } else {
        x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Expr]) :: Nil
      }))

      case c@CompoundStatement(innerStatements) =>
        c.copy(innerStatements = innerStatements.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Statement]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Statement]) :: Nil
        }))
      case c@EmptyStatement() => c
      case c@ExprStatement(expr: Expr) => c.copy(expr = replaceFeatureByTrue(expr, feat).asInstanceOf[Expr])
      case c@WhileStatement(expr: Expr, s) => c.copy(expr = replaceFeatureByTrue(expr, feat).asInstanceOf[Expr])
      case c@DoStatement(expr: Expr, s) => c.copy(expr = replaceFeatureByTrue(expr, feat).asInstanceOf[Expr])
      case c@ForStatement(expr1, expr2, expr3, s) =>
        c.copy(expr1 = if (expr1.isDefined) Some(replaceFeatureByTrue(expr1.get, feat).asInstanceOf[Expr]) else expr1)
        c.copy(expr2 = if (expr2.isDefined) Some(replaceFeatureByTrue(expr2.get, feat).asInstanceOf[Expr]) else expr2)
        c.copy(expr3 = if (expr3.isDefined) Some(replaceFeatureByTrue(expr3.get, feat).asInstanceOf[Expr]) else expr3)
      case c@GotoStatement(target) => c.copy(target = replaceFeatureByTrue(target, feat).asInstanceOf[Expr])
      case c@ContinueStatement() => c
      case c@BreakStatement() => c
      case c@ReturnStatement(None) => c
      case c@ReturnStatement(Some(e)) => c
      case c@LabelStatement(id: Id, _) => c
      case c@CaseStatement(e: Expr) => c.copy(c = replaceFeatureByTrue(e, feat).asInstanceOf[Expr])
      case c@DefaultStatement() => c
      case c@IfStatement(condition, thenBranch, elifs, elseBranch) =>
        c.copy(elifs = elifs.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[ElifStatement]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[ElifStatement]) :: Nil
        }))
      case c@ElifStatement(condition, thenBranch) => c
      case c@SwitchStatement(expr, s) => c.copy(expr = replaceFeatureByTrue(expr, feat).asInstanceOf[Expr])
      case c@DeclarationStatement(decl: Declaration) => c.copy(decl = replaceFeatureByTrue(decl, feat).asInstanceOf[Declaration])
      case c@NestedFunctionDef(isAuto, specifiers, declarator, parameters, stmt) =>
        c.copy(specifiers = specifiers.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Specifier]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Specifier]) :: Nil
        }))
          .copy(declarator = replaceFeatureByTrue(declarator, feat).asInstanceOf[Declarator])
          .copy(parameters = parameters.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Declaration]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Declaration]) :: Nil
        }))
          .copy(stmt = replaceFeatureByTrue(stmt, feat).asInstanceOf[CompoundStatement])
      case c@LocalLabelDeclaration(ids) => c.copy(ids = ids.flatMap(x => if (x.feature == feat) {
        x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Id]) :: Nil
      } else {
        x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Id]) :: Nil
      }))
      case c@OtherPrimitiveTypeSpecifier(typeName: String) => c
      case c@VoidSpecifier() => c
      case c@ShortSpecifier() => c
      case c@IntSpecifier() => c
      case c@FloatSpecifier() => c
      case c@LongSpecifier() => c
      case c@CharSpecifier() => c
      case c@DoubleSpecifier() => c

      case c@TypedefSpecifier() => c
      case c@TypeDefTypeSpecifier(name: Id) => c
      case c@SignedSpecifier() => c
      case c@UnsignedSpecifier() => c

      case c@InlineSpecifier() => c
      case c@AutoSpecifier() => c
      case c@RegisterSpecifier() => c
      case c@VolatileSpecifier() => c
      case c@ExternSpecifier() => c
      case c@ConstSpecifier() => c
      case c@RestrictSpecifier() => c
      case c@StaticSpecifier() => c

      case c@AtomicAttribute(n: String) => c
      case c@AttributeSequence(attributes) => c.copy(attributes = attributes.flatMap(x => if (x.feature == feat) {
        x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Attribute]) :: Nil
      } else {
        x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Attribute]) :: Nil
      }))
      case c@CompoundAttribute(inner) => c.copy(inner = inner.flatMap(x => if (x.feature == feat) {
        x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[AttributeSequence]) :: Nil
      } else {
        x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[AttributeSequence]) :: Nil
      }))

      case c@Declaration(declSpecs, init) =>
        c.copy(declSpecs = declSpecs.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Specifier]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Specifier]) :: Nil
        })).copy(init = init.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[InitDeclarator]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[InitDeclarator]) :: Nil
        }))

      case c@InitDeclaratorI(declarator, _, _) => c.copy(declarator = replaceFeatureByTrue(declarator, feat).asInstanceOf[Declarator])
      case c@InitDeclaratorE(declarator, _, e: Expr) => c.copy(declarator = replaceFeatureByTrue(declarator, feat).asInstanceOf[Declarator]).copy(e = replaceFeatureByTrue(e, feat).asInstanceOf[Expr])

      case c@AtomicNamedDeclarator(pointers, id, extensions) =>
        c.copy(pointers = pointers.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Pointer]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Pointer]) :: Nil
        }))
          .copy(extensions = extensions.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[DeclaratorExtension]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[DeclaratorExtension]) :: Nil
        }))
      case c@NestedNamedDeclarator(pointers, nestedDecl, extensions) =>
        c.copy(pointers = pointers.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Pointer]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Pointer]) :: Nil
        }))
          .copy(extensions = extensions.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[DeclaratorExtension]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[DeclaratorExtension]) :: Nil
        }))
          .copy(nestedDecl = replaceFeatureByTrue(nestedDecl, feat).asInstanceOf[Declarator])
      case c@AtomicAbstractDeclarator(pointers, extensions) =>
        c.copy(pointers = pointers.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Pointer]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Pointer]) :: Nil
        }))
          .copy(extensions = extensions.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[DeclaratorAbstrExtension]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[DeclaratorAbstrExtension]) :: Nil
        }))
      case c@NestedAbstractDeclarator(pointers, nestedDecl, extensions) =>
        c.copy(pointers = pointers.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Pointer]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Pointer]) :: Nil
        }))
          .copy(extensions = extensions.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[DeclaratorAbstrExtension]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[DeclaratorAbstrExtension]) :: Nil
        }))
          .copy(nestedDecl = replaceFeatureByTrue(nestedDecl, feat).asInstanceOf[AbstractDeclarator])

      case c@DeclIdentifierList(idList) => c.copy(idList = idList.flatMap(x => if (x.feature == feat) {
        x.copy(feature = FeatureExprFactory.True) :: Nil
      } else {
        x :: Nil
      }))
      case c@DeclParameterDeclList(parameterDecls) => c.copy(parameterDecls = parameterDecls.flatMap(x => if (x.feature == feat) {
        x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[ParameterDeclaration]) :: Nil
      } else {
        x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[ParameterDeclaration]) :: Nil
      }))
      case c@DeclArrayAccess(expr) => c.copy(expr = if (expr.isDefined) Some(replaceFeatureByTrue(expr.get, feat).asInstanceOf[Expr]) else expr)
      case c@Initializer(initializerElementLabel, expr: Expr) => c.copy(initializerElementLabel = if (initializerElementLabel.isDefined) {
        Some(replaceFeatureByTrue(initializerElementLabel.get, feat).asInstanceOf[InitializerElementLabel])
      } else {
        initializerElementLabel
      }).copy(expr = replaceFeatureByTrue(expr, feat).asInstanceOf[Expr])
      case c@Pointer(specifier) => c.copy(specifier = specifier.flatMap(x => if (x.feature == feat) {
        x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Specifier]) :: Nil
      } else {
        x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Specifier]) :: Nil
      }))
      case c@PlainParameterDeclaration(specifiers) => c.copy(specifiers = specifiers.flatMap(x => if (x.feature == feat) {
        x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Specifier]) :: Nil
      } else {
        x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Specifier]) :: Nil
      }))
      case c@ParameterDeclarationD(specifiers, decl) =>
        c.copy(specifiers = specifiers.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Specifier]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Specifier]) :: Nil
        }))
          .copy(decl = replaceFeatureByTrue(decl, feat).asInstanceOf[Declarator])
      case c@ParameterDeclarationAD(specifiers, decl) =>
        c.copy(specifiers = specifiers.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Specifier]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Specifier]) :: Nil
        }))
          .copy(decl = replaceFeatureByTrue(decl, feat).asInstanceOf[AbstractDeclarator])
      case c@VarArgs() => c
      case c@EnumSpecifier(id, Some(enums)) => c.copy(enumerators = Some(enums.flatMap(x => if (x.feature == feat) {
        x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Enumerator]) :: Nil
      } else {
        x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Enumerator]) :: Nil
      })))
      case c@EnumSpecifier(Some(id), None) => c
      case c@Enumerator(id, Some(init)) => c.copy(assignment = Some(replaceFeatureByTrue(init, feat).asInstanceOf[Expr]))
      case c@Enumerator(id, None) => c
      case c@StructOrUnionSpecifier(isUnion, id, Some(structDeclaration)) => c.copy(enumerators = Some(structDeclaration.flatMap(x => if (x.feature == feat) {
        x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[StructDeclaration]) :: Nil
      } else {
        x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[StructDeclaration]) :: Nil
      })))
      case c@StructOrUnionSpecifier(isUnion, id, None) => c
      case c@StructDeclaration(qualifierList, declaratorList) =>
        c.copy(qualifierList = qualifierList.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Specifier]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Specifier]) :: Nil
        }))
          .copy(declaratorList = declaratorList.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[StructDecl]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[StructDecl]) :: Nil
        }))
      case c@StructDeclarator(decl, Some(expr), attributes) =>
        c.copy(decl = replaceFeatureByTrue(decl, feat).asInstanceOf[Declarator])
          .copy(initializer = Some(replaceFeatureByTrue(expr, feat).asInstanceOf[Expr]))
          .copy(attributes = attributes.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[AttributeSpecifier]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[AttributeSpecifier]) :: Nil
        }))
      case c@StructDeclarator(decl, None, attributes) =>
        c.copy(decl = replaceFeatureByTrue(decl, feat).asInstanceOf[Declarator])
          .copy(attributes = attributes.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[AttributeSpecifier]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[AttributeSpecifier]) :: Nil
        }))
      case c@StructInitializer(expr, attributes) =>
        c.copy(expr = replaceFeatureByTrue(expr, feat).asInstanceOf[Expr])
          .copy(attributes = attributes.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[AttributeSpecifier]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[AttributeSpecifier]) :: Nil
        }))
      case c@AsmExpr(isVolatile, expr) => c.copy(expr = replaceFeatureByTrue(expr, feat).asInstanceOf[Expr])
      case c@FunctionDef(specifiers, declarator, oldStyleParameters, stmt) =>
        c.copy(declarator = replaceFeatureByTrue(declarator, feat).asInstanceOf[Declarator])
          .copy(specifiers = specifiers.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Specifier]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Specifier]) :: Nil
        }))
          .copy(oldStyleParameters = oldStyleParameters.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[OldParameterDeclaration]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[OldParameterDeclaration]) :: Nil
        }))

      case c@EmptyExternalDef() => c
      case c@TypelessDeclaration(declList) => c.copy(declList = declList.flatMap(x => if (x.feature == feat) {
        x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[InitDeclarator]) :: Nil
      } else {
        x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[InitDeclarator]) :: Nil
      }))
      case c@TypeName(specifiers, Some(abstrDecl)) =>
        c.copy(specifiers = specifiers.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Specifier]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Specifier]) :: Nil
        }))
          .copy(decl = Some(replaceFeatureByTrue(abstrDecl, feat).asInstanceOf[AbstractDeclarator]))
      case c@TypeName(specifiers, None) =>
        c.copy(specifiers = specifiers.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Specifier]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Specifier]) :: Nil
        }))
      case c@GnuAttributeSpecifier(attributeList) =>
        c.copy(attributeList = attributeList.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[AttributeSequence]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[AttributeSequence]) :: Nil
        }))
      case c@AsmAttributeSpecifier(stringConst) => c
      case c@LcurlyInitializer(inits) =>
        c.copy(inits = inits.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Initializer]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[Initializer]) :: Nil
        }))
      case c@AlignOfExprT(typeName: TypeName) => c.copy(typeName = replaceFeatureByTrue(typeName, feat).asInstanceOf[TypeName])
      case c@AlignOfExprU(expr: Expr) => c.copy(expr = replaceFeatureByTrue(expr, feat).asInstanceOf[Expr])
      case c@GnuAsmExpr(isVolatile: Boolean, isAuto, expr: StringLit, stuff: Any) => c.copy(expr = replaceFeatureByTrue(expr, feat).asInstanceOf[StringLit])
      case c@RangeExpr(from: Expr, to: Expr) => c.copy(from = replaceFeatureByTrue(from, feat).asInstanceOf[Expr]).copy(to = replaceFeatureByTrue(to, feat).asInstanceOf[Expr])
      case c@TypeOfSpecifierT(typeName: TypeName) => c.copy(typeName = replaceFeatureByTrue(typeName, feat).asInstanceOf[TypeName])
      case c@TypeOfSpecifierU(e: Expr) => c.copy(expr = replaceFeatureByTrue(e, feat).asInstanceOf[Expr])
      case c@InitializerArrayDesignator(expr: Expr) => c.copy(expr = replaceFeatureByTrue(expr, feat).asInstanceOf[Expr])
      case c@InitializerDesignatorD(id: Id) => c
      case c@InitializerDesignatorC(id: Id) => c
      case c@InitializerAssigment(desgs) => c.copy(designators = desgs.flatMap(x => if (x.feature == feat) {
        x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[InitializerElementLabel]) :: Nil
      } else {
        x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[InitializerElementLabel]) :: Nil
      }))
      case c@BuiltinOffsetof(typeName: TypeName, offsetofMemberDesignator) =>
        c.copy(offsetofMemberDesignator = offsetofMemberDesignator.flatMap(x => if (x.feature == feat) {
          x.copy(feature = FeatureExprFactory.True).copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[OffsetofMemberDesignator]) :: Nil
        } else {
          x.copy(entry = replaceFeatureByTrue(x.entry, feat).asInstanceOf[OffsetofMemberDesignator]) :: Nil
        }))
          .copy(typeName = replaceFeatureByTrue(typeName, feat).asInstanceOf[TypeName])
      case c@OffsetofMemberDesignatorID(id: Id) => c
      case c@OffsetofMemberDesignatorExpr(expr: Expr) => c.copy(expr = replaceFeatureByTrue(expr, feat).asInstanceOf[Expr])
      case c@BuiltinTypesCompatible(typeName1: TypeName, typeName2: TypeName) =>
        c.copy(typeName1 = replaceFeatureByTrue(typeName1, feat).asInstanceOf[TypeName])
          .copy(typeName2 = replaceFeatureByTrue(typeName2, feat).asInstanceOf[TypeName])
      case c@BuiltinVaArgs(expr: Expr, typeName: TypeName) =>
        c.copy(typeName = replaceFeatureByTrue(typeName, feat).asInstanceOf[TypeName])
          .copy(expr = replaceFeatureByTrue(expr, feat).asInstanceOf[Expr])
      case c@CompoundStatementExpr(compoundStatement: CompoundStatement) => c.copy(compoundStatement = replaceFeatureByTrue(compoundStatement, feat).asInstanceOf[CompoundStatement])
      case c@Pragma(command: StringLit) => c.copy(command = replaceFeatureByTrue(command, feat).asInstanceOf[StringLit])

      case e =>
        println("Missing case @ remove feature: " + e)
        e
    }
  } */

  def replaceFeatureByTrue[T <: Product](t: T, feat: FeatureExpr): T = {
    val r = manytd(rule {
      case l: List[Opt[_]] =>
        l.flatMap(o =>
          if (feat.&(o.feature).isContradiction()) {
            // println("Feature " + feat + " and " + o.feature + " in Opt: " + o + "are not compatible.")
            List()
          } else if (o.feature.equivalentTo(feat)) {
            List(o.copy(feature = FeatureExprFactory.True))
          } else {
            List(o)
          })
    })
    t match {
      case o: Opt[_] =>
        r(o.copy(feature = FeatureExprFactory.True)) match {
          case None => t
          case _ => r(o.copy(feature = FeatureExprFactory.True)).get.asInstanceOf[T]
        }
      case _ => r(t).get.asInstanceOf[T]
    }
  }

  def rewriteFeatureAndIds[T <: Product](t: T, feat: FeatureExpr, id: Id): T = {
    val r = manytd(rule {
      case l: List[Opt[_]] =>
        l.flatMap(o =>
          if (feat.&(o.feature).isContradiction()) {
            List()
          } else if (o.feature.equivalentTo(feat)) {
            List(o.copy(feature = FeatureExprFactory.True))
          } else {
            List(o)
          })
      case i: Id =>
        if (i.name == id.name) {
          alreadyReplaced += i
          Id("_" + IdMap.get(feat).get + "_" + i.name)
        } else {
          i
        }
    })
    t match {
      case o: Opt[_] => r(o.copy(feature = FeatureExprFactory.True)).get.asInstanceOf[T]
      case _ => r(t).get.asInstanceOf[T]
    }
  }

  def convertSingleDeclaration(dcl: Opt[Declaration], env: ASTEnv, defuse: IdentityHashMap[Id, List[Id]]): List[Opt[Declaration]] = {
    val decl = replaceFeatureByTrue(dcl, dcl.feature)
    decl.entry match {
      case d@Declaration(declSpecs, init) =>
        if (isVariable(decl)) {
          val featurecombinations = getFeatureExpressions(decl, env)
          val newDecls: List[Opt[Declaration]] = featurecombinations.map(x =>
            filterOptsByFeature(decl.copy(entry = d.copy(declSpecs = declSpecs.map(x => x match {
              case o@Opt(_, StructOrUnionSpecifier(_, Some(i: Id), _)) => convertId(x, dcl.feature, defuse)
              case Opt(_, e: EnumSpecifier) => convertAllIds(x, dcl.feature, defuse)
              case z => z
            }), init = init.map(y => y match {
              case Opt(_, i: InitDeclaratorI) => convertId(y, x, defuse)
              case z => z
            }))), x)
          )
          //newDecls.foreach(x => println(PrettyPrinter.print(x.entry) + "\n\n"))
          newDecls
        } else {
          val tempDecl = decl.copy(entry = d.copy(declSpecs = declSpecs.map(x => x match {
            case o@Opt(_, StructOrUnionSpecifier(_, Some(i: Id), _)) => convertId(x, dcl.feature, defuse)
            case Opt(_, e: EnumSpecifier) => convertAllIds(x, dcl.feature, defuse)
            case z => z
          }), init = init.map(x => x match {
            case Opt(_, i: InitDeclaratorI) => convertId(x, dcl.feature, defuse)
            case z => z
          })))
          List(tempDecl)
        }
      case _ => List(replaceFeatureByTrue(decl, dcl.feature))
    }
  }

  def convertId[T <: Product](t: T, ft: FeatureExpr, defuse: IdentityHashMap[Id, List[Id]]): T = {
    val r = oncetd(rule {
      case i: Id =>
        if (i.name != "main") {
          if (defuse.containsKey(i)) {
            val idUsages = defuse.get(i)
            idUsages.foreach(x => {
              if (idsToBeReplaced.containsKey(x)) {
                idsToBeReplaced.put(x, ft :: idsToBeReplaced.get(x))
              } else {
                idsToBeReplaced.put(x, List(ft))
              }
            })
          }
          replaceId.put(i, ft)
          if (!IdMap.contains(ft)) {
            IdMap += (ft -> IdMap.size)
          }
          Id("_" + IdMap.get(ft).get + "_" + i.name)
        } else {
          i
        }

    })
    r(t) match {
      case None => t
      case k => k.get.asInstanceOf[T]
    }
  }

  def convertAllIds[T <: Product](t: T, ft: FeatureExpr, defuse: IdentityHashMap[Id, List[Id]]): T = {
    val r = manytd(rule {
      case i: Id =>
        // TODO auf Funktionen beschränken
        if (i.name != "main") {
          if (defuse.containsKey(i)) {
            val idUsages = defuse.get(i)
            idUsages.foreach(x => {
              if (idsToBeReplaced.containsKey(x)) {
                idsToBeReplaced.put(x, ft :: idsToBeReplaced.get(x))
              } else {
                idsToBeReplaced.put(x, List(ft))
              }
            })
          }
          replaceId.put(i, ft)
          Id("_" + IdMap.get(ft).get + "_" + i.name)
        } else {
          i
        }

    })
    r(t).get.asInstanceOf[T]
  }

  def eqContains[T <: AnyRef](seq: Seq[T], toCheck: T): Boolean = {
    seq.foreach(x => if (x.eq(toCheck)) return true)
    return false
  }

  def removeOne[T <: AnyRef](seq: List[T], toRemove: T): List[T] = {
    seq.flatMap(x => if (x.eq(toRemove)) List() else List(x))
  }

  def removeList[T <: AnyRef](seq: List[T], toRemove: List[T]): List[T] = {
    seq.flatMap(x => if (eqContains(toRemove, x)) List() else List(x))
  }

  def filterOptsByFeature[T <: Product](t: T, feat: FeatureExpr): T = {
    val r = manytd(rule {
      case l: List[Opt[_]] =>
        l.flatMap(o =>
          if (feat.&(o.feature).isContradiction()) {
            List()
          } else if (feat.implies(o.feature).isTautology) {
            List(o.copy(feature = FeatureExprFactory.True))
          } else {
            List()
          })
    })
    r(t) match {
      case None => t
      case k => k.get.asInstanceOf[T]
    }
  }

  def filterTrueOpts[T <: Product](t: T): T = {
    val r = manytd(rule {
      case l: List[Opt[_]] =>
        l.flatMap(o =>
          if (o.feature.equivalentTo(FeatureExprFactory.True)) {
            List(o)
          } else {
            List()
          })
    })
    r(t) match {
      case None => t
      case k => k.get.asInstanceOf[T]
    }
  }

  def removeContraryOptsByFeature[T <: Product](t: T, feat: FeatureExpr): T = {
    val r = manytd(rule {
      case l: List[Opt[_]] =>
        l.flatMap(o =>
          if (feat.&(o.feature).isContradiction()) {
            List()
          } else if (o.feature.equivalentTo(feat) || feat.implies(o.feature).isTautology) {
            List(o.copy(feature = FeatureExprFactory.True))
          } else {
            List(o)
          })
    })
    r(t) match {
      case None => t
      case k => k.get.asInstanceOf[T]
    }
  }

  def removeContraryOptsAndReplaceTrueByFeature[T <: Product](t: T, feat: FeatureExpr): T = {
    val r = manytd(rule {
      case l: List[Opt[_]] =>
        l.flatMap(o =>
          if (feat.&(o.feature).isContradiction()) {
            List()
          } else if (o.feature.equivalentTo(feat) || feat.implies(o.feature).isTautology || o.feature.equivalentTo(FeatureExprFactory.True)) {
            List(o.copy(feature = feat))
          } else {
            List(o)
          })
    })
    r(t) match {
      case None => t
      case k => k.get.asInstanceOf[T]
    }
  }

  def transformDeclarations[T <: Product](t: T, env: ASTEnv, defuse: IdentityHashMap[Id, List[Id]]): T = {
    fillIdMap(t, env)
    val r = alltd(rule {
      case o@Opt(ft: DefinedExternal, entry) =>
        if (ft != FeatureExprFactory.True) {
          entry match {
            case decl: Declaration =>
              val newDecl = convertSingleDeclaration(o.asInstanceOf[Opt[Declaration]], env, defuse)
              //println("Converted:\n" + o + "\nto: " + newDecl)
              //println("++Pretty old++\n" + PrettyPrinter.print(decl) + " @ " + decl.getPositionFrom + "\n++Pretty new++\n" + PrettyPrinter.print(newDecl.entry) + "\n")
              //println("++Pretty old++\n" + PrettyPrinter.print(decl) + " @ " + decl.getPositionFrom + "\n\nSpecs: " + decl.declSpecs + "\nInit: " + decl.init + "\n\n")
              //transformDeclarations(newDecl, env, defuse)
              newDecl
            case fd: FunctionDef =>
              convertId(replaceFeatureByTrue(o, o.feature), o.feature, defuse)
            case e: ExprStatement =>
              val ifStmt = IfStatement(One(featureToCExpr(o.feature)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, e)))), List(), None)
              o.copy(entry = ifStmt)
            case i: IfStatement =>
              val ftLst = filterFeatures(i, env)
              val cond = One(featureToCExpr(ftLst.head))
              val then = One(CompoundStatement(List(Opt(FeatureExprFactory.True, filterOptsByFeature(i, ftLst.head)))))
              val elifs = ftLst.tail.map(x => Opt(FeatureExprFactory.True, ElifStatement(One(featureToCExpr(x)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, filterOptsByFeature(i, x)))))))).toList
              // val elseBranch = Some(One(CompoundStatement(List(Opt(FeatureExprFactory.True, filterOptsByFeature(i, FeatureExprFactory.True))))))
              val ifStmt = IfStatement(cond, then, elifs, None)
              Opt(FeatureExprFactory.True, ifStmt)
            case sd: StructDeclaration =>
              convertAllIds(replaceFeatureByTrue(o, o.feature), o.feature, defuse)

            case i: InitDeclaratorI =>
              println("WWW: " + env.parent(env.parent(i)))
              o
            case s: Specifier =>
              o.copy(feature = FeatureExprFactory.True)
            case s: String =>
              o.copy(feature = FeatureExprFactory.True)
            case p: Pragma =>
              o.copy(feature = FeatureExprFactory.True)
            case k =>
              println("Missing Opt: " + o + "\nFrom: " + PrettyPrinter.print(k.asInstanceOf[AST]))
              o
          }
        } else {
          o
        }
    })
    var idsToReplace: IdentityHashMap[Id, List[FeatureExpr]] = new IdentityHashMap()
    val tmp = r(t).get.asInstanceOf[T]

    replaceId.keySet.toArray().foreach(y => if (defuse.containsKey(y) && !defuse.get(y).isEmpty) {
      println("Defuse from: " + y + " is: " + defuse.get(y))
      defuse.get(y).foreach(x =>
        if (idsToReplace.containsKey(x)) {
          idsToReplace.put(x, idsToReplace.get(x) ++ List(replaceId.get(x)))
        } else {
          idsToReplace.put(x, List(replaceId.get(x)))
        })
    })


    idsToReplace.keySet.toArray().foreach(x =>
      if (!eqContains(alreadyReplaced, x)) {
        findPriorASTElem[TypeDefTypeSpecifier](x.asInstanceOf[Id], env) match {
          case None =>
            findPriorASTElem[Statement](x.asInstanceOf[Id], env) match {
              case Some(o: Statement) =>
                if (!eqContains(alreadyReplaced, x)) {
                  env.parent(o) match {
                    case op: Opt[Statement] =>
                      val ifStmt = IfStatement(One(featureToCExpr(op.feature)), One(CompoundStatement(List(rewriteFeatureAndIds(op, op.feature, x.asInstanceOf[Id])))), List(), None)
                      toBeReplaced.put(op, Opt(FeatureExprFactory.True, ifStmt))
                    case on@One(st: Statement) =>

                  }

                  //work = replaceOpt(work, rewriteFeatureAndIds(current, current.feature, x.asInstanceOf[Id]), current)
                }
              case None => println("Missing: " + env.parent(env.parent(x)))
            }
          case Some(o: TypeDefTypeSpecifier) =>
            val newId = Id("_" + IdMap.get(idsToReplace.get(x).head).get + "_" + o.name.name)
            //println("Replaced " + o.name + " with " + newId)
            toBeReplaced.put(o.name, newId)

          //work = replaceId(work, newId, o.name)
        }
      })
    alreadyReplaced.clear()

    val work = replaceAnyIdHashMap(t, toBeReplaced)
    val result = r(work).get.asInstanceOf[T]
    toBeReplaced.clear()


    println("Defuse: " + defuse)
    //println("\n\n" + idsToReplace.keySet.toArray.foreach(x => println(x.toString() + "\nParent: " + env.parent(x) + "\nParent of Parent: " + env.parent(env.parent(x)) + "\n")))
    result
  }

  def transformAst[T <: Product](t: T, env: ASTEnv, defuse: IdentityHashMap[Id, List[Id]]): T = {
    fillIdMap(t, env)
    def transformDeclarationsRecursive[T <: Product](t: T, env: ASTEnv, defuse: IdentityHashMap[Id, List[Id]]): T = {
      val r = alltd(rule {
        case l: List[Opt[_]] =>
          l.flatMap(x => x match {
            case o@Opt(ft: FeatureExpr, entry) =>
              if (ft != FeatureExprFactory.True) {
                entry match {
                  case declStmt@DeclarationStatement(decl: Declaration) =>
                    val newDecl = Opt(FeatureExprFactory.True, DeclarationStatement(replaceFeatureByTrue(convertId(decl, o.feature, defuse), o.feature)))
                    List(newDecl)
                  case decl: Declaration =>
                    val newDecls = convertSingleDeclaration(o.asInstanceOf[Opt[Declaration]], env, defuse)
                    //println("Converted:\n" + o + "\nto: " + newDecls)
                    //println("++Pretty old++\n" + PrettyPrinter.print(decl) + " @ " + decl.getPositionFrom + "\n++Pretty new++\n" + PrettyPrinter.print(newDecls.head.entry) + "\n")
                    //transformDeclarations(newDecl, env, defuse)
                    newDecls
                  case e: Enumerator =>
                    List(transformDeclarationsRecursive(convertId(replaceFeatureByTrue(o, o.feature), o.feature, defuse), env, defuse))
                  case fd: FunctionDef =>
                    var tmpFunctDef = convertId(replaceFeatureByTrue(o, o.feature), o.feature, defuse).asInstanceOf[Opt[FunctionDef]]
                    if (fd.getName.equals("main")) {
                      tmpFunctDef = replaceFeatureByTrue(o, o.feature).asInstanceOf[Opt[FunctionDef]]
                    }
                    if (isVariable(tmpFunctDef.entry.specifiers) || isVariable(tmpFunctDef.entry.declarator)) {
                      val features2 = getFeatureCombinations(removeOne(getNextFeatures(fd).flatMap(x => x.collectDistinctFeatures2).toList, ft))
                      val features = getFeatureCombinations(removeList(getNextFeatures(fd).flatMap(x => x.collectDistinctFeatures2).toList, ft.collectDistinctFeatures2.toList))
                      println("Features are: " + features)
                      val result = features.map(x => {
                        transformDeclarationsRecursive(removeContraryOptsByFeature(convertId(replaceFeatureByTrue(o, o.feature), x.&(ft), defuse), x), env, defuse)
                        //tmp.copy(entry = tmp.entry.copy(stmt = transformDeclarationsRecursive(tmp.entry.stmt, env, defuse)))
                      })
                      /*if (needTrueExpression(features)) {
                       val tmp = convertId(replaceFeatureByTrue(o, o.feature), FeatureExprFactory.True)
                       val trueFunctionDef = removeContraryOptsByFeature(convertId(replaceFeatureByTrue(o, o.feature), o.feature), o.feature)
                       trueFunctionDef :: result
                     } else {
                       result
                     } */
                      result
                      //val newFunctDef = tmpFunctDef.copy(entry = entry.copy(stmt = transformDeclarationsRecursive(entry.stmt, env, defuse)))
                      //List(newFunctDef)
                    } else {
                      if (isVariable(tmpFunctDef.entry.stmt)) {
                        //List(transformDeclarationsRecursive(o, env, defuse))
                        List(tmpFunctDef)
                      } else {
                        List(tmpFunctDef)
                      }
                    }
                  case e: ExprStatement =>
                    val ifStmt = IfStatement(One(featureToCExpr(o.feature)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, filterOptsByFeature(e, o.feature))))), List(), None)
                    List(Opt(FeatureExprFactory.True, ifStmt))
                  case i@IfStatement(Choice(ft, cThen, cEls), then, elif, els) =>
                    val if1 = Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(ft)), One(filterOptsByFeature(CompoundStatement(List(Opt(FeatureExprFactory.True, IfStatement(cThen, transformDeclarationsRecursive(then, env, defuse), transformDeclarationsRecursive(elif, env, defuse), transformDeclarationsRecursive(els, env, defuse))))), ft)), List(), Some(One(filterOptsByFeature(CompoundStatement(List(Opt(FeatureExprFactory.True, IfStatement(cEls, transformDeclarationsRecursive(then, env, defuse), transformDeclarationsRecursive(elif, env, defuse), transformDeclarationsRecursive(els, env, defuse))))), getFeatureExpressions(cEls, env).head)))))
                    val elsFeature = getFeatureExpressions(cEls, env).head
                    val newIf2 = IfStatement(One(featureToCExpr(ft)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, IfStatement(filterOptsByFeature(cThen, ft), transformDeclarationsRecursive(filterOptsByFeature(then, ft), env, defuse), transformDeclarationsRecursive(filterOptsByFeature(elif, ft), env, defuse), transformDeclarationsRecursive(filterOptsByFeature(els, ft), env, defuse)))))), List(), Some(One(CompoundStatement(List(Opt(FeatureExprFactory.True, IfStatement(filterOptsByFeature(cEls, elsFeature), transformDeclarationsRecursive(filterOptsByFeature(then, elsFeature), env, defuse), transformDeclarationsRecursive(filterOptsByFeature(elif, elsFeature), env, defuse), transformDeclarationsRecursive(filterOptsByFeature(els, elsFeature), env, defuse))))))))
                    val newIf = IfStatement(
                      One(featureToCExpr(ft)),
                      One(CompoundStatement(List(Opt(FeatureExprFactory.True,
                        IfStatement(
                          replaceFeatureByTrue(cThen, ft),
                          transformDeclarationsRecursive(removeContraryOptsByFeature(then, ft), env, defuse),
                          transformDeclarationsRecursive(removeContraryOptsByFeature(elif, ft), env, defuse),
                          transformDeclarationsRecursive(removeContraryOptsByFeature(els, ft), env, defuse)))))),
                      List(),
                      Some(One(CompoundStatement(List(Opt(FeatureExprFactory.True, IfStatement(removeContraryOptsByFeature(cEls, elsFeature), transformDeclarationsRecursive(removeContraryOptsByFeature(then, elsFeature), env, defuse), transformDeclarationsRecursive(removeContraryOptsByFeature(elif, elsFeature), env, defuse), transformDeclarationsRecursive(removeContraryOptsByFeature(els, elsFeature), env, defuse))))))))
                    List(Opt(FeatureExprFactory.True, newIf))
                  case i@IfStatement(One(cond), _, _, _) =>
                    val ftLst = getSingleFeatureSet(i, env)
                    val cond = One(featureToCExpr(ftLst.head))
                    val then = One(CompoundStatement(List(Opt(FeatureExprFactory.True, filterOptsByFeature(i, ftLst.head)))))
                    val elifs = ftLst.tail.map(x => Opt(FeatureExprFactory.True, ElifStatement(One(featureToCExpr(x)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, filterOptsByFeature(i, x)))))))).toList
                    // val elseBranch = Some(One(CompoundStatement(List(Opt(FeatureExprFactory.True, filterOptsByFeature(i, FeatureExprFactory.True))))))
                    val ifStmt = IfStatement(cond, then, elifs, None)
                    List(Opt(FeatureExprFactory.True, ifStmt))
                  case sd: StructDeclaration =>
                    List(convertAllIds(replaceFeatureByTrue(o, o.feature), o.feature, defuse))

                  case i: InitDeclaratorI =>
                    /// TODO: Handeln?
                    List(o)
                  case s: Specifier =>
                    List(o.copy(feature = FeatureExprFactory.True))
                  case s: String =>
                    List(o.copy(feature = FeatureExprFactory.True))
                  case p: Pragma =>
                    // TODO: Eventuell variabel lassen
                    List(o.copy(feature = FeatureExprFactory.True))
                  case k =>
                    println("Missing Opt: " + o + "\nFrom: " + PrettyPrinter.print(k.asInstanceOf[AST]))
                    List(o)
                }
              } else {
                entry match {

                  case e@Enumerator(id, Some(soe: SizeOfExprT)) =>
                    if (isVariable(e)) {
                      /*
                      val featureSet = getSingleFeatureSet(e, env)
                      val newEnumerators = featureSet.map(x => Opt(FeatureExprFactory.True, transformDeclarationsRecursive(filterOptsByFeature(e, x), env, defuse))).toList
                      println("Old: " + e + "\n" + PrettyPrinter.print(e))
                      newEnumerators.foreach(x => println("\nNew\n" + PrettyPrinter.print(x.entry)))
                      */
                      /*
                   val ifStmt = IfStatement(
                     One(featureToCExpr(featureSet.head)),
                     One(CompoundStatement(Opt(FeatureExprFactory.True, filterOptsByFeature(e, featureSet.head)))),
                     featureSet.tail.map(x => Opt(FeatureExprFactory.True, ElifStatement(One(featureToCExpr(x), One(CompoundStatement(List(Opt(FeatureExprFactory.True, filterOptsByFeature(e, x))))))))).toList,
                     None) */
                      val featureCombinations = getFeatureCombinations(filterFeatures(e, env).toList)
                      val newEnums = featureCombinations.map(x => Opt(FeatureExprFactory.True, convertId(filterOptsByFeature(e, x), x, defuse))).toList

                      newEnums
                    } else {
                      List(o)
                    }
                  case e@Enumerator(id, Some(nae: NAryExpr)) =>
                    if (isVariable(e)) {
                      val featureSet = getSingleFeatureSet(e, env)
                      val newEnumerators = featureSet.map(x => Opt(FeatureExprFactory.True, convertId(filterOptsByFeature(e, x), x, defuse))).toList
                      newEnumerators
                    } else {
                      List(o)
                    }
                  case sd: StructDeclaration =>
                    if (isVariable(sd)) {
                      val features = getSingleFeatureSet(sd, env)
                      val newStructDecls = features.map(x => Opt(FeatureExprFactory.True, convertId(filterOptsByFeature(sd, x), x, defuse)))
                      newStructDecls
                    } else {
                      List(o)
                    }
                  case d@Declaration(declSpecs, init) =>
                    if (isVariable(d)) {
                      //println("Current Declaration:\n" + d + "\n" + PrettyPrinter.print(d))
                      val features = getSingleFeatureSet(d, env)
                      if (declSpecs.exists(x => x.entry.isInstanceOf[EnumSpecifier] || (x.entry.isInstanceOf[StructOrUnionSpecifier] && x.feature.equivalentTo(FeatureExprFactory.True)))) {
                        List(transformDeclarationsRecursive(o, env, defuse))
                      } else {
                        val newDecls = Opt(FeatureExprFactory.True, Declaration(filterOptsByFeature(declSpecs, FeatureExprFactory.True), filterOptsByFeature(init, FeatureExprFactory.True))) :: features.map(x => Opt(FeatureExprFactory.True, Declaration(filterOptsByFeature(declSpecs, x), convertId(filterOptsByFeature(init, x), x, defuse)))).toList
                        newDecls
                      }
                      /*}
                    if (init.exists(i => !(i.feature.equivalentTo(FeatureExprFactory.True)))) {
                      val featureSet = init.map(x => x.feature).toSet
                      val newDecls = featureSet.map(x => transformDeclarationsRecursive(replaceFeatureByTrue(Opt(FeatureExprFactory.True, Declaration(declSpecs, init.filter(y => y.feature.equivalentTo(x)).map(z => convertId(z, x)))), x, env), env, defuse))
                      newDecls*/
                    } else {
                      List(o)
                    }
                  case i@IfStatement(c@Choice(ft, cThen, cEls), then, elif, els) =>
                    val if1 = Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(ft)), One(filterOptsByFeature(CompoundStatement(List(Opt(FeatureExprFactory.True, IfStatement(cThen, transformDeclarationsRecursive(then, env, defuse), transformDeclarationsRecursive(elif, env, defuse), transformDeclarationsRecursive(els, env, defuse))))), ft)), List(), Some(One(filterOptsByFeature(CompoundStatement(List(Opt(FeatureExprFactory.True, IfStatement(cEls, transformDeclarationsRecursive(then, env, defuse), transformDeclarationsRecursive(elif, env, defuse), transformDeclarationsRecursive(els, env, defuse))))), getFeatureExpressions(cEls, env).head)))))
                    filterOptsByFeature(o, ft)
                    List(if1)
                  case i@IfStatement(One(cond), One(then), elif, els) =>
                    if (isVariable(i)) {
                      var tmp = o.asInstanceOf[Opt[IfStatement]]
                      if (isVariable(cond)) {
                        val featureCombinations = getFeatureCombinations(filterFeatures(cond, env).toList)
                        val newIfStmt = IfStatement(One(featureToCExpr(featureCombinations.head)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, IfStatement(One(filterOptsByFeature(cond, featureCombinations.head)), filterOptsByFeature(One(then), featureCombinations.head), elif.map(j => filterOptsByFeature(j, featureCombinations.head)), filterOptsByFeature(els, featureCombinations.head)))))), featureCombinations.tail.map(x => Opt(FeatureExprFactory.True, ElifStatement(One(featureToCExpr(x)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, IfStatement(One(filterOptsByFeature(cond, x)), filterOptsByFeature(One(then), x), elif.map(j => filterOptsByFeature(j, x)), filterOptsByFeature(els, x))))))))).toList, Some(One(CompoundStatement(List(Opt(FeatureExprFactory.True, IfStatement(One(filterOptsByFeature(cond, FeatureExprFactory.True)), filterOptsByFeature(One(then), FeatureExprFactory.True), elif.map(j => filterOptsByFeature(j, FeatureExprFactory.True)), filterOptsByFeature(els, FeatureExprFactory.True))))))))
                        tmp = tmp.copy(entry = newIfStmt)
                      }
                      if (isVariable(then)) {
                        tmp = tmp.copy(entry = tmp.entry.copy(thenBranch = One(transformDeclarationsRecursive(then, env, defuse))))
                      }
                      if (isVariable(els)) {
                        tmp = tmp.copy(entry = tmp.entry.copy(elseBranch = transformDeclarationsRecursive(els, env, defuse)))
                      }
                      List(tmp)
                    } else {
                      List(o)
                    }
                  case e: ExprStatement =>
                    if (containsIdUsage(e)) {
                      val features = getIdUsageFeatureList(e)
                      features.map(x => Opt(True, exprStatementToIf(e, x)))
                    } else if (isVariable(e)) {
                      List(transformDeclarationsRecursive(o, env, defuse))
                    } else {
                      List(o)
                    }
                  case k: Product =>
                    if (isVariable(k)) {
                      List(transformDeclarationsRecursive(o, env, defuse))
                    } else {
                      List(o)
                    }
                  case r =>
                    List(o)
                }
              }
            case k =>
              List(transformDeclarationsRecursive(k, env, defuse))
          })
      })
      r(t) match {
        case None => t
        case k =>
          k.get.asInstanceOf[T]
      }
    }
    println("Test")
    transformDeclarationsRecursive(t, env, defuse)
    //val toReallyReplace = replaceId.keySet().toArray().flatMap(x => if (defuse.containsKey(x)) defuse.get(x).map(y => (y -> replaceId.get(x))) else List())
  }

  def getParentOpt[T <: Product](t: T, env: ASTEnv): Opt[_] = {
    env.parent(t) match {
      case t: TranslationUnit =>
        null
      case o: Opt[_] =>
        o
      case k => getParentOpt(k, env)
    }
  }

  /*
  Old imeplementation of the lift opt function which makes use of the ast environment and looks for the next higher opt node of a variable opt node.
   */
  def liftOptsOld[T <: Product](t: T, env: ASTEnv): T = {
    def fillReplaceMap[T <: Product](t: T, env: ASTEnv): Boolean = {
      val r = manytd(query {
        case o@Opt(ft, entry) =>
          if (!ft.equivalentTo(FeatureExprFactory.True)) {
            val parent = getParentOpt(o, env)
            val toBeReplaced = List(replaceFeatureByTrue(parent, ft).copy(feature = ft), removeContraryOptsByFeature(parent, ft.not()).copy(feature = ft.not()))
            /*
            if (liftOptReplaceMap.contains(parent) && liftOptReplaceMap.get(parent).get != toBeReplaced) {
              val current = liftOptReplaceMap.get(parent).get ++ toBeReplaced
              liftOptReplaceMap += (parent -> current)
            } else {
              liftOptReplaceMap += (parent -> toBeReplaced)
            } */
            liftOptReplaceMap += (parent -> toBeReplaced)
          }
      })
      r(t)
      false
    }
    fillReplaceMap(t, env)
    val newAst = replaceWithMap(t, liftOptReplaceMap)
    liftOptReplaceMap = Map()
    newAst
  }

  def nextLevelContainsVariability(t: Any): Boolean = {
    val optList = getNextOptList(t)
    val result = optList.exists(x => (x.feature != FeatureExprFactory.True))
    result
  }

  def secondNextLevelContainsVariability(t: Any): Boolean = {
    val optList = getNextOptList(t)
    var result = false
    if (!optList.isEmpty) {
      val finalOptList = optList.flatMap(x => getNextOptList(x))
      result = finalOptList.exists(x => (x.feature != FeatureExprFactory.True))
    }
    result
  }

  def containsDeclaration(a: Any): Boolean = {
    return !filterASTElems[Declaration](a).isEmpty
  }

  def containsIdUsage(a: Any): Boolean = {
    val ids = filterASTElems[Id](a)
    ids.foreach(x => if (idsToBeReplaced.containsKey(x)) return true)
    return false
  }

  def getIdUsageFeatureList(a: Any): List[FeatureExpr] = {
    val ids = filterASTElems[Id](a)
    val features = ids.flatMap(x => if (idsToBeReplaced.containsKey(x)) idsToBeReplaced.get(x) else List())
    features.distinct
  }

  def getNextOptList(a: Any): List[Opt[_]] = {
    a match {
      case d: Opt[_] => List(d)
      case l: List[_] => l.flatMap(getNextOptList(_))
      case p: Product => p.productIterator.toList.flatMap(getNextOptList(_))
      case _ => List()
    }
  }

  def getSecondNextOptList(a: Any): List[Opt[_]] = {
    val optList = getNextOptList(a)
    if (!optList.isEmpty) {
      optList.flatMap(x => getNextOptList(x))
    } else {
      List()
    }
  }

  def getNextFeatures(a: Any): Set[FeatureExpr] = {
    def getNextFeatureHelp(a: Any): List[FeatureExpr] = {
      a match {
        case d@Opt(ft, entry) => List(ft)
        case l: List[_] => l.flatMap(getNextFeatures(_))
        case p: Product => p.productIterator.toList.flatMap(getNextFeatures(_))
        case _ => List()
      }
    }
    getNextFeatureHelp(a).toSet
  }

  /*
  New implementation of the lift opt function. This method looks at all Opt(True, entry) nodes and checks if the next lower level
  of opt nodes is variable. This node is then copied for the different feature combinations of his next level of opt nodes.
   */
  def liftOpts[T <: Product](t: T, env: ASTEnv): T = {
    val r = manytd(rule {
      case l: List[Opt[_]] =>
        l.flatMap(x => x match {
          case o@Opt(ft: FeatureExpr, entry) =>
            if (ft == FeatureExprFactory.True && nextLevelContainsVariability(entry)) {
              val nextLevel = getNextOptList(entry)
              val features = nextLevel.flatMap(x => if (x.feature != FeatureExprFactory.True) List(x.feature) else List()).toSet
              var needTrueExpression = false
              features.foreach(x => if (!features.exists(y => x.&(y).isContradiction())) {
                needTrueExpression = true
              })
              val result = features.map(x => removeContraryOptsAndReplaceTrueByFeature(o, x).copy(feature = x)).toList
              if (needTrueExpression) {
                removeContraryOptsAndReplaceTrueByFeature(o, FeatureExprFactory.True) :: result
              } else {
                result
              }
            } else {
              List(o)
            }
        })
    })
    val newAst = r(t).get.asInstanceOf[T]
    newAst
  }

  def exprStatementToIf(e: ExprStatement, ft: FeatureExpr): IfStatement = {
    def convertId[T <: Product](t: T, feat: FeatureExpr): T = {
      val r = manytd(rule {
        case i: Id =>
          if (idsToBeReplaced.containsKey(i)) {
            if (!IdMap.contains(feat)) {
              IdMap += (feat -> IdMap.size)
            }
            Id("_" + IdMap.get(feat).get + "_" + i.name)
          } else {
            i
          }
      })
      r(t) match {
        case None => t
        case k => k.get.asInstanceOf[T]
      }
    }
    IfStatement(One(featureToCExpr(ft)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, convertId(removeContraryOptsByFeature(e, ft), ft))))), List(), None)
  }
}