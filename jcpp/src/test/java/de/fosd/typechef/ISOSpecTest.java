package de.fosd.typechef;

import java.io.IOException;

import org.anarres.cpp.LexerException;
import org.junit.Test;

/**
 * test the examples from the ISO specification
 * 
 * @author kaestner
 * 
 */
public class ISOSpecTest extends AbstractCheckTests {

	@Test
	public void testEx1() throws LexerException, IOException {
		testFile("iso/ex1.c");
	}
	
	
	
	@Test
	public void testEx2() throws LexerException, IOException {
		testFile("iso/ex2.c");
	}
	
	@Test
	public void testEx3() throws LexerException, IOException {
		testFile("iso/ex3.c");
	}
	
//	@Test
//	public void testEx4() throws LexerException, IOException {
//		testFile("iso/ex4.c");
//	}
//	
//	@Test
//	public void testEx5() throws LexerException, IOException {
//		testFile("iso/ex5.c");
//	}
//	
//	@Test
//	public void testEx6() throws LexerException, IOException {
//		testFile("iso/ex6.c");
//	}
//	
}
