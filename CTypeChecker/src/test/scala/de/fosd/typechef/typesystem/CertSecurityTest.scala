package de.fosd.typechef.typesystem

import de.fosd.typechef.error.Severity
import de.fosd.typechef.featureexpr.FeatureExprFactory
import de.fosd.typechef.parser.c._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSuite, Matchers}

/**
 * test for various CERT rules implemented in typechef
 */
@RunWith(classOf[JUnitRunner])
class CertSecurityTest extends FunSuite with Matchers with TestHelper {

    private def checkExpr(code: String, enableAnalysis: Boolean, printAST: Boolean = false): Boolean =
        check("void main() { " + code + "}", enableAnalysis, printAST)

    private def check(code: String, enableAnalysis: Boolean, printAST: Boolean = false): Boolean = {
//        println("checking " + code);
        if (printAST)
            println("AST: " + getAST(code));
        check(getAST(code), enableAnalysis);
    }
    private def check(ast: TranslationUnit, enableAnalysis: Boolean): Boolean = {
        assert(ast != null, "void ast");
        new CTypeSystemFrontend(ast, FeatureExprFactory.default.featureModelFactory.empty,
            if (enableAnalysis) new LinuxDefaultOptions {
                override def warning_long_designator = true
                override def warning_conflicting_linkage = true
                override def warning_implicit_identifier = true
                override def warning_volatile = true
                override def warning_const_assignment = true
                override def warning_character_signed = true
            } else LinuxDefaultOptions
        ).makeSilent().checkAST(false).filterNot(_.severity==Severity.Warning).isEmpty
    }

    def correct(code: String) {
        assert(check(code, false), "Expected correct code, but found error without analysis")
        assert(check(code, true), "Expected correct code, but found error with analysis")
    }
    def error(code: String) {
        assert(check(code, false), "Expected correct code (without analysis), but found error without analysis")
        assert(!check(code, true), "Expected error (with analysis), but found no error with analysis")
    }
    def correctExpr(code: String) {
        assert(checkExpr(code, false), "Expected correct code, but found error without analysis")
        assert(checkExpr(code, true), "Expected correct code, but found error with analysis")
    }
    def errorExpr(code: String) {
        assert(checkExpr(code, false), "Expected correct code (without analysis), but found error without analysis")
        assert(!checkExpr(code, true), "Expected error (with analysis), but analysis did not find an error")
    }


    /**
     * very simple, structural rule
     */
    test("DCL16-C. Use L, not l, to indicate a long value") {
        correctExpr("long a = 1L;")
        errorExpr("long a = 1l;")
    }

    /**
     * very simple, structural rule
     */
    test("DCL31-C. Declare identifiers before using them -- implicit int/return") {
        correct("extern int foo;")
        error("extern foo;")

        error("foo(void){return 1;}")
    }

    /**
     * less simple type system rule
     */
    test("DCL31-C. Declare identifiers before using them -- implicit function declaration") {
        //we are actually stronger than the specification (it fails without activating warnings)
        assert(!check(
            """
              |int main(void) {
              |  int c = foo();
              |  return 0;
              |}
              |
              |int foo(int a) {
              |  return a;
              |}
            """.stripMargin, true))
    }

    /*
     * type system rule, applying to redeclarations of identifiers
     * see https://www.securecoding.cert.org/confluence/display/seccode/DCL36-C.+Do+not+declare+an+identifier+with+conflicting+linkage+classifications
     */
    test("DCL36-C. Do not declare an identifier with conflicting linkage classifications") {
        correct(
            """
              |static int a;
              |static int a;
              |//should be internal
            """.stripMargin)
        error(
            """
              |static int a;
              |int a;
              |//should be undefined
            """.stripMargin)
        correct(
            """
              |static int a;
              |extern int a;
              |//should be internal
            """.stripMargin)

        error(
            """
              |int a;
              |static int a;
              |//should be undefined
            """.stripMargin)

        correct(
            """
              |int a;
              |int a;
              |//should be external
            """.stripMargin)
        correct(
            """
              |int a;
              |extern int a;
              |//should be external
            """.stripMargin)
        error(
            """
              |extern int a;
              |static int a;
              |//should be undefined
            """.stripMargin)
        correct(
            """
              |extern int a;
              |int a;
              |//should be external
            """.stripMargin)
        correct(
            """
              |extern int a;
              |extern int a;
              |//should be external
            """.stripMargin)
    }


    /**
     * less simple type system rule, requires tracking volatile
     * (implemented for variables, not necessarily for function parameters if applicable there as well)
     */
    test("EXP32-C. Do not access a volatile object through a non-volatile reference") {
        correctExpr(
            """
              |static volatile int **ipp;
              |static volatile int *ip;
              |static volatile int i = 0;
              |
              |ipp = &ip;
              |*ipp = &i;
              |if (*ip != 0) {
              |  /* ... */
              |}
            """.stripMargin)


        errorExpr(
            """
              |static volatile int **ipp;
              |static int *ip;
              |static volatile int i = 0;
              |
              |//printf("i = %d.\n", i);
              |
              |ipp = &ip; /* produces warnings in modern compilers */
              |*ipp = &i; /* valid */
              |if (*ip != 0) { /* valid */
              |  /* ... */
              |}
            """.stripMargin)


        errorExpr(
            """
              |static volatile int **ipp;
              |static int *ip;
              |static volatile int i = 0;
              |
              |//printf("i = %d.\n", i);
              |
              |ipp = (int**) &ip; /* constraint violation, also produces warnings */
              |*ipp = &i; /* valid */
              |if (*ip != 0) { /* valid */
              |  /* ... */
              |}
            """.stripMargin)
    }


    /**
     * less simple type system rule, requires tracking const
     */
    test("EXP40-C. Do not modify constant values") {
        correctExpr(
            """
              |char const **cpp;
              |char *cp;
              |char const c = 'A';
              |
              |*cpp = &c; /* valid */
              |*cp = 'B'; /* valid */
            """.stripMargin)


        errorExpr(
            """
              |char const **cpp;
              |char *cp;
              |
              |cpp = &cp; /* constraint violation */
            """.stripMargin)

        errorExpr(
            """
              |char
              |#ifdef X
              |  const
              |#endif
              |  **cpp;
              |char *cp;
              |
              |cpp = &cp; /* constraint violation */
            """.stripMargin)
    }

    test("EXP05-C. Do not cast away a const qualification") {
        errorExpr(
            """
              |const char s[] = "foo";
              |int main() {
              |  *(char*)s = '\0';
              |}
            """.stripMargin)
        correct(
            """
              |void remove_spaces(char *str, int slen) {
              |  char *p = str;
              |  int i;
              |  for (i = 0; i < slen && str[i]; i++) {
              |    if (str[i] != ' ') *p++ = str[i];
              |  }
              |  *p = '\0';
              |}
            """.stripMargin)
        error(
            """
              |void remove_spaces(const char *str, int slen) {
              |  char *p = (char *)str;
              |  int i;
              |  for (i = 0; i < slen && str[i]; i++) {
              |    if (str[i] != ' ') *p++ = str[i];
              |  }
              |  *p = '\0';
              |}
            """.stripMargin)

        error(
            """
              |void *memset(void *str, int c, int n);
              |void test(){
              |  const int vals[3] = {3, 4, 5};
              |  memset(vals, 0, sizeof(vals));
              |}
            """.stripMargin)
    }

    test("STR04-C. Use plain char for characters in the basic character set") {
        correct(
            """
              |int strlen(const char * p);
              |void foo() {
              |  int len;
              |  char cstr[] = "char string";
              |  len = strlen(cstr);
              |}
            """.stripMargin)

        error(
            """
              |int strlen(const char * p);
              |void foo() {
              |  int len;
              |  signed char cstr[] = "char string";
              |  len = strlen(cstr);
              |}
            """.stripMargin)

        error(
            """
              |int strlen(const char * p);
              |void foo() {
              |  int len;
              |  unsigned char cstr[] = "char string";
              |  len = strlen(cstr);
              |}
            """.stripMargin)

        error(
            """
              |int strlen(const char * p);
              |void foo() {
              |  int len;
              |#ifdef X
              |  unsigned
              |#endif
              |     char cstr[] = "char string";
              |  len = strlen(cstr);
              |}
            """.stripMargin)
    }

}