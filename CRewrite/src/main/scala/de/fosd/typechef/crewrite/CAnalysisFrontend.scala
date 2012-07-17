
package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr._
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.conditional.{Opt, Choice}
import de.fosd.typechef.parser.c.{PrettyPrinter, TranslationUnit, FunctionDef, AST}

class CAnalysisFrontend(tunit: AST, fm: FeatureModel = FeatureExprFactory.default.featureModelFactory.empty) extends ConditionalNavigation with ConditionalControlFlow with IOUtilities with Liveness with EnforceTreeHelper {

  // derive a specific product from a given configuration
  def deriveProductFromConfiguration[T <: Product](a: T, c: Configuration, env: ASTEnv): T = {
    // manytd is crucial here; consider the following example
    // Product1( c1, c2, c3, c4, c5)
    // all changes the elements top down, so the parent is changed before the children and this
    // way the lookup env.featureExpr(x) will not fail. Using topdown or everywherebu changes the children and so also the
    // parent before the parent is processed so we get a NullPointerExceptions calling env.featureExpr(x). Reason is
    // changed children lead to changed parent and a new hashcode so a call to env fails.
    val pconfig = manytd(rule {
      case Choice(feature, thenBranch, elseBranch) => if (c.config implies (if (env.containsASTElem(thenBranch)) env.featureExpr(thenBranch) else FeatureExprFactory.True) isTautology()) thenBranch else elseBranch
      case l: List[Opt[_]] => {
        var res: List[Opt[_]] = List()
        // use l.reverse here to omit later reverse on res or use += or ++= in the thenBranch
        for (o <- l.reverse)
          if (o.feature == FeatureExprFactory.True)
            res ::= o
          else if (c.config implies (if (env.containsASTElem(o)) env.featureExpr(o) else FeatureExprFactory.True) isTautology()) {
            res ::= o.copy(feature = FeatureExprFactory.True)
          }
        res
      }
      // we need ast here because otherwise we have old and new elements in the resulting product
      // and this might pollute our caches later
      case a: AST => a.clone()
    })

    val x = pconfig(a).get.asInstanceOf[T]
    appendToFile("output.c", PrettyPrinter.print(x.asInstanceOf[AST]))
    assert(isVariable(x) == false, "product still contains variability")
    x
  }

  def checkCfG(fileName: String) {

    val env = CASTEnv.createASTEnv(tunit)
    val fdefs = filterAllASTElems[FunctionDef](tunit)

    fdefs.map(intraCfGFunctionDef(_, env))
  }

  private def intraCfGFunctionDef(f: FunctionDef, env: ASTEnv) = {
    val s = getAllSucc(f, env)
    val p = getAllPred(f, env)

    val errors = compareSuccWithPred(s, p, env)
    CCFGErrorOutput.printCCFGErrors(s, p, errors, env)

    errors.size > 0
  }
}
