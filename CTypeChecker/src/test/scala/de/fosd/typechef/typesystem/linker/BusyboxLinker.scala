package de.fosd.typechef.typesystem.linker

import java.io.File
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprParser}

object BusyboxLinker extends App {


    val path = "S:\\ARCHIVE\\kos\\share\\TypeChef\\cprojects\\busybox\\busybox-1.18.5\\"
    val filesfile = "S:\\ARCHIVE\\kos\\share\\TypeChef\\busybox\\busybox_files"


    println("parsing")


    val fileList = io.Source.fromFile(filesfile).getLines().toList
    var interfaces = fileList.map(f => reader.readInterface(new File(f + ".c.interface"))).map(SystemLinker.linkStdLib(_))


    println("composing")

    var finalInterface: CInterface = EmptyInterface

    val t1 = System.currentTimeMillis()
    for (i <- interfaces) {
        if (!(finalInterface isCompatibleTo i))
            println(finalInterface getConflicts i)
        finalInterface = finalInterface link i


    }

    val t2 = System.currentTimeMillis()

    println("total composition time: " + (t2 - t1))

    val reader = new InterfaceWriter() {}
    reader.writeInterface(finalInterface, new File("busyboxfinal.interface"))
    reader.debugInterface(finalInterface, new File("busyboxfinal.dbginterface"))

    //    println(finalInterface)

}

object TmpLinkerStuff extends App {
    val reader = new InterfaceWriter() {}
    val i = SystemLinker.linkStdLib(reader.readInterface(new File("busyboxfinal.interface")))

    var fm = FeatureExpr.base
    for (l: String <- io.Source.fromFile("S:\\ARCHIVE\\kos\\share\\TypeChef\\busybox\\featureModel").getLines())
        if (l != "") {
            val f = new FeatureExprParser().parse(l)
            fm = fm and f
        }

    println(fm.isSatisfiable())
    println((fm implies i.featureModel).isTautology())

    val ii = i.andFM(fm).pack

    println("packed")

    def excludeSymbol(sym: String) =
        sym.startsWith("BUG_") /*depends on compiler optimizations*/ ||
                sym == "alloc_action" || /*nested function, not inferred correctly right now*/
                sym == "x2x_utoa" || //inference bug, function parameter
                sym == "adjust_width_and_validate_wc" || //requires static analysis
                false

    for (imp <- ii.imports.sortBy(_.name))
        if (imp.fexpr.isSatisfiable() && !excludeSymbol(imp.name))
            println(imp)


    println(ii.imports.size)

}


object BusyboxStatistics extends App {

    val path = "S:\\ARCHIVE\\kos\\share\\TypeChef\\cprojects\\busybox\\busybox-1.18.5\\"
    val filesfile = "S:\\ARCHIVE\\kos\\share\\TypeChef\\busybox\\busybox_files"

    val fileList = io.Source.fromFile(filesfile).getLines().toList

    println("number of files: " + fileList.size)

    val featureList = List[String]()

    var filesPerFeature: Map[String /*Feature*/ , Set[String /*File*/ ]] = featureList.map(f => (f, Set[String]())).toMap

    for (file <- fileList) {
        val dbg = io.Source.fromFile(path + file + ".dbg").getLines().toList
        val packg = file.take(file.lastIndexOf("/"))

        var featureLine = dbg.filter(_ startsWith "  Distinct Features:").head
        var features = featureLine.drop(21).split(";").toList

        for (f <- features)
            filesPerFeature += (f -> (filesPerFeature.getOrElse(f, Set()) + file))


    }
    println(filesPerFeature.mapValues(_.size).toList.sortBy(_._2).mkString("\n"))


}