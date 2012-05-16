package de.fosd.typechef.typesystem


import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}

/**
 * typing C expressions
 */
trait CExprTyping extends CTypes with CEnv with CDeclTyping with CTypeSystemInterface with CDefUse {

    /**
     * types an expression in an environment, returns a new
     * environment for all subsequent tokens (eg in a sequence)
     */
    def getExprType(expr: Expr, featureExpr: FeatureExpr, env: Env): Conditional[CType] = {
        val et = getExprType(_: Expr, featureExpr, env)
        val etF = getExprType(_: Expr, _: FeatureExpr, env)
        //        TODO assert types in varCtx and funCtx are welltyped and non-void

        val resultType: Conditional[CType] =
            if (!featureExpr.isSatisfiable()) {
                One(CIgnore())
            } else
                expr match {
                    /**
                     * The standard provides for methods of
                     * specifying constants in unsigned, long and oating point types; we omit
                     * these for brevity's sake
                     */
                    //TODO other constant types
                    case Constant(v) =>
                        if (v == "0" || v == "'\\0'") One(CZero())
                        else
                        if (v.head == '\'') One(CUnsigned(CChar()))
                        else
                        if (v.last.toLower == 'l') One(CSigned(CLong()))
                        else One(CSigned(CInt()))
                    //variable or function ref
                    case id@Id(name) =>
                        val ctype = env.varEnv(name)
                        ctype.mapf(featureExpr, {
                            (f, t) =>
                                if (t.isUnknown && f.isSatisfiable()) {
                                    val when = env.varEnv.whenDefined(name)
                                    issueTypeError(Severity.IdLookupError, f, name + " undeclared" +
                                        (if (when.isSatisfiable()) " (only under condition " + when + ")" else ""),
                                        expr)
                                }
                                checkStructs(t, f, env, id)
                        })
                        ctype.map(_.toObj)
                    //&a: create pointer
                    case pc@PointerCreationExpr(expr) =>
                        et(expr).mapf(featureExpr, {
                            case (f, CObj(t)) => CPointer(t)
                            case (f, t: CFunction) => CPointer(t)
                            case (f, e) =>
                                reportTypeError(f, "invalid & on " + pc + " (" + e + ")", pc)
                        })
                    //*a: pointer dereferencing
                    case pd@PointerDerefExpr(expr) =>
                        et(expr).mapf(featureExpr, (f, x) => x.toValue match {
                            case CPointer(t) if (t != CVoid) => t.toObj
                            case fun: CFunction => fun // for some reason deref of a function still yields a valid function in gcc
                            case e =>
                                reportTypeError(f, "invalid * on " + pd + " (" + e + ")", pd)
                        })
                    //e.n notation
                    case p@PostfixExpr(expr, PointerPostfixSuffix(".", i@Id(id))) =>
                        def lookup(fields: ConditionalTypeMap, fexpr: FeatureExpr): Conditional[CType] = {
                            val rt = fields.getOrElse(id, CUnknown("field not found: (" + expr + ")." + id + "; has " + fields))
                            rt.mapf(fexpr, (f, t) => if (t.isUnknown && f.isSatisfiable()) issueTypeError(Severity.FieldLookupError, f, "unknown field " + id, i))
                            rt
                        }

                        et(expr).mapfr(featureExpr, {
                            case (f, CObj(CAnonymousStruct(fields, _))) => lookup(fields, f).map(_.toObj)
                            case (f, CAnonymousStruct(fields, _)) => lookup(fields, f)
                            case (f, CObj(CStruct(s, isUnion))) => structEnvLookup(env.structEnv, s, isUnion, id, p, f).map(_.toObj)
                            case (f, CStruct(s, isUnion)) => structEnvLookup(env.structEnv, s, isUnion, id, p, f).mapf(f, {
                                case (f, e) if (arrayType(e)) =>
                                    reportTypeError(f, "expression " + p + " must not have array " + e, p)
                                case (f, e) => e
                            })
                            case (f, e) =>
                                One(reportTypeError(f, "invalid ." + id + " on " + p + " (" + e + ")", p))
                        })
                    //e->n (by rewrite to &e.n)
                    case p@PostfixExpr(expr, PointerPostfixSuffix("->", Id(id))) =>
                        val newExpr = PostfixExpr(PointerDerefExpr(expr), PointerPostfixSuffix(".", Id(id)))
                        et(newExpr)
                    //(a)b
                    case ce@CastExpr(targetTypeName, expr) =>
                        val targetTypes = getTypenameType(targetTypeName, featureExpr, env)
                        val sourceTypes = et(expr).map(_.toValue)
                        ConditionalLib.mapCombinationF(sourceTypes, targetTypes, featureExpr,
                            (fexpr: FeatureExpr, sourceType: CType, targetType: CType) =>
                                if (targetType == CVoid() ||
                                    isPointer(targetType) ||
                                    (isScalar(sourceType) && isScalar(targetType))) targetType
                                else if (isScalar(targetType) && isPointer(normalize(sourceType))) targetType //cast from pointer to long is valid
                                else if (isCompound(sourceType) && (isStruct(targetType) || isArray(targetType))) targetType //workaround for array/struct initializers
                                else if (sourceType.isIgnore || targetType.isIgnore || sourceType.isUnknown || targetType.isUnknown) targetType
                                else
                                    reportTypeError(fexpr, "incorrect cast from " + sourceType + " to " + targetType, ce)
                        )
                    //a()
                    case pe@PostfixExpr(expr, FunctionCall(ExprList(parameterExprs))) =>
                        val functionType: Conditional[CType] = et(expr)
                        val providedParameterTypes: List[Opt[Conditional[CType]]] = parameterExprs.map({
                            case Opt(f, e) => Opt(f, etF(e, featureExpr and f))
                        })

                        // defuse chain
                        addExprUse(expr, env)

                        val providedParameterTypesExploded: Conditional[List[CType]] = ConditionalLib.explodeOptList(Conditional.flatten(providedParameterTypes))
                        ConditionalLib.mapCombinationF(functionType, providedParameterTypesExploded, featureExpr,
                            (fexpr: FeatureExpr, funType: CType, paramTypes: List[CType]) =>
                                funType.toValue match {
                                    case CPointer(CFunction(parameterTypes, retType)) => typeFunctionCall(expr, parameterTypes, retType, paramTypes, pe, fexpr, env)
                                    case CFunction(parameterTypes, retType) => typeFunctionCall(expr, parameterTypes, retType, paramTypes, pe, fexpr, env)
                                    case u: CUnknown => u
                                    case e =>
                                        reportTypeError(fexpr, expr + " is not a function, but has type " + e, pe)
                                }
                        )

                    //a=b, a+=b, ...
                    case ae@AssignExpr(lexpr, op, rexpr) =>
                        ConditionalLib.mapCombinationF(et(rexpr), et(lexpr), featureExpr,
                            (fexpr: FeatureExpr, rtype: CType, ltype: CType) => {
                                val opType = operationType(op, ltype, rtype, ae, fexpr)
                                ltype match {
                                    case CObj(t) if (coerce(t, opType)) => prepareArray(ltype).toValue
                                    case u: CUnknown => u.toValue
                                    case e => reportTypeError(fexpr, "incorrect assignment with " + e + " " + op + " " + rtype, ae)
                                }
                            })
                    //a++, a--
                    case pe@PostfixExpr(expr, SimplePostfixSuffix(_)) => et(expr) map {
                        prepareArray
                    } map {
                        case CObj(t) if (isScalar(t)) => t //apparently ++ also works on arrays
                        //TODO check?: not on function references
                        case e => reportTypeError(featureExpr, "wrong type argument to increment " + e, pe)
                    }
                    //a+b
                    case ne@NAryExpr(expr, opList) =>
                        ConditionalLib.conditionalFoldLeftFR(opList, et(expr), featureExpr,
                            (fexpr: FeatureExpr, ctype: CType, subExpr: NArySubExpr) =>
                                etF(subExpr.e, fexpr) map (subExprType => operationType(subExpr.op, ctype, subExprType, ne, fexpr))
                        )
                    //a[e]
                    case p@PostfixExpr(expr, ArrayAccess(idx)) =>
                        //syntactic sugar for *(a+i)
                        val newExpr = PointerDerefExpr(createSum(expr, idx))
                        et(newExpr)
                    //"a"
                    case StringLit(_) => One(CPointer(CSignUnspecified(CChar()))) //unspecified sign according to Paolo
                    //++a, --a
                    case p@UnaryExpr(_, expr) =>
                        val newExpr = AssignExpr(expr, "+=", Constant("1"))
                        et(newExpr)

                    case SizeOfExprT(_) => One(CUnsigned(CInt())) //actual type should be "size_t" as defined in stddef.h on the target system.
                    case SizeOfExprU(_) => One(CUnsigned(CInt()))
                    case ue@UnaryOpExpr(kind, expr) =>
                        if (kind == "&&")
                        //label deref, TODO check that label is actually declared
                            One(CPointer(CVoid())) //label dereference
                        else {
                            val exprType = et(expr).map(_.toValue)
                            kind match {
                                //TODO complete list: __real__ __imag__
                                //TODO promotions
                                case "+" => exprType.mapf(featureExpr,
                                    (fexpr, x) => if (isArithmetic(x) || x.isIgnore) promote(x) else reportTypeError(fexpr, "incorrect type, expected arithmetic, was " + x, ue))
                                case "-" => exprType.mapf(featureExpr,
                                    (fexpr, x) => if (isArithmetic(x) || x.isIgnore) promote(x) else reportTypeError(fexpr, "incorrect type, expected arithmetic, was " + x, ue))
                                case "~" => exprType.mapf(featureExpr,
                                    (fexpr, x) => if (isIntegral(x) || x.isIgnore) CSigned(CInt()) else reportTypeError(fexpr, "incorrect type, expected integer, was " + x, ue))
                                case "!" => exprType.mapf(featureExpr,
                                    (fexpr, x) => if (isScalar(x) || x.isIgnore) CSigned(CInt()) else reportTypeError(fexpr, "incorrect type, expected scalar, was " + x, ue))
                                case _ => One(reportTypeError(featureExpr, "unknown unary operator " + kind + " (TODO)", ue))
                            }
                        }
                    //x?y:z  (gnuc: x?:z === x?x:z)
                    case ce@ConditionalExpr(condition, thenExpr, elseExpr) =>
                        et(condition) mapfr(featureExpr, {
                            (fexpr, conditionType) =>
                                if (isScalar(conditionType))
                                    getConditionalExprType(etF(thenExpr.getOrElse(condition), fexpr), etF(elseExpr, fexpr), fexpr, ce)
                                else if (conditionType.isIgnore) One(conditionType)
                                else One(reportTypeError(fexpr, "invalid type of condition: " + conditionType, ce))
                        })
                    //compound statement in expr. ({a;b;c;}), type is the type of the last statement
                    case CompoundStatementExpr(compoundStatement) =>
                        getStmtType(compoundStatement, featureExpr, env)._1
                    case ExprList(exprs) => //comma operator, evaluated left to right, last expr yields value and type; like compound statement expression
                        //implemented like compound statement
                        //get a type of every inner expression, collect OptList of types (with one type for every statement, under the same conditions)
                        val typeOptList: List[Opt[Conditional[CType]]] =
                            for (Opt(exprFeature, innerExpr) <- exprs) yield {
                                Opt(exprFeature, etF(innerExpr, featureExpr and exprFeature))
                            }

                        //return last type
                        val lastType: Conditional[Option[Conditional[CType]]] = ConditionalLib.lastEntry(typeOptList)
                        val t: Conditional[CType] = lastType.mapr({
                            case None => One(CVoid()) //TODO what is the type of an empty ExprList?
                            case Some(ctype) => ctype
                        }) simplify (featureExpr);

                        //return original environment, definitions don't leave this scope
                        t

                    case LcurlyInitializer(inits) => One(CCompound()) //TODO more specific checks, currently just use CCompound which can be cast into any structure or array
                    case GnuAsmExpr(_, _, _, _) => One(CIgnore()) //don't care about asm now
                    case BuiltinOffsetof(_, _) => One(CSigned(CInt()))
                    case c: BuiltinTypesCompatible => One(CSigned(CInt())) //http://www.delorie.com/gnu/docs/gcc/gcc_81.html
                    case c: BuiltinVaArgs => One(CIgnore())
                    case AlignOfExprT(typename) => getTypenameType(typename, featureExpr, env); One(CSigned(CInt()))
                    case AlignOfExprU(expr) => et(expr); One(CSigned(CInt()))

                    //TODO initializers 6.5.2.5
                    case e => One(reportTypeError(featureExpr, "unknown expression " + e + " (TODO)", e))
                }

        typedExpr(expr, resultType, featureExpr)
        addEnv(expr, env)
        resultType.simplify(featureExpr)
    }

    private def getConditionalExprType(thenTypes: Conditional[CType], elseTypes: Conditional[CType], featureExpr: FeatureExpr, where: AST) =
        ConditionalLib.mapCombinationF(thenTypes, elseTypes, featureExpr, (featureExpr: FeatureExpr, thenType: CType, elseType: CType) => {
            (thenType.toValue, elseType.toValue) match {
                case (CPointer(CVoid()), CPointer(x)) => CPointer(x) //spec
                case (CPointer(x), CPointer(CVoid())) => CPointer(x) //spec
                case (t1, t2) if (coerce(t1, t2)) => converse(t1, t2) //spec
                case (t1, t2) => reportTypeError(featureExpr, "different address spaces in conditional expression: " + t1 + " and " + t2, where)
            }
        })


    /**
     * defines types of various operations
     * TODO currently incomplete and possibly incorrect
     *
     * visible only for test cases
     */
    private[typesystem] def operationType(op: String, type1: CType, type2: CType, where: AST, featureExpr: FeatureExpr): CType = {
        def pointerArthOp(o: String) = Set("+", "-") contains o
        def pointerArthAssignOp(o: String) = Set("+=", "-=") contains o
        def assignOp(o: String) = Set("+=", "/=", "-=", "*=", "%=", "<<=", ">>=", "&=", "|=", "^=") contains o
        def compOp(o: String) = Set("==", "!=", "<", ">", "<=", ">=") contains o
        def logicalOp(o: String) = Set("&&", "||") contains o
        def bitwiseOp(o: String) = Set("&", "|", "^", "~") contains o
        def shiftOp(o: String) = Set("<<", ">>") contains o

        (op, normalize(type1), normalize(type2)) match {
            //pointer arithmetic
            case (_, CIgnore(), _) => CIgnore()
            case (_, _, CIgnore()) => CIgnore()
            case (o, t1, t2) if (pointerArthOp(o) && isArithmetic(t1) && isArithmetic(t2) && coerce(t1, t2)) => converse(t1, t2) //spec
            case (o, t1, t2) if (pointerArthOp(o) && isPointer(t1) && isIntegral(t2)) => type1.toValue //spec
            case ("+", t1, t2) if (isIntegral(t1) && isPointer(t2)) => type2.toValue //spec
            case ("-", t1, t2) if (isPointer(t1) && (t1 == t2)) => CSigned(CInt()) //spec
            case (o, t1, t2) if ((Set("+=", "-=") contains o) && type1.isObject && isPointer(t1) && isIntegral(t2)) => type1.toValue //spec
            case ("+=", t1, t2) if (type1.isObject && isIntegral(t1) && isPointer(t2)) => type2.toValue //spec
            case ("-=", t1, t2) if (type1.isObject && isPointer(t1) && (t1 == t2)) => CSigned(CInt()) //spec
            //bitwise operations defined on isIntegral
            case (op, t1, t2) if (bitwiseOp(op) && isIntegral(t1) && isIntegral(t2)) => converse(t1, t2)
            case (op, t1, t2) if (shiftOp(op) && isIntegral(t1) && isIntegral(t2)) => promote(t1) //spec

            //comparisons
            case (op, t1, t2) if (compOp(op) && isArithmetic(t1) && isArithmetic(t2)) => CSigned(CInt()) //spec
            case (op, t1, t2) if (compOp(op) && isPointer(t1) && isPointer(t2) && coerce(t1, t2)) => CSigned(CInt()) //spec


            case ("*", t1, t2) if (isArithmetic(t1) && isArithmetic(t2) && coerce(t1, t2)) => converse(t1, t2)
            case ("/", t1, t2) if (isArithmetic(t1) && isArithmetic(t2) && coerce(t1, t2)) => converse(t1, t2)
            case ("%", t1, t2) if (isIntegral(t1) && isIntegral(t2) && coerce(t1, t2)) => converse(t1, t2)
            case ("=", t1, t2) if (type1.isObject) => type2.toValue //TODO spec says return t1?
            case (o, t1, t2) if (logicalOp(o) && isScalar(t1) && isScalar(t2)) => CSigned(CInt()) //spec
            case (o, t1, t2) if (assignOp(o) && type1.isObject && coerce(t1, t2)) => type2.toValue //TODO spec says return t1?
            case (o, t1, t2) if (pointerArthAssignOp(o) && type1.isObject && isPointer(t1) && isIntegral(t2)) => type1.toValue
            case (o, t1, t2) =>
                if (t1.isUnknown || t2.isUnknown)
                    CUnknown(t1 + " " + op + " " + t2)
                else
                    reportTypeError(featureExpr, "unknown operation or incompatible types " + type1 + " " + op + " " + type2, where)
        }
    }

    private def prepareArray(t: CType): CType = t match {
        case CObj(CArray(x, _)) => CObj(CPointer(x))
        case x => x
    }


    /**
     * returns the wider of two types for automatic widening
     */
    def wider(t1: CType, t2: CType) =
        if (t1 < t2) t2 else t1


    private def createSum(a: Expr, b: Expr) =
        NAryExpr(a, List(Opt(FeatureExprFactory.True, NArySubExpr("+", b))))


    private def typeFunctionCall(expr: AST, parameterTypes: Seq[CType], retType: CType, _foundTypes: List[CType],
                                 funCall: PostfixExpr, featureExpr: FeatureExpr, env: Env): CType = {
        checkStructs(retType, featureExpr, env, expr)
        parameterTypes.map(checkStructs(_, featureExpr, env, expr))

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
            reportTypeError(featureExpr, "parameter number mismatch: expected " + expectedTypes.size + " parameter but found " + foundTypes.size + " in " + expr + " (expected types: " + parameterTypes + ")", funCall)
        else
        if (areParameterCompatible(foundTypes, expectedTypes))
            retType
        else {
            //better reporting
            val nr = 1.to(foundTypes.size)
            val problems = findIncompatibleParamter(foundTypes, expectedTypes).map(!_)
            val data = nr zip problems zip foundTypes zip expectedTypes
            for ((((nr, probl), foundType), expectedType) <- data)
                if (probl)
                    reportTypeError(featureExpr,
                        "type mismatch for parameter " + nr + ": expected " + expectedType + " found " + foundType, funCall)
            CUnknown("parameter mismatch in " + funCall)
        }
    }


    private def areParameterCompatible(foundTypes: Seq[CType], expectedTypes: Seq[CType]): Boolean =
        findIncompatibleParamter(foundTypes, expectedTypes) forall (x => x)


    private def findIncompatibleParamter(foundTypes: Seq[CType], expectedTypes: Seq[CType]): Seq[Boolean] =
        (foundTypes zip expectedTypes) map {
            case (ft, et) => coerce(et, ft) || ft.isUnknown
        }


    private def structEnvLookup(strEnv: StructEnv, structName: String, isUnion: Boolean, fieldName: String, astNode: Expr, featureExpr: FeatureExpr): Conditional[CType] = {
        assert(strEnv.someDefinition(structName, isUnion), "struct/union " + structName + " unknown") //should not occur by construction. system won't build a struct type if its name is not in the struct table

        val struct: ConditionalTypeMap = strEnv.get(structName, isUnion)
        val ctype = struct.getOrElse(fieldName, CUnknown("field " + fieldName + " unknown in " + structName))

        ctype.mapf(featureExpr, {
            (f, t) => if (t.isUnknown && f.isSatisfiable()) issueTypeError(Severity.FieldLookupError, f, "field " + fieldName + " unknown in " + structName, astNode)
        })

        ctype
    }


    //implemented by CTypeSystem
    def getStmtType(stmt: Statement, featureExpr: FeatureExpr, env: Env): (Conditional[CType], Env)


}