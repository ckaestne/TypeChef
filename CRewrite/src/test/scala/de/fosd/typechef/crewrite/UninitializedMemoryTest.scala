package de.fosd.typechef.crewrite

import org.junit.Test
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.typesystem._
import de.fosd.typechef.parser.c._

class UninitializedMemoryTest extends TestHelper with ShouldMatchers with CFGHelper {

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
        val tunit = parseTranslationUnit(code)
        val ts = new CTypeSystemFrontend(tunit, FeatureExprFactory.empty) with CDeclUse
        assert(ts.checkASTSilent, "typecheck fails!")
        val env = CASTEnv.createASTEnv(tunit)
        val udm = ts.getUseDeclMap

        val fdefs = filterAllASTElems[FunctionDef](tunit)
        val errors = fdefs.flatMap(uninitializedMemory(_, env, udm))

        if (errors.isEmpty) {
            println("No uages of uninitialized memory found!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        !errors.isEmpty
    }

    private def uninitializedMemory(f: FunctionDef, env: ASTEnv, udm: UseDeclMap): List[AnalysisError] = {
        var res: List[AnalysisError] = List()

        // It's ok to use FeatureExprFactory.empty here.
        // Using the project's fm is too expensive since control
        // flow computation requires a lot of sat calls.
        // We use the proper fm in UninitializedMemory (see MonotoneFM).
        val ss = getAllPred(f, FeatureExprFactory.empty, env).reverse
        val um = new UninitializedMemory(env, udm, FeatureExprFactory.empty)
        val nss = ss.map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])

        for (s <- nss) {
            val g = um.getFunctionCallArguments(s)
            val in = um.in(s)
            val gen = um.gen(s)
            val kill = um.kill(s)
            val out = um.out(s)
            println("s: " + PrettyPrinter.print(s), "args: " + g, "gen: " + gen, "kill: " + kill, "in: " + in, "out: " + out)

            for ((i, h) <- in)
                for ((f, j) <- g)
                    j.find(_ == i) match {
                        case None =>
                        case Some(x) => {
                            val xdecls = udm.get(x)
                            var idecls = udm.get(i)
                            if (idecls == null)
                                idecls = List(i)
                            for (ei <- idecls)
                                if (xdecls.exists(_.eq(ei)))
                                    res ::= new AnalysisError(h, "warning: Variable " + x.name + " is used uninitialized!", x)
                        }
                    }
        }

        res
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
        }""".stripMargin) should be(true)

        uninitializedMemory( """
        void close(int i) { }
        void foo() {
            int fd;
            fd = 2;
            close(fd);
        }""".stripMargin) should be(false)

        uninitializedMemory( """
        void close(int i) { }
        void foo() {
            int fd;
            #ifdef A
            fd = 2;
            #endif
            close(fd);
        }""".stripMargin) should be(true)
    }
}
