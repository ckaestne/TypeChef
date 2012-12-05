package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import de.fosd.typechef.featureexpr.FeatureExprFactory.True
import de.fosd.typechef.parser.c.{Id, TestHelper}
import de.fosd.typechef.conditional._

@RunWith(classOf[JUnitRunner])
class ExprTypingTest extends CTypeSystem with CEnv with FunSuite with ShouldMatchers with TestHelper {

    val _i = One(CSigned(CInt()))
    val _l = One(CSigned(CLong()))
    val _d = One(CDouble())
    val _oi = One(CObj(CSigned(CInt())))
    val _ol = One(CObj(CSigned(CLong())))
    val _od = One(CObj(CDouble()))
    val _u = One(CUndefined)
    val c_i_l = Choice(fx, _i, _l)

    protected def assertCondEquals(exp: Conditional[CType], act: Conditional[CType]) {
        assert(ConditionalLib.equals(exp, act), "Expected: " + exp + "\nActual:   " + act)
    }

    private def exprV(code: String): Conditional[CType] = {
        val ast = parseExpr(code)
        val env = EmptyEnv.updateVarEnv(varCtx).updateStructEnv(astructEnv)
        val r = getExprType(ast, True, env)
        println(ast + " --> " + r)
        r
    }

    private def expr(code: String): CType =
        exprV(code) match {
            case One(t) => t
            case e => CUnknown("Multiple types " + e)
        }


    val varCtx: VarTypingContext =
        new VarTypingContext() ++ (Seq(
            ("a", True, CDouble()),
            ("i", True, CSigned(CInt())),
            ("ca", fa, CDouble()),
            ("v", True, CVoid()),
            ("ig", True, CIgnore()),
            ("s", True, CStruct("str")),
            ("sp", True, CPointer(CStruct("str"))),
            ("arr", True, CArray(CDouble(), 5)),
            ("foo", True, CFunction(Seq(), CDouble())),
            ("bar", True, CFunction(Seq(CDouble(), CPointer(CStruct("str"))), CVoid())),
            ("strf", True, CFunction(Seq(), CStruct("str"))),
            ("funparam", True, CPointer(CFunction(Seq(), CDouble()))),
            ("funparamptr", True, CPointer(CPointer(CFunction(Seq(), CDouble())))),
            ("argv", True, CArray(CPointer(CSignUnspecified(CChar())), -1))
        ).map(x => (x._1, x._2, null, One(x._3), KDeclaration, 0)) ++ Seq(
            ("c", True, null, c_i_l, KDeclaration, 0),
            ("vstruct", True, null, Choice(fx, One(CStruct("vstrA")), One(CStruct("vstrB"))), KDeclaration, 0),
            ("vstruct2", True, null, Choice(fx, One(CStruct("vstrA")), _u), KDeclaration, 0),
            ("cfun", True, null, Choice(fx,
                One(CFunction(Seq(CSigned(CInt())), CSigned(CInt()))),
                One(CFunction(Seq(CSigned(CInt()), CSigned(CInt())), CSigned(CLong())))), KDeclaration, 0) //i->i or i,i->l
        ))

    val astructEnv: StructEnv =
        new StructEnv().addComplete(
            Id("str"), false, True, new ConditionalTypeMap() +("a", True, null, One(CDouble())) +("b", True, null, One(CStruct("str"))),1).addComplete(
            Id("vstrA"), false, fx, new ConditionalTypeMap() +("a", fx and fy, null, _l) +("b", fx, null, One(CStruct("str"))),1).addComplete(
            Id("vstrB"), false, True, new ConditionalTypeMap() +("a", True, null, _i) +("b", True, null, _i) +("c", fx.not, null, _i),1
        )

    test("primitives and pointers") {
        expr("0") should be(CZero())
        expr("'\\0'") should be(CZero())
        expr("1") should be(CSigned(CInt()))
        expr("blub") should be(CUnknown())
        expr("a") should be(CObj(CDouble()))
        expr("\"a\"") should be(CPointer(CSignUnspecified(CChar())))
        expr("'0'") should be(CUnsigned(CChar()))
        expr("&a") should be(CPointer(CDouble()))
        expr("*(&a)") should be(CObj(CDouble()))
        expr("*a") should be(CUnknown())
        expr("*v") should be(CUnknown())
        expr("&foo") should be(CPointer(CFunction(Seq(), CDouble())))
    }

    test("conditional primitives and pointers") {
        exprV("ca") should be(Choice(fa, One(CObj(CDouble())), _u))
        exprV("c") should be(c_i_l.map(CObj(_)))
        exprV("&c") should be(c_i_l.map(CPointer(_)))
        exprV("*c").simplify should be(One(CUnknown()))
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
            Choice(fx and fy, _ol, Choice(fx.not, _oi, One(CUndefined)))
        )
        assertCondEquals(exprV("vstruct2.a"), Choice(fx and fy, _ol, _u))
        assertCondEquals(exprV("vstruct.b.a"), Choice(fx, _od, _u))
    }

    test("casts") {
        expr("(double)3") should be(CDouble())
        expr("(void*)foo") should be(CPointer(CVoid()))
        expr("(int(*)())foo") should be(CPointer(CFunction(List(), CSigned(CInt()))))
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
        expr(" __builtin_va_arg()") should be(CUnknown())
        expr(" __builtin_va_arg(a, int*)") should be(CIgnore())
    }

    test("conditional function calls") {
        assertCondEquals(exprV( """cfun(1
            #ifdef Y
            ,2
            #endif
            )"""),
            Choice(fx, Choice(fy.not, _i, _u), Choice(fy.not, _u, _l)))
    }

    test("assignment") {
        expr("a=2") should be(CDouble())
        expr("a=s") should be(CUnknown())
        expr("sp=0") should be(CPointer(CStruct("str")))
    }
    test("pre/post increment") {
        expr("a++") should be(CDouble())
        expr("a--") should be(CDouble())
        expr("v++") should be(CUnknown())
        expr("3++") should be(CUnknown())
        expr("--a") should be(CDouble())
        expr("++3") should be(CUnknown())
        expr("*++argv") should be(CObj(CPointer(CSignUnspecified(CChar()))))
    }
    test("binary operation") {
        expr("1+2") should be(CSigned(CInt()))
        expr("1l+2") should be(CSigned(CLong()))
        expr("1+2l") should be(CSigned(CLong()))
        expr("a+=2") should be(CDouble())
    }
    test("unary op") {
        expr("~i") should be(CSigned(CInt()))
        expr("~a").isUnknown should be(true)
    }
    test("conditional op") {
        expr("i?i:i") should be(CSigned(CInt()))
        expr("i?i:a") should be(CDouble())
    }
    test("conditional binary operation") {
        assertCondEquals(_i,
            exprV( """1
                 #ifdef X
                 +2
                 #endif
                 +3"""))
        assertCondEquals(Choice(fx, _u, _i),
            exprV( """1
                 #ifdef X
                 +s
                 #endif
                 +3"""))
        assertCondEquals(Choice(fx, _l, _i),
            exprV( """1
                 #ifdef X
                 +1l
                 #endif
                 +3"""))

    }
    test("compound statement expressions") {
        expr("({1;foo();2;})") should be(CSigned(CInt()))
    }
    test("conditional compound statement expressions") {
        exprV( """({1;
                    foo();
                    "";
                    #ifdef X
                    2;
                    #endif
                    })""") should be(Choice(fx, _i, One(CPointer(CSignUnspecified(CChar())))))
    }

    test("arrays") {
        expr("arr[0]") should be(CObj(CDouble()))
    }

    test("operations") {

        operationType("+", CPointer(CUnsigned(CLong())), CSigned(CInt()), null, null) should be(CPointer(CUnsigned(CLong())))
        CObj(CArray(CUnsigned(CLong()), -1)).toValue should be(CPointer(CUnsigned(CLong())))
        operationType("+", CObj(CArray(CUnsigned(CLong()), -1)), CSigned(CInt()), null, null) should be(CPointer(CUnsigned(CLong())))

    }

    test("label deref") {
        expr("&&foo") should be(CPointer(CVoid()))
    }

    test("ignored types") {
        expr("ig").toValue should be(CIgnore())
        expr("&ig") should be(CPointer(CIgnore()))
        expr("*ig") should be(CIgnore())
        expr("(double)ig") should be(CDouble())
    }

    //    @Ignore
    //    test("array access") {
    //        expr("arr[3]") should be(CDouble())
    //    }
}