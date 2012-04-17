package de.fosd.typechef.featureexpr.sat

/**
 * @author John Williams
 *
 * see http://www.scala-lang.org/node/49
 */

object LazyLib {

    /**Delay the evaluation of an expression until it is needed. */
    def delay[A](value: => A): Susp[A] = new SuspImpl[A](value)

    /**Get the value of a delayed expression. */
    implicit def force[A](s: Susp[A]): A = s()

    /**
     * Data type of suspended computations. (The name stems from ML.)
     */
    abstract class Susp[+A] extends Function0[A]

    /**
     * Implementation of suspended computations, separated from the
     * abstract class so that the type parameter can be invariant.
     */
    class SuspImpl[A](lazyValue: => A) extends Susp[A] {
        private var maybeValue: Option[A] = None

        override def apply() = maybeValue match {
            case None =>
                val value = lazyValue
                maybeValue = Some(value)
                value
            case Some(value) =>
                value
        }

        override def toString() = maybeValue match {
            case None => "Susp(?)"
            case Some(value) => "Susp(" + value + ")"
        }
    }

}