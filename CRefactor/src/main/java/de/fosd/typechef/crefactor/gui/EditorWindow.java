package de.fosd.typechef.crefactor.gui;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;

/**
 * The main editor window.
 */
public class EditorWindow extends JFrame {

    /**
     * Reference to the textarea.
     */
    private RSyntaxTextArea textArea;

    /**
     * The windows title.
     */
    private static final String WINDOW_TITLE = "CJScalaRefactor";

    /**
     * Default number of rows .
     */
    private static final int rows = 40;

    /**
     * Default number of colummns.
     */
    private static final int columns = 80;

    /**
     * Constructs a new object editor window.
     */
    protected EditorWindow() {
        super(WINDOW_TITLE);
        JPanel contentPane = new JPanel(new BorderLayout());

        // make textarea
        this.textArea = new RSyntaxTextArea(rows, columns);
        this.textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);
        this.textArea.setWhitespaceVisible(true);
        this.textArea.setCodeFoldingEnabled(true);
        this.textArea.setAntiAliasingEnabled(true);

        // add menubar
        super.setJMenuBar(MenubarFactory.buildMenuBar(this.textArea));

        // Enable Scrolling
        RTextScrollPane scrollPane = new RTextScrollPane(textArea);
        scrollPane.setFoldIndicatorEnabled(true);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        setContentPane(contentPane);

        // make context menu
        JPopupMenu menu = this.textArea.getPopupMenu();
        PopupMenuFactory.extendPopUpMenu(menu, this.textArea);


        // TODO Close Action
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);


    }
}
