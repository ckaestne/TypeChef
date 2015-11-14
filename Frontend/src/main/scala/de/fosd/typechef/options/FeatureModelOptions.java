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

import java.util.List;

/**
 * TypeChef uses two different feature models for different purposes, a small and a full
 * feature model. In Linux smallFM = approx.fm and fullFM = dimacs file.
 * <p/>
 * The full feature model should contain all constraints of the small feature model (fullFM => smallFM).
 * This is checked at startup and a warning will be issued if the full model does
 * not imply the small model (for --smallFeatureModelFExpr only). Violating this invariant can
 * lead to very obscure bugs, because some configurations
 * might be excluded during lexing, but not during type checking.
 * <p/>
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
 * but potentially at additional costs. (In Linux 2.6.3.33 a check with fullFM takes about 0.5 seconds
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
 * <p/>
 * Users can specify small and full feature model separately using --smallFeatureModelDimacs or --smallFeatureModelFExpr
 * for the small feature model and using --featureModelDimacs or --featureModelFExpr for the full feature model.
 * <p/>
 * If the small feature model is not defined it is assumed to be empty.
 * If the full feature model is not defined it is assumed to be the same
 * as the small feature model.
 */
public abstract class FeatureModelOptions extends LexerOptions implements ILexerOptions {
    protected FeatureModel smallFeatureModel = null;
    protected FeatureModel fullFeatureModel = null;
    protected PartialConfiguration partialConfig = null;
    protected String dimacsPrefix = "CONFIG_";
    private FeatureExpr smallFeatureModelExpr = null;
    private boolean dimacsModelLoaded = false;


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
        if (smallFeatureModel != null)
            return smallFeatureModel;
        return FeatureExprLib.featureModelFactory().empty();
    }

    private static final char FM_FEXPR = Options.genOptionId();
    private static final char FM_DIMACS = Options.genOptionId();
    private static final char FM_FEXPR_SMALL = Options.genOptionId();
    private static final char FM_DIMACS_SMALL = Options.genOptionId();
    private static final char FM_PARTIALCONFIG = Options.genOptionId();
    private static final char FM_DIMACS_PREFIX = Options.genOptionId();

    @Override
    protected List<Options.OptionGroup> getOptionGroups() {
        List<Options.OptionGroup> r = super.getOptionGroups();

        r.add(new OptionGroup("Feature models", 100,
                new Option("featureModelDimacs", LongOpt.REQUIRED_ARGUMENT, FM_DIMACS, "file",
                        "Dimacs file describing a feature model."),
                new Option("featureModelFExpr", LongOpt.REQUIRED_ARGUMENT, FM_FEXPR, "file",
                        "File in FExpr format describing a feature model."),
                new Option("smallFeatureModelDimacs", LongOpt.REQUIRED_ARGUMENT, FM_DIMACS_SMALL, "file",
                        "Dimacs file describing a feature model."),
                new Option("smallFeatureModelFExpr", LongOpt.REQUIRED_ARGUMENT, FM_FEXPR_SMALL, "file",
                        "File in FExpr format describing a feature model."),
                new Option("partialConfiguration", LongOpt.REQUIRED_ARGUMENT, FM_PARTIALCONFIG, "file",
                        "Loads a partial configuration to the type-system feature model (file with #define and #undef lines)."),
                new Option("dimacsFeaturePrefix", LongOpt.REQUIRED_ARGUMENT, FM_DIMACS_PREFIX, "prefix",
                        "Prefix that is added to all names in the dimacs file loaded after this option (default: CONFIG_). Use two double-quotes (\"\") as an empty prefix.")
        ));

        return r;

    }

    @Override
    protected boolean interpretOption(int c, Getopt g) throws OptionException {
        if (c == FM_DIMACS) {       //--featureModelDimacs, loads the full model
            String filename = g.getOptarg();
            checkFileExists(filename);
            if (fullFeatureModel != null)
                throw new OptionException("cannot load feature model " + filename + " from dimacs file. A feature model was already loaded.");
            fullFeatureModel = FeatureExprLib.featureModelFactory().createFromDimacsFilePrefix(filename, dimacsPrefix);
            dimacsModelLoaded = true;
        } else if (c == FM_DIMACS_SMALL) {       //--smallFeatureModelDimacs
            String filename = g.getOptarg();
            checkFileExists(filename);
            if (smallFeatureModel != null)
                throw new OptionException("cannot load feature model " + filename + " from dimacs file. A feature model was already loaded.");
            smallFeatureModel = FeatureExprLib.featureModelFactory().createFromDimacsFilePrefix(filename, dimacsPrefix);
            dimacsModelLoaded = true;
        } else if (c == FM_FEXPR) {     //--featureModelFExpr
            checkFileExists(g.getOptarg());
            FeatureExpr f = new FeatureExprParserJava(FeatureExprLib.l()).parseFile(g.getOptarg());
            if (fullFeatureModel == null)
                fullFeatureModel = FeatureExprLib.featureModelFactory().create(f);
            else fullFeatureModel = fullFeatureModel.and(f);
        } else if (c == FM_FEXPR_SMALL) {     //--smallFeatureModelFExpr
            checkFileExists(g.getOptarg());
            FeatureExpr f = new FeatureExprParserJava(FeatureExprLib.l()).parseFile(g.getOptarg());
            if (smallFeatureModelExpr == null)
                smallFeatureModelExpr = f;
            else smallFeatureModelExpr = smallFeatureModelExpr.and(f);
            if (smallFeatureModel == null)
                smallFeatureModel = FeatureExprLib.featureModelFactory().create(f);
            else smallFeatureModel = smallFeatureModel.and(f);
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
        } else if (c == FM_DIMACS_PREFIX) {    //--dimacsFeaturePrefix
            String prefix = g.getOptarg();

            // if any dimacs model was already loaded, the prefix is useless
            if ((fullFeatureModel != null || smallFeatureModel !=null) && dimacsModelLoaded)
                throw new OptionException("--dimacsFeaturePrefix given after --featureModelDimacs! this way, the feature model is not loaded the right way.");

            if (prefix.startsWith("--"))
                throw new OptionException("cannot set prefix \"" + prefix + "\" as dimacs-feature prefix due to invalid first characters.\n" +
                        "probably, you gave no argument in the first place, use \"\" to have an empty string as prefix.");

            // catch 'wrong' empty string
            if (prefix.startsWith("''")) {
                System.err.println("WARNING: You supplied '' as dimacs prefix. Reset as \"\" to ensure empty string as prefix.");
                prefix = "";
            }

            dimacsPrefix = prefix;
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

    @Override
    protected void afterParsing() throws OptionException {
        super.afterParsing();

        if (smallFeatureModelExpr!=null && !smallFeatureModelExpr.isTautology(getFullFeatureModel())) {
            System.err.println("WARNING: The small feature model is not a subset of the full feature model. This can have unintended side effects; see FeatureModelOptions.java.");
        }

    }
}
