package de.fosd.typechef.crewrite

import org.junit.Test
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.parser.c._

class DoubleFreeTest extends TestHelper with ShouldMatchers with CFGHelper with EnforceTreeHelper {

    // check freed pointers
    private def getFreedMem(code: String) = {
        val a = parseCompoundStmt(code)
        val df = new DoubleFree(CASTEnv.createASTEnv(a), null, null, "")
        df.gen(a)
    }

    def doubleFree(code: String): Boolean = {
        val tunit = prepareAST[TranslationUnit](parseTranslationUnit(code))
        val df = new CAnalysisFrontend(tunit)
        df.doubleFree()
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
        doubleFree("""
              void* malloc(int i) { return ((void*)0); }
              void free(void* p) { }
              int foo() {
                  int fd;
                  if (fd) {
                      int *a;
                      int *buf;
                      free(buf);
                      free(a);
                  }
                  return 0;
              }
                      """.stripMargin) should be(true)
        doubleFree("""
                 void* malloc(int i) { return ((void*)0); }
                 void free(void* p) { }
                 void foo() {
                     int *a = malloc(2);
                     free(a);
                 #ifdef A
                     free(a);
                 #endif
                 } """) should be(false)
        doubleFree("""
              void* malloc(int i) { return ((void*)0); }
              void free(void* p) { }
              void foo() {
                  int *a = malloc(2);
                  free(a);
                  a = malloc(2);
                  free(a);
              }
            """.stripMargin) should be(true)
        doubleFree("""
              void* malloc(int i) { return ((void*)0); }
              void free(void* p) { }
              void foo() {
                  int *a = malloc(2);
                  free(a);
              #ifdef A
                  a = malloc(2);
              #endif
                  free(a);  // diagnostic
              }
                      """.stripMargin) should be(false)
        doubleFree("""
              void* malloc(int i) { return ((void*)0); }
              void free(void* p) { }
              void foo() {
                  int *a = malloc(2);
                  free(a);
              }
                      """.stripMargin) should be(true)
        doubleFree("""
              void* malloc(int i) { return ((void*)0); }
              void free(void* p) { }
              void* realloc(void* p, int i) { return ((void*)0); }
              void foo() {
                  int *a = malloc(2);
                  int *b = realloc(a, 3);
                  free(a);    // diagnostic
              }
                      """.stripMargin) should be(false)
        doubleFree("""
              void* malloc(int i) { return ((void*)0); }
              void free(void* p) { }
              void* realloc(void* p, int i) { return ((void*)0); }
              void foo() {
                  int *a = malloc(2);
              #ifdef A
                  int *b = realloc(a, 3);
              #else
                  free(a);
              #endif
              #ifdef A
                  free(b);
              #endif
              }
                      """.stripMargin) should be(true)
        // take from: https://www.securecoding.cert.org/confluence/display/seccode/MEM31-C.+Free+dynamically+allocated+memory+exactly+once
        doubleFree("""
              void* malloc(int i) { return ((void*)0); }
              void free(void* p) { }
              int f(int n) {
                  int error_condition = 0;

                  int *x = (int*)malloc(n * sizeof(int));
                  if (x == ((void*)0))
                      return -1;

                  /* Use x and set error_condition on error. */

                  if (error_condition == 1) {
                      /* Handle error condition*/
                      free(x);
                  }

                  /* ... */
                  free(x);   // diagnostic
                  return error_condition;
              }
                      """.stripMargin) should be(false)

    }
}
