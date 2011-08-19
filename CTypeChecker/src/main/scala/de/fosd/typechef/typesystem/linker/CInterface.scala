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
    def pack: CInterface = CInterface(featureModel, packImports, packExports)
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
        val formula = featureModel and pairs.foldLeft(base)((a, b) => a and (b._1 mex b._2))
        formula.isTautology
    }


    def link(that: CInterface): CInterface =
        CInterface(
            this.featureModel and that.featureModel,
            this.imports ++ that.imports,
            this.exports ++ that.exports
        ).pack


    def and(f: FeatureExpr): CInterface =
        CInterface(
            featureModel,
            imports.map(_ and f),
            exports.map(_ and f)
        )

}