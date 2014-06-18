package de.fosd.typechef.featureexpr

import scala.io.Source

/**
 * Factory interface to create feature models
 *
 * A classname implementing this interface can be provided as command line option to the frontend
 */

trait FeatureModelFactory {
    def empty: FeatureModel

    /**
     * create a feature model from a feature expression
     */
    def create(expr: FeatureExpr): FeatureModel

    /**
     * load a standard Dimacs file as feature model. comments are used in format "c <ID> <NAME>" to
     * describe a mapping from ids to feature names. Those feature names can be rewritten, eg
     * to add a prefix with the translateNames parameter.
     *
     * if autoAddVariables is activated all variables not defined by comments are loaded as
     * "fresh" variables with generated names. Those should never be used in formulas but originate
     * from the equisat transformation.
     */
    def createFromDimacsFile(file: Source, translateNames: String => String, autoAddVariables: Boolean = true): FeatureModel


    /**
     * default implementation
     *
     * does prefix all loaded names with CONFIG_ by default
     */
    def createFromDimacsFile(file: Source): FeatureModel = createFromDimacsFile(file, "CONFIG_" + _)
    def createFromDimacsFile(file: String): FeatureModel = createFromDimacsFile(Source.fromFile(file), "CONFIG_" + _)
    def createFromDimacsFilePrefix(file: String, prefix: String): FeatureModel = createFromDimacsFile(Source.fromFile(file), prefix + _)


//    /**
//     * special reader for the -2var model used by the LinuxAnalysis tools from waterloo
//     *
//     * prefixes all loaded names with CONFIG_ by default and translates _2 postfix in variable
//     * names to _MODULE
//     */
//    def createFromDimacsFile_2Var(file: Source): FeatureModel = createFromDimacsFile(file,
//        n => "CONFIG_" + (/*if (n.endsWith("_m")) n.dropRight(2)+"_MODULE" else*/ n)
//    )

    /**
     * common part for loading dimacs files
     *
     * returns name to id mapping, list of clauses, and maximal id declared/found
     */
    protected def loadDimacsData(file: Source, translateNames: String => String, autoAddVariables: Boolean): (Map[String, Int], List[List[Int]], Int) = {
        var variables: Map[String, Int] = Map()
        var clauses = List[List[Int]]()
        var maxId = 0

        var numDeclaredVariables = -1
        var numDeclaredClauses = -1

        for (line <- file.getLines) {
            if (line startsWith "c ") {
                val entries = line.substring(2).split(" ")
                val id = if (entries(0) endsWith "$")
                    entries(0).substring(0, entries(0).length - 1).toInt
                else
                    entries(0).toInt
                maxId = scala.math.max(id, maxId)
                val varname = translateNames(entries(1))
                assert(!(variables contains varname), "Inconsistent dimacs file: variable " + varname + " declared twice")
                variables += (varname -> id)
            } else if (line startsWith "p ") {
                val entries = line.split(" ")
                assert(entries(1) == "cnf")
                numDeclaredVariables = entries(2).toInt
                numDeclaredClauses = entries(3).toInt
            } else if (line.trim.size == 0) {
                //comment, do nothing
            } else {
                var vec = List[Int]()
                for (literal <- line.split(" "))
                    if (literal != "0")
                        vec ::= literal.toInt
                clauses ::= vec
            }

        }
        assert(clauses.size == numDeclaredClauses, "Inconsistent dimacs file: number of clauses %d differes from declared number of clauses %d".format(clauses.size, numDeclaredClauses))
        if (!autoAddVariables) {
            assert(maxId == variables.size, "Inconsistent dimacs file: largest variable id " + maxId + " differs from number of variables " + variables.size)
        } else {
            assert(maxId >= variables.size, "Inconsistent dimacs file: largest variable id " + maxId + " is smaller than the number of variables " + variables.size)
            assert(maxId <= numDeclaredVariables, "Inconsistent dimacs file: largest variable id " + maxId + " is larger than declared number of variables " + numDeclaredVariables)
            var freshId = 0
            val valSet = variables.values.toSet
            def freshName: String = { freshId += 1; "__fresh" + freshId }
            for (i <- 1 to numDeclaredVariables)
                if (!(valSet contains i))
                    variables += (freshName -> i)
            maxId = numDeclaredVariables
        }

        (variables, clauses, maxId)
    }


}