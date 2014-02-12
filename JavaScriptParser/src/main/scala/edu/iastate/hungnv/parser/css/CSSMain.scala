package edu.iastate.hungnv.parser.css

import java.io.FileReader
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.parser.TokenReader
import de.fosd.typechef.parser.common.CharacterLexer

/**
 * @author HUNG
 * Modified from TypeChef
 */
object CSSMain extends App {

    var tokens = CharacterLexer.lex(new FileReader("src/main/resources/test.css"))

    println(tokens)
    

    val p = new CSSParser

    val parseResult = p.phrase(p.StyleSheet)(tokens, FeatureExprFactory.True)
    

    var result = List[Opt[String]]()
    parseResult match {
        case p.Success(r, rest) =>
            result = r.ruleSets.map(t => Opt(t.feature, t.entry.toString))
            if (!rest.atEnd) println("error: Parser not at end: "+rest)
        case x => println("parsing problem: "+x)
    }

    println(result.mkString("\n"))

    println("\n\n")
}
