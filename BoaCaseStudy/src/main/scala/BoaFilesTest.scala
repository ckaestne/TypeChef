package de.fosd.typechef.parser.c
import java.io.FileWriter

import junit.framework._;
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._

object BoaFilesTest extends Application {
    def parseFile(fileName: String) {
        val initialContext = new CTypeContext().addType("__uint32_t")
        val result = new CParser().translationUnit(
            CLexer.lexFile(fileName, "testfiles/cgram/").setContext(initialContext), FeatureExpr.base
            )
        val resultStr: String = result.toString
        println(FeatureSolverCache.statistics)
        val writer = new FileWriter(fileName + ".ast")
        writer.write(resultStr);
        writer.close
        println("done.")

        //        System.out.println(resultStr)

        result match {
            case Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream (" + unparsed.first.getPositionStr + "): " + unparsed, unparsed.atEnd)
                //succeed
            }
            case NoSuccess(msg, context, unparsed, inner) =>
                fail(msg + " at " + unparsed + " with context " + context + " " + inner)
        }

    }
    //    def testLinuxFork {parseFile("../linux-2.6.33.3/kernel/fork.pi")}

    parseFile("w:/work/typechef/boa/src/hash.pi")
}