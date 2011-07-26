package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.parser._
import de.fosd.typechef.typesystem._
import de.fosd.typechef.featureexpr.{FeatureExpr, DefinedExternal}
import org.kiama.rewriting.Rewriter._
import org.kiama._
import attribution.Attributable
import org.kiama.attribution.DynamicAttribution._

class CRefactorings extends ASTNavigation {

    val hvars: Attributable ==> List[Id] = attr {
        case Id(name) => List(Id(name))
        case AssignExpr(id, _, _) => id -> hvars
        case ExprStatement(s) => s -> hvars
        case PostfixExpr(id, FunctionCall(_)) => List()
    }

    private val rewriteStrategy = everywhere(rule {
        case Opt(f, stmt: Statement) if (!f.isTautology) =>
            val hookname = generateHookname
            //      val hookparameters = List(
            //        Opt(FeatureExpr.base, Id("k")),
            //        Opt(FeatureExpr.base, PointerCreationExpr(Id("l"))),
            //        Opt(FeatureExpr.base, Id("l"))
            //      )
            val hooks = stmt -> hvars
            println(hooks)
            val hookparameters = List[Opt[AST]]()
            Opt(FeatureExpr.base, ExprStatement(PostfixExpr(Id(hookname), FunctionCall(ExprList(hookparameters)))))

        case Choice(f, One(a: Statement), One(b: Statement)) =>
            One(IfStatement(featureToCExpr(f), a, List(), Some(b)))
    })

    private var currenthook = -1

    private def generateHookname = {
        currenthook += 1
        "hook" + currenthook.toString
    }

    def rewrite(ast: AST): AST = {
        rewriteStrategy(ast).get.asInstanceOf[AST]
    }

    def featureToCExpr(feature: FeatureExpr): Expr = feature match {
        case d: DefinedExternal => Id(d.feature)
    //TODO implement complex feature expressions
    }
}