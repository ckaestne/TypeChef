package org.anarres.cpp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.fosd.typechef.featureexpr.MacroContext;

public abstract class DebuggingPreprocessor {
	public static Logger logger = Logger.getLogger("de.ovgu.jcpp");
	public static boolean DEBUG_TOKENSTREAM = false;
	static {
		try {
			Handler fh;
			fh = new FileHandler("jcpp.log");
			logger.addHandler(fh);
			logger.setLevel(Level.WARNING);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	BufferedWriter debugFile;
	BufferedWriter debugSourceFile;
	{
		try {
			debugFile = new BufferedWriter(new FileWriter(new File(
					"tokenstream.txt")));
			debugSourceFile = new BufferedWriter(new FileWriter(new File(
					"debugsource.txt")));
		} catch (IOException e) {
		}
	}

	protected abstract MacroContext getMacros();

	public String debugMacros() {
		return getMacros().toString();
	}

	public void debugWriteMacros() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					"macroDebug.txt"));
			writer.write(debugMacros());
			writer.close();
			debugNextTokens();
			logger.info("macro dump written");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected abstract Token parse_main() throws IOException, LexerException;

	private void debugNextTokens() {
		for (int i = 0; i < 20; i++)
			try {
				parse_main();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (LexerException e) {
				e.printStackTrace();
			}
	}

	public void debug_receivedToken(Source source, Token tok) {
		if (DEBUG_TOKENSTREAM)
			try {
				Source tmpSrc = source.getParent();
				while (tmpSrc != null) {
					debugFile.write("\t");
					tmpSrc = tmpSrc.getParent();
				}
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
				for (int i = 1; i < debugSourceIdx; i++)
					debugSourceFile.write("\t");
				debugSourceFile.write("push " + source.toString()+" -- "+state.getLocalFeatureExpr() + "\n");
				debugSourceFile.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void debugSourceEnd(Source source) {
		if (source instanceof FileLexerSource) {
			debugSourceIdx--;
			try {
				for (int i = 0; i < debugSourceIdx; i++)
					debugSourceFile.write("\t");
				debugSourceFile.write("pop " + source.toString() + "\n");
				debugSourceFile.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
