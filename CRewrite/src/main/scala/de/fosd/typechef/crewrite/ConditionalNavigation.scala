package de.fosd.typechef.crewrite

import de.fosd.typechef.conditional._
import de.fosd.typechef.parser.c.AST
import de.fosd.typechef.featureexpr.FeatureExpr

import org.kiama.rewriting.Rewriter._

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
    var res = false
    val variable = manytd(query {
      case Opt(f, _) => if (f != FeatureExpr.dead && f != FeatureExpr.base) res = true
      case x => res = res
    })

    variable(e)
    res
  }

  def filterAllOptElems(e: Product): List[Opt[_]] = {
    var res: List[Opt[_]] = List()
    val filter = manytd(query {
      case o: Opt[_] => res ::= o
    })

    filter(e)
    res
  }

  // return all Opt and One elements
  def filterAllVariableElems(e: Product): List[Product] = {
    var res: List[Product] = List()
    val filter = manytd(query {
      case o: Opt[_] => res ::= o
      case o: One[_] => res ::= o
    })

    filter(e)
    res
  }
}
