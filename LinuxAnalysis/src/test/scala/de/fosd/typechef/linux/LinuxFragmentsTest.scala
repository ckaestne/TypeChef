package de.fosd.typechef.linux

import junit.framework._;
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._
import de.fosd.typechef.parser.c._

class LinuxFragmentsTest extends TestCase {
    val p = new CParser(LinuxFeatureModel.featureModelApprox)

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


    //    def testLinux_aes_glue = assertParseable(
    //        """
    //#if definedEx(CONFIG_DISCONTIGMEM)
    //static int pfn_valid(unsigned long pfn) {
    //	if (((pfn) >> (                      29 - 12)) >= (1UL << (36 -
    //#if (definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM))
    //29
    //#endif
    //#if (definedEx(CONFIG_SPARSEMEM) && !definedEx(CONFIG_X86_PAE))
    //26
    //#endif
    //)))
    //		return 0;
    //	return valid_section(__nr_to_section(((pfn) >> (29 - 12))));
    //}
    //#endif
    //        """, p.phrase(p.translationUnit))


    def testLinux_ptrace = assertParseable(
        """static char vmac_string[128] = {'\x01', '\x01', '\x01', '\x01',
                                '\x02', '\x03', '\x02', '\x02',
                                '\x02', '\x04', '\x01', '\x07',
                                '\x04', '\x01', '\x04', '\x03',};""", p.phrase(p.translationUnit))


    def testLinux_mmu = {
        assertParseable("""return (gpte & ((((1ULL << 52) - 1) & ~(u64)(((1UL) << 12)-1)) & ~((1ULL << (12 + (((lvl) - 1) * 9))) - 1))) >> 12;""", p.phrase(p.statement))
        assertParseable(
            """typedef unsigned long  gfn_t;
            static gfn_t paging64_gpte_to_gfn_lvl(int gpte, int lvl)
            {
                return (gpte & ((((1ULL << 52) - 1) & ~(u64)(((1UL) << 12)-1)) & ~((1ULL << (12 + (((lvl) - 1) * 9))) - 1))) >> 12;
            }""", p.phrase(p.translationUnit))
    }

    def testLinux2 {
        assertParseable(
            """  __builtin_type *""", p.phrase(p.typeName))
        assertParseable(
            """    ap""", p.phrase(p.expr))
        assertParseable(
            """    __builtin_va_arg(ap,__builtin_type *)""", p.phrase(p.primaryExpr))
        assertParseable(
            """    string = __builtin_va_arg(ap,__builtin_type *);""", p.phrase(p.statement))
    }

    def testLinux3 {
    }

    def testLinux4 {
        assertParseable("""(__builtin_constant_p((6)) ? __constant_memcpy3d((&mesg->content.normal.mac_addr), (mac_addr), (6)) : __memcpy3d((&mesg->content.normal.mac_addr), (mac_addr), (6)));""", p.phrase(p.statement))
        assertParseable("""__memcpy((&mesg->content.normal.mac_addr), (mac_addr), (6));""", p.phrase(p.statement))


    }

    def testLinux5 = assertParseable(
        """void foo(){
        return 0;
nla_put_failure: __attribute__ ((unused))
        return -1;
}""", p.phrase(p.translationUnit))


}