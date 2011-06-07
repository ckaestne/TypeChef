package de.fosd.typechef.typesystem

import de.fosd.typechef.featureexpr.FeatureExpr
import FeatureExpr.base


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

    sealed abstract class CBasicType

    sealed abstract class CType {
        def toObj: CType = CObj(this)

        //simplify rewrites Choice Types; requires reasoning about variability
        def simplify = _simplify(base)
        def simplify(ctx: FeatureExpr) = _simplify(ctx)
        protected[CTypes] def _simplify(context: FeatureExpr) = this

        /* map over this type considering variability */
        def mapV(f: FeatureExpr, op: (FeatureExpr, CType) => CType): CType = op(f, this)
        def map(op: CType => CType): CType = op(this)

        def sometimesUnknown: Boolean = false
    }

    //    /** type without variability */
    //    sealed abstract class CStaticType extends CType

    case class CChar() extends CBasicType

    case class CShort() extends CBasicType

    case class CInt() extends CBasicType

    case class CLong() extends CBasicType

    case class CLongLong() extends CBasicType

    implicit def toCType(x: CInt): CType = CSigned(x)
    implicit def toCType(x: CChar): CType = CSignUnspecified(x)
    implicit def toCType(x: CShort): CType = CSigned(x)
    implicit def toCType(x: CLong): CType = CSigned(x)

    case class CVoid() extends CType

    case class CSigned(b: CBasicType) extends CType

    case class CUnsigned(b: CBasicType) extends CType

    case class CSignUnspecified(b: CBasicType) extends CType

    //implementationspecific for Char

    case class CFloat() extends CType

    case class CDouble() extends CType

    case class CLongDouble() extends CType

    case class CPointer(t: CType) extends CType

    //length is currently not analyzed. using always -1
    case class CArray(t: CType, length: Int = -1) extends CType

    case class CStruct(s: String) extends CType

    case class CAnonymousStruct(fields: List[(String, CType)]) extends CType

    case class CFunction(param: Seq[CType], ret: CType) extends CType {
        override def toObj = this
    }

    //varargs should only occur in paramter lists
    case class CVarArgs() extends CType

    /**objects in memory */
    case class CObj(t: CType) extends CType

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


    /**
     * variability: alternative types (choice node on types!)
     */
    case class CChoice(f: FeatureExpr, a: CType, b: CType) extends CType {
        protected[CTypes] override def _simplify(context: FeatureExpr) = {
            val aa = a._simplify(context and f)
            val bb = b._simplify(context andNot f)
            if ((context and f).isContradiction) bb
            else if ((context andNot f).isContradiction) aa
            else CChoice(f, aa, bb)
        }
        override def toObj = CChoice(f, a.toObj, b.toObj)
        override def mapV(ctx: FeatureExpr, op: (FeatureExpr, CType) => CType): CType =
            CChoice(f, op(ctx and f, a), op(ctx andNot f, b))
        override def map(op: CType => CType): CType =
            CChoice(f, op(a), op(b))
        override def sometimesUnknown: Boolean = a.sometimesUnknown || b.sometimesUnknown
    }


    type PtrEnv = Set[String]
    //assumed well-formed pointer targets on structures


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

    /**
     *  determines whether types are compatible in assignements etc
     */
    def coerce(t1: CType, t2: CType) =
    //TODO variability
        (t1 == t2) ||
                (isArithmetic(t1) && isArithmetic(t2)) ||
                ((t1, t2) match {
                    case (CPointer(a), CPointer(b)) => a == CVoid() || b == CVoid()
                    case _ => false
                })


}