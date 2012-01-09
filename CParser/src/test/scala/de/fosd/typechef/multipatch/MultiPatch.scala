package de.fosd.typechef.multipatch

import org.junit.Test

class TestA {
  @Test
  def testEmpty {
    val mp = MultiPatch(List())
    val p = Patch(42, List())

    val result = mp.addPatch(p)

    assert(result == mp)
  }

  @Test
  def testComplex {
    val mp = MultiPatch(List())
    val p1 = Patch(27, List(Insert(0, "Hello"), Insert(0, "World")))
    val p2 = Patch(42, List(Insert(1, "Wonderfull")))
    val p3 = Patch(57, List(Delete(1), Insert(1, "Wonderful")))

    val result = mp.addPatch(p1).addPatch(p2).addPatch(p3)
    assert(result == MultiPatch(List((27, 2147483647, "Hello"), (57, 2147483647, "Wonderful"), (42, 57, "Wonderfull"), (27, 2147483647, "World"))))
  }
}