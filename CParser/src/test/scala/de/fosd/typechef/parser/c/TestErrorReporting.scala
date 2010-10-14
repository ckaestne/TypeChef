package de.fosd.typechef.parser.c
import junit.framework.TestCase

import org.junit._
import junit.framework._;
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._
import org.junit.Test

class TestErrorReporting extends TestCase {
  
  def parseFile(fileName: String) {
    	val inputStream = getClass.getResourceAsStream("/"+fileName)
    	assertNotNull("file not found "+fileName,inputStream)
        val result = new CParser().translationUnit(
            CLexer.lexStream(inputStream, "testfiles/cgram/"), FeatureExpr.base
            )
        System.out.println(result)
        (result: @unchecked) match {
            case Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                //succeed
            }
            case NoSuccess(msg, context, unparsed, inner) =>
                fail(msg + " at " + unparsed + " with context " + context + " " + inner)
        }

    }

    // 

    @Test
    def test1() { parseFile("errors/test.c") }
  

}
