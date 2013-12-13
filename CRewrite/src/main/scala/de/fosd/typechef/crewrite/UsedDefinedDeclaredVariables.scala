package de.fosd.typechef.crewrite

import org.kiama.attribution.Attribution._

import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional._

// defines functions to compute sets for used, defined, and declared variables
// used for Liveness and ReachingDefinitions
trait UsedDefinedDeclaredVariables {

    // returns all declared Ids independent of their annotation
    val declares: AnyRef => List[Id] =
        attr {
            case DeclarationStatement(decl) => declares(decl)
            case Declaration(_, init) => init.flatMap(declares)
            case InitDeclaratorI(declarator, _, _) => declares(declarator)
            case AtomicNamedDeclarator(_, id, _) => List(id)
            case Opt(_, entry) => declares(entry.asInstanceOf[AnyRef])
            case _ => List()
        }

    // returns all defined Ids independent of their annotation
    val defines: AnyRef => List[Id] =
        attr {
            case AssignExpr(target: Id, _, source) => List(target)
            case DeclarationStatement(d) => defines(d)
            case Declaration(_, init) => init.flatMap(defines)
            case InitDeclaratorI(i, _, _) => defines(i)
            case AtomicNamedDeclarator(_, i, _) => List(i)
            case ExprStatement(_: Id) => List()
            case ExprStatement(PointerDerefExpr(_)) => List()
            case ExprStatement(expr) => defines(expr)
            case PostfixExpr(i@Id(_), SimplePostfixSuffix(_)) => List(i) // a++; or a--;
            case UnaryExpr(kind, i: Id) => if (kind == "++" || kind == "--") List(i) else List() // ++a; or --a;
            case Opt(_, entry) => defines(entry.asInstanceOf[AnyRef])
            case PointerDerefExpr(i: Id) => List(i)
            case _ => List()
        }

    // returns all used Ids independent of their annotation
    val uses: AnyRef => List[Id] =
        attr {
            case ForStatement(expr1, expr2, expr3, _) => uses(expr1) ++ uses(expr2) ++ uses(expr3)
            case ReturnStatement(Some(x)) => uses(x)
            case WhileStatement(expr, _) => uses(expr)
            case DeclarationStatement(d) => uses(d)
            case Declaration(_, init) => init.flatMap(uses)
            case InitDeclaratorI(_, _, Some(i)) => uses(i)
            case AtomicNamedDeclarator(_, id, _) => List(id)
            case NestedNamedDeclarator(_, nestedDecl, _) => uses(nestedDecl)
            case Initializer(_, expr) => uses(expr)
            case i: Id => List(i)
            case FunctionCall(params) => params.exprs.map(_.entry).flatMap(uses)
            case ArrayAccess(expr) => uses(expr)
            case PostfixExpr(_: Id, f: FunctionCall) => uses(f)
            case PostfixExpr(p, s) => uses(p) ++ uses(s)
            case UnaryExpr(_, ex) => uses(ex)
            case SizeOfExprU(expr) => uses(expr)
            case CastExpr(_, expr) => uses(expr)
            case PointerDerefExpr(castExpr) => uses(castExpr)
            case PointerCreationExpr(castExpr) => uses(castExpr)
            case UnaryOpExpr(kind, castExpr) => uses(castExpr)
            case NAryExpr(ex, others) => uses(ex) ++ others.flatMap(uses)
            case NArySubExpr(_, ex) => uses(ex)
            case ConditionalExpr(condition, _, _) => uses(condition)
            case ExprStatement(expr) => uses(expr)
            case AssignExpr(target, op, source) => uses(source) ++ (if (op == "=") List() else uses(target))
            case Opt(_, entry) => uses(entry.asInstanceOf[AnyRef])
            case _ => List()
        }
}
