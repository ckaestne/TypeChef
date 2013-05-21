package de.fosd.typechef.crefactor.busyBoxVerifacation

import java.io.{FileInputStream, File}
import de.fosd.typechef.crefactor.Morpheus
import de.fosd.typechef.parser.c.{Id, PostfixExpr, FunctionCall}
import de.fosd.typechef.crefactor.backend.refactor.InlineFunction


class Inline extends BusyBoxVerification {

    private val output = OUTPUT_PATH + "/inlined/"


    def performRefactor(fileToRefactor: File) {
        val testStart = currentTime
        logger.info("+++ Rename Verification on " + fileToRefactor.getName + " +++")

        val path = fileToRefactor.getCanonicalPath.replaceFirst(new File(busyBoxPath).getCanonicalPath, new File(output).getCanonicalPath).replace(".pi", "")
        val fis = new FileInputStream(fileToRefactor)
        val parsingStartTime = currentTime
        val ast = parseFile(fis, fileToRefactor.getName, fileToRefactor.getParent)
        fis.close()
        val parsingTime = currentTime - parsingStartTime

        val typeCheckStartTime = currentTime
        val morpheus = new Morpheus(ast, fileToRefactor)
        val typeCheckTime = currentTime - typeCheckStartTime

        val calls = filterASTElems[FunctionCall](ast)


        var succ = false
        var counter = 0

        while (!succ && (counter < 5)) {
            counter += 1
            val call = getValidRandomCall(calls, morpheus)
            parentAST(call, morpheus.getASTEnv) match {
                case PostfixExpr(i@Id(_), _) => succ = inline(i, morpheus)
                case _ => logger.error("Error in detecting the calling id.")
            }
        }

        val runtime = currentTime - testStart
        logger.info("+++ Finished Refactoring on " + fileToRefactor.getName + " in " + runtime + "ms +++")
        logger.info(succ)
    }

    private def inline(id: Id, morpheus: Morpheus): Boolean = {
        try {
            val ast = InlineFunction.inline(morpheus, id, true, false)
            true
        } catch {
            case e: AssertionError => logger.error(e)
                false
        }
    }

    private def getValidRandomCall(calls: List[FunctionCall], morpheus: Morpheus): FunctionCall = calls.apply((math.random * calls.size).toInt)
}
