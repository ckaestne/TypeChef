package de.fosd.typechef.parser

import scala.collection.mutable.WeakHashMap
import de.fosd.typechef.featureexpr.{FeatureModel, FeatureExpr}

class FeatureSolverCache(featureModel: FeatureModel) {
    var i_hits, i_query, m_hits, m_query = 0;

    private val impliesCache: WeakHashMap[FeatureExpr, WeakHashMap[FeatureExpr, Boolean]] = new WeakHashMap()
    private val mutuallyExclusiveCache: WeakHashMap[FeatureExpr, WeakHashMap[FeatureExpr, Boolean]] = new WeakHashMap()

    val LOGGING = false

    /**
     * cheap if a in CNF and b in DNF
     */
    def implies(a: FeatureExpr, b: FeatureExpr): Boolean = {
        i_query += 1
        getOrElseUpdate(impliesCache, false, a, b, {
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
        getOrElseUpdate(mutuallyExclusiveCache, true, a, b, {
            m_hits += 1;
            if (LOGGING) println(a + " x " + b);
            a.and(b).isContradiction(featureModel)
        })
    }

    def getOrElseUpdate(cache: WeakHashMap[FeatureExpr, WeakHashMap[FeatureExpr, Boolean]], isCommutative: Boolean,
                        a: FeatureExpr, b: FeatureExpr, newValue: => Boolean): Boolean = {
        //find (a,b)
        val ca = cache.get(a)
        if (ca.isDefined) {
            val v = ca.get.get(b)
            if (v.isDefined)
                return v.get
        }
        //find (b,a)  if commutative
        if (isCommutative) {
            val cb = cache.get(b)
            if (cb.isDefined) {
                val v = cb.get.get(a)
                if (v.isDefined)
                    return v.get
            }
        }

        //add result to cache
        val result: Boolean = newValue
        cache.getOrElseUpdate(a, new WeakHashMap[FeatureExpr, Boolean]).update(b, result)
        result
    }

    def statistics = "implies " + i_hits + "/" + i_query + " (" + (1.0 - (1.0 * i_hits) / i_query) + "); mex " + m_hits + "/" + m_query + " (" + (1.0 - (1.0 * m_hits) / m_query) + ")";
}


