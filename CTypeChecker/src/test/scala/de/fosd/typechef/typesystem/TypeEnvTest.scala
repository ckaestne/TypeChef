package de.fosd.typechef.typesystem


import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.featureexpr.FeatureExprFactory.{False, True}
import de.fosd.typechef.parser.c._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSuite, Matchers}

@RunWith(classOf[JUnitRunner])
class TypeEnvTest extends FunSuite with Matchers with CTypeSystem with CEnvCache with CTypeCache with TestHelper {

    import de.fosd.typechef.typesystem.CType.makeCType

    val _l = OneT(CSigned(CLong()).toCType)
    val _i = OneT(CSigned(CInt()).toCType)
    val _ui = OneT(CUnsigned(CInt()).toCType)
    val x_i = Choice(fx, _i, OneT(CUndefined.toCType))

    private def compileCode(code: String) = {
        val ast = getAST(code)
        typecheckTranslationUnit(ast)
        ast
    }

    private def ast = (compileCode( """
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
            int end;
                                    """))

    val lastDecl = ast.defs.last.entry

    test("parse struct decl") {

        val env: StructEnv = lookupEnv(lastDecl).structEnv



        //struct should be in environement
        env.isComplete("account", false) should be(True)
        env.isComplete("account", true) should be(False) //not a union

        env.isComplete("uaccount", false) should be(False)
        env.isComplete("uaccount", true) should be(True) //a union

        env.isComplete("announcedStruct", false) should be(False) //announced structs should be in the environement, but empty
        env.getFieldsMerged("announcedStruct", false) should be('isEmpty)

        val accountStruct = env.getFieldsMerged("account", false)

        //should have field "firstname"
        accountStruct contains "first_name" should be(true)
        //should have correct type
        val firstname = accountStruct("first_name")
        val balance = accountStruct("balance")

        balance should be(OneT(CFloat().toCType))
        firstname should be(OneT(CPointer(CChar()).toCType))

        val envvar = lookupEnv(lastDecl).varEnv
//        println("test: " + envvar.getAstOrElse("i", null))

    }


    private def OneT(t: CType): One[CType] = One(t)

    test("variable environment") {
        val env = lookupEnv(lastDecl).varEnv

        env("foo") should be(_i)
        env("bar") should be(_i)
        env("a") should be(OneT(CPointer(CStruct("account"))))
        env("ua") should be(OneT(CPointer(CStruct("uaccount", true))))
        env("acc") should be(OneT(CStruct("account")))
        env("main") should be(OneT(CFunction(Seq(CDouble()), CVoid())))

        env("i") should be(OneT(CFunction(Seq(CDouble(), CPointer(CFunction(Seq(), CVoid()))), CSigned(CInt()))))
        env("inner") should be(OneT(CDouble()))
    }

    test("variable scoping") {
        t()
    }

    def t() {
        //finding but last statement in last functiondef
        val fundef = ast.defs.takeRight(3).head.entry.asInstanceOf[FunctionDef]
        val lastStmt = fundef.stmt.asInstanceOf[CompoundStatement].innerStatements.last.entry
        val env = lookupEnv(lastStmt).varEnv

//        println(env)

        env("inner") should be(_i)
        env("foo") should be(OneT(CDouble()))

        //parameters should be in scope
        env("param") should be(OneT(CDouble()))
        env("param2") should be(OneT(CPointer(CFunction(Seq(), CVoid()))))

        //nested functions should be in scope
        env("square") should be(OneT(CFunction(List(CDouble()), CDouble())))
    }

    test("nested functions (lexical) scoping") {
        //finding last statement in nested function in last functiondef
        val fundef = ast.defs.takeRight(3).head.entry.asInstanceOf[FunctionDef]
        val nestedFundef = fundef.stmt.asInstanceOf[CompoundStatement].innerStatements.tail.head.entry.asInstanceOf[NestedFunctionDef]
        val stmt = nestedFundef.stmt.innerStatements.head.entry
        val env = lookupEnv(stmt).varEnv

        env("z") should be(OneT(CDouble()))
        env("inner") should be(_i)
    }


    test("typedef synonyms") {
        val env = lookupEnv(lastDecl).varEnv
        val typedefs = lookupEnv(lastDecl).typedefEnv

        typedefs("myint") should be(_i)
        typedefs("myunsign") should be(OneT(CUnsigned(CInt())))

        //typedef is not a declaration
        env.contains("myint") should be(false)
        env.contains("mystr") should be(false)

        env("myintvar") should be(_i)
        env("mypairvar") should be(OneT(CStruct("pair")))

        //structure definitons should be recognized despite typedefs
        val structenv: StructEnv = lookupEnv(ast.defs.last.entry).structEnv
        structenv.isComplete("pair", false) should be(True)
        structenv.isComplete("mystr", false) should be(False)

        typedefs("transunion") should be(OneT(CIgnore()))
    }


    test("typedefEnv cycle") {
        val ast = (compileCode( """
            typedef int myint;
            typedef myint mymyint;
            mymyint inner;
                                """))
        val typedefs = lookupEnv(ast.defs.last.entry).typedefEnv
//        println(typedefs)
        //expect no exception due to cyclic dependencies anymore
    }

    test("enum environment and lookup") {
        val ast = (compileCode( """
            enum Direction { North, South, East, West };
            enum Color { Red, Green, Blue };
            enum Direction d = South;
            enum Direction e = Red;
            enum Undef x = Red;
            enum Direction e = Undef;
                                """))
        val env = lookupEnv(ast.defs.last.entry).varEnv
        val enumenv = lookupEnv(ast.defs.last.entry).enumEnv

        enumenv should contain key ("Direction")
        enumenv should contain key ("Color")
        enumenv should not contain key("Undef")

        env("North") should be(_i)
        env("South") should be(_i)
        env("Red") should be(_i)
        env("Green") should be(_i)
        env("d") should be(_ui)
        env("e") should be(_ui)
        //        env("x").sometimesUnknown should be(OneT(true) TODO
        env("Undef") should be(OneT(CUndefined))
    }

    test("anonymous struct and typedef") {
        val ast = (compileCode( """
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
        val env = lookupEnv(fundef.stmt.asInstanceOf[CompoundStatement].innerStatements.last.entry).varEnv
//        println(fundef.stmt.asInstanceOf[CompoundStatement].innerStatements)
//        println(env)
        env("v") match {
            case One(CType(CPointer(CAnonymousStruct(_, _, _)), _, _, _)) =>
            case e => fail(e.toString)
        }
    }

    test("anonymous structs nested") {
        //unnamed fields of struct or union type are inlined (and should be checked for name clashes)
        //see http://gcc.gnu.org/onlinedocs/gcc/Unnamed-Fields.html#Unnamed-Fields
        val ast = compileCode( """
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
        val env = lookupEnv(ast.defs.last.entry).varEnv
//        println(env)
        env("foo") match {
            case One(CType(CAnonymousStruct(_, members, false), _, _, _)) =>
                members("b3") should be(_i)
                members("b1") should be(_i)
                members("b2") should be(_i)
                members("f1") should be(OneT(CFloat()))
                members("i1") should be(_i)
            //                members("a1") should be(CDouble()) //TODO, not implemented yet
            //                members("a2") should be(CDouble())
            case e => fail(e.toString)
        }
    }

    test("nested anonymous structs nested") {
        //see above
        val ast = compileCode( """struct {
                                 |  int a;
                                 |  union {
                                 |    struct {
                                 |      int b;
                                 |    };
                                 |    struct {
                                 |      int c;
                                 |      int d;
                                 |    };
                                 |  };
                                 |} a;
                                 |
                                 |int x;
                               """.stripMargin)
        val env = lookupEnv(ast.defs.last.entry).varEnv
//        println(env)
        env("a") match {
            case One(CType(CAnonymousStruct(_, members, false), _, _, _)) =>
                members("a") should be(_i)
                members("b") should be(_i)
                members("c") should be(_i)
                members("d") should be(_i)
            case e => fail(e.toString)
        }

        val ast2 = compileCode( """struct xx {
                                  |  int a;
                                  |  union {
                                  |    struct {
                                  |      int b;
                                  |    };
                                  |    struct {
                                  |      int c;
                                  |      int d;
                                  |    };
                                  |  };
                                  |} a;
                                  |
                                  |int x;
                                """.stripMargin)
        val env2 = lookupEnv(ast2.defs.last.entry).structEnv
//        println(env2)
        val fields = env2.getFieldsMerged("xx", false)
        fields("a") should be(_i)
        fields("b") should be(_i)
        fields("c") should be(_i)
    }

    test("anonymous union vs struct") {
        //a special hell for developers using this
        val ast2 = compileCode( """struct xx {
                                  |  int a;
                                  |#ifdef X
                                  |  union {
                                  |#else
                                  |  struct {
                                  |#endif
                                  |    int c;
                                  |    int d;
                                  |  };
                                  |} a;
                                  |
                                  |int x;
                                """.stripMargin)
        val env2 = lookupEnv(ast2.defs.last.entry).structEnv
//        println(env2)
        val fields = env2.getFieldsMerged("xx", false)
        fields("a") should be(_i)
        fields("c") should be(_i)
    }

    test("typedef environment") {
        val ast = (compileCode( """
            typedef struct {
                long counter;
            } a;
            typedef a b;

            void foo() {}
                                """))
        val fundef = ast.defs.last.entry.asInstanceOf[FunctionDef]

        val tdenv = lookupEnv(fundef).typedefEnv

//        println(tdenv)

        assert(wellformedC(null, null, tdenv("a")))
        assert(wellformedC(null, null, tdenv("b")))
    }

    test("conditional typedef environment") {
        val ast = (compileCode( """
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

        val tdenv = lookupEnv(fundef).typedefEnv
        val env = lookupEnv(fundef).varEnv

//        println("tdenv: " + tdenv)

        env("v") should be(Choice(fx.not(), _l, _i))
        env("w") should equal(env("v"))
    }


    test("conditional enum environment") {
        val ast = (compileCode( """
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
            int end;
                                """))
        val env = lookupEnv(ast.defs.last.entry).varEnv
        val enumenv = lookupEnv(ast.defs.last.entry).enumEnv

        enumenv should contain key ("Direction")
        enumenv should contain key ("Color")
        enumenv should not contain key("Undef")

        enumenv("Direction") should be(True, Id("Direction"))
        enumenv("Color") should be((fy or fx), Id("Color"))

        env("South") should be(Choice(fx, _i, OneT(CUndefined)))
        env("North") should be(_i)
        env("East") should be(Choice(fx, _i, Choice(fy, _i, OneT(CUndefined))))
        env("West") should be(_i)
        env("Red") should be(Choice(fy, _i, OneT(CUndefined)))
        env("Blue") should be(Choice(fx, _i, Choice(fy, _i, OneT(CUndefined))))
    }

    test("enum forward declaration") {
        val ast = (compileCode( """
                    enum Direction;
                    int end;
                                """))
        val enumenv = lookupEnv(ast.defs.last.entry).enumEnv

        enumenv should contain key ("Direction")
    }


    test("conditional structs") {
        val ast = (compileCode( """
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

//        println(ast)

        val structenv: StructEnv = lookupEnv(ast.defs.last.entry).structEnv

//        println(structenv)

        structenv.isComplete("s1", false) should be(True)
        structenv.isComplete("s2", false) should be(fx)
        structenv.isComplete("s3", false) should be(fx or fy)

        val structS1 = structenv.getFieldsMerged("s1", false)
        structS1("b") should be(_i)
        structS1("a") should be(x_i)
        structS1("c") should be(Choice(fx.not(), _l, _i))
        structS1("d") should be(Choice(fx.not and fy, _l, Choice(fx, _i, OneT(CUndefined))))

        val structS2 = structenv.getFieldsMerged("s2", false)
        structS2("a") should be(Choice(fx, _i, OneT(CUndefined)))
        structS2("b") should be(Choice(fx and fy, _i, OneT(CUndefined)))

        val structS3 = structenv.getFieldsMerged("s3", false)
        ConditionalLib.equals(
            structS3("a"),
            Choice(fx, _l, OneT(CUndefined))) should be(true)
        ConditionalLib.equals(
            structS3("b"),
            Choice(fx, _l, Choice(fy, _i, OneT(CUndefined)))) should be(true)


        val venv = lookupEnv(ast.defs.last.entry).varEnv


        venv("vs1") should be(OneT(CStruct("s1", false)))
        venv("vvs1") should be(OneT(CStruct("s1", false)))
        venv("vs2") should be(Choice(fx, OneT(CStruct("s2", false)), OneT(CUndefined)))
        venv("vvs2") should be(Choice(fx, OneT(CStruct("s2", false)), OneT(CUndefined)))
        ConditionalLib.equals(
            venv("vs3"),
            Choice(fx or fy, OneT(CStruct("s3", false)), OneT(CUndefined))) should be(true)
        ConditionalLib.equals(
            venv("vvs3"),
            Choice(fx or fy, OneT(CStruct("s3", false)), OneT(CUndefined))) should be(true)


        //anonymous struct
        venv("vs4") match {
            case One(CType(CAnonymousStruct(_, fields, false), _, _, _)) =>
                fields("a") should be(_i)
                fields("b") should be(Choice(fy, _i, OneT(CUndefined)))
            case e => fail("vs4 illtyped: " + e)
        }

    }

    test("conditional variable environment") {
        val ast = (compileCode( """

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
               int localend;
            }
            long l;
            int end;
                                """))



        val venv = lookupEnv(ast.defs.last.entry).varEnv

        val fundef = ast.defs.takeRight(3).head.entry.asInstanceOf[FunctionDef]
        val fenv = lookupEnv(fundef.stmt.asInstanceOf[CompoundStatement].innerStatements.last.entry).varEnv

        venv("l") should be(_l)
        fenv("l") should be(_i)
        fenv("a") should be(_i)
        fenv("b") should be(x_i)
        fenv("c") should be(Choice(fx, _i, _l))
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
        val ast = compileCode( """
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

        val env = lookupEnv(exprStmt.expr).varEnv
        env("m") should be(OneT(CPointer(CStruct("mtab_list", false))))

        val senv = lookupEnv(exprStmt.expr).structEnv
        senv.getFieldsMerged("mtab_list", false) should not be (null)
//        println(senv.getFieldsMerged("mtab_list", false))

//        println(exprStmt)
        val et = lookupExprType(exprStmt.expr)
//        println(et)
        et should be(OneT(CPointer(CSignUnspecified(CChar())).toCType.toObj))

    }


    test("nested structs") {
        //unnamed fields of struct or union type are inlined (and should be checked for name clashes)
        //see http://gcc.gnu.org/onlinedocs/gcc/Unnamed-Fields.html#Unnamed-Fields
        val ast = compileCode( """
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

        val env = lookupEnv(exprStmt.expr).varEnv
        env("m") should be(OneT(CPointer(CStruct("volume_descriptor", false))))

        val senv = lookupEnv(exprStmt.expr).structEnv
        senv.getFieldsMerged("volume_descriptor", false) should not be (null)
//        println(senv.getFieldsMerged("volume_descriptor", false))
        senv.getFieldsMerged("descriptor_tag", false) should not be (null)
//        println(senv.getFieldsMerged("descriptor_tag", false))
        senv.getFieldsMerged("X", false) should not be (null)
//        println(senv.getFieldsMerged("X", false))
        senv.getFieldsMerged("Y", false) should not be (null)
//        println(senv.getFieldsMerged("Y", false))

//        println(exprStmt)
        val et = lookupExprType(exprStmt.expr)
//        println(et)
        et should be(OneT(CFloat().toCType.toObj))

    }

    test("test __mode__ attribute") {
        //for now, no support for __mode__ and set to ignore to avoid false negatives
        val ast = compileCode( """
                typedef unsigned int a __attribute__ ((__mode__ (__QI__)));
                a b;
                               """)
        val env = lookupEnv(ast.defs.last.entry).typedefEnv
        env("a") should be(OneT(CIgnore()))
    }

    test("scope of enum in struct") {
        //did not find a proper specification, but cf
        //http://forums.whirlpool.net.au/archive/1689677
        val ast = compileCode( """
            struct lzma2_dec {
                enum lzma2_seq {
                        SEQ_CONTROL,
                        SEQ_COPY
                } sequence;
                enum lzma2_seq next_sequence;
                int uncompressed;
            };
            int a=SEQ_COPY;
                               """)
        val last = lookupEnv(ast.defs.last.entry)
        last.varEnv("uncompressed") should be(OneT(CUnknown()))
        last.varEnv("SEQ_COPY") should be(_i)

        last.enumEnv("lzma2_seq") should be(FeatureExprFactory.True, Id("lzma2_seq"))
    }

    test("joergs defuse example") {
        //did not find a proper specification, but cf
        //http://forums.whirlpool.net.au/archive/1689677
        val ast = compileCode( """
              int a;
              void foo();
              void foo() { }
              void bar() { foo(); }
                               """)
        val last = lookupEnv(ast.defs.last.entry)
        last.varEnv("a") should be(_i)
    }


}