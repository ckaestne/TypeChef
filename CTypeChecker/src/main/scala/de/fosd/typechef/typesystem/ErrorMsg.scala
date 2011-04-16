package de.fosd.typechef.typesystem

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
                "undefined function: '" + name + "'\n" +
                        "  no declaration found"
            case _ =>
                "undefined function: '" + name + "'\n" +
                        "  callsite features: " + callerFeatures.mkString(", ") + "\n" +
                        "  " + definitionFeatures.size + " potential declarations found:\n" +
                        definitionFeatures.mkString("    declaration features: ", "\n    declaration features: ", "")
        }
        new ErrorMsg(msg, callerSources, targets)
    }
    override def toString = toError.toString
}

case class RedefErrorMsg(name: String, newDef: Entry, existingDef: Entry) {
    override def toString =
        "duplicate definition: '" + name + "'\n" +
                "  previous features: " + existingDef.feature + "\n" +
                "  new features:      " + newDef.feature
}

object ErrorMsgs {
    def errNoDecl(name: String, caller: AST, callerFeature: FeatureExpr) =
        ErrorMsgs(name, List((callerFeature, caller)), Nil)
}
