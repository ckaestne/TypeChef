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
        """static char vmac_string[128] = {'\x01', '\x01', '\x01', '\x01',
                                '\x02', '\x03', '\x02', '\x02',
                                '\x02', '\x04', '\x01', '\x07',
                                '\x04', '\x01', '\x04', '\x03',};""", p.phrase(p.translationUnit))


    def testLinux_mmu = {
        assertParseable("""return (gpte & ((((1ULL << 52) - 1) & ~(u64)(((1UL) << 12)-1)) & ~((1ULL << (12 + (((lvl) - 1) * 9))) - 1))) >> 12;""", p.phrase(p.statement))
        assertParseable(
            """typedef unsigned long  gfn_t;
            static gfn_t paging64_gpte_to_gfn_lvl(int gpte, int lvl)
            {
                return (gpte & ((((1ULL << 52) - 1) & ~(u64)(((1UL) << 12)-1)) & ~((1ULL << (12 + (((lvl) - 1) * 9))) - 1))) >> 12;
            }""", p.phrase(p.translationUnit))
    }

    def testLinux2 {
        assertParseable(
            """  __builtin_type *""", p.phrase(p.typeName))
        assertParseable(
            """    ap""", p.phrase(p.expr))
        assertParseable(
            """    __builtin_va_arg(ap,__builtin_type *)""", p.phrase(p.primaryExpr))
        assertParseable(
            """    string = __builtin_va_arg(ap,__builtin_type *);""", p.phrase(p.statement))
    }

    def testLinux3 {
    }

    def testLinux4 {
        assertParseable("""(__builtin_constant_p((6)) ? __constant_memcpy3d((&mesg->content.normal.mac_addr), (mac_addr), (6)) : __memcpy3d((&mesg->content.normal.mac_addr), (mac_addr), (6)));""", p.phrase(p.statement))
        assertParseable("""__memcpy((&mesg->content.normal.mac_addr), (mac_addr), (6));""", p.phrase(p.statement))


    }

    def testLinux4b {
        assertParseable("""	if (mac_addr)

#if (((!definedEx(CONFIG_PARAVIRT) || definedEx(CONFIG_X86_USE_3DNOW) || !definedEx(CONFIG_KMEMCHECK)) && (definedEx(CONFIG_X86_USE_3DNOW) || definedEx(CONFIG_KMEMCHECK) || definedEx(CONFIG_PARAVIRT)) && definedEx(CONFIG_PARAVIRT) && definedEx(CONFIG_X86_USE_3DNOW) && (!definedEx(CONFIG_PARAVIRT) || definedEx(CONFIG_X86_USE_3DNOW) || definedEx(CONFIG_KMEMCHECK)) && (definedEx(CONFIG_X86_USE_3DNOW) || definedEx(CONFIG_PARAVIRT) || !definedEx(CONFIG_KMEMCHECK))) || (definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_PARAVIRT)))
(__builtin_constant_p((6)) ? __constant_memcpy3d((&mesg->content.normal.mac_addr), (mac_addr), (6)) : __memcpy3d((&mesg->content.normal.mac_addr), (mac_addr), (6)))
#endif
#if ((definedEx(CONFIG_X86_USE_3DNOW) || definedEx(CONFIG_PARAVIRT) || !definedEx(CONFIG_KMEMCHECK)) && ((definedEx(CONFIG_PARAVIRT) && !definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_KMEMCHECK) && (!definedEx(CONFIG_PARAVIRT) || definedEx(CONFIG_X86_USE_3DNOW) || !definedEx(CONFIG_KMEMCHECK))) || (!definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_KMEMCHECK) && !definedEx(CONFIG_PARAVIRT))) && (definedEx(CONFIG_PARAVIRT) || !definedEx(CONFIG_X86_USE_3DNOW)) && ((definedEx(CONFIG_PARAVIRT) && !definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_KMEMCHECK)) || (!definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_KMEMCHECK)) || !definedEx(CONFIG_X86_USE_3DNOW) || (!definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_PARAVIRT) && definedEx(CONFIG_KMEMCHECK)) || !definedEx(CONFIG_PARAVIRT) || (!definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_KMEMCHECK) && !definedEx(CONFIG_PARAVIRT))))
__builtin_memcpy(&mesg->content.normal.mac_addr, mac_addr, 6)
#endif
#if ((definedEx(CONFIG_PARAVIRT) || !definedEx(CONFIG_X86_USE_3DNOW)) && ((definedEx(CONFIG_PARAVIRT) && !definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_KMEMCHECK) && (definedEx(CONFIG_X86_USE_3DNOW) || definedEx(CONFIG_KMEMCHECK) || definedEx(CONFIG_PARAVIRT))) || (!definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_PARAVIRT) && definedEx(CONFIG_KMEMCHECK))) && ((definedEx(CONFIG_PARAVIRT) && !definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_KMEMCHECK)) || (!definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_KMEMCHECK)) || !definedEx(CONFIG_X86_USE_3DNOW) || (!definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_PARAVIRT) && definedEx(CONFIG_KMEMCHECK)) || !definedEx(CONFIG_PARAVIRT) || (!definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_KMEMCHECK) && !definedEx(CONFIG_PARAVIRT))) && ((!definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_PARAVIRT) && definedEx(CONFIG_KMEMCHECK)) || ((definedEx(CONFIG_X86_USE_3DNOW) || definedEx(CONFIG_KMEMCHECK) || definedEx(CONFIG_PARAVIRT)) && (!definedEx(CONFIG_PARAVIRT) || definedEx(CONFIG_X86_USE_3DNOW) || definedEx(CONFIG_KMEMCHECK) || (definedEx(CONFIG_PARAVIRT) && !definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_KMEMCHECK)))) || (definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_PARAVIRT))))
__memcpy((&mesg->content.normal.mac_addr), (mac_addr), (6))
#endif
;
	else
		mesg->content.normal.targetless_le_arp = 1;""", p.phrase(p.statement))


    }

    def testLinux4c =
        assertParseable("""/*
 * LANE2: new argument struct sk_buff *data contains
 * the LE_ARP based TLVs introduced in the LANE2 spec
 */
 typedef int atmlec_msg_type;       typedef int gfp_t;
 typedef int mac_addr;
static int
send_to_lecd(struct lec_priv *priv, atmlec_msg_type type,
	     const unsigned char *mac_addr, const unsigned char *atm_addr,
	     struct sk_buff *data)
{
	struct sock *sk;
	struct sk_buff *skb;
	struct atmlec_msg *mesg;

	if (!priv || !priv->lecd) {
		return -1;
	}
	skb = alloc_skb(sizeof(struct atmlec_msg), ((( gfp_t)0x20u)));
	if (!skb)
		return -1;
	skb->len = sizeof(struct atmlec_msg);
	mesg = (struct atmlec_msg *)skb->data;
	__builtin_memset(mesg, 0, sizeof(struct atmlec_msg));
	mesg->type = type;
	if (data != ((void *)0))
		mesg->sizeoftlvs = data->len;
	if (mac_addr)

#if (((!definedEx(CONFIG_PARAVIRT) || definedEx(CONFIG_X86_USE_3DNOW) || !definedEx(CONFIG_KMEMCHECK)) && (definedEx(CONFIG_X86_USE_3DNOW) || definedEx(CONFIG_KMEMCHECK) || definedEx(CONFIG_PARAVIRT)) && definedEx(CONFIG_PARAVIRT) && definedEx(CONFIG_X86_USE_3DNOW) && (!definedEx(CONFIG_PARAVIRT) || definedEx(CONFIG_X86_USE_3DNOW) || definedEx(CONFIG_KMEMCHECK)) && (definedEx(CONFIG_X86_USE_3DNOW) || definedEx(CONFIG_PARAVIRT) || !definedEx(CONFIG_KMEMCHECK))) || (definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_PARAVIRT)))
(__builtin_constant_p((6)) ? __constant_memcpy3d((&mesg->content.normal.mac_addr), (mac_addr), (6)) : __memcpy3d((&mesg->content.normal.mac_addr), (mac_addr), (6)))
#endif
#if ((definedEx(CONFIG_X86_USE_3DNOW) || definedEx(CONFIG_PARAVIRT) || !definedEx(CONFIG_KMEMCHECK)) && ((definedEx(CONFIG_PARAVIRT) && !definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_KMEMCHECK) && (!definedEx(CONFIG_PARAVIRT) || definedEx(CONFIG_X86_USE_3DNOW) || !definedEx(CONFIG_KMEMCHECK))) || (!definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_KMEMCHECK) && !definedEx(CONFIG_PARAVIRT))) && (definedEx(CONFIG_PARAVIRT) || !definedEx(CONFIG_X86_USE_3DNOW)) && ((definedEx(CONFIG_PARAVIRT) && !definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_KMEMCHECK)) || (!definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_KMEMCHECK)) || !definedEx(CONFIG_X86_USE_3DNOW) || (!definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_PARAVIRT) && definedEx(CONFIG_KMEMCHECK)) || !definedEx(CONFIG_PARAVIRT) || (!definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_KMEMCHECK) && !definedEx(CONFIG_PARAVIRT))))
__builtin_memcpy(&mesg->content.normal.mac_addr, mac_addr, 6)
#endif
#if ((definedEx(CONFIG_PARAVIRT) || !definedEx(CONFIG_X86_USE_3DNOW)) && ((definedEx(CONFIG_PARAVIRT) && !definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_KMEMCHECK) && (definedEx(CONFIG_X86_USE_3DNOW) || definedEx(CONFIG_KMEMCHECK) || definedEx(CONFIG_PARAVIRT))) || (!definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_PARAVIRT) && definedEx(CONFIG_KMEMCHECK))) && ((definedEx(CONFIG_PARAVIRT) && !definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_KMEMCHECK)) || (!definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_KMEMCHECK)) || !definedEx(CONFIG_X86_USE_3DNOW) || (!definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_PARAVIRT) && definedEx(CONFIG_KMEMCHECK)) || !definedEx(CONFIG_PARAVIRT) || (!definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_KMEMCHECK) && !definedEx(CONFIG_PARAVIRT))) && ((!definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_PARAVIRT) && definedEx(CONFIG_KMEMCHECK)) || ((definedEx(CONFIG_X86_USE_3DNOW) || definedEx(CONFIG_KMEMCHECK) || definedEx(CONFIG_PARAVIRT)) && (!definedEx(CONFIG_PARAVIRT) || definedEx(CONFIG_X86_USE_3DNOW) || definedEx(CONFIG_KMEMCHECK) || (definedEx(CONFIG_PARAVIRT) && !definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_KMEMCHECK)))) || (definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_PARAVIRT))))
__memcpy((&mesg->content.normal.mac_addr), (mac_addr), (6))
#endif
;
	else
		mesg->content.normal.targetless_le_arp = 1;
	if (atm_addr)

#if (((!definedEx(CONFIG_PARAVIRT) || definedEx(CONFIG_X86_USE_3DNOW) || !definedEx(CONFIG_KMEMCHECK)) && (definedEx(CONFIG_X86_USE_3DNOW) || definedEx(CONFIG_KMEMCHECK) || definedEx(CONFIG_PARAVIRT)) && definedEx(CONFIG_PARAVIRT) && definedEx(CONFIG_X86_USE_3DNOW) && (!definedEx(CONFIG_PARAVIRT) || definedEx(CONFIG_X86_USE_3DNOW) || definedEx(CONFIG_KMEMCHECK)) && (definedEx(CONFIG_X86_USE_3DNOW) || definedEx(CONFIG_PARAVIRT) || !definedEx(CONFIG_KMEMCHECK))) || (definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_PARAVIRT)))
(__builtin_constant_p((20)) ? __constant_memcpy3d((&mesg->content.normal.atm_addr), (atm_addr), (20)) : __memcpy3d((&mesg->content.normal.atm_addr), (atm_addr), (20)))
#endif
#if ((definedEx(CONFIG_X86_USE_3DNOW) || definedEx(CONFIG_PARAVIRT) || !definedEx(CONFIG_KMEMCHECK)) && ((definedEx(CONFIG_PARAVIRT) && !definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_KMEMCHECK) && (!definedEx(CONFIG_PARAVIRT) || definedEx(CONFIG_X86_USE_3DNOW) || !definedEx(CONFIG_KMEMCHECK))) || (!definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_KMEMCHECK) && !definedEx(CONFIG_PARAVIRT))) && (definedEx(CONFIG_PARAVIRT) || !definedEx(CONFIG_X86_USE_3DNOW)) && ((definedEx(CONFIG_PARAVIRT) && !definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_KMEMCHECK)) || (!definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_KMEMCHECK)) || !definedEx(CONFIG_X86_USE_3DNOW) || (!definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_PARAVIRT) && definedEx(CONFIG_KMEMCHECK)) || !definedEx(CONFIG_PARAVIRT) || (!definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_KMEMCHECK) && !definedEx(CONFIG_PARAVIRT))))
__builtin_memcpy(&mesg->content.normal.atm_addr, atm_addr, 20)
#endif
#if ((definedEx(CONFIG_PARAVIRT) || !definedEx(CONFIG_X86_USE_3DNOW)) && ((definedEx(CONFIG_PARAVIRT) && !definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_KMEMCHECK) && (definedEx(CONFIG_X86_USE_3DNOW) || definedEx(CONFIG_KMEMCHECK) || definedEx(CONFIG_PARAVIRT))) || (!definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_PARAVIRT) && definedEx(CONFIG_KMEMCHECK))) && ((definedEx(CONFIG_PARAVIRT) && !definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_KMEMCHECK)) || (!definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_KMEMCHECK)) || !definedEx(CONFIG_X86_USE_3DNOW) || (!definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_PARAVIRT) && definedEx(CONFIG_KMEMCHECK)) || !definedEx(CONFIG_PARAVIRT) || (!definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_KMEMCHECK) && !definedEx(CONFIG_PARAVIRT))) && ((!definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_PARAVIRT) && definedEx(CONFIG_KMEMCHECK)) || ((definedEx(CONFIG_X86_USE_3DNOW) || definedEx(CONFIG_KMEMCHECK) || definedEx(CONFIG_PARAVIRT)) && (!definedEx(CONFIG_PARAVIRT) || definedEx(CONFIG_X86_USE_3DNOW) || definedEx(CONFIG_KMEMCHECK) || (definedEx(CONFIG_PARAVIRT) && !definedEx(CONFIG_X86_USE_3DNOW) && definedEx(CONFIG_KMEMCHECK)))) || (definedEx(CONFIG_X86_USE_3DNOW) && !definedEx(CONFIG_PARAVIRT))))
__memcpy((&mesg->content.normal.atm_addr), (atm_addr), (20))
#endif
;

	atm_force_charge(priv->lecd, skb->truesize);
	sk = sk_atm(priv->lecd);
	skb_queue_tail(&sk->sk_receive_queue, skb);
	sk->sk_data_ready(sk, skb->len);

	if (data != ((void *)0)) {

#if definedEx(CONFIG_DYNAMIC_DEBUG)
do { static struct _ddebug descriptor __attribute__((__used__)) __attribute__((section("__verbose"), aligned(8))) = { "lec", __func__, "/app/home/kaestner/TypeChef/linux-2.6.33.3/net/atm/lec.c", "lec: about to send %d bytes of data\n", DEBUG_HASH, DEBUG_HASH2, 636, 0 }; if (({ int __ret = 0; if (__builtin_expect(!!((dynamic_debug_enabled &(1LL << DEBUG_HASH)) &&(dynamic_debug_enabled2 &(1LL << DEBUG_HASH2))), 0)) if (__builtin_expect(!!(descriptor.flags), 0)) __ret = 1; __ret; })) printk("<7>" "lec: about to send %d bytes of data\n",data->len); } while (0)
#endif
#if !definedEx(CONFIG_DYNAMIC_DEBUG)
({ if (0) printk("<7>" "lec: about to send %d bytes of data\n",data->len); 0; })
#endif
;
		atm_force_charge(priv->lecd, data->truesize);
		skb_queue_tail(&sk->sk_receive_queue, data);
		sk->sk_data_ready(sk, skb->len);
	}

	return 0;
}""", p.phrase(p.translationUnit))


    def testLinux5 = assertParseable(
        """void foo(){
        return 0;
nla_put_failure: __attribute__ ((unused))
        return -1;
}""", p.phrase(p.translationUnit))

    def testLinux6 = assertParseable(
        """
#if definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU)extern __attribute__((section(".discard"), unused)) char __pcpu_scope_this_cpu_off; extern __attribute__((section(#if definedEx(CONFIG_SMP)
".data.percpu"
#endif
#if !definedEx(CONFIG_SMP)
".data"
#endif
 "")))  __typeof__(unsigned long) per_cpu__this_cpu_off
#endif
#if !definedEx(CONFIG_DEBUG_FORCE_WEAK_PER_CPU)
extern __attribute__((section(
#if definedEx(CONFIG_SMP)
".data.percpu"
#endif
#if !definedEx(CONFIG_SMP)
".data"
#endif
 "")))  __typeof__(unsigned long) per_cpu__this_cpu_off
#endif
;
""", p.phrase(p.translationUnit))


}