package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c.{FunctionDef, Expr, AST}
import de.fosd.typechef.conditional.Conditional
import de.fosd.typechef.featureexpr.FeatureExpr


trait CTypeCache extends CTypeSystemInterface {
    private var cacheExpr: Map[Expr, Conditional[CType]] = Map()
    private var cacheFun: Map[FunctionDef, Conditional[CType]] = Map()

    override protected def typedExpr(expr: Expr, ctype: Conditional[CType], featureExpr: FeatureExpr, env: Env) {
        cacheExpr = cacheExpr + (expr -> ctype)
        super.typedExpr(expr, ctype, featureExpr, env)
    }
    override protected def typedFunction(fun: FunctionDef, ctype: Conditional[CType], featureExpr: FeatureExpr) {
        cacheFun = cacheFun + (fun -> ctype)
        super.typedFunction(fun, ctype, featureExpr)
    }

    def lookupExprType(expr: Expr): Conditional[CType] = cacheExpr(expr)
    def lookupFunType(fun: FunctionDef): Conditional[CType] = cacheFun(fun)

}


trait CEnvCache extends CTypeSystemInterface {
    private var cache: Map[AST, Env] = Map()

    override protected def addEnv(ast: AST, env: Env) {
        cache = cache + (ast -> env)
        super.addEnv(ast, env)
    }

    def lookupEnv(ast: AST) = cache(ast)
}

