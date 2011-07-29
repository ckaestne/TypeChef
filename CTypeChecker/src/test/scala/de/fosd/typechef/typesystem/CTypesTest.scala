package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.FeatureExpr.base
import de.fosd.typechef.featureexpr.FeatureExpr

@RunWith(classOf[JUnitRunner])
class CTypesTest extends FunSuite with ShouldMatchers with CTypes with CExprTyping with CStmtTyping {

    test("wellformed types") {
        val sEnv: StructEnv = new StructEnv(Map(
            (("wf1", false) -> (new ConditionalTypeMap() + ("a", base, TOne(CFloat())))),
            (("wf2", true) -> (new ConditionalTypeMap() + ("a", base, TOne(CFloat())) + ("b", base, TOne(CDouble())))), //union
            (("wf3", false) -> (new ConditionalTypeMap() + ("a", base, TOne(CPointer(CStruct("wf2")))) + ("b", base, TOne(CDouble())))),
            (("wf4", false) -> (new ConditionalTypeMap() + ("a", base, TOne(CPointer(CStruct("wf2")))) + ("b", base, TOne(CPointer(CStruct("wf4")))))),
            //            (("nwf1", false) -> new ConditionalTypeMap(Map("a" -> Seq((base, CFloat()), (base, CDouble()))))),
            (("nwf2", false) -> (new ConditionalTypeMap() + ("a", base, TOne(CVoid())) + ("b", base, TOne(CDouble())))),
            (("nwf3", false) -> new ConditionalTypeMap())
        ).mapValues(x => (base, x)))
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
        nwf(CStruct("wf1", true)) //union not allowed as struct and vice versa
        wf(CStruct("wf2", true))
        nwf(CStruct("wf2"))
        wf(CStruct("wf3"))
        wf(CStruct("wf4"))
        //        nwf(CStruct("nwf1"))
        nwf(CStruct("nwf2"))
        nwf(CStruct("nwf3"))
        nwf(CVarArgs())
        nwf(CFunction(Seq(), CVarArgs()))
        wf(CFunction(Seq(CDouble(), CVarArgs()), CVoid()))
        nwf(CFunction(Seq(CVarArgs(), CDouble()), CVoid()))
        nwf(CFunction(Seq(CDouble(), CVarArgs(), CVarArgs()), CVoid()))
    }

    test("simple expression types") {
        val et = getExprType(new VarTypingContext(), new StructEnv(), _: PrimaryExpr)

        et(Constant("1")) should be(TOne(CSigned(CInt())))
    }

    test("choice types and their operations") {
        val fx = FeatureExpr.createDefinedExternal("X")
        val c = TChoice(fx, TOne(CDouble()), TOne(CFloat()))
        val c2 = TChoice(fx, TOne(CDouble()), TChoice(fx.not, TOne(CFloat()), TOne(CUnknown(""))))

        c2.simplify should be(c)
        c2.simplify(fx) should be(TOne(CDouble()))
        c2.simplify(fx) should be(c.simplify(fx))

        c2.map({
            case CDouble() => CUnsigned(CChar())
            case x => x
        }) should be(TChoice(fx, TOne(CUnsigned(CChar())), TChoice(fx.not, TOne(CFloat()), TOne(CUnknown("")))))
    }

    test("coersion") {
        coerce(CDouble(), CInt()) should be(true)
        coerce(CUnsigned(CInt()), CInt()) should be(true)
        coerce(CStruct("a"), CInt()) should be(false)
        coerce(CPointer(CStruct("a")), CPointer(CVoid())) should be(true)

        coerce(CPointer(CVoid()), CPointer(CFunction(List(), CSigned(CInt())))) should be(true)
        coerce(CPointer(CFunction(List(), CSigned(CInt()))), CPointer(CVoid())) should be(true)

        coerce(CPointer(CFunction(Seq(), CVoid())), CFunction(Seq(), CVoid())) should be(true)
        coerce(CFunction(Seq(), CVoid()), CPointer(CFunction(Seq(), CVoid()))) should be(true)
        coerce(CPointer(CPointer(CPointer(CFunction(Seq(), CVoid())))), CFunction(Seq(), CVoid())) should be(true)
    }

}