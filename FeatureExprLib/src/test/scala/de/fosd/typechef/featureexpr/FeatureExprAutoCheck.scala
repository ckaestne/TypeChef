package de.fosd.typechef.featureexpr

import org.scalacheck._
import Gen._
import FeatureExpr._

object FeatureExprAutoCheck extends Properties("FeatureExpr") {


    val featureNames = List("a", "b", "c", "d", "e", "f")
    val genAtomicFeatureWithDeadAndBase =
        oneOf(FeatureExpr.base :: FeatureExpr.dead :: featureNames.map(FeatureExpr.createDefinedExternal(_)))
    val genAtomicFeatureWithoutDeadAndBase =
        oneOf(featureNames.map(FeatureExpr.createDefinedExternal(_)))

    implicit def arbFeatureExpr: Arbitrary[FeatureExpr] = Arbitrary{
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

    property("and1") = Prop.forAll((a: FeatureExpr) => (a and FeatureExpr.base) equivalentTo a)
    property("and0") = Prop.forAll((a: FeatureExpr) => (a and FeatureExpr.dead) equivalentTo FeatureExpr.dead)
    property("andSelf") = Prop.forAll((a: FeatureExpr) => (a and a) equivalentTo a)
    property("or1") = Prop.forAll((a: FeatureExpr) => (a or base) equivalentTo base)
    property("or0") = Prop.forAll((a: FeatureExpr) => (a or dead) equivalentTo a)
    property("orSelf") = Prop.forAll((a: FeatureExpr) => (a or a) equivalentTo a)
    property("simplify does not change satisfiability") = Prop.forAll((a: FeatureExpr) => a.simplify equivalentTo a)

    property("a eq a") = Prop.forAll((a: FeatureExpr) => a eq a)
    property("a equals a") = Prop.forAll((a: FeatureExpr) => a equals a)
    property("a equivalent a") = Prop.forAll((a: FeatureExpr) => a equivalentTo a)
    property("simplify does not change pointer equality") = Prop.forAll((a: FeatureExpr) => a eq (a.simplify))
    property("simplify does not change equality") = Prop.forAll((a: FeatureExpr) => a equals (a.simplify))
    property("simplify does not change equivalence") = Prop.forAll((a: FeatureExpr) => a equivalentTo (a.simplify))

    property("creating (a and b) twice creates the same object") = Prop.forAll((a: FeatureExpr, b: FeatureExpr) => (a and b) eq (a and b))
    property("creating (a or b) twice creates the same object") = Prop.forAll((a: FeatureExpr, b: FeatureExpr) => (a or b) eq (a or b))
    property("creating (not a) twice creates the same object") = Prop.forAll((a: FeatureExpr) => (a.not) eq (a.not))
    property("applying not twice yields the same object") = Prop.forAll((a: FeatureExpr) => a eq (a.not.not))

    property("SAT(toCNF) == SAT(toEquiCNF)") = Prop.forAll((a: FeatureExpr) => new SatSolver().isSatisfiable(a.toCNF) == new SatSolver().isSatisfiable(a.toEquiCNF))



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