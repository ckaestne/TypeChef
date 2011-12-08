package de.fosd.typechef.typesystem.linker

/**
 * adhoc reader for KConfig (busybox subset)
 */

object KConfigReader extends App {

    val path = "S:\\ARCHIVE\\kos\\share\\TypeChef\\cprojects\\busybox\\busybox-1.18.5\\"

    var config = io.Source.fromFile(path + "Config.in").getLines().toList

    var flag = ""
    var skipHelp = false
    var flagType = ""
    var flagDep = ""
    var menuStack = List[String]()

    var features = List[String]()

    def processConfig() {
        if (flag == "") return;
        if (flagType == "bool") {
            println(dir + "/" + flag /*+ " => " + flagDep*/)

            //            println("#ifdef CONFIG_"+flag+"\n" +
            //                    "   #define ENABLE_"+flag+" 1\n" +
            //                    "   #define IF_"+flag+"(...) __VA_ARGS__\n" +
            //                    "   #define IF_NOT_"+flag+"(...)\n" +
            //                    "#else\n" +
            //                    "   #define ENABLE_"+flag+" 0\n" +
            //                    "   #define IF_NOT_"+flag+"(...) __VA_ARGS__\n" +
            //                    "   #define IF_"+flag+"(...)\n" +
            //                    "#endif")

            features = flag :: features
        }
        flag = ""
        skipHelp = false
        flagType = ""
        flagDep = ""
    }

    var dir = ""

    while (!config.isEmpty) {
        val line = config.head
        config = config.tail

        var skip = false
        if (line.trim == "" || line.trim.startsWith("#") || line.trim.startsWith("comment") || line.trim.startsWith("mainmenu")) skip = true

        if (!skip && line.startsWith("config")) {
            processConfig()

            flag = line.drop(7).trim
        }
        if (!skip && line.startsWith("menu")) {
            //            println("<menu " + line.drop(5) + ">")
            menuStack = line.drop(5) :: menuStack
        }
        if (!skip && line.startsWith("endmenu")) {
            //            println("</menu " + menuStack(0) + ">")
            menuStack = menuStack.tail
        }

        if (!skip && line.startsWith("source")) {
            processConfig()
            val file = path + line.drop(7)
            dir = line.drop(7).take(line.drop(7).lastIndexOf("/"))
            config =
                    io.Source.fromFile(file).getLines().toList ++ config

        }


        if (!skip && !skipHelp && flag != "" && line.trim() == ("help"))
            skipHelp = true

        if (!skip && !skipHelp && flag != "" && line.trim().startsWith("bool"))
            flagType = "bool"

        if (!skip && !skipHelp && flag != "" && line.trim().startsWith("depends on"))
            flagDep = line.trim.drop(11)

    }

    processConfig()

    assert(menuStack.isEmpty)

    println(features.size + " - " + features.toSet.size)

}

