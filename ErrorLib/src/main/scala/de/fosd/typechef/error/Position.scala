package de.fosd.typechef.error

/**
 * basic position information
 */
trait Position extends Serializable {
    def getFile: String
    def getLine: Int
    def getColumn: Int
    def <(that: Position) = (this.getLine < that.getLine) || ((this.getLine == that.getLine) && (this.getColumn < that.getColumn))
    override def toString = getFile + ":" + getLine + ":" + getColumn
    override def equals(that: Any) = that match {
        case p: Position => getLine == p.getLine && getColumn == p.getColumn && getFile == p.getFile
        case _ => false
    }
    override def hashCode = getLine * 127 + getColumn
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
    def getFile: Option[String] = {
        if (range.isDefined) return Some(range.get._1.getFile) else None
    }
    var range: Option[(Position, Position)] = None
    def setPositionRange(that: WithPosition): WithPosition.this.type = setPositionRange(that.getPositionFrom, that.getPositionTo)
    def setPositionRange(from: WithPosition, to: WithPosition): WithPosition.this.type = setPositionRange(from.getPositionFrom, to.getPositionTo)
    def setPositionRange(from: Position, to: Position): WithPosition.this.type = {
        range = Some((from, to))
        this
    }
    def hasPosition: Boolean = range.isDefined
    def getPositionFrom: Position = rangeClean._1
    def getPositionTo: Position = rangeClean._2
    def rangeClean = if (hasPosition) range.get else (NoPosition, NoPosition)
}

