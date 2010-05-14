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
  var simplifiedExprCache:FeatureExpr = null;
  def simplify():FeatureExpr = { 
    if (simplifiedExprCache==null)
    	simplifiedExprCache = this match {
		    case And(children) => {
		      val childrenSimplified = children.map(_.simplify()) - BaseFeature();
		      var childrenFlattened:Set[FeatureExpr] = Set()
		      for (childs<-childrenSimplified)
		    	  	childs match {
		    	  	  case And(innerChildren) => childrenFlattened = childrenFlattened ++ innerChildren
		    	  	  case e => childrenFlattened = childrenFlattened + e
		    	  	}
		      for (childs<-childrenFlattened)
		    	  	if(childrenFlattened.exists(_==Not(childs)))
		    	  		return DeadFeature();
		      if (childrenFlattened.exists(_==DeadFeature())) return DeadFeature()
		      if (childrenFlattened.size==1) return (childrenFlattened.elements).next()
		      if (childrenFlattened.size==0) return BaseFeature()
		      return And(childrenFlattened)
		    }
		    case Or(children) => {
		      val childrenSimplified =  children.map(_.simplify()) - DeadFeature();
		      var childrenFlattened:Set[FeatureExpr] = Set()
		      for (childs<-childrenSimplified)
		    	  	childs match {
		    	  	  case Or(innerChildren) => childrenFlattened = childrenFlattened ++ innerChildren
		    	  	  case e => childrenFlattened = childrenFlattened + e
		    	  	}
		      for (childs<-childrenFlattened)
		    	  	if(childrenFlattened.exists(_==Not(childs)))
		    	  		return BaseFeature();
		      if (childrenFlattened.exists(_==BaseFeature())) return BaseFeature()
		      if (childrenFlattened.size==1) return (childrenFlattened.elements).next()
		      if (childrenFlattened.size==0) return DeadFeature()
		      return Or(childrenFlattened)
		    }
		    case BinaryFeatureExpr(left, right, "==",_) if left.simplify() == right.simplify() => right simplify
		    case BinaryFeatureExpr(IntegerLit(a),IntegerLit(b), "<",_) => if (a<b) BaseFeature() else DeadFeature()
		    case BinaryFeatureExpr(DeadFeature(),IntegerLit(b), "<",_) => if (0<b) BaseFeature() else DeadFeature()
		    case BinaryFeatureExpr(DeadFeature(),IntegerLit(b), ">=",_) => if (0>=b) BaseFeature() else DeadFeature()
		    case BinaryFeatureExpr(DeadFeature(),IntegerLit(b), "!=",_) => if (0!=b) BaseFeature() else DeadFeature()
		    case BinaryFeatureExpr(DeadFeature(),IntegerLit(b), "==",_) => if (0==b) BaseFeature() else DeadFeature()
		    case BinaryFeatureExpr(left, right, opStr, op) => BinaryFeatureExpr(left simplify, right simplify, opStr, op)
		    case UnaryFeatureExpr(expr, opStr, op) => UnaryFeatureExpr(expr simplify, opStr, op)
		    case Not(a)  => 
		      a.simplify match {
		      	case DeadFeature() => BaseFeature()
		      	case BaseFeature() => DeadFeature()
		      	case Not(e) => e
		      	case e=>Not(e)
		      }
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
  def isDead():Boolean = 
    this==DeadFeature() || new SatSolver().isContradiction(this); 
  def isBase():Boolean = 
    this==BaseFeature() || new SatSolver().isTautology(this); 
  def accept(f:FeatureExpr=>Unit):Unit;
  
  def toCNF():FeatureExpr =
    this.simplify match {
      case Not(And(children)) => Or(children.map(Not(_).toCNF())).toCNF()
      case Not(Or(children)) => And(children.map(Not(_).toCNF())).toCNF()
      case And(children) => And(children.map(_.toCNF)).simplify
      case Or(children) => {
        val cnfchildren=children.map(_.toCNF)
        if (cnfchildren.exists(_.isInstanceOf[And])) {
	        var orClauses:Set[Or] = Set(Or(Set()))//list of Or expressions
	        for (val child<-cnfchildren) {
	          child match {
	            case And(innerChildren) => {
	              var newClauses:Set[Or] = Set()
	              for (val innerChild<-innerChildren)
	                newClauses = newClauses ++ orClauses.map(_.addChild(innerChild));
	              orClauses=newClauses;
	            }
	            case _ => orClauses = orClauses.map(_.addChild(child));
	          }
	        }
	        And(orClauses.map(a=>a)).simplify
        } else Or(cnfchildren)
      }
      case e => e
    }
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
  def accept(f:FeatureExpr=>Unit):Unit = {
    f(this)
    left.accept(f)
    right.accept(f)
  }
}
abstract class AbstractNaryBinaryFeatureExpr(
  children:Set[FeatureExpr], 
  opStr:String, 
  op:(Boolean,Boolean)=>Boolean
) extends FeatureExpr {
  def print() = children.mkString("("," "+ opStr +" ",")")
  def accept(f:FeatureExpr=>Unit):Unit = {
    f(this)
    for (child<-children)child.accept(f)
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
  def accept(f:FeatureExpr=>Unit):Unit = {
    f(this)
    expr.accept(f)
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
  def accept(f:FeatureExpr=>Unit):Unit = f(this)
}

case class CharacterLit(char:Char) extends FeatureExpr {
  def print():String = "'"+char.toString+"'";
  //def eval(context:FeatureProvider):Long = char.toLong;
  def calcPossibleValues():Set[Long] = Set(char.toLong)
  def accept(f:FeatureExpr=>Unit):Unit = f(this)
}
case class IntegerLit(num:Long) extends FeatureExpr {
  def print():String = num.toString;
  //def eval(context:FeatureProvider):Long = num;
  def calcPossibleValues():Set[Long] = Set(num)
  def accept(f:FeatureExpr=>Unit):Unit = f(this)
}

case class DeadFeature() extends FeatureExpr {
  def print() = "DEAD"
  //def eval(context:FeatureProvider) = 0;
  def calcPossibleValues():Set[Long] = Set(0)
  def accept(f:FeatureExpr=>Unit):Unit = f(this)
}
case class BaseFeature() extends FeatureExpr {
  def print() = "BASE"
  //def eval(context:FeatureProvider) = 1;
  def calcPossibleValues():Set[Long] = Set(1)
  def accept(f:FeatureExpr=>Unit):Unit = f(this)
}

case class IfExpr(condition:FeatureExpr, thenBranch:FeatureExpr, elseBranch: FeatureExpr) extends FeatureExpr {
	def calcPossibleValues() = if (condition.isBase()) thenBranch.possibleValues()
                        else if (condition.isDead()) elseBranch.possibleValues()
                        else thenBranch.possibleValues() ++ elseBranch.possibleValues()
    def print():String = "__IF__("+condition+","+thenBranch+","+elseBranch+")";
    def accept(f:FeatureExpr=>Unit):Unit = { f(this); condition.accept(f);thenBranch.accept(f);elseBranch.accept(f) }
}
 
case class Not(expr:FeatureExpr)extends AbstractUnaryBoolFeatureExpr(expr, "!", !_);
case class And(children:Set[FeatureExpr]) extends AbstractNaryBinaryFeatureExpr(children, "&&", _ && _) {
  def this(left:FeatureExpr,right:FeatureExpr) = this(Set(left,right))
  def calcPossibleValues():Set[Long] = {
    var result:Set[Long]=Set()
    if (children.exists(_.calcPossibleValues().exists(_==0))) result+=0
    if (children.forall(_.calcPossibleValues().exists(_==1))) result+=1
    result
  }
}
case class Or(children:Set[FeatureExpr]) extends AbstractNaryBinaryFeatureExpr(children, "||", _ || _){
  def this(left:FeatureExpr,right:FeatureExpr) = this(Set(left,right))
  def calcPossibleValues():Set[Long] = {
    var result:Set[Long]=Set()
    if (children.exists(_.calcPossibleValues().exists(_==1))) result+=1
    if (children.forall(_.calcPossibleValues().exists(_==0))) result+=0
    result
  }
  def addChild(child:FeatureExpr) = Or(children+child);
}

case class UnaryFeatureExpr(expr:FeatureExpr, opStr:String, op:(Long)=>Long) extends AbstractUnaryFeatureExpr(expr, opStr, op)
case class BinaryFeatureExpr(left:FeatureExpr, right:FeatureExpr, opStr:String, op:(Long, Long)=>Long) extends AbstractBinaryFeatureExpr(left, right, opStr, op)


