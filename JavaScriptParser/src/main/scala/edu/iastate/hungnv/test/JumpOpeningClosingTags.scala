package edu.iastate.hungnv.test

import de.fosd.typechef.conditional._
import de.fosd.typechef.parser.html._
import edu.iastate.hungnv.test.Util._
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory}

/**
 * @author HUNG
 */
object JumpOpeningClosingTags {
  
	def run(elementList: List[Opt[DElement]], list_feature_expr: List[FeatureExpr] = List(FeatureExprFactory.True)): Unit = {
		for (ele <- elementList) {
			ele match {
			  	case Opt(f, x) => {run(x, f :: list_feature_expr)}
			}
        } 
	}
	
	def run(element: DElement, list_feature_expr: List[FeatureExpr]): Unit = {
		element match {
		  	case DNode(name, attributes, children, openTag, closingTag) => {
		  		log(list_feature_expr + "Name: " + name.name + " | Open: " + openTag.getPositionFrom + " | Close: " + closingTag.getPositionFrom)
		  		
		  		Main.tagsFile.write("Name: " + name.name + " | Open: " + openTag.getPositionFrom + " | Close: " + closingTag.getPositionFrom + "\n")
		  		
		  		run(children, list_feature_expr)
		  	}
		  	case DText(tokens) => {}
		}
	}

}