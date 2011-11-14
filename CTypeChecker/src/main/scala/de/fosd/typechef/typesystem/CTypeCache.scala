package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c.Expr
import de.fosd.typechef.conditional.Conditional
import de.fosd.typechef.parser.c.AST


trait CTypeCache extends CExprTyping {
    private var cache: Map[Expr, Conditional[CType]] = Map()

    override def addExprType(expr: Expr, ctype: Conditional[CType]) {
        cache = cache + (expr -> ctype)
    }

    def lookupExprType(expr: Expr): Conditional[CType] = cache(expr)
}


trait CEnvCache extends CTypeSystem {
    private var cache: Map[AST, Env] = Map()

    override def addEnv(ast: AST, env: Env) {
        cache = cache + (ast -> env)
    }

    def lookupEnv(ast: AST) = cache(ast)
}

