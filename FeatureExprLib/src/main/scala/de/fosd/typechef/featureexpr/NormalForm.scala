package de.fosd.typechef.featureexpr

/**
 * classes used to represent CNF and DNF expressions
 * 
 * (note, this does not include the process of translation from arbitrary
 * expressions to CNF, it is just for representing expressions that are already
 * in normal form)
 */

/** normal form for both DNF and CNF **/
class NF(val clauses: Set[Clause], val isFull: Boolean) {
    /** isFull is meant to be the oppositve of empty
     * with CNF empty means always true and full means always false
     * with DNF empty means always false and full means always true   
     * it is not valid to set clauses and isFull at the same time  */

    def this(c: Set[Clause]) = this(c.map(_.simplify).filter(!_.isEmpty), false)
    def this(emptyOrFull_isFull: Boolean) = this(Set(), emptyOrFull_isFull)

    /** join (CNF and CNF / DNF or DNF)**/
    def ++(that: NF) =
        if (this.isFull || that.isFull) new NF(true) else new NF(this.clauses ++ that.clauses)
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
    /** returns a set with all referenced macros (DefinedMacro)**/
    def findMacros(): Set[DefinedMacro] = {
        var result: Set[DefinedMacro] = Set()
        clauses.foreach(clause => {
            result = result ++ clause.findMacros
        })
        result
    }
    /**helper function for the SAT solver. replaces all macros named "$$" by the given name */
    def replaceMacroName(targetName: String): NF = new NF(clauses.map(_.replaceMacroName(targetName)), isFull)
}
/** clause in a normal form **/
class Clause(var posLiterals: Set[DefinedExpr], var negLiterals: Set[DefinedExpr]) {
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
        (posLiterals.map(_.satName) ++ negLiterals.map("!" + _.satName)).mkString("(", "*", ")")
    def printCNF =
        (posLiterals.map(_.print) ++ negLiterals.map(Not(_).print)).mkString("(", "||", ")")
    override def hashCode = posLiterals.hashCode + negLiterals.hashCode
    override def equals(that: Any) = that match {
        case thatClause: Clause => (this.posLiterals equals thatClause.posLiterals) && (this.negLiterals equals thatClause.negLiterals)
        case _ => false
    }
    /** returns a set with all referenced macros (DefinedMacro)**/
    def findMacros(): Set[DefinedMacro] = {
        var result: Set[DefinedMacro] = Set()
        ((posLiterals.toList) ++ (negLiterals.toList)).foreach(
            _ match {
                case x: DefinedMacro => result = result + x
                case DefinedExternal(_) =>
            })
        result
    }
    /**helper function for the SAT solver. replaces all macros named "$$" by the given name */
    def replaceMacroName(targetName: String): Clause = new Clause(posLiterals.map(replaceMacro(_, targetName)), negLiterals.map(replaceMacro(_, targetName)))
    private def replaceMacro(literal: DefinedExpr, targetName: String): DefinedExpr =
        literal match {
            case DefinedExternal(name) if (name == NFBuilder.HOLE) => DefinedExternal(targetName)
            case e => e
        }
}

/**
 * NFBuilder builds normal form classes from expressions that are already in normal form
 * 
 * NFBuilder will not turn arbitrary expressions into normal forms! Use 
 * FeatureExprTree.toCNF before. Throws an exception when applied to
 * a non-NF formula 
 */
object NFBuilder {
    val HOLE = "$$"
    def toCNF_(exprInCNF: FeatureExprTree): Option[NF] = try { Some(toCNF(exprInCNF)) } catch { case e: NFException => None }
    def toDNF_(exprInDNF: FeatureExprTree): Option[NF] = try { Some(toDNF(exprInDNF)) } catch { case e: NFException => None }
    def toCNF(exprInCNF: FeatureExprTree): NF = toNF(exprInCNF, true)
    def toDNF(exprInDNF: FeatureExprTree): NF = toNF(exprInDNF, false)
    private def toNF(exprInNF: FeatureExprTree, isCNF: Boolean) = exprInNF match {
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
        case f@DefinedExpr(_) => new NF(Set(new Clause(Set(f), Set())))
        case Not(f@DefinedExpr(_)) => new NF(Set(new Clause(Set(), Set(f))))
        case BaseFeature() => new NF(!isCNF)
        case DeadFeature() => new NF(isCNF)
        case e => throw new NoNFException(e, exprInNF, isCNF)
    }
    private def toClause(literals: Set[FeatureExprTree]): Clause = {
        var posLiterals: Set[DefinedExpr] = Set()
        var negLiterals: Set[DefinedExpr] = Set()
        for (literal <- literals)
            literal match {
                case f@DefinedExpr(_) => posLiterals = posLiterals + f
                case Not(f@DefinedExpr(_)) => negLiterals = negLiterals + f
                case e => throw new NoLiteralException(e)
            }
        new Clause(posLiterals, negLiterals)
    }
}
