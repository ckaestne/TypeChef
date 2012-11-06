package de.fosd.typechef.crefactor.frontend;

import de.fosd.typechef.conditional.Opt;
import de.fosd.typechef.crefactor.backend.ASTPosition;
import de.fosd.typechef.crefactor.backend.Connector;
import de.fosd.typechef.crefactor.backend.refactor.ExtractFunction;
import de.fosd.typechef.crefactor.frontend.actions.refactor.Rename;
import de.fosd.typechef.crefactor.frontend.util.Selection;
import de.fosd.typechef.crefactor.util.Configuration;
import de.fosd.typechef.parser.c.*;
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

        // Retrieve all available ids - Requiered for renamings
        final List<Id> selectedIDs = ASTPosition.getSelectedIDs(Connector.getAST(), editor.getFile().getAbsolutePath(),
                selection.getLineStart(), selection.getLineEnd(), selection.getRowStart(), selection.getRowEnd());
        if (!selectedIDs.isEmpty()) {
            JMenu rename = new JMenu(Configuration.getInstance().getConfig("refactor.rename.name"));
            this.menu.add(rename);
            addRenamingsToMenu(selectedIDs, rename);
        }

        // TODO Sweet it up!
        List<Opt<?>> selectedAST = ASTPosition.getSelectedOpts(Connector.getAST(), Connector.getASTEnv(), editor.getFile().getAbsolutePath(),
                selection.getLineStart(), selection.getLineEnd(), selection.getRowStart(), selection.getRowEnd());
        List<Statement> selectedStatements = ASTPosition.getSelectedStatements(Connector.getAST(), Connector.getASTEnv(), editor.getFile().getAbsolutePath(),
                selection.getLineStart(), selection.getLineEnd(), selection.getRowStart(), selection.getRowEnd());
        FunctionDef parentFunc = ExtractFunction.getParentFunction(selectedAST, Connector.getASTEnv());
        List<Opt<Specifier>> specs = ExtractFunction.generateSpecifiers(parentFunc, Connector.getASTEnv());
        List<Opt<DeclaratorExtension>> declExt = ExtractFunction.generateParameter(ExtractFunction.getParentFunction(selectedAST, Connector.getASTEnv()), ExtractFunction.getParameterIds(ExtractFunction.getAllUsedIds(selectedAST), Connector.getDefUseMap()), Connector.getASTEnv(), Connector.getDefUseMap());
        Declarator decl = ExtractFunction.generateDeclarator("func", declExt);
        CompoundStatement cs = ExtractFunction.generateCompoundStatement(selectedStatements, Connector.getASTEnv());
        Opt<FunctionDef> newFunc = ExtractFunction.generateFuncOpt(parentFunc, ExtractFunction.generateFuncDef(specs, decl, cs), Connector.getASTEnv());
        System.out.println("function " + newFunc);
        System.out.println(PrettyPrinter.print(newFunc.entry()));
        System.out.println("insert");
        System.out.println(PrettyPrinter.print(ExtractFunction.insertNewFunction(parentFunc, newFunc, Connector.getAST(), Connector.getASTEnv())));
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
