package de.fosd.typechef.parser.html

import java.io.FileReader
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.parser.TokenReader
import de.fosd.typechef.parser.common.CharacterLexer

/**
 * Created with IntelliJ IDEA.
 * User: ckaestne
 * Date: 10/22/13
 * Time: 12:37 PM
 * To change this template use File | Settings | File Templates.
 */
object Main extends App {

    // stage 1: SAX parser

    var tokens = CharacterLexer.lex(new FileReader("JavaScriptParser/src/main/resources/test.html"))

    println(tokens)

    val p = new HTMLSAXParser

    val tagSequence = p.HtmlSequence(tokens,FeatureExprFactory.True)

    // stage 2: DOM parser

    var domTokens = List[HElementToken]()
    tagSequence match {
        case p.Success(r, rest) => domTokens = r.map(t=>new HElementToken(t))
        case x => println("parsing problem: "+x)
    }

    println(domTokens)

    val p2 = new HTMLDomParser

    val tokenStream = new TokenReader[HElementToken, Null](domTokens, 0, null, new HElementToken(Opt(FeatureExprFactory.True,HText(List()))))


    val dom = p2.Element(tokenStream,FeatureExprFactory.True)

    println(dom)


}
