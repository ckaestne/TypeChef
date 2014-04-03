package de.fosd.typechef

import java.io._
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr, SingleFeatureExpr, FeatureModel}
import scala.io.Source
import java.util.regex.Pattern
import scala.Some
import scala.collection.mutable.ListBuffer
import de.fosd.typechef.parser.c.{AST, TranslationUnit}
import de.fosd.typechef.conditional._

object ConfigurationHandling {
    def loadConfigurationsFromCSVFile(csvFile: File, dimacsFile: File,
                                      ff: FileFeatures, fm: FeatureModel, fnamePrefix: String = ""):
    (List[SimpleConfiguration], String) = {
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
            val searchResult = ff.features.find(_.feature == (fnamePrefix + featureNames(i)))
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

        // iterate over all lines with Features, determine the selection/deselection
        // in available products and add it to product configurations (true features / false features)
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
            val config = new SimpleConfiguration(ff, pconfigurations(i)._1, pconfigurations(i)._2)

            // need to check the configuration here again.
            if (!config.toFeatureExpr.getSatisfiableAssignment(fm, ff.features.toSet, 1 == 1).isDefined) {
                println("no satisfiable solution for product (" + i + "): " + csvFile)
            } else {
                retList ::= config
            }
        }

        (retList, "Generated Configs: " + retList.size + "\n")
    }

    def loadConfigurationFromKconfigFile(ff: FileFeatures, fm: FeatureModel, file: File): (List[SimpleConfiguration], String) = {
        val features = ff.features
        var trueFeatures: Set[SingleFeatureExpr] = Set()
        var falseFeatures: Set[SingleFeatureExpr] = Set()
        var logMsg = ""
        var retList: List[SimpleConfiguration] = List()

        val enabledPattern: Pattern = java.util.regex.Pattern.compile("([^=]*)=y")
        val disabledPattern: Pattern = java.util.regex.Pattern.compile("([^=]*)=n")
        for (line <- Source.fromFile(file).getLines().filterNot(_.startsWith("#")).filterNot(_.isEmpty)) {
            var matcher = enabledPattern.matcher(line)
            if (matcher.matches()) {
                val name = matcher.group(1)
                val feature = FeatureExprFactory.createDefinedExternal(name)
                trueFeatures += feature
            } else {
                matcher = disabledPattern.matcher(line)
                if (matcher.matches()) {
                    val name = matcher.group(1)
                    val feature = FeatureExprFactory.createDefinedExternal(name)
                    falseFeatures += feature
                }
            }
        }
        println("features mentioned in c-file but not in config: ")
        for (x <- features.filterNot((trueFeatures ++ falseFeatures).contains)) {
            println(x.feature)
        }
        val interestingTrueFeatures = trueFeatures.filter(features.contains).toList
        val interestingFalseFeatures = falseFeatures.filter(features.contains).toList

        val config = new SimpleConfiguration(ff, interestingTrueFeatures, interestingFalseFeatures)
        if (config.toFeatureExpr.isSatisfiable(fm))
            retList ::= config
        else
            logMsg += "Configuration not satisfiable!"

        (retList, logMsg)
    }

    def saveSerializedConfigurations(tasks: List[Task], featureList: List[SingleFeatureExpr],
                                     mainDir: File, file: String) {
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

        for ((taskName, configs) <- tasks) {
            writeObject(toJavaList(configs), new File(mainDir, taskName + ".ser"))
        }
    }

    def loadSerializedConfigurations(featureList: List[SingleFeatureExpr], mainDir: File): List[Task] = {
        def readObject[T](file: File): T = {
            try {
                val fileIn: FileInputStream = new FileInputStream(file)
                val in: ObjectInputStream = new ObjectInputStream(fileIn)
                val e: T = in.readObject().asInstanceOf[T]
                in.close()
                fileIn.close()
                e
            } catch {
                case i: IOException => throw i
            }
        }

        var taskList: ListBuffer[Task] = ListBuffer()

        // assert(savedFeatures.equals(toJavaList(featureList.map(_.feature))))
        for (file <- mainDir.listFiles()) {
            val fn = file.getName
            if (fn.endsWith(".ser")) {
                val configs = readObject[java.util.ArrayList[SimpleConfiguration]](file)
                val taskName = fn.substring(0, fn.length - ".ser".length)
                var taskConfigs: scala.collection.mutable.ListBuffer[SimpleConfiguration] = ListBuffer()
                val i = configs.iterator()
                while (i.hasNext) {
                    taskConfigs += i.next()
                }
                taskList.+=((taskName, taskConfigs.toList))
            }
        }
        taskList.toList
    }


    def buildConfigurationsSingleConf(tunit: TranslationUnit, ff: FileFeatures, fm: FeatureModel,
                                      opt: FamilyBasedVsSampleBasedOptions, configDir: File,
                                      caseStudy: String, exTasks: List[Task]): (String, List[Task]) = {
        var tasks: List[Task] = List()
        var log = ""
        var msg = ""
        var startTime: Long = 0

        if (exTasks.exists(_._1 == "singleconf")) {
            msg = "omitting singleconf generation, because a serialized version was loaded"
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
                throw new Exception("unknown case Study, give linux, busybox, openssl, or sqlite")
            startTime = System.currentTimeMillis()
            val (configs, logMsg) = ConfigurationHandling.loadConfigurationFromKconfigFile(ff, fm,
                new File(configFile))
            tasks :+= Pair("singleconf", configs)
            msg = "Time for config generation (singleconf): " + (System.currentTimeMillis() - startTime) +
                " ms\n" + logMsg
        }
        println(msg)
        log = log + msg + "\n"
        (log, tasks)
    }

    def buildConfigurationsPairwise(tunit: TranslationUnit, ff: FileFeatures, fm: FeatureModel,
                                    opt: FamilyBasedVsSampleBasedOptions, configDir: File,
                                    caseStudy: String, exTasks: List[Task]): (String, List[Task]) = {
        var tasks: List[Task] = List()
        var log = ""
        var msg = ""
        var startTime: Long = 0

        if (exTasks.exists(_._1 == "pairwise")) {
            msg = "omitting pairwise generation, because a serialized version was loaded"
        } else {
            var productsFile: File = null
            var dimacsFM: File = null
            var featurePrefix = ""
            if (caseStudy == "linux") {
                productsFile = new File(opt.getRootFolder + "TypeChef-LinuxAnalysis/linux_pairwise_configs.csv")
                dimacsFM = new File(opt.getRootFolder + "TypeChef-LinuxAnalysis/2.6.33.3-2var.dimacs")
                featurePrefix = "CONFIG_"
            } else if (caseStudy == "busybox") {
                productsFile = new File(opt.getRootFolder + "TypeChef-BusyboxAnalysis/busybox_pairwise_configs.csv")
                dimacsFM = new File(opt.getRootFolder + "TypeChef-BusyboxAnalysis/BB_fm.dimacs")
                featurePrefix = "CONFIG_"
            } else if (caseStudy == "openssl") {
                productsFile = new File(opt.getRootFolder +
                    "TypeChef-OpenSSLAnalysis/openssl-1.0.1c/openssl_pairwise_configs.csv")
                dimacsFM = new File(opt.getRootFolder +
                    "TypeChef-OpenSSLAnalysis/openssl-1.0.1c/openssl.dimacs")
            } else if (caseStudy == "sqlite") {
                productsFile = new File(opt.getRootFolder +
                    "cRefactor-SQLiteEvaluation/sqlite_pairwise_configs.csv")
                dimacsFM = new File(opt.getRootFolder + "cRefactor-SQLiteEvaluation/sqlite.dimacs")
            } else {
                throw new Exception("unknown case Study, give linux or busybox")
            }
            startTime = System.currentTimeMillis()

            val (configs, logMsg) = ConfigurationHandling.loadConfigurationsFromCSVFile(productsFile, dimacsFM, ff,
                fm, featurePrefix)

            tasks :+= Pair("pairwise", configs)
            msg = "Time for config generation (pairwise): " + (System.currentTimeMillis() - startTime) +
                " ms\n" + logMsg
        }
        println(msg)
        log = log + msg + "\n"
        (log, tasks)
    }

    def buildConfigurationsCodeCoverageNH(tunit: TranslationUnit, ff: FileFeatures, fm: FeatureModel,
                                          configDir: File, caseStudy: String, exTasks: List[Task])
    : (String, List[Task]) = {
        var tasks: List[Task] = List()
        var log = ""
        var msg = ""
        var startTime: Long = 0
        if (exTasks.exists(_._1 == "coverage_noHeader")) {
            msg = "omitting coverage_noHeader generation, because a serialized version was loaded"
        } else {
            startTime = System.currentTimeMillis()
            val (configs, logMsg) = configurationCoverage(tunit, fm, ff, List(),
                preferDisabledFeatures = false, includeVariabilityFromHeaderFiles = false)
            tasks :+= Pair("coverage_noHeader", configs)
            msg = "Time for config generation (coverage_noHeader): " +
                (System.currentTimeMillis() - startTime) + " ms\n" + logMsg
        }
        println(msg)
        log = log + msg + "\n"

        (log, tasks)
    }

    def buildConfigurationsCodeCoverage(tunit: TranslationUnit, ff: FileFeatures, fm: FeatureModel,
                                                configDir: File, caseStudy: String, exTasks: List[Task])
    : (String, List[Task]) = {
        var tasks: List[Task] = List()
        var log = ""
        var msg = ""
        var startTime: Long = 0
        if (caseStudy != "linux") {
            if (exTasks.exists(_._1 == "coverage")) {
                msg = "omitting coverage generation, because a serialized version was loaded"
            } else {
                startTime = System.currentTimeMillis()
                val (configs, logMsg) = configurationCoverage(tunit, fm, ff, List(),
                    preferDisabledFeatures = false, includeVariabilityFromHeaderFiles = true)
                tasks :+= Pair("coverage", configs)
                msg = "Time for config generation (coverage): " +
                    (System.currentTimeMillis() - startTime) + " ms\n" + logMsg
            }
            println(msg)
            log = log + msg + "\n"
        } else {
            println("omit code coverage for case study linux; computation is too expensive!")
        }

        (log, tasks)
    }

    /**
     * Implements the naive variant of code coverage, with option for including variability in header
     * files or not.
     *
     * For more information, see https://www4.cs.fau.de/Publications/2012/tartler_12_osr.pdf p 12
     *
     * Creates configurations based on the variability nodes found in the given AST.
     * Searches for variable AST nodes and generates enough configurations to cover them all.
     * Configurations do always satisfy the FeatureModel fm.
     * If existingConfigs is non-empty, no config will be created for nodes already covered by these
     * configurations.
     * @param astRoot root of the AST
     * @param fm The Feature Model
     * @param ff The set of "interestingFeatures". Only these features will be set in the configs.
     *                 (Normally the set of all features appearing in the file.)
     * @param existingConfigs described above
     * @param preferDisabledFeatures the sat solver will prefer (many) small configs instead of (fewer) large ones
     * @param includeVariabilityFromHeaderFiles if set to false (default) we will ignore variability in
     *                                          files not ending with ".c".
     *                                          This corresponds to the view of the developer of a ".c" file.
     * @return
     */
    private def configurationCoverage(astRoot: TranslationUnit, fm: FeatureModel, ff: FileFeatures,
                                      existingConfigs: List[SimpleConfiguration] = List(),
                                      preferDisabledFeatures: Boolean,
                                      includeVariabilityFromHeaderFiles: Boolean = false):
    (List[SimpleConfiguration], String) = {
        var presenceConditions: Set[Set[FeatureExpr]] = Set()

        def collectPresenceConditions(root: Any, curFeatureExprSet: Set[FeatureExpr], curFile: String = null) {
            root match {
                case x: Opt[_] =>
                    collectPresenceConditions(x.entry, curFeatureExprSet + x.feature, curFile)
                case x: Choice[_] =>
                    collectPresenceConditions(x.thenBranch, curFeatureExprSet + x.feature, curFile)
                    collectPresenceConditions(x.elseBranch, curFeatureExprSet + x.feature.not(), curFile)
                case One(x) => collectPresenceConditions(x, curFeatureExprSet, curFile)

                case l: List[_] =>
                    for (x <- l) {
                        collectPresenceConditions(x, curFeatureExprSet, curFile)
                    }
                case x: AST =>
                    val newFile = if (x.getFile.isDefined) x.getFile.get else curFile
                    if (x.productArity == 0) {
                        // termination point of recursion
                        if (includeVariabilityFromHeaderFiles ||
                            (newFile != null && newFile.endsWith(".c"))) {
                            if (!presenceConditions.contains(curFeatureExprSet))
                                presenceConditions += curFeatureExprSet
                        }
                    } else {
                        for (y <- x.productIterator.toList) {
                            collectPresenceConditions(y, curFeatureExprSet, newFile)
                        }
                    }
                case Some(x) => collectPresenceConditions(x, curFeatureExprSet, curFile)
                case None =>
                case o =>
                    // termination point of recursion
                    if (includeVariabilityFromHeaderFiles ||
                        (curFile != null && curFile.endsWith(".c"))) {
                        if (!presenceConditions.contains(curFeatureExprSet))
                            presenceConditions += curFeatureExprSet
                    }
            }
        }
        // we set the empty string as default curFile; will be overridden when we hit an AST element with
        // proper file information
        collectPresenceConditions(astRoot, Set(FeatureExprFactory.True), "")

        var retList: List[SimpleConfiguration] = List()

        // if no proper presence condition occurred in the file, build one random config and return it
        if (presenceConditions.isEmpty) {
            val completeConfig = completeConfiguration(FeatureExprFactory.True, ff, fm, preferDisabledFeatures)
            if (completeConfig != null) {
                retList ::= completeConfig
            }
        } else {
            for (featureSet: Set[FeatureExpr] <- presenceConditions) {
                val pc: FeatureExpr = featureSet.fold(FeatureExprFactory.True)(_ and _)

                if (pc.isSatisfiable(fm)) {
                    val completeConfig = completeConfiguration(pc, ff, fm, preferDisabledFeatures)
                    if (completeConfig != null) {
                        retList ::= completeConfig
                    }
                }
            }
        }

        // Determine all features in presence conditions collected from AST.
        def getFeaturesInCoveredExpressions: Set[SingleFeatureExpr] = {
            var features: Set[SingleFeatureExpr] = Set()
            for (featureExprSet <- presenceConditions)
                for (featureExpr <- featureExprSet)
                    features ++= featureExpr.collectDistinctFeatureObjects
            features
        }

        (retList, " created combinations:" + retList.size + "\n" +
            (if (!includeVariabilityFromHeaderFiles) " Features in CFile: " +
                getFeaturesInCoveredExpressions.size + "\n" else "") + "\n")
    }


    /**
     * Optimized version of the completeConfiguration method. Uses FeatureExpr.getSatisfiableAssignment
     * to need only one SAT call.
     * @param expr input feature expression
     * @param ff file features
     * @param model input feature model
     * @return
     */
    private def completeConfiguration(expr: FeatureExpr, ff: FileFeatures, model: FeatureModel,
                              preferDisabledFeatures: Boolean = false):
    SimpleConfiguration = {
        expr.getSatisfiableAssignment(model, ff.features.toSet, preferDisabledFeatures) match {
            case Some(ret) => new SimpleConfiguration(ff, ret._1, ret._2)
            case None => null
        }
    }

}
