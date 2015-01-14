package de.fosd.typechef.xtclexer;


import de.fosd.typechef.LexerToken;
import de.fosd.typechef.VALexer;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;
import de.fosd.typechef.featureexpr.FeatureModel;
import de.fosd.typechef.lexer.*;
import de.fosd.typechef.lexer.macrotable.MacroFilter;
import net.sf.javabdd.BDD;
import xtc.LexerInterface;
import xtc.XtcMacroFilter;
import xtc.lang.cpp.CTag;
import xtc.lang.cpp.PresenceConditionManager;
import xtc.lang.cpp.Stream;
import xtc.lang.cpp.Syntax;
import xtc.tree.Locatable;

import java.io.*;
import java.util.*;


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


    private LexerInterface.ErrorHandler exceptionErrorHandler = new LexerInterface.ErrorHandler() {
        final LexerInterface.ErrorHandler defaultErrorHandler = new LexerInterface.ExceptionErrorHandler();

        @Override
        public void error(PresenceConditionManager.PresenceCondition pc, String msg, Locatable location) {
            //delegate to TypeChef error handler, unless none is registered; then fallback to the original Lexer error handler
            if (listener == null)
                defaultErrorHandler.error(pc, msg, location);

            String file = (location.hasLocation() ? location.getLocation().file : "");
            int line = (location.hasLocation() ? location.getLocation().line : -1);
            int col = (location.hasLocation() ? location.getLocation().column : -1);

            try {
                listener.handleError(file, line, col, msg, translate(pc));
            } catch (LexerException e) {
                throw new RuntimeException(msg, e);
            }
        }
    };

//            new LexerInterface.ExceptionErrorHandler() {
//        public void error(PresenceConditionManager.PresenceCondition pc, String msg, Locatable location) {
//            FeatureExpr fexpr = translate(pc);
//            if (fexpr.isSatisfiable(featureModel))
//                super.error(pc, msg, location);
//        }
//    };

    public XtcPreprocessor(final MacroFilter tcMacroFilter, FeatureModel featureModel) {
        this.macroFilter = new XtcMacroFilter() {
            @Override
            public boolean isVariable(String macroName) {
                return tcMacroFilter.flagFilter(macroName);
            }
        };
        this.featureModel = featureModel;

        try {
            this.addMacro("__DATE__", FeatureExprFactory.True(), String.format(Locale.US, "\"%1$tb %1$2te %1$tY\"", Calendar.getInstance()));
            this.addMacro("__TIME__", FeatureExprFactory.True(), String.format(Locale.US, "\"%1$tT\"", Calendar.getInstance()));
        } catch (LexerException e) {
            //won't happen
        }
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


    List<Iterator<Syntax>> lexers = null;
    Stack<FeatureExpr> stack;

    private List<Iterator<Syntax>> getCurrentLexer() throws FileNotFoundException {
        if (lexers == null) {
            assert (file == null) == (fileReader == null) : "no file given";

            if (file != null && file.getParentFile()!=null)
                I.add(file.getParentFile().getAbsolutePath());
            lexers = LexerInterface.createLexer(commandLine.toString(), fileReader, file, exceptionErrorHandler, iquIncludes, I, sysIncludes, macroFilter);
            stack = new Stack<FeatureExpr>();
            stack.push(FeatureExprFactory.True());
        }
        return lexers;
    }


    @Override
    public LexerToken getNextToken() throws IOException {
        Iterator<Syntax> lexer = getCurrentLexer().get(0);
        Syntax s = lexer.next();
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
            Boolean visible = stack.peek().isSatisfiable();
            if (visible) {
                if (s.kind() == Syntax.Kind.LANGUAGE)
                    return new XtcToken(s, stack.peek());
                if (s.kind() == Syntax.Kind.LAYOUT)
                    return new XtcToken(s, stack.peek());
            }

            s = lexer.next();
        }
        getCurrentLexer().remove(0);
        if (getCurrentLexer().isEmpty())
            return new EOFToken();
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

                    FeatureExpr var;
                    if (fname.length() > 10 && fname.substring(0, 9).equals("(defined ") && fname.substring(fname.length() - 1).equals(")"))
                        var = FeatureExprLib.l().createDefinedExternal(fname.substring(9, fname.length() - 1));
                    else
                        var = new XtcFExprAnalyzer().resolveFExpr(fname);
                    if (sat[i] == 0) var = var.not();

                    innerResult = innerResult.and(var);
                }

            result = result.or(innerResult);
        }
        return result;


    }


    public static class XtcToken implements LexerToken {

        public XtcToken(Syntax t, FeatureExpr f) {
            xtcToken = t;
            fexpr = f;
            if (xtcToken == null || xtcToken.getLocation() == null) sourceStr = null;
            else sourceStr = xtcToken.getLocation().file;
        }

        Syntax xtcToken;
        FeatureExpr fexpr;
        String sourceStr;


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


//        @Override
//        public int getType() {
//            if (xtcToken.kind() == Syntax.Kind.CONDITIONAL)
//                return Token.P_IF;
//            if (xtcToken.kind() == Syntax.Kind.LAYOUT)
//                return Token.WHITESPACE;
//            if (xtcToken.kind() == Syntax.Kind.EOF)
//                return Token.EOF;
//            if (xtcToken.kind() == Syntax.Kind.DIRECTIVE)
//                return Token.P_LINE;
//            if (xtcToken.kind() == Syntax.Kind.ERROR)
//                return Token.P_LINE;
//            if (xtcToken.kind() == Syntax.Kind.MARKER)
//                return Token.P_LINE;
//            if (xtcToken.kind() == Syntax.Kind.LANGUAGE) {
//                //TODO this is unnecessarily slow and stupid. check on demand
//                if (xtcToken.toLanguage().tag()==CTag.CHARACTERconstant)
//                    return Token.CHARACTER;
//                if (xtcToken.toLanguage().tag()==CTag.STRINGliteral)
//                    return Token.STRING;
//                if (xtcToken.toLanguage().tag()==CTag.INTEGERconstant)
//                    return Token.INTEGER;
//                if (xtcToken.toLanguage().tag()==CTag.IDENTIFIER ||
//                        xtcToken.toLanguage().tag()==CTag.__BUILTIN_VA_LIST)
//                    return Token.IDENTIFIER;
//                return 0;
//            }
//            if (xtcToken.kind() == Syntax.Kind.PREPROCESSOR)
//                return Token.P_LINE;
//            throw new RuntimeException("unknown token kind");
//        }

        @Override
        public boolean isLanguageToken() {
            return xtcToken.kind() == Syntax.Kind.LANGUAGE &&
                    !xtcToken.toLanguage().getTokenText().equals("__extension__");
        }

        @Override
        public boolean isEOF() {
            return xtcToken.kind() == Syntax.Kind.EOF;
        }

        /**
         * is a language identifier (or type in C)
         * <p/>
         * essentially only excludes brackets, commas, literals, and such
         */
        @Override
        public boolean isKeywordOrIdentifier() {
            return isLanguageToken() && (xtcToken.toLanguage().tag() == CTag.IDENTIFIER ||
                    xtcToken.toLanguage().tag() == CTag.TYPEDEFname ||
                    keywords.contains(xtcToken.toLanguage().tag()));

        }

        static Set<CTag> keywords = new HashSet<CTag>();

        static {
            keywords.add(CTag.AUTO);
            keywords.add(CTag.BREAK);
            keywords.add(CTag.CASE);
            keywords.add(CTag.CHAR);
            keywords.add(CTag.CONST);
            keywords.add(CTag.CONTINUE);
            keywords.add(CTag.DEFAULT);
            keywords.add(CTag.DO);
            keywords.add(CTag.DOUBLE);
            keywords.add(CTag.ELSE);
            keywords.add(CTag.ENUM);
            keywords.add(CTag.EXTERN);
            keywords.add(CTag.FLOAT);
            keywords.add(CTag.FOR);
            keywords.add(CTag.GOTO);
            keywords.add(CTag.IF);
            keywords.add(CTag.INT);
            keywords.add(CTag.LONG);
            keywords.add(CTag.REGISTER);
            keywords.add(CTag.RETURN);
            keywords.add(CTag.SHORT);
            keywords.add(CTag.SIGNED);
            keywords.add(CTag.SIZEOF);
            keywords.add(CTag.STATIC);
            keywords.add(CTag.STRUCT);
            keywords.add(CTag.SWITCH);
            keywords.add(CTag.TYPEDEF);
            keywords.add(CTag.UNION);
            keywords.add(CTag.UNSIGNED);
            keywords.add(CTag.VOID);
            keywords.add(CTag.VOLATILE);
            keywords.add(CTag.WHILE);
            keywords.add(CTag._BOOL);
            keywords.add(CTag._COMPLEX);
            keywords.add(CTag.INLINE);
            keywords.add(CTag.__ALIGNOF);
            keywords.add(CTag.__ALIGNOF__);
            keywords.add(CTag.ASM);
            keywords.add(CTag.__ASM);
            keywords.add(CTag.__ASM__);
            keywords.add(CTag.__ATTRIBUTE);
            keywords.add(CTag.__ATTRIBUTE__);
            keywords.add(CTag.__BUILTIN_OFFSETOF);
            keywords.add(CTag.__BUILTIN_TYPES_COMPATIBLE_P);
            keywords.add(CTag.__BUILTIN_VA_ARG);
            keywords.add(CTag.__BUILTIN_VA_LIST);
            keywords.add(CTag.__COMPLEX__);
            keywords.add(CTag.__CONST);
            keywords.add(CTag.__CONST__);
            keywords.add(CTag.__EXTENSION__);
            keywords.add(CTag.__INLINE);
            keywords.add(CTag.__INLINE__);
            keywords.add(CTag.__LABEL__);
            keywords.add(CTag.__RESTRICT);
            keywords.add(CTag.__RESTRICT__);
            keywords.add(CTag.__SIGNED);
            keywords.add(CTag.__SIGNED__);
            keywords.add(CTag.__THREAD);
            keywords.add(CTag.TYPEOF);
            keywords.add(CTag.__TYPEOF);
            keywords.add(CTag.__TYPEOF__);
            keywords.add(CTag.__VOLATILE);
            keywords.add(CTag.__VOLATILE__);
        }

        @Override
        public boolean isNumberLiteral() {
            return isLanguageToken() && (
                    xtcToken.toLanguage().tag() == CTag.INTEGERconstant ||
                            xtcToken.toLanguage().tag() == CTag.OCTALconstant ||
                            xtcToken.toLanguage().tag() == CTag.HEXconstant ||
                            xtcToken.toLanguage().tag() == CTag.FLOATINGconstant);
        }

        @Override
        public boolean isStringLiteral() {
            return isLanguageToken() && xtcToken.toLanguage().tag() == CTag.STRINGliteral;
        }

        @Override
        public boolean isCharacterLiteral() {
            return isLanguageToken() && xtcToken.toLanguage().tag() == CTag.CHARACTERconstant;
        }

        /**
         * "Lazily print" this token, i.e. print it without constructing a full in-memory representation. This is just a
         * default implementation, override it for tokens with a potentially huge string representation.
         *
         * @param writer The { @link java.io.PrintWriter} to print onto.
         */
        @Override
        public void lazyPrint(PrintWriter writer) {
            writer.write(getText());
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
        public FeatureExpr getFeature() {
            return fexpr;
        }

        @Override
        public void setFeature(FeatureExpr fexpr) {
            this.fexpr = fexpr;
        }

        @Override
        public String toString() {
            return getText();
        }


        @Override
        public String getSourceName() {
            return sourceStr;
        }

        @Override
        public void setSourceName(String src) {
            this.sourceStr = src;
        }


    }

    @Override
    public FeatureModel getFeatureModel() {
        return featureModel;
    }
}
