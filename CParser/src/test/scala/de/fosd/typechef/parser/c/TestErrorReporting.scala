package de.fosd.typechef.parser.c

import junit.framework._;
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._
import org.junit.Test
import java.util.Collections

class TestErrorReporting extends TestCase {
  //XXX duplicate of CGramFilesTest.parseFile
  def parseFile(fileName: String) {
    val inputStream = getClass.getResourceAsStream("/" + fileName)
    assertNotNull("file not found " + fileName, inputStream)
    val p = new CParser()
    val result = p.translationUnit(
      CLexer.lexStream(inputStream, fileName, Collections.singletonList("testfiles/cgram/"), null), FeatureExprFactory.True)
    System.out.println(result)
    (result: @unchecked) match {
      case p.Success(ast, unparsed) => {
        fail("should not succeed")
        //succeed
      }
      case p.NoSuccess(msg, unparsed, inner) =>
      case p.SplittedParseResult(_, _, p.NoSuccess(_, _, _)) =>
    }

  }

  //

  @Test
  def test1() {
    parseFile("errors/test.c")
  }

}
