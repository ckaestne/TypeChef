package de.fosd.typechef.crefactor.frontend.util;

import de.fosd.typechef.crefactor.frontend.Editor;

import javax.swing.text.BadLocationException;

/**
 * Selection object for storing the current selection start and end.
 *
 * @author Andreas Janker
 */
public class Selection {

    /**
     * The currently referenced editor.
     */
    private Editor editor;

    /**
     * Generates a new selection object for a given editor.
     *
     * @param editor the editor instance.
     */
    public Selection(final Editor editor) {
        this.editor = editor;
    }

    /**
     * Retrieves the starting line of the current selection.
     *
     * @return the starting line of the current selection.
     */
    public int getLineStart() {
        return PositionWrapper.getLine(editor.getRTextArea(),
                previousSymbolOccurrence(editor.getRTextArea().getSelectionStart())) + 1;
    }

    /**
     * Retrieves the ending line of the current selection.
     *
     * @return the ending line of the current selection.
     */
    public int getLineEnd() {
        return PositionWrapper.getLine(editor.getRTextArea(),
                nextSymblOccurrence(editor.getRTextArea().getSelectionEnd())) + 1;
    }

    /**
     * Retrieves the starting row of the current selection.
     *
     * @return the starting row of the current selection.
     */
    public int getRowStart() {
        return PositionWrapper.getRow(editor.getRTextArea(),
                previousSymbolOccurrence(editor.getRTextArea().getSelectionStart()));
    }

    /**
     * Retrieves the ending row of the current selection.
     *
     * @return the ending row of the current selection.
     */
    public int getRowEnd() {
        return PositionWrapper.getRow(editor.getRTextArea(),
                nextSymblOccurrence(editor.getRTextArea().getSelectionEnd()));
    }

    /**
     * Retrieves the previous offset where a symbol occurs
     *
     * @param offset the offstet to start
     * @return the previous offset where a symbol occurs
     */
    public int previousSymbolOccurrence(final int offset) {
        final int step = -1;
        return findSymbolOccurrence(offset, step);
    }

    /**
     * Retrieves the next offset where a symbol occurs.
     *
     * @param offset the offset to start
     * @return the next offset where a symbol occurs
     */
    public int nextSymblOccurrence(final int offset) {
        final int step = 1;
        return findSymbolOccurrence(offset, step);
    }

    /**
     * Retrieves the path of the file loaded into the editor
     *
     * @return the absolute filepath
     */
    public String getFilePath() {
        return editor.getMorpheus().getFile().getAbsolutePath();
    }

    /**
     * Finds the offset where as in a certain step a symbol occurs
     *
     * @param offset the start offset
     * @param step   the step size
     * @return the found offset
     */
    private int findSymbolOccurrence(final int offset, final int step) {
        try {
            final int nextOffset = offset + step;
            final String symbol = editor.getRTextArea().getText(nextOffset, 1);
            if (symbol.matches("\\s")) {
                return findSymbolOccurrence(nextOffset, step);
            }
            return nextOffset;
        } catch (final BadLocationException e) {
            return offset;
        }
    }
}
