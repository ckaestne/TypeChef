package edu.iastate.hungnv.test

import de.fosd.typechef.conditional._
import de.fosd.typechef.parser.html._
import edu.iastate.hungnv.test.Util._

/**
 * @author HUNG
 */
object JumpOpeningClosingTags {
  
	def run(elementList: List[Opt[DElement]]): Unit = {
		for (ele <- elementList) {
			ele match {
			  	case Opt(f, x) => {run(x)}
			}
        } 
	}
	
	def run(element: DElement): Unit = {
		element match {
		  	case DNode(name, attributes, children, openTag, closingTag) => {
		  		log("Name: " + name + " Open: " + openTag.getPositionFrom + " Close: " + closingTag.getPositionFrom)
		  		run(children)
		  	}
		  	case DText(tokens) => {}
		}
	}

}