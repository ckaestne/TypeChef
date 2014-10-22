package de.fosd.typechef.options

import java.io.{FileInputStream, File}
import gnu.getopt.{Getopt, LongOpt}
import java.util.{ArrayList, Properties}


class FrontendOptionsWithConfigFiles extends FrontendOptions {
    ////////////////////////////////////////
    // General setup of built-in headers, should become more general and move
    // elsewhere.
    ////////////////////////////////////////
    val predefSettings = new Properties()
    val settings = new Properties(predefSettings)

    def systemRoot = settings.getProperty("systemRoot")

    def systemIncludes = settings.getProperty("systemIncludes")

    def predefMacroDef = settings.getProperty("predefMacros")

    def setSystemRoot(value: String) = settings.setProperty("systemRoot", value)

    def setSystemIncludes(value: String) = settings.setProperty("systemIncludes", value)

    def setPredefMacroDef(value: String) = settings.setProperty("predefMacros", value)

    var preIncludeDirs: Seq[String] = Nil
    var postIncludeDirs: Seq[String] = Nil

    def initSettings {
        predefSettings.setProperty("systemRoot", File.separator)
        predefSettings.setProperty("systemIncludes", "usr" + File.separator + "include")
        predefSettings.setProperty("predefMacros", "")
    }

    def loadPropList(key: String) = for (x <- settings.getProperty(key, "").split(",")) yield x.trim

    def loadSettings(configPath: String) = {
        settings.load(new FileInputStream(configPath))
        preIncludeDirs = loadPropList("preIncludes") ++ preIncludeDirs
//        println("preIncludes: " + preIncludeDirs)
//        println("systemIncludes: " + systemIncludes)
        postIncludeDirs = postIncludeDirs ++ loadPropList("postIncludes")
//        println("postIncludes: " + postIncludeDirs)
    }

    override def parseOptions(args: Array[String]) = {
        initSettings
        super.parseOptions(args)
    }


    private final val SYSINCL: Char = Options.genOptionId()

    protected override def getOptionGroups() = {
        import Options.OptionGroup
        import Options.Option

        val groups = new ArrayList[OptionGroup](super.getOptionGroups)

        groups.add(new OptionGroup("System configuration", 8,
            new Option("systemRoot", LongOpt.REQUIRED_ARGUMENT, 'r', "dir",
                "Path to system root. Default: '/'."),
            new Option("platfromHeader", LongOpt.REQUIRED_ARGUMENT, 'h', "file",
                "Header files with platform macros (create with 'cpp -dM -std=gnu99 -'). Default: 'platform.h'."),
            new Option("systemIncludes", LongOpt.REQUIRED_ARGUMENT, SYSINCL, "dir",
                "System include directory. Default: '$systemRoot/usr/include'."),
            new Option("settingsFile", LongOpt.REQUIRED_ARGUMENT, 'c', "dir",
                "Property file specifying system root, platform headers, and system include directories.")
        ))
        groups
    }

    override protected def interpretOption(c: Int, g: Getopt) = {
        if (c == 'r') {
            checkDirectoryExists(g.getOptarg)
            setSystemRoot(g.getOptarg)
            true
        } else if (c == 'h') {
            checkFileExists(g.getOptarg)
            setPredefMacroDef(g.getOptarg)
            true
        } else if (c == 'c') {
            checkFileExists(g.getOptarg)
            loadSettings(g.getOptarg)
            true
        } else if (c == SYSINCL) {
            checkDirectoryExists(g.getOptarg)
            setSystemIncludes(g.getOptarg)
            true
        } else
            super.interpretOption(c, g)
    }

    override def getIncludePaths() = {
        val r = new ArrayList[String](super.getIncludePaths)
        val r2 = (preIncludeDirs ++ List(systemIncludes) ++ postIncludeDirs).flatMap(
            (path: String) =>
                if (path != null && !("" equals path))
                    List(systemRoot + File.separator + path)
                else
                    List()
        )
        for (a <- r2)
            r.add(a)
        r
    }

    override def getIncludedHeaders = {
        if (predefMacroDef.length() > 0 && new File(predefMacroDef).exists()) {
            val r = new ArrayList[String](super.getIncludedHeaders)
            r.add(0, predefMacroDef)
            r
        } else super.getIncludedHeaders
    }

    protected override def afterParsing: Unit = {
        super.afterParsing()
        if (!isPrintVersion && (predefMacroDef.length() == 0 || !new File(predefMacroDef).exists()))
            println("Warning: No platform header specified")
    }
}