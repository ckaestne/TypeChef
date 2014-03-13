package edu.iastate.hungnv.parser.css

import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import java.io.StringReader
import de.fosd.typechef.parser.TokenReader
import org.junit.Assert._
import org.junit.Test
import edu.iastate.hungnv.parser.css._
import de.fosd.typechef.parser.common.{CharacterLexer, CharacterToken}

class CSSParserTest {



    val p = new CSSParser()

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


    @Test def test1 {
        assertParseable("d",p.Char)
    }
    
    @Test def test2 {
        assertParseable("div {color:blue}", p.StyleSheet)
    }
    
    @Test def test3 {
        assertParseable("div {color: blue }\n#if (abc)\ndiv1 {color: red}\n#else\ndiv2 {color: red}\n#endif\n", p.StyleSheet)
    }
    
    @Test def test4 {
        assertParseable("\n#if (abc)\ndiv1 {color: red}\n#else\ndiv2 {color: red}\n#endif\n", p.StyleSheet)
    }
    
    @Test def test5 {
    	assertParseable("table#maintable th, div {SYM}", p.StyleSheet)
    }
    
    @Test def test6 {
    	assertParseable("table#maintable, div {SYM}", p.StyleSheet)
    }
    
    @Test def test7 {
    	assertParseable("body,#footer,ul {margin:0;padding:0;}", p.StyleSheet)
    }
    
    @Test def test8 {
    	assertParseable(".yellowtext {margin:0;padding:0;}", p.StyleSheet)
    }
    
    // Problem with space in if-else: 1 Success becomes SplittedParseResult(Success, Success) 
    @Test def test_Debug1 {
    	assertParseable("\n#if (abc)\n   body {background-image:url('./skins/header-}\n#else\ndiv { div }\n#endif\n", p.StyleSheet)
    }
    
    // Problem with space in if-no-else: Use test.css. Why are 2 test results different?
    @Test def test_Debug2 {
    	assertParseable("\n#if (X)\nc {x}\n#endif\n", p.StyleSheet)
    }

}