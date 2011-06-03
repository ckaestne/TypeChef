package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._

@RunWith(classOf[JUnitRunner])
class TypeSystemTest extends FunSuite with ShouldMatchers with ASTNavigation with TestHelper {

    private def check(code: String): Boolean = check(getAST(code))
    private def check(ast: TranslationUnit): Boolean = new CTypeSystem().checkAST(ast)


    test("typecheck simple translation unit") {
        expect(true) {
            check("void foo() {};" +
                    "void bar(){foo();}")
        }
        expect(false) {check("void bar(){foo();}")}
    }
    test("detect redefinitions") {
        expect(false) {check("void foo(){} void foo(){}")}
        expect(false) {
            check("void foo(){} \n" +
                    "#ifdef A\n" +
                    "void foo(){}\n" +
                    "#endif\n")
        }
        expect(true) {
            check("#ifndef A\n" +
                    "void foo(){} \n" +
                    "#endif\n" +
                    "#ifdef A\n" +
                    "void foo(){}\n" +
                    "#endif\n")
        }
    }
    test("typecheck translation unit with features") {
        expect(true) {
            check("void foo(){} \n" +
                    "#ifdef A\n" +
                    "void bar(){foo();}\n" +
                    "#endif\n")
        }
        expect(false) {
            check(
                "#ifdef A\n" +
                        "void foo(){} \n" +
                        "#endif\n" +
                        "void bar(){foo();}\n")
        }
        expect(true) {
            check(
                "#ifdef A\n" +
                        "void foo(){} \n" +
                        "#endif\n" +
                        "#ifndef A\n" +
                        "void foo(){} \n" +
                        "#endif\n" +
                        "void bar(){foo();}\n")
        }

    }
}