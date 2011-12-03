package de.fosd.typechef.typesystem.linker

import java.io.File
import de.fosd.typechef.featureexpr.FeatureExprParser

object BusyboxLinker extends App {


    def getLinkFolder(folderPath: String): CInterface = {

        val ifiles = new File(folderPath).listFiles().filter(f => f.isFile && f.getName.endsWith(".c.interface")).toList

        val reader = new InterfaceWriter() {}
        var interfaces = ifiles.map(reader.readInterface(_)).map(SystemLinker.linkStdLib(_))
        val localFeatureConstraints = ifiles.map(f => new FeatureExprParser().parseFile(f.getAbsolutePath.dropRight(11) + "pi.fm"))
        interfaces = (interfaces zip localFeatureConstraints).map(x => x._1.and(x._2))

        interfaces map {i => assert(i.isWellformed, "illformed interface " + i)}

        val result = (ifiles zip interfaces).foldLeft[CInterface](EmptyInterface)((composedInterface, newFile) => {
            //            println("linking " + newFile._1)
            if (!(composedInterface isCompatibleTo newFile._2))
                println(composedInterface getConflicts newFile._2)
            composedInterface link newFile._2
        })
        result
    }

    val path = "S:\\ARCHIVE\\kos\\share\\TypeChef\\cprojects\\busybox\\busybox-1.18.5\\"

    val dirList = new File(path).listFiles().filter(_.isDirectory).toList
    //    val dirList = List(new File(path + "miscutils"))

    //modutils

    var finalInterface: CInterface = EmptyInterface

    val dirInterfaces = for (dir <- dirList) yield {
        val i = getLinkFolder(dir.getAbsolutePath)
        if (i.featureModel.isContradiction())
            println(i)
        println(dir + "; imported functions: " + i.imports.size + "; exported functions: " + i.exports.size + "; feature model: " + i.featureModel)
        finalInterface = finalInterface link i
        println("imported functions: " + finalInterface.imports.size + "; exported functions: " + finalInterface.exports.size + "; feature model: " + finalInterface.featureModel)
        i
    }


    val reader = new InterfaceWriter() {}
    reader.writeInterface(finalInterface, new File("busyboxfinal.interface"))
    reader.debugInterface(finalInterface, new File("busyboxfinal.dbginterface"))

    //    println(finalInterface)

}