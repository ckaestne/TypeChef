package de.fosd.typechef.crefactor.backend.refactor

import de.fosd.typechef.parser.c._
import de.fosd.typechef.crewrite.{ConditionalNavigation, ASTEnv, ASTNavigation}
import java.util
import scala._
import de.fosd.typechef.parser.c.PostfixExpr
import de.fosd.typechef.parser.c.Id
import de.fosd.typechef.parser.c.AutoSpecifier
import de.fosd.typechef.parser.c.AtomicNamedDeclarator
import de.fosd.typechef.parser.c.InlineSpecifier
import de.fosd.typechef.parser.c.VolatileSpecifier
import scala.Some
import de.fosd.typechef.parser.c.FunctionDef
import de.fosd.typechef.parser.c.ExternSpecifier
import de.fosd.typechef.parser.c.VoidSpecifier
import de.fosd.typechef.parser.c.ParameterDeclarationD
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.parser.c.FunctionCall
import de.fosd.typechef.parser.c.RestrictSpecifier
import de.fosd.typechef.parser.c.RegisterSpecifier
import de.fosd.typechef.parser.c.Pointer
import de.fosd.typechef.parser.c.StaticSpecifier
import de.fosd.typechef.parser.c.Declaration
import de.fosd.typechef.parser.c.ConstSpecifier


/**
 * Implements the process of extracting a function.
 */
object ExtractFunction extends ASTNavigation with ConditionalNavigation {

  /**
   * Retrieves if selected statements are eligable for extract function
   */
  def isEligableForExtraction(selection: List[Opt[_]], astEnv: ASTEnv): Boolean = {
    // First Step - check parent function
    val parentFunction = getParentFunction(selection, astEnv)
    parentFunction match {
      case null => return false
      case _ =>
    }
    val condComplete = isConditionalComplete(selection, parentFunction, astEnv)

    if (!condComplete)
      return false

    true
  }

  /**
   * Retrieves the parent function of the selection. Returns null if not definied in an function or different functions.
   */
  def getParentFunction(selection: List[Opt[_]], env: ASTEnv): FunctionDef = {
    var funcDef: FunctionDef = null
    for (entry <- selection) {
      findPriorASTElem[FunctionDef](entry, env) match {
        case Some(f) => {
          if (funcDef == null) {
            funcDef = f
          } else if (!f.eq(funcDef)) {
            return null
          }
        }
        case none => return null
      }
    }
    funcDef
  }


  /**
   * Retrieves all used ids of the selection.
   */
  def getAllUsedIds(selection: List[Opt[_]]): List[Id] = {
    var result = List[Id]()
    for (entry <- selection) {
      result = result ::: filterASTElems[Id](entry)
    }
    result
  }

  /**
   * Checks if id is declared in the selection.
   */
  private def idIsDeclaredInSelection(id: Id, usedIds: List[Id], defuse: util.IdentityHashMap[Id, List[Id]]): Boolean = {
    val decl = Helper.findDecl(defuse, id)
    usedIds.foreach(x => if (x.eq(decl)) return true)
    false
  }

  /**
   * Retrieves a parameter list.
   */
  def getParameterIds(usedIds: List[Id], defuse: util.IdentityHashMap[Id, List[Id]]): Set[Id] = {
    usedIds.filter(p => !idIsDeclaredInSelection(p, usedIds, defuse)).toSet
  }

  /**
   * Retrieves all externally referenced ids.
   */
  def getAllExternallyReferencedIds(usedIds: List[Id], defUse: util.IdentityHashMap[Id, List[Id]], astEnv: ASTEnv): List[Id] = {
    // Filter function calls
    val ids = usedIds.filter(x => astEnv.parent(x) match {
      case PostfixExpr(_, FunctionCall(_)) => false
      case _ => true
    })

    /**
     * Removes all locally used variables.
     */
    def filterLocalIds(usedIds: List[Id], defUse: util.IdentityHashMap[Id, List[Id]], result: List[Id] = List[Id]()): List[Id] = {
      if (usedIds.isEmpty) {
        return List[Id]()
      }
      val decl = Helper.findDecl(defUse, usedIds.head)
      val defUseIds = decl :: defUse.get(decl)
      val used = usedIds.diff(defUseIds).diff(List(usedIds.head))
      var filtered = result ::: defUseIds.diff(usedIds)
      if (!used.isEmpty)
        filtered = filterLocalIds(used, defUse, filtered)
      filtered
    }
    // TODO Check Redecls
    filterLocalIds(ids, defUse)
  }

  /**
   * Conditional complete?
   */
  def isConditionalComplete(selection: List[Opt[_]], parentFunction: FunctionDef, astEnv: ASTEnv): Boolean = {
    println("parent" + parentFunction)
    selection.foreach(x => {
      println("prev " + x)
      // println(astEnv.previous(x))
      // println("parent" + astEnv.parent(x))
    })
    if (!selectionIsConditional(selection)) {
      // no variable configuration -> conditional complete
      return true
    }
    val fExpr = filterAllFeatureExpr(selection).toSet
    println("fexpr" + filterAllFeatureExpr(selection).toSet)
    if (!(fExpr.size > 1)) {
      // only one and the same feature -> conditonal complete
      return true
    }

    val expr1 = selection.head
    val expr2 = selection.last

    println("1" + expr1)
    println("2" + expr2)
    println("nested1? " + expr1.feature.implies(expr2.feature))
    println("nested2? " + expr2.feature.implies(expr1.feature))
    println("not " + expr2.feature.equiv(expr1.feature))

    false
  }

  /**
   * Selection is conditonal?
   */
  def selectionIsConditional(selection: List[Opt[_]]): Boolean = {
    selection.par.foreach(x => if (isVariable(x)) return true)
    false
  }

  /**
   * Generates the required specifiers.
   */
  def generateSpecifiers(funcDef: FunctionDef, astEnv: ASTEnv /* , typeSpecifier: Opt[Specifier] = Opt(FeatureExprFactory.True, VoidSpecifier()) */): List[Opt[Specifier]] = {
    var specifiers = List[Opt[Specifier]]()
    specifiers = Opt(parentOpt(funcDef, astEnv).feature, VoidSpecifier()) :: specifiers

    // preserv specifiers from function definition except type specifiers
    for (specifier <- funcDef.specifiers) {
      specifier.entry match {
        case InlineSpecifier() => specifiers = specifier :: specifiers
        case AutoSpecifier() => specifiers = specifier :: specifiers
        case RegisterSpecifier() => specifiers = specifier :: specifiers
        case VolatileSpecifier() => specifiers = specifier :: specifiers
        case ExternSpecifier() => specifiers = specifier :: specifiers
        case ConstSpecifier() => specifiers = specifier :: specifiers
        case RestrictSpecifier() => specifiers = specifier :: specifiers
        case StaticSpecifier() => specifiers = specifier :: specifiers
        case _ =>
      }
    }
    specifiers
  }

  /**
   * Genertates the declarator.
   */
  def generateDeclarator(name: String /*, pointer: List[Opt[Pointer]] = List[Opt[Pointer]]()*/ , extensions: List[Opt[DeclaratorExtension]] = List[Opt[DeclaratorExtension]]()): Declarator = {
    AtomicNamedDeclarator(List[Opt[Pointer]](), Id(name), extensions)
  }

  def generateCompoundStatement(statements: List[Statement], astEnv: ASTEnv): CompoundStatement = {
    var statementElements = List[Opt[_]]()

    // TODO @ AST Position
    for (element <- statements) {
      val parent = parentOpt(element, astEnv)
      if (parent.entry.isInstanceOf[Statement]) {
        statementElements = parentOpt(element, astEnv) :: statementElements
      }
    }

    def makePointer(expr: Expr): Expr = {
      expr match {
        case a@AssignExpr(target, operation, source) => {
          a.copy(target = makePointer(target), source = makePointer(source))
        }
        case i@Id(name) => PointerDerefExpr(i)
        case p@PointerDerefExpr(expr) => p.copy(castExpr = makePointer(expr))
        case p2@PointerCreationExpr(expr) => PointerDerefExpr(p2)
        case _ => {
          println("missed expr " + expr)
          expr
        }
      }

    }
    var compundStmtElements = List[Opt[Statement]]()
    for (optStatement <- statementElements) {
      optStatement.entry match {
        case e@ExprStatement(expr) => compundStmtElements = Opt[Statement](optStatement.feature, optStatement.entry.asInstanceOf[ExprStatement].copy(expr = makePointer(expr))) :: compundStmtElements
        case _ => println("Class " + optStatement.entry)
      }
    }

    CompoundStatement(compundStmtElements)
  }

  /**
   * Generates the function definition.
   */
  def generateFuncDef(specifiers: List[Opt[Specifier]], declarator: Declarator /*, oldStyleParameters: List[Opt[OldParameterDeclaration]] = List[Opt[OldParameterDeclaration]]() */ , stmt: CompoundStatement): FunctionDef = {
    FunctionDef(specifiers, declarator, List[Opt[OldParameterDeclaration]](), stmt)
  }

  /**
   * Generates the opt node for the ast.
   */
  def generateFuncOpt(oldFunc: FunctionDef, newFunc: FunctionDef, env: ASTEnv): Opt[FunctionDef] = {
    // TODO Extended Features
    Opt[FunctionDef](parentOpt(oldFunc, env).feature, newFunc)
  }

  /**
   * Inserts the extracted function in the ast.
   */
  def insertNewFunction(oldFunc: FunctionDef, newFunc: Opt[FunctionDef], selection: List[Opt[_]], ast: AST, env: ASTEnv): AST = {
    var refactoredAST = Helper.insertInAstBeforeTD(ast, parentOpt(oldFunc, env), newFunc)
    // TODO External Vars
    val functionCall = Opt[ExprStatement](newFunc.feature, ExprStatement(PostfixExpr(Id(newFunc.entry.getName), FunctionCall(ExprList(List[Opt[Expr]]())))))
    refactoredAST = Helper.replaceInAST(refactoredAST, selection.head, functionCall)
    selection.foreach(x => refactoredAST = Helper.removeFromAST(refactoredAST, x))

    refactoredAST

  }

  /**
   * Genereates the parameter.
   */
  def generateParameter(funcDef: FunctionDef, params: Set[Id], astEnv: ASTEnv, defUse: util.IdentityHashMap[Id, List[Id]]): List[Opt[DeclaratorExtension]] = {

    def generateParameterDecl(param: Id): Opt[ParameterDeclarationD] = {

      /**
       * Generates the init declaration for variables declared in the method body.
       */
      def generateInit(decl: Declaration): Declarator = {
        // make pointer
        var pointer = List[Opt[Pointer]]()
        for (declSpecs <- decl.declSpecs) {
          pointer = pointer ::: List[Opt[Pointer]](Opt(declSpecs.feature, Pointer(List[Opt[Specifier]]())))
        }
        for (declInit <- decl.init) {
          pointer = pointer ::: declInit.entry.declarator.pointers
        }
        AtomicNamedDeclarator(pointer, Id(param.name), List[Opt[DeclaratorExtension]]())
      }

      val decl = findPriorASTElem[Declaration](param, astEnv)
      decl match {
        case Some(_) => Opt(parentOpt(funcDef, astEnv).feature, ParameterDeclarationD(decl.get.declSpecs, generateInit(decl.get)))
        case none => {
          val parameterDecl = findPriorASTElem[ParameterDeclarationD](param, astEnv)
          // TODO Pointer
          Opt(parentOpt(funcDef, astEnv).feature, parameterDecl.get)
        }
      }

    }

    var decls = List[Opt[ParameterDeclaration]]()
    params.foreach(id => {
      decls = generateParameterDecl(Helper.findDecl(defUse, id)) :: decls
    })
    List[Opt[DeclaratorExtension]](Opt(parentOpt(funcDef, astEnv).feature, DeclParameterDeclList(decls)))
  }


}
