package de.fosd.typechef.crefactor.gui.actions;

import de.fosd.typechef.crefactor.connector.CreateASTForCode;
import de.fosd.typechef.crefactor.gui.util.PositionWrapper;
import de.fosd.typechef.crewrite.Refactor;
import de.fosd.typechef.parser.c.AST;
import de.fosd.typechef.parser.c.Id;
import org.fife.ui.rtextarea.RTextArea;
import scala.collection.Iterator;
import scala.collection.immutable.List;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.util.IdentityHashMap;

/**
 * Implements an ActionListener in order to display valid and possible refactorings in the context menu.
 */
public class RefactorMenuActions implements MenuListener {

    private RTextArea textArea;
    private JMenu menu;

    public RefactorMenuActions(final RTextArea textArea, JMenu menu) {
        this.textArea = textArea;
        this.menu = menu;
    }

    @Override
    public void menuSelected(MenuEvent e) {
        this.menu.removeAll();
        addDefaultItems(this.textArea, this.menu);

        // retrive selection
        // TODO Refactor

        int lineStart = PositionWrapper.getLine(textArea, textArea.getSelectionStart());
        int columnStart = PositionWrapper.getRow(textArea, textArea.getSelectionStart());
        int lineEnd = PositionWrapper.getLine(textArea, textArea.getSelectionEnd());
        int columnEnd = PositionWrapper.getRow(textArea, textArea.getSelectionEnd());
        List<AST> selection = CreateASTForCode.extendedPosAnalyse(columnStart, columnEnd, lineStart + 1, lineEnd + 1);
        IdentityHashMap<Id, List<Id>> defuse = CreateASTForCode.getDefUseMap();
        Refactor refactor = new Refactor();
        List<Id> possibleRefactorings = refactor.getPossibleRenameFunctionIDs(defuse, selection);

        for (Iterator<Id> it = possibleRefactorings.iterator(); it.hasNext(); ) {
            Id id = it.next();
            this.menu.add(new JMenuItem(TypeChefActions.renameFunction(this.textArea, defuse, id)));
        }
    }

    @Override
    public void menuDeselected(MenuEvent e) {
        // Just does nothing - not implemented
    }

    @Override
    public void menuCanceled(MenuEvent e) {
        // Just does nothing - not implemented
    }

    private static void addDefaultItems(final RTextArea textArea, final JMenu refactorMenu) {
        refactorMenu.add(new JMenuItem(TypeChefActions.getAST(textArea)));
        refactorMenu.add(new JMenuItem(TypeChefActions.getElementsAtPosition(textArea)));
        refactorMenu.add(new JMenuItem(TypeChefActions.showPrettyAST(textArea)));
        // refactorMenu.add(new JMenuItem(TypeChefActions.getElementsAtPosition(textArea)));
    }
}
