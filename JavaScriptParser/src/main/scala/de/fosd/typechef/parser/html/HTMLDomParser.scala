package de.fosd.typechef.parser.html

import de.fosd.typechef.parser._
import de.fosd.typechef.conditional.Opt

//
///*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// * based on GCIDE grammar in CIDE
// * which is again based on the publically available JavaCC grammar
// */
//
class HTMLDomParser extends MultiFeatureParser {
    type Elem = HElementToken
    type TypeContext = Null


    def Element: MultiParser[DElement] =
        Text | Node

    def Node: MultiParser[DNode] =
        Tag | EmptyTag | fail("expected well-formed node")

    private def _Tag: MultiParser[(String, String, List[Opt[HAttribute]], List[Opt[DElement]])] = StartTag ~ repOpt(Element) ~ ClosingTag ^^ {case (n, a) ~ e ~ n2 => (n, n2, a, e)}

    def Tag: MultiParser[DNode] = new MultiParser[DNode] {
        def apply(in: Input, ctx: ParserState): MultiParseResult[DNode] = {
            val result = _Tag(in, ctx)
            result.mapfr(ctx, {
                case (f, Success((n1, n2, attr, inner), next)) =>
                    if (n1==n2)
                        Success(DNode(n1, attr, inner), next)
                    else
                        Failure("Illformed html "+n1+" - "+n2, next,List())
                case (f, r@NoSuccess(_,_,_)) => r
            })
        }
    }


    def StartTag: MultiParser[(String, List[Opt[HAttribute]])] = token("starttag", _.element match {
        case HTag(name, false, false, attributes) => true
        case _ => false
    }) ^^ {x => val htag = x.element.asInstanceOf[HTag]; (htag.name, htag.attributes)}

    def ClosingTag: MultiParser[String] = token("closingtag", _.element match {
        case HTag(name, true, false, _) => true
        case _ => false
    }) ^^ {x => x.element.asInstanceOf[HTag].name}

    def EmptyTag: MultiParser[DNode] = token("emptytag", _.element match {
        case HTag(name, false, true, attributes) => true
        case _ => false
    }) ^^ {x => val htag = x.element.asInstanceOf[HTag]; DNode(htag.name, htag.attributes, List())}

    def Text: MultiParser[DText] = token("text", _.element match {
        case HText(text) => true
        case _ => false
    }) ^^ {x => DText(x.element.asInstanceOf[HText].value)}


}


