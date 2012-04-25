package de.fosd.typechef.featureexpr.sat


/**
 * CNFHelper provides several auxiliary functions to determine whether an expression is
 * in normal form and to access parts of that normal form
 */
object CNFHelper {


    //for testing
    def isCNF(expr: SATFeatureExpr) = isTrueFalse(expr) || isClause(expr) || (expr match {
        case And(clauses) => clauses.forall(isClause(_))
        case e => false
    })
    def isClauseOrTF(expr: SATFeatureExpr) = isTrueFalse(expr) || isClause(expr)
    def isClause(expr: SATFeatureExpr) = isLiteral(expr) || (expr match {
        case Or(literals) => literals.forall(isLiteral(_))
        case _ => false
    })
    def isLiteral(expr: SATFeatureExpr) = expr match {
        case x: DefinedExpr => true
        case Not(DefinedExpr(_)) => true
        case _ => false
    }
    def isLiteralExternal(expr: SATFeatureExpr) = expr match {
        case x: DefinedExternal => true
        case Not(x: DefinedExternal) => true
        case _ => false
    }
    def isTrueFalse(expr: SATFeatureExpr) = expr match {
        case True => true
        case False => true
        case _ => false
    }

    def getCNFClauses(cnfExpr: SATFeatureExpr): Traversable[SATFeatureExpr /*Clause*/ ] = cnfExpr match {
        case And(clauses) => clauses
        case e => Set(e)
    }

    def getLiterals(orClause: SATFeatureExpr): Traversable[SATFeatureExpr /*Literal*/ ] = orClause match {
        case Or(literals) => literals
        case e => Set(e)
    }

    def getDefinedExprs(orClause: SATFeatureExpr): Set[DefinedExpr] = orClause match {
        case Or(literals) => literals.map(getDefinedExpr(_)).foldLeft[Set[DefinedExpr]](Set())(_ + _)
        case e => Set(getDefinedExpr(e))
    }

    def getDefinedExpr(literal: SATFeatureExpr): DefinedExpr = literal match {
        case x: DefinedExpr => x
        case Not(x: DefinedExpr) => x
        case _ => throw new NoLiteralException(literal)
    }
}
