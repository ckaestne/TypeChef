package de.fosd.typechef.typesystem.generator

import java.io.{File, FileWriter}

import scala.sys.process._
import scala.util.Random

/**
  * infrastructure for generating test cases that compare against GCC
  */
trait AbstractGenerator {

    case class Opt(numberOfPossibleValues: Int)

    def configSpace: List[Opt]

    protected def gccParam: List[String] = Nil
    protected def considerWarnings: Boolean = false
    protected def ignoredTests: Map[String, String] = Map()

    case class Config(vals: List[Int]) {
        override def toString = vals.mkString("conf", "_", "")

        def updated(idx: Int, v: Int) = Config(vals.updated(idx, v))
    }

    def genTest(c: Config): List[String]

    private def pairs[A](elem: List[A]): Iterator[(A, A)] =
        for (a <- elem.tails.take(elem.size); b <- a.tail) yield (a.head, b)

    private val rand = new Random()

    protected def randomConfig: Config = Config(
        configSpace.map(opt => rand.nextInt(opt.numberOfPossibleValues)))

    protected def emptyConfig: Config = Config(
        configSpace.map(opt => 0))

    protected def pairwiseConfigs: Iterator[Config] =
        for ((i, j) <- pairs(configSpace.indices.toList);
             vi <- 0 until configSpace(i).numberOfPossibleValues;
             vj <- 0 until configSpace(j).numberOfPossibleValues
        )
            yield emptyConfig.updated(i, vi).updated(j, vj)

    protected def pairwiseRandConfigs: Iterator[Config] =
        for ((i, j) <- pairs(configSpace.indices.toList);
             vi <- 0 until configSpace(i).numberOfPossibleValues;
             vj <- 0 until configSpace(j).numberOfPossibleValues
        )
            yield randomConfig.updated(i, vi).updated(j, vj)

    protected def bruteforceConfigs: Iterator[Config] = _bruteforceConfigs(0, configSpace).iterator

    private def _bruteforceConfigs(idx: Int, space: List[Opt]): Seq[Config] =
        if (space.isEmpty)
            List(emptyConfig)
        else {
            val other = _bruteforceConfigs(idx + 1, space.tail)
            (for (i <- 0 until space.head.numberOfPossibleValues) yield other.map(_.updated(idx, i))).flatten
        }


    def generate(className: String, configs: Iterator[Config]) {

        val testFileWriter = new FileWriter(new File(s"CTypeChecker/src/test/scala/de/fosd/typechef/typesystem/generated/$className.scala"))
        testFileWriter.write(
            """package de.fosd.typechef.typesystem.generated
              |
              |import org.junit._
              |import de.fosd.typechef.typesystem._
              |
              |/** generated tests! do not modify! */
              |class $className extends TestHelperTS {
              |
              | """.stripMargin.replace("$className", className))

        var testedConfigs: Set[Config] = Set()

        for (c <- configs;
             if !testedConfigs.contains(c)) {
            testedConfigs += c



            val testBodies = genTest(c)
            var tests = ""
            for (testBody <- testBodies) {
                val fileAddr = File.createTempFile(c.toString, ".c")
                val file = fileAddr.getAbsolutePath
                val w = new FileWriter(file)
                w.write(testBody)
                w.close()
                var msg = ""
                val log = new ProcessLogger {
                    override def buffer[T](f: => T): T = f

                    override def out(s: => String): Unit = msg += s + "\n"

                    override def err(s: => String): Unit = msg += s + "\n"
                }
                val gccp = gccParam.mkString(" ")
                val exitcode = s"gcc $gccp -c $file" ! log

                println(c + ": " + exitcode)
                msg = msg.replace(file, "test.c")

                tests += writeTest(c, testBody, msg, exitcode)
                fileAddr.deleteOnExit()
            }

            if (ignoredTests contains c.toString)
                testFileWriter.write("   @Ignore(\"" + ignoredTests(c.toString) + "\")\n")
            val s = "   @Test def test_" + c + "() {\n"
            testFileWriter.write(s)
            testFileWriter.write(tests)
            testFileWriter.write("   }\n\n\n")
        }
        println("generated " + testedConfigs.size + " tests")

        testFileWriter.write("\n\n}")
        testFileWriter.close()

    }

    def writeTest(c: Config, testBody: String, msg: String, exitcode: Int): String = {
        var result = ""
        if (msg.nonEmpty)
            result += "        /* gcc reports:\n" +
                msg + "\n        */\n"


        var isWarning = msg contains "warning: "

        result += "        " + (if (exitcode > 0) "error" else if (isWarning && considerWarnings) "warning" else "correct") +
            "(\"\"\"\n" + testBody + "\n" +
            "                \"\"\")\n"

        result
    }


    protected def addStructs(m: String): String = {
        var t = m
        if (t contains "struct S")
            t = "              struct S { int x; int y; };\n\n" + t
        if (t contains "struct T")
            t = "              struct T { int x; int y; int z; };\n\n" + t
        if (t contains "struct_anonymous")
            t = "              typedef struct { int x; } struct_anonymous;\n\n" + t
        t
    }
}
