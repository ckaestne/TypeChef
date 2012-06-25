package de.fosd.typechef.crewrite

import de.fosd.typechef.typesystem.{CTypeSystem, CDefUse}
import de.fosd.typechef.parser.c._
import java.util
import de.fosd.typechef.crewrite.CASTEnv._
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.parser.c.AtomicNamedDeclarator
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.parser.c.Initializer
import de.fosd.typechef.parser.c.FunctionDef
import de.fosd.typechef.parser.c.PostfixExpr
import de.fosd.typechef.parser.c.Id
import de.fosd.typechef.parser.c.InitDeclaratorI


trait ASTRefactor extends ConditionalNavigation with ASTNavigation with CDefUse with CTypeSystem {

  def renameFunction(ast: AST, defUSE: util.IdentityHashMap[Id, List[Id]], newID: String, oldID: Id): AST = {
    // TODO Check if possible
    return performFunctionRenaming(ast, newID, oldID)
  }

  private def getCorrectDelarator(declarator : AtomicNamedDeclarator, newID : Id, oldId :Id) : AtomicNamedDeclarator = {
      if(declarator.id.equals(oldId)) {
        return declarator.copy(id = newID)
      }
      return declarator
  }


  private def performFunctionRenaming(ast: AST, newID: String, oldID: Id) : AST = {
     val id = oldID.copy(name = newID)
     val env = createASTEnv(ast)
     var result = ast

    for (key <- env.keys) {
      if (key.equals(oldID)) {
         val parentOptValue = parentOpt(key.asInstanceOf[Product], env)
         val parentKey = env.parent(key).asInstanceOf[AST]
         val replace = replaceKeyInEntry(parentKey, id, oldID)
         val replaceOpt = replaceInOpt(parentOptValue, parentKey, replace, env)
         result = replaceOptinAST(result, parentOptValue, replaceOpt)
         println("Parent opt" + parentOptValue)
         println("ReplaceOPT: " + replaceOpt)
         //println(parent)
         //println(newOpt)
         //println(newOpt.equals(parent))
       }
     }
    println("result =" + result)
    return result
  }

  private def replaceKeyInEntry(entry: AST, newID: Id, oldID: Id) : AST = {
    entry match {
      case a: AtomicNamedDeclarator => {
        return a.copy(id = newID)
      }
      case p: PostfixExpr => {
        return p.copy(p = newID)
      }
      case _ => {
        return entry
      }
    }
  }

  private def replaceInOpt(opt : Opt[_], toReplace : AST, replace : AST, env : ASTEnv) : Opt[_] = {
     val parent = env.parent(toReplace)
     parent match {
      case o : Opt[_]  => {
        return o.copy(entry = replace)
      }
      case iDec : InitDeclaratorI => {
          replace match {
            case a : AtomicNamedDeclarator => {
              val newReplace = iDec.copy(declarator = replace.asInstanceOf[AtomicNamedDeclarator])
              return replaceInOpt(opt, parent.asInstanceOf[AST], newReplace, env)
            }
            case init : Initializer => {
              val newReplace = iDec.copy(i = Option[Initializer](replace.asInstanceOf[Initializer]))
              return replaceInOpt(opt, parent.asInstanceOf[AST], newReplace, env)
            }
            case _ => return opt
          }
      }
      case i2 : Initializer => {
        replace match {
          case p : PostfixExpr => {
            val newReplace = i2.copy(expr = replace.asInstanceOf[Expr])
            return replaceInOpt(opt, parent.asInstanceOf[AST], newReplace, env)
          }
        }
      }
      case f : FunctionDef => {
         replace match {
           case a : AtomicNamedDeclarator => {
             val newReplace = f.copy(declarator = replace.asInstanceOf[AtomicNamedDeclarator])
             return replaceInOpt(opt, parent.asInstanceOf[AST], newReplace, env)
           }
         }
     }
      case _ => return opt
    }
     return opt
  }

  private def replaceOptinAST[T <: Product](t: T, e: Opt[_], n: Opt[_]): T = {
    val r = manytd(rule {
      case l: List[Opt[_]] => l.flatMap({x => if (x.equals(e)) n::Nil else x::Nil})
    })
    r(t).get.asInstanceOf[T]
  }

}