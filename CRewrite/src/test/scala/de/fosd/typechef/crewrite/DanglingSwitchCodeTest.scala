package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import org.junit.Test
import org.scalatest.Matchers

import scala.Predef._

class DanglingSwitchCodeTest extends TestHelper with Matchers with CFGHelper with EnforceTreeHelper {

    def danglingSwitchCode(code: String): Boolean = {
        val tunit = prepareAST[TranslationUnit](parseTranslationUnit(code))
        val ds = new CIntraAnalysisFrontend(tunit, null)
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
        """.stripMargin) should be(false)
    }

    danglingSwitchCode( """
               void f(void) {
                  int a;
                  switch (a) {
                    case 0: a+2;
                    default: a+3;
                  }
               }
    """.stripMargin) should be(true)

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
    """.stripMargin) should be(false)

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
    """.stripMargin) should be(true)
}

