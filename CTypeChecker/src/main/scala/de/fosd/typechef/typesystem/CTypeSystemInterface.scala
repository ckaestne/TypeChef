package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c._
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
    protected def typedExpr(expr: Expr, ctype: Conditional[CType], featureExpr: FeatureExpr) {}

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
    protected def issueError(condition: FeatureExpr, msg: String, where: AST, whereElse: AST = null) {}
    protected def issueTypeError(condition: FeatureExpr, msg: String, where: AST) {}


}