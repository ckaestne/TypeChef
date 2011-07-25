import sbt._
import reaktor.scct.ScctProject
import eu.henkelmann.sbt.JUnitXmlTestsListener

class TypeChef(info: ProjectInfo) extends ParentProject(info) with IdeaProject {

    lazy val featureexpr = project("FeatureExprLib", "FeatureExprLib", new DefaultSubProject(_) {
        val sat4j = "org.sat4j" % "org.sat4j.core" % "2.3.0"
    })
    lazy val conditionalLib = project("ConditionalLib","ConditionalLib", new DefaultSubProject(_) with Kiama, featureexpr)
    lazy val parserexp = project("ParserFramework", "ParserFramework", new DefaultSubProject(_) with Kiama, featureexpr, conditionalLib)
    lazy val jcpp = project("PartialPreprocessor", "PartialPreprocessor", new JavaSubProject(_), featureexpr)
    lazy val cparser = project("CParser", "CParser", new DefaultSubProject(_) with Kiama, featureexpr, jcpp, parserexp, conditionalLib)
    lazy val linuxAnalysis = project("LinuxAnalysis", "LinuxAnalysis", new LinuxAnalysisProject(_), featureexpr, jcpp, cparser, ctypechecker, conditionalLib)
    lazy val ctypechecker = project("CTypeChecker", "CTypeChecker", new DefaultSubProject(_) with Kiama, cparser, conditionalLib)
    lazy val javaparser = project("JavaParser", "JavaParser", new DefaultSubProject(_), featureexpr, parserexp, conditionalLib)
    lazy val crewrite = project("CRewrite", "CRewrite", new DefaultSubProject(_) with Kiama, cparser, ctypechecker, conditionalLib)

    class DefaultSubProject(info: ProjectInfo) extends DefaultProject(info) with ScctProject with IdeaProject {
        val junitInterface = "com.novocode" % "junit-interface" % "0.6" % "test->default"
        override def testOptions = super.testOptions ++ Seq(TestArgument(TestFrameworks.JUnit, "-q", "-v"))
        val scalacheck = "org.scala-tools.testing" % "scalacheck_2.8.1" % "1.8" % "test->default"
        def junitXmlListener: TestReportListener = new JUnitXmlTestsListener(outputPath.toString)
        override def testListeners: Seq[TestReportListener] = super.testListeners ++ Seq(junitXmlListener)

        override def javaCompileOptions = super.javaCompileOptions ++ javaCompileOptions("-source", "1.5", "-Xlint:unchecked")
        override def compileOptions = super.compileOptions ++ Seq(Unchecked,
            Deprecation, ExplainTypes, Optimize)
    }

    class LinuxAnalysisProject(info: ProjectInfo) extends DefaultSubProject(info) {
        lazy val parse = runTask("de.fosd.typechef.linux.LinuxParser")
        lazy val typecheck = runTask("de.fosd.typechef.linux.LinuxTypeChecker")
        lazy val processFileList = runTask("de.fosd.typechef.linux.ProcessFileList")
        lazy val stats = runTask("de.fosd.typechef.linux.Stats")
        lazy val pcppStats = runTask("de.fosd.typechef.linux.PCPPStats")
        lazy val web = runTask("de.fosd.typechef.linux.WebFrontend")
    }

    trait Kiama {
        val kiama = "com.googlecode" %% "kiama" % "1.0.2"
    }


    class JavaSubProject(info: ProjectInfo) extends DefaultProject(info) with ScctProject with IdeaProject {
        //-source 1.5 is required for standalone ecj - it defaults to 1.3!
        override def javaCompileOptions = super.javaCompileOptions ++ javaCompileOptions("-source", "1.5", "-Xlint:unchecked")
        val junit = "junit" % "junit" % "4.8.2" % "test->default"
    }

}
