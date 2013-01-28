package de.fosd.typechef.crefactor

import de.fosd.typechef.crewrite.{CASTEnv, ASTEnv}
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory}
import de.fosd.typechef.parser.c.{TranslationUnit, AST}
import de.fosd.typechef.typesystem._

class Morpheus(ast: AST, fm: FeatureExpr) extends CDeclUse with CTypeEnv with CEnvCache with CTypeCache with CTypeSystem {
  def this(ast: AST) = this(ast, FeatureExprFactory.True)

  private var astCached: AST = ast
  private var astEnvCached: ASTEnv = CASTEnv.createASTEnv(ast)
  typecheckTranslationUnit(ast.asInstanceOf[TranslationUnit], fm)

  def update(ast: AST) = {
    astCached = ast
    astEnvCached = CASTEnv.createASTEnv(astCached)
    typecheckTranslationUnit(astCached.asInstanceOf[TranslationUnit], fm)
  }

  def getEnv(ast: AST): Env = lookupEnv(ast)

  def getAST(): AST = astCached

  def getASTEnv(): ASTEnv = astEnvCached

  def getFeatureModel(): FeatureExpr = fm

}
