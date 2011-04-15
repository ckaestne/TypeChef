package de.fosd.typechef.linux

import java.io.File
import de.fosd.typechef.parser.c._
import org.anarres.cpp.Main
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.typesystem.TypeSystem

/**
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 10.04.11
 * Time: 12:56
 *
 *
 * frontend to the parser with little dependencies on other files
 *
 * produces html output
 */

object WebFrontend {

    def main(args: Array[String]) {
        if (args.size != 1 || !new File(args(0)).exists())
            println("incorrect paramter, expect C file")
        else {
            parse(new File(args(0)))
        }
    }

    def parse(file: File) {

        println("<h2>Input after macro expansion</h2><pre name='partiallypreprocessed'>")
        val tokenStream = new Main().run(Array(file.getAbsolutePath), true, true, null)
        println("</pre>")

        println("<h2>Conditional Token Stream</h2><div name='tokenstream'>")
        val in = CLexer.prepareTokens(tokenStream)
        for (tok <- in.tokens) {
            print('"' + tok.getText + '"')
            if (tok.getFeature != FeatureExpr.base)
                print("<sub>" + tok.getFeature + "</sub>")
            println(" * ")
        }
        println("</div>")

        println("<h2>Parser report</h2><pre name='parserresult'>")
        val parserMain = new ParserMain(new CParser(null, false))
        val ast = parserMain.parserMain(in)
        println("</pre>")
        println("<h2>AST</h2><div name='ast'>")
        println(ast)
        println("</div>")

        if (ast != null) {
            println("<h2>Type checking (incomplete!)</h2><pre name='tsoutput'>")
            new TypeSystem(null).checkAST(ast)
            println("</pre>")
        }
    }


}