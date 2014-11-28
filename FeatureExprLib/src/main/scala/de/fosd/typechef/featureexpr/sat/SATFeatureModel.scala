package de.fosd.typechef.featureexpr.sat

import org.sat4j.core.{VecInt, Vec}
import org.sat4j.specs.{IVec, IVecInt}
import scala.collection.{mutable, immutable}
import java.net.URI
import java.io.File
import de.fosd.typechef.featureexpr.{FeatureModelFactory, FeatureExpr, FeatureModel}
import java.io.FileWriter
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
 */
class SATFeatureModel(val variables: Map[String, Int], val clauses: IVec[IVecInt], val lastVarId: Int) extends FeatureModel {
    /**
     * make the feature model stricter by a formula
     */
    def and(expr: FeatureExpr /*CNF*/) =
        if (expr.isTautology) this
        else {
            assert(expr.isInstanceOf[SATFeatureExpr])
            val cnf = expr.asInstanceOf[SATFeatureExpr].toCNF
            try {
                assert(!expr.isContradiction(null))
                val (newVariables, newLastVarId) = SATFeatureModel.getVariables(cnf, lastVarId, variables)
                val newClauses = SATFeatureModel.addClauses(cnf, newVariables, clauses)
                new SATFeatureModel(newVariables, newClauses, newLastVarId)
            } catch {
                case e: Exception => println("FeatureModel.and: Exception: " + e + " with expr: " + expr + " and cnf: " + cnf); throw e
            }
        }
    def assumeTrue(featurename: String) = this.and(SATFeatureExprFactory.createDefinedExternal(featurename))
    def assumeFalse(featurename: String) = this.and(SATFeatureExprFactory.createDefinedExternal(featurename).not)


    def writeToFile(fileName: String, data: String) = {
        val fileWriter = new FileWriter(fileName)
        try {
            fileWriter.write(data)
        } finally {
            fileWriter.close()
        }

    }

    // export given FeatureModel fm to file name fileName
    // the format is:
    // ((not A) and B) or C, where A, B, and C are feature
    // names and not, and, and or are boolean functions
    def exportFM2DNF(fm: FeatureModel, fileName: String) = {
        // reverse the map of variables (fm.variables) Map[String, Int]
        // so we get a Map[Int, String] and can lookup feature names
        // using ids given from the system.
        val mIdFlagg = Map() ++ variables.toList.map(_.swap)
        var res = ""

        // return feature name or negated feature name based on given id
        def posNegFeatureName(id: Int) = {
            if (mIdFlagg.isDefinedAt(id.abs))
                Some("(" + (if (id > 0) "" else "not ") + mIdFlagg.get(id.abs).get + ")")
            else
                None
        }

        // adds element e between consecutive elements
        // e.g., intersperse '-' "Hello" ~> "H-e-l-l-o"
        def intersperse[T](e: T, l: List[T]): List[T] = {
            l match {
                case Nil => Nil
                case x :: Nil => x :: Nil
                case x :: xs => x :: e :: intersperse(e, xs)
            }
        }

        // generate or clauses
        var orcls: List[String] = List()
        for (i <- 0 to (clauses.size - 1)) {
            val cl = clauses.get(i)
            val values = cl.toArray.toList.map(posNegFeatureName)
            val definedValues = values.filter(_.isDefined).map(_.get)
            orcls ::= "(" + intersperse(" and ", definedValues).fold("")(_ + _) + ")"
        }

        res += intersperse(" or ", orcls).fold("")(_ + _)

        writeToFile(fileName, res)
    }

    // $COVERAGE-OFF$
    def writeToDimacsFile(file: File) {
        var fw: FileWriter = null
        try {
            fw = new FileWriter(file);
            val vars: Array[(String, Int)] = new Array(variables.size)
            variables.copyToArray(vars)
            def sortFunction(a: (String, Int), b: (String, Int)): Boolean = {
                a._2 < b._2
            }
            for ((varname, varid) <- vars.sortWith(sortFunction)) {
                val realVarname = if (varname.startsWith("CONFIG_")) varname.replaceFirst("CONFIG_", "") else varname
                fw.write("c " + varid + " " + realVarname + "\n")
            }
            var numClauses = 0;
            val clauseBuffer = new StringBuffer();
            for (clause: IVecInt <- clauses.toArray) {
                if (clause != null) {
                    numClauses += 1;
                    for (entry: Int <- clause.toArray) {
                        clauseBuffer.append(entry + " ");
                    }
                    clauseBuffer.append("0\n");
                }
            }
            fw.write("p cnf " + vars.length + " " + numClauses + "\n")
            fw.write(clauseBuffer.toString)
        } finally {
            if (fw != null) fw.close()
        }
    }
    // $COVERAGE-ON$
}

/**
 * empty feature model
 */
object SATNoFeatureModel extends SATFeatureModel(Map(), new Vec(), 0)

/**
 * companion object to create feature models
 */
object SATFeatureModel extends FeatureModelFactory {
    /**
     * create an empty feature model
     */
    def empty = SATNoFeatureModel

    /**
     * create a feature model from a feature expression
     */
    def create(expr: FeatureExpr) = SATNoFeatureModel.and(expr)

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
        new SATFeatureModel(variables, clauses, varIdx)
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

        new SATFeatureModel(variables, vecclauses, maxId)
    }


    private[SATFeatureModel] def getVariables(expr: SATFeatureExpr /*CNF*/ , lastVarId: Int, oldMap: Map[String, Int] = Map()): (Map[String, Int], Int) = {
        val uniqueFlagIds = mutable.Map[String, Int]()
        uniqueFlagIds ++= oldMap
        var lastId = lastVarId

        for (clause <- CNFHelper.getCNFClauses(expr))
            for (literal <- CNFHelper.getDefinedExprs(clause))
                if (!uniqueFlagIds.contains(literal.satName)) {
                    lastId = lastId + 1
                    uniqueFlagIds(literal.satName) = lastId
                }
        (immutable.Map[String, Int]() ++ uniqueFlagIds, lastId)
    }


    private[SATFeatureModel] def addClauses(cnf: SATFeatureExpr /*CNF*/ , variables: Map[String, Int], oldVec: IVec[IVecInt] = null): Vec[IVecInt] = {
        val result = new Vec[IVecInt]()
        if (oldVec != null)
            oldVec.copyTo(result)
        for (clause <- CNFHelper.getCNFClauses(cnf); if (clause != True))
            result.push(SatSolver.getClauseVec(variables, clause))
        result
    }
}
