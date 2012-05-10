package de.fosd.typechef.crefactor.gui;

import de.fosd.typechef.crefactor.gui.actions.TypeChefActions;
import org.fife.ui.rtextarea.RTextArea;

import javax.swing.*;

/**
 * Factory class for extending the default popupmenu.
 */
public final class PopupMenuFactory {

    /**
     * Name of the refactoring menu
     */
    public static final String REFACTOR_MENU_NAME = "Refactor";

    /**
     * Extends the default popupmenu
     *
     * @param menu the menu to extend
     * @return the extended popupmenu
     */
    public static JPopupMenu extendPopUpMenu(JPopupMenu menu, final RTextArea textArea) {
        if (menu == null) {
            menu = new JPopupMenu();
        }
        menu.addSeparator();
        menu.add(getRefactorMenu(textArea));
        return menu;
    }

    /**
     * Retrieves a refactor menu.
     *
     * @return a refactor menu.
     */
    private static JMenu getRefactorMenu(final RTextArea textArea) {
        JMenu refactorMenu = new JMenu(REFACTOR_MENU_NAME);
        refactorMenu.add(new JMenuItem(TypeChefActions.getAST(textArea)));
        refactorMenu.add(new JMenuItem(TypeChefActions.getPrettyAST(textArea)));
        return refactorMenu;
    }

}
