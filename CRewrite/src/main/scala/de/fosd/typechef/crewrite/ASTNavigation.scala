package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.AST
import de.fosd.typechef.conditional._

// simplified navigation support
// reimplements basic navigation between AST nodes not affected by Opt and Choice nodes
// see old version: https://github.com/ckaestne/TypeChef/blob/ConditionalControlFlow/CParser/src/main/scala/de/fosd/typechef/parser/c/ASTNavigation.scala
trait ASTNavigation extends CASTEnv {
  def parentAST(e: Any, env: ASTEnv): AST = {
    val eparent = env.parent(e)
    eparent match {
      case o: Opt[_] => parentAST(o, env)
      case c: Conditional[_] => parentAST(c, env)
      case a: AST => a
      case _ => null
    }
  }

  def prevAST(e: Any, env: ASTEnv): AST = {
    val eprev = env.previous(e)
    eprev match {
      case c: Choice[_] => lastChoice(c)
      case o: One[_] => o.value.asInstanceOf[AST]
      case a: AST => a
      case Opt(_, v: Choice[_]) => lastChoice(v)
      case Opt(_, v: One[_]) => v.value.asInstanceOf[AST]
      case Opt(_, v: AST) => v
      case null => {
        val eparent = env.get(e)._2
        eparent match {
          case o: Opt[_] => prevAST(o, env)
          case c: Choice[_] => prevAST(c, env)
          case c: One[_] => prevAST(c, env)
          case _ => null
        }
      }
    }
  }

  def nextAST(e: Any, env: ASTEnv): AST = {
    val enext = env.next(e)
    enext match {
      case c: Choice[_] => firstChoice(c)
      case o: One[_] => o.value.asInstanceOf[AST]
      case a: AST => a
      case Opt(_, v: Choice[_]) => firstChoice(v)
      case Opt(_, v: One[_]) => v.value.asInstanceOf[AST]
      case Opt(_, v: AST) => v
      case null => {
        val eparent = env.get(e)._2
        eparent match {
          case o: Opt[_] => nextAST(o, env)
          case c: Choice[_] => nextAST(c, env)
          case c: One[_] => nextAST(c, env)
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

  def childAST(e: Any): AST = {
    e match {
      case Opt(_, v: AST) => v
      case Opt(_, v: One[_]) => v.value.asInstanceOf[AST]
      case Opt(_, v: Choice[_]) => firstChoice(v)
      case x: One[_] => x.value.asInstanceOf[AST]
      case a: AST => a
      case Some(a) => childAST(a)
      case _ => null
    }
  }

  private def lastChoice(x: Choice[_]): AST = {
    x.elseBranch match {
      case c: Choice[_] => lastChoice(c)
      case One(c) => c.asInstanceOf[AST]
    }
  }

  private def firstChoice(x: Choice[_]): AST = {
    x.thenBranch match {
      case c: Choice[_] => firstChoice(c)
      case One(c) => c.asInstanceOf[AST]
    }
  }
}