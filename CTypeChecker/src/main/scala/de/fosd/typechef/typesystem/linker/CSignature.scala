package de.fosd.typechef.typesystem.linker

import de.fosd.typechef.typesystem.CType
import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.error.Position


/**
 * signature with name type and condition. the position is only stored for debugging purposes and has no further
 * relevance.
 * its also not necessarily de/serialized
 *
 * TODO types should be selfcontained (i.e. not reference to structures or type names defined elsewhere,
 * but resolved to anonymous structs, etc.)
 */
case class CSignature(name: String, ctype: CType, fexpr: FeatureExpr, pos: Seq[Position], extraFlags: Set[CFlag] = Set()) {
    override def toString =
        name + ": " + ctype.toText + " " + extraFlags.mkString("+") + "\t\tif " + fexpr + "\t\tat " + pos.mkString(", ")

    override def hashCode = name.hashCode
    override def equals(that: Any) = that match {
        case CSignature(thatName, thatCType, thatFexpr, thatPos, thatExtraFlags) => name == thatName && CType.isLinkCompatible(ctype, thatCType) && fexpr.equivalentTo(thatFexpr) && pos == thatPos && extraFlags == thatExtraFlags
        case _ => false
    }

    def and(f: FeatureExpr) = CSignature(name, ctype, fexpr and f, pos, extraFlags)

}

