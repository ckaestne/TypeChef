package de.fosd.typechef.typesystem.linker


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr.FeatureExpr.{base, dead}
import de.fosd.typechef.typesystem.{CVoid, CFunction, CFloat}

@RunWith(classOf[JUnitRunner])
class LinkerTest extends FunSuite with ShouldMatchers with TestHelper {

    val tfun = CFunction(Seq(), CVoid())
    val tfun2 = CFunction(Seq(CFloat()), CVoid())

    test("wellformed interfaces") {
        new CInterface(List(), List()).isWellformed should be(true)
        new CInterface(List(), List(CSignature("foo", tfun, base, Seq()))).isWellformed should be(true)
        new CInterface(List(), List(CSignature("foo", tfun, base, Seq()), CSignature("bar", tfun, base, Seq()))).isWellformed should be(true)
        new CInterface(List(), List(CSignature("foo", tfun, base, Seq()), CSignature("foo", tfun, base, Seq()))).isWellformed should be(false)
        new CInterface(List(), List(CSignature("foo", tfun, base, Seq()), CSignature("bar", tfun, base, Seq()), CSignature("foo", tfun, base, Seq()))).isWellformed should be(false)
        new CInterface(List(), List(CSignature("foo", tfun, base, Seq()), CSignature("foo", tfun2, base, Seq()))).isWellformed should be(false)
        new CInterface(List(), List(CSignature("foo", tfun, fa, Seq()), CSignature("foo", tfun, fa.not, Seq()))).isWellformed should be(true)
    }

    val ffoo = CSignature("foo", tfun, base, Seq())
    val fbar = CSignature("bar", tfun, base, Seq())
    test("simple linking") {
        val i1 = new CInterface(List(), List(ffoo))
        val i2 = new CInterface(List(), List(fbar))
        val i3 = new CInterface(List(), List(ffoo, fbar))

        (i1 link i2) should be(i3)
        (i1 link i1).isWellformed should be(false)

        ((i1 and fa) link (i1 and fa.not)).isWellformed should be(true)

        val ii = new CInterface(List(ffoo), List())
        (ii link ii) should be(ii)
        ((ii and fa) link ii) should be(ii)
        (ii link ii).isWellformed should be(true)
        (ii link new CInterface(List(fbar), List())) should be(new CInterface(List(ffoo, fbar), List()))

        (ii link i1) should be(i1)

        (CInterface(fa, List(), List()) link CInterface(fb, List(), List())) should be(CInterface(fa and fb, List(), List()))
        (CInterface(fa, List(), List()) link CInterface(fa.not, List(), List())) should be(CInterface(dead, List(), List()))
        (CInterface(fa, List(ffoo), List()) link CInterface(fa.not, List(), List())) should be(CInterface(dead, List(), List()))
        (CInterface(fa, List(), List(ffoo)) link CInterface(fa.not, List(), List())) should be(CInterface(dead, List(), List()))
    }

    test("packing") {
        CInterface(fa.not, List(ffoo and fa), List(ffoo and fa)).pack should be(CInterface(fa.not, List(), List()))
    }

}