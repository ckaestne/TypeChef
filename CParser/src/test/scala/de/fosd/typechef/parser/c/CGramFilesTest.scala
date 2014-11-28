package de.fosd.typechef.parser.c

import junit.framework._;
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._
import org.junit.Test
import org.kiama.rewriting.Rewriter._
import java.util.Collections


class CGramFilesTest extends TestCase with TestHelper {
  val p = new CParser()

  //XXX duplicate of TestErrorReportingTest.parseFile
  def parseFile(fileName: String) {
    val inputStream = getClass.getResourceAsStream("/" + fileName)
    println(inputStream.toString)
    assertNotNull("file not found " + fileName, inputStream)
    val result = p.phrase(p.translationUnit)(
      lexStream(inputStream, fileName, Collections.emptyList(), null), FeatureExprFactory.True)
    System.out.println(result)
    (result: @unchecked) match {
      case p.Success(ast, unparsed) => {
        val emptyLocation = checkPositionInformation(ast.asInstanceOf[Product])
        assertTrue("found nodes with empty location information", emptyLocation.isEmpty)
        assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
        //succeed
      }
      case p.NoSuccess(msg, unparsed, inner) =>
        Assert.fail(msg + " at " + unparsed + " " + inner)
    }

  }

  def checkPositionInformation(ast: Product): List[Product] = {
    assert(ast != null)
    var nodeswithoutposition: List[Product] = List()
    val checkpos = everywherebu(query[Product] {
      case a: AST => if (!a.hasPosition) nodeswithoutposition ::= a
    })
    checkpos(ast)
    nodeswithoutposition
  }

  //

  @Test
  def test1() {
    parseFile("cgram/test.c")
  }

  def test2() {
    parseFile("cgram/test2.c")
  }

  def test3() {
    parseFile("cgram/test3.c")
  }

  def test4() {
    parseFile("cgram/test4.c")
  }

  def test5() {
    parseFile("cgram/test5.c")
  }

  def test6() {
    parseFile("cgram/test6.c")
  }

  def test7() {
    parseFile("cgram/test7.c")
  }

  def ignoretest8() {
    parseFile("cgram/test8.c")
  }

  //scoped typedef
  def test9() {
    parseFile("cgram/test9.c")
  }

  def test10() {
    parseFile("cgram/test10.c")
  }

  def test11() {
    parseFile("cgram/test11.c")
  }

  def test12() {
    parseFile("cgram/test12.c")
  }

  def test13() {
    parseFile("cgram/test13.c")
  }

  def test14() {
    parseFile("cgram/test14.c")
  }

  def test15() {
    parseFile("cgram/test15.c")
  }

  def test16() {
    parseFile("cgram/test16.c")
  }

  def test17() {
    parseFile("cgram/test17.c")
  }

  def test18() {
    parseFile("cgram/test18.c")
  }

  def test19() {
    parseFile("cgram/test19.c")
  }

  def test20() {
    parseFile("cgram/test20.c")
  }

  def test21() {
    parseFile("cgram/test21.c")
  }

  def test22() {
    parseFile("cgram/test22.c")
  }

  def test23() {
    parseFile("cgram/test23.c")
  }

  def test24() {
    parseFile("cgram/test24.c")
  }

  def test25() {
    parseFile("cgram/test25.c")
  }

  def test26() {
    parseFile("cgram/test26.c")
  }

  def test27() {
    parseFile("cgram/test27.c")
  }

  def test28() {
    parseFile("cgram/test28.c")
  }

  def test29() {
    parseFile("cgram/test29.c")
  }

  def test30() {
    parseFile("cgram/test30.c")
  }

  def test31() {
    parseFile("cgram/test31.c")
  }

  def test32() {
    parseFile("cgram/test32.c")
  }

  def test33() {
    parseFile("cgram/test33.c")
  }

  def test34() {
    parseFile("cgram/test34.c")
  }

  def test35() {
    parseFile("cgram/test35.c")
  }

  def test36() {
    parseFile("cgram/test36.c")
  }

  def test37() {
    parseFile("cgram/test37.c")
  }

  def test38() {
    parseFile("cgram/test38.c")
  }

  def test39() {
    parseFile("cgram/test39.c")
  }

  def test40() {
    parseFile("cgram/test40.c")
  }

  def test41() {
    parseFile("cgram/test41.c")
  }

  def ignoretest42() {
    parseFile("cgram/test42.c")
  }

  //ignore variable and typedef with same name
  def test43() {
    parseFile("cgram/test43.c")
  }

  def test44() {
    parseFile("cgram/test44.c")
  }

  def test45() {
    parseFile("cgram/test45.c")
  }

  def test46() {
    parseFile("cgram/test46.c")
  }

  def test47() {
    parseFile("cgram/test47.c")
  }

  def test48() {
    parseFile("cgram/test48.c")
  }

  def test49() {
    parseFile("cgram/test49.c")
  }

  def test50() {
    parseFile("cgram/test50.c")
  }

  def test51() {
    parseFile("cgram/test51.c")
  }

  def test52() {
    parseFile("cgram/test52.c")
  }

  def test53() {
    parseFile("cgram/test53.c")
  }

  def test54() {
    parseFile("cgram/test54.c")
  }

  def test55() {
    parseFile("cgram/test55.c")
  }

  def test56() {
    parseFile("cgram/test56.c")
  }

  def test57() {
    parseFile("cgram/test57.c")
  }

  def test58() {
    parseFile("cgram/test58.c")
  }

  def test59() {
    parseFile("cgram/test59.c")
  }

  def test60() {
    parseFile("cgram/test60.c")
  }

  def test61() {
    parseFile("cgram/test61.c")
  }

  def test62() {
    parseFile("cgram/test62.c")
  }

  def test63() {
    parseFile("cgram/test63.c")
  }

  def test64() {
    parseFile("cgram/test64.c")
  }

  def test65() {
    parseFile("cgram/test65.c")
  }

  def test66() {
    parseFile("cgram/test66.c")
  }

  def test67() {
    parseFile("cgram/test67.c")
  }

  def test68() {
    parseFile("cgram/test68.c")
  }

  def test69() {
    parseFile("cgram/test69.c")
  }

  def test70() {
    parseFile("cgram/test70.c")
  }

  def test71() {
    parseFile("cgram/test71.c")
  }

  def test72() {
    parseFile("cgram/test72.c")
  }

  def test73() {
    parseFile("cgram/test73.c")
  }

  def test74() {
    parseFile("cgram/test74.c")
  }

  def test75() {
    parseFile("cgram/test75.c")
  }

  def test76() {
    parseFile("cgram/test76.c")
  }

  def test77() {
    parseFile("cgram/test77.c")
  }

  def test78() {
    parseFile("cgram/test78.c")
  }

  def test79() {
    parseFile("cgram/test79.c")
  }

  def test80() {
    parseFile("cgram/test80.c")
  }

  def test81() {
    parseFile("cgram/test81.c")
  }

  def test83() {
    parseFile("cgram/test83.c")
  }

  def test84() {
    parseFile("cgram/test84.c")
  }

  def test85() {
    parseFile("cgram/test85.c")
  }

  def test86() {
    parseFile("cgram/test86.c")
  }

  def test87() {
    parseFile("cgram/test87.c")
  }

  def testparamstruct() {
    parseFile("errors/parameterstruct.c")
  }
}