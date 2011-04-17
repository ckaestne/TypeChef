package de.fosd.typechef.ast

import _root_.de.fosd.typechef.parser._
import org.kiama.attribution.Attributable

/**
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 17.04.11
 * Time: 13:22
 * To change this template use File | Settings | File Templates.
 */

object TestAST {

    type Type = String

    abstract class LangAST extends Attributable


    case class CompUnit(pckg: Opt[Pckg], imports: Many[Imp] = Many(), smts: One[Stm] = One(Empty())) extends LangAST with Attributable

    case class Pckg(name: One[String]) extends LangAST

    case class Imp(name: One[String]) extends LangAST

    abstract class Stm extends LangAST

    abstract class Expr extends LangAST

    case class VarDecl(vartype: One[Type], name: One[String]) extends Stm

    case class Assign(left: One[String], right: One[Expr]) extends Stm

    case class Block(stms: Many[Stm]) extends Stm {
        override def toString = "{" + stms + "}"
    }

    case class Return(ret: One[String]) extends Stm

    case class Empty() extends Stm

    case class Var(name: One[String]) extends Expr

    case class Primitive(typ: One[Type]) extends Expr


}