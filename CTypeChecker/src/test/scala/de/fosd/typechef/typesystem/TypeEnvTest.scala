package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._

@RunWith(classOf[JUnitRunner])
class TypeEnvTest extends FunSuite with ShouldMatchers with CTypeAnalysis with CTypeEnv with CTypes {

    private def getAST(code: String) = {
        val ast: AST = new ParserMain(new CParser).parserMain(
            () => CLexer.lex(code, null), new CTypeContext, false)
        ast should not be (null)
        ast.asInstanceOf[TranslationUnit]
    }

    test("parse struct decl") {
        val ast = (getAST("""
struct account {
   int account_number;
   char *first_name;
   char *last_name;
   float balance;
};        """))
        val env: StructEnv = ast.defs.last.entry -> structEnv

        println(env.env)

        //should be in environement
        env.contains("account") should be(true)
        //four fields
        env.get("account").size should be(4)
        //should have field "firstname"
        env.get("account").filter(_._1 == "first_name").size should be(1)
        //should have numeric type
        isIntegral(env.get("account").filter(_._1 == "first_name").head._3) should be(true)
    }

}