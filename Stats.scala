package main

import io.Source
import java.io._

/*
* Created by IntelliJ IDEA.
* User: kaestner
* Date: 15.02.11
* Time: 09:17
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
        val file = args(0)
        val outStats = if (args.length > 1) args(1) else "stats.csv"
        val out = new BufferedWriter(new FileWriter(outStats, true))

        //println(file)
        out.write(file + ";")

        val lines = scala.io.Source.fromFile(file).getLines.toList
        if (lines.exists(_ contains "java.lang.OutOfMemoryError")) {
            println("out of memory")
            out.write("out of memory;0;")
        } else
            out.write(";1;")


        out.write( parseLine(lines,"Duration parsing"," ms\n").getOrElse("") + ";" )
        out.write( parseLine(lines,"Tokens:","\n").getOrElse("") + ";" )
        out.write( parseLine(lines,"Tokens Consumed:","\n").getOrElse("") + ";" )
        out.write( parseLine(lines,"Tokens Backtracked:","\n").getOrElse("") + ";" )
        out.write( parseLine(lines,"Tokens Repeated:","\n").getOrElse("") + ";" )

        if (lines.exists(_ contains "True\tsucceeded")) 
            out.write("1;")
        else
            out.write("0;")

        out.write("\n")

        out.close
    }
}

// vim: set sw=4:
