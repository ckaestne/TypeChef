package de.fosd.typechef.lexer.macrotable

import junit.framework._
import junit.framework.Assert._
import org.junit.Test

import de.fosd.typechef.featureexpr.FeatureExpr
import de.fosd.typechef.featureexpr.FeatureExprFactory.sat._

class TestDefinedMacro extends TestCase {

  def a = createDefinedExternal("A")

  def b = createDefinedExternal("B")

  def c = createDefinedExternal("C")

  def d = createDefinedExternal("D")

  def x = createDefinedExternal("X")

  def y = createDefinedExternal("Y")

  def assertEquiv(a: FeatureExpr, b: FeatureExpr) = assertTrue("expected: " + a + " equivalentTo " + b, a equivalentTo b)

  @Test
  def testMacroTable() {
    var macroTable = new MacroContext[String]()

    macroTable = macroTable.define("X", b, "1")
    assertEquiv(b or x, macroTable.getMacroCondition("X"))

    macroTable = macroTable.define("X", c, "2")
    assertEquiv(b or c or x, macroTable.getMacroCondition("X"))

    macroTable = macroTable.undefine("X", d)
    assertEquiv(b or c or x and (d.not), macroTable.getMacroCondition("X"))

    macroTable = macroTable.undefine("X", True)
    assertEquiv(False, macroTable.getMacroCondition("X"))

    macroTable = macroTable.define("X", b, "1")
    assertEquiv(b, macroTable.getMacroCondition("X"))

    //        expectFail(macroTable.define("Y", createDefinedMacro("X"), "1"))

    assertEquiv(b or y, macroTable.define("Y", createDefinedMacro("X", macroTable), "1").getMacroCondition("Y"))
    assertEquiv(b or y, macroTable.define("Y", createDefinedMacro("X", macroTable), "1").getMacroCondition("Y"))

  }

  @Test
  def testMacroTable2() {
    var macroTable = new MacroContext[String](new MacroFilter().setPostfixFilter("_"))

    assertEquiv(x, macroTable.getMacroCondition("X"))
    macroTable = macroTable.undefine("X", b)
    assertEquiv(x andNot b, macroTable.getMacroCondition("X"))

    assertEquiv(False, macroTable.getMacroCondition("X_"))
    macroTable = macroTable.undefine("X_", b)
    assertEquiv(False, macroTable.getMacroCondition("X_"))


  }

  def getMacroTable = {
    var macroTable = new MacroContext[String]()

    //X if a | b
    //  -> 1 if b &!c
    //  -> 2 if c
    //Y if a | x
    //  -> 1 if a
    //Z if false

    macroTable = macroTable.undefine("X", True)
    macroTable = macroTable.define("X", b, "1")
    macroTable = macroTable.define("X", c, "2")
    macroTable = macroTable.define("Y", a, "1")
    macroTable = macroTable.undefine("Z", True)
    macroTable
  }


  //    @Test
  //    def testNFMacro() {
  //    	assertEquals(Set(new DefinedMacro("X")),createDefinedMacro("X").toCNF.findMacros)
  //    }

  @Test
  def testSatisfiability() {
    val macroTable = (getMacroTable)

    val x = createDefinedMacro("X", macroTable)
    assertTrue(x.isSatisfiable())
    assertFalse(x.isContradiction())
    assertTrue(createDefinedMacro("Z", macroTable).isContradiction())

  }

  @Test
  def testSatisfiability2() {
    var macroTable = new MacroContext[String]()
    macroTable = macroTable.undefine("X", True)
    macroTable = macroTable.define("X", a, "1")
    macroTable = macroTable.define("X", a.not and b, "2")
    macroTable = macroTable.define("X", a.not and b.not and c, "3")
    //        macroTable = macroTable.undefine("Y", True)
    //        macroTable = macroTable.define("Y", a, "1")
    macroTable = macroTable.define("GLOBAL", createDefinedMacro("X", macroTable), "")
    //        macroTable = macroTable.define("GLOBAL", createDefinedMacro("Y"), "")

    val u = macroTable.getMacroCondition("GLOBAL")

    assertFalse(u.isTautology())

    assertFalse(createDefinedMacro("GLOBAL", macroTable).isTautology())
    assertFalse(createDefinedMacro("GLOBAL", macroTable).isTautology())

  }

  @Test
  def testSatisfiability3() {
    var macroTable = new MacroContext[String]()
    macroTable = macroTable.undefine("X", True)

    assertFalse(createDefinedMacro("X", macroTable).isTautology())
    assertTrue(createDefinedMacro("X", macroTable).isContradiction())

    assertTrue(createDefinedMacro("X", macroTable).not.isTautology())
    assertFalse(createDefinedMacro("X", macroTable).not.isContradiction())

    assertTrue(createDefinedMacro("Y", macroTable).isSatisfiable())
    assertFalse(createDefinedMacro("Y", macroTable).isContradiction())
    assertFalse(createDefinedMacro("Y", macroTable).isTautology())

    assertFalse((createDefinedMacro("Y", macroTable) and createDefinedMacro("X", macroTable)).isSatisfiable())

    macroTable = macroTable.define("X", True, "")
    assertTrue((createDefinedMacro("Y", macroTable) and createDefinedMacro("X", macroTable)).isSatisfiable())
    assertFalse((createDefinedMacro("Y", macroTable) and createDefinedMacro("X", macroTable)).isTautology())
  }

  @Test
  def testOverTime() {
    var macroTable = new MacroContext[String]()
    macroTable = macroTable.undefine("X", True)

    val firstX = createDefinedMacro("X", macroTable) //false
    assertTrue(firstX.isContradiction)

    macroTable = macroTable.define("X", a, "")
    val secondX = createDefinedMacro("X", macroTable) //A
    assertTrue(firstX.isContradiction)
    assertTrue(secondX.isSatisfiable)

    macroTable = macroTable.define("X", a.not, "")
    val thirdX = createDefinedMacro("X", macroTable) //true
    assertTrue(firstX.isContradiction)
    assertTrue(secondX.isSatisfiable)
    assertTrue(thirdX.isTautology)

    macroTable = macroTable.undefine("X", True)
    macroTable = macroTable.define("X", a.not, "") //!A
    val fourthX = createDefinedMacro("X", macroTable)
    assertTrue(firstX.isContradiction)
    assertTrue(secondX.isSatisfiable)
    assertTrue(thirdX.isTautology)
    assertTrue(fourthX.isSatisfiable)
    assertTrue((fourthX or secondX).isTautology)
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
