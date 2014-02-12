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
		for (ele <- elementList) {
			ele match {
			  	case Opt(f, x) => {run(x)}
			}
        } 
	}
	
	def run(element: DElement): Unit = {
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
		  			
		  			detectCSSClasses(cssResult)
		  		}

		  		run(children)
		  	}
		  	case DText(tokens) => {}
		}
	}
	
	def detectCSSClasses(styleSheet: CStyleSheet): Unit = {
		for (r <- styleSheet.ruleSets) {
		  for (s <- r.entry.selectors) {
		    log(s.name + " @ " + s.getFile + " " + s.getPositionFrom + " " + s.getPositionTo)
		  }
		}
	}

}