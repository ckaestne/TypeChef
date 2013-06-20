package de.fosd.typechef.parser.c

import junit.framework.Assert._
import de.fosd.typechef.featureexpr._
import org.kiama.rewriting.Rewriter._
import org.junit.{Assert, Ignore, Test}
import java.util.Collections

class PPFilesTest {
  def parseFile(fileName: String) {
    val inputStream = getClass.getResourceAsStream("/" + fileName)
    assertNotNull("file not found " + fileName, inputStream)
    val p = new CParser()
    val result = p.translationUnit(
      CLexer.lexStream(inputStream, fileName, Collections.singletonList("testfiles/boa/"), null), FeatureExprFactory.True)
    (result: @unchecked) match {
      case p.Success(ast, unparsed) => {
        val emptyLocation = checkPositionInformation(ast.asInstanceOf[Product])
        assertTrue("found nodes with empty location information", emptyLocation.isEmpty)
        assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
        //succeed
      }
      case p.NoSuccess(msg, unparsed, inner) =>
        println(unparsed.context)
        Assert.fail(msg + " at " + unparsed + " " + inner)
    }

  }

  def checkPositionInformation(ast: Product): List[Product] = {
    assert(ast != null)
    var nodeswithoutposition: List[Product] = List()
    val checkpos = everywherebu(query {
      case a: AST => if (!a.hasPosition) nodeswithoutposition ::= a
    })
    checkpos(ast)
    nodeswithoutposition
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