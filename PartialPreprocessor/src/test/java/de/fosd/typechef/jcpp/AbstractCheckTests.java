package de.fosd.typechef.jcpp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.anarres.cpp.Feature;
import org.anarres.cpp.FeatureExprLib;
import org.anarres.cpp.FileLexerSource;
import org.anarres.cpp.LexerException;
import org.anarres.cpp.Preprocessor;
import org.anarres.cpp.PreprocessorListener;
import org.anarres.cpp.Source;
import org.anarres.cpp.StringLexerSource;
import org.anarres.cpp.Token;
import org.anarres.cpp.Warning;

import de.fosd.typechef.featureexpr.MacroContext$;

public class AbstractCheckTests {

	private Preprocessor pp;

	public AbstractCheckTests() {
		super();
	}

	protected void testFile(String filename) throws LexerException, IOException {
		testFile(filename, false);
	}

	/**
	 * parses a file and checks the result against the results specified in the
	 * filename.check file
	 * 
	 * @param filename
	 * @throws LexerException
	 * @throws IOException
	 */
	private void testFile(String filename, boolean debug)
			throws LexerException, IOException {
		String folder = "tc_data/";

		InputStream inputStream = getClass().getResourceAsStream(
				"/" + folder + filename);

		StringBuffer output;
		LexerException ex = null;
		try {
			output = parse(new FileLexerSource(inputStream, folder + filename),
					debug, getClass().getResource("/" + folder).getFile());
		} catch (LexerException e) {
			ex = e;
			output = new StringBuffer("ERROR: ").append(e.toString());
		}
		if (!check(filename, folder, output))
			if (ex != null)
				throw ex;

	}

	protected String parseCodeFragment(String code) throws LexerException,
			IOException {
		return parse(new StringLexerSource(code, true), false, null).toString();
	}

	private boolean check(String filename, String folder, StringBuffer output)
			throws FileNotFoundException, IOException {
		boolean containsErrorCheck = false;
		InputStream inputStream = getClass().getResourceAsStream(
				"/" + folder + filename + ".check");
		BufferedReader checkFile = new BufferedReader(new InputStreamReader(
				inputStream));
		String line;
		while ((line = checkFile.readLine()) != null) {
			if (line.startsWith("!")) {
				String substring = line.substring(2);
				if (output.toString().contains(substring)) {
					System.err.println(output);
					Assert.fail(substring
							+ " found but not expected in output\n"
							+ output.toString());
				}
			}
			if (line.startsWith("+")) {
				int expected = Integer.parseInt(line.substring(1, 2));
				int found = 0;
				String substring = line.substring(3);

				String content = output.toString();
				int idx = content.indexOf(substring);
				while (idx >= 0) {
					found++;
					content = content.substring(idx + substring.length());
					idx = content.indexOf(substring);
				}

				if (expected != found) {
					failOutput(output);
					Assert.fail(substring + " found " + found
							+ " times, but expected " + expected + " times\n"
							+ content);
				}
			}
			if (line.startsWith("*")) {
				String substring = line.substring(2);

				String content = output.toString();
				int idx = content.indexOf(substring);
				if (idx < 0) {
					failOutput(output);
					Assert.fail(substring + " not found but expected\n"
							+ content);
				}
			}
			if (line.trim().equals("error")) {
				containsErrorCheck = true;
				Assert.assertTrue(
						"Expected error, but preprocessing succeeded", output
								.toString().startsWith("ERROR: "));
			}
			if (line.trim().equals("print")) {
				System.out.println(output.toString());
			}
			if (line.trim().equals("macrooutput")) {
				System.out.println(pp.debugMacros());
			}
		}
		return containsErrorCheck;
	}

	private void failOutput(StringBuffer output) {
		System.err.println(output);
		if (pp != null)
			System.err.println(pp.debugMacros());
	}

	private StringBuffer parse(Source source, boolean debug, String folder)
			throws LexerException, IOException {
		// XXX Why here? And isn't the whole thing duplicated from elsewhere?
		MacroContext$.MODULE$.setPrefixFilter("CONFIG_");

		pp = new Preprocessor();
		pp.addFeature(Feature.DIGRAPHS);
		pp.addFeature(Feature.TRIGRAPHS);
		pp.addFeature(Feature.LINEMARKERS);
		pp.addWarnings(Warning.allWarnings());
		pp.setListener(new PreprocessorListener(pp) {
			@Override
			public void handleWarning(Source source, int line, int column,
					String msg) throws LexerException {
				super.handleWarning(source, line, column, msg);
				throw new LexerException(msg + " " + source + ":" + line + ":"
						+ column);
			}
		});
		pp.addMacro("__JCPP__", FeatureExprLib.base());

		// include path
		if (folder != null)
			pp.getSystemIncludePath().add(folder);

		pp.addInput(source);

		StringBuffer output = new StringBuffer();
		for (;;) {
			Token tok = pp.getNextToken();
			if (tok == null)
				break;
			if (tok.getType() == Token.EOF)
				break;

			output.append(tok.getText());
			if (debug)
				System.out.print(tok.getText());
		}
		return output;
	}

}