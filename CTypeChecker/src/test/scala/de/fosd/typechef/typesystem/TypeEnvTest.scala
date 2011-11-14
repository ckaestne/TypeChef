package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.featureexpr.FeatureExpr.base
import de.fosd.typechef.featureexpr.FeatureExpr.dead
import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional._

@RunWith(classOf[JUnitRunner])
class TypeEnvTest extends FunSuite with ShouldMatchers with CTypeAnalysis with TestHelper {

    val _l = One(CSigned(CLong()))
    val _i = One(CSigned(CInt()))
    val x_i = Choice(fx, _i, One(CUndefined()))


    private def ast = (getAST("""
            typedef int myint;
            typedef struct { double x; } mystr;
            typedef struct pair { double x,y; } mypair;
            typedef unsigned myunsign;
            typedef union { double a;} transunion __attribute__ ((__transparent_union__));
            myint myintvar;
            mystr *mystrvar;
            mypair mypairvar;
            struct announcedStruct;
            transunion _transunion;

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
            int i(double param, void (*param2)(void)) {
                int inner;
                double square (double z) { return z * z; } //nested function
                double foo=0;
                return foo;
            }
            double inner;
            """))

    test("parse struct decl") {

        val env: StructEnv = ast.defs.last.entry -> structEnv

        println(env.env)

        //struct should be in environement
        env.isDefined("account", false) should be(base)
        env.isDefined("account", true) should be(dead) //not a union

        env.isDefined("uaccount", false) should be(dead)
        env.isDefined("uaccount", true) should be(base) //a union

        env.isDefined("announcedStruct", false) should be(base) //announced structs should be in the environement, but empty
        env.get("announcedStruct", false) should be('isEmpty)

        val accountStruct = env.get("account", false)

        //should have field "firstname"
        accountStruct contains "first_name" should be(true)
        //should have correct type
        val firstname = accountStruct("first_name")
        val balance = accountStruct("balance")

        balance should be(One(CFloat()))
        firstname should be(One(CPointer(CChar())))

    }

    test("variable environment") {
        val env = ast.defs.last.entry -> varEnv

        env("foo") should be(_i)
        env("bar") should be(_i)
        env("a") should be(One(CPointer(CStruct("account"))))
        env("ua") should be(One(CPointer(CStruct("uaccount", true))))
        env("acc") should be(One(CStruct("account")))
        env("main") should be(One(CFunction(Seq(CDouble()), CVoid())))

        env("i") should be(One(CFunction(Seq(CDouble(), CPointer(CFunction(Seq(CVoid()), CVoid()))), CSigned(CInt()))))
        env("inner") should be(One(CDouble()))
    }

    test("variable scoping") {t()}
    def t() {
        //finding but last statement in last functiondef
        val fundef = ast.defs.takeRight(2).head.entry.asInstanceOf[FunctionDef]
        val env = fundef.stmt.asInstanceOf[CompoundStatement].innerStatements.last.entry -> varEnv

        println(env)

        env("inner") should be(_i)
        env("foo") should be(One(CDouble()))

        //parameters should be in scope
        env("param") should be(One(CDouble()))
        env("param2") should be(One(CPointer(CFunction(Seq(CVoid()), CVoid()))))

        //nested functions should be in scope
        env("square") should be(One(CFunction(List(CDouble()), CDouble())))
    }

    test("nested functions (lexical) scoping") {
        //finding last statement in nested function in last functiondef
        val fundef = ast.defs.takeRight(2).head.entry.asInstanceOf[FunctionDef]
        val nestedFundef = fundef.stmt.asInstanceOf[CompoundStatement].innerStatements.tail.head.entry.asInstanceOf[NestedFunctionDef]
        val stmt = nestedFundef.stmt.innerStatements.head.entry
        val env = stmt -> varEnv

        env("z") should be(One(CDouble()))
        env("inner") should be(_i)
    }


    test("typedef synonyms") {
        val env = ast.defs.last.entry -> varEnv
        val typedefs = ast.defs.last.entry -> typedefEnv

        typedefs("myint") should be(_i)
        typedefs("mystr") should be(One(CAnonymousStruct(new ConditionalTypeMap() + ("x", base, One(CDouble())))))
        typedefs("myunsign") should be(One(CUnsigned(CInt())))

        //typedef is not a declaration
        env.contains("myint") should be(false)
        env.contains("mystr") should be(false)

        env("myintvar") should be(_i)
        env("mystrvar") should be(One(CPointer(CAnonymousStruct(new ConditionalTypeMap() + ("x", base, One(CDouble()))))))
        env("mypairvar") should be(One(CStruct("pair")))

        //structure definitons should be recognized despite typedefs
        val structenv: StructEnv = ast.defs.last.entry -> structEnv
        structenv.isDefined("pair", false) should be(base)
        structenv.isDefined("mystr", false) should be(dead)

        typedefs("transunion") should be(One(CIgnore()))
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

        env("North") should be(_i)
        env("South") should be(_i)
        env("Red") should be(_i)
        env("Green") should be(_i)
        env("d") should be(_i)
        env("e") should be(_i)
        //        env("x").sometimesUnknown should be(One(true) TODO
        env("Undef") should be(One(CUndefined()))
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
                members("b3") should be(_i)
                members("b1") should be(_i)
                members("b2") should be(_i)
                members("f1") should be(One(CFloat()))
                members("i1") should be(_i)
            //                members("a1") should be(CDouble()) //TODO, not implemented yet
            //                members("a2") should be(CDouble())
            case e => fail(e.toString)
        }
    }


    test("typedef environment") {
        val ast = (getAST("""
            typedef struct {
                long counter;
            } a;
            typedef a b;

            void foo() {}
        """))
        val fundef = ast.defs.last.entry.asInstanceOf[FunctionDef]

        val tdenv = fundef -> typedefEnv

        println(tdenv)

        assert(wellformed(null, null, tdenv("a")))
        assert(wellformed(null, null, tdenv("b")))
    }

    test("conditional typedef environment") {
        val ast = (getAST("""
        #ifdef X
            typedef int a;
        #else
            typedef long a;
        #endif
            typedef a b;

            a v;
            b w;

            void foo() {}
        """))
        val fundef = ast.defs.last.entry.asInstanceOf[FunctionDef]

        val tdenv = fundef -> typedefEnv
        val env = fundef -> varEnv

        println("tdenv: " + tdenv)

        env("v") should be(Choice(fx.not, _l, _i))
        env("w") should equal(env("v"))
    }


    test("conditional enum environment") {
        val ast = (getAST("""
            enum Direction {
                #ifdef X
                    South,
                #endif
                #ifdef X
                    North,
                #else
                    North,
                #endif
                #ifdef X
                    East,
                #endif
                #ifdef Y
                    East,
                #endif
                West };
            #ifdef Y
            enum Color { Red, Green, Blue };
            #endif
            #ifdef X
            enum Color { Blue };
            #endif
            """))
        val env = ast.defs.last.entry -> varEnv
        val enumenv = ast.defs.last.entry -> enumEnv

        enumenv should contain key ("Direction")
        enumenv should contain key ("Color")
        enumenv should not contain key("Undef")

        enumenv("Direction") should be(base)
        enumenv("Color") should be(fy or fx)

        env("South") should be(Choice(fx, _i, One(CUndefined())))
        env("North") should be(_i)
        env("East") should be(Choice(fx, _i, Choice(fy, _i, One(CUndefined()))))
        env("West") should be(_i)
        env("Red") should be(Choice(fy, _i, One(CUndefined())))
        env("Blue") should be(Choice(fx, _i, Choice(fy, _i, One(CUndefined()))))
    }


    test("conditional structs") {
        val ast = (getAST("""
            struct s1 {
                #ifdef X
                int a;
                #endif
                int b;
                #ifdef X
                int c;
                #else
                long c;
                #endif
                #ifdef X
                int d;
                #elif defined(Y)
                long d;
                #endif
            } vs1;
            #ifdef X
            struct s2 {
                int a;
                #ifdef Y
                int b;
                #endif
                int c;
            } vs2;
            #endif
            ;
            #ifdef X
            struct s3 { long a; long b; } vs3;
            #elif defined Y
            struct s3 { int b; } vs3;
            #endif
            ;
            struct s1 vvs1;
            #ifdef X
            struct s2 vvs2;
            #endif
            #if defined(X) || defined(Y)
            struct s3 vvs3;
            #endif
            struct {
                int a;
                #ifdef Y
                int b;
                #endif
                int c;
            } vs4;
            void foo() {}
        """))

        println(ast)

        val structenv: StructEnv = ast.defs.last.entry -> structEnv

        println(structenv)

        structenv.isDefined("s1", false) should be(base)
        structenv.isDefined("s2", false) should be(fx)
        structenv.isDefined("s3", false) should be(fx or fy)

        val structS1 = structenv.get("s1", false)
        structS1("b") should be(_i)
        structS1("a") should be(x_i)
        structS1("c") should be(Choice(fx.not, _l, _i))
        structS1("d") should be(Choice(fx.not and fy, _l, Choice(fx, _i, One(CUndefined()))))

        val structS2 = structenv.get("s2", false)
        structS2("a") should be(Choice(fx, _i, One(CUndefined())))
        structS2("b") should be(Choice(fx and fy, _i, One(CUndefined())))

        val structS3 = structenv.get("s3", false)
        ConditionalLib.equals(
            structS3("a"),
            Choice(fx, _l, One(CUndefined()))) should be(true)
        ConditionalLib.equals(
            structS3("b"),
            Choice(fx, _l, Choice(fy, _i, One(CUndefined())))) should be(true)


        val venv = ast.defs.last.entry -> varEnv


        venv("vs1") should be(One(CStruct("s1", false)))
        venv("vvs1") should be(One(CStruct("s1", false)))
        venv("vs2") should be(Choice(fx, One(CStruct("s2", false)), One(CUndefined())))
        venv("vvs2") should be(Choice(fx, One(CStruct("s2", false)), One(CUndefined())))
        ConditionalLib.equals(
            venv("vs3"),
            Choice(fx or fy, One(CStruct("s3", false)), One(CUndefined()))) should be(true)
        ConditionalLib.equals(
            venv("vvs3"),
            Choice(fx or fy, One(CStruct("s3", false)), One(CUndefined()))) should be(true)


        //anonymous struct
        venv("vs4") match {
            case One(CAnonymousStruct(fields, false)) =>
                fields("a") should be(_i)
                fields("b") should be(Choice(fy, _i, One(CUndefined())))
            case e => fail("vs4 illtyped: " + e)
        }

    }

    test("conditional variable environment") {
        val ast = (getAST("""

            int a;
            #ifdef X
            int b;
            #endif
            ;
            #ifdef X
            int c;
            #else
            long c;
            #endif
            int x;

            void foo(int p
                #ifdef X
                ,int q
                #endif
                ,int r) {
               int l;
               #ifdef X
               long x;
               long p; //TODO local redefinition of parameter
               #endif
            }
            long l;

        """))



        val venv = ast.defs.last.entry -> varEnv

        val fundef = ast.defs.takeRight(2).head.entry.asInstanceOf[FunctionDef]
        val fenv = fundef.stmt.asInstanceOf[CompoundStatement].innerStatements.last.entry -> varEnv

        venv("l") should be(_l)
        fenv("l") should be(_i)
        fenv("a") should be(_i)
        fenv("b") should be(x_i)
        fenv("c") should be(Choice(fx.not, _l, _i))
        venv("x") should be(_i)
        fenv("x") should be(Choice(fx, _l, _i))
        fenv("q") should be(x_i)
        fenv("p") should be(Choice(fx, _l, _i))
        fenv("r") should be(_i)


        //TODO check local redefinition of parameter for validity
    }


    test("recursive and local struct") {
        //unnamed fields of struct or union type are inlined (and should be checked for name clashes)
        //see http://gcc.gnu.org/onlinedocs/gcc/Unnamed-Fields.html#Unnamed-Fields
        val ast = getAST("""
            void foo() {
                struct mtab_list {
                    char *dir;
                    char *device;
                    struct mtab_list *next;
                } *mtl, *m;
                ;
                m->next->dir;
            }
          """)
        val fundef = ast.defs.last.entry.asInstanceOf[FunctionDef]
        val exprStmt = fundef.stmt.asInstanceOf[CompoundStatement].innerStatements.last.entry.asInstanceOf[ExprStatement]

        val env = exprStmt.expr -> varEnv
        env("m") should be(One(CPointer(CStruct("mtab_list", false))))

        val senv = exprStmt.expr -> structEnv
        senv.get("mtab_list", false) should not be (null)
        println(senv.get("mtab_list", false))

        println(exprStmt)
        val et = exprStmt.expr -> exprType
        println(et)
        et should be(One(CObj(CPointer(CSignUnspecified(CChar())))))

    }


    test("nested structs") {
        //unnamed fields of struct or union type are inlined (and should be checked for name clashes)
        //see http://gcc.gnu.org/onlinedocs/gcc/Unnamed-Fields.html#Unnamed-Fields
        val ast = getAST("""
            struct volume_descriptor {
	            struct descriptor_tag {
		            float	id;
		        } *tag;
		        union {
		            struct X {
		                float x;
		            } a;
		            struct Y {
		                float y;
		            } b;
		        } u;
		    } *m;

            void foo() {
                m->tag->id;
            }
          """)
        val fundef = ast.defs.last.entry.asInstanceOf[FunctionDef]
        val exprStmt = fundef.stmt.asInstanceOf[CompoundStatement].innerStatements.last.entry.asInstanceOf[ExprStatement]

        val env = exprStmt.expr -> varEnv
        env("m") should be(One(CPointer(CStruct("volume_descriptor", false))))

        val senv = exprStmt.expr -> structEnv
        senv.get("volume_descriptor", false) should not be (null)
        println(senv.get("volume_descriptor", false))
        senv.get("descriptor_tag", false) should not be (null)
        println(senv.get("descriptor_tag", false))
        senv.get("X", false) should not be (null)
        println(senv.get("X", false))
        senv.get("Y", false) should not be (null)
        println(senv.get("Y", false))

        println(exprStmt)
        val et = exprStmt.expr -> exprType
        println(et)
        et should be(One(CObj(CFloat())))

    }
    """"""
}