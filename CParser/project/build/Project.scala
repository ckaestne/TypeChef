import sbt._

class CParser(info:ProjectInfo) extends DefaultProject(info) {
	  val bryanjswift = "Bryan J Swift Repository" at "http://repos.bryanjswift.com/maven2/"
	 val junitInterface = "com.novocode" % "junit-interface" % "0.5" % "test->default"
	 val junit = "junit" % "junit" % "4.8.2" % "test"
	 override def testFrameworks = super.testFrameworks ++ List(new TestFramework("com.novocode.junit.JUnitFrameworkNoMarker"))
}
