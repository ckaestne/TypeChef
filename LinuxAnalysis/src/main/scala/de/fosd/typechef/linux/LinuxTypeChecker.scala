/*
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 17.03.11
 * Time: 16:13
 */
package de.fosd.typechef.linux

import de.fosd.typechef.parser.c.TranslationUnit
import de.fosd.typechef.typesystem.{CTypeSystemFrontend, CTypeSystem}

object LinuxTypeChecker {
    def main(args: Array[String]): Unit =
        LinuxParser.main(args, {x => new CTypeSystemFrontend(x.asInstanceOf[TranslationUnit] /*, LinuxFeatureModel.featureModelExcludingDead*/).checkAST})
}
