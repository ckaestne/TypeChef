package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem.UseDeclMap
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureModel}
import de.fosd.typechef.typesystem.{CDeclUse, CTypeSystemFrontend}

object CheckDataflow extends IntraCFG with CFGHelper {

    def checkDataflow(tunit: TranslationUnit, fm: FeatureModel = FeatureExprFactory.default.featureModelFactory.empty) {
        val fdefs = filterAllASTElems[FunctionDef](tunit)
        val ts = new CTypeSystemFrontend(tunit, fm) with CDeclUse
        val udm = ts.getUseDeclMap
        fdefs.map(intraDataflowAnalysis(_, fm, udm))
    }

    private def intraDataflowAnalysis(f: FunctionDef, fm: FeatureModel, udm: UseDeclMap) {
        if (f.stmt.innerStatements.isEmpty) return

        val env = CASTEnv.createASTEnv(f)

        val ss = getAllSucc(f.stmt.innerStatements.head.entry, env)
        val lv = new Liveness(env, udm, fm)

        val nss = ss.map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])
        for (s <- nss) lv.in(s)
    }

}
