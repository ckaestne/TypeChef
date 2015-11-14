package de.fosd.typechef.lexer

import java.io.File

import org.scalatest.FunSuite

class XtcDiffFileTest extends FunSuite with DifferentialTestingFramework {
    override protected def useXtc(): Boolean = true
    override protected def status(s: String) = info(s + " /xtc")

    val dir = new File(getClass.getResource("/tc_data").toURI)
    private def testFile(s: String): Unit = analyzeFile(new File(dir, s), dir)


    val filesToTest =
        """counter.c
           alternDiffArities1.c  ifdefnumeric.c          numbers.c
               alternDiffArities2.c  in1.c                   out1.c
               alternativedef.c      in2.c                   selfdef.c
               beispielJoerg.c       includeguards.c         simplecompare.c
               bnx.c                 includemacro.c    includemacroalt.c          stringify.c
               bnx2.c                incompatibleMacroExp.c  stringifyNl.c
               concatVarargs.c       kbuildstr.c             test_div_by_zero.c
               conditionalerror.c
               dateTime.c                       test_div_by_zero3.c
               deadcomparison.c      linuxtestflock.c        test_div_by_zero4.c
               defdefined.c          macro.c                 tokenpasting.c
               elifchain.c           macro2.c                undef.c
               emptyinclude.c        macroPFN.c              useconddef.c
               expandWithinExpand.c  multiinclude.c          varargs.c
               multimacro.c            variadic.c
               hiddenDeadAndBase.c   nestingdead.c
               ifcondition2.c         non_tautologic.c
               byteorder.h           h2.h              jiffiesTest.h       redef.h
               deadelse.h            header.h          parametricmacro.h   unlikely.h
                 if.h              parametricmacro2.h
               h1.h                  includeguards2.h  recursivemacro.h
               iso/ex1.c
               iso/ex2.c
               iso/ex3.c
               macrofilter/test.c
               nesting/termination.c
               nesting/m.c
               ifenabled.c
        """.split("\\ +").map(_.trim).filter(_.nonEmpty)

    val ignoredFiles =
        """linebreaks.c, line breaks not implemented
          |test_div_by_zero2.c, incorrect evaluation order
          |linebreaks2.c, warning in cpp fails in xtc
          |filebasefile.c, test compares file paths in different formats
          |filebasefileheader.h, test compares file paths in different formats
          |ifcondition.c, unclear problem, va-lexing works, but lexing single configuration produces incorrect result
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
