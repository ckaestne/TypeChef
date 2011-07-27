package de.fosd.typechef.typesystem


import de.fosd.typechef.parser.c._
import org.kiama.attribution.Attribution._
import org.kiama._
import de.fosd.typechef.conditional.{TOne, ConditionalLib, TConditional}

/**
 * typing C statements
 */
trait CStmtTyping extends CTypes with CExprTyping {

    def ctype(stmt: Statement) = getStmtType(stmt)

    def getStmtType(stmt: Statement): TConditional[CType] = stmt -> stmtType


    /**
     * types for statements; most statements do not have types
     *
     * information extracted from sparse (evaluate.c)
     */
    val stmtType: Statement ==> TConditional[CType] = attr {
        case ExprStatement(expr) => ctype(expr)
        case CompoundStatement(inner) =>
            val lastStmt: TConditional[Option[Statement]] = ConditionalLib.lastEntry(inner)
            lastStmt.mapr({
                case None => TOne(CVoid())
                case Some(stmt) => stmt -> stmtType
            }) simplify
        case stmt =>
            TOne(CUnknown("no type for " + stmt))
    }

}