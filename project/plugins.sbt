resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "0.11.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.7.2")

addCompilerPlugin("org.scala-tools.sxr" % "sxr_2.9.0" % "0.2.7")

