package de.fosd.typechef.featureexpr

import org.sat4j.core.{VecInt, Vec}
import org.sat4j.specs.{IVec, IVecInt}
import scala.collection.{mutable, immutable}

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
 */
class FeatureModel(val variables: Map[String, Int], val clauses: IVec[IVecInt], val lastVarId: Int) {
    /**
     * make the feature model stricter by a formula
     */
    def and(expr: FeatureExpr /*CNF*/) = this
    //    def and(expr: FeatureExpr /*CNF*/) = if (expr == FeatureExpr.base) this
    //    else {
    //        val cnf = expr.toCNF
    //        try {
    //            assert(!expr.isContradiction(null))
    //            val (newVariables, newLastVarId) = FeatureModel.getVariables(cnf, lastVarId, variables)
    //            val newClauses = FeatureModel.addClauses(cnf, newVariables, clauses)
    //            new FeatureModel(newVariables, newClauses, newLastVarId)
    //        } catch {
    //            case e: Exception => println("FeatureModel.and: Exception: " + e + " with expr: " + expr + " and cnf: " + cnf); throw e
    //        }
    //    }
}

/**
 * empty feature model
 */
object NoFeatureModel extends FeatureModel(Map(), new Vec(), 0)

/**
 * companion object to create feature models
 */
object FeatureModel {
    /**
     * create an empty feature model
     */
    def empty = NoFeatureModel

    /**
     * create a feature model from a feature expression
     */
    def create(expr: FeatureExpr) = NoFeatureModel.and(expr)

    /**
     * create a feature model by loading a CNF file
     * (proprietary format used previously by LinuxAnalysis tools)
     */
    def createFromCNFFile(file: String) = empty
    //    {
    //        var variables: Map[String, Int] = Map()
    //        var varIdx = 0
    //        val clauses = new Vec[IVecInt]()
    //
    //        for (line <- scala.io.Source.fromFile(file).getLines) {
    //            if ((line startsWith "@ ") || (line startsWith "$ ")) {
    //                varIdx += 1
    //                variables = variables.updated("CONFIG_" + line.substring(2), varIdx)
    //            } else {
    //                val vec = new VecInt()
    //                for (literal <- line.split(" "))
    //                    vec.push(lookupLiteral(literal, variables))
    //                clauses.push(vec)
    //            }
    //
    //        }
    //        new FeatureModel(variables, clauses, varIdx)
    //    }

    /**
     * load a standard Dimacs file as feature model
     */
    def createFromDimacsFile(file: String) = empty
    //{
    //        var variables: Map[String, Int] = Map()
    //        val clauses = new Vec[IVecInt]()
    //        var maxId = 0
    //
    //        for (line <- scala.io.Source.fromFile(file).getLines) {
    //            if (line startsWith "c ") {
    //                val entries = line.substring(2).split(" ")
    //                val id = if (entries(0) endsWith "$")
    //                    entries(0).substring(0, entries(0).length - 1).toInt
    //                else
    //                    entries(0).toInt
    //                maxId = scala.math.max(id, maxId)
    //                variables = variables.updated("CONFIG_" + entries(1), id)
    //            } else if ((line startsWith "p ") || (line.trim.size == 0)) {
    //                //comment, do nothing
    //            } else {
    //                val vec = new VecInt()
    //                for (literal <- line.split(" "))
    //                    if (literal != "0")
    //                        vec.push(literal.toInt)
    //                clauses.push(vec)
    //            }
    //
    //        }
    //        assert(maxId == variables.size)
    //        new FeatureModel(variables, clauses, maxId)
    //    }
    /**
     * special reader for the -2var model used by the LinuxAnalysis tools from waterloo
     */
    def createFromDimacsFile_2Var(file: String) = empty
    //{
    //        var variables: Map[String, Int] = Map()
    //        val clauses = new Vec[IVecInt]()
    //        var maxId = 0
    //
    //        for (line <- scala.io.Source.fromFile(file).getLines) {
    //            if (line startsWith "c ") {
    //                val entries = line.substring(2).split(" ")
    //                val id = if (entries(0) endsWith "$")
    //                    entries(0).substring(0, entries(0).length - 1).toInt
    //                else
    //                    entries(0).toInt
    //                maxId = scala.math.max(id, maxId)
    //                val varname = "CONFIG_" + (/*if (entries(1).endsWith("_m")) entries(1).substring(0, entries(1).length - 2)+"_MODULE" else*/ entries(1))
    //                if (variables contains varname)
    //                    assert(false, "variable " + varname + " declared twice")
    //                variables = variables.updated(varname, id)
    //            } else if ((line startsWith "p ") || (line.trim.size == 0)) {
    //                //comment, do nothing
    //            } else {
    //                val vec = new VecInt()
    //                for (literal <- line.split(" "))
    //                    if (literal != "0")
    //                        vec.push(literal.toInt)
    //                clauses.push(vec)
    //            }
    //
    //        }
    //        assert(maxId == variables.size)
    //        new FeatureModel(variables, clauses, maxId)
    //    }

    //    private def lookupLiteral(literal: String, variables: Map[String, Int]) =
    //        if (literal.startsWith("-"))
    //            -variables.getOrElse("CONFIG_" + (literal.substring(1)), throw new Exception("variable not declared"))
    //        else
    //            variables.getOrElse("CONFIG_" + literal, throw new Exception("variable not declared"))
    //
    //
    //    private[FeatureModel] def getVariables(expr: FeatureExpr /*CNF*/ , lastVarId: Int, oldMap: Map[String, Int] = Map()): (Map[String, Int], Int) = {
    //        val uniqueFlagIds = mutable.Map[String, Int]()
    //        uniqueFlagIds ++= oldMap
    //        var lastId = lastVarId
    //
    //        for (clause <- CNFHelper.getCNFClauses(expr))
    //            for (literal <- CNFHelper.getDefinedExprs(clause))
    //                if (!uniqueFlagIds.contains(literal.satName)) {
    //                    lastId = lastId + 1
    //                    uniqueFlagIds(literal.satName) = lastId
    //                }
    //        (immutable.Map[String, Int]() ++ uniqueFlagIds, lastId)
    //    }
    //
    //
    //    private[FeatureModel] def addClauses(cnf: FeatureExpr /*CNF*/ , variables: Map[String, Int], oldVec: IVec[IVecInt] = null): Vec[IVecInt] = {
    //        val result = new Vec[IVecInt]()
    //        if (oldVec != null)
    //            oldVec.copyTo(result)
    //        for (clause <- CNFHelper.getCNFClauses(cnf); if (clause != True))
    //            result.push(SatSolver.getClauseVec(variables, clause))
    //        result
    //    }
}
