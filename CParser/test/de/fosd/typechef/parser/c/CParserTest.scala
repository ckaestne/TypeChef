package de.fosd.typechef.parser.c

import junit.framework._;
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._

class CParserTest extends TestCase {
    val p = new CParser()
    def a = Id("a"); def b = Id("b"); def c = Id("c"); def d = Id("d"); def x = Id("x");
    def o[T](x: T) = Opt(FeatureExpr.base, x)

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
                if (unparsed.atEnd)
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
        assertParseResult(PostfixExpr(Id("b"), List(FunctionCall(ExprList(List())))), "b()", List(p.postfixExpr, p.unaryExpr))
        assertParseResult(PostfixExpr(Id("b"), List(FunctionCall(ExprList(List(a, b, c))))), "b(a,b,c)", List(p.postfixExpr, p.unaryExpr))
        assertParseResult(PostfixExpr(Id("b"), List(FunctionCall(ExprList(List())), FunctionCall(ExprList(List(a))))), "b()(a)", List(p.postfixExpr, p.unaryExpr))
        assertParseResult(PostfixExpr(Id("b"), List(ArrayAccess(a))), "b[a]", List(p.postfixExpr, p.unaryExpr))
        assertParseResult(PostfixExpr(Id("b"), List(FunctionCall(ExprList(List())), ArrayAccess(a))), "b()[a]", List(p.postfixExpr, p.unaryExpr))

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
        assertParseResult(NAryExpr(a, List(("*", b))), "a*b", p.multExpr)
        assertParseResult(NAryExpr(a, List(("*", b), ("*", b))), "a*b*b", p.multExpr)
    }
    def testExprs {
        assertParseResult(NAryExpr(NAryExpr(a, List(("*", b))), List(("+", c))), "a*b+c", p.expr)
        assertParseResult(NAryExpr(c, List(("+", NAryExpr(a, List(("*", b)))))), "c+a*b", p.expr)
        assertParseResult(NAryExpr(NAryExpr(a, List(("+", b))), List(("*", c))), "(a+b)*c", p.expr)
        assertParseResult(AssignExpr(a, "=", NAryExpr(b, List(("==", c)))), "a=b==c", p.expr)
        assertParseResult(NAryExpr(a, List(("/", b))), "a/b", p.expr)
        assertParseResult(ConditionalExpr(a, b, c), "a?b:c", p.expr)
        assertParseResult(ExprList(List(a, b, NAryExpr(NAryExpr(c, List(("+", NAryExpr(c, List(("/", d)))))), List(("|", x))))), "a,b,c+c/d|x", p.expr)
    }
    def testAltExpr {
        assertParseResult(Alt(fa, a, b),
            """|#ifdef a
        					|a
        					|#else
        					|b
        					|#endif""", p.expr)
        assertParseResult(Alt(fa, NAryExpr(a, List(("+", c))), NAryExpr(b, List(("+", c)))),
            """|#ifdef a
        					|a +
        					|#else
        					|b +
        					|#endif
        					|c""", p.expr)
        assertParseResult(Alt(fa, AssignExpr(a, "=", ConditionalExpr(b, b, d)), AssignExpr(a, "=", ConditionalExpr(b, c, d))),
            """|a=b?
        					|#ifdef a
        					|b 
        					|#else
        					|c 
        					|#endif
        					|:d""", p.expr)
    }
    
    def notestStatements {
        assertParseable("a;", p.statement)
        assertParseable("a(x->i);", p.statement)
        assertParseable("while (x) a;", p.statement)
        assertParseable(";", p.statement)
        assertParseable("if (a) b; ", p.statement)
        assertParseable("if (a) {b;c;} ", p.statement)
        assertParseable("if (a) b; else c;", p.statement)
        assertParseable("if (a) if (b) if (c) d; ", p.statement)
        assertParseable("{a;b;}", p.statement)
        assertParseable("case a: x;", p.statement)
        assertParseable("break;", p.statement)
        assertParseable("a:", p.statement)
        assertParseable("goto x;", p.statement)
        assertParseResult(AltStatement(fa, IfStatement(a, ExprStatement(b), None), ExprStatement(b)),
            """|#ifdef a
        					|if (a)
        					|#endif
    			  			|b;""", p.statement)
        assertParseResult(IfStatement(a, AltStatement(fa, ExprStatement(b), ExprStatement(c)), None),
            """|if (a)
    			  			|#ifdef a
        					|b;
        					|#else
    			  			|c;
        					|#endif""", p.statement)
        assertParseAnyResult(AltStatement(fa, CompoundStatement(List(),List(o(IfStatement(a, ExprStatement(b), None)), o(ExprStatement(c)))), CompoundStatement(List(),List(o(IfStatement(a, ExprStatement(c), None))))),
            """|{
        		|if (a)
    			  			|#ifdef a
        					|b;
        					|#endif
    			  			|c;}""", p.statement)

        assertParseAnyResult(CompoundStatement(List(),List(o(ExprStatement(a)), Opt(fa, ExprStatement(b)), o(ExprStatement(c)))),
            """|{
        		|a;
    			  			|#ifdef a
        					|b;
        					|#endif
    			  			|c;}""", p.statement)
    }
    def testParameterDecl {
    	assertParseable("void", p.parameterDeclaration)
    	assertParseable("extern void", p.parameterDeclaration)
    	assertParseable("extern void", p.parameterDeclaration)
    	assertParseable("void *", p.parameterDeclaration)
    	assertParseable("void *[]", p.parameterDeclaration)
    	assertParseable("void *[a]", p.parameterDeclaration)
    	assertParseable("void *(*[])", p.parameterDeclaration)
    	assertParseable("void *()", p.parameterDeclaration)
    	assertParseable("void *(void, int)", p.parameterDeclaration)
    	assertParseable("void ****(void, int)", p.parameterDeclaration)
    	assertParseable("void ****a", p.parameterDeclaration)
    }
    def testDeclarator {
    	assertParseResult(DeclaratorId(List(),a,List()), "a", p.declarator(false))
    	assertParseResult(DeclaratorDecl(List(),DeclaratorId(List(),a,List(DeclArrayAccess(None))),List()), "(a[])", p.declarator(false))
    	assertParseResult(DeclaratorId(List(Pointer(List())),a,List()), "*a", p.declarator(false))
    	assertParseResult(DeclaratorId(List(Pointer(List()),Pointer(List())),a,List()), "**a", p.declarator(false))
    	assertParseResult(DeclaratorId(List(Pointer(List(OtherSpecifier("const")))),a,List()), "*const a", p.declarator(false))
    	assertParseResult(DeclaratorId(List(Pointer(List(OtherSpecifier("const"),OtherSpecifier("volatile")))),a,List()), "*const volatile a", p.declarator(false))
    	assertParseResult(DeclaratorId(List(),a,List(DeclArrayAccess(None))), "a[]", p.declarator(false))
//    	assertParseResult(DeclaratorId(List(),a,List(DeclIdentifierList(List(a,b)))), "a(a,b)", p.declarator(false))
//    	assertParseResult(DeclaratorId(List(),a,List(DeclParameterTypeList(List()))), "a()", p.declarator(false))
    }
}