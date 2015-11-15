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
object VarDeclTestGenerator extends App with AbstractGenerator {

    def types = ReturnConstantTestGenerator.types


    val configSpace = List(Opt(types.size))
    override protected def considerWarnings: Boolean = true
    override protected def ignoredTests = Map("conf16" -> "cannot distinguish origin of anonymous struct type")

    def genTest(c: Config): List[String] = {
        val t1 = types(c.vals(0))

        List(addStructs(s"              $t1 x;"),
            addStructs(s"              void foo() { $t1 x; }"),
            addStructs(s"              void foo($t1 x) {} "),
            addStructs(s"              void foo($t1 x); "),
            addStructs(s"              struct x { $t1 x; };")
        )
    }


    generate("GeneratedVarDeclTests", bruteforceConfigs)

}
