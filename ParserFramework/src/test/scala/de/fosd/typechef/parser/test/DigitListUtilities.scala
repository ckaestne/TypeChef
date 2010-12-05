package de.fosd.typechef.parser.test

import de.fosd.typechef.parser._
import junit.framework._;
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._
import org.junit.Test

trait DigitListUtilities {
    val f1 = FeatureExpr.createDefinedExternal("a")
    val f2 = FeatureExpr.createDefinedExternal("b")
    val l1 = Lit(1)
    val l2 = Lit(2)
    val l3 = Lit(3)

    def t(text: String): MyToken = t(text, FeatureExpr.base)
    def t(text: String, feature: FeatureExpr): MyToken = new MyToken(text, feature)
    def outer(x: AST) = DigitList2(List(o(x)))
    def wrapList(x: AST*) = DigitList2(List() ++ x.map(Opt(FeatureExpr.base, _)))
    def wrapList(x: List[AST]) : DigitList2 = wrapList(x :_*)

    def assertParseResult(expected: AST, actual: ParseResult[AST, MyToken, Any]) {
        System.out.println(actual)
        actual match {
            case Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                assertEquals("incorrect parse result", outer(expected), ast)
            }
            case NoSuccess(msg, context, unparsed, inner) =>
                fail(msg + " at " + unparsed + " with context " + context + " " + inner)
        }
    }

    def o(ast: AST) = Opt(FeatureExpr.base, ast)
}