package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c.{TestHelper, TranslationUnit}
import org.scalatest.{Matchers, FunSuite}

/**
 * Created with IntelliJ IDEA.
 * User: snadi
 * Date: 24/06/13
 * Time: 1:44 PM
 * To change this template use File | Settings | File Templates.
 */
class ComplexNumTest extends FunSuite with Matchers with TestHelperTS {



    test("complex numbers") {
        assertResult(true) {
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
