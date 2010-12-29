package de.fosd.typechef.parser.c

import junit.framework._;
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._
import org.junit.Test

class RepOptTest extends TestCase {
    val p = new CParser()

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
    def parseExtList(code: String): (List[Opt[ExternalDef]], TokenReader[TokenWrapper, CTypeContext]) = {
        val actual = p.parseAny(code.stripMargin, p.externalList)
        (actual: @unchecked) match {
            case p.Success(ast, unparsed) => {
                (ast.asInstanceOf[List[Opt[ExternalDef]]], unparsed);
                //succeed
            }
            case p.NoSuccess(msg, unparsed, inner) => {
                fail(msg + " at " + unparsed + " " + inner)
                (null, unparsed)
            }
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

    def testRepOptMultiFeatureOverlap7_linux() {
        var (ast, next) = parseExtList(""" 
#ifdef A
#ifdef SMP

#ifdef CPU
extern __attribute__((section(".discard"), unused)) char __pcpu_scope_x86_bios_cpu_apicid; extern __attribute__((section(".data.percpu" "")))  __typeof__(u16) per_cpu__x86_bios_cpu_apicid
#else
extern __attribute__((section(".data.percpu" "")))  __typeof__(u16) per_cpu__x86_bios_cpu_apicid
#endif
; extern __typeof__(u16) *x86_bios_cpu_apicid_early_ptr; extern __typeof__(u16) x86_bios_cpu_apicid_early_map[]
#else

#ifdef CPU
extern __attribute__((section(".discard"), unused)) char __pcpu_scope_x86_bios_cpu_apicid; extern __attribute__((section(".data" "")))  __typeof__(u16) per_cpu__x86_bios_cpu_apicid
#else
extern __attribute__((section(".data" "")))  __typeof__(u16) per_cpu__x86_bios_cpu_apicid
#endif

#endif
;    
typedef long y;
typedef long y;
#endif
""")
        ast = flatten(ast)
        println(ast.mkString("\n"))
        println(next)
        assert(ast.size == 10)
    }

    def testRepOptMultiFeatureOverlap6_linux() {
        var (ast, next) = parseExtList(""" 
#ifdef A
#ifdef CPU
extern __attribute__((section(".discard"), unused)) char __pcpu_scope_orig_ist; 
extern __attribute__((section(
#ifdef SMP
".data.percpu"
#else
".data"
#endif
 "")))  __typeof__(struct orig_ist) per_cpu__orig_ist
#else
extern __attribute__((section(
#ifdef SMP
".data.percpu"
#else
".data"
#endif
 "")))  __typeof__(struct orig_ist) per_cpu__orig_ist
#endif
;
#ifdef CPU
extern __attribute__((section(".discard"), unused)) char __pcpu_scope_orig_ist; 
extern __attribute__((section(
#ifdef SMP
".data.percpu"
#else
".data"
#endif
 "")))  __typeof__(struct orig_ist) per_cpu__orig_ist
#else
extern __attribute__((section(
#ifdef SMP
".data.percpu"
#else
".data"
#endif
 "")))  __typeof__(struct orig_ist) per_cpu__orig_ist
#endif
;
typedef unsigned long a;
#else
#ifdef C
typedef long x;
typedef long x;
#else
typedef long y;
typedef long y;
#endif
#endif
""")
        ast = flatten(ast)
        println(ast.mkString("\n"))
        println(next)
        assert(ast.size == 11)
    }

    def testRepOptMultiFeatureOverlap5_linux() {
        var (ast, next) = parseExtList(""" 
#ifdef A
#ifdef CPU
extern __attribute__((section(".discard"), unused)) char __pcpu_scope_orig_ist; 
extern __attribute__((section(
#ifdef SMP
".data.percpu"
#else
".data"
#endif
 "")))  __typeof__(struct orig_ist) per_cpu__orig_ist
#else
extern __attribute__((section(
#ifdef SMP
".data.percpu"
#else
".data"
#endif
 "")))  __typeof__(struct orig_ist) per_cpu__orig_ist
#endif
;
typedef unsigned long a;
#else
#ifdef C
typedef long x;
typedef long x;
#else
typedef long y;
typedef long y;
#endif
#endif
""")
        ast = flatten(ast)
        println(ast.mkString("\n"))
        println(next)
        assert(ast.size == 8)
    }

    def testRepOptMultiFeatureOverlap4() {
        var (ast, next) = parseExtList(""" 
#ifdef A
#ifdef B
typedef char a
#else
#ifdef C
typedef int a
#else
typedef long a
#endif
#endif
;
typedef unsigned long a;
#else
#ifdef C
typedef long x;
typedef long x;
#else
typedef long y;
typedef long y;
#endif
#endif
""")
        ast = flatten(ast)
        println(ast.mkString("\n"))
        println(next)
        assert(ast.size == 8)
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
        val size = ast.asInstanceOf[List[Opt[ExternalDef]]].size
        assert(size == 2 || size == 3)
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
        println(p.featureSolverCache.statistics)
    }
}