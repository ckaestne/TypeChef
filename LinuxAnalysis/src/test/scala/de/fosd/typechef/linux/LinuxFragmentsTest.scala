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

}