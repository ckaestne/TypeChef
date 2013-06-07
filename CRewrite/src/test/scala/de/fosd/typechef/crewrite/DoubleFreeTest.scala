package de.fosd.typechef.crewrite

import org.junit.Test
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.typesystem._
import de.fosd.typechef.parser.c._
import java.io.{FileWriter, File}

class DoubleFreeTest extends TestHelper with ShouldMatchers with CFGHelper {

    // check freed pointers
    private def getFreedMem(code: String) = {
        val a = parseCompoundStmt(code)
        val df = new DoubleFree(CASTEnv.createASTEnv(a), null, null, "")
        df.gen(a)
    }

    def hasDoubleFree(code: String): Boolean = {
        val tunit = parseTranslationUnit(code)
        val ts = new CTypeSystemFrontend(tunit, FeatureExprFactory.empty) with CDeclUse
        assert(ts.checkASTSilent, "typecheck fails!")
        val env = CASTEnv.createASTEnv(tunit)
        val udm = ts.getUseDeclMap

        val fdefs = filterAllASTElems[FunctionDef](tunit)
        val errors = fdefs.flatMap(doubleFreeFunctionDef(_, env, udm, ""))

        if (errors.isEmpty) {
            println("No double frees found!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        ! errors.isEmpty
    }

    // intraprocedural check of double freeing pointers
    // we check whether a freed memory cell is freed again
    private def doubleFreeFunctionDef(f: FunctionDef, env: ASTEnv, udm: UseDeclMap, casestudy: String): List[AnalysisError] = {
        var res: List[AnalysisError] = List()

        // It's ok to use FeatureExprFactory.empty here.
        // Using the project's fm is too expensive since control
        // flow computation requires a lot of sat calls.
        // We use the proper fm in DoubleFree (see MonotoneFM).
        val ss = getAllSucc(f, FeatureExprFactory.empty, env).reverse
        val df = new DoubleFree(env, udm, FeatureExprFactory.empty, casestudy)
        val nss = ss.map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])

        for (s <- nss) {
            val g = df.gen(s)
            val out = df.out(s)

            for ((i, h) <- out)
                for ((f, j) <- g)
                    j.find(_ == i) match {
                        case None =>
                        case Some(x) => {
                            val xdecls = udm.get(x)
                            val idecls = udm.get(i)

                            for (ei <- idecls)
                                if (xdecls.exists(_.eq(ei)))
                                    res ::= new AnalysisError(h, "warning: Try to free a memory block that has been released", x)
                        }
                    }
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
        hasDoubleFree("""
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
                      """.stripMargin) should be(false)
        hasDoubleFree("""
                 void* malloc(int i) { return ((void*)0); }
                 void free(void* p) { }
                 void foo() {
                     int *a = malloc(2);
                     free(a);
                 #ifdef A
                     free(a);
                 #endif
                 } """) should be(true)
        hasDoubleFree("""
              void* malloc(int i) { return ((void*)0); }
              void free(void* p) { }
              void foo() {
                  int *a = malloc(2);
                  free(a);
                  a = malloc(2);
                  free(a);
              }
            """.stripMargin) should be(false)
        hasDoubleFree("""
              void* malloc(int i) { return ((void*)0); }
              void free(void* p) { }
              void foo() {
                  int *a = malloc(2);
                  free(a);
              #ifdef A
                  a = malloc(2);
              #endif
                  free(a);
              }
                      """.stripMargin) should be(true)
        hasDoubleFree("""
              void* malloc(int i) { return ((void*)0); }
              void free(void* p) { }
              void foo() {
                  int *a = malloc(2);
                  free(a);
              }
                      """.stripMargin) should be(false)
        hasDoubleFree("""
              void* malloc(int i) { return ((void*)0); }
              void free(void* p) { }
              void* realloc(void* p, int i) { return ((void*)0); }
              void foo() {
                  int *a = malloc(2);
                  int *b = realloc(a, 3);
                  free(a);
              }
                      """.stripMargin) should be(true)
        hasDoubleFree("""
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
                      """.stripMargin) should be(false)
        // take from: https://www.securecoding.cert.org/confluence/display/seccode/MEM31-C.+Free+dynamically+allocated+memory+exactly+once
        hasDoubleFree("""
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
                  free(x);
                  return error_condition;
              }
                      """.stripMargin) should be(true)

    }
}
