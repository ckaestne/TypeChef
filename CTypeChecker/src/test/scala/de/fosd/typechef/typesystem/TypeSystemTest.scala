package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._
import org.kiama.attribution.DynamicAttribution._
import org.kiama._

@RunWith(classOf[JUnitRunner])
class TypeSystemTest extends FunSuite with ShouldMatchers with CTypeAnalysis {


    def getAST(code: String) = {
        val ast: AST = new ParserMain(new CParser).parserMain(
            () => CLexer.lex(code, null), new CTypeContext, false)
        ast should not be (null)
        ast
    }

    def check(code: String) =
        new CTypeSystem().checkAST(getAST(code))

    //    def getFunction(ast:AST, name: String):FunctionD {}

    def functionDef(functionName: String): AST ==> List[FunctionDef] =
        attr {
            case e@FunctionDef(_, DeclaratorId(_, Id(name), _), _, _) => List(e)
            case TranslationUnit(extDefs) => extDefs.map(opt =>
                functionDef(functionName)(opt.entry)
            ).flatten
            case AltExternalDef(_, a, b) => functionDef(functionName)(a) ++ functionDef(functionName)(b)
            case e => List()
        }


    test("function environments") {

        val ast = getAST("void foo() {};" +
                "void bar(){foo();}")
        val foo = functionDef("foo")(ast)

        println(foo -> env)

    }

    test("typecheck simple translation unit") {

        expect(true) {
            check("void foo() {};" +
                    "void bar(){foo();}")
        }

    }


}