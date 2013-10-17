package de.fosd.typechef.parser.javascript

;

import rhino._
import java.io.FileReader
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.error.{NoPosition, Position}
import de.fosd.typechef.parser.TokenReader

object ParserMain extends App {

    val ts = new TokenStream(new Parser() {
        def reportError(s: String) { println(s); }
        def addWarning(s: String, s1: String) { println(s + " - " + s1); }
        def addError(s: String) { println(s); }
    }, new FileReader("JavaScriptParser/src/main/resources/jquery203.js"), null, 0)

    var tokens = List[JSToken]()

    while (!ts.eof()) {
        val t = ts.getToken
        print(Token.typeToName(t) + ":  ")

        println(ts.getString)

        tokens = new JSToken(ts.getString, FeatureExprFactory.True, new JPosition("file", ts.getLineno, -1), t) :: tokens
    }


    val parser = new JSParser()
    val result = parser.Program(new TokenReader[JSToken, Null](tokens, 0, null, new JSToken("EOF", FeatureExprFactory.True, NoPosition, Token.EOF)), FeatureExprFactory.True)
    println(result)
    //    parser.phrase()


}
