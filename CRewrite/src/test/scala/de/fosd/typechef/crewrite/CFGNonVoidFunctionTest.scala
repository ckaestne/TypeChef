package de.fosd.typechef.crewrite

import org.junit.Test
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._
import scala.Predef._

class CFGNonVoidFunctionTest extends TestHelper with ShouldMatchers with CFGHelper with EnforceTreeHelper {

    def cfgNonVoidFunction(code: String): Boolean = {
        val tunit = prepareAST[TranslationUnit](parseTranslationUnit(code))
        val cf = new CAnalysisFrontend(tunit)
        cf.cfgNonVoidFunction()
    }

    @Test def test_cfgNonVoidFunction() {
        cfgNonVoidFunction( """
               int f(void) {
                  int a;
                  switch (a) {
                    a = a+1;
                    case 0: a+2;
                    default: a+3;
                  }
               }
        """.stripMargin) should be(false)

        cfgNonVoidFunction( """
                   void f(void) {
                      int a;
                      switch (a) {
                        case 0: a+2;
                        default: a+3;
                      }
                   }
        """.stripMargin) should be(true)

        cfgNonVoidFunction( """
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

