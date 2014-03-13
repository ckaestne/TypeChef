package edu.iastate.hungnv.test

import de.fosd.typechef.featureexpr.{ FeatureExprFactory, FeatureExpr }
import de.fosd.typechef.parser.common.CharacterToken
import de.fosd.typechef.conditional._
import de.fosd.typechef.parser.html._
import edu.iastate.hungnv.parser.css._
import edu.iastate.hungnv.test.Util._

/**
 * @author HUNG
 */
object JumpCSSClasses {
  
	def run(elementList: List[Opt[DElement]]): Unit = {
		var cssSelectors = scala.collection.mutable.ListBuffer[CSelector]()
		
		detectCssSelectors(elementList, cssSelectors)
		
//		printCssSelectors(cssSelectors)
		
		detectSelectedHtmlElements(elementList, cssSelectors)
	}
	
	/*
	 * Detect CSS selectors
	 */
	
	def detectCssSelectors(elementList: List[Opt[DElement]], cssSelectors: scala.collection.mutable.ListBuffer[CSelector]): Unit = {
	  for (ele <- elementList) {
			ele match {
			  	case Opt(f, x) => {detectCssSelectors(x, cssSelectors)}
			}
        }
	}
	
	def detectCssSelectors(element: DElement, cssSelectors: scala.collection.mutable.ListBuffer[CSelector]): Unit = {
		element match {
		  	case DNode(name, attributes, children, openTag, closingTag) => {
		  		if (name.name.equals("style")) {
		  			var cssSource = List[Opt[CharacterToken]]()

		  			for (child <- children) {
		  				val text = child.entry.asInstanceOf[DText].value
		  				cssSource = cssSource ::: text 
		  			}
		  			
		  			var tokens: List[CharacterToken] = cssSource.map(optToken => optToken.entry)
          
		  			val cssResult = CSSParser.parse(tokens)
		  			
		  			detectCssSelectors(cssResult, cssSelectors)
		  		}

		  		detectCssSelectors(children, cssSelectors)
		  	}
		  	case DText(tokens) => {}
		}
	}
	
	def detectCssSelectors(styleSheet: CStyleSheet, cssSelectors: scala.collection.mutable.ListBuffer[CSelector]): Unit = {
		for (r <- styleSheet.ruleSets) {
		  for (s <- r.entry.selectors) {
		    cssSelectors += s
		  }
		}
	}
	
	/*
	 * Print CSS selectors
	 */
	
	def printCssSelectors(cssSelectors: scala.collection.mutable.ListBuffer[CSelector]): Unit = {
		for (s <- cssSelectors) {
		  log(getDescription(s) + " @ " + s.getPositionFrom)
		}
	}
	
	def getDescription(s: CSelector): String  = {
	  s match {
	      case s1:CSimpleSelector => s1.name.name + (if (s1.nestedSelector.isDefined) getDescription(s1.nestedSelector.get) else "")
	      case s2:CClassSelector => "." + s2.name.name
	      case s3:CHashSelector => "#" + s3.name.name
	      case _ => "OtherSelector"
	  }
	}
	
	/*
	 * Detect selected HTML elements
	 */

	def detectSelectedHtmlElements(elementList: List[Opt[DElement]], cssSelectors: scala.collection.mutable.ListBuffer[CSelector]): Unit = {
	  for (s <- cssSelectors) {
		  log(getDescription(s) + " @ " + s.getPositionFrom)
	  
		  var selectedHtmlEles = scala.collection.mutable.ListBuffer[DNode]()
		  detectSelectedHtmlElements(elementList, s, selectedHtmlEles)
		  
		  // Retain unique nodes only
		  var uniqueEles = scala.collection.mutable.SortedSet[String]()
		  for (dNode <- selectedHtmlEles) {
		    val str = "\t" + dNode.name.name + " @ " + dNode.openTag.getPositionFrom
		    if (!uniqueEles.contains(str))
		      uniqueEles += str
		  }
		  
		  // Print unique nodes
		  for (ele <- uniqueEles)
			  log(ele)
      }
	}
	
	def detectSelectedHtmlElements(elementList: List[Opt[DElement]], cssSelector: CSelector, selectedHtmlEles: scala.collection.mutable.ListBuffer[DNode]): Unit = {	  
	  for (ele <- elementList) {
			ele match {
			  	case Opt(f, x) => {detectSelectedHtmlElements(x, cssSelector, selectedHtmlEles)}
			}
        }
	}
	
	def detectSelectedHtmlElements(element: DElement, cssSelector: CSelector, selectedHtmlEles: scala.collection.mutable.ListBuffer[DNode]): Unit = {
		element match {
		  	case dNode:DNode => {
		  		if (detectSelectedHtmlElements(dNode, cssSelector)) {
		  		  selectedHtmlEles += dNode
		  		}

		  		detectSelectedHtmlElements(dNode.children, cssSelector, selectedHtmlEles)
		  	}
		  	case DText(tokens) => {}
		}
	}
	
	def detectSelectedHtmlElements(dNode: DNode, cssSelector: CSelector): Boolean = {
	  cssSelector match {
	      case s1:CSimpleSelector => return detectSelectedHtmlElementsWithSimpleSelector(dNode, s1)
	      case s2:CClassSelector => return detectSelectedHtmlElementsWithClassSelector(dNode, s2)
	      case s3:CHashSelector => return detectSelectedHtmlElementsWithHashSelector(dNode, s3)
	      case s4:COtherSelector => return detectSelectedHtmlElementsWithOtherSelector(dNode, s4)
	      case _ => return false
	  }
	}
	
	def detectSelectedHtmlElementsWithSimpleSelector(dNode: DNode, cssSelector: CSimpleSelector): Boolean = {
	  if (cssSelector.nestedSelector.isDefined)
	    if (!detectSelectedHtmlElements(dNode, cssSelector.nestedSelector.get))
	      return false

	   return dNode.name.name.toLowerCase().equals(cssSelector.name.name.toLowerCase())
	}
	
	def detectSelectedHtmlElementsWithClassSelector(dNode: DNode, cssSelector: CClassSelector): Boolean = {
		return (dNode.attributes.exists(x => x.entry.name.name.equals("class")
		    && x.entry.value.getOrElse("").equals(cssSelector.name.name)))
	}
		
	def detectSelectedHtmlElementsWithHashSelector(dNode: DNode, cssSelector: CHashSelector): Boolean = {
	  return (dNode.attributes.exists(x => x.entry.name.name.equals("id")
	      && x.entry.value.getOrElse("").equals(cssSelector.name.name)))
	}
			
	def detectSelectedHtmlElementsWithOtherSelector(dNode: DNode, cssSelector: COtherSelector): Boolean = {
	  return false
	}
	
}