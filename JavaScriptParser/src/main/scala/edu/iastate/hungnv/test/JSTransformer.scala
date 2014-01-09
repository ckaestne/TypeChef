package edu.iastate.hungnv.test

import java.io._
import de.fosd.typechef.conditional._
import de.fosd.typechef.error._
import de.fosd.typechef.featureexpr._
import de.fosd.typechef.parser._
import de.fosd.typechef.parser.common._
import de.fosd.typechef.parser.javascript._
import edu.iastate.hungnv.test.Util._

/**
 * @author HUNG
 */
object JSTransformer {
  
	def transform(r: Reader): String = {
		val tokenReader = CharacterLexer.lex(r)
		transform(tokenReader)
	}
	
	def transform(tokens: List[CharacterToken]): String = {
		val tokenReader = new TokenReader[CharacterToken, Null](tokens, 0, null, new CharacterToken(-1, FeatureExprFactory.True, new JPosition("", -1, -1)))
		transform(tokenReader)
	}
	
	def transform(tokenReader: TokenReader[CharacterToken, Null]): String = {
		val parser = new JSParser()
        val result = parser.phrase(parser.Program)(tokenReader, FeatureExprFactory.True)
        log(result.toString)
        
        val jsTree = result match {
		  case parser.Success(r, rest) => printJS(r, FeatureExprFactory.True, 0)
		}
        log(jsTree)
        
        result match {
		  case parser.Success(r, rest) => "\n" + transformJS(r, FeatureExprFactory.True, 0)
		}
	}
	
	def printJS(r: Any, f: FeatureExpr, depth: Int): String = {
	  val str = new StringBuilder
	  r match {
	    case Choice(feat, thenBranch, elseBranch) =>  {
	      str ++= Util.padding(depth) + "Choice(" + feat.toString + ")\n"
	      str ++= printJS(thenBranch, feat, depth + 1)
	      str ++= printJS(elseBranch, feat, depth + 1)
	    }
	    case One(v) => {
	      str ++= printJS(v, f, depth)
	    }
	    case Opt(feat, v) => {
	      str ++= Util.padding(depth) + "Opt(" + feat.toString + ")\n"
	      str ++= printJS(v, feat, depth + 1)
	    }
	    case JSProgram(l) => {
	      str ++= Util.padding(depth) + "JSProgram\n"
	      for (e <- l) {
	        str ++= printJS(e, f, depth + 1)
	      }
	    }
	    case JSFunctionDeclaration(name, params, funcBody) => {
	      str ++= Util.padding(depth) + "JSFunctionDeclaration\n"
	      str ++= Util.padding(depth + 1) + "Name: " + name.name + "\n"
	      str ++= Util.padding(depth + 1) + "Params: " + params + "\n"
	      str ++= Util.padding(depth + 1) + "Body:\n"
	      str ++= printJS(funcBody, f, depth + 2)
	    }
	    case JSExprStatement(s) => {
	      str ++= Util.padding(depth) + "JSExprStatement\n"
	      str ++= printJS(s, f, depth + 1)
	    }
	    case JSFunctionCall(target, arguments) => {
	      str ++= Util.padding(depth) + "JSFunctionCall\n"
	      str ++= Util.padding(depth + 1) + "Target: " + target + "\n"
	      str ++= Util.padding(depth + 1) + "Arguments: " + arguments + "\n"
	    }
	    case JSAssignment(e1, op, e2) => {
	      str ++= Util.padding(depth) + "JSAssignment\n"
	      str ++= Util.padding(depth + 1) + "LHS: " + e1 + "\n"
	      str ++= Util.padding(depth + 1) + "Op: " + op + "\n"
	      str ++= Util.padding(depth + 1) + "RHS: " + e2 + "\n"
	    }
	    case JSBlock(list) => {
	      str ++= Util.padding(depth) + "JSBlock\n"
	      for (e <- list) {
	        str ++= printJS(e, f, depth + 1)
	      }
	    }
	    case JSIfStatement(e, s1, s2) => {
	      str ++= Util.padding(depth) + "JSIfStatement\n"
	      str ++= Util.padding(depth + 1) + "Expression\n"
	      str ++= printJS(e, f, depth + 2)
	      str ++= Util.padding(depth + 1) + "Then Statement\n"
	      str ++= printJS(s1, f, depth + 2)
	      s2 match {
	        case Some(stmt2) => {
	          str ++= Util.padding(depth + 1) + "Else Statement\n"
	          str ++= printJS(stmt2, f, depth + 2)
	        }
	        case _ => {}
	      }
	    }
	    case ast: AST => {
	      str ++= Util.padding(depth) + ast + "\n"
	    }
	    case _ => {
	      str ++= Util.padding(depth) + "Unknown type\n"
	    }
	  }
	  str.toString
	}
	
	def transformJS(r: Any, f: FeatureExpr, depth: Int): String = {
	  val str = new StringBuilder
	  r match {
	    case Choice(feat, thenBranch, elseBranch) =>  {
	      str ++= Util.padding(depth, "  ") + "if (" + feat.toString + ") {\n"
	      str ++= transformJS(thenBranch, feat, depth + 1)
	      str ++= Util.padding(depth, "  ") + "}\n"
	      str ++= Util.padding(depth, "  ") + "else {\n"
	      str ++= transformJS(elseBranch, feat, depth + 1)
	      str ++= Util.padding(depth, "  ") + "}\n"
	    }
	    case One(v) => {
	      str ++= transformJS(v, f, depth)
	    }
	    case Opt(feat, v) => {
	      if (feat.isTautology)
	        str ++= transformJS(v, feat, depth)
	      else {
	    	  str ++= Util.padding(depth, "  ") + "if (" + feat.toString + ") {\n"
	    	  str ++= transformJS(v, feat, depth + 1)
	    	  str ++= Util.padding(depth, "  ") + "}\n"
	      }
	    }
	    case JSProgram(l) => {
	      for (e <- l) {
	        str ++= transformJS(e, f, depth)
	      }
	    }
	    case JSFunctionDeclaration(name, params, funcBody) => {
	      str ++= Util.padding(depth, "  ") + "function " + name.name + "("
	      params match {
	        case Nil => {
	        }
	        case l: List[JSIdentifier] => {
	          for (arg <- l)
	        		str ++= arg.name + " "
	        }
	        case l: List[String] => {
	        	for (arg <- l)
	        		str ++= arg + " "
	        }
	        case _ => {
	          str ++= params.toString
	        }
	      }
	      str ++= "){\n"
	      str ++= transformJS(funcBody, f, depth + 2)
	      str ++= Util.padding(depth, "  ") + "}\n"
	    }
	   case JSExprStatement(s) => {
	      str ++= transformJS(s, f, depth)
	    }
	    case JSFunctionCall(target, arguments) => {
	      str ++= Util.padding(depth) + target.asInstanceOf[JSIdentifier].name + "("
	      for (arg <- arguments)
	        str ++= arg + " "
	      str ++= ")\n"
	    }
	    case _ => {
	      str ++= Util.padding(depth) + "AST\n"
	    }
	  }
	  str.toString
	}

}