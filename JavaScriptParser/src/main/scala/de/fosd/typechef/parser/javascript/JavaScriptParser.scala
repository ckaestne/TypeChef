package de.fosd.typechef.parser.javascript

;

import rhino._
import java.io.{Reader, FileReader}
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory}
import de.fosd.typechef.error.{NoPosition, Position}
import de.fosd.typechef.parser.TokenReader
import scala.collection.mutable.ListBuffer
import java.util

object ParserMain extends App {

    def lex(r: Reader) = {
        val ts = new TokenStream(new Parser() {
            def reportError(s: String) { println(s); }
            def addWarning(s: String, s1: String) { println(s + " - " + s1); }
            def addError(s: String) { println(s); }
        }, r, null, 0)

        var tokens = List[JSToken]()
        var afterNewLine = true
        var fexprStack = new util.Stack[FeatureExpr]()
        fexprStack.push(FeatureExprFactory.True)
        var startIfdef = false
        var startIfndef = false

        while (!ts.eof()) {
            val t = ts.getToken
            var skip = false

            if (startIfdef || startIfndef) {
                if (t != Token.STRING) System.err.println("invalid __ifdef syntax, expecting feature name as string")
                else {
                    var expr:FeatureExpr=FeatureExprFactory.createDefinedExternal(ts.getString)
                    if (startIfndef) expr=expr.not()
                    fexprStack.push(fexprStack.peek() and expr)
                }
                skip = true
            }
            startIfdef = ts.getString == "__ifdef"
            startIfndef = ts.getString == "__ifndef"
            if (startIfdef || startIfndef) skip = true
            if (ts.getString == "__endif") {
                if (fexprStack.size() <= 1)
                    System.err.println("too many __endif")
                else fexprStack.pop()
                skip = true
            }


            //        print(Token.typeToName(t) + ":  ")
            //
            //        println(ts.getString)


            if (t == Token.COMMENT) skip = true

            if (!skip) {
                if (t != Token.EOL && t != Token.EOF)
                    tokens = new JSToken(if (Set(Token.NAME, Token.NUMBER) contains t) ts.getString else Token.typeToName(t), fexprStack.peek(), new JPosition("file", ts.getLineno, -1), t, afterNewLine) :: tokens

                afterNewLine = t == Token.EOL
            }
        }

        new TokenReader[JSToken, Null](tokens.reverse, 0, null, new JSToken("EOF", FeatureExprFactory.True, NoPosition, Token.EOF, true))
    }

    def parse(r: Reader) = {


        val parser = new JSParser()
        parser.phrase(parser.Program)(lex(r), FeatureExprFactory.True)
    }


    val result = parse(new FileReader("JavaScriptParser/src/main/resources/jquery203.js"))

    println(result)
    //    parser.phrase()


}
