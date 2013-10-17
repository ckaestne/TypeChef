package de.fosd.typechef.parser.javascript.rhino;

/**
 * Created with IntelliJ IDEA.
 * User: ckaestne
 * Date: 10/17/13
 * Time: 1:46 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Parser {
    void addError(String s);

    void addWarning(String s, String s1);

    void reportError(String s);
}
