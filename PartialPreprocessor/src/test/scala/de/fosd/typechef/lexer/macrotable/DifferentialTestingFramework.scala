package de.fosd.typechef.lexer.macrotable

import java.io._
import java.net.{URISyntaxException, URL, URI}
import java.util
import java.util.Collections

import de.fosd.typechef.{LexerToken, VALexer}
import de.fosd.typechef.conditional.{One, Conditional}
import de.fosd.typechef.featureexpr.{FeatureExprFactory, SingleFeatureExpr}
import de.fosd.typechef.lexer.LexerFrontend.{LexerError, LexerSuccess, LexerResult}
import de.fosd.typechef.lexer.{Feature, LexerFrontend, LexerException}
import org.junit.Assert
import org.scalatest.FunSuite

import scala.io.Source

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
trait DifferentialTestingFramework extends LexerHelper  {

    import scala.collection.JavaConversions._


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

    def getInitFeatures(s: String): Set[SingleFeatureExpr] =
        s.split("\n").filter(_ startsWith "// features:").flatMap(
            _.drop(12).split(" ").map(_.trim).filterNot(_.isEmpty)).map(FeatureExprFactory.createDefinedExternal).toSet

    def extractTokensForConfig(vresult: Conditional[LexerResult], config: Set[SingleFeatureExpr]): List[LexerToken] = {
        val c = config.map(_.feature)
        val configuredvresult = vresult.select(c)
        val configuredvtokens = getTokensFromResult(configuredvresult)
        configuredvtokens.filter(_.getFeature.evaluate(c))
    }

    def getTokensFromResult(result: LexerResult): List[LexerToken] =
        if (result.isInstanceOf[LexerSuccess])
            result.asInstanceOf[LexerSuccess].getTokens.toList.filter(_.isLanguageToken)
        else List()

    def compareTokenLists(vlist: List[LexerToken], alist: List[LexerToken], config: Set[SingleFeatureExpr]): Unit = {
        lazy val msg = s" in config $config.\n" +
            s"variability-aware lexing: $vlist\n" +
            s"lexing specifc config:    $alist"
        assert(vlist.length == alist.length, "preprocessor produces output of different length" + msg)

        (vlist zip alist).foreach(
            x => assert(x._1.getText == x._2.getText, s"mismatch on token $x" + msg)
        )


    }

    protected def status(s: String) = {}

    /**
     * @param filepath
     */
    def analyzeFile(filepath: URL, debug: Boolean = false, ignoreWarnings: Boolean = false): Unit = {
        assert(filepath != null, "filepath parameter null")
        val fileContent = getFileContent(filepath)
        //        assert(firstLine.isDefined, s"file $filepath not found/could not be opened")

        val initFeatures: Set[SingleFeatureExpr] = getInitFeatures(fileContent)


        status(s"lexing all configurations for $filepath")
        val vresult = lex(filepath, getFolder(), debug, ignoreWarnings)
        val features = getFeatures(initFeatures, vresult)
        if (features.size > 10)
            System.err.println("Warning: too many features (%s; 2^%d configurations)\n using random instead of exaustive strategy beyond 10 features".format(features, features.size))

        for (config <- genAllConfigurations(features)) {
            //run same lexer on a single configuration
            status(s"comparing against single config, configuration $config")
            if (debug)
                println(s"### configuration $config")
            val configuredvtokens = extractTokensForConfig(vresult, config)
            if (debug)
                println(s"expecting $configuredvtokens")


            val result = lex(filepath, getFolder(), debug, ignoreWarnings, config.map(f => (f.feature -> "1")).toMap, (features -- config).map(_.feature))

            assert(result.isInstanceOf[One[_]], "received conditional result when executing a single configuration??")
            val tokens = getTokensFromResult(result.asInstanceOf[One[LexerResult]].value)

            compareTokenLists(configuredvtokens, tokens, config)

            //compare against CPP
            status(s"comparing against cpp, configuration $config")
            val cppresult = lexcpp(fileContent, getFolder(), debug, ignoreWarnings, config.map(f => (f.feature -> "1")).toMap, (features -- config).map(_.feature))
            assert(cppresult.isInstanceOf[One[_]], "received conditional result when executing a single configuration??")
            val cpptokens = getTokensFromResult(cppresult.asInstanceOf[One[LexerResult]].value)

            compareTokenLists(configuredvtokens, cpptokens, config)
        }

    }

    protected def lexcpp(fileContent: String,
                         folder: String,
                         debug: Boolean = false,
                         ignoreWarnings: Boolean = true,
                         definedMacros: Map[String, String] = Map(),
                         undefMacros: Set[String] = Set()
                            ): Conditional[LexerFrontend.LexerResult] = {
        //
        //        val tmpInputFile = File.createTempFile("cpptest",".c")
        //        val writer = new FileWriter(tmpInputFile)
        //        writer.write(fileContent)
        //        writer.close()
        val input = new ByteArrayInputStream((fileContent+"\n").getBytes)
        val output = new ByteArrayOutputStream()

        import sys.process._

        val cppcmd = "C:\\Program Files\\tdmgcc\\bin\\cpp.exe"

        val cmd = cppcmd + " -I " + getClass.getResource("/" + folder).toURI.getPath.drop(1) + " " + definedMacros.map(v => "-D" + v._1 + "=" + v._2).mkString(" ") + " " + undefMacros.map("-U" + _).mkString(" ")

        var msg = ""
        val isSuccess = cmd #< input #> output ! ProcessLogger(l => msg = msg + "\n" + l)
        if (isSuccess != 0) {
            System.err.println(msg)
            return One(new LexerError(s"cpp execution failed with value $isSuccess: $msg", "", 0, 0))
        }

        val jcppinput = new ByteArrayInputStream(output.toByteArray)
        lex(new VALexer.StreamSource(jcppinput, "nofile"), false, "", false, Map[String, String](), Set[String]())
    }


    private def getFeatures(initSet: Set[SingleFeatureExpr], conditional: Conditional[LexerResult]): Set[SingleFeatureExpr] = {
        var foundFeatures: Set[SingleFeatureExpr] = Set()
        conditional.foreach(x =>
            if (x.isInstanceOf[LexerSuccess]) x.asInstanceOf[LexerSuccess].getTokens.foreach(t =>
                foundFeatures ++= t.getFeature.collectDistinctFeatureObjects
            ))

        if (initSet.nonEmpty) {
            assert((foundFeatures subsetOf initSet) || (foundFeatures equals initSet), "features declared in test file, but additional features found; check test file")
            initSet
        } else
            foundFeatures
    }

    private def getFileContent(uri: URL): String = {
        val source = Source.fromFile(uri.toURI)
        val r = source.mkString
        source.close()
        r
    }


}
