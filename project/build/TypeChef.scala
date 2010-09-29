import sbt._

class TypeChef(info:ProjectInfo) extends ParentProject(info){
	lazy val featureexpr = project("FeatureExprLib","FeatureExprLib")
	lazy val parserexp = project("ParserExperiment","Parser Core",featureexpr)
	lazy val jcpp = project("jcpp","Partial Preprocessor",featureexpr)
	lazy val cparser = project("CParser", "CParser",featureexpr,jcpp,parserexp)

	//val junit = "junit" % "junit" % "4.8"
	 val junitInterface = "com.novocode" % "junit-interface" % "0.5" % "test->default"
}
