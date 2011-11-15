package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.junit.runner.RunWith
import de.fosd.typechef.conditional._
import org.scalatest.FunSuite

@RunWith(classOf[JUnitRunner])
class RewriteTest extends FunSuite with ShouldMatchers with TestHelper {

    private def processTest(t: => TranslationUnit) = {
        val newast = new IfdefToIf().rewrite(t)
        println(t)
        println(PrettyPrinter.print(t))
        assertNotVariability(newast)
        println(PrettyPrinter.print(newast))
    }

    //strange rewrite bug. worked before. fix later, see Issue #6
    test("eliminateVariabilityFunctionCall") {
        processTest(getAST("""
        volatile int k;
        int foo() {
            if (!x || y || z) test();
            int a;
            #if (defined(X) || defined(Y)) && defined(Z)
            foo();
            #else
            bar();
            #endif
            #ifdef Y
            spam();
            #endif
        }
        """))
    }

    //strange rewrite bug. worked before. fix later, see Issue #6
    test("eliminateVariabilityIfStatement") {
        processTest(getAST("""
      int foo() {
        #if defined(X)
        if (null)
        #endif
        foo();
      }
      """))
    }


    private def assertNotVariability(ast: Product) {
        ast match {
            case Opt(f, _) => assert(f.isTautology, "Optional children not expected: " + ast)
            case c: Choice[_] => assert(false, "Choice nodes not expected: " + ast)
            case _ =>
        }

        for (c <- ast.productIterator)
            if (c.isInstanceOf[Product])
                assertNotVariability(c.asInstanceOf[Product])
    }

}