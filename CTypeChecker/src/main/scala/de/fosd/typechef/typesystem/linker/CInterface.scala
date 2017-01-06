package de.fosd.typechef.typesystem.linker

import de.fosd.typechef.error.Position
import de.fosd.typechef.featureexpr.FeatureExprFactory.{False, True}
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory, FeatureModel}
import de.fosd.typechef.typesystem.{CInt, CSigned, _}


/**
  * linking can be strict or less strict on what it considers a match to
  * an import.
  *
  * At the lowest level NAMEONLY, the linker checks only the names of symbols, not their types.
  *
  * At the highest level it expects that the signatures match exactly, including their types.
  * Only few equivalence rules are applied by type normalization in the type system (e.g., int foo(void) == int foo()).
  * Used pointers and structs must also match perfectly.
  *
  * At the medium level, which may be the most practical one, matches are determined (roughly) using
  * coercion rules. Here "int foo(* struct x)" is compatible with "int foo(*void)" and
  * "int foo(int)" is compatible with "int foo(short)"
  *
  * at less stricter comparisons, types and extraflags after merging are copied from one of
  * the sources, but (pseudo-randomly) from the first import.
  */
sealed trait Strictness

object LINK_STRICT extends Strictness

object LINK_RELAXED extends Strictness

object LINK_NAMEONLY extends Strictness

/**
  * describes the linker interface for a file, i.e. all imported (and used)
  * signatures and all exported signatures.
  */
case class CInterface(
                         featureModel: FeatureExpr,
                         importedFeatures: Set[String],
                         declaredFeatures: Set[String], //not inferred
                         imports: Seq[CSignature],
                         exports: Seq[CSignature]) {


    def this(imports: Seq[CSignature], exports: Seq[CSignature]) = this(True, Set(), Set(), imports, exports)

    override def toString =
        "fm " + featureModel + "\n" +
            "features (" + importedFeatures.size + ")\n\t" + importedFeatures.toList.sorted.mkString(", ") +
            (if (declaredFeatures.isEmpty) "" else "declared features (" + declaredFeatures.size + ")\n\t" + declaredFeatures.toList.sorted.mkString(", ")) +
            "\nimports (" + imports.size + ")\n" + sortedImports.map("\t" + _.toString).mkString("\n") +
            "\nexports (" + exports.size + ")\n" + sortedExports.map("\t" + _.toString).mkString("\n") + "\n"

    lazy val importsByName = imports.groupBy(_.name)
    lazy val exportsByName = exports.groupBy(_.name)

    def sortedImports = imports.sortWith((a, b) => a.name < b.name || (a.name == b.name && a.pos.toString < b.pos.toString))
    def sortedExports = imports.sortWith((a, b) => a.name < b.name || (a.name == b.name && a.pos.toString < b.pos.toString))


    lazy val getInterfaceFeatures: Set[String] = {
        var result: Set[String] = Set()

        def addFeatures(featureExpr: FeatureExpr) {
            result = result ++ featureExpr.collectDistinctFeatures
        }

        addFeatures(featureModel)
        imports.map(s => addFeatures(s.fexpr))
        exports.map(s => addFeatures(s.fexpr))

        result
    }

    /**
      * joins multiple exports with the same name
      * removes imports that are available as exports in the same file
      * removes False imports
      * does NOT join multiple imports with the same name but from different locations (makes debugging difficult, use deduplicateImports instead)
      *
      * how to determine whether two elements are duplicate depends on the strictness level (see above)
      *
      * exports are not packed beyond removing False exports.
      * duplicate exports are used for error detection
      */
    def pack(strictness: Strictness = LINK_STRICT): CInterface = if (isPacked) this
    else
        CInterface(featureModel, importedFeatures -- declaredFeatures, declaredFeatures,
            packImports(true, strictness), packExports).setPacked

    def packWithOutElimination(strictness: Strictness = LINK_STRICT): CInterface = if (isPacked) this
    else
        CInterface(featureModel, importedFeatures -- declaredFeatures, declaredFeatures,
            packImports(false, strictness), packExports).setPacked


    def deduplicateImports(strictness: Strictness = LINK_STRICT): CInterface = if (isDedupImport) this
    else
        CInterface(featureModel, importedFeatures, declaredFeatures,
            _deduplicateImports(strictness), exports).setDedupImport

    private var isPacked = false;
    protected def setPacked() = {
        isPacked = true;
        this
    }
    private var isDedupImport = false;
    protected def setDedupImport() = {
        isDedupImport = true;
        this
    }

    protected def genComparisonKey(sig: CSignature, strictness: Strictness): Object = strictness match {
        case LINK_STRICT => (sig.name, sig.ctype, sig.extraFlags)
        case LINK_NAMEONLY => sig.name
        case LINK_RELAXED => (sig.name, relaxType(sig.ctype))
    }

    protected def _deduplicateImports(strictness: Strictness = LINK_STRICT): Seq[CSignature] = {
        var importMap = Map[Object, (CSignature, FeatureExpr, Seq[Position])]()

        //eliminate duplicates with a map
        for (imp <- imports if ((featureModel and imp.fexpr).isSatisfiable())) {
            val key = genComparisonKey(imp, strictness)
            val old = importMap.getOrElse(key, (imp, False, Seq()))
            importMap = importMap + (key ->(old._1, old._2 or imp.fexpr, old._3 ++ imp.pos))
        }

        val r = for (v <- importMap.values)
            yield CSignature(v._1.name, v._1.ctype, v._2, v._3, v._1.extraFlags)
        r.toSeq
    }


    /**
      * updates the conditions of imports by matching them against
      * exports
      *
      * @param eliminateInfeasible if true, remove all imports that have infeasible conditions; otherwise
      *                            they remain with updated but infeasible conditions
      * @param strictness
      * @return updated list of imports
      */
    protected def packImports(eliminateInfeasible: Boolean, strictness: Strictness = LINK_STRICT): Seq[CSignature] = {
        var exportMap = Map[Object, FeatureExpr]()

        //index all exports
        for (exp <- exports) {
            val key = genComparisonKey(exp, strictness)
            exportMap += (key -> (exp.fexpr or exportMap.getOrElse(key, False)))
        }

        val newImports =
            for (imp <- imports) yield {
                val key = genComparisonKey(imp, strictness)
                val exportCondition = exportMap.getOrElse(key, False)
                val newFexpr = imp.fexpr andNot exportCondition
                if (!eliminateInfeasible || (featureModel and newFexpr).isSatisfiable())
                    Some(imp.copy(fexpr = newFexpr))
                else
                    None
            }

        newImports.flatten
    }


    protected def packExports: Seq[CSignature] = exports.filter(_.fexpr.and(featureModel).isSatisfiable())


    /**
      * rewrites a type for relatex matching with LINK_RELAXED
      *
      * this makes a number of simplifications in types, including considering all
      * integers and structs and pointers as equivalent
      */
    private def relaxType(ctype: CType): CType = ctype.atype match {
        case CFunction(p, r) => CFunction(p.map(_relaxType), _relaxType(r))
        case v => _relaxType(v)
    }

    private def _relaxType(ctype: CType): CType = ctype.atype match {
        //consider all scalar types as equivalent
        case c: CSignSpecifier => CSigned(CInt())
        //consider all pointers and functions and arrays as equivalent
        case c: CPointer => CPointer(CVoid())
        case f: CFunction => CPointer(CVoid())
        case f: CArray => CPointer(CVoid())
        //consider all structs (not pointers to structs) as equivalent
        case s: CStruct => CStruct("")
        case s: CAnonymousStruct => CStruct("")
        //void and ... remain unmodified
        case v: CVoid => v
        case v: CVarArgs => v
        case v: CFloat => v
        case v: CDouble => v
        case v: CLongDouble => CDouble()
        case v: CIgnore => v
        case v: CUnknown => CIgnore()
        case v: CZero => CPointer(CVoid())
        case v: CBuiltinVaList => v
        case v: CCompound => CPointer(CVoid())
        case v: CBool => CSigned(CInt())
    }

    /**
      * ensures a couple of invariants.
      *
      * a module is illformed if
      * (a) it exports the same signature twice in the same configuration
      * (b) it imports the same signature twice in the same configuration
      * (c) if it exports and imports a name in the same configuration
      *
      * by construction, this should not occur in inferred and linked interfaces
      */
    def isWellformed: Boolean = {
        val exportsByName = exports.groupBy(_.name)
        val importsByName = imports.groupBy(_.name)

        var wellformed = true
        for (funName <- (exportsByName.keySet ++ importsByName.keySet)) {
            val sigs = exportsByName.getOrElse(funName, Seq()) ++ importsByName.getOrElse(funName, Seq())

            if (wellformed && !mutuallyExclusive(sigs)) {
                wellformed = false
                println(funName + " imported/exported multiple times in the same configuration: \n" + sigs.mkString("\t", "\n\t", "\n"))
            }
        }

        wellformed
    }


    def link(that: CInterface, strictness: Strictness = LINK_STRICT): CInterface =
        CInterface(
            this.featureModel and that.featureModel and inferConstraintsWith(that),
            this.importedFeatures ++ that.importedFeatures,
            this.declaredFeatures ++ that.declaredFeatures,
            this.imports ++ that.imports,
            this.exports ++ that.exports
        ).pack(strictness)

    def linkWithOutElimination(that: CInterface, strictness: Strictness = LINK_STRICT): CInterface =
        CInterface(
            this.featureModel and that.featureModel and inferConstraintsWith(that),
            this.importedFeatures ++ that.importedFeatures,
            this.declaredFeatures ++ that.declaredFeatures,
            this.imports ++ that.imports,
            this.exports ++ that.exports
        ).packWithOutElimination(strictness)

    /** links without proper checks and packing. only for debugging purposes **/
    def debug_join(that: CInterface): CInterface =
        CInterface(
            this.featureModel and that.featureModel,
            this.importedFeatures ++ that.importedFeatures,
            this.declaredFeatures ++ that.declaredFeatures,
            this.imports ++ that.imports,
            this.exports ++ that.exports
        )


    /**
      * determines conflicts and returns corresponding name, feature expression and involved signatures
      *
      * conflicts are:
      * (a) both modules export the same name in the same configuration
      * (b) both modules import the same name with different types in the same configuration
      * (c) one module imports a name the other modules exports in the same configuration but with a different type
      *
      * returns any conflict (does not call a sat solver), even if the conditions are mutually exclusive.
      * the condition is true if there is NO conflict (it describes configurations without conflict)
      *
      * public only for debugging purposes
      */
    def getConflicts(that: CInterface): List[(String, FeatureExpr, Seq[CSignature])] =
        CInterface.presenceConflicts(this.exportsByName, that.exportsByName) ++
            CInterface.typeConflicts(this.importsByName, that.importsByName) ++
            CInterface.typeConflicts(this.importsByName, that.exportsByName) ++
            CInterface.typeConflicts(this.exportsByName, that.importsByName)

    /**
      * when there is an overlap in the exports, infer constraints which must be satisfied
      * to not have a problem
      */
    private def inferConstraintsWith(that: CInterface): FeatureExpr =
        getConflicts(that).foldLeft(FeatureExprFactory.True)(_ and _._2)


    def and(f: FeatureExpr): CInterface =
        CInterface(
            featureModel,
            importedFeatures,
            declaredFeatures,
            imports.map(_ and f),
            exports.map(_ and f)
        )

    def andFM(feature: FeatureExpr): CInterface = mapFM(_ and feature)
    def mapFM(f: FeatureExpr => FeatureExpr) = CInterface(f(featureModel), importedFeatures, declaredFeatures, imports, exports)


    /**
      * linking two well-formed models always yields a wellformed module, but it makes
      * only sense if the resulting feature model is not void.
      * hence compatibility is checked by checking the resulting feature model
      */
    def isCompatibleTo(that: CInterface): Boolean =
        (this link that).featureModel.isSatisfiable() &&
            this.declaredFeatures.intersect(that.declaredFeatures).isEmpty

    def isCompatibleTo(thatSeq: Seq[CInterface]): Boolean = {
        var m = this
        for (that <- thatSeq) {
            if (!(m isCompatibleTo that)) return false
            m = m link that
        }
        return true
    }


    /**
      * A variability-aware module is complete if it has no remaining imports with
      * satisfiable conditions and if the feature model is satisfiable (i.e., it allows to derive
      * at least one variant). A complete and fully-configured module is the desired end
      * result when configuring a product line for a specific use case.
      */
    def isComplete(strictness: Strictness = LINK_STRICT): Boolean = featureModel.isSatisfiable && pack(strictness).imports.isEmpty

    def isFullyConfigured(strictness: Strictness = LINK_STRICT): Boolean =
        pack(strictness).imports.forall(s => (featureModel implies s.fexpr).isTautology) &&
            pack(strictness).exports.forall(s => (featureModel implies s.fexpr).isTautology)

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

    private def mutuallyExclusive(sigs: Seq[CSignature]): Boolean = if (sigs.size <= 1) true
    else {
        val pairs = for (a <- sigs.tails.take(sigs.size); b <- a.tail)
            yield (a.head.fexpr, b.fexpr)
        val formula = featureModel implies pairs.foldLeft(True)((a, b) => a and (b._1 mex b._2))
        formula.isTautology
    }

}

object CInterface {

    private[linker] def apply(fm: FeatureExpr, imp: Seq[CSignature], exp: Seq[CSignature]): CInterface =
        CInterface(fm, Set(), Set(), imp, exp)

    /**
      * signatures from a and b must not share a presence condition
      */
    private def presenceConflicts(a: Map[String, Seq[CSignature]], b: Map[String, Seq[CSignature]]): List[(String, FeatureExpr, Seq[CSignature])] = {
        var result: List[(String, FeatureExpr, Seq[CSignature])] = List()

        for (signame <- a.keys)
            if (b.contains(signame)) {
                val aa = a(signame)
                val bb = b(signame)
                val conflictExpr = disjointSigFeatureExpr(aa) mex disjointSigFeatureExpr(bb)
                result = (signame, conflictExpr, aa ++ bb) :: result
            }
        result
    }

    /**
      * signatures from a and b must not differ in type for the same configuration
      */
    private def typeConflicts(a: Map[String, Seq[CSignature]], b: Map[String, Seq[CSignature]]): List[(String, FeatureExpr, Seq[CSignature])] = {
        var result: List[(String, FeatureExpr, Seq[CSignature])] = List()

        for (signame <- a.keys)
            if (b.contains(signame)) {
                val aa = a(signame)
                val bb = b(signame)

                for (asig <- aa; bsig <- bb)
                    if (asig.ctype != bsig.ctype) //TODO use coerce
                        result = (signame, asig.fexpr mex bsig.fexpr, Seq(asig, bsig)) :: result
            }

        result
    }


    private def disjointSigFeatureExpr(a: Seq[CSignature]): FeatureExpr = a.foldLeft(False)(_ or _.fexpr)


    //    /**
    //     * debugging information, underlying inferConstraints
    //     *
    //     * describes which method is exported twice under which constraints
    //     */
    //    def getConflicts(that: CInterface): Map[String, Seq[CSignature]] = {
    //        val aa = this.exports.groupBy(_.name)
    //        val bb = that.exports.groupBy(_.name)
    //        var result = Map[String, Seq[CSignature]]()
    //
    //        //two sets of signatures with the same name
    //        //(a1 or a2 or a3) mex (b1 or b2 or b3)
    //        def addConstraint(a: Seq[CSignature], b: Seq[CSignature]) =
    //            a.foldLeft(False)(_ or _.fexpr) mex b.foldLeft(False)(_ or _.fexpr)
    //
    //        for (signame <- aa.keys)
    //            if (bb.contains(signame)) {
    //                val c = addConstraint(aa(signame), bb(signame))
    //                if (!c.isSatisfiable())
    //                    result = result + (signame -> (aa(signame) ++ bb(signame)))
    //            }
    //        result
    //    }

}

object EmptyInterface extends CInterface(FeatureExprFactory.True, Set(), Set(), Seq(), Seq())