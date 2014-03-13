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
  def StyleSheet: MultiParser[CStyleSheet] = 
    						WSo ~>
  							opt(CHARSET_SYM ~ STRING ~ ';') ~>
  							repPlain(S | CDO | CDC) ~>
  							repOpt(RuleSet <~ repPlain(CDO ~ Sa | CDC ~ Sa)) ^^ { CStyleSheet(_) } // FIXME

  def RuleSet: MultiParser[CRuleSet] = 
		  					repSepPlain1(Selector, ',' ~ Sa) <~
		  					//'{' <~ Sa <~ repSepPlain(';' ~ Sa, Declaration) <~ '}' <~ Sa ^^ { case s => new CRuleSet(s, "") }
		  					'{' <~ Declaration <~ '}' <~ Sa ^^ { case s => new CRuleSet(s, "") } // FIXME
  
  def Selector: MultiParser[CSelector] = 
		  					SimpleSelector ~
		  					opt((Combinator ~ Selector) | (Sp ~ opt(opt(Combinator) ~ Selector))) ^^ {
		  								case x ~ None => x
		  								case x ~ Some(List(_*)~None) => x
		  								case x ~ y => new COtherSelector(new DString(x.toString + y.toString))
		  							}
		  					
  def Combinator: MultiParser[Any] = 
    						('+' ~ Sa) |
    						('>' ~ Sa)
  
  def SimpleSelector: MultiParser[CSelector] = 
    						(ElementName ~ repPlain(HASH | Clazz | Attrib | Pseudo)) ^^ {
    									case x ~ List() => new CSimpleSelector(x, None)
    									case x ~ List(y) => new CSimpleSelector(x, Some(y))
    									case x ~ _ => new COtherSelector(new DString(""))
    								} |
    						rep1Plain(HASH | Clazz | Attrib | Pseudo) ^^ { x => x.head }
  
  def ElementName: MultiParser[DString] =
		  					IDENT | ('*' ^^ {x => new DString(x.getText) })
		  					
  def Clazz: MultiParser[CSelector] =
    						'.' ~> IDENT ^^ {x => new CClassSelector(x)}
    						
  def Attrib: MultiParser[CSelector] =
    						'[' ~ Sa ~ IDENT ~ Sa ~ opt(("=" | INCLUDES | DASHMATCH) ~ Sa ~ (IDENT | STRING) ~ Sa) ~ ']' ^^ {_ => new COtherSelector(new DString(""))}
    						
  def Pseudo: MultiParser[CSelector] =
    						':' ~ (IDENT | (FUNCTION ~ Sa ~ opt(IDENT ~ Sa) ~ ')')) ^^ {_ => new COtherSelector(new DString(""))}
		  					
  def Declaration: MultiParser[Any] = rep1Plain(token("any char except }", x => x.getKindChar() != '}')) // FIXME
  
  /*
   * CSS Lexer
   */
  def CDO = "<!--"
    
  def CDC = "-->"
    
  def CHARSET_SYM = "@charset "
    
  def STRING = ("\"" ~ negate('\"') ~ "\"") | ("\'" ~ negate('\'') ~ "\'") // FIXME
  
  def IDENT: MultiParser[DString] = Char ~ repPlain(Char) ^^ { case f ~ r => new DString((f :: r).map(_.getText()).mkString) } // FIXME
  
  def HASH: MultiParser[CSelector] = '#' ~> IDENT ^^ {x => new CHashSelector(x) } // FIXME
  
  def INCLUDES = "~="
    
  def DASHMATCH = "|="
    
  def FUNCTION = IDENT ~ "("
    
  def S = WS
  
  def Sa = repPlain(WS)
  
  def Sp = rep1Plain(WS)
  
  def Char = token("word", isWordChar(_))

  def isWordChar(x: Elem): Boolean = (('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9') :+ '_' :+ '-' :+ ':') contains x.getKind()
  
  /*
   * Hung's utility methods
   */
  def negate(c: Char): MultiParser[Any] = token("any char except " + c, x => x.getKindChar() != c)
  
}