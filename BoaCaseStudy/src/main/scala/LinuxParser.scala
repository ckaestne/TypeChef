/**
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 10.02.11
 * Time: 10:34
 * To change this template use File | Settings | File Templates.
 */

import de.fosd.typechef.featureexpr.FeatureModel
import de.fosd.typechef.typesystem.TypeSystem
import de.fosd.typechef.parser.c._

object LinuxParser {

    def main(args: Array[String]) = {
        LinuxParserMain.main(args, new TypeSystem(
            FeatureModel.createFromDimacsFile_2Var("2.6.33.3-2var.dimacs")
        ).checkAST(_))
    }

    //    val filenames=Source.fromFile("d:/work/linuxpi/files").getLines
    //    for (filename<-filenames)
    //        LinuxParserMain.main(Array[String]("d:/work/linuxpi/"+filename))

}