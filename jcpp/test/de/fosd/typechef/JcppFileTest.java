package de.fosd.typechef;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.Assert;

import org.anarres.cpp.Feature;
import org.anarres.cpp.LexerException;
import org.anarres.cpp.Preprocessor;
import org.anarres.cpp.PreprocessorListener;
import org.anarres.cpp.Token;
import org.anarres.cpp.Warning;
import org.junit.Test;

import de.fosd.typechef.featureexpr.BaseFeature;

public class JcppFileTest {

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
	protected void testFile(String filename, boolean debug)
			throws LexerException, IOException {
		String folder = "test/tc_data/";

		StringBuffer output = parse(filename, debug, folder);
		check(filename, folder, output);

	}

	private void check(String filename, String folder, StringBuffer output)
			throws FileNotFoundException, IOException {
		BufferedReader checkFile = new BufferedReader(new FileReader(new File(
				folder + filename + ".check")));
		String line;
		while ((line = checkFile.readLine()) != null) {
			if (line.startsWith("!")) {
				String substring = line.substring(2);
				if (output.toString().contains(substring)) {
					System.out.println(output);
					Assert
							.fail(substring
									+ " found but not expected in output");
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
					System.out.println(output);
					Assert.fail(substring + " found " + found
							+ " times, but expected " + expected + " times");
				}
			}
			if (line.startsWith("*")) {
				String substring = line.substring(2);

				String content = output.toString();
				int idx = content.indexOf(substring);
				if (idx < 0) {
					System.out.println(output);
					Assert.fail(substring + " not found but expected");
				}
			}
		}
	}

	private StringBuffer parse(String filename, boolean debug, String folder)
			throws LexerException, IOException {
		Preprocessor pp = new Preprocessor();
		pp.addFeature(Feature.DIGRAPHS);
		pp.addFeature(Feature.TRIGRAPHS);
		pp.addFeature(Feature.LINEMARKERS);
		pp.addWarning(Warning.IMPORT);
		pp.setListener(new PreprocessorListener(pp));
		pp.addMacro("__JCPP__", new BaseFeature());

		// include path
		pp.getSystemIncludePath().add(folder);

		pp.addInput(new File(folder + filename));

		StringBuffer output = new StringBuffer();
		for (;;) {
			Token tok = pp.token();
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

	@Test
	public void testNestingDead() throws LexerException, IOException {
		testFile("nestingdead.c");
	}

	@Test
	public void testDeadElse() throws LexerException, IOException {
		testFile("deadelse.h");
	}
	@Test
	public void testIncludeGuard() throws LexerException, IOException {
		testFile("in1.c");
	}
}
