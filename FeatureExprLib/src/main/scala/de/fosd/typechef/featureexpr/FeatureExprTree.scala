package de.fosd.typechef.featureexpr

/**
 * FeatureExprTree is the root class for non-propositional nodes in feature
 * expressions, i.e., Integer and If nodes, which cannot be checked for satisfiability.
 */
sealed trait FeatureExprTree[T] {
    def asObject(): FeatureExprTree[Object] = this.asInstanceOf[FeatureExprTree[Object]]
}

trait FeatureExprValueOps {
    def createValue[T](v: T): FeatureExprTree[T]

    implicit def long2value(x: Long): FeatureExprValue = createValue(x)
    //
    //    //This is to add, for Scala sources, the toFeatureExpr method to
    //    //FeatureExprTree[Long] = FeatureExprValue
    //    class RichFeatureExprValue private[featureexpr](val v: FeatureExprValue) {
    //        def toFeatureExpr: FeatureExpr = FeatureExprValue.toFeatureExpr(v)
    //    }
    //
    //    implicit def val2rich(x: FeatureExprValue) = new RichFeatureExprValue(x)
}

object FeatureExprValue {
    def toFeatureExpr(v: FeatureExprTree[Long], factory: AbstractFeatureExprFactory): FeatureExpr = {
        val zero = factory.createValue[Long](0)
        factory.evalRelation(v, zero)(_ != _)
    }
}

/**
 * values (integers, chars and operations and relations on them)
 */

private[featureexpr] class If[T](val expr: FeatureExpr, val thenBr: FeatureExprTree[T], val elseBr: FeatureExprTree[T]) extends FeatureExprTree[T] {
    override def toString = "(" + expr + "?" + thenBr + ":" + elseBr + ")"
    override def hashCode = expr.hashCode()
    override def equals(that: Any) = that match {
        case that: If[_] => expr == that.expr && thenBr == that.asInstanceOf[If[T]].thenBr && elseBr == that.asInstanceOf[If[T]].elseBr
        case _ => super.equals(that)
    }
}

private[featureexpr] object If {
    def unapply[T](x: If[T]) = Some((x.expr, x.thenBr, x.elseBr))
}

private[featureexpr] class Value[T](val value: T) extends FeatureExprTree[T] {
    override def toString = value.toString
    override def hashCode = value.hashCode()
    override def equals(that: Any) = that match {
        case that: Value[_] => value == that.asInstanceOf[Value[T]].value
        case _ => super.equals(that)
    }
}

private[featureexpr] object Value {
    def unapply[T](x: Value[T]) = Some(x.value)
}

private[featureexpr] class ErrorValue[T](val msg: String) extends FeatureExprTree[T] {
    override def toString = "###Error: " + msg + " ###"
}

private[featureexpr] object ErrorValue {
    def unapply[T](x: ErrorValue[T]) = Some(x.msg)

    def apply[T](x: String) = new ErrorValue[T](x)
}
