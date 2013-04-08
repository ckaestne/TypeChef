package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.{FunctionDef, TestHelper}
import org.junit.Test
import java.io.{StringWriter, FileNotFoundException, InputStream}
import de.fosd.typechef.featureexpr.FeatureExprFactory

class InterCFGTest extends InterCFG with TestHelper with NoFunctionLookup with CFGHelper {

  @Test def test_two_functions() {
    val folder = "testfiles/"
    val filename = "intercfgtest01.c"
    val is: InputStream = getClass.getResourceAsStream("/" + folder + filename)
    if (is == null)
      throw new FileNotFoundException("Input file not fould!")

    val ast = parseFile(is, folder, filename)
    val env = CASTEnv.createASTEnv(ast)

    val fdefs = filterAllASTElems[FunctionDef](ast)

    val a = new StringWriter()
    val dot = new DotGraph(a)
    dot.writeHeader("test")

    val fsuccs = fdefs.map(getAllSucc(_, FeatureExprFactory.empty, env))

    for (s <- fsuccs)
      dot.writeMethodGraph(s, env, Map())
    dot.writeFooter()
    dot.close()
    println(a)
  }
}
