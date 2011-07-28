package de.fosd.typechef.parser.c

import de.fosd.typechef.parser._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.lexer._

/**
 * thin wrapper around jccp tokens to make them accessible to MultiFeatureParser
 * @author kaestner
 *
 */
class TokenWrapper(token: Token, number: Int) extends /*AbstractToken*/ ProfilingToken {
    def getFeature = token.getFeature()
    def isInteger = token.getType == Token.INTEGER
    def isIdentifier = token.getType == Token.IDENTIFIER && !CLexer.keywords.contains(token.getText)
    def getText: String = token.getText
    def getType = token.getType
    override def toString = "\"" + token.getText + "\"" + (if (!getFeature.isBase()) getFeature else "")
    private lazy val pos = new TokenPosition(
        if (token.getSource == null) null else token.getSource.toString,
        token.getLine,
        token.getColumn,
        number
    )
    def getPosition = pos
}

class TokenPosition(file: String, line: Int, column: Int, tokenNr: Int) extends Position {
    def getFile = file
    def getLine = line
    def getColumn = column
    //    override def toString = "token no. " + tokenNr + " (line: " + getLine + ")"
}

///**
// * Debug and Profiling information for an input line.
// */
//class LineInformation(val lineNumber: Int) {
//    /**
//     * How often where tokens in that line accessed during
//     * parsing?
//     */
//    var accessCount: Int = 0
//
//    /**How many tokens are in this line? */
//    var tokenCount: Int = 0
//
//    var failureCount: Int = 0;
//    var splitCount: Int = 0;
//    var successCount: Int = 0;
//}

//object LineInformation {
//    private val lineBuffer
//    = new ListBuffer[LineInformation]()
//
//    def addLine(line: LineInformation)
//    = lineBuffer += line
//
//    def getLines: List[LineInformation]
//    = lineBuffer.toList
//
//    def printStatistics(out: PrintStream) = {
//        out.println("line; tokens; token accesses; success; failure; split");
//        for (info <- getLines) {
//            out.print(info.lineNumber)
//            out.print(";")
//            out.print(info.tokenCount)
//            out.print(";")
//            out.print(info.accessCount)
//            out.print(";")
//            out.print(info.successCount)
//            out.print(";")
//            out.print(info.failureCount)
//            out.print(";")
//            out.print(info.splitCount)
//            out.println();
//        }
//    }
//}
//
///**
// * A variant of TokenWrapper which keeps track of profiling
// * information.
// *
// * @author Tillmann Rendel
// */
//class ProfilingTokenWrapper(line: LineInformation, token: Token, number: Int) extends TokenWrapper(token, number) {
//    /**
//     * Increase the token access counter in the associated
//     * line information object before actually doing something.
//     */
//    private def access[A](code: => A): A = {
//        line.accessCount += 1
//        code
//    }
//
//    // Wrap all methods in the public interface.
//    //
//    // This works well because these methods are all simple
//    // wrappers and do not call each other, so late binding
//    // does not get into the way.
//    override def getFeature = access {super.getFeature}
//    override def isInteger = access {super.isInteger}
//    override def isIdentifier = access {super.isIdentifier}
//    override def getText = access {super.getText}
//    override def getType = access {super.getType}
//    override def toString = access {super.toString}
//    override def getPosition = access {super.getPosition}
//
//    // profiling
//    override def countSplit {super.countSplit; line.splitCount += 1}
//    override def countSuccess(feature: FeatureExpr) {super.countSuccess(feature); line.successCount += 1}
//    override def countFailure {super.countFailure; line.failureCount += 1}
//}

object TokenWrapper {
    //    /**
    //     * Enable or disable access counting.
    //     */
    //    val profiling = false
    //
    //    // Information about the last token
    //    var lastLine: Int = -1
    //    var currentLine: LineInformation = new LineInformation(-1)
    //    var lastTime: Long = 0;

    /**
     * Factory method for the creation of TokenWrappers.
     */
    def apply(token: Token, number: Int) = {
        //        if (profiling) {
        //            // this tokens starts a new line?
        //            if (lastLine < token.getLine) {
        //                // prepare new line information
        //                lastLine = token.getLine
        //                currentLine = new LineInformation(token.getLine)
        //                LineInformation.addLine(currentLine)
        //            }
        //
        //            // count the token
        //            currentLine.tokenCount += 1
        //
        //            // create profiling token wrapper
        //            new ProfilingTokenWrapper(currentLine, token, number);
        //        } else {
        // create non-profiling token wrapper
        new TokenWrapper(token, number)
        //        }
    }

    val EOF = new TokenWrapper(new SimpleToken(Token.EOF, -1, -1, "<EOF>", null, null), -1) {
        override def getFeature = FeatureExpr.dead
    }
}