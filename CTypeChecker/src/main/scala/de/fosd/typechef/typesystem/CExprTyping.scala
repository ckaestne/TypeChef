package de.fosd.typechef.typesystem


import de.fosd.typechef.conditional._
import de.fosd.typechef.error._
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory}
import de.fosd.typechef.parser.c._

/**
  * typing C expressions
  */
trait CExprTyping extends CTypes with CEnv with CDeclTyping with CTypeSystemInterface with CDeclUseInterface {


    /**
      * types an expression in an environment, returns a new
      * environment for all subsequent tokens (eg in a sequence)
      */
    def getExprType(expr: Expr, featureExpr: FeatureExpr, env: Env): Conditional[CType] = {
        getExprTypeRec(expr, featureExpr, env)
    }


    def getExprTypeRec(expr: Expr, featureExpr: FeatureExpr, env: Env, recurse: Boolean = false): Conditional[CType] = {
        val et = getExprTypeRec(_: Expr, featureExpr, env, true)
        def etF(e: Expr, f: FeatureExpr, newEnv: Env = env) = getExprTypeRec(e, f, newEnv, true)
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
                    case c@Constant(v) =>
                        if (opts.warning_long_designator && v.lastOption.map(_ == 'l').getOrElse(false))
                            reportTypeError(featureExpr, "Use \"L,\" not \"l,\" to indicate a long value", c, Severity.SecurityWarning, "long-designator")

                        if (v == "0" || v == "'\\0'" || isHexNull(v)) One(CZero())
                        else if (v.head == '\'') One(CSignUnspecified(CChar()))
                        else if (v.last.toUpper == 'L') One(CSigned(CLong()))
                        else if (v.contains(".")) One(CDouble())
                        else One(CSigned(CInt()))
                    //variable or function ref
                    case id@Id(name) =>
                        var ctype = env.varEnv(name)

                        ctype = markSecurityRelevantFunctions(name, ctype)
                        // addUse(id, featureExpr, env)
                        ctype.vmap(featureExpr, {
                            (f, t) =>
                                if (t.isUnknown && f.isSatisfiable()) {
                                    val when = env.varEnv.whenDefined(name)
                                    issueTypeError(Severity.IdLookupError, f, name + " undeclared" +
                                        (if (when.isSatisfiable()) " (only under condition " + when + ")" else ""),
                                        expr)
                                }
                            //checkStructCompleteness(t, f, env, id) -- do not check on every access, only when a variable is declared, see issue #12
                        })
                        ctype.map(_.toObj)
                    //&a: create pointer
                    case pc@PointerCreationExpr(expr) =>
                        et(expr).vmap(featureExpr, {
                            case (f, t) if t.isObject => t.map(CPointer(_))
                            case (f, t) if t.isFunction => t.map(CPointer(_))
                            case (f, e) =>
                                reportTypeError(f, "invalid & on " + expr + " (" + e + ")", pc)
                        })
                    //*a: pointer dereferencing
                    case pd@PointerDerefExpr(expr) =>
                        et(expr).vmap(featureExpr, (f, x) => x.map(_ match {
                            case CPointer(s@CStruct(name, isUnion)) =>
                                //dereferencing pointers to structures only for complete structures
                                val whenComplete = env.structEnv.isComplete(name, isUnion)
                                if ((f andNot whenComplete).isSatisfiable())
                                    reportTypeError(f andNot whenComplete, "dereferencing pointer to incomplete type " + name + " (complete when " + whenComplete + ")", pd)
                                s
                            case CPointer(t) if (t != CVoid) => t
                            case CArray(t, _) => t
                            case i@CIgnore() => i
                            case fun: CFunction => fun // for some reason deref of a function still yields a valid function in gcc
                            case e =>
                                reportTypeError(f, "invalid * on " + expr + " (" + e + ")", pd)
                        }).toObj.toConst(false).toVolatile(false))
                    //e.n notation
                    case p@PostfixExpr(expr, PointerPostfixSuffix(".", i@Id(id))) =>
                        def lookup(fields: ConditionalTypeMap, fexpr: FeatureExpr): Conditional[CType] = {
                            val rt = fields.getOrElse(id, CUnknown("field not found: (" + expr + ")." + id + "; has " + fields))
                            rt.vmap(fexpr, (f, t) => if (t.isUnknown && f.isSatisfiable()) issueTypeError(Severity.FieldLookupError, f, "unknown field " + id, i))
                            rt
                        }

                        et(expr).vflatMap(featureExpr, {
                            case (f, CType(CAnonymousStruct(_, fields, _), true, _, _)) =>
                                addAnonStructUse(i, fields)
                                lookup(fields, f).map(_.toObj)
                            case (f, CType(CAnonymousStruct(_, fields, _), false, _, _)) =>
                                lookup(fields, f)
                            case (f, CType(CStruct(s, isUnion), true, _, _)) =>
                                addStructUse(i, featureExpr, env, s, isUnion)
                                structEnvLookup(env.structEnv, s, isUnion, id, p, f).map(_.toObj)
                            case (f, CType(CStruct(s, isUnion), false, _, _)) =>
                                structEnvLookup(env.structEnv, s, isUnion, id, p, f).vmap(f, {
                                    case (f, e) if (arrayType(e)) =>
                                        reportTypeError(f, "expression " + p + " must not have array " + e, p)
                                    case (f, e) => e
                                })
                            case (f, e) =>
                                One(reportTypeError(f, "request for member " + id + " in something not a structure or union (" + p + "; " + e + ")", p))
                        })
                    //e->n (by rewrite to *e.n)
                    case p@PostfixExpr(expr, PointerPostfixSuffix("->", i@Id(id))) =>
                        val newExpr = PostfixExpr(PointerDerefExpr(expr).setPositionRange(p), PointerPostfixSuffix(".", i).setPositionRange(p)).setPositionRange(p)
                        newExpr.setPositionRange(p.getPositionFrom, p.getPositionTo) //enable line reporting in error messages
                        newExpr.p.setPositionRange(expr.getPositionFrom, expr.getPositionTo) //enable line reporting in error messages
                        newExpr.s.setPositionRange(i.getPositionFrom, i.getPositionTo) //enable line reporting in error messages
                        et(newExpr)
                    //(a)b
                    case ce@CastExpr(targetTypeName, expr) =>
                        val targetTypes = getTypenameType(targetTypeName, featureExpr, env)
                        addUse(expr, featureExpr, env)
                        val sourceTypes = et(expr).map(_.toValue)
                        ConditionalLib.vmapCombinationOp(sourceTypes, targetTypes, featureExpr,
                            (fexpr: FeatureExpr, sourceType: CType, targetType: CType) => {
                                if (opts.warning_const_assignment && sourceType.isConstant && !targetType.isConstant)
                                    reportTypeError(fexpr, "Do not cast away a const qualification '%s <- %s'; may result in undefined behavior".format(targetType.toText, sourceType.toText), ce, Severity.SecurityWarning, "const-cast")

                                if (targetType == CVoid().toCType) targetType
                                else if (sourceType.isIgnore || targetType.isIgnore || sourceType.isUnknown || targetType.isUnknown) targetType
                                else if (isCompound(sourceType) && (isStruct(targetType) || isArray(targetType))) targetType.toObj //workaround for array/struct initializers
                                else if (isAnonymousStruct(targetType) && sourceType.atype!=targetType.atype) //cannot even cast an anonymous struct to itself unless it is from the same (typedef) definition
                                    reportTypeError(fexpr, "conversion to non-scalar type requested (" + sourceType + " to " + targetType+")", ce)
                                else if (isIntegral(sourceType) && isPointer(targetType)) targetType
                                else if (isPointer(sourceType) && isPointer(targetType)) targetType
                                else if (isFunction(sourceType) && isPointer(targetType)) targetType
                                else if (isIntegral(sourceType) && isScalar(targetType)) targetType
                                else if (isArithmetic(sourceType) && isArithmetic(targetType)) targetType
                                else if (targetType.atype == sourceType.atype) targetType // casting to the same type is fine
                                else if (isIntegral(targetType) && isPointer(normalize(sourceType))) targetType //cast from pointer to long is valid
                                else if (isStruct(targetType))    //int -> struct is error // more specific error message
                                    reportTypeError(fexpr, "conversion to non-scalar type requested (" + sourceType + " to " + targetType+")", ce)
                                else
                                    reportTypeError(fexpr, "incorrect cast from " + sourceType + " to " + targetType, ce)
                            })
                    //a()
                    case pe@PostfixExpr(expr, FunctionCall(ExprList(parameterExprs))) =>
                        val functionType: Conditional[CType] = et(expr)
                        val hasSecurityRelevantFunction = functionType.exists({
                            case CType(f: CFunction, _, _, _) => f.securityRelevant;
                            case _ => false
                        })
                        val providedParameterTypes: List[Opt[Conditional[CType]]] = parameterExprs.map({
                            case Opt(f, e) => Opt(f, etF(e, featureExpr and f, env.markSecurityRelevant(hasSecurityRelevantFunction, "sensitive function parameters")))
                        })
                        //                        parameterExprs.foreach(x => getExprTypeRec(x.entry, featureExpr.and(x.condition), env)) // redundant to previous statement

                        val providedParameterTypesExploded: Conditional[List[CType]] = ConditionalLib.explodeOptList(ConditionalLib.flatten(providedParameterTypes))
                        ConditionalLib.vmapCombinationOp(functionType, providedParameterTypesExploded, featureExpr,
                            (fexpr: FeatureExpr, funType: CType, paramTypes: List[CType]) =>
                                funType.atype match {
                                    case CPointer(CFunction(parameterTypes, retType)) => typeFunctionCall(expr, parameterTypes, retType, paramTypes, pe, fexpr, env)
                                    case CFunction(parameterTypes, retType) => typeFunctionCall(expr, parameterTypes, retType, paramTypes, pe, fexpr, env)
                                    case u: CUnknown => u
                                    case e =>
                                        reportTypeError(fexpr, expr + " is not a function, but has type " + e, pe)
                                }
                        )

                    //a=b, a+=b, ...
                    case ae@AssignExpr(lexpr, op, rexpr) =>
                        ConditionalLib.vmapCombinationOp(et(rexpr), et(lexpr), featureExpr,
                            (fexpr: FeatureExpr, rtype: CType, ltype: CType) => {
                                //security check for integer overflows when operand is used in pointer arithmetic (ie. also array access)
                                //checks expression again in a tighter context
                                if (isPointer(ltype) && pointerArthAssignOp(op)) etF(rexpr, fexpr, env.markSecurityRelevant("array access/pointer arithmetic"))

                                if (opts.warning_volatile && ltype.isVolatile && !rtype.isVolatile)
                                    reportTypeError(fexpr, "Cannot convert from '%s' to '%s' with '%s'; undefined behavior".format(rtype.toText, ltype.toText, op), ae, Severity.SecurityWarning, "volatile")
                                if (opts.warning_const_assignment && ltype.isConstant)
                                    reportTypeError(fexpr, "Cannot assign to const '%s'; undefined behavior".format(ltype.toText), ae, Severity.SecurityWarning, "const_assignment")

                                val opType = operationType(op, ltype, rtype, ae, fexpr, env)

                                if (ltype.isConstant && isArithmetic(ltype) && isScalar(opType))
                                    reportTypeError(fexpr, "assignment of read-only variable", ae)

                                ltype match {
                                    case CType(t, true, _, _) if coerce(ltype, opType).isDefined =>
                                        if (opts.warning_implicit_coercion && isForcedCoercion(ltype.atype, rtype.atype))
                                            reportTypeError(fexpr, "Implicit coercion of integer types (%s <- %s), consider a cast".format(ltype.toText, rtype.toText), ae, Severity.SecurityWarning, "implicit_coercion")
                                        if (opts.warning_character_signed && isCharSignCoercion(ltype.atype, rtype.atype))
                                            reportTypeError(fexpr, "Incompatible character types '%s <- %s'; consider a cast".format(ltype.toText, rtype.toText), expr, Severity.SecurityWarning, "char_signness")
                                        val warning = coerce(ltype, opType).get
                                        if (warning.nonEmpty)
                                            reportTypeError(fexpr, warning + " (" + ltype + " " + op + " " + rtype + ")", ae, severity = Severity.Warning)

                                        prepareArray(ltype).toValue
                                    case CType(u: CUnknown, _, _, _) => ltype.toValue
                                    case CObj(i@CIgnore()) => ltype.toValue
                                    case e => reportTypeError(fexpr, "incorrect assignment with " + e + " " + op + " " + rtype, ae)
                                }
                            })
                    //a++, a--
                    case pe@PostfixExpr(expr, SimplePostfixSuffix(_)) =>
                        //check for integer overflow
                        if (opts.warning_potential_integer_overflow && env.isSecurityRelevantLocation)
                            issueTypeError(Severity.SecurityWarning, featureExpr, "Potential integer overflow in security relevant context (%s)".format(env.securityRelevantLocation.get), pe, "potential_integer_overflow")

                        et(expr) map {
                            prepareArray
                        } vmap(featureExpr, {
                            case (f, CObj(t)) if (isScalar(t)) => t //apparently ++ also works on arrays
                            case (f, CObj(CIgnore())) => CIgnore()
                            //TODO check?: not on function references
                            case (f, e) => reportTypeError(f, "wrong type argument to increment " + e, pe)
                        })
                    //a+b
                    case ne@NAryExpr(expr, opList) =>
                        ConditionalLib.vfoldLeft(opList, et(expr), featureExpr,
                            (fexpr: FeatureExpr, ctype: CType, subExpr: NArySubExpr) => {
                                //security check for integer overflows when operand is used in pointer arithmetic (ie. also array access)
                                val isPointerArith = (pointerArthOp(subExpr.op) || pointerArthAssignOp(subExpr.op)) && isPointer(ctype)
                                val subExprType = etF(subExpr.e, fexpr, if (isPointerArith) env.markSecurityRelevant("array access/pointer arithmetic") else env)

                                subExprType vmap(fexpr, (fexpr, subExprType) => operationType(subExpr.op, ctype, subExprType, ne, fexpr, env))
                            }
                        )
                    //a[e]
                    case p@PostfixExpr(expr, ArrayAccess(idx)) =>
                        //syntactic sugar for *(a+i)
                        val newExpr = PointerDerefExpr(createSum(expr, idx)).setPositionRange(p)
                        et(newExpr)
                    //"a"
                    case StringLit(v) => One(CType(CPointer(CSignUnspecified(CChar())),true,false,false))
                    //++a, --a
                    case p@UnaryExpr(_, expr) =>
                        val newExpr = AssignExpr(expr, "+=", Constant("1").setPositionRange(p)).setPositionRange(p)
                        et(newExpr)
                    //sizeof()
                    case SizeOfExprT(x) =>
                        /*x match {
                            case TypeName(lst, decl) =>
                                checkTypeSpecifiers(lst, featureExpr, env)
                            case _ =>
                        }*/
                        sizeofType(env, x, featureExpr)
                    case SizeOfExprU(x) =>
                        sizeofType(env, x, featureExpr)
                    case ue@UnaryOpExpr(kind, expr) =>
                        if (kind == "&&")
                        //label deref, TODO check that label is actually declared
                            One(CPointer(CVoid())) //label dereference
                        else {
                            val exprType = et(expr).map(_.toValue)
                            kind match {
                                //TODO complete list: __real__ __imag__
                                //TODO promotions
                                case "+" => exprType.vmap(featureExpr,
                                    (fexpr, x) => if (isArithmetic(x) || x.isIgnore) promote(x) else reportTypeError(fexpr, "incorrect type, expected arithmetic, was " + x, ue))
                                case "-" =>
                                    //check for integer overflow (+,~,! do not overflow)
                                    if (opts.warning_potential_integer_overflow && env.isSecurityRelevantLocation)
                                        issueTypeError(Severity.SecurityWarning, featureExpr, "Potential integer overflow in security relevant context (%s)".format(env.securityRelevantLocation.get), ue, "potential_integer_overflow")

                                    exprType.vmap(featureExpr,
                                        (fexpr, x) => if (isArithmetic(x) || x.isIgnore) promote(x) else reportTypeError(fexpr, "incorrect type, expected arithmetic, was " + x, ue))
                                case "~" => exprType.vmap(featureExpr,
                                    (fexpr, x) => if (isIntegral(x) || x.isIgnore) CSigned(CInt()) else reportTypeError(fexpr, "incorrect type, expected integer, was " + x, ue))
                                case "!" => exprType.vmap(featureExpr,
                                    (fexpr, x) => if (isScalar(x) || x.isIgnore) CSigned(CInt()) else reportTypeError(fexpr, "incorrect type, expected scalar, was " + x, ue))
                                case "__real__" | "__imag__" => One(CIgnore().toCType.toObj)
                                case _ => One(reportTypeError(featureExpr, "unknown unary operator " + kind + " (TODO)", ue))
                            }
                        }
                    //x?y:z  (gnuc: x?:z === x?x:z)
                    case ce@ConditionalExpr(condition, thenExpr, elseExpr) =>
                        //dead code detection, see CTypeSystem..IfStatement
                        val (contr, taut) = analyzeExprBounds(One(condition), featureExpr, env)

                        et(condition) vflatMap(featureExpr, {
                            (fexpr, conditionType) =>
                                if (isScalar(conditionType))
                                    getConditionalExprType(etF(thenExpr.getOrElse(condition), fexpr, env.markDead(contr)), etF(elseExpr, fexpr, env.markDead(taut)), fexpr, ce)
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
                        val t: Conditional[CType] = lastType.flatMap({
                            case None => One(CVoid().toCType) //TODO what is the type of an empty ExprList?
                            case Some(ctype) => ctype
                        }) simplify (featureExpr);

                        //return original environment, definitions don't leave this scope
                        t

                    case LcurlyInitializer(inits) => One(CCompound().toCType.toObj) //TODO more specific checks, currently just use CCompound which can be cast into any structure or array
                    case a: GnuAsmExpr =>
                        One(CIgnore()) //don't care about asm now
                    case BuiltinOffsetof(typename, offsetDesignators) =>
                        // no type checking, but tracking of def-use relationship as far as reasonably possible
                        addTypenameUse(typename, offsetDesignators, env)
                        One(CSigned(CInt()))
                    case c: BuiltinTypesCompatible => One(CSigned(CInt())) //http://www.delorie.com/gnu/docs/gcc/gcc_81.html
                    case b@BuiltinVaArgs(expr, typename) =>
                        //check expr is of type va_list
                        et(expr) vflatMap(featureExpr, {
                            (fexpr, exprType) => if (exprType.atype != CBuiltinVaList())
                                One(reportTypeError(fexpr, "invalid type of condition: " + exprType, b))
                            else One("all okay")
                        })
                        //return whatever type declared without further check
                        getTypenameType(typename, featureExpr, env)
                    case AlignOfExprT(typename) => getTypenameType(typename, featureExpr, env); One(CSigned(CInt()))
                    case AlignOfExprU(expr) => et(expr); One(CSigned(CInt()))

                    //TODO initializers 6.5.2.5
                    case e => One(reportTypeError(featureExpr, "unknown expression " + e + " (TODO)", e))
                }

        typedExpr(expr, resultType, featureExpr, env)
        addEnv(expr, env)
        if (!recurse) {
            addUse(expr, featureExpr, env)
        }
        resultType.simplify(featureExpr)
    }

    private[typesystem] def analyzeExprBounds(expr: Conditional[Expr], context: FeatureExpr, env: Env): (FeatureExpr, FeatureExpr)

    private def getConditionalExprType(thenTypes: Conditional[CType], elseTypes: Conditional[CType], featureExpr: FeatureExpr, where: AST): Conditional[CType] =
        ConditionalLib.vmapCombinationOp(thenTypes, elseTypes, featureExpr, (featureExpr: FeatureExpr, thenType: CType, elseType: CType) => {
            (thenType.atype, elseType.atype) match {
                case (CPointer(CVoid()), CPointer(x)) => CPointer(x) //spec
                case (CPointer(x), CPointer(CVoid())) => CPointer(x) //spec
                case (t1, t2) if (coerce(t1, t2).isDefined) => converse(t1, t2) //spec
                case (t1, t2) if ((isArithmetic(t1) && t2 == CVoid()) || (isArithmetic(t2) && t1 == CVoid())) => CVoid() //tested from gcc
                case (t1, t2) => reportTypeError(featureExpr, "different address spaces in conditional expression: " + t1 + " and " + t2, where)
            }
        })


    private def pointerArthOp(o: String) = Set("+", "-") contains o
    private def pointerArthAssignOp(o: String) = Set("+=", "-=") contains o

    //see https://www.securecoding.cert.org/confluence/display/seccode/INT32-C.+Ensure+that+operations+on+signed+integers+do+not+result+in+overflow
    private def potentiallyOverflowingOp(o: String) = Set("+", "-", "*", "/", "%", "++", "--", "+=", "-=", "/=", "%=", "<<=", "<<") contains o

    /**
     * defines types of various operations
     * TODO currently incomplete and possibly incorrect
     *
     * visible only for test cases
     */
    private[typesystem] def operationType(op: String, type1: CType, type2: CType, where: AST, featureExpr: FeatureExpr, env: Env): CType = {
        def assignOp(o: String) = Set("+=", "/=", "-=", "*=", "%=", "<<=", ">>=", "&=", "|=", "^=") contains o
        def compOp(o: String) = Set("==", "!=", "<", ">", "<=", ">=") contains o
        def logicalOp(o: String) = Set("&&", "||") contains o
        def bitwiseOp(o: String) = Set("&", "|", "^", "~") contains o
        def shiftOp(o: String) = Set("<<", ">>") contains o

        //check integer overflow in security relevant contexts
        if (opts.warning_potential_integer_overflow && env.isSecurityRelevantLocation && potentiallyOverflowingOp(op) && !(isPointer(normalize(type1))))
            issueTypeError(Severity.SecurityWarning, featureExpr, "Potential integer overflow in security relevant context (%s)".format(env.securityRelevantLocation.get), where, "potential_integer_overflow")

        (op, normalize(type1), normalize(type2)) match {
            //pointer arithmetic
            case (_, i, _) if i.isIgnore => CIgnore()
            case (_, _, i) if i.isIgnore => CIgnore()
            case (o, t1, t2) if (pointerArthOp(o) && isArithmetic(t1) && isArithmetic(t2) && coerce(t1, t2)==Some("")) => converse(t1, t2) //spec
            case (o, t1, t2) if (pointerArthOp(o) && isPointer(t1) && isIntegral(t2)) => type1.toValue //spec
            case ("+", t1, t2) if (isIntegral(t1) && isPointer(t2)) => type2.toValue //spec
            case ("-", t1, t2) if (isPointer(t1) && (t1.atype == t2.atype)) => CSigned(CInt()) //spec
            case (o, t1, t2) if ((Set("+=", "-=") contains o) && type1.isObject && isPointer(t1) && isIntegral(t2)) => type1.toValue //spec
            case ("+=", t1, t2) if (type1.isObject && isIntegral(t1) && isPointer(t2)) => type2.toValue //spec
            case ("-=", t1, t2) if (type1.isObject && isPointer(t1) && (t1.atype == t2.atype)) => CSigned(CInt()) //spec
            //bitwise operations defined on isIntegral
            case (op, t1, t2) if (bitwiseOp(op) && isIntegral(t1) && isIntegral(t2)) => converse(t1, t2)
            case (op, t1, t2) if (shiftOp(op) && isIntegral(t1) && isIntegral(t2)) => promote(t1) //spec

            //comparisons
            case (op, t1, t2) if (compOp(op) && isArithmetic(t1) && isArithmetic(t2)) => CSigned(CInt()) //spec
            case (op, t1, t2) if (compOp(op) && isPointer(t1) && isPointer(t2) && coerce(t1, t2)==Some("")) => CSigned(CInt()) //spec


            case ("*", t1, t2) if (isArithmetic(t1) && isArithmetic(t2) && coerce(t1, t2)==Some("")) => converse(t1, t2)
            case ("/", t1, t2) if (isArithmetic(t1) && isArithmetic(t2) && coerce(t1, t2)==Some("")) => converse(t1, t2)
            case ("%", t1, t2) if (isIntegral(t1) && isIntegral(t2) && coerce(t1, t2)==Some("")) => converse(t1, t2)
            case ("=", t1, t2) if (type1.isObject) => type2.toValue //TODO spec says return t1?
            case (o, t1, t2) if (logicalOp(o) && isScalar(t1) && isScalar(t2)) => CSigned(CInt()) //spec
            case (o, t1, t2) if (assignOp(o) && type1.isObject && coerce(t1, t2)==Some("")) => type2.toValue //TODO spec says return t1?
            case (o, t1, t2) if (pointerArthAssignOp(o) && type1.isObject && isPointer(t1) && isIntegral(t2)) => type1.toValue

            //assigning incompatible pointer types to each other is a warning
            case (op, t1, t2) if (compOp(op) && isPointer(t1) && isPointer(t2) && !(coerce(t1, t2)==Some(""))) => CSigned(CInt()) //spec
                reportTypeError(featureExpr, "incompatible pointer types " + type1 + " " + op + " " + type2, where, severity = Severity.Warning)
                CSigned(CInt())
            case (o, t1, t2) =>
                if (t1.isUnknown || t2.isUnknown)
                    CUnknown(t1 + " " + op + " " + t2)
                else
                    reportTypeError(featureExpr, "unknown operation or incompatible types " + type1 + " " + op + " " + type2, where)
        }
    }

    private def prepareArray(t: CType): CType =
        if (t.isObject)
            t.map(_ match {
                case CArray(x, _) => CPointer(x)
                case x => x
            })
        else t


    /**
     * returns the wider of two types for automatic widening
     */
    def wider(t1: CType, t2: CType) =
        if (t1 < t2) t2 else t1


    private def createSum(a: Expr, b: Expr) =
        NAryExpr(a, List(Opt(FeatureExprFactory.True, NArySubExpr("+", b).setPositionRange(b)))).setPositionRange(a.getPositionFrom, b.getPositionTo)


    private def typeFunctionCall(expr: AST, parameterTypes: Seq[CType], retType: CType, _foundTypes: List[CType],
                                 funCall: PostfixExpr, featureExpr: FeatureExpr, env: Env): CType = {
        //probably just checked on declaration: (??)
        //        checkStructs(retType, featureExpr, env, expr)
        //        parameterTypes.map(checkStructs(_, featureExpr, env, expr))

        var expectedTypes = parameterTypes
        var foundTypes = _foundTypes
        //variadic macros
        if (expectedTypes.lastOption == Some(CVarArgs().toCType)) {
            expectedTypes = expectedTypes.dropRight(1)
            foundTypes = foundTypes.take(expectedTypes.size)
        }
        else if (expectedTypes.lastOption == Some(CVoid().toCType))
            expectedTypes = expectedTypes.dropRight(1)

        //check parameter size and types
        if (expectedTypes.size != foundTypes.size)
            reportTypeError(featureExpr, "parameter number mismatch: expected " + expectedTypes.size + " parameter but found " + foundTypes.size + " in " + expr + " (expected types: " + parameterTypes + ")", funCall)
        else
        if (areParameterCompatible(foundTypes, expectedTypes)) {
            if (opts.warning_const_assignment)
                (foundTypes zip expectedTypes) map {
                    case (ft, et) =>
                        if (ft.isConstant && !et.isConstant)
                            reportTypeError(featureExpr, "Do not (implicitly) cast away a const qualification '%s <- %s'; may result in undefined behavior".format(et.toText, ft.toText), expr, Severity.SecurityWarning, "const-implicit-cast")
                        if (opts.warning_character_signed && isCharSignCoercion(et.atype, ft.atype))
                            reportTypeError(featureExpr, "Incompatible character types '%s <- %s'; consider a cast".format(et.toText, ft.toText), expr, Severity.SecurityWarning, "char_signness")
                }

            retType
        } else {
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
            case (ft, et) => coerce(et, ft)==Some("") || ft.isUnknown
        }


    private def structEnvLookup(strEnv: StructEnv, structName: String, isUnion: Boolean, fieldName: String, astNode: Expr, featureExpr: FeatureExpr): Conditional[CType] = {
        //sanity check, should not occur by construction
        //completeness is checked when declaring the variable or when dereferencing a pointer
        val whenComplete = strEnv.isComplete(structName, isUnion)
        if ((featureExpr andNot whenComplete).isSatisfiable())
            issueTypeError(Severity.FieldLookupError, featureExpr andNot whenComplete, "member access to incomplete or unknown structure/union " + structName, astNode)

        val struct: ConditionalTypeMap = strEnv.getFieldsMerged(structName, isUnion)
        val ctype = struct.getOrElse(fieldName, CUnknown("member " + fieldName + " unknown in " + structName))

        ctype.vmap(featureExpr, {
            (f, t) => if (t.isUnknown && f.isSatisfiable()) issueTypeError(Severity.FieldLookupError, f, "member " + fieldName + " unknown in " + structName, astNode)
        })

        ctype
    }


    //implemented by CTypeSystem
    def getStmtType(stmt: Statement, featureExpr: FeatureExpr, env: Env): (Conditional[CType], Env)

    /**
     * sizeof() has type Any->size_t. Type size_t is defined in individual header files (e.g. stddef.h) of the system though
     * and may not be defined in all cases. here we look up the type of size_t and return an int in case it fails
     */
    def sizeofType(env: Env, x: AST, featureExpr: FeatureExpr): Conditional[CType] = {
        x match {
            case p@PostfixExpr(expr, PointerPostfixSuffix(_, i@Id(id))) =>
                addStructUsageFromSizeOfExprU(p, featureExpr, env)
            case p@PostfixExpr(i: Id, _) =>
                addUse(i, featureExpr, env)
            case pd@PointerDerefExpr(i: Id) =>
                // TODO: isUnion is set to true
                addUse(i, featureExpr, env)
            case pd@PointerDerefExpr(NAryExpr(p, expr)) => addStructUsageFromSizeOfExprU(p, featureExpr, env)
            case pd@PointerDerefExpr(c: CastExpr) =>
                getExprType(c, featureExpr, env)
            case pe@PostfixExpr(p: PostfixExpr, _) => addStructUsageFromSizeOfExprU(p, featureExpr, env)
            case tn@TypeName(lst: List[Opt[Specifier]], decl) =>
                getTypenameType(tn, featureExpr, env)

            case _ => // println("missed " + x)
        }
        env.typedefEnv.getOrElse("size_t", CUnsigned(CInt()))
    }

    private def addStructUsageFromSizeOfExprU(a: AST, featureExpr: FeatureExpr, env: Env) = {
        val et = getExprTypeRec(_: Expr, featureExpr, env, true)
        a match {
            case p@PostfixExpr(expr, PointerPostfixSuffix(_, i@Id(id))) =>
                et(expr).map(_.atype).vflatMap(featureExpr, {
                    case (f, CAnonymousStruct(_, fields, _)) =>
                        addAnonStructUse(i, fields)
                        null
                    case (f, CStruct(s, isUnion)) =>
                        addStructUse(i, featureExpr, env, s, isUnion)
                        null
                    case (f, CPointer(CStruct(name, isUnion))) =>
                        addStructUse(i, featureExpr, env, name, isUnion)
                        null
                    case (f, e) =>
                        null
                })
            case pde@PointerDerefExpr(expr) =>
                print("")
            case _ =>
        }
    }


    /**
     * helper function for CDeclUse
     */
    def addTypenameUse(typename: TypeName, offsetDesignators: List[Opt[OffsetofMemberDesignator]], env: Env) =
        typename.specifiers.foreach(x => {
            x match {
                case Opt(ft, TypeDefTypeSpecifier(name)) =>
                    addTypeUse(name, env, ft)
                case Opt(ft, StructOrUnionSpecifier(isUnion, Some(i: Id), _, _, _)) =>
                    offsetDesignators.foreach(x => x match {
                        case Opt(ft, OffsetofMemberDesignatorID(offsetId: Id)) =>
                            addStructUse(offsetId, ft, env, i.name, isUnion)
                        case _ =>
                    })
                    addStructDeclUse(i, env, isUnion, ft)
                case _ =>
            }
        })


    /**
     * hardcoding of functions for which parameters are considered security relevant
     * parameters of these functions are checked for integer overflows
     */
    def markSecurityRelevantFunctions(funname: String, ctype: Conditional[CType]): Conditional[CType] = {
        val functions = Set("malloc", "calloc", "realloc")

        if (functions contains funname)
            ctype.map(_ map {
                case c: CFunction => c.markSecurityRelevant
                case t => t
            })
        else ctype

    }

    val HexNull = "0x0+".r
    private def isHexNull(s:String) = s match {
        case HexNull() => true
        case _ => false
    }



}