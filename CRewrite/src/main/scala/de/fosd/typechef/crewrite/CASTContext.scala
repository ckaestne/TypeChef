package de.fosd.typechef.crewrite

import java.util.IdentityHashMap
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.c.{TranslationUnit, AST}
import de.fosd.typechef.conditional.Opt

trait CASTEnv {

  type ASTContext = (FeatureExpr, Any, Any, Any, List[Any])

  object EmptyASTEnv extends ASTEnv (new IdentityHashMap[Any, ASTContext]())

  // store context of an AST entry
  // e: AST => (feature-expr: FeatureExpr, parent: AST, prev: AST, next: AST, children: List[AST])
  protected class ASTEnv (val astc: IdentityHashMap[Any, ASTContext]) {
    def add(elem: Any, newelemc: ASTContext) = {
      var curastc = astc
      var curelemc = curastc.get(elem)
      if (curelemc == null) { curelemc = (null, null, null, null, null)}

      // feature-expr; parent; prev; next; children
      if (curelemc._1 != newelemc._1 && newelemc._1 != null) { curelemc = curelemc.copy(_1 = newelemc._1)}
      if (curelemc._2 != newelemc._2 && newelemc._2 != null) { curelemc = curelemc.copy(_2 = newelemc._2)}
      if (curelemc._3 != newelemc._3 && newelemc._3 != null) { curelemc = curelemc.copy(_3 = newelemc._3)}
      if (curelemc._4 != newelemc._4 && newelemc._4 != null) { curelemc = curelemc.copy(_4 = newelemc._4)}
      if (curelemc._5 != newelemc._5 && newelemc._5 != null) { curelemc = curelemc.copy(_5 = newelemc._5)}

      curastc.put(elem, curelemc)
      new ASTEnv(curastc)
    }
  }

}