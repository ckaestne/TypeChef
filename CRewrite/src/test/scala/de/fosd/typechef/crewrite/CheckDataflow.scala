package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureModel}

object CheckDataflow extends ConditionalControlFlow with CFGHelper with Liveness {

    def checkDataflow(tunit: AST, fm: FeatureModel = FeatureExprFactory.default.featureModelFactory.empty) {
      val fdefs = filterAllASTElems[FunctionDef](tunit)
      fdefs.map(intraDataflowAnalysis(_, fm))
    }

    private def intraDataflowAnalysis(f: FunctionDef, fm: FeatureModel) {
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
