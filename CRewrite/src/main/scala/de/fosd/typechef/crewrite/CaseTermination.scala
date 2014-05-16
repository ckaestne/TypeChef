package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.crewrite.asthelper.ASTEnv

// implements a simple analysis that checks whether a case statement associated with a statement
// terminates under all conditions with a break statement
// https://www.securecoding.cert.org/confluence/display/seccode/MSC17-C.+Finish+every+set+of+statements+associated+with+a+case+label+with+a+break+statement
// MSC17-C
class CaseTermination(env: ASTEnv) extends IntraCFG {
    def isTerminating(c: CaseStatement): Boolean = {
        // get all successor elements of the case statement and filter other
        // case statements because case after case (fall through) is allowed
        var wList: List[Opt[AST]] = succ(c, env).filterNot({
            case Opt(_, _: CaseStatement) => true
            case _ => false
        })

        // visited list; to determine cyclic successors
        var vList: List[Opt[AST]] = List()

        // determine the switch to determine whether successor elements
        // still belong to the switch
        val switch = findPriorASTElem[SwitchStatement](c, env)

        // determine starting from the case statement that all successor elements will finally
        // come through a break statement
        while (wList.nonEmpty) {
            val curElem = wList.head
            wList = wList.tail

            if (! vList.exists(_.eq(curElem))) {
                vList ::= curElem

                curElem match {
                case Opt(_, _: BreakStatement) =>
                case Opt(_, _: CaseStatement) => return false
                case Opt(_, _: DefaultStatement) => return false
                    case Opt(_, s) => if (!isPartOf(s, switch))
                                          return false
                                      else
                                          wList ++= succ(s, env)
                }
            }
        }

        true
    }
}
