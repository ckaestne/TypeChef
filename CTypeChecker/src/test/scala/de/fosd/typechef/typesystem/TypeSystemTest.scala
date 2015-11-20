package de.fosd.typechef.typesystem


import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.parser.c._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSuite, Matchers}

@RunWith(classOf[JUnitRunner])
class TypeSystemTest extends FunSuite with Matchers with TestHelperTS {


    test("typecheck simple translation unit") {
        assertResult(true) {
            check("void foo() {};" +
                "void bar(){foo();}")
        }
        assertResult(false) {
            check("void bar(){foo();}")
        }
    }
    test("detect redefinitions") {
        assertResult(false) {
            check("void foo(){} void foo(){}")
        }
        assertResult(false) {
            check("void foo(){} \n" +
                "#ifdef A\n" +
                "void foo(){}\n" +
                "#endif\n")
        }
        assertResult(true) {
            check("#ifndef A\n" +
                "void foo(){} \n" +
                "#endif\n" +
                "#ifdef A\n" +
                "void foo(){}\n" +
                "#endif\n")
        }
    }
    test("typecheck function calls in translation unit with features") {
        assertResult(true) {
            check("void foo(){} \n" +
                "#ifdef A\n" +
                "void bar(){foo();}\n" +
                "#endif\n")
        }
        assertResult(false) {
            check(
                "#ifdef A\n" +
                    "void foo2(){} \n" +
                    "#endif\n" +
                    "void bar(){foo2();}\n")
        }
        assertResult(true) {
            check(
                "#ifdef A\n" +
                    "void foo3(){} \n" +
                    "#endif\n" +
                    "#ifndef A\n" +
                    "void foo3(){} \n" +
                    "#endif\n" +
                    "void bar(){foo3();}\n")
        }
        assertResult(true) {
            check(
                "#ifdef A\n" +
                    "int foo4(){} \n" +
                    "#endif\n" +
                    "#ifndef A\n" +
                    "double foo4(){}\n" +
                    "#endif\n" +
                    "void bar(){foo4();}\n")
        }
        assertResult(true) {
            check("#ifdef A\n" +
                "void foo(){} \n" +
                "void bar(){foo();}\n" +
                "#endif\n")
        }

    }

    test("local variable test") {
        assertResult(true) {
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
        correct("void foo(){ return; }")
        warning("void foo(){ return 1; }")
        correct("int foo(){ return 1; }")
        warning("int * foo(){ return \"abc\"; }") //warning
        correct("int * foo(){ return 0; }")
        warning("int * foo(){ return 1; }") //warning
        error("int foo(){ return; }")
    }

    test("increment on array") {
        assertResult(false) {
            check( """
                struct s {} x;
                int foo() { if (x->a) {} }""")
        }

        assertResult(true) {
            check( """
            void xchdir(const char *path) ;
            int foo(char *argv[]) {
                if (*++argv)
                    xchdir(*argv++);
            }""")
        }
    }

    test("illtyped only under unsatisfiable configurations") {
        assertResult(true) {
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
        assertResult(true) {
            check( """
                int foo (double a, double b){
                    double square (double z) { return z * z; }
                    return square (a) + square (b);
                }""")
        }
        assertResult(true) {
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
        assertResult(true) {
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
        assertResult(true) {
            check( """
            int a() {
            #ifdef X
                typedef int xx;
            #endif
            }
                   """)
        }
        assertResult(true) {
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
                   """)
        }


    }

    test("initializer scope") {
        assertResult(false) {
            check( """
             int x=y;
                   """)
        }
        assertResult(true) {
            check( """
             int x=x;
                   """)
        }
    }

    ignore("check label environement") {
        //TODO check label environement
        assertResult(false) {
            check( """
         void foo() {&&__lab;}
                   """)
        }
        assertResult(false) {
            check( """
         void foo() {goto __lab;}
                   """)
        }
    }

    test("local labels and label deref") {
        assertResult(true) {
            check( """
             void foo() {__label__ __lab; __lab: &&__lab;}
                   """)
        }
    }
    test("recursive function") {
        assertResult(true) {
            check( """
             int foo(int i) {if (i==1) return 0; else return foo(i-1);}
                   """)
        }
    }
    test("alternative parameter declaration") {
        assertResult(true) {
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
        assertResult(false) {
            check( """
             void foo(int a, int *b, int c) {}
             void bar() {
                int a,b,*c;
                foo(a,b,c);
             }
                   """)
        }
        assertResult(true) {
            check( """
             void foo(int *a) {}
             void bar() {
                foo(0);
             }
                   """)
        }
        assertResult(false) {
            check( """
             void foo(int *a) {}
             void bar() {
                foo(1);
             }
                   """)
        }
    }

    test("function comparison") {
        assertResult(true) {
            check( """
             void foo(int a) ;
             void bar() {
                if (foo==&foo);
             }
                   """)
        }
    }

    test("enum scope") {
        assertResult(true) {
            check( """enum { A, B, C };
                      int x = A;  """)
        }
        assertResult(true) {
            check( """enum { A, B, C } x = A;""")
        }
        assertResult(true) {
            check( """enum { A, B, C } foo() { return 0; }
                      int x=A; """)
        }
        assertResult(true) {
            check( """enum { A, B, C } foo() { return A; }""")
        }
    }

    test("decl scope") {
        assertResult(true) {
            check( """
             struct { int a; } a[2], *b=a;
                   """)
        }

    }

    test("check array initialization") {
        assertResult(true) {
            check( """
                int a=3;
                int b[a];
                   """)
        }
        assertResult(false) {
            check( """
                int b[a];
                   """)
        }
    }

    test("builtin") {
        assertResult(true) {
            check( """
                void foo(){int a[],b[];
                    int x[];
                    x=a;
                    x=b;
                }
                   """)
        }
        correct( """  const char *x = __PRETTY_FUNCTION__; """)

        assertResult(true) {
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
        assertResult(true) {
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
        assertResult(false) {
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
        assertResult(true) {
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
        assertResult(true) {
            check( """
             #ifdef X
             int a
             #endif
             ;
                   """)
        }

    }


    test("alternative structs") {
        assertResult(true) {
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
        assertResult(true) {
            check( """
                    int a;
                    __asm__("whatever");
                    int b;
                   """)
        }
    }

    test("int pointer compatibility") {
        assertResult(true) {
            check( """
                void foo(){
                    unsigned int a;
                    signed int b;
                    a=b;
                }
                   """)
        }
        assertResult(false) {
            check( """
                void foo(){
                    unsigned int *a;
                    signed int *b;
                    a=&b;
                }
                   """)
        }
        //last two should not yield an error or warning if -Wno-pointer-sign is set (default in linux)
        assertResult(true) {
            check( """
                void foo(){
                    unsigned int *a;
                    signed int *b;
                    a=b;
                }
                   """)
        }
        assertResult(true) {
            check( """
                void foo(){
                    char *a;
                    unsigned char *b;
                    a=b;
                }
                   """)
        }
        assertResult(true) {
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
        assertResult(true) {
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
        assertResult(true) {
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
        assertResult(true) {
            check( """
                    void foo(){
                        char *a, *b, *c, *d;
                        d=a-b+c;
                    }
                   """)
        }

    }

    test("asm statement") {
        assertResult(true) {
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
        assertResult(false) {
            check( """
                     typedef struct x Sx;

                     Sx y;
                   """)
        }

        assertResult(false) {
            check( """
                 __typeof__(struct x) y;
                   """)
        }
        assertResult(false) {
            check( """
                 struct x y;
                   """)
        }
        assertResult(true) {
            check( """
                 struct x Sx;

                 static Sx y;

                 struct x {
                  int a;
                 };   """)
        }

        assertResult(true) {
            check( """
                     __typeof__(struct x) y;

                     struct x {
                      int a;
                     };   """)
        }

        assertResult(true) {
            check( """
                             struct x y;

                             struct x {
                              int a;
                             };   """)
        }
    }
    test("enum type is unsigned int") {
        assertResult(true) {
            check( """enum x {a};
                           enum x f(void);
                           unsigned int f() { return 0; }
                   """)
        }
        assertResult(false) {
            check( """enum x {a};
                           enum x f(void);
                           signed int f() { return 0; }
                   """)
        }
        assertResult(false) {
            check( """enum x {a};
                           enum x f(void);
                           int f() { return 0; }
                   """)
        }
    }
    test("field decrement") {
        assertResult(true) {
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
        assertResult(false) {
            check("float x; int x;")
        }
        assertResult(true) {
            check("int x; int x;")
        }
        assertResult(false) {
            check("int x=1; int x=1;")
        }
        assertResult(false) {
            check("int x=1; \n" +
                "#ifdef A\n" +
                "int x=1;\n" +
                "#endif\n")
        }
        assertResult(true) {
            check("#ifndef A\n" +
                "float x=1; \n" +
                "#endif\n" +
                "#ifdef A\n" +
                "int x=1;\n" +
                "#endif\n")
        }
        assertResult(false) {
            check("enum x {a,b}; int a=3;")
        }
        //TODO checking of different kinds currently not implemented
        //        assertResult(false) {
        //            check("enum x {a,b}; int a;")
        //        }
        assertResult(false) {
            check("enum x {a,b}; enum y {a,c};")
        }
        //TODO not implemented yet:
        //        assertResult(false) {
        //            check("int foo(int a, int a) {}")
        //        }
        assertResult(false) {
            check("int foo(int a) {int a; a++;}")
        }
        assertResult(false) {
            check("int foo() {int a; int a;}")
        }
        assertResult(false) {
            check("int foo() {int a; int a=1;}")
        }
        assertResult(false) {
            check("int foo() {int a=2; int a=1;}")
        }
        assertResult(true) {
            check("typedef struct s { int a;} s_t;" +
                "extern s_t x;" +
                "s_t x = (s_t) {0};")
        }
    }

    test("handle boolean types") {
        assertResult(true) {
            check("_Bool foo() {return 1;}")
        }
        assertResult(true) {
            check("_Bool foo() {int a; return a;}")
        }
        assertResult(true) {
            check("_Bool foo() {int a; return &a;}")
        }
    }

    test("curly initializers and casts and pointer deref") {
        assertResult(true) {
            check( """struct ab { int x; };

                           void bar() {
                                   struct ab * i;
                                   i=&(struct ab){ 1 };
                           }""")
        }

    }

    test("default types") {
        assertResult(true) {
            check(
                """
            static x = 0;
                """.stripMargin)
        }
        assertResult(true) {
            check(
                """
            foo() { return 0; }
                """.stripMargin)
        }
    }


    test("nested structs") {
        assertResult(true) {
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
        assertResult(true) {
            check( """
            struct x {
                    struct y {
                            int a;
                    } yy;
                    struct y b;
            } xx;
                   """)
        }
        assertResult(true) {
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
        assertResult(false) {
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
        assertResult(false) {
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
        assertResult(true) {
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
        assertResult(true) {
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
        assertResult(true) {
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
        assertResult(true) {
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
        assertResult(true) {
            check(
                """
                  |int foo() { return 3; }
                  |int main() {
                  |  if (&foo) ;
                  |  return 0;
                  |}
                """.stripMargin)
        }
        assertResult(true) {
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
        assertResult(true) {
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
        assertResult(true) {
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
        assertResult(true) {
            check(
                """
                  |int main(a, b)
                  |{
                  |}
                """.stripMargin)
        }
        assertResult(true) {
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
        assertResult(true) {
            //function foo returns a pointer to a function that accepts an int pointer and returns an int
            check(
                """
                  |int (*foo(long f))(int*) { if (f); return 0; }
                """.stripMargin)
        }

    }


    test("__builtin_va_arg") {

        assertResult(true) {
            check(
                """
                  |typedef __builtin_va_list va_list;
                  |void foo(int y, va_list ap) {
                  |  int x = 3;
                  |  *(__builtin_va_arg(ap,int*)) = x;
                  |}
                """.stripMargin)
        }

        assertResult(false) {
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
        assertResult(false) {
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
        //        assertResult(false) {
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

        assertResult(false) {
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

        assertResult(true) {
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
    ignore("enum scoping in struct") { // this problem occurs in one file in linux. not easy to fix
        correct( """
                   |struct A {
                   |    enum C { AA, BB };
                   |    int DD[AA]; //<-- AA was not in scope here
                   |} D;
                   |int x() {
                   |    int i;
                   |    for (i=0; i< AA; i++) ;
                   |}
                 """.stripMargin)
        error( """
                   |struct A {
                   |    enum C { Ax, BB };
                   |} D;
                   |int x() { return AA; }
                 """.stripMargin)
    }

    test("asm statements") {
        correct("""
            void foo(){
                 asm volatile("2: rdmsr ; xor %[err],%[err]\n"
                       "1:\n\t"
                       ".section .fixup,\"ax\"\n\t"
                       "3:  mov %[fault],%[err] ; jmp 1b\n\t"
                       ".previous\n\t"
                       " .section __ex_table,\"a\"\n"
                       : "c" (msr), [fault] "i" (-5));
                       }
                """        )
    }

    test("valid and invalid return types") {
        correct("""
            typedef void VOID;
            VOID foo(){}
              """        )
        error("""
            #ifdef X
                 typedef void VOID;
            #endif
            VOID foo(){}
                """        )
        correct("""
            typedef void VOID;
            VOID foo(){ return; }
                """        )
        correct("""
            #ifdef X
                 typedef void VOID;
            #else
                 typedef int VOID;
            #endif
            VOID foo(){
            #ifdef X
              return;
            #else
              return 1;
            #endif
            }
              """        )
    }

    test("conditional const type") {
        errorIf(
            """
              |void foo() {
              |  #ifdef X
              |  const
              |  #endif
              |  int i=0;
              |  i=3;
              |}
            """.stripMargin, FeatureExprFactory.createDefinedExternal("X"))
    }
}

