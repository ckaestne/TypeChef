package de.fosd.typechef

import conditional.{Choice, Opt}
import crewrite._
import featureexpr._

import bdd.{BDDFeatureModel, SatSolver}
import parser.c.{AST, PrettyPrinter, TranslationUnit}
import typesystem.CTypeSystemFrontend
import scala.collection.immutable.HashMap
import scala.Predef._
import scala._
import collection.mutable.{ListBuffer, HashSet, BitSet}
import io.Source
import java.util.regex.Pattern
import java.util.ArrayList
import java.lang.SuppressWarnings
import java.io._

import org.kiama.rewriting.Rewriter.manytd
import org.kiama.rewriting.Rewriter.query

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

    class SimpleConfiguration(private val config: scala.collection.immutable.BitSet) extends scala.Serializable {

        def this(trueSet: List[SingleFeatureExpr], falseSet: List[SingleFeatureExpr]) = this(
        {
            val ret: scala.collection.mutable.BitSet = BitSet()
            for (tf: SingleFeatureExpr <- trueSet) ret.add(featureIDHashmap(tf))
            for (ff: SingleFeatureExpr <- falseSet) ret.remove(featureIDHashmap(ff))
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

        // caching, values of this field will not be serialized
        @transient
        private var featureExpression : FeatureExpr = null
        def toFeatureExpr : FeatureExpr = {
            if (featureExpression == null)
                featureExpression = FeatureExprFactory.createFeatureExprFast(getTrueSet, getFalseSet)
            return featureExpression
        }

        /**
         * This method assumes that all features in the parameter-set appear in either the trueList, or in the falseList
         * @param features
         * @return
         */
        def containsAllFeaturesAsEnabled(features: Set[SingleFeatureExpr]): Boolean = {
            for (fex <- features) {
                if (!config.apply(featureIDHashmap(fex))) return false
            }
            return true
        }
        /**
         * This method assumes that all features in the parameter-set appear in the configuration (either as true or as false)
         * @param features
         * @return
         */
        def containsAllFeaturesAsDisabled(features: Set[SingleFeatureExpr]): Boolean = {
            for (fex <- features) {
                if (config.apply(featureIDHashmap(fex))) return false
            }
            return true
        }

        def containsAtLeastOneFeatureAsEnabled(set: Set[SingleFeatureExpr]) : Boolean =
            ! containsAllFeaturesAsDisabled(set)
        def containsAtLeastOneFeatureAsDisabled(set: Set[SingleFeatureExpr]) : Boolean =
            ! containsAllFeaturesAsEnabled(set)

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

    def saveSerializationOfTasks(tasks: List[(String, List[SimpleConfiguration])], featureList: List[SingleFeatureExpr], mainDir : File) {
        def writeObject(obj : java.io.Serializable, file : File) {
            try {
                file.createNewFile()
                val fileOut : FileOutputStream =
                    new FileOutputStream(file);
                val out : ObjectOutputStream =
                    new ObjectOutputStream(fileOut);
                out.writeObject(obj);
                out.close();
                fileOut.close();
            } catch
            {
                case i : IOException => i.printStackTrace()
            }
        }
        def toJavaList[T](orig : List[T]) : ArrayList[T] = {
            val javaList : ArrayList[T] = new ArrayList[T]()
            for (f : T <- orig) javaList.add(f)
            javaList
        }
        mainDir.mkdirs()
        // it seems that the scala lists cannot be serialized, so i use java ArrayLists
        writeObject(toJavaList(featureList.map(_.feature)), new File(mainDir,"FeatureHashmap.ser"))
        for ((taskName,configs)<-tasks) {
            writeObject(toJavaList(configs), new File(mainDir,taskName + ".ser"))
        }
    }
    def loadSerializedTasks(featureList: List[SingleFeatureExpr], mainDir : File) :List[(String, List[SimpleConfiguration])] = {
        def readObject[T](file : File) : T = {
            try {
                val fileIn : FileInputStream =
                    new FileInputStream(file);
                val in : ObjectInputStream = new ObjectInputStream(fileIn);
                val e : T = in.readObject().asInstanceOf[T];
            in.close();
            fileIn.close();
            return e
            } catch {
                case i : IOException => {
                    // do not handle
                    throw i
                }
            }
        }
        def toJavaList[T](orig : List[T]) : ArrayList[T] = {
            val javaList : ArrayList[T] = new ArrayList[T]()
            for (f : T <- orig) javaList.add(f)
            javaList
        }
        var taskList : ListBuffer[(String, List[SimpleConfiguration])] = ListBuffer()
        // it seems that the scala lists cannot be serialized, so i use java ArrayLists
        val savedFeatures : ArrayList[String] =  readObject[ArrayList[String]](new File(mainDir,"FeatureHashmap.ser"))
        assert(savedFeatures.equals(toJavaList(featureList.map((_.feature)))))
        for (file <- mainDir.listFiles()) {
            val fn = file.getName
            if (!fn.equals("FeatureHashmap.ser") && fn.endsWith(".ser")) {
                val configs = readObject[ArrayList[SimpleConfiguration]](file)
                val taskName = fn.substring(0,fn.length-".ser".length)
                var taskConfigs : scala.collection.mutable.ListBuffer[SimpleConfiguration] = ListBuffer()
                val iter = configs.iterator()
                while (iter.hasNext) {
                    taskConfigs += iter.next()
                }
                taskList.+=((taskName, taskConfigs.toList))
            }
        }
        return taskList.toList
    }

    def typecheckProducts(fm_1: FeatureModel, fm_ts: FeatureModel, ast: AST, opt: FrontendOptions) {
        val thisFilePath = opt.getFile.substring(opt.getFile.lastIndexOf("linux-2.6.33.3"))

        var log, msg: String = ""
        val fm = fm_ts // I got false positives while using the other fm
        //val fm = fm_1
        println("starting product typechecking.")
        val cf = new CAnalysisFrontend(ast.asInstanceOf[TranslationUnit], fm)
        val family_ast = cf.prepareAST[TranslationUnit](ast.asInstanceOf[TranslationUnit])

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
        //println("features: ")
        //for (f <- features) println(f)

        /**Starting with no tasks */
        var typecheckingTasks: List[Pair[String, List[SimpleConfiguration]]] = List()
        val configSerializationDir =new File("../savedConfigs/" + thisFilePath.substring(0, thisFilePath.length - 2))
        val useSerialization = true
        if (useSerialization &&
            configSerializationDir.exists() &&
            new File(configSerializationDir,"FeatureHashmap.ser").exists()) {
            /** Load serialized tasks */
            {
                startTime = System.currentTimeMillis()
                println("loading tasks from serialized files")
                typecheckingTasks = loadSerializedTasks(features, configSerializationDir)
                msg = "Time for serialization loading: " + (System.currentTimeMillis() - startTime) + " ms\n"
                println(msg)
                log = log + msg
            }
        }
        /** Generate tasks */
        var configurationCollection: List[SimpleConfiguration] = List()
        /**All products */
        //typecheckingTasks ::= Pair("allProducts", getAllProducts(features, fm, family_env))
        /** Load config from file */

        {
            if (typecheckingTasks.find(_._1.equals("FileConfig")).isDefined) {
                msg = "omitting FileConfig generation, because a serialized version was loaded"
            } else {
                startTime = System.currentTimeMillis()
                //val (configs, logmsg) = getConfigsFromFiles(features, fm, new File("/home/rhein/Tools/TypeChef/GitClone/Linux_allyes_modified.config"))
                val (configs, logmsg) = getConfigsFromFiles(features, fm, new File("../Linux_allyes_modified.config"))
                typecheckingTasks :+= Pair("FileConfig", configs)
                configurationCollection ++= configs
                msg = "Time for config generation (FileConfig): " + (System.currentTimeMillis() - startTime) + " ms\n" + logmsg

                //abort if no configuration could be loaded. No report file will be written in this case
                if (configs.isEmpty) {
                    println(msg +"\n" + " aborting check of file \"" + thisFilePath + "\" because file config could not be loaded.")
                    return
                }
            }
            println(msg)
            log = log + msg
        }

        /**Single-wise */

        {
            if (typecheckingTasks.find(_._1.equals("singleWise")).isDefined) {
                msg = "omitting singleWise generation, because a serialized version was loaded"
            } else {
                startTime = System.currentTimeMillis()
                val (configs, logmsg) = getAllSinglewiseConfigurations(features, fm, configurationCollection, preferDisabledFeatures = false)
                typecheckingTasks :+= Pair("singleWise", configs)

                configurationCollection ++= configs
                msg = "Time for config generation (singleWise): " + (System.currentTimeMillis() - startTime) + " ms\n" + logmsg
            }
            println(msg)
            log = log + msg
        }

        /**Coverage Configurations */

        {
            if (typecheckingTasks.find(_._1.equals("coverage")).isDefined) {
                msg = "omitting coverage generation, because a serialized version was loaded"
            } else {
                startTime = System.currentTimeMillis()
                val (configs, logmsg) = configurationCoverage(family_ast, fm, features, configurationCollection, preferDisabledFeatures = false)
                typecheckingTasks :+= Pair("coverage", configs)
                configurationCollection ++= configs
                msg = "Time for config generation (coverage): " + (System.currentTimeMillis() - startTime) + " ms\n" + logmsg
            }
            println(msg)
            log = log + msg
        }

        /**Pairwise MAX */
/*
        {
            if (typecheckingTasks.find(_._1.equals("pairWiseMax")).isDefined) {
                msg = "omitting pairWiseMax generation, because a serialized version was loaded"
            } else {
                startTime = System.currentTimeMillis()
                val (configs, logmsg) = getAllPairwiseConfigurations(features, fm, configurationCollection, preferDisabledFeatures = false)
                typecheckingTasks :+= Pair("pairWiseMax", configs)
                configurationCollection ++= configs
                msg = "Time for config generation (pairwiseMax): " + (System.currentTimeMillis() - startTime) + " ms\n" + logmsg
            }
            println(msg)
            log = log + msg
        }
*/
        /**Pairwise */
        /*
        if (typecheckingTasks.find(_._1.equals("pairWise")).isDefined) {
            msg = "omitting pairWise generation, because a serialized version was loaded"
        } else {
            startTime = System.currentTimeMillis()
            typecheckingTasks :+= Pair("pairWise", getAllPairwiseConfigurations(features,fm, preferDisabledFeatures=true))
            msg = "Time for config generation (pairwise): " + (System.currentTimeMillis() - startTime) + " ms\n"
        }
            println(msg)
            log = log + msg
        */

        /**Just one hardcoded config */
/*
            typecheckingTasks :+= Pair("hardcoded", getOneConfigWithFeatures(
              List("CONFIG_SMP"),
              List("CONFIG_X86_32_SMP","CONFIG_X86_64_SMP"),
              features,fm, true)
              )
*/
        saveSerializationOfTasks(typecheckingTasks, features, configSerializationDir)

        if (typecheckingTasks.size > 0) println("start task - typechecking (" + (typecheckingTasks.size) + " tasks)")
        // results (taskName, (NumConfigs, errors, timeSum))
        var configCheckingResults: List[(String, (java.lang.Integer, java.lang.Integer, java.lang.Long))] = List()
        val outFilePrefix: String = "../reports/" + thisFilePath.substring(0, thisFilePath.length - 2)
        for ((taskDesc: String, configs : List[SimpleConfiguration]) <- typecheckingTasks) {
            var configurationsWithErrors = 0
            var current_config = 0
            var totalTimeProductChecking: Long = 0
            for (config <- configs) {
                current_config += 1
                println("checking configuration " + current_config + " of " + configs.size + " (" + thisFilePath + " , " + taskDesc + ")")
                val product: TranslationUnit = cf.deriveProd[TranslationUnit](family_ast,
                    new Configuration(config.toFeatureExpr, fm))
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

        val file: File = new File(outFilePrefix + "_report.txt")
        file.getParentFile.mkdirs()
        fw = new FileWriter(file)
        fw.write("File : " + thisFilePath + "\n")
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
            if (conf.containsAllFeaturesAsEnabled(features))
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
        println("cecking satisfiability of " + partConfig.toTextExpr)
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
                val conf = f1
                val completeConfig = completeConfiguration(conf, features, fm)
                if (completeConfig != null) {
                    pwConfigs ::= completeConfig
                } else {
                    //println("no satisfiable configuration for feature " + f1)
                    unsatCombinations += 1
                }
            } else {
                //println("feature " + f1 + " already covered")
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
        val startTime = System.currentTimeMillis()
        println("generating pair-wise configurations")
        var pwConfigs: List[SimpleConfiguration] = List()

        // this for-loop structure should avoid pairs like "(A,A)" and ( "(A,B)" and "(B,A)" )
        for (index1 <- 0 to features.size - 1) {
            val f1 = features(index1)
            var f1Configs = (pwConfigs ++ existingConfigs).filter({_.containsAllFeaturesAsEnabled(Set(f1))})
            for (index2 <- index1 + 1 to features.size - 1) {
                val f2 = features(index2)
                //if (!configListContainsFeaturesAsEnabled(pwConfigs ++ existingConfigs, Set(f1, f2))) {
                if (!configListContainsFeaturesAsEnabled(f1Configs, Set(f2))) {
                    // this pair was not considered yet
                    val confEx = FeatureExprFactory.True.and(f1).and(f2)
                    // make config complete by choosing the other features
                    val completeConfig = completeConfiguration(confEx, features, fm, preferDisabledFeatures)
                    if (completeConfig != null) {
                        pwConfigs ::= completeConfig
                        f1Configs ::= completeConfig
                    } else {
                        //println("no satisfiable configuration for features " + f1 + " and " + f2)
                        unsatCombinations += 1
                    }
                } else {
                    //println("feature combination " + f1 + " and " + f2 + " already covered")
                    alreadyCoveredCombinations += 1
                }
                //if (System.currentTimeMillis() - startTime > 60000) { // should be 1 minute
                if (System.currentTimeMillis() - startTime > 600000) { // should be 10 minutes
                    val todo = features.size
                    val done = index1-1
                    return (pwConfigs,
                        " unsatisfiableCombinations:" + unsatCombinations + "\n" +
                            " already covered combinations:" + alreadyCoveredCombinations + "\n" +
                            " created combinations:" + pwConfigs.size + "\n"+
                            " generation stopped after 10 minutes (" + index1 +"/" + features.size + " features processed in outer loop) => (" + ((done*done+2*done+2*todo*100)/(todo*todo)) + "% done)\n")
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
                        // make config complete by choosing the other features
                        val completeConfig = completeConfiguration(conf, features, fm)
                        if (completeConfig != null) {
                            pwConfigs ::= completeConfig
                        } else {
                            //println("no satisfiable configuration for features " + f1 + " and " + f2 + " and " + f3)
                            unsatCombinations += 1
                        }
                    } else {
                        //println("feature combination " + f1 + " and " + f2 + " and " + f3 + " already covered")
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


    /*
    Configuration Coverage Method copied from Joerg and heavily modified :)
     */
    def configurationCoverage(astRoot : TranslationUnit, fm: FeatureModel, features : List[SingleFeatureExpr],
                              existingConfigs : List[SimpleConfiguration] = List(), preferDisabledFeatures: Boolean) : (List[SimpleConfiguration],String) = {
        val env = CASTEnv.createASTEnv(astRoot)
        val unsatCombinationsCacheFile = new File("unsatCombinationsCache.txt")
        val useUnsatCombinationsCache = true
        val unsatCombinationsCache : scala.collection.immutable.HashSet[String] = if (useUnsatCombinationsCache && unsatCombinationsCacheFile.exists()) {
            new scala.collection.immutable.HashSet[String] ++ (Source.fromFile(unsatCombinationsCacheFile).getLines()).toSet
        } else { scala.collection.immutable.HashSet() }
        var unsatCombinations = 0
        var alreadyCoveredCombinations = 0
        var complexNodes = 0
        var simpleOrNodes = 0
        var simpleAndNodes = 0
        var optNodes: List[Opt[_]] = List()
        var choiceNodes: List[Choice[_]] = List()
        val scanNodes = manytd(query {
            case o: Opt[_] => optNodes ::= o
            case o: Choice[_] => choiceNodes ::= o
        })
        scanNodes(astRoot)
        // now optNodes contains all Opt[..] nodes in the file, and choiceNodes all Choice nodes.
        // True node never needs to be handled
        val handledExpressions : HashSet[FeatureExpr] = HashSet(FeatureExprFactory.True)
        var retList : List[SimpleConfiguration] = List()
        //inner function
        def handleFeatureExpression(fex:FeatureExpr) = {
            if (! handledExpressions.contains(fex) && !(useUnsatCombinationsCache && unsatCombinationsCache.contains(fex.toTextExpr))) {

                //println("fex : " + fex.toTextExpr)
                // search for configs that imply this node
                var isCovered : Boolean = false
                fex.getConfIfSimpleAndExpr() match {
                    case None => {
                        fex.getConfIfSimpleOrExpr() match {
                            case None => {
                                complexNodes+=1
                                isCovered = (retList++existingConfigs).exists(
                                {conf : SimpleConfiguration => conf.toFeatureExpr.implies(fex).isTautology(fm)}
                                )
                            }
                            case Some((enabled:Set[SingleFeatureExpr], disabled:Set[SingleFeatureExpr])) => {
                                simpleOrNodes+=1
                                isCovered = (retList++existingConfigs).exists( {
                                    conf:SimpleConfiguration => conf.containsAtLeastOneFeatureAsEnabled(enabled) ||
                                        conf.containsAtLeastOneFeatureAsDisabled(disabled)
                                })
                            }
                        }
                    }
                    case Some((enabled:Set[SingleFeatureExpr], disabled:Set[SingleFeatureExpr])) => {
                        simpleAndNodes+=1
                        isCovered = (retList++existingConfigs).exists( {
                            conf:SimpleConfiguration => conf.containsAllFeaturesAsEnabled(enabled) &&
                                conf.containsAllFeaturesAsDisabled(disabled)
                        })
                    }
                }
                if (!isCovered) {
                    val completeConfig = completeConfiguration(fex, features, fm, preferDisabledFeatures)
                    if (completeConfig != null) {
                        retList ::= completeConfig
                        //println("created config for fex " + fex.toTextExpr)
                    } else {
                        if (useUnsatCombinationsCache) {
                            //unsatCombinationsCacheFile.getParentFile.mkdirs()
                            val fw = new FileWriter(unsatCombinationsCacheFile, true)
                            fw.write(fex.toTextExpr+"\n")
                            fw.close()
                        }
                        unsatCombinations += 1
                        //println("no satisfiable configuration for fex " + fex.toTextExpr)
                    }
                } else {
                    //println("covered fex " + fex.toTextExpr)
                    alreadyCoveredCombinations += 1
                }
                handledExpressions.add(fex)
                //println("retList.size = " + retList.size)
            }
        }

        for (optN <- optNodes) {
            val fex : FeatureExpr = env.lfeature(optN).fold(optN.feature)({(a:FeatureExpr,b:FeatureExpr) => a.and(b)})
            handleFeatureExpression(fex)
        }

        for (choiceNode <- choiceNodes) {
            val fex : FeatureExpr = env.lfeature(choiceNode).fold(FeatureExprFactory.True)({(a:FeatureExpr,b:FeatureExpr) => a.and(b)})
            handleFeatureExpression(fex.and(choiceNode.feature))
            handleFeatureExpression(fex.and(choiceNode.feature.not()))
        }
        return (retList,
            " unsatisfiableCombinations:" + unsatCombinations + "\n" +
                " already covered combinations:" + alreadyCoveredCombinations + "\n" +
                " created combinations:" + retList.size + "\n" +
                " found " + simpleAndNodes + " simpleAndNodes, " + simpleOrNodes + " simpleOrNodes and " + complexNodes + " complex nodes.\n")
    }


    def getConfigsFromFiles(@SuppressWarnings(Array("unchecked")) features: List[SingleFeatureExpr], fm: FeatureModel, file :File) : (List[SimpleConfiguration], String) = {
        val correctFeatureModelIncompatibility = false
        var ignoredFeatures = 0
        var changedAssignment = 0
        var totalFeatures = 0
        var fileEx : FeatureExpr = FeatureExprFactory.True
        var trueFeatures : Set[SingleFeatureExpr] = Set()
        var falseFeatures : Set[SingleFeatureExpr] = Set()

        val enabledPattern : Pattern = java.util.regex.Pattern.compile("CONFIG_([^=]*)=y")
        val disabledPattern : Pattern = java.util.regex.Pattern.compile("CONFIG_([^=]*)=n")
        for(line <- Source.fromFile(file).getLines().filterNot(_.startsWith("#")).filterNot(_.isEmpty)) {
            totalFeatures+=1
            var matcher = enabledPattern.matcher(line)
            if (matcher.matches()) {
                val name = "CONFIG_" + matcher.group(1)
                val feature = FeatureExprFactory.createDefinedExternal(name)
                var fileExTmp = fileEx.and(feature)
                if (correctFeatureModelIncompatibility) {
                    val isSat =fileExTmp.isSatisfiable(fm)
                    println(name+" "+(if (isSat) "sat" else "!sat"))
                    if (!isSat) {
                        fileExTmp = fileEx.andNot(feature); println("disabling feature " + feature)
                        //fileExTmp = fileEx; println("ignoring Feature " +feature)
                        falseFeatures +=feature
                        changedAssignment+=1
                    } else {
                        trueFeatures += feature
                    }
                } else {
                    trueFeatures +=feature
                }
                fileEx = fileExTmp
            } else {
                matcher = disabledPattern.matcher(line)
                if (matcher.matches()) {
                    val name = "CONFIG_" + matcher.group(1)
                    val feature = FeatureExprFactory.createDefinedExternal(name)
                    var fileExTmp = fileEx.andNot(feature)
                    if (correctFeatureModelIncompatibility) {
                        val isSat = fileEx.isSatisfiable(fm)
                        println("! " + name+" "+(if (isSat) "sat" else "!sat"))
                        if (!isSat) {
                            fileExTmp = fileEx.and(feature)
                            println("SETTING " + name + "=y")
                            trueFeatures +=feature
                            changedAssignment+=1
                        } else {
                            falseFeatures +=feature
                        }
                    } else {
                        falseFeatures +=feature
                    }
                    fileEx = fileExTmp
                } else {
                    ignoredFeatures+=1
                    //println("ignoring line: " + line)
                }
            }
            //println(line)
        }
        println("features mentioned in c-file but not in config: ")
        for (x <- features.filterNot((trueFeatures++falseFeatures).contains(_))) {
            println(x.feature)
        }
        if (correctFeatureModelIncompatibility) {
            // save corrected file
            val fw = new FileWriter(new File(file.getParentFile, file.getName + "_corrected"))
            fw.write("# configFile written by typechef, based on " + file.getAbsoluteFile)
            fw.write("# ignored " + ignoredFeatures + " features of " + totalFeatures + " features")
            fw.write("# changed assignment for " + changedAssignment + " features of " + totalFeatures + " features")
            for (feature <- trueFeatures)
                fw.append(feature.feature + "=y\n")
            fw.close()
        }
        val interestingTrueFeatures = trueFeatures.filter(features.contains(_)).toList
        val interestingFalseFeatures = falseFeatures.filter(features.contains(_)).toList

        fileEx.getSatisfiableAssignment(fm,features.toSet,true) match {
            case None => println("configuration not satisfiable"); return (List(),"")
            case Some((en,dis)) => return (List(new SimpleConfiguration(en,dis)), "")
        }
        return (List(new SimpleConfiguration(interestingTrueFeatures,interestingFalseFeatures)),
            "")
    }

    /**
     * Does the same as the other config-from-file method. However, it does not create additional bdd-Feature
     * expressions but uses string Sets as parameters to the sat-call.
     * Results are slightly different to the other method ?!
     * @param features
     * @param fm
     * @param file
     * @return
     */
    def getConfigsFromFiles_noBDDcreation(@SuppressWarnings(Array("unchecked")) features: List[SingleFeatureExpr], fm: FeatureModel, file :File) : (List[SimpleConfiguration], String) = {
        var ignoredFeatures = 0
        var totalFeatures = 0
        var trueFeatures : Set[String] = Set()
        var falseFeatures : Set[String] = Set()
        val enabledPattern : Pattern = java.util.regex.Pattern.compile("CONFIG_([^=]*)=y")
        val disabledPattern : Pattern = java.util.regex.Pattern.compile("CONFIG_([^=]*)=n")
        for(line <- Source.fromFile(file).getLines().filterNot(_.startsWith("#")).filterNot(_.isEmpty)) {
            totalFeatures+=1
            var matcher = enabledPattern.matcher(line)
            if (matcher.matches()) {
                val name = "CONFIG_" + matcher.group(1)
                trueFeatures +=name
            } else {
                matcher = disabledPattern.matcher(line)
                if (matcher.matches()) {
                    val name = "CONFIG_" + matcher.group(1)
                    falseFeatures +=name
                } else {
                    ignoredFeatures+=1
                }
            }
        }
        println("features mentioned in c-file but not in config: ")
        for (x <- features.filterNot({x=>(trueFeatures++falseFeatures).contains(x.feature)})) {
            println(x.feature)
        }
        if (fm.isInstanceOf[BDDFeatureModel]) {
            SatSolver.getSatisfiableAssignmentFromStringSets(fm.asInstanceOf[BDDFeatureModel],
                features.toSet,trueFeatures,falseFeatures,true) match {
                case None => println("configuration not satisfiable"); return (List(),"")
                case Some((en,dis)) => {
                    val x:SimpleConfiguration = new SimpleConfiguration(en,dis)
                    if (!x.toFeatureExpr.isSatisfiable(fm)) {
                        println("created unsat expr")
                    }
                    return (List(x), "")
                }
            }
        } else {
            println("ok, this works only with bdds!")
            return null
        }
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
