package de.fosd.typechef.crewrite


import de.fosd.typechef.conditional._
import de.fosd.typechef.parser.c._
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
  private val cache: java.util.IdentityHashMap[Product, List[AST]] = new java.util.IdentityHashMap[Product, List[AST]]()

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

  // result type of pred/succ determination
  type CCFGRes = List[(FeatureExpr, FeatureExpr, AST)]

  // during traversal of AST elements, we sometimes dig into elements, and don't want to get out again
  // we use the barrier list to add elements we do not want to get out again; TODO should not be necessary
  // in case we determine where we come from.
  var barrier: List[AST] = List()

  // determines predecessor of a given element
  // results are cached for secondary evaluation
  def pred(source: Product, env: ASTEnv): List[AST] = {
    predCCFGCache.lookup(source) match {
      case Some(v) => v
      case None => {
        var oldres: CCFGRes = List()
        val ctx = env.featureExpr(source)

        if (ctx isContradiction()) return List()

        var newres: CCFGRes = predHelper(source, ctx, oldres, env)
        var changed = true

        while (changed) {
          changed = false
          oldres = newres
          newres = List()

          for (oldelem <- oldres) {
            var add2newres: CCFGRes = List()
            oldelem._3 match {

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
                else add2newres = List((env.featureExpr(b), env.featureExpr(b), b))
              }
              // a continue statement shall appear only in a loop body
              // a continue statement causes a jump to the loop-continuation portion
              // of the smallest enclosing iteration statement
              case c: ContinueStatement => {
                val a2c = findPriorASTElem2ContinueStatement(source, env)
                val b2c = findPriorASTElem2ContinueStatement(c, env)

                if (a2c.isDefined && b2c.isDefined && a2c.get.eq(b2c.get)) {
                  a2c.get match {
                    case WhileStatement(expr, _) if (isPartOf(source, expr)) => add2newres = List((env.featureExpr(c), env.featureExpr(c), c))
                    case DoStatement(expr, _) if (isPartOf(source, expr)) => add2newres = List((env.featureExpr(c), env.featureExpr(c), c))
                    case ForStatement(_, Some(expr2), None, _) if (isPartOf(source, expr2)) => add2newres = List((env.featureExpr(c), env.featureExpr(c), c))
                    case ForStatement(_, _, Some(expr3), _) if (isPartOf(source, expr3)) => add2newres = List((env.featureExpr(c), env.featureExpr(c), c))
                    case _ => add2newres = List()
                  }
                } else add2newres = List()
              }

              // goto statements
              // in general only label statements can be the source of goto statements
              // and only the ones that have the same name
              case s@GotoStatement(Id(name)) => {
                if (source.isInstanceOf[LabelStatement]) {
                  val lname = source.asInstanceOf[LabelStatement].id.name
                  if (name == lname) add2newres = List((env.featureExpr(s), env.featureExpr(s), s))
                }
              }

              case _ => add2newres = List(oldelem)
            }

            // add only elements that are not in newres so far
            for (addnew <- add2newres)
              if (! newres.exists(_._3.eq(addnew._3))) newres ::= addnew
          }
        }

        val newret = newres.map(_._3)
        predCCFGCache.update(source, newret)
        newret
      }
    }
  }

  // determine context of new element based on the current result
  // the context is the not of all elements (or-d) together combined with the context of the source elemente
  // (ctx) and the context of the current element (curctx)
  private def getNewResCtx(curres: CCFGRes, ctx: FeatureExpr, curctx: FeatureExpr) = {
    curres.map(_._1).fold(FeatureExprFactory.False)(_ or _).not() and ctx and curctx
  }

  // checks reference equality of e in a given struture t (either product or list)
  private def isPartOf(subterm: Product, term: Any): Boolean = {
    term match {
      case _: Product if (subterm.asInstanceOf[AnyRef].eq(term.asInstanceOf[AnyRef])) => true
      case l: List[_] => l.map(isPartOf(subterm, _)).exists(_ == true)
      case p: Product => p.productIterator.toList.map(isPartOf(subterm, _)).exists(_ == true)
      case _ => false
    }
  }

  def predHelper(source: Product, ctx: FeatureExpr, oldres: CCFGRes, env: ASTEnv): CCFGRes = {

    // helper method to handle a switch, source is a case or a default statement
    def handleSwitch(t: AST) = {
      val prior_switch = findPriorASTElem[SwitchStatement](t, env)
      assert(prior_switch.isDefined, "default or case statements should always occur withing a switch definition")
      prior_switch.get match {
        case SwitchStatement(expr, _) => {
          val r1 = getExprPred(expr, ctx, oldres, env)

          // do not determine the pred of t in case no case statement precedes t; TODO nested case
          // switch (e) {
          //   int a;
          //   case 1;
          val r2 = {
            val sprevs = prevASTElems(t, env).reverse.tail
            val pcases = sprevs.flatMap(x => filterCaseStatements(One(x.asInstanceOf[Statement]), env.featureExpr(expr), env))
            if (! pcases.isEmpty) getStmtPred(t, ctx, oldres, env)
            else List()
          }
          r1 ++ r2
        }
      }
    }

    source match {
      case t: CaseStatement => handleSwitch(t)
      case t: DefaultStatement => handleSwitch(t)

      // pred elements of an if statement are
      // all bodys of existing elifs
      // body of existing else
      // body of existing then
      // if no else exists then go for conditions of elifs and condition itself
      case t@IfStatement(condition, thenBranch, elifs, elseBranch) => {
        var res: CCFGRes = List()
        val elifsrc = elifs.reverse.map(childAST)

        if (!elifs.isEmpty) {
          if (elseBranch.isEmpty) {
            for (elif <- elifsrc) {

              if (predComplete(ctx, res)) { }
              else {
                elif match {
                  case ElifStatement(elif_condition, _) => {
                    val newres = getCondExprPred(elif_condition, ctx, oldres, env)

                    for (n <- newres) {
                      if (res.map(_._2).fold(FeatureExprFactory.False)(_ or _).not() and n._2 isContradiction()) { }
                      else res ++= List(n)
                    }
                  }
                }
              }
            }
          }

          for (elif <- elifsrc) {
            val eliffexp = env.featureExpr(elif)
            if (! (eliffexp and ctx isContradiction())) {
              elif match {
                case ElifStatement(_, elif_thenBranch) => res ++= getCondStmtPred(elif_thenBranch, ctx, oldres, env)
                case _ => assert(1 != 0, "expected elif statement")
              }
            }
          }
        }

        if (elseBranch.isDefined) res ++= getCondStmtPred(elseBranch.get, ctx, oldres, env)
        else if (! predComplete(ctx, res)) {
          val newres = getCondExprPred(condition, ctx, oldres, env)

          for (n <- newres) {
            if (res.map(_._2).fold(FeatureExprFactory.False)(_ or _).not() and n._2 isContradiction()) { }
            else res ++= List(n)
          }
        }

        res ++= getCondStmtPred(thenBranch, ctx, oldres, env)

        res
      }

      // all break statements are possible predecessors
      // furthermore if there is no default, we might just have fallen through all case statements (i.e. no break
      // statements available)
      case t@SwitchStatement(expr, s) => {
        val lbreaks = filterBreakStatements(s, ctx, env)
        val ldefaults = filterDefaultStatements(s, ctx, env)

        var res = oldres ++ lbreaks

        if (ldefaults.isEmpty) {
          res ++= getExprPred(expr, ctx, oldres, env)
          res ++= getCondStmtPred(s, ctx, oldres, env)
        } else {
          res ++= getCondStmtPred(s, ctx, oldres, env)
        }
        res
      }

      case t@WhileStatement(expr, s) => getExprPred(expr, ctx, oldres, env) ++ filterBreakStatements(s, ctx, env)
      case t@DoStatement(expr, s) => getExprPred(expr, ctx, oldres, env) ++ filterBreakStatements(s, ctx, env)
      case t@ForStatement(_, Some(expr2), _, s) => getExprPred(expr2, ctx, oldres, env) ++
        filterBreakStatements(s, ctx, env)
      case t@ForStatement(_, _, _, s) => oldres ++ filterBreakStatements(s, ctx, env)

      case c@CompoundStatement(innerStatements) => {
        if (! parentAST(c, env).isInstanceOf[FunctionDef]) barrier ::= c
        val res = getCompoundPred(innerStatements.reverse.map(_.entry), c, ctx, oldres, env)
        barrier = barrier.filterNot(x => x.eq(c))
        res
      }

      case t@LabelStatement(Id(n), _) => {
        findPriorASTElem[FunctionDef](t, env) match {
          case None => assert(1 == 0, "label statements should always occur within a function definition"); List()
          case Some(f) => {
            val l_gotos = filterASTElems[GotoStatement](f, env.featureExpr(t), env)
            // filter gotostatements with the same id as the labelstatement
            // and all gotostatements with dynamic target
            val l_gotos_filtered = l_gotos.filter({
              case GotoStatement(Id(name)) => if (n == name) true else false
              case _ => true
            })
            val l_preds = getStmtPred(t, ctx, oldres, env)
            l_gotos_filtered.map(x => (env.featureExpr(x), env.featureExpr(x), x)) ++ l_preds
          }
        }
      }

      case o: Opt[_] => predHelper(childAST(o), ctx, oldres, env)
      case c: Conditional[_] => predHelper(childAST(c), ctx, oldres, env)

      case f@FunctionDef(_, _, _, CompoundStatement(List())) => (getNewResCtx(oldres, ctx, env.featureExpr(f)), env.featureExpr(f), f) :: oldres
      case f@FunctionDef(_, _, _, stmt) => predHelper(childAST(stmt), ctx, oldres, env) ++
        filterReturnStatements(stmt, ctx, env)

      case s: Statement => getStmtPred(s, ctx, oldres, env)
      case _ => followPred(source, ctx, oldres, env)
    }
  }

  def succ(source: Product, env: ASTEnv): List[AST] = {
    succCCFGCache.lookup(source) match {
      case Some(v) => v
      case None => {
        var curres: CCFGRes = List()
        val ctx = env.featureExpr(source)

        if (ctx isContradiction()) return List()

        curres = succHelper(source, ctx, curres, env)

        val res = curres.map(_._3)
        succCCFGCache.update(source, res)
        res
      }
    }
  }

  // checks whether a given AST element is a succ instruction or not
  private def isCFGInstruction(elem: AST): Boolean = {
    elem match {
      case _: ForStatement => false
      case _: WhileStatement => false
      case _: DoStatement => false
      case _: CompoundStatement => false
      case _: CompoundStatementExpr => false
      case _: IfStatement => false
      case _: ElifStatement => false
      case _: SwitchStatement => false
      case _ => true
    }
  }

  // the predecessor determination is complete, if
  // all result elements combined with or are equivalent to element x with ctx: equivalence ensures that result elements
  // would have been reached before x
  private def predComplete(ctx: FeatureExpr, curres: CCFGRes): Boolean = {
    val curresfexp = curres.map(_._2)
    curresfexp.exists(x => x.not() and ctx isContradiction())
  }

  private def predCompleteBlock(ctx: FeatureExpr, curres: CCFGRes): Boolean = {
    val curresctx = curres.map(_._1).fold(FeatureExprFactory.False)(_ or _)
    curresctx equivalentTo ctx
  }

  // checks whether the current result list is complete
  private def succComplete(ctx: FeatureExpr, curres: CCFGRes): Boolean = {
    val curresctx = curres.map(_._1).fold(FeatureExprFactory.False)(_ or _)
    ctx equivalentTo curresctx
  }

  private def succHelper(source: Product, ctx: FeatureExpr, oldres: CCFGRes, env: ASTEnv): CCFGRes = {
    source match {
      // ENTRY element
      case f@FunctionDef(_, _, _, CompoundStatement(List())) => List((env.featureExpr(f), env.featureExpr(f), f))
      case f@FunctionDef(_, _, _, stmt) => oldres ++ succHelper(stmt, ctx, oldres, env)

      // EXIT element
      case t@ReturnStatement(_) => {
        findPriorASTElem[FunctionDef](t, env) match {
          case None => assert(1 == 0, "return statement should always occur within a function statement"); List()
          case Some(f) => (getNewResCtx(oldres, ctx, env.featureExpr(f)), env.featureExpr(f), f) :: oldres
        }
      }

      case c@CompoundStatement(l) => getCompoundSucc(l.map(_.entry), c, ctx, oldres, env)

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
      case DoStatement(expr, s) => {
        val rs = getCondStmtSucc(s, ctx, oldres, env)
        val re = if (! succComplete(ctx, rs)) getExprSucc(expr, ctx, rs, env)
                 else List()
        rs ++ re
      }

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
          case None => assert(1 == 0, "goto statement should always occur within a function definition"); oldres
          case Some(f) => {
            val l_list = filterAllASTElems[LabelStatement](f, env.featureExpr(t), env).filter(_.id.name == l)
            if (l_list.isEmpty) getStmtSucc(t, ctx, oldres, env)
            else oldres ++ l_list.map(x => (env.featureExpr(x), env.featureExpr(x), x))
          }
        }
      }
      // in case we have an indirect goto dispatch all goto statements
      // within the function (this is our invariant) are possible targets of this goto
      // so fetch the function statement and filter for all label statements
      case t@GotoStatement(PointerDerefExpr(_)) => {
        findPriorASTElem[FunctionDef](t, env) match {
          case None => assert(1 == 0, "goto statement should always occur within a function definition"); oldres
          case Some(f) => {
            val l_list = filterAllASTElems[LabelStatement](f, env.featureExpr(t))
            if (l_list.isEmpty) getStmtSucc(t, ctx, oldres, env)
            else oldres ++ l_list.map(x => (env.featureExpr(x), env.featureExpr(x), x))
          }
        }
      }

      case t: DefaultStatement => getStmtSucc(t, ctx, oldres, env)

      case t: Statement => getStmtSucc(t, ctx, oldres, env)
      case t => followSucc(t, ctx, oldres, env)
    }
  }

  private def getCondStmtSucc(c: Conditional[Statement], ctx: FeatureExpr, oldres: CCFGRes, env: ASTEnv): CCFGRes = {
    c match {
      case Choice(_, thenBranch, elseBranch) =>
        getCondStmtSucc(thenBranch, ctx, oldres, env) ++ getCondStmtSucc(elseBranch, ctx, oldres, env)
      case One(c@CompoundStatement(l)) => {
        barrier ::= c
        val res = getCompoundSucc(l.map(_.entry), c, ctx, oldres, env)
        barrier = barrier.filterNot(x => x.eq(c))
        res
      }
      case One(s: Statement) => {
        barrier ::= s
        val res = getCompoundSucc(List(s), s, ctx, oldres, env)
        barrier = barrier.filterNot(x => x.eq(s))
        res
      }
    }
  }

  private def getCondStmtPred(cond: Conditional[_], ctx: FeatureExpr, oldres: CCFGRes, env: ASTEnv): CCFGRes = {
    cond match {
      case Choice(_, thenBranch, elseBranch) =>
        getCondStmtPred(thenBranch, ctx, oldres, env) ++ getCondStmtPred(elseBranch, ctx, oldres, env)
      case One(c@CompoundStatement(l)) => {
        barrier ::= c
        val res = getCompoundPred(l.reverse.map(_.entry), c, ctx, oldres, env)
        barrier = barrier.filterNot(x => x.eq(c))
        res
      }
      case One(s: Statement) => {
        barrier ::= s
        val res = getCompoundPred(List(s), s, ctx, oldres, env)
        barrier = barrier.filterNot(x => x.eq(s))
        res
      }
    }
  }

  private def getExprSucc(exp: Expr, ctx: FeatureExpr, oldres: CCFGRes, env: ASTEnv): CCFGRes = {
    exp match {
      case c@CompoundStatementExpr(CompoundStatement(innerStatements)) => getCompoundSucc(innerStatements.map(_.entry), c, ctx, oldres, env)
      case _ => {
        val fexpexp = env.featureExpr(exp)
        if (! (ctx and fexpexp isContradiction())) oldres ++ List((getNewResCtx(oldres, ctx, fexpexp), fexpexp, exp))
        else oldres
      }
    }
  }

  private def getCondExprSucc(cexp: Conditional[Expr], ctx: FeatureExpr, oldres: CCFGRes, env: ASTEnv): CCFGRes = {
    cexp match {
      case One(value) => getExprSucc(value, ctx, oldres, env)
      case Choice(_, thenBranch, elseBranch) =>
        getCondExprSucc(thenBranch, ctx, oldres, env) ++
          getCondExprSucc(elseBranch, ctx, oldres, env)
    }
  }

  private def getExprPred(exp: Expr, ctx: FeatureExpr, oldres: CCFGRes, env: ASTEnv): CCFGRes = {
    exp match {
      case c@CompoundStatementExpr(CompoundStatement(innerStatements)) =>
        getCompoundPred(innerStatements.reverse.map(_.entry), c, ctx, oldres, env)
      case _ => {
        val fexpexp = env.featureExpr(exp)
        if (! (fexpexp and ctx isContradiction())) {
          if (oldres.map(_._1).exists(x => x equivalentTo fexpexp)) oldres
          else oldres ++ List((getNewResCtx(oldres, ctx, fexpexp), fexpexp, exp))
        }
        else oldres
      }
    }
  }

  private def getCondExprPred(cexp: Conditional[Expr], ctx: FeatureExpr, oldres: CCFGRes, env: ASTEnv): CCFGRes = {
    cexp match {
      case One(value) => getExprPred(value, ctx, oldres, env)
      case Choice(_, thenBranch, elseBranch) =>
        getCondExprPred(thenBranch, ctx, oldres, env) ++
          getCondExprPred(elseBranch, ctx, oldres, env)
    }
  }

  // handling of successor determination of nested structures, such as for, while, ... and next element in a list
  // of statements
  private def followSucc(nested_ast_elem: Product, ctx: FeatureExpr, oldres: CCFGRes, env: ASTEnv): CCFGRes = {

    if (barrier.exists(x => x.eq(nested_ast_elem))) return oldres

    nested_ast_elem match {
      case t: ReturnStatement => {
        findPriorASTElem[FunctionDef](t, env) match {
          case None => assert(1 == 0, "return statement should always occur within a function statement"); List()
          case Some(f) => (getNewResCtx(oldres, ctx, env.featureExpr(f)), env.featureExpr(f), f) :: oldres
        }
      }
      case _ => {
        val surrounding_parent = parentAST(nested_ast_elem, env)
        surrounding_parent match {
          // loops
          case t@ForStatement(Some(expr1), expr2, _, s) if (isPartOf(nested_ast_elem, expr1)) =>
            if (expr2.isDefined) getExprSucc(expr2.get, ctx, oldres, env)
            else getCondStmtSucc(s, ctx, oldres, env)
          case t@ForStatement(_, Some(expr2), expr3, s) if (isPartOf(nested_ast_elem, expr2)) => {
            val rt = getStmtSucc(t, ctx, oldres, env)
            val rs = getCondStmtSucc(s, ctx, oldres, env)

            if (! succComplete(ctx, rs)) {
              val re = if (expr3.isDefined) getExprSucc(expr3.get, ctx, oldres, env)
                       else getExprSucc(expr2, ctx, oldres, env)
              rt ++ rs ++ re
            } else {
              rt ++ rs
            }
          }
          case t@ForStatement(_, expr2, Some(expr3), s) if (isPartOf(nested_ast_elem, expr3)) =>
            if (expr2.isDefined) getExprSucc(expr2.get, ctx, oldres, env)
            else getCondStmtSucc(s, ctx, oldres, env)
          case t@ForStatement(_, expr2, expr3, s) if (isPartOf(nested_ast_elem, s)) => {
            if (expr3.isDefined) getExprSucc(expr3.get, ctx, oldres, env)
            else if (expr2.isDefined) getExprSucc(expr2.get, ctx, oldres, env)
            else getCondStmtSucc(s, ctx, oldres, env)
          }
          case t@WhileStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) => {
            val rt = getStmtSucc(t, ctx, oldres, env)
            val rs = getCondStmtSucc(s, ctx, oldres, env)
            val re = if (! succComplete(ctx, rs)) getExprSucc(expr, ctx, rs, env)
                     else List()
            rs ++ re ++ rt
          }            
          case WhileStatement(expr, _) => getExprSucc(expr, ctx, oldres, env)
          case t@DoStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) => {
            val rs = getCondStmtSucc(s, ctx, oldres, env)
            val rt = getStmtSucc(t, ctx, oldres, env)
            val re = if (! succComplete(ctx, rs)) getExprSucc(expr, ctx, rs, env)
                     else List()
            rs ++ re ++ rt
          }

          case DoStatement(expr, s) => getExprSucc(expr, ctx, oldres, env)

          // conditional statements
          // we are in the condition of the if statement
          case t@IfStatement(condition, thenBranch, elifs, elseBranch) if (isPartOf(nested_ast_elem, condition)) => {
            var res = oldres

            if (!elifs.isEmpty) {
              for (e <- elifs.map(childAST)) {
                if (succComplete(ctx, res)) { }
                else {
                  e match {
                    case ce@ElifStatement(elif_condition, _) => {
                      val newres = getCondExprSucc(elif_condition, ctx, oldres, env)

                      for (n <- newres) {
                        if (res.map(_._2).fold(FeatureExprFactory.False)(_ or _).not() and n._2 isContradiction()) { }
                        else res ++= List(n)
                      }
                    }
                    case _ => assert(1 != 0, "expected elif statement")
                  }
                }
              }
            }

            if (! succComplete(ctx, res)) {
              if (elseBranch.isDefined) res ++= getCondStmtSucc(elseBranch.get, ctx, oldres, env)
              else res ++= getStmtSucc(t, ctx, oldres, env)
            }
            res ++= getCondStmtSucc(thenBranch, ctx, oldres, env)
            res
          }

          // either go to next ElifStatement, ElseBranch, or next statement of the surrounding IfStatement
          case t@ElifStatement(condition, thenBranch) if (isPartOf(nested_ast_elem, condition)) => {
            var res = oldres
            val snexts = nextASTElems(t, env).tail

            for (e <- snexts) {
              if (succComplete(ctx, res)) { }
              else {
                e match {
                  case ce@ElifStatement(elif_condition, _) => {
                    val newres = getCondExprSucc(elif_condition, ctx, oldres, env)

                    for (n <- newres) {
                      if (res.map(_._2).fold(FeatureExprFactory.False)(_ or _).not() and n._2 isContradiction()) { }
                      else res ++= List(n)
                    }
                  }
                  case _ => assert(1 != 0, "expected elif statement")
                }
              }
            }

            if (! succComplete(ctx, res)) {
              parentAST(t, env) match {
                case tp@IfStatement(_, _, _, None) => res ++= getStmtSucc(tp, ctx, res, env)
                case IfStatement(_, _, _, Some(elseBranch)) => res ++= getCondStmtSucc(elseBranch, ctx, res, env)
              }
            }

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
              res ++= filterCaseStatements(s, env.featureExpr(t), env)
              val dcase = filterDefaultStatements(s, env.featureExpr(t), env)

              if (dcase.isEmpty) res ++= getStmtSucc(t, ctx, oldres, env)
              else res ++= dcase
            }
            res
          }

          case t: Expr => followSucc(t, ctx, oldres, env)
          case t: Statement => getStmtSucc(t, ctx, oldres, env)

          case t: FunctionDef => oldres ++ List((env.featureExpr(t), env.featureExpr(t), t))
          case _ => List()
        }
      }
    }
  }

  // method to catch surrounding ast element, which precedes the given nested_ast_element
  private def followPred(nested_ast_elem: Product, ctx: FeatureExpr, oldres: CCFGRes, env: ASTEnv): CCFGRes = {

    def handleSwitch(t: AST): CCFGRes = {
      val prior_switch = findPriorASTElem[SwitchStatement](t, env)
      assert(prior_switch.isDefined, "default statement without surrounding switch")
      prior_switch.get match {
        case SwitchStatement(expr, _) => {
          val lconds = getExprPred(expr, ctx, oldres, env)
          if (env.previous(t) != null) lconds ++ getStmtPred(t, ctx, oldres, env)
          else {
            val tparent = parentAST(t, env)
            if (tparent.isInstanceOf[CaseStatement]) (getNewResCtx(oldres, ctx, env.featureExpr(tparent)), env.featureExpr(tparent), tparent) :: lconds
            else lconds ++ getStmtPred(tparent, ctx, oldres, env)
          }
        }
      }
    }

    if (barrier.exists(x => x.eq(nested_ast_elem))) {
      parentAST(nested_ast_elem, env) match {
        case _: DoStatement =>
        case _ => return oldres
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

          // for statements consists of of (init, break, inc, s)
          // we are in one of these elements
          // init
          case t@ForStatement(Some(expr1), _, _, _) if (isPartOf(nested_ast_elem, expr1)) =>
            getStmtPred(t, ctx, oldres, env)
          // inc
          case t@ForStatement(_, Some(expr2), Some(expr3), s) if (isPartOf(nested_ast_elem, expr3)) => {
            val rs = getCondStmtPred(s, ctx, oldres, env)
            val rf = filterContinueStatements(s, env.featureExpr(t), env)
            
            if (! predCompleteBlock(ctx, rs)) {
              rs ++ getExprPred(expr2, ctx, rs, env) ++ rf
            } else {
              rs ++ rf
            }
          }
          // break
          case t@ForStatement(None, Some(expr2), None, One(CompoundStatement(List()))) =>
            (getNewResCtx(oldres, ctx, env.featureExpr(expr2)), env.featureExpr(expr2), expr2) :: getStmtPred(t, ctx, oldres, env)
          case t@ForStatement(expr1, Some(expr2), expr3, s) if (isPartOf(nested_ast_elem, expr2)) => {
            var res = oldres
            if (expr1.isDefined) res ++= getExprPred(expr1.get, ctx, oldres, env)
            else res ++= getStmtPred(t, ctx, oldres, env)
            if (expr3.isDefined) res ++= getExprPred(expr3.get, ctx, oldres, env)
            else {
              res ++= getCondStmtPred(s, ctx, oldres, env)
              res ++= filterContinueStatements(s, env.featureExpr(t), env)
            }
            res
          }
          // s
          case t@ForStatement(expr1, expr2, expr3, s) if (isPartOf(nested_ast_elem, s)) =>
            if (expr2.isDefined) getExprPred(expr2.get, ctx, oldres, env)
            else if (expr3.isDefined) getExprPred(expr3.get, ctx, oldres, env)
            else {
              var res = oldres
              if (expr1.isDefined) res = res ++ getExprPred(expr1.get, ctx, oldres, env)
              else res = getStmtPred(t, ctx, oldres, env) ++ res
              res = res ++ getCondStmtPred(s, ctx, oldres, env)
              res
            }

          // while statement consists of (expr, s)
          // special case; we handle empty compound statements here directly because otherwise we do not terminate
          case t@WhileStatement(expr, One(CompoundStatement(List()))) if (isPartOf(nested_ast_elem, expr)) =>
            (getNewResCtx(oldres, ctx, env.featureExpr(expr)), env.featureExpr(expr), expr) :: getStmtPred(t, ctx, oldres, env)
          case t@WhileStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) =>
            (getStmtPred(t, ctx, oldres, env) ++ getCondStmtPred(s, ctx, oldres, env) ++
              filterContinueStatements(s, env.featureExpr(t), env))
          case t@WhileStatement(expr, _) => {
            if (nested_ast_elem.eq(expr)) getStmtPred(t, ctx, oldres, env)
            else getExprPred(expr, ctx, oldres, env)
          }

          // do statement consists of (expr, s)
          // special case: we handle empty compound statements here directly because otherwise we do not terminate
          case t@DoStatement(expr, One(CompoundStatement(List()))) if (isPartOf(nested_ast_elem, expr)) =>
            (getNewResCtx(oldres, ctx, env.featureExpr(expr)), env.featureExpr(expr), expr) :: getStmtPred(t, ctx, oldres, env)
          case t@DoStatement(expr, s) if (isPartOf(nested_ast_elem, expr)) =>
            getCondStmtPred(s, ctx, oldres, env) ++ filterContinueStatements(s, env.featureExpr(t), env)
          case t@DoStatement(expr, s) => {
            if (isPartOf(nested_ast_elem, expr)) getCondStmtPred(s, ctx, oldres, env)
            else getExprPred(expr, ctx, oldres, env) ++ getStmtPred(t, ctx, oldres, env)
          }

          // conditional statements
          // if statement: control flow comes either out of:
          // elseBranch: elifs + condition is the result
          // elifs: rest of elifs + condition
          // thenBranch: condition
          case t@IfStatement(condition, thenBranch, elifs, elseBranch) => {
            if (isPartOf(nested_ast_elem, condition)) getStmtPred(t, ctx, oldres, env)
            else if (isPartOf(nested_ast_elem, thenBranch)) getCondExprPred(condition, ctx, oldres, env)
            else if (isPartOf(nested_ast_elem, elseBranch)) {
              var res = oldres

              for (e <- elifs.reverse.map(childAST)) {
                if (predComplete(ctx, res)) { }
                else {
                  e match {
                    case ElifStatement(elif_condition, _) => {
                      val newres = getCondExprPred(elif_condition, ctx, oldres, env)

                      for (n <- newres) {
                        if (res.map(_._2).fold(FeatureExprFactory.False)(_ or _).not() and n._2 isContradiction()) { }
                        else res ++= List(n)
                      }
                    }
                  }
                }
              }

              if (! predComplete(ctx, res)) {
                val newres = getCondExprPred(condition, ctx, oldres, env)

                for (n <- newres) {
                  if (res.map(_._2).fold(FeatureExprFactory.False)(_ or _).not() and n._2 isContradiction()) { }
                  else res ++= List(n)
                }
              }
              res
            } else {
              getStmtPred(nested_ast_elem.asInstanceOf[AST], ctx, oldres, env)
            }
          }

          // pred of thenBranch is the condition itself
          // and if we are in condition, we strike for a previous elifstatement or the if
          case t@ElifStatement(condition, thenBranch) => {
            if (isPartOf(nested_ast_elem, condition)) {
              var res = oldres
              val elifs = prevASTElems(t, env).reverse.tail

              for (e <- elifs) {
                if (predCompleteBlock(ctx, res)) { }
                else {
                  e match {
                    case ce@ElifStatement(elif_condition, _) => {
                      val newres = getCondExprPred(elif_condition, ctx, oldres, env)

                      for (n <- newres) {
                        if (res.map(_._2).fold(FeatureExprFactory.False)(_ or _).not() and n._2 isContradiction()) { }
                        else res ++= List(n)
                      }
                    }
                  }
                }
              }

              if (! predCompleteBlock(ctx, res)) {
                parentAST(t, env) match {
                  case tp@IfStatement(if_condition, _, _, _) => res ++= getCondExprPred(if_condition, ctx, oldres, env)
                }
              }
              res
            }
            else getCondExprPred(condition, ctx, oldres, env)
          }

          case t@SwitchStatement(expr, s) => {
            if (isPartOf(nested_ast_elem, s)) getExprPred(expr, ctx, oldres, env)
            else getStmtPred(t, ctx, oldres, env)
          }

          case t: CaseStatement => oldres ++ List((getNewResCtx(oldres, ctx, env.featureExpr(t)), env.featureExpr(t), t))

          // pred of default is either the expression of the switch, which is
          // returned by handleSwitch, or a previous statement (e.g.,
          // switch (exp) {
          // ...
          // label1:
          // default: ...)
          // as part of a fall through (sequence of statements without a break and that we catch
          // with getStmtPred
          case t: DefaultStatement => handleSwitch(t) ++ getStmtPred(t, ctx, oldres, env)

          case t: CompoundStatementExpr => followPred(t, ctx, oldres, env)

          case t: Statement => getStmtPred(t, ctx, oldres, env)
          case t: FunctionDef => {
            val ffexp = env.featureExpr(t)
            if (predComplete(ctx, oldres)) oldres
            else oldres ++ List((getNewResCtx(oldres, ctx, ffexp), ffexp, t))
          }
          case _ => oldres
        }
      }
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

  private def getStmtPred(s: AST, ctx: FeatureExpr, oldres: CCFGRes, env: ASTEnv): CCFGRes = {
    val sprevs = prevASTElems(s, env).reverse.tail
    getCompoundPred(sprevs, s, ctx, oldres, env)
  }

  private def getStmtSucc(s: AST, ctx: FeatureExpr, oldres: CCFGRes, env: ASTEnv): CCFGRes = {
    val snexts = nextASTElems(s, env).tail
    getCompoundSucc(snexts, s, ctx, oldres, env)
  }


  // this method filters BreakStatements
  // a break belongs to next outer loop (for, while, do-while)
  // or a switch statement (see [2])
  // use this method with the loop or switch body!
  // so we recursively go over the structure of the ast elems
  // in case we find a break, we add it to the result list
  // in case we hit another loop or switch we return the empty list
  private def filterBreakStatements(c: Conditional[Statement], ctx: FeatureExpr, env: ASTEnv): CCFGRes = {
    def filterBreakStatementsHelper(a: Any): CCFGRes = {
      a match {
        case t: BreakStatement => {
          val tfexp = env.featureExpr(t)
          if (! (tfexp and ctx isContradiction())) List((tfexp, tfexp, t)) else List()
        }
        case _: SwitchStatement => List()
        case _: ForStatement => List()
        case _: WhileStatement => List()
        case _: DoStatement => List()
        case l: List[_] => l.flatMap(filterBreakStatementsHelper)
        case x: Product => x.productIterator.toList.flatMap(filterBreakStatementsHelper)
        case _ => List()
      }
    }
    filterBreakStatementsHelper(c)
  }

  // this method filters ContinueStatements
  // according to [2]: A continue statement shall appear only in or as a
  // loop body
  // use this method only with the loop body!
  private def filterContinueStatements(c: Conditional[Statement], ctx: FeatureExpr, env: ASTEnv): CCFGRes = {
    def filterContinueStatementsHelper(a: Any): CCFGRes = {
      a match {
        case t: ContinueStatement => {
          val tfexp = env.featureExpr(t)
          if (! (tfexp and ctx isContradiction())) List((tfexp, tfexp, t)) else List()
        }
        case _: ForStatement => List()
        case _: WhileStatement => List()
        case _: DoStatement => List()
        case l: List[_] => l.flatMap(filterContinueStatementsHelper)
        case x: Product => x.productIterator.toList.flatMap(filterContinueStatementsHelper)
        case _ => List()
      }
    }
    filterContinueStatementsHelper(c)
  }

  // this method filters all CaseStatements
  private def filterCaseStatements(c: Conditional[Statement], ctx: FeatureExpr, env: ASTEnv): CCFGRes = {
    def filterCaseStatementsHelper(a: Any): CCFGRes = {
      a match {
        case t@CaseStatement(_) => {
          val tfexp = env.featureExpr(t)
          if (! (tfexp and ctx isContradiction())) List((tfexp, tfexp, t)) else List()
        }
        case _: SwitchStatement => List()
        case l: List[_] => l.flatMap(filterCaseStatementsHelper)
        case x: Product => x.productIterator.toList.flatMap(filterCaseStatementsHelper)
        case _ => List()
      }
    }
    filterCaseStatementsHelper(c)
  }

  // this method filters all ReturnStatements
  private def filterReturnStatements(c: CompoundStatement, ctx: FeatureExpr, env: ASTEnv): CCFGRes = {
    def filterReturnStatementsHelper(a: Any): CCFGRes = {
      a match {
        case t@ReturnStatement(_) => {
          val tfexp = env.featureExpr(t)
          if (! (tfexp and ctx isContradiction())) List((tfexp, tfexp, t)) else List()
        }
        case _: NestedFunctionDef => List()
        case l: List[_] => l.flatMap(filterReturnStatementsHelper)
        case x: Product => x.productIterator.toList.flatMap(filterReturnStatementsHelper)
        case _ => List()
      }
    }
    filterReturnStatementsHelper(c)
  }

  // although the standard says that a case statement only has one default statement
  // we may have differently annotated default statements
  private def filterDefaultStatements(c: Conditional[Statement], ctx: FeatureExpr, env: ASTEnv): CCFGRes = {
    def filterDefaultStatementsHelper(a: Any): CCFGRes = {
      a match {
        case _: SwitchStatement => List()
        case t: DefaultStatement => {
          val tfexp = env.featureExpr(t)
          if (! (tfexp and ctx isContradiction())) List((tfexp, tfexp, t)) else List()
        }
        case l: List[_] => l.flatMap(filterDefaultStatementsHelper)
        case x: Product => x.productIterator.toList.flatMap(filterDefaultStatementsHelper)
        case _ => List()
      }
    }
    filterDefaultStatementsHelper(c)
  }

  // given a list of AST elements, determine successor AST elements based on feature expressions
  private def getCompoundSucc(l: List[AST], parent: AST, ctx: FeatureExpr, oldres: CCFGRes, env: ASTEnv): CCFGRes = {
    if (l.isEmpty) {
      if (succComplete(ctx, oldres)) oldres
      else followSucc(parent, ctx, oldres, env)
    } else {
      var curres = oldres
      l.map({
        x => {
          val ctxx = env.featureExpr(x)
          val newres = if (isCFGInstruction(x)) {
            List((getNewResCtx(curres, ctx, ctxx), ctxx, x))
          } else {
            if (barrier.exists(e => e.eq(x))) {
              succHelper(x, ctx, curres, env)
            } else {
              barrier ::= x
              val res = succHelper(x, ctx, curres, env)
              barrier = barrier.filterNot(e => e.eq(x))
              res
            }
          }

          if (newres.isEmpty) { }
          else if (ctxx and ctx isContradiction()) { }
          else if (newres.map(_._2).forall(z => curres.map(_._2).exists(y => z equivalentTo y))) { }
          else if (newres.map(_._2).forall(x => curres.map(_._2).fold(FeatureExprFactory.False)(_ or _) equivalentTo x)) { }
          else {

            for (n <- newres) {
              if (curres.map(_._2).fold(FeatureExprFactory.False)(_ or _).not() and n._2 isContradiction()) { }
              else if (curres.map(_._2).exists(x => (x and ctx) equivalentTo (n._2 and ctx))) { }
              else curres ++= List(n)
            }

            if (succComplete(ctx, curres)) return curres
          }
        }
      })

      followSucc(parent, ctx, curres, env)
    }
  }

  // determine pred elements from a list of variable elements; originally type is List[Opt[AST]]
  // we check the input list and determine, whether we hit all variants; if not go one level up (followPred) and
  // continue the search with the already determine result list curres.
  private def getCompoundPred(l: List[AST], parent: AST, ctx: FeatureExpr, oldres: CCFGRes, env: ASTEnv): CCFGRes = {
    if (l.isEmpty) {
      if (predComplete(ctx, oldres)) oldres
      else followPred(parent, ctx, oldres, env)
    } else {
      var curres = oldres
      l.map({
        x => {
          val ctxx = env.featureExpr(x)

          if (ctxx and ctx isContradiction()) { }
          else if (curres.map(_._2).exists(z => z equivalentTo ctxx)) { }
          else if (ctxx implies curres.map(_._2).fold(FeatureExprFactory.False)(_ or _) isTautology()) { }
          else {
            if (isCFGInstruction(x)) curres ++= {
              if (curres.map(_._2).exists(z => (ctx and ctxx) equivalentTo (ctxx and z))) List()
              else List((getNewResCtx(curres, ctx, ctxx), ctxx, x))
            }
            else curres = predHelper(x, ctx, curres, env)

            if (predComplete(ctx, curres) || predCompleteBlock(ctx, curres)) return curres
          }
        }
      })
      followPred(parent, ctx, curres, env)
    }
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
}

