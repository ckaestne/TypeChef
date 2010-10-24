package de.fosd.typechef.featureexpr

object FeatureExpr {
    def createDefined(feature: String, context: FeatureProvider): FeatureExpr =
        context.getMacroCondition(feature)

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
    def createInteger(value: Long): FeatureExpr = new FeatureExprImpl(IntegerLit(value))
    def createCharacter(value: Char): FeatureExpr = new FeatureExprImpl(CharacterLit(value))
    def createIf(condition: FeatureExpr, thenBranch: FeatureExpr, elseBranch: FeatureExpr) = new FeatureExprImpl(IfExpr(condition.expr, thenBranch.expr, elseBranch.expr))
    def createIf(condition: FeatureExprTree, thenBranch: FeatureExprTree, elseBranch: FeatureExprTree) = new FeatureExprImpl(IfExpr(condition, thenBranch, elseBranch))

    val base = new FeatureExprImpl(BaseFeature())
    val dead = new FeatureExprImpl(DeadFeature())

    private var freshFeatureNameCounter = 0
    def calcFreshFeatureName(): String = { freshFeatureNameCounter = freshFeatureNameCounter + 1; "__fresh" + freshFeatureNameCounter; }
}

trait FeatureExpr {
    def expr: FeatureExprTree
    def cnfExpr: Option[NF]
    def dnfExpr: Option[NF]
    def toString(): String
    def isContradiction() = !isSatisfiable
    def isTautology() = !this.not.isSatisfiable
    def isSatisfiable(): Boolean
    def isDead() = isContradiction
    def isBase() = isTautology
    def accept(f: FeatureExprTree => Unit): Unit
    def print(): String
    def debug_print(): String
    def equals(that: Any): Boolean

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
protected class FeatureExprImpl(aexpr: FeatureExprTree, acnfExpr: Option[NF], adnfExpr: Option[NF]) extends FeatureExpr {
    def this(expr: FeatureExprTree) = this(expr, NFBuilder.toCNF_(expr), NFBuilder.toDNF_(expr))

    def expr: FeatureExprTree = aexpr
    def cnfExpr = acnfExpr
    def dnfExpr = adnfExpr

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

    def isSatisfiable(): Boolean = this.cnfExpr match {
        case Some(cnfExpr) => new SatSolver().isSatisfiable(cnfExpr)
        case None => new SatSolver().isSatisfiable(NFBuilder.toCNF(expr))
    }

    def u(a: Option[NF], b: Option[NF], op: (NF, NF) => NF): Option[NF] = (a, b) match {
        case (Some(na), Some(nb)) => Some(op(na, nb))
        case _ => None
    }
    def neg(a: Option[NF]): Option[NF] = a match {
        case Some(na) => Some(na.neg)
        case None => None
    }

    override def toString(): String = this.print()

    def accept(f: FeatureExprTree => Unit): Unit = { simplify(); expr.accept(f) }

    def print(): String = cnfExpr match {
        case Some(cnf) =>
            cnf.printCNF
        case None => simplify(); expr.print()
    }

    def debug_print(): String = { simplify(); expr.debug_print(0); }

    override def equals(that: Any) = that match {
        case e: FeatureExpr => (this eq e) || (this.expr eq e.expr) || this.implies(e).and(e.implies(this)).isBase;
        case _ => false
    }

}

/** normal form for both DNF and CNF **/
class NF(val clauses: Set[Clause], val isFull: Boolean) {
    /** isFull is meant to be the oppositve of empty
     * with CNF empty means always true and full means always false
     * with DNF empty means always false and full means always true   
     * it is not valid to set clauses and isFull at the same time  */

    def this(c: Set[Clause]) = this(c.map(_.simplify).filter(!_.isEmpty), false)
    def this(emptyOrFull_isFull: Boolean) = this(Set(), emptyOrFull_isFull)

    /** join (CNF and CNF / DNF or DNF)**/
    def ++(that: NF) = if (this.isFull || that.isFull) new NF(true) else new NF(this.clauses ++ that.clauses)
    /** explode (CNF or CNF / DNF and DNF)**/
    def **(that: NF) =
        if (this.isFull) that
        else if (that.isFull) this
        else new NF(for (clauseA <- this.clauses; clauseB <- that.clauses) yield clauseA ++ clauseB)
    /** negate all literals **/
    def neg() =
        if (isEmpty || isFull) this
        else new NF(clauses.map(_.neg))
    /** empty means true for CNF, false for DNF **/
    def isEmpty = !isFull && clauses.isEmpty
    override def toString = if (isEmpty) "EMPTY" else if (isFull) "FULL" else clauses.mkString("*")
    def printCNF = if (isEmpty) "1" else if (isFull) "0" else clauses.map(_.printCNF).mkString("&&")
    override def hashCode = clauses.hashCode
    override def equals(that: Any) = that match { case thatNF: NF => this.clauses equals thatNF.clauses; case _ => false }
}
/** clause in a normal form **/
class Clause(var posLiterals: Set[DefinedExternal], var negLiterals: Set[DefinedExternal]) {
    def simplify = {
        //A || !A = true 
        if (!(posLiterals intersect negLiterals).isEmpty) {
            posLiterals = Set()
            negLiterals = Set()
        }
        this
    }
    def isEmpty = posLiterals.isEmpty && negLiterals.isEmpty
    /** join two clauses **/
    def ++(that: Clause) = new Clause(this.posLiterals ++ that.posLiterals, this.negLiterals ++ that.negLiterals).simplify
    def neg() = new Clause(this.negLiterals, this.posLiterals)
    def size = posLiterals.size + negLiterals.size
    override def toString =
        (posLiterals.map(_.feature) ++ negLiterals.map("!" + _.feature)).mkString("(", "*", ")")
    def printCNF =
        (posLiterals.map(_.print) ++ negLiterals.map(Not(_).print)).mkString("(", "||", ")")
    override def hashCode = posLiterals.hashCode + negLiterals.hashCode
    override def equals(that: Any) = that match {
        case thatClause: Clause => (this.posLiterals equals thatClause.posLiterals) && (this.negLiterals equals thatClause.negLiterals)
        case _ => false
    }
}
object NFBuilder {
    def toCNF_(expr: FeatureExprTree): Option[NF] = try { Some(toCNF(expr)) } catch { case e: NFException => None }
    def toDNF_(expr: FeatureExprTree): Option[NF] = try { Some(toDNF(expr)) } catch { case e: NFException => None }
    def toCNF(expr: FeatureExprTree): NF = toNF(expr.toCNF, true)
    def toDNF(expr: FeatureExprTree): NF = toNF(expr.toDNF, false)
    def toNF(expr: FeatureExprTree, isCNF: Boolean) = expr match {
        case And(clauses) if isCNF => {
            new NF(for (clause <- clauses) yield clause match {
                case Or(o) => toClause(o)
                case e => toClause(Set(e)) //literal?
            })
        }
        case Or(clauses) if !isCNF => {
            new NF(for (clause <- clauses) yield clause match {
                case And(c) => toClause(c)
                case e => toClause(Set(e)) //literal?
            })
        }
        case Or(o) if isCNF => new NF(Set(toClause(o)))
        case And(o) if !isCNF => new NF(Set(toClause(o)))
        case f@DefinedExternal(_) => new NF(Set(new Clause(Set(f), Set())))
        case Not(f@DefinedExternal(_)) => new NF(Set(new Clause(Set(), Set(f))))
        case BaseFeature() => new NF(!isCNF)
        case DeadFeature() => new NF(isCNF)
        case e => throw new NoNFException(e, expr, isCNF)
    }
    def toClause(literals: Set[FeatureExprTree]): Clause = {
        var posLiterals: Set[DefinedExternal] = Set()
        var negLiterals: Set[DefinedExternal] = Set()
        for (literal <- literals)
            literal match {
                case f@DefinedExternal(_) => posLiterals = posLiterals + f
                case Not(f@DefinedExternal(_)) => negLiterals = negLiterals + f
                case e => throw new NoLiteralException(e)
            }
        new Clause(posLiterals, negLiterals)
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
            val result = this match {
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

                case BinaryFeatureExprTree(left, right, opStr, op) =>
                    (left simplify, right simplify) match {
                        case (IntegerLit(a), IntegerLit(b)) => IntegerLit(op(a, b))
                        case (IfExpr(c, a, b), right) => IfExpr(c, BinaryFeatureExprTree(a, right, opStr, op), BinaryFeatureExprTree(b, right, opStr, op)).simplify
                        case (left, IfExpr(c, a, b)) => IfExpr(c, BinaryFeatureExprTree(left, a, opStr, op), BinaryFeatureExprTree(left, b, opStr, op)).simplify
                        case (a, b) => BinaryFeatureExprTree(a, b, opStr, op)
                    }

                case UnaryFeatureExprTree(expr, opStr, op) =>
                    expr simplify match {
                        case IntegerLit(x) => IntegerLit(op(x));
                        case IfExpr(c, a, b) => IfExpr(c, UnaryFeatureExprTree(a, opStr, op), UnaryFeatureExprTree(b, opStr, op)).simplify
                        case x => UnaryFeatureExprTree(x, opStr, op)
                    }

                case Not(a) =>
                    a.simplify.intToBool() match {
                        case IntegerLit(v) => if (v == 0) BaseFeature() else DeadFeature()
                        case Not(e) => e
                        case e => Not(e)
                    }

                case IfExpr(c, a, b) => {
                    val as = a simplify;
                    val bs = b simplify;
                    val cs = c simplify;
                    if (cs == BaseFeature()) as
                    else if (cs == DeadFeature()) bs
                    else if (as == bs) as
                    else IfExpr(cs, as, bs)
                }

                case IntegerLit(_) => this

                case CharacterLit(_) => this

                case DefinedExternal(_) => this
            }
            result.setSimplified
        }
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

    //def eval(context:FeatureProvider) = op(left.eval(context), right.eval(context))
    def print() = "(" + left.print + " " + opStr + " " + right.print + ")"
    def debug_print(level: Int): String =
        indent(level) + opStr + "\n" +
            left.debug_print(level + 1) +
            right.debug_print(level + 1);
    //  def calcPossibleValues():Set[Long] = {
    //    var result=Set[Long]()
    //    for (
    //    	a<-left.possibleValues();
    //        b<-right.possibleValues()
    //    ) result += op(a, b)
    //    result
    //  }
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
    //def eval(context:FeatureProvider) = op(expr.eval(context))
    def print() = opStr + "(" + expr.print + ")"
    def debug_print(level: Int) = indent(level) + opStr + "\n" + expr.debug_print(level + 1);
    //  def calcPossibleValues():Set[Long] = {
    //    var result=Set[Long]()
    //    for (
    //    	a<-expr.possibleValues()
    //    ) result += op(a)
    //    result
    //  }
    def accept(f: FeatureExprTree => Unit): Unit = {
        f(this)
        expr.accept(f)
    }
}
abstract class AbstractUnaryBoolFeatureExprTree(
    expr: FeatureExprTree,
    opStr: String,
    op: (Boolean) => Boolean) extends AbstractUnaryFeatureExprTree(expr, opStr, (ev) => if (op(ev != 0)) 1 else 0);

/** external definion of a feature (cannot be decided to Base or Dead inside this file) */
case class DefinedExternal(feature: String) extends FeatureExprTree {
    def print(): String = {
        assert(feature != "")
        "defined(" + feature + ")";
    }
    def debug_print(level: Int): String = indent(level) + feature + "\n";
    def accept(f: FeatureExprTree => Unit): Unit = f(this)
}

case class CharacterLit(char: Int) extends FeatureExprTree {
    def print(): String = "'" + char.toString + "'";
    def debug_print(level: Int): String = indent(level) + print() + "\n";
    //def eval(context:FeatureProvider):Long = char.toLong;
    def calcPossibleValues(): Set[Long] = Set(char.toLong)
    def accept(f: FeatureExprTree => Unit): Unit = f(this)
}

case class IntegerLit(num: Long) extends FeatureExprTree {
    def print(): String = num.toString
    def debug_print(level: Int): String = indent(level) + print() + "\n"
    //def eval(context:FeatureProvider):Long = num;
    def calcPossibleValues(): Set[Long] = Set(num)
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
    //	def calcPossibleValues() = if (condition.isBase()) thenBranch.possibleValues()
    //                        else if (condition.isDead()) elseBranch.possibleValues()
    //                        else thenBranch.possibleValues() ++ elseBranch.possibleValues()
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
    //  def calcPossibleValues():Set[Long] = {
    //    var result:Set[Long]=Set()
    //    if (children.exists(_.calcPossibleValues().exists(_==0))) result+=0
    //    if (children.forall(_.calcPossibleValues().exists(_==1))) result+=1
    //    result
    //  }
    def addChild(child: FeatureExprTree) = And(children + child);
}
object And {
    def apply(a: FeatureExprTree, b: FeatureExprTree) = new And(a, b)
}
case class Or(children: Set[FeatureExprTree]) extends AbstractNaryBinaryFeatureExprTree(children, "||", _ || _) {
    def this(left: FeatureExprTree, right: FeatureExprTree) = this(Set(left, right))
    //  def calcPossibleValues():Set[Long] = {
    //    var result:Set[Long]=Set()
    //    if (children.exists(_.calcPossibleValues().exists(_==1))) result+=1
    //    if (children.forall(_.calcPossibleValues().exists(_==0))) result+=0
    //    result
    //  }
    def addChild(child: FeatureExprTree) = Or(children + child);
}
object Or {
    def apply(a: FeatureExprTree, b: FeatureExprTree) = new Or(a, b)
}

case class UnaryFeatureExprTree(expr: FeatureExprTree, opStr: String, op: (Long) => Long) extends AbstractUnaryFeatureExprTree(expr, opStr, op)
case class BinaryFeatureExprTree(left: FeatureExprTree, right: FeatureExprTree, opStr: String, op: (Long, Long) => Long) extends AbstractBinaryFeatureExprTree(left, right, opStr, op)

