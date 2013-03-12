package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.featureexpr.sat._
import collection.mutable.ListBuffer
import java.util
import util.IdentityHashMap
import java.io.FileWriter
import de.fosd.typechef.conditional._
import collection.mutable
import de.fosd.typechef.parser.c.PlainParameterDeclaration
import de.fosd.typechef.parser.c.SwitchStatement
import de.fosd.typechef.parser.c.Enumerator
import de.fosd.typechef.parser.c.EnumSpecifier
import scala.Some
import de.fosd.typechef.parser.c.NAryExpr
import de.fosd.typechef.parser.c.DoStatement
import de.fosd.typechef.parser.c.PointerPostfixSuffix
import de.fosd.typechef.parser.c.VoidSpecifier
import de.fosd.typechef.parser.c.AssignExpr
import de.fosd.typechef.conditional.One
import de.fosd.typechef.parser.c.ForStatement
import de.fosd.typechef.parser.c.DeclParameterDeclList
import de.fosd.typechef.parser.c.SizeOfExprT
import de.fosd.typechef.parser.c.Id
import de.fosd.typechef.parser.c.Constant
import de.fosd.typechef.parser.c.DeclarationStatement
import de.fosd.typechef.parser.c.TypelessDeclaration
import de.fosd.typechef.parser.c.Pragma
import de.fosd.typechef.parser.c.ExprList
import de.fosd.typechef.parser.c.CompoundStatement
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.parser.c.CaseStatement
import de.fosd.typechef.parser.c.StructDeclarator
import de.fosd.typechef.parser.c.PostfixExpr
import de.fosd.typechef.parser.c.ReturnStatement
import de.fosd.typechef.parser.c.AtomicNamedDeclarator
import de.fosd.typechef.parser.c.StructOrUnionSpecifier
import de.fosd.typechef.parser.c.ExternSpecifier
import de.fosd.typechef.conditional.Choice
import de.fosd.typechef.parser.c.TranslationUnit
import de.fosd.typechef.parser.c.FunctionCall
import de.fosd.typechef.parser.c.IfStatement
import de.fosd.typechef.parser.c.NArySubExpr
import de.fosd.typechef.parser.c.WhileStatement
import de.fosd.typechef.parser.c.EmptyExternalDef
import de.fosd.typechef.parser.c.InitDeclaratorI
import de.fosd.typechef.parser.c.UnaryOpExpr
import de.fosd.typechef.parser.c.Declaration
import de.fosd.typechef.parser.c.LabelStatement
import de.fosd.typechef.parser.c.ExprStatement
import de.fosd.typechef.parser.c.DeclIdentifierList
import de.fosd.typechef.parser.c.EmptyStatement
import de.fosd.typechef.parser.c.StructDeclaration
import de.fosd.typechef.parser.c.IntSpecifier
import de.fosd.typechef.parser.c.GotoStatement
import de.fosd.typechef.parser.c.ElifStatement
import de.fosd.typechef.parser.c.FunctionDef
import de.fosd.typechef.parser.c.NestedFunctionDef
import scala.Tuple2

/**
 * strategies to rewrite ifdefs to ifs
 */

class IfdefToIf extends ASTNavigation with ConditionalNavigation {

  val CONFIGPREFIX = "v_"
  var counter = 0
  var defuse: IdentityHashMap[Id, List[Id]] = new IdentityHashMap()
  var IdMap: Map[FeatureExpr, Int] = Map()
  var fctMap: Map[Id, Map[FeatureExpr, String]] = Map()
  var jmpMap: Map[String, Map[FeatureExpr, String]] = Map()
  var replaceId: IdentityHashMap[Id, FeatureExpr] = new IdentityHashMap()
  var typeDefs: ListBuffer[Id] = ListBuffer()
  var alreadyReplaced: ListBuffer[Id] = ListBuffer()
  val toBeReplaced: util.IdentityHashMap[Product, Product] = new IdentityHashMap()
  var liftOptReplaceMap: Map[Opt[_], List[Opt[_]]] = Map()
  val idsToBeReplaced: IdentityHashMap[Id, List[FeatureExpr]] = new IdentityHashMap()
  val writeOptionsIntoFile = true

  // Variables for statistics

  // Features
  var noOfFeatures = 0
  var noOfTotalFeatures = 0
  var featureSet: Set[DefinedExternal] = Set()

  // Declarations
  var noOfOptionalDeclarations = 0
  var noOfDeclarations = 0
  var noOfDeclarationDuplications = 0
  var noOfDeclarationDuplicationsSpecifiers = 0
  var noOfDeclarationDuplicationsInits = 0

  // Functions
  var noOfFunctions = 0
  var noOfOptionalFunctions = 0
  var noOfFunctionDuplicationsSpecifiers = 0
  var noOfFunctionDuplicationsDeclarators = 0
  var noOfFunctionDuplicationsParameters = 0
  var noOfFunctionDuplications = 0

  // Statements
  var noOfStatements = 0
  var noOfStatementDuplications = 0
  var noOfStatementsVariable = 0

  // StructDeclarations
  var noOfStructDeclarations = 0
  var noOfStructDeclarationsRenamed = 0

  // Enumerators
  var noOfEnumerators = 0
  var noOfEnumeratorsVariable = 0

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

  def resetValues() = {
    // Features
    noOfFeatures = 0

    // Declarations
    noOfOptionalDeclarations = 0
    noOfDeclarations = 0
    noOfDeclarationDuplications = 0
    noOfDeclarationDuplicationsSpecifiers = 0
    noOfDeclarationDuplicationsInits = 0

    // Functions
    noOfFunctions = 0
    noOfOptionalFunctions = 0
    noOfFunctionDuplicationsSpecifiers = 0
    noOfFunctionDuplicationsDeclarators = 0
    noOfFunctionDuplicationsParameters = 0
    noOfFunctionDuplications = 0

    // Statements
    noOfStatements = 0
    noOfStatementDuplications = 0
    noOfStatementsVariable = 0
  }

  def transformAsts(asts: List[Tuple3[TranslationUnit, IdentityHashMap[Id, List[Id]], String]]): List[Tuple2[AST, String]] = {
    asts.map(x => (transformAst(x._1, x._2), x._3)._1)
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
      if (l.size < 1) {
        println()
      }
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

  def createCsvString(fileName: String): String = {
    val s = ","
    noOfFeatures + s + noOfDeclarations + s + noOfOptionalDeclarations + s + noOfDeclarationDuplications + s + noOfFunctions + s + noOfOptionalFunctions + s + noOfFunctionDuplications + s + noOfStatements + s + noOfStatementsVariable + s + noOfStatementDuplications
  }

  /*
  Creates an AST including an external int, a function, a struct with all features and an init function for that struct
   */
  def getOptionFile(ast: AST): TranslationUnit = {
    val features = filterFeatures(ast)
    val optionsAst = definedExternalToAst(features)
    optionsAst
  }

  def getTotalOptionFile(): TranslationUnit = {
    definedExternalToAst(featureSet)
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
    val features = getFeatureExpressions(a).flatMap(x => x.collectDistinctFeatures2).toSet
    if (!writeOptionsIntoFile) {
      featureSet = featureSet ++ features
    }
    noOfFeatures = features.size
    features
  }

  def createStatistics(a: Any, current: Tuple3[Tuple2[Int, Int], Tuple2[Int, Int], Tuple2[Int, Int]] = ((0, 0), (0, 0), (0, 0))): Tuple3[Tuple2[Int, Int], Tuple2[Int, Int], Tuple2[Int, Int]] = {
    def addTuples(tuples: List[Tuple3[Tuple2[Int, Int], Tuple2[Int, Int], Tuple2[Int, Int]]], currentTuple: Tuple3[Tuple2[Int, Int], Tuple2[Int, Int], Tuple2[Int, Int]] = ((0, 0), (0, 0), (0, 0))): Tuple3[Tuple2[Int, Int], Tuple2[Int, Int], Tuple2[Int, Int]] = {
      tuples.foldLeft(currentTuple)((first, second) => (((first._1._1 + second._1._1), (first._1._2 + second._1._2)), ((first._2._1 + second._2._1), (first._2._2 + second._2._2)), ((first._3._1 + second._3._1), (first._3._2 + second._3._2))))
    }
    a match {
      case o: Opt[_] => {
        o.entry match {
          case decl@Declaration(specs, init) =>
            if (o.feature.equivalentTo(FeatureExprFactory.True)) {
              addTuples(List(((0, 1), (0, 0), (0, 0)), createStatistics(decl)), current)
            } else {
              (((current._1._1 + 1), (current._1._2 + 1)), current._2, current._3)
            }
        }
      }
      case l: List[_] => l.map(x => createStatistics(x, ((0, 0), (0, 0), (0, 0)))).foldLeft(current)((first, second) => (((first._1._1 + second._1._1), (first._1._2 + second._1._2)), ((first._2._1 + second._2._1), (first._2._2 + second._2._2)), ((first._3._1 + second._3._1), (first._3._2 + second._3._2))))
      case p: Product => p.productIterator.toList.map(x => createStatistics(x, ((0, 0), (0, 0), (0, 0)))).foldLeft(current)((first, second) => (((first._1._1 + second._1._1), (first._1._2 + second._1._2)), ((first._2._1 + second._2._1), (first._2._2 + second._2._2)), ((first._3._1 + second._3._1), (first._3._2 + second._3._2))))
      case _ => current
    }
  }

  /*
  Retrieves a list of tuples out of a choice node. Also takes choices inside choices into account
   */
  private def choiceToTuple[T <: Product](choice: Choice[T]): List[Tuple2[FeatureExpr, T]] = {
    def addOne[T <: Product](entry: One[T], ft: FeatureExpr): List[Tuple2[FeatureExpr, T]] = {
      entry match {
        case One(null) =>
          List()
        case One(a) =>
          List(Tuple2(ft, a))
      }
    }
    choice match {
      case Choice(ft, first@One(_), second@One(_)) =>
        addOne(first, ft) ++ addOne(second, ft.not())
      case Choice(ft, first@Choice(_, _, _), second@Choice(_, _, _)) =>
        choiceToTuple(first) ++ choiceToTuple(second)
      case Choice(ft, first@One(a), second@Choice(_, _, _)) =>
        addOne(first, ft) ++ choiceToTuple(second)
      case Choice(ft, first@Choice(_, _, _), second@One(_)) =>
        choiceToTuple(first) ++ addOne(second, ft.not())
    }
  }

  /*
 Filteres a given product for feature expressions which are not True and returns a set of all different feature expressions
  */
  def getSingleFeatureSet(a: Any): List[FeatureExpr] = {
    def getFeatureExpressions(a: Any): List[FeatureExpr] = {
      a match {
        case o: Opt[_] => (if (o.feature == FeatureExprFactory.True) List() else List(o.feature)) ++ o.productIterator.toList.flatMap(getFeatureExpressions(_))
        case l: List[_] => l.flatMap(getFeatureExpressions(_))
        case p: Product => p.productIterator.toList.flatMap(getFeatureExpressions(_))
        case t: FeatureExpr => if (t == FeatureExprFactory.True) List() else List(t)
        case _ => List()
      }
    }
    val result = getFeatureExpressions(a).distinct
    result
  }

  def getFeatureExpressions(a: Any): List[FeatureExpr] = {
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

  def filterInvariableOpts(a: Any): List[Opt[_]] = {
    a match {
      case o: Opt[_] => if (isVariable(o)) List(o) else List() ++ o.productIterator.toList.flatMap(filterInvariableOpts(_))
      case l: List[_] => l.flatMap(filterInvariableOpts(_))
      case p: Product => p.productIterator.toList.flatMap(filterInvariableOpts(_))
      case _ => List()
    }
  }

  /*
  This method fills the IdMap which is used to map a feature expression to a number.
   */
  def fillIdMap(a: Any) = {
    if (IdMap.size == 0) {
      IdMap += (FeatureExprFactory.True -> IdMap.size)
    }
    getSingleFeatureSet(a).foreach(x => if (!IdMap.contains(x)) {
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

  def filterVariableOpts(a: Any): List[Opt[_]] = {
    a match {
      case o: Opt[_] => if (o.feature != FeatureExprFactory.True) List(o) else List() ++ o.productIterator.toList.flatMap(filterVariableOpts(_))
      case l: List[_] => l.flatMap(filterVariableOpts(_))
      case p: Product => p.productIterator.toList.flatMap(filterVariableOpts(_))
      case _ => List()
    }
  }

  def filterVariableFunctionDef(a: Any): List[Opt[_]] = {
    a match {
      case o: Opt[_] => if (o.feature != FeatureExprFactory.True && o.entry.isInstanceOf[FunctionDef]) List(o) else List() ++ o.productIterator.toList.flatMap(filterVariableFunctionDef(_))
      case l: List[_] => l.flatMap(filterVariableFunctionDef(_))
      case p: Product => p.productIterator.toList.flatMap(filterVariableFunctionDef(_))
      case _ => List()
    }
  }

  def filterVariableDeclarations(a: Any): List[Opt[Declaration]] = {
    a match {
      case d: Opt[_] => if (d.feature != FeatureExprFactory.True && d.entry.isInstanceOf[Declaration]) List(d.asInstanceOf[Opt[Declaration]]) else List() ++ d.productIterator.toList.flatMap(filterVariableDeclarations(_))
      case l: List[_] => l.flatMap(filterVariableDeclarations(_))
      case p: Product => p.productIterator.toList.flatMap(filterVariableDeclarations(_))
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
          if (o.feature.equivalentTo(FeatureExprFactory.True)) {
            List(o)
          } else if (feat.&(o.feature).isContradiction()) {
            List()
          } else if (o.feature.equivalentTo(feat)) {
            List(o.copy(feature = FeatureExprFactory.True))
          } else if (feat.implies(o.feature).isTautology()) {
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

  def convertDeclaration(dcl: Opt[Declaration]): List[Opt[Declaration]] = {
    if (dcl.feature.equivalentTo(FeatureExprFactory.True)) {
      List()
    } else {
      val tmp = removeContraryOptsByFeature(Opt(FeatureExprFactory.True, Declaration(dcl.entry.declSpecs, dcl.entry.init.map(x => x match {
        case Opt(ft, i: InitDeclaratorI) => convertId(x, ft)
        case k => k
      }))), dcl.feature)
      println(PrettyPrinter.print(tmp.entry) + "\n\n")
      val tmp_ft = computeNextRelevantFeatures(tmp.entry)
      if (!tmp_ft.isEmpty) {
        val i = 0
      }
      List()
    }
  }


  def convertSingleDeclaration(dcl: Opt[Declaration]): List[Opt[Declaration]] = {
    val decl = replaceFeatureByTrue(dcl, dcl.feature)
    decl.entry match {
      case d@Declaration(declSpecs, init) =>
        noOfDeclarations = noOfDeclarations + 1
        noOfOptionalDeclarations = noOfOptionalDeclarations + 1
        if (isVariable(decl)) {
          if (isVariable(d.declSpecs)) {
            noOfDeclarationDuplicationsSpecifiers = noOfDeclarationDuplicationsSpecifiers + 1
          }
          if (isVariable(d.init)) {
            noOfDeclarationDuplicationsInits = noOfDeclarationDuplicationsInits + 1
          }
          val featurecombinations = getFeatureExpressions(decl)
          val newDecls: List[Opt[Declaration]] = featurecombinations.map(x =>
            filterOptsByFeature(decl.copy(entry = d.copy(declSpecs = declSpecs.map(x => x match {
              case o@Opt(_, StructOrUnionSpecifier(_, Some(i: Id), _)) => convertId(x, dcl.feature)
              case Opt(_, e: EnumSpecifier) => convertAllIds(x, dcl.feature)
              case z => z
            }), init = init.map(y => y match {
              case Opt(_, i: InitDeclaratorI) => convertId(y, x)
              case z => z
            }))), x)
          )
          if (!newDecls.isEmpty) {
            noOfDeclarationDuplications = noOfDeclarationDuplications + newDecls.size - 1
          }
          //newDecls.foreach(x => println(PrettyPrinter.print(x.entry) + "\n\n"))
          newDecls
        } else {
          val tempDecl = decl.copy(entry = d.copy(declSpecs = declSpecs.map(x => x match {
            case o@Opt(_, StructOrUnionSpecifier(_, Some(i: Id), _)) => convertId(x, dcl.feature)
            case Opt(_, e: EnumSpecifier) => convertAllIds(x, dcl.feature)
            case z => z
          }), init = init.map(x => x match {
            case Opt(_, i: InitDeclaratorI) => convertId(x, dcl.feature)
            case z => z
          })))
          List(tempDecl)
        }
      case _ => List(replaceFeatureByTrue(decl, dcl.feature))
    }
  }

  def convertId[T <: Product](t: T, ft: FeatureExpr): T = {
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

  def convertAllIds[T <: Product](t: T, ft: FeatureExpr): T = {
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
        l.flatMap(o => {
          if (feat.&(o.feature).isContradiction()) {
            List()
          } else if (feat.equivalentTo(o.feature) || feat.implies(o.feature).isTautology) {
            List(o.copy(feature = FeatureExprFactory.True))
          } else {
            List()
          }
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
          if (o.feature.equals(FeatureExprFactory.True)) {
            List(o)
          } else if (feat.&(o.feature).isContradiction()) {
            List()
          } else if (o.feature.equivalentTo(feat) || feat.implies(o.feature).isTautology) {
            List(o.copy(feature = FeatureExprFactory.True))
          } else {
            // TODO: Reduktion vom booleschen Ausdruck: #if (A) #if (B) int #else long #endif foo() {} #endif
            List(o)
          })
    })
    r(t) match {
      case None => t
      case k => k.get.asInstanceOf[T]
    }
  }

  def replaceOptAndId[T <: Product](t: T, feat: FeatureExpr): T = {
    val r = manytd(rule {
      case l: List[Opt[_]] =>
        l.flatMap(o =>
          if (o.feature.equals(FeatureExprFactory.True)) {
            List(o)
          } else if (feat.&(o.feature).isContradiction()) {
            List()
          } else if (o.feature.equivalentTo(feat) || feat.implies(o.feature).isTautology) {
            List(o.copy(feature = FeatureExprFactory.True))
          } else {
            List(o)
          })
      case i: Id =>
        if (idsToBeReplaced.containsKey(i)) {
          // Increase number of expanded statements
          if (!IdMap.contains(feat)) {
            IdMap += (feat -> IdMap.size)
          }
          val test = idsToBeReplaced.get(i).find(x => feat.implies(x).isTautology)
          test match {
            case None =>
              // TODO: this should not happen?
              Id("_" + IdMap.get(feat).get + "_" + i.name)
            case Some(x: FeatureExpr) =>
              Id("_" + IdMap.get(x).get + "_" + i.name)
            case k =>
              Id("")
          }
        } else {
          i
        }
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

  def transformAst[T <: Product](t: T, decluse: IdentityHashMap[Id, List[Id]], fileName: String = ""): Tuple2[T, String] = {
    fillIdMap(t)
    defuse = decluse
    val result = transformDeclarationsRecursive(t)
    val features = filterFeatures(t)
    val csvNumbers = createCsvString(fileName)
    resetValues()
    if (writeOptionsIntoFile) {
      (TranslationUnit(definedExternalToAst(features).defs ++ result.asInstanceOf[TranslationUnit].defs).asInstanceOf[T], csvNumbers)
    } else {
      (result, csvNumbers)
    }
  }

  var noOfStmts = 0
  var noOfOptStmts = 0
  var noOfFuncts = 0
  var noOfOptFuncts = 0
  var noOfDecls = 0
  var noOfOptDecls = 0

  def generateStatistics[T <: Product](t: T, ft: FeatureExpr = FeatureExprFactory.True) = {
    val r = alltd(query {
      case o@Opt(feat, entry) =>
        if (feat.equivalentTo(FeatureExprFactory.True) || feat.equivalentTo(ft)) {
          /* A node without new context */
          entry match {
            case declStmt@DeclarationStatement(decl: Declaration) =>
              noOfDecls = noOfDecls + 1
            case decl: Declaration =>
              noOfDecls = noOfDecls + 1
            case e: Enumerator =>
              noOfDecls = noOfDecls + 1
            case sd: StructDeclaration =>
              noOfDecls = noOfDecls + 1

            case fd: FunctionDef =>
              noOfFuncts = noOfFuncts + 1
            case nfd: NestedFunctionDef =>
              noOfFuncts = noOfFuncts + 1

            case cs: CompoundStatement =>
            case s: Statement =>
              noOfStmts = noOfStmts + 1
          }
        } else {
          /* A new optional node */

        }
    })
  }

  def transformDeclarationsRecursive[T <: Product](t: T, currentContext: FeatureExpr = FeatureExprFactory.True): T = {
    val r = alltd(rule {
      case l: List[Opt[_]] =>
        l.flatMap(x => x match {
          case o@Opt(ft: FeatureExpr, entry) =>

            /*
           Handle opt nodes which occur under a certain condition
            */
            if (ft != FeatureExprFactory.True) {
              entry match {
                case i@IfStatement(_, _, _, _) =>
                  noOfStatements = noOfStatements + 1
                  handleIfStatements(o, ft)
                case r: ReturnStatement =>
                  noOfStatements = noOfStatements + 1
                  noOfStatementsVariable = noOfStatementsVariable + 1
                  if (containsIdUsage(r)) {
                    //TODO: IdUsage
                    List(Opt(FeatureExprFactory.True, transformDeclarationsRecursive(removeContraryOptsByFeature(statementToIf(r, ft), ft))))
                  } else {
                    List(Opt(FeatureExprFactory.True, transformDeclarationsRecursive(removeContraryOptsByFeature(statementToIf(r, ft), ft))))
                  }

                case w: WhileStatement =>
                  noOfStatements = noOfStatements + 1
                  /*val result = List(Opt(FeatureExprFactory.True, transformDeclarationsRecursive(removeContraryOptsByFeature(statementToIf(w, ft), ft), env, defuse)))
                  result*/
                  handleWhileStatements(o.asInstanceOf[Opt[Statement]])
                case s: SwitchStatement =>
                  noOfStatements = noOfStatements + 1
                  noOfStatementsVariable = noOfStatementsVariable + 1
                  val result = List(Opt(FeatureExprFactory.True, transformDeclarationsRecursive(removeContraryOptsByFeature(statementToIf(s, ft), ft))))
                  result
                case d: DoStatement =>
                  noOfStatements = noOfStatements + 1
                  noOfStatementsVariable = noOfStatementsVariable + 1
                  // val result = List(Opt(FeatureExprFactory.True, transformDeclarationsRecursive(removeContraryOptsByFeature(statementToIf(d, ft), ft))))
                  val result = handleDoStatements(o.asInstanceOf[Opt[Statement]])
                  result
                case g: GotoStatement =>
                  noOfStatements = noOfStatements + 1
                  noOfStatementsVariable = noOfStatementsVariable + 1
                  val result = List(Opt(FeatureExprFactory.True, transformDeclarationsRecursive(removeContraryOptsByFeature(statementToIf(g, ft), ft))))
                  result
                case f: ForStatement =>
                  noOfStatements = noOfStatements + 1
                  noOfStatementsVariable = noOfStatementsVariable + 1
                  handleForStatements(o.asInstanceOf[Opt[Statement]])
                case elif@ElifStatement(One(expr: Expr), then) =>
                  noOfStatements = noOfStatements + 1
                  noOfStatementsVariable = noOfStatementsVariable + 1
                  List(Opt(FeatureExprFactory.True, transformDeclarationsRecursive(removeContraryOptsByFeature(ElifStatement(One(NAryExpr(featureToCExpr(ft), List(Opt(FeatureExprFactory.True, NArySubExpr("&&", expr))))), then), ft))))

                case e: ExprStatement =>
                  noOfStatements = noOfStatements + 1
                  noOfStatementsVariable = noOfStatementsVariable + 1
                  val ifStmt = IfStatement(One(featureToCExpr(o.feature)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, filterOptsByFeature(e, o.feature))))), List(), None)
                  List(Opt(FeatureExprFactory.True, transformDeclarationsRecursive(ifStmt)))
                case label: LabelStatement =>
                  noOfStatements = noOfStatements + 1
                  noOfStatementsVariable = noOfStatementsVariable + 1
                  if (containsIdUsage(label)) {
                    List(Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(ft)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, convertIdUsagesFromDefuse(label, ft))))), List(), None)))
                  } else {
                    List(Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(ft)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, label)))), List(), None)))
                  }


                case declStmt@DeclarationStatement(decl: Declaration) =>
                  noOfDeclarations = noOfDeclarations + 1
                  noOfOptionalDeclarations = noOfOptionalDeclarations + 1
                  val newDecl = Opt(FeatureExprFactory.True, DeclarationStatement(replaceFeatureByTrue(convertId(decl, o.feature), o.feature)))
                  List(newDecl)

                case decl: Declaration =>
                  //println(PrettyPrinter.print(decl))
                  val newDecls = convertSingleDeclaration(o.asInstanceOf[Opt[Declaration]])
                  // convertDeclaration(o.asInstanceOf[Opt[Declaration]])
                  newDecls

                case typeless: TypelessDeclaration =>
                  // TODO: Umwandlung
                  List(o)


                case fd: FunctionDef =>
                  noOfFunctions = noOfFunctions + 1
                  handleFunctions(o)

                case nfd: NestedFunctionDef =>
                  noOfFunctions = noOfFunctions + 1
                  handleFunctions(o)

                case e: Enumerator =>
                  noOfOptionalDeclarations = noOfOptionalDeclarations + 1
                  noOfDeclarations = noOfDeclarations + 1
                  noOfEnumerators = noOfEnumerators + 1
                  noOfEnumeratorsVariable = noOfEnumeratorsVariable + 1
                  val result = List(transformDeclarationsRecursive(convertId(replaceFeatureByTrue(o, o.feature), o.feature)))
                  result
                case sd: StructDeclaration =>
                  noOfStructDeclarations = noOfStructDeclarations + 1
                  noOfStructDeclarationsRenamed = noOfStructDeclarationsRenamed + 1
                  List(convertAllIds(replaceFeatureByTrue(o, o.feature), o.feature))

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
                case cs: CompoundStatement =>
                  List(transformDeclarationsRecursive(o))
                case k =>
                  println("Missing Opt: " + o + "\nFrom: " + k.asInstanceOf[AST].getPositionFrom + "\n")
                  //println("Missing Opt: " + o + "\nFrom: " + PrettyPrinter.print(k.asInstanceOf[AST]) + "\nParent = " + PrettyPrinter.print(env.parent(o).asInstanceOf[AST]))
                  List(o)
              }
            } else {

              /*
             Handle opt nodes which occur under condition true
              */
              entry match {
                case cmpStmt: CompoundStatement =>
                  List(Opt(FeatureExprFactory.True, transformDeclarationsRecursive(cmpStmt)))
                case f: ForStatement =>
                  noOfStatements = noOfStatements + 1
                  List(o)
                case d: DoStatement =>
                  noOfStatements = noOfStatements + 1
                  List(o)
                case r: ReturnStatement =>
                  noOfStatements = noOfStatements + 1
                  List(o)
                case g: GotoStatement =>
                  noOfStatements = noOfStatements + 1
                  List(o)
                case l: LabelStatement =>
                  noOfStatements = noOfStatements + 1
                  List(o)
                case e: ExprStatement =>
                  noOfStatements = noOfStatements + 1
                  if (containsIdUsage(e)) {
                    val features2 = computeIdUsageFeatures(e)
                    val features = computeIdUsageFeatures(e, currentContext).filterNot(x => x.equivalentTo(FeatureExprFactory.False))
                    if (!features.isEmpty) {
                      noOfStatementDuplications = noOfStatementDuplications - 1 + features.size
                    } else {

                    }
                    val res = features.map(x => Opt(True, transformDeclarationsRecursive(exprStatementToIf(convertIdUsagesFromDefuse(e, x), x))))
                    res

                  } else if (isVariable(e)) {
                    val features = getSingleFeatureSet(e)
                    features.map(x => Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(x)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, transformDeclarationsRecursive(filterOptsByFeature(e, x)))))), List(), None)))
                    //List(transformDeclarationsRecursive(o, env, defuse))
                  } else {
                    List(o)
                  }
                case w@WhileStatement(expr: Expr, s: Conditional[_]) =>
                  noOfStatements = noOfStatements + 1
                  val result = handleWhileStatements(o.asInstanceOf[Opt[Statement]])
                  result
                case declStmt@DeclarationStatement(decl: Declaration) =>
                  noOfDeclarations = noOfDeclarations + 1
                  if (isVariable(decl)) {
                    val features = computeNextRelevantFeatures(decl)
                    features.map(x => convertId(removeContraryOptsByFeature(o, x), x))
                  } else {
                    List(o)
                  }
                case ss: SwitchStatement =>
                  noOfStatements = noOfStatements + 1
                  if (isVariable(ss)) {
                    val features = getFeatureCombinations(getFeatureExpressions(ss).flatMap(x => x.collectDistinctFeatures2).toSet.toList)
                    if (!features.isEmpty) {
                      noOfStatementsVariable = noOfStatementDuplications - 1 + features.size
                    }
                    features.map(x => {
                      optStatementToIf(Opt(x, transformDeclarationsRecursive(removeContraryOptsByFeature(replaceFeatureByTrue(o, o.feature), x)).entry).asInstanceOf[Opt[Statement]])
                    })
                  } else {
                    List(transformDeclarationsRecursive(o))
                  }
                case i@IfStatement(_, _, _, _) =>
                  noOfStatements = noOfStatements + 1
                  handleIfStatements(o, ft)
                case elif@ElifStatement(One(cond), then) =>
                  noOfStatements = noOfStatements + 1
                  val feat = computeNextRelevantFeatures(cond)
                  if (!feat.isEmpty) {
                    noOfStatementDuplications = noOfStatementDuplications - 1 + feat.size
                    feat.map(x => transformDeclarationsRecursive(replaceOptAndId(Opt(FeatureExprFactory.True, ElifStatement(One(NAryExpr(featureToCExpr(x), List(Opt(FeatureExprFactory.True, NArySubExpr("&&", cond))))), then)), x)))
                  } else {
                    List(transformDeclarationsRecursive(o))
                  }
                /*if (containsIdUsage(cond)) {
                  //val feat = computeNextRelevantFeatures(i)
                  val feat = computeIdUsageFeatures(cond)
                  if (!feat.isEmpty) {
                     noOfStatementDuplications = noOfStatementDuplications - 1 + feat.size
                  }
                  feat.map(x => transformDeclarationsRecursive(removeContraryOptsByFeature(Opt(FeatureExprFactory.True, ElifStatement(One(NAryExpr(featureToCExpr(x), List(Opt(FeatureExprFactory.True, NArySubExpr("&&", convertIdUsagesFromDefuse(cond, x)))))), then)), x)))
                } else {
                  List(transformDeclarationsRecursive(o))
                }*/
                case elif@ElifStatement(c@Choice(ft, thenBranch, elseBranch), then) =>
                  noOfStatements = noOfStatements + 1
                  val choices = choiceToTuple(c)
                  if (!choices.isEmpty) {
                    noOfStatementDuplications = noOfStatementDuplications - 1 + choices.size
                  }
                  choices.map(x => {
                    if (containsIdUsage(thenBranch)) {
                      transformDeclarationsRecursive(removeContraryOptsByFeature(Opt(FeatureExprFactory.True, ElifStatement(One(NAryExpr(featureToCExpr(x._1), List(Opt(FeatureExprFactory.True, NArySubExpr("&&", convertIdUsagesFromDefuse(x._2, x._1)))))), then)), x._1))
                    } else {
                      transformDeclarationsRecursive(removeContraryOptsByFeature(Opt(FeatureExprFactory.True, ElifStatement(One(NAryExpr(featureToCExpr(x._1), List(Opt(FeatureExprFactory.True, NArySubExpr("&&", x._2))))), then)), x._1))
                    }
                  })

                case td: TypelessDeclaration =>
                  List(o)

                case fd: FunctionDef =>
                  noOfFunctions = noOfFunctions + 1
                  handleFunctions(o)

                case nfd: NestedFunctionDef =>
                  noOfFunctions = noOfFunctions + 1
                  handleFunctions(o)

                case e@Enumerator(id, Some(soe: SizeOfExprT)) =>
                  noOfDeclarations = noOfDeclarations + 1
                  noOfEnumerators = noOfEnumerators + 1
                  if (isVariable(e)) {
                    noOfOptionalDeclarations = noOfOptionalDeclarations + 1
                    noOfEnumeratorsVariable = noOfEnumeratorsVariable + 1
                    val featureCombinations = computeNextRelevantFeatures(e)
                    val newEnums = featureCombinations.map(x => Opt(FeatureExprFactory.True, convertId(filterOptsByFeature(e, x), x))).toList
                    if (featureCombinations.size > 1) {
                      noOfDeclarationDuplications = noOfDeclarationDuplications - 1 + featureCombinations.size
                    }
                    newEnums
                  } else {
                    List(o)
                  }
                case e@Enumerator(id, Some(nae: NAryExpr)) =>
                  noOfDeclarations = noOfDeclarations + 1
                  if (isVariable(e)) {
                    val featureSet = getSingleFeatureSet(e)
                    if (!featureSet.isEmpty) {
                      noOfDeclarationDuplications = noOfDeclarationDuplications + featureSet.size - 1
                    }
                    val newEnumerators = featureSet.map(x => Opt(FeatureExprFactory.True, convertId(filterOptsByFeature(e, x), x))).toList
                    newEnumerators
                  } else {
                    List(o)
                  }
                case sd: StructDeclaration =>
                  noOfDeclarations = noOfDeclarations + 1
                  noOfStructDeclarations = noOfStructDeclarations + 1
                  if (isVariable(sd)) {
                    val features = getNextVariableFeatures(sd)
                    if (!features.isEmpty) {
                      noOfDeclarationDuplications = noOfDeclarationDuplications + features.size - 1
                      noOfStructDeclarationsRenamed = noOfStructDeclarationsRenamed + 1
                      if (sd.qualifierList.exists(x => (x.entry.isInstanceOf[StructOrUnionSpecifier]))) {
                        features.map(x => Opt(FeatureExprFactory.True, transformDeclarationsRecursive(filterOptsByFeature(sd, x))))
                      } else {
                        features.map(x => Opt(FeatureExprFactory.True, transformDeclarationsRecursive(convertId(filterOptsByFeature(sd, x), x))))
                      }
                    } else {
                      List(transformDeclarationsRecursive(o))
                    }
                  } else {
                    List(o)
                  }
                case d@Declaration(declSpecs, init) =>
                  noOfDeclarations = noOfDeclarations + 1
                  /*if (!d.init.isEmpty && d.init.head.entry.getId.name.equals("opt_flags")) {
                    println(computeNextRelevantFeatures(d.init).size)
                    println(ConditionalLib.explodeOptList(d.init.head.entry.asInstanceOf[InitDeclaratorI].i.get.expr.asInstanceOf[LcurlyInitializer].inits))
                    println("debug")
                  }*/
                  if (isVariable(d)) {
                    if (declSpecs.exists(x => (x.entry.isInstanceOf[EnumSpecifier] || (x.entry.isInstanceOf[StructOrUnionSpecifier]) && x.feature.equivalentTo(FeatureExprFactory.True)))) {
                      List(transformDeclarationsRecursive(o))
                    } else {
                      // val features = getSingleFeatureSet(d, env).toList

                      if (isExclusion(getNextVariableFeaturesCondition(d))) {
                        val features = getNextVariableFeaturesCondition(d).map(x => Opt(FeatureExprFactory.True, Declaration(filterOptsByFeature(declSpecs, x), convertId(filterOptsByFeature(init, x), x))))
                        if (!features.isEmpty) {
                          noOfDeclarationDuplications = noOfDeclarationDuplications - 1 + features.size
                        }
                        features
                      } else {
                        val features = getFeatureCombinations(getNextVariableFeaturesCondition(d)).map(x => Opt(FeatureExprFactory.True, Declaration(filterOptsByFeature(declSpecs, x), convertId(filterOptsByFeature(init, x), x))))
                        if (!features.isEmpty) {
                          noOfDeclarationDuplications = noOfDeclarationDuplications - 1 + features.size
                        }
                        features
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


                case k: Product =>
                  List(transformDeclarationsRecursive(o))
                case r =>
                  List(o)
              }
            }
          case k =>
            List(transformDeclarationsRecursive(k))
        })
    })
    r(t) match {
      case None => t
      case k =>
        k.get.asInstanceOf[T]
    }
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

  def getIdUsageFeatureList(a: Any): List[List[FeatureExpr]] = {
    val ids = filterASTElems[Id](a)
    val features = ids.filter(x => idsToBeReplaced.containsKey(x)).map(x => idsToBeReplaced.get(x))
    features.distinct
  }

  def computeIdUsageFeatures(a: Any, currentContext: FeatureExpr = FeatureExprFactory.True): List[FeatureExpr] = {
    if (currentContext.equivalentTo(FeatureExprFactory.True)) {
      val res = getIdUsageFeatureList(a, currentContext).foldLeft(List(FeatureExprFactory.True))((first, second) => first.flatMap(x => second.diff(first).map(y => y.and(x))))
      res
    } else {
      val res = getIdUsageFeatureList(a, currentContext).foldLeft(List(FeatureExprFactory.True))((first, second) => first.flatMap(x => second.diff(first).map(y => y.and(x)))).flatMap(x => if (currentContext.implies(x).isTautology()) List(x) else List())
      res
    }
  }

  def getVariableIdAndFeature(a: Any): List[Tuple2[Id, FeatureExpr]] = {
    List()
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

  def computeDebug2(a: Any, currentContext: FeatureExpr = FeatureExprFactory.True): List[FeatureExpr] = {
    // TODO: Kommentare
    val featureList = getNextVariableFeaturesCondition(a).filterNot(x => x.equivalentTo(currentContext)) ++ List(FeatureExprFactory.False)
    val featureBuffer: ListBuffer[List[FeatureExpr]] = ListBuffer()
    val currentFeatures: mutable.HashSet[FeatureExpr] = new mutable.HashSet
    featureList.foldLeft(List(): List[FeatureExpr])((first, second) => {
      // Reached end of list
      if (second.equivalentTo(FeatureExprFactory.False)) {
        if (!first.isEmpty) {
          // featureBuffer += getFeatureCombinations(first.head.collectDistinctFeatures2.diff(currentContext.collectDistinctFeatures2).toList)
          if (!currentFeatures.contains(first.head)) {
            currentFeatures.add(first.head)
            currentFeatures.add(first.head.or(currentContext.not).not)
            featureBuffer += List(first.head, first.head.or(currentContext.not).not)
          }
        }
        List()
      } else if (first.isEmpty) {
        second :: first
      } else {
        var result = true
        val mexResult = first.foldLeft(second)((a, b) => {
          if (b.equivalentTo(FeatureExprFactory.False)) {
            b
          } else if (a.mex(b).isTautology()) {
            b
          } else {
            result = false
            b
          }
        })
        val orResult = first.foldLeft(second)((a, b) => a.or(b))
        if (result && currentContext.implies(orResult).isTautology()) {
          featureBuffer += (second :: first)
          List()
        } else if (result) {
          second :: first
        } else {
          //featureBuffer += getFeatureCombinations(first.head.collectDistinctFeatures2.diff(currentContext.collectDistinctFeatures2).toList)
          if (!currentFeatures.contains(first.head)) {
            currentFeatures.add(first.head)
            currentFeatures.add(first.head.or(currentContext.not).not)
            featureBuffer += List(first.head, first.head.or(currentContext.not).not)
          }

          if (second.equivalentTo(FeatureExprFactory.False)) {
            //featureBuffer += getFeatureCombinations(second.collectDistinctFeatures2.diff(currentContext.collectDistinctFeatures2).toList)
            if (!currentFeatures.contains(second)) {
              currentFeatures += second
              currentFeatures += second.or(currentContext.not).not
              featureBuffer += List(second, second.or(currentContext.not).not)
            }
          }
          List(second)
        }
      }
    })
    //featureBuffer.foreach(x => println(x))
    //println()
    currentFeatures.clear()
    if (featureBuffer.isEmpty) {
      List()
    } else if (featureBuffer.size == 1) {
      val result = featureBuffer.toList.head
      result
    } else {
      val result = featureBuffer.toList.tail.foldLeft(featureBuffer.toList.head)((first, second) => {
        first.flatMap(x => second.map(y => y.and(x)))
      })
      result
    }
  }

  def computeDebug(a: Any, currentContext: FeatureExpr = FeatureExprFactory.True): List[FeatureExpr] = {
    val featureList = getNextVariableFeaturesCondition(a).filterNot(x => x.equivalentTo(currentContext))
    val featureBuffer = ListBuffer(featureList.head)
    featureList.tail.foldLeft(featureList.head)((first, second) => {
      if (first.mex(second).isTautology() && first.and(second).implies(currentContext).isTautology()) {
        featureBuffer += second
        FeatureExprFactory.True
      } else if (first.mex(second).isTautology()) {
        featureBuffer += second
        first.and(second)
      } else {
        featureBuffer ++ getFeatureCombinations(second.collectDistinctFeatures2.diff(currentContext.collectDistinctFeatures2).toList)
        FeatureExprFactory.True
      }
    })
    featureBuffer.toList
  }

  def computeNextRelevantFeatures(a: Any, currentContext: FeatureExpr = FeatureExprFactory.True): List[FeatureExpr] = {
    def computationHelper(a: Any, currentContext: FeatureExpr = FeatureExprFactory.True, expectAtLeastOneResult: Boolean = false): List[FeatureExpr] = {
      val featureList = getNextVariableFeaturesCondition(a).filterNot(x => x.equivalentTo(currentContext)) ++ List(FeatureExprFactory.False)
      val featureList2 = getNextVariableFeaturesCondition2(a)
      if (!featureList2.isEmpty || featureList.size > 1) {
        val i = 0
      }
      if (expectAtLeastOneResult && featureList.size == 1) {
        List(FeatureExprFactory.True)
      } else {
        val featureBuffer: ListBuffer[List[FeatureExpr]] = ListBuffer()
        val currentFeatures: mutable.HashSet[FeatureExpr] = new mutable.HashSet
        featureList.foldLeft(List(): List[FeatureExpr])((first, second) => {
          // Reached end of list
          if (second.equivalentTo(FeatureExprFactory.False)) {
            if (!first.isEmpty) {
              // featureBuffer += getFeatureCombinations(first.head.collectDistinctFeatures2.diff(currentContext.collectDistinctFeatures2).toList)
              if (!currentFeatures.contains(first.head)) {
                currentFeatures.add(first.head)
                currentFeatures.add(first.head.or(currentContext.not).not)
                featureBuffer += List(first.head, first.head.or(currentContext.not).not)
              }
            }
            List()
          } else if (first.isEmpty) {
            second :: first
          } else {
            var result = true
            val mexResult = first.foldLeft(second)((a, b) => {
              if (b.equivalentTo(FeatureExprFactory.False)) {
                b
              } else if (a.mex(b).isTautology()) {
                b
              } else {
                result = false
                b
              }
            })
            val orResult = first.foldLeft(second)((a, b) => a.or(b))
            if (result && currentContext.implies(orResult).isTautology()) {
              featureBuffer += (second :: first)
              List()
            } else if (result) {
              second :: first
            } else {
              //featureBuffer += getFeatureCombinations(first.head.collectDistinctFeatures2.diff(currentContext.collectDistinctFeatures2).toList)
              if (!currentFeatures.contains(first.head)) {
                currentFeatures.add(first.head)
                currentFeatures.add(first.head.or(currentContext.not).not)
                featureBuffer += List(first.head, first.head.or(currentContext.not).not)
              }

              if (second.equivalentTo(FeatureExprFactory.False)) {
                //featureBuffer += getFeatureCombinations(second.collectDistinctFeatures2.diff(currentContext.collectDistinctFeatures2).toList)
                if (!currentFeatures.contains(second)) {
                  currentFeatures += second
                  currentFeatures += second.or(currentContext.not).not
                  featureBuffer += List(second, second.or(currentContext.not).not)
                }
              }
              List(second)
            }
          }
        })
        //featureBuffer.foreach(x => println(x))
        //println()
        currentFeatures.clear()
        if (featureBuffer.isEmpty) {
          if (!featureList2.isEmpty) {
            featureList2
          }
          List()
        } else if (featureBuffer.size == 1) {
          val result = featureBuffer.toList.head
          result
        } else {
          val result = featureBuffer.toList.tail.foldLeft(featureBuffer.toList.head)((first, second) => {
            first.flatMap(x => second.map(y => y.and(x)))
          })
          result
        }
      }
    }
    a match {
      case ws: WhileStatement =>
        computationHelper(ws.expr, currentContext)
      case fs: ForStatement =>
        val features1 = computationHelper(fs.expr1, currentContext, true)
        var features2 = computationHelper(fs.expr2, currentContext, true).diff(features1)
        if (features2.isEmpty) {
          features2 = List(FeatureExprFactory.True)
        }
        var features3 = computationHelper(fs.expr3, currentContext, true).diff(features2).diff(features1)
        if (features3.isEmpty) {
          features3 = List(FeatureExprFactory.True)
        }
        val result = features1.flatMap(x => features2.map(y => y.and(x))).flatMap(x => features3.map(y => y.and(x)))
        result
      case is: IfStatement =>
        computationHelper(is.condition, currentContext)
      case ss: SwitchStatement =>
        computationHelper(ss.expr, currentContext)
      case es: ExprStatement =>
        computationHelper(es.expr, currentContext)
      case ds: DoStatement =>
        computationHelper(ds.expr, currentContext)
      case rs@ReturnStatement(Some(x)) =>
        computationHelper(x, currentContext)
      case gs: GotoStatement =>
        computationHelper(gs.target, currentContext)
      case fd: FunctionDef =>
        val features1 = computationHelper(fd.specifiers, currentContext, true)
        var features2 = computationHelper(fd.declarator, currentContext, true).diff(features1)
        if (features2.isEmpty) {
          features2 = List(FeatureExprFactory.True)
        }
        var features3 = computationHelper(fd.oldStyleParameters, currentContext, true).diff(features2).diff(features1)
        if (features3.isEmpty) {
          features3 = List(FeatureExprFactory.True)
        }
        val result = features1.flatMap(x => features2.map(y => y.and(x))).flatMap(x => features3.map(y => y.and(x)))
        result
      case k =>
        computationHelper(k, currentContext)
    }
  }

  def debugNextRelevantFeatures(features: List[FeatureExpr], currentContext: FeatureExpr = FeatureExprFactory.True): List[List[FeatureExpr]] = {
    /* features.tail.foldLeft(features.head)((first, second) => {
      if (first.mex(second).isTautology()) {
        currentContext.implies(first.and(second)).isTautology()
      } else {

      }
    })*/
    List()
  }

  def getNextVariableFeaturesCondition(a: Any, currentContext: FeatureExpr = FeatureExprFactory.True): List[FeatureExpr] = {
    def getNextFeatureHelp(a: Any, currentContext: FeatureExpr = FeatureExprFactory.True): List[FeatureExpr] = {
      a match {
        case d@Opt(ft, entry: StructOrUnionSpecifier) => if (ft.equals(FeatureExprFactory.True)) entry.productIterator.toList.flatMap(getNextFeatureHelp(_)) else List(ft) ++ entry.productIterator.toList.flatMap(getNextFeatureHelp(_, ft))
        case d@Opt(ft, entry: NArySubExpr) => if (ft.equals(FeatureExprFactory.True)) entry.productIterator.toList.flatMap(getNextFeatureHelp(_)) else List(ft) ++ entry.productIterator.toList.flatMap(getNextFeatureHelp(_, ft))
        case d@Opt(ft, entry: Expr) => if (ft.equals(FeatureExprFactory.True)) entry.productIterator.toList.flatMap(getNextFeatureHelp(_)) else List(ft) ++ entry.productIterator.toList.flatMap(getNextFeatureHelp(_, ft))
        case d@Opt(ft, entry: DeclParameterDeclList) => if (ft.equals(FeatureExprFactory.True)) entry.productIterator.toList.flatMap(getNextFeatureHelp(_)) else List(ft) ++ entry.productIterator.toList.flatMap(getNextFeatureHelp(_))
        case d@Opt(ft, entry: InitDeclaratorI) => (if (!ft.equals(FeatureExprFactory.True)) List(ft) else List()) ++ entry.productIterator.toList.flatMap(getNextFeatureHelp(_))
        case d@Opt(ft, entry) => if (!ft.equals(FeatureExprFactory.True)) List(ft) else List()
        /*case i: Id =>
          if (idsToBeReplaced.containsKey(i)) {
            val tmp = idsToBeReplaced.get(i) //.filter(x => )
            idsToBeReplaced.get(i)
          } else {
            List()
          }*/
        case l: List[_] => l.flatMap(getNextFeatureHelp(_))
        case p: Product => p.productIterator.toList.flatMap(getNextFeatureHelp(_))
        case _ => List()
      }
    }
    getNextFeatureHelp(a).distinct
  }

  def getNextVariableFeaturesCondition2(a: Any, currentContext: FeatureExpr = FeatureExprFactory.True): List[FeatureExpr] = {
    def getNextFeatureHelp(a: Any, currentContext: FeatureExpr = FeatureExprFactory.True): List[FeatureExpr] = {
      a match {
        case d@Opt(ft, entry: StructOrUnionSpecifier) => entry.productIterator.toList.flatMap(getNextFeatureHelp(_, ft))
        case d@Opt(ft, entry: NArySubExpr) => entry.productIterator.toList.flatMap(getNextFeatureHelp(_, ft))
        case d@Opt(ft, entry: Expr) => entry.productIterator.toList.flatMap(getNextFeatureHelp(_, ft))
        case d@Opt(ft, entry: DeclParameterDeclList) => entry.productIterator.toList.flatMap(getNextFeatureHelp(_))
        case d@Opt(ft, entry: InitDeclaratorI) => entry.productIterator.toList.flatMap(getNextFeatureHelp(_))
        case d@Opt(ft, entry) => List()
        case i: Id =>
          if (idsToBeReplaced.containsKey(i)) {
            idsToBeReplaced.get(i)
          } else {
            List()
          }
        case l: List[_] => l.flatMap(getNextFeatureHelp(_))
        case p: Product => p.productIterator.toList.flatMap(getNextFeatureHelp(_))
        case _ => List()
      }
    }
    getNextFeatureHelp(a).distinct
  }

  /*
  New implementation of the lift opt function. This method looks at all Opt(True, entry) nodes and checks if the next lower level
  of opt nodes is variable. This node is then copied for the different feature combinations of his next level of opt nodes.
   */
  def liftOpts[T <: Product](t: T): T = {
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
          // Increase number of expanded statements
          if (!IdMap.contains(feat)) {
            IdMap += (feat -> IdMap.size)
          }
          val test = idsToBeReplaced.get(i).find(x => feat.implies(x).isTautology)
          test match {
            case None =>
              // TODO: this should not happen?
              Id("_" + IdMap.get(feat).get + "_" + i.name)
            case Some(x: FeatureExpr) =>
              Id("_" + IdMap.get(x).get + "_" + i.name)
            case k =>
              Id("")
          }
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
    def conditionalToStatement(c: Conditional[Statement], ft: FeatureExpr = FeatureExprFactory.False): List[Tuple2[Statement, FeatureExpr]] = {
      c match {
        case One(null) => List()
        case Choice(choiceFeature, first: Conditional[_], second: Conditional[_]) =>
          conditionalToStatement(first, choiceFeature) ++ conditionalToStatement(second, choiceFeature.not())
        case One(value) =>
          List((value, ft))
      }
    }
    One(CompoundStatement(conditionalToStatement(c).map(x => Opt(FeatureExprFactory.True, statementToIf(x._1, x._2)))))

    /*c match {
      case Choice(ft, One(first: Statement), One(second: Statement)) =>
        One(CompoundStatement(List(Opt(FeatureExprFactory.True, statementToIf(first, ft)), Opt(FeatureExprFactory.True, statementToIf(second, ft.not())))))
      case _ =>
        println("ChoiceToIf not exhaustive: " + c)
        null
    }*/
  }

  def convertThenBody(optIf: Opt[_]): Opt[_] = {
    optIf.entry match {
      case i@IfStatement(a, One(statement), b, c) =>
        statement match {
          case cs: CompoundStatement =>
            optIf
          case k =>
            Opt(optIf.feature, IfStatement(a, One(CompoundStatement(List(Opt(FeatureExprFactory.True, statement)))), b, c))
        }
      case k =>
        optIf
    }
  }

  def handleIfStatements(opt: Opt[_], currentFeature: FeatureExpr = FeatureExprFactory.True): List[Opt[_]] = {
    val optIf = convertThenBody(opt)
    optIf.entry match {
      case i@IfStatement(c@Choice(ft, cThen, cEls), then, elif, els) =>
        val choices = choiceToTuple(c)
        handleIfStatements(Opt(FeatureExprFactory.True, IfStatement(One(NAryExpr(featureToCExpr(choices.head._1), List(Opt(FeatureExprFactory.True, NArySubExpr("&&", choices.head._2.asInstanceOf[Expr]))))), then, choices.tail.map(x => Opt(FeatureExprFactory.True, ElifStatement(One(NAryExpr(featureToCExpr(x._1), List(Opt(FeatureExprFactory.True, NArySubExpr("&&", x._2.asInstanceOf[Expr]))))), then))) ++ elif, els)), currentFeature)
      case i@IfStatement(One(cond), One(then: CompoundStatement), elif, els) =>
        if (containsIdUsage(cond)) {
          //val feat = computeNextRelevantFeatures(i)
          val feat = computeIdUsageFeatures(cond)
          if (!feat.isEmpty) {
            noOfStatementDuplications = noOfStatementDuplications - 1 + feat.size
          }
          feat.flatMap(x => handleIfStatements(removeContraryOptsByFeature(Opt(optIf.feature, IfStatement(One(NAryExpr(featureToCExpr(x), List(Opt(FeatureExprFactory.True, NArySubExpr("&&", convertIdUsagesFromDefuse(cond, x)))))), One(then), elif, els)), x), x))
        } else {
          if (optIf.feature.equivalentTo(FeatureExprFactory.True)) {
            if (isVariable(cond)) {
              noOfStatementsVariable = noOfStatementsVariable + 1
              val feat = computeNextRelevantFeatures(i)
              if (!feat.isEmpty) {
                noOfStatementDuplications = noOfStatementDuplications - 1 + feat.size
              }
              feat.flatMap(x => handleIfStatements(removeContraryOptsByFeature(Opt(optIf.feature, IfStatement(One(NAryExpr(featureToCExpr(x), List(Opt(FeatureExprFactory.True, NArySubExpr("&&", convertIdUsagesFromDefuse(cond, x)))))), One(then), elif, els)), x), x))
            } else if (isVariable(elif)) {

              /*
             Case #1: Always occurring if statement with variability in elif statements
              */
              noOfStatementsVariable = noOfStatementsVariable + 1
              List(optIf.copy(entry = i.copy(elifs = transformDeclarationsRecursive(elif), thenBranch = One(transformDeclarationsRecursive(then, currentFeature)))))
            } else {

              /*
              Case #2: Always occurring if statement without further variability
               */
              //List(Opt(FeatureExprFactory.True, IfStatement(One(cond), One(transformDeclarationsRecursive(then, env, defuse)), transformDeclarationsRecursive(elif, env, defuse), els)))
              List(transformDeclarationsRecursive(optIf, currentFeature))
              //List(optIf)
            }
          } else {
            noOfStatementsVariable = noOfStatementsVariable + 1
            handleIfStatements(removeContraryOptsByFeature(Opt(FeatureExprFactory.True, IfStatement(One(NAryExpr(featureToCExpr(optIf.feature), List(Opt(FeatureExprFactory.True, NArySubExpr("&&", cond))))), One(then), elif, els)), optIf.feature), optIf.feature)
          }
        }
      case k =>
        List()
    }
  }

  def handleWhileStatements(opt: Opt[Statement], currentFeature: FeatureExpr = FeatureExprFactory.True): List[Opt[Statement]] = {
    opt.entry match {
      case w@WhileStatement(expr, conditional) =>
        if (!opt.feature.equivalentTo(FeatureExprFactory.True)) {
          noOfStatementsVariable = noOfStatementsVariable + 1
          List((Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(opt.feature)), One(CompoundStatement(handleWhileStatements(removeContraryOptsByFeature(Opt(FeatureExprFactory.True, w), opt.feature), opt.feature))), List(), None))))
        } else {
          conditional match {
            case One(statement) =>
              if (containsIdUsage(expr)) {
                val feat = computeIdUsageFeatures(expr)
                val result = feat.map(x => {
                  Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(x)), One(CompoundStatement(handleWhileStatements(Opt(FeatureExprFactory.True, WhileStatement(convertIdUsagesFromDefuse(expr, x), conditional)), x))), List(), None))
                })
                if (!result.isEmpty) {
                  noOfStatementDuplications = noOfStatementDuplications - 1 + result.size
                }
                result
              } else {
                List(transformDeclarationsRecursive(opt, currentFeature))
              }
            case c@Choice(ft, one, second) =>
              val choices = choiceToTuple(c)
              if (!choices.isEmpty) {
                noOfStatementDuplications = noOfStatementDuplications - 1 + choices.size
              }
              List(Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(choices.head._1)), One(CompoundStatement(handleWhileStatements(Opt(FeatureExprFactory.True, WhileStatement(expr, One(choices.head._2))), choices.head._1))), choices.tail.map(y => Opt(FeatureExprFactory.True, ElifStatement(One(featureToCExpr(y._1)), One(CompoundStatement(handleWhileStatements(Opt(FeatureExprFactory.True, WhileStatement(expr, One(y._2))), y._1)))))), None)))
          }
        }
      case k =>
        List()
    }
  }

  def handleDoStatements(opt: Opt[Statement], currentFeature: FeatureExpr = FeatureExprFactory.True): List[Opt[Statement]] = {
    opt.entry match {
      case d@DoStatement(expr, conditional) =>
        if (!opt.feature.equivalentTo(FeatureExprFactory.True)) {
          noOfStatementsVariable = noOfStatementsVariable + 1
          List((Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(opt.feature)), One(CompoundStatement(handleDoStatements(removeContraryOptsByFeature(Opt(FeatureExprFactory.True, d), opt.feature), opt.feature))), List(), None))))
        } else {
          conditional match {
            case One(statement) =>
              if (containsIdUsage(expr)) {
                val feat = computeIdUsageFeatures(expr)
                val result = feat.map(x => {
                  Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(x)), One(CompoundStatement(handleDoStatements(Opt(FeatureExprFactory.True, WhileStatement(convertIdUsagesFromDefuse(expr, x), conditional)), x))), List(), None))
                })
                if (!result.isEmpty) {
                  noOfStatementDuplications = noOfStatementDuplications - 1 + result.size
                }
                result
              } else {
                List(transformDeclarationsRecursive(opt, currentFeature))
              }
            case c@Choice(ft, one, second) =>
              val choices = choiceToTuple(c)
              if (!choices.isEmpty) {
                noOfStatementDuplications = noOfStatementDuplications - 1 + choices.size
              }
              List(Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(choices.head._1)), One(CompoundStatement(handleDoStatements(Opt(FeatureExprFactory.True, WhileStatement(expr, One(choices.head._2))), choices.head._1))), choices.tail.map(y => Opt(FeatureExprFactory.True, ElifStatement(One(featureToCExpr(y._1)), One(CompoundStatement(handleDoStatements(Opt(FeatureExprFactory.True, WhileStatement(expr, One(y._2))), y._1)))))), None)))
          }
        }
      case k =>
        List()
    }
  }

  def handleForStatements(opt: Opt[Statement], currentFeature: FeatureExpr = FeatureExprFactory.True): List[Opt[Statement]] = {
    opt.entry match {
      case f@ForStatement(expr1, expr2, expr3, conditional) =>
        if (!opt.feature.equivalentTo(FeatureExprFactory.True)) {
          noOfStatementsVariable = noOfStatementsVariable + 1
          List((Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(opt.feature)), One(CompoundStatement(handleForStatements(removeContraryOptsByFeature(Opt(FeatureExprFactory.True, f), opt.feature), opt.feature))), List(), None))))
        } else {
          conditional match {
            case One(statement) =>
              // TODO: check expr2 and expr3
              val test = computeNextRelevantFeatures(f)
              if (containsIdUsage(expr1) /*|| containsIdUsage(expr2) || containsIdUsage(expr3)*/ ) {
                val feat = computeIdUsageFeatures(expr1)
                val result = feat.map(x => {
                  if (!currentFeature.equivalentTo(FeatureExprFactory.True) && currentFeature.implies(x).isTautology()) {
                    transformDeclarationsRecursive(replaceOptAndId(opt, x))
                  } else {
                    Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(x)), One(CompoundStatement(handleForStatements(Opt(FeatureExprFactory.True, ForStatement(convertIdUsagesFromDefuse(expr1, x), convertIdUsagesFromDefuse(expr2, x), convertIdUsagesFromDefuse(expr3, x), conditional)), x))), List(), None))
                  }
                })
                if (!result.isEmpty) {
                  noOfStatementDuplications = noOfStatementDuplications - 1 + result.size
                }
                result
              } else {
                List(transformDeclarationsRecursive(opt, currentFeature))
              }
            case c@Choice(ft, one, second) =>
              val choices = choiceToTuple(c)
              if (!choices.isEmpty) {
                noOfStatementDuplications = noOfStatementDuplications - 1 + choices.size
              }
              List(Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(choices.head._1)), One(CompoundStatement(handleForStatements(Opt(FeatureExprFactory.True, ForStatement(expr1, expr2, expr3, One(choices.head._2))), choices.head._1))), choices.tail.map(y => Opt(FeatureExprFactory.True, ElifStatement(One(featureToCExpr(y._1)), One(CompoundStatement(handleForStatements(Opt(FeatureExprFactory.True, ForStatement(expr1, expr2, expr3, One(y._2))), y._1)))))), None)))
          }
        }
      case k =>
        List()
    }
  }

  def handleFunctions(optFunction: Opt[_]): List[Opt[_]] = {
    optFunction.entry match {
      // TODO: Nested Functions
      case fd: FunctionDef =>
        if (optFunction.feature.equals(FeatureExprFactory.True)) {
          val functionWithoutBody = fd.copy(stmt = CompoundStatement(List()))
          if (isVariable(fd.specifiers) || isVariable(fd.oldStyleParameters) || isVariable(fd.declarator)) {

            /*
            Case #1: Always occuring function with variability outside of the function body
             */
            val features = computeNextRelevantFeatures(fd)
            if (isVariable(fd.specifiers)) {
              // features = features ++ computeNextRelevantFeatures(fd.specifiers)
              noOfFunctionDuplicationsSpecifiers = noOfFunctionDuplicationsSpecifiers + 1
            }
            if (isVariable(fd.declarator)) {
              // features = features ++ computeNextRelevantFeatures(fd.declarator)
              noOfFunctionDuplicationsDeclarators = noOfFunctionDuplicationsDeclarators + 1
            }
            if (isVariable(fd.oldStyleParameters)) {
              // features = features ++ computeNextRelevantFeatures(fd.oldStyleParameters)
              noOfFunctionDuplicationsParameters = noOfFunctionDuplicationsParameters + 1
            }
            // val features = computeNextRelevantFeatures(functionWithoutBody)
            // val features = getFeatureCombinations(removeList(getNextFeatures(fd).flatMap(x => x.collectDistinctFeatures2).toList, optFunction.feature.collectDistinctFeatures2.toList))
            val result = features.map(x => {
              val tmpResult = filterOptsByFeature(convertId(replaceFeatureByTrue(optFunction, optFunction.feature), x.&(optFunction.feature)), x).asInstanceOf[Opt[FunctionDef]]
              tmpResult.copy(entry = tmpResult.entry.copy(stmt = transformDeclarationsRecursive(tmpResult.entry.stmt)))
              //transformDeclarationsRecursive(filterOptsByFeature(convertId(replaceFeatureByTrue(optFunction, optFunction.feature), x.&(optFunction.feature), defuse), x), env, defuse)
            })
            if (!result.isEmpty) {
              noOfOptionalFunctions = noOfOptionalFunctions + 1
              noOfFunctionDuplications = noOfFunctionDuplications + result.size - 1
            }
            result
          } else {

            /*
            Case #2: Always occuring function without variability outside of the function body
             */
            List(transformDeclarationsRecursive(optFunction))
          }
        } else {
          val tempOpt = replaceFeatureByTrue(optFunction, optFunction.feature).asInstanceOf[Opt[FunctionDef]]
          val functionWithoutBody = tempOpt.entry.copy(stmt = CompoundStatement(List()))
          if (isVariable(tempOpt.entry.specifiers) || isVariable(tempOpt.entry.oldStyleParameters) || isVariable(tempOpt.entry.declarator)) {

            /*
            Case #3: Annotated function with variability outside of the function body
             */
            // var features: List[FeatureExpr] = List()
            if (isVariable(tempOpt.entry.specifiers)) {
              //features = features ++ computeNextRelevantFeatures(tempOpt.entry.specifiers)
              noOfFunctionDuplicationsSpecifiers = noOfFunctionDuplicationsSpecifiers + 1
            }
            if (isVariable(tempOpt.entry.declarator)) {
              //features = features ++ computeNextRelevantFeatures(tempOpt.entry.declarator)
              noOfFunctionDuplicationsDeclarators = noOfFunctionDuplicationsDeclarators + 1
            }
            if (isVariable(tempOpt.entry.oldStyleParameters)) {
              //features = features ++ computeNextRelevantFeatures(tempOpt.entry.oldStyleParameters)
              noOfFunctionDuplicationsParameters = noOfFunctionDuplicationsParameters + 1
            }
            //val features = computeNextRelevantFeatures(functionWithoutBody)
            val features = computeNextRelevantFeatures(fd, optFunction.feature).map(x => x.and(optFunction.feature))
            // features = features.toSet.toList
            val result = features.map(x => {
              transformDeclarationsRecursive(filterOptsByFeature(convertId(tempOpt, x), x))
            })
            if (!result.isEmpty) {
              noOfOptionalFunctions = noOfOptionalFunctions + 1
              noOfFunctionDuplications = noOfFunctionDuplications + result.size - 1
            }
            result
          } else {

            /*
           Case #4: Annotated function without variability outside of the function body
            */
            noOfOptionalFunctions = noOfOptionalFunctions + 1
            val tmpResult = convertId(tempOpt, optFunction.feature)
            List(tmpResult.copy(entry = tmpResult.entry.copy(stmt = transformDeclarationsRecursive(tmpResult.entry.stmt))))
          }
        }
      case nfd: NestedFunctionDef =>
        if (optFunction.feature.equals(FeatureExprFactory.True)) {
          val functionWithoutBody = nfd.copy(stmt = CompoundStatement(List()))
          if (isVariable(nfd.specifiers) || isVariable(nfd.parameters) || isVariable(nfd.declarator)) {

            /*
            Case #1: Always occuring function with variability outside of the function body
             */
            val features = computeNextRelevantFeatures(nfd)
            if (isVariable(nfd.specifiers)) {
              // features = features ++ computeNextRelevantFeatures(fd.specifiers)
              noOfFunctionDuplicationsSpecifiers = noOfFunctionDuplicationsSpecifiers + 1
            }
            if (isVariable(nfd.declarator)) {
              // features = features ++ computeNextRelevantFeatures(fd.declarator)
              noOfFunctionDuplicationsDeclarators = noOfFunctionDuplicationsDeclarators + 1
            }
            if (isVariable(nfd.parameters)) {
              // features = features ++ computeNextRelevantFeatures(fd.oldStyleParameters)
              noOfFunctionDuplicationsParameters = noOfFunctionDuplicationsParameters + 1
            }
            // val features = computeNextRelevantFeatures(functionWithoutBody)
            // val features = getFeatureCombinations(removeList(getNextFeatures(fd).flatMap(x => x.collectDistinctFeatures2).toList, optFunction.feature.collectDistinctFeatures2.toList))
            val result = features.map(x => {
              val tmpResult = filterOptsByFeature(convertId(replaceFeatureByTrue(optFunction, optFunction.feature), x.&(optFunction.feature)), x).asInstanceOf[Opt[FunctionDef]]
              tmpResult.copy(entry = tmpResult.entry.copy(stmt = transformDeclarationsRecursive(tmpResult.entry.stmt)))
              //transformDeclarationsRecursive(filterOptsByFeature(convertId(replaceFeatureByTrue(optFunction, optFunction.feature), x.&(optFunction.feature), defuse), x), env, defuse)
            })
            if (!result.isEmpty) {
              noOfOptionalFunctions = noOfOptionalFunctions + 1
              noOfFunctionDuplications = noOfFunctionDuplications + result.size - 1
            }
            result
          } else {

            /*
            Case #2: Always occuring function without variability outside of the function body
             */
            List(transformDeclarationsRecursive(optFunction))
          }
        } else {
          val tempOpt = replaceFeatureByTrue(optFunction, optFunction.feature).asInstanceOf[Opt[FunctionDef]]
          val functionWithoutBody = tempOpt.entry.copy(stmt = CompoundStatement(List()))
          if (isVariable(tempOpt.entry.specifiers) || isVariable(tempOpt.entry.oldStyleParameters) || isVariable(tempOpt.entry.declarator)) {

            /*
            Case #3: Annotated function with variability outside of the function body
             */
            // var features: List[FeatureExpr] = List()
            if (isVariable(tempOpt.entry.specifiers)) {
              //features = features ++ computeNextRelevantFeatures(tempOpt.entry.specifiers)
              noOfFunctionDuplicationsSpecifiers = noOfFunctionDuplicationsSpecifiers + 1
            }
            if (isVariable(tempOpt.entry.declarator)) {
              //features = features ++ computeNextRelevantFeatures(tempOpt.entry.declarator)
              noOfFunctionDuplicationsDeclarators = noOfFunctionDuplicationsDeclarators + 1
            }
            if (isVariable(tempOpt.entry.oldStyleParameters)) {
              //features = features ++ computeNextRelevantFeatures(tempOpt.entry.oldStyleParameters)
              noOfFunctionDuplicationsParameters = noOfFunctionDuplicationsParameters + 1
            }
            //val features = computeNextRelevantFeatures(functionWithoutBody)
            val features = computeNextRelevantFeatures(nfd, optFunction.feature).map(x => x.and(optFunction.feature))
            // features = features.toSet.toList
            val result = features.map(x => {
              transformDeclarationsRecursive(filterOptsByFeature(convertId(tempOpt, x), x))
            })
            if (!result.isEmpty) {
              noOfOptionalFunctions = noOfOptionalFunctions + 1
              noOfFunctionDuplications = noOfFunctionDuplications + result.size - 1
            }
            result
          } else {

            /*
           Case #4: Annotated function without variability outside of the function body
            */
            noOfOptionalFunctions = noOfOptionalFunctions + 1
            val tmpResult = convertId(tempOpt, optFunction.feature)
            List(tmpResult.copy(entry = tmpResult.entry.copy(stmt = transformDeclarationsRecursive(tmpResult.entry.stmt))))
          }
        }
      case _ => List()
    }
  }

  def analyseDeclarations[T <: Product](t: T) = {
    val r = manytd(query {
      case Declaration(declSpecs, init) =>
        init.foreach(x => x match {
          case Opt(ft, iDecl: InitDeclaratorI) =>
            iDecl.declarator match {
              case a: AtomicNamedDeclarator =>
                val i = 0
              case n: NestedNamedDeclarator =>
                val i = 0
              case k =>
                println(k)
            }
          case k =>
            val i = 0
        })
    })
    r(t)
  }
}