package de.fosd.typechef.linux

import scala.util.parsing.combinator._
import scala.util.control.Breaks
import de.fosd.typechef.featureexpr._
import java.io._

/**
 * processes thorstens file list (passed as parameter)
 *
 * produces
 *  a) a list of all relevant files linux_file.lst
 *  b) a list of all ignored files linux_file_ignored.lst (excluded by feature model and partial configuration)
 *  c) a .cW file for every .c file, wrapping the .c file with the corresponding condition
 *  d) a .piW file for every .pi file, wrapping the .pi file with the corresponding condition
 */
object ProcessFileList extends RegexParsers {
    def toFeature(name: String, isModule: Boolean) =
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
        "!" ~> commit(bool) ^^ (_ not) |
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
        val pcList = args(0)

        val outDir = "out" //XXX
        val reversePath = ".." //XXX, reverse of outDir

        val lines = io.Source.fromFile(pcList).getLines
        val mybreaks = new Breaks
        val stderr = new PrintWriter(System.err, true)

        val fm = LinuxFeatureModel.featureModel

        import mybreaks.{break, breakable}
        breakable {
            val FileNameFilter = """.*\.c""".r
            for (line <- lines; fields = line.split(':'); fileName = fields(0) if (
                    fileName match {
                        case FileNameFilter(_*) => true
                        case _ => false
                    }
                    )) {
                val pcExpr = parseAll(expr, fields(1))
                pcExpr match {
                    case Success(cond, _) =>
                        if (cond.isSatisfiable(fm))
                            println(fileName + " " + cond)
                        else
                            stderr.println(fileName + " has condition False, parsed from: " + fields(1))
                    //                        val wrapperSrcPath = new File(outDir + File.separator + fileName)
                    //                        wrapperSrcPath.getParentFile().mkdirs()
                    //                        val wrapperSrc = new PrintWriter(wrapperSrcPath)
                    //                        wrapperSrc.print("#if "); cond.print(wrapperSrc)
                    //                        wrapperSrc.println("\n#include \"" + reversePath + "/" + fileName + "\"\n#endif")
                    //                        wrapperSrc.close
                    //                    case Success(cond, _) =>
                    //                        stderr.println(fileName + " has condition False, parsed from: " + fields(1))
                    case NoSuccess(msg, _) =>
                        stderr.println(fileName + " " + pcExpr)
                        break
                }
            }
        }
    }
}

// vim: set ts=4 sw=4 et:
