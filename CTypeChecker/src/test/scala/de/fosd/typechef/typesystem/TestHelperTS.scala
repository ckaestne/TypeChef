package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c.{TranslationUnit, TestHelper}


trait TestHelperTS extends TestHelper {
    protected def check(code: String, printAST: Boolean = false): Boolean = {
        println("checking " + code);
        if (printAST) println("AST: " + getAST(code));
        check(getAST(code));
    }
    protected def check(ast: TranslationUnit): Boolean = {
        assert(ast != null, "void ast");
        new CTypeSystemFrontend(ast).checkAST()
    }
    private def checkExpr(code: String, printAST: Boolean = false): Boolean =
        check("void main() { " + code + "}", printAST)


    def correct(code: String) {
        assert(check(code), "Expected correct code, but found error")
    }
    def error(code: String) {
        assert(!check(code), "Expected error, but found none")
    }
    def correctExpr(code: String) {
        assert(checkExpr(code), "Expected correct code, but found error")
    }
    def errorExpr(code: String) {
        assert(!checkExpr(code), "Expected error, but found none")
    }
}
