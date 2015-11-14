package de.fosd.typechef.typesystem


import de.fosd.typechef.parser.c._
import org.junit.runner.RunWith
import org.scalatest.{Matchers, FunSuite}
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TypeSignatureTest extends FunSuite with Matchers with TestHelperTS {



    test("typdef types") {
        assertResult(true) {
            check("typedef int a;\n" +
                "void foo(){a b;}")
        }
        assertResult(false) {
            check("#ifdef X\n" +
                "typedef int a;\n" +
                "#endif\n" +
                "void foo(){a b;}")
        }
    }


    //    test("enum declaration") {
    //        expectResult(true) {
    //            check("enum s;") //forward declaration
    //        }
    //        expectResult(false) {
    //            check("enum s x;") //not a forward declaration
    //        }
    //        expectResult(true) {
    //            check("enum s {a,b};\n" +
    //                "void foo(){enum s x;}")
    //        }
    //        expectResult(false) {
    //            check("#ifdef X\n" +
    //                "enum s {a, b};\n" +
    //                "#endif\n" +
    //                "void foo(){enum s x;}")
    //        }
    //        expectResult(false) {
    //            check("#ifdef X\n" +
    //                "enum s {a, b};\n" +
    //                "#endif\n" +
    //                "enum s foo();")
    //        }
    //    }

}