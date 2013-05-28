package de.fosd.typechef.crewrite

import org.junit.Test
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.typesystem._
import de.fosd.typechef.parser.c._

class XFreeTest extends TestHelper with ShouldMatchers with CFGHelper {

    private def getUninitializedVariables(code: String) = {
        val a = parseCompoundStmt(code)
        val xf = new XFree(CASTEnv.createASTEnv(a), null, null, "")
        xf.gen(a)
    }

    def xfree(code: String): Boolean = {
        val tunit = parseTranslationUnit(code)
        val ts = new CTypeSystemFrontend(tunit, FeatureExprFactory.empty) with CDeclUse
        assert(ts.checkASTSilent, "typecheck fails!")
        val env = CASTEnv.createASTEnv(tunit)
        val udm = ts.getUseDeclMap

        val fdefs = filterAllASTElems[FunctionDef](tunit)
        val errors = fdefs.flatMap(xfree(_, env, udm))

        if (errors.isEmpty) {
            println("No uages of uninitialized memory found!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        !errors.isEmpty
    }

    private def xfree(f: FunctionDef, env: ASTEnv, udm: UseDeclMap): List[AnalysisError] = {
        var res: List[AnalysisError] = List()

        // It's ok to use FeatureExprFactory.empty here.
        // Using the project's fm is too expensive since control
        // flow computation requires a lot of sat calls.
        // We use the proper fm in UninitializedMemory (see MonotoneFM).
        val ss = getAllPred(f, FeatureExprFactory.empty, env).reverse
        val xf = new XFree(env, udm, FeatureExprFactory.empty, "")
        val nss = ss.map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])

        for (s <- nss) {
            val g = xf.freedVariables(s)
            val in = xf.in(s)
            val gen = xf.gen(s)
            val kill = xf.kill(s)
            val out = xf.out(s)
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
                                    res ::= new AnalysisError(h, "warning: Variable " + x.name + " is freed although not dynamically allocted!", x)
                        }
                    }
        }

        res
    }

    @Test def test_variables() {
        getUninitializedVariables("{ int a; }") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        getUninitializedVariables("{ int a = 2; }") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        getUninitializedVariables("{ int a, b = 1; }") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        getUninitializedVariables("{ int *a = (int*)malloc(2); }") should be(Map())
    }

    @Test def test_xfree_simple() {
        xfree( """
               void* realloc(void* ptr, int size) { return ((void*)0); }
               void f(void) {
                 char buf[20];
                 char *p;
                 p = (char*)realloc(buf, 2*20);
               }
               """.stripMargin) should be(true)

        xfree( """
               void* malloc(int size) { return ((void*)0); }
               void* realloc(void* ptr, int size) { return ((void*)0); }
               void f(void) {
                 char* buf = (char*)malloc(20);
                 char *p;
                 p = (char*)realloc(buf,20);
               }
               """.stripMargin) should be(false)

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
               free(str);
               return 0;
               }
               """.stripMargin) should be(true)
    }
}
