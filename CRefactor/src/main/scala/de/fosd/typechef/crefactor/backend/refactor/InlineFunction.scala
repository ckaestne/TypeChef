package de.fosd.typechef.crefactor.backend.refactor

import de.fosd.typechef.crefactor._
import backend.ASTSelection
import de.fosd.typechef.parser.c._
import de.fosd.typechef.crefactor.frontend.util.Selection
import de.fosd.typechef.parser.c.FunctionDef
import de.fosd.typechef.parser.c.FunctionCall
import de.fosd.typechef.parser.c.Id
import de.fosd.typechef.crewrite.ASTEnv
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

  def isAvailable(ast: AST, astEnv: ASTEnv, selection: Selection): Boolean = !getAvailableIdentifiers(ast, astEnv, selection).isEmpty

  def isFunctionCall(morph: Morpheus, id: Id): Boolean = {
    parentAST(id, morph.getASTEnv) match {
      case p: PostfixExpr => true
      case _ => false
    }
  }

  /**
   * Perform the actual inlining.
   *
   * @param morph the morph environment
   * @param id the function's identifier
   * @param rename indicates if variables should be renamed
   * @return the refactored ast
   */
  def inline(morph: Morpheus, id: Id, rename: Boolean, once: Boolean = false): AST = {
    val divided = divideCallDeclDef(id, morph)
    val calls = divided._1
    val decl = divided._2
    val defs = divided._3
    val callExpr = divided._4

    if (defs.isEmpty) assert(false, "No valid function definition found.") // Included because of linked functions

    // Do inlining.
    // TODO Inline once
    val refactoredAST = calls.foldLeft(morph.getAST)((workingAST, call) => inlineFuncCall(workingAST, new Morpheus(workingAST), call, defs, rename))

    // Remove inlined function stmt's declaration and definitions
    val removedDecls = decl.foldLeft(refactoredAST)((workingAST, x) => removeFromAST(workingAST, x))
    val removedDefs = defs.foldLeft(removedDecls)((workingAST, x) => removeFromAST(workingAST, x))
    removedDefs
  }

  private def divideCallDeclDef(callId: Id, morph: Morpheus): (List[Opt[Statement]], List[Opt[_]], List[Opt[FunctionDef]], List[Opt[_]]) = {
    var calls = List[Opt[Statement]]()
    var decl = List[Opt[_]]()
    var defs = List[Opt[FunctionDef]]()
    var callExpr = List[Opt[_]]()

    // TODO Refactor
    getAllConnectedIdentifier(callId, morph.getDeclUseMap(), morph.getUseDeclMap).foreach(id => {
      val parent = parentOpt(id, morph.getASTEnv)
      parent.entry match {
        case p: Statement => calls ::= parent.asInstanceOf[Opt[Statement]]
        case f: FunctionDef => defs ::= parent.asInstanceOf[Opt[FunctionDef]]
        // case n: NestedFunctionDef => defs = n :: defs
        case iI: InitDeclaratorI =>
          iI.i match {
            case None => decl ::= parentOpt(parent, morph.getASTEnv)
            case _ => calls ::= parentOpt(parent, morph.getASTEnv).asInstanceOf[Opt[DeclarationStatement]]
          }
        case iE: InitDeclaratorE => decl ::= parentOpt(parent, morph.getASTEnv)
        case e: Expr => callExpr ::= parent // TODO Inline them!
        case _ => assert(false, "Invalid function found!")
      }
    })

    (calls, decl, defs, callExpr)
  }

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

  /*
   * Retrieves if there are bad conditional return during control flow.
   */
  private def hasBadReturns(func: FunctionDef, morph: Morpheus): Boolean = {

    def codeAfterStatement(feature: FeatureExpr, opt: Opt[_]): Boolean = {
      val next = nextOpt(opt, morph.getASTEnv)
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
          val parent = parentOpt(statement, morph.getASTEnv)
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
    var workingCallCompStmt = getCallCompStatement(call, morph.getASTEnv)

    // TODO Handling for other occurences
    // TODO Refactor for further abstraction
    parentAST(call.entry, morph.getASTEnv) match {
      case c: CompoundStatement =>
        workingCallCompStmt = funcDefs.foldLeft(workingCallCompStmt)((workingStatement, funcDef) =>
          funcDef match {
            case f: Opt[FunctionDef] => inlineFuncDefInCompStmt(workingStatement, morph, call, f, rename)
            case _ =>
              assert(false, "Missed case!")
              null
          })
        // Remove stmt
        workingCallCompStmt = removeFromAST(workingCallCompStmt, call)
      case i: IfStatement =>
        workingCallCompStmt = funcDefs.foldLeft(workingCallCompStmt)((workingStatement, funcDef) =>
          funcDef match {
            case f: Opt[FunctionDef] => inlineFuncDefInIfStmt(workingStatement, i, morph, call, f, rename)
            case _ =>
              assert(false, "Missed case!")
              null
          })
        // Remove stmt
        workingCallCompStmt = removeFromAST(workingCallCompStmt, parentOpt(i, morph.getASTEnv))
      case _ => println("forgotten")
    }

    val parentFunc = parentOpt(getCallCompStatement(call, morph.getASTEnv), morph.getASTEnv)
    parentFunc.entry match {
      case f: FunctionDef => replaceInASTOnceTD(ast, parentFunc, parentFunc.copy(entry = f.copy(stmt = workingCallCompStmt)))
      case _ =>
        assert(false, "Somethings bad happend - i am going to cry.")
        ast
    }
  }

  private def getCallCompStatement(call: Opt[Statement], astEnv: ASTEnv): CompoundStatement =
    findPriorASTElem[CompoundStatement](call.entry, astEnv) match {
      case Some(entry) => entry
      case _ => null
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
      // TODO ReplaceStrategy
      replaceInASTOnceTD(cStatement.asInstanceOf[AST], statement, call.copy(feature, declStatement.copy(rDecl))).asInstanceOf[CompoundStatement]
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

  private def inlineFuncDefInCompStmt(ccStatement: CompoundStatement, morph: Morpheus, call: Opt[Statement], funcDef: Opt[FunctionDef], rename: Boolean): CompoundStatement = {
    if (!isValidFuncDef(funcDef, call, ccStatement, morph)) return ccStatement
    var workingStatement = ccStatement

    val idsToRename = getIdentifierToRename(funcDef.entry, call.entry, workingStatement, morph)
    if (!rename && !idsToRename.isEmpty) assert(false, "Can not inline - variables need to be renamed")

    val renamed = renameShadowedIds(idsToRename, funcDef, call, morph)
    val initializer = getInitializers(call, renamed._2)

    // Apply feature environment
    val statements = applyFeaturesOnInlineStmts(renamed._1, call)

    // find return statements
    val returnStmts = getReturnStmts(statements)

    // instert in ast
    workingStatement = insertInAstBefore(workingStatement, call, initializer)
    workingStatement = insertInAstBefore(workingStatement, call, statements)

    // insert return statements
    workingStatement = assignReturnValue(workingStatement, call, returnStmts)
    workingStatement
  }

  private def inlineFuncDefInIfStmt(compStmt: CompoundStatement, ifStmt: IfStatement, morpheus: Morpheus, call: Opt[Statement], funcDef: Opt[FunctionDef], rename: Boolean): CompoundStatement = {
    if (!isValidFuncDef(funcDef, call, compStmt, morpheus)) return compStmt

    var workingStatement = compStmt

    val idsToRename = getIdentifierToRename(funcDef.entry, call.entry, workingStatement, morpheus)
    if (!rename && !idsToRename.isEmpty) assert(false, "Can not inline - variables need to be renamed")

    val renamed = renameShadowedIds(idsToRename, funcDef, call, morpheus)
    val initializer = getInitializers(call, renamed._2)
    val statements = applyFeaturesOnInlineStmts(renamed._1, call)
    val returnStmts = getReturnStmts(statements)

    val insert = ExprStatement(CompoundStatementExpr(CompoundStatement(initializer ::: statements)))
    val ifOpt = parentOpt(ifStmt, morpheus.getASTEnv)
    workingStatement = insertInAstBefore(compStmt, ifOpt, ifOpt.copy(feature = call.feature.and(ifOpt.feature), entry = ifStmt.copy(thenBranch = One(insert))))
    // TODO Return Statement
    workingStatement
  }

  private def isDeclared(id: Id, env: Env, statement: CompoundStatement, morpheus: Morpheus): Boolean = {

    def checkOne(one: Conditional[(CType, DeclarationKind, Int)], recursive: Boolean = false): Boolean = {
      one match {
        case One((CUnknown(_), _, _)) =>
          if (!recursive) (false || checkConditional(morpheus.getEnv(statement.innerStatements.last.entry).varEnv.lookup(id.name), true))
          else false
        case One((CFunction(_, _), _, _)) => parentAST(id, morpheus.getASTEnv) match {
          case PostfixExpr(_, FunctionCall(_)) => false
          case _ => parentOpt(id, morpheus.getASTEnv).entry match {
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

  private def isValidFuncDef(fDef: Opt[FunctionDef], call: Opt[Statement], ccStatement: CompoundStatement, morph: Morpheus): Boolean = {
    if (!(fDef.feature.equivalentTo(FeatureExprFactory.True) || fDef.feature.implies(call.feature).isTautology())) return false
    // stmt's feature does not imply funcDef's feature -> no need to inline this def at this position
    assert(!isRecursive(fDef.entry), "Can not inline - method is recursive.")
    assert(!hasBadReturns(fDef.entry, morph), "Can not inline - method has bad return statements")
    true
  }

  private def getIdentifierToRename(funcDef: FunctionDef, call: AST, compoundStmt: CompoundStatement, morph: Morpheus) = filterAllASTElems[Id](funcDef).filter(id => isDeclared(id, morph.getEnv(call).asInstanceOf[Env], compoundStmt: CompoundStatement, morph: Morpheus))

  private def getInitializers(call: Opt[Statement], parameters: List[Opt[DeclaratorExtension]]): List[Opt[DeclarationStatement]] = {
    def generateInitializer(parameter: Opt[DeclaratorExtension], exprList: List[Opt[Expr]]): List[Opt[DeclarationStatement]] = {
      var exprs = exprList
      parameter.entry match {
        case p: DeclParameterDeclList =>
          p.parameterDecls.flatMap(pDecl => {
            val expr = exprs.head
            val feature = expr.feature.and(parameter.feature)

            if (!feature.isSatisfiable()) None
            else {
              exprs = exprs.tail
              pDecl.entry match {
                case p: ParameterDeclarationD =>
                  val spec = p.specifiers.map(specifier => specifier.copy(feature = feature.and(specifier.feature)))
                  Some(Opt(feature, DeclarationStatement(Declaration(spec, List(Opt(feature, InitDeclaratorI(p.decl, List(), Some(Initializer(None, expr.entry)))))))))
                case _ =>
                  assert(false, "Can not init parameters!")
                  None
              }
            }
          })
        case _ =>
          assert(false, "Can not init parameters!")
          null
      }
    }
    // TODO Safe solution -> features
    val exprList = filterASTElems[FunctionCall](call).head.params.exprs
    parameters.flatMap(parameter => {
      if (exprList.isEmpty) None
      else generateInitializer(parameter, exprList) match {
        case null => None
        case x => Some(x)
      }
    }).foldLeft(List[Opt[DeclarationStatement]]())((result, entry) => result ::: entry)
  }

  private def renameShadowedIds(idsToRename: List[Id], funcDef: Opt[FunctionDef], call: Opt[Statement], morph: Morpheus): (List[Opt[Statement]], List[Opt[DeclaratorExtension]]) = {
    val statements = idsToRename.foldLeft(funcDef.entry.stmt.innerStatements)((statement, id) => replaceInAST(statement, id, id.copy(name = generateValidName(id, call, morph))).asInstanceOf[List[Opt[Statement]]])
    val parameters = idsToRename.foldLeft(funcDef.entry.declarator.extensions)((extension, id) => replaceInAST(extension, id, id.copy(name = generateValidName(id, call, morph))).asInstanceOf[List[Opt[DeclaratorExtension]]])
    (statements, parameters)
  }

  private def applyFeaturesOnInlineStmts(statements: List[Opt[Statement]], call: Opt[Statement]): List[Opt[Statement]] = {
    statements.flatMap(statement => {
      val feature = statement.feature.and(call.feature)
      feature.isSatisfiable() match {
        case true => Some(statement, statement.copy(feature = feature))
        case _ => None
      }
    }).foldLeft(statements)((stmt, entry) => replaceInAST(stmt, entry._1, entry._2))
  }

  private def getReturnStmts(statements: List[Opt[Statement]]): List[Opt[ReturnStatement]] = {
    filterAllOptElems(statements).flatMap(opt => {
      opt.entry match {
        case r: ReturnStatement => Some(opt.asInstanceOf[Opt[ReturnStatement]])
        case _ => None
      }
    })
  }
}
