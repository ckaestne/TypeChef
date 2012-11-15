package de.fosd.typechef.crefactor.frontend.actions.refactor;

import de.fosd.typechef.crefactor.backend.Connector;
import de.fosd.typechef.crefactor.frontend.Editor;
import de.fosd.typechef.crefactor.util.Configuration;
import de.fosd.typechef.parser.c.PrettyPrinter;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * All actions with code or ast analyse purpose are found here.
 */
public class Analyse {

    /**
     * Pretty prints the current ast in the editor window
     *
     * @param editor current editor
     * @return the action
     */
    public static Action getPrettyPrintASTAction(final Editor editor) {

        final Action action = new AbstractAction() {

            {
                putValue(Action.NAME, Configuration.getInstance().getConfig("refactor.displayPrettyAST"));
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                editor.getRTextArea().setText(PrettyPrinter.print(Connector.getAST()));
            }
        };
        return action;
    }

    // TODO Action for displaying AST
}
