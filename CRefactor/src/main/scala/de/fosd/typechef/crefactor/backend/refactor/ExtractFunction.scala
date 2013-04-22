package de.fosd.typechef.crefactor.backend.refactor

import de.fosd.typechef.crefactor.backend.ASTSelection
import de.fosd.typechef.parser.c._
import de.fosd.typechef.crefactor.frontend.util.Selection
import java.util
import de.fosd.typechef.crefactor.Morpheus
import de.fosd.typechef.crefactor.util.Configuration
import util.{IdentityHashMap, Collections}
import management.ManagementFactory
import de.fosd.typechef.typesystem._
import de.fosd.typechef.parser.c.PostfixExpr
import de.fosd.typechef.parser.c.ReturnStatement
import de.fosd.typechef.parser.c.SwitchStatement
import de.fosd.typechef.parser.c.AtomicNamedDeclarator
import de.fosd.typechef.parser.c.InlineSpecifier
import de.fosd.typechef.parser.c.VolatileSpecifier
import scala.Some
import de.fosd.typechef.parser.c.NAryExpr
import de.fosd.typechef.parser.c.DoStatement
import de.fosd.typechef.parser.c.ExternSpecifier
import de.fosd.typechef.parser.c.PointerCreationExpr
import de.fosd.typechef.parser.c.AssignExpr
import de.fosd.typechef.parser.c.VoidSpecifier
import de.fosd.typechef.parser.c.ConditionalExpr
import de.fosd.typechef.parser.c.FunctionCall
import de.fosd.typechef.conditional.{Choice, One, Opt}
import de.fosd.typechef.parser.c.RestrictSpecifier
import de.fosd.typechef.parser.c.ForStatement
import de.fosd.typechef.parser.c.IfStatement
import de.fosd.typechef.parser.c.DeclParameterDeclList
import de.fosd.typechef.parser.c.WhileStatement
import de.fosd.typechef.parser.c.Pointer
import de.fosd.typechef.parser.c.SizeOfExprT
import de.fosd.typechef.parser.c.UnaryOpExpr
import de.fosd.typechef.parser.c.Declaration
import de.fosd.typechef.parser.c.ExprStatement
import de.fosd.typechef.parser.c.Id
import de.fosd.typechef.parser.c.Constant
import de.fosd.typechef.parser.c.AutoSpecifier
import de.fosd.typechef.parser.c.PointerDerefExpr
import de.fosd.typechef.parser.c.GotoStatement
import de.fosd.typechef.parser.c.ElifStatement
import de.fosd.typechef.parser.c.ExprList
import de.fosd.typechef.parser.c.FunctionDef
import de.fosd.typechef.parser.c.SizeOfExprU
import de.fosd.typechef.parser.c.NestedFunctionDef
import de.fosd.typechef.parser.c.BreakStatement
import de.fosd.typechef.parser.c.ContinueStatement
import de.fosd.typechef.parser.c.ParameterDeclarationD
import de.fosd.typechef.parser.c.CastExpr
import de.fosd.typechef.parser.c.CompoundStatement
import de.fosd.typechef.parser.c.CaseStatement
import de.fosd.typechef.parser.c.RegisterSpecifier
import de.fosd.typechef.parser.c.StringLit
import de.fosd.typechef.parser.c.StaticSpecifier
import de.fosd.typechef.parser.c.ConstSpecifier
import de.fosd.typechef.parser.c.UnaryExpr
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}

/**
 * Implements the strategy of extracting a function.
 */
// TODO Replace original ExtractFunction -> Delete and Refactor
object ExtractFunction extends ASTSelection with Refactor {

  private var lastSelection: Selection = null

  private var cachedSelectedElements: List[AST] = null

  def getSelectedElements(morpheus: Morpheus, selection: Selection): List[AST] = {
    if (lastSelection.eq(selection)) return cachedSelectedElements
    lastSelection = selection

    // TODO Better solution for Control Statements
    val ids = filterASTElementsForFile[Id](filterASTElems[Id](morpheus.getAST).par.filter(x => isInSelectionRange(x, selection)).toList, selection.getFilePath)

    def findMostUpwardExpr(element: Expr): Expr = {
      parentAST(element, morpheus.getASTEnv) match {
        case e: Id => findMostUpwardExpr(e)
        case e: Constant => findMostUpwardExpr(e)
        case e: StringLit => findMostUpwardExpr(e)
        case e: UnaryExpr => findMostUpwardExpr(e)
        case e: PostfixExpr => findMostUpwardExpr(e)
        case e: SizeOfExprT => findMostUpwardExpr(e)
        case e: SizeOfExprU => findMostUpwardExpr(e)
        case e: CastExpr => findMostUpwardExpr(e)
        case e: PointerDerefExpr => findMostUpwardExpr(e)
        case e: PointerCreationExpr => findMostUpwardExpr(e)
        case e: UnaryOpExpr => findMostUpwardExpr(e)
        case e: NAryExpr => findMostUpwardExpr(e)
        case e: ExprList => findMostUpwardExpr(e)
        case e: ConditionalExpr => findMostUpwardExpr(e)
        case e: AssignExpr => findMostUpwardExpr(e)
        case _ => element
      }
    }

    def findParent(id: Id): Some[AST] = {
      val priorElement = findPriorASTElem[Statement](id, morpheus.getASTEnv)
      priorElement match {
        case None => null
        case _ => priorElement.get match {
          case ifState: IfStatement => Some(findMostUpwardExpr(id))
          case elIf: ElifStatement => Some(findMostUpwardExpr(id))
          case doState: DoStatement => Some(findMostUpwardExpr(id))
          case whileState: WhileStatement => Some(findMostUpwardExpr(id))
          case forState: ForStatement => Some(findMostUpwardExpr(id))
          case returnState: ReturnStatement => Some(findMostUpwardExpr(id))
          case switch: SwitchStatement => Some(findMostUpwardExpr(id))
          case caseState: CaseStatement => Some(findMostUpwardExpr(id))
          case s => Some(s)
        }
      }

    }

    def exploitStatements(statement: Statement): Statement = {
      try {
        parentAST(statement, morpheus.getASTEnv) match {
          case null =>
            assert(false, "An error during determine the preconditions occured.")
            statement
          case f: FunctionDef => statement
          case nf: NestedFunctionDef => statement
          case p =>
            if (isElementOfSelectionRange(p, selection)) {
              exploitStatements(p.asInstanceOf[Statement])
            }
            else statement
        }
      } catch {
        case _ => statement
      }
    }

    def lookupControlStatements(statement: Statement): Statement = {
      try {
        nextAST(statement, morpheus.getASTEnv) match {
          case null => statement
          case c: ContinueStatement =>
            if (isElementOfSelectionRange(c, selection)) c
            else statement
          case b: BreakStatement =>
            if (isElementOfSelectionRange(b, selection)) b
            else statement
          case c: CaseStatement =>
            if (isElementOfSelectionRange(c, selection)) c
            else statement
          case g: GotoStatement =>
            if (isElementOfSelectionRange(g, selection)) g
            else statement
          case r: ReturnStatement =>
            if (isElementOfSelectionRange(r, selection)) r
            else statement
          case _ => statement
        }
      } catch {
        case _ => statement
      }
    }
    val uniqueSelectedStatements = Collections.newSetFromMap[Statement](new util.IdentityHashMap())
    val uniqueSelectedExpressions = Collections.newSetFromMap[Expr](new util.IdentityHashMap())

    ids.foreach(id => {
      val parent = findParent(id)
      parent match {
        case null =>
        case _ =>
          if (parent.get.isInstanceOf[Statement]) {
            uniqueSelectedStatements.add(parent.get.asInstanceOf[Statement])
            uniqueSelectedStatements.add(lookupControlStatements(parent.get.asInstanceOf[Statement]))

          }
          else if (parent.get.isInstanceOf[Expr]) uniqueSelectedExpressions.add(parent.get.asInstanceOf[Expr])
      }
    })

    var parents: List[AST] = List()
    // TODO Optimize expensive array and list conversions
    if (!uniqueSelectedStatements.isEmpty) {
      parents = uniqueSelectedStatements.toArray(Array[Statement]()).toList
      uniqueSelectedStatements.clear()
      parents.foreach(statement => {
        val exploitedStatement = exploitStatements(statement.asInstanceOf[Statement])
        uniqueSelectedStatements.add(exploitedStatement)
      })
      parents = uniqueSelectedStatements.toArray(Array[Statement]()).toList
    } else parents = uniqueSelectedExpressions.toArray(Array[Expr]()).toList

    cachedSelectedElements = parents.sortWith(comparePosition)
    logger.info("ExtractFuncSelection: " + cachedSelectedElements)
    cachedSelectedElements
  }

  def getAvailableIdentifiers(morpheus: Morpheus, selection: Selection): List[Id] = getSelectedElements(morpheus, selection).isEmpty match {
    case true => null
    case false => List[Id]() // returns a empty list to signalize a valid selection was found
  }

  def isAvailable(morpheus: Morpheus, selection: Selection): Boolean = {
    val selectedElements = getSelectedElements(morpheus, selection)
    // Validate selection
    if (selectedElements.isEmpty) false
    else if (!selectedElements.par.forall(element => isPartOfAFunction(element, morpheus))) false
    else if (!isPartOfSameCompStmt(selectedElements, morpheus)) false
    else if (!filterAllASTElems[ReturnStatement](selectedElements, morpheus.getFeatureModel, morpheus.getASTEnv).isEmpty) false
    else if (!selectedElements.par.forall(element => !isBadExtractStatement(element, selectedElements, morpheus))) false
    // else if (!isConditionalComplete(selectedElements, getParentFunction(selectedElements, morpheus), morpheus)) false // Not Relevant?
    else true
  }

  def extract(morpheus: Morpheus, selection: List[AST], funcName: String): AST = {
    verifyFunctionName(funcName, selection, morpheus)

    // Analyze selection first -> either expression or statements
    // Workaraound for type erasure of Scala
    selection.head match {
      case e: Expr =>
        assert(false, "This refactoring is not yet supported!")
        morpheus.getAST
      case s: Statement => extractStatements(morpheus, selection, funcName)
      case _ =>
        assert(false, "Fatal error in selected elements!")
        morpheus.getAST
    }
  }

  private def extractStatements(morpheus: Morpheus, selection: List[AST], funcName: String): AST = {
    val parentFunction = getParentFunction(selection, morpheus)
    val parentFunctionOpt: Opt[FunctionDef] = parentOpt(parentFunction, morpheus.getASTEnv).asInstanceOf[Opt[FunctionDef]]
    val selectedOptStatements: List[Opt[Statement]] = selection.map(selected => parentOpt(selected, morpheus.getASTEnv)).asInstanceOf[List[Opt[Statement]]]
    val selectedIds = filterAllASTElems[Id](selection)
    val compStmt = getCompoundStatement(selectedOptStatements.head.entry, morpheus)

    logger.debug(selectedOptStatements)

    /**
     * Liveness analysis
     */
    val tb = ManagementFactory.getThreadMXBean
    val startTime = tb.getCurrentThreadCpuTime

    val externalUses = externalOccurrences(selectedIds, morpheus.getDeclUseMap)
    val externalDefs = externalOccurrences(selectedIds, morpheus.getUseDeclMap)
    val allExtRefIds = externalDefs.flatMap(x => Some(x._1))
    val extRefIds = uniqueExtRefIds(externalDefs, externalUses)
    val toDeclare = getIdsToDeclare(externalUses)

    // externalUses of selected Decls are currently refused
    if (!toDeclare.isEmpty) assert(false, "Invalid selection, a declared variable in the selection gets used outside.")
    val paramIds = getParamterIds(extRefIds, morpheus)

    logger.info("Liveness Analysis: " + (tb.getCurrentThreadCpuTime() - startTime) / 1000000 + " ms")
    logger.debug("ExternalUses: " + externalUses)
    logger.debug("ExternalDecls: " + externalDefs)
    logger.debug("Parameters " + extRefIds)



    val specifiers = generateSpecifiers(parentFunction, morpheus)
    val parameters = getParameterDecls(extRefIds, parentFunction, morpheus)
    // For not expected behaviour of pretty printer...
    /** val funcOpts = parameters.foldLeft(List[Opt[FunctionDef]]())((fDefs, funcParams) => {
      val declarator = generateDeclarator(funcName, List(funcParams))
      val compundStatement = generateCompoundStatement(selectedOptStatements, allExtRefIds, paramIds, morpheus)
      val newFunc = generateFuncDef(specifiers, declarator, compundStatement)
      val funcOpt = generateFuncOpt(parentFunction, newFunc, morpheus, funcParams.feature)
      fDefs ::: List(funcOpt)
    }) */
    val declarator = generateDeclarator(funcName, parameters)
    val compundStatement = generateCompoundStatement(selectedOptStatements, allExtRefIds, paramIds, morpheus)
    val newFunc = generateFuncDef(specifiers, declarator, compundStatement)
    val funcOpt = generateFuncOpt(parentFunction, newFunc, morpheus)

    val callParameters = generateFuncCallParameter(extRefIds, morpheus)
    val functionCall = Opt[ExprStatement](funcOpt.feature, ExprStatement(PostfixExpr(Id(funcOpt.entry.getName), FunctionCall(ExprList(callParameters)))))

    // Keep changes at the AST as local as possible
    //val insertCall2 = funcOpts.foldLeft()
    val insertedCall = insertBefore(compStmt.innerStatements, selectedOptStatements.head, functionCall)
    val ccStmtWithRemovedStmts = eqRemove(insertedCall, selectedOptStatements)
    // val astWFunc = funcOpts.foldLeft(morpheus.getAST)((ast, func) => insertInAstBefore(ast, parentFunctionOpt, func))
    val astWFunc = insertInAstBefore(morpheus.getAST, parentFunctionOpt, funcOpt)

    val refAST = replaceInAST(astWFunc, compStmt, compStmt.copy(innerStatements = ccStmtWithRemovedStmts))
    refAST
  }

  private def getParamterIds(liveParamIds: List[Id], morpheus: Morpheus) = retrieveParameters(liveParamIds, morpheus).flatMap(entry => Some(entry._3))

  private def getParameterDecls(liveParamIds: List[Id], funcDef: FunctionDef, morpheus: Morpheus) = {
    val decls = retrieveParameters(liveParamIds, morpheus).flatMap(entry => Some(entry._1))
    /*Workaround for missing choices and bad behaviour of the prettyPrinter
    val features = decls.foldLeft(List[FeatureExpr]())((l, decl) => {
      if (!l.exists(f => f.equivalentTo(decl.feature))) l ::: List(decl.feature) else l
    })
    val declsFeature = features.foldLeft(List[Opt[DeclaratorExtension]]())((l, feature) => {
      val nDecls = decls.foldLeft(List[Opt[ParameterDeclaration]]())((nl, entry) => if ((feature.implies(entry.feature).isTautology())) (nl ::: List(entry)) else nl)
      l ::: List[Opt[DeclaratorExtension]](Opt(parentOpt(funcDef, morpheus.getASTEnv).feature.and(feature), DeclParameterDeclList(nDecls)))
    })
    declsFeature */
    List[Opt[DeclaratorExtension]](Opt(parentOpt(funcDef, morpheus.getASTEnv).feature, DeclParameterDeclList(decls)))
  }

  private def retrieveParameters(liveParamIds: List[Id], morpheus: Morpheus): List[(Opt[ParameterDeclaration], Opt[Expr], Id)] = {
    val declIdMap: IdentityHashMap[Declaration, Id] = new IdentityHashMap
    val declFeatureMap: IdentityHashMap[Declaration, FeatureExpr] = new IdentityHashMap
    val declDeclPointerMap: IdentityHashMap[Declaration, Declarator] = new IdentityHashMap
    def addTodeclIdMapMap(decl: Declaration, id: Id) = if (!declIdMap.containsKey(decl)) declIdMap.put(decl, id)
    def addToDeclFeatureMap(decl: Declaration, declFeature: FeatureExpr) = if (declFeatureMap.containsKey(decl)) declFeatureMap.put(decl, declFeature.and(declFeatureMap.get(decl))) else declFeatureMap.put(decl, declFeature)
    def addTodeclDeclPointerMap(decl: Declaration, declarator: Declarator) = if (!declDeclPointerMap.containsKey(decl)) declDeclPointerMap.put(decl, declarator)

    /**
     * Generates the init declaration for variables declared in the method body.
     */
    def generateInit(decl: Declaration, param: Id, array: Boolean = false): Declarator = {
      // make pointer
      var pointer = List[Opt[Pointer]]()
      decl.declSpecs.foreach(declSpec => pointer :::= List[Opt[Pointer]](Opt(declSpec.feature, Pointer(List[Opt[Specifier]]()))))
      decl.init.foreach(declInit => pointer :::= declInit.entry.declarator.pointers)

      //if (array) AtomicNamedDeclarator(pointer, Id(param.name), List[Opt[DeclaratorExtension]](Opt(FeatureExprFactory.True, DeclArrayAccess(None))))
      AtomicNamedDeclarator(pointer, Id(param.name), List[Opt[DeclaratorExtension]]())
    }

    def addChoice(c: Choice[_], id: Id, ft: FeatureExpr = FeatureExprFactory.True): Unit = {
      c match {
        case c@Choice(cft, o1@One(_), o2@One(_)) =>
          addOne(o1, id)
          addOne(o2, id /*, cft.not()*/)
        case c@Choice(cft, c1@Choice(_, _, _), o2@One(_)) =>
          addChoice(c1, id)
          addOne(o2, id)
        case c@Choice(cft, o1@One(_), c1@Choice(_, _, _)) =>
          addChoice(c1, id)
          addOne(o1, id)
        case c@Choice(cft, c1@Choice(_, _, _), c2@Choice(_, _, _)) =>
          addChoice(c1, id)
          addChoice(c2, id)
      }
    }

    def addOne(o: One[_], id: Id, ft: FeatureExpr = FeatureExprFactory.True) = {
      o match {
        // only variables are interesting
        case o@One((CUnknown(_), _, _)) =>
        case o@One((CFunction(_, _), _, _)) =>

        /** case o@One((CArray(_,_),_,_)) =>
          val decl = findPriorASTElem[Declaration](id, morpheus.getASTEnv)
          decl match {
            case Some(_) =>
              var feature: FeatureExpr = FeatureExprFactory.True
              if (ft.equivalentTo(FeatureExprFactory.True)) feature = parentOpt(decl.get, morpheus.getASTEnv).feature
              else feature = ft
              addToDeclFeatureMap(decl.get, feature)
              addTodeclDeclPointerMap(decl.get, generateInit(decl.get, id, true))
              addTodeclIdMapMap(decl.get, id)
            case x => logger.error("Missed " + x)
          }  */
        // TODO Better enum handling
        case o@One((CSigned(CInt()), KEnumVar, _)) =>
          if (morpheus.getUseDeclMap.get(id).exists(t => findPriorASTElem[CompoundStatement](t, morpheus.getASTEnv) match {
            case None => false
            case _ => true
          })) assert(false, "Type Declaration for " + id.name + " would be invisible after extraction!")
        case o =>
          val decl = findPriorASTElem[Declaration](id, morpheus.getASTEnv)
          decl match {
            case Some(_) =>
              var feature: FeatureExpr = FeatureExprFactory.True
              if (ft.equivalentTo(FeatureExprFactory.True)) feature = parentOpt(decl.get, morpheus.getASTEnv).feature
              else feature = ft
              addToDeclFeatureMap(decl.get, feature)
              addTodeclDeclPointerMap(decl.get, generateInit(decl.get, id))
              addTodeclIdMapMap(decl.get, id)
            case x => logger.error("Missed " + x)
          }
      }
    }

    liveParamIds.foreach(liveId =>
      try {
        // only lookUp variables
        // TODO Refactor
        morpheus.getEnv(liveId).varEnv.lookup(liveId.name) match {
          case o@One(_) => addOne(o, liveId)
          case c@Choice(_, _, _) => addChoice(c, liveId)
          case x =>
            logger.warn("Missed pattern choice? " + x)
          // logger.debug(morpheus.getEnv(param).varEnv.lookup(param.name))
        }
      } catch {
        case e: Exception => // logger.warn("No entry found for: " + param)
      })
    val decls = declFeatureMap.keySet().toArray(Array[Declaration]()).toList
    decls.flatMap(decl => {
      val feature = decls.foldLeft(declFeatureMap.get(decl))((ft, otherDecl) => {
        if (declDeclPointerMap.get(decl).getName.equals(declDeclPointerMap.get(otherDecl).getName) && !(decl.eq(otherDecl))) {
          val andFeature = declFeatureMap.get(otherDecl).not()
          if (!andFeature.equivalentTo(FeatureExprFactory.False)) ft.and(andFeature)
          else ft
        }
        else ft
      })
      decl.declSpecs.foreach(spec => {
        spec.entry match {
          case t@TypeDefTypeSpecifier(i@Id(_)) =>
            if (morpheus.getUseDeclMap.get(i).exists(t => findPriorASTElem[CompoundStatement](t, morpheus.getASTEnv) match {
              case None => false
              case _ => true
            })) assert(false, "Type Declaration for " + i + " would be invisible after extraction!")
          case s@StructOrUnionSpecifier(_, Some(i@Id(_)), _) =>
            if (morpheus.getUseDeclMap.get(i).exists(t => findPriorASTElem[CompoundStatement](t, morpheus.getASTEnv) match {
              case None => false
              case _ => true
            })) assert(false, "Type Declaration for " + i + " would be invisible after extraction!")
          case _ => logger.debug("Specs " + spec)
        }
      }
      )
      val pD = Opt(feature, ParameterDeclarationD(decl.declSpecs, declDeclPointerMap.get(decl)))
      val expr = Opt(feature, PointerCreationExpr(Id(declDeclPointerMap.get(decl).getName)))
      val id = declIdMap.get(decl)
      Some((pD, expr, id))
    })
  }

  private def isPartOfSameCompStmt(selectedElements: List[AST], morpheus: Morpheus): Boolean =
    findPriorASTElem[CompoundStatement](selectedElements.head, morpheus.getASTEnv) match {
      case Some(c) => selectedElements.par.forall(element => isElementOfEqCompStmt(element, c, morpheus))
      case _ => false // not element of an ccStmt
    }

  /**
   * Generates the parameters requiered in the function stmt.
   */
  private def generateFuncCallParameter(extRefIds: List[Id], morpheus: Morpheus) = retrieveParameters(extRefIds, morpheus).flatMap(entry => Some(entry._2))


  private def uniqueExtRefIds(defs: List[(Id, List[Id])], uses: List[(Id, List[Id])]) = {
    val parameterIds = Collections.newSetFromMap[Id](new util.IdentityHashMap())
    defs.foreach(x => x._2.foreach(entry => parameterIds.add(entry)))
    uses.foreach(x => parameterIds.add(x._1))
    parameterIds.toArray(Array[Id]()).toList.sortWith(compareByName)
  }

  private def getIdsToDeclare(uses: List[(Id, List[Id])]) = {
    val declarationIds = Collections.newSetFromMap[Id](new util.IdentityHashMap())
    uses.foreach(id => declarationIds.add(id._1))
    declarationIds.toArray(Array[Id]()).toList.sortWith(compareByName)
  }


  private def generateCompoundStatement(statements: List[Opt[Statement]], externalRef: List[Id], parameters: List[Id], morpheus: Morpheus): CompoundStatement = {
    def isPartOfParameter(id: Id, params: List[Id], morpheus: Morpheus): Boolean = {
      if (!morpheus.getUseDeclMap.containsKey(id)) false
      morpheus.getUseDeclMap.get(id).exists(decl => params.exists(param => param.eq(decl)))
    }

    val variables = externalRef.par.flatMap(id => isPartOfParameter(id, parameters, morpheus) match {
      case true => Some(id)
      case _ => None
    }).toList
    // Make Pointer
    val idsAsPointer = variables.foldLeft(statements)((stmts, id) => replaceInAST(stmts, id, PointerDerefExpr(id)))
    CompoundStatement(idsAsPointer)
  }


  private def externalOccurrences(ids: List[Id], map: util.IdentityHashMap[Id, List[Id]]) =
    ids.par.flatMap(id => {
      if (map.containsKey(id)) {
        val external = map.get(id).par.flatMap(aId => {
          if (ids.par.exists(oId => oId.eq(aId))) None
          else Some(aId)
        }).toList
        if (external.isEmpty) None
        else Some(id, external)
      } else None
    }).toList

  private def isPartOfAFunction(toValidate: AST, morpheus: Morpheus): Boolean =
    findPriorASTElem[FunctionDef](toValidate, morpheus.getASTEnv) match {
      case Some(f) => true
      case _ => false
    }

  private def isElementOfEqCompStmt(element: AST, compStmt: CompoundStatement, morpheus: Morpheus) = getCompoundStatement(element, morpheus).eq(compStmt)

  private def getCompoundStatement(element: AST, morpheus: Morpheus): CompoundStatement =
    findPriorASTElem[CompoundStatement](element, morpheus.getASTEnv) match {
      case Some(c) => c
      case _ => null
    }

  private def verifyFunctionName(funcName: String, selection: List[AST], morpheus: Morpheus) {
    assert(isValidName(funcName), Configuration.getInstance().getConfig("refactor.extractFunction.failed.shadowing"))
    // Check for shadowing with last statement of the extraction compound statement
    assert(!isShadowed(funcName, getCompoundStatement(selection.head, morpheus).innerStatements.last.entry, morpheus), Configuration.getInstance().getConfig("default.error.invalidName"))
  }

  /**
   * Conditional complete?
   */
  private def isConditionalComplete(selection: List[AST], parentFunction: FunctionDef, morpheus: Morpheus): Boolean = {
    if (selection.isEmpty) return false

    if (!selectionIsConditional(selection)) return true
    // no variable configuration -> conditional complete

    if (!(filterAllFeatureExpr(selection).toSet.size > 1)) return true
    // only one and the same feature -> conditonal complete

    val expr1 = selection.head.asInstanceOf[Opt[_]]
    val expr2 = selection.last.asInstanceOf[Opt[_]]

    if (expr1.feature.equivalentTo(expr2.feature)) return true
    // start and end feature are the same -> eligable


    val prevState = prevOpt(expr1, morpheus.getASTEnv)
    val nextState = nextOpt(expr2, morpheus.getASTEnv)

    if (((prevState != null) && (prevState.feature.equals(expr2.feature)))
      || ((nextState != null) && (nextState.feature.equals(expr1.feature)))
      || ((prevState != null) && (nextState != null) && nextState.feature.equals(prevState.feature))) return true
    // prev feature and next feature are the same -> eligible

    // TODO Null States!
    false
  }

  /**
   * InlineFuncOptionSelector is conditonal?
   */
  private def selectionIsConditional(selection: List[AST]) = selection.exists(x => (isVariable(x)))

  private def isBadExtractStatement(element: AST, selection: List[AST], morpheus: Morpheus): Boolean = {

    def filter[T <: AST](stmts: List[AST])(implicit m: ClassManifest[T]) = {
      stmts.exists(stmt => {
        findPriorASTElem[T](stmt, morpheus.getASTEnv) match {
          case None => false
          case Some(x) =>
            selection.exists(s =>
              if (s.eq(x)) true
              else filterAllASTElems[T](s, morpheus.getASTEnv).par.exists(fs => fs.eq(x)))
        }
      })
    }

    val cStmt = filterAllASTElems[ContinueStatement](element)
    cStmt.isEmpty match {
      case true =>
      case _ =>
        if (filter[ForStatement](cStmt)) return false
        if (filter[DoStatement](cStmt)) return false
        if (filter[WhileStatement](cStmt)) return false
        return true
    }
    val bStmt = filterAllASTElems[BreakStatement](element)
    bStmt.isEmpty match {
      case true =>
      case _ =>
        if (filter[DoStatement](bStmt)) return false
        if (filter[WhileStatement](bStmt)) return false
        if (filter[ForStatement](bStmt)) return false
        if (filter[SwitchStatement](bStmt)) return false
        return true
    }
    val caStmt = filterAllASTElems[CaseStatement](element)
    caStmt.isEmpty match {
      case true =>
      case _ =>
        if (filter[SwitchStatement](caStmt)) return false
        return true
    }
    val gotoS = filterAllASTElems[GotoStatement](element)
    gotoS.isEmpty match {
      case true =>
      case _ => return !gotoS.exists(goto => morpheus.getUseDeclMap.get(goto).exists(labels => filter[Id](gotoS)))
    }
    val labels = filterAllASTElems[LabelStatement](element)
    labels.isEmpty match {
      case true =>
      case _ => return !labels.exists(label => morpheus.getDeclUseMap.get(label).exists(goto => filter[Id](labels)))
    }
    false

    /**
    element match {
    case c: ContinueStatement => true
    case b: BreakStatement => true
    case c: CaseStatement => true
    case g: GotoStatement => true // TODO Target Find
    case _ => false
  } */
  }

  private def getParentFunction(selection: List[AST], morpheus: Morpheus): FunctionDef = {
    findPriorASTElem[FunctionDef](selection.head, morpheus.getASTEnv) match {
      case Some(f) => f
      case _ => null
    }
  }

  /**
   * Generates the required specifiers.
   */
  private def generateSpecifiers(funcDef: FunctionDef, morpheus: Morpheus /* , typeSpecifier: Opt[Specifier] = Opt(FeatureExprFactory.True, VoidSpecifier()) */): List[Opt[Specifier]] = {
    var specifiers: List[Opt[Specifier]] = List(Opt(parentOpt(funcDef, morpheus.getASTEnv).feature, VoidSpecifier()))

    // preserv specifiers from function definition except type specifiers
    funcDef.specifiers.foreach(specifier => {
      specifier.entry match {
        case InlineSpecifier() => specifiers ::= specifier
        case AutoSpecifier() => specifiers ::= specifier
        case RegisterSpecifier() => specifiers ::= specifier
        case VolatileSpecifier() => specifiers ::= specifier
        case ExternSpecifier() => specifiers ::= specifier
        case ConstSpecifier() => specifiers ::= specifier
        case RestrictSpecifier() => specifiers ::= specifier
        case StaticSpecifier() => specifiers ::= specifier
        case _ =>
      }
    })
    specifiers
  }

  /**
   * Generates the function definition.
   */
  private def generateFuncDef(specs: List[Opt[Specifier]], decl: Declarator, stmts: CompoundStatement, oldStyleParameters: List[Opt[OldParameterDeclaration]] = List[Opt[OldParameterDeclaration]]()) = FunctionDef(specs, decl, oldStyleParameters, stmts)

  /**
   * Generates the opt node for the ast.
   */
  private def generateFuncOpt(oldFunc: FunctionDef, newFunc: FunctionDef, morpheus: Morpheus, feature: FeatureExpr = FeatureExprFactory.True) = Opt[FunctionDef](parentOpt(oldFunc, morpheus.getASTEnv).feature.and(feature), newFunc)

  /**
   * Generates the decl.
   */
  private def generateDeclarator(name: String /*, pointer: List[Opt[Pointer]] = List[Opt[Pointer]]()*/ , extensions: List[Opt[DeclaratorExtension]] = List[Opt[DeclaratorExtension]]()) = AtomicNamedDeclarator(List[Opt[Pointer]](), Id(name), extensions)
}