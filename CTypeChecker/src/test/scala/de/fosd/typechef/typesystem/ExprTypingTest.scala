package de.fosd.typechef.typesystem


import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.FeatureExprFactory.True
import de.fosd.typechef.parser.c.{Id, TestHelper}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSuite, Matchers}

@RunWith(classOf[JUnitRunner])
class ExprTypingTest extends FunSuite with CTypeSystem with CEnv with Matchers with TestHelper {

    val _i: Conditional[CType] = One(CSigned(CInt()))
    val _l: Conditional[CType] = One(CSigned(CLong()))
    val _d: Conditional[CType] = One(CDouble())
    val _oi: Conditional[CType] = One(CSigned(CInt()).toCType.toObj)
    val _ol: Conditional[CType] = One(CSigned(CLong()).toCType.toObj)
    val _od: Conditional[CType] = One(CDouble().toCType.toObj)
    val _u: Conditional[CType] = One(CUndefined)
    val c_i_l: Conditional[CType] = Choice(fx, _i, _l)

    protected def assertCondEquals(exp: Conditional[CType], act: Conditional[CType]) {
        assert(ConditionalLib.equals(exp, act), "Expected: " + exp + "\nActual:   " + act)
    }

    private def exprV(code: String): Conditional[CType] = {
        val ast = parseExpr(code)
        val env = EmptyEnv.updateVarEnv(varCtx).updateStructEnv(astructEnv)
        val r = getExprType(ast, True, env)
//        println(ast + " --> " + r)
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
            ("u", True, CUnsigned(CInt())),
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
        ).map(x => (x._1, x._2, null, One(x._3.toCType), KDeclaration, 0, NoLinkage)) ++ Seq(
            ("c", True, null, c_i_l, KDeclaration, 0, NoLinkage),
            ("vstruct", True, null, Choice(fx, One(CStruct("vstrA").toCType), One(CStruct("vstrB").toCType)), KDeclaration, 0, NoLinkage),
            ("vstruct2", True, null, Choice(fx, One(CStruct("vstrA").toCType), _u), KDeclaration, 0, NoLinkage),
            ("cfun", True, null, Choice(fx,
                One(CFunction(Seq(CSigned(CInt())), CSigned(CInt())).toCType),
                One(CFunction(Seq(CSigned(CInt()), CSigned(CInt())), CSigned(CLong())).toCType)), KDeclaration, 0, NoLinkage) //i->i or i,i->l
        ))

    val astructEnv: StructEnv =
        new StructEnv().addComplete(
            Id("str"), false, True, new ConditionalTypeMap() +("a", True, null, One(CDouble())) +("b", True, null, One(CStruct("str"))), 1).addComplete(
            Id("vstrA"), false, fx, new ConditionalTypeMap() +("a", fx and fy, null, _l) +("b", fx, null, One(CStruct("str"))), 1).addComplete(
            Id("vstrB"), false, True, new ConditionalTypeMap() +("a", True, null, _i) +("b", True, null, _i) +("c", fx.not, null, _i), 1
        )

    test("primitives and pointers") {
        expr("0") should be(CZero().toCType)
        expr("'\\0'") should be(CZero().toCType)
        expr("0x0000") should be(CZero().toCType)
        expr("1") should be(CSigned(CInt()).toCType)
        expr("blub") should be(CUnknown().toCType)
        expr("a") should be(CDouble().toCType.toObj)
        expr("\"a\"") should be(CPointer(CSignUnspecified(CChar())).toCType)
        expr("'0'") should be(CSignUnspecified(CChar()).toCType)
        expr("&a") should be(CPointer(CDouble()).toCType.toObj)
        expr("*(&a)") should be(CDouble().toCType.toObj)
        expr("*a") should be(CUnknown().toCType)
        expr("*v") should be(CUnknown().toCType)
        expr("&foo") should be(CPointer(CFunction(Seq(), CDouble())).toCType.toObj)
    }

    test("conditional primitives and pointers") {
        exprV("ca") should be(Choice(fa, One(CDouble().toCType.toObj), _u))
        exprV("c") should be(c_i_l.map(_.toObj))
        exprV("&c") should be(c_i_l.map(_.map(CPointer(_)).toObj))
        exprV("*c").simplify should be(One(CUnknown().toCType))
    }

    test("struct member access") {
        expr("s.a") should be(CDouble().toCType.toObj)
        expr("strf().a") should be(CDouble().toCType)
        expr("s.b") should be(CStruct("str").toCType.toObj)
        expr("s.b.a") should be(CDouble().toCType.toObj)
        expr("s.b.b.a") should be(CDouble().toCType.toObj)
        expr("(&s)->a") should be(CDouble().toCType.toObj)
    }
    test("conditional struct member access") {
        assertCondEquals(
            exprV("vstruct.a"),
            Choice(fx and fy, _ol, Choice(fx.not, _oi, One(CUndefined.toCType)))
        )
        assertCondEquals(exprV("vstruct2.a"), Choice(fx and fy, _ol, _u))
        assertCondEquals(exprV("vstruct.b.a"), Choice(fx, _od, _u))
    }

    test("casts") {
        expr("(double)3") should be(CDouble().toCType)
        expr("(void*)foo") should be(CPointer(CVoid()).toCType)
        expr("(int(*)())foo") should be(CPointer(CFunction(List(), CSigned(CInt()))).toCType)
        expr("(struct str)u") should be(CStruct("str").toCType)
        expr("(struct str)s") should be(CStruct("str").toCType)
        expr("(struct b)s") should be(CUnknown().toCType)
    }



    test("function calls") {
        expr("foo") should be(CFunction(Seq(), CDouble()).toCType.toObj)
        expr("foo()") should be(CDouble().toCType)
        expr("foo(1)") should be(CUnknown().toCType)
        expr("bar") should be(CFunction(Seq(CDouble(), CPointer(CStruct("str"))), CVoid()).toCType.toObj)
        expr("bar()") should be(CUnknown().toCType)
        expr("bar(1,s)") should be(CUnknown().toCType)
        expr("bar(1,&s)") should be(CVoid().toCType)
        expr("funparam()") should be(CDouble().toCType)
        expr("(*funparam)()") should be(CDouble().toCType)
        expr("(****funparam)()") should be(CDouble().toCType)
        expr("funparamptr()") should be(CUnknown().toCType)
        expr("(*funparamptr)()") should be(CDouble().toCType)
        expr(" __builtin_va_arg()") should be(CUnknown().toCType)
        expr(" __builtin_va_arg(a, int*)") should be(CPointer(CSigned(CInt())).toCType)
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
        expr("a=2") should be(CDouble().toCType)
        expr("a=s") should be(CUnknown().toCType)
        expr("sp=0") should be(CPointer(CStruct("str")).toCType)
    }
    test("pre/post increment") {
        expr("a++") should be(CDouble().toCType)
        expr("a--") should be(CDouble().toCType)
        expr("v++") should be(CUnknown().toCType)
        expr("3++") should be(CUnknown().toCType)
        expr("--a") should be(CDouble().toCType)
        expr("++3") should be(CUnknown().toCType)
        expr("*++argv") should be(CPointer(CSignUnspecified(CChar())).toCType.toObj)
    }
    test("binary operation") {
        expr("1+2") should be(CSigned(CInt()).toCType)
        expr("1l+2") should be(CSigned(CLong()).toCType)
        expr("1+2l") should be(CSigned(CLong()).toCType)
        expr("a+=2") should be(CDouble().toCType)
    }
    test("unary op") {
        expr("~i") should be(CSigned(CInt()).toCType)
        expr("~a").isUnknown should be(true)
    }
    test("conditional op") {
        expr("i?i:i") should be(CSigned(CInt()).toCType)
        expr("i?i:a") should be(CDouble().toCType)
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
        expr("({1;foo();2;})") should be(CSigned(CInt()).toCType)
    }
    test("conditional compound statement expressions") {
        exprV( """({1;
                    foo();
                    "";
                    #ifdef X
                    2;
                    #endif
                    })""") should be(Choice(fx, _i, One(CPointer(CSignUnspecified(CChar())).toCType)))
    }

    test("arrays") {
        expr("arr[0]") should be(CDouble().toCType.toObj)
    }

    test("operations") {

        operationType("+", CPointer(CUnsigned(CLong())), CSigned(CInt()), null, null, EmptyEnv) should be(CPointer(CUnsigned(CLong())).toCType)
        CArray(CUnsigned(CLong()), -1).toCType.toObj.toValue should be(CPointer(CUnsigned(CLong())).toCType)
        operationType("+", CArray(CUnsigned(CLong()), -1).toCType.toObj, CSigned(CInt()), null, null, EmptyEnv) should be(CPointer(CUnsigned(CLong())).toCType)

    }

    test("label deref") {
        expr("&&foo") should be(CPointer(CVoid()).toCType)
    }

    test("ignored types") {
        expr("ig").toValue should be(CIgnore().toCType)
        expr("&ig") should be(CPointer(CIgnore()).toCType.toObj)
        expr("*ig") should be(CIgnore().toCType.toObj)
        expr("(double)ig") should be(CDouble().toCType)
    }

    //    @Ignore
    //    test("array access") {
    //        expr("arr[3]") should be(CDouble())
    //    }
}