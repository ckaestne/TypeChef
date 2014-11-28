package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.{CDeclUse, CTypeCache, CTypeSystemFrontend}
import org.junit.Test
import org.scalatest.Matchers

import scala.Predef._

class CFGInNonVoidFuncTest extends TestHelper with Matchers with CFGHelper with EnforceTreeHelper {

    def cfgInNonVoidFunc(code: String): Boolean = {
        val tunit = prepareAST[TranslationUnit](parseTranslationUnit(code))
        val ts = new CTypeSystemFrontend(tunit) with CTypeCache with CDeclUse
        assert(ts.checkASTSilent, "typecheck fails!")
        val cf = new CIntraAnalysisFrontend(tunit, ts)
        cf.cfgInNonVoidFunc()
    }

    @Test def test_cfgInNonVoidFunc() {
        cfgInNonVoidFunc( """
               int f(void) {
                  int a;
                  switch (a) {
                    a = a+1;
                    case 0: a+2;
                    default: a+3;
                  }
               }
        """.stripMargin) should be(false)

        cfgInNonVoidFunc( """
                   void f(void) {
                      int a;
                      switch (a) {
                        case 0: a+2;
                        default: a+3;
                      }
                   }
        """.stripMargin) should be(true)

        cfgInNonVoidFunc( """
                   #ifdef A
                   void
                   #else
                   int
                   #endif
                   f(void) {
                      int a;
                      #ifndef A
                      return a;
                      #endif
                   }
                            """.stripMargin) should be(true)
    }
}

