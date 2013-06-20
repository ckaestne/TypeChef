package de.fosd.typechef.multipatch


/**
 *
 *
 * deprecated
 *
 *
 */

abstract class Command(line: Int)

case class Delete(line: Int) extends Command(line)

case class Insert(line: Int, text: String) extends Command(line)

case class Patch(val version: Int, val lines: List[Command])

object Patch {
    def fromFile(filename: String): Patch = {
        null
    }
}

case class MultiPatch(val lines: List[(Int, Int, String)]) {
    def addPatch(patch: Patch): MultiPatch = {
        val result = List.newBuilder[(Int, Int, String)]
        var lineNumber = 0
        var deleted = false

        def processPatch(patch: Patch) =
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
