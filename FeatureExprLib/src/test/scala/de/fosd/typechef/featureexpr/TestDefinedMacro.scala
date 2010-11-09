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

//        expectFail(macroTable.define("Y", createDefinedMacro("X"), "1"))

        assertEquals(b or y,  macroTable.define("Y", createDefinedMacro("X").resolveToExternal(macroTable), "1").getMacroCondition("Y"))
        assertEquals(b or y,  macroTable.define("Y", createDefinedMacro("X"), "1").getMacroCondition("Y"))

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
    def testDebugCNF() {
    	val x=(a.not and b) 
    	val y=(a.not and b.not and c)
    	y.toCNF
    	val dd=y.not
    	d.toCNF
    	val z=x and (y.not)
    	println(z)
    	assertFalse(z.isBase(null))
    	assertFalse(z.isContradiction(null))
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
    
        @Test
    def testSatisfiability2() {
        var macroTable=new MacroContext()
        macroTable = macroTable.undefine("X", base)
        macroTable = macroTable.define("X", a, "1")
        macroTable = macroTable.define("X", a.not and b, "2")
        macroTable = macroTable.define("X", a.not and b.not and c, "3")
//        macroTable = macroTable.undefine("Y", base)
//        macroTable = macroTable.define("Y", a, "1")
        macroTable = macroTable.define("GLOBAL", createDefinedMacro("X"), "")
//        macroTable = macroTable.define("GLOBAL", createDefinedMacro("Y"), "")
        
        val u=macroTable.getMacroCondition("GLOBAL")
        val x=u.resolveToExternal(macroTable);
        val y=new FeatureExprImpl(x.expr.toCNF)
        

        assertFalse(u.isTautology(macroTable))
        assertFalse(x.isTautology(macroTable))
        assertFalse(y.isTautology(macroTable))
        
        assertFalse(createDefinedMacro("GLOBAL").isTautology(macroTable))
        assertFalse(createDefinedMacro("GLOBAL").isTautology(macroTable))
        
        
    }
        
        
       @Test
    def testSatisfiability3() {
        var macroTable=new MacroContext()
        macroTable = macroTable.undefine("X", base)
        
        assertFalse(createDefinedMacro("X").isTautology(macroTable))
        assertTrue(createDefinedMacro("X").isContradiction(macroTable))

        assertTrue(createDefinedMacro("X").not.isTautology(macroTable))
        assertFalse(createDefinedMacro("X").not.isContradiction(macroTable))
        
        assertTrue(createDefinedMacro("Y").isSatisfiable(macroTable))
        assertFalse(createDefinedMacro("Y").isContradiction(macroTable))
        assertFalse(createDefinedMacro("Y").isTautology(macroTable))

        assertFalse((createDefinedMacro("Y") and createDefinedMacro("X")).isSatisfiable(macroTable))

        macroTable = macroTable.define("X", base, "")
        assertTrue((createDefinedMacro("Y") and createDefinedMacro("X")).isSatisfiable(macroTable))
        assertFalse((createDefinedMacro("Y") and createDefinedMacro("X")).isTautology(macroTable))
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
