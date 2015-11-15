package de.fosd.typechef.typesystem.generated

import org.junit._
import de.fosd.typechef.typesystem._

class GeneratedCastTests extends TestHelperTS {

    @Test def test_conf0_0() {
        correct("""
              char foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf0_1() {
        correct("""
              char foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf0_2() {
        correct("""
              char foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf0_3() {
        correct("""
              char foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf0_4() {
        correct("""
              char foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf0_5() {
        correct("""
              char foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }
   @Test def test_conf0_6() {
        correct("""
              char foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }
   @Test def test_conf0_7() {
        correct("""
              char foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf0_8() {
        correct("""
              char foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf0_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_9727111082082633436.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_9727111082082633436.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int * a = (int *) foo();
                           ^

        */
        correct("""
              char foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf0_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_10529997063484067307.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_10529997063484067307.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 long * a = (long *) foo();
                            ^

        */
        correct("""
              char foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf0_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_111722503998958267763.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_111722503998958267763.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 double * a = (double *) foo();
                              ^

        */
        correct("""
              char foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf0_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_12670461492777272574.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_12670461492777272574.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              char foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }
   @Test def test_conf0_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_13781519648619151301.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_13781519648619151301.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              char foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }
   @Test def test_conf0_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_143188772987652735163.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_143188772987652735163.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf0_143188772987652735163.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf0_143188772987652735163.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              char foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf0_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_154515363552402485526.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_154515363552402485526.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf0_154515363552402485526.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf0_154515363552402485526.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              char foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf0_16() {
        correct("""
              char foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf0_17() {
        correct("""
              char foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf0_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_18622506657500466778.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_18622506657500466778.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int * a = (int *) foo();
                           ^

        */
        correct("""
              char foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf0_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_197814001517744705870.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_197814001517744705870.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 const int * a = (const int *) foo();
                                 ^

        */
        correct("""
              char foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf0_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_208499167570601307520.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_208499167570601307520.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 volatile int * a = (volatile int *) foo();
                                    ^

        */
        correct("""
              char foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf1_0() {
        correct("""
              signed char foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf1_1() {
        correct("""
              signed char foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf1_2() {
        correct("""
              signed char foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf1_3() {
        correct("""
              signed char foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf1_4() {
        correct("""
              signed char foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf1_5() {
        correct("""
              signed char foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }
   @Test def test_conf1_6() {
        correct("""
              signed char foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }
   @Test def test_conf1_7() {
        correct("""
              signed char foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf1_8() {
        correct("""
              signed char foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf1_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_94574522823208891629.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_94574522823208891629.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int * a = (int *) foo();
                           ^

        */
        correct("""
              signed char foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf1_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_107916400921733543392.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_107916400921733543392.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 long * a = (long *) foo();
                            ^

        */
        correct("""
              signed char foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf1_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_113255844448652038076.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_113255844448652038076.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 double * a = (double *) foo();
                              ^

        */
        correct("""
              signed char foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf1_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_12947431291418845524.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_12947431291418845524.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              signed char foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }
   @Test def test_conf1_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_136614454512062263397.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_136614454512062263397.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              signed char foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }
   @Test def test_conf1_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_145158864717859883630.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_145158864717859883630.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf1_145158864717859883630.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf1_145158864717859883630.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              signed char foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf1_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_159058493645192672957.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_159058493645192672957.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf1_159058493645192672957.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf1_159058493645192672957.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              signed char foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf1_16() {
        correct("""
              signed char foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf1_17() {
        correct("""
              signed char foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf1_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_183629812281876956022.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_183629812281876956022.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int * a = (int *) foo();
                           ^

        */
        correct("""
              signed char foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf1_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_192020363644285420526.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_192020363644285420526.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 const int * a = (const int *) foo();
                                 ^

        */
        correct("""
              signed char foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf1_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_203522156739920944251.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_203522156739920944251.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 volatile int * a = (volatile int *) foo();
                                    ^

        */
        correct("""
              signed char foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf2_0() {
        correct("""
              unsigned char foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf2_1() {
        correct("""
              unsigned char foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf2_2() {
        correct("""
              unsigned char foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf2_3() {
        correct("""
              unsigned char foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf2_4() {
        correct("""
              unsigned char foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf2_5() {
        correct("""
              unsigned char foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }
   @Test def test_conf2_6() {
        correct("""
              unsigned char foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }
   @Test def test_conf2_7() {
        correct("""
              unsigned char foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf2_8() {
        correct("""
              unsigned char foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf2_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_9958015461513285670.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_9958015461513285670.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int * a = (int *) foo();
                           ^

        */
        correct("""
              unsigned char foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf2_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_10649287723574460298.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_10649287723574460298.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 long * a = (long *) foo();
                            ^

        */
        correct("""
              unsigned char foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf2_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_111895052979802282166.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_111895052979802282166.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 double * a = (double *) foo();
                              ^

        */
        correct("""
              unsigned char foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf2_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_12670184503517797776.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_12670184503517797776.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              unsigned char foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }
   @Test def test_conf2_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_138951807578066538530.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_138951807578066538530.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              unsigned char foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }
   @Test def test_conf2_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_148377835834622224906.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_148377835834622224906.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf2_148377835834622224906.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf2_148377835834622224906.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              unsigned char foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf2_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_154416858209783920067.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_154416858209783920067.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf2_154416858209783920067.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf2_154416858209783920067.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              unsigned char foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf2_16() {
        correct("""
              unsigned char foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf2_17() {
        correct("""
              unsigned char foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf2_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_184316585277188472030.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_184316585277188472030.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int * a = (int *) foo();
                           ^

        */
        correct("""
              unsigned char foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf2_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_198093110091943622277.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_198093110091943622277.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 const int * a = (const int *) foo();
                                 ^

        */
        correct("""
              unsigned char foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf2_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_205268189713734113600.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_205268189713734113600.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 volatile int * a = (volatile int *) foo();
                                    ^

        */
        correct("""
              unsigned char foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf3_0() {
        correct("""
              unsigned int foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf3_1() {
        correct("""
              unsigned int foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf3_2() {
        correct("""
              unsigned int foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf3_3() {
        correct("""
              unsigned int foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf3_4() {
        correct("""
              unsigned int foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf3_5() {
        correct("""
              unsigned int foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }
   @Test def test_conf3_6() {
        correct("""
              unsigned int foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }
   @Test def test_conf3_7() {
        correct("""
              unsigned int foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf3_8() {
        correct("""
              unsigned int foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf3_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_97139255897363876234.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_97139255897363876234.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int * a = (int *) foo();
                           ^

        */
        correct("""
              unsigned int foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf3_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_109053637849451025353.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_109053637849451025353.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 long * a = (long *) foo();
                            ^

        */
        correct("""
              unsigned int foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf3_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_114290113155805586114.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_114290113155805586114.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 double * a = (double *) foo();
                              ^

        */
        correct("""
              unsigned int foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf3_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_121830834745722806003.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_121830834745722806003.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              unsigned int foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }
   @Test def test_conf3_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_132326674415009764085.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_132326674415009764085.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              unsigned int foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }
   @Test def test_conf3_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_141298084166251175938.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_141298084166251175938.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf3_141298084166251175938.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf3_141298084166251175938.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              unsigned int foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf3_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_154392216077819881468.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_154392216077819881468.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf3_154392216077819881468.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf3_154392216077819881468.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              unsigned int foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf3_16() {
        correct("""
              unsigned int foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf3_17() {
        correct("""
              unsigned int foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf3_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_182672842051847458499.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_182672842051847458499.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int * a = (int *) foo();
                           ^

        */
        correct("""
              unsigned int foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf3_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_193621209749396149290.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_193621209749396149290.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 const int * a = (const int *) foo();
                                 ^

        */
        correct("""
              unsigned int foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf3_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_202290562006335334313.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_202290562006335334313.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 volatile int * a = (volatile int *) foo();
                                    ^

        */
        correct("""
              unsigned int foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf4_0() {
        correct("""
              signed int foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf4_1() {
        correct("""
              signed int foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf4_2() {
        correct("""
              signed int foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf4_3() {
        correct("""
              signed int foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf4_4() {
        correct("""
              signed int foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf4_5() {
        correct("""
              signed int foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }
   @Test def test_conf4_6() {
        correct("""
              signed int foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }
   @Test def test_conf4_7() {
        correct("""
              signed int foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf4_8() {
        correct("""
              signed int foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf4_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_92767975008551974694.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_92767975008551974694.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int * a = (int *) foo();
                           ^

        */
        correct("""
              signed int foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf4_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_105267961650781460376.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_105267961650781460376.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 long * a = (long *) foo();
                            ^

        */
        correct("""
              signed int foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf4_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_114925561170812410356.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_114925561170812410356.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 double * a = (double *) foo();
                              ^

        */
        correct("""
              signed int foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf4_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_121566212662085059765.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_121566212662085059765.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              signed int foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }
   @Test def test_conf4_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_131391321267403336163.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_131391321267403336163.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              signed int foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }
   @Test def test_conf4_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_144486342403622855925.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_144486342403622855925.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf4_144486342403622855925.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf4_144486342403622855925.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              signed int foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf4_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_151203263258591880292.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_151203263258591880292.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf4_151203263258591880292.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf4_151203263258591880292.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              signed int foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf4_16() {
        correct("""
              signed int foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf4_17() {
        correct("""
              signed int foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf4_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_181789484913012808176.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_181789484913012808176.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int * a = (int *) foo();
                           ^

        */
        correct("""
              signed int foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf4_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_198072017841656734294.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_198072017841656734294.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 const int * a = (const int *) foo();
                                 ^

        */
        correct("""
              signed int foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf4_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_201314520318174545609.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_201314520318174545609.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 volatile int * a = (volatile int *) foo();
                                    ^

        */
        correct("""
              signed int foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf5_0() {
        correct("""
              long foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf5_1() {
        correct("""
              long foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf5_2() {
        correct("""
              long foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf5_3() {
        correct("""
              long foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf5_4() {
        correct("""
              long foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf5_5() {
        correct("""
              long foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }
   @Test def test_conf5_6() {
        correct("""
              long foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }
   @Test def test_conf5_7() {
        correct("""
              long foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf5_8() {
        correct("""
              long foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf5_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_91797191562693148186.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_91797191562693148186.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int * a = (int *) foo();
                           ^

        */
        correct("""
              long foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf5_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_104117476140279064149.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_104117476140279064149.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 long * a = (long *) foo();
                            ^

        */
        correct("""
              long foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf5_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_116133840405322207233.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_116133840405322207233.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 double * a = (double *) foo();
                              ^

        */
        correct("""
              long foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf5_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_122361506874396131513.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_122361506874396131513.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              long foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }
   @Test def test_conf5_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_133071318758292032103.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_133071318758292032103.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              long foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }
   @Test def test_conf5_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_143921853377500059364.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_143921853377500059364.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf5_143921853377500059364.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf5_143921853377500059364.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              long foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf5_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_155428696224017926277.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_155428696224017926277.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf5_155428696224017926277.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf5_155428696224017926277.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              long foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf5_16() {
        correct("""
              long foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf5_17() {
        correct("""
              long foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf5_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_187345996100831527717.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_187345996100831527717.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int * a = (int *) foo();
                           ^

        */
        correct("""
              long foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf5_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_198014898517797887911.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_198014898517797887911.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 const int * a = (const int *) foo();
                                 ^

        */
        correct("""
              long foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf5_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_20937518216816236223.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_20937518216816236223.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 volatile int * a = (volatile int *) foo();
                                    ^

        */
        correct("""
              long foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf6_0() {
        correct("""
              float foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf6_1() {
        correct("""
              float foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf6_2() {
        correct("""
              float foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf6_3() {
        correct("""
              float foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf6_4() {
        correct("""
              float foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf6_5() {
        correct("""
              float foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }
   @Test def test_conf6_6() {
        correct("""
              float foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }
   @Test def test_conf6_7() {
        correct("""
              float foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf6_8() {
        correct("""
              float foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf6_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_97218347949394619014.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_97218347949394619014.c:3:17: error: cannot convert to a pointer type
                 int * a = (int *) foo();
                 ^

        */
        error("""
              float foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf6_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_108616117779783087990.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_108616117779783087990.c:3:17: error: cannot convert to a pointer type
                 long * a = (long *) foo();
                 ^

        */
        error("""
              float foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf6_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_117770129186292683544.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_117770129186292683544.c:3:17: error: cannot convert to a pointer type
                 double * a = (double *) foo();
                 ^

        */
        error("""
              float foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf6_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_12960495824964896361.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_12960495824964896361.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              float foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }
   @Test def test_conf6_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_13214967938814295388.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_13214967938814295388.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              float foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }
   @Test def test_conf6_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_141193787770189365584.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_141193787770189365584.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf6_141193787770189365584.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf6_141193787770189365584.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              float foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf6_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_156736290617236140408.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_156736290617236140408.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf6_156736290617236140408.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf6_156736290617236140408.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              float foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf6_16() {
        correct("""
              float foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf6_17() {
        correct("""
              float foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf6_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_183049685017708012869.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_183049685017708012869.c:3:17: error: cannot convert to a pointer type
                 int * a = (int *) foo();
                 ^

        */
        error("""
              float foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf6_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_197620111734117420839.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_197620111734117420839.c:3:17: error: cannot convert to a pointer type
                 const int * a = (const int *) foo();
                 ^

        */
        error("""
              float foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf6_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_206320279537384783977.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_206320279537384783977.c:3:17: error: cannot convert to a pointer type
                 volatile int * a = (volatile int *) foo();
                 ^

        */
        error("""
              float foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf7_0() {
        correct("""
              double foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf7_1() {
        correct("""
              double foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf7_2() {
        correct("""
              double foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf7_3() {
        correct("""
              double foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf7_4() {
        correct("""
              double foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf7_5() {
        correct("""
              double foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }
   @Test def test_conf7_6() {
        correct("""
              double foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }
   @Test def test_conf7_7() {
        correct("""
              double foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf7_8() {
        correct("""
              double foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf7_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_92614408134246883822.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_92614408134246883822.c:3:17: error: cannot convert to a pointer type
                 int * a = (int *) foo();
                 ^

        */
        error("""
              double foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf7_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_107759228590848520253.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_107759228590848520253.c:3:17: error: cannot convert to a pointer type
                 long * a = (long *) foo();
                 ^

        */
        error("""
              double foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf7_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_11224644591649930450.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_11224644591649930450.c:3:17: error: cannot convert to a pointer type
                 double * a = (double *) foo();
                 ^

        */
        error("""
              double foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf7_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_124170763884065818746.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_124170763884065818746.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              double foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }
   @Test def test_conf7_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_133710064899856634165.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_133710064899856634165.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              double foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }
   @Test def test_conf7_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_146975171644350741302.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_146975171644350741302.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf7_146975171644350741302.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf7_146975171644350741302.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              double foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf7_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_15155081527116217306.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_15155081527116217306.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf7_15155081527116217306.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf7_15155081527116217306.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              double foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf7_16() {
        correct("""
              double foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf7_17() {
        correct("""
              double foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf7_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_186508023403824131037.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_186508023403824131037.c:3:17: error: cannot convert to a pointer type
                 int * a = (int *) foo();
                 ^

        */
        error("""
              double foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf7_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_198154526281421216531.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_198154526281421216531.c:3:17: error: cannot convert to a pointer type
                 const int * a = (const int *) foo();
                 ^

        */
        error("""
              double foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf7_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_203865807885599524159.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_203865807885599524159.c:3:17: error: cannot convert to a pointer type
                 volatile int * a = (volatile int *) foo();
                 ^

        */
        error("""
              double foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf8_0() {
        correct("""
              long double foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf8_1() {
        correct("""
              long double foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf8_2() {
        correct("""
              long double foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf8_3() {
        correct("""
              long double foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf8_4() {
        correct("""
              long double foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf8_5() {
        correct("""
              long double foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }
   @Test def test_conf8_6() {
        correct("""
              long double foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }
   @Test def test_conf8_7() {
        correct("""
              long double foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf8_8() {
        correct("""
              long double foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf8_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_9556929454884362419.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_9556929454884362419.c:3:17: error: cannot convert to a pointer type
                 int * a = (int *) foo();
                 ^

        */
        error("""
              long double foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf8_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_107352357556706362110.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_107352357556706362110.c:3:17: error: cannot convert to a pointer type
                 long * a = (long *) foo();
                 ^

        */
        error("""
              long double foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf8_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_112890628310497101792.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_112890628310497101792.c:3:17: error: cannot convert to a pointer type
                 double * a = (double *) foo();
                 ^

        */
        error("""
              long double foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf8_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_128914216285165492316.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_128914216285165492316.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              long double foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }
   @Test def test_conf8_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_138334126387575490267.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_138334126387575490267.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              long double foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }
   @Test def test_conf8_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_147835558387184972052.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_147835558387184972052.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf8_147835558387184972052.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf8_147835558387184972052.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              long double foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf8_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_157741786011442734549.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_157741786011442734549.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf8_157741786011442734549.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf8_157741786011442734549.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              long double foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf8_16() {
        correct("""
              long double foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf8_17() {
        correct("""
              long double foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf8_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_183850990473797059294.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_183850990473797059294.c:3:17: error: cannot convert to a pointer type
                 int * a = (int *) foo();
                 ^

        */
        error("""
              long double foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf8_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_19323185918542982011.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_19323185918542982011.c:3:17: error: cannot convert to a pointer type
                 const int * a = (const int *) foo();
                 ^

        */
        error("""
              long double foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf8_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_20658381504597390648.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_20658381504597390648.c:3:17: error: cannot convert to a pointer type
                 volatile int * a = (volatile int *) foo();
                 ^

        */
        error("""
              long double foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf9_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_08226840579207891042.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_08226840579207891042.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 char a = (char) foo();
                          ^

        */
        correct("""
              int * foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf9_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_15968775224454300638.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_15968775224454300638.c:3:33: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 signed char a = (signed char) foo();
                                 ^

        */
        correct("""
              int * foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf9_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_25743023479575965738.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_25743023479575965738.c:3:35: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 unsigned char a = (unsigned char) foo();
                                   ^

        */
        correct("""
              int * foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf9_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_32894181305604990534.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_32894181305604990534.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 unsigned int a = (unsigned int) foo();
                                  ^

        */
        correct("""
              int * foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf9_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_48633435510624816350.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_48633435510624816350.c:3:32: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 signed int a = (signed int) foo();
                                ^

        */
        correct("""
              int * foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf9_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_51050489486645598329.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_51050489486645598329.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 long a = (long) foo();
                          ^

        */
        correct("""
              int * foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }
   @Test def test_conf9_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_65630164396017996561.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_65630164396017996561.c:3:17: error: pointer value used where a floating point value was expected
                 float a = (float) foo();
                 ^

        */
        error("""
              int * foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }
   @Test def test_conf9_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_71046649065904306413.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_71046649065904306413.c:3:17: error: pointer value used where a floating point value was expected
                 double a = (double) foo();
                 ^

        */
        error("""
              int * foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf9_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_82153814169414775723.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_82153814169414775723.c:3:17: error: pointer value used where a floating point value was expected
                 long double a = (long double) foo();
                 ^

        */
        error("""
              int * foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf9_9() {
        correct("""
              int * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf9_10() {
        correct("""
              int * foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf9_11() {
        correct("""
              int * foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf9_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_12384893119255255287.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_12384893119255255287.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              int * foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }
   @Test def test_conf9_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_137730080935228796322.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_137730080935228796322.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              int * foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }
   @Test def test_conf9_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_145420324900309809932.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_145420324900309809932.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf9_145420324900309809932.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf9_145420324900309809932.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              int * foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf9_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_157114420180548770059.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_157114420180548770059.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf9_157114420180548770059.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf9_157114420180548770059.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              int * foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf9_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_164899913807729232721.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_164899913807729232721.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 volatile int a = (volatile int) foo();
                                  ^

        */
        correct("""
              int * foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf9_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_174917782347998170383.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_174917782347998170383.c:3:31: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 const int a = (const int) foo();
                               ^

        */
        correct("""
              int * foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf9_18() {
        correct("""
              int * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf9_19() {
        correct("""
              int * foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf9_20() {
        correct("""
              int * foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf10_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_0474024758541959643.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_0474024758541959643.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 char a = (char) foo();
                          ^

        */
        correct("""
              long * foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf10_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_11504504459059804088.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_11504504459059804088.c:3:33: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 signed char a = (signed char) foo();
                                 ^

        */
        correct("""
              long * foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf10_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_28672416037692081737.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_28672416037692081737.c:3:35: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 unsigned char a = (unsigned char) foo();
                                   ^

        */
        correct("""
              long * foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf10_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_3375508037879228749.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_3375508037879228749.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 unsigned int a = (unsigned int) foo();
                                  ^

        */
        correct("""
              long * foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf10_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_42356114306874390322.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_42356114306874390322.c:3:32: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 signed int a = (signed int) foo();
                                ^

        */
        correct("""
              long * foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf10_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_54610123767313699442.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_54610123767313699442.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 long a = (long) foo();
                          ^

        */
        correct("""
              long * foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }
   @Test def test_conf10_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_63127327813404365669.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_63127327813404365669.c:3:17: error: pointer value used where a floating point value was expected
                 float a = (float) foo();
                 ^

        */
        error("""
              long * foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }
   @Test def test_conf10_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_71309577823355847564.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_71309577823355847564.c:3:17: error: pointer value used where a floating point value was expected
                 double a = (double) foo();
                 ^

        */
        error("""
              long * foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf10_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_8770532189096398697.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_8770532189096398697.c:3:17: error: pointer value used where a floating point value was expected
                 long double a = (long double) foo();
                 ^

        */
        error("""
              long * foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf10_9() {
        correct("""
              long * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf10_10() {
        correct("""
              long * foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf10_11() {
        correct("""
              long * foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf10_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_12829550101132593621.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_12829550101132593621.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              long * foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }
   @Test def test_conf10_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_133207557755792317802.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_133207557755792317802.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              long * foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }
   @Test def test_conf10_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_146813076346720976415.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_146813076346720976415.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf10_146813076346720976415.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf10_146813076346720976415.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              long * foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf10_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_154842160840222649270.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_154842160840222649270.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf10_154842160840222649270.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf10_154842160840222649270.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              long * foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf10_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_168702267316642940104.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_168702267316642940104.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 volatile int a = (volatile int) foo();
                                  ^

        */
        correct("""
              long * foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf10_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_17355135269527276478.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_17355135269527276478.c:3:31: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 const int a = (const int) foo();
                               ^

        */
        correct("""
              long * foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf10_18() {
        correct("""
              long * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf10_19() {
        correct("""
              long * foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf10_20() {
        correct("""
              long * foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf11_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_0472569741274321839.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_0472569741274321839.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 char a = (char) foo();
                          ^

        */
        correct("""
              double * foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf11_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_1668160451960042608.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_1668160451960042608.c:3:33: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 signed char a = (signed char) foo();
                                 ^

        */
        correct("""
              double * foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf11_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_23210520950854802998.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_23210520950854802998.c:3:35: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 unsigned char a = (unsigned char) foo();
                                   ^

        */
        correct("""
              double * foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf11_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_35036320277266069361.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_35036320277266069361.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 unsigned int a = (unsigned int) foo();
                                  ^

        */
        correct("""
              double * foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf11_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_46386803809604967629.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_46386803809604967629.c:3:32: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 signed int a = (signed int) foo();
                                ^

        */
        correct("""
              double * foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf11_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_5238248343908921929.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_5238248343908921929.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 long a = (long) foo();
                          ^

        */
        correct("""
              double * foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }
   @Test def test_conf11_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_62482332665113444193.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_62482332665113444193.c:3:17: error: pointer value used where a floating point value was expected
                 float a = (float) foo();
                 ^

        */
        error("""
              double * foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }
   @Test def test_conf11_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_72301962422695920795.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_72301962422695920795.c:3:17: error: pointer value used where a floating point value was expected
                 double a = (double) foo();
                 ^

        */
        error("""
              double * foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf11_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_85113981364744089239.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_85113981364744089239.c:3:17: error: pointer value used where a floating point value was expected
                 long double a = (long double) foo();
                 ^

        */
        error("""
              double * foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf11_9() {
        correct("""
              double * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf11_10() {
        correct("""
              double * foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf11_11() {
        correct("""
              double * foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf11_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_127177795423934911508.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_127177795423934911508.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              double * foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }
   @Test def test_conf11_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_137845515129716280307.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_137845515129716280307.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              double * foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }
   @Test def test_conf11_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_144857706295354985319.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_144857706295354985319.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf11_144857706295354985319.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf11_144857706295354985319.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              double * foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf11_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_158107298149634691378.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_158107298149634691378.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf11_158107298149634691378.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf11_158107298149634691378.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              double * foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf11_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_168280677523527562079.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_168280677523527562079.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 volatile int a = (volatile int) foo();
                                  ^

        */
        correct("""
              double * foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf11_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_175760997134159085147.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_175760997134159085147.c:3:31: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 const int a = (const int) foo();
                               ^

        */
        correct("""
              double * foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf11_18() {
        correct("""
              double * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf11_19() {
        correct("""
              double * foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf11_20() {
        correct("""
              double * foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf12_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_06674944542495993965.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_06674944542495993965.c:5:17: error: aggregate value used where an integer was expected
                 char a = (char) foo();
                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf12_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_19083618235101038169.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_19083618235101038169.c:5:17: error: aggregate value used where an integer was expected
                 signed char a = (signed char) foo();
                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf12_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_22678402278106202997.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_22678402278106202997.c:5:17: error: aggregate value used where an integer was expected
                 unsigned char a = (unsigned char) foo();
                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf12_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_34672069221237504286.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_34672069221237504286.c:5:17: error: aggregate value used where an integer was expected
                 unsigned int a = (unsigned int) foo();
                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf12_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_42286566268535085702.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_42286566268535085702.c:5:17: error: aggregate value used where an integer was expected
                 signed int a = (signed int) foo();
                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf12_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_58373755432163320485.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_58373755432163320485.c:5:17: error: aggregate value used where an integer was expected
                 long a = (long) foo();
                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }
   @Test def test_conf12_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_62332271128984554935.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_62332271128984554935.c:5:17: error: aggregate value used where a float was expected
                 float a = (float) foo();
                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }
   @Test def test_conf12_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_75966980197056481523.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_75966980197056481523.c:5:17: error: aggregate value used where a float was expected
                 double a = (double) foo();
                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf12_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_88890377591965777931.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_88890377591965777931.c:5:17: error: aggregate value used where a float was expected
                 long double a = (long double) foo();
                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf12_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_96842229324272352448.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_96842229324272352448.c:5:17: error: cannot convert to a pointer type
                 int * a = (int *) foo();
                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf12_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_105425209475002263835.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_105425209475002263835.c:5:17: error: cannot convert to a pointer type
                 long * a = (long *) foo();
                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf12_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_116392216541516226476.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_116392216541516226476.c:5:17: error: cannot convert to a pointer type
                 double * a = (double *) foo();
                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf12_12() {
        correct("""
              struct S { int x; int y; };

              struct S foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }
   @Test def test_conf12_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_131124595255120646570.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_131124595255120646570.c:7:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct S { int x; int y; };

              struct S foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }
   @Test def test_conf12_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_148767873517731090748.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_148767873517731090748.c:5:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf12_148767873517731090748.c:6:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf12_148767873517731090748.c:7:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf12_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_155179286626416896865.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_155179286626416896865.c:5:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf12_155179286626416896865.c:6:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf12_155179286626416896865.c:7:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf12_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_161426128138396996897.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_161426128138396996897.c:5:17: error: aggregate value used where an integer was expected
                 volatile int a = (volatile int) foo();
                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf12_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_174360201750154104056.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_174360201750154104056.c:5:17: error: aggregate value used where an integer was expected
                 const int a = (const int) foo();
                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf12_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_187889515970389833307.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_187889515970389833307.c:5:17: error: cannot convert to a pointer type
                 int * a = (int *) foo();
                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf12_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_191320732020878252867.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_191320732020878252867.c:5:17: error: cannot convert to a pointer type
                 const int * a = (const int *) foo();
                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf12_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_208783928725077989148.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_208783928725077989148.c:5:17: error: cannot convert to a pointer type
                 volatile int * a = (volatile int *) foo();
                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf13_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_05651034571960305197.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_05651034571960305197.c:5:17: error: aggregate value used where an integer was expected
                 char a = (char) foo();
                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf13_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_15836794143558525855.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_15836794143558525855.c:5:17: error: aggregate value used where an integer was expected
                 signed char a = (signed char) foo();
                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf13_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_27567379652588004427.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_27567379652588004427.c:5:17: error: aggregate value used where an integer was expected
                 unsigned char a = (unsigned char) foo();
                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf13_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_37846684051606557838.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_37846684051606557838.c:5:17: error: aggregate value used where an integer was expected
                 unsigned int a = (unsigned int) foo();
                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf13_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_41805569730457513638.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_41805569730457513638.c:5:17: error: aggregate value used where an integer was expected
                 signed int a = (signed int) foo();
                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf13_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_53734546236015584063.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_53734546236015584063.c:5:17: error: aggregate value used where an integer was expected
                 long a = (long) foo();
                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }
   @Test def test_conf13_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_67526356848809677269.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_67526356848809677269.c:5:17: error: aggregate value used where a float was expected
                 float a = (float) foo();
                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }
   @Test def test_conf13_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_78714199929062920191.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_78714199929062920191.c:5:17: error: aggregate value used where a float was expected
                 double a = (double) foo();
                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf13_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_81939231187481057420.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_81939231187481057420.c:5:17: error: aggregate value used where a float was expected
                 long double a = (long double) foo();
                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf13_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_95591692702710079958.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_95591692702710079958.c:5:17: error: cannot convert to a pointer type
                 int * a = (int *) foo();
                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf13_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_10492140147174781365.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_10492140147174781365.c:5:17: error: cannot convert to a pointer type
                 long * a = (long *) foo();
                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf13_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_116699733013891848489.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_116699733013891848489.c:5:17: error: cannot convert to a pointer type
                 double * a = (double *) foo();
                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf13_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_12467646443044140902.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_12467646443044140902.c:7:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct S { int x; int y; };

              struct T foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }
   @Test def test_conf13_13() {
        correct("""
              struct T { int x; int y; int z; };

              struct T foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }
   @Test def test_conf13_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_147241102847741927750.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_147241102847741927750.c:5:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf13_147241102847741927750.c:6:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf13_147241102847741927750.c:7:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf13_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_15843296488348198957.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_15843296488348198957.c:5:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf13_15843296488348198957.c:6:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf13_15843296488348198957.c:7:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf13_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_165026390878074544874.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_165026390878074544874.c:5:17: error: aggregate value used where an integer was expected
                 volatile int a = (volatile int) foo();
                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf13_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_174316379208618657242.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_174316379208618657242.c:5:17: error: aggregate value used where an integer was expected
                 const int a = (const int) foo();
                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf13_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_186550215619672306261.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_186550215619672306261.c:5:17: error: cannot convert to a pointer type
                 int * a = (int *) foo();
                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf13_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_196376216552544771122.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_196376216552544771122.c:5:17: error: cannot convert to a pointer type
                 const int * a = (const int *) foo();
                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf13_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_203921742473773437153.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_203921742473773437153.c:5:17: error: cannot convert to a pointer type
                 volatile int * a = (volatile int *) foo();
                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf14_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_01903241099264128053.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_01903241099264128053.c:3:17: error: aggregate value used where an integer was expected
                 char a = (char) foo();
                 ^

        */
        error("""
              struct { int a; } foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf14_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_13832985637505983531.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_13832985637505983531.c:3:17: error: aggregate value used where an integer was expected
                 signed char a = (signed char) foo();
                 ^

        */
        error("""
              struct { int a; } foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf14_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_21838705255544552215.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_21838705255544552215.c:3:17: error: aggregate value used where an integer was expected
                 unsigned char a = (unsigned char) foo();
                 ^

        */
        error("""
              struct { int a; } foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf14_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_38966231869300095155.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_38966231869300095155.c:3:17: error: aggregate value used where an integer was expected
                 unsigned int a = (unsigned int) foo();
                 ^

        */
        error("""
              struct { int a; } foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf14_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_45976121444355748253.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_45976121444355748253.c:3:17: error: aggregate value used where an integer was expected
                 signed int a = (signed int) foo();
                 ^

        */
        error("""
              struct { int a; } foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf14_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_53119470931109876275.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_53119470931109876275.c:3:17: error: aggregate value used where an integer was expected
                 long a = (long) foo();
                 ^

        */
        error("""
              struct { int a; } foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }
   @Test def test_conf14_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_67400857440119725263.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_67400857440119725263.c:3:17: error: aggregate value used where a float was expected
                 float a = (float) foo();
                 ^

        */
        error("""
              struct { int a; } foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }
   @Test def test_conf14_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_74693864945598814526.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_74693864945598814526.c:3:17: error: aggregate value used where a float was expected
                 double a = (double) foo();
                 ^

        */
        error("""
              struct { int a; } foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf14_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_84770566659571517128.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_84770566659571517128.c:3:17: error: aggregate value used where a float was expected
                 long double a = (long double) foo();
                 ^

        */
        error("""
              struct { int a; } foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf14_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_98501576889643602745.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_98501576889643602745.c:3:17: error: cannot convert to a pointer type
                 int * a = (int *) foo();
                 ^

        */
        error("""
              struct { int a; } foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf14_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_10937078019964455240.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_10937078019964455240.c:3:17: error: cannot convert to a pointer type
                 long * a = (long *) foo();
                 ^

        */
        error("""
              struct { int a; } foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf14_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_115714874639089890670.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_115714874639089890670.c:3:17: error: cannot convert to a pointer type
                 double * a = (double *) foo();
                 ^

        */
        error("""
              struct { int a; } foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf14_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_128811664312748576436.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_128811664312748576436.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              struct { int a; } foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }
   @Test def test_conf14_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_132850509932957280646.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_132850509932957280646.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct { int a; } foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }
   @Test def test_conf14_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_143381487793547409604.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_143381487793547409604.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf14_143381487793547409604.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf14_143381487793547409604.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              struct { int a; } foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf14_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_155331037166973959953.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_155331037166973959953.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf14_155331037166973959953.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf14_155331037166973959953.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              struct { int a; } foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf14_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_168841919989891607236.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_168841919989891607236.c:3:17: error: aggregate value used where an integer was expected
                 volatile int a = (volatile int) foo();
                 ^

        */
        error("""
              struct { int a; } foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf14_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_179160506343955766607.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_179160506343955766607.c:3:17: error: aggregate value used where an integer was expected
                 const int a = (const int) foo();
                 ^

        */
        error("""
              struct { int a; } foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf14_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_185233988589571981376.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_185233988589571981376.c:3:17: error: cannot convert to a pointer type
                 int * a = (int *) foo();
                 ^

        */
        error("""
              struct { int a; } foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf14_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_193702859440399576217.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_193702859440399576217.c:3:17: error: cannot convert to a pointer type
                 const int * a = (const int *) foo();
                 ^

        */
        error("""
              struct { int a; } foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf14_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_208388857707417039474.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_208388857707417039474.c:3:17: error: cannot convert to a pointer type
                 volatile int * a = (volatile int *) foo();
                 ^

        */
        error("""
              struct { int a; } foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf15_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_03752114764061948572.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_03752114764061948572.c:3:17: error: aggregate value used where an integer was expected
                 char a = (char) foo();
                 ^

        */
        error("""
              struct { float b; } foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf15_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_11546139161297254450.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_11546139161297254450.c:3:17: error: aggregate value used where an integer was expected
                 signed char a = (signed char) foo();
                 ^

        */
        error("""
              struct { float b; } foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf15_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_23379202753229524113.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_23379202753229524113.c:3:17: error: aggregate value used where an integer was expected
                 unsigned char a = (unsigned char) foo();
                 ^

        */
        error("""
              struct { float b; } foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf15_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_38606018827046734324.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_38606018827046734324.c:3:17: error: aggregate value used where an integer was expected
                 unsigned int a = (unsigned int) foo();
                 ^

        */
        error("""
              struct { float b; } foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf15_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_48672858251968657064.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_48672858251968657064.c:3:17: error: aggregate value used where an integer was expected
                 signed int a = (signed int) foo();
                 ^

        */
        error("""
              struct { float b; } foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf15_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_57634854607131230944.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_57634854607131230944.c:3:17: error: aggregate value used where an integer was expected
                 long a = (long) foo();
                 ^

        */
        error("""
              struct { float b; } foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }
   @Test def test_conf15_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_6122447606261644247.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_6122447606261644247.c:3:17: error: aggregate value used where a float was expected
                 float a = (float) foo();
                 ^

        */
        error("""
              struct { float b; } foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }
   @Test def test_conf15_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_72913968069538886376.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_72913968069538886376.c:3:17: error: aggregate value used where a float was expected
                 double a = (double) foo();
                 ^

        */
        error("""
              struct { float b; } foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf15_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_85680863200261056150.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_85680863200261056150.c:3:17: error: aggregate value used where a float was expected
                 long double a = (long double) foo();
                 ^

        */
        error("""
              struct { float b; } foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf15_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_95971259485592186286.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_95971259485592186286.c:3:17: error: cannot convert to a pointer type
                 int * a = (int *) foo();
                 ^

        */
        error("""
              struct { float b; } foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf15_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_104359232155204685132.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_104359232155204685132.c:3:17: error: cannot convert to a pointer type
                 long * a = (long *) foo();
                 ^

        */
        error("""
              struct { float b; } foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf15_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_116780193041331008736.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_116780193041331008736.c:3:17: error: cannot convert to a pointer type
                 double * a = (double *) foo();
                 ^

        */
        error("""
              struct { float b; } foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf15_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_126703588023948415250.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_126703588023948415250.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              struct { float b; } foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }
   @Test def test_conf15_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_13777834579257991904.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_13777834579257991904.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct { float b; } foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }
   @Test def test_conf15_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_1415632910895973988.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_1415632910895973988.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf15_1415632910895973988.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf15_1415632910895973988.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              struct { float b; } foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf15_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_154191070037785658737.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_154191070037785658737.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf15_154191070037785658737.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf15_154191070037785658737.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              struct { float b; } foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf15_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_161473579870194782208.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_161473579870194782208.c:3:17: error: aggregate value used where an integer was expected
                 volatile int a = (volatile int) foo();
                 ^

        */
        error("""
              struct { float b; } foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf15_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_178783844389169615873.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_178783844389169615873.c:3:17: error: aggregate value used where an integer was expected
                 const int a = (const int) foo();
                 ^

        */
        error("""
              struct { float b; } foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf15_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_181609408325336448413.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_181609408325336448413.c:3:17: error: cannot convert to a pointer type
                 int * a = (int *) foo();
                 ^

        */
        error("""
              struct { float b; } foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf15_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_195025626330504123838.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_195025626330504123838.c:3:17: error: cannot convert to a pointer type
                 const int * a = (const int *) foo();
                 ^

        */
        error("""
              struct { float b; } foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf15_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_20491551807946364886.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_20491551807946364886.c:3:17: error: cannot convert to a pointer type
                 volatile int * a = (volatile int *) foo();
                 ^

        */
        error("""
              struct { float b; } foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf16_0() {
        correct("""
              volatile int foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf16_1() {
        correct("""
              volatile int foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf16_2() {
        correct("""
              volatile int foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf16_3() {
        correct("""
              volatile int foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf16_4() {
        correct("""
              volatile int foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf16_5() {
        correct("""
              volatile int foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }
   @Test def test_conf16_6() {
        correct("""
              volatile int foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }
   @Test def test_conf16_7() {
        correct("""
              volatile int foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf16_8() {
        correct("""
              volatile int foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf16_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_93014247758522649068.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_93014247758522649068.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int * a = (int *) foo();
                           ^

        */
        correct("""
              volatile int foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf16_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_108517170698235197815.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_108517170698235197815.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 long * a = (long *) foo();
                            ^

        */
        correct("""
              volatile int foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf16_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_11238869458200930065.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_11238869458200930065.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 double * a = (double *) foo();
                              ^

        */
        correct("""
              volatile int foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf16_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_121337028798698281446.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_121337028798698281446.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              volatile int foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }
   @Test def test_conf16_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_134700978129785505768.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_134700978129785505768.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              volatile int foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }
   @Test def test_conf16_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_142798964239075240866.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_142798964239075240866.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf16_142798964239075240866.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf16_142798964239075240866.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              volatile int foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf16_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_151666845616882403872.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_151666845616882403872.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf16_151666845616882403872.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf16_151666845616882403872.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              volatile int foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf16_16() {
        correct("""
              volatile int foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf16_17() {
        correct("""
              volatile int foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf16_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_183093155038119758602.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_183093155038119758602.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int * a = (int *) foo();
                           ^

        */
        correct("""
              volatile int foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf16_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_197947681024635888525.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_197947681024635888525.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 const int * a = (const int *) foo();
                                 ^

        */
        correct("""
              volatile int foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf16_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_202747789850502356159.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_202747789850502356159.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 volatile int * a = (volatile int *) foo();
                                    ^

        */
        correct("""
              volatile int foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf17_0() {
        correct("""
              const int foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf17_1() {
        correct("""
              const int foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf17_2() {
        correct("""
              const int foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf17_3() {
        correct("""
              const int foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf17_4() {
        correct("""
              const int foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf17_5() {
        correct("""
              const int foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }
   @Test def test_conf17_6() {
        correct("""
              const int foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }
   @Test def test_conf17_7() {
        correct("""
              const int foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf17_8() {
        correct("""
              const int foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf17_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_97056518870897049416.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_97056518870897049416.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int * a = (int *) foo();
                           ^

        */
        correct("""
              const int foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf17_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_109045253714134932057.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_109045253714134932057.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 long * a = (long *) foo();
                            ^

        */
        correct("""
              const int foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf17_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_113667558066196538998.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_113667558066196538998.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 double * a = (double *) foo();
                              ^

        */
        correct("""
              const int foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf17_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_124566910733452031163.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_124566910733452031163.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              const int foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }
   @Test def test_conf17_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_133247825963570000649.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_133247825963570000649.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              const int foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }
   @Test def test_conf17_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_142716646538279351040.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_142716646538279351040.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf17_142716646538279351040.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf17_142716646538279351040.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              const int foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf17_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_154536203494207071844.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_154536203494207071844.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf17_154536203494207071844.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf17_154536203494207071844.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              const int foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf17_16() {
        correct("""
              const int foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf17_17() {
        correct("""
              const int foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf17_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_183826424046905761366.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_183826424046905761366.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int * a = (int *) foo();
                           ^

        */
        correct("""
              const int foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf17_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_191729332989711206963.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_191729332989711206963.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 const int * a = (const int *) foo();
                                 ^

        */
        correct("""
              const int foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf17_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_205110390487012733360.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_205110390487012733360.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 volatile int * a = (volatile int *) foo();
                                    ^

        */
        correct("""
              const int foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf18_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_06015381478057232608.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_06015381478057232608.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 char a = (char) foo();
                          ^

        */
        correct("""
              int * foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf18_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_11899948746469570748.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_11899948746469570748.c:3:33: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 signed char a = (signed char) foo();
                                 ^

        */
        correct("""
              int * foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf18_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_22359526423607295699.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_22359526423607295699.c:3:35: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 unsigned char a = (unsigned char) foo();
                                   ^

        */
        correct("""
              int * foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf18_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_31984914254724932501.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_31984914254724932501.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 unsigned int a = (unsigned int) foo();
                                  ^

        */
        correct("""
              int * foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf18_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_41751928690396501873.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_41751928690396501873.c:3:32: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 signed int a = (signed int) foo();
                                ^

        */
        correct("""
              int * foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf18_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_52435527960109920941.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_52435527960109920941.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 long a = (long) foo();
                          ^

        */
        correct("""
              int * foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }
   @Test def test_conf18_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_67035912495450764835.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_67035912495450764835.c:3:17: error: pointer value used where a floating point value was expected
                 float a = (float) foo();
                 ^

        */
        error("""
              int * foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }
   @Test def test_conf18_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_77153392248561790750.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_77153392248561790750.c:3:17: error: pointer value used where a floating point value was expected
                 double a = (double) foo();
                 ^

        */
        error("""
              int * foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf18_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_85313670659374907833.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_85313670659374907833.c:3:17: error: pointer value used where a floating point value was expected
                 long double a = (long double) foo();
                 ^

        */
        error("""
              int * foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf18_9() {
        correct("""
              int * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf18_10() {
        correct("""
              int * foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf18_11() {
        correct("""
              int * foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf18_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_128926729721467353199.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_128926729721467353199.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              int * foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }
   @Test def test_conf18_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_137700683155942819792.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_137700683155942819792.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              int * foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }
   @Test def test_conf18_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_146207402229712561567.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_146207402229712561567.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf18_146207402229712561567.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf18_146207402229712561567.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              int * foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf18_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_158707679433894835789.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_158707679433894835789.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf18_158707679433894835789.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf18_158707679433894835789.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              int * foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf18_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_165104531938212464528.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_165104531938212464528.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 volatile int a = (volatile int) foo();
                                  ^

        */
        correct("""
              int * foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf18_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_174646560386127021304.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_174646560386127021304.c:3:31: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 const int a = (const int) foo();
                               ^

        */
        correct("""
              int * foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf18_18() {
        correct("""
              int * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf18_19() {
        correct("""
              int * foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf18_20() {
        correct("""
              int * foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf19_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_05816676508258285943.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_05816676508258285943.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 char a = (char) foo();
                          ^

        */
        correct("""
              const int * foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf19_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_19048090237601812886.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_19048090237601812886.c:3:33: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 signed char a = (signed char) foo();
                                 ^

        */
        correct("""
              const int * foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf19_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_27904745099519127457.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_27904745099519127457.c:3:35: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 unsigned char a = (unsigned char) foo();
                                   ^

        */
        correct("""
              const int * foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf19_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_3536320810483193430.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_3536320810483193430.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 unsigned int a = (unsigned int) foo();
                                  ^

        */
        correct("""
              const int * foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf19_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_47225223645170871334.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_47225223645170871334.c:3:32: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 signed int a = (signed int) foo();
                                ^

        */
        correct("""
              const int * foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf19_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_58541875368125854079.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_58541875368125854079.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 long a = (long) foo();
                          ^

        */
        correct("""
              const int * foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }
   @Test def test_conf19_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_62370872973521852568.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_62370872973521852568.c:3:17: error: pointer value used where a floating point value was expected
                 float a = (float) foo();
                 ^

        */
        error("""
              const int * foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }
   @Test def test_conf19_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_79007845080944873484.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_79007845080944873484.c:3:17: error: pointer value used where a floating point value was expected
                 double a = (double) foo();
                 ^

        */
        error("""
              const int * foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf19_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_84313588954693876686.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_84313588954693876686.c:3:17: error: pointer value used where a floating point value was expected
                 long double a = (long double) foo();
                 ^

        */
        error("""
              const int * foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf19_9() {
        correct("""
              const int * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf19_10() {
        correct("""
              const int * foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf19_11() {
        correct("""
              const int * foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf19_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_121499459922043561956.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_121499459922043561956.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              const int * foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }
   @Test def test_conf19_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_132725587605026695192.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_132725587605026695192.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              const int * foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }
   @Test def test_conf19_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_147127469865067779040.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_147127469865067779040.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf19_147127469865067779040.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf19_147127469865067779040.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              const int * foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf19_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_15431515838610439496.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_15431515838610439496.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf19_15431515838610439496.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf19_15431515838610439496.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              const int * foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf19_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_163024969578031669570.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_163024969578031669570.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 volatile int a = (volatile int) foo();
                                  ^

        */
        correct("""
              const int * foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf19_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_177029017930073942124.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_177029017930073942124.c:3:31: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 const int a = (const int) foo();
                               ^

        */
        correct("""
              const int * foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf19_18() {
        correct("""
              const int * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf19_19() {
        correct("""
              const int * foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf19_20() {
        correct("""
              const int * foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf20_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_06360782139691833391.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_06360782139691833391.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 char a = (char) foo();
                          ^

        */
        correct("""
              volatile int * foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf20_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_16374694558938955885.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_16374694558938955885.c:3:33: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 signed char a = (signed char) foo();
                                 ^

        */
        correct("""
              volatile int * foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf20_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_28658360982668055745.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_28658360982668055745.c:3:35: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 unsigned char a = (unsigned char) foo();
                                   ^

        */
        correct("""
              volatile int * foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }
   @Test def test_conf20_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_34649869052693000026.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_34649869052693000026.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 unsigned int a = (unsigned int) foo();
                                  ^

        */
        correct("""
              volatile int * foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf20_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_44321281209128258763.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_44321281209128258763.c:3:32: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 signed int a = (signed int) foo();
                                ^

        */
        correct("""
              volatile int * foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf20_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_58053371734962828137.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_58053371734962828137.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 long a = (long) foo();
                          ^

        */
        correct("""
              volatile int * foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }
   @Test def test_conf20_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_66158786819547198411.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_66158786819547198411.c:3:17: error: pointer value used where a floating point value was expected
                 float a = (float) foo();
                 ^

        */
        error("""
              volatile int * foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }
   @Test def test_conf20_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_74595127192942795617.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_74595127192942795617.c:3:17: error: pointer value used where a floating point value was expected
                 double a = (double) foo();
                 ^

        */
        error("""
              volatile int * foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf20_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_87000144409972568736.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_87000144409972568736.c:3:17: error: pointer value used where a floating point value was expected
                 long double a = (long double) foo();
                 ^

        */
        error("""
              volatile int * foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }
   @Test def test_conf20_9() {
        correct("""
              volatile int * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf20_10() {
        correct("""
              volatile int * foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf20_11() {
        correct("""
              volatile int * foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf20_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_123013474811633785040.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_123013474811633785040.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              volatile int * foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }
   @Test def test_conf20_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_134132406171378752122.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_134132406171378752122.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              volatile int * foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }
   @Test def test_conf20_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_143018869759434704671.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_143018869759434704671.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf20_143018869759434704671.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf20_143018869759434704671.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              volatile int * foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf20_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_158491167256227737808.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_158491167256227737808.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf20_158491167256227737808.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf20_158491167256227737808.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              volatile int * foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }
   @Test def test_conf20_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_166479943926843284028.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_166479943926843284028.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 volatile int a = (volatile int) foo();
                                  ^

        */
        correct("""
              volatile int * foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf20_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_173729913019407926323.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_173729913019407926323.c:3:31: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 const int a = (const int) foo();
                               ^

        */
        correct("""
              volatile int * foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }
   @Test def test_conf20_18() {
        correct("""
              volatile int * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf20_19() {
        correct("""
              volatile int * foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }
   @Test def test_conf20_20() {
        correct("""
              volatile int * foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }


}