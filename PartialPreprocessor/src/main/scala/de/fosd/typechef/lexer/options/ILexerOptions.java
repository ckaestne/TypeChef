package de.fosd.typechef.lexer.options;

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

    boolean isPrintVersion();


    Set<Warning> getWarnings();

    Set<Feature> getFeatures();

    List<String> getFiles();

    boolean isLexPrintToStdout();

    boolean useXtcLexer();


    FeatureModel getLexerFeatureModel();

    PartialConfiguration getLexerPartialConfiguration();

    /**
     * report line numbers of the .pi file instead of the line
     * numbers in the original .c and .h files
     */
    boolean isAdjustLineNumbers();
}
