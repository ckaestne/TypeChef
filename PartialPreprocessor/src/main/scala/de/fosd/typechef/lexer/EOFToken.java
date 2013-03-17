package de.fosd.typechef.lexer;

/**
 * Created with IntelliJ IDEA.
 * User: ckaestne
 * Date: 3/17/13
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class EOFToken extends SimpleToken {
    public EOFToken() {
        super(Token.EOF, 0, 0, "EOF", null);
    }
}
