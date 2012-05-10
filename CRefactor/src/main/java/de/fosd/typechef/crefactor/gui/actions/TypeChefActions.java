package de.fosd.typechef.crefactor.gui.actions;

import de.fosd.typechef.crefactor.connector.CreateASTForCode;
import org.fife.ui.rtextarea.RTextArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * All actions to interact with TypeChef are found in this class
 */
public class TypeChefActions {

    public static Action getAST(final RTextArea textArea) {
        Action action = new AbstractAction() {
            {
                putValue(Action.NAME, "Zeige AST");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedText = textArea.getSelectedText();
                if (selectedText == null) {
                    return;
                }
                try {
                    CreateASTForCode connector = new CreateASTForCode(selectedText);
                    String ast = connector.analyse();
                    displayModalTextArea("AST", ast);


                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        };
        return action;
    }

    public static Action getPrettyAST(final RTextArea textArea) {
        Action action = new AbstractAction() {
            {
                putValue(Action.NAME, "Analysiere AST");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedText = textArea.getSelectedText();
                if (selectedText == null) {
                    return;
                }
                try {
                    CreateASTForCode connector = new CreateASTForCode(selectedText);
                    String ast = connector.prettyAnalyse();
                    displayModalTextArea("Analyse", ast);


                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        };
        return action;
    }

    /**
     * Displays a modal text area box with a title and a text.
     *
     * @param title the title to display
     * @param text  the text to display
     */
    private static void displayModalTextArea(final String title, final String text) {
        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setModal(true);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.setSize(new Dimension(480, 320));
        dialog.setLocationRelativeTo(null);

        JScrollPane textPane = new JScrollPane(buildResultTextArea(text), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        dialog.getContentPane().add(textPane, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    /**
     * Builds a new text area with a certain text as input
     *
     * @param text the text to display
     */
    private static JTextArea buildResultTextArea(final String text) {
        JTextArea resultTextarea = new JTextArea();
        resultTextarea.setColumns(20);
        resultTextarea.setLineWrap(true);
        resultTextarea.setRows(5);
        resultTextarea.setWrapStyleWord(false);
        resultTextarea.setEditable(false);
        resultTextarea.setText(text);
        return resultTextarea;
    }
}
