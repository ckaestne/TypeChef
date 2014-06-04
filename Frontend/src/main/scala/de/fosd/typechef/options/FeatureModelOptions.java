package de.fosd.typechef.options;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprParserJava;
import de.fosd.typechef.featureexpr.FeatureModel;
import de.fosd.typechef.lexer.FeatureExprLib;
import de.fosd.typechef.lexer.options.ILexerOptions;
import de.fosd.typechef.lexer.options.PartialConfiguration;
import de.fosd.typechef.lexer.options.PartialConfigurationParser$;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.io.File;
import java.util.List;

/**
 * TypeChef uses two different feature models for different purposes, a small and a full
 * feature model. The full feature model should contain all constraints of the small feature
 * model (fullFM => smallFM).
 * <p/>
 * The key concern is performance since all SAT checks involving the feature model take
 * more time. There are generally three different ways to call the SAT solver then
 * <ul>
 * <li>f.isSatisfiable()</li>
 * <li>f.isSatisfiable(smallFM)</li>
 * <li>f.isSatisfiable(fullFM)</li>
 * </ul>
 * The first is the fastest but may return that the formula is satisfiable even if
 * it would not be satisfiable with the full feature model. The small feature model
 * is more precise but also slower. The full feature model provides the reference answer
 * but potentially at additional costs. (In Linux a check with fullFM takes about 0.5 seconds
 * whereas the other two are essentially instantanious).
 * <p/>
 * A common strategy used throughout TypeChef is to use SAT checks without the
 * feature model as long as possible and only use the
 * full feature model before reporting bugs to a user. That way many infeasible
 * paths are pruned early, but some may remain for additional computations.
 * <p/>
 * In the lexer and parser, we found that pruning only at the end when reporting error messages
 * is too late and requires to track complex error conditions and perform many redundant
 * computations. Running the TypeChef parser without any feature model is even slower than running
 * it with the full feature model. Here the small feature model helps to prune more paths
 * and safe computational effort.
 * <p/>
 * Feature models are used in TypeChef as follows:
 * <ul>
 * <li>parser and lexer use the small feature model (if available) in most checks and prune the result with the full feature model</li>
 * <li>the type system computes without any feature model and only checks reported errors with the full feature model</li>
 * <li>...</li>
 * </ul>
 * <p/>
 * Users can specify small and full feature model separately using --featureModelDimacs or --featureModelFExpr
 * for the small feature model and using --typeSystemFeatureModelDimacs for the full feature model.
 * If the small feature model is not defined
 * it is assumed to be empty. If the full feature model is not defined it is assumed to be the same
 * as the small feature model.
 */
public abstract class FeatureModelOptions extends LexerOptions implements ILexerOptions {
    protected FeatureModel smallFeatureModel = null;
    protected FeatureModel fullFeatureModel = null;
    protected PartialConfiguration partialConfig = null;
    private File fmDimacs = null;


    @Override
    public FeatureModel getSmallFeatureModel() {
        if (smallFeatureModel == null)
            return FeatureExprLib.featureModelFactory().empty();
        return smallFeatureModel;
    }

    //    @Override
    public FeatureModel getFullFeatureModel() {
        if (fullFeatureModel != null)
            return fullFeatureModel;
        if (smallFeatureModel == null)
            return FeatureExprLib.featureModelFactory().empty();
        return smallFeatureModel;
    }

    public File getDimacsFile() { return fmDimacs; }

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
                        "Extended feature model for error checking."),
                new Option("partialConfiguration", LongOpt.REQUIRED_ARGUMENT, FM_PARTIALCONFIG, "file",
                        "Loads a partial configuration to the type-system feature model (file with #define and #undef lines).")
        ));

        return r;

    }

    @Override
    protected boolean interpretOption(int c, Getopt g) throws OptionException {
        if (c == FM_DIMACS) {       //--featureModelDimacs
            if (smallFeatureModel != null)
                throw new OptionException("cannot load feature model from dimacs file. feature model already exists.");
            checkFileExists(g.getOptarg());
            // case studies busybox and load a specialized feature model
            // all others load a standard feature model in which the prefix is set to "" (default is "CONFIG_"),
            // which is used in busybox and linux
            if (g.getOptarg().contains("linux") || g.getOptarg().contains("busybox"))
                smallFeatureModel = FeatureExprLib.featureModelFactory().createFromDimacsFile_2Var(g.getOptarg());
            else
                smallFeatureModel = FeatureExprLib.featureModelFactory().createFromDimacsFile(g.getOptarg(), "");
        } else if (c == FM_FEXPR) {     //--featureModelFExpr
            checkFileExists(g.getOptarg());
            FeatureExpr f = new FeatureExprParserJava(FeatureExprLib.l()).parseFile(g.getOptarg());
            if (smallFeatureModel == null)
                smallFeatureModel = FeatureExprLib.featureModelFactory().create(f);
            else smallFeatureModel = smallFeatureModel.and(f);
//        } else if (c == FM_CLASS) {//--featureModelClass
//            try {
//                FeatureModelFactory factory = (FeatureModelFactory) Class.forName(g.getOptarg()).newInstance();
//                smallFeatureModel = factory.createFeatureModel();
//            } catch (Exception e) {
//                throw new OptionException("cannot instantiate feature model: " + e.getMessage());
//            }
        } else if (c == FM_TSDIMACS) {
            checkFileExists(g.getOptarg());
            fmDimacs = new File(g.getOptarg());
            fullFeatureModel = FeatureExprLib.featureModelFactory().createFromDimacsFile_2Var(g.getOptarg());
        } else if (c == FM_PARTIALCONFIG) {
            checkFileExists(g.getOptarg());
            if (partialConfig != null)
                throw new OptionException("cannot load a second partial configuration");
            partialConfig = PartialConfigurationParser$.MODULE$.load(g.getOptarg());
            FeatureExpr f = partialConfig.getFeatureExpr();
            if (fullFeatureModel == null)
                fullFeatureModel = FeatureExprLib.featureModelFactory().empty();

            for (String featureName : partialConfig.getDefinedFeatures())
                fullFeatureModel = fullFeatureModel.assumeTrue(featureName);
            for (String featureName : partialConfig.getUndefinedFeatures())
                fullFeatureModel = fullFeatureModel.assumeFalse(featureName);
        } else
            return super.interpretOption(c, g);
        return true;
    }


    public void setSmallFeatureModel(FeatureModel fm) {
        smallFeatureModel = fm;
    }
    public void setFullFeatureModel(FeatureModel fm) {
        fullFeatureModel = fm;
    }

    @Override
    public PartialConfiguration getLexerPartialConfiguration() {
        return partialConfig;
    }
}
