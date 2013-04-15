package de.fosd.typechef.parser.c

import java.io.InputStream
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.conditional.One
import java.util.Collections

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

  def getAST(code: String): TranslationUnit = {
    val ast: AST = new ParserMain(new CParser).parserMain(
      () => CLexer.lex(code, null), new CTypeContext, SilentParserOptions)
    ast.asInstanceOf[TranslationUnit]
  }

  def parseFile(stream: InputStream, file: String, dir: String): TranslationUnit = {
    val ast: AST = new ParserMain(new CParser).parserMain(
      () => CLexer.lexStream(stream, file, Collections.singletonList(dir), null), new CTypeContext, SilentParserOptions)
    ast.asInstanceOf[TranslationUnit]
  }

  def parseExpr(code: String): Expr = {
    val in = CLexer.lex(code, null).setContext(new CTypeContext())
    val p = new CParser()
    val r = p.phrase(p.expr)(in, FeatureExprFactory.True)
    r.asInstanceOf[p.Success[Expr]].result
  }

  def parseDecl(code: String): Declaration = {
    val in = CLexer.lex(code, null).setContext(new CTypeContext())
    val p = new CParser()
    val r = p.phrase(p.declaration)(in, FeatureExprFactory.True)
    r.asInstanceOf[p.Success[Declaration]].result
  }

  def parseCompoundStmt(code: String): CompoundStatement = {
    val in = CLexer.lex(code, null).setContext(new CTypeContext())
    val p = new CParser()
    val r = p.phrase(p.compoundStatement)(in, FeatureExprFactory.True)
    r.asInstanceOf[p.Success[CompoundStatement]].result
  }

  def parseFunctionDef(code: String): FunctionDef = {
    val in = CLexer.lex(code, null).setContext(new CTypeContext())
    val p = new CParser()
    val r = p.phrase(p.functionDef)(in, FeatureExprFactory.True)
    r.asInstanceOf[p.Success[FunctionDef]].result
  }

  def parseStmt(code: String): Statement = {
    val in = CLexer.lex(code, null).setContext(new CTypeContext())
    val p = new CParser()
    val r = p.phrase(p.statement)(in, FeatureExprFactory.True)
    r.asInstanceOf[p.Success[One[Statement]]].result.value
  }
}