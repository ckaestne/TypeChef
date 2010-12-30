package de.fosd.typechef.featureexpr

import scala.collection.mutable.WeakHashMap

import LazyLib._
import ref.WeakReference

object FeatureExpr {
    def resolveDefined(feature: DefinedMacro, macroTable: FeatureProvider): FeatureExpr =
        macroTable.getMacroCondition(feature.name)

    def createComplement(expr: FeatureExpr) = new FeatureExpr(UnaryFeatureExprTree(expr.expr, "~", ~_))
    def createNeg(expr: FeatureExpr) = new FeatureExpr(UnaryFeatureExprTree(expr.expr, "-", -_))
    def createBitAnd(left: FeatureExpr, right: FeatureExpr) = new FeatureExpr(BinaryFeatureExprTree(left.expr, right.expr, "&", _ & _))
    def createBitOr(left: FeatureExpr, right: FeatureExpr) = new FeatureExpr(BinaryFeatureExprTree(left.expr, right.expr, "|", _ | _))
    def createDivision(left: FeatureExpr, right: FeatureExpr) = new FeatureExpr(BinaryFeatureExprTree(left.expr, right.expr, "/", _ / _))
    def createModulo(left: FeatureExpr, right: FeatureExpr) = new FeatureExpr(BinaryFeatureExprTree(left.expr, right.expr, "%", _ % _))
    def createEquals(left: FeatureExpr, right: FeatureExpr) = new FeatureExpr(BinaryFeatureExprTree(left.expr, right.expr, "==", (a, b) => if (a == b) 1 else 0))
    def createNotEquals(left: FeatureExpr, right: FeatureExpr) = new FeatureExpr(BinaryFeatureExprTree(left.expr, right.expr, "!=", (a, b) => if (a != b) 1 else 0))
    def createLessThan(left: FeatureExpr, right: FeatureExpr) = new FeatureExpr(BinaryFeatureExprTree(left.expr, right.expr, "<", (a, b) => if (a < b) 1 else 0))
    def createLessThanEquals(left: FeatureExpr, right: FeatureExpr) = new FeatureExpr(BinaryFeatureExprTree(left.expr, right.expr, "<=", (a, b) => if (a <= b) 1 else 0))
    def createGreaterThan(left: FeatureExpr, right: FeatureExpr) = new FeatureExpr(BinaryFeatureExprTree(left.expr, right.expr, ">", (a, b) => if (a > b) 1 else 0))
    def createGreaterThanEquals(left: FeatureExpr, right: FeatureExpr) = new FeatureExpr(BinaryFeatureExprTree(left.expr, right.expr, ">=", (a, b) => if (a >= b) 1 else 0))
    def createMinus(left: FeatureExpr, right: FeatureExpr) = new FeatureExpr(BinaryFeatureExprTree(left.expr, right.expr, "-", _ - _))
    def createMult(left: FeatureExpr, right: FeatureExpr) = new FeatureExpr(BinaryFeatureExprTree(left.expr, right.expr, "*", _ * _))
    def createPlus(left: FeatureExpr, right: FeatureExpr) = new FeatureExpr(BinaryFeatureExprTree(left.expr, right.expr, "+", _ + _))
    def createPwr(left: FeatureExpr, right: FeatureExpr) = new FeatureExpr(BinaryFeatureExprTree(left.expr, right.expr, "^", _ ^ _))
    def createShiftLeft(left: FeatureExpr, right: FeatureExpr) = new FeatureExpr(BinaryFeatureExprTree(left.expr, right.expr, "<<", _ << _))
    def createShiftRight(left: FeatureExpr, right: FeatureExpr) = new FeatureExpr(BinaryFeatureExprTree(left.expr, right.expr, ">>", _ >> _))
    def createInteger(value: Long): FeatureExpr = new FeatureExpr(IntegerLit(value))
    def createCharacter(value: Char): FeatureExpr = new FeatureExpr(IntegerLit(value))


    //caching to reduce number of objects and enable test for pointer equality
    private var definedExternalCache: WeakHashMap[String, FeatureExpr] = WeakHashMap()
    def createDefinedExternal(name: String): FeatureExpr = definedExternalCache.getOrElseUpdate(name,
        new FeatureExpr(new DefinedExternal(name)))
    //create a macro definition (which expands to the current entry in the macro table; the current entry is stored in a closure-like way).
    //a form of caching provided by MacroTable, which we need to repeat here to create the same FeatureExpr object
    private var definedMacroCache: WeakHashMap[DefinedMacro, FeatureExpr] = WeakHashMap()
    def createDefinedMacro(name: String, macroTable: FeatureProvider): FeatureExpr = {
        val macroCondition = macroTable.getMacroCondition(name)
        if (macroCondition.isSmall) {
            macroCondition
        } else {
            val macroConditionCNF = macroTable.getMacroConditionCNF(name)
            val definedMacro = new DefinedMacro(
                name,
                macroTable.getMacroCondition(name),
                macroConditionCNF._1,
                macroConditionCNF._2)
            definedMacroCache.getOrElseUpdate(definedMacro, new FeatureExpr(definedMacro))
        }
    }

    //helper
    def createIf(condition: FeatureExpr, thenBranch: FeatureExpr, elseBranch: FeatureExpr) = new FeatureExpr(IfExpr(condition.expr, thenBranch.expr, elseBranch.expr))
    def createIf(condition: FeatureExprTree, thenBranch: FeatureExprTree, elseBranch: FeatureExprTree) = new FeatureExpr(IfExpr(condition, thenBranch, elseBranch))
    def createImplies(left: FeatureExpr, right: FeatureExpr) = left implies right
    def createEquiv(left: FeatureExpr, right: FeatureExpr) = left equiv right

    val base: FeatureExpr = new FeatureExpr(BaseFeature())
    val dead: FeatureExpr = new FeatureExpr(DeadFeature())

    private var freshFeatureNameCounter = 0
    def calcFreshFeatureName(): String = {
        freshFeatureNameCounter = freshFeatureNameCounter + 1;
        "__fresh" + freshFeatureNameCounter;
    }
}


/**
 * feature expressions
 *
 * may be simplified. caches results on most operations
 *
 * feature expressions are compared on object identity (comparing them for equivalence is
 * an additional but expensive operation). propositions such as and or and not
 * cache results, so that the operation yields identical results on identical parameters
 */
class FeatureExpr private[featureexpr](var aexpr: FeatureExprTree) {
    /**caches */
    private[FeatureExpr] val andCache: WeakHashMap[FeatureExpr, FeatureExpr] = new WeakHashMap()
    private[FeatureExpr] val orCache: WeakHashMap[FeatureExpr, FeatureExpr] = new WeakHashMap()
    private[FeatureExpr] var notCache: FeatureExpr = null
    private val cacheIsSatisfiable: WeakHashMap[FeatureModel, Boolean] = WeakHashMap()
    private var cnfCache: NF = null;
    private var equiCnfCache: NF = null;


    def expr: FeatureExprTree = aexpr

    def simplify(): FeatureExpr = {
        this.aexpr = aexpr.simplify;
        this
    }

    /**
     * transform the underlying expression into CNF (not equiCNF)
     * this can be used as a form of simplification, but can be
     * quite expensive for large formula
     *
     * call only if you know what you are doing
     */
    def normalize(): FeatureExpr = {
        aexpr = NFBuilder.CNFtoFeatureExpr(this.toCNF)
        this
    }

    def and(that: FeatureExpr): FeatureExpr =
        if (that eq this) this
        else
            andCache.getOrElseUpdate(that,
                that.andCache.getOrElseUpdate(this,
                    new FeatureExpr(And(this.expr, that.expr))))


    def or(that: FeatureExpr): FeatureExpr =
        if (that eq this) this
        else
            orCache.getOrElseUpdate(that,
                that.orCache.getOrElseUpdate(this,
                    new FeatureExpr(Or(this.expr, that.expr))))

    def not(): FeatureExpr = {
        if (notCache == null) {
            notCache = new FeatureExpr(Not(this.expr))
            notCache.notCache = this //applying not again will lead back to this object
        }
        notCache
    }
    def implies(that: FeatureExpr): FeatureExpr = this.not or that
    //mutual exclusion
    def mex(that: FeatureExpr): FeatureExpr = (this and that).not

    def equiv(that: FeatureExpr): FeatureExpr = (this implies that) and (that implies this)


    def toCNF: NF =
        if (cnfCache != null)
            cnfCache
        else
            try {
                simplify
                cnfCache = NFBuilder.toCNF(expr.toCNF)
                cnfCache
            } catch {
                case t: Throwable => {
                    System.err.println("Exception on toCNF for: " + expr.print())
                    t.printStackTrace
                    throw t
                }
            }
    def toEquiCNF: NF =
        if (cnfCache != null)
            cnfCache
        else if (equiCnfCache != null)
            equiCnfCache
        else
            try {
                simplify
                equiCnfCache = NFBuilder.toCNF(expr.toCnfEquiSat)
                equiCnfCache
            } catch {
                case t: Throwable => {
                    System.err.println("Exception on toEquiCNF for: " + expr.print())
                    t.printStackTrace
                    throw t
                }
            }

    def isContradiction(): Boolean = isContradiction(null)
    def isTautology(): Boolean = isTautology(null)
    def isDead(): Boolean = isContradiction(null)
    def isBase(): Boolean = isTautology(null)
    def isSatisfiable(): Boolean = isSatisfiable(null)
    /**
     * FM -> X is tautology if FM.implies(X).isTautology or
     * !FM.and.(x.not).isSatisfiable
     *
     **/
    def isTautology(fm: FeatureModel): Boolean = !this.not.isSatisfiable(fm)
    def isContradiction(fm: FeatureModel): Boolean = !isSatisfiable(fm)
    /**
     * x.isSatisfiable(fm) is short for x.and(fm).isSatisfiable
     * but is faster because FM is cached
     */
    def isSatisfiable(fm: FeatureModel): Boolean =
        cacheIsSatisfiable.getOrElseUpdate(fm, {
            val isSat = new SatSolver().isSatisfiable(toEquiCNF, fm)
            //an unsatisfiable expression can be simplified to DEAD
            if (!isSat) {
                aexpr = DeadFeature()
                cache_external = this
                equiCnfCache = null
                cnfCache = null
                andCache.clear
                orCache.clear
                notCache = FeatureExpr.base
            }
            isSat
        })

    override def toString(): String = {
        simplify;
        this.print()
    }

    def accept(f: FeatureExprTree => Unit): Unit = {
        simplify();
        expr.accept(f)
    }

    def print(): String = {
        simplify;
        aexpr.print()
    }

    def debug_print(): String = {
        simplify;
        expr.debug_print(0);
    }

    /**
     * do not use equals on feature expressions!
     *
     * use pointer equivalence (eq) if you want to compare
     * whether two expressions are identical (caching
     * ensures that creating "a and b" twice from the same
     * objects will yield the same object).
     * hashCode and equals will act just as pointer equivalence
     *
     * use equivalentTo if you want to compare whether expressions
     * are equivalent. note that this is an expensive operation
     * that requires a SAT solver (hashCode won't help to narrow
     * possible candidates down!)
     *
     * finally, you may call equals on the internal expressions,
     * but be aware that those may change due to simplification
     * and that equality between two objects of FeatureExprTree
     * does not tell you anything but that they have the exact same
     * structure (for example: a and b != b and a)
     */
    final override def equals(that: Any) = super.equals(that)
    final override def hashCode = super.hashCode
    /**
     * uses a SAT solver to determine whether two expressions are
     * equivalent.
     *
     * for performance reasons, it checks pointer
     * equivalence first, but won't use the recursive equals on aexpr
     * (there should only be few cases when equals is more
     * accurate than eq, which are not worth the performance
     * overhead)
     */
    def equivalentTo(that: FeatureExpr): Boolean = (this eq that) || (this.expr eq that.expr) ||
            FeatureExpr.createEquiv(this, that).isTautology();

    /**
     * checks whether there is some unresolved macro (DefinedMacro) somewhere
     * in the expression tree
     */
    def isResolved() = aexpr.isResolved

    var cache_external: FeatureExpr = null;
    /**
     * replace DefinedMacro by DefinedExternal from MacroTable
     */
    def resolveToExternal(): FeatureExpr = {
        if (cache_external == null) {
            simplify
            cache_external = new FeatureExpr(aexpr.resolveToExternal().simplify)
        }
        cache_external
    }

    /**
     * used only internally do determine whether to expand an DefinedMacro() or not during creation
     */
    def isSmall(): Boolean = {
        simplify
        expr.isSmall
    }
}

sealed abstract class FeatureExprTree {
    //optimization to not simplify the same expression over and over again
    private var isSimplified: Boolean = false
    private def setSimplified(): FeatureExprTree = {
        isSimplified = true;
        return this
    }
    def simplify(): FeatureExprTree = {
        if (isSimplified)
            this
        else {
            val result = this bubbleUpIf match {
                case And(children) => {
                    val childrenSimplified = children.map(_.simplify().intToBool()).filter(!BaseFeature.unapply(_)); //TODO also remove all non-zero integer literals
                    var childrenFlattened: Seq[FeatureExprTree] = SmallList() //computing sets is too expensive
                    for (childs <- childrenSimplified)
                        childs match {
                            case And(innerChildren) => childrenFlattened = childrenFlattened ++ innerChildren
                            case e => childrenFlattened = e +: childrenFlattened
                        }
                    //only apply these operations on small expressions, because they are rather expensive
                    if (isSmall) {
                        childrenFlattened = childrenFlattened.distinct
                        for (childs <- childrenFlattened)
                            if (childrenFlattened.exists(_ == Not(childs)))
                                return DeadFeature();
                    }
                    if (childrenFlattened.exists(DeadFeature.unapply(_)))
                    /*return*/
                        DeadFeature()
                    else if (childrenFlattened.size == 1)
                    /*return*/
                        (childrenFlattened.iterator).next()
                    else if (childrenFlattened.size == 0)
                    /*return*/
                        BaseFeature()
                    else
                    /*return*/
                        And(childrenFlattened)
                }

                case Or(c) => {
                    var children = c
                    val childrenSimplified = children.map(_.simplify().intToBool()).filter(!DeadFeature.unapply(_));
                    var childrenFlattened: Seq[FeatureExprTree] = SmallList()
                    for (childs <- childrenSimplified)
                        childs match {
                            case Or(innerChildren) => childrenFlattened = childrenFlattened ++ innerChildren
                            case e => childrenFlattened = e +: childrenFlattened
                        }
                    if (isSmall) {
                        childrenFlattened = childrenFlattened.distinct
                        for (childs <- childrenFlattened)
                            if (childrenFlattened.exists(_ == Not(childs)))
                                return BaseFeature();
                    }
                    if (childrenFlattened.exists(BaseFeature.unapply(_)))
                    /*return*/
                        BaseFeature()
                    else if (childrenFlattened.size == 1)
                    /*return*/
                        (childrenFlattened.iterator).next()
                    else if (childrenFlattened.size == 0)
                    /*return*/
                        DeadFeature()
                    else
                    /*return*/
                        Or(childrenFlattened)
                }

                /**
                 * first try push down binary operators over if without simplifying. simplify afterward
                 */
                case BinaryFeatureExprTree(left, right, opStr, op) =>
                    (left simplify, opStr, right simplify) match {
                        case (IntegerLit(a), _, IntegerLit(b)) =>
                            try {
                                IntegerLit(op(a, b))
                            } catch {
                                case t: Throwable =>
                                    System.err.println("Exception with left = " + left.print + ", right = " + right.print)
                                    t.printStackTrace()
                                    throw t
                            }
                        case (a, opStr, b) => BinaryFeatureExprTree(a, b, opStr, op)
                    }

                /**
                 * as binary expr, first propagate down inside if branches before further simplifications
                 */
                case UnaryFeatureExprTree(expr, opStr, op) =>
                    expr simplify match {
                        case IntegerLit(x) => IntegerLit(op(x));
                        case x => UnaryFeatureExprTree(x, opStr, op)
                    }

                case Not(a) =>
                    a.simplify.intToBool() match {
                        case IntegerLit(v) => if (v == 0) BaseFeature() else DeadFeature()
                        case Not(e) => e
                        case e => Not(e)
                    }

                /**
                 * binary expressions are pushed inside ifexpr before simplifcation here
                 */
                case IfExpr(c, a, b) => {
                    val as = a simplify;
                    val bs = b simplify;
                    val cs = c simplify;
                    (cs, as, bs) match {
                        case (BaseFeature(), a, _) => a
                        case (DeadFeature(), _, b) => b
                        case (c, a, b) if (a == b) => a
                        //case (c, a, b) => IfExpr(c, a, b)
                        case (c, a, b) => Or(And(c, a), And(Not(c), b)) simplify
                    }
                }

                case IntegerLit(_) => this

                case DefinedExpr(_) => this
            }
            result.setSimplified
        }
    }

    /**
     * step prior to simplification. Unary and Binary expressions are pushed down
     * over IfExpr in the tree. IfExpr should not be children of Binary or Unary operators
     * on Integers 
     */
    private var isBubbleUpIf: Boolean = false
    private def setBubbleUpIf(): FeatureExprTree = {
        isBubbleUpIf = true;
        return this
    }
    private def bubbleUpIf: FeatureExprTree =
        if (isBubbleUpIf)
            this
        else {
            val result = this match {
                case And(children) => And(children.map(_.bubbleUpIf))
                case Or(children) => Or(children.map(_.bubbleUpIf))

                case BinaryFeatureExprTree(left, right, opStr, op) =>
                    (left bubbleUpIf, right bubbleUpIf) match {
                        case (IfExpr(c, a, b), right) => IfExpr(c, BinaryFeatureExprTree(a, right, opStr, op) bubbleUpIf, BinaryFeatureExprTree(b, right, opStr, op) bubbleUpIf)
                        case (left, IfExpr(c, a, b)) => IfExpr(c, BinaryFeatureExprTree(left, a, opStr, op) bubbleUpIf, BinaryFeatureExprTree(left, b, opStr, op) bubbleUpIf)
                        case (a, b) => BinaryFeatureExprTree(a, b, opStr, op)
                    }

                case UnaryFeatureExprTree(expr, opStr, op) =>
                    expr bubbleUpIf match {
                        case IfExpr(c, a, b) => IfExpr(c, UnaryFeatureExprTree(a, opStr, op) bubbleUpIf, UnaryFeatureExprTree(b, opStr, op) bubbleUpIf)
                        case e => UnaryFeatureExprTree(e, opStr, op)
                    }

                case Not(a) => Not(a bubbleUpIf)

                case IfExpr(c, a, b) => IfExpr(c bubbleUpIf, a bubbleUpIf, b bubbleUpIf)

                case IntegerLit(_) => this

                case DefinedExpr(_) => this
            }
            result.setBubbleUpIf
        }

    //TODO caching
    def isResolved(): Boolean = {
        var foundDefinedMacro = false;
        this.accept(
            _ match {
                case x: DefinedMacro => foundDefinedMacro = true
                case _ =>
            })
        !foundDefinedMacro
    }
    def resolveToExternal(): FeatureExprTree =
    //        TODO caching and do not replace same formula over and over again
        this match {
            case And(children) => And(children.map(_.resolveToExternal()))
            case Or(children) => Or(children.map(_.resolveToExternal()))
            case BinaryFeatureExprTree(left, right, opStr, op) => BinaryFeatureExprTree(left resolveToExternal, right resolveToExternal, opStr, op)
            case UnaryFeatureExprTree(expr, opStr, op) => UnaryFeatureExprTree(expr resolveToExternal, opStr, op)
            case Not(a) => Not(a.resolveToExternal)
            case IfExpr(c, a, b) => IfExpr(c.resolveToExternal, a.resolveToExternal, b resolveToExternal)
            case IntegerLit(_) => this
            case DefinedExternal(_) => this
            case DefinedMacro(name, expansion, _, _) => {
                expansion.simplify;
                expansion.resolveToExternal.expr
            } //TODO stupid to throw away CNF and DNF
        }

    def print(): String
    def debug_print(level: Int): String
    def indent(level: Int): String = {
        var result = "";
        for (i <- 0 until level) result = result + "\t";
        result;
    }
    override def toString(): String = print()
    def intToBool() = this

    def accept(f: FeatureExprTree => Unit): Unit;

    def toCNF(): FeatureExprTree =
        this.simplify match {
            case IfExpr(c, a, b) => new Or(new And(c, a), new And(Not(c), b)).toCNF()
            case Not(And(children)) => Or(children.map(Not(_).toCNF())).toCNF()
            case Not(Or(children)) => And(children.map(Not(_).toCNF())).toCNF()
            case And(children) => And(children.map(_.toCNF)).simplify
            case Or(children) => {
                val cnfchildren = children.map(_.toCNF)
                if (cnfchildren.exists(_.isInstanceOf[And])) {
                    var orClauses: Seq[Or] = SmallList(Or(SmallList())) //list of Or expressions
                    for (child <- cnfchildren) {
                        child match {
                            case And(innerChildren) => {
                                var newClauses: Seq[Or] = SmallList()
                                for (innerChild <- innerChildren)
                                    newClauses = newClauses ++ orClauses.map(_.addChild(innerChild));
                                orClauses = newClauses;
                            }
                            case _ => orClauses = orClauses.map(_.addChild(child));
                        }
                    }
                    And(orClauses.map(a => a)).simplify
                } else Or(cnfchildren)
            }
            case e => e
        }
    private var cacheEquiCnf: WeakReference[FeatureExprTree] = null
    def toCnfEquiSat(): FeatureExprTree = {
        //	  System.out.println(this.print)
        val cache = if (cacheEquiCnf == null) None else cacheEquiCnf.get
        if (cache.isDefined) return cache.get

        val result =
            this.simplify match {
                case IfExpr(c, a, b) => new Or(new And(c, a), new And(Not(c), b)).simplify.toCnfEquiSat()
                case Not(e) =>
                    e match {
                        case And(children) => Or(children.map(Not(_).toCnfEquiSat())).toCnfEquiSat()
                        case Or(children) => And(children.map(Not(_).toCnfEquiSat())).simplify
                        case e: IfExpr => Not(e.toCnfEquiSat).toCnfEquiSat
                        case e => Not(e.toCnfEquiSat)
                    }
                case And(children) => And(children.map(_.toCnfEquiSat)).simplify
                case Or(children) => {
                    val cnfchildren = children.map(_.toCnfEquiSat)
                    if (cnfchildren.exists(_.isInstanceOf[And])) {
                        var orClauses: Seq[FeatureExprTree] = SmallList() //list of Or expressions
                        var freshFeatureNames: Seq[FeatureExprTree] = SmallList()
                        for (child <- cnfchildren) {
                            val freshFeatureName = Not(DefinedExternal(FeatureExpr.calcFreshFeatureName()))
                            child match {
                                case And(innerChildren) => {
                                    for (innerChild <- innerChildren)
                                        orClauses = new Or(freshFeatureName, innerChild) +: orClauses
                                }
                                case e => orClauses = new Or(freshFeatureName, e) +: orClauses
                            }
                            freshFeatureNames = Not(freshFeatureName).simplify +: freshFeatureNames
                        }
                        orClauses = Or(freshFeatureNames) +: orClauses
                        And(orClauses).simplify
                    } else Or(cnfchildren)
                }
                case e => e
            }
        cacheEquiCnf = new WeakReference(result)
        result
    }
    //    def toDNF(): FeatureExprTree = this.simplify match {
    //        case IfExpr(c, a, b) => new Or(new And(c, a), new And(Not(c), b)).toDNF()
    //        case Not(And(children)) => Or(children.map(Not(_).toDNF())).toDNF()
    //        case Not(Or(children)) => And(children.map(Not(_).toDNF())).toDNF()
    //        case Or(children) => Or(children.map(_.toDNF)).simplify
    //        case And(children) => {
    //            val dnfchildren = children.map(_.toDNF)
    //            if (dnfchildren.exists(_.isInstanceOf[Or])) {
    //                var andClauses: Set[And] = Set(And(Set())) //list of Or expressions
    //                for (child <- dnfchildren) {
    //                    child match {
    //                        case Or(innerChildren) => {
    //                            var newClauses: Set[And] = Set()
    //                            for (innerChild <- innerChildren)
    //                                newClauses = newClauses ++ andClauses.map(_.addChild(innerChild));
    //                            andClauses = newClauses;
    //                        }
    //                        case _ => andClauses = andClauses.map(_.addChild(child));
    //                    }
    //                }
    //                And(andClauses.map(a => a)).simplify
    //            } else Or(dnfchildren)
    //        }
    //        case e => e
    //    }

    /**size and small are heuristics used apply aggressive optimizations only to
     *  small formulas (in the hope that they won't grow large eventually)
     */
    def isSmall(): Boolean = getSize() <= 10

    var cachedSize: Option[Int] = None
    final def getSize(): Int = {
        if (!cachedSize.isDefined)
            cachedSize = Some(countSize)
        cachedSize.get
    }
    protected def countSize: Int
}

abstract class AbstractBinaryFeatureExprTree(
                                                    private val left: FeatureExprTree,
                                                    private val right: FeatureExprTree,
                                                    private val opStr: String,
                                                    op: (Long, Long) => Long) extends FeatureExprTree {

    def print() = "(" + left.print + " " + opStr + " " + right.print + ")"
    def debug_print(level: Int): String =
        indent(level) + opStr + "\n" +
                left.debug_print(level + 1) +
                right.debug_print(level + 1);
    def accept(f: FeatureExprTree => Unit): Unit = {
        f(this)
        left.accept(f)
        right.accept(f)
    }
    def countSize() = 1 + left.getSize() + right.getSize()

    /**
     * assumption: opStr and op form a unique pair, so that we can check equality by checking opStr
     */
    override def hashCode = left.hashCode + right.hashCode + opStr.hashCode
    override def equals(that: Any) = that match {
        case e: AbstractBinaryFeatureExprTree => (this.left equals e.left) && (this.right equals e.right) && (this.opStr equals e.opStr)
        case _ => false
    }
}

abstract class AbstractNaryFeatureExprTree(
                                                  private val children: Seq[FeatureExprTree],
                                                  private val opStr: String,
                                                  op: (Boolean, Boolean) => Boolean) extends FeatureExprTree {
    def print() = children.map(_.print).mkString("(", " " + opStr + " ", ")")
    def debug_print(level: Int): String =
        indent(level) + opStr + "\n" +
                children.map(_.debug_print(level + 1)).mkString("")
    def accept(f: FeatureExprTree => Unit): Unit = {
        f(this)
        for (child <- children) child.accept(f)
    }
    def countSize() = children.foldRight(1)((a, b) => a.getSize() + b)

    /**
     * assumption: opStr and op form a unique pair, so that we can check equality by checking opStr
     */
    override def hashCode = children.hashCode + opStr.hashCode
    override def equals(that: Any) = that match {
        case e: AbstractNaryFeatureExprTree => (this.children equals e.children) && (this.opStr equals e.opStr)
        case _ => false
    }
}

abstract class AbstractBinaryBoolFeatureExprTree(
                                                        left: FeatureExprTree,
                                                        right: FeatureExprTree,
                                                        opStr: String,
                                                        op: (Boolean, Boolean) => Boolean) extends AbstractBinaryFeatureExprTree(left, right, opStr, (a, b) => if (op(a != 0, b != 0)) 1 else 0)

abstract class AbstractBinaryCompFeatureExprTree(
                                                        left: FeatureExprTree,
                                                        right: FeatureExprTree,
                                                        opStr: String,
                                                        op: (Long, Long) => Boolean) extends AbstractBinaryFeatureExprTree(left, right, opStr, (a, b) => if (op(a, b)) 1 else 0)

abstract class AbstractUnaryFeatureExprTree(
                                                   private val expr: FeatureExprTree,
                                                   private val opStr: String,
                                                   op: (Long) => Long) extends FeatureExprTree {
    def print() = opStr + "(" + expr.print + ")"
    def debug_print(level: Int) = indent(level) + opStr + "\n" + expr.debug_print(level + 1);
    def accept(f: FeatureExprTree => Unit): Unit = {
        f(this)
        expr.accept(f)
    }
    def countSize() = 1 + expr.getSize()
    /**
     * assumption: opStr and op form a unique pair, so that we can check equality by checking opStr
     */
    override def hashCode = expr.hashCode + opStr.hashCode
    override def equals(that: Any) = that match {
        case e: AbstractUnaryFeatureExprTree => (this.expr equals e.expr) && (this.opStr equals e.opStr)
        case _ => false
    }
}

abstract class AbstractUnaryBoolFeatureExprTree(
                                                       expr: FeatureExprTree,
                                                       opStr: String,
                                                       op: (Boolean) => Boolean) extends AbstractUnaryFeatureExprTree(expr, opStr, (ev) => if (op(ev != 0)) 1 else 0);

abstract class DefinedExpr extends FeatureExprTree {
    /*
     * This method is overriden by children case classes to return the name.
     * It would be nice to have an actual field here, but that doesn't play nicely with case classes;
     * avoiding case classes and open-coding everything would take too much code.
     */
    def feature: String
    def debug_print(level: Int): String = indent(level) + feature + "\n";
    def accept(f: FeatureExprTree => Unit): Unit = f(this)
    def satName = feature
    //used for sat solver only to distinguish extern and macro
    def isExternal: Boolean
}

object DefinedExpr {
    def unapply(f: DefinedExpr): Option[DefinedExpr] = f match {
        case x: DefinedExternal => Some(x)
        case x: DefinedMacro => Some(x)
        case _ => None
    }
    def checkFeatureName(name: String) = assert(name != "1" && name != "0" && name != "")
}

/**external definition of a feature (cannot be decided to Base or Dead inside this file) */
case class DefinedExternal(name: String) extends DefinedExpr {
    def feature = name
    def print(): String = {
        DefinedExpr.checkFeatureName(name)
        "definedEx(" + name + ")";
    }
    def countSize() = 1
    def isExternal = true
    /**
     * assumption: opStr and op form a unique pair, so that we can check equality by checking opStr
     */
    override def hashCode = name.hashCode
    override def equals(that: Any) = that match {
        case e: DefinedExternal => this.name equals e.name
        case _ => false
    }
}

/**
 * definition based on a macro, still to be resolved using the macro table
 * (the macro table may not contain DefinedMacro expressions, but only DefinedExternal)
 * assumption: expandedName is unique and may be used for comparison
 */
case class DefinedMacro(name: String, presenceCondition: FeatureExpr, expandedName: String, presenceConditionCNF: Susp[NF]) extends DefinedExpr {
    def feature = name
    def print(): String = {
        DefinedExpr.checkFeatureName(name)
        "defined(" + name + ")";
    }
    override def satName = expandedName
    /**
     * definedMacros are equal if they have the same Name and the same expansion! (otherwise they refer to
     * the macro at different points in time and should not be considered equal)
     * actually, we only check the expansion name which is unique for each DefinedMacro anyway
     */
    override def equals(that: Any) = that match {
        case e@DefinedMacro(_, _, expandedName, _) => (this eq e) || ((this.expandedName eq expandedName))
        case _ => false
    }
    override def hashCode = expandedName.hashCode
    def countSize() = 1
    def isExternal = false
}

case class IntegerLit(num: Long) extends FeatureExprTree {
    def print(): String = num.toString
    def debug_print(level: Int): String = indent(level) + print() + "\n"
    def accept(f: FeatureExprTree => Unit): Unit = f(this)
    override def intToBool() = if (num == 0) DeadFeature() else BaseFeature()
    def getNum = num
    def countSize() = 1
    override def hashCode: Int = num.toInt
    override def equals(that: Any) = that match {
        case e: IntegerLit => this.num equals e.num
        case _ => false
    }
}

class DeadFeature extends IntegerLit(0) {}

object DeadFeature {
    def unapply(f: FeatureExprTree): Boolean = f match {
        case IntegerLit(0) => true
        case _ => false
    }
    def apply() = new DeadFeature()
}

class BaseFeature extends IntegerLit(1) {}

object BaseFeature {
    def unapply(f: FeatureExprTree): Boolean = f match {
        case IntegerLit(1) => true
        case _ => false
    }
    def apply() = new BaseFeature()
}

case class IfExpr(condition: FeatureExprTree, thenBranch: FeatureExprTree, elseBranch: FeatureExprTree) extends FeatureExprTree {
    def this(cond: FeatureExpr, thenB: FeatureExpr, elseBr: FeatureExpr) = this (cond.expr, thenB.expr, elseBr.expr);
    def print(): String = "__IF__(" + condition.print + "," + thenBranch.print + "," + elseBranch.print + ")";
    def debug_print(level: Int): String =
        indent(level) + "__IF__" + "\n" +
                condition.debug_print(level + 1) +
                indent(level) + "__THEN__" + "\n" +
                thenBranch.debug_print(level + 1) +
                indent(level) + "__ELSE__" + "\n" +
                elseBranch.debug_print(level + 1);
    def accept(f: FeatureExprTree => Unit): Unit = {
        f(this);
        condition.accept(f);
        thenBranch.accept(f);
        elseBranch.accept(f)
    }
    def countSize() = condition.getSize() + thenBranch.getSize() + elseBranch.getSize() + 1
    override def hashCode = condition.hashCode + thenBranch.hashCode + elseBranch.hashCode
    override def equals(that: Any) = that match {
        case e: IfExpr => (this.condition equals e.hashCode) && (this.thenBranch equals e.thenBranch) && (this.elseBranch equals e.elseBranch)
        case _ => false
    }
}

case class Not(expr: FeatureExprTree) extends AbstractUnaryBoolFeatureExprTree(expr, "!", !_);

case class And(children: Seq[FeatureExprTree]) extends AbstractNaryFeatureExprTree(children, "&&", _ && _) {
    def this(left: FeatureExprTree, right: FeatureExprTree) = this (SmallList(left, right))
    def addChild(child: FeatureExprTree) = And(child +: children);
}

object And {
    def apply(a: FeatureExprTree, b: FeatureExprTree) = new And(a, b)
}

case class Or(children: Seq[FeatureExprTree]) extends AbstractNaryFeatureExprTree(children, "||", _ || _) {
    def this(left: FeatureExprTree, right: FeatureExprTree) = this (SmallList(left, right))
    def addChild(child: FeatureExprTree) = Or(child +: children);
}

object Or {
    def apply(a: FeatureExprTree, b: FeatureExprTree) = new Or(a, b)
}

case class UnaryFeatureExprTree(expr: FeatureExprTree, opStr: String, op: (Long) => Long) extends AbstractUnaryFeatureExprTree(expr, opStr, op)

case class BinaryFeatureExprTree(left: FeatureExprTree, right: FeatureExprTree, opStr: String, op: (Long, Long) => Long) extends AbstractBinaryFeatureExprTree(left, right, opStr, op)

