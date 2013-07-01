package de.fosd.typechef.crewrite

import de.fosd.typechef.featureexpr.FeatureExprFactory._
import org.junit.Test
import java.io.{FileWriter, File}
import junit.framework.Assert


class CFGSerializationTest {
    val fa = createDefinedExternal("A")

    val n1 = new CFGNode(1, "declaration", null, 1, "foo", True)
    val n2 = new CFGNode(2, "statement", null, 1, "foo()", True)
    val f1 = new FileCFG(Set(n1, n2), Set((n2, n1, True)))

    val d1 = new CFGNode(3, "function", null, 1, "foo", fa)
    val f2 = new FileCFG(Set(d1), Set())


    val ff = f1.link(f2)

    @Test def testSerialization() {
        val f = File.createTempFile("cfg", ".cfg")
        f.deleteOnExit()
        val w = new FileWriter(f)
        ff.write(w)
        w.close()

        val cfg = WholeProjectCFG.loadCFG(f)

        Assert.assertEquals(ff, cfg)

    }
}