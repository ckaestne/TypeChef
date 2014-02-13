package de.fosd.typechef.parser.common

import de.fosd.typechef.featureexpr.{ FeatureExprFactory, FeatureExpr }
import de.fosd.typechef.error.NoPosition
import de.fosd.typechef.parser.TokenReader
import java.util
import java.io.Reader
import scala.xml.NodeSeq
import scala.xml.Node
import scala.xml.Elem
import scala.xml.Text

/**
 * this "lexer" chunks a file into character tokens. it will later resolve special characters
 * and it recognizes ifdefs
 */
object CharacterLexer {

  def lex(r: Reader): TokenReader[CharacterToken, Null] = {

    var _tokens = List[CharacterToken]()
    var result = List[CharacterToken]()
    var fexprCombinedStack = new util.Stack[FeatureExpr]()
    var fexprStack = new util.Stack[FeatureExpr]()
    fexprStack.push(FeatureExprFactory.True)
    fexprCombinedStack.push(FeatureExprFactory.True)

    var line = 0
    var col = -1

    var c = r.read()
    while (c >= 0) {
      col += 1
      if (c == '\n') {
        line += 1
        col = 0
      }

      _tokens = new CharacterToken(c, FeatureExprFactory.True, new JPosition("", line, col)) :: _tokens

      c = r.read()
    }
    val tokens = _tokens.reverse.toArray

    var i = 0
    while (i < tokens.size) {
      var intWrapper = Array[Int](i)
      if (checkMacro(tokens, intWrapper, "\n#if")) {
        var featureName = readFeatureName(tokens, intWrapper)

        var expr: FeatureExpr = FeatureExprFactory.createDefinedExternal(featureName.toString)
        fexprStack.push(expr)
        fexprCombinedStack.push(fexprCombinedStack.peek() and expr)

        i = intWrapper(0);
      } else if (checkMacro(tokens, intWrapper, "\n#else\n")) {
        fexprStack.push(fexprStack.pop() not)
        fexprCombinedStack.pop()
        fexprCombinedStack.push(fexprCombinedStack.peek() and fexprStack.peek())

        i = intWrapper(0);
      } else if (checkMacro(tokens, intWrapper, "\n#endif\n")) {
        if (fexprStack.size() == 0)
          System.err.println("too many __endif")
        else {
          fexprStack.pop()
          fexprCombinedStack.pop()
        }
        i = intWrapper(0);
      } else {
        result = tokens(i).and(fexprCombinedStack.peek()) :: result
        i += 1
      }
    }

    new TokenReader[CharacterToken, Null](result.reverse, 0, null, new CharacterToken(-1, FeatureExprFactory.True, new JPosition("", -1, -1)))

  }

  def checkMacro(tokens: Array[CharacterToken], position: Array[Int], macro: String): Boolean = {
    var pos = position(0)
    if (pos + macro.length() - 1 >= tokens.size)
      return false;

    var i = 0;
    for (i <- 0 to macro.length - 1)
      if (tokens(pos + i).getKind() != macro.charAt(i))
        return false;

    position(0) += macro.length()
    return true;
  }

  def readFeatureName(tokens: Array[CharacterToken], position: Array[Int]): String = {
    var pos = position(0)
    var i = pos;
    while (tokens(i).getKind() != '(')
      i += 1

    var openParens = 1;
    i += 1
    var featureName = new StringBuffer();

    var ok = true;
    while (ok) {
      if (tokens(i).getKind() == '(')
        openParens += 1
      if (tokens(i).getKind() == ')')
        openParens -= 1

      if (openParens == 0)
        ok = false
      else
        featureName.append(tokens(i))

      i += 1
    }

    position(0) = i + 1; // Eat the \n character after the closing parenthesis
    return featureName.toString;
  }
  
  
  
  
  
  def visitString(str: String, file: String, position: Int, fexprCombinedStack: util.Stack[FeatureExpr], fexprStack: util.Stack[FeatureExpr], tokens: scala.collection.mutable.MutableList[CharacterToken]): Unit = {
    val r = new java.io.StringReader(str)
    
    var c = r.read()
    var pos = position
    while (c >= 0) {
    	tokens.+=:(new CharacterToken(c, fexprCombinedStack.peek(), new JPosition(file, -1, pos)))
    	
    	c = r.read()
    	pos += 1
    }
  }
  
  def visitNode(node: Any, fexprCombinedStack: util.Stack[FeatureExpr], fexprStack: util.Stack[FeatureExpr], tokens: scala.collection.mutable.MutableList[CharacterToken]): Unit = {
    node match {
      case x: Text => {
        // Do nothing
      }
      
      case x: Elem => {
//        println("Visiting " + x.getClass() + " Label: " + x.label)
        if (x.label == "Select") {
        	var featureName = (x \ "@StringValue").text
        	if (featureName.isEmpty())
        	  featureName = "[Unresolved Constraint]"
        	
        	// Visit true branch
     		var expr: FeatureExpr = FeatureExprFactory.createDefinedExternal(featureName)
     		fexprStack.push(expr)
     		fexprCombinedStack.push(fexprCombinedStack.peek() and expr)
     		if ((x \ "True").size > 0)
     			visitNode((x \ "True").head.child, fexprCombinedStack, fexprStack, tokens)
     		
     		// Visit false branch
     		fexprStack.push(fexprStack.pop() not)
     		fexprCombinedStack.pop()
     		fexprCombinedStack.push(fexprCombinedStack.peek() and fexprStack.peek())
     		if ((x \ "False").size > 0)
     			visitNode((x \ "False").head.child, fexprCombinedStack, fexprStack, tokens)
     		
     		// Exit branches
     		fexprStack.pop()
     		fexprCombinedStack.pop()
        }
        else if (x.label == "Concat") {
          visitNode(x.child, fexprCombinedStack, fexprStack, tokens)
        }
        else if (x.label == "Repeat") {
          visitNode(x.child, fexprCombinedStack, fexprStack, tokens)
        }
        else if (x.label == "Symbolic") {
          visitString("SYM", (x \ "@File").text, (x \ "@Position").text.toInt, fexprCombinedStack, fexprStack, tokens)
        }
        else if (x.label == "Literal") {
          val str = (x \ "@StringValue")
//          println("Literal: " + str.text)
          visitString(str.text, (x \ "@File").text, (x \ "@Position").text.toInt, fexprCombinedStack, fexprStack, tokens)
          // TODO: Set location in XML file
          //visitString(str.text, (x \ "@File").text, x., fexprCombinedStack, fexprStack, tokens)
        }
        else {
          println("Error in CharacterLexer.visitNode. Can't recognize label: " + x.label)
          java.lang.System.exit(0)
        }
      }
      
      case x: NodeSeq => {
//        println("Visiting " + x)
        for (ele <- x)
          visitNode(ele, fexprCombinedStack, fexprStack, tokens)
      }
      
      case x:  Seq[Node] => {
        for (ele <- x)
        	visitNode(ele, fexprCombinedStack, fexprStack, tokens)
      }
      
      case _ => {
        println("Error in CharacterLexer.visitNode.")
        java.lang.System.exit(0)
      }
    }
  }
  
  def lexForXml(r: Reader): TokenReader[CharacterToken, Null] = {
	val dmodel = scala.xml.XML.load(r);
	
    var fexprCombinedStack = new util.Stack[FeatureExpr]()
    var fexprStack = new util.Stack[FeatureExpr]()
    fexprStack.push(FeatureExprFactory.True)
    fexprCombinedStack.push(FeatureExprFactory.True)
    
    var tokens = scala.collection.mutable.MutableList[CharacterToken]()
    
    visitNode(dmodel.child, fexprCombinedStack, fexprStack, tokens)
    
    new TokenReader[CharacterToken, Null](tokens.reverse.toList, 0, null, new CharacterToken(-1, FeatureExprFactory.True, new JPosition("", -1, -1)))
  }

}

