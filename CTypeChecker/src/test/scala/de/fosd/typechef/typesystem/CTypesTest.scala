package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class CTypesTest extends FunSuite with ShouldMatchers with CTypes {

    //    test("basic types equality") {
    //        CSigned(CInt()) should equal (CSigned(CInt()))
    //        CSigned(CInt()) should equal (CInt())
    //        CInt() should equal (CSigned(CInt()))
    //   }

    test("wellformed types") {
        val sEnv: StructEnv = Map(
            ("wf1" -> Seq(("a", CFloat()))),
            ("wf2" -> Seq(("a", CFloat()), ("b", CDouble()))),
            ("wf3" -> Seq(("a", CPointer(CStruct("wf2"))), ("b", CDouble()))),
            ("wf4" -> Seq(("a", CPointer(CStruct("wf2"))), ("b", CPointer(CStruct("wf4"))))),
            ("nwf1" -> Seq(("a", CFloat()), ("a", CDouble()))),
            ("nwf2" -> Seq(("a", CVoid()), ("b", CDouble()))),
            ("nwf3" -> Seq())
        )
        val tEnv: PtrEnv = Set("Str", "wf2")
        val wf = wellformed(sEnv, tEnv, _: CType) should be(true)
        val nwf = wellformed(sEnv, tEnv, _: CType) should be(false)

        wf(CSigned(CInt()))
        wf(CSigned(CChar()))
        wf(CUnsigned(CInt()))
        wf(CVoid())
        wf(CDouble())
        wf(CLongDouble())
        wf(CFloat())
        wf(CPointer(CInt())) //implicit conv.
        wf(CPointer(CFloat()))
        wf(CPointer(CStruct("Str")))
        nwf(CPointer(CStruct("NoStr")))
        wf(CArray(CDouble(), 3))
        nwf(CArray(CVoid(), 3))
        nwf(CArray(CDouble(), 0))
        wf(CFunction(Seq(), CVoid()))
        wf(CFunction(Seq(CInt()), CVoid()))
        wf(CFunction(Seq(CInt(), CDouble()), CVoid()))
        nwf(CFunction(Seq(CPointer(CStruct("NoStr"))), CVoid()))
        nwf(CFunction(Seq(CVoid()), CVoid()))
        nwf(CFunction(Seq(CArray(CDouble(), 2)), CVoid()))
        nwf(CFunction(Seq(CDouble()), CArray(CDouble(), 2)))
        wf(CStruct("wf1"))
        wf(CStruct("wf2"))
        wf(CStruct("wf3"))
        wf(CStruct("wf4"))
        nwf(CStruct("nwf1"))
        nwf(CStruct("nwf2"))
        nwf(CStruct("nwf3"))
    }


}