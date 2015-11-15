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
object ReturnConstantTestGenerator extends App with AbstractGenerator {

    def types =
        """char
          |signed char
          |unsigned char
          |unsigned int
          |signed int
          |long
          |double
          |int *
          |char *
          |signed char *
          |unsigned char *
          |char **
          |unsigned char **
          |signed char **
          |double *
          |struct S
          |struct { float b; }
          |volatile int
          |const int
          |const double
          |volatile double
          |int *
          |const int *
          |volatile int *
          |void
        """.stripMargin.split("\n").map(_.trim).filter(_.nonEmpty)
    def constants =
        """0
          |1
          |-1
          |1l
          |0xa4
          |0.2
          |"0.2"
          |&"foo"
          |*"foo"
          |&1
        """.stripMargin.split("\n").map(_.trim).filter(_.nonEmpty)

    //TODO extend once curly initializers are properly supported
//    {}
//    {1,2}

    val configSpace = List(Opt(types.size), Opt(constants.size))
    override protected def considerWarnings: Boolean = true

    var _ignoredTests = Map("conf11_7" -> "handling of string literals (array with fixed length) is not precise enough")
    override def ignoredTests:Map[String,String]=super.ignoredTests ++ _ignoredTests

    def genTest(c: Config): List[String] = {
        val t1 = types(c.vals(0))
        val c1 = constants(c.vals(1)).replace("---","")
        val t =
            s"              $t1 x() { return $c1; }"

        val u =
            s"              $t1 x() {\n" +
                s"                $t1 a = $c1;\n" +
                "                return a;\n" +
                "              }"

        if (c1=="")
            List(addStructs(t))
        else if (c1 startsWith "{")
            List(addStructs(u))
        else
            List(addStructs(t), addStructs(u))
    }


    generate("GeneratedReturnConstantTests", bruteforceConfigs)

}
