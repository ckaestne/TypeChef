package de.fosd.typechef.lexer.xtc

import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory}
import de.fosd.typechef.xtclexer.XtcFExprAnalyzer
import org.junit.{Assert, Test}


class XtcFExprAnalyzerTest {

    private def assertExpr(s: String, f: FeatureExpr): Unit = {
        Assert.assertEquals(f, new XtcFExprAnalyzer().resolveFExpr(s))
    }
    private def d(s: String) = FeatureExprFactory.createDefinedExternal(s)

    @Test def testExpr1 {
        assertExpr(" (((defined X) ? 3 : 0) == 3)", d("X"))
        assertExpr(" (((defined X)?3:0) == 3)", d("X"))
    }

    @Test def testInvalid: Unit = {
        //should produce a warning and substitute constant by 0
        assertExpr("BLK_MAX_CDB", FeatureExprFactory.False)
        assertExpr("(16 > BLK_MAX_CDB)", FeatureExprFactory.True)
    }

}
