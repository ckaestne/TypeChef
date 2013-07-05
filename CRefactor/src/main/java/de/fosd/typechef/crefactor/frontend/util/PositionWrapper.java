package de.fosd.typechef.crefactor.frontend.util;


import org.fife.ui.rtextarea.RTextArea;

import javax.swing.text.BadLocationException;

/**
 * Helping class for converting the caret position to line and row positions
 */
public class PositionWrapper {

    public static final int INVALID_POSITION = -1;

    /**
     * Gets the line of certain offset
     *
     * @param textArea the current textarea
     * @param offset   the current offset
     * @return the line number
     */
    public final static int getLine(final RTextArea textArea, final int offset) {
        try {
            return textArea.getLineOfOffset(offset);
        } catch (BadLocationException e) {
            // never happens
            return INVALID_POSITION;
        }
    }

    /**
     * Gets the row of a certain offset
     *
     * @param textArea the current textarea
     * @param offset   the current offset
     * @return the row
     */
    public final static int getRow(final RTextArea textArea, final int offset) {
        int line = getLine(textArea, offset);
        try {
            return offset - textArea.getLineStartOffset(line);
        } catch (BadLocationException e) {
            // never happens
            return INVALID_POSITION;
        }
    }
}
