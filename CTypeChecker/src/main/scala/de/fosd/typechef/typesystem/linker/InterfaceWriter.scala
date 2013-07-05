package de.fosd.typechef.typesystem.linker

import java.io._
import de.fosd.typechef.typesystem.CType
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExprParser}
import de.fosd.typechef.error.Position

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
        (node \ "feature").map(_.text.trim).toSet,
        (node \ "newfeature").map(_.text.trim).toSet,
        (node \ "import").map(signatureFromXML(_)),
        (node \ "export").map(signatureFromXML(_))
    )
    private def getFM(node: scala.xml.Node) = {
        val txt = (node \ "featuremodel").text
        if (txt.trim == "")
            FeatureExprFactory.True
        else new FeatureExprParser().parse(txt)
    }

    def interfaceToXML(int: CInterface): xml.Elem =
        <interface>
            <featuremodel>
                {int.featureModel.toTextExpr}
            </featuremodel>{int.importedFeatures.map(x => <feature>
            {x}
        </feature>)}{int.declaredFeatures.map(x => <newfeature>
            {x}
        </newfeature>)}{int.imports.map(x => <import>
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
            </featureexpr>{sig.pos.map(posToXML(_))}{for (extraFlag <- sig.extraFlags) yield <extraFlag name={extraFlag.toString}/>}
        </sig>


    private def signatureFromXML(node: scala.xml.Node): CSignature = {
        val sig = node \ "sig"
        new CSignature(
            (sig \ "name").text.trim,
            CType.fromXML((sig \ "type")),
            new FeatureExprParser().parse((sig \ "featureexpr").text),
            (sig \ "pos").map(positionFromXML(_)),
            (sig \ "extraFlag").flatMap(extraFlagFromXML(_)).filter(_.isDefined).map(_.get).toSet
        )
    }
    private def positionFromXML(node: scala.xml.Node): Position = {
        val col = (node \ "col").text.trim.toInt
        val line = (node \ "line").text.trim.toInt
        val file = (node \ "file").text.trim
        new Position() {
            def getColumn: Int = col
            def getLine: Int = line
            def getFile: String = file
        }
    }
    private def extraFlagFromXML(node: scala.xml.Node): Seq[Option[CFlag]] = {
        (node \ "@name").map(n => if (n.text == "WeakExport") Some(WeakExport) else None)
    }
    private def posToXML(p: Position) =
        <pos>
            <file>
                {p.getFile}
            </file>
            <line>
                {p.getLine}
            </line>
            <col>
                {p.getColumn}
            </col>
        </pos>


}