import java.io.File

/**
 * Boa test case.
 */
class BoaFilesTest extends AbstractTestProject {
    override def projDir = "boa" + File.separator + "src"

    protected def nameList = Array(
        "alias", "boa", "buffer", "cgi",
        "cgi_header", "config", "escape", "get", "hash", "ip", "log",
        "mmap_cache", "pipe", "queue", "read", "request", "response",
        "select", "signals", "sublog", "util"
    )
    //Considering HAVE_LIBDMALLOC causes the program to try including dmalloc.h, which is not installed. 
    override def extraPreprocessorOpt = super.extraPreprocessorOpt ++ Array("-U", "HAVE_LIBDMALLOC")
}

object BoaFilesTest {
    def main(args: Array[String]) = {
        new TestCaseHarness(new BoaFilesTest).run(args)
    }
}