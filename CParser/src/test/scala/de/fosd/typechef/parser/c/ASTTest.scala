package de.fosd.typechef.parser.c

import org.junit.Test
import de.fosd.typechef.parser.Position

class ASTTest {

    @Test
    def testCloning {
        val n = Id("foo")
        val cn = n.clone()

        assert(n equals cn)
        assert(!(n eq cn))
        assert(n.hasPosition equals cn.hasPosition)
    }

    @Test
    def testCloningNested {
        val n = Id("foo")
        val p = PointerPostfixSuffix("a", n)
        assert(n.parent eq p)
        val cp = p.clone().asInstanceOf[PointerPostfixSuffix]
        val cn = cp.id

        assert(p equals cp)
        assert(!(p eq cp))
        assert(n.parent eq p)

        assert(n equals cn)
        //        assert(!(n eq cn))
    }

    @Test
    def testCloningWithPosition {
        val n = Id("foo")
        n.setPositionRange(new Position {
            def getColumn: Int = 1
            def getLine: Int = 2
            def getFile: String = null
        }, new Position {
            def getColumn: Int = 10
            def getLine: Int = 2
            def getFile: String = null
        })

        val cn = n.clone()

        assert(n.getPositionFrom equals cn.getPositionFrom)
    }


}