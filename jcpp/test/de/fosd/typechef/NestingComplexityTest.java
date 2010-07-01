package de.fosd.typechef;

import java.io.IOException;


import org.anarres.cpp.LexerException;
import org.junit.Ignore;
import org.junit.Test;

import de.fosd.typechef.featureexpr.BaseFeature;

/**
 * test output with .check files
 * 
 * @author kaestner
 * 
 */
public class NestingComplexityTest extends AbstractCheckTests {

	@Test
	public void testNesting1() throws LexerException, IOException {
		testFile("nesting/m.c");
	}
}
