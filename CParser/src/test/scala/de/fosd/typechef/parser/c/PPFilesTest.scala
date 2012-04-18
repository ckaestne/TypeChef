package de.fosd.typechef.parser.c

import org.junit.{Ignore, Test}
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._

class PPFilesTest {
    def parseFile(fileName: String) {
        val inputStream = getClass.getResourceAsStream("/" + fileName)
        assertNotNull("file not found " + fileName, inputStream)
        val p = new CParser()
        val result = p.translationUnit(
            CLexer.lexStream(inputStream, fileName, "testfiles/boa/", null), FeatureExprFactory.base)
        (result: @unchecked) match {
            case p.Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                //succeed
            }
            case p.NoSuccess(msg, unparsed, inner) =>
                println(unparsed.context)
                fail(msg + " at " + unparsed + " " + inner)
        }

    }

    //

    @Test
    def testEscapePi() {
        parseFile("boa/escape.pi")
    }

    @Test
    def testAliasPi() {
        parseFile("boa/alias.pi")
    }

    @Test
    @Ignore("not checked?")
    def testIpPi() {
        parseFile("boa/ip.pi")
    }

    @Test
    def testLineEditPi() {
        parseFile("other/lineedit.pi")
    }

}