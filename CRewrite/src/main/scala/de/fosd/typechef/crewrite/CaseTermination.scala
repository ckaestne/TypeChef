package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr.FeatureModel
import de.fosd.typechef.conditional.Opt

// implements a simple analysis that checks whether a case statement associated with a statement
// terminates under all conditions with a break statement
// https://www.securecoding.cert.org/confluence/display/seccode/MSC17-C.+Finish+every+set+of+statements+associated+with+a+case+label+with+a+break+statement
// MSC17-C
class CaseTermination(env: ASTEnv, fm: FeatureModel) extends IntraCFG {
    def isTerminating(c: CaseStatement): Boolean = {
        // get all successor elements of the case statement
        // and filter out other case statements, as fall through (case after case)
        // is allowed in this analysis
        var wlist: List[Opt[AST]] = succ(c, fm, env).filterNot({
            case Opt(_, _: CaseStatement) => true
            case _ => false
        })

        // determine switch to make sure we do not leave the successor element
        val switch = findPriorASTElem[SwitchStatement](c, env)

        // determine starting from the case statement that all successor elements will finally
        // come through a break statement
        while (wlist.size > 0) {
            val curelem = wlist.head
            wlist = wlist.tail

            curelem match {
                case Opt(_, _: BreakStatement) =>
                case Opt(_, _: CaseStatement) => return false
                case Opt(_, _: DefaultStatement) => return false
                case Opt(_, s) => wlist ++= succ(s, fm, env).filterNot({
                    case Opt(_, x) => !isPartOf(x, switch)
                })
            }
        }

        true
    }
}
