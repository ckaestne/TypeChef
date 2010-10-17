import sbt._

class TypeChef(info: ProjectInfo) extends ParentProject(info) {
	lazy val featureexpr = project("FeatureExprLib", "FeatureExprLib")
	lazy val parserexp = project("ParserFramework", "Parser Core", featureexpr)
	lazy val jcpp = project("PartialPreprocessor", "Partial Preprocessor",
	  info => new CustomProject(info), featureexpr)

	class CustomProject(info: ProjectInfo) extends DefaultProject(info) {
	  //-source 1.5 is required for standalone ecj - it defaults to 1.3!
	  override def javaCompileOptions = super.javaCompileOptions ++ javaCompileOptions("-source", "1.5")
	}

	lazy val cparser = project("CParser", "CParser", featureexpr, jcpp, parserexp)
	lazy val ctypechecker = project("CTypeChecker", "CTypeChecker", cparser)
	lazy val boacasestudy = project("BoaCaseStudy", "BoaCaseStudy", ctypechecker)
	//val junit = "junit" % "junit" % "4.8"
	val junitInterface = "com.novocode" % "junit-interface" % "0.5" % "test->default"
}
