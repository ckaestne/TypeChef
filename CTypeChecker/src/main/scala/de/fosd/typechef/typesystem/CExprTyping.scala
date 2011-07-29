package de.fosd.typechef.typesystem


import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.FeatureExpr
import org.kiama.attribution.Attribution._
import org.kiama._

/**
 * typing C expressions
 */
trait CExprTyping extends CTypes with CTypeEnv with CDeclTyping {

    def ctype(expr: Expr): TConditional[CType] = expr -> exprType

    def ctype(expr: Expr, context: AST): TConditional[CType] =
        getExprType(outerVarEnv(context), outerStructEnv(context), expr)

    val exprType: Expr ==> TConditional[CType] = attr {
        case expr => getExprType(expr -> varEnv, expr -> structEnv, expr)
    }

    def getStmtType(stmt: Statement): TConditional[CType]

    //implemented by CStmtTyping

    private def structEnvLookup(strEnv: StructEnv, structName: String, isUnion: Boolean, fieldName: String): TConditional[CType] = {
        assert(strEnv.someDefinition(structName, isUnion), "struct/union " + structName + " unknown") //should not occur by construction. system won't build a struct type if its name is not in the struct table

        val struct: ConditionalTypeMap = strEnv.get(structName, isUnion)
        struct.getOrElse(fieldName, CUnknown("field " + fieldName + " unknown in " + structName))
    }

    //    private def anonymousStructLookup(fields: List[(String, CType)], fieldName:String):CType =
    //        if (fie)

    private[typesystem] def getExprType(varCtx: VarTypingContext, strEnv: StructEnv, expr: Expr): TConditional[CType] = {
        val et = getExprType(varCtx, strEnv, _: Expr)
        //TODO assert types in varCtx and funCtx are welltyped and non-void
        expr match {
            /**
             * The standard provides for methods of
             * specifying constants in unsigned, long and oating point types; we omit
             * these for brevity's sake
             */
            //TODO constant 0 is special, can be any pointer or function
            //TODO other constant types
            case Constant(v) => if (v.last.toLower == 'l') TOne(CSigned(CLong())) else TOne(CSigned(CInt()))
            //variable or function ref
            case Id(name) => varCtx(name).map(_.toObj)
            //&a: create pointer
            case PointerCreationExpr(expr) =>
                et(expr).map({
                    case CObj(t) => CPointer(t)
                    case t: CFunction => CPointer(t)
                    case e => CUnknown("& on " + e)
                })
            //*a: pointer dereferencing
            case PointerDerefExpr(expr) =>
                et(expr).map(_.toValue match {
                    case CPointer(t) if (t != CVoid) => t.toObj
                    case f: CFunction => f // for some reason deref of a function still yields a valid function in gcc
                    case e => CUnknown("* on " + e)
                })
            //e.n notation
            case PostfixExpr(expr, PointerPostfixSuffix(".", Id(id))) =>
                def lookup(fields: ConditionalTypeMap): TConditional[CType] =
                    fields.getOrElse(id, CUnknown("field not found: (" + expr + ")." + id + "; has " + fields))

                et(expr).mapr({
                    case CObj(CAnonymousStruct(fields, _)) => lookup(fields).map(_.toObj)
                    case CAnonymousStruct(fields, _) => lookup(fields)
                    case CObj(CStruct(s, isUnion)) => structEnvLookup(strEnv, s, isUnion, id).map(_.toObj)
                    case CStruct(s, isUnion) => structEnvLookup(strEnv, s, isUnion, id).map({
                        case e if (arrayType(e)) => CUnknown("(" + e + ")." + id + " has array type")
                        case e => e
                    })
                    case e => TOne(CUnknown("(" + e + ")." + id))
                })
            //e->n (by rewrite to &e.n)
            case p@PostfixExpr(expr, PointerPostfixSuffix("->", Id(id))) =>
                val newExpr = PostfixExpr(PointerDerefExpr(expr.clone), PointerPostfixSuffix(".", Id(id))) //deep cloning not necessary, because no properties affected by parent are changed here
                newExpr.parent = p.parent //important clone entries and set parent to avoid rewrites of the original ast
                et(newExpr)
            //(a)b
            case CastExpr(targetTypeName, expr) =>
                val targetTypes = ctype(targetTypeName)
                val sourceTypes = et(expr).map(_.toValue)
                ConditionalLib.mapCombination(sourceTypes, targetTypes,
                    (sourceType: CType, targetType: CType) =>
                        if (targetType == CVoid() ||
                                targetType == CPointer(CVoid()) ||
                                (isScalar(sourceType) && isScalar(targetType))) targetType
                        else if (isCompound(sourceType) && (isStruct(targetType) || isArray(targetType))) targetType //workaround for array/struct initializers
                        else if (sourceType == CIgnore()) targetType
                        else
                            CUnknown("incorrect cast from " + sourceType + " to " + targetType)
                ).simplify
            //a()
            case PostfixExpr(expr, FunctionCall(ExprList(parameterExprs))) =>
                val functionType: TConditional[CType] = et(expr)
                val providedParameterTypes: List[Opt[TConditional[CType]]] = parameterExprs.map({
                    case Opt(f, e) => Opt(f, et(e))
                })
                val providedParameterTypesExploded: TConditional[List[CType]] = ConditionalLib.explodeOptList(TConditional.flatten(providedParameterTypes))
                ConditionalLib.mapCombination(functionType, providedParameterTypesExploded,
                    (funType: CType, paramTypes: List[CType]) => {
                        funType.toValue match {
                            case CPointer(CFunction(parameterTypes, retType)) => typeFunctionCall(expr, parameterTypes, retType, paramTypes)
                            case CFunction(parameterTypes, retType) => typeFunctionCall(expr, parameterTypes, retType, paramTypes)
                            case e => CUnknown(expr + " is not a function, but " + e)
                        }
                    })

            //a=b, a+=b, ...
            case AssignExpr(lexpr, op, rexpr) =>
                ConditionalLib.mapCombination(et(rexpr), et(lexpr),
                    (rtype: CType, ltype: CType) => {
                        val opType = operationType(op, ltype, rtype)
                        ltype match {
                            case CObj(t) if (!arrayType(t) && coerce(t, opType)) => t.toValue
                            case CObj(t) if (!arrayType(t) && coerce(t, opType)) => t.toValue
                            case e => CUnknown("incorrect assignment with " + e + " " + op + " " + rtype)
                        }
                    })
            //a++, a--
            case PostfixExpr(expr, SimplePostfixSuffix(_)) => et(expr) map {
                case CObj(t) if (isScalar(t)) => t
                //TODO check?: not on function references
                case e => CUnknown("incorrect post increment/decrement on type " + e)
            }
            //a+b
            case NAryExpr(expr, opList) =>
                ConditionalLib.conditionalFoldRightR(opList, et(expr),
                    (subExpr: NArySubExpr, ctype: CType) =>
                        et(subExpr.e) map (subExprType => operationType(subExpr.op, ctype, subExprType))
                )
            //a[e]
            case p@PostfixExpr(expr, ArrayAccess(idx)) =>
                //syntactic sugar for *(a+i)
                val newExpr = PointerDerefExpr(createSum(expr.clone, idx.clone)) //deep cloning not necessary, because no properties affected by parent are changed here
                newExpr.parent = p.parent //important clone entries and set parent to avoid rewrites of the original ast
                et(newExpr)
            //"a"
            case StringLit(_) => TOne(CPointer(CSignUnspecified(CChar()))) //unspecified sign according to Paolo
            //++a, --a
            case p@UnaryExpr(_, expr) =>
                val newExpr = AssignExpr(expr.clone, "+=", Constant("1")) //deep cloning not necessary, because no properties affected by parent are changed here
                newExpr.parent = p.parent //important clone entries and set parent to avoid rewrites of the original ast
                et(newExpr)

            case SizeOfExprT(_) => TOne(CUnsigned(CInt())) //actual type should be "size_t" as defined in stddef.h on the target system.
            case SizeOfExprU(_) => TOne(CUnsigned(CInt()))
            case UnaryOpExpr(kind, expr) =>
                val exprType = et(expr).map(_.toValue)
                kind match {
                    //TODO complete list: __real__ __imag__
                    //TODO promotions
                    case "+" => exprType.map(x => if (isArithmetic(x)) promote(x) else CUnknown("incorrect type, expected arithmetic, was " + x))
                    case "-" => exprType.map(x => if (isArithmetic(x)) promote(x) else CUnknown("incorrect type, expected arithmetic, was " + x))
                    case "~" => exprType.map(x => if (isIntegral(x)) CSigned(CInt()) else CUnknown("incorrect type, expected integer, was " + x))
                    case "!" => exprType.map(x => if (isScalar(x)) CSigned(CInt()) else CUnknown("incorrect type, expected scalar, was " + x))
                    case "&&" => TOne(CPointer(CVoid())) //label dereference
                    case _ => TOne(CUnknown("unknown unary operator " + kind + " (TODO)"))
                }
            //x?y:z  (gnuc: x?:z === x?x:z)
            case ConditionalExpr(condition, thenExpr, elseExpr) =>
                et(condition) mapr {
                    conditionType =>
                        if (isScalar(conditionType))
                            getConditionalExprType(ctype(thenExpr.getOrElse(condition)), ctype(elseExpr))
                        else TOne(CUnknown("invalid type of condition: " + conditionType))
                }
            //compound statement in expr. ({a;b;c;}), type is the type of the last statement
            case CompoundStatementExpr(compoundStatement) =>
                getStmtType(compoundStatement)
            case ExprList(exprs) => //comma operator, evaluated left to right, last expr yields value and type; like compound statement expression
                ConditionalLib.lastEntry(exprs).mapr({
                    case None => TOne(CVoid())
                    case Some(expr) => et(expr)
                })
            case LcurlyInitializer(inits) => TOne(CCompound()) //TODO more specific checks, currently just use CCompound which can be cast into any structure or array
            case GnuAsmExpr(_, _, _) => TOne(CIgnore()) //don't care about asm now
            case BuiltinOffsetof(_, _) => TOne(CSigned(CInt()))
            case c: BuiltinTypesCompatible => TOne(CSigned(CInt())) //http://www.delorie.com/gnu/docs/gcc/gcc_81.html
            case c: BuiltinVaArgs => TOne(CSigned(CInt()))

            //TODO initializers 6.5.2.5
            case e => TOne(CUnknown("unknown expression " + e + " (TODO)"))
        }
    }

    private def getConditionalExprType(thenTypes: TConditional[CType], elseTypes: TConditional[CType]) =
        ConditionalLib.mapCombination(thenTypes, elseTypes, (thenType: CType, elseType: CType) => {
            (thenType.toValue, elseType.toValue) match {
                case (CPointer(CVoid()), CPointer(x)) => CPointer(x) //spec
                case (CPointer(x), CPointer(CVoid())) => CPointer(x) //spec
                case (t1, t2) if (coerce(t1, t2)) => converse(t1, t2) //spec
                case (t1, t2) => CUnknown("different address spaces in conditional expression: " + t1 + " and " + t2)
            }
        })


    /**
     * defines types of various operations
     * TODO currently incomplete and possibly incorrect
     */
    def operationType(op: String, type1: CType, type2: CType): CType = {
        def pointerArthOp(o: String) = Set("+", "-") contains o
        def pointerArthAssignOp(o: String) = Set("+=", "-=") contains o
        def assignOp(o: String) = Set("+=", "/=", "-=", "*=", "%=", "<<=", ">>=", "&=", "|=", "^=") contains o
        def compOp(o: String) = Set("==", "!=", "<", ">", "<=", ">=") contains o
        def artithmeticOp(o: String) = Set("+", "-", "*", "/", "%") contains o
        def logicalOp(o: String) = Set("&&", "||") contains o
        def bitwiseOp(o: String) = Set("&", "|", "^", "~") contains o
        def shiftOp(o: String) = Set("<<", ">>") contains o

        (op, type1.toValue, type2.toValue) match {
            //pointer arithmetic
            case (o, t1, t2) if (pointerArthOp(o) && isArithmetic(t1) && isArithmetic(t2) && coerce(t1, t2)) => converse(t1, t2) //spec
            case (o, t1, t2) if (pointerArthOp(o) && isPointer(t1) && isIntegral(t2)) => t1 //spec
            case ("+", t1, t2) if (isIntegral(t1) && isPointer(t2)) => t2 //spec
            case ("-", t1, t2) if (isPointer(t1) && (t1 == t2)) => CSigned(CInt()) //spec
            //bitwise operations defined on isIntegral
            case (op, t1, t2) if (bitwiseOp(op) && isIntegral(t1) && isIntegral(t2)) => converse(t1, t2)
            case (op, t1, t2) if (shiftOp(op) && isIntegral(t1) && isIntegral(t2)) => promote(t1) //spec

            //comparisons
            case (op, t1, t2) if (compOp(op) && isScalar(t1) && isScalar(t2)) => CSigned(CInt()) //spec
            case (op, t1, t2) if (compOp(op) && isPointer(t1) && isPointer(t2) && coerce(t1, t2)) => CSigned(CInt()) //spec


            case ("*", t1, t2) if (isArithmetic(t1) && isArithmetic(t2) && coerce(t1, t2)) => converse(t1, t2)
            case ("/", t1, t2) if (isArithmetic(t1) && isArithmetic(t2) && coerce(t1, t2)) => converse(t1, t2)
            case ("%", t1, t2) if (isIntegral(t1) && isIntegral(t2) && coerce(t1, t2)) => converse(t1, t2)
            case ("=", t1, t2) if (type1.isObject) => t2.toValue //TODO spec says return t1?
            case (o, t1, t2) if (logicalOp(o) && isScalar(t1) && isScalar(t2)) => CSigned(CInt()) //spec
            case (o, t1, t2) if (assignOp(o) && type1.isObject && coerce(t1, t2)) => t2.toValue //TODO spec says return t1?
            case (o, t1, t2) if (pointerArthAssignOp(o) && type1.isObject && isPointer(t1) && isIntegral(t2)) => t1
            case _ => CUnknown("unknown operation or incompatible types " + type1 + " " + op + " " + type2)
        }
    }


    /**
     * returns the wider of two types for automatic widening
     * TODO check correctness
     */
    def wider(t1: CType, t2: CType) =
        if (t1 < t2) t2 else t1


    private def createSum(a: Expr, b: Expr) =
        NAryExpr(a, List(Opt(FeatureExpr.base, NArySubExpr("+", b))))


    private def typeFunctionCall(expr: AST, parameterTypes: Seq[CType], retType: CType, _foundTypes: List[CType]): CType = {
        var expectedTypes = parameterTypes
        var foundTypes = _foundTypes
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
        if (areParameterCompatible(foundTypes, expectedTypes))
            retType
        else
            CUnknown("parameter type mismatch: expected " + parameterTypes + " found " + foundTypes + " (matching: " + findIncompatibleParamter(foundTypes, expectedTypes).mkString(", ") + ")")
    }

    private def areParameterCompatible(foundTypes: Seq[CType], expectedTypes: Seq[CType]): Boolean =
        findIncompatibleParamter(foundTypes, expectedTypes) forall (x => x)


    private def findIncompatibleParamter(foundTypes: Seq[CType], expectedTypes: Seq[CType]): Seq[Boolean] =
        (foundTypes zip expectedTypes) map {
            case (ft, et) => coerce(ft, et)
        }


}