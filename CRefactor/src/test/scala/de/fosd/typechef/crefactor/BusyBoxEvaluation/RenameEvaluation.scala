package de.fosd.typechef.crefactor.BusyBoxEvaluation

import org.junit.Test
import java.io.File
import de.fosd.typechef.parser.c.{Id, AST}
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.crefactor.util.{PrepareRefactoredASTforEval, TimeMeasurement}
import de.fosd.typechef.crefactor.Morpheus
import de.fosd.typechef.crefactor.backend.refactor.RenameIdentifier

class RenameEvaluation extends BusyBoxEvaluation {

    private val refactor_name = "refactoredID"

    @Test
    def evaluate() {
        val files = getBusyBoxFiles.reverse
        val refactor = files.map(file => {
            var stats = List[Any]()
            val parseTypeCheckMs = new TimeMeasurement
            val bb_file = new File(busyBoxPath + file)
            val parsed = parse(bb_file)
            val ast = parsed._1
            val fm = parsed._2
            val morpheus = new Morpheus(ast, fm)
            val parseTypeCheckTime = parseTypeCheckMs.getTime
            stats ::= parseTypeCheckTime
            val result = applyRefactor(morpheus, stats)
            if (result._2) PrepareRefactoredASTforEval.prepare(result._1, morpheus.getFeatureModel, bb_file.getCanonicalPath, result._3, 0)

            val verify = RefactorVerification.verify(bb_file, 0, fm)
            var stat2 = result._4
            stat2 = stat2.::(result._2 && verify)
            writeStats(stat2, bb_file.getCanonicalPath, 0)
        })
        logger.info("Refactor succ: " + refactor.contains(false))

    }

    def applyRefactor(morpheus: Morpheus, stat: List[Any]): (AST, Boolean, List[FeatureExpr], List[Any]) = {
        val ids = morpheus.getUseDeclMap.values().toArray(Array[List[Id]]()).par.foldLeft(List[Id]())((list, entry) => list ::: entry).toList
        def getVariableIdForRename(depth: Int = 0): (Id, Int, List[FeatureExpr]) = {
            val id = ids.apply((math.random * ids.size).toInt)

            val amountOfIds = RenameIdentifier.getAllConnectedIdentifier(id, morpheus.getDeclUseMap, morpheus.getUseDeclMap).length
            val features = RenameIdentifier.getAllConnectedIdentifier(id, morpheus.getDeclUseMap, morpheus.getUseDeclMap).map(x => morpheus.getASTEnv.featureExpr(x))
            // check recursive only for variable ids
            val writeAble = RenameIdentifier.getAllConnectedIdentifier(id, morpheus.getDeclUseMap, morpheus.getUseDeclMap).forall(i => new File(i.getFile.get.replaceFirst("file ", "")).canWrite)
            if (!writeAble || id.name.equals("main")) getVariableIdForRename(depth)
            else if ((features.distinct.length == 1) && features.contains("True") && FORCE_VARIABILITY && (depth < MAX_DEPTH)) getVariableIdForRename(depth + 1)
            else (id, amountOfIds, features)
        }



        val toRename = getVariableIdForRename()
        val id = toRename._1
        val features = toRename._3

        val startRenaming = new TimeMeasurement
        val refactored = RenameIdentifier.rename(id, refactor_name, morpheus)
        val renamingTime = startRenaming.getTime
        var stats = stat.::(renamingTime)
        stats = stats.::(id)
        stats = stats.::(toRename._2)
        stats = stats.::(features)

        val morpheus2 = new Morpheus(refactored, morpheus.getFeatureModel)

        val originAmount = analsyeDeclUse(morpheus.getDeclUseMap).sorted
        val newAmount = analsyeDeclUse(morpheus2.getDeclUseMap).sorted

        (refactored, originAmount == newAmount, features, stats)
    }
}
