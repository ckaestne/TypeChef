package de.fosd.typechef.typesystem

import _root_.de.fosd.typechef.conditional._
import _root_.de.fosd.typechef.parser.c._
import _root_.de.fosd.typechef.featureexpr.FeatureExprFactory._
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

/**
 * structs are complicated:
 *
 * a tag gives a name to a struct. In "struct x", x is a tag for a struct. (definition)
 *
 * incomplete structs don't have a body, complete structs do. (definition)
 *
 * ignoring qualified types (const etc) of structs and members. (impl. decision)
 *
 * members of struct shall not contain a member with incomplete or function type (pointers to those are okay though) -- tested below
 *
 * the presence of a struct-declaration-list in a struct-or-union-specifier declares a new type, within a translation unit -- tested below
 *
 * the struct type is incomplete until after the } that terminates the list.   -- tested below
 *
 * if "struct x" occurs in a declaration without a declarator, it declares tag x with an incomplete struct type; redeclarations
 * do not change the type -- tested below
 *
 * structs may be redeclared in different scopes -- tested below
 *
 * A specific type shall have its content defined at most once.
 *
 *
 *
 */

class StructTest extends FunSuite with CEnv with ShouldMatchers with TestHelper {

  private def check(code: String, printAST: Boolean = false): Boolean = {
    println("checking " + code);
    if (printAST) println("AST: " + getAST(code));
    check(getAST(code));
  }

  private def check(ast: TranslationUnit): Boolean = {
    assert(ast != null, "void ast");
    new CTypeSystemFrontend(ast).checkAST
  }

  test("StructEnv behavior") {
    var env = new StructEnv()
    env.isComplete("a", true) should be(False)

    //struct a;
    env = env.addIncomplete(Id("a"), true, True, 1)
    env.isComplete("a", true) should be(False)

    //struct a {double x;} //same scope should make it complete
    env = env.addComplete(Id("a"), true, True, new ConditionalTypeMap() +("x", True, null, One(CDouble())), 1)
    env.isComplete("a", true) should be(True)

    //struct a; // in same scope should not affect result
    env = env.addIncomplete(Id("a"), true, True, 1)
    env.isComplete("a", true) should be(True)

    //struct a; // in higher scope should replace
    env = env.addIncomplete(Id("a"), true, fa, 2)
    env.isComplete("a", true) should be(fa.not)

    //struct a{double x; double y;};
    env = env.addComplete(Id("a"), true, fa, new ConditionalTypeMap() +("x", True, null, One(CDouble())) +("y", True, null, One(CDouble())), 2)
    env.isComplete("a", true) should be(True)

    val fields = env.getFieldsMerged("a", true)
    fields.whenDefined("x") should be(True)
    fields.whenDefined("y") should be(fa)
  }

  test("members of struct shall not contain a member with incomplete or function type") {
    expect(true) {
      check( """
               |struct a {
               |  int b;
               |};
             """.stripMargin)
    }
    expect(false) {
      check( """
               |struct a {
               |  struct b x;
               |};
             """.stripMargin)
    }
    expect(false) {
      check( """
               |struct a {
               |  struct a x;
               |};
             """.stripMargin)
    }
    expect(true) {
      check( """
               |struct a {
               |  struct b * x;
               |};
             """.stripMargin)
    }
    expect(false) {
      check( """
               |struct a {
               |  int x(int);
               |};
             """.stripMargin)
    }
    expect(false) {
      check( """
               |struct a {
               |  int x(int b);
               |};
             """.stripMargin)
    }
    expect(true) {
      check( """
               |struct a {
               |  int (*x)(int);
               |};
             """.stripMargin)
    }
    expect(true) {
      check( """
               |typedef struct a {
               |  char (*x)(struct x *);
               |};
             """.stripMargin)
    }
  }

  test("recursive structs") {
    expect(true) {
      check( """
               |struct tnode {
               |  int count;
               |  struct tnode *left, *right;
               |};
               |struct tnode s;
               |struct tnode *sp;
             """.stripMargin)
    }
    expect(false) {
      check( """
               |struct tnode {
               |  int count;
               |  struct tnode left, *right;
               |};
               |struct tnode s;
               |struct tnode *sp;
             """.stripMargin)
    }



    expect(true) {
      check( """
               |typedef struct tnode TNODE;
               |struct tnode {
               |  int count;
               |  TNODE *left, *right;
               |};
               |TNODE s, *sp;
             """.stripMargin)
    }
    expect(true) {
      check( """
               |struct s1 { struct s2 *s2p; /* ... */ }; // D1
               |struct s2 { struct s1 *s1p; /* ... */ }; // D2
             """.stripMargin)
    }
  }


  ignore("struct scoping and redeclaration") {
    //the scoping rules are more complicated than I'm currently willing to implement right now.
    //structs can be different in different scopes, in a declaration we need to remember which struct declaration
    //we referenced. however incomplete structs can still be changed later in the scope.

    expect(true) {
      check( """
               |void foo(){
               |  struct s1 { int a; };
               |  {
               |      struct s2 { struct s1 *x; }; //refers to {int a}
               |      struct s1 { int b; };
               |  }
               |}
             """.stripMargin
      )
    }
    expect(true) {
      check(
        """
          |void foo() {
          |        struct s1 { int a; };
          |        {
          |                struct s2 { struct s1 *x; };
          |                struct s1 { int b; };
          |                struct s2 n;
          |                int i=n.x->a;
          |        }
          |}
        """.stripMargin)
    }
    expect(false) {
      check(
        """
          |void foo() {
          |        struct s1 { int a; };
          |        {
          |                struct s2 { struct s1 *x; };
          |                struct s1 { int b; };
          |                struct s2 n;
          |                int i=n.x->b;
          |        }
          |}
        """.stripMargin)
    }

    expect(true) {
      check( """
               |void foo(){
               |  struct s1 { int a; };
               |  {
               |      struct s1; // introduces a new struct in an inner scope
               |      struct s2 { struct s1 *x; }; //refers to {int b}
               |      struct s1 { int b; };
               |  }
               |}
             """.stripMargin)
    }
    expect(true) {
      check(
        """
          |void foo() {
          |        struct s1 { int a; };
          |        {
          |                struct s1;
          |                struct s2 { struct s1 *x; };
          |                struct s1 { int b; };
          |                struct s2 n;
          |                int i=n.x->b;
          |        }
          |}
        """.stripMargin)
    }
    expect(false) {
      check(
        """
          |void foo() {
          |        struct s1 { int a; };
          |        {
          |                struct s1;
          |                struct s2 { struct s1 *x; };
          |                struct s1 { int b; };
          |                struct s2 n;
          |                int i=n.x->a;
          |        }
          |}
        """.stripMargin)
    }
  }

  test("inner structs escape") {
    expect(true) {
      check(
        """
          |struct { int a; struct b { int x; } bb; } c;
          |struct b d;
        """.stripMargin)
    }

  }

  test("deref pointers to incomplete structs") {
    expect(false) {
      check( """
               |struct s1 *x;
               |void foo() {
               |  int a=*x;
               |}
             """.stripMargin)
    }

    expect(true) {
      check( """
               |void foo() {
               |        struct x *a;
               |        struct x { int b; };
               |        int i;
               |        i=a->b;
               |}
               |
             """.stripMargin)
    }
    expect(false) {
      check( """
               |void foo() {
               |        struct x *a;
               |        int i;
               |        i=a->b;//dereferencing to incomplete type
               |}
               |
             """.stripMargin)
    }
  }

  test("struct scopes") {
    expect(true) {
      check( """
               | void foo(){
               |        struct x{};
               |        struct x a;
               |        {
               |            struct x b;
               |        }
               |}
             """.stripMargin)
    }
    expect(true) {
      check( """
               | void foo(){
               |        struct x{};
               |        struct x a;
               |        {
               |            struct x {int i;};
               |            struct x b;
               |        }
               |}
             """.stripMargin)
    }
    expect(false) {
      check( """
               | void foo(){
               |        struct x{};
               |        struct x a;
               |        {
               |            struct x;
               |            struct x b;
               |        }
               |}
             """.stripMargin)
    }
  }

  test("incomplete structs in signatures") {
    expect(true) {
      check( """struct x {}; struct x foo() {  }""")
    }
    expect(false) {
      check( """struct x foo() { }""")
    }
    expect(false) {
      check( """void foo(struct x b) { }""")
    }
  }

  test("inner structs") {
    expect(true) {
      check( """
               |struct x {
               |   struct z { int a; } c;
               |   struct z b;
               |} y;
             """.stripMargin)
    }
    expect(false) {
      check( """
               |void foo() {
               |        int a;
               |        {
               |                struct x {};
               |                struct x a;
               |        }
               |        struct x b;//struct x is incomplete
               |}
             """.stripMargin)
    }

  }

  test("forward declaration struct from sched.c") {
    /**
     * top level declarations can be declared with an incomplete type if the type is completed eventually
     * (in contrast declarations inside functions and function signatures must immediately contain complete types)
     */
    expect(true) {
      check("static __attribute__((section(\".data\" \"\"))) struct rt_rq per_cpu__init_rt_rq_var ;" +
        "struct rt_rq {};")
    }
    expect(true) {
      check("static __attribute__((section(\".data\" \"\"))) __typeof__(struct rt_rq) per_cpu__init_rt_rq_var ;" +
        "struct rt_rq {};")
    }
    expect(true) {
      check("struct x a;" +
        "struct x {};")
    }
    expect(false) {
      check("struct x a;" +
        "void foo() { a; }" +
        "struct x {};")
    }

  }



  test("structure types without variability") {
    expect(true) {
      check("struct s;") //forward declaration
    }
    expect(false) {
      check("struct s x;") //no forward declaration
    }
    expect(true) {
      check("struct s {} x;") //empty struct declaration
    }
    expect(true) {
      check("struct s {int a;};\n" +
        "void foo(){struct s b;}")
    }
    expect(false) {
      check("struct s { struct t x; };") // t is not a struct, check x like variable declaration
    }
    expect(true) {
      check("struct s foo();")
    }
    expect(true) {
      check("struct s { char x;} a[3];")
    }
    expect(true) {
      check("struct r{ struct s { char x;} a[3]; };")
    }
    expect(true) {
      check("struct s {int a;};\n" +
        "void foo(){struct c {struct s x;} b;}")
    }
    expect(false) {
      check("struct s foo(){}\n" +
        "void bar() { foo(); }")
    }
    expect(false) {
      check("struct s bar() { }")
    }
    expect(false) {
      check("void bar(struct c x) { }")
    }
    expect(true) {
      check("struct s {int a;};\n" +
        "struct s foo(){}\n" +
        "void bar() { foo(); }")
    }
    expect(false) {
      check("void foo(){struct {int a; struct x b;} b;}")
    }
    expect(false) {
      check("struct s {int a; struct x b;};\n" +
        "void foo(){struct s b;}")
    }
    expect(true) {
      check("extern struct s b;")
    }
    expect(false) {
      check("extern struct s b;\n" +
        "void foo() { b; }")
    }
  }
  test("structure types with variability") {
    expect(false) {
      check("#ifdef X\n" +
        "struct s {int a;};\n" +
        "#endif\n" +
        "void foo(){struct s b;}")
    }
    expect(false) {
      check("#ifdef X\n" +
        "struct s {int a;};\n" +
        "#endif\n" +
        "void foo(){struct c {struct s x;} b;}")
    }
    expect(false) {
      check("#ifdef X\n" +
        "struct s {int a;};\n" +
        "#endif\n" +
        "struct s foo(){}\n" +
        "void bar() { foo(); }")
    }

  }


  test("alternative struct declaration") {
    expect(true) {
      check( """
#if defined( X)
typedef unsigned long int stat_cnt_t;
typedef struct reiserfs_proc_info_data {        int a; } reiserfs_proc_info_data_t;
#else
typedef struct reiserfs_proc_info_data {} reiserfs_proc_info_data_t;
#endif
struct reiserfs_sb_info {
    int b;
    reiserfs_proc_info_data_t s_proc_info_data;
};
             """)

    }
  }

  test("recursive structures") {
    expect(true) {
      check( """
                     struct mtab_list {
                		char *dir;
                		char *device;
                		struct mtab_list *next;
                	} *mtl, *m;
             """)
    }
    expect(true) {
      check( """
         void foo(){
             struct mtab_list {
        		char *dir;
        		char *device;
        		struct mtab_list *next;
        	} *mtl, *m;
         }""")
    }
    expect(true) {
      check( """
            #ifdef X
                 struct x { int b;};
            #endif
                 struct y {
                    int a;
            #ifdef X
                    struct x d;
            #endif
                    int e;
                 };
                 struct y test(){}
             """)
    }
  }

  ignore("A specific type shall have its content defined at most once") {
    expect(true) {
      check(
        """
          |typedef union
          |{
          |  struct y
          |  {
          |    int z;
          |  } yy;
          |} xx;
        """.stripMargin)
    }
    expect(true) {
      check(
        """
          |struct x;
          |struct x { };
          |struct x;
        """.stripMargin)
    }
    expect(false) {
      check(
        """
          |struct x { };
          |struct x { };
        """.stripMargin)
    }
    expect(true) {
      check(
        """
          |#ifdef X
          |struct x { };
          |#else
          |struct x { };
          |#endif
        """.stripMargin)
    }

  }

  test("extern structs") {
    expect(true) {
      check("extern struct x a;".stripMargin)
    }
    expect(false) {
      check("struct x a;".stripMargin) // error: storage size of ‘a’ isn’t known
    }
    expect(true) {
      check("extern struct x a; void foo() { &a; }".stripMargin) // valid in gcc, do not recheck at pointer creation
    }
    expect(false) {
      check("extern struct x a; void foo() { a; }".stripMargin) // valid in gcc, do not recheck at pointer creation
    }
    expect(false) {
      check("extern struct x a; void bar(struct x b){} void foo() { bar(a); }".stripMargin) // valid in gcc, do not recheck at pointer creation
    }


  }

}
