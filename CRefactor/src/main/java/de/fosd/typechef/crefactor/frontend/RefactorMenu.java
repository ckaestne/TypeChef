package de.fosd.typechef.crefactor.frontend;

import de.fosd.typechef.conditional.Opt;
import de.fosd.typechef.crefactor.Morpheus;
import de.fosd.typechef.crefactor.backend.ASTPosition;
import de.fosd.typechef.crefactor.backend.Cache;
import de.fosd.typechef.crefactor.backend.refactor.ExtractFunction;
import de.fosd.typechef.crefactor.backend.refactor.InlineFunction;
import de.fosd.typechef.crefactor.frontend.actions.refactor.ExtractFunctionActions;
import de.fosd.typechef.crefactor.frontend.actions.refactor.RefactorAction;
import de.fosd.typechef.crefactor.frontend.actions.refactor.RenameActions;
import de.fosd.typechef.crefactor.frontend.util.Selection;
import de.fosd.typechef.crefactor.util.Configuration;
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
        final Morpheus morpheus = this.editor.getMorpheus();


        /**
         * Refactor Renaming
         */

        // Retrieve all available ids - Requiered for renamings
        final List<Id> selectedIDs = ASTPosition.getSelectedIDs(Cache.getAST(), editor.getFile().getAbsolutePath(),
                selection.getLineStart(), selection.getLineEnd(), selection.getRowStart(), selection.getRowEnd());
        if (!selectedIDs.isEmpty()) {
            final JMenu rename = new JMenu(Configuration.getInstance().getConfig("refactor.rename.name"));
            this.menu.add(rename);
            addRenamingsToMenu(selectedIDs, rename);
        }

        /**
         * Refactor Extract Method
         */
        final List<Opt<?>> selectedAST = ASTPosition.getSelectedOpts(Cache.getAST(), Cache.getASTEnv(), editor.getFile().getAbsolutePath(),
                selection.getLineStart(), selection.getLineEnd(), selection.getRowStart(), selection.getRowEnd());
        // List<AST> selectedElements = ASTPosition.getSelectedExprOrStatements(Cache.getAST(), Cache.getASTEnv(), editor.getFile().getAbsolutePath(),
        //        selection.getLineStart(), selection.getLineEnd(), selection.getRowStart(), selection.getRowEnd());
        // final boolean extractionPossible = ExtractMethod.refactorIsPossible(selectedElements, Cache.getAST(), Cache.getASTEnv(), Cache.getDeclUseMap(), Cache.getUseDeclMap(), "newFunc");
        final boolean eligable = ExtractFunction.isEligableForExtraction(selectedAST, Cache.getASTEnv());
        if (this.menu.getComponentCount() != 0) {
            this.menu.add(new JSeparator());
        }
        final JMenuItem extract = new JMenuItem(ExtractFunctionActions.getExtractFunctionAction(editor, selectedAST));
        this.menu.add(extract);
        extract.setEnabled(eligable);

        /**
         * Inline function
         */
        /*
        Morpheus morpheus = new Morpheus(Cache.getAST());
        long time = System.currentTimeMillis();
        final List<Id> availableIdentifiers = InlineFunction.getAvailableIdentifiers(morpheus.getAST(), morpheus.getASTEnv(), selection);
        AST inlinevar = InlineFunction.inline(morpheus, availableIdentifiers.head(), true);
        System.out.println("Duration1: " + (System.currentTimeMillis() - time));
        editor.getRTextArea().setText(PrettyPrinter.print(inlinevar));
        System.out.println("Duration2: " + (System.currentTimeMillis() - time));
        **/

        /**
         * Inline Function
         */
        final List<Id> availableIdentifiers = InlineFunction.getAvailableIdentifiers(morpheus.getAST(), morpheus.getASTEnv(), selection);
        if (!availableIdentifiers.isEmpty()) {
            final JMenu inline = new JMenu(Configuration.getInstance().getConfig("refactor.inline.name"));
            this.menu.add(inline);
            addInliningToMenu(availableIdentifiers, inline);
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
            menu.add(RenameActions.getRenameAction(this.editor, it.next()));
        }
    }

    private void addInliningToMenu(final List<Id> selectedIDs, final JMenu menu) {
        final Iterator<Id> it = selectedIDs.iterator();
        while (it.hasNext()) {
            menu.add(RefactorAction.getInlineFunction(editor, it.next()));
        }

    }
}
