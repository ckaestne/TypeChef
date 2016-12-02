// https://github.com/harrah/xsbt/wiki/Full-Configuration-Example

import java.text.SimpleDateFormat
import java.util.Date

import sbt.Keys._
import sbt._

object BuildSettings {

    import Dependencies._

    val buildOrganization = "de.fosd.typechef"
    val buildVersion = "0.4.2"
    val buildScalaVersion = "2.12.0"


    val buildSettings = Defaults.coreDefaultSettings ++ Seq(
        organization := buildOrganization,
        version := buildVersion,
        scalaVersion := buildScalaVersion,
        shellPrompt := ShellPrompt.buildShellPrompt,

        javacOptions ++= Seq("-Xlint:unchecked"),
        scalacOptions ++= Seq("-deprecation", "-unchecked"),
        testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v"),

        // suppress feature warnings in Scala 2.10.x
        // (we do not actually change the code to allow cross builds with prior scala versions)
        scalacOptions <++= scalaVersion map {
            sv =>
                if (sv startsWith "2.1") List(
                    "-feature",
                    "-language:postfixOps",
                    "-language:implicitConversions",
                    "-Xfatal-warnings" // make sure we take warnings seriously
                )
                else Nil
        },

        crossScalaVersions := Seq("2.10.6", "2.11.8"),

        conflictWarning := ConflictWarning.disable,

        libraryDependencies :=  {
            CrossVersion.partialVersion(scalaVersion.value) match {
                // if scala 2.11+ is used, add dependency on scala-xml module
                case Some((2, scalaMajor)) if scalaMajor >= 11 =>
                    libraryDependencies.value ++ testEnvironment ++ scala211Libraries
                case _ => libraryDependencies.value ++ testEnvironment
            }
        },

        parallelExecution := false, //need to sequentially execute tests, because some tests may use BDDs and others may use SAT and they should not intermix through parallelism

        homepage := Some(url("https://github.com/ckaestne/TypeChef")),
        licenses := Seq("GNU Lesser General Public License v3.0" -> url("http://www.gnu.org/licenses/lgpl.txt")),

        //maven
        publishTo <<= version {
            (v: String) =>
                val nexus = "https://oss.sonatype.org/"
                if (v.trim.endsWith("SNAPSHOT"))
                    Some("snapshots" at nexus + "content/repositories/snapshots")
                else
                    Some("releases" at nexus + "service/local/staging/deploy/maven2")
        },
        publishMavenStyle := true,
        publishArtifact in Test := false,
        pomIncludeRepository := {
            _ => false
        },
        pomExtra :=
            <parent>
                <groupId>org.sonatype.oss</groupId>
                <artifactId>oss-parent</artifactId>
                <version>7</version>
            </parent> ++
                <scm>
                    <connection>scm:git:git@github.com:ckaestne/TypeChef.git</connection>
                    <url>git@github.com:ckaestne/TypeChef.git</url>
                </scm> ++
                <developers>
                    <developer>
                        <id>ckaestne</id> <name>Christian Kaestner</name> <url>http://www.cs.cmu.edu/~ckaestne/</url>
                    </developer>
                </developers>,

        credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
    ) 
}

object ShellPrompt {

    object devnull extends ProcessLogger {
        def info(s: => String) {}
        def error(s: => String) {}
        def buffer[T](f: => T): T = f
    }

    val current = """\*\s+(\w+)""".r

    def gitBranches = (("git branch --no-color" lines_! devnull).mkString)

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
    val junit = "junit" % "junit" % "4.12" % "test"
    val junitInterface = "com.novocode" % "junit-interface" % "0.11" % "test"
    val scalacheck = "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
    val scalatest = "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    val scalaparsercombinators = "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4"
    val scalaxml = "org.scala-lang.modules" %% "scala-xml" % "1.0.6"

    val testEnvironment = Seq(junit, junitInterface, scalatest, scalacheck)
    val scala211Libraries = Seq(scalaparsercombinators, scalaxml)
}

object VersionGen {

    object VersionGenKeys {
        val versionGen = TaskKey[Seq[File]]("version-gen")
        val versionGenPackage = SettingKey[String]("version-gen-package")
        val versionGenClass = SettingKey[String]("version-gen-class")
    }

    import VersionGen.VersionGenKeys._

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
                        "class " + vgc + " extends VersionInfo {\n" +
                        "  def getVersion:String = \"" + version + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "\"\n" +
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
        errorlib,
        ctypechecker,
        javaparser,
        crewrite,
        frontend
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

    lazy val errorlib = Project(
        "ErrorLib",
        file("ErrorLib"),
        settings = buildSettings
    ) dependsOn (featureexpr)

    lazy val parserexp = Project(
        "ParserFramework",
        file("ParserFramework"),
        settings = buildSettings
    ) dependsOn(featureexpr, conditionallib, errorlib)

    lazy val jcpp = Project(
        "PartialPreprocessor",
        file("PartialPreprocessor"),
        settings = buildSettings
    ) dependsOn(featureexpr, conditionallib, errorlib)

    lazy val cparser = Project(
        "CParser",
        file("CParser"),
        settings = buildSettings ++
            Seq(parallelExecution in Test := false,
                libraryDependencies <+= scalaVersion(kiamaDependency(_)))
    ) dependsOn(featureexpr, jcpp, parserexp, conditionallib, errorlib)


    lazy val frontend = Project(
        "Frontend",
        file("Frontend"),
        settings = buildSettings ++ VersionGen.settings
    ) dependsOn(featureexpr, jcpp, cparser % "test->test;compile->compile", ctypechecker, conditionallib, crewrite, javaparser, errorlib)

    lazy val ctypechecker = Project(
        "CTypeChecker",
        file("CTypeChecker"),
        settings = buildSettings ++
            Seq(libraryDependencies <+= scalaVersion(kiamaDependency(_)))
    ) dependsOn(cparser % "test->test;compile->compile", conditionallib, errorlib)

    lazy val javaparser = Project(
        "JavaParser",
        file("JavaParser"),
        settings = buildSettings
    ) dependsOn(featureexpr, parserexp, conditionallib, errorlib)

    lazy val crewrite = Project(
        "CRewrite",
        file("CRewrite"),
        settings = buildSettings ++
            Seq(libraryDependencies <+= scalaVersion(kiamaDependency(_)))
    ) dependsOn(cparser % "test->test;compile->compile", ctypechecker, conditionallib, errorlib)


    def kiamaDependency(scalaVersion: String, testOnly: Boolean = false) = {
        val x = scalaVersion match {
            case "2.9.1" => "com.googlecode.kiama" %% "kiama" % "1.2.0"
            case _ => "com.googlecode.kiama" %% "kiama" % "1.8.0"
        }
        if (testOnly) x % "test" else x
    }
}

