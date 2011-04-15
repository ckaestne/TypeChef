package org.anarres.cpp;

import de.fosd.typechef.featureexpr.MacroContext;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DebuggingPreprocessor {
    public static Logger logger = Logger.getLogger("de.ovgu.jcpp");
    public static boolean DEBUG_TOKENSTREAM = false;

    static {
        logger.setLevel(Level.WARNING);
        try {
            Handler fh;
            fh = new FileHandler("jcpp.log");
            logger.addHandler(fh);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
        }
    }

    int max_nesting = 0;
    int header_count = 0;
    Set<String> distinctHeaders = new HashSet<String>();

    BufferedWriter debugFile;
    BufferedWriter debugSourceFile;
    String outputName;

    private String baseOutName() {
        if (outputName != null)
            //TODO: Buggy, should use replaceFirst, otherwise the 1st is not
            //interpreted as a regexp and doesn't work at all (here, no
            //replacement is done).
            return outputName.replace(".pi$", "");
        else
            return null;
    }

    public void openDebugFiles(String outputName) {
        this.outputName = outputName;
        try {
            if (DEBUG_TOKENSTREAM)
                debugFile = new BufferedWriter(new FileWriter(new File(
                        baseOutName() + ".tokStr")));
            debugSourceFile = new BufferedWriter(new FileWriter(new File(
                    baseOutName() + ".dbgSrc")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract MacroContext getMacros();

    public void debugWriteMacros() {
        try {
            String outName = baseOutName();
            if (outName == null) {
                logger.info("macro dump skipped");
                return;
            }
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(
                    outName + ".macroDbg")));
            getMacros().debugPrint(writer);
            writer.close();
            // Confusing - it advances some debug files but not others.
            // debugNextTokens();

            // also add statistics to debugSourceFile
            if (debugSourceFile != null) {
                debugSourceFile
                        .append("\n\n\nStatistics (max_nesting,header_count,distinct files):\n"
                                + max_nesting
                                + ";"
                                + header_count
                                + ";"
                                + distinctHeaders.size() + "\n");
                debugSourceFile.flush();
            }

            logger.info("macro dump written");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract Token parse_main() throws IOException, LexerException;

//	private void debugNextTokens() {
//		for (int i = 0; i < 20; i++)
//			try {
//				parse_main();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (LexerException e) {
//				e.printStackTrace();
//			}
//	}

    public void debug_receivedToken(Source source, Token tok) {
        if (DEBUG_TOKENSTREAM && tok != null && debugFile != null)
            try {
                Source tmpSrc = source.getParent();
                while (tmpSrc != null) {
                    debugFile.write("\t");
                    tmpSrc = tmpSrc.getParent();
                }
                if (tok.getText() != null)
                    debugFile.write(tok.getText() + "\n");
                debugFile.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    int debugSourceIdx = 0;

    public void debugSourceBegin(Source source, State state) {
        if (source instanceof FileLexerSource) {
            debugSourceIdx++;
            try {
                StringBuffer b = new StringBuffer();
                max_nesting = Math.max(max_nesting, debugSourceIdx);
                distinctHeaders.add(source.toString());
                header_count++;
                for (int i = 1; i < debugSourceIdx; i++)
                    b.append("\t");
                b
                        .append("push "
                                + source.toString()
                                + " -- "
                                + (state == null ? "null" : state
                                .getLocalFeatureExpr()
                                + " ("
                                + state.getFullPresenceCondition()
                                + ")") + "\n");
//				 System.out.println(b.toString());
                if (debugSourceFile != null) {
                    debugSourceFile.write(b.toString());
                    debugSourceFile.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void debugSourceEnd(Source source) {
        if (source instanceof FileLexerSource) {
            debugSourceIdx--;
            try {
                StringBuffer b = new StringBuffer();
                for (int i = 0; i < debugSourceIdx; i++)
                    b.append("\t");
                b.append("pop " + source.toString() + "\n");
//				 System.out.println(b.toString());
                if (debugSourceFile != null) {
                    debugSourceFile.write(b.toString());
                    debugSourceFile.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
