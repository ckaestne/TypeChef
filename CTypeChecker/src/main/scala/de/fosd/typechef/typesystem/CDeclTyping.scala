package de.fosd.typechef.typesystem


import de.fosd.typechef.parser.c._
import de.fosd.typechef.parser._

/**
 * parsing types from declarations (top level declarations, parameters, etc)
 */
trait CDeclTyping extends CTypes {


    def constructType(specifiers: List[Opt[Specifier]]): CType = {
        //TODO variability
        //other specifiers for declarations
        //        specifier("auto") | specifier("register") | (textToken("typedef") ^^ {x => TypedefSpecifier()}) | functionStorageClassSpecifier
        //        specifier("extern") | specifier("static") | inline
        //  const | volatile | restrict
        //        attributes

        //type specifiers

        val isSigned: Boolean = specifiers.exists({
            case Opt(_, SignedSpecifier()) => true
            case _ => false
        })
        val isUnsigned: Boolean = specifiers.exists({
            case Opt(_, UnsignedSpecifier()) => true
            case _ => false
        })
        if (isSigned && isUnsigned)
            return CUnknown("type both signed and unsigned")

        def sign(t: CBasicType): CType = if (isSigned) CSigned(t) else if (isUnsigned) CUnsigned(t) else CSignUnspecified(t)
        var types = List[CType]()
        for (Opt(_, specifier) <- specifiers) specifier match {
            case CharSpecifier() => types = types :+ sign(CChar())
            case ShortSpecifier() => types = types :+ sign(CShort())
            case IntSpecifier() => types = types :+ sign(CInt())
            case LongSpecifier() => types = types :+ sign(CLong())
            case FloatSpecifier() => types = types :+ CFloat()
            case DoubleSpecifier() => types = types :+ CDouble()
            case VoidSpecifier() => types = types :+ CVoid()
            case e: OtherSpecifier =>
            case e: TypeSpecifier => types = types :+ CUnknown("unknown type specifier " + e)
        }
        if (types.contains(CDouble()) && types.contains(CSigned(CLong())))
            types = CLongDouble() +: types.-(CDouble()).-(CSigned(CLong()))

        if (types.size == 1)
            types.head
        else if (types.size == 0)
            CUnknown("no type specfier found")
        else
            CUnknown("multiple types found " + types)
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

    def declType(decl: ADeclaration): List[(String, CType)] = {


        val returnType = constructType(decl.declSpecs)

        for (init <- decl.init) yield (init.entry.declarator.getName, getDeclaratorType(init.entry.declarator, returnType))
    }

    def declType(specs: List[Opt[Specifier]], decl: Declarator) =
        getDeclaratorType(decl, constructType(specs))


    private def getDeclaratorType(decl: Declarator, returnType: CType): CType = {
        val rtype = decorateDeclaratorExt(decorateDeclaratorPointer(returnType, decl.pointers), decl.extensions)

        //this is an absurd order but seems to be as specified
        //cf. http://www.ericgiguere.com/articles/reading-c-declarations.html
        decl match {
            case AtomicNamedDeclarator(ptrList, name, e) => rtype
            case NestedNamedDeclarator(ptrList, innerDecl, e) => getDeclaratorType(innerDecl, rtype)
        }
    }

    private def getAbstractDeclaratorType(decl: AbstractDeclarator, returnType: CType): CType = {
        val rtype = decorateDeclaratorExt(decorateDeclaratorPointer(returnType, decl.pointers), decl.extensions)

        //this is an absurd order but seems to be as specified
        //cf. http://www.ericgiguere.com/articles/reading-c-declarations.html
        decl match {
            case AtomicAbstractDeclarator(ptrList, e) => rtype
            case NestedAbstractDeclarator(ptrList, innerDecl, e) => getAbstractDeclaratorType(innerDecl, rtype)
        }
    }

    private def decorateDeclaratorExt(t: CType, extensions: List[Opt[DeclaratorExtension]]): CType = {
        var rtype = t
        for (Opt(_, ext) <- extensions.reverse) rtype = ext match {
            case DeclIdentifierList(idList) => if (idList.isEmpty) CFunction(Seq(), rtype) else CUnknown("cannot derive type of function in this style yet")
            case DeclParameterDeclList(parameterDecls) => CFunction(getParameterTypes(parameterDecls), rtype)
            case DeclArrayAccess(expr) => CArray(rtype)
        }
        rtype
    }
    private def decorateDeclaratorPointer(t: CType, pointers: List[Opt[Pointer]]): CType = pointers.foldRight(t)((a, b) => CPointer(b))


    private def getParameterTypes(parameterDecls: List[Opt[ParameterDeclaration]]) = {
        for (Opt(_, param) <- parameterDecls) yield param match {
            case PlainParameterDeclaration(specifiers) => constructType(specifiers)
            case ParameterDeclarationD(specifiers, decl) => getDeclaratorType(decl, constructType(specifiers))
            case ParameterDeclarationAD(specifiers, decl) => getAbstractDeclaratorType(decl, constructType(specifiers))
        }

    }


}