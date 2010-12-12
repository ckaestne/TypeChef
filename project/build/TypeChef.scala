import sbt._
import reaktor.scct.ScctProject

class TypeChef(info: ProjectInfo) extends ParentProject(info) {
	lazy val featureexpr = project("FeatureExprLib", "FeatureExprLib", new DefaultSubProject(_))
	lazy val parserexp = project("ParserFramework", "Parser Core", new DefaultSubProject(_), featureexpr)
	lazy val jcpp = project("PartialPreprocessor", "Partial Preprocessor",	new JavaSubProject(_), featureexpr)
	lazy val cparser = project("CParser", "CParser", new DefaultSubProject(_), featureexpr, jcpp, parserexp)
	lazy val boacasestudy = project("BoaCaseStudy", "BoaCaseStudy", new DefaultSubProject(_), cparser, ctypechecker)
	lazy val ctypechecker = project("CTypeChecker", "CTypeChecker", new DefaultSubProject(_), cparser)
	lazy val javaparser = project("JavaParser", "JavaParser", new DefaultSubProject(_), featureexpr, parserexp)

	class DefaultSubProject(info:ProjectInfo) extends DefaultProject(info) with ScctProject {
                lazy val hi = task { println("Hello World"); None }
                val junitInterface = "com.novocode" % "junit-interface" % "0.5" % "test->default"
//		val scalatest = "org.scala-tools.testing" % "scalatest" % "0.9.5" % "test->default"
	  	override def javaCompileOptions = super.javaCompileOptions ++ javaCompileOptions("-source", "1.5")
        }
	class JavaSubProject(info: ProjectInfo) extends DefaultProject(info) {
	  //-source 1.5 is required for standalone ecj - it defaults to 1.3!
	  override def javaCompileOptions = super.javaCompileOptions ++ javaCompileOptions("-source", "1.5")
	}

}
