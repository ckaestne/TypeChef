package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._

@RunWith(classOf[JUnitRunner])
class TypeSystemTest extends FunSuite with ShouldMatchers with TestHelper {

    private def check(code: String, printAST: Boolean = false): Boolean = {println("checking " + code); if (printAST) println("AST: " + getAST(code)); check(getAST(code));}
    private def check(ast: TranslationUnit): Boolean = {assert(ast!=null,"void ast"); new CTypeSystemFrontend(ast).checkAST}


    test("typecheck simple translation unit") {
        expect(true) {
            check("void foo() {};" +
                    "void bar(){foo();}")
        }
        expect(false) {check("void bar(){foo();}")}
    }
    ignore("detect redefinitions") {
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

    test("illtyped only under unsatisfiable configurations") {
        expect(true) {
            check("""
            #if defined(X)
            void foo() {
            #if !defined(X)
                if (idxx) {};
            #endif
            }
            #endif
            """)
        }
    }

    test("nested functions") {
        expect(true) {
            check("""
                int foo (double a, double b){
                    double square (double z) { return z * z; }
                    return square (a) + square (b);
                }""")
        }
        expect(true) {
            check("""
                 int bar (int *array, int offset, int size)
                     {
                       int access (int *array, int index)
                         { return array[index + offset]; }
                       int i;
                       for (i = 0; i < size; i++)
                            access (array, i);
                     }        """)
        }
    }

    test("local typedef") {
        expect(true) {
            check("""
            void copyt(int n)
            {
                typedef int B[n]; // B is n ints, n evaluated now
                n += 1;
                B a; // ais n ints, n without += 1
                int b[n]; // a and b are different sizes
                int i;
                for (i = 1; i < n; i++)
                      a[i-1] = b[i];
            }
            """)
        }
        expect(true) {
            check("""
            int a() {
            #ifdef X
                typedef int xx;
            #endif
            }
            """)
        }
        expect(true) {
            check("""
            int
            #ifdef A
            a()
            #else
            b()
            #endif
            {
                typedef int xx __attribute__((__may_alias__));
                xx c;
            }
            """, true)
        }


    }

    test("initializer scope") {
        expect(false) {
            check("""
             int x=y;
             """)
        }
        expect(true) {
            check("""
             int x=x;
             """)
        }
    }

    test("local labels") {
        //TODO check label environement
        expect(false) {
            check("""
             void foo() {&&__lab;}
             """)
        }
        expect(true) {
            check("""
             void foo() {__label__ __lab; __lab: &&__lab;}
             """)
        }
    }
}