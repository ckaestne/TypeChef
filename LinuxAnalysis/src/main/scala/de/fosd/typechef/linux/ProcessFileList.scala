package de.fosd.typechef.linux

import scala.util.parsing.combinator._
import scala.util.control.Breaks
import java.io._
import de.fosd.typechef.featureexpr.{False, True, FeatureExpr}

/**
 * processes thorstens file list (passed as parameter) and creates corresponding .pi.pc files
 *
 * (presence conditions for files, in contrast to .pi.fm which represents a local feature model for dependencies)
 *
 * call with file created by thorsten as parameter
 */
object ProcessFileList extends RegexParsers {

    def toFeature(name: String, isModule: Boolean) =
    //ChK: deactivate modules for now. not interested and messes with the sat solver
        FeatureExpr.createDefinedExternal("CONFIG_" +
                (if (isModule)
                //name + "_2" // This is not SAT-solving, use the Linux names!
                    name + "_MODULE"
                else
                    name))

    def expr: Parser[FeatureExpr] =
        ("(" ~> (expr ~ "||" ~ expr) <~ ")") ^^ {case (a ~ _ ~ b) => a or b} |
                term
    def term: Parser[FeatureExpr] =
        "!" ~> commit(expr) ^^ (_ not) |
                ("(" ~> (expr ~ "&&" ~ expr) <~ ")") ^^ {case (a ~ _ ~ b) => a and b} |
                bool

    def bool: Parser[FeatureExpr] =
        "[TRUE]" ^^ (_ => True) |
                "InvalidExpression()" ^^ (_ => False) |
                ("(" ~> (ID ~ "!=" ~! featVal) <~ ")") ^^ {
                    case (id ~ _ ~ isModule) =>
                        if (!isModule)
                            toFeature(id, false).not
                        // X != y should be probably translated to something like
                        // CONFIG_X && CONFIG_X_2 || !CONFIG_X; to represent X ==
                        // "m", in the SAT-solver we enable CONFIG_X and CONFIG_X_2
                        // (by having CONFIG_X_2 imply CONFIG_X), while autoconf.h
                        // defines just CONFIG_X_MODULE.
                        else
                        // X != m should be probably translated to something like CONFIG_X && !CONFIG_X_2.
                            throw new RuntimeException("Can't handle this case!")
                } |
                ("(" ~> (ID ~ "==" ~! featVal) <~ ")") ^^ {case (id ~ _ ~ isModule) => toFeature(id, isModule)} |
                ID ^^ (id => toFeature(id, true) or toFeature(id, false))
    //Having this case here makes the grammar not LL(1) - and one expression
    //triggers exponential backtracking. Since source
    //phrases are always parenthesizes, I can include parentheses in each
    //production.
    //|
    //"(" ~> expr <~ ")"

    def ID = "[A-Za-z0-9_]*".r

    def featVal = ("\"" ~> "(y|m)".r <~ "\"") ^^ (_ == "m")

    def main(args: Array[String]) {
        if (args.isEmpty) {
            println("expected parameter: file with presence conditions\nsecond parameter for working directory (optional)")
            sys.exit()
        }

        val pcList = args(0)
        val workingDir = if (args.size >= 2) args(1) else ""

        val lines = io.Source.fromFile(pcList).getLines
        val mybreaks = new Breaks
        val stderr = new PrintWriter(System.err, true)


        import mybreaks.{break, breakable}
        breakable {
            val FileNameFilter = """.*\.c""".r
            for (line <- lines; fields = line.split(':'); fullFilename = fields(0) if (
                    fullFilename match {
                        case FileNameFilter(_*) => true
                        case _ => false
                    }
                    )) {
                val fullFilenameNoExt = workingDir + fullFilename.dropRight(2)
                val filename = fullFilename.substring(fullFilename.lastIndexOf("/") + 1).dropRight(2)

                val pcExpr = parseAll(expr, fields(1))
                pcExpr match {
                    case Success(cond, _) =>
                        //file should be parsed
                        println(fullFilename + " " + cond)

                        //create .cW and .piW file
                        val pcFile = new PrintWriter(new File(fullFilenameNoExt + ".pi.pc"))
                        cond.print(pcFile)
                        pcFile.close

                    case NoSuccess(msg, _) =>
                        stderr.println(fullFilename + " " + pcExpr)
                        break
                }
            }
        }
    }
}
