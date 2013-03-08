package de.fosd.typechef.crefactor.frontend.util;

import javax.swing.*;
import java.awt.event.*;

public class InlineDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox inlineEveryOccurrenceCheckBox;
    private JCheckBox renameShadowedVariablesCheckBox;
    private boolean once;
    private boolean refactor;
    private boolean rename;


    public InlineDialog(final JFrame frame, final String title, final boolean call) {
        super(frame, title);
        setLocationRelativeTo(null);
        setContentPane(this.contentPane);
        setResizable(false);
        setModal(true);
        getRootPane().setDefaultButton(this.buttonOK);

        this.inlineEveryOccurrenceCheckBox.setEnabled(call);

        this.buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        this.buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // stmt onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // stmt onCancel() on ESCAPE
        this.contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // stmt onOK() on ENTER
        this.contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        this.once = !this.inlineEveryOccurrenceCheckBox.isSelected();
        this.rename = this.renameShadowedVariablesCheckBox.isSelected();
        this.refactor = true;
        dispose();
    }

    private void onCancel() {
        this.refactor = false;
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
}
