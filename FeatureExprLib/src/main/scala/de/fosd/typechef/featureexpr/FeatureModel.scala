package de.fosd.typechef.featureexpr

import org.sat4j.core.{VecInt, Vec}
import org.sat4j.specs.IVecInt

/**
 * the feature model is a special container for a single feature expression
 * that is used very often in a conjunction
 *
 * it stores the formula in an array structure easy to process by the
 * sat solver
 *
 *
 * it can load an expression from a FeatureExpr or from a file in CNF
 * format (TODO)
 *
 */
class FeatureModel(val variables: Map[String, Int], val clauses: org.sat4j.specs.IVec[org.sat4j.specs.IVecInt])

object NoFeatureModel extends FeatureModel(Map(), new Vec())

object FeatureModel {

    def empty = NoFeatureModel

    def create(expr: FeatureExpr) = {
        val cnf = expr.toCNF
        try {
            assert(!expr.isContradiction)
            val variables = getVariables(cnf)
            val clauses = addClauses(cnf, variables)
            new FeatureModel(variables, clauses)
        } catch {
            case e: Exception => println("FeatureModel.create: Exception: " + e + " with expr: " + expr + " and cnf: " + cnf); throw e
        }
    }

    def createFromCNFFile(file: String) = {
        var variables: Map[String, Int] = Map()
        var varIdx = 0
        val clauses = new Vec[IVecInt]()

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
        new FeatureModel(variables, clauses)
    }

    def createFromDimacsFile(file: String) = {
        var variables: Map[String, Int] = Map()
        val clauses = new Vec[IVecInt]()
        var maxId = 0

        for (line <- scala.io.Source.fromFile(file).getLines) {
            if (line startsWith "c ") {
                val entries = line.substring(2).split(" ")
                val id = if (entries(0) endsWith "$")
                    entries(0).substring(0, entries(0).length - 1).toInt
                else
                    entries(0).toInt
                maxId = scala.math.max(id, maxId)
                variables = variables.updated("CONFIG_" + entries(1), id)
            } else if ((line startsWith "p ") || (line.trim.size == 0)) {
                //comment, do nothing
            } else {
                val vec = new VecInt()
                for (literal <- line.split(" "))
                    if (literal != "0")
                        vec.push(literal.toInt)
                clauses.push(vec)
            }

        }
        assert(maxId == variables.size)
        new FeatureModel(variables, clauses)
    }
    /**
     * special reader for the -2var model
     */
    def createFromDimacsFile_2Var(file: String) = {
        var variables: Map[String, Int] = Map()
        val clauses = new Vec[IVecInt]()
        var maxId = 0

        for (line <- scala.io.Source.fromFile(file).getLines) {
            if (line startsWith "c ") {
                val entries = line.substring(2).split(" ")
                val id = if (entries(0) endsWith "$")
                    entries(0).substring(0, entries(0).length - 1).toInt
                else
                    entries(0).toInt
                maxId = scala.math.max(id, maxId)
                //only interested in variables with _1
                val varname = "CONFIG_" + (if (entries(1).endsWith("_1")) entries(1).substring(0, entries(1).length - 2) else entries(1))
                variables = variables.updated(varname, id)
            } else if ((line startsWith "p ") || (line.trim.size == 0)) {
                //comment, do nothing
            } else {
                val vec = new VecInt()
                for (literal <- line.split(" "))
                    if (literal != "0")
                        vec.push(literal.toInt)
                clauses.push(vec)
            }

        }
        assert(maxId == variables.size)
        new FeatureModel(variables, clauses)
    }

    private def lookupLiteral(literal: String, variables: Map[String, Int]) =
        if (literal.startsWith("-"))
            -variables.getOrElse("CONFIG_" + (literal.substring(1)), throw new Exception("variable not declared"))
        else
            variables.getOrElse("CONFIG_" + literal, throw new Exception("variable not declared"))


    private def getVariables(expr: FeatureExpr/*CNF*/): Map[String, Int] = {
        import scala.collection.mutable.Map
        val uniqueFlagIds: scala.collection.mutable.Map[String, Int] = Map();

        for (clause <- CNFHelper.getCNFClauses(expr))
            for (literal <- CNFHelper.getDefinedExprs(clause))
                if (!uniqueFlagIds.contains(literal.satName))
                    uniqueFlagIds(literal.satName) = uniqueFlagIds.size + 1
        scala.collection.immutable.Map[String, Int]() ++ uniqueFlagIds
    }


    private def addClauses(cnf: FeatureExpr/*CNF*/, variables: Map[String, Int]): Vec[IVecInt] = {
        val result = new Vec[IVecInt]()
        for (clause <- CNFHelper.getCNFClauses(cnf); if (clause!=True))
            result.push(SatSolver.getClauseVec(variables, clause))
        result
    }
}