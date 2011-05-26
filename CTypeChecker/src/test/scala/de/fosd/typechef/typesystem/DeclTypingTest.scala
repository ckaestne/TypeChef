package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr.FeatureExpr
import FeatureExpr.base
import org.scalatest.FunSuite

@RunWith(classOf[JUnitRunner])
class DeclTypingTest extends FunSuite with ShouldMatchers with CTypes with CDeclTyping {


    private def parseDecl(code: String): ADeclaration = {
        val in = CLexer.lex(code, null).setContext(new CTypeContext())
        val p = new CParser()
        val r = p.phrase(p.declaration)(in, FeatureExpr.base)
        r.asInstanceOf[p.Success[ADeclaration]].result
    }
    private def declT(code: String): CType = {
        val ast = parseDecl(code)
        val r = declType(ast)
        println(ast + " --> " + r)
        r
    }


    test("primitives and pointers") {
        declT("extern void main(int a);") should be(CFunction(Seq(),CVoid()))
    }



}