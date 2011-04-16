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
object PCPPStats {
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
                (scala.io.Source.fromFile(args(1)).getLines.toList, if (args.length > 2) args(2) else "pcppstats.csv")
            else
                (List(args(0)), if (args.length > 1) args(1) else "pcppstats.csv")
        val append = args(0) != "-f"

        val out = new BufferedWriter(new FileWriter(outStats, append))
        if (!append)
            out.write("file;hasError;missingFile;fullLine\n")

        for (file <- files) {
            val fullFilePath = LinuxSettings.pathToLinuxSource + "/" + file + ".err"

            if (!new File(fullFilePath).exists)
                println("skipping " + file)
            else {

                println("processing " + file)
                out.write(file + ";")

                val source = scala.io.Source.fromFile(fullFilePath)
                val lines = source.getLines.toList.filter(_ startsWith "SEVERE:")
                source.close
                if (lines.isEmpty) {
                    out.write("0;;")
                } else {
                    out.write("1;")
                    if (lines(0) contains "File not found: ") {
                        val fileline = lines(0).substring(lines(0).indexOf("File not found: ") + 16)
                        val file = fileline.substring(0, fileline.indexOf(" in "))
                        out.write(file)
                    }
                    out.write(";" + lines.mkString("--"))
                }
                out.write("\n")
            }
        }

        out.close
    }
}

// vim: set sw=4:
