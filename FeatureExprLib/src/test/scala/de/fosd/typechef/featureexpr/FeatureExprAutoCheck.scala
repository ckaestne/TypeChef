package de.fosd.typechef.featureexpr

import org.scalacheck._
import Gen._
import FeatureExpr._
import Prop._

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

    property("a eq a") = Prop.forAll((a: FeatureExpr) => a eq a)
    property("a equals a") = Prop.forAll((a: FeatureExpr) => a equals a)
    property("a equivalent a") = Prop.forAll((a: FeatureExpr) => a equivalentTo a)
    //    property("simplify does not change pointer equality") = Prop.forAll((a: FeatureExpr) => a eq (a.simplify))
    //    property("simplify does not change equality") = Prop.forAll((a: FeatureExpr) => a equals (a.simplify))
    //    property("simplify does not change equivalence") = Prop.forAll((a: FeatureExpr) => a equivalentTo (a.simplify))

    property("creating (a and b) twice creates the same object") = Prop.forAll((a: FeatureExpr, b: FeatureExpr) => (a and b) eq (a and b))
    property("creating (a or b) twice creates the same object") = Prop.forAll((a: FeatureExpr, b: FeatureExpr) => (a or b) eq (a or b))
    property("creating (not a) twice creates the same object") = Prop.forAll((a: FeatureExpr) => (a.not) eq (a.not))
    property("applying not twice yields the same object") = Prop.forAll((a: FeatureExpr) => a eq (a.not.not))

    property("toCNF produces CNF") = Prop.forAll((a: FeatureExpr) => NFBuilder.isCNF(a.toCNF))
    property("toEquiCNF produces CNF") = Prop.forAll((a: FeatureExpr) => NFBuilder.isCNF(a.toCnfEquiSat))
    property("SAT(toCNF) == SAT(toEquiCNF)") = Prop.forAll((a: FeatureExpr) => new SatSolver().isSatisfiable(a.equiCNF) == new SatSolver().isSatisfiable(a.cnf))

    property("cnf does not change satisifiability") = Prop.forAll((a: FeatureExpr, b: FeatureExpr) =>
        ((a and b).isSatisfiable == (a.toCNF and (b.toCNF)).isSatisfiable) &&
                ((a or b).isSatisfiable == (a.toCNF or (b.toCNF)).isSatisfiable) &&
                ((a not).isSatisfiable == (a.toCNF.not).isSatisfiable)
    )

    property("taut(a=>b) == contr(a and !b)") = Prop.forAll((a: FeatureExpr, b: FeatureExpr) => a.implies(b).isTautology() == a.and(b.not).isContradiction)

    property("featuremodel.tautology") = Prop.forAll((a: FeatureExpr, b: FeatureExpr) => (!a.isDead) ==> {
        val fm = FeatureModel.create(a)
        b.isTautology(fm) == a.implies(b).isTautology
    })

    property("featuremodel.sat") = Prop.forAll((a: FeatureExpr, b: FeatureExpr) => (!a.isDead) ==> {
        val fm = FeatureModel.create(a)
        b.isSatisfiable(fm) == a.and(b).isSatisfiable
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