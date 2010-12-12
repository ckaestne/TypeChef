package de.fost.typechef.parser.java15
import de.fosd.typechef.parser.java15.JavaLexer

import java.io._

object ParseMobileMedia {


	val path = "W:\\work\\workspaces\\cide2runtime\\MobileMedia08_OO\\src\\lancs\\mobilemedia"

    val dir = new File(path)

    def main(args: Array[String]) {
        checkDir(dir)
    }

    def checkDir(dir: File) {
        if (dir.isFile)
            checkFile(dir)
        else
            //recurse
            dir.listFiles.map(checkDir(_))
    }
    def checkFile(file: File) {
        if (file.getName.endsWith(".java")) {
            println(file.getAbsolutePath)
            JavaLexer.lexFile(file.getAbsolutePath)
        }
    }

}