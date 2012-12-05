package de.fosd.typechef.crefactor.frontend.actions.refactor;

import de.fosd.typechef.crefactor.backend.Connector;
import de.fosd.typechef.crefactor.backend.refactor.Renaming;
import de.fosd.typechef.crefactor.frontend.Editor;
import de.fosd.typechef.crefactor.frontend.util.InputBox;
import de.fosd.typechef.crefactor.util.Configuration;
import de.fosd.typechef.parser.c.AST;
import de.fosd.typechef.parser.c.Id;
import de.fosd.typechef.parser.c.PrettyPrinter;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * All actions required for renaming are found here.
 */
public class RenameActions {

    /**
     * Retrieves the action for renaming a variable or function
     *
     * @param editor the current editor instance
     * @param id     the id to rename
     * @return the action
     */
    public static Action getRenameAction(final Editor editor, final Id id) {
        return new AbstractAction() {

            {
                putValue(Action.NAME, id.name() + " " + Configuration.getInstance().getConfig("refactor.rename"));
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final InputBox box = new InputBox();
                box.createAndShowInputBox(Configuration.getInstance().getConfig("refactor.rename.name"),
                        Configuration.getInstance().getConfig("refactor.rename.newName"), id.name());
                if (box.getInput() != null) {
                    // Verify Input
                    if (!Renaming.validName(box.getInput())) {
                        JOptionPane.showMessageDialog(
                                null, Configuration.getInstance().getConfig("default.error.invalidName"), Configuration.getInstance().getConfig("default.error"), JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if (!Renaming.renamingIsPossible(
                            Connector.getAST(), Connector.getDeclUseMap(), box.getInput(), id)) {
                        JOptionPane.showMessageDialog(
                                null, "Umbenennen nicht möglich!", Configuration.getInstance().getConfig("default.error"), JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    AST refactored = Renaming.renameId(Connector.getAST(), Connector.getDeclUseMap(), Connector.getUseDeclMap(), box.getInput(), id);
                    Connector.update(refactored);
                    // Pretty Print :)
                    editor.getRTextArea().setText(PrettyPrinter.print(Connector.getAST()));
                    // TODO Verification!
                }
            }
        };
    }
}
