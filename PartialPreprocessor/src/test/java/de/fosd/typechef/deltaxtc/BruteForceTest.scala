//package de.fosd.typechef.deltaxtc
//
//import de.fosd.typechef.featureexpr._
//import de.fosd.typechef.lexer.{Token, FeatureExprLib, PartialPPLexer, LexerException}
//import org.junit._
//import java.io.{FileOutputStream, File}
//
///**
// * run tests against gcc in all configurations
// *
// * (exploiting commutativity)
// *
// * @author kaestner
// */
//class BruteForceTest {
//
//    import scala.collection.JavaConversions._
//
//    @Test def testNestingDead {
//        testFile("nestingdead.c")
//    }
//    @Test def testDeadElse {
//        testFile("deadelse.h")
//    }
//    @Test def testIncludeGuard {
//        testFile("in1.c")
//    }
//    @Test def testUnlikely {
//        testFile("unlikely.h")
//    }
//    @Test def testByteOrder {
//        testFile("byteorder.h")
//    }
//    //	@Test
//    //	@Ignore
//    //	public void testIf() throws LexerException, IOException {
//    //		testFile("if.h");
//    //	}
//    @Test def testAlternativeMacros {
//        testFile("macro2.c")
//    }
//    @Test def testIncludeGuards {
//        testFile("includeguards.c")
//    }
//    @Test def testIncludeGuards2 {
//        testFile("includeguards2.h")
//    }
//    @Test def testDefDefined {
//        testFile("defdefined.c")
//    }
//    @Test def testAlternativeDef {
//        testFile("alternativedef.c")
//    }
//    @Test def testHiddenBaseAndDead {
//        testFile("hiddenDeadAndBase.c")
//    }
//    //	@Test
//    //	@Ignore
//    //	public void testMultiInclude() throws LexerException, IOException {
//    //		// XXX this is not supported right now. let's see whether we will need
//    //		// it.
//    //		testFile("multiinclude.c");
//    //	}
//    @Test def testIfCondition {
//        testFile("ifcondition.c")
//    }
//    @Test def testBeispielJoerg {
//        testFile("beispielJoerg.c")
//    }
//    @Test def testNumericIfAlternative {
//        testFile("ifdefnumeric.c")
//    }
//    @Test def testLinuxTestFLock {
//        testFile("linuxtestflock.c")
//    }
//    @Test def testElIfChain {
//        testFile("elifchain.c")
//    }
//    @Test def testSelfDef {
//        testFile("selfdef.c")
//    }
//    @Test def testNonTautologicExpansions {
//        testFile("non_tautologic.c")
//    }
//    @Test def testVariadic {
//        testFile("variadic.c")
//    }
//    @Test def testIncompMacroExp {
//        testFile("incompatibleMacroExp.c")
//    }
//    @Test def testRedef {
//        testFile("redef.h")
//    }
//    @Test def testJiffies {
//        testFile("jiffiesTest.h")
//    }
//    @Test def testIncludeMacros {
//        testFile("includemacro.c")
//    }
//    @Test def testRecursiveMacro {
//        testFile("recursivemacro.h")
//    }
//    @Test def testStringifyNl {
//        testFile("stringifyNl.c")
//    }
//    @Test def testUseCondDef {
//        testFile("useconddef.c")
//    }
//    @Test def testDivByZero {
//        testFile("test_div_by_zero.c")
//    }
//    @Test def testDivByZero2 {
//        testFile("test_div_by_zero2.c")
//    }
//    @Test def testMacroPNF {
//        testFile("macroPFN.c")
//    }
//    @Test def testParametricMacro {
//        testFile("parametricmacro.h")
//    }
//    @Test def testParametricMacro2 {
//        testFile("parametricmacro2.h")
//    }
//    @Test def testKBuildStr {
//        testFile("kbuildstr.c")
//    }
//    @Test def testStringify {
//        testFile("stringify.c")
//    }
//    @Test def testAlternativeDifferentArities1 {
//        testFile("alternDiffArities1.c")
//    }
//    @Test def testAlternativeDifferentArities2 {
//        testFile("alternDiffArities2.c")
//    }
//    @Test def testDateTime {
//        testFile("dateTime.c")
//    }
//    @Test def testNumbers {
//        testFile("numbers.c")
//    }
//    @Test def testConcatVarargs {
//        testFile("concatVarargs.c")
//    }
//    @Test def testDeadcomparison {
//        testFile("deadcomparison.c")
//    }
//    @Test def testExpandWithinExpand {
//        testFile("expandWithinExpand.c", false, true)
//    }
//    @Test
//    @Ignore def testLinebreaks {
//        testFile("linebreaks.c", false, true)
//    }
//    @Test def testLinebreaks2 {
//        testFile("linebreaks2.c", false, true)
//    }
//    @Test def testFileBaseFile {
//        testFile("filebasefile.c", false, true)
//    }
//    @Test def testBnx2 {
//        testFile("bnx2.c", false, true)
//    }
//    @Test
//    @Ignore("bug in lexer, see issue #10") def testBnx {
//        testFile("bnx.c", false, true)
//    }
//    @Test def testVarargs {
//        testFile("varargs.c", false, true)
//    }
//    val folder = "tc_data/";
//
//
//    @Test
//    def testXtcTestCases {
//
//        val testfolders = new File("/usr0/home/ckaestne/work/TypeChef/xtc/tests/cpp") ::
//            new File("/usr0/home/ckaestne/work/TypeChef/xtc/tests/preprocessor") :: Nil
//        for (testfolder<-testfolders; file <- testfolder.listFiles())
//            if (file.isFile && file.getName.endsWith(".c")) {
//                println("*** " + file)
//                testFileBruteForce(file)
//            }
//
//    }
//
//    def testFile(filename: String, a: Boolean = false, b: Boolean = false) {
//        val in = File.createTempFile("typechef", ".c")
//        val s = getClass().getResourceAsStream(
//            "/" + folder + filename)
//        Help.copyStream(s, new FileOutputStream(in))
//
//        testFileBruteForce(in)
//
//        in.delete()
//    }
//
//    val sysIncludePath: java.util.List[String] = List(
//        "/usr0/home/ckaestne/work/TypeChef/xtc/tests/cpp/include1",
//        "/usr0/home/ckaestne/work/TypeChef/xtc/tests/cpp/include2",
//    "/usr0/home/ckaestne/work/TypeChef/xtc/tests/cpp/sys"
//    )
//
//
//    def testFileBruteForce(file: File) {
//        val typechefTokens = new PartialPPLexer().parseFile(file.getAbsolutePath, sysIncludePath, FeatureExprLib.featureModelFactory().empty);
//
//        println(typechefTokens)
//
//
//        val features = typechefTokens.foldRight(Set[String]())((t, a) => a ++ t.getFeature.collectDistinctFeatures)
//        println(features)
//
//
//        assert(features.size <= 8, "too many features: " + features.size)
//
//        //copy file
//        val out = File.createTempFile("typechef", ".i")
//
//        for (config <- features.subsets) {
//            println("Config: " + config)
//
//            val cmd = "cpp -P " + file.getAbsolutePath + " " + config.map("-D " + _).mkString(" ") +" "+ sysIncludePath.map("-I "+_).mkString(" ") //+" > "+out
//
//            println(cmd)
//            val sr = exec2out(cmd)
//            Help.copyStream(sr, new FileOutputStream(out))
//
//
//            val expectedConfigTokens = typechefTokens.filter(
//                _.getFeature.evaluate(config)
//            )
//
//            val configTokens = new PartialPPLexer().parseFile(out.getAbsolutePath, sysIncludePath, FeatureExprLib.featureModelFactory().empty);
//
//            Assert.assertEquals(configTokens.size(), expectedConfigTokens.size)
//            for (i <- 0 until (configTokens.size() - 1)) {
//                Assert.assertEquals(configTokens.get(i).getText, expectedConfigTokens.get(i).getText);
//            }
//
//        }
//
//
//        out.delete()
//
//    }
//
//    def exec(cmd: String) = Runtime.getRuntime exec cmd
//    /* result to System.out */
//    def exec2out(cmd: String) = {
//        val process = exec(cmd)
//        process.getInputStream
//    }
//
//
//}
