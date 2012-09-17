package de.fosd.typechef.crefactor.frontend;

import de.fosd.typechef.crefactor.util.Configuration;

import javax.swing.*;

/**
 * Factory class for extending the default popup menu.
 *
 * @author Andreas Janker
 */
public class PopupMenuFactory {

    /**
     * Adds to a given JPopupMenu a menu entry for refactorings.
     *
     * @param menu   the given JPopupMenu
     * @param editor the used editor
     * @return menu extended with an entry for refactorings
     */
    public static JPopupMenu addRefactorMenuToPopupMenu(final JPopupMenu menu, final Editor editor) {
        if (menu == null) {
            return null;
        }
        menu.addSeparator();
        menu.add(getRefactorMenu(editor));
        return menu;
    }

    public static JMenu getRefactorMenu(final Editor editor) {
        final JMenu refactorMenu = new JMenu(Configuration.getInstance().getConfig("refactor.name"));
        refactorMenu.addMenuListener(new RefactorMenu(editor, refactorMenu));
        return refactorMenu;
    }
}
