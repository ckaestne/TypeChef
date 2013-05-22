package de.fosd.typechef.crewrite

import org.junit.Test
import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr.FeatureExprFactory
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.typesystem.{CDeclUse, CTypeSystemFrontend}
import de.fosd.typechef.conditional.Opt

// this test does not check the determination of
// used, defined, and declared variables
// see LivenessTest for that
class ReachingDefinitionsTest extends TestHelper with ShouldMatchers with IntraCFG with CFGHelper {

    private def runExample(code: String) {
        val a = parseFunctionDef(code)

        val env = CASTEnv.createASTEnv(a)
        val ss = getAllPred(a.stmt.innerStatements.head.entry, FeatureExprFactory.empty, env).map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])

        val ts = new CTypeSystemFrontend(TranslationUnit(List(Opt(FeatureExprFactory.True, a)))) with CDeclUse
        assert(ts.checkASTSilent, "typecheck fails!")
        val udm = ts.getUseDeclMap
        val rd = new ReachingDefintions(env, udm, FeatureExprFactory.empty)

        for (s <- ss) {
            println(PrettyPrinter.print(s) + "  uses: " + rd.gen(s) + "   defines: " + rd.kill(s) +
                    "  in: " + rd.in(s) + "   out: " + rd.out(s))
        }

    }

    @Test def test_standard_reachingdefinitions_example() {
        runExample( """
      void foo(int x, int y) {
        y = 3;
        x = y;
    }""")
    }

    @Test def test_standard_reachingdefinitions_example2() {
        runExample( """
      void foo(int x, int y) {
        y = 3;
        y = 4;
        x = y;
    }""")
    }

    @Test def test_standard_reachingdefinitions_example3() {
        runExample( """
      void foo(int x, int y, int z) {
        y = 3;
        z = 2;
        #ifdef A
        x = y;
        #else
        x = z;
        #endif
    }""")
    }
}
