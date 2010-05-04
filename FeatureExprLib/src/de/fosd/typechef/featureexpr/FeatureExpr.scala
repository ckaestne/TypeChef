package de.fosd.typechef.featureexpr



sealed abstract class FeatureExpr {
  def eval(context:FeatureProvider):Long
  //TODO simplify works only at toplevel constructs
  def simplify():FeatureExpr = this match {
    case And(BaseFeature(),e) => e simplify
    case And(e,BaseFeature()) => e simplify
    case And(DeadFeature(),_) => DeadFeature()
    case And(_,DeadFeature()) => DeadFeature()
    case And(a,b) => And(a simplify, b simplify)
    case Or(DeadFeature(),e) => e simplify
    case Or(e,DeadFeature()) => e simplify
    case Or(BaseFeature(),_) => BaseFeature()
    case Or(_,BaseFeature()) => BaseFeature()
    case Or(a,b) => Or(a simplify, b simplify)
    case Not(Not(e)) => e simplify
    case Neg(Neg(e)) => e simplify
    case _ => this
  }
  def print():String
  override def toString():String = print
}
abstract class BinaryFeatureExpr(
  left:FeatureExpr, 
  right:FeatureExpr, 
  opStr:String, 
  op:(Long,Long)=>Long
) extends FeatureExpr {

  def eval(context:FeatureProvider) = op(left.eval(context), right.eval(context))
  def print() = left.print + " "+ opStr +" "+right.print
//  override def simplify = super.simplify() 
}

abstract class BinaryBoolFeatureExpr( 
  left:FeatureExpr, 
  right:FeatureExpr, 
  opStr:String, 
  op:(Boolean,Boolean)=>Boolean
) extends BinaryFeatureExpr(left,right,opStr, (a,b) => if (op(a!=0,b!=0)) 1 else 0)
abstract class BinaryCompFeatureExpr( 
  left:FeatureExpr, 
  right:FeatureExpr, 
  opStr:String, 
  op:(Long,Long)=>Boolean
) extends BinaryFeatureExpr(left,right,opStr, (a,b) => if (op(a,b)) 1 else 0)

abstract class UnaryFeatureExpr(
  expr:FeatureExpr, 
  opStr:String, 
  op:(Long)=>Long
) extends FeatureExpr {
  def eval(context:FeatureProvider) = op(expr.eval(context))
  def print() = opStr +expr.print
}
abstract class UnaryBoolFeatureExpr(
  expr:FeatureExpr, 
  opStr:String, 
  op:(Boolean)=>Boolean
) extends FeatureExpr {
  def eval(context:FeatureProvider) = if (op(expr.eval(context)!=0)) 1 else 0
  def print() = opStr +expr.print
}

case class Defined(feature:String)extends FeatureExpr {
  def print():String = "defined("+feature+")";
  def eval(context:FeatureProvider):Long = if (context isFeatureDefined feature) 1 else 0;
}

case class CharacterLit(char:Char) extends FeatureExpr {
  def print():String = "'"+char.toString+"'";
  def eval(context:FeatureProvider):Long = char.toLong;
}
case class IntegerLit(num:Long) extends FeatureExpr {
  def print():String = num.toString;
  def eval(context:FeatureProvider):Long = num;
}

case class DeadFeature() extends FeatureExpr {
  def print() = "DEAD"
  def eval(context:FeatureProvider) = 0;
}
case class BaseFeature() extends FeatureExpr {
  def print() = "BASE"
  def eval(context:FeatureProvider) = 1;
}

case class Not(expr:FeatureExpr)extends UnaryBoolFeatureExpr(expr, "!", !_);
case class Complement(expr:FeatureExpr)extends UnaryFeatureExpr(expr, "~", ~_);
case class Neg(expr:FeatureExpr)extends UnaryFeatureExpr(expr, "-", -_);
case class And(left:FeatureExpr, right:FeatureExpr) extends BinaryBoolFeatureExpr(left, right, "&&", _ && _)
case class Or(left:FeatureExpr, right:FeatureExpr) extends BinaryBoolFeatureExpr(left, right, "||", _ || _)
case class BitAnd(left:FeatureExpr, right:FeatureExpr) extends BinaryFeatureExpr(left, right, "&", _ & _)
case class BitOr(left:FeatureExpr, right:FeatureExpr) extends BinaryFeatureExpr(left, right, "|", _ | _)
case class Division(left:FeatureExpr, right:FeatureExpr) extends BinaryFeatureExpr(left, right, "/", _ / _)
case class Equals(left:FeatureExpr, right:FeatureExpr) extends BinaryCompFeatureExpr(left, right, "==", _ == _)
case class NotEquals(left:FeatureExpr, right:FeatureExpr) extends BinaryCompFeatureExpr(left, right, "!=", _ != _)
case class LessThan(left:FeatureExpr, right:FeatureExpr) extends BinaryCompFeatureExpr(left, right, "==", _ < _)
case class LessThanEquals(left:FeatureExpr, right:FeatureExpr) extends BinaryCompFeatureExpr(left, right, "==", _ <= _)
case class GreaterThan(left:FeatureExpr, right:FeatureExpr) extends BinaryCompFeatureExpr(left, right, "==", _ > _)
case class GreaterThanEquals(left:FeatureExpr, right:FeatureExpr) extends BinaryCompFeatureExpr(left, right, "==", _ >= _)
case class Minus(left:FeatureExpr, right:FeatureExpr) extends BinaryFeatureExpr(left, right, "-", _ - _)
case class Mult(left:FeatureExpr, right:FeatureExpr) extends BinaryFeatureExpr(left, right, "*", _ * _)
case class Plus(left:FeatureExpr, right:FeatureExpr) extends BinaryFeatureExpr(left, right, "+", _ + _)
case class Pwr(left:FeatureExpr, right:FeatureExpr) extends BinaryFeatureExpr(left, right, "^", _ ^ _)
case class ShiftLeft(left:FeatureExpr, right:FeatureExpr) extends BinaryFeatureExpr(left, right, "<<", _ << _)
case class ShiftRight(left:FeatureExpr, right:FeatureExpr) extends BinaryFeatureExpr(left, right, ">>", _ >> _)


