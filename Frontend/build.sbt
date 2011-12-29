libraryDependencies += "gnu.getopt" % "java-getopt" % "1.0.13"

mainClass := Some("de.fosd.typechef.Frontend")

TaskKey[File]("mkrun") <<= (baseDirectory, fullClasspath in Runtime, mainClass in Runtime) map { (base, cp, main) =>
  val template = """#!/bin/sh
java -classpath "%s" %s "$@"
"""
  val mainStr = main getOrElse error("No main class specified")
  val contents = template.format(cp.files.absString, mainStr)
  val out = base / "../typechef.sh"
  IO.write(out, contents)
  out.setExecutable(true)
  out
}