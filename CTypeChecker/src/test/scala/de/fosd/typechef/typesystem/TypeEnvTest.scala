package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.featureexpr.FeatureExpr.base
import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional._

@RunWith(classOf[JUnitRunner])
class TypeEnvTest extends FunSuite with ShouldMatchers with CTypeAnalysis with TestHelper {


    private def ast = (getAST("""
            typedef int myint;
            typedef struct { double x; } mystr;
            typedef struct pair { double x,y; } mypair;
            typedef unsigned myunsign;
            myint myintvar;
            mystr *mystrvar;
            mypair mypairvar;
            struct announcedStruct;

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

        env.contains("announcedStruct", false) should be(true) //announced structs should be in the environement, but empty
        env.get("announcedStruct", false) should be('isEmpty)

        val accountStruct = env.get("account", false)

        //should have field "firstname"
        accountStruct contains "first_name" should be(true)
        //should have correct type
        val firstname = accountStruct("first_name")
        val balance = accountStruct("balance")

        balance should be(CFloat())
        firstname should be(CPointer(CChar()))

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
        typedefs("mystr") should be(CAnonymousStruct(new ConditionalTypeMap() + ("x", base, One(CDouble()))))
        typedefs("myunsign") should be(CUnsigned(CInt()))

        //typedef is not a declaration
        env.contains("myint") should be(false)
        env.contains("mystr") should be(false)

        env("myintvar") should be(CSigned(CInt()))
        env("mystrvar") should be(CPointer(CAnonymousStruct(new ConditionalTypeMap() + ("x", base, One(CDouble())))))
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

    test("anonymous struct and typedef") {
        val ast = (getAST("""
            typedef struct {
             volatile long counter;
            } atomic64_t;
            typedef atomic64_t atomic_long_t;

            static inline __attribute__((always_inline)) long atomic_long_sub_return(long i, atomic_long_t *l)
            {
             atomic64_t *v = (atomic64_t *)l;
             return (long)atomic64_sub_return(i, v);
            }
        """))
        val fundef = ast.defs.last.entry.asInstanceOf[FunctionDef]
        val env = fundef.stmt.asInstanceOf[CompoundStatement].innerStatements.last.entry -> varEnv
        println(fundef.stmt.asInstanceOf[CompoundStatement].innerStatements)
        println(env)
        env("v") match {
            case One(CPointer(CAnonymousStruct(_, _))) =>
            case e => fail(e.toString)
        }
    }

    test("anonymous structs nested") {
        //unnamed fields of struct or union type are inlined (and should be checked for name clashes)
        //see http://gcc.gnu.org/onlinedocs/gcc/Unnamed-Fields.html#Unnamed-Fields
        val ast = getAST("""
          struct stra { double a1, a2; };
          struct {
            struct {
              int b1;
              int b2;
            };
            union {
              float f1;
              int i1;
            };
            struct stra;
            int b3;
          } foo = {{31, 17}, {3.2}, 13};

          int
          main ()
          {
            int b1 = foo.b1;
            int b3 = foo.b3;
            return 0;
          }""")
        val env = ast.defs.last.entry -> varEnv
        println(env)
        env("foo") match {
            case One(CAnonymousStruct(members, false)) =>
                members("b3") should be(CSigned(CInt()))
                members("b1") should be(CSigned(CInt()))
                members("b2") should be(CSigned(CInt()))
                members("f1") should be(CFloat())
                members("i1") should be(CSigned(CInt()))
            //                members("a1") should be(CDouble()) //TODO, not implemented yet
            //                members("a2") should be(CDouble())
            case e => fail(e.toString)
        }
    }

}