package de.fosd.typechef.featureexpr

import junit.framework._
import junit.framework.Assert._
import org.junit.Test

import FeatureExpr._

import FeatureExpr.createDefinedExternal

class TestDefinedMacro extends TestCase {

    def a = createDefinedExternal("A")
    def b = createDefinedExternal("B")
    def c = createDefinedExternal("C")
    def d = createDefinedExternal("D")
    def x = createDefinedExternal("X")
    def y = createDefinedExternal("Y")

    @Test
    def testMacroTable() {
        var macroTable = new MacroContext()

        macroTable = macroTable.define("X", b, "1")
        assertEquals(b or x, macroTable.getMacroCondition("X"))

        macroTable = macroTable.define("X", c, "2")
        assertEquals(b or c or x, macroTable.getMacroCondition("X"))

        macroTable = macroTable.undefine("X", d)
        assertEquals(b or c or x and (d.not), macroTable.getMacroCondition("X"))

        macroTable = macroTable.undefine("X", base)
        assertEquals(dead, macroTable.getMacroCondition("X"))

        macroTable = macroTable.define("X", b, "1")
        assertEquals(b, macroTable.getMacroCondition("X"))

        expectFail(macroTable.define("Y", createDefinedMacro("X"), "1"))

        macroTable = macroTable.define("Y", createDefinedMacro("X").resolveToExternal(macroTable), "1")
        assertEquals(b or y, macroTable.getMacroCondition("Y"))

    }

    def getMacroTable = {
        var macroTable = new MacroContext()

        //X if a | b
        //  -> 1 if b &!c
        //  -> 2 if c
        //Y if a | x
        //  -> 1 if a
        //Z if false

        macroTable = macroTable.undefine("X", base)
        macroTable = macroTable.define("X", b, "1")
        macroTable = macroTable.define("X", c, "2")
        macroTable = macroTable.define("Y", a, "1")
        macroTable = macroTable.undefine("Z", base)
        macroTable
    }

    @Test
    def testNFMacro() {
    	assertEquals(Set(new DefinedMacro("X")),createDefinedMacro("X").toCNF.findMacros)
    }
    
    @Test
    def testSatisfiability() {
        val macroTable=(getMacroTable)
        
        val x=createDefinedMacro("X")
        assertTrue(x.isSatisfiable(macroTable))
        assertFalse(x.isContradiction(macroTable))
        assertTrue(createDefinedMacro("Z").isContradiction(macroTable))
        
    }

    def expectFail(f: => Unit) =
        try {
            f
            fail("should not succeed")
        } catch {
            case e: AssertionError =>
        }

    //(A||B) && (!B|| !A)

}
