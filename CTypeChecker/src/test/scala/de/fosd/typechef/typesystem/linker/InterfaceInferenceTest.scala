package de.fosd.typechef.typesystem.linker

import java.io.{FileNotFoundException, InputStream}

import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.parser.c.{TestHelper, TranslationUnit}
import org.scalatest.{FunSuite, Matchers}

class InterfaceInferenceTest extends FunSuite with TestHelper with Matchers {

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


  private def d(x: String) = FeatureExprFactory.createDefinedExternal(x)

  val ast = parse("mini.pi")
  val interface = new CInferInterface {}.inferInterface(ast)
//  println(interface)


  test("find imported function") {
    assert(interface.imports.exists(_.name == "bar"))
    assert(interface.imports.exists(_.name == "printf"))
    assert(interface.imports.exists(_.name == "local"))
  }


  test("foo declared but not used, should not show up in interface") {
    assert(!interface.imports.exists(_.name == "foo"))
    assert(!interface.imports.exists(_.name == "unusedlocal"))
  }
  test("local variables should not show up in interfaces") {
    assert(!interface.imports.exists(_.name == "a"))
  }
  test("conditionally called->conditionally imported") {
    assert(interface.imports.exists(x => x.name == "partiallyCalled" && (x.fexpr equivalentTo (d("PARTIAL") or d("P2")))))
  }
  test("main should be exported") {
    assert(interface.exports.exists(_.name == "main"))
    assert(interface.exports.exists(_.name == "foobar"))
    assert(interface.exports.exists(_.name == "partialA"))
    assert(interface.exports.exists(_.name == "partialB"))
  }
  test("exported methods should not be imported") {
    assert(!interface.imports.exists(_.name == "main"))
    assert(!interface.imports.exists(_.name == "foobar"))
    assert(!interface.imports.exists(_.name == "partialB"))
    assert(interface.imports.exists(_.name == "partialA"))
  }
  test("local variables should not be exported") {
    assert(!interface.exports.exists(_.name == "a"))
  }

  test("static functions should not be exported") {
    assert(!interface.exports.exists(_.name == "staticfun"))
    assert(!interface.imports.exists(_.name == "staticfun"))
    assert(!interface.exports.exists(_.name == "staticfunconditional"))
    assert(!interface.imports.exists(_.name == "staticfunconditional"))
    assert(interface.exports.exists(x => x.name == "partialstatic" && (x.fexpr equivalentTo (d("STAT").not))))
    assert(!interface.imports.exists(_.name == "partialstatic"))
  }

  test("test inline behavior") {
    //static inline and extern inline are not exported, whereas inline is
    //(see also http://stackoverflow.com/questions/216510/extern-inline)
    assert(interface.exports.exists(_.name == "inlinefun"))
    assert(!interface.exports.exists(_.name == "externinlinefun"))
    assert(!interface.exports.exists(_.name == "staticinlinefun"))
    assert(!interface.imports.exists(_.name == "inlinefun"))
    assert(interface.imports.exists(_.name == "externinlinefun"))
    assert(!interface.imports.exists(_.name == "staticinlinefun"))
  }
  test("parameters, function pointers") {
    //passing around function pointers does not constitute calling a method, dereferencing a function does
    assert(!interface.imports.exists(_.name == "fun_t_p"))
    assert(interface.imports.exists(_.name == "fun_t_b"))
    assert(interface.imports.exists(_.name == "funpoint"))
  }

    test("weak export annotations") {
        val expectedWeakExports = interface.exports.filter(_.name == "weaklyExportedFunction")
        assert(expectedWeakExports.size == 1)

        val weakExports = interface.exports.filter(_.extraFlags contains WeakExport)
        assert(expectedWeakExports == weakExports)
    }


}

