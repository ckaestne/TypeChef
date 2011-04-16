/*
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 17.03.11
 * Time: 16:13
 */
package de.fosd.typechef.linux

import de.fosd.typechef.typesystem.TypeSystem

object LinuxTypeChecker {
    def main(args: Array[String]): Unit =
        LinuxParser.main(args, new TypeSystem(LinuxFeatureModel.featureModelExcludingDead).checkAST(_))
}
