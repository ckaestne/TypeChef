package de.fosd.typechef.typesystem


import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.parser.c.TestHelper
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSuite, Matchers}

@RunWith(classOf[JUnitRunner])
class ChoiceTypesTest extends FunSuite with Matchers with CTypeSystem with CEnvCache with TestHelper {


    test("alternatives in declarations") {
        t()
    }
    def t() {
        val ast = getAST( """
         #ifdef X
         int a;
         #else
         double a;
         #endif

         #ifdef X
         int x;
         #endif
         #ifdef Y
         double x;
         #endif

         double
         #ifdef X
         b
         #else
         c
         #endif
         ;

         #ifdef X
         const
         #endif
         double y;

         int end;""")
        typecheckTranslationUnit(ast)
        val env = lookupEnv(ast.defs.last.entry).varEnv

        env("a").map(_.atype) should be(Choice(fx.not, One(CDouble()), One(CSigned(CInt()))))
        env("x").map(_.atype) should be(Choice(fy, One(CDouble()), Choice(fx, One(CSigned(CInt())), One(CUndefined))))
        env("b").map(_.atype) should be(Choice(fx, One(CDouble()), One(CUndefined)))
        env("y") should be(Choice(fx,
            One(CType(CDouble(), false, false, true)), One(CType(CDouble(), false, false, false))))
    }


    test("inlined functions") {
        val ast = getAST( """static
#if !defined(CONFIG_OPTIMIZE_INLINING)
inline __attribute__((always_inline))
#endif
#if defined(CONFIG_OPTIMIZE_INLINING)
inline
#endif
 void __rcu_read_lock_bh(void)
{
	local_bh_disable();
}""")
        val env = checkTranslationUnit(ast, FeatureExprFactory.True, EmptyEnv).varEnv
//        println(env)
        env("__rcu_read_lock_bh").map(_.atype) should be(One(CFunction(List(), CVoid())))
    }


}