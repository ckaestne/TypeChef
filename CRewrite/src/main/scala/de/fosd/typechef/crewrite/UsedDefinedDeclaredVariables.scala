package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional._

import org.kiama.attribution.Attribution._

// defines functions to compute sets for used, defined, and declared variables
// used for Liveness and ReachingDefinitions
trait UsedDefinedDeclaredVariables {
    // returns all declared Ids independent of their annotation
    val declares: AnyRef => Set[Id] =
        attr {
            case DeclarationStatement(decl) => declares(decl)
            case Declaration(_, init) => init.toSet.flatMap(declares)
            case InitDeclaratorI(declarator, _, _) => declares(declarator)
            case AtomicNamedDeclarator(_, id, _) => Set(id)
            case Opt(_, entry) => declares(entry.asInstanceOf[AnyRef])
            case _ => Set()
        }

    // returns all defined Ids independent of their annotation
    val defines: AnyRef => Set[Id] =
        attr {
            case AssignExpr(target: Id, _, source) => Set(target)
            case DeclarationStatement(d) => defines(d)
            case Declaration(_, init) => init.toSet.flatMap(defines)
            case InitDeclaratorI(i, _, _) => defines(i)
            case AtomicNamedDeclarator(_, i, _) => Set(i)
            case ExprStatement(_: Id) => Set()
            case ExprStatement(PointerDerefExpr(_)) => Set()
            case ExprStatement(expr) => defines(expr)
            case PostfixExpr(i@Id(_), SimplePostfixSuffix(_)) => Set(i) // a++; or a--;
            case UnaryExpr(kind, i@Id(_)) => if (kind == "++" || kind == "--") Set(i) else Set() // ++a; or --a;
            case Opt(_, entry) => defines(entry.asInstanceOf[AnyRef])
            case PointerDerefExpr(i@Id(_)) => Set(i)
            case _ => Set()
        }

    // returns all used Ids independent of their annotation
    val uses: AnyRef => Set[Id] =
        attr {
            case ForStatement(expr1, expr2, expr3, _) => uses(expr1) ++ uses(expr2) ++ uses(expr3)
            case ReturnStatement(Some(x)) => uses(x)
            case WhileStatement(expr, _) => uses(expr)
            case DeclarationStatement(d) => uses(d)
            case Declaration(_, init) => init.map(_.entry).toSet.flatMap { x: InitDeclarator => uses(x.asInstanceOf[AnyRef]) }
            case InitDeclaratorI(_, _, Some(i)) => uses(i)
            case AtomicNamedDeclarator(_, id, _) => Set(id)
            case NestedNamedDeclarator(_, nestedDecl, _) => uses(nestedDecl)
            case Initializer(_, expr) => uses(expr)
            case i@Id(name) => Set(i)
            case FunctionCall(params) => params.exprs.map(_.entry).toSet.flatMap(uses)
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
            case Opt(_, entry) => uses(entry.asInstanceOf[AnyRef])
            case _ => Set()
        }

}
