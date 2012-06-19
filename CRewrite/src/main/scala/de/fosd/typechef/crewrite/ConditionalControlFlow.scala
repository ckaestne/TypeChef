package de.fosd.typechef.crewrite


import de.fosd.typechef.conditional._
import de.fosd.typechef.parser.c._
import java.util.IdentityHashMap
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}

// implements conditional control flow (cfg) on top of the typechef
// infrastructure
// at first sight the implementation of succ with a lot of private
// function seems overly complicated; however the structure allows
// also to implement pred
// consider the following points:

// the function definition an ast belongs to serves as the entry
// and exit node of the cfg, because we do not have special ast
// nodes for that, or we store everything in a ccfg itself with
// special nodes for entry and exit such as
// [1] http://soot.googlecode.com/svn/DUA_Forensis/src/dua/method/CFG.java

// normally pred succ are the same except for the following cases:
// 1. code in switch body that does not belong to a case block, but that has a label and
//    can be reached otherwise, e.g., switch (x) { l1: <code> case 0: .... }
// 2. infinite for loops without break or return statements in them, e.g.,
//    for (;;) { <code without break or return> }
//    this way we do not have any handle to jump into the for block from
//    its successors; we work around this issue by introducing a break statement
//    that always evaluates to true; for (;;) => for (;1;)

// for more information:
// iso/iec 9899 standard; committee draft
// [2] http://www.open-std.org/jtc1/sc22/wg14/www/docs/n1124.pdf

// TODO handling empty { } e.g., void foo() { { } }
// TODO support for (expr) ? (expr) : (expr);
// TODO analysis static gotos should have a label (if more labels must be unique according to feature expresssions)
// TODO analysis dynamic gotos should have a label
// TODO analysis: The expression of each case label shall be an integer constant expression and no two of
//                the case constant expressions in the same switch statement shall have the same value
//                after conversion.
// TODO analysis: There may be at most one default label in a switch statement.
// TODO analysis: we can continue this list only by looking at constrains in [2]


class CCFGCache {
  private val cache: IdentityHashMap[Product, List[AST]] = new IdentityHashMap[Product, List[AST]]()

  def update(k: Product, v: List[AST]) {
    cache.put(k, v)
  }

  def lookup(k: Product): Option[List[AST]] = {
    val v = cache.get(k)
    if (v != null) Some(v)
    else None
  }
}

trait ConditionalControlFlow extends ASTNavigation with ConditionalNavigation {

  private implicit def optList2ASTList(l: List[Opt[AST]]) = l.map(_.entry)
  private implicit def opt2AST(s: Opt[AST]) = s.entry
  private implicit def condition2AST(c: Conditional[AST]) = childAST(c)

  private val predCCFGCache = new CCFGCache()
  private val succCCFGCache = new CCFGCache()

  // equal annotated AST elements
  type IfdefBlock  = List[AST]
  type CCFGRes     = List[(FeatureExpr, AST)]

  // determines predecessor of a given element
  // results are cached for secondary evaluation
  def pred(source: Product, env: ASTEnv): List[AST] = {
    predCCFGCache.lookup(source) match {
      case Some(v) => v
      case None => {
        var oldres: List[AST] = List()
        val ctx = env.featureExpr(source)
        val resctx = List(FeatureExprFactory.False)
        var newres: List[AST] = predHelper(source, ctx, resctx, env)
        var changed = true

        while (changed) {
          changed = false
          oldres = newres
          newres = List()

          for (oldelem <- oldres) {
            var add2newres = List[AST]()
            oldelem match {

              case _: ReturnStatement if (!source.isInstanceOf[FunctionDef]) => add2newres = List()

              // a break statement shall appear only in or as a switch body or loop body
              // a break statement terminates execution of the smallest enclosing switch or
              // iteration statement (see standard [2])
              // so as soon as we hit a break statement and the break statement belongs to the same loop as we do
              // the break statement is not a valid predecessor
              case b: BreakStatement => {
                val b2b = findPriorASTElem2BreakStatement(b, env)

                assert(b2b.isDefined, "missing loop to break statement!")
                if (isPartOf(source, b2b.get)) add2newres = List()
                else add2newres = List(b)
              }
              // a continue statement shall appear only in a loop body
              // a continue statement causes a jump to the loop-continuation portion
              // of the smallest enclosing iteration statement
              case c: ContinueStatement => {
                val a2c = findPriorASTElem2ContinueStatement(source, env)
                val b2c = findPriorASTElem2ContinueStatement(c, env)

                if (a2c.isDefined && b2c.isDefined && a2c.get.eq(b2c.get)) {
                  a2c.get match {
                    case WhileStatement(expr, _) if (isPartOf(source, expr)) => add2newres = List(c)
                    case DoStatement(expr, _) if (isPartOf(source, expr)) => add2newres = List(c)
                    case ForStatement(_, Some(expr2), None, _) if (isPartOf(source, expr2)) => add2newres = List(c)
                    case ForStatement(_, _, Some(expr3), _) if (isPartOf(source, expr3)) => add2newres = List(c)
                    case _ => add2newres = List()
                  }
                } else add2newres = List()
              }
              // in case we hit an elif statement, we have to check whether a and the elif belong to the same if
              // if a belongs to an if
              // TODO should be moved to pred determination directly
              case e@ElifStatement(condition, _) => {
                val a2e = findPriorASTElem[IfStatement](source, env)
                val b2e = findPriorASTElem[IfStatement](e, env)

                if (a2e.isEmpty) { changed = true; add2newres = rollUp(e, oldelem, env.featureExpr(oldelem), resctx, env)}
                else if (a2e.isDefined && b2e.isDefined && a2e.get.eq(b2e.get)) {
                  changed = true
                  add2newres = getCondExprPred(condition, env.featureExpr(oldelem), resctx, env)
                }
                else {
                  changed = true
                  add2newres = rollUp(e, oldelem, env.featureExpr(oldelem), resctx, env)
                }
              }

              // goto statements
              // in general only label statements can be the source of goto statements
              // and only the ones that have the same name
              case s@GotoStatement(Id(name)) => {
                if (source.isInstanceOf[LabelStatement]) {
                  val lname = source.asInstanceOf[LabelStatement].id.name
                  if (name == lname) add2newres = List(s)
                }
              }

              // for all other elements we use rollup and check whether the outcome of rollup differs from
              // its input (oldelem)
              case _: AST => {
                add2newres = rollUp(source, oldelem, env.featureExpr(oldelem), resctx, env)
                if (add2newres.size > 1 || (add2newres.size > 0 && add2newres.head.ne(oldelem))) changed = true
              }
            }

            // add only elements that are not in newres so far
            // add them add the end to keep the order of the elements
            for (addnew <- add2newres)
              if (newres.map(_.eq(addnew)).foldLeft(false)(_ || _).unary_!) newres = newres ++ List(addnew)
          }
        }
        predCCFGCache.update(source, newres)
        newres
      }
    }
  }

  // checks reference equality of e in a given struture t (either product or list)
  def isPartOf(subterm: Product, term: Any): Boolean = {
    term match {
      case _: Product if (subterm.asInstanceOf[AnyRef].eq(term.asInstanceOf[AnyRef])) => true
      case l: List[_] => l.map(isPartOf(subterm, _)).exists(_ == true)
      case p: Product => p.productIterator.toList.map(isPartOf(subterm, _)).exists(_ == true)
      case _ => false
    }
  }

  def predHelper(source: Product, ctx: FeatureExpr, resctx: List[FeatureExpr], env: ASTEnv): List[AST] = {

    // helper method to handle a switch, if we come from a case or a default statement
    def handleSwitch(t: AST) = {
      val prior_switch = findPriorASTElem[SwitchStatement](t, env)
      assert(prior_switch.isDefined, "default or case statements should always occur withing a switch definition")
      prior_switch.get match {
        case SwitchStatement(expr, _) => getExprPred(expr, ctx, resctx, env) ++ getStmtPred(t, ctx, resctx, env)
      }
    }

    source match {
      case t: CaseStatement => handleSwitch(t)
      case t: DefaultStatement => handleSwitch(t)

      case t@LabelStatement(Id(n), _) => {
        findPriorASTElem[FunctionDef](t, env) match {
          case None => assert(false, "label statements should always occur within a function definition"); List()
          case Some(f) => {
            val l_gotos = filterASTElems[GotoStatement](f, env.featureExpr(t), env)
            // filter gotostatements with the same id as the labelstatement
            // and all gotostatements with dynamic target
            val l_gotos_filtered = l_gotos.filter({
              case GotoStatement(Id(name)) => if (n == name) true else false
              case _ => true
            })
            val l_preds = getStmtPred(t, ctx, resctx, env).
              flatMap({ x => rollUp(source, x, env.featureExpr(x), resctx, env) })
            l_gotos_filtered ++ l_preds
          }
        }
      }

      case o: Opt[_] => predHelper(childAST(o), ctx, resctx, env)
      case c: Conditional[_] => predHelper(childAST(c), ctx, resctx, env)

      case f@FunctionDef(_, _, _, CompoundStatement(List())) => List(f)
      case f@FunctionDef(_, _, _, stmt) => predHelper(childAST(stmt), ctx, resctx, env) ++
        filterAllASTElems[ReturnStatement](f, env.featureSet(f))
      case c@CompoundStatement(innerStatements) => getCompoundPred(innerStatements, c, ctx, resctx, env)

      case s: Statement => getStmtPred(s, ctx, resctx, env)
      case _ => followPred(source, ctx, resctx, env)
    }
  }

  def succ(source: Product, env: ASTEnv): List[AST] = {
    succCCFGCache.lookup(source) match {
      case Some(v) => v
      case None => {
        var curres: CCFGRes = List()
        val ctxsource = env.featureExpr(source)
        curres = succHelper(source, ctxsource, curres, env)

        val res = curres.map(_._2)
        succCCFGCache.update(source, res)
        res
      }
    }
  }

  // checks whether a given AST element is a succ instruction or not
  private def isSuccInstruction(elem: AST): Boolean = {
    elem match {
      case _: ForStatement => false
      case _: WhileStatement => false
      case _: DoStatement => false
      case _: CompoundStatement => false
      case _: CompoundStatementExpr => false
      case _: IfStatement => false
      case _: SwitchStatement => false
      case _ => true
    }
  }

  // checks whether the current result list is complete
  private def succComplete(ctx: FeatureExpr, curres: CCFGRes): Boolean = {
    val curresctx = curres.map(_._1)
    ctx implies (curresctx.fold(FeatureExprFactory.False)(_ or _)) isTautology()
  }

  private def succHelper(source: Product, ctx: FeatureExpr, oldres: CCFGRes, env: ASTEnv): CCFGRes = {
    source match {
      // ENTRY element
      case f@FunctionDef(_, _, _, CompoundStatement(List())) => List((env.featureExpr(f), f))
      case f@FunctionDef(_, _, _, stmt) => oldres ++ succHelper(stmt, ctx, oldres, env)

      // EXIT element
      case t@ReturnStatement(_) => {
        findPriorASTElem[FunctionDef](t, env) match {
          case None => assert(false, "return statement should always occur within a function statement"); List()
          case Some(f) => oldres ++ List((env.featureExpr(f), f))
        }
      }

      case c@CompoundStatement(l) => getCompoundSucc(l, c, ctx, oldres, env)

      // loop statements
      case ForStatement(None, Some(expr2), None, One(EmptyStatement())) => getExprSucc(expr2, ctx, oldres, env)
      case ForStatement(None, Some(expr2), None, One(CompoundStatement(List()))) => getExprSucc(expr2, ctx, oldres, env)
      case ForStatement(expr1, expr2, expr3, s) => {
        if (expr1.isDefined) getExprSucc(expr1.get, ctx, oldres, env)
        else if (expr2.isDefined) getExprSucc(expr2.get, ctx, oldres, env)
        else getCondStmtSucc(s, ctx, oldres, env)
      }
      case WhileStatement(expr, One(EmptyStatement())) => getExprSucc(expr, ctx, oldres, env)
      case WhileStatement(expr, One(CompoundStatement(List()))) => getExprSucc(expr, ctx, oldres, env)
      case WhileStatement(expr, _) => getExprSucc(expr, ctx, oldres, env)
      case DoStatement(expr, One(CompoundStatement(List()))) => getExprSucc(expr, ctx, oldres, env)
      case DoStatement(_, s) => getCondStmtSucc(s, ctx, oldres, env)

      // conditional statements
      case t@IfStatement(condition, _, _, _) => getCondExprSucc(condition, ctx, oldres, env)
      case t@ElifStatement(condition, _) => getCondExprSucc(condition, ctx, oldres, env)
      case SwitchStatement(expr, _) => getExprSucc(expr, ctx, oldres, env)

      case t@BreakStatement() => {
        val e2b = findPriorASTElem2BreakStatement(t, env)
        assert(e2b.isDefined, "break statement should always occur within a for, do-while, while, or switch statement")
        getStmtSucc(e2b.get, ctx, oldres, env)
      }
      case t@ContinueStatement() => {
        val e2c = findPriorASTElem2ContinueStatement(t, env)
        assert(e2c.isDefined, "continue statement should always occur within a for, do-while, or while statement")
        e2c.get match {
          case t@ForStatement(_, expr2, expr3, s) => {
            if (expr3.isDefined) getExprSucc(expr3.get, ctx, oldres, env)
            else if (expr2.isDefined) getExprSucc(expr2.get, ctx, oldres, env)
            else getCondStmtSucc(s, ctx, oldres, env)
          }
          case WhileStatement(expr, _) => getExprSucc(expr, ctx, oldres, env)
          case DoStatement(expr, _) => getExprSucc(expr, ctx, oldres, env)
          case _ => List()
        }
      }
      case t@GotoStatement(Id(l)) => {
        findPriorASTElem[FunctionDef](t, env) match {
          case None => assert(false, "goto statement should always occur within a function definition"); oldres
          case Some(f) => {
            val l_list = filterAllASTElems[LabelStatement](f, env.featureExpr(t), env).filter(_.id.name == l)
            if (l_list.isEmpty) getStmtSucc(t, ctx, oldres, env)
            else oldres ++ l_list.map(x => (env.featureExpr(x), x))
          }
        }
      }
      // in case we have an indirect goto dispatch all goto statements
      // within the function (this is our invariant) are possible targets of this goto
      // so fetch the function statement and filter for all label statements
      case t@GotoStatement(PointerDerefExpr(_)) => {
        findPriorASTElem[FunctionDef](t, env) match {
          case None => assert(false, "goto statement should always occur within a function definition"); oldres
          case Some(f) => {
            val l_list = filterAllASTElems[LabelStatement](f, env.featureExpr(t))
            if (l_list.isEmpty) getStmtSucc(t, ctx, oldres, env)
            else oldres ++ l_list.map(x => (env.featureExpr(x), x))
          }
        }
      }

      case CaseStatement(_) => oldres

      case DefaultStatement(Some(s)) => getCondStmtSucc(s, ctx, oldres, env)
      case t: DefaultStatement => getStmtSucc(t, ctx, oldres, env)

      case t: Statement => getStmtSucc(t, ctx, oldres, env)
      case t => followSucc(t, ctx, oldres, env)
    }
  }

  private def getCondStmtSucc(c: Conditional[Statement], ctx: FeatureExpr, oldres: CCFGRes, env: ASTEnv): CCFGRes = {
    c match {
      case Choice(_, thenBranch, elseBranch) =>
        getCondStmtSucc(thenBranch, ctx, oldres, env) ++ getCondStmtSucc(elseBranch, ctx, oldres, env)
      case One(CompoundStatement(l)) => getCompoundSucc(l, parentAST(c, env), ctx, oldres, env)
      case One(s: Statement) => oldres ++ List((env.featureExpr(s), s))
    }
  }

  private def getCondStmtPred(p: AST, c: Conditional[_], ctx: FeatureExpr, resctx: List[FeatureExpr], env: ASTEnv): List[AST] = {
    c match {
      case Choice(_, thenBranch, elseBranch) =>
        getCondStmtPred(p, thenBranch, ctx, resctx, env) ++ getCondStmtPred(p, elseBranch, ctx, resctx, env)
      case o@One(CompoundStatement(l)) => getCompoundPred(l, o, ctx, resctx, env)
      case One(s: Statement) => List(s)
    }
  }

  private def getExprSucc(e: Expr, ctx: FeatureExpr, oldres: CCFGRes, env: ASTEnv): CCFGRes = {
    e match {
      case c@CompoundStatementExpr(CompoundStatement(innerStatements)) =>
        getCompoundSucc(innerStatements, c, ctx, oldres, env)
      case _ => oldres ++ List((env.featureExpr(e), e))
    }
  }

  private def getCondExprSucc(cexp: Conditional[Expr], ctx: FeatureExpr, oldres: CCFGRes, env: ASTEnv): CCFGRes = {
    cexp match {
      case One(value) => getExprSucc(value, ctx, oldres, env)
      case Choice(_, thenBranch, elseBranch) =>
        getCondExprSucc(thenBranch, env.featureExpr(thenBranch), oldres, env) ++
          getCondExprSucc(elseBranch, env.featureExpr(elseBranch), oldres, env)
    }
  }

  private def getExprPred(exp: Expr, ctx: FeatureExpr, resctx: List[FeatureExpr], env: ASTEnv) = {
    exp match {
      case t@CompoundStatementExpr(CompoundStatement(innerStatements)) => getCompoundPred(innerStatements, t, ctx, resctx, env)
      case _ => {
        val expfexp = env.featureExpr(exp)
        if (expfexp implies resctx.fold(FeatureExprFactory.False)(_ or _) isTautology()) List()
        else List(exp)
      }
    }
  }

  private def getCondExprPred(cexp: Conditional[Expr], ctx: FeatureExpr, resctx: List[FeatureExpr], env: ASTEnv): List[AST] = {
    cexp match {
      case One(value) => getExprPred(value, ctx, resctx, env)
      case Choice(_, thenBranch, elseBranch) =>
        getCondExprPred(thenBranch, env.featureExpr(thenBranch), resctx, env) ++
          getCondExprPred(elseBranch, env.featureExpr(elseBranch), resctx, env)
    }
  }

  // handling of successor determination of nested structures, such as for, while, ... and next element in a list
  // of statements
  private def followSucc(nested_ast_elem: Product, ctx: FeatureExpr, oldres: CCFGRes, env: ASTEnv): CCFGRes = {
    nested_ast_elem match {
      case t: ReturnStatement => {
        findPriorASTElem[FunctionDef](t, env) match {
          case None => assert(false, "return statement should always occur within a function statement"); List()
          case Some(f) => oldres ++ List((env.featureExpr(f), f))
        }
      }
      case _ => {
        val surrounding_parent = parentAST(nested_ast_elem, env)
        surrounding_parent match {
          // loops
          case t@ForStatement(Some(expr1), expr2, _, s) if (isPartOf(nested_ast_elem, expr1)) =>
            if (expr2.isDefined) getExprSucc(expr2.get, ctx, oldres, env)
            else getCondStmtSucc(s, ctx, oldres, env)
          case t@ForStatement(_, Some(expr2), _, s) if (isPartOf(nested_ast_elem, expr2)) =>
            getStmtSucc(t, ctx, oldres, env) ++ getCondStmtSucc(s, ctx, oldres, env)
          case t@ForStatement(_, expr2, Some(expr3), s) if (isPartOf(nested_ast_elem, expr3)) =>
            if (expr2.isDefined) getExprSucc(expr2.get, ctx, oldres, env)
            else getCondStmtSucc(s, ctx, oldres, env)
          case t@ForStatement(_, expr2, expr3, s) if (isPartOf(nested_ast_elem, s)) => {
            if (expr3.isDefined) getExprSucc(expr3.get, ctx, oldres, env)
            else if (expr2.isDefined) getExprSucc(expr2.get, ctx, oldres, env)
            else getCondStmtSucc(s, ctx, oldres, env)
          }
          case t@WhileStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) =>
            getCondStmtSucc(s, ctx, oldres, env) ++ getStmtSucc(t, ctx, oldres, env)
          case WhileStatement(expr, _) => getExprSucc(expr, ctx, oldres, env)
          case t@DoStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) =>
            getCondStmtSucc(s, ctx, oldres, env) ++ getStmtSucc(t, ctx, oldres, env)
          case DoStatement(expr, s) => getExprSucc(expr, ctx, oldres, env)

          // conditional statements
          // we are in the condition of the if statement
          case t@IfStatement(condition, thenBranch, elifs, elseBranch) if (isPartOf(nested_ast_elem, condition)) => {
            var res = getCondStmtSucc(thenBranch, ctx, oldres, env)
            if (!elifs.isEmpty) res = res ++ getCompoundSucc(elifs, t, ctx, oldres, env)
            if (elifs.isEmpty && elseBranch.isDefined) res = res ++ getCondStmtSucc(elseBranch.get, ctx, oldres, env)
            if (elifs.isEmpty && !elseBranch.isDefined) res = res ++ getStmtSucc(t, ctx, oldres, env)
            res
          }

          // either go to next ElifStatement, ElseBranch, or next statement of the surrounding IfStatement
          // filtering is necessary, as else branches are not considered by getSuccSameLevel
          case t@ElifStatement(condition, thenBranch) if (isPartOf(nested_ast_elem, condition)) => {
            val res = getStmtSucc(t, ctx, oldres, env)
            res ++ getCondStmtSucc(thenBranch, ctx, oldres, env)
          }
          case t: ElifStatement => followSucc(t, ctx, oldres, env)

          // the switch statement behaves like a dynamic goto statement;
          // based on the expression we jump to one of the case statements or default statements
          // after the jump the case/default statements do not matter anymore
          // when hitting a break statement, we jump to the end of the switch
          case t@SwitchStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) => {
            var res: CCFGRes = oldres
            if (isPartOf(nested_ast_elem, expr)) {
              res ++= filterCaseStatements(s, env.featureExpr(t), env).map(x => (env.featureExpr(x), x))
              val dcase = filterDefaultStatements(s, env.featureExpr(t), env)

              if (dcase.isEmpty) res ++= getStmtSucc(t, ctx, oldres, env)
              else res ++= dcase.map(x => (env.featureExpr(x), x))
            }
            res
          }

          case t: Expr => followSucc(t, ctx, oldres, env)
          case t: Statement => getStmtSucc(t, ctx, oldres, env)

          case t: FunctionDef => oldres ++ List((env.featureExpr(t), t))
          case _ => List()
        }
      }
    }
  }

  // method to catch surrounding ast element, which precedes the given nested_ast_element
  private def followPred(nested_ast_elem: Product, ctx: FeatureExpr, resctx: List[FeatureExpr], env: ASTEnv): List[AST] = {

    def handleSwitch(t: AST) = {
      val prior_switch = findPriorASTElem[SwitchStatement](t, env)
      assert(prior_switch.isDefined, "default statement without surrounding switch")
      prior_switch.get match {
        case SwitchStatement(expr, _) => {
          val lconds = getExprPred(expr, ctx, resctx, env)
          if (env.previous(t) != null) lconds ++ getStmtPred(t, ctx, resctx, env)
          else {
            val tparent = parentAST(t, env)
            if (tparent.isInstanceOf[CaseStatement]) tparent :: lconds  // TODO rewrite, nested cases.
            else lconds ++ getStmtPred(tparent, ctx, resctx, env)
          }
        }
      }
    }

    nested_ast_elem match {

      // case or default statements belong only to switch statements
      case t: CaseStatement => handleSwitch(t)
      case t: DefaultStatement => handleSwitch(t)

      case _ => {
        val surrounding_parent = parentAST(nested_ast_elem, env)
        surrounding_parent match {

          // loop statements

          // for statements consists of of (init, break, inc, body)
          // we are in one of these elements
          // init
          case t@ForStatement(Some(expr1), _, _, _) if (isPartOf(nested_ast_elem, expr1)) =>
            getStmtPred(t, ctx, resctx, env)
          // inc
          case t@ForStatement(_, _, Some(expr3), s) if (isPartOf(nested_ast_elem, expr3)) =>
            getCondStmtPred(t, s, ctx, resctx, env) ++ filterContinueStatements(s, env.featureExpr(t), env)
          // break
          case t@ForStatement(None, Some(expr2), None, One(CompoundStatement(List()))) =>
            List(expr2) ++ getStmtPred(t, ctx, resctx, env)
          case t@ForStatement(expr1, Some(expr2), expr3, s) if (isPartOf(nested_ast_elem, expr2)) => {
            var res: List[AST] = List()
            if (expr1.isDefined) res ++= getExprPred(expr1.get, ctx, resctx, env)
            else res ++= getStmtPred(t, ctx, resctx, env)
            if (expr3.isDefined) res ++= getExprPred(expr3.get, ctx, resctx, env)
            else {
              res ++= getCondStmtPred(t, s, ctx, resctx, env)
              res ++= filterContinueStatements(s, env.featureExpr(t), env)
            }
            res
          }
          // s
          case t@ForStatement(expr1, expr2, expr3, s) if (isPartOf(nested_ast_elem, s)) =>
            if (expr2.isDefined) getExprPred(expr2.get, ctx, resctx, env)
            else if (expr3.isDefined) getExprPred(expr3.get, ctx, resctx, env)
            else {
              var res: List[AST] = List()
              if (expr1.isDefined) res = res ++ getExprPred(expr1.get, ctx, resctx, env)
              else res = getStmtPred(t, ctx, resctx, env) ++ res
              res = res ++ getCondStmtPred(t, s, ctx, resctx, env)
              res
            }

          // while statement consists of (expr, s)
          // special case; we handle empty compound statements here directly because otherwise we do not terminate
          case t@WhileStatement(expr, One(CompoundStatement(List()))) if (isPartOf(nested_ast_elem, expr)) =>
            getStmtPred(t, ctx, resctx, env) ++ List(expr)
          case t@WhileStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) =>
            (getStmtPred(t, ctx, resctx, env) ++ getCondStmtPred(t, s, ctx, resctx, env) ++
              filterContinueStatements(s, env.featureExpr(t), env))
          case t@WhileStatement(expr, _) => {
            if (nested_ast_elem.eq(expr)) getStmtPred(t, ctx, resctx, env)
            else getExprPred(expr, ctx, resctx, env)
          }

          // do statement consists of (expr, s)
          // special case: we handle empty compound statements here directly because otherwise we do not terminate
          case t@DoStatement(expr, One(CompoundStatement(List()))) if (isPartOf(nested_ast_elem, expr)) =>
            getStmtPred(t, ctx, resctx, env) ++ List(expr)
          case t@DoStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) =>
            getCondStmtPred(t, s, ctx, resctx, env) ++ filterContinueStatements(s, env.featureExpr(t), env)
          case t@DoStatement(expr, s) => {
            if (isPartOf(nested_ast_elem, expr)) getCondStmtPred(t, s, ctx, resctx, env)
            else getExprPred(expr, ctx, resctx, env) ++ getStmtPred(t, ctx, resctx, env)
          }

          // conditional statements
          // if statement: control flow comes either out of:
          // elseBranch: elifs + condition is the result
          // elifs: rest of elifs + condition
          // thenBranch: condition
          case t@IfStatement(condition, thenBranch, elifs, elseBranch) => {
            if (isPartOf(nested_ast_elem, condition)) getStmtPred(t, env.featureExpr(t), resctx, env)
            else if (isPartOf(nested_ast_elem, thenBranch)) getCondExprPred(condition, ctx, resctx, env)
            else if (isPartOf(nested_ast_elem, elseBranch)) {
              if (elifs.isEmpty) getCondExprPred(condition, ctx, resctx, env)
              else {
                getCompoundPred(elifs, t, ctx, resctx, env).flatMap({
                  case ElifStatement(elif_condition, _) => getCondExprPred(elif_condition, ctx, resctx, env)
                  case x => List(x)
                })
              }
            } else {
              getStmtPred(nested_ast_elem.asInstanceOf[AST], ctx, resctx, env)
            }
          }

          // pred of thenBranch is the condition itself
          // and if we are in condition, we strike for a previous elifstatement or the if itself using
          // getPredSameLevel
          case t@ElifStatement(condition, thenBranch) => {
            if (isPartOf(nested_ast_elem, condition)) predElifStatement(t, ctx, resctx, env)
            else getCondExprPred(condition, ctx, resctx, env)
          }

          case SwitchStatement(expr, s) if (isPartOf(nested_ast_elem, s)) => getExprPred(expr, ctx, resctx, env)
          case t: CaseStatement => List(t)

          // pred of default is either the expression of the switch, which is
          // returned by handleSwitch, or a previous statement (e.g.,
          // switch (exp) {
          // ...
          // label1:
          // default: ...)
          // as part of a fall through (sequence of statements without a break and that we catch
          // with getStmtPred
          case t: DefaultStatement => handleSwitch(t) ++ getStmtPred(t, ctx, resctx, env)

          case t: CompoundStatementExpr => followPred(t, ctx, resctx, env)
          case t: Statement => getStmtPred(t, ctx, resctx, env)
          case t: FunctionDef => {
            val ffexp = env.featureExpr(t)
            if (ffexp implies resctx.fold(FeatureExprFactory.False)(_ or _) isTautology()) List()
            else List(t)
          }
          case _ => List()
        }
      }
    }
  }

  private def predElifStatement(a: ElifStatement, ctx: FeatureExpr, resctx: List[FeatureExpr], env: ASTEnv): List[AST] = {
    val surrounding_if = parentAST(a, env)
    surrounding_if match {
      case IfStatement(condition, thenBranch, elifs, elseBranch) => {
        var res: List[AST] = List()
        val prev_elifs = elifs.reverse.dropWhile(_.entry.eq(a.asInstanceOf[AnyRef]).unary_!).drop(1)
        val ifdef_blocks = determineIfdefBlocks(prev_elifs, env)
        //        res = res ++ determineFollowingElements(ctx, oldres, ifdef_blocks, env).merge

        determineFollowingElementsPred(ctx, resctx, ifdef_blocks, env) match {
          case Left(plist) => res = res ++ plist
          case Right((cresctx, plist)) => res = res ++ plist ++ getCondExprPred(condition, ctx, cresctx, env)
        }
        res

        //        // if no previous elif statement is found, the result is condition
        //        if (!res.isEmpty) {
        //          var newres: List[AST] = List()
        //          for (elem_res <- res) {
        //            elem_res match {
        //              case ElifStatement(elif_condition, _) =>
        //                newres = getCondExprPred(elif_condition, ctx, oldres, env) ++ newres
        //              case _ => newres = elem_res :: newres
        //            }
        //          }
        //          newres
        //        }
        //        else getCondExprPred(condition, ctx, oldres, env)
      }
      case _ => List()
    }
  }

  // method to find a prior loop statement that belongs to a given break statement
  private def findPriorASTElem2BreakStatement(a: Product, env: ASTEnv): Option[AST] = {
    val aparent = env.parent(a)
    aparent match {
      case t: ForStatement => Some(t)
      case t: WhileStatement => Some(t)
      case t: DoStatement => Some(t)
      case t: SwitchStatement => Some(t)
      case null => None
      case p: Product => findPriorASTElem2BreakStatement(p, env)
    }
  }

  // method to find prior element to a continue statement
  private def findPriorASTElem2ContinueStatement(a: Product, env: ASTEnv): Option[AST] = {
    val aparent = env.parent(a)
    aparent match {
      case t: ForStatement => Some(t)
      case t: WhileStatement => Some(t)
      case t: DoStatement => Some(t)
      case null => None
      case p: Product => findPriorASTElem2ContinueStatement(p, env)
    }
  }

  // we have to check possible successor nodes in at max three steps:
  // 1. get direct successors with same annotation; if yes stop; if not go to step 2.
  // 2. get all annotated elements at the same level and check whether we find a definite set of successor nodes
  //    if yes stop; if not go to step 3.
  // 3. get the parent of our node and determine successor nodes of it
  private def getStmtSucc(s: AST, ctx: FeatureExpr, oldres: CCFGRes, env: ASTEnv): CCFGRes = {

    // check whether next statement has the same annotation if yes return it, if not
    // check the following ifdef blocks; 1.
    val snexts = nextASTElems(s, env).tail.map(x => parentOpt(x, env).asInstanceOf[Opt[AST]])
    getCompoundSucc(snexts, s, ctx, oldres, env)
  }

  // specialized version of getStmtSucc for ElifStatements
  private def getElifSucc(s: ElifStatement, ctx: FeatureExpr, oldres: CCFGRes, env: ASTEnv): CCFGRes = {
    getStmtSucc(s, ctx, oldres, env)
  }

  // this method filters BreakStatements
  // a break belongs to next outer loop (for, while, do-while)
  // or a switch statement (see [2])
  // use this method with the loop or switch body!
  // so we recursively go over the structure of the ast elems
  // in case we find a break, we add it to the result list
  // in case we hit another loop or switch we return the empty list
  private def filterBreakStatements(c: Conditional[Statement], ctx: FeatureExpr, env: ASTEnv): List[BreakStatement] = {
    def filterBreakStatementsHelper(a: Any): List[BreakStatement] = {
      a match {
        case t: BreakStatement => if (env.featureExpr(t) implies ctx isSatisfiable()) List(t) else List()
        case _: SwitchStatement => List()
        case _: ForStatement => List()
        case _: WhileStatement => List()
        case _: DoStatement => List()
        case l: List[_] => l.flatMap(filterBreakStatementsHelper(_))
        case x: Product => x.productIterator.toList.flatMap(filterBreakStatementsHelper(_))
        case _ => List()
      }
    }
    filterBreakStatementsHelper(c)
  }

  // this method filters ContinueStatements
  // according to [2]: A continue statement shall appear only in or as a
  // loop body
  // use this method only with the loop body!
  private def filterContinueStatements(c: Conditional[Statement], ctx: FeatureExpr, env: ASTEnv): List[ContinueStatement] = {
    def filterContinueStatementsHelper(a: Any): List[ContinueStatement] = {
      a match {
        case t: ContinueStatement => if (env.featureExpr(t) implies ctx isSatisfiable()) List(t) else List()
        case _: ForStatement => List()
        case _: WhileStatement => List()
        case _: DoStatement => List()
        case l: List[_] => l.flatMap(filterContinueStatementsHelper(_))
        case x: Product => x.productIterator.toList.flatMap(filterContinueStatementsHelper(_))
        case _ => List()
      }
    }
    filterContinueStatementsHelper(c)
  }

  // this method filters all CaseStatements
  private def filterCaseStatements(c: Conditional[Statement], ctx: FeatureExpr, env: ASTEnv): List[CaseStatement] = {
    def filterCaseStatementsHelper(a: Any): List[CaseStatement] = {
      a match {
        case t@CaseStatement(_) => if (env.featureExpr(t) implies ctx isSatisfiable()) List(t) else List()
        case _: SwitchStatement => List()
        case l: List[_] => l.flatMap(filterCaseStatementsHelper(_))
        case x: Product => x.productIterator.toList.flatMap(filterCaseStatementsHelper(_))
        case _ => List()
      }
    }
    filterCaseStatementsHelper(c)
  }

  // although the standard says that a case statement only has one default statement
  // we may have differently annotated default statements
  private def filterDefaultStatements(c: Conditional[Statement], ctx: FeatureExpr, env: ASTEnv): List[DefaultStatement] = {
    def filterDefaultStatementsHelper(a: Any): List[DefaultStatement] = {
      a match {
        case _: SwitchStatement => List()
        case t: DefaultStatement => if (env.featureExpr(t) implies ctx isSatisfiable()) List(t) else List()
        case l: List[_] => l.flatMap(filterDefaultStatementsHelper(_))
        case x: Product => x.productIterator.toList.flatMap(filterDefaultStatementsHelper(_))
        case _ => List()
      }
    }
    filterDefaultStatementsHelper(c)
  }

  // in predecessor determination we have to dig in into elements at certain points
  // we dig into ast that have an Conditional part, such as for, while, ...
  // source is the element that we compute the predecessor for
  // target is the current determined predecessor that might be evaluated further
  // ctx stores the context of target element
  // env is the ast environment that stores references to parents, siblings, and children
  private def rollUp(source: Product, target: AST, ctx: FeatureExpr, resctx: List[FeatureExpr], env: ASTEnv): List[AST] = {
    target match {

      // in general all elements from the different branches (thenBranch, elifs, elseBranch)
      // can be predecessors
      case t@IfStatement(condition, thenBranch, elifs, elseBranch) => {
        var res = List[AST]()

        if (elseBranch.isDefined) res ++= getCondStmtPred(t, elseBranch.get, ctx, resctx, env)
        if (!elifs.isEmpty) {
          for (Opt(f, elif@ElifStatement(_, thenBranch)) <- elifs) {
            if (f.implies(ctx).isSatisfiable())
              res ++= getCondStmtPred(elif, thenBranch, env.featureExpr(elif), resctx, env)
          }

          // without an else branch, the condition of elifs are possible predecessors of a
          if (elseBranch.isEmpty) res ++= getCompoundPred(elifs, t, ctx, resctx, env)
        }
        res ++= getCondStmtPred(t, thenBranch, ctx, resctx, env)

        if (elifs.isEmpty && elseBranch.isEmpty)
          res ++= getCondExprPred(condition, ctx, resctx, env)
        res.flatMap({ x => rollUp(source, x, env.featureExpr(x), resctx, env) })
      }
      case ElifStatement(condition, thenBranch) => {
        var res = List[AST]()
        res ++= getCondExprPred(condition, ctx, resctx, env)

        // check wether source is part of a possibly exising elsebranch;
        // if so we do not roll up the thenbranch
        findPriorASTElem[IfStatement](source, env) match {
          case None =>
          case Some(IfStatement(_, _, _, None)) => res ++= getCondStmtPred(target, thenBranch, ctx, resctx, env)
          case Some(IfStatement(_, _, _, Some(x))) => if (! isPartOf(source, x))
            res ++= getCondStmtPred(target, thenBranch, ctx, resctx, env)
        }

        res.flatMap({ x => rollUp(source, x, env.featureExpr(x), resctx, env) })
      }
      case t@SwitchStatement(expr, s) => {
        val lbreaks = filterBreakStatements(s, env.featureExpr(t), env)
        lazy val ldefaults = filterDefaultStatements(s, env.featureExpr(t), env)

        // if no break and default statement is there, possible predecessors are the expr of the switch itself
        // and the code after the last case
        if (lbreaks.isEmpty && ldefaults.isEmpty) {
          var res = getExprPred(expr, ctx, resctx, env)
          val listcasestmts = filterCaseStatements(s, ctx, env)

          if (! listcasestmts.isEmpty) {
            val lastcase = listcasestmts.last
            res ++= rollUpJumpStatement(lastcase, true, env.featureExpr(lastcase), resctx, env)
          }

          res
        }
        else if (ldefaults.isEmpty) lbreaks ++ getExprPred(expr, ctx, resctx, env)
        else lbreaks ++ ldefaults.flatMap({ x => rollUpJumpStatement(x, true, env.featureExpr(x), resctx, env) })
      }

      case t@WhileStatement(expr, s) => List(expr) ++ filterBreakStatements(s, env.featureExpr(t), env)
      case t@DoStatement(expr, s) => List(expr) ++ filterBreakStatements(s, env.featureExpr(t), env)
      case t@ForStatement(_, Some(expr2), _, s) => List(expr2) ++ filterBreakStatements(s, env.featureExpr(t), env)
      case t@ForStatement(_, _, _, s) => filterBreakStatements(s, env.featureExpr(t), env)

      case c@CompoundStatement(innerStatements) => getCompoundPred(innerStatements, c, ctx, resctx, env).
        flatMap({ x => rollUp(source, x, env.featureExpr(x), resctx, env) })

      case t@GotoStatement(PointerDerefExpr(_)) => {
        if (source.isInstanceOf[LabelStatement]) List(target)
        else {
          findPriorASTElem[FunctionDef](t, env) match {
            case None => assert(false, "goto statement should always occur within a function definition"); List()
            case Some(f) => {
              val l_list = filterAllASTElems[LabelStatement](f, env.featureExpr(t))
              if (l_list.isEmpty) List(target)
              else List()
            }
          }
        }
      }

      case _ => List(target)
    }
  }

  // we have a separate rollUp function for CaseStatement, DefaultStatement, and BreakStatement
  // because using rollUp in pred determination (see above) will return wrong results
  private def rollUpJumpStatement(a: AST, fromSwitch: Boolean, ctx: FeatureExpr, resctx: List[FeatureExpr], env: ASTEnv): List[AST] = {
    a match {
      // the code that belongs to the jump target default is either reachable via nextAST from the
      // default statement: this first case statement here
      // or the code is nested in the DefaultStatement, so we match it with the next case statement
      case t@DefaultStatement(_) if (nextAST(t, env) != null && fromSwitch) => {
        val dparent = findPriorASTElem[CompoundStatement](t, env)
        assert(dparent.isDefined, "default statement always occurs in a compound statement of a switch")
        dparent.get match {
          case c@CompoundStatement(innerStatements) => getCompoundPred(innerStatements, c, ctx, resctx, env)
        }
      }
      case t@DefaultStatement(Some(s)) => getCondStmtPred(t, s, ctx, resctx, env).
        flatMap({ x => rollUpJumpStatement(x, false, env.featureExpr(x), resctx, env) })
      case _: BreakStatement => List()
      case _ => List(a)
    }
  }

  // we have to check possible predecessor nodes in at max three steps:
  // 1. get direct predecessor with same annotation; if yes stop; if not go to step 2.
  // 2. get all annotated elements at the same level and check whether we find a definite set of predecessor nodes
  //    if yes stop; if not go to step 3.
  // 3. get the parent of our node and determine predecessor nodes of it
  private def getStmtPred(s: AST, ctx: FeatureExpr, resctx: List[FeatureExpr], env: ASTEnv): List[AST] = {

    // 1.
    val sprev = prevAST(s, env)
    if (sprev != null && (env.featureExpr(sprev) equivalentTo ctx)) {
      sprev match {
        case BreakStatement() => List()
        case a => List(a).flatMap({ x => rollUpJumpStatement(x, false, env.featureExpr(x), resctx, env) })
      }
    } else {
      val sprevs = prevASTElems(s, env)
      val ifdefblocks = determineIfdefBlocks(sprevs, env)
      val taillist = getTailListPred(s, ifdefblocks)
      val taillistreversed = taillist.map(_.reverse).reverse

      determineFollowingElementsPred(ctx, resctx, taillistreversed.drop(1), env) match {
        case Left(plist) => plist.
          flatMap({ x => rollUpJumpStatement(x, false, env.featureExpr(x), resctx, env)}) // 2.
        case Right((cresctx, plist)) => plist.
          flatMap({ x => rollUpJumpStatement(x, false, env.featureExpr(x), cresctx, env)}) ++
          followPred(s, ctx, cresctx, env) // 3.

      }
    }
  }

  // given a list of AST elements, determine successor AST elements based on feature expressions
  private def getCompoundSucc(l: List[Opt[AST]], parent: AST, ctx: FeatureExpr, oldres: CCFGRes, env: ASTEnv): CCFGRes = {
    if (l.isEmpty) {
      if (succComplete(ctx, oldres)) oldres
      else followSucc(parent, ctx, oldres, env)
    }
    else {
      var curres = oldres
      l.map({
        x => {
          val ctxx = env.featureExpr(x.entry)

          if (ctxx and ctx isContradiction()) { }
          else if (curres.exists(x => x._1 equivalentTo ctxx)) { }
          else if (ctx implies ctxx isSatisfiable()) {
            if (isSuccInstruction(x.entry)) curres ::= (ctxx, x.entry)
            else curres = succHelper(x.entry, ctx, curres, env)

            if (succComplete(ctx, curres)) return curres
          }
        }
      })
      if (succComplete(ctx, curres)) return curres
      else followSucc(parentAST(l.head, env), ctx, curres, env)
    }
  }

  // given a list of AST elements, determine predecessor AST elements based on feature expressions
  private def getCompoundPred(l: List[AST], parent: Product, ctx: FeatureExpr, resctx: List[FeatureExpr], env: ASTEnv): List[AST] = {
    val ifdefblocks = determineIfdefBlocks(l, env)
    val ifdefblocksreverse = ifdefblocks.map(_.reverse).reverse

    determineFollowingElementsPred(ctx, resctx, ifdefblocksreverse, env) match {
      case Left(plist) => plist
      case Right((cresctx, plist)) => plist ++ (if (l.isEmpty) followPred(parent, ctx, cresctx, env)
      else followPred(l.reverse.head, ctx, cresctx, env))
    }
  }

  // get list with rev and all following lists
  private def getTailListSucc(rev: AST, l: List[IfdefBlock]): List[IfdefBlock] = {
    // iterate each sublist of the incoming tuples TypedOptAltBlock combine equality check
    // and drop elements in which s occurs

    def contains(ifdefbl: IfdefBlock): Boolean = {
      ifdefbl.exists( x => x.eq(rev) )
    }

    l.dropWhile(x => contains(x).unary_!)
  }

  // same as getTailListSucc but with reversed input
  // result list ist reversed again so we have a list of TypedOptBlocks with the last
  // block containing rev
  private def getTailListPred(rev: AST, l: List[IfdefBlock]): List[IfdefBlock] = {
    getTailListSucc(rev, l.reverse).reverse
  }

  private def determineFollowingElementsPred(ctx: FeatureExpr,
                                             resctx: List[FeatureExpr],
                                             l: List[IfdefBlock],
                                             env: ASTEnv): Either[List[AST], (List[FeatureExpr], List[AST])] = {
    // context of all added AST nodes that have been added to res
    var res = List[AST]()
    var cresctx = resctx

    for (ifdefblock <- l) {
      // get the first element of the ifdef block and check
      val head = ifdefblock.head
      val bfexp = env.featureExpr(head)

      // annotation of the block contradicts with context; do nothing
      if ((ctx and bfexp) isContradiction()) { }

      // nodes of annotations that have been added before: e.g., ctx is true; A B A true
      // the second A should not be added again because if A is selected the first A would have been selected
      // and not the second one
      else if (bfexp implies cresctx.fold(FeatureExprFactory.False)(_ or _) isTautology()) { }

      // otherwise add element and update resulting context
      else {res = res ++ List(head); cresctx ::= bfexp}

      if (cresctx.fold(FeatureExprFactory.False)(_ or _) equivalentTo ctx) return Left(res)
      if (bfexp equivalentTo ctx) return Left(res)
      if (bfexp isTautology()) return Left(res)
    }
    Right((cresctx, res))
  }



  // determine recursively all succs check
  def getAllSucc(i: AST, env: ASTEnv) = {
    var r = List[(AST, List[AST])]()
    var s = List(i)
    var d = List[AST]()
    var c: AST = null

    while (!s.isEmpty) {
      c = s.head
      s = s.drop(1)

      if (d.filter(_.eq(c)).isEmpty) {
        r = (c, succ(c, env)) :: r
        s = s ++ r.head._2
        d = d ++ List(c)
      }
    }
    r
  }

  // determine recursively all pred
  def getAllPred(i: AST, env: ASTEnv) = {
    var r = List[(AST, List[AST])]()
    var s = List(i)
    var d = List[AST]()
    var c: AST = null

    while (!s.isEmpty) {
      c = s.head
      s = s.drop(1)

      if (d.filter(_.eq(c)).isEmpty) {
        r = (c, pred(c, env)) :: r
        s = s ++ r.head._2
        d = d ++ List(c)
      }
    }
    r
  }

  // given an ast element x and its successors lx: x should be in pred(lx)
  def compareSuccWithPred(lsuccs: List[(AST, List[AST])], lpreds: List[(AST, List[AST])], env: ASTEnv): List[CCFGError] = {
    var errors: List[CCFGError] = List()

    // check that number of nodes match
    val sdiff = lsuccs.map(_._1).diff(lpreds.map(_._1))
    val pdiff = lpreds.map(_._1).diff(lsuccs.map(_._1))

    for (sdelem <- sdiff)
      errors = new CCFGErrorMis("is not present in preds!", sdelem, env.featureExpr(sdelem)) :: errors


    for (pdelem <- pdiff)
      errors = new CCFGErrorMis("is not present in succs!", pdelem, env.featureExpr(pdelem)) :: errors

    // check that number of edges match
    var succ_edges: List[(AST, AST)] = List()
    for ((ast_elem, succs) <- lsuccs) {
      for (succ <- succs) {
        succ_edges = (ast_elem, succ) :: succ_edges
      }
    }

    var pred_edges: List[(AST, AST)] = List()
    for ((ast_elem, preds) <- lpreds) {
      for (pred <- preds) {
        pred_edges = (ast_elem, pred) :: pred_edges
      }
    }

    // check succ/pred connection and print out missing connections
    // given two ast elems:
    //   a
    //   b
    // we check (a1, b1) successor
    // against  (b2, a2) predecessor
    for ((a1, b1) <- succ_edges) {
      var isin = false
      for ((b2, a2) <- pred_edges) {
        if (a1.eq(a2) && b1.eq(b2))
          isin = true
      }
      if (!isin) {
        errors = new CCFGErrorDir("is missing in preds", b1, env.featureExpr(b1), a1, env.featureExpr(a1)) :: errors
      }
    }

    // check pred/succ connection and print out missing connections
    // given two ast elems:
    //  a
    //  b
    // we check (b1, a1) predecessor
    // against  (a2, b2) successor
    for ((b1, a1) <- pred_edges) {
      var isin = false
      for ((a2, b2) <- succ_edges) {
        if (a1.eq(a2) && b1.eq(b2))
          isin = true
      }
      if (!isin) {
        errors = new CCFGErrorDir("is missing in succs", a1, env.featureExpr(a1), b1, env.featureExpr(b1)) :: errors
      }
    }

    errors
  }

  // given a comparison function f: pack consecutive elements of list elements into sublists
  private def pack[T](f: (T, T) => Boolean)(l: List[T]): List[List[T]] = {
    if (l.isEmpty) List()
    else (l.head :: l.tail.takeWhile(f(l.head, _))) :: pack[T](f)(l.tail.dropWhile(f(l.head, _)))
  }

  // given a list of Opt elements; pack elements with the same annotation into sublists
  private def determineIfdefBlocks(l: List[AST], env: ASTEnv): List[IfdefBlock] = {
    pack[AST](env.featureExpr(_) equivalentTo env.featureExpr(_))(l)
  }
}

