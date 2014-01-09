package edu.iastate.hungnv.test

/**
 * @author HUNG
 */
object Util {

  def log(msg: String = "", newLine: Boolean = true): Unit = {
    if (newLine)
      println(msg)
	else
	  print(msg)
  }
  
  def standardize(s: String): String = {
    s.replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r")
  }
  
  def padding(depth: Int, s: String = "|  "): String = {
    val out = new StringBuilder
    for (i <- 1 to depth)
      out ++= s
    out.toString
  }
  
}