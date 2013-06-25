package de.fosd.typechef.crewrite

import org.junit.Test
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.parser.c._

class UninitializedMemoryTest extends TestHelper with ShouldMatchers with CFGHelper with EnforceTreeHelper {

    private def getUninitializedVariables(code: String) = {
        val a = parseCompoundStmt(code)
        val um = new UninitializedMemory(CASTEnv.createASTEnv(a), null, null)
        um.gen(a)
    }

    private def getFunctionCallArguments(code: String) = {
        val a = parseExpr(code)
        val um = new UninitializedMemory(CASTEnv.createASTEnv(a), null, null)
        um.getFunctionCallArguments(a)
    }

    def uninitializedMemory(code: String): Boolean = {
        val tunit = prepareAST[TranslationUnit](parseTranslationUnit(code))
        val um = new CAnalysisFrontend(tunit)
        um.uninitializedMemory()
    }

    @Test def test_variables() {
        getUninitializedVariables("{ int a; }") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        getUninitializedVariables("{ int a = 2; }") should be(Map())
        getUninitializedVariables("{ int a, b = 1; }") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        getUninitializedVariables("{ int a = 1, b; }") should be(Map(FeatureExprFactory.True -> Set(Id("b"))))
        getUninitializedVariables("{ int *a; }") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        getUninitializedVariables("{ int a[5]; }") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        getUninitializedVariables("""{
              #ifdef A
              int a;
              #endif
              }""".stripMargin) should be(Map(fa -> Set(Id("a"))))
    }

    @Test def test_functioncall_arguments() {
        getFunctionCallArguments("foo(a,b)") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        getFunctionCallArguments("foo(a,bar(c))") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("c"))))
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
        }""".stripMargin) should be(false)

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
    }
}
