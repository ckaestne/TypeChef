package de.fosd.typechef.ast

import _root_.de.fosd.typechef.ast._
import org.junit.Test
import de.fosd.typechef.parser._
import de.fosd.typechef.featureexpr.FeatureExpr
import org.junit.Assert._
import TestAST._

class ASTTest {


    val a = FeatureExpr.createDefinedExternal("a")
    val b = FeatureExpr.createDefinedExternal("b")

    /**
     * the following tests only describe the expected interface and will not
     * fail (they will at most not compile on interface changes)
     */
    @Test
    def buildAST_NoChoice =
        println(
            new CompUnit(
                One(new Pckg(One("com.test"))),
                Many()
            )
        )

    @Test
    def buildAST_ChoiceOneInOpt =
        println(
            new CompUnit(
                Choice(a, One(new Pckg(One("com.test"))), One(new Pckg(One("org.test")))),
                Many()
            )
        )

    @Test
    def buildAST_ChoiceOpt =
        println(
            new CompUnit(
                Choice(a, Opt(b, new Pckg(One("com.test"))), One(new Pckg(One("org.test")))),
                Many()
            )
        )

    @Test
    def buildAST_ChoiceOne =
        println(
            new Pckg(Choice(a, One("com.test"), One("org.test")))
        )
    //    @Test Should not compile
    //  def buildAST4b =
    //  println(
    //      new Pckg(Choice(a,Opt(b,"com.test"),One("org.test")))
    //  )

    @Test
    def buildAST_EmptyPackage =
        println(
            new CompUnit(
                Opt(),
                Many()
            )
        )

    @Test
    def buildAST_List =
        println(
            new CompUnit(
                Opt(),
                Many(One(new Imp(One("org.junit.Test"))))
            )
        )
    @Test
    def buildAST_List2a =
        println(
            new CompUnit(
                Opt(),
                Many(One(new Imp(One("org.junit.Test"))), Opt(), Opt())
            )
        )
    @Test
    def buildAST_List2 =
        println(
            new CompUnit(
                Opt(),
                Many(
                    One(new Imp(One("org.junit.Test"))),
                    Opt(a, new Imp(One("org.junit.Test"))),
                    Opt(b, new Imp(One("org.junit.Test")))
                )
            )
        )
    @Test
    def buildAST_List3 =
        println(
            new CompUnit(
                Opt(),
                Many(
                    Choice(a, One(new Imp(One("org.junit.Test"))), One(new Imp(One("org.junit.Before")))),
                    Choice(b, One(new Imp(One("x.junit.Test"))), One(new Imp(One("x.junit.Before"))))
                )
            )
        )
    @Test
    def buildAST_List4 =
        println(
            new CompUnit(
                Opt(),
                Many(
                    Choice(a, One(new Imp(One("org.junit.Test"))),
                        Choice(b, One(new Imp(One("x.junit.Test"))), One(new Imp(One("x.junit.Before")))))
                )
            )
        )

//    @Test
//    def distribution_1 =
//        assertEquals(
//            new CompUnit(
//                Choice(a, One(new Pckg(One("com.test"))), One(new Pckg(One("org.test")))),
//                Many()
//            ),
//            new CompUnit(
//                One(new Pckg(Choice(a, One("com.test"), One("org.test")))).distribute,
//                Many()
//            )
//        )
//
//    @Test
//    def factor_1 =
//        assertEquals(
//            new CompUnit(
//                One(new Pckg(Choice(a, One("com.test"), One("org.test")))),
//                Many()
//            ),
//            new CompUnit(
//                Choice(a, One(new Pckg(One("com.test"))), One(new Pckg(One("org.test")))),
//                Many()
//            ).factor
//        )
}