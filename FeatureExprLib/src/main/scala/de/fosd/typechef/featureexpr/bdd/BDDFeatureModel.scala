package de.fosd.typechef.featureexpr.bdd

import org.sat4j.core.{VecInt, Vec}
import org.sat4j.specs.{IVec, IVecInt}
import de.fosd.typechef.featureexpr._
import scala.Predef._
import scala.io.Source

/**
 * the feature model is a special container for a single feature expression
 * that is used very often in a conjunction
 *
 * it stores the formula in an array structure easy to process by the
 * sat solver
 *
 *
 * it can load an expression from a FeatureExpr or from a file in CNF
 * format
 *
 * extra constraints contain arbitrary formulas that are used in every query. may be expensive to calculate each time.
 *
 * assumedFalse and assumedTrue can hold sets of feature names that are known to be true or false (typically from
 * partial configurations). Those are cheap to pass to the SAT solver.
 *
 */
class BDDFeatureModel(val variables: Map[String, Int], val clauses: IVec[IVecInt], val lastVarId: Int,
                      val extraConstraints: BDDFeatureExpr,
                      val assumedFalse: Set[String], val assumedTrue: Set[String]) extends FeatureModel {
    /**
     * make the feature model stricter by a formula
     */
    def and(expr: FeatureExpr /*CNF*/) = {
        new BDDFeatureModel(variables, clauses, lastVarId, CastHelper.asBDDFeatureExpr(extraConstraints and expr), assumedFalse, assumedTrue)
    }


    def assumeTrue(featurename: String) =
        new BDDFeatureModel(variables, clauses, lastVarId, extraConstraints, assumedFalse, assumedTrue + featurename)

    def assumeFalse(featurename: String) =
        new BDDFeatureModel(variables, clauses, lastVarId, extraConstraints, assumedFalse + featurename, assumedTrue)

    /** helper function */
    def assumptions: BDDFeatureExpr =
        (assumedTrue.map(BDDFeatureExprFactory.createDefinedExternal(_)).fold(BDDFeatureExprFactory.True)(_ and _) and
            assumedFalse.map(BDDFeatureExprFactory.createDefinedExternal(_).not).fold(BDDFeatureExprFactory.True)(_ and _)).asInstanceOf[BDDFeatureExpr]
}

/**
 * empty feature model
 */
object BDDNoFeatureModel extends BDDFeatureModel(Map(), new Vec(), 0, True, Set(), Set())

/**
 * companion object to create feature models
 *
 * TODO: this code is replicated from SATFeatureModel, integrate again
 */
object BDDFeatureModel extends FeatureModelFactory {
    /**
     * create an empty feature model
     */
    def empty: BDDFeatureModel = BDDNoFeatureModel

    /**
     * create a feature model from a feature expression
     */
    def create(expr: FeatureExpr) = BDDNoFeatureModel.and(expr)

    /**
     * create a feature model by loading a CNF file
     * (proprietary format used previously by LinuxAnalysis tools)
     */
    def createFromCNFFile(file: String) = {
        var variables: Map[String, Int] = Map()
        var varIdx = 0
        val clauses = new Vec[IVecInt]()

        def lookupLiteral(literal: String, variables: Map[String, Int]) =
            if (literal.startsWith("-"))
                -variables.getOrElse("CONFIG_" + (literal.substring(1)), throw new Exception("variable not declared"))
            else
                variables.getOrElse("CONFIG_" + literal, throw new Exception("variable not declared"))


        for (line <- scala.io.Source.fromFile(file).getLines) {
            if ((line startsWith "@ ") || (line startsWith "$ ")) {
                varIdx += 1
                variables = variables.updated("CONFIG_" + line.substring(2), varIdx)
            } else {
                val vec = new VecInt()
                for (literal <- line.split(" "))
                    vec.push(lookupLiteral(literal, variables))
                clauses.push(vec)
            }

        }
        new BDDFeatureModel(variables, clauses, varIdx, BDDFeatureExprFactory.TrueB, Set(), Set())
    }

    /**
     * load a standard Dimacs file as feature model
     */
    def createFromDimacsFile(file: Source, translateNames: String => String, autoAddVariables: Boolean): FeatureModel = {
        val (variables, clauses, maxId) = loadDimacsData(file, translateNames, autoAddVariables)
        val vecclauses = new Vec[IVecInt]()

        for (clause <- clauses) {
            val vec = new VecInt()
            for (literal <- clause)
                vec.push(literal)
            vecclauses.push(vec)
        }


        new BDDFeatureModel(variables, vecclauses, maxId, BDDFeatureExprFactory.TrueB, Set(), Set())
    }


}
