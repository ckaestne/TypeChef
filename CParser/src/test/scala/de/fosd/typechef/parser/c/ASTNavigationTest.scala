package de.fosd.typechef.parser.c

import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.FeatureExprFactory._
import de.fosd.typechef.featureexpr._
import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, Suite}

@RunWith(classOf[JUnitRunner])
class ASTNavigationTest extends Matchers with ASTNavigation with ConditionalNavigation with EnforceTreeHelper with TestHelper with Suite{


    val ast = getAST("void foo() {}" +
        ";" +
        "void bar(){foo();}")
    val foo = ast.defs.head.entry
    val bar = ast.defs.last.entry

    private val ga = FeatureExprFactory.createDefinedExternal("a")
    private val gb = FeatureExprFactory.createDefinedExternal("b")
    private val gc = FeatureExprFactory.createDefinedExternal("c")
    private val gd = FeatureExprFactory.createDefinedExternal("d")

    @Test
    def test_filter_switch() {
        val a = getAST(
            """void foo() {
              switch (a) {
                case 1:
                break;
                case 2:
                switch (b) {
                  case 3:
                  case 4:
                }
              }
            }
            """.stripMargin)

        filterASTElems[SwitchStatement](a).size should be(1)
        filterAllASTElems[SwitchStatement](a).size should be(1)
    }

    @Test
    def test_filter_postfix() {
        val a = getAST(
            """void foo() {
              free(a->b->c);
            }
            """.stripMargin)
        println(a)
        filterASTElems[PointerPostfixSuffix](a).size should be(2)
        filterAllASTElems[PointerPostfixSuffix](a).size should be(2)
    }


    @Test
    def test_ast_navigtition_nextopt_onlytrue() {
        val stmt0 = LabelStatement(Id("stmt0"), None)
        val stmt1 = LabelStatement(Id("stmt1"), None)
        val stmt2 = LabelStatement(Id("stmt2"), None)
        val optstmt0 = Opt(True, stmt0)
        val optstmt1 = Opt(True, stmt1)
        val optstmt2 = Opt(True, stmt2)
        val cp = CompoundStatement(List(optstmt0, optstmt1, optstmt2))
        val env = CASTEnv.createASTEnv(cp)
        nextOpt(optstmt0, env) should be(optstmt1)
        nextOpt(optstmt1, env) should be(optstmt2)
    }

    @Test
    def test_ast_navigation_nextopt_conditionals() {
        val stmt0 = LabelStatement(Id("stmt0"), None)
        val stmt1 = LabelStatement(Id("stmt1"), None)
        val stmt2 = LabelStatement(Id("stmt2"), None)
        val optstmt0 = Opt(True, stmt0)
        val optstmt1 = Opt(fa, stmt1)
        val optstmt2 = Opt(True, stmt2)
        val cp = CompoundStatement(List(optstmt0, optstmt1, optstmt2))
        val env = CASTEnv.createASTEnv(cp)

        nextOpt(optstmt0, env) should be(optstmt1)
    }

    @Test
    def test_ast_navigation_prev_and_next_with_Opt_and_Choice_tree() {
        implicit def toOne[T](x: T): Conditional[T] = One(x)
        val stmt0 = LabelStatement(Id("stmt0"), None)
        val stmt1 = LabelStatement(Id("stmt1"), None)
        val stmt2 = LabelStatement(Id("stmt2"), None)
        val stmt3 = LabelStatement(Id("stmt3"), None)
        val stmt4 = LabelStatement(Id("stmt4"), None)
        val stmt5 = LabelStatement(Id("stmt5"), None)
        val stmt6 = LabelStatement(Id("stmt6"), None)
        val stmt7 = LabelStatement(Id("stmt7"), None)
        val stmt8 = LabelStatement(Id("stmt8"), None)
        val stmt9 = LabelStatement(Id("stmt9"), None)
        val optstmt0 = Opt(gd, stmt0)
        val optstmt6 = Opt(gd, stmt6)
        val optstmt7 = Opt(gd, stmt7)
        val optstmt8 = Opt(FeatureExprFactory.True, stmt8)
        val optstmt9 = Opt(FeatureExprFactory.True, stmt9)

        val c1 = Choice(gb, stmt4, stmt5)
        val c2 = Choice(gb, stmt2, stmt3)
        val c3 = Choice(ga, c2, c1)
        val c4 = Choice(gc, stmt1, c3)
        val optc4 = Opt(gd, c4)
        val l = List[Opt[Statement]](optstmt0) ++
            ConditionalLib.flatten(List(optc4)) ++
            List[Opt[Statement]](optstmt6, optstmt7, optstmt8, optstmt9)

        val root = CompoundStatement(l)
        println(PrettyPrinter.print(root))

        //#if fd
        //stmt0
        //#if fc // c4
        //stmt1
        //#else
        //  #if fa // c3
        //    #if fb // c2
        //    stmt2
        //    #else
        //    stmt3
        //    #endif
        //  #else
        //    #if fb // c1
        //    stmt4
        //    #else
        //    stmt5
        //    #endif
        //  #endif
        //#endif
        //stmt6
        //stmt7
        //#endif
        //stmt8
        //stmt9

        val env = CASTEnv.createASTEnv(root)
        prevOpt(optstmt7, env) should equal(optstmt6)
        nextOpt(optstmt6, env) should equal(optstmt7)
        nextOpt(optstmt7, env) should equal(optstmt8)
        nextOpt(nextOpt(optstmt6, env), env) should equal(optstmt8)
    }
}