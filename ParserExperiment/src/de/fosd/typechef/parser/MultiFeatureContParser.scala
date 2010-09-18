package de.fosd.typechef.parser

import scala.util.continuations._
import scala.collection.mutable.ArrayStack
import scala.collection.mutable.Stack
import scala.util.parsing.input._
import de.fosd.typechef.featureexpr.FeatureExpr

//In this version, I use separate tokens for the preprocessing directives.
abstract class PreprocDirective
case class If(f: FeatureExpr) extends PreprocDirective {} //includes ifdef
case class Elif(f: FeatureExpr) extends PreprocDirective {}
case object Else extends PreprocDirective {}
case object Endif extends PreprocDirective {}

/**
 * Continuation-based solution to parsing partially-preprocessed C code, 
 * and more in general to parse an ambiguous input stream (as opposed to an input stream
 * with an ambiguous parse tree).
 * 
 * @author Paolo G. Giarrusso
 */

//class MultiFeatureContParsers /*extends Parsers*/ {
//  type Elem >: PreprocDirective
//  override type Input = Reader[Elem]
//
//  /** A base class for parser results. A result is either successful or not (failure may be fatal, i.e., an CpsError, or
//   * not, i.e., a CpsFailure). On success, provides a result of type `T` which consists of some result (and the rest of
//   * the input). */
//  sealed abstract class CpsParseResult[+T] {
//    /** Functional composition of ParseResults
//     * 
//     * @param `f' the function to be lifted over this result
//     * @return `f' applied to the result of this `CpsParseResult', packaged up as a new `CpsParseResult'
//     */
//    def map[U](f: T => U): CpsParseResult[U]
//    
//    /** Partial functional composition of ParseResults
//     * 
//     * @param `f' the partial function to be lifted over this result
//     * @param error a function that takes the same argument as `f' and produces an error message 
//     *        to explain why `f' wasn't applicable (it is called when this is the case)
//     * @return <i>if `f' f is defined at the result in this `CpsParseResult',</i>
//     *         `f' applied to the result of this `CpsParseResult', packaged up as a new `CpsParseResult'.
//     *         If `f' is not defined, `CpsFailure'.
//     */
//    def mapPartial[U](f: PartialFunction[T, U], error: T => String): CpsParseResult[U]   
//    
//    def flatMapWithNext[U](f: T => Input => CpsParseResult[U]): CpsParseResult[U]     
//
//    def append[U >: T](a: => CpsParseResult[U]): CpsParseResult[U]
//    
//    def isEmpty = !successful
//    
//    /** Returns the embedded result */
//    def get: T
//    
//    def getOrElse[B >: T](default: => B): B = 
//        if (isEmpty) default else this.get
//    
//    val next: Input
//    
//    val successful: Boolean
//  }
//
//  /** The success case of CpsParseResult: contains the result and the remaining input.
//   *
//   *  @param result The parser's output 
//   *  @param next   The parser's remaining input
//   */
//  case class CpsSuccess[+T](result: T, override val next: Input) extends CpsParseResult[T] {
//    def map[U](f: T => U) = CpsSuccess(f(result), next)
//    def mapPartial[U](f: PartialFunction[T, U], error: T => String): CpsParseResult[U] 
//       = if(f.isDefinedAt(result)) CpsSuccess(f(result), next) 
//         else CpsFailure(error(result), next)
//
//    def flatMapWithNext[U](f: T => CpsParser[U]): CpsParseResult[U] 
//      = f(result)(next) 
//
//    def append[U >: T](a: => CpsParseResult[U]): CpsParseResult[U] = this
//
//    def get: T = result
//    
//    /** The toString method of a CpsSuccess */
//    override def toString = "["+next.pos+"] parsed: "+result
//    
//    val successful = true
//  }
//  
//  var lastNoSuccess: CpsNoSuccess = null
//
//  /** A common super-class for unsuccessful parse results
//   */
//  sealed abstract class CpsNoSuccess(val msg: String, override val next: Input) extends CpsParseResult[Nothing] { // when we don't care about the difference between CpsFailure and CpsError
//    val successful = false
//    if (!(lastNoSuccess != null && next.pos < lastNoSuccess.next.pos))
//      lastNoSuccess = this
//
//    def map[U](f: Nothing => U) = this
//    def mapPartial[U](f: PartialFunction[Nothing, U], error: Nothing => String): CpsParseResult[U] = this
//
//    def flatMapWithNext[U](f: Nothing => Input => CpsParseResult[U]): CpsParseResult[U] 
//      = this
//
//    def get: Nothing = error("No result when parsing failed")
//  }
//  /** An extractor so CpsNoSuccess(msg, next) can be used in matches.
//   */
//  object CpsNoSuccess {
//    def unapply[T](x: CpsParseResult[T]) = x match {
//      case CpsFailure(msg, next)   => Some(msg, next)
//      case CpsError(msg, next)     => Some(msg, next)
//      case _                    => None
//    }
//  }
//  
//  /** The failure case of CpsParseResult: contains an error-message and the remaining input.
//   * Parsing will back-track when a failure occurs.
//   *
//   *  @param msg    An error message string describing the failure.
//   *  @param next   The parser's unconsumed input at the point where the failure occurred.
//   */
//  case class CpsFailure(override val msg: String, override val next: Input) extends CpsNoSuccess(msg, next) {
//    /** The toString method of a CpsFailure yields an error message */
//    override def toString = "["+next.pos+"] failure: "+msg+"\n\n"+next.pos.longString
//    
//    def append[U >: Nothing](a: => CpsParseResult[U]): CpsParseResult[U] = { val alt = a; alt match {
//      case CpsSuccess(_, _) => alt
//      case ns: CpsNoSuccess => if (alt.next.pos < next.pos) this else alt
//    }}
//  }
//  
//  /** The fatal failure case of CpsParseResult: contains an error-message and the remaining input.
//   * No back-tracking is done when a parser returns an `CpsError' 
//   *
//   *  @param msg    An error message string describing the error.
//   *  @param next   The parser's unconsumed input at the point where the error occurred.
//   */
//  case class CpsError(override val msg: String, override val next: Input) extends CpsNoSuccess(msg, next) {
//    /** The toString method of an CpsError yields an error message */
//    override def toString = "["+next.pos+"] error: "+msg+"\n\n"+next.pos.longString
//    def append[U >: Nothing](a: => CpsParseResult[U]): CpsParseResult[U] = this
//  }
//
//  def resetParser[U](next: CpsParser[U]) = new CpsParser[U] { def apply(in: Input) = reset { next(in) } }
//  case class ParsingThread[+U](contPars: CpsParser[U], feature: FeatureExpr, captCont: CpsParseResult[U] => CpsParseResult[U])
//  
//  case class ParsingThreads[+U](isComplete: Boolean, cont: CpsParser[U], currFeat: FeatureExpr, states: List[ParsingThread[U]])
//  class ParsingContext extends ArrayStack[ParsingThreads[_]] {}
//  //XXX make this thread-local
//  def parsingCtx = new ParsingContext()
//
//  def pushNewCtx[U](next: CpsParser[U], feature: FeatureExpr) = parsingCtx.push(ParsingThreads(feature.isBase(), next, feature, Nil))
//  
//  /* The returned parser fetches the top of the stack, and pushes it again after adding the captured continuation and feature,
//   * in the list of suspended closures associated with firstNext,
//   * reinvoke oldNext under reset (maybe the original reset is enough, maybe not).*/
//  def alternativeParser[U](next: CpsParser[U], newTopFeatOpt: Option[FeatureExpr]) = //: CpsParser[U] =
//	  ((in: Input) => /*: CpsParseResult[U] @cpsParam[CpsParseResult[U], CpsParser[U]] = {*/
//	  shift {
//	 	  (captCont: CpsParseResult[U] => CpsParseResult[U]) =>
//	 	  val topEntry @ ParsingThreads (isComplete, oldNext, topFeat, currStates) = parsingCtx.pop
//	 	  val newList = new ParsingThread(next, topFeat, captCont) :: currStates
//	 	  val negOldFeat = topFeat.not()
//	 	  parsingCtx.push(newTopFeatOpt match {
//	 	 	  case Some(newFeat) => ParsingThreads(isComplete, next, negOldFeat and newFeat, newList)
//	 	 	  case None => ParsingThreads(true, next, negOldFeat, newList)
//	 	  })
//	 	  oldNext
//	  })
//  def CpsParser[T](f: Input => CpsParseResult[T]): CpsParser[T] =
//	new CpsParser[T]{ def apply(in: Input) = f(in) }
//  
//  //class CpsParser[+T] extends (Input => (CpsParseResult[T] @cpsParam[CpsParseResult[T], CpsParser[T]])) {}
//  
//  abstract class CpsParser[+T] extends (Input => CpsParseResult[T]) {
//	private var name: String = ""
//    def named(n: String): this.type = {name=n; this}
//    override def toString() = "CpsParser ("+ name +")"
//
//    def flatMap[U](f: T => CpsParser[U]): CpsParser[U]
//     = CpsParser{ in => this(in) flatMapWithNext(f) }
//
//    //def ~ [U](p: => Parser[U]): Parser[~[T, U]] = (for(a <- this; b <- p) yield new ~(a,b)).named("~")
//    //def baseParserSeq[U](p: => CpsParser[U]): Parser[~[T, U]] =
//    //  (for (a <- this; b <- p) yield new ~(a, b)).named("~")
//
//    def baseParserSeq[U](p: Elem => CpsParser[U]): CpsParser[~[T, U]] =
//    	this.flatMap((resThis: T) => new CpsParser[~[T, U]] {def apply(in: Input) {new ~(resThis, p(in.first)(in)) } })
//      //(for (a <- this; b <- p) yield new ~(a, b)) //.named("~")*/
//
//    // XXX: this should be part of a companion Object.
//    //This needs to be invoked on the resulting parser to accept #ifdef also at the beginning.
//    //All other positions are handled by ~  
//    def invoke[U](next: => CpsParser[U]) = new CpsParser[U] {
//    	def apply(in: Input): CpsParseResult[U] = in.first match {
//		    //do the same handling as below for #if: push next somewhere.
//        	case If(feature) => pushNewCtx(next, feature); reset { next(in.rest) }
//    		case _ => next(in)
//    	} // | next
//    }
//    def append[U >: T](p: => CpsParser[U]): CpsParser[U] 
//      = CpsParser{ in => this(in) append p(in)}
//
//    def | [U >: T](q: => CpsParser[U]): CpsParser[U] = append(q).named("|")
//
//	//MultiFeatureContParsers.this.CpsParseResult[U] @cpsParam[CpsParseResult[U],Nothing]  required: MultiFeatureContParsers.this.CpsParseResult[U]    
//    
//    def createAltNode(l: List[_]): AnyRef
//    
//    def ~[U](next: => CpsParser[U]): CpsParser[~[T, U]] = {
//    	this baseParserSeq ({
//    		case If(feature) => pushNewCtx(next, feature); resetParser(next)
//    		/* push next somewhere, so as to reinvoke it and to check if we re-share it.*/
//    		case Elif(feature) => val oldNext = alternativeParser(next, Some(feature)); resetParser(oldNext)
//    		case Else => val oldNext = alternativeParser(next, None); resetParser(oldNext)
//    		case Endif =>
//    		alternativeParser(next, None) //resetParser(next)
//    		//isComplete signals whether this is #if...#endif or #if...#else...#endif!
//    		val topEntry @ ParsingThreads (isComplete, oldNext, topFeat, currStates) = parsingCtx.pop
//    		val nextParser = if (isComplete) next else oldNext
//    		new CpsParser[U] {
//    			def apply(in: Input) = reset {
//    				createAltNode (currStates flatMap {
//    					{ case ParsingThread(next, topFeat, captCont) => if (next == nextParser) List(captCont(nextParser(in))) else Nil }
//    				})
//    			}
//    		}
//        })
//
//        /* In endif, do the same as in #else, then
//        pop the context stack and merge back the suspended works which have the
//        same next component; then, for each entry in the merged list, invoke
//        next, pass the result to all continuations, and finally merge them in an
//        alternative node. Fuuuh! */
//      
//        /*accept ("#ifdef") ~ reset next |
//        (accept("#else") | accept("#elif")) ~ shift k
//        commit(reset next) |
//        accept("#endif") ~ commit(reset next) ) match {
//        case (~(a, b)) => return b*/
//      //}
//    }
//  }
//}
