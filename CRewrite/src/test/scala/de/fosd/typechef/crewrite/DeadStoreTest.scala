package de.fosd.typechef.crewrite

import org.junit.Test
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.{CDeclUse, CTypeCache, CTypeSystemFrontend}
import de.fosd.typechef.crewrite.asthelper.EnforceTreeHelper

class DeadStoreTest extends TestHelper with ShouldMatchers with CFGHelper {

    def deadstore(code: String): Boolean = {
        val tunit = EnforceTreeHelper.prepareAST[TranslationUnit](parseTranslationUnit(code))
        val ts = new CTypeSystemFrontend(tunit) with CTypeCache with CDeclUse
        assert(ts.checkASTSilent, "typecheck fails!")
        val df = new CIntraAnalysisFrontend(tunit, ts)
        df.deadStore()
    }

    @Test def test_simple() {
        deadstore( """
              void foo() {
                  int *a;

                  *a = 2;
                  if (!a) {
                  }
              }
                   """.stripMargin) should be(true)
    }

    // inspired by http://en.wikipedia.org/wiki/Dead_store
    @Test def test_while() {
        deadstore(
            """
              void foo(int x, int y) {
                  int z;
                  int i = 300;
                  while (i-- > 0) {
                      z = x + y;  // dead store
                  }
              }
            """.stripMargin) should be(false)
    }

    @Test def test_for() {
        deadstore(
            """
static
void test1(int *code,
        int *length,
        int minLen,
        int maxLen,
        int alphaSize)
{
    int n, vec, i;

    vec = 0;
    for (n = minLen; n <= maxLen; n++) {
        for (i = 0; i < alphaSize; i++)
            ;
        vec <<= 1;
    }
}
            """.stripMargin) should be(true)
    }

    @Test def test_get_header_tar() {
        deadstore(
            """
            void foo(unsigned len) {
              char* p;
              p += len;

              p[-1] = '\0';
            }
            """.stripMargin) should be(false)
    }

    @Test def test_var_optional() {
        deadstore(
            """
            void foo() {
              int a;
              #ifdef A
              a = 0;
              #endif
            }
            """.stripMargin
        ) should be (false)
        deadstore(
            """
            void foo() {
              int a;
              #ifdef A
              a = 0;
              #endif
              #ifdef A
              a;
              #endif
            }
            """.stripMargin
        ) should be (true)
        deadstore(
            """
            void foo() {
              int a;
              #ifdef A
              a = 0;
              #endif
              #ifndef A
              a;
              #endif
            }
            """.stripMargin
        ) should be (false)
    }

    @Test def test_var_alternative() {
        deadstore(
            """
            void foo() {
              int a = 0;
              #ifdef A
              a++;
              a;
              #else
              a--;
              a;
              #endif
            }
            """.stripMargin
        ) should be (true)
    }
}
