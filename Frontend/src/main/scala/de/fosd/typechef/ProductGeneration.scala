package de.fosd.typechef

import conditional.{Conditional, Choice, Opt}
import crewrite.{CAnalysisFrontend, CASTEnv}
import featureexpr._

import parser.c.{AST, PrettyPrinter, TranslationUnit}
import typesystem.CTypeSystemFrontend
import java.io.{File, FileWriter}
import scala.collection.immutable.HashMap
import scala.collection.mutable.{BitSet, Queue}
import scala.Predef._
import scala._

/**
 *
 * User: rhein
 * Date: 4/2/12
 * Time: 3:45 PM
 *
 */

object ProductGeneration {
    /**Maps SingleFeatureExpr Objects to IDs (IDs only known/used in this file) */
    private var featureIDHashmap: Map[SingleFeatureExpr, Int] = null
    /**List of all features found in the currently processed file */
    private var features: List[SingleFeatureExpr] = null

    class SimpleConfiguration(private val config: scala.collection.immutable.BitSet) {
        def this(trueSet: List[SingleFeatureExpr], falseSet: List[SingleFeatureExpr]) = this(
        {
            var ret: scala.collection.mutable.BitSet = BitSet()
            for (tf: SingleFeatureExpr <- trueSet) ret.add(featureIDHashmap(tf))
            for (ff: SingleFeatureExpr <- falseSet) ret.remove(featureIDHashmap(ff))
            println("config created")
            ret.toImmutable
        }
        )

        def getTrueSet: Set[SingleFeatureExpr] = {
            features.filter({
                fex: SingleFeatureExpr => config.apply(featureIDHashmap(fex))
            }).toSet
        }

        def getFalseSet: Set[SingleFeatureExpr] = {
            features.filterNot({
                fex: SingleFeatureExpr => config.apply(featureIDHashmap(fex))
            }).toSet
        }

        override def toString(): String = {
            features.map(
            {
                fex: SingleFeatureExpr => if (config.apply(featureIDHashmap(fex))) fex else fex.not()
            }
            ).mkString("&&")
        }

        /**
         * This method assumes that all features in the parameter-set appear in either the trueList, or in the falseList
         * @param features
         * @return
         */
        def containsFeaturesAsEnabled(features: Set[SingleFeatureExpr]): Boolean = {
            for (fex <- features) {
                if (!config.apply(featureIDHashmap(fex))) return false
            }
            return true
        }

        override def equals(other: Any): Boolean = {
            if (!other.isInstanceOf[SimpleConfiguration]) return super.equals(other)
            else {
                val otherSC = other.asInstanceOf[SimpleConfiguration]
                return otherSC.config.equals(this.config)
            }
        }

        override def hashCode(): Int = {
            return config.hashCode()
        }
    }

    def typecheckProducts(fm_1: FeatureModel, fm_ts: FeatureModel, ast: AST, opt: FrontendOptions) {
        var log, msg: String = ""
        val fm = fm_ts // I got false positives while using the other fm
        //val fm = fm_1
        println("starting product typechecking.")
        val cf = new CAnalysisFrontend(ast.asInstanceOf[TranslationUnit], fm)
        var family_ast = cf.prepareAST[TranslationUnit](ast.asInstanceOf[TranslationUnit])
        var family_env = CASTEnv.createASTEnv(family_ast)

        // PLAN: Ich bekomme ständig Fehlermedlungen, die keine Positionsangaben enthalten.
        // also: den ganzen AST durchgehen, bei allen Elementen, die keine Angaben haben, die der parents einfügen.

        val tmpq: Queue[Product] = Queue()
        tmpq.+=(family_ast)
        while (!tmpq.isEmpty) {
            val x: Product = tmpq.dequeue()

            if (x.isInstanceOf[AST] && !x.asInstanceOf[AST].hasPosition) {
                var parent: Any = family_env.parent(x)
                var parentisnull = (parent == null)
                while (!parentisnull && (!parent.isInstanceOf[AST] || !parent.asInstanceOf[AST].hasPosition)) {
                    parent = family_env.parent(parent)
                    parentisnull = (parent == null)
                }
                if (x.isInstanceOf[AST] && !parentisnull) {
                    x.asInstanceOf[AST].setPositionRange(parent.asInstanceOf[AST].getPositionFrom, parent.asInstanceOf[AST].getPositionTo)
                    //println("added position to " + x.hashCode() + " : " + x.getClass().getSimpleName())
                } else {
                }
            } else {
            }
            for (y <- family_env.children(x)) {
                if (y.isInstanceOf[List[_]]) {
                    for (lelem <- y.asInstanceOf[List[_]]) {
                        tmpq.enqueue(lelem.asInstanceOf[Product])
                    }
                } else if (y.isInstanceOf[AST]) {
                    tmpq.enqueue(y.asInstanceOf[AST])
                } else if (y.isInstanceOf[Conditional[_]]) {
                }
            }
        }
        family_env = CASTEnv.createASTEnv(family_ast)
        var fw: FileWriter = null

        /**write family ast */
        /*
            fw = new FileWriter(new File("../ast_fam.txt"))
            fw.write(family_ast.toString)
            fw.close()
        */

        println("generating configurations.")
        var startTime: Long = 0

        features = getAllFeatures(family_ast)
        featureIDHashmap = new HashMap[SingleFeatureExpr, Int]().++(features.zipWithIndex)
        //for (f <- features) println(f)
        /**Starting with no tasks */
        var typecheckingTasks: List[Pair[String, List[SimpleConfiguration]]] = List()
        var configurationCollection: List[SimpleConfiguration] = List()

        /**All products */
        //typecheckingTasks ::= Pair("allProducts", getAllProducts(features, fm, family_env))
        /**Single-wise */
        {
            startTime = System.currentTimeMillis()
            val (configs, logmsg) = getAllSinglewiseConfigurations(features, fm, configurationCollection, preferDisabledFeatures = true)
            typecheckingTasks ::= Pair("singleWise", configs)
            configurationCollection ++= configs
            msg = "Time for config generation (singleWise): " + (System.currentTimeMillis() - startTime) + " ms\n" + logmsg
            println(msg)
            log = log + msg
        }

        /**Coverage Configurations */
        /*
            typecheckingTasks ::=
                Pair("coverage", ConfigurationCoverage.naiveCoverageAny(family_ast, fm, family_env).toList.map(
                {ex : FeatureExpr => completeConfiguration(ex, features, fm) }
            ))
            println("got " + typecheckingTasks.last._2.size + " coverage configurations")
        */
        /**Pairwise MAX */
        {
            startTime = System.currentTimeMillis()
            val (configs, logmsg) = getAllPairwiseConfigurations(features, fm, configurationCollection, preferDisabledFeatures = false)
            typecheckingTasks ::= Pair("pairWiseMax", configs)
            configurationCollection ++= configs
            msg = "Time for config generation (pairwiseMax): " + (System.currentTimeMillis() - startTime) + " ms\n" + logmsg
            println(msg)
            log = log + msg
        }

        /**Pairwise */
        /*
            startTime = System.currentTimeMillis()
            typecheckingTasks ::= Pair("pairWise", getAllPairwiseConfigurations(features,fm, preferDisabledFeatures=true))
            msg = "Time for config generation (pairwise): " + (System.currentTimeMillis() - startTime) + " ms\n"
            println(msg)
            log = log + msg
        */

        /**Just one hardcoded config */
        /*
            typecheckingTasks ::= Pair("hardcoded", getOneConfigWithFeatures(
              List("CONFIG_LONG_OPTS"),
              List(),
              features,fm, true, true)
              )
        */

        if (typecheckingTasks.size > 0) println("start task - typechecking (" + (typecheckingTasks.size) + " tasks)")
        // results (taskName, (NumConfigs, errors, timeSum))
        var configCheckingResults: List[(String, (java.lang.Integer, java.lang.Integer, java.lang.Long))] = List()
        val outFilePrefix: String = "../reports/" + opt.getFile.substring(0, opt.getFile.length - 2)
        for ((taskDesc: String, configs: List[SimpleConfiguration]) <- typecheckingTasks) {
            var configurationsWithErrors = 0
            var current_config = 0
            var totalTimeProductChecking: Long = 0
            for (config <- configs) {
                current_config += 1
                println("checking configuration " + current_config + " of " + configs.size + " (" + opt.getFile + " , " + taskDesc + ")")
                val product: TranslationUnit = cf.deriveProd[TranslationUnit](family_ast,
                    new Configuration(FeatureExprFactory.createFeatureExprFast(config.getTrueSet, config.getFalseSet), fm), family_env)
                val ts = new CTypeSystemFrontend(product, FeatureExprFactory.default.featureModelFactory.empty)
                val startTime: Long = System.currentTimeMillis()
                val noErrors: Boolean = ts.checkAST
                val configTime: Long = System.currentTimeMillis() - startTime
                totalTimeProductChecking += configTime
                if (!noErrors) {
                    //if (true) {
                    // log product with error
                    configurationsWithErrors += 1
                    var file: File = new File(outFilePrefix + "_" + taskDesc + "_errors" + current_config + ".txt")
                    file.getParentFile.mkdirs()
                    fw = new FileWriter(file)
                    for (error <- ts.errors)
                        fw.write("  - " + error + "\n")
                    fw.close()
                    // write product to file
                    file = new File(outFilePrefix + "_" + taskDesc + "_" + current_config + "_product.c")
                    fw = new FileWriter(file)
                    fw.write(PrettyPrinter.print(product))
                    fw.close()
                    //write configuration to file
                    file = new File(outFilePrefix + "_" + taskDesc + "_" + current_config + "_config.txt")
                    fw = new FileWriter(file)
                    fw.write(config.toString().replace("&&", "&&\n"))
                    fw.close()
                    // write ast to file
                    file = new File(outFilePrefix + "_" + taskDesc + "_" + current_config + "_ast.txt")
                    fw = new FileWriter(file)
                    fw.write(product.toString)
                    fw.close()
                }
            }
            configCheckingResults ::=(taskDesc, (configs.size, configurationsWithErrors, totalTimeProductChecking))

        }
        // family base checking
        println("family-based type checking:")
        startTime = System.currentTimeMillis()
        val ts = new CTypeSystemFrontend(family_ast.asInstanceOf[TranslationUnit], fm_ts)
        val noErrors: Boolean = ts.checkAST
        val familyTime: Long = System.currentTimeMillis() - startTime

        var file: File = new File(outFilePrefix + "_report.txt")
        file.getParentFile.mkdirs()
        fw = new FileWriter(file)
        fw.write("File : " + opt.getFile + "\n")
        fw.write("Features : " + features.size + "\n")
        fw.write(log + "\n")

        for ((taskDesc, (numConfigs, errors, time)) <- configCheckingResults) {
            fw.write("\n -- Task: " + taskDesc + "\n")
            fw.write("(" + taskDesc + ")Processed configurations: " + numConfigs + "\n")
            fw.write("(" + taskDesc + ")Configurations with errors: " + errors + "\n")
            fw.write("(" + taskDesc + ")TimeSum Products: " + time + " ms\n")
            fw.write("\n")
        }

        fw.write("Errors in family check: " + (if (noErrors) "No" else "Yes") + "\n")
        fw.write("Time Family:      " + familyTime + " ms\n")
        fw.close()

    }

    def configListContainsFeaturesAsEnabled(lst: List[SimpleConfiguration], features: Set[SingleFeatureExpr]): Boolean = {
        for (conf <- lst) {
            if (conf.containsFeaturesAsEnabled(features))
                return true
        }
        return false
    }

    def getOneConfigWithFeatures(trueFeatures: List[String], falseFeatures: List[String],
                                 allFeatures: List[SingleFeatureExpr], fm: FeatureModel, fixConfig: Boolean = true): List[SimpleConfiguration] = {
        var partConfig: FeatureExpr = FeatureExprFactory.True
        var remainingFeatures: List[SingleFeatureExpr] = allFeatures
        var trueFeatureObjects: List[SingleFeatureExpr] = List()
        for (fName: String <- trueFeatures) {
            val fIndex: Int = remainingFeatures.indexWhere({
                (f: SingleFeatureExpr) => f.feature.equals(fName)
            })
            if (fIndex != -1) {
                //-1 := no feature found
                partConfig = partConfig.and(remainingFeatures.apply(fIndex))
                trueFeatureObjects ::= remainingFeatures.apply(fIndex)
                // I know this is horrible. But it will be used only for debugging
            } else {
                //throw new IllegalArgumentException("Feature not found: " + fName)
                println("Feature not found: " + fName)
            }
            remainingFeatures = remainingFeatures.slice(0, fIndex) ++ remainingFeatures.slice(fIndex + 1, remainingFeatures.length + 1)
        }
        var falseFeatureObjects: List[SingleFeatureExpr] = List()
        for (fName: String <- falseFeatures) {
            val fIndex: Int = remainingFeatures.indexWhere({
                (f: SingleFeatureExpr) => f.feature.equals(fName)
            })
            if (fIndex != -1) {
                //-1 := no feature found
                partConfig = partConfig.andNot(remainingFeatures.apply(fIndex))
                // I know this is horrible. But it will be used only for debugging
                falseFeatureObjects ::= remainingFeatures.apply(fIndex)
            } else {
                //throw new IllegalArgumentException("Feature not found: " + fName)
                println("Feature not found: " + fName)
            }
            remainingFeatures = remainingFeatures.slice(0, fIndex) ++ remainingFeatures.slice(fIndex + 1, remainingFeatures.length + 1)
        }
        if (partConfig.isSatisfiable(fm)) {
            if (fixConfig) {
                val completeConfig = completeConfiguration(partConfig, remainingFeatures, fm)
                if (completeConfig == null) {
                    throw new IllegalArgumentException("PartialConfig has no satisfiable extension!")
                } else {
                    return List(completeConfig)
                }
            } else {
                return List(new SimpleConfiguration(trueFeatureObjects, falseFeatureObjects))
            }
        } else {
            throw new IllegalArgumentException("PartialConfig \"" + partConfig.toTextExpr + "\" is not satisfiable!")
        }
    }

    def getAllSinglewiseConfigurations(features: List[SingleFeatureExpr], fm: FeatureModel,
                                       existingConfigs: List[SimpleConfiguration] = List(),
                                       preferDisabledFeatures: Boolean): (List[SimpleConfiguration], String) = {
        var unsatCombinations = 0
        var alreadyCoveredCombinations = 0
        println("generating single-wise configurations")
        var pwConfigs: List[SimpleConfiguration] = List()
        for (f1 <- features) {
            if (!configListContainsFeaturesAsEnabled(pwConfigs ++ existingConfigs, Set(f1))) {
                // this pair was not considered yet
                val conf = FeatureExprFactory.True.and(f1)
                if (conf.isSatisfiable(fm)) {
                    // this pair is satisfiable
                    // make config complete by choosing the other features
                    // val remainingFeatures = features.filterNot({(fe : FeatureExpr) => fe.equals(f1) || fe.equals(f2)})
                    val remainingFeatures = features.filterNot({
                        (fe: SingleFeatureExpr) => fe.equals(f1)
                    })
                    val completeConfig = completeConfiguration(conf, remainingFeatures, fm)
                    if (completeConfig != null) {
                        pwConfigs ::= completeConfig
                    } else {
                        println("no satisfiable configuration for feature " + f1)
                        unsatCombinations += 1
                    }
                } else {
                    println("no satisfiable configuration for feature " + f1)
                    unsatCombinations += 1
                }
            } else {
                println("feature " + f1 + " already covered")
                alreadyCoveredCombinations += 1
            }
        }
        return (pwConfigs,
            " unsatisfiableCombinations:" + unsatCombinations + "\n" +
                " already covered combinations:" + alreadyCoveredCombinations + "\n" +
                " created combinations:" + pwConfigs.size + "\n")
    }

    def getAllPairwiseConfigurations(features: List[SingleFeatureExpr], fm: FeatureModel,
                                     existingConfigs: List[SimpleConfiguration] = List(),
                                     preferDisabledFeatures: Boolean): (List[SimpleConfiguration], String) = {
        var unsatCombinations = 0
        var alreadyCoveredCombinations = 0
        println("generating pair-wise configurations")
        var pwConfigs: List[SimpleConfiguration] = List()

        // this for-loop structure should avoid pairs like "(A,A)" and ( "(A,B)" and "(B,A)" )
        for (index1 <- 0 to features.size - 1) {
            val f1 = features(index1)
            for (index2 <- index1 + 1 to features.size - 1) {
                val f2 = features(index2)
                if (!configListContainsFeaturesAsEnabled(pwConfigs ++ existingConfigs, Set(f1, f2))) {
                    // this pair was not considered yet
                    val confEx = FeatureExprFactory.True.and(f1).and(f2)
                    if (confEx.isSatisfiable(fm)) {
                        // this pair is satisfiable
                        // make config complete by choosing the other features
                        val remainingFeatures = features.filterNot({
                            (fe: SingleFeatureExpr) => fe.equals(f1) || fe.equals(f2)
                        })
                        val completeConfig = completeConfiguration(confEx, remainingFeatures, fm, preferDisabledFeatures)
                        if (completeConfig != null) {
                            pwConfigs ::= completeConfig
                        } else {
                            println("no satisfiable configuration for features " + f1 + " and " + f2)
                            unsatCombinations += 1
                        }
                    } else {
                        println("no satisfiable configuration for features " + f1 + " and " + f2)
                        unsatCombinations += 1
                    }
                } else {
                    println("feature combination " + f1 + " and " + f2 + " already covered")
                    alreadyCoveredCombinations += 1
                }
            }
        }
        return (pwConfigs,
            " unsatisfiableCombinations:" + unsatCombinations + "\n" +
                " already covered combinations:" + alreadyCoveredCombinations + "\n" +
                " created combinations:" + pwConfigs.size + "\n")
    }

    def getAllTriplewiseConfigurations(features: List[SingleFeatureExpr], fm: FeatureModel,
                                       existingConfigs: List[SimpleConfiguration] = List(),
                                       preferDisabledFeatures: Boolean): (List[SimpleConfiguration], String) = {
        var unsatCombinations = 0
        var alreadyCoveredCombinations = 0
        println("generating triple-wise configurations")
        var pwConfigs: List[SimpleConfiguration] = List()
        // this for-loop structure should avoid pairs like "(A,A)" and ( "(A,B)" and "(B,A)" )
        for (index1 <- 0 to features.size - 1) {
            val f1 = features(index1)
            for (index2 <- index1 to features.size - 1) {
                val f2 = features(index2)
                for (index3 <- index2 to features.size - 1) {
                    val f3 = features(index3)
                    if (!configListContainsFeaturesAsEnabled(pwConfigs ++ existingConfigs, Set(f1, f2, f3))) {
                        // this pair was not considered yet
                        val conf = FeatureExprFactory.True.and(f1).and(f2).and(f3)
                        if (conf.isSatisfiable(fm)) {
                            // this pair is satisfiable
                            // make config complete by choosing the other features
                            val remainingFeatures = features.filterNot({
                                (fe: SingleFeatureExpr) => fe.equals(f1) || fe.equals(f2) || fe.equals(f3)
                            })
                            val completeConfig = completeConfiguration(conf, remainingFeatures, fm)
                            if (completeConfig != null) {
                                pwConfigs ::= completeConfig
                            } else {
                                println("no satisfiable configuration for features " + f1 + " and " + f2 + " and " + f3)
                                unsatCombinations += 1
                            }
                        } else {
                            println("no satisfiable configuration for features " + f1 + " and " + f2 + " and " + f3)
                            unsatCombinations += 1
                        }
                    } else {
                        println("feature combination " + f1 + " and " + f2 + " and " + f3 + " already covered")
                        alreadyCoveredCombinations += 1
                    }
                }
            }
        }
        return (pwConfigs,
            " unsatisfiableCombinations:" + unsatCombinations + "\n" +
                " already covered combinations:" + alreadyCoveredCombinations + "\n" +
                " created combinations:" + pwConfigs.size + "\n")
    }

    /**
     * Optimzed version of the completeConfiguration method. Uses FeatureExpr.getSatisfiableAssignment to need only one SAT call.
     * @param expr
     * @param list
     * @param model
     * @return
     */
    def completeConfiguration(expr: FeatureExpr, list: List[SingleFeatureExpr], model: FeatureModel, preferDisabledFeatures: Boolean = false): SimpleConfiguration = {
        expr.getSatisfiableAssignment(model, list.toSet, preferDisabledFeatures) match {
            case Some(ret) => return new SimpleConfiguration(ret._1, ret._2)
            case None => return null
        }
    }

    /**
     * Completes a partial configuration so that no variability remains.
     * Features are set to false if possible.
     * If no satisfiable configuration is found then null is returned.
     * @param partialConfig
     * @param remainingFeatures
     * @param fm
     */
    def completeConfiguration_Inefficient(partialConfig: FeatureExpr, remainingFeatures: List[FeatureExpr], fm: FeatureModel, preferDisabledFeatures: Boolean = true): FeatureExpr = {
        var config: FeatureExpr = partialConfig
        val fIter = remainingFeatures.iterator
        var partConfigFeasible: Boolean = true
        while (partConfigFeasible && fIter.hasNext) {
            val fx: FeatureExpr = fIter.next()
            if (preferDisabledFeatures) {
                // try to set other variables to false first
                var tmp: FeatureExpr = config.andNot(fx)
                val res1: Boolean = tmp.isSatisfiable(fm)
                if (res1) {
                    config = tmp
                } else {
                    tmp = config.and(fx)
                    val res2: Boolean = tmp.isSatisfiable(fm)
                    if (res2) {
                        config = tmp
                    } else {
                        // this configuration cannot be satisfied any more
                        return null
                        partConfigFeasible = false
                    }
                }
            } else {
                // try to set other variables to true first
                var tmp: FeatureExpr = config.and(fx)
                if (tmp.isSatisfiable(fm)) {
                    config = tmp
                } else {
                    tmp = config.andNot(fx)
                    if (tmp.isSatisfiable(fm)) {
                        config = tmp
                    } else {
                        // this configuration cannot be satisfied any more
                        return null
                        partConfigFeasible = false
                    }
                }
            }
        }
        if (partConfigFeasible) {
            // all features have been processed, and the config is still feasible.
            // so we have a complete configuration now!
            return config
        }
        return null
    }

    /**
     * Returns a sorted list of all features in this AST, including Opt and Choice Nodes
     * @param root
     * @return
     */
    def getAllFeatures(root: Product): List[SingleFeatureExpr] = {
        var featuresSorted: List[SingleFeatureExpr] = getAllFeaturesRec(root).toList
        // sort to eliminate any non-determinism caused by the set
        featuresSorted = featuresSorted.sortWith({
            (x: SingleFeatureExpr, y: SingleFeatureExpr) => x.feature.compare(y.feature) > 0
        })
        println("found " + featuresSorted.size + " features")
        return featuresSorted //.map({s:String => FeatureExprFactory.createDefinedExternal(s)});
    }

    private def getAllFeaturesRec(root: Any): Set[SingleFeatureExpr] = {
        root match {
            case x: Opt[_] => x.feature.collectDistinctFeatureObjects.toSet ++ getAllFeaturesRec(x.entry)
            case x: Choice[_] => x.feature.collectDistinctFeatureObjects.toSet ++ getAllFeaturesRec(x.thenBranch) ++ getAllFeaturesRec(x.elseBranch)
            case l: List[_] => {
                var ret: Set[SingleFeatureExpr] = Set();
                for (x <- l) {
                    ret = ret ++ getAllFeaturesRec(x);
                }
                ret
            }
            case x: Product => {
                var ret: Set[SingleFeatureExpr] = Set();
                for (y <- x.productIterator.toList) {
                    ret = ret ++ getAllFeaturesRec(y);
                }
                ret
            }
            case o => {
                Set()
            }
        }
    }
}
