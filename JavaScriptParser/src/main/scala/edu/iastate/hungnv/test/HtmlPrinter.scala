package edu.iastate.hungnv.test

import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.{ FeatureExprFactory, FeatureExpr }
import java.io.{ FileWriter, StringWriter, Writer }
import de.fosd.typechef.parser.html._
import de.fosd.typechef.parser.common.CharacterToken
import de.fosd.typechef.parser.common.JPosition
import edu.iastate.hungnv.test.Util._

object HtmlPrinter {

  //pretty printer combinators, stolen from http://www.scala-blogs.org/2009/04/combinators-for-pretty-printers-part-1.html
  sealed abstract class Doc {
    def ~(that: Doc) = Cons(this, that)

    def ~~(that: Doc) = this ~ space ~ that

    def *(that: Doc) = this ~ line ~ that

    def ~>(that: Doc) = this ~ nest(1, line ~ that)
  }

  case object Empty extends Doc

  case object Line extends Doc

  case class Text(s: String) extends Doc

  case class Cons(left: Doc, right: Doc) extends Doc

  case class Nest(n: Int, d: Doc) extends Doc

  implicit def string(s: String): Doc = Text(s)

  val line = Line
  val space = Text(" ")
  var newLineForIfdefs = true

  def nest(n: Int, d: Doc) = Nest(n, d)

  def block(d: Doc): Doc = "{" ~> d * "}"

  def layout(d: Doc): String = d match {
    case Empty => ""
    case Line => "\n"
    case Text(s) => s
    case Cons(l, r) => layout(l) + layout(r)
    case Nest(n, Empty) => layout(Empty)
    case Nest(n, Line) => "\n" + ("   " * n)
    case Nest(n, Text(s)) => layout(Text(s))
    case Nest(n, Cons(l, r)) => layout(Cons(Nest(n, l), Nest(n, r)))
    case Nest(i, Nest(j, x)) => layout(Nest(i + j, x))
  }

  // old version causing stack overflows and pretty slow
  //def print(ast: AST): String = layout(prettyPrint(ast))
  // new awesome fast version using a string writer instance
  def print(ast: AST): String = printW(ast, new StringWriter()).toString

  def layoutW(d: Doc, p: Writer): Unit = d match {
    case Empty => p.write("")
    case Line => p.write("\n")
    case Text(s) => p.write(s)
    case Cons(l, r) =>
      layoutW(l, p)
      layoutW(r, p)
    case Nest(n, Empty) => layoutW(Empty, p)
    case Nest(n, Line) => p.write("\n" + (" " * n))
    case Nest(n, Text(s)) => layoutW(Text(s), p)
    case Nest(n, Cons(l, r)) => layoutW(Cons(Nest(n, l), Nest(n, r)), p)
    case Nest(i, Nest(j, x)) => layoutW(Nest(i + j, x), p)
    case _ =>
  }

  def printW(ast: AST, writer: Writer): Writer = {
    layoutW(prettyPrint(ast), writer)
    writer
  }

  def printF(ast: AST, path: String, newLines: Boolean = true) = {
    newLineForIfdefs = newLines
    val writer = new FileWriter(path)
    layoutW(prettyPrint(ast), writer)
    writer.close()
  }

  def ppConditional(e: Conditional[_], list_feature_expr: List[FeatureExpr]): Doc = e match {
    case One(c: AST) => prettyPrint(c, list_feature_expr)
    case Choice(f, a: AST, b: AST) =>
      if (newLineForIfdefs) {
        line ~
          "#if" ~~ f.toTextExpr *
          prettyPrint(a, f :: list_feature_expr) *
          "#else" *
          prettyPrint(b, f.not :: list_feature_expr) *
          "#endif" ~
          line
      } else {
        "#if" ~~ f.toTextExpr *
          prettyPrint(a, f :: list_feature_expr) *
          "#else" *
          prettyPrint(b, f.not :: list_feature_expr) *
          "#endif"
      }

    case Choice(f, a: Conditional[_], b: Conditional[_]) =>
      if (newLineForIfdefs) {
        line ~
          "#if" ~~ f.toTextExpr *
          ppConditional(a, f :: list_feature_expr) *
          "#else" *
          ppConditional(b, f.not :: list_feature_expr) *
          "#endif" ~
          line
      } else {
        "#if" ~~ f.toTextExpr *
          ppConditional(a, f :: list_feature_expr) *
          "#else" *
          ppConditional(b, f.not :: list_feature_expr) *
          "#endif"
      }
  }

  private def optConditional(e: Opt[AST], list_feature_expr: List[FeatureExpr]): Doc = {
    if (e.feature == FeatureExprFactory.True ||
      list_feature_expr.foldLeft(FeatureExprFactory.True)(_ and _).implies(e.feature).isTautology())
      prettyPrint(e.entry, list_feature_expr)
    else if (newLineForIfdefs) {
      /*
            line ~
                "#if" ~~ e.feature.toTextExpr *
                prettyPrint(e.entry, e.feature :: list_feature_expr) *
                "#endif" ~
                    line
           */
      prettyPrint(e.entry, e.feature :: list_feature_expr)
    } else {
      "#if" ~~ e.feature.toTextExpr *
        prettyPrint(e.entry, e.feature :: list_feature_expr) *
        "#endif"
    }

  }

  def prettyPrint(ast: AST, list_feature_expr: List[FeatureExpr] = List(FeatureExprFactory.True)): Doc = {
    implicit def pretty(a: AST): Doc = prettyPrint(a, list_feature_expr)
    implicit def prettyOpt(a: Opt[AST]): Doc = optConditional(a, list_feature_expr)
    implicit def prettyCond(a: Conditional[_]): Doc = ppConditional(a, list_feature_expr)
    implicit def prettyOptStr(a: Opt[String]): Doc = string(a.entry)

    // this method separates Opt elements of an input list variability-aware
    // problem is that when having for instance a function with one mandatory and one optional
    // parameter, e.g.,
    // void foo( int a
    // #ifdef B
    // , int B
    // #endif
    // ) {}
    // the standard sep function prints out the comma between both parameters without an
    // annotation. Further processing of the output will lead to an error.
    // This function prints out separated lists with annotated commas solving that problem.
    def sepVaware(l: List[Opt[AST]], selem: String, breakselem: Doc = space) = {
      var res: Doc = if (l.isEmpty) Empty else l.head
      var combCtx: FeatureExpr = if (l.isEmpty) FeatureExprFactory.True else l.head.feature

      for (celem <- l.drop(1)) {
        val selemfexp = combCtx.and(celem.feature)

        // separation element is never present
        if (selemfexp.isContradiction())
          res = res ~ breakselem ~ prettyOpt(celem)

        // separation element is always present
        else if (selemfexp.isTautology())
          res = res ~ selem ~ breakselem ~ prettyOpt(celem)

        // separation element is sometimes present
        else {
          res = res * "#if" ~~ selemfexp.toTextExpr * selem * "#endif" * prettyOpt(celem)
        }

        // add current feature expression as it might influence the addition of selem for
        // the remaint elements of the input list l
        combCtx = combCtx.or(celem.feature)
      }

      res
    }

    def sep(l: List[Opt[AST]], s: (Doc, Doc) => Doc) = {
      val r: Doc = if (l.isEmpty) Empty else l.head
      l.drop(1).foldLeft(r)((a, b) => s(a, prettyOpt(b)))
    }
    def seps(l: List[Opt[String]], s: (Doc, Doc) => Doc) = {
      val r: Doc = if (l.isEmpty) Empty else l.head
      l.drop(1).foldLeft(r)(s(_, _))
    }
    def commaSep(l: List[Opt[AST]]) = sep(l, _ ~ "," ~~ _)
    def spaceSep(l: List[Opt[AST]]) = sep(l, _ ~~ _)
    def opt(o: Option[AST]): Doc = if (o.isDefined) o.get else Empty
    def optExt(o: Option[AST], ext: (Doc) => Doc): Doc = if (o.isDefined) ext(o.get) else Empty
    def optCondExt(o: Option[Conditional[AST]], ext: (Doc) => Doc): Doc = if (o.isDefined) ext(o.get) else Empty

    ast match {
      case DNode(name, attributes, children, openTag, closingTag) => {
        var script: String = ""
        if (name.name.equals("script")) {
          var jsSource = List[Opt[CharacterToken]]()

          for (child <- children) {
            val text = child.entry.asInstanceOf[DText].value
            jsSource = jsSource ::: text 
          }
          jsSource = jsSource ::: List(Opt(FeatureExprFactory.True, new CharacterToken(';', FeatureExprFactory.True, new de.fosd.typechef.parser.common.JPosition("", -1, -1)))) // Adhoc fix: Add semi-colon right before the ending </script> tag to force joins (fixed a bug with function filterResults when running AddressBook-6.2.12)
          
          script = prettyPrintJS(jsSource, true)
        }

        "<" ~ name.name ~ (if (attributes.isEmpty) Empty else " " ~ sep(attributes, _ ~~ _)) ~ (if (list_feature_expr.size == 2) (" cond=\"" ~ list_feature_expr.toString.replace("\"", "'")) ~ "\"" else Empty) ~ ">" ~
          (if (name.name.equals("script"))
            script
          else if (children == null)
            Empty
          else if (children.isEmpty)
            Empty
          else
            nest(1, line ~ sep(children, _ * _))) *
          "</" ~ name.name ~ ">"
      }

      case DText(x) => {
        /*
                    val out = new StringBuilder

			        out ++= "Text(List[Opt[CharacterToken]]): "
			
			        var currFeat: FeatureExpr = null
			
			        for (optToken <- x) {
			            optToken match {
			                case Opt(f, e) => {
			                    if (f != currFeat) {
			                        out ++= "(PC=" + f + ") "
			                        currFeat = f;
			                    }
			                    out ++= Util.standardize(e.getText()) + " "
			                }
			            }
			        }
			
			        out.toString
			        */

        "Text"
      }

      case HAttribute(name, value) => name.name ~ "=" ~
          (if (value.isDefined) 
        	  ("\"" ~
        	      (if (name.name.startsWith("on"))
//        	    	  "javascript: " ~ prettyPrintJS(stringToListOfOptToken(value.get + ";", new JPosition(name.getPositionTo.getFile, name.getPositionTo.getLine, name.getPositionTo.getColumn)), false)
//        	    	  "javascript: " ~ value.get ~ ";"
        	    	  prettyPrintJS(stringToListOfOptToken(value.get + ";", new JPosition(name.getPositionTo.getFile, name.getPositionTo.getLine, name.getPositionTo.getColumn)), false)        	        
//        	    	  value.get
        	      else
        	    	  value.get) ~ 
        	  "\"") 
          else Empty)

      case e => assert(assertion = false, message = "match not exhaustive: " + e); ""
    }
  }
  
  def stringToListOfOptToken(str: String, pos: JPosition): List[Opt[CharacterToken]] = {
    var list = List[Opt[CharacterToken]]()
    for (c <- str) {
      list = Opt(FeatureExprFactory.True, new CharacterToken(c, FeatureExprFactory.True, pos)) :: list
    }
    list.reverse
  }

  def prettyPrintJS(tokens: List[Opt[CharacterToken]], withLineBreaks: Boolean): String = {
//    val str = new StringBuilder
//    for (optToken <- tokens) {
//    	str ++= optToken.entry.getText
//    }

    
    var newTokens: List[CharacterToken] = tokens.map(optToken => optToken.entry)
    val domResult = JSParser.parse(newTokens)

    var doc = JSPrinter.prettyPrint(domResult, List(FeatureExprFactory.True))
    val layout = new java.io.StringWriter()
    JSPrinter.layoutWGeneric(doc, layout, withLineBreaks)
    
//    var layout = JSPrinter2.print(domResult, List(FeatureExprFactory.True), 0)
    
    //log("JsPrinter:")
    //log(layout, true)
    //log()

    layout.toString()
  }

}