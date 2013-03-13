package de.fosd.typechef.xtclexer;


import de.fosd.typechef.VALexer;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;
import de.fosd.typechef.featureexpr.FeatureModel;
import de.fosd.typechef.lexer.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import de.fosd.typechef.lexer.macrotable.MacroFilter;
import net.sf.javabdd.BDD;
import xtc.LexerInterface;
import xtc.XtcMacroFilter;
import xtc.lang.cpp.*;
import xtc.lang.cpp.Preprocessor;
import xtc.tree.Locatable;
import xtc.tree.Location;


/**
 * wrapper class around the Xtc/SuperC preprocessor to have a somewhat compatible interface with
 * the original Typechef Preprocessor
 */
public class XtcPreprocessor implements VALexer {


    private File file = null;//file is just the name for the file reader. file and fileReader should be null or nonnull at the same time
    private Reader fileReader = null;
    private List<String> sysIncludes = new ArrayList<String>();
    private List<String> I = new ArrayList<String>();
    private List<String> iquIncludes = new ArrayList<String>();
    private PreprocessorListener listener;
    private StringBuilder commandLine = new StringBuilder();
    private final XtcMacroFilter macroFilter;
    private final FeatureModel featureModel;
    private LexerInterface.ErrorHandler exceptionErrorHandler = new LexerInterface.ExceptionErrorHandler() {
        public void error(PresenceConditionManager.PresenceCondition pc, String msg, Locatable location) {
            FeatureExpr fexpr = translate(pc);
            if (fexpr.isSatisfiable(featureModel))
                super.error(pc, msg, location);
        }
    };

    public XtcPreprocessor(final MacroFilter tcMacroFilter, FeatureModel featureModel) {
        this.macroFilter = new XtcMacroFilter() {
            @Override
            public boolean isVariable(String macroName) {
                return tcMacroFilter.flagFilter(macroName);
            }
        };
        this.featureModel = featureModel;
    }


    @Override
    public void addInput(LexerInput source) throws IOException {
        if (source instanceof FileSource) {
            commandLine.append("#include \"" + ((FileSource) source).file.getAbsolutePath() + "\"\n");
        } else if (source instanceof StreamSource) {
            assert file == null : "can support only one file at a time, previously set: " + file;
            file = new File(((StreamSource) source).filename);
            fileReader = new InputStreamReader(((StreamSource) source).inputStream);
        } else if (source instanceof TextSource)
            commandLine.append(((TextSource) source).code);
        else
            throw new RuntimeException("unexpected input");
    }

    @Override
    public void debugPreprocessorDone() {
        //nothing to do
    }

    @Override
    public void addFeature(Feature digraphs) {
        //nothing to do (no configuration accepted)
    }

    @Override
    public void addWarning(Warning warning) {
        //ignore
    }


    @Override
    public void addMacro(String macro, FeatureExpr fexpr) {
        wrapFExpr("#define " + macro + "\n", fexpr);
    }

    @Override
    public void addMacro(String macro, FeatureExpr fexpr, String value) throws LexerException {
        wrapFExpr("#define " + macro + " " + value + "\n", fexpr);
    }

    @Override
    public void removeMacro(String macro, FeatureExpr fexpr) {
        wrapFExpr("#undef " + macro + "\n", fexpr);
    }


    private void wrapFExpr(String command, FeatureExpr fexpr) {
        if (!fexpr.isTautology())
            commandLine.append("#if " + fexpr.toTextExpr().replace("definedEx(", "defined(") + "\n");

        commandLine.append(command);

        if (!fexpr.isTautology())
            commandLine.append("#endif\n");
    }

    @Override
    public void addSystemIncludePath(String folder) {
        sysIncludes.add(folder);
    }

    @Override
    public void addQuoteIncludePath(String folder) {
        iquIncludes.add(folder);
    }

    @Override
    public List<String> getSystemIncludePath() {
        return sysIncludes;
    }

    @Override
    public List<String> getQuoteIncludePath() {
        return iquIncludes;
    }


    List<Stream> lexers = null;
    Stack<FeatureExpr> stack;

    private List<Stream> getCurrentLexer() throws FileNotFoundException {
        if (lexers == null) {
            assert (file == null) == (fileReader == null) : "no file given";

            if (file != null)
                I.add(file.getParentFile().getAbsolutePath());
            lexers = LexerInterface.createLexer(commandLine.toString(), fileReader, file, exceptionErrorHandler, iquIncludes, I, sysIncludes, macroFilter);
            stack = new Stack<FeatureExpr>();
            stack.push(FeatureExprFactory.True());
        }
        return lexers;
    }



    @Override
    public Token getNextToken() throws IOException {
        Stream lexer = getCurrentLexer().get(0);
        Syntax s = lexer.scan();
        while (s.kind() != Syntax.Kind.EOF) {
            if (s.kind() == Syntax.Kind.CONDITIONAL) {
                Syntax.Conditional c = s.toConditional();
                if (c.tag() == Syntax.ConditionalTag.START) {
                    stack.push(stack.peek().and(translate(c.presenceCondition())));
//                    System.out.println("#if " + stack.peek());
                } else if (c.tag() == Syntax.ConditionalTag.NEXT) {
                    stack.pop();
                    stack.push(stack.peek().and(translate(c.presenceCondition())));
//                    System.out.println("#elif " + stack.peek());
                } else {
                    stack.pop();
//                    System.out.println("#endif");
                }
            }


            if (s.kind() == Syntax.Kind.CONDITIONAL)
                return new XtcToken(s, stack.peek());
            Boolean visible=stack.peek().isSatisfiable();
            if (visible) {
                if (s.kind() == Syntax.Kind.LANGUAGE)
                    return new XtcToken(s, stack.peek());
                if (s.kind() == Syntax.Kind.LAYOUT)
                    return new XtcToken(s, stack.peek());
            }

            s = lexer.scan();
        }
        getCurrentLexer().remove(0);
        if (getCurrentLexer().isEmpty())
            return new SimpleToken(Token.EOF, 0, 0, "EOF", null);
        else
            return getNextToken();
    }

    @Override
    public void setListener(PreprocessorListener preprocessorListener) {
        listener = preprocessorListener;
    }

    @Override
    public void printSourceStack(PrintStream err) {
        //nothing to do, specific to typechef
    }

    @Override
    public void openDebugFiles(String lexOutputFile) {
        //nothing to do, specific to typechef
    }


    public static FeatureExpr translate(PresenceConditionManager.PresenceCondition pc) {

        BDD bdd = pc.getBDD();

        List allsat;
        boolean firstTerm;

        if (bdd.isOne())
            return FeatureExprLib.True();
        if (bdd.isZero()) return FeatureExprLib.False();

        allsat = (List) bdd.allsat();

        FeatureExpr result = FeatureExprLib.False();

        firstTerm = true;
        for (Object o : allsat) {
            byte[] sat;
            boolean first;

            FeatureExpr innerResult = FeatureExprLib.True();


            sat = (byte[]) o;
            first = true;
            for (int i = 0; i < sat.length; i++)
                if (sat[i] == 0 || sat[i] == 1) {
                    String fname = pc.getPCManager().vars.getName(i);

                    if (fname.length() > 10 && fname.substring(0, 9).equals("(defined ") && fname.substring(fname.length() - 1).equals(")"))
                        fname = fname.substring(9, fname.length() - 1);
                    else
                        fname = formulaToName(fname);
                    FeatureExpr var = FeatureExprLib.l().createDefinedExternal(fname);
                    if (sat[i] == 0) var = var.not();

                    innerResult = innerResult.and(var);
                }

            result = result.or(innerResult);
        }
        return result;


    }

    private static String formulaToName(String fname) {
        String res = "_" + fname.replace(' ', '_').
                replace(">=", "_ge_").replace("<=", "_le_").
                replace(">", "_gt_").replace("<", "_lt_").
                replace("=", "_eq_").
                replace("<<", "_shiftleft_").replace(">>", "_shiftright_").
                replace("(", "").replace(")", "").
                replace("+", "_plus_").replace("-", "_minus_");

        System.err.println("Warning: depending on external macro definition: " + fname + " -> " + res);

        return res;  //To change body of created methods use File | Settings | File Templates.
    }

    public static class XtcToken extends Token {
        public XtcToken(Syntax t, FeatureExpr f) {
            xtcToken = t;
            fexpr = f;
        }

        Syntax xtcToken;
        FeatureExpr fexpr;

        @Override
        public int getType() {
            if (xtcToken.kind() == Syntax.Kind.CONDITIONAL)
                return Token.P_IF;
            if (xtcToken.kind() == Syntax.Kind.LAYOUT)
                return Token.WHITESPACE;
            if (xtcToken.kind() == Syntax.Kind.EOF)
                return Token.EOF;
            if (xtcToken.kind() == Syntax.Kind.DIRECTIVE)
                return Token.P_LINE;
            if (xtcToken.kind() == Syntax.Kind.ERROR)
                return Token.P_LINE;
            if (xtcToken.kind() == Syntax.Kind.MARKER)
                return Token.P_LINE;
            if (xtcToken.kind() == Syntax.Kind.LANGUAGE) {
                //TODO this is unnecessarily slow and stupid. check on demand
                if (xtcToken.getTokenText().startsWith("'"))
                    return Token.CHARACTER;
                if (xtcToken.getTokenText().startsWith("\""))
                    return Token.STRING;
                try {
                    Double.valueOf(xtcToken.getTokenText());
                    return Token.INTEGER;
                } catch (NumberFormatException e){
                }
                return Token.IDENTIFIER;
            }
            if (xtcToken.kind() == Syntax.Kind.PREPROCESSOR)
                return Token.P_LINE;
            throw new RuntimeException("unknown token kind");
        }

        int localLine = Integer.MIN_VALUE;

        @Override
        public int getLine() {
            if (localLine != Integer.MIN_VALUE)
                return localLine;
            if (xtcToken == null || xtcToken.getLocation() == null) return -1;
            return xtcToken.getLocation().line;
        }

        @Override
        public void setLine(int line) {
            localLine = line;
        }

        @Override
        public int getColumn() {
            if (xtcToken == null || xtcToken.getLocation() == null) return -1;
            return xtcToken.getLocation().column;
        }

        @Override
        public String getText() {
//            if (xtcToken.testFlag(Preprocessor.PREV_WHITE))
//                return " "+xtcToken.toString();
            String prefix = "";
            if (xtcToken.kind() == Syntax.Kind.CONDITIONAL) prefix = "\n";
            return prefix + xtcToken.getTokenText();
        }

        @Override
        public Object getValue() {
            return xtcToken;
        }

        @Override
        public FeatureExpr getFeature() {
            return fexpr;
        }

        @Override
        public void setFeature(FeatureExpr expr) {
            fexpr = expr;
        }

        @Override
        public String toString() {
            return getText();
        }

        @Override
        public void setNoFurtherExpansion() {
        }

        @Override
        public boolean mayExpand() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Source getSource() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public String getSourceName() {
            if (xtcToken == null || xtcToken.getLocation() == null) return null;
            return xtcToken.getLocation().file;
        }

        @Override
        public Token clone() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setLocation(int line, int column) {
            throw new UnsupportedOperationException();
        }
    }
}
