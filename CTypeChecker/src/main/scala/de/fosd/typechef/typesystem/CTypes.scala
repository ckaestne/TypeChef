package de.fosd.typechef.typesystem

import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.parser.c.AST

/**
  * basic types of C and definitions which types are compatible
  *
  * extended by alternative types with variability: CChoice
  */

/**
  * Missing from used formalization:
  *
  * We omit the following features of C's type system: enumeration types,
  * the type qualifiers const and volatile, bit-elds, and union types. Furthermore
  * we do not allow functions to take variable numbers of arguments,
  * and we also gloss over the typedef construct, assuming that this last facility
  * is compiled out in such a way that occurrences of type identifiers are
  * replaced with the type that they abbreviate.
  *
  * Our treatment of functions and array types as function parameters is
  * different from that in the standard. In the case of functions, the standard
  * makes use of what it terms pointers to functions. These are essentially
  * variables that can contain references to functions, where possible values
  * are all of the program's dened functions. This is how we shall treat func
  * tion references henceforth, stripping them of the confusing semantic baggage
  * associated with pointers. (The language in the standard is continually
  * having to make exceptions for pointers to functions in its description of
  * operations on pointers. It is not possible to perform pointer arithmetic on
  * function references, and dereferencing of function pointers is an idempotent
  * operation.) This clarity of exposition will also be evident in the
  * discussion of the dynamic semantics. We will discuss the nature of array
  * types, and how our denition differs from that given in the standard in
  * section 2.3 below.
  */

case class CType(
                    val atype: AType,
                    val isObject: Boolean,
                    val isVolatile: Boolean,
                    val isConstant: Boolean) {

    private def copy(atype: AType = this.atype, isObject: Boolean = this.isObject, isVolatile: Boolean = this.isVolatile, isConstant: Boolean = this.isConstant) = new CType(atype, isObject, isVolatile, isConstant)

    //convert from object to value (lvalue to rvalue) if applicable; if already a type return the type
    def toValue: CType = if (!isObject) this
    else {
        val newatype = atype match {
            case CArray(g, _) if isObject => CPointer(g)
            case _ => atype
        }
        copy(atype = newatype, isObject = false)
    }

    //special version for the linker, where toValue does not normalize Arrays to Pointers! (see CERT ARR31-C)
    def toValueLinker = if (!isObject) this else copy(isObject = false)

    def toObj: CType = if (isObject) this else copy(isObject = true)

    def toVolatile(newVal: Boolean = true): CType = copy(isVolatile = newVal)

    def toConst(newVal: Boolean = true): CType = copy(isConstant = newVal)

    def isFunction: Boolean = atype.isFunction

    def isUnknown: Boolean = atype.isUnknown

    def isIgnore: Boolean = atype.isIgnore

    def toText: String =
        (if (isObject) "obj " else "") +
            (if (isVolatile) "volatile " else "") +
            (if (isConstant) "const " else "") +
            atype.toText

    def <(that: CType): Boolean = this.atype < that.atype

    def map(f: AType => AType) = copy(atype = f(this.atype))

    /**
      * by default equals between two CTypes will only compare their ATypes and const, object and volatile modifier
      *
      * (changed Nov 20 2015 from equalsAType behavior)
      */
    override def equals(that: Any) = that match {
        case thattype: CType =>
            (this.isUnknown && thattype.isUnknown) || equalsCType(thattype)
        case thattype: AType => throw new RuntimeException("comparison between CType and AType")
        case _ => super.equals(that)
    }

    /**
      * equals between two CTypes comparing only their ATypes (including the ATypes of nested CTypes)
      *
      * that is, this equality checking does not care about const, volatile, and object. to check equality
      * of two CTypes including these parameters use equalsCType
      */
    def equalsAType(thattype: CType) =
            (this.isUnknown && thattype.isUnknown) ||
                (this.atype == thattype.atype)

    /**
      * note, this check currently only checks the top-level CTypes for equality, but uses the AType
      * equality for nested structures in CAnonymousStruct and CFunction
      */
    def equalsCType(that: CType) =
        this.atype == that.atype &&
            this.isObject == that.isObject &&
            this.isConstant == that.isConstant &&
            this.isVolatile == that.isVolatile

    /**
      * compares ATypes and const flag only
      */
    def equalsWithConst(that: CType) =
        this.atype == that.atype &&
            this.isConstant == that.isConstant

  /**
      * compares ATypes and const and volatile flag only
      */
    def equalsWithConstAndVolatile(that: CType) =
        this.atype == that.atype &&
            this.isConstant == that.isConstant &&
            this.isVolatile == that.isVolatile


    override def hashCode() = atype.hashCode()

    def toXML: xml.Elem = {
        var result = atype.toXML
        //opposite order from parsing!
        if (isConstant) result = <const>
            {result}
        </const>
        if (isVolatile) result = <volatile>
            {result}
        </volatile>
        if (isObject) result = <obj>
            {result}
        </obj>
        result
    }

}

import de.fosd.typechef.typesystem.CType.makeCType

sealed abstract class CBasicType {
    def <(that: CBasicType): Boolean

    def toXML: xml.Elem

    def toText: String //for debug purposes
}


sealed abstract class AType {
    def isFunction: Boolean = false


    /* map over this type considering variability */
    def mapV(f: FeatureExpr, op: (FeatureExpr, AType) => AType): AType = op(f, this)

    def map(op: AType => AType): AType = op(this)

    def isUnknown: Boolean = false

    def isIgnore: Boolean = false

    /** compares with of two types. if this<that, this type can be converted (widened) to that */
    def <(that: AType): Boolean = false

    def toXML: xml.Elem

    def toText: String = toString //for debug purposes

    def toCType = new CType(this, false, false, false)

    //    override def equals(that: Any) = that match {
    //        case thattype: CType => throw new RuntimeException("comparison between AType and CType")
    //        case _ => super.equals(that)
    //    }
}

//    /** type without variability */
//    sealed abstract class CStaticType extends CType


case class CChar() extends CBasicType {
    def <(that: CBasicType) = that == CLongLong() || that == CInt() || that == CLong() || that == CInt128()

    override def toText = "char"

    def toXML = <char/>
}

case class CShort() extends CBasicType {
    def <(that: CBasicType) = that == CLongLong() || that == CInt() || that == CLong() || that == CInt128()

    override def toText = "short"

    def toXML = <short/>
}

case class CInt() extends CBasicType {
    def <(that: CBasicType) = that == CLongLong() || that == CLong() || that == CInt128()

    override def toText = "int"

    def toXML = <int/>
}

case class CLong() extends CBasicType {
    def <(that: CBasicType) = that == CLongLong() || that == CInt128()

    override def toText = "long"

    def toXML = <long/>
}

case class CLongLong() extends CBasicType {
    def <(that: CBasicType) = that == CInt128()

    override def toText = "long long"

    def toXML = <longlong/>
}

case class CInt128() extends CBasicType {
    def <(that: CBasicType) = false

    override def toText = "__int128"

    def toXML = <int128/>
}

case class CVoid() extends AType {
    override def toText = "void"

    def toXML = <void/>
}

/**
  * zero is a special type for the constant 0
  * that is all: a function and an integer and a pointer
  */
case class CZero() extends AType {
    override def toText = "void"

    def toXML = <zero/>
}

abstract class CSignSpecifier(val basicType: CBasicType) extends AType

case class CSigned(b: CBasicType) extends CSignSpecifier(b) {
    override def <(that: AType) = that match {
        case CSigned(thatb) => b < thatb
        case _ => false
    }

    override def toText = "signed " + b.toText

    def toXML = <signed>
        {b.toXML}
    </signed>
}

case class CUnsigned(b: CBasicType) extends CSignSpecifier(b) {
    override def <(that: AType) = that match {
        case CUnsigned(thatb) => b < thatb
        case _ => false
    }

    override def toText = "unsigned " + b.toText

    def toXML = <unsigned>
        {b.toXML}
    </unsigned>
}

case class CSignUnspecified(b: CBasicType) extends CSignSpecifier(b) {
    override def <(that: AType) = that match {
        case CSignUnspecified(thatb) => b < thatb
        case _ => false
    }

    override def toText = b.toText

    def toXML = <nosign>
        {b.toXML}
    </nosign>
}

case class CBool() extends AType {
    override def toText = "_Bool"

    def toXML = <_Bool/>
}

//implementationspecific for Char

case class CFloat() extends AType {
    override def toText = "float"

    def toXML = <float/>
}

case class CDouble() extends AType {
    override def toText = "double"

    def toXML = <double/>
}

case class CLongDouble() extends AType {
    override def toText = "long double"

    def toXML = <longdouble/>
}

case class CPointer(t: AType) extends AType {
    override def toText = t.toText + "*"

    def toXML = <pointer>
        {t.toXML}
    </pointer>
}

//length is currently not analyzed. using always -1
case class CArray(t: AType, length: Int = -1) extends AType {
    override def toText = t.toText + "[]"

    def toXML = <array length={length.toString}>
        {t.toXML}
    </array>
}

/** struct and union are handled in the same construct but distinguished with a flag
  *
  * struct types have only a name. to decide whether a type is a complete type, we need
  * an environment (a complete type has known content, an incomplete type only has a name)
  * */
case class CStruct(s: String, isUnion: Boolean = false) extends AType {
    override def toText = (if (isUnion) "union " else "struct ") + s

    def toXML = <struct isUnion={isUnion.toString}>
        {s}
    </struct>
}

object AnonymousStructUniqueIdGen {
    var id=0
    def gen = { id+=1; id }
}

case class CAnonymousStruct(uniqueId:Int, fields: ConditionalTypeMap, isUnion: Boolean = false) extends AType {
    override def toText = (if (isUnion) "union " else "struct ") + "{" + fields + "}"

    def toXML = <astruct isUnion={isUnion.toString}>
        {}
    </astruct>
}

object CFunction {
    def apply(param: Seq[CType], ret: CType) = new CFunction(normalizeFunctionParameters(param), ret)

    def unapply[B](value: CFunction): Option[(Seq[CType], CType)] = value match {
        case f: CFunction => Some((f.param, f.ret))
        case _ => None
    }

    /**
      * normalize CFunction types on creation
      *
      * following ISO C standard  ISO/IEC 9899:1999 Chapter 6.7.5.3
      * -> unnamed void parameter as only parameter => empty parameter list (handled elsewhere)
      * -> translate array parameter into pointer
      * -> translate function parameter into pointer to function
      */
    def normalizeFunctionParameters(param: Seq[CType]): Seq[CType] =
            param.map(_.map({
                case CArray(t, l) => CPointer(t)
                case f: CFunction => CPointer(f)
                case e => e
            }))

}

class CFunction(val param: Seq[CType], val ret: CType) extends AType {
    var securityRelevant: Boolean = false

    override def isFunction: Boolean = true

    override def toText = param.map(_.toText).mkString("(", ", ", ")") + " => " + ret.toText

    def toXML = <function>
        {param.map(x => <param>
            {x.toXML}
        </param>)}<ret>
            {ret.toXML}
        </ret>
    </function>

    def markSecurityRelevant() = {
        securityRelevant = true;
        this
    }

    override def hashCode() = param.hashCode() * ret.hashCode()

    override def equals(that: Any) = that match {
        case thatf: CFunction => (this.param.length==thatf.param.length) && this.param.zip(thatf.param).forall(x => x._1 equalsAType x._2) && (this.ret equalsAType thatf.ret)
        case _ => super.equals(that)
    }

    override def toString() = "CFunction(" + param + "," + ret + ")"
}


//varargs should only occur in paramter lists
case class CVarArgs() extends AType {
    override def toText = "..."

    def toXML = <vargs/>
}

object CObj {
    //shorthand for pattern matching with CType(x, true, _, _)
    def unapply(x: CType): Option[AType] = if (x.isObject) Some(x.atype) else None
}

/**
  * CCompound is a workaround for initializers.
  * This type is created by initializers with any structure.
  * We currently don't care about internals.
  * CCompound can be cast into any array or structure
  */
case class CCompound() extends AType {
    def toXML = <compound/>
}

/**
  * CIgnore is a type for stuff we currently do not want to check
  * it can be cast to anything and is not considered an error
  */
case class CIgnore() extends AType {
    def toXML = <ignore/>

    override def isIgnore = true
}


case class CBuiltinVaList() extends AType {
    def toXML = <builtinvalist/>
}

/** errors */
case class CUnknown(msg: String = "") extends AType {
    override def equals(that: Any) = that match {
        case CUnknown(_) => true
        case _ => super.equals(that)
    }

    override def isUnknown: Boolean = true

    def toXML = <unknown msg={msg}/>
}

object CUndefined extends CUnknown("unknown")


/**
  * xml reader
  */
object CType {


    def fromXML(anode: scala.xml.NodeSeq): CType = {
        var node = anode
        var isVolatile = false
        var isConst = false
        var isObject = false
        (node \ "obj").map(x => {
            isObject = true; node = x
        })
        (node \ "volatile").map(x => {
            isVolatile = true; node = x
        })
        (node \ "const").map(x => {
            isConst = true; node = x
        })
        new CType(fromXMLAType(node), isObject, isVolatile, isConst)
    }

    def fromXMLAType(node: scala.xml.NodeSeq): AType = {
        var result: AType = CUnknown("fromXML error: " + node.text)
        (node \ "signed").map(x => result = CSigned(fromXMLBasicType(x)))
        (node \ "unsigned").map(x => result = CUnsigned(fromXMLBasicType(x)))
        (node \ "nosign").map(x => result = CSignUnspecified(fromXMLBasicType(x)))
        (node \ "zero").map(x => result = CZero())
        (node \ "void").map(x => result = CVoid())
        (node \ "_Bool").map(x => result = CBool())
        (node \ "float").map(x => result = CFloat())
        (node \ "double").map(x => result = CDouble())
        (node \ "longdouble").map(x => result = CLongDouble())
        (node \ "vargs").map(x => result = CVarArgs())
        (node \ "pointer").map(x => result = CPointer(fromXMLAType(x)))
        (node \ "array").map(x => result = CArray(fromXMLAType(x), x.attribute("length").get.head.text.toInt))
        (node \ "struct").map(x => result = CStruct(x.text.trim, x.attribute("isUnion").get.head.text.toBoolean))
        (node \ "astruct").map(x => result = CAnonymousStruct(AnonymousStructUniqueIdGen.gen, new ConditionalTypeMap(), x.attribute("isUnion").get.head.text.toBoolean)) //TODO
        (node \ "function").map(x => result = CFunction(
            (x \ "param").map(fromXML(_)),
            fromXML((x \ "ret").head)
        ))
        (node \ "compound").map(x => result = CCompound())
        (node \ "builtinvalist").map(x => result = CBuiltinVaList())
        (node \ "unkown").map(x => result = CUnknown(x.attribute("msg").get.text))
        (node \ "ignore").map(x => result = CIgnore())
        result
    }


    def fromXMLBasicType(node: scala.xml.Node): CBasicType = {
        var result: CBasicType = CInt()
        (node \ "char").map(x => result = CChar())
        (node \ "short").map(x => result = CShort())
        (node \ "long").map(x => result = CLong())
        (node \ "longlong").map(x => result = CLongLong())
        (node \ "int128").map(x => result = CInt128())
        result
    }

    implicit def makeCType(x: AType): CType = new CType(x, false, false, false)


    /**
      * checks whether two (function) types are compatbile and can be linked together
      */
    def isLinkCompatible(a: CType, b: CType): Boolean = {
        a.atype == b.atype
    }

}

//assumed well-formed pointer targets on structures


/**
  * maintains a map from names to types
  * a name may be mapped to alternative types with different feature expressions
  *
  * internally storing Type, whether its a definition (as opposed to a declaration), and the current scope idx
  */
class ConditionalTypeMap(m: ConditionalMap[String, (AST, Conditional[CType])])
    extends ConditionalCMap[CType](m) {
    def this() = this(new ConditionalMap())

    def apply(name: String): Conditional[CType] = getOrElse(name, CUnknown(name))

    def ++(that: ConditionalTypeMap) = if (that.isEmpty) this else new ConditionalTypeMap(this.m ++ that.m)

    def ++(l: Seq[(String, FeatureExpr, (AST, Conditional[CType]))]) = if (l.isEmpty) this else new ConditionalTypeMap(m ++ l)

    def +(name: String, f: FeatureExpr, a: AST, t: Conditional[CType]) = new ConditionalTypeMap(m.+(name, f, (a, t)))

    def and(f: FeatureExpr) = new ConditionalTypeMap(m.and(f))
}

/**
  * storing the following information per variable:
  *
  * * name
  * * AST -> declaring AST element, for debugging purposes and giving error messages with locations
  * * CType -> type
  * * DeclarationKind -> declaration, definition, enum, or parameter
  * * Int -> Scope (0=top level, 1 = function, ...)
  * * Linkage (isInternal) -> internal/external
  */

class ConditionalVarEnv(m: ConditionalMap[String, (AST, Conditional[(CType, DeclarationKind, Int, Linkage)])])
    extends ConditionalCMap[(CType, DeclarationKind, Int, Linkage)](m) {
    def this() = this(new ConditionalMap())

    def apply(name: String): Conditional[CType] = lookupType(name)

    def lookup(name: String): Conditional[(CType, DeclarationKind, Int, Linkage)] = getOrElse(name, (CUnknown(name), KDeclaration, -1, NoLinkage))

    def lookupType(name: String): Conditional[CType] = lookup(name).map(_._1)

    def lookupKind(name: String): Conditional[DeclarationKind] = lookup(name).map(_._2)

    def lookupScope(name: String): Conditional[Int] = lookup(name).map(_._3)

    def lookupIsInternalLinkage(name: String): FeatureExpr = (lookup(name).map(_._4 == InternalLinkage).when(identity))

    def lookupIsExternalLinkage(name: String): FeatureExpr = (lookup(name).map(_._4 == ExternalLinkage).when(identity))

    def +(name: String, f: FeatureExpr, a: AST, t: Conditional[CType], kind: DeclarationKind, scope: Int, linkage: Linkage) = new ConditionalVarEnv(m.+(name, f, (a, t.map(x => (x, kind, scope, linkage)))))

    def +(name: String, f: FeatureExpr, a: AST, t: Conditional[CType], kind: DeclarationKind, scope: Int, linkage: Conditional[Linkage]) = new ConditionalVarEnv(m.+(name, f, (a, ConditionalLib.mapCombination(t, linkage, (x: CType, l: Linkage) => (x, kind, scope, l)))))

    def ++(v: Seq[(String, FeatureExpr, AST, Conditional[CType], DeclarationKind, Int, Linkage)]) =
        v.foldLeft(this)((c, x) => c.+(x._1, x._2, x._3, x._4, x._5, x._6, x._7))
}

/**
  * map from names to ASTs with their conditional types (or other conditional information)
  *
  * the normal lookup functions apply and getOrElse will only return the conditional T, not the AST node (legacy reasons)
  * too get the corresponding AST elements call the respective functions (getASTOrElse, ..)
  *
  */
abstract class ConditionalCMap[T](protected val m: ConditionalMap[String, (AST, Conditional[T])]) {
    /**
      * apply returns a type, possibly CUndefined or a
      * choice type
      *
      * returns only the type information, not the ast
      */
    def getOrElse(name: String, errorType: T): Conditional[T] = ConditionalLib.combine(getFullOrElse(name, (null, One(errorType))).map(_._2))

    def getAstOrElse(name: String, errorNode: AST): Conditional[AST] = getFullOrElse(name, (errorNode, null)).map(_._1)

    def getFullOrElse(name: String, errorNode: (AST, Conditional[T])): Conditional[(AST, Conditional[T])] = m.getOrElse(name, errorNode)

    def contains(name: String) = m.contains(name)

    def isEmpty = m.isEmpty

    def allTypes: Iterable[Conditional[T]] = m.allEntriesFlat.map(_._2)

    //warning: do not use, probably not what desired
    def keys = m.keys

    def whenDefined(name: String): FeatureExpr = m.whenDefined(name)

    override def equals(that: Any) = that match {
        case c: ConditionalTypeMap => m equals c.m;
        case _ => false
    }

    override def hashCode = m.hashCode

    override def toString = m.toString
}


/**
  * helper functions
  */
trait CTypes extends COptionProvider {
    type PtrEnv = Set[String]


    implicit def toCType(x: CInt): AType = CSigned(x)

    implicit def toCType(x: CChar): AType = CSignUnspecified(x)

    implicit def toCType(x: CShort): AType = CSigned(x)

    implicit def toCType(x: CLong): AType = CSigned(x)

    def arrayType(t: CType): Boolean = t.atype match {
        case CArray(_, _) => true
        case _ => false
    }

    def isScalar(t: CType): Boolean = isArithmetic(t) || isPointer(t) || isFunction(t)

    def isZero(t: CType): Boolean = t.atype match {
        case CZero() => true
        case _ => false
    }

    def isPointer(t: CType): Boolean = t.toValue.atype match {
        case CPointer(_) => true
        case CZero() => true
        //case function references => true
        case _ => false
    }

    def isFunction(t: CType): Boolean = t.atype match {
        case CFunction(_, _) => true
        case _ => false
    }

    def isIntegral(t: CType): Boolean = t.atype match {
        case CZero() => true
        case CSigned(_) => true
        case CUnsigned(_) => true
        case CSignUnspecified(_) => true
        case CBool() => true
        case _ => false
    }

    def isArithmetic(t: CType): Boolean = isIntegral(t) || isFloatingPoint(t)

    def isFloatingPoint(t: CType): Boolean = t.atype match {
        case CFloat() => true
        case CDouble() => true
        case CLongDouble() => true
        case _ => false
    }

    def isArray(t: CType): Boolean = t.atype match {
        case CArray(_, _) => true
        case _ => false
    }

    def isStruct(t: CType): Boolean = isAnonymousStruct(t) || (t.atype match {
        case CStruct(_, _) => true
        case _ => false
    })


    def isAnonymousStruct(t: CType): Boolean = t.atype match {
        case CAnonymousStruct(_, _, _) => true
        case _ => false
    }

    def isCompound(t: CType): Boolean = t.atype == CCompound()

    def isVoid(t: CType): Boolean = t.atype == CVoid()


    /**
      * determines whether types are compatible in assignements etc
      *
      * for "a=b;" with a:type1 and b:type2
      *
      * returns None if there is an error, Some("") if it passes
      * and Some(msg) if there is a warning
      */
    def coerce(expectedType: CType, foundType: CType): Option[String] = {
        lazy val success = Some("")
        lazy val error = None

        val t1 = normalize(expectedType).atype
        val t2 = normalize(foundType).atype
        def pointerCompat(a: AType): Boolean = a == CVoid() || a == CZero() || a == CIgnore()
        //either void pointer?
        if ((expectedType.atype == CPointer(CVoid())) || (foundType.atype == CPointer(CVoid()))) return success
        (t1, t2) match {
            //void pointer are compatible to all other pointers and to functions (or only pointers to functions??)
            case (CPointer(a), CPointer(b)) if pointerCompat(a) || pointerCompat(b) => return success
            case (CPointer(a: CSignSpecifier), CPointer(b: CSignSpecifier)) if !opts.warning_pointer_sign && (a.basicType == b.basicType) =>
                if (foundType.isConstant && !expectedType.isConstant) return Some("assignment discards 'const' qualifier from pointer target type")
                if (foundType.isVolatile && !expectedType.isVolatile) return Some("assignment discards 'volatile' qualifier from pointer target type")
                return success
            //CCompound can be assigned to arrays and structs
            case (CPointer(_) /*incl array*/ , CCompound()) => return success
            case (CStruct(_, _), CCompound()) => return success
            case (CAnonymousStruct(id1, _, _), CAnonymousStruct(id2, _, _)) if (id1==id2) => return success
            case (CAnonymousStruct(id1, _, _), CAnonymousStruct(id2, _, _)) if (id1!=id2) => return error
            case (CAnonymousStruct(_, _, _), CCompound()) => return success
            case (a, CCompound()) if isScalar(a) => return success //works for literals as well
            case _ =>
        }

        //not same but compatible functions (e.g. ignored parameters)
        if (funCompatible(t1, t2)) return success

        //same?
        if (t1 == t2) return success

        //ignore?
        if ((t1 == CIgnore()) || (t2 == CIgnore())) return success

        //assignment pointer = 0
        if (isPointer(t1) && isZero(t2)) return success
        if (isPointer(t2) && isZero(t1)) return success

        //arithmetic operation?
        if (isArithmetic(t1) && isArithmetic(t2)) return success

        //_Bool = any scala value
        if (t1 == CBool() && isScalar(t2)) return success

        if (isIntegral(t1) && isPointer(t2)) return Some("assignment makes integer from pointer without a cast")
        if (isIntegral(t2) && isPointer(t1)) return Some("assignment makes pointer from integer without a cast")
        if (isPointer(t2) && isPointer(t1)) return Some("assignment from incompatible pointer type")

        error
    }

    /**
      * we can report as a warning if both types are number, but they are not the same width or signed
      */
    def isForcedCoercion(expectedType: AType, foundType: AType): Boolean = {
        val t1 = normalize(expectedType)
        val t2 = normalize(foundType)
        isArithmetic(t1) && isArithmetic(t2) && t1 != t2 && t2 != CZero() && !(t2 < t1)
    }

    private def funCompatible(t1: AType, t2: AType): Boolean = (t1, t2) match {
        case (CPointer(p1), CPointer(p2)) => funCompatible(p1, p2)
        case (CFunction(plist1, ret1), CFunction(plist2, ret2)) =>
            coerce(ret1, ret2)==Some("") && (plist1.size == plist2.size) && (plist1 zip plist2).forall(x => coerce(x._1, x._2)==Some(""))
        case _ => false
    }

    /**
      * ansi c conversion rules of two arithmetic types
      * (if called on other types, it just returns the first type; hence the pattern
      * if cocerce(x,y) return converse(x,y) should yield the default behavior )
      *
      * default is int. if either operand has a higher priority, it is preferred over int
      *
      * according to specification: http://techpubs.sgi.com/library/manuals/0000/007-0701-150/pdf/007-0701-150.pdf
      */
    def converse(a: CType, b: CType): CType =
        if (isArithmetic(a) && isArithmetic(b)) {
            val priority = List[CType](
                CLongDouble(), CDouble(), CFloat(),
                CUnsigned(CInt128()), CSigned(CInt128()), CSignUnspecified(CInt128()),
                CUnsigned(CLongLong()), CSigned(CLongLong()), CSignUnspecified(CLongLong()),
                CUnsigned(CLong()), CSigned(CLong()), CSignUnspecified(CLong()),
                CUnsigned(CInt()))
            def either(c: CType): Boolean = (a equalsAType c) || (b equalsAType c)
            priority.foldRight[CType](CSigned(CInt()))((ctype, result) => if (either(ctype)) ctype else result)
        } else a

    /** promotion is what happens internally during conversion */
    def promote(x: CType) = converse(x, x)

    /**
      * normalize types for internal comparison in coerce (do not return this to the outside)
      *
      * * function -> pointer to function
      *
      * * pointer to pointer to function -> pointer to function
      *
      * * remove any CObj within the type
      *
      * * regard arrays as pointers
      *
      * * pointer to ignore equals ignore
      *
      * * CVoid in function parameters is removed
      */
    protected def normalize(t: CType): CType = t.map(normalize)

    protected def normalize(t: AType): AType =
        addFunctionPointers(normalizeA(t))


    /** helper function, part of normalize */
    private def normalizeA(t: AType): AType = t match {
        case CPointer(x: AType) =>
            normalizeA(x) match {
                case c: CFunction => c
                case i: CIgnore => i
                case e => CPointer(e)
            }
        case CArray(g, _) => normalizeA(CPointer(g)) //TODO do this recursively for all occurences of Array
        case CFunction(p, rt) => CFunction(p.map(_.map(normalizeA)).filter(_.atype != CVoid()), rt.map(normalizeA))
        case c => c
    }


    /** helper function, part of normalize */
    private def addFunctionPointers(t: AType): AType = t match {
        case CFunction(p, rt) => CPointer(CFunction(p.map(_.map(addFunctionPointers)), rt map addFunctionPointers))
        //congruence:
        case CPointer(x: AType) => CPointer(addFunctionPointers(x))
        case c => c
    }


    /**
      * are both types char but with different signage?
      */
    protected def isCharSignCoercion(a: AType, b: AType): Boolean = (normalize(a), normalize(b)) match {
        case (CPointer(aa), CPointer(bb)) => isCharSignCoercion(aa, bb)
        case (aa: CSignSpecifier, bb: CSignSpecifier) =>
            aa.basicType == CChar() && bb.basicType == CChar() && aa != bb
        case _ => false
    }


}


sealed trait DeclarationKind

object KDeclaration extends DeclarationKind {
    override def toString = "declaration"
}

object KDefinition extends DeclarationKind {
    override def toString = "definition"
}

object KEnumVar extends DeclarationKind {
    override def toString = "enumerate"
}

object KParameter extends DeclarationKind {
    override def toString = "parameter"
}


sealed trait Linkage

object ExternalLinkage extends Linkage {
    override def toString = "external linkage"
}

object InternalLinkage extends Linkage {
    override def toString = "internal linkage"
}

object NoLinkage extends Linkage {
    override def toString = "no linkage"
}