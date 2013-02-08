package de.fosd.typechef.lexer.options;

import de.fosd.typechef.featureexpr.*;
import de.fosd.typechef.featureexpr.sat.SATFeatureExprFactory;
import de.fosd.typechef.featureexpr.sat.SATFeatureModel;
import de.fosd.typechef.lexer.FeatureExprLib;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 29.12.11
 * Time: 11:06
 * To change this template use File | Settings | File Templates.
 */
public class FeatureModelOptions extends Options implements IFeatureModelOptions {
    protected FeatureModel featureModel = null;
    protected FeatureModel featureModel_typeSystem = null;
    protected PartialConfiguration partialConfig = null;

    // for linux we have two different feature models
    // a small one satFM (used for parsing)
    // and a big one satFMTypeSystem (used later for typechecking)
    protected FeatureModel satFM           = null;
    protected FeatureModel satFMTypeSystem = null;

    public FeatureModel getSATFeatureModel() {
        if (satFMTypeSystem == null)
            satFMTypeSystem = SATFeatureModel.empty();
        return satFMTypeSystem;
    }

    public FeatureModel getSATFeatureModelTypeSystem() {
        return satFMTypeSystem;
    }

    @Override
    public FeatureModel getFeatureModel() {
        if (featureModel == null)
            return FeatureExprLib.featureModelFactory().empty();
        return featureModel;
    }

    @Override
    public FeatureModel getFeatureModelTypeSystem() {
        if (featureModel_typeSystem != null)
            return featureModel_typeSystem;
        if (featureModel == null)
            return FeatureExprLib.featureModelFactory().empty();
        return featureModel;
    }

    private static final char FM_DIMACS = Options.genOptionId();
    private static final char FM_FEXPR = Options.genOptionId();
    //    private static final char FM_CLASS = Options.genOptionId();
    private static final char FM_TSDIMACS = Options.genOptionId();
    private static final char FM_PARTIALCONFIG = Options.genOptionId();

    @Override
    protected List<Options.OptionGroup> getOptionGroups() {
        List<Options.OptionGroup> r = super.getOptionGroups();

        r.add(new OptionGroup("Feature models", 100,
                new Option("featureModelDimacs", LongOpt.REQUIRED_ARGUMENT, FM_DIMACS, "file",
                        "Dimacs file describing a feature model."),
                new Option("featureModelFExpr", LongOpt.REQUIRED_ARGUMENT, FM_FEXPR, "file",
                        "File in FExpr format describing a feature model."),
//                new Option("featureModelClass", LongOpt.REQUIRED_ARGUMENT, FM_CLASS, "classname",
//                        "Class describing a feature model."),
                new Option("typeSystemFeatureModelDimacs", LongOpt.REQUIRED_ARGUMENT, FM_TSDIMACS, "file",
                        "Distinct feature model for the type system."),
                new Option("partialConfiguration", LongOpt.REQUIRED_ARGUMENT, FM_PARTIALCONFIG, "file",
                        "Loads a partial configuration to the type-system feature model (file with #define and #undef lines).")
        ));

        return r;

    }

    @Override
    protected boolean interpretOption(int c, Getopt g) throws OptionException {
        if (c == FM_DIMACS) {       //--featureModelDimacs
            if (featureModel != null)
                throw new OptionException("cannot load feature model from dimacs file. feature model already exists.");
            checkFileExists(g.getOptarg());
            featureModel = FeatureExprLib.featureModelFactory().createFromDimacsFile_2Var(g.getOptarg());
        } else if (c == FM_FEXPR) {     //--featureModelFExpr
            checkFileExists(g.getOptarg());
            FeatureExpr f = new FeatureExprParser(FeatureExprLib.l()).parseFile(g.getOptarg());
            if (featureModel == null)
                featureModel = FeatureExprLib.featureModelFactory().create(f);
            else featureModel = featureModel.and(f);

            if (satFM == null)
                satFM = SATFeatureModel.empty();
            FeatureExpr fsat = new FeatureExprParser(FeatureExprFactory$.MODULE$.sat()).parseFile(g.getOptarg());
            satFM = satFM.and(fsat);

//        } else if (c == FM_CLASS) {//--featureModelClass
//            try {
//                FeatureModelFactory factory = (FeatureModelFactory) Class.forName(g.getOptarg()).newInstance();
//                featureModel = factory.createFeatureModel();
//            } catch (Exception e) {
//                throw new OptionException("cannot instantiate feature model: " + e.getMessage());
//            }
        } else if (c == FM_TSDIMACS) {
            checkFileExists(g.getOptarg());
            featureModel_typeSystem = FeatureExprLib.featureModelFactory().createFromDimacsFile_2Var(g.getOptarg());

            satFMTypeSystem = SATFeatureExprFactory.featureModelFactory().createFromDimacsFile_2Var(g.getOptarg());
        } else if (c == FM_PARTIALCONFIG) {
            checkFileExists(g.getOptarg());
            if (partialConfig != null)
                throw new OptionException("cannot load a second partial configuration");
            partialConfig = PartialConfigurationParser$.MODULE$.load(g.getOptarg());
            FeatureExpr f = partialConfig.getFeatureExpr();
            if (featureModel_typeSystem == null)
                featureModel_typeSystem = FeatureExprLib.featureModelFactory().empty();

            for (String featureName : partialConfig.getDefinedFeatures()){
                featureModel_typeSystem = featureModel_typeSystem.assumeTrue(featureName);
                satFMTypeSystem = satFMTypeSystem.assumeTrue(featureName);
            }

            for (String featureName : partialConfig.getUndefinedFeatures()) {
                featureModel_typeSystem = featureModel_typeSystem.assumeFalse(featureName);
                satFMTypeSystem = satFMTypeSystem.assumeTrue(featureName);
            }
        } else
            return super.interpretOption(c, g);
        return true;
    }


    public void setFeatureModel(FeatureModel fm) {
        featureModel = fm;
    }

    public PartialConfiguration getPartialConfiguration() {
        return partialConfig;
    }
}
