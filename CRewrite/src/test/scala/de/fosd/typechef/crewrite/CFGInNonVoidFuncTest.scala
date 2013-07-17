package de.fosd.typechef.crewrite

import org.junit.Test
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._
import scala.Predef._

class CFGInNonVoidFuncTest extends TestHelper with ShouldMatchers with CFGHelper with EnforceTreeHelper {

    def cfgInNonVoidFunc(code: String): Boolean = {
        val tunit = prepareAST[TranslationUnit](parseTranslationUnit(code))
        val cf = new CIntraAnalysisFrontend(tunit)
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

