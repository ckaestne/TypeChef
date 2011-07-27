package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import de.fosd.typechef.featureexpr.FeatureExpr.base
import de.fosd.typechef.parser.c.TestHelper
import de.fosd.typechef.conditional._

@RunWith(classOf[JUnitRunner])
class ExprTypingTest extends FunSuite with ShouldMatchers with CTypes with CExprTyping with CStmtTyping with TestHelper {

    val _i = TOne(CSigned(CInt()))
    val _l = TOne(CSigned(CLong()))
    val _d = TOne(CDouble())
    val _oi = TOne(CObj(CSigned(CInt())))
    val _ol = TOne(CObj(CSigned(CLong())))
    val _od = TOne(CObj(CDouble()))
    val _u = TOne(CUndefined())
    val c_i_l = TChoice(fx, _i, _l)

    protected def assertCondEquals(exp: TConditional[CType], act: TConditional[CType]) {
        assert(ConditionalLib.equals(exp, act), "Expected: " + exp + "\nActual:   " + act)
    }

    private def exprV(code: String): TConditional[CType] = {
        val ast = parseExpr(code)
        val r = getExprType(varCtx, astructEnv, ast)
        println(ast + " --> " + r)
        r
    }

    private def expr(code: String): CType =
        exprV(code) match {
            case TOne(t) => t
            case e => CUnknown("Multiple types " + e)
        }


    val varCtx: VarTypingContext =
        new VarTypingContext() ++ (Seq(
            ("a", base, CDouble()),
            ("ca", fa, CDouble()),
            ("v", base, CVoid()),
            ("s", base, CStruct("str")),
            ("sp", base, CPointer(CStruct("str"))),
            ("arr", base, CArray(CDouble(), 5)),
            ("foo", base, CFunction(Seq(), CDouble())),
            ("bar", base, CFunction(Seq(CDouble(), CPointer(CStruct("str"))), CVoid())),
            ("strf", base, CFunction(Seq(), CStruct("str"))),
            ("funparam", base, CPointer(CFunction(Seq(), CDouble()))),
            ("funparamptr", base, CPointer(CPointer(CFunction(Seq(), CDouble()))))
        ).map(x => (x._1, x._2, TOne(x._3))) ++ Seq(
            ("c", base, c_i_l),
            ("vstruct", base, TChoice(fx, TOne(CStruct("vstrA")), TOne(CStruct("vstrB")))),
            ("vstruct2", base, TChoice(fx, TOne(CStruct("vstrA")), _u)),
            ("cfun", base, TChoice(fx,
                TOne(CFunction(Seq(CSigned(CInt())), CSigned(CInt()))),
                TOne(CFunction(Seq(CSigned(CInt()), CSigned(CInt())), CSigned(CLong()))))) //i->i or i,i->l
        ))

    val astructEnv: StructEnv =
        new StructEnv().add(
            "str", false, base, new ConditionalTypeMap() + ("a", base, TOne(CDouble())) + ("b", base, TOne(CStruct("str")))).add(
            "vstrA", false, fx, new ConditionalTypeMap() + ("a", fx and fy, _l) + ("b", fx, TOne(CStruct("str")))).add(
            "vstrB", false, base, new ConditionalTypeMap() + ("a", base, _i) + ("b", base, _i) + ("c", fx.not, _i)
        )

    test("primitives and pointers") {
        expr("1") should be(CSigned(CInt()))
        expr("blub") should be(CUnknown())
        expr("a") should be(CObj(CDouble()))
        expr("&a") should be(CPointer(CDouble()))
        expr("*(&a)") should be(CObj(CDouble()))
        expr("*a") should be(CUnknown())
        expr("*v") should be(CUnknown())
    }

    test("conditional primitives and pointers") {
        exprV("ca") should be(TChoice(fa, TOne(CObj(CDouble())), _u))
        exprV("c") should be(c_i_l.map(CObj(_)))
        exprV("&c") should be(c_i_l.map(CPointer(_)))
        exprV("*c").simplify should be(TOne(CUnknown()))
    }

    test("struct member access") {
        expr("s.a") should be(CObj(CDouble()))
        expr("strf().a") should be(CDouble())
        expr("s.b") should be(CObj(CStruct("str")))
        expr("s.b.a") should be(CObj(CDouble()))
        expr("s.b.b.a") should be(CObj(CDouble()))
        expr("(&s)->a") should be(CObj(CDouble()))
    }
    test("conditional struct member access") {
        assertCondEquals(
            exprV("vstruct.a"),
            TChoice(fx and fy, _ol, TChoice(fx.not, _oi, TOne(CUndefined())))
        )
        assertCondEquals(exprV("vstruct2.a"), TChoice(fx and fy, _ol, _u))
        assertCondEquals(exprV("vstruct.b.a"), TChoice(fx, _od, _u))
    }

    test("coersion") {
        expr("(double)3") should be(CDouble())
        coerce(CDouble(), CInt()) should be(true)
        coerce(CUnsigned(CInt()), CInt()) should be(true)
        coerce(CStruct("a"), CInt()) should be(false)
        coerce(CPointer(CStruct("a")), CPointer(CVoid())) should be(true)
    }

    test("function calls") {
        expr("foo") should be(CFunction(Seq(), CDouble()))
        expr("foo()") should be(CDouble())
        expr("foo(1)") should be(CUnknown())
        expr("bar") should be(CFunction(Seq(CDouble(), CPointer(CStruct("str"))), CVoid()))
        expr("bar()") should be(CUnknown())
        expr("bar(1,s)") should be(CUnknown())
        expr("bar(1,&s)") should be(CVoid())
        expr("funparam()") should be(CDouble())
        expr("(*funparam)()") should be(CDouble())
        expr("(****funparam)()") should be(CDouble())
        expr("funparamptr()") should be(CUnknown())
        expr("(*funparamptr)()") should be(CDouble())
    }

    test("conditional function calls") {
        assertCondEquals(exprV("""cfun(1
            #ifdef Y
            ,2
            #endif
            )"""),
            TChoice(fx, TChoice(fy.not, _i, _u), TChoice(fy.not, _u, _l)))
    }

    test("assignment") {
        expr("a=2") should be(CDouble())
        expr("a=s") should be(CUnknown())
    }
    test("pre/post increment") {
        expr("a++") should be(CDouble())
        expr("a--") should be(CDouble())
        expr("v++") should be(CUnknown())
        expr("3++") should be(CUnknown())
        expr("--a") should be(CDouble())
        expr("++3") should be(CUnknown())
    }
    test("binary operation") {
        expr("1+2") should be(CSigned(CInt()))
        expr("1l+2") should be(CSigned(CLong()))
        expr("1+2l") should be(CSigned(CLong()))
        expr("a+=2") should be(CDouble())
    }
    test("conditional binary operation") {
        assertCondEquals(_i,
            exprV("""1
                 #ifdef X
                 +2
                 #endif
                 +3"""))
        assertCondEquals(TChoice(fx, _u, _i),
            exprV("""1
                 #ifdef X
                 +s
                 #endif
                 +3"""))
        assertCondEquals(TChoice(fx, _l, _i),
            exprV("""1
                 #ifdef X
                 +1l
                 #endif
                 +3"""))

    }
    test("compound statement expressions") {
        expr("({1;foo();2;})") should be(CSigned(CInt()))
    }
    test("conditional compound statement expressions") {
        exprV("""({1;
                    foo();
                    "";
                    #ifdef X
                    2;
                    #endif
                    })""") should be(TChoice(fx, _i, TOne(CPointer(CSignUnspecified(CChar())))))
    }


    test("operations") {

        operationType("+", CPointer(CUnsigned(CLong())), CSigned(CInt())) should be(CPointer(CUnsigned(CLong())))
        CObj(CArray(CUnsigned(CLong()), -1)).toValue should be(CPointer(CUnsigned(CLong())))
        operationType("+", CObj(CArray(CUnsigned(CLong()), -1)), CSigned(CInt())) should be(CPointer(CUnsigned(CLong())))

    }

    //    @Ignore
    //    test("array access") {
    //        expr("arr[3]") should be(CDouble())
    //    }
}