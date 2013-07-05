package de.fosd.typechef.featureexpr

import scala.Predef._


trait FeatureExprTreeFactory extends FeatureExprValueOps {
    def createComplement(expr: FeatureExprValue): FeatureExprValue = applyUnaryOperation(expr)(~_)
    def createNeg(expr: FeatureExprValue) = applyUnaryOperation(expr)(-_)
    def createBitAnd(left: FeatureExprValue, right: FeatureExprValue) = applyBinaryOperation(left, right)(_ & _)
    def createBitOr(left: FeatureExprValue, right: FeatureExprValue) = applyBinaryOperation(left, right)(_ | _)
    def createDivision(left: FeatureExprValue, right: FeatureExprValue): FeatureExprValue = applyBinaryOperation(left, right)(
        (l, r) => if (r == 0) ErrorValue[Long]("division by zero") else createValue(l / r))
    def createModulo(left: FeatureExprValue, right: FeatureExprValue) = applyBinaryOperation(left, right)(_ % _)
    def createEquals(left: FeatureExprValue, right: FeatureExprValue) = evalRelation(left, right)(_ == _)
    def createNotEquals(left: FeatureExprValue, right: FeatureExprValue) = evalRelation(left, right)(_ != _)
    def createLessThan(left: FeatureExprValue, right: FeatureExprValue) = evalRelation(left, right)(_ < _)
    def createLessThanEquals(left: FeatureExprValue, right: FeatureExprValue) = evalRelation(left, right)(_ <= _)
    def createGreaterThan(left: FeatureExprValue, right: FeatureExprValue) = evalRelation(left, right)(_ > _)
    def createGreaterThanEquals(left: FeatureExprValue, right: FeatureExprValue) = evalRelation(left, right)(_ >= _)
    def createMinus(left: FeatureExprValue, right: FeatureExprValue) = applyBinaryOperation(left, right)(_ - _)
    def createMult(left: FeatureExprValue, right: FeatureExprValue) = applyBinaryOperation(left, right)(_ * _)
    def createPlus(left: FeatureExprValue, right: FeatureExprValue) = applyBinaryOperation(left, right)(_ + _)
    def createPwr(left: FeatureExprValue, right: FeatureExprValue) = applyBinaryOperation(left, right)(_ ^ _)
    def createShiftLeft(left: FeatureExprValue, right: FeatureExprValue) = applyBinaryOperation(left, right)(_ << _)
    def createShiftRight(left: FeatureExprValue, right: FeatureExprValue) = applyBinaryOperation(left, right)(_ >> _)

    def createInteger(value: Long): FeatureExprValue = createValue(value)
    def createCharacter(value: Char): FeatureExprValue = createValue(value)

    def createBooleanIf(expr: FeatureExpr, thenBr: FeatureExpr, elseBr: FeatureExpr): FeatureExpr = (expr and thenBr) or (expr.not and elseBr)

    //provided externally
    def True: FeatureExpr
    def False: FeatureExpr
    //    def createIf[T](condition: FeatureExpr, thenBranch: FeatureExprTree[T], elseBranch: FeatureExprTree[T]): FeatureExprTree[T] = createIf(condition, thenBranch, elseBranch)


    /**
     * Central builder class, responsible for simplification of expressions during creation
     * and for extensive caching.
     */

    private def propagateError[T](left: FeatureExprTree[T], right: FeatureExprTree[T]): Option[ErrorValue[T]] = {
        (left, right) match {
            case (msg1: ErrorValue[_], msg2: ErrorValue[_]) => Some(ErrorValue(msg1.msg + ";" + msg2.msg))
            case (msg: ErrorValue[_], _) => Some(ErrorValue(msg.msg))
            case (_, msg: ErrorValue[_]) => Some(ErrorValue(msg.msg))
            case _ => None
        }
    }

    def evalRelation[T](smaller: FeatureExprTree[T], larger: FeatureExprTree[T])(relation: (T, T) => Boolean): FeatureExpr = {
        propagateError(smaller, larger) match {
            case Some(ErrorValue(msg)) => return new ErrorFeature(msg, False)
            case _ =>
                (smaller, larger) match {
                    case (a: Value[_], b: Value[_]) => if (relation(a.value.asInstanceOf[T], b.value.asInstanceOf[T])) True else False
                    case (i1: If[_], i2: If[_]) =>
                        createBooleanIf(i1.expr,
                            createBooleanIf(i2.expr, evalRelation(i1.thenBr, i2.thenBr)(relation), evalRelation(i1.thenBr, i2.elseBr)(relation)),
                            createBooleanIf(i2.expr, evalRelation(i1.elseBr, i2.thenBr)(relation), evalRelation(i1.elseBr, i2.elseBr)(relation)))
                    case (i: If[_], x) => createBooleanIf(i.expr, evalRelation(i.thenBr, x)(relation), evalRelation(i.elseBr, x)(relation))
                    case (x, i: If[_]) => createBooleanIf(i.expr, evalRelation(x, i.thenBr)(relation), evalRelation(x, i.elseBr)(relation))
                    case _ => throw new Exception("evalRelation: unexpected " +(smaller, larger))
                }
        }
    }

    def applyBinaryOperation[T, U <% FeatureExprTree[T]](left: FeatureExprTree[T], right: FeatureExprTree[T])(operation: (T, T) => U): FeatureExprTree[T] = {
        propagateError(left, right) match {
            case Some(err) => return err
            case _ =>
                (left, right) match {
                    case (a: Value[_], b: Value[_]) => try {
                        operation(a.value.asInstanceOf[T], b.value.asInstanceOf[T])
                    } catch {
                        case e: ArithmeticException => System.err.println("ArithmeticException evaluating " + a.value + " op " + b.value); throw e
                    }
                    case (i1: If[_], i2: If[_]) =>
                        createIf[T](i1.expr,
                            createIf[T](i2.expr, applyBinaryOperation(i1.thenBr.asInstanceOf[FeatureExprTree[T]], i2.thenBr.asInstanceOf[FeatureExprTree[T]])(operation), applyBinaryOperation(i1.thenBr.asInstanceOf[FeatureExprTree[T]], i2.elseBr.asInstanceOf[FeatureExprTree[T]])(operation)),
                            createIf[T](i2.expr, applyBinaryOperation(i1.elseBr.asInstanceOf[FeatureExprTree[T]], i2.thenBr.asInstanceOf[FeatureExprTree[T]])(operation), applyBinaryOperation(i1.elseBr.asInstanceOf[FeatureExprTree[T]], i2.elseBr.asInstanceOf[FeatureExprTree[T]])(operation)))
                    case (i: If[_], x) => createIf(i.expr, applyBinaryOperation(i.thenBr.asInstanceOf[FeatureExprTree[T]], x)(operation), applyBinaryOperation(i.elseBr.asInstanceOf[FeatureExprTree[T]], x)(operation))
                    case (x, i: If[_]) => createIf(i.expr, applyBinaryOperation(x, i.thenBr.asInstanceOf[FeatureExprTree[T]])(operation), applyBinaryOperation(x, i.elseBr.asInstanceOf[FeatureExprTree[T]])(operation))
                    case _ => throw new Exception("applyBinaryOperation: unexpected " +(left, right))
                }
        }
    }

    def applyUnaryOperation[T](expr: FeatureExprTree[T])(operation: T => T): FeatureExprTree[T] = expr match {
        case a: Value[_] => createValue(operation(a.value.asInstanceOf[T]))
        case i: If[_] => createIf(i.expr, applyUnaryOperation(i.thenBr.asInstanceOf[FeatureExprTree[T]])(operation), applyUnaryOperation(i.elseBr.asInstanceOf[FeatureExprTree[T]])(operation))
        case _ => throw new Exception("applyUnaryOperation: unexpected " + expr)
    }

    def createIf[T](expr: FeatureExpr, thenBr: FeatureExprTree[T], elseBr: FeatureExprTree[T]): FeatureExprTree[T] =
        if (expr.isTautology()) thenBr
        else if (expr.isContradiction()) elseBr
        else if (thenBr == elseBr) thenBr
        else new If(expr, thenBr, elseBr)

    def createValue[T](v: T): FeatureExprTree[T] = new Value[T](v)

}