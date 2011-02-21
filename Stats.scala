package main

import io.Source
import java.io._

/*
* Created by IntelliJ IDEA.
* User: kaestner
* Date: 15.02.11
* Time: 09:17
*/

object Stats extends Application {
    
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


    val dir = new File(".")
    val out = new BufferedWriter(new FileWriter("stats.csv"))

    for (file <- dir.listFiles(new FilenameFilter() {
        def accept(dir: File, name: String): Boolean = name.endsWith(".pi.log")
    })) {
        println(file)
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
    }

    out.close
}
