package de.fosd.typechef.typesystem.linker

import org.junit._
import de.fosd.typechef.parser.c.{TestHelper, TranslationUnit}
import java.io.{File, InputStream, FileNotFoundException}
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory}
import FeatureExprFactory._

class InterfaceInferenceDeadCodeDetectionTest extends TestHelper {

  val folder = "testfiles/"

  private def parse(filename: String): TranslationUnit = {
    val start = System.currentTimeMillis
    println("parsing " + filename)
    var inputStream: InputStream = getClass.getResourceAsStream("/" + folder + filename)
    if (inputStream == null) {
      throw new FileNotFoundException("Input file not found: " + filename)
    }
    val ast = parseFile(inputStream, filename, folder)
    val parsed = System.currentTimeMillis
    println("parsed " + filename + " (" + (parsed - start) + ")")
    ast
  }


  private def checkSerialization(i: CInterface) {
    val inf = new InterfaceWriter {}
    val f = new File("tmp.interface")
    inf.writeInterface(i, f)
    val interface2 = inf.readInterface(f)


    //
    assert(i equals interface2)
    assert(!(i eq interface2))

    f.delete()
  }


  @Test
  def testDeadCodeDetection {
    val ast = parse("deadcode.pi")
    val interface = new CInferInterface {}.inferInterface(ast)
//    println(interface)
    checkSerialization(interface)

    def whenImported(s: String): FeatureExpr = interface.imports.filter(_.name == s).map(_.fexpr).fold(False)(_ or _)

    def assertEquivalent(actual: FeatureExpr, expected: FeatureExpr) =
      assert(actual equivalentTo expected, "expected " + expected + ", but found " + actual)

    assertEquivalent(whenImported("activefunction"), True)
    assertEquivalent(whenImported("activefunction2"), True)
    assert(!interface.imports.exists(_.name == "deadfunction"))
    assertEquivalent(whenImported("sometimesdead"), fx)
    assertEquivalent(whenImported("sometimesdead2"), fx.not)
    assertEquivalent(whenImported("sometimesdead3"), fx)
    assertEquivalent(whenImported("sometimesdead4"), fx.not)
    assertEquivalent(whenImported("sometimesdeadAB"), (fx and fy))


    assertEquivalent(whenImported("i3"), True)
    assertEquivalent(whenImported("i1"), fx.not)
    assertEquivalent(whenImported("i2"), ((fx.not and fy).not))

    assertEquivalent(whenImported("t1"), fx)
    assertEquivalent(whenImported("t2"), (fx orNot fy).not)
    assertEquivalent(whenImported("t3"), (fx or fy).not)

    assertEquivalent(whenImported("s1"), fx)
    assertEquivalent(whenImported("s2"), False)
    assertEquivalent(whenImported("s3"), fx.not)

    assertEquivalent(whenImported("ignoresizeof"), False)
    assertEquivalent(whenImported("ignoresizeofElse"), False)
    assertEquivalent(whenImported("BUG_bad_PRIO_PROCESS"), False)

    assertEquivalent(whenImported("c1"), fx)
    assertEquivalent(whenImported("c2"), fx.not)

    assertEquivalent(whenImported("deadsizeof1"), False)
    assertEquivalent(whenImported("deadsizeof2"), False)

    assertEquivalent(whenImported("deadByEnum"), False)

    assert(interface.exports.exists(_.name == "main"))
  }


  //       @Test
  //    def testFork {
  //        val ast = parse("fork_.pi")
  //        val interface = new CInferInterface {}.inferInterface(ast)
  //        println(interface)
  //    }
}