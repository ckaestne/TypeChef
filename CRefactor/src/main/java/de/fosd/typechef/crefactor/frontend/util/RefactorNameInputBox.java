package de.fosd.typechef.crefactor.frontend.util;

import de.fosd.typechef.crefactor.util.Configuration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Inputbox for renaming an callId.
 */
public class RefactorNameInputBox extends JDialog {

    /**
     * Entered text.
     */
    private String input;

    /**
     * Constructs a new inputbox.
     */
    public RefactorNameInputBox() {
        this.input = null;
    }

    /**
     * Creates and shows the input box.
     *
     * @param title          title of the input box.
     * @param description    description
     * @param preDefinedText Predefined text for the input box.
     */
    public void createAndShowInputBox(final String title, final String description, final String preDefinedText) {
        // TODO Refactor
        super.setTitle(title);
        super.setModal(true);
        super.setModalityType(ModalityType.APPLICATION_MODAL);
        super.setResizable(false);
        super.setSize(new Dimension(Configuration.getInstance().getConfigAsInt("inputbox.width"),
                Configuration.getInstance().getConfigAsInt("inputbox.height")));

        super.getContentPane().setLayout(new BoxLayout(super.getContentPane(), BoxLayout.Y_AXIS));

        final JTextField textField = new JTextField(preDefinedText, 20);

        super.getContentPane().add(makeInputPanel(description, textField));
        super.getContentPane().add(new JSeparator());
        super.getContentPane().add(makeControlButtons(textField));

        // stmt onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        textField.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOk(textField);
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // stmt onCancel() on ESCAPE
        textField.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        super.setLocationRelativeTo(null);
        super.setLocationByPlatform(true);
        super.setVisible(true);
    }

    /**
     * Makes the control buttons.
     *
     * @param textField the used textField
     * @return The generated buttons
     */
    private JPanel makeControlButtons(JTextField textField) {
        final JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttons.setSize(Configuration.getInstance().getConfigAsInt("inputbox.width"),
                Configuration.getInstance().getConfigAsInt("loader.buttonsHeight"));
        final JButton ok = new JButton("OK");
        ok.addActionListener(getOKAction(textField));

        final JButton cancel = new JButton("Cancel");
        cancel.addActionListener(getCancelAction());

        buttons.add(ok);
        buttons.add(cancel);
        return buttons;
    }

    /**
     * Makes the input panel.
     *
     * @param description description of the panel
     * @param textField   panel's textfield
     * @return The generated panel.
     */
    private JPanel makeInputPanel(final String description, final JTextField textField) {
        final JPanel input = new JPanel();
        final SpringLayout layout = new SpringLayout();
        input.setLayout(layout);

        final JLabel desc = new JLabel(description);
        desc.setLabelFor(textField);
        input.add(desc);
        input.add(textField);

        layout.putConstraint(SpringLayout.WEST, desc, 5, SpringLayout.WEST, input);
        layout.putConstraint(SpringLayout.NORTH, desc, 10, SpringLayout.NORTH, input);

        layout.putConstraint(SpringLayout.WEST, textField, 5, SpringLayout.EAST, desc);
        layout.putConstraint(SpringLayout.NORTH, textField, 5, SpringLayout.NORTH, input);
        return input;
    }

    /**
     * Retrieves the action to perform when the ok button is pressed.
     *
     * @param textField the used textfield
     * @return the action
     */
    private AbstractAction getOKAction(final JTextField textField) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onOk(textField);
            }
        };
    }

    private void onOk(JTextField textField) {
        if (textField.getText().trim().length() > 0) {
            input = textField.getText().trim();
            dispose();
        }
    }

    /**
     * Retrieves the action to perform when the cancel button is pressed.
     *
     * @return the action
     */
    private AbstractAction getCancelAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onCancel();
            }
        };
    }

    private void onCancel() {
        input = null;
        dispose();
    }

    /**
     * Retrieves the input.
     *
     * @return the entered input or <code>null</code> if user pressed abort.
     */
    public String getInput() {
        return this.input;
    }
}
