package de.fosd.typechef.crefactor.frontend.util;

import de.fosd.typechef.crefactor.frontend.Editor;

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
        return PositionWrapper.getLine(editor.getRTextArea(), editor.getRTextArea().getSelectionStart()) + 1;
    }

    /**
     * Retrieves the ending line of the current selection.
     *
     * @return the ending line of the current selection.
     */
    public int getLineEnd() {
        return PositionWrapper.getLine(editor.getRTextArea(), editor.getRTextArea().getSelectionEnd()) + 1;
    }

    /**
     * Retrieves the starting row of the current selection.
     *
     * @return the starting row of the current selection.
     */
    public int getRowStart() {
        return PositionWrapper.getRow(editor.getRTextArea(), editor.getRTextArea().getSelectionStart());
    }

    /**
     * Retrieves the ending row of the current selection.
     *
     * @return the ending row of the current selection.
     */
    public int getRowEnd() {
        return PositionWrapper.getRow(editor.getRTextArea(), editor.getRTextArea().getSelectionEnd());
    }
}
