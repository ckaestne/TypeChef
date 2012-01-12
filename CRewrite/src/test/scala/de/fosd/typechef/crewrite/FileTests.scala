package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.TestHelper
import de.fosd.typechef.featureexpr.{FeatureModel, NoFeatureModel}
import java.io.{FileNotFoundException, InputStream}
import org.junit.Test

class FileTests extends TestHelper with EnforceTreeHelper with CASTEnv {
  val folder = "/testfiles"

  private def check(filename: String, featureExpr: FeatureModel = NoFeatureModel) = {
    println("analysis " + filename)
    var inputStream: InputStream = getClass.getResourceAsStream("/" + folder + filename)

    if (inputStream == null)
      throw new FileNotFoundException("Input file not fould: " + filename)

    val ast = parseFile(inputStream, filename, folder)
    val nast = prepareAST(ast)
    val env = createASTEnv(nast)
    true
  }

  @Test def test1 {assert(check("test01.c"))}
}