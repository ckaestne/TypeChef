package de.fosd.typechef.parser
import scala.collection.mutable.WeakHashMap

import de.fosd.typechef.featureexpr.FeatureExpr

object FeatureSolverCache {
    var i_hits, i_query, m_hits, m_query = 0;

    private val impliesCache: WeakHashMap[FeatureExprPair, Boolean] = new WeakHashMap()
    private val mutuallyExclusiveCache: WeakHashMap[FeatureExprPair, Boolean] = new WeakHashMap()

    def implies(a: FeatureExpr, b: FeatureExpr): Boolean = {
        i_query += 1
        impliesCache.getOrElseUpdate(new FeatureExprPair(a, b),
            {
                i_hits += 1;
                println(a + " => " + b)
                a.implies(b).isTautology()
            })
    }

    def mutuallyExclusive(a: FeatureExpr, b: FeatureExpr): Boolean = {
        m_query += 1
        mutuallyExclusiveCache.getOrElseUpdate(new FeatureExprPair(a, b),
            {
                m_hits += 1;
                println(a + " x " + b);
                a.and(b).isContradiction()
            })
    }

    def statistics = "implies " + i_hits + "/" + i_query + " (" + (1.0 - (1.0*i_hits) / i_query) + "); mex " + m_hits + "/" + m_query + " (" + (1.0 - (1.0*m_hits) / m_query) + ")";
}

/**
 * this pair class has the sole purpose of comparing feature expressions
 * by identity (eq instead of equals) in maps
 */
private class FeatureExprPair(val a: FeatureExpr, val b: FeatureExpr) {
    override def hashCode = a.hashCode + b.hashCode
    override def equals(that: Any) = that match {
        case e: FeatureExprPair => (e.a eq this.a) && (e.b eq this.b)
        case _ => false
    }

}