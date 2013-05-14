package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.WithPosition
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional.{Opt, One}


/**
 * preparation and checks for downstream tools
 * which require a tree structure
 *
 * we use the product interface of the elements here works both for
 * case classes Opt and AST elements, which derive product directly
 */
trait EnforceTreeHelper {

    /**
     * unfortunately cloning loses position information, so we have to reassign it
     */
    def copyPositions(source: Product, target: Product) {
        assert(source.getClass == target.getClass, "cloned tree should match exactly the original, typewise")
        if (source.isInstanceOf[WithPosition])
            target.asInstanceOf[WithPosition].range = source.asInstanceOf[WithPosition].range

        //        assert(source.children.size==target.children.size,"cloned tree should match exactly the original")
        for ((c1, c2) <- source.productIterator.zip(target.productIterator)) {
            if (!c1.isInstanceOf[Product] || !c2.isInstanceOf[Product])
                copyPositions(c1.asInstanceOf[Product], c2.asInstanceOf[Product])
        }
    }


    // creates an AST without shared objects
    // the parser reuses parsed elements in different subtrees of the AST
    // this method makes sure we create an AST with unique elements
    def prepareAST[T <: Product](ast: T): T = {
        assert(ast != null)

        val clone = everywherebu(rule {
            // function to add a break expression to infinite loops: "for (;;) {}" and "for (;;) ;"
            // reason is: for (;;) is the only infinite loop without explicit break statement,
            // so if we omit CompoundStatement in succ pred determination, we need an expression
            // so that succ(e) -> e and pred(e) is e
            // we add a Constant("1") at the break
            case ForStatement(None, None, None, One(CompoundStatement(List()))) =>
                ForStatement(None, Some(Constant("1")), None, One(CompoundStatement(List())))
            case n: AST => n.clone()
        })
        val cast = clone(ast).get.asInstanceOf[T]
        copyPositions(ast, cast)
        cast
    }

    // cparser creates dead ast nodes that causes problems in the control flow analysis (grouping of ast nodes etc.)
    // the function removes dead nodes from the ast
    // see issue: https://github.com/ckaestne/TypeChef/issues/4
    def removeDeadNodes[T <: Product](ast: T, env: ASTEnv): T = {
        assert(ast != null)

        val removedead = manytd(rule {
            case l: List[Opt[_]] => l.filter({
                x => env.featureExpr(x).isSatisfiable()
            })
        })

        val cast = removedead(ast).get.asInstanceOf[T]
        copyPositions(ast, cast)
        cast
    }

    // function to add a break expression to infinite loops: "for (;;) {}" and "for (;;) ;"
    // reason is: for (;;) is the only infinite loop without explicit break statement,
    // so if we omit CompoundStatement in succ pred determination, we need an expression
    // so that succ(e) -> e and pred(e) is e
    // we add a Constant("1") at the break
    def rewriteInfiniteForLoops[T <: Product](ast: T): T = {
        assert(ast != null)

        val rewrite = everywherebu(rule {
            case f@ForStatement(_, None, _, _) =>
                f.copy(expr2 = Some(Constant("1")))
            case n: AST => n
        })

        val cast = rewrite(ast).get.asInstanceOf[T]
        copyPositions(ast, cast)
        cast
    }

    // filter AST nodes that do not have position information
    def checkPositionInformation[T <: Product](ast: T): List[AST] = {
        assert(ast != null)
        var nodeswithoutposition: List[AST] = List()

        val checkpos = everywherebu(query {
            case a: AST => if (!a.hasPosition) nodeswithoutposition ::= a
        })

        checkpos(ast)

        nodeswithoutposition
    }
}