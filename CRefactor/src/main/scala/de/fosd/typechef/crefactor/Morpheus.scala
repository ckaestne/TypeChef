package de.fosd.typechef.crefactor

import de.fosd.typechef.crewrite.{CASTEnv, ASTEnv}
import de.fosd.typechef.featureexpr.FeatureModel
import de.fosd.typechef.parser.c.{TranslationUnit, AST}
import de.fosd.typechef.typesystem._
import java.util.Observable
import java.io.File
import de.fosd.typechef.Frontend

class Morpheus(ast: AST, fm: FeatureModel, file: File) extends Observable with CDeclUse with CTypeEnv with CEnvCache with CTypeCache with CTypeSystem with Logging {
    def this(ast: AST) = this(ast, null, null)

    def this(ast: AST, fm: FeatureModel) = this(ast, fm, null)

    def this(ast: AST, file: File) = this(ast, null, file)

    private var astCached: AST = ast
    private var astEnvCached: ASTEnv = CASTEnv.createASTEnv(ast)
    typecheckTranslationUnit(ast.asInstanceOf[TranslationUnit])

    //private var ts = new CTypeSystemFrontend(ast.asInstanceOf[TranslationUnit], fm)
    //ts.checkAST
    def update(ast: AST) {
        astCached = ast
        astEnvCached = CASTEnv.createASTEnv(astCached)
        //ts = new CTypeSystemFrontend(astCached.asInstanceOf[TranslationUnit], fm)
        //ts.checkAST
        typecheckTranslationUnit(ast.asInstanceOf[TranslationUnit])
        setChanged()
        notifyObservers()
    }

    def getEnv(ast: AST) = lookupEnv(ast)

    def getAST = astCached

    def getASTEnv = astEnvCached

    def getFeatureModel = fm

    def getFile = file
}

object Parse {

    def parse(configuration: Array[String]): AST = {
        Frontend.main(configuration)
        Frontend.getAST
    }
}
