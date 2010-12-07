package de.fosd.typechef.parser.c

import junit.framework._;
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._

class CParserTest extends TestCase {
    val p = new CParser()
    case class Alt(feature: FeatureExpr, thenBranch: AST, elseBranch: AST) extends Expr
    object Alt {
        def join = (f: FeatureExpr, x: AST, y: AST) => if (x == y) x else Alt(f, x, y)
    }
    def assertParseResult(expected: AST, code: String, mainProduction: (TokenReader[TokenWrapper, CTypeContext], FeatureExpr) => MultiParseResult[AST, TokenWrapper, CTypeContext]) {
        val actual = p.parse(code.stripMargin, mainProduction).forceJoin(FeatureExpr.base, Alt.join)
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
    def assertParseResult(expected: AST, code: String, productions: List[(TokenReader[TokenWrapper, CTypeContext], FeatureExpr) => MultiParseResult[AST, TokenWrapper, CTypeContext]]) {
        for (production <- productions)
            assertParseResult(expected, code, production)
    }

    def assertParseable(code: String, mainProduction: (TokenReader[TokenWrapper, CTypeContext], FeatureExpr) => MultiParseResult[Any, TokenWrapper, CTypeContext]) {
        val actual = p.parseAny(code.stripMargin, mainProduction)
        System.out.println(actual)
        (actual: @unchecked) match {
            case Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                //succeed
            }
            case NoSuccess(msg, context, unparsed, inner) =>
                fail(msg + " at " + unparsed + " with context " + context + " " + inner)
        }
    }
    def assertParseAnyResult(expected: Any, code: String, mainProduction: (TokenReader[TokenWrapper, CTypeContext], FeatureExpr) => MultiParseResult[Any, TokenWrapper, CTypeContext]) {
        val actual = p.parseAny(code.stripMargin, mainProduction)
        System.out.println(actual)
        (actual: @unchecked) match {
            case Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                assertEquals("incorrect parse result", expected, ast)
            }
            case NoSuccess(msg, context, unparsed, inner) =>
                fail(msg + " at " + unparsed + " with context " + context + " " + inner)
        }
    }
    def assertParseError(code: String, mainProduction: (TokenReader[TokenWrapper, CTypeContext], FeatureExpr) => MultiParseResult[Any, TokenWrapper, CTypeContext], expectErrorMsg: Boolean = false) {
        val actual = p.parseAny(code.stripMargin, mainProduction)
        System.out.println(actual)
        (actual: @unchecked) match {
            case Success(ast, unparsed) => {
                if (expectErrorMsg || unparsed.atEnd)
                    Assert.fail("parsing succeeded unexpectedly with " + ast + " - " + unparsed)
            }
            case NoSuccess(msg, context, unparsed, inner) => ;
        }
    }
    def assertParseError(code: String, productions: List[(TokenReader[TokenWrapper, CTypeContext], FeatureExpr) => MultiParseResult[Any, TokenWrapper, CTypeContext]]) {
        for (production <- productions)
            assertParseError(code, production)
    }

    def a = Id("a"); def b = Id("b"); def c = Id("c"); def d = Id("d"); def x = Id("x");
    def intType = TypeName(lo(PrimitiveTypeSpecifier("int")), None)
    def o[T](x: T) = Opt(FeatureExpr.base, x)
    def lo[T](x: T) = List(o(x))
    def lo[T](x: T, y:T) = List(o(x),o(y))
    def lo[T](x: T, y:T, z:T) = List(o(x),o(y),o(z))

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
        assertParseResult(StringLit(lo("\"test\"")), "\"test\"", List(p.primaryExpr, p.stringConst))
        assertParseResult(Alt(fa, StringLit(lo("\"test\"")), StringLit(lo("\"ba\\\"r\""))), """|#ifdef a
        					|"test"
        					|#else
        					|"ba\"r"
        					|#endif""", List(p.primaryExpr, p.stringConst))
        assertParseError("'c'", List(p.stringConst))
    }

    def testConstant() {
        def parseConstant(const: String) { assertParseResult(Constant(const), const, p.numConst) }
        def parseString(const: String) { assertParseResult(StringLit(lo(const)), const, p.stringConst) }
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
        assertParseAnyResult(lo(PointerPostfixSuffix("->", Id("a"))), "->a", p.postfixSuffix)
        assertParseAnyResult(lo(PointerPostfixSuffix("->", Id("a"))), "->    a", p.postfixSuffix)
        assertParseAnyResult(lo(PointerPostfixSuffix("->", Id("a")), PointerPostfixSuffix("->", Id("a"))), "->a->a", p.postfixSuffix)
        assertParseAnyResult(lo(PointerPostfixSuffix(".", Id("a"))), ".a", p.postfixSuffix)
        assertParseAnyResult(lo(SimplePostfixSuffix("++")), "++", p.postfixSuffix)
        assertParseAnyResult(lo(SimplePostfixSuffix("++"), SimplePostfixSuffix("--")), "++ --", p.postfixSuffix)
    }
    def testPostfixExpr {
        assertParseResult(PostfixExpr(Id("b"), lo(PointerPostfixSuffix("->", Id("a")))), "b->a", List(p.postfixExpr, p.unaryExpr))
        assertParseResult(Id("b"), "b", List(p.postfixExpr, p.unaryExpr))
        assertParseResult(PostfixExpr(Id("b"), lo(FunctionCall(ExprList(List())))), "b()", List(p.postfixExpr, p.unaryExpr))
        assertParseResult(PostfixExpr(Id("b"), lo(FunctionCall(ExprList(lo(a, b, c))))), "b(a,b,c)", List(p.postfixExpr, p.unaryExpr))
        assertParseResult(PostfixExpr(Id("b"), lo(FunctionCall(ExprList(List())), FunctionCall(ExprList(lo(a))))), "b()(a)", List(p.postfixExpr, p.unaryExpr))
        assertParseResult(PostfixExpr(Id("b"), lo(ArrayAccess(a))), "b[a]", List(p.postfixExpr, p.unaryExpr))
        assertParseResult(PostfixExpr(Id("b"), lo(FunctionCall(ExprList(List())), ArrayAccess(a))), "b()[a]", List(p.postfixExpr, p.unaryExpr))

        assertParseAnyResult(
        		PostfixExpr(Id("b"),
        		List(Opt(fa, PointerPostfixSuffix(".", Id("a"))), Opt(fa.not,PointerPostfixSuffix("->", Id("a"))))),
            """|b
        					|#ifdef a
        					|.
        					|#else
        					|->
        					|#endif
        					|a""", p.postfixExpr)
        assertParseResult(PostfixExpr(Id("b"), List(Opt(fa,SimplePostfixSuffix("++")))),
            """|b
        					|#ifdef a
        					|++
        					|#endif""", p.postfixExpr)
        assertParseable("__builtin_offsetof(void,a.b)", p.primaryExpr)
        assertParseable("c", p.castExpr)
        assertParseable("__real__", p.unaryOperator)
        assertParseable("__real__ c", p.unaryOperator ~ p.castExpr)
        assertParseable("__real__ c", p.unaryExpr)
    }
    def testUnaryExpr {
        assertParseResult(Id("b"), "b", p.unaryExpr)
        assertParseResult(UnaryExpr("++", Id("b")), "++b", p.unaryExpr)
        assertParseResult(SizeOfExprT(intType), "sizeof(int)", p.unaryExpr)
        assertParseResult(SizeOfExprU(Id("b")), "sizeof b", p.unaryExpr)
        assertParseResult(SizeOfExprU(UnaryExpr("++", Id("b"))), "sizeof ++b", p.unaryExpr)
        assertParseResult(UCastExpr("+", CastExpr(intType, Id("b"))), "+(int)b", List(p.unaryExpr))
        assertParseResult(UCastExpr("&", CastExpr(intType, Id("b"))), "&(int)b", List(p.unaryExpr))
        assertParseResult(UCastExpr("!", CastExpr(intType, Id("b"))), "!(int)b", List(p.unaryExpr))
        assertParseError("(c)b", List(p.unaryExpr))
    }

    def testCastExpr {
        assertParseResult(CastExpr(intType, SizeOfExprT(intType)), "(int)sizeof(int)", List(p.castExpr /*, p.unaryExpr*/ ))
        assertParseResult(CastExpr(intType, Id("b")), "(int)b", List(p.castExpr /*, p.unaryExpr*/ ))
        assertParseResult(CastExpr(intType, CastExpr(intType, CastExpr(intType, SizeOfExprT(intType)))), "(int)(int)(int)sizeof(int)", List(p.castExpr /*, p.unaryExpr*/ ))
        assertParseable("(int)sizeof(void)", p.castExpr)
    }

    def testNAryExpr {
        assertParseResult(NAryExpr(a, List(o("*", b))), "a*b", p.multExpr)
        assertParseResult(NAryExpr(a, List(o("*", b), o("*", b))), "a*b*b", p.multExpr)
    }
    def testExprs {
        assertParseResult(NAryExpr(NAryExpr(a, List(o("*", b))), List(o("+", c))), "a*b+c", p.expr)
        assertParseResult(NAryExpr(c, List(o("+", NAryExpr(a, List(o("*", b)))))), "c+a*b", p.expr)
        assertParseResult(NAryExpr(NAryExpr(a, List(o("+", b))), List(o("*", c))), "(a+b)*c", p.expr)
        assertParseResult(AssignExpr(a, "=", NAryExpr(b, List(o("==", c)))), "a=b==c", p.expr)
        assertParseResult(NAryExpr(a, List(o("/", b))), "a/b", p.expr)
        assertParseResult(ConditionalExpr(a, Some(b), c), "a?b:c", p.expr)
        assertParseResult(ExprList(List(o(a), o(b), o(NAryExpr(NAryExpr(c, List(o("+", NAryExpr(c, List(o("/", d)))))), List(o("|", x)))))), "a,b,c+c/d|x", p.expr)
    }
    def testAltExpr {
        assertParseResult(Alt(fa, a, b),
            """|#ifdef a
        					|a
        					|#else
        					|b
        					|#endif""", p.expr)
        assertParseResult(Alt(fa, NAryExpr(a, List(Opt(fa,("+", c)))), NAryExpr(b, List(Opt(fa.not,(("+", c)))))),
            """|#ifdef a
        					|a +
        					|#else
        					|b +
        					|#endif
        					|c""", p.expr)
        assertParseResult(Alt(fa, AssignExpr(a, "=", ConditionalExpr(b, Some(b), d)), AssignExpr(a, "=", ConditionalExpr(b, Some(c), d))),
            """|a=b?
        					|#ifdef a
        					|b 
        					|#else
        					|c 
        					|#endif
        					|:d""", p.expr)
    }

    def testStatements {
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
        assertParseAnyResult(CompoundStatement(List(
        		Opt(fa,IfStatement(a, ExprStatement(b), None)), 
        		Opt(fa, ExprStatement(c)), 
        		Opt(fa.not, IfStatement(a, ExprStatement(c), None)))),
            """|{
        		|if (a)
    			  			|#ifdef a
        					|b;
        					|#endif
    			  			|c;}""", p.statement)

        assertParseAnyResult(CompoundStatement(List(o(ExprStatement(a)), Opt(fa, ExprStatement(b)), o(ExprStatement(c)))),
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
        assertParseResult(DeclaratorId(List(), a, List()), "a", p.declarator)
        assertParseResult(DeclaratorDecl(List(), None, DeclaratorId(List(), a, lo(DeclArrayAccess(None))), List()), "(a[])", p.declarator)
        assertParseResult(DeclaratorId(lo(Pointer(List())), a, List()), "*a", p.declarator)
        assertParseResult(DeclaratorId(lo(Pointer(List()), Pointer(List())), a, List()), "**a", p.declarator)
        assertParseResult(DeclaratorId(lo(Pointer(lo(OtherSpecifier("const")))), a, List()), "*const a", p.declarator)
        assertParseResult(DeclaratorId(lo(Pointer(lo(OtherSpecifier("const"), OtherSpecifier("volatile")))), a, List()), "*const volatile a", p.declarator)
        assertParseResult(DeclaratorId(List(), a, lo(DeclArrayAccess(None))), "a[]", p.declarator)
        //    	assertParseResult(DeclaratorId(List(),a,List(DeclIdentifierList(List(a,b)))), "a(a,b)", p.declarator(false))
        //    	assertParseResult(DeclaratorId(List(),a,List(DeclParameterTypeList(List()))), "a()", p.declarator(false))
    }
    def testEnumerator {
        assertParseable("enum e", p.enumSpecifier)
        assertParseable("enum e { a }", p.enumSpecifier)
        assertParseable("enum { a }", p.enumSpecifier)
        assertParseable("enum e { a=1, b=3 }", p.enumSpecifier)
        assertParseError("enum {  }", p.enumSpecifier)
    }

    def testStructOrUnion {
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
    }

    def testAsmExpr {
        assertParseable("asm { 3+3};", p.asm_expr)
        assertParseable("asm volatile { 3+3};", p.asm_expr)
    }

    def testFunctionDef {
        assertParseable("void foo(){}", p.functionDef)
        assertParseable("void foo(int a) { a; }", p.functionDef)
        assertParseable("""|void 
        				|#ifdef X
        				|foo
        				|#else
        				|bar
        				|#endif
        				|(){}
        				|void x(){}""", p.translationUnit)
        assertParseable("main(){}", p.functionDef)
        assertParseable("main(){int T=100, a=(T)+1;}", p.functionDef)
    }

    def testTypedefName {
        assertParseable("int a;", p.translationUnit)
        assertParseError("foo a;", p.translationUnit)
        assertParseable("typedef int foo; foo a;", p.translationUnit)
        //scoping of typedef not considered yet:
        //assertParseable("typedef int T;main(){int T=100, a=(T)+1;}", p.functionDef)
    }

    def testAttribute {
        assertParseable("", p.attributeList)
        assertParseable("__attribute__((a b))", p.attributeDecl)
        assertParseable("__attribute__(())", p.attributeDecl)
        assertParseable("__attribute__((a,b))", p.attributeDecl)
        assertParseable("__attribute__((a,(b,b)))", p.attributeDecl)
    }

    def testMethodLookAhead {
        //should return parse error instead of empty parse result with unparsed tokens
        assertParseError("void main () { int a; ", p.translationUnit, true)
        assertParseError("int main () { abs = ", p.translationUnit, true)
    }

    def testMisc0 {
        assertParseable("{__label__ hey, now;}", p.compoundStatement)
        assertParseable("{abs = ({__label__ hey, now;});}", p.compoundStatement)
        assertParseable("extern int my_printf (void *my_object, const char *my_format);", p.externalDef)
        assertParseable("extern int my_printf (void *my_object, const char *my_format) __attribute__ ((format (printf, 2, 3)));", p.externalDef)
        assertParseable("extern int my_printf (void *my_object, const char *my_format, ...) __attribute__ ((format (printf, 2, 3)));", p.externalDef)
        assertParseable("asm volatile (\".set noreorder\");", p.statement)
        assertParseable("""asm volatile (".set noreorder\n"
              ".set noat\n"
              ".set mips3");""", p.statement)
        assertParseable("""
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
    def testMisc1a = assertParseable("""typedef struct  pojeqsd {
    			char                        hgfretty[99 ];
    		} pojeqsd_t;""", p.translationUnit)
    def testMisc1b = assertParseable("""typedef int hgfretty;
				typedef struct  pojeqsd {
				char                        hgfretty[99 ];
    		} pojeqsd_t;""", p.translationUnit)

    def testMisc2 = assertParseable("( checkme )->j76g", p.expr)
    def testMisc2b = assertParseable("if ((( checkme )->j76g) ) { }", p.statement)
    def testMisc3a = assertParseable("(int)q23w3", p.expr)
    def testMisc3b = assertParseable("void *", p.typeName)
    def testMisc3c = assertParseable("++(int)q23w3", p.unaryExpr)
    def testMisc3d = assertParseable("(++(int)q23w3->ll881ss[3])", p.primaryExpr)
    def testMisc3e = assertParseable("(void *) (++(int)q23w3->ll881ss[3])", p.expr)
    def testMisc4 = assertParseable("""if (x3 && x4) {
        char gh554j[19];  
        gh554j[0]='\n';
    }""", p.statement)

    def testBoa1 = assertParseable("__attribute__((__cdecl__))", p.attributeDecl)
    def testBoa2 = assertParseable("int (__attribute__((__cdecl__)) * _read) (struct _reent *, void *, char *, int);", p.structDeclaration)
    def testBoa3 = assertParseable("""typedef int FILE;
typedef __builtin_va_list __gnuc_va_list;
int	__attribute__((__cdecl__)) vfprintf (FILE *, const char *, __gnuc_va_list)
 __attribute__ ((__format__(__printf__, 2, 0)));""", p.translationUnit)
    def testBoa4 = assertParseable("""struct alias {
    char *fakename;             /* URI path to file */
    char *realname;             /* Actual path to file */
    int type;                   /* ALIAS, SCRIPTALIAS, REDIRECT */
    int fake_len;               /* strlen of fakename */
    int real_len;               /* strlen of realname */
    struct alias *next;
};

typedef struct alias alias;""", p.translationUnit)
    def testBoa5 = assertParseable("""char *fakename;             /* URI path to file */
    char *realname;             /* Actual path to file */
    int type;                   /* ALIAS, SCRIPTALIAS, REDIRECT */
    int fake_len;               /* strlen of fakename */
    int real_len;               /* strlen of realname */
    struct alias *next;""", p.structDeclarationList0)

    def testOptListBoa1 = assertParseable("""
typedef	char *	caddr_t;
#if defined(GO32)
typedef unsigned long vm_offset_t;
#endif
typedef unsigned long vm_size_t;
""", p.translationUnit)

    def testEnsureError = assertParseError("""main()
{
  for(;;
	{
      }
}""", p.translationUnit)

    def testLinuxHeader = assertParseable("""
#define __restrict
/* Convert a string to a long long integer.  */
__extension__ extern long long int atoll (__const char *__nptr)
     __attribute__ ((__nothrow__)) __attribute__ ((__pure__)) __attribute__ ((__nonnull__ (1))) ;



/* Convert a string to a floating-point number.  */
extern double strtod (__const char *__restrict __nptr, char **__restrict __endptr)
     __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1))) ;""", p.translationUnit)

    def testDoubleMain = assertParseable("""
int foo() {}
#if defined(X)
int main(void) {}
#endif
""", p.translationUnit)

    def testDoubleMain2 = assertParseable("""
int foo() {}
#if defined(X)
int main(void) {}
#else
int main(void) {}
#endif
""", p.translationUnit)

    def testIfdefInStatement = assertParseable(""" 
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

    def testBoaIp1 =
        assertParseable("""
{
#if !(defined(INET6))
    memmove(dest, inet_ntoa(s->sin_addr), len);
#endif
    return dest;
}""", p.compoundStatement)

    def testBoaIp2 =
        assertParseable("""
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

	def busyBox1 = assertParseable("""int grep_main(int argc __attribute__ ((__unused__)), char **argv){}""", p.translationUnit)
	
	
	def busyBox2 = assertParseable("""
int grep_main(int argc __attribute__ ((__unused__)), char **argv)
{
	FILE *file;
	int matched;
	llist_t *fopt = ((void *)0);

	/* do normal option parsing */
#if definedEx(CONFIG_FEATURE_GREP_CONTEXT)
	int Copt, opts;

	/* -H unsets -h; -C unsets -A,-B; -e,-f are lists;
	 * -m,-A,-B,-C have numeric param */
	opt_complementary = "H-h:C-AB:e::f::m+:A+:B+:C+";
	opts = getopt32(argv,
		"lnqvscFiHhe:f:Lorm:w" "A:B:C:" 
#if definedEx(CONFIG_FEATURE_GREP_EGREP_ALIAS)
"E"
#endif
#if !(definedEx(CONFIG_FEATURE_GREP_EGREP_ALIAS))

#endif
 
#if definedEx(CONFIG_EXTRA_COMPAT)
"z"
#endif
#if !(definedEx(CONFIG_EXTRA_COMPAT))

#endif
 "aI",
		&((*(struct globals*)&bb_common_bufsiz1).pattern_head ), &fopt, &((*(struct globals*)&bb_common_bufsiz1).max_matches ),
		&((*(struct globals*)&bb_common_bufsiz1).lines_after ), &((*(struct globals*)&bb_common_bufsiz1).lines_before ), &Copt);

	if (opts & OPT_C) {
		/* -C unsets prev -A and -B, but following -A or -B
		   may override it */
		if (!(opts & OPT_A)) /* not overridden */
			((*(struct globals*)&bb_common_bufsiz1).lines_after ) = Copt;
		if (!(opts & OPT_B)) /* not overridden */
			((*(struct globals*)&bb_common_bufsiz1).lines_before ) = Copt;
	}
	/* sanity checks */
	if (opts & (OPT_c|OPT_q|OPT_l|OPT_L)) {
		option_mask32 &= ~OPT_n;
		((*(struct globals*)&bb_common_bufsiz1).lines_before ) = 0;
		((*(struct globals*)&bb_common_bufsiz1).lines_after ) = 0;
	} else if (((*(struct globals*)&bb_common_bufsiz1).lines_before ) > 0) {
		if (((*(struct globals*)&bb_common_bufsiz1).lines_before ) > 2147483647 / sizeof(long long))
			((*(struct globals*)&bb_common_bufsiz1).lines_before ) = 2147483647 / sizeof(long long);
		/* overflow in (lines_before * sizeof(x)) is prevented (above) */
		((*(struct globals*)&bb_common_bufsiz1).before_buf ) = xzalloc(((*(struct globals*)&bb_common_bufsiz1).lines_before ) * sizeof(((*(struct globals*)&bb_common_bufsiz1).before_buf )[0]));
		
#if definedEx(CONFIG_EXTRA_COMPAT)
((*(struct globals*)&bb_common_bufsiz1).before_buf_size ) = xzalloc(((*(struct globals*)&bb_common_bufsiz1).lines_before ) * sizeof(((*(struct globals*)&bb_common_bufsiz1).before_buf_size )[0]));
#endif
#if !(definedEx(CONFIG_EXTRA_COMPAT))

#endif

	}
#endif
#if !(definedEx(CONFIG_FEATURE_GREP_CONTEXT))
	/* with auto sanity checks */
	/* -H unsets -h; -c,-q or -l unset -n; -e,-f are lists; -m N */
	opt_complementary = "H-h:c-n:q-n:l-n:e::f::m+";
	getopt32(argv, "lnqvscFiHhe:f:Lorm:w"  
#if definedEx(CONFIG_FEATURE_GREP_EGREP_ALIAS)
"E"
#endif
#if !(definedEx(CONFIG_FEATURE_GREP_EGREP_ALIAS))

#endif
 
#if definedEx(CONFIG_EXTRA_COMPAT)
"z"
#endif
#if !(definedEx(CONFIG_EXTRA_COMPAT))

#endif
 "aI",
		&((*(struct globals*)&bb_common_bufsiz1).pattern_head ), &fopt, &((*(struct globals*)&bb_common_bufsiz1).max_matches ));
#endif
	((*(struct globals*)&bb_common_bufsiz1).invert_search ) = ((option_mask32 & OPT_v) != 0); /* 0 | 1 */

	if (((*(struct globals*)&bb_common_bufsiz1).pattern_head ) != ((void *)0)) {
		/* convert char **argv to grep_list_data_t */
		llist_t *cur;

		for (cur = ((*(struct globals*)&bb_common_bufsiz1).pattern_head ); cur; cur = cur->link)
			cur->data = 
#if definedEx(CONFIG_FEATURE_CLEAN_UP)
add_grep_list_data(cur->data, 0)
#endif
#if !(definedEx(CONFIG_FEATURE_CLEAN_UP))
add_grep_list_data(cur->data)
#endif
;
	}
	if (option_mask32 & OPT_f)
		load_regexes_from_file(fopt);

	if (
#if definedEx(CONFIG_FEATURE_GREP_FGREP_ALIAS)
1
#endif
#if !(definedEx(CONFIG_FEATURE_GREP_FGREP_ALIAS))
0
#endif
 && applet_name[0] == 'f')
		option_mask32 |= OPT_F;

#if !(definedEx(CONFIG_EXTRA_COMPAT))
	if (!(option_mask32 & (OPT_o | OPT_w)))
		((*(struct globals*)&bb_common_bufsiz1).reflags ) = (((1 << 1) << 1) << 1);
#endif
	if (
#if definedEx(CONFIG_FEATURE_GREP_EGREP_ALIAS)
1
#endif
#if !(definedEx(CONFIG_FEATURE_GREP_EGREP_ALIAS))
0
#endif

	 && (applet_name[0] == 'e' || (option_mask32 & OPT_E))
	) {
		
#if !(definedEx(CONFIG_EXTRA_COMPAT))
((*(struct globals*)&bb_common_bufsiz1).reflags )
#endif
#if definedEx(CONFIG_EXTRA_COMPAT)
re_syntax_options
#endif
 |= 
#if !(definedEx(CONFIG_EXTRA_COMPAT))
1
#endif
#if definedEx(CONFIG_EXTRA_COMPAT)
(RE_SYNTAX_EGREP | RE_INTERVALS | RE_NO_BK_BRACES)
#endif
;
	}
#if definedEx(CONFIG_EXTRA_COMPAT)
	else {
		re_syntax_options = RE_SYNTAX_GREP;
	}
#endif
	if (option_mask32 & OPT_i) {
#if !(definedEx(CONFIG_EXTRA_COMPAT))
		((*(struct globals*)&bb_common_bufsiz1).reflags ) |= (1 << 1);
#endif
#if definedEx(CONFIG_EXTRA_COMPAT)
		int i;
		((*(struct globals*)&bb_common_bufsiz1).case_fold ) = xmalloc(256);
		for (i = 0; i < 256; i++)
			((*(struct globals*)&bb_common_bufsiz1).case_fold )[i] = (unsigned char)i;
		for (i = 'a'; i <= 'z'; i++)
			((*(struct globals*)&bb_common_bufsiz1).case_fold )[i] = (unsigned char)(i - ('a' - 'A'));
#endif
	}

	argv += optind;

	/* if we didn't get a pattern from -e and no command file was specified,
	 * first parameter should be the pattern. no pattern, no worky */
	if (((*(struct globals*)&bb_common_bufsiz1).pattern_head ) == ((void *)0)) {
		char *pattern;
		if (*argv == ((void *)0))
			bb_show_usage();
		pattern = 
#if definedEx(CONFIG_FEATURE_CLEAN_UP)
add_grep_list_data(*argv++, 0)
#endif
#if !(definedEx(CONFIG_FEATURE_CLEAN_UP))
add_grep_list_data(*argv++)
#endif
;
		llist_add_to(&((*(struct globals*)&bb_common_bufsiz1).pattern_head ), pattern);
	}

	/* argv[0..(argc-1)] should be names of file to grep through. If
	 * there is more than one file to grep, we will print the filenames. */
	if (argv[0] && argv[1])
		((*(struct globals*)&bb_common_bufsiz1).print_filename ) = 1;
	/* -H / -h of course override */
	if (option_mask32 & OPT_H)
		((*(struct globals*)&bb_common_bufsiz1).print_filename ) = 1;
	if (option_mask32 & OPT_h)
		((*(struct globals*)&bb_common_bufsiz1).print_filename ) = 0;

	/* If no files were specified, or '-' was specified, take input from
	 * stdin. Otherwise, we grep through all the files specified. */
	matched = 0;
	do {
		((*(struct globals*)&bb_common_bufsiz1).cur_file ) = *argv;
		file = stdin;
		if (!((*(struct globals*)&bb_common_bufsiz1).cur_file ) || ((((*(struct globals*)&bb_common_bufsiz1).cur_file ))[0] == '-' && !(((*(struct globals*)&bb_common_bufsiz1).cur_file ))[1])) {
			((*(struct globals*)&bb_common_bufsiz1).cur_file ) = "(standard input)";
		} else {
			if (option_mask32 & OPT_r) {
				struct stat st;
				if (stat(((*(struct globals*)&bb_common_bufsiz1).cur_file ), &st) == 0 && ((((st.st_mode)) & 0170000) == (0040000))) {
					if (!(option_mask32 & OPT_h))
						((*(struct globals*)&bb_common_bufsiz1).print_filename ) = 1;
					matched += grep_dir(((*(struct globals*)&bb_common_bufsiz1).cur_file ));
					goto grep_done;
				}
			}
			/* else: fopen(dir) will succeed, but reading won't */
			file = fopen_for_read(((*(struct globals*)&bb_common_bufsiz1).cur_file ));
			if (file == ((void *)0)) {
				if (!(option_mask32 & OPT_s))
					bb_simple_perror_msg(((*(struct globals*)&bb_common_bufsiz1).cur_file ));
				((*(struct globals*)&bb_common_bufsiz1).open_errors ) = 1;
				continue;
			}
		}
		matched += grep_file(file);
		fclose_if_not_stdin(file);
 grep_done: ;
	} while (*argv && *++argv);

	/* destroy all the elments in the pattern list */
	if (
#if definedEx(CONFIG_FEATURE_CLEAN_UP)
1
#endif
#if !(definedEx(CONFIG_FEATURE_CLEAN_UP))
0
#endif
) {
		while (((*(struct globals*)&bb_common_bufsiz1).pattern_head )) {
			llist_t *pattern_head_ptr = ((*(struct globals*)&bb_common_bufsiz1).pattern_head );
			grep_list_data_t *gl = (grep_list_data_t *)pattern_head_ptr->data;

			((*(struct globals*)&bb_common_bufsiz1).pattern_head ) = ((*(struct globals*)&bb_common_bufsiz1).pattern_head )->link;
			if (gl->flg_mem_alocated_compiled & 1)
				free(gl->pattern);
			if (gl->flg_mem_alocated_compiled & 2)
				regfree(&gl->compiled_regex);
			free(gl);
			free(pattern_head_ptr);
		}
	}
	/* 0 = success, 1 = failed, 2 = error */
	if (((*(struct globals*)&bb_common_bufsiz1).open_errors ))
		return 2;
	return !matched; /* invert return value: 0 = success, 1 = failed */
}
""", p.translationUnit)
	

}