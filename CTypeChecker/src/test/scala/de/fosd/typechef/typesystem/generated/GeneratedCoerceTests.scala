package de.fosd.typechef.typesystem.generated

import de.fosd.typechef.typesystem._
import org.junit._

class GeneratedCoerceTests extends TestHelperTS {

    @Test def test_conf0_0() {
        correct("""
              char foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        correct("""
              char foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf0_1() {
        correct("""
              char foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        correct("""
              char foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf0_2() {
        correct("""
              char foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        correct("""
              char foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf0_3() {
        correct("""
              char foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        correct("""
              char foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf0_4() {
        correct("""
              char foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        correct("""
              char foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf0_5() {
        correct("""
              char foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        correct("""
              char foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf0_6() {
        correct("""
              char foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        correct("""
              char foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf0_7() {
        correct("""
              char foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        correct("""
              char foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf0_8() {
        correct("""
              char foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        correct("""
              char foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf0_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              char foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization makes pointer from integer without a cast
                 int * b = foo();
                           ^

        */
        warning("""
              char foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf0_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              char foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization makes pointer from integer without a cast
                 int ** b = foo();
                            ^

        */
        warning("""
              char foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf0_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              char foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization makes pointer from integer without a cast
                 char * b = foo();
                            ^

        */
        warning("""
              char foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf0_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              char foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:30: warning: initialization makes pointer from integer without a cast
                 double * b = foo();
                              ^

        */
        warning("""
              char foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf0_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'char'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              char foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              char foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf0_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'char'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              char foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              char foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf0_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'char'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              char foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              char foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf0_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'char'
                 b = foo();
                   ^

        */
        error("""
              char foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              char foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf0_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'char'
                 b = foo();
                   ^

        */
        error("""
              char foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              char foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf0_18() {
        correct("""
              char foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        correct("""
              char foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf0_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              char foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        correct("""
              char foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf0_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              char foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        correct("""
              char foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf0_21() {
        correct("""
              char foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        correct("""
              char foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf0_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              char foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization makes pointer from integer without a cast
                 int * b = foo();
                           ^

        */
        warning("""
              char foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf0_23() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              char foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: warning: initialization makes pointer from integer without a cast
                 const int * b = foo();
                                 ^

        */
        warning("""
              char foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf0_24() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              char foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:36: warning: initialization makes pointer from integer without a cast
                 volatile int * b = foo();
                                    ^

        */
        warning("""
              char foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf1_0() {
        correct("""
              signed char foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        correct("""
              signed char foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf1_1() {
        correct("""
              signed char foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        correct("""
              signed char foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf1_2() {
        correct("""
              signed char foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        correct("""
              signed char foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf1_3() {
        correct("""
              signed char foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        correct("""
              signed char foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf1_4() {
        correct("""
              signed char foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        correct("""
              signed char foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf1_5() {
        correct("""
              signed char foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        correct("""
              signed char foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf1_6() {
        correct("""
              signed char foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        correct("""
              signed char foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf1_7() {
        correct("""
              signed char foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        correct("""
              signed char foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf1_8() {
        correct("""
              signed char foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        correct("""
              signed char foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf1_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              signed char foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization makes pointer from integer without a cast
                 int * b = foo();
                           ^

        */
        warning("""
              signed char foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf1_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              signed char foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization makes pointer from integer without a cast
                 int ** b = foo();
                            ^

        */
        warning("""
              signed char foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf1_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              signed char foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization makes pointer from integer without a cast
                 char * b = foo();
                            ^

        */
        warning("""
              signed char foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf1_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              signed char foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:30: warning: initialization makes pointer from integer without a cast
                 double * b = foo();
                              ^

        */
        warning("""
              signed char foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf1_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'signed char'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              signed char foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              signed char foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf1_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'signed char'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              signed char foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              signed char foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf1_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'signed char'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              signed char foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              signed char foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf1_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'signed char'
                 b = foo();
                   ^

        */
        error("""
              signed char foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              signed char foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf1_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'signed char'
                 b = foo();
                   ^

        */
        error("""
              signed char foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              signed char foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf1_18() {
        correct("""
              signed char foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        correct("""
              signed char foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf1_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              signed char foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        correct("""
              signed char foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf1_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              signed char foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        correct("""
              signed char foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf1_21() {
        correct("""
              signed char foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        correct("""
              signed char foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf1_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              signed char foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization makes pointer from integer without a cast
                 int * b = foo();
                           ^

        */
        warning("""
              signed char foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf1_23() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              signed char foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: warning: initialization makes pointer from integer without a cast
                 const int * b = foo();
                                 ^

        */
        warning("""
              signed char foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf1_24() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              signed char foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:36: warning: initialization makes pointer from integer without a cast
                 volatile int * b = foo();
                                    ^

        */
        warning("""
              signed char foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf2_0() {
        correct("""
              unsigned char foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        correct("""
              unsigned char foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf2_1() {
        correct("""
              unsigned char foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        correct("""
              unsigned char foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf2_2() {
        correct("""
              unsigned char foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        correct("""
              unsigned char foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf2_3() {
        correct("""
              unsigned char foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        correct("""
              unsigned char foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf2_4() {
        correct("""
              unsigned char foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        correct("""
              unsigned char foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf2_5() {
        correct("""
              unsigned char foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        correct("""
              unsigned char foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf2_6() {
        correct("""
              unsigned char foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        correct("""
              unsigned char foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf2_7() {
        correct("""
              unsigned char foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        correct("""
              unsigned char foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf2_8() {
        correct("""
              unsigned char foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        correct("""
              unsigned char foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf2_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              unsigned char foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization makes pointer from integer without a cast
                 int * b = foo();
                           ^

        */
        warning("""
              unsigned char foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf2_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              unsigned char foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization makes pointer from integer without a cast
                 int ** b = foo();
                            ^

        */
        warning("""
              unsigned char foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf2_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              unsigned char foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization makes pointer from integer without a cast
                 char * b = foo();
                            ^

        */
        warning("""
              unsigned char foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf2_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              unsigned char foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:30: warning: initialization makes pointer from integer without a cast
                 double * b = foo();
                              ^

        */
        warning("""
              unsigned char foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf2_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'unsigned char'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              unsigned char foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              unsigned char foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf2_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'unsigned char'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              unsigned char foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              unsigned char foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf2_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'unsigned char'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              unsigned char foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              unsigned char foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf2_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'unsigned char'
                 b = foo();
                   ^

        */
        error("""
              unsigned char foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              unsigned char foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf2_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'unsigned char'
                 b = foo();
                   ^

        */
        error("""
              unsigned char foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              unsigned char foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf2_18() {
        correct("""
              unsigned char foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        correct("""
              unsigned char foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf2_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              unsigned char foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        correct("""
              unsigned char foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf2_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              unsigned char foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        correct("""
              unsigned char foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf2_21() {
        correct("""
              unsigned char foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        correct("""
              unsigned char foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf2_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              unsigned char foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization makes pointer from integer without a cast
                 int * b = foo();
                           ^

        */
        warning("""
              unsigned char foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf2_23() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              unsigned char foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: warning: initialization makes pointer from integer without a cast
                 const int * b = foo();
                                 ^

        */
        warning("""
              unsigned char foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf2_24() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              unsigned char foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:36: warning: initialization makes pointer from integer without a cast
                 volatile int * b = foo();
                                    ^

        */
        warning("""
              unsigned char foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf3_0() {
        correct("""
              unsigned int foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        correct("""
              unsigned int foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf3_1() {
        correct("""
              unsigned int foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        correct("""
              unsigned int foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf3_2() {
        correct("""
              unsigned int foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        correct("""
              unsigned int foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf3_3() {
        correct("""
              unsigned int foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        correct("""
              unsigned int foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf3_4() {
        correct("""
              unsigned int foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        correct("""
              unsigned int foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf3_5() {
        correct("""
              unsigned int foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        correct("""
              unsigned int foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf3_6() {
        correct("""
              unsigned int foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        correct("""
              unsigned int foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf3_7() {
        correct("""
              unsigned int foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        correct("""
              unsigned int foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf3_8() {
        correct("""
              unsigned int foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        correct("""
              unsigned int foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf3_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              unsigned int foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization makes pointer from integer without a cast
                 int * b = foo();
                           ^

        */
        warning("""
              unsigned int foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf3_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              unsigned int foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization makes pointer from integer without a cast
                 int ** b = foo();
                            ^

        */
        warning("""
              unsigned int foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf3_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              unsigned int foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization makes pointer from integer without a cast
                 char * b = foo();
                            ^

        */
        warning("""
              unsigned int foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf3_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              unsigned int foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:30: warning: initialization makes pointer from integer without a cast
                 double * b = foo();
                              ^

        */
        warning("""
              unsigned int foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf3_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'unsigned int'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              unsigned int foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              unsigned int foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf3_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'unsigned int'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              unsigned int foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              unsigned int foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf3_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'unsigned int'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              unsigned int foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              unsigned int foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf3_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'unsigned int'
                 b = foo();
                   ^

        */
        error("""
              unsigned int foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              unsigned int foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf3_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'unsigned int'
                 b = foo();
                   ^

        */
        error("""
              unsigned int foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              unsigned int foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf3_18() {
        correct("""
              unsigned int foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        correct("""
              unsigned int foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf3_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              unsigned int foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        correct("""
              unsigned int foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf3_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              unsigned int foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        correct("""
              unsigned int foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf3_21() {
        correct("""
              unsigned int foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        correct("""
              unsigned int foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf3_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              unsigned int foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization makes pointer from integer without a cast
                 int * b = foo();
                           ^

        */
        warning("""
              unsigned int foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf3_23() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              unsigned int foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: warning: initialization makes pointer from integer without a cast
                 const int * b = foo();
                                 ^

        */
        warning("""
              unsigned int foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf3_24() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              unsigned int foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:36: warning: initialization makes pointer from integer without a cast
                 volatile int * b = foo();
                                    ^

        */
        warning("""
              unsigned int foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf4_0() {
        correct("""
              signed int foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        correct("""
              signed int foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf4_1() {
        correct("""
              signed int foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        correct("""
              signed int foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf4_2() {
        correct("""
              signed int foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        correct("""
              signed int foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf4_3() {
        correct("""
              signed int foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        correct("""
              signed int foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf4_4() {
        correct("""
              signed int foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        correct("""
              signed int foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf4_5() {
        correct("""
              signed int foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        correct("""
              signed int foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf4_6() {
        correct("""
              signed int foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        correct("""
              signed int foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf4_7() {
        correct("""
              signed int foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        correct("""
              signed int foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf4_8() {
        correct("""
              signed int foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        correct("""
              signed int foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf4_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              signed int foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization makes pointer from integer without a cast
                 int * b = foo();
                           ^

        */
        warning("""
              signed int foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf4_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              signed int foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization makes pointer from integer without a cast
                 int ** b = foo();
                            ^

        */
        warning("""
              signed int foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf4_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              signed int foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization makes pointer from integer without a cast
                 char * b = foo();
                            ^

        */
        warning("""
              signed int foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf4_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              signed int foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:30: warning: initialization makes pointer from integer without a cast
                 double * b = foo();
                              ^

        */
        warning("""
              signed int foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf4_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'int'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              signed int foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              signed int foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf4_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'int'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              signed int foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              signed int foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf4_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'int'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              signed int foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              signed int foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf4_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int'
                 b = foo();
                   ^

        */
        error("""
              signed int foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              signed int foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf4_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int'
                 b = foo();
                   ^

        */
        error("""
              signed int foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              signed int foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf4_18() {
        correct("""
              signed int foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        correct("""
              signed int foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf4_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              signed int foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        correct("""
              signed int foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf4_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              signed int foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        correct("""
              signed int foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf4_21() {
        correct("""
              signed int foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        correct("""
              signed int foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf4_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              signed int foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization makes pointer from integer without a cast
                 int * b = foo();
                           ^

        */
        warning("""
              signed int foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf4_23() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              signed int foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: warning: initialization makes pointer from integer without a cast
                 const int * b = foo();
                                 ^

        */
        warning("""
              signed int foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf4_24() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              signed int foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:36: warning: initialization makes pointer from integer without a cast
                 volatile int * b = foo();
                                    ^

        */
        warning("""
              signed int foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf5_0() {
        correct("""
              long foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        correct("""
              long foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf5_1() {
        correct("""
              long foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        correct("""
              long foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf5_2() {
        correct("""
              long foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        correct("""
              long foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf5_3() {
        correct("""
              long foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        correct("""
              long foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf5_4() {
        correct("""
              long foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        correct("""
              long foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf5_5() {
        correct("""
              long foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        correct("""
              long foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf5_6() {
        correct("""
              long foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        correct("""
              long foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf5_7() {
        correct("""
              long foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        correct("""
              long foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf5_8() {
        correct("""
              long foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        correct("""
              long foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf5_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              long foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization makes pointer from integer without a cast
                 int * b = foo();
                           ^

        */
        warning("""
              long foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf5_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              long foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization makes pointer from integer without a cast
                 int ** b = foo();
                            ^

        */
        warning("""
              long foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf5_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              long foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization makes pointer from integer without a cast
                 char * b = foo();
                            ^

        */
        warning("""
              long foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf5_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              long foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:30: warning: initialization makes pointer from integer without a cast
                 double * b = foo();
                              ^

        */
        warning("""
              long foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf5_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'long int'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              long foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              long foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf5_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'long int'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              long foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              long foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf5_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'long int'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              long foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              long foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf5_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'long int'
                 b = foo();
                   ^

        */
        error("""
              long foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              long foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf5_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'long int'
                 b = foo();
                   ^

        */
        error("""
              long foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              long foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf5_18() {
        correct("""
              long foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        correct("""
              long foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf5_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              long foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        correct("""
              long foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf5_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              long foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        correct("""
              long foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf5_21() {
        correct("""
              long foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        correct("""
              long foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf5_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              long foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization makes pointer from integer without a cast
                 int * b = foo();
                           ^

        */
        warning("""
              long foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf5_23() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              long foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: warning: initialization makes pointer from integer without a cast
                 const int * b = foo();
                                 ^

        */
        warning("""
              long foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf5_24() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              long foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:36: warning: initialization makes pointer from integer without a cast
                 volatile int * b = foo();
                                    ^

        */
        warning("""
              long foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf6_0() {
        correct("""
              float foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        correct("""
              float foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf6_1() {
        correct("""
              float foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        correct("""
              float foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf6_2() {
        correct("""
              float foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        correct("""
              float foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf6_3() {
        correct("""
              float foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        correct("""
              float foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf6_4() {
        correct("""
              float foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        correct("""
              float foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf6_5() {
        correct("""
              float foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        correct("""
              float foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf6_6() {
        correct("""
              float foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        correct("""
              float foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf6_7() {
        correct("""
              float foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        correct("""
              float foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf6_8() {
        correct("""
              float foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        correct("""
              float foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf6_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int *' from type 'float'
                 b = foo();
                   ^

        */
        error("""
              float foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'int *' using type 'float'
                 int * b = foo();
                           ^

        */
        error("""
              float foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf6_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int **' from type 'float'
                 b = foo();
                   ^

        */
        error("""
              float foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'int **' using type 'float'
                 int ** b = foo();
                            ^

        */
        error("""
              float foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf6_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'char *' from type 'float'
                 b = foo();
                   ^

        */
        error("""
              float foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'char *' using type 'float'
                 char * b = foo();
                            ^

        */
        error("""
              float foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf6_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double *' from type 'float'
                 b = foo();
                   ^

        */
        error("""
              float foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:30: error: incompatible types when initializing type 'double *' using type 'float'
                 double * b = foo();
                              ^

        */
        error("""
              float foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf6_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'float'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              float foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              float foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf6_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'float'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              float foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              float foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf6_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'float'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              float foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              float foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf6_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'float'
                 b = foo();
                   ^

        */
        error("""
              float foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              float foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf6_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'float'
                 b = foo();
                   ^

        */
        error("""
              float foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              float foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf6_18() {
        correct("""
              float foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        correct("""
              float foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf6_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              float foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        correct("""
              float foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf6_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              float foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        correct("""
              float foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf6_21() {
        correct("""
              float foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        correct("""
              float foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf6_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int *' from type 'float'
                 b = foo();
                   ^

        */
        error("""
              float foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'int *' using type 'float'
                 int * b = foo();
                           ^

        */
        error("""
              float foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf6_23() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'const int *' from type 'float'
                 b = foo();
                   ^

        */
        error("""
              float foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: error: incompatible types when initializing type 'const int *' using type 'float'
                 const int * b = foo();
                                 ^

        */
        error("""
              float foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf6_24() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'volatile int *' from type 'float'
                 b = foo();
                   ^

        */
        error("""
              float foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:36: error: incompatible types when initializing type 'volatile int *' using type 'float'
                 volatile int * b = foo();
                                    ^

        */
        error("""
              float foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf7_0() {
        correct("""
              double foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        correct("""
              double foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf7_1() {
        correct("""
              double foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        correct("""
              double foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf7_2() {
        correct("""
              double foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        correct("""
              double foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf7_3() {
        correct("""
              double foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        correct("""
              double foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf7_4() {
        correct("""
              double foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        correct("""
              double foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf7_5() {
        correct("""
              double foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        correct("""
              double foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf7_6() {
        correct("""
              double foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        correct("""
              double foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf7_7() {
        correct("""
              double foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        correct("""
              double foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf7_8() {
        correct("""
              double foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        correct("""
              double foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf7_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int *' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              double foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'int *' using type 'double'
                 int * b = foo();
                           ^

        */
        error("""
              double foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf7_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int **' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              double foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'int **' using type 'double'
                 int ** b = foo();
                            ^

        */
        error("""
              double foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf7_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'char *' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              double foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'char *' using type 'double'
                 char * b = foo();
                            ^

        */
        error("""
              double foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf7_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double *' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              double foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:30: error: incompatible types when initializing type 'double *' using type 'double'
                 double * b = foo();
                              ^

        */
        error("""
              double foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf7_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              double foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              double foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf7_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              double foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              double foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf7_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              double foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              double foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf7_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              double foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              double foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf7_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              double foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              double foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf7_18() {
        correct("""
              double foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        correct("""
              double foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf7_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              double foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        correct("""
              double foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf7_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              double foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        correct("""
              double foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf7_21() {
        correct("""
              double foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        correct("""
              double foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf7_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int *' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              double foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'int *' using type 'double'
                 int * b = foo();
                           ^

        */
        error("""
              double foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf7_23() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'const int *' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              double foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: error: incompatible types when initializing type 'const int *' using type 'double'
                 const int * b = foo();
                                 ^

        */
        error("""
              double foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf7_24() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'volatile int *' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              double foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:36: error: incompatible types when initializing type 'volatile int *' using type 'double'
                 volatile int * b = foo();
                                    ^

        */
        error("""
              double foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf8_0() {
        correct("""
              long double foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        correct("""
              long double foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf8_1() {
        correct("""
              long double foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        correct("""
              long double foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf8_2() {
        correct("""
              long double foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        correct("""
              long double foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf8_3() {
        correct("""
              long double foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        correct("""
              long double foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf8_4() {
        correct("""
              long double foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        correct("""
              long double foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf8_5() {
        correct("""
              long double foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        correct("""
              long double foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf8_6() {
        correct("""
              long double foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        correct("""
              long double foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf8_7() {
        correct("""
              long double foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        correct("""
              long double foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf8_8() {
        correct("""
              long double foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        correct("""
              long double foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf8_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int *' from type 'long double'
                 b = foo();
                   ^

        */
        error("""
              long double foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'int *' using type 'long double'
                 int * b = foo();
                           ^

        */
        error("""
              long double foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf8_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int **' from type 'long double'
                 b = foo();
                   ^

        */
        error("""
              long double foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'int **' using type 'long double'
                 int ** b = foo();
                            ^

        */
        error("""
              long double foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf8_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'char *' from type 'long double'
                 b = foo();
                   ^

        */
        error("""
              long double foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'char *' using type 'long double'
                 char * b = foo();
                            ^

        */
        error("""
              long double foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf8_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double *' from type 'long double'
                 b = foo();
                   ^

        */
        error("""
              long double foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:30: error: incompatible types when initializing type 'double *' using type 'long double'
                 double * b = foo();
                              ^

        */
        error("""
              long double foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf8_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'long double'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              long double foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              long double foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf8_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'long double'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              long double foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              long double foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf8_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'long double'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              long double foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              long double foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf8_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'long double'
                 b = foo();
                   ^

        */
        error("""
              long double foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              long double foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf8_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'long double'
                 b = foo();
                   ^

        */
        error("""
              long double foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              long double foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf8_18() {
        correct("""
              long double foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        correct("""
              long double foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf8_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              long double foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        correct("""
              long double foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf8_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              long double foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        correct("""
              long double foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf8_21() {
        correct("""
              long double foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        correct("""
              long double foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf8_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int *' from type 'long double'
                 b = foo();
                   ^

        */
        error("""
              long double foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'int *' using type 'long double'
                 int * b = foo();
                           ^

        */
        error("""
              long double foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf8_23() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'const int *' from type 'long double'
                 b = foo();
                   ^

        */
        error("""
              long double foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: error: incompatible types when initializing type 'const int *' using type 'long double'
                 const int * b = foo();
                                 ^

        */
        error("""
              long double foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf8_24() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'volatile int *' from type 'long double'
                 b = foo();
                   ^

        */
        error("""
              long double foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:36: error: incompatible types when initializing type 'volatile int *' using type 'long double'
                 volatile int * b = foo();
                                    ^

        */
        error("""
              long double foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf9_0() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              int * foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:26: warning: initialization makes integer from pointer without a cast
                 char b = foo();
                          ^

        */
        warning("""
              int * foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf9_1() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              int * foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: warning: initialization makes integer from pointer without a cast
                 signed char b = foo();
                                 ^

        */
        warning("""
              int * foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf9_2() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              int * foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:35: warning: initialization makes integer from pointer without a cast
                 unsigned char b = foo();
                                   ^

        */
        warning("""
              int * foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf9_3() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              int * foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: warning: initialization makes integer from pointer without a cast
                 unsigned int b = foo();
                                  ^

        */
        warning("""
              int * foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf9_4() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              int * foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:32: warning: initialization makes integer from pointer without a cast
                 signed int b = foo();
                                ^

        */
        warning("""
              int * foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf9_5() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              int * foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:26: warning: initialization makes integer from pointer without a cast
                 long b = foo();
                          ^

        */
        warning("""
              int * foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf9_6() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'float' from type 'int *'
                 b = foo();
                   ^

        */
        error("""
              int * foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'float' using type 'int *'
                 float b = foo();
                           ^

        */
        error("""
              int * foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf9_7() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double' from type 'int *'
                 b = foo();
                   ^

        */
        error("""
              int * foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'double' using type 'int *'
                 double b = foo();
                            ^

        */
        error("""
              int * foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf9_8() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'long double' from type 'int *'
                 b = foo();
                   ^

        */
        error("""
              int * foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: error: incompatible types when initializing type 'long double' using type 'int *'
                 long double b = foo();
                                 ^

        */
        error("""
              int * foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf9_9() {
        correct("""
              int * foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        correct("""
              int * foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf9_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              int * foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization from incompatible pointer type
                 int ** b = foo();
                            ^

        */
        warning("""
              int * foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf9_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              int * foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization from incompatible pointer type
                 char * b = foo();
                            ^

        */
        warning("""
              int * foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf9_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              int * foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:30: warning: initialization from incompatible pointer type
                 double * b = foo();
                              ^

        */
        warning("""
              int * foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf9_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'int *'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              int * foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              int * foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf9_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'int *'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              int * foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              int * foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf9_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'int *'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              int * foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              int * foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf9_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int *'
                 b = foo();
                   ^

        */
        error("""
              int * foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              int * foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf9_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int *'
                 b = foo();
                   ^

        */
        error("""
              int * foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              int * foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf9_18() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              int * foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: warning: initialization makes integer from pointer without a cast
                 volatile int b = foo();
                                  ^

        */
        warning("""
              int * foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf9_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              int * foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:31: warning: initialization makes integer from pointer without a cast
                 const int b = foo();
                               ^

        */
        warning("""
              int * foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf9_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              int * foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: error: incompatible types when initializing type 'double' using type 'int *'
                 const double b = foo();
                                  ^

        */
        error("""
              int * foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf9_21() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double' from type 'int *'
                 b = foo();
                   ^

        */
        error("""
              int * foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:37: error: incompatible types when initializing type 'double' using type 'int *'
                 volatile double b = foo();
                                     ^

        */
        error("""
              int * foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf9_22() {
        correct("""
              int * foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        correct("""
              int * foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf9_23() {
        correct("""
              int * foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        correct("""
              int * foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf9_24() {
        correct("""
              int * foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        correct("""
              int * foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf10_0() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              int ** foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:26: warning: initialization makes integer from pointer without a cast
                 char b = foo();
                          ^

        */
        warning("""
              int ** foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf10_1() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              int ** foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: warning: initialization makes integer from pointer without a cast
                 signed char b = foo();
                                 ^

        */
        warning("""
              int ** foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf10_2() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              int ** foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:35: warning: initialization makes integer from pointer without a cast
                 unsigned char b = foo();
                                   ^

        */
        warning("""
              int ** foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf10_3() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              int ** foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: warning: initialization makes integer from pointer without a cast
                 unsigned int b = foo();
                                  ^

        */
        warning("""
              int ** foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf10_4() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              int ** foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:32: warning: initialization makes integer from pointer without a cast
                 signed int b = foo();
                                ^

        */
        warning("""
              int ** foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf10_5() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              int ** foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:26: warning: initialization makes integer from pointer without a cast
                 long b = foo();
                          ^

        */
        warning("""
              int ** foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf10_6() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'float' from type 'int **'
                 b = foo();
                   ^

        */
        error("""
              int ** foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'float' using type 'int **'
                 float b = foo();
                           ^

        */
        error("""
              int ** foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf10_7() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double' from type 'int **'
                 b = foo();
                   ^

        */
        error("""
              int ** foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'double' using type 'int **'
                 double b = foo();
                            ^

        */
        error("""
              int ** foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf10_8() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'long double' from type 'int **'
                 b = foo();
                   ^

        */
        error("""
              int ** foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: error: incompatible types when initializing type 'long double' using type 'int **'
                 long double b = foo();
                                 ^

        */
        error("""
              int ** foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf10_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              int ** foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization from incompatible pointer type
                 int * b = foo();
                           ^

        */
        warning("""
              int ** foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf10_10() {
        correct("""
              int ** foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        correct("""
              int ** foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf10_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              int ** foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization from incompatible pointer type
                 char * b = foo();
                            ^

        */
        warning("""
              int ** foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf10_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              int ** foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:30: warning: initialization from incompatible pointer type
                 double * b = foo();
                              ^

        */
        warning("""
              int ** foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf10_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'int **'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              int ** foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              int ** foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf10_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'int **'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              int ** foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              int ** foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf10_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'int **'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              int ** foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              int ** foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf10_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int **'
                 b = foo();
                   ^

        */
        error("""
              int ** foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              int ** foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf10_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int **'
                 b = foo();
                   ^

        */
        error("""
              int ** foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              int ** foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf10_18() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              int ** foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: warning: initialization makes integer from pointer without a cast
                 volatile int b = foo();
                                  ^

        */
        warning("""
              int ** foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf10_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              int ** foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:31: warning: initialization makes integer from pointer without a cast
                 const int b = foo();
                               ^

        */
        warning("""
              int ** foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf10_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              int ** foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: error: incompatible types when initializing type 'double' using type 'int **'
                 const double b = foo();
                                  ^

        */
        error("""
              int ** foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf10_21() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double' from type 'int **'
                 b = foo();
                   ^

        */
        error("""
              int ** foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:37: error: incompatible types when initializing type 'double' using type 'int **'
                 volatile double b = foo();
                                     ^

        */
        error("""
              int ** foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf10_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              int ** foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization from incompatible pointer type
                 int * b = foo();
                           ^

        */
        warning("""
              int ** foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf10_23() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              int ** foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: warning: initialization from incompatible pointer type
                 const int * b = foo();
                                 ^

        */
        warning("""
              int ** foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf10_24() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              int ** foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:36: warning: initialization from incompatible pointer type
                 volatile int * b = foo();
                                    ^

        */
        warning("""
              int ** foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf11_0() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              char * foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:26: warning: initialization makes integer from pointer without a cast
                 char b = foo();
                          ^

        */
        warning("""
              char * foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf11_1() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              char * foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: warning: initialization makes integer from pointer without a cast
                 signed char b = foo();
                                 ^

        */
        warning("""
              char * foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf11_2() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              char * foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:35: warning: initialization makes integer from pointer without a cast
                 unsigned char b = foo();
                                   ^

        */
        warning("""
              char * foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf11_3() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              char * foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: warning: initialization makes integer from pointer without a cast
                 unsigned int b = foo();
                                  ^

        */
        warning("""
              char * foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf11_4() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              char * foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:32: warning: initialization makes integer from pointer without a cast
                 signed int b = foo();
                                ^

        */
        warning("""
              char * foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf11_5() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              char * foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:26: warning: initialization makes integer from pointer without a cast
                 long b = foo();
                          ^

        */
        warning("""
              char * foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf11_6() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'float' from type 'char *'
                 b = foo();
                   ^

        */
        error("""
              char * foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'float' using type 'char *'
                 float b = foo();
                           ^

        */
        error("""
              char * foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf11_7() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double' from type 'char *'
                 b = foo();
                   ^

        */
        error("""
              char * foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'double' using type 'char *'
                 double b = foo();
                            ^

        */
        error("""
              char * foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf11_8() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'long double' from type 'char *'
                 b = foo();
                   ^

        */
        error("""
              char * foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: error: incompatible types when initializing type 'long double' using type 'char *'
                 long double b = foo();
                                 ^

        */
        error("""
              char * foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf11_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              char * foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization from incompatible pointer type
                 int * b = foo();
                           ^

        */
        warning("""
              char * foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf11_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              char * foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization from incompatible pointer type
                 int ** b = foo();
                            ^

        */
        warning("""
              char * foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf11_11() {
        correct("""
              char * foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        correct("""
              char * foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf11_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              char * foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:30: warning: initialization from incompatible pointer type
                 double * b = foo();
                              ^

        */
        warning("""
              char * foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf11_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'char *'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              char * foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              char * foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf11_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'char *'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              char * foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              char * foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf11_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'char *'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              char * foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              char * foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf11_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'char *'
                 b = foo();
                   ^

        */
        error("""
              char * foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              char * foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf11_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'char *'
                 b = foo();
                   ^

        */
        error("""
              char * foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              char * foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf11_18() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              char * foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: warning: initialization makes integer from pointer without a cast
                 volatile int b = foo();
                                  ^

        */
        warning("""
              char * foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf11_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              char * foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:31: warning: initialization makes integer from pointer without a cast
                 const int b = foo();
                               ^

        */
        warning("""
              char * foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf11_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              char * foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: error: incompatible types when initializing type 'double' using type 'char *'
                 const double b = foo();
                                  ^

        */
        error("""
              char * foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf11_21() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double' from type 'char *'
                 b = foo();
                   ^

        */
        error("""
              char * foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:37: error: incompatible types when initializing type 'double' using type 'char *'
                 volatile double b = foo();
                                     ^

        */
        error("""
              char * foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf11_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              char * foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization from incompatible pointer type
                 int * b = foo();
                           ^

        */
        warning("""
              char * foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf11_23() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              char * foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: warning: initialization from incompatible pointer type
                 const int * b = foo();
                                 ^

        */
        warning("""
              char * foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf11_24() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              char * foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:36: warning: initialization from incompatible pointer type
                 volatile int * b = foo();
                                    ^

        */
        warning("""
              char * foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf12_0() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              double * foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:26: warning: initialization makes integer from pointer without a cast
                 char b = foo();
                          ^

        */
        warning("""
              double * foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf12_1() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              double * foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: warning: initialization makes integer from pointer without a cast
                 signed char b = foo();
                                 ^

        */
        warning("""
              double * foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf12_2() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              double * foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:35: warning: initialization makes integer from pointer without a cast
                 unsigned char b = foo();
                                   ^

        */
        warning("""
              double * foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf12_3() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              double * foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: warning: initialization makes integer from pointer without a cast
                 unsigned int b = foo();
                                  ^

        */
        warning("""
              double * foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf12_4() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              double * foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:32: warning: initialization makes integer from pointer without a cast
                 signed int b = foo();
                                ^

        */
        warning("""
              double * foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf12_5() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              double * foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:26: warning: initialization makes integer from pointer without a cast
                 long b = foo();
                          ^

        */
        warning("""
              double * foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf12_6() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'float' from type 'double *'
                 b = foo();
                   ^

        */
        error("""
              double * foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'float' using type 'double *'
                 float b = foo();
                           ^

        */
        error("""
              double * foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf12_7() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double' from type 'double *'
                 b = foo();
                   ^

        */
        error("""
              double * foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'double' using type 'double *'
                 double b = foo();
                            ^

        */
        error("""
              double * foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf12_8() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'long double' from type 'double *'
                 b = foo();
                   ^

        */
        error("""
              double * foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: error: incompatible types when initializing type 'long double' using type 'double *'
                 long double b = foo();
                                 ^

        */
        error("""
              double * foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf12_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              double * foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization from incompatible pointer type
                 int * b = foo();
                           ^

        */
        warning("""
              double * foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf12_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              double * foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization from incompatible pointer type
                 int ** b = foo();
                            ^

        */
        warning("""
              double * foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf12_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              double * foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization from incompatible pointer type
                 char * b = foo();
                            ^

        */
        warning("""
              double * foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf12_12() {
        correct("""
              double * foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        correct("""
              double * foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf12_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'double *'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              double * foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              double * foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf12_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'double *'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              double * foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              double * foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf12_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'double *'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              double * foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              double * foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf12_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double *'
                 b = foo();
                   ^

        */
        error("""
              double * foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              double * foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf12_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double *'
                 b = foo();
                   ^

        */
        error("""
              double * foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              double * foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf12_18() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              double * foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: warning: initialization makes integer from pointer without a cast
                 volatile int b = foo();
                                  ^

        */
        warning("""
              double * foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf12_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              double * foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:31: warning: initialization makes integer from pointer without a cast
                 const int b = foo();
                               ^

        */
        warning("""
              double * foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf12_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              double * foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: error: incompatible types when initializing type 'double' using type 'double *'
                 const double b = foo();
                                  ^

        */
        error("""
              double * foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf12_21() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double' from type 'double *'
                 b = foo();
                   ^

        */
        error("""
              double * foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:37: error: incompatible types when initializing type 'double' using type 'double *'
                 volatile double b = foo();
                                     ^

        */
        error("""
              double * foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf12_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              double * foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization from incompatible pointer type
                 int * b = foo();
                           ^

        */
        warning("""
              double * foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf12_23() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              double * foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: warning: initialization from incompatible pointer type
                 const int * b = foo();
                                 ^

        */
        warning("""
              double * foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf12_24() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              double * foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:36: warning: initialization from incompatible pointer type
                 volatile int * b = foo();
                                    ^

        */
        warning("""
              double * foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf13_0() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'char' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:26: error: incompatible types when initializing type 'char' using type 'struct S'
                 char b = foo();
                          ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf13_1() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'signed char' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:33: error: incompatible types when initializing type 'signed char' using type 'struct S'
                 signed char b = foo();
                                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf13_2() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'unsigned char' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:35: error: incompatible types when initializing type 'unsigned char' using type 'struct S'
                 unsigned char b = foo();
                                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf13_3() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'unsigned int' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:34: error: incompatible types when initializing type 'unsigned int' using type 'struct S'
                 unsigned int b = foo();
                                  ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf13_4() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'int' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:32: error: incompatible types when initializing type 'int' using type 'struct S'
                 signed int b = foo();
                                ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf13_5() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'long int' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:26: error: incompatible types when initializing type 'long int' using type 'struct S'
                 long b = foo();
                          ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf13_6() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'float' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:27: error: incompatible types when initializing type 'float' using type 'struct S'
                 float b = foo();
                           ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf13_7() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'double' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:28: error: incompatible types when initializing type 'double' using type 'struct S'
                 double b = foo();
                            ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf13_8() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'long double' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:33: error: incompatible types when initializing type 'long double' using type 'struct S'
                 long double b = foo();
                                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf13_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'int *' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:27: error: incompatible types when initializing type 'int *' using type 'struct S'
                 int * b = foo();
                           ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf13_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'int **' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:28: error: incompatible types when initializing type 'int **' using type 'struct S'
                 int ** b = foo();
                            ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf13_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'char *' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:28: error: incompatible types when initializing type 'char *' using type 'struct S'
                 char * b = foo();
                            ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf13_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'double *' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:30: error: incompatible types when initializing type 'double *' using type 'struct S'
                 double * b = foo();
                              ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf13_13() {
        correct("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        correct("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf13_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:8:19: error: incompatible types when assigning to type 'struct T' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct S { int x; int y; };

              struct S foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:7:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct S { int x; int y; };

              struct S foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf13_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:8:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct S { int x; int y; };

              struct S foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:7:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct S { int x; int y; };

              struct S foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf13_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf13_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf13_18() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'int' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:34: error: incompatible types when initializing type 'int' using type 'struct S'
                 volatile int b = foo();
                                  ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf13_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:31: error: incompatible types when initializing type 'int' using type 'struct S'
                 const int b = foo();
                               ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf13_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:34: error: incompatible types when initializing type 'double' using type 'struct S'
                 const double b = foo();
                                  ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf13_21() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'double' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:37: error: incompatible types when initializing type 'double' using type 'struct S'
                 volatile double b = foo();
                                     ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf13_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'int *' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:27: error: incompatible types when initializing type 'int *' using type 'struct S'
                 int * b = foo();
                           ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf13_23() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'const int *' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:33: error: incompatible types when initializing type 'const int *' using type 'struct S'
                 const int * b = foo();
                                 ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf13_24() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'volatile int *' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:36: error: incompatible types when initializing type 'volatile int *' using type 'struct S'
                 volatile int * b = foo();
                                    ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf14_0() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'char' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:26: error: incompatible types when initializing type 'char' using type 'struct T'
                 char b = foo();
                          ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf14_1() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'signed char' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:33: error: incompatible types when initializing type 'signed char' using type 'struct T'
                 signed char b = foo();
                                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf14_2() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'unsigned char' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:35: error: incompatible types when initializing type 'unsigned char' using type 'struct T'
                 unsigned char b = foo();
                                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf14_3() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'unsigned int' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:34: error: incompatible types when initializing type 'unsigned int' using type 'struct T'
                 unsigned int b = foo();
                                  ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf14_4() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'int' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:32: error: incompatible types when initializing type 'int' using type 'struct T'
                 signed int b = foo();
                                ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf14_5() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'long int' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:26: error: incompatible types when initializing type 'long int' using type 'struct T'
                 long b = foo();
                          ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf14_6() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'float' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:27: error: incompatible types when initializing type 'float' using type 'struct T'
                 float b = foo();
                           ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf14_7() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'double' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:28: error: incompatible types when initializing type 'double' using type 'struct T'
                 double b = foo();
                            ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf14_8() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'long double' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:33: error: incompatible types when initializing type 'long double' using type 'struct T'
                 long double b = foo();
                                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf14_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'int *' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:27: error: incompatible types when initializing type 'int *' using type 'struct T'
                 int * b = foo();
                           ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf14_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'int **' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:28: error: incompatible types when initializing type 'int **' using type 'struct T'
                 int ** b = foo();
                            ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf14_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'char *' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:28: error: incompatible types when initializing type 'char *' using type 'struct T'
                 char * b = foo();
                            ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf14_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'double *' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:30: error: incompatible types when initializing type 'double *' using type 'struct T'
                 double * b = foo();
                              ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf14_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:8:19: error: incompatible types when assigning to type 'struct S' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct S { int x; int y; };

              struct T foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:7:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct S { int x; int y; };

              struct T foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf14_14() {
        correct("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        correct("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf14_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:8:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:7:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf14_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf14_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf14_18() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'int' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:34: error: incompatible types when initializing type 'int' using type 'struct T'
                 volatile int b = foo();
                                  ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf14_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:31: error: incompatible types when initializing type 'int' using type 'struct T'
                 const int b = foo();
                               ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf14_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:34: error: incompatible types when initializing type 'double' using type 'struct T'
                 const double b = foo();
                                  ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf14_21() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'double' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:37: error: incompatible types when initializing type 'double' using type 'struct T'
                 volatile double b = foo();
                                     ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf14_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'int *' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:27: error: incompatible types when initializing type 'int *' using type 'struct T'
                 int * b = foo();
                           ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf14_23() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'const int *' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:33: error: incompatible types when initializing type 'const int *' using type 'struct T'
                 const int * b = foo();
                                 ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf14_24() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'volatile int *' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:36: error: incompatible types when initializing type 'volatile int *' using type 'struct T'
                 volatile int * b = foo();
                                    ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf15_0() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'char' from type 'struct_anonymous'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:26: error: incompatible types when initializing type 'char' using type 'struct_anonymous'
                 char b = foo();
                          ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf15_1() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'signed char' from type 'struct_anonymous'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:33: error: incompatible types when initializing type 'signed char' using type 'struct_anonymous'
                 signed char b = foo();
                                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf15_2() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'unsigned char' from type 'struct_anonymous'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:35: error: incompatible types when initializing type 'unsigned char' using type 'struct_anonymous'
                 unsigned char b = foo();
                                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf15_3() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'unsigned int' from type 'struct_anonymous'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:34: error: incompatible types when initializing type 'unsigned int' using type 'struct_anonymous'
                 unsigned int b = foo();
                                  ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf15_4() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'int' from type 'struct_anonymous'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:32: error: incompatible types when initializing type 'int' using type 'struct_anonymous'
                 signed int b = foo();
                                ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf15_5() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'long int' from type 'struct_anonymous'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:26: error: incompatible types when initializing type 'long int' using type 'struct_anonymous'
                 long b = foo();
                          ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf15_6() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'float' from type 'struct_anonymous'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:27: error: incompatible types when initializing type 'float' using type 'struct_anonymous'
                 float b = foo();
                           ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf15_7() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'double' from type 'struct_anonymous'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:28: error: incompatible types when initializing type 'double' using type 'struct_anonymous'
                 double b = foo();
                            ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf15_8() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'long double' from type 'struct_anonymous'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:33: error: incompatible types when initializing type 'long double' using type 'struct_anonymous'
                 long double b = foo();
                                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf15_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'int *' from type 'struct_anonymous'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:27: error: incompatible types when initializing type 'int *' using type 'struct_anonymous'
                 int * b = foo();
                           ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf15_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'int **' from type 'struct_anonymous'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:28: error: incompatible types when initializing type 'int **' using type 'struct_anonymous'
                 int ** b = foo();
                            ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf15_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'char *' from type 'struct_anonymous'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:28: error: incompatible types when initializing type 'char *' using type 'struct_anonymous'
                 char * b = foo();
                            ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf15_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'double *' from type 'struct_anonymous'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:30: error: incompatible types when initializing type 'double *' using type 'struct_anonymous'
                 double * b = foo();
                              ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf15_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:8:19: error: incompatible types when assigning to type 'struct S' from type 'struct_anonymous'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct S { int x; int y; };

              struct_anonymous foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:7:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct S { int x; int y; };

              struct_anonymous foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf15_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:8:19: error: incompatible types when assigning to type 'struct T' from type 'struct_anonymous'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct T { int x; int y; int z; };

              struct_anonymous foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:7:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct T { int x; int y; int z; };

              struct_anonymous foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf15_15() {
        correct("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        correct("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf15_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct_anonymous'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf15_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct_anonymous'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf15_18() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'int' from type 'struct_anonymous'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:34: error: incompatible types when initializing type 'int' using type 'struct_anonymous'
                 volatile int b = foo();
                                  ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf15_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:31: error: incompatible types when initializing type 'int' using type 'struct_anonymous'
                 const int b = foo();
                               ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf15_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:34: error: incompatible types when initializing type 'double' using type 'struct_anonymous'
                 const double b = foo();
                                  ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf15_21() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'double' from type 'struct_anonymous'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:37: error: incompatible types when initializing type 'double' using type 'struct_anonymous'
                 volatile double b = foo();
                                     ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf15_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'int *' from type 'struct_anonymous'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:27: error: incompatible types when initializing type 'int *' using type 'struct_anonymous'
                 int * b = foo();
                           ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf15_23() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'const int *' from type 'struct_anonymous'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:33: error: incompatible types when initializing type 'const int *' using type 'struct_anonymous'
                 const int * b = foo();
                                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf15_24() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'volatile int *' from type 'struct_anonymous'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:36: error: incompatible types when initializing type 'volatile int *' using type 'struct_anonymous'
                 volatile int * b = foo();
                                    ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct_anonymous foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf16_0() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'char' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:26: error: incompatible types when initializing type 'char' using type 'struct <anonymous>'
                 char b = foo();
                          ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf16_1() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'signed char' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: error: incompatible types when initializing type 'signed char' using type 'struct <anonymous>'
                 signed char b = foo();
                                 ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf16_2() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'unsigned char' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:35: error: incompatible types when initializing type 'unsigned char' using type 'struct <anonymous>'
                 unsigned char b = foo();
                                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf16_3() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'unsigned int' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: error: incompatible types when initializing type 'unsigned int' using type 'struct <anonymous>'
                 unsigned int b = foo();
                                  ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf16_4() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:32: error: incompatible types when initializing type 'int' using type 'struct <anonymous>'
                 signed int b = foo();
                                ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf16_5() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'long int' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:26: error: incompatible types when initializing type 'long int' using type 'struct <anonymous>'
                 long b = foo();
                          ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf16_6() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'float' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'float' using type 'struct <anonymous>'
                 float b = foo();
                           ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf16_7() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'double' using type 'struct <anonymous>'
                 double b = foo();
                            ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf16_8() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'long double' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: error: incompatible types when initializing type 'long double' using type 'struct <anonymous>'
                 long double b = foo();
                                 ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf16_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int *' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'int *' using type 'struct <anonymous>'
                 int * b = foo();
                           ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf16_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int **' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'int **' using type 'struct <anonymous>'
                 int ** b = foo();
                            ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf16_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'char *' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'char *' using type 'struct <anonymous>'
                 char * b = foo();
                            ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf16_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double *' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:30: error: incompatible types when initializing type 'double *' using type 'struct <anonymous>'
                 double * b = foo();
                              ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf16_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct { int a; } foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              struct { int a; } foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf16_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct { int a; } foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct { int a; } foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf16_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct { int a; } foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct { int a; } foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf16_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf16_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf16_18() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: error: incompatible types when initializing type 'int' using type 'struct <anonymous>'
                 volatile int b = foo();
                                  ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf16_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:31: error: incompatible types when initializing type 'int' using type 'struct <anonymous>'
                 const int b = foo();
                               ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf16_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: error: incompatible types when initializing type 'double' using type 'struct <anonymous>'
                 const double b = foo();
                                  ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf16_21() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:37: error: incompatible types when initializing type 'double' using type 'struct <anonymous>'
                 volatile double b = foo();
                                     ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf16_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int *' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'int *' using type 'struct <anonymous>'
                 int * b = foo();
                           ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf16_23() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'const int *' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: error: incompatible types when initializing type 'const int *' using type 'struct <anonymous>'
                 const int * b = foo();
                                 ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf16_24() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'volatile int *' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:36: error: incompatible types when initializing type 'volatile int *' using type 'struct <anonymous>'
                 volatile int * b = foo();
                                    ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf17_0() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'char' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:26: error: incompatible types when initializing type 'char' using type 'struct <anonymous>'
                 char b = foo();
                          ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf17_1() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'signed char' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: error: incompatible types when initializing type 'signed char' using type 'struct <anonymous>'
                 signed char b = foo();
                                 ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf17_2() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'unsigned char' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:35: error: incompatible types when initializing type 'unsigned char' using type 'struct <anonymous>'
                 unsigned char b = foo();
                                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf17_3() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'unsigned int' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: error: incompatible types when initializing type 'unsigned int' using type 'struct <anonymous>'
                 unsigned int b = foo();
                                  ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf17_4() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:32: error: incompatible types when initializing type 'int' using type 'struct <anonymous>'
                 signed int b = foo();
                                ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf17_5() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'long int' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:26: error: incompatible types when initializing type 'long int' using type 'struct <anonymous>'
                 long b = foo();
                          ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf17_6() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'float' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'float' using type 'struct <anonymous>'
                 float b = foo();
                           ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf17_7() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'double' using type 'struct <anonymous>'
                 double b = foo();
                            ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf17_8() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'long double' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: error: incompatible types when initializing type 'long double' using type 'struct <anonymous>'
                 long double b = foo();
                                 ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf17_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int *' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'int *' using type 'struct <anonymous>'
                 int * b = foo();
                           ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf17_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int **' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'int **' using type 'struct <anonymous>'
                 int ** b = foo();
                            ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf17_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'char *' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'char *' using type 'struct <anonymous>'
                 char * b = foo();
                            ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf17_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double *' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:30: error: incompatible types when initializing type 'double *' using type 'struct <anonymous>'
                 double * b = foo();
                              ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf17_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct { float b; } foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              struct { float b; } foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf17_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct { float b; } foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct { float b; } foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf17_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct { float b; } foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              struct { float b; } foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf17_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf17_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf17_18() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: error: incompatible types when initializing type 'int' using type 'struct <anonymous>'
                 volatile int b = foo();
                                  ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf17_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:31: error: incompatible types when initializing type 'int' using type 'struct <anonymous>'
                 const int b = foo();
                               ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf17_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: error: incompatible types when initializing type 'double' using type 'struct <anonymous>'
                 const double b = foo();
                                  ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf17_21() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:37: error: incompatible types when initializing type 'double' using type 'struct <anonymous>'
                 volatile double b = foo();
                                     ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf17_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int *' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'int *' using type 'struct <anonymous>'
                 int * b = foo();
                           ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf17_23() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'const int *' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: error: incompatible types when initializing type 'const int *' using type 'struct <anonymous>'
                 const int * b = foo();
                                 ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf17_24() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'volatile int *' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:36: error: incompatible types when initializing type 'volatile int *' using type 'struct <anonymous>'
                 volatile int * b = foo();
                                    ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf18_0() {
        correct("""
              volatile int foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        correct("""
              volatile int foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf18_1() {
        correct("""
              volatile int foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        correct("""
              volatile int foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf18_2() {
        correct("""
              volatile int foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        correct("""
              volatile int foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf18_3() {
        correct("""
              volatile int foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        correct("""
              volatile int foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf18_4() {
        correct("""
              volatile int foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        correct("""
              volatile int foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf18_5() {
        correct("""
              volatile int foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        correct("""
              volatile int foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf18_6() {
        correct("""
              volatile int foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        correct("""
              volatile int foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf18_7() {
        correct("""
              volatile int foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        correct("""
              volatile int foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf18_8() {
        correct("""
              volatile int foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        correct("""
              volatile int foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf18_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              volatile int foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization makes pointer from integer without a cast
                 int * b = foo();
                           ^

        */
        warning("""
              volatile int foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf18_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              volatile int foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization makes pointer from integer without a cast
                 int ** b = foo();
                            ^

        */
        warning("""
              volatile int foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf18_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              volatile int foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization makes pointer from integer without a cast
                 char * b = foo();
                            ^

        */
        warning("""
              volatile int foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf18_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              volatile int foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:30: warning: initialization makes pointer from integer without a cast
                 double * b = foo();
                              ^

        */
        warning("""
              volatile int foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf18_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'int'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              volatile int foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              volatile int foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf18_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'int'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              volatile int foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              volatile int foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf18_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'int'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              volatile int foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              volatile int foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf18_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int'
                 b = foo();
                   ^

        */
        error("""
              volatile int foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              volatile int foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf18_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int'
                 b = foo();
                   ^

        */
        error("""
              volatile int foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              volatile int foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf18_18() {
        correct("""
              volatile int foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        correct("""
              volatile int foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf18_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              volatile int foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        correct("""
              volatile int foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf18_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              volatile int foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        correct("""
              volatile int foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf18_21() {
        correct("""
              volatile int foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        correct("""
              volatile int foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf18_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              volatile int foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization makes pointer from integer without a cast
                 int * b = foo();
                           ^

        */
        warning("""
              volatile int foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf18_23() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              volatile int foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: warning: initialization makes pointer from integer without a cast
                 const int * b = foo();
                                 ^

        */
        warning("""
              volatile int foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf18_24() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              volatile int foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:36: warning: initialization makes pointer from integer without a cast
                 volatile int * b = foo();
                                    ^

        */
        warning("""
              volatile int foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf19_0() {
        correct("""
              const int foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        correct("""
              const int foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf19_1() {
        correct("""
              const int foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        correct("""
              const int foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf19_2() {
        correct("""
              const int foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        correct("""
              const int foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf19_3() {
        correct("""
              const int foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        correct("""
              const int foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf19_4() {
        correct("""
              const int foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        correct("""
              const int foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf19_5() {
        correct("""
              const int foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        correct("""
              const int foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf19_6() {
        correct("""
              const int foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        correct("""
              const int foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf19_7() {
        correct("""
              const int foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        correct("""
              const int foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf19_8() {
        correct("""
              const int foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        correct("""
              const int foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf19_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              const int foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization makes pointer from integer without a cast
                 int * b = foo();
                           ^

        */
        warning("""
              const int foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf19_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              const int foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization makes pointer from integer without a cast
                 int ** b = foo();
                            ^

        */
        warning("""
              const int foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf19_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              const int foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization makes pointer from integer without a cast
                 char * b = foo();
                            ^

        */
        warning("""
              const int foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf19_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              const int foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:30: warning: initialization makes pointer from integer without a cast
                 double * b = foo();
                              ^

        */
        warning("""
              const int foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf19_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'int'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              const int foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              const int foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf19_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'int'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              const int foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              const int foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf19_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'int'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              const int foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              const int foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf19_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int'
                 b = foo();
                   ^

        */
        error("""
              const int foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              const int foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf19_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int'
                 b = foo();
                   ^

        */
        error("""
              const int foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              const int foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf19_18() {
        correct("""
              const int foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        correct("""
              const int foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf19_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              const int foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        correct("""
              const int foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf19_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              const int foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        correct("""
              const int foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf19_21() {
        correct("""
              const int foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        correct("""
              const int foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf19_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              const int foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization makes pointer from integer without a cast
                 int * b = foo();
                           ^

        */
        warning("""
              const int foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf19_23() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              const int foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: warning: initialization makes pointer from integer without a cast
                 const int * b = foo();
                                 ^

        */
        warning("""
              const int foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf19_24() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              const int foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:36: warning: initialization makes pointer from integer without a cast
                 volatile int * b = foo();
                                    ^

        */
        warning("""
              const int foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf20_0() {
        correct("""
              const double foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        correct("""
              const double foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf20_1() {
        correct("""
              const double foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        correct("""
              const double foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf20_2() {
        correct("""
              const double foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        correct("""
              const double foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf20_3() {
        correct("""
              const double foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        correct("""
              const double foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf20_4() {
        correct("""
              const double foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        correct("""
              const double foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf20_5() {
        correct("""
              const double foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        correct("""
              const double foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf20_6() {
        correct("""
              const double foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        correct("""
              const double foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf20_7() {
        correct("""
              const double foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        correct("""
              const double foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf20_8() {
        correct("""
              const double foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        correct("""
              const double foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf20_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int *' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              const double foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'int *' using type 'double'
                 int * b = foo();
                           ^

        */
        error("""
              const double foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf20_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int **' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              const double foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'int **' using type 'double'
                 int ** b = foo();
                            ^

        */
        error("""
              const double foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf20_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'char *' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              const double foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'char *' using type 'double'
                 char * b = foo();
                            ^

        */
        error("""
              const double foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf20_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double *' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              const double foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:30: error: incompatible types when initializing type 'double *' using type 'double'
                 double * b = foo();
                              ^

        */
        error("""
              const double foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf20_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              const double foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              const double foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf20_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              const double foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              const double foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf20_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              const double foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              const double foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf20_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              const double foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              const double foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf20_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              const double foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              const double foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf20_18() {
        correct("""
              const double foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        correct("""
              const double foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf20_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              const double foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        correct("""
              const double foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf20_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              const double foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        correct("""
              const double foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf20_21() {
        correct("""
              const double foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        correct("""
              const double foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf20_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int *' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              const double foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'int *' using type 'double'
                 int * b = foo();
                           ^

        */
        error("""
              const double foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf20_23() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'const int *' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              const double foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: error: incompatible types when initializing type 'const int *' using type 'double'
                 const int * b = foo();
                                 ^

        */
        error("""
              const double foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf20_24() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'volatile int *' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              const double foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:36: error: incompatible types when initializing type 'volatile int *' using type 'double'
                 volatile int * b = foo();
                                    ^

        */
        error("""
              const double foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf21_0() {
        correct("""
              volatile double foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        correct("""
              volatile double foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf21_1() {
        correct("""
              volatile double foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        correct("""
              volatile double foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf21_2() {
        correct("""
              volatile double foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        correct("""
              volatile double foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf21_3() {
        correct("""
              volatile double foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        correct("""
              volatile double foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf21_4() {
        correct("""
              volatile double foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        correct("""
              volatile double foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf21_5() {
        correct("""
              volatile double foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        correct("""
              volatile double foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf21_6() {
        correct("""
              volatile double foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        correct("""
              volatile double foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf21_7() {
        correct("""
              volatile double foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        correct("""
              volatile double foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf21_8() {
        correct("""
              volatile double foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        correct("""
              volatile double foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf21_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int *' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              volatile double foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'int *' using type 'double'
                 int * b = foo();
                           ^

        */
        error("""
              volatile double foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf21_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int **' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              volatile double foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'int **' using type 'double'
                 int ** b = foo();
                            ^

        */
        error("""
              volatile double foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf21_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'char *' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              volatile double foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'char *' using type 'double'
                 char * b = foo();
                            ^

        */
        error("""
              volatile double foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf21_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double *' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              volatile double foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:30: error: incompatible types when initializing type 'double *' using type 'double'
                 double * b = foo();
                              ^

        */
        error("""
              volatile double foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf21_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              volatile double foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              volatile double foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf21_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              volatile double foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              volatile double foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf21_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              volatile double foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              volatile double foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf21_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              volatile double foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              volatile double foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf21_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              volatile double foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              volatile double foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf21_18() {
        correct("""
              volatile double foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        correct("""
              volatile double foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf21_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              volatile double foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        correct("""
              volatile double foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf21_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              volatile double foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        correct("""
              volatile double foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf21_21() {
        correct("""
              volatile double foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        correct("""
              volatile double foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf21_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'int *' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              volatile double foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'int *' using type 'double'
                 int * b = foo();
                           ^

        */
        error("""
              volatile double foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf21_23() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'const int *' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              volatile double foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: error: incompatible types when initializing type 'const int *' using type 'double'
                 const int * b = foo();
                                 ^

        */
        error("""
              volatile double foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf21_24() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'volatile int *' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              volatile double foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:36: error: incompatible types when initializing type 'volatile int *' using type 'double'
                 volatile int * b = foo();
                                    ^

        */
        error("""
              volatile double foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf22_0() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              int * foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:26: warning: initialization makes integer from pointer without a cast
                 char b = foo();
                          ^

        */
        warning("""
              int * foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf22_1() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              int * foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: warning: initialization makes integer from pointer without a cast
                 signed char b = foo();
                                 ^

        */
        warning("""
              int * foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf22_2() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              int * foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:35: warning: initialization makes integer from pointer without a cast
                 unsigned char b = foo();
                                   ^

        */
        warning("""
              int * foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf22_3() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              int * foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: warning: initialization makes integer from pointer without a cast
                 unsigned int b = foo();
                                  ^

        */
        warning("""
              int * foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf22_4() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              int * foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:32: warning: initialization makes integer from pointer without a cast
                 signed int b = foo();
                                ^

        */
        warning("""
              int * foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf22_5() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              int * foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:26: warning: initialization makes integer from pointer without a cast
                 long b = foo();
                          ^

        */
        warning("""
              int * foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf22_6() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'float' from type 'int *'
                 b = foo();
                   ^

        */
        error("""
              int * foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'float' using type 'int *'
                 float b = foo();
                           ^

        */
        error("""
              int * foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf22_7() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double' from type 'int *'
                 b = foo();
                   ^

        */
        error("""
              int * foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'double' using type 'int *'
                 double b = foo();
                            ^

        */
        error("""
              int * foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf22_8() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'long double' from type 'int *'
                 b = foo();
                   ^

        */
        error("""
              int * foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: error: incompatible types when initializing type 'long double' using type 'int *'
                 long double b = foo();
                                 ^

        */
        error("""
              int * foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf22_9() {
        correct("""
              int * foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        correct("""
              int * foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf22_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              int * foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization from incompatible pointer type
                 int ** b = foo();
                            ^

        */
        warning("""
              int * foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf22_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              int * foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization from incompatible pointer type
                 char * b = foo();
                            ^

        */
        warning("""
              int * foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf22_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              int * foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:30: warning: initialization from incompatible pointer type
                 double * b = foo();
                              ^

        */
        warning("""
              int * foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf22_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'int *'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              int * foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              int * foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf22_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'int *'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              int * foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              int * foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf22_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'int *'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              int * foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              int * foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf22_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int *'
                 b = foo();
                   ^

        */
        error("""
              int * foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              int * foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf22_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int *'
                 b = foo();
                   ^

        */
        error("""
              int * foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              int * foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf22_18() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              int * foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: warning: initialization makes integer from pointer without a cast
                 volatile int b = foo();
                                  ^

        */
        warning("""
              int * foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf22_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              int * foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:31: warning: initialization makes integer from pointer without a cast
                 const int b = foo();
                               ^

        */
        warning("""
              int * foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf22_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              int * foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: error: incompatible types when initializing type 'double' using type 'int *'
                 const double b = foo();
                                  ^

        */
        error("""
              int * foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf22_21() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double' from type 'int *'
                 b = foo();
                   ^

        */
        error("""
              int * foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:37: error: incompatible types when initializing type 'double' using type 'int *'
                 volatile double b = foo();
                                     ^

        */
        error("""
              int * foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf22_22() {
        correct("""
              int * foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        correct("""
              int * foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf22_23() {
        correct("""
              int * foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        correct("""
              int * foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf22_24() {
        correct("""
              int * foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        correct("""
              int * foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf23_0() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              const int * foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:26: warning: initialization makes integer from pointer without a cast
                 char b = foo();
                          ^

        */
        warning("""
              const int * foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf23_1() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              const int * foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: warning: initialization makes integer from pointer without a cast
                 signed char b = foo();
                                 ^

        */
        warning("""
              const int * foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf23_2() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              const int * foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:35: warning: initialization makes integer from pointer without a cast
                 unsigned char b = foo();
                                   ^

        */
        warning("""
              const int * foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf23_3() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              const int * foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: warning: initialization makes integer from pointer without a cast
                 unsigned int b = foo();
                                  ^

        */
        warning("""
              const int * foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf23_4() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              const int * foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:32: warning: initialization makes integer from pointer without a cast
                 signed int b = foo();
                                ^

        */
        warning("""
              const int * foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf23_5() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              const int * foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:26: warning: initialization makes integer from pointer without a cast
                 long b = foo();
                          ^

        */
        warning("""
              const int * foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf23_6() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'float' from type 'const int *'
                 b = foo();
                   ^

        */
        error("""
              const int * foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'float' using type 'const int *'
                 float b = foo();
                           ^

        */
        error("""
              const int * foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf23_7() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double' from type 'const int *'
                 b = foo();
                   ^

        */
        error("""
              const int * foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'double' using type 'const int *'
                 double b = foo();
                            ^

        */
        error("""
              const int * foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf23_8() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'long double' from type 'const int *'
                 b = foo();
                   ^

        */
        error("""
              const int * foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: error: incompatible types when initializing type 'long double' using type 'const int *'
                 long double b = foo();
                                 ^

        */
        error("""
              const int * foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf23_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment discards 'const' qualifier from pointer target type
                 b = foo();
                   ^

        */
        warning("""
              const int * foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization discards 'const' qualifier from pointer target type
                 int * b = foo();
                           ^

        */
        warning("""
              const int * foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf23_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              const int * foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization from incompatible pointer type
                 int ** b = foo();
                            ^

        */
        warning("""
              const int * foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf23_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              const int * foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization from incompatible pointer type
                 char * b = foo();
                            ^

        */
        warning("""
              const int * foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf23_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              const int * foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:30: warning: initialization from incompatible pointer type
                 double * b = foo();
                              ^

        */
        warning("""
              const int * foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf23_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'const int *'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              const int * foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              const int * foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf23_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'const int *'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              const int * foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              const int * foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf23_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'const int *'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              const int * foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              const int * foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf23_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'const int *'
                 b = foo();
                   ^

        */
        error("""
              const int * foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              const int * foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf23_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'const int *'
                 b = foo();
                   ^

        */
        error("""
              const int * foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              const int * foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf23_18() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              const int * foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: warning: initialization makes integer from pointer without a cast
                 volatile int b = foo();
                                  ^

        */
        warning("""
              const int * foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf23_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              const int * foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:31: warning: initialization makes integer from pointer without a cast
                 const int b = foo();
                               ^

        */
        warning("""
              const int * foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf23_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              const int * foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: error: incompatible types when initializing type 'double' using type 'const int *'
                 const double b = foo();
                                  ^

        */
        error("""
              const int * foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf23_21() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double' from type 'const int *'
                 b = foo();
                   ^

        */
        error("""
              const int * foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:37: error: incompatible types when initializing type 'double' using type 'const int *'
                 volatile double b = foo();
                                     ^

        */
        error("""
              const int * foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf23_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment discards 'const' qualifier from pointer target type
                 b = foo();
                   ^

        */
        warning("""
              const int * foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization discards 'const' qualifier from pointer target type
                 int * b = foo();
                           ^

        */
        warning("""
              const int * foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf23_23() {
        correct("""
              const int * foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        correct("""
              const int * foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf23_24() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment discards 'const' qualifier from pointer target type
                 b = foo();
                   ^

        */
        warning("""
              const int * foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:36: warning: initialization discards 'const' qualifier from pointer target type
                 volatile int * b = foo();
                                    ^

        */
        warning("""
              const int * foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf24_0() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              volatile int * foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:26: warning: initialization makes integer from pointer without a cast
                 char b = foo();
                          ^

        */
        warning("""
              volatile int * foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf24_1() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              volatile int * foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: warning: initialization makes integer from pointer without a cast
                 signed char b = foo();
                                 ^

        */
        warning("""
              volatile int * foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf24_2() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              volatile int * foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:35: warning: initialization makes integer from pointer without a cast
                 unsigned char b = foo();
                                   ^

        */
        warning("""
              volatile int * foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf24_3() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              volatile int * foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: warning: initialization makes integer from pointer without a cast
                 unsigned int b = foo();
                                  ^

        */
        warning("""
              volatile int * foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf24_4() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              volatile int * foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:32: warning: initialization makes integer from pointer without a cast
                 signed int b = foo();
                                ^

        */
        warning("""
              volatile int * foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf24_5() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              volatile int * foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:26: warning: initialization makes integer from pointer without a cast
                 long b = foo();
                          ^

        */
        warning("""
              volatile int * foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf24_6() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'float' from type 'volatile int *'
                 b = foo();
                   ^

        */
        error("""
              volatile int * foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: error: incompatible types when initializing type 'float' using type 'volatile int *'
                 float b = foo();
                           ^

        */
        error("""
              volatile int * foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf24_7() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double' from type 'volatile int *'
                 b = foo();
                   ^

        */
        error("""
              volatile int * foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: error: incompatible types when initializing type 'double' using type 'volatile int *'
                 double b = foo();
                            ^

        */
        error("""
              volatile int * foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf24_8() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'long double' from type 'volatile int *'
                 b = foo();
                   ^

        */
        error("""
              volatile int * foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: error: incompatible types when initializing type 'long double' using type 'volatile int *'
                 long double b = foo();
                                 ^

        */
        error("""
              volatile int * foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf24_9() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment discards 'volatile' qualifier from pointer target type
                 b = foo();
                   ^

        */
        warning("""
              volatile int * foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization discards 'volatile' qualifier from pointer target type
                 int * b = foo();
                           ^

        */
        warning("""
              volatile int * foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf24_10() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              volatile int * foo();
              void main() {
                int ** b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization from incompatible pointer type
                 int ** b = foo();
                            ^

        */
        warning("""
              volatile int * foo();
              void main() {
                int ** b = foo();
              }
                """)
   }


   @Test def test_conf24_11() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              volatile int * foo();
              void main() {
                char * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:28: warning: initialization from incompatible pointer type
                 char * b = foo();
                            ^

        */
        warning("""
              volatile int * foo();
              void main() {
                char * b = foo();
              }
                """)
   }


   @Test def test_conf24_12() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              volatile int * foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:30: warning: initialization from incompatible pointer type
                 double * b = foo();
                              ^

        */
        warning("""
              volatile int * foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf24_13() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'volatile int *'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              volatile int * foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              volatile int * foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf24_14() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'volatile int *'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              volatile int * foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              volatile int * foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf24_15() {
        /* gcc reports:
test.c: In function 'main':
test.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'volatile int *'
                 b = foo();
                   ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              volatile int * foo();
              void main() {
                struct_anonymous b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:5:17: error: invalid initializer
                 struct_anonymous b = foo();
                 ^

        */
        error("""
              typedef struct { int x; } struct_anonymous;

              volatile int * foo();
              void main() {
                struct_anonymous b = foo();
              }
                """)
   }


   @Test def test_conf24_16() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'volatile int *'
                 b = foo();
                   ^

        */
        error("""
              volatile int * foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              volatile int * foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf24_17() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'volatile int *'
                 b = foo();
                   ^

        */
        error("""
              volatile int * foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              volatile int * foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf24_18() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              volatile int * foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: warning: initialization makes integer from pointer without a cast
                 volatile int b = foo();
                                  ^

        */
        warning("""
              volatile int * foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf24_19() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              volatile int * foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:31: warning: initialization makes integer from pointer without a cast
                 const int b = foo();
                               ^

        */
        warning("""
              volatile int * foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf24_20() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              volatile int * foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:34: error: incompatible types when initializing type 'double' using type 'volatile int *'
                 const double b = foo();
                                  ^

        */
        error("""
              volatile int * foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf24_21() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: error: incompatible types when assigning to type 'double' from type 'volatile int *'
                 b = foo();
                   ^

        */
        error("""
              volatile int * foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:37: error: incompatible types when initializing type 'double' using type 'volatile int *'
                 volatile double b = foo();
                                     ^

        */
        error("""
              volatile int * foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf24_22() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment discards 'volatile' qualifier from pointer target type
                 b = foo();
                   ^

        */
        warning("""
              volatile int * foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:27: warning: initialization discards 'volatile' qualifier from pointer target type
                 int * b = foo();
                           ^

        */
        warning("""
              volatile int * foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf24_23() {
        /* gcc reports:
test.c: In function 'main':
test.c:4:19: warning: assignment discards 'volatile' qualifier from pointer target type
                 b = foo();
                   ^

        */
        warning("""
              volatile int * foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
test.c: In function 'main':
test.c:3:33: warning: initialization discards 'volatile' qualifier from pointer target type
                 const int * b = foo();
                                 ^

        */
        warning("""
              volatile int * foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf24_24() {
        correct("""
              volatile int * foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        correct("""
              volatile int * foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }




}