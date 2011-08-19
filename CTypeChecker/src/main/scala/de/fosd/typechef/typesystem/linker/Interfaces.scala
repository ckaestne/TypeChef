package de.fosd.typechef.typesystem.linker

import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprParser}
import de.fosd.typechef.featureexpr.FeatureExpr.dead
import java.io._
import de.fosd.typechef.typesystem.CType
import de.fosd.typechef.parser.Position


/**
 * describes the linker interface for a file, i.e. all imported (and used)
 * signatures and all exported signatures.
 */
case class CInterface(imports: Iterable[CSignature], exports: Iterable[CSignature]) {
    override def toString =
        "imports\n" + imports.map("\t" + _.toString).mkString("\n") +
                "\nexports\n" + exports.map("\t" + _.toString).mkString("\n") + "\n"

    def toXML: xml.Elem =
        <interface>
            {imports.map(x => <import>
            {x.toXML}
        </import>)}{exports.map(x => <export>
            {x.toXML}
        </export>)}
        </interface>

    /**
     * removes duplicates by joining the corresponding conditions
     * removes imports that are available as exports in the same file
     *
     * two elements are duplicate if they have the same name and type
     */
    def pack: CInterface = CInterface(packImports, exports)
    private def packImports: Iterable[CSignature] = {
        var importMap = Map[(String, CType), (FeatureExpr, Seq[Position])]()

        //eliminate duplicates with a map
        for (imp <- imports) {
            val key = (imp.name, imp.ctype)
            val old = importMap.getOrElse(key, (dead, Seq()))
            importMap = importMap + (key -> (old._1 or imp.fexpr, old._2 ++ imp.pos))
        }
        //eliminate imports that have corresponding exports
        for (exp <- exports) {
            val key = (exp.name, exp.ctype)
            if (importMap.contains(key)) {
                val (oldFexpr, oldPos) = importMap(key)
                val newFexpr = oldFexpr andNot exp.fexpr
                if (newFexpr.isSatisfiable())
                    importMap = importMap + (key -> (newFexpr, oldPos))
                else
                    importMap = importMap - key
            }
        }


        val r = for ((k, v) <- importMap.iterator)
        yield CSignature(k._1, k._2, v._1, v._2)
        r.toSeq
    }

}

/**
 * signature with name type and condition. the position is only stored for debugging purposes and has no further
 * relevance.
 * its also not necessarily de/serialized
 *
 * TODO types should be selfcontained (i.e. not reference to structures or type names defined elsewhere,
 * but resolved to anonymous structs, etc.)
 */
case class CSignature(name: String, ctype: CType, fexpr: FeatureExpr, pos: Seq[Position]) {
    override def toString =
        name + ": " + ctype + "\t\tif " + fexpr + "\t\tat " + pos.mkString(", ")
    def toXML: xml.Elem =
        <sig>
            <name>
                {name}
            </name>
            <type>
                {ctype.toXML}
            </type>
            <featureexpr>
                {fexpr.toTextExpr}
            </featureexpr>
            <pos>
                {pos.toString}
            </pos>
        </sig>
    override def hashCode = name.hashCode + ctype.hashCode()
    override def equals(that: Any) = that match {
        case CSignature(thatName, thatCType, thatFexpr, _) => name == thatName && ctype == thatCType && fexpr.equivalentTo(thatFexpr)
        case _ => false
    }

}
object CSignature {

    def fromXML(node: scala.xml.Node): CSignature = {
        val sig = node \ "sig"
        new CSignature(
            (sig \ "name").text.trim,
            CType.fromXML((sig \ "type")),
            new FeatureExprParser().parse((sig \ "featureexpr").text),
            Seq()
        )
    }

}
object CInterface {

    def fromXML(node: scala.xml.Node): CInterface = new CInterface(
        (node \ "import").map(CSignature.fromXML(_)),
        (node \ "export").map(CSignature.fromXML(_))
    )


}

trait Interfaces {


    def writeInterface(interface: CInterface, file: File) {
        val stream = new FileWriter(file)
        scala.xml.XML.write(stream, interface.toXML, "UTF-8", true, null)
        stream.close()
    }


    def debugInterface(interface: CInterface, file: File) {
        val stream = new FileWriter(file)
        stream.write(interface.toString)
        stream.close()
    }

    def readInterface(file: File): CInterface = {
        val loadnode = xml.XML.loadFile(file)
        CInterface.fromXML(loadnode)
    }

}