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
C:\Users\ckaestne\AppData\Local\Temp\conf0_95869283240658161598.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_95869283240658161598.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_94336611994939445333.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_94336611994939445333.c:3:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_105779340523476607770.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_105779340523476607770.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              char foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_105152675706511033467.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_105152675706511033467.c:3:28: warning: initialization makes pointer from integer without a cast
                 long * b = foo();
                            ^

        */
        warning("""
              char foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf0_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_118420788853937784313.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_118420788853937784313.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_1186578865927105232.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_1186578865927105232.c:3:30: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf0_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_12759739026986932434.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_12759739026986932434.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_124655335893883521957.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_124655335893883521957.c:5:24: error: invalid initializer
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


   @Test def test_conf0_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_131498619532386893169.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_131498619532386893169.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_13572852896347349488.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_13572852896347349488.c:5:24: error: invalid initializer
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


   @Test def test_conf0_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_142620335373050649769.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_142620335373050649769.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_144930852796260148063.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_144930852796260148063.c:3:24: error: invalid initializer
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


   @Test def test_conf0_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_153045985769094239935.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_153045985769094239935.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_155178810506530529163.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_155178810506530529163.c:3:24: error: invalid initializer
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


   @Test def test_conf0_16() {
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


   @Test def test_conf0_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_17352181396789256170.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_17352181396789256170.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf0_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_188337562441944336986.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_188337562441944336986.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf0_19() {
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


   @Test def test_conf0_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_20965214110070423090.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_20965214110070423090.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_202386006682732483474.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_202386006682732483474.c:3:27: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf0_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_211626454300066593052.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_211626454300066593052.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_215847764522252152253.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_215847764522252152253.c:3:33: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf0_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf0_222194789853255810086.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_222194789853255810086.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf0_225824944671309465794.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf0_225824944671309465794.c:3:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_96387257008284392721.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_96387257008284392721.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_95842812739831407750.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_95842812739831407750.c:3:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_102493635971780722442.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_102493635971780722442.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              signed char foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_104141053481915512220.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_104141053481915512220.c:3:28: warning: initialization makes pointer from integer without a cast
                 long * b = foo();
                            ^

        */
        warning("""
              signed char foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf1_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_113659719855235520392.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_113659719855235520392.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_118244035811280860365.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_118244035811280860365.c:3:30: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf1_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_122909091266531830407.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_122909091266531830407.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'signed char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_124351070961427200639.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_124351070961427200639.c:5:24: error: invalid initializer
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


   @Test def test_conf1_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_136092231330204820898.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_136092231330204820898.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'signed char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_132706296320539380188.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_132706296320539380188.c:5:24: error: invalid initializer
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


   @Test def test_conf1_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_147551550417906790943.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_147551550417906790943.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'signed char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_147738589036017630965.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_147738589036017630965.c:3:24: error: invalid initializer
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


   @Test def test_conf1_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_156444608138794463867.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_156444608138794463867.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'signed char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_151733921982445073993.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_151733921982445073993.c:3:24: error: invalid initializer
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


   @Test def test_conf1_16() {
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


   @Test def test_conf1_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_171970453972158818464.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_171970453972158818464.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf1_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_187569227848107158060.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_187569227848107158060.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf1_19() {
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


   @Test def test_conf1_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_205271205339158225757.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_205271205339158225757.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_203705246925085349763.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_203705246925085349763.c:3:27: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf1_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_21988276930639536577.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_21988276930639536577.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_211949127137308358903.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_211949127137308358903.c:3:33: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf1_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf1_228160440335054156605.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_228160440335054156605.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf1_221883841397867975443.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf1_221883841397867975443.c:3:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_97213762167562533045.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_97213762167562533045.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_93601329282524959716.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_93601329282524959716.c:3:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_107280374544471647534.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_107280374544471647534.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              unsigned char foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_103078743625296712048.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_103078743625296712048.c:3:28: warning: initialization makes pointer from integer without a cast
                 long * b = foo();
                            ^

        */
        warning("""
              unsigned char foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf2_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_113242033710454975853.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_113242033710454975853.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_111401028029756058574.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_111401028029756058574.c:3:30: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf2_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_124070460083937104519.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_124070460083937104519.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'unsigned char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_123205913253052850549.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_123205913253052850549.c:5:24: error: invalid initializer
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


   @Test def test_conf2_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_137293804816400049474.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_137293804816400049474.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'unsigned char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_133331979592084873287.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_133331979592084873287.c:5:24: error: invalid initializer
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


   @Test def test_conf2_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_145368227047328232131.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_145368227047328232131.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'unsigned char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_146284932782315276732.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_146284932782315276732.c:3:24: error: invalid initializer
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


   @Test def test_conf2_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_152555597790449882084.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_152555597790449882084.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'unsigned char'
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_152474130635865345349.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_152474130635865345349.c:3:24: error: invalid initializer
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


   @Test def test_conf2_16() {
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


   @Test def test_conf2_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_175016605624904750338.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_175016605624904750338.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf2_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_183747271626283518878.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_183747271626283518878.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf2_19() {
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


   @Test def test_conf2_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_202401909413849152439.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_202401909413849152439.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_204165406687175337921.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_204165406687175337921.c:3:27: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf2_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_217685589227449949274.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_217685589227449949274.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_21444351757984303059.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_21444351757984303059.c:3:33: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf2_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf2_222286324477144000585.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_222286324477144000585.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf2_222620118163792675062.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf2_222620118163792675062.c:3:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_94446084825544982711.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_94446084825544982711.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_93686570499774035753.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_93686570499774035753.c:3:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_105750776131060688087.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_105750776131060688087.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              unsigned int foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_101405208665830617565.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_101405208665830617565.c:3:28: warning: initialization makes pointer from integer without a cast
                 long * b = foo();
                            ^

        */
        warning("""
              unsigned int foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf3_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_115364607609675092789.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_115364607609675092789.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_112290353214991685474.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_112290353214991685474.c:3:30: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf3_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_125437402605472209488.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_125437402605472209488.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'unsigned int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_124455972712914625452.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_124455972712914625452.c:5:24: error: invalid initializer
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


   @Test def test_conf3_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_136539471940485836901.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_136539471940485836901.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'unsigned int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_135597181254112891964.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_135597181254112891964.c:5:24: error: invalid initializer
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


   @Test def test_conf3_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_14788082638692772612.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_14788082638692772612.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'unsigned int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_143644088944829036268.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_143644088944829036268.c:3:24: error: invalid initializer
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


   @Test def test_conf3_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_15520675171373639581.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_15520675171373639581.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'unsigned int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_154315065896505361497.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_154315065896505361497.c:3:24: error: invalid initializer
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


   @Test def test_conf3_16() {
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


   @Test def test_conf3_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_17758384574525532098.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_17758384574525532098.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf3_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_182546656391583580126.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_182546656391583580126.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf3_19() {
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


   @Test def test_conf3_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_2045697564433224610.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_2045697564433224610.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_202593824639454485623.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_202593824639454485623.c:3:27: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf3_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_21275002234671294943.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_21275002234671294943.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_215899287955355143893.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_215899287955355143893.c:3:33: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf3_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf3_224609227084762990796.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_224609227084762990796.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf3_22192057515583655534.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf3_22192057515583655534.c:3:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_9257863287085053990.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_9257863287085053990.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_97276614185313659208.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_97276614185313659208.c:3:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_104823920472225567824.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_104823920472225567824.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              signed int foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_105846294714855164492.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_105846294714855164492.c:3:28: warning: initialization makes pointer from integer without a cast
                 long * b = foo();
                            ^

        */
        warning("""
              signed int foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf4_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_114640977724368581007.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_114640977724368581007.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_11427847331501733131.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_11427847331501733131.c:3:30: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf4_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_123153093592907573298.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_123153093592907573298.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_122092708952820287371.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_122092708952820287371.c:5:24: error: invalid initializer
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


   @Test def test_conf4_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_134778230897403640395.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_134778230897403640395.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_138921354961192898462.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_138921354961192898462.c:5:24: error: invalid initializer
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


   @Test def test_conf4_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_148164208945538642813.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_148164208945538642813.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_146527096313857131640.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_146527096313857131640.c:3:24: error: invalid initializer
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


   @Test def test_conf4_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_151255946324287477945.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_151255946324287477945.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_152708875115200996094.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_152708875115200996094.c:3:24: error: invalid initializer
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


   @Test def test_conf4_16() {
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


   @Test def test_conf4_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_172004098477245451162.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_172004098477245451162.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf4_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_188446892813891639218.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_188446892813891639218.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf4_19() {
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


   @Test def test_conf4_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_207223729165065187964.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_207223729165065187964.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_206108916404329587835.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_206108916404329587835.c:3:27: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf4_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_212537073956930225719.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_212537073956930225719.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_218291999911544180824.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_218291999911544180824.c:3:33: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf4_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf4_224747533750597425907.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_224747533750597425907.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf4_22926638360828004287.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf4_22926638360828004287.c:3:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_98913823256054690695.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_98913823256054690695.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_95669245882400505254.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_95669245882400505254.c:3:27: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_101036160927676296740.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_101036160927676296740.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              long foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_107046143998685032367.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_107046143998685032367.c:3:28: warning: initialization makes pointer from integer without a cast
                 long * b = foo();
                            ^

        */
        warning("""
              long foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf5_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_114837453560742033615.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_114837453560742033615.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_117312364950662688958.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_117312364950662688958.c:3:30: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf5_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_12614710339553440027.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_12614710339553440027.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'long int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_122668322461439673270.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_122668322461439673270.c:5:24: error: invalid initializer
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


   @Test def test_conf5_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_134161549980857512595.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_134161549980857512595.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'long int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_135712952522322678920.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_135712952522322678920.c:5:24: error: invalid initializer
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


   @Test def test_conf5_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_145928064471671783607.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_145928064471671783607.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'long int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_14218319203994016470.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_14218319203994016470.c:3:24: error: invalid initializer
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


   @Test def test_conf5_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_155574300205728779412.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_155574300205728779412.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'long int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_151730615158108670486.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_151730615158108670486.c:3:24: error: invalid initializer
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


   @Test def test_conf5_16() {
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


   @Test def test_conf5_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_17517812598170645064.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_17517812598170645064.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf5_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_185705671831656617633.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_185705671831656617633.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf5_19() {
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


   @Test def test_conf5_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_207365662907013844088.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_207365662907013844088.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_205531640261931013472.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_205531640261931013472.c:3:27: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf5_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_211228207271071862499.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_211228207271071862499.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_21419330589864525143.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_21419330589864525143.c:3:33: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf5_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf5_222324419379770995324.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_222324419379770995324.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf5_222132776664815985162.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf5_222132776664815985162.c:3:36: warning: initialization makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_95804949434013598657.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_95804949434013598657.c:4:19: error: incompatible types when assigning to type 'int *' from type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_92731638484098457792.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_92731638484098457792.c:3:27: error: incompatible types when initializing type 'int *' using type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_106294540716270765959.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_106294540716270765959.c:4:19: error: incompatible types when assigning to type 'long int *' from type 'float'
                 b = foo();
                   ^

        */
        error("""
              float foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_10761144635119193599.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_10761144635119193599.c:3:28: error: incompatible types when initializing type 'long int *' using type 'float'
                 long * b = foo();
                            ^

        */
        error("""
              float foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf6_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_118836606680237343815.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_118836606680237343815.c:4:19: error: incompatible types when assigning to type 'double *' from type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_115951209543923794245.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_115951209543923794245.c:3:30: error: incompatible types when initializing type 'double *' using type 'float'
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


   @Test def test_conf6_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_125607103727185379152.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_125607103727185379152.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_128013673497463459040.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_128013673497463459040.c:5:24: error: invalid initializer
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


   @Test def test_conf6_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_131900552847440447455.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_131900552847440447455.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_136920172919288918300.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_136920172919288918300.c:5:24: error: invalid initializer
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


   @Test def test_conf6_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_144717757173947901770.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_144717757173947901770.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_146383854220020321766.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_146383854220020321766.c:3:24: error: invalid initializer
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


   @Test def test_conf6_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_158711205217639386386.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_158711205217639386386.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_158074746073794222739.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_158074746073794222739.c:3:24: error: invalid initializer
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


   @Test def test_conf6_16() {
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


   @Test def test_conf6_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_172885372479639796485.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_172885372479639796485.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf6_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_187949474635515631932.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_187949474635515631932.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf6_19() {
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


   @Test def test_conf6_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_202814688074995962312.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_202814688074995962312.c:4:19: error: incompatible types when assigning to type 'int *' from type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_204452654459768248383.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_204452654459768248383.c:3:27: error: incompatible types when initializing type 'int *' using type 'float'
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


   @Test def test_conf6_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_211807209652594218582.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_211807209652594218582.c:4:19: error: incompatible types when assigning to type 'const int *' from type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_214205034456868478727.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_214205034456868478727.c:3:33: error: incompatible types when initializing type 'const int *' using type 'float'
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


   @Test def test_conf6_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf6_223716764911990281131.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_223716764911990281131.c:4:19: error: incompatible types when assigning to type 'volatile int *' from type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf6_222700317334466011845.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf6_222700317334466011845.c:3:36: error: incompatible types when initializing type 'volatile int *' using type 'float'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_91429978946698538182.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_91429978946698538182.c:4:19: error: incompatible types when assigning to type 'int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_95481367499720431882.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_95481367499720431882.c:3:27: error: incompatible types when initializing type 'int *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_108599229063718909693.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_108599229063718909693.c:4:19: error: incompatible types when assigning to type 'long int *' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              double foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_106819241828127823217.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_106819241828127823217.c:3:28: error: incompatible types when initializing type 'long int *' using type 'double'
                 long * b = foo();
                            ^

        */
        error("""
              double foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf7_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_116982162160970253565.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_116982162160970253565.c:4:19: error: incompatible types when assigning to type 'double *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_116220043241164016971.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_116220043241164016971.c:3:30: error: incompatible types when initializing type 'double *' using type 'double'
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


   @Test def test_conf7_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_128375360115869360885.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_128375360115869360885.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_128720086798834912589.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_128720086798834912589.c:5:24: error: invalid initializer
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


   @Test def test_conf7_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_138921807371082643319.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_138921807371082643319.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_132960384759342601551.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_132960384759342601551.c:5:24: error: invalid initializer
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


   @Test def test_conf7_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_142606356153546219850.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_142606356153546219850.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_141707573067267761501.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_141707573067267761501.c:3:24: error: invalid initializer
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


   @Test def test_conf7_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_154120834954473593238.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_154120834954473593238.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_157228704464844788880.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_157228704464844788880.c:3:24: error: invalid initializer
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


   @Test def test_conf7_16() {
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


   @Test def test_conf7_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_174698410195711651867.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_174698410195711651867.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf7_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_188127374785847202838.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_188127374785847202838.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf7_19() {
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


   @Test def test_conf7_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_203749365868578069611.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_203749365868578069611.c:4:19: error: incompatible types when assigning to type 'int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_202107099642595469897.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_202107099642595469897.c:3:27: error: incompatible types when initializing type 'int *' using type 'double'
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


   @Test def test_conf7_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_216566285140419069193.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_216566285140419069193.c:4:19: error: incompatible types when assigning to type 'const int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_217322541473315514391.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_217322541473315514391.c:3:33: error: incompatible types when initializing type 'const int *' using type 'double'
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


   @Test def test_conf7_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf7_227962413577501788165.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_227962413577501788165.c:4:19: error: incompatible types when assigning to type 'volatile int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf7_227393488509014645083.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf7_227393488509014645083.c:3:36: error: incompatible types when initializing type 'volatile int *' using type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_94973785960199956335.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_94973785960199956335.c:4:19: error: incompatible types when assigning to type 'int *' from type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_97598342469705295150.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_97598342469705295150.c:3:27: error: incompatible types when initializing type 'int *' using type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_108560006003719926763.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_108560006003719926763.c:4:19: error: incompatible types when assigning to type 'long int *' from type 'long double'
                 b = foo();
                   ^

        */
        error("""
              long double foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_103832584026137667879.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_103832584026137667879.c:3:28: error: incompatible types when initializing type 'long int *' using type 'long double'
                 long * b = foo();
                            ^

        */
        error("""
              long double foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf8_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_116541469102404819247.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_116541469102404819247.c:4:19: error: incompatible types when assigning to type 'double *' from type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_112263178010720414129.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_112263178010720414129.c:3:30: error: incompatible types when initializing type 'double *' using type 'long double'
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


   @Test def test_conf8_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_128268171495071591685.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_128268171495071591685.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_124544646510357723673.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_124544646510357723673.c:5:24: error: invalid initializer
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


   @Test def test_conf8_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_133314356605221201130.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_133314356605221201130.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_13828493060221247119.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_13828493060221247119.c:5:24: error: invalid initializer
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


   @Test def test_conf8_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_143395512690532869930.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_143395512690532869930.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_141434850491486320105.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_141434850491486320105.c:3:24: error: invalid initializer
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


   @Test def test_conf8_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_155456236529553246886.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_155456236529553246886.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_153696045700178766784.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_153696045700178766784.c:3:24: error: invalid initializer
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


   @Test def test_conf8_16() {
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


   @Test def test_conf8_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_175663683390924278629.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_175663683390924278629.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf8_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_187611637559239233764.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_187611637559239233764.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf8_19() {
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


   @Test def test_conf8_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_206973071779391629946.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_206973071779391629946.c:4:19: error: incompatible types when assigning to type 'int *' from type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_203536568075533140591.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_203536568075533140591.c:3:27: error: incompatible types when initializing type 'int *' using type 'long double'
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


   @Test def test_conf8_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_218439660821740190736.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_218439660821740190736.c:4:19: error: incompatible types when assigning to type 'const int *' from type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_211212877999723454816.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_211212877999723454816.c:3:33: error: incompatible types when initializing type 'const int *' using type 'long double'
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


   @Test def test_conf8_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf8_223921065820924412161.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_223921065820924412161.c:4:19: error: incompatible types when assigning to type 'volatile int *' from type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf8_228347519146863741154.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf8_228347519146863741154.c:3:36: error: incompatible types when initializing type 'volatile int *' using type 'long double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_09220050369153801987.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_09220050369153801987.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_03499604760371609376.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_03499604760371609376.c:3:26: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_18816579448191209391.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_18816579448191209391.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_11402856097911535965.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_11402856097911535965.c:3:33: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_24286256776121166254.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_24286256776121166254.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_25683650048848368070.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_25683650048848368070.c:3:35: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_34035761554124402118.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_34035761554124402118.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_3802529503681271079.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_3802529503681271079.c:3:34: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_41686645343203417967.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_41686645343203417967.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_4528527825792687280.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_4528527825792687280.c:3:32: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_56905082952915056767.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_56905082952915056767.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_56400404635466208792.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_56400404635466208792.c:3:26: warning: initialization makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_65878105359270759065.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_65878105359270759065.c:4:19: error: incompatible types when assigning to type 'float' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_61738206869434750664.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_61738206869434750664.c:3:27: error: incompatible types when initializing type 'float' using type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_76451527709504260151.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_76451527709504260151.c:4:19: error: incompatible types when assigning to type 'double' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_74728182358321738575.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_74728182358321738575.c:3:28: error: incompatible types when initializing type 'double' using type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_85277060810909758329.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_85277060810909758329.c:4:19: error: incompatible types when assigning to type 'long double' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_88381249328366125622.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_88381249328366125622.c:3:33: error: incompatible types when initializing type 'long double' using type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_105175458075569602364.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_105175458075569602364.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              int * foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_102525700224194194020.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_102525700224194194020.c:3:28: warning: initialization from incompatible pointer type
                 long * b = foo();
                            ^

        */
        warning("""
              int * foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf9_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_115577187361319670531.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_115577187361319670531.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_115380827952697518912.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_115380827952697518912.c:3:30: warning: initialization from incompatible pointer type
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


   @Test def test_conf9_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_124676216280988492925.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_124676216280988492925.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_126489958783730622952.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_126489958783730622952.c:5:24: error: invalid initializer
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


   @Test def test_conf9_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_138124059109257240673.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_138124059109257240673.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_136340204912273394596.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_136340204912273394596.c:5:24: error: invalid initializer
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


   @Test def test_conf9_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_144453083539587221453.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_144453083539587221453.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_143221931619800243833.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_143221931619800243833.c:3:24: error: invalid initializer
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


   @Test def test_conf9_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_157675467505823518200.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_157675467505823518200.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_153400277421234712119.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_153400277421234712119.c:3:24: error: invalid initializer
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


   @Test def test_conf9_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_163889393053836871832.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_163889393053836871832.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_168166759079564930970.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_168166759079564930970.c:3:34: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf9_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_177063905588585909914.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_177063905588585909914.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_174327157483074040149.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_174327157483074040149.c:3:31: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf9_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_185440383359575510388.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_185440383359575510388.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_181614730682970166062.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_181614730682970166062.c:3:34: error: incompatible types when initializing type 'double' using type 'int *'
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


   @Test def test_conf9_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf9_195375954911909357858.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_195375954911909357858.c:4:19: error: incompatible types when assigning to type 'double' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf9_193026991329706896630.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf9_193026991329706896630.c:3:37: error: incompatible types when initializing type 'double' using type 'int *'
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


   @Test def test_conf9_20() {
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


   @Test def test_conf9_21() {
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


   @Test def test_conf9_22() {
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
C:\Users\ckaestne\AppData\Local\Temp\conf10_09182337652892994571.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_09182337652892994571.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              long * foo();
              void main() {
                char b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_06680543635511969410.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_06680543635511969410.c:3:26: warning: initialization makes integer from pointer without a cast
                 char b = foo();
                          ^

        */
        warning("""
              long * foo();
              void main() {
                char b = foo();
              }
                """)
   }


   @Test def test_conf10_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_11901081055695345536.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_11901081055695345536.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              long * foo();
              void main() {
                signed char b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_15236832575986454420.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_15236832575986454420.c:3:33: warning: initialization makes integer from pointer without a cast
                 signed char b = foo();
                                 ^

        */
        warning("""
              long * foo();
              void main() {
                signed char b = foo();
              }
                """)
   }


   @Test def test_conf10_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_22751575701908568378.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_22751575701908568378.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              long * foo();
              void main() {
                unsigned char b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_28627110567139789324.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_28627110567139789324.c:3:35: warning: initialization makes integer from pointer without a cast
                 unsigned char b = foo();
                                   ^

        */
        warning("""
              long * foo();
              void main() {
                unsigned char b = foo();
              }
                """)
   }


   @Test def test_conf10_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_37996879660496584070.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_37996879660496584070.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              long * foo();
              void main() {
                unsigned int b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_31798462515021862863.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_31798462515021862863.c:3:34: warning: initialization makes integer from pointer without a cast
                 unsigned int b = foo();
                                  ^

        */
        warning("""
              long * foo();
              void main() {
                unsigned int b = foo();
              }
                """)
   }


   @Test def test_conf10_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_43313321043709896067.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_43313321043709896067.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              long * foo();
              void main() {
                signed int b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_44055044956859239460.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_44055044956859239460.c:3:32: warning: initialization makes integer from pointer without a cast
                 signed int b = foo();
                                ^

        */
        warning("""
              long * foo();
              void main() {
                signed int b = foo();
              }
                """)
   }


   @Test def test_conf10_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_51827989145738697692.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_51827989145738697692.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              long * foo();
              void main() {
                long b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_57239901611915477478.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_57239901611915477478.c:3:26: warning: initialization makes integer from pointer without a cast
                 long b = foo();
                          ^

        */
        warning("""
              long * foo();
              void main() {
                long b = foo();
              }
                """)
   }


   @Test def test_conf10_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_67706853822857724550.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_67706853822857724550.c:4:19: error: incompatible types when assigning to type 'float' from type 'long int *'
                 b = foo();
                   ^

        */
        error("""
              long * foo();
              void main() {
                float b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_61282457911274562492.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_61282457911274562492.c:3:27: error: incompatible types when initializing type 'float' using type 'long int *'
                 float b = foo();
                           ^

        */
        error("""
              long * foo();
              void main() {
                float b = foo();
              }
                """)
   }


   @Test def test_conf10_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_7420842799403061691.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_7420842799403061691.c:4:19: error: incompatible types when assigning to type 'double' from type 'long int *'
                 b = foo();
                   ^

        */
        error("""
              long * foo();
              void main() {
                double b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_71215583198859465904.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_71215583198859465904.c:3:28: error: incompatible types when initializing type 'double' using type 'long int *'
                 double b = foo();
                            ^

        */
        error("""
              long * foo();
              void main() {
                double b = foo();
              }
                """)
   }


   @Test def test_conf10_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_84911787856383289224.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_84911787856383289224.c:4:19: error: incompatible types when assigning to type 'long double' from type 'long int *'
                 b = foo();
                   ^

        */
        error("""
              long * foo();
              void main() {
                long double b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_87904890397294353527.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_87904890397294353527.c:3:33: error: incompatible types when initializing type 'long double' using type 'long int *'
                 long double b = foo();
                                 ^

        */
        error("""
              long * foo();
              void main() {
                long double b = foo();
              }
                """)
   }


   @Test def test_conf10_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_93139779209236519993.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_93139779209236519993.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              long * foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_9894102481309348.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_9894102481309348.c:3:27: warning: initialization from incompatible pointer type
                 int * b = foo();
                           ^

        */
        warning("""
              long * foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf10_10() {
        correct("""
              long * foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        correct("""
              long * foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf10_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_114595066453982104068.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_114595066453982104068.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              long * foo();
              void main() {
                double * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_112841960958040317515.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_112841960958040317515.c:3:30: warning: initialization from incompatible pointer type
                 double * b = foo();
                              ^

        */
        warning("""
              long * foo();
              void main() {
                double * b = foo();
              }
                """)
   }


   @Test def test_conf10_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_123045319209928072514.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_123045319209928072514.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'long int *'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              long * foo();
              void main() {
                struct S b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_122888512208775090584.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_122888512208775090584.c:5:24: error: invalid initializer
                 struct S b = foo();
                        ^

        */
        error("""
              struct S { int x; int y; };

              long * foo();
              void main() {
                struct S b = foo();
              }
                """)
   }


   @Test def test_conf10_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_138524284934453544217.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_138524284934453544217.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'long int *'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              long * foo();
              void main() {
                struct T b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_1340598349686302024.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_1340598349686302024.c:5:24: error: invalid initializer
                 struct T b = foo();
                        ^

        */
        error("""
              struct T { int x; int y; int z; };

              long * foo();
              void main() {
                struct T b = foo();
              }
                """)
   }


   @Test def test_conf10_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_142439190507926379974.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_142439190507926379974.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'long int *'
                 b = foo();
                   ^

        */
        error("""
              long * foo();
              void main() {
                struct { int a; } b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_142771394262774660107.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_142771394262774660107.c:3:24: error: invalid initializer
                 struct { int a; } b = foo();
                        ^

        */
        error("""
              long * foo();
              void main() {
                struct { int a; } b = foo();
              }
                """)
   }


   @Test def test_conf10_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_154619687810349583001.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_154619687810349583001.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'long int *'
                 b = foo();
                   ^

        */
        error("""
              long * foo();
              void main() {
                struct { float b; } b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_15600487534868579926.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_15600487534868579926.c:3:24: error: invalid initializer
                 struct { float b; } b = foo();
                        ^

        */
        error("""
              long * foo();
              void main() {
                struct { float b; } b = foo();
              }
                """)
   }


   @Test def test_conf10_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_164322709032363685155.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_164322709032363685155.c:4:19: warning: assignment makes integer from pointer without a cast
                 b = foo();
                   ^

        */
        warning("""
              long * foo();
              void main() {
                volatile int b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_16598109597428607087.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_16598109597428607087.c:3:34: warning: initialization makes integer from pointer without a cast
                 volatile int b = foo();
                                  ^

        */
        warning("""
              long * foo();
              void main() {
                volatile int b = foo();
              }
                """)
   }


   @Test def test_conf10_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_175600741890481261303.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_175600741890481261303.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              long * foo();
              void main() {
                const int b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_177666623540803144138.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_177666623540803144138.c:3:31: warning: initialization makes integer from pointer without a cast
                 const int b = foo();
                               ^

        */
        warning("""
              long * foo();
              void main() {
                const int b = foo();
              }
                """)
   }


   @Test def test_conf10_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_186740840728432688198.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_186740840728432688198.c:4:19: error: assignment of read-only variable 'b'
                 b = foo();
                   ^

        */
        error("""
              long * foo();
              void main() {
                const double b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_185370244834144625359.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_185370244834144625359.c:3:34: error: incompatible types when initializing type 'double' using type 'long int *'
                 const double b = foo();
                                  ^

        */
        error("""
              long * foo();
              void main() {
                const double b = foo();
              }
                """)
   }


   @Test def test_conf10_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_192037402092818370356.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_192037402092818370356.c:4:19: error: incompatible types when assigning to type 'double' from type 'long int *'
                 b = foo();
                   ^

        */
        error("""
              long * foo();
              void main() {
                volatile double b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_195493592289905470090.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_195493592289905470090.c:3:37: error: incompatible types when initializing type 'double' using type 'long int *'
                 volatile double b = foo();
                                     ^

        */
        error("""
              long * foo();
              void main() {
                volatile double b = foo();
              }
                """)
   }


   @Test def test_conf10_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_207253964576174046443.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_207253964576174046443.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              long * foo();
              void main() {
                int * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_201883866002282536622.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_201883866002282536622.c:3:27: warning: initialization from incompatible pointer type
                 int * b = foo();
                           ^

        */
        warning("""
              long * foo();
              void main() {
                int * b = foo();
              }
                """)
   }


   @Test def test_conf10_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_215788651341146190892.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_215788651341146190892.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              long * foo();
              void main() {
                const int * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_215985156918535944167.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_215985156918535944167.c:3:33: warning: initialization from incompatible pointer type
                 const int * b = foo();
                                 ^

        */
        warning("""
              long * foo();
              void main() {
                const int * b = foo();
              }
                """)
   }


   @Test def test_conf10_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_224385542132073688476.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_224385542132073688476.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              long * foo();
              void main() {
                volatile int * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf10_225632408517856130527.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf10_225632408517856130527.c:3:36: warning: initialization from incompatible pointer type
                 volatile int * b = foo();
                                    ^

        */
        warning("""
              long * foo();
              void main() {
                volatile int * b = foo();
              }
                """)
   }


   @Test def test_conf11_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_03393634264265201768.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_03393634264265201768.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_01292099778335472204.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_01292099778335472204.c:3:26: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf11_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_1517855200533174858.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_1517855200533174858.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_1773731410957198758.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_1773731410957198758.c:3:33: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf11_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_29012077263774456119.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_29012077263774456119.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_2389696007857370944.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_2389696007857370944.c:3:35: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf11_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_34899120688096449411.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_34899120688096449411.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_31667440721573465684.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_31667440721573465684.c:3:34: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf11_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_4775626739547012987.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_4775626739547012987.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_4770434836544415347.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_4770434836544415347.c:3:32: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf11_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_51747264597833324155.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_51747264597833324155.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_53720987159247555765.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_53720987159247555765.c:3:26: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf11_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_67752725217690386733.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_67752725217690386733.c:4:19: error: incompatible types when assigning to type 'float' from type 'double *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_64045651092380511619.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_64045651092380511619.c:3:27: error: incompatible types when initializing type 'float' using type 'double *'
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


   @Test def test_conf11_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_73040465932928996859.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_73040465932928996859.c:4:19: error: incompatible types when assigning to type 'double' from type 'double *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_71595375204497646795.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_71595375204497646795.c:3:28: error: incompatible types when initializing type 'double' using type 'double *'
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


   @Test def test_conf11_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_87035083913769130780.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_87035083913769130780.c:4:19: error: incompatible types when assigning to type 'long double' from type 'double *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_84340674039648412026.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_84340674039648412026.c:3:33: error: incompatible types when initializing type 'long double' using type 'double *'
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


   @Test def test_conf11_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_94762284705969767948.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_94762284705969767948.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_97556272730132921285.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_97556272730132921285.c:3:27: warning: initialization from incompatible pointer type
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


   @Test def test_conf11_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_104957565571187180741.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_104957565571187180741.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              double * foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_106704844322707953606.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_106704844322707953606.c:3:28: warning: initialization from incompatible pointer type
                 long * b = foo();
                            ^

        */
        warning("""
              double * foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf11_11() {
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


   @Test def test_conf11_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_12113469138179496901.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_12113469138179496901.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'double *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_127409360200824334704.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_127409360200824334704.c:5:24: error: invalid initializer
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


   @Test def test_conf11_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_138215549105953957089.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_138215549105953957089.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'double *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_133459756601374777265.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_133459756601374777265.c:5:24: error: invalid initializer
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


   @Test def test_conf11_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_147540774609130717035.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_147540774609130717035.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_148822492880332533775.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_148822492880332533775.c:3:24: error: invalid initializer
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


   @Test def test_conf11_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_154977717489115403560.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_154977717489115403560.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_155777833466051547115.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_155777833466051547115.c:3:24: error: invalid initializer
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


   @Test def test_conf11_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_169016286072025805038.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_169016286072025805038.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_167615504534349257423.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_167615504534349257423.c:3:34: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf11_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_176262127076337537378.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_176262127076337537378.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_175021516244954987349.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_175021516244954987349.c:3:31: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf11_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_181305153828708260629.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_181305153828708260629.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_186194102605964201252.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_186194102605964201252.c:3:34: error: incompatible types when initializing type 'double' using type 'double *'
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


   @Test def test_conf11_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_193987098929781555827.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_193987098929781555827.c:4:19: error: incompatible types when assigning to type 'double' from type 'double *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_194941402876663261174.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_194941402876663261174.c:3:37: error: incompatible types when initializing type 'double' using type 'double *'
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


   @Test def test_conf11_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_204628424604509628164.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_204628424604509628164.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_208212959227550852329.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_208212959227550852329.c:3:27: warning: initialization from incompatible pointer type
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


   @Test def test_conf11_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_215157674825886587221.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_215157674825886587221.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_217130484520157210004.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_217130484520157210004.c:3:33: warning: initialization from incompatible pointer type
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


   @Test def test_conf11_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf11_227857125973587727265.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_227857125973587727265.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf11_226787926594307039740.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf11_226787926594307039740.c:3:36: warning: initialization from incompatible pointer type
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


   @Test def test_conf12_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_07449019281779535171.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_07449019281779535171.c:6:19: error: incompatible types when assigning to type 'char' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_06993175384716504416.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_06993175384716504416.c:5:26: error: incompatible types when initializing type 'char' using type 'struct S'
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


   @Test def test_conf12_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_11987493686538188919.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_11987493686538188919.c:6:19: error: incompatible types when assigning to type 'signed char' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_16937780525613131468.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_16937780525613131468.c:5:33: error: incompatible types when initializing type 'signed char' using type 'struct S'
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


   @Test def test_conf12_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_27012711626520105657.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_27012711626520105657.c:6:19: error: incompatible types when assigning to type 'unsigned char' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_24100912514374655524.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_24100912514374655524.c:5:35: error: incompatible types when initializing type 'unsigned char' using type 'struct S'
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


   @Test def test_conf12_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_37895371990480503284.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_37895371990480503284.c:6:19: error: incompatible types when assigning to type 'unsigned int' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_37128462734593164690.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_37128462734593164690.c:5:34: error: incompatible types when initializing type 'unsigned int' using type 'struct S'
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


   @Test def test_conf12_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_43249249434153025941.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_43249249434153025941.c:6:19: error: incompatible types when assigning to type 'int' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_45786712158418301605.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_45786712158418301605.c:5:32: error: incompatible types when initializing type 'int' using type 'struct S'
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


   @Test def test_conf12_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_56814212300702319362.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_56814212300702319362.c:6:19: error: incompatible types when assigning to type 'long int' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_56229643783417800283.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_56229643783417800283.c:5:26: error: incompatible types when initializing type 'long int' using type 'struct S'
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


   @Test def test_conf12_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_64963493699276085727.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_64963493699276085727.c:6:19: error: incompatible types when assigning to type 'float' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_6168277067520563973.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_6168277067520563973.c:5:27: error: incompatible types when initializing type 'float' using type 'struct S'
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


   @Test def test_conf12_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_75182712347529104307.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_75182712347529104307.c:6:19: error: incompatible types when assigning to type 'double' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_75048597032213463001.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_75048597032213463001.c:5:28: error: incompatible types when initializing type 'double' using type 'struct S'
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


   @Test def test_conf12_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_81317526531509760086.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_81317526531509760086.c:6:19: error: incompatible types when assigning to type 'long double' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_86912508405085204250.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_86912508405085204250.c:5:33: error: incompatible types when initializing type 'long double' using type 'struct S'
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


   @Test def test_conf12_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_94320713753938205974.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_94320713753938205974.c:6:19: error: incompatible types when assigning to type 'int *' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_92258034411740326078.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_92258034411740326078.c:5:27: error: incompatible types when initializing type 'int *' using type 'struct S'
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


   @Test def test_conf12_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_101708036244421406973.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_101708036244421406973.c:6:19: error: incompatible types when assigning to type 'long int *' from type 'struct S'
                 b = foo();
                   ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_10618969170785606742.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_10618969170785606742.c:5:28: error: incompatible types when initializing type 'long int *' using type 'struct S'
                 long * b = foo();
                            ^

        */
        error("""
              struct S { int x; int y; };

              struct S foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf12_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_112071875890547555272.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_112071875890547555272.c:6:19: error: incompatible types when assigning to type 'double *' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_113659065526935977332.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_113659065526935977332.c:5:30: error: incompatible types when initializing type 'double *' using type 'struct S'
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


   @Test def test_conf12_12() {
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


   @Test def test_conf12_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_133502326628227534508.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_133502326628227534508.c:8:19: error: incompatible types when assigning to type 'struct T' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_13128230744875903382.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_13128230744875903382.c:7:24: error: invalid initializer
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


   @Test def test_conf12_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_14974157776301119089.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_14974157776301119089.c:6:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_145663841192751701147.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_145663841192751701147.c:5:24: error: invalid initializer
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


   @Test def test_conf12_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_152451922503942653582.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_152451922503942653582.c:6:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_158201189522726532529.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_158201189522726532529.c:5:24: error: invalid initializer
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


   @Test def test_conf12_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_165764878100798355695.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_165764878100798355695.c:6:19: error: incompatible types when assigning to type 'int' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_167479115335413532177.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_167479115335413532177.c:5:34: error: incompatible types when initializing type 'int' using type 'struct S'
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


   @Test def test_conf12_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_172420222393702278660.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_172420222393702278660.c:6:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_171753775699739924988.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_171753775699739924988.c:5:31: error: incompatible types when initializing type 'int' using type 'struct S'
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


   @Test def test_conf12_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_188732532864771593118.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_188732532864771593118.c:6:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_187319400948437112736.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_187319400948437112736.c:5:34: error: incompatible types when initializing type 'double' using type 'struct S'
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


   @Test def test_conf12_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_193888994223074136215.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_193888994223074136215.c:6:19: error: incompatible types when assigning to type 'double' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_198624916218899735187.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_198624916218899735187.c:5:37: error: incompatible types when initializing type 'double' using type 'struct S'
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


   @Test def test_conf12_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_203511559548847320985.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_203511559548847320985.c:6:19: error: incompatible types when assigning to type 'int *' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_204533396107085515156.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_204533396107085515156.c:5:27: error: incompatible types when initializing type 'int *' using type 'struct S'
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


   @Test def test_conf12_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_214120949646341074528.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_214120949646341074528.c:6:19: error: incompatible types when assigning to type 'const int *' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_21785070335468170987.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_21785070335468170987.c:5:33: error: incompatible types when initializing type 'const int *' using type 'struct S'
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


   @Test def test_conf12_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf12_221252680492977299625.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_221252680492977299625.c:6:19: error: incompatible types when assigning to type 'volatile int *' from type 'struct S'
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
C:\Users\ckaestne\AppData\Local\Temp\conf12_224275487374359104678.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf12_224275487374359104678.c:5:36: error: incompatible types when initializing type 'volatile int *' using type 'struct S'
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


   @Test def test_conf13_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_05386016791039192879.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_05386016791039192879.c:6:19: error: incompatible types when assigning to type 'char' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_08321791066368740247.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_08321791066368740247.c:5:26: error: incompatible types when initializing type 'char' using type 'struct T'
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


   @Test def test_conf13_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_12471744604617673173.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_12471744604617673173.c:6:19: error: incompatible types when assigning to type 'signed char' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_14550934769026660265.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_14550934769026660265.c:5:33: error: incompatible types when initializing type 'signed char' using type 'struct T'
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


   @Test def test_conf13_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_28918235241314729653.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_28918235241314729653.c:6:19: error: incompatible types when assigning to type 'unsigned char' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_24887762709257307348.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_24887762709257307348.c:5:35: error: incompatible types when initializing type 'unsigned char' using type 'struct T'
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


   @Test def test_conf13_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_34501860339606931567.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_34501860339606931567.c:6:19: error: incompatible types when assigning to type 'unsigned int' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_36463172964585036279.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_36463172964585036279.c:5:34: error: incompatible types when initializing type 'unsigned int' using type 'struct T'
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


   @Test def test_conf13_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_41265704881258627381.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_41265704881258627381.c:6:19: error: incompatible types when assigning to type 'int' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_41372420092626875890.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_41372420092626875890.c:5:32: error: incompatible types when initializing type 'int' using type 'struct T'
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


   @Test def test_conf13_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_5630348743352707879.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_5630348743352707879.c:6:19: error: incompatible types when assigning to type 'long int' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_57045433738883379868.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_57045433738883379868.c:5:26: error: incompatible types when initializing type 'long int' using type 'struct T'
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


   @Test def test_conf13_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_65625984175946148562.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_65625984175946148562.c:6:19: error: incompatible types when assigning to type 'float' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_68350983825734815185.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_68350983825734815185.c:5:27: error: incompatible types when initializing type 'float' using type 'struct T'
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


   @Test def test_conf13_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_76228001051152258175.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_76228001051152258175.c:6:19: error: incompatible types when assigning to type 'double' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_723544322195544321.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_723544322195544321.c:5:28: error: incompatible types when initializing type 'double' using type 'struct T'
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


   @Test def test_conf13_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_83354736646157612682.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_83354736646157612682.c:6:19: error: incompatible types when assigning to type 'long double' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_83000025638001934991.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_83000025638001934991.c:5:33: error: incompatible types when initializing type 'long double' using type 'struct T'
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


   @Test def test_conf13_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_92442391698278751354.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_92442391698278751354.c:6:19: error: incompatible types when assigning to type 'int *' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_93184570455520167836.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_93184570455520167836.c:5:27: error: incompatible types when initializing type 'int *' using type 'struct T'
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


   @Test def test_conf13_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_10694246732575435310.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_10694246732575435310.c:6:19: error: incompatible types when assigning to type 'long int *' from type 'struct T'
                 b = foo();
                   ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_104491371657511628318.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_104491371657511628318.c:5:28: error: incompatible types when initializing type 'long int *' using type 'struct T'
                 long * b = foo();
                            ^

        */
        error("""
              struct T { int x; int y; int z; };

              struct T foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf13_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_116891675059590698367.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_116891675059590698367.c:6:19: error: incompatible types when assigning to type 'double *' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_118847166921526370228.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_118847166921526370228.c:5:30: error: incompatible types when initializing type 'double *' using type 'struct T'
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


   @Test def test_conf13_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_124739051286717796460.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_124739051286717796460.c:8:19: error: incompatible types when assigning to type 'struct S' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_121375780429011114938.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_121375780429011114938.c:7:24: error: invalid initializer
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


   @Test def test_conf13_13() {
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


   @Test def test_conf13_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_142652252380143767119.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_142652252380143767119.c:6:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_144571174540257955757.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_144571174540257955757.c:5:24: error: invalid initializer
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


   @Test def test_conf13_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_157059731442417055400.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_157059731442417055400.c:6:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_154987646056849736581.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_154987646056849736581.c:5:24: error: invalid initializer
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


   @Test def test_conf13_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_165138785365967618317.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_165138785365967618317.c:6:19: error: incompatible types when assigning to type 'int' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_166204738896628185964.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_166204738896628185964.c:5:34: error: incompatible types when initializing type 'int' using type 'struct T'
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


   @Test def test_conf13_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_174644020027861821663.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_174644020027861821663.c:6:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_173994635596289388551.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_173994635596289388551.c:5:31: error: incompatible types when initializing type 'int' using type 'struct T'
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


   @Test def test_conf13_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_182498091996258050146.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_182498091996258050146.c:6:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_188962651226190842910.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_188962651226190842910.c:5:34: error: incompatible types when initializing type 'double' using type 'struct T'
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


   @Test def test_conf13_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_194483695044319388616.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_194483695044319388616.c:6:19: error: incompatible types when assigning to type 'double' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_194432200084544150406.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_194432200084544150406.c:5:37: error: incompatible types when initializing type 'double' using type 'struct T'
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


   @Test def test_conf13_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_205585775818738756372.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_205585775818738756372.c:6:19: error: incompatible types when assigning to type 'int *' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_20969466754815411760.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_20969466754815411760.c:5:27: error: incompatible types when initializing type 'int *' using type 'struct T'
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


   @Test def test_conf13_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_212580194601936804550.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_212580194601936804550.c:6:19: error: incompatible types when assigning to type 'const int *' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_218100145563342899768.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_218100145563342899768.c:5:33: error: incompatible types when initializing type 'const int *' using type 'struct T'
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


   @Test def test_conf13_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf13_228140742256156168538.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_228140742256156168538.c:6:19: error: incompatible types when assigning to type 'volatile int *' from type 'struct T'
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
C:\Users\ckaestne\AppData\Local\Temp\conf13_225125794421376637366.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf13_225125794421376637366.c:5:36: error: incompatible types when initializing type 'volatile int *' using type 'struct T'
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


   @Test def test_conf14_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_02442214881804599498.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_02442214881804599498.c:4:19: error: incompatible types when assigning to type 'char' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_01816079152931955606.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_01816079152931955606.c:3:26: error: incompatible types when initializing type 'char' using type 'struct <anonymous>'
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


   @Test def test_conf14_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_13957135182586278369.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_13957135182586278369.c:4:19: error: incompatible types when assigning to type 'signed char' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_16109010621033938268.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_16109010621033938268.c:3:33: error: incompatible types when initializing type 'signed char' using type 'struct <anonymous>'
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


   @Test def test_conf14_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_25790506422649786252.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_25790506422649786252.c:4:19: error: incompatible types when assigning to type 'unsigned char' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_29136802648849043773.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_29136802648849043773.c:3:35: error: incompatible types when initializing type 'unsigned char' using type 'struct <anonymous>'
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


   @Test def test_conf14_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_33442867376490646151.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_33442867376490646151.c:4:19: error: incompatible types when assigning to type 'unsigned int' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_32446886132598796602.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_32446886132598796602.c:3:34: error: incompatible types when initializing type 'unsigned int' using type 'struct <anonymous>'
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


   @Test def test_conf14_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_47512187276922274439.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_47512187276922274439.c:4:19: error: incompatible types when assigning to type 'int' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_46757139108254139461.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_46757139108254139461.c:3:32: error: incompatible types when initializing type 'int' using type 'struct <anonymous>'
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


   @Test def test_conf14_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_55175555871753614923.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_55175555871753614923.c:4:19: error: incompatible types when assigning to type 'long int' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_55975878462049825511.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_55975878462049825511.c:3:26: error: incompatible types when initializing type 'long int' using type 'struct <anonymous>'
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


   @Test def test_conf14_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_68892039237254091462.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_68892039237254091462.c:4:19: error: incompatible types when assigning to type 'float' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_67866981957118342258.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_67866981957118342258.c:3:27: error: incompatible types when initializing type 'float' using type 'struct <anonymous>'
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


   @Test def test_conf14_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_78856677980040310100.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_78856677980040310100.c:4:19: error: incompatible types when assigning to type 'double' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_7180484320495129101.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_7180484320495129101.c:3:28: error: incompatible types when initializing type 'double' using type 'struct <anonymous>'
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


   @Test def test_conf14_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_84455060399392328076.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_84455060399392328076.c:4:19: error: incompatible types when assigning to type 'long double' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_82717974833716098827.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_82717974833716098827.c:3:33: error: incompatible types when initializing type 'long double' using type 'struct <anonymous>'
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


   @Test def test_conf14_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_91943511926427533472.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_91943511926427533472.c:4:19: error: incompatible types when assigning to type 'int *' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_91634915402634494810.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_91634915402634494810.c:3:27: error: incompatible types when initializing type 'int *' using type 'struct <anonymous>'
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


   @Test def test_conf14_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_102401763464412171463.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_102401763464412171463.c:4:19: error: incompatible types when assigning to type 'long int *' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_107706130474288240920.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_107706130474288240920.c:3:28: error: incompatible types when initializing type 'long int *' using type 'struct <anonymous>'
                 long * b = foo();
                            ^

        */
        error("""
              struct { int a; } foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf14_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_117618506749780437727.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_117618506749780437727.c:4:19: error: incompatible types when assigning to type 'double *' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_116543178053334121644.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_116543178053334121644.c:3:30: error: incompatible types when initializing type 'double *' using type 'struct <anonymous>'
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


   @Test def test_conf14_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_126860582565247148619.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_126860582565247148619.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_123872189913296549667.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_123872189913296549667.c:5:24: error: invalid initializer
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


   @Test def test_conf14_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_138396988269313249729.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_138396988269313249729.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_132670975067571809486.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_132670975067571809486.c:5:24: error: invalid initializer
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


   @Test def test_conf14_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_143959640807681119131.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_143959640807681119131.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_145023062512462801836.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_145023062512462801836.c:3:24: error: invalid initializer
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


   @Test def test_conf14_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_153546190468878451910.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_153546190468878451910.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_158367510318812090705.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_158367510318812090705.c:3:24: error: invalid initializer
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


   @Test def test_conf14_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_167559492194836313135.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_167559492194836313135.c:4:19: error: incompatible types when assigning to type 'int' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_165647403569055398199.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_165647403569055398199.c:3:34: error: incompatible types when initializing type 'int' using type 'struct <anonymous>'
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


   @Test def test_conf14_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_173988556289684363925.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_173988556289684363925.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_175383017625609812715.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_175383017625609812715.c:3:31: error: incompatible types when initializing type 'int' using type 'struct <anonymous>'
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


   @Test def test_conf14_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_184358466226059977681.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_184358466226059977681.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_187891423148549037883.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_187891423148549037883.c:3:34: error: incompatible types when initializing type 'double' using type 'struct <anonymous>'
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


   @Test def test_conf14_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_191931559160958609794.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_191931559160958609794.c:4:19: error: incompatible types when assigning to type 'double' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_197375110451072311561.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_197375110451072311561.c:3:37: error: incompatible types when initializing type 'double' using type 'struct <anonymous>'
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


   @Test def test_conf14_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_208065245251447537978.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_208065245251447537978.c:4:19: error: incompatible types when assigning to type 'int *' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_204969037783896363477.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_204969037783896363477.c:3:27: error: incompatible types when initializing type 'int *' using type 'struct <anonymous>'
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


   @Test def test_conf14_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_215789572571025383570.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_215789572571025383570.c:4:19: error: incompatible types when assigning to type 'const int *' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_211529594670326834760.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_211529594670326834760.c:3:33: error: incompatible types when initializing type 'const int *' using type 'struct <anonymous>'
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


   @Test def test_conf14_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf14_223351202242651753340.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_223351202242651753340.c:4:19: error: incompatible types when assigning to type 'volatile int *' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf14_225613076123935371291.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf14_225613076123935371291.c:3:36: error: incompatible types when initializing type 'volatile int *' using type 'struct <anonymous>'
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


   @Test def test_conf15_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_08758300313679496978.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_08758300313679496978.c:4:19: error: incompatible types when assigning to type 'char' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_03268140153261461015.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_03268140153261461015.c:3:26: error: incompatible types when initializing type 'char' using type 'struct <anonymous>'
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


   @Test def test_conf15_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_1369810825929153575.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_1369810825929153575.c:4:19: error: incompatible types when assigning to type 'signed char' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_14304505208542945285.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_14304505208542945285.c:3:33: error: incompatible types when initializing type 'signed char' using type 'struct <anonymous>'
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


   @Test def test_conf15_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_23840820639783411108.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_23840820639783411108.c:4:19: error: incompatible types when assigning to type 'unsigned char' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_25751243945507817143.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_25751243945507817143.c:3:35: error: incompatible types when initializing type 'unsigned char' using type 'struct <anonymous>'
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


   @Test def test_conf15_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_37470296565146319961.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_37470296565146319961.c:4:19: error: incompatible types when assigning to type 'unsigned int' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_3583375350642247097.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_3583375350642247097.c:3:34: error: incompatible types when initializing type 'unsigned int' using type 'struct <anonymous>'
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


   @Test def test_conf15_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_4998636947369367593.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_4998636947369367593.c:4:19: error: incompatible types when assigning to type 'int' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_44889293632021174792.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_44889293632021174792.c:3:32: error: incompatible types when initializing type 'int' using type 'struct <anonymous>'
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


   @Test def test_conf15_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_55419468593679208132.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_55419468593679208132.c:4:19: error: incompatible types when assigning to type 'long int' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_53928484722420839622.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_53928484722420839622.c:3:26: error: incompatible types when initializing type 'long int' using type 'struct <anonymous>'
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


   @Test def test_conf15_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_61374812624260987188.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_61374812624260987188.c:4:19: error: incompatible types when assigning to type 'float' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_68947902209110751970.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_68947902209110751970.c:3:27: error: incompatible types when initializing type 'float' using type 'struct <anonymous>'
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


   @Test def test_conf15_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_72544823096657336969.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_72544823096657336969.c:4:19: error: incompatible types when assigning to type 'double' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_78352778281621166076.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_78352778281621166076.c:3:28: error: incompatible types when initializing type 'double' using type 'struct <anonymous>'
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


   @Test def test_conf15_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_897997822114067459.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_897997822114067459.c:4:19: error: incompatible types when assigning to type 'long double' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_81213632882806644317.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_81213632882806644317.c:3:33: error: incompatible types when initializing type 'long double' using type 'struct <anonymous>'
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


   @Test def test_conf15_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_95168087412620087799.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_95168087412620087799.c:4:19: error: incompatible types when assigning to type 'int *' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_93133940376457409482.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_93133940376457409482.c:3:27: error: incompatible types when initializing type 'int *' using type 'struct <anonymous>'
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


   @Test def test_conf15_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_105218172760205693176.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_105218172760205693176.c:4:19: error: incompatible types when assigning to type 'long int *' from type 'struct <anonymous>'
                 b = foo();
                   ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_104712482851270081699.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_104712482851270081699.c:3:28: error: incompatible types when initializing type 'long int *' using type 'struct <anonymous>'
                 long * b = foo();
                            ^

        */
        error("""
              struct { float b; } foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf15_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_113808835374559172098.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_113808835374559172098.c:4:19: error: incompatible types when assigning to type 'double *' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_116713273868425437068.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_116713273868425437068.c:3:30: error: incompatible types when initializing type 'double *' using type 'struct <anonymous>'
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


   @Test def test_conf15_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_12817367464027488507.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_12817367464027488507.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_122993315761796388008.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_122993315761796388008.c:5:24: error: invalid initializer
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


   @Test def test_conf15_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_138646387072740047297.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_138646387072740047297.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_134418421534807769446.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_134418421534807769446.c:5:24: error: invalid initializer
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


   @Test def test_conf15_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_144092914299962454034.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_144092914299962454034.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_145165468749288190700.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_145165468749288190700.c:3:24: error: invalid initializer
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


   @Test def test_conf15_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_156000175721586361363.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_156000175721586361363.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_154626252928171311429.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_154626252928171311429.c:3:24: error: invalid initializer
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


   @Test def test_conf15_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_168283710828993640975.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_168283710828993640975.c:4:19: error: incompatible types when assigning to type 'int' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_162789882708471776967.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_162789882708471776967.c:3:34: error: incompatible types when initializing type 'int' using type 'struct <anonymous>'
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


   @Test def test_conf15_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_176281899575622834410.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_176281899575622834410.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_173057307137560381852.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_173057307137560381852.c:3:31: error: incompatible types when initializing type 'int' using type 'struct <anonymous>'
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


   @Test def test_conf15_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_188190924102522710870.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_188190924102522710870.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_184256218952140334332.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_184256218952140334332.c:3:34: error: incompatible types when initializing type 'double' using type 'struct <anonymous>'
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


   @Test def test_conf15_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_194157813700545928099.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_194157813700545928099.c:4:19: error: incompatible types when assigning to type 'double' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_191481722039842190601.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_191481722039842190601.c:3:37: error: incompatible types when initializing type 'double' using type 'struct <anonymous>'
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


   @Test def test_conf15_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_206922708704139440412.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_206922708704139440412.c:4:19: error: incompatible types when assigning to type 'int *' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_203237787486284623396.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_203237787486284623396.c:3:27: error: incompatible types when initializing type 'int *' using type 'struct <anonymous>'
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


   @Test def test_conf15_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_212284531342998694293.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_212284531342998694293.c:4:19: error: incompatible types when assigning to type 'const int *' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_213356086642937445809.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_213356086642937445809.c:3:33: error: incompatible types when initializing type 'const int *' using type 'struct <anonymous>'
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


   @Test def test_conf15_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf15_222626228133320667377.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_222626228133320667377.c:4:19: error: incompatible types when assigning to type 'volatile int *' from type 'struct <anonymous>'
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
C:\Users\ckaestne\AppData\Local\Temp\conf15_22162277773711820962.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf15_22162277773711820962.c:3:36: error: incompatible types when initializing type 'volatile int *' using type 'struct <anonymous>'
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


   @Test def test_conf16_0() {
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


   @Test def test_conf16_1() {
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


   @Test def test_conf16_2() {
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


   @Test def test_conf16_3() {
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


   @Test def test_conf16_4() {
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


   @Test def test_conf16_5() {
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


   @Test def test_conf16_6() {
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


   @Test def test_conf16_7() {
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


   @Test def test_conf16_8() {
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


   @Test def test_conf16_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_97684502744484521998.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_97684502744484521998.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_94310133075183749222.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_94310133075183749222.c:3:27: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf16_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_10992225756810877952.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_10992225756810877952.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              volatile int foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_104961766445727917762.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_104961766445727917762.c:3:28: warning: initialization makes pointer from integer without a cast
                 long * b = foo();
                            ^

        */
        warning("""
              volatile int foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf16_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_114493894948501883908.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_114493894948501883908.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_11987790908648891801.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_11987790908648891801.c:3:30: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf16_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_126500784691743380915.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_126500784691743380915.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_123491097443672035060.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_123491097443672035060.c:5:24: error: invalid initializer
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


   @Test def test_conf16_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_137979059506853014470.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_137979059506853014470.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_131271084663897252658.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_131271084663897252658.c:5:24: error: invalid initializer
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


   @Test def test_conf16_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_144310954968693479434.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_144310954968693479434.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_147097114451679662929.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_147097114451679662929.c:3:24: error: invalid initializer
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


   @Test def test_conf16_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_152398713858702279225.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_152398713858702279225.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_15725618853308566208.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_15725618853308566208.c:3:24: error: invalid initializer
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


   @Test def test_conf16_16() {
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


   @Test def test_conf16_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_178903346169403455505.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_178903346169403455505.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf16_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_181453173227113355241.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_181453173227113355241.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf16_19() {
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


   @Test def test_conf16_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_206199829146489133150.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_206199829146489133150.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_203769564351164953070.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_203769564351164953070.c:3:27: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf16_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_21465131310161099952.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_21465131310161099952.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_216527391377401492276.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_216527391377401492276.c:3:33: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf16_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf16_22413820352925858236.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_22413820352925858236.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf16_224619179544072705749.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf16_224619179544072705749.c:3:36: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf17_0() {
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


   @Test def test_conf17_1() {
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


   @Test def test_conf17_2() {
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


   @Test def test_conf17_3() {
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


   @Test def test_conf17_4() {
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


   @Test def test_conf17_5() {
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


   @Test def test_conf17_6() {
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


   @Test def test_conf17_7() {
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


   @Test def test_conf17_8() {
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


   @Test def test_conf17_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_98269874895479394760.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_98269874895479394760.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_99180652472424710790.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_99180652472424710790.c:3:27: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf17_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_107756245270953636593.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_107756245270953636593.c:4:19: warning: assignment makes pointer from integer without a cast
                 b = foo();
                   ^

        */
        warning("""
              const int foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_104269078617920197646.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_104269078617920197646.c:3:28: warning: initialization makes pointer from integer without a cast
                 long * b = foo();
                            ^

        */
        warning("""
              const int foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf17_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_112553041169046960341.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_112553041169046960341.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_116009449491389552552.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_116009449491389552552.c:3:30: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf17_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_128165772635213036941.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_128165772635213036941.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_122030566959649773144.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_122030566959649773144.c:5:24: error: invalid initializer
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


   @Test def test_conf17_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_134656093141091890160.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_134656093141091890160.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_133954252284706699169.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_133954252284706699169.c:5:24: error: invalid initializer
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


   @Test def test_conf17_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_143105697751536774157.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_143105697751536774157.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_148399386698393650755.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_148399386698393650755.c:3:24: error: invalid initializer
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


   @Test def test_conf17_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_156102601615807133048.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_156102601615807133048.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int'
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_153370242048064052925.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_153370242048064052925.c:3:24: error: invalid initializer
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


   @Test def test_conf17_16() {
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


   @Test def test_conf17_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_1728754207617732716.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_1728754207617732716.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf17_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_187737732343691235667.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_187737732343691235667.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf17_19() {
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


   @Test def test_conf17_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_2043506482520876933.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_2043506482520876933.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_201880118489530307824.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_201880118489530307824.c:3:27: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf17_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_216646799800247249101.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_216646799800247249101.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_213628077078795255675.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_213628077078795255675.c:3:33: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf17_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf17_222722787315322699881.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_222722787315322699881.c:4:19: warning: assignment makes pointer from integer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf17_223067472988217820516.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf17_223067472988217820516.c:3:36: warning: initialization makes pointer from integer without a cast
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


   @Test def test_conf18_0() {
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


   @Test def test_conf18_1() {
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


   @Test def test_conf18_2() {
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


   @Test def test_conf18_3() {
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


   @Test def test_conf18_4() {
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


   @Test def test_conf18_5() {
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


   @Test def test_conf18_6() {
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


   @Test def test_conf18_7() {
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


   @Test def test_conf18_8() {
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


   @Test def test_conf18_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_95776267186743018237.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_95776267186743018237.c:4:19: error: incompatible types when assigning to type 'int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_92862854596016269850.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_92862854596016269850.c:3:27: error: incompatible types when initializing type 'int *' using type 'double'
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


   @Test def test_conf18_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_103350425707536838712.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_103350425707536838712.c:4:19: error: incompatible types when assigning to type 'long int *' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              const double foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_103039056890059926120.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_103039056890059926120.c:3:28: error: incompatible types when initializing type 'long int *' using type 'double'
                 long * b = foo();
                            ^

        */
        error("""
              const double foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf18_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_112546301959626954870.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_112546301959626954870.c:4:19: error: incompatible types when assigning to type 'double *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_11895689332005724324.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_11895689332005724324.c:3:30: error: incompatible types when initializing type 'double *' using type 'double'
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


   @Test def test_conf18_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_129123257108968303539.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_129123257108968303539.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_124527396691980903834.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_124527396691980903834.c:5:24: error: invalid initializer
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


   @Test def test_conf18_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_134520560027691035204.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_134520560027691035204.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_137867619642068119588.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_137867619642068119588.c:5:24: error: invalid initializer
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


   @Test def test_conf18_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_14514237104359910677.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_14514237104359910677.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_145075964540427120560.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_145075964540427120560.c:3:24: error: invalid initializer
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


   @Test def test_conf18_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_156295898804593182795.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_156295898804593182795.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_151489177366008113131.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_151489177366008113131.c:3:24: error: invalid initializer
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


   @Test def test_conf18_16() {
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


   @Test def test_conf18_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_176619825222380466655.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_176619825222380466655.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf18_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_185394532140713691960.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_185394532140713691960.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf18_19() {
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


   @Test def test_conf18_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_2041981564883967120.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_2041981564883967120.c:4:19: error: incompatible types when assigning to type 'int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_208659236508699517132.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_208659236508699517132.c:3:27: error: incompatible types when initializing type 'int *' using type 'double'
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


   @Test def test_conf18_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_211196979938952023268.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_211196979938952023268.c:4:19: error: incompatible types when assigning to type 'const int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_216221315935559467822.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_216221315935559467822.c:3:33: error: incompatible types when initializing type 'const int *' using type 'double'
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


   @Test def test_conf18_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf18_228092139948479243204.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_228092139948479243204.c:4:19: error: incompatible types when assigning to type 'volatile int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf18_221094386199829196825.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf18_221094386199829196825.c:3:36: error: incompatible types when initializing type 'volatile int *' using type 'double'
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


   @Test def test_conf19_0() {
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


   @Test def test_conf19_1() {
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


   @Test def test_conf19_2() {
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


   @Test def test_conf19_3() {
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


   @Test def test_conf19_4() {
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


   @Test def test_conf19_5() {
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


   @Test def test_conf19_6() {
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


   @Test def test_conf19_7() {
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


   @Test def test_conf19_8() {
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


   @Test def test_conf19_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_99144020692315116792.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_99144020692315116792.c:4:19: error: incompatible types when assigning to type 'int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_93636885072963700416.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_93636885072963700416.c:3:27: error: incompatible types when initializing type 'int *' using type 'double'
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


   @Test def test_conf19_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_107072858847742590503.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_107072858847742590503.c:4:19: error: incompatible types when assigning to type 'long int *' from type 'double'
                 b = foo();
                   ^

        */
        error("""
              volatile double foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_107977153479905027653.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_107977153479905027653.c:3:28: error: incompatible types when initializing type 'long int *' using type 'double'
                 long * b = foo();
                            ^

        */
        error("""
              volatile double foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf19_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_117291064420950090844.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_117291064420950090844.c:4:19: error: incompatible types when assigning to type 'double *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_111773466613352022646.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_111773466613352022646.c:3:30: error: incompatible types when initializing type 'double *' using type 'double'
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


   @Test def test_conf19_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_121802966291766170648.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_121802966291766170648.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_124229277968129372390.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_124229277968129372390.c:5:24: error: invalid initializer
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


   @Test def test_conf19_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_134863817491335873205.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_134863817491335873205.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_135327932665502165776.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_135327932665502165776.c:5:24: error: invalid initializer
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


   @Test def test_conf19_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_14604289949936057291.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_14604289949936057291.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_14865521022492531116.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_14865521022492531116.c:3:24: error: invalid initializer
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


   @Test def test_conf19_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_158978795312428768612.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_158978795312428768612.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_151123137191617166605.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_151123137191617166605.c:3:24: error: invalid initializer
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


   @Test def test_conf19_16() {
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


   @Test def test_conf19_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_173603293737200992572.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_173603293737200992572.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf19_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_18977620813201686286.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_18977620813201686286.c:4:19: error: assignment of read-only variable 'b'
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


   @Test def test_conf19_19() {
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


   @Test def test_conf19_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_208815136872195989036.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_208815136872195989036.c:4:19: error: incompatible types when assigning to type 'int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_202170605244494268362.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_202170605244494268362.c:3:27: error: incompatible types when initializing type 'int *' using type 'double'
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


   @Test def test_conf19_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_21682146253777456580.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_21682146253777456580.c:4:19: error: incompatible types when assigning to type 'const int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_213635525834165541786.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_213635525834165541786.c:3:33: error: incompatible types when initializing type 'const int *' using type 'double'
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


   @Test def test_conf19_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf19_225433329034752661392.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_225433329034752661392.c:4:19: error: incompatible types when assigning to type 'volatile int *' from type 'double'
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
C:\Users\ckaestne\AppData\Local\Temp\conf19_228208624533193309528.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf19_228208624533193309528.c:3:36: error: incompatible types when initializing type 'volatile int *' using type 'double'
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


   @Test def test_conf20_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_07479075964519970612.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_07479075964519970612.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_0858193351216356891.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_0858193351216356891.c:3:26: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf20_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_167334980486551280.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_167334980486551280.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_16423497384104685912.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_16423497384104685912.c:3:33: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf20_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_21995696128978777692.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_21995696128978777692.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_21921911239210944269.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_21921911239210944269.c:3:35: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf20_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_32022262109586786540.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_32022262109586786540.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_35326679109634357562.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_35326679109634357562.c:3:34: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf20_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_42043250736066814723.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_42043250736066814723.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_43196147806083307934.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_43196147806083307934.c:3:32: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf20_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_51315860700779124499.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_51315860700779124499.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_58887063432996297781.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_58887063432996297781.c:3:26: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf20_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_67808661149718198753.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_67808661149718198753.c:4:19: error: incompatible types when assigning to type 'float' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_64239273147151576536.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_64239273147151576536.c:3:27: error: incompatible types when initializing type 'float' using type 'int *'
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


   @Test def test_conf20_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_738252437496410942.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_738252437496410942.c:4:19: error: incompatible types when assigning to type 'double' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_75365353646508489019.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_75365353646508489019.c:3:28: error: incompatible types when initializing type 'double' using type 'int *'
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


   @Test def test_conf20_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_82615825527235873076.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_82615825527235873076.c:4:19: error: incompatible types when assigning to type 'long double' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_8944554884262085866.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_8944554884262085866.c:3:33: error: incompatible types when initializing type 'long double' using type 'int *'
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


   @Test def test_conf20_9() {
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


   @Test def test_conf20_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_101754783234800022310.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_101754783234800022310.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              int * foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_10489501205430622018.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_10489501205430622018.c:3:28: warning: initialization from incompatible pointer type
                 long * b = foo();
                            ^

        */
        warning("""
              int * foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf20_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_112381813580166411426.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_112381813580166411426.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_115110460568086446103.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_115110460568086446103.c:3:30: warning: initialization from incompatible pointer type
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


   @Test def test_conf20_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_1252766240572626581.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_1252766240572626581.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_121004295480652604037.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_121004295480652604037.c:5:24: error: invalid initializer
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


   @Test def test_conf20_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_13984381990563951931.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_13984381990563951931.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_131463363217117773710.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_131463363217117773710.c:5:24: error: invalid initializer
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


   @Test def test_conf20_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_145717041835381321511.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_145717041835381321511.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_14396890610163679360.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_14396890610163679360.c:3:24: error: invalid initializer
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


   @Test def test_conf20_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_153829762157561900786.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_153829762157561900786.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_15933806791849189380.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_15933806791849189380.c:3:24: error: invalid initializer
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


   @Test def test_conf20_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_166625283700749106983.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_166625283700749106983.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_162845104168588906460.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_162845104168588906460.c:3:34: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf20_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_171695421846681172734.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_171695421846681172734.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_172769493913539758634.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_172769493913539758634.c:3:31: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf20_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_185577550834035441471.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_185577550834035441471.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_186149753337800307453.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_186149753337800307453.c:3:34: error: incompatible types when initializing type 'double' using type 'int *'
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


   @Test def test_conf20_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf20_199155609492446540250.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_199155609492446540250.c:4:19: error: incompatible types when assigning to type 'double' from type 'int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf20_19442569798682901247.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf20_19442569798682901247.c:3:37: error: incompatible types when initializing type 'double' using type 'int *'
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


   @Test def test_conf20_20() {
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


   @Test def test_conf20_21() {
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


   @Test def test_conf20_22() {
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


   @Test def test_conf21_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_02968626759836066277.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_02968626759836066277.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_04246180209193734130.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_04246180209193734130.c:3:26: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf21_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_12372417466256997603.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_12372417466256997603.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_17156191668614700375.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_17156191668614700375.c:3:33: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf21_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_2478500452367885693.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_2478500452367885693.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_24901541665369000990.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_24901541665369000990.c:3:35: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf21_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_31169814487485903878.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_31169814487485903878.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_31726506102968508161.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_31726506102968508161.c:3:34: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf21_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_4179712095949008762.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_4179712095949008762.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_45886200774296484400.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_45886200774296484400.c:3:32: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf21_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_53234408358893264001.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_53234408358893264001.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_54476584003085310343.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_54476584003085310343.c:3:26: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf21_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_61568681170078957212.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_61568681170078957212.c:4:19: error: incompatible types when assigning to type 'float' from type 'const int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_65385878689894816770.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_65385878689894816770.c:3:27: error: incompatible types when initializing type 'float' using type 'const int *'
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


   @Test def test_conf21_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_77125249415182921279.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_77125249415182921279.c:4:19: error: incompatible types when assigning to type 'double' from type 'const int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_75543388755727279845.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_75543388755727279845.c:3:28: error: incompatible types when initializing type 'double' using type 'const int *'
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


   @Test def test_conf21_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_88418693449019432415.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_88418693449019432415.c:4:19: error: incompatible types when assigning to type 'long double' from type 'const int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_81615246770545713524.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_81615246770545713524.c:3:33: error: incompatible types when initializing type 'long double' using type 'const int *'
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


   @Test def test_conf21_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_91022252494085445987.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_91022252494085445987.c:4:19: warning: assignment discards 'const' qualifier from pointer target type
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_91034378553702880240.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_91034378553702880240.c:3:27: warning: initialization discards 'const' qualifier from pointer target type
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


   @Test def test_conf21_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_101405695172827912710.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_101405695172827912710.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              const int * foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_108716022785310084738.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_108716022785310084738.c:3:28: warning: initialization from incompatible pointer type
                 long * b = foo();
                            ^

        */
        warning("""
              const int * foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf21_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_11911750316173886929.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_11911750316173886929.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_115328511635416334897.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_115328511635416334897.c:3:30: warning: initialization from incompatible pointer type
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


   @Test def test_conf21_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_125737054711060358498.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_125737054711060358498.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'const int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_12615285427702303176.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_12615285427702303176.c:5:24: error: invalid initializer
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


   @Test def test_conf21_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_132094449234650471014.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_132094449234650471014.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'const int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_135829929825741781662.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_135829929825741781662.c:5:24: error: invalid initializer
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


   @Test def test_conf21_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_141851236597380438515.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_141851236597380438515.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'const int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_142982416259263207520.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_142982416259263207520.c:3:24: error: invalid initializer
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


   @Test def test_conf21_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_155332312100310561105.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_155332312100310561105.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'const int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_156628260755971382353.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_156628260755971382353.c:3:24: error: invalid initializer
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


   @Test def test_conf21_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_164221950310919767850.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_164221950310919767850.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_168318072711016780721.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_168318072711016780721.c:3:34: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf21_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_174640220224586363280.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_174640220224586363280.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_175619516406322766129.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_175619516406322766129.c:3:31: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf21_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_187073920168826609937.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_187073920168826609937.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_182434755455504413867.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_182434755455504413867.c:3:34: error: incompatible types when initializing type 'double' using type 'const int *'
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


   @Test def test_conf21_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_197980284242119387015.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_197980284242119387015.c:4:19: error: incompatible types when assigning to type 'double' from type 'const int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_198053036087806785822.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_198053036087806785822.c:3:37: error: incompatible types when initializing type 'double' using type 'const int *'
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


   @Test def test_conf21_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_202653238700828855642.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_202653238700828855642.c:4:19: warning: assignment discards 'const' qualifier from pointer target type
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_201421602601196424422.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_201421602601196424422.c:3:27: warning: initialization discards 'const' qualifier from pointer target type
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


   @Test def test_conf21_21() {
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


   @Test def test_conf21_22() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf21_225523513604391552580.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_225523513604391552580.c:4:19: warning: assignment discards 'const' qualifier from pointer target type
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
C:\Users\ckaestne\AppData\Local\Temp\conf21_223525573475579038859.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf21_223525573475579038859.c:3:36: warning: initialization discards 'const' qualifier from pointer target type
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


   @Test def test_conf22_0() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_03395738963601494684.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_03395738963601494684.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_03112247528147934661.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_03112247528147934661.c:3:26: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf22_1() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_1604710658798167320.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_1604710658798167320.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_12950620028958823173.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_12950620028958823173.c:3:33: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf22_2() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_27846629586142480328.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_27846629586142480328.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_27816313780028684723.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_27816313780028684723.c:3:35: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf22_3() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_3594156327291320287.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_3594156327291320287.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_39115184778042708928.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_39115184778042708928.c:3:34: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf22_4() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_48274870452279227436.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_48274870452279227436.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_44773440804540662428.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_44773440804540662428.c:3:32: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf22_5() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_55792366521369558630.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_55792366521369558630.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_5737743943720376813.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_5737743943720376813.c:3:26: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf22_6() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_65822196399801405819.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_65822196399801405819.c:4:19: error: incompatible types when assigning to type 'float' from type 'volatile int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_62111219117322949448.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_62111219117322949448.c:3:27: error: incompatible types when initializing type 'float' using type 'volatile int *'
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


   @Test def test_conf22_7() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_78702932940974508538.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_78702932940974508538.c:4:19: error: incompatible types when assigning to type 'double' from type 'volatile int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_73000521661750818309.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_73000521661750818309.c:3:28: error: incompatible types when initializing type 'double' using type 'volatile int *'
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


   @Test def test_conf22_8() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_88809884267436464556.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_88809884267436464556.c:4:19: error: incompatible types when assigning to type 'long double' from type 'volatile int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_8723880337973494850.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_8723880337973494850.c:3:33: error: incompatible types when initializing type 'long double' using type 'volatile int *'
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


   @Test def test_conf22_9() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_92682702780061824477.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_92682702780061824477.c:4:19: warning: assignment discards 'volatile' qualifier from pointer target type
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_95452902725229818715.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_95452902725229818715.c:3:27: warning: initialization discards 'volatile' qualifier from pointer target type
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


   @Test def test_conf22_10() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_104259541326989312805.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_104259541326989312805.c:4:19: warning: assignment from incompatible pointer type
                 b = foo();
                   ^

        */
        warning("""
              volatile int * foo();
              void main() {
                long * b;
                b = foo();
              }
                """)
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_108452572438250205140.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_108452572438250205140.c:3:28: warning: initialization from incompatible pointer type
                 long * b = foo();
                            ^

        */
        warning("""
              volatile int * foo();
              void main() {
                long * b = foo();
              }
                """)
   }


   @Test def test_conf22_11() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_111186969880350908237.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_111186969880350908237.c:4:19: warning: assignment from incompatible pointer type
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_114955202449445727889.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_114955202449445727889.c:3:30: warning: initialization from incompatible pointer type
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


   @Test def test_conf22_12() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_124017517278765202770.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_124017517278765202770.c:6:19: error: incompatible types when assigning to type 'struct S' from type 'volatile int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_124957997843488254542.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_124957997843488254542.c:5:24: error: invalid initializer
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


   @Test def test_conf22_13() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_137993388066197492784.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_137993388066197492784.c:6:19: error: incompatible types when assigning to type 'struct T' from type 'volatile int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_135989234289763134085.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_135989234289763134085.c:5:24: error: invalid initializer
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


   @Test def test_conf22_14() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_148640886423827030505.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_148640886423827030505.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'volatile int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_141069533608752846112.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_141069533608752846112.c:3:24: error: invalid initializer
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


   @Test def test_conf22_15() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_152457023743704883899.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_152457023743704883899.c:4:19: error: incompatible types when assigning to type 'struct <anonymous>' from type 'volatile int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_158320783479248131415.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_158320783479248131415.c:3:24: error: invalid initializer
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


   @Test def test_conf22_16() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_162273371315184523786.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_162273371315184523786.c:4:19: warning: assignment makes integer from pointer without a cast
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_162685231150612929401.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_162685231150612929401.c:3:34: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf22_17() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_175262773739921614984.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_175262773739921614984.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_177930446584899438431.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_177930446584899438431.c:3:31: warning: initialization makes integer from pointer without a cast
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


   @Test def test_conf22_18() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_181702063909105265563.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_181702063909105265563.c:4:19: error: assignment of read-only variable 'b'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_184368619318702821881.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_184368619318702821881.c:3:34: error: incompatible types when initializing type 'double' using type 'volatile int *'
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


   @Test def test_conf22_19() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_198405353261297865742.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_198405353261297865742.c:4:19: error: incompatible types when assigning to type 'double' from type 'volatile int *'
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_192179999064361182745.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_192179999064361182745.c:3:37: error: incompatible types when initializing type 'double' using type 'volatile int *'
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


   @Test def test_conf22_20() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_204578652318200938973.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_204578652318200938973.c:4:19: warning: assignment discards 'volatile' qualifier from pointer target type
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_203015304669393087619.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_203015304669393087619.c:3:27: warning: initialization discards 'volatile' qualifier from pointer target type
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


   @Test def test_conf22_21() {
        /* gcc reports:
C:\Users\ckaestne\AppData\Local\Temp\conf22_216096483967415861545.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_216096483967415861545.c:4:19: warning: assignment discards 'volatile' qualifier from pointer target type
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
C:\Users\ckaestne\AppData\Local\Temp\conf22_212370827265829380686.c: In function 'main':
C:\Users\ckaestne\AppData\Local\Temp\conf22_212370827265829380686.c:3:33: warning: initialization discards 'volatile' qualifier from pointer target type
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


   @Test def test_conf22_22() {
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