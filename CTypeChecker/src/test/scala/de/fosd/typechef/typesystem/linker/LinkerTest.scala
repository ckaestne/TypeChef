package de.fosd.typechef.typesystem.linker


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr.FeatureExpr.base
import de.fosd.typechef.typesystem.{CVoid, CFunction, CFloat}

@RunWith(classOf[JUnitRunner])
class CTypesTest extends FunSuite with ShouldMatchers with TestHelper {

    val tfun = CFunction(Seq(), CVoid())
    val tfun2 = CFunction(Seq(CFloat()), CVoid())

    test("wellformed interfaces") {
        CInterface(List(), List()).isWellformed should be(true)
        CInterface(List(), List(CSignature("foo", tfun, base, Seq()))).isWellformed should be(true)
        CInterface(List(), List(CSignature("foo", tfun, base, Seq()), CSignature("bar", tfun, base, Seq()))).isWellformed should be(true)
        CInterface(List(), List(CSignature("foo", tfun, base, Seq()), CSignature("foo", tfun, base, Seq()))).isWellformed should be(false)
        CInterface(List(), List(CSignature("foo", tfun, base, Seq()), CSignature("bar", tfun, base, Seq()), CSignature("foo", tfun, base, Seq()))).isWellformed should be(false)
        CInterface(List(), List(CSignature("foo", tfun, base, Seq()), CSignature("foo", tfun2, base, Seq()))).isWellformed should be(false)
        CInterface(List(), List(CSignature("foo", tfun, fa, Seq()), CSignature("foo", tfun, fa.not, Seq()))).isWellformed should be(true)
    }

}