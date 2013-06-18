package de.fosd.typechef.crewrite

import org.junit.Test
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.typesystem._
import de.fosd.typechef.parser.c._

class DanglingSwitchCodeTest extends TestHelper with ShouldMatchers with CFGHelper {

    def danglingSwitchCode(code: String): Boolean = {
        val tunit = parseTranslationUnit(code)
        val ts = new CTypeSystemFrontend(tunit, FeatureExprFactory.empty) with CDeclUse
        assert(ts.checkASTSilent, "typecheck fails!")
        val env = CASTEnv.createASTEnv(tunit)
        val udm = ts.getUseDeclMap

        val fdefs = filterAllASTElems[FunctionDef](tunit)
        val errors = fdefs.flatMap(danglingSwitchCode(_, env, udm))

        if (errors.isEmpty) {
            println("No dangling code in switch statements found!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        !errors.isEmpty
    }

    private def danglingSwitchCode(f: FunctionDef, env: ASTEnv, udm: UseDeclMap): List[AnalysisError] = {
        var res: List[AnalysisError] = List()

        val ss = filterAllASTElems[SwitchStatement](f)

        for (s <- ss) {
            val ds = new DanglingSwitchCode(env, FeatureExprFactory.empty).computeDanglingCode(s)

            if (! ds.isEmpty) {
                for (e <- ds)
                    res ::= new AnalysisError(e.feature, "warning: switch statement has dangling code ", e.entry)
            }
        }

        res
    }

    @Test def test_danglingswitch_simple() {
        danglingSwitchCode( """
               void f(void) {
                  int a;
                  switch (a) {
                    a = a+1;
                    case 0: a+2;
                    default: a+3;
                  }
               }
        """.stripMargin) should be(true)
    }

    danglingSwitchCode( """
               void f(void) {
                  int a;
                  switch (a) {
                    case 0: a+2;
                    default: a+3;
                  }
               }
    """.stripMargin) should be(false)

    danglingSwitchCode( """
               void f(void) {
                  int a;
                  switch (a) {
                    #ifdef A
                    a++;
                    #endif
                    case 0: a+2;
                    default: a+3;
                  }
               }
    """.stripMargin) should be(true)

    danglingSwitchCode( """
               void f(void) {
                  int a;
                  #ifdef A
                  switch (a) {
                    #ifndef A
                    a++;
                    #endif
                    case 0: a+2;
                    default: a+3;
                  }
                  #endif
               }
    """.stripMargin) should be(false)
}

