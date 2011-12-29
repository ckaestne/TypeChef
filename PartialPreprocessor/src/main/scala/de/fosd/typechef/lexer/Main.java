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

import de.fosd.typechef.featureexpr.FeatureModel;
import de.fosd.typechef.lexer.macrotable.MacroContext$;
import de.fosd.typechef.lexer.options.ILexerOptions;
import de.fosd.typechef.lexer.options.LexerOptions;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.io.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * (Currently a simple test class).
 */
public class Main {




    public static void main(String[] args) throws Exception {
        (new Main()).run(args, false, true, null);
    }

    public List<Token> run(String[] args, boolean returnTokenList, boolean printToStdOutput, FeatureModel featureModel) throws Exception {
        LexerOptions options = new LexerOptions();
        options.parseOptions(args);
        return run(options);
    }

    public List<Token> run(ILexerOptions options) throws Exception {
        if (options.isPrintVersion()) {
                    version(System.out);
                    return new ArrayList<Token>();
        }



        String outputName = null;



        Preprocessor pp = new Preprocessor(options.getFeatureModel());
        // No sane code uses TRIGRAPHS or DIGRAPHS - at least, no code
        // written with ASCII available!
        //pp.addFeature(Feature.DIGRAPHS);
        //pp.addFeature(Feature.TRIGRAPHS);
        pp.addFeature(Feature.LINEMARKERS);
        pp.addFeature(Feature.INCLUDENEXT);
        pp.addFeature(Feature.GNUCEXTENSIONS);

        pp.setListener(new PreprocessorListener(pp));
        pp.addMacro("__JCPP__", FeatureExprLib.base());

        PrintWriter output = null;
                if (options.getOutputName().equals("$$stdout"))
                    output = new PrintWriter(new OutputStreamWriter(System.out));
                else if (options.getOutputName().length() > 0)  {
                    output = new PrintWriter(new BufferedWriter(new FileWriter(outputName)));
                    pp.openDebugFiles(outputName);
                }

        for (Map.Entry<String,String> macro:options.getDefinedMacros().entrySet())
            pp.addMacro(macro.getKey(), FeatureExprLib.base(),macro.getValue());
        for (String undef:options.getUndefMacros())
                    pp.removeMacro(undef, FeatureExprLib.base());

                    pp.getSystemIncludePath().addAll(options.getSystemIncludePath());
        pp.getQuoteIncludePath().addAll(options.getQuoteIncludePath());
        
        for (String filter:options.getMacroFilter())
        switch (filter.charAt(0)){
            case 'p':
                    MacroContext$.MODULE$.setPrefixFilter(filter.substring(2));
                    break;
                case 'P':
                    MacroContext$.MODULE$.setPostfixFilter(filter.substring(2));
                    break;
                case 'x':
                    MacroContext$.MODULE$.setPrefixOnlyFilter(filter.substring(2));
                    break;
                case '4':
                    MacroContext$.MODULE$.setListFilter(filter.substring(2));
                    break;
    }

        pp.getWarnings().clear();
        pp.addWarnings(options.getWarnings());


        for (String include:options.getIncludedHeaders())
            pp.addInput(new File(include));

//                case 'v':
//                    pp.addFeature(Feature.VERBOSE);
//                    break;
//                case 3:
//                    pp.addFeature(Feature.DEBUG);
//                    break;

        for (String file:options.getFiles())
            pp.addInput(new FileLexerSource(new File(file)));
        if (options.getFiles().isEmpty())
            pp.addInput(new InputLexerSource(System.in));

        if (pp.getFeature(Feature.VERBOSE)) {
            System.err.println("#" + "include \"...\" search starts here:");
            for (String dir : pp.getQuoteIncludePath())
                System.err.println("  " + dir);
            System.err.println("#" + "include <...> search starts here:");
            for (String dir : pp.getSystemIncludePath())
                System.err.println("  " + dir);
            System.err.println("End of search list.");
        }

        List<Token> resultTokenList = new ArrayList<Token>();
        int outputLine = 1;
        try {
            // TokenFilter tokenFilter = new TokenFilter();
            for (; ; ) {
                Token tok = pp.getNextToken();
                if (tok == null)
                    break;
                if (tok.getType() == Token.EOF)
                    break;


                String image = tok.getText();
                while (image.indexOf('\n') >= 0) {
                    outputLine++;
                    image = image.substring(image.indexOf('\n') + 1);
                }

                if (returnTokenList && PartialPPLexer.isResultToken(tok)) {
                    if (tok instanceof SimpleToken)
                        ((SimpleToken) tok).setLine(outputLine);
                    resultTokenList.add(tok);
                }

                if (output != null)
                    tok.lazyPrint(output);
            }
        } catch (Throwable e) {
            Preprocessor.logger.severe(e.toString());
            e.printStackTrace(System.err);
            Source s = pp.getSource();
            while (s != null) {
                System.err.println(" -> " + s);
                s = s.getParent();
            }
        } finally {
            pp.debugWriteMacros();
            if (output != null)
                output.flush();
            if (output != null && !printToStdOutput)
                output.close();
        }
        return resultTokenList;
    }

    private void version(PrintStream out) {
        out.println("Anarres Java C Preprocessor version "
                + Version.getVersion());
        out.println("Copyright (C) 2008 Shevek (http://www.anarres.org/).");
        out
                .println("This is free software; see the source for copying conditions.  There is NO");
        out
                .println("warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.");
    }

    private static String getShortOpts(Option[] opts) throws Exception {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < opts.length; i++) {
            char c = (char) opts[i].getVal();
            if (!Character.isLetterOrDigit(c))
                continue;
            for (int j = 0; j < buf.length(); j++)
                if (buf.charAt(j) == c)
                    throw new Exception("Duplicate short option " + c);
            buf.append(c);
            switch (opts[i].getHasArg()) {
                case LongOpt.NO_ARGUMENT:
                    break;
                case LongOpt.OPTIONAL_ARGUMENT:
                    buf.append("::");
                    break;
                case LongOpt.REQUIRED_ARGUMENT:
                    buf.append(":");
                    break;
            }
        }
        return buf.toString();
    }

    private static void usage(String command, Option[] options) {
        StringBuilder text = new StringBuilder("Usage: ");
        text.append(command).append('\n');
        for (int i = 0; i < options.length; i++) {
            StringBuilder line = new StringBuilder();
            Option opt = options[i];
            line.append("    --").append(opt.getName());
            switch (opt.getHasArg()) {
                case LongOpt.NO_ARGUMENT:
                    break;
                case LongOpt.OPTIONAL_ARGUMENT:
                    line.append("[=").append(opt.eg).append(']');
                    break;
                case LongOpt.REQUIRED_ARGUMENT:
                    line.append('=').append(opt.eg);
                    break;
            }
            if (Character.isLetterOrDigit(opt.getVal()))
                line.append(" (-").append((char) opt.getVal()).append(")");
            if (line.length() < 30) {
                while (line.length() < 30)
                    line.append(' ');
            } else {
                line.append('\n');
                for (int j = 0; j < 30; j++)
                    line.append(' ');
            }
            /* This should use wrap. */
            line.append(opt.help);
            line.append('\n');
            text.append(line);
        }

        System.out.println(text);
    }

}
