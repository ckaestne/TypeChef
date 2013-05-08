package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.UseDeclMap

import de.fosd.typechef.conditional.Opt

class IdentityHashMapCache[A] {
    private val cache: java.util.IdentityHashMap[Any, A] = new java.util.IdentityHashMap[Any, A]()
    def update(k: Any, v: A) { cache.put(k, v) }
    def lookup(k: Any): Option[A] = {
        val v = cache.get(k)
        if (v != null) Some(v)
        else None
    }
}

class Liveness(env: ASTEnv, udm: UseDeclMap, fm: FeatureModel) extends MonotoneFW[Id](env, udm, fm) with IntraCFG  {

    // returns all declared Ids independent of their annotation
    private def declares(a: Any): Set[Id] =
        a match {
            case DeclarationStatement(decl) => declares(decl)
            case Declaration(_, init) => init.flatMap(declares).toSet
            case InitDeclaratorI(declarator, _, _) => declares(declarator)
            case AtomicNamedDeclarator(_, id, _) => Set(id)
            case Opt(_, entry) => declares(entry)
            case _ => Set()
        }

    // returns all defined Ids independent of their annotation
    private def defines(a: Any): Set[Id] =
        a match {
            case i@Id(_) => Set(i)
            case AssignExpr(target, _, source) => defines(target)
            case DeclarationStatement(d) => defines(d)
            case Declaration(_, init) => init.flatMap(defines).toSet
            case InitDeclaratorI(i, _, _) => defines(i)
            case AtomicNamedDeclarator(_, i, _) => Set(i)
            case ExprStatement(_: Id) => Set()
            case ExprStatement(expr) => defines(expr)
            case PostfixExpr(i@Id(_), SimplePostfixSuffix(_)) => Set(i) // a++; or a--;
            case UnaryExpr(_, i@Id(_)) => Set(i) // ++a; or --a;
            case Opt(_, entry) => defines(entry)
            case _ => Set()
    	}

    // returns all used Ids independent of their annotation
    private def uses(a: Any): Set[Id] = {
        a match {
            case ForStatement(expr1, expr2, expr3, _) => uses(expr1) ++ uses(expr2) ++ uses(expr3)
            case ReturnStatement(Some(x)) => uses(x)
            case WhileStatement(expr, _) => uses(expr)
            case DeclarationStatement(d) => uses(d)
            case Declaration(_, init) => init.flatMap(uses(_)).toSet
            case InitDeclaratorI(_, _, Some(i)) => uses(i)
            case AtomicNamedDeclarator(_, id, _) => Set(id)
            case NestedNamedDeclarator(_, nestedDecl, _) => uses(nestedDecl)
            case Initializer(_, expr) => uses(expr)
            case i@Id(name) => Set(i)
            case FunctionCall(params) => params.exprs.map(_.entry).flatMap(uses(_)).toSet
            case ArrayAccess(expr) => uses(expr)
            case PostfixExpr(Id(_), f@FunctionCall(_)) => uses(f)
            case PostfixExpr(p, s) => uses(p) ++ uses(s)
            case UnaryExpr(_, ex) => uses(ex)
            case SizeOfExprU(expr) => uses(expr)
            case CastExpr(_, expr) => uses(expr)
            case PointerDerefExpr(castExpr) => uses(castExpr)
            case PointerCreationExpr(castExpr) => uses(castExpr)
            case UnaryOpExpr(kind, castExpr) => uses(castExpr)
            case NAryExpr(ex, others) => uses(ex) ++ others.flatMap(uses(_)).toSet
            case NArySubExpr(_, ex) => uses(ex)
            case ConditionalExpr(condition, _, _) => uses(condition)
            case ExprStatement(expr) => uses(expr)
            case AssignExpr(target, op, source) => uses(source) ++ (if (op == "=") Set() else uses(target))
            case Opt(_, entry) => uses(entry)
            case _ => Set()
        }
    }

    // returns all declared variables with their annotation
    val declaresVar: PartialFunction[(Any), Map[FeatureExpr, Set[Id]]] = {
        case a => addAnnotation2ResultSet(declares(a))
    }

    def gen(a: AST): Map[FeatureExpr, Set[Id]] = { addAnnotation2ResultSet(uses(a)) }
    def kill(a: AST): Map[FeatureExpr, Set[Id]] = { addAnnotation2ResultSet(defines(a)) }

    // cf. http://www.cs.colostate.edu/~mstrout/CS553/slides/lecture03.pdf
    // page 5
    //  in(n) = uses(n) + (out(n) - defines(n))
    // out(n) = for s in succ(n) r = r + in(s); r

    def id2SetT(i: Id) = Set(i)

    override val analysis_exit = analysis_exit_forward
}
