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

    val ast = (getAST("""
            int foo;
            struct account {
               int account_number;
               char *first_name;
               char *last_name;
               float balance, *b;
            };
            int bar;
            restricted account* a;
            void main(double a);"""))

    test("parse struct decl") {

        val env: StructEnv = ast.defs.last.entry -> structEnv

        println(env.env)

        //should be in environement
        env.contains("account") should be(true)
        val accountStruct = env.get("account")

        //four fields
        accountStruct.size should be(5)
        //should have field "firstname"
        accountStruct.filter(_._1 == "first_name").size should be(1)
        //should have correct type
        val firstname = accountStruct.filter(_._1 == "first_name").head
        val balance = accountStruct.filter(_._1 == "balance").head

        balance._3 should be(CFloat())
        firstname._3 should be(CPointer(CChar()))
    }

}