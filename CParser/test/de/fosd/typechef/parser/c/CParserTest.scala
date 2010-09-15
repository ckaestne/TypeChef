package de.fosd.typechef.parser.c

import junit.framework._;
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._

class CParserTest extends TestCase {
    val p = new CParser()

    def assertParseResult(expected: AST, code: String, mainProduction: (TokenReader[TokenWrapper], FeatureExpr) => MultiParseResult[AST, TokenWrapper]) {
        val actual = p.parse(code.stripMargin, mainProduction)
        System.out.println(actual)
        actual match {
            case Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                assertEquals("incorrect parse result", expected, ast)
            }
            case NoSuccess(msg, context, unparsed, inner) =>
                fail(msg + " at " + unparsed + " with context " + context + " " + inner)
        }
    }
    def assertParseResult(expected: AST, code: String, productions: List[(TokenReader[TokenWrapper], FeatureExpr) => MultiParseResult[AST, TokenWrapper]]) {
        for (val production <- productions)
            assertParseResult(expected, code, production)
    }
    def assertParseable(code: String, mainProduction: (TokenReader[TokenWrapper], FeatureExpr) => MultiParseResult[Any, TokenWrapper]) {
        val actual = p.parseAny(code.stripMargin, mainProduction)
        System.out.println(actual)
        actual match {
            case Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                //succeed
            }
            case NoSuccess(msg, context, unparsed, inner) =>
                fail(msg + " at " + unparsed + " with context " + context + " " + inner)
        }
    }
    def assertParseAnyResult(expected: Any, code: String, mainProduction: (TokenReader[TokenWrapper], FeatureExpr) => MultiParseResult[Any, TokenWrapper]) {
        val actual = p.parseAny(code.stripMargin, mainProduction)
        System.out.println(actual)
        actual match {
            case Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                assertEquals("incorrect parse result", expected, ast)
            }
            case NoSuccess(msg, context, unparsed, inner) =>
                fail(msg + " at " + unparsed + " with context " + context + " " + inner)
        }
    }
    def assertParseError(code: String, mainProduction: (TokenReader[TokenWrapper], FeatureExpr) => MultiParseResult[Any, TokenWrapper]) {
        val actual = p.parseAny(code.stripMargin, mainProduction)
        System.out.println(actual)
        actual match {
            case Success(ast, unparsed) => {
                Assert.fail("parsing succeeded unexpectedly with " + ast + " - " + unparsed)
            }
            case NoSuccess(msg, context, unparsed, inner) => ;
        }
    }
    def assertParseError(code: String, productions: List[(TokenReader[TokenWrapper], FeatureExpr) => MultiParseResult[Any, TokenWrapper]]) {
        for (val production <- productions)
            assertParseError(code, production)
    }
    def fa = FeatureExpr.createDefinedExternal("a")

    def testId() {
        assertParseResult(Id("test"), "test", List(p.primaryExpr, p.ID))
        assertParseResult(Alt(fa, Id("test"), Id("bar")), """|#ifdef a
        					|test
        					|#else
        					|bar
        					|#endif""", List(p.primaryExpr, p.ID))
        assertParseError("case", List(p.primaryExpr, p.ID))
    }

    def testStringLit() {
        assertParseResult(StringLit("\"test\""), "\"test\"", List(p.primaryExpr, p.stringConst))
        assertParseResult(Alt(fa, StringLit("\"test\""), StringLit("\"ba\\\"r\"")), """|#ifdef a
        					|"test"
        					|#else
        					|"ba\"r"
        					|#endif""", List(p.primaryExpr, p.stringConst))
        assertParseError("'c'", List(p.stringConst))
    }

    def testConstant() {
        def parseConstant(const: String) { assertParseResult(Constant(const), const, p.numConst) }
        parseConstant("1")
        parseConstant("0xF")
        parseConstant("0X1A")
        parseConstant("0X1Al")
        parseConstant("0X1Au")
        parseConstant("0X1AU")
        parseConstant("0X1AL")
        parseConstant("01")
        parseConstant("1222223")
        parseConstant("1L")
        parseConstant("1U")
        parseConstant("1u")
        parseConstant("1l")
        parseConstant("1E32")
        parseConstant("1e32")
        parseConstant("1E+32")
        parseConstant("1E-32")
        parseConstant("1.1")
        parseConstant(".1")
        parseConstant(".1E2l")
        parseConstant("'a'")
        assertParseResult(Alt(fa, Constant("1"), Constant("2")), """|#ifdef a
        					|1
        					|#else
        					|2
        					|#endif""", List(p.primaryExpr, p.numConst))
    }
    def testDots() {
        assertParseable(".", p.DOT)
        assertParseable("...", p.VARARGS)
        assertParseError("...", p.DOT)
        assertParseError(".", p.VARARGS)
    }
    def testPostfixSuffix {
        assertParseAnyResult(List(PointerPostfixSuffix("->", Id("a"))), "->a", p.postfixSuffix)
        assertParseAnyResult(List(PointerPostfixSuffix("->", Id("a"))), "->    a", p.postfixSuffix)
        assertParseAnyResult(List(PointerPostfixSuffix("->", Id("a")), PointerPostfixSuffix("->", Id("a"))), "->a->a", p.postfixSuffix)
        assertParseAnyResult(List(PointerPostfixSuffix(".", Id("a"))), ".a", p.postfixSuffix)
        assertParseAnyResult(List(SimplePostfixSuffix("++")), "++", p.postfixSuffix)
        assertParseAnyResult(List(SimplePostfixSuffix("++"), SimplePostfixSuffix("--")), "++ --", p.postfixSuffix)
    }
    def testPostfixExpr {
        assertParseResult(PostfixExpr(Id("b"), List(PointerPostfixSuffix("->", Id("a")))), "b->a", List(p.postfixExpr, p.unaryExpr))
        assertParseResult(Id("b"), "b", List(p.postfixExpr, p.unaryExpr))

        assertParseResult(Alt(fa, PostfixExpr(Id("b"), List(PointerPostfixSuffix(".", Id("a")))), PostfixExpr(Id("b"), List(PointerPostfixSuffix("->", Id("a"))))),
            """|b
        					|#ifdef a
        					|.
        					|#else
        					|->
        					|#endif
        					|a""", p.postfixExpr)
        assertParseResult(Alt(fa, PostfixExpr(Id("b"), List(SimplePostfixSuffix("++"))), Id("b")),
            """|b
        					|#ifdef a
        					|++
        					|#endif""", p.postfixExpr)

    }
    def testUnaryExpr {
        assertParseResult(Id("b"), "b", p.unaryExpr)
        assertParseResult(UnaryExpr("++", Id("b")), "++b", p.unaryExpr)
        assertParseResult(SizeOfExprT(Id("b")), "sizeof(b)", p.unaryExpr)
        assertParseResult(SizeOfExprU(Id("b")), "sizeof b", p.unaryExpr)
        assertParseResult(SizeOfExprU(UnaryExpr("++", Id("b"))), "sizeof ++b", p.unaryExpr)
        assertParseResult(UCastExpr("+", CastExpr(Id("c"), Id("b"))), "+(c)b", List(p.unaryExpr))
        assertParseResult(UCastExpr("&", CastExpr(Id("c"), Id("b"))), "&(c)b", List(p.unaryExpr))
        assertParseResult(UCastExpr("!", CastExpr(Id("c"), Id("b"))), "!(c)b", List(p.unaryExpr))
        assertParseError("(c)b", List(p.unaryExpr))
    }

    def testCastExpr {
        assertParseResult(CastExpr(Id("c"), SizeOfExprT(Id("b"))), "(c)sizeof(b)", List(p.castExpr /*, p.unaryExpr*/ ))
        assertParseResult(CastExpr(Id("c"), Id("b")), "(c)b", List(p.castExpr /*, p.unaryExpr*/ ))
        assertParseResult(CastExpr(Id("a"), CastExpr(Id("b"), CastExpr(Id("c"), SizeOfExprT(Id("b"))))), "(a)(b)(c)sizeof(b)", List(p.castExpr /*, p.unaryExpr*/ ))
    }

    def testNAryExpr {
        def a = Id("a")
        def b = Id("b")
        assertParseResult(NAryExpr(a, List(("*", b))), "a*b", p.multExpr)
        assertParseResult(NAryExpr(a, List(("*", b), ("*", b))), "a*b*b", p.multExpr)
    }

}