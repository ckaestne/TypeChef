package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import org.junit.Test
import java.io.{StringWriter, FileNotFoundException, InputStream}
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory}
import de.fosd.typechef.parser.c.TranslationUnit
import de.fosd.typechef.parser.c.FunctionDef

class InterCFGTest extends InterCFG with TestHelper with CFGHelper {

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

      def lookupFExpr(e: AST): FeatureExpr = e match {
          case o if env.isKnown(o) => env.featureExpr(o)
          case _ => FeatureExprFactory.True
      }

    for (s <- fsuccs)
      dot.writeMethodGraph(s, lookupFExpr, "")
    dot.writeFooter()
    dot.close()
    println(a)
  }
    // provide a lookup mechanism for function defs (from the type system or selfimplemented)
    def getTranslationUnit(): TranslationUnit = null
}
