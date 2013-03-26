package de.fosd.typechef.featureexpr

import util.parsing.combinator._
import java.io._


/**
 * simple parser to read feature expressions in the format produced by FeatureExpr.print(...)
 *
 * does not support integer values yet
 */
class FeatureExprParser(featureFactory: AbstractFeatureExprFactory = FeatureExprFactory.default) extends RegexParsers {

    def toFeature(name: String) = featureFactory.createDefinedExternal(name)


    //implications
    def expr: Parser[FeatureExpr] =
        "oneOf" ~ "(" ~> rep1sep(expr, ",") <~ ")" ^^ {
            e => oneOf(e)
        } | "atLeastOne" ~ "(" ~> rep1sep(expr, ",") <~ ")" ^^ {
            e => atLeastOne(e)
        } | aterm

    def aterm: Parser[FeatureExpr] =
        bterm ~ opt(("=>" | "implies") ~> aterm) ^^ {
            case a ~ b => if (b.isDefined) a implies b.get else a
        }

    def bterm: Parser[FeatureExpr] =
        cterm ~ opt(("<=>" | "equiv") ~> bterm) ^^ {
            case a ~ b => if (b.isDefined) a equiv b.get else a
        }

    //mutually exclusion
    def cterm: Parser[FeatureExpr] =
        dterm ~ opt(("<!>" | "mex") ~> cterm) ^^ {
            case a ~ b => if (b.isDefined) a mex b.get else a
        }

    //||
    def dterm: Parser[FeatureExpr] =
        term ~ rep(("||" | "|" | "or") ~> dterm) ^^ {
            case a ~ bs => bs.foldLeft(a)(_ or _)
        }

    def term: Parser[FeatureExpr] =
        bool ~ rep(("&&" | "&" | "and") ~> term) ^^ {
            case a ~ bs => bs.foldLeft(a)(_ and _)
        }

    def bool: Parser[FeatureExpr] =
        "!" ~> bool ^^ (_ not) |
            ("(" ~> expr <~ ")") |
            "InvalidExpression()" ^^ (_ => featureFactory.False) |
            (("definedEx" | "defined" | "def") ~ "(" ~> ID <~ ")") ^^ {
                toFeature(_)
            } |
            "1" ^^ {
                x => featureFactory.True
            } |
            "0" ^^ {
                x => featureFactory.False
            } | ID ^^ {
            toFeature(_)
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

    private def trimComment(l: String): String = {
        if (l.indexOf("//") >= 0)
            l.take(l.indexOf("//"))
        else l
    }

    //* parse files with multiple lines considered as one big conjunction */
    def parseFile(reader: BufferedReader): FeatureExpr = {
        var line = reader.readLine()
        var result = featureFactory.True
        while (line != null) {
            line = trimComment(line)
            val lineExpr = if (line.trim.isEmpty) featureFactory.True else parse(line)
            result = result and lineExpr
            line = reader.readLine()
        }
        result
    }


    def parseFile(cfilename: String): FeatureExpr = parseFile(new BufferedReader(new FileReader(cfilename)))

    def parseFile(stream: InputStream): FeatureExpr = parseFile(new BufferedReader(new InputStreamReader(stream)))

    def parseFile(file: File): FeatureExpr = parseFile(new BufferedReader(new FileReader(file)))


    private def oneOf(features: List[FeatureExpr]): FeatureExpr = {
        (for (f1 <- features; f2 <- features if (f1 != f2)) yield f1 mex f2).
            foldLeft(features.foldLeft(featureFactory.False)(_ or _))(_ and _)

    }

    def atLeastOne(featuresNames: List[FeatureExpr]): FeatureExpr =
        featuresNames.foldLeft(featureFactory.False)(_ or _)

}