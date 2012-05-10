package de.fosd.typechef.crefactor.gui;


import javax.swing.*;

/**
 * Launches up a simple editor window for editing and refactoring c code.
 *
 * @author Andreas Janker
 */
public class Launch {

    /**
     * Do not allow an instance of this class.
     */
    private Launch() {
    }

    /**
     * Definition of the used look and feel.
     */
    private static final String LOOK_AND_FEEL = UIManager.getSystemLookAndFeelClassName();

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(LOOK_AND_FEEL);
                    new EditorWindow().setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
