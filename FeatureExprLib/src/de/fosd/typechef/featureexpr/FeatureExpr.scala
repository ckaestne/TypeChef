package de.fosd.typechef.featureexpr


object FeatureExpr {
  def createDefined(feature:String, context:FeatureProvider):FeatureExpr = 
    context.getMacroCondition(feature) simplify
  
  def createComplement(expr:FeatureExpr) = UnaryFeatureExpr(expr, "~", ~_)
  def createNeg(expr:FeatureExpr) = UnaryFeatureExpr(expr, "-", -_)
  def createBitAnd(left:FeatureExpr, right:FeatureExpr) = BinaryFeatureExpr(left, right, "&", _ & _)
  def createBitOr(left:FeatureExpr, right:FeatureExpr) = BinaryFeatureExpr(left, right, "|", _ | _)
  def createDivision(left:FeatureExpr, right:FeatureExpr) = BinaryFeatureExpr(left, right, "/", _ / _)
  def createModulo(left:FeatureExpr, right:FeatureExpr) = BinaryFeatureExpr(left, right, "%", _ % _)
  def createEquals(left:FeatureExpr, right:FeatureExpr) = BinaryFeatureExpr(left, right, "==", (a,b) => if (a==b) 1 else 0)
  def createNotEquals(left:FeatureExpr, right:FeatureExpr) = BinaryFeatureExpr(left, right, "!=", (a,b) => if (a!=b) 1 else 0)
  def createLessThan(left:FeatureExpr, right:FeatureExpr) = BinaryFeatureExpr(left, right,  "<", (a,b) => if (a<b) 1 else 0)
  def createLessThanEquals(left:FeatureExpr, right:FeatureExpr) = BinaryFeatureExpr(left, right, "<=", (a,b) => if (a<=b) 1 else 0)
  def createGreaterThan(left:FeatureExpr, right:FeatureExpr) = BinaryFeatureExpr(left, right, ">", (a,b) => if (a>b) 1 else 0)
  def createGreaterThanEquals(left:FeatureExpr, right:FeatureExpr) = BinaryFeatureExpr(left, right, ">=", (a,b) => if (a>=b) 1 else 0)
  def createMinus(left:FeatureExpr, right:FeatureExpr) = BinaryFeatureExpr(left, right, "-", _ - _)
  def createMult(left:FeatureExpr, right:FeatureExpr) = BinaryFeatureExpr(left, right, "*", _ * _)
  def createPlus(left:FeatureExpr, right:FeatureExpr) = BinaryFeatureExpr(left, right, "+", _ + _)
  def createPwr(left:FeatureExpr, right:FeatureExpr) = BinaryFeatureExpr(left, right,  "^", _ ^ _)
  def createShiftLeft(left:FeatureExpr, right:FeatureExpr) = BinaryFeatureExpr(left, right, "<<", _ << _)
  def createShiftRight(left:FeatureExpr, right:FeatureExpr) = BinaryFeatureExpr(left, right, ">>", _ >> _)
}

sealed abstract class FeatureExpr {
  //def eval(context:FeatureProvider):Long
  //TODO simplify works only at toplevel constructs
  var simplifiedExprCache:FeatureExpr = null;
  def simplify():FeatureExpr = { 
    if (simplifiedExprCache==null)
    	simplifiedExprCache = this match {
    case And(a,b) => {
      val as=a simplify;
      val bs=b simplify;
      if (as==DeadFeature() || bs==DeadFeature()) DeadFeature()
      else if (as == BaseFeature()) bs
      else if (bs == BaseFeature()) as
      else if (as == bs) as
      else if (as == Not(bs) || Not(as) == bs) DeadFeature()
      else And(as, bs)
    }
    case Or(a,b) => {
      val as=a simplify;
      val bs=b simplify;
      if (as==BaseFeature() || bs==BaseFeature()) BaseFeature()
      else if (as == DeadFeature()) bs
      else if (bs == DeadFeature()) as
      else if (as == bs) as
      else if (as == Not(bs) || Not(as) == bs) BaseFeature()
      else Or(as, bs)
    }    
    //case Or(Defined(a),Not(Defined(a))) => BaseFeature()
    case Not(DeadFeature()) => BaseFeature()
    case Not(BaseFeature()) => DeadFeature()
    case Not(Not(e)) => e simplify
    case BinaryFeatureExpr(left, right, "==",_) if left.simplify() == right.simplify() => right simplify
    case BinaryFeatureExpr(left, right, opStr, op) => BinaryFeatureExpr(left simplify, right simplify, opStr, op)
    case UnaryFeatureExpr(expr, opStr, op) => UnaryFeatureExpr(expr simplify, opStr, op)
    case Not(a)  => Not(a simplify)
    case IfExpr(c,a,b) => {
      val as=a simplify;
      val bs=b simplify;
      val cs=c simplify;
      if (cs.isBase()) as
      else if (cs.isDead()) bs
      else if (as==bs) as
      else IfExpr(c simplify, a simplify, b simplify)
    }
    case IntegerLit(1) => BaseFeature() 
    case IntegerLit(0) => DeadFeature()
    case IntegerLit(_) => this 
    case CharacterLit(_) => this
    case DefinedExternal(_) => this
    case DeadFeature() => this
    case BaseFeature() => this
  }
    simplifiedExprCache
  }
  def print():String
  override def toString():String = print
  var possibleValuesCache:Set[Long] = null
  def possibleValues():Set[Long] = {
    if (possibleValuesCache==null)
      possibleValuesCache = calcPossibleValues();
    possibleValuesCache
  }
  def calcPossibleValues():Set[Long]
  def isDead():Boolean = possibleValues()==Set(0)
  def isBase():Boolean = possibleValues()==Set(1)
}
abstract class AbstractBinaryFeatureExpr(
  left:FeatureExpr, 
  right:FeatureExpr, 
  opStr:String, 
  op:(Long,Long)=>Long
) extends FeatureExpr {

  //def eval(context:FeatureProvider) = op(left.eval(context), right.eval(context))
  def print() = "("+left.print + " "+ opStr +" "+right.print+")"
  def calcPossibleValues():Set[Long] = {
    var result=Set[Long]()
    for (
    	a<-left.possibleValues();
        b<-right.possibleValues()
    ) result += op(a, b)
    result
  }
}

abstract class AbstractBinaryBoolFeatureExpr( 
  left:FeatureExpr, 
  right:FeatureExpr, 
  opStr:String, 
  op:(Boolean,Boolean)=>Boolean
) extends AbstractBinaryFeatureExpr(left,right,opStr, (a,b) => if (op(a!=0,b!=0)) 1 else 0)
abstract class AbstractBinaryCompFeatureExpr( 
  left:FeatureExpr, 
  right:FeatureExpr, 
  opStr:String, 
  op:(Long,Long)=>Boolean
) extends AbstractBinaryFeatureExpr(left,right,opStr, (a,b) => if (op(a,b)) 1 else 0)

abstract class AbstractUnaryFeatureExpr(
  expr:FeatureExpr, 
  opStr:String, 
  op:(Long)=>Long
) extends FeatureExpr {
  //def eval(context:FeatureProvider) = op(expr.eval(context))
  def print() = opStr +"("+expr.print+")"
  def calcPossibleValues():Set[Long] = {
    var result=Set[Long]()
    for (
    	a<-expr.possibleValues()
    ) result += op(a)
    result
  }
}
abstract class AbstractUnaryBoolFeatureExpr(
  expr:FeatureExpr, 
  opStr:String, 
  op:(Boolean)=>Boolean
) extends AbstractUnaryFeatureExpr(expr,opStr, (ev) => if (op(ev!=0)) 1 else 0);


/** external definion of a feature (cannot be decided to Base or Dead inside this file) */
case class DefinedExternal(feature:String)extends FeatureExpr {
  def print():String = "defined("+feature+")";
  def calcPossibleValues():Set[Long] = Set(0,1)
}

case class CharacterLit(char:Char) extends FeatureExpr {
  def print():String = "'"+char.toString+"'";
  //def eval(context:FeatureProvider):Long = char.toLong;
  def calcPossibleValues():Set[Long] = Set(char.toLong)
}
case class IntegerLit(num:Long) extends FeatureExpr {
  def print():String = num.toString;
  //def eval(context:FeatureProvider):Long = num;
  def calcPossibleValues():Set[Long] = Set(num)
}

case class DeadFeature() extends FeatureExpr {
  def print() = "DEAD"
  //def eval(context:FeatureProvider) = 0;
  def calcPossibleValues():Set[Long] = Set(0)
}
case class BaseFeature() extends FeatureExpr {
  def print() = "BASE"
  //def eval(context:FeatureProvider) = 1;
  def calcPossibleValues():Set[Long] = Set(1)
}

case class IfExpr(condition:FeatureExpr, thenBranch:FeatureExpr, elseBranch: FeatureExpr) extends FeatureExpr {
	def calcPossibleValues() = if (condition.isBase()) thenBranch.possibleValues()
                        else if (condition.isDead()) elseBranch.possibleValues()
                        else thenBranch.possibleValues() ++ elseBranch.possibleValues()
    def print():String = "__IF__("+condition+","+thenBranch+","+elseBranch+")";
}
 
case class Not(expr:FeatureExpr)extends AbstractUnaryBoolFeatureExpr(expr, "!", !_);
//case class Complement(expr:FeatureExpr)extends UnaryFeatureExpr(expr, "~", ~_);
//case class Neg(expr:FeatureExpr)extends UnaryFeatureExpr(expr, "-", -_);
case class And(left:FeatureExpr, right:FeatureExpr) extends AbstractBinaryBoolFeatureExpr(left, right, "&&", _ && _)
case class Or(left:FeatureExpr, right:FeatureExpr) extends AbstractBinaryBoolFeatureExpr(left, right, "||", _ || _)
case class UnaryFeatureExpr(expr:FeatureExpr, opStr:String, op:(Long)=>Long) extends AbstractUnaryFeatureExpr(expr, opStr, op)
case class BinaryFeatureExpr(left:FeatureExpr, right:FeatureExpr, opStr:String, op:(Long, Long)=>Long) extends AbstractBinaryFeatureExpr(left, right, opStr, op)
//case class BitAnd(left:FeatureExpr, right:FeatureExpr) extends BinaryFeatureExpr(left, right, "&", _ & _)
//case class BitOr(left:FeatureExpr, right:FeatureExpr) extends BinaryFeatureExpr(left, right, "|", _ | _)
//case class Division(left:FeatureExpr, right:FeatureExpr) extends BinaryFeatureExpr(left, right, "/", _ / _)
//case class Equals(left:FeatureExpr, right:FeatureExpr) extends BinaryCompFeatureExpr(left, right, "==", _ == _)
//case class NotEquals(left:FeatureExpr, right:FeatureExpr) extends BinaryCompFeatureExpr(left, right, "!=", _ != _)
//case class LessThan(left:FeatureExpr, right:FeatureExpr) extends BinaryCompFeatureExpr(left, right, "==", _ < _)
//case class LessThanEquals(left:FeatureExpr, right:FeatureExpr) extends BinaryCompFeatureExpr(left, right, "==", _ <= _)
//case class GreaterThan(left:FeatureExpr, right:FeatureExpr) extends BinaryCompFeatureExpr(left, right, "==", _ > _)
//case class GreaterThanEquals(left:FeatureExpr, right:FeatureExpr) extends BinaryCompFeatureExpr(left, right, "==", _ >= _)
//case class Minus(left:FeatureExpr, right:FeatureExpr) extends BinaryFeatureExpr(left, right, "-", _ - _)
//case class Mult(left:FeatureExpr, right:FeatureExpr) extends BinaryFeatureExpr(left, right, "*", _ * _)
//case class Plus(left:FeatureExpr, right:FeatureExpr) extends BinaryFeatureExpr(left, right, "+", _ + _)
//case class Pwr(left:FeatureExpr, right:FeatureExpr) extends BinaryFeatureExpr(left, right, "^", _ ^ _)
//case class ShiftLeft(left:FeatureExpr, right:FeatureExpr) extends BinaryFeatureExpr(left, right, "<<", _ << _)
//case class ShiftRight(left:FeatureExpr, right:FeatureExpr) extends BinaryFeatureExpr(left, right, ">>", _ >> _)


