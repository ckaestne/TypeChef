/**
 * 
 */
package de.fosd.typechef.parser

import scala.collection.mutable.ArrayStack
import scala.collection.mutable.Stack
import scala.util.parsing.input._
import scala.util.parsing.combinator.Parsers
import de.fosd.typechef.featureexpr.FeatureExpr

/**
 * Continuation-based solution to parsing partially-preprocessed C code, 
 * and more in general to parse an ambiguous input stream (as opposed to an input stream
 * with an ambiguous parse tree).
 * 
 * @author Paolo G. Giarrusso
 *
 * XXX drop the inheritance relationship, it's a bad idea.
 */
class MultiFeatureContParsers extends Parsers {
	abstract class PreprocDirective
	case class Ifdef(f: FeatureExpr) extends PreprocDirective
	//XXX add the others.
	

	type Elem <: PreprocDirective
	override
	type Input = Reader[Elem]
	
	class ParsingContext {
		val contStack = new ArrayStack[MultiParser[_]]
		def pushNext(next: => MultiParser[_]) {
			contStack.push(next)
		}

	}
	object ThreadParsingContext {
		//XXX make this thread-local
		private val ctx = new ParsingContext() 
		def getContext = ctx
	}
	abstract class MultiParser[+T] extends Parser {
		//XXX: alternatively, move the imported object to a companion
		//object.
		import MultiFeatureContParsers.this.type.ThreadParsingContext

		sealed abstract class StateFork {
		}

		case class ParserStateFork[U >: T](cont: MultiParser[U]) extends StateFork
		case class StacksStateFork[U >: T](cont: MultiParser[U], states: List[_]) extends StateFork

		//def ~ [U](p: => Parser[U]): Parser[~[T, U]] = (for(a <- this; b <- p) yield new ~(a,b)).named("~")
		def baseParserSeq [U](p: => MultiParser[U]): Parser[~[T, U]] =
			(for(a <- this; b <- p) yield new ~(a,b)) //.named("~")*/
		
		// XXX: this should be part of a companion Object.
		//This needs to be invoked on the resulting parser to accept #ifdef also at the beginning.
		//All other positions are handled by ~  
		def invoke[U >: T](next: => MultiParser[U]): Parser[U] = {
			//accept("#ifdef") //Or #if
			// baseParserSeq reset next
			(acceptIf(x => x match {
				case Ifdef(_) => true
				case _ => false
			})(_ => "No ifdef found") ~> next).asInstanceOf[Parser[T]] |
			    //{ThreadParsingContext.getContext.pushNext(next); reset next}
			next
			//do the same handling as below for #if: push next somewhere.
		}
	}
}
