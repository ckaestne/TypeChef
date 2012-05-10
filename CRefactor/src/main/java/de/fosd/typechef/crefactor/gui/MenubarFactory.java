package de.fosd.typechef.crefactor.gui;

import de.fosd.typechef.crefactor.gui.actions.FileActions;
import org.fife.ui.rtextarea.RTextArea;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Factory class for building a menubar for the window.
 */
public final class MenubarFactory {

    /**
     * Displayed name for the edit button.
     */
    private final static String EDIT_MENU = "Bearbeiten";

    /**
     * Displayed name for the help button.
     */
    private final static String HELP_MENU = "Hilfe";

    /**
     * Name of the filemenu.
     */
    private static final String FILE_MENU = "Datei";

    /**
     * Name of the new document action
     */
    private static final String NEW_ACTION = "Neu";

    /**
     * Factory method for building a new menubar
     *
     * @param textArea the referenced textarea
     * @return the generated menubar.
     */
    public static JMenuBar buildMenuBar(final RTextArea textArea) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(buildFileMenu(textArea));
        menuBar.add(new JMenu(EDIT_MENU));
        menuBar.add(new JMenu(HELP_MENU));
        return menuBar;
    }

    private static JMenu buildFileMenu(final RTextArea textArea) {
        JMenu fileMenu = new JMenu(FILE_MENU);

        // new action
        JMenuItem newDoc = new JMenuItem(NEW_ACTION);
        newDoc.setEnabled(false);
        fileMenu.add(newDoc);
        fileMenu.addSeparator();

        // open action
        JMenuItem open = new JMenuItem(FileActions.openAction(textArea));
        fileMenu.add(open);

        // save action
        JMenuItem save = new JMenuItem(FileActions.saveAction(textArea));
        fileMenu.add(save);

        fileMenu.addSeparator();

        // exit action
        // TODO Save and Quit Dialog
        JMenuItem exit = new JMenuItem("Beenden");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exit);

        return fileMenu;
    }


}
