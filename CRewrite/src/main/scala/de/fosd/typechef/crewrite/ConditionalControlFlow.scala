package de.fosd.typechef.crewrite


import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.c._

trait ConditionalControlFlow extends CASTEnv {

  def createASTEnv(tunit: TranslationUnit, featureModel: FeatureExpr = FeatureExpr.base): ASTEnv = {
    assert(tunit != null, "tunit is null!")
    addTranslationUnit(tunit, featureModel, EmptyASTEnv)
  }

  // create ast-neighborhood context for conditional control flow
  private def addTranslationUnit(tunit: TranslationUnit, featureExpr: FeatureExpr = FeatureExpr.base, initialEnv: ASTEnv): ASTEnv = {
    var env = initialEnv
    handleASTElems(tunit, null, featureExpr, env)
  }

  private def handleASTElems[T, U](e: T, parent: U, fexp: FeatureExpr, env: ASTEnv): ASTEnv = {
    e match {
      case l:List[Opt[AST]] => handleOptLists(l, parent, fexp, env)
      case x:AST => {
        var curenv = env.add(e, (fexp, e, null, null, x.productIterator.toList))
        for (elem <- x.productIterator.toList) {
          curenv = handleASTElems(elem, e, fexp, curenv)
        }
        curenv
      }
      case _ => env
    }
  }

  private def handleOptLists[T](l: List[Opt[T]], parent: T, fexp: FeatureExpr, env: ASTEnv): ASTEnv = {
    var curenv = env

    // set prev and next and children
    for (e <- createPrevElemNextTuples(l)) {
      e match {
        case (prev, Some(elem), next) => {
          curenv = curenv.add(elem, (null, null, prev.getOrElse(null), next.getOrElse(null), null))
        }
        case _ => ;
      }
    }

    // recursive call
    for (o@Opt(f, e) <- l) {
      curenv = handleASTElems(e, o, fexp and f, curenv)
    }
    curenv
  }

  // since we do not have an neutral element that does not have any effect on ast
  // we use null and Any to represent values of no reference
  private def createPrevElemNextTuples[T](l: List[T]): List[(Option[T],Option[T],Option[T])] = {
    val nl = l.map(Some(_))
    val p = None :: None :: nl
    val e = (None :: Nil) ++ (nl ++ (None :: Nil))
    val n = nl ++ (None :: None :: Nil)

    (p,e,n).zipped.toList
  }
}