package de.fosd.typechef.typesystem.generated

import org.junit._
import de.fosd.typechef.typesystem._

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
C:\Users\ckaestne\AppData\Local\Temp\conf0_66703675801542884251.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf0_66703675801542884251.c:1:26: warning: return makes integer from pointer without a cast
               char x() { return "0.2"; }
                          ^

        */
        warning("""
              char x() { return "0.2"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_64070738727317147062.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf0_64070738727317147062.c:2:26: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_73621175387460647889.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf0_73621175387460647889.c:1:26: warning: return makes integer from pointer without a cast
               char x() { return &"foo"; }
                          ^

        */
        warning("""
              char x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_76231476577553817774.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf0_76231476577553817774.c:2:26: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_9103753121639099270.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf0_9103753121639099270.c:1:33: error: lvalue required as unary '&' operand
               char x() { return &1; }
                                 ^

        */
        error("""
              char x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_93315869727412646246.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf0_93315869727412646246.c:2:26: error: lvalue required as unary '&' operand
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_62520137801050117636.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf1_62520137801050117636.c:1:33: warning: return makes integer from pointer without a cast
               signed char x() { return "0.2"; }
                                 ^

        */
        warning("""
              signed char x() { return "0.2"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_63637406733244261893.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf1_63637406733244261893.c:2:33: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_77046001528577113128.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf1_77046001528577113128.c:1:33: warning: return makes integer from pointer without a cast
               signed char x() { return &"foo"; }
                                 ^

        */
        warning("""
              signed char x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_78983535899461181323.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf1_78983535899461181323.c:2:33: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_945039924554195544.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf1_945039924554195544.c:1:40: error: lvalue required as unary '&' operand
               signed char x() { return &1; }
                                        ^

        */
        error("""
              signed char x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_94355657196413239632.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf1_94355657196413239632.c:2:33: error: lvalue required as unary '&' operand
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_63095428602487638786.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf2_63095428602487638786.c:1:35: warning: return makes integer from pointer without a cast
               unsigned char x() { return "0.2"; }
                                   ^

        */
        warning("""
              unsigned char x() { return "0.2"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_65541799607906272073.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf2_65541799607906272073.c:2:35: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_78793304713860252895.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf2_78793304713860252895.c:1:35: warning: return makes integer from pointer without a cast
               unsigned char x() { return &"foo"; }
                                   ^

        */
        warning("""
              unsigned char x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_75934034452619607057.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf2_75934034452619607057.c:2:35: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_98105778992667024686.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf2_98105778992667024686.c:1:42: error: lvalue required as unary '&' operand
               unsigned char x() { return &1; }
                                          ^

        */
        error("""
              unsigned char x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_91909605337224818720.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf2_91909605337224818720.c:2:35: error: lvalue required as unary '&' operand
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_62251777559722924075.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf3_62251777559722924075.c:1:34: warning: return makes integer from pointer without a cast
               unsigned int x() { return "0.2"; }
                                  ^

        */
        warning("""
              unsigned int x() { return "0.2"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_68186937319856923844.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf3_68186937319856923844.c:2:34: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_77276752321862486912.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf3_77276752321862486912.c:1:34: warning: return makes integer from pointer without a cast
               unsigned int x() { return &"foo"; }
                                  ^

        */
        warning("""
              unsigned int x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_76503336385315414568.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf3_76503336385315414568.c:2:34: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_9577435230721665671.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf3_9577435230721665671.c:1:41: error: lvalue required as unary '&' operand
               unsigned int x() { return &1; }
                                         ^

        */
        error("""
              unsigned int x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_94041693060032223367.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf3_94041693060032223367.c:2:34: error: lvalue required as unary '&' operand
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_65844045582011972996.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf4_65844045582011972996.c:1:32: warning: return makes integer from pointer without a cast
               signed int x() { return "0.2"; }
                                ^

        */
        warning("""
              signed int x() { return "0.2"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_67514125951731326068.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf4_67514125951731326068.c:2:32: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_72687415755383611469.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf4_72687415755383611469.c:1:32: warning: return makes integer from pointer without a cast
               signed int x() { return &"foo"; }
                                ^

        */
        warning("""
              signed int x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_73553029576475353156.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf4_73553029576475353156.c:2:32: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_91144083077091265121.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf4_91144083077091265121.c:1:39: error: lvalue required as unary '&' operand
               signed int x() { return &1; }
                                       ^

        */
        error("""
              signed int x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_97049701597124498013.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf4_97049701597124498013.c:2:32: error: lvalue required as unary '&' operand
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_61259144156501952872.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf5_61259144156501952872.c:1:26: warning: return makes integer from pointer without a cast
               long x() { return "0.2"; }
                          ^

        */
        warning("""
              long x() { return "0.2"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_61037733642003055419.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf5_61037733642003055419.c:2:26: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_77431321725050764069.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf5_77431321725050764069.c:1:26: warning: return makes integer from pointer without a cast
               long x() { return &"foo"; }
                          ^

        */
        warning("""
              long x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_74154370728157947974.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf5_74154370728157947974.c:2:26: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_9876827519232013002.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf5_9876827519232013002.c:1:33: error: lvalue required as unary '&' operand
               long x() { return &1; }
                                 ^

        */
        error("""
              long x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_97817715559343707734.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf5_97817715559343707734.c:2:26: error: lvalue required as unary '&' operand
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_6667755003409972527.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf6_6667755003409972527.c:1:28: error: incompatible types when returning type 'char *' but 'double' was expected
               double x() { return "0.2"; }
                            ^

        */
        error("""
              double x() { return "0.2"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_69183308094913589678.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf6_69183308094913589678.c:2:28: error: incompatible types when initializing type 'double' using type 'char *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_77257310629925080340.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf6_77257310629925080340.c:1:28: error: incompatible types when returning type 'char (*)[4]' but 'double' was expected
               double x() { return &"foo"; }
                            ^

        */
        error("""
              double x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_749085977694374350.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf6_749085977694374350.c:2:28: error: incompatible types when initializing type 'double' using type 'char (*)[4]'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_96524414665069664699.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf6_96524414665069664699.c:1:35: error: lvalue required as unary '&' operand
               double x() { return &1; }
                                   ^

        */
        error("""
              double x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_9120725589563652092.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf6_9120725589563652092.c:2:28: error: lvalue required as unary '&' operand
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_16903901591533486904.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf7_16903901591533486904.c:1:27: warning: return makes pointer from integer without a cast
               int * x() { return 1; }
                           ^

        */
        warning("""
              int * x() { return 1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_19141572848010119984.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf7_19141572848010119984.c:2:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_25285133840022003258.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf7_25285133840022003258.c:1:27: warning: return makes pointer from integer without a cast
               int * x() { return -1; }
                           ^

        */
        warning("""
              int * x() { return -1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_21707732777099698568.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf7_21707732777099698568.c:2:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_33072333891993522075.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf7_33072333891993522075.c:1:27: warning: return makes pointer from integer without a cast
               int * x() { return 1l; }
                           ^

        */
        warning("""
              int * x() { return 1l; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_35225033642837262430.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf7_35225033642837262430.c:2:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_45424248070131312250.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf7_45424248070131312250.c:1:27: warning: return makes pointer from integer without a cast
               int * x() { return 0xa4; }
                           ^

        */
        warning("""
              int * x() { return 0xa4; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_47336451216988800202.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf7_47336451216988800202.c:2:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_55042805567059474072.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf7_55042805567059474072.c:1:27: error: incompatible types when returning type 'double' but 'int *' was expected
               int * x() { return 0.2; }
                           ^

        */
        error("""
              int * x() { return 0.2; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_53254314169601816514.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf7_53254314169601816514.c:2:27: error: incompatible types when initializing type 'int *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_68971974225254061765.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf7_68971974225254061765.c:1:27: warning: return from incompatible pointer type
               int * x() { return "0.2"; }
                           ^

        */
        warning("""
              int * x() { return "0.2"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_66002090532800171696.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf7_66002090532800171696.c:2:27: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_78403011021938090108.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf7_78403011021938090108.c:1:27: warning: return from incompatible pointer type
               int * x() { return &"foo"; }
                           ^

        */
        warning("""
              int * x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_73958543500368835927.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf7_73958543500368835927.c:2:27: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_83473717775264668901.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf7_83473717775264668901.c:1:27: warning: return makes pointer from integer without a cast
               int * x() { return *"foo"; }
                           ^

        */
        warning("""
              int * x() { return *"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_81275237916161946295.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf7_81275237916161946295.c:2:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_97288329800297613682.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf7_97288329800297613682.c:1:34: error: lvalue required as unary '&' operand
               int * x() { return &1; }
                                  ^

        */
        error("""
              int * x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_98110035559861090901.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf7_98110035559861090901.c:2:27: error: lvalue required as unary '&' operand
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_1889527867063180325.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf8_1889527867063180325.c:1:28: warning: return makes pointer from integer without a cast
               char * x() { return 1; }
                            ^

        */
        warning("""
              char * x() { return 1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_12453232679443997068.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf8_12453232679443997068.c:2:28: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_28654453460188138423.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf8_28654453460188138423.c:1:28: warning: return makes pointer from integer without a cast
               char * x() { return -1; }
                            ^

        */
        warning("""
              char * x() { return -1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_26300262931169415635.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf8_26300262931169415635.c:2:28: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_32849147543315949677.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf8_32849147543315949677.c:1:28: warning: return makes pointer from integer without a cast
               char * x() { return 1l; }
                            ^

        */
        warning("""
              char * x() { return 1l; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_36210695813176552720.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf8_36210695813176552720.c:2:28: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_48017611369122664357.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf8_48017611369122664357.c:1:28: warning: return makes pointer from integer without a cast
               char * x() { return 0xa4; }
                            ^

        */
        warning("""
              char * x() { return 0xa4; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_41963695151307187912.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf8_41963695151307187912.c:2:28: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_54392336685412657546.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf8_54392336685412657546.c:1:28: error: incompatible types when returning type 'double' but 'char *' was expected
               char * x() { return 0.2; }
                            ^

        */
        error("""
              char * x() { return 0.2; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_54171209207099273117.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf8_54171209207099273117.c:2:28: error: incompatible types when initializing type 'char *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_73478423617920980678.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf8_73478423617920980678.c:1:28: warning: return from incompatible pointer type
               char * x() { return &"foo"; }
                            ^

        */
        warning("""
              char * x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_72322687136783386360.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf8_72322687136783386360.c:2:28: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_83430260455353033769.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf8_83430260455353033769.c:1:28: warning: return makes pointer from integer without a cast
               char * x() { return *"foo"; }
                            ^

        */
        warning("""
              char * x() { return *"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_8513589792640115731.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf8_8513589792640115731.c:2:28: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_94713231873567342379.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf8_94713231873567342379.c:1:35: error: lvalue required as unary '&' operand
               char * x() { return &1; }
                                   ^

        */
        error("""
              char * x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_99105515570819604724.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf8_99105515570819604724.c:2:28: error: lvalue required as unary '&' operand
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_14119266029300767467.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf9_14119266029300767467.c:1:35: warning: return makes pointer from integer without a cast
               signed char * x() { return 1; }
                                   ^

        */
        warning("""
              signed char * x() { return 1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_17464194051928153443.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf9_17464194051928153443.c:2:35: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_26877222301926145602.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf9_26877222301926145602.c:1:35: warning: return makes pointer from integer without a cast
               signed char * x() { return -1; }
                                   ^

        */
        warning("""
              signed char * x() { return -1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_25674006397658630484.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf9_25674006397658630484.c:2:35: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_31952158426189783433.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf9_31952158426189783433.c:1:35: warning: return makes pointer from integer without a cast
               signed char * x() { return 1l; }
                                   ^

        */
        warning("""
              signed char * x() { return 1l; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_34483249018118127691.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf9_34483249018118127691.c:2:35: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_41902273804138938032.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf9_41902273804138938032.c:1:35: warning: return makes pointer from integer without a cast
               signed char * x() { return 0xa4; }
                                   ^

        */
        warning("""
              signed char * x() { return 0xa4; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_42519137024446095031.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf9_42519137024446095031.c:2:35: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_53850722342183111832.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf9_53850722342183111832.c:1:35: error: incompatible types when returning type 'double' but 'signed char *' was expected
               signed char * x() { return 0.2; }
                                   ^

        */
        error("""
              signed char * x() { return 0.2; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_57180541932279271719.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf9_57180541932279271719.c:2:35: error: incompatible types when initializing type 'signed char *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_71332106617647689884.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf9_71332106617647689884.c:1:35: warning: return from incompatible pointer type
               signed char * x() { return &"foo"; }
                                   ^

        */
        warning("""
              signed char * x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_76670818918632257984.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf9_76670818918632257984.c:2:35: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_87173110805822913954.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf9_87173110805822913954.c:1:35: warning: return makes pointer from integer without a cast
               signed char * x() { return *"foo"; }
                                   ^

        */
        warning("""
              signed char * x() { return *"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_8801345377147495702.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf9_8801345377147495702.c:2:35: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_92445151064261397053.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf9_92445151064261397053.c:1:42: error: lvalue required as unary '&' operand
               signed char * x() { return &1; }
                                          ^

        */
        error("""
              signed char * x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_94438053760174368554.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf9_94438053760174368554.c:2:35: error: lvalue required as unary '&' operand
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_14996704814426696584.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf10_14996704814426696584.c:1:37: warning: return makes pointer from integer without a cast
               unsigned char * x() { return 1; }
                                     ^

        */
        warning("""
              unsigned char * x() { return 1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_11733842062352608478.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf10_11733842062352608478.c:2:37: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_21622544806022008659.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf10_21622544806022008659.c:1:37: warning: return makes pointer from integer without a cast
               unsigned char * x() { return -1; }
                                     ^

        */
        warning("""
              unsigned char * x() { return -1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_28727640323088187863.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf10_28727640323088187863.c:2:37: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_35621765966780586083.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf10_35621765966780586083.c:1:37: warning: return makes pointer from integer without a cast
               unsigned char * x() { return 1l; }
                                     ^

        */
        warning("""
              unsigned char * x() { return 1l; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_3938873934975121854.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf10_3938873934975121854.c:2:37: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_44061083975453555480.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf10_44061083975453555480.c:1:37: warning: return makes pointer from integer without a cast
               unsigned char * x() { return 0xa4; }
                                     ^

        */
        warning("""
              unsigned char * x() { return 0xa4; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_44911466715789282946.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf10_44911466715789282946.c:2:37: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_53137650802455397490.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf10_53137650802455397490.c:1:37: error: incompatible types when returning type 'double' but 'unsigned char *' was expected
               unsigned char * x() { return 0.2; }
                                     ^

        */
        error("""
              unsigned char * x() { return 0.2; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_52627639271714597920.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf10_52627639271714597920.c:2:37: error: incompatible types when initializing type 'unsigned char *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_784220442671153491.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf10_784220442671153491.c:1:37: warning: return from incompatible pointer type
               unsigned char * x() { return &"foo"; }
                                     ^

        */
        warning("""
              unsigned char * x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_78978455358900746493.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf10_78978455358900746493.c:2:37: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_82339232384250841671.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf10_82339232384250841671.c:1:37: warning: return makes pointer from integer without a cast
               unsigned char * x() { return *"foo"; }
                                     ^

        */
        warning("""
              unsigned char * x() { return *"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_8371072976987293940.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf10_8371072976987293940.c:2:37: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_98469352096236972626.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf10_98469352096236972626.c:1:44: error: lvalue required as unary '&' operand
               unsigned char * x() { return &1; }
                                            ^

        */
        error("""
              unsigned char * x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_96398766461159709341.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf10_96398766461159709341.c:2:37: error: lvalue required as unary '&' operand
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_16906110044797365489.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf11_16906110044797365489.c:1:29: warning: return makes pointer from integer without a cast
               char ** x() { return 1; }
                             ^

        */
        warning("""
              char ** x() { return 1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_17205085829623723243.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf11_17205085829623723243.c:2:29: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_21630602089277920298.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf11_21630602089277920298.c:1:29: warning: return makes pointer from integer without a cast
               char ** x() { return -1; }
                             ^

        */
        warning("""
              char ** x() { return -1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_29035759797208710622.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf11_29035759797208710622.c:2:29: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_34760454566746690613.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf11_34760454566746690613.c:1:29: warning: return makes pointer from integer without a cast
               char ** x() { return 1l; }
                             ^

        */
        warning("""
              char ** x() { return 1l; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_33606595053848751696.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf11_33606595053848751696.c:2:29: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_4625394492798498682.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf11_4625394492798498682.c:1:29: warning: return makes pointer from integer without a cast
               char ** x() { return 0xa4; }
                             ^

        */
        warning("""
              char ** x() { return 0xa4; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_48746718835421773692.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf11_48746718835421773692.c:2:29: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_51228271629909079874.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf11_51228271629909079874.c:1:29: error: incompatible types when returning type 'double' but 'char **' was expected
               char ** x() { return 0.2; }
                             ^

        */
        error("""
              char ** x() { return 0.2; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_56950396321724022886.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf11_56950396321724022886.c:2:29: error: incompatible types when initializing type 'char **' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_64471977846924496577.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf11_64471977846924496577.c:1:29: warning: return from incompatible pointer type
               char ** x() { return "0.2"; }
                             ^

        */
        warning("""
              char ** x() { return "0.2"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_68763234517627514158.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf11_68763234517627514158.c:2:29: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_79179856944428686792.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf11_79179856944428686792.c:1:29: warning: return from incompatible pointer type
               char ** x() { return &"foo"; }
                             ^

        */
        warning("""
              char ** x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_73659419542748031583.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf11_73659419542748031583.c:2:29: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_87249862396007643947.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf11_87249862396007643947.c:1:29: warning: return makes pointer from integer without a cast
               char ** x() { return *"foo"; }
                             ^

        */
        warning("""
              char ** x() { return *"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_88509504003159962531.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf11_88509504003159962531.c:2:29: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_98655517320619219713.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf11_98655517320619219713.c:1:36: error: lvalue required as unary '&' operand
               char ** x() { return &1; }
                                    ^

        */
        error("""
              char ** x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_98184785230411885240.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf11_98184785230411885240.c:2:29: error: lvalue required as unary '&' operand
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_14000421622942975074.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf12_14000421622942975074.c:1:38: warning: return makes pointer from integer without a cast
               unsigned char ** x() { return 1; }
                                      ^

        */
        warning("""
              unsigned char ** x() { return 1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_14062939953869380146.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf12_14062939953869380146.c:2:38: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_24653005581571831030.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf12_24653005581571831030.c:1:38: warning: return makes pointer from integer without a cast
               unsigned char ** x() { return -1; }
                                      ^

        */
        warning("""
              unsigned char ** x() { return -1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_2515872077835574286.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf12_2515872077835574286.c:2:38: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_33711073404599145380.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf12_33711073404599145380.c:1:38: warning: return makes pointer from integer without a cast
               unsigned char ** x() { return 1l; }
                                      ^

        */
        warning("""
              unsigned char ** x() { return 1l; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_37276881781465365819.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf12_37276881781465365819.c:2:38: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_4944813055483028158.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf12_4944813055483028158.c:1:38: warning: return makes pointer from integer without a cast
               unsigned char ** x() { return 0xa4; }
                                      ^

        */
        warning("""
              unsigned char ** x() { return 0xa4; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_4695952111442673611.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf12_4695952111442673611.c:2:38: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_54680690833286479900.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf12_54680690833286479900.c:1:38: error: incompatible types when returning type 'double' but 'unsigned char **' was expected
               unsigned char ** x() { return 0.2; }
                                      ^

        */
        error("""
              unsigned char ** x() { return 0.2; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_54707248753787132841.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf12_54707248753787132841.c:2:38: error: incompatible types when initializing type 'unsigned char **' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_69045307836785055788.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf12_69045307836785055788.c:1:38: warning: return from incompatible pointer type
               unsigned char ** x() { return "0.2"; }
                                      ^

        */
        warning("""
              unsigned char ** x() { return "0.2"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_650983860976086179.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf12_650983860976086179.c:2:38: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_75234198691452803155.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf12_75234198691452803155.c:1:38: warning: return from incompatible pointer type
               unsigned char ** x() { return &"foo"; }
                                      ^

        */
        warning("""
              unsigned char ** x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_7593082842798597631.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf12_7593082842798597631.c:2:38: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_86606455131446561894.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf12_86606455131446561894.c:1:38: warning: return makes pointer from integer without a cast
               unsigned char ** x() { return *"foo"; }
                                      ^

        */
        warning("""
              unsigned char ** x() { return *"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_82896281470409037865.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf12_82896281470409037865.c:2:38: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_95604560205420593409.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf12_95604560205420593409.c:1:45: error: lvalue required as unary '&' operand
               unsigned char ** x() { return &1; }
                                             ^

        */
        error("""
              unsigned char ** x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_91508024424191210362.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf12_91508024424191210362.c:2:38: error: lvalue required as unary '&' operand
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_13179802457833393756.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf13_13179802457833393756.c:1:36: warning: return makes pointer from integer without a cast
               signed char ** x() { return 1; }
                                    ^

        */
        warning("""
              signed char ** x() { return 1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_1695904885319432386.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf13_1695904885319432386.c:2:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_23536269825856921474.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf13_23536269825856921474.c:1:36: warning: return makes pointer from integer without a cast
               signed char ** x() { return -1; }
                                    ^

        */
        warning("""
              signed char ** x() { return -1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_28541565370170731032.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf13_28541565370170731032.c:2:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_36509177792294348369.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf13_36509177792294348369.c:1:36: warning: return makes pointer from integer without a cast
               signed char ** x() { return 1l; }
                                    ^

        */
        warning("""
              signed char ** x() { return 1l; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_37514896524533329655.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf13_37514896524533329655.c:2:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_44693980711728511939.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf13_44693980711728511939.c:1:36: warning: return makes pointer from integer without a cast
               signed char ** x() { return 0xa4; }
                                    ^

        */
        warning("""
              signed char ** x() { return 0xa4; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_45676755639880525254.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf13_45676755639880525254.c:2:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_57946170507608974687.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf13_57946170507608974687.c:1:36: error: incompatible types when returning type 'double' but 'signed char **' was expected
               signed char ** x() { return 0.2; }
                                    ^

        */
        error("""
              signed char ** x() { return 0.2; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_56810846830369295694.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf13_56810846830369295694.c:2:36: error: incompatible types when initializing type 'signed char **' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_63540853783913771595.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf13_63540853783913771595.c:1:36: warning: return from incompatible pointer type
               signed char ** x() { return "0.2"; }
                                    ^

        */
        warning("""
              signed char ** x() { return "0.2"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_67725133893926515666.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf13_67725133893926515666.c:2:36: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_74327512002665692851.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf13_74327512002665692851.c:1:36: warning: return from incompatible pointer type
               signed char ** x() { return &"foo"; }
                                    ^

        */
        warning("""
              signed char ** x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_7481458144449468334.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf13_7481458144449468334.c:2:36: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_82811669576344666806.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf13_82811669576344666806.c:1:36: warning: return makes pointer from integer without a cast
               signed char ** x() { return *"foo"; }
                                    ^

        */
        warning("""
              signed char ** x() { return *"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_86099616086266080678.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf13_86099616086266080678.c:2:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_91964214364238292876.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf13_91964214364238292876.c:1:43: error: lvalue required as unary '&' operand
               signed char ** x() { return &1; }
                                           ^

        */
        error("""
              signed char ** x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_96149462475566160162.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf13_96149462475566160162.c:2:36: error: lvalue required as unary '&' operand
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_12416697347106523428.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf14_12416697347106523428.c:1:30: warning: return makes pointer from integer without a cast
               double * x() { return 1; }
                              ^

        */
        warning("""
              double * x() { return 1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_12699266691366426470.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf14_12699266691366426470.c:2:30: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_21394297951996360331.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf14_21394297951996360331.c:1:30: warning: return makes pointer from integer without a cast
               double * x() { return -1; }
                              ^

        */
        warning("""
              double * x() { return -1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_21075424391681790461.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf14_21075424391681790461.c:2:30: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_31213304949164503248.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf14_31213304949164503248.c:1:30: warning: return makes pointer from integer without a cast
               double * x() { return 1l; }
                              ^

        */
        warning("""
              double * x() { return 1l; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_32877949734538333254.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf14_32877949734538333254.c:2:30: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_4113618178583220495.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf14_4113618178583220495.c:1:30: warning: return makes pointer from integer without a cast
               double * x() { return 0xa4; }
                              ^

        */
        warning("""
              double * x() { return 0xa4; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_47992751553152554058.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf14_47992751553152554058.c:2:30: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_52143149015201395438.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf14_52143149015201395438.c:1:30: error: incompatible types when returning type 'double' but 'double *' was expected
               double * x() { return 0.2; }
                              ^

        */
        error("""
              double * x() { return 0.2; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_56673313498026379246.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf14_56673313498026379246.c:2:30: error: incompatible types when initializing type 'double *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_62997768962357124440.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf14_62997768962357124440.c:1:30: warning: return from incompatible pointer type
               double * x() { return "0.2"; }
                              ^

        */
        warning("""
              double * x() { return "0.2"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_67996227472383174778.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf14_67996227472383174778.c:2:30: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_75115810494850944980.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf14_75115810494850944980.c:1:30: warning: return from incompatible pointer type
               double * x() { return &"foo"; }
                              ^

        */
        warning("""
              double * x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_73649972785036029496.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf14_73649972785036029496.c:2:30: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_85475049177797772198.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf14_85475049177797772198.c:1:30: warning: return makes pointer from integer without a cast
               double * x() { return *"foo"; }
                              ^

        */
        warning("""
              double * x() { return *"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_81254542762788398418.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf14_81254542762788398418.c:2:30: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_96189729204530998083.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf14_96189729204530998083.c:1:37: error: lvalue required as unary '&' operand
               double * x() { return &1; }
                                     ^

        */
        error("""
              double * x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_91582311338575692561.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf14_91582311338575692561.c:2:30: error: lvalue required as unary '&' operand
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_01438997777414950761.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf15_01438997777414950761.c:3:30: error: incompatible types when returning type 'int' but 'struct S' was expected
               struct S x() { return 0; }
                              ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() { return 0; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_04072837469640752201.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf15_04072837469640752201.c:4:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_13116224494794146223.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf15_13116224494794146223.c:3:30: error: incompatible types when returning type 'int' but 'struct S' was expected
               struct S x() { return 1; }
                              ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() { return 1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_17185437466510932574.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf15_17185437466510932574.c:4:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_21136796242746386837.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf15_21136796242746386837.c:3:30: error: incompatible types when returning type 'int' but 'struct S' was expected
               struct S x() { return -1; }
                              ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() { return -1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_29181433564756895548.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf15_29181433564756895548.c:4:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_38561213480305812117.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf15_38561213480305812117.c:3:30: error: incompatible types when returning type 'long int' but 'struct S' was expected
               struct S x() { return 1l; }
                              ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() { return 1l; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_36411080314541588904.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf15_36411080314541588904.c:4:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_48074053656489109418.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf15_48074053656489109418.c:3:30: error: incompatible types when returning type 'int' but 'struct S' was expected
               struct S x() { return 0xa4; }
                              ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() { return 0xa4; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_4301420016934516618.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf15_4301420016934516618.c:4:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_54173211807694086862.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf15_54173211807694086862.c:3:30: error: incompatible types when returning type 'double' but 'struct S' was expected
               struct S x() { return 0.2; }
                              ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() { return 0.2; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_51440598513345407830.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf15_51440598513345407830.c:4:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_63233798626445078987.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf15_63233798626445078987.c:3:30: error: incompatible types when returning type 'char *' but 'struct S' was expected
               struct S x() { return "0.2"; }
                              ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() { return "0.2"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_64918203339107504554.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf15_64918203339107504554.c:4:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_74477150386057718880.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf15_74477150386057718880.c:3:30: error: incompatible types when returning type 'char (*)[4]' but 'struct S' was expected
               struct S x() { return &"foo"; }
                              ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_71828304028979693547.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf15_71828304028979693547.c:4:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_85686694406483237051.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf15_85686694406483237051.c:3:30: error: incompatible types when returning type 'char' but 'struct S' was expected
               struct S x() { return *"foo"; }
                              ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() { return *"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_81710050985683048042.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf15_81710050985683048042.c:4:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_97929309959697557440.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf15_97929309959697557440.c:3:37: error: lvalue required as unary '&' operand
               struct S x() { return &1; }
                                     ^

        */
        error("""
              struct S { int x; int y; };

              struct S x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_95965973684418591035.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf15_95965973684418591035.c:4:30: error: lvalue required as unary '&' operand
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_06606319959262870921.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf16_06606319959262870921.c:1:41: error: incompatible types when returning type 'int' but 'struct <anonymous>' was expected
               struct { float b; } x() { return 0; }
                                         ^

        */
        error("""
              struct { float b; } x() { return 0; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_03382766435235308380.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf16_03382766435235308380.c:2:24: error: invalid initializer
                 struct { float b; } a = 0;
                        ^
C:\Users\ckaestne\AppData\Local\Temp\conf16_03382766435235308380.c:3:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_16140248369788129350.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf16_16140248369788129350.c:1:41: error: incompatible types when returning type 'int' but 'struct <anonymous>' was expected
               struct { float b; } x() { return 1; }
                                         ^

        */
        error("""
              struct { float b; } x() { return 1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_16106420211309927536.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf16_16106420211309927536.c:2:24: error: invalid initializer
                 struct { float b; } a = 1;
                        ^
C:\Users\ckaestne\AppData\Local\Temp\conf16_16106420211309927536.c:3:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_26561504672314638889.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf16_26561504672314638889.c:1:41: error: incompatible types when returning type 'int' but 'struct <anonymous>' was expected
               struct { float b; } x() { return -1; }
                                         ^

        */
        error("""
              struct { float b; } x() { return -1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_26878416137118353688.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf16_26878416137118353688.c:2:24: error: invalid initializer
                 struct { float b; } a = -1;
                        ^
C:\Users\ckaestne\AppData\Local\Temp\conf16_26878416137118353688.c:3:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_38003601435017030696.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf16_38003601435017030696.c:1:41: error: incompatible types when returning type 'long int' but 'struct <anonymous>' was expected
               struct { float b; } x() { return 1l; }
                                         ^

        */
        error("""
              struct { float b; } x() { return 1l; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_3156283177424357446.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf16_3156283177424357446.c:2:24: error: invalid initializer
                 struct { float b; } a = 1l;
                        ^
C:\Users\ckaestne\AppData\Local\Temp\conf16_3156283177424357446.c:3:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_47138768455310110559.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf16_47138768455310110559.c:1:41: error: incompatible types when returning type 'int' but 'struct <anonymous>' was expected
               struct { float b; } x() { return 0xa4; }
                                         ^

        */
        error("""
              struct { float b; } x() { return 0xa4; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_48454755682461403008.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf16_48454755682461403008.c:2:24: error: invalid initializer
                 struct { float b; } a = 0xa4;
                        ^
C:\Users\ckaestne\AppData\Local\Temp\conf16_48454755682461403008.c:3:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_58058216618702184761.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf16_58058216618702184761.c:1:41: error: incompatible types when returning type 'double' but 'struct <anonymous>' was expected
               struct { float b; } x() { return 0.2; }
                                         ^

        */
        error("""
              struct { float b; } x() { return 0.2; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_5445496996638041710.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf16_5445496996638041710.c:2:24: error: invalid initializer
                 struct { float b; } a = 0.2;
                        ^
C:\Users\ckaestne\AppData\Local\Temp\conf16_5445496996638041710.c:3:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_62987716863253542707.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf16_62987716863253542707.c:1:41: error: incompatible types when returning type 'char *' but 'struct <anonymous>' was expected
               struct { float b; } x() { return "0.2"; }
                                         ^

        */
        error("""
              struct { float b; } x() { return "0.2"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_68169458227266994326.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf16_68169458227266994326.c:2:24: error: invalid initializer
                 struct { float b; } a = "0.2";
                        ^
C:\Users\ckaestne\AppData\Local\Temp\conf16_68169458227266994326.c:3:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_74892963313208348986.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf16_74892963313208348986.c:1:41: error: incompatible types when returning type 'char (*)[4]' but 'struct <anonymous>' was expected
               struct { float b; } x() { return &"foo"; }
                                         ^

        */
        error("""
              struct { float b; } x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_78725103852302615300.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf16_78725103852302615300.c:2:24: error: invalid initializer
                 struct { float b; } a = &"foo";
                        ^
C:\Users\ckaestne\AppData\Local\Temp\conf16_78725103852302615300.c:3:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_83941800838378284453.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf16_83941800838378284453.c:1:41: error: incompatible types when returning type 'char' but 'struct <anonymous>' was expected
               struct { float b; } x() { return *"foo"; }
                                         ^

        */
        error("""
              struct { float b; } x() { return *"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_84568797310228630353.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf16_84568797310228630353.c:2:24: error: invalid initializer
                 struct { float b; } a = *"foo";
                        ^
C:\Users\ckaestne\AppData\Local\Temp\conf16_84568797310228630353.c:3:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_98531317211426343628.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf16_98531317211426343628.c:1:48: error: lvalue required as unary '&' operand
               struct { float b; } x() { return &1; }
                                                ^

        */
        error("""
              struct { float b; } x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_9762415547551313502.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf16_9762415547551313502.c:2:41: error: lvalue required as unary '&' operand
                 struct { float b; } a = &1;
                                         ^
C:\Users\ckaestne\AppData\Local\Temp\conf16_9762415547551313502.c:3:17: error: incompatible types when returning type 'struct <anonymous>' but 'struct <anonymous>' was expected
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_68839619925820830858.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf17_68839619925820830858.c:1:34: warning: return makes integer from pointer without a cast
               volatile int x() { return "0.2"; }
                                  ^

        */
        warning("""
              volatile int x() { return "0.2"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_66819319499236623799.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf17_66819319499236623799.c:2:34: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_71879049180344461521.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf17_71879049180344461521.c:1:34: warning: return makes integer from pointer without a cast
               volatile int x() { return &"foo"; }
                                  ^

        */
        warning("""
              volatile int x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_73983190858078548154.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf17_73983190858078548154.c:2:34: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_97329228613347673602.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf17_97329228613347673602.c:1:41: error: lvalue required as unary '&' operand
               volatile int x() { return &1; }
                                         ^

        */
        error("""
              volatile int x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_96566859230747969303.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf17_96566859230747969303.c:2:34: error: lvalue required as unary '&' operand
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_64457512931373261113.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf18_64457512931373261113.c:1:31: warning: return makes integer from pointer without a cast
               const int x() { return "0.2"; }
                               ^

        */
        warning("""
              const int x() { return "0.2"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_65056692779424518497.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf18_65056692779424518497.c:2:31: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_75225488299334706084.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf18_75225488299334706084.c:1:31: warning: return makes integer from pointer without a cast
               const int x() { return &"foo"; }
                               ^

        */
        warning("""
              const int x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_71539267789482035470.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf18_71539267789482035470.c:2:31: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_9508685120298253928.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf18_9508685120298253928.c:1:38: error: lvalue required as unary '&' operand
               const int x() { return &1; }
                                      ^

        */
        error("""
              const int x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_98443152974700803355.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf18_98443152974700803355.c:2:31: error: lvalue required as unary '&' operand
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_66538693903345951490.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf19_66538693903345951490.c:1:34: error: incompatible types when returning type 'char *' but 'double' was expected
               const double x() { return "0.2"; }
                                  ^

        */
        error("""
              const double x() { return "0.2"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_64893003258625562953.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf19_64893003258625562953.c:2:34: error: incompatible types when initializing type 'double' using type 'char *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_75495820636900962356.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf19_75495820636900962356.c:1:34: error: incompatible types when returning type 'char (*)[4]' but 'double' was expected
               const double x() { return &"foo"; }
                                  ^

        */
        error("""
              const double x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_78742987526608492912.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf19_78742987526608492912.c:2:34: error: incompatible types when initializing type 'double' using type 'char (*)[4]'
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_96837689451076642877.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf19_96837689451076642877.c:1:41: error: lvalue required as unary '&' operand
               const double x() { return &1; }
                                         ^

        */
        error("""
              const double x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_98681953532534430994.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf19_98681953532534430994.c:2:34: error: lvalue required as unary '&' operand
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_65737845235556566329.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf20_65737845235556566329.c:1:37: error: incompatible types when returning type 'char *' but 'double' was expected
               volatile double x() { return "0.2"; }
                                     ^

        */
        error("""
              volatile double x() { return "0.2"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_63989458234608210323.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf20_63989458234608210323.c:2:37: error: incompatible types when initializing type 'double' using type 'char *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_79089542106408667809.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf20_79089542106408667809.c:1:37: error: incompatible types when returning type 'char (*)[4]' but 'double' was expected
               volatile double x() { return &"foo"; }
                                     ^

        */
        error("""
              volatile double x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_72984503388814440232.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf20_72984503388814440232.c:2:37: error: incompatible types when initializing type 'double' using type 'char (*)[4]'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_92551631236941361948.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf20_92551631236941361948.c:1:44: error: lvalue required as unary '&' operand
               volatile double x() { return &1; }
                                            ^

        */
        error("""
              volatile double x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_92620000011420167295.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf20_92620000011420167295.c:2:37: error: lvalue required as unary '&' operand
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_19139466428392161414.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf21_19139466428392161414.c:1:27: warning: return makes pointer from integer without a cast
               int * x() { return 1; }
                           ^

        */
        warning("""
              int * x() { return 1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_11149112582129445095.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf21_11149112582129445095.c:2:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_26018067823686954726.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf21_26018067823686954726.c:1:27: warning: return makes pointer from integer without a cast
               int * x() { return -1; }
                           ^

        */
        warning("""
              int * x() { return -1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_21584684728537217111.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf21_21584684728537217111.c:2:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_38372172258454408107.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf21_38372172258454408107.c:1:27: warning: return makes pointer from integer without a cast
               int * x() { return 1l; }
                           ^

        */
        warning("""
              int * x() { return 1l; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_35594195229489728953.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf21_35594195229489728953.c:2:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_42821162599987900587.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf21_42821162599987900587.c:1:27: warning: return makes pointer from integer without a cast
               int * x() { return 0xa4; }
                           ^

        */
        warning("""
              int * x() { return 0xa4; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_4377122118524793734.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf21_4377122118524793734.c:2:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_55469351378970234572.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf21_55469351378970234572.c:1:27: error: incompatible types when returning type 'double' but 'int *' was expected
               int * x() { return 0.2; }
                           ^

        */
        error("""
              int * x() { return 0.2; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_56387490639913055641.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf21_56387490639913055641.c:2:27: error: incompatible types when initializing type 'int *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_62035517467047157784.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf21_62035517467047157784.c:1:27: warning: return from incompatible pointer type
               int * x() { return "0.2"; }
                           ^

        */
        warning("""
              int * x() { return "0.2"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_61630410393336006810.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf21_61630410393336006810.c:2:27: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_77019574893269467757.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf21_77019574893269467757.c:1:27: warning: return from incompatible pointer type
               int * x() { return &"foo"; }
                           ^

        */
        warning("""
              int * x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_73213974014117291809.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf21_73213974014117291809.c:2:27: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_85160260239142525939.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf21_85160260239142525939.c:1:27: warning: return makes pointer from integer without a cast
               int * x() { return *"foo"; }
                           ^

        */
        warning("""
              int * x() { return *"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_82192623276012998135.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf21_82192623276012998135.c:2:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_91674920964070738849.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf21_91674920964070738849.c:1:34: error: lvalue required as unary '&' operand
               int * x() { return &1; }
                                  ^

        */
        error("""
              int * x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_9268210745111363514.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf21_9268210745111363514.c:2:27: error: lvalue required as unary '&' operand
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_15367373723669803326.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf22_15367373723669803326.c:1:33: warning: return makes pointer from integer without a cast
               const int * x() { return 1; }
                                 ^

        */
        warning("""
              const int * x() { return 1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_12141131823769413477.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf22_12141131823769413477.c:2:33: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_2775278083255581570.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf22_2775278083255581570.c:1:33: warning: return makes pointer from integer without a cast
               const int * x() { return -1; }
                                 ^

        */
        warning("""
              const int * x() { return -1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_26781157590093243951.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf22_26781157590093243951.c:2:33: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_33024932457228123201.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf22_33024932457228123201.c:1:33: warning: return makes pointer from integer without a cast
               const int * x() { return 1l; }
                                 ^

        */
        warning("""
              const int * x() { return 1l; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_36335097706569630903.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf22_36335097706569630903.c:2:33: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_45403092277222690162.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf22_45403092277222690162.c:1:33: warning: return makes pointer from integer without a cast
               const int * x() { return 0xa4; }
                                 ^

        */
        warning("""
              const int * x() { return 0xa4; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_4635011007677056818.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf22_4635011007677056818.c:2:33: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_57135013926104572552.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf22_57135013926104572552.c:1:33: error: incompatible types when returning type 'double' but 'const int *' was expected
               const int * x() { return 0.2; }
                                 ^

        */
        error("""
              const int * x() { return 0.2; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_55359577008741275624.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf22_55359577008741275624.c:2:33: error: incompatible types when initializing type 'const int *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_62779924021185768860.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf22_62779924021185768860.c:1:33: warning: return from incompatible pointer type
               const int * x() { return "0.2"; }
                                 ^

        */
        warning("""
              const int * x() { return "0.2"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_65038493822699082747.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf22_65038493822699082747.c:2:33: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_79088011953041386184.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf22_79088011953041386184.c:1:33: warning: return from incompatible pointer type
               const int * x() { return &"foo"; }
                                 ^

        */
        warning("""
              const int * x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_76184357608449585783.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf22_76184357608449585783.c:2:33: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_86412066551586260315.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf22_86412066551586260315.c:1:33: warning: return makes pointer from integer without a cast
               const int * x() { return *"foo"; }
                                 ^

        */
        warning("""
              const int * x() { return *"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_87263215199987500038.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf22_87263215199987500038.c:2:33: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_92767111721795445247.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf22_92767111721795445247.c:1:40: error: lvalue required as unary '&' operand
               const int * x() { return &1; }
                                        ^

        */
        error("""
              const int * x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_91096832280026848838.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf22_91096832280026848838.c:2:33: error: lvalue required as unary '&' operand
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_14215576831795047066.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf23_14215576831795047066.c:1:36: warning: return makes pointer from integer without a cast
               volatile int * x() { return 1; }
                                    ^

        */
        warning("""
              volatile int * x() { return 1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_11860781336809042504.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf23_11860781336809042504.c:2:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_24300559968110589059.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf23_24300559968110589059.c:1:36: warning: return makes pointer from integer without a cast
               volatile int * x() { return -1; }
                                    ^

        */
        warning("""
              volatile int * x() { return -1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_28530158137722182714.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf23_28530158137722182714.c:2:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_35924477263339255790.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf23_35924477263339255790.c:1:36: warning: return makes pointer from integer without a cast
               volatile int * x() { return 1l; }
                                    ^

        */
        warning("""
              volatile int * x() { return 1l; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_38670224941266564894.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf23_38670224941266564894.c:2:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_42682923676271668455.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf23_42682923676271668455.c:1:36: warning: return makes pointer from integer without a cast
               volatile int * x() { return 0xa4; }
                                    ^

        */
        warning("""
              volatile int * x() { return 0xa4; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_48609790408182300691.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf23_48609790408182300691.c:2:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_56225329370674041758.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf23_56225329370674041758.c:1:36: error: incompatible types when returning type 'double' but 'volatile int *' was expected
               volatile int * x() { return 0.2; }
                                    ^

        */
        error("""
              volatile int * x() { return 0.2; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_55679414429923111117.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf23_55679414429923111117.c:2:36: error: incompatible types when initializing type 'volatile int *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_66467358651442988466.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf23_66467358651442988466.c:1:36: warning: return from incompatible pointer type
               volatile int * x() { return "0.2"; }
                                    ^

        */
        warning("""
              volatile int * x() { return "0.2"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_62035088459313648005.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf23_62035088459313648005.c:2:36: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_7746004031413866161.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf23_7746004031413866161.c:1:36: warning: return from incompatible pointer type
               volatile int * x() { return &"foo"; }
                                    ^

        */
        warning("""
              volatile int * x() { return &"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_7111546162776099668.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf23_7111546162776099668.c:2:36: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_88985499818633823752.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf23_88985499818633823752.c:1:36: warning: return makes pointer from integer without a cast
               volatile int * x() { return *"foo"; }
                                    ^

        */
        warning("""
              volatile int * x() { return *"foo"; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_81142074848751232747.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf23_81142074848751232747.c:2:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_94491866003559432576.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf23_94491866003559432576.c:1:43: error: lvalue required as unary '&' operand
               volatile int * x() { return &1; }
                                           ^

        */
        error("""
              volatile int * x() { return &1; }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf23_92859885977122064035.c: In function 'x':
C:\Users\ckaestne\AppData\Local\Temp\conf23_92859885977122064035.c:2:36: error: lvalue required as unary '&' operand
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




}