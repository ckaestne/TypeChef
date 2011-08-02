package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import de.fosd.typechef.typesystem._
import de.fosd.typechef.featureexpr._
import java.io.{PrintWriter, FileWriter}

object Annotations2FeatureModules {

  val DEBUG = true

  // file operations write and append to file
  // http://stackoverflow.com/questions/4604237/how-to-write-to-a-file-in-scala
  def using[A <: {def close(): Unit}, B](param: A)(f: A => B): B = {
    try { f(param) } finally { param.close();}
  }

  def writeToFile(fileName: String, data: String) = {
    using (new FileWriter(fileName)) {
      fileWriter => fileWriter.write(data)
    }
  }

  def appendToFile(fileName:String, textData:String) = {
    using (new FileWriter(fileName, true)) {
      fileWriter => using(new PrintWriter(fileWriter)) {
        printWriter => printWriter.println(textData)
      }
    }
  }

  def main(args: List[String]) = {
    val cli = new OptionParser
    var options = Map[Symbol, Any]('inputfolder -> ".", 'refactor -> false)
    cli.banner = "Annotations2FeatureModules [Options]"
    cli.separator("")
    cli.separator("Options:")
    cli.optl("-i", "--inputfolder[=FOLDER]", "input folder; default=.") { v: Option[String] => options += 'inputfolder -> v.getOrElse(".")}
    cli.bool("-r", "--refactor", "apply refactorings directly; default=false") { v => options += 'refactor -> v}

    try {
      cli.parse(args)
    } catch {
      case e: OptionParserException => println(e.getMessage); exit(1)
    }
  }
}

import org.kiama.rewriting.Rewriter._
import org.kiama._
import attribution.Attributable
import org.kiama.attribution.DynamicAttribution._
import de.fosd.typechef.conditional._

class CRefactorings extends ASTNavigation {

  override val DEBUG = true

  private def optStmtBlock[T](a: Opt[T]): List[Opt[T]] = {
    var result = List[Opt[T]]()
    result.+:(a)
    var prev = a->prevOpt
    var next = a->nextOpt

    while (next != null) {
      result.+:(next)
      next = next->nextOpt
    }

    while (prev != null) {
      result.+:(prev)
      prev = prev->prevOpt
    }

    return result
  }

  val hvars: Attributable ==> List[Id] = attr {
    case Id(name) => List(Id(name))
    case AssignExpr(id, _, _) => id -> hvars
    case ExprStatement(id) => id -> hvars
    case PostfixExpr(_, FunctionCall(params)) => params -> hvars
    case ExprList(exprs) => exprs.map(_->hvars).flatten
    case Opt(_, Id(name)) => List(Id(name))
//    case IfStatement(condition, thenBranch, elifs, elseBranch) => condition->hvars ++ elseBranch->hvars
  }

  private val rewriteStrategy = everywhere(rule {
    case e @ Opt(f, stmt: Statement) if (!f.isTautology) =>
      val hookname = generateHookname
      val hooks = stmt -> hvars
      if (DEBUG) println("number of hookparameters: " + hooks.size)
      var hookparameters = List[Opt[Expr]]()
      for (h <- hooks) hookparameters = hookparameters.+:(Opt(FeatureExpr.base, h))
      if (DEBUG) println("hookparameters: " + hookparameters)
      if (DEBUG) println("optStmtBlock: " + optStmtBlock(e))
      Opt(True, ExprStatement(PostfixExpr(Id(hookname), FunctionCall(ExprList(hookparameters)))))
  })

  private var currenthook = -1

  private def generateHookname = {
    currenthook += 1
    "hook" + currenthook.toString
  }

  def rewrite(ast: AST): AST = {
    rewriteStrategy(ast).get.asInstanceOf[AST]
  }

  def featureToCExpr(feature: FeatureExpr): Expr = feature match {
    case d: DefinedExternal => Id(d.feature)
    case a: And =>
      val l = a.clauses.toList
      var del = List[Opt[NArySubExpr]]()
      for (e <- l.tail)
        del = del ++ List(Opt(FeatureExpr.base, NArySubExpr("&&", featureToCExpr(e))))
      NAryExpr(featureToCExpr(l.head), del)
    case o: Or =>
      val l = o.clauses.toList
      var del = List[Opt[NArySubExpr]]()
      for (e <- l.tail)
        del = del ++ List(Opt(FeatureExpr.base, NArySubExpr("||", featureToCExpr(e))))
      NAryExpr(featureToCExpr(l.head), del)
    case Not(n) => UnaryOpExpr("!", featureToCExpr(n))
    case _ => assert(false, "todo"); Id("dead")
  }
}