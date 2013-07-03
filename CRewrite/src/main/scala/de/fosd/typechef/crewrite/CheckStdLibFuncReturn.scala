package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureModel}
import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional.Opt

// https://www.securecoding.cert.org/confluence/display/seccode/ERR33-C.+Detect+and+handle+standard+library+errors
// ERR33-C
class CheckStdLibFuncReturn(env: ASTEnv, fm: FeatureModel) {

    // list of standard library functions and their possible error returns
    // taken from above website
    private val functionErrorReturns: Map[String, List[AST]] = Map(
        ("aligned_alloc", List(Id("NULL"))),
        ("asctime_s",     List(NArySubExpr("!=",Constant("0")))),
        ("at_quick_exit", List(NArySubExpr("!=",Constant("0")))),
        ("atexit",        List(NArySubExpr("!=",Constant("0")))),
        ("bsearch",       List(Id("NULL"))),
        ("bsearch_s",     List(Id("NULL"))),
        ("btowc",         List(Constant("0xffffffffu"))), // WEOF; see wchar.h
        ("c16rtomb",      List(CastExpr(TypeName(List(Opt(FeatureExprFactory.True, TypeDefTypeSpecifier(Id("size_t")))),None),UnaryOpExpr("-",Constant("1"))))),
        ("c32rtomb",      List(CastExpr(TypeName(List(Opt(FeatureExprFactory.True, TypeDefTypeSpecifier(Id("size_t")))),None),UnaryOpExpr("-",Constant("1"))))),
        ("calloc",        List(Id("NULL"))),
        ("clock",         List(CastExpr(TypeName(List(Opt(FeatureExprFactory.True, TypeDefTypeSpecifier(Id("clock_t")))),None),UnaryOpExpr("-",Constant("1"))))),
        ("cnd_broadcast", List(Id("thrd_error"))),        // see http://en.cppreference.com/w/c/thread/thrd_errors
        ("cnd_init",      List(Id("thrd_nomem"), Id("thrd_error"))),
        ("cnd_signal",    List(Id("thrd_error"))),
        ("cnd_timedwait", List(Id("thrd_timedout"), Id("thrd_error"))),
        ("cnd_wait",      List(Id("thrd_error"))),
        ("ctime_s",       List()),                 // Nonzero
        ("fclose",        List(Constant("-1"))),   // EOF; system dependant, but usually -1
        ("fflush",        List(Constant("-1"))),   // EOF
        ("fgetc",         List(Constant("-1"))),   // EOF
        ("fgetwc",        List()),
        ("fopen",         List(Id("NULL"))),
        ("fopen_s",       List()),                 // Nonzero
        ("fprintf",       List()),                 // Negative
        ("fprintf_s",     List()),                 // Negative
        ("fputc",         List(Constant("-1"))),   // EOF
        ("fputs",         List(Constant("-1"))),   // EOF (negative)
        ("fputws",        List(Constant("-1"))),   // EOF (negative)
        ("fread",         List()),                 // elements read
        ("freopen",       List(Id("NULL"))),
        ("freopen_s",     List()),                 // Nonzero
        ("fscanf",        List(Constant("-1"))),   // EOF (negative)
        ("fscanf_s",      List(Constant("-1"))),   // EOF (negative)
        ("fseek",         List()),                 // Nonzero
        ("fsetpos",       List()),                 // Nonzero
        ("ftell",         List(UnaryOpExpr("-",Constant("1L")))),
        ("fwprintf",      List()),                 // Negative
        ("fwprintf_s",    List()),                 // Negative
        ("fwrite",        List()),                 // Elements written
        ("fwscanf",       List(Constant("-1"))),   // EOF (negative)
        ("fwscanf_s",     List(Constant("-1"))),   // EOF (negative)
        ("getc",          List(Constant("-1")))    // EOF
        // to be continued ...
    )
}
