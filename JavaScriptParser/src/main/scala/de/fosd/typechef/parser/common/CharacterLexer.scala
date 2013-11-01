package de.fosd.typechef.parser.common

import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import de.fosd.typechef.error.NoPosition
import de.fosd.typechef.parser.TokenReader
import java.util
import java.io.Reader

/**
 * this "lexer" chunks a file into character tokens. it will later resolve special characters
 * and it recognizes ifdefs
 */
object CharacterLexer {

    def lex(r: Reader): TokenReader[CharacterToken, Null] = {

        var _tokens = List[CharacterToken]()
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

            _tokens = new CharacterToken(c, FeatureExprFactory.True, new JPosition("", line, col)) :: _tokens

            c = r.read()
        }
        val tokens = _tokens.reverse.toArray

        var i = 0
        while (i < tokens.size) {
            var skip = false
            if (i<tokens.size-6 && tokens(i).getKind() == '_' && tokens(i + 1).getKind() == '_' && tokens(i + 2).getKind() == 'i' && tokens(i + 3).getKind() == 'f' && tokens(i + 4).getKind() == ' ' && tokens(i + 5).getKind() == '"') {
                var j = 1
                var neg = false
                val featureName = new StringBuffer()
                if (tokens(i + 5 + j).getKind() == '!') {
                    j += 1
                    neg = true
                }
                while (tokens(i + 5 + j).getKind() != '"') {
                    featureName.append(tokens(i + 5 + j))
                    j += 1
                }
                var expr: FeatureExpr = FeatureExprFactory.createDefinedExternal(featureName.toString)
                if (neg) expr = expr.not()
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

