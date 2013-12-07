package de.fost.typechef.parser.javascript

import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import java.io.StringReader
import de.fosd.typechef.parser.TokenReader
import org.junit.Assert._
import org.junit.Test
import de.fosd.typechef.parser.html._
import de.fosd.typechef.parser.common.{CharacterLexer, CharacterToken}

class HtmlParserTest {



    val p = new HTMLSAXParser()

    def assertParseable(code: String, mainProduction: (TokenReader[CharacterToken, Null], FeatureExpr) => p.MultiParseResult[Any]) {
        var tokens=CharacterLexer.lex(new StringReader(code))

        println(tokens)

        val actual = mainProduction(tokens, FeatureExprFactory.True)
        System.out.println(actual)
        (actual: @unchecked) match {
            case p.Success(ast, unparsed) => {
                assertTrue("parser did not reach end of token stream: " + unparsed, unparsed.atEnd)
                //succeed
            }
            case p.NoSuccess(msg, unparsed, inner) =>
                fail(msg + " at " + unparsed + " " + inner)
        }
    }


    @Test def test {
        assertParseable("d",p.Char)
        assertParseable("<foo>",p.HtmlTag)
        assertParseable("<foo/>",p.HtmlTag)
        assertParseable("<foo x='3'>",p.HtmlTag)
        assertParseable("<foo>a b<bar></bar>xx</foo>",p.rep1(p.HtmlElement))
      assertParseable("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"  >",p.HtmlTag)
//        assertParseable("<foo>__if \"X\"<bar></bar>__endif</foo>",p.HtmlTag)
//        assertParseable("<__if \"X\" foo __endif bar>",p.HtmlTag)
    }

}