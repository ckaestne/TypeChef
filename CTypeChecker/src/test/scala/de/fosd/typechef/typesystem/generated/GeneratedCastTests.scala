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
C:\Users\ckaestne\AppData\Local\Temp\conf0_97588790794135455693.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_97588790794135455693.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_10579941978787748904.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_10579941978787748904.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int ** a = (int **) foo();
                            ^

        */
        correct("""
              char foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf0_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_118740010698097736688.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_118740010698097736688.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 char * a = (char *) foo();
                            ^

        */
        correct("""
              char foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf0_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_122847690999132752935.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_122847690999132752935.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf0_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_132274398975380654690.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_132274398975380654690.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf0_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_143450387788072206043.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_143450387788072206043.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf0_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_154916108036654434253.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_154916108036654434253.c:5:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              char foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf0_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_16392933649341411338.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_16392933649341411338.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf0_16392933649341411338.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf0_16392933649341411338.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf0_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_177361167460495007558.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_177361167460495007558.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf0_177361167460495007558.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf0_177361167460495007558.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf0_18() {
        correct("""
              char foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf0_19() {
        correct("""
              char foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf0_20() {
        correct("""
              char foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf0_21() {
        correct("""
              char foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf0_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_224433316985299241212.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_224433316985299241212.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf0_23() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_238072117806069959944.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_238072117806069959944.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf0_24() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_245322244899712226054.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf0_245322244899712226054.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_936080512871670098.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_936080512871670098.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_107776578066113371227.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_107776578066113371227.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int ** a = (int **) foo();
                            ^

        */
        correct("""
              signed char foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf1_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_114504811998529142564.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_114504811998529142564.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 char * a = (char *) foo();
                            ^

        */
        correct("""
              signed char foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf1_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_123293076515019986003.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_123293076515019986003.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf1_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_134395094012272613971.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_134395094012272613971.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf1_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_141920627101977050522.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_141920627101977050522.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf1_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_157879937053890053313.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_157879937053890053313.c:5:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              signed char foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf1_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_165640900312616570707.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_165640900312616570707.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf1_165640900312616570707.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf1_165640900312616570707.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf1_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_178781169013819587426.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_178781169013819587426.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf1_178781169013819587426.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf1_178781169013819587426.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf1_18() {
        correct("""
              signed char foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf1_19() {
        correct("""
              signed char foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf1_20() {
        correct("""
              signed char foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf1_21() {
        correct("""
              signed char foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf1_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_228777780387714263990.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_228777780387714263990.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf1_23() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_238517523889474553845.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_238517523889474553845.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf1_24() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_246393682014089375577.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf1_246393682014089375577.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_92557507695972058923.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_92557507695972058923.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_103842756026987411749.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_103842756026987411749.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int ** a = (int **) foo();
                            ^

        */
        correct("""
              unsigned char foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf2_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_111217952770157054062.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_111217952770157054062.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 char * a = (char *) foo();
                            ^

        */
        correct("""
              unsigned char foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf2_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_122366191881885541728.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_122366191881885541728.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf2_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_131065353736295645864.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_131065353736295645864.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf2_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_146892076397122581252.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_146892076397122581252.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf2_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_156928426801403842642.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_156928426801403842642.c:5:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              unsigned char foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf2_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_167235731403663759706.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_167235731403663759706.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf2_167235731403663759706.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf2_167235731403663759706.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf2_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_174620780082084188739.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_174620780082084188739.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf2_174620780082084188739.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf2_174620780082084188739.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf2_18() {
        correct("""
              unsigned char foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf2_19() {
        correct("""
              unsigned char foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf2_20() {
        correct("""
              unsigned char foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf2_21() {
        correct("""
              unsigned char foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf2_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_22245421594122356928.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_22245421594122356928.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf2_23() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_239117857471687278264.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_239117857471687278264.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf2_24() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_242622914370677326011.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf2_242622914370677326011.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_98832656673191997476.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_98832656673191997476.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_107641363913134520691.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_107641363913134520691.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int ** a = (int **) foo();
                            ^

        */
        correct("""
              unsigned int foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf3_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_116185831560894875919.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_116185831560894875919.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 char * a = (char *) foo();
                            ^

        */
        correct("""
              unsigned int foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf3_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_122255079690623784972.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_122255079690623784972.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf3_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_136707184025527925572.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_136707184025527925572.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf3_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_143881355665473014851.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_143881355665473014851.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf3_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_159078869338483819682.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_159078869338483819682.c:5:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              unsigned int foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf3_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_161802743784486408129.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_161802743784486408129.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf3_161802743784486408129.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf3_161802743784486408129.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf3_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_173450376323578621839.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_173450376323578621839.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf3_173450376323578621839.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf3_173450376323578621839.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf3_18() {
        correct("""
              unsigned int foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf3_19() {
        correct("""
              unsigned int foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf3_20() {
        correct("""
              unsigned int foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf3_21() {
        correct("""
              unsigned int foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf3_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_22478892944230032019.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_22478892944230032019.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf3_23() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_232933010420455901976.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_232933010420455901976.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf3_24() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_241879464162827765115.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf3_241879464162827765115.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_96323058321110824593.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_96323058321110824593.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_103266540431294068958.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_103266540431294068958.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int ** a = (int **) foo();
                            ^

        */
        correct("""
              signed int foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf4_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_116577189108575281922.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_116577189108575281922.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 char * a = (char *) foo();
                            ^

        */
        correct("""
              signed int foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf4_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_127139714271466854092.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_127139714271466854092.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf4_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_132414626627233782960.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_132414626627233782960.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf4_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_14577350789703419867.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_14577350789703419867.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf4_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_15586369857930231505.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_15586369857930231505.c:5:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              signed int foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf4_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_163124171293553375061.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_163124171293553375061.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf4_163124171293553375061.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf4_163124171293553375061.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf4_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_173737178880242590595.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_173737178880242590595.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf4_173737178880242590595.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf4_173737178880242590595.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf4_18() {
        correct("""
              signed int foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf4_19() {
        correct("""
              signed int foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf4_20() {
        correct("""
              signed int foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf4_21() {
        correct("""
              signed int foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf4_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_223084495923161098171.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_223084495923161098171.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf4_23() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_236088280523062668872.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_236088280523062668872.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf4_24() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_247047808279485678086.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf4_247047808279485678086.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_95703402546595906547.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_95703402546595906547.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_106578544510908185905.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_106578544510908185905.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int ** a = (int **) foo();
                            ^

        */
        correct("""
              long foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf5_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_114275807692380060377.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_114275807692380060377.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 char * a = (char *) foo();
                            ^

        */
        correct("""
              long foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf5_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_121474820873729233173.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_121474820873729233173.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf5_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_135647349328734260995.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_135647349328734260995.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf5_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_14320081567118643355.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_14320081567118643355.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf5_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_153840501748716580653.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_153840501748716580653.c:5:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              long foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf5_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_166529702167968908344.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_166529702167968908344.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf5_166529702167968908344.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf5_166529702167968908344.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf5_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_171569181828664226212.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_171569181828664226212.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf5_171569181828664226212.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf5_171569181828664226212.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf5_18() {
        correct("""
              long foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf5_19() {
        correct("""
              long foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf5_20() {
        correct("""
              long foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf5_21() {
        correct("""
              long foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf5_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_226223207795685081819.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_226223207795685081819.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf5_23() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_239125397665317670842.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_239125397665317670842.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf5_24() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_249192298347369834753.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf5_249192298347369834753.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_9254199984042413494.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_9254199984042413494.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_10508315891707031241.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_10508315891707031241.c:3:17: error: cannot convert to a pointer type
                 int ** a = (int **) foo();
                 ^

        */
        error("""
              float foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf6_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_118345713473460593885.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_118345713473460593885.c:3:17: error: cannot convert to a pointer type
                 char * a = (char *) foo();
                 ^

        */
        error("""
              float foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf6_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_126431598012607591365.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_126431598012607591365.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf6_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_137158559188642220607.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_137158559188642220607.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf6_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_14748759677566356296.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_14748759677566356296.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf6_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_155778248382377325881.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_155778248382377325881.c:5:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              float foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf6_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_167657198140492630215.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_167657198140492630215.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf6_167657198140492630215.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf6_167657198140492630215.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf6_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_17513797310344451615.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_17513797310344451615.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf6_17513797310344451615.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf6_17513797310344451615.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf6_18() {
        correct("""
              float foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf6_19() {
        correct("""
              float foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf6_20() {
        correct("""
              float foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf6_21() {
        correct("""
              float foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf6_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_228324684880627473564.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_228324684880627473564.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf6_23() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_23915593897958012236.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_23915593897958012236.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf6_24() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_244060086807362690433.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf6_244060086807362690433.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_94940959333580443784.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_94940959333580443784.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_105701841569667476897.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_105701841569667476897.c:3:17: error: cannot convert to a pointer type
                 int ** a = (int **) foo();
                 ^

        */
        error("""
              double foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf7_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_113040527429064462948.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_113040527429064462948.c:3:17: error: cannot convert to a pointer type
                 char * a = (char *) foo();
                 ^

        */
        error("""
              double foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf7_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_126043521165545036734.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_126043521165545036734.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf7_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_138916539169442589849.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_138916539169442589849.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf7_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_145606099762375964608.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_145606099762375964608.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf7_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_154147984299367434936.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_154147984299367434936.c:5:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              double foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf7_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_169147306008640873077.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_169147306008640873077.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf7_169147306008640873077.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf7_169147306008640873077.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf7_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_171979408632090655661.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_171979408632090655661.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf7_171979408632090655661.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf7_171979408632090655661.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf7_18() {
        correct("""
              double foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf7_19() {
        correct("""
              double foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf7_20() {
        correct("""
              double foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf7_21() {
        correct("""
              double foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf7_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_221558113718340553938.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_221558113718340553938.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf7_23() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_231836134370170775258.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_231836134370170775258.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf7_24() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_246684142854269338565.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf7_246684142854269338565.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_95466275027803647469.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_95466275027803647469.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_108835588290481638492.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_108835588290481638492.c:3:17: error: cannot convert to a pointer type
                 int ** a = (int **) foo();
                 ^

        */
        error("""
              long double foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf8_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_116487005304290365989.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_116487005304290365989.c:3:17: error: cannot convert to a pointer type
                 char * a = (char *) foo();
                 ^

        */
        error("""
              long double foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf8_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_12909740227036482826.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_12909740227036482826.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf8_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_138140774038131753706.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_138140774038131753706.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf8_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_145108100043919643049.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_145108100043919643049.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf8_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_15892514224032245054.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_15892514224032245054.c:5:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              long double foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf8_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_165843032454944525006.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_165843032454944525006.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf8_165843032454944525006.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf8_165843032454944525006.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf8_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_176244396680452310432.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_176244396680452310432.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf8_176244396680452310432.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf8_176244396680452310432.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf8_18() {
        correct("""
              long double foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf8_19() {
        correct("""
              long double foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf8_20() {
        correct("""
              long double foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf8_21() {
        correct("""
              long double foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf8_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_227866561542090150933.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_227866561542090150933.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf8_23() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_23491431297156182875.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_23491431297156182875.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf8_24() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_245378527624515143596.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf8_245378527624515143596.c:3:17: error: cannot convert to a pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_02867879613510873490.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_02867879613510873490.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_16121557285414153961.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_16121557285414153961.c:3:33: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_23122106693379880785.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_23122106693379880785.c:3:35: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_32191860728838577787.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_32191860728838577787.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_47579461068364280467.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_47579461068364280467.c:3:32: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_52164984918681253120.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_52164984918681253120.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_68548461500097348542.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_68548461500097348542.c:3:17: error: pointer value used where a floating point value was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_7575973554292992654.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_7575973554292992654.c:3:17: error: pointer value used where a floating point value was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_83242361165219365392.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_83242361165219365392.c:3:17: error: pointer value used where a floating point value was expected
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
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf9_11() {
        correct("""
              int * foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf9_12() {
        correct("""
              int * foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf9_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_135825483916302868799.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_135825483916302868799.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf9_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_143415623972686917408.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_143415623972686917408.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf9_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_151295056108389080155.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_151295056108389080155.c:5:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              int * foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf9_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_162491127654511670228.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_162491127654511670228.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf9_162491127654511670228.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf9_162491127654511670228.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf9_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_174645931255352452687.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_174645931255352452687.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf9_174645931255352452687.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf9_174645931255352452687.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf9_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_186940343615571314942.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_186940343615571314942.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf9_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_198926946430488302555.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_198926946430488302555.c:3:31: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf9_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_204718949031064909135.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_204718949031064909135.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf9_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_215775508962167243037.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf9_215775508962167243037.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf9_22() {
        correct("""
              int * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf9_23() {
        correct("""
              int * foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf9_24() {
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_08891768505657081456.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_08891768505657081456.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 char a = (char) foo();
                          ^

        */
        correct("""
              int ** foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_18417088314889158611.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_18417088314889158611.c:3:33: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 signed char a = (signed char) foo();
                                 ^

        */
        correct("""
              int ** foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_2684198173280833309.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_2684198173280833309.c:3:35: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 unsigned char a = (unsigned char) foo();
                                   ^

        */
        correct("""
              int ** foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_36556825852743289885.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_36556825852743289885.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 unsigned int a = (unsigned int) foo();
                                  ^

        */
        correct("""
              int ** foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_45283693082523252527.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_45283693082523252527.c:3:32: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 signed int a = (signed int) foo();
                                ^

        */
        correct("""
              int ** foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_54293102187461572603.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_54293102187461572603.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 long a = (long) foo();
                          ^

        */
        correct("""
              int ** foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_65762998657216666062.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_65762998657216666062.c:3:17: error: pointer value used where a floating point value was expected
                 float a = (float) foo();
                 ^

        */
        error("""
              int ** foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_74427864069652556478.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_74427864069652556478.c:3:17: error: pointer value used where a floating point value was expected
                 double a = (double) foo();
                 ^

        */
        error("""
              int ** foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_85090313692345351634.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_85090313692345351634.c:3:17: error: pointer value used where a floating point value was expected
                 long double a = (long double) foo();
                 ^

        */
        error("""
              int ** foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_9() {
        correct("""
              int ** foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_10() {
        correct("""
              int ** foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_11() {
        correct("""
              int ** foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_12() {
        correct("""
              int ** foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_133183693768098646587.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_133183693768098646587.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              int ** foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_142994765439789772442.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_142994765439789772442.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              int ** foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_159021023987823791656.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_159021023987823791656.c:5:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              int ** foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_164641011150075270934.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_164641011150075270934.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf10_164641011150075270934.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf10_164641011150075270934.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              int ** foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_172914781094529287565.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_172914781094529287565.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf10_172914781094529287565.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf10_172914781094529287565.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              int ** foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_183864504316633323461.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_183864504316633323461.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 volatile int a = (volatile int) foo();
                                  ^

        */
        correct("""
              int ** foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_197048316464184838801.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_197048316464184838801.c:3:31: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 const int a = (const int) foo();
                               ^

        */
        correct("""
              int ** foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_202969751789518704188.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_202969751789518704188.c:3:17: error: pointer value used where a floating point value was expected
                 const double a = (const double) foo();
                 ^

        */
        error("""
              int ** foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_214795843226675200815.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf10_214795843226675200815.c:3:17: error: pointer value used where a floating point value was expected
                 volatile double a = (volatile double) foo();
                 ^

        */
        error("""
              int ** foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_22() {
        correct("""
              int ** foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_23() {
        correct("""
              int ** foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf10_24() {
        correct("""
              int ** foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_05955713637025683548.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_05955713637025683548.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 char a = (char) foo();
                          ^

        */
        correct("""
              char * foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_11540339508781826892.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_11540339508781826892.c:3:33: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 signed char a = (signed char) foo();
                                 ^

        */
        correct("""
              char * foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_26777990169929026281.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_26777990169929026281.c:3:35: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 unsigned char a = (unsigned char) foo();
                                   ^

        */
        correct("""
              char * foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_33862873222353890165.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_33862873222353890165.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 unsigned int a = (unsigned int) foo();
                                  ^

        */
        correct("""
              char * foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_42345104346409843290.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_42345104346409843290.c:3:32: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 signed int a = (signed int) foo();
                                ^

        */
        correct("""
              char * foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_53996492926845060438.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_53996492926845060438.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 long a = (long) foo();
                          ^

        */
        correct("""
              char * foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_67150792538710074060.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_67150792538710074060.c:3:17: error: pointer value used where a floating point value was expected
                 float a = (float) foo();
                 ^

        */
        error("""
              char * foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_7133476637699634025.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_7133476637699634025.c:3:17: error: pointer value used where a floating point value was expected
                 double a = (double) foo();
                 ^

        */
        error("""
              char * foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_83799106251986494109.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_83799106251986494109.c:3:17: error: pointer value used where a floating point value was expected
                 long double a = (long double) foo();
                 ^

        */
        error("""
              char * foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_9() {
        correct("""
              char * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_10() {
        correct("""
              char * foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_11() {
        correct("""
              char * foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_12() {
        correct("""
              char * foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_137807856778966802096.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_137807856778966802096.c:5:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              struct S { int x; int y; };

              char * foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_143301965341283968474.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_143301965341283968474.c:5:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              struct T { int x; int y; int z; };

              char * foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_151097174815042839052.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_151097174815042839052.c:5:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              char * foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_161402818084524729858.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_161402818084524729858.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf11_161402818084524729858.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf11_161402818084524729858.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              char * foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_17106118737874826155.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_17106118737874826155.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf11_17106118737874826155.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf11_17106118737874826155.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              char * foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_185280931719204104389.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_185280931719204104389.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 volatile int a = (volatile int) foo();
                                  ^

        */
        correct("""
              char * foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_192080326757107508057.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_192080326757107508057.c:3:31: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
                 const int a = (const int) foo();
                               ^

        */
        correct("""
              char * foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_202832456604359972753.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_202832456604359972753.c:3:17: error: pointer value used where a floating point value was expected
                 const double a = (const double) foo();
                 ^

        */
        error("""
              char * foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_218742687213496309580.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf11_218742687213496309580.c:3:17: error: pointer value used where a floating point value was expected
                 volatile double a = (volatile double) foo();
                 ^

        */
        error("""
              char * foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_22() {
        correct("""
              char * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_23() {
        correct("""
              char * foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf11_24() {
        correct("""
              char * foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf12_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_06362881690891959005.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_06362881690891959005.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf12_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_1563171255241204893.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_1563171255241204893.c:3:33: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf12_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_26789332036520994864.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_26789332036520994864.c:3:35: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf12_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_31132279764694838894.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_31132279764694838894.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf12_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_42247665144492802685.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_42247665144492802685.c:3:32: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf12_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_52366767426984202587.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_52366767426984202587.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf12_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_67398549645620393324.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_67398549645620393324.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf12_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_74594109917304905267.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_74594109917304905267.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf12_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_84164303593985038943.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_84164303593985038943.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf12_9() {
        correct("""
              double * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf12_10() {
        correct("""
              double * foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf12_11() {
        correct("""
              double * foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf12_12() {
        correct("""
              double * foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf12_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_136114264591260984326.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_136114264591260984326.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf12_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_14114463299785010175.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_14114463299785010175.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf12_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_15351393604560298997.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_15351393604560298997.c:5:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              double * foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf12_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_169125140274708999352.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_169125140274708999352.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf12_169125140274708999352.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf12_169125140274708999352.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf12_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_173935874356819558254.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_173935874356819558254.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf12_173935874356819558254.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf12_173935874356819558254.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf12_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_188752082848212546740.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_188752082848212546740.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf12_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_194156342979247593388.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_194156342979247593388.c:3:31: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf12_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_208025628508128258698.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_208025628508128258698.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf12_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_217245130549810148848.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf12_217245130549810148848.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf12_22() {
        correct("""
              double * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf12_23() {
        correct("""
              double * foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf12_24() {
        correct("""
              double * foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf13_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_02359225817210584.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_02359225817210584.c:5:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf13_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_14376333625840599167.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_14376333625840599167.c:5:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf13_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_26441061069327430914.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_26441061069327430914.c:5:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf13_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_32292274824892662388.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_32292274824892662388.c:5:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf13_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_45469565346057596572.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_45469565346057596572.c:5:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf13_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_56853294271146833215.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_56853294271146833215.c:5:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf13_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_68542110037266699606.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_68542110037266699606.c:5:17: error: aggregate value used where a float was expected
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


   @Test def test_conf13_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_78708698680252268999.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_78708698680252268999.c:5:17: error: aggregate value used where a float was expected
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


   @Test def test_conf13_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_83628362789503059215.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_83628362789503059215.c:5:17: error: aggregate value used where a float was expected
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


   @Test def test_conf13_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_98680530023372342621.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_98680530023372342621.c:5:17: error: cannot convert to a pointer type
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


   @Test def test_conf13_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_107982342929472783246.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_107982342929472783246.c:5:17: error: cannot convert to a pointer type
                 int ** a = (int **) foo();
                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf13_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_117027567193999230068.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_117027567193999230068.c:5:17: error: cannot convert to a pointer type
                 char * a = (char *) foo();
                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf13_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_123613147446497062561.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_123613147446497062561.c:5:17: error: cannot convert to a pointer type
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


   @Test def test_conf13_13() {
        correct("""
              struct S { int x; int y; };

              struct S foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }


   @Test def test_conf13_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_14858686307970831198.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_14858686307970831198.c:7:38: error: conversion to non-scalar type requested
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


   @Test def test_conf13_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_15639853270469986636.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_15639853270469986636.c:7:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct S { int x; int y; };

              struct S foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf13_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_163411461470328958919.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_163411461470328958919.c:5:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf13_163411461470328958919.c:6:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf13_163411461470328958919.c:7:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf13_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_177992474329450735443.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_177992474329450735443.c:5:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf13_177992474329450735443.c:6:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf13_177992474329450735443.c:7:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf13_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_184863432182616289550.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_184863432182616289550.c:5:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf13_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_198869291841678058245.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_198869291841678058245.c:5:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf13_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_204740037257723213755.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_204740037257723213755.c:5:17: error: aggregate value used where a float was expected
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


   @Test def test_conf13_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_213818386709931554866.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_213818386709931554866.c:5:17: error: aggregate value used where a float was expected
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


   @Test def test_conf13_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_221149415843347540334.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_221149415843347540334.c:5:17: error: cannot convert to a pointer type
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


   @Test def test_conf13_23() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_235296826521901379862.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_235296826521901379862.c:5:17: error: cannot convert to a pointer type
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


   @Test def test_conf13_24() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_24707194990434147781.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf13_24707194990434147781.c:5:17: error: cannot convert to a pointer type
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


   @Test def test_conf14_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_04588214637023102649.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_04588214637023102649.c:5:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf14_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_17098848965918079174.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_17098848965918079174.c:5:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf14_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_24718620671320687446.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_24718620671320687446.c:5:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf14_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_36138252086275741411.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_36138252086275741411.c:5:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf14_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_43955636987415725127.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_43955636987415725127.c:5:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf14_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_53485881558647074648.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_53485881558647074648.c:5:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf14_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_64502404753082842263.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_64502404753082842263.c:5:17: error: aggregate value used where a float was expected
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


   @Test def test_conf14_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_79107914972425605207.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_79107914972425605207.c:5:17: error: aggregate value used where a float was expected
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


   @Test def test_conf14_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_81243343891271106489.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_81243343891271106489.c:5:17: error: aggregate value used where a float was expected
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


   @Test def test_conf14_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_91459263877394534087.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_91459263877394534087.c:5:17: error: cannot convert to a pointer type
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


   @Test def test_conf14_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_105946876920018179487.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_105946876920018179487.c:5:17: error: cannot convert to a pointer type
                 int ** a = (int **) foo();
                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf14_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_118627689559183062283.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_118627689559183062283.c:5:17: error: cannot convert to a pointer type
                 char * a = (char *) foo();
                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf14_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_12333205906185320088.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_12333205906185320088.c:5:17: error: cannot convert to a pointer type
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


   @Test def test_conf14_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_138739961849118636894.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_138739961849118636894.c:7:38: error: conversion to non-scalar type requested
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


   @Test def test_conf14_14() {
        correct("""
              struct T { int x; int y; int z; };

              struct T foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }


   @Test def test_conf14_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_152707104752943622300.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_152707104752943622300.c:7:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct T { int x; int y; int z; };

              struct T foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf14_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_164266165322671942398.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_164266165322671942398.c:5:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf14_164266165322671942398.c:6:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf14_164266165322671942398.c:7:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf14_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_178705811740846949902.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_178705811740846949902.c:5:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf14_178705811740846949902.c:6:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf14_178705811740846949902.c:7:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf14_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_187713184677671417220.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_187713184677671417220.c:5:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf14_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_193785087443941280931.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_193785087443941280931.c:5:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf14_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_207693186658059691407.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_207693186658059691407.c:5:17: error: aggregate value used where a float was expected
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


   @Test def test_conf14_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_218622085296659505963.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_218622085296659505963.c:5:17: error: aggregate value used where a float was expected
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


   @Test def test_conf14_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_227547507902017634638.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_227547507902017634638.c:5:17: error: cannot convert to a pointer type
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


   @Test def test_conf14_23() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_233689773555001503757.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_233689773555001503757.c:5:17: error: cannot convert to a pointer type
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


   @Test def test_conf14_24() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_249118023585892268560.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf14_249118023585892268560.c:5:17: error: cannot convert to a pointer type
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


   @Test def test_conf15_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_08560119046580921301.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_08560119046580921301.c:5:17: error: aggregate value used where an integer was expected
                 char a = (char) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_11795107890704004152.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_11795107890704004152.c:5:17: error: aggregate value used where an integer was expected
                 signed char a = (signed char) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_26258539340513997408.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_26258539340513997408.c:5:17: error: aggregate value used where an integer was expected
                 unsigned char a = (unsigned char) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_37955669187562355050.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_37955669187562355050.c:5:17: error: aggregate value used where an integer was expected
                 unsigned int a = (unsigned int) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_42367539393517423735.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_42367539393517423735.c:5:17: error: aggregate value used where an integer was expected
                 signed int a = (signed int) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_53015043939925364232.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_53015043939925364232.c:5:17: error: aggregate value used where an integer was expected
                 long a = (long) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_63687281367135941133.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_63687281367135941133.c:5:17: error: aggregate value used where a float was expected
                 float a = (float) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_72079993143442163730.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_72079993143442163730.c:5:17: error: aggregate value used where a float was expected
                 double a = (double) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_8656023239010388736.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_8656023239010388736.c:5:17: error: aggregate value used where a float was expected
                 long double a = (long double) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_91893920482651964553.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_91893920482651964553.c:5:17: error: cannot convert to a pointer type
                 int * a = (int *) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_105558091033705293228.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_105558091033705293228.c:5:17: error: cannot convert to a pointer type
                 int ** a = (int **) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_117814359461705443681.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_117814359461705443681.c:5:17: error: cannot convert to a pointer type
                 char * a = (char *) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_122802100760391972773.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_122802100760391972773.c:5:17: error: cannot convert to a pointer type
                 double * a = (double *) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_135323201962163657062.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_135323201962163657062.c:7:38: error: conversion to non-scalar type requested
                 struct S a = (struct S) foo();
                                      ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct S { int x; int y; };

              struct_anonymous foo();
              struct S bar() {
                struct S a = (struct S) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_147277048752617674837.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_147277048752617674837.c:7:38: error: conversion to non-scalar type requested
                 struct T a = (struct T) foo();
                                      ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct T { int x; int y; int z; };

              struct_anonymous foo();
              struct T bar() {
                struct T a = (struct T) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_15() {
        correct("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_161980051971827458586.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_161980051971827458586.c:5:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf15_161980051971827458586.c:6:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf15_161980051971827458586.c:7:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              struct { int a; } bar() {
                struct { int a; } a = (struct { int a; }) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_1766369010332563384.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_1766369010332563384.c:5:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf15_1766369010332563384.c:6:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf15_1766369010332563384.c:7:15: warning: control reaches end of non-void function [-Wreturn-type]
               }
               ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              struct { float b; } bar() {
                struct { float b; } a = (struct { float b; }) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_189099255416970767427.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_189099255416970767427.c:5:17: error: aggregate value used where an integer was expected
                 volatile int a = (volatile int) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_197239977885286260255.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_197239977885286260255.c:5:17: error: aggregate value used where an integer was expected
                 const int a = (const int) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_201024773414363706581.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_201024773414363706581.c:5:17: error: aggregate value used where a float was expected
                 const double a = (const double) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_213937383953451774870.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_213937383953451774870.c:5:17: error: aggregate value used where a float was expected
                 volatile double a = (volatile double) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_228664823100224839217.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_228664823100224839217.c:5:17: error: cannot convert to a pointer type
                 int * a = (int *) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_23() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_236223402706372572614.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_236223402706372572614.c:5:17: error: cannot convert to a pointer type
                 const int * a = (const int *) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf15_24() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_247312682448609938089.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf15_247312682448609938089.c:5:17: error: cannot convert to a pointer type
                 volatile int * a = (volatile int *) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf16_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_01338670274836793238.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_01338670274836793238.c:3:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf16_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_17767491794208436614.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_17767491794208436614.c:3:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf16_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_22621422505723761651.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_22621422505723761651.c:3:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf16_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_38233872334563852872.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_38233872334563852872.c:3:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf16_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_44683928622014143637.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_44683928622014143637.c:3:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf16_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_51634576938427000094.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_51634576938427000094.c:3:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf16_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_61091112238361126014.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_61091112238361126014.c:3:17: error: aggregate value used where a float was expected
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


   @Test def test_conf16_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_75917838993886279921.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_75917838993886279921.c:3:17: error: aggregate value used where a float was expected
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


   @Test def test_conf16_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_85578586220122778537.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_85578586220122778537.c:3:17: error: aggregate value used where a float was expected
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


   @Test def test_conf16_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_92683244645831589124.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_92683244645831589124.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf16_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_10737730979115445770.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_10737730979115445770.c:3:17: error: cannot convert to a pointer type
                 int ** a = (int **) foo();
                 ^

        */
        error("""
              struct { int a; } foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf16_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_113406729045476201812.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_113406729045476201812.c:3:17: error: cannot convert to a pointer type
                 char * a = (char *) foo();
                 ^

        */
        error("""
              struct { int a; } foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf16_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_123848244505192821295.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_123848244505192821295.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf16_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_138192089651713429168.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_138192089651713429168.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf16_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_147673080535512884046.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_147673080535512884046.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf16_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_157356497632140302306.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_157356497632140302306.c:5:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct { int a; } foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf16_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_167874436796336361027.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_167874436796336361027.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf16_167874436796336361027.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf16_167874436796336361027.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf16_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_178561986017484462276.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_178561986017484462276.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf16_178561986017484462276.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf16_178561986017484462276.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf16_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_184307576966775533166.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_184307576966775533166.c:3:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf16_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_195797969037092689396.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_195797969037092689396.c:3:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf16_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_207294232710933493750.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_207294232710933493750.c:3:17: error: aggregate value used where a float was expected
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


   @Test def test_conf16_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_215857279523433068435.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_215857279523433068435.c:3:17: error: aggregate value used where a float was expected
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


   @Test def test_conf16_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_226816020874028942800.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_226816020874028942800.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf16_23() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_237807669327046234430.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_237807669327046234430.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf16_24() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_245815601649504166136.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf16_245815601649504166136.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf17_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_06601569548160151369.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_06601569548160151369.c:3:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf17_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_18025469797705177689.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_18025469797705177689.c:3:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf17_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_23997771381971710207.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_23997771381971710207.c:3:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf17_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_31939493918599839453.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_31939493918599839453.c:3:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf17_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_46426582269841590595.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_46426582269841590595.c:3:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf17_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_53368249822091837633.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_53368249822091837633.c:3:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf17_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_68234266338595486130.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_68234266338595486130.c:3:17: error: aggregate value used where a float was expected
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


   @Test def test_conf17_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_71183379127983734909.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_71183379127983734909.c:3:17: error: aggregate value used where a float was expected
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


   @Test def test_conf17_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_84401067364590640896.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_84401067364590640896.c:3:17: error: aggregate value used where a float was expected
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


   @Test def test_conf17_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_9537519618237777550.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_9537519618237777550.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf17_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_105616329794136912723.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_105616329794136912723.c:3:17: error: cannot convert to a pointer type
                 int ** a = (int **) foo();
                 ^

        */
        error("""
              struct { float b; } foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf17_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_115982762761825061865.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_115982762761825061865.c:3:17: error: cannot convert to a pointer type
                 char * a = (char *) foo();
                 ^

        */
        error("""
              struct { float b; } foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf17_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_125013083384877140656.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_125013083384877140656.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf17_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_137607491966593734751.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_137607491966593734751.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf17_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_141384649332084499196.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_141384649332084499196.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf17_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_15532758988693247286.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_15532758988693247286.c:5:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct { float b; } foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf17_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_167623757061265827622.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_167623757061265827622.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf17_167623757061265827622.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf17_167623757061265827622.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf17_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_174876588686793313179.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_174876588686793313179.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf17_174876588686793313179.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf17_174876588686793313179.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf17_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_183859842385887514051.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_183859842385887514051.c:3:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf17_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_199073458523986311228.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_199073458523986311228.c:3:17: error: aggregate value used where an integer was expected
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


   @Test def test_conf17_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_207037473934258312549.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_207037473934258312549.c:3:17: error: aggregate value used where a float was expected
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


   @Test def test_conf17_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_214452338080395792902.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_214452338080395792902.c:3:17: error: aggregate value used where a float was expected
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


   @Test def test_conf17_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_227882070183324311955.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_227882070183324311955.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf17_23() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_238581089923696310106.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_238581089923696310106.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf17_24() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_242423117545420472602.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf17_242423117545420472602.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf18_0() {
        correct("""
              volatile int foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_1() {
        correct("""
              volatile int foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_2() {
        correct("""
              volatile int foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_3() {
        correct("""
              volatile int foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_4() {
        correct("""
              volatile int foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_5() {
        correct("""
              volatile int foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_6() {
        correct("""
              volatile int foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_7() {
        correct("""
              volatile int foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_8() {
        correct("""
              volatile int foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_91703493522259795404.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_91703493522259795404.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf18_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_103438190683760609529.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_103438190683760609529.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int ** a = (int **) foo();
                            ^

        */
        correct("""
              volatile int foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_111735530839475562802.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_111735530839475562802.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 char * a = (char *) foo();
                            ^

        */
        correct("""
              volatile int foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_123818429672758545912.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_123818429672758545912.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf18_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_137690342398930053876.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_137690342398930053876.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf18_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_141450380750998722732.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_141450380750998722732.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf18_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_154864570721577526779.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_154864570721577526779.c:5:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              volatile int foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_161519386265891166567.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_161519386265891166567.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf18_161519386265891166567.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf18_161519386265891166567.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf18_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_174709122918359024934.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_174709122918359024934.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf18_174709122918359024934.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf18_174709122918359024934.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf18_18() {
        correct("""
              volatile int foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_19() {
        correct("""
              volatile int foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_20() {
        correct("""
              volatile int foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_21() {
        correct("""
              volatile int foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf18_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_2258148342249010194.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_2258148342249010194.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf18_23() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_235713802338423772779.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_235713802338423772779.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf18_24() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_248942634773956798482.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf18_248942634773956798482.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf19_0() {
        correct("""
              const int foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_1() {
        correct("""
              const int foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_2() {
        correct("""
              const int foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_3() {
        correct("""
              const int foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_4() {
        correct("""
              const int foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_5() {
        correct("""
              const int foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_6() {
        correct("""
              const int foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_7() {
        correct("""
              const int foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_8() {
        correct("""
              const int foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_93488846667975041464.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_93488846667975041464.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf19_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_102758846528585654449.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_102758846528585654449.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 int ** a = (int **) foo();
                            ^

        */
        correct("""
              const int foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_11890322520965731064.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_11890322520965731064.c:3:28: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
                 char * a = (char *) foo();
                            ^

        */
        correct("""
              const int foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_127336148314483153403.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_127336148314483153403.c:3:30: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf19_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_135350288777484302198.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_135350288777484302198.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf19_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_143810963212722312364.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_143810963212722312364.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf19_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_152139351438320360921.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_152139351438320360921.c:5:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              const int foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_169153238746707806109.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_169153238746707806109.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf19_169153238746707806109.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf19_169153238746707806109.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf19_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_171120608313363818251.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_171120608313363818251.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf19_171120608313363818251.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf19_171120608313363818251.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf19_18() {
        correct("""
              const int foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_19() {
        correct("""
              const int foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_20() {
        correct("""
              const int foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_21() {
        correct("""
              const int foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf19_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_222115676399773076720.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_222115676399773076720.c:3:27: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf19_23() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_238916236848378271758.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_238916236848378271758.c:3:33: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf19_24() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_242109024507863249040.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf19_242109024507863249040.c:3:36: warning: cast to pointer from integer of different size [-Wint-to-pointer-cast]
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


   @Test def test_conf20_0() {
        correct("""
              const double foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_1() {
        correct("""
              const double foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_2() {
        correct("""
              const double foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_3() {
        correct("""
              const double foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_4() {
        correct("""
              const double foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_5() {
        correct("""
              const double foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_6() {
        correct("""
              const double foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_7() {
        correct("""
              const double foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_8() {
        correct("""
              const double foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_97580825705275491829.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_97580825705275491829.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf20_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_10257857952568939180.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_10257857952568939180.c:3:17: error: cannot convert to a pointer type
                 int ** a = (int **) foo();
                 ^

        */
        error("""
              const double foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_115423081748723380450.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_115423081748723380450.c:3:17: error: cannot convert to a pointer type
                 char * a = (char *) foo();
                 ^

        */
        error("""
              const double foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_124901131871739182784.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_124901131871739182784.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf20_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_133337826021055704643.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_133337826021055704643.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf20_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_147585430744976454117.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_147585430744976454117.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf20_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_153042310657808149542.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_153042310657808149542.c:5:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              const double foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_16160663673896161320.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_16160663673896161320.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf20_16160663673896161320.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf20_16160663673896161320.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf20_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_173560904578198693097.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_173560904578198693097.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf20_173560904578198693097.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf20_173560904578198693097.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf20_18() {
        correct("""
              const double foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_19() {
        correct("""
              const double foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_20() {
        correct("""
              const double foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_21() {
        correct("""
              const double foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf20_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_223258736441295847454.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_223258736441295847454.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf20_23() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_239045574249001904200.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_239045574249001904200.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf20_24() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_242027665525580589910.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf20_242027665525580589910.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf21_0() {
        correct("""
              volatile double foo();
              char bar() {
                char a = (char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_1() {
        correct("""
              volatile double foo();
              signed char bar() {
                signed char a = (signed char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_2() {
        correct("""
              volatile double foo();
              unsigned char bar() {
                unsigned char a = (unsigned char) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_3() {
        correct("""
              volatile double foo();
              unsigned int bar() {
                unsigned int a = (unsigned int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_4() {
        correct("""
              volatile double foo();
              signed int bar() {
                signed int a = (signed int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_5() {
        correct("""
              volatile double foo();
              long bar() {
                long a = (long) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_6() {
        correct("""
              volatile double foo();
              float bar() {
                float a = (float) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_7() {
        correct("""
              volatile double foo();
              double bar() {
                double a = (double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_8() {
        correct("""
              volatile double foo();
              long double bar() {
                long double a = (long double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_97021930510191586107.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_97021930510191586107.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf21_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_102242739760586501514.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_102242739760586501514.c:3:17: error: cannot convert to a pointer type
                 int ** a = (int **) foo();
                 ^

        */
        error("""
              volatile double foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_111655810616546669823.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_111655810616546669823.c:3:17: error: cannot convert to a pointer type
                 char * a = (char *) foo();
                 ^

        */
        error("""
              volatile double foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_127957502888259579775.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_127957502888259579775.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf21_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_131775912589604552300.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_131775912589604552300.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf21_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_145941550086348315864.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_145941550086348315864.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf21_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_154460425662431485293.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_154460425662431485293.c:5:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              volatile double foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_161315293142108220914.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_161315293142108220914.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf21_161315293142108220914.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf21_161315293142108220914.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf21_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_17151505299222365673.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_17151505299222365673.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf21_17151505299222365673.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf21_17151505299222365673.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf21_18() {
        correct("""
              volatile double foo();
              volatile int bar() {
                volatile int a = (volatile int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_19() {
        correct("""
              volatile double foo();
              const int bar() {
                const int a = (const int) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_20() {
        correct("""
              volatile double foo();
              const double bar() {
                const double a = (const double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_21() {
        correct("""
              volatile double foo();
              volatile double bar() {
                volatile double a = (volatile double) foo();
                return a;
              }
                """)
   }


   @Test def test_conf21_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_22658374657789303052.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_22658374657789303052.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf21_23() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_232524318218303564090.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_232524318218303564090.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf21_24() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_246649480437689021864.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf21_246649480437689021864.c:3:17: error: cannot convert to a pointer type
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


   @Test def test_conf22_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_07901602682956984151.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_07901602682956984151.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf22_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_18531485214234566608.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_18531485214234566608.c:3:33: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf22_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_26673713872869440583.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_26673713872869440583.c:3:35: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf22_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_32534347804136599276.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_32534347804136599276.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf22_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_49021211670374587588.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_49021211670374587588.c:3:32: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf22_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_52171789778440297732.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_52171789778440297732.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf22_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_66916461878028807606.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_66916461878028807606.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf22_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_74185160180566244367.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_74185160180566244367.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf22_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_83044495193355097599.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_83044495193355097599.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf22_9() {
        correct("""
              int * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf22_10() {
        correct("""
              int * foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf22_11() {
        correct("""
              int * foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf22_12() {
        correct("""
              int * foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf22_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_136415039210597722812.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_136415039210597722812.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf22_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_148731400587341440019.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_148731400587341440019.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf22_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_155351016265219015665.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_155351016265219015665.c:5:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              int * foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf22_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_161012701108168122974.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_161012701108168122974.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf22_161012701108168122974.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf22_161012701108168122974.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf22_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_173728040615695160908.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_173728040615695160908.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf22_173728040615695160908.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf22_173728040615695160908.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf22_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_186088180712454491892.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_186088180712454491892.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf22_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_196210022039450350718.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_196210022039450350718.c:3:31: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf22_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_20879694432564967279.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_20879694432564967279.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf22_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_218677073584585544676.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf22_218677073584585544676.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf22_22() {
        correct("""
              int * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf22_23() {
        correct("""
              int * foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf22_24() {
        correct("""
              int * foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf23_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_02460478362943531735.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf23_02460478362943531735.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf23_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_13605885742312385494.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf23_13605885742312385494.c:3:33: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf23_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_27194699506160647702.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf23_27194699506160647702.c:3:35: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf23_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_33521930955597733278.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf23_33521930955597733278.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf23_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_43104509325319645500.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf23_43104509325319645500.c:3:32: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf23_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_51888282335604085774.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf23_51888282335604085774.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf23_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_64856613131834154856.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf23_64856613131834154856.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf23_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_72119884167357130293.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf23_72119884167357130293.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf23_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_82191863790475662315.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf23_82191863790475662315.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf23_9() {
        correct("""
              const int * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf23_10() {
        correct("""
              const int * foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf23_11() {
        correct("""
              const int * foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf23_12() {
        correct("""
              const int * foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf23_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_137539859757849408625.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf23_137539859757849408625.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf23_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_14971150670774259646.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf23_14971150670774259646.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf23_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_156585479284082573904.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf23_156585479284082573904.c:5:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              const int * foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf23_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_161345629611255953596.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf23_161345629611255953596.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf23_161345629611255953596.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf23_161345629611255953596.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf23_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_177549217982342748288.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf23_177549217982342748288.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf23_177549217982342748288.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf23_177549217982342748288.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf23_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_18770554922099887123.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf23_18770554922099887123.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf23_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_193781551024194545857.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf23_193781551024194545857.c:3:31: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf23_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_203219333443060086560.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf23_203219333443060086560.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf23_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_217532120610194224058.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf23_217532120610194224058.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf23_22() {
        correct("""
              const int * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf23_23() {
        correct("""
              const int * foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf23_24() {
        correct("""
              const int * foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf24_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf24_03793047647437240361.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf24_03793047647437240361.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf24_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf24_15371123980678956404.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf24_15371123980678956404.c:3:33: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf24_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf24_23237943779207012879.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf24_23237943779207012879.c:3:35: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf24_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf24_39067244570361762929.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf24_39067244570361762929.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf24_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf24_42050541290460344329.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf24_42050541290460344329.c:3:32: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf24_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf24_56362419994293423739.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf24_56362419994293423739.c:3:26: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf24_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf24_62855605739360332250.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf24_62855605739360332250.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf24_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf24_7368006698744632304.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf24_7368006698744632304.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf24_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf24_83413730983021314013.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf24_83413730983021314013.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf24_9() {
        correct("""
              volatile int * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf24_10() {
        correct("""
              volatile int * foo();
              int ** bar() {
                int ** a = (int **) foo();
                return a;
              }
                """)
   }


   @Test def test_conf24_11() {
        correct("""
              volatile int * foo();
              char * bar() {
                char * a = (char *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf24_12() {
        correct("""
              volatile int * foo();
              double * bar() {
                double * a = (double *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf24_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf24_131191963429769588862.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf24_131191963429769588862.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf24_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf24_142498312963430924587.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf24_142498312963430924587.c:5:38: error: conversion to non-scalar type requested
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


   @Test def test_conf24_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf24_151638351177238238908.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf24_151638351177238238908.c:5:17: error: conversion to non-scalar type requested
                 struct_anonymous a = (struct_anonymous) foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              volatile int * foo();
              struct_anonymous bar() {
                struct_anonymous a = (struct_anonymous) foo();
                return a;
              }
                """)
   }


   @Test def test_conf24_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf24_168432688590065117938.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf24_168432688590065117938.c:3:47: error: conversion to non-scalar type requested
                 struct { int a; } a = (struct { int a; }) foo();
                                               ^
C:\Users\ckaestne\AppData\Local\Temp\conf24_168432688590065117938.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf24_168432688590065117938.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf24_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf24_175910976744365154634.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf24_175910976744365154634.c:3:49: error: conversion to non-scalar type requested
                 struct { float b; } a = (struct { float b; }) foo();
                                                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf24_175910976744365154634.c:4:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^
C:\Users\ckaestne\AppData\Local\Temp\conf24_175910976744365154634.c:5:15: warning: control reaches end of non-void function [-Wreturn-type]
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


   @Test def test_conf24_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf24_188824125059614004391.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf24_188824125059614004391.c:3:34: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf24_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf24_19830581595173607926.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf24_19830581595173607926.c:3:31: warning: cast from pointer to integer of different size [-Wpointer-to-int-cast]
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


   @Test def test_conf24_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf24_204711518917839334876.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf24_204711518917839334876.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf24_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf24_218087412192368891859.c: In function 'bar':
C:\Users\ckaestne\AppData\Local\Temp\conf24_218087412192368891859.c:3:17: error: pointer value used where a floating point value was expected
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


   @Test def test_conf24_22() {
        correct("""
              volatile int * foo();
              int * bar() {
                int * a = (int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf24_23() {
        correct("""
              volatile int * foo();
              const int * bar() {
                const int * a = (const int *) foo();
                return a;
              }
                """)
   }


   @Test def test_conf24_24() {
        correct("""
              volatile int * foo();
              volatile int * bar() {
                volatile int * a = (volatile int *) foo();
                return a;
              }
                """)
   }




}