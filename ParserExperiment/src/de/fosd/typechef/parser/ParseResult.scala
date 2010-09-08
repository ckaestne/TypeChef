package de.fosd.typechef.parser
import scala.util.parsing.input.Reader


/**
 * contains the recognized parser result (including recognized alternatives?)
 * @author kaestner
 */
sealed abstract class ParseResult[+T](nextInput: Reader[Token]) {
    def map[U](f: T => U): ParseResult[U]
    def next = nextInput
    def isSuccess:Boolean
}
case class NoSuccess(msg: String, nextInput: Reader[Token]) extends ParseResult[Nothing](nextInput) {
    def map[U](f: Nothing => U) = this
    def isSuccess:Boolean=false
}
case class Success[+T](result: T, nextInput: Reader[Token]) extends ParseResult[T](nextInput) {
    def map[U](f: T => U): ParseResult[U] = Success(f(result), next)
    def isSuccess:Boolean=true
}
