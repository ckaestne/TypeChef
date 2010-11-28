package de.fosd.typechef.parser.c

import junit.framework._;
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._
import org.junit.Test

class RepOptTest extends TestCase {
    val p = new CParser()

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
    def parseExtList(code: String): (List[Opt[ExternalDef]], TokenReader[TokenWrapper, CTypeContext]) = {
        val actual = p.parseAny(code.stripMargin, p.externalList)
        (actual: @unchecked) match {
            case Success(ast, unparsed) => {
                (ast.asInstanceOf[List[Opt[ExternalDef]]], unparsed);
                //succeed
            }
            case NoSuccess(msg, context, unparsed, inner) => {
                fail(msg + " at " + unparsed + " with context " + context + " " + inner)
                (null, unparsed)
            }
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

       def testRepOptMultiFeatureOverlap3() {
        var (ast, next) = parseExtList(""" 
#ifdef X
#ifdef Y
typedef 
#else
typedef
#endif
#ifdef Y
char 
#else
int
#endif
a;
typedef long a;
#end
""")
        ast = flatten(ast)
        println(ast.mkString("\n"))
        println(next)
        assert(ast.size == 3)
        assert(next.context.knowsType("a"))
    }
       
       def testRepOptMultiFeatureOverlap2() {
        var (ast, next) = parseExtList(""" 
#ifdef X
#ifdef Y
typedef char a
#else
typedef int a
#endif
;
#else
typedef long a;
typedef int b;
#end
""")
        ast = flatten(ast)
        println(ast.mkString("\n"))
        println(next)
        assert(ast.size == 4)
        assert(next.context.knowsType("a"))
    }
       
       def testRepOptMultiFeatureOverlap() {
        var (ast, next) = parseExtList(""" 
#ifdef X
#ifdef Y
typedef char a
#else
typedef int a
#endif
;
#else
typedef long a;
#end
""")
        ast = flatten(ast)
        println(ast.mkString("\n"))
        println(next)
        assert(ast.size == 3)
        assert(next.context.knowsType("a"))
    }
       
       
    def testRepOptMultiFeature() {
        var (ast, next) = parseExtList(""" 
#ifdef X
#ifdef Y
typedef char a;
#else
typedef int a;
#endif
#else
typedef long a;
#end
""")
        ast = flatten(ast)
        println(ast.mkString("\n"))
        println(next)
        assert(ast.size == 3)
        assert(next.context.knowsType("a"))
    }
    
   

    /**
     * for now, we add a type to the type context, even if it 
     * is defined only conditionally XXX
     */
    @Test
    def testRepOptPlain() {
        val (ast, next) = parseExtList(""" 
#ifdef X
typedef char a;
#else
typedef int a;
#endif
typedef int b;
""")
        println(ast)
        println(next)
        assert(ast.asInstanceOf[List[Opt[ExternalDef]]].size == 3)
        assert(next.context.knowsType("a"))
        assert(next.context.knowsType("b"))
    }

    @Test
    def testRepOptPlain2() {
        val (ast, next) = parseExtList(""" 
#ifdef X
typedef char a;
typedef char c;
#else
typedef int a;
typedef int c;
#endif
typedef int b;
""")
        println(ast)
        println(next)
        assert(ast.asInstanceOf[List[Opt[ExternalDef]]].size == 5)
        assert(next.context.knowsType("a"))
        assert(next.context.knowsType("b"))
        assert(next.context.knowsType("c"))
    }

    @Test
    def testRepOptCommonEnd() {
        var (ast, next) = parseExtList(""" 
#ifdef X
typedef char a
#else
typedef int a
#endif
;
typedef int b;
""")
        ast = flatten(ast)
        println(ast.mkString("\n"))
        println(next)
        assert(ast.size == 3)
        assert(next.context.knowsType("a"))
        assert(next.context.knowsType("b"))
    }

    /*    @Test
    def testRepOptCommonEnd2() {
        var (ast, next) = parseExtList(""" 
#ifdef X
typedef char a;
typedef char c
#else
typedef int a;
typedef int c
#endif
;
typedef int b;
""")
        ast = flatten(ast)
        println(ast.mkString("\n"))
        println(next)
        assert(ast.size == 5)
        assert(next.context.knowsType("a"))
        assert(next.context.knowsType("b"))
    }*/

    private def flatten(optList: List[Opt[ExternalDef]]): List[Opt[ExternalDef]] = {
        var result: List[Opt[ExternalDef]] = List()
        for (e <- optList.reverse) {
            e.entry match {
                case AltDeclaration(f, a, b) =>
                    result = flatten(List(Opt(e.feature and f, a))) ++ flatten(List(Opt(e.feature and (f.not), b))) ++ result;
                case _ =>
                    result = e :: result;
            }
        }
        result
    }
    
    @Test
    def testPrintStatistics() {
    	println(FeatureSolverCache.statistics)
    }
}