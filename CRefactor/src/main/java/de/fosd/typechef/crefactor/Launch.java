package de.fosd.typechef.crefactor;

import de.fosd.typechef.crefactor.backend.Connector;
import de.fosd.typechef.crefactor.frontend.Editor;
import de.fosd.typechef.crefactor.frontend.loader.Loader;
import de.fosd.typechef.crefactor.util.Configuration;

import javax.swing.*;
import java.io.File;
import java.util.LinkedList;

/**
 * Launch up class for starting up CRefactor in combination with typechef.
 *
 * @author Andreas Janker
 */
public final class Launch {

    /**
     * Do not allow an instance of this class.
     */
    private Launch() {
    }

    /**
     * Main method to start up the programme. This is where the magic begins :)
     *
     * @param args no parameter args used
     */
    public static void main(String[] args) {
        try {
            // Set System L&F
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // show file and include loading window.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final Loader loadingWindow = new Loader();
                loadingWindow.createAndShowLoader();

                if (loadingWindow.getFileToAnalyse() == null) {
                    // Exit System
                    System.exit(1);
                }
                // parse file
                Connector.parse(generateTypeChefArguments(
                        loadingWindow.getFileToAnalyse(), loadingWindow.getIncludeDir(),
                        loadingWindow.getIncludeHeader(), loadingWindow.getFeatureModel()));

                // show editor window
                final Editor editor = new Editor();
                editor.loadFileInEditor(loadingWindow.getFileToAnalyse());
                // editor.getRTextArea().setText(PrettyPrinter.print(Connector.getAST()));
                editor.setVisible(true);
            }
        });
    }

    /**
     * Generates the commandline arguments for typechef.
     *
     * @param toLoad        file to load.
     * @param toInclude     directory to include
     * @param includeHeader header file to include
     * @param featureModel  feature model to include
     * @return arguments to run typechef :)
     */
    private static String[] generateTypeChefArguments(
            final File toLoad, final File toInclude, final File includeHeader, final File featureModel) {
        final LinkedList<String> args = new LinkedList<String>();
        args.add(toLoad.getAbsolutePath());
        // TODO Ask Jörg -> Prefix
        args.add("-xCONFIG_");
        args.add("-c".concat(Configuration.getInstance().getTypeChefConfigFilePath()));


        if (includeHeader != null) {
            args.add("--include".concat(includeHeader.getAbsolutePath()));
        }

        if (toInclude != null) {
            args.add("-I".concat(toInclude.getAbsolutePath()));
        }

        if (featureModel != null) {
            args.add("--featureModelFExpr".concat(featureModel.getAbsolutePath()));
        }

        // args.add("--typecheck");

        return args.toArray(new String[args.size()]);
    }
}
