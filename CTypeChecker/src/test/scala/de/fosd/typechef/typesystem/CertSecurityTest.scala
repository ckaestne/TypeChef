package de.fosd.typechef.typesystem

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr.FeatureExprFactory

/**
 * test for various CERT rules implemented in typechef
 */
@RunWith(classOf[JUnitRunner])
class CertSecurityTest extends FunSuite with ShouldMatchers with TestHelper {

    private def checkExpr(code: String, enableAnalysis: Boolean, printAST: Boolean = false): Boolean =
        check("void main() { " + code + "}", enableAnalysis, printAST)

    private def check(code: String, enableAnalysis: Boolean, printAST: Boolean = false): Boolean = {
        println("checking " + code);
        if (printAST) println("AST: " + getAST(code));
        check(getAST(code), enableAnalysis);
    }
    private def check(ast: TranslationUnit, enableAnalysis: Boolean): Boolean = {
        assert(ast != null, "void ast");
        new CTypeSystemFrontend(ast, FeatureExprFactory.default.featureModelFactory.empty,
            if (enableAnalysis) new LinuxDefaultOptions {
                override def warning_long_designator = true
                override def warning_implicit_identifier = true
            } else LinuxDefaultOptions
        ).checkAST(false)
    }

    def correct(code: String) {
        assert(check(code, false), "Expected correct code, but found error without analysis")
        assert(check(code, true), "Expected correct code, but found error with analysis")
    }
    def error(code: String) {
        assert(check(code, false), "Expected correct code (without analysis), but found error without analysis")
        assert(!check(code, true), "Expected error (with analysis), but found no error with analysis")
    }
    def correctExpr(code: String) {
        assert(checkExpr(code, false), "Expected correct code, but found error without analysis")
        assert(checkExpr(code, true), "Expected correct code, but found error with analysis")
    }
    def errorExpr(code: String) {
        assert(checkExpr(code, false), "Expected correct code (without analysis), but found error without analysis")
        assert(!checkExpr(code, true), "Expected error (with analysis), but found no error with analysis")
    }


    /**
     * very simple, structural rule
     */
    test("DCL16-C. Use L, not l, to indicate a long value") {
        correctExpr("long a = 1L;")
        errorExpr("long a = 1l;")
    }

    /**
     * very simple, structural rule
     */
    test("DCL31-C. Declare identifiers before using them -- implicit int") {
        correct("extern int foo;")
        error("extern foo;")

    }

    test("DCL31-C. Declare identifiers before using them -- implicit function declaration") {
        //we are actually stronger than the specification (it fails without activating warnings)
        assert(!check(
            """
              |int main(void) {
              |  int c = foo();
              |  return 0;
              |}
              |
              |int foo(int a) {
              |  return a;
              |}
            """.stripMargin, true))
    }


}

