package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.WithPosition
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.parser.c._

/**
 * preparation and checks for downstream tools
 * which require a tree structure
 *
 * we use the product interface of the elements here works both for
 * case classes Opt and AST elements, which derive product directly
 */
trait EnforceTreeHelper extends CASTEnv {
  private def assertTree(ast: Product, env: ASTEnv ) {
    for (c <- ast.productIterator) {
      val cparent = env.astc.get(c)._2
      assert(cparent == ast, "Child " + c + " points to different parent:\n  " + cparent + "\nshould be\n  " + ast)
      assertTree(c.asInstanceOf[Product], env)
    }
  }

//  private def ensureTree(ast: Attributable) {
//    for (c <- ast.children) {
//      c.parent = ast
//      ensureTree(c)
//    }
//  }

  /**
   * unfortunately cloning loses position information, so we have to reassign it
   */
  private def copyPositions(source: Product, target: Product) {
    assert(source.getClass == target.getClass, "cloned tree should match exactly the original, typewise")
    if (source.isInstanceOf[WithPosition])
      target.asInstanceOf[WithPosition].range = source.asInstanceOf[WithPosition].range

    //        assert(source.children.size==target.children.size,"cloned tree should match exactly the original")
    for (c <- source.productIterator.zip(target.productIterator)) {
      copyPositions(c._1.asInstanceOf[Product], c._2.asInstanceOf[Product])
    }
  }


  def prepareAST(ast: Product): TranslationUnit = {
    assert(ast != null)

    val clone = everywherebu(rule {
      case n: AST =>
        if (n.productIterator.size > 0) {
          //                    n.setChildConnections
          n
        } else
          n.clone()
    })
    val cast = clone(ast).get.asInstanceOf[TranslationUnit]
    //        ensureTree(cast)
    val env = createASTEnv(cast)
    assertTree(cast, env)
    copyPositions(ast, cast)
    cast
  }

}