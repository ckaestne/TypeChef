package de.fosd.typechef

import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr, SingleFeatureExpr}

// representation of a product configuration that can be dumped into a file
// and loaded at further runs
class SimpleConfiguration(val ff: FileFeatures, @transient val trueSet: List[SingleFeatureExpr],
                          @transient val falseSet: List[SingleFeatureExpr]) extends scala.Serializable {

    val ret: scala.collection.mutable.BitSet = scala.collection.mutable.BitSet()
    for (elem: SingleFeatureExpr <- trueSet)  ret.add(ff.featureIDHashmap(elem))
    for (elem: SingleFeatureExpr <- falseSet) ret.remove(ff.featureIDHashmap(elem))
    ret.toImmutable

    def getTrueSet: Set[SingleFeatureExpr] = {
        ff.features.filter({
            fex: SingleFeatureExpr => ret.apply(ff.featureIDHashmap(fex))
        }).toSet
    }

    def getFalseSet: Set[SingleFeatureExpr] = {
        ff.features.filterNot({
            fex: SingleFeatureExpr => ret.apply(ff.featureIDHashmap(fex))
        }).toSet
    }

    override def toString: String = {
        ff.features.map(
        {
            fex: SingleFeatureExpr => if (ret.apply(ff.featureIDHashmap(fex))) fex else fex.not()
        }
        ).mkString("&&")
    }

    // caching, values of this field will not be serialized
    @transient
    private var featureExpression: FeatureExpr = null

    def toFeatureExpr: FeatureExpr = {
        if (featureExpression == null)
            featureExpression = FeatureExprFactory.createFeatureExprFast(getTrueSet, getFalseSet)
        featureExpression
    }

    /**
     * This method assumes that all features in the parameter-set appear in either the trueList,
     * or in the falseList
     * @param features given feature set
     * @return
     */
    def containsAllFeaturesAsEnabled(features: Set[SingleFeatureExpr]): Boolean = {
        for (fex <- features) {
            if (!ret.apply(ff.featureIDHashmap(fex))) return false
        }
        true
    }

    /**
     * This method assumes that all features in the parameter-set appear in the configuration
     * (either as true or as false)
     * @param features given feature set
     * @return
     */
    def containsAllFeaturesAsDisabled(features: Set[SingleFeatureExpr]): Boolean = {
        for (fex <- features) {
            if (ret.apply(ff.featureIDHashmap(fex))) return false
        }
        true
    }

    def containsAtLeastOneFeatureAsEnabled(set: Set[SingleFeatureExpr]): Boolean =
        !containsAllFeaturesAsDisabled(set)

    def containsAtLeastOneFeatureAsDisabled(set: Set[SingleFeatureExpr]): Boolean =
        !containsAllFeaturesAsEnabled(set)

    override def equals(other: Any): Boolean = {
        if (!other.isInstanceOf[SimpleConfiguration]) super.equals(other)
        else {
            val otherSC = other.asInstanceOf[SimpleConfiguration]
            otherSC.ret.equals(this.ret)
        }
    }

    override def hashCode(): Int = ret.hashCode()
}
