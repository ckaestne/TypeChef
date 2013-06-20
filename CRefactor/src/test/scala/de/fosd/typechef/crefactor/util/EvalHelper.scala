package de.fosd.typechef.crefactor.util

import java.io.{FileReader, BufferedReader, FileWriter, File}
import de.fosd.typechef.parser.c.{GnuAsmExpr, Id, PrettyPrinter, AST}
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr, SingleFeatureExpr, FeatureModel}
import java.util.regex.Pattern
import scala.io.Source
import de.fosd.typechef.crefactor.Logging
import de.fosd.typechef.ProductGeneration.SimpleConfiguration
import de.fosd.typechef.Frontend
import java.util.IdentityHashMap
import java.util

trait EvalHelper extends Logging {


    val caseStudyPath = "../busybox/"
    val completeBusyBoxPath = new File(caseStudyPath).getCanonicalPath
    val busyBoxFiles: String = completeBusyBoxPath + "/busybox_files"
    val busyBoxPath = completeBusyBoxPath + "/busybox-1.18.5/"
    val busyBoxPathUntouched = completeBusyBoxPath + caseStudyPath + "/busybox-1.18.5_untouched/"
    val result = "/result/"

    val filterFeatures = List("def(CONFIG_SELINUX)", "CONFIG_SELINUX")
    val allFeaturesFile = getClass.getResource("/BusyBoxAllFeatures.config").getFile
    val allFeatures = getAllFeaturesFromConfigFile(null, new File(allFeaturesFile))

    private val systemProperties: String = completeBusyBoxPath + "/redhat.properties"
    private val includeHeader: String = completeBusyBoxPath + "/config.h"
    private val includeDir: String = completeBusyBoxPath + "/busybox-1.18.5/include"
    private val featureModel: String = completeBusyBoxPath + "/featureModel"

    def writeAST(ast: AST, filePath: String) {
        val writer = new FileWriter(filePath)
        val prettyPrinted = PrettyPrinter.print(ast)
        writer.write(prettyPrinted.replaceAll("definedEx", "defined"))
        writer.flush()
        writer.close()
    }

    def writeStats(stats: List[Any], originalFilePath: String, run: Int) = {
        val dir = getResultDir(originalFilePath, run)
        val out = new java.io.FileWriter(dir.getCanonicalPath + File.separatorChar + getFileName(originalFilePath) + ".stats")
        stats.foreach(stat => {
            out.write(stat.toString)
            out.write("\n")
        })
        out.flush()
        out.close()
    }

    def writeConfig(config: List[SingleFeatureExpr], dir: File, name: String) {
        val out = new java.io.FileWriter(dir.getCanonicalPath + File.separatorChar + name)
        val disabledFeatures = allFeatures._1.diff(config)
        config.foreach(feature => {
            val ft = feature.feature
            out.write(ft + "=y")
            out.write("\n")
        })
        disabledFeatures.foreach(feature => {
            val ft = feature.feature
            if (allFeatures._2.containsKey(feature.feature)) out.write(ft + "=" + allFeatures._2.get(feature.feature))
            else out.write("# " + ft + " is not set")
            out.write("\n")
        })
        out.flush()
        out.close()
    }

    def getFileName(originalFilePath: String) = originalFilePath.substring(originalFilePath.lastIndexOf(File.separatorChar), originalFilePath.length)

    def getResultDir(originalFilePath: String, run: Int): File = {
        val outputFilePath = originalFilePath.replace("busybox-1.18.5", "result")
        val result = new File(outputFilePath + File.separatorChar + run + File.separatorChar)
        if (!result.exists()) result.mkdirs()
        result
    }

    def parse(file: File): (AST, FeatureModel) = {
        def getTypeChefArguments(file: String) = Array(file, "-c", systemProperties, "-x", "CONFIG_", "--include", includeHeader, "-I", includeDir, "--featureModelFExpr", featureModel, "--debugInterface", "--recordTiming", "--parserstatistics", "-U", "HAVE_LIBDMALLOC", "-DCONFIG_FIND", "-U", "CONFIG_FEATURE_WGET_LONG_OPTIONS", "-U", "ENABLE_NC_110_COMPAT", "-U", "CONFIG_EXTRA_COMPAT", "-D_GNU_SOURCE")
        Frontend.main(getTypeChefArguments(file.getAbsolutePath))
        (Frontend.getAST, Frontend.getFeatureModel)
    }

    def getAllRelevantIds(a: Any): List[Id] = {
        a match {
            case id: Id => if (!(id.name.startsWith("__builtin"))) List(id) else List()
            case gae: GnuAsmExpr => List()
            case l: List[_] => l.flatMap(x => getAllRelevantIds(x))
            case p: Product => p.productIterator.toList.flatMap(x => getAllRelevantIds(x))
            case k => List()
        }
    }

    def analsyeDeclUse(map: IdentityHashMap[Id, List[Id]]): List[Int] = map.keySet().toArray(Array[Id]()).map(key => map.get(key).length).toList


    def getEnabledFeaturesFromConfigFile(fm: FeatureModel, file: File): List[SingleFeatureExpr] = {
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
                    logger.info(name + " " + (if (isSat) "sat" else "!sat"))
                    if (!isSat) {
                        fileExTmp = fileEx.andNot(feature)
                        logger.info("disabling feature " + feature)
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
                            logger.info("SETTING " + name + "=y")
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
                    logger.info("ignoring line: " + line)
                }
            }
        }
        trueFeatures.toList
    }

    def getBusyBoxFiles: List[String] = {
        def readIn(reader: BufferedReader): List[String] = {
            reader.readLine() match {
                case null => List()
                case x => List(x + ".c").:::(readIn(reader))
            }
        }
        val reader = new BufferedReader(new FileReader(busyBoxFiles))
        val files = readIn(reader)
        reader.close()
        files
    }

    def getAllFeaturesFromConfigFile(fm: FeatureModel, file: File): (List[SingleFeatureExpr], IdentityHashMap[String, String]) = {
        val correctFeatureModelIncompatibility = false
        var ignoredFeatures = 0
        var changedAssignment = 0
        var totalFeatures = 0
        var fileEx: FeatureExpr = FeatureExprFactory.True
        var trueFeatures: Set[SingleFeatureExpr] = Set()
        var falseFeatures: Set[SingleFeatureExpr] = Set()
        val assignValues = new util.IdentityHashMap[String, String]()

        val enabledPattern: Pattern = java.util.regex.Pattern.compile("([^=]*)=.*")
        val disabledPattern: Pattern = java.util.regex.Pattern.compile("([^=]*) is*")
        for (line <- Source.fromFile(file).getLines().filterNot(_.startsWith("#")).filterNot(_.isEmpty)) {
            totalFeatures += 1
            var matcher = enabledPattern.matcher(line)
            if (matcher.matches()) {
                val name = matcher.group(1)
                val value = line.substring(line.lastIndexOf('=') + 1).trim
                val feature = FeatureExprFactory.createDefinedExternal(name)
                if (!value.equals("y")) assignValues.put(feature.feature, value)
                var fileExTmp = fileEx.and(feature)
                if (correctFeatureModelIncompatibility) {
                    val isSat = fileExTmp.isSatisfiable(fm)
                    logger.info(name + " " + (if (isSat) "sat" else "!sat"))
                    if (!isSat) {
                        fileExTmp = fileEx.andNot(feature)
                        logger.info("disabling feature " + feature)
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
                            logger.info("SETTING " + name + "=y")
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
                    logger.info("ignoring line: " + line)
                }
            }
        }
        (trueFeatures.toList, assignValues)
    }

    def loadConfigurationsFromCSVFile(csvFile: File, dimacsFile: File, features: List[SingleFeatureExpr], fm: FeatureModel, fnamePrefix: String = ""): (List[SimpleConfiguration], String) = {
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

}
