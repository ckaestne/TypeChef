package de.fosd.typechef.parser.html

import de.fosd.typechef.parser._
import de.fosd.typechef.conditional.{ ConditionalLib, Opt }
import de.fosd.typechef.parser.common.CharacterToken

//
///*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// * based on GCIDE grammar in CIDE
// * which is again based on the publically available JavaCC grammar
// */
//
class HTMLSAXParser extends MultiFeatureParser {
  type Elem = CharacterToken
  type TypeContext = Null

  ////grammar
  ///*****************************************
  // * THE JAVA LANGUAGE GRAMMAR STARTS HERE *
  // *****************************************/
  //
  ///*
  // * Program structuring syntax follows.
  // */
  //

  implicit def keyword(s: Char): MultiParser[Elem] = token(String.valueOf(s), _.getKind() == s)

  def HtmlSequence: MultiParser[List[Opt[HElement]]] =
    opt(HtmlDocType).! ~>      repOpt(HtmlElement)

  def HtmlDocType = '<' ~ '!' ~ repPlain(token("any char except >", _.getKindChar() != '>')) ~ '>' ~ repOpt(token("any char except <", _.getKindChar() != '<'))

  def HtmlElement: MultiParser[HElement] = HtmlTag | HtmlText | HtmlComment

  def HtmlTag: MultiParser[HTag] = '<' ~ opt('/') ~ (WSs ?) ~ Identifier ~ AttributesSeq ~ opt('/' <~ (WSs ?)) ~ '>' ^^ {
    case _ ~ c ~ _ ~ id ~ attr ~ c2 ~ _ => HTag(id, c.isDefined, c2.isDefined, attr)
  }

  /*
    def HtmlScriptTag = HtmlScriptTagBegin ~ HtmlScriptTagBody ~ HtmlScriptTagEnd
    
    def HtmlScriptTagBegin: MultiParser[HTag] = '<' ~> 's' ~> 'c' ~> 'r' ~> 'i' ~> 'p' ~> 't' ~> AttributesSeq ~ opt('/' <~ (WSs ?)) <~ '>' ^^ {
      case attr ~ c2 => HTag("script", false, c2.isDefined, attr)
    }
    
    def HtmlScriptTagBody: MultiParser[HText] = rep1(CharInScriptTagBody) ^^ {HText(_)}
    
    def CharInScriptTagBody: MultiParser[CharacterToken] = (token("any char except <", x => x.getKindChar() != '<') 
    													| ('<' <~ lookahead(token("any char except s", x => x.getKindChar() != 's')))
    													| ('<' <~ lookahead('s' ~ token("any char except c", x => x.getKindChar() != 'c')))
    													| ('<' <~ lookahead('s' ~ 'c' ~ token("any char except r", x => x.getKindChar() != 'r')))
    													| ('<' <~ lookahead('s' ~ 'c' ~ 'r' ~ token("any char except i", x => x.getKindChar() != 'i')))
    													| ('<' <~ lookahead('s' ~ 'c' ~ 'r' ~ 'i' ~ token("any char except p", x => x.getKindChar() != 'p')))
    													| ('<' <~ lookahead('s' ~ 'c' ~ 'r' ~ 'i' ~ 'p' ~ token("any char except t", x => x.getKindChar() != 't'))))
    													
    def HtmlScriptTagEnd: MultiParser[HTag] = '<' ~ '/' ~ 's' ~ 'c' ~ 'r' ~ 'i' ~ 'p' ~ 't' ~ '>' ^^ { x => HTag("script", true, false, Nil)}
    */

  //avoid replicating entire tags due to whitespace issues, instead replicate only attribute sequences and force join
  private def AttributesSeq: MultiParser[List[Opt[HAttribute]]] = (optList(WSs ~> Attributes) <~ (WSs ?)).join ^^ { ConditionalLib.items(_).map(e => e._2.map(_.and(e._1))).reduce(_ ++ _) }

  //def HtmlText: MultiParser[HText] = rep1(token("no < or >", x => !(Set('<', '>') contains x.getKindChar()))) ^^ {HText(_)}
  def HtmlText: MultiParser[HText] = rep1(CharInText) ^^ { HText(_) }

  def CharInText: MultiParser[CharacterToken] = (token("any char except <", x => x.getKindChar() != '<'))
  //    												| ('<' <~ lookahead(token("not word char", x => x.getKindChar() != '/' && x.getKindChar() != '!' && !isWordChar(x))))
  //    												| ('<' <~ lookahead('/' ~ token("not word char", !isWordChar(_))))
  //    												| ('<' <~ lookahead('!' ~ token("any char except -", x => x.getKindChar() != '-')))
  //    												| ('<' <~ lookahead('!' ~ '-' ~ token("any char except -", x => x.getKindChar() != '-'))))

  def HtmlComment: MultiParser[HText] = '<' ~> '!' ~> '-' ~> '-' ~> repOpt(CharInComment) <~ '-' <~ '-' <~ '>' ^^ {
    HText(_)
  }

  def CharInComment: MultiParser[CharacterToken] = (token("any char except -", x => x.getKindChar() != '-')
    | ('-' <~ lookahead(token("any char except -", x => x.getKindChar() != '-')))
    | ('-' <~ lookahead('-' ~ token("any char except >", x => x.getKindChar() != '>'))))

  def WS: MultiParser[Elem] = keyword(' ') | '\t' | '\n' | '\r'

  def WSs: MultiParser[Any] = rep1(WS)

  def Identifier: MultiParser[String] = Char ~ repPlain(Char) ^^ { case f ~ r => (f :: r).map(_.getText()).mkString }

  def Char = token("word", isWordChar(_))

  def isWordChar(x: Elem): Boolean =
    (('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9') :+ '_' :+ '-' :+ ':') contains x.getKind()

  def Attributes: MultiParser[List[Opt[HAttribute]]] = repSep(Attribute, WSs)
  def Attribute: MultiParser[HAttribute] = Identifier ~ opt((WSs ?) ~ '=' ~> AttrValue) ^^ { case i ~ v => HAttribute(i, v) }
  def AttrValue: MultiParser[String] =
    (('"' ~> repPlain(token("any char except \"", _.getKindChar() != '"')) <~ '"') |
      ('\'' ~> repPlain(token("any char except '", _.getKindChar() != '\'')) <~ '\'') |
      (repPlain(token("any char except space", _.getKindChar() != ' ')) <~ lookahead(' '))) ^^ { _.map(_.getText()).mkString }

}


