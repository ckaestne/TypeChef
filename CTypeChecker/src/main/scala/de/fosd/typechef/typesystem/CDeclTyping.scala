package de.fosd.typechef.typesystem


import de.fosd.typechef.parser.c._
import de.fosd.typechef.parser.Opt
trait CDeclTyping extends CTypes with CTypeEnv {


    def declType(decl: ADeclaration): CType = {


        val returnType = constructType(decl.declSpecs)



        returnType
    }

    private def declSpecifierContext(declSpecs: List[Opt[Specifier]]) = {
        var isTypedef = false
        for (Opt(_, specifier) <- declSpecs) specifier match {
            case TypedefSpecifier() => isTypedef = true
            case AutoSpecifier() =>
        //            case OtherSpecifier("register") =>
        //            case OtherSpecifier("extern") =>
        //            case OtherSpecifier("static") =>
        //            case OtherSpecifier("inline") =>


        }


    }


}