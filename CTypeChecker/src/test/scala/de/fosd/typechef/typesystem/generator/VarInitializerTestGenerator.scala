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
object VarInitializerTestGenerator extends App with AbstractGenerator {

    def types = ReturnConstantTestGenerator.types

    def constants = ReturnConstantTestGenerator.constants

    val configSpace = List(Opt(types.size), Opt(constants.size))
    override protected def considerWarnings: Boolean = true

    var _ignoredTests: Map[String, String] = Map()
    override def ignoredTests: Map[String, String] = super.ignoredTests ++ _ignoredTests

    def genTest(c: Config): List[String] = {
        val t1 = types(c.vals(0))
        val c1 = constants(c.vals(1))
        val t = s"              $t1 x = $c1;"
        val u = s"              void foo() { $t1 x = $c1; x = $c1; }"

        if (Set("*", "&", "\"").exists(c1 startsWith _))
            _ignoredTests += (c.toString -> "initializers are not analyzed precisely enough")

        List(addStructs(t), addStructs(u))
    }


    generate("GeneratedVarInitializerTests", bruteforceConfigs)

}
