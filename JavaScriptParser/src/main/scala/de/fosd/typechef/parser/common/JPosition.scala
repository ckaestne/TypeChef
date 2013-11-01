package de.fosd.typechef.parser.common


import de.fosd.typechef.error.Position


class JPosition(file: String, line: Int, col: Int) extends Position {
    def getFile: String = file

    def getLine: Int = line

    def getColumn: Int = col
}
