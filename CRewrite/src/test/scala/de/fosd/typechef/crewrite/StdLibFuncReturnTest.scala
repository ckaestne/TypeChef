package de.fosd.typechef.crewrite

import org.junit.Test
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.{CDeclUse, CTypeCache, CTypeSystemFrontend}

class StdLibFuncReturnTest extends TestHelper with ShouldMatchers with CFGHelper with EnforceTreeHelper {

    def stdlibfuncreturn(code: String): Boolean = {
        val tunit = prepareAST[TranslationUnit](parseTranslationUnit(code))
        val ts = new CTypeSystemFrontend(tunit) with CTypeCache with CDeclUse
        assert(ts.checkASTSilent, "typecheck fails!")
        val df = new CIntraAnalysisFrontend(tunit, ts)
        df.stdLibFuncReturn()
    }

    @Test def test_simple() {
        stdlibfuncreturn(
            """
            void foo() {}
            """.stripMargin) should be(true)
    }

    @Test def test_malloc() {
        stdlibfuncreturn(
            """
            void* malloc() { return (void*)0; }
            void foo() {
                if (malloc() == ((void*)1)) { }
            }
            """.stripMargin) should be(false)

        stdlibfuncreturn(
            """
            void* malloc() { return (void*)0; }
            void foo() {
                void* a = malloc();

                if (a == ((void*)1)) { }
            }
            """.stripMargin) should be(false)

    }
}
