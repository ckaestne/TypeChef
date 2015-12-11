package de.fosd.typechef.featureexpr


import junit.framework.TestCase
import org.junit.Test

import scala.util.Random

/**
  * Created by ckaestne on 12/10/2015.
  */
class ConcurrencyTest extends TestCase {


    val nrFeatures = 100
    val featureNames = (0 to nrFeatures).map("feature" + _)

    val r = new Random()

    class RandomFeatureOp(threadId: Int) extends Runnable {
        var counter: Int = 2000
        var satCounter = 0
        var feature: FeatureExpr = FeatureExprFactory.True
        override def run(): Unit = {

            while (counter > 0) {

                if (r.nextBoolean())
                    feature = feature.not()
                else if (r.nextBoolean())
                    feature = feature and FeatureExprFactory.createDefinedExternal(featureNames(r.nextInt(nrFeatures)))
                else if (r.nextBoolean())
                    feature = feature or FeatureExprFactory.createDefinedExternal(featureNames(r.nextInt(nrFeatures)))

                if (feature.isSatisfiable())
                    satCounter += 1
                else {
                    feature = FeatureExprFactory.True
                    satCounter=0
                }

                counter -= 1
            }
            println(threadId+": done")
        }
    }


    @Test def testConcurrencySAT() {
        FeatureExprFactory.setDefault(FeatureExprFactory.sat)

        val threads = for (i <- 0 to 10)
            yield new Thread(new RandomFeatureOp(i))
        threads.foreach(_.start())
        threads.foreach(_.join())
        println("all done.")
    }

    @Test def testConcurrencyBDD() {
        FeatureExprFactory.setDefault(FeatureExprFactory.bdd)

        val threads = for (i <- 0 to 10)
            yield new Thread(new RandomFeatureOp(i))
        threads.foreach(_.start())
        threads.foreach(_.join())
        println("all done.")
    }


}
