import sbt._
import reaktor.scct.ScctProject
import eu.henkelmann.sbt.JUnitXmlTestsListener

class KiamaExp(info: ProjectInfo) extends DefaultProject(info) with IdeaProject {

    val kiama = "com.googlecode" %% "kiama" % "1.0.2"
    val featureexpr = "FeatureExprLib" % "FeatureExprLib" % "0.2"

        val junitInterface = "com.novocode" % "junit-interface" % "0.6" % "test->default"
        override def testOptions = super.testOptions ++ Seq(TestArgument(TestFrameworks.JUnit, "-q", "-v"))
        val scalacheck = "org.scala-tools.testing" % "scalacheck_2.8.1" % "1.8" % "test->default"
        def junitXmlListener: TestReportListener = new JUnitXmlTestsListener(outputPath.toString)
        override def testListeners: Seq[TestReportListener] = super.testListeners ++ Seq(junitXmlListener)

        override def javaCompileOptions = super.javaCompileOptions ++ javaCompileOptions("-source", "1.5", "-Xlint:unchecked")
        override def compileOptions = super.compileOptions ++ Seq(Unchecked,
            Deprecation, ExplainTypes, Optimize)

}
