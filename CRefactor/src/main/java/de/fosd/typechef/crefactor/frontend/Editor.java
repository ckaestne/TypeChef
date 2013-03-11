package de.fosd.typechef.crefactor.frontend;

import de.fosd.typechef.crefactor.Morpheus;
import de.fosd.typechef.crefactor.util.Configuration;
import de.fosd.typechef.parser.c.PrettyPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Observable;
import java.util.Observer;

/**
 * The main editor window.
 */
public class Editor extends JFrame implements Observer {

    private static Logger logger = LogManager.getLogger(Editor.class);

    /**
     * Reference to the textarea.
     */
    private RSyntaxTextArea textArea;

    /**
     * The current morph object
     */
    private Morpheus morpheus;

    /**
     * Generates a new editor window instance.
     */
    public Editor(final Morpheus morph) {
        super(Configuration.getInstance().getConfig("editor.title"));

        this.morpheus = morph;
        morph.addObserver(this);

        // make textarea
        this.textArea = new RSyntaxTextArea(Configuration.getInstance().getConfigAsInt("editor.rows"),
                Configuration.getInstance().getConfigAsInt("editor.columns"));
        this.textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);
        this.textArea.setWhitespaceVisible(false);
        this.textArea.setCodeFoldingEnabled(true);
        this.textArea.setAntiAliasingEnabled(true);
        this.textArea.setEditable(true);

        // Enable Scrolling
        final JPanel contentPane = new JPanel(new BorderLayout());
        final RTextScrollPane scrollPane = new RTextScrollPane(this.textArea);
        scrollPane.setFoldIndicatorEnabled(true);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        setContentPane(contentPane);

        // make context menu
        final JPopupMenu menu = this.textArea.getPopupMenu();
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
    public final void loadFileInEditor(final File file) {
        if (!file.isFile()) {
            return;
        }

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            this.textArea.read(reader, null);
        } catch (final Exception e) {
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
    public final RTextArea getRTextArea() {
        return this.textArea;
    }

    /**
     * Retrieves the morpheus morph object.
     *
     * @return the morph object
     */
    public final Morpheus getMorpheus() {
        return this.morpheus;
    }

    @Override
    public void update(final Observable observable, final Object o) {
        final ThreadMXBean tb = ManagementFactory.getThreadMXBean();
        final long startTime = tb.getCurrentThreadCpuTime();
        this.textArea.setText(PrettyPrinter.print(this.morpheus.getAST()));
        final long duration = (tb.getCurrentThreadCpuTime() - startTime) / 1000000;
        logger.info("PrettyPrinting duration: " + duration + "ms");
    }
}
