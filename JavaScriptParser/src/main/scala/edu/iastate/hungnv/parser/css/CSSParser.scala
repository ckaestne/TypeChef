package edu.iastate.hungnv.parser.css

import de.fosd.typechef.parser._
import de.fosd.typechef.conditional.{ ConditionalLib, Opt }
import de.fosd.typechef.parser.common.CharacterToken

/**
 * @author HUNG
 * Modified from TypeChef
 */
class CSSParser extends MultiFeatureParser {
  type Elem = CharacterToken
  type TypeContext = Null

  /*
   * Utility methods
   */
  private implicit def char(s: Char): MultiParserExt[Elem] = token(s.toString, _.getKindChar() == s)
  private implicit def keyword(s: String): MultiParserExt[String] = {
    assert(s.length > 0)
    var result = char(s.charAt(0)) ^^ { _.getText() }
    for (i <- 1 until s.length)
      result = result ~ char(s.charAt(i)) ^^ { case a ~ b => a + b.getText() }
    new MultiParserExt(result)
  }
  
  //implict hack to introduce ~~~
  implicit private def parserExtension[U](s: MultiParser[U]): MultiParserExt[U] = new MultiParserExt(s)

  private class MultiParserExt[T](s: MultiParser[T]) extends MultiParser[T] {
    // sequence two parsers separated by whitespace
    def ~~~[U](thatParser: => MultiParser[U]): MultiParser[~[T, U]] = (s <~ WSs) ~ thatParser
    def ~~~?[U](thatParser: => MultiParser[U]): MultiParser[~[T, U]] = (s <~ opt(WSs)) ~ thatParser
    def <~~~[U](thatParser: => MultiParser[U]): MultiParser[T] = (s <~ WSs) <~ thatParser
    def <~~~?[U](thatParser: => MultiParser[U]): MultiParser[T] = (s <~ opt(WSs)) <~ thatParser
    def ~~~>[U](thatParser: => MultiParser[U]): MultiParser[U] = (s <~ WSs) ~> thatParser
    def ~~~?>[U](thatParser: => MultiParser[U]): MultiParser[U] = (s <~ opt(WSs)) ~> thatParser
    def apply(v1: Input, v2: ParserState): MultiParseResult[T] = s.apply(v1, v2)
  }

  private def repSepPlainWSo[T, U](p: => MultiParser[T], separator: => MultiParser[U]): MultiParser[List[T]] =
    repSepPlain(p, WSo ~ separator ~ WSo)
  private def repSepPlainWSs[T, U](p: => MultiParser[T], separator: => MultiParser[U]): MultiParser[List[T]] =
    repSepPlain(p, WSs ~ separator ~ WSs)
  private def rep1SepWSo[T, U](p: => MultiParser[T], separator: => MultiParser[U]) =
    rep1Sep(p, WSo ~ separator ~ WSo)
  private def rep1SepPlainWSo[T, U](p: => MultiParser[T], separator: => MultiParser[U]) =
    repSepPlain1(p, WSo ~ separator ~ WSo)

  def WS: MultiParser[Any] = char(' ') | '\t' | '\n' | '\r' | BlockComment | LineComment | fail("expected whitespace")
  def WSs: MultiParser[Any] = rep1(WS)
  def WSo: MultiParser[Any] = repOpt(WS)

  def BlockComment: MultiParser[Any] = keyword("/*") ~ BlockCommentBody
  def BlockCommentBody: MultiParser[Any] = repPlain(BlockCommentChar) ~ BlockCommentEnd
  def BlockCommentChar: MultiParser[Any] = token("any except *", _.getKindChar() != '*')
  def BlockCommentEnd: MultiParser[Any] = keyword("*/") | (char('*') ~ BlockCommentBody)

  def LineComment = keyword("//") ~ repPlain(LineCommentChar)
  def LineCommentChar: MultiParser[Any] = token("any except newline", _.getKindChar() != '\n')

  /*
   * CSS Grammar
   * http://www.w3.org/TR/CSS21/grammar.html#grammar
   */
  def StyleSheet: MultiParser[CStyleSheet] = repOpt(WSo ~> RuleSet) <~ WSo ^^ { CStyleSheet(_) }

//  def RuleSet: MultiParser[CRuleSet] = repSepPlainWSo(Selector, ',') ~~~? '{' ~~~? Declarations ~~~? '}' ^^ { case s ~ _ ~ d ~ _ => new CRuleSet(s, d.toString) }
  def RuleSet: MultiParser[CRuleSet] = repSepPlain(Selector, WS | ',') ~~~? '{' ~~~? Declarations ~~~? '}' ^^ { case s ~ _ ~ d ~ _ => new CRuleSet(s, d.toString) }
  
  def Selector: MultiParser[CSelector] = SimpleSelector | OtherSelector
  
  def SimpleSelector: MultiParser[CSelector] = (('.' ~> Identifier) | ('#' ~> Identifier)) ^^ { x => new CSelector(x) }
  
  def OtherSelector: MultiParser[CSelector] = rep1(token("OtherSelector", x => (('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9') :+ '_' :+ '-' :+ ':' :+ '#' :+ '[' :+ ']' :+ '=' :+ '.') contains x.getKind())) ^^ { _ => new CSelector(new DString("OtherSelector")) }
  
  def Declarations: MultiParser[Any] = repOpt(token("any char except }", x => x.getKindChar() != '}'))
    
  
  
  def Identifier: MultiParser[DString] = Char ~ repPlain(Char) ^^ { case f ~ r => new DString((f :: r).map(_.getText()).mkString) }

  def Char = token("word", isWordChar(_))

  def isWordChar(x: Elem): Boolean = (('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9') :+ '_' :+ '-' :+ ':') contains x.getKind()

}