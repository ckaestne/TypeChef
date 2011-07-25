package de.fosd.typechef.typesystem


import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr.FeatureExpr
import org.kiama.attribution.Attribution._
import org.kiama._
import de.fosd.typechef.conditional._
import ConditionalLib._

/**
 * parsing types from declarations (top level declarations, parameters, etc)
 *
 * handling of typedef synonyms
 */
trait CDeclTyping extends CTypes with ASTNavigation with FeatureExprLookup {

    def ctype(fun: FunctionDef) = fun -> funType
    def ctype(fun: TypeName) = fun -> typenameType
    def ctype(exp: Expr): Conditional[CType]
    //provided by CExprTyping
    def ctype(expr: Expr, context: AST): Conditional[CType]

    val funType: FunctionDef ==> Conditional[CType] = attr {
        case fun =>
            if (!fun.oldStyleParameters.isEmpty) One(CUnknown("alternative parameter notation not supported yet"))
            else if (isTypedef(fun.specifiers)) One(CUnknown("Invalid typedef specificer for function definition (?)"))
            else declType(fun.specifiers, fun.declarator)
    }
    val typenameType: TypeName ==> Conditional[CType] = attr {
        case TypeName(specs, None) => constructType(specs)
        case TypeName(specs, Some(decl)) => getAbstractDeclaratorType(decl, constructType(specs))
    }


    /**
     * filtering is a workaround for a parsing problem (see open test) that can produce
     * dead AST-subtrees in some combinations.
     *
     * remove when problem is fixed
     */
    protected def filterDeadSpecifiers[T](l: List[Opt[T]], ctx: FeatureExpr): List[Opt[T]] =
        l.filter(o => ((o.feature) and ctx).isSatisfiable)


    def constructType(specifiers: List[Opt[Specifier]]): Conditional[CType] = {
        //unwrap variability
        val exploded: Conditional[List[Specifier]] = explodeOptList(specifiers)
        Conditional.combine(exploded.map(constructTypeOne)) simplify
    }


    def constructTypeOne(specifiers: List[Specifier]): Conditional[CType] = {
        //type specifiers
        var types = List[Conditional[CType]]()
        for (specifier <- specifiers) specifier match {
            case StructOrUnionSpecifier(isUnion, Some(id), _) => types = types :+ One(CStruct(id.name, isUnion))
            case StructOrUnionSpecifier(isUnion, None, members) => types = types :+ One(CAnonymousStruct(parseStructMembers(members), isUnion))
            case e@TypeDefTypeSpecifier(Id(typedefname)) => {
                val typedefEnvironment = e -> previousTypedefEnv
                if (typedefEnvironment contains typedefname) types = types :+ typedefEnvironment(typedefname)
                else types = types :+ One(CUnknown("type not defined " + typedefname)) //should not occur, because the parser should reject this already
            }
            case EnumSpecifier(_, _) => types = types :+ One(CSigned(CInt())) //TODO check that enum name is actually defined (not urgent, there is not much checking possible for enums anyway)
            case TypeOfSpecifierT(typename) => types = types :+ ctype(typename)
            case TypeOfSpecifierU(expr) =>
                val outer = findOutermostDeclaration(expr)

                types = types :+ (if (outer == null) ctype(expr) else ctype(expr, outer)) //use context outside declaration to avoid recursion
            case _ =>
        }



        def count(spec: Specifier): Int = specifiers.count(_ == spec)
        def has(spec: Specifier): Boolean = count(spec) > 0

        val isSigned = has(SignedSpecifier())
        val isUnsigned = has(UnsignedSpecifier())
        if (isSigned && isUnsigned)
            return One(CUnknown("type both signed and unsigned"))

        //for int, short, etc, always assume signed by default
        def sign(t: CBasicType): One[CType] = One(if (isUnsigned) CUnsigned(t) else CSigned(t))
        //for characters care about SignUnspecified
        def signC(t: CBasicType): One[CType] = One(if (isSigned) CSigned(t) else if (isUnsigned) CUnsigned(t) else CSignUnspecified(t))

        if (has(CharSpecifier()))
            types = types :+ signC(CChar())
        if (count(LongSpecifier()) == 2)
            types = types :+ sign(CLongLong())
        if (count(LongSpecifier()) == 1 && !has(DoubleSpecifier()))
            types = types :+ sign(CLong())
        if (has(ShortSpecifier()))
            types = types :+ sign(CShort())
        if (has(DoubleSpecifier()) && has(LongSpecifier()))
            types = types :+ One(CLongDouble())
        if (has(DoubleSpecifier()) && !has(LongSpecifier()))
            types = types :+ One(CDouble())
        if (has(FloatSpecifier()))
            types = types :+ One(CFloat())
        if ((isSigned || isUnsigned || has(IntSpecifier()) || has(OtherPrimitiveTypeSpecifier("_Bool"))) && !has(ShortSpecifier()) && !has(LongSpecifier()) && !has(CharSpecifier()))
            types = types :+ sign(CInt())

        if (has(VoidSpecifier()))
            types = types :+ One(CVoid())

        //TODO prevent invalid combinations completely?


        if (types.size == 1)
            types.head
        else if (types.size == 0)
            One(CUnknown("no type specfier found"))
        else
            One(CUnknown("multiple types found " + types))
        //
        //                      | textToken("char")
        //            | textToken("short")
        //            | textToken("int")
        //            | textToken("long")
        //            | textToken("float")
        //            | textToken("double")
        //            | signed
        //            | textToken("unsigned")
        //            | textToken("_Bool")
        //            | textToken("_Complex")
        //            | textToken("__complex__")) ^^ {(t: Elem) => PrimitiveTypeSpecifier(t.getText)})
        //            | structOrUnionSpecifier
        //            | enumSpecifier
        //            //TypeDefName handled elsewhere!
        //            | (typeof ~ LPAREN ~> ((typeName ^^ {TypeOfSpecifierT(_)})
        //            | (expr ^^ {TypeOfSpecifierU(_)})) <~ RPAREN))

    }

    def declType(decl: Declaration): List[(String, FeatureExpr, Conditional[CType])] = enumDeclarations(decl.declSpecs) ++ {
        if (isTypedef(decl.declSpecs)) List() //no declaration for a typedef
        else {
            val returnType: Conditional[CType] = constructType(filterDeadSpecifiers(decl.declSpecs, decl -> featureExpr))

            for (Opt(f, init) <- decl.init)
            yield (init.declarator.getName, init -> featureExpr, getDeclaratorType(init.declarator, returnType))
        }
    }

    /**define all fields from enum type specifiers as int values */
    private def enumDeclarations(specs: List[Opt[Specifier]]): List[(String, FeatureExpr, Conditional[CType])] = {
        var result = List[(String, FeatureExpr, Conditional[CType])]()
        //TODO variability
        for (Opt(_, spec) <- specs) spec match {
            case EnumSpecifier(_, Some(enums)) => for (Opt(_, enum) <- enums)
                result = (enum.id.name, enum -> featureExpr, One(CSigned(CInt()))) :: result
            case _ =>
        }
        result
    }


    def declType(specs: List[Opt[Specifier]], decl: Declarator): Conditional[CType] =
        getDeclaratorType(decl, constructType(specs))

    def isTypedef(specs: List[Opt[Specifier]]) = specs.map(_.entry).contains(TypedefSpecifier())

    protected def getDeclaratorType(decl: Declarator, returnType: Conditional[CType]): Conditional[CType] = {
        val rtype = decorateDeclaratorExt(decorateDeclaratorPointer(returnType, decl.pointers), decl.extensions)

        //this is an absurd order but seems to be as specified
        //cf. http://www.ericgiguere.com/articles/reading-c-declarations.html
        decl match {
            case AtomicNamedDeclarator(ptrList, name, e) => rtype
            case NestedNamedDeclarator(ptrList, innerDecl, e) => getDeclaratorType(innerDecl, rtype)
        }
    }

    private def getAbstractDeclaratorType(decl: AbstractDeclarator, returnType: Conditional[CType]): Conditional[CType] = {
        val rtype = decorateDeclaratorExt(decorateDeclaratorPointer(returnType, decl.pointers), decl.extensions)

        //this is an absurd order but seems to be as specified
        //cf. http://www.ericgiguere.com/articles/reading-c-declarations.html
        decl match {
            case AtomicAbstractDeclarator(ptrList, e) => rtype
            case NestedAbstractDeclarator(ptrList, innerDecl, e) => getAbstractDeclaratorType(innerDecl, rtype)
        }
    }

    private def decorateDeclaratorExt(t: Conditional[CType], extensions: List[Opt[DeclaratorExtension]]): Conditional[CType] =
        conditionalFoldRightR(extensions.reverse, t,
            (ext: DeclaratorExtension, rtype: CType) => ext match {
                case DeclIdentifierList(idList) => One(if (idList.isEmpty) CFunction(List(), rtype) else CUnknown("cannot derive type of function in this style yet"))
                case DeclParameterDeclList(parameterDecls) =>
                    var paramLists: Conditional[List[CType]] =
                        ConditionalLib.explodeOptList(getParameterTypes(parameterDecls))
                    paramLists.map(CFunction(_, rtype))
                case DeclArrayAccess(expr) => One(CArray(rtype))
            }
        )

    private def decorateDeclaratorPointer(t: Conditional[CType], pointers: List[Opt[Pointer]]): Conditional[CType] =
        ConditionalLib.conditionalFoldRight(pointers, t, (a: Pointer, b: CType) => CPointer(b))


    private def getParameterTypes(parameterDecls: List[Opt[ParameterDeclaration]]): List[Opt[CType]] = {
        val r: List[Opt[Conditional[CType]]] = for (Opt(f, param) <- parameterDecls) yield param match {
            case PlainParameterDeclaration(specifiers) => Opt(f, constructType(specifiers))
            case ParameterDeclarationD(specifiers, decl) => Opt(f, getDeclaratorType(decl, constructType(specifiers)))
            case ParameterDeclarationAD(specifiers, decl) => Opt(f, getAbstractDeclaratorType(decl, constructType(specifiers)))
            case VarArgs() => Opt(f, One(CVarArgs()))
        }
        Conditional.flatten(r)
    }

    def parseStructMembers(members: List[Opt[StructDeclaration]]): ConditionalTypeMap = {
        var result = new ConditionalTypeMap()
        for (Opt(f, structDeclaration) <- members) {
            for (Opt(g, structDeclarator) <- structDeclaration.declaratorList)
                structDeclarator match {
                    case StructDeclarator(decl, _, _) => result = result + (decl.getName, f and g, declType(structDeclaration.qualifierList, decl))
                    case StructInitializer(expr, _) => //TODO check: ignored for now, does not have a name, seems not addressable. occurs for example in struct timex in async.i test
                }
            //for unnamed fields, if they are struct or union inline their fields
            //cf. http://gcc.gnu.org/onlinedocs/gcc/Unnamed-Fields.html#Unnamed-Fields
            if (structDeclaration.declaratorList.isEmpty) constructType(structDeclaration.qualifierList) match {
            //TODO correctly deal with variability. "One" in the next line is a hack for the common case
                case One(CAnonymousStruct(fields, _)) => result = result ++ fields
                //                case CStruct(name, _) => //TODO inline as well
                case _ => //don't care about other types
            }
        }
        result
    }


    /*************
     * Typedef environment (all defined type synonyms up to here)
     */
    //Type synonyms with typedef
    type TypeDefEnv = Map[String, Conditional[CType]]

    /**typedef enviroment, outside the current declaration*/
    val previousTypedefEnv: AST ==> TypeDefEnv = {
        case ast: AST =>
            if (!(ast -> inDeclaration)) ast -> typedefEnv
            else (ast -> outerDeclaration -> prevOrParentAST -> typedefEnv)
    }


    protected def findOutermostDeclaration(a: AST): AST = findOutermostDeclaration(a, null)
    protected def findOutermostDeclaration(a: AST, last: AST): AST = a match {
        case decl: Declaration => findOutermostDeclaration(decl -> parentAST, decl)
        case decl: DeclarationStatement => findOutermostDeclaration(decl -> parentAST, decl)
        case a: AST => findOutermostDeclaration(a -> parentAST, last)
        case null => last
    }

    val typedefEnv: AST ==> TypeDefEnv = attr {
        //TODO variability
        case e: Declaration => outerTypedefEnv(e) ++ recognizeTypedefs(e)
        case e: AST => outerTypedefEnv(e)
    }
    private def outerTypedefEnv(e: AST): TypeDefEnv =
        outer[TypeDefEnv](typedefEnv, () => Map(), e)

    private def recognizeTypedefs(decl: Declaration): TypeDefEnv = {
        if (isTypedef(decl.declSpecs))
            (for (Opt(f, init) <- decl.init) yield (init.getName -> declType(decl.declSpecs, init.declarator))) toMap
        else Map()
    }

    private val inDeclaration: AST ==> Boolean = attr {
        case e: Declaration => true
        case e: AST => if (e -> parentAST == null) false else e -> parentAST -> inDeclaration
    }
    //get the first parent node that is a declaration
    private val outerDeclaration: AST ==> Declaration = attr {
        case e: Declaration => e
        case e: AST => if ((e -> parentAST) == null) null else e -> parentAST -> outerDeclaration
    }

}