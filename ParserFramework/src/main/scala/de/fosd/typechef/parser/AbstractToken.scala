package de.fosd.typechef.parser
import de.fosd.typechef.featureexpr.FeatureExpr

trait AbstractToken {
    def getFeature: FeatureExpr
    def getText: String
    def getPosition: Position

    // profiling
    def countSuccess = {} 
    def countFailure = {} 
    def countSplit = {} 
}

trait Position {
    def getFile: String
    def getLine: Int
    def getColumn: Int
    override def toString = getFile + ":" + getLine + ":" + getColumn
}