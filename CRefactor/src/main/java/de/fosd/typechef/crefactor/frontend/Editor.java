package de.fosd.typechef.crefactor.frontend;

import de.fosd.typechef.crefactor.util.Configuration;
import de.fosd.typechef.parser.c.AST;
import de.fosd.typechef.parser.c.Id;
import de.fosd.typechef.typesystem.CEnv;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import scala.Tuple3;
import scala.collection.immutable.List;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.IdentityHashMap;

/**
 * The main editor window.
 */
public class Editor extends JFrame {

    /**
     * Reference to the textarea.
     */
    private RSyntaxTextArea textArea;

    /**
     * The analysed code by typechef.
     */
    private Tuple3<AST, IdentityHashMap<Id, List<Id>>, CEnv.Env> parsedCode;

    /**
     * The currently loaded file.
     */
    private File file;

    /**
     * Generates a new editor window instance.
     *
     * @param parsedCode the parsed Code by typechef, containing the ast and defUseMap
     */
    public Editor(final Tuple3<AST, IdentityHashMap<Id, List<Id>>, CEnv.Env> parsedCode) {
        super(Configuration.getInstance().getConfig("editor.title"));
        this.parsedCode = parsedCode;
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
     * Retrieves the current ast.
     *
     * @return the current ast.
     */
    public AST getAST() {
        return this.parsedCode._1();
    }

    /**
     * Retrieves the current defuse map.
     *
     * @return the current defuse map.
     */
    public IdentityHashMap<Id, List<Id>> getDefUseMap() {
        return this.parsedCode._2();
    }

    /**
     * Retrieves the current enviorement.
     *
     * @return the current enviorement
     */
    public CEnv.Env getEnv() {
        return this.parsedCode._3();
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
     * Updates the parsed code by typechef
     *
     * @param parsedCode the new parsedCode
     */
    public void updateParsedCode(final Tuple3<AST, IdentityHashMap<Id, List<Id>>, CEnv.Env> parsedCode) {
        this.parsedCode = parsedCode;
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
