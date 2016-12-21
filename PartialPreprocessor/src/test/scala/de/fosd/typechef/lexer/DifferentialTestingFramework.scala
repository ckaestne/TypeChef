package de.fosd.typechef.lexer

import java.io._

import de.fosd.typechef.conditional.{Conditional, One}
import de.fosd.typechef.featureexpr.{FeatureExprFactory, SingleFeatureExpr}
import de.fosd.typechef.lexer.LexerFrontend.{LexerError, LexerResult, LexerSuccess}
import de.fosd.typechef.{LexerToken, VALexer}

/**
 * differential testing compares the output of the jcpp/xtc preprocessor with
 * the output of an external preprocessor, brute force over all configurations
 *
 * That is, we execute the following:
 * * jcpp/xtc over all configurations producing a conditional token stream
 * * jcpp/xtc over each configuration separately, producing a token stream without conditions
 * * cpp (needs to be available in the system's path) producing a preprocessed file, that is then lexed by jcpp/xtc
 *
 * Features can be defined in the first line of the test file with "// features: A B C D" or are extracted
 * from whatever features jcpp/xtc find
 */
trait DifferentialTestingFramework extends LexerHelper {

    import scala.collection.JavaConverters._


    def analyzeFile(file: File, inclDirectory: File, debug: Boolean = false, ignoreWarnings: Boolean = false): Unit = {
        assert(file != null && file.exists(), s"file not found: $file")
        val fileContent = getFileContent(file)

        val initFeatures: Set[SingleFeatureExpr] = getInitFeatures(fileContent)


        status(s"lexing all configurations for $file")
        val vresult = lex(file, inclDirectory, debug, ignoreWarnings)
        assert(expectTotalFailure(fileContent) || hasSuccess(vresult), "parsing failed in all configurations: " + vresult)
        val features = getFeatures(initFeatures, vresult)
        val maxFeatures = 8
        if (features.size > maxFeatures)
            System.err.println("Warning: too many features (%s; 2^%d configurations)\n using random instead of exaustive strategy beyond %d features".format(features, features.size, maxFeatures))

        for (config <- genAllConfigurations(features)) {
            //run same lexer on a single configuration
            status(s"comparing against single config, configuration $config")
            if (debug)
                println(s"### configuration $config")
            val configuredvtokens = extractTokensForConfig(vresult, config)
            if (debug)
                println(s"expecting $configuredvtokens")


            val result = lex(file, inclDirectory, debug, ignoreWarnings, config.map(f => (f.feature -> "1")).toMap, (features -- config).map(_.feature))

            assert(result.isInstanceOf[One[_]], "received conditional result when executing a single configuration??")
            val tokens = getTokensFromResult(result.asInstanceOf[One[LexerResult]].value)

            compareTokenLists(configuredvtokens, tokens, config, false)

            //compare against CPP
            status(s"comparing against cpp, configuration $config")
            val cppresult: Conditional[LexerFrontend.LexerResult] = tryAgainIfEmpty(() => lexcpp(file, inclDirectory, debug, ignoreWarnings, config.map(f => (f.feature -> "1")).toMap, (features -- config).map(_.feature)), 3)
            assert(cppresult.isInstanceOf[One[_]], "received conditional result when executing a single configuration??")
            val cpptokens = getTokensFromResult(cppresult.asInstanceOf[One[LexerResult]].value)

            compareTokenLists(configuredvtokens, cpptokens, config, true)
        }

    }


    def genAllConfigurations(exprs: Set[SingleFeatureExpr]): List[Set[SingleFeatureExpr]] =
        if (exprs.isEmpty) List(Set())
        else {
            val configs = genAllConfigurations(exprs.tail)
            val head = exprs.head
            //if too many features, just select random values after the first 10 instead of exploring all
            if (exprs.size > 10) {
                if (Math.random() > 0.5) configs else configs.map(_ + head)
            }
            else configs ++ configs.map(_ + head)
        }

    def getInitFeatures(filecontent: String): Set[SingleFeatureExpr] =
        filecontent.split("\n").filter(_ startsWith "// features:").flatMap(
            _.drop(12).split(" ").map(_.trim).filterNot(_.isEmpty)).map(FeatureExprFactory.createDefinedExternal).toSet
    def expectTotalFailure(filecontent: String): Boolean =
        filecontent.split("\n").exists(_ startsWith "// expect cpp failure")

    def extractTokensForConfig(vresult: Conditional[LexerResult], config: Set[SingleFeatureExpr]): List[LexerToken] = {
        val c = config.map(_.feature)
        val configuredvresult = vresult.select(c)
        val configuredvtokens = getTokensFromResult(configuredvresult)
        configuredvtokens.filter(_.getFeature.evaluate(c))
    }

    def getTokensFromResult(result: LexerResult): List[LexerToken] =
        if (result.isInstanceOf[LexerSuccess])
            result.asInstanceOf[LexerSuccess].getTokens.asScala.toList.filter(_.isLanguageToken)
        else {
            List()
        }

    def compareTokenLists(vlist: List[LexerToken], alist: List[LexerToken], config: Set[SingleFeatureExpr], withCPP: Boolean): Unit = {
        val msgWithCPP = if (withCPP) "(cpp)" else "(typechef)"
        lazy val msg = s" in config $config.\n" +
            s"variability-aware lexing: $vlist\n" +
            s"lexing specific config $msgWithCPP: $alist"
        assert(vlist.length == alist.length, "preprocessor produces output of different length" + msg)

        (vlist zip alist).foreach(
            x => assert(x._1.getText == x._2.getText, s"mismatch on token $x" + msg)
        )


    }

    def hasSuccess(conditional: Conditional[LexerResult]): Boolean = conditional.exists({
        case a: LexerSuccess => true
        case _ => false
    })


    protected def status(s: String) = {}


    protected def tryAgainIfEmpty(cmd: () => Conditional[LexerFrontend.LexerResult], nrTries: Int): Conditional[LexerFrontend.LexerResult] = {
        val result = cmd()
        if (nrTries > 1) {
            val r = result.asInstanceOf[One[LexerResult]].value
            var failed = false
            if (r.isInstanceOf[LexerSuccess])
                if (r.asInstanceOf[LexerSuccess].getTokens.asScala.toList.filter(_.isLanguageToken).isEmpty)
                    failed = true
            if (failed)
                return tryAgainIfEmpty(cmd, nrTries-1)
        }
        return result
    }

    protected def lexcpp(file: File,
                         folder: File,
                         debug: Boolean = false,
                         ignoreWarnings: Boolean = true,
                         definedMacros: Map[String, String] = Map(),
                         undefMacros: Set[String] = Set()
                            ): Conditional[LexerFrontend.LexerResult] = {

        val output = new ByteArrayOutputStream()

        import scala.sys.process._

        val cppcmd = "cpp"

        val cmd = cppcmd + " -I " + folder.getAbsolutePath + " " + file.getAbsolutePath + " " + definedMacros.map(v => "-D" + v._1 + "=" + v._2).mkString(" ") + " " + undefMacros.map("-U" + _).mkString(" ")

        var msg = ""
        val isSuccess = cmd #> output ! ProcessLogger(l => msg = msg + "\n" + l)
        if (isSuccess != 0) {
//            System.err.println(msg)
            return One(new LexerError(s"cpp execution failed with value $isSuccess: $msg", "", 0, 0))
        }

        val jcppinput = new ByteArrayInputStream(output.toByteArray)
        lex(new VALexer.StreamSource(jcppinput, "nofile"), false, new File("."), false, Map[String, String](), Set[String]())
    }


    private def getFeatures(initSet: Set[SingleFeatureExpr], conditional: Conditional[LexerResult]): Set[SingleFeatureExpr] = {
        var foundFeatures: Set[SingleFeatureExpr] = Set()
        conditional.foreach(x =>
            if (x.isInstanceOf[LexerSuccess]) x.asInstanceOf[LexerSuccess].getTokens.asScala.foreach(t =>
                foundFeatures ++= t.getFeature.collectDistinctFeatureObjects
            ))

        if (initSet.nonEmpty) {
            assert((foundFeatures subsetOf initSet) || (foundFeatures equals initSet), "features declared in test file, but additional features found; check test file")
            initSet
        } else
            foundFeatures
    }

    private def getFileContent(file: File): String = {
        val source = io.Source.fromFile(file)
        val r = source.mkString
        source.close()
        r
    }


}
