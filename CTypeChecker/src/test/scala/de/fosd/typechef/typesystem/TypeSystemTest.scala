package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._

@RunWith(classOf[JUnitRunner])
class TypeSystemTest extends FunSuite with ShouldMatchers with ASTNavigation with TestHelper {

    private def check(code: String): Boolean = {println("checking " + code); check(getAST(code));}
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
    test("typecheck function calls in translation unit with features") {
        expect(true) {
            check("void foo(){} \n" +
                    "#ifdef A\n" +
                    "void bar(){foo();}\n" +
                    "#endif\n")
        }
        expect(false) {
            check(
                "#ifdef A\n" +
                        "void foo2(){} \n" +
                        "#endif\n" +
                        "void bar(){foo2();}\n")
        }
        expect(true) {
            check(
                "#ifdef A\n" +
                        "void foo3(){} \n" +
                        "#endif\n" +
                        "#ifndef A\n" +
                        "void foo3(){} \n" +
                        "#endif\n" +
                        "void bar(){foo3();}\n")
        }
        expect(true) {
            check(
                "#ifdef A\n" +
                        "int foo4(){} \n" +
                        "#endif\n" +
                        "#ifndef A\n" +
                        "double foo4(){}\n" +
                        "#endif\n" +
                        "void bar(){foo4();}\n")
        }
        expect(true) {
            check("#ifdef A\n" +
                    "void foo(){} \n" +
                    "void bar(){foo();}\n" +
                    "#endif\n")
        }

    }

    test("local variable test") {
        expect(true) {
            check("""
enum {
 false = 0,
 true = 1
};
void *__alloc_percpu()
{
 ({
    static _Bool __warned;
    __warned = true;
 });
 return 1;
}""")
        }
    }

    test("typecheck return statements") {
        expect(true) {
            check("void foo(){ return; }")
        }
        expect(false) {
            check("void foo(){ return 1; }")
        }
        expect(true) {
            check("int foo(){ return 1; }")
        }
        expect(false) {
            check("int * foo(){ return \"abc\"; }")
        }
        expect(true) {
            check("int * foo(){ return 0; }")
        }
        expect(true) {
            check("int * foo(){ return 1; }") //corner case, is a warning in gcc. accepting here for now.
        }
        expect(false) {
            check("int foo(){ return; }")
        }
    }

    test("increment on array") {
        expect(false) {
            check("""
                struct s {} x;
                int foo() { if (x->a) {} }""")
        }

        expect(true) {
            check("""
            void xchdir(const char *path) ;
            int foo(char *argv[]) {
                if (*++argv)
                    xchdir(*argv++);
            }""")
        }
    }
}