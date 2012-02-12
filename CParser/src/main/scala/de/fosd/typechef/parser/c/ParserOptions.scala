package de.fosd.typechef.parser.c

/**
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 12.02.12
 * Time: 10:55
 * To change this template use File | Settings | File Templates.
 */

trait ParserOptions {
    def printParserResult: Boolean
    def printParserStatistics: Boolean
}

object DefaultParserOptions extends ParserOptions {
    def printParserResult = true
    def printParserStatistics = true
}


object SilentParserOptions extends ParserOptions {
    def printParserResult = false
    def printParserStatistics = false
}
