/*
 * TypeChef Variability-Aware Lexer.
 * Copyright 2010-2011, Christian Kaestner, Paolo Giarrusso
 * Licensed under GPL 3.0
 *
 * built on top of
 *
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
package de.fosd.typechef.lexer

import java.io._
import java.{util => jUtil}
import java.util.Iterator
import de.fosd.typechef.lexer.Token._
import util.control.Breaks
import java.lang.StringBuilder
import collection.JavaConverters._
import collection.immutable.Queue
import annotation.tailrec

object MacroTokenSource {
  /* XXX Called from Preprocessor [ugly]. */
  private[lexer] def escape(buf: StringBuilder, cs: CharSequence): Unit = {
    {
      var i: Int = 0
      while (i < cs.length) {
        {
          var c: Char = cs.charAt(i)
          c match {
            case '\\' =>
              buf.append("\\\\")
            case '"' =>
              buf.append("\\\"")
            case '\n' =>
              buf.append("\\n")
            case '\r' =>
              buf.append("\\r")
            case _ =>
              buf.append(c)
          }
        }
        ({
          i += 1;
          i
        })
      }
    }
  }
}

class MacroTokenSource extends Source {
  private[lexer] def this(macroName: String, m: MacroData, args: jUtil.List[Argument], gnuCExtensions: Boolean) {
    this()
    this.macroName = macroName
    this._macro = m
    this.tokenIter = m.getTokens.iterator
    this.args = args
    this.arg = null
    this.gnuCExtensions = gnuCExtensions
  }

  private[lexer] override def mayExpand(macroName: String): Boolean = {
    if (macroName.equals(this.macroName)) return false
    return super.mayExpand(macroName)
  }

  @tailrec
  private def addFirstNonSpaceToken(srcTokens: Seq[Token], _destTokens: Queue[Token]): Queue[Token] = {
    var destTokens = _destTokens
    if (srcTokens.nonEmpty) {
      val argTok0 = srcTokens.head
      if (argTok0.getType != NL && !argTok0.isWhite) {
        destTokens = destTokens enqueue argTok0
        destTokens ++= srcTokens.tail
        destTokens
      } else {
        addFirstNonSpaceToken(srcTokens.tail, destTokens)
      }
    } else
      destTokens
  }

  //XXX inline, probably.
  private def extractTokensForConcat(arg: Argument) =
    addFirstNonSpaceToken(arg.tokens, Queue[Token]())

  private def tokensToStr(printWriter: PrintWriter, arg: Argument) {
    extractTokensForConcat(arg) foreach {
      _.lazyPrint(printWriter)
    }
  }

  private def stringify(pos: Token, arg: Argument): Token = {
    var buf: StringWriter = new StringWriter
    var printWriter: PrintWriter = new PrintWriter(buf)
    tokensToStr(printWriter, arg)
    var str: StringBuilder = new StringBuilder("\"")
    MacroTokenSource.escape(str, buf.getBuffer)
    str.append("\"")
    return new SimpleToken(STRING, pos.getLine, pos.getColumn, str.toString, buf.toString, this)
  }

  private def paste(_ptok: Token): Unit = {
    var buf = new StringWriter
    var printWriter = new PrintWriter(buf)
    var tokens = Queue[Token]()
    var stringPasting = false

    def strToTokens() {
      if (stringPasting) {
        val sl = new StringLexerSource(buf.toString)
        stringPasting = false
        tokens ++= sl.asScala
        buf = new StringWriter()
        printWriter = new PrintWriter(buf)
      }
    }

    def concat(_tokens: Queue[Token], arg: Argument, queuedCommaOpt: Option[Token]): Queue[Token] = {
      var tokens = _tokens
      queuedCommaOpt match {
        case Some(queuedComma) =>
          strToTokens()
          //Output the comma that we didn't output previously.
          if (!arg.isOmittedArg || !gnuCExtensions) {
            tokens = tokens enqueue queuedComma
            tokens ++= extractTokensForConcat(arg)
          } else {
            //Swallow the comma, as prescribed by:
            // http://gcc.gnu.org/onlinedocs/cpp/Variadic-Macros.html
            assert(arg.tokens.isEmpty)
          }
        case None =>
          stringPasting = true
          tokensToStr(printWriter, arg)
      }
      tokens
    }

    var ptok = _ptok
    var queuedComma: Option[Token] = None
    var count: Int = 2

    var i: Int = 0
    import Breaks._
    breakable {
      while (i < count) {
        if (!tokenIter.hasNext) {
          error(ptok.getLine, ptok.getColumn, "Paste at end of expansion")
          strToTokens()
          tokens ++= Seq(Token.space, ptok)
          break
        }
        var tok: Token = tokenIter.next
        if (queuedComma.isDefined && tok.getType != M_ARG) {
          strToTokens()
          tokens = tokens enqueue (queuedComma.get)
          queuedComma = None
        }
        tok.getType match {
          case M_PASTE =>
            count += 2
            ptok = tok
          case M_ARG =>
            val idx: Int = (tok.getValue.asInstanceOf[java.lang.Integer]).intValue
            tokens = concat(tokens, args.get(idx), queuedComma)
          /* XXX Test this. */
          case CCOMMENT | CPPCOMMENT =>
          case ',' =>
            assert(",".equals(tok.getText))
            queuedComma = Some(tok)
          case _ =>
            //strToTokens()
            //tokens += tok
            stringPasting = true
            tok.lazyPrint(printWriter)
        }
        i += 1
      }
    }
    strToTokens()
    arg = tokens.iterator.asJava
  }

  def token(): Token = {
    val tok: Token = _token()
    if (tok.getType != P_FEATUREEXPR && tok.getText.equals(macroName)) tok.setNoFurtherExpansion
    return tok
  }

  def _token(): Token = {
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
      val tok: Token = tokenIter.next
      var idx: Int = 0
      tok.getType match {
        case M_STRING =>
          idx = (tok.getValue.asInstanceOf[java.lang.Integer]).intValue
          return stringify(tok, args.get(idx))
        case M_ARG =>
          idx = (tok.getValue.asInstanceOf[java.lang.Integer]).intValue
          if (idx < args.size) {
            arg = args.get(idx).expansion
          } else {
            //This can happen in valid code only because of the brokenness of expanded_token(), see comment there.
            //This affects linux-2.6.33.3/fs/jfs/jfs_lockmgr.c
            arg = null
          }

        case M_PASTE =>
          paste(tok)
        case _ =>
          return tok
      }
    }
    null
  }

  override def toString: String = {
    var buf: StringBuilder = new StringBuilder
    buf = buf.append("expansion of ").append(macroName)
    var parent: Source = getParent
    if (parent != null) buf = buf.append(" in ").append(String.valueOf(parent))
    return buf.toString
  }

  private[lexer] def debug_getContent: String = {
    return _macro.getTokens.toString + " args: " + args
  }

  private final var _macro: MacroData = null
  private var tokenIter: Iterator[Token] = null
  private var args: jUtil.List[Argument] = null
  private var arg: Iterator[Token] = null
  private final var macroName: String = null
  private var gnuCExtensions: Boolean = false
}
