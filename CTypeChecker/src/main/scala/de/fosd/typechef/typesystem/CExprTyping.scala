package de.fosd.typechef.typesystem


import de.fosd.typechef.parser.c._
import de.fosd.typechef.parser.Opt
import de.fosd.typechef.featureexpr.FeatureExpr

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
            //&a: create pointer
            case PointerCreationExpr(expr) =>
                et(expr) match {
                    case CObj(t) => CPointer(t)
                    case e => CUnknown("& on " + e)
                }
            //*a: pointer dereferencing
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
            //e->n
            case PostfixExpr(expr, PointerPostfixSuffix("->", Id(id))) =>
                et(PostfixExpr(PointerDerefExpr(expr), PointerPostfixSuffix(".", Id(id))))
            case CastExpr(targetTypeName, expr) =>
                val targetType = typeName(targetTypeName)
                val sourceType = et(expr)
                if (targetType == CVoid() || (isScalar(sourceType) && isScalar(targetType)))
                    targetType
                else
                    CUnknown("incorrect cast from " + sourceType + " to " + targetType)
            //a()
            case PostfixExpr(expr, FunctionCall(ExprList(parameterExprs))) =>
            //TODO ignoring variability for now
                et(expr) match {
                    case CFunction(parameterTypes, retType) =>
                        if (parameterExprs.size != parameterTypes.size)
                            CUnknown("parameter number mismatch in " + expr)
                        else
                        if ((parameterExprs zip parameterTypes) forall {
                            case (Opt(_, e), t) => coerce(et(e), t)
                        }) retType
                        else
                            CUnknown("parameter type mismatch")
                    case _ => CUnknown(expr + " is not a function")
                }
            //a=b, a+=b, ...
            case AssignExpr(texpr, op, sexpr) =>
                val stype = et(sexpr)
                val ttype = et(texpr)
                val opType = operationType(op, ttype, stype)
                ttype match {
                    case CObj(t) if (!arrayType(t) && coerce(t, opType)) => t
                    case e => CUnknown("incorrect assignment with " + e + " " + op + " " + stype)
                }
            //a++, a--
            case PostfixExpr(expr, SimplePostfixSuffix(_)) => et(expr) match {
                case CObj(t) if (isScalar(t)) => t
                //TODO check?: not on function references
                case e => CUnknown("incorrect post increment/decrement on type " + e)
            }
            //a+b
            case NAryExpr(expr, opList) =>
            //TODO ignoring variability for now
                var result = et(expr)
                for (Opt(_, (op, thatExpr)) <- opList) {
                    val thatType = et(thatExpr)
                    result = operationType(op, result, thatType)
                }
                result
            //a[e]
            case PostfixExpr(expr, ArrayAccess(idx)) =>
            //syntactic sugar for *(a+i)
                et(PointerDerefExpr(createSum(expr, idx)))
            //"a"
            case StringLit(_) => CPointer(CUnsigned(CChar())) //TODO unsigned?
            //++a, --a
            case UnaryExpr(_, expr) =>
                et(AssignExpr(expr, "+=", Constant("1")))

            case SizeOfExprT(_) => CInt()
            case SizeOfExprU(_) => CInt()
            case UnaryOpExpr(kind, expr) => kind match {
            //TODO complete list: + - ~ ! && and __real__ __imag__
                case _ => CUnknown("unknown unary operator " + kind + " (TODO)")
            }
            case ConditionalExpr(condition, thenExpr, elseExpr) =>
                CUnknown("not implemented yet (TODO)")
        }
    }

    def typeName(name: TypeName): CType = {
        //TODO ignoring variability for now
        if (name.decl.isDefined) return CUnknown("unsupported declarator (TODO)")
        if (name.specifiers.size != 1) return CUnknown("unsupported type with " + name.specifiers.size + " specifiers (TODO)")
        for (Opt(_, specifier) <- name.specifiers) specifier match {
        //TODO handle signed and unsigned
            case PrimitiveTypeSpecifier("double") => return CDouble()
            case PrimitiveTypeSpecifier("char") => return CSigned(CChar())
            case PrimitiveTypeSpecifier("short") => return CShort()
            case PrimitiveTypeSpecifier("int") => return CInt()
            case PrimitiveTypeSpecifier("long") => return CLong()
            case PrimitiveTypeSpecifier("float") => return CFloat()
        }
        return CUnknown("unsupported type " + name)
    }

    /**
     * defines types of various operations
     * TODO currently incomplete and possibly incorrect
     */
    def operationType(op: String, t1: CType, t2: CType): CType = (op, t1, t2) match {
        case ("+", t1, t2) if (coerce(t1, t2)) => t1
        case ("=", _, t2) => t2
        case ("+=", CObj(t1), t2) if (coerce(t1, t2)) => t1
        case _ => CUnknown("unknown operation or incompatible types " + t1 + " " + op + " " + t2)
    }


    private def createSum(a: Expr, b: Expr) =
        NAryExpr(a, List(Opt(FeatureExpr.base, ("+", b))))


}