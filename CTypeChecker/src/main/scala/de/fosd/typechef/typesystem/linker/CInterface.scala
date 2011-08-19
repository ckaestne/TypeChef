package de.fosd.typechef.typesystem.linker

import de.fosd.typechef.typesystem.CType
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.Position
import de.fosd.typechef.featureexpr.FeatureExpr.{base, dead}

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

    def andFM(f: FeatureExpr): CInterface = CInterface(featureModel and f, imports, exports)


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
}