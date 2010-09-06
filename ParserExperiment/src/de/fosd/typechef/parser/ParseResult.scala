package de.fosd.typechef.parser
import scala.util.parsing.input.Reader
/**
 * @class ParseResult 
 * contains the general parse result. in a split parsing phase, there are multiple
 * results. later these are merged to a single FeatureParseResult if possible
 * @author kaestner
 */
case class ParseResult[+T](results: Map[Context, FeatureParseResult[T]]) {
    def get =
        results

    def isError =
        results.values.exists(_ match { case NoSuccess(_, _) => true; case _ => false })
    
    def isFinished = 
        !results.values.exists(_ match { case NoSuccess(_, rest) => !rest.atEnd; case Success(_, rest) => !rest.atEnd })

    def map[U](f: T => U): ParseResult[U] =
        ParseResult(results.map((e) => (e._1 -> e._2.map(f))))
}

/**
 * contains the recognized parser result (including recognized alternatives?)
 * @author kaestner
 */
sealed abstract class FeatureParseResult[+T](nextInput: Reader[Token]) {
    def map[U](f: T => U): FeatureParseResult[U]
    def next = nextInput
    def join(feature: Int, that: FeatureParseResult[Any]): FeatureParseResult[T]
}
case class NoSuccess(msg: String, nextInput: Reader[Token]) extends FeatureParseResult[Nothing](nextInput) {
    def map[U](f: Nothing => U) = this
    def join(feature: Int, that: FeatureParseResult[Any]) = this
}
case class Success[+T](result: T, nextInput: Reader[Token]) extends FeatureParseResult[T](nextInput) {
    def map[U](f: T => U): FeatureParseResult[U] = Success(f(result), next)
    def join(feature: Int, that: FeatureParseResult[Any]) = that match {
        case Success(thatResult, thatNext) => this //Success(IF(feature, this.result, thatResult), nextInput)
        case ns@NoSuccess(_, _) => ns
    }

}