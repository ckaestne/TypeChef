/*
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 17.03.11
 * Time: 16:13
 */
package de.fosd.typechef.linux

import de.fosd.typechef.typesystem.CTypeSystem
import de.fosd.typechef.parser.c.TranslationUnit

object LinuxTypeChecker {
    def main(args: Array[String]): Unit =
        LinuxParser.main(args, x=>new CTypeSystem(LinuxFeatureModel.featureModelExcludingDead).checkAST(x.asInstanceOf[TranslationUnit]))
}
