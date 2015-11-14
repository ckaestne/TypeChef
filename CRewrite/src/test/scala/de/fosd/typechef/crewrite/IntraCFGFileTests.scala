package de.fosd.typechef.crewrite

import java.io.{FileNotFoundException, InputStream}
import org.junit.{Ignore, Test}
import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureModel}


class IntraCFGFileTests extends TestHelper with EnforceTreeHelper with IntraCFG with ConditionalNavigation with CFGHelper {
    val folder = "testfiles/"

    private def checkCFG(filename: String, fm: FeatureModel = FeatureExprFactory.default.featureModelFactory.empty) = {
        var errorOccured = false

        println("check cfg for file (" + filename + ")")
        val inputStream: InputStream = getClass.getResourceAsStream("/" + folder + filename)

        if (inputStream == null)
            throw new FileNotFoundException("Input file not fould: " + filename)

        val ast = parseFile(inputStream, filename, folder)
        val family_ast = rewriteInfiniteForLoops[TranslationUnit](prepareAST(ast))
        val family_env = CASTEnv.createASTEnv(family_ast)

        val family_function_defs = filterAllASTElems[FunctionDef](family_ast)
        errorOccured |= family_function_defs.map(intraCfgFunctionDef(_, family_env)).exists(_ == true)

        errorOccured
    }

    private def intraCfgFunctionDef(f: FunctionDef, env: ASTEnv) = {
        val s = getAllSucc(f, env)
        val p = getAllPred(f, env)
        val errors = CheckCFG.compareSuccWithPred(s, p, env)

        if (errors.size > 0)
            CFGErrorOutput.printCFGErrors(s, p, errors, env)

        errors.size > 0
    }

    // gcc testsuite
    // using llvm and clang, one can compute non-variability-aware cfgs with
    // clang -cc1 -analyze -cfg-dump <test file>
    // clang does not support nested function definitions; here we ignore tests with nested function definitions
    // we also ignore test cases that typechef cannot parse
    // we also ignore test cases that make use of assembler (prettyprinter fails here); these test cases are not
    // interesting anyway

    @Test def test_20000105_1() {
        assert(checkCFG("20000105-1.c") == false)
    }

    @Test def test_20000105_2() {
        assert(checkCFG("20000105-2.c") == false)
    }

    @Ignore def test_20000120_1() {
        assert(checkCFG("20000120-1.c") == false)
    }

    @Test def test_20000120_2() {
        assert(checkCFG("20000120-2.c") == false)
    }

    @Test def test_20000127_1() {
        assert(checkCFG("20000127-1.c") == false)
    }

    @Ignore def test_20000211_1() {
        assert(checkCFG("20000211-1.c") == false)
    }

    @Test def test_20000211_3() {
        assert(checkCFG("20000211-3.c") == false)
    }

    @Test def test_20000224_1() {
        assert(checkCFG("20000224-1.c") == false)
    }

    @Test def test_20000314_1() {
        assert(checkCFG("20000314-1.c") == false)
    }

    // parser fails
    @Ignore def test_20000314_2() {
        assert(checkCFG("20000314-2.c") == false)
    }

    @Test def test_20000319_1() {
        assert(checkCFG("20000319-1.c") == false)
    }

    @Test def test_20000326_1() {
        assert(checkCFG("20000326-1.c") == false)
    }

    @Test def test_20000326_2() {
        assert(checkCFG("20000326-2.c") == false)
    }

    @Test def test_20000329_1() {
        assert(checkCFG("20000329-1.c") == false)
    }

    @Test def test_20000403_1() {
        assert(checkCFG("20000403-1.c") == false)
    }

    @Test def test_20000403_2() {
        assert(checkCFG("20000403-2.c") == false)
    }

    @Test def test_20000405_1() {
        assert(checkCFG("20000405-1.c") == false)
    }

    @Test def test_20000405_2() {
        assert(checkCFG("20000405-2.c") == false)
    }

    @Test def test_20000405_3() {
        assert(checkCFG("20000405-3.c") == false)
    }

    @Test def test_20000412_1() {
        assert(checkCFG("20000412-1.c") == false)
    }

    @Test def test_20000412_2() {
        assert(checkCFG("20000412-2.c") == false)
    }

    @Test def test_20000420_1() {
        assert(checkCFG("20000420-1.c") == false)
    }

    @Test def test_20000420_2() {
        assert(checkCFG("20000420-2.c") == false)
    }

    @Test def test_20000427_1() {
        assert(checkCFG("20000427-1.c") == false)
    }

    @Test def test_20000502_1() {
        assert(checkCFG("20000502-1.c") == false)
    }

    @Test def test_20000504_1() {
        assert(checkCFG("20000504-1.c") == false)
    }

    @Test def test_20000511_1() {
        assert(checkCFG("20000511-1.c") == false)
    }

    @Test def test_20000517_1() {
        assert(checkCFG("20000517-1.c") == false)
    }

    @Test def test_20000518_1() {
        assert(checkCFG("20000518-1.c") == false)
    }

    @Test def test_20000523_1() {
        assert(checkCFG("20000523-1.c") == false)
    }

    @Test def test_20000605_1() {
        assert(checkCFG("20000605-1.c") == false)
    }

    @Test def test_20000606_1() {
        assert(checkCFG("20000606-1.c") == false)
    }

    @Test def test_20000609_1() {
        assert(checkCFG("20000609-1.c") == false)
    }

    @Test def test_20000629_1() {
        assert(checkCFG("20000629-1.c") == false)
    }

    // test fails
    @Ignore def test_20000701_1() {
        assert(checkCFG("20000701-1.c") == false)
    }

    @Test def test_20000717_1() {
        assert(checkCFG("20000717-1.c") == false)
    }

    @Test def test_20000718() {
        assert(checkCFG("20000718.c") == false)
    }

    // test fails; infinite loop
    @Test def test_20000728_1() {
        assert(checkCFG("20000728-1.c") == false)
    }

    @Ignore def test_20000802_1() {
        assert(checkCFG("20000802-1.c") == false)
    }

    @Test def test_20000803_1() {
        assert(checkCFG("20000803-1.c") == false)
    }

    // test fails; has dead code
    @Ignore def test_20000804_1() {
        assert(checkCFG("20000804-1.c") == false)
    }

    @Test def test_20000818_1() {
        assert(checkCFG("20000818-1.c") == false)
    }

    @Test def test_20000825_1() {
        assert(checkCFG("20000825-1.c") == false)
    }

    @Test def test_20000827_1() {
        assert(checkCFG("20000827-1.c") == false)
    }

    @Test def test_20000922_1() {
        assert(checkCFG("20000922-1.c") == false)
    }

    @Test def test_20000923_1() {
        assert(checkCFG("20000923-1.c") == false)
    }

    @Ignore def test_20001018_1() {
        assert(checkCFG("20001018-1.c") == false)
    }

    // test fails; parser
    @Ignore def test_20001024_1() {
        assert(checkCFG("20001024-1.c") == false)
    }

    @Test def test_20001109_1() {
        assert(checkCFG("20001109-1.c") == false)
    }

    @Test def test_20001109_2() {
        assert(checkCFG("20001109-2.c") == false)
    }

    @Test def test_20001116_1() {
        assert(checkCFG("20001116-1.c") == false)
    }

    @Test def test_20001121_1() {
        assert(checkCFG("20001121-1.c") == false)
    }

    @Test def test_20001123_1() {
        assert(checkCFG("20001123-1.c") == false)
    }

    @Test def test_20001123_2() {
        assert(checkCFG("20001123-2.c") == false)
    }

    @Test def test_20001205_1() {
        assert(checkCFG("20001205-1.c") == false)
    }

    @Test def test_20001212_1() {
        assert(checkCFG("20001212-1.c") == false)
    }

    @Ignore def test_20001221_1() {
        assert(checkCFG("20001221-1.c") == false)
    }

    @Test def test_20001222_1() {
        assert(checkCFG("20001222-1.c") == false)
    }

    // test fails; kiama stackoverflow
    @Ignore def test_20001226_1() {
        assert(checkCFG("20001226-1.c") == false)
    }

    @Test def test_200031109_1() {
        assert(checkCFG("200031109-1.c") == false)
    }

    // test fails; parser
    @Ignore def test_20010102_1() {
        assert(checkCFG("20010102-1.c") == false)
    }

    @Test def test_20010107_1() {
        assert(checkCFG("20010107-1.c") == false)
    }

    @Test def test_20010112_1() {
        assert(checkCFG("20010112-1.c") == false)
    }

    @Test def test_20010113_1() {
        assert(checkCFG("20010113-1.c") == false)
    }

    @Test def test_20010114_1() {
        assert(checkCFG("20010114-1.c") == false)
    }

    // test fails; parser File not found: stdbool.h
    @Ignore def test_20010114_2() {
        assert(checkCFG("20010114-2.c") == false)
    }

    @Test def test_20010117_1() {
        assert(checkCFG("20010117-1.c") == false)
    }

    @Test def test_20010117_2() {
        assert(checkCFG("20010117-2.c") == false)
    }

    @Test def test_20010118_1() {
        assert(checkCFG("20010118-1.c") == false)
    }

    // test fails; parser
    @Ignore def test_20010124_1() {
        assert(checkCFG("20010124-1.c") == false)
    }

    @Test def test_20010202_1() {
        assert(checkCFG("20010202-1.c") == false)
    }

    @Test def test_20010209_1() {
        assert(checkCFG("20010209-1.c") == false)
    }

    @Test def test_20010226_1() {
        assert(checkCFG("20010226-1.c") == false)
    }

    @Test def test_20010227_1() {
        assert(checkCFG("20010227-1.c") == false)
    }

    @Test def test_20010313_1() {
        assert(checkCFG("20010313-1.c") == false)
    }

    @Test def test_20010320_1() {
        assert(checkCFG("20010320-1.c") == false)
    }

    @Test def test_20010326_1() {
        assert(checkCFG("20010326-1.c") == false)
    }

    @Test def test_20010327_1() {
        assert(checkCFG("20010327-1.c") == false)
    }

    @Test def test_20010328_1() {
        assert(checkCFG("20010328-1.c") == false)
    }

    @Test def test_20010329_1() {
        assert(checkCFG("20010329-1.c") == false)
    }

    // test fails; parser File not found limits.h
    @Ignore def test_20010404_1() {
        assert(checkCFG("20010404-1.c") == false)
    }

    // test fails; parser File not found stdbool.h
    @Ignore def test_20010423_1() {
        assert(checkCFG("20010423-1.c") == false)
    }

    @Ignore def test_20010408_1() {
        assert(checkCFG("20010408-1.c") == false)
    }

    @Test def test_20010421_1() {
        assert(checkCFG("20010421-1.c") == false)
    }

    @Test def test_20010426_1() {
        assert(checkCFG("20010426-1.c") == false)
    }

    @Test def test_20010503_1() {
        assert(checkCFG("20010503-1.c") == false)
    }

    @Test def test_20010510_1() {
        assert(checkCFG("20010510-1.c") == false)
    }

    @Test def test_20010516_1() {
        assert(checkCFG("20010516-1.c") == false)
    }

    @Ignore def test_20010518_1() {
        assert(checkCFG("20010518-1.c") == false)
    }

    // test fails; parser File not found limits.h
    @Ignore def test_20010518_2() {
        assert(checkCFG("20010518-2.c") == false)
    }

    @Test def test_20010525_1() {
        assert(checkCFG("20010525-1.c") == false)
    }

    // test fails; nested function definition
    @Ignore def test_20010605_1() {
        assert(checkCFG("20010605-1.c") == false)
    }

    @Test def test_20010605_2() {
        assert(checkCFG("20010605-2.c") == false)
    }

    @Test def test_20010605_3() {
        assert(checkCFG("20010605-3.c") == false)
    }

    // test fails; parser File not found stdbool.h
    @Ignore def test_20010610_1() {
        assert(checkCFG("20010610-1.c") == false)
    }

    @Test def test_20010611_1() {
        assert(checkCFG("20010611-1.c") == false)
    }

    @Test def test_20010701_1() {
        assert(checkCFG("20010701-1.c") == false)
    }

    @Test def test_20010706_1() {
        assert(checkCFG("20010706-1.c") == false)
    }

    @Test def test_20010711_1() {
        assert(checkCFG("20010711-1.c") == false)
    }

    @Test def test_20010711_2() {
        assert(checkCFG("20010711-2.c") == false)
    }

    // test fails; parser
    @Ignore def test_20010714_1() {
        assert(checkCFG("20010714-1.c") == false)
    }

    @Test def test_20010824_1() {
        assert(checkCFG("20010824-1.c") == false)
    }

    @Test def test_20010903_1() {
        assert(checkCFG("20010903-1.c") == false)
    }

    // test fails; nested function definition
    @Ignore def test_20010903_2() {
        assert(checkCFG("20010903-2.c") == false)
    }

    @Test def test_20010911_1() {
        assert(checkCFG("20010911-1.c") == false)
    }

    @Test def test_20011010_1() {
        assert(checkCFG("20011010-1.c") == false)
    }

    @Test def test_20011023_1() {
        assert(checkCFG("20011023-1.c") == false)
    }

    @Test def test_20011029_1() {
        assert(checkCFG("20011029-1.c") == false)
    }

    @Test def test_20011106_1() {
        assert(checkCFG("20011106-1.c") == false)
    }

    @Test def test_20011106_2() {
        assert(checkCFG("20011106-2.c") == false)
    }

    // test fails
    @Ignore def test_20011109_1() {
        assert(checkCFG("20011109-1.c") == false)
    }

    // test fails; parser
    @Ignore def test_20011114_1() {
        assert(checkCFG("20011114-1.c") == false)
    }

    @Test def test_20011114_2() {
        assert(checkCFG("20011114-2.c") == false)
    }

    // test fails; parser
    @Ignore def test_20011114_3() {
        assert(checkCFG("20011114-3.c") == false)
    }

    @Test def test_20011114_4() {
        assert(checkCFG("20011114-4.c") == false)
    }

    @Test def test_20011119_1() {
        assert(checkCFG("20011119-1.c") == false)
    }

    @Test def test_20011119_2() {
        assert(checkCFG("20011119-2.c") == false)
    }

    @Ignore def test_20011130_1() {
        assert(checkCFG("20011130-1.c") == false)
    }

    @Ignore def test_20011130_2() {
        assert(checkCFG("20011130-2.c") == false)
    }

    @Test def test_20011205_1() {
        assert(checkCFG("20011205-1.c") == false)
    }

    @Test def test_20011217_1() {
        assert(checkCFG("20011217-1.c") == false)
    }

    @Test def test_20011217_2() {
        assert(checkCFG("20011217-2.c") == false)
    }

    @Test def test_20011218_1() {
        assert(checkCFG("20011218-1.c") == false)
    }

    @Ignore def test_20011219_1() {
        assert(checkCFG("20011219-1.c") == false)
    }

    @Ignore def test_20011219_2() {
        assert(checkCFG("20011219-2.c") == false)
    }

    @Ignore def test_20011229_1() {
        assert(checkCFG("20011229-1.c") == false)
    }

    @Test def test_20011229_2() {
        assert(checkCFG("20011229-2.c") == false)
    }

    @Test def test_20020103_1() {
        assert(checkCFG("20020103-1.c") == false)
    }

    @Test def test_20020106_1() {
        assert(checkCFG("20020106-1.c") == false)
    }

    @Test def test_20020109_1() {
        assert(checkCFG("20020109-1.c") == false)
    }

    @Test def test_20020109_2() {
        assert(checkCFG("20020109-2.c") == false)
    }

    @Test def test_20020110() {
        assert(checkCFG("20020110.c") == false)
    }

    @Test def test_20020116_1() {
        assert(checkCFG("20020116-1.c") == false)
    }

    @Test def test_20020120_1() {
        assert(checkCFG("20020120-1.c") == false)
    }

    @Test def test_20020121_1() {
        assert(checkCFG("20020121-1.c") == false)
    }

    @Test def test_20020129_1() {
        assert(checkCFG("20020129-1.c") == false)
    }

    @Ignore def test_20020206_1() {
        assert(checkCFG("20020206-1.c") == false)
    }

    @Test def test_20020210_1() {
        assert(checkCFG("20020210-1.c") == false)
    }

    @Test def test_20020303_1() {
        assert(checkCFG("20020303-1.c") == false)
    }

    // test fails -- goto
    @Ignore def test_20020304_1() {
        assert(checkCFG("20020304-1.c") == false)
    }

    @Test def test_20020304_2() {
        assert(checkCFG("20020304-2.c") == false)
    }

    @Test def test_20020309_1() {
        assert(checkCFG("20020309-1.c") == false)
    }

    @Test def test_20020309_2() {
        assert(checkCFG("20020309-2.c") == false)
    }

    @Test def test_20020312_1() {
        assert(checkCFG("20020312-1.c") == false)
    }

    // test fails; goto
    @Ignore def test_20020314_1() {
        assert(checkCFG("20020314-1.c") == false)
    }

    @Test def test_20020315_1() {
        assert(checkCFG("20020315-1.c") == false)
    }

    @Test def test_20020318_1() {
        assert(checkCFG("20020318-1.c") == false)
    }

    @Test def test_20020319_1() {
        assert(checkCFG("20020319-1.c") == false)
    }

    @Test def test_20020320_1() {
        assert(checkCFG("20020320-1.c") == false)
    }

    @Test def test_20020323_1() {
        assert(checkCFG("20020323-1.c") == false)
    }

    @Test def test_20020330_1() {
        assert(checkCFG("20020330-1.c") == false)
    }

    // test fails; parser File not found limits.h
    @Ignore def test_20020409_1() {
        assert(checkCFG("20020409-1.c") == false)
    }

    @Test def test_20020415_1() {
        assert(checkCFG("20020415-1.c") == false)
    }

    @Test def test_20020418_1() {
        assert(checkCFG("20020418-1.c") == false)
    }

    @Test def test_20020530_1() {
        assert(checkCFG("20020530-1.c") == false)
    }

    @Ignore def test_20020604_1() {
        assert(checkCFG("20020604-1.c") == false)
    }

    @Test def test_20020605_1() {
        assert(checkCFG("20020605-1.c") == false)
    }

    // test fails; parser
    @Ignore def test_20020701_1() {
        assert(checkCFG("20020701-1.c") == false)
    }

    @Test def test_20020706_1() {
        assert(checkCFG("20020706-1.c") == false)
    }

    @Test def test_20020706_2() {
        assert(checkCFG("20020706-2.c") == false)
    }

    @Test def test_20020709_1() {
        assert(checkCFG("20020709-1.c") == false)
    }

    @Test def test_20020710_1() {
        assert(checkCFG("20020710-1.c") == false)
    }

    @Test def test_20020715_1() {
        assert(checkCFG("20020715-1.c") == false)
    }

    // test fails
    @Ignore def test_20020807_1() {
        assert(checkCFG("20020807-1.c") == false)
    }

    @Test def test_20020910_1() {
        assert(checkCFG("20020910-1.c") == false)
    }

    @Test def test_20020926_1() {
        assert(checkCFG("20020926-1.c") == false)
    }

    @Test def test_20020927_1() {
        assert(checkCFG("20020927-1.c") == false)
    }

    @Test def test_20020930_1() {
        assert(checkCFG("20020930-1.c") == false)
    }

    @Test def test_20021001_1() {
        assert(checkCFG("20021001-1.c") == false)
    }

    @Test def test_20021007_1() {
        assert(checkCFG("20021007-1.c") == false)
    }

    @Test def test_20021008_1() {
        assert(checkCFG("20021008-1.c") == false)
    }

    @Test def test_20021015_1() {
        assert(checkCFG("20021015-1.c") == false)
    }

    @Test def test_20021015_2() {
        assert(checkCFG("20021015-2.c") == false)
    }

    @Test def test_20021103_1() {
        assert(checkCFG("20021103-1.c") == false)
    }

    // test fails; dead code
    @Ignore def test_20021108_1() {
        assert(checkCFG("20021108-1.c"))
    }

    @Test def test_20021110() {
        assert(checkCFG("20021110.c") == false)
    }

    @Test def test_20021119_1() {
        assert(checkCFG("20021119-1.c") == false)
    }

    @Test def test_20021120_1() {
        assert(checkCFG("20021120-1.c") == false)
    }

    @Test def test_20021120_2() {
        assert(checkCFG("20021120-2.c") == false)
    }

    @Test def test_20021123_1() {
        assert(checkCFG("20021123-1.c") == false)
    }

    @Test def test_20021123_4() {
        assert(checkCFG("20021123-4.c") == false)
    }

    @Test def test_20021124_1() {
        assert(checkCFG("20021124-1.c") == false)
    }

    // test fails
    @Ignore def test_20021204_1() {
        assert(checkCFG("20021204-1.c"))
    }

    @Test def test_20021205_1() {
        assert(checkCFG("20021205-1.c") == false)
    }

    @Test def test_20021212_1() {
        assert(checkCFG("20021212-1.c") == false)
    }

    @Test def test_20021230_1() {
        assert(checkCFG("20021230-1.c") == false)
    }

    @Test def test_20030109_1() {
        assert(checkCFG("20030109-1.c") == false)
    }

    @Test def test_20030110_1() {
        assert(checkCFG("20030110-1.c") == false)
    }

    // test fails
    @Ignore def test_20030125_1() {
        assert(checkCFG("20030125-1.c"))
    }

    @Test def test_20030206_1() {
        assert(checkCFG("20030206-1.c") == false)
    }

    @Test def test_20030216_1() {
        assert(checkCFG("20030216-1.c") == false)
    }

    @Test def test_20030219_1() {
        assert(checkCFG("20030219-1.c") == false)
    }

    @Test def test_20030220_1() {
        assert(checkCFG("20030220-1.c") == false)
    }

    @Ignore def test_20030224_1() {
        assert(checkCFG("20030224-1.c") == false)
    }

    @Test def test_20030305_1() {
        assert(checkCFG("20030305-1.c") == false)
    }

    @Test def test_20030314_1() {
        assert(checkCFG("20030314-1.c") == false)
    }

    @Test def test_20030319_1() {
        assert(checkCFG("20030319-1.c") == false)
    }

    @Test def test_20030320_1() {
        assert(checkCFG("20030320-1.c") == false)
    }

    // test fails
    @Ignore def test_20030323_1() {
        assert(checkCFG("20030323-1.c"))
    }

    @Test def test_20030330_1() {
        assert(checkCFG("20030330-1.c") == false)
    }

    @Test def test_20030331_1() {
        assert(checkCFG("20030331-1.c") == false)
    }

    @Test def test_20030405_1() {
        assert(checkCFG("20030405-1.c") == false)
    }

    @Test def test_20030410_1() {
        assert(checkCFG("20030410-1.c") == false)
    }

    @Test def test_20030415_1() {
        assert(checkCFG("20030415-1.c") == false)
    }

    @Test def test_20030418_1() {
        assert(checkCFG("20030418-1.c") == false)
    }

    // test fails -- goto
    @Ignore def test_20030503_1() {
        assert(checkCFG("20030503-1.c"))
    }

    @Test def test_20030518_1() {
        assert(checkCFG("20030518-1.c") == false)
    }

    @Test def test_20030604_1() {
        assert(checkCFG("20030604-1.c") == false)
    }

    @Test def test_20030605_1() {
        assert(checkCFG("20030605-1.c") == false)
    }

    @Test def test_20030612_1() {
        assert(checkCFG("20030612-1.c") == false)
    }

    @Test def test_20030624_1() {
        assert(checkCFG("20030624-1.c") == false)
    }

    @Test def test_20030703_1() {
        assert(checkCFG("20030703-1.c") == false)
    }

    @Test def test_20030704_1() {
        assert(checkCFG("20030704-1.c") == false)
    }

    @Test def test_20030707_1() {
        assert(checkCFG("20030707-1.c") == false)
    }

    @Test def test_20030708_1() {
        assert(checkCFG("20030708-1.c") == false)
    }

    @Test def test_20030725_1() {
        assert(checkCFG("20030725-1.c") == false)
    }

    @Test def test_20030804_1() {
        assert(checkCFG("20030804-1.c") == false)
    }

    @Test def test_20030821_1() {
        assert(checkCFG("20030821-1.c") == false)
    }

    @Test def test_20030903_1() {
        assert(checkCFG("20030903-1.c") == false)
    }

    @Ignore def test_20030904_1() {
        assert(checkCFG("20030904-1.c") == false)
    }

    @Test def test_20030907_1() {
        assert(checkCFG("20030907-1.c") == false)
    }

    @Test def test_20030921_1() {
        assert(checkCFG("20030921-1.c") == false)
    }

    @Test def test_20031002_1() {
        assert(checkCFG("20031002-1.c") == false)
    }

    @Test def test_20031010_1() {
        assert(checkCFG("20031010-1.c") == false)
    }

    @Test def test_20031011_1() {
        assert(checkCFG("20031011-1.c") == false)
    }

    @Test def test_20031011_2() {
        assert(checkCFG("20031011-2.c") == false)
    }

    // test fails; parser File not found limits.h
    @Ignore def test_20031023_1() {
        assert(checkCFG("20031023-1.c"))
    }

    // test fails; parser File not found 20031023-1.c
    @Ignore def test_20031023_2() {
        assert(checkCFG("20031023-2.c"))
    }

    // test fails; parser File not found 20031023-1.c
    @Ignore def test_20031023_3() {
        assert(checkCFG("20031023-3.c"))
    }

    // test fails; parser File not found 20031023-1.c
    @Ignore def test_20031023_4() {
        assert(checkCFG("20031023-4.c"))
    }

    // test fails
    @Ignore def test_20031031_1() {
        assert(checkCFG("20031031-1.c"))
    }

    @Test def test_20031031_2() {
        assert(checkCFG("20031031-2.c") == false)
    }

    @Test def test_20031102_1() {
        assert(checkCFG("20031102-1.c") == false)
    }

    @Test def test_20031112_1() {
        assert(checkCFG("20031112-1.c") == false)
    }

    @Test def test_20031113_1() {
        assert(checkCFG("20031113-1.c") == false)
    }

    @Test def test_20031208_1() {
        assert(checkCFG("20031208-1.c") == false)
    }

    @Ignore def test_20031220_1() {
        assert(checkCFG("20031220-1.c") == false)
    }

    @Test def test_20031220_2() {
        assert(checkCFG("20031220-2.c") == false)
    }

    @Test def test_20031227_1() {
        assert(checkCFG("20031227-1.c") == false)
    }

    // test fails; continue statement
    @Ignore def test_20031231_1() {
        assert(checkCFG("20031231-1.c"))
    }

    @Test def test_20040101_1() {
        assert(checkCFG("20040101-1.c") == false)
    }

    // test fails; default before case; no breaks
    @Ignore def test_20040109_1() {
        assert(checkCFG("20040109-1.c"))
    }

    @Test def test_20040121_1() {
        assert(checkCFG("20040121-1.c") == false)
    }

    @Test def test_20040130_1() {
        assert(checkCFG("20040130-1.c") == false)
    }

    @Test def test_20040209_1() {
        assert(checkCFG("20040209-1.c") == false)
    }

    @Test def test_20040214_1() {
        assert(checkCFG("20040214-1.c") == false)
    }

    @Test def test_20040214_2() {
        assert(checkCFG("20040214-2.c") == false)
    }

    @Ignore def test_20040304_1() {
        assert(checkCFG("20040304-1.c") == false)
    }

    @Test def test_20040531_1() {
        assert(checkCFG("20040531-1.c") == false)
    }

    @Test def test_20040602_1() {
        assert(checkCFG("20040602-1.c") == false)
    }

    @Test def test_20040705_1() {
        assert(checkCFG("20040705-1.c") == false)
    }

    // test fails; dead code break after return
    @Ignore def test_20040708_1() {
        assert(checkCFG("20040708-1.c"))
    }

    @Test def test_20040726_1() {
        assert(checkCFG("20040726-1.c") == false)
    }

    @Test def test_20041007_1() {
        assert(checkCFG("20041007-1.c") == false)
    }

    @Test def test_20041018_1() {
        assert(checkCFG("20041018-1.c") == false)
    }

    @Test def test_20041119_1() {
        assert(checkCFG("20041119-1.c") == false)
    }

    @Test def test_20050105_1() {
        assert(checkCFG("20050105-1.c") == false)
    }

    @Test def test_20050113_1() {
        assert(checkCFG("20050113-1.c") == false)
    }

    @Test def test_900116_1() {
        assert(checkCFG("900116-1.c") == false)
    }

    @Test def test_900216_1() {
        assert(checkCFG("900216-1.c") == false)
    }

    @Test def test_900313_1() {
        assert(checkCFG("900313-1.c") == false)
    }

    @Test def test_900407_1() {
        assert(checkCFG("900407-1.c") == false)
    }

    @Test def test_900516_1() {
        assert(checkCFG("900516-1.c") == false)
    }

    @Test def test_920301_1() {
        assert(checkCFG("920301-1.c") == false)
    }

    @Ignore def test_920409_1() {
        assert(checkCFG("920409-1.c") == false)
    }

    @Ignore def test_920409_2() {
        assert(checkCFG("920409-2.c") == false)
    }

    @Test def test_920410_1() {
        assert(checkCFG("920410-1.c") == false)
    }

    @Test def test_920410_2() {
        assert(checkCFG("920410-2.c") == false)
    }

    @Ignore def test_920411_2() {
        assert(checkCFG("920411-2.c") == false)
    }

    @Test def test_920413_1() {
        assert(checkCFG("920413-1.c") == false)
    }

    // test fails; nested function
    @Ignore def test_920415_1() {
        assert(checkCFG("920415-1.c"))
    }

    @Test def test_920428_1() {
        assert(checkCFG("920428-1.c") == false)
    }

    @Ignore def test_920428_2() {
        assert(checkCFG("920428-2.c") == false)
    }

    @Test def test_920428_3() {
        assert(checkCFG("920428-3.c") == false)
    }

    @Test def test_920428_4() {
        assert(checkCFG("920428-4.c") == false)
    }

    @Test def test_920428_5() {
        assert(checkCFG("920428-5.c") == false)
    }

    @Test def test_920428_6() {
        assert(checkCFG("920428-6.c") == false)
    }

    @Test def test_920428_7() {
        assert(checkCFG("920428-7.c") == false)
    }

    @Test def test_920501_10() {
        assert(checkCFG("920501-10.c") == false)
    }

    @Test def test_920501_11() {
        assert(checkCFG("920501-11.c") == false)
    }

    @Test def test_920501_12() {
        assert(checkCFG("920501-12.c") == false)
    }

    @Test def test_920501_13() {
        assert(checkCFG("920501-13.c") == false)
    }

    @Test def test_920501_15() {
        assert(checkCFG("920501-15.c") == false)
    }

    @Test def test_920501_16() {
        assert(checkCFG("920501-16.c") == false)
    }

    @Test def test_920501_17() {
        assert(checkCFG("920501-17.c") == false)
    }

    // test fails
    @Test def test_920501_18() {
        assert(checkCFG("920501-18.c") == false)
    }

    @Test def test_920501_19() {
        assert(checkCFG("920501-19.c") == false)
    }

    @Test def test_920501_1() {
        assert(checkCFG("920501-1.c") == false)
    }

    @Test def test_920501_20() {
        assert(checkCFG("920501-20.c") == false)
    }

    @Test def test_920501_21() {
        assert(checkCFG("920501-21.c") == false)
    }

    @Test def test_920501_22() {
        assert(checkCFG("920501-22.c") == false)
    }

    // test fails
    @Test def test_920501_23() {
        assert(checkCFG("920501-23.c") == false)
    }

    @Test def test_920501_2() {
        assert(checkCFG("920501-2.c") == false)
    }

    @Test def test_920501_3() {
        assert(checkCFG("920501-3.c") == false)
    }

    // test fails; handling else block
    @Ignore def test_920501_4() {
        assert(checkCFG("920501-4.c"))
    }

    @Test def test_920501_6() {
        assert(checkCFG("920501-6.c") == false)
    }

    @Test def test_920501_7() {
        assert(checkCFG("920501-7.c") == false)
    }

    @Test def test_920501_8() {
        assert(checkCFG("920501-8.c") == false)
    }

    @Test def test_920501_9() {
        assert(checkCFG("920501-9.c") == false)
    }

    @Test def test_920502_1() {
        assert(checkCFG("920502-1.c") == false)
    }

    @Test def test_920502_2() {
        assert(checkCFG("920502-2.c") == false)
    }

    @Test def test_920520_1() {
        assert(checkCFG("920520-1.c") == false)
    }

    @Test def test_920521_1() {
        assert(checkCFG("920521-1.c") == false)
    }

    @Test def test_920529_1() {
        assert(checkCFG("920529-1.c") == false)
    }

    @Test def test_920608_1() {
        assert(checkCFG("920608-1.c") == false)
    }

    @Test def test_920611_2() {
        assert(checkCFG("920611-2.c") == false)
    }

    @Test def test_920615_1() {
        assert(checkCFG("920615-1.c") == false)
    }

    @Test def test_920617_1() {
        assert(checkCFG("920617-1.c") == false)
    }

    @Test def test_920617_2() {
        assert(checkCFG("920617-2.c") == false)
    }

    @Test def test_920623_1() {
        assert(checkCFG("920623-1.c") == false)
    }

    // test fails; switch without default and breaks
    @Ignore def test_920624_1() {
        assert(checkCFG("920624-1.c"))
    }

    // test fails; continue (filter break statements)
    @Ignore def test_920625_1() {
        assert(checkCFG("920625-1.c"))
    }

    @Ignore def test_920625_2() {
        assert(checkCFG("920625-2.c") == false)
    }

    @Test def test_920626_1() {
        assert(checkCFG("920626-1.c") == false)
    }

    @Test def test_920701_1() {
        assert(checkCFG("920701-1.c") == false)
    }

    @Test def test_920702_1() {
        assert(checkCFG("920702-1.c") == false)
    }

    @Test def test_920706_1() {
        assert(checkCFG("920706-1.c") == false)
    }

    @Test def test_920710_2() {
        assert(checkCFG("920710-2.c") == false)
    }

    @Test def test_920711_1() {
        assert(checkCFG("920711-1.c") == false)
    }

    @Test def test_920721_1() {
        assert(checkCFG("920721-1.c") == false)
    }

    @Test def test_920723_1() {
        assert(checkCFG("920723-1.c") == false)
    }

    // test fails; infinite loop
    @Ignore def test_920729_1() {
        assert(checkCFG("920729-1.c"))
    }

    @Test def test_920806_1() {
        assert(checkCFG("920806-1.c") == false)
    }

    @Test def test_920808_1() {
        assert(checkCFG("920808-1.c") == false)
    }

    @Test def test_920809_1() {
        assert(checkCFG("920809-1.c") == false)
    }

    // test fails
    @Test def test_920817_1() {
        assert(checkCFG("920817-1.c") == false)
    }

    @Test def test_920820_1() {
        assert(checkCFG("920820-1.c") == false)
    }

    @Test def test_920821_1() {
        assert(checkCFG("920821-1.c") == false)
    }

    @Test def test_920821_2() {
        assert(checkCFG("920821-2.c") == false)
    }

    @Test def test_920825_1() {
        assert(checkCFG("920825-1.c") == false)
    }

    @Test def test_920825_2() {
        assert(checkCFG("920825-2.c") == false)
    }

    @Test def test_920826_1() {
        assert(checkCFG("920826-1.c") == false)
    }

    @Test def test_920828_1() {
        assert(checkCFG("920828-1.c") == false)
    }

    @Test def test_920829_1() {
        assert(checkCFG("920829-1.c") == false)
    }

    @Test def test_920831_1() {
        assert(checkCFG("920831-1.c") == false)
    }

    @Test def test_920902_1() {
        assert(checkCFG("920902-1.c") == false)
    }

    @Test def test_920909_1() {
        assert(checkCFG("920909-1.c") == false)
    }

    // test fails; switch without default can case
    @Ignore def test_920917_1() {
        assert(checkCFG("920917-1.c"))
    }

    @Test def test_920928_1() {
        assert(checkCFG("920928-1.c") == false)
    }

    // test fails;
    @Ignore def test_920928_2() {
        assert(checkCFG("920928-2.c") == false)
    }

    // test fails
    @Test def test_920928_3() {
        assert(checkCFG("920928-3.c") == false)
    }

    @Test def test_920928_4() {
        assert(checkCFG("920928-4.c") == false)
    }

    @Test def test_920928_5() {
        assert(checkCFG("920928-5.c") == false)
    }

    @Test def test_920928_6() {
        assert(checkCFG("920928-6.c") == false)
    }

    @Test def test_921004_1() {
        assert(checkCFG("921004-1.c") == false)
    }

    @Ignore def test_921011_1() {
        assert(checkCFG("921011-1.c"))
    }

    @Ignore def test_921011_2() {
        assert(checkCFG("921011-2.c") == false)
    }

    @Test def test_921012_1() {
        assert(checkCFG("921012-1.c") == false)
    }

    @Test def test_921012_2() {
        assert(checkCFG("921012-2.c") == false)
    }

    @Ignore def test_921013_1() {
        assert(checkCFG("921013-1.c") == false)
    }

    @Test def test_921019_1() {
        assert(checkCFG("921019-1.c") == false)
    }

    @Test def test_921021_1() {
        assert(checkCFG("921021-1.c") == false)
    }

    @Test def test_921024_1() {
        assert(checkCFG("921024-1.c") == false)
    }

    @Test def test_921026_1() {
        assert(checkCFG("921026-1.c") == false)
    }

    @Test def test_921103_1() {
        assert(checkCFG("921103-1.c") == false)
    }

    @Test def test_921109_1() {
        assert(checkCFG("921109-1.c") == false)
    }

    @Test def test_921111_1() {
        assert(checkCFG("921111-1.c") == false)
    }

    @Test def test_921116_2() {
        assert(checkCFG("921116-2.c") == false)
    }

    @Test def test_921118_1() {
        assert(checkCFG("921118-1.c") == false)
    }

    @Test def test_921126_1() {
        assert(checkCFG("921126-1.c") == false)
    }

    // test fails; infinite loop
    @Ignore def test_921202_1() {
        assert(checkCFG("921202-1.c"))
    }

    // test fails
    @Test def test_921202_2() {
        assert(checkCFG("921202-2.c") == false)
    }

    @Test def test_921203_1() {
        assert(checkCFG("921203-1.c") == false)
    }

    @Test def test_921203_2() {
        assert(checkCFG("921203-2.c") == false)
    }

    @Ignore def test_921206_1() {
        assert(checkCFG("921206-1.c") == false)
    }

    @Test def test_921227_1() {
        assert(checkCFG("921227-1.c") == false)
    }

    @Test def test_930109_1() {
        assert(checkCFG("930109-1.c") == false)
    }

    @Test def test_930109_2() {
        assert(checkCFG("930109-2.c") == false)
    }

    @Test def test_930111_1() {
        assert(checkCFG("930111-1.c") == false)
    }

    @Test def test_930117_1() {
        assert(checkCFG("930117-1.c") == false)
    }

    @Test def test_930118_1() {
        assert(checkCFG("930118-1.c") == false)
    }

    // test fails
    @Test def test_930120_1() {
        assert(checkCFG("930120-1.c") == false)
    }

    @Test def test_930126_1() {
        assert(checkCFG("930126-1.c") == false)
    }

    // test fails
    @Test def test_930210_1() {
        assert(checkCFG("930210-1.c") == false)
    }

    @Test def test_930217_1() {
        assert(checkCFG("930217-1.c") == false)
    }

    @Test def test_930222_1() {
        assert(checkCFG("930222-1.c") == false)
    }

    @Test def test_930325_1() {
        assert(checkCFG("930325-1.c") == false)
    }

    @Test def test_930326_1() {
        assert(checkCFG("930326-1.c") == false)
    }

    // test fails; static gotos
    @Ignore def test_930411_1() {
        assert(checkCFG("930411-1.c"))
    }

    @Test def test_930421_1() {
        assert(checkCFG("930421-1.c") == false)
    }

    @Ignore def test_930427_2() {
        assert(checkCFG("930427-2.c") == false)
    }

    @Test def test_930503_1() {
        assert(checkCFG("930503-1.c") == false)
    }

    @Test def test_930503_2() {
        assert(checkCFG("930503-2.c") == false)
    }

    @Test def test_930506_1() {
        assert(checkCFG("930506-1.c") == false)
    }

    @Test def test_930506_2() {
        assert(checkCFG("930506-2.c") == false)
    }

    @Test def test_930510_1() {
        assert(checkCFG("930510-1.c") == false)
    }

    @Test def test_930513_1() {
        assert(checkCFG("930513-1.c") == false)
    }

    @Test def test_930513_2() {
        assert(checkCFG("930513-2.c") == false)
    }

    @Test def test_930513_3() {
        assert(checkCFG("930513-3.c") == false)
    }

    // test fails; infinite loop
    @Ignore def test_930523_1() {
        assert(checkCFG("930523-1.c"))
    }

    @Test def test_930525_1() {
        assert(checkCFG("930525-1.c") == false)
    }

    // test fails
    @Test def test_930527_1() {
        assert(checkCFG("930527-1.c") == false)
    }

    // test fails; goto hell
    @Ignore def test_930529_1() {
        assert(checkCFG("930529-1.c"))
    }

    @Test def test_930530_1() {
        assert(checkCFG("930530-1.c") == false)
    }

    @Test def test_930602_1() {
        assert(checkCFG("930602-1.c") == false)
    }

    @Test def test_930603_1() {
        assert(checkCFG("930603-1.c") == false)
    }

    @Test def test_930607_1() {
        assert(checkCFG("930607-1.c") == false)
    }

    @Test def test_930611_1() {
        assert(checkCFG("930611-1.c") == false)
    }

    @Test def test_930618_1() {
        assert(checkCFG("930618-1.c") == false)
    }

    // test fails
    @Ignore def test_930621_1() {
        assert(checkCFG("930621-1.c"))
    }

    @Test def test_930623_1() {
        assert(checkCFG("930623-1.c") == false)
    }

    @Test def test_930702_1() {
        assert(checkCFG("930702-1.c") == false)
    }

    @Test def test_930926_1() {
        assert(checkCFG("930926-1.c") == false)
    }

    // test fails; parser File not found stddef.h
    @Ignore def test_930927_1() {
        assert(checkCFG("930927-1.c"))
    }

    @Test def test_931003_1() {
        assert(checkCFG("931003-1.c") == false)
    }

    @Test def test_931004_1() {
        assert(checkCFG("931004-1.c") == false)
    }

    @Test def test_931013_1() {
        assert(checkCFG("931013-1.c") == false)
    }

    @Test def test_931013_2() {
        assert(checkCFG("931013-2.c") == false)
    }

    @Test def test_931013_3() {
        assert(checkCFG("931013-3.c") == false)
    }

    @Test def test_931018_1() {
        assert(checkCFG("931018-1.c") == false)
    }

    @Test def test_931031_1() {
        assert(checkCFG("931031-1.c") == false)
    }

    // test fails; loops
    @Ignore def test_931102_1() {
        assert(checkCFG("931102-1.c") == false)
    }

    @Test def test_931102_2() {
        assert(checkCFG("931102-2.c") == false)
    }

    @Test def test_931203_1() {
        assert(checkCFG("931203-1.c") == false)
    }

    @Ignore def test_940611_1() {
        assert(checkCFG("940611-1.c") == false)
    }

    @Test def test_940712_1() {
        assert(checkCFG("940712-1.c") == false)
    }

    // test fails
    @Ignore def test_940718_1() {
        assert(checkCFG("940718-1.c") == false)
    }

    @Test def test_941014_1() {
        assert(checkCFG("941014-1.c") == false)
    }

    @Test def test_941014_2() {
        assert(checkCFG("941014-2.c") == false)
    }

    @Ignore def test_941014_3() {
        assert(checkCFG("941014-3.c") == false)
    }

    @Test def test_941014_4() {
        assert(checkCFG("941014-4.c") == false)
    }

    @Test def test_941019_1() {
        assert(checkCFG("941019-1.c") == false)
    }

    @Test def test_941111_1() {
        assert(checkCFG("941111-1.c") == false)
    }

    @Test def test_941113_1() {
        assert(checkCFG("941113-1.c") == false)
    }

    @Test def test_950122_1() {
        assert(checkCFG("950122-1.c") == false)
    }

    // test fails
    @Ignore def test_950124_1() {
        assert(checkCFG("950124-1.c") == false)
    }

    @Ignore def test_950221_1() {
        assert(checkCFG("950221-1.c") == false)
    }

    // test fails
    @Ignore def test_950329_1() {
        assert(checkCFG("950329-1.c") == false)
    }

    @Test def test_950512_1() {
        assert(checkCFG("950512-1.c") == false)
    }

    @Test def test_950530_1() {
        assert(checkCFG("950530-1.c") == false)
    }

    @Ignore def test_950607_1() {
        assert(checkCFG("950607-1.c") == false)
    }

    @Test def test_950610_1() {
        assert(checkCFG("950610-1.c") == false)
    }

    // test fails
    @Ignore def test_950612_1() {
        assert(checkCFG("950612-1.c") == false)
    }

    // test fails
    @Ignore def test_950613_1() {
        assert(checkCFG("950613-1.c") == false)
    }

    @Test def test_950618_1() {
        assert(checkCFG("950618-1.c") == false)
    }

    @Test def test_950719_1() {
        assert(checkCFG("950719-1.c") == false)
    }

    // test fails
    @Ignore def test_950729_1() {
        assert(checkCFG("950729-1.c") == false)
    }

    @Test def test_950816_1() {
        assert(checkCFG("950816-1.c") == false)
    }

    @Ignore def test_950816_2() {
        assert(checkCFG("950816-2.c") == false)
    }

    @Test def test_950816_3() {
        assert(checkCFG("950816-3.c") == false)
    }

    @Test def test_950910_1() {
        assert(checkCFG("950910-1.c") == false)
    }

    // test fails; parser bad token
    @Ignore def test_950919_1() {
        assert(checkCFG("950919-1.c") == false)
    }

    @Test def test_950921_1() {
        assert(checkCFG("950921-1.c") == false)
    }

    @Ignore def test_950922_1() {
        assert(checkCFG("950922-1.c") == false)
    }

    @Test def test_951004_1() {
        assert(checkCFG("951004-1.c") == false)
    }

    @Test def test_951106_1() {
        assert(checkCFG("951106-1.c") == false)
    }

    // test fails nesting function
    @Ignore def test_951116_1() {
        assert(checkCFG("951116-1.c") == false)
    }

    @Test def test_951128_1() {
        assert(checkCFG("951128-1.c") == false)
    }

    @Test def test_951220_1() {
        assert(checkCFG("951220-1.c") == false)
    }

    @Test def test_951222_1() {
        assert(checkCFG("951222-1.c") == false)
    }

    @Test def test_960106_1() {
        assert(checkCFG("960106-1.c") == false)
    }

    @Test def test_960130_1() {
        assert(checkCFG("960130-1.c") == false)
    }

    @Test def test_960201_1() {
        assert(checkCFG("960201-1.c") == false)
    }

    @Test def test_960218_1() {
        assert(checkCFG("960218-1.c") == false)
    }

    @Test def test_960220_1() {
        assert(checkCFG("960220-1.c") == false)
    }

    @Test def test_960221_1() {
        assert(checkCFG("960221-1.c") == false)
    }

    @Test def test_960319_1() {
        assert(checkCFG("960319-1.c") == false)
    }

    @Test def test_960514_1() {
        assert(checkCFG("960514-1.c") == false)
    }

    @Test def test_960704_1() {
        assert(checkCFG("960704-1.c") == false)
    }

    @Test def test_960829_1() {
        assert(checkCFG("960829-1.c") == false)
    }

    @Test def test_961004_1() {
        assert(checkCFG("961004-1.c") == false)
    }

    @Test def test_961010_1() {
        assert(checkCFG("961010-1.c") == false)
    }

    @Test def test_961019_1() {
        assert(checkCFG("961019-1.c") == false)
    }

    @Test def test_961031_1() {
        assert(checkCFG("961031-1.c") == false)
    }

    // test fails
    @Ignore def test_961126_1() {
        assert(checkCFG("961126-1.c") == false)
    }

    @Test def test_961203_1() {
        assert(checkCFG("961203-1.c") == false)
    }

    @Test def test_970206_1() {
        assert(checkCFG("970206-1.c") == false)
    }

    // test fails; parser File not found stddef.h
    @Ignore def test_970214_1() {
        assert(checkCFG("970214-1.c") == false)
    }

    // test fails
    @Ignore def test_980329_1() {
        assert(checkCFG("980329-1.c") == false)
    }

    @Test def test_980408_1() {
        assert(checkCFG("980408-1.c") == false)
    }

    @Test def test_980504_1() {
        assert(checkCFG("980504-1.c") == false)
    }

    // test fails -- goto
    @Ignore def test_980506_1() {
        assert(checkCFG("980506-1.c") == false)
    }

    // test fails
    @Ignore def test_980506_2() {
        assert(checkCFG("980506-2.c") == false)
    }

    @Test def test_980511_1() {
        assert(checkCFG("980511-1.c") == false)
    }

    @Test def test_980701_1() {
        assert(checkCFG("980701-1.c") == false)
    }

    @Test def test_980706_1() {
        assert(checkCFG("980706-1.c") == false)
    }

    @Test def test_980726_1() {
        assert(checkCFG("980726-1.c") == false)
    }

    @Test def test_980729_1() {
        assert(checkCFG("980729-1.c") == false)
    }

    // test fails
    @Ignore def test_980816_1() {
        assert(checkCFG("980816-1.c") == false)
    }

    // test fails
    @Ignore def test_980821_1() {
        assert(checkCFG("980821-1.c") == false)
    }

    @Test def test_980825_1() {
        assert(checkCFG("980825-1.c") == false)
    }

    @Test def test_981001_1() {
        assert(checkCFG("981001-1.c") == false)
    }

    @Test def test_981001_2() {
        assert(checkCFG("981001-2.c") == false)
    }

    @Test def test_981001_3() {
        assert(checkCFG("981001-3.c") == false)
    }

    @Test def test_981001_4() {
        assert(checkCFG("981001-4.c") == false)
    }

    @Test def test_981006_1() {
        assert(checkCFG("981006-1.c") == false)
    }

    @Ignore def test_981007_1() {
        assert(checkCFG("981007-1.c") == false)
    }

    @Ignore def test_981022_1() {
        assert(checkCFG("981022-1.c") == false)
    }

    @Test def test_981107_1() {
        assert(checkCFG("981107-1.c") == false)
    }

    @Test def test_981223_1() {
        assert(checkCFG("981223-1.c") == false)
    }

    @Test def test_990107_1() {
        assert(checkCFG("990107-1.c") == false)
    }

    @Ignore def test_990117_1() {
        assert(checkCFG("990117-1.c") == false)
    }

    @Test def test_990203_1() {
        assert(checkCFG("990203-1.c") == false)
    }

    @Ignore def test_990517_1() {
        assert(checkCFG("990517-1.c") == false)
    }

    @Test def test_990519_1() {
        assert(checkCFG("990519-1.c") == false)
    }

    @Test def test_990523_1() {
        assert(checkCFG("990523-1.c") == false)
    }

    @Test def test_990527_1() {
        assert(checkCFG("990527-1.c") == false)
    }

    @Test def test_990617_1() {
        assert(checkCFG("990617-1.c") == false)
    }

    // test fails; parser File not found string.h
    @Ignore def test_990625_1() {
        assert(checkCFG("990625-1.c") == false)
    }

    @Test def test_990625_2() {
        assert(checkCFG("990625-2.c") == false)
    }

    // test fails
    @Ignore def test_990801_1() {
        assert(checkCFG("990801-1.c") == false)
    }

    @Test def test_990801_2() {
        assert(checkCFG("990801-2.c") == false)
    }

    @Test def test_990829_1() {
        assert(checkCFG("990829-1.c") == false)
    }

    @Test def test_990913_1() {
        assert(checkCFG("990913-1.c") == false)
    }

    @Test def test_990928_1() {
        assert(checkCFG("990928-1.c") == false)
    }

    @Test def test_991008_1() {
        assert(checkCFG("991008-1.c") == false)
    }

    @Test def test_991026_1() {
        assert(checkCFG("991026-1.c") == false)
    }

    @Test def test_991026_2() {
        assert(checkCFG("991026-2.c") == false)
    }

    @Test def test_991127_1() {
        assert(checkCFG("991127-1.c") == false)
    }

    @Test def test_991202_1() {
        assert(checkCFG("991202-1.c") == false)
    }

    @Test def test_991208_1() {
        assert(checkCFG("991208-1.c") == false)
    }

    @Test def test_991213_1() {
        assert(checkCFG("991213-1.c") == false)
    }

    @Ignore def test_991213_2() {
        assert(checkCFG("991213-2.c") == false)
    }

    @Test def test_991213_3() {
        assert(checkCFG("991213-3.c") == false)
    }

    @Test def test_991214_1() {
        assert(checkCFG("991214-1.c") == false)
    }

    @Test def test_991214_2() {
        assert(checkCFG("991214-2.c") == false)
    }

    @Test def test_991229_1() {
        assert(checkCFG("991229-1.c") == false)
    }

    @Test def test_991229_2() {
        assert(checkCFG("991229-2.c") == false)
    }

    @Test def test_991229_3() {
        assert(checkCFG("991229-3.c") == false)
    }

    @Test def test_calls() {
        assert(checkCFG("calls.c") == false)
    }

    @Test def test_cmpdi_1() {
        assert(checkCFG("cmpdi-1.c") == false)
    }

    @Test def test_combine_hang() {
        assert(checkCFG("combine-hang.c") == false)
    }

    @Test def test_complex_1() {
        assert(checkCFG("complex-1.c") == false)
    }

    @Test def test_cpp_1() {
        assert(checkCFG("cpp-1.c") == false)
    }

    @Test def test_cpp_2() {
        assert(checkCFG("cpp-2.c") == false)
    }

    @Ignore def test_dll() {
        assert(checkCFG("dll.c") == false)
    }

    @Test def test_fix_trunc_mem_1() {
        assert(checkCFG("fix-trunc-mem-1.c") == false)
    }

    @Test def test_funcptr_1() {
        assert(checkCFG("funcptr-1.c") == false)
    }

    // test fails
    @Ignore def test_goto_1() {
        assert(checkCFG("goto-1.c") == false)
    }

    @Test def test_iftrap_1() {
        assert(checkCFG("iftrap-1.c") == false)
    }

    @Test def test_iftrap_2() {
        assert(checkCFG("iftrap-2.c") == false)
    }

    @Test def test_init_1() {
        assert(checkCFG("init-1.c") == false)
    }

    @Test def test_init_2() {
        assert(checkCFG("init-2.c") == false)
    }

    @Test def test_init_3() {
        assert(checkCFG("init-3.c") == false)
    }

    @Test def test_inline_1() {
        assert(checkCFG("inline-1.c") == false)
    }

    // test fails -- unreachable code
    @Ignore def test_labels_1() {
        assert(checkCFG("labels-1.c") == false)
    }

    @Ignore def test_labels_2() {
        assert(checkCFG("labels-2.c") == false)
    }

    @Test def test_labels_3() {
        assert(checkCFG("labels-3.c") == false)
    }

    @Test def test_libcall_1() {
        assert(checkCFG("libcall-1.c") == false)
    }

    @Test def test_mangle_1() {
        assert(checkCFG("mangle-1.c") == false)
    }

    @Test def test_mipscop_1() {
        assert(checkCFG("mipscop-1.c") == false)
    }

    @Test def test_mipscop_2() {
        assert(checkCFG("mipscop-2.c") == false)
    }

    @Test def test_mipscop_3() {
        assert(checkCFG("mipscop-3.c") == false)
    }

    @Test def test_mipscop_4() {
        assert(checkCFG("mipscop-4.c") == false)
    }

    @Test def test_packed_1() {
        assert(checkCFG("packed-1.c") == false)
    }

    @Test def test_pr13889() {
        assert(checkCFG("pr13889.c") == false)
    }

    @Test def test_pr16566_1() {
        assert(checkCFG("pr16566-1.c") == false)
    }

    @Test def test_pr16566_2() {
        assert(checkCFG("pr16566-2.c") == false)
    }

    @Test def test_pr16566_3() {
        assert(checkCFG("pr16566-3.c") == false)
    }

    @Test def test_simd_1() {
        assert(checkCFG("simd-1.c") == false)
    }

    @Test def test_simd_2() {
        assert(checkCFG("simd-2.c") == false)
    }

    @Ignore def test_simd_3() {
        assert(checkCFG("simd-3.c") == false)
    }

    @Test def test_simd_4() {
        assert(checkCFG("simd-4.c") == false)
    }

    @Test def test_simd_5() {
        assert(checkCFG("simd-5.c") == false)
    }

    @Test def test_simd_6() {
        assert(checkCFG("simd-6.c") == false)
    }

    @Test def test_structs() {
        assert(checkCFG("structs.c") == false)
    }

    @Test def test_test01() {
        assert(checkCFG("test01.c") == false)
    }

    @Test def test_test02() {
        assert(checkCFG("test02.c") == false)
    }

    @Test def test_trunctfdf() {
        assert(checkCFG("trunctfdf.c") == false)
    }

    @Test def test_widechar_1() {
        assert(checkCFG("widechar-1.c") == false)
    }

    @Test def test_zero_strct_1() {
        assert(checkCFG("zero-strct-1.c") == false)
    }

    @Test def test_zero_strct_2() {
        assert(checkCFG("zero-strct-2.c") == false)
    }

    @Test def test_else_if_chains() {
        assert(checkCFG("test_else_if_chains.c") == false)
    }

    // bugfinding
    @Test def test_bug01() {
        assert(checkCFG("bug01.c") == false)
    }

    @Test def test_bug02() {
        assert(checkCFG("bug02.c") == false)
    }

    @Test def test_bug03() {
        assert(checkCFG("bug03.c") == false)
    }

    @Test def test_bug04() {
        assert(checkCFG("bug04.c") == false)
    }

    @Test def test_bug05() {
        assert(checkCFG("bug05.c") == false)
    }

    @Test def test_bug06() {
        assert(checkCFG("bug06.c") == false)
    }

    @Test def test_bug07() {
        assert(checkCFG("bug07.c") == false)
    }

    @Test def test_bug08() {
        assert(checkCFG("bug08.c") == false)
    }

    @Test def test_bug09() {
        assert(checkCFG("bug09.c") == false)
    }

    @Test def test_bug10() {
        assert(checkCFG("bug10.c") == false)
    }

    @Test def test_bug11() {
        assert(checkCFG("bug11.c") == false)
    }

    @Test def test_bug12() {
        assert(checkCFG("bug12.c") == false)
    }

    @Test def test_bug13() {
        assert(checkCFG("bug13.c") == false)
    }

    @Test def test_bug14() {
        assert(checkCFG("bug14.c") == false)
    }

    @Test def test_bug15() {
        assert(checkCFG("bug15.c") == false)
    }

    @Test def test_bug16() {
        assert(checkCFG("bug16.c") == false)
    }

    @Test def test_bug17() {
        assert(checkCFG("bug17.c") == false)
    }

    @Test def test_bug18() {
        assert(checkCFG("bug18.c") == false)
    }

    @Test def test_bug19() {
        assert(checkCFG("bug19.c") == false)
    }

    @Test def test_bug20() {
        assert(checkCFG("bug20.c") == false)
    }

    @Test def test_bug21() {
        assert(checkCFG("bug21.c") == false)
    }

    @Test def test_bug22() {
        assert(checkCFG("bug22.c") == false)
    }

    @Test def test_bug23() {
        assert(checkCFG("bug23.c") == false)
    }

    @Test def test_bug24() {
        assert(checkCFG("bug24.c") == false)
    }

    @Test def test_bug25() {
        assert(checkCFG("bug25.c") == false)
    }

    @Test def test_bug26() {
        assert(checkCFG("bug26.c") == false)
    }

    @Test def test_bug27() {
        assert(checkCFG("bug27.c") == false)
    }

    @Test def test_bug28() {
        assert(checkCFG("bug28.c") == false)
    }

    @Test def test_bug29() {
        assert(checkCFG("bug29.c") == false)
    }

    @Test def test_bug30() {
        assert(checkCFG("bug30.c") == false)
    }

    @Test def test_bug31() {
        assert(checkCFG("bug31.c") == false)
    }

    @Test def test_bug32() {
        assert(checkCFG("bug32.c") == false)
    }

    @Test def test_bug33() {
        assert(checkCFG("bug33.c") == false)
    }

    @Test def test_bug34() {
        assert(checkCFG("bug34.c") == false)
    }

    @Test def test_bug35() {
        assert(checkCFG("bug35.c") == false)
    }

    @Test def test_bug36() {
        assert(checkCFG("bug36.c") == false)
    }

    @Test def test_bug37() {
        assert(checkCFG("bug37.c") == false)
    }

    @Test def test_bug38() {
        assert(checkCFG("bug38.c") == false)
    }

    @Test def test_bug39() {
        assert(checkCFG("bug39.c") == false)
    }

    @Test def test_bug40() {
        assert(checkCFG("bug40.c") == false)
    }

    @Test def test_bug41() {
        assert(checkCFG("bug41.c") == false)
    }

    @Test def test_bug42() {
        assert(checkCFG("bug42.c") == false)
    }

    @Test def test_bug43() {
        assert(checkCFG("bug43.c") == false)
    }

    @Test def test_bug44() {
        assert(checkCFG("bug44.c") == false)
    }

    @Test def test_bug45() {
        assert(checkCFG("bug45.c") == false)
    }

    @Test def test_bug46() {
        assert(checkCFG("bug46.c") == false)
    }

    @Test def test_bug47() {
        assert(checkCFG("bug47.c") == false)
    }

    @Test def test_bug48() {
        assert(checkCFG("bug48.c") == false)
    }

    @Test def test_bug49() {
        assert(checkCFG("bug49.c") == false)
    }

    @Test def test_bug50() {
        assert(checkCFG("bug50.c") == false)
    }

    @Test def test_bug51() {
        assert(checkCFG("bug51.c") == false)
    }

    @Test def test_bug52() {
        assert(checkCFG("bug52.c") == false)
    }

    @Test def test_bug53() {
        assert(checkCFG("bug53.c") == false)
    }

    @Test def test_bug54() {
        assert(checkCFG("bug54.c") == false)
    }

    @Test def test_bug55() {
        assert(checkCFG("bug55.c") == false)
    }

    @Test def test_bug56() {
        assert(checkCFG("bug56.c") == false)
    }

    @Test def test_bug57() {
        assert(checkCFG("bug57.c") == false)
    }

    @Test def test_bug58() {
        assert(checkCFG("bug58.c") == false)
    }

    @Test def test_bug59() {
        assert(checkCFG("bug59.c") == false)
    }

    @Test def test_bug60() {
        assert(checkCFG("bug60.c") == false)
    }

    @Test def test_bug61() {
        assert(checkCFG("bug61.c") == false)
    }

    @Test def test_bug62() {
        assert(checkCFG("bug62.c") == false)
    }

    @Test def test_bug63() {
        assert(checkCFG("bug63.c") == false)
    }

    @Test def test_bug64() {
        assert(checkCFG("bug64.c") == false)
    }

    @Test def test_bug65() {
        assert(checkCFG("bug65.c") == false)
    }

    @Test def test_bug66() {
        assert(checkCFG("bug66.c") == false)
    }

    @Test def test_bug67() {
        assert(checkCFG("bug67.c") == false)
    }

    @Test def test_bug68() {
        assert(checkCFG("bug68.c") == false)
    }

    @Test def test_bug69() {
        assert(checkCFG("bug69.c") == false)
    }

    @Test def test_bug70() {
        assert(checkCFG("bug70.c") == false)
    }

    @Test def test_bug71() {
        assert(checkCFG("bug71.c") == false)
    }

    @Test def test_bug72() {
        assert(checkCFG("bug72.c") == false)
    }

    @Test def test_bug73() {
        assert(checkCFG("bug73.c") == false)
    }

    @Test def test_bug74() {
        assert(checkCFG("bug74.c") == false)
    }

    @Test def test_bug75() {
        assert(checkCFG("bug75.c") == false)
    }

    @Test def test_bug76() {
        assert(checkCFG("bug76.c") == false)
    }

    @Test def test_bug77() {
        assert(checkCFG("bug77.c") == false)
    }

    @Test def test_bug78() {
        assert(checkCFG("bug78.c") == false)
    }

    @Test def test_bug79() {
        assert(checkCFG("bug79.c") == false)
    }

    @Test def test_bug80() {
        assert(checkCFG("bug80.c") == false)
    }

    @Test def test_bug81() {
        assert(checkCFG("bug81.c") == false)
    }

    @Ignore def test_bug82() {
        assert(checkCFG("bug82.c") == false)
    }

    @Test def test_bug83(): Unit = {
        assert(checkCFG("bug83.c").unary_!)
    }
}
