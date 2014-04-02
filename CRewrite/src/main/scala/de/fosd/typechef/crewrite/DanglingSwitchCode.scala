package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.crewrite.asthelper.ASTEnv

// implements a simple analysis that checks whether a switch statement has
// code that does not occur in the control flow of a case or default statement
// https://www.securecoding.cert.org/confluence/display/seccode/MSC35-C.+Do+not+include+any+executable+statements+inside+a+switch+statement+before+the+first+case+label
// MSC35-C
// superseeded by DCL41-C (https://www.securecoding.cert.org/confluence/display/seccode/DCL41-C.+Do+not+declare+variables+inside+a+switch+statement+before+the+first+case+label)
class DanglingSwitchCode(env: ASTEnv) extends IntraCFG {
    def danglingSwitchCode(s: SwitchStatement): List[Opt[AST]] = {
        // get all successor elements of all expressions in the switch statement
        // SwitchStatement(expr, ...) and filter out other case statements, as fall through (case after case)
        // is allowed in this analysis
        val exprsuccs = succ(s, env).flatMap({ x => succ(x.entry, env) })
        val wlist: List[Opt[AST]] = exprsuccs.filterNot({
            case Opt(_, _: CaseStatement) => true
            case Opt(_, _: DefaultStatement) => true

            // filter out all elements that are not part of the switch
            case Opt(_, x) => if (isPartOf(x, s)) false else true
        })

        wlist
    }
}
