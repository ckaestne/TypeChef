package de.fosd.typechef.typesystem


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import de.fosd.typechef.featureexpr.FeatureExpr.base
import de.fosd.typechef.featureexpr.FeatureExpr.dead
import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.FeatureExpr

@RunWith(classOf[JUnitRunner])
class TypeSignatureTest extends FunSuite with ShouldMatchers with TestHelper {

    private def check(code: String, printAST: Boolean = false): Boolean = {
        println("checking " + code);
        if (printAST) println("AST: " + getAST(code));
        check(getAST(code));
    }

    private def check(ast: TranslationUnit): Boolean = {
        assert(ast != null, "void ast");
        new CTypeSystemFrontend(ast).checkAST
    }


    test("typdef types") {
        expect(true) {
            check("typedef int a;\n" +
                "void foo(){a b;}")
        }
        expect(false) {
            check("#ifdef X\n" +
                "typedef int a;\n" +
                "#endif\n" +
                "void foo(){a b;}")
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

    test("recursive structures") {
        expect(true) {
            check("""
                     struct mtab_list {
                		char *dir;
                		char *device;
                		struct mtab_list *next;
                	} *mtl, *m;
                 """)
        }
        expect(true) {
            check("""
         void foo(){
             struct mtab_list {
        		char *dir;
        		char *device;
        		struct mtab_list *next;
        	} *mtl, *m;
         }""")
        }
        expect(true) {
            check("""
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

    //    test("enum declaration") {
    //        expect(true) {
    //            check("enum s;") //forward declaration
    //        }
    //        expect(false) {
    //            check("enum s x;") //not a forward declaration
    //        }
    //        expect(true) {
    //            check("enum s {a,b};\n" +
    //                "void foo(){enum s x;}")
    //        }
    //        expect(false) {
    //            check("#ifdef X\n" +
    //                "enum s {a, b};\n" +
    //                "#endif\n" +
    //                "void foo(){enum s x;}")
    //        }
    //        expect(false) {
    //            check("#ifdef X\n" +
    //                "enum s {a, b};\n" +
    //                "#endif\n" +
    //                "enum s foo();")
    //        }
    //    }

}