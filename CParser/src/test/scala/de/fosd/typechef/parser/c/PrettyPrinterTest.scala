package de.fosd.typechef.parser.c

import org.junit.Test
import org.junit.Assert._
import PrettyPrinter._
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.{TokenReader, Opt}
import junit.framework.TestCase

class PrettyPrinterTest extends TestCase {
    val p = new CParser()

    @Test
    def testPP {

        //        val sp=new StringPrinter()
        //
        //        val ast=new TranslationUnit(List())
        //
        //        println(PrettyPrinter.print(sp,ast))

        val f = FeatureExpr.base
        val doc = prettyPrint(EnumSpecifier(Some(Id("e")), Some(List(
            Opt(f, Enumerator(Id("test"), None)),
            Opt(f, Enumerator(Id("test2"), None))
        ))))

        println(layout(doc))
    }

    @Test def testEnum = parsePrintParse("enum e {  test,  test2}", p.enumSpecifier)
    @Test def testString = parsePrintParse(""" "test" "b" """, p.stringConst)
    @Test
    def testConstant() {
        def parseConstant(const: String) {
            parsePrintParse(const, p.numConst)
        }
        def parseString(const: String) {
            parsePrintParse(const, p.stringConst)
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
        //        assertParseResult(Alt(fa, Constant("1"), Constant("2")), """|#ifdef a
        //        					|1
        //        					|#else
        //        					|2
        //        					|#endif""", List(p.primaryExpr, p.numConst))
    }

    def testEnumerator {
        parsePrintParse("enum e", p.enumSpecifier)
        parsePrintParse("enum e { a }", p.enumSpecifier)
        parsePrintParse("enum { a }", p.enumSpecifier)
        parsePrintParse("enum e { a=1, b=3 }", p.enumSpecifier)
    }

    def testParameterDecl {
        parsePrintParse("void", p.parameterDeclaration)
        parsePrintParse("extern void", p.parameterDeclaration)
        parsePrintParse("extern void", p.parameterDeclaration)
        parsePrintParse("void *", p.parameterDeclaration)
        parsePrintParse("void *[]", p.parameterDeclaration)
        parsePrintParse("void *[a]", p.parameterDeclaration)
        parsePrintParse("void *(*[])", p.parameterDeclaration)
        parsePrintParse("void *()", p.parameterDeclaration)
        parsePrintParse("void *(void, int)", p.parameterDeclaration)
        parsePrintParse("void ****(void, int)", p.parameterDeclaration)
        parsePrintParse("void ****a", p.parameterDeclaration)
    }

    def testStatements {
        parsePrintParse("a;", p.statement)
        parsePrintParse("{}", p.compoundStatement)
        parsePrintParse("{}", p.statement)
        parsePrintParse("a(x->i);", p.statement)
        parsePrintParse("while (x) a;", p.statement)
        parsePrintParse(";", p.statement)
        parsePrintParse("if (a) b; ", p.statement)
        parsePrintParse("if (a) {b;c;} ", p.statement)
        parsePrintParse("if (a) b; else c;", p.statement)
        parsePrintParse("if (a) if (b) if (c) d; ", p.statement)
        parsePrintParse("{a;b;}", p.statement)
        parsePrintParse("case a: x;", p.statement)
        parsePrintParse("break;", p.statement)
        parsePrintParse("a:", p.statement)
        parsePrintParse("goto x;", p.statement)
        //        assertParseResult(AltStatement(fa, IfStatement(a, ExprStatement(b), List(), None), ExprStatement(b)),
        //            """|#ifdef a
        //        					|if (a)
        //        					|#endif
        //    			  			|b;""", p.statement)
        //        assertParseResult(IfStatement(a, AltStatement(fa, ExprStatement(b), ExprStatement(c)), List(), None),
        //            """|if (a)
        //    			  			|#ifdef a
        //        					|b;
        //        					|#else
        //    			  			|c;
        //        					|#endif""", p.statement)
        //        assertParseAnyResult(CompoundStatement(List(
        //            Opt(fa, IfStatement(a, ExprStatement(b), List(), None)),
        //            Opt(fa, ExprStatement(c)),
        //            Opt(fa.not, IfStatement(a, ExprStatement(c), List(), None)))),
        //            """|{
        //        		|if (a)
        //    			  			|#ifdef a
        //        					|b;
        //        					|#endif
        //    			  			|c;}""", p.statement)
        //
        //        assertParseAnyResult(CompoundStatement(List(o(ExprStatement(a)), Opt(fa, ExprStatement(b)), o(ExprStatement(c)))),
        //            """|{
        //        		|a;
        //    			  			|#ifdef a
        //        					|b;
        //        					|#endif
        //    			  			|c;}""", p.statement)
    }


    def testStructOrUnion {
        parsePrintParse("struct a", p.structOrUnionSpecifier)
        parsePrintParse("union a", p.structOrUnionSpecifier)
        parsePrintParse("x ", p.structDeclarator)
        parsePrintParse("struct { void x; }", p.structOrUnionSpecifier)
        parsePrintParse("struct { void x,y; }", p.structOrUnionSpecifier)
        parsePrintParse("struct a{ void x; int x:3+2,z:3;}", p.structOrUnionSpecifier)
        parsePrintParse("struct {  }", p.structOrUnionSpecifier)
    }

    def testAsmExpr {
        parsePrintParse("asm { 3+3};", p.asm_expr)
        parsePrintParse("asm volatile { 3+3};", p.asm_expr)
    }

    def testFunctionDef {

        parsePrintParse("void foo(){}", p.functionDef)
        parsePrintParse("void foo(){a;}", p.functionDef)
        parsePrintParse("void foo(int a) { a; }", p.functionDef)
        //           parsePrintParse("""|void
        //                           |#ifdef X
        //                           |foo
        //                           |#else
        //                           |bar
        //                           |#endif
        //                           |(){}
        //                           |void x(){}""", p.translationUnit)
        parsePrintParse("main(){}", p.functionDef)
        parsePrintParse("main(){int T=100, a=(T)+1;}", p.functionDef)
    }

    def testTypedefName {
        parsePrintParse("int a;", p.translationUnit)
        parsePrintParse("typedef int foo; foo a;", p.translationUnit)
        parsePrintParse("__builtin_type a;", p.translationUnit)
        parsePrintParse("(notATypeName)", p.expr)
        parsePrintParse("(__builtin_type)", p.expr)
        parsePrintParse("3+(__builtin_type)", p.expr)
    }

    def testAttribute {
        parsePrintParse("__attribute__((a b))", p.attributeDecl)
        parsePrintParse("__attribute__(())", p.attributeDecl)
        parsePrintParse("__attribute__((a,b))", p.attributeDecl)
        parsePrintParse("__attribute__((a,(b,b)))", p.attributeDecl)
    }

    def testInitializer {
        parsePrintParse("a", p.initializer)
        parsePrintParse(".a = 3", p.initializer)
        parsePrintParse("a: 3", p.initializer)
        parsePrintParse("[3] = 3", p.initializer)
        parsePrintParse("{}", p.initializer)
        parsePrintParse("{3}", p.initializer)
        parsePrintParse("{3,4}", p.initializer)
        parsePrintParse("{{3},4}", p.initializer)
        parsePrintParse("{.l={{.r={.w={1},.m=2}}},.c=2}", p.initializer)
        parsePrintParse("{ .lock = { { .rlock = { .raw_lock = { 1 } } } } }", p.initializer)
        parsePrintParse("{ .lock = (int) { { .rlock = { .raw_lock = { 1 } } } } }", p.initializer)
        parsePrintParse("{ .lock = { { .rlock = { .raw_lock = { 1 } } } } }", p.initializer)
        parsePrintParse("{ .entry.mask = 1 }", p.initializer) //from io_apic.c
        parsePrintParse("{ [2].y = yv2, [2].x = xv2, [0].x = xv0 }", p.initializer) //from gcc spec
        parsePrintParse("{ [' '] = 1, ['\\t'] = 1, ['\\h'] = 1,\n           ['\\f'] = 1, ['\\n'] = 1, ['\\r'] = 1 }", p.initializer) //from gcc spec
        parsePrintParse("{ [1] = v1, v2, [4] = v4 }", p.initializer) //from gcc spec
        parsePrintParse("{ y: yvalue, x: xvalue }", p.initializer) //from gcc spec
        parsePrintParse("{ .y = yvalue, .x = xvalue }", p.initializer) //from gcc spec
        parsePrintParse("{ [0 ... 9] = 1, [10 ... 99] = 2, [100] = 3 }", p.initializer) //from gcc spec
        parsePrintParse("(int) { .lock = (int) { { .rlock = { .raw_lock = { 1 } } } } }", p.castExpr)
        parsePrintParse("(int) { .lock = (int) { { .rlock = { .raw_lock = { 1 } } } } }", p.expr)
        parsePrintParse("sem = (int) { .lock = (int) { { .rlock = { .raw_lock = { 1 } } } } };", p.statement)
    }

    def testNAryExpr {
        parsePrintParse("a*b", p.multExpr)
        parsePrintParse("a*b*b", p.multExpr)
    }
    def testExprs {
        parsePrintParse("a*b+c", p.expr)
        parsePrintParse("c+a*b", p.expr)
        parsePrintParse("(a+b)*c", p.expr)
        parsePrintParse("a=b==c", p.expr)
        parsePrintParse("a/b", p.expr)
        parsePrintParse("a?b:c", p.expr)
        parsePrintParse("2+(a?b:c)", p.expr)
        parsePrintParse("2+(a?(b+2):c)", p.expr)
        parsePrintParse("(2+2)+(a?(b+2):c)", p.expr)
        parsePrintParse("a,b,c+c/d|x", p.expr)
    }

    private def parsePrintParse(code: String, production: (TokenReader[TokenWrapper, CTypeContext], FeatureExpr) => p.MultiParseResult[AST]) {

        //parse
        val ast = parse(code, production)

        println("AST: " + ast)


        //pretty print
        val doc = prettyPrint(ast)
        val printed = layout(doc)

        println("Pretty: " + printed)

        val ast2 = parse(printed, production)
        println("new AST: " + ast2)

        assertEquals("AST after parsing printed result is different\n" + printed, ast, ast2)
    }


    private def parse(code: String, production: (TokenReader[TokenWrapper, CTypeContext], FeatureExpr) => p.MultiParseResult[AST]): AST = {
        val actual = p.parse(code.stripMargin, production)
        actual match {
            case p.Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                ast
            }
            case p.NoSuccess(msg, unparsed, inner) =>
                fail(msg + " at " + unparsed + " " + inner + "\nin " + code)
                null
        }
    }


    @Test def testDeclarations = {
        def parsePrintParseDecl(str: String) = parsePrintParse(str, p.translationUnit)
        parsePrintParseDecl("int a;")
        parsePrintParseDecl("signed int a;")
        parsePrintParseDecl("unsigned int a;")
        parsePrintParseDecl("unsigned char a;")
        parsePrintParseDecl("unsigned a;")
        parsePrintParseDecl("signed a;")
        parsePrintParseDecl("double a;")
        parsePrintParseDecl("long double a;")

        //allow also uncommon but correct notations
        parsePrintParseDecl("char a;")
        parsePrintParseDecl("signed char a;")
        parsePrintParseDecl("unsigned char a;")
        parsePrintParseDecl("short a;")
        parsePrintParseDecl("short int a;")
        parsePrintParseDecl("unsigned short a;")
        parsePrintParseDecl("int a;")
        parsePrintParseDecl("unsigned int a;")
        parsePrintParseDecl("long int a;")
        parsePrintParseDecl("unsigned long int a;")
        parsePrintParseDecl("long a;")
        parsePrintParseDecl("unsigned long a;")
        parsePrintParseDecl("long long int a;")
        parsePrintParseDecl("unsigned long long int a;")
        parsePrintParseDecl("long long a;")
        parsePrintParseDecl("unsigned long long a;")
        parsePrintParseDecl("float a;")
        parsePrintParseDecl("double a;")
        parsePrintParseDecl("long double a;")

        parsePrintParseDecl("int double a;")
        parsePrintParseDecl("signed unsigned char a;")
        parsePrintParseDecl("auto a;")

        parsePrintParseDecl("double a;")
        parsePrintParseDecl("double a,b;")
        parsePrintParseDecl("double a[];")
        parsePrintParseDecl("double **a;")
        parsePrintParseDecl("double *a[];")
        parsePrintParseDecl("double a[][];")
        parsePrintParseDecl("double *a[][];")
        parsePrintParseDecl("double (*a)[];")
        parsePrintParseDecl("double *(*a[1])();")

        parsePrintParseDecl("void main();")
        parsePrintParseDecl("double (*fp)();")
        parsePrintParseDecl("double *fp();")
        parsePrintParseDecl("void main(double a);")

        parsePrintParseDecl("void main(double*, double);")
        parsePrintParseDecl("void main(double*(), double);")
        parsePrintParseDecl("void main(double(*(*)())());")

        parsePrintParseDecl("struct { double a;} foo;")
        parsePrintParseDecl("struct a foo;")
        parsePrintParseDecl("struct a { double a;} foo;")
        parsePrintParseDecl("struct a;")
    }

}