package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr.FeatureExpr
import java.util.IdentityHashMap
import de.fosd.typechef.conditional.{Choice, Opt}

trait CASTEnv {

  type ASTContext = (List[FeatureExpr], Any, Any, Any, List[Any])

  // store context of an AST entry
  // e: AST => (lfexp: List[FeatureExpr] parent: AST, prev: AST, next: AST, children: List[AST])
  class ASTEnv (val astc: IdentityHashMap[Any, ASTContext]) {

    def lfeature(elem: Any) = {
      val element =astc.get(elem)
      if (element != null) {
          element._1
      } else if (elem.isInstanceOf[Object]) {
          throw new IllegalArgumentException("Key not found in environment: " + elem + " : "
            + elem.asInstanceOf[Object].getClass)
      } else {
          throw new IllegalArgumentException("Key not found in environment: " + elem)
      }
    }
    def featureExpr(elem: Any) = {
      assert (!elem.isInstanceOf[FeatureExpr], "Should not ask for a FeatureExpr for a FeatureExpr");
      lfeature(elem).foldLeft(FeatureExpr.base)(_ and _)
    }
    def containsASTElem(elem: Any) = astc.containsKey(elem)
    def parent(elem: Any) = astc.get(elem)._2
    def previous(elem: Any) = astc.get(elem)._3
    def next(elem: Any) = astc.get(elem)._4
    def children(elem: Any) = astc.get(elem)._5
    def keys() = astc.keySet().toArray
    def get(elem: Any) = astc.get(elem)
    def elems() = astc.keySet().toArray.toList.map(System.identityHashCode(_)).sortWith((x, y) => x > y)

    def add(elem: Any, newelemc: ASTContext) = {
      var curelemc: ASTContext = null
      if (astc.containsKey(elem)) curelemc = astc.get(elem)
      else curelemc = (null, null, null, null, null)

      // lfexp; parent; prev; next; children
      if (curelemc._1 != newelemc._1 && newelemc._1 != null) { curelemc = curelemc.copy(_1 = newelemc._1)}
      if (curelemc._2 != newelemc._2 && newelemc._2 != null) { curelemc = curelemc.copy(_2 = newelemc._2)}
      if (curelemc._3 != newelemc._3 && newelemc._3 != null) { curelemc = curelemc.copy(_3 = newelemc._3)}
      if (curelemc._4 != newelemc._4 && newelemc._4 != null) { curelemc = curelemc.copy(_4 = newelemc._4)}
      if (curelemc._5 != newelemc._5 && newelemc._5 != null) { curelemc = curelemc.copy(_5 = newelemc._5)}

      astc.put(elem, curelemc)
      this
    }
  }

  // create ast-neighborhood context for a given translation-unit
  def createASTEnv(a: Product, lfexp: List[FeatureExpr] = List(FeatureExpr.base)): ASTEnv = {
    assert(a != null, "ast elem is null!")
    handleASTElem(a, null, lfexp, new ASTEnv(new IdentityHashMap[Any, ASTContext]()))
  }

  // handle single ast elements
  // handling is generic because we can use the product-iterator interface of case classes, which makes
  // neighborhood settings is straight forward
  private def handleASTElem[T, U](e: T, parent: U, lfexp: List[FeatureExpr], env: ASTEnv): ASTEnv = {
    e match {
      case l:List[Opt[_]] => handleOptList(l, parent, lfexp, env)
      case Some(o) =>  {
        handleASTElem(o, parent, lfexp, env)
      }
      case x : Choice[_] => {
        val trueExp:List[FeatureExpr] = lfexp ++ List(x.feature)
        val falseExp:List[FeatureExpr] = lfexp ++ List(x.feature.not())
        var x1 = new ASTContext(trueExp, x, null, x.elseBranch, x.thenBranch.asInstanceOf[Product].productIterator.toList)
        var x2 = new ASTContext(falseExp, x, x.thenBranch, null, x.elseBranch.asInstanceOf[Product].productIterator.toList)
        var curenv = env.add(x.thenBranch,x1).add(x.elseBranch,x2)
        curenv = handleASTElem(x.thenBranch, x, trueExp, curenv)
        curenv = handleASTElem(x.elseBranch, x, falseExp, curenv)
        curenv
      }
      case x:Product => {
        var curenv = env.add(e, (lfexp, parent, null, null, x.productIterator.toList))

        for (elem <- x.productIterator.toList) {
          curenv = handleASTElem(elem, x, lfexp, curenv)
        }
        curenv
      }
      case x => {
        env
      }
    }
  }

  // handle list of Opt nodes
  // sets prev-next connections for elements and recursively calls handleASTElems
  private def handleOptList[T](l: List[Opt[_]], parent: T, lfexp: List[FeatureExpr], env: ASTEnv): ASTEnv = {
    var curenv = env

    // set prev and next and children
    for (e <- createPrevElemNextTuples(l)) {
      e match {
        case (prev, Some(elem), next) => {
          curenv = curenv.add(elem, (lfexp, parent, prev.getOrElse(null), next.getOrElse(null), elem.asInstanceOf[Product].productIterator.toList))
        }
        case _ => ;
      }
    }

    // recursive call
    for (o@Opt(f, e) <- l) {
      curenv = handleASTElem(e, o, f::lfexp, curenv)
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