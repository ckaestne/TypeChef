package de.fosd.typechef.parser.c

import junit.framework._;
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._
import de.fosd.typechef.conditional._
import org.junit.{Ignore, Test}
import FeatureExprFactory._

class CParserTest extends TestHelper {
    val p = new CParser()


    def assertParseResult(expected: AST, code: String, mainProduction: p.MultiParser[AST]) {
        assertParseResult(One(expected), code, mainProduction ^^ {
            One(_)
        })
    }

    def assertParseResult(expected: Conditional[AST], code: String, mainProduction: p.MultiParser[Conditional[AST]]) {
        val actual = p.parse(lex(code.stripMargin), mainProduction).expectOneResult
        System.out.println(actual)
        actual match {
            case p.Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                assertEquals("incorrect parse result", expected, ast)
                //                assertTree(ast)
                //TODO False nodes not reported. filter later.(?) cf. Issue #4
                //                assertNoDeadNodes(ast)
            }
            case p.NoSuccess(msg, unparsed, inner) =>
                fail(msg + " at " + unparsed + " " + inner)
        }
    }

    def assertParseResultL(expected: AST, code: String, productions: List[p.MultiParser[AST]]) {
        assertParseResultL(One(expected), code, productions.map(_ ^^ {
            One(_)
        }))
    }

    def assertParseResultL(expected: Conditional[AST], code: String, productions: List[p.MultiParser[Conditional[AST]]]) {
        for (production <- productions)
            assertParseResult(expected, code, production)
    }

    def assertParseable(code: String, mainProduction: (TokenReader[CToken, CTypeContext], FeatureExpr) => p.MultiParseResult[Any]): Unit = {
        val actual = p.parseAny(lex(code.stripMargin), mainProduction)
        System.out.println(actual)
        (actual: @unchecked) match {
            case p.Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                if (ast.isInstanceOf[AST]) {
                    //                    assertTree(ast.asInstanceOf[AST])
                    //TODO False nodes not reported. filter later.(?) cf. Issue #4
                    //                    assertNoDeadNodes(ast.asInstanceOf[AST])
                }
                //succeed
            }
            case p.NoSuccess(msg, unparsed, inner) =>
                fail(msg + " at " + unparsed + " " + inner)
        }
    }

    def assertParseableAST[T](code: String, mainProduction: (TokenReader[CToken, CTypeContext], FeatureExpr) => p.MultiParseResult[T]): Option[T] = {
        val actual = p.parse(lex(code.stripMargin), mainProduction)
        System.out.println(actual)
        (actual: @unchecked) match {
            case p.Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                Some(ast)
                //succeed
            }
            case p.NoSuccess(msg, unparsed, inner) =>
                fail(msg + " at " + unparsed + " [[" + unparsed.context + "]] " + inner)
                None
        }
    }

    def assertParseAnyResult(expected: Any, code: String, mainProduction: (TokenReader[CToken, CTypeContext], FeatureExpr) => p.MultiParseResult[Any]) {
        val actual = p.parseAny(lex(code.stripMargin), mainProduction)
        System.out.println(actual)
        (actual: @unchecked) match {
            case p.Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                assertEquals("incorrect parse result", expected, ast)
            }
            case p.NoSuccess(msg, unparsed, inner) =>
                fail(msg + " at " + unparsed + " " + inner)
        }
    }

    def assertParseError(code: String, mainProduction: (TokenReader[CToken, CTypeContext], FeatureExpr) => p.MultiParseResult[Any], expectErrorMsg: Boolean = false) {
        val actual = p.parseAny(lex(code.stripMargin), mainProduction)
        System.out.println(actual)
        (actual: @unchecked) match {
            case p.Success(ast, unparsed) => {
                if (expectErrorMsg || unparsed.atEnd)
                    Assert.fail("parsing succeeded unexpectedly with " + ast + " - " + unparsed)
            }
            case p.NoSuccess(msg, unparsed, inner) => ;
        }
    }

    def assertParseError(code: String, productions: List[(TokenReader[CToken, CTypeContext], FeatureExpr) => p.MultiParseResult[Any]]) {
        for (production <- productions)
            assertParseError(code, production)
    }

    def a = Id("a");

    def b = Id("b");

    def c = Id("c");

    def d = Id("d");

    def x = Id("x");

    def intType = TypeName(lo(IntSpecifier()), None)

    def o[T](x: T) = Opt(FeatureExprFactory.True, x)

    def lo[T](x: T) = List(o(x))

    def lo[T](x: T, y: T) = List(o(x), o(y))

    def lo[T](x: T, y: T, z: T) = List(o(x), o(y), o(z))

    override val fa = FeatureExprFactory.createDefinedExternal("a")

    @Test
    def testId() {
        assertParseResultL(Id("test"), "test", List(p.primaryExpr, p.ID))
        assertParseResultL(Choice(fa, Id("test"), Id("bar")), """|#ifdef a
                                                                	 				|test
                                                                	 				|#else
                                                                	 				|bar
                                                                	 				|#endif""", List(p.primaryExpr !, p.ID !))
        assertParseError("case", List(p.primaryExpr, p.ID))
    }

    @Test
    def testStringLit() {
        assertParseResultL(StringLit(lo("\"test\"")), "\"test\"", List(p.primaryExpr, p.stringConst))
        assertParseResultL(Choice(fa, StringLit(lo("\"test\"")), StringLit(lo("\"ba\\\"r\""))), """|#ifdef a
                                                                                                  	 				|"test"
                                                                                                  	 				|#else
                                                                                                  	 				|"ba\"r"
                                                                                                  	 				|#endif""", List(p.primaryExpr !, p.stringConst !))
        assertParseError("'c'", List(p.stringConst))
    }

    @Test
    def testConstant() {
        def parseConstant(const: String) {
            assertParseResult(Constant(const), const, p.numConst)
        }
        def parseString(const: String) {
            assertParseResult(StringLit(lo(const)), const, p.stringConst)
        }
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
        parseConstant("3.0f")
        parseConstant("3.0fi")
        parseConstant("3L")
        parseConstant("3U")
        parseConstant("3l")
        parseConstant("3I")
        parseConstant("3i")
        parseConstant("3j")
        parseConstant("3J")
        parseConstant(".1E2l")
        parseConstant("'a'")
        parseConstant("L'a'")
        parseString("L\"a\"")
        assertParseResultL(Choice(fa, Constant("1"), Constant("2")), """|#ifdef a
                                                                       	 				|1
                                                                       	 				|#else
                                                                       	 				|2
                                                                       	 				|#endif""", List(p.primaryExpr !, p.numConst !))
    }

    @Test def testDots() {
        assertParseable(".", p.DOT)
        assertParseable("...", p.VARARGS)
        assertParseError("...", p.DOT)
        assertParseError(".", p.VARARGS)
    }

    @Test def testPostfixSuffix {
        assertParseAnyResult(List(PointerPostfixSuffix("->", Id("a"))), "->a", p.postfixSuffix)
        assertParseAnyResult(List(PointerPostfixSuffix("->", Id("a"))), "->    a", p.postfixSuffix)
        assertParseAnyResult(List(PointerPostfixSuffix("->", Id("a")), PointerPostfixSuffix("->", Id("a"))), "->a->a", p.postfixSuffix)
        assertParseAnyResult(List(PointerPostfixSuffix(".", Id("a"))), ".a", p.postfixSuffix)
        assertParseAnyResult(List(SimplePostfixSuffix("++")), "++", p.postfixSuffix)
        assertParseAnyResult(List(SimplePostfixSuffix("++"), SimplePostfixSuffix("--")), "++ --", p.postfixSuffix)
    }

    @Test def testPostfixExpr {
        assertParseResult(Choice(fa, PostfixExpr(Id("b"), SimplePostfixSuffix("++")), Id("b")),
            """|b
              	 				|#ifdef a
              	 				|++
              	 				|#endif""", p.postfixExpr !)

        assertParseResultL(PostfixExpr(Id("b"), PointerPostfixSuffix("->", Id("a"))), "b->a", List(p.postfixExpr, p.unaryExpr))
        assertParseResultL(Id("b"), "b", List(p.postfixExpr, p.unaryExpr))
        assertParseResultL(PostfixExpr(Id("b"), FunctionCall(ExprList(List()))), "b()", List(p.postfixExpr, p.unaryExpr))
        assertParseResultL(PostfixExpr(Id("b"), FunctionCall(ExprList(lo(a, b, c)))), "b(a,b,c)", List(p.postfixExpr, p.unaryExpr))
        assertParseResultL(PostfixExpr(PostfixExpr(Id("b"), FunctionCall(ExprList(List()))), FunctionCall(ExprList(lo(a)))), "b()(a)", List(p.postfixExpr, p.unaryExpr))
        assertParseResultL(PostfixExpr(Id("b"), ArrayAccess(a)), "b[a]", List(p.postfixExpr, p.unaryExpr))
        assertParseResultL(PostfixExpr(PostfixExpr(Id("b"), FunctionCall(ExprList(List()))), ArrayAccess(a)), "b()[a]", List(p.postfixExpr, p.unaryExpr))

        //TODO
        //        assertParseAnyResult(
        //            PostfixExpr(Id("b"),
        //                List(Opt(fa, PointerPostfixSuffix(".", Id("a"))), Opt(fa.not, PointerPostfixSuffix("->", Id("a"))))),
        //            """|b
        //        					|#ifdef a
        //        					|.
        //        					|#else
        //        					|->
        //        					|#endif
        //        					|a""", p.postfixExpr)
        assertParseResult(Choice(fa,
            PostfixExpr(Id("b"), PointerPostfixSuffix(".", Id("a"))),
            PostfixExpr(Id("b"), PointerPostfixSuffix("->", Id("a")))),
            """|b
              	 				|#ifdef a
              	 				|.
              	 				|#else
              	 				|->
              	 				|#endif
              	 				|a""", p.postfixExpr !)
        assertParseable("++", p.postfixSuffix)
        assertParseable("b++", p.postfixExpr)
        assertParseable("__builtin_offsetof(void,a.b)", p.primaryExpr)
        assertParseable("__builtin_offsetof(void,a[1])", p.primaryExpr)
        assertParseable("c", p.castExpr)
        assertParseable("__real__", p.unaryOperator)
        assertParseable("__real__ c", p.unaryOperator ~ p.castExpr)
        assertParseable("__real__ c", p.unaryExpr)
    }

    @Test def testUnaryExpr {
        assertParseResult(Id("b"), "b", p.unaryExpr)
        assertParseResult(UnaryExpr("++", Id("b")), "++b", p.unaryExpr)
        assertParseResult(SizeOfExprT(intType), "sizeof(int)", p.unaryExpr)
        assertParseResult(SizeOfExprU(Id("b")), "sizeof b", p.unaryExpr)
        assertParseResult(SizeOfExprU(UnaryExpr("++", Id("b"))), "sizeof ++b", p.unaryExpr)
        assertParseResult(UnaryOpExpr("+", CastExpr(intType, Id("b"))), "+(int)b", (p.unaryExpr))
        assertParseResult(PointerCreationExpr(CastExpr(intType, Id("b"))), "&(int)b", (p.unaryExpr))
        assertParseResult(UnaryOpExpr("!", CastExpr(intType, Id("b"))), "!(int)b", (p.unaryExpr))
        assertParseError("(c)b", List(p.unaryExpr))
    }

    @Test def testCastExpr {
        assertParseResultL(CastExpr(intType, SizeOfExprT(intType)), "(int)sizeof(int)", List(p.castExpr /*, p.unaryExpr*/))
        assertParseResultL(CastExpr(intType, Id("b")), "(int)b", List(p.castExpr /*, p.unaryExpr*/))
        assertParseResultL(CastExpr(intType, CastExpr(intType, CastExpr(intType, SizeOfExprT(intType)))), "(int)(int)(int)sizeof(int)", List(p.castExpr /*, p.unaryExpr*/))
        assertParseable("(int)sizeof(void)", p.castExpr)
    }

    @Test def testNAryExpr {
        assertParseResult(NAryExpr(a, List(o(NArySubExpr("*", b)))), "a*b", p.multExpr)
        assertParseResult(NAryExpr(a, List(o(NArySubExpr("*", b)), o(NArySubExpr("*", b)))), "a*b*b", p.multExpr)
    }

    @Test def testExprs {
        assertParseResult(NAryExpr(NAryExpr(a, List(o(NArySubExpr("*", b)))), List(o(NArySubExpr("+", c)))), "a*b+c", p.expr)
        assertParseResult(NAryExpr(c, List(o(NArySubExpr("+", NAryExpr(a, List(o(NArySubExpr("*", b)))))))), "c+a*b", p.expr)
        assertParseResult(NAryExpr(NAryExpr(a, List(o(NArySubExpr("+", b)))), List(o(NArySubExpr("*", c)))), "(a+b)*c", p.expr)
        assertParseResult(AssignExpr(a, "=", NAryExpr(b, List(o(NArySubExpr("==", c))))), "a=b==c", p.expr)
        assertParseResult(NAryExpr(a, List(o(NArySubExpr("/", b)))), "a/b", p.expr)
        assertParseResult(ConditionalExpr(a, Some(b), c), "a?b:c", p.expr)
        assertParseResult(ExprList(List(o(a), o(b), o(NAryExpr(NAryExpr(c, List(o(NArySubExpr("+", NAryExpr(c, List(o(NArySubExpr("/", d)))))))), List(o(NArySubExpr("|", x))))))), "a,b,c+c/d|x", p.expr)
    }

    @Test def testAltExpr {
        assertParseResult(Choice(fa, a, b),
            """|#ifdef a
              	 				|a
              	 				|#else
              	 				|b
              	 				|#endif""", p.expr !)
        assertParseResult(Choice(fa, NAryExpr(a, List(Opt(True, NArySubExpr("+", c)))), NAryExpr(b, List(Opt(True, (NArySubExpr("+", c)))))),
            """|#ifdef a
              	 				|a +
              	 				|#else
              	 				|b +
              	 				|#endif
              	 				|c""", p.expr !)
        assertParseResult(Choice(fa, AssignExpr(a, "=", ConditionalExpr(b, Some(b), d)), AssignExpr(a, "=", ConditionalExpr(b, Some(c), d))),
            """|a=b?
              	 				|#ifdef a
              	 				|b
              	 				|#else
              	 				|c
              	 				|#endif
              	 				|:d""", p.expr !)
    }

    private implicit def makeConditionalOne[T <: AST](a: T): Conditional[T] = One(a)

    @Test def testStatements {
        assertParseable("a;", p.statement)
        assertParseable("{}", p.compoundStatement)
        assertParseable("{}", p.statement)
        assertParseable("a(x->i);", p.statement)
        assertParseable("while (x) a;", p.statement)
        assertParseable(";", p.statement)
        assertParseable("if (a) b; ", p.statement)
        assertParseable("if (a) {b;c;} ", p.statement)
        assertParseable("if (a) b; else c;", p.statement)
        assertParseable("if (a) if (b) if (c) d; ", p.statement)
        assertParseable("{a;b;}", p.statement)
        //assertParseable("case a: x;", p.statement)
        assertParseable("break;", p.statement)
        assertParseable("a:", p.statement)
        assertParseable("goto x;", p.statement)
        assertParseResult(Choice(fa, One(IfStatement(a, ExprStatement(b), List(), None)), One(ExprStatement(b))),
            """|#ifdef a
              	 				|if (a)
              	 				|#endif
              	 		  			|b;""", p.statement)
        assertParseResult(IfStatement(a, Choice(fa, One(ExprStatement(b)), One(ExprStatement(c))), List(), None),
            """|if (a)
              	 		  			|#ifdef a
              	 				|b;
              	 				|#else
              	 		  			|c;
              	 				|#endif""", p.statement)
        assertParseAnyResult(One(CompoundStatement(List(
            Opt(fa, IfStatement(a, ExprStatement(b), List(), None)),
            Opt(fa.not, IfStatement(a, ExprStatement(c), List(), None)),
            Opt(fa, ExprStatement(c))
        ))),
            """|{
              	 	|if (a)
              	 		  			|#ifdef a
              	 				|b;
              	 				|#endif
              	 		  			|c;}""", p.statement)

        assertParseAnyResult(One(CompoundStatement(List(o(ExprStatement(a)), Opt(fa, ExprStatement(b)), o(ExprStatement(c))))),
            """|{
              	 	|a;
              	 		  			|#ifdef a
              	 				|b;
              	 				|#endif
              	 		  			|c;}""", p.statement)
    }

    @Test def testLocalDeclarations {
        assertParseableAST("{int * a = 3;}", p.compoundStatement) match {
            case Some(CompoundStatement(List(Opt(_, (DeclarationStatement(_)))))) =>
            case e => fail("expected declaration, found " + e)
        }
        assertParseableAST("{t * a = 3;}", p.compoundStatement) match {
            case Some(CompoundStatement(List(Opt(_, (ExprStatement(AssignExpr(_, _, _))))))) =>
            case e => fail("expected declaration, found " + e)
        }
        assertParseable("__builtin_type * a = 3;", p.compoundDeclaration)
        assertParseableAST("{__builtin_type * a = 3;}", p.compoundStatement) match {
            case Some(CompoundStatement(List(Opt(_, (DeclarationStatement(_)))))) =>
            case e => fail("expected declaration, found " + e)
        }
    }

    @Test def testParameterDecl {
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

    @Test def testDeclarator {
        assertParseResult(AtomicNamedDeclarator(List(), a, List()), "a", p.declarator)
        assertParseResult(NestedNamedDeclarator(List(), AtomicNamedDeclarator(List(), a, lo(DeclArrayAccess(None))), List(), List()), "(a[])", p.declarator)
        assertParseResult(AtomicNamedDeclarator(lo(Pointer(List())), a, List()), "*a", p.declarator)
        assertParseResult(AtomicNamedDeclarator(lo(Pointer(List()), Pointer(List())), a, List()), "**a", p.declarator)
        assertParseResult(AtomicNamedDeclarator(lo(Pointer(lo(ConstSpecifier()))), a, List()), "*const a", p.declarator)
        assertParseResult(AtomicNamedDeclarator(lo(Pointer(lo(ConstSpecifier(), VolatileSpecifier()))), a, List()), "*const volatile a", p.declarator)
        assertParseResult(AtomicNamedDeclarator(List(), a, lo(DeclArrayAccess(None))), "a[]", p.declarator)
        //    	assertParseResult(AtomicNamedDeclarator(List(),a,List(DeclIdentifierList(List(a,b)))), "a(a,b)", p.declarator(false))
        //    	assertParseResult(AtomicNamedDeclarator(List(),a,List(DeclParameterTypeList(List()))), "a()", p.declarator(false))
    }

    @Test def testEnumerator {
        assertParseable("enum e", p.enumSpecifier)
        assertParseable("enum e { a }", p.enumSpecifier)
        assertParseable("enum { a }", p.enumSpecifier)
        assertParseable("enum e { a=1, b=3 }", p.enumSpecifier)
        assertParseError("enum {  }", p.enumSpecifier)
    }

    @Test def testStructOrUnion {
        assertParseable("struct a", p.structOrUnionSpecifier)
        assertParseable("union a", p.structOrUnionSpecifier)
        assertParseable("x ", p.structDeclarator)
        assertParseable("void ", p.specifierQualifierList)
        assertParseError("void x", p.specifierQualifierList)
        assertParseable(" void x; ", p.structDeclarationList0)
        assertParseable("struct { void x; }", p.structOrUnionSpecifier)
        assertParseable("struct { void x,y; }", p.structOrUnionSpecifier)
        assertParseable("struct a{ void x; int x:3+2,z:3;}", p.structOrUnionSpecifier)
        assertParseable("struct {  }", p.structOrUnionSpecifier)
        assertParseError("struct { void x }", p.structOrUnionSpecifier)
        assertParseable("struct { void x; ; }", p.structOrUnionSpecifier)
        assertParseable("struct { ; }", p.structOrUnionSpecifier)
        assertParseable("struct { ; ; }", p.structOrUnionSpecifier)
    }

    @Test def testAsmExpr {
        assertParseable("asm ( 3+3);", p.asm_expr)
        assertParseable("asm volatile ( 3+3);", p.asm_expr)
        assertParseable("""
                 asm volatile("2: rdmsr ; xor %[err],%[err]\n"
                       "1:\n\t"
                       ".section .fixup,\"ax\"\n\t"
                       "3:  mov %[fault],%[err] ; jmp 1b\n\t"
                       ".previous\n\t"
                       " .section __ex_table,\"a\"\n"
                       : "c" (msr), [fault] "i" (-5))
            """, p.expr)
    }

    @Test def testFunctionDef {

        assertParseable("int a", p.parameterDeclList)
        assertParseError("int a)", p.parameterDeclList)
        //        assertParseable("(int a)", p.declaratorParamaterList)
        assertParseable("void foo(){}", p.functionDef)
        assertParseable("void foo(){a;}", p.functionDef)
        assertParseable("void foo(int a) { a; }", p.functionDef)
        assertParseable( """|void
                           	 			|#ifdef X
                           	 			|foo
                           	 			|#else
                           	 			|bar
                           	 			|#endif
                           	 			|(){}
                           	 			|void x(){}""", p.translationUnit)
        assertParseable("main(){}", p.functionDef)
        assertParseable("main(){int T=100, a=(T)+1;}", p.functionDef)
        assertParseable( """
        int
main (int argc, char **argv)
{
  int size = 10;

  typedef struct {
    char val[size];
  } block;
  block retframe_block()
    {
      return *(block*)0;
    }
  test();
  return 0;
}
                         """, p.functionDef)
        assertParseable( """main(){
          for (;1;) ;
        }
                         """, p.functionDef)
        assertParseable( """
      int foo(void) {
        a = 0;
        l1: b = a + 1;
        c = c + b;
        a = b + 2;
        if (a) goto l1;
        return c;
      }
                         """, p.functionDef)

    }

    @Test def testTypedefName {
        assertParseable("int a;", p.translationUnit)
        assertParseError("foo a;", p.translationUnit)
        assertParseable("typedef int foo; foo a;", p.translationUnit)
        assertParseable("__builtin_type a;", p.translationUnit)
        assertParseable("(notATypeName)", p.expr)
        assertParseable("(__builtin_type)", p.expr)
        assertParseable("3+(__builtin_type)", p.expr)
        //scoping of typedef not considered yet:
        //assertParseable("typedef int T;main(){int T=100, a=(T)+1;}", p.functionDef)
    }

    @Test def testAttribute {
        assertParseable("", p.attributeList)
        assertParseable("__attribute__((a b))", p.attributeDecl)
        assertParseable("__attribute__(())", p.attributeDecl)
        assertParseable("__attribute__((a,b))", p.attributeDecl)
        assertParseable("__attribute__((a,(b,b)))", p.attributeDecl)
    }

    @Test def testMethodLookAhead {
        //should return parse error instead of empty parse result with unparsed tokens
        assertParseError("void main () { int a; ", p.translationUnit, true)
        assertParseError("int main () { abs = ", p.translationUnit, true)
    }

    @Test def testInitializer {
        assertParseable("a", p.initializer)
        assertParseable(".a = 3", p.initializer)
        assertParseable("a: 3", p.initializer)
        assertParseable("[3] = 3", p.initializer)
        assertParseable("{}", p.initializer)
        assertParseable("{3}", p.initializer)
        assertParseable("{3,4}", p.initializer)
        assertParseable("{{3},4}", p.initializer)
        assertParseable("{.l={{.r={.w={1},.m=2}}},.c=2}", p.initializer)
        assertParseable("{ .lock = { { .rlock = { .raw_lock = { 1 } } } } }", p.initializer)
        assertParseable("{ .lock = (int) { { .rlock = { .raw_lock = { 1 } } } } }", p.initializer)
        assertParseable("{ .lock = { { .rlock = { .raw_lock = { 1 } } } } }", p.initializer)
        assertParseable("{ .entry.mask = 1 }", p.initializer) //from io_apic.c
        assertParseable("{ [2].y = yv2, [2].x = xv2, [0].x = xv0 }", p.initializer) //from gcc spec
        assertParseable("{ [' '] = 1, ['\\t'] = 1, ['\\h'] = 1,\n           ['\\f'] = 1, ['\\n'] = 1, ['\\r'] = 1 }", p.initializer) //from gcc spec
        assertParseable("{ [1] = v1, v2, [4] = v4 }", p.initializer) //from gcc spec
        assertParseable("{ y: yvalue, x: xvalue }", p.initializer) //from gcc spec
        assertParseable("{ .y = yvalue, .x = xvalue }", p.initializer) //from gcc spec
        assertParseable("{ [0 ... 9] = 1, [10 ... 99] = 2, [100] = 3 }", p.initializer) //from gcc spec
        assertParseable("(int) { .lock = (int) { { .rlock = { .raw_lock = { 1 } } } } }", p.castExpr)
        assertParseable("(int) { .lock = (int) { { .rlock = { .raw_lock = { 1 } } } } }", p.expr)
        assertParseable("sem = (int) { .lock = (int) { { .rlock = { .raw_lock = { 1 } } } } };", p.statement)
    }

    @Test def testInitializerAlt =
        assertParseable( """{
        #ifdef X
        {3}
        #else
        {2}
        #endif
        ,4}""", p.initializer)


    @Test def testAsm {
        assertParseableAST("asm (\"A\");", p.externalDef) match {
            case Some(One(AsmExpr(false, _))) =>
            case e => Assert.fail(e.toString)
        }
        assertParseableAST("int asm (\"A\") a;", p.externalDef) match {
            case Some(One(x: Declaration)) =>
            case e => Assert.fail(e.toString)
        }
        assertParseableAST("asm (\"A\") int a;", p.externalDef) match {
            case Some(One(x: Declaration)) => println("done")
            case e => Assert.fail(e.toString)
        }
    }

    @Test def testMisc0 {
        assertParseable("{__label__ hey, now;}", p.compoundStatement)
        assertParseable("{abs = ({__label__ hey, now;});}", p.compoundStatement)
        assertParseable("extern int my_printf (void *my_object, const char *my_format);", p.externalDef)
        assertParseable("extern int my_printf (void *my_object, const char *my_format) __attribute__ ((format (printf, 2, 3)));", p.externalDef)
        assertParseable("extern int my_printf (void *my_object, const char *my_format, ...) __attribute__ ((format (printf, 2, 3)));", p.phrase(p.externalDef))
        assertParseable("asm volatile (\".set noreorder\");", p.statement)
        assertParseable( """asm volatile (".set noreorder\n"
              ".set noat\n"
              ".set mips3");""", p.statement)
        assertParseable( """
        asm volatile("rep ; movsl\n\t"
		     "movl %4,%%ecx\n\t"
		     "andl $3,%%ecx\n\t"
		     "jz 1f\n\t"
		     "rep ; movsb\n\t"
		     "1:"
		     : "=&c" (d0), "=&D" (d1), "=&S" (d2)
		     : "0" (n / 4), "g" (n), "1" ((long)to), "2" ((long)from)
		     : "memory");""", p.statement)

        assertParseable("enum { DDD = -7 }", p.enumSpecifier)
        assertParseable("char                        hgfretty[99 ];", p.structDeclaration)
        assertParseable(" struct  pojeqsd {    char                        hgfretty[99 ];}", p.structOrUnionSpecifier)
    }

    @Test def testMisc1a = assertParseable( """typedef struct  pojeqsd {
    			char                        hgfretty[99 ];
    		} pojeqsd_t;""", p.translationUnit)

    @Test def testMisc1b = assertParseable( """typedef int hgfretty;
				typedef struct  pojeqsd {
				char                        hgfretty[99 ];
    		} pojeqsd_t;""", p.translationUnit)

    @Test def testMisc2 = assertParseable("( checkme )->j76g", p.expr)

    @Test def testMisc2b = assertParseable("if ((( checkme )->j76g) ) { }", p.statement)

    @Test def testMisc3a = assertParseable("(int)q23w3", p.expr)

    @Test def testMisc3b = assertParseable("void *", p.typeName)

    @Test def testMisc3f = assertParseable("__builtin_type *", p.typeName)

    @Test def testMisc3c = assertParseable("++(int)q23w3", p.unaryExpr)

    @Test def testMisc3d = assertParseable("(++(int)q23w3->ll881ss[3])", p.primaryExpr)

    @Test def testMisc3e = assertParseable("(void *) (++(int)q23w3->ll881ss[3])", p.expr)

    @Test def testMisc4 = assertParseable( """if (x3 && x4) {
        char gh554j[19];
        gh554j[0]='\n';
    }""", p.statement)

    @Test def testBoa1 = assertParseable("__attribute__((__cdecl__))", p.attributeDecl)

    @Test def testBoa2 = assertParseable("int (__attribute__((__cdecl__)) * _read) (struct _reent *, void *, char *, int);", p.structDeclaration)

    @Test def testBoa3 = assertParseable( """typedef int FILE;
typedef __builtin_va_list __gnuc_va_list;
int	__attribute__((__cdecl__)) vfprintf (FILE *, const char *, __gnuc_va_list)
 __attribute__ ((__format__(__printf__, 2, 0)));""", p.translationUnit)

    @Test def testBoa4 = assertParseable( """struct alias {
    char *fakename;             /* URI path to file */
    char *realname;             /* Actual path to file */
    int type;                   /* ALIAS, SCRIPTALIAS, REDIRECT */
    int fake_len;               /* strlen of fakename */
    int real_len;               /* strlen of realname */
    struct alias *next;
};

typedef struct alias alias;""", p.translationUnit)

    @Test def testBoa5 = assertParseable( """char *fakename;             /* URI path to file */
    char *realname;             /* Actual path to file */
    int type;                   /* ALIAS, SCRIPTALIAS, REDIRECT */
    int fake_len;               /* strlen of fakename */
    int real_len;               /* strlen of realname */
    struct alias *next;""", p.structDeclarationList0)

    @Test def testOptListBoa1 = assertParseable( """
typedef	char *	caddr_t;
#if defined(GO32)
typedef unsigned long vm_offset_t;
#endif
typedef unsigned long vm_size_t;
                                                 """, p.translationUnit)

    @Test def testEnsureError = assertParseError( """main()
{
  for(;;
	{
      }
}""", p.translationUnit)

    @Test def testLinuxHeader = assertParseable( """
#define __restrict
/* Convert a string to a long long integer.  */
__extension__ extern long long int atoll (__const char *__nptr)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1))) ;



/* Convert a string to a floating-point number.  */
extern double strtod (__const char *__restrict __nptr, char **__restrict __endptr)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;""", p.translationUnit)

    @Test def testDoubleMain = assertParseable( """
int foo() {}
#if defined(X)
int main(void) {}
#endif
                                                """, p.translationUnit)

    @Test def testDoubleMain2 = assertParseable( """
int foo() {}
#if defined(X)
int main(void) {}
#else
int main(void) {}
#endif
                                                 """, p.translationUnit)

    @Test def testIfdefInStatement = assertParseable( """
int foo() {
  foo1();
  while (current) {
#ifdef X
		foo2();
#endif
		if (whatever) {
#ifdef X
			foo3();
#endif
			return x;
		}
#ifdef X
		else
			foo4();
#endif
  }
}
                                                      """, p.translationUnit)

    @Test def testBoaIp1 =
        assertParseable( """
{
#if !(defined(INET6))
    memmove(dest, inet_ntoa(s->sin_addr), len);
#endif
    return dest;
}""", p.compoundStatement)

    @Test def testBoaIp2 =
        assertParseable( """
char *ascii_sockaddr(struct
#if defined(INET6)
sockaddr_storage
#endif
#if !(defined(INET6))
sockaddr_in
#endif
 *s, char *dest, int len)
{
#if !(defined(INET6))
    memmove(dest, inet_ntoa(s->sin_addr), len);
#endif
    return dest;
}""", p.translationUnit)

    @Test
    def testBusyBox1 = assertParseable( """int grep_main(int argc __attribute__ ((__unused__)), char **argv){}""", p.functionDef)

    @Test
    def testBusyBox2 = assertParseable( """
static int  func_name(const char *fileName __attribute__ ((__unused__)), const struct stat *statbuf __attribute__ ((__unused__)), int* ap __attribute__ ((__unused__)))
{
	const char *tmp = bb_basename(fileName);
	if (tmp != fileName && *tmp == '\0') {
		/* "foo/bar/". Oh no... go back to 'b' */
		tmp--;
		while (tmp != fileName && *--tmp != '/')
			continue;
		if (*tmp == '/')
			tmp++;
	}
	/* Was using FNM_PERIOD flag too,
	 * but somewhere between 4.1.20 and 4.4.0 GNU find stopped using it.
	 * find -name '*foo' should match .foo too:
	 */
	return fnmatch(ap->pattern, tmp, (ap->iname ? (1 << 4) : 0)) == 0;
}""", p.functionDef)

    @Test
    def testDecl =
        assertParseable("extern int my_printf (void *my_object, const char *my_format);", p.externalDef)

    @Test def testLinux1 = assertParseable("extern char * \n#if (definedEx(CONFIG_ENABLE_MUST_CHECK) && definedEx(CONFIG_ENABLE_MUST_CHECK))\n__attribute__((warn_unused_result))\n#endif\n#if (!(definedEx(CONFIG_ENABLE_MUST_CHECK)) && !((definedEx(CONFIG_ENABLE_MUST_CHECK) && definedEx(CONFIG_ENABLE_MUST_CHECK))))\n\n#endif\n skip_spaces(const char *);", p.phrase(p.externalDef))


    @Test def testLists {
        assertParseable("short foo(a, c)\n  short a;\n  char c;\n{   return 3;\n}", p.phrase(p.functionDef))
        assertParseable("extern void bb_info_msg(const char *s, ...) __attribute__ ((format (printf, 1, 2))) ;", p.phrase(p.externalDef))
        assertParseable("extern void set_matchpathcon_printf(void (*f) (const char *fmt, ...));", p.phrase(p.externalDef))
        assertParseable("extern void set_matchpathcon_invalidcon(int (*f) (const char *path,\n     unsigned lineno,\n   char *context));", p.phrase(p.externalDef))
        assertParseable("union selinux_callback {\n	/* log the printf-style format and arguments,\n	   with the type code indicating the type of message */\n	int \n\n__attribute__ ((format(printf, 2, 3)))\n\n	(*func_log) (int type, const char *fmt, ...);\n	/* store a string representation of auditdata (corresponding\n	   to the given security class) into msgbuf. */\n	int (*func_audit) (void *auditdata, int cls,\n			   char *msgbuf, int msgbufsize);\n	/* validate the supplied context, modifying if necessary */\n	int (*func_validate) (int *ctx);\n	/* netlink callback for setenforce message */\n	int (*func_setenforce) (int enforcing);\n	/* netlink callback for policyload message */\n	int (*func_policyload) (int seqno);\n};", p.phrase(p.externalDef))

        assertParseable("enum {\n	                        PARM_a         ,\n	                        PARM_o         \n	\n#if definedEx(CONFIG_FEATURE_FIND_NOT)\n,PARM_char_not\n#endif\n#if !(definedEx(CONFIG_FEATURE_FIND_NOT))\n\n#endif\n\n#if definedEx(CONFIG_DESKTOP)\n	                        ,PARM_and       ,\n	                        PARM_or        \n	\n#if definedEx(CONFIG_FEATURE_FIND_NOT)\n,PARM_not\n#endif\n#if !(definedEx(CONFIG_FEATURE_FIND_NOT))\n\n#endif\n\n#endif\n	  ,                      PARM_print     \n	\n#if definedEx(CONFIG_FEATURE_FIND_PRINT0)\n,PARM_print0\n#endif\n#if !(definedEx(CONFIG_FEATURE_FIND_PRINT0))\n\n#endif\n\n	\n#if definedEx(CONFIG_FEATURE_FIND_DEPTH)\n,PARM_depth\n#endif\n#if !(definedEx(CONFIG_FEATURE_FIND_DEPTH))\n\n#endif\n\n	\n#if definedEx(CONFIG_FEATURE_FIND_PRUNE)\n,PARM_prune\n#endif\n#if !(definedEx(CONFIG_FEATURE_FIND_PRUNE))\n\n#endif\n\n	\n#if definedEx(CONFIG_FEATURE_FIND_DELETE)\n,PARM_delete\n#endif\n#if !(definedEx(CONFIG_FEATURE_FIND_DELETE))\n\n#endif\n\n	\n#if definedEx(CONFIG_FEATURE_FIND_EXEC)\n,PARM_exec\n#endif\n#if !(definedEx(CONFIG_FEATURE_FIND_EXEC))\n\n#endif\n\n	\n#if definedEx(CONFIG_FEATURE_FIND_PAREN)\n,PARM_char_brace\n#endif\n#if !(definedEx(CONFIG_FEATURE_FIND_PAREN))\n\n#endif\n\n	/* All options starting from here require argument */\n	                        ,PARM_name      ,\n	                        PARM_iname     \n	\n#if definedEx(CONFIG_FEATURE_FIND_PATH)\n,PARM_path\n#endif\n#if !(definedEx(CONFIG_FEATURE_FIND_PATH))\n\n#endif\n\n	\n#if definedEx(CONFIG_FEATURE_FIND_REGEX)\n,PARM_regex\n#endif\n#if !(definedEx(CONFIG_FEATURE_FIND_REGEX))\n\n#endif\n\n	\n#if definedEx(CONFIG_FEATURE_FIND_TYPE)\n,PARM_type\n#endif\n#if !(definedEx(CONFIG_FEATURE_FIND_TYPE))\n\n#endif\n\n	\n#if definedEx(CONFIG_FEATURE_FIND_PERM)\n,PARM_perm\n#endif\n#if !(definedEx(CONFIG_FEATURE_FIND_PERM))\n\n#endif\n\n	\n#if definedEx(CONFIG_FEATURE_FIND_MTIME)\n,PARM_mtime\n#endif\n#if !(definedEx(CONFIG_FEATURE_FIND_MTIME))\n\n#endif\n\n	\n#if definedEx(CONFIG_FEATURE_FIND_MMIN)\n,PARM_mmin\n#endif\n#if !(definedEx(CONFIG_FEATURE_FIND_MMIN))\n\n#endif\n\n	\n#if definedEx(CONFIG_FEATURE_FIND_NEWER)\n,PARM_newer\n#endif\n#if !(definedEx(CONFIG_FEATURE_FIND_NEWER))\n\n#endif\n\n	\n#if definedEx(CONFIG_FEATURE_FIND_INUM)\n,PARM_inum\n#endif\n#if !(definedEx(CONFIG_FEATURE_FIND_INUM))\n\n#endif\n\n	\n#if definedEx(CONFIG_FEATURE_FIND_USER)\n,PARM_user\n#endif\n#if !(definedEx(CONFIG_FEATURE_FIND_USER))\n\n#endif\n\n	\n#if definedEx(CONFIG_FEATURE_FIND_GROUP)\n,PARM_group\n#endif\n#if !(definedEx(CONFIG_FEATURE_FIND_GROUP))\n\n#endif\n\n	\n#if definedEx(CONFIG_FEATURE_FIND_SIZE)\n,PARM_size\n#endif\n#if !(definedEx(CONFIG_FEATURE_FIND_SIZE))\n\n#endif\n\n	\n#if definedEx(CONFIG_FEATURE_FIND_CONTEXT)\n,PARM_context\n#endif\n#if !(definedEx(CONFIG_FEATURE_FIND_CONTEXT))\n\n#endif\n\n	\n#if definedEx(CONFIG_FEATURE_FIND_LINKS)\n,PARM_links\n#endif\n#if !(definedEx(CONFIG_FEATURE_FIND_LINKS))\n\n#endif\n\n	}", p.phrase(p.enumSpecifier))
    }

    @Test def testIfElIfChain =
        assertParseable(
            """
            if (a) {
                a;
            }
            #ifdef A
            else if (b) {
                b;
            }
            #endif
            #ifdef B
            else if (c) {
                c;
            }
            #endif
            #ifdef C
             else if (d) {
                d;
            }
            #endif
            #ifdef D
             else if (e) {
                e;
            }
            #endif
            #ifdef E
             else if (f) {
                f;
            }
            #endif
            #ifdef F
             else if (f) {
                f;
            }
            #endif
            #ifdef G
             else if (f) {
                f;
            }
            #endif
            #ifdef H
             else if (f) {
                f;
            }
            #endif
            #ifdef I
             else if (f) {
                f;
            }
            #endif
            #ifdef J
             else if (f) {
                f;
            }
            #endif
            #ifdef K
             else if (f) {
                f;
            }
            #endif
            #ifdef L
             else if (f) {
                f;
            }
            #endif
            #ifdef M
             else if (f) {
                f;
            }
            #endif
            #ifdef N
             else if (f) {
                f;
            }
            #endif
            #ifdef O
             else if (f) {
                f;
            }
            #endif
            #ifdef P
             else if (f) {
                f;
            }
            #endif
            else xx;
            """, p.phrase(p.statement))


    @Test def testLinuxAddonCpuidFeature =
        assertParseable( """
struct cpuinfo_x86;
void __attribute__ ((__section__(".cpuinit.text")))  detect_extended_topology(struct cpuinfo_x86 *c)
{
#if definedEx(CONFIG_SMP)
	unsigned int eax, ebx, ecx, edx, sub_index;
	return;
#endif
}
                         """, p.phrase(p.translationUnit))


    @Test def testLinuxSpinlockInitializer =
        assertParseable( """
typedef struct spinlock {} spinlock_t;

         void sema_init(struct semaphore *sem, int val)
{
	*sem = (struct abc) { .lock = (spinlock_t ) { { .rlock = { .raw_lock =
{ 1 }
,
.magic = 0xdead4ead, .owner_cpu = -1, .owner = ((void *)-1L),

.dep_map = { .name = "(*sem).lock" }
 } } }, .count = val, .wait_list = { &((*sem).wait_list), &((*sem).wait_list) }, };
 }
                         """, p.phrase(p.translationUnit))


    @Test def testTypeDefSequence {
        assertParseable( """
            __expectNotType[:a:]
            typedef int a;
            __expectType[:a:]
                         """, p.phrase(p.translationUnit))
        assertParseError( """
            typedef int b;
            __expectType[:a:]
                          """, p.phrase(p.translationUnit))
        // currently this is not checked here! check at type system level!
        //        assertParseError("""
        //            #ifdef X
        //            typedef int a;
        //            #endif
        //            __expectType[:a:]
        //            """, p.phrase(p.translationUnit))
        //        assertParseError("""
        //            #ifdef X
        //            typedef int a;
        //            #endif
        //            #ifdef Z
        //            typedef int b;
        //            #endif
        //            __expectType[:a:]
        //            __expectType[:b:]
        //            """, p.phrase(p.translationUnit))
        assertParseable( """
            #ifdef X
            typedef int a;
            #endif
            int b;
            #ifdef X
            __expectType[:a:]
            #else
            __expectNotType[:a:]
            #endif
                         """, p.phrase(p.translationUnit))

        assertParseable( """
            #ifdef X
            typedef int a;
            #else
            typedef int b;
            #endif
            #ifdef X
            __expectType[:a:]
            __expectNotType[:b:]
            #else
            __expectType[:b:]
            __expectNotType[:a:]
            #endif
                         """, p.phrase(p.translationUnit))


    }


    @Test def testLinux_cstate = assertParseable(
        """
typedef int spinlock_t;
static
#if !definedEx(CONFIG_OPTIMIZE_INLINING)
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline
#endif
void sema_init(struct semaphore *sem, int val)
{
static struct lock_class_key __key;
*sem = (struct semaphore) { .lock = (int ) { { .rlock = { .raw_lock =
{
#if definedEx(CONFIG_SMP)
0   }
#endif
#if !definedEx(CONFIG_SMP)
1 }
#endif

,
#if definedEx(CONFIG_DEBUG_SPINLOCK)
.magic = 0xdead4ead, .owner_cpu = -1, .owner = ((void *)-1L),
#endif
#if !definedEx(CONFIG_DEBUG_SPINLOCK)

#endif

#if definedEx(CONFIG_DEBUG_LOCK_ALLOC)
.dep_map = { .name = "(*sem).lock" }
#endif
#if !definedEx(CONFIG_DEBUG_LOCK_ALLOC)

#endif
} } }, .count = val, .wait_list = { &((*sem).wait_list), &((*sem).wait_list) }, };

#if !definedEx(CONFIG_LOCKDEP)
do { (void)("semaphore->lock"); (void)(&__key); } while (0)
#endif
#if definedEx(CONFIG_LOCKDEP)
lockdep_init_map(&sem->lock.dep_map, "semaphore->lock", &__key, 0)
#endif
;
}
        """, p.phrase(p.translationUnit))


    /** this code produced False AST nodes */
    @Ignore("TODO, remove False nodes from AST")
    @Test def testNoDeadAstNodes {
        val c = """
         #ifdef X
         int a;
         #else
         double a;
         #endif

         #ifdef X
         int x;
         #endif
         #ifdef Y
         double x;
         #endif

         double
         #ifdef X
         b
         #else
         c
         #endif
         ;"""
        val ast = assertParseableAST(c, p.translationUnit)
        assertNoDeadNodes(ast.get, FeatureExprFactory.True, ast.get)
    }

    @Test def test_va_list {
        assertParseableAST( """
typedef __builtin_va_list __gnuc_va_list;
typedef __gnuc_va_list va_list;
extern int vsscanf(const char *, const char *, va_list)
	__attribute__ ((format (scanf, 2, 0)));
         ;""", p.translationUnit)

        assertParseableAST( """
#ifdef A
typedef __builtin_va_list __gnuc_va_list;
typedef __gnuc_va_list va_list;
extern int vsscanf(const char *, const char *, va_list)
	__attribute__ ((format (scanf, 2, 0)));
#endif
         ;""", p.translationUnit)
    }

    @Ignore("TODO properly support conditional type declarations to enable conditional error messages")
    @Test def test_conditional_typedecl {
        //expect error only if X is not selected
        assertParseableAST( """
#ifdef X
typedef char *foo;
#endif
void bar() {
    foo x;
}
                            """, p.translationUnit)
    }

    @Test def test_local_typedef {
        assertParseableAST( """
            void foo(){
                typedef int B;
                B a;
                int b;
            }
                            """, p.translationUnit)
        assertParseableAST( """
            void copyt(int n)
            {
                typedef int B[n];
                n += 1;
                B a;
                int b[n];
                for (i = 1; i < n; i++)
                    a[i-1] = b[i];
            }
                            """, p.translationUnit)
    }

    @Ignore("currently local typedefs are not scoped correctly")
    @Test
    def test_lexical_scope_of_typedef {
        assertParseError( """
            void foo(){
                typedef int B;
                B a;
                int b;
            }
            B x;
                          """, p.translationUnit)
    }


    @Test
    def test_conditionalTypeDef {
        assertParseableAST( """
                    #if defined(A) && defined(B)
                    void foo();
                    #endif
                    #if defined(A) && !defined(B)
                    typedef int a;
                    #endif
                    int c;


                    #if defined(A) && !defined(B)
                    a x;
                    #endif
                    int x;
                            """, p.translationUnit)
    }

    @Test
    def test_bug03 {
        assertParseableAST( """
      a(){int**b[]={&&c};c:;}
                            """, p.translationUnit)
    }

    @Test
    def test_uclibc {
        assertParseableAST( """
                              __extension__ static __inline unsigned int
                              __attribute__ ((__nothrow__)) gnu_dev_major (unsigned long long int __dev)
                              {
                                return ((__dev >> 8) & 0xfff) | ((unsigned int) (__dev >> 32) & ~0xfff);
                              }

                            """, p.translationUnit)
    }

    @Test
    @Ignore("this is a bug in our parser. `jin:...' should be one labled statement, but is parsed as two statements; therefore the else does not match")
    def test_labels {
        //based on a problem in uclibc
        assertParseableAST( """if (0)
                              |	    jin:{
                              |		if ((a = *++haystack) == c)
                              |		  goto crest;
                              |	      }
                              |	    else
                              |	      a = *++haystack;""".stripMargin, p.statement)
    }
    @Test
    def test_labels2 {
        //based on a problem in uclibc
        assertParseableAST( """if (0)
                              |	    {jin:{
                              |		if ((a = *++haystack) == c)
                              |		  goto crest;
                              |	      }}
                              |	    else
                              |	      a = *++haystack;""".stripMargin, p.statement)
    }

    @Test
    def test_nouveaudrivers {
        assertParseableAST(
            """
              |struct nouveau_oclass *
              |nv04_instmem_oclass = &(struct nouveau_instmem_impl) {}.base;
            """.stripMargin, p.translationUnit)
    }


    @Test
    def test_forunsigned {
        //based on a problem in uclibc; only working with a newer C standard C99 or GNUC99
        //        assertParseableAST("""for (unsigned int t = 0; t < 16; ++t);""".stripMargin , p.statement)
    }


    private def assertNoDeadNodes(ast: Product) {
        assertNoDeadNodes(ast, FeatureExprFactory.True, ast)
    }

    private def assertNoDeadNodes(ast: Any, f: FeatureExpr, orig: Product) {
        assert(f.isSatisfiable(), "False AST subtree: " + ast + " in " + orig)
        ast match {
            case Opt(g, e: Object) => assertNoDeadNodes(e, f and g, orig)
            case c: Choice[_] => assertNoDeadNodes(c.thenBranch, f and c.condition, orig); assertNoDeadNodes(c.elseBranch, f andNot c.condition, orig)
            case e: Product => for (c <- e.productIterator) assertNoDeadNodes(c, f, orig)
            case _ =>
        }
    }


}