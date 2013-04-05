
package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser.c.{FunctionDef, AST}
import java.io.{Writer, StringWriter}

class CAnalysisFrontend(tunit: AST, fm: FeatureModel = FeatureExprFactory.default.featureModelFactory.empty) extends CFGHelper with Liveness {

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

  def liveness() {
    val fdefs = filterAllASTElems[FunctionDef](tunit)
    fdefs.map(intraDataflowAnalysis(_))
  }

  private def intraDataflowAnalysis(f: FunctionDef) {
    if (f.stmt.innerStatements.isEmpty) return

    val env = CASTEnv.createASTEnv(f)
    setEnv(env)
    val ss = getAllSucc(f.stmt.innerStatements.head.entry, FeatureExprFactory.empty, env)
    val udr = determineUseDeclareRelation(f)
    setUdr(udr)
    setFm(fm)

    val nss = ss.map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])
    for (s <- nss) in(s)
  }
}
