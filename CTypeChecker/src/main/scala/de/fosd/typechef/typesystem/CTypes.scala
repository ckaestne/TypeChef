package de.fosd.typechef.typesystem

import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.conditional._

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

    /**compares with of two types. if this<that, this type can be converted (widened) to that */
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

case class CSigned(b: CBasicType) extends CType {
    override def <(that: CType) = that match {
        case CSigned(thatb) => b < thatb
        case _ => false
    }
    override def toText = "signed " + b
    def toXML = <signed>
        {b.toXML}
    </signed>
}

case class CUnsigned(b: CBasicType) extends CType {
    override def <(that: CType) = that match {
        case CUnsigned(thatb) => b < thatb
        case _ => false
    }
    override def toText = "unsigned " + b
    def toXML = <unsigned>
        {b.toXML}
    </unsigned>
}

case class CSignUnspecified(b: CBasicType) extends CType {
    override def <(that: CType) = that match {
        case CSignUnspecified(thatb) => b < thatb
        case _ => false
    }
    override def toText = b.toText
    def toXML = <nosign>
        {b.toXML}
    </nosign>
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

/**struct and union are handled in the same construct but distinguished with a flag */
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
    override def toText = param.mkString("(", ", ", ")") + " => " + ret
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

/**objects in memory */
case class CObj(t: CType) extends CType {
    override def toObj = this
    //no CObj(CObj(...))
    override def toValue: CType = t match {
        case CArray(t, _) => CPointer(t)
        case _ => t
    }
    override def isObject = true
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
}


/**errors */
case class CUnknown(msg: String = "") extends CType {
    override def toObj = this
    override def equals(that: Any) = that match {
        case CUnknown(_) => true
        case _ => super.equals(that)
    }
    override def isUnknown: Boolean = true
    def toXML = <unknown msg={msg}/>
}

/**not defined in environment, typically only used in CChoice types */
case class CUndefined() extends CUnknown("undefined")


/**
 * xml reader
 */
object CType {
    def fromXML(node: scala.xml.NodeSeq): CType = {
        var result: CType = CUnknown("fromXML error: " + node.text)
        (node \ "signed").map(x => result = CSigned(fromXMLBasicType(x)))
        (node \ "unsigned").map(x => result = CUnsigned(fromXMLBasicType(x)))
        (node \ "nosign").map(x => result = CSignUnspecified(fromXMLBasicType(x)))
        (node \ "void").map(x => result = CVoid())
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
 */
class ConditionalTypeMap(private val m: ConditionalMap[String, Conditional[CType]]) {
    def this() = this (new ConditionalMap())
    /**
     * apply returns a type, possibly CUndefined or a
     * choice type
     */
    def apply(name: String): Conditional[CType] = getOrElse(name, CUnknown(name))
    def getOrElse(name: String, errorType: CType): Conditional[CType] = Conditional.combine(m.getOrElse(name, One(errorType))) simplify

    def ++(that: ConditionalTypeMap) = if (that.isEmpty) this else new ConditionalTypeMap(this.m ++ that.m)
    def ++(l: Seq[(String, FeatureExpr, Conditional[CType])]) = if (l.isEmpty) this else new ConditionalTypeMap(m ++ l)
    def +(name: String, f: FeatureExpr, t: Conditional[CType]) = new ConditionalTypeMap(m.+(name, f, t))
    def contains(name: String) = m.contains(name)
    def isEmpty = m.isEmpty
    def allTypes: Iterable[Conditional[CType]] = m.allEntriesFlat

    override def equals(that: Any) = that match {case c: ConditionalTypeMap => m equals c.m; case _ => false}
    override def hashCode = m.hashCode
    override def toString = m.toString
}


/**
 * helper functions
 */
trait CTypes {
    type PtrEnv = Set[String]


    implicit def toCType(x: CInt): CType = CSigned(x)
    implicit def toCType(x: CChar): CType = CSignUnspecified(x)
    implicit def toCType(x: CShort): CType = CSigned(x)
    implicit def toCType(x: CLong): CType = CSigned(x)

    def arrayType(t: CType): Boolean = t.toValue match {
        case CArray(_, _) => true
        case _ => false
    }

    def isScalar(t: CType): Boolean = isArithmetic(t) || isPointer(t)

    def isPointer(t: CType): Boolean = t.toValue match {
        case CPointer(_) => true
        //case function references => true
        case _ => false
    }

    def isIntegral(t: CType): Boolean = t.toValue match {
        case CSigned(_) => true
        case CUnsigned(_) => true
        case CSignUnspecified(_) => true
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
     *  determines whether types are compatible in assignements etc
     */
    def coerce(type1: CType, type2: CType): Boolean = {
        val t1 = normalize(type1)
        val t2 = normalize(type2)
        //either void pointer?
        if ((type1.toValue == CPointer(CVoid())) || (type2.toValue == CPointer(CVoid()))) return true;
        ((t1, t2) match {
            //void pointer are compatible to all other pointers and to functions (or only pointers to functions??)
            case (CPointer(a), CPointer(b)) => if (a == CVoid() || b == CVoid()) return true
            case _ =>
            //                    case (CPointer(CVoid()), CPointer(_)) => true
            //                    case (CPointer(CVoid()), CFunction(_, _)) => true
            //                    case (CPointer(_), CPointer(CVoid())) => true
            //                    case (CFunction(_, _), CPointer(CVoid())) => true
        })

        //same?
        if (t1 == t2) return true;

        //ignore?
        if ((t1 == CIgnore()) || (t2 == CIgnore())) return true

        //arithmetic operation?
        if (isArithmetic(t1) && isArithmetic(t2)) return true

        //assignment pointer = int (actually only correct if int is zero, otherwise should be a warning)
        if (isPointer(t1) && isIntegral(t2)) return true

        return false
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
                CLongDouble(), CLong(), CFloat(),
                CUnsigned(CLongLong()), CSigned(CLongLong()), CSignUnspecified(CLongLong()),
                CUnsigned(CLong()), CSigned(CLong()), CSignUnspecified(CLong()),
                CUnsigned(CInt()))
            def either(c: CType): Boolean = (a == c) || (b == c)
            priority.foldRight[CType](CSigned(CInt()))((ctype, result) => if (either(ctype)) ctype else result)
        } else a
    /**promotion is what happens internally during conversion */
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
     */
    private def normalize(t: CType): CType =
        addFunctionPointers(normalizeA(t))


    /**helper function, part of normalize */
    private def normalizeA(t: CType): CType = t.toValue match {
        case CPointer(x: CType) =>
            normalizeA(x) match {
                case c: CFunction => c
                case e => CPointer(e)
            }
        case CArray(t, _) => CPointer(normalizeA(t)) //TODO do this recursively for all occurences of Array
        case CFunction(p, rt) => CFunction(p.map(normalizeA), normalizeA(rt))
        case c => c
    }

    /**helper function, part of normalize */
    private def addFunctionPointers(t: CType): CType = t match {
        case CFunction(p, rt) => CPointer(CFunction(p.map(addFunctionPointers), addFunctionPointers(rt)))
        //congruence:
        case CPointer(x: CType) => CPointer(addFunctionPointers(x))
        case c => c
    }


}