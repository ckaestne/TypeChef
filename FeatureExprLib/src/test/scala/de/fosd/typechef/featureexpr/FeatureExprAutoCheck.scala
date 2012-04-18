package de.fosd.typechef.featureexpr

import org.scalacheck._
import Gen._
import FeatureExprFactory.sat._
import Prop.propBoolean
import java.io._
import sat.SATFeatureModel

object FeatureExprAutoCheck extends Properties("FeatureExpr") {
    def feature(a: String) = FeatureExprFactory.createDefinedExternal(a)
    val featureNames = List("a", "b", "c", "d", "e", "f")
    val a = feature("a")
    val b = feature("b")
    val c = feature("c")
    val d = feature("d")
    val e = feature("e")
    val f = feature("f")

    val genAtomicFeatureWithDeadAndBase =
        oneOf(True :: False :: featureNames.map(feature(_)))
    val genAtomicFeatureWithoutDeadAndBase =
        oneOf(featureNames.map(FeatureExprFactory.createDefinedExternal(_)))

    implicit def arbFeatureExpr: Arbitrary[FeatureExpr] = Arbitrary {
        genFeatureExpr(genAtomicFeatureWithDeadAndBase, {
            x => x
        })
    }
    def getNonDeadFeatureExpr: Gen[FeatureExpr] = genFeatureExpr(genAtomicFeatureWithoutDeadAndBase, {
        x => 3
    })

    private def genFeatureExpr(genAtomicFeature: Gen[FeatureExpr], size: (Int) => Int) = {
        def genCompoundFeature(size: Int) = oneOf(
            for {
                a <- genFeatureExpr(size)
                b <- genFeatureExpr(size)
            } yield a and b,
            for {
                a <- genFeatureExpr(size)
                b <- genFeatureExpr(size)
            } yield a or b,
            for {
                a <- genFeatureExpr(size)
            } yield a.not)

        def genFeatureExpr(size: Int): Gen[FeatureExpr] =
            if (size <= 0) genAtomicFeature
            else Gen.frequency((1, genAtomicFeature), (3, genCompoundFeature(size / 2)))
        Gen.sized(sz => genFeatureExpr(sz))
    }


    property("parse(print(x))==x") = Prop.forAll((a: FeatureExpr) => {
        val writer = new StringWriter()
        a.print(writer)
        val b = new FeatureExprParser().parse(new StringReader(writer.toString))
        a equivalentTo b
    })

    property("and1") = Prop.forAll((a: FeatureExpr) => (a and True) equivalentTo a)
    property("and0") = Prop.forAll((a: FeatureExpr) => (a and False) equivalentTo False)
    property("andSelf") = Prop.forAll((a: FeatureExpr) => (a and a) equivalentTo a)
    property("or1") = Prop.forAll((a: FeatureExpr) => (a or True) equivalentTo True)
    property("or0") = Prop.forAll((a: FeatureExpr) => (a or False) equivalentTo a)
    property("orSelf") = Prop.forAll((a: FeatureExpr) => (a or a) equivalentTo a)

    property("a eq a") = Prop.forAll((a: FeatureExpr) => a eq a)
    property("a equals a") = Prop.forAll((a: FeatureExpr) => a equals a)
    property("a equivalent a") = Prop.forAll((a: FeatureExpr) => a equivalentTo a)

    property("a implies a") = Prop.forAll((a: FeatureExpr) => (a implies a).isTautology())
    //    property("simplify does not change pointer equality") = Prop.forAll((a: FeatureExpr) => a eq (a.simplify))
    //    property("simplify does not change equality") = Prop.forAll((a: FeatureExpr) => a equals (a.simplify))
    //    property("simplify does not change equivalence") = Prop.forAll((a: FeatureExpr) => a equivalentTo (a.simplify))

    property("creating (a and b) twice creates equal object") = Prop.forAll((a: FeatureExpr, b: FeatureExpr) => (a and b) == (a and b))
    property("creating (a or b) twice creates equal object") = Prop.forAll((a: FeatureExpr, b: FeatureExpr) => (a or b) == (a or b))
    property("creating (not a) twice creates equal object") = Prop.forAll((a: FeatureExpr) => (a.not) == (a.not))
    //    property("applying not twice yields the same object") = Prop.forAll((a: FeatureExpr) => a eq (a.not.not)) //does not necessarily hold any more; they are only equivalent
    property("applying not twice yields an equivalent formula") = Prop.forAll((a: FeatureExpr) => a equivalentTo (a.not.not)) //does not necessarily hold any more; they are only equivalent

    property("Commutativity wrt. equivalence: (a and b) produces the same object as (b and a)") = Prop.forAll((a: FeatureExpr, b: FeatureExpr) => (a and b) equivalentTo (b and a))
    property("Commutativity wrt. equivalence: (a or b) produces the same object as (b or a)") = Prop.forAll((a: FeatureExpr, b: FeatureExpr) => (a or b) equivalentTo (b or a))

    //    def associativeAnd = (a: FeatureExpr, b: FeatureExpr, c: FeatureExpr) => ((a and b) and c) eq (a and (b and c))
    //    def associativeOr = (a: FeatureExpr, b: FeatureExpr, c: FeatureExpr) => ((a or b) or c) eq (a or (b or c))
    //    property("Associativity wrt. object identity for and") = Prop.forAll(associativeAnd)
    //    property("Associativity wrt. object identity for or") = Prop.forAll(associativeOr)
    //
    //    //This case can't work without SAT-equivalence checking: opt. patterns will remove e from the clause!! And SAT-equivalence checking would depend on a suitable
    //    //hash function!! With few clauses one could use the truth table in theory, but I don't think it's worth.
    //    property("Special case assocAnd") = associativeAnd(f not, a, e or a)
    //    property("Special case assocOr") = associativeOr(f not, a, e and a)
    //
    //    property("Associativity + commutativity wrt. object identity for and") = Prop.forAll((a: FeatureExpr, b: FeatureExpr, c: FeatureExpr) => ((a and b) and c) eq ((c and b) and a))
    //    property("Associativity + commutativity wrt. object identity for or") = Prop.forAll((a: FeatureExpr, b: FeatureExpr, c: FeatureExpr) => ((a or b) or c) eq ((c or b) or a))


    property("taut(a=>b) == contr(a and !b)") = Prop.forAll((a: FeatureExpr, b: FeatureExpr) => a.implies(b).isTautology() == a.and(b.not).isContradiction)

    property("featuremodel.tautology") = Prop.forAll((a: FeatureExpr, b: FeatureExpr) => (!a.isContradiction) ==> {
        val fm = SATFeatureModel.create(a)
        b.isTautology(fm) == a.implies(b).isTautology
    })

    property("featuremodel.sat") = Prop.forAll((a: FeatureExpr, b: FeatureExpr) => (!a.isContradiction) ==> {
        val fm = SATFeatureModel.create(a)
        b.isSatisfiable(fm) == a.and(b).isSatisfiable
    })

    property("trueSat") = True.isSatisfiable
    property("falseSat") = !(False.isSatisfiable())

    property("can_print") = Prop.forAll((a: FeatureExpr) => {
        a.toTextExpr;
        a.debug_print(0);
        true
    })
    property("can_calcSize") = Prop.forAll((a: FeatureExpr) => {
        a.size;
        true
    })


    //
    //  property("endsWith") = Prop.forAll((a: String, b: String) => (a+b).endsWith(b))
    //
    //  // Is this really always true?
    //  property("concat") = Prop.forAll((a: String, b: String) =>
    //    (a+b).length > a.length && (a+b).length > b.length
    //  )
    //
    //  property("substring") = Prop.forAll((a: String, b: String) =>
    //    (a+b).substring(a.length) == b
    //  )
    //
    //  property("substring") = Prop.forAll((a: String, b: String, c: String) =>
    //    (a+b+c).substring(a.length, a.length+b.length) == b
    //  )
}
