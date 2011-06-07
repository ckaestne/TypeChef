package de.fosd.typechef.typesystem

import org.junit.Test
import de.fosd.typechef.parser.c.TranslationUnit
import java.io.{InputStream, FileNotFoundException}

class FileTest extends TestHelper {

    val folder = "testfiles/"
    private def check(filename: String): Boolean = {
        val start = System.currentTimeMillis
        println("parsing " + filename)
        var inputStream: InputStream = getClass.getResourceAsStream("/" + folder + filename)
        if (inputStream == null) {
            throw new FileNotFoundException("Input file not found: " + filename)
        }
        val ast = parseFile(inputStream, filename, folder)
        val parsed = System.currentTimeMillis
        println("type checking " + filename + " (" + (parsed - start) + ")")
        val r = check(ast)
        println("done. (" + (System.currentTimeMillis - parsed) + ")")
        r
    }
    private def check(ast: TranslationUnit): Boolean = new CTypeSystem().checkAST(ast)


    //async.i
    @Test def test1 {check("test1.i")}


}