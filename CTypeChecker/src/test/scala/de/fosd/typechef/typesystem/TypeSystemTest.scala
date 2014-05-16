package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._

@RunWith(classOf[JUnitRunner])
class TypeSystemTest extends FunSuite with ShouldMatchers with TestHelper {

    private def check(code: String, printAST: Boolean = false): Boolean = {
        println("checking " + code);
        if (printAST) println("AST: " + getAST(code));
        check(getAST(code));
    }
    private def check(ast: TranslationUnit): Boolean = {
        assert(ast != null, "void ast");
        new CTypeSystemFrontend(ast).checkAST()
    }

    protected def correct(code: String) = expectResult(true) {check(code)}
    protected def error(code: String) = expectResult(false) {check(code)}

    test("typecheck simple translation unit") {
        expectResult(true) {
            check("void foo() {};" +
                "void bar(){foo();}")
        }
        expectResult(false) {
            check("void bar(){foo();}")
        }
    }
    test("detect redefinitions") {
        expectResult(false) {
            check("void foo(){} void foo(){}")
        }
        expectResult(false) {
            check("void foo(){} \n" +
                "#ifdef A\n" +
                "void foo(){}\n" +
                "#endif\n")
        }
        expectResult(true) {
            check("#ifndef A\n" +
                "void foo(){} \n" +
                "#endif\n" +
                "#ifdef A\n" +
                "void foo(){}\n" +
                "#endif\n")
        }
    }
    test("typecheck function calls in translation unit with features") {
        expectResult(true) {
            check("void foo(){} \n" +
                "#ifdef A\n" +
                "void bar(){foo();}\n" +
                "#endif\n")
        }
        expectResult(false) {
            check(
                "#ifdef A\n" +
                    "void foo2(){} \n" +
                    "#endif\n" +
                    "void bar(){foo2();}\n")
        }
        expectResult(true) {
            check(
                "#ifdef A\n" +
                    "void foo3(){} \n" +
                    "#endif\n" +
                    "#ifndef A\n" +
                    "void foo3(){} \n" +
                    "#endif\n" +
                    "void bar(){foo3();}\n")
        }
        expectResult(true) {
            check(
                "#ifdef A\n" +
                    "int foo4(){} \n" +
                    "#endif\n" +
                    "#ifndef A\n" +
                    "double foo4(){}\n" +
                    "#endif\n" +
                    "void bar(){foo4();}\n")
        }
        expectResult(true) {
            check("#ifdef A\n" +
                "void foo(){} \n" +
                "void bar(){foo();}\n" +
                "#endif\n")
        }

    }

    test("local variable test") {
        expectResult(true) {
            check( """
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
        expectResult(true) {
            check("void foo(){ return; }")
        }
        expectResult(false) {
            check("void foo(){ return 1; }")
        }
        expectResult(true) {
            check("int foo(){ return 1; }")
        }
        expectResult(false) {
            check("int * foo(){ return \"abc\"; }")
        }
        expectResult(true) {
            check("int * foo(){ return 0; }")
        }
        expectResult(false) {
            check("int * foo(){ return 1; }") //warning
        }
        expectResult(false) {
            check("int foo(){ return; }")
        }
    }

    test("increment on array") {
        expectResult(false) {
            check( """
                struct s {} x;
                int foo() { if (x->a) {} }""")
        }

        expectResult(true) {
            check( """
            void xchdir(const char *path) ;
            int foo(char *argv[]) {
                if (*++argv)
                    xchdir(*argv++);
            }""")
        }
    }

    test("illtyped only under unsatisfiable configurations") {
        expectResult(true) {
            check( """
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
        expectResult(true) {
            check( """
                int foo (double a, double b){
                    double square (double z) { return z * z; }
                    return square (a) + square (b);
                }""")
        }
        expectResult(true) {
            check( """
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
        expectResult(true) {
            check( """
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
        expectResult(true) {
            check( """
            int a() {
            #ifdef X
                typedef int xx;
            #endif
            }
                   """)
        }
        expectResult(true) {
            check( """
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
        expectResult(false) {
            check( """
             int x=y;
                   """)
        }
        expectResult(true) {
            check( """
             int x=x;
                   """)
        }
    }

    ignore("check label environement") {
        //TODO check label environement
        expectResult(false) {
            check( """
         void foo() {&&__lab;}
                   """)
        }
        expectResult(false) {
            check( """
         void foo() {goto __lab;}
                   """)
        }
    }

    test("local labels and label deref") {
        expectResult(true) {
            check( """
             void foo() {__label__ __lab; __lab: &&__lab;}
                   """)
        }
    }
    test("recursive function") {
        expectResult(true) {
            check( """
             int foo(int i) {if (i==1) return 0; else return foo(i-1);}
                   """)
        }
    }
    test("alternative parameter declaration") {
        expectResult(true) {
            check( """
             int foo(
             #ifdef X
                int sock
             #else
                void
             #endif
             ) {}
                   """)
        }
    }
    test("parameter checks (warnings)") {
        expectResult(false) {
            check( """
             void foo(int a, int *b, int c) {}
             void bar() {
                int a,b,*c;
                foo(a,b,c);
             }
                   """)
        }
        expectResult(true) {
            check( """
             void foo(int *a) {}
             void bar() {
                foo(0);
             }
                   """)
        }
        expectResult(false) {
            check( """
             void foo(int *a) {}
             void bar() {
                foo(1);
             }
                   """)
        }
    }

    test("function comparison") {
        expectResult(true) {
            check( """
             void foo(int a) ;
             void bar() {
                if (foo==&foo);
             }
                   """)
        }
    }

    test("enum scope") {
        expectResult(true) {
            check( """enum { A, B, C };
                      int x = A;  """)
        }
        expectResult(true) {
            check( """enum { A, B, C } x = A;""")
        }
        expectResult(true) {
            check( """enum { A, B, C } foo() { return 0; }
                      int x=A; """)
        }
        expectResult(true) {
            check( """enum { A, B, C } foo() { return A; }""")
        }
    }

    test("decl scope") {
        expectResult(true) {
            check( """
             struct { int a; } a[2], *b=a;
                   """)
        }

    }

    test("check array initialization") {
        expectResult(true) {
            check( """
                int a=3;
                int b[a];
                   """)
        }
        expectResult(false) {
            check( """
                int b[a];
                   """)
        }
    }

    test("builtin") {
        expectResult(true) {
            check( """
                void foo(){int a[],b[];
                    int x[];
                    x=a;
                    x=b;
                }
                   """)
        }
        expectResult(true) {
            check( """
                        char x[]=__PRETTY_FUNCTION__;
                   """)
        }
        expectResult(true) {
            check( """
                typedef __builtin_va_list __gnuc_va_list;
                typedef __gnuc_va_list va_list;
                void foo(const char *ctl, ...){
                         va_list va;
                         __builtin_va_start(va,ctl);
                         __builtin_va_end(va);
                }""")
        }

    }


    test("multiple conditional structs") {
        expectResult(true) {
            check( """
            #ifdef X
            struct s { char x; };

            #ifdef Y
            struct t { struct s y[3]; };

            void foo() { struct t x; }
            #endif
            #endif
                   """)
        }
        expectResult(false) {
            check( """
            #ifdef Y
            struct s { char x; };
            #endif

            struct t {
                struct s y[3];
            };
            void foo() { struct t x; }
                   """)
        }
        expectResult(true) {
            check( """
            #ifdef Y
            struct s { char x; };
            #endif

            struct t {
            #ifdef Y
                struct s y[3];
            #endif
            };

            void foo() { struct t x; }
                   """)
        }
    }


    test("mostly optional declaration") {
        //the following case is problematic
        //a declaration that is always there, but where all specifiers and initializers have the same condition
        //Opt(true,Declaration(List(Opt(X,...
        expectResult(true) {
            check( """
             #ifdef X
             int a
             #endif
             ;
                   """)
        }

    }


    test("alternative structs") {
        expectResult(true) {
            check( """
            #ifdef X
                struct s { char x; };
            #else
                struct s { long x; };
            #endif

            void m(struct s *a) { a->x; }
                   """)
        }
    }

    test("top level inline assembler") {
        expectResult(true) {
            check( """
                    int a;
                    __asm__("whatever");
                    int b;
                   """)
        }
    }

    test("int pointer compatibility") {
        expectResult(true) {
            check( """
                void foo(){
                    unsigned int a;
                    signed int b;
                    a=b;
                }
                   """)
        }
        expectResult(false) {
            check( """
                void foo(){
                    unsigned int *a;
                    signed int *b;
                    a=&b;
                }
                   """)
        }
        //last two should not yield an error or warning if -Wno-pointer-sign is set (default in linux)
        expectResult(true) {
            check( """
                void foo(){
                    unsigned int *a;
                    signed int *b;
                    a=b;
                }
                   """)
        }
        expectResult(true) {
            check( """
                void foo(){
                    char *a;
                    unsigned char *b;
                    a=b;
                }
                   """)
        }
        expectResult(true) {
            check( """
                         void f(int *x) {}
                         void g() {
                                unsigned int y=3;
                                f(&y);
                        }
                   """)
        }
    }


    test("cast pointer to long") {
        expectResult(true) {
            check( """
                extern void f();
                void foo(){
                    long a;
                    a=(long) f;
                }
                   """)
        }

    }


    test("range expression ") {
        expectResult(true) {
            check( """
                    void foo(){
                    int c;
                      switch (c) {
                                         default:
                                                            break;
                                         case 7 ... 9:
                                                            break;
                        }
                    }
                   """)
        }

    }

    test("pointer arithmetics") {
        //don't ask. pointer-pointer yields an int, + a pointer is a pointer
        expectResult(true) {
            check( """
                    void foo(){
                        char *a, *b, *c, *d;
                        d=a-b+c;
                    }
                   """)
        }

    }

    test("asm statement") {
        expectResult(true) {
            check( """
                         void arch_kgdb_breakpoint(void)
                        {
                                asm("   int $3");
                        }
                   """)
        }
    }

    //TODO ignore for now, forward declarations not yet properly supported
    ignore("typedef and __typeof__ as struct forward declaration") {
        //typeof works like a synonym to a typedef
        //as such also works as forward declaration
        //see http://gcc.gnu.org/onlinedocs/gcc/Typeof.html
        expectResult(false) {
            check( """
                     typedef struct x Sx;

                     Sx y;
                   """)
        }

        expectResult(false) {
            check( """
                 __typeof__(struct x) y;
                   """)
        }
        expectResult(false) {
            check( """
                 struct x y;
                   """)
        }
        expectResult(true) {
            check( """
                 struct x Sx;

                 static Sx y;

                 struct x {
                  int a;
                 };   """)
        }

        expectResult(true) {
            check( """
                     __typeof__(struct x) y;

                     struct x {
                      int a;
                     };   """)
        }

        expectResult(true) {
            check( """
                             struct x y;

                             struct x {
                              int a;
                             };   """)
        }
    }
    test("enum type is unsigned int") {
        expectResult(true) {
            check( """enum x {a};
                           enum x f(void);
                           unsigned int f() { return 0; }
                   """)
        }
        expectResult(false) {
            check( """enum x {a};
                           enum x f(void);
                           signed int f() { return 0; }
                   """)
        }
        expectResult(false) {
            check( """enum x {a};
                           enum x f(void);
                           int f() { return 0; }
                   """)
        }
    }
    test("field decrement") {
        expectResult(true) {
            check( """struct x { int i; } ;
                         void test(void *data) {
                            struct x *v=data;
                            if (v->i > 0)
                                v->i--;
                         }
                   """)
        }
    }

    test("detect variable redefinitions") {
        expectResult(false) {
            check("float x; int x;")
        }
        expectResult(true) {
            check("int x; int x;")
        }
        expectResult(false) {
            check("int x=1; int x=1;")
        }
        expectResult(false) {
            check("int x=1; \n" +
                "#ifdef A\n" +
                "int x=1;\n" +
                "#endif\n")
        }
        expectResult(true) {
            check("#ifndef A\n" +
                "float x=1; \n" +
                "#endif\n" +
                "#ifdef A\n" +
                "int x=1;\n" +
                "#endif\n")
        }
        expectResult(false) {
            check("enum x {a,b}; int a=3;")
        }
        //TODO checking of different kinds currently not implemented
        //        expectResult(false) {
        //            check("enum x {a,b}; int a;")
        //        }
        expectResult(false) {
            check("enum x {a,b}; enum y {a,c};")
        }
        //TODO not implemented yet:
        //        expectResult(false) {
        //            check("int foo(int a, int a) {}")
        //        }
        expectResult(false) {
            check("int foo(int a) {int a; a++;}")
        }
        expectResult(false) {
            check("int foo() {int a; int a;}")
        }
        expectResult(false) {
            check("int foo() {int a; int a=1;}")
        }
        expectResult(false) {
            check("int foo() {int a=2; int a=1;}")
        }
        expectResult(true) {
            check("typedef struct s { int a;} s_t;" +
                "extern s_t x;" +
                "s_t x = (s_t) {0};")
        }
    }

    test("handle boolean types") {
        expectResult(true) {
            check("_Bool foo() {return 1;}")
        }
        expectResult(true) {
            check("_Bool foo() {int a; return a;}")
        }
        expectResult(true) {
            check("_Bool foo() {int a; return &a;}")
        }
    }

    test("curly initializers and casts and pointer deref") {
        expectResult(true) {
            check( """struct ab { int x; };

                           void bar() {
                                   struct ab * i;
                                   i=&(struct ab){ 1 };
                           }""")
        }

    }

    test("default types") {
        expectResult(true) {
            check(
                """
            static x = 0;
                """.stripMargin)
        }
        expectResult(true) {
            check(
                """
            foo() { return 0; }
                """.stripMargin)
        }
    }


    test("nested structs") {
        expectResult(true) {
            check( """
            struct x {
                    struct y {
                            int a;
                    } yy;
                    struct {
                            struct y b;
                    } zz;
            } xx;
                   """)
        }
        expectResult(true) {
            check( """
            struct x {
                    struct y {
                            int a;
                    } yy;
                    struct y b;
            } xx;
                   """)
        }
        expectResult(true) {
            check( """
            struct x {
                    struct z{
                            struct y {
                                int a;
                            } yy;
                    } zz;
                            struct y b;
            } xx;
                   """)
        }
        expectResult(false) {
            check( """
            struct x {
                    struct z{
                            struct y {
                                int a;
                            } yy;
                    } zz;
                    struct abc b;
            } xx;
                   """)
        }
        expectResult(false) {
            check( """
              struct x {
                      struct z{
                              struct y {
                                  int a;
                              } yy;
                      } zz;
                      struct x b;
              } xx;
                   """)
        }
    }

    test("function parameters etc") {
        expectResult(true) {
            check(
                """
                  |typedef int (a)();
                  |int foo() { return 3; }
                  |int bar(a y) { return y(); }
                  |int main() {
                  |  int x = bar(foo);
                  |  return x;
                  |}
                """.stripMargin)
        }
        expectResult(true) {
            check(
                """
                  |typedef int (a)();
                  |int foo() { return 3; }
                  |int bar(a* y) { return y(); }
                  |int main() {
                  |  int x = bar(foo);
                  |  return x;
                  |}
                """.stripMargin)
        }
        expectResult(true) {
            check(
                """
                  |typedef int (a)();
                  |int foo() { return 3; }
                  |int bar(a y) { return y(); }
                  |int main() {
                  |  int x = bar(&foo);
                  |  return x;
                  |}
                """.stripMargin)
        }
        expectResult(true) {
            check(
                """
                  |typedef int (a)();
                  |int foo() { return 3; }
                  |int bar(a* y) { return y(); }
                  |int main() {
                  |  int x = bar(&foo);
                  |  return x;
                  |}
                """.stripMargin)
        }
        expectResult(true) {
            check(
                """
                  |int foo() { return 3; }
                  |int main() {
                  |  if (&foo) ;
                  |  return 0;
                  |}
                """.stripMargin)
        }
        expectResult(true) {
            check(
                """
                  |int foo() { return 3; }
                  |int main() {
                  |  if (foo) ;
                  |  return 0;
                  |}
                """.stripMargin)
        }

    }


    test("old style function parameters") {
        expectResult(true) {
            check(
                """
                  |int main(a, b)
                  |     int a;
                  |     int b;
                  |{
                  |  a++;
                  |  return b;
                  |}
                """.stripMargin)
        }
        expectResult(true) {
            check(
                """
                  |int main(a, b)
                  |     int a;
                  |     int b;
                  |{
                  |  a++;
                  |  return b;
                  |}
                  |int foo() {
                  |     main(3,5);
                  |}
                """.stripMargin)
        }
        expectResult(true) {
            check(
                """
                  |int main(a, b)
                  |{
                  |}
                """.stripMargin)
        }
        expectResult(true) {
            check(
                """
                  |int main(a, b)
                  |{
                  |     b++;
                  |}
                """.stripMargin)
        }

    }



    test("functions returning a function") {
        expectResult(true) {
            //function foo returns a pointer to a function that accepts an int pointer and returns an int
            check(
                """
                  |int (*foo(long f))(int*) { if (f); return 0; }
                """.stripMargin)
        }

    }


    test("__builtin_va_arg") {

        expectResult(true) {
            check(
                """
                  |typedef __builtin_va_list va_list;
                  |void foo(int y, va_list ap) {
                  |  int x = 3;
                  |  *(__builtin_va_arg(ap,int*)) = x;
                  |}
                """.stripMargin)
        }

        expectResult(false) {
            check(
                """
                  |typedef __builtin_va_list va_list;
                  |void foo(int y, va_list ap) {
                  |  struct {int x;} x = {1};
                  |  *(__builtin_va_arg(ap,int*)) = x;
                  |}
                """.stripMargin)
        }
        //
    }

    test("conflicting types in redeclaration") {
        //https://www.securecoding.cert.org/confluence/display/seccode/DCL40-C.+Incompatible+declarations+of+the+same+function+or+object
        expectResult(false) {
            check(
                """
                  |extern int i;   /* UB 15 */
                  |
                  |int f(void) {
                  |    return ++i;   /* UB 37 */
                  |}
                  |
                  |short i;   /* UB 15 */
                  |
                """.stripMargin)
        }

        //incompatible according to https://www.securecoding.cert.org/confluence/display/seccode/DCL40-C.+Incompatible+declarations+of+the+same+function+or+object
        //        expectResult(false) {
        //            check(
        //                """
        //                  |extern int *a;   /* UB 15 */
        //                  |
        //                  |int f(unsigned i, int x) {
        //                  |  int tmp = a[i];   /* UB 37: read access */
        //                  |  a[i] = x;         /* UB 37: write access*/
        //                  |  return tmp;
        //                  |}
        //                  |
        //                  |int a[] = { 1, 2, 3, 4 };   /* UB 15 */
        //                """.stripMargin)
        //        }

        expectResult(false) {
            check(
                """
                  |extern int f(int a);   /* UB 15 */
                  |
                  |int g(int a) {
                  |  return f(a);   /* UB 41 */
                  |}
                  |
                  |long f(long a) {   /* UB 15 */
                  |  return a * 2;
                  |}
                """.stripMargin)
        }


    }

    test("static/external problem") {
        //problem from uclibc, that initially reported that static and extern occur together
        val c = """
                  |#if definedEx(__UCLIBC_HAS_THREADS__)
                  |static pthread_mutex_t mylock =
                  |#if (definedEx(__UCLIBC_HAS_THREADS__) && definedEx(__UCLIBC_HAS_THREADS_NATIVE__))
                  |{ { 0, 0, 0, 0, 0, 0, { 0, 0 } } }
                  |#endif
                  |#if (!definedEx(__UCLIBC_HAS_THREADS_NATIVE__) && definedEx(__LINUXTHREADS_NEW__) && definedEx(__UCLIBC_HAS_THREADS__))
                  |{0, 0, 0, PTHREAD_MUTEX_TIMED_NP,
                  |
                  |{ 0,
                  |
                  |0
                  |
                  |
                  | }
                  |
                  |
                  |}
                  |#endif
                  |#if (definedEx(__LINUXTHREADS_OLD__) && !definedEx(__LINUXTHREADS_NEW__) && !definedEx(__UCLIBC_HAS_THREADS_NATIVE__) && definedEx(__UCLIBC_HAS_THREADS__))
                  |{0, 0, 0, PTHREAD_MUTEX_ADAPTIVE_NP,
                  |
                  |{ 0,
                  |
                  |0
                  |
                  |
                  | }
                  |
                  |
                  |}
                  |#endif
                  |#if (!definedEx(__UCLIBC_HAS_THREADS__) || (!definedEx(__LINUXTHREADS_OLD__) && !definedEx(__LINUXTHREADS_NEW__) && !definedEx(__UCLIBC_HAS_THREADS_NATIVE__)))
                  |PTHREAD_MUTEX_INITIALIZER
                  |#endif
                  |
                  |#endif
                  |#if !definedEx(__UCLIBC_HAS_THREADS__)
                  |extern void *__UCLIBC_MUTEX_DUMMY_mylock
                  |#endif
                  |;
                """.stripMargin

        expectResult(true) {
            check(c)
        }
    }


    test("enum initializer") {
        correct( """
                   |enum {
                   |   A = 0,
                   |   B = A + 0
                   |};
                   |void foo() { int x = A; }
                 """.stripMargin)
        error( """
                   |enum {
                   |   A = 0,
                   |   B = A + 0
                   |};
                   |void foo() { int x = C; }
                 """.stripMargin)

        error( """
                  |enum {
                  |   _1_A = 0,
                  |   B = A + 0
                  |};
                """.stripMargin)

    }
}

