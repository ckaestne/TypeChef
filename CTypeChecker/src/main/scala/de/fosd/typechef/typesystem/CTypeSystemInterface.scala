package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c._
import de.fosd.typechef.error._
import de.fosd.typechef.conditional.Conditional
import de.fosd.typechef.featureexpr.FeatureExpr

/**
 * hooks that other analysis can override to collect/cache their own information
 */

trait CTypeSystemInterface extends CEnv {

    /**
     * invoked after typing an expression
     *
     * refined by CTypeCache if desired
     */
    protected def typedExpr(expr: Expr, ctype: Conditional[CType], featureExpr: FeatureExpr, env: Env) {}

    /**
     * invoked after typing a function definition (top level declaration, not nested function)
     */
    protected def typedFunction(fun: FunctionDef, ctype: Conditional[CType], featureExpr: FeatureExpr) {}

    /**
     * invoked before checking an external declaration (for example used for progress reports)
     */
    protected def checkingExternal(externalDef: ExternalDef) {}

    /**
     * invoked before every external decl, statement and expression with the environment of
     * that node
     *
     * for example to debug the environment
     *
     * mixed in from CEnvCache
     */
    protected def addEnv(ast: AST, env: Env) {}


    /**
     * error reporting for type errors
     */
    protected def issueTypeError(severity: Severity.Severity, condition: FeatureExpr, msg: String, where: AST, severityExtra: String = "") {}


    protected def assertTypeSystemConstraint(condition: Boolean, featureExpr: FeatureExpr, msg: String, where: AST): Boolean = {
        if (!condition)
            issueTypeError(Severity.Crash, featureExpr, msg, where)
        condition
    }

    protected final def reportTypeError(featureExpr: FeatureExpr, txt: String, where: AST, severity: Severity.Severity = Severity.OtherError, severityExtra: String = ""): CUnknown = {
        issueTypeError(severity, featureExpr, txt, where, severityExtra)
        CUnknown(txt)
    }

}

