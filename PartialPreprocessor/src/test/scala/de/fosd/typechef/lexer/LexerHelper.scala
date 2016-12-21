package de.fosd.typechef.lexer

import java.io.{File, FileInputStream}
import java.util.Collections

import de.fosd.typechef.VALexer
import de.fosd.typechef.VALexer.StreamSource
import de.fosd.typechef.conditional.Conditional
import de.fosd.typechef.lexer.macrotable.MacroFilter


trait LexerHelper {
    import scala.collection.JavaConverters._

    protected def lex(file: File,
                      incDir: File,
                      debug: Boolean = false,
                      ignoreWarnings: Boolean = true,
                      definedMacros: Map[String, String] = Map(),
                      undefMacros: Set[String] = Set()
                         ): Conditional[LexerFrontend.LexerResult] =
        lex(new StreamSource(new FileInputStream(file),file.getAbsolutePath), debug, incDir, ignoreWarnings, definedMacros, undefMacros)

    protected def lex(source: VALexer.LexerInput, debug: Boolean, folder: File, ignoreWarnings: Boolean,
                      definedMacros: Map[String, String], undefMacros: Set[String]): Conditional[LexerFrontend.LexerResult] = {
        return new LexerFrontend().run(new LexerFrontend.DefaultLexerOptions(source, debug, null) {
            override def isReturnLanguageTokensOnly: Boolean = {
                return false
            }
            override def getIncludePaths: java.util.List[String] = {
                return Collections.singletonList(folder.getAbsolutePath)
            }
            override def isHandleWarningsAsErrors: Boolean = {
                return !ignoreWarnings
            }
            override def getFeatures: java.util.Set[Feature] = {
                val features: java.util.Set[Feature] = new java.util.HashSet[Feature]
                features.add(Feature.DIGRAPHS)
                features.add(Feature.TRIGRAPHS)
                features.add(Feature.LINEMARKERS)
                features.add(Feature.GNUCEXTENSIONS)
                return features
            }
            override def useXtcLexer: Boolean = {
                return useXtc()
            }
            override def getMacroFilter: MacroFilter = {
                return useMacroFilter
            }
            override def getDefinedMacros: java.util.Map[String, String] = {
                return definedMacros.asJava
            }
            override def getUndefMacros: java.util.Set[String] = {
                return undefMacros.asJava
            }
            override def printLexerErrorsToStdErr: Boolean = {
                return false
            }
        }, true)
    }
    protected def useXtc(): Boolean
    protected def useMacroFilter: MacroFilter = return new MacroFilter
}
