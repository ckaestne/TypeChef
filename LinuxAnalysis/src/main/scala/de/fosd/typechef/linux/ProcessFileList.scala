package de.fosd.typechef.linux

import scala.util.parsing.combinator._
import scala.util.control.Breaks
import de.fosd.typechef.featureexpr._
import java.io._
import scala.collection.mutable.Map
import io.Source

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

    val openFeatures = Source.fromFile(LinuxSettings.openFeatureList).getLines.toList

    def toFeature(name: String, isModule: Boolean) =
    //ChK: deactivate modules for now. not interested and messes with the sat solver
        if (isModule) False
        else
        if (!(openFeatures contains ("CONFIG_" + name))) False //currently only interested in open features (and not in features that may not be in the feature model in the first place)
        else
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

        val lines = io.Source.fromFile(pcList).getLines
        val mybreaks = new Breaks
        val stderr = new PrintWriter(System.err, true)

        val fm = LinuxFeatureModel.featureModelExcludingDead

        assert(FeatureExpr.base.isSatisfiable(fm))

        val fileListWriter = new PrintWriter(new File("linux_files.lst"))
        val ignoredFileListWriter = new PrintWriter(new File("linux_file_ignored.lst"))

        val knownConditions: Map[FeatureExpr, Boolean] = Map()

        import mybreaks.{break, breakable}
        breakable {
            val FileNameFilter = """.*\.c""".r
            for (line <- lines; fields = line.split(':'); fullFilename = fields(0) if (
                    fullFilename match {
                        case FileNameFilter(_*) => true
                        case _ => false
                    }
                    )) {
                val filename = fullFilename.substring(fullFilename.lastIndexOf("/") + 1).dropRight(2)

                val pcExpr = parseAll(expr, fields(1))
                pcExpr match {
                    case Success(cond, _) =>
                        if (
                            knownConditions.getOrElseUpdate(cond, cond.isSatisfiable(fm))
                        ) {
                            //file should be parsed
                            println(fullFilename + " " + cond)

                            fileListWriter.write(fullFilename + "\n")
                            fileListWriter.flush

                            //create .cW and .piW file
                            val wrapperSrc = new PrintWriter(new File(LinuxSettings.pathToLinuxSource + "/" + fullFilename + "W"))
                            val wrapperPiSrc = new PrintWriter(new File(LinuxSettings.pathToLinuxSource + "/" + fullFilename.dropRight(2) + ".piW"))
                            wrapperSrc.print("#if ")
                            wrapperPiSrc.print("#if ")
                            cond.print(wrapperSrc)
                            cond.print(wrapperPiSrc)
                            wrapperSrc.println("\n#include \"" + filename + ".c" + "\"\n#endif")
                            wrapperPiSrc.println("\n#include \"" + filename + ".pi" + "\"\n#endif")
                            wrapperSrc.close
                            wrapperPiSrc.close

                            val fmFile = new PrintWriter(new File(LinuxSettings.pathToLinuxSource + "/" + fullFilename.dropRight(2) + ".pi.fm"))
                            cond.print(fmFile)
                            fmFile.close
                        }
                        else {
                            stderr.println(fullFilename + " has unsatisfiable condition " + cond + ", parsed from: " + fields(1))
                            ignoredFileListWriter.write(fullFilename + ": " + fields(1) + "\n")
                            ignoredFileListWriter.flush
                        }
                    case NoSuccess(msg, _) =>
                        stderr.println(fullFilename + " " + pcExpr)
                        break
                }
            }
        }
        fileListWriter.close
        ignoredFileListWriter.close

    }
}

// vim: set ts=4 sw=4 et:
