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

    private def ast = (getAST("""
            typedef int myint;
            typedef struct { double x; } mystr;
            typedef struct pair { double x,y; } mypair;
            myint myintvar;
            mystr *mystrvar;
            mypair mypairvar;

            int foo;
            struct account {
               int account_number;
               char *first_name;
               char *last_name;
               float balance, *b;
            } acc;
            int bar;
            struct account *a;
            void main(double a);
            int i() { int inner; double foo=0; return foo; }
            double inner;
            """))

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

    test("variable environment") {
        val env = ast.defs.last.entry -> varEnv

        env("foo") should be(CSignUnspecified(CInt()))
        env("bar") should be(CSignUnspecified(CInt()))
        env("a") should be(CPointer(CStruct("account")))
        env("acc") should be(CStruct("account"))
        env("main") should be(CFunction(Seq(CDouble()), CVoid()))

        env("i") should be(CFunction(Seq(), CSignUnspecified(CInt())))
        env("inner") should be(CDouble())
    }

    test("variable scoping") {
        //finding but last statement in last functiondef
        val fundef = ast.defs.takeRight(2).head.entry.asInstanceOf[FunctionDef]
        val env = fundef.stmt.asInstanceOf[CompoundStatement].innerStatements.last.entry -> varEnv

        println(env)

        env("inner") should be(CSignUnspecified(CInt()))
        env("foo") should be(CDouble())
    }

    test("typedef synonyms") {
        val env = ast.defs.last.entry -> varEnv
        val typedefs = ast.defs.last.entry -> typedefEnv

        typedefs("myint") should be(CSignUnspecified(CInt()))
        typedefs("mystr") should be(CAnonymousStruct(List(("x", CDouble()))))

        //typedef is not a declaration
        env.contains("myint") should be(false)
        env.contains("mystr") should be(false)

        env("myintvar") should be(CSignUnspecified(CInt()))
        env("mystrvar") should be(CPointer(CAnonymousStruct(List(("x", CDouble())))))
        env("mypairvar") should be(CStruct("pair"))

        //structure definitons should be recognized despite typedefs
        val structenv: StructEnv = ast.defs.last.entry -> structEnv
        structenv.contains("pair") should be(true)
        structenv.contains("mystr") should be(false)
    }

}