package de.fosd.typechef.crefactor.frontend;

import de.fosd.typechef.crefactor.backend.ASTPosition;
import de.fosd.typechef.crefactor.frontend.actions.refactor.Rename;
import de.fosd.typechef.crefactor.frontend.util.Selection;
import de.fosd.typechef.parser.c.Id;
import scala.collection.Iterator;
import scala.collection.immutable.List;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * Implements an ActionListener in order to display valid and possible refactorings in the context menu.
 *
 * @author Andreas Janker
 */
public class RefactorMenu implements MenuListener {

    /**
     * Reference to the current editor instance.
     */
    private Editor editor;

    /**
     * Reference to the current menu instance.
     */
    private JMenu menu;

    /**
     * Generates a new menulistener instance for a editor and a menu.
     *
     * @param editor the used editor instance.
     * @param menu   the menu
     */
    public RefactorMenu(final Editor editor, final JMenu menu) {
        this.editor = editor;
        this.menu = menu;
    }

    @Override
    public void menuSelected(MenuEvent menuEvent) {
        this.menu.removeAll();
        final Selection selection = new Selection(editor);

        // Retrieve all available ids
        final List<Id> selectedIDs = ASTPosition.getSelectedIDs(editor.getAST(),
                selection.getLineStart(), selection.getLineEnd(), selection.getRowStart(), selection.getRowEnd());
        if (!selectedIDs.isEmpty()) {
            addRenamingsToMenu(selectedIDs, this.menu);
        }

    }

    @Override
    public void menuDeselected(MenuEvent menuEvent) {
        // Just does nothing - not implemented
    }

    @Override
    public void menuCanceled(MenuEvent menuEvent) {
        // Just does nothing - not implemented
    }

    /**
     * Add possible renamings to menu.
     *
     * @param selectedIDs selected ids to rename
     * @param menu        the menu where to add the rename entries
     */
    private void addRenamingsToMenu(final List<Id> selectedIDs, final JMenu menu) {
        final Iterator<Id> it = selectedIDs.iterator();
        while (it.hasNext()) {
            menu.add(Rename.getRenameAction(this.editor, it.next()));
        }
    }
}
