package de.fosd.typechef

import scala.collection.immutable.HashMap
import org.kiama.rewriting.Rewriter._

import de.fosd.typechef.featureexpr.SingleFeatureExpr
import de.fosd.typechef.parser.c.TranslationUnit
import de.fosd.typechef.conditional.{Choice, Opt}

class FileFeatures(@transient val tunit: TranslationUnit) extends scala.Serializable {

    /** List of all features found in the currently processed file (tunit) */
    val features: List[SingleFeatureExpr] = getAllFeatures

    /** Maps SingleFeatureExpr Objects to IDs (IDs only known/used in this file) */
    @transient lazy val featureIDHashmap: Map[SingleFeatureExpr, Int] =
        new HashMap[SingleFeatureExpr, Int]().++(features.zipWithIndex)

    /**
     * Returns a sorted list of all features in this AST, including Opt and Choice Nodes
     * @return
     */
    private def getAllFeatures: List[SingleFeatureExpr] = {
        val r = collects {
            case x: Opt[_] => x.feature.collectDistinctFeatureObjects
            case x: Choice[_] => x.feature.collectDistinctFeatureObjects
        }

        var featureSet = r(tunit).flatten

        // sort to eliminate any non-determinism caused by the set
        val featureList = featureSet.toList.sortWith({
            (x: SingleFeatureExpr, y: SingleFeatureExpr) => x.feature.compare(y.feature) > 0
        })

        assert(! featureList.isEmpty)

        featureList
    }
}
