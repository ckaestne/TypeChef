import AssemblyKeys._

libraryDependencies += "gnu.getopt" % "java-getopt" % "1.0.13"

mainClass in Runtime := Some("de.fosd.typechef.Sampling")


//generate typechef.sh file with full classpath
TaskKey[File]("mkrunsampling") <<= (baseDirectory, fullClasspath in Runtime, mainClass in Runtime) map { (base, cp, main) =>
  val template = """#!/bin/sh
java -ea -Xmx4096m -Xms128m -Xss10m -classpath "%s" %s "$@"
"""
  val mainStr = main getOrElse error("No main class specified")
  val contents = template.format(cp.files.absString, mainStr)
  val out = base / "../typechefsampling.sh"
  IO.write(out, contents)
  out.setExecutable(true)
  out
}



//generate a single fat jar file with the assembly plugin

seq(assemblySettings: _*)

test in assembly := {}

defaultJarName in assembly <<= version { v => "../../TypeChef-" + v + ".jar" }


//assembleArtifact in packageScala := false

assembleArtifact in packageSrc := false

//exclude signatures, cf http://emc-an.blogspot.com/2011/10/getting-one-big-jar-out-of-sbt-scala.html
excludedFiles in assembly := { (bases: Seq[File]) =>
   bases flatMap { base =>
     (base / "META-INF" * "*").get collect {
       case f if f.getName.toLowerCase.contains(".rsa") => f
       case f if f.getName.toLowerCase.contains(".dsa") => f
       case f if f.getName.toLowerCase.contains(".sf") => f
       case f if f.getName.toLowerCase == "manifest.mf" => f
     }
   }}

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case f if f startsWith "org/fusesource/" => MergeStrategy.first
    case x => old(x)
  }
}
