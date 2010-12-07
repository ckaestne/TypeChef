package de.fosd.typechef.typesystem
import de.fosd.typechef.parser.c.AST
import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr._

class ErrorMsg(msg: String, callers: List[AST], target: List[Entry]) {
    override def toString = msg + " (" + callers.mkString(", ") + ") => " + target.mkString(" || ")
}

case class ErrorMsgs(name: String, callers: List[(FeatureExpr, AST)], targets: List[Entry]) {
    def withNewCaller(newCaller: AST, newCallerFeature: FeatureExpr) = ErrorMsgs(name, (newCallerFeature, newCaller) :: callers, targets)
    def definitionFeatures = targets.map(_.feature)
    def callerFeatures = callers.map(_._1)
    def callerSources = callers.map(_._2)
    def toError = {
        val msg = definitionFeatures match {
            case Nil =>
                assert(targets.isEmpty)
                "declaration of function " + name + " not found"
            case _ =>
                "declaration of function " + name + " not always reachable" +
                    " (" + targets.size + " potential targets): features of callsites (" +
                    callerFeatures.mkString(", ") + ") do not imply " +
                    definitionFeatures.mkString(" || ")
        }
        new ErrorMsg(msg, callerSources, targets)
    }
    override def toString = toError.toString
}
case class RedefErrorMsg(name: String, newDef: Entry, existingDef: Entry) {
    override def toString = "function " + name + " redefined with feature " + newDef.feature + "; previous: " + existingDef
}

object ErrorMsgs {
    def errNoDecl(name: String, caller: AST, callerFeature: FeatureExpr) =
        ErrorMsgs(name, List((callerFeature, caller)), Nil)
}
