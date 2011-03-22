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
            out.write("file;outofmemory;finished;parsingtime;tokens;tokensConsumed;tokensBacktracked;tokensRepeated;noError;errorMsg\n")

        for (file <- files) {
            val fullFilePath = LinuxSettings.pathToLinuxSource + "/" + file + ".dbg"

            if (!new File(fullFilePath).exists)
                println("skipping " + file)
            else {

                println("processing " + file)
                out.write(file + ";")

                val lines = scala.io.Source.fromFile(fullFilePath).getLines.toList
                if (lines.exists(_ contains "java.lang.OutOfMemoryError")) {
                    println("out of memory")
                    out.write("out of memory;0;")
                } else
                    out.write(";1;")


                out.write(parseLine(lines, "Duration parsing", " ms\n").getOrElse("").dropRight(3) + ";")
                out.write(parseLine(lines, "Tokens:", "\n").getOrElse("") + ";")
                out.write(parseLine(lines, "Tokens Consumed:", "\n").getOrElse("") + ";")
                out.write(parseLine(lines, "Tokens Backtracked:", "\n").getOrElse("") + ";")
                out.write(parseLine(lines, "Tokens Repeated:", "\n").getOrElse("") + ";")

                if (lines.exists(_ contains "True\tsucceeded"))
                    out.write("1;")
                else
                    out.write("0;")

                if (lines.exists(_ contains "failed: "))
                    out.write(lines.filter(_ contains "failed: ").mkString)

                out.write("\n")
            }
        }

        out.close
    }
}

// vim: set sw=4:
