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

  def createImplies(left:FeatureExpr, right:FeatureExpr) = new Or(Not(left),right)
  
  private var freshFeatureNameCounter=0 
  def calcFreshFeatureName():String = {freshFeatureNameCounter=freshFeatureNameCounter+1; "__fresh"+freshFeatureNameCounter;}
}

sealed abstract class FeatureExpr {
  //optimization to not simplify the same expression over and over again
  private var cache_simplifiedExpr:FeatureExpr = null;
  private var isSimplified:Boolean = false
  private def setSimplified():FeatureExpr = { isSimplified=true; return this }
  def simplify():FeatureExpr = { 
    if (isSimplified) return this
    if (cache_simplifiedExpr==null) {
    	cache_simplifiedExpr = this match {
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
//    		case Or(And(a,Not(b)),c) if b==c => new Or(a,b)
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
		    case BinaryFeatureExpr(IfExpr(c,a,b), right, opStr, op) =>
		      IfExpr(c,BinaryFeatureExpr(a, right, opStr, op),BinaryFeatureExpr(b, right, opStr, op)).simplify
		    case BinaryFeatureExpr(left, right, "==",_) if left.simplify() == right.simplify() => BaseFeature()
		    case BinaryFeatureExpr(IntegerLit(a),IntegerLit(b), _,op) => IntegerLit(op(a,b))
		    case BinaryFeatureExpr(left, right, opStr, op) => BinaryFeatureExpr(left simplify, right simplify, opStr, op)
		    case UnaryFeatureExpr(expr, opStr, op) => UnaryFeatureExpr(expr simplify, opStr, op)
		    case Not(a)  => 
			      a.simplify match {
				      	case IntegerLit(v) => if (v==0) BaseFeature() else DeadFeature()
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
		    case IntegerLit(_) => this 
		    case CharacterLit(_) => this
		    case DefinedExternal(_) => this
    	}
    	cache_simplifiedExpr = cache_simplifiedExpr.setSimplified
    }
    return cache_simplifiedExpr
  }
  def print():String
  override def toString():String = print
  var possibleValuesCache:Set[Long] = null
//  def possibleValues():Set[Long] = {
//    if (possibleValuesCache==null)
//      possibleValuesCache = calcPossibleValues();
//    possibleValuesCache
//  }
//  def calcPossibleValues():Set[Long]
  var cached_isDead:Option[Boolean] = None
  var cached_isBase:Option[Boolean] = None
  /**
   * checks whether the formula is a contradiction
   * @return
   */
  def isDead():Boolean = {
	  if (cached_isBase == Some(true)) return false
	  if (cached_isDead.isEmpty)
		  cached_isDead=Some(this==DeadFeature() || new SatSolver().isContradiction(this))
	  if (cached_isDead.get)
	 	  cache_simplifiedExpr=DeadFeature();
      cached_isDead.get
  }
  /**
   * checks whether the formula is a tautology
   * @return
   */
  def isBase():Boolean = {
	  if (cached_isDead == Some(true)) return false
	  if (cached_isBase.isEmpty)
	 	  cached_isBase = Some(this==BaseFeature() || new SatSolver().isTautology(this))
	  if (cached_isBase.get)
	 	  cache_simplifiedExpr=BaseFeature();
	  cached_isBase.get
  }
    
  def accept(f:FeatureExpr=>Unit):Unit;
  
//  def toCNF():FeatureExpr =
//    this.simplify match {
//      case IfExpr(c,a,b) => new Or(new And(c,a),new And(Not(c),b)).toCNF()
//      case Not(And(children)) => Or(children.map(Not(_).toCNF())).toCNF()
//      case Not(Or(children)) => And(children.map(Not(_).toCNF())).toCNF()
//      case And(children) => And(children.map(_.toCNF)).simplify
//      case Or(children) => {
//        val cnfchildren=children.map(_.toCNF)
//        if (cnfchildren.exists(_.isInstanceOf[And])) {
//	        var orClauses:Set[Or] = Set(Or(Set()))//list of Or expressions
//	        for (val child<-cnfchildren) {
//	          child match {
//	            case And(innerChildren) => {
//	              var newClauses:Set[Or] = Set()
//	              for (val innerChild<-innerChildren)
//	                newClauses = newClauses ++ orClauses.map(_.addChild(innerChild));
//	              orClauses=newClauses;
//	            }
//	            case _ => orClauses = orClauses.map(_.addChild(child));
//	          }
//	        }
//	        And(orClauses.map(a=>a)).simplify
//        } else Or(cnfchildren)
//      }
//      case e => e
//    }  
  
  def toCnfEquiSat():FeatureExpr =
    this.simplify match {
      case IfExpr(c,a,b) => new Or(new And(c,a),new And(Not(c),b)).toCnfEquiSat()
      case Not(And(children)) => Or(children.map(Not(_).toCnfEquiSat())).toCnfEquiSat()
      case Not(Or(children)) => And(children.map(Not(_).toCnfEquiSat())).simplify
      case Not(e:IfExpr) => Not(e.toCnfEquiSat()).simplify.toCnfEquiSat()
      case And(children) => And(children.map(_.toCnfEquiSat)).simplify
      case Or(children) => {
        val cnfchildren=children.map(_.toCnfEquiSat)
        if (cnfchildren.exists(_.isInstanceOf[And])) {
	        var orClauses:Set[FeatureExpr] = Set()//list of Or expressions
//	        val freshFeatureNames:Set[FeatureExpr]=for (child<-children) yield DefinedExternal(freshFeatureName())
         
	        var freshFeatureNames:Set[FeatureExpr]=Set()
        	for (val child<-cnfchildren) {
        	  val freshFeatureName = Not(DefinedExternal(FeatureExpr.calcFreshFeatureName()))
	          child match {
	            case And(innerChildren) => {
	              for (innerChild<-innerChildren)
	            	  orClauses += new Or(freshFeatureName,innerChild);
	            }
	            case e => orClauses += new Or(freshFeatureName,e);
	          }
        	  freshFeatureNames += Not(freshFeatureName).simplify
	        }
        	orClauses += Or(freshFeatureNames)
	        And(orClauses).simplify
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
//  def calcPossibleValues():Set[Long] = {
//    var result=Set[Long]()
//    for (
//    	a<-left.possibleValues();
//        b<-right.possibleValues()
//    ) result += op(a, b)
//    result
//  }
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
//  def calcPossibleValues():Set[Long] = {
//    var result=Set[Long]()
//    for (
//    	a<-expr.possibleValues()
//    ) result += op(a)
//    result
//  }
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
  def print():String = {
    assert(feature!="") 
    "defined("+feature+")";
  }
  def accept(f:FeatureExpr=>Unit):Unit = f(this)
}

case class CharacterLit(char:Int) extends FeatureExpr {
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

case class DeadFeature() extends IntegerLit(0)

case class BaseFeature() extends IntegerLit(1)


case class IfExpr(condition:FeatureExpr, thenBranch:FeatureExpr, elseBranch: FeatureExpr) extends FeatureExpr {
//	def calcPossibleValues() = if (condition.isBase()) thenBranch.possibleValues()
//                        else if (condition.isDead()) elseBranch.possibleValues()
//                        else thenBranch.possibleValues() ++ elseBranch.possibleValues()
    def print():String = "__IF__("+condition+","+thenBranch+","+elseBranch+")";
    def accept(f:FeatureExpr=>Unit):Unit = { f(this); condition.accept(f);thenBranch.accept(f);elseBranch.accept(f) }
}
 
case class Not(expr:FeatureExpr)extends AbstractUnaryBoolFeatureExpr(expr, "!", !_);
case class And(children:Set[FeatureExpr]) extends AbstractNaryBinaryFeatureExpr(children, "&&", _ && _) {
  def this(left:FeatureExpr,right:FeatureExpr) = this(Set(left,right))
//  def calcPossibleValues():Set[Long] = {
//    var result:Set[Long]=Set()
//    if (children.exists(_.calcPossibleValues().exists(_==0))) result+=0
//    if (children.forall(_.calcPossibleValues().exists(_==1))) result+=1
//    result
//  }
}
case class Or(children:Set[FeatureExpr]) extends AbstractNaryBinaryFeatureExpr(children, "||", _ || _){
  def this(left:FeatureExpr,right:FeatureExpr) = this(Set(left,right))
//  def calcPossibleValues():Set[Long] = {
//    var result:Set[Long]=Set()
//    if (children.exists(_.calcPossibleValues().exists(_==1))) result+=1
//    if (children.forall(_.calcPossibleValues().exists(_==0))) result+=0
//    result
//  }
  def addChild(child:FeatureExpr) = Or(children+child);
}

case class UnaryFeatureExpr(expr:FeatureExpr, opStr:String, op:(Long)=>Long) extends AbstractUnaryFeatureExpr(expr, opStr, op)
case class BinaryFeatureExpr(left:FeatureExpr, right:FeatureExpr, opStr:String, op:(Long, Long)=>Long) extends AbstractBinaryFeatureExpr(left, right, opStr, op)


