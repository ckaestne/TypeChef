package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c.{Id, AST}
import java.util


class Refactor extends ASTNavigation with ConditionalNavigation {

  def canRenameFunction(defUSE: util.IdentityHashMap[Id, List[Id]], selection: List[AST]): Boolean = {
    return !(getPossibleRenameFunctionIDs(defUSE, selection).isEmpty);
  }

  def getPossibleRenameFunctionIDs(defUSE: util.IdentityHashMap[Id, List[Id]], selection: List[AST]): List[Id] = {
    val filteredIDs = filterIds(selection)
    var result = List[Id]()
    // TODO In Nice!
    for (entry <- filteredIDs) {
      for (id <- defUSE.keySet().toArray)
        for (usedId <- defUSE.get(id)) {
          if (entry.eq(usedId) || id.eq(entry)) {
            result = id.asInstanceOf[Id] :: result
          }
        }
    }
    println("possilble ids " + result)
    return result.distinct
  }

  private def filterIds(selection: List[AST]): List[Id] = {
    var filteredIDs = List[Id]()
    for (entry <- selection) {
      filteredIDs = filteredIDs ::: filterAllASTElems[Id](entry)
    }
    return filteredIDs.distinct
  }
}
