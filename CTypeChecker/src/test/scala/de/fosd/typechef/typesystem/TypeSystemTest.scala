package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._
import org.kiama.attribution.DynamicAttribution._
import org.kiama._
import de.fosd.typechef.featureexpr.FeatureExpr
import FeatureExpr.base
import de.fosd.typechef.parser.Opt

@RunWith(classOf[JUnitRunner])
class TypeSystemTest extends FunSuite with ShouldMatchers with CTypeAnalysis {


    private def getAST(code: String) = {
        val ast: AST = new ParserMain(new CParser).parserMain(
            () => CLexer.lex(code, null), new CTypeContext, false)
        ast should not be (null)
        ast
    }

    private def check(code: String) =
        new CTypeSystem().checkAST(getAST(code))

    private def functionDef(functionName: String): AST ==> List[FunctionDef] =
        attr {
            case e@FunctionDef(_, DeclaratorId(_, Id(name), _), _, _) if (name == functionName) => List(e)
            case TranslationUnit(extDefs) => extDefs.map(opt =>
                functionDef(functionName)(opt.entry)
            ).flatten
            case AltExternalDef(_, a, b) => functionDef(functionName)(a) ++ functionDef(functionName)(b)
            case e => List()
        }

    val ast = getAST("void foo() {}" +
            "void bar(){foo();}")
    val foo = functionDef("foo")(ast).head
    val bar = functionDef("bar")(ast).head

    private val fa = FeatureExpr.createDefinedExternal("a")
    private val fb = FeatureExpr.createDefinedExternal("b")
    private val fc = FeatureExpr.createDefinedExternal("c")
    private def o[T](o: T) = Option(base, o)

    test("ast navigation with Opt") {
        foo -> parentAST should equal(ast)
        bar -> parentAST should equal(ast)
        bar -> prevAST should equal(foo)
    }
    test("ast navigation with Choice and Opt") {
        val stmt0 = LabelStatement(Id("stmt0"), None)
        val stmt1 = LabelStatement(Id("stmt1"), None)
        val stmt2 = LabelStatement(Id("stmt2"), None)
        val stmt3 = LabelStatement(Id("stmt3"), None)
        val stmt4 = LabelStatement(Id("stmt4"), None)
        val stmt5 = LabelStatement(Id("stmt5"), None)
        val stmt6 = LabelStatement(Id("stmt6"), None)
        val stmt7 = LabelStatement(Id("stmt7"), None)
        val choicestmt = AltStatement(fc, stmt1, AltStatement(fa, AltStatement(fb, stmt2, stmt3), AltStatement(fb, stmt4, stmt5)))
        val root = CompoundStatement(List(stmt0, choicestmt, stmt6, stmt7).map(Opt(base, _)))
        stmt0 -> prevAST should equal(null)
        stmt1 -> prevAST should equal(stmt0)
        stmt2 -> prevAST should equal(stmt1)
        stmt3 -> prevAST should equal(stmt2)
        stmt4 -> prevAST should equal(stmt3)
        stmt5 -> prevAST should equal(stmt4)
        stmt6 -> prevAST should equal(stmt5)
        stmt7 -> prevAST should equal(stmt6)
        stmt0 -> parentAST should equal(root)
        stmt1 -> parentAST should equal(root)
        stmt2 -> parentAST should equal(root)
        stmt3 -> parentAST should equal(root)
        stmt4 -> parentAST should equal(root)
        stmt5 -> parentAST should equal(root)
        stmt6 -> parentAST should equal(root)
        stmt7 -> parentAST should equal(root)
    }

    test("function environments") {
        env(foo) find ("foo") should have size (1)
        env(foo) find ("bar") should be('empty)

        env(bar) find ("foo") should have size (1)
        env(bar) find ("bar") should have size (1)
        env(bar) find ("foo") should have size (1)
    }

    test("typecheck simple translation unit") {

        expect(true) {
            check("void foo() {};" +
                    "void bar(){foo();}")
        }

    }


}