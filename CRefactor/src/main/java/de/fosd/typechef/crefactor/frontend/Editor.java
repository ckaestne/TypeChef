package de.fosd.typechef.crefactor.frontend;

import de.fosd.typechef.crefactor.util.Configuration;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * The main editor window.
 */
public class Editor extends JFrame {

    /**
     * Reference to the textarea.
     */
    private RSyntaxTextArea textArea;

    /**
     * The currently loaded file.
     */
    private File file;

    /**
     * Generates a new editor window instance.
     */
    public Editor() {
        super(Configuration.getInstance().getConfig("editor.title"));

        JPanel contentPane = new JPanel(new BorderLayout());

        // make textarea
        this.textArea = new RSyntaxTextArea(Configuration.getInstance().getConfigAsInt("editor.rows"),
                Configuration.getInstance().getConfigAsInt("editor.columns"));
        this.textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);
        this.textArea.setWhitespaceVisible(true);
        this.textArea.setCodeFoldingEnabled(true);
        this.textArea.setAntiAliasingEnabled(true);
        this.textArea.setEditable(false);

        // Enable Scrolling
        RTextScrollPane scrollPane = new RTextScrollPane(textArea);
        scrollPane.setFoldIndicatorEnabled(true);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        setContentPane(contentPane);

        // make context menu
        JPopupMenu menu = this.textArea.getPopupMenu();
        PopupMenuFactory.addRefactorMenuToPopupMenu(menu, this);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationByPlatform(true);
        setLocationRelativeTo(null);
    }

    /**
     * Loads a file into the editor window.
     *
     * @param file the file to load into the editor window
     */
    public void loadFileInEditor(final File file) {
        if (!file.isFile()) {
            return;
        }

        BufferedReader reader = null;
        this.file = file;
        try {
            reader = new BufferedReader(new FileReader(file));
            this.textArea.read(reader, null);
        } catch (Exception e) {
            e.printStackTrace();
            UIManager.getLookAndFeel().provideErrorFeedback(this.textArea);
        } finally {
            org.apache.commons.io.IOUtils.closeQuietly(reader);
        }
    }

    /**
     * Retrieves the editor's textarea.
     *
     * @return the editor's textarea
     */
    public RTextArea getRTextArea() {
        return this.textArea;
    }


    /**
     * Retrieves the currently used file.
     *
     * @return the currently used file.
     */
    public File getFile() {
        return this.file;
    }
}
