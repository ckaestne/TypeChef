package de.fosd.typechef.crefactor.frontend.actions.refactor;

import de.fosd.typechef.crefactor.frontend.Editor;
import de.fosd.typechef.crefactor.util.Configuration;
import de.fosd.typechef.parser.c.PrettyPrinter;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * All actions with code or ast analyse purpose are found here.
 */
public class Analyse {

    private static String cache = null;

    private static Boolean prettyPrintActive = false;

    private static Boolean astDisplayActive = false;

    /**
     * Pretty prints the current ast in the editor window
     *
     * @param editor current editor
     * @return the action
     */
    public static Action getPrettyPrintASTAction(final Editor editor) {

        final Action action = new AbstractAction() {

            {
                // setEnabled(!prettyPrintActive);
                putValue(Action.NAME, Configuration.getInstance().getConfig("refactor.displayPrettyAST"));
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cache == null) {
                    cache = editor.getRTextArea().getText();
                }
                prettyPrintActive = true;
                astDisplayActive = false;
                editor.getRTextArea().setText(PrettyPrinter.print(editor.getMorpheus().getAST()));
            }
        };
        return action;
    }

    /**
     * Pretty prints the current ast in the editor window
     *
     * @param editor current editor
     * @return the action
     */
    public static Action getPrintASTAction(final Editor editor) {

        final Action action = new AbstractAction() {

            {
                // setEnabled(!astDisplayActive);
                putValue(Action.NAME, Configuration.getInstance().getConfig("refactor.displayAST"));
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cache == null) {
                    cache = editor.getRTextArea().getText();
                }
                astDisplayActive = true;
                prettyPrintActive = false;
                editor.getRTextArea().setText(editor.getMorpheus().getAST().toString());
            }
        };
        return action;
    }

    /**
     * Pretty prints the current ast in the editor window
     *
     * @param editor current editor
     * @return the action
     */
    public static Action getPrintOriginalAction(final Editor editor) {

        final Action action = new AbstractAction() {

            {
                // setEnabled((astDisplayActive || prettyPrintActive));
                putValue(Action.NAME, Configuration.getInstance().getConfig("refactor.displayOrigin"));
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                astDisplayActive = false;
                prettyPrintActive = false;
                if (cache != null) {
                    editor.getRTextArea().setText(cache);
                }
            }
        };
        return action;
    }

    // TODO Action for displaying AST
}
