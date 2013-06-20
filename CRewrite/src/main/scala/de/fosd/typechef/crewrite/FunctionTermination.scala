package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem._
import de.fosd.typechef.featureexpr.FeatureModel
import de.fosd.typechef.conditional.{ConditionalLib, Opt}

// implements a simple analysis that checks whether the control-flow statements
// of a function with a non-void return type, always end in a return statement
// https://www.securecoding.cert.org/confluence/display/seccode/MSC17-C.+Finish+every+set+of+statements+associated+with+a+case+label+with+a+break+statement
// MSC17-C
class FunctionTermination(env: ASTEnv, fm: FeatureModel, ts: CTypeSystemFrontend with CTypeCache) extends IntraCFG {
    def isTerminating(f: FunctionDef): Boolean = {
        // get all predecessor elements of the function and look for non-return statements
        var wlist: List[Opt[AST]] = pred(f, fm, env)

        val ftypes = ts.lookupFunType(f)

        true
    }
}
