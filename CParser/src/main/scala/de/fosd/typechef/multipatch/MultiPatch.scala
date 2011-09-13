package de.fosd.typechef.multipatch

import de.fosd.typechef.conditional.Opt
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: rendel
 * Date: 13.09.11
 * Time: 14:56
 * To change this template use File | Settings | File Templates.
 */

abstract class Command(line : Int)

case class Delete(line : Int) extends Command(line)

case class Insert(line : Int, text : String) extends Command(line)

case class Patch(val version: Int, val lines: List[Command])

object Patch {
  def fromFile(filename : String) : Patch = {
    null
  }
}

case class MultiPatch(val lines : List[(Int, Int, String)]) {
  def addPatch(patch : Patch) : MultiPatch = {
    val result = List.newBuilder[(Int, Int, String)]
    var lineNumber = 0
    var deleted = false

    def processPatch(patch : Patch) =
      for (thingy <- patch.lines) {
        thingy match {
          case Delete(line) if line == lineNumber =>
            deleted = true
          case Insert(line, text) if line == lineNumber =>
            result += ((patch.version, Int.MaxValue, text))
          case _ =>
        }
      }

    if (lines.isEmpty)
      processPatch(patch)
    else
      for (line <- lines) {
        deleted = false
        if (patch.version < line._2) {
          processPatch(patch)
          lineNumber += 1
        }
        result += ((line._1, if (deleted) patch.version else Int.MaxValue, line._3))
      }

    new MultiPatch(result.result)
  }
}

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
    assert(result == MultiPatch(List((27,2147483647,"Hello"), (57,2147483647,"Wonderful"), (42,57,"Wonderfull"), (27,2147483647,"World"))))
  }
}