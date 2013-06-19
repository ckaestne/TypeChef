/*
 * TypeChef Variability-Aware Lexer.
 * Copyright 2010-2011, Christian Kaestner, Paolo Giarrusso
 * Licensed under GPL 3.0
 *
 * built on top of
 *
 * Anarres C Preprocessor
 * Copyright (c) 2007-2008, Shevek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package de.fosd.typechef.lexer;

import de.fosd.typechef.LexerToken;
import de.fosd.typechef.VALexer;
import de.fosd.typechef.featureexpr.FeatureModel;
import de.fosd.typechef.lexer.macrotable.MacroFilter;
import de.fosd.typechef.lexer.options.ILexerOptions;
import de.fosd.typechef.lexer.options.PartialConfiguration;
import de.fosd.typechef.xtclexer.XtcPreprocessor;

import java.io.*;
import java.util.*;

/**
 * (Currently a simple test class).
 */
public class Main {


    //    public static void main(String[] args) throws Exception {
//        (new Main()).run(args, false, true, null);
//    }
//

    /**
     * shorthand with few default options, avoiding all command-line parsing
     */
    public List<LexerToken> run(final File targetFile,
                                final boolean returnTokenList,
                                final boolean printToStdOutput,
                                final FeatureModel featureModel) throws Exception {
        @SuppressWarnings("unchecked")
        ILexerOptions options = new ILexerOptions() {

            @Override
            public Map<String, String> getDefinedMacros() {
                return Collections.EMPTY_MAP;
            }

            @Override
            public Set<String> getUndefMacros() {
                return Collections.EMPTY_SET;
            }

            @Override
            public List<String> getIncludePaths() {
                return Collections.EMPTY_LIST;
            }

            @Override
            public List<String> getQuoteIncludePath() {
                return Collections.EMPTY_LIST;
            }

            @Override
            public MacroFilter getMacroFilter() {
                return new MacroFilter();
            }

            @Override
            public List<String> getIncludedHeaders() {
                return Collections.EMPTY_LIST;
            }

            @Override
            public String getLexOutputFile() {
                return null;
            }

            @Override
            public boolean isPrintVersion() {
                return false;
            }

            @Override
            public Set<Warning> getWarnings() {
                return Collections.EMPTY_SET;
            }

            @Override
            public Set<Feature> getFeatures() {
                return Collections.EMPTY_SET;
            }

            @Override
            public List<String> getFiles() {
                return Collections.singletonList(targetFile.getAbsolutePath());
            }

            @Override
            public boolean isLexPrintToStdout() {
                return printToStdOutput;
            }

            @Override
            public boolean useXtcLexer() {
                return false;
            }

            @Override
            public FeatureModel getLexerFeatureModel() {
                return featureModel;
            }

            @Override
            public PartialConfiguration getLexerPartialConfiguration() {
                return null;
            }

            @Override
            public boolean isAdjustLineNumbers() {
                return true;
            }
        };
        return run(options, returnTokenList);
    }

    public List<LexerToken> run(final ILexerOptions options, boolean returnTokenList) throws Exception {
        return run(new VALexer.LexerFactory() {
            @Override
            public VALexer create(FeatureModel featureModel) {
                if (options.useXtcLexer())
                    return new XtcPreprocessor(options.getMacroFilter(), featureModel);
                return new Preprocessor(options.getMacroFilter(), featureModel);
            }
        }, options, returnTokenList);
    }

    public List<LexerToken> run(VALexer.LexerFactory lexerFactory, ILexerOptions options, boolean returnTokenList) throws Exception {
        if (options.isPrintVersion()) {
            version(System.out);
            return new ArrayList<LexerToken>();
        }


        VALexer pp = lexerFactory.create(options.getLexerFeatureModel());

        for (Warning w : options.getWarnings())
            pp.addWarning(w);
        for (Feature f : options.getFeatures())
            pp.addFeature(f);

        pp.setListener(new PreprocessorListener(pp));
        pp.addMacro("__TYPECHEF__", FeatureExprLib.True());

        PrintWriter output = null;
        if (options.getLexOutputFile().length() > 0) {
            output = new PrintWriter(new BufferedWriter(new FileWriter(options.getLexOutputFile())));
            pp.openDebugFiles(options.getLexOutputFile());
        } else if (options.isLexPrintToStdout())
            output = new PrintWriter(new OutputStreamWriter(System.out));

        if (options.getLexerPartialConfiguration() != null) {
            for (String def : options.getLexerPartialConfiguration().getDefinedFeatures())
                pp.addMacro(def, FeatureExprLib.True(), "1");
            for (String undef : options.getLexerPartialConfiguration().getUndefinedFeatures())
                pp.removeMacro(undef, FeatureExprLib.True());
        }
        for (Map.Entry<String, String> macro : options.getDefinedMacros().entrySet())
            pp.addMacro(macro.getKey(), FeatureExprLib.True(), macro.getValue());
        for (String undef : options.getUndefMacros())
            pp.removeMacro(undef, FeatureExprLib.True());

        for (String sysInclPath : options.getIncludePaths())
            pp.addSystemIncludePath(sysInclPath);
        for (String quoInclPath : options.getQuoteIncludePath())
            pp.addQuoteIncludePath(quoInclPath);


        for (String include : options.getIncludedHeaders())
            pp.addInput(new VALexer.FileSource(new File(include)));


        for (String file : options.getFiles())
            pp.addInput(new VALexer.FileSource(new File(file)));
        if (options.getFiles().isEmpty())
            pp.addInput(new VALexer.StreamSource(System.in, "<console>"));

        if (options.getFeatures().contains(Feature.DEBUG_INCLUDEPATH)) {
            System.err.println("#" + "include \"...\" search starts here:");
            for (String dir : pp.getQuoteIncludePath())
                System.err.println("  " + dir);
            System.err.println("#" + "include <...> search starts here:");
            for (String dir : pp.getSystemIncludePath())
                System.err.println("  " + dir);
            System.err.println("End of search list.");
        }

        List<LexerToken> resultTokenList = new ArrayList<LexerToken>();
        int outputLine = 1;
        try {
            // TokenFilter tokenFilter = new TokenFilter();
            for (; ; ) {
                LexerToken tok = pp.getNextToken();
                if (tok == null)
                    break;
                if (tok.isEOF())
                    break;


                if (returnTokenList && tok.isLanguageToken()) {
                    resultTokenList.add(tok);
                }

                if (output != null) {
                    if (options.isAdjustLineNumbers()) {
                        //adjust line numbers to .pi file for debugging
                        String image = tok.getText();
                        while (image.indexOf('\n') >= 0) {
                            outputLine++;
                            image = image.substring(image.indexOf('\n') + 1);
                        }
                        tok.setLine(outputLine);
                        if (options.getLexOutputFile() != null)
                            tok.setSourceName(options.getLexOutputFile());
                    }

                    //write to .pi file
                    tok.lazyPrint(output);
                }
            }
        } catch (Throwable e) {
            Preprocessor.logger.severe(e.toString());
            e.printStackTrace(System.err);
            pp.printSourceStack(System.err);
        } finally {
            pp.debugPreprocessorDone();
            if (output != null)
                output.flush();
            if (output != null && !options.isLexPrintToStdout())
                output.close();
        }
        return resultTokenList;
    }

    private void version(PrintStream out) {
        out.println("TypeChef " + Version.getVersion());
        out.println("This is free software.  There is NO warranty.");
    }


}
