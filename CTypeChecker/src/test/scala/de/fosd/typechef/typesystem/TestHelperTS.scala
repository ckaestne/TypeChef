package de.fosd.typechef.typesystem

import de.fosd.typechef.error.TypeChefError
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import de.fosd.typechef.parser.c.{TranslationUnit, TestHelper}


trait TestHelperTS extends TestHelper {


    protected def check(code: String, printAST: Boolean = false): Boolean = _check(code, printAST).isEmpty

    protected def _check(code: String, printAST: Boolean = false): List[TypeChefError] = {
        //        println("checking " + code);
        if (printAST)
            println("AST: " + getAST(code))
        _check(getAST(code))
    }

    protected def check(ast: TranslationUnit): Boolean = _check(ast).isEmpty

    protected def _check(ast: TranslationUnit): List[TypeChefError] = {
        assert(ast != null, "void ast");
        new CTypeSystemFrontend(ast).makeSilent().checkAST()
    }

    private def checkExpr(code: String, printAST: Boolean = false): Boolean = _checkExpr(code, printAST).isEmpty

    private def _checkExpr(code: String, printAST: Boolean = false): List[TypeChefError] =
        _check("void main() { " + code + "}", printAST)


    def correct(code: String) {
        val r = _check(code)
        assert(r.filterNot(_.isWarning).isEmpty, "False positive (expected correct code, but found error): \n" + r.mkString("\n"))
        assert(r.filter(_.isWarning).isEmpty, "False positive warning (expected correct code, but found warning): \n" + r.mkString("\n"))
    }

    def error(code: String) {
        val r = _check(code)
        assert(r.filter(!_.isWarning).nonEmpty, "False negative (expected error, but found none)")
    }

    def errorIf(code: String, expectedErrorCondition: FeatureExpr) {
        val r = _check(code)
        val foundErrorCondition = r.filter(!_.isWarning).foldRight(FeatureExprFactory.False)(_.condition or _)
        assert(foundErrorCondition equivalentTo expectedErrorCondition, s"Expected error under condition $expectedErrorCondition, but found error under condition $foundErrorCondition")
    }

    def warning(code: String) {
        val r = _check(code)
        assert(r.filter(!_.isWarning).isEmpty, "False positive (expected warning, but found error): \n" + r.mkString("\n"))
        assert(r.nonEmpty, "Missing warning (expected warning, but found neither error nor warning)")
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