package de.fosd.typechef.featureexpr

//sealed trait FeatureExprTree[T]

/**
 * FeatureExprValue is the root class for non-propositional nodes in feature
 * expressions, i.e., Integer and If nodes, which cannot be checked for satisfiability.
 */

sealed trait FeatureExprValue /*extends FeatureExprTree[Long]*/ {
    def toFeatureExpr: FeatureExpr = {
        val zero = FExprBuilder.createValue(0)
        FExprBuilder.evalRelation(this, zero, _ != _)
    }
}

object FeatureExprValue {
    implicit def long2value(x: Long): FeatureExprValue = FExprBuilder.createValue(x)
}

/**
 * values (integers, chars and operations and relations on them)
 */

private[featureexpr] class If(val expr: FeatureExpr, val thenBr: FeatureExprValue, val elseBr: FeatureExprValue) extends FeatureExprValue {
    override def toString = "(" + expr + "?" + thenBr + ":" + elseBr + ")"
}

private[featureexpr] object If {
    def unapply(x: If) = Some((x.expr, x.thenBr, x.elseBr))
}

private[featureexpr] class Value(val value: Long) extends FeatureExprValue {
    override def toString = value.toString
}

private[featureexpr] object Value {
    def unapply(x: Value) = Some(x.value)
}

private[featureexpr] class ErrorValue(val msg: String) extends FeatureExprValue {
    override def toString = "###Error: " + msg + " ###"
}

private[featureexpr] object ErrorValue {
    def unapply(x: ErrorValue) = Some(x.msg)

    def apply(x: String) = new ErrorValue(x)
}
