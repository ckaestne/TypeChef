package de.fosd.typechef.crefactor.backend.refactor

import de.fosd.typechef.crefactor._
import backend.ASTSelection
import de.fosd.typechef.parser.c._
import de.fosd.typechef.crefactor.frontend.util.Selection
import de.fosd.typechef.parser.c.FunctionDef
import de.fosd.typechef.parser.c.FunctionCall
import de.fosd.typechef.parser.c.Id
import de.fosd.typechef.crewrite.{CASTEnv, ASTEnv}
import de.fosd.typechef.conditional.{Choice, Conditional, One, Opt}
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory}
import de.fosd.typechef.typesystem.{CFunction, CType, DeclarationKind, CUnknown}

/**
 * Implements the technique of inlining a function
 */
object InlineFunction extends ASTSelection with Refactor {

  def getSelectedElements(ast: AST, astEnv: ASTEnv, selection: Selection): List[AST] = {
    val functions = (filterASTElems[FunctionDef](ast) ::: filterASTElems[FunctionCall](ast)
      ::: filterAllASTElems[NestedFunctionDef](ast)).filter(x => isSelected(x, astEnv, selection))
    filterASTElementsForFile(functions, selection.getFilePath).sortWith(comparePosition)
  }

  def getAvailableIdentifiers(ast: AST, astEnv: ASTEnv, selection: Selection): List[Id] = {
    val ids = getSelectedElements(ast, astEnv, selection).map(x => getFunctionIdentifier(x, astEnv))
    ids.sortWith(comparePosition)
  }

  def isFunctionCall(morph: Morpheus, id: Id): Boolean = {
    parentAST(id, morph.getASTEnv()) match {
      case p: PostfixExpr => true
      case _ => false
    }
  }

  /*
   * Retrieves if there are bad conditional return during control flow.
   */
  private def hasBadReturns(func: FunctionDef, morph: Morpheus): Boolean = {

    def codeAfterStatement(feature: FeatureExpr, opt: Opt[_]): Boolean = {
      val next = nextOpt(opt, morph.getASTEnv())
      next match {
        case null => false
        case _ =>
          if (((feature.equivalentTo(FeatureExprFactory.True) || feature.implies(next.feature).isTautology()))) true
          else codeAfterStatement(feature, next)
      }
    }

    def getStatementOpt(opt: Opt[_], statements: List[Opt[Statement]]): Opt[_] = {
      statements.foreach(statement => if (filterAllOptElems(statement).exists(x => opt.eq(x))) return statement)
      null
    }

    def getStatementOpts(opt: Opt[_], statement: Product, statements: List[Opt[_]] = List[Opt[_]]()): List[Opt[_]] = {
      var result = statements
      val filtered = filterAllOptElems(statement)
      if (filtered.length > 1) filtered.foreach((stmt =>
        if (filterAllOptElems(stmt).exists(x =>
          opt.eq(x))) result :::= stmt :: getStatementOpts(opt, stmt.entry.asInstanceOf[Product], result)))
      result
    }

    filterASTElems[ReturnStatement](func).exists(statement =>
      statement match {
        case r: ReturnStatement =>
          val parent = parentOpt(statement, morph.getASTEnv())
          val outerStatement = getStatementOpt(parent, func.stmt.innerStatements)
          getStatementOpts(parent, outerStatement, List(outerStatement)).exists(statement => codeAfterStatement(parent.feature, statement))
        case _ => false
      }
    )
  }

  private def isRecursive(funcDef: FunctionDef): Boolean = {
    filterASTElems[PostfixExpr](funcDef).exists(expr => expr match {
      case PostfixExpr(Id(name), FunctionCall(_)) => name.equals(funcDef.getName)
      case _ => false
    })
  }

  private def isRecursive(funcDef: NestedFunctionDef): Boolean = {
    filterASTElems[PostfixExpr](funcDef).exists(expr => expr match {
      case PostfixExpr(Id(name), FunctionCall(_)) => name.equals(funcDef.getName)
      case _ => false
    })
  }

  private def inlineFuncCall(ast: AST, morph: Morpheus, call: Opt[Statement], funcDefs: List[Opt[_]], rename: Boolean): AST = {
    var workingTree = ast
    // TODO Optimize for performance - workaround because of changing statements
    val astEnv = CASTEnv.createASTEnv(workingTree)
    var callCompoundStmt = getCallCompStatement(call, astEnv)

    funcDefs.foreach(funcDef => {
      funcDef match {
        case f: Opt[FunctionDef] => callCompoundStmt = inlineDef(callCompoundStmt, morph, call, f, rename)
        case n: Opt[NestedFunctionDef] => workingTree // TODO
        case _ => assert(false, "Missed case!")
      }
    })

    // Remove call
    callCompoundStmt = removeFromAST(callCompoundStmt, call)

    val parent = parentOpt(call, astEnv)

    // TODO If/While Statements
    parent.entry match {
      case f: FunctionDef => workingTree = replaceInASTOnceTD(ast, parent, parent.copy(entry = f.copy(stmt = callCompoundStmt)))
      case x => assert(false, "Missed case!" + x)
    }

    workingTree
  }

  private def getCallCompStatement(call: Opt[Statement], astEnv: ASTEnv): CompoundStatement = {
    findPriorASTElem[CompoundStatement](call, astEnv) match {
      case Some(entry) => entry
      case _ =>
        assert(false, "Could not find Statement")
        null
    }
  }

  private def assignReturnValue(statement: CompoundStatement, call: Opt[Statement], returnStmts: List[Opt[ReturnStatement]]): CompoundStatement = {
    // TODO deep inspect
    var workingStatement = statement

    def initReturnStatement(decl: Declaration, statement: Opt[ReturnStatement], declStatement: DeclarationStatement, cStatement: CompoundStatement, call: Opt[Statement]): CompoundStatement = {
      var initDecls = List[Opt[InitDeclarator]]()
      decl.init.foreach(init => {
        val feature = init.feature.and(statement.feature)
        if (!feature.isSatisfiable()) return cStatement
        init.entry match {
          case i@InitDeclaratorI(_, _, Some(Initializer(label, expr))) => initDecls ::= Opt(feature, i.copy(i = Some(Initializer(label, statement.entry.expr.get))))
          case _ => assert(false, "Pattern matching not exhaustive")
        }
      })
      val declSpecs = decl.declSpecs.map(spec => {
        val feature = spec.feature.and(statement.feature)
        if (!feature.isSatisfiable()) return cStatement
        spec.copy(feature = feature)
      })

      val feature = call.feature.and(statement.feature)
      if (!feature.isSatisfiable()) return cStatement
      val rDecl = decl.copy(declSpecs.reverse, initDecls.reverse)

      replaceInAST(cStatement.asInstanceOf[AST], statement, call.copy(feature, declStatement.copy(rDecl))).asInstanceOf[CompoundStatement]
    }

    def assignStatement(statement: Opt[Statement], cStatement: CompoundStatement, call: Opt[Statement], entry: Expr): CompoundStatement = {
      var wStatement = cStatement
      call.entry match {
        case ExprStatement(AssignExpr(t, o, _)) =>
          val feature = statement.feature.and(call.feature)
          if (feature.isSatisfiable())
          // TODO Ask Jörg for better solution -> manybu/oncetd <- wtf!?!!?!
            if (wStatement.innerStatements.exists(entry => entry.eq(statement))) wStatement = replaceInASTOnceTD(wStatement, statement, Opt(feature, ExprStatement(AssignExpr(t, o, entry))))
            else wStatement = replaceInAST(wStatement, statement, Opt(feature, ExprStatement(AssignExpr(t, o, entry))))
          else wStatement = removeFromAST(wStatement, statement)
        case _ => assert(false, "Missed Pattern!")
      }
      wStatement
    }

    def includeReturnStatement(returnStmts: List[Opt[Statement]], cStatement: CompoundStatement, call: Opt[Statement], assign: Boolean): CompoundStatement = {
      var wStatement = cStatement
      returnStmts.foreach(statement => statement.entry match {
        case ReturnStatement(Some(entry)) =>
          val feature = statement.feature.and(call.feature)
          if (assign) wStatement = assignStatement(statement, wStatement, call, entry)
          else if (feature.isSatisfiable()) wStatement = insertInAstBefore(wStatement, call, Opt(feature, ExprStatement(entry)))
        case _ => assert(false, "Missed Pattern!")
      })
      wStatement
    }

    call.entry match {
      case expr@ExprStatement(e) => e match {
        case p: PostfixExpr => workingStatement = includeReturnStatement(returnStmts, workingStatement, call, false)
        case a: AssignExpr => workingStatement = includeReturnStatement(returnStmts, workingStatement, call, true)
        case x => println("missed" + x)
      }
      case declStmt@DeclarationStatement(decl) => returnStmts.foreach(statement => workingStatement = initReturnStatement(decl, statement, declStmt, workingStatement, call))
      case _ => assert(false, "Pattern matching not exhaustive")
    }
    workingStatement
  }

  def generateValidName(id: Id, morph: Morpheus, scope: AST, appendix: Int = 1): String = {
    val newName = id.name + "_" + appendix
    if (isDeclaredVarInScope(morph, newName, id, scope)) generateValidName(id, morph, scope, (appendix + 1))
    else newName
  }

  private def inlineDef(ccStatement: CompoundStatement, morph: Morpheus, call: Opt[Statement], fDef: Opt[FunctionDef], rename: Boolean): CompoundStatement = {
    if (!(fDef.feature.equivalentTo(FeatureExprFactory.True) || fDef.feature.implies(call.feature).isTautology())) return ccStatement
    // call's feature does not imply funcDef's feature -> no need to inline this def at this position
    assert(!isRecursive(fDef.entry), "Can not inline - method is recursive.")
    assert(!hasBadReturns(fDef.entry, morph), "Can not inline - method has bad return statements")

    var workingStatement = ccStatement

    val funcDef: FunctionDef = fDef.entry
    val idsToRename = identifierToRename(funcDef, call.entry, workingStatement, morph)
    if (!rename && !idsToRename.isEmpty) assert(false, "Can not inline - variables need to be renamed")

    // rename ids
    var statements = idsToRename.foldLeft(funcDef.stmt.innerStatements)((statement, id) => replaceInAST(statement, id, id.copy(name = generateValidName(id, morph, ccStatement.innerStatements.last.entry))).asInstanceOf[List[Opt[Statement]]])
    val parameters = idsToRename.foldLeft(funcDef.declarator.extensions)((extension, id) => replaceInAST(extension, id, id.copy(name = generateValidName(id, morph, ccStatement.innerStatements.last.entry))).asInstanceOf[List[Opt[DeclaratorExtension]]])

    val exprList = filterASTElems[FunctionCall](call).head.params.exprs

    val inits = parameters.flatMap(parameter => {
      if (exprList.isEmpty) None
      else {
        val expr = exprList.head
        val feature = expr.feature.and(parameter.feature)

        if (!feature.isSatisfiable()) None
        else parameter.entry match {
          case p: DeclParameterDeclList =>
            val decl = p.parameterDecls.head.entry.asInstanceOf[ParameterDeclarationD].decl
            val specs = p.parameterDecls.head.entry.asInstanceOf[ParameterDeclarationD].specifiers
            Some(Opt(feature, DeclarationStatement(Declaration(specs, List(Opt(feature, InitDeclaratorI(decl, List(), Some(Initializer(None, expr.entry)))))))))
          case _ =>
            assert(false, "Can not init parameters!")
            None
        }
      }
    })

    // Apply feature environment
    statements = statements.flatMap(statement => {
      val feature = statement.feature.and(call.feature)
      feature.isSatisfiable() match {
        case true => Some(statement, statement.copy(feature = feature))
        case _ => None
      }
    }).foldLeft(statements)((stmt, entry) => replaceInAST(stmt, entry._1, entry._2))

    // find return statements
    val returnStmts = filterAllOptElems(statements).flatMap(opt => {
      opt.entry match {
        case r: ReturnStatement => Some(opt.asInstanceOf[Opt[ReturnStatement]])
        case _ => None
      }
    })

    // instert in ast
    workingStatement = insertInAstBefore(workingStatement, call, inits)
    workingStatement = insertInAstBefore(workingStatement, call, statements)

    // insert return statements
    workingStatement = assignReturnValue(workingStatement, call, returnStmts)
    workingStatement
  }

  private def isDeclared(id: Id, env: Env, statement: CompoundStatement, morpheus: Morpheus): Boolean = {

    def checkOne(one: Conditional[(CType, DeclarationKind, Int)], recursive: Boolean = false): Boolean = {
      one match {
        case One((CUnknown(_), _, _)) =>
          if (!recursive) (false || checkConditional(morpheus.getEnv(statement.innerStatements.last.entry).varEnv.lookup(id.name), true))
          else false
        case One((CFunction(_, _), _, _)) => parentAST(id, morpheus.getASTEnv()) match {
          case PostfixExpr(_, FunctionCall(_)) => false
          case _ => parentOpt(id, morpheus.getASTEnv()).entry match {
            case f: FunctionDef => false
            case _ => true
          }
        }
        case _ => true
      }
    }

    def checkConditional(conditional: Conditional[(CType, DeclarationKind, Int)], recursive: Boolean = false): Boolean = {
      conditional match {
        case c@Choice(feature, then, elseB) => checkConditional(then, recursive) || checkConditional(elseB, recursive)
        case o@One((_)) => checkOne(conditional, recursive)
      }
    }

    checkConditional(env.varEnv.lookup(id.name))

  }

  private def identifierToRename(funcDef: FunctionDef, call: AST, compoundStmt: CompoundStatement, morph: Morpheus): List[Id] = {
    filterAllASTElems[Id](funcDef).filter(id => isDeclared(id, morph.getEnv(call).asInstanceOf[Env], compoundStmt: CompoundStatement, morph: Morpheus))
  }

  /**
   * Perform the actual inlining.
   *
   * @param morph the morph environment
   * @param id the function's identifier
   * @param rename indicates if variables should be renamed
   * @return the refactored ast
   */
  def inline(morph: Morpheus, id: Id, rename: Boolean): AST = {

    /**
     * Divide calls and definitions
     */
    var calls = List[Opt[Statement]]()
    var decl = List[Opt[_]]()
    var defs = List[Opt[FunctionDef]]()

    findAllConnectedIds(id, morph.getDeclUseMap(), morph.getUseDeclMap).foreach(id => {
      val parent = parentOpt(id, morph.getASTEnv())
      parent.entry match {
        case p: Statement => calls ::= parent.asInstanceOf[Opt[Statement]]
        case f: FunctionDef => defs ::= parent.asInstanceOf[Opt[FunctionDef]]
        // case n: NestedFunctionDef => defs = n :: defs
        case iI: InitDeclaratorI =>
          iI.i match {
            case None => decl ::= parentOpt(parent, morph.getASTEnv())
            case _ => calls ::= parentOpt(parent, morph.getASTEnv()).asInstanceOf[Opt[DeclarationStatement]]
          }
        case iE: InitDeclaratorE => decl ::= parentOpt(parent, morph.getASTEnv())
        case x => assert(false, "Missed case. " + parent)
      }
    })

    var refactor = morph.getAST()

    /**
     * Do inlining.
     */
    calls.foreach(call => refactor = inlineFuncCall(refactor, new Morpheus(refactor), call, defs, rename))

    /**
     * Remove inlined function call's declaration and definitions
     */
    decl.foreach(x => refactor = removeFromAST(refactor, x))
    defs.foreach(x => refactor = removeFromAST(refactor, x))
    refactor
  }

  /**
  private def inlineFuncDef(compStmt: CompoundStatement, morph: Morpheus, call: Opt[Statement], fDef: Opt[FunctionDef], rename: Boolean): CompoundStatement = {
    if (!(fDef.feature.equivalentTo(FeatureExprFactory.True) || fDef.feature.implies(call.feature).isTautology())) return compStmt
    // call's feature does not imply funcDef's feature -> no need to inline this def at this position

    assert(!isRecursive(fDef.entry), "Can not inline - method is recursive.")
    assert(!hasBadReturns(fDef.entry, morph), "Can not inline - method has bad return statements")

    val idsToRename = identifierToRename(fDef.entry, call.entry, compStmt, morph)
    if (!rename && !idsToRename.isEmpty) assert(false, "Can not inline - variables need to be renamed")

    var statements = idsToRename.foldLeft(fDef.entry.stmt.innerStatements)((statement, id) => replaceInAST(statement, id, id.copy(name = generateValidName(id, morph, compStmt.innerStatements.last.entry))).asInstanceOf[List[Opt[Statement]]])
    val workingStatement = insertInAstBefore(compStmt, call, statements)

    workingStatement

  }

  private def inlineCall(ast: AST, morph: Morpheus, call: Opt[Statement], funcDefs: List[Opt[FunctionDef]], rename: Boolean): Morpheus = {
    var refactoredAST = ast

    var workingAST: AST = null

    call.entry match {
      case e: ExprStatement => workingAST = getCallCompStatement(call, morph.getASTEnv())
      case x => assert(false, "Inling currently not supported.")
    }

    workingAST = funcDefs.foldLeft(workingAST.asInstanceOf[CompoundStatement])((refactor, funcDef) => inlineFuncDef(refactor, morph, call, funcDef, rename))

    new Morpheus(refactoredAST)
  } */

  private def getFunctionIdentifier(function: AST, astEnv: ASTEnv): Id = {
    function match {
      case f: FunctionDef => f.declarator.getId
      case n: NestedFunctionDef => n.declarator.getId
      case c: FunctionCall => getFunctionIdentifier(astEnv.parent(c).asInstanceOf[AST], astEnv)
      case PostfixExpr(i@Id(_), _) => i
      case _ =>
        assert(false, function)
        null
    }
  }

  private def isSelected(element: AST, astEnv: ASTEnv, selection: Selection): Boolean = {
    element match {
      case f: FunctionDef => isInSelectionRange(f.declarator.getId, selection)
      case n: NestedFunctionDef => isInSelectionRange(n.declarator.getId, selection)
      case c: FunctionCall => isSelected(astEnv.parent(c).asInstanceOf[AST], astEnv, selection)
      case p: PostfixExpr => isInSelectionRange(p.p, selection)
      case _ =>
        assert(false, element)
        false
    }
  }
}
