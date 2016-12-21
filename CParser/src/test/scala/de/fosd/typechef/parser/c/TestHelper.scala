package de.fosd.typechef.parser.c

import java.io.InputStream
import de.fosd.typechef.featureexpr.{FeatureModel, FeatureExprFactory}
import de.fosd.typechef.conditional.One
import java.util.Collections
import de.fosd.typechef.parser.TokenReader
import de.fosd.typechef.lexer.LexerFrontend
import de.fosd.typechef.parser.c.CLexerAdapter.TokenWrapper

/**
 * common infrastructure for tests.
 * mainly for parsing
 */

trait TestHelper {

    val fa = FeatureExprFactory.createDefinedExternal("A")
    val fb = FeatureExprFactory.createDefinedExternal("B")
    val fc = FeatureExprFactory.createDefinedExternal("C")
    val fx = FeatureExprFactory.createDefinedExternal("X")
    val fy = FeatureExprFactory.createDefinedExternal("Y")

    import scala.collection.JavaConverters._

    def lexFile(fileName: String, systemIncludePath: java.util.List[String], featureModel: FeatureModel = FeatureExprFactory.empty): TokenReader[TokenWrapper, CTypeContext] =
        CLexerAdapter.prepareTokens(new LexerFrontend().parseFile(fileName, systemIncludePath, featureModel).asScala)

    def lexStream(stream: InputStream, filePath: String, systemIncludePath: java.util.List[String], featureModel: FeatureModel = FeatureExprFactory.empty): TokenReader[TokenWrapper, CTypeContext] =
        CLexerAdapter.prepareTokens(new LexerFrontend().parseStream(stream, filePath, systemIncludePath, featureModel).asScala)

    def lex(text: String, featureModel: FeatureModel = FeatureExprFactory.empty): TokenReader[TokenWrapper, CTypeContext] =
        CLexerAdapter.prepareTokens(new LexerFrontend().parse(text, new java.util.ArrayList[String](), featureModel).asScala)


    def getAST(code: String): TranslationUnit = {
        val ast: AST = new ParserMain(new CParser).parserMain(
            () => lex(code, null), new CTypeContext, SilentParserOptions, null)
        ast.asInstanceOf[TranslationUnit]
    }

    def parseFile(stream: InputStream, file: String, dir: String): TranslationUnit = {
        val ast: AST = new ParserMain(new CParser).parserMain(
            () => lexStream(stream, file, Collections.singletonList(dir), null), new CTypeContext, SilentParserOptions, null)
        ast.asInstanceOf[TranslationUnit]
    }

    def parseExpr(code: String): Expr = {
        val in = lex(code, null).setContext(new CTypeContext())
        val p = new CParser()
        val r = p.phrase(p.expr)(in, FeatureExprFactory.True)
        r.asInstanceOf[p.Success[Expr]].result
    }

    def parseDecl(code: String): Declaration = {
        val in = lex(code, null).setContext(new CTypeContext())
        val p = new CParser()
        val r = p.phrase(p.declaration)(in, FeatureExprFactory.True)
        r.asInstanceOf[p.Success[Declaration]].result
    }

    def parseCompoundStmt(code: String): CompoundStatement = {
        val in = lex(code, null).setContext(new CTypeContext())
        val p = new CParser()
        val r = p.phrase(p.compoundStatement)(in, FeatureExprFactory.True)
        r.asInstanceOf[p.Success[CompoundStatement]].result
    }

    def parseFunctionDef(code: String): FunctionDef = {
        val in = lex(code, null).setContext(new CTypeContext())
        val p = new CParser()
        val r = p.phrase(p.functionDef)(in, FeatureExprFactory.True)
        r.asInstanceOf[p.Success[FunctionDef]].result
    }

    def parseStmt(code: String): Statement = {
        val in = lex(code, null).setContext(new CTypeContext())
        val p = new CParser()
        val r = p.phrase(p.statement)(in, FeatureExprFactory.True)
        r.asInstanceOf[p.Success[One[Statement]]].result.value
    }
    def parseTranslationUnit(code: String): TranslationUnit = {
        val in = lex(code, null).setContext(new CTypeContext())
        val p = new CParser()
        val r = p.phrase(p.translationUnit)(in, FeatureExprFactory.True)
        r.asInstanceOf[p.Success[TranslationUnit]].result
    }
}