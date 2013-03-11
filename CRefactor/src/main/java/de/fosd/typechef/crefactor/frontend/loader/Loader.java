package de.fosd.typechef.crefactor.frontend.loader;

import de.fosd.typechef.crefactor.util.Configuration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * Loader class to display a window for choosing the file to refactor and the required include directories.
 *
 * @author Andreas Janker
 */
public class Loader extends JDialog {

    /**
     * Reference to the file we want to analyse.
     */
    private File fileToAnalyse;

    /**
     * Reference to the directory where the additional includes are located.
     */
    private File includeDir;

    /**
     * Reference to the include header.
     */
    private File includeHeader;

    /**
     * Reference to the feature model.
     */
    private File featureModel;


    /**
     * Generates a new instance of this class.
     */
    public Loader() {
        this.fileToAnalyse = null;
        this.includeDir = null;
    }

    /**
     * Creates and shows the loading window.
     */
    public void createAndShowLoader() {
        super.setTitle(Configuration.getInstance().getConfig("loader.title"));
        super.setModal(true);
        super.setModalityType(ModalityType.APPLICATION_MODAL);
        super.setSize(Configuration.getInstance().getConfigAsInt("loader.windowWidth"),
                Configuration.getInstance().getConfigAsInt("loader.windowHeight"));
        super.setResizable(false);
        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent windowEvent) {
                // Closing the window is the same like pressing cancel.
                getCancelAction().actionPerformed(null);
            }
        });

        super.getContentPane().setLayout(new BoxLayout(super.getContentPane(), BoxLayout.Y_AXIS));

        super.getContentPane().add(makeFileChoosePanel(
                Configuration.getInstance().getConfig("loader.loadFile"), 9, fileToAnalyse, false));
        super.getContentPane().add(new JSeparator());
        // TODO Refactor for correct alingement of correction value -> remove magic numbers
        super.getContentPane().add(makeFileChoosePanel(
                Configuration.getInstance().getConfig("loader.includeDir"), 40, includeDir, true));
        super.getContentPane().add(new JSeparator());
        super.getContentPane().add(makeFileChoosePanel(
                Configuration.getInstance().getConfig("loader.includePre"), 38, includeHeader, false));
        super.getContentPane().add(new JSeparator());
        super.getContentPane().add(makeFileChoosePanel(
                Configuration.getInstance().getConfig("loader.feature"), 62, featureModel, false));
        super.getContentPane().add(new JSeparator());
        super.getContentPane().add(makeControlButtons());

        // Finalize Window and display it
        super.setLocationRelativeTo(null);
        super.setLocationByPlatform(true);
        super.setVisible(true);
    }

    /**
     * Build panel for choosing a file or directory.
     *
     * @param description description to be displayed up front.
     * @param correction  layout correction value.
     * @return the panel
     */
    private JPanel makeFileChoosePanel(
            final String description, final int correction, final File fileReference, final boolean directoryOnly) {
        final JPanel fileToLoad = new JPanel();
        final SpringLayout layout = new SpringLayout();
        fileToLoad.setLayout(layout);

        final JLabel fileLabel = new JLabel(description);

        final JTextField fileField = new JTextField(Configuration.getInstance().getConfig("loader.defaultEntry"),
                Configuration.getInstance().getConfigAsInt("loader.textFieldWidth"));
        fileField.setEditable(false);
        fileLabel.setLabelFor(fileField);

        final JButton choose = new JButton(Configuration.getInstance().getConfig("loader.choose"));
        fileToLoad.add(choose);
        final Dialog dialog = this;
        choose.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(false);
                if (directoryOnly) {
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                } else {
                    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                }

                if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                    fileField.setText(chooser.getSelectedFile().getAbsolutePath());
                    assignFile(chooser.getSelectedFile(), fileReference);
                } else {
                    fileField.setText(Configuration.getInstance().getConfig("loader.defaultEntry"));
                    assignFile(null, fileReference);
                }
            }
        });

        fileToLoad.add(fileLabel);
        fileToLoad.add(fileField);

        // arrange position
        layout.putConstraint(SpringLayout.WEST, fileLabel, 5, SpringLayout.WEST, fileToLoad);
        layout.putConstraint(SpringLayout.NORTH, fileLabel, 10, SpringLayout.NORTH, fileToLoad);

        layout.putConstraint(SpringLayout.WEST, fileField, 5 + correction, SpringLayout.EAST, fileLabel);
        layout.putConstraint(SpringLayout.NORTH, fileField, 5, SpringLayout.NORTH, fileToLoad);

        layout.putConstraint(SpringLayout.WEST, choose, 5, SpringLayout.EAST, fileField);
        layout.putConstraint(SpringLayout.NORTH, choose, 5, SpringLayout.NORTH, fileToLoad);

        return fileToLoad;
    }

    /**
     * Assigns the selected file to the correct reference.
     *
     * @param toAssign      the file object to assign
     * @param fileReference the file reference
     */
    private void assignFile(final File toAssign, final File fileReference) {
        // TODO Correct assigning! Bug @ reassignment of files.
        if (fileReference == fileToAnalyse) {
            fileToAnalyse = toAssign;
        } else if (fileReference == includeDir) {
            includeDir = toAssign;
        }
    }

    /**
     * Generates the control button panel.
     *
     * @return the control button panel.
     */
    private JPanel makeControlButtons() {
        final JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttons.setSize(Configuration.getInstance().getConfigAsInt("loader.windowWidth"),
                Configuration.getInstance().getConfigAsInt("loader.buttonsHeight"));

        final JButton okButton = new JButton(Configuration.getInstance().getConfig("loader.load"));
        okButton.addActionListener(getOkAction());

        final JButton cancelButton = new JButton(Configuration.getInstance().getConfig("loader.cancel"));
        cancelButton.addActionListener(getCancelAction());

        buttons.add(okButton);
        buttons.add(cancelButton);
        return buttons;
    }

    /**
     * Retrieves the file to analyse.
     *
     * @return the file to analyse or <code>null</code> if no file was specified.
     */
    public File getFileToAnalyse() {
        return this.fileToAnalyse;
    }

    /**
     * Retrieves the include dir.
     *
     * @return the include directory or <code>null</code> if no directory was specified.
     */
    public File getIncludeDir() {
        return this.includeDir;
    }

    /**
     * Retrieves the include header.
     *
     * @return the include header file or <code>null</code> if no file was specified.
     */
    public File getIncludeHeader() {
        return this.includeHeader;
    }

    /**
     * Retrieves the feature model.
     *
     * @return the feature model file or <code>null</code> if no file was specified.
     */
    public File getFeatureModel() {
        return this.featureModel;
    }

    /**
     * Action performed in case cancel button or close window button is called.
     *
     * @return the action to perform.
     */
    private AbstractAction getCancelAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fileToAnalyse = null;
                includeDir = null;
                dispose();
            }
        };
    }

    /**
     * Action performed in case ok button is pressed.
     *
     * @return the action to perform.
     */
    private AbstractAction getOkAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (getFileToAnalyse() != null) {
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, Configuration.getInstance().getConfig("loader.noFile"),
                            Configuration.getInstance().getConfig("default.error"), JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }
}