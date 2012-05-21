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
// 1. forward jumps using gotos
// 2. code in switch body that does not belong to a case block, but that has a label and
//    can be reached otherwise, e.g., switch (x) { l1: <code> case 0: .... }
// 3. infinite for loops without break or return statements in them, e.g.,
//    for (;;) { <code without break or return> }
//    this way we do not have any handle to jump into the for block from
//    its successors

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

trait ConditionalControlFlow extends ASTNavigation {

  private implicit def optList2ASTList(l: List[Opt[AST]]) = l.map(_.entry)

  private implicit def opt2AST(s: Opt[AST]) = s.entry

  private val predCCFGCache = new CCFGCache()
  private val succCCFGCache = new CCFGCache()

  type IfdefBlock = List[AST]
  type IfdefBlocks = List[List[AST]]

  // determines predecessor of a given element
  // results are cached for secondary evaluation
  def pred(a: Product, env: ASTEnv): List[AST] = {
    predCCFGCache.lookup(a) match {
      case Some(v) => v
      case None => {
        var oldres: List[AST] = List()
        val ctx = env.featureExpr(a)
        var newres: List[AST] = predHelper(a, ctx, env)
        var changed = true

        while (changed) {
          changed = false
          oldres = newres
          newres = List()

          for (oldelem <- oldres) {
            var add2newres = List[AST]()
            oldelem match {

              case _: ReturnStatement if (!a.isInstanceOf[FunctionDef]) => add2newres = List()

              // a break statement shall appear only in or as a switch body or loop body
              // a break statement terminates execution of the smallest enclosing switch or
              // iteration statement (see standard [2])
              // therefore all predecessors of a belong to a different switch/loop;
              // otherwise we filter them
              case b: BreakStatement => {
                val a2b = findPriorASTElem2BreakStatement(a, env)
                val b2b = findPriorASTElem2BreakStatement(b, env)

                if (a2b.isEmpty && b2b.isDefined) add2newres = List(b)
                else if (a2b.isDefined && b2b.isDefined && a2b.get.ne(b2b.get)) add2newres = List(b)
                else add2newres = List()
              }
              // a continue statement shall appear only in a loop body
              // a continue statement causes a jump to the loop-continuation portion
              // of the smallest enclosing iteration statement
              case c: ContinueStatement => {
                val a2c = findPriorASTElem2ContinueStatement(a, env)
                val b2c = findPriorASTElem2ContinueStatement(c, env)

                if (a2c.isDefined && b2c.isDefined && a2c.get.eq(b2c.get)) {
                  a2c.get match {
                    case WhileStatement(expr, _) if (isPartOf(a, expr)) => add2newres = List(c)
                    case DoStatement(expr, _) if (isPartOf(a, expr)) => add2newres = List(c)
                    case ForStatement(_, Some(expr2), None, _) if (isPartOf(a, expr2)) => add2newres = List(c)
                    case ForStatement(_, _, Some(expr3), _) if (isPartOf(a, expr3)) => add2newres = List(c)
                    case _ => add2newres = List()
                  }
                } else add2newres = List()
              }
              // in case we hit an elif statement, we have to check whether a and the elif belong to the same if
              // if a belongs to an if
              // TODO should be moved to pred determination directly
              case e@ElifStatement(condition, _) => {
                val a2e = findPriorASTElem[IfStatement](a, env)
                val b2e = findPriorASTElem[IfStatement](e, env)

                if (a2e.isEmpty) { changed = true; add2newres = rollUp(e, oldelem, env.featureExpr(oldelem), env)}
                else if (a2e.isDefined && b2e.isDefined && a2e.get.eq(b2e.get)) {
                  changed = true
                  add2newres = getCondExprPred(condition, env.featureExpr(oldelem), env)
                }
                else {
                  changed = true
                  add2newres = rollUp(e, oldelem, env.featureExpr(oldelem), env)
                }
              }
              case _: AST => {
                add2newres = rollUp(a, oldelem, env.featureExpr(oldelem), env)
                if (!(add2newres.size == 1 && add2newres.head.eq(oldelem))) changed = true
              }
            }

            // add only elements that are not in newres so far
            // add them add the end to keep the order of the elements
            for (addnew <- add2newres)
              if (newres.map(_.eq(addnew)).foldLeft(false)(_ || _).unary_!) newres = newres ++ List(addnew)
          }
        }
        predCCFGCache.update(a, newres)
        newres
      }
    }
  }

  // checks reference equality of e in a given struture t (either product or list)
  def isPartOf(e: Product, t: Any): Boolean = {
    t match {
      case _: Product if (e.asInstanceOf[AnyRef].eq(t.asInstanceOf[AnyRef])) => true
      case l: List[_] => l.map(isPartOf(e, _)).exists(_ == true)
      case p: Product => p.productIterator.toList.map(isPartOf(e, _)).exists(_ == true)
      case _ => false
    }
  }

  def predHelper(a: Product, ctx: FeatureExpr, env: ASTEnv): List[AST] = {

    // helper method to handle a switch, if we come from a case or a default statement
    def handleSwitch(t: AST) = {
      val prior_switch = findPriorASTElem[SwitchStatement](t, env)
      assert(prior_switch.isDefined, "default or case statements should always occur withing a switch definition")
      prior_switch.get match {
        case SwitchStatement(expr, _) => getCondExprPred(expr, ctx, env) ++ getStmtPred(t, ctx, env)
      }
    }

    a match {
      case t: CaseStatement => handleSwitch(t)
      case t: DefaultStatement => handleSwitch(t)

      case t@LabelStatement(Id(n), _) => {
        findPriorASTElem[FunctionDef](t, env) match {
          case None => assert(false, "label statements should always occur within a function definition"); List()
          case Some(f) => {
            val l_gotos = gotoLookup(f, n, env)
            val l_preds = getStmtPred(t, ctx, env)
            l_gotos ++ l_preds
          }
        }
      }

      case o: Opt[_] => predHelper(childAST(o), ctx, env)
      case c: Conditional[_] => predHelper(childAST(c), ctx, env)

      case f@FunctionDef(_, _, _, CompoundStatement(List())) => List(f)
      case f@FunctionDef(_, _, _, stmt) => predHelper(childAST(stmt), ctx, env) ++
         filterAllASTElems[ReturnStatement](f, env.featureExpr(f))
      case c@CompoundStatement(innerStatements) => getCompoundPred(innerStatements, c, ctx, env)

      case s: Statement => getStmtPred(s, ctx, env)
      case _ => followPred(a, ctx, env)
    }
  }

  def succ(a: Product, env: ASTEnv): List[AST] = {
    succCCFGCache.lookup(a) match {
      case Some(v) => v
      case None => {
        var oldres: List[AST] = List()
        val ctx = env.featureExpr(a)
        var newres: List[AST] = succHelper(a, ctx, env)
        var changed = true

        while (changed) {
          changed = false
          oldres = newres
          newres = List()
          for (oldelem <- oldres) {
            var add2newres: List[AST] = List()
            oldelem match {
              case _: IfStatement => changed = true; add2newres = succHelper(oldelem, env.featureExpr(oldelem), env)
              case _: ElifStatement => changed = true; add2newres = succHelper(oldelem, env.featureExpr(oldelem), env)
              case _: SwitchStatement => changed = true; add2newres = succHelper(oldelem, env.featureExpr(oldelem), env)
              case _: CompoundStatement => changed = true; add2newres = succHelper(oldelem, env.featureExpr(oldelem), env)
              case _: DoStatement => changed = true; add2newres = succHelper(oldelem, env.featureExpr(oldelem), env)
              case _: WhileStatement => changed = true; add2newres = succHelper(oldelem, env.featureExpr(oldelem), env)
              case _: ForStatement => changed = true; add2newres = succHelper(oldelem, env.featureExpr(oldelem), env)
              case _: DefaultStatement => changed = true; add2newres = succHelper(oldelem, env.featureExpr(oldelem), env)
              case _ => add2newres = List(oldelem)
            }

            // add only elements that are not in newres so far
            // add them add the end to keep the order of the elements
            for (addnew <- add2newres)
              if (newres.map(_.eq(addnew)).foldLeft(false)(_ || _).unary_!) newres = newres ++ List(addnew)
          }
        }
        succCCFGCache.update(a, newres)
        newres
      }
    }
  }

  private def succHelper(a: Product, ctx: FeatureExpr, env: ASTEnv): List[AST] = {
    a match {
      // ENTRY element
      case f@FunctionDef(_, _, _, CompoundStatement(List())) => List(f) // TODO after rewrite of compound handling -> could be removed
      case f@FunctionDef(_, _, _, stmt) => succHelper(stmt, ctx, env)

      // EXIT element
      case t@ReturnStatement(_) => {
        findPriorASTElem[FunctionDef](t, env) match {
          case None => assert(false, "return statement should always occur within a function statement"); List()
          case Some(f) => List(f)
        }
      }

      case t@CompoundStatement(l) => getCompoundSucc(l, t, ctx, env)

      case o: Opt[_] => succHelper(o.entry.asInstanceOf[Product], ctx, env)
      case t: Conditional[_] => succHelper(childAST(t), ctx, env)

      // loop statements
      case ForStatement(None, Some(expr2), None, One(EmptyStatement())) => getCondExprSucc(expr2, ctx, env)
      case ForStatement(None, Some(expr2), None, One(CompoundStatement(List()))) => getCondExprSucc(expr2, ctx, env)
      case t@ForStatement(expr1, expr2, expr3, s) => {
        if (expr1.isDefined) getCondExprSucc(expr1.get, ctx, env)
        else if (expr2.isDefined) getCondExprSucc(expr2.get, ctx, env)
        else getCondStmtSucc(t, s, ctx, env)
      }
      case WhileStatement(expr, One(EmptyStatement())) => getCondExprSucc(expr, ctx, env)
      case WhileStatement(expr, One(CompoundStatement(List()))) => getCondExprSucc(expr, ctx, env)
      case WhileStatement(expr, _) => getCondExprSucc(expr, ctx, env)
      case DoStatement(expr, One(CompoundStatement(List()))) => getCondExprSucc(expr, ctx, env)
      case t@DoStatement(_, s) => getCondStmtSucc(t, s, ctx, env)

      // conditional statements
      case t@IfStatement(condition, _, _, _) => getCondExprSucc(condition, ctx, env)
      case t@ElifStatement(condition, _) => getCondExprSucc(condition, ctx, env)
      case SwitchStatement(expr, _) => getCondExprSucc(expr, ctx, env)

      case t@BreakStatement() => {
        val e2b = findPriorASTElem2BreakStatement(t, env)
        assert(e2b.isDefined, "break statement should always occur within a for, do-while, while, or switch statement")
        getStmtSucc(e2b.get, ctx, env)
      }
      case t@ContinueStatement() => {
        val e2c = findPriorASTElem2ContinueStatement(t, env)
        assert(e2c.isDefined, "continue statement should always occur within a for, do-while, or while statement")
        e2c.get match {
          case t@ForStatement(_, expr2, expr3, s) => {
            if (expr3.isDefined) getCondExprSucc(expr3.get, ctx, env)
            else if (expr2.isDefined) getCondExprSucc(expr2.get, ctx, env)
            else getCondStmtSucc(t, s, ctx, env)
          }
          case WhileStatement(expr, _) => getCondExprSucc(expr, ctx, env)
          case DoStatement(expr, _) => getCondExprSucc(expr, ctx, env)
          case _ => List()
        }
      }
      case t@GotoStatement(Id(l)) => {
        findPriorASTElem[FunctionDef](t, env) match {
          case None => assert(false, "goto statement should always occur within a function definition"); List()
          case Some(f) => {
            val l_list = labelLookup(f, l, env)
            if (l_list.isEmpty) getStmtSucc(t, ctx, env)
            else l_list
          }
        }
      }
      // in case we have an indirect goto dispatch all goto statements
      // within the function (this is our invariant) are possible targets of this goto
      // so fetch the function statement and filter for all label statements
      case t@GotoStatement(PointerDerefExpr(_)) => {
        findPriorASTElem[FunctionDef](t, env) match {
          case None => assert(false, "goto statement should always occur within a function definition"); List()
          case Some(f) => {
            val l_list = filterAllASTElems[LabelStatement](f, env.featureExpr(t))
            if (l_list.isEmpty) getStmtSucc(t, ctx, env)
            else l_list
          }
        }
      }

      case t@CaseStatement(_, Some(s)) => getCondStmtSucc(t, s, ctx, env)
      case t: CaseStatement => getStmtSucc(t, ctx, env)

      case t@DefaultStatement(Some(s)) => getCondStmtSucc(t, s, ctx, env)
      case t: DefaultStatement => getStmtSucc(t, ctx, env)

      case t: Statement => getStmtSucc(t, ctx, env)
      case t => followSucc(t, ctx, env)
    }
  }

  private def iterateChildren(a: Any, label: String, env: ASTEnv, op: (Any, String, ASTEnv) => List[AST]): List[AST] = {
    env.children(a).map(
      x => x match {
        case e: AST      => op(e, label, env)
        case ls: List[_] => ls.flatMap(op(_, label, env))
        case o@Opt(_, entry) => op(entry, label, env)
        case o@Choice(_, thenBranch, elseBranch) => op(thenBranch, label, env) ++ op(elseBranch, label, env)
        case p: Product  => p.productIterator.toList.flatMap(op(_, label, env))
        case _ => List()
      }
    ).foldLeft(List[AST]())(_ ++ _.asInstanceOf[List[AST]])
  }

  // lookup all goto with matching ids or those using indirect goto dispatch
  // indirect goto dispatch are possible candidates for this label because
  // evaluating the expression of the goto might lead to the given label
  private def gotoLookup(a: Any, l: String, env: ASTEnv): List[AST] = {
    a match {
      case e@GotoStatement(Id(n)) if (n == l) => List(e)
      case e@GotoStatement(PointerDerefExpr(_)) => List(e)
      case p: Product => iterateChildren(p, l, env, gotoLookup)
    }
  }

  private def labelLookup(a: Any, label: String, env: ASTEnv): List[AST] = {
    a match {
      case e@LabelStatement(Id(n), _) if (n == label) => List(e) ++ iterateChildren(e, label, env, labelLookup)
      case p: Product => iterateChildren(p, label, env, labelLookup)
    }
  }

  private def getCondStmtSucc(p: AST, c: Conditional[_], ctx: FeatureExpr, env: ASTEnv): List[AST] = {
    c match {
      case Choice(_, thenBranch, elseBranch) =>
        getCondStmtSucc(p, thenBranch, ctx, env) ++ getCondStmtSucc(p, elseBranch, ctx, env)
      case One(CompoundStatement(l)) => getCompoundSucc(l, c, ctx, env)
      case One(s: Statement) => List(s)
    }
  }

  private def getCondStmtPred(p: AST, c: Conditional[_], ctx: FeatureExpr, env: ASTEnv): List[AST] = {
    c match {
      case Choice(_, thenBranch, elseBranch) =>
        getCondStmtPred(p, thenBranch, ctx, env) ++ getCondStmtPred(p, elseBranch, ctx, env)
      case o@One(CompoundStatement(l)) => getCompoundPred(l, o, ctx, env)
      case One(s: Statement) => List(s)
    }
  }

  private def getCondExprSucc(e: Expr, ctx: FeatureExpr, env: ASTEnv) = {
    e match {
      case c@CompoundStatementExpr(CompoundStatement(innerStatements)) =>
        getCompoundSucc(innerStatements, c, ctx, env)
      case _ => List(e)
    }
  }

  private def getCondExprPred(e: Expr, ctx: FeatureExpr, env: ASTEnv) = {
    e match {
      case c@CompoundStatementExpr(CompoundStatement(innerStatements)) => getCompoundPred(innerStatements, c, ctx, env)
      case _ => List(e)
    }
  }

  // handling of successor determination of nested structures, such as for, while, ... and next element in a list
  // of statements
  private def followSucc(nested_ast_elem: Product, ctx: FeatureExpr, env: ASTEnv): List[AST] = {
    nested_ast_elem match {
      case t: ReturnStatement => {
        findPriorASTElem[FunctionDef](t, env) match {
          case None => assert(false, "return statement should always occur within a function statement"); List()
          case Some(f) => List(f)
        }
      }
      case _ => {
        val surrounding_parent = parentAST(nested_ast_elem, env)
        surrounding_parent match {
          // loops
          case t@ForStatement(Some(expr1), expr2, _, s) if (isPartOf(nested_ast_elem, expr1)) =>
            if (expr2.isDefined) getCondExprSucc(expr2.get, ctx, env)
            else getCondStmtSucc(t, s, ctx, env)
          case t@ForStatement(_, Some(expr2), _, s) if (isPartOf(nested_ast_elem, expr2)) =>
            getStmtSucc(t, ctx, env) ++ getCondStmtSucc(t, s, ctx, env)
          case t@ForStatement(_, expr2, Some(expr3), s) if (isPartOf(nested_ast_elem, expr3)) =>
            if (expr2.isDefined) getCondExprSucc(expr2.get, ctx, env)
            else getCondStmtSucc(t, s, ctx, env)
          case t@ForStatement(_, expr2, expr3, s) if (isPartOf(nested_ast_elem, s)) => {
            if (expr3.isDefined) getCondExprSucc(expr3.get, ctx, env)
            else if (expr2.isDefined) getCondExprSucc(expr2.get, ctx, env)
            else getCondStmtSucc(t, s, ctx, env)
          }
          case t@WhileStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) =>
            getCondStmtSucc(t, s, ctx, env) ++ getStmtSucc(t, ctx, env)
          case WhileStatement(expr, s) => List(expr)
          case t@DoStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) =>
            getCondStmtSucc(t, s, ctx, env) ++ getStmtSucc(t, ctx, env)
          case DoStatement(expr, s) => List(expr)

          // conditional statements
          case t@IfStatement(condition, thenBranch, elifs, elseBranch) if (isPartOf(nested_ast_elem, condition)) => {
            var res = getCondStmtSucc(t, thenBranch, ctx, env)
            if (!elifs.isEmpty) res = res ++ getCompoundSucc(elifs, t, ctx, env)
            if (elifs.isEmpty && elseBranch.isDefined) res = res ++ getCondStmtSucc(t, elseBranch.get, ctx, env)
            if (elifs.isEmpty && !elseBranch.isDefined) res = res ++ getStmtSucc(t, ctx, env)
            res
          }

          // either go to next ElifStatement, ElseBranch, or next statement of the surrounding IfStatement
          // filtering is necessary, as else branches are not considered by getSuccSameLevel
          case t@ElifStatement(condition, thenBranch) if (isPartOf(nested_ast_elem, condition)) => {
            var res = getStmtSucc(t, ctx, env)
            if (res.filter(_.isInstanceOf[ElifStatement]).isEmpty) {
              env.parent(env.parent(t)) match {
                case tp@IfStatement(_, _, _, None) => res = getStmtSucc(tp, ctx, env)
                case IfStatement(_, _, _, Some(elseBranch)) => res = getCondStmtSucc(t, elseBranch, ctx, env)
              }
            }
            res ++ getCondStmtSucc(t, thenBranch, ctx, env)
          }
          case t: ElifStatement => followSucc(t, ctx, env)

          // the switch statement behaves like a dynamic goto statement;
          // based on the expression we jump to one of the case statements or default statements
          // after the jump the case/default statements do not matter anymore
          // when hitting a break statement, we jump to the end of the switch
          case t@SwitchStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) => {
            var res: List[AST] = List()
            if (isPartOf(nested_ast_elem, expr)) {
              res = filterCaseStatements(s, env.featureExpr(t), env)
              val dcase = filterDefaultStatements(s, env.featureExpr(t), env)

              if (dcase.isEmpty) res = res ++ getStmtSucc(t, ctx, env)
              else res = res ++ dcase
            }
            res
          }

          case t: Expr => followSucc(t, ctx, env)
          case t: Statement => getStmtSucc(t, ctx, env)

          case t: FunctionDef => List(t)
          case _ => List()
        }
      }
    }
  }

  // method to catch surrounding ast element, which precedes the given nested_ast_element
  private def followPred(nested_ast_elem: Product, ctx: FeatureExpr, env: ASTEnv): List[AST] = {

    def handleSwitch(t: AST) = {
      val prior_switch = findPriorASTElem[SwitchStatement](t, env)
      assert(prior_switch.isDefined, "default statement without surrounding switch")
      prior_switch.get match {
        case SwitchStatement(expr, _) => {
          val lconds = getCondExprPred(expr, ctx, env)
          if (env.previous(t) != null) lconds ++ getStmtPred(t, ctx, env)
          else {
            val tparent = parentAST(t, env)
            if (tparent.isInstanceOf[CaseStatement]) tparent :: lconds  // TODO rewrite, nested cases.
            else lconds ++ getStmtPred(tparent, ctx, env)
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
            getStmtPred(t, ctx, env)
          // inc
          case t@ForStatement(_, _, Some(expr3), s) if (isPartOf(nested_ast_elem, expr3)) =>
            getCondStmtPred(t, s, ctx, env) ++ filterContinueStatements(s, env.featureExpr(t), env)
          // break
          case t@ForStatement(None, Some(expr2), None, One(CompoundStatement(List()))) =>
            List(expr2) ++ getStmtPred(t, ctx, env)
          case t@ForStatement(expr1, Some(expr2), expr3, s) if (isPartOf(nested_ast_elem, expr2)) => {
            var res: List[AST] = List()
            if (expr1.isDefined) res ++= getCondExprPred(expr1.get, ctx, env)
            else res ++= getStmtPred(t, ctx, env)
            if (expr3.isDefined) res ++= getCondExprPred(expr3.get, ctx, env)
            else {
              res ++= getCondStmtPred(t, s, ctx, env)
              res ++= filterContinueStatements(s, env.featureExpr(t), env)
            }
            res
          }
          // s
          case t@ForStatement(expr1, expr2, expr3, s) if (isPartOf(nested_ast_elem, s)) =>
            if (expr2.isDefined) getCondExprPred(expr2.get, ctx, env)
            else if (expr3.isDefined) getCondExprPred(expr3.get, ctx, env)
            else {
              var res: List[AST] = List()
              if (expr1.isDefined) res = res ++ getCondExprPred(expr1.get, ctx, env)
              else res = getStmtPred(t, ctx, env) ++ res
              res = res ++ getCondStmtPred(t, s, ctx, env)
              res
            }

          // while statement consists of (expr, s)
          // special case; we handle empty compound statements here directly because otherwise we do not terminate
          case t@WhileStatement(expr, One(CompoundStatement(List()))) if (isPartOf(nested_ast_elem, expr)) =>
            getStmtPred(t, ctx, env) ++ List(expr)
          case t@WhileStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) =>
            (getStmtPred(t, ctx, env) ++ getCondStmtPred(t, s, ctx, env) ++
              filterContinueStatements(s, env.featureExpr(t), env))
          case t@WhileStatement(expr, _) => {
            if (nested_ast_elem.eq(expr)) getStmtPred(t, ctx, env)
            else getCondExprPred(expr, ctx, env)
          }

          // do statement consists of (expr, s)
          // special case: we handle empty compound statements here directly because otherwise we do not terminate
          case t@DoStatement(expr, One(CompoundStatement(List()))) if (isPartOf(nested_ast_elem, expr)) =>
            getStmtPred(t, ctx, env) ++ List(expr)
          case t@DoStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) =>
            getCondStmtPred(t, s, ctx, env) ++ filterContinueStatements(s, env.featureExpr(t), env)
          case t@DoStatement(expr, s) => {
            if (isPartOf(nested_ast_elem, expr)) getCondStmtPred(t, s, ctx, env)
            else getCondExprPred(expr, ctx, env) ++ getStmtPred(t, ctx, env)
          }

          // conditional statements
          // if statement: control flow comes either out of:
          // elseBranch: elifs + condition is the result
          // elifs: rest of elifs + condition
          // thenBranch: condition
          case t@IfStatement(condition, thenBranch, elifs, elseBranch) => {
            if (isPartOf(nested_ast_elem, condition)) getStmtPred(t, ctx, env)
            else if (isPartOf(nested_ast_elem, thenBranch)) getCondExprPred(condition, ctx, env)
            else if (isPartOf(nested_ast_elem, elseBranch)) {
              if (elifs.isEmpty) getCondExprPred(condition, ctx, env)
              else getCompoundPred(elifs, t, ctx, env)
            } else {
              getStmtPred(nested_ast_elem.asInstanceOf[AST], ctx, env)
            }
          }

          // pred of thenBranch is the condition itself
          // and if we are in condition, we strike for a previous elifstatement or the if itself using
          // getPredSameLevel
          case t@ElifStatement(condition, thenBranch) => {
            if (isPartOf(nested_ast_elem, condition)) predElifStatement(t, ctx, env)
            else List(condition)
          }

          case SwitchStatement(expr, s) if (isPartOf(nested_ast_elem, s)) => getCondExprPred(expr, ctx, env)
          case t: CaseStatement => List(t)

          // pred of default is either the expression of the switch, which is
          // returned by handleSwitch, or a previous statement (e.g.,
          // switch (exp) {
          // ...
          // label1:
          // default: ...)
          // as part of a fall through (sequence of statements without a break and that we catch
          // with getStmtPred
          case t: DefaultStatement => handleSwitch(t) ++ getStmtPred(t, ctx, env)

          case t: CompoundStatementExpr => followPred(t, ctx, env)
          case t: Statement => getStmtPred(t, ctx, env)
          case t: FunctionDef => List(t)
          case _ => List()
        }
      }
    }
  }

  private def predElifStatement(a: ElifStatement, ctx: FeatureExpr, env: ASTEnv): List[AST] = {
    val surrounding_if = parentAST(a, env)
    surrounding_if match {
      case IfStatement(condition, thenBranch, elifs, elseBranch) => {
        var res: List[AST] = List()
        val prev_elifs = elifs.reverse.dropWhile(_.entry.eq(a.asInstanceOf[AnyRef]).unary_!).drop(1)
        val ifdef_blocks = determineIfdefBlocks(prev_elifs, env)
        val grouped_ifdef_blocks = groupIfdefBlocks(ifdef_blocks, env)
        val typed_grouped_ifdef_blocks = determineTypeOfGroupedIfdefBlocks(grouped_ifdef_blocks, ctx, env)
        res = res ++ determineFollowingElements(ctx, typed_grouped_ifdef_blocks, env).merge

        // if no previous elif statement is found, the result is condition
        if (!res.isEmpty) {
          var newres: List[AST] = List()
          for (elem_res <- res) {
            elem_res match {
              case ElifStatement(elif_condition, _) =>
                newres = getCondExprPred(elif_condition, ctx, env) ++ newres
              case _ => newres = elem_res :: newres
            }
          }
          newres
        }
        else getCondExprPred(condition, ctx, env)
      }
      case _ => List()
    }
  }



  // method to find prior element to a break statement
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
  private def getStmtSucc(s: AST, ctx: FeatureExpr, env: ASTEnv): List[AST] = {
    val next_ifdef_blocks = getNextIfdefBlocks(s, ctx, env)
    val next_equal_annotated_ast_element = getNextEqualAnnotatedASTElem(s, next_ifdef_blocks)
    next_equal_annotated_ast_element match {
      // 1.
      case Some(x) => List(x)
      case None => {
        val successor_list = determineFollowingElements(ctx, next_ifdef_blocks.drop(1), env)
        successor_list match {
          case Left(s_list) => s_list // 2.
          case Right(s_list) => s_list ++ followSucc(s, ctx, env) // 3.
        }
      }
    }
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
        case t@CaseStatement(_, s) =>
          (if (env.featureExpr(t) implies ctx isSatisfiable()) List(t) else List()) ++
            (if (s.isDefined) filterCaseStatementsHelper(s.get) else List())
        case SwitchStatement => List()
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
        case SwitchStatement => List()
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
  private def rollUp(source: Product, target: AST, ctx: FeatureExpr, env: ASTEnv): List[AST] = {
    target match {

      // in general all elements from the different branches (thenBranch, elifs, elseBranch)
      // can be predecessors
      case t@IfStatement(condition, thenBranch, elifs, elseBranch) => {
        var res = List[AST]()
        if (elseBranch.isDefined) res ++= getCondStmtPred(t, elseBranch.get, ctx, env)
        if (!elifs.isEmpty) {
          for (Opt(f, elif@ElifStatement(_, thenBranch)) <- elifs) {
            res ++= getCondStmtPred(elif, thenBranch, ctx, env).flatMap(rollUp(source, _, ctx, env))
          }

          // without an else branch, the condition of elifs are possible predecessors of a
          if (elseBranch.isEmpty) res ++= getCompoundPred(elifs, t, ctx, env)
        }
        res ++= getCondStmtPred(t, thenBranch, ctx, env).flatMap(rollUp(source, _, ctx, env))

        if (elifs.isEmpty && elseBranch.isEmpty)
          res ++= getCondExprPred(condition, ctx, env)
        res
      }
      case ElifStatement(condition, thenBranch) => {
        var res = List[AST]()
        res ++= getCondExprPred(condition, ctx, env)
        res ++= getCondStmtPred(target, thenBranch, ctx, env).flatMap(rollUp(source, _, ctx, env))
        res
      }
      case t@SwitchStatement(expr, s) => {
        val lbreaks = filterBreakStatements(s, env.featureExpr(t), env)
        val ldefaults = filterDefaultStatements(s, env.featureExpr(t), env)

        // TODO both lbreaks and ldefaults are empty!
        if (ldefaults.isEmpty) lbreaks ++ getCondExprPred(expr, ctx, env)
        else lbreaks ++ ldefaults.flatMap(rollUpJumpStatement(_, true, ctx, env))
      }

      case t@WhileStatement(expr, s) => List(expr) ++ filterBreakStatements(s, env.featureExpr(t), env)
      case t@DoStatement(expr, s) => List(expr) ++ filterBreakStatements(s, env.featureExpr(t), env)
      case t@ForStatement(_, Some(expr2), _, s) => List(expr2) ++ filterBreakStatements(s, env.featureExpr(t), env)
      case t@ForStatement(_, _, _, s) => filterBreakStatements(s, env.featureExpr(t), env)

      case c@CompoundStatement(innerStatements) => getCompoundPred(innerStatements, c, ctx, env).
        flatMap(rollUp(source, _, ctx, env))

      // in case we found a goto statement we check whether the goto statement has possible targets
      // if so the goto cannot be the pred of our current element
      // if not the goto is the pred of our current element
      case t@GotoStatement(Id(l)) => {
        if (source.isInstanceOf[LabelStatement]) List(target)
        else {
          findPriorASTElem[FunctionDef](t, env) match {
            case None => assert(false, "goto statement should always occur within a function definition"); List()
            case Some(f) => {
              val l_list = labelLookup(f, l, env)
              if (l_list.isEmpty) List(target)
              else List()
            }
          }
        }
      }

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
  private def rollUpJumpStatement(a: AST, fromSwitch: Boolean, ctx: FeatureExpr, env: ASTEnv): List[AST] = {
    a match {
      case t@CaseStatement(_, Some(s)) => getCondStmtPred(t, s, ctx, env).flatMap(rollUpJumpStatement(_, false, ctx, env))

      // the code that belongs to the jump target default is either reachable via nextAST from the
      // default statement: this first case statement here
      // or the code is nested in the DefaultStatement, so we match it with the next case statement
      case t@DefaultStatement(_) if (nextAST(t, env) != null && fromSwitch) => {
        val dparent = findPriorASTElem[CompoundStatement](t, env)
        assert(dparent.isDefined, "default statement always occurs in a compound statement of a switch")
        dparent.get match {
          case c@CompoundStatement(innerStatements) => getCompoundPred(innerStatements, c, ctx, env)
        }
      }
      case t@DefaultStatement(Some(s)) => getCondStmtPred(t, s, ctx, env).flatMap(rollUpJumpStatement(_, false, ctx, env))
      case _: BreakStatement => List()
      case _ => List(a)
    }
  }

  // we have to check possible predecessor nodes in at max three steps:
  // 1. get direct predecessor with same annotation; if yes stop; if not go to step 2.
  // 2. get all annotated elements at the same level and check whether we find a definite set of predecessor nodes
  //    if yes stop; if not go to step 3.
  // 3. get the parent of our node and determine predecessor nodes of it
  private def getStmtPred(s: AST, ctx: FeatureExpr, env: ASTEnv): List[AST] = {
    val previous_ifdef_blocks = getPreviousIfdefBlocks(s, ctx, env)
    val previous_equal_annotated_ast_elem = getNextEqualAnnotatedASTElem(s, previous_ifdef_blocks)
    previous_equal_annotated_ast_elem match {
      // 1.
      case Some(BreakStatement()) => List()
      case Some(x) => List(x).flatMap(rollUpJumpStatement(_, false, ctx, env))
      case None => {
        val predecessor_list = determineFollowingElements(ctx, previous_ifdef_blocks.drop(1), env)
        predecessor_list match {
          case Left(p_list) => p_list.flatMap(rollUpJumpStatement(_, false, ctx, env)) // 2.
          case Right(p_list) => p_list.flatMap(rollUpJumpStatement(_, false, ctx, env)) ++ followPred(s, ctx, env) // 3.
        }
      }
    }
  }

  // given a list of AST elements, determine successor AST elements based on feature expressions
  private def getCompoundSucc(l: List[AST], parent: Product, ctx: FeatureExpr, env: ASTEnv): List[AST] = {
    val ifdef_blocks = determineIfdefBlocks(l, env)
    val grouped_ifdef_blocks = groupIfdefBlocks(ifdef_blocks.reverse, env)
    val typed_grouped_ifdef_blocks = determineTypeOfGroupedIfdefBlocks(grouped_ifdef_blocks, ctx, env).reverse
    val successor_list = determineFollowingElements(ctx, typed_grouped_ifdef_blocks, env)

    successor_list match {
      case Left(s_list) => s_list
      case Right(s_list) => s_list ++ (if (l.isEmpty) followSucc(parent, ctx, env)
                                       else followSucc(l.head, ctx, env))
    }
  }

  // given a list of AST elements, determine predecessor AST elements based on feature expressions
  private def getCompoundPred(l: List[AST], parent: Product, ctx: FeatureExpr, env: ASTEnv): List[AST] = {
    val ifdef_blocks = determineIfdefBlocks(l.reverse, env)
    val grouped_ifdef_blocks = groupIfdefBlocks(ifdef_blocks, env)
    val typed_grouped_ifdef_blocks = determineTypeOfGroupedIfdefBlocks(grouped_ifdef_blocks, ctx, env)
    val predecessor_list = determineFollowingElements(ctx, typed_grouped_ifdef_blocks, env)

    predecessor_list match {
      case Left(p_list) => p_list
      case Right(p_list) => p_list ++ (if (l.isEmpty) followPred(parent, ctx, env)
                                       else followPred(l.reverse.head, ctx, env))
    }
  }

  // returns a list next AST elems grouped according to feature expressions
  private def getNextIfdefBlocks(s: AST, ctx: FeatureExpr, env: ASTEnv): List[(Int, IfdefBlocks)] = {
    val prev_and_next_ast_elems = prevASTElems(s, env) ++ nextASTElems(s, env).drop(1)
    val ifdef_blocks = determineIfdefBlocks(prev_and_next_ast_elems, env)
    val grouped_ifdef_blocks = groupIfdefBlocks(ifdef_blocks, env)
    val typed_grouped_ifdef_blocks = determineTypeOfGroupedIfdefBlocks(grouped_ifdef_blocks, ctx, env)
    getTailList(s, typed_grouped_ifdef_blocks)
  }

  private def getPreviousIfdefBlocks(s: AST, ctx: FeatureExpr, env: ASTEnv) = {
    val prev_and_next_ast_elems = prevASTElems(s, env) ++ nextASTElems(s, env).drop(1)
    val prev_and_next_ast_elems_reversed = prev_and_next_ast_elems.reverse
    val ifdef_blocks = determineIfdefBlocks(prev_and_next_ast_elems_reversed, env)
    val grouped_ifdef_blocks = groupIfdefBlocks(ifdef_blocks, env)
    val typed_grouped_ifdef_blocks = determineTypeOfGroupedIfdefBlocks(grouped_ifdef_blocks, ctx, env)
    getTailList(s, typed_grouped_ifdef_blocks)
  }

  // o occurs somewhere in l
  // determine where using getElementAfterO and return the result
  // if o does not have a next element return None
  // otherwise return Some of the next element
  private def getNextEqualAnnotatedASTElem(o: AST, l: List[(Int, IfdefBlocks)]): Option[AST] = {
    if (l.isEmpty) return None
    val l_with_eq_annotated_elems_as_o = l.head._2

    // using eq to determine o in IfdefBlocks
    // if found get the next element in the list and return it
    def getElementAfterO(ls: IfdefBlocks): Option[AST] = {
      var found_o = false
      for (ifdef_block <- ls) {
        found_o = false
        for (stmt <- ifdef_block) {
          if (found_o) return Some(stmt)
          if (stmt.eq(o)) found_o = true
        }
      }
      None
    }

    getElementAfterO(l_with_eq_annotated_elems_as_o)
  }

  // get list with o and all following lists
  private def getTailList(s: AST, l: List[(Int, IfdefBlocks)]): List[(Int, IfdefBlocks)] = {
    // iterate each sublist of the incoming tuples (Int, List[List[Opt[_]]] combine equality check
    // and drop elements in which s occurs

    def contains(ls: (Int, IfdefBlocks)): Boolean = {
      for (ifdef_block <- ls._2) {
        for (stmt <- ifdef_block) {
          if (stmt.eq(s)) return true
        }
      }
      false
    }

    l.dropWhile(contains(_).unary_!)
  }

  // code works both for succ and pred determination
  // based on the type of the IfdefBlocks (True(0), Optional (1), Alternative (2))
  // the function computes the following elements
  //   context - represents of the element we come frome
  //   l - list of grouped/typed ifdef blocks
  //   env - hold AST environment (parent, children, next, ...)
  private def determineFollowingElements(context: FeatureExpr,
                                         l: List[(Int, IfdefBlocks)],
                                         env: ASTEnv): Either[List[AST], List[AST]] = {
    // context of all added AST nodes that have been added to res
    var rescontext: List[FeatureExpr] = List()

    var res = List[AST]()

    for (e <- l) {
      e match {
        case (0, ifdef_blocks) => return Left(res ++ List(ifdef_blocks.head.head))
        case (_, ifdef_blocks) => {
          for (block <- ifdef_blocks) {
            val bfexp = env.featureExpr(block.head)
            if (context equivalentTo bfexp) return Left(res ++ List(block.head))

            // annotations contradict each other: e.g., A and !A
            if ((context and bfexp) isContradiction()) { }

            // nodes of annotations that have been added before: e.g., ctx is true; A B A true
            // the second A should not be added again because if A is selected the first A would have been selected
            // and not the second one
            else if (rescontext.exists(_ equivalentTo bfexp)) { }

            // otherwise add element and update resulting context
            else {res = res ++ List(block.head); rescontext ::= bfexp}

            if (rescontext.fold(FeatureExprFactory.False)(_ or _) isTautology()) return Left(res)
          }
        }
      }
    }
    Right(res)
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

  // the following functions belong to detection and type cateorization of ifdef blocks
  // pack similar elements into sublists
  private def pack[T](f: (T, T) => Boolean)(l: List[T]): List[List[T]] = {
    if (l.isEmpty) List()
    else (l.head :: l.tail.takeWhile(f(l.head, _))) :: pack(f)(l.tail.dropWhile(f(l.head, _)))
  }

  // group consecutive Opts in a list and return a list of list containing consecutive (feature equivalent) opts
  // e.g.:
  // List(Opt(true, Id1), Opt(fa, Id2), Opt(fa, Id3)) => List(List(Opt(true, Id1)), List(Opt(fa, Id2), Opt(fa, Id3)))
  private[crewrite] def determineIfdefBlocks(l: List[AST], env: ASTEnv): IfdefBlocks = {
    pack[AST](env.featureExpr(_) equivalentTo env.featureExpr(_))(l)
  }

  // group List[List[AST]] according their feature expressions
  // later one should imply the not of previous ones; therefore using l.reverse
  private def groupIfdefBlocks(l: IfdefBlocks, env: ASTEnv) = {
    def checkImplication(a: AST, b: AST) = {
      val as = env.lfeature(a).toSet
      val bs = env.lfeature(b).toSet
      val cs = as.intersect(bs)
      as.--(cs).foldLeft(FeatureExprFactory.True)(_ and _).implies(bs.--(cs).foldLeft(FeatureExprFactory.True)(_ and _).not()).isTautology()
    }
    pack[List[AST]]({(x, y) => checkImplication(x.head, y.head)})(l.reverse).reverse
  }

  // get type of IfdefBlocks:
  // 0 -> only true values
  // 1 -> #if-(#elif)* block
  // 2 -> #if-(#elif)*-#else block
  private def determineTypeOfGroupedIfdefBlocks(l: List[IfdefBlocks], ctx: FeatureExpr, env: ASTEnv): List[(Int, IfdefBlocks)] = {

    l match {
      case (h :: t) => {
        val feature_expr_over_ifdef_blocks = h.map({
          e => env.featureExpr(e.head)
        })

        if ((ctx implies feature_expr_over_ifdef_blocks.foldLeft(FeatureExprFactory.True)(_ and _)).isTautology())
          (0, h) :: determineTypeOfGroupedIfdefBlocks(t, ctx, env)
        else if (feature_expr_over_ifdef_blocks.map(_.not()).foldLeft(FeatureExprFactory.True)(_ and _).isContradiction())
          (2, h.reverse) :: determineTypeOfGroupedIfdefBlocks(t, ctx, env)
        else (1, h) :: determineTypeOfGroupedIfdefBlocks(t, ctx, env)
      }
      case Nil => List()
    }
  }
}

