package de.fosd.typechef.lexer.options;

import de.fosd.typechef.VALexer;
import de.fosd.typechef.featureexpr.FeatureModel;
import de.fosd.typechef.lexer.Feature;
import de.fosd.typechef.lexer.Warning;
import de.fosd.typechef.lexer.macrotable.MacroFilter;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ILexerOptions {

    Map<String, String> getDefinedMacros();

    Set<String> getUndefMacros();

    List<String> getIncludePaths();

    List<String> getQuoteIncludePath();

    MacroFilter getMacroFilter();

    List<String> getIncludedHeaders();

    String getLexOutputFile();

    Set<Warning> getWarnings();

    boolean isPrintWarnings();

    boolean isPrintLexingSuccess();

    Set<Feature> getFeatures();

    List<VALexer.LexerInput> getInput();

    boolean isLexPrintToStdout();

    boolean printLexerErrorsToStdErr();

    boolean useXtcLexer();


    FeatureModel getSmallFeatureModel();
    FeatureModel getFullFeatureModel();

    PartialConfiguration getLexerPartialConfiguration();

    /**
     * report line numbers of the .pi file instead of the line
     * numbers in the original .c and .h files
     */
    boolean isAdjustLineNumbers();

    /**
     * by default only language tokens (that is, no white space, no tokens
     * representing preprocessor instructions and so forth) are returned
     *
     * for debugging purposes this behavior can be overridden to return
     * also all other tokens
     *
     * @return whether only language tokens are returned (default true)
     */
    boolean isReturnLanguageTokensOnly();

    /**
     * debug facility to handle all warnings strictly as if they were errors
     *
     * used in testing; false by default
     */
    boolean isHandleWarningsAsErrors();
}
