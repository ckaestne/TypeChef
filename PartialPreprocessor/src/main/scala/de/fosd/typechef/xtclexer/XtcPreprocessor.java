package de.fosd.typechef.xtclexer;


import de.fosd.typechef.VALexer;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;
import de.fosd.typechef.lexer.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import net.sf.javabdd.BDD;
import xtc.TestLexer;
import xtc.lang.cpp.*;


/**
 * wrapper class around the Xtc/SuperC preprocessor to have a somewhat compatible interface with
 * the original Typechef Preprocessor
 */
public class XtcPreprocessor  implements VALexer {

    /*
            Stream lexer = TestLexer.createLexer(checkFile, new File(filename), new TestLexer.ExceptionErrorHandler());

        //create TypeChef style token stream
        List<Token> result = new ArrayList<Token>();

        Syntax s = lexer.scan();
        Stack<FeatureExpr> stack = new Stack<FeatureExpr>();
        stack.push(FeatureExprFactory.True());
        while (s.kind() != Syntax.Kind.EOF) {
            if (s.kind() == Syntax.Kind.CONDITIONAL) {
                Syntax.Conditional c = s.toConditional();
                if (c.tag() == Syntax.ConditionalTag.START)
                    stack.push(stack.peek().and(translate(c.presenceCondition())));
                else if (c.tag() == Syntax.ConditionalTag.NEXT) {
                    stack.pop();
                    stack.push(stack.peek().and(translate(c.presenceCondition())));
                } else stack.pop();
            }

            if (s.kind() == Syntax.Kind.LANGUAGE)
                result.add(new XtcToken(s, stack.peek()));

            s = lexer.scan();
        }
        return result;

     */


    private File file=null;
    private Reader fileReader=null;
    private List<String> sysIncludes=new ArrayList<String>();
    private PreprocessorListener listener;


    @Override
    public void addInput(LexerInput source) throws IOException {
        if (source instanceof FileSource) {
            assert file==null: "File already set";
            file=                        ((FileSource)source).file;
            fileReader=new FileReader(file);
        }
        else if (source instanceof StreamSource) {
            file=new File(((StreamSource)source).filename);
            fileReader=new InputStreamReader(((StreamSource)source).inputStream);
        }
//        else if (source instanceof TextSource)
//            addInput(new StringLexerSource(((TextSource)source).code,true));
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
    public void addWarnings(Collection<Warning> warnings) {
        //nothing to do (no configuration accepted)
    }

    @Override
    public void addMacro(String macro, FeatureExpr pc) {
        //TODO not supported yet, possibly fake as prescript
    }

    @Override
    public void addSystemIncludePath(String folder) {
        sysIncludes.add(folder);
    }


    Stream lexer =null;
    Stack<FeatureExpr> stack ;
    private Stream getLexer() throws FileNotFoundException {
        if (lexer==null) {
            assert file!=null:"no file given";
                lexer = TestLexer.createLexer(fileReader, file, new TestLexer.ExceptionErrorHandler());
                stack= new Stack<FeatureExpr>();
                stack.push(FeatureExprFactory.True());
        }
        return lexer;
    }

    @Override
    public Token getNextToken() throws IOException {
        Syntax s = getLexer().scan();
        while (s.kind() != Syntax.Kind.EOF) {
            if (s.kind() == Syntax.Kind.CONDITIONAL) {
                Syntax.Conditional c = s.toConditional();
                if (c.tag() == Syntax.ConditionalTag.START)
                    stack.push(stack.peek().and(translate(c.presenceCondition())));
                else if (c.tag() == Syntax.ConditionalTag.NEXT) {
                    stack.pop();
                    stack.push(stack.peek().and(translate(c.presenceCondition())));
                } else stack.pop();
            }

            if (s.kind() == Syntax.Kind.LANGUAGE)
                return new XtcToken(s, stack.peek());
            if (s.kind() == Syntax.Kind.LAYOUT)
                return new XtcToken(s, stack.peek());

            s = lexer.scan();
        }
        return new SimpleToken(Token.EOF,0,0,"EOF",null);
    }

    @Override
    public void setListener(PreprocessorListener preprocessorListener) {
        listener=preprocessorListener;
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
            for (int i = 0; i < sat.length; i++) if (sat[i] == 0 || sat[i] == 1) {
                String fname =pc.getPCManager().vars.getName(i);
                fname=fname.substring(9,fname.length()-1);
                FeatureExpr var = FeatureExprLib.l().createDefinedExternal(fname);
                if (sat[i] == 0) var = var.not();

                innerResult = innerResult.and(var);
            }

            result = result.or(innerResult);
        }
        return result;


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
            return 0;
        }

        @Override
        public int getLine() {
            return xtcToken.getLocation().line;
        }

        @Override
        public int getColumn() {
            return xtcToken.getLocation().column;
        }

        @Override
        public String getText() {
//            if (xtcToken.kind()== Syntax.Kind.LANGUAGE)
//                return xtcToken.toLanguage().getTokenText();
//            else if (xtcToken.kind()== Syntax.Kind.LAYOUT)
//                return xtcToken.toLayout().getTokenText();
//            return "<unknown>";
            if (xtcToken.testFlag(xtc.lang.cpp.Preprocessor.PREV_WHITE))
                return " "+xtcToken.toString();
            return xtcToken.toString();
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
            throw new UnsupportedOperationException();
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
