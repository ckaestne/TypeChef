package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr.FeatureExpr

/**
 * common infrastructure for tests.
 * mainly for parsing
 */

trait TestHelper {


    def getAST(code: String): TranslationUnit = {
        val ast: AST = new ParserMain(new CParser).parserMain(
            () => CLexer.lex(code, null), new CTypeContext, false)
        assert(ast != null)
        ast.asInstanceOf[TranslationUnit]
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

}