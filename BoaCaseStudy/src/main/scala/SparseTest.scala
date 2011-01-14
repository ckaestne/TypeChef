class SparseTest extends AbstractTestProject {
    override def projDir = "sparse"

    protected def nameList = Array(
            "target", "parse", "tokenize", "pre-process", "symbol", "lib", "scope",
            "expression", "show-parse", "evaluate", "expand", "inline", "linearize", "sort",
            "allocate",

            //"compat-$(OS)",
            // Each of these files requires OS-specific headers:
            //"compat-mingw", "compat-solaris"
            "compat-linux", "compat-cygwin", "compat-bsd",

            "ptrlist", "flow", "cse", "simplify", "memops",
            "liveness", "storage", "unssa", "dissect", "test-lexing", "test-parsing",
            "obfuscate", "compile", "graph", "sparse", "test-linearize", "example",
            "test-unssa", "test-dissect", "ctags", "test-inspect", "c2xml"

    )
    override def extraPreprocessorOpt = super.extraPreprocessorOpt ++ Array("-P", "_H")
}

object SparseTest {
    def main(args: Array[String]) = {
        new TestCaseHarness(new SparseTest).run(args)
    }
}