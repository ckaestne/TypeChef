/*******************************************************************************
* SAT4J: a SATisfiability library for Java Copyright (C) 2004-2008 Daniel Le Berre
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Alternatively, the contents of this file may be used under the terms of
* either the GNU Lesser General Public License Version 2.1 or later (the
* "LGPL"), in which case the provisions of the LGPL are applicable instead
* of those above. If you wish to allow use of your version of this file only
* under the terms of the LGPL, and not to allow others to use your version of
* this file under the terms of the EPL, indicate your decision by deleting
* the provisions above and replace them with the notice and other provisions
* required by the LGPL. If you do not delete the provisions above, a recipient
* may use your version of this file under the terms of the EPL or the LGPL.
* 
* Based on the original MiniSat specification from:
* 
* An extensible SAT solver. Niklas Een and Niklas Sorensson. Proceedings of the
* Sixth International Conference on Theory and Applications of Satisfiability
* Testing, LNCS 2919, pp 502-518, 2003.
*
* See www.minisat.se for the original solver in C++.
* 
*******************************************************************************/
package org.sat4j.reader;

/**
 * Exception launched when there is a problem during parsing.
 * 
 * @author leberre
 * @see Reader
 */
public class ParseFormatException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor for ParseFormatException.
     */
    public ParseFormatException() {
        super("DIMACS Format Error");
    }

    /**
     * Constructor for ParseFormatException.
     * 
     * @param message
     *            the error message
     */
    public ParseFormatException(String message) {
        super("DIMACS Format error: " + message);
    }

    /**
     * Constructor for ParseFormatException.
     * 
     * @param message
     *            the error message
     * @param cause
     *            the cause of the exception
     */
    public ParseFormatException(String message, Throwable cause) {
        super("DIMACS Format error: " + message, cause);
    }

    /**
     * Constructor for ParseFormatException.
     * 
     * @param cause
     *            the cause of the exception
     */
    public ParseFormatException(Throwable cause) {
        super(cause);
    }

}
