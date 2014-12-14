package de.fosd.typechef.lexer.macrotable

import java.io.IOException

import de.fosd.typechef.lexer.LexerException
import org.junit.{Ignore, Test}
import org.scalatest.FunSuite

class XtcDiffFileTest extends FunSuite with DifferentialTestingFramework {
    override protected def useXtc(): Boolean = true
    override protected def status(s: String) = info(s)


    private def testFile(s:String):Unit = analyzeFile(getClass.getResource("/tc_data/"+s))


             val filesToTest =
             """alternDiffArities1.c  ifdefnumeric.c          numbers.c
               alternDiffArities2.c  in1.c                   out1.c
               alternativedef.c      in2.c                   selfdef.c
               beispielJoerg.c       includeguards.c         simplecompare.c
               bnx.c                 includemacro.c          stringify.c
               bnx2.c                incompatibleMacroExp.c  stringifyNl.c
               concatVarargs.c       kbuildstr.c             test_div_by_zero.c
               conditionalerror.c    linebreaks.c            test_div_by_zero2.c
               dateTime.c            linebreaks2.c           test_div_by_zero3.c
               deadcomparison.c      linuxtestflock.c        test_div_by_zero4.c
               defdefined.c          macro.c                 tokenpasting.c
               elifchain.c           macro2.c                undef.c
               emptyinclude.c        macroPFN.c              useconddef.c
               expandWithinExpand.c  multiinclude.c          varargs.c
               filebasefile.c        multimacro.c            variadic.c
               hiddenDeadAndBase.c   nestingdead.c
               ifcondition.c         non_tautologic.c
               byteorder.h           h2.h              jiffiesTest.h       redef.h
               deadelse.h            header.h          parametricmacro.h   unlikely.h
               filebasefileheader.h  if.h              parametricmacro2.h
               h1.h                  includeguards2.h  recursivemacro.h
             """.split("\\ +").map(_.trim).filter(_.nonEmpty)

    for (file<-filesToTest)
        test(s"differential testing of $file") {
            testFile(file)
        }

//    test("testNestingDead") {
//        println(filesToTest.mkString(","))
////        testFile("nestingdead.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testDeadElse {
//        testFile("deadelse.h")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testIncludeGuard {
//        testFile("in1.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testUnlikely {
//        testFile("unlikely.h")
//    }
//    @Test
//    def testByteOrder {
//        testFile("byteorder.h")
//    }
//    //	@Test
//    //	@Ignore
//    //	public void testIf() throws LexerException, IOException {
//    //		testFile("if.h");
//    //	}
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testAlternativeMacros {
//        testFile("macro2.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testIncludeGuards {
//        testFile("includeguards.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testIncludeGuards2 {
//        testFile("includeguards2.h")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testDefDefined {
//        testFile("defdefined.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testAlternativeDef {
//        testFile("alternativedef.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testHiddenBaseAndDead {
//        testFile("hiddenDeadAndBase.c")
//    }
//    //	@Test
//    //	@Ignore
//    //	public void testMultiInclude() throws LexerException, IOException {
//    //		// XXX this is not supported right now. let's see whether we will need
//    //		// it.
//    //		testFile("multiinclude.c");
//    //	}
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testIfCondition {
//        testFile("ifcondition.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testBeispielJoerg {
//        testFile("beispielJoerg.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testNumericIfAlternative {
//        testFile("ifdefnumeric.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testLinuxTestFLock {
//        testFile("linuxtestflock.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testElIfChain {
//        testFile("elifchain.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testSelfDef {
//        testFile("selfdef.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testNonTautologicExpansions {
//        testFile("non_tautologic.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testVariadic {
//        testFile("variadic.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testIncompMacroExp {
//        testFile("incompatibleMacroExp.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testRedef {
//        testFile("redef.h")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testJiffies {
//        testFile("jiffiesTest.h")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testIncludeMacros {
//        testFile("includemacro.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testRecursiveMacro {
//        testFile("recursivemacro.h")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testStringifyNl {
//        testFile("stringifyNl.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testUseCondDef {
//        testFile("useconddef.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testDivByZero {
//        testFile("test_div_by_zero.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testDivByZero2 {
//        testFile("test_div_by_zero2.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testDivByZero3 {
//        testFile("test_div_by_zero3.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testDivByZero4 {
//        testFile("test_div_by_zero4.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testMacroPNF {
//        testFile("macroPFN.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testParametricMacro {
//        testFile("parametricmacro.h")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testParametricMacro2 {
//        testFile("parametricmacro2.h")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testKBuildStr {
//        testFile("kbuildstr.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testStringify {
//        testFile("stringify.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testAlternativeDifferentArities1 {
//        testFile("alternDiffArities1.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testAlternativeDifferentArities2 {
//        testFile("alternDiffArities2.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testDateTime {
//        testFile("dateTime.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testNumbers {
//        testFile("numbers.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testConcatVarargs {
//        testFile("concatVarargs.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testDeadcomparison {
//        testFile("deadcomparison.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testExpandWithinExpand {
//        testFile("expandWithinExpand.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testLinebreaks {
//        testFile("linebreaks.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testLinebreaks2 {
//        testFile("linebreaks2.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testFileBaseFile {
//        testFile("filebasefile.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testBnx2 {
//        testFile("bnx2.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testBnx {
//        testFile("bnx.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testVarargs {
//        testFile("varargs.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testConditionalError {
//        testFile("conditionalerror.c")
//    }
//    @Test
//    @throws(classOf[LexerException])
//    @throws(classOf[IOException])
//    def testSimpleCompare {
//        testFile("simplecompare.c")
//    }
}
