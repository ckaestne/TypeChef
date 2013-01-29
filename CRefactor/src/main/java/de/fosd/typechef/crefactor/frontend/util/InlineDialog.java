package de.fosd.typechef.crefactor.frontend.util;

import javax.swing.*;
import java.awt.event.*;

public class InlineDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox inlineEveryOccurenceCheckBox;
    private JCheckBox renameShadowedVariablesCheckBox;
    private boolean refactor;
    private boolean once;
    private boolean rename;


    public InlineDialog(final JFrame frame, final String title, final boolean call) {
        super(frame, title);
        setLocationRelativeTo(null);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        this.inlineEveryOccurenceCheckBox.setEnabled(call);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        once = !this.inlineEveryOccurenceCheckBox.isSelected();
        rename = this.renameShadowedVariablesCheckBox.isSelected();
        refactor = true;
        dispose();
    }

    private void onCancel() {
        refactor = false;
        dispose();
    }

    public boolean isRefactor() {
        return this.refactor;
    }

    public boolean isOnce() {
        return this.once;
    }

    public boolean isRename() {
        return this.rename;
    }

    /**
     * public static void main(String[] args) {
     * InlineDialog dialog = new InlineDialog("Inline Function", false);
     * dialog.pack();
     * dialog.setVisible(true);
     * System.exit(0);
     * }
     */

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
