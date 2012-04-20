package de.fosd.typechef.crewrite


import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.c._
import org.kiama.attribution.AttributionBase
import java.util.IdentityHashMap

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
        var newres: List[AST] = predHelper(a, env)
        var changed = true

        while (changed) {
          changed = false
          oldres = newres
          newres = List()

          for (oldelem <- oldres) {
            var add2newres = List[AST]()
            oldelem match {

              case _: ReturnStatement if (!a.isInstanceOf[FunctionDef]) => changed = true; add2newres = List()

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

                if (a2e.isEmpty) add2newres = rollUp(oldelem, env)
                else if (a2e.isDefined && b2e.isDefined && a2e.get.eq(b2e.get)) add2newres = getCondExprPred(condition, env)
                else add2newres = rollUp(oldelem, env)
              }
              case _ => {
                add2newres = rollUp(oldelem, env)
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

  def predHelper(a: Product, env: ASTEnv): List[AST] = {

    // helper method to handle a switch, if we come from a case or a default statement
    def handleSwitch(t: AST) = {
      val prior_switch = findPriorASTElem[SwitchStatement](t, env)
      assert(prior_switch.isDefined, "default or case statements should always occur withing a switch definition")
      prior_switch.get match {
        case SwitchStatement(expr, _) => getCondExprPred(expr, env) ++ getStmtPred(t, env)
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
            val l_preds = getStmtPred(t, env)
            l_gotos ++ l_preds
          }
        }
      }

      case o: Opt[_] => predHelper(childAST(o), env)
      case c: Conditional[_] => predHelper(childAST(c), env)

      case f@FunctionDef(_, _, _, CompoundStatement(List())) => List(f)
      case f@FunctionDef(_, _, _, stmt) => predHelper(childAST(stmt), env) ++ filterAllASTElems[ReturnStatement](f)
      case CompoundStatement(innerStatements) => getCompoundPred(innerStatements, env)

      case s: Statement => getStmtPred(s, env)
      case _ => followPred(a, env)
    }
  }

  def succ(a: Product, env: ASTEnv): List[AST] = {
    succCCFGCache.lookup(a) match {
      case Some(v) => v
      case None => {
        var oldres: List[AST] = List()
        var newres: List[AST] = succHelper(a, env)
        var changed = true

        while (changed) {
          changed = false
          oldres = newres
          newres = List()
          for (oldelem <- oldres) {
            var add2newres: List[AST] = List()
            oldelem match {
              case _: IfStatement => changed = true; add2newres = succHelper(oldelem, env)
              case _: ElifStatement => changed = true; add2newres = succHelper(oldelem, env)
              case _: SwitchStatement => changed = true; add2newres = succHelper(oldelem, env)
              case _: CompoundStatement => changed = true; add2newres = succHelper(oldelem, env)
              case _: DoStatement => changed = true; add2newres = succHelper(oldelem, env)
              case _: WhileStatement => changed = true; add2newres = succHelper(oldelem, env)
              case _: ForStatement => changed = true; add2newres = succHelper(oldelem, env)
              case _: DefaultStatement => changed = true; add2newres = succHelper(oldelem, env)
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

  private def succHelper(a: Product, env: ASTEnv): List[AST] = {
    a match {
      // ENTRY element
      case f@FunctionDef(_, _, _, stmt) => succHelper(stmt, env)

      // EXIT element
      case t@ReturnStatement(_) => {
        findPriorASTElem[FunctionDef](t, env) match {
          case None => assert(false, "return statement should always occur within a function statement"); List()
          case Some(f) => List(f)
        }
      }

      case t@CompoundStatement(l) => getCompoundSucc(l, t, env)

      case o: Opt[_] => succHelper(o.entry.asInstanceOf[Product], env)
      case t: Conditional[_] => succHelper(childAST(t), env)

      // loop statements
      case ForStatement(None, Some(expr2), None, One(EmptyStatement())) => getCondExprSucc(expr2, env)
      case ForStatement(None, Some(expr2), None, One(CompoundStatement(List()))) => getCondExprSucc(expr2, env)
      case t@ForStatement(expr1, expr2, expr3, s) => {
        if (expr1.isDefined) getCondExprSucc(expr1.get, env)
        else if (expr2.isDefined) getCondExprSucc(expr2.get, env)
        else getCondStmtSucc(t, s, env)
      }
      case WhileStatement(expr, One(EmptyStatement())) => getCondExprSucc(expr, env)
      case WhileStatement(expr, One(CompoundStatement(List()))) => getCondExprSucc(expr, env)
      case WhileStatement(expr, _) => getCondExprSucc(expr, env)
      case DoStatement(expr, One(CompoundStatement(List()))) => getCondExprSucc(expr, env)
      case t@DoStatement(_, s) => getCondStmtSucc(t, s, env)

      // conditional statements
      case t@IfStatement(condition, _, _, _) => getCondExprSucc(condition, env)
      case t@ElifStatement(condition, _) => getCondExprSucc(condition, env)
      case SwitchStatement(expr, _) => getCondExprSucc(expr, env)

      case t@BreakStatement() => {
        val e2b = findPriorASTElem2BreakStatement(t, env)
        assert(e2b.isDefined, "break statement should always occur within a for, do-while, while, or switch statement")
        getStmtSucc(e2b.get, env)
      }
      case t@ContinueStatement() => {
        val e2c = findPriorASTElem2ContinueStatement(t, env)
        assert(e2c.isDefined, "continue statement should always occur within a for, do-while, or while statement")
        e2c.get match {
          case t@ForStatement(_, expr2, expr3, s) => {
            if (expr3.isDefined) getCondExprSucc(expr3.get, env)
            else if (expr2.isDefined) getCondExprSucc(expr2.get, env)
            else getCondStmtSucc(t, s, env)
          }
          case WhileStatement(expr, _) => getCondExprSucc(expr, env)
          case DoStatement(expr, _) => getCondExprSucc(expr, env)
          case _ => List()
        }
      }
      case t@GotoStatement(Id(l)) => {
        findPriorASTElem[FunctionDef](t, env) match {
          case None => assert(false, "goto statement should always occur within a function definition"); List()
          case Some(f) => {
            val l_list = labelLookup(f, l, env)
            if (l_list.isEmpty) getStmtSucc(t, env)
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
            val l_list = filterAllASTElems[LabelStatement](f)
            if (l_list.isEmpty) getStmtSucc(t, env)
            else l_list
          }
        }
      }

      case t@CaseStatement(_, Some(s)) => getCondStmtSucc(t, s, env)
      case t: CaseStatement => getStmtSucc(t, env)

      case t@DefaultStatement(Some(s)) => getCondStmtSucc(t, s, env)
      case t: DefaultStatement => getStmtSucc(t, env)

      case t: Statement => getStmtSucc(t, env)
      case t => followSucc(t, env)
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

  private def getCondStmtSucc(p: AST, c: Conditional[_], env: ASTEnv): List[AST] = {
    c match {
      case Choice(_, thenBranch, elseBranch) => getCondStmtSucc(p, thenBranch, env) ++ getCondStmtSucc(p, elseBranch, env)
      case One(CompoundStatement(l)) => getCompoundSucc(l, c, env)
      case One(s: Statement) => List(s)
    }
  }

  private def getCondStmtPred(p: AST, c: Conditional[_], env: ASTEnv): List[AST] = {
    c match {
      case Choice(_, thenBranch, elseBranch) => getCondStmtPred(p, thenBranch, env) ++ getCondStmtPred(p, elseBranch, env)
      case One(CompoundStatement(l)) => getCompoundPred(l, env)
      case One(s: Statement) => List(s)
    }
  }

  private def getCondExprSucc(e: Expr, env: ASTEnv) = {
    e match {
      case c@CompoundStatementExpr(CompoundStatement(innerStatements)) =>
        getCompoundSucc(innerStatements, c, env)
      case _ => List(e)
    }
  }

  private def getCondExprPred(e: Expr, env: ASTEnv) = {
    e match {
      case CompoundStatementExpr(CompoundStatement(innerStatements)) => getCompoundPred(innerStatements, env)
      case _ => List(e)
    }
  }

  // handling of successor determination of nested structures, such as for, while, ... and next element in a list
  // of statements
  private def followSucc(nested_ast_elem: Product, env: ASTEnv): List[AST] = {
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
            if (expr2.isDefined) getCondExprSucc(expr2.get, env)
            else getCondStmtSucc(t, s, env)
          case t@ForStatement(_, Some(expr2), _, s) if (isPartOf(nested_ast_elem, expr2)) =>
            getStmtSucc(t, env) ++ getCondStmtSucc(t, s, env)
          case t@ForStatement(_, expr2, Some(expr3), s) if (isPartOf(nested_ast_elem, expr3)) =>
            if (expr2.isDefined) getCondExprSucc(expr2.get, env)
            else getCondStmtSucc(t, s, env)
          case t@ForStatement(_, expr2, expr3, s) if (isPartOf(nested_ast_elem, s)) => {
            if (expr3.isDefined) getCondExprSucc(expr3.get, env)
            else if (expr2.isDefined) getCondExprSucc(expr2.get, env)
            else getCondStmtSucc(t, s, env)
          }
          case t@WhileStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) => getCondStmtSucc(t, s, env) ++ getStmtSucc(t, env)
          case WhileStatement(expr, s) => List(expr)
          case t@DoStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) => getCondStmtSucc(t, s, env) ++ getStmtSucc(t, env)
          case DoStatement(expr, s) => List(expr)

          // conditional statements
          case t@IfStatement(condition, thenBranch, elifs, elseBranch) if (isPartOf(nested_ast_elem, condition)) => {
            var res = getCondStmtSucc(t, thenBranch, env)
            if (!elifs.isEmpty) res = res ++ getCompoundSucc(elifs, t, env)
            if (elifs.isEmpty && elseBranch.isDefined) res = res ++ getCondStmtSucc(t, elseBranch.get, env)
            if (elifs.isEmpty && !elseBranch.isDefined) res = res ++ getStmtSucc(t, env)
            res
          }

          // either go to next ElifStatement, ElseBranch, or next statement of the surrounding IfStatement
          // filtering is necessary, as else branches are not considered by getSuccSameLevel
          case t@ElifStatement(condition, thenBranch) if (isPartOf(nested_ast_elem, condition)) => {
            var res = getStmtSucc(t, env)
            if (res.filter(_.isInstanceOf[ElifStatement]).isEmpty) {
              env.parent(env.parent(t)) match {
                case tp@IfStatement(_, _, _, None) => res = getStmtSucc(tp, env)
                case IfStatement(_, _, _, Some(elseBranch)) => res = getCondStmtSucc(t, elseBranch, env)
              }
            }
            res ++ getCondStmtSucc(t, thenBranch, env)
          }
          case t: ElifStatement => followSucc(t, env)

          // the switch statement behaves like a dynamic goto statement;
          // based on the expression we jump to one of the case statements or default statements
          // after the jump the case/default statements do not matter anymore
          // when hitting a break statement, we jump to the end of the switch
          case t@SwitchStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) => {
            var res: List[AST] = List()
            if (isPartOf(nested_ast_elem, expr)) {
              res = filterCaseStatements(s, env)
              val dcase = filterDefaultStatements(s, env)

              if (dcase.isEmpty) res = res ++ getStmtSucc(t, env)
              else res = res ++ dcase
            }
            res
          }

          case t: Expr => followSucc(t, env)
          case t: Statement => getStmtSucc(t, env)

          case t: FunctionDef => List(t)
          case _ => List()
        }
      }
    }
  }

  // method to catch surrounding ast element, which precedes the given nested_ast_element
  private def followPred(nested_ast_elem: Product, env: ASTEnv): List[AST] = {

    def handleSwitch(t: AST) = {
      val prior_switch = findPriorASTElem[SwitchStatement](t, env)
      assert(prior_switch.isDefined, "default statement without surrounding switch")
      prior_switch.get match {
        case SwitchStatement(expr, _) => {
          val lconds = getCondExprPred(expr, env)
          if (env.previous(t) != null) lconds ++ getStmtPred(t, env)
          else {
            val tparent = parentAST(t, env)
            if (tparent.isInstanceOf[CaseStatement]) tparent :: lconds  // TODO rewrite, nested cases.
            else lconds ++ getStmtPred(tparent, env)
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
            getStmtPred(t, env)
          // inc
          case t@ForStatement(_, _, Some(expr3), s) if (isPartOf(nested_ast_elem, expr3)) =>
            getCondStmtPred(t, s, env) ++ filterContinueStatements(s, env)
          // break
          case t@ForStatement(None, Some(expr2), None, One(CompoundStatement(List()))) =>
            List(expr2) ++ getStmtPred(t, env)
          case t@ForStatement(expr1, Some(expr2), expr3, s) if (isPartOf(nested_ast_elem, expr2)) => {
            var res: List[AST] = List()
            if (expr1.isDefined) res ++= getCondExprPred(expr1.get, env)
            else res ++= getStmtPred(t, env)
            if (expr3.isDefined) res ++= getCondExprPred(expr3.get, env)
            else {
              res ++= getCondStmtPred(t, s, env)
              res ++= filterContinueStatements(s, env)
            }
            res
          }
          // s
          case t@ForStatement(expr1, expr2, expr3, s) if (isPartOf(nested_ast_elem, s)) =>
            if (expr2.isDefined) getCondExprPred(expr2.get, env)
            else if (expr3.isDefined) getCondExprPred(expr3.get, env)
            else {
              var res: List[AST] = List()
              if (expr1.isDefined) res = res ++ getCondExprPred(expr1.get, env)
              else res = getStmtPred(t, env) ++ res
              res = res ++ getCondStmtPred(t, s, env)
              res
            }

          // while statement consists of (expr, s)
          // special case; we handle empty compound statements here directly because otherwise we do not terminate
          case t@WhileStatement(expr, One(CompoundStatement(List()))) if (isPartOf(nested_ast_elem, expr)) =>
            getStmtPred(t, env) ++ List(expr)
          case t@WhileStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) =>
            getStmtPred(t, env) ++ getCondStmtPred(t, s, env) ++ filterContinueStatements(s, env)
          case t@WhileStatement(expr, _) => {
            if (nested_ast_elem.eq(expr)) getStmtPred(t, env)
            else getCondExprPred(expr, env)
          }

          // do statement consists of (expr, s)
          // special case: we handle empty compound statements here directly because otherwise we do not terminate
          case t@DoStatement(expr, One(CompoundStatement(List()))) if (isPartOf(nested_ast_elem, expr)) =>
            getStmtPred(t, env) ++ List(expr)
          case t@DoStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) =>
            getCondStmtPred(t, s, env) ++ filterContinueStatements(s, env)
          case t@DoStatement(expr, s) => {
            if (isPartOf(nested_ast_elem, expr)) getCondStmtPred(t, s, env)
            else getCondExprPred(expr, env) ++ getStmtPred(t, env)
          }

          // conditional statements
          // if statement: control flow comes either out of:
          // elseBranch: elifs + condition is the result
          // elifs: rest of elifs + condition
          // thenBranch: condition
          case t@IfStatement(condition, thenBranch, elifs, elseBranch) => {
            if (isPartOf(nested_ast_elem, condition)) getStmtPred(t, env)
            else if (isPartOf(nested_ast_elem, thenBranch)) getCondExprPred(condition, env)
            else if (isPartOf(nested_ast_elem, elseBranch)) {
              if (elifs.isEmpty) getCondExprPred(condition, env)
              else getCompoundPred(elifs, env)
            } else {
              getStmtPred(nested_ast_elem.asInstanceOf[AST], env)
            }
          }

          // pred of thenBranch is the condition itself
          // and if we are in condition, we strike for a previous elifstatement or the if itself using
          // getPredSameLevel
          case t@ElifStatement(condition, thenBranch) => {
            if (isPartOf(nested_ast_elem, condition)) predElifStatement(t, env)
            else List(condition)
          }

          case SwitchStatement(expr, s) if (isPartOf(nested_ast_elem, s)) => getCondExprPred(expr, env)
          case t: CaseStatement => List(t)
          //case t: DefaultStatement => List(t)
          case t: DefaultStatement => handleSwitch(t)

          case t: CompoundStatementExpr => followPred(t, env)
          case t: Statement => getStmtPred(t, env)
          case t: FunctionDef => List(t)
          case _ => List()
        }
      }
    }
  }

  private def predElifStatement(a: ElifStatement, env: ASTEnv): List[AST] = {
    val surrounding_if = parentAST(a, env)
    surrounding_if match {
      case IfStatement(condition, thenBranch, elifs, elseBranch) => {
        var res: List[AST] = List()
        val prev_elifs = elifs.reverse.dropWhile(_.entry.eq(a.asInstanceOf[AnyRef]).unary_!).drop(1)
        val eliffexp = env.featureExpr(a)
        val ifdef_blocks = determineIfdefBlocks(prev_elifs, env)
        val grouped_ifdef_blocks = groupIfdefBlocks(ifdef_blocks, env)
        val typed_grouped_ifdef_blocks = determineTypeOfGroupedIfdefBlocks(grouped_ifdef_blocks, env)
        res = res ++ determineFollowingElements(eliffexp, typed_grouped_ifdef_blocks, env).merge

        // if no previous elif statement is found, the result is condition
        if (!res.isEmpty) {
          var newres: List[AST] = List()
          for (elem_res <- res) {
            elem_res match {
              case ElifStatement(elif_condition, _) =>
                newres = getCondExprPred(elif_condition, env) ++ newres
              case _ => newres = elem_res :: newres
            }
          }
          newres
        }
        else getCondExprPred(condition, env)
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
  private def getStmtSucc(s: AST, env: ASTEnv): List[AST] = {
    val next_ifdef_blocks = getNextIfdefBlocks(s, env)
    val next_equal_annotated_ast_element = getNextEqualAnnotatedASTElem(s, next_ifdef_blocks)
    next_equal_annotated_ast_element match {
      // 1.
      case Some(x) => List(x)
      case None => {
        val parentsfexp = if (env.parent(s) != null) env.featureExpr(env.parent(s)) else FeatureExpr.base
        val successor_list = determineFollowingElements(parentsfexp, next_ifdef_blocks.drop(1), env)
        successor_list match {
          case Left(s_list) => s_list // 2.
          case Right(s_list) => s_list ++ followSucc(s, env) // 3.
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
  private def filterBreakStatements(c: Conditional[Statement], env: ASTEnv): List[BreakStatement] = {
    def filterBreakStatementsHelper(a: Any): List[BreakStatement] = {
      a match {
        case t: BreakStatement => List(t)
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
  private def filterContinueStatements(c: Conditional[Statement], env: ASTEnv): List[ContinueStatement] = {
    def filterContinueStatementsHelper(a: Any): List[ContinueStatement] = {
      a match {
        case t: ContinueStatement => List(t)
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
  private def filterCaseStatements(c: Conditional[Statement], env: ASTEnv): List[CaseStatement] = {
    def filterCaseStatementsHelper(a: Any): List[CaseStatement] = {
      a match {
        case t@CaseStatement(_, s) => List(t) ++ (if (s.isDefined) filterCaseStatementsHelper(s.get) else List())
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
  private def filterDefaultStatements(c: Conditional[Statement], env: ASTEnv): List[DefaultStatement] = {
    def filterDefaultStatementsHelper(a: Any): List[DefaultStatement] = {
      a match {
        case SwitchStatement => List()
        case t: DefaultStatement => List(t)
        case l: List[_] => l.flatMap(filterDefaultStatementsHelper(_))
        case x: Product => x.productIterator.toList.flatMap(filterDefaultStatementsHelper(_))
        case _ => List()
      }
    }
    filterDefaultStatementsHelper(c)
  }

  // in predecessor determination we have to dig in into elements at certain points
  // we dig into ast that have an Conditional part, such as for, while, ...
  private def rollUp(a: AST, env: ASTEnv): List[AST] = {
    a match {

      // in general all elements from the different branches (thenBranch, elifs, elseBranch)
      // can be predecessors
      case t@IfStatement(condition, thenBranch, elifs, elseBranch) => {
        var res = List[AST]()
        if (elseBranch.isDefined) res ++= getCondStmtPred(t, elseBranch.get, env)
        if (!elifs.isEmpty) {
          for (Opt(f, elif@ElifStatement(_, thenBranch)) <- elifs) {
            res ++= getCondStmtPred(elif, thenBranch, env).flatMap(rollUp(_, env))
          }

          // without an else branch, the condition of elifs are possible predecessors of a
          if (elseBranch.isEmpty) res ++= getCompoundPred(elifs, env)
        }
        res ++= getCondStmtPred(t, thenBranch, env).flatMap(rollUp(_, env))

        if (elifs.isEmpty && elseBranch.isEmpty)
          res ++= getCondExprPred(condition, env)
        res
      }
      case ElifStatement(condition, thenBranch) => {
        var res = List[AST]()
        res ++= getCondExprPred(condition, env)
        res ++= getCondStmtPred(a, thenBranch, env).flatMap(rollUp(_, env))
        res
      }
      case t@SwitchStatement(expr, s) => {
        val lbreaks = filterBreakStatements(s, env)
        val ldefaults = filterDefaultStatements(s, env)

        // TODO both lbreaks and ldefaults are empty!
        if (ldefaults.isEmpty) lbreaks ++ getCondExprPred(expr, env)
        else lbreaks ++ ldefaults.flatMap(rollUpJumpStatement(_, true, env))
      }

      case t@WhileStatement(expr, s) => List(expr) ++ filterBreakStatements(s, env)
      case t@DoStatement(expr, s) => List(expr) ++ filterBreakStatements(s, env)
      case t@ForStatement(_, Some(expr2), _, s) => List(expr2) ++ filterBreakStatements(s, env)
      case t@ForStatement(_, _, _, s) => filterBreakStatements(s, env)

      case CompoundStatement(innerStatements) => getCompoundPred(innerStatements, env).flatMap(rollUp(_, env))

      case _ => List(a)
    }
  }

  // we have a separate rollUp function for CaseStatement, DefaultStatement, and BreakStatement
  // because using rollUp in pred determination (see above) will return wrong results
  private def rollUpJumpStatement(a: AST, fromSwitch: Boolean, env: ASTEnv): List[AST] = {
    a match {
      case t@CaseStatement(_, Some(s)) => getCondStmtPred(t, s, env).flatMap(rollUpJumpStatement(_, false, env))

      // the code that belongs to the jump target default is either reachable via nextAST from the
      // default statement: this first case statement here
      // or the code is nested in the DefaultStatement, so we match it with the next case statement
      case t@DefaultStatement(_) if (nextAST(t, env) != null && fromSwitch) => {
        val dparent = findPriorASTElem[CompoundStatement](t, env)
        assert(dparent.isDefined, "default statement always occurs in a compound statement of a switch")
        dparent.get match {
          case CompoundStatement(innerStatements) => getCompoundPred(innerStatements, env)
        }
      }
      case t@DefaultStatement(Some(s)) => getCondStmtPred(t, s, env).flatMap(rollUpJumpStatement(_, false, env))
      case _: BreakStatement => List()
      case _ => List(a)
    }
  }

  // we have to check possible predecessor nodes in at max three steps:
  // 1. get direct predecessor with same annotation; if yes stop; if not go to step 2.
  // 2. get all annotated elements at the same level and check whether we find a definite set of predecessor nodes
  //    if yes stop; if not go to step 3.
  // 3. get the parent of our node and determine predecessor nodes of it
  private def getStmtPred(s: AST, env: ASTEnv): List[AST] = {
    val previous_ifdef_blocks = getPreviousIfdefBlocks(s, env)
    val previous_equal_annotated_ast_elem = getNextEqualAnnotatedASTElem(s, previous_ifdef_blocks)
    previous_equal_annotated_ast_elem match {
      // 1.
      case Some(BreakStatement()) => List()
      case Some(x) => List(x).flatMap(rollUpJumpStatement(_, false, env))
      case None => {
        val parentsfexp = if (env.parent(s) != null) env.featureExpr(env.parent(s)) else FeatureExpr.base
        val predecessor_list = determineFollowingElements(parentsfexp, previous_ifdef_blocks.drop(1), env)
        predecessor_list match {
          case Left(p_list) => p_list.flatMap(rollUpJumpStatement(_, false, env)) // 2.
          case Right(p_list) => p_list.flatMap(rollUpJumpStatement(_, false, env)) ++ followPred(s, env) // 3.
        }
      }
    }
  }

  // given a list of AST elements, determine successor AST elements based on feature expressions
  private def getCompoundSucc(l: List[AST], parent: Product, env: ASTEnv): List[AST] = {
    val ifdef_blocks = determineIfdefBlocks(l, env)
    val grouped_ifdef_blocks = groupIfdefBlocks(ifdef_blocks, env).reverse
    val typed_grouped_ifdef_blocks = determineTypeOfGroupedIfdefBlocks(grouped_ifdef_blocks, env).reverse
    val successor_list = determineFollowingElements(env.featureExpr(parent), typed_grouped_ifdef_blocks, env)

    successor_list match {
      case Left(s_list) => s_list
      case Right(s_list) => s_list ++ (if (l.isEmpty) followSucc(parent, env)
                                       else followSucc(l.head, env))
    }
  }

  // given a list of AST elements, determine predecessor AST elements based on feature expressions
  private def getCompoundPred(l: List[AST], env: ASTEnv): List[AST] = {
    if (l.isEmpty) List()
    else {
      val ifdef_blocks = determineIfdefBlocks(l.reverse, env)
      val grouped_ifdef_blocks = groupIfdefBlocks(ifdef_blocks, env)
      val typed_grouped_ifdef_blocks = determineTypeOfGroupedIfdefBlocks(grouped_ifdef_blocks, env)
      val compoundstmtfexp = env.featureExpr(env.parent(l.head))
      val predecessor_list = determineFollowingElements(compoundstmtfexp, typed_grouped_ifdef_blocks, env)

      predecessor_list match {
        case Left(p_list) => p_list
        case Right(p_list) => p_list ++ followPred(l.reverse.head, env)
      }
    }
  }

  // returns a list next AST elems grouped according to feature expressions
  private def getNextIfdefBlocks(s: AST, env: ASTEnv): List[(Int, IfdefBlocks)] = {
    val prev_and_next_ast_elems = prevASTElems(s, env) ++ nextASTElems(s, env).drop(1)
    val ifdef_blocks = determineIfdefBlocks(prev_and_next_ast_elems, env)
    val grouped_ifdef_blocks = groupIfdefBlocks(ifdef_blocks, env)
    val typed_grouped_ifdef_blocks = determineTypeOfGroupedIfdefBlocks(grouped_ifdef_blocks, env)
    getTailList(s, typed_grouped_ifdef_blocks)
  }

  private def getPreviousIfdefBlocks(s: AST, env: ASTEnv) = {
    val prev_and_next_ast_elems = prevASTElems(s, env) ++ nextASTElems(s, env).drop(1)
    val prev_and_next_ast_elems_reversed = prev_and_next_ast_elems.reverse
    val ifdef_blocks = determineIfdefBlocks(prev_and_next_ast_elems_reversed, env)
    val grouped_ifdef_blocks = groupIfdefBlocks(ifdef_blocks, env)
    val typed_grouped_ifdef_blocks = determineTypeOfGroupedIfdefBlocks(grouped_ifdef_blocks, env)
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
  //   context - represents surrounding annotation context
  //   l - list of grouped/typed ifdef blocks
  //   env - hold AST environment (parent, children, next, ...)
  private def determineFollowingElements(context: FeatureExpr,
                                         l: List[(Int, IfdefBlocks)],
                                         env: ASTEnv): Either[List[AST], List[AST]] = {
    var res = List[AST]()
    for (e <- l) {
      e match {
        case (0, ifdef_blocks) => return Left(res ++ List(ifdef_blocks.head.head))
        case (1, ifdef_blocks) => {
          res = res ++ ifdef_blocks.flatMap({
            x => List(x.head)
          })
          val e_feature_expr = env.featureExpr(ifdef_blocks.head.head)
          if (e_feature_expr.equivalentTo(context)) return Left(res)
        }
        case (2, ifdef_blocks) => return Left(res ++ ifdef_blocks.flatMap({
          x => List(x.head)
        }))
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
      as.--(cs).foldLeft(FeatureExpr.base)(_ and _).implies(bs.--(cs).foldLeft(FeatureExpr.base)(_ and _).not()).isTautology()
    }
    pack[List[AST]]({
      (x, y) => checkImplication(x.head, y.head)
    })(l.reverse).reverse
  }

  // get type of IfdefBlocks:
  // 0 -> only true values
  // 1 -> #if-(#elif)* block
  // 2 -> #if-(#elif)*-#else block
  private def determineTypeOfGroupedIfdefBlocks(l: List[IfdefBlocks], env: ASTEnv): List[(Int, IfdefBlocks)] = {

    l match {
      case (h :: t) => {
        val feature_expr_over_ifdef_blocks = h.map({
          e => env.featureExpr(e.head)
        })

        if (feature_expr_over_ifdef_blocks.foldLeft(FeatureExpr.base)(_ and _).isTautology())
          (0, h) :: determineTypeOfGroupedIfdefBlocks(t, env)
        else if (feature_expr_over_ifdef_blocks.map(_.not()).foldLeft(FeatureExpr.base)(_ and _).isContradiction())
          (2, h.reverse) :: determineTypeOfGroupedIfdefBlocks(t, env)
        else (1, h) :: determineTypeOfGroupedIfdefBlocks(t, env)
      }
      case Nil => List()
    }
  }
}

// defines and uses we can jump to using succ
// beware of List[Opt[_]]!! all list elements can possibly have a different annotation
trait Variables extends ASTNavigation {
  val uses: PartialFunction[Any, Set[Id]] = {
    case a: Any => findUses(a)
  }

  private def findUses(e: Any): Set[Id] = {
    e match {
      case ForStatement(expr1, expr2, expr3, _) => uses(expr1) ++ uses(expr2) ++ uses(expr3)
      case ReturnStatement(Some(x)) => uses(x)
      case WhileStatement(expr, _) => uses(expr)
      case DeclarationStatement(d) => uses(d)
      case Declaration(_, init) => init.flatMap(uses).toSet
      case InitDeclaratorI(_, _, Some(i)) => uses(i)
      case AtomicNamedDeclarator(_, id, _) => Set(id)
      case NestedNamedDeclarator(_, nestedDecl, _) => uses(nestedDecl)
      case Initializer(_, expr) => uses(expr)
      case i@Id(name) => Set(i)
      case PointerPostfixSuffix(kind, id) => Set(id)
      case FunctionCall(params) => params.exprs.map(_.entry).flatMap(uses).toSet
      case ArrayAccess(expr) => uses(expr)
      case PostfixExpr(p, s) => uses(p) ++ uses(s)
      case UnaryExpr(_, ex) => uses(ex)
      case SizeOfExprU(expr) => uses(expr)
      case CastExpr(_, expr) => uses(expr)
      case PointerDerefExpr(castExpr) => uses(castExpr)
      case PointerCreationExpr(castExpr) => uses(castExpr)
      case UnaryOpExpr(kind, castExpr) => uses(castExpr)
      case NAryExpr(ex, others) => uses(ex) ++ others.flatMap(uses).toSet
      case NArySubExpr(_, ex) => uses(ex)
      case ConditionalExpr(condition, _, _) => uses(condition)
      case ExprStatement(expr) => uses(expr)
      case AssignExpr(_, _, source) => uses(source)
      case Opt(_, entry) => uses(entry)
      case _ => Set()
    }
  }

  val defines: PartialFunction[Any, Set[Id]] = {
    case ExprStatement(expr) => defines(expr)
    case i@Id(_) => Set(i)
    case AssignExpr(target, _, source) => defines(target)
    case DeclarationStatement(d) => defines(d)
    case Declaration(_, init) => init.flatMap(defines).toSet
    case InitDeclaratorI(a, _, _) => defines(a)
    case AtomicNamedDeclarator(_, i, _) => Set(i)
    case o@Opt(_, entry) => defines(entry)
    case _ => Set()
  }
}

class LivenessCache {
  private val cache: IdentityHashMap[Any, Map[FeatureExpr, Set[Id]]] = new IdentityHashMap[Any, Map[FeatureExpr, Set[Id]]]()

  def update(k: Any, v: Map[FeatureExpr, Set[Id]]) {
    cache.put(k, v)
  }

  def lookup(k: Any): Option[Map[FeatureExpr, Set[Id]]] = {
    val v = cache.get(k)
    if (v != null) Some(v)
    else None
  }
}

trait Liveness extends AttributionBase with Variables with ConditionalControlFlow {

  private val incache = new LivenessCache()
  private val outcache = new LivenessCache()

  private def updateMap(m: Map[FeatureExpr, Set[Id]],
                        e: (FeatureExpr, Set[Id]),
                        op: (Set[Id], Set[Id]) => Set[Id]): Map[FeatureExpr, Set[Id]] = {
    val key = m.find(_._1.equivalentTo(e._1))
    key match {
      case None => m.+(e)
      // beware op is not symetric, first element of op application should always the current
      // value element of the map (here v)
      case Some((k, v)) => m.+((k, op(v, e._2)))
    }
  }

  // cache for in; we have to store all tuples of (a, env) their because using
  // (a, env) always creates a new one!!! and circular internally uses another
  // IdentityHashMap and uses (a, env) as a key there.
  private val astIdenEnvHM = new IdentityHashMap[AST, (AST, ASTEnv)]()
  private implicit def astIdenTup(a: AST) = astIdenEnvHM.get(a)

  // cf. http://www.cs.colostate.edu/~mstrout/CS553/slides/lecture03.pdf
  // page 5
  //  in(n) = uses(n) + (out(n) - defines(n))
  // out(n) = for s in succ(n) r = r + in(s); r
  // insimple and outsimple are the non variability-aware in and out versiosn
  // of liveness determination
  val insimple: PartialFunction[(Product, ASTEnv), Set[Id]] = {
    circular[(Product, ASTEnv), Set[Id]](Set()) {
      case t@(FunctionDef(_, _, _, _), _) => Set()
      case t@(e, env) => {
        val u = uses(e)
        val d = defines(e)
        var res = outsimple(t)

        res = u.union(res.diff(d))
        res
      }
    }
  }

  val outsimple: PartialFunction[(Product, ASTEnv), Set[Id]] = {
    circular[(Product, ASTEnv), Set[Id]](Set()) {
      case t@(e, env) => {
        val ss = succ(e, env).filterNot(_.isInstanceOf[FunctionDef])
        var res: Set[Id] = Set()
        for (s <- ss) {
          if (!astIdenEnvHM.containsKey(s)) astIdenEnvHM.put(s, (s, env))
          res = res.union(insimple(s))
        }
        res
      }
    }
  }

  // in and out variability-aware versions
  val inrec: PartialFunction[(Product, ASTEnv), Map[FeatureExpr, Set[Id]]] = {
    circular[(Product, ASTEnv), Map[FeatureExpr, Set[Id]]](Map()) {
      case t@(FunctionDef(_, _, _, _), _) => Map()
      case t@(e, env) => {
        val u = uses(e)
        val d = defines(e)
        var res = out(t)
        res = updateMap(res, (env.featureExpr(e), d), _.diff(_))
        res = updateMap(res, (env.featureExpr(e), u), _.union(_))
        res
      }
    }
  }

  val outrec: PartialFunction[(Product, ASTEnv), Map[FeatureExpr, Set[Id]]] =
    circular[(Product, ASTEnv), Map[FeatureExpr, Set[Id]]](Map()) {
      case t@(e, env) => {
        val sl = succ(e, env).filterNot(_.isInstanceOf[FunctionDef])
        var res = Map[FeatureExpr, Set[Id]]()
        for (a <- sl) {
          if (!astIdenEnvHM.containsKey(a)) astIdenEnvHM.put(a, (a, env))
          for (el <- in(a))
            res = updateMap(res, el, _.union(_))
        }
        res
      }
    }

  def out(a: (Product, ASTEnv)) = {
    outcache.lookup(a._1) match {
      case Some(v) => v
      case None => {
        val r = outrec(a)
        outcache.update(a._1, r)
        r
      }
    }
  }

  def in(a: (Product, ASTEnv)) = {
    incache.lookup(a._1) match {
      case Some(v) => v
      case None => {
        val r = inrec(a)
        incache.update(a._1, r)
        r
      }
    }
  }
}
