package de.fosd.typechef.typesystem.linker

import java.io.File
import de.fosd.typechef.featureexpr.{FeatureModel, FeatureExpr, FeatureExprParser}


object BusyboxHelp {
    def getBusyboxVM(): FeatureExpr = {
        var fm = FeatureExpr.base
        for (l: String <- io.Source.fromFile("S:\\ARCHIVE\\kos\\share\\TypeChef\\busybox\\featureModel").getLines())
            if (l != "") {
                val f = new FeatureExprParser().parse(l)
                fm = fm and f
            }
        fm
    }

    val path = "S:\\ARCHIVE\\kos\\share\\TypeChef\\cprojects\\busybox\\busybox-1.18.5\\"
    val filesfile = "S:\\ARCHIVE\\kos\\share\\TypeChef\\busybox\\busybox_files"
    val featuresfile = path + "features"

    var fileList = io.Source.fromFile(filesfile).getLines().toList
    val featureList = io.Source.fromFile(featuresfile).getLines().toList
}

object BusyboxLinker extends App {

    import BusyboxHelp._


    val vm = FeatureModel.create(getBusyboxVM())

    println("parsing")

    val reader = new InterfaceWriter() {}

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

            //            print(".")
            //            val conflicts = (left getConflicts right).filter(_._2.not.isSatisfiable(vm))
            //            if (!conflicts.isEmpty)
            //                println(conflicts)

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

    reader.writeInterface(finalInterface, new File("busyboxfinal.interface"))
    reader.debugInterface(finalInterface, new File("busyboxfinal.dbginterface"))

    //    println(finalInterface)
}


object TmpLinkerStuff extends App {

    import BusyboxHelp._

    val reader = new InterfaceWriter() {}
    var i = SystemLinker.linkStdLib(reader.readInterface(new File("busyboxfinal.interface")))

    val fm = getBusyboxVM()

    def d(s: String) = FeatureExpr.createDefinedExternal(s)

    println(fm.isSatisfiable())
    println((fm implies i.featureModel).isTautology())

    i = SystemLinker.conditionalLinkSelinux(i, d("CONFIG_SELINUX"))
    i = SystemLinker.conditionalLinkPam(i, d("CONFIG_PAM"))
    i = i.andFM(fm)
    i = i.pack

    println("packed")

    def excludeSymbol(sym: String) =
        sym.startsWith("BUG_") /*depends on compiler optimizations*/ ||
                (List("alloc_action", /*nested function, not inferred correctly right now*/
                    "x2x_utoa", //inference bug, function parameter
                    "adjust_width_and_validate_wc", //requires dead-code detection
                    "add_sun_partition", "bsd_select", "check2", "check_root2",
                    "create_sgiinfo", "get_prefix_", "data_extract_to_command", "del_loop",
                    "delete_block_backed_filesystems", "delete_eth_table", "erase_mtab",
                    "get_header_tar", "get_header_tar_bz2", "get_header_tar_gz", "get_header_tar_lzma",
                    "gpt_list_table", "nfsmount", "make_bad_inode2", "make_root_inode2" //if (0) dead-code detection
                    , "evaltreenr" //weired __attribute__
                ) contains sym)
    false

    for (imp <- i.imports.sortBy(_.name))
        if (imp.fexpr.isSatisfiable() && !excludeSymbol(imp.name))
            println(imp)


    println(i.imports.size)

}


object BusyboxStatistics extends App {

    import de.fosd.typechef.typesystem.linker.BusyboxHelp._

    val configFlagList = featureList.map(f => ("CONFIG_" + f.drop(f.lastIndexOf("/") + 1)))

    //    fileList=List("editors/diff")

    println("number of files: " + fileList.size + "; number of features: " + configFlagList.size)

    var filesPerFeature: Map[String /*Feature*/ , Set[String /*File*/ ]] = configFlagList.map(f => (f -> Set[String]())).toMap //token stream variability
    var filesPerFeatureI: Map[String /*Feature*/ , Set[String /*File*/ ]] = configFlagList.map(f => (f -> Set[String]())).toMap //interface variability
    var featuresPerFile: Map[String, Set[String]] = Map()
    var featuresPerFileI: Map[String, Set[String]] = Map()
    var pcs = List[FeatureExpr]()
    var interfaces = List[CInterface]()

    for (file <- fileList) {
        println(file)
        val dbg = io.Source.fromFile(path + file + ".dbg").getLines().toList
        val packg = file.take(file.lastIndexOf("/"))

        var featureLine = dbg.filter(_ startsWith "  Distinct Features:").head
        var features = featureLine.drop(21).split(";").toSet

        val pcFile = new File(path + file + ".pi.pc")
        val pc = if (pcFile.exists())
            new FeatureExprParser().parseFile(pcFile)
        else FeatureExpr.base
        pcs = pcs :+ pc

        //interface variability
        val i = new InterfaceWriter() {}.readInterface(new File(path + file + ".c.interface")) //.and(pc)
        interfaces = interfaces :+ SystemLinker.linkStdLib(i)

        var ifeatures = (i.exports ++ i.imports).flatMap(_.fexpr.collectDistinctFeatures).map(_.feature).toSet

        if (features.isEmpty)
            println(file + "  no variability!")

        featuresPerFile += (file -> features)
        featuresPerFileI += (file -> ifeatures)

        for (f <- features if (f != ""))
            filesPerFeature += (f -> (filesPerFeature.getOrElse(f, Set()) + file))
        for (f <- ifeatures if (f != ""))
            filesPerFeatureI += (f -> (filesPerFeatureI.getOrElse(f, Set()) + file))
    }

    val featuresPerSize = filesPerFeature.mapValues(_.size).groupBy(_._2)
    val featuresPerSizeI = filesPerFeatureI.mapValues(_.size).groupBy(_._2)

    def flagToFeature(flag: String): String = {
        val idx = configFlagList.indexOf(flag)
        if (idx < 0) {println("flag not found " + flag); ""} else
            featureList(idx)
    }

    //    println(
    //        filesPerFeature.map(x => (flagToFeature(x._1), x._2)).map(x => {
    //            val folder = x._1.take(x._1.lastIndexOf("/"))
    //            (x._1, (x._2.forall(_ startsWith folder), x._2))
    //        })
    //    )

    //    println(featuresPerFile)
    //    println(featuresPerFileI)

    //    number of files per feature
    //            println(filesPerFeatureI.mapValues(_.size).values.mkString(","))
    //            println(featuresPerSizeI.mapValues(_.keys.size).toList.sorted.mkString("\n"))
    //            println(featuresPerSizeI.mapValues(_.keys))

    //files per feature
    //    println(filesPerFeature.mapValues(_.size).toList.sortBy(_._2).mkString("\n"))


    //unique features per file
    //          println(filesPerFeature.filter(_._2.size==1).mapValues(_.head).toList.groupBy( _._2 ).mapValues(_.size) )


    //features exclusive to interfaces and inner implementations
    //    def printlnStat(a: Set[_]) {println(a.size + ": " + a.toString())}
    //    val pcFeatures = pcs.flatMap(_.collectDistinctFeatures.map(_.feature)).toSet
    //    val innerFeatures=filesPerFeature.filter(_._2.size>0).keys.toSet
    //    printlnStat(pcFeatures) //features in file presence conditions
    //    printlnStat(innerFeatures) //features inside any modules
    //    printlnStat(innerFeatures -- pcFeatures) // features exclusively inside modules
    //    printlnStat(pcFeatures -- innerFeatures) // features exclusively in presence conditions


}