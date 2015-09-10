package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.{CDeclUse, CTypeCache, CTypeSystemFrontend}
import org.junit.Test
import org.scalatest.Matchers

class DeadStoreTest extends TestHelper with Matchers with CFGHelper with EnforceTreeHelper {

    def deadstore(code: String): Boolean = {
        val tunit = prepareAST[TranslationUnit](parseTranslationUnit(code))
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

    @Test def test_sqlite_while() {
        deadstore(
        """
          |static int sqlite3FtsUnicodeIsalnum(int c){
          |  const static unsigned int aEntry[] = {
          |    0x07D9140B, 0x07DA0046, 0x07DC0074, 0x38000401, 0x38008060,
          |    0x380400F0,
          |  };
          |  static const unsigned int aAscii[4] = {
          |    0xFFFFFFFF, 0xFC00FFFF, 0xF8000001, 0xF8000001,
          |  };
          |
          |  if( c<128 ){
          |    return ( (aAscii[c >> 5] & (1 << (c & 0x001F)))==0 );
          |  }else if( c<(1<<22) ){
          |    unsigned int key = (((unsigned int)c)<<10) | 0x000003FF;
          |    int iRes;
          |    int iHi = sizeof(aEntry)/sizeof(aEntry[0]) - 1;
          |    int iLo = 0;
          |    while( iHi>=iLo ){
          |      int iTest = (iHi + iLo) / 2;
          |      if( key >= aEntry[iTest] ){
          |        iRes = iTest;
          |        iLo = iTest+1;
          |      }else{
          |        iHi = iTest-1;
          |      }
          |    }
          |    ((void) (0));
          |    ((void) (0));
          |    return (((unsigned int)c) >= ((aEntry[iRes]>>10) + (aEntry[iRes]&0x3FF)));
          |  }
          |  return 1;
          |}
          |
        """.stripMargin
        ) should be(true)
    }
}
