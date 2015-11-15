package de.fosd.typechef.typesystem

import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.parser.c._
import org.junit.runner.RunWith
import org.scalatest.{Matchers, FunSuite}
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class IntegerSecurityTest extends FunSuite with Matchers with TestHelperTSConditional {


    override protected def check(ast: TranslationUnit, enableAnalysis: Boolean): Boolean = {
        assert(ast != null, "void ast");
        val frontend =
            new CTypeSystemFrontend(ast, FeatureExprFactory.default.featureModelFactory.empty, if (enableAnalysis) new LinuxDefaultOptions {
                override def warning_potential_integer_overflow = true
                override def warning_implicit_coercion = true
            } else new LinuxDefaultOptions {})
        frontend.makeSilent().checkAST(false).isEmpty
    }


    /**
     * very simply approximation, relying on structural nesting
     *
     * data-flow dependent analysis required for more precision, see ignored case below
     */
    test("check pointer arithmetic -- structural nesting") {
        correctExpr("int a; a++;")
        correctExpr("int* a; a+1;")
        correctExpr("int* a; int x; a+(x=4);") //not overflowing
        correctExpr("int *a,*b; b=a+2;") //not overflowing
        correctExpr("int* a; int x; a+x;") //not overflowing
        correctExpr("int* a; int x; a[x];") //not overflowing
        correctExpr("int* a; int x; a+(x&4);") //not overflowing
        correctExpr("int* a; int x,y; a+(x&&y);") //not overflowing
        errorExpr("int* a; int x; a+(x-4);") //potentially overflowing
        errorExpr("int* a; int x; a+(-x);") //potentially overflowing
        errorExpr("int* a; int x; a+(++x);") //potentially overflowing
        errorExpr("int* a; int x; a+(x++);") //potentially overflowing
        errorExpr("int* a; int x; a+(x<<5);") //potentially overflowing
        errorExpr("int* a; int x; a+=(x-5);") //potentially overflowing
        errorExpr("int a[]; int x; a[x-5];") //potentially overflowing
    }

    ignore("check pointer arithmetic -- data flow") {
        errorExpr("int* a; int x; int y = x -4; a+y;") //potentially overflowing
        errorExpr("int a[]; int x;int y = x %4; a[y];") //potentially overflowing
    }

    test("check memory allocation -- structural nesting") {
        correct("int printf(const char * restrict format, ...);void main(){int x; printf(\"%d\",x+1);}")
        error("void *malloc(int size); void main(){int x; malloc(x+1);}")
    }


    test("integer conversions") {
        correctExpr("int a; int b; b=a;")
        correctExpr("int a; long b; b=a;") //widening is okay
        correctExpr("int a; long b; a=(int)b;") //narrowing is okay when explicit with a cast
        errorExpr("int a; long b; a=b;")
        errorExpr("unsigned int a; signed b; b=a;")
    }


}

