package de.fosd.typechef.parser.html

import java.io.FileReader
import de.fosd.typechef.featureexpr.FeatureExprFactory

/**
 * Created with IntelliJ IDEA.
 * User: ckaestne
 * Date: 10/22/13
 * Time: 12:37 PM
 * To change this template use File | Settings | File Templates.
 */
object Main extends App {

    var tokens = Lexer.lex(new FileReader("JavaScriptParser/src/main/resources/test.html"))

    println(tokens)

    val p = new HTMLParser

    println(p.HtmlSequence(tokens,FeatureExprFactory.True))

}
