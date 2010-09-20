package de.fosd.typechef.parser

import de.fosd.typechef.featureexpr.FeatureExpr

object FeatureSolverCache {
	type CacheT = (FeatureExpr,FeatureExpr,Boolean)
	var i1,i2,m1,m2:CacheT = (null,null,false)

//    var i_lastA: FeatureExpr = null
//    var i_lastB: FeatureExpr = null
//    var i_lastResult: Boolean = false
//    var m_lastA: FeatureExpr = null
//    var m_lastB: FeatureExpr = null
//    var m_lastResult: Boolean = false
    
    var i_hits, i_query, m_hits, m_query=0;

    def implies(a: FeatureExpr, b: FeatureExpr): Boolean = {
    	i_query+=1
        if ((a eq i1._1) && (b eq i1._2))
            return i1._3
        if ((a eq i2._1) && (b eq i2._2))
        	{ swapi; return i1._3}
    	i_hits+=1
        val result = a.implies(b).isBase
        println(a+" => "+b+"="+result)
        storei((a,b,result))
        return result
    }
    def mutuallyExclusive(a: FeatureExpr, b: FeatureExpr): Boolean = {
    	m_query+=1
        if ((a eq m1._1) && (b eq m1._2))
            return m1._3
        if ((a eq m2._1) && (b eq m2._2))
        	{ swapm; return m1._3}
    	m_hits+=1
        val result = a.and(b).isDead
        println(a+" x "+b+"="+result)
        storem((a,b,result))
        return result
    }
    
    def swapi { val t=i1; i1=i2; i2=t;}
    def storei(c:CacheT) { i2=i1; i1=c;}
    def swapm { val t=m1; m1=m2; m2=t;}
    def storem(c:CacheT) { m2=m1; m1=c;}
    
    
    def statistics = "implies "+i_hits+"/"+i_query+" ("+(1.0-i_hits/i_query)+"); mex "+m_hits+"/"+m_query+" ("+(1.0-m_hits/m_query)+")";

}