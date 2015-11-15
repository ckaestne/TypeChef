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
C:\Users\ckaestne\AppData\Local\Temp\conf0_94765824219190310498.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_94765824219190310498.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_107048058756408515291.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_107048058756408515291.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_111201693299011265779.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_111201693299011265779.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_124451230552420165533.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_124451230552420165533.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_13530879374834803647.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_13530879374834803647.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_144521293126970453294.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_144521293126970453294.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf0_144521293126970453294.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf0_144521293126970453294.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_156681839173724548603.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_156681839173724548603.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf0_156681839173724548603.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf0_156681839173724548603.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
        correct("""
              char foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf0_19() {
        correct("""
              char foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf0_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_203638473156235961863.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_203638473156235961863.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf0_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_212355293911759693894.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_212355293911759693894.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf0_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_225166629856954726927.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_225166629856954726927.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_92832209837391162962.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_92832209837391162962.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_101889285833541876311.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_101889285833541876311.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_117225697151534765297.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_117225697151534765297.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_124893218341567999575.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_124893218341567999575.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_136059182304879913521.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_136059182304879913521.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_148995050353468660518.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_148995050353468660518.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf1_148995050353468660518.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf1_148995050353468660518.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_155013680924097610805.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_155013680924097610805.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf1_155013680924097610805.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf1_155013680924097610805.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
        correct("""
              signed char foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf1_19() {
        correct("""
              signed char foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf1_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_204789233252832438146.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_204789233252832438146.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf1_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_213080355837227564266.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_213080355837227564266.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf1_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_222503353880443869569.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_222503353880443869569.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_95962966725225804259.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_95962966725225804259.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_10826756404328452214.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_10826756404328452214.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_111520151175757755544.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_111520151175757755544.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_128227948754345353646.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_128227948754345353646.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_136403643634365748801.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_136403643634365748801.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_148647939720997560249.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_148647939720997560249.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf2_148647939720997560249.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf2_148647939720997560249.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_152028203642219579604.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_152028203642219579604.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf2_152028203642219579604.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf2_152028203642219579604.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
        correct("""
              unsigned char foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf2_19() {
        correct("""
              unsigned char foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf2_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_208434715232373742186.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_208434715232373742186.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf2_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_218049593456852090627.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_218049593456852090627.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf2_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_225177834376188252489.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_225177834376188252489.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_92320900133994196123.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_92320900133994196123.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_102482329759343070434.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_102482329759343070434.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_111072775872001683779.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_111072775872001683779.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_124844410840926808008.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_124844410840926808008.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_134661951404993854856.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_134661951404993854856.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_142680758652806702706.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_142680758652806702706.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf3_142680758652806702706.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf3_142680758652806702706.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_151247190376100511087.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_151247190376100511087.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf3_151247190376100511087.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf3_151247190376100511087.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
        correct("""
              unsigned int foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf3_19() {
        correct("""
              unsigned int foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf3_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_206208511677904881628.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_206208511677904881628.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf3_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_213549181937188929502.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_213549181937188929502.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf3_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_223255329730086019632.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_223255329730086019632.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_91766657195988210925.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_91766657195988210925.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_101895969746776891131.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_101895969746776891131.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_118512949911635354332.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_118512949911635354332.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_124703727607498442673.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_124703727607498442673.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_135969584194927344495.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_135969584194927344495.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_143385925513863226011.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_143385925513863226011.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf4_143385925513863226011.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf4_143385925513863226011.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_151133584547326393338.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_151133584547326393338.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf4_151133584547326393338.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf4_151133584547326393338.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
        correct("""
              signed int foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf4_19() {
        correct("""
              signed int foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf4_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_203713087576493853760.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_203713087576493853760.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf4_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_215902607069912362563.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_215902607069912362563.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf4_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_222226768943129310.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_222226768943129310.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_98973506818828898438.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_98973506818828898438.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_104475086661508743544.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_104475086661508743544.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_113684688079510984085.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_113684688079510984085.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_127131280170169223799.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_127131280170169223799.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_131669915840286979702.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_131669915840286979702.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_144703421189417854417.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_144703421189417854417.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf5_144703421189417854417.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf5_144703421189417854417.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_152241818048194027425.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_152241818048194027425.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf5_152241818048194027425.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf5_152241818048194027425.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
        correct("""
              long foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf5_19() {
        correct("""
              long foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf5_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_205095555559046683526.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_205095555559046683526.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf5_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_211316775503521150866.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_211316775503521150866.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf5_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_225024534586900927309.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_225024534586900927309.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_97142623796900236139.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_97142623796900236139.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_107130194554076877803.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_107130194554076877803.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_114077235262067695046.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_114077235262067695046.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_121885814111955920545.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_121885814111955920545.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_134712948061319567068.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_134712948061319567068.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_142615938530407156607.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_142615938530407156607.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf6_142615938530407156607.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf6_142615938530407156607.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_158051799602762779602.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_158051799602762779602.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf6_158051799602762779602.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf6_158051799602762779602.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
        correct("""
              float foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf6_19() {
        correct("""
              float foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf6_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_203254951004963965156.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_203254951004963965156.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf6_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_21817316529972227850.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_21817316529972227850.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf6_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_221493299295517274071.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_221493299295517274071.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_94282276500831406471.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_94282276500831406471.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_104639865781580233930.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_104639865781580233930.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_117771201838214295320.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_117771201838214295320.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_121831497836022983415.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_121831497836022983415.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_139065912431621350501.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_139065912431621350501.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_148651241093705324214.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_148651241093705324214.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf7_148651241093705324214.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf7_148651241093705324214.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_154997668883122903038.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_154997668883122903038.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf7_154997668883122903038.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf7_154997668883122903038.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
        correct("""
              double foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf7_19() {
        correct("""
              double foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf7_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_204516534278302822259.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_204516534278302822259.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf7_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_217613649339519378748.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_217613649339519378748.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf7_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_226155357828233391049.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_226155357828233391049.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_93946045322206870921.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_93946045322206870921.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_105073335391329809726.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_105073335391329809726.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_116853328252256941653.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_116853328252256941653.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_124538733169941644964.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_124538733169941644964.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_138273056437440789598.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_138273056437440789598.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_147646634272388950445.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_147646634272388950445.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf8_147646634272388950445.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf8_147646634272388950445.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_15489726402284420898.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_15489726402284420898.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf8_15489726402284420898.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf8_15489726402284420898.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
        correct("""
              long double foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf8_19() {
        correct("""
              long double foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf8_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_206947173639480338013.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_206947173639480338013.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf8_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_218774520021182433636.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_218774520021182433636.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf8_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_228646852989128165908.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_228646852989128165908.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_0225771869668179889.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_0225771869668179889.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_14066703366251085878.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_14066703366251085878.c:3:33: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_26017540179120616641.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_26017540179120616641.c:3:35: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_35368684828958500429.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_35368684828958500429.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_45040228134234168928.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_45040228134234168928.c:3:32: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_57381832201740846853.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_57381832201740846853.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_68165344987858802663.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_68165344987858802663.c:3:17: error: pointer value used where a floating point value was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_74380783177874134807.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_74380783177874134807.c:3:17: error: pointer value used where a floating point value was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_88343775917319730571.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_88343775917319730571.c:3:17: error: pointer value used where a floating point value was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_126700014865971398611.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_126700014865971398611.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_138822907291819443804.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_138822907291819443804.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_147826188719281328912.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_147826188719281328912.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf9_147826188719281328912.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf9_147826188719281328912.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_151994128415301597659.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_151994128415301597659.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf9_151994128415301597659.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf9_151994128415301597659.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_166141913250890396109.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_166141913250890396109.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_174731060045599652862.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_174731060045599652862.c:3:31: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_18623732390677944480.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_18623732390677944480.c:3:17: error: pointer value used where a floating point value was expected
                 const double a = (const double) foo();
                 ^

        */
        error("""
              int * foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf9_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_192418447723176295448.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_192418447723176295448.c:3:17: error: pointer value used where a floating point value was expected
                 volatile double a = (volatile double) foo();
                 ^

        */
        error("""
              int * foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf9_20() {
        correct("""
              int * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf9_21() {
        correct("""
              int * foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf9_22() {
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_02402703419769420708.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_02402703419769420708.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_1118688270888807074.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_1118688270888807074.c:3:33: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_25329738120123887829.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_25329738120123887829.c:3:35: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_3447253280879274321.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_3447253280879274321.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_4688422840389867537.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_4688422840389867537.c:3:32: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_54389042662831253619.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_54389042662831253619.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_61522194517381870356.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_61522194517381870356.c:3:17: error: pointer value used where a floating point value was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_77639575528746164168.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_77639575528746164168.c:3:17: error: pointer value used where a floating point value was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_8571779672780065477.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_8571779672780065477.c:3:17: error: pointer value used where a floating point value was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_124879050438972849971.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_124879050438972849971.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_132202418959428534064.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_132202418959428534064.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_145519920007512326530.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_145519920007512326530.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf10_145519920007512326530.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf10_145519920007512326530.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_157042285677051361983.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_157042285677051361983.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf10_157042285677051361983.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf10_157042285677051361983.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_168506519152450586469.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_168506519152450586469.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_174204993420723502768.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_174204993420723502768.c:3:31: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_186194140085066909423.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_186194140085066909423.c:3:17: error: pointer value used where a floating point value was expected
                 const double a = (const double) foo();
                 ^

        */
        error("""
              long * foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_196960902341372253887.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_196960902341372253887.c:3:17: error: pointer value used where a floating point value was expected
                 volatile double a = (volatile double) foo();
                 ^

        */
        error("""
              long * foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_20() {
        correct("""
              long * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_21() {
        correct("""
              long * foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_22() {
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_02062270856602044439.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_02062270856602044439.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_1136848919074274435.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_1136848919074274435.c:3:33: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_23953842003428910719.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_23953842003428910719.c:3:35: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_33117035071668498293.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_33117035071668498293.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_43946658140886407776.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_43946658140886407776.c:3:32: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_51528631141901893788.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_51528631141901893788.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_61488768313577092341.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_61488768313577092341.c:3:17: error: pointer value used where a floating point value was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_71355439915516637309.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_71355439915516637309.c:3:17: error: pointer value used where a floating point value was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_85849923043476084163.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_85849923043476084163.c:3:17: error: pointer value used where a floating point value was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_122324645026505862451.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_122324645026505862451.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_13240599373184526695.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_13240599373184526695.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_141321018511104156547.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_141321018511104156547.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf11_141321018511104156547.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf11_141321018511104156547.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_155516510872796857561.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_155516510872796857561.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf11_155516510872796857561.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf11_155516510872796857561.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_165843227445379563923.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_165843227445379563923.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_178643693347517970934.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_178643693347517970934.c:3:31: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_183634473193012449623.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_183634473193012449623.c:3:17: error: pointer value used where a floating point value was expected
                 const double a = (const double) foo();
                 ^

        */
        error("""
              double * foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_195183074296529649265.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_195183074296529649265.c:3:17: error: pointer value used where a floating point value was expected
                 volatile double a = (volatile double) foo();
                 ^

        */
        error("""
              double * foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_20() {
        correct("""
              double * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_21() {
        correct("""
              double * foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_22() {
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_03113072830541254741.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_03113072830541254741.c:5:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_13921190139169001843.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_13921190139169001843.c:5:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_21167848043678809988.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_21167848043678809988.c:5:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_35645807242923575413.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_35645807242923575413.c:5:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_47669182902705895992.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_47669182902705895992.c:5:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_52341832125412384853.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_52341832125412384853.c:5:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_6875118447423558306.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_6875118447423558306.c:5:17: error: aggregate value used where a float was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_72730068098172023066.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_72730068098172023066.c:5:17: error: aggregate value used where a float was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_85522310431996384228.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_85522310431996384228.c:5:17: error: aggregate value used where a float was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_97691364926435239997.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_97691364926435239997.c:5:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_105319449024308615286.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_105319449024308615286.c:5:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_117672028389718125334.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_117672028389718125334.c:5:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_139118131481285487663.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_139118131481285487663.c:7:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_142390949246563761014.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_142390949246563761014.c:5:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf12_142390949246563761014.c:6:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf12_142390949246563761014.c:7:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_15452155362338216146.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_15452155362338216146.c:5:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf12_15452155362338216146.c:6:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf12_15452155362338216146.c:7:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_163941948370848497867.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_163941948370848497867.c:5:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_176217347798193008081.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_176217347798193008081.c:5:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_188254532213658026073.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_188254532213658026073.c:5:17: error: aggregate value used where a float was expected
                 const double a = (const double) foo();
                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf12_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_192960231938790821502.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_192960231938790821502.c:5:17: error: aggregate value used where a float was expected
                 volatile double a = (volatile double) foo();
                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf12_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_201854019691472242200.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_201854019691472242200.c:5:17: error: cannot convert to a pointer type
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


   @Test def test_conf12_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_211076149639502008155.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_211076149639502008155.c:5:17: error: cannot convert to a pointer type
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


   @Test def test_conf12_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_228206640114067506016.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_228206640114067506016.c:5:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_03884302241758579839.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_03884302241758579839.c:5:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_1343706106304304180.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_1343706106304304180.c:5:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_26027877573054728033.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_26027877573054728033.c:5:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_37532940029548317113.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_37532940029548317113.c:5:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_44147788431497178709.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_44147788431497178709.c:5:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_51597467777720049899.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_51597467777720049899.c:5:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_62488427173115678985.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_62488427173115678985.c:5:17: error: aggregate value used where a float was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_7692787881041014916.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_7692787881041014916.c:5:17: error: aggregate value used where a float was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_88131382307807934455.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_88131382307807934455.c:5:17: error: aggregate value used where a float was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_91561649474635202985.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_91561649474635202985.c:5:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_101073268639202970190.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_101073268639202970190.c:5:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_116375445853405987988.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_116375445853405987988.c:5:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_124407640608421177969.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_124407640608421177969.c:7:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_141844483096162181849.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_141844483096162181849.c:5:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf13_141844483096162181849.c:6:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf13_141844483096162181849.c:7:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_157780704792799996167.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_157780704792799996167.c:5:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf13_157780704792799996167.c:6:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf13_157780704792799996167.c:7:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_167501374331548761535.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_167501374331548761535.c:5:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_179138174221174652882.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_179138174221174652882.c:5:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_183160914161049747244.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_183160914161049747244.c:5:17: error: aggregate value used where a float was expected
                 const double a = (const double) foo();
                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf13_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_199170295516949752028.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_199170295516949752028.c:5:17: error: aggregate value used where a float was expected
                 volatile double a = (volatile double) foo();
                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf13_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_201104136300197303179.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_201104136300197303179.c:5:17: error: cannot convert to a pointer type
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


   @Test def test_conf13_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_212786796828427330167.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_212786796828427330167.c:5:17: error: cannot convert to a pointer type
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


   @Test def test_conf13_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_227302676041659518618.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_227302676041659518618.c:5:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_02050932855395287443.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_02050932855395287443.c:3:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_15342343873530087977.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_15342343873530087977.c:3:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_26163816621026551382.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_26163816621026551382.c:3:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_32055196555904479379.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_32055196555904479379.c:3:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_45958923639282096461.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_45958923639282096461.c:3:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_51171032687044994463.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_51171032687044994463.c:3:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_66890018721733217476.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_66890018721733217476.c:3:17: error: aggregate value used where a float was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_75256081648053302563.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_75256081648053302563.c:3:17: error: aggregate value used where a float was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_84978593385077173584.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_84978593385077173584.c:3:17: error: aggregate value used where a float was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_97391826674971080234.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_97391826674971080234.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_106887596592018247846.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_106887596592018247846.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_11377261597456041427.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_11377261597456041427.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_125228492876098904933.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_125228492876098904933.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_13613323911467186448.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_13613323911467186448.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_149163880305720553566.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_149163880305720553566.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf14_149163880305720553566.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf14_149163880305720553566.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_157807524980532498681.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_157807524980532498681.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf14_157807524980532498681.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf14_157807524980532498681.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_168239078562273332813.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_168239078562273332813.c:3:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_17415499784104725077.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_17415499784104725077.c:3:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_181262264023858743401.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_181262264023858743401.c:3:17: error: aggregate value used where a float was expected
                 const double a = (const double) foo();
                 ^

        */
        error("""
              struct { int a; } foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf14_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_192826156176791127305.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_192826156176791127305.c:3:17: error: aggregate value used where a float was expected
                 volatile double a = (volatile double) foo();
                 ^

        */
        error("""
              struct { int a; } foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf14_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_206373764880493649757.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_206373764880493649757.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf14_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_218684661485161548329.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_218684661485161548329.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf14_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_22296478280493031187.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_22296478280493031187.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_03637582129774680577.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_03637582129774680577.c:3:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_12979260768525508334.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_12979260768525508334.c:3:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_26191816023661135126.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_26191816023661135126.c:3:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_33050718110775120069.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_33050718110775120069.c:3:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_48014038489007664680.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_48014038489007664680.c:3:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_52623949881873099854.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_52623949881873099854.c:3:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_61593037314094426787.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_61593037314094426787.c:3:17: error: aggregate value used where a float was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_78815144776696931979.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_78815144776696931979.c:3:17: error: aggregate value used where a float was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_86647985520103401631.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_86647985520103401631.c:3:17: error: aggregate value used where a float was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_98560800709006676950.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_98560800709006676950.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_102042237971438179164.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_102042237971438179164.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_117606444574778841740.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_117606444574778841740.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_125899135269073929394.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_125899135269073929394.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_138811461668977066015.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_138811461668977066015.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_143658218413699484705.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_143658218413699484705.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf15_143658218413699484705.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf15_143658218413699484705.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_157923765429688235958.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_157923765429688235958.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf15_157923765429688235958.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf15_157923765429688235958.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_162024644896276323382.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_162024644896276323382.c:3:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_178291842987526705142.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_178291842987526705142.c:3:17: error: aggregate value used where an integer was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_183036269055974066501.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_183036269055974066501.c:3:17: error: aggregate value used where a float was expected
                 const double a = (const double) foo();
                 ^

        */
        error("""
              struct { float b; } foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_193452488066993580154.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_193452488066993580154.c:3:17: error: aggregate value used where a float was expected
                 volatile double a = (volatile double) foo();
                 ^

        */
        error("""
              struct { float b; } foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_203646167121934802054.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_203646167121934802054.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf15_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_216847178751358839160.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_216847178751358839160.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf15_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_225786805798184388670.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_225786805798184388670.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_94206034349607592383.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_94206034349607592383.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_108980150899284708199.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_108980150899284708199.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_114385237313792987158.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_114385237313792987158.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_12485284716360169662.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_12485284716360169662.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_13534832543240771471.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_13534832543240771471.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_146793210867391849736.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_146793210867391849736.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf16_146793210867391849736.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf16_146793210867391849736.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_159092856166802574842.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_159092856166802574842.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf16_159092856166802574842.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf16_159092856166802574842.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
        correct("""
              volatile int foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf16_19() {
        correct("""
              volatile int foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf16_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_205833779160943451330.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_205833779160943451330.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf16_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_215212489733421905421.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_215212489733421905421.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf16_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_223411403490811922855.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_223411403490811922855.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_91888495469027605697.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_91888495469027605697.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_103053926878237123918.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_103053926878237123918.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_116639818228825480981.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_116639818228825480981.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_124010234483294230176.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_124010234483294230176.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_13195031240430754657.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_13195031240430754657.c:5:38: error: conversion to non-scalar type requested
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_144465614661402234891.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_144465614661402234891.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf17_144465614661402234891.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf17_144465614661402234891.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_152611400216298853656.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_152611400216298853656.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf17_152611400216298853656.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf17_152611400216298853656.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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
        correct("""
              const int foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf17_19() {
        correct("""
              const int foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf17_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_20523718319586687928.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_20523718319586687928.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf17_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_215076251425053512709.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_215076251425053512709.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf17_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_229167941472426331235.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_229167941472426331235.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
        correct("""
              const double foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_1() {
        correct("""
              const double foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_2() {
        correct("""
              const double foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_3() {
        correct("""
              const double foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_4() {
        correct("""
              const double foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_5() {
        correct("""
              const double foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_6() {
        correct("""
              const double foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_7() {
        correct("""
              const double foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_8() {
        correct("""
              const double foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_95447433006841723556.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_95447433006841723556.c:3:17: error: cannot convert to a pointer type
                 int * a = (int *) foo();
                 ^

        */
        error("""
              const double foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_102484510689982406303.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_102484510689982406303.c:3:17: error: cannot convert to a pointer type
                 long * a = (long *) foo();
                 ^

        */
        error("""
              const double foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_116597282649027548557.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_116597282649027548557.c:3:17: error: cannot convert to a pointer type
                 double * a = (double *) foo();
                 ^

        */
        error("""
              const double foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_128541271632229666062.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_128541271632229666062.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              const double foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_133978745872799450723.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_133978745872799450723.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              const double foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_147578180878074672075.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_147578180878074672075.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf18_147578180878074672075.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf18_147578180878074672075.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              const double foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_156490894758895423500.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_156490894758895423500.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf18_156490894758895423500.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf18_156490894758895423500.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              const double foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_16() {
        correct("""
              const double foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_17() {
        correct("""
              const double foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_18() {
        correct("""
              const double foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_19() {
        correct("""
              const double foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_202203252860130535577.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_202203252860130535577.c:3:17: error: cannot convert to a pointer type
                 int * a = (int *) foo();
                 ^

        */
        error("""
              const double foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_212248677761343928745.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_212248677761343928745.c:3:17: error: cannot convert to a pointer type
                 const int * a = (const int *) foo();
                 ^

        */
        error("""
              const double foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_224159656329227428074.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_224159656329227428074.c:3:17: error: cannot convert to a pointer type
                 volatile int * a = (volatile int *) foo();
                 ^

        */
        error("""
              const double foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_0() {
        correct("""
              volatile double foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_1() {
        correct("""
              volatile double foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_2() {
        correct("""
              volatile double foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_3() {
        correct("""
              volatile double foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_4() {
        correct("""
              volatile double foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_5() {
        correct("""
              volatile double foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_6() {
        correct("""
              volatile double foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_7() {
        correct("""
              volatile double foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_8() {
        correct("""
              volatile double foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_9592261852324175088.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_9592261852324175088.c:3:17: error: cannot convert to a pointer type
                 int * a = (int *) foo();
                 ^

        */
        error("""
              volatile double foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_10255045243538419180.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_10255045243538419180.c:3:17: error: cannot convert to a pointer type
                 long * a = (long *) foo();
                 ^

        */
        error("""
              volatile double foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_117627555137821868894.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_117627555137821868894.c:3:17: error: cannot convert to a pointer type
                 double * a = (double *) foo();
                 ^

        */
        error("""
              volatile double foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_128169629456424989174.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_128169629456424989174.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              volatile double foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_13300412073325931077.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_13300412073325931077.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              volatile double foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_144334487011529510881.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_144334487011529510881.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf19_144334487011529510881.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf19_144334487011529510881.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              volatile double foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_155099215697765939978.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_155099215697765939978.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf19_155099215697765939978.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf19_155099215697765939978.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              volatile double foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_16() {
        correct("""
              volatile double foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_17() {
        correct("""
              volatile double foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_18() {
        correct("""
              volatile double foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_19() {
        correct("""
              volatile double foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_201436919796944715777.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_201436919796944715777.c:3:17: error: cannot convert to a pointer type
                 int * a = (int *) foo();
                 ^

        */
        error("""
              volatile double foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_214529513178980887071.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_214529513178980887071.c:3:17: error: cannot convert to a pointer type
                 const int * a = (const int *) foo();
                 ^

        */
        error("""
              volatile double foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_225685531268350187869.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_225685531268350187869.c:3:17: error: cannot convert to a pointer type
                 volatile int * a = (volatile int *) foo();
                 ^

        */
        error("""
              volatile double foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_05276036493357283047.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_05276036493357283047.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf20_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_13152091703717049714.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_13152091703717049714.c:3:33: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf20_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_28632653068210228993.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_28632653068210228993.c:3:35: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf20_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_3359931779464820317.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_3359931779464820317.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf20_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_41564692217494693659.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_41564692217494693659.c:3:32: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf20_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_51646381443565515831.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_51646381443565515831.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf20_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_62518632266527160957.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_62518632266527160957.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf20_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_73297000652512742522.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_73297000652512742522.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf20_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_83154294201034139234.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_83154294201034139234.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf20_9() {
        correct("""
              int * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_10() {
        correct("""
              int * foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_11() {
        correct("""
              int * foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_121470582358939513837.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_121470582358939513837.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf20_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_134739035404554834350.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_134739035404554834350.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf20_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_143459433990847287234.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_143459433990847287234.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf20_143459433990847287234.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf20_143459433990847287234.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf20_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_157131928599667608478.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_157131928599667608478.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf20_157131928599667608478.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf20_157131928599667608478.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf20_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_166958319211057592595.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_166958319211057592595.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf20_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_173912181285516313389.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_173912181285516313389.c:3:31: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf20_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_186033058945649968440.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_186033058945649968440.c:3:17: error: pointer value used where a floating point value was expected
                 const double a = (const double) foo();
                 ^

        */
        error("""
              int * foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_197441130005926897170.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_197441130005926897170.c:3:17: error: pointer value used where a floating point value was expected
                 volatile double a = (volatile double) foo();
                 ^

        */
        error("""
              int * foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_20() {
        correct("""
              int * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_21() {
        correct("""
              int * foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_22() {
        correct("""
              int * foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_06470385810768610083.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_06470385810768610083.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf21_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_13586635809549417251.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_13586635809549417251.c:3:33: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf21_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_23475664444242421881.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_23475664444242421881.c:3:35: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf21_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_36886285099106025585.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_36886285099106025585.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf21_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_41666433942425556244.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_41666433942425556244.c:3:32: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf21_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_58556082793077526320.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_58556082793077526320.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf21_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_65903858295456145740.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_65903858295456145740.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf21_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_77559225750136130248.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_77559225750136130248.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf21_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_85653744284866169508.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_85653744284866169508.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf21_9() {
        correct("""
              const int * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_10() {
        correct("""
              const int * foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_11() {
        correct("""
              const int * foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_124465228016462018058.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_124465228016462018058.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf21_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_13933309400763492622.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_13933309400763492622.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf21_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_149147261922477850780.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_149147261922477850780.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf21_149147261922477850780.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf21_149147261922477850780.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf21_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_156907504598679405799.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_156907504598679405799.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf21_156907504598679405799.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf21_156907504598679405799.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf21_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_165144451872419857378.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_165144451872419857378.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf21_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_179170559383218937859.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_179170559383218937859.c:3:31: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf21_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_183861206252880040382.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_183861206252880040382.c:3:17: error: pointer value used where a floating point value was expected
                 const double a = (const double) foo();
                 ^

        */
        error("""
              const int * foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_195683944259743309966.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_195683944259743309966.c:3:17: error: pointer value used where a floating point value was expected
                 volatile double a = (volatile double) foo();
                 ^

        */
        error("""
              const int * foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_20() {
        correct("""
              const int * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_21() {
        correct("""
              const int * foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_22() {
        correct("""
              const int * foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf22_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_06314842841115909857.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_06314842841115909857.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf22_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_11765576408481494695.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_11765576408481494695.c:3:33: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf22_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_27529733369592936898.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_27529733369592936898.c:3:35: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf22_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_36892532004620811779.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_36892532004620811779.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf22_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_4354031740980523389.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_4354031740980523389.c:3:32: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf22_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_53232446094734431725.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_53232446094734431725.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf22_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_65857181479305969719.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_65857181479305969719.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf22_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_78942973585654083071.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_78942973585654083071.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf22_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_84885049466526814962.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_84885049466526814962.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf22_9() {
        correct("""
              volatile int * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf22_10() {
        correct("""
              volatile int * foo();
              long * bar() {
                long * a = (long *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf22_11() {
        correct("""
              volatile int * foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf22_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_122358673735961639658.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_122358673735961639658.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf22_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_135982005126937734936.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_135982005126937734936.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf22_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_148104261673436264406.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_148104261673436264406.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf22_148104261673436264406.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf22_148104261673436264406.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf22_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_155803775043573280083.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_155803775043573280083.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf22_155803775043573280083.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf22_155803775043573280083.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf22_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_167794250830830796115.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_167794250830830796115.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf22_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_175799359294990844565.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_175799359294990844565.c:3:31: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf22_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_183432014692955644489.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_183432014692955644489.c:3:17: error: pointer value used where a floating point value was expected
                 const double a = (const double) foo();
                 ^

        */
        error("""
              volatile int * foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf22_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_191072200787765572841.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_191072200787765572841.c:3:17: error: pointer value used where a floating point value was expected
                 volatile double a = (volatile double) foo();
                 ^

        */
        error("""
              volatile int * foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf22_20() {
        correct("""
              volatile int * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf22_21() {
        correct("""
              volatile int * foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf22_22() {
        correct("""
              volatile int * foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }




}