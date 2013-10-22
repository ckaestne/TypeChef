package de.fosd.typechef.parser.html

import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import de.fosd.typechef.error.{NoPosition, Position}
import de.fosd.typechef.parser.{TokenReader, ProfilingToken, AbstractToken}
import de.fosd.typechef.parser.javascript.rhino.{Token, Parser, TokenStream}
import de.fosd.typechef.parser.javascript.{JPosition, JSToken}
import java.util
import java.io.Reader

/**
 * this "lexer" chunks a file into character tokens. it will later resolve special characters
 * and it recognizes ifdefs
 */
object Lexer {

    def lex(r: Reader): TokenReader[CharacterToken, Null] = {

        var tokens = List[CharacterToken]()
        var result = List[CharacterToken]()
        var fexprStack = new util.Stack[FeatureExpr]()
        fexprStack.push(FeatureExprFactory.True)

        var line = 0
        var col = -1

        var c = r.read()
        while (c >= 0) {
            col += 1
            if (c == '\n') {
                line += 1
                col = 0
            }

            tokens = new CharacterToken(c, FeatureExprFactory.True, new JPosition("", line, col)) :: tokens

            c = r.read()
        }
        tokens = tokens.reverse

        var i = 0
        while (i < tokens.size) {
            var skip = false
            if (i<tokens.size-6 && tokens(i).getKind() == '_' && tokens(i + 1).getKind() == '_' && tokens(i + 2).getKind() == 'i' && tokens(i + 3).getKind() == 'f' && tokens(i + 4).getKind() == ' ' && tokens(i + 5).getKind() == '"') {
                var j = 1
                val featureName = new StringBuffer()
                while (tokens(i + 5 + j).getKind() != '"') {
                    featureName.append(tokens(i + 5 + j))
                    j += 1
                }
                var expr: FeatureExpr = FeatureExprFactory.createDefinedExternal(featureName.toString)
                fexprStack.push(fexprStack.peek() and expr)
                i += 5 + j + 1
                skip = true
            }

            if (i<tokens.size-6 && tokens(i).getKind() == '_' && tokens(i + 1).getKind() == '_' && tokens(i + 2).getKind() == 'e' && tokens(i + 3).getKind() == 'n' && tokens(i + 4).getKind() == 'd' && tokens(i + 5).getKind() == 'i' && tokens(i + 6).getKind() == 'f') {
                if (fexprStack.size() <= 1)
                    System.err.println("too many __endif")
                else fexprStack.pop()
                i += 7
                skip = true
            }

            if (!skip) {
                result = tokens(i).and(fexprStack.peek()) :: result
                i += 1
            }
        }


        new TokenReader[CharacterToken, Null](result.reverse, 0, null, new CharacterToken(-1, FeatureExprFactory.True, NoPosition))

    }
}


class CharacterToken(
                        image: Int,
                        featureExpr: FeatureExpr,
                        position: Position) extends AbstractToken with ProfilingToken {


    def getFeature(): FeatureExpr = featureExpr

    def getText(): String = "" + image.toChar

    def getKind(): Int = image
    def getKindChar(): Char = image.toChar

    def getPosition(): Position = position

    override def toString = getText() + (if (!getFeature.isTautology()) getFeature else "")

    def and(expr: FeatureExpr): CharacterToken = new CharacterToken(image, featureExpr and expr, position)
}