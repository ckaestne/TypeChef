package de.fosd.typechef.crefactor.gui.actions;

import de.fosd.typechef.crefactor.connector.CreateASTForCode;
import de.fosd.typechef.crefactor.connector.CreateASTForCode$;
import de.fosd.typechef.crefactor.gui.actions.util.InputBox;
import de.fosd.typechef.crefactor.gui.util.PositionWrapper;
import de.fosd.typechef.crewrite.ASTRefactor;
import de.fosd.typechef.parser.c.Id;
import org.fife.ui.rtextarea.RTextArea;
import scala.collection.immutable.List;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.IdentityHashMap;

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

                try {
                    String ast = CreateASTForCode$.MODULE$.analyse();

                    displayModalTextArea("AST", ast);


                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        };
        return action;
    }

    public static Action showPrettyAST(final RTextArea textArea) {
        Action action = new AbstractAction() {
            {
                putValue(Action.NAME, "Zeige Code");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText(CreateASTForCode.prettyAnalyse());
            }
        };
        return action;
    }

    public static Action getElementsAtPosition(final RTextArea textArea) {
        Action action = new AbstractAction() {
            {
                putValue(Action.NAME, "Rename Function");
            }

            @Override
            public void actionPerformed(ActionEvent e) {

                if (textArea.getSelectionStart() == textArea.getSelectionEnd()) {
                    // nothing to do
                    // TODO: display message
                    return;
                }

                int lineStart = PositionWrapper.getLine(textArea, textArea.getSelectionStart());
                int columnStart = PositionWrapper.getRow(textArea, textArea.getSelectionStart());
                int lineEnd = PositionWrapper.getLine(textArea, textArea.getSelectionEnd());
                int columnEnd = PositionWrapper.getRow(textArea, textArea.getSelectionEnd());

                CreateASTForCode.extendedPosAnalyse(columnStart, columnEnd, lineStart + 1, lineEnd + 1);


            }
        };
        return action;
    }

    public static Action getElementAtPosition(final RTextArea textArea) {
        Action action = new AbstractAction() {
            {
                putValue(Action.NAME, "Analysiere Element");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textArea.getText();
                int column = textArea.getCaretOffsetFromLineStart();
                int line = textArea.getCaretLineNumber() + 1;
                if (text == null) {
                    return;
                }
                try {
                    String ast = CreateASTForCode.positionAnalyse(column, line);
                    displayModalTextArea("Analyse", ast);


                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        };
        return action;
    }

    public static Action renameFunction(final IdentityHashMap<Id, List<Id>> defuse, final Id id) {
        Action action = new AbstractAction() {

            {
                putValue(Action.NAME, "Rename " + id.name() + "()");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                ASTRefactor refactor = new ASTRefactor();
                InputBox box = new InputBox(id.name());
                String rename = box.getInput();
                if ((rename != null) && (rename.trim().length() > 1)) {
                    refactor.renameFunction(CreateASTForCode.ast(), defuse, rename, id);
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
