package de.fosd.typechef.typesystem

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.parser.c._

@RunWith(classOf[JUnitRunner])
class RedeclarationTest extends FunSuite with ShouldMatchers with TestHelper {

    private def check(code: String, printAST: Boolean = false): Boolean = {
        println("checking " + code);
        if (printAST) println("AST: " + getAST(code));
        check(getAST(code));
    }

    private def check(ast: TranslationUnit): Boolean = {
        assert(ast != null, "void ast");
        new CTypeSystemFrontend(ast).checkAST()
    }


    test("function declaration redeclaration") {
        expectResult(true) {
            check("int foo();\n" +
                "int foo();")
        }
        //more parameters are ok, but last declaration is important
        expectResult(true) {
            check("int foo();\n" +
                "int foo(int a);" +
                "void main() { foo(1); }")
        }
        expectResult(false) {
            check("int foo(double a);\n" +
                "int foo(int a);")
        }
        expectResult(false) {
            check("int foo();\n" +
                "int foo(int a);" +
                "void main() { foo(); }")
        }
        //return types must not differ
        expectResult(false) {
            check("int foo();\n" +
                "double foo();")
        }
    }
    test("global variable redeclaration") {
        expectResult(true) {
            check("int a;\n" +
                "int a;")
        }
        expectResult(false) {
            check("int a;\n" +
                "long a;")
        }
    }
    test("local variable redeclaration") {
        expectResult(false) {
            check("int foo() {" +
                "int a;\n" +
                "int a;}")
        }
    }

    test("function redefinition") {
        expectResult(false) {
            check("int foo() {}" +
                "int foo() {}")
        }
        expectResult(true) {
            check( """
                #ifdef X
                int foo() {}
                #else
                int foo() {}
                #endif
                   """)
        }
        expectResult(true) {
            check( """
                #ifdef X
                int foo() {}
                #endif
                int a;
                #if !defined(X) && !defined(Y)
                int foo() {}
                #endif
                   """)
        }
    }


    test("function declaration/definition") {
        expectResult(true) {
            check("int foo();" +
                "int foo() {}")
        }
        expectResult(false) {
            check("int foo(int p);" +
                "int foo() {}")
        }
        expectResult(false) {
            //actually just a warning in GCC
            check("int foo();" +
                "int foo(int p) {}")
        }
        expectResult(false) {
            check("long foo();" +
                "int foo() {}")
        }
        expectResult(true) {
            check("int foo() {}" +
                "int foo();")
        }
        expectResult(false) {
            check("int foo() {}" +
                "int foo(int p);")
        }
        expectResult(false) {
            //actually just a warning in GCC
            check("int foo(int p) {}" +
                "int foo();")
        }
        expectResult(false) {
            check("int foo() {}" +
                "double foo();")
        }

        //failing case: change argument to const.
        // GCC does not complain about this
        expectResult(true) {
            check("int foo(int x);" +
                "int foo(const int x) {}")
        }

        //failing case: not sure what the problem is here. It seems it has something to do with the second line
        //(if second line is removed, no type error is detected)
        expectResult(true) {
            check("extern double fabs(double __x) __attribute__ ((__nothrow__)) __attribute__ ((__const__));" +
                "extern __typeof (fabs) fabs __asm__ (\"\" \"__GI_fabs\") __attribute__ ((visibility (\"hidden\"),));" +
                "double fabs(double x){ }")
        }

        //      the following is technically refused by gcc, but should also never occur in practice
        //        expectResult(false) {
        //            check("int foo(struct {int x;} x);" +
        //                "int foo(struct {const int x;} x) {}")
        //        }
    }
    test("variable scopes") {
        expectResult(true) {
            check("int a;" +
                "int foo() {" +
                "  double a;" +
                "  a=0;" +
                "  { long a;" +
                "    a=0; " +
                "  }" +
                "}")
        }
        expectResult(true) {
            check("int a();" +
                "int foo() {" +
                "  double a;" +
                "  a=0;" +
                "  { long a;" +
                "    a=0; " +
                "  }" +
                "}")
        }
    }

    test("function/variable decl") {
        expectResult(false) {
            check("int foo;" +
                "int foo() {}")
        }
        expectResult(false) {
            check("int foo;" +
                "int foo();")
        }
        expectResult(false) {
            check("int foo();" +
                "int foo;")
        }
    }

    test("function/variable decl inside") {
        expectResult(true) {
            check("int foo() {int foo;}")
        }
        expectResult(true) {
            check("int x;" +
                "int foo(int x){x;}")
        }
        expectResult(false) {
            check("int foo(int x){double x;}")
        }
        expectResult(false) {
            check("int foo(int x){int x;}")
        }
    }

    //currently not checked
    ignore("struct redeclaration") {
        expectResult(true) {
            check("struct a {int x;};struct a;")
        }
        expectResult(true) {
            check("struct a;struct a {int x;};")
        }
        expectResult(false) {
            check("struct a {int x;};struct a {int x;};")
        }

    }

    test("function declaration redeclaration - reparsing?") {
        expectResult(true) {
            check( """int x(){}
            #ifdef OUTER
            static
            #ifdef INLINE
            inline __attribute__((always_inline))
            #endif
            #ifndef INLINE
            inline
            #endif
            int foo() {}
            int bar() {}
            #endif
            int end() {}
                   """)
        }
    }

    test("redeclaration with CIgnore") {
        expectResult(true) {
            check("typedef union { int x; } X __attribute__ ((__transparent_union__)); \n" +
                "int foo(X a);\n" +
                "int foo(int a);")
        }
        expectResult(true) {
            check("typedef union { int x; } X __attribute__ ((__transparent_union__)); \n" +
                "int foo(X a){}\n" +
                "int foo(int a){}")
        }
    }

}