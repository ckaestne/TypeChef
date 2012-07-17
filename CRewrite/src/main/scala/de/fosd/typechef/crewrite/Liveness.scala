package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.c._
import org.kiama.attribution.AttributionBase
import de.fosd.typechef.conditional.Opt

// defines and uses we can jump to using succ
// beware of List[Opt[_]]!! all list elements can possibly have a different annotation
trait Variables {

  val usesVar: PartialFunction[(Any, ASTEnv), Map[FeatureExpr, Set[Id]]] = {
    case (a, env) => findUsesVar(a, env)
  }

  private def findUsesVar(e: Any, env: ASTEnv): Map[FeatureExpr, Set[Id]] = {
    var res = Map[FeatureExpr, Set[Id]]()

    for (r <- uses(e)) {
      val rfexp = env.featureExpr(r)

      val key = res.find(_._1 equivalentTo rfexp)
      key match {
        case None => res = res.+((rfexp, Set(r)))
        case Some((k, v)) => res = res.+((k, v ++ Set(r)))
      }
    }

    res
  }

  val definesVar: PartialFunction[(Any, ASTEnv), Map[FeatureExpr, Set[Id]]] = {
    case (a, env) => findDefinesVar(a, env)
  }

  private def findDefinesVar(e: Any, env: ASTEnv): Map[FeatureExpr, Set[Id]] = {
    var res = Map[FeatureExpr, Set[Id]]()

    for (r <- defines(e)) {
      val rfexp = env.featureExpr(r)

      val key = res.find(_._1 equivalentTo rfexp)
      key match {
        case None => res = res.+((rfexp, Set(r)))
        case Some((k, v)) => res = res.+((k, v ++ Set(r)))
      }
    }

    res
  }

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
      case FunctionCall(params) => params.exprs.map(_.entry).flatMap(findUses).toSet
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
    case Opt(_, entry) => defines(entry)
    case _ => Set()
  }
}

class LivenessCache {
  private val cache: java.util.IdentityHashMap[Any, Map[FeatureExpr, Set[Id]]] = new java.util.IdentityHashMap[Any, Map[FeatureExpr, Set[Id]]]()

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
  private val astIdenEnvHM = new java.util.IdentityHashMap[AST, (AST, ASTEnv)]()

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
        val ss = succ(e, env).filterNot(x => x.entry.isInstanceOf[FunctionDef])
        var res: Set[Id] = Set()
        for (s <- ss.map(_.entry)) {
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
        val u = usesVar(e, env)
        val d = definesVar(e, env)
        var res = out(t)

        for ((k, v) <- d) res = updateMap(res, (k, v), _.diff(_))
        for ((k, v) <- u) res = updateMap(res, (k, v), _.union(_))

        res
      }
    }
  }

  val outrec: PartialFunction[(Product, ASTEnv), Map[FeatureExpr, Set[Id]]] =
    circular[(Product, ASTEnv), Map[FeatureExpr, Set[Id]]](Map()) {
      case t@(e, env) => {
        val ss = succ(e, env).filterNot(x => x.entry.isInstanceOf[FunctionDef])
        var res = Map[FeatureExpr, Set[Id]]()
        for (Opt(f, s) <- ss) {
          if (!astIdenEnvHM.containsKey(s)) astIdenEnvHM.put(s, (s, env))
          for ((fexpin, el) <- in(s))
            res = updateMap(res, (f and fexpin, el), _.union(_))
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
