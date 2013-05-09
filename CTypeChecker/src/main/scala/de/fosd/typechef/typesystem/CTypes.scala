package de.fosd.typechef.typesystem

import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.conditional._
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

sealed abstract class CBasicType {
    def <(that: CBasicType): Boolean
    def toXML: xml.Elem
    def toText: String //for debug purposes
}

sealed abstract class CType {
    def toObj: CType = CObj(this)
    //convert from object to value (lvalue to rvalue) if applicable; if already a type return the type
    def toValue: CType = this
    def isObject: Boolean = false
    def isFunction: Boolean = false


    /* map over this type considering variability */
    def mapV(f: FeatureExpr, op: (FeatureExpr, CType) => CType): CType = op(f, this)
    def map(op: CType => CType): CType = op(this)
    def isUnknown: Boolean = false
    def isIgnore: Boolean = false

    /** compares with of two types. if this<that, this type can be converted (widened) to that */
    def <(that: CType): Boolean = false
    def toXML: xml.Elem
    def toText: String = toString //for debug purposes
}

//    /** type without variability */
//    sealed abstract class CStaticType extends CType


case class CChar() extends CBasicType {
    def <(that: CBasicType) = that == CLongLong() || that == CInt() || that == CLong()
    override def toText = "char"
    def toXML = <char/>
}

case class CShort() extends CBasicType {
    def <(that: CBasicType) = that == CLongLong() || that == CInt() || that == CLong()
    override def toText = "short"
    def toXML = <short/>
}

case class CInt() extends CBasicType {
    def <(that: CBasicType) = that == CLongLong() || that == CLong()
    override def toText = "int"
    def toXML = <int/>
}

case class CLong() extends CBasicType {
    def <(that: CBasicType) = that == CLongLong()
    override def toText = "long"
    def toXML = <long/>
}

case class CLongLong() extends CBasicType {
    def <(that: CBasicType) = false
    override def toText = "long long"
    def toXML = <longlong/>
}


case class CVoid() extends CType {
    override def toText = "void"
    def toXML = <void/>
}

/**
 * zero is a special type for the constant 0
 * that is all: a function and an integer and a pointer
 */
case class CZero() extends CType {
    override def toText = "void"
    def toXML = <zero/>
}

abstract class CSignSpecifier(val basicType: CBasicType) extends CType

case class CSigned(b: CBasicType) extends CSignSpecifier(b) {
    override def <(that: CType) = that match {
        case CSigned(thatb) => b < thatb
        case _ => false
    }
    override def toText = "signed " + b.toText
    def toXML = <signed>
        {b.toXML}
    </signed>
}

case class CUnsigned(b: CBasicType) extends CSignSpecifier(b) {
    override def <(that: CType) = that match {
        case CUnsigned(thatb) => b < thatb
        case _ => false
    }
    override def toText = "unsigned " + b.toText
    def toXML = <unsigned>
        {b.toXML}
    </unsigned>
}

case class CSignUnspecified(b: CBasicType) extends CSignSpecifier(b) {
    override def <(that: CType) = that match {
        case CSignUnspecified(thatb) => b < thatb
        case _ => false
    }
    override def toText = b.toText
    def toXML = <nosign>
        {b.toXML}
    </nosign>
}

case class CBool() extends CType {
    override def toText = "_Bool"
    def toXML = <_Bool/>
}

//implementationspecific for Char

case class CFloat() extends CType {
    override def toText = "float"
    def toXML = <float/>
}

case class CDouble() extends CType {
    override def toText = "double"
    def toXML = <double/>
}

case class CLongDouble() extends CType {
    override def toText = "long double"
    def toXML = <longdouble/>
}

case class CPointer(t: CType) extends CType {
    override def toText = "*" + t.toText
    def toXML = <pointer>
        {t.toXML}
    </pointer>
}

//length is currently not analyzed. using always -1
case class CArray(t: CType, length: Int = -1) extends CType {
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
case class CStruct(s: String, isUnion: Boolean = false) extends CType {
    override def toText = (if (isUnion) "union " else "struct ") + s
    def toXML = <struct isUnion={isUnion.toString}>
        {s}
    </struct>
}

case class CAnonymousStruct(fields: ConditionalTypeMap, isUnion: Boolean = false) extends CType {
    override def toText = (if (isUnion) "union " else "struct ") + "{" + fields + "}"
    def toXML = <astruct isUnion={isUnion.toString}>
        {}
    </astruct>
}

case class CFunction(param: Seq[CType], ret: CType) extends CType {
    override def toObj = this
    override def isFunction: Boolean = true
    override def toText = param.map(_.toText).mkString("(", ", ", ")") + " => " + ret.toText
    def toXML = <function>
        {param.map(x => <param>
            {x.toXML}
        </param>)}<ret>
            {ret.toXML}
        </ret>
    </function>
}


//varargs should only occur in paramter lists
case class CVarArgs() extends CType {
    override def toText = "..."
    def toXML = <vargs/>
}

/** objects in memory */
case class CObj(t: CType) extends CType {
    override def toObj = this
    //no CObj(CObj(...))
    override def toValue: CType = t match {
        case CArray(g, _) => CPointer(g)
        case _ => t
    }
    override def isObject = true
    override def isFunction = t.isFunction
    override def isUnknown = t.isUnknown
    override def isIgnore = t.isIgnore
    def toXML = <obj>
        {t.toXML}
    </obj>
}

/**
 * CCompound is a workaround for initializers.
 * This type is created by initializers with any structure.
 * We currently don't care about internals.
 * CCompound can be cast into any array or structure
 */
case class CCompound() extends CType {
    def toXML = <compound/>
}

/**
 * CIgnore is a type for stuff we currently do not want to check
 * it can be cast to anything and is not considered an error
 */
case class CIgnore() extends CType {
    def toXML = <ignore/>
    override def isIgnore = true
}


/** errors */
case class CUnknown(msg: String = "") extends CType {
    override def toObj = this
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
    def fromXML(node: scala.xml.NodeSeq): CType = {
        var result: CType = CUnknown("fromXML error: " + node.text)
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
        (node \ "pointer").map(x => result = CPointer(fromXML(x)))
        (node \ "array").map(x => result = CArray(fromXML(x), x.attribute("length").get.head.text.toInt))
        (node \ "struct").map(x => result = CStruct(x.text.trim, x.attribute("isUnion").get.head.text.toBoolean))
        (node \ "astruct").map(x => result = CAnonymousStruct(new ConditionalTypeMap(), x.attribute("isUnion").get.head.text.toBoolean)) //TODO
        (node \ "function").map(x => result = CFunction(
            (x \ "param").map(fromXML(_)),
            fromXML((x \ "ret").head)
        ))
        (node \ "obc").map(x => result = CObj(fromXML(x)))
        (node \ "compound").map(x => result = CCompound())
        (node \ "ignore").map(x => result = CIgnore())
        (node \ "unkown").map(x => result = CUnknown(x.attribute("msg").get.text))
        result
    }


    def fromXMLBasicType(node: scala.xml.Node): CBasicType = {
        var result: CBasicType = CInt()
        (node \ "char").map(x => result = CChar())
        (node \ "short").map(x => result = CShort())
        (node \ "long").map(x => result = CLong())
        (node \ "longlong").map(x => result = CLongLong())
        result
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
 */
class ConditionalVarEnv(m: ConditionalMap[String, (AST, Conditional[(CType, DeclarationKind, Int)])])
        extends ConditionalCMap[(CType, DeclarationKind, Int)](m) {
    def this() = this(new ConditionalMap())
    def apply(name: String): Conditional[CType] = lookupType(name)
    def lookup(name: String): Conditional[(CType, DeclarationKind, Int)] = getOrElse(name, (CUnknown(name), KDeclaration, -1))
    def lookupType(name: String): Conditional[CType] = lookup(name).map(_._1)
    def lookupKind(name: String): Conditional[DeclarationKind] = lookup(name).map(_._2)
    def lookupScope(name: String): Conditional[Int] = lookup(name).map(_._3)
    def +(name: String, f: FeatureExpr, a: AST, t: Conditional[CType], kind: DeclarationKind, scope: Int) = new ConditionalVarEnv(m.+(name, f, (a, t.map(x => (x, kind, scope)))))
    def ++(v: Seq[(String, FeatureExpr, AST, Conditional[CType], DeclarationKind, Int)]) =
        v.foldLeft(this)((c, x) => c.+(x._1, x._2, x._3, x._4, x._5, x._6))
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
    def getOrElse(name: String, errorType: T): Conditional[T] = Conditional.combine(getFullOrElse(name, (null, One(errorType))).map(_._2))
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


    implicit def toCType(x: CInt): CType = CSigned(x)
    implicit def toCType(x: CChar): CType = CSignUnspecified(x)
    implicit def toCType(x: CShort): CType = CSigned(x)
    implicit def toCType(x: CLong): CType = CSigned(x)

    def arrayType(t: CType): Boolean = t.toValue match {
        case CArray(_, _) => true
        case _ => false
    }

    def isScalar(t: CType): Boolean = isArithmetic(t) || isPointer(t) || isFunction(t)

    def isZero(t: CType): Boolean = t.toValue match {
        case CZero() => true
        case _ => false
    }

    def isPointer(t: CType): Boolean = t.toValue match {
        case CPointer(_) => true
        case CZero() => true
        //case function references => true
        case _ => false
    }

    def isFunction(t: CType): Boolean = t.toValue match {
        case CFunction(_, _) => true
        case _ => false
    }

    def isIntegral(t: CType): Boolean = t.toValue match {
        case CZero() => true
        case CSigned(_) => true
        case CUnsigned(_) => true
        case CSignUnspecified(_) => true
        case CBool() => true
        case _ => false
    }

    def isArithmetic(t: CType): Boolean = isIntegral(t) || (t.toValue match {
        case CFloat() => true
        case CDouble() => true
        case CLongDouble() => true
        case _ => false
    })

    def isArray(t: CType): Boolean = t.toValue match {
        case CArray(_, _) => true
        case _ => false
    }

    def isStruct(t: CType): Boolean = t.toValue match {
        case CStruct(_, _) => true
        case CAnonymousStruct(_, _) => true
        case _ => false
    }

    def isCompound(t: CType): Boolean = t.toValue == CCompound()


    /**
     * determines whether types are compatible in assignements etc
     *
     * for "a=b;" with a:type1 and b:type2
     */
    def coerce(expectedType: CType, foundType: CType): Boolean = {
        val t1 = normalize(expectedType)
        val t2 = normalize(foundType)
        def pointerCompat(a: CType): Boolean = a == CVoid() || a == CZero() || a == CIgnore()
        //either void pointer?
        if ((expectedType.toValue == CPointer(CVoid())) || (foundType.toValue == CPointer(CVoid()))) return true;
        ((t1, t2) match {
            //void pointer are compatible to all other pointers and to functions (or only pointers to functions??)
            case (CPointer(a), CPointer(b)) if (pointerCompat(a) || pointerCompat(b)) => return true
            case (CPointer(a: CSignSpecifier), CPointer(b: CSignSpecifier)) if (!opts.warning_pointer_sign && (a.basicType == b.basicType)) => return true
            //CCompound can be assigned to arrays and structs
            case (CPointer(_) /*incl array*/ , CCompound()) => return true
            case (CStruct(_, _), CCompound()) => return true
            case (CAnonymousStruct(_, _), CCompound()) => return true
            case (a, CCompound()) if (isScalar(a)) => return true //works for literals as well
            case _ =>
        })

        //not same but compatible functions (e.g. ignored parameters)
        if (funCompatible(t1, t2)) return true;

        //same?
        if (t1 == t2) return true;

        //ignore?
        if ((t1 == CIgnore()) || (t2 == CIgnore())) return true


        //arithmetic operation?
        if (isArithmetic(t1) && isArithmetic(t2)) return true

        //_Bool = any scala value
        if (t1 == CBool() && isScalar(t2)) return true

        //assignment pointer = 0
        if (isPointer(t1) && isZero(t2)) return true
        if (isPointer(t2) && isZero(t1)) return true

        false
    }

    private def funCompatible(t1: CType, t2: CType): Boolean = (t1, t2) match {
        case (CPointer(p1), CPointer(p2)) => funCompatible(p1, p2)
        case (CFunction(plist1, ret1), CFunction(plist2, ret2)) =>
            coerce(ret1, ret2) && (plist1.size == plist2.size) && (plist1 zip plist2).forall(x => coerce(x._1, x._2))
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
                CUnsigned(CLongLong()), CSigned(CLongLong()), CSignUnspecified(CLongLong()),
                CUnsigned(CLong()), CSigned(CLong()), CSignUnspecified(CLong()),
                CUnsigned(CInt()))
            def either(c: CType): Boolean = (a == c) || (b == c)
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
    protected def normalize(t: CType): CType =
        addFunctionPointers(normalizeA(t))


    /** helper function, part of normalize */
    private def normalizeA(t: CType): CType = t.toValue match {
        case CPointer(x: CType) =>
            normalizeA(x) match {
                case c: CFunction => c
                case i: CIgnore => i
                case e => CPointer(e)
            }
        case CArray(g, _) => normalizeA(CPointer(g)) //TODO do this recursively for all occurences of Array
        case CFunction(p, rt) => CFunction(p.map(normalizeA).filter(_ != CVoid()), normalizeA(rt))
        case c => c
    }

    /** helper function, part of normalize */
    private def addFunctionPointers(t: CType): CType = t match {
        case CFunction(p, rt) => CPointer(CFunction(p.map(addFunctionPointers), addFunctionPointers(rt)))
        //congruence:
        case CPointer(x: CType) => CPointer(addFunctionPointers(x))
        case c => c
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