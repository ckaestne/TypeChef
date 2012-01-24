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
// also to implement pred (although I'm not sure, we need to have
// this), so we could exchange this implementation for another one.

// one usage for pred is for instance the determination of
// reaching definitions (cf. http://en.wikipedia.org/wiki/Reaching_definition)
trait ConditionalControlFlow extends CASTEnv with ASTNavigation {

  private implicit def optList2ASTList(l: List[Opt[AST]]) = l.map(_.entry)
  private implicit def opt2AST(s: Opt[AST]) = s.entry

  type IfdefBlocks = List[List[AST]]

  def pred(a: Any, env: ASTEnv): List[AST] = {
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
          case _: IfStatement => changed = true; add2newres = predHelper(oldelem, env)
          case _: ElifStatement => changed = true; add2newres = predHelper(oldelem, env)
          case _: CompoundStatement => changed = true; add2newres = predHelper(oldelem, env)
          case _ => add2newres = List(oldelem)
        }

        // add only elements that are not in newres so far
        for (addnew <- add2newres)
          if (newres.map(_.eq(addnew)).foldLeft(false)(_ || _).unary_!) newres = newres ++ List(addnew)
      }
    }
    newres
  }

  def predHelper(a: Any, env: ASTEnv): List[AST] = {
    a match {
      case w@LabelStatement(Id(n), _) => gotoLookup(findPriorFuncDefinition(w, env), n, env)
      case _: FunctionDef => List()
      case o: Opt[_] => predHelper(o.entry, env)
      case s: Statement => getPredSameLevel(s, env)
      case _ => nestedPred(a, env)
    }
  }

  def succ(a: Any, env: ASTEnv): List[AST] = {
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
          case _: CompoundStatement => changed = true; add2newres = succHelper(oldelem, env)
          case _ => add2newres = List(oldelem)
        }

        // add only elements that are not in newres so far
        for (addnew <- add2newres)
          if (newres.map(_.eq(addnew)).foldLeft(false)(_ || _).unary_!) newres = newres ++ List(addnew)
      }
    }
    newres
  }

  private def succHelper(a: Any, env: ASTEnv): List[AST] = {
    a match {
      case f@FunctionDef(_, _, _, stmt) => succHelper(stmt, env)
      case o: Opt[_] => succHelper(o.entry, env)
      case t@ForStatement(expr1, expr2, expr3, s) => {
        if (expr1.isDefined) List(expr1.get)
        else if (expr2.isDefined) List(expr2.get)
        else simpleOrCompoundStatementSucc(t, s, env)
      }
      case WhileStatement(expr, _) => List(expr)
      case t@DoStatement(_, s) => simpleOrCompoundStatementSucc(t, s, env)
      case t@IfStatement(condition, _, _, _) => List(condition)
      case t@ElifStatement(c, _) => List(c) ++ getSuccSameLevel(t, env)
      case SwitchStatement(c, _) => List(c)
      case t@ReturnStatement(_) => getSuccSameLevel(t, env)
      case t@CompoundStatement(l) => getSuccSameLevel(t, env) ++ getSuccNestedLevel(l, env)
      case t@BreakStatement() => {
        val f = followUpSucc(t, env)
        if (f.isDefined) getSuccSameLevel(f.get.head, env) else getSuccSameLevel(t, env)
      }
      case t@ContinueStatement() => {
        val f = followUpSucc(t, env)
        if (f.isDefined) f.get.head match {
          case t@ForStatement(_, break, inc, b) => {
            if (inc.isDefined) List(inc.get)
            else if (break.isDefined) List(break.get)
            else simpleOrCompoundStatementSucc(t, b, env)
          }
          case WhileStatement(c, _) => List(c)
          case DoStatement(c, _) => List(c)
          case _ => List() // TODO
        } else getSuccSameLevel(t, env)
      }
      case t@GotoStatement(Id(l)) => {
        val f = findPriorFuncDefinition(t, env)
        if (f == null) getSuccSameLevel(t, env)
        else labelLookup(f, l, env).asInstanceOf[List[AST]]
      }
      case t: Statement => getSuccSameLevel(t, env)
      case t => nestedSucc(t, env)
    }
  }

  private def findPriorFuncDefinition(a: Any, env: ASTEnv): FunctionDef = {
    a match {
      case f: FunctionDef => f
      case o: Any => {
        val oparent = env.parent(o)
        if (oparent != null) findPriorFuncDefinition(oparent, env)
        else null
      }
      case _ => null
    }
  }

  private def iterateChildren(a: Any, l: String, env: ASTEnv, op: (Any, String, ASTEnv) => List[AST]): List[AST] = {
    env.children(a).map(
      x => x match {
        case e: AST => op(e, l, env)
        case Opt(_, entry) => op(entry, l, env)
        case ls: List[_] => ls.flatMap(op(_, l, env))
        case _ => List()
      }
    ).foldLeft(List[AST]())(_ ++ _)
  }

  private def labelLookup(a: Any, l: String, env: ASTEnv): List[AST] = {
    a match {
      case e @ LabelStatement(Id(n), _) if (n == l) => List(e) ++ iterateChildren(e, l, env, labelLookup)
      case e : AST => iterateChildren(e, l, env, labelLookup)
      case o : Opt[_] => iterateChildren(o, l, env, labelLookup)
    }
  }

  private def gotoLookup(a: Any, l: String, env: ASTEnv): List[AST] = {
    a match {
      case e @ GotoStatement(Id(n)) if (n == l) => List(e)
      case e : AST => iterateChildren(e, l, env, gotoLookup)
      case o : Opt[_] => iterateChildren(o, l, env, gotoLookup)
    }
  }

  private def simpleOrCompoundStatementSucc(p: AST, c: Conditional[_], env: ASTEnv) = {
    c.asInstanceOf[One[_]].value match {
      case CompoundStatement(l) => if (l.isEmpty) List(p) else getSuccNestedLevel(l, env)
      case s: Statement => List(s)
    }
  }

  private def simpleOrCompoundStatementPred(p: AST, c: Conditional[_], env: ASTEnv) = {
    c.asInstanceOf[One[_]].value match {
      case CompoundStatement(l) => if (l.isEmpty) List(p) else getPredNestedLevel(l, env)
      case s: Statement => List(s)
    }
  }

  // handling of successor determination of nested structures, such as for, while, ... and next element in a list
  // of statements
  private def nestedSucc(a: Any, env: ASTEnv): List[AST] = {
    val aparent = env.parent(a)
    aparent match {
      case t@ForStatement(Some(e), c, _, b) if e.eq(a.asInstanceOf[AnyRef]) => if (c.isDefined) List(c.get) else simpleOrCompoundStatementSucc(t, b, env)
      case t@ForStatement(_, Some(e), _, b) if e.eq(a.asInstanceOf[AnyRef]) => getSuccSameLevel(t, env) ++ simpleOrCompoundStatementSucc (t, b, env)
      case t@ForStatement(_, c, Some(e), b) if e.eq(a.asInstanceOf[AnyRef]) => if (c.isDefined) List(c.get) else simpleOrCompoundStatementSucc(t, b, env)
      case t@ForStatement(_, c, i, e) if e.eq(a.asInstanceOf[AnyRef])=> {
        if (i.isDefined) List(i.get)
        else if (c.isDefined) List(c.get)
        else simpleOrCompoundStatementSucc(t, e, env)
      }
      case t@WhileStatement(e, b) if e.eq(a.asInstanceOf[AnyRef]) => simpleOrCompoundStatementSucc(t, b, env) ++ getSuccSameLevel(t, env)
      case t@DoStatement(e, b) if e.eq(a.asInstanceOf[AnyRef]) => simpleOrCompoundStatementSucc(t, b, env) ++ getSuccSameLevel(t, env)
      case t@IfStatement(e, tb, elif, el) if e.eq(a.asInstanceOf[AnyRef]) => {
        var res = simpleOrCompoundStatementSucc(t, tb, env)
        if (! elif.isEmpty) res = res ++ getSuccNestedLevel(elif, env)  // TODO call getSuccNestedLevel on elif does not seem right
        if (elif.isEmpty && el.isDefined) res = res ++ simpleOrCompoundStatementSucc(t, el.get, env)
        res
      }

      // either go to next ElifStatement, ElseBranch, or next statement of the surrounding IfStatement
      // filtering is necessary, as else branches are not considered by getSuccSameLevel
      case t@ElifStatement(e, thenBranch) if e.eq(a.asInstanceOf[AnyRef]) => {
        var res = getSuccSameLevel(t, env)
        if (res.filter(_.isInstanceOf[ElifStatement]).isEmpty) {
          env.parent(env.parent(t)) match {
            case tp@IfStatement(_, _, _, None) => res = getSuccSameLevel(tp, env)
            case IfStatement(_, _, _, Some(elseBranch)) => res = List(childAST(elseBranch))
          }
        }
        res ++ simpleOrCompoundStatementSucc(t, thenBranch, env)
      }
      case _ => List()
    }
  }

  // handling off predecessor determination of nested structures, such as for, while, ... and previous element in a list
  // of statements
  private def nestedPred(a: Any, env: ASTEnv): List[AST] = {
    env.parent(a) match {
      case t@ForStatement(Some(e), _, _, _) if e.eq(a.asInstanceOf[AnyRef]) => List(t)
      case t@ForStatement(e, Some(c), i, b) if e.eq(a.asInstanceOf[AnyRef]) => {
        var res = List[AST]()
        if (e.isDefined) res = e.get :: res
        else res = res ++ getPredSameLevel (t, env)
        if (i.isDefined) res = i.get :: res
        else res = res ++ simpleOrCompoundStatementPred(t, b, env)
        res
      }
      case _ => List()
    }
  }

  // method to catch surrounding while, for, ... statement, which is the follow item of a last element in it's list
  private def followUpSucc(nested_ast_elem: AnyRef, env: ASTEnv): Option[List[AST]] = {

    nested_ast_elem match {
      case _: ReturnStatement => None
      case _ => {
        val surrounding_parent = env.parent(nested_ast_elem)
        surrounding_parent match {
          // handling of #ifdef-conditional elements
          case o: Opt[_] => followUpSucc(o, env)
          case c: Conditional[_] => followUpSucc(c, env)

          // skip over CompoundStatement; we do not consider it in ast-succ evaluation anyway
          case c: CompoundStatement => followUpSucc(c, env)

          // in all loop statements go back to the statement itself
          case t: ForStatement => Some(List(t))
          case t: WhileStatement => Some(List(t))
          case t: DoStatement => Some(List(t))

          // after control flow comes out of a branch from an IfStatement,
          // we go for the next element in the row
          case t: IfStatement => Some(getSuccSameLevel(t, env))
          case t: ElifStatement => followUpSucc(t, env)

          case t: Statement => followUpSucc(t, env)
          case _ => None
        }
      }
    }
  }

  // method to catch surrounding ast element, which precedes the given nested_ast_element
  private def followUpPred(nested_ast_elem: AnyRef, env: ASTEnv): Option[List[AST]] = {
    val surrounding_parent = env.parent(nested_ast_elem)
    surrounding_parent match {
      // handling of #ifdef-conditional ast elements
      case o: Opt[_] => followUpPred(o, env)
      case c: Conditional[_] => followUpPred(c, env)

      // skip over CompoundStatement: we do not consider it in ast-pred evaluation anyway
      case c: CompoundStatement => followUpPred(c, env)

      // in all loop statements go to the statement itself
      case t: ForStatement => followUpPred(t, env)
      case t: WhileStatement => followUpPred(t, env)
      case t: DoStatement => followUpPred(t, env)

      // after control flow comes out of a branch from an ElifStatement
      // we go for the previous element of the ElifStatement
      case t@IfStatement(condition, thenBranch, elifs, elseBranch) => {
        if (nested_ast_elem.eq(thenBranch)) Some(List(condition))
        else if (nested_ast_elem.eq(elseBranch)) {
          Some(getPredNestedLevel(elifs.reverse, env))
        }
        else {
          Some(getPredSameLevel(nested_ast_elem.asInstanceOf[AST], env))
        }
      }

      case t: Statement => followUpPred(t, env)
      case _ => None
    }
  }

  // we have to check possible successor nodes in at max three steps:
  // 1. get direct successors with same annotation; if yes stop; if not go to step 2.
  // 2. get all annotated elements at the same level and check whether we find a definite set of successor nodes
  //    if yes stop; if not go to step 3.
  // 3. get the parent of our node and determine successor nodes of it
  private def getSuccSameLevel(s: AST, env: ASTEnv): List[AST] = {
    val next_ifdef_blocks = getNextIfdefBlocks(s, env)
    val next_equal_annotated_ast_element = getNextEqualAnnotatedASTElem(s, next_ifdef_blocks)
    next_equal_annotated_ast_element match {
      // 1.
      case Some(x) => List(x)
      case None => {
        val feature_expr_s_statement = env.featureExpr(s)
        val successor_list = getSuccFromList(feature_expr_s_statement, next_ifdef_blocks.drop(1), env)
        successor_list match {
          case Left(s_list) => s_list // 2.
          case Right(s_list) => s_list ++ followUpSucc(s, env).getOrElse(List()) // 3.
        }
      }
    }
  }

  // we have to check possible predecessor nodes in at max three steps:
  // 1. get direct predecessor with same annotation; if yes stop; if not go to step 2.
  // 2. get all annotated elements at the same level and check whether we find a definite set of predecessor nodes
  //    if yes stop; if not go to step 3.
  // 3. get the parent of our node and determine predecessor nodes of it
  private def getPredSameLevel(s: AST, env: ASTEnv): List[AST] = {
    val previous_ifdef_blocks = getPreviousIfdefBlocks(s, env)
    val previous_equal_annotated_ast_elem = getNextEqualAnnotatedASTElem(s, previous_ifdef_blocks)
    previous_equal_annotated_ast_elem match {
      // 1.
      case Some(x) => List(x)
      case None => {
        val feature_expr_s_statement = env.featureExpr(s)
        val predecessor_list = getPredFromList(feature_expr_s_statement, previous_ifdef_blocks, env)
        predecessor_list match {
          case Left(p_list) => p_list // 2.
          case Right(p_list) => p_list ++ followUpPred(s, env).getOrElse(List()) // 3.
        }
      }
    }
  }

  private def getSuccNestedLevel(l: List[AST], env: ASTEnv): List[AST] = {
    if (l.isEmpty) List()
    else {
      val typed_grouped_ifdef_blocks = determineTypeOfGroupedIfdefBlocks(groupIfdefBlocks(determineIfdefBlocks(l, env), env).reverse, env).reverse
      val list_parent_feature_expr = env.featureExpr(env.parent(l.head))
      val successor_list = getPredFromList(list_parent_feature_expr, typed_grouped_ifdef_blocks, env)

      successor_list match {
        case Left(s_list) => s_list
        case Right(s_list) => s_list ++ followUpSucc(l.head, env).getOrElse(List())
      }
    }
  }

  private def getPredNestedLevel(l: List[AST], env: ASTEnv): List[AST] = {
    if (l.isEmpty) List()
    else {
      val typed_grouped_ifdef_blocks = determineTypeOfGroupedIfdefBlocks(groupIfdefBlocks(determineIfdefBlocks(l, env), env).reverse, env).reverse
      val list_parent_feature_expr = env.featureExpr(env.parent(l.reverse.head))
      val predecessor_list = getPredFromList(list_parent_feature_expr, typed_grouped_ifdef_blocks, env)

      predecessor_list match {
        case Left(p_list) => p_list
        case Right(p_list) => p_list ++ followUpPred(l.reverse.head, env).getOrElse(List())
      }
      List()
    }
  }

  // returns a list next AST elems grouped according to feature expressions
  private def getNextIfdefBlocks(s: AST, env: ASTEnv): List[(Int, IfdefBlocks)] = {
    val l = prevASTElems(s, env) ++ nextASTElems(s, env).drop(1)
    val d = determineTypeOfGroupedIfdefBlocks(groupIfdefBlocks(determineIfdefBlocks(l, env), env), env)
    getTailList(s, d)
  }

  private def getPreviousIfdefBlocks(s: AST, env: ASTEnv) = {
    val l = prevASTElems(s, env) ++ nextASTElems(s, env).drop(1)
    val d = determineTypeOfGroupedIfdefBlocks(groupIfdefBlocks(determineIfdefBlocks(l.reverse, env), env), env)
    getTailList(s, d)
  }

  private def getNextEqualAnnotatedASTElem(o: AST, l: List[(Int, IfdefBlocks)]): Option[AST] = {
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
  private def getTailList(o: AST, l: List[(Int, IfdefBlocks)]): List[(Int, IfdefBlocks)] = {
    // get the list with o and all following lists
    // iterate each sublist of the incoming tuples (Int, List[List[Opt[_]]] combine equality check
    // with foldLeft and drop tuples in which o does not occur
    l.dropWhile(_._2.map(_.map(_.eq(o)).max).max.unary_!)
  }

  // get all succ nodes of an unknown input node; useful for cases in which successor nodes occur
  // in a different block
  private def getSuccFromList(f: FeatureExpr, l: List[(Int, IfdefBlocks)], env: ASTEnv): Either[List[AST], List[AST]] = {
    var res = List[AST]()
    for (e <- l) {
      e match {
        case (0, opts) => return Left(res ++ List(opts.head.head))
        case (1, opts) => {
          res = res ++ opts.flatMap({ x=> List(x.head)})
          val e_feature_expr = env.featureExpr(opts.head.head)
          if (e_feature_expr.equivalentTo(f) && e._1 == 1) return Left(res)
        }
        case (2, opts) => return Left(res ++ opts.flatMap({ x=> List(x.head)}))
      }
    }
    Right(res)
  }

  // get all predecessor nodes of an unknown input node; useful for cases in which predecessor nodes occur
  // in a different block
  private def getPredFromList(f: FeatureExpr, l: List[(Int, IfdefBlocks)], env: ASTEnv): Either[List[AST], List[AST]] = {
    var res = List[AST]()
    for (e <- l) {
      e match {
        case (0, opts) => return Left(res ++ List(opts.reverse.head.reverse.head))
        case (1, opts) => {
          res = res ++ opts.flatMap({ x => List(x.reverse.head)})
          val e_feature_expr = env.featureExpr(opts.reverse.head.reverse.head)
          if (e_feature_expr.equivalentTo(f) && e._1 == 1) return Left(res)
        }
        case (2, opts) => return Left(res ++ opts.reverse.flatMap({ x=> List(x.reverse.head)}))
      }
    }
    Right(res)
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

  // the following functions belong to detection and type cateorization of ifdef blocks
  // pack similar elements into sublists
  private def pack[T](f: (T, T) => Boolean)(l: List[T]): List[List[T]] = {
    if (l.isEmpty) List()
    else (l.head::l.tail.takeWhile(f(l.head, _)))::pack(f)(l.tail.dropWhile(f(l.head, _)))
  }

  // group consecutive Opts in a list and return a list of list containing consecutive (feature equivalent) opts
  // e.g.:
  // List(Opt(true, Id1), Opt(fa, Id2), Opt(fa, Id3)) => List(List(Opt(true, Id1)), List(Opt(fa, Id2), Opt(fa, Id3)))
  private[crewrite] def determineIfdefBlocks(l: List[AST], env: ASTEnv): IfdefBlocks = {
    pack[AST](env.featureExpr(_) equivalentTo env.featureExpr(_))(l)
  }

  // group List[Opt[_]] according to implication
  // later one should imply the not of previous ones; therefore using l.reverse
  private def groupIfdefBlocks(l: IfdefBlocks, env: ASTEnv) = {
    def checkImplication(a: AST, b: AST) = {
      val as = env.get(a)._1.toSet
      val bs = env.get(b)._1.toSet
      val cs = as.intersect(bs)
      as.--(cs).foldLeft(FeatureExpr.base)(_ and _).implies(bs.--(cs).foldLeft(FeatureExpr.base)(_ and _).not()).isTautology()
    }
    pack[List[AST]]({ (x,y) => checkImplication(x.head, y.head)})(l.reverse).reverse
  }

  // get type of IfdefBlock:
  // 0 -> only true values
  // 1 -> #if-(#elif)* block
  // 2 -> #if-(#elif)*-#else block
  private def determineTypeOfGroupedIfdefBlocks(l: List[IfdefBlocks], env: ASTEnv): List[(Int, IfdefBlocks)] = {
    l match {
      case (h::t) => {
        val f = h.map({ e => env.featureExpr(e.head) })

        if (f.foldLeft(FeatureExpr.base)(_ and _).isTautology()) (0, h)::determineTypeOfGroupedIfdefBlocks(t, env)
        else if (f.map(_.not()).foldLeft(FeatureExpr.base)(_ and _).isContradiction()) (2, h.reverse)::determineTypeOfGroupedIfdefBlocks(t, env)
        else (1, h)::determineTypeOfGroupedIfdefBlocks(t, env)
      }
      case Nil => List()
    }
  }
}

// defines and uses we can jump to using succ
// beware of List[Opt[_]]!! all list elements can possibly have a different annotation
trait Variables extends ASTNavigation {
  val uses: PartialFunction[Any, Set[Id]] = {case a: Any => findUses(a)}

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