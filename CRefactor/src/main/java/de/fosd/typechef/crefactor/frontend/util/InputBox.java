package de.fosd.typechef.crefactor.frontend.util;

import de.fosd.typechef.crefactor.util.Configuration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Inputbox for renaming an id.
 */
public class InputBox extends JDialog {

    /**
     * Entered text.
     */
    private String input;

    /**
     * Constructs a new inputbox.
     */
    public InputBox() {
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
        // TODO Config
        super.setSize(new Dimension(Configuration.getInstance().getConfigAsInt("inputbox.width"),
                Configuration.getInstance().getConfigAsInt("inputbox.height")));

        super.getContentPane().setLayout(new BoxLayout(super.getContentPane(), BoxLayout.Y_AXIS));

        final JTextField textField = new JTextField(preDefinedText, 20);

        super.getContentPane().add(makeInputPanel(description, textField));
        super.getContentPane().add(new JSeparator());
        super.getContentPane().add(makeControlButtons(textField));

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
        final JButton cancel = new JButton("Abbrechen");
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
                if (textField.getText().trim().length() > 0) {
                    input = textField.getText().trim();
                    dispose();
                }
                // TODO else case

            }
        };
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
                input = null;
                dispose();
            }
        };
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
