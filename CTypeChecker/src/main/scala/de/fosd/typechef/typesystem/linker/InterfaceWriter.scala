package de.fosd.typechef.typesystem.linker

import java.io._
import de.fosd.typechef.typesystem.CType
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprParser}

trait InterfaceWriter {


    def writeInterface(interface: CInterface, file: File) {
        val stream = new FileWriter(file)
        scala.xml.XML.write(stream, interfaceToXML(interface), "UTF-8", true, null)
        stream.close()
    }


    def debugInterface(interface: CInterface, file: File) {
        val stream = new FileWriter(file)
        stream.write(interface.toString)
        stream.close()
    }

    def readInterface(file: File): CInterface = {
        val loadnode = xml.XML.loadFile(file)
        interfaceFromXML(loadnode)
    }


    def interfaceFromXML(node: scala.xml.Node): CInterface = new CInterface(
        getFM(node),
        (node \ "import").map(signatureFromXML(_)),
        (node \ "export").map(signatureFromXML(_))
    )
    private def getFM(node:scala.xml.Node)={
        val txt=(node \ "featuremodel").text
        if (txt.trim=="")
            FeatureExpr.base
        else new FeatureExprParser().parse(txt)
    }

    def interfaceToXML(int: CInterface): xml.Elem =
        <interface>
            <featuremodel>
                {int.featureModel.toTextExpr}
            </featuremodel>{int.imports.map(x => <import>
            {signatureToXML(x)}
        </import>)}{int.exports.map(x => <export>
            {signatureToXML(x)}
        </export>)}
        </interface>


    def signatureToXML(sig: CSignature): xml.Elem =
        <sig>
            <name>
                {sig.name}
            </name>
            <type>
                {sig.ctype.toXML}
            </type>
            <featureexpr>
                {sig.fexpr.toTextExpr}
            </featureexpr>
            <pos>
                {sig.pos.toString}
            </pos>
        </sig>


    def signatureFromXML(node: scala.xml.Node): CSignature = {
        val sig = node \ "sig"
        new CSignature(
            (sig \ "name").text.trim,
            CType.fromXML((sig \ "type")),
            new FeatureExprParser().parse((sig \ "featureexpr").text),
            Seq()
        )
    }


}