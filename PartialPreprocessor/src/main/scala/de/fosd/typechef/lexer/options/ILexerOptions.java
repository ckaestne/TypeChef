package de.fosd.typechef.lexer.options;

import de.fosd.typechef.featureexpr.FeatureModel;
import de.fosd.typechef.lexer.Warning;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ILexerOptions {

    Map<String, String> getDefinedMacros();

    Set<String> getUndefMacros();

    List<String> getSystemIncludePath();

    List<String> getQuoteIncludePath();

    List<String> getMacroFilter();

    List<String> getIncludedHeaders();

    String getOutputName();

    boolean isPrintVersion();

    FeatureModel getFeatureModel();

    Set<Warning> getWarnings();
    
    List<String> getFiles();
}
