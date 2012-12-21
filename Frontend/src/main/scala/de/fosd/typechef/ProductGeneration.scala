package de.fosd.typechef

import conditional.{One, Choice, Opt}
import crewrite.{CAnalysisFrontend, EnforceTreeHelper, ProductDerivation}
import featureexpr._

import bdd.{BDDFeatureExpr, BDDFeatureModel, SatSolver}
import parser.c.{AST, TranslationUnit}
import typesystem.CTypeSystemFrontend
import scala.collection.immutable.HashMap
import scala.Predef._
import scala._
import collection.mutable
import collection.mutable.{ListBuffer, HashSet, BitSet}
import io.Source
import java.util.regex.Pattern
import java.lang.SuppressWarnings
import java.io._
import util.Random
import java.util.Calendar

/**
 *
 * User: rhein
 * Date: 4/2/12
 * Time: 3:45 PM
 *
 */
object ProductGeneration extends EnforceTreeHelper {
    type Task = Pair[String, List[SimpleConfiguration]]

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

    def saveSerializationOfTasks(tasks: List[(String, List[SimpleConfiguration])], featureList: List[SingleFeatureExpr], mainDir: File) {
        def writeObject(obj: java.io.Serializable, file: File) {
            try {
                file.createNewFile()
                val fileOut: FileOutputStream =
                    new FileOutputStream(file)
                val out: ObjectOutputStream =
                    new ObjectOutputStream(fileOut)
                out.writeObject(obj)
                out.close()
                fileOut.close()
            } catch {
                case i: IOException => i.printStackTrace()
            }
        }
        def toJavaList[T](orig: List[T]): java.util.ArrayList[T] = {
            val javaList: java.util.ArrayList[T] = new java.util.ArrayList[T]
            for (f: T <- orig) javaList.add(f)
            javaList
        }
        mainDir.mkdirs()
        // it seems that the scala lists cannot be serialized, so i use java ArrayLists
        writeObject(toJavaList(featureList.map(_.feature)), new File(mainDir, "FeatureHashmap.ser"))
        for ((taskName, configs) <- tasks) {
            writeObject(toJavaList(configs), new File(mainDir, taskName + ".ser"))
        }
    }

    def loadSerializedTasks(featureList: List[SingleFeatureExpr], mainDir: File): List[(String, List[SimpleConfiguration])] = {
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
            for (f: T <- orig) javaList.add(f)
            javaList
        }
        var taskList: ListBuffer[(String, List[SimpleConfiguration])] = ListBuffer()
        // it seems that the scala lists cannot be serialized, so i use java ArrayLists
        val savedFeatures: java.util.ArrayList[String] = readObject[java.util.ArrayList[String]](new File(mainDir, "FeatureHashmap.ser"))
        assert(savedFeatures.equals(toJavaList(featureList.map((_.feature)))))
        for (file <- mainDir.listFiles()) {
            val fn = file.getName
            if (!fn.equals("FeatureHashmap.ser") && fn.endsWith(".ser")) {
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

    /**
     * returns: (log:String, configs: List[Pair[String,List[SimpleConfiguration] ] ])
     * log is a compilation of the log messages
     * the configs-list contains pairs of the name of the config-generation method and the respective generated configs
     *
     */
    def buildConfigurations(family_ast: TranslationUnit, fm: FeatureModel, configSerializationDir: File, caseStudy: String): (String, List[Task]) = {
        var msg: String = ""
        var log: String = ""
        println("generating configurations.")
        var startTime: Long = 0

        initializeFeatureList(family_ast)
        //println("features: ")
        //for (f <- features) println(f)

        /**Starting with no tasks */
        var tasks: List[Task] = List()

        val useSerialization = true
        if (useSerialization &&
            configSerializationDir.exists() &&
            new File(configSerializationDir, "FeatureHashmap.ser").exists()) {
            /**Load serialized tasks */
            {
                startTime = System.currentTimeMillis()
                println("loading tasks from serialized files")
                tasks = loadSerializedTasks(features, configSerializationDir)
                msg = "Time for serialization loading: " + (System.currentTimeMillis() - startTime) + " ms\n"
                println(msg)
                log = log + msg + "\n"
            }
        }
        /**Generate tasks */
        var configurations: List[SimpleConfiguration] = List()

        /**Load config from file */

                {
                    if (tasks.find(_._1.equals("FileConfig")).isDefined) {
                        msg = "omitting FileConfig generation, because a serialized version was loaded"
                    } else {
                        val configFile = if (caseStudy.equals("linux"))
                            "../Linux_allyes_modified.config"
                        else if (caseStudy.equals("busybox"))
                            "../BusyboxBigConfig.config"
                        else if (caseStudy.equals("openssl"))
                            "/local/joliebig/OpenSSL.config"
                        else
                            throw new Exception("unknown case Study, give linux, busybox, or openssl")
                        startTime = System.currentTimeMillis()
                        val (configs, logmsg) = getConfigsFromFiles(features, fm, new File(configFile))
                        tasks :+= Pair("FileConfig", configs)
                        configurations ++= configs
                        msg = "Time for config generation (FileConfig): " + (System.currentTimeMillis() - startTime) + " ms\n" + logmsg
                    }
                    println(msg)
                    log = log + msg + "\n"
                }

        /**Henard CSV configurations */

        {
            if (tasks.find(_._1.equals("henard")).isDefined) {
                msg = "omitting henard loading, because a serialized version was loaded from serialization"
            } else {
                var productsDir: File = null
                var dimacsFM: File = null
                if (caseStudy == "linux") {
                    productsDir = new File("../TypeChef-LinuxAnalysis/generatedConfigs_henard/")
                    dimacsFM = new File("../TypeChef-LinuxAnalysis/generatedConfigs_henard/SuperFM.dimacs")
                } else if (caseStudy == "busybox") {
                    productsDir = new File("../TypeChef-BusyboxAnalysis/generatedConfigs_Henard/")
                    dimacsFM = new File("../TypeChef-BusyboxAnalysis/generatedConfigs_Henard/BB_fm.dimacs")
                } else if (caseStudy == "openssl") {
                  productsDir = new File("/local/joliebig/TypeChef-OpenSSLAnalysis/openssl-1.0.1c/generatedConfigs_Henard/")
                  dimacsFM = new File ("/local/joliebig/TypeChef-OpenSSLAnalysis/openssl-1.0.1c/generatedConfigs_Henard/Openssl.dimacs")
                } else {
                    throw new Exception("unknown case Study, give linux, busybox, or openssl")
                }
                startTime = System.currentTimeMillis()
                val (configs, logmsg) = loadConfigurationsFromCSVFile(new File("/local/joliebig/TypeChef-OpenSSLAnalysis/openssl-1.0.1c/generatedConfigs_Henard/henard.csv"), features, fm)
//                val (configs, logmsg) = loadConfigurationsFromHenardFiles(
//                    productsDir.list().map(new File(productsDir, _)).toList.
//                        filter(!_.getName.endsWith(".dat")).filter(!_.getName.endsWith(".dimacs")).
//                        sortBy({
//                        f: File => (f.getName.substring(f.getName.lastIndexOf("product") + "product".length)).toInt
//                    }),
//                    dimacsFM,
//                    features, fm)
                tasks :+= Pair("henard", configs)

                configurations ++= configs
                msg = "Time for config generation (henard): " + (System.currentTimeMillis() - startTime) + " ms\n" + logmsg
            }
            println(msg)
            log = log + msg + "\n"
        }

        /**Single-wise */

        /*
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
                    log = log + msg + "\n"
                }
        */
        /**Coverage Configurations - no Header files*/

        {
            if (tasks.find(_._1.equals("coverage_noHeader")).isDefined) {
                msg = "omitting coverage_noHeader generation, because a serialized version was loaded"
            } else {
                startTime = System.currentTimeMillis()
                val (configs, logmsg) = configurationCoverage(family_ast, fm, features, List(),
                    preferDisabledFeatures = false, includeVariabilityFromHeaderFiles = false)
                tasks :+= Pair("coverage_noHeader", configs)
                configurations ++= configs
                msg = "Time for config generation (coverage_noHeader): " + (System.currentTimeMillis() - startTime) + " ms\n" + logmsg
            }
            println(msg)
            log = log + msg + "\n"
        }

        /**Coverage Configurations - including Header files*/

        {
            if (tasks.find(_._1.equals("coverage")).isDefined) {
                msg = "omitting coverage generation, because a serialized version was loaded"
            } else {
                System.out.println("generating code coverage with header - " + Calendar.getInstance().getTime)
                startTime = System.currentTimeMillis()
                val (configs, logmsg) = configurationCoverage(family_ast, fm, features, List(),
                    preferDisabledFeatures = false, includeVariabilityFromHeaderFiles = true)
                tasks :+= Pair("coverage", configs)
                configurations ++= configs
                msg = "Time for config generation (coverage): " + (System.currentTimeMillis() - startTime) + " ms\n" + logmsg
            }
            println(msg)
            log = log + msg + "\n"
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
                    log = log + msg + "\n"
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
            log = log + msg + "\n"
        */

        /**Just one hardcoded config */
        /*
                    typecheckingTasks :+= Pair("hardcoded", getOneConfigWithFeatures(
                      List("CONFIG_LOCK_STAT"),
                      List("CONFIG_DEBUG_LOCK_ALLOC"),
                      features,fm, true)
                      )
        */
        (log, tasks)
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

    private def varAwareAnalysisSetup(fm_ts: FeatureModel, ast: AST, opt: FrontendOptions): (TranslationUnit, List[Task], String, String) = {
      var caseStudy = ""
      var thisFilePath = ""
      val fileAbsPath = new File(".").getAbsolutePath + opt.getFile
      if (fileAbsPath.contains("linux-2.6.33.3")) {
        thisFilePath = fileAbsPath.substring(opt.getFile.lastIndexOf("linux-2.6.33.3"))
        caseStudy = "linux"
      } else if (fileAbsPath.contains("busybox-1.18.5")) {
        thisFilePath = fileAbsPath.substring(opt.getFile.lastIndexOf("busybox-1.18.5"))
        caseStudy = "busybox"
      } else if (fileAbsPath.contains("openssl-1.0.1c")) {
        thisFilePath = fileAbsPath.substring(opt.getFile.lastIndexOf("openssl-1.0.1c"))
        caseStudy = "openssl"
      } else {
        thisFilePath=opt.getFile
      }

      val famast = prepareAST[TranslationUnit](ast.asInstanceOf[TranslationUnit])
      val configDir = new File("../savedConfigs/" + thisFilePath.substring(0, thisFilePath.length - 2))
      val (log: String, tasks: List[Task]) = buildConfigurations(famast, fm_ts, configDir, caseStudy)
      saveSerializationOfTasks(tasks, features, configDir)

      (famast, tasks, thisFilePath, log)
    }

    def dataflowAnalysis(fm_ts: FeatureModel, ast: AST, opt: FrontendOptions, logMessage: String) {
      val (famast, tasks, filePath, log) = varAwareAnalysisSetup(fm_ts, ast, opt)
      dataflowAnalysisTasks(famast, tasks, fm_ts, filePath, log)
    }

    private def dataflowAnalysisTasks(famast: TranslationUnit, tasks: List[Task], fm: FeatureModel, fileName: String, startLog: String = "") {
      val log:String = startLog
      println("starting product dataflow analysis.")

      // Warmup: we do a complete separate run of all tasks for warmup
      {
        val tsWarmup = new CAnalysisFrontend(famast, fm)
        val startTimeWarmup : Long = System.currentTimeMillis()
        tsWarmup.checkDataflow()
        println("warmupTime_Family" + ": " + (System.currentTimeMillis() - startTimeWarmup))
        for ((taskDesc: String, configs : List[SimpleConfiguration]) <- tasks) {
          for (configID:Int <- 0 until configs.size) {
            val product: TranslationUnit = ProductDerivation.deriveProd[TranslationUnit](famast,
              new Configuration(configs(configID).toFeatureExpr, fm))
            val ts = new CAnalysisFrontend(product, FeatureExprFactory.default.featureModelFactory.empty)
            val startTime: Long = System.currentTimeMillis()
            ts.checkDataflow()
            println("warmupTime_" + taskDesc + "_" + (configID+1) + ": " + (System.currentTimeMillis() - startTime))
          }
        }
      }

      if (tasks.size > 0) println("start task - dataflow (" + (tasks.size) + " tasks)")
      // results (taskName, (NumConfigs, errors, timeSum))
      var configCheckingResults: List[(String, (Int, Int, Long, List[Long]))] = List()
      val outFilePrefix: String = "/local/joliebig/output/reports/" + fileName.substring(0, fileName.length - 2)
      for ((taskDesc: String, configs : List[SimpleConfiguration]) <- tasks) {
        val configurationsWithErrors = 0
        var current_config = 0
        var checkTimes : List[Long] = List()
        for (config <- configs) {
          current_config += 1
          println("checking configuration " + current_config + " of " + configs.size + " (" + fileName + " , " + taskDesc + ")")
          val product: TranslationUnit = ProductDerivation.deriveProd[TranslationUnit](famast,
            new Configuration(config.toFeatureExpr, fm))
          val ts = new CAnalysisFrontend(product, FeatureExprFactory.empty)
          val startTime: Long = System.currentTimeMillis()
          ts.checkDataflow()
          val configTime: Long = System.currentTimeMillis() - startTime
          checkTimes ::= configTime // append to the beginning of checkTimes
        }
        // reverse checkTimes to get the ordering correct
        configCheckingResults ::=(taskDesc, (configs.size, configurationsWithErrors, checkTimes.sum, checkTimes.reverse))

      }
      // family base checking
      println("family-based dataflow analysis:")
      val ts = new CAnalysisFrontend(famast, fm)
      val startTime : Long = System.currentTimeMillis()
      ts.checkDataflow()
      val familyTime: Long = System.currentTimeMillis() - startTime

      val file: File = new File(outFilePrefix + "_report.txt")
      file.getParentFile.mkdirs()
      val fw : FileWriter = new FileWriter(file)
      fw.write("File : " + fileName + "\n")
      fw.write("Features : " + features.size + "\n")
      fw.write(log + "\n")

      for ((taskDesc, (numConfigs, errors, totalTime, lstTimes:List[Long])) <- configCheckingResults) {
        fw.write("\n -- Task: " + taskDesc + "\n")
        fw.write("(" + taskDesc + ")Processed configurations: " + numConfigs + "\n")
        fw.write("(" + taskDesc + ")TimeSum Products: " + totalTime + " ms\n")
        fw.write("(" + taskDesc + ")Times Products: " + lstTimes.mkString(","))
        fw.write("\n")
      }

      fw.write("Time Family:      " + familyTime + " ms\n")
      fw.close()
    }



    def typecheckProducts(fm_scanner: FeatureModel, fm_ts: FeatureModel, ast: AST, opt: FrontendOptions, logMessage: String) {
        var caseStudy = ""
        var thisFilePath: String = ""
        val fileAbsPath = new File(".").getAbsolutePath + opt.getFile
        if (fileAbsPath.contains("linux-2.6.33.3")) {
            thisFilePath = fileAbsPath.substring(fileAbsPath.lastIndexOf("linux-2.6.33.3"))
            caseStudy = "linux"
        } else if (fileAbsPath.contains("busybox-1.18.5")) {
            thisFilePath = fileAbsPath.substring(fileAbsPath.lastIndexOf("busybox-1.18.5"))
            caseStudy = "busybox"
        } else if (fileAbsPath.contains("openssl-1.0.1c")) {
          thisFilePath = fileAbsPath.substring(fileAbsPath.lastIndexOf("openssl-1.0.1c"))
          caseStudy = "openssl"
        } else {
            thisFilePath = opt.getFile
        }

        val fm = fm_ts // I got false positives while using the other fm
        val family_ast = prepareAST[TranslationUnit](ast.asInstanceOf[TranslationUnit])

        println("starting product checking.")

        val configSerializationDir = new File("/local/joliebig/output/savedConfigs/" + thisFilePath.substring(0, thisFilePath.length - 2))

        val (configGenLog: String, typecheckingTasks: List[Task]) =
            buildConfigurations(family_ast, fm_ts, configSerializationDir, caseStudy)
        saveSerializationOfTasks(typecheckingTasks, features, configSerializationDir)
        typecheckConfigurations(typecheckingTasks, family_ast, fm, family_ast, thisFilePath, startLog = configGenLog)
    }

  def median(s: Seq[Long]) = {
    val (lower, upper) = s.sortWith(_ < _).splitAt(s.size / 2)
    if (s.size % 2 == 0) (lower.last + upper.head) / 2 else upper.head
  }

  def typecheckConfigurations(typecheckingTasks: List[Task],
                                family_ast: TranslationUnit, fm: FeatureModel, ast: AST,
                                fileID: String, startLog: String = "") {
      val log: String = startLog
      val checkXTimes = 3
      val nstoms = 1000000
      println("starting product checking.")

      // family base checking
      println("family-based checking: (" + countNumberOfASTElements(family_ast) + ")")
      // analysis initialization and warmup
      val ts = new CTypeSystemFrontend(family_ast, fm)
      ts.checkASTSilent

      // measurement
      val tb = java.lang.management.ManagementFactory.getThreadMXBean
      var foundError: Boolean = false
      var times = Seq[Long]()
      var lastTime: Long = 0
      var curTime: Long = 0

      for (_ <- 0 until checkXTimes) {
        lastTime = tb.getCurrentThreadCpuTime
        foundError |= ts.checkASTSilent
        curTime = (tb.getCurrentThreadCpuTime - lastTime)
        times = times.:+(curTime)
      }
      val familyTime: Long = median(times) / nstoms

      // analysis initialization and warm-up
      val df = new CAnalysisFrontend(family_ast, fm)
      df.checkDataflow()
      var timesDf = Seq[Long]()
      var lastTimeDf: Long = 0
      var curTimeDf: Long = 0
 
      for (_ <- 0 until checkXTimes) {
        lastTimeDf = tb.getCurrentThreadCpuTime
        df.checkDataflow()
        curTimeDf = (tb.getCurrentThreadCpuTime - lastTimeDf)
        timesDf = timesDf.:+(curTimeDf)
      }
      val timeDfFamily = median(timesDf) / nstoms

      if (typecheckingTasks.size > 0) println("start task - checking (" + (typecheckingTasks.size) + " tasks)")
        // results (taskName, (NumConfigs, errors, timeSum))
        var configCheckingResults: List[(String, (Int, Int, List[Long], List[Long]))] = List()
        val outFilePrefix: String = "../reports/" + fileID.substring(0, fileID.length - 2)
        for ((taskDesc: String, configs: List[SimpleConfiguration]) <- typecheckingTasks) {
            var configurationsWithErrors = 0
            var current_config = 0
            var tcProductTimes: List[Long] = List()
            var dfProductTimes: List[Long] = List()
            for (config <- configs) {
                current_config += 1

                // product derivation
                val product: TranslationUnit = ProductDerivation.deriveProd[TranslationUnit](family_ast,
                    new Configuration(config.toFeatureExpr, fm))
                println("checking configuration " + current_config + " of " + configs.size + " (" + fileID + " , " + taskDesc + ")" + "(" + countNumberOfASTElements(product) + ")")

                // analysis initialization and warm-up
                val ts = new CTypeSystemFrontend(product, FeatureExprFactory.default.featureModelFactory.empty)
                ts.checkASTSilent

                // measurement
                var foundError: Boolean = false
                var lastTime: Long = 0
                var curTime: Long = 0
                var times = Seq[Long]()

                for (_ <- 0 until checkXTimes) {
                  lastTime = tb.getCurrentThreadCpuTime
                  foundError |= ts.checkASTSilent
                  curTime = (tb.getCurrentThreadCpuTime - lastTime)
                  times = times.:+(curTime)
                }
                val productTime: Long = median(times) / nstoms

                tcProductTimes ::= productTime // append to the beginning of tcProductTimes
                // analysis initialization and warm-up
                val df = new CAnalysisFrontend(product, FeatureExprFactory.empty)
                df.checkDataflow()

                // measurement
                var lastTimeDf: Long = 0
                var curTimeDf: Long = 0
                var timesDf = Seq[Long]()
                for (_ <- 0 until checkXTimes) {
                  lastTimeDf = tb.getCurrentThreadCpuTime
                  df.checkDataflow()
                  curTimeDf = (tb.getCurrentThreadCpuTime - lastTimeDf)
                  timesDf = timesDf.:+(curTimeDf)
                }
                val timeDataFlowProduct = median(timesDf) / nstoms

                dfProductTimes ::= timeDataFlowProduct // add to the head - reverse later

                if (foundError) configurationsWithErrors += 1
//                    var file: File = new File(outFilePrefix + "_" + taskDesc + "_errors" + current_config + ".txt")
//                    file.getParentFile.mkdirs()
//                    fw = new FileWriter(file)
//                    for (error <- ts.errors)
//                        fw.write("  - " + error + "\n")
//                    fw.close()
//                    // write product to file
//                    file = new File(outFilePrefix + "_" + taskDesc + "_" + current_config + "_product.c")
//                    fw = new FileWriter(file)
//                    fw.write(PrettyPrinter.print(product))
//                    fw.close()
//                    //write configuration to file
//                    file = new File(outFilePrefix + "_" + taskDesc + "_" + current_config + "_config.txt")
//                    fw = new FileWriter(file)
//                    fw.write(config.toString().replace("&&", "&&\n"))
//                    fw.close()
//                    // write ast to file
//                    file = new File(outFilePrefix + "_" + taskDesc + "_" + current_config + "_ast.txt")
//                    fw = new FileWriter(file)
//                    fw.write(product.toString)
//                    fw.close()
            }
            // reverse tcProductTimes to get the ordering correct
            configCheckingResults ::= (taskDesc, (configs.size, configurationsWithErrors, dfProductTimes.reverse, tcProductTimes.reverse))
        }

      val file: File = new File(outFilePrefix + "_report.txt")
      file.getParentFile.mkdirs()
      val fw: FileWriter = new FileWriter(file)
      fw.write("File : " + fileID + "\n")
      fw.write("Features : " + features.size + "\n")
      fw.write(log + "\n")

        for ((taskDesc, (numConfigs, errors, dfProductTimes, tcProductTimes: List[Long])) <- configCheckingResults) {
            fw.write("\n -- Task: " + taskDesc + "\n")
            fw.write("(" + taskDesc + ")Processed configurations: " + numConfigs + "\n")
            fw.write("(" + taskDesc + ")Configurations with errors: " + errors + "\n")
            fw.write("(" + taskDesc + ")TimeSum Products: " + tcProductTimes.filter(_ > 0).sum + " ms\n")
            fw.write("(" + taskDesc + ")Times Products: " + tcProductTimes.mkString(",") + "\n")
            fw.write("(" + taskDesc + ")DataflowSum Products: " + dfProductTimes.filter(_ > 0).sum + " ms\n")
            fw.write("(" + taskDesc + ")Dataflow Products: " + dfProductTimes.mkString(",") + "\n")
            fw.write("\n")
        }

        fw.write("Errors in family check: " + (if (foundError) "No" else "Yes") + "\n")
        fw.write("Time Family:      " + familyTime + " ms\n")
        fw.write("Dataflow Time Family:     " + timeDfFamily + " ms\n")
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
            new scala.collection.immutable.HashSet[String] ++ (Source.fromFile(unsatCombinationsCacheFile).getLines()).toSet
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
                    val newPreviousFile = (if (x.getFile.isDefined) x.getFile.get else previousFile)
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
                case Some(x) => {collectAnnotationLeafNodes(x, previousFeatureExprs, previousFile)}
                case None => {}
                case One(x) => {collectAnnotationLeafNodes(x, previousFeatureExprs, previousFile)}
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
        collectAnnotationLeafNodes(astRoot, List(FeatureExprFactory.True), (if (astRoot.getFile.isDefined) astRoot.getFile.get else null))

        // now optNodes contains all Opt[..] nodes in the file, and choiceNodes all Choice nodes.
        // True node never needs to be handled
        val handledExpressions: HashSet[FeatureExpr] = HashSet(FeatureExprFactory.True)
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
                (if (!includeVariabilityFromHeaderFiles) (" Features in CFile: " + getFeaturesInCoveredExpressions.size + "\n") else "") +
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
                val name = matcher.group(1)
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
                    val name = matcher.group(1)
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
        println("features mentioned in c-file but not in config2: ")
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

    def loadConfigurationsFromHenardFiles(files: List[File], dimacsFile: File, features: List[SingleFeatureExpr], fm: FeatureModel): (List[SimpleConfiguration], String) = {
        def getConfigID(filename: String): Int = {
            // this is specific for the files generated by henard
            // example: "2.6.33.3-2var.dimacs_GA-SimpleGAProducts-200prods-60000ms-run1.product4"
            (filename.substring(filename.lastIndexOf("product") + "product".length)).toInt
        }
        var retList: List[SimpleConfiguration] = List()
        var featureNamesTmp: List[String] = List("--dummy--") // we have to pre-set index 0, so that the real indices start with 1
        var currentLine: Int = 1
        for (line: String <- Source.fromFile(dimacsFile).getLines().takeWhile(_.startsWith("c"))) {
            //format: "c 3779 AT76C50X_USB"
            val lineElements: Array[String] = line.split(" ")
            if (!lineElements(1).endsWith("$")) {
                // feature indices ending with $ are artificial and can be ignored here
                assert(augmentString(lineElements(1)).toInt.equals(currentLine), "\"" + lineElements(1) + "\"" + " != " + currentLine)
                featureNamesTmp ::= lineElements(2)
                //assert (featureNamesTmp.head.equals(lineElements(2)))
            }
            currentLine += 1
        }

        val featureNames: Array[String] = featureNamesTmp.reverse.toArray
        featureNamesTmp = null
        val interestingFeaturesMap: scala.collection.mutable.HashMap[Int, SingleFeatureExpr] = new scala.collection.mutable.HashMap()
        for (i <- 0.to(featureNames.length - 1)) {
            val searchResult = features.find(_.feature.equals("CONFIG_" + featureNames(i)))
            if (searchResult.isDefined) {
                interestingFeaturesMap.update(i, searchResult.get)
            }
        }
        var unsat_configs: List[Int] = List()
        for (file: File <- files) {
            // load
            var trueFeatures: List[SingleFeatureExpr] = List()
            var falseFeatures: List[SingleFeatureExpr] = List()
            //var fex = FeatureExprFactory.True;
            for (line: String <- Source.fromFile(file).getLines()) {
                val lineContent: Int = augmentString(line).toInt
                if (interestingFeaturesMap.contains(math.abs(lineContent))) {
                    if (lineContent > 0) {
                        trueFeatures ::= interestingFeaturesMap(math.abs(lineContent))
                        //println(interestingFeaturesMap(math.abs(lineContent)) +  " := true (" + (lineContent) + ")")
                    } else {
                        falseFeatures ::= interestingFeaturesMap(math.abs(lineContent))
                        //println(interestingFeaturesMap(math.abs(lineContent)) +  " := false (" + (lineContent) + ")")
                    }
                }
            }
            val config = new SimpleConfiguration(trueFeatures, falseFeatures)
            if (!config.toFeatureExpr.getSatisfiableAssignment(fm, features.toSet, 1 == 1).isDefined) {
                //println("no satisfiable solution for product: " + file)
                unsat_configs ::= getConfigID(file.getName)
            } else {
                //println("Config" + getConfigID(file.getName) + " true Features : " + "%3d".format(trueFeatures.size) +" false Features : " + falseFeatures.size)
                retList ::= config
            }
        }
        (retList, "Generated Configs: " + retList.size + "\n" +
            "Unsat Configs:" + unsat_configs.mkString("{", ",", "}"))
    }

  def loadConfigurationsFromCSVFile(csvFile: File, features: List[SingleFeatureExpr], fm: FeatureModel): (List[SimpleConfiguration], String) = {
    var retList: List[SimpleConfiguration] = List()
    val lines = Source.fromFile(csvFile).getLines().filterNot(_.startsWith("#")).filterNot(_.isEmpty)
    val headline = lines.next()
    val featureNames: Array[String] = headline.split(";")
    val interestingFeaturesMap: scala.collection.mutable.HashMap[Int, SingleFeatureExpr] = new scala.collection.mutable.HashMap()
    /*
            println("myList:")
            println(features.slice(0,10).map(_.feature).mkString(";"))

            println("csv:")
            println(featureNames.slice(0,10).mkString(";"))
    */

    for (i <- 0.to(featureNames.length - 1)) {
      val searchResult = features.find(_.feature.equals(featureNames(i).substring(featureNames(i).indexOf(":") + 1)))
      if (searchResult.isDefined) {
        interestingFeaturesMap.update(i, searchResult.get)
      }
    }
    println("interestingFsize: " + interestingFeaturesMap.size)
    println("first feature: " + featureNames(0))
    println("last feature: " + featureNames(featureNames.length - 1))
    var line = 0
    while (lines.hasNext) {
      line += 1
      val currentLineElements: Array[String] = lines.next().split(";")
      var trueFeatures: List[SingleFeatureExpr] = List()
      var falseFeatures: List[SingleFeatureExpr] = List()
      for (i <- 0.to(currentLineElements.length - 1)) {
        if (currentLineElements(i).toUpperCase.equals("X")) {
          //println("on: " + featureNames(i))
          if (featureNames(i).substring(featureNames(i).indexOf(":") + 1).equals("X86_32") || featureNames(i).substring(featureNames(i).indexOf(":") + 1).equals("64BIT"))
            println("active: " + featureNames(i))
          if (interestingFeaturesMap.contains(i))
            trueFeatures ::= interestingFeaturesMap(i)
        } else if (currentLineElements(i).equals("-")) {
          //println("off: " + featureNames(i))
          if (featureNames(i).substring(featureNames(i).indexOf(":") + 1).equals("X86_32") || featureNames(i).substring(featureNames(i).indexOf(":") + 1).equals("64BIT"))
            println("deactivated: " + featureNames(i))
          if (interestingFeaturesMap.contains(i))
            falseFeatures ::= interestingFeaturesMap(i)
        } else
          println("csv file contains an element that is not \"X\" and not \"-\"! " + csvFile + " element: " + currentLineElements(i))
      }
      println("true Features : " + trueFeatures.size)
      println("false Features : " + falseFeatures.size)
      println("all: " + features.size)
      if (!FeatureExprFactory.True.getSatisfiableAssignment(fm, features.toSet, 1 == 1).isDefined) {
        println("no satisfiable solution for product in line " + line)
      }
      retList ::= new SimpleConfiguration(trueFeatures, falseFeatures)
    }
    (retList, "")
  }

  def loadConfigurationsFromCSVFile2(csvFile: File, features: List[SingleFeatureExpr],
                                    fm: FeatureModel, kconfigonly: Boolean = true): List[SimpleConfiguration] = {
    var retlist: List[SimpleConfiguration] = List()

    // filter lines with comments out
    val lines = Source.fromFile(csvFile).getLines().filterNot(_.startsWith("#")).filterNot(_.isEmpty)

    // map with feature names we care fore
    val interestingfeaturesmap: scala.collection.mutable.HashMap[Int, SingleFeatureExpr] = new scala.collection.mutable.HashMap()

    while (lines.hasNext) {
      // check whether line contains mapping between int->feature name
      // if so then add the mapping to the map interestingfeaturesmap
      // create map with features we care for together with the corresponding
      // integer that is used internally by the sat solver
      // for systems that use kconfig we filter for "CONFIG_"
      // for all other systems we use all features
      val curline = lines.next()
      if (curline.contains("->")) {
        val res = curline.split("->")
        val featureid = res(0).toInt
        val featurename = if (kconfigonly) "CONFIG_" + res(1) else res(1)

        features.find(_.feature.equals(featurename)) match {
          case Some(x) => interestingfeaturesmap.update(featureid, x)
          case None    => ;
        }
      } else {
        // the line specifies one product
        // format is
        // 1;-2;3 ...
        // numbers denote feature identifiers
        // >0 feature selected
        // <0 feature not selected
        var truefeatures: List[SingleFeatureExpr] = List()
        var falsefeatures: List[SingleFeatureExpr] = List()

        val productconf: Array[String] = curline.split(";")

        for (featureid <- productconf) {
          if (featureid(0) == '-') falsefeatures ::= interestingfeaturesmap.get(featureid.substring(1).toInt).get
          else truefeatures ::= interestingfeaturesmap.get(featureid.toInt).get
        }

        retlist ::= new SimpleConfiguration(truefeatures, falsefeatures)
      }

    }

    retlist
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
                val name = matcher.group(1)
                trueFeatures += name
            } else {
                matcher = disabledPattern.matcher(line)
                if (matcher.matches()) {
                    val name = matcher.group(1)
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
     * This method works, but it is hopeless.
     * I had it run for 10 minutes on one file (tested 10,000 configurations) but this was
     * only 1E-66% of all possible configs (no valid config found).
     * @param astRoot input ast element
     * @param fm input feature model
     * @return
     */
    def estimateNumberOfVariants(astRoot: AST, fm: FeatureModel): (Long, Long) = {
        // init features list and hashmap
        initializeFeatureList(astRoot)


        val testedConfigs: HashSet[SimpleConfiguration] = new mutable.HashSet[SimpleConfiguration]()
        val rndGen: Random = new Random(42)

        var tested: Long = 0
        var valid: Long = 0

        val configsUpperBound = math.pow(2, features.size)
        val numTestsMax = math.min(Int.MaxValue, configsUpperBound)
        //val maxTimeMs = 300000; // 5 minutes
        //val maxTimeMs = 600000; // 10 minutes
        val maxTimeMs = 10800000
        // 3 hours
        val maxSearchTimeOneConfig = 2000
        // 5 seconds
        val startTime = System.currentTimeMillis()

        while (tested < numTestsMax && (System.currentTimeMillis() - startTime) < maxTimeMs) {
            var config: SimpleConfiguration = null
            val startTimeSearchOneConfig = System.currentTimeMillis()
            var enSize, disSize = 0
            var enabledList: List[SingleFeatureExpr] = List()
            var disabledList: List[SingleFeatureExpr] = List()
            while ((config == null || testedConfigs.contains(config)) &&
                (System.currentTimeMillis() - startTimeSearchOneConfig) < maxSearchTimeOneConfig) {
                enabledList = List()
                disabledList = List()
                for (f <- features) {
                    if (rndGen.nextBoolean()) enabledList ::= f
                    else disabledList ::= f
                }
                enSize = enabledList.size
                disSize = disabledList.size
                config = new SimpleConfiguration(enabledList, disabledList)
            }
            val fex = config.toFeatureExpr
            if (fex.isSatisfiable(fm)) {
                tested += 1
                valid += 1
                //println("config " + tested + " sat " + enSize + " enabled and " + disSize + " disabled features")
            } else {
                tested += 1
                //println("config " + tested + " unsat " + enSize + " enabled and " + disSize + " disabled features")
            }
            if (fex.isInstanceOf[BDDFeatureExpr])
                fex.asInstanceOf[BDDFeatureExpr].freeBDD() // we can safely free the bdd here, because we will never use the same expression again.
            testedConfigs.add(config)
            if (tested % 1000 == 0) {
                println("intermediate report:")
                println("elapsed time (sec): " + ((System.currentTimeMillis() - startTime) / 1000))
                println("tested configs: " + tested + " (" + ((tested * 100) / configsUpperBound) + "% of all possible)")
                println("valid configs: " + valid)
                println("|features|: " + features.size)
                println("2^|features|: " + configsUpperBound)
            }
        }
        println("end-of-method:")
        println("elapsed time (sec): " + ((System.currentTimeMillis() - startTime) / 1000))
        println("tested configs: " + tested + " (" + ((tested * 100) / configsUpperBound) + "% of all possible)")
        println("valid configs: " + valid)
        println("|features|: " + features.size)
        println("2^|features|: " + configsUpperBound)
        (valid, tested)
    }
}