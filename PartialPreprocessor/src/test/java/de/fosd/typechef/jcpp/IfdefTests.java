package de.fosd.typechef.jcpp;

import java.io.IOException;

import org.anarres.cpp.LexerException;
import org.anarres.cpp.Main;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * isolated (often unmaintained) tests of the parser
 * 
 * @author kaestner
 * 
 */
public class IfdefTests extends AbstractCheckTests {

	// @Test
	// @Ignore
	// public void testMacroContext() {
	// MacroContext c = new MacroContext();
	// System.out.println(c);
	// c = c.define("test", new FeatureExpr().base(), "=>0");
	// System.out.println(Arrays.toString(c.getMacroExpansions("test")));// 0
	// // if
	// // BASE
	// c = c.define("test", new FeatureExpr().base(), "=>1");
	// System.out.println(Arrays.toString(c.getMacroExpansions("test")));// 1
	// // if
	// // BASE;
	// // 0
	// // if
	// // DEAD
	// c = c.define("test", new FeatureExpr(new DefinedExternal("X")), "=>2");
	// System.out.println(Arrays.toString(c.getMacroExpansions("test")));// 2
	// // if
	// // X;
	// // 1
	// // if
	// // !X;
	// // 0
	// // if
	// // DEAD
	// c = c.define("test", new FeatureExpr().base(), "=>3");
	// System.out.println(Arrays.toString(c.getMacroExpansions("test")));// 3
	// // if
	// // BASE
	//
	// c = new MacroContext();
	// System.out.println(c);
	// c = c.define("test", new FeatureExpr(new DefinedExternal("X")), "=>0");
	// System.out.println(Arrays.toString(c.getMacroExpansions("test")));// 0
	// // if
	// // X
	// }
	//
	// @Test
	// @Ignore
	// public void testFeatureExprLib() {
	// // Defined$ d=Defined$.MODULE$;
	// // System.out.println(new FeatureExpr().test());
	// System.out.println(FeatureExpr$.MODULE$.createDefined("test",
	// new MacroContext()).not());
	// }

	@Test
	@Ignore
	public void testIfdef1() throws Exception {
		Main.main(new String[] { "test/tc_data/in1.c", "-I", "test/tc_data/"// ,"--debug"
		});

	}

	@Test
	@Ignore
	public void testIfdef2() throws Exception {
		Main.main(new String[] { "test/tc_data/undef.c", "-I", "test/tc_data/"// ,"--debug"
		});

	}

	@Test
	public void testMacros() throws Exception {
		Main.main(new String[] { "test/tc_data/macro.c", "-I", "test/tc_data/"// ,"--debug"
		});

	}

	@Test
	public void testDeadElse() throws Exception {
		Main.main(new String[] { "test/tc_data/deadelse.h", "-I",
				"test/tc_data/"// ,"--debug"
		});

	}

	@Test
	public void testRecursiveMacro() throws Exception {
		Main.main(new String[] { "test/tc_data/recursivemacro.h", "-I",
				"test/tc_data/"// ,"--debug"
		});

	}

	@Test
	public void testMultiMacro() throws Exception {
		Main.main(new String[] { "test/tc_data/multimacro.c", "-I",
				"test/tc_data/"// ,"--debug"
		});

	}

	@Test
	public void testUnnecessaryWarning() throws LexerException, IOException {
		parseCodeFragment("#ifdef DOUBLE\n" + "#if DOUBLE>2\n" + "#endif\n"
				+ "#endif\n");

	}

	@Test
	public void testNecessaryWarning() throws IOException {
		try {
			parseCodeFragment("#if DOUBLE>2\n" + "#endif\n");
			Assert.fail("no warning");
		} catch (LexerException e) {
			// expected exception for warning
		}

	}
	
	@Test
	public void testAlternativeDefiition() throws Exception {
		Main.main(new String[] { "test/input/test2.h"// ,"--debug"
		});
	}
}
