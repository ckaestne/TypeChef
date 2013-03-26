package de.fosd.typechef.lexer;

import org.junit.Test;

import java.util.Arrays;

import static de.fosd.typechef.lexer.Token.*;
import static org.junit.Assert.assertEquals;

public class LexerSourceTestCase {

    private void testLexerSource(String in, int... out)
            throws Exception {
        System.out.println("Testing '" + in + "' => " +
                Arrays.toString(out));
        StringLexerSource s = new StringLexerSource(in);

        for (int i = 0; i < out.length; i++) {
            Token tok = s.token();
            System.out.println("Token is " + tok);
            assertEquals(out[i], tok.getType());
        }
        assertEquals(EOF, s.token().getType());
    }

    @Test
    public void testLexerSource()
            throws Exception {

        testLexerSource("int a = 5;",
                IDENTIFIER, WHITESPACE, IDENTIFIER, WHITESPACE,
                '=', WHITESPACE, INTEGER, ';', EOF
        );

        // \n is WHITESPACE because ppvalid = false
        testLexerSource("# #   \r\n\n\r \rfoo",
                HASH, WHITESPACE, '#', WHITESPACE, IDENTIFIER
        );

        testLexerSource("%:%:", PASTE);
        testLexerSource("%:?", '#', '?');
        testLexerSource("%:%=", '#', MOD_EQ);
        testLexerSource("0x1234ffdUL 0765I",
                INTEGER, WHITESPACE, INTEGER);

        testLexerSource("+= -= *= /= %= <= >= >>= <<= &= |= ^= x",
                PLUS_EQ, WHITESPACE,
                SUB_EQ, WHITESPACE,
                MULT_EQ, WHITESPACE,
                DIV_EQ, WHITESPACE,
                MOD_EQ, WHITESPACE,
                LE, WHITESPACE,
                GE, WHITESPACE,
                RSH_EQ, WHITESPACE,
                LSH_EQ, WHITESPACE,
                AND_EQ, WHITESPACE,
                OR_EQ, WHITESPACE,
                XOR_EQ, WHITESPACE,
                IDENTIFIER);
        testLexerSource("-0.76", '-', INTEGER);
    }

}
