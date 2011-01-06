package de.fosd.typechef.featureexpr

import LazyLib._
import collection.mutable.{WeakHashMap, Map}

/**
 * external interface to constructing feature expressions (mostly delegated to FExprBuilder)
 */
object FeatureExpr {

    def createComplement(expr: FeatureExprValue): FeatureExprValue = FExprBuilder.applyUnaryOperation(expr, ~_)
    def createNeg(expr: FeatureExprValue) = FExprBuilder.applyUnaryOperation(expr, -_)
    def createBitAnd(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.applyBinaryOperation(left, right, _ & _)
    def createBitOr(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.applyBinaryOperation(left, right, _ | _)
    def createDivision(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.applyBinaryOperation(left, right, _ / _)
    def createModulo(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.applyBinaryOperation(left, right, _ % _)
    def createEquals(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.evalRelation(left, right, _ == _)
    def createNotEquals(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.evalRelation(left, right, _ != _)
    def createLessThan(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.evalRelation(left, right, _ < _)
    def createLessThanEquals(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.evalRelation(left, right, _ <= _)
    def createGreaterThan(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.evalRelation(left, right, _ > _)
    def createGreaterThanEquals(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.evalRelation(left, right, _ >= _)
    def createMinus(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.applyBinaryOperation(left, right, _ - _)
    def createMult(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.applyBinaryOperation(left, right, _ * _)
    def createPlus(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.applyBinaryOperation(left, right, _ + _)
    def createPwr(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.applyBinaryOperation(left, right, _ ^ _)
    def createShiftLeft(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.applyBinaryOperation(left, right, _ << _)
    def createShiftRight(left: FeatureExprValue, right: FeatureExprValue) = FExprBuilder.applyBinaryOperation(left, right, _ >> _)
    def createInteger(value: Long): FeatureExprValue = FExprBuilder.createValue(value)
    def createCharacter(value: Char): FeatureExprValue = FExprBuilder.createValue(value)


    def createDefinedExternal(name: String) = FExprBuilder.definedExternal(name)
    def createDefinedMacro(name: String, macroTable: FeatureProvider) = FExprBuilder.definedMacro(name, macroTable)


    //helper
    def createIf(condition: FeatureExpr, thenBranch: FeatureExpr, elseBranch: FeatureExpr): FeatureExpr = FExprBuilder.createIf(condition, thenBranch, elseBranch)
    def createIf(condition: FeatureExpr, thenBranch: FeatureExprValue, elseBranch: FeatureExprValue): FeatureExprValue = FExprBuilder.createIf(condition, thenBranch, elseBranch)
    def createImplies(left: FeatureExpr, right: FeatureExpr) = left implies right
    def createEquiv(left: FeatureExpr, right: FeatureExpr) = left equiv right

    val base: FeatureExpr = True
    val dead: FeatureExpr = False

}

object FeatureExprHelper {
    def resolveDefined(macro: DefinedMacro, macroTable: FeatureProvider): FeatureExpr =
        macroTable.getMacroCondition(macro.feature)

    private var freshFeatureNameCounter = 0
    def calcFreshFeatureName(): String = {
        freshFeatureNameCounter = freshFeatureNameCounter + 1;
        "__fresh" + freshFeatureNameCounter;
    }
}

/**
 * feature expressions
 *
 * feature expressions are compared on object identity (comparing them for equivalence is
 * an additional but expensive operation). propositions such as and or and not
 * cache results, so that the operation yields identical results on identical parameters
 */
abstract class FeatureExpr {

    def or(that: FeatureExpr): FeatureExpr = FExprBuilder.or(this, that)
    def and(that: FeatureExpr): FeatureExpr = FExprBuilder.and(this, that)
    def not(): FeatureExpr = FExprBuilder.not(this)

    def orNot(that: FeatureExpr) = this or (that.not)
    def andNot(that: FeatureExpr) = this and (that.not)
    def implies(that: FeatureExpr) = this.not.or(that)
    def equiv(that: FeatureExpr) = (this implies that) and (that implies this)
    def mex(that: FeatureExpr): FeatureExpr = (this and that).not

    def isContradiction(): Boolean = isContradiction(NoFeatureModel)
    def isTautology(): Boolean = isTautology(NoFeatureModel)
    def isDead(): Boolean = isContradiction(NoFeatureModel)
    def isBase(): Boolean = isTautology(NoFeatureModel)
    def isSatisfiable(): Boolean = isSatisfiable(NoFeatureModel)
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
    def isSatisfiable(fm: FeatureModel): Boolean = cacheIsSatisfiable.getOrElseUpdate(fm, new SatSolver().isSatisfiable(equiCNF, fm))
    private val cacheIsSatisfiable: WeakHashMap[FeatureModel, Boolean] = WeakHashMap()

    //    def accept(f: FeatureExpr => Unit): Unit


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
    def equivalentTo(that: FeatureExpr): Boolean = (this eq that) || (this equiv that).isTautology();


    protected def indent(level: Int): String = "\t" * level

    final lazy val size: Int = calcSize
    protected def calcSize: Int
    def isSmall(): Boolean = size <= 10

    lazy val resolveToExternal: FeatureExpr = FExprBuilder.resolveToExternal(this)

    /**
     * checks whether there is some unresolved macro (DefinedMacro) somewhere
     * in the expression tree
     */
    lazy val isResolved: Boolean = calcIsResolved
    private def calcIsResolved: Boolean = {
        //exception used to stop at the first found Macro
        //map used for caching (to not look twice at the same subtree)
        class FoundUnresolvedException extends Exception
        try {
            this.mapDefinedExpr({
                case e: DefinedMacro => {throw new FoundUnresolvedException(); e}
                case e => e
            }, Map())
            return true;
        } catch {
            case e: FoundUnresolvedException => return false
        }
    }

    def mapDefinedExpr(f: DefinedExpr => FeatureExpr, cache: Map[FeatureExpr, FeatureExpr]): FeatureExpr
    def print(): String
    def debug_print(indent: Int): String

    private var cache_cnf: FeatureExpr = null
    private var cache_cnfEquiSat: FeatureExpr = null

    def toCNF(): FeatureExpr = {
        if (cache_cnf == null) {cache_cnf = calcCNF; cache_cnfEquiSat = cache_cnf}
        assert(NFBuilder.isCNF(cache_cnf))
        cache_cnf
    }
    def toCnfEquiSat(): FeatureExpr = {
        if (cache_cnfEquiSat == null) cache_cnfEquiSat = calcCNFEquiSat
        assert(NFBuilder.isCNF(cache_cnfEquiSat))
        cache_cnfEquiSat
    }
    protected def calcCNF: FeatureExpr
    protected def calcCNFEquiSat: FeatureExpr

    private[featureexpr] lazy val cnf: NF = NFBuilder.toCNF(toCNF)
    private[featureexpr] lazy val equiCNF: NF = if (cache_cnf != null) cnf else NFBuilder.toCNF(toCnfEquiSat)
}

trait FeatureExprValue {
    def toFeatureExpr: FeatureExpr = {
        val zero = FExprBuilder.createValue(0)
        FExprBuilder.evalRelation(this, zero, _ != _)
    }
}


/**
 * central builder class, responsible for simplification of expressions during creation
 * and for extensive caching
 */
private[featureexpr] object FExprBuilder {

    import collection.mutable.WeakHashMap
    import collection.mutable.HashMap

    private class FExprPair(val a: FeatureExpr, val b: FeatureExpr) {
        //pair in which the order does not matter
        override def hashCode = a.hashCode + b.hashCode;
        override def equals(o: Any) = o match {
            case that: FExprPair => (this.a.equals(that.a) && this.b.equals(that.b)) ||
                    (this.a.equals(that.b) && this.b.equals(that.a))
            case _ => false
        }
    }

    private val andCache: HashMap[FExprPair, FeatureExpr] = new HashMap()
    private val orCache: HashMap[FExprPair, FeatureExpr] = new HashMap()
    private val notCache: WeakHashMap[FeatureExpr, FeatureExpr] = new WeakHashMap()
    private val ifCache: HashMap[(FeatureExpr, FeatureExprValue, FeatureExprValue), FeatureExprValue] = new HashMap()
    private val featureCache: Map[String, DefinedExternal] = Map()
    private var macroCache: Map[String, DefinedMacro] = Map()
    private val valCache: Map[Long, Value] = Map()
    private val resolvedCache: WeakHashMap[FeatureExpr, FeatureExpr] = WeakHashMap()

    def and(a: FeatureExpr, b: FeatureExpr): FeatureExpr =
        (a, b) match {
            case (e1, e2) if (e1 == e2) => e1
            case (_, False) => False
            case (False, _) => False
            case (True, e) => e
            case (e, True) => e
            case (e1, e2) if (e1.not == e2) => False
            case other =>
                andCache.getOrElseUpdate(new FExprPair(a, b), other match {
                    case (a1: And, a2: And) => new And(a1.clauses ++ a2.clauses distinct)
                    case (a: And, e) => if (a.clauses contains e) a else new And(e :: a.clauses)
                    case (e, a: And) => if (a.clauses contains e) a else new And(e :: a.clauses)
                    case (e1, e2) => new And(e1 :: e2 :: Nil)
                })
        }
    def createAnd(clauses: Seq[FeatureExpr]) = clauses.foldLeft[FeatureExpr](True)(and(_, _))

    def or(a: FeatureExpr, b: FeatureExpr): FeatureExpr =
    //simple cases without caching
        (a, b) match {
            case (e1, e2) if (e1 == e2) => e1
            case (_, True) => True
            case (True, _) => True
            case (False, e) => e
            case (e, False) => e
            case (e1, e2) if (e1.not == e2) => True
            case other => orCache.getOrElseUpdate(new FExprPair(a, b), other match {
                case (o1: Or, o2: Or) => new Or(o1.clauses ++ o2.clauses distinct)
                case (o: Or, e) => if (o.clauses contains e) a else new Or(e :: o.clauses)
                case (e, o: Or) => if (o.clauses contains e) a else new Or(e :: o.clauses)
                case (e1, e2) => new Or(e1 :: e2 :: Nil)
            })
        }
    def createOr(clauses: Seq[FeatureExpr]) = clauses.foldLeft[FeatureExpr](False)(or(_, _))

    def not(a: FeatureExpr): FeatureExpr = a match {
        case True => False
        case False => True
        case n: Not => n.expr
        case e => notCache.getOrElseUpdate(e, new Not(e))
    }

    def definedExternal(name: String) = featureCache.getOrElseUpdate(name, new DefinedExternal(name))

    //create a macro definition (which expands to the current entry in the macro table; the current entry is stored in a closure-like way).
    //a form of caching provided by MacroTable, which we need to repeat here to create the same FeatureExpr object
    def definedMacro(name: String, macroTable: FeatureProvider): FeatureExpr = {
        val macroCondition = macroTable.getMacroCondition(name)
        if (macroCondition.isSmall) {
            macroCondition
        } else {
            val macroConditionCNF = macroTable.getMacroConditionCNF(name)

            /**
             * definedMacros are equal if they have the same Name and the same expansion! (otherwise they refer to
             * the macro at different points in time and should not be considered equal)
             * actually, we only check the expansion name which is unique for each DefinedMacro anyway
             */
            macroCache.getOrElseUpdate(macroConditionCNF._1,
                new DefinedMacro(
                    name,
                    macroTable.getMacroCondition(name),
                    macroConditionCNF._1,
                    macroConditionCNF._2))
        }
    }


    def evalRelation(smaller: FeatureExprValue, larger: FeatureExprValue, relation: (Long, Long) => Boolean): FeatureExpr = (smaller, larger) match {
        case (Value(a), Value(b)) => if (relation(a, b)) True else False
        case (If(e, a, b), x: Value) => createIf(e, evalRelation(a, x, relation), evalRelation(b, x, relation))
        case (x: Value, If(e, a, b)) => createIf(e, evalRelation(x, a, relation), evalRelation(x, b, relation))
        case (If(e1, a1, b1), If(e2, a2, b2)) => createIf(e1,
            createIf(e2, evalRelation(a1, a2, relation), evalRelation(a1, b2, relation)),
            createIf(e2, evalRelation(b1, a2, relation), evalRelation(b1, b2, relation)))
        case _ => throw new Exception("evalRelation: unexpected " + (smaller, larger))
    }

    def applyBinaryOperation(left: FeatureExprValue, right: FeatureExprValue, operation: (Long, Long) => Long): FeatureExprValue = (left, right) match {
        case (Value(a), Value(b)) => createValue(operation(a, b))
        case (If(e, a, b), x: Value) => createIf(e, applyBinaryOperation(a, x, operation), applyBinaryOperation(b, x, operation))
        case (x: Value, If(e, a, b)) => createIf(e, applyBinaryOperation(x, a, operation), applyBinaryOperation(x, b, operation))
        case (If(e1, a1, b1), If(e2, a2, b2)) => createIf(e1,
            createIf(e2, applyBinaryOperation(a1, a2, operation), applyBinaryOperation(a1, b2, operation)),
            createIf(e2, applyBinaryOperation(b1, a2, operation), applyBinaryOperation(b1, b2, operation)))
        case _ => throw new Exception("applyBinaryOperation: unexpected " + (left, right))
    }
    def applyUnaryOperation(expr: FeatureExprValue, operation: Long => Long): FeatureExprValue = expr match {
        case Value(a) => createValue(operation(a))
        case If(e, a, b) => createIf(e, applyUnaryOperation(a, operation), applyUnaryOperation(b, operation))
        case _ => throw new Exception("applyUnaryOperation: unexpected " + expr)
    }

    def createIf(expr: FeatureExpr, thenBr: FeatureExprValue, elseBr: FeatureExprValue): FeatureExprValue = expr match {
        case True => thenBr
        case False => elseBr
        case _ => {
            if (thenBr == elseBr) thenBr
            else ifCache.getOrElseUpdate((expr, thenBr, elseBr), new If(expr, thenBr, elseBr))
        }
    }
    def createIf(expr: FeatureExpr, thenBr: FeatureExpr, elseBr: FeatureExpr): FeatureExpr = (expr and thenBr) or (expr.not and elseBr)

    def createValue(v: Long): FeatureExprValue = valCache.getOrElseUpdate(v, new Value(v))

    def resolveToExternal(expr: FeatureExpr): FeatureExpr = expr.mapDefinedExpr({
        case e: DefinedMacro => e.presenceCondition.resolveToExternal
        case e => e
    }, resolvedCache)
}


//
//sealed abstract class FeatureExprTree {
//    //optimization to not simplify the same expression over and over again
//    private var isSimplified: Boolean = false
//    private def setSimplified(): FeatureExprTree = {
//        isSimplified = true;
//        return this
//    }
//    def simplify(): FeatureExprTree = {
//        if (isSimplified)
//            this
//        else {
//            val result = this bubbleUpIf match {
//                case And(children) => {
//                    val childrenSimplified = children.map(_.simplify().intToBool()).filter(!BaseFeature.unapply(_)); //TODO also remove all non-zero integer literals
//                    var childrenFlattened: Seq[FeatureExprTree] = SmallList() //computing sets is too expensive
//                    for (childs <- childrenSimplified)
//                        childs match {
//                            case And(innerChildren) => childrenFlattened = childrenFlattened ++ innerChildren
//                            case e => childrenFlattened = e +: childrenFlattened
//                        }
//                    //only apply these operations on small expressions, because they are rather expensive
//                    if (isSmall) {
//                        childrenFlattened = childrenFlattened.distinct
//                        for (childs <- childrenFlattened)
//                            if (childrenFlattened.exists(_ == Not(childs)))
//                                return DeadFeature();
//                    }
//                    if (childrenFlattened.exists(DeadFeature.unapply(_)))
//                    /*return*/
//                        DeadFeature()
//                    else if (childrenFlattened.size == 1)
//                    /*return*/
//                        (childrenFlattened.iterator).next()
//                    else if (childrenFlattened.size == 0)
//                    /*return*/
//                        BaseFeature()
//                    else
//                    /*return*/
//                        And(childrenFlattened)
//                }
//
//                case Or(c) => {
//                    var children = c
//                    val childrenSimplified = children.map(_.simplify().intToBool()).filter(!DeadFeature.unapply(_));
//                    var childrenFlattened: Seq[FeatureExprTree] = SmallList()
//                    for (childs <- childrenSimplified)
//                        childs match {
//                            case Or(innerChildren) => childrenFlattened = childrenFlattened ++ innerChildren
//                            case e => childrenFlattened = e +: childrenFlattened
//                        }
//                    if (isSmall) {
//                        childrenFlattened = childrenFlattened.distinct
//                        for (childs <- childrenFlattened)
//                            if (childrenFlattened.exists(_ == Not(childs)))
//                                return BaseFeature();
//                    }
//                    if (childrenFlattened.exists(BaseFeature.unapply(_)))
//                    /*return*/
//                        BaseFeature()
//                    else if (childrenFlattened.size == 1)
//                    /*return*/
//                        (childrenFlattened.iterator).next()
//                    else if (childrenFlattened.size == 0)
//                    /*return*/
//                        DeadFeature()
//                    else
//                    /*return*/
//                        Or(childrenFlattened)
//                }
//
//                /**
//                 * first try push down binary operators over if without simplifying. simplify afterward
//                 */
//                case BinaryFeatureExprTree(left, right, opStr, op) =>
//                    (left simplify, opStr, right simplify) match {
//                        case (IntegerLit(a), _, IntegerLit(b)) =>
//                            try {
//                                IntegerLit(op(a, b))
//                            } catch {
//                                case t: Throwable =>
//                                    System.err.println("Exception with left = " + left.print + ", right = " + right.print)
//                                    t.printStackTrace()
//                                    throw t
//                            }
//                        case (a, opStr, b) => BinaryFeatureExprTree(a, b, opStr, op)
//                    }
//
//                /**
//                 * as binary expr, first propagate down inside if branches before further simplifications
//                 */
//                case UnaryFeatureExprTree(expr, opStr, op) =>
//                    expr simplify match {
//                        case IntegerLit(x) => IntegerLit(op(x));
//                        case x => UnaryFeatureExprTree(x, opStr, op)
//                    }
//
//                case Not(a) =>
//                    a.simplify.intToBool() match {
//                        case IntegerLit(v) => if (v == 0) BaseFeature() else DeadFeature()
//                        case Not(e) => e
//                        case e => Not(e)
//                    }
//
//                /**
//                 * binary expressions are pushed inside ifexpr before simplifcation here
//                 */
//                case IfExpr(c, a, b) => {
//                    val as = a simplify;
//                    val bs = b simplify;
//                    val cs = c simplify;
//                    (cs, as, bs) match {
//                        case (BaseFeature(), a, _) => a
//                        case (DeadFeature(), _, b) => b
//                        case (c, a, b) if (a == b) => a
//                        //case (c, a, b) => IfExpr(c, a, b)
//                        case (c, a, b) => Or(And(c, a), And(Not(c), b)) simplify
//                    }
//                }
//
//                case IntegerLit(_) => this
//
//                case DefinedExpr(_) => this
//            }
//            result.setSimplified
//        }
//    }
//
//    /**
//     * step prior to simplification. Unary and Binary expressions are pushed down
//     * over IfExpr in the tree. IfExpr should not be children of Binary or Unary operators
//     * on Integers
//     */
//    private var isBubbleUpIf: Boolean = false
//    private def setBubbleUpIf(): FeatureExprTree = {
//        isBubbleUpIf = true;
//        return this
//    }
//    private def bubbleUpIf: FeatureExprTree =
//        if (isBubbleUpIf)
//            this
//        else {
//            val result = this match {
//                case And(children) => And(children.map(_.bubbleUpIf))
//                case Or(children) => Or(children.map(_.bubbleUpIf))
//
//                case BinaryFeatureExprTree(left, right, opStr, op) =>
//                    (left bubbleUpIf, right bubbleUpIf) match {
//                        case (IfExpr(c, a, b), right) => IfExpr(c, BinaryFeatureExprTree(a, right, opStr, op) bubbleUpIf, BinaryFeatureExprTree(b, right, opStr, op) bubbleUpIf)
//                        case (left, IfExpr(c, a, b)) => IfExpr(c, BinaryFeatureExprTree(left, a, opStr, op) bubbleUpIf, BinaryFeatureExprTree(left, b, opStr, op) bubbleUpIf)
//                        case (a, b) => BinaryFeatureExprTree(a, b, opStr, op)
//                    }
//
//                case UnaryFeatureExprTree(expr, opStr, op) =>
//                    expr bubbleUpIf match {
//                        case IfExpr(c, a, b) => IfExpr(c, UnaryFeatureExprTree(a, opStr, op) bubbleUpIf, UnaryFeatureExprTree(b, opStr, op) bubbleUpIf)
//                        case e => UnaryFeatureExprTree(e, opStr, op)
//                    }
//
//                case Not(a) => Not(a bubbleUpIf)
//
//                case IfExpr(c, a, b) => IfExpr(c bubbleUpIf, a bubbleUpIf, b bubbleUpIf)
//
//                case IntegerLit(_) => this
//
//                case DefinedExpr(_) => this
//            }
//            result.setBubbleUpIf
//        }
//
//    //TODO caching
//    def isResolved(): Boolean = {
//        var foundDefinedMacro = false;
//        this.accept(
//            _ match {
//                case x: DefinedMacro => foundDefinedMacro = true
//                case _ =>
//            })
//        !foundDefinedMacro
//    }
//    def resolveToExternal(): FeatureExprTree =
//    //        TODO caching and do not replace same formula over and over again
//        this match {
//            case And(children) => And(children.map(_.resolveToExternal()))
//            case Or(children) => Or(children.map(_.resolveToExternal()))
//            case BinaryFeatureExprTree(left, right, opStr, op) => BinaryFeatureExprTree(left resolveToExternal, right resolveToExternal, opStr, op)
//            case UnaryFeatureExprTree(expr, opStr, op) => UnaryFeatureExprTree(expr resolveToExternal, opStr, op)
//            case Not(a) => Not(a.resolveToExternal)
//            case IfExpr(c, a, b) => IfExpr(c.resolveToExternal, a.resolveToExternal, b resolveToExternal)
//            case IntegerLit(_) => this
//            case DefinedExternal(_) => this
//            case DefinedMacro(name, expansion, _, _) => {
//                expansion.simplify;
//                expansion.resolveToExternal.expr
//            } //TODO stupid to throw away CNF and DNF
//        }
//
//    def print(): String
//    def debug_print(level: Int): String
//    def indent(level: Int): String = {
//        var result = "";
//        for (i <- 0 until level) result = result + "\t";
//        result;
//    }
//    override def toString(): String = print()
//    def intToBool() = this
//
//    def accept(f: FeatureExprTree => Unit): Unit;
//
//    def toCNF(): FeatureExprTree =
//        this.simplify match {
//            case IfExpr(c, a, b) => new Or(new And(c, a), new And(Not(c), b)).toCNF()
//            case Not(And(children)) => Or(children.map(Not(_).toCNF())).toCNF()
//            case Not(Or(children)) => And(children.map(Not(_).toCNF())).toCNF()
//            case And(children) => And(children.map(_.toCNF)).simplify
//            case Or(children) => {
//                val cnfchildren = children.map(_.toCNF)
//                if (cnfchildren.exists(_.isInstanceOf[And])) {
//                    var orClauses: Seq[Or] = SmallList(Or(SmallList())) //list of Or expressions
//                    for (child <- cnfchildren) {
//                        child match {
//                            case And(innerChildren) => {
//                                var newClauses: Seq[Or] = SmallList()
//                                for (innerChild <- innerChildren)
//                                    newClauses = newClauses ++ orClauses.map(_.addChild(innerChild));
//                                orClauses = newClauses;
//                            }
//                            case _ => orClauses = orClauses.map(_.addChild(child));
//                        }
//                    }
//                    And(orClauses.map(a => a)).simplify
//                } else Or(cnfchildren)
//            }
//            case e => e
//        }
//    private var cacheEquiCnf: WeakReference[FeatureExprTree] = null
//    def toCnfEquiSat(): FeatureExprTree = {
//        //	  System.out.println(this.print)
//        val cache = if (cacheEquiCnf == null) None else cacheEquiCnf.get
//        if (cache.isDefined) return cache.get
//
//        val result =
//            this.simplify match {
//                case IfExpr(c, a, b) => new Or(new And(c, a), new And(Not(c), b)).simplify.toCnfEquiSat()
//                case Not(e) =>
//                    e match {
//                        case And(children) => Or(children.map(Not(_).toCnfEquiSat())).toCnfEquiSat()
//                        case Or(children) => And(children.map(Not(_).toCnfEquiSat())).simplify
//                        case e: IfExpr => Not(e.toCnfEquiSat).toCnfEquiSat
//                        case e => Not(e.toCnfEquiSat)
//                    }
//                case And(children) => And(children.map(_.toCnfEquiSat)).simplify
//                case Or(children) => {
//                    val cnfchildren = children.map(_.toCnfEquiSat)
//                    if (cnfchildren.exists(_.isInstanceOf[And])) {
//                        var orClauses: Seq[FeatureExprTree] = SmallList() //list of Or expressions
//                        var freshFeatureNames: Seq[FeatureExprTree] = SmallList()
//                        for (child <- cnfchildren) {
//                            val freshFeatureName = Not(DefinedExternal(FeatureExpr.calcFreshFeatureName()))
//                            child match {
//                                case And(innerChildren) => {
//                                    for (innerChild <- innerChildren)
//                                        orClauses = new Or(freshFeatureName, innerChild) +: orClauses
//                                }
//                                case e => orClauses = new Or(freshFeatureName, e) +: orClauses
//                            }
//                            freshFeatureNames = Not(freshFeatureName).simplify +: freshFeatureNames
//                        }
//                        orClauses = Or(freshFeatureNames) +: orClauses
//                        And(orClauses).simplify
//                    } else Or(cnfchildren)
//                }
//                case e => e
//            }
//        cacheEquiCnf = new WeakReference(result)
//        result
//    }
//    //    def toDNF(): FeatureExprTree = this.simplify match {
//    //        case IfExpr(c, a, b) => new Or(new And(c, a), new And(Not(c), b)).toDNF()
//    //        case Not(And(children)) => Or(children.map(Not(_).toDNF())).toDNF()
//    //        case Not(Or(children)) => And(children.map(Not(_).toDNF())).toDNF()
//    //        case Or(children) => Or(children.map(_.toDNF)).simplify
//    //        case And(children) => {
//    //            val dnfchildren = children.map(_.toDNF)
//    //            if (dnfchildren.exists(_.isInstanceOf[Or])) {
//    //                var andClauses: Set[And] = Set(And(Set())) //list of Or expressions
//    //                for (child <- dnfchildren) {
//    //                    child match {
//    //                        case Or(innerChildren) => {
//    //                            var newClauses: Set[And] = Set()
//    //                            for (innerChild <- innerChildren)
//    //                                newClauses = newClauses ++ andClauses.map(_.addChild(innerChild));
//    //                            andClauses = newClauses;
//    //                        }
//    //                        case _ => andClauses = andClauses.map(_.addChild(child));
//    //                    }
//    //                }
//    //                And(andClauses.map(a => a)).simplify
//    //            } else Or(dnfchildren)
//    //        }
//    //        case e => e
//    //    }
//
//    /**size and small are heuristics used apply aggressive optimizations only to
//     *  small formulas (in the hope that they won't grow large eventually)
//     */
//    def isSmall(): Boolean = getSize() <= 10
//
//    var cachedSize: Option[Int] = None
//    final def getSize(): Int = {
//        if (!cachedSize.isDefined)
//            cachedSize = Some(countSize)
//        cachedSize.get
//    }
//    protected def countSize: Int
//}

//abstract class AbstractBinaryFeatureExprTree(
//                                                    private val left: FeatureExprTree,
//                                                    private val right: FeatureExprTree,
//                                                    private val opStr: String,
//                                                    op: (Long, Long) => Long) extends FeatureExprTree {
//
//    def print() = "(" + left.print + " " + opStr + " " + right.print + ")"
//    def debug_print(level: Int): String =
//        indent(level) + opStr + "\n" +
//                left.debug_print(level + 1) +
//                right.debug_print(level + 1);
//    def accept(f: FeatureExprTree => Unit): Unit = {
//        f(this)
//        left.accept(f)
//        right.accept(f)
//    }
//    def countSize() = 1 + left.getSize() + right.getSize()
//
//    /**
//     * assumption: opStr and op form a unique pair, so that we can check equality by checking opStr
//     */
//    override def hashCode = left.hashCode + right.hashCode + opStr.hashCode
//    override def equals(that: Any) = that match {
//        case e: AbstractBinaryFeatureExprTree => (this.left equals e.left) && (this.right equals e.right) && (this.opStr equals e.opStr)
//        case _ => false
//    }
//}
//
//abstract class AbstractNaryFeatureExprTree(
//                                                  private val children: Seq[FeatureExprTree],
//                                                  private val opStr: String,
//                                                  op: (Boolean, Boolean) => Boolean) extends FeatureExprTree {
//    def print() = children.map(_.print).mkString("(", " " + opStr + " ", ")")
//    def debug_print(level: Int): String =
//        indent(level) + opStr + "\n" +
//                children.map(_.debug_print(level + 1)).mkString("")
//    def accept(f: FeatureExprTree => Unit): Unit = {
//        f(this)
//        for (child <- children) child.accept(f)
//    }
//    def countSize() = children.foldRight(1)((a, b) => a.getSize() + b)
//
//    /**
//     * assumption: opStr and op form a unique pair, so that we can check equality by checking opStr
//     */
//    override def hashCode = children.hashCode + opStr.hashCode
//    override def equals(that: Any) = that match {
//        case e: AbstractNaryFeatureExprTree => (this.children equals e.children) && (this.opStr equals e.opStr)
//        case _ => false
//    }
//}
//
//abstract class AbstractBinaryBoolFeatureExprTree(
//                                                        left: FeatureExprTree,
//                                                        right: FeatureExprTree,
//                                                        opStr: String,
//                                                        op: (Boolean, Boolean) => Boolean) extends AbstractBinaryFeatureExprTree(left, right, opStr, (a, b) => if (op(a != 0, b != 0)) 1 else 0)
//
//abstract class AbstractBinaryCompFeatureExprTree(
//                                                        left: FeatureExprTree,
//                                                        right: FeatureExprTree,
//                                                        opStr: String,
//                                                        op: (Long, Long) => Boolean) extends AbstractBinaryFeatureExprTree(left, right, opStr, (a, b) => if (op(a, b)) 1 else 0)
//
//abstract class AbstractUnaryFeatureExprTree(
//                                                   private val expr: FeatureExprTree,
//                                                   private val opStr: String,
//                                                   op: (Long) => Long) extends FeatureExprTree {
//    def print() = opStr + "(" + expr.print + ")"
//    def debug_print(level: Int) = indent(level) + opStr + "\n" + expr.debug_print(level + 1);
//    def accept(f: FeatureExprTree => Unit): Unit = {
//        f(this)
//        expr.accept(f)
//    }
//    def countSize() = 1 + expr.getSize()
//    /**
//     * assumption: opStr and op form a unique pair, so that we can check equality by checking opStr
//     */
//    override def hashCode = expr.hashCode + opStr.hashCode
//    override def equals(that: Any) = that match {
//        case e: AbstractUnaryFeatureExprTree => (this.expr equals e.expr) && (this.opStr equals e.opStr)
//        case _ => false
//    }
//}
//
//abstract class AbstractUnaryBoolFeatureExprTree(
//                                                       expr: FeatureExprTree,
//                                                       opStr: String,
//                                                       op: (Boolean) => Boolean) extends AbstractUnaryFeatureExprTree(expr, opStr, (ev) => if (op(ev != 0)) 1 else 0);


/**
 * propositional formulas
 */
private[featureexpr] abstract class TrueFalseFeatureExpr(isTrue: Boolean) extends FeatureExpr {
    override def calcSize = 0
    override def print = if (isTrue) "1" else "0"
    override def debug_print(ind: Int) = indent(ind) + print + "\n"
    override def mapDefinedExpr(f: DefinedExpr => FeatureExpr, cache: Map[FeatureExpr, FeatureExpr]): FeatureExpr = this
    override def calcCNF = this
    override def calcCNFEquiSat = this
    override def isSatisfiable(fm: FeatureModel) = isTrue
}

object True extends TrueFalseFeatureExpr(true) {
    override def toString = "True"
}

object False extends TrueFalseFeatureExpr(false) {
    override def toString = "False"
}


class And(val clauses: List[FeatureExpr]) extends FeatureExpr {
    override def toString = clauses.mkString("(", "&", ")")
    override def print = clauses.map(_.print).mkString("(", " && ", ")")
    override def debug_print(ind: Int) = indent(ind) + "&\n" + clauses.map(_.debug_print(ind + 1)).mkString

    override def calcSize = clauses.foldLeft(0)(_ + _.size)
    override def mapDefinedExpr(f: DefinedExpr => FeatureExpr, cache: Map[FeatureExpr, FeatureExpr]): FeatureExpr = cache.getOrElseUpdate(this, {
        var anyChange = false
        val newClauses = clauses.map(x => {val y = x.mapDefinedExpr(f, cache); anyChange |= x == y; y})
        if (anyChange) FExprBuilder.createAnd(newClauses) else this
    })

    protected def calcCNF: FeatureExpr = FExprBuilder.createAnd(clauses.map(_.toCNF))
    protected def calcCNFEquiSat: FeatureExpr = FExprBuilder.createAnd(clauses.map(_.toCnfEquiSat))
}

object And {
    def unapply(x: And) = Some(x.clauses)
}

class Or(val clauses: List[FeatureExpr]) extends FeatureExpr {
    override def toString = clauses.mkString("(", "|", ")")
    override def print = clauses.map(_.print).mkString("(", " || ", ")")
    override def debug_print(ind: Int) = indent(ind) + "|\n" + clauses.map(_.debug_print(ind + 1)).mkString

    override def calcSize = clauses.foldLeft(0)(_ + _.size)
    override def mapDefinedExpr(f: DefinedExpr => FeatureExpr, cache: Map[FeatureExpr, FeatureExpr]): FeatureExpr = cache.getOrElseUpdate(this, {
        var anyChange = false
        val newClauses = clauses.map(x => {val y = x.mapDefinedExpr(f, cache); anyChange |= x == y; y})
        if (anyChange) FExprBuilder.createOr(newClauses) else this
    })

    protected def calcCNF: FeatureExpr = {
        val cnfchildren = clauses.map(_.toCNF)
        if (cnfchildren.exists(_.isInstanceOf[And])) {
            var orClauses: Seq[FeatureExpr] = SmallList(False) //list of Or expressions
            for (child <- cnfchildren) {
                child match {
                    case And(innerChildren) => {
                        var newClauses: Seq[FeatureExpr] = SmallList()
                        for (innerChild <- innerChildren) {
                            val aClauses =
                                newClauses ++= orClauses.map(_ or innerChild)
                        }
                        orClauses = newClauses
                    }
                    case _ => orClauses = orClauses.map(_ or child)
                }
            }
            assert(orClauses.forall(c => NFBuilder.isClause(c) || c == True || c == False))
            FExprBuilder.createAnd(orClauses)
        } else FExprBuilder.createOr(cnfchildren)
    }


    protected def calcCNFEquiSat: FeatureExpr = {
        val cnfchildren = clauses.map(_.toCnfEquiSat)
        if (cnfchildren.exists(_.isInstanceOf[And])) {
            var orClauses: Seq[FeatureExpr] = SmallList() //list of Or expressions
            var freshFeatureNames: Seq[FeatureExpr] = SmallList()
            for (child <- cnfchildren) {
                val freshFeatureName = FExprBuilder.definedExternal(FeatureExprHelper.calcFreshFeatureName()).not
                child match {
                    case And(innerChildren) => {
                        for (innerChild <- innerChildren)
                            orClauses = (freshFeatureName or innerChild) +: orClauses
                    }
                    case e => orClauses = (freshFeatureName or e) +: orClauses
                }
                freshFeatureNames = freshFeatureName.not +: freshFeatureNames
            }
            orClauses = FExprBuilder.createOr(freshFeatureNames) +: orClauses
            FExprBuilder.createAnd(orClauses)
        } else FExprBuilder.createOr(cnfchildren)
    }

}

object Or {
    def unapply(x: Or) = Some(x.clauses)
}

class Not(val expr: FeatureExpr) extends FeatureExpr {
    override def toString = "!" + expr.toString
    override def print = "!" + expr.print
    override def debug_print(ind: Int) = indent(ind) + "!\n" + expr.debug_print(ind + 1)

    override def calcSize = expr.size
    override def mapDefinedExpr(f: DefinedExpr => FeatureExpr, cache: Map[FeatureExpr, FeatureExpr]): FeatureExpr = cache.getOrElseUpdate(this, {
        val newExpr = expr.mapDefinedExpr(f, cache)
        if (newExpr != expr) FExprBuilder.not(newExpr) else this
    })
    protected def calcCNF: FeatureExpr = expr match {
        case And(children) => FExprBuilder.createOr(children.map(_.not.toCNF)).toCNF
        case Or(children) => FExprBuilder.createAnd(children.map(_.not.toCNF))
        case e => this
    }

    protected def calcCNFEquiSat: FeatureExpr = expr match {
        case And(children) => FExprBuilder.createOr(children.map(_.not.toCnfEquiSat())).toCnfEquiSat()
        case Or(children) => FExprBuilder.createAnd(children.map(_.not.toCnfEquiSat()))
        case e => this
    }

}

object Not {
    def unapply(x: Not) = Some(x.expr)
}


abstract class DefinedExpr extends FeatureExpr {
    /*
     * This method is overriden by children case classes to return the name.
     * It would be nice to have an actual field here, but that doesn't play nicely with case classes;
     * avoiding case classes and open-coding everything would take too much code.
     */
    def feature: String
    def debug_print(level: Int): String = indent(level) + feature + "\n";
    def accept(f: FeatureExpr => Unit): Unit = f(this)
    def satName = feature
    //used for sat solver only to distinguish extern and macro
    def isExternal: Boolean
    override def calcSize = 1
    override def mapDefinedExpr(f: DefinedExpr => FeatureExpr, cache: Map[FeatureExpr, FeatureExpr]): FeatureExpr = cache.getOrElseUpdate(this, f(this))
    override def calcCNF = this
    override def calcCNFEquiSat = this
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
class DefinedExternal(name: String) extends DefinedExpr {
    DefinedExpr.checkFeatureName(name)

    def feature = name
    override def print(): String = "definedEx(" + name + ")";
    override def toString = name
    def countSize() = 1
    def isExternal = true
}

/**
 * definition based on a macro, still to be resolved using the macro table
 * (the macro table may not contain DefinedMacro expressions, but only DefinedExternal)
 * assumption: expandedName is unique and may be used for comparison
 */
class DefinedMacro(val name: String, val presenceCondition: FeatureExpr, val expandedName: String, val presenceConditionCNF: Susp[NF]) extends DefinedExpr {
    DefinedExpr.checkFeatureName(name)

    def feature = name
    override def print(): String = "defined(" + name + ")"
    override def toString = "macro(" + name + ")"
    override def satName = expandedName
    def countSize() = 1
    def isExternal = false
}

object DefinedMacro {
    def unapply(x: DefinedMacro) = Some((x.name, x.presenceCondition, x.expandedName, x.presenceConditionCNF))
}


/**
 * values (integers, chars and operations and relations on them)
 */

class If(val expr: FeatureExpr, val thenBr: FeatureExprValue, val elseBr: FeatureExprValue) extends FeatureExprValue {
    override def toString = "(" + expr + "?" + thenBr + ":" + elseBr + ")"
}

object If {
    def unapply(x: If) = Some(Tuple3(x.expr, x.thenBr, x.elseBr))
}

class Value(val value: Long) extends FeatureExprValue {
    override def toString = value.toString
}

object Value {
    def unapply(x: Value) = Some(x.value)
}


//case class IfExpr(condition: FeatureExprTree, thenBranch: FeatureExprTree, elseBranch: FeatureExprTree) extends FeatureExprTree {
//    def this(cond: FeatureExpr, thenB: FeatureExpr, elseBr: FeatureExpr) = this (cond.expr, thenB.expr, elseBr.expr);
//    def print(): String = "__IF__(" + condition.print + "," + thenBranch.print + "," + elseBranch.print + ")";
//    def debug_print(level: Int): String =
//        indent(level) + "__IF__" + "\n" +
//                condition.debug_print(level + 1) +
//                indent(level) + "__THEN__" + "\n" +
//                thenBranch.debug_print(level + 1) +
//                indent(level) + "__ELSE__" + "\n" +
//                elseBranch.debug_print(level + 1);
//    def accept(f: FeatureExprTree => Unit): Unit = {
//        f(this);
//        condition.accept(f);
//        thenBranch.accept(f);
//        elseBranch.accept(f)
//    }
//    def countSize() = condition.getSize() + thenBranch.getSize() + elseBranch.getSize() + 1
//    override def hashCode = condition.hashCode + thenBranch.hashCode + elseBranch.hashCode
//    override def equals(that: Any) = that match {
//        case e: IfExpr => (this.condition equals e.hashCode) && (this.thenBranch equals e.thenBranch) && (this.elseBranch equals e.elseBranch)
//        case _ => false
//    }
//}

