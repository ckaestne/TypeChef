package de.fosd.typechef.typesystem

import _root_.de.fosd.typechef.conditional._
import _root_.de.fosd.typechef.parser.c._
import _root_.de.fosd.typechef.featureexpr.FeatureExprFactory._
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

/**
 * structs are complicated:
 *
 * a tag gives a name to a struct. In "struct x", x is a tag for a struct.
 *
 * incomplete structs don't have a body, complete structs do.
 *
 * ignoring qualified types (const etc) of structs and members.
 *
 * members of struct shall not contain a member with incomplete or function type (pointers to those are okay though)
 *
 * the presence of a struct-declaration-list in a struct-or-union-specifier declares a new type, within a translation unit
 *
 * the struct type is incomplete until after the } that terminates the list.
 *
 * if "struct x" is used outside another context, it declares tag x with an incomplete struct type; redeclarations
 * do not change the type
 *
 * structs may be redeclared in different scopes
 *
 */

class StructTest extends FunSuite with CEnv with ShouldMatchers with TestHelper {

    //    private def check(code: String, printAST: Boolean = false): Boolean = {
    //        println("checking " + code);
    //        if (printAST) println("AST: " + getAST(code));
    //        check(getAST(code));
    //    }
    //    private def check(ast: TranslationUnit): Boolean = {
    //        assert(ast != null, "void ast");
    //        new CTypeSystemFrontend(ast).checkAST
    //    }

    test("StructEnv behavior") {
        var env = new StructEnv()
        env.isComplete("a", true) should be(False)

        //struct a;
        env = env.addIncomplete("a", true, True, 1)
        env.isComplete("a", true) should be(False)

        //struct a {double x;} //same scope should make it complete
        env = env.addComplete("a", true, True, new ConditionalTypeMap() +("x", True, null, One(CDouble())), 1)
        env.isComplete("a", true) should be(True)

        //struct a; // in same scope should not affect result
        env = env.addIncomplete("a", true, True, 1)
        env.isComplete("a", true) should be(True)

        //struct a; // in higher scope should replace
        env = env.addIncomplete("a", true, fa, 2)
        env.isComplete("a", true) should be(fa.not)

        //struct a{double x; double y;};
        env = env.addComplete("a", true, fa, new ConditionalTypeMap()+("x", True, null, One(CDouble()))+("y", True, null, One(CDouble())), 2)
        env.isComplete("a", true) should be(True)

        val fields=env.getFieldsMerged("a", true)
        fields.whenDefined("x") should be (True)
        fields.whenDefined("y") should be (fa)
    }

    """
      |struct tnode {
      |  int count;
      |  struct tnode *left, *right;
      |};
      |struct tnode s;
      |struct tnode *sp;
    """.stripMargin


    """
      |typedef struct tnode TNODE;
      |struct tnode {
      |  int count;
      |  TNODE *left, *right;
      |};
      |TNODE s, *sp;
    """.stripMargin

    """
      |struct s1 { struct s2 *s2p; /* ... */ }; // D1
      |struct s2 { struct s1 *s1p; /* ... */ }; // D2
    """.stripMargin


    """
      |struct s1 { int a; };
      |{
      |  struct s2 { struct s1 x; }; //refers to {int a}
      |  struct s1 { int b };
      |}
    """.stripMargin
    """
      |struct s1 { int a; };
      |{
      |  struct s1; // introduces a new struct in an inner scope
      |  struct s2 { struct s1 x; }; //refers to {int b}
      |  struct s1 { int b };
      |}
    """.stripMargin

    //deref pointers to incomplete structs


//    void foo(){
//    struct x{};
//    struct x a;
//    {
//    struct x;
//    struct x b;
//    }
//    }

    """struct x foo() { return 0; }"""//incomplete type

    """void foo(struct x b) { return 0; }"""//incomplete type

    """struct x {
      |        struct z { int a; } c;
      |        struct z b;
      |} y;
    """

    """
      |void foo() {
      |        int a;
      |        {
      |                struct x {};
      |                struct x a;
      |        }
      |        struct x b;//struct x is incomplete
      |}
    """.stripMargin

    """
      |void foo() {
      |        struct x *a;
      |        //struct x { int b; };
      |        int i;
      |        i=a->b;//dereferencing to incomplete type
      |}
      |
    """.stripMargin

}
