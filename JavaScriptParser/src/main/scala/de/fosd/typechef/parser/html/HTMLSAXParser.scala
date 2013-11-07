package de.fosd.typechef.parser.html

import de.fosd.typechef.parser._
import de.fosd.typechef.conditional.{ConditionalLib, Opt}
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
        repOpt(HtmlElement)

    def HtmlElement: MultiParser[HElement] = HtmlTag | HtmlText

    def HtmlTag: MultiParser[HTag] = '<' ~ opt('/') ~ (WSs ?) ~ Identifier ~ AttributesSeq ~ opt('/' <~ (WSs ?)) ~ '>' ^^ {
        case _ ~ c ~ _ ~ id ~ attr ~ c2 ~ _ => HTag(id, c.isDefined, c2.isDefined, attr)
    }

    //avoid replicating entire tags due to whitespace issues, instead replicate only attribute sequences and force join
    private def AttributesSeq: MultiParser[List[Opt[HAttribute]]] = (optList(WSs ~> Attributes) <~ (WSs ?)).join ^^ { ConditionalLib.items(_).map(e => e._2.map(_.and(e._1))).reduce(_ ++ _) }

    def HtmlText: MultiParser[HText] = rep1(token("no < or >", x => !(Set('<', '>') contains x.getKindChar()))) ^^ {HText(_)}

    def WS: MultiParser[Elem] = keyword(' ') | '\t' | '\n' | '\r'

    def WSs: MultiParser[Any] = rep1(WS)

    def Identifier: MultiParser[String] = Char ~ repPlain(Char) ^^ {case f ~ r => (f :: r).map(_.getText()).mkString}

    def Char = token("word", isWordChar(_))

    def isWordChar(x: Elem): Boolean =
        (('a' until 'z') ++ ('A' until 'Z') :+ '_') contains x.getKind()


    def Attributes: MultiParser[List[Opt[HAttribute]]] = repSep(Attribute, WSs)
    def Attribute: MultiParser[HAttribute] = Identifier ~ opt((WSs ?) ~ '=' ~> AttrValue) ^^ {case i ~ v => HAttribute(i, v)}
    def AttrValue: MultiParser[String] =
        (('"' ~> repPlain(token("any char except \"", _.getKindChar() != '"')) <~ '"') |
            ('\'' ~> repPlain(token("any char except '", _.getKindChar() != '\'')) <~ '\'')) ^^ {_.map(_.getText()).mkString}


}


