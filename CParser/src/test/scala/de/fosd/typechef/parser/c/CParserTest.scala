package de.fosd.typechef.parser.c

import junit.framework._;
import junit.framework.Assert._
import org.junit.Test
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._

class CParserTest extends TestCase {
    val p = new CParser()

    case class Alt(feature: FeatureExpr, thenBranch: AST, elseBranch: AST) extends Expr {
        override def equals(x: Any) = x match {
            case Alt(f, t, e) => f.equivalentTo(feature) && (thenBranch == t) && (elseBranch == e)
            case _ => false
        }
    }

    object Alt {
        def join = (f: FeatureExpr, x: AST, y: AST) => if (x == y) x else Alt(f, x, y)
    }

    def assertParseResult(expected: AST, code: String, mainProduction: (TokenReader[TokenWrapper, CTypeContext], FeatureExpr) => p.MultiParseResult[AST]) {
        val actual = p.parse(code.stripMargin, mainProduction).forceJoin(FeatureExpr.base, Alt.join)
        System.out.println(actual)
        actual match {
            case p.Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                assertEquals("incorrect parse result", expected, ast)
            }
            case p.NoSuccess(msg, unparsed, inner) =>
                fail(msg + " at " + unparsed + " " + inner)
        }
    }
    def assertParseResult(expected: AST, code: String, productions: List[(TokenReader[TokenWrapper, CTypeContext], FeatureExpr) => p.MultiParseResult[AST]]) {
        for (production <- productions)
            assertParseResult(expected, code, production)
    }

    def assertParseable(code: String, mainProduction: (TokenReader[TokenWrapper, CTypeContext], FeatureExpr) => p.MultiParseResult[Any]) {
        val actual = p.parseAny(code.stripMargin, mainProduction)
        System.out.println(actual)
        (actual: @unchecked) match {
            case p.Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                //succeed
            }
            case p.NoSuccess(msg, unparsed, inner) =>
                fail(msg + " at " + unparsed + " " + inner)
        }
    }
    def assertParseAnyResult(expected: Any, code: String, mainProduction: (TokenReader[TokenWrapper, CTypeContext], FeatureExpr) => p.MultiParseResult[Any]) {
        val actual = p.parseAny(code.stripMargin, mainProduction)
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
    def assertParseError(code: String, mainProduction: (TokenReader[TokenWrapper, CTypeContext], FeatureExpr) => p.MultiParseResult[Any], expectErrorMsg: Boolean = false) {
        val actual = p.parseAny(code.stripMargin, mainProduction)
        System.out.println(actual)
        (actual: @unchecked) match {
            case p.Success(ast, unparsed) => {
                if (expectErrorMsg || unparsed.atEnd)
                    Assert.fail("parsing succeeded unexpectedly with " + ast + " - " + unparsed)
            }
            case p.NoSuccess(msg, unparsed, inner) =>;
        }
    }
    def assertParseError(code: String, productions: List[(TokenReader[TokenWrapper, CTypeContext], FeatureExpr) => p.MultiParseResult[Any]]) {
        for (production <- productions)
            assertParseError(code, production)
    }

    def a = Id("a");
    def b = Id("b");
    def c = Id("c");
    def d = Id("d");
    def x = Id("x");
    def intType = TypeName(lo(PrimitiveTypeSpecifier("int")), None)
    def o[T](x: T) = Opt(FeatureExpr.base, x)
    def lo[T](x: T) = List(o(x))
    def lo[T](x: T, y: T) = List(o(x), o(y))
    def lo[T](x: T, y: T, z: T) = List(o(x), o(y), o(z))

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
        assertParseResult(PostfixExpr(Id("b"), List(Opt(fa, SimplePostfixSuffix("++")))),
            """|b
        					|#ifdef a
        					|++
        					|#endif""", p.postfixExpr)

        assertParseResult(PostfixExpr(Id("b"), lo(PointerPostfixSuffix("->", Id("a")))), "b->a", List(p.postfixExpr, p.unaryExpr))
        assertParseResult(Id("b"), "b", List(p.postfixExpr, p.unaryExpr))
        assertParseResult(PostfixExpr(Id("b"), lo(FunctionCall(ExprList(List())))), "b()", List(p.postfixExpr, p.unaryExpr))
        assertParseResult(PostfixExpr(Id("b"), lo(FunctionCall(ExprList(lo(a, b, c))))), "b(a,b,c)", List(p.postfixExpr, p.unaryExpr))
        assertParseResult(PostfixExpr(Id("b"), lo(FunctionCall(ExprList(List())), FunctionCall(ExprList(lo(a))))), "b()(a)", List(p.postfixExpr, p.unaryExpr))
        assertParseResult(PostfixExpr(Id("b"), lo(ArrayAccess(a))), "b[a]", List(p.postfixExpr, p.unaryExpr))
        assertParseResult(PostfixExpr(Id("b"), lo(FunctionCall(ExprList(List())), ArrayAccess(a))), "b()[a]", List(p.postfixExpr, p.unaryExpr))

        assertParseAnyResult(
            PostfixExpr(Id("b"),
                List(Opt(fa, PointerPostfixSuffix(".", Id("a"))), Opt(fa.not, PointerPostfixSuffix("->", Id("a"))))),
            """|b
        					|#ifdef a
        					|.
        					|#else
        					|->
        					|#endif
        					|a""", p.postfixExpr)
        assertParseable("++", p.postfixSuffix)
        assertParseable("b++", p.postfixExpr)
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
        assertParseResult(CastExpr(intType, SizeOfExprT(intType)), "(int)sizeof(int)", List(p.castExpr /*, p.unaryExpr*/))
        assertParseResult(CastExpr(intType, Id("b")), "(int)b", List(p.castExpr /*, p.unaryExpr*/))
        assertParseResult(CastExpr(intType, CastExpr(intType, CastExpr(intType, SizeOfExprT(intType)))), "(int)(int)(int)sizeof(int)", List(p.castExpr /*, p.unaryExpr*/))
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
        assertParseResult(Alt(fa, NAryExpr(a, List(Opt(fa, ("+", c)))), NAryExpr(b, List(Opt(fa.not, (("+", c)))))),
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
            Opt(fa, IfStatement(a, ExprStatement(b), None)),
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

        assertParseable("int a", p.parameterTypeList)
        assertParseError("int a)", p.parameterTypeList)
        //        assertParseable("(int a)", p.declaratorParamaterList)
        assertParseable("void foo(){}", p.functionDef)
        assertParseable("void foo(){a;}", p.functionDef)
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

    @Test
    def testBusyBox1 = assertParseable("""int grep_main(int argc __attribute__ ((__unused__)), char **argv){}""", p.functionDef)

    @Test
    def testBusyBox2 = assertParseable("""
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
    @Test def testLinux1a = assertParseable("extern char * __attribute__((warn_unused_result)) skip_spaces(const char *);", p.phrase(p.externalDef))
    @Test def testLinux1b = assertParseable("extern char * skip_spaces(const char *);", p.phrase(p.externalDef))

}