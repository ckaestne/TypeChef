package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._

@RunWith(classOf[JUnitRunner])
class TypeEnvTest extends FunSuite with ShouldMatchers with CTypeEnv with CTypes with TestHelper {


    private def ast = (getAST("""
            typedef int myint;
            typedef struct { double x; } mystr;
            typedef struct pair { double x,y; } mypair;
            typedef unsigned myunsign;
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
            union uaccount {
                int foo;
                int bar;
            } uua;
            union uaccount *ua;
            int bar;
            struct account *a;
            void main(double a);
            int i(double param, void (*param2)(void)) { int inner; double foo=0; return foo; }
            double inner;
            """))

    test("parse struct decl") {

        val env: StructEnv = ast.defs.last.entry -> structEnv

        println(env.env)

        //struct should be in environement
        env.contains("account", false) should be(true)
        env.contains("account", true) should be(false) //not a union

        env.contains("uaccount", false) should be(false)
        env.contains("uaccount", true) should be(true) //a union

        val accountStruct = env.get("account", false)

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

        env("foo") should be(CSigned(CInt()))
        env("bar") should be(CSigned(CInt()))
        env("a") should be(CPointer(CStruct("account")))
        env("ua") should be(CPointer(CStruct("uaccount", true)))
        env("acc") should be(CStruct("account"))
        env("main") should be(CFunction(Seq(CDouble()), CVoid()))

        env("i") should be(CFunction(Seq(CDouble(), CPointer(CFunction(Seq(CVoid()), CVoid()))), CSigned(CInt())))
        env("inner") should be(CDouble())
    }

    test("variable scoping") {t()}
    def t() {
        //finding but last statement in last functiondef
        val fundef = ast.defs.takeRight(2).head.entry.asInstanceOf[FunctionDef]
        val env = fundef.stmt.asInstanceOf[CompoundStatement].innerStatements.last.entry -> varEnv

        println(env)

        env("inner") should be(CSigned(CInt()))
        env("foo") should be(CDouble())

        //parameters should be in scope
        env("param") should be(CDouble())
        env("param2") should be(CPointer(CFunction(Seq(CVoid()), CVoid())))
    }

    test("typedef synonyms") {
        val env = ast.defs.last.entry -> varEnv
        val typedefs = ast.defs.last.entry -> typedefEnv

        typedefs("myint") should be(CSigned(CInt()))
        typedefs("mystr") should be(CAnonymousStruct(List(("x", CDouble()))))
        typedefs("myunsign") should be(CUnsigned(CInt()))

        //typedef is not a declaration
        env.contains("myint") should be(false)
        env.contains("mystr") should be(false)

        env("myintvar") should be(CSigned(CInt()))
        env("mystrvar") should be(CPointer(CAnonymousStruct(List(("x", CDouble())))))
        env("mypairvar") should be(CStruct("pair"))

        //structure definitons should be recognized despite typedefs
        val structenv: StructEnv = ast.defs.last.entry -> structEnv
        structenv.contains("pair", false) should be(true)
        structenv.contains("mystr", false) should be(false)

    }


    test("typedefEnv cycle") {
        val ast = (getAST("""
            typedef int myint;
            typedef myint mymyint;
            mymyint inner;
            """))
        val typedefs = ast.defs.last.entry -> typedefEnv
        println(typedefs)
        //expect no exception due to cyclic dependencies anymore
    }

    test("enum environment and lookup") {
        val ast = (getAST("""
            enum Direction { North, South, East, West };
            enum Color { Red, Green, Blue };
            enum Direction d = South;
            enum Direction e = Red;
            enum Undef x = Red;
            enum Direction e = Undef;
            """))
        val env = ast.defs.last.entry -> varEnv
        val enumenv = ast.defs.last.entry -> enumEnv

        enumenv should contain key ("Direction")
        enumenv should contain key ("Color")
        enumenv should not contain key("Undef")

        env("North") should be(CSigned(CInt()))
        env("South") should be(CSigned(CInt()))
        env("Red") should be(CSigned(CInt()))
        env("Green") should be(CSigned(CInt()))
        env("d") should be(CSigned(CInt()))
        env("e") should be(CSigned(CInt()))
        //        env("x").sometimesUnknown should be(true) TODO
        env("Undef") should be(CUndefined())
    }

}