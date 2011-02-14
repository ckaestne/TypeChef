/**
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 10.02.11
 * Time: 10:34
 * To change this template use File | Settings | File Templates.
 */

import de.fosd.typechef.typesystem.TypeSystem
import de.fosd.typechef.parser.c._

object LinuxParser {

    def main(args: Array[String]) = {
        LinuxParserMain.main(args, new TypeSystem().checkAST(_))
    }

    //    val filenames=Source.fromFile("d:/work/linuxpi/files").getLines
    //    for (filename<-filenames)
    //        LinuxParserMain.main(Array[String]("d:/work/linuxpi/"+filename))

}