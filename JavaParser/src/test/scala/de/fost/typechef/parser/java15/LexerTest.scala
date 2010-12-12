package de.fost.typechef.parser.java15
import org.junit.Test
import de.fosd.typechef.parser.java15.lexer._
import java.io._

class LexerTest {

    @Test
    def testLexerBasic() {
    	val input = "test"
        val a = new ByteArrayInputStream(input.toArray)
        val o = new OffsetCharStream(a)
        new Java15ParserTokenManager(o)
    }

}