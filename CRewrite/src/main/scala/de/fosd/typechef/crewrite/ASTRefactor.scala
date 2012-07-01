package de.fosd.typechef.crewrite

import de.fosd.typechef.typesystem.{CTypeSystem, CDefUse}
import de.fosd.typechef.parser.c._
import java.util
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.parser.c.Id


class ASTRefactor extends ConditionalNavigation with ASTNavigation with CDefUse with CTypeSystem {

  def renameFunction(ast: AST, defUSE: util.IdentityHashMap[Id, List[Id]], newID: String, oldID: Id): AST = {
    // TODO Rename in Header File
    val id = oldID.copy(name = newID)
    var result = ast

    /**
     * Replace original id first
     */
    result = replaceIDinAST(result, oldID, id)

    /**
     * Replace all uses
     */
    for (idUse <- defUSE.get(oldID)) {
      result = replaceIDinAST(result, idUse, idUse.copy(name = newID))
    }
    println("Refactored AST " + result)
    println(PrettyPrinter.print(result))
    return result

  }

  private def replaceIDinAST[T <: Product](t: T, e: Id, n: Id): T = {
    val r = manytd(rule {
      case i: Id => if (i.eq(e)) n else i
    })
    r(t).get.asInstanceOf[T]
  }

}