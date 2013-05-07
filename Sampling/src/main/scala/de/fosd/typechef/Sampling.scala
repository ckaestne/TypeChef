package de.fosd.typechef

import lexer.options.OptionException
import parser.c._
import parser.c.CTypeContext
import parser.TokenReader

object Sampling {
    def main(args: Array[String]) {
        // load options
        val opt = new FamilyBasedVsSampleBasedOptions()
        try {
            try {
                opt.parseOptions(args)
            } catch {
                case o: OptionException => if (!opt.isPrintVersion) throw o
            }

            if (opt.isPrintVersion) {
                var version = "development build"
                try {
                    val cl = Class.forName("de.fosd.typechef.Version")
                    version = "version " + cl.newInstance().asInstanceOf[VersionInfo].getVersion
                } catch {
                    case e: ClassNotFoundException =>
                }

                println("TypeChef " + version)
                return
            }
        }

        catch {
            case o: OptionException =>
                println("Invocation error: " + o.getMessage)
                println("use parameter --help for more information.")
                return
        }

        processFile(opt)
    }

    def processFile(opt: FamilyBasedVsSampleBasedOptions) {
        val errorXML = new ErrorXML(opt.getErrorXMLFile)
        opt.setRenderParserError(errorXML.renderParserError)

        val fm = opt.getFeatureModel.and(opt.getLocalFeatureModel).and(opt.getFilePresenceCondition)
        opt.setFeatureModel(fm)
        if (!opt.getFilePresenceCondition.isSatisfiable(fm)) {
            println("file has contradictory presence condition. existing.")
            return
        }

        new lexer.Main().run(opt, opt.parse)
        val in = lex(opt)

        // parse anyway no matter what the options say
        val parserMain = new ParserMain(new CParser(fm))
        val ast = parserMain.parserMain(in, opt)

        if (ast != null) {
            val fm_ts = opt.getFeatureModelTypeSystem.and(opt.getLocalFeatureModel).and(opt.getFilePresenceCondition)
            FamilyBasedVsSampleBased.typecheckProducts(fm_ts, fm_ts, ast, opt, "")
        }
    }

    def lex(opt: FamilyBasedVsSampleBasedOptions): TokenReader[CToken, CTypeContext] = {
        val tokens = new lexer.Main().run(opt, opt.parse)
        val in = CLexer.prepareTokens(tokens)
        in
    }
}
