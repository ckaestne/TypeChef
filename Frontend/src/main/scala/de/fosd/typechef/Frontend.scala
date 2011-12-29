package de.fosd.typechef

/**
 Main frontend class for TypeChef

 intended to be largely parameter compatible to gcc
 */


trait Config {

  def printVersion

  //lexer / preprocessor
  def preprocessOnly:Boolean // -E
  def definedMacros:Map[String,String] // -D
  def undefMacros:Seq[String] // -U
  def includeDirectories:Seq[String] // -I, -L
  def writePreprocessedFile // -PI
  def writeMacroDebug
  def writeHeaderNestingDebug
  def allPreprocessorDebug // enables all of the above


  //LIBRARY_PATH
  //CPATH,  C_INCLUDE_PATH
  
  
}