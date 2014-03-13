package de.fosd.typechef.parser.html

import de.fosd.typechef.parser._
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.parser.common.JPosition

//
///*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// * based on GCIDE grammar in CIDE
// * which is again based on the publically available JavaCC grammar
// */
//
class HTMLDomParser extends MultiFeatureParser {
    type Elem = HElementToken
    type TypeContext = Null


    def Document = repOpt(Element)

    def Element: MultiParser[DElement] =
        Text | Node

    def Node: MultiParser[DNode] =
        Tag | EmptyTag | fail("expected well-formed node")

//    private def _Tag: MultiParser[(String, String, List[Opt[HAttribute]], List[Opt[DElement]])] = StartTag ~! repOpt(Element) ~! ClosingTag ^^ {case (n, a) ~ e ~ n2 => (n, n2, a, e)}
//    private def TagOpening: MultiParser[(String, List[Opt[HAttribute]], List[Opt[DElement]])] = StartTag ~! repOpt(Element) ^^ {case (n, a) ~ e => (n, a, e)}

    def Tag: MultiParser[DNode] = new MultiParser[DNode] {
        def apply(in: Input, ctx: ParserState): MultiParseResult[DNode] = {
            //first parse opening tag and then all inner elements as far as possible
            //(essentially takes everything until reaching a closing tag)
            var openingResults = StartTag(in, ctx)
            openingResults.mapfr(ctx, (ctx, startResults) =>

                startResults.mapfr(ctx, (ctx, startResult) => startResult match {
                    case Success(startR, startRest) =>
                        val midResults = repOpt(Element)(startRest, ctx)
                        midResults.mapfr(ctx, (ctx, midResult) => midResult match {
                            case Success(midR, midRest) =>
                                var closingResults = ClosingTag(midRest, ctx)
                                closingResults.mapfr(ctx, (ctx, closingResult) =>
                                    closingResult match {
                                        case Success(closingR, closingRest) =>
                                            if (startR.name == closingR.name)
                                                Success(DNode(startR.name, startR.attributes, midR, startR, closingR), closingRest)
                                            else
                                              
                                              // BEGIN OF QUICK FIX
//                                            	if (startR.name.name.equals("img") | startR.name.name.equals("meta")) // Handled specifically for img, ...
//                                            	  Success(DNode(startR.name, startR.attributes, null, startR, null), startRest)
//                                            	else
                                              // END OF QUICK FIX
                                              
                                                Error("Mismatching closing tag for <" + startR.name.name + "> (found </" + closingR.name.name + "> at " + closingRest.pos + ")", in, List())
                                        case n@NoSuccess(msg, restCl, _) =>
                                              // BEGIN OF QUICK FIX
//                                            	if (startR.name.name.equals("img") | startR.name.name.equals("meta")) // Handled specifically for img, ...
//                                            	  Success(DNode(startR.name, startR.attributes, null, startR, null), startRest)
//                                            	else
                                              // END OF QUICK FIX 
                                               
                                            Error("No matching closing tag for <" + startR.name.name + ">", in, List(n))
                                    }
                                )
                            case e: NoSuccess => e
                        })
                    case e: NoSuccess => e
                })

        )
    }
}

def StartTag: MultiParser[HTag] = token ("starttag", _.element match {
case HTag (name, false, false, attributes) => true
case _ => false
}) ^^ {
x => val htag = x.element.asInstanceOf[HTag];
htag
}

//def StartTag: MultiParser[(DString, List[Opt[HAttribute]])] = token ("starttag", _.element match {
//case HTag (name, false, false, attributes) => true
//case _ => false
//}) ^^ {
//x => val htag = x.element.asInstanceOf[HTag];
//(htag.name, htag.attributes)
//}
//

def ClosingTag: MultiParser[HTag] = token ("closingtag", _.element match {
case HTag (name, true, false, _) => true
case _ => false
}) ^^ {
x => x.element.asInstanceOf[HTag]
} | fail ("expected closing tag")

def EmptyTag: MultiParser[DNode] = token ("emptytag", _.element match {
case HTag (name, false, true, attributes) => true
case _ => false
}) ^^ {
x => val htag = x.element.asInstanceOf[HTag];
DNode (htag.name, htag.attributes, List (), htag, htag)
}

def Text: MultiParser[DText] = token ("text", _.element match {
case HText (text) => true
case _ => false
}) ^^ {
x => DText (x.element.asInstanceOf[HText].value)
}


}


