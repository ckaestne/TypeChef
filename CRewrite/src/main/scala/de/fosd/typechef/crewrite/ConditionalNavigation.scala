package de.fosd.typechef.crewrite

import de.fosd.typechef.conditional._
import de.fosd.typechef.parser.c.AST
import de.fosd.typechef.featureexpr.FeatureExpr

trait ConditionalNavigation extends CASTEnv {
  def parentOpt(e: Any, env: ASTEnv): Opt[_] = {
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
  def isVariable(e: Any): Boolean = {
    e match {
      case o@Opt(FeatureExpr.base, _) => o.productIterator.toList.map(isVariable).exists(_ == true)
      case Opt(FeatureExpr.dead, _) => false
      case Opt(_, _) => true
      case l: List[_] => l.map(isVariable).exists(_ == true)
      case p: Product => p.productIterator.toList.map(isVariable).exists(_ == true)
      case _ => false
    }
  }

  def filterAllOptElems(e: Any): List[Opt[_]] = {
    e match {
      case x: Opt[_] => x :: x.productIterator.toList.flatMap(filterAllOptElems)
      case l: List[_] => l.flatMap(filterAllOptElems)
      case x: Product => x.productIterator.toList.flatMap(filterAllOptElems)
      case _ => List()
    }
  }
}
