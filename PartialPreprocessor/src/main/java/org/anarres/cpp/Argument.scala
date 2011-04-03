/*
 * Anarres C Preprocessor
 * Copyright (c) 2007-2008, Shevek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.anarres.cpp

import java.io.IOException
import java.util.ArrayList
import java.util.Collections
import java.util.Iterator
import java.util.List
import collection.JavaConversions.asScalaIterable

/**
 * A macro argument.
 *
 * This encapsulates a raw and preprocessed token stream.
 */
object Argument {
    def omittedVariadicArgument: Argument = {
        var a: Argument = new Argument
        a.omittedArg = true
        a.expanded = Collections.emptyList()
        return a
    }

    final val NO_ARGS: Int = -1
}

class Argument extends ArrayList[Token] {
    private var omittedArg: Boolean = false
    private var expanded: List[Token] = null

    def addToken(tok: Token): Unit = {
        if (!omittedArg) {
            add(tok)
        }
        else {
            throw new IllegalArgumentException("Tried to add a token to omittedVariadicArgument.")
        }
    }

    def isOmittedArg: Boolean = {
        return omittedArg
    }

    private[cpp] def expand(p: Preprocessor, inlineCppExpression: Boolean, macroName: String): Unit = {
        if (expanded == null) {
            this.expanded = p.macro_expandArgument(this, inlineCppExpression, macroName)
        }
    }

    def expansion(): Iterator[Token] = {
        return expanded.iterator
    }

    override def toString: String = {
        val buf: StringBuilder = new StringBuilder
        buf.append("Argument(")
        buf.append("raw=[ ")
        for (tok <- this)
            buf.append(tok.getText)
        buf.append(" ];expansion=[ ")
        if (expanded == null) buf.append("null")
        else {
            for (tok <- expanded)
                buf.append(tok.getText)
        }
        buf.append(" ])")
        return buf.toString
    }
}
