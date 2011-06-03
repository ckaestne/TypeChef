package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr.FeatureExpr
import FeatureExpr.base
import org.scalatest.FunSuite

@RunWith(classOf[JUnitRunner])
class ExprTypingTest extends FunSuite with ShouldMatchers with CTypes with CExprTyping {


    private def parseExpr(code: String): Expr = {
        val in = CLexer.lex(code, null).setContext(new CTypeContext())
        val p = new CParser()
        val r = p.phrase(p.expr)(in, FeatureExpr.base)
        r.asInstanceOf[p.Success[Expr]].result
    }
    private def expr(code: String): CType = {
        val ast = parseExpr(code)
        val r = exprType(varCtx, astructEnv, ast)
        println(ast + " --> " + r)
        r
    }


    val varCtx: VarTypingContext =
        Map(
            ("a" -> CDouble()),
            ("v" -> CVoid()),
            ("s" -> CStruct("str")),
            ("sp" -> CPointer(CStruct("str"))),
            ("arr" -> CArray(CDouble(), 5)),
            ("foo" -> CFunction(Seq(), CDouble())),
            ("bar" -> CFunction(Seq(CDouble(), CPointer(CStruct("str"))), CVoid()))
        )
    val astructEnv: StructEnv =
        new StructEnv().add(
            "str", Seq(("a", base, CDouble()), ("b", base, CStruct("str")))
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

    test("struct member access") {
        expr("s.a") should be(CObj(CDouble()))
        expr("s.b") should be(CObj(CStruct("str")))
        expr("s.b.a") should be(CObj(CDouble()))
        expr("s.b.b.a") should be(CObj(CDouble()))
        expr("(&s)->a") should be(CObj(CDouble()))
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
        expr("a+=2") should be(CDouble())
    }

    //    @Ignore
    //    test("array access") {
    //        expr("arr[3]") should be(CDouble())
    //    }
}