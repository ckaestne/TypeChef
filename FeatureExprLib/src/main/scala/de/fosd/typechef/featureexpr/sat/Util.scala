package de.fosd.typechef.featureexpr.sat

import scala.ref.WeakReference
import scala.ref.SoftReference

abstract class NFException(msg: String) extends Exception(msg)

class NoNFException(
                           e: SATFeatureExpr,
                           fullExpr: SATFeatureExpr,
                           expectCNF: Boolean)
        extends NFException("expression is not in " + (if (expectCNF) "cnf" else "dnf") + " " + e + " (" + fullExpr + ")")

class NoLiteralException(e: SATFeatureExpr) extends NFException("expression is not a literal " + e)

// Utility extractor to allow more convenient pattern matching.
object WeakRef {
    def unapply[T <: AnyRef](w: WeakReference[T]): Option[T] = w.get
}

object SoftRef {
    def unapply[T <: AnyRef](w: SoftReference[T]): Option[T] = w.get
}
