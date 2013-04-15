package de.fosd.typechef.parser.c

import junit.framework._;
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._

/**
 * tests taken from an earlier own ANTLR approach
 */
class CParserTest2 extends TestCase {
  val p = new CParser()

  def assertParseable(code: String, mainProduction: (TokenReader[CToken, CTypeContext], FeatureExpr) => p.MultiParseResult[Any]) {
    val actual = p.parseAny(code.stripMargin, mainProduction)
    System.out.println(actual)
    (actual: @unchecked) match {
      case p.Success(ast, unparsed) => {
        assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
        //succeed
      }
      case p.NoSuccess(msg, unparsed, inner) =>
        fail(msg + " at " + unparsed + " " + inner)
    }
  }


  def testType {
    assertParseable("void", p.typeSpecifier)
    assertParseable("int", p.typeSpecifier)
    assertParseable("char", p.typeSpecifier)
    assertParseable("short", p.typeSpecifier)
    assertParseable("long", p.typeSpecifier)
    assertParseable("float", p.typeSpecifier)
    assertParseable("double", p.typeSpecifier)
    assertParseable("signed", p.typeSpecifier)
    assertParseable("unsigned", p.typeSpecifier)
    assertParseable("_Bool", p.typeSpecifier)
    assertParseable("_Complex", p.typeSpecifier)
    assertParseable("int", p.typeSpecifier)
    assertParseable("unsigned long", p.typeSpecifier *)
    assertParseable("__typeof__(int)", p.typeSpecifier)
    assertParseable("__typeof__(unsigned long)", p.typeSpecifier)

    assertParseable("const", p.typeQualifier)
    assertParseable("restrict", p.typeQualifier)
    assertParseable("volatile", p.typeQualifier)
  }

  def testPrimaryExpression {
    assertParseable("1", p.primaryExpr)
    assertParseable("a", p.primaryExpr)
    assertParseable("__func__", p.primaryExpr)
    assertParseable("a = 1", p.expr)
    //        assertParseable("__builtin_va_arg(a = 1, int)", p.primaryExpr)
    //        assertParseable("__builtin_types_compatible_p ( int , int )", p.primaryExpr)
    //        assertParseable("__builtin_choose_expr(a=1,a=2,a=3)", p.primaryExpr)
    //        assertParseable("__builtin_offsetof ( int , i.i )", p.primaryExpr)
    //        assertParseable("__builtin_offsetof(struct pt_regs,ss)", p.primaryExpr)
    assertParseable("1?2:3", p.expr)
  }

  def testBuiltInOffsetOf {
    assertParseable("i", p.offsetofMemberDesignator)
    assertParseable("i.i", p.offsetofMemberDesignator)
    // parse("i[1]", "offsetofMemberDesignator");
    //

    assertParseable("__builtin_offsetof(struct pt_regs,ss)", p.primaryExpr)
    // newParser("(offset > (__builtin_offsetof(struct pt_regs,ss)))").expr();
    assertParseable("__builtin_offsetof ( int , i.i )", p.primaryExpr)

  }

  def testStatements() {
    assertParseable("i=1;", p.statement)
    assertParseable("for (;;)i=1;", p.statement)
    assertParseable("i;", p.statement)

    assertParseable("asm volatile(\"\": : :\"memory\");", p.statement)
    assertParseable("__asm__ __volatile__(\"\": : :\"memory\");", p.statement)

    //        assertParseable("asm volatile(\"\" \"771:\\n\\t\" \"call *%c[paravirt_opptr];\" \"\\n\" \"772:\\n\" \".pushsection .parainstructions,\\\"a\\\"\\n\" \" \" \".balign 4\" \" \" \"\\n\" \" \" \".long\" \" \" \" 771b\\n\" \"  .byte \" \"%c[paravirt_typenum]\" \"\\n\" \"  .byte 772b-771b\\n\" \"  .short \" \"%c[paravirt_clobber]\" \"\\n\" \".popsection\\n\" \"\" : \"=a\" (__eax), \"=d\" (__edx), \"=c\" (__ecx) : [paravirt_typenum] \"i\" ((__builtin_offsetof(struct paravirt_patch_template,pv_cpu_ops.load_sp0) / sizeof(void *))), [paravirt_opptr] \"i\" (&(pv_cpu_ops.load_sp0)), [paravirt_clobber] \"i\" (((1 << 4) - 1)), \"a\" ((unsigned long)(tss)), \"d\" ((unsigned long)(thread)) : \"memory\", \"cc\" );",p.gnuAsmExpr)
    //        assertParseable(
    //            "({ unsigned long __eax = __eax, __edx = __edx, __ecx = __ecx; ((void)pv_cpu_ops.load_sp0); asm volatile(\"\" \"771:\\n\\t\" \"call *%c[paravirt_opptr];\" \"\\n\" \"772:\\n\" \".pushsection .parainstructions,\\\"a\\\"\\n\" \" \" \".balign 4\" \" \" \"\\n\" \" \" \".long\" \" \" \" 771b\\n\" \"  .byte \" \"%c[paravirt_typenum]\" \"\\n\" \"  .byte 772b-771b\\n\" \"  .short \" \"%c[paravirt_clobber]\" \"\\n\" \".popsection\\n\" \"\" : \"=a\" (__eax), \"=d\" (__edx), \"=c\" (__ecx) : [paravirt_typenum] \"i\" ((__builtin_offsetof(struct paravirt_patch_template,pv_cpu_ops.load_sp0) / sizeof(void *))), [paravirt_opptr] \"i\" (&(pv_cpu_ops.load_sp0)), [paravirt_clobber] \"i\" (((1 << 4) - 1)), \"a\" ((unsigned long)(tss)), \"d\" ((unsigned long)(thread)) : \"memory\", \"cc\" ); });"
    //            , p.statement);

  }

  def testAsm {
    assertParseable("asm ( \" \")", p.gnuAsmExpr);
    assertParseable("asm volatile( \" \")", p.gnuAsmExpr);
    assertParseable("asm volatile( \" \" : \" \" )", p.gnuAsmExpr);
    assertParseable("asm volatile( \" \" : \" \"(1) )", p.gnuAsmExpr); // expr
    assertParseable("asm volatile( \" \" : \" \", \" \" )", p.gnuAsmExpr); // list
    assertParseable("asm volatile( \" \" : [x]\" \" )", p.gnuAsmExpr); // expr

    assertParseable("asm volatile( \" \" : \" \"  : \" \" )", p.gnuAsmExpr);
    assertParseable("asm volatile( \" \" : \" \"  : \" \": \" \" )", p.gnuAsmExpr);
  }

  def testTypeName {
    //        assertParseable("const *p", p.typeName)
    //        assertParseable("const __attribute__((aaa)) *p", p.typeName)
    assertParseable("__builtin_va_list", p.typedefName)
  }

  def testDeclaration {
    // newParser("__attribute__((aa))__ a").declarator(false);
    assertParseable("a", p.declarator)
    assertParseable("(a)", p.declarator)
    // parse("*a __attribute__ a()").declarator(false);

    assertParseable("void inline a;", p.declaration)
    assertParseable("void inline a();", p.declaration)
    assertParseable("__attribute__(()) void inline a();", p.declaration)
    assertParseable("void inline a() __attribute__(());", p.declaration)
    assertParseable("typedef int a;", p.declaration)
    assertParseable("typedef __builtin_va_list __gnuc_va_list;", p.declaration)

    assertParseable(
      "extern struct pcpu_alloc_info * __attribute__ ((__section__(\".init.text\"))) __attribute__((__cold__)) __attribute__((no_instrument_function)) pcpu_alloc_alloc_info(int nr_groups,       int nr_units);",
      p.declaration);
    assertParseable("struct ring_buffer;", p.declaration);
    assertParseable(
      "struct ring_buffer_event {			 ;			 int type_len:5, time_delta:27;			 ;			 int array[];			};",
      p.declaration);

  }

  def testStorageClassSpecifier {
    assertParseable("typedef", p.storageClassSpecifier)
    assertParseable("extern", p.storageClassSpecifier)
    assertParseable("static", p.storageClassSpecifier)
    assertParseable("auto", p.storageClassSpecifier)
    assertParseable("register", p.storageClassSpecifier)

    assertParseable("inline", p.functionDeclSpecifiers)

    assertParseable("inline", p.declSpecifiers)
    assertParseable("inline inline static typedef", p.declSpecifiers)
    assertParseable("void int inline", p.declSpecifiers)
  }

  def testParameter() {
    assertParseable("const int * __preg", p.parameterDeclaration)
  }

  def testDefinition {
    assertParseable(
      "void __attribute__((section(\".spinlock.text\"))) _raw_spin_lock_nest_lock(int *lock, int *map) ;",
      p.declaration);
  }
}