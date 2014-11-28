package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.{CDeclUse, CTypeCache, CTypeSystemFrontend}
import org.junit.Test
import org.scalatest.Matchers

class CaseTerminationTest extends TestHelper with Matchers with CFGHelper with EnforceTreeHelper {

    def caseTermination(code: String): Boolean = {
        val tunit = prepareAST[TranslationUnit](parseTranslationUnit(code))
        val ts = new CTypeSystemFrontend(tunit) with CTypeCache with CDeclUse
        assert(ts.checkASTSilent, "typecheck fails!")
        val df = new CIntraAnalysisFrontend(tunit, ts)
        df.caseTermination()
    }

    @Test def test_simple() {
        caseTermination(
            """
            void foo(int a) {
              switch (a) {
                case 0: a = 1; break;
              }
            }
            """.stripMargin) should be(true)
    }

    @Test def test_case_fallthrough() {
        caseTermination(
            """
            void foo(int a) {
              switch (a) {
                case 1:
                case 0: a = 1;
              }
            }
            """.stripMargin) should be(false)
        caseTermination(
            """
            void foo(int a) {
              switch (a) {
                case 1:
                case 0: a = 1;
                break;
              }
            }
            """.stripMargin) should be(true)
        caseTermination(
            """
            void foo(int a) {
              switch (a) {
                case 1:
                #ifdef A
                a = 2;
                #endif
                case 0: a = 1;
                break;
              }
            }
            """.stripMargin) should be(false)
        caseTermination(
            """
            void foo(int a) {
              switch (a) {
                case 1:
                #ifdef A
                case 0: a = 1;
                #endif
                break;
              }
            }
            """.stripMargin) should be(true)
        caseTermination(
            """
            void foo(int a) {
              switch (a) {
                case 1:
                #ifdef A
                case 0: a = 1;
                #endif
                break;
              }
            }
            """.stripMargin) should be(true)
    }

    @Test def test_nobreak() {
        caseTermination(
            """
            void foo(int a) {
              switch (a) {
                case 0: a = 1;
              }
            }
            """.stripMargin) should be(false)

        caseTermination(
            """
            void foo(int a) {
              switch (a) {
                case 0: a = 1;
                #ifdef A
                break;
                #endif
              }
            }
            """.stripMargin) should be(false)
        caseTermination(
            """
            void foo(int a) {
            #ifdef A
              switch (a) {
                case 0: a = 1;
            #ifndef A
                break;
            #endif
              }
            #endif
            }
            """.stripMargin) should be(false)

    }
}
