package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.parser.c.{Id, TranslationUnit, _}
import de.fosd.typechef.typesystem.{CDeclUse, CTypeCache, CTypeSystemFrontend}
import org.junit.Test
import org.scalatest.Matchers

import scala.Predef._

class XFreeTest extends TestHelper with Matchers with CFGHelper with EnforceTreeHelper {

    private def getUninitializedVariables(code: String) = {
        val a = parseFunctionDef(code)
        val xf = new XFree(CASTEnv.createASTEnv(a), null, null, null, "")
        xf.gen(a).map {case ((x, _), f) => (x, f)}
    }

    def xfree(code: String): Boolean = {
        val tunit = prepareAST[TranslationUnit](parseTranslationUnit(code))
        val ts = new CTypeSystemFrontend(tunit) with CTypeCache with CDeclUse
        assert(ts.checkASTSilent, "typecheck fails!")
        val xf = new CIntraAnalysisFrontend(tunit, ts)
        xf.xfree()
    }

    @Test def test_variables() {
        getUninitializedVariables("void foo() { int a; }") should be(Map(Id("a") -> FeatureExprFactory.True))
        getUninitializedVariables("void foo() { int a = 2; }") should be(Map(Id("a") -> FeatureExprFactory.True))
        getUninitializedVariables("void foo() { int a, b = 1; }") should be(Map(Id("a") -> FeatureExprFactory.True, Id("b") -> FeatureExprFactory.True))
        getUninitializedVariables("void foo() { int *a = (int*)malloc(2); }") should be(Map())
    }

    @Test def test_xfree_simple() {
        xfree( """
               void* realloc(void* ptr, int size) { return ((void*)0); }
               void f(void) {
                 char buf[20];
                 char *p;
                 p = (char*)realloc(buf, 2*20); // diagnostics
               }
               """.stripMargin) should be(false)

        xfree( """
               void* malloc(int size) { return ((void*)0); }
               void* realloc(void* ptr, int size) { return ((void*)0); }
               void f(void) {
                 char* buf = (char*)malloc(20);
                 char *p;
                 p = (char*)realloc(buf,20);
               }
               """.stripMargin) should be(true)

        xfree( """
               void* malloc(int size) { return ((void*)0); }
               void* realloc(void* ptr, int size) { return ((void*)0); }
               void free(void* ptr) { }
               int main(int argc, const char *argv[]) {
               char *str = ((void*)0);
               int len;
               if (argc == 2) {
                 len = 11;
                 str = (char *)malloc(len);
               } else {
                 str = "usage: $>a.exe [string]";
               }
               free(str); // diagnostics
               return 0;
               }
               """.stripMargin) should be(false)
    }
}
