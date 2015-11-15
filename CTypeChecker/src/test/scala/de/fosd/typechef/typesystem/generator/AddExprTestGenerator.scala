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
object AddExprTestGenerator extends App with AbstractGenerator {

    def types =
        """char
          |signed char
          |unsigned char
          |signed short int
          |unsigned short int
          |unsigned int
          |signed int
          |signed long
          |signed long long int
          |unsigned long
          |unsigned long long
          |float
          |double
          |long double
          |struct S
          |struct T
          |struct { int a; }
          |struct { float b; }
          |int *
          |long *
          |double *
          |volatile int
          |const int
          |void
          |void *
        """.stripMargin.split("\n").map(_.trim).filter(_.nonEmpty)

    val configSpace = List(Opt(types.size), Opt(types.size), Opt(types.size))


    def genTest(c: Config): List[String] = {
        val t1 = genType(c.vals(0))
        val t2 = genType(c.vals(1))
        val t3 = genType(c.vals(2))
        var t = s"              $t1 foo();\n" +
            s"              $t2 bar();\n" +
            s"              $t3 x() {\n" +
            s"                $t1 a = foo();\n" +
            s"                $t2 b = bar();\n" +
            s"                $t3 c = a + b;\n" +
            "                return c;\n" +
            "              }"

        if (t contains "struct S")
            t = "              struct S { int x; int y; };\n\n" + t
        if (t contains "struct T")
            t = "              struct T { int x; int y; int z; };\n\n" + t
        List(t)
    }


    def genType(t: Int) = types(t)

    generate("GeneratedAddExprTests", pairwiseRandConfigs)

}
