package de.fosd.typechef.typesystem.linker

import de.fosd.typechef.typesystem.CType
import de.fosd.typechef.parser.Position
import de.fosd.typechef.featureexpr.FeatureExpr.{base, dead}
import de.fosd.typechef.featureexpr.{FeatureModel, FeatureExpr}

/**
 * describes the linker interface for a file, i.e. all imported (and used)
 * signatures and all exported signatures.
 */
case class CInterface(featureModel: FeatureExpr, imports: Seq[CSignature], exports: Seq[CSignature]) {


    def this(imports: Seq[CSignature], exports: Seq[CSignature]) = this (base, imports, exports)

    override def toString =
        "fm " + featureModel + "\n" +
                "imports\n" + imports.map("\t" + _.toString).mkString("\n") +
                "\nexports\n" + exports.map("\t" + _.toString).mkString("\n") + "\n"


    /**
     * removes duplicates by joining the corresponding conditions
     * removes imports that are available as exports in the same file
     * removes dead imports
     *
     * two elements are duplicate if they have the same name and type
     *
     * exports are not packed beyond removing dead exports.
     * duplicate exports are used for error detection
     */
    def pack: CInterface = if (isPacked) this else CInterface(featureModel, packImports, packExports).setPacked
    private var isPacked = false;
    private def setPacked() = {isPacked = true; this}
    private def packImports: Seq[CSignature] = {
        var importMap = Map[(String, CType), (FeatureExpr, Seq[Position])]()

        //eliminate duplicates with a map
        for (imp <- imports if ((featureModel and imp.fexpr).isSatisfiable())) {
            val key = (imp.name, imp.ctype)
            val old = importMap.getOrElse(key, (dead, Seq()))
            importMap = importMap + (key -> (old._1 or imp.fexpr, old._2 ++ imp.pos))
        }
        //eliminate imports that have corresponding exports
        for (exp <- exports) {
            val key = (exp.name, exp.ctype)
            if (importMap.contains(key)) {
                val (oldFexpr, oldPos) = importMap(key)
                val newFexpr = oldFexpr andNot exp.fexpr
                if ((featureModel and newFexpr).isSatisfiable())
                    importMap = importMap + (key -> (newFexpr, oldPos))
                else
                    importMap = importMap - key
            }
        }


        val r = for ((k, v) <- importMap.iterator)
        yield CSignature(k._1, k._2, v._1, v._2)
        r.toSeq
    }
    private def packExports: Seq[CSignature] = exports.filter(_.fexpr.and(featureModel).isSatisfiable())


    /**
     * a module is illformed if it exports the same signature twice
     *
     * by construction, this should not occur in inferred and linked interfaces
     */
    def isWellformed: Boolean = {
        val exportsByName = exports.groupBy(_.name)

        var wellformed = true
        for (ex <- exportsByName.values)
            if (wellformed && !mutuallyExclusive(ex)) {
                wellformed = false
                println(ex.head.name + " exported multiple times: \n" + ex.mkString("\n"))
            }
        wellformed
    }
    private def mutuallyExclusive(sigs: Seq[CSignature]): Boolean = if (sigs.size <= 1) true
    else {
        val pairs = for (a <- sigs.tails.take(sigs.size); b <- a.tail)
        yield (a.head.fexpr, b.fexpr)
        val formula = featureModel implies pairs.foldLeft(base)((a, b) => a and (b._1 mex b._2))
        formula.isTautology
    }


    def link(that: CInterface): CInterface =
        CInterface(
            this.featureModel and that.featureModel and inferConstraints(this.exports, that.exports),
            this.imports ++ that.imports,
            this.exports ++ that.exports
        ).pack

    /**
     * when there is an overlap in the exports, infer constraints which must be satisfied
     * to not have a problem
     */
    private def inferConstraints(a: Seq[CSignature], b: Seq[CSignature]): FeatureExpr = {
        val aa = a.groupBy(_.name)
        val bb = b.groupBy(_.name)
        var result = base

        //two sets of signatures with the same name
        //(a1 or a2 or a3) mex (b1 or b2 or b3)
        def addConstraint(a: Seq[CSignature], b: Seq[CSignature]) =
            a.foldLeft(dead)(_ or _.fexpr) mex b.foldLeft(dead)(_ or _.fexpr)

        for (signame <- aa.keys)
            if (bb.contains(signame))
                result = result and addConstraint(aa(signame), bb(signame))
        result
    }


    def and(f: FeatureExpr): CInterface =
        CInterface(
            featureModel,
            imports.map(_ and f),
            exports.map(_ and f)
        )

    def andFM(feature: FeatureExpr): CInterface = mapFM(_ and feature)
    def mapFM(f: FeatureExpr => FeatureExpr) = CInterface(f(featureModel), imports, exports)


    /**
     * linking two well-formed models always yields a wellformed module, but it makes
     * only sense if the resulting feature model is not void.
     * hence compatibility is checked by checking the resulting feature model
     */
    def isCompatibleTo(that: CInterface): Boolean = (this link that).featureModel.isSatisfiable()

    /**
     * A variability-aware module is complete if it has no remaining imports with
     * satisfiable conditions and if the feature model is satisfiable (i.e., it allows to derive
     * at least one variant). A complete and fully-configured module is the desired end
     * result when configuring a product line for a specific use case.
     */
    def isComplete: Boolean = featureModel.isSatisfiable && pack.imports.isEmpty

    def isFullyConfigured: Boolean =
        pack.imports.forall(s => (featureModel implies s.fexpr).isTautology) &&
                pack.exports.forall(s => (featureModel implies s.fexpr).isTautology)

    /**
     * we can use a global feature model to ensure that composing modules
     * reflects the intended dependencies. This way, we can detect that we do not
     * accidentally restrict the product line more than intended by the domain expert
     * who designed the global feature model. We simply compare the feature model of
     * the linker result with a global model.
     */
    def compatibleWithGlobalFeatureModel(globalFM: FeatureExpr): Boolean =
        (globalFM implies featureModel).isTautology
    def compatibleWithGlobalFeatureModel(globalFM: FeatureModel): Boolean =
        featureModel.isTautology(globalFM)

    /**
     * turns the interface into a conditional interface (to emulate conditional
     * linking/composition of interfaces).
     *
     *
     * a.conditional(f) link b.conditional(g)
     *
     * is conceptually equivalent to
     *
     * if (f and g) a link b
     * else if (f and not g) a
     * else if (not f and g) b
     * else empty
     *
     * see text on conditional composition
     */
    def conditional(condition: FeatureExpr): CInterface =
        this.and(condition).mapFM(condition implies _)
}