package de.fosd.typechef.typesystem

import org.junit._
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
    private def check(ast: TranslationUnit): Boolean = new CTypeSystemFrontend(ast).checkAST


    //async.i
    @Test def test1 {assert(check("test1.xi"))}
    @Test def busybox_ar {assert(check("ar.xi"))}
    @Test def boa_boa {assert(check("boa.xi"))}
    @Test def boa_boa_pi {assert(check("boa.pi"))}
    @Test def busybox_top_pi {assert(check("top.pi"))}
    @Test def busybox_umount_pi {assert(check("umount.pi"))}
    @Test def busybox_udf_pi {assert(check("udf.pi"))}
    @Test
    @Ignore("too slow")
    def linux_fork_pi {assert(check("fork_.pi"))}
    @Test def toybox_patch_pi {assert(check("patch.pi"))}
    @Test def toybox_netcat_pi {assert(check("netcat.pi"))}
    @Test def busybox_modutils_pi {assert(check("modutils-24.pi"))}

}