package de.fosd.typechef.typesystem.linker


import de.fosd.typechef.featureexpr.FeatureExprFactory.{False, True}
import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.{CFloat, CFunction, CVoid}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSuite, Matchers}

@RunWith(classOf[JUnitRunner])
class LinkerTest extends FunSuite with Matchers with TestHelper {

    val tfun = CFunction(Seq(), CVoid()) // () -> void
    val tfun2 = CFunction(Seq(CFloat()), CVoid()) // float -> void

    val ffoo = CSignature("foo", tfun, True, Seq())
    val ffoo2 = CSignature("foo", tfun2, True, Seq())
    val fbar = CSignature("bar", tfun, True, Seq())

    test("wellformed interfaces") {
        new CInterface(List(), List()).isWellformed should be(true)
        new CInterface(List(), List(ffoo)).isWellformed should be(true)
        new CInterface(List(), List(ffoo, CSignature("bar", tfun, True, Seq()))).isWellformed should be(true)
        new CInterface(List(), List(ffoo, ffoo)).isWellformed should be(false)
        new CInterface(List(), List(ffoo, CSignature("bar", tfun, True, Seq()), ffoo)).isWellformed should be(false)
        new CInterface(List(), List(ffoo, CSignature("foo", tfun2, True, Seq()))).isWellformed should be(false)
        new CInterface(List(), List(CSignature("foo", tfun, fa, Seq()), CSignature("foo", tfun, fa.not, Seq()))).isWellformed should be(true)
        new CInterface(List(ffoo), List(ffoo)).isWellformed should be(false)
        new CInterface(List(ffoo, ffoo), List()).isWellformed should be(false)
    }

    test("simple linking") {
        val i1 = new CInterface(List(), List(ffoo))
        val i2 = new CInterface(List(), List(fbar))
        val i3 = new CInterface(List(), List(ffoo, fbar))

        (i1 link i2) should be(i3)
        (i1 link i1).featureModel should be(False)

        ((i1 and fa) link (i1 and fa.not)).isWellformed should be(true)

        val ii = new CInterface(List(ffoo), List())
        (ii link ii).pack().deduplicateImports() should be(ii)
        ((ii and fa) link ii).pack().deduplicateImports() should be(ii)
        (ii link ii).pack().deduplicateImports().isWellformed should be(true)
        (ii link new CInterface(List(fbar), List())).pack() should be(new CInterface(List(ffoo, fbar), List()))

        (ii link i1).pack().deduplicateImports() should be(i1)

        (CInterface(fa, List(), List()) link CInterface(fb, List(), List())).pack() should be(CInterface(fa and fb, List(), List()))
        (CInterface(fa, List(), List()) link CInterface(fa.not, List(), List())).pack() should be(CInterface(False, List(), List()))
        (CInterface(fa, List(ffoo), List()) link CInterface(fa.not, List(), List())).pack() should be(CInterface(False, List(), List()))
        (CInterface(fa, List(), List(ffoo)) link CInterface(fa.not, List(), List())).pack() should be(CInterface(False, List(), List()))

        (new CInterface(List(), List(ffoo and fa)) link new CInterface(List(), List(ffoo and fb))).featureModel should be(fa mex fb)
    }

    test("complete and configured") {
        val i1 = new CInterface(List(), List(ffoo))

        i1.isComplete() should be(true)

        CInterface(True, List(), List(ffoo and fa)).isFullyConfigured() should be(false)
        CInterface(fa, List(), List(ffoo and fa)).isFullyConfigured() should be(true)

    }

    test("packing") {
        CInterface(fa.not, List(ffoo and fa), List(ffoo and fa)).pack() should be(CInterface(fa.not, List(), List()))
    }

    test("conditional composition (db example)") {
        val fwrite = CSignature("write", tfun, True, Seq())
        val fread = CSignature("read", tfun, True, Seq())
        val fselect = CSignature("select", tfun, True, Seq())
        val fupdate = CSignature("update", tfun, True, Seq())
        val idb = CInterface(True, List(fwrite, fread), List(fselect, fupdate))
        val iinmem = CInterface(True, List(), List(fwrite, fread))
        val iperist = iinmem //CInterface(True, List(),List(fwrite,fread))
        val ifm = CInterface(fa xor fb, List(), List())

        (idb isCompatibleTo iinmem) should be(true)
        (iperist isCompatibleTo iinmem) should be(false)
//        println((iperist.conditional(fa) link iinmem))
        ((iperist.conditional(fa) link iinmem).featureModel implies fa.not).isTautology should be(true)
        (iperist.conditional(fa) isCompatibleTo iinmem.conditional(fb)) should be(true)
        (idb link iinmem).isComplete() should be(true)
        (idb link iinmem).isFullyConfigured() should be(true)
        (idb link iperist.conditional(fa) link iinmem.conditional(fb)).isComplete() should be(false)

        val ifull = ifm link idb link iperist.conditional(fa) link iinmem.conditional(fb)

        ifull.isComplete() should be(true)
        ifull.isFullyConfigured() should be(false)
//        println(ifull)
    }

    test("module compatibility") {

        //exports must not overlap (independent of type)
        (new CInterface(List(), List(ffoo)) isCompatibleTo new CInterface(List(), List(ffoo2))) should be(false)
        (new CInterface(List(), List(ffoo)) isCompatibleTo new CInterface(List(), List(ffoo))) should be(false)
        (new CInterface(List(), List(ffoo)) isCompatibleTo new CInterface(List(), List(ffoo.and(fa)))) should be(true)

        //import export must match
        (new CInterface(List(ffoo), List()) isCompatibleTo new CInterface(List(), List(ffoo))) should be(true)
        (new CInterface(List(ffoo), List()) isCompatibleTo new CInterface(List(), List(ffoo2))) should be(false)
        (new CInterface(List(ffoo), List()) isCompatibleTo new CInterface(List(), List(ffoo2.and(fa)))) should be(true)
        (new CInterface(List(), List(ffoo2)) isCompatibleTo new CInterface(List(ffoo), List())) should be(false)

        //imports must match
        (new CInterface(List(ffoo), List()) isCompatibleTo new CInterface(List(ffoo), List())) should be(true)
        (new CInterface(List(ffoo), List()) isCompatibleTo new CInterface(List(ffoo2), List())) should be(false)
        (new CInterface(List(ffoo), List()) isCompatibleTo new CInterface(List(ffoo2.and(fa)), List())) should be(true)
    }

}