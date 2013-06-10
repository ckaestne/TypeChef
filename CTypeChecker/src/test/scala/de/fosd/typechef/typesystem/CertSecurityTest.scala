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

    private def checkExpr(code: String, printAST: Boolean = false): Boolean =
        check("void main() { " + code + "}", printAST)

    private def check(code: String, printAST: Boolean = false): Boolean = {
        println("checking " + code);
        if (printAST) println("AST: " + getAST(code));
        check(getAST(code));
    }
    private def check(ast: TranslationUnit): Boolean = {
        assert(ast != null, "void ast");
        new CTypeSystemFrontend(ast, FeatureExprFactory.default.featureModelFactory.empty, new LinuxDefaultOptions {
            override def warning_long_designator = true
        }).checkAST(false)
    }


    /**
     * very simple, structural rule
     */
    test("DCL16-C. Use L, not l, to indicate a long value") {
        expect(true) {
            checkExpr("long a = 1L;")
        }
        expect(false) {
            checkExpr("long a = 1l;")
        }
    }
}

