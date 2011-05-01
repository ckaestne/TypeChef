package de.fosd.typechef.typesystem


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

    abstract class CAbstractType

    sealed abstract class CBasicType

    sealed abstract class CType extends CAbstractType

    case class CChar() extends CBasicType

    case class CShort() extends CBasicType

    case class CInt() extends CBasicType

    case class CLong() extends CBasicType

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

    case class CArray(t: CType, length: Int) extends CType

    case class CStruct(s: String) extends CType

    case class CFunction(param: Seq[CType], ret: CType) extends CType

    /**objects in memory */
    case class CObj(t: CType) extends CType

    /**errors */
    case class CUnknown(msg: String = "") extends CType {
        override def equals(that: Any) = that match {
            case CUnknown(_) => true
            case _ => super.equals(that)
        }
    }


    type PtrEnv = Set[String]
    //assumed well-formed pointer targets on structures


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
     * determines whether types are compatible in assignements etc
     */
    def coerce(t1: CType, t2: CType) =
        (t1 == t2) ||
                (isArithmetic(t1) && isArithmetic(t2)) ||
                ((t1, t2) match {
                    case (CPointer(a), CPointer(b)) => a == CVoid() || b == CVoid()
                    case _ => false
                })


}