package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._

@RunWith(classOf[JUnitRunner])
class TypeSignatureTest extends FunSuite with ShouldMatchers with TestHelper {

    private def check(code: String, printAST: Boolean = false): Boolean = {
        println("checking " + code);
        if (printAST) println("AST: " + getAST(code));
        check(getAST(code));
    }

    private def check(ast: TranslationUnit): Boolean = {
        assert(ast != null, "void ast");
        new CTypeSystemFrontend(ast).checkAST()
    }


    test("typdef types") {
        expect(true) {
            check("typedef int a;\n" +
                "void foo(){a b;}")
        }
        expect(false) {
            check("#ifdef X\n" +
                "typedef int a;\n" +
                "#endif\n" +
                "void foo(){a b;}")
        }
    }


    //    test("enum declaration") {
    //        expect(true) {
    //            check("enum s;") //forward declaration
    //        }
    //        expect(false) {
    //            check("enum s x;") //not a forward declaration
    //        }
    //        expect(true) {
    //            check("enum s {a,b};\n" +
    //                "void foo(){enum s x;}")
    //        }
    //        expect(false) {
    //            check("#ifdef X\n" +
    //                "enum s {a, b};\n" +
    //                "#endif\n" +
    //                "void foo(){enum s x;}")
    //        }
    //        expect(false) {
    //            check("#ifdef X\n" +
    //                "enum s {a, b};\n" +
    //                "#endif\n" +
    //                "enum s foo();")
    //        }
    //    }

}