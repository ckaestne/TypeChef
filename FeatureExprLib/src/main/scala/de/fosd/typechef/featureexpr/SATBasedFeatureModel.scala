package de.fosd.typechef.featureexprUtil

import org.sat4j.specs.{IVecInt, IVec}


trait SATBasedFeatureModel {
    val variables: Map[String, Int]
    val clauses: IVec[IVecInt]
    val lastVarId: Int
}
