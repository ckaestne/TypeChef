package de.fosd.typechef.typesystem


import de.fosd.typechef.parser.c._
import de.fosd.typechef.parser.Opt
import de.fosd.typechef.featureexpr.FeatureExpr
import org.kiama.attribution.Attribution._
import org.kiama._

/**
 * typing C expressions
 */
trait CExprTyping extends CTypes with CTypeEnv {

    def ctype(expr: Expr) = expr -> exprType
    val exprType: Expr ==> CType = attr {
        case expr => getExprType(expr -> varEnv, expr -> structEnv, expr)
    }

    private def structEnvLookup(strEnv: StructEnv, structName: String, fieldName: String): CType = {
        if (strEnv contains structName) {
            val struct = strEnv.get(structName)
            //TODO handle alternatives
            val field = struct.find(_._1 == fieldName)
            if (field.isDefined)
                field.get._3
            else
                CUnknown(fieldName + " unknown in " + structName)
        } else CUnknown("struct " + structName + " unknown")
    }

    private[typesystem] def getExprType(varCtx: VarTypingContext, strEnv: StructEnv, expr: Expr): CType = {
        val et = getExprType(varCtx, strEnv, _: Expr)
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
            case Id(name) => varCtx(name).toObj
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
                et(expr) map {
                    case CFunction(parameterTypes, retType) =>
                        var expectedTypes = parameterTypes
                        var foundTypes = parameterExprs.map({case Opt(_, e) => et(e)})
                        //variadic macros
                        if (expectedTypes.lastOption == Some(CVarArgs())) {
                            expectedTypes = expectedTypes.dropRight(1)
                            foundTypes = foundTypes.take(expectedTypes.size)
                        }
                        else if (expectedTypes.lastOption == Some(CVoid()))
                            expectedTypes = expectedTypes.dropRight(1)

                        //check parameter size and types
                        if (expectedTypes.size != foundTypes.size)
                            CUnknown("parameter number mismatch in " + expr + " (expected: " + parameterTypes + ")")
                        else
                        //                        if ((foundTypes zip expectedTypes) forall {
                        //                            case (ft, et) => coerce(ft, et)
                        //                        }) retType
                        //                        else
                        //                            CUnknown("parameter type mismatch: expected " + parameterTypes + " found " + foundTypes)
                            retType
                    case x: CUnknown => x
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
                for (Opt(_, NArySubExpr(op, thatExpr)) <- opList) {
                    val thatType = et(thatExpr)
                    result = operationType(op, result, thatType)
                }
                result
            //a[e]
            case PostfixExpr(expr, ArrayAccess(idx)) =>
            //syntactic sugar for *(a+i)
                et(PointerDerefExpr(createSum(expr, idx)))
            //"a"
            case StringLit(_) => CPointer(CSignUnspecified(CChar())) //unspecified sign according to Paolo
            //++a, --a
            case UnaryExpr(_, expr) =>
                et(AssignExpr(expr, "+=", Constant("1")))

            case SizeOfExprT(_) => CInt()
            case SizeOfExprU(_) => CInt()
            case UnaryOpExpr(kind, expr) =>
                val exprType = et(expr)
                kind match {
                //TODO complete list: __real__ __imag__
                //TODO promotions
                    case "+" => if (isArithmetic(exprType)) exprType else CUnknown("incorrect type, expected arithmetic, was " + exprType)
                    case "-" => if (isArithmetic(exprType)) exprType else CUnknown("incorrect type, expected arithmetic, was " + exprType)
                    case "~" => if (isIntegral(exprType)) exprType else CUnknown("incorrect type, expected integer, was " + exprType)
                    case "!" => if (isScalar(exprType)) exprType else CUnknown("incorrect type, expected scalar, was " + exprType)
                    case _ => CUnknown("unknown unary operator " + kind + " (TODO)")
                }
            case ConditionalExpr(condition, thenExpr, elseExpr) =>
                CUnknown("not implemented yet (TODO)")
            //TODO initializers 6.5.2.5
            case e => CUnknown("unknown expression " + e + " (TODO)")
        }
    }

    def typeName(name: TypeName): CType = {
        //TODO ignoring variability for now
        if (name.decl.isDefined) return CUnknown("unsupported declarator (TODO)")
        if (name.specifiers.size != 1) return CUnknown("unsupported type with " + name.specifiers.size + " specifiers (TODO)")
        for (Opt(_, specifier) <- name.specifiers) specifier match {
        //TODO handle signed and unsigned
            case DoubleSpecifier() => return CDouble()
            case CharSpecifier() => return CSigned(CChar())
            case ShortSpecifier() => return CShort()
            case IntSpecifier() => return CInt()
            case LongSpecifier() => return CLong()
            case FloatSpecifier() => return CFloat()
            case TypeDefTypeSpecifier(Id(typedefname)) =>
                val env = name -> typedefEnv
                assert(env contains typedefname, "typedefname " + typedefname + " not in typedef environment " + env)
                env(typedefname)
        }
        return CUnknown("unsupported type " + name)
    }

    /**
     * defines types of various operations
     * TODO currently incomplete and possibly incorrect
     */
    def operationType(op: String, t1: CType, t2: CType): CType = (op, t1, t2) match {
        case ("+", t1, t2) if (isArithmetic(t1) && isArithmetic(t2) && coerce(t1, t2)) => t1
        case ("+", t1, t2) if (((isPointer(t1) && isIntegral(t2)) || (isPointer(t2) && isIntegral(t1))) && coerce(t1, t2)) => t1
        case ("-", t1, t2) if (isArithmetic(t1) && isArithmetic(t2) && coerce(t1, t2)) => t1
        //TODO other cases for addition and substraction
        case ("*", t1, t2) if (isArithmetic(t1) && isArithmetic(t2) && coerce(t1, t2)) => t1
        case ("/", t1, t2) if (isArithmetic(t1) && isArithmetic(t2) && coerce(t1, t2)) => t1
        case ("%", t1, t2) if (isIntegral(t1) && isIntegral(t2) && coerce(t1, t2)) => t1
        case ("=", _, t2) => t2
        case ("+=", CObj(t1), t2) if (coerce(t1, t2)) => t1
        case _ => CUnknown("unknown operation or incompatible types " + t1 + " " + op + " " + t2)
    }


    private def createSum(a: Expr, b: Expr) =
        NAryExpr(a, List(Opt(FeatureExpr.base, NArySubExpr("+", b))))


}