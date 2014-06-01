import sbt._

object MyPlugins extends Build {
  lazy val root = Project("root", file(".")) dependsOn (junitXmlListener)
  lazy val junitXmlListener = uri("http://github.com/rasch/junit_xml_listener.git#7658ee513e9767f6056adf7d245ec2948b84c33c")
}
