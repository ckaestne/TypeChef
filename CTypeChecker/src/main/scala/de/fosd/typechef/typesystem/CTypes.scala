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
    implicit def toCType(x: CShort): CType = CSigned(x)
    implicit def toCType(x: CLong): CType = CSigned(x)

    case class CVoid() extends CType

    case class CSigned(b: CBasicType) extends CType

    case class CUnsigned(b: CBasicType) extends CType

    case class CFloat() extends CType

    case class CDouble() extends CType

    case class CLongDouble() extends CType

    case class CPointer(t: CType) extends CType

    case class CArray(t: CType, length: Int) extends CType

    case class CStruct(s: String) extends CType

    case class CFunction(param: Seq[CType], ret: CType) extends CType

    type StructEnv = Map[String, Seq[(String, CType)]]
    type PtrEnv = Set[String]
    //assumed well-formed pointer targets on structures


    def wellformed(structEnv: StructEnv, ptrEnv: PtrEnv, ctype: CType): Boolean = {
        val wf = wellformed(structEnv, ptrEnv, _: CType)
        ctype match {
            case CSigned(_) => true
            case CUnsigned(_) => true
            case CVoid() => true
            case CFloat() => true
            case CDouble() => true
            case CLongDouble() => true
            case CPointer(CStruct(s)) => ptrEnv contains s
            case CPointer(t) => wf(t)
            case CArray(t, n) => wf(t) && (t != CVoid()) && n > 0
            case CFunction(param, ret) => wf(ret) && !arrayType(ret) && (
                    param.forall(p => wf(p) && !arrayType(p) && p != CVoid()))
            case CStruct(name) => {
                val members = structEnv.getOrElse(name, Seq())
                val memberNames = members.map(_._1)
                val memberTypes = members.map(_._2)
                (!members.isEmpty && memberNames.distinct.size == memberNames.size &&
                        memberTypes.forall(t => {
                            t != CVoid() && wellformed(structEnv, ptrEnv + name, t)
                        }))
            }
        }
    }

    def arrayType(t: CType) = t match {
        case CArray(_, _) => true
        case _ => false
    }


}