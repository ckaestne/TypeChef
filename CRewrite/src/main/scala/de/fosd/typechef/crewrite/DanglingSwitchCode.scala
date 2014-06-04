package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional.{ConditionalLib, Opt}
import de.fosd.typechef.crewrite.asthelper.{ConditionalNavigation, ASTEnv}
import de.fosd.typechef.featureexpr.FeatureModel

// implements a simple analysis that checks whether a switch statement has
// code that does not occur in the control flow of a case or default statement
// https://www.securecoding.cert.org/confluence/display/seccode/MSC35-C.+Do+not+include+any+executable+statements+inside+a+switch+statement+before+the+first+case+label
// MSC35-C
// superseeded by DCL41-C (https://www.securecoding.cert.org/confluence/display/seccode/DCL41-C.+Do+not+declare+variables+inside+a+switch+statement+before+the+first+case+label)
class DanglingSwitchCode(env: ASTEnv, fm: FeatureModel) extends CFGHelper with ConditionalNavigation {
    def danglingSwitchCode(s: SwitchStatement): List[Opt[AST]] = {
        // To determine dangling switch code, we need to traverse the CFG
        // and look for CFG statements that are not guarded (in one or more
        // configurations) by a case label.

        // Starting with the control flow of the switch body,
        // we continuously determine successor elements.
        // If we hit a case or default label, we continue
        // If we hit a declaration statement, we check for initializer.
        //   If there is one, issue an error. Otherwise determine successor elements and proceed.
        // If we hit any other AST element, we return this element as identified dangling
        // switch code.
        var workingList = ConditionalLib.leaves(s.s)
            .flatMap { succ(_, env) }
            .filter { x => isPartOf(x.entry, s) && x.feature.isSatisfiable(fm) }

        var res: List[Opt[AST]] = List()
        while (workingList.nonEmpty) {
            val oItem = workingList.head
            workingList = workingList.tail

            oItem.entry match {
                case _: CaseStatement =>
                case _: DefaultStatement =>
                case ds@DeclarationStatement(d) =>
                    if (containsInitializationCode(d))
                        res ::= oItem
                    else
                        workingList ++= succ(ds, env)
                            .filter { case Opt(feature, entry) => isPartOf(entry, s) && feature.isSatisfiable(fm) }
                case _: AST => res ::= oItem
            }
        }

        res
    }

    // check whether initdeclarator has an initializer and whether that initialization code is satisfiable
    private def containsInitializationCode(d: Declaration): Boolean = {
        d.init.exists { x => x.entry.hasInitializer && env.featureExpr(x.entry).isSatisfiable(fm) }
    }
}
