package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr.FeatureExpr

@RunWith(classOf[JUnitRunner])
class ExprTypingTest extends FunSuite with ShouldMatchers with CTypes with CExprTyping {


    private def parseExpr(code: String): Expr = {
        val in = CLexer.lex(code, null).setContext(new CTypeContext())
        val p = new CParser()
        val r = p.phrase(p.expr)(in, FeatureExpr.base)
        println(r)
        r.asInstanceOf[p.Success[Expr]].result
    }
    private def expr(code: String): CType =
        exprType(varCtx, Map(), structEnv, parseExpr(code))


    val varCtx: VarTypingContext =
        Map(
            ("a" -> CDouble()),
            ("v" -> CVoid()),
            ("s" -> CStruct("str")))
    val structEnv: StructEnv =
        Map(
            ("str" -> Seq(("a" -> CDouble()), ("b" -> CPointer(CStruct("str")))))
        )

    test("primitives and pointers") {
        expr("1") should be(CSigned(CInt()))
        expr("foo") should be(CUnknown())
        expr("a") should be(CObj(CDouble()))
        expr("&a") should be(CPointer(CDouble()))
        expr("*(&a)") should be(CObj(CDouble()))
        expr("*a") should be(CUnknown())
        expr("*v") should be(CUnknown())
    }

    test("struct member access"){
        expr("s.a") should be(CObj(CDouble()))
        expr("s.b") should be(CObj(CPointer(CStruct("str"))))
    }

    test("coersion") {

    }

}