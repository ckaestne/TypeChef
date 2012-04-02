package de.fosd.typechef

import conditional.Opt
import crewrite.{ConfigurationCoverage, CAnalysisFrontend}
import featureexpr.{FeatureExpr, Configuration, FeatureModel}
import parser.c.{TranslationUnit, AST}
import typesystem.CTypeSystemFrontend

/**
 * Created by IntelliJ IDEA.
 * User: rhein
 * Date: 4/2/12
 * Time: 3:45 PM
 * To change this template use File | Settings | File Templates.
 */

object ProductGeneration {
  def typecheckProducts(fm:FeatureModel, fm_ts : FeatureModel, ast :AST) {
    //println(PrettyPrinter.print(ast))
    val cf = new CAnalysisFrontend(ast.asInstanceOf[TranslationUnit], fm_ts)
    val family_ast = cf.prepareAST[TranslationUnit](ast.asInstanceOf[TranslationUnit])
    val family_env = cf.createASTEnv(family_ast)
    /*
    println("Check base Product")

    val base_ast = cf.prepareAST[TranslationUnit](
      cf.deriveProductFromConfiguration[TranslationUnit](family_ast.asInstanceOf[TranslationUnit], new Configuration(FeatureExpr.base, fm), family_env))
    val ts = new CTypeSystemFrontend(base_ast.asInstanceOf[TranslationUnit], fm_ts)

    ts.checkAST

    */

    val configs = getAllProductsForAllFeatures(family_ast, fm, family_env.asInstanceOf[ConfigurationCoverage.ASTEnv])
    var current_config = 1
    for (config <- configs) {
      println("checking configuration " + current_config + " of " + configs.size)
      //println("Config: " + config.toTextExpr)

      current_config += 1
      val product = cf.deriveProductFromConfiguration[TranslationUnit](family_ast, new Configuration(config, fm), family_env)
      val product_ast = cf.prepareAST[TranslationUnit](product)
      val ts = new CTypeSystemFrontend(product_ast.asInstanceOf[TranslationUnit], fm_ts)
      ts.checkAST
    }
  }
  def getAllProductsForAllFeatures(a: Any, fm: FeatureModel, env: ConfigurationCoverage.ASTEnv) = {
    val opts = ConfigurationCoverage.filterAllOptElems(a)
    getAllProducts(opts.toSet, fm, env)
  }

  def getAllProducts(in: Set[Opt[_]], fm: FeatureModel, env: ConfigurationCoverage.ASTEnv) = {
    // so far this method generates partial products that cover all #if blocks
    // i want all (complete) products!
    val prodLimit : Int = 50;
    var limitReached : Boolean = false

    var R: Set[FeatureExpr] = Set()   // found configurations
    R+=FeatureExpr.True

    var B: Set[Opt[_]] = Set()  // handled blocks
    //println("FeatureModel: ")
    //FeatureModel.exportFM2CNF(fm, "console")

    // iterate over all optional blocks
    for (b <- in) {
      // optional block b has not been handled before
      if (! B.contains(b)) {
        var fexpb = env.featureExpr(b)
        var tmpR: Set[FeatureExpr] = R
        R = Set()
        for (partConfig <- tmpR) {

          if (R.size < prodLimit) {
            val confT = partConfig.and(fexpb)
            val okT = confT.isSatisfiable(fm)
            if (okT) R+=confT
            val confF = partConfig.and(fexpb.not())
            val okF = confF.isSatisfiable(fm)
            if (okF) R+=confF
          } else {
            /*  val confT = partConfig.and(fexpb)
              val okT = confT.isSatisfiable(fm)
              if (okT) {
                R+=confT
              } else {
                val confF = partConfig.and(fexpb.not())
                val okF = confF.isSatisfiable(fm)
                if (okF) R+=confF
              }
            */
            limitReached=true
          }
        }
        B += b
      }
    }
    if (limitReached)
      println("Product Limit of " + prodLimit + " was reached!")
    assert(in.size == B.size, "configuration coverage missed the following optional blocks\n" +
      (in.diff(B).map(_.feature)) + "\n" + R
    )
    // print all configurations
    /*
    var i = 0;
    for (config <- R) {
      println("config" + i + ": " + config.toTextExpr)
      i=i+1;
    }
    */
    R
  }
}
