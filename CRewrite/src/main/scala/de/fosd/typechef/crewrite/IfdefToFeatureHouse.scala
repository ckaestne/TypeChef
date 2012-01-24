package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr._


trait IfdefToFeatureHouse extends ConditionalControlFlow {

  val basedir = "/base"

  private def featureExprToStr(feature: FeatureExpr): String = {
    feature match {
      case d: DefinedExternal => d.feature
      case a: And => { a.clauses.map(featureExprToStr(_)).foldLeft("")(_ + "_and_" + _) }
      case o: Or => { o.clauses.map(featureExprToStr(_)).foldLeft("")(_ + "_or_" + _) }
      case n: Not => { "not_" + featureExprToStr(n.expr) }
      case _ => ""
    }
  }

  //
}