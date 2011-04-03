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

import java.{util => jUtil, lang => jLang}
import collection.JavaConversions._
import collection.JavaConverters._

/**
 * A macro argument.
 *
 * This encapsulates a raw and preprocessed token stream.
 */
object MacroArg {
    def omittedVariadicArgument: Argument = OmittedVariadicArgument
    def create(toks: jUtil.List[Token]) = NormArgument(toks)
    //Untested.
    def fromSources(sources: jLang.Iterable[org.anarres.cpp.Source]) =
        NormArgument(sources.asScala.toSeq.flatMap(_.asScala))
}

sealed abstract class Argument(omitted: Boolean) {
    def isOmittedArg: Boolean = omitted
    def tokens: Seq[Token]
    def jTokens = tokens.asJava
    def expandedTokens: Seq[Token]
    def expansion: jUtil.Iterator[Token] = expandedTokens.iterator

    private[cpp] def expand(p: Preprocessor, inlineCppExpression: Boolean, macroName: String) {}

    override def toString: String = {
        val buf: StringBuilder = new StringBuilder
        buf.append("Argument(")
        buf.append("raw=[ ")
        for (tok <- tokens)
            buf.append(tok.getText)
        buf.append(" ];expansion=[ ")
        if (expandedTokens.isEmpty) buf.append("null")
        else {
            for (tok <- expandedTokens)
                buf.append(tok.getText)
        }
        buf.append(" ])")
        return buf.toString
    }
}

case object OmittedVariadicArgument extends Argument(true) {
    def expandedTokens = Seq()
    def tokens = Seq()
}

case class NormArgument(tokens: Seq[Token]) extends Argument(false) {
    private var expanded: Seq[Token] = Seq()

    private var omittedArg: Boolean = false
    override private[cpp] def expand(p: Preprocessor, inlineCppExpression: Boolean, macroName: String) {
        if (expanded.isEmpty)
            this.expanded = p.macro_expandArgument(tokens, inlineCppExpression, macroName).asScala.toSeq
    }

    def expandedTokens = expanded
}
