package de.fosd.typechef.typesystem.linker

import java.io.File
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprParser}

object BusyboxLinker extends App {


    val path = "S:\\ARCHIVE\\kos\\share\\TypeChef\\cprojects\\busybox\\busybox-1.18.5\\"
    val filesfile = "S:\\ARCHIVE\\kos\\share\\TypeChef\\busybox\\busybox_files"

    println("parsing")

    val reader = new InterfaceWriter() {}

    val fileList = io.Source.fromFile(filesfile).getLines().toList
    println(fileList.size + " files")
    //    println(fileList.map(f => reader.readInterface(new File(path+f + ".c.interface"))))
    var interfaces = fileList.map(f => reader.readInterface(new File(path + f + ".c.interface"))).map(SystemLinker.linkStdLib(_))

    println("composing")

    var finalInterface: CInterface = EmptyInterface

    val t1 = System.currentTimeMillis()

    def linkTreewise(l: List[CInterface]): CInterface = {
        if (l.size > 2) {
            val m: Int = l.size / 2
            val left = l.take(m)
            val right = l.drop(m)
            linkTreewise(List(linkTreewise(left), linkTreewise(right)))
        }
        else if (l.size == 2) {
            val left = l(0)
            val right = l(1)
            if (!(left isCompatibleTo right))
                println(left getConflicts right)
            left link right
        } else if (l.size == 1) l(0)
        else {
            assert(false, l)
            EmptyInterface
        };

    }


    def linkIncrementally(l: List[CInterface]): CInterface = l.fold(EmptyInterface)((left, right) => {
        if (!(left isCompatibleTo right))
            println(left getConflicts right)
        left link right
    })


    finalInterface = linkTreewise(interfaces)

    val t2 = System.currentTimeMillis()

    println("total composition time: " + (t2 - t1))

    finalInterface = finalInterface.pack

    println("total composition time: " + (t2 - t1))

    reader.writeInterface(finalInterface, new File("busyboxfinal.interface"))
    reader.debugInterface(finalInterface, new File("busyboxfinal.dbginterface"))

    println(finalInterface)

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
    val featuresfile = path + "features"

    val fileList = io.Source.fromFile(filesfile).getLines().toList
    val featureList = io.Source.fromFile(featuresfile).getLines().toList
    val configFlagList = featureList.map(f => ("CONFIG_" + f.drop(f.lastIndexOf("/") + 1)))

    println("number of files: " + fileList.size + "; number of features: " + configFlagList.size)

    var filesPerFeature: Map[String /*Feature*/ , Set[String /*File*/ ]] = configFlagList.map(f => (f -> Set[String]())).toMap //token stream variability
    var filesPerFeatureI: Map[String /*Feature*/ , Set[String /*File*/ ]] = configFlagList.map(f => (f -> Set[String]())).toMap //interface variability
    var featuresPerFile: Map[String, Set[String]] = Map()
    var featuresPerFileI: Map[String, Set[String]] = Map()
    var pcs = List[FeatureExpr]()
    var interfaces = List[CInterface]()

    for (file <- fileList) {
        val dbg = io.Source.fromFile(path + file + ".dbg").getLines().toList
        val packg = file.take(file.lastIndexOf("/"))

        var featureLine = dbg.filter(_ startsWith "  Distinct Features:").head
        var features = featureLine.drop(21).split(";").toSet

        val pcFile = new File(path + file + ".pi.pc")
        val pc = if (pcFile.exists())
            new FeatureExprParser().parseFile(pcFile)
        else FeatureExpr.base
        features = features ++ pc.collectDistinctFeatures.map(_.feature)
        pcs = pcs :+ pc

        //interface variability
        val i = new InterfaceWriter() {}.readInterface(new File(path + file + ".c.interface")).and(pc)
        interfaces = interfaces :+ SystemLinker.linkStdLib(i)

        val ifeatures = (i.exports ++ i.imports).flatMap(_.fexpr.collectDistinctFeatures).map(_.feature).toSet

        featuresPerFile += (file -> features)
        featuresPerFileI += (file -> ifeatures)

        for (f <- features)
            filesPerFeature += (f -> (filesPerFeature.getOrElse(f, Set()) + file))
        for (f <- ifeatures)
            filesPerFeatureI += (f -> (filesPerFeatureI.getOrElse(f, Set()) + file))
    }

    val featuresPerSize = filesPerFeature.mapValues(_.size).groupBy(_._2)
    val featuresPerSizeI = filesPerFeatureI.mapValues(_.size).groupBy(_._2)

    def flagToFeature(flag: String): String = {
        val idx = configFlagList.indexOf(flag)
        if (idx < 0) {println("flag not found " + flag); ""} else
            featureList(idx)
    }

    println(
        filesPerFeature.map(x => (flagToFeature(x._1), x._2)).map(x => {
            val folder = x._1.take(x._1.lastIndexOf("/"))
            (x._1, (x._2.forall(_ startsWith folder), x._2))
        })
    )

    //    println(featuresPerFile)
    //    println(featuresPerFileI)

    //number of files per feature
    //    println(filesPerFeatureI.mapValues(_.size).values.mkString(","))
    //    println(featuresPerSizeI.mapValues(_.keys.size).toList.sorted.mkString("\n"))
    //    println(featuresPerSizeI.mapValues(_.keys))

    //files per feature
    //    println(filesPerFeature.mapValues(_.size).toList.sortBy(_._2).mkString("\n"))


    //unique features per file
    //      println(filesPerFeature.filter(_._2.size==1).mapValues(_.head).toList.groupBy(_.2))

}