package de.fosd.typechef.typesystem

import java.io.{FileNotFoundException, InputStream}
import de.fosd.typechef.parser.c.{TestHelper, TranslationUnit}
import org.junit.Test
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

/**
 * Created with IntelliJ IDEA.
 * User: snadi
 * Date: 24/06/13
 * Time: 1:44 PM
 * To change this template use File | Settings | File Templates.
 */
class ComplexNumTest extends FunSuite with ShouldMatchers with TestHelper {

    private def check(code: String, printAST: Boolean = false): Boolean = {
        println("checking " + code);
        if (printAST) println("AST: " + getAST(code));
        check(getAST(code));
    }
    private def check(ast: TranslationUnit): Boolean = {
        assert(ast != null, "void ast");
        new CTypeSystemFrontend(ast).checkAST()
    }

    test("complex numbers") {
        expect(true) {
            check( """extern long double cosl (long double __x) __attribute__ ((__nothrow__));
                     |extern long double sinl (long double __x) __attribute__ ((__nothrow__));
                     extern double exp (double __x) __attribute__ ((__nothrow__)); extern __typeof (exp) exp __asm__ ("" "__GI_exp") __attribute__ ((visibility ("hidden"),));
                     __complex__ long double cexpl(__complex__ long double z)
                     |{
                     |	__complex__ long double ret;
                     |	long double r_exponent = exp(__real__ z);
                     |
                     |	__real__ ret = r_exponent * cosl(__imag__ z);
                     |	__imag__ ret = r_exponent * sinl(__imag__ z);
                     |
                     |	return ret;
                     |}""".stripMargin)
        }
    }

}
