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
            println("linking " + newFile._1)
            if (!(composedInterface isCompatibleTo newFile._2))
                println((composedInterface getConflicts newFile._2).mapValues(_.map(_.pos)))
            composedInterface link newFile._2
        })
        println(result)
        result
    }

    val path = "S:\\ARCHIVE\\kos\\share\\TypeChef\\cprojects\\busybox\\busybox-1.18.5\\"

    //    val dirList=new File(path).listFiles().filter(_.isDirectory).toList
    val dirList = List(new File(path + "miscutils"))

    for (dir <- dirList) {
        val i = getLinkFolder(dir.getAbsolutePath)
        println(i)
        println(dir + " i: " + i.imports.size + " e: " + i.exports.size)
        println("done.")
    }

}