package de.fosd.typechef.typesystem.linker

import org.junit._
import java.io.{InputStream, FileNotFoundException}
import de.fosd.typechef.parser.c.{TestHelper, TranslationUnit}

class LinkerTest extends TestHelper {

    val folder = "testfiles/"
    private def parse(filename: String): TranslationUnit = {
        val start = System.currentTimeMillis
        println("parsing " + filename)
        var inputStream: InputStream = getClass.getResourceAsStream("/" + folder + filename)
        if (inputStream == null) {
            throw new FileNotFoundException("Input file not found: " + filename)
        }
        val ast = parseFile(inputStream, filename, folder)
        val parsed = System.currentTimeMillis
        println("parsed " + filename + " (" + (parsed - start) + ")")
        ast
    }


    @Test
    def testMini {
        val ast = parse("mini.pi")
        val interface = new CInferInterface {}.inferInterface(ast)
        println(interface)

        //find imported function
        assert(interface.imports.exists(_.name == "bar"))
        assert(interface.imports.exists(_.name == "printf"))
        assert(interface.imports.exists(_.name == "local"))
        //foo declared but not used, should not show up in interface
        assert(!interface.imports.exists(_.name == "foo"))
        assert(!interface.imports.exists(_.name == "unusedlocal"))
        //local variables should not show up in interfaces
        assert(!interface.imports.exists(_.name == "a"))
        //main should be exported
        assert(interface.exports.exists(_.name == "main"))
        //local variables should not be exported
        assert(!interface.exports.exists(_.name == "a"))
    }

}