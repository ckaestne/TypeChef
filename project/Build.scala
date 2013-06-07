// https://github.com/harrah/xsbt/wiki/Full-Configuration-Example

import java.text.SimpleDateFormat
import java.util.Date
import sbt._
import Keys._

object BuildSettings {

    import Dependencies._

    val buildOrganization = "de.fosd.typechef"
    val buildVersion = "0.3.5"
    val buildScalaVersion = "2.10.1"

    val testEnvironment = Seq(junit, junitInterface, scalatest, scalacheck)

    val buildSettings = Defaults.defaultSettings ++ Seq(
        organization := buildOrganization,
        version := buildVersion,
        scalaVersion := buildScalaVersion,
        shellPrompt := ShellPrompt.buildShellPrompt,

        testListeners <<= target.map(t => Seq(new eu.henkelmann.sbt.JUnitXmlTestsListener(t.getAbsolutePath))),

        javacOptions ++= Seq("-Xlint:unchecked"),
        scalacOptions ++= Seq("-deprecation", "-unchecked", "-optimise", "-explaintypes"),

        crossScalaVersions := Seq("2.9.1", "2.9.2", "2.10.1"),

        libraryDependencies ++= testEnvironment,

        parallelExecution := false, //run into memory problems on hudson otherwise

        homepage := Some(url("https://github.com/ckaestne/TypeChef")),
        licenses := Seq("GNU General Public License v3.0" -> url("http://www.gnu.org/licenses/gpl.txt")),

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
        pomIncludeRepository := { _ => false },
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
    val scalacheck = "org.scalacheck" %% "scalacheck" % "1.10.0" % "test"
    val scalatest = "org.scalatest" %% "scalatest" % "1.8" % "test" cross CrossVersion.binaryMapped {
        case "2.10" => "2.10.0" // useful if a%b was released with the old style
        case "2.10.1" => "2.10.0" // useful if a%b was released with the old style
        case x => x
    }
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
        ctypechecker,
        javaparser,
        crewrite,
        frontend,
        sampling
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
        settings = buildSettings ++ 
          Seq(parallelExecution in Test := false,
            libraryDependencies <+= scalaVersion(kiamaDependency(_,true)))
    ) dependsOn(featureexpr, jcpp, parserexp, conditionallib)


    lazy val frontend = Project(
        "Frontend",
        file("Frontend"),
        settings = buildSettings ++ VersionGen.settings
    ) dependsOn(featureexpr, jcpp, cparser % "test->test;compile->compile", ctypechecker, conditionallib, crewrite, javaparser)

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
        settings = buildSettings ++
            Seq(libraryDependencies <+= scalaVersion(kiamaDependency(_)))
    ) dependsOn(cparser % "test->test;compile->compile", ctypechecker, conditionallib)

    lazy val sampling = Project(
        "Sampling",
        file("Sampling"),
        settings = buildSettings ++
            Seq(libraryDependencies <+= scalaVersion(kiamaDependency(_)))
    ) dependsOn(cparser % "test->test;compile->compile", ctypechecker, crewrite, conditionallib, frontend)

    def kiamaDependency(scalaVersion: String, testOnly:Boolean=false) = {
      val x=scalaVersion match {
        case "2.9.1" => "com.googlecode.kiama" %% "kiama" % "1.2.0"
        case _ => "com.googlecode.kiama" %% "kiama" % "1.4.0"
      }
      if (testOnly) x % "test" else x
    }
}

