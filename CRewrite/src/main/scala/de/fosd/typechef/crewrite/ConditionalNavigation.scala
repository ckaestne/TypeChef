package de.fosd.typechef.crewrite

import de.fosd.typechef.conditional._
import de.fosd.typechef.parser.c.AST

trait ConditionalNavigation extends CASTEnv {
  def parentOpt(e: Any, env: ASTEnv): Opt[_] = {
    val eparent = env.get(e)._2
    eparent match {
      case o: Opt[_] => o
      case c: Conditional[_] => Conditional.toOptList(c).head
      case a: AST => parentOpt(a, env)
      case _ => null
    }
  }

  def prevOpt(e: Opt[_], env: ASTEnv): Opt[_] = {
    val eprev = env.get(e)._3
    eprev match {
      case o: Opt[_] => o
      case _ => null
    }
  }

  def nextOpt(e: Opt[_], env: ASTEnv): Opt[_] = {
    val enext = env.get(e)._4
    enext match {
      case o: Opt[_] => o
      case _ => null
    }
  }

  def isVariable(e: Any, env: ASTEnv): Boolean = {
    val efexp = env.get(e)._1.reduce(_ and _)
    efexp.not.isContradiction()
  }
}