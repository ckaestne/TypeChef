package de.fosd.typechef.featureexpr

import util.parsing.combinator._
import java.io._


/**
 * simple parser to read feature expressions in the format produced by FeatureExpr.print(...)
 *
 * does not support integer values yet
 */

class FeatureExprParser extends RegexParsers {

    def toFeature(name: String) = FeatureExpr.createDefinedExternal(name)

    def expr: Parser[FeatureExpr] =
        term ~ rep("||" ~> expr) ^^ {case a ~ bs => bs.foldLeft(a)(_ or _)}

    def term: Parser[FeatureExpr] =
        bool ~ rep("&&" ~> expr) ^^ {case a ~ bs => bs.foldLeft(a)(_ and _)}

    def bool: Parser[FeatureExpr] =
        "!" ~> bool ^^ (_ not) |
                ("(" ~> expr <~ ")") |
                "InvalidExpression()" ^^ (_ => False) |
                (("definedEx" | "defined") ~ "(" ~> ID <~ ")") ^^ {toFeature(_)} |
                "1" ^^ {x => FeatureExpr.base} |
                "0" ^^ {x => FeatureExpr.dead}

    def ID = "[A-Za-z0-9_]*".r

    def parse(featureExpr: String): FeatureExpr = parseAll(expr, featureExpr) match {
        case Success(r, _) => r
        case NoSuccess(msg, _) => throw new Exception("error parsing " + featureExpr + " " + msg)
    }
    def parse(featureExpr: Reader): FeatureExpr = parseAll(expr, featureExpr) match {
        case Success(r, _) => r
        case NoSuccess(msg, _) => throw new Exception("error parsing " + msg)
    }

}