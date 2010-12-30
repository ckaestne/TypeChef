package de.fosd.typechef.parser

import scala.collection.mutable.WeakHashMap
import de.fosd.typechef.featureexpr.{FeatureModel, FeatureExpr}

class FeatureSolverCache(featureModel: FeatureModel) {
    var i_hits, i_query, m_hits, m_query = 0;

    private val impliesCache: WeakHashMap[(FeatureExpr, FeatureExpr), Boolean] = new WeakHashMap()
    private val mutuallyExclusiveCache: WeakHashMap[(FeatureExpr, FeatureExpr), Boolean] = new WeakHashMap()

    val LOGGING = false

    /**
     * cheap if a in CNF and b in DNF
     */
    def implies(a: FeatureExpr, b: FeatureExpr): Boolean = {
        i_query += 1
        impliesCache.getOrElseUpdate((a, b), {
            i_hits += 1;
            if (LOGGING) println(a + " => " + b)

            /**
             * taut(a=>b) == contr(a && !b)
             * we use the latter one here to avoid accidental
             * negation of a, which is repeatedly used and
             * probably already available in CNF
             */
            a.and(b.not).isContradiction(featureModel)
        })
    }

    /**
     * cheap if a and b are in CNF
     */
    def mutuallyExclusive(a: FeatureExpr, b: FeatureExpr): Boolean = {
        m_query += 1
        mutuallyExclusiveCache.getOrElseUpdate((a, b), {
            m_hits += 1;
            if (LOGGING) println(a + " x " + b);
            a.and(b).isContradiction(featureModel)
        })
    }

    def statistics = "implies " + i_hits + "/" + i_query + " (" + (1.0 - (1.0 * i_hits) / i_query) + "); mex " + m_hits + "/" + m_query + " (" + (1.0 - (1.0 * m_hits) / m_query) + ")";
}

