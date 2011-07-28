package de.fosd.typechef.typesystem

import org.junit.Test
import java.io.{InputStream, FileNotFoundException}
import de.fosd.typechef.parser.c.{TestHelper, TranslationUnit}

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
    @Test def test1 {assert(check("test1.xi"))}
    @Test def busybox_ar {assert(check("ar.xi"))}
    @Test def boa_boa {assert(check("boa.xi"))}
    @Test def boa_boa_pi {assert(check("boa.pi"))}


}