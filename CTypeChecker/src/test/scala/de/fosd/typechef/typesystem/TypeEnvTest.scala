package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._

@RunWith(classOf[JUnitRunner])
class TypeEnvTest extends FunSuite with ShouldMatchers with CTypeAnalysis with CTypeEnv {

    private def getAST(code: String) = {
        val ast: AST = new ParserMain(new CParser).parserMain(
            () => CLexer.lex(code, null), new CTypeContext, false)
        ast should not be (null)
        ast.asInstanceOf[TranslationUnit]
    }

    test("parse struct decl") {
        println(getAST("""
struct account {
   int account_number;
   char *first_name;
   char *last_name;
   float balance;
};        """))
    }

}