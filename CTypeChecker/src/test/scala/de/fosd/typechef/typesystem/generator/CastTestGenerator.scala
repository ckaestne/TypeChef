package de.fosd.typechef.typesystem.generator

/**
  * there are too many cases and the declaration isn't understandable.
  *
  * instead, we generate test cases and check with gcc whether those test
  * cases should fail or not (differential testing)
  *
  * we consider a number of possible changes in a test and use a sampling
  * strategy to cover many combinations of the changes
  */
object CastTestGenerator extends App with AbstractGenerator {

    def types =
        """char
          |signed char
          |unsigned char
          |unsigned int
          |signed int
          |long
          |float
          |double
          |long double
          |int *
          |int **
          |char *
          |double *
          |struct S
          |struct T
          |struct_anonymous
          |struct { int a; }
          |struct { float b; }
          |volatile int
          |const int
          |const double
          |volatile double
          |int *
          |const int *
          |volatile int *
        """.stripMargin.split("\n").map(_.trim).filter(_.nonEmpty)

    override protected def gccParam: List[String] = "-Wall" :: super.gccParam

    val configSpace = List(Opt(types.size), Opt(types.size))


    def genTest(c: Config): List[String] = {
        val t1 = genType(c.vals(0))
        val t2 = genType(c.vals(1))
        var t = s"              $t1 foo();\n" +
            s"              $t2 bar() {\n" +
            s"                $t2 a = ($t2) foo();\n" +
            "                return a;\n" +
            "              }"

        List(addStructs(t))
    }


    def genType(t: Int) = types(t)

    generate("GeneratedCastTests", bruteforceConfigs)

}
