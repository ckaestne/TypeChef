package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.{FunctionDef, Id, TestHelper}
import org.junit.Test
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.featureexpr.FeatureExprFactory

class DoubleFreeTest extends TestHelper with ShouldMatchers with CFGHelper {

    // check freed pointers
    private def getFreedMem(code: String) = {
        val a = parseCompoundStmt(code)
        val df = new DoubleFree(CASTEnv.createASTEnv(a), null, null, "")
        df.gen(a)
    }

    // intraprocedural check of double freeing pointers
    // we check whether a freed memory cell is freed again
    private def hasDoubleFree(code: String): Boolean = {
        val f = parseFunctionDef(code)
        var res = false
        val df = new DoubleFree(CASTEnv.createASTEnv(f), null, null, "")

        val env = CASTEnv.createASTEnv(f)
        val ss = getAllSucc(f, FeatureExprFactory.empty, env)

        val nss = ss.map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])
        for (s <- nss) {

            val g = df.gen(s)
            val out = df.out(s)

            for ((i, _) <- out)
                for ((_, j) <- g)
                    if (j.contains(i))
                        res = true

        }
        res
    }

    @Test def test_free() {
        getFreedMem("{ free(a); }") should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        getFreedMem(
            """
            {
              #ifdef A
              free(a);
              #endif
            }
            """.stripMargin) should be(Map(fa -> Set(Id("a"))))
        getFreedMem(
            """
            {
              free(
              #ifdef A
              a
              #else
              b
              #endif
              );
            }
            """.stripMargin) should be(Map(fa -> Set(Id("a")), fa.not() -> Set(Id("b"))))
        getFreedMem(
            """
            {
              realloc(a, 2);
            }
            """.stripMargin) should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        getFreedMem(
            """
            {
              realloc(
            #ifdef A
              a
            #else
              b
            #endif
              , 2);
            }
            """.stripMargin) should be(Map(fa -> Set(Id("a")), fa.not() -> Set(Id("b"))))
        getFreedMem(
            """
            {
              realloc(
              a,
            #ifdef A
              sizeof(struct g)
            #else
              sizeof(struct g2)
            #endif
            );
            }
            """.stripMargin) should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        getFreedMem(""" { free(a->b); } """.stripMargin) should be(Map(FeatureExprFactory.True -> Set(Id("b"))))
        getFreedMem(""" { free(&(a->b)); } """.stripMargin) should be(Map(FeatureExprFactory.True -> Set(Id("b"))))
        getFreedMem(""" { free(*(a->b)); } """.stripMargin) should be(Map(FeatureExprFactory.True -> Set(Id("b"))))
        getFreedMem(""" { free(a->b->c); } """.stripMargin) should be(Map(FeatureExprFactory.True -> Set(Id("c"))))
        getFreedMem(""" { free(a.b); } """.stripMargin) should be(Map(FeatureExprFactory.True -> Set(Id("b"))))
        getFreedMem(""" { free(a[i]); }""".stripMargin) should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        getFreedMem(""" { free(*a); }""".stripMargin) should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        getFreedMem(""" { free(&a); }""".stripMargin) should be(Map(FeatureExprFactory.True -> Set(Id("a"))))
        getFreedMem(""" { free(a[i]->b); }""".stripMargin) should be(Map(FeatureExprFactory.True -> Set(Id("b"))))
    }

    @Test def test_double_free_simple() {
        hasDoubleFree("""void foo() {
                 int *a = malloc(2);
                 free(a);
                 #ifdef A
                 free(a);
                 #endif
                 } """) should be(true)
        hasDoubleFree(
            """
              void foo() {
              int *a = malloc(2);
              free(a);
              a = malloc(2);
              free(a);
              }
            """.stripMargin) should be(false)
        hasDoubleFree(
            """
              void foo() {
              int *a = malloc(2);
              free(a);
              #ifdef A
              a = malloc(2);
              #endif
              free(a);
              }
            """.stripMargin) should be(true)
        hasDoubleFree(
            """
              void foo() {
              int *a = malloc(2);
              free(a);
              }
            """.stripMargin) should be(false)
        hasDoubleFree(
            """
              void foo() {
              int *a = malloc(2);
              int *b = realloc(a, 3);
              free(a);
              }
            """.stripMargin) should be(true)
        hasDoubleFree(
            """
              void foo() {
              int *a = malloc(2);
              #ifdef A
              int *b = realloc(a, 3);
              #else
              free(a);
              #endif
              free(b);
              }
            """.stripMargin) should be(false)
        // take from: https://www.securecoding.cert.org/confluence/display/seccode/MEM31-C.+Free+dynamically+allocated+memory+exactly+once
        hasDoubleFree(
            """
              int f(int n) {
                int error_condition = 0;

                int *x = (int*)malloc(n * sizeof(int));
                if (x == NULL)
                  return -1;

                /* Use x and set error_condition on error. */

                if (error_condition == 1) {
                  /* Handle error condition*/
                  free(x);
                }

                /* ... */
                free(x);
                return error_condition;
              }
            """.stripMargin) should be(true)
    }
}
