package de.fosd.typechef.linux

import junit.framework._;
import junit.framework.Assert._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._
import de.fosd.typechef.parser.c._

class LinuxFragmentsTest extends TestCase {
    val p = new CParser(LinuxFeatureModel.featureModelApprox)

    case class Alt(feature: FeatureExpr, thenBranch: AST, elseBranch: AST) extends Expr {
        override def equals(x: Any) = x match {
            case Alt(f, t, e) => f.equivalentTo(feature) && (thenBranch == t) && (elseBranch == e)
            case _ => false
        }
    }

    object Alt {
        def join = (f: FeatureExpr, x: AST, y: AST) => if (x == y) x else Alt(f, x, y)
    }

    def assertParseResult(expected: AST, code: String, mainProduction: (TokenReader[TokenWrapper, CTypeContext], FeatureExpr) => p.MultiParseResult[AST]) {
        val actual = p.parse(code.stripMargin, mainProduction).forceJoin(FeatureExpr.base, Alt.join)
        System.out.println(actual)
        actual match {
            case p.Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                assertEquals("incorrect parse result", expected, ast)
            }
            case p.NoSuccess(msg, unparsed, inner) =>
                fail(msg + " at " + unparsed + " " + inner)
        }
    }
    def assertParseResult(expected: AST, code: String, productions: List[(TokenReader[TokenWrapper, CTypeContext], FeatureExpr) => p.MultiParseResult[AST]]) {
        for (production <- productions)
            assertParseResult(expected, code, production)
    }

    def assertParseable(code: String, mainProduction: (TokenReader[TokenWrapper, CTypeContext], FeatureExpr) => p.MultiParseResult[Any]) {
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
    def assertParseAnyResult(expected: Any, code: String, mainProduction: (TokenReader[TokenWrapper, CTypeContext], FeatureExpr) => p.MultiParseResult[Any]) {
        val actual = p.parseAny(code.stripMargin, mainProduction)
        System.out.println(actual)
        (actual: @unchecked) match {
            case p.Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                assertEquals("incorrect parse result", expected, ast)
            }
            case p.NoSuccess(msg, unparsed, inner) =>
                fail(msg + " at " + unparsed + " " + inner)
        }
    }
    def assertParseError(code: String, mainProduction: (TokenReader[TokenWrapper, CTypeContext], FeatureExpr) => p.MultiParseResult[Any], expectErrorMsg: Boolean = false) {
        val actual = p.parseAny(code.stripMargin, mainProduction)
        System.out.println(actual)
        (actual: @unchecked) match {
            case p.Success(ast, unparsed) => {
                if (expectErrorMsg || unparsed.atEnd)
                    Assert.fail("parsing succeeded unexpectedly with " + ast + " - " + unparsed)
            }
            case p.NoSuccess(msg, unparsed, inner) =>;
        }
    }
    def assertParseError(code: String, productions: List[(TokenReader[TokenWrapper, CTypeContext], FeatureExpr) => p.MultiParseResult[Any]]) {
        for (production <- productions)
            assertParseError(code, production)
    }

    def a = Id("a");
    def b = Id("b");
    def c = Id("c");
    def d = Id("d");
    def x = Id("x");
    def intType = TypeName(lo(PrimitiveTypeSpecifier("int")), None)
    def o[T](x: T) = Opt(FeatureExpr.base, x)
    def lo[T](x: T) = List(o(x))
    def lo[T](x: T, y: T) = List(o(x), o(y))
    def lo[T](x: T, y: T, z: T) = List(o(x), o(y), o(z))

    def fa = FeatureExpr.createDefinedExternal("a")


    //    def testLinux_aes_glue = assertParseable(
    //        """
    //#if definedEx(CONFIG_DISCONTIGMEM)
    //static int pfn_valid(unsigned long pfn) {
    //	if (((pfn) >> (                      29 - 12)) >= (1UL << (36 -
    //#if (definedEx(CONFIG_X86_PAE) && definedEx(CONFIG_SPARSEMEM))
    //29
    //#endif
    //#if (definedEx(CONFIG_SPARSEMEM) && !definedEx(CONFIG_X86_PAE))
    //26
    //#endif
    //)))
    //		return 0;
    //	return valid_section(__nr_to_section(((pfn) >> (29 - 12))));
    //}
    //#endif
    //        """, p.phrase(p.translationUnit))


    def testLinux_ptrace = assertParseable(
        """long arch_ptrace(struct task_struct *child, long request, long addr, long data)
        {
            int ret;
            unsigned long  *datap = (unsigned long  *)data;

            switch (request) {
            /* read the word at location addr in the USER area. */
            case 3: {
                unsigned long tmp;

                ret = -5;
                if ((addr & (sizeof(data) - 1)) || addr < 0 ||
                    addr >= sizeof(struct user))
                    break;

                tmp = 0;  /* Default return condition */
                if (addr < sizeof(struct user_regs_struct))
                    tmp = getreg(child, addr);
                else if (addr >= __builtin_offsetof(struct user,u_debugreg[0]) &&
                     addr <= __builtin_offsetof(struct user,u_debugreg[7])) {
                    addr -= __builtin_offsetof(struct user,u_debugreg[0]);
                    tmp = ptrace_get_debugreg(child, addr / sizeof(data));
                }
                ret = ({ int __ret_pu; __typeof__(*(datap)) __pu_val; (void)0; might_fault(); __pu_val = tmp; switch (sizeof(*(datap))) { case 1: asm volatile("call __put_user_" "1" : "=a" (__ret_pu) : "0" ((typeof(*(datap)))(__pu_val)), "c" (datap) : "ebx"); break; case 2: asm volatile("call __put_user_" "2" : "=a" (__ret_pu) : "0" ((typeof(*(datap)))(__pu_val)), "c" (datap) : "ebx"); break; case 4: asm volatile("call __put_user_" "4" : "=a" (__ret_pu) : "0" ((typeof(*(datap)))(__pu_val)), "c" (datap) : "ebx"); break; case 8: asm volatile("call __put_user_8" : "=a" (__ret_pu) : "A" ((typeof(*(datap)))(__pu_val)), "c" (datap) : "ebx"); break; default: asm volatile("call __put_user_" "X" : "=a" (__ret_pu) : "0" ((typeof(*(datap)))(__pu_val)), "c" (datap) : "ebx"); break; } __ret_pu; });
                break;
            }

            case 6: /* write the word at location addr in the USER area */
                ret = -5;
                if ((addr & (sizeof(data) - 1)) || addr < 0 ||
                    addr >= sizeof(struct user))
                    break;

                if (addr < sizeof(struct user_regs_struct))
                    ret = putreg(child, addr, data);
                else if (addr >= __builtin_offsetof(struct user,u_debugreg[0]) &&
                     addr <= __builtin_offsetof(struct user,u_debugreg[7])) {
                    addr -= __builtin_offsetof(struct user,u_debugreg[0]);
                    ret = ptrace_set_debugreg(child,
                                  addr / sizeof(data), data);
                }
                break;

            case 12:	/* Get all gp regs from the child. */
                return copy_regset_to_user(child,
                               task_user_regset_view(get_current()),
                               REGSET_GENERAL,
                               0, sizeof(struct user_regs_struct),
                               datap);

            case 13:	/* Set all gp regs in the child. */
                return copy_regset_from_user(child,
                                 task_user_regset_view(get_current()),
                                 REGSET_GENERAL,
                                 0, sizeof(struct user_regs_struct),
                                 datap);

            case 14:	/* Get the child FPU state. */
                return copy_regset_to_user(child,
                               task_user_regset_view(get_current()),
                               REGSET_FP,
                               0, sizeof(struct user_i387_struct),
                               datap);

            case 15:	/* Set the child FPU state. */
                return copy_regset_from_user(child,
                                 task_user_regset_view(get_current()),
                                 REGSET_FP,
                                 0, sizeof(struct user_i387_struct),
                                 datap);


            case 18:	/* Get the child extended FPU state. */
                return copy_regset_to_user(child, &user_x86_32_view,
                               REGSET_XFP,
                               0, sizeof(struct user_fxsr_struct),
                               datap) ? -5 : 0;

            case 19:	/* Set the child extended FPU state. */
                return copy_regset_from_user(child, &user_x86_32_view,
                                 REGSET_XFP,
                                 0, sizeof(struct user_fxsr_struct),
                                 datap) ? -5 : 0;


            case 25:
                if (addr < 0)
                    return -5;
                ret = do_get_thread_area(child, addr,
                             (struct user_desc  *) data);
                break;

            case 26:
                if (addr < 0)
                    return -5;
                ret = do_set_thread_area(child, addr,
                             (struct user_desc  *) data, 0);
                break;









            /*
             * These bits need more cooking - not enabled yet:
             */





























            default:
                ret = ptrace_request(child, request, addr, data);
                break;
            }

            return ret;
        }""", p.phrase(p.translationUnit))

}