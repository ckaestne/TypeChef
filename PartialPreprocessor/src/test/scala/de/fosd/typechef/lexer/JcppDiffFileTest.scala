package de.fosd.typechef.lexer

import java.io.File

import org.scalatest.FunSuite

class JcppDiffFileTest extends FunSuite with DifferentialTestingFramework {
    override protected def useXtc(): Boolean = false
    override protected def status(s: String) = info(s + " /jcpp")

    val dir = new File(getClass.getResource("/tc_data").toURI)
    private def testFile(s: String): Unit = analyzeFile(new File(dir, s), dir)


    val filesToTest =
        """counter.c
           alternDiffArities1.c  ifdefnumeric.c          numbers.c
                alternDiffArities2.c  in1.c                   out1.c
                alternativedef.c      in2.c                   selfdef.c
                beispielJoerg.c       includeguards.c         simplecompare.c
                                 includemacro.c
                bnx2.c                  stringifyNl.c
                concatVarargs.c       kbuildstr.c             test_div_by_zero.c
                conditionalerror.c
                dateTime.c                       test_div_by_zero3.c
                deadcomparison.c      linuxtestflock.c
                defdefined.c          macro.c                 tokenpasting.c
                elifchain.c           macro2.c                undef.c
                emptyinclude.c        macroPFN.c              useconddef.c
                            varargs.c
                multimacro.c            variadic.c
                hiddenDeadAndBase.c   nestingdead.c
                ifcondition2.c         non_tautologic.c
                           h2.h              jiffiesTest.h       redef.h
                deadelse.h            header.h          parametricmacro.h   unlikely.h
                         parametricmacro2.h
                h1.h                  includeguards2.h  recursivemacro.h
                iso/ex1.c
                iso/ex2.c
                iso/ex3.c
                macrofilter/test.c
                nesting/termination.c
                nesting/m.c
           linebreaks.c
           test_div_by_zero2.c
           linebreaks2.c
           filebasefile.c
           filebasefileheader.h
           ifcondition.c
        """.split("\\ +").map(_.trim).filter(_.nonEmpty)

    val ignoredFiles =
        """bnx.c, unclear - incorrect macro expansion
          |includemacroalt.c, include directives with alternative targets not supported
          |multiinclude.c, include directives with alternative targets not supported
          |stringify.c, concatenation with alternatives not supported
          |incompatibleMacroExp.c, conditional invalid macro expansion throws exception in all configurations
          |byteorder.h, unclear problem with macro expansion
          |if.h, unclear problem with macro expansion
          |test_div_by_zero4.c, conditional error message not handled correctly
          |expandWithinExpand.c, throws conditional error where cpp works without complaints
          |ifenabled.c, incorrect macro expansion
        """.stripMargin.split("\\n").filter(_.trim.nonEmpty).map(l => {
            val p = l.indexOf(",");
            (l.take(p).trim, l.drop(p + 1).trim)
        })



    for ((file, reason) <- ignoredFiles)
        ignore(file.replace('.', '_') + " - ignored due to lexer bug: " + reason) {
            testFile(file)
        }

    for (file <- filesToTest)
        test(file.replace('.', '_') + " - differential testing") {
            testFile(file)
        }

}
