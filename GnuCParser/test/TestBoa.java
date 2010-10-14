import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;

import cgram.GnuCLexer;
import cgram.GnuCParser;
import cgram.TNode;

import junit.framework.Assert;
import antlr.ANTLRException;
import antlr.RecognitionException;
import antlr.TokenStreamException;

public class TestBoa {

	@org.junit.Test
	public void testBoa1() throws FileNotFoundException, ANTLRException {
		parse("../boa/src/hash.pi");
	}
	

	@Ignore
	@org.junit.Test
	public void testForkI() throws FileNotFoundException, ANTLRException {
		parse("../linux-2.6.33.3/kernel/fork.pi");
	}

	private void parse(String programName) throws FileNotFoundException,
			ANTLRException {
		final List<ANTLRException> e = new ArrayList<ANTLRException>();
		DataInputStream dis = null;
		if (programName.equals("-")) {
			dis = new DataInputStream(System.in);
		} else {
			dis = new DataInputStream(new FileInputStream(programName));
		}
		GnuCLexer lexer = new GnuCLexer(dis);
		lexer.setTokenObjectClass("cgram.CToken");
		lexer.initialize();
		// Parse the input expression.
		GnuCParser parser = new GnuCParser(lexer) {
			@Override
			public void reportError(RecognitionException ex)
					throws RecognitionException {
				super.reportError(ex);
				throw ex;
				// e.add(ex);
			}
		};
		TNode node = new TNode();
		node.setType(GnuCParser.LITERAL_typedef);
		parser.setASTNodeType(TNode.class.getName());
		TNode.setTokenVocabulary("cgram.GNUCTokenTypes");

		// invoke parser
		parser.knownTypedefNames.add("__uint32");
		parser.translationUnit();
		System.out.println(programName
				+ " ************************************************");
		PrintStream out = new PrintStream(new FileOutputStream(new File(
				programName + ".ast")));
		TNode.printTree(out, parser.getAST());

		if (!e.isEmpty())
			throw e.iterator().next();
	}

	private GnuCParser newParser(String code) throws FileNotFoundException,
			ANTLRException {
		DataInputStream dis = null;
		dis = new DataInputStream(new ByteArrayInputStream(code.getBytes()));
		GnuCLexer lexer = new GnuCLexer(dis);
		lexer.setTokenObjectClass("cgram.CToken");
		lexer.initialize();
		// Parse the input expression.
		GnuCParser parser = new GnuCParser(lexer);
		TNode node = new TNode();
		node.setType(GnuCParser.LITERAL_typedef);
		parser.setASTNodeType(TNode.class.getName());
		TNode.setTokenVocabulary("cgram.GNUCTokenTypes");

		return parser;
	}

}
