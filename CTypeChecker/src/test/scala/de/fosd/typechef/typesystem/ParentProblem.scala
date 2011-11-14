//package de.fosd.typechef.typesystem
//
//import org.junit.runner.RunWith
//import org.scalatest.FunSuite
//import org.scalatest.junit.JUnitRunner
//import org.scalatest.matchers.ShouldMatchers
//import org.kiama.rewriting.Rewriter._
//import org.kiama.attribution.Attributable
//
//
///**
// * A simple imperative language abstract syntax designed for testing.
// */
//
//object TmpAST {
//
//    /**
//     * Identifiers are represented as strings.
//     */
//    type Idn = String
//
//
//    /**
//     * Superclass of all imperative language tree node types.  The Product
//     * supertype is used here to enable generic access to the children of
//     * an ImperativeNode; this capability is only used in the Kiama tests
//     * and is not usually needed for normal use of the library.
//     */
//    trait ImperativeNode extends Product with Cloneable with Attributable {
//        override def clone() = super.clone().asInstanceOf[ImperativeNode]
//    }
//
//
//    /**
//     * Expressions.
//     */
//    abstract class Exp extends ImperativeNode {
//
//
//        /**
//         * The numeric value of the expression.
//         */
//        def value: Double
//
//
//        /**
//         * The set of all variable references in the expression.
//         */
//        def vars: Set[Idn] = Set()
//
//
//        /**
//         * The number of divisions by the constant zero in the expression.
//         */
//        def divsbyzero: Int = 0
//
//
//        /**
//         * The depth of the expression, i.e., the number of levels from the
//         * root to the leaf values.
//         */
//        def depth: Int = 0
//
//
//        /**
//         * The number of additions of integer constants in the expression.
//         */
//        def intadds: Int = 0
//    }
//
//
//    /**
//     * Numeric expressions.
//     */
//    case class Num(d: Double) extends Exp {
//        override def value = d
//        override def depth = 2
//    }
//
//
//    /**
//     * Variable expressions.
//     */
//    case class Var(s: Idn) extends Exp {
//        // Hack to make tests more interesting
//        override def value = 3
//        override def vars = Set(s)
//        override def depth = 2
//        override def toString = "Var(\"" + s + "\")"
//    }
//
//
//    /**
//     * Unary negation expressions.
//     */
//    case class Neg(e: Exp) extends Exp {
//        override def value = -e.value
//        override def vars = e.vars
//        override def divsbyzero = e.divsbyzero
//        override def depth = 1 + e.depth
//        override def intadds = e.intadds
//    }
//
//
//    /**
//     * Binary expressions.
//     */
//    abstract class Binary(l: Exp, r: Exp) extends Exp {
//        override def vars = l.vars ++ r.vars
//        override def divsbyzero = l.divsbyzero + r.divsbyzero
//        override def depth = 1 + (l.depth).max(r.depth)
//        override def intadds = l.intadds + r.intadds
//    }
//
//
//    /**
//     * Addition expressions.
//     */
//    case class Add(l: Exp, r: Exp) extends Binary(l, r) {
//        override def value = l.value + r.value
//        override def intadds =
//            (l, r) match {
//                case (Num(_), Num(_)) => 1
//                case _ => super.intadds
//            }
//    }
//
//
//    /**
//     * Subtraction expressions.
//     */
//    case class Sub(l: Exp, r: Exp) extends Binary(l, r) {
//        override def value = l.value - r.value
//    }
//
//
//    /**
//     * Multiplication expressions.
//     */
//    case class Mul(l: Exp, r: Exp) extends Binary(l, r) {
//        override def value = l.value * r.value
//    }
//
//
//    /**
//     * Division expressions.
//     */
//    case class Div(l: Exp, r: Exp) extends Binary(l, r) {
//        // Hack: no errors, so return zero for divide by zero
//        override def value = if (r.value == 0) 0 else l.value / r.value
//        override def divsbyzero =
//            l.divsbyzero + (r match {
//                case Num(0) => 1
//                case _ => r.divsbyzero
//            })
//    }
//
//
//    /**
//     * Statements.
//     */
//    abstract class Stmt extends ImperativeNode {
//
//
//        /**
//         * The set of all variable references in the statement.
//         */
//        def vars: Set[Idn] = Set()
//
//
//    }
//
//
//    /**
//     * Empty statements.
//     */
//    case class Null() extends Stmt
//
//
//    /**
//     * Statement sequences.
//     */
//    case class Seqn(ss: Seq[Stmt]) extends Stmt {
//        override def vars = Set(ss flatMap (_ vars): _*)
//    }
//
//
//    /**
//     * Assignment statements.
//     */
//    case class Asgn(v: Var, e: Exp) extends Stmt {
//        override def vars = Set(v.s)
//    }
//
//
//    /**
//     * While loops.
//     */
//    case class While(e: Exp, b: Stmt) extends Stmt {
//        override def vars = e.vars ++ b.vars
//    }
//
//
//    //    // Congruences
//    //
//    //
//    //    def Num (s1 : => Strategy) : Strategy =
//    //        rulefs {
//    //            case _ : Num =>
//    //                congruence (s1)
//    //        }
//    //
//    //
//    //    def Var (s1 : => Strategy) : Strategy =
//    //        rulefs {
//    //            case _ : Var =>
//    //                congruence (s1)
//    //        }
//    //
//    //
//    //    def Neg (s1 : => Strategy) : Strategy =
//    //        rulefs {
//    //            case _ : Var =>
//    //                congruence (s1)
//    //        }
//    //
//    //
//    //    def Add (s1 : => Strategy, s2 : => Strategy) : Strategy =
//    //        rulefs {
//    //            case _ : Add =>
//    //                congruence (s1, s2)
//    //        }
//    //
//    //
//    //    def Sub (s1 : => Strategy, s2 : => Strategy) : Strategy =
//    //        rulefs {
//    //            case _ : Sub =>
//    //                congruence (s1, s2)
//    //        }
//    //
//    //
//    //    def Mul (s1 : => Strategy, s2 : => Strategy) : Strategy =
//    //        rulefs {
//    //            case _ : Mul =>
//    //                congruence (s1, s2)
//    //        }
//    //
//    //
//    //    def Div (s1 : => Strategy, s2 : => Strategy) : Strategy =
//    //        rulefs {
//    //            case _ : Div =>
//    //                congruence (s1, s2)
//    //        }
//    //
//    //
//    //    def Seqn (s1 : => Strategy) : Strategy =
//    //        rulefs {
//    //            case _ : Seqn =>
//    //                congruence (s1)
//    //        }
//    //
//    //
//    //    def Asgn (s1 : => Strategy, s2 : => Strategy) : Strategy =
//    //        rulefs {
//    //            case _ : Asgn =>
//    //                congruence (s1, s2)
//    //        }
//    //
//    //
//    //    def While (s1 : => Strategy, s2 : => Strategy) : Strategy =
//    //        rulefs {
//    //            case _ : While =>
//    //                congruence (s1, s2)
//    //        }
//
//
//}
//
//@RunWith(classOf[JUnitRunner])
//class ParentProblem extends FunSuite with ShouldMatchers {
//
//    import TmpAST._
//
//
//    test("cloning a term with sharing gives an equal but not eq term") {
//        val c = Add(Num(1), Num(2))
//        val d = Add(Num(1), Num(2))
//        val e = Add(Num(3), Num(4))
//        val t = Add(Mul(c,
//            Sub(c,
//                d)),
//            Add(Add(e,
//                Num(5)),
//                e))
//        val u = Add(Mul(Add(Num(1), Num(2)),
//            Sub(Add(Num(1), Num(2)),
//                d)),
//            Add(Add(Add(Num(3), Num(4)),
//                Num(5)),
//                Add(Num(3), Num(4))))
//
//        assert(!isTree(t))
//        assert(isTree(u))
//
//        val clone = everywherebu(rule {case n: ImperativeNode => n.clone()})
//        val ct = clone(t)
//
//        // Must get the right answer (==)
//        expect(Some(u))(ct)
//
//        // Must not get the original term (eq)
//        expectnotsame(Some(t))(ct)
//
//        ensureTree(ct.get.asInstanceOf[Attributable])
//        assert(isTree(ct.get.asInstanceOf[Attributable]))
//
//        //         // Check the terms at the positions of the two c occurrences
//        //         // against each other, since they are eq to start but should
//        //         // not be after
//        val mul = ct.get.asInstanceOf[Add].l.asInstanceOf[Mul]
//        val c1 = mul.l
//        val mulsub = mul.r.asInstanceOf[Sub]
//        val c2 = mulsub.l
//        expectnotsame(c1)(c2)
//        //
//        //         // Check the terms at the positions of the two c ocurrences
//        //         // against the one at the position of the d occurrence (which
//        //         // is == but not eq to the two original c's)
//        val d1 = mulsub.r
//        expectnotsame(c1)(d1)
//        expectnotsame(c2)(d1)
//    }
//
//    private def isTree(ast: Attributable): Boolean =
//        ast.children.forall(c => (c.parent eq ast) && isTree(c))
//    private def ensureTree(ast: Attributable) {
//        for (c <- ast.children) {
//            c.parent = ast
//            ensureTree(c)
//        }
//    }
//
//
//    /**
//     * Compare two optional terms.  Use reference equality for references
//     * and value equality for non-reference values.
//     */
//    def same(v: Option[Term], optv: Option[Term]): Boolean =
//        (v, optv) match {
//            case (Some(v1: AnyRef), Some(v2: AnyRef)) => v1 eq v2
//            case (Some(v1), Some(v2)) => v1 == v2
//            case (None, None) => true
//            case _ => false
//        }
//
//    /**
//     * Analogous to ScalaTest's expect but it uses same to compare
//     * the two values instead of equality.
//     */
//    def expectsame(expected: Option[Term])(actual: Option[Term]) {
//        if (!same(expected, actual)) {
//            fail("Expected same object as " + expected + ", but got " + actual)
//        }
//    }
//
//
//    /**
//     * Analogous to ScalaTest's expect but it uses same to compare
//     * the two values instead of equality.
//     */
//    def expectnotsame(expected: Option[Term])(actual: Option[Term]) {
//        if (same(expected, actual)) {
//            fail("Expected not same object as " + expected + ", but got " + actual)
//        }
//    }
//
//    /**
//     * Analogous to ScalaTest's expect but it uses same to compare
//     * the two values instead of equality.
//     */
//    def expectnotsame(expected: Any)(actual: Any) {
//        if (same(expected, actual)) {
//            fail("Expected not same object as " + expected + ", but got " + actual)
//        }
//    }
//
//    /**
//     * Compare two values.  Use reference equality for references
//     * and value equality for non-references.  If the values are
//     * both Some values, perform the check on the wrapped values.
//     */
//    def same(v1: Any, v2: Any): Boolean =
//        (v1, v2) match {
//            case (Some(r1: AnyRef), Some(r2: AnyRef)) => r1 eq r2
//            case (Some(v1), Some(v2)) => v1 == v2
//            case (None, None) => true
//            case (r1: AnyRef, r2: AnyRef) => r1 eq r2
//            case _ => v1 == v2
//        }
//}