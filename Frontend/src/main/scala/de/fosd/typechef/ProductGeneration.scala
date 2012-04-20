package de.fosd.typechef

import conditional.{Conditional, Choice, Opt}
import crewrite.{CASTEnv, ConfigurationCoverage, CAnalysisFrontend}
import featureexpr._
import parser.c.{AST, PrettyPrinter, TranslationUnit}
import parser.WithPosition
import org.kiama.rewriting.Rewriter._
import java.util.Date
import typesystem.CTypeSystemFrontend
import scala.Boolean
import scala.Tuple
import java.io.{File, FileWriter}
import collection.mutable.Queue
import scala.Predef._

/**
 *
 * User: rhein
 * Date: 4/2/12
 * Time: 3:45 PM
 *
 */

object ProductGeneration {
  def typecheckProducts(fm:FeatureModel, fm_ts : FeatureModel, ast :AST, opt: FrontendOptions) {
    println("starting product typechecking.")
    val cf = new CAnalysisFrontend(ast.asInstanceOf[TranslationUnit], fm)
    var family_ast = cf.prepareAST[TranslationUnit](ast.asInstanceOf[TranslationUnit])
    var family_env = cf.createASTEnv(family_ast)

    // PLAN: Ich bekomme ständig Fehlermedlungen, die keine Positionsangaben enthalten.
    // also: den ganzen AST durchgehen, bei allen Elementen, die keine Angaben haben, die der parents einfügen.

    val tmpq : Queue[Product] = Queue()
    tmpq.+=(family_ast)
    while (!tmpq.isEmpty) {
      val x : Product = tmpq.dequeue()

      if(x.isInstanceOf[AST] && !x.asInstanceOf[AST].hasPosition) {
        var parent :Any = family_env.parent(x)
        var parentisnull = (parent == null)
        while (!parentisnull && (!parent.isInstanceOf[AST] || !parent.asInstanceOf[AST].hasPosition)) {
          parent = family_env.parent(parent)
          parentisnull = (parent == null)
        }
        if (x.isInstanceOf[AST] && !parentisnull) {
          x.asInstanceOf[AST].setPositionRange(parent.asInstanceOf[AST].getPositionFrom,parent.asInstanceOf[AST].getPositionTo)
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
    family_env = cf.createASTEnv(family_ast)
    var fw : FileWriter = null

/** write family ast */
/*
    fw = new FileWriter(new File("../ast_fam.txt"))
    fw.write(family_ast.toString)
    fw.close()
*/


    println("generating configurations.")

    val features : List[DefinedExpr] = getAllFeatures(family_ast)
    //for (f <- features) println(f.feature)

    var typecheckingTasks : List[Pair[String, List[FeatureExpr]]] = List()

/**  All products */
    //typecheckingTasks ::= Pair("allProducts", getAllProducts(features, fm, family_env.asInstanceOf[ConfigurationCoverage.ASTEnv]))
/**  Single-wise */
    //typecheckingTasks ::= Pair("singleWise", getAllSinglewiseConfigurations(features,fm, preferDisabledFeatures=true))
    //println("got " + typecheckingTasks.last._2.size + " singleWise configurations")
/**  Pairwise */
    typecheckingTasks ::= Pair("pairWise", getAllPairwiseConfigurations(features,fm, preferDisabledFeatures=true))
/** Coverage Configurations */
    /*
    typecheckingTasks ::=
      Pair("coverage", ConfigurationCoverage.naiveCoverageAny(family_ast, fm, family_env.asInstanceOf[ConfigurationCoverage.ASTEnv]).toList.map(
      {ex : FeatureExpr => { println("completing config"); completeConfiguration(ex, features, fm, preferDisabledFeatures = true ) } }
    ))
    println("got " + typecheckingTasks.last._2.size + " coverage configurations")
    */

/** No configurations */

/** Just one hardcoded config */
/*
    typecheckingTasks ::= Pair("hardcoded", getOneConfigWithFeatures(
      List(),
      List("CONFIG_X86_32","CONFIG_X86_64"),
      features,fm, true, true)
      )
*/
    println("start typechecking")
    // results (taskName, (NumConfigs, errors, timeSum))
    var configCheckingResults : List[ (String, (java.lang.Integer, java.lang.Integer, java.lang.Long) ) ] = List()
    val outFilePrefix:String = "../reports/" + opt.getFile.substring(0,opt.getFile.length-2)
    for ((taskDesc : String, configs : List[FeatureExpr]) <- typecheckingTasks) {
      var configurationsWithErrors = 0
      var current_config = 0
      var totalTimeProductChecking : Long = 0
      for (config <- configs) {
        current_config += 1
        println("checking configuration " + current_config + " of " + configs.size + " (" + opt.getFile + " , " + taskDesc + ")")
        val product = cf.deriveProd[TranslationUnit](family_ast, new Configuration(config, fm), family_env)
        val ts = new CTypeSystemFrontend(product.asInstanceOf[TranslationUnit], FeatureModel.empty)
        val startTime : Long = System.currentTimeMillis()
        val noErrors : Boolean = ts.checkAST
        val configTime : Long = System.currentTimeMillis()-startTime
        totalTimeProductChecking += configTime
        if (!noErrors) {
        //if (true) {
          // log product with error
          configurationsWithErrors += 1
          var file :File = new File(outFilePrefix + "_" + taskDesc + "_errors" + current_config + ".txt")
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
          file = new File(outFilePrefix + "_" + taskDesc + "_" + current_config +  "_config.txt")
          fw = new FileWriter(file)
          fw.write(config.toTextExpr.replace("&&", "&&\n"))
          fw.close()
          // write ast to file
          file = new File(outFilePrefix + "_" + taskDesc + "_" + current_config + "_ast.txt")
          fw = new FileWriter(file)
          fw.write(product.toString)
          fw.close()
        }
      }
      configCheckingResults ::= (taskDesc, (configs.size, configurationsWithErrors, totalTimeProductChecking))
    }
    // family base checking
    println("family-based type checking:")
    val startTime : Long = System.currentTimeMillis()
    val ts = new CTypeSystemFrontend(family_ast.asInstanceOf[TranslationUnit], fm_ts)
    val noErrors : Boolean = ts.checkAST
    val familyTime : Long = System.currentTimeMillis()-startTime

    var file:File = new File(outFilePrefix + "_report.txt")
    file.getParentFile.mkdirs()
    fw = new FileWriter(file)
    fw.write("File : " + opt.getFile + "\n")
    fw.write("Features : " + features.size + "\n")

    for ((taskDesc,(numConfigs,errors,time)) <- configCheckingResults) {
      fw.write("\n -- Task: " + taskDesc + "\n")
      fw.write("(" + taskDesc + ")Processed configurations: " + numConfigs + "\n")
      fw.write("(" + taskDesc + ")Configurations with errors: " + errors + "\n")
      fw.write("(" + taskDesc + ")TimeSum Products: " + time + " ms\n")
      fw.write("\n")
    }

    fw.write("Errors in family check: " + (if (noErrors)"No"else"Yes") + "\n")
    fw.write("Time Family:      " + familyTime + " ms\n")
    fw.close()

  }
  def getOneConfigWithFeatures(trueFeatures:List[String], falseFeatures:List[String],
                                  allFeatures : List[DefinedExpr], fm:FeatureModel, preferDisabledFeatures : Boolean = true, fixConfig : Boolean = true) : List[FeatureExpr] = {
    var partConfig : FeatureExpr = FeatureExpr.base
    var remainingFeatures :List[DefinedExpr] = allFeatures
    for (fName : String <-trueFeatures) {
      val fIndex :Int= remainingFeatures.indexWhere({(f:DefinedExpr) => f.feature.trim.equals(fName)})
      if (fIndex != -1) { //-1 := no feature found
        partConfig = partConfig.and(remainingFeatures.apply(fIndex))
        // I know this is horrible. But it will be used only for debugging
      } else {
        //throw new IllegalArgumentException("Feature not found: " + fName)
        println("Feature not found: " + fName)
      }
      remainingFeatures = remainingFeatures.slice(0,fIndex) ++ remainingFeatures.slice(fIndex+1,remainingFeatures.length+1)
    }
    for (fName : String <-falseFeatures) {
      val fIndex :Int= remainingFeatures.indexWhere({(f:DefinedExpr) => f.feature.trim.equals(fName)})
      if (fIndex != -1) { //-1 := no feature found
        partConfig = partConfig.andNot(remainingFeatures.apply(fIndex))
        // I know this is horrible. But it will be used only for debugging
      } else {
        //throw new IllegalArgumentException("Feature not found: " + fName)
        println("Feature not found: " + fName)
      }
      remainingFeatures = remainingFeatures.slice(0,fIndex) ++ remainingFeatures.slice(fIndex+1,remainingFeatures.length+1)
    }
    if (partConfig.isSatisfiable(fm)) {
      if (fixConfig) {
        val completeConfig =  completeConfiguration(partConfig,remainingFeatures,fm,preferDisabledFeatures)
        if (completeConfig == null) {
          throw new IllegalArgumentException("PartialConfig has no satisfiable extension!")
        } else {
          return List(completeConfig)
        }
      } else {
        return List(partConfig)
      }
    } else {
      throw new IllegalArgumentException("PartialConfig \"" + partConfig.toTextExpr + "\" is not satisfiable!")
    }
  }


  def getAllSinglewiseConfigurations(features : List[DefinedExpr], fm:FeatureModel, preferDisabledFeatures : Boolean = true) : List[FeatureExpr] = {
    println("generating single-wise configurations")
    var pwConfigs : List[FeatureExpr] = List()
    var handledCombinations : Set[FeatureExpr] = Set()
    for (f1 <- features) {
      if (! (handledCombinations.contains(f1))) {
        // this pair was not considered yet
        var conf = FeatureExpr.base.and(f1)
        if (conf.isSatisfiable(fm)) {
          // this pair is satisfiable
          // make config complete by choosing the other featuresval remainingFeatures = features.filterNot({(fe : FeatureExpr) => fe.equals(f1) || fe.equals(f2)})
          val remainingFeatures = features.filterNot({(fe : FeatureExpr) => fe.equals(f1)})
          val completeConfig = completeConfiguration(conf,remainingFeatures,fm, preferDisabledFeatures)
          if (completeConfig != null) {
            pwConfigs ::= completeConfig
          } else {
            println("no satisfiable configuration for features " + f1)
          }
        } else {
          println("no satisfiable configuration for feature " + f1)
        }
        handledCombinations += f1
      }
    }
    return pwConfigs
  }

  def getAllPairwiseConfigurations(features : List[DefinedExpr], fm:FeatureModel, preferDisabledFeatures : Boolean = true) : List[FeatureExpr] = {
    println("generating pair-wise configurations")
    var pwConfigs : List[FeatureExpr] = List()
    var handledCombinations : Set[Pair[FeatureExpr,FeatureExpr]] = Set()
    for (f1 <- features)
      for (f2 <- features) {
        if (! (handledCombinations.contains(Pair(f2,f1)) || handledCombinations.contains(Pair(f1,f2)))) {
          // this pair was not considered yet
          var conf = FeatureExpr.base.and(f1).and(f2)
          if (conf.isSatisfiable(fm)) {
            // this pair is satisfiable
            // make config complete by choosing the other features
            val remainingFeatures = features.filterNot({(fe : FeatureExpr) => fe.equals(f1) || fe.equals(f2)})
            val completeConfig = completeConfiguration(conf,remainingFeatures,fm, preferDisabledFeatures)
            if (completeConfig != null) {
              pwConfigs ::= completeConfig
            } else {
              println("no satisfiable configuration for features " + f1 + " and " +f2)
            }
          } else {
            println("no satisfiable configuration for features " + f1 + " and " +f2)
          }
          handledCombinations += Pair(f1,f2)
        }
      }
    return pwConfigs
  }

  def getAllTriplewiseConfigurations(features : List[DefinedExpr], fm:FeatureModel) : List[FeatureExpr] = {
    println("generating triple-wise configurations")
    var pwConfigs : List[FeatureExpr] = List()
    var handledCombinations : Set[Set[FeatureExpr]] = Set()
    for (f1 <- features)
    for (f2 <- features)
    for (f3 <- features) {
      if (! (handledCombinations.contains(Set(f1,f2,f3)))) {
        // this pair was not considered yet
        var conf = FeatureExpr.base.and(f1).and(f2).and(f3)
        if (conf.isSatisfiable(fm)) {
          // this pair is satisfiable
          // make config complete by choosing the other features
          val remainingFeatures = features.filterNot({(fe : FeatureExpr) => fe.equals(f1) || fe.equals(f2) || fe.equals(f3)})
          val completeConfig = completeConfiguration(conf,remainingFeatures,fm)
          if (completeConfig != null) {
            pwConfigs ::= completeConfig
          } else {
            println("no satisfiable configuration for features " + f1 + " and " + f2 + " and " + f3)
          }
        } else {
          println("no satisfiable configuration for features " + f1 + " and " + f2 + " and " + f3)
        }
        handledCombinations += Set(f1,f2,f3)
      }
    }
    return pwConfigs
  }

  /**
   * Completes a partial configuration so that no variability remains.
   * Features are set to false if possible.
   * If no satisfiable configuration is found then null is returned.
   * @param partialConfig
   * @param remainingFeatures
   * @param fm
   */
  def completeConfiguration(partialConfig : FeatureExpr, remainingFeatures:List[DefinedExpr], fm:FeatureModel, preferDisabledFeatures : Boolean = true) : FeatureExpr = {
    var config : FeatureExpr = partialConfig
    val fIter = remainingFeatures.iterator
    var partConfigFeasible : Boolean = true
    while (partConfigFeasible && fIter.hasNext) {
      val fx :DefinedExpr = fIter.next()
      if (preferDisabledFeatures) {
        // try to set other variables to false first
        var tmp : FeatureExpr = config.andNot(fx)
        if (tmp.isSatisfiable(fm)) {
          config = tmp
        } else {
          tmp = config.and(fx)
          if (tmp.isSatisfiable(fm)) {
            config = tmp
          } else {
            // this configuration cannot be satisfied any more
            return null
            partConfigFeasible=false
          }
        }
      } else {
        // try to set other variables to true first
        var tmp : FeatureExpr = config.and(fx)
        if (tmp.isSatisfiable(fm)) {
          config = tmp
        } else {
          tmp = config.andNot(fx)
          if (tmp.isSatisfiable(fm)) {
            config = tmp
          } else {
            // this configuration cannot be satisfied any more
            return null
            partConfigFeasible=false
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
  def getAllFeatures(root: Product) : List[DefinedExpr] = {
    // sort to eliminate any non-determinism caused by the set
    val featuresSorted = getAllFeaturesRec(root).toList.sortWith({
      (x,y) => x.feature.compare(y.toTextExpr) > 0
    });
    println ("found " + featuresSorted.size + " features")
    return featuresSorted;
  }

  private def getAllFeaturesRec(root: Any) : Set[DefinedExpr] = {
    root match {
      case x: Opt[_] => x.feature.collectDistinctFeaturesInclMacros.toSet ++ getAllFeaturesRec(x.entry)
      case x: Choice[_] => x.feature.collectDistinctFeaturesInclMacros.toSet ++ getAllFeaturesRec(x.thenBranch) ++ getAllFeaturesRec(x.elseBranch)
      case l: List[_] => {
        var ret : Set[DefinedExpr] = Set();
        for (x <- l) {ret = ret ++ getAllFeaturesRec(x);}
        ret
      }
      case x: Product => {
        var ret : Set[DefinedExpr] = Set();
        for (y <- x.productIterator.toList) {ret = ret ++ getAllFeaturesRec(y);}
        ret
      }
      case o => {
        Set()
      }
    }
  }

  //Fixme: still handling only Opt nodes, ignoring Choice nodes
  /**
   * This method generates complete configurations for a list of Opt[] nodes.
   * No variability is left in these configurations.
   */
  def getAllProducts(features : List[DefinedExpr], fm: FeatureModel, env: ConfigurationCoverage.ASTEnv) = {
    val prodLimit : Int = 30;
    var limitReached : Boolean = false
    var R: List[FeatureExpr] = List()   // found configurations
    R::=FeatureExpr.True
    var B: Set[Opt[_]] = Set()  // handled blocks
    var f : Set[FeatureExpr] = Set()  // handled features
    println("making all configurations (limited to " + prodLimit + " configurations")

    val featuresSorted = features.toArray.sortWith({
      (x,y) => x.toTextExpr.compare(y.toTextExpr) > 0
    });
    for (fexpb <- featuresSorted) {
      if (f.contains(fexpb)) {
      } else
       if (!f.contains(fexpb)) {
        f += fexpb
        var tmpR: List[FeatureExpr] = R
        R = List()
        for (partConfig <- tmpR) {
          if (R.size < prodLimit) {
            val confT = partConfig.and(fexpb)
            val okT = confT.isSatisfiable(fm)
            if (okT) R::=confT
            val confF = partConfig.and(fexpb.not())
            val okF = confF.isSatisfiable(fm)
            if (okF) R::=confF
          } else {
            limitReached=true
          }
        }
      }
    }
    println("configurations ready")
    if (limitReached)
      println("Product Limit of " + prodLimit + " was reached!")
    R
  }
}
