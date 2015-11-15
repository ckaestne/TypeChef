package de.fosd.typechef.typesystem


import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.featureexpr.FeatureExprFactory.True
import de.fosd.typechef.parser.c._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSuite, Matchers}

@RunWith(classOf[JUnitRunner])
class CTypesTest extends FunSuite with CTypeSystem with Matchers {

    test("wellformed types") {
        val sEnv: StructEnv = new StructEnv(Map(
            (("wf1", false) -> (new ConditionalTypeMap() +("a", True, null, One(CFloat())))),
            (("wf2", true) -> (new ConditionalTypeMap() +("a", True, null, One(CFloat())) +("b", True, null, One(CDouble())))), //union
            (("wf3", false) -> (new ConditionalTypeMap() +("a", True, null, One(CPointer(CStruct("wf2")))) +("b", True, null, One(CDouble())))),
            (("wf4", false) -> (new ConditionalTypeMap() +("a", True, null, One(CPointer(CStruct("wf2")))) +("b", True, null, One(CPointer(CStruct("wf4")))))),
            //            (("nwf1", false) -> new ConditionalTypeMap(Map("a" -> Seq((True, CFloat()), (True, CDouble()))))),
            (("nwf2", false) -> (new ConditionalTypeMap() +("a", True, null, One(CVoid())) +("b", True, null, One(CDouble())))),
            (("nwf3", false) -> new ConditionalTypeMap())
        ).mapValues(x => One(StructTag(true, x, 0))))
        val tEnv: PtrEnv = Set("Str", "wf2")
        val wf = wellformed(sEnv, tEnv, _: AType) should be(true)
        val nwf = wellformed(sEnv, tEnv, _: AType) should be(false)

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
        wf(CFunction(Seq(CInt().toCType), CVoid()))
        wf(CFunction(Seq(CInt().toCType, CDouble()), CVoid()))
        nwf(CFunction(Seq(CPointer(CStruct("NoStr"))), CVoid()))
        nwf(CFunction(Seq(CVoid()), CVoid()))
        wf(CFunction(Seq(CArray(CDouble(), 2)), CVoid()))
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
        val et = getExprType(_: PrimaryExpr, True, EmptyEnv)

        et(Constant("1")) should be(One(CSigned(CInt()).toCType))
    }

    test("choice types and their operations") {
        val fx = FeatureExprFactory.createDefinedExternal("X")
        val c = Choice(fx, One(CDouble()), One(CFloat()))
        val c2 = Choice(fx, One(CDouble()), Choice(fx.not, One(CFloat()), One(CUnknown(""))))

        c2.simplify should be(c)
        c2.simplify(fx) should be(One(CDouble()))
        c2.simplify(fx) should be(c.simplify(fx))

        c2.map({
            case CDouble() => CUnsigned(CChar())
            case x => x
        }) should be(Choice(fx, One(CUnsigned(CChar())), Choice(fx.not, One(CFloat()), One(CUnknown("")))))
    }

    test("coercion") {
        coerce(CDouble(), CInt().toCType).isDefined should be(true)
        coerce(CUnsigned(CInt()), CInt().toCType).isDefined should be(true)
        coerce(CStruct("a"), CInt().toCType).isDefined should be(false)
        coerce(CPointer(CStruct("a")), CPointer(CVoid())).isDefined should be(true)

        coerce(CPointer(CVoid()), CPointer(CFunction(List(), CSigned(CInt())))).isDefined should be(true)
        coerce(CPointer(CFunction(List(), CSigned(CInt()))), CPointer(CVoid())).isDefined should be(true)

        coerce(CPointer(CFunction(Seq(), CVoid())), CFunction(Seq(), CVoid())).isDefined should be(true)
        coerce(CFunction(Seq(), CVoid()), CPointer(CFunction(Seq(), CVoid()))).isDefined should be(true)
        coerce(CPointer(CPointer(CPointer(CFunction(Seq(), CVoid())))), CFunction(Seq(), CVoid())).isDefined should be(true)

        coerce(CFunction(Seq(CDouble()), CVoid()), CFunction(Seq(CIgnore()), CVoid())).isDefined should be(true)
        coerce(CFunction(Seq(CDouble()), CVoid()), CFunction(Seq(CDouble()), CIgnore())).isDefined should be(true)

        coerce(CPointer(CZero()), CPointer(CSigned(CInt()))).isDefined should be(true)
    }

}