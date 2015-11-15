package de.fosd.typechef.typesystem.generated

import org.junit._
import de.fosd.typechef.typesystem._

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
C:\Users\ckaestne\AppData\Local\Temp\conf0_95008902548455146657.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_95008902548455146657.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_97930519317752976762.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_97930519317752976762.c:3:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_107248578305449785807.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_107248578305449785807.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_104049587884013293153.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_104049587884013293153.c:3:28: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_113758186816714495216.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_113758186816714495216.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_113753567944022361703.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_113753567944022361703.c:3:28: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_127909054592673897078.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_127909054592673897078.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_125636371585219652351.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_125636371585219652351.c:3:30: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_132996708892260997148.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_132996708892260997148.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_131960488855058600969.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_131960488855058600969.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_145660243293467907507.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_145660243293467907507.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_148455889395702243920.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_148455889395702243920.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_153948183231544696926.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_153948183231544696926.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_155089236666913625913.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_155089236666913625913.c:5:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_167919358606294085846.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_167919358606294085846.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_166515106793093085127.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_166515106793093085127.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_174278368965420322332.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_174278368965420322332.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_172644438812621879273.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_172644438812621879273.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_192988755531131800978.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_192988755531131800978.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_201322766151315714041.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_201322766151315714041.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_223930637590713516610.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_223930637590713516610.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_222165574723102566948.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_222165574723102566948.c:3:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_233510792125409953543.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_233510792125409953543.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_237353649334131695554.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_237353649334131695554.c:3:33: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_242466145957956498563.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_242466145957956498563.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_241718226647291071924.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_241718226647291071924.c:3:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_96147923914936582772.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_96147923914936582772.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_93380240404958915645.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_93380240404958915645.c:3:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_102152561277348301249.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_102152561277348301249.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_107236758836493969401.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_107236758836493969401.c:3:28: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_117125610227347088564.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_117125610227347088564.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_113244868589091055105.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_113244868589091055105.c:3:28: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_121058542897700039855.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_121058542897700039855.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_122953302300020959834.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_122953302300020959834.c:3:30: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_136818238818488964424.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_136818238818488964424.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'signed char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_137079618182922277029.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_137079618182922277029.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_143892417373935256000.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_143892417373935256000.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'signed char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_148721382023600508151.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_148721382023600508151.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_152918188783753388890.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_152918188783753388890.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'signed char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_152381091486274599323.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_152381091486274599323.c:5:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_164346643635912156480.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_164346643635912156480.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'signed char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_163841621784371006806.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_163841621784371006806.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_171582073502797613208.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_171582073502797613208.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'signed char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_171934620327734616508.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_171934620327734616508.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_196729140466123234645.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_196729140466123234645.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_201700216367176194420.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_201700216367176194420.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_228766086084687777966.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_228766086084687777966.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_223073439729100792581.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_223073439729100792581.c:3:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_231234414552460053433.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_231234414552460053433.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_234030950949972034956.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_234030950949972034956.c:3:33: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_244701294399156048772.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_244701294399156048772.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_247360795129968609058.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_247360795129968609058.c:3:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_93986684614263094285.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_93986684614263094285.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_95431827020841227338.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_95431827020841227338.c:3:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_102338398834675642137.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_102338398834675642137.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_104317561287275183016.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_104317561287275183016.c:3:28: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_114735220695187899345.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_114735220695187899345.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_117064891420330951776.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_117064891420330951776.c:3:28: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_124747686511670803571.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_124747686511670803571.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_125221112807161301518.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_125221112807161301518.c:3:30: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_131954477892404807551.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_131954477892404807551.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'unsigned char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_137640011908846529923.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_137640011908846529923.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_144859938257691552542.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_144859938257691552542.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'unsigned char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_144038228951746526053.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_144038228951746526053.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_15450415497817001672.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_15450415497817001672.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'unsigned char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_151471430927379803700.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_151471430927379803700.c:5:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_164928005263262896751.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_164928005263262896751.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'unsigned char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_161110198556196938386.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_161110198556196938386.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_17191278918106712228.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_17191278918106712228.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'unsigned char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_175309046728015311417.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_175309046728015311417.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_199110950247003408294.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_199110950247003408294.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_20166563331835699628.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_20166563331835699628.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_224431535274134972638.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_224431535274134972638.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_222710192358405145686.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_222710192358405145686.c:3:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_234274515083280547509.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_234274515083280547509.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_238089689108489078784.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_238089689108489078784.c:3:33: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_242916677237898389650.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_242916677237898389650.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_24569505128119053295.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_24569505128119053295.c:3:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_97893879849125380499.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_97893879849125380499.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_94837168373188480794.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_94837168373188480794.c:3:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_104319299225710219465.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_104319299225710219465.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_105803420258226038039.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_105803420258226038039.c:3:28: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_114981639798539325269.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_114981639798539325269.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_118507311059238120907.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_118507311059238120907.c:3:28: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_122632154861146786645.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_122632154861146786645.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_128542376338053756886.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_128542376338053756886.c:3:30: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_133074166556183034676.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_133074166556183034676.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'unsigned int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_13918791326509977121.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_13918791326509977121.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_147537910257441789536.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_147537910257441789536.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'unsigned int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_148874467539587028499.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_148874467539587028499.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_157448795698944984329.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_157448795698944984329.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'unsigned int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_158560336369286745272.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_158560336369286745272.c:5:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_169006151384588342417.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_169006151384588342417.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'unsigned int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_166456245654577205632.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_166456245654577205632.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_178773585379063597694.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_178773585379063597694.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'unsigned int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_178949439730100244072.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_178949439730100244072.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_192785546305914080163.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_192785546305914080163.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_207703366742141177362.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_207703366742141177362.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_22378922918283531304.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_22378922918283531304.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_222031624299489211158.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_222031624299489211158.c:3:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_232665234418137226972.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_232665234418137226972.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_233814781432482324947.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_233814781432482324947.c:3:33: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_24580162297759947298.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_24580162297759947298.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_246495209101937375817.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_246495209101937375817.c:3:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_99001842791075737304.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_99001842791075737304.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_93873732767791065771.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_93873732767791065771.c:3:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_104178344030911119826.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_104178344030911119826.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_108518011834274208604.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_108518011834274208604.c:3:28: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_112847682052781044203.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_112847682052781044203.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_111918244022387715657.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_111918244022387715657.c:3:28: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_127056213096967725713.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_127056213096967725713.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_129080179938971735614.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_129080179938971735614.c:3:30: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_137105986880549793087.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_137105986880549793087.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_135448122912787915732.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_135448122912787915732.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_143791696056969787621.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_143791696056969787621.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_145421311758507441385.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_145421311758507441385.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_151738719000015708812.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_151738719000015708812.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_152100892503038369422.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_152100892503038369422.c:5:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_166603926447786185089.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_166603926447786185089.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_164381205506587360723.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_164381205506587360723.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_173117829454633520783.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_173117829454633520783.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_178775126898908966871.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_178775126898908966871.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_192892296811362058796.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_192892296811362058796.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_205473832999170107479.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_205473832999170107479.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_222614902581452381655.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_222614902581452381655.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_225313794172329872595.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_225313794172329872595.c:3:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_236269420238243788031.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_236269420238243788031.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_231493140830857712047.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_231493140830857712047.c:3:33: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_244242016438502935627.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_244242016438502935627.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_245887521741695535276.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_245887521741695535276.c:3:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_91795798798381276767.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_91795798798381276767.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_93818678894043958625.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_93818678894043958625.c:3:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_105896351331903762329.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_105896351331903762329.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_108896982479166772831.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_108896982479166772831.c:3:28: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_118252198127872570075.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_118252198127872570075.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_111729797740546814810.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_111729797740546814810.c:3:28: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_129177901900290477293.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_129177901900290477293.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_128197545151559837808.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_128197545151559837808.c:3:30: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_137062966771610379284.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_137062966771610379284.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'long int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_134406570662567581485.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_134406570662567581485.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_148525567142389020092.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_148525567142389020092.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'long int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_142549522164283140614.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_142549522164283140614.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_153358967285032445459.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_153358967285032445459.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'long int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_153237950581819968568.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_153237950581819968568.c:5:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_162098479262833653467.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_162098479262833653467.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'long int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_161325678326208835791.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_161325678326208835791.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_171760276138793259415.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_171760276138793259415.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'long int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_172455272637100105345.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_172455272637100105345.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_196958592800195788932.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_196958592800195788932.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_205358197097015004708.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_205358197097015004708.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_226671673364381177008.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_226671673364381177008.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_226687867629577403043.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_226687867629577403043.c:3:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_232814403407413005766.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_232814403407413005766.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_23447140926814361089.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_23447140926814361089.c:3:33: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_243062136659773798043.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_243062136659773798043.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_246113990485225173975.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_246113990485225173975.c:3:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_92820593805013053423.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_92820593805013053423.c:4:19: error: incompatible types when assigning to type 'int *' from type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_94801903243240446810.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_94801903243240446810.c:3:27: error: incompatible types when initializing type 'int *' using type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_108426886875193729416.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_108426886875193729416.c:4:19: error: incompatible types when assigning to type 'int **' from type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_103856201621345883822.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_103856201621345883822.c:3:28: error: incompatible types when initializing type 'int **' using type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_111185889451502748505.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_111185889451502748505.c:4:19: error: incompatible types when assigning to type 'char *' from type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_112382779798045203650.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_112382779798045203650.c:3:28: error: incompatible types when initializing type 'char *' using type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_12556780984263426872.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_12556780984263426872.c:4:19: error: incompatible types when assigning to type 'double *' from type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_123297617919596096467.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_123297617919596096467.c:3:30: error: incompatible types when initializing type 'double *' using type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_1323635637779657113.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_1323635637779657113.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_138181889622722712326.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_138181889622722712326.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_146842005503691548585.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_146842005503691548585.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_144308703207874400600.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_144308703207874400600.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_156776074643655077912.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_156776074643655077912.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_155052786947559150240.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_155052786947559150240.c:5:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_164027861624292382107.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_164027861624292382107.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_161970823883034009496.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_161970823883034009496.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_171281055088152229093.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_171281055088152229093.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_174832718601812720532.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_174832718601812720532.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_194956948354696644617.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_194956948354696644617.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_209121091481568871565.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_209121091481568871565.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_223242985645864074615.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_223242985645864074615.c:4:19: error: incompatible types when assigning to type 'int *' from type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_225240082178313848957.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_225240082178313848957.c:3:27: error: incompatible types when initializing type 'int *' using type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_237119636720751825178.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_237119636720751825178.c:4:19: error: incompatible types when assigning to type 'const int *' from type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_232160501672441198055.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_232160501672441198055.c:3:33: error: incompatible types when initializing type 'const int *' using type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_24568521522160314316.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_24568521522160314316.c:4:19: error: incompatible types when assigning to type 'volatile int *' from type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_248691500075520901923.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_248691500075520901923.c:3:36: error: incompatible types when initializing type 'volatile int *' using type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_9347507334320106696.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_9347507334320106696.c:4:19: error: incompatible types when assigning to type 'int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_98313674475099401130.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_98313674475099401130.c:3:27: error: incompatible types when initializing type 'int *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_106521464651104317984.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_106521464651104317984.c:4:19: error: incompatible types when assigning to type 'int **' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_109069691928272964784.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_109069691928272964784.c:3:28: error: incompatible types when initializing type 'int **' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_118455149505782709838.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_118455149505782709838.c:4:19: error: incompatible types when assigning to type 'char *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_118708150932041552720.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_118708150932041552720.c:3:28: error: incompatible types when initializing type 'char *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_122612048339439843034.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_122612048339439843034.c:4:19: error: incompatible types when assigning to type 'double *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_125275831213258729445.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_125275831213258729445.c:3:30: error: incompatible types when initializing type 'double *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_133553575381165616470.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_133553575381165616470.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_134604876881706177547.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_134604876881706177547.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_14565918954948993859.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_14565918954948993859.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_142784943339223273507.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_142784943339223273507.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_15395647021860336888.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_15395647021860336888.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_1521479283100170335.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_1521479283100170335.c:5:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_167232018052256573859.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_167232018052256573859.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_168593141701991523822.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_168593141701991523822.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_176309245044547081657.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_176309245044547081657.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_171418342735001569903.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_171418342735001569903.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_192261663771863508785.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_192261663771863508785.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_206222836015470000415.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_206222836015470000415.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_223277724993545042651.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_223277724993545042651.c:4:19: error: incompatible types when assigning to type 'int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_228202073288756889307.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_228202073288756889307.c:3:27: error: incompatible types when initializing type 'int *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_237830244473455770395.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_237830244473455770395.c:4:19: error: incompatible types when assigning to type 'const int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_234392942512968038464.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_234392942512968038464.c:3:33: error: incompatible types when initializing type 'const int *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_245218064079614124616.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_245218064079614124616.c:4:19: error: incompatible types when assigning to type 'volatile int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_242021467310540718391.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_242021467310540718391.c:3:36: error: incompatible types when initializing type 'volatile int *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_9526418645994584199.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_9526418645994584199.c:4:19: error: incompatible types when assigning to type 'int *' from type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_96339337929374929171.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_96339337929374929171.c:3:27: error: incompatible types when initializing type 'int *' using type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_107347831310674589171.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_107347831310674589171.c:4:19: error: incompatible types when assigning to type 'int **' from type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_103659090500121682812.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_103659090500121682812.c:3:28: error: incompatible types when initializing type 'int **' using type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_112794317835562294575.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_112794317835562294575.c:4:19: error: incompatible types when assigning to type 'char *' from type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_112975344623270151083.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_112975344623270151083.c:3:28: error: incompatible types when initializing type 'char *' using type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_129111779401688874534.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_129111779401688874534.c:4:19: error: incompatible types when assigning to type 'double *' from type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_128265391454476598479.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_128265391454476598479.c:3:30: error: incompatible types when initializing type 'double *' using type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_132287775458890356112.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_132287775458890356112.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_135412141238311854523.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_135412141238311854523.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_142937306997205763857.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_142937306997205763857.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_141395415209522317419.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_141395415209522317419.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_152472529800214807509.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_152472529800214807509.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_157004767555580995113.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_157004767555580995113.c:5:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_168333968944633085940.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_168333968944633085940.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_165945069699241237170.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_165945069699241237170.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_175282420280411721321.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_175282420280411721321.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_173786940406696143119.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_173786940406696143119.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_197085954774381473093.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_197085954774381473093.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_207162076239203955821.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_207162076239203955821.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_227511386177167949934.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_227511386177167949934.c:4:19: error: incompatible types when assigning to type 'int *' from type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_224759320166869589418.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_224759320166869589418.c:3:27: error: incompatible types when initializing type 'int *' using type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_231602733568013568828.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_231602733568013568828.c:4:19: error: incompatible types when assigning to type 'const int *' from type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_231349998633838373314.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_231349998633838373314.c:3:33: error: incompatible types when initializing type 'const int *' using type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_247601942281460249339.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_247601942281460249339.c:4:19: error: incompatible types when assigning to type 'volatile int *' from type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_245936257988250882207.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_245936257988250882207.c:3:36: error: incompatible types when initializing type 'volatile int *' using type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_06444716095563432361.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_06444716095563432361.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_07252010192273065847.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_07252010192273065847.c:3:26: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_18926711915754013359.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_18926711915754013359.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_1468587108834427260.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_1468587108834427260.c:3:33: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_26717384132159958981.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_26717384132159958981.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_28254357825459534107.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_28254357825459534107.c:3:35: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_34151827475106503435.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_34151827475106503435.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_3372045820840538176.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_3372045820840538176.c:3:34: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_47954115444056093532.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_47954115444056093532.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_43258016477726625722.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_43258016477726625722.c:3:32: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_55681500818833605102.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_55681500818833605102.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_51942055956727859747.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_51942055956727859747.c:3:26: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_66424453083624549855.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_66424453083624549855.c:4:19: error: incompatible types when assigning to type 'float' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_64084173213618149490.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_64084173213618149490.c:3:27: error: incompatible types when initializing type 'float' using type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_72047150788201632282.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_72047150788201632282.c:4:19: error: incompatible types when assigning to type 'double' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_77810829888012543549.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_77810829888012543549.c:3:28: error: incompatible types when initializing type 'double' using type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_86327477263586962771.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_86327477263586962771.c:4:19: error: incompatible types when assigning to type 'long double' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_83370577289013521263.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_83370577289013521263.c:3:33: error: incompatible types when initializing type 'long double' using type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_103340154528229541587.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_103340154528229541587.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_105745579358031450185.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_105745579358031450185.c:3:28: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_114377376161901525206.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_114377376161901525206.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_116068400642505620691.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_116068400642505620691.c:3:28: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_129088058845406677312.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_129088058845406677312.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_125631188142457698116.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_125631188142457698116.c:3:30: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_13239754143095329835.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_13239754143095329835.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_137780445002393252423.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_137780445002393252423.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_146886918764705462719.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_146886918764705462719.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_147594745180077029791.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_147594745180077029791.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_15411778519861342759.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_15411778519861342759.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_156324655027323729166.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_156324655027323729166.c:5:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_168567991871834414762.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_168567991871834414762.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_164830458333218274060.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_164830458333218274060.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_174656274562905681769.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_174656274562905681769.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_172695270809917456680.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_172695270809917456680.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_188833126962872915163.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_188833126962872915163.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_187684739106197717050.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_187684739106197717050.c:3:34: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_194837307983708303045.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_194837307983708303045.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_197212577027125157571.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_197212577027125157571.c:3:31: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_206299291060624279480.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_206299291060624279480.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_206192148888674879892.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_206192148888674879892.c:3:34: error: incompatible types when initializing type 'double' using type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_211061469710151919892.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_211061469710151919892.c:4:19: error: incompatible types when assigning to type 'double' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_211622201026664736943.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_211622201026664736943.c:3:37: error: incompatible types when initializing type 'double' using type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_04320232746693457625.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_04320232746693457625.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_09068616311289867158.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_09068616311289867158.c:3:26: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_15724220496297208376.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_15724220496297208376.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_17877909760483170009.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_17877909760483170009.c:3:33: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_2332422657259027773.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_2332422657259027773.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_28102893386811106840.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_28102893386811106840.c:3:35: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_38146937107087588691.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_38146937107087588691.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_38345967906402978056.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_38345967906402978056.c:3:34: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_46659273278289636851.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_46659273278289636851.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_47165498686894182839.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_47165498686894182839.c:3:32: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_55525363078830598827.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_55525363078830598827.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_56595281147943127761.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_56595281147943127761.c:3:26: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_63114771581918042858.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_63114771581918042858.c:4:19: error: incompatible types when assigning to type 'float' from type 'int **'
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_64988271709311163485.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_64988271709311163485.c:3:27: error: incompatible types when initializing type 'float' using type 'int **'
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_7247932338970633031.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_7247932338970633031.c:4:19: error: incompatible types when assigning to type 'double' from type 'int **'
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_76429189099294062226.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_76429189099294062226.c:3:28: error: incompatible types when initializing type 'double' using type 'int **'
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_88970852860512683844.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_88970852860512683844.c:4:19: error: incompatible types when assigning to type 'long double' from type 'int **'
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_85513043858845281295.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_85513043858845281295.c:3:33: error: incompatible types when initializing type 'long double' using type 'int **'
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_93189916201624477898.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_93189916201624477898.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_95137007119157552861.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_95137007119157552861.c:3:27: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_116388599948732066197.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_116388599948732066197.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_113900595255756386392.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_113900595255756386392.c:3:28: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_124296843711432249362.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_124296843711432249362.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_126898005273903289351.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_126898005273903289351.c:3:30: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_13464682712257071876.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_13464682712257071876.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'int **'
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_134150438502856549059.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_134150438502856549059.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_146788223085855276664.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_146788223085855276664.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'int **'
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_143092079748168126722.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_143092079748168126722.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_153675446499026013200.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_153675446499026013200.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'int **'
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_157392215993566809274.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_157392215993566809274.c:5:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_165372227734157473985.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_165372227734157473985.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int **'
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_166764789354530936940.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_166764789354530936940.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_172863040446817165747.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_172863040446817165747.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int **'
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_175532820555969253789.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_175532820555969253789.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_182516398735603501008.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_182516398735603501008.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_181701280044329197327.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_181701280044329197327.c:3:34: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_192910601743806023563.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_192910601743806023563.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_192607080244825889145.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_192607080244825889145.c:3:31: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_206362122642282162825.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_206362122642282162825.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_204729785751397817426.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_204729785751397817426.c:3:34: error: incompatible types when initializing type 'double' using type 'int **'
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_211015186796194575585.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_211015186796194575585.c:4:19: error: incompatible types when assigning to type 'double' from type 'int **'
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_218614816813239080592.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_218614816813239080592.c:3:37: error: incompatible types when initializing type 'double' using type 'int **'
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_229075884173036431803.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_229075884173036431803.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_225270446800439518768.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_225270446800439518768.c:3:27: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_232611004452354986592.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_232611004452354986592.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_237905992335429718912.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_237905992335429718912.c:3:33: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_246394586978322252058.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_246394586978322252058.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_245958585758878889590.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_245958585758878889590.c:3:36: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_03196649943786221612.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_03196649943786221612.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_07795641633064367911.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_07795641633064367911.c:3:26: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_12912024962054470898.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_12912024962054470898.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_18317104425515329022.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_18317104425515329022.c:3:33: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_22208995770009034215.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_22208995770009034215.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_23474359240370808372.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_23474359240370808372.c:3:35: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_3652468268959057142.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_3652468268959057142.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_3792888515977987094.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_3792888515977987094.c:3:34: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_48095616596498137205.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_48095616596498137205.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_48489509586933800901.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_48489509586933800901.c:3:32: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_594819060647180661.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_594819060647180661.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_5148509361528997762.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_5148509361528997762.c:3:26: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_61589452372275137340.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_61589452372275137340.c:4:19: error: incompatible types when assigning to type 'float' from type 'char *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_67429678949666448910.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_67429678949666448910.c:3:27: error: incompatible types when initializing type 'float' using type 'char *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_7704639119502012528.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_7704639119502012528.c:4:19: error: incompatible types when assigning to type 'double' from type 'char *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_72355601416907980603.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_72355601416907980603.c:3:28: error: incompatible types when initializing type 'double' using type 'char *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_85905079460082342507.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_85905079460082342507.c:4:19: error: incompatible types when assigning to type 'long double' from type 'char *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_84030449195653408718.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_84030449195653408718.c:3:33: error: incompatible types when initializing type 'long double' using type 'char *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_96203690533771610295.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_96203690533771610295.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_92400829949755605558.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_92400829949755605558.c:3:27: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_106492205435400322159.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_106492205435400322159.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_108103163258948487713.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_108103163258948487713.c:3:28: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_124387607053201329246.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_124387607053201329246.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_125110913084229494801.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_125110913084229494801.c:3:30: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_137909723192073511934.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_137909723192073511934.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'char *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_135654943390260057777.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_135654943390260057777.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_14594364142507529271.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_14594364142507529271.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'char *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_145797257830550967651.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_145797257830550967651.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_152292012704572352551.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_152292012704572352551.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'char *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_158053454245287734644.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_158053454245287734644.c:5:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_162145127831252607774.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_162145127831252607774.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'char *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_161638574371877482351.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_161638574371877482351.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_175630017023642377213.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_175630017023642377213.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'char *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_173326499729145424726.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_173326499729145424726.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_187416753129847829847.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_187416753129847829847.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_186293480849584816600.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_186293480849584816600.c:3:34: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_194194737927097695801.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_194194737927097695801.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_191177023587807533421.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_191177023587807533421.c:3:31: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_202033876490843120749.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_202033876490843120749.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_209001029616702283416.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_209001029616702283416.c:3:34: error: incompatible types when initializing type 'double' using type 'char *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_21294831810490616189.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_21294831810490616189.c:4:19: error: incompatible types when assigning to type 'double' from type 'char *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_218670948848938562613.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_218670948848938562613.c:3:37: error: incompatible types when initializing type 'double' using type 'char *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_22186543944458278012.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_22186543944458278012.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_225024840315044266543.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_225024840315044266543.c:3:27: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_237725945857876744510.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_237725945857876744510.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_234276791577441291464.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_234276791577441291464.c:3:33: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_247353150695465396655.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_247353150695465396655.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_247596672613936555988.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_247596672613936555988.c:3:36: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_01855851015648178181.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_01855851015648178181.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_06837233554938720765.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_06837233554938720765.c:3:26: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_17541308247909738988.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_17541308247909738988.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_15026059790131340073.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_15026059790131340073.c:3:33: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_28761586105722717480.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_28761586105722717480.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_28332301036027390467.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_28332301036027390467.c:3:35: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_33911351217079354642.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_33911351217079354642.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_38025635122505787002.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_38025635122505787002.c:3:34: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_45219715672965211339.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_45219715672965211339.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_485192998953260692.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_485192998953260692.c:3:32: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_53728804902009096980.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_53728804902009096980.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_52341618902009736449.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_52341618902009736449.c:3:26: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_62646351386690323601.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_62646351386690323601.c:4:19: error: incompatible types when assigning to type 'float' from type 'double *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_62686023683256064546.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_62686023683256064546.c:3:27: error: incompatible types when initializing type 'float' using type 'double *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_7609539080413168348.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_7609539080413168348.c:4:19: error: incompatible types when assigning to type 'double' from type 'double *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_75415731619058458502.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_75415731619058458502.c:3:28: error: incompatible types when initializing type 'double' using type 'double *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_85213339826303541960.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_85213339826303541960.c:4:19: error: incompatible types when assigning to type 'long double' from type 'double *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_82313748626743677431.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_82313748626743677431.c:3:33: error: incompatible types when initializing type 'long double' using type 'double *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_95325078191420154329.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_95325078191420154329.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_96471908397428622739.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_96471908397428622739.c:3:27: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_101007607819679597577.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_101007607819679597577.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_105783993084548950733.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_105783993084548950733.c:3:28: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_118034310144267375161.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_118034310144267375161.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_117616467039915656065.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_117616467039915656065.c:3:28: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_131791952703369696574.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_131791952703369696574.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'double *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_133423120538516346944.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_133423120538516346944.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_143107320986389722291.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_143107320986389722291.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'double *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_143130508907427446983.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_143130508907427446983.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_152917002863835116961.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_152917002863835116961.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'double *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_153445449619757764682.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_153445449619757764682.c:5:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_163836394735874037243.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_163836394735874037243.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_165213568519871095177.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_165213568519871095177.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_173989839625426293173.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_173989839625426293173.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_177821090265819952528.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_177821090265819952528.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_186669827392103405261.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_186669827392103405261.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_184395213215509114488.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_184395213215509114488.c:3:34: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_194621919000440421813.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_194621919000440421813.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_191706020897902111777.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_191706020897902111777.c:3:31: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_203491524617666586047.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_203491524617666586047.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_205041628753270518562.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_205041628753270518562.c:3:34: error: incompatible types when initializing type 'double' using type 'double *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_212552268498533817177.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_212552268498533817177.c:4:19: error: incompatible types when assigning to type 'double' from type 'double *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_216181434763901291617.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_216181434763901291617.c:3:37: error: incompatible types when initializing type 'double' using type 'double *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_22713648505805692467.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_22713648505805692467.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_228447070397394518474.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_228447070397394518474.c:3:27: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_236411044636544043263.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_236411044636544043263.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_234669597307095406607.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_234669597307095406607.c:3:33: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_241049932180721893271.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_241049932180721893271.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_247376325722858018015.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_247376325722858018015.c:3:36: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_01978627731908125493.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_01978627731908125493.c:6:19: error: incompatible types when assigning to type 'char' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_06185588014661541093.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_06185588014661541093.c:5:26: error: incompatible types when initializing type 'char' using type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_12718291462003452540.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_12718291462003452540.c:6:19: error: incompatible types when assigning to type 'signed char' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_12815635576146029039.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_12815635576146029039.c:5:33: error: incompatible types when initializing type 'signed char' using type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_23555791915946668094.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_23555791915946668094.c:6:19: error: incompatible types when assigning to type 'unsigned char' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_25198903088629671902.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_25198903088629671902.c:5:35: error: incompatible types when initializing type 'unsigned char' using type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_37653386079575090161.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_37653386079575090161.c:6:19: error: incompatible types when assigning to type 'unsigned int' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_32591841369571783528.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_32591841369571783528.c:5:34: error: incompatible types when initializing type 'unsigned int' using type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_41485620851631769948.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_41485620851631769948.c:6:19: error: incompatible types when assigning to type 'int' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_45213248719475282665.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_45213248719475282665.c:5:32: error: incompatible types when initializing type 'int' using type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_51377367915641678352.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_51377367915641678352.c:6:19: error: incompatible types when assigning to type 'long int' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_55820477903789528834.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_55820477903789528834.c:5:26: error: incompatible types when initializing type 'long int' using type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_64534754705884048353.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_64534754705884048353.c:6:19: error: incompatible types when assigning to type 'float' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_6717521109682920266.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_6717521109682920266.c:5:27: error: incompatible types when initializing type 'float' using type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_76659004138111567611.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_76659004138111567611.c:6:19: error: incompatible types when assigning to type 'double' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_71145127252039139432.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_71145127252039139432.c:5:28: error: incompatible types when initializing type 'double' using type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_82616668023917686135.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_82616668023917686135.c:6:19: error: incompatible types when assigning to type 'long double' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_83372361506404452743.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_83372361506404452743.c:5:33: error: incompatible types when initializing type 'long double' using type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_92875916436442680809.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_92875916436442680809.c:6:19: error: incompatible types when assigning to type 'int *' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_99063741141578572386.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_99063741141578572386.c:5:27: error: incompatible types when initializing type 'int *' using type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_103516474460305244951.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_103516474460305244951.c:6:19: error: incompatible types when assigning to type 'int **' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_104959350746479905404.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_104959350746479905404.c:5:28: error: incompatible types when initializing type 'int **' using type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_111149339943759237097.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_111149339943759237097.c:6:19: error: incompatible types when assigning to type 'char *' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_112166977986658622407.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_112166977986658622407.c:5:28: error: incompatible types when initializing type 'char *' using type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_121528284706536753146.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_121528284706536753146.c:6:19: error: incompatible types when assigning to type 'double *' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_122606162386073894386.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_122606162386073894386.c:5:30: error: incompatible types when initializing type 'double *' using type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_142597141696986557869.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_142597141696986557869.c:8:19: error: incompatible types when assigning to type 'struct T' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_141054922647969636379.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_141054922647969636379.c:7:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_158897481125295784205.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_158897481125295784205.c:8:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_154307879620009029267.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_154307879620009029267.c:7:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_162806267807047216917.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_162806267807047216917.c:6:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_168129509149406572555.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_168129509149406572555.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_175389317739061861190.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_175389317739061861190.c:6:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_174490581396268154750.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_174490581396268154750.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_185653277916121510141.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_185653277916121510141.c:6:19: error: incompatible types when assigning to type 'int' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_187486914566780047174.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_187486914566780047174.c:5:34: error: incompatible types when initializing type 'int' using type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_193805644228348857223.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_193805644228348857223.c:6:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_193876747511432618107.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_193876747511432618107.c:5:31: error: incompatible types when initializing type 'int' using type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_204352215742747011996.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_204352215742747011996.c:6:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_207216678829209339711.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_207216678829209339711.c:5:34: error: incompatible types when initializing type 'double' using type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_215688271040941663787.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_215688271040941663787.c:6:19: error: incompatible types when assigning to type 'double' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_218639734329611566294.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_218639734329611566294.c:5:37: error: incompatible types when initializing type 'double' using type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_227961516500733342229.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_227961516500733342229.c:6:19: error: incompatible types when assigning to type 'int *' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_227060081068751734909.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_227060081068751734909.c:5:27: error: incompatible types when initializing type 'int *' using type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_23585522748315058318.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_23585522748315058318.c:6:19: error: incompatible types when assigning to type 'const int *' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_23743172851686314868.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_23743172851686314868.c:5:33: error: incompatible types when initializing type 'const int *' using type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_242849195473201406121.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_242849195473201406121.c:6:19: error: incompatible types when assigning to type 'volatile int *' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_245221927907874054511.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_245221927907874054511.c:5:36: error: incompatible types when initializing type 'volatile int *' using type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_02622763590203120991.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_02622763590203120991.c:6:19: error: incompatible types when assigning to type 'char' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_01491451016470067774.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_01491451016470067774.c:5:26: error: incompatible types when initializing type 'char' using type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_15968845482123128871.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_15968845482123128871.c:6:19: error: incompatible types when assigning to type 'signed char' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_18598438751692918672.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_18598438751692918672.c:5:33: error: incompatible types when initializing type 'signed char' using type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_2543414897767863494.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_2543414897767863494.c:6:19: error: incompatible types when assigning to type 'unsigned char' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_29133839378162860498.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_29133839378162860498.c:5:35: error: incompatible types when initializing type 'unsigned char' using type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_32777918938905231708.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_32777918938905231708.c:6:19: error: incompatible types when assigning to type 'unsigned int' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_34682071827247906918.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_34682071827247906918.c:5:34: error: incompatible types when initializing type 'unsigned int' using type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_47863355855359554083.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_47863355855359554083.c:6:19: error: incompatible types when assigning to type 'int' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_44534492428486274557.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_44534492428486274557.c:5:32: error: incompatible types when initializing type 'int' using type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_57526370540228449287.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_57526370540228449287.c:6:19: error: incompatible types when assigning to type 'long int' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_57178760671983276788.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_57178760671983276788.c:5:26: error: incompatible types when initializing type 'long int' using type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_66408324727875029022.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_66408324727875029022.c:6:19: error: incompatible types when assigning to type 'float' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_65916273912433224791.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_65916273912433224791.c:5:27: error: incompatible types when initializing type 'float' using type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_77789058078788332028.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_77789058078788332028.c:6:19: error: incompatible types when assigning to type 'double' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_71314171645558793812.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_71314171645558793812.c:5:28: error: incompatible types when initializing type 'double' using type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_88533035363336884211.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_88533035363336884211.c:6:19: error: incompatible types when assigning to type 'long double' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_87964618162533949764.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_87964618162533949764.c:5:33: error: incompatible types when initializing type 'long double' using type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_98138944914691659630.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_98138944914691659630.c:6:19: error: incompatible types when assigning to type 'int *' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_9246974545349916623.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_9246974545349916623.c:5:27: error: incompatible types when initializing type 'int *' using type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_101216499452992716113.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_101216499452992716113.c:6:19: error: incompatible types when assigning to type 'int **' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_104307397234033355108.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_104307397234033355108.c:5:28: error: incompatible types when initializing type 'int **' using type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_11102702444296201792.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_11102702444296201792.c:6:19: error: incompatible types when assigning to type 'char *' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_114719996324138172424.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_114719996324138172424.c:5:28: error: incompatible types when initializing type 'char *' using type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_127258006657196189380.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_127258006657196189380.c:6:19: error: incompatible types when assigning to type 'double *' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_122684503857347752321.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_122684503857347752321.c:5:30: error: incompatible types when initializing type 'double *' using type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_136620703947791711401.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_136620703947791711401.c:8:19: error: incompatible types when assigning to type 'struct S' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_138523166218024189659.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_138523166218024189659.c:7:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_156964461149173996528.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_156964461149173996528.c:8:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_151588361922400513225.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_151588361922400513225.c:7:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_166730289795211705498.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_166730289795211705498.c:6:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_168868177478064223471.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_168868177478064223471.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_171266776523724688144.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_171266776523724688144.c:6:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_175717924017481835492.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_175717924017481835492.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_185185710922282644455.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_185185710922282644455.c:6:19: error: incompatible types when assigning to type 'int' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_182252275263140276736.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_182252275263140276736.c:5:34: error: incompatible types when initializing type 'int' using type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_192037811961870023293.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_192037811961870023293.c:6:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_192601145102045356610.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_192601145102045356610.c:5:31: error: incompatible types when initializing type 'int' using type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_207671918702199291901.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_207671918702199291901.c:6:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_205835842127420992577.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_205835842127420992577.c:5:34: error: incompatible types when initializing type 'double' using type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_211954442176620182964.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_211954442176620182964.c:6:19: error: incompatible types when assigning to type 'double' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_217348695211316302274.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_217348695211316302274.c:5:37: error: incompatible types when initializing type 'double' using type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_226842978297122945117.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_226842978297122945117.c:6:19: error: incompatible types when assigning to type 'int *' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_22777330864861075525.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_22777330864861075525.c:5:27: error: incompatible types when initializing type 'int *' using type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_236968974422452855811.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_236968974422452855811.c:6:19: error: incompatible types when assigning to type 'const int *' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_238602425326558267001.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_238602425326558267001.c:5:33: error: incompatible types when initializing type 'const int *' using type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_242653249477087036740.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_242653249477087036740.c:6:19: error: incompatible types when assigning to type 'volatile int *' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_246638735154694547318.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_246638735154694547318.c:5:36: error: incompatible types when initializing type 'volatile int *' using type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_01950211796496802879.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_01950211796496802879.c:6:19: error: incompatible types when assigning to type 'char' from type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_06606940253770167958.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_06606940253770167958.c:5:26: error: incompatible types when initializing type 'char' using type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_13252862179145749306.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_13252862179145749306.c:6:19: error: incompatible types when assigning to type 'signed char' from type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_15498362742867969279.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_15498362742867969279.c:5:33: error: incompatible types when initializing type 'signed char' using type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_24523501254016403903.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_24523501254016403903.c:6:19: error: incompatible types when assigning to type 'unsigned char' from type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_29180974431347657451.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_29180974431347657451.c:5:35: error: incompatible types when initializing type 'unsigned char' using type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_38850533214633850481.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_38850533214633850481.c:6:19: error: incompatible types when assigning to type 'unsigned int' from type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_38338105825772450061.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_38338105825772450061.c:5:34: error: incompatible types when initializing type 'unsigned int' using type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_46008659718475943449.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_46008659718475943449.c:6:19: error: incompatible types when assigning to type 'int' from type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_42115422921183912815.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_42115422921183912815.c:5:32: error: incompatible types when initializing type 'int' using type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_58142116120381101997.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_58142116120381101997.c:6:19: error: incompatible types when assigning to type 'long int' from type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_52469432527966973515.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_52469432527966973515.c:5:26: error: incompatible types when initializing type 'long int' using type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_61250635561355494587.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_61250635561355494587.c:6:19: error: incompatible types when assigning to type 'float' from type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_6907966128740323120.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_6907966128740323120.c:5:27: error: incompatible types when initializing type 'float' using type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_74009403884276251315.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_74009403884276251315.c:6:19: error: incompatible types when assigning to type 'double' from type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_72251228619628608019.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_72251228619628608019.c:5:28: error: incompatible types when initializing type 'double' using type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_86061226353752165524.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_86061226353752165524.c:6:19: error: incompatible types when assigning to type 'long double' from type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_86339384222462902193.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_86339384222462902193.c:5:33: error: incompatible types when initializing type 'long double' using type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_92244492372946079243.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_92244492372946079243.c:6:19: error: incompatible types when assigning to type 'int *' from type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_92143975672264740392.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_92143975672264740392.c:5:27: error: incompatible types when initializing type 'int *' using type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_108982068668474165452.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_108982068668474165452.c:6:19: error: incompatible types when assigning to type 'int **' from type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_104392143107466169332.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_104392143107466169332.c:5:28: error: incompatible types when initializing type 'int **' using type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_11196441987166085016.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_11196441987166085016.c:6:19: error: incompatible types when assigning to type 'char *' from type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_116482621519127732589.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_116482621519127732589.c:5:28: error: incompatible types when initializing type 'char *' using type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_121883381607079566474.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_121883381607079566474.c:6:19: error: incompatible types when assigning to type 'double *' from type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_12935076498036550817.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_12935076498036550817.c:5:30: error: incompatible types when initializing type 'double *' using type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_135951186340399040750.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_135951186340399040750.c:8:19: error: incompatible types when assigning to type 'struct S' from type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_132372559134360962453.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_132372559134360962453.c:7:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_145802014786657267567.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_145802014786657267567.c:8:19: error: incompatible types when assigning to type 'struct T' from type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_142426275299487580741.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_142426275299487580741.c:7:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_161680089643093321840.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_161680089643093321840.c:6:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_163518190383415599733.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_163518190383415599733.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_17901686015254301851.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_17901686015254301851.c:6:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_173249565906173772632.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_173249565906173772632.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_181032004673102998247.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_181032004673102998247.c:6:19: error: incompatible types when assigning to type 'int' from type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_1866227695903349326.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_1866227695903349326.c:5:34: error: incompatible types when initializing type 'int' using type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_196029302827945379120.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_196029302827945379120.c:6:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_192159698861416725461.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_192159698861416725461.c:5:31: error: incompatible types when initializing type 'int' using type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_207033264878359525186.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_207033264878359525186.c:6:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_20184880163677340955.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_20184880163677340955.c:5:34: error: incompatible types when initializing type 'double' using type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_214065453752606883244.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_214065453752606883244.c:6:19: error: incompatible types when assigning to type 'double' from type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_21648641924127619392.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_21648641924127619392.c:5:37: error: incompatible types when initializing type 'double' using type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_223644621853146848563.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_223644621853146848563.c:6:19: error: incompatible types when assigning to type 'int *' from type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_228450348182800530670.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_228450348182800530670.c:5:27: error: incompatible types when initializing type 'int *' using type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_233954233959676015337.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_233954233959676015337.c:6:19: error: incompatible types when assigning to type 'const int *' from type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_238105129981326701870.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_238105129981326701870.c:5:33: error: incompatible types when initializing type 'const int *' using type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_243746372180955273044.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_243746372180955273044.c:6:19: error: incompatible types when assigning to type 'volatile int *' from type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_245156011224497417136.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_245156011224497417136.c:5:36: error: incompatible types when initializing type 'volatile int *' using type 'struct_anonymous'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_0218870181383449615.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_0218870181383449615.c:4:19: error: incompatible types when assigning to type 'char' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_01302581224453178838.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_01302581224453178838.c:3:26: error: incompatible types when initializing type 'char' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_13843060260300865721.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_13843060260300865721.c:4:19: error: incompatible types when assigning to type 'signed char' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_19080989783464404548.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_19080989783464404548.c:3:33: error: incompatible types when initializing type 'signed char' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_28813711808930378826.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_28813711808930378826.c:4:19: error: incompatible types when assigning to type 'unsigned char' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_22336321764898680823.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_22336321764898680823.c:3:35: error: incompatible types when initializing type 'unsigned char' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_38660111917642413106.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_38660111917642413106.c:4:19: error: incompatible types when assigning to type 'unsigned int' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_38023491113965785889.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_38023491113965785889.c:3:34: error: incompatible types when initializing type 'unsigned int' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_47750084324608496683.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_47750084324608496683.c:4:19: error: incompatible types when assigning to type 'int' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_46681774950402948319.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_46681774950402948319.c:3:32: error: incompatible types when initializing type 'int' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_55931365436810792006.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_55931365436810792006.c:4:19: error: incompatible types when assigning to type 'long int' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_52583173520733170392.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_52583173520733170392.c:3:26: error: incompatible types when initializing type 'long int' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_62846786499905963253.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_62846786499905963253.c:4:19: error: incompatible types when assigning to type 'float' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_68250376685418075392.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_68250376685418075392.c:3:27: error: incompatible types when initializing type 'float' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_73851692078902430391.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_73851692078902430391.c:4:19: error: incompatible types when assigning to type 'double' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_71715888365959898636.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_71715888365959898636.c:3:28: error: incompatible types when initializing type 'double' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_84383512135477010426.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_84383512135477010426.c:4:19: error: incompatible types when assigning to type 'long double' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_84150621709917617355.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_84150621709917617355.c:3:33: error: incompatible types when initializing type 'long double' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_95345516436985376966.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_95345516436985376966.c:4:19: error: incompatible types when assigning to type 'int *' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_97758952620445251717.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_97758952620445251717.c:3:27: error: incompatible types when initializing type 'int *' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_102100201375945049506.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_102100201375945049506.c:4:19: error: incompatible types when assigning to type 'int **' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_102658445751372912344.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_102658445751372912344.c:3:28: error: incompatible types when initializing type 'int **' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_113457693999815507451.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_113457693999815507451.c:4:19: error: incompatible types when assigning to type 'char *' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_112730721249076876615.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_112730721249076876615.c:3:28: error: incompatible types when initializing type 'char *' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_128266120910159271558.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_128266120910159271558.c:4:19: error: incompatible types when assigning to type 'double *' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_121148520973900196011.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_121148520973900196011.c:3:30: error: incompatible types when initializing type 'double *' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_132106925257634604786.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_132106925257634604786.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_138598196908009543474.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_138598196908009543474.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_143027679958770106286.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_143027679958770106286.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_148366989921565012078.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_148366989921565012078.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_157840753539490836977.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_157840753539490836977.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_154617409292754738434.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_154617409292754738434.c:5:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_167428145050355063185.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_167428145050355063185.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_163535404017783342130.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_163535404017783342130.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_178196380566417112561.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_178196380566417112561.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_171957973518358775762.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_171957973518358775762.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_18309596257841609643.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_18309596257841609643.c:4:19: error: incompatible types when assigning to type 'int' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_184352724763661469368.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_184352724763661469368.c:3:34: error: incompatible types when initializing type 'int' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_196466916143620579580.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_196466916143620579580.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_198498572452063277018.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_198498572452063277018.c:3:31: error: incompatible types when initializing type 'int' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_203309451376785395038.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_203309451376785395038.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_204185270730593661671.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_204185270730593661671.c:3:34: error: incompatible types when initializing type 'double' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_215464589570549560318.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_215464589570549560318.c:4:19: error: incompatible types when assigning to type 'double' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_212259567147063722382.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_212259567147063722382.c:3:37: error: incompatible types when initializing type 'double' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_228081345083841211329.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_228081345083841211329.c:4:19: error: incompatible types when assigning to type 'int *' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_226598248750026989359.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_226598248750026989359.c:3:27: error: incompatible types when initializing type 'int *' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_238315666586044183808.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_238315666586044183808.c:4:19: error: incompatible types when assigning to type 'const int *' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_237323538990978355970.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_237323538990978355970.c:3:33: error: incompatible types when initializing type 'const int *' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_248235999179626481499.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_248235999179626481499.c:4:19: error: incompatible types when assigning to type 'volatile int *' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_245471578069686635244.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_245471578069686635244.c:3:36: error: incompatible types when initializing type 'volatile int *' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_01715679941755976182.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_01715679941755976182.c:4:19: error: incompatible types when assigning to type 'char' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_06935928314013234573.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_06935928314013234573.c:3:26: error: incompatible types when initializing type 'char' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_14357842050188131275.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_14357842050188131275.c:4:19: error: incompatible types when assigning to type 'signed char' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_14790927005471734263.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_14790927005471734263.c:3:33: error: incompatible types when initializing type 'signed char' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_26364054487697842878.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_26364054487697842878.c:4:19: error: incompatible types when assigning to type 'unsigned char' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_25436017796614575061.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_25436017796614575061.c:3:35: error: incompatible types when initializing type 'unsigned char' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_31738154176223022378.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_31738154176223022378.c:4:19: error: incompatible types when assigning to type 'unsigned int' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_33580325615798274969.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_33580325615798274969.c:3:34: error: incompatible types when initializing type 'unsigned int' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_47006789307983937695.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_47006789307983937695.c:4:19: error: incompatible types when assigning to type 'int' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_42872152704762475469.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_42872152704762475469.c:3:32: error: incompatible types when initializing type 'int' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_56651452367470080191.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_56651452367470080191.c:4:19: error: incompatible types when assigning to type 'long int' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_54179725852045150470.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_54179725852045150470.c:3:26: error: incompatible types when initializing type 'long int' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_6557514884547643740.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_6557514884547643740.c:4:19: error: incompatible types when assigning to type 'float' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_61884786984336076980.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_61884786984336076980.c:3:27: error: incompatible types when initializing type 'float' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_73066033592145461229.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_73066033592145461229.c:4:19: error: incompatible types when assigning to type 'double' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_72446138931680492968.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_72446138931680492968.c:3:28: error: incompatible types when initializing type 'double' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_84461817275728518275.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_84461817275728518275.c:4:19: error: incompatible types when assigning to type 'long double' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_81348461279729961946.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_81348461279729961946.c:3:33: error: incompatible types when initializing type 'long double' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_9294706676357701810.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_9294706676357701810.c:4:19: error: incompatible types when assigning to type 'int *' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_9354152233383107906.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_9354152233383107906.c:3:27: error: incompatible types when initializing type 'int *' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_105792246273172265095.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_105792246273172265095.c:4:19: error: incompatible types when assigning to type 'int **' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_104313172629335762323.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_104313172629335762323.c:3:28: error: incompatible types when initializing type 'int **' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_11601031031538863639.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_11601031031538863639.c:4:19: error: incompatible types when assigning to type 'char *' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_112569480555004035280.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_112569480555004035280.c:3:28: error: incompatible types when initializing type 'char *' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_122538906317031288742.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_122538906317031288742.c:4:19: error: incompatible types when assigning to type 'double *' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_125826736730680842090.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_125826736730680842090.c:3:30: error: incompatible types when initializing type 'double *' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_131314939613590193697.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_131314939613590193697.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_135699385003130698067.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_135699385003130698067.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_1453037458919021983.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_1453037458919021983.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_142927070605849327125.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_142927070605849327125.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_153962904581537713300.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_153962904581537713300.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_152033852123950216109.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_152033852123950216109.c:5:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_168634282224074004209.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_168634282224074004209.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_164456459107074535246.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_164456459107074535246.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_17582479327984268956.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_17582479327984268956.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_178208698748658625457.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_178208698748658625457.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_185230236263748540134.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_185230236263748540134.c:4:19: error: incompatible types when assigning to type 'int' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_181253752938616982427.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_181253752938616982427.c:3:34: error: incompatible types when initializing type 'int' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_193351453042639536211.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_193351453042639536211.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_191525167062515783257.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_191525167062515783257.c:3:31: error: incompatible types when initializing type 'int' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_206798346634388787201.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_206798346634388787201.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_208945948639254869916.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_208945948639254869916.c:3:34: error: incompatible types when initializing type 'double' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_219118176035140729894.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_219118176035140729894.c:4:19: error: incompatible types when assigning to type 'double' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_214686288890588524190.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_214686288890588524190.c:3:37: error: incompatible types when initializing type 'double' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_22711686063159767896.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_22711686063159767896.c:4:19: error: incompatible types when assigning to type 'int *' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_227944839180917560776.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_227944839180917560776.c:3:27: error: incompatible types when initializing type 'int *' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_238979674941209365145.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_238979674941209365145.c:4:19: error: incompatible types when assigning to type 'const int *' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_237725689280864594549.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_237725689280864594549.c:3:33: error: incompatible types when initializing type 'const int *' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_245086194134450111425.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_245086194134450111425.c:4:19: error: incompatible types when assigning to type 'volatile int *' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_245147134743508229082.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_245147134743508229082.c:3:36: error: incompatible types when initializing type 'volatile int *' using type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_91943534810609307946.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_91943534810609307946.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_98684484847994361656.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_98684484847994361656.c:3:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_107510030004368500477.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_107510030004368500477.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_107078540377267256027.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_107078540377267256027.c:3:28: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_117745518451831181577.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_117745518451831181577.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_111892070438089451863.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_111892070438089451863.c:3:28: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_124563737965436245796.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_124563737965436245796.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_127512351537500018101.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_127512351537500018101.c:3:30: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_138318577076604346031.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_138318577076604346031.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_138836508279605846160.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_138836508279605846160.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_143882075700424798347.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_143882075700424798347.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_142195082465616888259.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_142195082465616888259.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_156347842642145096152.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_156347842642145096152.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_155372849827059425903.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_155372849827059425903.c:5:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_162062162453855521746.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_162062162453855521746.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_167131509903593654407.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_167131509903593654407.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_171343977464330050359.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_171343977464330050359.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_173160185404030363799.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_173160185404030363799.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_192625298773059270456.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_192625298773059270456.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_206613826990634742780.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_206613826990634742780.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_227898179090162083973.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_227898179090162083973.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_226042474665992498265.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_226042474665992498265.c:3:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_235055968333269864584.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_235055968333269864584.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_23585962613443249443.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_23585962613443249443.c:3:33: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_241703936060123404925.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_241703936060123404925.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_242995277777021433993.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_242995277777021433993.c:3:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_91573417150892704462.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_91573417150892704462.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_92580838494529676544.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_92580838494529676544.c:3:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_107648921243913246203.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_107648921243913246203.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_10351465588461034982.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_10351465588461034982.c:3:28: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_114038173912897533585.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_114038173912897533585.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_115902670768808997689.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_115902670768808997689.c:3:28: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_127083626560858247722.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_127083626560858247722.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_123238945185449651824.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_123238945185449651824.c:3:30: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_136664334786413605872.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_136664334786413605872.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_132392749450274571376.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_132392749450274571376.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_14387819333411843591.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_14387819333411843591.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_145844918208723884380.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_145844918208723884380.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_151014269084569987863.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_151014269084569987863.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_153007073774744795303.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_153007073774744795303.c:5:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_165507543602691635115.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_165507543602691635115.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_164178959612979827275.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_164178959612979827275.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_175053504210171610291.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_175053504210171610291.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_174602678195024940654.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_174602678195024940654.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_197552334823156221691.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_197552334823156221691.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_204541951321959673548.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_204541951321959673548.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_222729346886736183111.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_222729346886736183111.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_225924543517649693490.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_225924543517649693490.c:3:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_232796831515884161828.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_232796831515884161828.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_233115635168309592546.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_233115635168309592546.c:3:33: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_246206995990375984735.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_246206995990375984735.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_243423279258198328206.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_243423279258198328206.c:3:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_96599416720418417680.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_96599416720418417680.c:4:19: error: incompatible types when assigning to type 'int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_94727696552893998085.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_94727696552893998085.c:3:27: error: incompatible types when initializing type 'int *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_106557852877663670434.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_106557852877663670434.c:4:19: error: incompatible types when assigning to type 'int **' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_102984510404680010866.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_102984510404680010866.c:3:28: error: incompatible types when initializing type 'int **' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_113660755635624729301.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_113660755635624729301.c:4:19: error: incompatible types when assigning to type 'char *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_116325237064346827647.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_116325237064346827647.c:3:28: error: incompatible types when initializing type 'char *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_127268613968805504133.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_127268613968805504133.c:4:19: error: incompatible types when assigning to type 'double *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_123230604430289611866.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_123230604430289611866.c:3:30: error: incompatible types when initializing type 'double *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_133887871406662249108.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_133887871406662249108.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_133420456239266735835.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_133420456239266735835.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_145194845628643236454.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_145194845628643236454.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_148807664965687640216.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_148807664965687640216.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_1595193013983931336.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_1595193013983931336.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_157834208397990667264.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_157834208397990667264.c:5:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_167016490178917090778.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_167016490178917090778.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_166511327861986486235.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_166511327861986486235.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_173776993324662140377.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_173776993324662140377.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_175551199319353010410.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_175551199319353010410.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_191827353530071764978.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_191827353530071764978.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_207609377210710248600.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_207609377210710248600.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_22299109168668456712.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_22299109168668456712.c:4:19: error: incompatible types when assigning to type 'int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_227249396107983127428.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_227249396107983127428.c:3:27: error: incompatible types when initializing type 'int *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_232278585164418134724.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_232278585164418134724.c:4:19: error: incompatible types when assigning to type 'const int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_238371382227783887708.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_238371382227783887708.c:3:33: error: incompatible types when initializing type 'const int *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_243265591784410383911.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_243265591784410383911.c:4:19: error: incompatible types when assigning to type 'volatile int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_246533248102836446372.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_246533248102836446372.c:3:36: error: incompatible types when initializing type 'volatile int *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_92130322105114840944.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_92130322105114840944.c:4:19: error: incompatible types when assigning to type 'int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_95300762803454706645.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_95300762803454706645.c:3:27: error: incompatible types when initializing type 'int *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_10156980310675920681.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_10156980310675920681.c:4:19: error: incompatible types when assigning to type 'int **' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_108621488639809918778.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_108621488639809918778.c:3:28: error: incompatible types when initializing type 'int **' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_112835023883260409866.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_112835023883260409866.c:4:19: error: incompatible types when assigning to type 'char *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_118379204575027234445.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_118379204575027234445.c:3:28: error: incompatible types when initializing type 'char *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_12516175686096050252.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_12516175686096050252.c:4:19: error: incompatible types when assigning to type 'double *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_124302041277755907702.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_124302041277755907702.c:3:30: error: incompatible types when initializing type 'double *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_136110722318767712429.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_136110722318767712429.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_135890664505583446019.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_135890664505583446019.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_145510590917255783743.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_145510590917255783743.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_143285408524021967163.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_143285408524021967163.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_158055580651489972160.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_158055580651489972160.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_158607285338064917551.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_158607285338064917551.c:5:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_166118869824533803863.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_166118869824533803863.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_164839126324823788250.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_164839126324823788250.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_172851945404389124991.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_172851945404389124991.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_171211434077205369210.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_171211434077205369210.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_192825241470106341277.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_192825241470106341277.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_203413568887342938284.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_203413568887342938284.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_223551420308562752125.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_223551420308562752125.c:4:19: error: incompatible types when assigning to type 'int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_224994713819330497931.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_224994713819330497931.c:3:27: error: incompatible types when initializing type 'int *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_236173065484476035859.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_236173065484476035859.c:4:19: error: incompatible types when assigning to type 'const int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_237447401664120586305.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_237447401664120586305.c:3:33: error: incompatible types when initializing type 'const int *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_242993175627146686000.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_242993175627146686000.c:4:19: error: incompatible types when assigning to type 'volatile int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_244854158380644826335.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_244854158380644826335.c:3:36: error: incompatible types when initializing type 'volatile int *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_02335629461534800791.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_02335629461534800791.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_02860939672353068918.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_02860939672353068918.c:3:26: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_16269055190435950811.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_16269055190435950811.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_18179135451934135311.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_18179135451934135311.c:3:33: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_28924480123966988726.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_28924480123966988726.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_28452896012470933185.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_28452896012470933185.c:3:35: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_38533826833945636216.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_38533826833945636216.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_31898147230441245089.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_31898147230441245089.c:3:34: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_4102680415976002153.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_4102680415976002153.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_46387573440843352506.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_46387573440843352506.c:3:32: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_5401751013002904137.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_5401751013002904137.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_57095658833747600500.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_57095658833747600500.c:3:26: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_64210590152704399756.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_64210590152704399756.c:4:19: error: incompatible types when assigning to type 'float' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_62626168042123564179.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_62626168042123564179.c:3:27: error: incompatible types when initializing type 'float' using type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_7572624932005063516.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_7572624932005063516.c:4:19: error: incompatible types when assigning to type 'double' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_76808233868386711577.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_76808233868386711577.c:3:28: error: incompatible types when initializing type 'double' using type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_89072232227082217879.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_89072232227082217879.c:4:19: error: incompatible types when assigning to type 'long double' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_88407034383016553694.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_88407034383016553694.c:3:33: error: incompatible types when initializing type 'long double' using type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_10531790146206972790.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_10531790146206972790.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_102473059977146420511.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_102473059977146420511.c:3:28: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_114557620523959789226.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_114557620523959789226.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_117984978687125832844.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_117984978687125832844.c:3:28: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_1223635035047175630.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_1223635035047175630.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_123125334036686290911.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_123125334036686290911.c:3:30: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_13466678591074138595.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_13466678591074138595.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_136748896884162410849.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_136748896884162410849.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_146590395739434851015.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_146590395739434851015.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_14867307989931058570.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_14867307989931058570.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_156662190883890985365.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_156662190883890985365.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_154855736899753309001.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_154855736899753309001.c:5:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_16160297086591979723.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_16160297086591979723.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_166298447577595463191.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_166298447577595463191.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_17738253687154285193.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_17738253687154285193.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_174164932212112608451.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_174164932212112608451.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_182377401304198761022.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_182377401304198761022.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_183621550335892822714.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_183621550335892822714.c:3:34: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_191574042460314206976.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_191574042460314206976.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_198819292823540765155.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_198819292823540765155.c:3:31: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_20702595255216639320.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_20702595255216639320.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_207446673056846224196.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_207446673056846224196.c:3:34: error: incompatible types when initializing type 'double' using type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_211898365897842474426.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_211898365897842474426.c:4:19: error: incompatible types when assigning to type 'double' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_217999926225554713500.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_217999926225554713500.c:3:37: error: incompatible types when initializing type 'double' using type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_02207888033749665341.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_02207888033749665341.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_08654667194365346777.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_08654667194365346777.c:3:26: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_182972250158473531.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_182972250158473531.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_15854200623584287132.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_15854200623584287132.c:3:33: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_24992220055678999535.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_24992220055678999535.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_21955084724633207473.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_21955084724633207473.c:3:35: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_3694250461584767043.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_3694250461584767043.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_37530707299842847717.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_37530707299842847717.c:3:34: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_45404712572027212854.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_45404712572027212854.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_4108785473294871588.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_4108785473294871588.c:3:32: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_57974603008004709143.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_57974603008004709143.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_514430619793662129.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_514430619793662129.c:3:26: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_67619637561681407328.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_67619637561681407328.c:4:19: error: incompatible types when assigning to type 'float' from type 'const int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_64766747649382491498.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_64766747649382491498.c:3:27: error: incompatible types when initializing type 'float' using type 'const int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_72854229222224321840.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_72854229222224321840.c:4:19: error: incompatible types when assigning to type 'double' from type 'const int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_73532938922086800610.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_73532938922086800610.c:3:28: error: incompatible types when initializing type 'double' using type 'const int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_88481845329977800784.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_88481845329977800784.c:4:19: error: incompatible types when assigning to type 'long double' from type 'const int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_86296767627455853270.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_86296767627455853270.c:3:33: error: incompatible types when initializing type 'long double' using type 'const int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_91197191345079584187.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_91197191345079584187.c:4:19: warning: assignment discards 'const' qualifier from pointer target type
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_98014675623475083027.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_98014675623475083027.c:3:27: warning: initialization discards 'const' qualifier from pointer target type
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_1042758231489826306.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_1042758231489826306.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_103355335432260603330.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_103355335432260603330.c:3:28: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_114619281879070596116.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_114619281879070596116.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_116600993151243695582.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_116600993151243695582.c:3:28: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_12631684060998153378.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_12631684060998153378.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_12695833625305324992.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_12695833625305324992.c:3:30: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_133288130298666121685.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_133288130298666121685.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'const int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_131527318793725434447.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_131527318793725434447.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_143251197388304117068.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_143251197388304117068.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'const int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_148468102357214455243.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_148468102357214455243.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_158294280136612378338.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_158294280136612378338.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'const int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_156873940550301481690.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_156873940550301481690.c:5:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_163260705892415584045.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_163260705892415584045.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'const int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_164729377657873743906.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_164729377657873743906.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_178323767907341426655.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_178323767907341426655.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'const int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_175512139451817907195.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_175512139451817907195.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_183063948602074432092.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_183063948602074432092.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_188752826773302410342.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_188752826773302410342.c:3:34: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_197119137992291652972.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_197119137992291652972.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_199128709274260388891.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_199128709274260388891.c:3:31: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_202989922559814935404.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_202989922559814935404.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_20241470513607707440.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_20241470513607707440.c:3:34: error: incompatible types when initializing type 'double' using type 'const int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_21544071765344115621.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_21544071765344115621.c:4:19: error: incompatible types when assigning to type 'double' from type 'const int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_21327394766889408676.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_21327394766889408676.c:3:37: error: incompatible types when initializing type 'double' using type 'const int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_228004545785016375957.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_228004545785016375957.c:4:19: warning: assignment discards 'const' qualifier from pointer target type
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_221651961528437551207.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_221651961528437551207.c:3:27: warning: initialization discards 'const' qualifier from pointer target type
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_247490271575412098246.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_247490271575412098246.c:4:19: warning: assignment discards 'const' qualifier from pointer target type
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
C:\Users\ckaestne\AppData\Local\Temp\conf23_247756559274367589532.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf23_247756559274367589532.c:3:36: warning: initialization discards 'const' qualifier from pointer target type
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_07245883452186410922.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_07245883452186410922.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_07370607094028433419.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_07370607094028433419.c:3:26: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_19058691615963698545.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_19058691615963698545.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_12953236495737712559.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_12953236495737712559.c:3:33: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_28421587864359542529.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_28421587864359542529.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_21243156140212520576.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_21243156140212520576.c:3:35: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_36599473551311698049.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_36599473551311698049.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_32878911632362175049.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_32878911632362175049.c:3:34: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_48270203748528511994.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_48270203748528511994.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_45477759038899604162.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_45477759038899604162.c:3:32: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_58463559772738205235.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_58463559772738205235.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_5178201337403082105.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_5178201337403082105.c:3:26: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_61335060235851460740.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_61335060235851460740.c:4:19: error: incompatible types when assigning to type 'float' from type 'volatile int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_67811650196194128770.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_67811650196194128770.c:3:27: error: incompatible types when initializing type 'float' using type 'volatile int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_71537372543334620231.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_71537372543334620231.c:4:19: error: incompatible types when assigning to type 'double' from type 'volatile int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_76845226831864460447.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_76845226831864460447.c:3:28: error: incompatible types when initializing type 'double' using type 'volatile int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_82356658480973781205.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_82356658480973781205.c:4:19: error: incompatible types when assigning to type 'long double' from type 'volatile int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_85432884875664681068.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_85432884875664681068.c:3:33: error: incompatible types when initializing type 'long double' using type 'volatile int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_95275680514657155885.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_95275680514657155885.c:4:19: warning: assignment discards 'volatile' qualifier from pointer target type
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_97473937950272932451.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_97473937950272932451.c:3:27: warning: initialization discards 'volatile' qualifier from pointer target type
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_101788337702625458440.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_101788337702625458440.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_108167573302116607875.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_108167573302116607875.c:3:28: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_114692926984557228836.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_114692926984557228836.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_11591025770210473070.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_11591025770210473070.c:3:28: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_126912066815946852360.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_126912066815946852360.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_124435721419776180645.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_124435721419776180645.c:3:30: warning: initialization from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_131171688385245543315.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_131171688385245543315.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'volatile int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_138654691892783936075.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_138654691892783936075.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_143682817380801854652.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_143682817380801854652.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'volatile int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_143792603788807567501.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_143792603788807567501.c:5:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_151801644407483411022.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_151801644407483411022.c:6:19: error: incompatible types when assigning to type 'struct_anonymous' from type 'volatile int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_154659901635802651656.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_154659901635802651656.c:5:17: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_161604895016372847844.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_161604895016372847844.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'volatile int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_165752461091743325775.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_165752461091743325775.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_178031595228234353726.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_178031595228234353726.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'volatile int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_176400184438314988673.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_176400184438314988673.c:3:24: error: invalid initializer
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_18238726490710042374.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_18238726490710042374.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_183507141355511910361.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_183507141355511910361.c:3:34: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_19217646209844499189.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_19217646209844499189.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_195599625281288402956.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_195599625281288402956.c:3:31: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_201311019094739081340.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_201311019094739081340.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_208475780020449325378.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_208475780020449325378.c:3:34: error: incompatible types when initializing type 'double' using type 'volatile int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_216674115394353896418.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_216674115394353896418.c:4:19: error: incompatible types when assigning to type 'double' from type 'volatile int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_218442809875796229526.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_218442809875796229526.c:3:37: error: incompatible types when initializing type 'double' using type 'volatile int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_223604521353194474852.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_223604521353194474852.c:4:19: warning: assignment discards 'volatile' qualifier from pointer target type
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_222946446510622562975.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_222946446510622562975.c:3:27: warning: initialization discards 'volatile' qualifier from pointer target type
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_23149081187826797418.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_23149081187826797418.c:4:19: warning: assignment discards 'volatile' qualifier from pointer target type
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
C:\Users\ckaestne\AppData\Local\Temp\conf24_232770169166836945485.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf24_232770169166836945485.c:3:33: warning: initialization discards 'volatile' qualifier from pointer target type
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