package de.fosd.typechef.typesystem.linker

import de.fosd.typechef.featureexpr.FeatureExprFactory.True
import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSuite, Matchers}

@RunWith(classOf[JUnitRunner])
class LinkerNormalizationTest extends FunSuite with Matchers with TestHelper {

    private def link(a: CType, b: CType): CInterface = {

        val _import = new CInterface(Seq(CSignature("foo", a, True, Nil)), Nil)
        val _export = new CInterface(Nil, Seq(CSignature("foo", b, True, Nil)))

        _import debug_join _export
    }

    val strictnessValues = Set(LINK_NAMEONLY, LINK_RELAXED, LINK_STRICT)

    private def assertEqualAfterNormalization(a: CType, b: CType, strictness: Strictness = LINK_STRICT) = assertEquality(a, b, true, strictness)

    private def assertUnequalAfterNormalization(a: CType, b: CType, strictness: Strictness = LINK_STRICT) = assertEquality(a, b, false, strictness)

    private def assertEquality(a: CType, b: CType, expectedEquality: Boolean, strictness: Strictness) {
        var strictnessLevels: Set[Strictness] = Set() + strictness
        if (strictnessLevels contains LINK_STRICT)
            strictnessLevels += LINK_RELAXED
        if (strictnessLevels contains LINK_RELAXED)
            strictnessLevels += LINK_NAMEONLY

        for (s <- strictnessValues) {
            val _linked = link(a, b)
            val expectedResult = if (strictnessLevels contains s) expectedEquality else !expectedEquality
            assert(_linked.isComplete(s) == expectedResult, _linked + (if (expectedResult) " is not complete" else " is complete") + " with " + s)
        }
    }


    test("equal after normalization even with STRICT") {
        assertEqualAfterNormalization(
            CFunction(Seq(), CVoid()),
            CFunction(Seq(), CVoid()),
            LINK_STRICT
        )

//        //void parameter
//        assertEqualAfterNormalization(
//            CFunction(Seq(CVoid()), CVoid()),
//            CFunction(Seq(), CVoid()),
//            LINK_STRICT
//        )

        //array vs pointer
        assertEqualAfterNormalization(
            CFunction(Seq(CPointer(CUnsigned(CInt()))), CVoid()),
            CFunction(Seq(CArray(CUnsigned(CInt()), -1)), CVoid()),
            LINK_STRICT
        )
        assertEqualAfterNormalization(
            CFunction(Seq(CPointer(CUnsigned(CInt()))), CVoid()),
            CFunction(Seq(CArray(CUnsigned(CInt()), 10)), CVoid()),
            LINK_STRICT
        )
    }

    test("equal after normalization only with relaxed") {
        assertEqualAfterNormalization(
            CFunction(Seq(CFloat()), CVoid()),
            CFunction(Seq(CPointer(CUnsigned(CInt()))), CVoid()),
            LINK_NAMEONLY
        )

        assertEqualAfterNormalization(
            CFunction(Seq(CPointer(CDouble())), CVoid()),
            CFunction(Seq(CPointer(CUnsigned(CInt()))), CVoid()),
            LINK_RELAXED
        )

        assertEqualAfterNormalization(
            CFunction(Seq(CPointer(CStruct("foo", false))), CVoid()),
            CFunction(Seq(CPointer(CVoid())), CVoid()),
            LINK_RELAXED
        )

        assertEqualAfterNormalization(
            CFunction(Seq(CUnsigned(CShort())), CVoid()),
            CFunction(Seq(CUnsigned(CChar())), CVoid()),
            LINK_RELAXED
        )
    }
}