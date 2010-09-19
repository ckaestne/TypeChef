package de.fosd.typechef.parser.c

import junit.framework._;
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._

class BoaFilesTest extends TestCase {
    def parseFile(fileName: String) {
        val result = new CParser().translationUnit(
            CLexer.lexFile(fileName, "testfiles/cgram/"), FeatureExpr.base
            )
        System.out.println(result)
        result match {
            case Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream ("+unparsed.first.getPositionStr+"): " + unparsed, unparsed.atEnd)
                //succeed
            }
            case NoSuccess(msg, context, unparsed, inner) =>
                fail(msg + " at " + unparsed + " with context " + context + " " + inner)
        }

    }

    //
    def testDummy{}
    def notest1() { parseFile("../boa/src/hash.pi") }

}