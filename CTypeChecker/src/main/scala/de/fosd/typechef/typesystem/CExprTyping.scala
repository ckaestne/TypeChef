package de.fosd.typechef.typesystem


import de.fosd.typechef.parser.c._

/**
 * typing C expressions
 */
trait CExprTyping extends CTypes {
    //Variable-Typing Context: identifier to its non-void wellformed type
    type VarTypingContext = Map[String, CType]

    //Function-Typing Context: identifer to function types
    type FunTypingContext = Map[String, CFunction]


    private def structEnvLookup(strEnv: StructEnv, structName: String, fieldName: String): CType = {
        if (strEnv contains structName) {
            val struct = strEnv(structName)
            val field = struct.find(_._1 == fieldName)
            if (field.isDefined)
                field.get._2
            else
                CUnknown(fieldName + " unknown in " + structName)
        } else CUnknown("struct " + structName + " unknown")
    }

    def exprType(varCtx: VarTypingContext, funCtx: FunTypingContext, strEnv: StructEnv, expr: Expr): CType = {
        val et = exprType(varCtx, funCtx, strEnv, _: Expr)
        //TODO assert types in varCtx and funCtx are welltyped and non-void
        expr match {
        /**
         * The standard provides for methods of
         * specifying constants in unsigned, long and oating point types; we omit
         * these for brevity's sake
         */
        //TODO constant 0 is special, can be any pointer or function
            case Constant(_) => CSigned(CInt())
            //variable or function ref TODO check
            case Id(name) =>
                if (varCtx contains name)
                    CObj(varCtx(name))
                else if (funCtx contains name)
                    funCtx(name)
                else CUnknown("unknown id " + name)
            //create pointer
            case PointerCreationExpr(expr) =>
                et(expr) match {
                    case CObj(t) => CPointer(t)
                    case e => CUnknown("& on " + e)
                }
            //pointer dereferencing
            case PointerDerefExpr(expr) =>
                et(expr) match {
                    case CPointer(t) if (t != CVoid) => CObj(t)
                    case e => CUnknown("* on " + e)
                }
            //e.n notation
            case PostfixExpr(expr, PointerPostfixSuffix(".", Id(id))) =>
                et(expr) match {
                    case CObj(CStruct(s)) => CObj(structEnvLookup(strEnv, s, id))
                    case CStruct(s) => structEnvLookup(strEnv, s, id) match {
                        case e if (arrayType(e)) => CUnknown("(" + e + ")." + id + " has array type")
                        case e => e
                    }
                    case e => CUnknown("(" + e + ")." + id)
                }



        //            case class Id(name: String) extends PrimaryExpr
        //
        //            case class Constant(value: Int) extends PrimaryExpr
        //
        //            case class StringLit(name: List[Opt[String]]) extends PrimaryExpr
        //
        //            abstract class PostfixSuffix extends AST
        //
        //            case class SimplePostfixSuffix(t: String) extends PostfixSuffix
        //
        //            case class PointerPostfixSuffix(kind: String, id: Id) extends PostfixSuffix {
        //            }
        //
        //            case class FunctionCall(params: ExprList) extends PostfixSuffix {
        //            }
        //
        //            case class ArrayAccess(expr: Expr) extends PostfixSuffix {
        //            }
        //
        //            case class PostfixExpr(p: Expr, s: List[Opt[PostfixSuffix]]) extends Expr {
        //            }
        //
        //            case class UnaryExpr(kind: String, e: Expr) extends Expr {
        //            }
        //
        //            case class SizeOfExprT(typeName: TypeName) extends Expr {
        //            }
        //
        //            case class SizeOfExprU(expr: Expr) extends Expr {
        //            }
        //
        //            case class CastExpr(typeName: TypeName, expr: Expr) extends Expr {
        //            }
        //
        //            case class UnaryOpExpr(kind: String, castExpr: Expr) extends Expr {
        //            }
        //
        //            case class NAryExpr(e: Expr, others: List[Opt[(String, Expr)]]) extends Expr {
        //            }
        //
        //            case class ConditionalExpr(condition: Expr, thenExpr: Option[Expr], elseExpr: Expr) extends Expr
        //
        //            case class AssignExpr(target: Expr, operation: String, source: Expr) extends Expr
        //
        //            case class ExprList(exprs: List[Opt[Expr]]) extends Expr


        }
    }

}