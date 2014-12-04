package de.fosd.typechef.xtclexer

import de.fosd.typechef.conditional.{Choice, Conditional, ConditionalLib, One}
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory}

import scala.util.parsing.combinator.RegexParsers

/**
 * Xtc does not resolve expressions, but creates a new string. Here we resolve them to FeatureExpr
 *
 * e.g. (((defined X) ? 3 : 0) == 3) ==> X
 */
class XtcFExprAnalyzer {
    private val p = new XtcExprParser()

    def resolveFExpr(xtcCondition: String): FeatureExpr = {
        val parseResult = p.parse(p.phrase(p.expr), xtcCondition)
        parseResult match {
            case p.Success(r: Expr, _) => r.toFExpr
            case p.NoSuccess(msg, _) => throw new RuntimeException(s"parsing error: $msg when parsing feature name $xtcCondition produced by xtc")
        }

    }

    private class XtcExprParser extends RegexParsers {
        override type Elem = Char

        def expr: Parser[Expr] = feature | ite | comp | lit | featurename
        def feature: Parser[Expr] = "(" ~ "defined" ~> fid <~ ")" ^^ Feature
        def lit: Parser[Expr] = """[0-9]+""".r ^^ Lit
        def ite: Parser[Expr] = "(" ~> expr ~ "?" ~ expr ~ ":" ~ expr <~ ")" ^^ { case i ~ _ ~ t ~ _ ~ e => ITE(i, t, e)}
        def comp: Parser[Expr] = "(" ~> expr ~ ops ~ expr <~ ")" ^^ { case l ~ op ~ r => Comp(l, op, r)}
        def ops = "==" | "!=" | ">=" | "<=" | ">" | "<" | "<<" | ">>" | "+" | "-" | "*" | "/"
        def featurename: Parser[Expr] = fid ^^ { f=>
            System.err.println(s"found $f expected literal. assuming 0")
            Lit("0")
        }

        def fid = """[A-Za-z0-9_]+""".r

    }

    private trait Expr {
        def eval(): Conditional[Int]
        def toFExpr(): FeatureExpr = eval().when(_ != 0)
    }

    private case class Feature(n: String) extends Expr {
        def eval(): Conditional[Int] = Choice(FeatureExprFactory.createDefinedExternal(n), One(1), One(0))
    }
    private case class Lit(n: String) extends Expr {
        def eval(): Conditional[Int] = One(n.toInt)
    }

    private case class ITE(i: Expr, t: Expr, e: Expr) extends Expr {
        def eval(): Conditional[Int] = Choice(i.toFExpr(), t.eval(), e.eval())
    }

    private case class Comp(l: Expr, op: String, r: Expr) extends Expr {
        def eval(): Conditional[Int] = {
            ConditionalLib.mapCombination(l.eval(), r.eval(),
                (ll: Int, rr: Int) =>
                    op match {
                        case "==" => if (ll == rr) 1 else 0
                        case "!=" => if (ll != rr) 1 else 0
                        case "<=" => if (ll <= rr) 1 else 0
                        case ">=" => if (ll >= rr) 1 else 0
                        case "<" => if (ll < rr) 1 else 0
                        case ">" => if (ll > rr) 1 else 0
                        case "<<" => ll << rr
                        case ">>" => ll >> rr
                        case "+" => ll + rr
                        case "-" => ll - rr
                        case "/" => ll / rr
                        case "*" => ll * rr
                        case _ => throw new RuntimeException(s"unsupported operation $op in ($ll $op $rr)")
                    }
            )
        }
    }

}
