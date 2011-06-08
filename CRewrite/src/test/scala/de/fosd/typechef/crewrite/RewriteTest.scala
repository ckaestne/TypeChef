package de.fosd.typechef.crewrite

import org.junit.Test
import de.fosd.typechef.parser.c.TestHelper
import de.fosd.typechef.parser.c._
import de.fosd.typechef.parser.Opt
import org.kiama.attribution.Attributable
   import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class RewriteTest extends FunSuite with ShouldMatchers with TestHelper {


    @Test
    def eliminateVariability() {
        val ast= getAST("""
        int foo() {
            #ifdef X
            foo();
            #else
            bar();
            #endif
        }
        """)

        val newast=new IfdefToIf().rewrite(ast)

        assertNotVariability(newast)

        println(PrettyPrinter.print(newast))
    }


    private def assertNotVariability(ast:Attributable) {
        ast match {
            case Opt(f,_) => assert(f.isTautology,"Optional children not expected: "+ast)
            case c:Choice[_] => assert(false,"Choice nodes not expected: "+ast)
            case _=>
        }

        for (c<-ast.children)
            assertNotVariability(c)


    }

    test("rewrite variability granularity") {
       val ast= getAST("""
        int foo() {
            foo(
            #ifdef X
                a
            #endif
            );
            static
            #ifdef Y
            long
            #endif
            int a=3;
        }
        """)
        println(ast)

        val cv=new CoarseVariability()
        cv.check(ast) should be (false)

        val newast=cv.rewrite(ast)
        cv.check(newast) should be (true)



    }

}