package de.fosd.typechef.crewrite

import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.{CDeclUse, CTypeSystemFrontend}
import org.junit.{Ignore, Test}
import org.scalatest.Matchers

class LivenessTest extends EnforceTreeHelper with TestHelper with Matchers with IntraCFG with CFGHelper {

    private def runExample(code: String) {
        val a = prepareAST[FunctionDef](parseFunctionDef(code))

        val env = CASTEnv.createASTEnv(a)
        val ss = getAllSucc(a.stmt.innerStatements.head.entry, env).map(_._1).filterNot(_.isInstanceOf[FunctionDef])

        val ts = new CTypeSystemFrontend(TranslationUnit(List(Opt(FeatureExprFactory.True, a)))) with CDeclUse
        assert(ts.checkASTSilent, "typecheck fails!")
        val udm = ts.getUseDeclMap
        val lv = new Liveness(env, udm, FeatureExprFactory.empty)

        for (s <- ss) {
            println(PrettyPrinter.print(s) + "  uses: " + lv.gen(s) + "   defines: " + lv.kill(s) +
                    "  in: " + lv.in(s) + "   out: " + lv.out(s))
        }

    }

    private def runDefinesExample(code: String) = {
        val a = parseStmt(code)
        val lv = new Liveness(CASTEnv.createASTEnv(a), null, null)
        lv.kill(a).map {case (x, f) => (x, f)}
    }

    private def runUsesExample(code: String) = {
        val a = parseStmt(code)
        val lv = new Liveness(CASTEnv.createASTEnv(a), null, null)
        lv.gen(a).map {case (x, f) => (x, f)}
    }

    private def runDeclaresExample(code: String) = {
        val a = parseDecl(code)
        val lv = new Liveness(CASTEnv.createASTEnv(a), null, null)
        lv.declaresVar(a).map {case (x, f) => (x, f)}
    }

    @Test def test_return_function() {
        runExample( """
      void foo() {
        return foo();
    }
                    """)
    }

    @Test def test_standard_liveness_example() {
        runExample( """
      int foo(int a, int b, int c) {
        a = 0;
        l1: b = a + 1;
        c = c + b;
        a = b + 2;
        if (a < 20) goto l1;
        return c;
    }
                    """)
    }

    @Test def test_standard_liveness_variability_f() {
        runExample( """
      int foo(int a, int b, int c) {
        a = 0;
        l1: b = a + 1;
        c = c + b;
        a = b + 2;
        if (a < 20)
          goto l1;
        return c;
    }
                    """)
    }

    @Test def test_standard_liveness_variability_notf() {
        runExample( """
      int foo(int a, int b, int c) {
        a = 0;
        l1: b = a + 1;
        c = c + b;
        a = b + 2;
        return c;
    }
                    """)
    }

    @Test def test_standard_liveness_variability() {
        runExample( """
      int foo(int a, int b, int c) {
        a = 0;
        l1: b = a + 1;
        c = c + b;
        a = b + 2;
        #ifdef F
        if (a < 20)
          goto l1;
        #endif
        return c;
    }
                    """)
    }

    @Test def test_jens() {
        runExample( """
      int foo(int a, int b) {
        int c = a;
        if (c) {
          #if definedEx(A) && definedEx(B)
          c = c + a;
          #endif
          #if definedEx(B)
          c = c + b;
          #endif
        }
        return c;
    }
                    """)
    }

    @Test def test_jens_NANB() {
        runExample( """
      int foo(int a, int b) {
        int c = a;
        if (c) {
        }
    }
                    """)
    }

    @Test def test_jens_AB() {
        runExample( """
      int foo(int a, int b) {
        int c = a;
        if (c) {
          c = c + a;
          c = c + b;
        }
        return c;
    }
                    """)
    }

    @Test def test_jens_NAB() {
        runExample( """
      int foo(int a, int b) {
        int c = a;
        if (c) {
          c = c + b;
        }
        return c;
    }
                    """)
    }

    @Test def test_simple() {
        runExample( """
      int foo(int a, int b) {
        int c = a;
        if (c) {
          c = c + a;
          #if definedEx(A)
          c = c + b;
          #endif
        }
        return c;
    }
                    """)
    }

    @Test def test_simple_A() {
        runExample( """
      int foo(int a, int b) {
        int c = a;
        if (c) {
          c = c + a;
          c = c + b;
        }
        return c;
    }
                    """)
    }

    @Test def test_simple_NA() {
        runExample( """
      int foo(int a, int b) {
        int c = a;
        if (c) {
          c = c + a;
        }
        return c;
    }
                    """)
    }

    @Test def test_simle2() {
        runExample( """
      int foo() {
        int a;
        int b;
        #if definedEx(A)
        int c = c + b;
        return c;
        #endif
    }
                    """)
    }

    @Test def test_simle3() {
        runExample( """
      int foo() {
        int a;
        #if definedEx(A)
        int b;
        int c = c + b;
        return c;
        #endif
    }
                    """)
    }

    @Test def test_simle4() {
        runExample( """
      int foo(int a, int b, int c, int d, int e) {
        int f = a;
        #if definedEx(A)
        f = b;
        #endif
        f = c;
        #if definedEx(B)
        f = d;
        #endif
        f = e;
    }
                    """)
    }

    @Test def test_sign() {
        runExample( """
      int foo() {
        int x = 0;
        #if definedEx(A)
        x++;
        #else
        x--;
        #endif
        x = 0;
    }
                    """)
    }

    @Test def test_simple_alternative() {
        runExample(
            """
              int foo() {
                int x = 0;
                #ifdef A
                x = 2;
                #endif
                int y = x;
              }
            """.stripMargin)
    }

    @Test def test_alternative() {
        runExample( """
      int foo() {
        int x = 0;
        #if definedEx(A)
        int a = 0;
        #else
        int b = 0;
        #endif
        x = 0;
    }
                    """)
    }

    @Test def test_kill() {
        runExample( """
      int foo(int a, int b, int c) {
        c = b;
        b = c;
        c = a;
    }
                    """)
    }

    @Test def test_kill2() {
        runExample( """
      int foo(int a, int b, int c) {
        c = b;
        b = c;
        c = a;
        c = c;
    }
                    """)
    }

    @Test def test_shadowing() {
        runExample( """
      int foo() {
        int a = 0;
        int b = a;
        if (b) {
          int a = b;
          a;
          b;
        }
        a;
      }
                    """)
    }

    @Test def test_shadowing_variable() {
        runExample( """
      int foo() {
        int a = 0;
        int b = a;
        if (b) {
          #if definedEx(A)
          int a = b;
          #endif
          a;
        }
        b;
        return a;
      }""")
    }

    @Test def test_shadowing2() {
        runExample( """
      int foo() {
        int a = 0;
        int b = a;
        if (b) {
          #if definedEx(A)
          int a = b;
          #endif
          a;
        }
        b;
        return a;
      }""")
    }

    @Test def test_nameconflict() {
        runExample( """
      int foo() {
        int a = 0;
        int a1 = 0;
        int a2 = 0;
        int b = a + a1 + a2;
      }""")
    }

    @Test def test_DefAUseNotA() {
        runExample( """
      int foo() {
        int a = 0;
        int b = 0;
        int c = 0;
        int d = 0;
        b = 3;
        #if definedEx(A)
        a = b;
        d += 1;
        #endif
        c = 1;
        #if !definedEx(A)
        a;
        d += 2;
        #endif
      }""")
    }

    @Test def test_make_hash() {
        runExample( """
      static void make_hash(const char *key, unsigned *start, unsigned *decrement, const int hash_prime) {
      unsigned long hash_num = key[0];
      int len = 1;
      int i;

      for (i = 1; i < len; i++) {
        hash_num += (key[i] + key[i-1]) << ((key[i] * i) % 24);
      }
      *start = (unsigned) hash_num % hash_prime;
      *decrement = (unsigned) 1 + (hash_num % (hash_prime - 1));
    }
                    """)
    }

    @Test def test_simple_for() {
        runExample(
            """
              void foo(int min, int max, int alpha, int* key) {
                int i, n;
                for (n = min; n <= max; n++) {
                  for (i = 0; i < alpha; i++) {
                    key[i] = key[i] + alpha;
                  }
                }
              }
            """.stripMargin)
    }

    @Test def test_sven() {
        runExample( """
      void foo() {
        int a = 1;
        int b = 0;
        #if definedEx(A)
        int c = a;
        #endif
        #if !definedEx(A)
        char c = a;
        #endif
        if (c) {
          c += a;
        #if definedEx(B)
          c /= b;
        #endif
        }
      }
                    """)
    }

    @Test def test_rigorosum() {
        runExample( """
      void foo(int a, int b) {
        int c = 1;
        if (a) {
          c += a;
        #if definedEx(F)
          c /= b;
        #endif
        }
      }
                    """)
    }

    @Test def test_paper() {
        runExample(
            """
            int foo(int a
            #ifdef B
            , int b
            #endif
            ) {
            if (a) {
              return a;
            }
            int c = a;
            if (c) {
              c += a;
              #ifdef B
              c += b;
              #endif
            }
            return c;
            }
            """.stripMargin)
    }

    @Test def test_NNH() {
        runExample(
            """
      void foo(int x, int y, int z) {
        x = 2;  // dead code
        y = 4;
        x = 1;
        if (y > x) {
          z = y;
        } else {
          z = y*y;
        }
        x = z;
      }
            """)
    }

    @Test def test_NNH_var() {
        runExample(
            """
      void foo(int x, int y, int z) {
        x = 2;   // not dead since x = 1 is variable
        y = 4;
        #ifdef A
        x = 1;
        #endif
        if (y > x) {
          z = y;
        } else {
          z = y*y;
        }
        x = z;
      }
            """)
    }

    @Test def test_nested_loops() {
        runExample(
            """
static
void test1(int *code,
        int *length,
        int minLen,
        int maxLen,
        int alphaSize)
{
    int n, vec, i;

    vec = 0;
    for (n = minLen; n <= maxLen; n++) {

        for (i = 0; i < alphaSize; i++)
            ;
        #ifdef A
        vec <<= 1; // dead store
        #endif
    }
}
            """.stripMargin)
    }

    // !!! does not terminate !!!
    @Ignore def test_longsatformulas() {
        runExample(
            """
              void foo() {
                int a;
              #ifdef Z
                a = -1;
                #ifdef A
                if (a > 0) {
                  a = 1;
                } else
                #endif
                #ifdef B
                if (a > 1) {
                  a = 2;
                } else
                #endif
                  goto err;
                a = 2;
                return;
                err:
              #endif
                 a = 3;
              }
            """.stripMargin)
    }

    @Ignore def test_longsatformulas2() {
        runExample(
            """
              void foo() {
                int a;
                #if defined(Z)
                a = -1;
                #if defined(A) && defined(B)
                if (a > 0) {
                  a = 1;
                } else
                if (a > 1) {
                  a = 2;
                } else
                  goto err;
                #endif
                #if defined(A) && !defined(B)
                if (a > 0) {
                  a = 1;
                } else
                  goto err;
                #endif
                #if !defined(A) && defined(B)
                if (a > 1) {
                  a = 2;
                } else
                  goto err;
                #endif
                #if !defined(A) && !defined(B)
                  goto err;
                #endif
                a = 2;
                return;
                err:
              #endif
                 a = 3;
              }
            """.stripMargin)
    }

    @Ignore("requires bdd (sat is default) for efficient computation.") def test_longsatformulas3() {
        runExample(
            """
              void foo() {
                int a, b, c, d, e, f, g, h;
                again:;
                a = 2;
                #if defined(Z) || defined(Y)
                  a = 1;
                  #ifdef A
                  b = 2 + a;
                  #endif
                  c = 1;
                  #ifdef B
                  g = 2 + a;
                  a = 2;
                  #endif

                  #ifdef C
                  f = f + 1 + a;
                  goto again;
                  #endif

                  goto again;
                #endif
                h = a + b + c + d + e + f + g;
              }
            """.stripMargin)
    }

    @Test def test_alex() {
        runExample(
            """
              void foo() {
                int a, b, c;
                again:

                c = a;
                if (1) goto again;
                else a = b;
              }
            """.stripMargin)
    }

    @Test def test_ifcascade() {
        runExample(
            """
            void foo() {
              int a = 0;
              if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |if (a == 1) a = 2;
              |while (a == 1)
              |  continue;
              |
              |while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}
              |while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}
              |while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}
              |while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}
              |while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}
              |while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}
              |while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}
              |while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}
              |while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}
              |while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}
              |while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}
              |while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}
              |while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}
              |while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}
              |while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}
              |while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}
              |while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}
              |while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}
              |while (a == 2) {
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |  if (a > 1) a = 3;
              |}
              |
              |
              |
              |
              |
              |
              |
              |
              |
              |
              |
              |
              |
            }
            """.stripMargin
        )
    }


    // http://www.exforsys.com/tutorials/c-language/c-expressions.html
    @Test def test_uses() {
        runUsesExample("!a;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runUsesExample("a;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runUsesExample("a++;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runUsesExample("++a;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runUsesExample("a--;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runUsesExample("--a;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runUsesExample("a[b];") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("f(a, b, c);") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True), (Id("c"), FeatureExprFactory.True)))
        runUsesExample("a.b;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runUsesExample("a->b;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runUsesExample("return f(a,b,c);") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True), (Id("c"), FeatureExprFactory.True)))
        runUsesExample( """return f(a,
                       #if definedEx(B)
                         b,
                       #endif
                       c);
                        """) should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), fb), (Id("c"), FeatureExprFactory.True)))
        runUsesExample( """a = (b < 2) ? c : d;

                        """) should be(toMap(Id("b"), FeatureExprFactory.True)) // TODO conditional expressions.

        runUsesExample("&a;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runUsesExample("*a;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runUsesExample("!a;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runUsesExample("~a;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runUsesExample("-a;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runUsesExample("+a;") should be(toMap(Id("a"), FeatureExprFactory.True))

        runUsesExample("a * b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a - b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a / b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a % b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a & b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a ^ b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))

        runUsesExample("a && b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a || b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a | b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a << b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a >> b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))

        runUsesExample("a = b;") should be(toMap(Id("b"), FeatureExprFactory.True))
        runUsesExample("a = b++;") should be(toMap(Id("b"), FeatureExprFactory.True))
        runUsesExample("a *= b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a += b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a -= b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a /= b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a %= b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a &= b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a ^= b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a |= b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a >>= b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a <<= b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))

        runUsesExample("a == b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a != b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a < b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a > b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a <= b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runUsesExample("a >= b;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
    }

    @Test def test_defines() {
        runDefinesExample("a;") should be(Map())
        runDefinesExample("a++;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runDefinesExample("++a;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runDefinesExample("a[b];") should be(Map())
        runDefinesExample("f(a, b, c);") should be(Map())
        runDefinesExample("a.b;") should be(Map())
        runDefinesExample("a->b;") should be(Map())

        runDefinesExample("&a;") should be(Map())
        runDefinesExample("*a;") should be(Map())
        runDefinesExample("!a;") should be(Map())
        runDefinesExample("~a;") should be(Map())
        runDefinesExample("-a;") should be(Map())
        runDefinesExample("+a;") should be(Map())

        runDefinesExample("a * b;") should be(Map())
        runDefinesExample("a - b;") should be(Map())
        runDefinesExample("a / b;") should be(Map())
        runDefinesExample("a % b;") should be(Map())
        runDefinesExample("a & b;") should be(Map())
        runDefinesExample("a ^ b;") should be(Map())

        runDefinesExample("a && b;") should be(Map())
        runDefinesExample("a || b;") should be(Map())
        runDefinesExample("a | b;") should be(Map())
        runDefinesExample("a << b;") should be(Map())
        runDefinesExample("a >> b;") should be(Map())

        runDefinesExample("a = b;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runDefinesExample("a = b++;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runDefinesExample("a *= b;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runDefinesExample("a += b;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runDefinesExample("a -= b;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runDefinesExample("a /= b;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runDefinesExample("a %= b;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runDefinesExample("a &= b;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runDefinesExample("a ^= b;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runDefinesExample("a |= b;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runDefinesExample("a >>= b;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runDefinesExample("a <<= b;") should be(toMap(Id("a"), FeatureExprFactory.True))

        runDefinesExample("a == b;") should be(Map())
        runDefinesExample("a != b;") should be(Map())
        runDefinesExample("a < b;") should be(Map())
        runDefinesExample("a > b;") should be(Map())
        runDefinesExample("a <= b;") should be(Map())
        runDefinesExample("a >= b;") should be(Map())
    }

    // http://en.wikipedia.org/wiki/C_data_types
    @Test def test_declares() {
        runDeclaresExample("int a = 0;") should be(toMap(Id("a"), FeatureExprFactory.True))
        runDeclaresExample("int a, b = 0;") should be(Map((Id("a"), FeatureExprFactory.True), (Id("b"), FeatureExprFactory.True)))
        runDeclaresExample("int a[10];") should be(toMap(Id("a"), FeatureExprFactory.True))
        runDeclaresExample("char *c;") should be(toMap(Id("c"), FeatureExprFactory.True))
        runDeclaresExample("float f;") should be(toMap(Id("f"), FeatureExprFactory.True))
        runDeclaresExample( """
      struct {
        int i;
      } s;""") should be(toMap(Id("s"), FeatureExprFactory.True))
        runDeclaresExample( """
      struct k {
        int i;
      } s;""") should be(toMap(Id("s"), FeatureExprFactory.True))
        runDeclaresExample( """
      struct k {
        int i;
      };""") should be(Map())
        runDeclaresExample( """
      struct k s;""") should be(Map((Id("s"), FeatureExprFactory.True)))
        runDeclaresExample( """
      union {
        int i;
      } u;""") should be(toMap(Id("u"), FeatureExprFactory.True))
    }

    private def toMap[A,B](a: A, b: B) = Map(a -> b)
}
