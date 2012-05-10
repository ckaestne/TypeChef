package de.fosd.typechef.crefactor.gui.actions;

import org.fife.ui.rtextarea.RTextArea;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * All used file actions
 * User: andi
 * Date: 16.04.12
 * Time: 09:38
 */
public class FileActions {

    /**
     * Shows a new FileChooser and loads the file.
     *
     * @param textArea
     * @return the action
     */
    public static Action openAction(final RTextArea textArea) {
        Action action = new AbstractAction() {
            {
                putValue(Action.NAME, "Öffnen");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setApproveButtonText("Öffnen");
                String filePath = null;
                if (fc.showOpenDialog(textArea) == JFileChooser.APPROVE_OPTION) {
                    filePath = fc.getSelectedFile().getAbsolutePath();
                }
                if (filePath != null) {
                    loadFile(textArea, filePath);
                }
            }
        };
        return action;
    }

    public static Action saveAction(final RTextArea textArea) {
        Action action = new AbstractAction() {

            {
                putValue(Action.NAME, "Speichern");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setApproveButtonText("Speichern");
                String filePath = null;
                if (fc.showSaveDialog(textArea) == JFileChooser.APPROVE_OPTION) {
                    filePath = fc.getSelectedFile().getAbsolutePath();
                }

                if (filePath != null) {
                    saveFile(textArea, filePath);
                }
            }
        };
        return action;
    }

    /**
     * Saves the content of the textarea to a file.
     *
     * @param textArea
     * @param filePath
     */
    private static void saveFile(final RTextArea textArea, final String filePath) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filePath));
            textArea.write(writer);
        } catch (Exception e) {
            e.printStackTrace();
            UIManager.getLookAndFeel().provideErrorFeedback(textArea);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Loads the input of a file into the textarea.
     *
     * @param textArea the textarea to load into
     * @param filePath the path of the file.
     */
    private static void loadFile(final RTextArea textArea, final String filePath) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            textArea.read(reader, null);
        } catch (Exception e) {
            e.printStackTrace();
            UIManager.getLookAndFeel().provideErrorFeedback(textArea);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

