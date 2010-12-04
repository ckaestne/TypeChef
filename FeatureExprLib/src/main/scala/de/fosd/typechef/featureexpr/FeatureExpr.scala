package de.fosd.typechef.featureexpr
import scala.collection.mutable.WeakHashMap

import LazyLib._

object FeatureExpr {
    def resolveDefined(feature: DefinedMacro, macroTable: FeatureProvider): FeatureExpr =
        macroTable.getMacroCondition(feature.name)

    def createComplement(expr: FeatureExpr) = new FeatureExprImpl(UnaryFeatureExprTree(expr.expr, "~", ~_))
    def createNeg(expr: FeatureExpr) = new FeatureExprImpl(UnaryFeatureExprTree(expr.expr, "-", -_))
    def createBitAnd(left: FeatureExpr, right: FeatureExpr) = new FeatureExprImpl(BinaryFeatureExprTree(left.expr, right.expr, "&", _ & _))
    def createBitOr(left: FeatureExpr, right: FeatureExpr) = new FeatureExprImpl(BinaryFeatureExprTree(left.expr, right.expr, "|", _ | _))
    def createDivision(left: FeatureExpr, right: FeatureExpr) = new FeatureExprImpl(BinaryFeatureExprTree(left.expr, right.expr, "/", _ / _))
    def createModulo(left: FeatureExpr, right: FeatureExpr) = new FeatureExprImpl(BinaryFeatureExprTree(left.expr, right.expr, "%", _ % _))
    def createEquals(left: FeatureExpr, right: FeatureExpr) = new FeatureExprImpl(BinaryFeatureExprTree(left.expr, right.expr, "==", (a, b) => if (a == b) 1 else 0))
    def createNotEquals(left: FeatureExpr, right: FeatureExpr) = new FeatureExprImpl(BinaryFeatureExprTree(left.expr, right.expr, "!=", (a, b) => if (a != b) 1 else 0))
    def createLessThan(left: FeatureExpr, right: FeatureExpr) = new FeatureExprImpl(BinaryFeatureExprTree(left.expr, right.expr, "<", (a, b) => if (a < b) 1 else 0))
    def createLessThanEquals(left: FeatureExpr, right: FeatureExpr) = new FeatureExprImpl(BinaryFeatureExprTree(left.expr, right.expr, "<=", (a, b) => if (a <= b) 1 else 0))
    def createGreaterThan(left: FeatureExpr, right: FeatureExpr) = new FeatureExprImpl(BinaryFeatureExprTree(left.expr, right.expr, ">", (a, b) => if (a > b) 1 else 0))
    def createGreaterThanEquals(left: FeatureExpr, right: FeatureExpr) = new FeatureExprImpl(BinaryFeatureExprTree(left.expr, right.expr, ">=", (a, b) => if (a >= b) 1 else 0))
    def createMinus(left: FeatureExpr, right: FeatureExpr) = new FeatureExprImpl(BinaryFeatureExprTree(left.expr, right.expr, "-", _ - _))
    def createMult(left: FeatureExpr, right: FeatureExpr) = new FeatureExprImpl(BinaryFeatureExprTree(left.expr, right.expr, "*", _ * _))
    def createPlus(left: FeatureExpr, right: FeatureExpr) = new FeatureExprImpl(BinaryFeatureExprTree(left.expr, right.expr, "+", _ + _))
    def createPwr(left: FeatureExpr, right: FeatureExpr) = new FeatureExprImpl(BinaryFeatureExprTree(left.expr, right.expr, "^", _ ^ _))
    def createShiftLeft(left: FeatureExpr, right: FeatureExpr) = new FeatureExprImpl(BinaryFeatureExprTree(left.expr, right.expr, "<<", _ << _))
    def createShiftRight(left: FeatureExpr, right: FeatureExpr) = new FeatureExprImpl(BinaryFeatureExprTree(left.expr, right.expr, ">>", _ >> _))

    def createImplies(left: FeatureExpr, right: FeatureExpr) = left.not or right
    def createEquiv(left: FeatureExpr, right: FeatureExpr) = createImplies(left, right) and createImplies(right, left)
    def createDefinedExternal(name: String) = new FeatureExprImpl(new DefinedExternal(name))
    //create a macro definition (which expands to the current entry in the macro table; the current entry is stored in a closure-like way).
    def createDefinedMacro(name: String, macroTable: FeatureProvider): FeatureExpr = {
        val macroCondition = macroTable.getMacroCondition(name)
        if (macroCondition.isSmall) {
            macroCondition
        } else {
            var macroConditionCNF = macroTable.getMacroConditionCNF(name)
            new FeatureExprImpl(new DefinedMacro(
                name,
                macroTable.getMacroCondition(name),
                macroConditionCNF._1,
                macroConditionCNF._2))
        }
    }
    def createInteger(value: Long): FeatureExpr = new FeatureExprImpl(IntegerLit(value))
    def createCharacter(value: Char): FeatureExpr = new FeatureExprImpl(IntegerLit(value))
    def createIf(condition: FeatureExpr, thenBranch: FeatureExpr, elseBranch: FeatureExpr) = new FeatureExprImpl(IfExpr(condition.expr, thenBranch.expr, elseBranch.expr))
    def createIf(condition: FeatureExprTree, thenBranch: FeatureExprTree, elseBranch: FeatureExprTree) = new FeatureExprImpl(IfExpr(condition, thenBranch, elseBranch))

    val base = new FeatureExprImpl(BaseFeature())
    val dead = new FeatureExprImpl(DeadFeature())

    private var freshFeatureNameCounter = 0
    def calcFreshFeatureName(): String = { freshFeatureNameCounter = freshFeatureNameCounter + 1; "__fresh" + freshFeatureNameCounter; }
}

trait FeatureExpr {
    def expr: FeatureExprTree
    //    def cnfExpr: Susp[Option[NF]]
    //    def dnfExpr: Susp[Option[NF]]
    def toString(): String
    def toCNF: NF
    def toEquiCNF: NF
    def simplify(): FeatureExpr
    def isContradiction() = !isSatisfiable()
    def isTautology() = !this.not.isSatisfiable()
    def isSatisfiable(): Boolean
    def isDead() = isContradiction()
    def isBase() = isTautology()
    def accept(f: FeatureExprTree => Unit): Unit
    def print(): String
    def debug_print(): String
    def equals(that: Any): Boolean
    def resolveToExternal(): FeatureExpr
    def isResolved(): Boolean

    def or(that: FeatureExpr): FeatureExpr
    def and(that: FeatureExpr): FeatureExpr
    def implies(that: FeatureExpr): FeatureExpr = this.not or that
    def not(): FeatureExpr

    def isSmall(): Boolean
}

/**
 * feature expressions
 * 
 * may be simplified. caches results on most operations
 */
protected class FeatureExprImpl(var aexpr: FeatureExprTree) extends FeatureExpr {

    def expr: FeatureExprTree = aexpr

    def simplify(): FeatureExpr = { this.aexpr = aexpr.simplify; this }

    val andCache: WeakHashMap[FeatureExpr, FeatureExpr] = new WeakHashMap()
    def and(that: FeatureExpr): FeatureExpr = 
        andCache.getOrElseUpdate(that, new FeatureExprImpl(And(this.expr, that.expr)))
    

    val orCache: WeakHashMap[FeatureExpr, FeatureExpr] = new WeakHashMap()
    def or(that: FeatureExpr): FeatureExpr =
        orCache.getOrElseUpdate(that, new FeatureExprImpl(Or(this.expr, that.expr)))

    var notCache: FeatureExpr = null
    def not(): FeatureExpr = {
        if (notCache == null)
            notCache = new FeatureExprImpl(Not(this.expr))
        notCache
    }

    var cnfCache: NF = null;
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
    var equiCnfCache: NF = null;
    def toEquiCNF: NF =
        if (equiCnfCache != null)
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

    var cacheIsSatisfiable: Option[Boolean] = None
    def isSatisfiable(): Boolean = {
        if (!cacheIsSatisfiable.isDefined)
            cacheIsSatisfiable = Some(new SatSolver().isSatisfiable(toEquiCNF))
        cacheIsSatisfiable.get
    }

    override def toString(): String = { simplify; this.print() }

    def accept(f: FeatureExprTree => Unit): Unit = { simplify(); expr.accept(f) }

    def print(): String = { simplify; aexpr.print() }

    def debug_print(): String = { simplify; expr.debug_print(0); }

    override def equals(that: Any) = that match {
        case e: FeatureExpr => (this eq e) || (this.expr eq e.expr) || FeatureExpr.createEquiv(this, e).isTautology();
        case _ => false
    }

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
            cache_external = new FeatureExprImpl(aexpr.resolveToExternal().simplify)
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
    private def setSimplified(): FeatureExprTree = { isSimplified = true; return this }
    def simplify(): FeatureExprTree = {
        if (isSimplified)
            this
        else {
            val result = this bubbleUpIf match {
                case And(children) => {
                    val childrenSimplified = children.map(_.simplify().intToBool()).filter(!BaseFeature.unapply(_)); //TODO also remove all non-zero integer literals
                    var childrenFlattened: Seq[FeatureExprTree] = List() //computing sets is too expensive
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
                    var childrenFlattened: List[FeatureExprTree] = List()
                    for (childs <- childrenSimplified)
                        childs match {
                            case Or(innerChildren) => childrenFlattened = childrenFlattened ++ innerChildren
                            case e => childrenFlattened = e :: childrenFlattened
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
    private def setBubbleUpIf(): FeatureExprTree = { isBubbleUpIf = true; return this }
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
            case DefinedMacro(name, expansion, _, _) => { expansion.simplify; expansion.resolveToExternal.expr } //TODO stupid to throw away CNF and DNF
        }

    def print(): String
    def debug_print(level: Int): String
    def indent(level: Int): String = { var result = ""; for (i <- 0 until level) result = result + "\t"; result; }
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
                    var orClauses: List[Or] = List(Or(List())) //list of Or expressions
                    for (child <- cnfchildren) {
                        child match {
                            case And(innerChildren) => {
                                var newClauses: List[Or] = List()
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
    def toCnfEquiSat(): FeatureExprTree = {
        //	  System.out.println(this.print)
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
                    var orClauses: List[FeatureExprTree] = List() //list of Or expressions
                    var freshFeatureNames: List[FeatureExprTree] = List()
                    for (child <- cnfchildren) {
                        val freshFeatureName = Not(DefinedExternal(FeatureExpr.calcFreshFeatureName()))
                        child match {
                            case And(innerChildren) => {
                                for (innerChild <- innerChildren)
                                    orClauses = new Or(freshFeatureName, innerChild) :: orClauses
                            }
                            case e => orClauses = new Or(freshFeatureName, e) :: orClauses
                        }
                        freshFeatureNames = Not(freshFeatureName).simplify :: freshFeatureNames
                    }
                    orClauses = Or(freshFeatureNames) :: orClauses
                    And(orClauses).simplify
                } else Or(cnfchildren)
            }
            case e => e
        }
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

    /** size and small are heuristics used apply aggressive optimizations only to
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
    left: FeatureExprTree,
    right: FeatureExprTree,
    opStr: String,
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
}
abstract class AbstractNaryBinaryFeatureExprTree(
    children: Seq[FeatureExprTree],
    opStr: String,
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
    expr: FeatureExprTree,
    opStr: String,
    op: (Long) => Long) extends FeatureExprTree {
    def print() = opStr + "(" + expr.print + ")"
    def debug_print(level: Int) = indent(level) + opStr + "\n" + expr.debug_print(level + 1);
    def accept(f: FeatureExprTree => Unit): Unit = {
        f(this)
        expr.accept(f)
    }
    def countSize() = 1 + expr.getSize()
}
abstract class AbstractUnaryBoolFeatureExprTree(
    expr: FeatureExprTree,
    opStr: String,
    op: (Boolean) => Boolean) extends AbstractUnaryFeatureExprTree(expr, opStr, (ev) => if (op(ev != 0)) 1 else 0);

abstract class DefinedExpr extends FeatureExprTree {
    var feature: String = "";
    def this(name: String) { this(); feature = name.intern; assert(name != "1" && name != "0" && name != "") }
    def debug_print(level: Int): String = indent(level) + feature + "\n";
    def accept(f: FeatureExprTree => Unit): Unit = f(this)
    def satName = feature //used for sat solver only to distinguish extern and macro
    def isExternal: Boolean
}
object DefinedExpr {
    def unapply(f: DefinedExpr): Option[DefinedExpr] = f match {
        case x: DefinedExternal => Some(x)
        case x: DefinedMacro => Some(x)
        case _ => None
    }
}

/** external definion of a feature (cannot be decided to Base or Dead inside this file) */
case class DefinedExternal(name: String) extends DefinedExpr(name) {
    def print(): String = {
        assert(name != "")
        "definedEx(" + name + ")";
    }
    def countSize() = 1
    def isExternal = true
}

/**
 * definition based on a macro, still to be resolved using the macro table
 * (the macro table may not contain DefinedMacro expressions, but only DefinedExternal)
 */
case class DefinedMacro(name: String, presenceCondition: FeatureExpr, expandedName: String, presenceConditionCNF: Susp[NF]) extends DefinedExpr(name) {
    def print(): String = {
        assert(name != "")
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
    override def hashCode = presenceConditionCNF.hashCode
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
    def this(cond: FeatureExpr, thenB: FeatureExpr, elseBr: FeatureExpr) = this(cond.expr, thenB.expr, elseBr.expr);
    def print(): String = "__IF__(" + condition.print + "," + thenBranch.print + "," + elseBranch.print + ")";
    def debug_print(level: Int): String =
        indent(level) + "__IF__" + "\n" +
            condition.debug_print(level + 1) +
            indent(level) + "__THEN__" + "\n" +
            thenBranch.debug_print(level + 1) +
            indent(level) + "__ELSE__" + "\n" +
            elseBranch.debug_print(level + 1);
    def accept(f: FeatureExprTree => Unit): Unit = { f(this); condition.accept(f); thenBranch.accept(f); elseBranch.accept(f) }
    def countSize() = condition.getSize() + thenBranch.getSize() + elseBranch.getSize() + 1
}

case class Not(expr: FeatureExprTree) extends AbstractUnaryBoolFeatureExprTree(expr, "!", !_);
case class And(children: Seq[FeatureExprTree]) extends AbstractNaryBinaryFeatureExprTree(children, "&&", _ && _) {
    def this(left: FeatureExprTree, right: FeatureExprTree) = this(List(left, right))
    def addChild(child: FeatureExprTree) = And(child +: children);
}
object And {
    def apply(a: FeatureExprTree, b: FeatureExprTree) = new And(a, b)
}
case class Or(children: Seq[FeatureExprTree]) extends AbstractNaryBinaryFeatureExprTree(children, "||", _ || _) {
    def this(left: FeatureExprTree, right: FeatureExprTree) = this(List(left, right))
    def addChild(child: FeatureExprTree) = Or(child +: children);
}
object Or {
    def apply(a: FeatureExprTree, b: FeatureExprTree) = new Or(a, b)
}

case class UnaryFeatureExprTree(expr: FeatureExprTree, opStr: String, op: (Long) => Long) extends AbstractUnaryFeatureExprTree(expr, opStr, op)
case class BinaryFeatureExprTree(left: FeatureExprTree, right: FeatureExprTree, opStr: String, op: (Long, Long) => Long) extends AbstractBinaryFeatureExprTree(left, right, opStr, op)

