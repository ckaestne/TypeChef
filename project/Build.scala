// https://github.com/harrah/xsbt/wiki/Full-Configuration-Example

import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object BuildSettings {

    import Dependencies._

    val buildOrganization = "de.fosd.typechef"
    val buildVersion = "0.3"
    val buildScalaVersion = "2.9.1"

    val testEnvironment = Seq(junit, junitInterface, scalatest, scalacheck)

    val buildSettings = Defaults.defaultSettings ++ assemblySettings ++ Seq(
        organization := buildOrganization,
        version := buildVersion,
        scalaVersion := buildScalaVersion,
        shellPrompt := ShellPrompt.buildShellPrompt,

        testListeners <<= target.map(t => Seq(new eu.henkelmann.sbt.JUnitXmlTestsListener(t.getAbsolutePath))),

        javacOptions ++= Seq("-source", "1.5", "-Xlint:unchecked"),
        scalacOptions ++= Seq("-deprecation", "-unchecked", "-optimise", "-explaintypes"),
        //    scalacOptions <+= scalaSource in Compile map { "-P:sxr:base-directory:" + _.getAbsolutePath },

        libraryDependencies ++= testEnvironment,

        parallelExecution := false, //run into memory problems on hudson otherwise

        test in assembly := {},
        jarName in assembly := "TypeChef.jar",
        assembleArtifact in packageScala := false,
        assembleArtifact in packageSrc := false
    )
}

object ShellPrompt {

    object devnull extends ProcessLogger {
        def info(s: => String) {}

        def error(s: => String) {}

        def buffer[T](f: => T): T = f
    }

    val current = """\*\s+(\w+)""".r

    def gitBranches = ("git branch --no-color" lines_! devnull mkString)

    val buildShellPrompt = {
        (state: State) => {
            val currBranch =
                current findFirstMatchIn gitBranches map (_ group (1)) getOrElse "-"
            val currProject = Project.extract(state).currentProject.id
            "%s:%s:%s> ".format(
                currProject, currBranch, BuildSettings.buildVersion
            )
        }
    }

}

object Dependencies {
    val junit = "junit" % "junit" % "4.8.2" % "test"
    val junitInterface = "com.novocode" % "junit-interface" % "0.6" % "test"
    val scalacheck = "org.scala-tools.testing" %% "scalacheck" % "1.9" % "test"
    val scalatest = "org.scalatest" %% "scalatest" % "1.6.1" % "test"
}

object VersionGen {

    object VersionGenKeys {
        val versionGen = TaskKey[Seq[File]]("version-gen")
        val versionGenPackage = SettingKey[String]("version-gen-package")
        val versionGenClass = SettingKey[String]("version-gen-class")
    }

    import VersionGenKeys._

    lazy val settings = Seq(
        versionGenPackage <<= Keys.organization {
            (org) => org
        },
        versionGenClass := "Version",
        versionGen <<= (sourceManaged in Compile, name, version, versionGenPackage, versionGenClass) map {
            (sourceManaged: File, name: String, version: String, vgp: String, vgc: String) =>
                val file = sourceManaged / vgp.replace(".", "/") / ("%s.scala" format vgc)
                val code =
                    (
                        if (vgp != null && vgp.nonEmpty) "package " + vgp + "\n"
                        else ""
                        ) +
                        "object " + vgc + " {\n" +
                        "  val name\t= \"" + vgc + "\"\n" +
                        "  val version\t= \"" + version + "\"\n" +
                        "}\n"
                IO write(file, code)
                Seq(file)
        },
        sourceGenerators in Compile <+= versionGen map identity
    )
}

object TypeChef extends Build {

    import BuildSettings._

    lazy val typechef = Project(
        "TypeChef",
        file("."),
        settings = buildSettings
    ) aggregate(
        featureexpr,
        conditionallib,
        parserexp,
        jcpp,
        cparser,
        ctypechecker,
        javaparser,
        crewrite
        )

    lazy val featureexpr = Project(
        "FeatureExprLib",
        file("FeatureExprLib"),
        settings = buildSettings
    )

    lazy val conditionallib = Project(
        "ConditionalLib",
        file("ConditionalLib"),
        settings = buildSettings
    ) dependsOn (featureexpr)

    lazy val parserexp = Project(
        "ParserFramework",
        file("ParserFramework"),
        settings = buildSettings
    ) dependsOn(featureexpr, conditionallib)

    lazy val jcpp = Project(
        "PartialPreprocessor",
        file("PartialPreprocessor"),
        settings = buildSettings
    ) dependsOn (featureexpr)

    lazy val cparser = Project(
        "CParser",
        file("CParser"),
        settings = buildSettings ++ Seq(parallelExecution in Test := false)
    ) dependsOn(featureexpr, jcpp, parserexp, conditionallib)


    lazy val frontend = Project(
        "Frontend",
        file("Frontend"),
        settings = buildSettings ++ VersionGen.settings
    ) dependsOn(featureexpr, jcpp, cparser, ctypechecker, conditionallib)

    lazy val ctypechecker = Project(
        "CTypeChecker",
        file("CTypeChecker"),
        settings = buildSettings
    ) dependsOn(cparser % "test->test;compile->compile", conditionallib)

    lazy val javaparser = Project(
        "JavaParser",
        file("JavaParser"),
        settings = buildSettings
    ) dependsOn(featureexpr, parserexp, conditionallib)

    lazy val crewrite = Project(
        "CRewrite",
        file("CRewrite"),
        settings = buildSettings
    ) dependsOn(cparser % "test->test;compile->compile", ctypechecker, conditionallib)
}

