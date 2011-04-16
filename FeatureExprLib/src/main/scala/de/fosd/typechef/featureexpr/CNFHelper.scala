package de.fosd.typechef.featureexpr


/**
 * CNFHelper provides several auxiliary functions to determine whether an expression is
 * in normal form and to access parts of that normal form
 */
object CNFHelper {


    //for testing
    def isCNF(expr: FeatureExpr) = isTrueFalse(expr) || isClause(expr) || (expr match {
        case And(clauses) => clauses.forall(isClause(_))
        case e => false
    })
    def isClauseOrTF(expr: FeatureExpr) = isTrueFalse(expr) || isClause(expr)
    def isClause(expr: FeatureExpr) = isLiteral(expr) || (expr match {
        case Or(literals) => literals.forall(isLiteral(_))
        case _ => false
    })
    def isLiteral(expr: FeatureExpr) = expr match {
        case x: DefinedExpr => true
        case Not(DefinedExpr(_)) => true
        case _ => false
    }
    def isLiteralExternal(expr: FeatureExpr) = expr match {
        case x: DefinedExternal => true
        case Not(x: DefinedExternal) => true
        case _ => false
    }
    def isTrueFalse(expr: FeatureExpr) = expr match {
        case True => true
        case False => true
        case _ => false
    }

    def getCNFClauses(cnfExpr: FeatureExpr): Traversable[FeatureExpr /*Clause*/ ] = cnfExpr match {
        case And(clauses) => clauses
        case e => Set(e)
    }

    def getLiterals(orClause: FeatureExpr): Traversable[FeatureExpr /*Literal*/ ] = orClause match {
        case Or(literals) => literals
        case e => Set(e)
    }

    def getDefinedExprs(orClause: FeatureExpr): Set[DefinedExpr] = orClause match {
        case Or(literals) => literals.map(getDefinedExpr(_)).foldLeft[Set[DefinedExpr]](Set())(_ + _)
        case e => Set(getDefinedExpr(e))
    }

    def getDefinedExpr(literal: FeatureExpr): DefinedExpr = literal match {
        case x: DefinedExpr => x
        case Not(x: DefinedExpr) => x
        case _ => throw new NoLiteralException(literal)
    }
}
