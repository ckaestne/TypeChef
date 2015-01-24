package de.fosd.typechef.crewrite

import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.{CDeclUse, CTypeSystemFrontend}
import org.junit.Test
import org.scalatest.Matchers

class ReachingDefinitionsTest extends TestHelper with Matchers with IntraCFG with CFGHelper {

    private def runExample(code: String) {
        val a = parseFunctionDef(code)

        val env = CASTEnv.createASTEnv(a)
        val ss = getAllPred(a, env).map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])

        val ts = new CTypeSystemFrontend(TranslationUnit(List(Opt(FeatureExprFactory.True, a)))) with CDeclUse
        assert(ts.checkASTSilent, "typecheck fails!")
        val udm = ts.getUseDeclMap
        val dum = ts.getDeclUseMap
        val rd = new ReachingDefinitions(env, dum, udm, FeatureExprFactory.empty, a)

        for (s <- ss) {
            println(PrettyPrinter.print(s) + "  gen: " + rd.gen(s) + "   kill: " + rd.kill(s) +
             " in: " + rd.in(s) + " out: " + rd.out(s))
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

    @Test def test_standard_reachingdefinitions_example2_optional() {
        runExample( """
      void foo(int x, int y) {
        y = 3;
        #ifdef A
        y = 4;
        #endif
        x = y;
    }""")
    }

    @Test def test_standard_reachingdefinitions_example2_alternative() {
        runExample( """
      void foo(int x, int y) {
        #ifdef A
        y = 3;
        #else
        y = 4;
        #endif
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

    @Test def test_swap() {
        runExample( """
        void swap(int *a, int *b) {
          int tmp = *a;
          *a = *b;
          *b = tmp;
        }""".stripMargin)
    }

    // http://www.itu.dk/people/wasowski/teach/dsp-compiler-06/episode-6/episode06-handout.pdf p.7
    @Test def test_while() {
        runExample(
            """
            void whileEx(int a, int c) {
              a = 5;
              c = 1;
              while (c > a) {
                c = c + c;
              }
              a = c - a;
              c = 0;
            }
            """.stripMargin)
    }

    @Test def test_example2_7() {
        runExample(
            """
            void foo(int x, int y) {
              x = 5;
              y = 1;
              while (x>1) {
                y = x*y;
                x = x-1;
              }
            }
            """.stripMargin
        )
    }

    @Test def test_defuse() {
        runExample(
            """
              void foo() {
                int x;
                int a = 0;
                if (x) {
                  a = 1;
                } else {
                  a = 2;
                }
                int z = a + x;
              }
            """.stripMargin
        )
    }
}
