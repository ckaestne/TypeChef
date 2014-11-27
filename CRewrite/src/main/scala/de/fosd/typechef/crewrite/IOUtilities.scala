package de.fosd.typechef.crewrite

import java.io.PrintWriter
import scala.language.reflectiveCalls

trait IOUtilities {
    // http://stackoverflow.com/questions/4604237/how-to-write-to-a-file-in-scala

    import java.io.FileWriter

    def using[A <: {def close()}, B](param: A)(f: A => B): B =
        try {
            f(param)
        } finally {
            param.close()
        }

    def writeToFile(fileName: String, data: String) {
        using(new FileWriter(fileName)) {
            fileWriter => fileWriter.write(data)
        }
    }

    def appendToFile(fileName: String, textData: String) {
        using(new FileWriter(fileName, true)) {
            fileWriter => using(new PrintWriter(fileWriter)) {
                printWriter => printWriter.println(textData)
            }
        }
    }
}