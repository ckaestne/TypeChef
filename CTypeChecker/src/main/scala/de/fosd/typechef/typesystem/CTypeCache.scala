package de.fosd.typechef.typesystem

import java.util.IdentityHashMap

import de.fosd.typechef.parser.c.{FunctionDef, Expr, AST}
import de.fosd.typechef.conditional.Conditional
import de.fosd.typechef.featureexpr.FeatureExpr


trait CTypeCache extends CTypeSystemInterface {
    private val cacheExpr: IdentityHashMap[Expr, Conditional[CType]] = new IdentityHashMap()
    private val cacheFun: IdentityHashMap[FunctionDef, Conditional[CType]] = new IdentityHashMap()

    override protected def typedExpr(expr: Expr, ctype: Conditional[CType], featureExpr: FeatureExpr, env: Env) {
        cacheExpr.put(expr, ctype)
        super.typedExpr(expr, ctype, featureExpr, env)
    }
    override protected def typedFunction(fun: FunctionDef, ctype: Conditional[CType], featureExpr: FeatureExpr) {
        cacheFun.put(fun, ctype)
        super.typedFunction(fun, ctype, featureExpr)
    }

    def lookupExprType(expr: Expr): Conditional[CType] = cacheExpr.get(expr)
    def lookupFunType(fun: FunctionDef): Conditional[CType] = cacheFun.get(fun)

}


trait CEnvCache extends CTypeSystemInterface {
    private var cache: Map[AST, Env] = Map()

    override protected def addEnv(ast: AST, env: Env) {
        cache = cache + (ast -> env)
        super.addEnv(ast, env)
    }

    def lookupEnv(ast: AST) = cache(ast)
}

