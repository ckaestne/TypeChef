package de.fosd.typechef.lexer.options;

import de.fosd.typechef.featureexpr.FeatureModel;


public interface IFeatureModelOptions {

    FeatureModel getFeatureModel();

    FeatureModel getFeatureModelTypeSystem();

    PartialConfiguration getPartialConfiguration();
}
