package de.fosd.typechef.parser

object DebugSplitting {
    val DEBUG_SPLITTING = true
    def apply(msg: String) = if (DEBUG_SPLITTING) println(msg)
}