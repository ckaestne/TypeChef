package edu.iastate.hungnv.test

import de.fosd.typechef.conditional._
import de.fosd.typechef.parser.javascript._
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}

/**
 * @author HUNG
 */
object JSPrinter2 {

  def printCond(ast: Conditional[AST], list_feature_expr: List[FeatureExpr], nestingLevel: Int): String = {
    ast match {
      case One(c: AST) => print(c, list_feature_expr, nestingLevel)
      
      case Choice(f, a: Conditional[AST], b: Conditional[AST]) => {
        val str = 
         "if (php_cond(\"" + f.toString.replace("\"", "'") + "\")) {\n" +
        	printCond(a, f :: list_feature_expr, 1) + "\n" +
         "}\n" +
         "else {\n" +
         	printCond(b, f.not :: list_feature_expr, 1) + "\n" +
         "}"
         	
         padSpaces(str, nestingLevel)
      }
      
      case e => assert(assertion = false, message = "match not exhaustive: " + e); ""
    }
  }
  
  def printOpt(ast: Opt[AST], list_feature_expr: List[FeatureExpr], nestingLevel: Int): String = {
     if (ast.feature == FeatureExprFactory.True ||
            list_feature_expr.foldLeft(FeatureExprFactory.True)(_ and _).implies(ast.feature).isTautology())
            print(ast.entry, list_feature_expr, nestingLevel)
     else {
       val str =
    	 "if (php_cond(\"" + ast.feature.toString.replace("\"", "'") + "\")) {\n" +
    	 	print(ast.entry, list_feature_expr, 1) + "\n" +
    	 "}"
    	
    	padSpaces(str, nestingLevel)
     }
  }
  
  def print(ast: AST, list_feature_expr: List[FeatureExpr], nestingLevel: Int): String = {
    val str = new StringBuilder
    
    ast match {
      case JSProgram(sourceElements) => {
        for (e <- sourceElements)
          str ++= printOpt(e, list_feature_expr, 0) + "\n"
      }
       
      case JSFunctionDeclaration(name, param, funBody) => {
        str ++= "function " + print(name, list_feature_expr, 0) + "(params)" + " {\n" + 
        			print(funBody, list_feature_expr, 1) +
        		"\n}"
      }
      
      case JSIdentifier(name) => {
        str ++= name
      }
      
      case JSExprStatement(expr) => {
        str ++= print(expr, list_feature_expr, 0) + ";"
      }
      
      case JSOtherStatement() => {
        str ++= "JSOtherStatement" + ";"
      }
      
      case JSAssignment(e1, op, e2) => {
        str ++= print(e1, list_feature_expr, 0) + " " + op + " " + print(e2, list_feature_expr, 0)
      }
      
      case JSFunctionCall(target, arguments) => {
//        str ++= print(target, list_feature_expr, 0)
  
        str ++= print(target, list_feature_expr, 0) + "("
        for (e <- arguments) {
          str ++= print(e, list_feature_expr, 0) + ", "
      	}
        if (!arguments.isEmpty)
        	str.delete(str.length - 2, str.length)
        str ++= ")"
      }
      
      case JSVariableStatement(s) => {
        str ++= "var "
        for (e <- s)
          str ++= printOpt(e, list_feature_expr, 0) + ", "
        if (!s.isEmpty)
        	str.delete(str.length - 2, str.length)
        str ++= ";"
      }
      
      case JSVariableDeclaration(name, init) => {
        str ++= print(name, list_feature_expr, 0)
        if (init.isDefined)
          str ++= " = " + print(init.get, list_feature_expr, 0)
      }
      
      case JSIfStatement(e, s1, s2) => {
        str ++= "if (" + print(e, list_feature_expr, 0) + ")\n" +
        			print(s1, list_feature_expr, 1) + 
        		(if (s2.isDefined) 
        		  ("\nelse\n" +
        		  	print(s2.get, list_feature_expr, 1))
        		 else "")
      }
      
      case JSBinaryOp(e1, op, e2) => {
        str ++= print(e1, list_feature_expr, 0) + " " + op + " " + print(e2, list_feature_expr, 0)
      }
      
      case JSLit(n) => {
        str ++= n
      }
      
      case JSBlock(sourceElements) => {
        str ++= "{\n"
        for (e <- sourceElements)
          str ++= printOpt(e, list_feature_expr, nestingLevel: Int) + "\n"
        str ++= "}"
      }
      
      case JSForStatement(statement) => {
        str ++= "for (;;)\n" +
        			print(statement, list_feature_expr, 1)
      }
      
      case JSExpr() => {
        str ++= "JSExpr"
      }
        
      case JSUnaryExpr(e, op) => {
        str ++= op + print(e, list_feature_expr, 0)
      }
    
      case JSPostfixExpr(e, op) => {
        str ++= print(e, list_feature_expr, 0) + op
      }
            
      case _ => {
        str ++= "AST:" + ast.getClass()
      }
      
      case e => assert(assertion = false, message = "match not exhaustive: " + e); ""
    }
    
    padSpaces(str.toString, nestingLevel)
  }
  
  def padSpaces(str: String, nestingLevel: Int): String = {
    if (str.isEmpty())
      return str;
    
    var tabs = ""
    for (i <- 1 to nestingLevel)
      tabs = tabs + "\t"
      
    var newStr = tabs + str.replace("\n", "\n" + tabs);
    
    if (newStr.endsWith("\n" + tabs))
      newStr = newStr.substring(0, newStr.length() - tabs.length())
      
    return newStr
  }

}