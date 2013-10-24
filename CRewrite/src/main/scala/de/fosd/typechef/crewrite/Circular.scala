package de.fosd.typechef.crewrite

import java.util.IdentityHashMap
import org.kiama.attribution.Attributable

trait Circular {

    /**
     * Common functionality for all attributes.
     */
    abstract class Attribute[T <: AnyRef,U] extends (T => U) {

        /**
         * An optional name, used in debugging output if present.
         */
        def optName : Option[String]

        /**
         * Report a cycle in the calculation of this attribute discovered when
         * evaluating the attribute on value `t`. Throws an `IllegalStateException`.
         */
        def reportCycle (t : T) : U = {
            val error = "Cycle detected in attribute evaluation"
            val identity = optName.map (" '" + _ + "'").getOrElse ("")
            val message = "%s%s at %s".format (error, identity, t)
            throw new IllegalStateException (message)
        }

    }



    /**
     * An attribute of a node type `T` with value of type `U` which has a circular
     * definition.  The value of the attribute is computed by the function f
     * which may itself use the value of the attribute.  init specifies an
     * initial value for the attribute.  The attribute (and any circular attributes
     * on which it depends) are evaluated until no value changes (i.e., a fixed
     * point is reached).  The final result is memoised so that subsequent evaluations
     * return the same value.
     *
     * This code implements the basic circular evaluation algorithm from "Circular
     * Reference Attributed Grammars - their Evaluation and Applications", by Magnusson
     * and Hedin from LDTA 2003.
     */
    abstract class CircularAttribute[T <: AnyRef,U] (init : U, f : T => U) extends Attribute[T,U] {

        /**
         * Global state for the circular attribute evaluation algorithm
         * and the memoisation tables.
         */
        private class CircularState {
            var IN_CIRCLE = false
            var CHANGE = false
        }

        private val circ = new CircularState()

        /**
         * Has the value of this attribute for a given tree already been computed?
         */
        private val computed = new IdentityHashMap[T,Unit]

        /**
         * Has the attribute for given tree been computed on this iteration of the
         * circular evaluation?
         */
        private val visited = new IdentityHashMap[T,Unit]

        /**
         * The memo table for this attribute.
         */
        private val memo = new IdentityHashMap[T,U]

        /**
         * Return the value of the attribute for tree `t`, or the initial value if
         * no value for `t` has been computed.
         */
        private def value (t : T) : U = {
            val v = memo.get (t)
            if (v == null)
                init
            else
                v
        }

        private var counter = 0

        /**
         * Return the value of this attribute for node `t`.  Essentially Figure 6
         * from the CRAG paper.
         */
        def apply (t : T) : U = {
            counter += 1

            if (computed containsKey t) {
                //println("called apply for (value)", optName, counter, "times")
                // value is computed by now!
                value (t)
            } else if (!circ.IN_CIRCLE) {
                // we are in the computing process but starting to circle
                circ.IN_CIRCLE = true
                visited.put (t, ())
                var u = init
                do {
                    circ.CHANGE = false
                    val newu = f (t)
                    if (u != newu) {
                        circ.CHANGE = true
                        u = newu
                    }
                } while (circ.CHANGE)
                visited.remove (t)
                computed.put (t, ())
                memo.put (t, u)
                circ.IN_CIRCLE = false
                u
            } else if (! (visited containsKey t)) {
                visited.put (t, ())
                var u = value (t)
                val newu = f (t)
                if (u != newu) {
                    circ.CHANGE = true
                    u = newu
                    memo.put (t, u)
                }
                visited.remove (t)
                u
            } else {
                value (t)
            }

        }

    }

    /**
     * Define an optionally named circular attribute of `T` nodes of type `U`
     * by the function `f`. `f` is allowed to depend on the value of this
     * attribute, which will be given by `init` initially and will be evaluated
     * iteratively until a fixed point is reached (in conjunction with other
     * circular attributes on which it depends).  The final value is cached.
     * If `optNameDef` is not `None`, then `optNameDef.get` is used in
     * debugging output to identify this attribute.
     */
    def circular[T <: AnyRef,U] (optNameDef : Option[String]) (init : U) (f : T => U) : T => U = {
        new CircularAttribute (init, f) {
            val optName = optNameDef
        }
    }

    /**
     * Define an anonymous circular attribute of `T` nodes of type `U` by the
     * function `f`. `f` is allowed to depend on the value of this attribute,
     * which will be given by `init` initially and will be evaluated iteratively
     * until a fixed point is reached (in conjunction with other circular
     * attributes on which it depends).  The final value is cached.
     */
    def circular[T <: AnyRef,U] (init : U) (f : T => U) : T => U =
        circular (None) (init) (f)

    /**
     * Define a named circular attribute of `T` nodes of type `U` by the function
     * `f`. `f` is allowed to depend on the value of this attribute, which will
     * be given by `init` initially and will be evaluated iteratively until a
     * fixed point is reached (in conjunction with other circular attributes
     * on which it depends).  The final value is cached.  `name` is used in
     * debugging output to identify this attribute.
     */
    def circular[T <: AnyRef,U] (name : String) (init : U) (f : T => U) : T => U =
        circular (Some (name)) (init) (f)
}
