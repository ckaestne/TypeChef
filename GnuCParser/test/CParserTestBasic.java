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

public class CParserTestBasic {

	@org.junit.Test
	public void testDefinition() throws RecognitionException,
			TokenStreamException, FileNotFoundException, ANTLRException {
		newParser(
				"void __attribute__((section(\".spinlock.text\"))) _raw_spin_lock_nest_lock(int *lock, int *map) ;")
				.declaration();
	}

	@org.junit.Test
	public void test1() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test.c");
	}

	@org.junit.Test
	public void testattrib() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/testattrib.c");
	}

	@org.junit.Test
	public void testattribdef() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/testattribdef.c");
	}

	@org.junit.Test
	public void test2() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test2.c");
	}

	@org.junit.Test
	public void test3() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test3.c");
	}

	@org.junit.Test
	public void test4() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test4.c");
	}

	@org.junit.Test
	public void test5() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test5.c");
	}

	@org.junit.Test
	public void test6() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test6.c");
	}

	@org.junit.Test
	public void test7() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test7.c");
	}

	@org.junit.Test
	public void test8() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test8.c");
	}

	@org.junit.Test
	public void test9() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test9.c");
	}

	@org.junit.Test
	public void test10() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test10.c");
	}

	@org.junit.Test
	public void test11() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test11.c");
	}

	@org.junit.Test
	public void test12() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test12.c");
	}

	@org.junit.Test
	public void test13() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test13.c");
	}

	@org.junit.Test
	public void test14() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test14.c");
	}

	@org.junit.Test
	public void test15() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test15.c");
	}

	@org.junit.Test
	public void test16() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test16.c");
	}

	@org.junit.Test
	public void test17() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test17.c");
	}

	@org.junit.Test
	public void test18() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test18.c");
	}

	@org.junit.Test
	public void test19() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test19.c");
	}

	@org.junit.Test
	public void test20() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test20.c");
	}

	@org.junit.Test
	public void test21() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test21.c");
	}

	@org.junit.Test
	public void test22() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test22.c");
	}

	@org.junit.Test
	public void test23() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test23.c");
	}

	@org.junit.Test
	public void test24() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test24.c");
	}

	@org.junit.Test
	public void test25() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test25.c");
	}

	@org.junit.Test
	public void test26() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test26.c");
	}

	@org.junit.Test
	public void test27() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test27.c");
	}

	@org.junit.Test
	public void test28() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test28.c");
	}

	@org.junit.Test
	public void test29() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test29.c");
	}

	@org.junit.Test
	public void test30() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test30.c");
	}

	@org.junit.Test
	public void test31() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test31.c");
	}

	@org.junit.Test
	public void test32() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test32.c");
	}

	@org.junit.Test
	public void test33() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test33.c");
	}

	@org.junit.Test
	public void test34() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test34.c");
	}

	@org.junit.Test
	public void test35() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test35.c");
	}

	@org.junit.Test
	public void test36() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test36.c");
	}

	@org.junit.Test
	public void test37() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test37.c");
	}

	@org.junit.Test
	public void test38() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test38.c");
	}

	@org.junit.Test
	public void test39() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test39.c");
	}

	@org.junit.Test
	public void test40() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test40.c");
	}

	@org.junit.Test
	public void test41() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test41.c");
	}

	@org.junit.Test
	public void test42() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test42.c");
	}

	@org.junit.Test
	public void test43() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test43.c");
	}

	@org.junit.Test
	public void test44() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test44.c");
	}

	@org.junit.Test
	public void test45() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test45.c");
	}

	@org.junit.Test
	public void test46() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test46.c");
	}

	@org.junit.Test
	public void test47() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test47.c");
	}

	@org.junit.Test
	public void test48() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test48.c");
	}

	@org.junit.Test
	public void test49() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test49.c");
	}

	@org.junit.Test
	public void test50() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test50.c");
	}

	@org.junit.Test
	public void test51() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test51.c");
	}

	@org.junit.Test
	public void test52() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test52.c");
	}

	@org.junit.Test
	public void test53() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test53.c");
	}

	@org.junit.Test
	public void test54() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test54.c");
	}

	@org.junit.Test
	public void test55() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test55.c");
	}

	@org.junit.Test
	public void test56() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test56.c");
	}

	@org.junit.Test
	public void test57() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test57.c");
	}

	@org.junit.Test
	public void test58() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test58.c");
	}

	@org.junit.Test
	public void test59() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test59.c");
	}

	@org.junit.Test
	public void test60() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test60.c");
	}

	@org.junit.Test
	public void test61() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test61.c");
	}

	@org.junit.Test
	public void test62() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test62.c");
	}

	@org.junit.Test
	public void test63() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test63.c");
	}

	@org.junit.Test
	public void test64() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test64.c");
	}

	@org.junit.Test
	public void test65() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test65.c");
	}

	@org.junit.Test
	public void test66() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test66.c");
	}

	@org.junit.Test
	public void test67() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test67.c");
	}

	@org.junit.Test
	public void test68() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test68.c");
	}

	@org.junit.Test
	public void test69() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test69.c");
	}

	@org.junit.Test
	public void test70() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test70.c");
	}

	@org.junit.Test
	public void test71() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test71.c");
	}

	@org.junit.Test
	public void test72() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test72.c");
	}

	@org.junit.Test
	public void test73() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test73.c");
	}

	@org.junit.Test
	public void test74() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test74.c");
	}

	@org.junit.Test
	public void test75() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test75.c");
	}

	@org.junit.Test
	public void test76() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test76.c");
	}

	@org.junit.Test
	public void test77() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test77.c");
	}

	@org.junit.Test
	public void test78() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test78.c");
	}

	@org.junit.Test
	public void test79() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test79.c");
	}

	@org.junit.Test
	public void test80() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test80.c");
	}

	@org.junit.Test
	public void test81() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test81.c");
	}

	@org.junit.Test
	public void test83() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test83.c");
	}

	@org.junit.Test
	public void test84() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test84.c");
	}

	@org.junit.Test
	public void test85() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test85.c");
	}

	@org.junit.Test
	public void test86() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test86.c");
	}

	@org.junit.Test
	public void test87() throws FileNotFoundException, ANTLRException {
		parse("cgram/tests/test87.c");
	}

	@org.junit.Test
	public void testfail() throws FileNotFoundException {
		try {
			parse("cgram/tests/testfail.c");
			Assert.fail();
		} catch (ANTLRException e) {
			e.printStackTrace();
		}

	}

	@org.junit.Test
	@Ignore
	public void testForkI() throws FileNotFoundException, ANTLRException {
		parse("C:/Users/ckaestne/Documents/uni/typechef/Staging/fork.i");
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
