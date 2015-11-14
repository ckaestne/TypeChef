package de.fosd.typechef.crewrite

import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.{CDeclUse, CTypeCache, CTypeSystemFrontend}
import org.junit.Test
import org.scalatest.Matchers

class UninitializedMemoryTest extends TestHelper with Matchers with CFGHelper with EnforceTreeHelper {

    private def getKilledVariables(code: String) = {
        val a = parseFunctionDef(code)
        val ts = new CTypeSystemFrontend(TranslationUnit(List(Opt(FeatureExprFactory.True, a)))) with CDeclUse
        assert(ts.checkASTSilent, "typecheck fails!")
        val dum = ts.getDeclUseMap
        val udm = ts.getUseDeclMap
        val um = new UninitializedMemory(CASTEnv.createASTEnv(a), dum, udm, FeatureExprFactory.empty)
        um.kill(a).map {case ((x, _), f) => (x, f)}
    }

    private def getGeneratedVariables(code: String) = {
        val a = parseFunctionDef(code)
        val ts = new CTypeSystemFrontend(TranslationUnit(List(Opt(FeatureExprFactory.True, a)))) with CDeclUse
        assert(ts.checkASTSilent, "typecheck fails!")
        val dum = ts.getDeclUseMap
        val udm = ts.getUseDeclMap
        val um = new UninitializedMemory(CASTEnv.createASTEnv(a), dum, udm, FeatureExprFactory.empty)
        um.gen(a).map {case ((x, _), f) => (x, f)}
    }

    def uninitializedMemory(code: String): Boolean = {
        val tunit = prepareAST[TranslationUnit](parseTranslationUnit(code))
        val ts = new CTypeSystemFrontend(tunit) with CTypeCache with CDeclUse
        assert(ts.checkASTSilent, "typecheck fails!")
        val um = new CIntraAnalysisFrontend(tunit, ts)
        um.uninitializedMemory()
    }

    @Test def test_variables() {
        getKilledVariables("void foo(){ int a; }") should be(Map())
        getKilledVariables("void foo(){ int a = 2; }") should be(Map((Id("a"), FeatureExprFactory.True)))
        getKilledVariables("void foo(){ int a, b = 1; }") should be(Map((Id("b"), FeatureExprFactory.True)))
        getKilledVariables("void foo(){ int a = 1, b; }") should be(Map((Id("a"), FeatureExprFactory.True)))
        getKilledVariables("void foo(){ int *a; }") should be(Map())
        getKilledVariables("void foo(){ int a; a = 2; }") should be(Map((Id("a"), FeatureExprFactory.True)))
        getKilledVariables("void foo(){ int a[5]; }") should be(Map())
        getKilledVariables("""void foo(){
              #ifdef A
              int a;
              #endif
              }""".stripMargin) should be(Map())
        getGeneratedVariables("void foo(){ int a; }") should be(Map((Id("a"), FeatureExprFactory.True)))
        getGeneratedVariables("void foo(){ int a = 2; }") should be(Map())
        getGeneratedVariables("void foo(){ int a, b = 1; }") should be(Map((Id("a"), FeatureExprFactory.True)))
        getGeneratedVariables("void foo(){ int a = 1, b; }") should be(Map((Id("b"), FeatureExprFactory.True)))
        getGeneratedVariables("void foo(){ int *a; }") should be(Map((Id("a"), FeatureExprFactory.True)))
        getGeneratedVariables("void foo(int a){ a = 2; }") should be(Map())
        getGeneratedVariables("void foo(){ int a[5]; }") should be(Map((Id("a"), FeatureExprFactory.True)))
        getGeneratedVariables("""void foo(){
              #ifdef A
              int a;
              #endif
              }""".stripMargin) should be(Map((Id("a"), fa)))
    }

    @Test def test_uninitialized_memory_simple() {
        uninitializedMemory( """
        void get_sign(int number, int *sign) {
            if (sign == 0) {
                 /* ... */
            }
            if (number > 0) {
                *sign = 1;
            } else if (number < 0) {
                *sign = -1;
            } // If number == 0, sign is not changed.
        }
        int is_negative(int number) {
            int sign;
            get_sign(number, &sign);
            return (sign < 0); // diagnostic required
        }""".stripMargin) should be(true)

        uninitializedMemory( """
        int do_auth() { return 0; }
        int printf(const char *format, ...);
        int sprintf(char *str, const char* format, ...) { return 0; }
        void report_error(const char *msg) {
            const char *error_log;
            char buffer[24];
            sprintf(buffer, "Error: %s", error_log); // diagnostic required
            printf("%s\n", buffer);
        }
        int main(void) {
            if (do_auth() == -1) {
                report_error("Unable to login");
            }
            return 0;
        }""".stripMargin) should be(false)

        uninitializedMemory( """
        void close(int i) { }
        void foo() {
            int fd;
            close(fd);
        }""".stripMargin) should be(false)

        uninitializedMemory( """
        void close(int i) { }
        void foo() {
            int fd;
            fd = 2;
            close(fd);
        }""".stripMargin) should be(true)

        uninitializedMemory( """
        void close(int i) { }
        void foo() {
            int fd;
            #ifdef A
            fd = 2;
            #endif
            close(fd);
        }""".stripMargin) should be(false)

        uninitializedMemory( """
        int foo() {
          int i;
            #if definedEx(A)
            for ((i = 0); 1; i++) {}
            #endif
            #if !definedEx(A)
            for ((i = 5); 1; i++) {}
            #endif
        }""".stripMargin) should be(true)
    }
}
