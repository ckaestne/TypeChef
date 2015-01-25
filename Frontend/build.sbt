import AssemblyKeys._

libraryDependencies += "gnu.getopt" % "java-getopt" % "1.0.13"

mainClass in Runtime := Some("de.fosd.typechef.Frontend")


//generate typechef.sh file with full classpath
TaskKey[File]("mkrun") <<= (baseDirectory, fullClasspath in Runtime, mainClass in Runtime) map { (base, cp, main) =>
  val template = """#!/bin/sh
java -ea -Xmx1536m -Xms128m -Xss10m -classpath "%s" %s "$@"
"""
  val mainStr = main getOrElse sys.error("No main class specified")
  val contents = template.format(cp.files.absString, mainStr)
  val out = base / "../typechef.sh"
  IO.write(out, contents)
  out.setExecutable(true)
  out
}



//generate a single fat jar file with the assembly plugin

Seq(assemblySettings: _*)

test in assembly := {}

jarName in assembly := s"../../../TypeChef-${version.value}.jar" 


//assembleArtifact in packageScala := false

assembleArtifact in packageSrc := false

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case f if f startsWith "org/fusesource/" => MergeStrategy.first
    case x => old(x)
  }
}
