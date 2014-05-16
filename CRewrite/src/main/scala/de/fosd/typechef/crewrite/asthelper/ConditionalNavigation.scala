package de.fosd.typechef.crewrite.asthelper

import de.fosd.typechef.conditional._

import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import de.fosd.typechef.parser.c.AST

trait ConditionalNavigation {
    def parentOpt(e: Product, env: ASTEnv): Opt[_] = {
        val eparent = env.parent(e)
        eparent match {
            case o: Opt[_] => o
            case c: Conditional[_] => Conditional.toOptList(c).head
            case a: AST => parentOpt(a, env)
            case _ => null
        }
    }

    def prevOpt(e: Opt[_], env: ASTEnv): Opt[_] = {
        val eprev = env.previous(e)
        eprev match {
            case o: Opt[_] => o
            case _ => null
        }
    }

    def nextOpt(e: Opt[_], env: ASTEnv): Opt[_] = {
        val enext = env.next(e)
        enext match {
            case o: Opt[_] => o
            case _ => null
        }
    }

    // check recursively for any nodes that have an annotation != True
    def isVariable(e: Product): Boolean = {
        manytd(query[Product]{
            case Opt(f, _) => if (f != FeatureExprFactory.False && f != FeatureExprFactory.True) return true
        })(e)

        false
    }

    def filterAllOptElems(e: Product): List[Opt[_]] = {
        def filterAllOptElemsHelper(a: Any): List[Opt[_]] = {
            a match {
                case o@Opt(_, entry) => List(o) ++ (if (entry.isInstanceOf[Product]) entry.asInstanceOf[Product].productIterator.toList.flatMap(filterAllOptElemsHelper)
                else List())
                case l: List[_] => l.flatMap(filterAllOptElemsHelper(_))
                case x: Product => x.productIterator.toList.flatMap(filterAllOptElemsHelper(_))
                case _ => List()
            }
        }
        filterAllOptElemsHelper(e)
    }

    def filterAllFeatureExpr(e: Product): List[FeatureExpr] = {
        def filterAllFeatureExprHelper(a: Any): List[FeatureExpr] = {
            a match {
                case Opt(feature, entry) => List(feature) ++ (if (entry.isInstanceOf[Product]) entry.asInstanceOf[Product].productIterator.toList.flatMap(filterAllFeatureExprHelper)
                else List())
                case Choice(feature, thenBranch, elseBranch) => List(feature, feature.not()) ++
                        thenBranch.asInstanceOf[Product].productIterator.toList.flatMap(filterAllFeatureExprHelper) ++
                        elseBranch.asInstanceOf[Product].productIterator.toList.flatMap(filterAllFeatureExprHelper)
                case l: List[_] => l.flatMap(filterAllFeatureExprHelper)
                case x: Product => x.productIterator.toList.flatMap(filterAllFeatureExprHelper)
                case _ => List()
            }
        }
        filterAllFeatureExprHelper(e)
    }

    // return all Opt and One elements
    def filterAllVariableElems(e: Product): List[Product] = {
        var res: List[Product] = List()
        manytd(query[Product] {
            case o: Opt[_] => res ::= o
            case o: One[_] => res ::= o
        })(e)

        res
    }
}
