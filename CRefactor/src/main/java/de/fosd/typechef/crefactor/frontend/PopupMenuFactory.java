package de.fosd.typechef.crefactor.frontend;

import de.fosd.typechef.crefactor.frontend.actions.refactor.Analyse;
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

        final Action prettyPrint = Analyse.getPrettyPrintASTAction(editor);
        final JMenuItem prettyPrintMenu = new JMenuItem(prettyPrint);
        prettyPrintMenu.setEnabled(prettyPrint.isEnabled());
        menu.add(prettyPrintMenu);

        final Action printAST = Analyse.getPrintASTAction(editor);
        final JMenuItem printASTMenu = new JMenuItem(printAST);
        printASTMenu.setEnabled(printAST.isEnabled());
        menu.add(printASTMenu);

        final Action original = Analyse.getPrintOriginalAction(editor);
        final JMenuItem originalMenu = new JMenuItem(original);
        originalMenu.setEnabled(original.isEnabled());
        menu.add(originalMenu);

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
