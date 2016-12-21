package de.fosd.typechef.typesystem

import de.fosd.typechef.featureexpr.FeatureExprFactory.True
import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import de.fosd.typechef.lexer.LexerFrontend

/**
 * all compiler-specific built-in stuff
 */
trait CBuiltIn extends CEnv with CTypes with CDeclTyping {

    import CType.makeCType

    lazy val initialEnv = new Env(
        new ConditionalTypeMap() ++ initBuiltinTypedevEnv,
        new VarTypingContext() ++ initBuiltinVarEnv,
        new StructEnv(), Map(), Map(), None, 0, FeatureExprFactory.False, Nil, None)

    val initBuiltinTypedevEnv: Seq[(String, FeatureExpr, (AST, Conditional[CType]))] =
        Map(
            "__builtin_va_list" -> CBuiltinVaList().toCType
        ).toList.map(x => (x._1, True, (null, One(x._2))))


    lazy val initBuiltinVarEnv: Seq[(String, FeatureExpr, AST, Conditional[CType], DeclarationKind, Int, Linkage)] =
        (declare_builtin_functions() ++ Map(
            ("__builtin_expect", One(CFunction(Seq(CLong().toCType,CLong().toCType), CLong().toCType).toCType)),  // see http://gcc.gnu.org/onlinedocs/gcc/Other-Builtins.html for signature
            ("__builtin_safe_p", One(CFunction(Seq(CVarArgs()), CInt().toCType).toCType)),
            ("__builtin_warning", One(CFunction(Seq(CVarArgs()), CInt().toCType).toCType)),
            ("__builtin_choose_expr", One(CFunction(Seq(CVarArgs()), CInt().toCType).toCType)),
            ("__builtin_constant_p", One(CFunction(Seq(CVarArgs()), CInt().toCType).toCType)),
            ("__builtin_va_start", One(CFunction(Seq(CBuiltinVaList(), CVarArgs()), CVoid()).toCType)), //ignore most of these...
            //            "__builtin_va_arg", One(CFunction(Seq(CIgnore(), CIgnore()), CIgnore())),//handled differently in parser
            ("__builtin_va_end", One(CFunction(Seq(CBuiltinVaList()), CVoid()).toCType)),
            ("__builtin_va_copy", One(CFunction(Seq(CBuiltinVaList(), CBuiltinVaList()), CVoid()).toCType)),
            ("__builtin_bswap16", One(CFunction(Seq(CUnsigned(CInt()).toCType), CUnsigned(CInt()).toCType).toCType)),
            ("__builtin_bswap32", One(CFunction(Seq(CUnsigned(CInt()).toCType), CUnsigned(CInt()).toCType).toCType)),
            ("__builtin_bswap64", One(CFunction(Seq(CUnsigned(CLongLong()).toCType), CUnsigned(CLongLong()).toCType).toCType))
        )).toList.map(x => (x._1, True, null, x._2, KDeclaration, 0, ExternalLinkage))


    /** taken directly from sparse/lib.c */
    private def declare_builtin_functions(): Map[String, Conditional[CType]] = {
        var buffer = "";
        def add_pre_buffer(str: String) {
            buffer = buffer + str
        }
        {
            buffer = "#define __SIZE_TYPE__ unsigned int\n"

            /* Gaah. gcc knows tons of builtin <string.h> functions */
            add_pre_buffer("extern void *__builtin_memcpy(void *, const void *, __SIZE_TYPE__);\n");
            add_pre_buffer("extern void *__builtin_mempcpy(void *, const void *, __SIZE_TYPE__);\n");
            add_pre_buffer("extern void *__builtin_memset(void *, int, __SIZE_TYPE__);\n");
            add_pre_buffer("extern int __builtin_memcmp(const void *, const void *, __SIZE_TYPE__);\n");
            add_pre_buffer("extern char *__builtin_strcat(char *, const char *);\n");
            add_pre_buffer("extern char *__builtin_strncat(char *, const char *, __SIZE_TYPE__);\n");
            add_pre_buffer("extern int __builtin_strcmp(const char *, const char *);\n");
            add_pre_buffer("extern char *__builtin_strchr(const char *, int);\n");
            add_pre_buffer("extern char *__builtin_strcpy(char *, const char *);\n");
            add_pre_buffer("extern char *__builtin_strncpy(char *, const char *, __SIZE_TYPE__);\n");
            add_pre_buffer("extern __SIZE_TYPE__ __builtin_strspn(const char *, const char *);\n");
            add_pre_buffer("extern __SIZE_TYPE__ __builtin_strcspn(const char *, const char *);\n");
            add_pre_buffer("extern char * __builtin_strpbrk(const char *, const char *);\n");
            add_pre_buffer("extern __SIZE_TYPE__ __builtin_strlen(const char *);\n");

            /* And bitwise operations.. */
            add_pre_buffer("extern int __builtin_clz(int);\n");
            add_pre_buffer("extern int __builtin_clzl(long);\n");
            add_pre_buffer("extern int __builtin_clzll(long long);\n");
            add_pre_buffer("extern int __builtin_ctz(int);\n");
            add_pre_buffer("extern int __builtin_ctzl(long);\n");
            add_pre_buffer("extern int __builtin_ctzll(long long);\n");
            add_pre_buffer("extern int __builtin_ffs(int);\n");
            add_pre_buffer("extern int __builtin_ffsl(long);\n");
            add_pre_buffer("extern int __builtin_ffsll(long long);\n");
            add_pre_buffer("extern int __builtin_popcount(unsigned int);\n");
            add_pre_buffer("extern int __builtin_popcountl(unsigned long);\n");
            add_pre_buffer("extern int __builtin_popcountll(unsigned long long);\n");

            /* And some random ones.. */
            add_pre_buffer("extern void *__builtin_return_address(unsigned int);\n");
            add_pre_buffer("extern void *__builtin_extract_return_addr(void *);\n");
            add_pre_buffer("extern void *__builtin_frame_address(unsigned int);\n");
            add_pre_buffer("extern void __builtin_trap(void);\n");
            add_pre_buffer("extern void *__builtin_alloca(__SIZE_TYPE__);\n");
            add_pre_buffer("extern void __builtin_prefetch (const void *, ...);\n");
            add_pre_buffer("extern long __builtin_alpha_extbl(long, long);\n");
            add_pre_buffer("extern long __builtin_alpha_extwl(long, long);\n");
            add_pre_buffer("extern long __builtin_alpha_insbl(long, long);\n");
            add_pre_buffer("extern long __builtin_alpha_inswl(long, long);\n");
            add_pre_buffer("extern long __builtin_alpha_insql(long, long);\n");
            add_pre_buffer("extern long __builtin_alpha_inslh(long, long);\n");
            add_pre_buffer("extern long __builtin_alpha_cmpbge(long, long);\n");
            add_pre_buffer("extern long __builtin_labs(long);\n");
            add_pre_buffer("extern double __builtin_fabs(double);\n");

            //	/* Add Blackfin-specific stuff */
            //	add_pre_buffer(
            //		"#ifdef __bfin__\n"
            //		"extern void __builtin_bfin_csync(void);\n"
            //		"extern void __builtin_bfin_ssync(void);\n"
            //		"extern int __builtin_bfin_norm_fr1x32(int);\n"
            //		"#endif\n"
            //	);

            /* And some floating point stuff.. */
            add_pre_buffer("extern int __builtin_isgreater(float, float);\n");
            add_pre_buffer("extern int __builtin_isgreaterequal(float, float);\n");
            add_pre_buffer("extern int __builtin_isless(float, float);\n");
            add_pre_buffer("extern int __builtin_islessequal(float, float);\n");
            add_pre_buffer("extern int __builtin_islessgreater(float, float);\n");
            add_pre_buffer("extern int __builtin_isunordered(float, float);\n");

            /* And some __FORTIFY_SOURCE ones.. */
            add_pre_buffer("extern __SIZE_TYPE__ __builtin_object_size(void *, int);\n");
            add_pre_buffer("extern void * __builtin___memcpy_chk(void *, const void *, __SIZE_TYPE__, __SIZE_TYPE__);\n");
            add_pre_buffer("extern void * __builtin___memmove_chk(void *, const void *, __SIZE_TYPE__, __SIZE_TYPE__);\n");
            add_pre_buffer("extern void * __builtin___mempcpy_chk(void *, const void *, __SIZE_TYPE__, __SIZE_TYPE__);\n");
            add_pre_buffer("extern void * __builtin___memset_chk(void *, int, __SIZE_TYPE__, __SIZE_TYPE__);\n");
            add_pre_buffer("extern int __builtin___sprintf_chk(char *, int, __SIZE_TYPE__, const char *, ...);\n");
            add_pre_buffer("extern int __builtin___snprintf_chk(char *, __SIZE_TYPE__, int , __SIZE_TYPE__, const char *, ...);\n");
            add_pre_buffer("extern char * __builtin___stpcpy_chk(char *, const char *, __SIZE_TYPE__);\n");
            add_pre_buffer("extern char * __builtin___strcat_chk(char *, const char *, __SIZE_TYPE__);\n");
            add_pre_buffer("extern char * __builtin___strcpy_chk(char *, const char *, __SIZE_TYPE__);\n");
            add_pre_buffer("extern char * __builtin___strncat_chk(char *, const char *, __SIZE_TYPE__, __SIZE_TYPE__);\n");
            add_pre_buffer("extern char * __builtin___strncpy_chk(char *, const char *, __SIZE_TYPE__, __SIZE_TYPE__);\n");
            add_pre_buffer("extern int __builtin___vsprintf_chk(char *, int, __SIZE_TYPE__, const char *, __builtin_va_list);\n");
            add_pre_buffer("extern int __builtin___vsnprintf_chk(char *, __SIZE_TYPE__, int, __SIZE_TYPE__, const char *, __builtin_va_list ap);\n");
            add_pre_buffer("extern void __builtin_unreachable(void);\n");

            //__func__
            add_pre_buffer("static const char __func__[];\n")
            add_pre_buffer("static const char __PRETTY_FUNCTION__[];\n")
        }

        val ast = getAST(buffer)
        val env = EmptyEnv.addTypedef("__builtin_va_list", True, null, One(CIgnore()))
        Map() ++ (for (Opt(_, decl: Declaration) <- ast.defs) yield {
            val init = decl.init.head.entry
            (init.declarator.getName -> getDeclaratorType(init.declarator, constructType(decl.declSpecs, FeatureExprFactory.True, EmptyEnv, decl), FeatureExprFactory.True, env))
        })
    }

    private def getAST(code: String): TranslationUnit = {
        import scala.collection.JavaConverters._
        val ast: AST = new ParserMain(new CParser).parserMain(
            CLexerAdapter.prepareTokens(new LexerFrontend().parse(code).asScala), SilentParserOptions, null)
        assert(ast != null)
        ast.asInstanceOf[TranslationUnit]
    }
}