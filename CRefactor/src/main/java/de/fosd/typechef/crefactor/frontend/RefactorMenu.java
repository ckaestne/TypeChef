package de.fosd.typechef.crefactor.frontend;

import de.fosd.typechef.conditional.Opt;
import de.fosd.typechef.crefactor.Morpheus;
import de.fosd.typechef.crefactor.backend.ASTPosition;
import de.fosd.typechef.crefactor.backend.Cache;
import de.fosd.typechef.crefactor.backend.refactor.ExtractFunction;
import de.fosd.typechef.crefactor.backend.refactor.InlineFunction;
import de.fosd.typechef.crefactor.backend.refactor.RenameIdentifier;
import de.fosd.typechef.crefactor.frontend.actions.refactor.ExtractFunctionActions;
import de.fosd.typechef.crefactor.frontend.actions.refactor.RefactorAction;
import de.fosd.typechef.crefactor.frontend.util.Selection;
import de.fosd.typechef.crefactor.util.Configuration;
import de.fosd.typechef.parser.c.AST;
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

        // TODO Apply a nice design pattern

        /**
         * Refactor Renaming
         */
        final List<Id> availableIds = RenameIdentifier.getAvailableIdentifiers(morpheus.getAST(), morpheus.getASTEnv(), selection);
        if (!availableIds.isEmpty()) {
            final JMenu rename = new JMenu(Configuration.getInstance().getConfig("refactor.rename.name"));
            this.menu.add(rename);
            addRenamingsToMenu(availableIds, rename);
        }

        /**
         * Refactor Extract Method
         */
        final List<Opt<?>> selectedAST = ASTPosition.getSelectedOpts(Cache.getAST(), Cache.getASTEnv(), morpheus.getFile().getAbsolutePath(),
                selection.getLineStart(), selection.getLineEnd(), selection.getRowStart(), selection.getRowEnd());
        List<AST> selectedElements = ASTPosition.getSelectedExprOrStatements(Cache.getAST(), Cache.getASTEnv(), morpheus.getFile().getAbsolutePath(),
                selection.getLineStart(), selection.getLineEnd(), selection.getRowStart(), selection.getRowEnd());
        // System.out.println("Selection:" + selectedElements.toString());
        // final boolean extractionPossible = ExtractMethod.refactorIsPossible(selectedElements, Cache.getAST(), Cache.getASTEnv(), Cache.getDeclUseMap(), Cache.getUseDeclMap(), "newFunc");
        final boolean eligable = ExtractFunction.isEligableForExtraction(selectedAST, Cache.getASTEnv());
        if (this.menu.getComponentCount() != 0) {
            this.menu.add(new JSeparator());
        }
        final JMenuItem extract = new JMenuItem(ExtractFunctionActions.getExtractFunctionAction(editor, selectedAST));
        this.menu.add(extract);
        extract.setEnabled(eligable);

        /**
         * Inline Function
         */
        final List<Id> availableFuncIDs = InlineFunction.getAvailableIdentifiers(morpheus.getAST(), morpheus.getASTEnv(), selection);
        if (!availableFuncIDs.isEmpty()) {
            final JMenu inline = new JMenu(Configuration.getInstance().getConfig("refactor.inline.name"));
            this.menu.add(inline);
            addInliningToMenu(availableFuncIDs, inline);
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
            menu.add(RefactorAction.getRenameAction(this.editor.getMorpheus(), it.next()));
        }
    }

    private void addInliningToMenu(final List<Id> selectedIDs, final JMenu menu) {
        final Iterator<Id> it = selectedIDs.iterator();
        while (it.hasNext()) {
            menu.add(RefactorAction.getInlineFunction(this.editor.getMorpheus(), it.next()));
        }
    }
}
