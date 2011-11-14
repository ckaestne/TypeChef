package de.fosd.typechef.typesystem


import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional.{One, ConditionalLib, Conditional}

/**
 * typing C statements
 */
trait CStmtTyping extends CTypes with CEnv with CExprTyping {


    /**
     * types for statements; most statements do not have types
     *
     * information extracted from sparse (evaluate.c)
     */
    def getStmtType(stmt: Statement, env: Env): (Conditional[CType], Env) = stmt match {
        case ExprStatement(expr) => (getExprType(expr, env)._1, env)
        case CompoundStatement(inner) =>
            val lastStmt: Conditional[Option[Statement]] = ConditionalLib.lastEntry(inner)
            //TODO fix environment, fuse with building environment
            val t: Conditional[CType] = lastStmt.mapr({
                case None => One(CVoid())
                case Some(stmt) => getStmtType(stmt, env)._1
            }) simplify

            (t, env)
        case stmt =>
            (One(CUnknown("no type for " + stmt)), env)
    }

}