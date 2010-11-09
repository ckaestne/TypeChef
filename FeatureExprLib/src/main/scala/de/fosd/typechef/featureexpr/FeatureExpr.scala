package de.fosd.typechef.featureexpr

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
    def createDefinedExternal(name: String) = new FeatureExprImpl(new DefinedExternal(name))
    def createDefinedMacro(name: String) = new FeatureExprImpl(new DefinedMacro(name))
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
    def cnfExpr: Susp[Option[NF]]
    def dnfExpr: Susp[Option[NF]]
    def toString(): String
    def toCNF: NF
    def isContradiction(macroTable: FeatureProvider) = !isSatisfiable(macroTable)
    def isTautology(macroTable: FeatureProvider) = !this.not.isSatisfiable(macroTable)
    def isSatisfiable(macroTable: FeatureProvider): Boolean
    def isDead(macroTable: FeatureProvider) = isContradiction(macroTable)
    def isBase(macroTable: FeatureProvider) = isTautology(macroTable)
    def accept(f: FeatureExprTree => Unit): Unit
    def print(): String
    def debug_print(): String
    def equals(that: Any): Boolean
    def resolveToExternal(macroTable: FeatureProvider): FeatureExpr
    def isResolved(): Boolean

    def or(that: FeatureExpr): FeatureExpr
    def and(that: FeatureExpr): FeatureExpr
    def implies(that: FeatureExpr): FeatureExpr = this.not or that
    def not(): FeatureExpr
}

/**
 * feature expressions
 * 
 * always stored in three formats: as constructed, CNF and DNF.
 * CNF and DNF are updated immediately on changes
 */
protected class FeatureExprImpl(aexpr: FeatureExprTree, acnfExpr: Susp[Option[NF]], adnfExpr: Susp[Option[NF]]) extends FeatureExpr {
    def this(expr: FeatureExprTree) = this(expr, delay(NFBuilder.toCNF_(expr.toCNF)), delay(NFBuilder.toDNF_(expr.toDNF)))

    def expr: FeatureExprTree = aexpr
    def cnfExpr = acnfExpr
    def dnfExpr = adnfExpr

    //XXX doesn't look very useful, it is not overriden by anybody!
    def simplify(): FeatureExpr = this

    def and(that: FeatureExpr): FeatureExpr = new FeatureExprImpl(
        And(this.expr, that.expr),
        u(this.cnfExpr, that.cnfExpr, _ ++ _),
        u(this.dnfExpr, that.dnfExpr, _ ** _))

    def or(that: FeatureExpr): FeatureExpr = new FeatureExprImpl(
        Or(this.expr, that.expr),
        u(this.cnfExpr, that.cnfExpr, _ ** _),
        u(this.dnfExpr, that.dnfExpr, _ ++ _))

    def not(): FeatureExpr = new FeatureExprImpl(
        Not(this.expr),
        neg(this.dnfExpr),
        neg(this.cnfExpr))

    def toCNF: NF =
        try {
        	NFBuilder.toCNF(expr.toCNF)
//            this.cnfExpr() match {
//                case Some(cnfExpr) => cnfExpr
//                case None => NFBuilder.toCNF(expr.toCNF)
//            }
        } catch {
            case t: Throwable => {
                System.err.println("Exception on isSatisfiable for: " + expr.print())
                t.printStackTrace
                throw t
            }
        }

    def isSatisfiable(macroTable: FeatureProvider): Boolean =
        new SatSolver().isSatisfiable(macroTable, toCNF)

    def u(a: Susp[Option[NF]], b: Susp[Option[NF]], op: (NF, NF) => NF): Susp[Option[NF]] = delay((a(), b()) match {
        case (Some(na), Some(nb)) => Some(op(na, nb))
        case _ => None
    })
    def neg(a: Susp[Option[NF]]): Susp[Option[NF]] = delay(a() match {
        case Some(na) => Some(na.neg)
        case None => None
    })

    override def toString(): String = this.print()

    def accept(f: FeatureExprTree => Unit): Unit = { simplify(); expr.accept(f) }

    def print(): String = cnfExpr() match {
        case Some(cnf) =>
            cnf.printCNF
        case None => simplify(); expr.print()
    }

    def debug_print(): String = {
        //XXX: Simplify does not modify the expression, it produces a new one!!!
        simplify();
        expr.debug_print(0);
    }

    override def equals(that: Any) = that match {
        case e: FeatureExpr => (this eq e) || (this.expr eq e.expr) || this.implies(e).and(e.implies(this)).isBase(null);
        case _ => false
    }

    /**
     * checks whether there is some unresolved macro (DefinedMacro) somewhere 
     * in the expression tree
     */
    def isResolved() = aexpr.isResolved

    /**
     * replace DefinedMacro by DefinedExternal from MacroTable
     */
    def resolveToExternal(macroTable: FeatureProvider): FeatureExpr =
        new FeatureExprImpl(aexpr.resolveToExternal(macroTable))

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
                    val childrenSimplified = children.map(_.simplify().intToBool()) - BaseFeature(); //TODO also remove all non-zero integer literals
                    var childrenFlattened: Set[FeatureExprTree] = Set()
                    for (childs <- childrenSimplified)
                        childs match {
                            case And(innerChildren) => childrenFlattened = childrenFlattened ++ innerChildren
                            case e => childrenFlattened = childrenFlattened + e
                        }
                    for (childs <- childrenFlattened)
                        if (childrenFlattened.exists(_ == Not(childs)))
                            return DeadFeature();
                    if (childrenFlattened.exists(_ == DeadFeature()))
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
                    val childrenSimplified = children.map(_.simplify().intToBool()) - DeadFeature() - IntegerLit(0);
                    var childrenFlattened: Set[FeatureExprTree] = Set()
                    for (childs <- childrenSimplified)
                        childs match {
                            case Or(innerChildren) => childrenFlattened = childrenFlattened ++ innerChildren
                            case e => childrenFlattened = childrenFlattened + e
                        }
                    for (childs <- childrenFlattened)
                        if (childrenFlattened.exists(_ == Not(childs)))
                            return BaseFeature();
                    if (childrenFlattened.exists(_ == BaseFeature()))
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
    private def bubbleUpIf: FeatureExprTree =
        this match {
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

    private var isResolvedCache: Option[Boolean] = None
    def isResolved(): Boolean = {
        if (isResolvedCache.isDefined)
            return isResolvedCache.get
        var foundDefinedMacro = false;
        this.accept(
            _ match {
                case DefinedMacro(_) => foundDefinedMacro = true
                case _ =>
            })
        isResolvedCache = Some(!foundDefinedMacro)
        !foundDefinedMacro
    }
    def resolveToExternal(macroTable: FeatureProvider): FeatureExprTree = {
        if (isResolvedCache.isDefined && isResolvedCache.get) return this;
        val result = this match {
            case And(children) => And(children.map(_.resolveToExternal(macroTable)))
            case Or(children) => Or(children.map(_.resolveToExternal(macroTable)))
            case BinaryFeatureExprTree(left, right, opStr, op) => BinaryFeatureExprTree(left resolveToExternal (macroTable), right resolveToExternal (macroTable), opStr, op)
            case UnaryFeatureExprTree(expr, opStr, op) => UnaryFeatureExprTree(expr resolveToExternal (macroTable), opStr, op)
            case Not(a) => Not(a.resolveToExternal(macroTable))
            case IfExpr(c, a, b) => IfExpr(c.resolveToExternal(macroTable), a.resolveToExternal(macroTable), b resolveToExternal (macroTable))
            case IntegerLit(_) => this
            case DefinedExternal(_) => this
            case DefinedMacro(name) => macroTable.getMacroCondition(name).expr //TODO stupid to throw away CNF and DNF
        }
        result
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
                    var orClauses: Set[Or] = Set(Or(Set())) //list of Or expressions
                    for (child <- cnfchildren) {
                        child match {
                            case And(innerChildren) => {
                                var newClauses: Set[Or] = Set()
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
    def toDNF(): FeatureExprTree = this.simplify match {
        case IfExpr(c, a, b) => new Or(new And(c, a), new And(Not(c), b)).toDNF()
        case Not(And(children)) => Or(children.map(Not(_).toDNF())).toDNF()
        case Not(Or(children)) => And(children.map(Not(_).toDNF())).toDNF()
        case Or(children) => Or(children.map(_.toDNF)).simplify
        case And(children) => {
            val dnfchildren = children.map(_.toDNF)
            if (dnfchildren.exists(_.isInstanceOf[Or])) {
                var andClauses: Set[And] = Set(And(Set())) //list of Or expressions
                for (child <- dnfchildren) {
                    child match {
                        case Or(innerChildren) => {
                            var newClauses: Set[And] = Set()
                            for (innerChild <- innerChildren)
                                newClauses = newClauses ++ andClauses.map(_.addChild(innerChild));
                            andClauses = newClauses;
                        }
                        case _ => andClauses = andClauses.map(_.addChild(child));
                    }
                }
                And(andClauses.map(a => a)).simplify
            } else Or(dnfchildren)
        }
        case e => e
    }
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
}
abstract class AbstractNaryBinaryFeatureExprTree(
    children: Set[FeatureExprTree],
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
}
abstract class AbstractUnaryBoolFeatureExprTree(
    expr: FeatureExprTree,
    opStr: String,
    op: (Boolean) => Boolean) extends AbstractUnaryFeatureExprTree(expr, opStr, (ev) => if (op(ev != 0)) 1 else 0);

abstract class DefinedExpr(val feature: String) extends FeatureExprTree {
    def debug_print(level: Int): String = indent(level) + feature + "\n";
    def accept(f: FeatureExprTree => Unit): Unit = f(this)
    def satName = feature //used for sat solver only to distinguish extern and macro
}
object DefinedExpr {
    def unapply(f: DefinedExpr): Option[DefinedExpr] = f match {
        case x@DefinedExternal(_) => Some(x)
        case x@DefinedMacro(_) => Some(x)
        case _ => None
    }
}

/** external definion of a feature (cannot be decided to Base or Dead inside this file) */
case class DefinedExternal(name: String) extends DefinedExpr(name) {
    def print(): String = {
        assert(name != "")
        "definedEx(" + name + ")";
    }
}

/**
 * definition based on a macro, still to be resolved using the macro table
 * (the macro table may not contain DefinedMacro expressions, but only DefinedExternal)
 */
case class DefinedMacro(name: String) extends DefinedExpr(name) {
    def print(): String = {
        assert(name != "")
        "defined(" + name + ")";
    }
    override def satName = "$" + name
}

case class IntegerLit(num: Long) extends FeatureExprTree {
    def print(): String = num.toString
    def debug_print(level: Int): String = indent(level) + print() + "\n"
    def accept(f: FeatureExprTree => Unit): Unit = f(this)
    override def intToBool() = if (num == 0) DeadFeature() else BaseFeature()
    def getNum = num
}

object DeadFeature {
    def unapply(f: FeatureExprTree): Boolean = f match {
        case IntegerLit(0) => true
        case _ => false
    }
    def apply() = new IntegerLit(0)
}
object BaseFeature {
    def unapply(f: FeatureExprTree): Boolean = f match {
        case IntegerLit(1) => true
        case _ => false
    }
    def apply() = new IntegerLit(1)
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
}

case class Not(expr: FeatureExprTree) extends AbstractUnaryBoolFeatureExprTree(expr, "!", !_);
case class And(children: Set[FeatureExprTree]) extends AbstractNaryBinaryFeatureExprTree(children, "&&", _ && _) {
    def this(left: FeatureExprTree, right: FeatureExprTree) = this(Set(left, right))
    def addChild(child: FeatureExprTree) = And(children + child);
}
object And {
    def apply(a: FeatureExprTree, b: FeatureExprTree) = new And(a, b)
}
case class Or(children: Set[FeatureExprTree]) extends AbstractNaryBinaryFeatureExprTree(children, "||", _ || _) {
    def this(left: FeatureExprTree, right: FeatureExprTree) = this(Set(left, right))
    def addChild(child: FeatureExprTree) = Or(children + child);
}
object Or {
    def apply(a: FeatureExprTree, b: FeatureExprTree) = new Or(a, b)
}

case class UnaryFeatureExprTree(expr: FeatureExprTree, opStr: String, op: (Long) => Long) extends AbstractUnaryFeatureExprTree(expr, opStr, op)
case class BinaryFeatureExprTree(left: FeatureExprTree, right: FeatureExprTree, opStr: String, op: (Long, Long) => Long) extends AbstractBinaryFeatureExprTree(left, right, opStr, op)

