package de.fosd.typechef.parser.test
import de.fosd.typechef.parser._
import de.fosd.typechef.featureexpr.FeatureExpr

abstract class AST
case class Alt(feature: FeatureExpr, thenBranch: AST, elseBranch: AST) extends Expr
object Alt {
    def join = (f: FeatureExpr, x: AST, y: AST) => if (x == y) x else Alt(f, x, y)
}

abstract class Expr extends AST
case class Plus(left: AST, right: AST) extends Expr
case class Minus(left: AST, right: AST) extends Expr
case class Mul(left: AST, right: AST) extends Expr
case class Lit(value: Int) extends Expr
case class ExprList(list: List[Expr]) extends AST
case class Char(value: Char) extends Expr

