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


    //implications
    def expr: Parser[FeatureExpr] =
        "oneOf" ~ "(" ~> rep1sep(expr, ",") <~ ")" ^^ {
            e => oneOf(e)
        } | aterm

    def aterm: Parser[FeatureExpr] =
        bterm ~ opt("=>" ~> expr) ^^ {
            case a ~ b => if (b.isDefined) a implies b.get else a
        }

    //mutually exclusion
    def bterm: Parser[FeatureExpr] =
        cterm ~ opt("<!>" ~> expr) ^^ {
            case a ~ b => if (b.isDefined) a mex b.get else a
        }

    //||
    def cterm: Parser[FeatureExpr] =
        term ~ rep("||" ~> expr) ^^ {
            case a ~ bs => bs.foldLeft(a)(_ or _)
        }

    def term: Parser[FeatureExpr] =
        bool ~ rep("&&" ~> expr) ^^ {
            case a ~ bs => bs.foldLeft(a)(_ and _)
        }

    def bool: Parser[FeatureExpr] =
        "!" ~> bool ^^ (_ not) |
            ("(" ~> expr <~ ")") |
            "InvalidExpression()" ^^ (_ => False) |
            (("definedEx" | "defined") ~ "(" ~> ID <~ ")") ^^ {
                toFeature(_)
            } |
            "1" ^^ {
                x => FeatureExpr.base
            } |
            "0" ^^ {
                x => FeatureExpr.dead
            }

    def ID = "[A-Za-z0-9_]*".r

    def parse(featureExpr: String): FeatureExpr = parseAll(expr, featureExpr) match {
        case Success(r, _) => r
        case NoSuccess(msg, _) => throw new Exception("error parsing " + featureExpr + " " + msg)
    }

    def parse(featureExpr: Reader): FeatureExpr = parseAll(expr, featureExpr) match {
        case Success(r, _) => r
        case NoSuccess(msg, _) => throw new Exception("error parsing " + msg)
    }


    def parseFile(cfilename: String): FeatureExpr = {
        val featureModelFile = new File(cfilename)
        if (featureModelFile.exists) {
            scala.io.Source.fromFile(featureModelFile).getLines().map(line =>
                if (line.trim.isEmpty) FeatureExpr.base
                else parse(line)
            ).fold(FeatureExpr.base)(_ and _)
        } else FeatureExpr.base
    }

    def parseFile(file: File): FeatureExpr =
        new FeatureExprParser().parse(new FileReader(file))


    private def oneOf(features: List[FeatureExpr]): FeatureExpr = {
        (for (f1 <- features; f2 <- features if (f1 != f2)) yield f1 mex f2).
            foldLeft(features.foldLeft(FeatureExpr.dead)(_ or _))(_ and _)

    }
}