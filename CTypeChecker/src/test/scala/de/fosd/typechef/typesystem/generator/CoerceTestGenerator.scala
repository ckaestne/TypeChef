package de.fosd.typechef.typesystem.generator

import java.io.{File, FileWriter}

import de.fosd.typechef.typesystem.generator.RedeclTestGenerator._

import scala.sys.process._
import scala.util.Random

/**
  * there are too many cases and the declaration isn't understandable.
  *
  * instead, we generate test cases and check with gcc whether those test
  * cases should fail or not (differential testing)
  *
  * we consider a number of possible changes in a test and use a sampling
  * strategy to cover many combinations of the changes
  */
object CoerceTestGenerator extends App with AbstractGenerator {

    def types = CastTestGenerator.types

    val configSpace = List(Opt(types.size), Opt(types.size))


    def _firstParamType(c: Config): Int = c.vals(0)

    def _secondParamType(c: Config): Int = c.vals(1)

    def genTest(c: Config): String = {
        val t1 = genType(_firstParamType(c))
        val t2 = genType(_secondParamType(c))

        var t = s"              $t1 foo();\n" +
            "              void main() {\n" +
            s"                $t2 b = foo();\n" +
            "              }"

        if (t contains "struct S")
            t = "              struct S { int x; int y; };\n\n" + t
        if (t contains "struct T")
            t = "              struct T { int x; int y; int z; };\n\n" + t
        t
    }


    def genType(t: Int) = types(t)

    generate("GeneratedCoerceTests", bruteforceConfigs)

}
