package de.fosd.typechef.crefactor

import de.fosd.typechef.crewrite.{CASTEnv, ASTEnv}
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory}
import de.fosd.typechef.parser.c.{TranslationUnit, AST}
import de.fosd.typechef.typesystem._
import java.util.Observable
import java.io.File

class Morpheus(ast: AST, fm: FeatureExpr, file: File) extends Observable with CDeclUse with CTypeEnv with CEnvCache with CTypeCache with CTypeSystem {
  def this(ast: AST) = this(ast, FeatureExprFactory.True, null)

  def this(ast: AST, fm: FeatureExpr) = this(ast, fm, null)

  def this(ast: AST, file: File) = this(ast, FeatureExprFactory.True, file)

  private var astCached: AST = ast
  private var astEnvCached: ASTEnv = CASTEnv.createASTEnv(ast)
  typecheckTranslationUnit(ast.asInstanceOf[TranslationUnit], fm)

  def update(ast: AST) = {
    astCached = ast
    astEnvCached = CASTEnv.createASTEnv(astCached)
    typecheckTranslationUnit(astCached.asInstanceOf[TranslationUnit], fm)
    setChanged()
    notifyObservers()
  }

  def getEnv(ast: AST): Env = lookupEnv(ast)

  def getAST(): AST = astCached

  def getASTEnv(): ASTEnv = astEnvCached

  def getFeatureModel(): FeatureExpr = fm

  def getFile(): File = file

}
