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

    sealed abstract class CBasicType {
        def <(that: CBasicType): Boolean
    }

    sealed abstract class CType {
        def toObj: CType = CObj(this)
        //convert from object to value (lvalue to rvalue) if applicable; if already a type return the type
        def toValue: CType = this
        def isObject: Boolean = false

        //simplify rewrites Choice Types; requires reasoning about variability
        def simplify = _simplify(base)
        def simplify(ctx: FeatureExpr) = _simplify(ctx)
        protected[CTypes] def _simplify(context: FeatureExpr) = this

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
     * maintains a map from names to types
     * a name may be mapped to alternative types with different feature expressions
     */
    case class ConditionalTypeMap(private val entries: Map[String, Seq[(FeatureExpr, CType)]]) {
        def this() = this (Map())
        /*
            feature expressions are not rewritten as in the macrotable, but we
             may later want to ensure that they are mutually exclusive
             in get, they simply overwrite each other in order of addition
         */
        /**
         * apply returns a type, possibly CUndefined or a
         * choice type
         */
        def apply(name: String): CType = getOrElse(name, CUndefined())
        def getOrElse(name: String, errorType: CType): CType = {
            if (!contains(name)) errorType
            else {
                val types = entries(name)
                if (types.size == 1 && types.head._1 == base) types.head._2
                else createChoiceType(types, errorType)
            }
        }

        def ++(that: ConditionalTypeMap) = {
            var r = entries
            for ((name, seq) <- that.entries) {
                if (r contains name)
                    r = r + (name -> (seq ++ r(name)))
                else
                    r = r + (name -> seq)
            }
            new ConditionalTypeMap(r)
        }
        def ++(decls: Seq[(String, FeatureExpr, CType)]) = {
            var r = entries
            for (decl <- decls) {
                if (r contains decl._1)
                    r = r + (decl._1 -> ((decl._2, decl._3) +: r(decl._1)))
                else
                    r = r + (decl._1 -> Seq((decl._2, decl._3)))
            }
            new ConditionalTypeMap(r)
        }
        def +(name: String, f: FeatureExpr, t: CType) = this ++ Seq((name, f, t))
        def contains(name: String) = (entries contains name) && !entries(name).isEmpty
        def isEmpty = entries.isEmpty
        def allTypes: Iterable[CType] = entries.values.flatten.map(_._2)

        private def createChoiceType(types: Seq[(FeatureExpr, CType)], errorType: CType) =
            types.foldRight[CType](errorType)((p, t) => CChoice(p._1, p._2, t)) simplify
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
        (t1 == t2) ||
                (isArithmetic(t1) && isArithmetic(t2)) ||
                ((t1, t2) match {
                    case (CPointer(a), CPointer(b)) => a == CVoid() || b == CVoid()
                    case _ => false
                })
    }

    private def normalize(t: CType) = t.toValue match {
        case CPointer(f: CFunction) => f
        case CArray(t, _) => CPointer(t) //TODO do this recursively for all occurences of Array
        case c => c
    }


}