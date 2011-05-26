package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr.FeatureExpr
import org.scalatest.FunSuite

@RunWith(classOf[JUnitRunner])
class DeclTypingTest extends FunSuite with ShouldMatchers with CTypes with CDeclTyping {


    private def parseDecl(code: String): ADeclaration = {
        val in = CLexer.lex(code, null).setContext(new CTypeContext())
        val p = new CParser()
        val r = p.phrase(p.declaration)(in, FeatureExpr.base)
        println(r);
        r.asInstanceOf[p.Success[ADeclaration]].result
    }
    private def declTL(code: String) = {
        val ast = parseDecl(code)
        val r = declType(ast)
        r
    }
    private def declT(code: String) = declTL(code)(0)._2

    test("variable declarations") {
        declTL("double a;") should be(List(("a", CDouble())))
        declTL("double a,b;") should be(List(("a", CDouble()), ("b", CDouble())))
        declTL("double a[];") should be(List(("a", CArray(CDouble()))))
        declTL("double **a;") should be(List(("a", CPointer(CPointer(CDouble())))))
        declTL("double *a[];") should be(List(("a", CArray(CPointer(CDouble())))))
        declTL("double a[][];") should be(List(("a", CArray(CArray(CDouble())))))
        declTL("double *a[][];") should be(List(("a", CArray(CArray(CPointer(CDouble()))))))
        declTL("double (*a)[];") should be(List(("a", CPointer(CArray(CDouble())))))
        declT("double *(*a[1])();") should be(CArray(CPointer(CFunction(Seq(), CPointer(CDouble())))))
    }

    test("function declarations") {
        declT("void main();") should be(CFunction(Seq(), CVoid()))
        declT("double (*fp)();") should be(CPointer(CFunction(Seq(), CDouble())))
        declT("double *fp();") should be(CFunction(Seq(), CPointer(CDouble())))
        declT("void main(double a);") should be(CFunction(Seq(CDouble()), CVoid()))
    }

    test("function declarations with abstract declarators") {
        declT("void main(double*, double);") should be(CFunction(Seq(CPointer(CDouble()), CDouble()), CVoid()))
        declT("void main(double*(), double);") should be(CFunction(Seq(CFunction(Seq(), CPointer(CDouble())), CDouble()), CVoid()))
        declT("void main(double(*(*)())());") should be(CFunction(Seq(
            CPointer(CFunction(Seq(), CPointer(CFunction(Seq(), CDouble()))))
        ), CVoid()))

    }

}