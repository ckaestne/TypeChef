package de.fosd.typechef.lexer.options;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprParser;
import de.fosd.typechef.featureexpr.FeatureModel;
import de.fosd.typechef.featureexpr.FeatureModelFactory;
import de.fosd.typechef.featureexpr.NoFeatureModel$;
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


    @Override
    public FeatureModel getFeatureModel() {
        if (featureModel == null)
            return NoFeatureModel$.MODULE$;
        return featureModel;
    }

    @Override
    protected List<Options.OptionGroup> getOptionGroups() {
        List<Options.OptionGroup> r = super.getOptionGroups();

        r.add(new OptionGroup("Feature models", 100,
                new Option("featureModelDimacs", LongOpt.REQUIRED_ARGUMENT, 8, "file",
                        "Dimacs file describing a feature model."),
                new Option("featureModelFExpr", LongOpt.REQUIRED_ARGUMENT, 9, "file",
                        "File in FExpr format describing a feature model."),
                new Option("featureModelClass", LongOpt.REQUIRED_ARGUMENT, 10, "classname",
                        "Class describing a feature model.")
        ));

        return r;

    }

    @Override
    protected boolean interpretOption(int c, Getopt g) throws OptionException {
        switch (c) {
            case 8:       //--featureModelDimacs
                if (featureModel != null)
                    throw new OptionException("cannot load feature model from dimacs file. feature model already exists.");
                checkFileExists(g.getOptarg());
                featureModel = FeatureModel.createFromDimacsFile(g.getOptarg());
                return true;
            case 9:     //--featureModelFExpr
                checkFileExists(g.getOptarg());
                FeatureExpr f = new FeatureExprParser().parseFile(g.getOptarg());
                if (featureModel == null)
                    featureModel = de.fosd.typechef.featureexpr.FeatureModel.create(f);
                else featureModel = featureModel.and(f);
                return true;
            case 10://--featureModelClass
                try {
                    FeatureModelFactory factory = (FeatureModelFactory) Class.forName(g.getOptarg()).newInstance();
                    featureModel = factory.createFeatureModel();
                } catch (Exception e) {
                    throw new OptionException("cannot instantiate feature model: " + e.getMessage());
                }
                return true;
            default:
                return super.interpretOption(c, g);
        }


    }

    public void setFeatureModel(FeatureModel fm) {
        featureModel = fm;
    }
}
