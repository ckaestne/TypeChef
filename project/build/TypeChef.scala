import sbt._
import reaktor.scct.ScctProject
import webbytest.HtmlTestsProject

class TypeChef(info: ProjectInfo) extends ParentProject(info) with IdeaProject {
    val junit = "junit" % "junit" % "4.8.2" % "test->default"
    val junitInterface = "com.novocode" % "junit-interface" % "0.5" % "test->default"
    val scalacheck = "org.scala-tools.testing" % "scalacheck_2.8.1" % "1.8" % "test->default"

    lazy val sat4j = project("org.sat4j.core", "sat4j", new JavaSubProject(_))
    lazy val featureexpr = project("FeatureExprLib", "FeatureExprLib", new DefaultSubProject(_), sat4j)
    lazy val parserexp = project("ParserFramework", "Parser Core", new DefaultSubProject(_), featureexpr)
    lazy val jcpp = project("PartialPreprocessor", "Partial Preprocessor", new JavaSubProject(_), featureexpr)
    lazy val cparser = project("CParser", "CParser", new DefaultSubProject(_), featureexpr, jcpp, parserexp)
    lazy val boacasestudy = project("BoaCaseStudy", "BoaCaseStudy", new DefaultSubProject(_), cparser, ctypechecker)
    lazy val ctypechecker = project("CTypeChecker", "CTypeChecker", new DefaultSubProject(_), cparser)
    lazy val javaparser = project("JavaParser", "JavaParser", new DefaultSubProject(_), featureexpr, parserexp)

    class DefaultSubProject(info: ProjectInfo) extends DefaultProject(info) with ScctProject with IdeaProject with HtmlTestsProject {
        //val junitInterface = "com.novocode" % "junit-interface" % "0.5" % "test->default"
        //val scalacheck = "org.scala-tools.testing" % "scalacheck_2.8.1" % "1.8" % "test->default"
        //		val scalatest = "org.scala-tools.testing" % "scalatest" % "0.9.5" % "test->default"
        override def javaCompileOptions = super.javaCompileOptions ++ javaCompileOptions("-source", "1.5")
        val scalacheck = "org.scala-tools.testing" % "scalacheck_2.8.1" % "1.8" % "test->default"
    }

    class JavaSubProject(info: ProjectInfo) extends DefaultProject(info) with HtmlTestsProject with ScctProject with IdeaProject {
        //-source 1.5 is required for standalone ecj - it defaults to 1.3!
        override def javaCompileOptions = super.javaCompileOptions ++ javaCompileOptions("-source", "1.5")
        val junit = "junit" % "junit" % "4.8.2" % "test->default"
    }
}
