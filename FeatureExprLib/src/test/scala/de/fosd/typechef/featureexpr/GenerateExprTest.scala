//package de.fosd.typechef.featureexpr;
//
//import org.junit.Ignore
//import junit.framework._;
//import junit.framework.Assert._
//import org.junit.Test
//import FeatureExpr._
//import scala.util.Random
//
//class GenTestFeatureExpr extends TestCase {
//
//    def vars = List(DefinedExternal("a"), DefinedExternal("b"), DefinedExternal("c"), DefinedExternal("d"), DefinedExternal("e"), DefinedExternal("f"))
//    def gen(a: FeatureExprTree, b: FeatureExprTree) =
//        List(Not(a), And(a, b), Or(a, b), And(Not(a), b), Or(Not(a), b))
//
//    @Test
//    def testGenerate() {
//        var results: List[FeatureExprTree] = vars
//        results = results ++ gen(vars.head, vars.head)
//        results = results ++ gen(vars.head, vars.tail.head)
//        results = results ++ gen(vars.tail.head, vars.tail.head)
//        results = results ++ gen(vars.head, vars.tail.tail.head)
//        var counter = 200
//        while (counter > 0) {
//            counter = counter - 1
//            val a = results(Random.nextInt(results.size))
//            val b = results(Random.nextInt(results.size))
//            results = results ++ gen(a, b)
//        }
//        //		print (results)
//
//        var out_file = new java.io.FileOutputStream("out.txt");
//        var out_stream = new java.io.PrintStream(out_file);
//        for (r <- results) {
//        	counter=counter+1
//            out_stream.print("\tdef testGen"+counter+"() {run("+print(r)+")}\n");
//            }
//        out_stream.close;
//    }
//
//    def print(r: FeatureExprTree): String =
//        r match {
//            case And(c) => "And(" + c.map(print(_)).mkString(", ") + ")"
//            case Or(c) => "Or(" + c.map(print(_)).mkString(", ") + ")"
//            case Not(c) => "Not(" + print(c) + ")"
//            case DefinedExternal(c) => "DefinedExternal(\"" + c + "\")"
//            case e => "FAIL"
//        }
//
//
//
//}
