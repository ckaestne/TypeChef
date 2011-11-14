package de.fosd.typechef.typesystem


import de.fosd.typechef.parser.c._
import org.kiama.attribution.Attribution._
import org.kiama._
import de.fosd.typechef.conditional.{One, ConditionalLib, Conditional}

/**
 * typing C statements
 */
trait CStmtTyping extends CTypes with CExprTyping {

    def ctype(stmt: Statement) = getStmtType(stmt)

    def getStmtType(stmt: Statement): Conditional[CType] = stmt -> stmtType


    /**
     * types for statements; most statements do not have types
     *
     * information extracted from sparse (evaluate.c)
     */
    val stmtType: Statement ==> Conditional[CType] = attr {
        case ExprStatement(expr) => ctype(expr)
        case CompoundStatement(inner) =>
            val lastStmt: Conditional[Option[Statement]] = ConditionalLib.lastEntry(inner)
            lastStmt.mapr({
                case None => One(CVoid())
                case Some(stmt) => stmt -> stmtType
            }) simplify
        case stmt =>
            One(CUnknown("no type for " + stmt))
    }

}