package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr.FeatureExpr.base
import de.fosd.typechef.featureexpr.FeatureExpr

@RunWith(classOf[JUnitRunner])
class CTypesTest extends FunSuite with ShouldMatchers with CTypes with CExprTyping with CStmtTyping {

    test("wellformed types") {
        val sEnv: StructEnv = new StructEnv(Map(
            (("wf1", false) -> new ConditionalTypeMap(Map("a" -> Seq((base, CFloat()))))),
            (("wf2", true) -> new ConditionalTypeMap(Map("a" -> Seq((base, CFloat())), "b" -> Seq((base, CDouble()))))), //union
            (("wf3", false) -> new ConditionalTypeMap(Map("a" -> Seq((base, CPointer(CStruct("wf2")))), "b" -> Seq((base, CDouble()))))),
            (("wf4", false) -> new ConditionalTypeMap(Map("a" -> Seq((base, CPointer(CStruct("wf2")))), "b" -> Seq((base, CPointer(CStruct("wf4"))))))),
            //            (("nwf1", false) -> new ConditionalTypeMap(Map("a" -> Seq((base, CFloat()), (base, CDouble()))))),
            (("nwf2", false) -> new ConditionalTypeMap(Map("a" -> Seq((base, CVoid())), "b" -> Seq((base, CDouble()))))),
            (("nwf3", false) -> new ConditionalTypeMap(Map()))
        ))
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

        et(Constant("1")) should be(CSigned(CInt()))
    }

    test("choice types and their operations") {
        val fx = FeatureExpr.createDefinedExternal("X")
        val fy = FeatureExpr.createDefinedExternal("Y")
        val c = CChoice(fx, CDouble(), CFloat())
        val c2 = CChoice(fx, CDouble(), CChoice(fx.not, CFloat(), CUnknown("")))

        c2.simplify should be(c)
        c2.simplify(fx) should be(CDouble())
        c2.simplify(fx) should be(c.simplify(fx))

        c2.map({
            case CDouble() => CUnsigned(CChar())
            case x => x
        }) should be(CChoice(fx, CUnsigned(CChar()), CChoice(fx.not, CFloat(), CUnknown(""))))
    }

}