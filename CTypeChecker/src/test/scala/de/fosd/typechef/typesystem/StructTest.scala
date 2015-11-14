package de.fosd.typechef.typesystem

import _root_.de.fosd.typechef.conditional._
import _root_.de.fosd.typechef.featureexpr.FeatureExprFactory._
import _root_.de.fosd.typechef.parser.c._
import org.scalatest.{FunSuite, Matchers}

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

class StructTest extends FunSuite with CEnv with Matchers with TestHelperTS {



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
        assertResult(true) {
            check( """
                     |struct a {
                     |  int b;
                     |};
                   """.stripMargin)
        }
        assertResult(false) {
            check( """
                     |struct a {
                     |  struct b x;
                     |};
                   """.stripMargin)
        }
        assertResult(false) {
            check( """
                     |struct a {
                     |  struct a x;
                     |};
                   """.stripMargin)
        }
        assertResult(true) {
            check( """
                     |struct a {
                     |  struct b * x;
                     |};
                   """.stripMargin)
        }
        assertResult(false) {
            check( """
                     |struct a {
                     |  int x(int);
                     |};
                   """.stripMargin)
        }
        assertResult(false) {
            check( """
                     |struct a {
                     |  int x(int b);
                     |};
                   """.stripMargin)
        }
        assertResult(true) {
            check( """
                     |struct a {
                     |  int (*x)(int);
                     |};
                   """.stripMargin)
        }
        assertResult(true) {
            check( """
                     |typedef struct a {
                     |  char (*x)(struct x *);
                     |};
                   """.stripMargin)
        }
    }

    test("recursive structs") {
        assertResult(true) {
            check( """
                     |struct tnode {
                     |  int count;
                     |  struct tnode *left, *right;
                     |};
                     |struct tnode s;
                     |struct tnode *sp;
                   """.stripMargin)
        }
        assertResult(false) {
            check( """
                     |struct tnode {
                     |  int count;
                     |  struct tnode left, *right;
                     |};
                     |struct tnode s;
                     |struct tnode *sp;
                   """.stripMargin)
        }



        assertResult(true) {
            check( """
                     |typedef struct tnode TNODE;
                     |struct tnode {
                     |  int count;
                     |  TNODE *left, *right;
                     |};
                     |TNODE s, *sp;
                   """.stripMargin)
        }
        assertResult(true) {
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

        assertResult(true) {
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
        assertResult(true) {
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
        assertResult(false) {
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

        assertResult(true) {
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
        assertResult(true) {
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
        assertResult(false) {
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
        assertResult(true) {
            check(
                """
                  |struct { int a; struct b { int x; } bb; } c;
                  |struct b d;
                """.stripMargin)
        }

    }

    test("deref pointers to incomplete structs") {
        assertResult(false) {
            check( """
                     |struct s1 *x;
                     |void foo() {
                     |  int a=*x;
                     |}
                   """.stripMargin)
        }

        assertResult(true) {
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
        assertResult(false) {
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
        assertResult(true) {
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
        assertResult(true) {
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
        assertResult(false) {
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
        assertResult(true) {
            check( """struct x {}; struct x foo() {  }""")
        }
        assertResult(false) {
            check( """struct x foo() { }""")
        }
        assertResult(false) {
            check( """void foo(struct x b) { }""")
        }
    }

    test("inner structs") {
        assertResult(true) {
            check( """
                     |struct x {
                     |   struct z { int a; } c;
                     |   struct z b;
                     |} y;
                   """.stripMargin)
        }
        assertResult(false) {
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
        assertResult(true) {
            check("static __attribute__((section(\".data\" \"\"))) struct rt_rq per_cpu__init_rt_rq_var ;" +
                "struct rt_rq {};")
        }
        assertResult(true) {
            check("static __attribute__((section(\".data\" \"\"))) __typeof__(struct rt_rq) per_cpu__init_rt_rq_var ;" +
                "struct rt_rq {};")
        }
        assertResult(true) {
            check("struct x a;" +
                "struct x {};")
        }
        assertResult(false) {
            check("struct x a;" +
                "void foo() { a; }" +
                "struct x {};")
        }

    }



    test("structure types without variability") {
        assertResult(true) {
            check("struct s;") //forward declaration
        }
        assertResult(false) {
            check("struct s x;") //no forward declaration
        }
        assertResult(true) {
            check("struct s {} x;") //empty struct declaration
        }
        assertResult(true) {
            check("struct s {int a;};\n" +
                "void foo(){struct s b;}")
        }
        assertResult(false) {
            check("struct s { struct t x; };") // t is not a struct, check x like variable declaration
        }
        assertResult(true) {
            check("struct s foo();")
        }
        assertResult(true) {
            check("struct s { char x;} a[3];")
        }
        assertResult(true) {
            check("struct r{ struct s { char x;} a[3]; };")
        }
        assertResult(true) {
            check("struct s {int a;};\n" +
                "void foo(){struct c {struct s x;} b;}")
        }
        assertResult(false) {
            check("struct s foo(){}\n" +
                "void bar() { foo(); }")
        }
        assertResult(false) {
            check("struct s bar() { }")
        }
        assertResult(false) {
            check("void bar(struct c x) { }")
        }
        assertResult(true) {
            check("struct s {int a;};\n" +
                "struct s foo(){}\n" +
                "void bar() { foo(); }")
        }
        assertResult(false) {
            check("void foo(){struct {int a; struct x b;} b;}")
        }
        assertResult(false) {
            check("struct s {int a; struct x b;};\n" +
                "void foo(){struct s b;}")
        }
        assertResult(true) {
            check("extern struct s b;")
        }
        assertResult(false) {
            check("extern struct s b;\n" +
                "void foo() { b; }")
        }
    }
    test("structure types with variability") {
        assertResult(false) {
            check("#ifdef X\n" +
                "struct s {int a;};\n" +
                "#endif\n" +
                "void foo(){struct s b;}")
        }
        assertResult(false) {
            check("#ifdef X\n" +
                "struct s {int a;};\n" +
                "#endif\n" +
                "void foo(){struct c {struct s x;} b;}")
        }
        assertResult(false) {
            check("#ifdef X\n" +
                "struct s {int a;};\n" +
                "#endif\n" +
                "struct s foo(){}\n" +
                "void bar() { foo(); }")
        }

    }


    test("alternative struct declaration") {
        assertResult(true) {
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
        assertResult(true) {
            check( """
                     struct mtab_list {
                		char *dir;
                		char *device;
                		struct mtab_list *next;
                	} *mtl, *m;
                   """)
        }
        assertResult(true) {
            check( """
         void foo(){
             struct mtab_list {
        		char *dir;
        		char *device;
        		struct mtab_list *next;
        	} *mtl, *m;
         }""")
        }
        assertResult(true) {
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
        assertResult(true) {
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
        assertResult(true) {
            check(
                """
                  |struct x;
                  |struct x { };
                  |struct x;
                """.stripMargin)
        }
        assertResult(false) {
            check(
                """
                  |struct x { };
                  |struct x { };
                """.stripMargin)
        }
        assertResult(true) {
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
        assertResult(true) {
            check("extern struct x a;".stripMargin)
        }
        assertResult(false) {
            check("struct x a;".stripMargin) // error: storage size of ‘a’ isn’t known
        }
        assertResult(true) {
            check("extern struct x a; void foo() { &a; }".stripMargin) // valid in gcc, do not recheck at pointer creation
        }
        assertResult(false) {
            check("extern struct x a; void foo() { a; }".stripMargin) // valid in gcc, do not recheck at pointer creation
        }
        assertResult(false) {
            check("extern struct x a; void bar(struct x b){} void foo() { bar(a); }".stripMargin) // valid in gcc, do not recheck at pointer creation
        }


    }

    test("structs and scopes") {
        assertResult(true) {
            check( """
                     |struct structname { int fieldname; };
                     |
                     |int main() {
                     |        struct structname varname;
                     |        varname.fieldname = 2;
                     |        return varname.fieldname;
                     |}
                   """.stripMargin, true)
        }
    }

}
