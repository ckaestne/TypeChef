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
                    case (a1: And, a2: And) => a2.clauses.foldLeft[FeatureExpr](a1)(_ and _)
                    case (a: And, e) => if (a.clauses contains e) a else if (a.clauses contains (e.not)) False else new And(a.clauses + e)
                    case (e, a: And) => if (a.clauses contains e) a else if (a.clauses contains (e.not)) False else new And(a.clauses + e)
                    case (e1, e2) => new And(Set(e1, e2))
                })
        }
    def createAnd(clauses: Traversable[FeatureExpr]) = clauses.foldLeft[FeatureExpr](True)(and(_, _))

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
                case (o1: Or, o2: Or) => o2.clauses.foldLeft[FeatureExpr](o1)(_ or _)
                case (o: Or, e) => if (o.clauses contains e) o else if (o.clauses contains (e.not)) True else new Or(o.clauses + e)
                case (e, o: Or) => if (o.clauses contains e) o else if (o.clauses contains (e.not)) True else new Or(o.clauses + e)
                case (e1, e2) => new Or(Set(e1, e2))
            })
        }
    def createOr(clauses: Traversable[FeatureExpr]) = clauses.foldLeft[FeatureExpr](False)(or(_, _))

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


class And(val clauses: Set[FeatureExpr]) extends FeatureExpr {
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

class Or(val clauses: Set[FeatureExpr]) extends FeatureExpr {
    override def toString = clauses.mkString("(", "|", ")")
    override def print = clauses.map(_.print).mkString("(", " || ", ")")
    override def debug_print(ind: Int) = indent(ind) + "|\n" + clauses.map(_.debug_print(ind + 1)).mkString

    override def calcSize = clauses.foldLeft(0)(_ + _.size)
    override def mapDefinedExpr(f: DefinedExpr => FeatureExpr, cache: Map[FeatureExpr, FeatureExpr]): FeatureExpr = cache.getOrElseUpdate(this, {
        var anyChange = false
        val newClauses = clauses.map(x => {val y = x.mapDefinedExpr(f, cache); anyChange |= x == y; y})
        if (anyChange) FExprBuilder.createOr(newClauses) else this
    })

    protected def calcCNF: FeatureExpr =
        combineCNF(clauses.map(_.toCNF))
    protected def calcCNFEquiSat: FeatureExpr = {
        val cnfchildren = clauses.map(_.toCnfEquiSat)
        //heuristic: up to a medium size do not introduce new variables but use normal toCNF mechansim
        //rational: we might actually simplify the formula by transforming it into CNF and in such cases it's not very expensive
        def size(child: FeatureExpr) = child match {case And(inner) => inner.size; case _ => 1}
        val predicedCNFClauses = cnfchildren.foldRight(1)(size(_) * _)
        if (predicedCNFClauses <= 16)
            combineCNF(cnfchildren)
        else combineEquiCNF(cnfchildren)
    }


    /**
     * multiplies all clauses
     *
     * for n CNF expressions with e1, e2, .., en clauses
     * this mechanism produces e1*e2*...*en clauses
     */
    private def combineCNF(cnfchildren: Set[FeatureExpr]) =
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

    /**
     * introduces new variables to avoid exponential behavior
     *
     * for n CNF expressions with e1, e2, .., en clauses
     * this mechanism produces n new variables and results
     * in e1+e2+..+en+1 clauses
     *
     */
    private def combineEquiCNF(cnfchildren: Set[FeatureExpr]) =
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

