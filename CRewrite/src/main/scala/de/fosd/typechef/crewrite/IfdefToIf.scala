package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.featureexpr.sat._
import collection.mutable.ListBuffer
import java.util
import util.IdentityHashMap
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
  val trueF = FeatureExprFactory.True

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
  var featureSet: Set[SingleFeatureExpr] = Set()

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
        del = del ++ List(Opt(trueF, NArySubExpr("||", featureToCExpr(e))))
      NAryExpr(featureToCExpr(l.head), del)
    case Not(n) => UnaryOpExpr("!", featureToCExpr(n))
  }

  /*
 Creates a file including an external int, a function, a struct with all features and an init function for that struct
  */
  def writeOptionFile(ast: AST) = {
    val features = filterFeatures(ast)
    val optionsAst = definedExternalToAst(features)

    PrettyPrinter.printF(optionsAst, "opt.h")
  }

  /*
  Creates a csv friendly line with all the statistical information from one transformation
   */
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

  /*
  Creates an option struct for all collected FeatureExpressions
   */
  def getTotalOptionFile(): TranslationUnit = {
    definedExternalToAst(featureSet)
  }

  /*
  Converts a set of FeatureExpressions into an option struct
   */
  def definedExternalToAst(defExSet: Set[SingleFeatureExpr]): TranslationUnit = {
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
  Filteres a given product for feature expressions which are not True and returns a set including each single feature expression
   */
  def filterFeatures(a: Any): Set[SingleFeatureExpr] = {
    def getFeatureExpressions(a: Any): List[FeatureExpr] = {
      a match {
        case o: Opt[_] => (if (o.feature == trueF) List() else List(o.feature)) ++ o.productIterator.toList.flatMap(getFeatureExpressions(_))
        case l: List[_] => l.flatMap(getFeatureExpressions(_))
        case p: Product => p.productIterator.toList.flatMap(getFeatureExpressions(_))
        case t: FeatureExpr => if (t == trueF) List() else List(t)
        case _ => List()
      }
    }
    val features = getFeatureExpressions(a).flatMap(x => x.collectDistinctFeatureObjects).toSet
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
            if (o.feature.equivalentTo(trueF)) {
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
        case o: Opt[_] => (if (o.feature == trueF) List() else List(o.feature)) ++ o.productIterator.toList.flatMap(getFeatureExpressions(_))
        case l: List[_] => l.flatMap(getFeatureExpressions(_))
        case p: Product => p.productIterator.toList.flatMap(getFeatureExpressions(_))
        case t: FeatureExpr => if (t == trueF) List() else List(t)
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
        if (!f.equivalentTo(trueF) && !f.equivalentTo(FeatureExprFactory.False) && !lst.contains(f)) {
          lst += f
        }
    })
    r(a).get
    lst.toList
  }

  /*
  This method fills the IdMap which is used to map a feature expression to a number.
   */
  def fillIdMap(a: Any) = {
    if (IdMap.size == 0) {
      IdMap += (trueF -> IdMap.size)
    }
    getSingleFeatureSet(a).foreach(x => if (!IdMap.contains(x)) {
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
    } else {
      //val result = featureBufferList.tail.foldLeft(featureBufferList.head)((first, second) => {
      //first.flatMap(x => second.map(y => y.and(x)))
      //})
      lst.tail.foldLeft(List(lst.head, lst.head.not()))((first, second) => {
        first.flatMap(x => List(x.and(second), x.and(second.not())))
      })
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

  /*
  Retrieves the FeatureExpression which is mapped to the given number
   */
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

  def replaceFeatureByTrue[T <: Product](t: T, feat: FeatureExpr): T = {
    val r = manytd(rule {
      case l: List[Opt[_]] =>
        l.flatMap(o =>
          if (o.feature.equivalentTo(trueF)) {
            List(o)
          } else if (feat.&(o.feature).isContradiction()) {
            List()
          } else if (o.feature.equivalentTo(feat)) {
            List(o.copy(feature = trueF))
          } else if (feat.implies(o.feature).isTautology()) {
            List(o.copy(feature = trueF))
          } else {
            List(o)
          })
    })
    t match {
      case o@Opt(ft, entry) =>
        if (ft.equals(trueF)) {
          r(o) match {
            case None => t
            case _ => r(o).get.asInstanceOf[T]
          }
        } else if (ft.equals(feat)) {
          val newOpt = Opt(trueF, entry)
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

  /*
  Filters given Elements Opt Lists by Opt nodes where given feature implies Opt.feature and replaces these by True.
   */
  def filterOptsByFeature[T <: Product](t: T, feat: FeatureExpr): T = {
    val r = manytd(rule {
      case l: List[Opt[_]] =>
        l.flatMap(o => {
          if (feat.mex(o.feature).isTautology()) {
            List()
          } else if (feat.equivalentTo(o.feature) || feat.implies(o.feature).isTautology) {
            List(Opt(trueF, o.entry))
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

  /*
  Replaces given Elements Opt Lists Opt nodes which are the same as given feature by True.
  */
  def replaceFeature[T <: Product](t: T, feat: FeatureExpr): T = {
    val r = manytd(rule {
      case l: List[Opt[_]] =>
        l.flatMap(o =>
          if (feat.mex(o.feature).isTautology()) {
            List()
          } else if (o.feature.equivalentTo(feat)) {
            List(Opt(trueF, o.entry))
          } else {
            List(o)
          })
    })
    r(t) match {
      case None => t
      case k => k.get.asInstanceOf[T]
    }
  }

  /*
  Replaces given FeatureExpression recursively from given Element by True. Also removes Opt nodes which should not occur
  in this given context. Also renames Ids if they have a declaration annotated by given FeatureExpression.
  */
  def replaceOptAndId[T <: Product](t: T, feat: FeatureExpr): T = {
    val r = manytd(rule {
      case l: List[Opt[_]] =>
        l.flatMap(o =>
          if (o.feature.equals(trueF)) {
            List(o)
          } else if (feat.&(o.feature).isContradiction()) {
            List()
          } else if (o.feature.equivalentTo(feat) || feat.implies(o.feature).isTautology) {
            List(o.copy(feature = trueF))
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

  def replaceTrueByFeature[T <: Product](t: T, feat: FeatureExpr): T = {
    val r = manytd(rule {
      case l: List[Opt[_]] =>
        l.flatMap(o =>
          if (feat.&(o.feature).isContradiction()) {
            List()
          } else if (o.feature.equivalentTo(feat) || feat.implies(o.feature).isTautology || o.feature.equivalentTo(trueF)) {
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

  /*
  Makes #ifdef to if transformation on given AST element. Returns new AST element and a statistics String.
   */
  def transformAst[T <: Product](t: T, decluse: IdentityHashMap[Id, List[Id]], fileName: String = ""): Tuple2[T, String] = {
    fillIdMap(t)
    defuse = decluse
    val result = transformRecursive(t)
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

  def generateStatistics[T <: Product](t: T, ft: FeatureExpr = trueF) = {
    val r = alltd(query {
      case o@Opt(feat, entry) =>
        if (feat.equivalentTo(trueF) || feat.equivalentTo(ft)) {
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

  /*
  Transforms given AST element.
   */
  def transformRecursive[T <: Product](t: T, currentContext: FeatureExpr = trueF): T = {
    val r = alltd(rule {
      case l: List[Opt[_]] =>
        l.flatMap(x => x match {
          case o@Opt(ft: FeatureExpr, entry) =>
            /*
           Handle opt nodes which occur under a certain condition
            */
            if (ft != trueF) {
              entry match {
                case i@IfStatement(_, _, _, _) =>
                  noOfStatements = noOfStatements + 1
                  handleIfStatements(o, ft)
                case r: ReturnStatement =>
                  noOfStatements = noOfStatements + 1
                  noOfStatementsVariable = noOfStatementsVariable + 1
                  if (containsIdUsage(r)) {
                    //TODO: IdUsage
                    List(Opt(trueF, transformRecursive(replaceFeature(statementToIf(r, ft), ft))))
                  } else {
                    val test = statementToIf(r, ft)
                    val result = List(Opt(trueF, statementToIf(r, ft)))
                    result
                  }

                case w: WhileStatement =>
                  noOfStatements = noOfStatements + 1
                  /*val result = List(Opt(trueF, transformRecursive(replaceFeature(statementToIf(w, ft), ft), env, defuse)))
                  result*/
                  handleWhileStatements(o.asInstanceOf[Opt[Statement]], currentContext)
                case s: SwitchStatement =>
                  noOfStatements = noOfStatements + 1
                  noOfStatementsVariable = noOfStatementsVariable + 1
                  val result = List(Opt(trueF, transformRecursive(replaceFeature(statementToIf(s, ft), ft))))
                  result
                case d: DoStatement =>
                  noOfStatements = noOfStatements + 1
                  noOfStatementsVariable = noOfStatementsVariable + 1
                  // val result = List(Opt(trueF, transformRecursive(replaceFeature(statementToIf(d, ft), ft))))
                  val result = handleDoStatements(o.asInstanceOf[Opt[Statement]])
                  result
                case g: GotoStatement =>
                  noOfStatements = noOfStatements + 1
                  noOfStatementsVariable = noOfStatementsVariable + 1
                  val result = List(Opt(trueF, transformRecursive(replaceFeature(statementToIf(g, ft), ft))))
                  result
                case f: ForStatement =>
                  noOfStatements = noOfStatements + 1
                  noOfStatementsVariable = noOfStatementsVariable + 1
                  handleForStatements(o.asInstanceOf[Opt[Statement]])
                case elif@ElifStatement(One(expr: Expr), then) =>
                  noOfStatements = noOfStatements + 1
                  noOfStatementsVariable = noOfStatementsVariable + 1
                  List(Opt(trueF, transformRecursive(replaceFeature(ElifStatement(One(NAryExpr(featureToCExpr(ft), List(Opt(trueF, NArySubExpr("&&", expr))))), then), ft))))

                case e: ExprStatement =>
                  noOfStatements = noOfStatements + 1
                  noOfStatementsVariable = noOfStatementsVariable + 1
                  val ifStmt = IfStatement(One(featureToCExpr(o.feature)), One(CompoundStatement(List(Opt(trueF, filterOptsByFeature(e, o.feature))))), List(), None)
                  List(Opt(trueF, transformRecursive(ifStmt)))
                case label: LabelStatement =>
                  noOfStatements = noOfStatements + 1
                  noOfStatementsVariable = noOfStatementsVariable + 1
                  if (containsIdUsage(label)) {
                    List(Opt(trueF, IfStatement(One(featureToCExpr(ft)), One(CompoundStatement(List(Opt(trueF, convertIdUsagesFromDefuse(label, ft))))), List(), None)))
                  } else {
                    List(Opt(trueF, IfStatement(One(featureToCExpr(ft)), One(CompoundStatement(List(Opt(trueF, label)))), List(), None)))
                  }


                case declStmt@DeclarationStatement(decl: Declaration) =>
                  noOfDeclarations = noOfDeclarations + 1
                  noOfOptionalDeclarations = noOfOptionalDeclarations + 1
                  val newDecl = Opt(trueF, DeclarationStatement(replaceFeatureByTrue(convertId(decl, o.feature), o.feature)))
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
                  val result = List(transformRecursive(convertId(replaceFeatureByTrue(o, o.feature), o.feature)))
                  result
                case sd: StructDeclaration =>
                  noOfStructDeclarations = noOfStructDeclarations + 1
                  noOfStructDeclarationsRenamed = noOfStructDeclarationsRenamed + 1
                  List(convertAllIds(replaceFeatureByTrue(o, o.feature), o.feature))

                case p: Pragma =>
                  // TODO: Eventuell variabel lassen
                  List(o.copy(feature = trueF))
                case s: Specifier =>
                  List(o.copy(feature = trueF))
                case s: String =>
                  List(o.copy(feature = trueF))
                case es: EmptyStatement =>
                  List()
                case ee: EmptyExternalDef =>
                  List()
                case cs: CompoundStatement =>
                  List(transformRecursive(o))
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
                  List(Opt(trueF, transformRecursive(cmpStmt, currentContext)))
                case f: ForStatement =>
                  noOfStatements = noOfStatements + 1
                  handleForStatements(o.asInstanceOf[Opt[Statement]], currentContext)
                case d: DoStatement =>
                  noOfStatements = noOfStatements + 1
                  handleDoStatements(o.asInstanceOf[Opt[Statement]], currentContext)
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
                    val res = features.map(x => Opt(True, transformRecursive(exprStatementToIf(convertIdUsagesFromDefuse(e, x), x))))
                    res

                  } else if (isVariable(e)) {
                    val features = getSingleFeatureSet(e)
                    features.map(x => Opt(trueF, IfStatement(One(featureToCExpr(x)), One(CompoundStatement(List(Opt(trueF, transformRecursive(filterOptsByFeature(e, x)))))), List(), None)))
                    //List(transformRecursive(o, env, defuse))
                  } else {
                    List(o)
                  }
                case w@WhileStatement(expr: Expr, s: Conditional[_]) =>
                  noOfStatements = noOfStatements + 1
                  val result = handleWhileStatements(o.asInstanceOf[Opt[Statement]], currentContext)
                  result
                case declStmt@DeclarationStatement(decl: Declaration) =>
                  noOfDeclarations = noOfDeclarations + 1
                  if (isVariable(decl)) {
                    val features = computeNextRelevantFeatures(decl)
                    features.map(x => convertId(replaceFeature(o, x), x))
                  } else {
                    List(o)
                  }
                case ss: SwitchStatement =>
                  noOfStatements = noOfStatements + 1
                  if (isVariable(ss)) {
                    val features = getFeatureCombinations(getFeatureExpressions(ss).flatMap(x => x.collectDistinctFeatureObjects).distinct)
                    if (!features.isEmpty) {
                      noOfStatementsVariable = noOfStatementDuplications - 1 + features.size
                    }
                    features.map(x => {
                      optStatementToIf(Opt(x, transformRecursive(replaceFeatureByTrue(o, x)).entry).asInstanceOf[Opt[Statement]])
                    })
                  } else {
                    List(transformRecursive(o, currentContext))
                  }
                case i@IfStatement(_, _, _, _) =>
                  noOfStatements = noOfStatements + 1
                  handleIfStatements(o, currentContext)
                case elif@ElifStatement(One(cond), then) =>
                  noOfStatements = noOfStatements + 1
                  val feat = computeNextRelevantFeatures(cond)
                  if (!feat.isEmpty) {
                    noOfStatementDuplications = noOfStatementDuplications - 1 + feat.size
                    feat.map(x => transformRecursive(replaceOptAndId(Opt(trueF, ElifStatement(One(NAryExpr(featureToCExpr(x), List(Opt(trueF, NArySubExpr("&&", cond))))), then)), x), currentContext))
                  } else {
                    List(transformRecursive(o, currentContext))
                  }
                case elif@ElifStatement(c@Choice(ft, thenBranch, elseBranch), then) =>
                  noOfStatements = noOfStatements + 1
                  val choices = choiceToTuple(c)
                  if (!choices.isEmpty) {
                    noOfStatementDuplications = noOfStatementDuplications - 1 + choices.size
                  }
                  choices.map(x => {
                    if (containsIdUsage(thenBranch)) {
                      transformRecursive(replaceFeature(Opt(trueF, ElifStatement(One(NAryExpr(featureToCExpr(x._1), List(Opt(trueF, NArySubExpr("&&", convertIdUsagesFromDefuse(x._2, x._1)))))), then)), x._1), currentContext)
                    } else {
                      transformRecursive(replaceFeature(Opt(trueF, ElifStatement(One(NAryExpr(featureToCExpr(x._1), List(Opt(trueF, NArySubExpr("&&", x._2))))), then)), x._1), currentContext)
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
                    val newEnums = featureCombinations.map(x => Opt(trueF, convertId(filterOptsByFeature(e, x), x))).toList
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
                    val newEnumerators = featureSet.map(x => Opt(trueF, convertId(filterOptsByFeature(e, x), x))).toList
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
                        features.map(x => Opt(trueF, transformRecursive(filterOptsByFeature(sd, x))))
                      } else {
                        features.map(x => Opt(trueF, transformRecursive(convertId(filterOptsByFeature(sd, x), x))))
                      }
                    } else {
                      List(transformRecursive(o))
                    }
                  } else {
                    List(o)
                  }
                case d@Declaration(declSpecs, init) =>
                  noOfDeclarations = noOfDeclarations + 1
                  if (isVariable(d)) {
                    if (declSpecs.exists(x => (x.entry.isInstanceOf[EnumSpecifier] || (x.entry.isInstanceOf[StructOrUnionSpecifier]) && x.feature.equivalentTo(trueF)))) {
                      List(transformRecursive(o))
                    } else {
                      if (isExclusion(getNextVariableFeaturesCondition(d))) {
                        val features = getNextVariableFeaturesCondition(d).map(x => Opt(trueF, Declaration(filterOptsByFeature(declSpecs, x), convertId(filterOptsByFeature(init, x), x))))
                        if (!features.isEmpty) {
                          noOfDeclarationDuplications = noOfDeclarationDuplications - 1 + features.size
                        }
                        features
                      } else {
                        val features = getFeatureCombinations(getNextVariableFeaturesCondition(d)).map(x => Opt(trueF, Declaration(filterOptsByFeature(declSpecs, x), convertId(filterOptsByFeature(init, x), x))))
                        if (!features.isEmpty) {
                          noOfDeclarationDuplications = noOfDeclarationDuplications - 1 + features.size
                        }
                        features
                      }
                    }
                  } else {
                    List(o)
                  }
                case k: Product =>
                  List(transformRecursive(o, currentContext))
                case r =>
                  List(o)
              }
            }
          case k =>
            List(transformRecursive(k))
        })
    })
    r(t) match {
      case None =>
        t
      case k =>
        k.get.asInstanceOf[T]
    }
  }

  def nextLevelContainsVariability(t: Any): Boolean = {
    val optList = getNextOptList(t)
    val result = optList.exists(x => (x.feature != trueF))
    result
  }

  def secondNextLevelContainsVariability(t: Any): Boolean = {
    val optList = getNextOptList(t)
    var result = false
    if (!optList.isEmpty) {
      val finalOptList = optList.flatMap(x => getNextOptList(x))
      result = finalOptList.exists(x => (x.feature != trueF))
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

  def computeIdUsageFeatures(a: Any, currentContext: FeatureExpr = trueF): List[FeatureExpr] = {
    if (currentContext.equivalentTo(trueF)) {
      val res = getIdUsageFeatureList(a, currentContext).foldLeft(List(trueF))((first, second) => first.flatMap(x => second.diff(first).map(y => y.and(x))))
      res
    } else {
      val res = getIdUsageFeatureList(a, currentContext).foldLeft(List(trueF))((first, second) => first.flatMap(x => second.diff(first).map(y => y.and(x)))).flatMap(x => if (currentContext.implies(x).isTautology()) List(x) else List())
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
        case d@Opt(ft, entry) => if (!ft.equals(trueF)) List(ft) else List()
        case l: List[_] => l.flatMap(getNextFeatureHelp(_))
        case p: Product => p.productIterator.toList.flatMap(getNextFeatureHelp(_))
        case _ => List()
      }
    }
    getNextFeatureHelp(a).toSet.toList
  }

  def fixTypeChefsFeatureExpressions(feature: FeatureExpr, context: FeatureExpr): FeatureExpr = {
    if (feature.implies(context).isTautology) {
      feature
    } else {
      feature.and(context)
    }
  }

  def computeNextRelevantFeatures(a: Any, currentContext: FeatureExpr = trueF): List[FeatureExpr] = {
    def computationHelper(a: Any, currentContext: FeatureExpr = trueF, expectAtLeastOneResult: Boolean = false): List[FeatureExpr] = {
      val featureList = getNextVariableFeaturesCondition(a, currentContext).filterNot(x => x.equivalentTo(currentContext)) ++ List(FeatureExprFactory.False)
      val featureList2 = getNextVariableFeaturesCondition2(a)
      if (!featureList2.isEmpty || featureList.size > 1) {
        val i = 0
      }
      if (expectAtLeastOneResult && featureList.size == 1) {
        List(trueF)
      } else {
        val featureBuffer: ListBuffer[List[FeatureExpr]] = ListBuffer()
        val currentFeatures: mutable.HashSet[FeatureExpr] = new mutable.HashSet
        featureList.foldLeft(List(): List[FeatureExpr])((first, second) => {
          // Reached end of list
          if (second.equivalentTo(FeatureExprFactory.False)) {
            if (!first.isEmpty) {
              // featureBuffer += getFeatureCombinations(first.head.collectDistinctFeatures2.diff(currentContext.collectDistinctFeatures2).toList)
              if (!currentFeatures.contains(first.head)) {
                first.foreach(x => x.collectDistinctFeatureObjects.foreach(y => currentFeatures.add(y)))
                //currentFeatures.add(first.head)
                //currentFeatures.add(first.head.or(currentContext.not).not)
                //featureBuffer += List(first.head, first.head.or(currentContext.not).not)
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
              // All collected features are mutually exclusive and the context implies the or result of all of them
              featureBuffer += (second :: first)
              List()
            } else if (result) {
              // Continue collecting mutually exclusive expressions
              second :: first
            } else {
              //featureBuffer += getFeatureCombinations(first.head.collectDistinctFeatures2.diff(currentContext.collectDistinctFeatures2).toList)
              if (!currentFeatures.contains(first.head)) {
                //currentFeatures.add(first.head)
                //currentFeatures.add(first.head.or(currentContext.not).not)
                //featureBuffer += List(first.head, first.head.or(currentContext.not).not)
                first.foreach(x => x.collectDistinctFeatureObjects.foreach(y => currentFeatures.add(y)))
              }

              if (second.equivalentTo(FeatureExprFactory.False)) {
                //featureBuffer += getFeatureCombinations(second.collectDistinctFeatures2.diff(currentContext.collectDistinctFeatures2).toList)
                if (!currentFeatures.contains(second)) {
                  //currentFeatures += second
                  //currentFeatures += second.or(currentContext.not).not
                  //featureBuffer += List(second, second.or(currentContext.not).not)
                  second.collectDistinctFeatureObjects.foreach(x => currentFeatures.add(x))
                }
              }
              List(second)
            }
          }
        })
        //featureBuffer.foreach(x => println(x))
        //println()
        val singleFeaturesWithoutContext = currentFeatures.toList.diff(currentContext.collectDistinctFeatureObjects.toList)
        if (!singleFeaturesWithoutContext.isEmpty && singleFeaturesWithoutContext.size < 10) {
          featureBuffer += getFeatureCombinations(singleFeaturesWithoutContext)
        }
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
          val featureBufferList = featureBuffer.toList
          val result = featureBufferList.tail.foldLeft(featureBufferList.head)((first, second) => {
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
          features2 = List(trueF)
        }
        var features3 = computationHelper(fs.expr3, currentContext, true).diff(features2).diff(features1)
        if (features3.isEmpty) {
          features3 = List(trueF)
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
          features2 = List(trueF)
        }
        var features3 = computationHelper(fd.oldStyleParameters, currentContext, true).diff(features2).diff(features1)
        if (features3.isEmpty) {
          features3 = List(trueF)
        }
        val result = features1.flatMap(x => features2.map(y => y.and(x))).flatMap(x => features3.map(y => y.and(x)))
        result
      case k =>
        computationHelper(k, currentContext)
    }
  }

  def getNextVariableFeaturesCondition(a: Any, currentContext: FeatureExpr = trueF): List[FeatureExpr] = {
    def getNextFeatureHelp(a: Any, currentContext: FeatureExpr = trueF): List[FeatureExpr] = {
      a match {
        case d@Opt(ft, entry: StructOrUnionSpecifier) =>
          if (ft.equals(trueF) || ft.equals(FeatureExprFactory.False)) entry.productIterator.toList.flatMap(getNextFeatureHelp(_, currentContext)) else List(fixTypeChefsFeatureExpressions(ft, currentContext)) ++ entry.productIterator.toList.flatMap(getNextFeatureHelp(_, ft))
        case d@Opt(ft, entry: NArySubExpr) =>
          if (ft.equals(trueF) || ft.equals(FeatureExprFactory.False)) entry.productIterator.toList.flatMap(getNextFeatureHelp(_, currentContext)) else List(fixTypeChefsFeatureExpressions(ft, currentContext)) ++ entry.productIterator.toList.flatMap(getNextFeatureHelp(_, ft))
        case d@Opt(ft, entry: Expr) =>
          if (ft.equals(trueF) || ft.equals(FeatureExprFactory.False)) entry.productIterator.toList.flatMap(getNextFeatureHelp(_, currentContext)) else List(fixTypeChefsFeatureExpressions(ft, currentContext)) ++ entry.productIterator.toList.flatMap(getNextFeatureHelp(_, ft))
        case d@Opt(ft, entry: DeclParameterDeclList) =>
          if (ft.equals(trueF) || ft.equals(FeatureExprFactory.False)) entry.productIterator.toList.flatMap(getNextFeatureHelp(_, currentContext)) else List(fixTypeChefsFeatureExpressions(ft, currentContext)) ++ entry.productIterator.toList.flatMap(getNextFeatureHelp(_, ft))
        case d@Opt(ft, entry: InitDeclaratorI) =>
          (if (!ft.equals(trueF) || ft.equals(FeatureExprFactory.False)) List(fixTypeChefsFeatureExpressions(ft, currentContext)) else List()) ++ entry.productIterator.toList.flatMap(getNextFeatureHelp(_, currentContext))
        case d@Opt(ft, entry) =>
          if (!ft.equals(trueF) || ft.equals(FeatureExprFactory.False)) List(fixTypeChefsFeatureExpressions(ft, currentContext)) else List()
        case l: List[_] =>
          l.flatMap(getNextFeatureHelp(_, currentContext))
        case p: Product =>
          p.productIterator.toList.flatMap(getNextFeatureHelp(_, currentContext))
        case _ =>
          List()
      }
    }
    /*case i: Id =>
  if (idsToBeReplaced.containsKey(i)) {
    val tmp = idsToBeReplaced.get(i) //.filter(x => )
    idsToBeReplaced.get(i)
  } else {
    List()
  }*/
    val result = getNextFeatureHelp(a, currentContext).distinct
    result
  }

  def getNextVariableFeaturesCondition2(a: Any, currentContext: FeatureExpr = trueF): List[FeatureExpr] = {
    def getNextFeatureHelp(a: Any, currentContext: FeatureExpr = trueF): List[FeatureExpr] = {
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
            if (ft == trueF && nextLevelContainsVariability(entry)) {
              val nextLevel = getNextOptList(entry)
              val features = nextLevel.flatMap(x => if (x.feature != trueF) List(x.feature) else List()).toSet
              var needTrueExpression = false
              features.foreach(x => if (!features.exists(y => x.&(y).isContradiction())) {
                needTrueExpression = true
              })
              val result = features.map(x => replaceTrueByFeature(o, x).copy(feature = x)).toList
              if (needTrueExpression) {
                replaceTrueByFeature(o, trueF) :: result
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
    IfStatement(One(featureToCExpr(ft)), One(CompoundStatement(List(Opt(trueF, convertIdUsagesFromDefuse(replaceFeature(e, ft), ft))))), List(), None)
  }

  def statementToIf(e: Statement, ft: FeatureExpr): IfStatement = {
    IfStatement(One(featureToCExpr(ft)), One(CompoundStatement(List(Opt(trueF, convertIdUsagesFromDefuse(replaceFeature(e, ft), ft))))), List(), None)
  }

  def optStatementToIf(o: Opt[Statement]): Opt[IfStatement] = {
    Opt(trueF, IfStatement(One(featureToCExpr(o.feature)), One(CompoundStatement(List(Opt(trueF, convertIdUsagesFromDefuse(replaceFeature(o.entry, o.feature), o.feature))))), List(), None))
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
    One(CompoundStatement(conditionalToStatement(c).map(x => Opt(trueF, statementToIf(x._1, x._2)))))

    /*c match {
      case Choice(ft, One(first: Statement), One(second: Statement)) =>
        One(CompoundStatement(List(Opt(trueF, statementToIf(first, ft)), Opt(trueF, statementToIf(second, ft.not())))))
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
            Opt(optIf.feature, IfStatement(a, One(CompoundStatement(List(Opt(trueF, statement)))), b, c))
        }
      case k =>
        optIf
    }
  }

  def handleIfStatements(opt: Opt[_], currentFeature: FeatureExpr = trueF): List[Opt[_]] = {
    val optIf = convertThenBody(opt)
    optIf.entry match {
      case i@IfStatement(c@Choice(ft, cThen, cEls), then, elif, els) =>
        val choices = choiceToTuple(c)
        val result = choices.flatMap(x => handleIfStatements(replaceOptAndId(Opt(trueF, IfStatement(One(NAryExpr(featureToCExpr(x._1), List(Opt(trueF, NArySubExpr("&&", x._2))))), then, List(), None)), x._1), x._1))
        result
      //handleIfStatements(Opt(trueF, IfStatement(One(NAryExpr(featureToCExpr(choices.head._1), List(Opt(trueF, NArySubExpr("&&", choices.head._2))))), then, choices.tail.map(x => Opt(trueF, ElifStatement(One(NAryExpr(featureToCExpr(x._1), List(Opt(trueF, NArySubExpr("&&", x._2.asInstanceOf[Expr]))))), then))) ++ elif, els)), currentContext)
      case i@IfStatement(One(cond), One(then: CompoundStatement), elif, els) =>
        if (containsIdUsage(cond)) {
          //val feat = computeNextRelevantFeatures(i)
          val feat = computeIdUsageFeatures(cond)
          if (!feat.isEmpty) {
            noOfStatementDuplications = noOfStatementDuplications - 1 + feat.size
          }
          feat.flatMap(x => handleIfStatements(replaceFeature(Opt(optIf.feature, IfStatement(One(NAryExpr(featureToCExpr(x), List(Opt(trueF, NArySubExpr("&&", convertIdUsagesFromDefuse(cond, x)))))), One(then), elif, els)), x), x))
        } else {
          if (optIf.feature.equivalentTo(trueF)) {
            if (isVariable(cond)) {
              noOfStatementsVariable = noOfStatementsVariable + 1
              val feat = computeNextRelevantFeatures(i) // TODO: .filterNot(x => x.equals(FeatureExprFactory.False))
              if (!feat.isEmpty) {
                noOfStatementDuplications = noOfStatementDuplications - 1 + feat.size
              }
              feat.flatMap(x => handleIfStatements(filterOptsByFeature(Opt(optIf.feature, IfStatement(One(NAryExpr(featureToCExpr(x), List(Opt(trueF, NArySubExpr("&&", convertIdUsagesFromDefuse(cond, x)))))), One(then), elif, els)), x), x))
            } else if (isVariable(elif)) {

              /*
             Case #1: Always occurring if statement with variability in elif statements
              */
              noOfStatementsVariable = noOfStatementsVariable + 1
              List(optIf.copy(entry = i.copy(elifs = transformRecursive(elif), thenBranch = One(transformRecursive(then, currentFeature)))))
            } else {

              /*
              Case #2: Always occurring if statement without further variability
               */
              //List(Opt(trueF, IfStatement(One(cond), One(transformRecursive(then, env, defuse)), transformRecursive(elif, env, defuse), els)))
              List(transformRecursive(optIf, currentFeature))
              //List(optIf)
            }
          } else {
            noOfStatementsVariable = noOfStatementsVariable + 1
            handleIfStatements(replaceFeature(Opt(trueF, IfStatement(One(NAryExpr(featureToCExpr(optIf.feature), List(Opt(trueF, NArySubExpr("&&", cond))))), One(then), elif, els)), optIf.feature), optIf.feature)
          }
        }
      case k =>
        List()
    }
  }

  def handleWhileStatements(opt: Opt[Statement], currentContext: FeatureExpr = trueF): List[Opt[Statement]] = {
    opt.entry match {
      case w@WhileStatement(expr, conditional) =>
        if (!opt.feature.equivalentTo(trueF)) {
          noOfStatementsVariable = noOfStatementsVariable + 1
          val realFeature = fixTypeChefsFeatureExpressions(opt.feature, currentContext)
          List((Opt(trueF, IfStatement(One(featureToCExpr(realFeature)), One(CompoundStatement(handleWhileStatements(replaceFeature(replaceFeature(Opt(trueF, w), opt.feature), realFeature), realFeature))), List(), None))))
        } else {
          conditional match {
            case One(statement) =>
              val feat = computeNextRelevantFeatures(expr, currentContext)
              if (feat.exists(x => x.equals(FeatureExprFactory.False))) {
                val i = 0
              }
              if (!feat.isEmpty) {
                val result = feat.map(x => {
                  Opt(trueF, IfStatement(One(featureToCExpr(x)), One(CompoundStatement(handleWhileStatements(replaceOptAndId(Opt(trueF, WhileStatement(expr, conditional)), x), x))), List(), None))
                })
                if (!result.isEmpty) {
                  noOfStatementDuplications = noOfStatementDuplications - 1 + result.size
                }
                result
              } else {
                List(transformRecursive(opt, currentContext))
              }
            case c@Choice(ft, one, second) =>
              val choices = choiceToTuple(c)
              if (!choices.isEmpty) {
                noOfStatementDuplications = noOfStatementDuplications - 1 + choices.size
              }
              List(Opt(trueF, IfStatement(One(featureToCExpr(choices.head._1)), One(CompoundStatement(handleWhileStatements(replaceOptAndId(Opt(trueF, WhileStatement(expr, One(choices.head._2))), choices.head._1), choices.head._1))), choices.tail.map(y => Opt(trueF, ElifStatement(One(featureToCExpr(y._1)), One(CompoundStatement(handleWhileStatements(replaceOptAndId(Opt(trueF, WhileStatement(expr, One(y._2))), y._1), y._1)))))), None)))
          }
        }
      case k =>
        List()
    }
  }

  def handleDoStatements(opt: Opt[Statement], currentFeature: FeatureExpr = trueF): List[Opt[Statement]] = {
    opt.entry match {
      case d@DoStatement(expr, conditional) =>
        if (!opt.feature.equivalentTo(trueF)) {
          noOfStatementsVariable = noOfStatementsVariable + 1
          List((Opt(trueF, IfStatement(One(featureToCExpr(opt.feature)), One(CompoundStatement(handleDoStatements(replaceFeature(Opt(trueF, d), opt.feature), opt.feature))), List(), None))))
        } else {
          conditional match {
            case One(statement) =>
              if (containsIdUsage(expr)) {
                val feat = computeIdUsageFeatures(expr)
                val result = feat.map(x => {
                  Opt(trueF, IfStatement(One(featureToCExpr(x)), One(CompoundStatement(handleDoStatements(Opt(trueF, WhileStatement(convertIdUsagesFromDefuse(expr, x), conditional)), x))), List(), None))
                })
                if (!result.isEmpty) {
                  noOfStatementDuplications = noOfStatementDuplications - 1 + result.size
                }
                result
              } else {
                List(transformRecursive(opt, currentFeature))
              }
            case c@Choice(ft, one, second) =>
              val choices = choiceToTuple(c)
              if (!choices.isEmpty) {
                noOfStatementDuplications = noOfStatementDuplications - 1 + choices.size
              }
              List(Opt(trueF, IfStatement(One(featureToCExpr(choices.head._1)), One(CompoundStatement(handleDoStatements(Opt(trueF, WhileStatement(expr, One(choices.head._2))), choices.head._1))), choices.tail.map(y => Opt(trueF, ElifStatement(One(featureToCExpr(y._1)), One(CompoundStatement(handleDoStatements(Opt(trueF, WhileStatement(expr, One(y._2))), y._1)))))), None)))
          }
        }
      case k =>
        List()
    }
  }

  def handleForStatements(opt: Opt[Statement], currentFeature: FeatureExpr = trueF): List[Opt[Statement]] = {
    opt.entry match {
      case f@ForStatement(expr1, expr2, expr3, conditional) =>
        if (!opt.feature.equivalentTo(trueF)) {
          noOfStatementsVariable = noOfStatementsVariable + 1
          List((Opt(trueF, IfStatement(One(featureToCExpr(opt.feature)), One(CompoundStatement(handleForStatements(replaceFeature(Opt(trueF, f), opt.feature), opt.feature))), List(), None))))
        } else {
          conditional match {
            case One(statement) =>
              // TODO: check expr2 and expr3
              val test = computeNextRelevantFeatures(f)
              if (containsIdUsage(expr1) /*|| containsIdUsage(expr2) || containsIdUsage(expr3)*/ ) {
                val feat = computeIdUsageFeatures(expr1)
                val result = feat.map(x => {
                  if (!currentFeature.equivalentTo(trueF) && currentFeature.implies(x).isTautology()) {
                    transformRecursive(replaceOptAndId(opt, x))
                  } else {
                    Opt(trueF, IfStatement(One(featureToCExpr(x)), One(CompoundStatement(handleForStatements(Opt(trueF, ForStatement(convertIdUsagesFromDefuse(expr1, x), convertIdUsagesFromDefuse(expr2, x), convertIdUsagesFromDefuse(expr3, x), conditional)), x))), List(), None))
                  }
                })
                if (!result.isEmpty) {
                  noOfStatementDuplications = noOfStatementDuplications - 1 + result.size
                }
                result
              } else {
                List(transformRecursive(opt, currentFeature))
              }
            case c@Choice(ft, one, second) =>
              val choices = choiceToTuple(c)
              if (!choices.isEmpty) {
                noOfStatementDuplications = noOfStatementDuplications - 1 + choices.size
              }
              List(Opt(trueF, IfStatement(One(featureToCExpr(choices.head._1)), One(CompoundStatement(handleForStatements(Opt(trueF, ForStatement(expr1, expr2, expr3, One(choices.head._2))), choices.head._1))), choices.tail.map(y => Opt(trueF, ElifStatement(One(featureToCExpr(y._1)), One(CompoundStatement(handleForStatements(Opt(trueF, ForStatement(expr1, expr2, expr3, One(y._2))), y._1)))))), None)))
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
        if (optFunction.feature.equals(trueF)) {
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
              tmpResult.copy(entry = tmpResult.entry.copy(stmt = transformRecursive(tmpResult.entry.stmt)))
              //transformRecursive(filterOptsByFeature(convertId(replaceFeatureByTrue(optFunction, optFunction.feature), x.&(optFunction.feature), defuse), x), env, defuse)
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
            List(transformRecursive(optFunction))
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
              transformRecursive(filterOptsByFeature(convertId(tempOpt, x), x))
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
            List(tmpResult.copy(entry = tmpResult.entry.copy(stmt = transformRecursive(tmpResult.entry.stmt, optFunction.feature))))
          }
        }
      case nfd: NestedFunctionDef =>
        if (optFunction.feature.equals(trueF)) {
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
              tmpResult.copy(entry = tmpResult.entry.copy(stmt = transformRecursive(tmpResult.entry.stmt)))
              //transformRecursive(filterOptsByFeature(convertId(replaceFeatureByTrue(optFunction, optFunction.feature), x.&(optFunction.feature), defuse), x), env, defuse)
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
            List(transformRecursive(optFunction))
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
              transformRecursive(filterOptsByFeature(convertId(tempOpt, x), x))
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
            List(tmpResult.copy(entry = tmpResult.entry.copy(stmt = transformRecursive(tmpResult.entry.stmt))))
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