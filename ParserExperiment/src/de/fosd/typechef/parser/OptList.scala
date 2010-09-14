package de.fosd.typechef.parser

import scala.collection.immutable.List
import de.fosd.typechef.featureexpr.FeatureExpr

//class OptList[+T] extends List[Opt[T]] 

case class Opt[+T] (feature:FeatureExpr, entry:T)