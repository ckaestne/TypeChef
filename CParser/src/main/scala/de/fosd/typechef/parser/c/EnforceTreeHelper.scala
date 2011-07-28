package de.fosd.typechef.parser.c

import org.kiama.attribution.Attributable
import de.fosd.typechef.parser.WithPosition
import org.kiama.rewriting.Rewriter._

/**
 * preparation and checks for downstream tools
 * which require a tree structure
 */
trait EnforceTreeHelper {
    private def assertTree(ast: Attributable) {
        for (c <- ast.children) {
            assert(c.parent == ast, "Child " + c + " points to different parent:\n  " + c.parent + "\nshould be\n  " + ast)
            assertTree(c)
        }
    }

    private def ensureTree(ast: Attributable) {
        for (c <- ast.children) {
            c.parent = ast
            ensureTree(c)
        }
    }

    /**
     * unfortunately cloning loses position information, so we have to reassign it
     */
    private def copyPositions(source: Attributable, target: Attributable) {
        assert(source.getClass == target.getClass, "cloned tree should match exactly the original, typewise")
        if (source.isInstanceOf[WithPosition])
            target.asInstanceOf[WithPosition].range = source.asInstanceOf[WithPosition].range

        //        assert(source.children.size==target.children.size,"cloned tree should match exactly the original")
        for (c <- source.children.zip(target.children)) {
            copyPositions(c._1, c._2)
        }
    }


    protected def prepareAST(ast: AST): TranslationUnit = {
        assert(ast != null)

        val clone = everywherebu(rule {
            case n: AST =>
                if (n.hasChildren) {
                    n.setChildConnections
                    n
                } else
                    n.clone()
        })
        val cast = clone(ast).get.asInstanceOf[TranslationUnit]
        //        ensureTree(cast)
        assertTree(cast)
        copyPositions(ast, cast)
        cast
    }

}