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
    def createEquiv(left: FeatureExpr, right: FeatureExpr) = createImplies(left, right) and createImplies(right, left)
    def createDefinedExternal(name: String) = new FeatureExprImpl(new DefinedExternal(name))
    //create a macro definition (which expands to the current entry in the macro table; the current entry is stored in a closure-like way).
    def createDefinedMacro(name: String, macroTable: FeatureProvider) = new FeatureExprImpl(new DefinedMacro(
        name,
        macroTable.getMacroCondition(name),
        LazyLib.delay(macroTable.getMacroConditionCNF(name))))
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
}

/**
 * feature expressions
 * 
 * always stored in three formats: as constructed, CNF and DNF.
 * CNF and DNF are updated immediately on changes
 */
protected class FeatureExprImpl(var aexpr: FeatureExprTree) extends FeatureExpr {

    def expr: FeatureExprTree = aexpr

    def simplify() { this.aexpr = aexpr.simplify; aexpr; }

    def and(that: FeatureExpr): FeatureExpr = new FeatureExprImpl(
        And(this.expr, that.expr))

    def or(that: FeatureExpr): FeatureExpr = new FeatureExprImpl(
        Or(this.expr, that.expr))

    def not(): FeatureExpr = new FeatureExprImpl(
        Not(this.expr))

    def toCNF: NF =
        try {
            simplify
            NFBuilder.toCNF(expr.toCNF)
        } catch {
            case t: Throwable => {
                System.err.println("Exception on isSatisfiable for: " + expr.print())
                t.printStackTrace
                throw t
            }
        }
    def toEquiCNF: NF =
        try {
            simplify
            NFBuilder.toCNF(expr.toCnfEquiSat)
        } catch {
            case t: Throwable => {
                System.err.println("Exception on isSatisfiable for: " + expr.print())
                t.printStackTrace
                throw t
            }
        }

    def isSatisfiable(): Boolean =
        new SatSolver().isSatisfiable(toEquiCNF)

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

    /**
     * replace DefinedMacro by DefinedExternal from MacroTable
     */
    def resolveToExternal(): FeatureExpr = {
        simplify
        new FeatureExprImpl(aexpr.resolveToExternal())
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
                    var childrenFlattened: List[FeatureExprTree] = List()//computing sets is to expensive
                    for (childs <- childrenSimplified)
                        childs match {
                            case And(innerChildren) => childrenFlattened = childrenFlattened ++ innerChildren
                            case e => childrenFlattened = e::childrenFlattened
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
                    val childrenSimplified = children.map(_.simplify().intToBool()).filter(!DeadFeature.unapply(_));
                    var childrenFlattened: List[FeatureExprTree] = List()
                    for (childs <- childrenSimplified)
                        childs match {
                            case Or(innerChildren) => childrenFlattened = childrenFlattened ++ innerChildren
                            case e => childrenFlattened = e::childrenFlattened 
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
        //TODO caching
        this match {
            case And(children) => And(children.map(_.resolveToExternal()))
            case Or(children) => Or(children.map(_.resolveToExternal()))
            case BinaryFeatureExprTree(left, right, opStr, op) => BinaryFeatureExprTree(left resolveToExternal, right resolveToExternal, opStr, op)
            case UnaryFeatureExprTree(expr, opStr, op) => UnaryFeatureExprTree(expr resolveToExternal, opStr, op)
            case Not(a) => Not(a.resolveToExternal)
            case IfExpr(c, a, b) => IfExpr(c.resolveToExternal, a.resolveToExternal, b resolveToExternal)
            case IntegerLit(_) => this
            case DefinedExternal(_) => this
            case DefinedMacro(name, expansion, cnf) => expansion.expr //TODO stupid to throw away CNF and DNF
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
                    case e: IfExpr => Not(e.toCnfEquiSat()).simplify.toCnfEquiSat()
                    case e => {
                        Not(e.toCnfEquiSat)
                    }
                }
            case And(children) => And(children.map(_.toCnfEquiSat)).simplify
            case Or(children) => {
                val cnfchildren = children.map(_.toCnfEquiSat)
                if (cnfchildren.exists(_.isInstanceOf[And])) {
                    var orClauses: List[FeatureExprTree] = List() //list of Or expressions
                    //	        val freshFeatureNames:Set[FeatureExprTree]=for (child<-children) yield DefinedExternal(freshFeatureName())

                    var freshFeatureNames: List[FeatureExprTree] = List()
                    for (child <- cnfchildren) {
                        val freshFeatureName = Not(DefinedExternal(FeatureExpr.calcFreshFeatureName()))
                        child match {
                            case And(innerChildren) => {
                                for (innerChild <- innerChildren)
                                    orClauses = new Or(freshFeatureName, innerChild)::orClauses
                            }
                            case e => orClauses=new Or(freshFeatureName, e)::orClauses
                        }
                        freshFeatureNames = Not(freshFeatureName).simplify :: freshFeatureNames
                    }
                    orClauses = Or(freshFeatureNames)::orClauses
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
    children: List[FeatureExprTree],
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

abstract class DefinedExpr extends FeatureExprTree {
    var feature: String = "";
    def this(name: String) { this(); feature = name; assert(name != "1" && name != "0" && name != "") }
    def debug_print(level: Int): String = indent(level) + feature + "\n";
    def accept(f: FeatureExprTree => Unit): Unit = f(this)
    def satName = feature //used for sat solver only to distinguish extern and macro
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
}

/**
 * definition based on a macro, still to be resolved using the macro table
 * (the macro table may not contain DefinedMacro expressions, but only DefinedExternal)
 */
case class DefinedMacro(name: String, presenceCondition: FeatureExpr, presenceConditionCNF: Susp[NF]) extends DefinedExpr(name) {
    def print(): String = {
        assert(name != "")
        "defined(" + name + ")";
    }
    override def satName = "$" + name
    /**
     * definedMacros are equal if they have the same Name and the same expansion! (otherwise they refer to 
     * the macro at different points in time and should not be considered equal)
     * actually, we do not need to check for the same name, otherwise they would not have the same expansion
     */
    override def equals(that: Any) = that match {
        case e@DefinedMacro(aname, _, apresenceConditionCNF) => (this eq e) || ((this.name == aname) && (this.presenceConditionCNF eq apresenceConditionCNF))
        case _ => false
    }
    override def hashCode = presenceConditionCNF.hashCode
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
case class And(children: List[FeatureExprTree]) extends AbstractNaryBinaryFeatureExprTree(children, "&&", _ && _) {
    def this(left: FeatureExprTree, right: FeatureExprTree) = this(List(left, right))
    def addChild(child: FeatureExprTree) = And(child :: children);
}
object And {
    def apply(a: FeatureExprTree, b: FeatureExprTree) = new And(a, b)
}
case class Or(children: List[FeatureExprTree]) extends AbstractNaryBinaryFeatureExprTree(children, "||", _ || _) {
    def this(left: FeatureExprTree, right: FeatureExprTree) = this(List(left, right))
    def addChild(child: FeatureExprTree) = Or(child :: children);
}
object Or {
    def apply(a: FeatureExprTree, b: FeatureExprTree) = new Or(a, b)
}

case class UnaryFeatureExprTree(expr: FeatureExprTree, opStr: String, op: (Long) => Long) extends AbstractUnaryFeatureExprTree(expr, opStr, op)
case class BinaryFeatureExprTree(left: FeatureExprTree, right: FeatureExprTree, opStr: String, op: (Long, Long) => Long) extends AbstractBinaryFeatureExprTree(left, right, opStr, op)

