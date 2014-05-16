package de.fosd.typechef.featureexpr

import util.parsing.combinator._
import java.io._
import util.matching.Regex


/**
 * simple parser to read feature expressions in the format produced by FeatureExpr.print(...)
 *
 * prefer parseFile() over inherited parse() methods for the following features:
 * * Multiple lines in the parsed file are interpreted as conjunction.
 * * verything after "//" is interpreted as comment
 *
 *
 * does not support integer values yet
 *
 * optional parameters:
 * * featureFactory allows to select which factory is used to create external feature names (use system default as default)
 * * featurenameParser allows to parameterize which character sequences are allowed as feature names (by default all alphanumerical sequences including the underscore)
 * * featurenamePrefix allows to add a prefix to every read feature name, such as "CONFIG_". no prefix by default
 */
class FeatureExprParser(
                           featureFactory: AbstractFeatureExprFactory = FeatureExprFactory.default,
                           featurenameParser: Regex = "[A-Za-z0-9_]*".r,
                           featurenamePrefix: Option[String] = None) extends RegexParsers {


    def toFeature(name: String) = featureFactory.createDefinedExternal(featurenamePrefix.map(_ + name).getOrElse(name))


    //implications
    def expr: Parser[FeatureExpr] =
        "oneOf" ~ "(" ~> rep1sep(expr, ",") <~ ")" ^^ {
            e => oneOf(e)
        } | "atLeastOne" ~ "(" ~> rep1sep(expr, ",") <~ ")" ^^ {
            e => atLeastOne(e)
        } | "atMostOne" ~ "(" ~> rep1sep(expr, ",") <~ ")" ^^ {
            e => atMostOne(e)
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
            ("1" | "true" | "True" | "TRUE") ^^ {
                x => featureFactory.True
            } |
            ("0" | "false" | "False" | "FALSE") ^^ {
                x => featureFactory.False
            } | ID ^^ {
            toFeature(_)
        }

    def ID: Regex = featurenameParser

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

    /**
     * parse files with multiple lines considered as one big conjunction and "//" interpreted as comments
     **/
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


    /**
     * parse files with multiple lines considered as one big conjunction and "//" interpreted as comments
     **/
    def parseFile(cfilename: String): FeatureExpr = parseFile(new BufferedReader(new FileReader(cfilename)))

    /**
     * parse files with multiple lines considered as one big conjunction and "//" interpreted as comments
     **/
    def parseFile(stream: InputStream): FeatureExpr = parseFile(new BufferedReader(new InputStreamReader(stream)))

    /**
     * parse files with multiple lines considered as one big conjunction and "//" interpreted as comments
     **/
    def parseFile(file: File): FeatureExpr = parseFile(new BufferedReader(new FileReader(file)))


    def oneOf(features: List[FeatureExpr]): FeatureExpr =
        atLeastOne(features) and atMostOne(features)

    def atLeastOne(featuresNames: List[FeatureExpr]): FeatureExpr =
        featuresNames.foldLeft(featureFactory.False)(_ or _)

    def atMostOne(features: List[FeatureExpr]): FeatureExpr =
        (for ((a, b) <- pairs(features)) yield a mex b).
            foldLeft(featureFactory.True)(_ and _)

    def pairs[A](elem: List[A]): Iterator[(A, A)] =
        for (a <- elem.tails.take(elem.size); b <- a.tail) yield (a.head, b)


}


/** wrapper class for easier access from Java */
class FeatureExprParserJava(featureFactory: AbstractFeatureExprFactory) extends FeatureExprParser(featureFactory)
