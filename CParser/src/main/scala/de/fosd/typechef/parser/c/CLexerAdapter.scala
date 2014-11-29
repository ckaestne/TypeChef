package de.fosd.typechef.parser.c


import de.fosd.typechef.parser._
import de.fosd.typechef.lexer._
import scala.collection.mutable.ListBuffer
import de.fosd.typechef.LexerToken
import de.fosd.typechef.conditional.Conditional
import de.fosd.typechef.featureexpr.FeatureExprFactory.True
import de.fosd.typechef.lexer.LexerFrontend.LexerSuccess
import java.io.InputStream
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureModel}

/**
 * wrapper for the partial preprocessor, which adapts the output of the lexer into the input of the parser
 * @author kaestner
 *
 */
object CLexerAdapter {
    type TokenWrapper = CToken

    import scala.collection.JavaConversions._


    /**
     * converts the result of the lexer into a TokenReader by providing unique IDs
     */
    def prepareTokens(tokenList: Iterable[LexerToken]): TokenReader[TokenWrapper, CTypeContext] = {
        val tokens = tokenList.iterator
        val result = new ListBuffer[TokenWrapper]
        var tokenNr: Int = 0
        while (tokens.hasNext) {
            val t = tokens.next
            result += CToken(t, tokenNr)
            tokenNr = tokenNr + 1
        }
        new TokenReader(result.toList, 0, new CTypeContext(), CToken.EOF)
    }

    /**
     * converts the result of the lexer into a TokenReader by providing unique IDs
     *
     * if there are alternative parse results, they are merged into one TokenReader while
     * errors are ignored
     */
    def prepareTokens(lexerResult: Conditional[LexerFrontend.LexerResult]): TokenReader[TokenWrapper, CTypeContext] = {
        val tokens = lexerResult.vmap(True, {
            case (f, s: LexerSuccess) => s.getTokens.map(t => {t.setFeature(t.getFeature and f); t})
            case _ => Nil
        }).flatten((f,a,b)=>a ++ b)
        prepareTokens(tokens.toIterable)
    }

    def lexStream(stream: InputStream, filePath: String, systemIncludePath: java.util.List[String], featureModel: FeatureModel = FeatureExprFactory.empty): TokenReader[TokenWrapper, CTypeContext] =
        CLexerAdapter.prepareTokens(new LexerFrontend().parseStream(stream, filePath, systemIncludePath, featureModel))


}