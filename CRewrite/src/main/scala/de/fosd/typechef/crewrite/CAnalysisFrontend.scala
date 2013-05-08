
package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser.c.{TranslationUnit, FunctionDef}
import java.io.{Writer, StringWriter}
import de.fosd.typechef.typesystem._

class CAnalysisFrontend(tunit: TranslationUnit, fm: FeatureModel = FeatureExprFactory.empty) extends CFGHelper {

    def dumpCFG(writer: Writer = new StringWriter()) {
        val fdefs = filterAllASTElems[FunctionDef](tunit)
        val dump = new DotGraph(writer)
        val env = CASTEnv.createASTEnv(tunit)
        dump.writeHeader("CFGDump")

        for (f <- fdefs) {
            dump.writeMethodGraph(getAllSucc(f, fm, env), env, Map())
        }
        dump.writeFooter()
        dump.close()

        if (writer.isInstanceOf[StringWriter])
            println(writer.toString)
    }

    def doubleFree() {

        val casestudy = {
            tunit.getFile match {
                case None => ""
                case Some(x) => {
                    if (x.contains("linux")) "linux"
                    else if (x.contains("openssl")) "openssl"
                    else ""
                }
            }
        }

        val ts = new CTypeSystemFrontend(tunit, fm)
        //assert(ts.checkASTSilent, "typecheck fails!")
        val env = CASTEnv.createASTEnv(tunit)
        val udm = ts.getUseDeclMap

        val fdefs = filterAllASTElems[FunctionDef](tunit)
        println("#functions " + fdefs.size)
        val errors = fdefs.flatMap(doubleFreeFunctionDef(_, env, udm, casestudy))

        if (errors.isEmpty) {
            println("No double frees found!")
        } else {
            println(errors.map(_.toString + "\n").reduce(_ + _))
        }

        errors.isEmpty
    }

    private def doubleFreeFunctionDef(f: FunctionDef, env: ASTEnv, udm: UseDeclMap, casestudy: String): List[AnalysisError] = {
        println("Analyzing: " + f.getName)
        var res: List[AnalysisError] = List()

        // It's ok to use FeatureExprFactory.empty here.
        // Using the project's fm is too expensive since control
        // flow computation requires a lot of sat calls.
        // We use the proper fm in DoubleFree (see MonotoneFM).
        val ss = getAllSucc(f, FeatureExprFactory.empty, env).reverse
        val df = new DoubleFree(env, udm, fm, casestudy)

        val nss = ss.map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])

        for (s <- nss) {
            val g = df.gen(s)
            val out = df.out(s)

            for ((i, _) <- out)
                for ((_, j) <- g) {
                    j.find(_ == i) match {
                        case None =>
                        case Some(x) => res ::= new AnalysisError(env.featureExpr(x), "warning: Try to free a memory block that has been released", x)
                    }
                }
        }

        res
    }
}
