package de.fosd.typechef.crewrite


import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.c._
import org.kiama.attribution.AttributionBase
import java.util.IdentityHashMap

abstract sealed class CFCompleteness
case class CFComplete(s: List[AST]) extends CFCompleteness
case class CFIncomplete(s: List[AST]) extends CFCompleteness

// implements conditional control flow (cfg) on top of the typechef
// infrastructure
// at first sight the implementation of succ with a lot of private
// function seems overly complicated; however the structure allows
// also to implement pred (although I'm not sure, we need to have
// this), so we could exchange this implementation for another one.
trait ConditionalControlFlow extends CASTEnv with ASTNavigation {

  private implicit def optList2ASTList(l: List[Opt[AST]]) = l.map(_.entry)
  private implicit def opt2AST(s: Opt[AST]) = s.entry

  def succ(a: Any, env: ASTEnv): List[AST] = {
    a match {
      case f@FunctionDef(_, _, _, stmt) => succ(stmt, env)
      case o: Opt[_] => succ(o.entry.asInstanceOf[AST], env)
      case t@ForStatement(init, break, inc, b) => {
        if (init.isDefined) List(init.get)
        else if (break.isDefined) List(break.get)
        else simpleOrCompoundStatement(t, b, env)
      }
      case WhileStatement(e, _) => List(e)
      case t@DoStatement(_, b) => simpleOrCompoundStatement(t, b, env)
      case t@IfStatement(c, _, _, _) => List(c)
      case t@ElifStatement(c, _) => List(c)
      case SwitchStatement(c, _) => List(c)
      case w@ReturnStatement(_) => getSuccSameLevel(w, env)
      case w@CompoundStatement(l) => getSuccSameLevel(w, env) ++ getSuccNestedLevel(l, env)
      case w@BreakStatement() => {
        val f = followUp(w, env)
        if (f.isDefined) getSuccSameLevel(f.get.head, env) else getSuccSameLevel(w, env)
      }
      case w@ContinueStatement() => {
        val f = followUp(w, env)
        if (f.isDefined) f.get.head match {
          case t@ForStatement(_, break, inc, b) => {
            if (inc.isDefined) List(inc.get)
            else if (break.isDefined) List(break.get)
            else simpleOrCompoundStatement(t, b, env)
          }
          case WhileStatement(c, _) => List(c)
          case DoStatement(c, _) => List(c)
          case _ => List() // TODO
        } else getSuccSameLevel(w, env)
      }
      case w@GotoStatement(Id(l)) => {
        val f = findPriorFuncDefinition(w, env)
        if (f == null) getSuccSameLevel(w, env)
        else labelLookup(f, l, env).asInstanceOf[List[AST]]
      }
      case s: Statement => getSuccSameLevel(s, env)
      case t => following(t, env)
    }
  }

  private def findPriorFuncDefinition(a: Any, env: ASTEnv): FunctionDef = {
    a match {
      case f: FunctionDef => f
      case o: Any => {
        val oparent = env.get(o)._2
        if (oparent != null) findPriorFuncDefinition(oparent, env)
        else null
      }
      case _ => null
    }
  }

  private def labelLookup(a: Any, l: String, env: ASTEnv): List[Any] = {
    def iterateChildren(a: Any): List[Any] = {
      val achildren = env.get(a)._5
      achildren.map(
        x => x match {
          case e: AST => labelLookup(e, l, env)
          case Opt(_, entry) => labelLookup(entry.asInstanceOf[AST], l, env)
          case ls: List[_] => ls.flatMap(labelLookup(_, l, env))
          case _ => List()
        }).foldLeft(List[Any]())(_ ++ _)
    }

    a match {
      case e @ LabelStatement(Id(n), _) if (n == l) => List(e) ++ iterateChildren(e)
      case e : AST => iterateChildren(e)
      case o : Opt[_] => iterateChildren(o)
    }
  }

  private def simpleOrCompoundStatement(p: Statement, c: Conditional[_], env: ASTEnv) = {
    c.asInstanceOf[One[_]].value match {
      case CompoundStatement(l) => if (l.isEmpty) List(p) else getSuccNestedLevel(l, env)
      case s: Statement => List(s)
    }
  }

  // handling of successor determination of nested structures, such as for, while, ... and next element in a list
  // of statements
  private def following(a: Any, env: ASTEnv): List[AST] = {
    val aparent = env.get(a)._2
    aparent match {
      case t@ForStatement(Some(e), c, _, b) if e.eq(a.asInstanceOf[AnyRef]) => if (c.isDefined) List(c.get) else simpleOrCompoundStatement(t, b, env)
      case t@ForStatement(_, Some(e), _, b) if e.eq(a.asInstanceOf[AnyRef]) => getSuccSameLevel(t, env) ++ simpleOrCompoundStatement (t, b, env)
      case t@ForStatement(_, c, Some(e), b) if e.eq(a.asInstanceOf[AnyRef]) => if (c.isDefined) List(c.get) else simpleOrCompoundStatement(t, b, env)
      case t@ForStatement(_, c, i, e) if e.eq(a.asInstanceOf[AnyRef])=> {
        if (i.isDefined) List(i.get)
        else if (c.isDefined) List(c.get)
        else simpleOrCompoundStatement(t, e, env)
      }
      case t@WhileStatement(e, b) if e.eq(a.asInstanceOf[AnyRef]) => simpleOrCompoundStatement(t, b, env) ++ getSuccSameLevel(t, env)
      case t@DoStatement(e, b) if e.eq(a.asInstanceOf[AnyRef]) => simpleOrCompoundStatement(t, b, env) ++ getSuccSameLevel(t, env)
      case t@IfStatement(e, tb, elif, el) if e.eq(a.asInstanceOf[AnyRef]) => {
        var res = simpleOrCompoundStatement(t, tb, env)
        if (! elif.isEmpty) res = res ++ getSuccNestedLevel(elif, env)  // call getSuccNestedLevel on elif does not seem right
        if (el.isDefined) res = res ++ simpleOrCompoundStatement(t, el.get, env)
        res
      }
      case t@ElifStatement(e, One(CompoundStatement(l))) if e.eq(a.asInstanceOf[AnyRef]) => getSuccNestedLevel(l, env) ++ getSuccSameLevel(t, env)
      case _ => List()
    }
  }

  // method to catch surrounding while, for, ... statement, which is the follow item of a last element in it's list
  private def followUp(n: AnyRef, env: ASTEnv): Option[List[AST]] = {
    val nparent = env.get(n)._2
    nparent match {
      case c: CompoundStatement => followUp(c, env)
      case w @ WhileStatement(e, _) => Some(List(e))
      case w : ForStatement => Some(List(w))
      case w @ DoStatement(e, One(CompoundStatement(l))) => Some(List(e))
      case w @ IfStatement(_, _, _, _) => Some(getSuccSameLevel(w, env))
      case w @ ElifStatement(_, _) => followUp(w, env)
      case o: Opt[_] => followUp(o, env)
      case c: Conditional[_] => followUp(c, env)
      case s: Statement => followUp(s, env)
      case _ => None
    }
  }

  // we have to check possible successor nodes in at max three steps:
  // 1. get direct successors with same annotation; if yes stop; if not goto 2.
  // 2. get all annotated elements at the same level and check whether we find a definite set of successor nodes
  //    if yes stop; if not goto 3.
  // 3. get the parent of our node and determine successor nodes of it
  private def getSuccSameLevel(s: AST, env: ASTEnv) = {
    val sandf = getFeatureGroupedASTElems(s, env)
    val sos = getNextEqualAnnotatedSucc(s, sandf)
    sos match {
      // 1.
      case Some(x) => List(x)
      case None => {
        val sfexp = env.get(s)._1.foldLeft(FeatureExpr.base)(_ and _)
        val succel = getSuccFromList(sfexp, sandf.drop(1), env)
        succel match {
          case CFComplete(r) => r // 2.
          case CFIncomplete(r) => r ++ followUp(s, env).getOrElse(List()) // 3.
        }
      }
    }
  }

  private def getSuccNestedLevel(l: List[AST], env: ASTEnv) = {
    if (l.isEmpty) List()
    else {
      val wsandf = determineTypeOfGroupedOptLists(groupOptListsImplication(groupOptBlocksEquivalence(l, env), env).reverse, env).reverse
      val lparent = env.get(l.head)._2
      val lpfexp = env.get(lparent)._1.foldLeft(FeatureExpr.base)(_ and _)
      val succel = getSuccFromList(lpfexp, wsandf, env)

      succel match {
        case CFComplete(r) => r
        case CFIncomplete(r) => r ++ followUp(l.head, env).getOrElse(List())
      }
    }
  }

  // pack similar elements into sublists
  private def pack[T](f: (T, T) => Boolean)(l: List[T]): List[List[T]] = {
    if (l.isEmpty) List()
    else (l.head::l.tail.takeWhile(f(l.head, _)))::pack(f)(l.tail.dropWhile(f(l.head, _)))
  }

  // group consecutive Opts in a list and return a list of list containing consecutive (feature equivalent) opts
  // e.g.:
  // List(Opt(true, Id1), Opt(fa, Id2), Opt(fa, Id3)) => List(List(Opt(true, Id1)), List(Opt(fa, Id2), Opt(Id3)))
  private def groupOptBlocksEquivalence(l: List[AST], env: ASTEnv) = {
    pack[AST](env.get(_)._1.foldLeft(FeatureExpr.base)(_ and _) equivalentTo env.get(_)._1.foldLeft(FeatureExpr.base)(_ and _))(l)
  }

  // group List[Opt[_]] according to implication
  // later one should imply the not of previous ones; therefore using l.reverse
  private def groupOptListsImplication(l: List[List[AST]], env: ASTEnv) = {
    def checkImplication(a: AST, b: AST) = {
      val as = env.get(a)._1.toSet
      val bs = env.get(b)._1.toSet
      val cs = as.intersect(bs)
      as.--(cs).foldLeft(FeatureExpr.base)(_ and _).implies(bs.--(cs).foldLeft(FeatureExpr.base)(_ and _).not()).isTautology()
    }
    pack[List[AST]]({ (x,y) => checkImplication(x.head, y.head)})(l.reverse).reverse
  }

  // get type of List[List[AST]:
  // 0 -> only true values
  // 1 -> #if-(#elif)* block
  // 2 -> #if-(#elif)*-#else block
  private def determineTypeOfGroupedOptLists(l: List[List[List[AST]]], env: ASTEnv): List[(Int, List[List[AST]])] = {
    l match {
      case (h::t) => {
        var f: List[FeatureExpr] = List()
        for (e :: _ <- h) {
          val efexp = env.get(e)
          f = efexp._1.foldLeft(FeatureExpr.base)(_ and _) :: f
        }
        if (f.foldLeft(FeatureExpr.base)(_ and _).isTautology()) (0, h)::determineTypeOfGroupedOptLists(t, env)
        else if (f.map(_.not()).foldLeft(FeatureExpr.base)(_ and _).isContradiction()) (2, h.reverse)::determineTypeOfGroupedOptLists(t, env)
             else (1, h)::determineTypeOfGroupedOptLists(t, env)
      }
      case Nil => List()
    }
  }

  // returns a list of previous and next AST elems grouped according to feature expressions
  private def getFeatureGroupedASTElems(s: AST, env: ASTEnv) = {
    val l = prevASTElems(s, env) ++ nextASTElems(s, env).drop(1)
    val d = determineTypeOfGroupedOptLists(groupOptListsImplication(groupOptBlocksEquivalence(l, env), env), env)
    getSuccTailList(s, d)
  }

  // get all succ nodes of o
  private def getNextEqualAnnotatedSucc(o: AST, l: List[(Int, List[List[AST]])]): Option[AST] = {
    if (l.isEmpty) return None
    val el = l.head

    // take tuple with o and examine it
    // _.map(_.eq(o)).max compares object identity and not structural identity as list.contains does
    val il = el._2.filter(_.map(_.eq(o)).max)
    val jl = il.head.span(_.ne(o))._2.drop(1)
    if (! jl.isEmpty) Some(jl.head)
    else None
  }

  // get list with o and all following lists
  private def getSuccTailList(o: AST, l: List[(Int, List[List[AST]])]): List[(Int, List[List[AST]])] = {
    // get the list with o and all following lists
    // iterate each sublist of the incoming tuples (Int, List[List[Opt[_]]] combine equality check
    // with foldLeft and drop tuples in which o does not occur
    l.dropWhile(_._2.map(_.map(_.eq(o)).max).max.unary_!)
  }

  // get all succ nodes of an unknown input node; useful for cases in which successor nodes occur
  // in a different block
  private def getSuccFromList(c: FeatureExpr, l: List[(Int, List[List[AST]])], env: ASTEnv): CFCompleteness = {
    var r = List[AST]()
    for (e <- l) {
      e match {
        case (0, opts) => r = r ++ List(opts.head.head)
        case (_, opts) => r = r ++ opts.flatMap({ x=> List(x.head)})
      }

      if (e._1 == 2 || e._1 == 0) return CFComplete(r)
      val efexp = env.get(e._2.head.head)._1.foldLeft(FeatureExpr.base)(_ and _)
      if (efexp.equivalentTo(c) && e._1 == 1) return CFComplete(r)
    }
    CFIncomplete(r)
  }

  // determine recursively all succs
  def getAllSucc(i: AST, env: ASTEnv) = {
    var r = List[(AST, List[AST])]()
    var s = List(i)
    var d = List[AST]()
    var c: AST = null

    while (! s.isEmpty) {
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
}

// defines and uses we can jump to using succ
trait Variables extends ASTNavigation {
  val uses: PartialFunction[Any, Set[Id]] = {case a: Any => findUses(a)}

  private def findUses(e: Any): Set[Id] = {
    e match {
      case ForStatement(expr1, expr2, expr3, _) => {
        var res = Set[Id]()
        if (expr1.isDefined) res = res ++ uses(expr1.get)
        if (expr2.isDefined) res = res ++ uses(expr2.get)
        if (expr3.isDefined) res = res ++ uses(expr3.get)
        res
      }
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
      case AssignExpr(target, _, source) => uses(target) ++ uses(source)
      case o@Opt(_, entry) => uses(o.entry)
      case _ => Set()
    }
  }

  val defines: PartialFunction[Any, Set[Id]] = {
    case DeclarationStatement(d) => defines(d)
    case Declaration(_, init) => init.flatMap(defines).toSet
    case InitDeclaratorI(a, _, _) => defines(a)
    case AtomicNamedDeclarator(_, i, _) => Set(i)
    case o@Opt(_, entry) => defines(entry)
    case _ => Set()
  }
}

trait Liveness extends AttributionBase with Variables with ConditionalControlFlow {

  private def updateMap(m: Map[FeatureExpr, Set[Id]], e: (FeatureExpr, Set[Id]), op: (Set[Id], Set[Id]) => Set[Id]): Map[FeatureExpr, Set[Id]] = {
    val key = m.find(_._1.equivalentTo(e._1))
    key match {
      case None => m.+(e)
      case Some((k, v)) => m.+((k, op(e._2, v)))
    }
  }

  // cache for in; we have to store all tuples of (a, env) their because using
  // (a, env) always creates a new one!!! and circular internally uses another
  // IdentityHashMap and uses (a, env) as a key there.
  private val astIdenEnvHM = new IdentityHashMap[AST, (AST, ASTEnv)]()

  // cf. http://www.cs.colostate.edu/~mstrout/CS553/slides/lecture03.pdf
  // page 5
  //  in(n) = uses(n) + (out(n) - defines(n))
  // out(n) = for s in succ(n) r = r + in(s); r
  val in: PartialFunction[(Any, ASTEnv), Map[FeatureExpr, Set[Id]]] = {
    circular[(Any, ASTEnv), Map[FeatureExpr, Set[Id]]](Map()) {
      case t@(e, env) => {
        val u = uses(e)
        val d = defines(e)
        var res = out(t)
        if (!d.isEmpty) {
          val dhfexp = lfexp2Fexp(d.head, env)
          res = updateMap(res, (dhfexp, d), {_ -- _})
        }
        if (!u.isEmpty) {
          val uhfexp = lfexp2Fexp(u.head, env)
          res = updateMap(res, (uhfexp, u), {_ ++ _})
        }
        res
      }
    }
  }

  val out: PartialFunction[(Any, ASTEnv), Map[FeatureExpr, Set[Id]]] =
    circular[(Any, ASTEnv), Map[FeatureExpr, Set[Id]]] (Map()) {
      case t@(e, env) => {
        val sl = succ(e, env)
        var res = Map[FeatureExpr, Set[Id]]()
        for (a <- sl) {
          if (! astIdenEnvHM.containsKey(a))
            astIdenEnvHM.put(a, (a, env))
          for (el <- in(astIdenEnvHM.get(a)))
            res = updateMap(res, el, {_ ++ _})
        }
        res
      }
    }
}