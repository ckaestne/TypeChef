package de.fosd.typechef.parser.c

import de.fosd.typechef.featureexpr.FeatureExpr
import org.kiama.attribution.Attributable
import org.kiama.rewriting.Rewriter._
import java.io.InputStream
import util.parsing.input.OffsetPosition

/**
 * common infrastructure for tests.
 * mainly for parsing
 */

trait TestHelper {

    val fa = FeatureExpr.createDefinedExternal("A")
    val fb = FeatureExpr.createDefinedExternal("B")
    val fc = FeatureExpr.createDefinedExternal("C")
    val fx = FeatureExpr.createDefinedExternal("X")
    val fy = FeatureExpr.createDefinedExternal("Y")

    def getAST(code: String): TranslationUnit = {
        val ast: AST = new ParserMain(new CParser).parserMain(
            () => CLexer.lex(code, null), new CTypeContext, false)
        prepareAST(ast)
    }

    def parseFile(stream: InputStream, file: String, dir: String): TranslationUnit = {
        val ast: AST = new ParserMain(new CParser).parserMain(
            () => CLexer.lexStream(stream, file, dir, null), new CTypeContext, false)
        prepareAST(ast)
    }

    def parseExpr(code: String): Expr = {
        val in = CLexer.lex(code, null).setContext(new CTypeContext())
        val p = new CParser()
        val r = p.phrase(p.expr)(in, FeatureExpr.base)
        r.asInstanceOf[p.Success[Expr]].result
    }

    def parseDecl(code: String): Declaration = {
        val in = CLexer.lex(code, null).setContext(new CTypeContext())
        val p = new CParser()
        val r = p.phrase(p.declaration)(in, FeatureExpr.base)
        r.asInstanceOf[p.Success[Declaration]].result
    }


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

    private def prepareAST(ast: AST): TranslationUnit = {
        assert(ast != null)
        //        val clone = everywherebu(rule {
        //            case n: AST => n.clone()
        //        })
        ast.pos = new OffsetPosition("abcde", 3)
        val clone = everywherebu(rule {
            case n: AST =>
                if (n.hasChildren) {
                    n.setChildConnections
                    n
                } else
                    n.clone()
        })
        val cast = clone(ast).get.asInstanceOf[TranslationUnit]
        ensureTree(cast)
        assertTree(cast)
        cast
    }


}