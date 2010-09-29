package de.fosd.typechef.parser.c

import junit.framework._;
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._

class CGramFilesTest extends TestCase {
    def parseFile(fileName: String) {
        val result = new CParser().translationUnit(
            CLexer.lexFile(fileName, "testfiles/cgram/"), FeatureExpr.base
            )
        System.out.println(result)
        result match {
            case Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                //succeed
            }
            case NoSuccess(msg, context, unparsed, inner) =>
                fail(msg + " at " + unparsed + " with context " + context + " " + inner)
        }

    }

    // 

    def test1() { parseFile("testfiles/cgram/test.c") }
    def test2() { parseFile("testfiles/cgram/test2.c") }
    def test3() { parseFile("testfiles/cgram/test3.c") }
    def test4() { parseFile("testfiles/cgram/test4.c") }
    def test5() { parseFile("testfiles/cgram/test5.c") }
    def test6() { parseFile("testfiles/cgram/test6.c") }
    def test7() { parseFile("testfiles/cgram/test7.c") }
    def ignoretest8() { parseFile("testfiles/cgram/test8.c") } //scoped typedef
    def test9() { parseFile("testfiles/cgram/test9.c") }
    def test10() { parseFile("testfiles/cgram/test10.c") }
    def test11() { parseFile("testfiles/cgram/test11.c") }
    def test12() { parseFile("testfiles/cgram/test12.c") }
    def test13() { parseFile("testfiles/cgram/test13.c") }
    def test14() { parseFile("testfiles/cgram/test14.c") }
    def test15() { parseFile("testfiles/cgram/test15.c") }
    def test16() { parseFile("testfiles/cgram/test16.c") }
    def test17() { parseFile("testfiles/cgram/test17.c") }
    def test18() { parseFile("testfiles/cgram/test18.c") }
    def test19() { parseFile("testfiles/cgram/test19.c") }
    def test20() { parseFile("testfiles/cgram/test20.c") }
    def test21() { parseFile("testfiles/cgram/test21.c") }
    def test22() { parseFile("testfiles/cgram/test22.c") }
    def test23() { parseFile("testfiles/cgram/test23.c") }
    def test24() { parseFile("testfiles/cgram/test24.c") }
    def test25() { parseFile("testfiles/cgram/test25.c") }
    def test26() { parseFile("testfiles/cgram/test26.c") }
    def test27() { parseFile("testfiles/cgram/test27.c") }
    def test28() { parseFile("testfiles/cgram/test28.c") }
    def test29() { parseFile("testfiles/cgram/test29.c") }
    def test30() { parseFile("testfiles/cgram/test30.c") }
    def test31() { parseFile("testfiles/cgram/test31.c") }
    def test32() { parseFile("testfiles/cgram/test32.c") }
    def test33() { parseFile("testfiles/cgram/test33.c") }
    def test34() { parseFile("testfiles/cgram/test34.c") }
    def test35() { parseFile("testfiles/cgram/test35.c") }
    def test36() { parseFile("testfiles/cgram/test36.c") }
    def test37() { parseFile("testfiles/cgram/test37.c") }
    def test38() { parseFile("testfiles/cgram/test38.c") }
    def test39() { parseFile("testfiles/cgram/test39.c") }
    def test40() { parseFile("testfiles/cgram/test40.c") }
    def test41() { parseFile("testfiles/cgram/test41.c") }
    def ignoretest42() { parseFile("testfiles/cgram/test42.c") }//ignore variable and typedef with same name
    def test43() { parseFile("testfiles/cgram/test43.c") }
    def test44() { parseFile("testfiles/cgram/test44.c") }
    def test45() { parseFile("testfiles/cgram/test45.c") }
    def test46() { parseFile("testfiles/cgram/test46.c") }
    def test47() { parseFile("testfiles/cgram/test47.c") }
    def test48() { parseFile("testfiles/cgram/test48.c") }
    def test49() { parseFile("testfiles/cgram/test49.c") }
    def test50() { parseFile("testfiles/cgram/test50.c") }
    def test51() { parseFile("testfiles/cgram/test51.c") }
    def test52() { parseFile("testfiles/cgram/test52.c") }
    def test53() { parseFile("testfiles/cgram/test53.c") }
    def test54() { parseFile("testfiles/cgram/test54.c") }
    def test55() { parseFile("testfiles/cgram/test55.c") }
    def test56() { parseFile("testfiles/cgram/test56.c") }
    def test57() { parseFile("testfiles/cgram/test57.c") }
    def test58() { parseFile("testfiles/cgram/test58.c") }
    def test59() { parseFile("testfiles/cgram/test59.c") }
    def test60() { parseFile("testfiles/cgram/test60.c") }
    def test61() { parseFile("testfiles/cgram/test61.c") }
    def test62() { parseFile("testfiles/cgram/test62.c") }
    def test63() { parseFile("testfiles/cgram/test63.c") }
    def test64() { parseFile("testfiles/cgram/test64.c") }
    def test65() { parseFile("testfiles/cgram/test65.c") }
    def test66() { parseFile("testfiles/cgram/test66.c") }
    def test67() { parseFile("testfiles/cgram/test67.c") }
    def test68() { parseFile("testfiles/cgram/test68.c") }
    def test69() { parseFile("testfiles/cgram/test69.c") }
    def test70() { parseFile("testfiles/cgram/test70.c") }
    def test71() { parseFile("testfiles/cgram/test71.c") }
    def test72() { parseFile("testfiles/cgram/test72.c") }
    def test73() { parseFile("testfiles/cgram/test73.c") }
    def test74() { parseFile("testfiles/cgram/test74.c") }
    def test75() { parseFile("testfiles/cgram/test75.c") }
    def test76() { parseFile("testfiles/cgram/test76.c") }
    def test77() { parseFile("testfiles/cgram/test77.c") }
    def test78() { parseFile("testfiles/cgram/test78.c") }
    def test79() { parseFile("testfiles/cgram/test79.c") }
    def test80() { parseFile("testfiles/cgram/test80.c") }
    def test81() { parseFile("testfiles/cgram/test81.c") }
    def test83() { parseFile("testfiles/cgram/test83.c") }
    def test84() { parseFile("testfiles/cgram/test84.c") }
    def test85() { parseFile("testfiles/cgram/test85.c") }
    def test86() { parseFile("testfiles/cgram/test86.c") }
    def test87() { parseFile("testfiles/cgram/test87.c") }

}