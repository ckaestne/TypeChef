package de.fosd.typechef.typesystem

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._

@RunWith(classOf[JUnitRunner])
class IntegerSecurityTest extends FunSuite with ShouldMatchers with TestHelper {

    private def checkExpr(code: String, printAST: Boolean = false): Boolean =
        check("void main() { " + code + "}", printAST)

    private def check(code: String, printAST: Boolean = false): Boolean = {
        println("checking " + code);
        if (printAST) println("AST: " + getAST(code));
        check(getAST(code));
    }
    private def check(ast: TranslationUnit): Boolean = {
        assert(ast != null, "void ast");
        new CTypeSystemFrontend(ast).checkAST
    }


    test("check pointer arithmetic") {
        expect(true) {
            checkExpr("int a; a++;")
        }
        expect(true) {
            checkExpr("int* a; a+1;")
        }
        expect(true) {
            checkExpr("int* a; int x; a+(x=4);") //not overflowing
        }
        expect(true) {
            checkExpr("int* a; int x; a+(x&4);") //not overflowing
        }
        expect(true) {
            checkExpr("int* a; int x,y; a+(x&&y);") //not overflowing
        }
        expect(false) {
            checkExpr("int* a; int x; a+(x-4);") //potentially overflowing
        }
        expect(false) {
            checkExpr("int* a; int x; a+(-x);") //potentially overflowing
        }
        expect(false) {
            checkExpr("int* a; int x; a+(++x);") //potentially overflowing
        }
        expect(false) {
            checkExpr("int* a; int x; a+(x++);") //potentially overflowing
        }
        expect(false) {
            checkExpr("int* a; int x; a+(x<<5);") //potentially overflowing
        }
        expect(false) {
            checkExpr("int* a; int x; a+=(x-5);") //potentially overflowing
        }
        expect(false) {
            checkExpr("int a[]; int x; a[x-5];") //potentially overflowing
        }
    }

}

