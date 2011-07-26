package de.fosd.typechef.typesystem

import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.conditional._

/**
 * basic types of C and definitions which types are compatible
 *
 * extended by alternative types with variability: CChoice
 */
trait CTypes {

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
    }

    sealed abstract class CType {
        def toObj: CType = CObj(this)
        //convert from object to value (lvalue to rvalue) if applicable; if already a type return the type
        def toValue: CType = this
        def isObject: Boolean = false


        /* map over this type considering variability */
        def mapV(f: FeatureExpr, op: (FeatureExpr, CType) => CType): CType = op(f, this)
        def map(op: CType => CType): CType = op(this)

        def sometimesUnknown: Boolean = false

        /**compares with of two types. if this<that, this type can be converted (widened) to that */
        def <(that: CType): Boolean = false
    }

    //    /** type without variability */
    //    sealed abstract class CStaticType extends CType

    case class CChar() extends CBasicType {
        def <(that: CBasicType) = that == CLongLong() || that == CInt() || that == CLong()
    }

    case class CShort() extends CBasicType {
        def <(that: CBasicType) = that == CLongLong() || that == CInt() || that == CLong()
    }

    case class CInt() extends CBasicType {
        def <(that: CBasicType) = that == CLongLong() || that == CLong()
    }

    case class CLong() extends CBasicType {
        def <(that: CBasicType) = that == CLongLong()
    }

    case class CLongLong() extends CBasicType {
        def <(that: CBasicType) = false
    }

    implicit def toCType(x: CInt): CType = CSigned(x)
    implicit def toCType(x: CChar): CType = CSignUnspecified(x)
    implicit def toCType(x: CShort): CType = CSigned(x)
    implicit def toCType(x: CLong): CType = CSigned(x)

    case class CVoid() extends CType

    case class CSigned(b: CBasicType) extends CType {
        override def <(that: CType) = that match {
            case CSigned(thatb) => b < thatb
            case _ => false
        }
    }

    case class CUnsigned(b: CBasicType) extends CType {
        override def <(that: CType) = that match {
            case CUnsigned(thatb) => b < thatb
            case _ => false
        }
    }

    case class CSignUnspecified(b: CBasicType) extends CType {
        override def <(that: CType) = that match {
            case CSignUnspecified(thatb) => b < thatb
            case _ => false
        }
    }

    //implementationspecific for Char

    case class CFloat() extends CType

    case class CDouble() extends CType

    case class CLongDouble() extends CType

    case class CPointer(t: CType) extends CType

    //length is currently not analyzed. using always -1
    case class CArray(t: CType, length: Int = -1) extends CType

    /**struct and union are handled in the same construct but distinguished with a flag */
    case class CStruct(s: String, isUnion: Boolean = false) extends CType

    case class CAnonymousStruct(fields: ConditionalTypeMap, isUnion: Boolean = false) extends CType

    case class CFunction(param: Seq[CType], ret: CType) extends CType {
        override def toObj = this
    }

    //varargs should only occur in paramter lists
    case class CVarArgs() extends CType

    /**objects in memory */
    case class CObj(t: CType) extends CType {
        override def toObj = this
        //no CObj(CObj(...))
        override def toValue: CType = t match {
            case CArray(t, _) => CPointer(t)
            case _ => t
        }
        override def isObject = true
    }

    /**
     * CCompound is a workaround for initializers.
     * This type is created by initializers with any structure.
     * We currently don't care about internals.
     * CCompound can be cast into any array or structure
     */
    case class CCompound() extends CType

    /**
     * CIgnore is a type for stuff we currently do not want to check
     * it can be cast to anything and is not considered an error
     */
    case class CIgnore() extends CType


    /**errors */
    case class CUnknown(msg: String = "") extends CType {
        override def toObj = this
        override def equals(that: Any) = that match {
            case CUnknown(_) => true
            case _ => super.equals(that)
        }
        override def sometimesUnknown: Boolean = true
    }

    /**no defined in environment, typically only used in CChoice types */
    case class CUndefined() extends CUnknown("undefined")


    type PtrEnv = Set[String]

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
        def apply(name: String): Conditional[CType] = getOrElse(name, CUnknown())
        def getOrElse(name: String, errorType: CType): Conditional[CType] = Conditional.combine(m.getOrElse(name, One(errorType))) simplify

        def ++(that: ConditionalTypeMap) = new ConditionalTypeMap(this.m ++ that.m)
        def ++(l: Seq[(String, FeatureExpr, Conditional[CType])]) = new ConditionalTypeMap(m ++ l)
        def +(name: String, f: FeatureExpr, t: Conditional[CType]) = new ConditionalTypeMap(m.+(name, f, t))
        def contains(name: String) = m.contains(name)
        def isEmpty = m.isEmpty
        def allTypes: Iterable[Conditional[CType]] = m.allEntriesFlat

        override def equals(that: Any) = that match {case c: ConditionalTypeMap => m equals c.m; case _ => false}
        override def hashCode = m.hashCode
    }


    /**
     * helper functions
     */
    def arrayType(t: CType): Boolean = t match {
        case CArray(_, _) => true
        case _ => false
    }

    def isScalar(t: CType): Boolean = isArithmetic(t) || isPointer(t)

    def isPointer(t: CType): Boolean = t match {
        case CPointer(_) => true
        //case function references => true
        case _ => false
    }

    def isIntegral(t: CType): Boolean = t match {
        case CSigned(_) => true
        case CUnsigned(_) => true
        case CSignUnspecified(_) => true
        case _ => false
    }
    def isArithmetic(t: CType): Boolean = isIntegral(t) || (t match {
        case CFloat() => true
        case CDouble() => true
        case CLongDouble() => true
        case _ => false
    })

    def isArray(t: CType): Boolean = t match {
        case CArray(_, _) => true
        case _ => false
    }
    def isStruct(t: CType): Boolean = t match {
        case CStruct(_, _) => true
        case CAnonymousStruct(_, _) => true
        case _ => false
    }
    def isCompound(t: CType): Boolean = t == CCompound()


    /**
     *  determines whether types are compatible in assignements etc
     */
    def coerce(type1: CType, type2: CType) = {
        val t1 = normalize(type1)
        val t2 = normalize(type2)
        //TODO variability
        (t1 == t2) || (t1 == CIgnore()) || (t2 == CIgnore()) ||
                (isArithmetic(t1) && isArithmetic(t2)) ||
                ((t1, t2) match {
                    case (CPointer(a), CPointer(b)) => a == CVoid() || b == CVoid()
                    case _ => false
                })
    }

    /**
     * normalize types for internal comparison (do not return this to the outside)
     *
     * * Pointers to functions -> functions
     *
     * * remove any CObj within the type
     *
     * * regard arrays as pointers
     */
    private def normalize(t: CType): CType = t.toValue match {
        case CPointer(f: CFunction) => normalize(f)
        case CPointer(x: CType) => CPointer(normalize(x))
        case CArray(t, _) => CPointer(normalize(t)) //TODO do this recursively for all occurences of Array
        case CFunction(p, rt) => CFunction(p.map(normalize), normalize(rt))
        case c => c
    }


    //ugly workaround
    //TODO remove, replace by proper variability handling
    def __makeOne(c: Conditional[CType]): CType = c match {
        case One(e) => e
        case Choice(_, a, _) => __makeOne(a)
    }

}