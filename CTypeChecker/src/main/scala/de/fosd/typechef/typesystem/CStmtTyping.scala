package de.fosd.typechef.typesystem


import de.fosd.typechef.parser.c._
import org.kiama.attribution.Attribution._
import org.kiama._

/**
 * typing C statements
 */
trait CStmtTyping extends CTypes with CExprTyping {

    def ctype(stmt: Statement) = stmt -> stmtType

    def stmtType(stmt: Statement): CType = stmt -> stmtType


    /**
     * types for statements; most statements do not have types
     *
     * information extracted from sparse (evaluate.c)
     */
    val stmtType: Statement ==> CType = attr {
        case ExprStatement(expr) => __makeOne(ctype(expr))
        case CompoundStatement(inner) =>
        //TODO variability (the last statement may differ)
            if (inner.isEmpty) CVoid() //sparse
            else ctype(inner.last.entry)
        case stmt => CUnknown("no type for " + stmt)
    }

}