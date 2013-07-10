package de.fosd.typechef.crewrite

import org.junit.Test
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.{CDeclUse, CTypeSystemFrontend}
import scala.Predef._

class DanglingSwitchCodeTest extends TestHelper with ShouldMatchers with CFGHelper with EnforceTreeHelper {

    def danglingSwitchCode(code: String): Boolean = {
        val tunit = prepareAST[TranslationUnit](parseTranslationUnit(code))
        val ds = new CIntraAnalysisFrontend(tunit)
        ds.danglingSwitchCode()
    }

    @Test def test_danglingswitch_simple() {
        danglingSwitchCode( """
               void f(void) {
                  int a;
                  switch (a) {
                    a = a+1;
                    case 0: a+2;
                    default: a+3;
                  }
               }
        """.stripMargin) should be(true)
    }

    danglingSwitchCode( """
               void f(void) {
                  int a;
                  switch (a) {
                    case 0: a+2;
                    default: a+3;
                  }
               }
    """.stripMargin) should be(false)

    danglingSwitchCode( """
               void f(void) {
                  int a;
                  switch (a) {
                    #ifdef A
                    a++;
                    #endif
                    case 0: a+2;
                    default: a+3;
                  }
               }
    """.stripMargin) should be(true)

    danglingSwitchCode( """
               void f(void) {
                  int a;
                  #ifdef A
                  switch (a) {
                    #ifndef A
                    a++;
                    #endif
                    case 0: a+2;
                    default: a+3;
                  }
                  #endif
               }
    """.stripMargin) should be(false)
}

