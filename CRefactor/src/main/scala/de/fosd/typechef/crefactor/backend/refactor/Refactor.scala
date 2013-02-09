package de.fosd.typechef.crefactor.backend.refactor

import java.util
import util.Collections
import de.fosd.typechef.typesystem.CEnvCache
import de.fosd.typechef.crefactor.Morpheus
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.parser.c._
import de.fosd.typechef.crewrite.{ASTEnv, ConditionalNavigation, ASTNavigation}
import de.fosd.typechef.crefactor.frontend.util.Selection
import de.fosd.typechef.parser.c.Id
import scala.Some
import de.fosd.typechef.parser.c.TranslationUnit
import de.fosd.typechef.typesystem.CUnknown
import de.fosd.typechef.parser.c.CompoundStatement
import de.fosd.typechef.conditional.{Choice, Conditional, Opt, One}
import de.fosd.typechef.featureexpr.FeatureExpr

trait Refactor extends CEnvCache with ASTNavigation with ConditionalNavigation {

  private val VALID_NAME_PATTERN = "[a-zA-Z_][a-zA-Z0-9_]*"

  private val LANGUAGE_KEYWORDS = List(
    "auto",
    "break",
    "case",
    "char",
    "const",
    "continue",
    "default",
    "do",
    "double",
    "else",
    "enum",
    "extern",
    "float",
    "for",
    "goto",
    "if",
    "inline",
    "int",
    "long",
    "register",
    "restrict",
    "return",
    "short",
    "signed",
    "sizeof",
    "static",
    "struct",
    "switch",
    "typedef",
    "union",
    "unsigned",
    "void",
    "volatile",
    "while",
    "_Alignas",
    "_Alignof",
    "_Atomic",
    "_Bool",
    "_Complex",
    "_Generic",
    "_Imaginary",
    "_Noreturn",
    "_Static_assert",
    "_Thread_local"
  )

  def isAvailable(ast: AST, astEnv: ASTEnv, selection: Selection): Boolean

  /**
   * Checks if the name of a variable is compatible to the iso c standard. See 6.4.2 of the iso standard
   *
   * @param name name to check
   * @return <code>true</code> if valid, <code>false</code> if not
   */
  def isValidName(name: String): Boolean = (name.matches(VALID_NAME_PATTERN) && !name.startsWith("__") && !isReservedLanguageKeyword(name))

  def generateValidName(id: Id, stmt: Opt[AST], morph: Morpheus, appendix: Int = 1): String = {
    val newName = id.name + "_" + appendix
    if (isShadowed(newName, stmt.entry, morph)) generateValidName(id, stmt, morph, (appendix + 1))
    else newName
  }

  /**
   * Checks if the name is a language keyword.
   *
   * @param name the name to check
   * @return <code>true</code> if language keyword
   */
  def isReservedLanguageKeyword(name: String) = LANGUAGE_KEYWORDS.contains(name)

  def buildChoice[T <: AST](attribute: List[(T, FeatureExpr)]): Conditional[T] = {
    if (attribute.isEmpty) One(null.asInstanceOf[T])
    else if (attribute.length == 1) One(attribute.head._1)
    else Choice(attribute.head._2, One(attribute.head._1), buildChoice(attribute.tail))
  }

  def buildVariableCompoundStatement(stmts: List[(CompoundStatementExpr, FeatureExpr)]): CompoundStatementExpr = {
    // move several compoundStatement into one and apply their feature.
    val innerstmts = stmts.foldLeft(List[Opt[Statement]]())((innerstmts, stmtEntry) => stmtEntry._1 match {
      case CompoundStatementExpr(CompoundStatement(inner)) => innerstmts ::: inner.map(stmt => stmt.copy(feature = stmt.feature.and(stmtEntry._2)))
      case _ => innerstmts
    })
    CompoundStatementExpr(CompoundStatement(innerstmts))
  }

  def getAllConnectedIdentifier(lookup: Id, declUse: util.IdentityHashMap[Id, List[Id]], useDecl: util.IdentityHashMap[Id, List[Id]]) = {
    val occurrences = Collections.newSetFromMap[Id](new util.IdentityHashMap())

    // find all uses of an callId
    def addOccurrence(occurrence: Id) {
      if (!occurrences.contains(occurrence)) {
        occurrences.add(occurrence)
        if (!declUse.containsKey(occurrence)) return
        // workaround to avoid null pointer @ wrong forward declarations
        // occurrences.clear()
        declUse.get(occurrence).foreach(use => {
          occurrences.add(use)
          if (useDecl.containsKey(use)) useDecl.get(use).foreach(entry => addOccurrence(entry))
        })
      }
    }

    if (useDecl.containsKey(lookup)) useDecl.get(lookup).foreach(id => addOccurrence(id)) // lookup declarations and search for further referenced declarations
    else addOccurrence(lookup) // callId is decl - search for further referenced declarations

    occurrences.toArray(Array[Id]()).toList
  }

  def isShadowed(name: String, element: AST, morpheus: Morpheus): Boolean = {
    val lookupValue = findPriorASTElem[CompoundStatement](element, morpheus.getASTEnv) match {
      case s@Some(x) => x.innerStatements.last.entry
      case _ => morpheus.getAST.asInstanceOf[TranslationUnit].defs.last.entry
    }

    val env = morpheus.getEnv(lookupValue)

    isDeclaredVarInEnv(name, env.asInstanceOf[Env])
  }

  def isDeclaredVarInEnv(name: String, env: Env) = env.varEnv(name) match {
    case One(CUnknown(_)) => false
    case _ => true
  }

  def isDeclaredStructOrUnionInEnv(name: String, env: Env) = env.structEnv.someDefinition(name, false) || env.structEnv.someDefinition(name, true)

  def isDeclaredTypeDefInEnv(name: String, env: Env) = env.typedefEnv(name) match {
    case One(CUnknown(_)) => false
    case _ => true
  }

  // TODO Clean up ast rewrite strategies

  def insertInAstBefore[T <: Product](t: T, mark: Opt[_], insert: Opt[_]): T = {
    val r = oncetd(rule {
      case l: List[Opt[_]] => l.flatMap(x => if (x.eq(mark)) insert :: x :: Nil else x :: Nil)
    })
    r(t).get.asInstanceOf[T]
  }

  def insertInAstBefore[T <: Product](t: T, mark: Opt[_], insert: List[Opt[_]]): T = {
    val r = oncetd(rule {
      case l: List[Opt[_]] => l.flatMap(x => if (x.eq(mark)) insert ::: x :: Nil else x :: Nil)
    })
    r(t).get.asInstanceOf[T]
  }

  def replaceInAST[T <: Product](t: T, mark: Opt[_], replace: Opt[_]): T = {
    val r = manybu(rule {
      case l: List[Opt[_]] => l.flatMap(x => if (x.eq(mark)) replace :: Nil else x :: Nil)
    })
    r(t).get.asInstanceOf[T]
  }

  def replaceInASTOnceTD[T <: Product](t: T, mark: Opt[_], replace: Opt[_]): T = {
    val r = oncetd(rule {
      case l: List[Opt[_]] => l.flatMap(x => if (x.eq(mark)) replace :: Nil else x :: Nil)
    })
    r(t).get.asInstanceOf[T]
  }

  def replaceInASTOnceTD[T <: Product](t: T, mark: T, replace: T): T = {
    val r = oncetd(rule {
      case i: T => if (i.eq(mark)) replace else i
    })
    r(t).get.asInstanceOf[T]
  }

  def replaceInAST[T <: Product](t: T, e: T, n: T): T = {
    val r = manybu(rule {
      case i: T => if (i.eq(e)) n else i
    })
    r(t).get.asInstanceOf[T]
  }

  def replaceInAST_TD[T <: Product](t: T, e: T, n: T): T = {
    val r = alltd(rule {
      case i: T => if (i.eq(e)) n else i
    })
    r(t).get.asInstanceOf[T]
  }

  def removeFromAST[T <: Product](t: T, remove: Opt[_]): T = {
    val r = oncetd(rule {
      case l: List[Opt[_]] => l.flatMap(x => if (x.eq(remove)) Nil else x :: Nil)
    })
    r(t).get.asInstanceOf[T]
  }

  def insertRefactoredAST(morpheus: Morpheus, callCompStmt: CompoundStatement, workingCallCompStmt: CompoundStatement): AST = {
    val parent = parentOpt(callCompStmt, morpheus.getASTEnv)
    parent.entry match {
      case f: FunctionDef => replaceInASTOnceTD(morpheus.getAST, parent, parent.copy(entry = f.copy(stmt = workingCallCompStmt)))
      case c: CompoundStatement => replaceInAST(morpheus.getAST, c, c.copy(innerStatements = workingCallCompStmt.innerStatements))
      case x =>
        assert(false, "Something bad happend - i am going to cry.")
        morpheus.getAST
    }
  }
}
