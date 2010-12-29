package de.fosd.typechef.featureexpr

import scala.collection.mutable.ArrayBuffer

/**
 * classes used to represent CNF and DNF expressions
 *
 * (note, this does not include the process of translation from arbitrary
 * expressions to CNF, it is just for representing expressions that are already
 * in normal form)
 */


/**normal form for both DNF and CNF **/
class NF(val clauses: Seq[Clause], val isFull: Boolean) {
    /**isFull is meant to be the opposite of empty
     * with CNF empty means always true and full means always false
     * with DNF empty means always false and full means always true   
     * it is not valid to set clauses and isFull at the same time  */

    def this(c: Seq[Clause]) = this (c.map(_.simplify).filter(!_.isEmpty), false)
    def this(emptyOrFull_isFull: Boolean) = this (SmallList(), emptyOrFull_isFull)

    //    /** join (CNF and CNF / DNF or DNF)**/
    //    def ++(that: NF) =
    //        if (this.isFull || that.isFull) new NF(true) else new NF(this.clauses ++ that.clauses)
    //    /** explode (CNF or CNF / DNF and DNF)**/
    //    def **(that: NF) =
    //        if (this.isFull) that
    //        else if (that.isFull) this
    //        else new NF(for (clauseA <- this.clauses; clauseB <- that.clauses) yield clauseA ++ clauseB)
    //    /** negate all literals **/
    //    def neg() =
    //        if (isEmpty || isFull) this
    //        else new NF(clauses.map(_.neg))
    /**empty means true for CNF, false for DNF **/
    def isEmpty = !isFull && clauses.isEmpty
    def isAtomic = clauses.size == 1 && clauses.head.isAtomic
    override def toString = if (isEmpty) "EMPTY" else if (isFull) "FULL" else clauses.mkString("*")
    def printCNF = if (isEmpty) "1" else if (isFull) "0" else clauses.map(_.printCNF).mkString("&&")
    override def hashCode = clauses.hashCode
    override def equals(that: Any) = that match {
        case thatNF: NF => this.clauses equals thatNF.clauses; case _ => false
    }
    /**returns a set with all referenced macros (DefinedMacro)**/
    def findMacros(): Set[DefinedMacro] = {
        var result: Set[DefinedMacro] = Set()
        clauses.foreach(clause => {
            result = result ++ clause.findMacros
        })
        result
    }
    /**expensive operation, do not call on large NFs
     * feeding the formula unmodified into a SAT solver is usually faster*/
    def simplify: NF = new NF(clauses.distinct)
}

/**clause in a normal form **/
class Clause(var posLiterals: Seq[DefinedExpr], var negLiterals: Seq[DefinedExpr]) {
    var cacheIsSimplified = false
    def simplify = {
        if (!cacheIsSimplified) {
            //A || !A = true 
            posLiterals = posLiterals.distinct.sortWith((a, b) => a.feature > b.feature)
            negLiterals = negLiterals.distinct.sortWith((a, b) => a.feature > b.feature)
            if (!(posLiterals intersect negLiterals).isEmpty) {
                posLiterals = SmallList()
                negLiterals = SmallList()
            }
            cacheIsSimplified = true
        }
        this
    }
    def isEmpty = posLiterals.isEmpty && negLiterals.isEmpty
    /**join two clauses **/
    def ++(that: Clause) = new Clause(this.posLiterals ++ that.posLiterals, this.negLiterals ++ that.negLiterals).simplify
    def neg() = new Clause(this.negLiterals, this.posLiterals)
    def size = posLiterals.size + negLiterals.size
    override def toString =
        (posLiterals.map(_.satName) ++ negLiterals.map("!" + _.satName)).mkString("(", "*", ")")
    def printCNF =
        (posLiterals.map(_.print) ++ negLiterals.map(Not(_).print)).mkString("(", "||", ")")
    override def hashCode = {
        simplify;
        posLiterals.hashCode + negLiterals.hashCode
    }
    override def equals(that: Any) = that match {
        case thatClause: Clause => (this.simplify.posLiterals equals thatClause.simplify.posLiterals) && (this.simplify.negLiterals equals thatClause.simplify.negLiterals)
        case _ => false
    }
    /**returns a set with all referenced macros (DefinedMacro)**/
    def findMacros(): Set[DefinedMacro] = {
        var result: Set[DefinedMacro] = Set()
        ((posLiterals.toList) ++ (negLiterals.toList)).foreach(
            _ match {
                case x: DefinedMacro => result = result + x
                case DefinedExternal(_) =>
            })
        result
    }
    def substitute(f: DefinedExpr => DefinedExpr) = {
        var changed = false
        def checkChange(oldVal: DefinedExpr): DefinedExpr = {
            val newVal = f(oldVal)
            if (!(newVal eq oldVal)) changed = true
            newVal
        }
        val newPosLit = this.posLiterals.map(checkChange(_))
        val newNegLit = this.negLiterals.map(checkChange(_))
        if (changed)
            new Clause(newPosLit, newNegLit)
        else
            this
    }
    def isAtomic: Boolean = {
        if (size != 1) false
        else if (posLiterals.size == 1) posLiterals.head.isExternal
        else
            negLiterals.head.isExternal
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
    def toCNF_(exprInCNF: FeatureExprTree): Option[NF] = try {
        Some(toCNF(exprInCNF))
    } catch {
        case e: NFException => None
    }
    def toDNF_(exprInDNF: FeatureExprTree): Option[NF] = try {
        Some(toDNF(exprInDNF))
    } catch {
        case e: NFException => None
    }
    def toCNF(exprInCNF: FeatureExprTree): NF = toNF(exprInCNF, true)
    def toDNF(exprInDNF: FeatureExprTree): NF = toNF(exprInDNF, false)
    private def toNF(exprInNF: FeatureExprTree, isCNF: Boolean) =
        try {
            exprInNF simplify match {
                case And(clauses) if isCNF => {
                    new NF((for (clause <- clauses) yield clause match {
                        case Or(o) => toClause(o)
                        case e => toClause(SmallList(e)) //literal?
                    }))
                }
                case Or(clauses) if !isCNF => {
                    new NF((for (clause <- clauses) yield clause match {
                        case And(c) => toClause(c)
                        case e => toClause(SmallList(e)) //literal?
                    }))
                }
                case Or(o) if isCNF => new NF(SmallList(toClause(o)))
                case And(o) if !isCNF => new NF(SmallList(toClause(o)))
                case f@DefinedExpr(_) => new NF(SmallList(new Clause(SmallList(f), SmallList())))
                case Not(f@DefinedExpr(_)) => new NF(SmallList(new Clause(SmallList(), SmallList(f))))
                case BaseFeature() => new NF(!isCNF)
                case DeadFeature() => new NF(isCNF)
                case e => throw new NoNFException(e, exprInNF, isCNF)
            }
        } catch {
            case t: Throwable =>
                System.err.println("Exception on NormalForm.toNF for: " + exprInNF.print())
                t.printStackTrace
                throw t

        }
    private def toClause(literals: Seq[FeatureExprTree]): Clause = {
        var posLiterals: Seq[DefinedExpr] = SmallList()
        var negLiterals: Seq[DefinedExpr] = SmallList()
        for (literal <- literals)
            literal match {
                case f@DefinedExpr(_) => posLiterals = f +: posLiterals
                case Not(f@DefinedExpr(_)) => negLiterals = f +: negLiterals
                case e => throw new NoLiteralException(e)
            }
        new Clause(posLiterals, negLiterals).simplify
    }

    def CNFtoFeatureExpr(cnf: NF): FeatureExprTree =
        if (cnf.isEmpty) BaseFeature()
        else if (cnf.isFull) DeadFeature()
        else new And(
            for (clause <- cnf.clauses) yield new Or(clause.posLiterals ++ clause.negLiterals.map(Not(_)))
        )
}

object SmallList {
    def apply[T](e: T*): Seq[T] = {
        val v = new ArrayBuffer[T](e.length)
        v ++= e
        v
    }
}