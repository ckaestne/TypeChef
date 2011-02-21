package de.fosd.typechef.parser

import de.fosd.typechef.featureexpr.FeatureExpr
import scala.util.continuations._
import scala.collection.mutable.ArrayStack
import scala.collection.mutable.Stack
import scala.util.parsing.input._
import de.fosd.typechef.featureexpr.FeatureExpr

//In this version, I use separate tokens for the preprocessing directives.
abstract class PreprocDirective
case class If(f: FeatureExpr) extends PreprocDirective {} //includes ifdef
case class Elif(f: FeatureExpr) extends PreprocDirective {}
case object Else extends PreprocDirective {}
case object Endif extends PreprocDirective {}
