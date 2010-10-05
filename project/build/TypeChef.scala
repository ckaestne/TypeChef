import sbt._

class TypeChef(info:ProjectInfo) extends ParentProject(info){
	lazy val featureexpr = project("FeatureExprLib","FeatureExprLib")
	lazy val parserexp = project("ParserFramework","Parser Core",featureexpr)
	lazy val jcpp = project("PartialPreprocessor","Partial Preprocessor",featureexpr)
	lazy val cparser = project("CParser", "CParser",featureexpr,jcpp,parserexp)
	lazy val boacasestudy = project("BoaCaseStudy", "BoaCaseStudy",cparser)
	lazy val ctypechecker = project("CTypeChecker", "CTypeChecker",cparser)
	//val junit = "junit" % "junit" % "4.8"
	 val junitInterface = "com.novocode" % "junit-interface" % "0.5" % "test->default"
}
