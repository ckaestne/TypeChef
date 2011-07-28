package de.fosd.typechef.parser

/**
 * basic position information
 */
trait Position {
    def getFile: String
    def getLine: Int
    def getColumn: Int
    def <(that: Position) = (this.getLine < that.getLine) || ((this.getLine == that.getLine) && (this.getColumn < that.getColumn))
    override def toString = getFile + ":" + getLine + ":" + getColumn
}

object NoPosition extends Position {
    def getFile = ""
    def getLine = -1
    def getColumn = -1
}


/**
 * stores position range (from to)
 *
 * can be mixed into AST tokens
 *
 * is automatically set by the parser's map functions
 *
 * position might not always be set. try parent nodes if not set
 */
trait WithPosition {
    protected var range: Option[(Position, Position)] = None
    def setPositionRange(from: Position, to: Position): WithPosition.this.type = {
        range = Some((from, to))
        this
    }
    def hasPosition: Boolean = range.isDefined
    def getPositionFrom: Position = range.get._1
    def getPositionTo: Position = range.get._1
}

