package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.AST
import de.fosd.typechef.conditional._

// simplified navigation support
// reimplements basic navigation between AST nodes not affected by Opt and Choice nodes
// see old version: https://github.com/ckaestne/TypeChef/blob/ConditionalControlFlow/CParser/src/main/scala/de/fosd/typechef/parser/c/ASTNavigation.scala
trait ASTNavigation extends CASTEnv {
  def parentAST(e: Any, env: ASTEnv): AST = {
    val eparent = env.astc.get(e)._2
    eparent match {
      case o: Opt[_] => parentAST(o, env)
      case c: Conditional[_] => parentAST(c, env)
      case a: AST => a
      case _ => null
    }
  }

  def prevAST(e: Any, env: ASTEnv): AST = {
    val eprev = env.astc.get(e)._3
    eprev match {
      case c: Choice[AST] => lastChoice(c)
      case o: One[AST] => o.value
      case a: AST => a
      case Opt(_, v: Choice[AST]) => lastChoice(v)
      case Opt(_, v: One[AST]) => v.value
      case Opt(_, v: AST) => v
      case null => {
        val eparent = env.astc.get(e)._2
        eparent match {
          case o: Opt[_] => prevAST(o, env)
          case c: Choice[AST] => prevAST(c, env)
          case c: One[AST] => prevAST(c, env)
          case _ => null
        }
      }
    }
  }

  def nextAST(e: Any, env: ASTEnv): AST = {
    val enext = env.astc.get(e)._4
    enext match {
      case c: Choice[AST] => firstChoice(c)
      case o: One[AST] => o.value
      case a: AST => a
      case Opt(_, v: Choice[AST]) => firstChoice(v)
      case Opt(_, v: One[AST]) => v.value
      case Opt(_, v: AST) => v
      case null => {
        val eparent = env.astc.get(e)._2
        eparent match {
          case o: Opt[_] => nextAST(o, env)
          case c: Choice[AST] => nextAST(c, env)
          case c: One[AST] => nextAST(c, env)
          case _ => null
        }
      }
    }
  }

  def prevASTElems(e: Any, env: ASTEnv): List[AST] = {
    e match {
      case null => List()
      case s => prevASTElems(prevAST(s, env), env) ++ List(childAST(s))
    }
  }

  def nextASTElems(e: Any, env: ASTEnv): List[AST] = {
    e match {
      case null => List()
      case s => List(childAST(s)) ++ nextASTElems(nextAST(s, env), env)
    }
  }

  private def childAST(e: Any): AST = {
    e match {
      case Opt(_, v: AST) => v
      case Opt(_, v: One[AST]) => v.value
      case Opt(_, v: Choice[AST]) => firstChoice(v)
      case x: One[AST] => x.value
      case a: AST => a
    }
  }

  private def lastChoice[T <: AST](x: Choice[T]): T = {
    x.elseBranch match {
      case c: Choice[T] => lastChoice[T](c)
      case One(c) => c
    }
  }

  private def firstChoice[T <: AST](x: Choice[T]): T = {
    x.thenBranch match {
      case c: Choice[T] => firstChoice[T](c)
      case One(c) => c
    }
  }
}