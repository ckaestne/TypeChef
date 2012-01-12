package de.fosd.typechef.parser.c

import de.fosd.typechef.featureexpr.FeatureExpr
import java.io.InputStream

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
        ast.asInstanceOf[TranslationUnit]
    }

    def parseFile(stream: InputStream, file: String, dir: String): TranslationUnit = {
        val ast: AST = new ParserMain(new CParser).parserMain(
            () => CLexer.lexStream(stream, file, dir, null), new CTypeContext, false)
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

    def parseCompoundStmt(code: String): CompoundStatement = {
        val in = CLexer.lex(code, null).setContext(new CTypeContext())
        val p = new CParser()
        val r = p.phrase(p.compoundStatement)(in, FeatureExpr.base)
        r.asInstanceOf[p.Success[CompoundStatement]].result
    }

    def parseFunctionDef(code: String): FunctionDef = {
        val in = CLexer.lex(code, null).setContext(new CTypeContext())
        val p = new CParser()
        val r = p.phrase(p.functionDef)(in, FeatureExpr.base)
        r.asInstanceOf[p.Success[FunctionDef]].result
    }
}