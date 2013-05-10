package de.fosd.typechef.crewrite

import org.junit.Test
import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr.FeatureExprFactory
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.typesystem.{CDeclUse, CTypeSystemFrontend}
import de.fosd.typechef.conditional.Opt

class LivenessTest extends TestHelper with ShouldMatchers with IntraCFG with CFGHelper {

    private def runExample(code: String) {
        val a = parseFunctionDef(code)

        val env = CASTEnv.createASTEnv(a)
        val ss = getAllSucc(a.stmt.innerStatements.head.entry, FeatureExprFactory.empty, env).map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])

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
        lv.kill(a)
    }

    private def runUsesExample(code: String) = {
        val a = parseStmt(code)
        val lv = new Liveness(CASTEnv.createASTEnv(a), null, null)
        lv.gen(a)
    }

    private def runDeclaresExample(code: String) = {
        val a = parseDecl(code)
        val lv = new Liveness(CASTEnv.createASTEnv(a), null, null)
        lv.declaresVar(a)
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


    // http://www.exforsys.com/tutorials/c-language/c-expressions.html
    @Test def test_uses() {
        runUsesExample("a;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runUsesExample("a++;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runUsesExample("++a;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runUsesExample("a[b];") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("f(a, b, c);") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"), Id("c"))))
        runUsesExample("a.b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runUsesExample("a->b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runUsesExample("return f(a,b,c);") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"), Id("c"))))
        runUsesExample( """return f(a,
                       #if definedEx(B)
                         b,
                       #endif
                       c);
                        """) should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("c")), fb -> Set(Id("b"))))
        runUsesExample( """a = (b < 2) ? c : d;

                        """) should be(Map(FeatureExprFactory.True -> Set(Id("b")))) // TODO conditional expressions.

        runUsesExample("&a;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runUsesExample("*a;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runUsesExample("!a;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runUsesExample("~a;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runUsesExample("-a;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runUsesExample("+a;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))

        runUsesExample("a * b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a - b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a / b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a % b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a & b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a ^ b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))

        runUsesExample("a && b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a || b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a | b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a << b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a >> b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))

        runUsesExample("a = b;") should be(Map(FeatureExprFactory.True -> Set(Id("b"))))
        runUsesExample("a = b++;") should be(Map(FeatureExprFactory.True -> Set(Id("b"))))
        runUsesExample("a *= b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a += b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a -= b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a /= b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a %= b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a &= b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a ^= b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a |= b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a >>= b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a <<= b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))

        runUsesExample("a == b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a != b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a < b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a > b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a <= b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runUsesExample("a >= b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    }

    @Test def test_defines() {
        runDefinesExample("a;") should be(Map())
        runDefinesExample("a++;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runDefinesExample("++a;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
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

        runDefinesExample("a = b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runDefinesExample("a = b++;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runDefinesExample("a *= b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runDefinesExample("a += b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runDefinesExample("a -= b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runDefinesExample("a /= b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runDefinesExample("a %= b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runDefinesExample("a &= b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runDefinesExample("a ^= b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runDefinesExample("a |= b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runDefinesExample("a >>= b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runDefinesExample("a <<= b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))

        runDefinesExample("a == b;") should be(Map())
        runDefinesExample("a != b;") should be(Map())
        runDefinesExample("a < b;") should be(Map())
        runDefinesExample("a > b;") should be(Map())
        runDefinesExample("a <= b;") should be(Map())
        runDefinesExample("a >= b;") should be(Map())
    }

    // http://en.wikipedia.org/wiki/C_data_types
    @Test def test_declares() {
        runDeclaresExample("int a = 0;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runDeclaresExample("int a, b = 0;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
        runDeclaresExample("int a[10];") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        runDeclaresExample("char *c;") should be(Map(FeatureExprFactory.True -> Set(Id("c"))))
        runDeclaresExample("float f;") should be(Map(FeatureExprFactory.True -> Set(Id("f"))))
        runDeclaresExample( """
      struct {
        int i;
      } s;""") should be(Map(FeatureExprFactory.True -> Set(Id("s"))))
        runDeclaresExample( """
      struct k {
        int i;
      } s;""") should be(Map(FeatureExprFactory.True -> Set(Id("s"))))
        runDeclaresExample( """
      struct k {
        int i;
      };""") should be(Map())
        runDeclaresExample( """
      struct k s;""") should be(Map(FeatureExprFactory.True -> Set(Id("s"))))
        runDeclaresExample( """
      union {
        int i;
      } u;""") should be(Map(FeatureExprFactory.True -> Set(Id("u"))))
    }
}
