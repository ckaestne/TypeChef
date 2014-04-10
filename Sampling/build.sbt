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



