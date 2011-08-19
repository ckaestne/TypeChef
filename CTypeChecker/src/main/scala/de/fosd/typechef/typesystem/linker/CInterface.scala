package de.fosd.typechef.typesystem.linker

import de.fosd.typechef.typesystem.CType
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.Position
import de.fosd.typechef.featureexpr.FeatureExpr.{base, dead}

/**
 * describes the linker interface for a file, i.e. all imported (and used)
 * signatures and all exported signatures.
 */
case class CInterface(imports: Seq[CSignature], exports: Seq[CSignature]) {
    override def toString =
        "imports\n" + imports.map("\t" + _.toString).mkString("\n") +
                "\nexports\n" + exports.map("\t" + _.toString).mkString("\n") + "\n"


    /**
     * removes duplicates by joining the corresponding conditions
     * removes imports that are available as exports in the same file
     *
     * two elements are duplicate if they have the same name and type
     *
     * exports are not packed. duplicate exports are needed for error detection!
     */
    def pack: CInterface = CInterface(packImports, exports)
    private def packImports: Seq[CSignature] = {
        var importMap = Map[(String, CType), (FeatureExpr, Seq[Position])]()

        //eliminate duplicates with a map
        for (imp <- imports) {
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
                if (newFexpr.isSatisfiable())
                    importMap = importMap + (key -> (newFexpr, oldPos))
                else
                    importMap = importMap - key
            }
        }


        val r = for ((k, v) <- importMap.iterator)
        yield CSignature(k._1, k._2, v._1, v._2)
        r.toSeq
    }


    /**
     * a module is illformed if it exports the same signature twice
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
        val formula = pairs.foldLeft(base)((a, b) => a and (b._1 mex b._2))
        formula.isTautology
    }


    def link(that: CInterface): CInterface = this

}