package de.fosd.typechef.parser

import de.fosd.typechef.featureexpr.FeatureExpr
import FeatureExpr.base
import FeatureExpr.dead
import org.kiama.attribution.Attributable

/**
 * generic glue elements for AST properties representing choices, optional elements
 * or just one element
 *
 * an AST may use the following constructs:
 *
 * One - expects always one element (there may be alternatives, but one must be selected in any case)
 * One = OneLeaf[T] | Choice[One[T]]
 *
 * Opt - expects at most one element (there may be alternatives, but they are mutually exclusive)
 * Opt = OptLeaf[T] | Choice[Opt[T]]
 *
 * Many - expects any number of (possibly optional) elements
 * Many = List[OptLeaf[T]]
 *
 * (in One and Opt, mutually exclusion is guaranteed by Choice which always
 * splits the variant space into mutually exclusive parts)
 */


abstract class AST

trait ASTProp[+T] extends Attributable

trait One[+T] extends Opt[T]

trait Opt[+T] extends ASTProp[T] {
    private[parser] def toOptList: List[OptLeaf[T]]
    def and(f: FeatureExpr): Opt[T]
    def andNot(f: FeatureExpr): Opt[T]
}

trait Many[+T] extends ASTProp[T]


object One {
    def apply[T](v: T): OneLeaf[T] = new OneLeaf(v)
}

object Choice {
    def apply[T](f: FeatureExpr, left: One[T], right: One[T]): One[T] = new OneChoice(f, left, right)
    def apply[T](f: FeatureExpr, left: Opt[T], right: Opt[T]): Opt[T] = new OptChoice(f, left, right)
}

object Opt {
    def apply[T](f: FeatureExpr, v: T): OptLeaf[T] = new OptLeaf(f, v)
    def apply[T](): Opt[T] = new OptNone()
}

object Many {
    //    def apply[T](v: OptLeaf[T]*): Many[T] = new ManyImpl(v.toList)
    def apply[T](v: Opt[T]*): Many[T] = new ManyImpl(
        v.toList.foldRight(List[OptLeaf[T]]())(_.toOptList ++ _)
    )
//    def apply[T](v: T*): Many[T] = new ManyImpl(
//        v.toList.map(One(_))
//    )
}


private[parser] case class OneLeaf[+T](e: T) extends OptLeaf(base, e) with One[T] {
    override def toString = e.toString
}

private[parser] case class OneChoice[+T](
                                                override val feature: FeatureExpr,
                                                override val left: One[T],
                                                override val right: One[T])
        extends OptChoice(feature, left, right) with One[T]


private[parser] case class OptLeaf[+T](val feature: FeatureExpr, val entry: T) extends Opt[T] {
    def toOptList = List(this)
    //    override def equals(x: Any) = x match {
    //    //XXX: use feature equality instead of equivalence for performance! this may not always be what is expected.
    //        case OptLeaf(f, e) => (f == feature) && (entry == e)
    //        case _ => false
    //    }
    //    //helper function
    def and(f: FeatureExpr) = if (f == null || f == base) this else new OptLeaf(feature.and(f), entry)
    def andNot(f: FeatureExpr) = if (f == null || f == dead) this else new OptLeaf(feature.and(f.not), entry)
}

private[parser] case class OptChoice[+T](val feature: FeatureExpr, val left: Opt[T], val right: Opt[T]) extends Opt[T] {
    def toOptList = (left and feature).toOptList ++ (right andNot feature).toOptList
    def and(f: FeatureExpr) = new OptChoice(feature, left and f, right and f)
    def andNot(f: FeatureExpr) = new OptChoice(feature, left andNot f, right andNot f)
}

private[parser] case class OptNone extends Opt[Nothing] {
    def toOptList = List()
    def and(f: FeatureExpr) = this
    def andNot(f: FeatureExpr) = this
    override def toString = "-"
}

private[parser] case class ManyImpl[+T](val entries: List[OptLeaf[T]]) extends Many[T] {
    override def toString = entries.mkString("[", ",", "]")
}
