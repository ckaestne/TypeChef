import sbt._
import Keys._

object TypeChefBuild extends Build {
    lazy val root = Project(id = "TypeChef",
        base = file(".")) aggregate (
            featureexpr,
            conditionalLib,
            parserexp,
            jcpp,
            cparser,
            linuxAnalysis,
            ctypechecker,
            javaparser,
            crewrite)

    lazy val featureexpr = Project(id = "FeatureExprLib", base = file("FeatureExprLib"),
        settings = Project.defaultSettings ++ scalaSettings)

    lazy val conditionalLib = Project(
        id = "ConditionalLib",
        base = file("ConditionalLib"),
        settings = Project.defaultSettings ++ kiamaSettings ++ scalaSettings) dependsOn (featureexpr)

    lazy val parserexp = Project(
        id = "ParserFramework",
        base = file("ParserFramework"),
        settings = Project.defaultSettings ++ kiamaSettings ++ scalaSettings) dependsOn (featureexpr, conditionalLib)

    lazy val jcpp = Project(
        id = "PartialPreprocessor",
        base = file("PartialPreprocessor"),
        settings = Project.defaultSettings ++ scalaSettings) dependsOn (featureexpr)

    lazy val cparser = Project(id = "CParser", base = file("CParser"),
        settings = Project.defaultSettings ++ kiamaSettings ++ scalaSettings) dependsOn (featureexpr, jcpp, parserexp, conditionalLib)
    lazy val linuxAnalysis = Project(id = "LinuxAnalysis", base = file("LinuxAnalysis"),
        settings = Project.defaultSettings ++ scalaSettings) dependsOn (featureexpr, jcpp, cparser, ctypechecker, conditionalLib)
    lazy val ctypechecker = Project(id = "CTypeChecker", base = file("CTypeChecker"),
        settings = Project.defaultSettings ++ kiamaSettings ++ scalaSettings) dependsOn (cparser, conditionalLib)
    lazy val javaparser = Project(id = "JavaParser", base = file("JavaParser"),
        settings = Project.defaultSettings ++ scalaSettings) dependsOn (featureexpr, parserexp, conditionalLib)
    lazy val crewrite = Project(id = "CRewrite", base = file("CRewrite"),
        settings = Project.defaultSettings ++ kiamaSettings ++ scalaSettings) dependsOn (cparser, ctypechecker, conditionalLib)


    def kiamaSettings: Seq[Setting[_]] = Seq(
        libraryDependencies += "com.googlecode" % "kiama_2.9.0" % "1.1.0"
    )

    def scalaSettings: Seq[Setting[_]] = Seq(
        libraryDependencies += "com.novocode" % "junit-interface" % "0.6" % "test->default",
        libraryDependencies += "org.scala-tools.testing" % "scalacheck_2.9.0" % "1.9" % "test->default",
        libraryDependencies += "junit" % "junit" % "4.8.2" % "test->default",
        testListeners <<= target.map(t => Seq(new eu.henkelmann.sbt.JUnitXmlTestsListener(t.getAbsolutePath))),
        javacOptions ++= Seq("-source", "1.5", "-Xlint:unchecked")
        //        compileOptions ++= Seq(Unchecked, Deprecation, ExplainTypes, Optimize)
    )
}
