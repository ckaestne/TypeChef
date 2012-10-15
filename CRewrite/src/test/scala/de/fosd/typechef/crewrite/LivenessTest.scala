package de.fosd.typechef.crewrite

import org.junit.Test
import de.fosd.typechef.parser.c.{Id, TestHelper, PrettyPrinter, FunctionDef}
import de.fosd.typechef.featureexpr.FeatureExprFactory
import org.scalatest.matchers.ShouldMatchers

class LivenessTest extends TestHelper with ShouldMatchers with ConditionalControlFlow with Liveness {

  private def runExample(code: String) {
    val a = parseFunctionDef(code)

    val env = CASTEnv.createASTEnv(a)
    val ss = getAllSucc(a.stmt.innerStatements.head.entry, FeatureExprFactory.empty, env).map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])
    setEnv(env)
    val udr = determineUseDeclareRelation(a)
    println(udr)
    setUdr(udr)
    setFm(FeatureExprFactory.empty)

    for (s <- ss)
      println(PrettyPrinter.print(s) + "  uses: " + usesVar(s, env) + "   defines: " + definesVar(s, env) +
        "  in: " + in(s) + "   out: " + out(s))
    println("succs: " + DotGraph.map2file(getAllSucc(a, FeatureExprFactory.empty, env), env))
  }

  private def runDefinesExample(code: String) = {
    val a = parseStmt(code)
    definesVar(a, CASTEnv.createASTEnv(a))
  }

  private def runUsesExample(code: String) = {
    val a = parseStmt(code)
    usesVar(a, CASTEnv.createASTEnv(a))
  }

  private def runDeclaresExample(code: String) = {
    val a = parseDecl(code)
    declaresVar(a, CASTEnv.createASTEnv(a))
  }

  private def runUseDeclareRelationExample(code: String) = {
    val a = parseFunctionDef(code)
    val env = CASTEnv.createASTEnv(a)
    setEnv(env)
    determineUseDeclareRelation(a)
  }

  @Test def test_return_function() {
    runExample("""
      void foo() {
        return f(a, b, c);
    }
               """)
  }

  @Test def test_standard_liveness_example() {
    runExample("""
      void foo() {
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
    runExample("""
      void foo(int a, int b, int c) {
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
    runExample("""
      void foo() {
        a = 0;
        l1: b = a + 1;
        c = c + b;
        a = b + 2;
        return c;
    }
    """)
  }

  @Test def test_standard_liveness_variability() {
    val a = parseFunctionDef("""
      void foo() {
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
    runExample("""
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
    runExample("""
      int foo(int a, int b) {
        int c = a;
        if (c) {
        }
        return c;
    }
               """)
  }

  @Test def test_jens_AB() {
    runExample("""
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
    runExample("""
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
    runExample("""
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
    runExample("""
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
    runExample("""
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
    runExample("""
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
    runExample("""
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
    runExample("""
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
    runExample("""
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
    runExample("""
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
    runExample("""
      int foo(int a, int b, int c) {
        c = b;
        b = c;
        c = a;
    }
               """)
  }

  @Test def test_kill2() {
    runExample("""
      int foo(int a, int b, int c) {
        c = b;
        b = c;
        c = a;
        c = c;
    }
               """)
  }

  @Test def test_shadowing() {
    runExample("""
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
    runExample("""
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
    runExample("""
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
    runExample("""
      int foo() {
        int a = 0;
        int a1 = 0;
        int a2 = 0;
        int b = a + a1 + a2;
      }""")
  }

  @Test def test_DefAUseNotA() {
    runExample("""
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
    runExample("""
      static void make_hash(const char *key, unsigned *start, unsigned *decrement, const int hash_prime) {
      unsigned long hash_num = key[0];
      int len = strlen(key);
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
    runExample("""
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
    runExample("""
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
    runUsesExample("""return f(a,
                       #if definedEx(B)
                         b,
                       #endif
                       c);
                   """) should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("c")), fb -> Set(Id("b"))))
    runUsesExample("""a = (b < 2) ? c : d;

                   """) should be (Map(FeatureExprFactory.True -> Set(Id("b")))) // TODO

    runUsesExample("&a;") should be (Map(FeatureExprFactory.True -> Set(Id("a"))))
    runUsesExample("*a;") should be (Map(FeatureExprFactory.True -> Set(Id("a"))))
    runUsesExample("!a;") should be (Map(FeatureExprFactory.True -> Set(Id("a"))))
    runUsesExample("~a;") should be (Map(FeatureExprFactory.True -> Set(Id("a"))))
    runUsesExample("-a;") should be (Map(FeatureExprFactory.True -> Set(Id("a"))))
    runUsesExample("+a;") should be (Map(FeatureExprFactory.True -> Set(Id("a"))))

    runUsesExample("a * b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a - b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a / b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a % b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a & b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a ^ b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))

    runUsesExample("a && b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a || b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a | b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a << b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a >> b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))

    runUsesExample("a = b;") should be(Map(FeatureExprFactory.True -> Set(Id("b"))))
    runUsesExample("a = b++;") should be(Map(FeatureExprFactory.True -> Set(Id("b"))))
    runUsesExample("a *= b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a += b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a -= b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a /= b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a %= b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a &= b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a ^= b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a |= b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a >>= b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a <<= b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))

    runUsesExample("a == b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a != b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a < b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a > b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a <= b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runUsesExample("a >= b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
  }

  @Test def test_defines() {
    runDefinesExample("a;") should be(Map())
    runDefinesExample("a++;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
    runDefinesExample("++a;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
    runDefinesExample("a[b];") should be(Map())
    runDefinesExample("f(a, b, c);") should be(Map())
    runDefinesExample("a.b;") should be(Map())
    runDefinesExample("a->b;") should be(Map())

    runDefinesExample("&a;") should be (Map())
    runDefinesExample("*a;") should be (Map())
    runDefinesExample("!a;") should be (Map())
    runDefinesExample("~a;") should be (Map())
    runDefinesExample("-a;") should be (Map())
    runDefinesExample("+a;") should be (Map())

    runDefinesExample("a * b;") should be (Map())
    runDefinesExample("a - b;") should be (Map())
    runDefinesExample("a / b;") should be (Map())
    runDefinesExample("a % b;") should be (Map())
    runDefinesExample("a & b;") should be (Map())
    runDefinesExample("a ^ b;") should be (Map())

    runDefinesExample("a && b;") should be (Map())
    runDefinesExample("a || b;") should be (Map())
    runDefinesExample("a | b;") should be (Map())
    runDefinesExample("a << b;") should be (Map())
    runDefinesExample("a >> b;") should be (Map())

    runDefinesExample("a = b;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
    runDefinesExample("a = b++;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
    runDefinesExample("a *= b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"))))
    runDefinesExample("a += b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"))))
    runDefinesExample("a -= b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"))))
    runDefinesExample("a /= b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"))))
    runDefinesExample("a %= b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"))))
    runDefinesExample("a &= b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"))))
    runDefinesExample("a ^= b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"))))
    runDefinesExample("a |= b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"))))
    runDefinesExample("a >>= b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"))))
    runDefinesExample("a <<= b;") should be (Map(FeatureExprFactory.True -> Set(Id("a"))))

    runDefinesExample("a == b;") should be (Map())
    runDefinesExample("a != b;") should be (Map())
    runDefinesExample("a < b;") should be (Map())
    runDefinesExample("a > b;") should be (Map())
    runDefinesExample("a <= b;") should be (Map())
    runDefinesExample("a >= b;") should be (Map())
  }

  // http://en.wikipedia.org/wiki/C_data_types
  @Test def test_declares() {
    runDeclaresExample("int a = 0;") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
    runDeclaresExample("int a, b = 0;") should be(Map(FeatureExprFactory.True -> Set(Id("a"), Id("b"))))
    runDeclaresExample("int a[10];") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
    runDeclaresExample("char *c;") should be(Map(FeatureExprFactory.True -> Set(Id("c"))))
    runDeclaresExample("float f;") should be(Map(FeatureExprFactory.True -> Set(Id("f"))))
    runDeclaresExample("""
      struct {
        int i;
      } s;""") should be(Map(FeatureExprFactory.True -> Set(Id("s"))))
    runDeclaresExample("""
      struct k {
        int i;
      } s;""") should be(Map(FeatureExprFactory.True -> Set(Id("s"))))
    runDeclaresExample("""
      struct k {
        int i;
      };""") should be(Map())
    runDeclaresExample("""
      struct k s;""") should be(Map(FeatureExprFactory.True -> Set(Id("s"))))
    runDeclaresExample("""
      union {
        int i;
      } u;""") should be(Map(FeatureExprFactory.True -> Set(Id("u"))))
  }

  @Test def test_useDeclareRelation() {
    println(runUseDeclareRelationExample("""
      void foo() {
        int a = 0;
      }"""))
    println(runUseDeclareRelationExample("""
      void foo() {
        int a = 0;
        int b = a;
        if (b) {
          int a = b;
          a;
        }
        b;
      }"""))
    println(runUseDeclareRelationExample("""
      void foo(int argc) {
        struct s {
          int i;
          int j;
        };

        struct s k;

        if (argc) {
          k.i = 0;
        } else {
          k.j = 1;
        }
        k.i = 2;
      }"""))
    println(runUseDeclareRelationExample("""
      void foo() {
        int a = 0;
        int b = a;
        if (b) {
          #if definedEx(A)
          int a = b;
          #endif
          a;
        }
        b;
      }"""))
    println(runUseDeclareRelationExample("""
      void foo() {
        int a = 0;
        if (a) {
          int a;
          a;
        }
      }"""))
    println(runUseDeclareRelationExample("""
      void foo() {
        int a = 0;
        if (a) { }
        else if (a) {
          int a;
          a;
        }
      }"""))
    println(runUseDeclareRelationExample("""
      void foo() {
        int a = 0;
        if (a) { }
        else {
          int a;
          a;
        }
      }"""))
    println(runUseDeclareRelationExample("""
      void foo() {
        int a = 0;
        do {
          int a;
          a;
        } while (a);
      }"""))
    println(runUseDeclareRelationExample("""
      void foo() {
        int a = 0;
        while (a) {
          int a;
          a;
        }
      }"""))
    println(runUseDeclareRelationExample("""
      void foo() {
        int a = 0;
        for (;a < 10;) {
          int a;
          a;
        }
      }"""))
    println(runUseDeclareRelationExample("""
      void foo() {
        int a = 0;
        if (a) {
          int a;
          a;
        }
      }"""))
    println(runUseDeclareRelationExample("""
      void foo() {
        int a = 0;
        if (a) { }
        else if (a) {
          #if definedEx(A)
          int a;
          #endif
          a;
        }
      }"""))
    println(runUseDeclareRelationExample("""
      void foo() {
        int a = 0;
        if (a) { }
        else {
          #if definedEx(A)
          int a;
          #endif
          a;
        }
      }"""))
    println(runUseDeclareRelationExample("""
      void foo() {
        int a = 0;
        do {
          #if definedEx(A)
          int a;
          #endif
          a;
        } while (a);
      }"""))
    println(runUseDeclareRelationExample("""
      void foo() {
        int a = 0;
        while (a) {
          #if definedEx(A)
          int a;
          #endif
          a;
        }
      }"""))
    println(runUseDeclareRelationExample("""
      void foo() {
        int a = 0;
        for (;a < 10;) {
          #if definedEx(A)
          int a;
          #endif
          a;
        }
      }"""))
    println(runUseDeclareRelationExample("""
      void foo() {
        int a = ({int a = 2; a + 2;}) + 3;
      }"""))
  }
}
