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

import java.io._
import java.util.Iterator
import java.util.List
import org.anarres.cpp.Token._

object MacroTokenSource {
    private[cpp] def escape(buf: StringBuilder, cs: CharSequence): Unit = {
        {
            var i: Int = 0
            while (i < cs.length) {
                {
                    var c: Char = cs.charAt(i)
                    c match {
                        case '\\' =>
                            buf.append("\\\\")
                            break //todo: break is not supported
                        case '"' =>
                            buf.append("\\\"")
                            break //todo: break is not supported
                        case '\n' =>
                            buf.append("\\n")
                            break //todo: break is not supported
                        case '\r' =>
                            buf.append("\\r")
                            break //todo: break is not supported
                        case _ =>
                            buf.append(c)
                    }
                }
                ({
                    i += 1; i
                })
            }
        }
    }
}

class MacroTokenSource extends Source {
    private[cpp] def this(macroName: String, m: MacroData, args: List[Argument], gnuCExtensions: Boolean) {
        this ()
        this.macroName = macroName
        this.macro = m
        this.tokenIter = m.getTokens.iterator
        this.args = args
        this.arg = null
        this.gnuCExtensions = gnuCExtensions
    }

    private[cpp] override def mayExpand(macroName: String): Boolean = {
        if (macroName.equals(this.macroName)) return false
        return super.mayExpand(macroName)
    }

    private def concat(buf: PrintWriter, arg: Argument, queuedComma: Boolean): Unit = {
        if (queuedComma) {
            if (!arg.isOmittedArg || !gnuCExtensions) {
                buf.append(",")
            }
            else {
                assert(arg.isEmpty)
                return
            }
        }
        var i: Int = 0
        for (tok <- arg) {
            if (i != 0 || tok.getType != NL && !tok.isWhite) {
                tok.lazyPrint(buf)
                ({
                    i += 1; i
                })
            }
        }
    }

    private def stringify(pos: Token, arg: Argument): Token = {
        var buf: StringWriter = new StringWriter
        var printWriter: PrintWriter = new PrintWriter(buf)
        concat(printWriter, arg, false)
        var str: StringBuilder = new StringBuilder("\"")
        escape(str, buf.getBuffer)
        str.append("\"")
        return new SimpleToken(STRING, pos.getLine, pos.getColumn, str.toString, buf.toString, this)
    }

    private def paste(ptok: Token): Unit = {
        var buf: StringWriter = new StringWriter
        var printWriter: PrintWriter = new PrintWriter(buf)
        var queuedComma: Boolean = false
        var count: Int = 2
        {
            var i: Int = 0
            while (i < count) {
                {
                    if (!tokenIter.hasNext) {
                        error(ptok.getLine, ptok.getColumn, "Paste at end of expansion")
                        buf.append(' ').append(ptok.getText)
                        break //todo: break is not supported
                    }
                    var tok: Token = tokenIter.next
                    if (queuedComma && tok.getType != M_ARG) {
                        buf.append(",")
                        queuedComma = false
                    }
                    tok.getType match {
                        case M_PASTE =>
                            count += 2
                            ptok = tok
                            break //todo: break is not supported
                        case M_ARG =>
                            var idx: Int = (tok.getValue.asInstanceOf[Integer]).intValue
                            concat(printWriter, args.get(idx), queuedComma)
                            break //todo: break is not supported
                        case CCOMMENT =>
                        case CPPCOMMENT =>
                            break //todo: break is not supported
                        case ',' =>
                            assert(",".equals(tok.getText))
                            queuedComma = true
                            break //todo: break is not supported
                        case _ =>
                            buf.append(tok.getText)
                            break //todo: break is not supported
                    }
                }
                ({
                    i += 1; i
                })
            }
        }
        var sl: StringLexerSource = new StringLexerSource(buf.toString)
        arg = new SourceIterator(sl)
    }

    def token: Token = {
        var tok: Token = _token
        if (tok.getType != P_FEATUREEXPR && tok.getText.equals(macroName)) tok.setNoFurtherExpansion
        return tok
    }

    def _token: Token = {
        while (true) {
            if (arg != null) {
                if (arg.hasNext) {
                    var tok: Token = arg.next
                    assert(tok.getType != M_PASTE, "Unexpected paste token")
                    return tok
                }
                arg = null
            }
            if (!tokenIter.hasNext) return new SimpleToken(EOF, -1, -1, "", this)
            var tok: Token = tokenIter.next
            var idx: Int = 0
            tok.getType match {
                case M_STRING =>
                    idx = (tok.getValue.asInstanceOf[Integer]).intValue
                    return stringify(tok, args.get(idx))
                case M_ARG =>
                    idx = (tok.getValue.asInstanceOf[Integer]).intValue
                    arg = args.get(idx).expansion
                    break //todo: break is not supported
                case M_PASTE =>
                    paste(tok)
                    break //todo: break is not supported
                case _ =>
                    return tok
            }
        }
    }

    override def toString: String = {
        var buf: StringBuilder = new StringBuilder
        buf = buf.append("expansion of ").append(macroName)
        var parent: Source = getParent
        if (parent != null) buf = buf.append(" in ").append(String.valueOf(parent))
        return buf.toString
    }

    private[cpp] def debug_getContent: String = {
        return macro.getTokens.toString + " args: " + args
    }

    private final val macro: MacroData = null
    private var tokenIter: Iterator[Token] = null
    private var args: List[Argument] = null
    private var arg: Iterator[Token] = null
    private final val macroName: String = null
    private var gnuCExtensions: Boolean = false
}