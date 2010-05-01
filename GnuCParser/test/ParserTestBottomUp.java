import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.Assert;

import antlr.ANTLRException;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import org.junit.Test;

public class ParserTestBottomUp {

	@Test
	public void testType() throws RecognitionException, TokenStreamException,
			FileNotFoundException, ANTLRException {
		newParser("void").typeSpecifier(0);
		newParser("int").typeSpecifier(0);
		newParser("char").typeSpecifier(0);
		newParser("short").typeSpecifier(0);
		newParser("long").typeSpecifier(0);
		newParser("float").typeSpecifier(0);
		newParser("double").typeSpecifier(0);
		newParser("signed").typeSpecifier(0);
		newParser("unsigned").typeSpecifier(0);
		newParser("_Bool").typeSpecifier(0);
		newParser("_Complex").typeSpecifier(0);
		newParser("int").typeSpecifier(0);
		newParser("unsigned long").typeSpecifier(0);
		newParser("__typeof__(int)").typeSpecifier(0);
		newParser("__typeof__(unsigned long)").typeSpecifier(0);

		newParser("const").typeQualifier();
		newParser("restrict").typeQualifier();
		newParser("volatile").typeQualifier();
	}

	@Test
	public void testPrimaryExpression() throws RecognitionException,
			TokenStreamException, FileNotFoundException, ANTLRException {
		newParser("1").primaryExpr();
		newParser("a").primaryExpr();
		newParser("__func__").primaryExpr();
		newParser("a = 1").primaryExpr();
		newParser("__builtin_va_arg(a = 1, int)").primaryExpr();
		newParser("__builtin_types_compatible_p ( int , int )").primaryExpr();
		newParser("__builtin_choose_expr(a=1,a=2,a=3)").primaryExpr();
		newParser("__builtin_offsetof ( int , i.i )").primaryExpr();
		newParser("__builtin_offsetof(struct pt_regs,ss)").primaryExpr();
		newParser("1?2:3").primaryExpr();
	}

	@Test
	public void testBuiltInOffsetOf() throws Exception {
		parse("i", "offsetofMemberDesignator");
		parse("i.i", "offsetofMemberDesignator");
		// parse("i[1]", "offsetofMemberDesignator");
		// 

		newParser("__builtin_offsetof(struct pt_regs,ss)").primaryExpr();
		// newParser("(offset > (__builtin_offsetof(struct pt_regs,ss)))").expr();
		newParser("__builtin_offsetof ( int , i.i )").primaryExpr();

	}

	@Test
	public void testStatements() throws RecognitionException,
			TokenStreamException, FileNotFoundException, ANTLRException {
		newParser("i=1;").statement();
		newParser("for (;;)i=1;").statement();
		newParser("i;").statement();

		newParser(
				"({ unsigned long __eax = __eax, __edx = __edx, __ecx = __ecx; ((void)pv_cpu_ops.load_sp0); asm volatile(\"\" \"771:\\n\\t\" \"call *%c[paravirt_opptr];\" \"\\n\" \"772:\\n\" \".pushsection .parainstructions,\\\"a\\\"\\n\" \" \" \".balign 4\" \" \" \"\\n\" \" \" \".long\" \" \" \" 771b\\n\" \"  .byte \" \"%c[paravirt_typenum]\" \"\\n\" \"  .byte 772b-771b\\n\" \"  .short \" \"%c[paravirt_clobber]\" \"\\n\" \".popsection\\n\" \"\" : \"=a\" (__eax), \"=d\" (__edx), \"=c\" (__ecx) : [paravirt_typenum] \"i\" ((__builtin_offsetof(struct paravirt_patch_template,pv_cpu_ops.load_sp0) / sizeof(void *))), [paravirt_opptr] \"i\" (&(pv_cpu_ops.load_sp0)), [paravirt_clobber] \"i\" (((1 << 4) - 1)), \"a\" ((unsigned long)(tss)), \"d\" ((unsigned long)(thread)) : \"memory\", \"cc\" ); });")
				.statement();

		newParser("asm volatile(\"\": : :\"memory\");").statement();
		newParser("__asm__ __volatile__(\"\": : :\"memory\");").statement();
	}

	@Test
	public void testAsm() throws Exception {
		parse("asm ( \" \")", "gnuAsmExpr");
		parse("asm volatile( \" \")", "gnuAsmExpr");
		parse("asm volatile( \" \" : \" \" )", "gnuAsmExpr");
		parse("asm volatile( \" \" : \" \"(1) )", "gnuAsmExpr");// expr
		parse("asm volatile( \" \" : \" \", \" \" )", "gnuAsmExpr");// list
		parse("asm volatile( \" \" : [x]\" \" )", "gnuAsmExpr");// expr

		parse("asm volatile( \" \" : \" \"  : \" \" )", "gnuAsmExpr");
		parse("asm volatile( \" \" : \" \"  : \" \": \" \" )", "gnuAsmExpr");
	}

	@Test
	public void testTypeName() throws RecognitionException,
			TokenStreamException, FileNotFoundException, ANTLRException {
		newParser("const *p").typeName();
		newParser("const __attribute__((aaa)) *p").typeName();
		newParser("__builtin_va_list").typedefName();
		// newParser("i;").statement();
	}

	// @Test
	// public void testInitializers() throws RecognitionException,
	// TokenStreamException, FileNotFoundException, ANTLRException{
	// newParser("i=1").initializer();
	// newParser("{i=2, j=3}").initializer();
	// // newParser("i;").initializer();
	// }

	@Test
	public void testDeclaration() throws Exception {
		// newParser("__attribute__((aa))__ a").declarator(false);
		newParser("a").declarator(false);
		newParser("(a)").declarator(false);
		// parse("*a __attribute__ a()").declarator(false);

		newParser("void inline a;").declaration();
		newParser("void inline a();").declaration();
		newParser("__attribute__(()) void inline a();").declaration();
		newParser("void inline a() __attribute__(());").declaration();
		newParser("typedef int a;").declaration();
		newParser("typedef __builtin_va_list __gnuc_va_list;").declaration();

		parse(
				"extern struct pcpu_alloc_info * __attribute__ ((__section__(\".init.text\"))) __attribute__((__cold__)) __attribute__((no_instrument_function)) pcpu_alloc_alloc_info(int nr_groups,       int nr_units);",
				"declaration");
		parse("struct ring_buffer;", "declaration");
		parse(
				"struct ring_buffer_event {			 ;			 int type_len:5, time_delta:27;			 ;			 int array[];			};",
				"declaration");

	}

	@Test
	public void testStorageClassSpecifier() throws RecognitionException,
			TokenStreamException, FileNotFoundException, ANTLRException {
		newParser("typedef").storageClassSpecifier();
		newParser("extern").storageClassSpecifier();
		newParser("static").storageClassSpecifier();
		newParser("auto").storageClassSpecifier();
		newParser("register").storageClassSpecifier();

		newParser("inline").functionDeclSpecifiers();

		newParser("inline").declSpecifiers();
		newParser("inline inline static typedef").declSpecifiers();
		newParser("void int inline").declSpecifiers();
	}

	@org.junit.Test
	public void testDefinition() throws RecognitionException,
			TokenStreamException, FileNotFoundException, ANTLRException {
		newParser(
				"void __attribute__((section(\".spinlock.text\"))) _raw_spin_lock_nest_lock(int *lock, int *map) ;")
				.declaration();
	}

	private GnuCParser newParser(String code) throws FileNotFoundException,
			ANTLRException {
		DataInputStream dis = null;
		dis = new DataInputStream(new ByteArrayInputStream(code.getBytes()));
		GnuCLexer lexer = new GnuCLexer(dis);
		lexer.setTokenObjectClass("CToken");
		lexer.initialize();
		// Parse the input expression.
		GnuCParser parser = new GnuCParser(lexer) {
			@Override
			public void reportError(RecognitionException ex)
					throws RecognitionException {
				super.reportError(ex);
				throw ex;
			}
		};
		TNode node = new TNode();
		node.setType(GnuCParser.LITERAL_typedef);
		parser.setASTNodeType(TNode.class.getName());
		TNode.setTokenVocabulary("GNUCTokenTypes");

		return parser;
	}

	private void parse(String code, String production) throws Exception {
		DataInputStream dis = null;
		dis = new DataInputStream(new ByteArrayInputStream(code.getBytes()));
		GnuCLexer lexer = new GnuCLexer(dis);
		lexer.setTokenObjectClass("CToken");
		lexer.initialize();
		// Parse the input expression.
		GnuCParser parser = new GnuCParser(lexer) {
			@Override
			public void reportError(RecognitionException ex)
					throws RecognitionException {
				super.reportError(ex);
				throw ex;
			}
		};
		TNode node = new TNode();
		node.setType(GnuCParser.LITERAL_typedef);
		parser.setASTNodeType(TNode.class.getName());
		TNode.setTokenVocabulary("GNUCTokenTypes");

		try {
			Method method = parser.getClass().getMethod(production);
			method.invoke(parser);
		} catch (InvocationTargetException e) {
			if (e instanceof Exception)
				throw (Exception) e.getTargetException();
		}

		Assert.assertTrue("EOF not reached " + parser.LA(1), parser.LA(1) == 1);

	}

}
