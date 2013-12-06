package edu.iastate.hungnv.test

import java.io._
import de.fosd.typechef.conditional._
import de.fosd.typechef.error._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._
import de.fosd.typechef.parser.common._
import de.fosd.typechef.parser.html._
import de.fosd.typechef.parser.javascript._
import edu.iastate.hungnv.test.Util._

/**
 * @author HUNG
 */
object HtmlTransformer {
  
  type ElementList = List[Opt[HElement]]
  
  def transform(r: Reader): Unit = {
    
    
	/*
     * Step 1: SAX tokens
     */
    val tokenReader = CharacterLexer.lex(r)
    val tokens = tokenReader.tokens.slice(0, tokenReader.tokens.size)
    
    log("1. SAX tokens:")
    log(prettyPrintSaxTokens(tokens))
    log()
 
    //java.lang.System.exit(0)
    
    /*
     * Step 2: SAX sequence
     */
    val p = new HTMLSAXParser
    var saxResult = p.HtmlSequence(tokenReader,FeatureExprFactory.True)

//    log(saxResult.toString)
    
    def printResult(f: FeatureExpr, x: p.MultiParseResult[ElementList]) :Unit= x match {
      case p.Success(y,_) => println(f+": \n"+y.size)//y.take(4))
      case p.Failure(y,_,_) => println("failed["+y.size+"]") //+y.take(4))
      case p.SplittedParseResult(fx, x, y) =>
        printResult(f and fx, x)
        printResult(f andNot fx , y)
    }
    try {
    	printResult(FeatureExprFactory.True, saxResult)
    } catch {
      case e: Throwable => log("Error: " + e.getClass().toString()); e.printStackTrace()
    }
    
    
    // Take the longest SplittedParser Result
    def getLongestSuccessResult(f: FeatureExpr, x: p.MultiParseResult[List[Opt[HElement]]]): Option[p.Success[ElementList]] = x match {
      case e:p.Success[ElementList] => Some(e)
      case p.Failure(y,_,_) => None
      case p.SplittedParseResult(fx, x, y) => {
        var firstSuccessResult = getLongestSuccessResult(f and fx, x)
        var secondSuccessResult = getLongestSuccessResult(f andNot fx, y)
        if (!firstSuccessResult.isDefined) return secondSuccessResult
        if (!secondSuccessResult.isDefined) return firstSuccessResult
        if (firstSuccessResult.get.result.size > secondSuccessResult.get.result.size)
          firstSuccessResult
        else
          secondSuccessResult
      }
    }
    
    var success = getLongestSuccessResult(FeatureExprFactory.True, saxResult)
    
    //java.lang.System.exit(0)
    
    //var success = saxResult.asInstanceOf[p.Success[List[de.fosd.typechef.conditional.Opt[de.fosd.typechef.parser.html.HElement]]]]
    var saxSequence = success.get.result
      
    println("2. SAX sequence:")
    println(prettyPrintSaxSequence(saxSequence))
    println()

    /*
     * Step 3: DOM tokens
     */
    var domTokens = List[HElementToken]()
//    saxResult match {
//        case p.Success(r, rest) => domTokens = r.map(t=>new HElementToken(t))
//        case x => println("parsing problem: "+x)
//    }
//    
    println("3. DOM tokens:")
    println(prettyPrintDomTokens(domTokens))
    println()

    /*
     * Step 4: DOM tree
     */
    val p2 = new HTMLDomParser
    val tokenStream = new TokenReader[HElementToken, Null](domTokens, 0, null, new HElementToken(Opt(FeatureExprFactory.True,HText(List()))))
    val dom = p2.Element(tokenStream,FeatureExprFactory.True)
    
    log("Parse result: " + standardize(dom.toString), true)
    
    dom match {
      case x: p2.Success[_] => { log("Good")}
      case p2.Error(_, next, _) => { log(next.first.getPosition.toString) }
      case _ => {}
    }
    
    if (!dom.isInstanceOf[p.Success[DNode]])
      return;
    
    var s = dom.asInstanceOf[p.Success[DNode]]
    var rootNode = s.result
    
    println("4. DOM tree:")
    println(prettyPrintDNode(rootNode, 0))
    println()
    
    var doc = PrettyPrinter.prettyPrint(rootNode)
    var layout = PrettyPrinter.layout(doc)
    log("PrettyPrinter:")
    log(layout, true)
    log()
    
    log(applyDElement(rootNode, FeatureExprFactory.True, printHtml))
    log()
    
    /*
     * Step 5: Transform HTML
     */
    var newRootNode = applyDElement(rootNode, FeatureExprFactory.True, transformHtml)
    log("5. Transformed HTML:")
    log(applyDElement(newRootNode, FeatureExprFactory.True, printHtml))
    log();
    
    /*
     * Step 6: Transform JS
     */
    newRootNode = applyDElement(newRootNode, FeatureExprFactory.True, traverseHTMLandTransformJS)
    log("6. Transformed HTML/JS:")
    log(applyDElement(newRootNode, FeatureExprFactory.True, printHtml))
    log()
  }
  
  /*
   * Utility methods
   */
  
    /*
     * SAX tokens
     */
    def prettyPrintSaxTokens(r: List[CharacterToken]): String = {
        val out = new StringBuilder
       
        var currFeat: FeatureExpr = FeatureExprFactory.True
        for (tok <- r.takeRight(20)) {
            var newFeat: FeatureExpr = tok.getFeature
            if (newFeat != currFeat) {
                out ++= "\n[PC = " + newFeat.toString + "] "
                currFeat = newFeat
            }
            out ++= Util.standardize(tok.getText);
            out ++= " "
        }

        out.toString
    }
    
    /*
     * SAX sequence
     */
    def prettyPrintSaxSequence(r: List[de.fosd.typechef.conditional.Opt[de.fosd.typechef.parser.html.HElement]]): String = {
      val out = new StringBuilder
      
      for (e <- r) {
        out ++= Util.standardize(e.toString) + "\n"
      }
      
      out.toString
    }

    /*
     * DOM tokens
     */
    def prettyPrintDomTokens(l: List[de.fosd.typechef.parser.html.HElementToken]): String = {
      val out = new StringBuilder
      
      for (e <- l) {
        out ++= Util.standardize(e.toString) + "\n"
      }
      
      out.toString 
    }
    
    /*
     * DOM tree
     */
    def prettyPrintDElement(d: DElement, depth: Int): String = {
      d match {
        case n: DNode => prettyPrintDNode(n, depth)
        case t: DText => prettyPrintDText(t, depth)
      } 
    }
    
    def prettyPrintDNode(n: DNode, depth: Int): String = {
      val out = new StringBuilder
      
      out ++= Util.padding(depth) + "Node <" + n.name + ">\n"
      //out ++= Util.padding(depth) + n.attributes + "\n"
      
      if (!n.attributes.isEmpty) {
    	  out ++= Util.padding(depth + 1) + "Attributes\n"
    	  for (a <- n.attributes) {
    		 a match {
    		   case Opt(f, HAttribute(name, Some(value)) ) => {
    		     out ++= Util.padding(depth + 2) + "Opt(" + f + ")\n"
    		     out ++= Util.padding(depth + 3) + name + " = " + value + "\n" 
    		   }
    		 }
    	  }
      }
      
      for (c <- n.children) {
        c match {
        	case Opt(f, e) => {
        	  out ++= Util.padding(depth + 1) + "Opt(" + f + ")\n"
        	  out ++= prettyPrintDElement(e, depth + 2)
        	}
        }
      }
        
      out.toString
    }
    
    def prettyPrintDText(n: DText, depth: Int): String = {
      val out = new StringBuilder
    
      out ++= Util.padding(depth) + "Text"
      
      var currFeat: FeatureExpr = null
      
      for (optToken <- n.value) {
        optToken match {
         	case Opt(f, e) => {
         	  if (f != currFeat) {
         		  out ++= "\n" + Util.padding(depth + 1) + "Opt(" + f + ")\n"
         		  out ++= Util.padding(depth + 2)
         		  currFeat = f;
         	  }
         	  out ++= Util.standardize(e.getText()) + " "
         	}
         }
       }
      out ++= "\n"
      
      out.toString
    }
    
    def prettyPrintCharacterToken(n: CharacterToken, depth: Int): String = {
      Util.padding(depth) + Util.standardize(n.getText) + " (" + n.getFeature() +  ")\n"
    }
    
    def applyDElement[T](e: DElement, f: FeatureExpr, m: (DElement, FeatureExpr) => T): T = {
      e match {
        case n: DNode => m(n, f)
        case t: DText => m(t, f)
      }
    }
    
    /*
     * Print HTML
     */
    def printHtml(e: DElement, feature: FeatureExpr): String = {
      e match {
        case n: DNode => {
        	val str = new StringBuilder()
        	str ++= "<" + n.name

        	for (attr <- n.attributes) {
		      attr match {
		        case Opt(f, HAttribute(name, Some(value))) => str ++= " " + name + "=\"" + value + "\""
		      }
		    }
        	
        	str ++= ">"
        	
        	for (child <- n.children) {
		      child match {
		        case Opt(f, e) => str ++= applyDElement(e, f, printHtml)
		      }
		    }
        	str ++= "</" + n.name + ">"
        	str.toString
        }
        case t: DText => {
          val str = new StringBuilder()
          val list = t.value.map(t => t match {case Opt(f, e) => e})
//          str ++= prettyPrintSaxTokens(list)
          for (c <- list)
            str ++= c.getText
          //str ++= "\n"
          str.toString
        }
      }
    }
    
    /*
     * Transform HTML
     */
    def transformHtml(e: DElement, feature: FeatureExpr): DElement = {
      e match {
        case n: DNode => {
        	val str = new StringBuilder()
    
	    	val a = HAttribute("cond", Some(feature.toString))
	    	val aList = n.attributes ::: List(Opt(FeatureExprFactory.True, a))
	    	
	    	var newChildren = List[Opt[DElement]]()
	    	for (child <- n.children) {
		      child match {
		        case Opt(f1, e1) => newChildren = List(Opt(f1, applyDElement(e1, f1, transformHtml))) ::: newChildren
		      }
		    }
        	
        	new DNode(n.name, aList, newChildren.reverse)
        }
        case t: DText => {
        	t
        }
      }
    }
    
    /*
     * Traverse HTML and transform JS
     */
    def traverseHTMLandTransformJS(e: DElement, feature: FeatureExpr): DElement = {
      e match {
        case n: DNode => {
        	val str = new StringBuilder()
        	
        	
        	var newAttributes = List[Opt[HAttribute]]()
	    	for (child <- n.attributes) {
	    		child match {
			    	case Opt(f1, a1) => {
			    	  var newAttr = a1
			    	  if (a1.name.startsWith("on")) {
			    	    val newValue = a1.value match {
			    	      case Some(s) => JSTransformer.transform(new StringReader(s))
			    	    }
			    	    newAttr = new HAttribute(a1.name, Some(newValue))
			    	  }
			    	  newAttributes = Opt(FeatureExprFactory.True, newAttr) :: newAttributes
			    	}
			    }
			}
        	
        	
	    	var newChildren = List[Opt[DElement]]()
	    	
	    	if (n.name.equals("script")) {
	    		val s = n.children match {
	    		  case List(Opt(_, DText(v))) => {
	    		    JSTransformer.transform(v.map(el => el match {case Opt(f, e) => e}))
	    		  }
	    		  case List(Opt(_, _), Opt(_, DText(v)), Opt(_, _)) => {
	    		    JSTransformer.transform(v.map(el => el match {case Opt(f, e) => e}))
	    		  }
	    		  case _ => "Error"
	    		}
	    		
	    		var l = List[Opt[CharacterToken]]()
	    		for (c <- s) {
	    		  l = Opt(FeatureExprFactory.True, new CharacterToken(c, FeatureExprFactory.True, NoPosition)) :: l
	    		}
	    		
		    	val text = new DText(l.reverse)
		    	newChildren = List(Opt(FeatureExprFactory.True, text))
	    	}
	    	else { 
		    	for (child <- n.children) {
			      child match {
			        case Opt(f1, e1) => newChildren = List(Opt(f1, applyDElement(e1, f1, traverseHTMLandTransformJS))) ::: newChildren
			      }
			    }
	    	}
        	
        	new DNode(n.name, newAttributes, newChildren.reverse)
        }
        case t: DText => {
        	t
        }
      }
    }
            
}