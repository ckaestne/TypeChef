package de.fosd.typechef.typesystem

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSuite, Matchers}

@RunWith(classOf[JUnitRunner])
class RedeclarationTest extends FunSuite with Matchers with TestHelperTS {


    test("function declaration redeclaration") {
        assertResult(true) {
            check("int foo();\n" +
                "int foo();")
        }
        //more parameters are ok if first was empty (not void), but last declaration is important
        assertResult(true) {
            check("int foo();\n" +
                "int foo(int a);" +
                "void main() { foo(1); }")
        }
        assertResult(false) {
            check("int foo(double a);\n" +
                "int foo(int a);")
        }
        assertResult(false) {
            check("int foo();\n" +
                "int foo(int a);" +
                "void main() { foo(); }")
        }
        //return types must not differ
        assertResult(false) {
            check("int foo();\n" +
                "double foo();")
        }
    }
    test("global variable redeclaration") {
        assertResult(true) {
            check("int a;\n" +
                "int a;")
        }
        assertResult(false) {
            check("int a;\n" +
                "long a;")
        }
    }
    test("local variable redeclaration") {
        assertResult(false) {
            check("int foo() {" +
                "int a;\n" +
                "int a;}")
        }
    }

    test("function redefinition") {
        assertResult(false) {
            check("int foo() {}" +
                "int foo() {}")
        }
        assertResult(true) {
            check( """
                #ifdef X
                int foo() {}
                #else
                int foo() {}
                #endif
                   """)
        }
        assertResult(true) {
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
        assertResult(true) {
            check("int foo();" +
                "int foo() {}")
        }
        assertResult(true) {
            check("extern int foo();" +
                "int foo() {}")
        }
        assertResult(false) {
            check("int foo(int p);" +
                "int foo() {}")
        }
        assertResult(false) {
            check("enum x { a, b }; " +
                "int foo(int p);" +
                "int foo(enum x y) {}")
        }
        assertResult(false) {
            //actually just a warning in GCC
            check("int foo();" +
                "int foo(int p) {}")
        }
        assertResult(false) {
            check("long foo();" +
                "int foo() {}")
        }
        assertResult(true) {
            check("int foo() {}" +
                "int foo();")
        }
        assertResult(false) {
            check("int foo() {}" +
                "int foo(int p);")
        }
        assertResult(false) {
            //actually just a warning in GCC
            check("int foo(int p) {}" +
                "int foo();")
        }
        assertResult(false) {
            check("int foo() {}" +
                "double foo();")
        }

        //failing case: change argument to const.
        // GCC does not complain about this
        assertResult(true) {
            check("int foo(int x);" +
                "int foo(const int x) {}")
        }

        //failing case: not sure what the problem is here. It seems it has something to do with the second line
        //(if second line is removed, no type error is detected)
        assertResult(true) {
            check("extern double fabs(double __x) __attribute__ ((__nothrow__)) __attribute__ ((__const__));" +
                "extern __typeof (fabs) fabs __asm__ (\"\" \"__GI_fabs\") __attribute__ ((visibility (\"hidden\"),));" +
                "double fabs(double x){ }")
        }

        //      the following is technically refused by gcc, but should also never occur in practice
        //        assertResult(false) {
        //            check("int foo(struct {int x;} x);" +
        //                "int foo(struct {const int x;} x) {}")
        //        }
    }
    test("variable scopes") {
        assertResult(true) {
            check("int a;" +
                "int foo() {" +
                "  double a;" +
                "  a=0;" +
                "  { long a;" +
                "    a=0; " +
                "  }" +
                "}")
        }
        assertResult(true) {
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
        assertResult(false) {
            check("int foo;" +
                "int foo() {}")
        }
        assertResult(false) {
            check("int foo;" +
                "int foo();")
        }
        assertResult(false) {
            check("int foo();" +
                "int foo;")
        }
    }

    test("function/variable decl inside") {
        assertResult(true) {
            check("int foo() {int foo;}")
        }
        assertResult(true) {
            check("int x;" +
                "int foo(int x){x;}")
        }
        assertResult(false) {
            check("int foo(int x){double x;}")
        }
        assertResult(false) {
            check("int foo(int x){int x;}")
        }
    }

    //currently not checked
    ignore("struct redeclaration") {
        assertResult(true) {
            check("struct a {int x;};struct a;")
        }
        assertResult(true) {
            check("struct a;struct a {int x;};")
        }
        assertResult(false) {
            check("struct a {int x;};struct a {int x;};")
        }

    }

    test("function declaration redeclaration - reparsing?") {
        assertResult(true) {
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
        assertResult(true) {
            check("typedef union { int x; } X __attribute__ ((__transparent_union__)); \n" +
                "int foo(X a);\n" +
                "int foo(int a);")
        }
        assertResult(true) {
            check("typedef union { int x; } X __attribute__ ((__transparent_union__)); \n" +
                "int foo(X a){}\n" +
                "int foo(int a){}")
        }
    }

}