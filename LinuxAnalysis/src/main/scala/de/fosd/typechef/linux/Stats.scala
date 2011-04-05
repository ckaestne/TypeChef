package de.fosd.typechef.linux

import java.io._

/*
* Created by IntelliJ IDEA.
* User: kaestner
* Date: 15.02.11
* Time: 09:17
*/

/**
 * usage:
 *
 * stats file outputFile
 * or
 * stats -f filelist outputFile
 */
object Stats {

    val HEADER = "file;outofmemory;parsingtime;" +
            "tokens;tokensConsumed;tokensBacktracked;tokensRepeated;noError;" +
            "macros;altmacros;altmacros3;altmacros4;altmacros5;condmacros;" +
            "headernesting;includecount;distinctheaders;" +
            "condtokens;distinctfeatures;featureexpr;choicenodes;" +
            "timereal;timeuser;timesys;" +
            "errorMsg\n"

    def parseLine(lines: List[String], key: String, trailing: String): Option[String] = {
        val l: List[String] = lines.filter(_ contains key)
        if (!l.isEmpty) {
            var dur = l.head.substring(l.head.indexOf(":") + 2)
            if (dur.endsWith(trailing))
                dur = dur.substring(0, dur.length - trailing.length)
            Some(dur)
        }
        else None
    }

    def main(args: Array[String]) {
        val (files, outStats) =
            if (args(0) == "-f")
                (scala.io.Source.fromFile(args(1)).getLines.toList, if (args.length > 2) args(2) else "stats.csv")
            else
                (List(args(0)), if (args.length > 1) args(1) else "stats.csv")
        val append = args(0) != "-f"

        val out = new BufferedWriter(new FileWriter(outStats, append))
        if (!append)
            out.write(HEADER)

        val count = files.size
        var nr = 0;
        for (file <- files) {
            nr += 1
            val fullFilePath = LinuxSettings.pathToLinuxSource + "/" + file

            if (!new File(fullFilePath + ".dbg").exists)
                println("skipping " + file)
            else {

                println("processing " + file + "(" + nr + "/" + count + ")")
                processFile(file, fullFilePath, out)

            }
        }

        out.close
    }

    //    def main(args: Array[String]) =
    //        processFile("tmp", "S:\\ARCHIVE\\kos\\share\\TypeChef\\linux-2.6.33.3\\init\\calibrate", new BufferedWriter(new OutputStreamWriter( System.out)))

    def processFile(file: String, fullFilePath: String, out: BufferedWriter) {
        //file
        out.write(file + ";")

        val lines = getLines(fullFilePath + ".dbg")

        //out of memory
        if (lines.exists(_ contains "java.lang.OutOfMemoryError")) {
            println("out of memory")
            out.write("1;")
        } else
            out.write("0;")


        //            "tokens;tokensConsumed;tokensBacktracked;tokensRepeated;;" +
        out.write(parseLine(lines, "Duration parsing", " ms\n").getOrElse("").dropRight(3) + ";")
        out.write(parseLine(lines, "Tokens:", "\n").getOrElse("") + ";")
        out.write(parseLine(lines, "Tokens Consumed:", "\n").getOrElse("") + ";")
        out.write(parseLine(lines, "Tokens Backtracked:", "\n").getOrElse("") + ";")
        out.write(parseLine(lines, "Tokens Repeated:", "\n").getOrElse("") + ";")

        //noerror
        if (lines.exists(_ contains "True\tsucceeded"))
            out.write("1;")
        else
            out.write("0;")

        //macros, altmacros, altmacros3, altmacros4, altmacros5, condmacros
        val macroStats = getLines(fullFilePath + ".pi.macroDbg").last
        out.write(macroStats + ";")

        //        "headernesting;includecount;distinctheaders;" +
        val includeStats = getLines(fullFilePath + ".pi.dbgSrc").last
        out.write(includeStats + ";")

        //        "condtokens;distinctfeatures;featureexpr;choicenodes;" +
        out.write(parseLine(lines, "Conditional Tokens:", "\n").getOrElse("-1") + ";")
        out.write(parseLine(lines, "Distinct Features:", "\n").getOrElse("-1") + ";")
        out.write(parseLine(lines, "Distinct Feature Expressions:", "\n").getOrElse("-1") + ";")
        out.write(parseLine(lines, "Choice Nodes:", "\n").getOrElse("-1") + ";")

        //            "timereal;timeuser;timesys;" +
        val timeStats = getLines(fullFilePath + ".time")
        out.write(readTime(timeStats(1)) + ";")
        out.write(readTime(timeStats(2)) + ";")
        out.write(readTime(timeStats(3)) + ";")

        //errormsg
        if (lines.exists(_ contains "failed: "))
            out.write(lines.filter(_ contains "failed: ").mkString.replace('\n', ';'))

        out.write("\n")
        out.flush
    }

    def getLines(file: String) = {
        val source = scala.io.Source.fromFile(file)
        val lines = source.getLines.toList
        source.close
        lines
    }

    val TimePattern = """.*\s(\d+)m(\d+)\.(\d+)s""".r
    def readTime(line: String): Int = {
        val TimePattern(min, sec, msec) = line
        msec.toInt + sec.toInt * 1000 + min.toInt * 60 * 1000
    }

}

// vim: set sw=4:
