package de.fosd.typechef.crewrite

import de.fosd.typechef.conditional.{Choice, Opt}
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}

// store context of an AST entry
// e: AST => (lfexp: Set[FeatureExpr] parent: AST, prev: AST, next: AST, children: List[AST])
class ASTEnv(private var astc: java.util.IdentityHashMap[Any, (Set[FeatureExpr], Product, Product, Product, List[Any])]) {

    type ASTContext = (Set[FeatureExpr], Product, Product, Product, List[Any])

    override def toString = {
        var res = ""
        for (i <- astc.values().toArray.toList) {
            res += i
            res += "\n"
        }
        res
    }

    def containsASTElem(elem: Any) = astc.containsKey(elem)

    def get(elem: Any): ASTContext = astc.get(elem)

    def featureSet(elem: Any) = astc.get(elem)._1

    def featureExpr(elem: Any) = featureSet(elem).foldLeft(FeatureExprFactory.True)(_ and _)

    def parent(elem: Any) = astc.get(elem)._2

    def previous(elem: Any) = astc.get(elem)._3

    def next(elem: Any) = astc.get(elem)._4

    def children(elem: Any) = astc.get(elem)._5

    def keys() = astc.keySet().toArray

    def add(elem: Any, newelemc: ASTContext) = {
        var curelemc: ASTContext = null
        if (astc.containsKey(elem)) curelemc = astc.get(elem)
        else curelemc = (null, null, null, null, null)

        // lfexp; parent; prev; next; children
        if (curelemc._1 != newelemc._1 && newelemc._1 != null) {
            curelemc = curelemc.copy(_1 = newelemc._1)
        }
        if (curelemc._2 != newelemc._2 && newelemc._2 != null) {
            curelemc = curelemc.copy(_2 = newelemc._2)
        }
        if (curelemc._3 != newelemc._3 && newelemc._3 != null) {
            curelemc = curelemc.copy(_3 = newelemc._3)
        }
        if (curelemc._4 != newelemc._4 && newelemc._4 != null) {
            curelemc = curelemc.copy(_4 = newelemc._4)
        }
        if (curelemc._5 != newelemc._5 && newelemc._5 != null) {
            curelemc = curelemc.copy(_5 = newelemc._5)
        }

        astc.put(elem, curelemc)
        this
    }

    def isKnown(elem: Any): Boolean = astc.containsKey(elem)
}

object CASTEnv {
    // create ast-neighborhood context for a given translation-unit
    def createASTEnv(a: Product, fexpset: Set[FeatureExpr] = Set(FeatureExprFactory.True)): ASTEnv = {
        assert(a != null, "ast elem is null!")
        handleASTElem(a, null, fexpset, new ASTEnv(new java.util.IdentityHashMap[Any, (Set[FeatureExpr], Product, Product, Product, List[Any])]()))
    }

    // handle single ast elements
    // handling is generic because we can use the product-iterator interface of case classes, which makes
    // neighborhood settings is straight forward
    private def handleASTElem[T, U <: Product](e: T, parent: U, fexpset: Set[FeatureExpr], env: ASTEnv): ASTEnv = {
        e match {
            case l: List[_] => handleOptList(l.asInstanceOf[List[Opt[_]]], parent, fexpset, env)
            case Some(o) => handleASTElem(o, parent, fexpset, env)
            case c@Choice(feature, thenBranch, elseBranch) => {
                var curenv = env.add(c, (fexpset, parent, null, null, c.productIterator.toList))
                curenv = handleASTElem(thenBranch, c, fexpset + feature, curenv)
                curenv = handleASTElem(elseBranch, c, fexpset + (feature.not()), curenv)
                curenv
            }
            case x: Product => {
                var curenv = env.add(e, (fexpset, parent, null, null, x.productIterator.toList))
                for (elem <- x.productIterator.toList) {
                    curenv = handleASTElem(elem, x, fexpset, curenv)
                }
                curenv
            }
            case _ => env
        }
    }

    // handle list of Opt nodes
    // sets prev-next connections for elements and recursively calls handleASTElems
    private def handleOptList[T <: Product](l: List[Opt[_]], parent: T, fexpset: Set[FeatureExpr], env: ASTEnv): ASTEnv = {
        var curenv = env

        // set prev and next and children
        for (e <- createPrevElemNextTuples(l)) {
            e match {
                case (prev, Some(elem), next) => {
                    curenv = curenv.add(elem, (fexpset, parent, prev.getOrElse(null), next.getOrElse(null), elem.asInstanceOf[Product].productIterator.toList))
                }
                case _ =>
            }
        }

        // recursive call
        for (o@Opt(f, e) <- l) {
            curenv = handleASTElem(e, o, fexpset + f, curenv)
        }
        curenv
    }

    // since we do not have an neutral element that does not have any effect on ast
    // we use null and Any to represent values of no reference
    private def createPrevElemNextTuples[T](l: List[T]): List[(Option[T], Option[T], Option[T])] = {
        val nl = l.map(Some(_))
        val p = None :: None :: nl
        val e = (None :: Nil) ++ (nl ++ (None :: Nil))
        val n = nl ++ (None :: None :: Nil)

        (p, e, n).zipped.toList
    }
}