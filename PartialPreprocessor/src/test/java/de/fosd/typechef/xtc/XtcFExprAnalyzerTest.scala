package de.fosd.typechef.xtc

import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory}
import de.fosd.typechef.xtclexer.XtcFExprAnalyzer
import org.junit.{Assert, Test}

/**
 * Created by ckaestne on 11/28/14.
 */
class XtcFExprAnalyzerTest {

    private def assertExpr(s: String, f: FeatureExpr): Unit = {
        Assert.assertEquals(f, new XtcFExprAnalyzer().resolveFExpr(s))
    }
    private def d(s: String) = FeatureExprFactory.createDefinedExternal(s)

    @Test def testExpr1 {
        assertExpr(" (((defined X) ? 3 : 0) == 3)", d("X"))
        assertExpr(" (((defined X)?3:0) == 3)", d("X"))
    }

}
