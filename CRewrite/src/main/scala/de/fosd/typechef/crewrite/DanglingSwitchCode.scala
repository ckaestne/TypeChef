package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.crewrite.asthelper.{ConditionalNavigation, ASTEnv}
import de.fosd.typechef.featureexpr.FeatureModel

// implements a simple analysis that checks whether a switch statement has
// code that does not occur in the control flow of a case or default statement
// https://www.securecoding.cert.org/confluence/display/seccode/MSC35-C.+Do+not+include+any+executable+statements+inside+a+switch+statement+before+the+first+case+label
// MSC35-C
// superseeded by DCL41-C (https://www.securecoding.cert.org/confluence/display/seccode/DCL41-C.+Do+not+declare+variables+inside+a+switch+statement+before+the+first+case+label)
class DanglingSwitchCode(env: ASTEnv, fm: FeatureModel) extends CFGHelper with ConditionalNavigation {
    def danglingSwitchCode(s: SwitchStatement): List[Opt[AST]] = {
        // determine the succ CFG and the pred CFG for the switch statement
        val switchSuccs = getAllSucc(s, env) flatMap {_._2} filter {x => isPartOf(x.entry, s)} map {_.entry}
        val switchPreds = getAllPred(s, env) flatMap {_._2} filter {x => isPartOf(x.entry, s)} map {_.entry}

        // determine the diff between both CFG results
        val diff = ((switchSuccs diff switchPreds) ++ (switchPreds diff switchSuccs)).distinct

        println(diff)

        val res = diff flatMap {
            case x@DeclarationStatement(d) if !containsInitializationCode(d) =>
                Some(parentOpt(x, env).asInstanceOf[Opt[AST]])
            case x: AST => Some(parentOpt(x, env).asInstanceOf[Opt[AST]])
            case _      => None
        }

        res
    }

    // check whether initdeclarator has an initializer and whether that initialization code is satisfiable
    private def containsInitializationCode(d: Declaration): Boolean = {
        d.init.exists { x => x.entry.hasInitializer && env.featureExpr(x.entry).isSatisfiable(fm) }
    }
}
