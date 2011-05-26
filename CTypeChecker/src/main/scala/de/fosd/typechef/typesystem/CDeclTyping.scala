package de.fosd.typechef.typesystem


import de.fosd.typechef.parser.c._
import de.fosd.typechef.parser._

/**
 * parsing types from declarations (top level declarations, parameters, etc)
 */
trait CDeclTyping extends CTypes with CTypeEnv {


    def declType(decl: ADeclaration): List[(String, CType)] = {


        val returnType = constructType(decl.declSpecs)

        for (init <- decl.init) yield (init.entry.declarator.getName, getDeclaratorType(init.entry.declarator, returnType))
    }


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