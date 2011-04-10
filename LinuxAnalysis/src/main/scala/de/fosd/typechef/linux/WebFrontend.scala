package de.fosd.typechef.linux

import java.io.File
import de.fosd.typechef.parser.c._
import org.anarres.cpp.Main
import de.fosd.typechef.featureexpr.FeatureExpr

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

        println("<pre name='partiallypreprocessed'>")
        val tokenStream = new Main().run(Array(file.getAbsolutePath), true, true, null)
        println("</pre>")

        println("<div name='tokenstream'>")
        val in = CLexer.prepareTokens(tokenStream)
        for (tok <- in.tokens) {
            print('"' + tok.getText + '"')
            if (tok.getFeature != FeatureExpr.base)
                print("<sub>" + tok.getFeature + "</sub>")
            println(" * ")
        }
        println("</div>")

        println("<div name='parserresult'>")
        val parserMain = new ParserMain(new CParser(null, false))
        val ast = parserMain.parserMain(in)
        println("</div>")
        println("<div name='ast'>")
        println(ast)
        println("</div>")

        println("done.")
        //            //create parser and start parsing
        //            val parserMain = new ParserMain(new CParser())
        //
        //            val parentPath = new File(filename).getParent()
        //            val ast = parserMain.parserMain(filename, parentPath)
        //            if (check != null && ast != null)
        //                check(ast)
    }


}