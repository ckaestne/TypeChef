package de.fosd.typechef.typesystem.generated

import org.junit._
import de.fosd.typechef.typesystem._

/** generated tests! do not modify! */
class GeneratedReturnConstantTests extends TestHelperTS {

    @Test def test_conf0_0() {
        correct("""
              char x() { return 0; }
                """)
        correct("""
              char x() {
                char a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf0_1() {
        correct("""
              char x() { return 1; }
                """)
        correct("""
              char x() {
                char a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf0_2() {
        correct("""
              char x() { return -1; }
                """)
        correct("""
              char x() {
                char a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf0_3() {
        correct("""
              char x() { return 1l; }
                """)
        correct("""
              char x() {
                char a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf0_4() {
        correct("""
              char x() { return 0xa4; }
                """)
        correct("""
              char x() {
                char a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf0_5() {
        correct("""
              char x() { return 0.2; }
                """)
        correct("""
              char x() {
                char a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf0_6() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:26: warning: return makes integer from pointer without a cast
               char x() { return "0.2"; }
                          ^

        */
        warning("""
              char x() { return "0.2"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:26: warning: initialization makes integer from pointer without a cast
                 char a = "0.2";
                          ^

        */
        warning("""
              char x() {
                char a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf0_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:26: warning: return makes integer from pointer without a cast
               char x() { return &"foo"; }
                          ^

        */
        warning("""
              char x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:26: warning: initialization makes integer from pointer without a cast
                 char a = &"foo";
                          ^

        */
        warning("""
              char x() {
                char a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf0_8() {
        correct("""
              char x() { return *"foo"; }
                """)
        correct("""
              char x() {
                char a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf0_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:33: error: lvalue required as unary '&' operand
               char x() { return &1; }
                                 ^

        */
        error("""
              char x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:26: error: lvalue required as unary '&' operand
                 char a = &1;
                          ^

        */
        error("""
              char x() {
                char a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf1_0() {
        correct("""
              signed char x() { return 0; }
                """)
        correct("""
              signed char x() {
                signed char a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf1_1() {
        correct("""
              signed char x() { return 1; }
                """)
        correct("""
              signed char x() {
                signed char a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf1_2() {
        correct("""
              signed char x() { return -1; }
                """)
        correct("""
              signed char x() {
                signed char a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf1_3() {
        correct("""
              signed char x() { return 1l; }
                """)
        correct("""
              signed char x() {
                signed char a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf1_4() {
        correct("""
              signed char x() { return 0xa4; }
                """)
        correct("""
              signed char x() {
                signed char a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf1_5() {
        correct("""
              signed char x() { return 0.2; }
                """)
        correct("""
              signed char x() {
                signed char a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf1_6() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:33: warning: return makes integer from pointer without a cast
               signed char x() { return "0.2"; }
                                 ^

        */
        warning("""
              signed char x() { return "0.2"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:33: warning: initialization makes integer from pointer without a cast
                 signed char a = "0.2";
                                 ^

        */
        warning("""
              signed char x() {
                signed char a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf1_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:33: warning: return makes integer from pointer without a cast
               signed char x() { return &"foo"; }
                                 ^

        */
        warning("""
              signed char x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:33: warning: initialization makes integer from pointer without a cast
                 signed char a = &"foo";
                                 ^

        */
        warning("""
              signed char x() {
                signed char a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf1_8() {
        correct("""
              signed char x() { return *"foo"; }
                """)
        correct("""
              signed char x() {
                signed char a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf1_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:40: error: lvalue required as unary '&' operand
               signed char x() { return &1; }
                                        ^

        */
        error("""
              signed char x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:33: error: lvalue required as unary '&' operand
                 signed char a = &1;
                                 ^

        */
        error("""
              signed char x() {
                signed char a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf2_0() {
        correct("""
              unsigned char x() { return 0; }
                """)
        correct("""
              unsigned char x() {
                unsigned char a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf2_1() {
        correct("""
              unsigned char x() { return 1; }
                """)
        correct("""
              unsigned char x() {
                unsigned char a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf2_2() {
        correct("""
              unsigned char x() { return -1; }
                """)
        correct("""
              unsigned char x() {
                unsigned char a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf2_3() {
        correct("""
              unsigned char x() { return 1l; }
                """)
        correct("""
              unsigned char x() {
                unsigned char a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf2_4() {
        correct("""
              unsigned char x() { return 0xa4; }
                """)
        correct("""
              unsigned char x() {
                unsigned char a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf2_5() {
        correct("""
              unsigned char x() { return 0.2; }
                """)
        correct("""
              unsigned char x() {
                unsigned char a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf2_6() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:35: warning: return makes integer from pointer without a cast
               unsigned char x() { return "0.2"; }
                                   ^

        */
        warning("""
              unsigned char x() { return "0.2"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:35: warning: initialization makes integer from pointer without a cast
                 unsigned char a = "0.2";
                                   ^

        */
        warning("""
              unsigned char x() {
                unsigned char a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf2_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:35: warning: return makes integer from pointer without a cast
               unsigned char x() { return &"foo"; }
                                   ^

        */
        warning("""
              unsigned char x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:35: warning: initialization makes integer from pointer without a cast
                 unsigned char a = &"foo";
                                   ^

        */
        warning("""
              unsigned char x() {
                unsigned char a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf2_8() {
        correct("""
              unsigned char x() { return *"foo"; }
                """)
        correct("""
              unsigned char x() {
                unsigned char a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf2_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:42: error: lvalue required as unary '&' operand
               unsigned char x() { return &1; }
                                          ^

        */
        error("""
              unsigned char x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:35: error: lvalue required as unary '&' operand
                 unsigned char a = &1;
                                   ^

        */
        error("""
              unsigned char x() {
                unsigned char a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf3_0() {
        correct("""
              unsigned int x() { return 0; }
                """)
        correct("""
              unsigned int x() {
                unsigned int a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf3_1() {
        correct("""
              unsigned int x() { return 1; }
                """)
        correct("""
              unsigned int x() {
                unsigned int a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf3_2() {
        correct("""
              unsigned int x() { return -1; }
                """)
        correct("""
              unsigned int x() {
                unsigned int a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf3_3() {
        correct("""
              unsigned int x() { return 1l; }
                """)
        correct("""
              unsigned int x() {
                unsigned int a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf3_4() {
        correct("""
              unsigned int x() { return 0xa4; }
                """)
        correct("""
              unsigned int x() {
                unsigned int a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf3_5() {
        correct("""
              unsigned int x() { return 0.2; }
                """)
        correct("""
              unsigned int x() {
                unsigned int a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf3_6() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:34: warning: return makes integer from pointer without a cast
               unsigned int x() { return "0.2"; }
                                  ^

        */
        warning("""
              unsigned int x() { return "0.2"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:34: warning: initialization makes integer from pointer without a cast
                 unsigned int a = "0.2";
                                  ^

        */
        warning("""
              unsigned int x() {
                unsigned int a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf3_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:34: warning: return makes integer from pointer without a cast
               unsigned int x() { return &"foo"; }
                                  ^

        */
        warning("""
              unsigned int x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:34: warning: initialization makes integer from pointer without a cast
                 unsigned int a = &"foo";
                                  ^

        */
        warning("""
              unsigned int x() {
                unsigned int a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf3_8() {
        correct("""
              unsigned int x() { return *"foo"; }
                """)
        correct("""
              unsigned int x() {
                unsigned int a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf3_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:41: error: lvalue required as unary '&' operand
               unsigned int x() { return &1; }
                                         ^

        */
        error("""
              unsigned int x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:34: error: lvalue required as unary '&' operand
                 unsigned int a = &1;
                                  ^

        */
        error("""
              unsigned int x() {
                unsigned int a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf4_0() {
        correct("""
              signed int x() { return 0; }
                """)
        correct("""
              signed int x() {
                signed int a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf4_1() {
        correct("""
              signed int x() { return 1; }
                """)
        correct("""
              signed int x() {
                signed int a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf4_2() {
        correct("""
              signed int x() { return -1; }
                """)
        correct("""
              signed int x() {
                signed int a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf4_3() {
        correct("""
              signed int x() { return 1l; }
                """)
        correct("""
              signed int x() {
                signed int a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf4_4() {
        correct("""
              signed int x() { return 0xa4; }
                """)
        correct("""
              signed int x() {
                signed int a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf4_5() {
        correct("""
              signed int x() { return 0.2; }
                """)
        correct("""
              signed int x() {
                signed int a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf4_6() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:32: warning: return makes integer from pointer without a cast
               signed int x() { return "0.2"; }
                                ^

        */
        warning("""
              signed int x() { return "0.2"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:32: warning: initialization makes integer from pointer without a cast
                 signed int a = "0.2";
                                ^

        */
        warning("""
              signed int x() {
                signed int a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf4_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:32: warning: return makes integer from pointer without a cast
               signed int x() { return &"foo"; }
                                ^

        */
        warning("""
              signed int x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:32: warning: initialization makes integer from pointer without a cast
                 signed int a = &"foo";
                                ^

        */
        warning("""
              signed int x() {
                signed int a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf4_8() {
        correct("""
              signed int x() { return *"foo"; }
                """)
        correct("""
              signed int x() {
                signed int a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf4_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:39: error: lvalue required as unary '&' operand
               signed int x() { return &1; }
                                       ^

        */
        error("""
              signed int x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:32: error: lvalue required as unary '&' operand
                 signed int a = &1;
                                ^

        */
        error("""
              signed int x() {
                signed int a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf5_0() {
        correct("""
              long x() { return 0; }
                """)
        correct("""
              long x() {
                long a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf5_1() {
        correct("""
              long x() { return 1; }
                """)
        correct("""
              long x() {
                long a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf5_2() {
        correct("""
              long x() { return -1; }
                """)
        correct("""
              long x() {
                long a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf5_3() {
        correct("""
              long x() { return 1l; }
                """)
        correct("""
              long x() {
                long a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf5_4() {
        correct("""
              long x() { return 0xa4; }
                """)
        correct("""
              long x() {
                long a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf5_5() {
        correct("""
              long x() { return 0.2; }
                """)
        correct("""
              long x() {
                long a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf5_6() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:26: warning: return makes integer from pointer without a cast
               long x() { return "0.2"; }
                          ^

        */
        warning("""
              long x() { return "0.2"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:26: warning: initialization makes integer from pointer without a cast
                 long a = "0.2";
                          ^

        */
        warning("""
              long x() {
                long a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf5_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:26: warning: return makes integer from pointer without a cast
               long x() { return &"foo"; }
                          ^

        */
        warning("""
              long x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:26: warning: initialization makes integer from pointer without a cast
                 long a = &"foo";
                          ^

        */
        warning("""
              long x() {
                long a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf5_8() {
        correct("""
              long x() { return *"foo"; }
                """)
        correct("""
              long x() {
                long a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf5_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:33: error: lvalue required as unary '&' operand
               long x() { return &1; }
                                 ^

        */
        error("""
              long x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:26: error: lvalue required as unary '&' operand
                 long a = &1;
                          ^

        */
        error("""
              long x() {
                long a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf6_0() {
        correct("""
              double x() { return 0; }
                """)
        correct("""
              double x() {
                double a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf6_1() {
        correct("""
              double x() { return 1; }
                """)
        correct("""
              double x() {
                double a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf6_2() {
        correct("""
              double x() { return -1; }
                """)
        correct("""
              double x() {
                double a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf6_3() {
        correct("""
              double x() { return 1l; }
                """)
        correct("""
              double x() {
                double a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf6_4() {
        correct("""
              double x() { return 0xa4; }
                """)
        correct("""
              double x() {
                double a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf6_5() {
        correct("""
              double x() { return 0.2; }
                """)
        correct("""
              double x() {
                double a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf6_6() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:28: error: incompatible types when returning type 'char *' but 'double' was expected
               double x() { return "0.2"; }
                            ^

        */
        error("""
              double x() { return "0.2"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:28: error: incompatible types when initializing type 'double' using type 'char *'
                 double a = "0.2";
                            ^

        */
        error("""
              double x() {
                double a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf6_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:28: error: incompatible types when returning type 'char (*)[4]' but 'double' was expected
               double x() { return &"foo"; }
                            ^

        */
        error("""
              double x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:28: error: incompatible types when initializing type 'double' using type 'char (*)[4]'
                 double a = &"foo";
                            ^

        */
        error("""
              double x() {
                double a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf6_8() {
        correct("""
              double x() { return *"foo"; }
                """)
        correct("""
              double x() {
                double a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf6_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:35: error: lvalue required as unary '&' operand
               double x() { return &1; }
                                   ^

        */
        error("""
              double x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:28: error: lvalue required as unary '&' operand
                 double a = &1;
                            ^

        */
        error("""
              double x() {
                double a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf7_0() {
        correct("""
              int * x() { return 0; }
                """)
        correct("""
              int * x() {
                int * a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf7_1() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:27: warning: return makes pointer from integer without a cast
               int * x() { return 1; }
                           ^

        */
        warning("""
              int * x() { return 1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:27: warning: initialization makes pointer from integer without a cast
                 int * a = 1;
                           ^

        */
        warning("""
              int * x() {
                int * a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf7_2() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:27: warning: return makes pointer from integer without a cast
               int * x() { return -1; }
                           ^

        */
        warning("""
              int * x() { return -1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:27: warning: initialization makes pointer from integer without a cast
                 int * a = -1;
                           ^

        */
        warning("""
              int * x() {
                int * a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf7_3() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:27: warning: return makes pointer from integer without a cast
               int * x() { return 1l; }
                           ^

        */
        warning("""
              int * x() { return 1l; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:27: warning: initialization makes pointer from integer without a cast
                 int * a = 1l;
                           ^

        */
        warning("""
              int * x() {
                int * a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf7_4() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:27: warning: return makes pointer from integer without a cast
               int * x() { return 0xa4; }
                           ^

        */
        warning("""
              int * x() { return 0xa4; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:27: warning: initialization makes pointer from integer without a cast
                 int * a = 0xa4;
                           ^

        */
        warning("""
              int * x() {
                int * a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf7_5() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:27: error: incompatible types when returning type 'double' but 'int *' was expected
               int * x() { return 0.2; }
                           ^

        */
        error("""
              int * x() { return 0.2; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:27: error: incompatible types when initializing type 'int *' using type 'double'
                 int * a = 0.2;
                           ^

        */
        error("""
              int * x() {
                int * a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf7_6() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:27: warning: return from incompatible pointer type
               int * x() { return "0.2"; }
                           ^

        */
        warning("""
              int * x() { return "0.2"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:27: warning: initialization from incompatible pointer type
                 int * a = "0.2";
                           ^

        */
        warning("""
              int * x() {
                int * a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf7_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:27: warning: return from incompatible pointer type
               int * x() { return &"foo"; }
                           ^

        */
        warning("""
              int * x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:27: warning: initialization from incompatible pointer type
                 int * a = &"foo";
                           ^

        */
        warning("""
              int * x() {
                int * a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf7_8() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:27: warning: return makes pointer from integer without a cast
               int * x() { return *"foo"; }
                           ^

        */
        warning("""
              int * x() { return *"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:27: warning: initialization makes pointer from integer without a cast
                 int * a = *"foo";
                           ^

        */
        warning("""
              int * x() {
                int * a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf7_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:34: error: lvalue required as unary '&' operand
               int * x() { return &1; }
                                  ^

        */
        error("""
              int * x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:27: error: lvalue required as unary '&' operand
                 int * a = &1;
                           ^

        */
        error("""
              int * x() {
                int * a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf8_0() {
        correct("""
              char * x() { return 0; }
                """)
        correct("""
              char * x() {
                char * a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf8_1() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:28: warning: return makes pointer from integer without a cast
               char * x() { return 1; }
                            ^

        */
        warning("""
              char * x() { return 1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:28: warning: initialization makes pointer from integer without a cast
                 char * a = 1;
                            ^

        */
        warning("""
              char * x() {
                char * a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf8_2() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:28: warning: return makes pointer from integer without a cast
               char * x() { return -1; }
                            ^

        */
        warning("""
              char * x() { return -1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:28: warning: initialization makes pointer from integer without a cast
                 char * a = -1;
                            ^

        */
        warning("""
              char * x() {
                char * a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf8_3() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:28: warning: return makes pointer from integer without a cast
               char * x() { return 1l; }
                            ^

        */
        warning("""
              char * x() { return 1l; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:28: warning: initialization makes pointer from integer without a cast
                 char * a = 1l;
                            ^

        */
        warning("""
              char * x() {
                char * a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf8_4() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:28: warning: return makes pointer from integer without a cast
               char * x() { return 0xa4; }
                            ^

        */
        warning("""
              char * x() { return 0xa4; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:28: warning: initialization makes pointer from integer without a cast
                 char * a = 0xa4;
                            ^

        */
        warning("""
              char * x() {
                char * a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf8_5() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:28: error: incompatible types when returning type 'double' but 'char *' was expected
               char * x() { return 0.2; }
                            ^

        */
        error("""
              char * x() { return 0.2; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:28: error: incompatible types when initializing type 'char *' using type 'double'
                 char * a = 0.2;
                            ^

        */
        error("""
              char * x() {
                char * a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf8_6() {
        correct("""
              char * x() { return "0.2"; }
                """)
        correct("""
              char * x() {
                char * a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf8_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:28: warning: return from incompatible pointer type
               char * x() { return &"foo"; }
                            ^

        */
        warning("""
              char * x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:28: warning: initialization from incompatible pointer type
                 char * a = &"foo";
                            ^

        */
        warning("""
              char * x() {
                char * a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf8_8() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:28: warning: return makes pointer from integer without a cast
               char * x() { return *"foo"; }
                            ^

        */
        warning("""
              char * x() { return *"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:28: warning: initialization makes pointer from integer without a cast
                 char * a = *"foo";
                            ^

        */
        warning("""
              char * x() {
                char * a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf8_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:35: error: lvalue required as unary '&' operand
               char * x() { return &1; }
                                   ^

        */
        error("""
              char * x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:28: error: lvalue required as unary '&' operand
                 char * a = &1;
                            ^

        */
        error("""
              char * x() {
                char * a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf9_0() {
        correct("""
              signed char * x() { return 0; }
                """)
        correct("""
              signed char * x() {
                signed char * a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf9_1() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:35: warning: return makes pointer from integer without a cast
               signed char * x() { return 1; }
                                   ^

        */
        warning("""
              signed char * x() { return 1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:35: warning: initialization makes pointer from integer without a cast
                 signed char * a = 1;
                                   ^

        */
        warning("""
              signed char * x() {
                signed char * a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf9_2() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:35: warning: return makes pointer from integer without a cast
               signed char * x() { return -1; }
                                   ^

        */
        warning("""
              signed char * x() { return -1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:35: warning: initialization makes pointer from integer without a cast
                 signed char * a = -1;
                                   ^

        */
        warning("""
              signed char * x() {
                signed char * a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf9_3() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:35: warning: return makes pointer from integer without a cast
               signed char * x() { return 1l; }
                                   ^

        */
        warning("""
              signed char * x() { return 1l; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:35: warning: initialization makes pointer from integer without a cast
                 signed char * a = 1l;
                                   ^

        */
        warning("""
              signed char * x() {
                signed char * a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf9_4() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:35: warning: return makes pointer from integer without a cast
               signed char * x() { return 0xa4; }
                                   ^

        */
        warning("""
              signed char * x() { return 0xa4; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:35: warning: initialization makes pointer from integer without a cast
                 signed char * a = 0xa4;
                                   ^

        */
        warning("""
              signed char * x() {
                signed char * a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf9_5() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:35: error: incompatible types when returning type 'double' but 'signed char *' was expected
               signed char * x() { return 0.2; }
                                   ^

        */
        error("""
              signed char * x() { return 0.2; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:35: error: incompatible types when initializing type 'signed char *' using type 'double'
                 signed char * a = 0.2;
                                   ^

        */
        error("""
              signed char * x() {
                signed char * a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf9_6() {
        correct("""
              signed char * x() { return "0.2"; }
                """)
        correct("""
              signed char * x() {
                signed char * a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf9_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:35: warning: return from incompatible pointer type
               signed char * x() { return &"foo"; }
                                   ^

        */
        warning("""
              signed char * x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:35: warning: initialization from incompatible pointer type
                 signed char * a = &"foo";
                                   ^

        */
        warning("""
              signed char * x() {
                signed char * a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf9_8() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:35: warning: return makes pointer from integer without a cast
               signed char * x() { return *"foo"; }
                                   ^

        */
        warning("""
              signed char * x() { return *"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:35: warning: initialization makes pointer from integer without a cast
                 signed char * a = *"foo";
                                   ^

        */
        warning("""
              signed char * x() {
                signed char * a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf9_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:42: error: lvalue required as unary '&' operand
               signed char * x() { return &1; }
                                          ^

        */
        error("""
              signed char * x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:35: error: lvalue required as unary '&' operand
                 signed char * a = &1;
                                   ^

        */
        error("""
              signed char * x() {
                signed char * a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf10_0() {
        correct("""
              unsigned char * x() { return 0; }
                """)
        correct("""
              unsigned char * x() {
                unsigned char * a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf10_1() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:37: warning: return makes pointer from integer without a cast
               unsigned char * x() { return 1; }
                                     ^

        */
        warning("""
              unsigned char * x() { return 1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:37: warning: initialization makes pointer from integer without a cast
                 unsigned char * a = 1;
                                     ^

        */
        warning("""
              unsigned char * x() {
                unsigned char * a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf10_2() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:37: warning: return makes pointer from integer without a cast
               unsigned char * x() { return -1; }
                                     ^

        */
        warning("""
              unsigned char * x() { return -1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:37: warning: initialization makes pointer from integer without a cast
                 unsigned char * a = -1;
                                     ^

        */
        warning("""
              unsigned char * x() {
                unsigned char * a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf10_3() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:37: warning: return makes pointer from integer without a cast
               unsigned char * x() { return 1l; }
                                     ^

        */
        warning("""
              unsigned char * x() { return 1l; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:37: warning: initialization makes pointer from integer without a cast
                 unsigned char * a = 1l;
                                     ^

        */
        warning("""
              unsigned char * x() {
                unsigned char * a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf10_4() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:37: warning: return makes pointer from integer without a cast
               unsigned char * x() { return 0xa4; }
                                     ^

        */
        warning("""
              unsigned char * x() { return 0xa4; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:37: warning: initialization makes pointer from integer without a cast
                 unsigned char * a = 0xa4;
                                     ^

        */
        warning("""
              unsigned char * x() {
                unsigned char * a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf10_5() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:37: error: incompatible types when returning type 'double' but 'unsigned char *' was expected
               unsigned char * x() { return 0.2; }
                                     ^

        */
        error("""
              unsigned char * x() { return 0.2; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:37: error: incompatible types when initializing type 'unsigned char *' using type 'double'
                 unsigned char * a = 0.2;
                                     ^

        */
        error("""
              unsigned char * x() {
                unsigned char * a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf10_6() {
        correct("""
              unsigned char * x() { return "0.2"; }
                """)
        correct("""
              unsigned char * x() {
                unsigned char * a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf10_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:37: warning: return from incompatible pointer type
               unsigned char * x() { return &"foo"; }
                                     ^

        */
        warning("""
              unsigned char * x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:37: warning: initialization from incompatible pointer type
                 unsigned char * a = &"foo";
                                     ^

        */
        warning("""
              unsigned char * x() {
                unsigned char * a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf10_8() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:37: warning: return makes pointer from integer without a cast
               unsigned char * x() { return *"foo"; }
                                     ^

        */
        warning("""
              unsigned char * x() { return *"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:37: warning: initialization makes pointer from integer without a cast
                 unsigned char * a = *"foo";
                                     ^

        */
        warning("""
              unsigned char * x() {
                unsigned char * a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf10_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:44: error: lvalue required as unary '&' operand
               unsigned char * x() { return &1; }
                                            ^

        */
        error("""
              unsigned char * x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:37: error: lvalue required as unary '&' operand
                 unsigned char * a = &1;
                                     ^

        */
        error("""
              unsigned char * x() {
                unsigned char * a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf11_0() {
        correct("""
              char ** x() { return 0; }
                """)
        correct("""
              char ** x() {
                char ** a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf11_1() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:29: warning: return makes pointer from integer without a cast
               char ** x() { return 1; }
                             ^

        */
        warning("""
              char ** x() { return 1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:29: warning: initialization makes pointer from integer without a cast
                 char ** a = 1;
                             ^

        */
        warning("""
              char ** x() {
                char ** a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf11_2() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:29: warning: return makes pointer from integer without a cast
               char ** x() { return -1; }
                             ^

        */
        warning("""
              char ** x() { return -1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:29: warning: initialization makes pointer from integer without a cast
                 char ** a = -1;
                             ^

        */
        warning("""
              char ** x() {
                char ** a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf11_3() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:29: warning: return makes pointer from integer without a cast
               char ** x() { return 1l; }
                             ^

        */
        warning("""
              char ** x() { return 1l; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:29: warning: initialization makes pointer from integer without a cast
                 char ** a = 1l;
                             ^

        */
        warning("""
              char ** x() {
                char ** a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf11_4() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:29: warning: return makes pointer from integer without a cast
               char ** x() { return 0xa4; }
                             ^

        */
        warning("""
              char ** x() { return 0xa4; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:29: warning: initialization makes pointer from integer without a cast
                 char ** a = 0xa4;
                             ^

        */
        warning("""
              char ** x() {
                char ** a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf11_5() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:29: error: incompatible types when returning type 'double' but 'char **' was expected
               char ** x() { return 0.2; }
                             ^

        */
        error("""
              char ** x() { return 0.2; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:29: error: incompatible types when initializing type 'char **' using type 'double'
                 char ** a = 0.2;
                             ^

        */
        error("""
              char ** x() {
                char ** a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf11_6() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:29: warning: return from incompatible pointer type
               char ** x() { return "0.2"; }
                             ^

        */
        warning("""
              char ** x() { return "0.2"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:29: warning: initialization from incompatible pointer type
                 char ** a = "0.2";
                             ^

        */
        warning("""
              char ** x() {
                char ** a = "0.2";
                return a;
              }
                """)
   }


   @Ignore("handling of string literals (array with fixed length) is not precise enough")
   @Test def test_conf11_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:29: warning: return from incompatible pointer type
               char ** x() { return &"foo"; }
                             ^

        */
        warning("""
              char ** x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:29: warning: initialization from incompatible pointer type
                 char ** a = &"foo";
                             ^

        */
        warning("""
              char ** x() {
                char ** a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf11_8() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:29: warning: return makes pointer from integer without a cast
               char ** x() { return *"foo"; }
                             ^

        */
        warning("""
              char ** x() { return *"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:29: warning: initialization makes pointer from integer without a cast
                 char ** a = *"foo";
                             ^

        */
        warning("""
              char ** x() {
                char ** a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf11_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:36: error: lvalue required as unary '&' operand
               char ** x() { return &1; }
                                    ^

        */
        error("""
              char ** x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:29: error: lvalue required as unary '&' operand
                 char ** a = &1;
                             ^

        */
        error("""
              char ** x() {
                char ** a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf12_0() {
        correct("""
              unsigned char ** x() { return 0; }
                """)
        correct("""
              unsigned char ** x() {
                unsigned char ** a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf12_1() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:38: warning: return makes pointer from integer without a cast
               unsigned char ** x() { return 1; }
                                      ^

        */
        warning("""
              unsigned char ** x() { return 1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:38: warning: initialization makes pointer from integer without a cast
                 unsigned char ** a = 1;
                                      ^

        */
        warning("""
              unsigned char ** x() {
                unsigned char ** a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf12_2() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:38: warning: return makes pointer from integer without a cast
               unsigned char ** x() { return -1; }
                                      ^

        */
        warning("""
              unsigned char ** x() { return -1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:38: warning: initialization makes pointer from integer without a cast
                 unsigned char ** a = -1;
                                      ^

        */
        warning("""
              unsigned char ** x() {
                unsigned char ** a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf12_3() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:38: warning: return makes pointer from integer without a cast
               unsigned char ** x() { return 1l; }
                                      ^

        */
        warning("""
              unsigned char ** x() { return 1l; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:38: warning: initialization makes pointer from integer without a cast
                 unsigned char ** a = 1l;
                                      ^

        */
        warning("""
              unsigned char ** x() {
                unsigned char ** a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf12_4() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:38: warning: return makes pointer from integer without a cast
               unsigned char ** x() { return 0xa4; }
                                      ^

        */
        warning("""
              unsigned char ** x() { return 0xa4; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:38: warning: initialization makes pointer from integer without a cast
                 unsigned char ** a = 0xa4;
                                      ^

        */
        warning("""
              unsigned char ** x() {
                unsigned char ** a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf12_5() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:38: error: incompatible types when returning type 'double' but 'unsigned char **' was expected
               unsigned char ** x() { return 0.2; }
                                      ^

        */
        error("""
              unsigned char ** x() { return 0.2; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:38: error: incompatible types when initializing type 'unsigned char **' using type 'double'
                 unsigned char ** a = 0.2;
                                      ^

        */
        error("""
              unsigned char ** x() {
                unsigned char ** a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf12_6() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:38: warning: return from incompatible pointer type
               unsigned char ** x() { return "0.2"; }
                                      ^

        */
        warning("""
              unsigned char ** x() { return "0.2"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:38: warning: initialization from incompatible pointer type
                 unsigned char ** a = "0.2";
                                      ^

        */
        warning("""
              unsigned char ** x() {
                unsigned char ** a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf12_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:38: warning: return from incompatible pointer type
               unsigned char ** x() { return &"foo"; }
                                      ^

        */
        warning("""
              unsigned char ** x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:38: warning: initialization from incompatible pointer type
                 unsigned char ** a = &"foo";
                                      ^

        */
        warning("""
              unsigned char ** x() {
                unsigned char ** a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf12_8() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:38: warning: return makes pointer from integer without a cast
               unsigned char ** x() { return *"foo"; }
                                      ^

        */
        warning("""
              unsigned char ** x() { return *"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:38: warning: initialization makes pointer from integer without a cast
                 unsigned char ** a = *"foo";
                                      ^

        */
        warning("""
              unsigned char ** x() {
                unsigned char ** a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf12_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:45: error: lvalue required as unary '&' operand
               unsigned char ** x() { return &1; }
                                             ^

        */
        error("""
              unsigned char ** x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:38: error: lvalue required as unary '&' operand
                 unsigned char ** a = &1;
                                      ^

        */
        error("""
              unsigned char ** x() {
                unsigned char ** a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf13_0() {
        correct("""
              signed char ** x() { return 0; }
                """)
        correct("""
              signed char ** x() {
                signed char ** a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf13_1() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:36: warning: return makes pointer from integer without a cast
               signed char ** x() { return 1; }
                                    ^

        */
        warning("""
              signed char ** x() { return 1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:36: warning: initialization makes pointer from integer without a cast
                 signed char ** a = 1;
                                    ^

        */
        warning("""
              signed char ** x() {
                signed char ** a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf13_2() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:36: warning: return makes pointer from integer without a cast
               signed char ** x() { return -1; }
                                    ^

        */
        warning("""
              signed char ** x() { return -1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:36: warning: initialization makes pointer from integer without a cast
                 signed char ** a = -1;
                                    ^

        */
        warning("""
              signed char ** x() {
                signed char ** a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf13_3() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:36: warning: return makes pointer from integer without a cast
               signed char ** x() { return 1l; }
                                    ^

        */
        warning("""
              signed char ** x() { return 1l; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:36: warning: initialization makes pointer from integer without a cast
                 signed char ** a = 1l;
                                    ^

        */
        warning("""
              signed char ** x() {
                signed char ** a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf13_4() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:36: warning: return makes pointer from integer without a cast
               signed char ** x() { return 0xa4; }
                                    ^

        */
        warning("""
              signed char ** x() { return 0xa4; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:36: warning: initialization makes pointer from integer without a cast
                 signed char ** a = 0xa4;
                                    ^

        */
        warning("""
              signed char ** x() {
                signed char ** a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf13_5() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:36: error: incompatible types when returning type 'double' but 'signed char **' was expected
               signed char ** x() { return 0.2; }
                                    ^

        */
        error("""
              signed char ** x() { return 0.2; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:36: error: incompatible types when initializing type 'signed char **' using type 'double'
                 signed char ** a = 0.2;
                                    ^

        */
        error("""
              signed char ** x() {
                signed char ** a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf13_6() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:36: warning: return from incompatible pointer type
               signed char ** x() { return "0.2"; }
                                    ^

        */
        warning("""
              signed char ** x() { return "0.2"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:36: warning: initialization from incompatible pointer type
                 signed char ** a = "0.2";
                                    ^

        */
        warning("""
              signed char ** x() {
                signed char ** a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf13_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:36: warning: return from incompatible pointer type
               signed char ** x() { return &"foo"; }
                                    ^

        */
        warning("""
              signed char ** x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:36: warning: initialization from incompatible pointer type
                 signed char ** a = &"foo";
                                    ^

        */
        warning("""
              signed char ** x() {
                signed char ** a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf13_8() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:36: warning: return makes pointer from integer without a cast
               signed char ** x() { return *"foo"; }
                                    ^

        */
        warning("""
              signed char ** x() { return *"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:36: warning: initialization makes pointer from integer without a cast
                 signed char ** a = *"foo";
                                    ^

        */
        warning("""
              signed char ** x() {
                signed char ** a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf13_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:43: error: lvalue required as unary '&' operand
               signed char ** x() { return &1; }
                                           ^

        */
        error("""
              signed char ** x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:36: error: lvalue required as unary '&' operand
                 signed char ** a = &1;
                                    ^

        */
        error("""
              signed char ** x() {
                signed char ** a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf14_0() {
        correct("""
              double * x() { return 0; }
                """)
        correct("""
              double * x() {
                double * a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf14_1() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:30: warning: return makes pointer from integer without a cast
               double * x() { return 1; }
                              ^

        */
        warning("""
              double * x() { return 1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:30: warning: initialization makes pointer from integer without a cast
                 double * a = 1;
                              ^

        */
        warning("""
              double * x() {
                double * a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf14_2() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:30: warning: return makes pointer from integer without a cast
               double * x() { return -1; }
                              ^

        */
        warning("""
              double * x() { return -1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:30: warning: initialization makes pointer from integer without a cast
                 double * a = -1;
                              ^

        */
        warning("""
              double * x() {
                double * a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf14_3() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:30: warning: return makes pointer from integer without a cast
               double * x() { return 1l; }
                              ^

        */
        warning("""
              double * x() { return 1l; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:30: warning: initialization makes pointer from integer without a cast
                 double * a = 1l;
                              ^

        */
        warning("""
              double * x() {
                double * a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf14_4() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:30: warning: return makes pointer from integer without a cast
               double * x() { return 0xa4; }
                              ^

        */
        warning("""
              double * x() { return 0xa4; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:30: warning: initialization makes pointer from integer without a cast
                 double * a = 0xa4;
                              ^

        */
        warning("""
              double * x() {
                double * a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf14_5() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:30: error: incompatible types when returning type 'double' but 'double *' was expected
               double * x() { return 0.2; }
                              ^

        */
        error("""
              double * x() { return 0.2; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:30: error: incompatible types when initializing type 'double *' using type 'double'
                 double * a = 0.2;
                              ^

        */
        error("""
              double * x() {
                double * a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf14_6() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:30: warning: return from incompatible pointer type
               double * x() { return "0.2"; }
                              ^

        */
        warning("""
              double * x() { return "0.2"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:30: warning: initialization from incompatible pointer type
                 double * a = "0.2";
                              ^

        */
        warning("""
              double * x() {
                double * a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf14_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:30: warning: return from incompatible pointer type
               double * x() { return &"foo"; }
                              ^

        */
        warning("""
              double * x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:30: warning: initialization from incompatible pointer type
                 double * a = &"foo";
                              ^

        */
        warning("""
              double * x() {
                double * a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf14_8() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:30: warning: return makes pointer from integer without a cast
               double * x() { return *"foo"; }
                              ^

        */
        warning("""
              double * x() { return *"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:30: warning: initialization makes pointer from integer without a cast
                 double * a = *"foo";
                              ^

        */
        warning("""
              double * x() {
                double * a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf14_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:37: error: lvalue required as unary '&' operand
               double * x() { return &1; }
                                     ^

        */
        error("""
              double * x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:30: error: lvalue required as unary '&' operand
                 double * a = &1;
                              ^

        */
        error("""
              double * x() {
                double * a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf15_0() {
        /* gcc reports:
test.c: In function 'x':
test.c:3:30: error: incompatible types when returning type 'int' but 'struct S' was expected
               struct S x() { return 0; }
                              ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() { return 0; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:4:24: error: invalid initializer
                 struct S a = 0;
                        ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() {
                struct S a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf15_1() {
        /* gcc reports:
test.c: In function 'x':
test.c:3:30: error: incompatible types when returning type 'int' but 'struct S' was expected
               struct S x() { return 1; }
                              ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() { return 1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:4:24: error: invalid initializer
                 struct S a = 1;
                        ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() {
                struct S a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf15_2() {
        /* gcc reports:
test.c: In function 'x':
test.c:3:30: error: incompatible types when returning type 'int' but 'struct S' was expected
               struct S x() { return -1; }
                              ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() { return -1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:4:24: error: invalid initializer
                 struct S a = -1;
                        ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() {
                struct S a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf15_3() {
        /* gcc reports:
test.c: In function 'x':
test.c:3:30: error: incompatible types when returning type 'long int' but 'struct S' was expected
               struct S x() { return 1l; }
                              ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() { return 1l; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:4:24: error: invalid initializer
                 struct S a = 1l;
                        ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() {
                struct S a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf15_4() {
        /* gcc reports:
test.c: In function 'x':
test.c:3:30: error: incompatible types when returning type 'int' but 'struct S' was expected
               struct S x() { return 0xa4; }
                              ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() { return 0xa4; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:4:24: error: invalid initializer
                 struct S a = 0xa4;
                        ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() {
                struct S a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf15_5() {
        /* gcc reports:
test.c: In function 'x':
test.c:3:30: error: incompatible types when returning type 'double' but 'struct S' was expected
               struct S x() { return 0.2; }
                              ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() { return 0.2; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:4:24: error: invalid initializer
                 struct S a = 0.2;
                        ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() {
                struct S a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf15_6() {
        /* gcc reports:
test.c: In function 'x':
test.c:3:30: error: incompatible types when returning type 'char *' but 'struct S' was expected
               struct S x() { return "0.2"; }
                              ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() { return "0.2"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:4:24: error: invalid initializer
                 struct S a = "0.2";
                        ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() {
                struct S a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf15_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:3:30: error: incompatible types when returning type 'char (*)[4]' but 'struct S' was expected
               struct S x() { return &"foo"; }
                              ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:4:24: error: invalid initializer
                 struct S a = &"foo";
                        ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() {
                struct S a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf15_8() {
        /* gcc reports:
test.c: In function 'x':
test.c:3:30: error: incompatible types when returning type 'char' but 'struct S' was expected
               struct S x() { return *"foo"; }
                              ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() { return *"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:4:24: error: invalid initializer
                 struct S a = *"foo";
                        ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() {
                struct S a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf15_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:3:37: error: lvalue required as unary '&' operand
               struct S x() { return &1; }
                                     ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:4:30: error: lvalue required as unary '&' operand
                 struct S a = &1;
                              ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() {
                struct S a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf16_0() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:41: error: incompatible types when returning type 'int' but 'struct <anonymous>' was expected
               struct { float b; } x() { return 0; }
                                         ^

        */
        error("""
              struct { float b; } x() { return 0; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:24: error: invalid initializer
                 struct { float b; } a = 0;
                        ^
test.c:3:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^

        */
        error("""
              struct { float b; } x() {
                struct { float b; } a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf16_1() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:41: error: incompatible types when returning type 'int' but 'struct <anonymous>' was expected
               struct { float b; } x() { return 1; }
                                         ^

        */
        error("""
              struct { float b; } x() { return 1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:24: error: invalid initializer
                 struct { float b; } a = 1;
                        ^
test.c:3:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^

        */
        error("""
              struct { float b; } x() {
                struct { float b; } a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf16_2() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:41: error: incompatible types when returning type 'int' but 'struct <anonymous>' was expected
               struct { float b; } x() { return -1; }
                                         ^

        */
        error("""
              struct { float b; } x() { return -1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:24: error: invalid initializer
                 struct { float b; } a = -1;
                        ^
test.c:3:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^

        */
        error("""
              struct { float b; } x() {
                struct { float b; } a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf16_3() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:41: error: incompatible types when returning type 'long int' but 'struct <anonymous>' was expected
               struct { float b; } x() { return 1l; }
                                         ^

        */
        error("""
              struct { float b; } x() { return 1l; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:24: error: invalid initializer
                 struct { float b; } a = 1l;
                        ^
test.c:3:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^

        */
        error("""
              struct { float b; } x() {
                struct { float b; } a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf16_4() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:41: error: incompatible types when returning type 'int' but 'struct <anonymous>' was expected
               struct { float b; } x() { return 0xa4; }
                                         ^

        */
        error("""
              struct { float b; } x() { return 0xa4; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:24: error: invalid initializer
                 struct { float b; } a = 0xa4;
                        ^
test.c:3:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^

        */
        error("""
              struct { float b; } x() {
                struct { float b; } a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf16_5() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:41: error: incompatible types when returning type 'double' but 'struct <anonymous>' was expected
               struct { float b; } x() { return 0.2; }
                                         ^

        */
        error("""
              struct { float b; } x() { return 0.2; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:24: error: invalid initializer
                 struct { float b; } a = 0.2;
                        ^
test.c:3:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^

        */
        error("""
              struct { float b; } x() {
                struct { float b; } a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf16_6() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:41: error: incompatible types when returning type 'char *' but 'struct <anonymous>' was expected
               struct { float b; } x() { return "0.2"; }
                                         ^

        */
        error("""
              struct { float b; } x() { return "0.2"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:24: error: invalid initializer
                 struct { float b; } a = "0.2";
                        ^
test.c:3:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^

        */
        error("""
              struct { float b; } x() {
                struct { float b; } a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf16_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:41: error: incompatible types when returning type 'char (*)[4]' but 'struct <anonymous>' was expected
               struct { float b; } x() { return &"foo"; }
                                         ^

        */
        error("""
              struct { float b; } x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:24: error: invalid initializer
                 struct { float b; } a = &"foo";
                        ^
test.c:3:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^

        */
        error("""
              struct { float b; } x() {
                struct { float b; } a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf16_8() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:41: error: incompatible types when returning type 'char' but 'struct <anonymous>' was expected
               struct { float b; } x() { return *"foo"; }
                                         ^

        */
        error("""
              struct { float b; } x() { return *"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:24: error: invalid initializer
                 struct { float b; } a = *"foo";
                        ^
test.c:3:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^

        */
        error("""
              struct { float b; } x() {
                struct { float b; } a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf16_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:48: error: lvalue required as unary '&' operand
               struct { float b; } x() { return &1; }
                                                ^

        */
        error("""
              struct { float b; } x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:41: error: lvalue required as unary '&' operand
                 struct { float b; } a = &1;
                                         ^
test.c:3:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
                 return a;
                 ^

        */
        error("""
              struct { float b; } x() {
                struct { float b; } a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf17_0() {
        correct("""
              volatile int x() { return 0; }
                """)
        correct("""
              volatile int x() {
                volatile int a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf17_1() {
        correct("""
              volatile int x() { return 1; }
                """)
        correct("""
              volatile int x() {
                volatile int a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf17_2() {
        correct("""
              volatile int x() { return -1; }
                """)
        correct("""
              volatile int x() {
                volatile int a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf17_3() {
        correct("""
              volatile int x() { return 1l; }
                """)
        correct("""
              volatile int x() {
                volatile int a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf17_4() {
        correct("""
              volatile int x() { return 0xa4; }
                """)
        correct("""
              volatile int x() {
                volatile int a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf17_5() {
        correct("""
              volatile int x() { return 0.2; }
                """)
        correct("""
              volatile int x() {
                volatile int a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf17_6() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:34: warning: return makes integer from pointer without a cast
               volatile int x() { return "0.2"; }
                                  ^

        */
        warning("""
              volatile int x() { return "0.2"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:34: warning: initialization makes integer from pointer without a cast
                 volatile int a = "0.2";
                                  ^

        */
        warning("""
              volatile int x() {
                volatile int a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf17_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:34: warning: return makes integer from pointer without a cast
               volatile int x() { return &"foo"; }
                                  ^

        */
        warning("""
              volatile int x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:34: warning: initialization makes integer from pointer without a cast
                 volatile int a = &"foo";
                                  ^

        */
        warning("""
              volatile int x() {
                volatile int a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf17_8() {
        correct("""
              volatile int x() { return *"foo"; }
                """)
        correct("""
              volatile int x() {
                volatile int a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf17_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:41: error: lvalue required as unary '&' operand
               volatile int x() { return &1; }
                                         ^

        */
        error("""
              volatile int x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:34: error: lvalue required as unary '&' operand
                 volatile int a = &1;
                                  ^

        */
        error("""
              volatile int x() {
                volatile int a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf18_0() {
        correct("""
              const int x() { return 0; }
                """)
        correct("""
              const int x() {
                const int a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf18_1() {
        correct("""
              const int x() { return 1; }
                """)
        correct("""
              const int x() {
                const int a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf18_2() {
        correct("""
              const int x() { return -1; }
                """)
        correct("""
              const int x() {
                const int a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf18_3() {
        correct("""
              const int x() { return 1l; }
                """)
        correct("""
              const int x() {
                const int a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf18_4() {
        correct("""
              const int x() { return 0xa4; }
                """)
        correct("""
              const int x() {
                const int a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf18_5() {
        correct("""
              const int x() { return 0.2; }
                """)
        correct("""
              const int x() {
                const int a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf18_6() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:31: warning: return makes integer from pointer without a cast
               const int x() { return "0.2"; }
                               ^

        */
        warning("""
              const int x() { return "0.2"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:31: warning: initialization makes integer from pointer without a cast
                 const int a = "0.2";
                               ^

        */
        warning("""
              const int x() {
                const int a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf18_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:31: warning: return makes integer from pointer without a cast
               const int x() { return &"foo"; }
                               ^

        */
        warning("""
              const int x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:31: warning: initialization makes integer from pointer without a cast
                 const int a = &"foo";
                               ^

        */
        warning("""
              const int x() {
                const int a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf18_8() {
        correct("""
              const int x() { return *"foo"; }
                """)
        correct("""
              const int x() {
                const int a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf18_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:38: error: lvalue required as unary '&' operand
               const int x() { return &1; }
                                      ^

        */
        error("""
              const int x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:31: error: lvalue required as unary '&' operand
                 const int a = &1;
                               ^

        */
        error("""
              const int x() {
                const int a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf19_0() {
        correct("""
              const double x() { return 0; }
                """)
        correct("""
              const double x() {
                const double a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf19_1() {
        correct("""
              const double x() { return 1; }
                """)
        correct("""
              const double x() {
                const double a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf19_2() {
        correct("""
              const double x() { return -1; }
                """)
        correct("""
              const double x() {
                const double a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf19_3() {
        correct("""
              const double x() { return 1l; }
                """)
        correct("""
              const double x() {
                const double a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf19_4() {
        correct("""
              const double x() { return 0xa4; }
                """)
        correct("""
              const double x() {
                const double a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf19_5() {
        correct("""
              const double x() { return 0.2; }
                """)
        correct("""
              const double x() {
                const double a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf19_6() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:34: error: incompatible types when returning type 'char *' but 'double' was expected
               const double x() { return "0.2"; }
                                  ^

        */
        error("""
              const double x() { return "0.2"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:34: error: incompatible types when initializing type 'double' using type 'char *'
                 const double a = "0.2";
                                  ^

        */
        error("""
              const double x() {
                const double a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf19_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:34: error: incompatible types when returning type 'char (*)[4]' but 'double' was expected
               const double x() { return &"foo"; }
                                  ^

        */
        error("""
              const double x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:34: error: incompatible types when initializing type 'double' using type 'char (*)[4]'
                 const double a = &"foo";
                                  ^

        */
        error("""
              const double x() {
                const double a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf19_8() {
        correct("""
              const double x() { return *"foo"; }
                """)
        correct("""
              const double x() {
                const double a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf19_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:41: error: lvalue required as unary '&' operand
               const double x() { return &1; }
                                         ^

        */
        error("""
              const double x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:34: error: lvalue required as unary '&' operand
                 const double a = &1;
                                  ^

        */
        error("""
              const double x() {
                const double a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf20_0() {
        correct("""
              volatile double x() { return 0; }
                """)
        correct("""
              volatile double x() {
                volatile double a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf20_1() {
        correct("""
              volatile double x() { return 1; }
                """)
        correct("""
              volatile double x() {
                volatile double a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf20_2() {
        correct("""
              volatile double x() { return -1; }
                """)
        correct("""
              volatile double x() {
                volatile double a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf20_3() {
        correct("""
              volatile double x() { return 1l; }
                """)
        correct("""
              volatile double x() {
                volatile double a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf20_4() {
        correct("""
              volatile double x() { return 0xa4; }
                """)
        correct("""
              volatile double x() {
                volatile double a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf20_5() {
        correct("""
              volatile double x() { return 0.2; }
                """)
        correct("""
              volatile double x() {
                volatile double a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf20_6() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:37: error: incompatible types when returning type 'char *' but 'double' was expected
               volatile double x() { return "0.2"; }
                                     ^

        */
        error("""
              volatile double x() { return "0.2"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:37: error: incompatible types when initializing type 'double' using type 'char *'
                 volatile double a = "0.2";
                                     ^

        */
        error("""
              volatile double x() {
                volatile double a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf20_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:37: error: incompatible types when returning type 'char (*)[4]' but 'double' was expected
               volatile double x() { return &"foo"; }
                                     ^

        */
        error("""
              volatile double x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:37: error: incompatible types when initializing type 'double' using type 'char (*)[4]'
                 volatile double a = &"foo";
                                     ^

        */
        error("""
              volatile double x() {
                volatile double a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf20_8() {
        correct("""
              volatile double x() { return *"foo"; }
                """)
        correct("""
              volatile double x() {
                volatile double a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf20_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:44: error: lvalue required as unary '&' operand
               volatile double x() { return &1; }
                                            ^

        */
        error("""
              volatile double x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:37: error: lvalue required as unary '&' operand
                 volatile double a = &1;
                                     ^

        */
        error("""
              volatile double x() {
                volatile double a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf21_0() {
        correct("""
              int * x() { return 0; }
                """)
        correct("""
              int * x() {
                int * a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf21_1() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:27: warning: return makes pointer from integer without a cast
               int * x() { return 1; }
                           ^

        */
        warning("""
              int * x() { return 1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:27: warning: initialization makes pointer from integer without a cast
                 int * a = 1;
                           ^

        */
        warning("""
              int * x() {
                int * a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf21_2() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:27: warning: return makes pointer from integer without a cast
               int * x() { return -1; }
                           ^

        */
        warning("""
              int * x() { return -1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:27: warning: initialization makes pointer from integer without a cast
                 int * a = -1;
                           ^

        */
        warning("""
              int * x() {
                int * a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf21_3() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:27: warning: return makes pointer from integer without a cast
               int * x() { return 1l; }
                           ^

        */
        warning("""
              int * x() { return 1l; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:27: warning: initialization makes pointer from integer without a cast
                 int * a = 1l;
                           ^

        */
        warning("""
              int * x() {
                int * a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf21_4() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:27: warning: return makes pointer from integer without a cast
               int * x() { return 0xa4; }
                           ^

        */
        warning("""
              int * x() { return 0xa4; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:27: warning: initialization makes pointer from integer without a cast
                 int * a = 0xa4;
                           ^

        */
        warning("""
              int * x() {
                int * a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf21_5() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:27: error: incompatible types when returning type 'double' but 'int *' was expected
               int * x() { return 0.2; }
                           ^

        */
        error("""
              int * x() { return 0.2; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:27: error: incompatible types when initializing type 'int *' using type 'double'
                 int * a = 0.2;
                           ^

        */
        error("""
              int * x() {
                int * a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf21_6() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:27: warning: return from incompatible pointer type
               int * x() { return "0.2"; }
                           ^

        */
        warning("""
              int * x() { return "0.2"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:27: warning: initialization from incompatible pointer type
                 int * a = "0.2";
                           ^

        */
        warning("""
              int * x() {
                int * a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf21_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:27: warning: return from incompatible pointer type
               int * x() { return &"foo"; }
                           ^

        */
        warning("""
              int * x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:27: warning: initialization from incompatible pointer type
                 int * a = &"foo";
                           ^

        */
        warning("""
              int * x() {
                int * a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf21_8() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:27: warning: return makes pointer from integer without a cast
               int * x() { return *"foo"; }
                           ^

        */
        warning("""
              int * x() { return *"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:27: warning: initialization makes pointer from integer without a cast
                 int * a = *"foo";
                           ^

        */
        warning("""
              int * x() {
                int * a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf21_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:34: error: lvalue required as unary '&' operand
               int * x() { return &1; }
                                  ^

        */
        error("""
              int * x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:27: error: lvalue required as unary '&' operand
                 int * a = &1;
                           ^

        */
        error("""
              int * x() {
                int * a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf22_0() {
        correct("""
              const int * x() { return 0; }
                """)
        correct("""
              const int * x() {
                const int * a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf22_1() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:33: warning: return makes pointer from integer without a cast
               const int * x() { return 1; }
                                 ^

        */
        warning("""
              const int * x() { return 1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:33: warning: initialization makes pointer from integer without a cast
                 const int * a = 1;
                                 ^

        */
        warning("""
              const int * x() {
                const int * a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf22_2() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:33: warning: return makes pointer from integer without a cast
               const int * x() { return -1; }
                                 ^

        */
        warning("""
              const int * x() { return -1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:33: warning: initialization makes pointer from integer without a cast
                 const int * a = -1;
                                 ^

        */
        warning("""
              const int * x() {
                const int * a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf22_3() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:33: warning: return makes pointer from integer without a cast
               const int * x() { return 1l; }
                                 ^

        */
        warning("""
              const int * x() { return 1l; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:33: warning: initialization makes pointer from integer without a cast
                 const int * a = 1l;
                                 ^

        */
        warning("""
              const int * x() {
                const int * a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf22_4() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:33: warning: return makes pointer from integer without a cast
               const int * x() { return 0xa4; }
                                 ^

        */
        warning("""
              const int * x() { return 0xa4; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:33: warning: initialization makes pointer from integer without a cast
                 const int * a = 0xa4;
                                 ^

        */
        warning("""
              const int * x() {
                const int * a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf22_5() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:33: error: incompatible types when returning type 'double' but 'const int *' was expected
               const int * x() { return 0.2; }
                                 ^

        */
        error("""
              const int * x() { return 0.2; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:33: error: incompatible types when initializing type 'const int *' using type 'double'
                 const int * a = 0.2;
                                 ^

        */
        error("""
              const int * x() {
                const int * a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf22_6() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:33: warning: return from incompatible pointer type
               const int * x() { return "0.2"; }
                                 ^

        */
        warning("""
              const int * x() { return "0.2"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:33: warning: initialization from incompatible pointer type
                 const int * a = "0.2";
                                 ^

        */
        warning("""
              const int * x() {
                const int * a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf22_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:33: warning: return from incompatible pointer type
               const int * x() { return &"foo"; }
                                 ^

        */
        warning("""
              const int * x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:33: warning: initialization from incompatible pointer type
                 const int * a = &"foo";
                                 ^

        */
        warning("""
              const int * x() {
                const int * a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf22_8() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:33: warning: return makes pointer from integer without a cast
               const int * x() { return *"foo"; }
                                 ^

        */
        warning("""
              const int * x() { return *"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:33: warning: initialization makes pointer from integer without a cast
                 const int * a = *"foo";
                                 ^

        */
        warning("""
              const int * x() {
                const int * a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf22_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:40: error: lvalue required as unary '&' operand
               const int * x() { return &1; }
                                        ^

        */
        error("""
              const int * x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:33: error: lvalue required as unary '&' operand
                 const int * a = &1;
                                 ^

        */
        error("""
              const int * x() {
                const int * a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf23_0() {
        correct("""
              volatile int * x() { return 0; }
                """)
        correct("""
              volatile int * x() {
                volatile int * a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf23_1() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:36: warning: return makes pointer from integer without a cast
               volatile int * x() { return 1; }
                                    ^

        */
        warning("""
              volatile int * x() { return 1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:36: warning: initialization makes pointer from integer without a cast
                 volatile int * a = 1;
                                    ^

        */
        warning("""
              volatile int * x() {
                volatile int * a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf23_2() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:36: warning: return makes pointer from integer without a cast
               volatile int * x() { return -1; }
                                    ^

        */
        warning("""
              volatile int * x() { return -1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:36: warning: initialization makes pointer from integer without a cast
                 volatile int * a = -1;
                                    ^

        */
        warning("""
              volatile int * x() {
                volatile int * a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf23_3() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:36: warning: return makes pointer from integer without a cast
               volatile int * x() { return 1l; }
                                    ^

        */
        warning("""
              volatile int * x() { return 1l; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:36: warning: initialization makes pointer from integer without a cast
                 volatile int * a = 1l;
                                    ^

        */
        warning("""
              volatile int * x() {
                volatile int * a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf23_4() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:36: warning: return makes pointer from integer without a cast
               volatile int * x() { return 0xa4; }
                                    ^

        */
        warning("""
              volatile int * x() { return 0xa4; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:36: warning: initialization makes pointer from integer without a cast
                 volatile int * a = 0xa4;
                                    ^

        */
        warning("""
              volatile int * x() {
                volatile int * a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf23_5() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:36: error: incompatible types when returning type 'double' but 'volatile int *' was expected
               volatile int * x() { return 0.2; }
                                    ^

        */
        error("""
              volatile int * x() { return 0.2; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:36: error: incompatible types when initializing type 'volatile int *' using type 'double'
                 volatile int * a = 0.2;
                                    ^

        */
        error("""
              volatile int * x() {
                volatile int * a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf23_6() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:36: warning: return from incompatible pointer type
               volatile int * x() { return "0.2"; }
                                    ^

        */
        warning("""
              volatile int * x() { return "0.2"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:36: warning: initialization from incompatible pointer type
                 volatile int * a = "0.2";
                                    ^

        */
        warning("""
              volatile int * x() {
                volatile int * a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf23_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:36: warning: return from incompatible pointer type
               volatile int * x() { return &"foo"; }
                                    ^

        */
        warning("""
              volatile int * x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:36: warning: initialization from incompatible pointer type
                 volatile int * a = &"foo";
                                    ^

        */
        warning("""
              volatile int * x() {
                volatile int * a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf23_8() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:36: warning: return makes pointer from integer without a cast
               volatile int * x() { return *"foo"; }
                                    ^

        */
        warning("""
              volatile int * x() { return *"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:36: warning: initialization makes pointer from integer without a cast
                 volatile int * a = *"foo";
                                    ^

        */
        warning("""
              volatile int * x() {
                volatile int * a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf23_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:43: error: lvalue required as unary '&' operand
               volatile int * x() { return &1; }
                                           ^

        */
        error("""
              volatile int * x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:36: error: lvalue required as unary '&' operand
                 volatile int * a = &1;
                                    ^

        */
        error("""
              volatile int * x() {
                volatile int * a = &1;
                return a;
              }
                """)
   }


   @Test def test_conf24_0() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:26: warning: 'return' with a value, in function returning void
               void x() { return 0; }
                          ^

        */
        warning("""
              void x() { return 0; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:22: error: variable or field 'a' declared void
                 void a = 0;
                      ^
test.c:3:17: warning: 'return' with a value, in function returning void
                 return a;
                 ^

        */
        error("""
              void x() {
                void a = 0;
                return a;
              }
                """)
   }


   @Test def test_conf24_1() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:26: warning: 'return' with a value, in function returning void
               void x() { return 1; }
                          ^

        */
        warning("""
              void x() { return 1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:22: error: variable or field 'a' declared void
                 void a = 1;
                      ^
test.c:3:17: warning: 'return' with a value, in function returning void
                 return a;
                 ^

        */
        error("""
              void x() {
                void a = 1;
                return a;
              }
                """)
   }


   @Test def test_conf24_2() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:26: warning: 'return' with a value, in function returning void
               void x() { return -1; }
                          ^

        */
        warning("""
              void x() { return -1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:22: error: variable or field 'a' declared void
                 void a = -1;
                      ^
test.c:3:17: warning: 'return' with a value, in function returning void
                 return a;
                 ^

        */
        error("""
              void x() {
                void a = -1;
                return a;
              }
                """)
   }


   @Test def test_conf24_3() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:26: warning: 'return' with a value, in function returning void
               void x() { return 1l; }
                          ^

        */
        warning("""
              void x() { return 1l; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:22: error: variable or field 'a' declared void
                 void a = 1l;
                      ^
test.c:3:17: warning: 'return' with a value, in function returning void
                 return a;
                 ^

        */
        error("""
              void x() {
                void a = 1l;
                return a;
              }
                """)
   }


   @Test def test_conf24_4() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:26: warning: 'return' with a value, in function returning void
               void x() { return 0xa4; }
                          ^

        */
        warning("""
              void x() { return 0xa4; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:22: error: variable or field 'a' declared void
                 void a = 0xa4;
                      ^
test.c:3:17: warning: 'return' with a value, in function returning void
                 return a;
                 ^

        */
        error("""
              void x() {
                void a = 0xa4;
                return a;
              }
                """)
   }


   @Test def test_conf24_5() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:26: warning: 'return' with a value, in function returning void
               void x() { return 0.2; }
                          ^

        */
        warning("""
              void x() { return 0.2; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:22: error: variable or field 'a' declared void
                 void a = 0.2;
                      ^
test.c:3:17: warning: 'return' with a value, in function returning void
                 return a;
                 ^

        */
        error("""
              void x() {
                void a = 0.2;
                return a;
              }
                """)
   }


   @Test def test_conf24_6() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:26: warning: 'return' with a value, in function returning void
               void x() { return "0.2"; }
                          ^

        */
        warning("""
              void x() { return "0.2"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:22: error: variable or field 'a' declared void
                 void a = "0.2";
                      ^
test.c:2:26: warning: initialization makes integer from pointer without a cast
                 void a = "0.2";
                          ^
test.c:3:17: warning: 'return' with a value, in function returning void
                 return a;
                 ^

        */
        error("""
              void x() {
                void a = "0.2";
                return a;
              }
                """)
   }


   @Test def test_conf24_7() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:26: warning: 'return' with a value, in function returning void
               void x() { return &"foo"; }
                          ^

        */
        warning("""
              void x() { return &"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:22: error: variable or field 'a' declared void
                 void a = &"foo";
                      ^
test.c:2:26: warning: initialization makes integer from pointer without a cast
                 void a = &"foo";
                          ^
test.c:3:17: warning: 'return' with a value, in function returning void
                 return a;
                 ^

        */
        error("""
              void x() {
                void a = &"foo";
                return a;
              }
                """)
   }


   @Test def test_conf24_8() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:26: warning: 'return' with a value, in function returning void
               void x() { return *"foo"; }
                          ^

        */
        warning("""
              void x() { return *"foo"; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:22: error: variable or field 'a' declared void
                 void a = *"foo";
                      ^
test.c:3:17: warning: 'return' with a value, in function returning void
                 return a;
                 ^

        */
        error("""
              void x() {
                void a = *"foo";
                return a;
              }
                """)
   }


   @Test def test_conf24_9() {
        /* gcc reports:
test.c: In function 'x':
test.c:1:33: error: lvalue required as unary '&' operand
               void x() { return &1; }
                                 ^
test.c:1:26: warning: 'return' with a value, in function returning void
               void x() { return &1; }
                          ^

        */
        error("""
              void x() { return &1; }
                """)
        /* gcc reports:
test.c: In function 'x':
test.c:2:22: error: variable or field 'a' declared void
                 void a = &1;
                      ^
test.c:2:26: error: lvalue required as unary '&' operand
                 void a = &1;
                          ^
test.c:3:17: warning: 'return' with a value, in function returning void
                 return a;
                 ^

        */
        error("""
              void x() {
                void a = &1;
                return a;
              }
                """)
   }




}