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

    case class Config(vals: List[Int]) {
        override def toString = vals.mkString("conf", "_", "")

        def updated(idx: Int, v: Int) = Config(vals.updated(idx, v))
    }

    def genTest(c: Config): String

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
              |import org.junit.runner.RunWith
              |import org.scalatest.{Matchers, FunSuite}
              |import org.scalatest.junit.JUnitRunner
              |import de.fosd.typechef.typesystem._
              |
              |@RunWith(classOf[JUnitRunner])
              |class $className extends FunSuite with Matchers with TestHelperTS {
              |
              | """.stripMargin.replace("$className", className))

        var testedConfigs: Set[Config] = Set()

        for (c <- configs;
             if !testedConfigs.contains(c)) {
            testedConfigs += c

            val fileAddr = File.createTempFile(c.toString, ".c")
            val file = fileAddr.getAbsolutePath
            val w = new FileWriter(file)
            val testBody = genTest(c)
            w.write(testBody)
            w.close()
            var msg = ""
            val log = new ProcessLogger {
                override def buffer[T](f: => T): T = f

                override def out(s: => String): Unit = msg += s + "\n"

                override def err(s: => String): Unit = msg += s + "\n"
            }
            val exitcode = s"gcc -Wall -c $file" ! log

            println(c + ": " + exitcode)

            writeTest(testFileWriter, c, testBody, msg, exitcode)
            fileAddr.deleteOnExit()

        }
        println("generated "+testedConfigs.size+" tests")

        testFileWriter.write("\n\n}")
        testFileWriter.close()

    }

    def writeTest(testFileWriter: FileWriter, c: Config, testBody: String, msg: String, exitcode: Int): Unit = {
        val s = "   test(\"generated test " + c + "\") {\n"
        testFileWriter.write(s)
        if (msg.nonEmpty)
            testFileWriter.write("        /* gcc reports:\n" +
                msg + "\n        */\n")


        testFileWriter.write(
            "        " + (if (exitcode == 0) "correct" else "error") +
                "(\"\"\"\n" + testBody + "\n" +
                "                \"\"\")\n" +
                "   }\n")
    }
}
