//package de.fosd.typechef.typesystem
//
//
//import org.junit.runner.RunWith
//import org.scalatest.FunSuite
//import org.scalatest.junit.JUnitRunner
//import org.scalatest.matchers.ShouldMatchers
//import de.fosd.typechef.parser.c._
//import org.kiama.attribution.DynamicAttribution._
//import org.kiama._
//import attribution.Attributable
//import de.fosd.typechef.featureexpr.FeatureExpr
//import FeatureExpr.base
//import de.fosd.typechef.parser.Opt
//
//@RunWith(classOf[JUnitRunner])
//class TypeSystemTest extends FunSuite with ShouldMatchers with ASTNavigation {
//
//
//   test("typecheck simple translation unit") {
//        expect(true) {check(ast)}
//        expect(false) {check("void bar(){foo();}")}
//    }
//    test("detect redefinitions") {
//        expect(false) {check("void foo(){} void foo(){}")}
//        expect(false) {
//            check("void foo(){} \n" +
//                    "#ifdef A\n" +
//                    "void foo(){}\n" +
//                    "#endif\n")
//        }
//        expect(true) {
//            check("#ifndef A\n" +
//                    "void foo(){} \n" +
//                    "#endif\n" +
//                    "#ifdef A\n" +
//                    "void foo(){}\n" +
//                    "#endif\n")
//        }
//    }
//    test("typecheck translation unit with features") {
//        expect(true) {
//            check("void foo(){} \n" +
//                    "#ifdef A\n" +
//                    "void bar(){foo();}\n" +
//                    "#endif\n")
//        }
//        expect(false) {
//            check(
//                "#ifdef A\n" +
//                        "void foo(){} \n" +
//                        "#endif\n" +
//                        "void bar(){foo();}\n")
//        }
//        expect(true) {
//            check(
//                "#ifdef A\n" +
//                        "void foo(){} \n" +
//                        "#endif\n" +
//                        "#ifndef A\n" +
//                        "void foo(){} \n" +
//                        "#endif\n" +
//                        "void bar(){foo();}\n")
//        }
//
//    }
//}