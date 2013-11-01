package de.fosd.typechef.parser.javascript

;

import java.io.{Reader, FileReader}
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.parser.common.CharacterLexer

object ParserMain extends App {



    def parse(r: Reader) = {


        val parser = new JSParser()
        parser.phrase(parser.Identifier)(CharacterLexer.lex(r), FeatureExprFactory.True)
    }


    val result = parse(new FileReader("JavaScriptParser/src/main/resources/jquery203.js"))

    println(result)
    //    parser.phrase()


}
