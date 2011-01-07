package de.fosd.typechef.featureexpr

import scala.collection.mutable.ArrayBuffer

/**
 * classes used to represent CNF and DNF expressions
 *
 * (note, this does not include the process of translation from arbitrary
 * expressions to CNF, it is just for representing expressions that are already
 * in normal form)
 */
//
//
///**normal form for both DNF and CNF **/
//class NF(val clauses: Traversable[Clause], val isFull: Boolean) {
//    /**isFull is meant to be the opposite of empty
//     * with CNF empty means always true and full means always false
//     * with DNF empty means always false and full means always true
//     * it is not valid to set clauses and isFull at the same time  */
//
//    def this(c: Traversable[Clause]) = this (c.filter(!_.isEmpty), false)
//    def this(emptyOrFull_isFull: Boolean) = this (SmallList(), emptyOrFull_isFull)
//
//    //    /** join (CNF and CNF / DNF or DNF)**/
//    //    def ++(that: NF) =
//    //        if (this.isFull || that.isFull) new NF(true) else new NF(this.clauses ++ that.clauses)
//    //    /** explode (CNF or CNF / DNF and DNF)**/
//    //    def **(that: NF) =
//    //        if (this.isFull) that
//    //        else if (that.isFull) this
//    //        else new NF(for (clauseA <- this.clauses; clauseB <- that.clauses) yield clauseA ++ clauseB)
//    //    /** negate all literals **/
//    //    def neg() =
//    //        if (isEmpty || isFull) this
//    //        else new NF(clauses.map(_.neg))
//    /**empty means true for CNF, false for DNF **/
//    def isEmpty = !isFull && clauses.isEmpty
//    def isAtomic = clauses.size == 1 && clauses.head.isAtomic
//    override def toString = printCNF
//    //if (isEmpty) "EMPTY" else if (isFull) "FULL" else clauses.mkString("*")
//    def printCNF = if (isEmpty) "1" else if (isFull) "0" else clauses.map(_.printCNF).mkString("&")
//    override def hashCode = clauses.hashCode
//    override def equals(that: Any) = that match {
//        case thatNF: NF => this.clauses equals thatNF.clauses; case _ => false
//    }
//    /**returns a set with all referenced macros (DefinedMacro)**/
//    def findMacros(): Set[DefinedMacro] = {
//        var result: Set[DefinedMacro] = Set()
//        clauses.foreach(clause => {
//            result = result ++ clause.findMacros
//        })
//        result
//    }
//    /**expensive operation, do not call on large NFs
//     * feeding the formula unmodified into a SAT solver is usually faster*/
//    def simplify: NF = new NF(clauses)
//}
//
///**clause in a normal form **/
//class Clause(var posLiterals: Seq[DefinedExpr], var negLiterals: Seq[DefinedExpr]) {
//    //    var cacheIsSimplified = false
//    //    def simplify = {
//    //        if (!cacheIsSimplified) {
//    //            //A || !A = true
//    //            posLiterals = posLiterals.distinct.sortWith((a, b) => a.feature > b.feature)
//    //            negLiterals = negLiterals.distinct.sortWith((a, b) => a.feature > b.feature)
//    //            if (!(posLiterals intersect negLiterals).isEmpty) {
//    //                posLiterals = SmallList()
//    //                negLiterals = SmallList()
//    //            }
//    //            cacheIsSimplified = true
//    //        }
//    //        this
//    //    }
//    def isEmpty = posLiterals.isEmpty && negLiterals.isEmpty
//    /**join two clauses **/
//    def ++(that: Clause) = new Clause(this.posLiterals ++ that.posLiterals, this.negLiterals ++ that.negLiterals)
//    def neg() = new Clause(this.negLiterals, this.posLiterals)
//    def size = posLiterals.size + negLiterals.size
//    override def toString =
//        (posLiterals.map(_.satName) ++ negLiterals.map("!" + _.satName)).mkString("(", "*", ")")
//    def printCNF =
//        (posLiterals.map(_.print) ++ negLiterals.map(_.not.print)).mkString("(", "|", ")")
//    override def hashCode = {
//        //        simplify;
//        posLiterals.hashCode + negLiterals.hashCode
//    }
//    override def equals(that: Any) = that match {
//        case thatClause: Clause => (this.posLiterals equals thatClause.posLiterals) && (this.negLiterals equals thatClause.negLiterals)
//        case _ => false
//    }
//    /**returns a set with all referenced macros (DefinedMacro)**/
//    def findMacros(): Set[DefinedMacro] = {
//        var result: Set[DefinedMacro] = Set()
//        ((posLiterals.toList) ++ (negLiterals.toList)).foreach(
//            _ match {
//                case x: DefinedMacro => result = result + x
//                case x: DefinedExternal =>
//            })
//        result
//    }
//    def substitute(f: DefinedExpr => DefinedExpr) = {
//        var changed = false
//        def checkChange(oldVal: DefinedExpr): DefinedExpr = {
//            val newVal = f(oldVal)
//            if (!(newVal eq oldVal)) changed = true
//            newVal
//        }
//        val newPosLit = this.posLiterals.map(checkChange(_))
//        val newNegLit = this.negLiterals.map(checkChange(_))
//        if (changed)
//            new Clause(newPosLit, newNegLit)
//        else
//            this
//    }
//    def isAtomic: Boolean = {
//        if (size != 1) false
//        else if (posLiterals.size == 1) posLiterals.head.isExternal
//        else
//            negLiterals.head.isExternal
//    }
//}

/**
 * NFBuilder builds normal form classes from expressions that are already in normal form
 *
 * NFBuilder will not turn arbitrary expressions into normal forms! Use 
 * FeatureExprTree.toCNF before. Throws an exception when applied to
 * a non-NF formula 
 */
object CNFHelper {
    //    def toCNF(exprInCNF: FeatureExpr): NF = toNF(exprInCNF, true)
    //    def toDNF(exprInDNF: FeatureExpr): NF = toNF(exprInDNF, false)
    //    private def toNF(exprInNF: FeatureExpr, isCNF: Boolean) =
    //        try {
    //            exprInNF match {
    //                case And(clauses) if isCNF => {
    //                    new NF((for (clause <- clauses) yield clause match {
    //                        case Or(o) => toClause(o)
    //                        case e => toClause(SmallList(e)) //literal?
    //                    }))
    //                }
    //                case Or(clauses) if !isCNF => {
    //                    new NF((for (clause <- clauses) yield clause match {
    //                        case And(c) => toClause(c)
    //                        case e => toClause(SmallList(e)) //literal?
    //                    }))
    //                }
    //                case Or(o) if isCNF => new NF(SmallList(toClause(o)))
    //                case And(o) if !isCNF => new NF(SmallList(toClause(o)))
    //                case f@DefinedExpr(_) => new NF(SmallList(new Clause(SmallList(f), SmallList())))
    //                case Not(f@DefinedExpr(_)) => new NF(SmallList(new Clause(SmallList(), SmallList(f))))
    //                case True => new NF(!isCNF)
    //                case False => new NF(isCNF)
    //                case e => throw new NoNFException(e, exprInNF, isCNF)
    //            }
    //        } catch {
    //            case t: Throwable =>
    //                System.err.println("Exception on NormalForm.toNF for: " + exprInNF.print())
    //                t.printStackTrace
    //                throw t
    //
    //        }
    //    private def toClause(literals: TraversableOnce[FeatureExpr]): Clause = {
    //        var posLiterals: Seq[DefinedExpr] = SmallList()
    //        var negLiterals: Seq[DefinedExpr] = SmallList()
    //        for (literal <- literals)
    //            literal match {
    //                case f@DefinedExpr(_) => posLiterals = f +: posLiterals
    //                case Not(f@DefinedExpr(_)) => negLiterals = f +: negLiterals
    //                case e => throw new NoLiteralException(e)
    //            }
    //        new Clause(posLiterals, negLiterals)
    //    }

    //for testing
    def isCNF(expr: FeatureExpr) = isTrueFalse(expr) || isClause(expr) || (expr match {
        case And(clauses) => clauses.forall(isClause(_))
        case e => false
    })
    def isClauseOrTF(expr: FeatureExpr) = isTrueFalse(expr) || isClause(expr)
    def isClause(expr: FeatureExpr) = isLiteral(expr) || (expr match {
        case Or(literals) => literals.forall(isLiteral(_))
        case _ => false
    })
    def isLiteral(expr: FeatureExpr) = expr match {
        case x: DefinedExpr => true
        case Not(DefinedExpr(_)) => true
        case _ => false
    }
    def isLiteralExternal(expr: FeatureExpr) = expr match {
        case x: DefinedExternal => true
        case Not(x: DefinedExternal) => true
        case _ => false
    }
    def isTrueFalse(expr: FeatureExpr) = expr match {
        case True => true
        case False => true
        case _ => false
    }

    def getCNFClauses(cnfExpr: FeatureExpr): Traversable[FeatureExpr /*Clause*/ ] = cnfExpr match {
        case And(clauses) => clauses
        case e => Set(e)
    }

    def getLiterals(orClause: FeatureExpr): Traversable[FeatureExpr /*Literal*/ ] = orClause match {
        case Or(literals) => literals
        case e => Set(e)
    }

    def getDefinedExprs(orClause: FeatureExpr): Set[DefinedExpr] = orClause match {
        case Or(literals) => literals.map(getDefinedExpr(_)).foldLeft[Set[DefinedExpr]](Set())(_ + _)
        case e => Set(getDefinedExpr(e))
    }
    def getDefinedExpr(literal: FeatureExpr): DefinedExpr = literal match {
        case x: DefinedExpr => x
        case Not(x: DefinedExpr) => x
        case _ => throw new NoLiteralException(literal)
    }
}

//    def CNFtoFeatureExpr(cnf: NF): FeatureExpr =
//        if (cnf.isEmpty) BaseFeature()
//        else if (cnf.isFull) DeadFeature()
//        else new And(
//            for (clause <- cnf.clauses) yield new Or(clause.posLiterals ++ clause.negLiterals.map(Not(_)))
//        )


object SmallList {
    def apply[T](e: T*): Seq[T] = {
        val v = new ArrayBuffer[T](e.length)
        v ++= e
        v
    }
}