package de.fosd.typechef

import de.fosd.typechef.conditional.{One, Choice, Opt}
import de.fosd.typechef.crewrite._
import de.fosd.typechef.featureexpr._

import bdd.{BDDFeatureModel, SatSolver}
import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem._
import scala.collection.immutable.HashMap
import scala.Predef._
import scala._
import collection.mutable.ListBuffer
import io.Source
import java.util.regex.Pattern
import java.lang.SuppressWarnings
import java.io._
import util.Random
import java.util.Collections
import de.fosd.typechef.featureexpr.sat.{SATFeatureExprFactory, SATFeatureModel}
import de.fosd.typechef.lexer.LexerFrontend

/**
 *
 * User: rhein
 * Date: 4/2/12
 * Time: 3:45 PM
 *
 */
object FamilyBasedVsSampleBased extends EnforceTreeHelper with ASTNavigation with CFGHelper {
    type Task = Pair[String, List[SimpleConfiguration]]

    /**Maps SingleFeatureExpr Objects to IDs (IDs only known/used in this file) */
    private var featureIDHashmap: Map[SingleFeatureExpr, Int] = null
    /**List of all features found in the currently processed file */
    private var features: List[SingleFeatureExpr] = null

    // representation of a product configuration that can be dumped into a file
    // and loaded at further runs
    class SimpleConfiguration(private val config: scala.collection.immutable.BitSet) extends scala.Serializable {

        def this(trueSet: List[SingleFeatureExpr], falseSet: List[SingleFeatureExpr]) = this(
        {
            val ret: scala.collection.mutable.BitSet = scala.collection.mutable.BitSet()
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

        override def toString: String = {
            features.map(
            {
                fex: SingleFeatureExpr => if (config.apply(featureIDHashmap(fex))) fex else fex.not()
            }
            ).mkString("&&")
        }

        // caching, values of this field will not be serialized
        @transient
        private var featureExpression: FeatureExpr = null

        def toFeatureExpr: FeatureExpr = {
            if (featureExpression == null)
                featureExpression = FeatureExprFactory.createFeatureExprFast(getTrueSet, getFalseSet)
            featureExpression
        }

        /**
         * This method assumes that all features in the parameter-set appear in either the trueList, or in the falseList
         * @param features given feature set
         * @return
         */
        def containsAllFeaturesAsEnabled(features: Set[SingleFeatureExpr]): Boolean = {
            for (fex <- features) {
                if (!config.apply(featureIDHashmap(fex))) return false
            }
            true
        }

        /**
         * This method assumes that all features in the parameter-set appear in the configuration (either as true or as false)
         * @param features given feature set
         * @return
         */
        def containsAllFeaturesAsDisabled(features: Set[SingleFeatureExpr]): Boolean = {
            for (fex <- features) {
                if (config.apply(featureIDHashmap(fex))) return false
            }
            true
        }

        def containsAtLeastOneFeatureAsEnabled(set: Set[SingleFeatureExpr]): Boolean =
            !containsAllFeaturesAsDisabled(set)

        def containsAtLeastOneFeatureAsDisabled(set: Set[SingleFeatureExpr]): Boolean =
            !containsAllFeaturesAsEnabled(set)

        override def equals(other: Any): Boolean = {
            if (!other.isInstanceOf[SimpleConfiguration]) super.equals(other)
            else {
                val otherSC = other.asInstanceOf[SimpleConfiguration]
                otherSC.config.equals(this.config)
            }
        }

        override def hashCode(): Int = config.hashCode()
    }

    def saveSerializationOfTasks(tasks: List[Task], featureList: List[SingleFeatureExpr], mainDir: File, file: String) {
        def writeObject(obj: java.io.Serializable, file: File) {
            try {
                file.createNewFile()
                val fileOut: FileOutputStream = new FileOutputStream(file)
                val out: ObjectOutputStream = new ObjectOutputStream(fileOut)
                out.writeObject(obj)
                out.close()
                fileOut.close()
            } catch {
                case i: IOException => i.printStackTrace()
            }
        }
        def toJavaList[T](orig: List[T]): java.util.ArrayList[T] = {
            val javaList: java.util.ArrayList[T] = new java.util.ArrayList[T]
            for (f <- orig) javaList.add(f)
            javaList
        }
        mainDir.mkdirs()
        // it seems that the scala lists cannot be serialized, so i use java ArrayLists
        writeObject(toJavaList(featureList.map(_.feature)), new File(mainDir, "featurehashmap.ser"))
        for ((taskName, configs) <- tasks) {
            writeObject(toJavaList(configs), new File(mainDir, taskName + ".ser"))
        }
    }

    def loadSerializedTasks(featureList: List[SingleFeatureExpr], mainDir: File): List[Task] = {
        def readObject[T](file: File): T = {
            try {
                val fileIn: FileInputStream = new FileInputStream(file)
                val in: ObjectInputStream = new ObjectInputStream(fileIn)
                val e: T = in.readObject().asInstanceOf[T]
                in.close()
                fileIn.close()
                e
            } catch {
                case i: IOException => {
                    // do not handle
                    throw i
                }
            }
        }
        def toJavaList[T](orig: List[T]): java.util.ArrayList[T] = {
            val javaList: java.util.ArrayList[T] = new java.util.ArrayList[T]
            for (f <- orig) javaList.add(f)
            javaList
        }
        var taskList: ListBuffer[Task] = ListBuffer()
        // it seems that the scala lists cannot be serialized, so i use java ArrayLists
        val savedFeatures: java.util.ArrayList[String] = readObject[java.util.ArrayList[String]](new File(mainDir, "featurehashmap.ser"))
        assert(savedFeatures.equals(toJavaList(featureList.map(_.feature))))
        for (file <- mainDir.listFiles()) {
            val fn = file.getName
            if (!fn.equals("featurehashmap.ser") && fn.endsWith(".ser")) {
                val configs = readObject[java.util.ArrayList[SimpleConfiguration]](file)
                val taskName = fn.substring(0, fn.length - ".ser".length)
                var taskConfigs: scala.collection.mutable.ListBuffer[SimpleConfiguration] = ListBuffer()
                val iter = configs.iterator()
                while (iter.hasNext) {
                    taskConfigs += iter.next()
                }
                taskList.+=((taskName, taskConfigs.toList))
            }
        }
        taskList.toList
    }

    def initializeFeatureList(family_ast:AST) {
        features = getAllFeatures(family_ast)
        featureIDHashmap = new HashMap[SingleFeatureExpr, Int]().++(features.zipWithIndex)
    }

    private def buildConfigurationsSingleConf(tunit: TranslationUnit, fm: FeatureModel, opt: FamilyBasedVsSampleBasedOptions,
                                              configDir: File, caseStudy: String, extasks: List[Task])
    : (String, List[Task]) = {
        var tasks: List[Task] = List()
        var log = ""
        var msg = ""
        var startTime: Long = 0

        if (extasks.exists(_._1.equals("fileconfig"))) {
            msg = "omitting fileconfig generation, because a serialized version was loaded"
        } else {
            val configFile = if (caseStudy.equals("linux"))
                opt.getRootFolder + "Linux_allyes_modified.config"
            else if (caseStudy.equals("busybox"))
                opt.getRootFolder + "BusyboxBigConfig.config"
            else if (caseStudy.equals("openssl"))
                opt.getRootFolder + "OpenSSL.config"
            else if (caseStudy.equals("sqlite"))
                opt.getRootFolder + "SQLite.config"
            else
                throw new Exception("unknown case Study, give linux, busybox, or openssl")
            startTime = System.currentTimeMillis()
            val (configs, logmsg) = getConfigsFromFiles(features, fm, new File(configFile))
            tasks :+= Pair("fileconfig", configs)
            msg = "Time for config generation (singleconf): " + (System.currentTimeMillis() - startTime) + " ms\n" + logmsg
        }
        println(msg)
        log = log + msg + "\n"
        (log, tasks)
    }

    private def buildConfigurationsPairwise(tunit: TranslationUnit, fm: FeatureModel, opt: FamilyBasedVsSampleBasedOptions,
                                            configDir: File, caseStudy: String, extasks: List[Task])
    : (String, List[Task]) = {
        var tasks: List[Task] = List()
        var log = ""
        var msg = ""
        var startTime: Long = 0

        if (extasks.exists(_._1.equals("pairwise"))) {
            msg = "omitting pairwise generation, because a serialized version was loaded"
            } else {
            var productsFile: File = null
            var dimacsFM: File = null
            var featureprefix = ""
            if (caseStudy == "linux") {
                productsFile = new File(opt.getRootFolder + "TypeChef-LinuxAnalysis/linux_pairwise_configs.csv")
                dimacsFM = new File(opt.getRootFolder + "TypeChef-LinuxAnalysis/2.6.33.3-2var.dimacs")
                featureprefix = "CONFIG_"
            } else if (caseStudy == "busybox") {
                productsFile = new File(opt.getRootFolder + "TypeChef-BusyboxAnalysis/busybox_pairwise_configs.csv")
                dimacsFM = new File(opt.getRootFolder + "TypeChef-BusyboxAnalysis/BB_fm.dimacs")
                featureprefix = "CONFIG_"
            } else if (caseStudy == "openssl") {
                productsFile = new File(opt.getRootFolder + "TypeChef-OpenSSLAnalysis/openssl-1.0.1c/openssl_pairwise_configs.csv")
                dimacsFM = new File(opt.getRootFolder + "TypeChef-OpenSSLAnalysis/openssl-1.0.1c/openssl.dimacs")
            } else if (caseStudy == "sqlite") {
                productsFile = new File(opt.getRootFolder + "cRefactor-SQLiteEvaluation/sqlite_pairwise_configs.csv")
                dimacsFM = new File(opt.getRootFolder + "cRefactor-SQLiteEvaluation/sqlite.dimacs")
            } else {
                    throw new Exception("unknown case Study, give linux or busybox")
            }
            startTime = System.currentTimeMillis()

            val (configs, logmsg) = loadConfigurationsFromCSVFile(productsFile, dimacsFM, features, fm, featureprefix)

            tasks :+= Pair("pairwise", configs)
            msg = "Time for config generation (pairwise): " + (System.currentTimeMillis() - startTime) + " ms\n" + logmsg
            }
            println(msg)
            log = log + msg + "\n"
        	(log, tasks)
    	}

    private def buildConfigurationsCodecoverageNH(tunit: TranslationUnit, fm: FeatureModel, configDir: File, caseStudy: String, extasks: List[Task])
    : (String, List[Task]) = {
        var tasks: List[Task] = List()
        var log = ""
        var msg = ""
        var startTime: Long = 0
        if (extasks.exists(_._1.equals("coverage_noHeader"))) {
            msg = "omitting coverage_noHeader generation, because a serialized version was loaded"
        } else {
            startTime = System.currentTimeMillis()
            val (configs, logmsg) = configurationCoverage(tunit, fm, features, List(),
                preferDisabledFeatures = false, includeVariabilityFromHeaderFiles = false)
            tasks :+= Pair("coverage_noHeader", configs)
            msg = "Time for config generation (coverage_noHeader): " + (System.currentTimeMillis() - startTime) + " ms\n" + logmsg
        }
        println(msg)
        log = log + msg + "\n"

        (log, tasks)
    }

    private def buildConfigurationsCodecoverage(tunit: TranslationUnit, fm: FeatureModel, configDir: File, caseStudy: String, extasks: List[Task])
    : (String, List[Task]) = {
        var tasks: List[Task] = List()
        var log = ""
        var msg = ""
        var startTime: Long = 0
        if (caseStudy != "linux") {
            if (extasks.exists(_._1.equals("coverage"))) {
                msg = "omitting coverage generation, because a serialized version was loaded"
            } else {
                startTime = System.currentTimeMillis()
                val (configs, logmsg) = configurationCoverage(tunit, fm, features, List(),
                    preferDisabledFeatures = false, includeVariabilityFromHeaderFiles = true)
                tasks :+= Pair("coverage", configs)
                msg = "Time for config generation (coverage): " + (System.currentTimeMillis() - startTime) + " ms\n" + logmsg
            }
            println(msg)
            log = log + msg + "\n"
        } else {
            println("omit code coverage for case study linux; computation is too expensive!")
        }

        (log, tasks)
    }

    /**
     * returns: (log:String, configs: List[Pair[String,List[SimpleConfiguration] ] ])
     * log is a compilation of the log messages
     * the configs-list contains pairs of the name of the config-generation method and the respective generated configs
     *
     */
    def buildConfigurations(tunit: TranslationUnit, fm: FeatureModel, opt: FamilyBasedVsSampleBasedOptions,
                            configdir: File, caseStudy: String): (String, List[Task]) = {
        var msg: String = ""
        var log: String = ""
        println("generating configurations.")
        var startTime: Long = 0
        initializeFeatureList(tunit)

        var tasks: List[Task] = List()

        /** try to load tasks from exisiting files */
        if (configdir.exists() && new File(configdir, "featurehashmap.ser").exists()) {

            startTime = System.currentTimeMillis()
            println("loading tasks from serialized files")
            tasks = loadSerializedTasks(features, configdir)
            msg = "Time for serialization loading: " + (System.currentTimeMillis() - startTime) + " ms\n"
            println(msg)
            log = log + msg + "\n"
        }

        /** pairwise configurations */
        if (opt.pairwise) {
            val (plog, ptasks) = buildConfigurationsPairwise(tunit, fm, opt, configdir, caseStudy, tasks)
            log = log + plog
            tasks ++= ptasks
        } else {
            tasks = tasks.filterNot(_._1 == "pairwise")
        }

        /** code coverage - no Header files */
        if (opt.codecoverageNH) {
            val (clog, ctasks) = buildConfigurationsCodecoverageNH(tunit, fm, configdir, caseStudy, tasks)
            log = log + clog
            tasks ++= ctasks
        } else {
            tasks = tasks.filterNot(_._1 == "coverage_noHeader")
        }

        /** code coverage - including Header files */
        if (opt.codecoverage) {
            val (clog, ctasks) = buildConfigurationsCodecoverage(tunit, fm, configdir, caseStudy, tasks)
            log = log + clog
            tasks ++= ctasks
        } else {
            tasks = tasks.filterNot(_._1 == "coverage")
        }

        /** singleconf */
        if (opt.singleconf) {
            val (flog, ftasks) = buildConfigurationsSingleConf(tunit, fm, opt, configdir, caseStudy, tasks)
            log = log + flog
            tasks ++= ftasks
        } else {
            tasks = tasks.filterNot(_._1 == "fileconfig")
        }

        /** family */
        if (opt.family) {
            val (flog, ftasks) = ("", List(Pair("family", List(new SimpleConfiguration(List(), List())))))
            log = log + flog
            tasks ++= ftasks
        } else {
            tasks = tasks.filterNot(_._1 == "family")
        }

        /** Single-wise */

        /*
                {
                    if (tasks.find(_._1.equals("singleWise")).isDefined) {
                        msg = "omitting singleWise generation, because a serialized version was loaded"
                    } else {
                        startTime = System.currentTimeMillis()
                        val (configs, logmsg) = getAllSinglewiseConfigurations(features, fm, configurationCollection, preferDisabledFeatures = false)
                        tasks :+= Pair("singleWise", configs)

                        configurationCollection ++= configs
                        msg = "Time for config generation (singleWise): " + (System.currentTimeMillis() - startTime) + " ms\n" + logmsg
                    }
                    println(msg)
                    log = log + msg + "\n"
                }
        */

        /**Pairwise MAX */
        /*
                {
                    if (tasks.find(_._1.equals("pairWiseMax")).isDefined) {
                        msg = "omitting pairWiseMax generation, because a serialized version was loaded"
                    } else {
                        startTime = System.currentTimeMillis()
                        val (configs, logmsg) = getAllPairwiseConfigurations(features, fm, configurationCollection, preferDisabledFeatures = false)
                        tasks :+= Pair("pairWiseMax", configs)
                        configurationCollection ++= configs
                        msg = "Time for config generation (pairwiseMax): " + (System.currentTimeMillis() - startTime) + " ms\n" + logmsg
                    }
                    println(msg)
                    log = log + msg + "\n"
                }
        */

        /**Pairwise */
        /*
        if (tasks.find(_._1.equals("pairWise")).isDefined) {
            msg = "omitting pairWise generation, because a serialized version was loaded"
        } else {
            startTime = System.currentTimeMillis()
            tasks :+= Pair("pairWise", getAllPairwiseConfigurations(features,fm, preferDisabledFeatures=true))
            msg = "Time for config generation (pairwise): " + (System.currentTimeMillis() - startTime) + " ms\n"
        }
            println(msg)
            log = log + msg + "\n"
        */

        /**Just one hardcoded config */
        /*
                    tasks :+= Pair("hardcoded", getOneConfigWithFeatures(
                      List("CONFIG_LOCK_STAT"),
                      List("CONFIG_DEBUG_LOCK_ALLOC"),
                      features,fm, true)
                      )
        */
        (log, tasks)
    }

    private def loadConfigurationsFromCSVFile(csvFile: File, dimacsFile: File,
                                              features: List[SingleFeatureExpr],
                                              fm: FeatureModel, fnamePrefix: String = ""): (List[SimpleConfiguration], String) = {
        var retList: List[SimpleConfiguration] = List()

        // determine the feature ids used by the sat solver from the dimacs file
        // dimacs format (c stands for comment) is "c 3779 AT76C50X_USB"
        // we have to pre-set index 0, so that the real indices start with 1
        var featureNamesTmp: List[String] = List("--dummy--")
        val featureMap: scala.collection.mutable.HashMap[String, SingleFeatureExpr] = new scala.collection.mutable.HashMap()
        var currentLine: Int = 1

        for (line: String <- Source.fromFile(dimacsFile).getLines().takeWhile(_.startsWith("c"))) {
            val lineElements: Array[String] = line.split(" ")
            if (!lineElements(1).endsWith("$")) {
                // feature indices ending with $ are artificial and can be ignored here
                assert(augmentString(lineElements(1)).toInt.equals(currentLine), "\"" + lineElements(1) + "\"" + " != " + currentLine)
                featureNamesTmp ::= lineElements(2)
        	}
            currentLine += 1
    	}

        // maintain a hashmap that maps feature names to corresponding feature expressions (SingleFeatureExpr)
        // we only store those features that occur in the file (keeps configuration small);
        // the rest is not important for the configuration;
        val featureNames: Array[String] = featureNamesTmp.reverse.toArray
        featureNamesTmp = null
        for (i <- 0.to(featureNames.length - 1)) {
            val searchResult = features.find(_.feature.equals(fnamePrefix + featureNames(i)))
            if (searchResult.isDefined) {
                featureMap.update(featureNames(i), searchResult.get)
            }
      }

        // parse configurations
        // format is:
        // Feature\Product;0;..;N;       // number of Products (N+1)
        // FeatureA;-;X;....;            // exclusion of FeatureA in Product 0 and inclusion of FeatureA in Product 1
        // FeatureB                      // additional features
        // ...
        val csvLines = Source.fromFile(csvFile).getLines()
        val numProducts = csvLines.next().split(";").last.toInt + 1

        // create and initialize product configurations array
        val pconfigurations = new Array[(List[SingleFeatureExpr], List[SingleFeatureExpr])](numProducts)
        for (i <- 0 to numProducts - 1) {
            pconfigurations.update(i, (List(), List()))
    	}

        // iterate over all lines with Features, determine the selection/deselection in available products and add it to
        // product configurations (true features / false features)
        while (csvLines.hasNext) {
            val featureLine = csvLines.next().split(";")

            for (i <- 1 to numProducts) {
                if (featureMap.contains(featureLine(0))) {
                    var product = pconfigurations(i - 1)
                    if (featureLine(i) == "X") {
                        product = product.copy(_1 = featureMap(featureLine(0)) :: product._1)
                    } else {
                        product = product.copy(_2 = featureMap(featureLine(0)) :: product._2)
          			}
                    pconfigurations.update(i - 1, product)
        		}
      		}
        }

        // create a single configuration from the true features and false features list
        for (i <- 0 to pconfigurations.length - 1) {
            val config = new SimpleConfiguration(pconfigurations(i)._1, pconfigurations(i)._2)

            // need to check the configuration here again.
            if (!config.toFeatureExpr.getSatisfiableAssignment(fm, features.toSet, 1 == 1).isDefined) {
                println("no satisfiable solution for product (" + i + "): " + csvFile)
            } else {
                retList ::= config
      		}
        }

        (retList, "Generated Configs: " + retList.size + "\n")
    }

    private def countNumberOfASTElements(ast: AST): Long = {
        def countNumberOfASTElementsHelper(a: Any): Long = {
            a match {
                case l: List[_] => l.map(countNumberOfASTElementsHelper).sum
                case _: FeatureExpr => 0
                case p: Product => 1 + p.productIterator.toList.map(countNumberOfASTElementsHelper).sum
                case _ => 1
            }
        }
        countNumberOfASTElementsHelper(ast)
    }
    
    def initSampling(fm_scanner: FeatureModel, fm: FeatureModel, ast: TranslationUnit, opt: FamilyBasedVsSampleBasedOptions,
                     logMessage: String): (String, String, List[Task]) = {
        var caseStudy = ""
        var thisFilePath: String = ""
        val fileAbsPath = new File(new File(".").getAbsolutePath, opt.getFile).toString
        if (fileAbsPath.contains("linux-2.6.33.3")) {
            thisFilePath = fileAbsPath.substring(fileAbsPath.lastIndexOf("linux-2.6.33.3"))
            caseStudy = "linux"
        } else if (fileAbsPath.contains("busybox-1.18.5")) {
            thisFilePath = fileAbsPath.substring(fileAbsPath.lastIndexOf("busybox-1.18.5"))
            caseStudy = "busybox"
        } else if (fileAbsPath.contains("openssl-1.0.1c")) {
            thisFilePath = fileAbsPath.substring(fileAbsPath.lastIndexOf("openssl-1.0.1c"))
            caseStudy = "openssl"
        } else if (fileAbsPath.contains("SQLite")) {
            thisFilePath = fileAbsPath
            caseStudy = "sqlite"
        } else {
            thisFilePath = opt.getFile
        }

        val configSerializationDir = new File(thisFilePath.substring(0, thisFilePath.length - 2))

        val (configGenLog: String, typecheckingTasks: List[Task]) =
            buildConfigurations(ast, fm, opt, configSerializationDir, caseStudy)
        saveSerializationOfTasks(typecheckingTasks, features, configSerializationDir, opt.getFile)
        (configGenLog, thisFilePath, typecheckingTasks)
    }

    private class StopWatch {
        var lastStart: Long = 0
        var currentPeriod: String = "none"
        var currentPeriodId: Int = 0
        var times: Map[(Int, String), Long] = Map()
        val tb = java.lang.management.ManagementFactory.getThreadMXBean
        val nstoms = 1000000

        private def genId(): Int = { currentPeriodId += 1; currentPeriodId }

        def start(period: String) {
            val now = tb.getCurrentThreadCpuTime
            val lastTime = (now - lastStart) / nstoms
            times = times + ((genId(), currentPeriod) -> lastTime)
            lastStart = now
            currentPeriod = period
        }

        def get(period: String): Long = times.filter(v => v._1._2 == period).headOption.map(_._2).getOrElse(0)

        override def toString = {
            var res = "timing "
            val switems = times.toList.filterNot(x => x._1._2 == "none" || x._1._2 == "done").sortBy(_._1._1)

            if (switems.size > 0) {
                res = res + "("
                res = res + switems.map(_._1._2).reduce(_ + ", " + _)
                res = res + ")\n"
                res = res + switems.map(_._2.toString).reduce(_ + ";" + _)
            }
            res
        }
    }

    def checkErrorsAgainstSamplingConfigs(fm_scanner: FeatureModel, fm: FeatureModel, ast: TranslationUnit, opt: FamilyBasedVsSampleBasedOptions,
                                          logMessage: String) {

        val (log, fileID, samplingTasks) = initSampling(fm_scanner, fm, ast, opt, logMessage)
        val samplingTastsWithoutFamily = samplingTasks.filterNot { x => x._1 == "family" }
        println("starting error checking.")
        val sw = new StopWatch()

        sw.start("typechecking")
        val ts = new CTypeSystemFrontend(ast, fm, opt) with CTypeCache with CDeclUse
        ts.checkASTSilent
        sw.start("initsa")
        val sa = new CIntraAnalysisFrontend(ast, ts.asInstanceOf[CTypeSystemFrontend with CTypeCache with CDeclUse], fm)

        val outFilePrefix: String = fileID.substring(0, fileID.length - 2)

        sw.start("doublefree")
        sa.doubleFree()
        sw.start("uninitializedmemory")
        sa.uninitializedMemory()
        sw.start("casetermination")
        sa.caseTermination()
        sw.start("xfree")
        sa.xfree()
        sw.start("danglingswitchcode")
        sa.danglingSwitchCode()
        sw.start("cfginnonvoidfunc")
        sa.cfgInNonVoidFunc()
        sw.start("checkstdlibfuncreturn")
        sa.stdLibFuncReturn()
        sw.start("deadstore")
        sa.deadStore()
        sw.start("done")

        val file: File = new File(outFilePrefix + ".errreport")
        file.getParentFile.mkdirs()
        val fw: FileWriter = new FileWriter(file)
        fw.write("File : " + fileID + "\n")
        fw.write("Features : " + features.size + "\n")
        fw.write(log + "\n")

        fw.write("Potential number of data-flow errors: " + sa.errors.size + "\n\n")

        for (e <- sa.errors) fw.write(e + "\n\n")

        var caughterrorsmap = Map[String, Integer]()
        for ((name, _) <- samplingTastsWithoutFamily) caughterrorsmap += ((name, 0))


        // check for each error whether the tasklist of an sampling approach contains a configuration
        // that fullfills the error condition (using evaluate)
        for (e <- sa.errors) {
            for ((name, tasklist) <- samplingTastsWithoutFamily ) {
                if (tasklist.exists { x => e.condition.evaluate(x.getTrueSet.map(_.feature)) })
                    caughterrorsmap += ((name, 1 + caughterrorsmap(name)))
            }
        }

        val reslist = ("all", sa.errors.size) :: caughterrorsmap.toList.sortBy(_._1)
        fw.write(reslist.map(_._1).mkString(";") + "\n")
        fw.write(reslist.map(_._2).mkString(";") + "\n")

        println(sw.toString)

        fw.close()
    }

    def typecheckProducts(fm_scanner: FeatureModel, fm: FeatureModel, ast: TranslationUnit, opt: FamilyBasedVsSampleBasedOptions,
                          logMessage: String) {

        val (configGenLog, thisFilePath, typecheckingTasks) = initSampling(fm_scanner, fm, ast, opt, logMessage)
        analyzeTasks(typecheckingTasks, ast, fm, opt, thisFilePath, startLog = configGenLog)
    }

    def parseFile(stream: InputStream, file: String, dir: String): TranslationUnit = {
        import scala.collection.JavaConversions._
        val ast: AST = new ParserMain(new CParser).parserMain(
            CLexerAdapter.prepareTokens(new LexerFrontend().parseStream(stream, file, Collections.singletonList(dir), null)),SilentParserOptions)
        ast.asInstanceOf[TranslationUnit]
    }

    private def liveness(tunit: AST, udm: UseDeclMap, env: ASTEnv, fm: FeatureModel = FeatureExprFactory.empty) {
        val fdefs = filterAllASTElems[FunctionDef](tunit)
        fdefs.map( x => intraDataflowAnalysis(x, udm, env, fm))
    }

    private def intraDataflowAnalysis(f: FunctionDef, udm: UseDeclMap, env: ASTEnv, fm: FeatureModel) {
        if (f.stmt.innerStatements.isEmpty) return

        val pp = getAllPred(f, FeatureExprFactory.empty, env)
        val li = new Liveness(f, env, udm, FeatureExprFactory.empty)

        val nss = pp.map(_._1).filterNot(x => x.isInstanceOf[FunctionDef])

        for (s <- nss) {
            li.out(s)
        }
    }

    def analyzeTasks(tasks: List[Task], tunit: TranslationUnit, fm: FeatureModel, opt: FamilyBasedVsSampleBasedOptions,
                                fileID: String, startLog: String = "") {
      val log: String = startLog
      val nstoms = 1000000
      println("start analysis.")


        // measurement
        val tb = java.lang.management.ManagementFactory.getThreadMXBean

        if (tasks.size > 0) println("start task - checking (" + tasks.size + " tasks)")
        // results (taskName, (NumConfigs, productDerivationTimes, errors, typecheckingTimes, dataflowTimes))
        var configCheckingResults: List[(String, (Int, List[Long], Int, List[Long], List[Long]))] = List()
        for ((taskDesc: String, configs: List[SimpleConfiguration]) <- tasks) {
            var configurationsWithErrors = 0
            var current_config = 0
            var productDerivationTimes: List[Long] = List()
            var tcProductTimes: List[Long] = List()
            var dfProductTimes: List[Long] = List()
            for (config <- configs) {
                current_config += 1

                // product derivation
                val productDerivationStart = tb.getCurrentThreadCpuTime
                val selectedFeatures = config.getTrueSet.map(_.feature)
                val product: TranslationUnit =
                    if (taskDesc == "family") tunit
                    else ProductDerivation.deriveProduct[TranslationUnit](tunit, selectedFeatures)

                val productDerivationDiff = tb.getCurrentThreadCpuTime - productDerivationStart
                productDerivationTimes ::= (productDerivationDiff / nstoms)
                println("checking configuration " + current_config + " of " + configs.size + " (" +
                        fileID + " , " + taskDesc + ")" + "(" + countNumberOfASTElements(product) + ")" +
                        "(" + selectedFeatures.size + ")"
                )

                val ts = if (taskDesc == "family") new CTypeSystemFrontend(product, fm) with CDeclUse
                         else new CTypeSystemFrontend(product) with CDeclUse

                // typechecking measurement
                var foundError: Boolean = false
                var lastTime: Long = 0
                var curTime: Long = 0

                lastTime = tb.getCurrentThreadCpuTime
                foundError |= !ts.checkASTSilent
                curTime = tb.getCurrentThreadCpuTime - lastTime
                val productTime: Long = curTime / nstoms

                tcProductTimes ::= productTime // append to the beginning of tcProductTimes

                // liveness measurement
                var lastTimeDf: Long = 0
                var curTimeDf: Long = 0

                lastTimeDf = tb.getCurrentThreadCpuTime
                val env = CASTEnv.createASTEnv(product)
                liveness(product, ts.getUseDeclMap, env)
                curTimeDf = tb.getCurrentThreadCpuTime - lastTimeDf
                val timeDataFlowProduct = curTimeDf / nstoms

                dfProductTimes ::= timeDataFlowProduct // add to the head - reverse later

                if (foundError) configurationsWithErrors += 1
            }
            // reverse tcProductTimes to get the ordering correct
            configCheckingResults ::=(taskDesc, (configs.size, productDerivationTimes.reverse, configurationsWithErrors, dfProductTimes.reverse, tcProductTimes.reverse))
        }

      val file: File = new File(fileID + "_" + tasks.map(_._1).mkString + ".vaareport")
      file.getParentFile.mkdirs()
      val fw: FileWriter = new FileWriter(file)
      fw.write("File : " + fileID + "\n")
      fw.write("Features : " + features.size + "\n")
      fw.write(log + "\n")

        for ((taskDesc, (numConfigs, productDerivationTimes, errors, dfProductTimes, tcProductTimes)) <- configCheckingResults) {
            fw.write("\n -- Task: " + taskDesc + "\n")
            fw.write("(" + taskDesc + ")Processed configurations: " + numConfigs + "\n")
            fw.write("(" + taskDesc + ")Product Derivation Times: " + productDerivationTimes.mkString(",") + "\n")
            fw.write("(" + taskDesc + ")Configurations with errors: " + errors + "\n")
            fw.write("(" + taskDesc + ")TypecheckingSum Products: " + tcProductTimes.filter(_ > 0).sum + " ms\n")
            fw.write("(" + taskDesc + ")Typechecking Products: " + tcProductTimes.mkString(",") + "\n")
            fw.write("(" + taskDesc + ")DataflowSum Products: " + dfProductTimes.filter(_ > 0).sum + " ms\n")
            fw.write("(" + taskDesc + ")Dataflow Products: " + dfProductTimes.mkString(",") + "\n")
            fw.write("\n")
        }
        fw.close()

    }

    def configListContainsFeaturesAsEnabled(lst: List[SimpleConfiguration], features: Set[SingleFeatureExpr]): Boolean = {
        for (conf <- lst) {
            if (conf.containsAllFeaturesAsEnabled(features))
                return true
        }
        false
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
                    List(completeConfig)
                }
            } else {
                List(new SimpleConfiguration(trueFeatureObjects, falseFeatureObjects))
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
        (pwConfigs,
            " unsatisfiableCombinations:" + unsatCombinations + "\n" +
                " already covered combinations:" + alreadyCoveredCombinations + "\n" +
                " created combinations:" + pwConfigs.size + "\n")
    }

    /**
     * This version of the single-wise configs creation method collects compatible features as long as possible to create fewer configurations.
     * It works, however we need more time to execute the additional sat calls.
     * Test on "kernel/time/clocksource.c": time 91sec (normal 30sec) created configs 9 (normal 21)
     * @param features list of features
     * @param fm input feature model
     * @param existingConfigs list of configs
     * @param preferDisabledFeatures flag
     * @return
     */
    def getAllSinglewiseConfigurations_fewerConfigs(features: List[SingleFeatureExpr], fm: FeatureModel,
                                                    existingConfigs: List[SimpleConfiguration] = List(),
                                                    preferDisabledFeatures: Boolean): (List[SimpleConfiguration], String) = {
        var unsatCombinations = 0
        var alreadyCoveredCombinations = 0
        println("generating single-wise configurations")
        var pwConfigs: List[SimpleConfiguration] = List()
        var prevExpression: List[FeatureExpr] = List()
        var prevConfig: SimpleConfiguration = null
        for (f1 <- features) {
            if (!configListContainsFeaturesAsEnabled(pwConfigs ++ existingConfigs, Set(f1))) {
                // this feature was not considered yet
                // try to add to previous configs
                val ex = if (prevConfig != null) prevExpression.fold(FeatureExprFactory.True)({
                    (fe1, fe2) => fe1.and(fe2)
                })
                else f1
                val completeConfig = completeConfiguration(ex, features, fm)
                if (completeConfig != null) {
                    //println("added feature to running config")
                    prevExpression ::= f1
                    prevConfig = completeConfig
                } else {
                    if (prevConfig != null)
                        pwConfigs ::= prevConfig
                    prevExpression = List(f1)
                    val completeConfig = completeConfiguration(ex, features, fm)
                    if (completeConfig != null) {
                        //println("Started new running config")
                        prevConfig = completeConfig
                    } else {
                        prevExpression = List(FeatureExprFactory.True)
                        prevConfig = null
                        //println("no satisfiable configuration for feature " + f1)
                        unsatCombinations += 1
                    }
                }
            } else {
                //println("feature " + f1 + " already covered")
                alreadyCoveredCombinations += 1
            }
        }
        if (prevConfig != null)
            pwConfigs ::= prevConfig
        //for (f1 <- features)
        //    if (!configListContainsFeaturesAsEnabled(pwConfigs ++ existingConfigs, Set(f1)))
        //        println("results do not contain " + f1.feature)
        (pwConfigs,
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
            var f1Configs = (pwConfigs ++ existingConfigs).filter({
                _.containsAllFeaturesAsEnabled(Set(f1))
            })
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
                if (System.currentTimeMillis() - startTime > 600000) {
                    // should be 10 minutes
                    val todo = features.size
                    val done = index1 - 1
                    return (pwConfigs,
                        " unsatisfiableCombinations:" + unsatCombinations + "\n" +
                            " already covered combinations:" + alreadyCoveredCombinations + "\n" +
                            " created combinations:" + pwConfigs.size + "\n" +
                            " generation stopped after 10 minutes (" + index1 + "/" + features.size + " features processed in outer loop) => (" + ((done * done + 2 * done + 2 * todo * 100) / (todo * todo)) + "% done)\n")
                }
            }
        }
        (pwConfigs,
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
        (pwConfigs,
            " unsatisfiableCombinations:" + unsatCombinations + "\n" +
                " already covered combinations:" + alreadyCoveredCombinations + "\n" +
                " created combinations:" + pwConfigs.size + "\n")
    }


    /*
    Configuration Coverage Method copied from Joerg and heavily modified :)
     */
    /**
     * Creates configurations based on the variability nodes found in the given AST.
     * Searches for variability nodes and generates enough configurations to cover all nodes.
     * Configurations do always satisfy the FeatureModel fm.
     * If existingConfigs is non-empty, no config will be created for nodes already covered by these configurations.
     * @param astRoot root of the AST
     * @param fm The Feature Model
     * @param features The set of "interestingFeatures". Only these features will be set in the configs.
     *                 (Normally the set of all features appearing in the file.)
     * @param existingConfigs described above
     * @param preferDisabledFeatures the sat solver will prefer (many) small configs instead of (fewer) large ones
     * @param includeVariabilityFromHeaderFiles if set to false (default) we will ignore variability in files not ending with ".c".
     *                                        This corresponds to the view of the developer of a ".c" file.
     * @return
     */
    def configurationCoverage(astRoot: TranslationUnit, fm: FeatureModel, features: List[SingleFeatureExpr],
                              existingConfigs: List[SimpleConfiguration] = List(), preferDisabledFeatures: Boolean,
                              includeVariabilityFromHeaderFiles: Boolean = false):
                              (List[SimpleConfiguration], String) = {
        //val env = CASTEnv.createASTEnv(astRoot)
        val unsatCombinationsCacheFile = new File("unsatCombinationsCache.txt")
        // using this is not correct when different files have different presence conditions
        val useUnsatCombinationsCache = false
        val unsatCombinationsCache: scala.collection.immutable.HashSet[String] = if (useUnsatCombinationsCache && unsatCombinationsCacheFile.exists()) {
            new scala.collection.immutable.HashSet[String] ++ Source.fromFile(unsatCombinationsCacheFile).getLines().toSet
        } else {
            scala.collection.immutable.HashSet()
        }
        var unsatCombinations = 0
        var alreadyCoveredCombinations = 0
        var complexNodes = 0
        var simpleOrNodes = 0
        var simpleAndNodes = 0
        var nodeExpressions: Set[List[FeatureExpr]] = Set()
        def collectAnnotationLeafNodes(root: Any, previousFeatureExprs: List[FeatureExpr] = List(FeatureExprFactory.True), previousFile:String = null) {
            root match {
                case x: Opt[_] => {
                    if (x.feature.equals(previousFeatureExprs.head)){
                        collectAnnotationLeafNodes(x.entry, previousFeatureExprs, previousFile)
                    } else {
                        collectAnnotationLeafNodes(x.entry, previousFeatureExprs.::(x.feature), previousFile)
                    }
                }
                case x: Choice[_] => {
                    collectAnnotationLeafNodes(x.thenBranch, previousFeatureExprs.::(x.feature), previousFile)
                    collectAnnotationLeafNodes(x.elseBranch, previousFeatureExprs.::(x.feature.not()), previousFile)
                }
                case l: List[_] =>
                    for (x <- l) {
                        collectAnnotationLeafNodes(x, previousFeatureExprs, previousFile)
                    }
                case x: AST => {
                    val newPreviousFile = if (x.getFile.isDefined) x.getFile.get else previousFile
                    if (x.productArity == 0) {
                        // termination point of recursion
                        if (includeVariabilityFromHeaderFiles ||
                            (newPreviousFile == null || newPreviousFile.endsWith(".c"))) {
                            if (!nodeExpressions.contains(previousFeatureExprs)) {
                                nodeExpressions += previousFeatureExprs
                            }
                        }
                    } else {
                        for (y <- x.productIterator.toList) {
                            collectAnnotationLeafNodes(y, previousFeatureExprs, newPreviousFile)
                        }
                    }
                }
                case Some(x) => {
                    collectAnnotationLeafNodes(x, previousFeatureExprs, previousFile)
                }
                case None => {}
                case One(x) => {
                    collectAnnotationLeafNodes(x, previousFeatureExprs, previousFile)
                }
                case o => {
                    // termination point of recursion
                    if (includeVariabilityFromHeaderFiles ||
                        (previousFile == null || previousFile.endsWith(".c"))) {
                        if (!nodeExpressions.contains(previousFeatureExprs)) {
                            nodeExpressions += previousFeatureExprs
                        }
                    }
                }
            }
        }
        collectAnnotationLeafNodes(astRoot, List(FeatureExprFactory.True), if (astRoot.getFile.isDefined) astRoot.getFile.get else null)

        // now optNodes contains all Opt[..] nodes in the file, and choiceNodes all Choice nodes.
        // True node never needs to be handled
        val handledExpressions = scala.collection.mutable.HashSet(FeatureExprFactory.True)
        var retList: List[SimpleConfiguration] = List()
        //inner function
        def handleFeatureExpression(fex: FeatureExpr) = {
            if (!handledExpressions.contains(fex) && !(useUnsatCombinationsCache && unsatCombinationsCache.contains(fex.toTextExpr))) {
                //println("fex : " + fex.toTextExpr)
                // search for configs that imply this node
                var isCovered: Boolean = false
                fex.getConfIfSimpleAndExpr() match {
                    case None => {
                        fex.getConfIfSimpleOrExpr() match {
                            case None => {
                                complexNodes += 1
                                isCovered = (retList ++ existingConfigs).exists(
                                {
                                    conf: SimpleConfiguration => conf.toFeatureExpr.implies(fex).isTautology(fm)
                                }
                                )
                            }
                            case Some((enabled: Set[SingleFeatureExpr], disabled: Set[SingleFeatureExpr])) => {
                                simpleOrNodes += 1
                                isCovered = (retList ++ existingConfigs).exists({
                                    conf: SimpleConfiguration => conf.containsAtLeastOneFeatureAsEnabled(enabled) ||
                                        conf.containsAtLeastOneFeatureAsDisabled(disabled)
                                })
                            }
                        }
                    }
                    case Some((enabled: Set[SingleFeatureExpr], disabled: Set[SingleFeatureExpr])) => {
                        simpleAndNodes += 1
                        isCovered = (retList ++ existingConfigs).exists({
                            conf: SimpleConfiguration => conf.containsAllFeaturesAsEnabled(enabled) &&
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
                            fw.write(fex.toTextExpr + "\n")
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
        if (nodeExpressions.isEmpty ||
            (nodeExpressions.size==1 && nodeExpressions.head.equals(List(FeatureExprFactory.True)))) {
            // no feature variables in this file, build one random config and return it
            val completeConfig = completeConfiguration(FeatureExprFactory.True, features, fm, preferDisabledFeatures)
            if (completeConfig != null) {
                retList ::= completeConfig
                //println("created config for fex " + fex.toTextExpr)
            } else {
                if (useUnsatCombinationsCache) {
                    //unsatCombinationsCacheFile.getParentFile.mkdirs()
                    val fw = new FileWriter(unsatCombinationsCacheFile, true)
                    fw.write(FeatureExprFactory.True + "\n")
                    fw.close()
                }
                unsatCombinations += 1
                //println("no satisfiable configuration for fex " + fex.toTextExpr)
            }
        } else {
            for (featureList:List[FeatureExpr] <- nodeExpressions) {
                val fex: FeatureExpr = featureList.fold(FeatureExprFactory.True)(_ and _)
                handleFeatureExpression(fex)
            }
        }
        def getFeaturesInCoveredExpressions : Set[SingleFeatureExpr] = {
            // how many features have been found in this file (only the .c files)?
            var features : Set[SingleFeatureExpr] = Set()
            for (exLst <- nodeExpressions)
                for (ex <- exLst)
                    for (feature <- ex.collectDistinctFeatureObjects)
                        features += feature
            features
        }
        (retList,
            " unsatisfiableCombinations:" + unsatCombinations + "\n" +
                " already covered combinations:" + alreadyCoveredCombinations + "\n" +
                " created combinations:" + retList.size + "\n" +
                (if (!includeVariabilityFromHeaderFiles) " Features in CFile: " + getFeaturesInCoveredExpressions.size + "\n" else "") +
                " found " + nodeExpressions.size + " NodeExpressions\n" +
                " found " + simpleAndNodes + " simpleAndNodes, " + simpleOrNodes + " simpleOrNodes and " + complexNodes + " complex nodes.\n")
    }


    def getConfigsFromFiles(@SuppressWarnings(Array("unchecked")) features: List[SingleFeatureExpr], fm: FeatureModel, file: File): (List[SimpleConfiguration], String) = {
        val correctFeatureModelIncompatibility = false
        var ignoredFeatures = 0
        var changedAssignment = 0
        var totalFeatures = 0
        var fileEx: FeatureExpr = FeatureExprFactory.True
        var trueFeatures: Set[SingleFeatureExpr] = Set()
        var falseFeatures: Set[SingleFeatureExpr] = Set()

        val enabledPattern: Pattern = java.util.regex.Pattern.compile("([^=]*)=y")
        val disabledPattern: Pattern = java.util.regex.Pattern.compile("([^=]*)=n")
        for (line <- Source.fromFile(file).getLines().filterNot(_.startsWith("#")).filterNot(_.isEmpty)) {
            totalFeatures += 1
            var matcher = enabledPattern.matcher(line)
            if (matcher.matches()) {
                val name = "CONFIG_" + matcher.group(1)
                val feature = FeatureExprFactory.createDefinedExternal(name)
                var fileExTmp = fileEx.and(feature)
                if (correctFeatureModelIncompatibility) {
                    val isSat = fileExTmp.isSatisfiable(fm)
                    println(name + " " + (if (isSat) "sat" else "!sat"))
                    if (!isSat) {
                        fileExTmp = fileEx.andNot(feature)
                        println("disabling feature " + feature)
                        //fileExTmp = fileEx; println("ignoring Feature " +feature)
                        falseFeatures += feature
                        changedAssignment += 1
                    } else {
                        trueFeatures += feature
                    }
                } else {
                    trueFeatures += feature
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
                        println("! " + name + " " + (if (isSat) "sat" else "!sat"))
                        if (!isSat) {
                            fileExTmp = fileEx.and(feature)
                            println("SETTING " + name + "=y")
                            trueFeatures += feature
                            changedAssignment += 1
                        } else {
                            falseFeatures += feature
                        }
                    } else {
                        falseFeatures += feature
                    }
                    fileEx = fileExTmp
                } else {
                    ignoredFeatures += 1
                    //println("ignoring line: " + line)
                }
            }
            //println(line)
        }
        println("features mentioned in c-file but not in config: ")
        for (x <- features.filterNot((trueFeatures ++ falseFeatures).contains)) {
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

        fileEx.getSatisfiableAssignment(fm, features.toSet, 1 == 1) match {
            case None => println("configuration not satisfiable"); return (List(), "")
            case Some((en, dis)) => return (List(new SimpleConfiguration(en, dis)), "")
        }
        (List(new SimpleConfiguration(interestingTrueFeatures, interestingFalseFeatures)), "")
    }


    /**
     * Does the same as the other config-from-file method. However, it does not create additional bdd-Feature
     * expressions but uses string Sets as parameters to the sat-call.
     * Results are slightly different to the other method ?!
     * @param features list of features
     * @param fm input feature model
     * @param file input file
     * @return
     */
    def getConfigsFromFiles_noBDDcreation(@SuppressWarnings(Array("unchecked")) features: List[SingleFeatureExpr], fm: FeatureModel, file: File): (List[SimpleConfiguration], String) = {
        var ignoredFeatures = 0
        var totalFeatures = 0
        var trueFeatures: Set[String] = Set()
        var falseFeatures: Set[String] = Set()
        val enabledPattern: Pattern = java.util.regex.Pattern.compile("CONFIG_([^=]*)=y")
        val disabledPattern: Pattern = java.util.regex.Pattern.compile("CONFIG_([^=]*)=n")
        for (line <- Source.fromFile(file).getLines().filterNot(_.startsWith("#")).filterNot(_.isEmpty)) {
            totalFeatures += 1
            var matcher = enabledPattern.matcher(line)
            if (matcher.matches()) {
                val name = "CONFIG_" + matcher.group(1)
                trueFeatures += name
            } else {
                matcher = disabledPattern.matcher(line)
                if (matcher.matches()) {
                    val name = "CONFIG_" + matcher.group(1)
                    falseFeatures += name
                } else {
                    ignoredFeatures += 1
                }
            }
        }
        println("features mentioned in c-file but not in config: ")
        for (x <- features.filterNot({
            x => (trueFeatures ++ falseFeatures).contains(x.feature)
        })) {
            println(x.feature)
        }
        if (fm.isInstanceOf[BDDFeatureModel]) {
            SatSolver.getSatisfiableAssignmentFromStringSets(fm.asInstanceOf[BDDFeatureModel],
                features.toSet, trueFeatures, falseFeatures, 1 == 1) match {
                case None => println("configuration not satisfiable"); (List(), "")
                case Some((en, dis)) => {
                    val x: SimpleConfiguration = new SimpleConfiguration(en, dis)
                    if (!x.toFeatureExpr.isSatisfiable(fm)) {
                        println("created unsat expr")
                    }
                    (List(x), "")
                }
            }
        } else {
            println("ok, this works only with bdds!")
            null
        }
    }

    /**
     * Optimzed version of the completeConfiguration method. Uses FeatureExpr.getSatisfiableAssignment to need only one SAT call.
     * @param expr input feature expression
     * @param list list of features
     * @param model input feature model
     * @return
     */
    def completeConfiguration(expr: FeatureExpr, list: List[SingleFeatureExpr], model: FeatureModel, preferDisabledFeatures: Boolean = false): SimpleConfiguration = {
        expr.getSatisfiableAssignment(model, list.toSet, preferDisabledFeatures) match {
            case Some(ret) => new SimpleConfiguration(ret._1, ret._2)
            case None => null
        }
    }

    /**
     * Completes a partial configuration so that no variability remains.
     * Features are set to false if possible.
     * If no satisfiable configuration is found then null is returned.
     * @param partialConfig partical configuration in form of a feature expression
     * @param remainingFeatures list of remaining features
     * @param fm input feature model
     */
    def completeConfiguration_Inefficient(partialConfig: FeatureExpr, remainingFeatures: List[FeatureExpr], fm: FeatureModel, preferDisabledFeatures: Boolean = true): FeatureExpr = {
        var config: FeatureExpr = partialConfig
        val fIter = remainingFeatures.iterator
        while (fIter.hasNext) {
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
                    }
                }
            }
        }
        // all features have been processed, and the config is still feasible.
        // so we have a complete configuration now!
        config
    }

    /**
     * Returns a sorted list of all features in this AST, including Opt and Choice Nodes
     * @param root input element
     * @return
     */
    def getAllFeatures(root: Product): List[SingleFeatureExpr] = {
        var featuresSorted: List[SingleFeatureExpr] = getAllFeaturesRec(root).toList
        // sort to eliminate any non-determinism caused by the set
        featuresSorted = featuresSorted.sortWith({
            (x: SingleFeatureExpr, y: SingleFeatureExpr) => x.feature.compare(y.feature) > 0
        })
        println("found " + featuresSorted.size + " features")
        featuresSorted //.map({s:String => FeatureExprFactory.createDefinedExternal(s)});
    }

    private def getAllFeaturesRec(root: Any): Set[SingleFeatureExpr] = {
        root match {
            case x: Opt[_] => x.feature.collectDistinctFeatureObjects.toSet ++ getAllFeaturesRec(x.entry)
            case x: Choice[_] => x.feature.collectDistinctFeatureObjects.toSet ++ getAllFeaturesRec(x.thenBranch) ++ getAllFeaturesRec(x.elseBranch)
            case l: List[_] => {
                var ret: Set[SingleFeatureExpr] = Set()
                for (x <- l) {
                    ret = ret ++ getAllFeaturesRec(x)
                }
                ret
            }
            case x: Product => {
                var ret: Set[SingleFeatureExpr] = Set()
                for (y <- x.productIterator.toList) {
                    ret = ret ++ getAllFeaturesRec(y)
                }
                ret
            }
            case o => Set()
        }
    }

    /**
     * This method generates and tests random configurations:
     * 1. load feature model fm
     * 2. create configuration based on selection/deselection of all features
     * 3. check whether configuration is satisfiable; increase tested
     * 3.1 if satisfiable increase valid
     * 4. repeat until timeout or after a number of tested configurations
     * 5. return pair (valid, tested)
     * @param fm input feature model (used for parsing; smaller than fmts)
     * @param fmts input feature model (used for type checking); this model can be null, since only Linux has both models
     * @return
     */
    def generateAndTestRandomConfigurations(fm: FeatureModel, fmts: FeatureModel): (Long, Long) = {
        val rndGen: Random = new Random(42)

        var tested: Long = 0
        var valid: Long = 0

        val featureMap = (if (fmts == null) fm else fmts).asInstanceOf[SATFeatureModel].variables
        val startTime = System.currentTimeMillis()
        val configsUpperBound = math.pow(2, featureMap.size)
        val numTestsMax = math.min(Int.MaxValue, configsUpperBound)

        println("start report:")
        println("|features|  : " + featureMap.size)
        println("2^|features|: " + configsUpperBound)

        while (tested < numTestsMax) {
            var enabledSet: Set[SingleFeatureExpr] = Set()
            var disabledSet: Set[SingleFeatureExpr] = Set()

            enabledSet = Set()
            disabledSet = Set()

            for ((id, key) <- featureMap) {
                if (rndGen.nextBoolean()) enabledSet += SATFeatureExprFactory.createDefinedExternal(id)
                else disabledSet += SATFeatureExprFactory.createDefinedExternal(id)
            }
            // check first fm since it is smaller the check should take less time
            // and if fexpr is not valid for fm it is not valid for fmts either
            val fexpr = SATFeatureExprFactory.createFeatureExprFast(enabledSet, disabledSet)
            if (fexpr.isSatisfiable(fm)) {
                println("fexpr maybe valid! #" + tested + " tested!")
                if (fmts != null && fexpr.isSatisfiable(fmts))
                    valid += 1
            }
                tested += 1
            if (tested % 1000 == 0) {
                println("intermediate report:")
                println("elapsed time (sec): " + ((System.currentTimeMillis() - startTime) / 1000))
                println("tested configs: " + tested + " (" + ((tested * 100) / configsUpperBound) + "% of all possible)")
                println("valid configs: " + valid)
            }
        }
        println("end-of-method:")
        println("elapsed time (sec): " + ((System.currentTimeMillis() - startTime) / 1000))
        println("tested configs: " + tested + " (" + ((tested * 100) / configsUpperBound) + "% of all possible)")
        println("valid configs: " + valid)
        println("|features|: " + featureMap.size)
        println("2^|features|: " + configsUpperBound)
        (valid, tested)
    }
}