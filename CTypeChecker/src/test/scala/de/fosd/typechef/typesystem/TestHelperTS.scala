package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c.{TranslationUnit, TestHelper}


trait TestHelperTS extends TestHelper {


    protected def check(code: String, printAST: Boolean = false): Boolean = _check(code, printAST) == CheckResult.Correct

    protected def _check(code: String, printAST: Boolean = false): CheckResult.CheckResult = {
        //        println("checking " + code);
        if (printAST)
            println("AST: " + getAST(code))
        _check(getAST(code))
    }

    protected def check(ast: TranslationUnit): Boolean = _check(ast) == CheckResult.Correct

    protected def _check(ast: TranslationUnit): CheckResult.CheckResult = {
        assert(ast != null, "void ast");
        new CTypeSystemFrontend(ast).makeSilent().checkAST()
    }

    private def checkExpr(code: String, printAST: Boolean = false): Boolean = _checkExpr(code, printAST) == CheckResult.Correct

    private def _checkExpr(code: String, printAST: Boolean = false): CheckResult.CheckResult =
        _check("void main() { " + code + "}", printAST)


    def correct(code: String) {
        assert(_check(code) == CheckResult.Correct, "False positive (expected correct code, but found error)")
    }

    def error(code: String) {
        assert(_check(code) == CheckResult.Error, "False negative (expected error, but found none)")
    }

    def warning(code: String) {
        assert(_check(code) == CheckResult.Warning, "False positive (expected correct code, but found error)")
    }

    def correctExpr(code: String) {
        assert(checkExpr(code), "False positive (expected correct code, but found error)")
    }

    def errorExpr(code: String) {
        assert(checkExpr(code), "False negative (expected error, but found none)")
    }
}


trait TestHelperTSConditional extends TestHelper {

    private def checkExpr(code: String, enableAnalysis: Boolean, printAST: Boolean = false): Boolean =
        check("void main() { " + code + "}", enableAnalysis, printAST)

    private def check(code: String, enableAnalysis: Boolean, printAST: Boolean = false): Boolean = {
        println("checking " + code);
        if (printAST)
            println("AST: " + getAST(code));
        check(getAST(code), enableAnalysis);
    }

    protected def check(ast: TranslationUnit, enableAnalysis: Boolean): Boolean

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

}