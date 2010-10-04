package de.fosd.typechef.jcpp;

import java.io.IOException;

import org.anarres.cpp.LexerException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * test output with .check files
 * 
 * @author kaestner
 * 
 */
public class JcppFileTest extends AbstractCheckTests {

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

	@Test
	public void testUnlikely() throws LexerException, IOException {
		testFile("unlikely.h");
	}

	@Test
	public void testByteOrder() throws LexerException, IOException {
		testFile("byteorder.h");
	}

	@Test
	public void testIf() throws LexerException, IOException {
		testFile("if.h");
	}

	@Test
	public void testAlternativeMacros() throws LexerException, IOException {
		testFile("macro2.c");
	}

	@Test
	public void testIncludeGuards() throws LexerException, IOException {
		testFile("includeguards.c");
	}
	@Test
	public void testDefDefined() throws LexerException, IOException {
		testFile("defdefined.c");
	}
	@Test
	public void testAlternativeDef() throws LexerException, IOException {
		testFile("alternativedef.c");
	}	@Test
	public void testHiddenBaseAndDead() throws LexerException, IOException {
		testFile("hiddenDeadAndBase.c");
	}

	@Test@Ignore
	public void testMultiInclude() throws LexerException, IOException {
		//XXX this is not supported right now. let's see whether we will need it.
		testFile("multiinclude.c");
	}
	
	@Test
	public void testIfElseParsing() throws LexerException, IOException {
		testFile("ifcondition.c");
	}
	
	@Test
	public void testBeispielJoerg() throws LexerException, IOException {
		testFile("beispielJoerg.c");
	}
}
