package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import org.kiama.rewriting.Rewriter._
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
        if (!f.equivalentTo(FeatureExprFactory.True) && !f.equivalentTo(FeatureExprFactory.False) && !lst.contains(f)) {
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

  def getDistinctFeatures(lst: List[FeatureExpr]): List[FeatureExpr] = {
    lst.filterNot(x => x.equals(FeatureExprFactory.True)).flatMap(x => x.collectDistinctFeatures2).toSet.toList
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

  /*
 Creates all possible 2^n combinations for a list of n raw feature expressions. List(def(x64), def(x86)) becomes
 List(def(x64)&def(x86),!def(x64)&def(x86),def(x64)&!def(x86),!def(x64)&!def(x86).
  */
  def isExclusion(lst: List[FeatureExpr]): Boolean = {
    def isExclusionRecursive(lst: List[FeatureExpr]): Boolean = {
      if (lst.size == 2) {
        lst.head.mex(lst.tail.head).isTautology()
      } else if (lst.size > 2) {
        lst.head.mex(lst.tail.head).isTautology().&&(isExclusionRecursive(lst.tail))
      } else {
        false
      }
    }
    isExclusionRecursive(lst)
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
      case o@Opt(ft, entry) =>
        if (ft.equals(FeatureExprFactory.True)) {
          r(o) match {
            case None => t
            case _ => r(o).get.asInstanceOf[T]
          }
        } else if (ft.equals(feat)) {
          val newOpt = Opt(FeatureExprFactory.True, entry)
          r(newOpt) match {
            case None => newOpt.asInstanceOf[T]
            case _ => r(newOpt).get.asInstanceOf[T]
          }
        } else {
          r(o) match {
            case None => t
            case _ => r(o).get.asInstanceOf[T]
          }
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

            case cmpdStmt: CompoundStatement =>
              List(Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(ft)), One(cmpdStmt), List(), None)))

            case s: Specifier =>
              o.copy(feature = FeatureExprFactory.True)
            case s: String =>
              o.copy(feature = FeatureExprFactory.True)
            case p: Pragma =>
              o.copy(feature = FeatureExprFactory.True)
            case k =>
              println("Missing Opt: " + o + "\nFrom: " + k.asInstanceOf[AST].getPositionFrom + "\n")
              //println("Missing Opt: " + o + "\nFrom: " + PrettyPrinter.print(k.asInstanceOf[AST]))
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
                    newDecls

                  case typeless: TypelessDeclaration =>
                    //TODO: Umwandlung
                    List(o)
                  case label: LabelStatement =>
                    if (containsIdUsage(label)) {
                      List(Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(ft)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, convertIdUsagesFromDefuse(label, ft))))), List(), None)))
                    } else {
                      List(Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(ft)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, label)))), List(), None)))
                    }

                  case fd: FunctionDef =>
                    var tmpFunctDef = convertId(replaceFeatureByTrue(o, o.feature), o.feature, defuse).asInstanceOf[Opt[FunctionDef]]
                    if (fd.getName.equals("main")) {
                      tmpFunctDef = replaceFeatureByTrue(o, o.feature).asInstanceOf[Opt[FunctionDef]]
                    }
                    if (isVariable(tmpFunctDef.entry.specifiers) || isVariable(tmpFunctDef.entry.declarator)) {
                      val features = getFeatureCombinations(removeList(getNextFeatures(fd).flatMap(x => x.collectDistinctFeatures2).toList, ft.collectDistinctFeatures2.toList))
                      //println("Features are: " + features)
                      val result = features.map(x => {
                        transformDeclarationsRecursive(removeContraryOptsByFeature(convertId(replaceFeatureByTrue(o, o.feature), x.&(ft), defuse), x), env, defuse)
                      })
                      result
                    } else {
                      if (isVariable(fd.stmt) || containsIdUsage(fd.stmt)) {
                        List(tmpFunctDef.copy(entry = tmpFunctDef.entry.copy(stmt = transformDeclarationsRecursive(tmpFunctDef.entry.stmt, env, defuse))))
                      } else {
                        List(tmpFunctDef)
                      }
                    }

                  case nfd: NestedFunctionDef =>
                    var tmpFunctDef = convertId(replaceFeatureByTrue(o, o.feature), o.feature, defuse).asInstanceOf[Opt[NestedFunctionDef]]
                    if (nfd.getName.equals("main")) {
                      tmpFunctDef = replaceFeatureByTrue(o, o.feature).asInstanceOf[Opt[NestedFunctionDef]]
                    }
                    if (isVariable(tmpFunctDef.entry.specifiers) || isVariable(tmpFunctDef.entry.declarator)) {
                      val features = getFeatureCombinations(removeList(getNextFeatures(nfd).flatMap(x => x.collectDistinctFeatures2).toList, ft.collectDistinctFeatures2.toList))
                      val result = features.map(x => {
                        transformDeclarationsRecursive(removeContraryOptsByFeature(convertId(replaceFeatureByTrue(o, o.feature), x.&(ft), defuse), x), env, defuse)
                      })
                      result
                    } else {
                      if (isVariable(nfd.stmt) || containsIdUsage(nfd.stmt)) {
                        List(tmpFunctDef.copy(entry = tmpFunctDef.entry.copy(stmt = transformDeclarationsRecursive(tmpFunctDef.entry.stmt, env, defuse))))
                      } else {
                        List(tmpFunctDef)
                      }
                    }

                  case e: Enumerator =>
                    val result = List(transformDeclarationsRecursive(convertId(replaceFeatureByTrue(o, o.feature), o.feature, defuse), env, defuse))
                    result
                  case sd: StructDeclaration =>
                    List(convertAllIds(replaceFeatureByTrue(o, o.feature), o.feature, defuse))


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
                    val ftLst = getSingleFeatureSet(o, env)
                    val cond = One(featureToCExpr(ftLst.head))
                    val then = One(CompoundStatement(List(Opt(FeatureExprFactory.True, filterOptsByFeature(i, ftLst.head)))))
                    val elifs = ftLst.tail.map(x => Opt(FeatureExprFactory.True, ElifStatement(One(featureToCExpr(x)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, filterOptsByFeature(i, x)))))))).toList
                    // val elseBranch = Some(One(CompoundStatement(List(Opt(FeatureExprFactory.True, filterOptsByFeature(i, FeatureExprFactory.True))))))
                    val ifStmt = IfStatement(cond, then, elifs, None)
                    List(Opt(FeatureExprFactory.True, ifStmt))

                  case r: ReturnStatement =>
                    if (containsIdUsage(r)) {
                      //TODO: IdUsage
                      List(Opt(FeatureExprFactory.True, transformDeclarationsRecursive(removeContraryOptsByFeature(statementToIf(r, ft), ft), env, defuse)))
                    } else {
                      List(Opt(FeatureExprFactory.True, transformDeclarationsRecursive(removeContraryOptsByFeature(statementToIf(r, ft), ft), env, defuse)))
                    }

                  case w: WhileStatement =>
                    val result = List(Opt(FeatureExprFactory.True, transformDeclarationsRecursive(removeContraryOptsByFeature(statementToIf(w, ft), ft), env, defuse)))
                    result

                  case elif@ElifStatement(One(expr: Expr), then) =>
                    List(Opt(FeatureExprFactory.True, transformDeclarationsRecursive(removeContraryOptsByFeature(ElifStatement(One(NAryExpr(featureToCExpr(ft), List(Opt(FeatureExprFactory.True, NArySubExpr("&&", expr))))), then), ft), env, defuse)))

                  case p: Pragma =>
                    // TODO: Eventuell variabel lassen
                    List(o.copy(feature = FeatureExprFactory.True))
                  case s: Specifier =>
                    List(o.copy(feature = FeatureExprFactory.True))
                  case s: String =>
                    List(o.copy(feature = FeatureExprFactory.True))
                  case es: EmptyStatement =>
                    List()
                  case ee: EmptyExternalDef =>
                    List()
                  case k =>
                    println("Missing Opt: " + o + "\nFrom: " + k.asInstanceOf[AST].getPositionFrom + "\n")
                    //println("Missing Opt: " + o + "\nFrom: " + PrettyPrinter.print(k.asInstanceOf[AST]) + "\nParent = " + PrettyPrinter.print(env.parent(o).asInstanceOf[AST]))
                    List(o)
                }
              } else {
                entry match {
                  case ss: SwitchStatement =>
                    if (isVariable(ss)) {
                      // println("Test: " + getFeatureCombinations(getFeatureExpressions(ss, env)))
                      val features = getFeatureCombinations(getFeatureExpressions(ss, env).flatMap(x => x.collectDistinctFeatures2).toSet.toList)
                      features.map(x => {
                        optStatementToIf(Opt(x, transformDeclarationsRecursive(removeContraryOptsByFeature(replaceFeatureByTrue(o, o.feature), x), env, defuse).entry).asInstanceOf[Opt[Statement]])
                      })
                    } else {
                      List(o)
                    }
                  case fd: FunctionDef =>
                    if (isVariable(fd.specifiers) || isVariable(fd.declarator)) {
                      val specifierFeatures = getNextFeatures(fd).toList.filterNot(x => x.equals(FeatureExprFactory.True))
                      if (isExclusion(specifierFeatures)) {
                        getNextFeatures(fd).toList.filterNot(x => x.equals(FeatureExprFactory.True)).map(x => {
                          transformDeclarationsRecursive(removeContraryOptsByFeature(convertId(replaceFeatureByTrue(o, o.feature), x, defuse), x), env, defuse)
                        })
                      } else {
                        val features = getFeatureCombinations(removeList(getNextFeatures(fd).flatMap(x => x.collectDistinctFeatures2).toList, ft.collectDistinctFeatures2.toList))
                        val result = features.map(x => {
                          transformDeclarationsRecursive(removeContraryOptsByFeature(convertId(replaceFeatureByTrue(o, o.feature), x.&(ft), defuse), x), env, defuse)
                        })
                        result
                      }
                    } else {
                      if (isVariable(fd.stmt) || containsIdUsage(fd.stmt)) {
                        List(transformDeclarationsRecursive(o, env, defuse))
                      } else {
                        List(o)
                      }
                    }
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
                      val features = getNextVariableFeatures(sd, env)
                      if (!features.isEmpty) {
                        if (sd.qualifierList.exists(x => (x.entry.isInstanceOf[StructOrUnionSpecifier]))) {
                          features.map(x => Opt(FeatureExprFactory.True, transformDeclarationsRecursive(filterOptsByFeature(sd, x), env, defuse)))
                        } else {
                          features.map(x => Opt(FeatureExprFactory.True, transformDeclarationsRecursive(convertId(filterOptsByFeature(sd, x), x, defuse), env, defuse)))
                        }
                      } else {
                        List(transformDeclarationsRecursive(o, env, defuse))
                      }
                    } else {
                      List(o)
                    }
                  case d@Declaration(declSpecs, init) =>
                    if (isVariable(d)) {
                      if (declSpecs.exists(x => (x.entry.isInstanceOf[EnumSpecifier] || (x.entry.isInstanceOf[StructOrUnionSpecifier]) && x.feature.equivalentTo(FeatureExprFactory.True)))) {
                        List(transformDeclarationsRecursive(o, env, defuse))
                      } else {
                        // val features = getSingleFeatureSet(d, env).toList
                        val features = getNextVariableFeaturesCondition(d)
                        if (isExclusion(features)) {
                          features.map(x => Opt(FeatureExprFactory.True, Declaration(filterOptsByFeature(declSpecs, x), convertId(filterOptsByFeature(init, x), x, defuse))))
                        } else {
                          getFeatureCombinations(features).map(x => Opt(FeatureExprFactory.True, Declaration(filterOptsByFeature(declSpecs, x), convertId(filterOptsByFeature(init, x), x, defuse))))
                        }
                        //val features = getFeatureCombinations(getNextVariableFeaturesCondition(d))
                        //val newDecls = features.map(x => Opt(FeatureExprFactory.True, Declaration(filterOptsByFeature(declSpecs, x), convertId(filterOptsByFeature(init, x), x, defuse))))
                        //newDecls
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
                    if (getFeatureExpressions(cEls, env).isEmpty) {
                      List(Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(ft)), One(filterOptsByFeature(CompoundStatement(List(Opt(FeatureExprFactory.True, IfStatement(cThen, transformDeclarationsRecursive(then, env, defuse), transformDeclarationsRecursive(elif, env, defuse), transformDeclarationsRecursive(els, env, defuse))))), ft)), List(), Some(One(filterOptsByFeature(CompoundStatement(List(Opt(FeatureExprFactory.True, IfStatement(cEls, transformDeclarationsRecursive(then, env, defuse), transformDeclarationsRecursive(elif, env, defuse), transformDeclarationsRecursive(els, env, defuse))))), ft.not()))))))
                    } else {
                      List(Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(ft)), One(filterOptsByFeature(CompoundStatement(List(Opt(FeatureExprFactory.True, IfStatement(cThen, transformDeclarationsRecursive(then, env, defuse), transformDeclarationsRecursive(elif, env, defuse), transformDeclarationsRecursive(els, env, defuse))))), ft)), List(), Some(One(filterOptsByFeature(CompoundStatement(List(Opt(FeatureExprFactory.True, IfStatement(cEls, transformDeclarationsRecursive(then, env, defuse), transformDeclarationsRecursive(elif, env, defuse), transformDeclarationsRecursive(els, env, defuse))))), getFeatureExpressions(cEls, env).head))))))
                    }
                  case i@IfStatement(One(cond), One(then), elif, els) =>
                    if (isVariable(i)) {
                      var tmp = o.asInstanceOf[Opt[IfStatement]]
                      if (isVariable(cond)) {
                        val featureCombinations = getFeatureCombinations(filterFeatures(cond, env).toList)
                        val newIfStmt = IfStatement(One(featureToCExpr(featureCombinations.head)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, IfStatement(One(filterOptsByFeature(cond, featureCombinations.head)), filterOptsByFeature(One(then), featureCombinations.head), elif.map(j => transformDeclarationsRecursive(filterOptsByFeature(j, featureCombinations.head), env, defuse)), filterOptsByFeature(els, featureCombinations.head)))))), featureCombinations.tail.map(x => Opt(FeatureExprFactory.True, ElifStatement(One(featureToCExpr(x)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, IfStatement(One(filterOptsByFeature(cond, x)), filterOptsByFeature(One(then), x), elif.map(j => filterOptsByFeature(j, x)), filterOptsByFeature(els, x))))))))).toList, Some(One(CompoundStatement(List(Opt(FeatureExprFactory.True, IfStatement(One(filterOptsByFeature(cond, FeatureExprFactory.True)), filterOptsByFeature(One(then), FeatureExprFactory.True), elif.map(j => filterOptsByFeature(j, FeatureExprFactory.True)), filterOptsByFeature(els, FeatureExprFactory.True))))))))
                        tmp = tmp.copy(entry = newIfStmt)
                      }
                      if (isVariable(then)) {
                        tmp = tmp.copy(entry = tmp.entry.copy(thenBranch = One(transformDeclarationsRecursive(then, env, defuse))))
                      }
                      if (isVariable(els)) {
                        tmp = tmp.copy(entry = tmp.entry.copy(elseBranch = transformDeclarationsRecursive(els, env, defuse)))
                      }
                      if (isVariable(elif)) {
                        tmp = tmp.copy(entry = tmp.entry.copy(elifs = transformDeclarationsRecursive(elif, env, defuse)))
                      }
                      List(tmp)
                    } else {
                      List(o)
                    }
                  case i@IfStatement(One(cond), c: Choice[Statement], elif, els) =>
                    if (isVariable(i)) {
                      val newThen = choiceToIf(c)
                      var tmp = o.asInstanceOf[Opt[IfStatement]].copy(entry = i.copy(thenBranch = newThen))
                      if (isVariable(cond)) {
                        val featureCombinations = getFeatureCombinations(filterFeatures(cond, env).toList)
                        val newIfStmt = IfStatement(One(featureToCExpr(featureCombinations.head)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, IfStatement(One(filterOptsByFeature(cond, featureCombinations.head)), filterOptsByFeature(newThen, featureCombinations.head), elif.map(j => transformDeclarationsRecursive(filterOptsByFeature(j, featureCombinations.head), env, defuse)), filterOptsByFeature(els, featureCombinations.head)))))), featureCombinations.tail.map(x => Opt(FeatureExprFactory.True, ElifStatement(One(featureToCExpr(x)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, IfStatement(One(filterOptsByFeature(cond, x)), filterOptsByFeature(newThen, x), elif.map(j => filterOptsByFeature(j, x)), filterOptsByFeature(els, x))))))))).toList, Some(One(CompoundStatement(List(Opt(FeatureExprFactory.True, IfStatement(One(filterOptsByFeature(cond, FeatureExprFactory.True)), filterOptsByFeature(newThen, FeatureExprFactory.True), elif.map(j => filterOptsByFeature(j, FeatureExprFactory.True)), filterOptsByFeature(els, FeatureExprFactory.True))))))))
                        tmp = tmp.copy(entry = newIfStmt)
                      }
                      if (isVariable(els)) {
                        tmp = tmp.copy(entry = tmp.entry.copy(elseBranch = transformDeclarationsRecursive(els, env, defuse)))
                      }
                      if (isVariable(elif)) {
                        tmp = tmp.copy(entry = tmp.entry.copy(elifs = transformDeclarationsRecursive(elif, env, defuse)))
                      }
                      List(tmp)
                    } else {
                      List(o)
                    }
                  case e: ExprStatement =>
                    if (containsIdUsage(e)) {
                      val features = getIdUsageFeatureList(e)
                      val res = features.map(x => Opt(True, transformDeclarationsRecursive(exprStatementToIf(e, x), env, defuse)))
                      res
                    } else if (isVariable(e)) {
                      val features = getSingleFeatureSet(e, env)
                      features.map(x => Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(x)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, transformDeclarationsRecursive(filterOptsByFeature(e, x), env, defuse))))), List(), None)))
                      //List(transformDeclarationsRecursive(o, env, defuse))
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
    transformDeclarationsRecursive(t, env, defuse)
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

  def getNextFeatures(a: Any): List[FeatureExpr] = {
    def getNextFeatureHelp(a: Any): List[FeatureExpr] = {
      a match {
        case d@Opt(ft, entry) => List(ft)
        case l: List[_] => l.flatMap(getNextFeatures(_))
        case p: Product => p.productIterator.toList.flatMap(getNextFeatures(_))
        case _ => List()
      }
    }
    getNextFeatureHelp(a).toSet.toList
  }

  def getNextVariableFeatures(a: Any): List[FeatureExpr] = {
    def getNextFeatureHelp(a: Any): List[FeatureExpr] = {
      a match {
        case d@Opt(ft, entry) => if (!ft.equals(FeatureExprFactory.True)) List(ft) else List()
        case l: List[_] => l.flatMap(getNextFeatureHelp(_))
        case p: Product => p.productIterator.toList.flatMap(getNextFeatureHelp(_))
        case _ => List()
      }
    }
    getNextFeatureHelp(a).toSet.toList
  }

  def getNextVariableFeaturesCondition(a: Any): List[FeatureExpr] = {
    def getNextFeatureHelp(a: Any): List[FeatureExpr] = {
      a match {
        case d@Opt(ft, entry: InitDeclaratorI) => (if (!ft.equals(FeatureExprFactory.True)) List(ft) else List()) ++ entry.productIterator.toList.flatMap(getNextFeatureHelp(_))
        case d@Opt(ft, entry) => if (!ft.equals(FeatureExprFactory.True)) List(ft) else List()
        case l: List[_] => l.flatMap(getNextFeatureHelp(_))
        case p: Product => p.productIterator.toList.flatMap(getNextFeatureHelp(_))
        case _ => List()
      }
    }
    getNextFeatureHelp(a).toSet.toList
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

  def convertIdUsagesFromDefuse[T <: Product](t: T, feat: FeatureExpr): T = {
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

  def exprStatementToIf(e: ExprStatement, ft: FeatureExpr): IfStatement = {
    IfStatement(One(featureToCExpr(ft)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, convertIdUsagesFromDefuse(removeContraryOptsByFeature(e, ft), ft))))), List(), None)
  }

  def statementToIf(e: Statement, ft: FeatureExpr): IfStatement = {
    IfStatement(One(featureToCExpr(ft)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, convertIdUsagesFromDefuse(removeContraryOptsByFeature(e, ft), ft))))), List(), None)
  }

  def optStatementToIf(o: Opt[Statement]): Opt[IfStatement] = {
    Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(o.feature)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, convertIdUsagesFromDefuse(removeContraryOptsByFeature(o.entry, o.feature), o.feature))))), List(), None))
  }

  def choiceToIf(c: Choice[Statement]): One[Statement] = {
    c match {
      case Choice(ft, One(first: Statement), One(second: Statement)) =>
        One(CompoundStatement(List(Opt(FeatureExprFactory.True, statementToIf(first, ft)), Opt(FeatureExprFactory.True, statementToIf(second, ft.not())))))
      case _ =>
        println("ChoiceToIf not exhaustive: " + c)
        null
    }
  }

  /*def categorizeCaseStatements(ss: SwitchStatement) : List[Statement] = {
    def innerStatementsToList(lst: List[Opt[Statement]]) = List[List[Opt[Statement]]] {
      def innerStatementsToListRecursive(lst: List[Opt[Statement]], newList: Boolean = true) = List[Opt[_]] {
        if (lst.size > 0) {
          lst.head.entry match {
            case CaseStatement =>
              if (newList) {
                List(lst.head) ++ innerStatementsToListRecursive(lst.tail, false).asInstanceOf[List[Opt[_]]]
              } else {
                List()
              }
            case DefaultStatement =>
              if (newList) {
                List(lst.head) ++ innerStatementsToListRecursive(lst.tail, false).asInstanceOf[List[Opt[_]]]
              } else {
                List()
              }
            case st: Statement =>
              if (newList) {
                println("List head is: " + st)
                List()
              } else {
                List(lst.head) ++ innerStatementsToListRecursive(lst.tail, false).asInstanceOf[List[Opt[_]]]
              }
            case k =>
              if (newList) {
                println("List head is: " + k)
                List()
              } else {
                List(lst.head) ++ innerStatementsToListRecursive(lst.tail, false).asInstanceOf[List[Opt[_]]]
              }
          }
        } else {
          List()
        }
      }
    }
    ss.s match {
      case One(c: CompoundStatement) =>
        // c.innerStatements.
    }
  }*/
}