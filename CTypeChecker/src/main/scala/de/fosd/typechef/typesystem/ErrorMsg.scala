package de.fosd.typechef.typesystem
import de.fosd.typechef.parser.c.AST

class ErrorMsg(msg:String,caller:AST,target:List[Entry]) {
      override def  toString = msg+" ("+caller+" => "+target.mkString(" || ")
}


