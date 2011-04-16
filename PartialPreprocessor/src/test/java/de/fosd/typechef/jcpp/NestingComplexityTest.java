package de.fosd.typechef.jcpp;

import java.io.IOException;

import de.fosd.typechef.lexer.LexerException;
import org.junit.Test;

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

	@Test
	public void testSimpleNesting() throws LexerException, IOException {
		testFile("nesting/termination.c");
	}
}
