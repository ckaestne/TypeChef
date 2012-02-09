package de.fosd.typechef

import net.sf.javabdd._

/**
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 09.02.12
 * Time: 11:25
 * To change this template use File | Settings | File Templates.
 */

object BDDTest extends App {

    val bddCacheSize = 1000
    var bddValNum = 1000
    var maxFeatureId = -1
    val bddFactory = BDDFactory.init(bddValNum, bddCacheSize)

    val v1 = bddFactory.ithVar(1)
    val v2 = bddFactory.ithVar(2)
    println(v1)
    println(v2)

    val a = v1 and v2

    println(a)

}
