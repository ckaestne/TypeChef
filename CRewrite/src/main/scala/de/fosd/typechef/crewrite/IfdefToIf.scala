package de.fosd.typechef.crewrite

import de.fosd.typechef.parser.c._
import org.kiama.rewriting.Rewriter._
import de.fosd.typechef.conditional._
import de.fosd.typechef.crewrite.CASTEnv._
import de.fosd.typechef.featureexpr._
import sat._
import collection.mutable.ListBuffer
import java.util

/**
 * strategies to rewrite ifdefs to ifs
 */

class IfdefToIf extends ASTNavigation with ConditionalNavigation {

  val CONFIGPREFIX = "v_"
  var counter = 0
  var IdMap:Map[FeatureExpr, Int] = Map()
  var IdMap2:Map[FeatureExpr, Int] = Map()
  var IdSet:Set[FeatureExpr] = Set()
  var fctMap:Map[Id, Map[FeatureExpr, String]] = Map()
  var jmpMap:Map[String, Map[FeatureExpr, String]] = Map()


  //    private val rewriteStrategy = everywherebu(rule {
  //        case Opt(f, stmt: Statement) if (!f.isTautology) =>
  //            Opt(base, IfStatement(featureToCExpr(f), One(stmt), List(), None))
  //        case Choice(f, One(a: Statement), One(b: Statement)) =>
  //            One(IfStatement(featureToCExpr(f), One(a), List(), Some(One(b))))
  //    })
  //
  //    def rewrite(ast: AST): AST = {
  //        rewriteStrategy(ast).get.asInstanceOf[AST]
  //    }

  // this method replaces a given element e within a structure t with the elements
  // n; we only match elements inside lists (case l: List[Opt[T]] => )
  def replace[T <: Product](t: T, e: Opt[_], n: List[Opt[_]]): T = {
    val r = all(rule {
      case l: List[Opt[_]] => l.flatMap({
        x => if (x.eq(e)) n else x :: Nil
      })
    })

    r(t).get.asInstanceOf[T]
  }

  def replaceOnce[T <: Product](t: T, e: Opt[_], n: Opt[_]): T = {
    val r = oncetd(rule {
      case l: List[Opt[_]] => l.flatMap({x => if (x.eq(e)) n::Nil else x::Nil})
    })
    r(t).get.asInstanceOf[T]
  }

  def replaceSame[T <: Product](t: T, e: Opt[_], n: Opt[_]): T = {
    val r = manytd(rule {
      case l: List[Opt[_]] => l.flatMap({x => if (x.eq(e)) n::Nil else x::Nil})
    })
    r(t).get.asInstanceOf[T]
  }
  
  def replaceOptByOpts[T <: Product](t: T, e: Opt[_], n: List[Opt[_]]): T = {
    // println("Replacing\n" + e + "\nwith\n" + n + "\n\nin:\n" + t +"\n\n")
    val r = manytd(rule {
      case l: List[Opt[_]] => l.flatMap({x => if (x.eq(e)) n else x::Nil})
    })
    r(t).get.asInstanceOf[T]
  }

  def replaceWithListOnce[T <: Product](t: T, e: Opt[_], n: List[Opt[_]]): T = {
    // println("Replacing\n" + e + "\nwith\n" + n + "\n\nin:\n" + t +"\n\n")
    val r = oncetd(rule {
      case l: List[Opt[_]] => l.flatMap({x => if (x.eq(e)) n else x::Nil})
    })
    r(t).get.asInstanceOf[T]
  }

  def replaceRecursive[T <: Product](t: T, e: ListBuffer[Opt[_]], n: ListBuffer[List[Opt[_]]]): T = {
    if (e.size > 0 && n.size > 0) {
      replaceRecursive(replaceOptByOpts(t, e(0), n(0)), e.tail, n.tail)
    } else {
      return t
    }
  }

  def replaceWithMap[T <: Product](t: T, map: Map[Opt[_], List[Opt[_]]]): T = {
    val r = manytd(rule {
      case l: List[Opt[_]] => l.flatMap({x => if (map.contains(x)) map.get(x).get else x::Nil})
    })
    r(t).get.asInstanceOf[T]
  }

  def insertBefore[T <: Product](t: T, mark: Opt[_], insert: Opt[_]): T = {
    val r = oncetd(rule {
      case l: List[Opt[_]] => l.flatMap({x => if (x.eq(mark)) insert::x::Nil else x::Nil})
    })
    r(t).get.asInstanceOf[T]
  }


  def featureToCExpr(feature: FeatureExpr): Expr = feature match {
    case d: DefinedExternal => PostfixExpr(Id("options"),PointerPostfixSuffix(".", Id(d.feature.toLowerCase())))
    case a: And =>
      val l = a.clauses.toList
      var del = List[Opt[NArySubExpr]]()
      for (e <- l.tail)
        del = del ++ List(Opt(True, NArySubExpr("&&", featureToCExpr(e))))
      NAryExpr(featureToCExpr(l.head), del)
    case o: Or =>
      val l = o.clauses.toList
      var del = List[Opt[NArySubExpr]]()
      for (e <- l.tail)
        del = del ++ List(Opt(FeatureExprFactory.True, NArySubExpr("||", featureToCExpr(e))))
      NAryExpr(featureToCExpr(l.head), del)
    case Not(n) => UnaryOpExpr("!", featureToCExpr(n))
  }

  def definedExternalToStruct(defExSet: Set[DefinedExternal]) : CompoundStatement = {
    def getAllStructDeclarators(defExSet: Set[DefinedExternal]) : List[Opt[StructDeclaration]] = {
      def defExToOpt(defEx: DefinedExternal) : Opt[StructDeclaration] = {
        val all = Opt(True, StructDeclaration(List(Opt(True,IntSpecifier())),List(Opt(True,StructDeclarator(AtomicNamedDeclarator(List(),Id(defEx.feature.toLowerCase),List()),None,List())))))
        all
      }
      if (defExSet.size > 0) {
        List(defExToOpt(defExSet.head)) ++ getAllStructDeclarators (defExSet.tail)
      } else {
        List()
      }
    }
    def structHeader(structDecl: List[Opt[StructDeclaration]]) : List[Opt[Declaration]] = {
      val struct = List(Opt(True, Declaration(List(Opt(True, StructOrUnionSpecifier(false, Some(Id("options")), structDecl))), List())))
      struct
    }

    val all = structHeader(getAllStructDeclarators(defExSet))
    val cstmt = CompoundStatement(all.asInstanceOf[List[Opt[Statement]]])
    cstmt
  }
  def filterFeatures(a: Any, env: ASTEnv) : Set[DefinedExternal] = {
    def filterFeatureExpressions(lst: List[FeatureExpr]) : Set[DefinedExternal] = {
      if (lst.size > 0) {
        //println(lst(0).collectDistinctFeatures.toSet)
        lst(0).collectDistinctFeatures2 ++ filterFeatureExpressions (lst.tail)
      } else {
        Set()
      }
    }
    def getFeatureExpressions(a: Any, env:ASTEnv): List[FeatureExpr] = {
      a match {
        case o: Opt[_] => if (o.feature == FeatureExprFactory.True) List() ++ o.productIterator.toList.flatMap(getFeatureExpressions(_, env)) else List(o.feature)
        case l: List[_] => l.flatMap(getFeatureExpressions(_, env))
        case p: Product => p.productIterator.toList.flatMap(getFeatureExpressions(_, env))
        case t: FeatureExpr => if (t == FeatureExprFactory.True) List() else List(t)
        case _ => List()
      }
    }

    filterFeatureExpressions(getFeatureExpressions(a, env))
  }

  def filterInvariableOpts(a: Any,  env:ASTEnv): List[Opt [_]] = {
    a match {
      case o: Opt[_] => if (isVariable(o, env)) List(o) else List() ++ o.productIterator.toList.flatMap(filterInvariableOpts(_, env))
      case l: List[_] => l.flatMap(filterInvariableOpts(_, env))
      case p: Product => p.productIterator.toList.flatMap(filterInvariableOpts(_, env))
      case _ => List()
    }
  }

  def getNewFunctionDef(a: AST) : List[List[FunctionDef]] = {
    val tempLst = filterASTElems[FunctionDef](a)
    val lst = tempLst.filter(x => x.specifiers.length > 1)
    def rename(list: List[FunctionDef]) : List[List[FunctionDef]] = {
      def getAll(fd: FunctionDef) : List[FunctionDef] = {
        val map = functionMap(a)
        def getNewId(name: Id, specifiers: List[Opt[_]]) : List[Id] = {
          if (specifiers.length > 0) {
            List(Id(map.get(name).get(specifiers(0).feature))) ++ getNewId (name, specifiers.tail)
          } else {
            List()
          }
        }
        def getSpecs(specs: List[Opt[Specifier]]) : List[Specifier] = {
          if (specs.length > 0) {
            List(specs(0).entry) ++ getSpecs(specs.tail)
          } else {
            List()
          }
        }
        def getDeclarator(decl: Declarator, ids: List[Id]) : List[AtomicNamedDeclarator] = {
          if (ids.length > 0) {
            List(AtomicNamedDeclarator(decl.pointers, ids(0), decl.extensions)) ++ getDeclarator(decl, ids.tail)
          } else {
            List()
          }
        }
        def getNewFunctions(decl: List[Declarator], spec: List[Specifier], fd: FunctionDef) : List[FunctionDef] = {
          if (decl.length > 0) {
            List(FunctionDef(List(Opt(FeatureExprFactory.True, spec(0))), decl(0), fd.oldStyleParameters, fd.stmt)) ++
              getNewFunctions(decl.tail, spec.tail, fd)
          } else {
            List()
          }
        }
        getNewFunctions(getDeclarator(fd.declarator,
          getNewId(fd.declarator.asInstanceOf[AtomicNamedDeclarator].id, fd.specifiers)), getSpecs(fd.specifiers), fd)
      }
      if (list.size > 0) {
        List(getAll(list(0))) ++ rename (list.tail)
      } else {
        List()
      }
    }
    rename(lst)
  }

  def replaceConvertFunctions(a: AST, e: ASTEnv, idHashMap: util.IdentityHashMap[Id, List[Id]]) : AST = {
    val tempAST = replaceFunctionDef(a, e)
    val finalAST = convertFunctionCalls(tempAST, e, idHashMap)
    finalAST
  }

  def replaceConvertFunctionsNew(a: AST, e: ASTEnv) : AST = {
    val tempAST = replaceFunctionDef(a, e)
    val finalAST = convertFunctionCallsNew(tempAST, e)
    finalAST
  }

  def replaceFunctionDef(a: AST, e: ASTEnv) : AST = {
    var replaceMap:Map[Opt[_], List[Opt[_]]] = Map()
    val tempLst = filterASTElems[FunctionDef](a)
    val lst = tempLst.filter(x => x.specifiers.length > 1)
    val lstLst = getNewFunctionDef(a)
    if (lst.size == lstLst.size) {
      for (i <- 0 to (lst.size - 1)) {
        val newOpts = listFDefToOpt(lstLst(i))
        val oldOpt = getFunctionOpt(e, lst(i))
        replaceMap += (oldOpt -> newOpts)
      }
    }
    replaceWithMap(a, replaceMap)
  }

  def getFunctionOpt(env: ASTEnv, fd: FunctionDef) : Opt[FunctionDef] = {
    env.parent(fd).asInstanceOf[Opt[FunctionDef]]
  }


  def listFDefToOpt(lst: List[FunctionDef]) : List[Opt[FunctionDef]] = {
    if (lst.size > 0) {
      List(Opt(FeatureExprFactory.True, lst.head)) ++ listFDefToOpt(lst.tail)
    } else {
      List()
    }
  }



  def functionMap(a: AST) : Map[Id, Map[FeatureExpr, String]] = {
    val functions = filterASTElems[FunctionDef](a)
    val fct = functions.filter(s => s.specifiers.length > 1)
    var M:Map[Id,  Map[FeatureExpr,  String]] = Map()
    var M2:Map[FeatureExpr,  String] = Map()
    def addMap(lst: List[FunctionDef]) = {
      def addSMap(name: Id, specs: List[Opt[Specifier]]) = {
        specs.foreach(x => if(!IdMap.contains(x.feature)) {
          IdMap += (x.feature -> IdMap.size)
        })
        specs.foreach(x => if (IdMap.contains(x.feature)) {
          M2 += (x.feature -> ("_" + IdMap.get(x.feature).get + "_" + name.name))
        })
        M += (name -> M2)
        M2 = Map()
      }
      lst.foreach(y => addSMap(y.declarator.asInstanceOf[AtomicNamedDeclarator].id, y.specifiers))
    }
    // println("Found " + fct.length + " functions with more than one specifier.")
    addMap(fct)
    println("\nMap:\n" + M + "\n\n")
    fctMap = M
    M
  }

  def convertLabelStmt(lStmt: Opt[LabelStatement]) : Opt[IfStatement] = {
    val ifBranch = featureToCExpr(lStmt.feature)
    val thenBranch = One(CompoundStatement(List(Opt(FeatureExprFactory.True,LabelStatement(lStmt.entry.id, None)))))
    val ifStmt = Opt(FeatureExprFactory.True, IfStatement(One(ifBranch), thenBranch, List(), None))
    ifStmt
  }


  def replaceLabelsGotos(a: AST, env: ASTEnv) : AST = {
    def getParent(lst: List[LabelStatement], e: ASTEnv) : List[Opt[LabelStatement]] = {
      if (lst.length > 0) {
        List(e.parent(lst(0)).asInstanceOf[Opt[LabelStatement]]) ++ getParent(lst.tail, e)
      } else {
        List()
      }
    }

    def getMap(oldMap: Map[Id, List[Opt[LabelStatement]]]) : Map[String, Map[FeatureExpr,  String]] = {
      var M:Map[String,  Map[FeatureExpr,  String]] = Map()
      def getInnerMap(lst: List[Opt[LabelStatement]]) : Map[FeatureExpr,  String] = {
        var M2:Map[FeatureExpr,  String] = Map()
        lst.foreach(x => if (IdMap.contains(x.feature)) {
          M2 += (x.feature -> ("_" + IdMap.get(x.feature).get + "_" + x.entry.id.name))
        } else {
          IdMap += (x.feature -> IdMap.size)
          M2 += (x.feature -> ("_" + IdMap.get(x.feature).get + "_" + x.entry.id.name))
        })
        M2
      }
      oldMap.foreach(x => M += (x._1.name -> getInnerMap (x._2)))
      M
    }

    def replaceLabels(lst: List[Opt[LabelStatement]], ast: AST, env: ASTEnv) : AST = {
      if (lst.size > 0) {
        val ifBranch = featureToCExpr(lst(0).feature)
        val thenBranch = One(CompoundStatement(List(Opt(FeatureExprFactory.True,LabelStatement(Id("_" + IdMap.get(lst(0).feature).get + "_" + lst(0).entry.id.name), None)))))
        val ifStmt = Opt(FeatureExprFactory.True, IfStatement(One(ifBranch), thenBranch, List(), None))
        val newAst = replaceSame(ast, lst(0), ifStmt)
        val optLabels = getParent(filterASTElems[LabelStatement](ast), env).filter(s => s.feature != FeatureExprFactory.True)
        replaceLabels(optLabels, newAst, createASTEnv(newAst))
      } else {
        ast
      }
    }


    val labelList = filterASTElems[LabelStatement](a)
    val optLabels = getParent(labelList, env)
    val filteredOptLabels = optLabels.filter(s => s.feature != FeatureExprFactory.True)
    val groupedOptLabels = filteredOptLabels.groupBy(x => x.entry.id)
    val map = getMap(groupedOptLabels)


    def replaceGotos(lst: List[GotoStatement], ast: AST, env: ASTEnv) : AST = {
      if (lst.size > 0) {
        val name = lst(0).target.asInstanceOf[Id].name
        val innerMap = map.get(name).get
        val optBuffer: ListBuffer[Opt[IfStatement]] = ListBuffer()
        innerMap.foreach(x => optBuffer += Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(x._1)),One(CompoundStatement(List(Opt(FeatureExprFactory.True,GotoStatement(Id(x._2)))))),List(),None)))
        val optList = optBuffer.toList
        val newAst = replaceOptByOpts(ast, env.parent(lst(0)).asInstanceOf[Opt[GotoStatement]], optList)
        val newLst = filterASTElems[GotoStatement](newAst).filter(x => map.contains(x.target.asInstanceOf[Id].name))
        replaceGotos(newLst, newAst, createASTEnv(newAst))
      } else {
        ast
      }
    }

    var newAst = replaceLabels(filteredOptLabels, a, env)
    val filteredGotoStmts = filterASTElems[GotoStatement](a).filter(x => map.contains(x.target.asInstanceOf[Id].name))

    newAst = replaceGotos(filteredGotoStmts, newAst, createASTEnv(newAst))


    newAst
  }

  def getFeatureForId(id: Int) : FeatureExpr = {
    if (IdMap.size < id || id < 0) {
      null
    } else {
      val it = IdMap.iterator
      while (it.hasNext) {
        val next = it.next()
        if (next._2.equals(id)) {
          return next._1
        }
      }
      return null
    }
  }

  def convertFunctionCalls(a: AST,  env: ASTEnv, defUseMap: util.IdentityHashMap[Id, List[Id]]) : AST = {
    val map = fctMap
    var replaceMap:Map[Opt[_], List[Opt[_]]] = Map()

    def convertFunctCall(ft: FeatureExpr, newName: String, fct: FunctionCall) : Opt[IfStatement] = {
      val ifBranch = featureToCExpr(ft)
      val thenBranch = One(CompoundStatement(List(Opt(FeatureExprFactory.True, ExprStatement(PostfixExpr(Id(newName), fct))))))
      val ifStmt = IfStatement(One(ifBranch), thenBranch, List(), None)
      Opt(FeatureExprFactory.True, ifStmt)
    }
    def mapToOptList(map: Map[FeatureExpr, String], fct: FunctionCall) : List[Opt[_]] = {
      val it = map.iterator
      val optBuffer = new ListBuffer[Opt[IfStatement]]
      while (it.hasNext) {
        val current = it.next()
        optBuffer += convertFunctCall(current._1, current._2, fct)
      }
      val optLst = optBuffer.toList
      optLst
    }

    fctMap.keySet.foreach(x => if (defUseMap.containsKey(x)) {
      val replaceList = defUseMap.get(x)
      if (replaceList.size > 0) {
        replaceList.foreach(y => replaceMap += (idToOptExpr(y, env) -> mapToOptList(fctMap.get(x).get, idToFunctionCall(y, env))))
      }
    })
    replaceWithMap(a, replaceMap)
  }

  def convertFunctionCallsNew(a: AST,  env: ASTEnv) : AST = {
    val map = fctMap
    val filteredFCT = filterASTElems[FunctionCall](a).filter(s => (env.parent(s).isInstanceOf[PostfixExpr] && env.parent(s).asInstanceOf[PostfixExpr].p.isInstanceOf[Id] && map .contains (env.parent(s).asInstanceOf[PostfixExpr].p.asInstanceOf[Id])))
    var replaceMap:Map[Opt[_], List[Opt[_]]] = Map()

    def convertFC(ast: AST, fct: FunctionCall) {
      val toBeReplaced = env.parent(env.parent(env.parent(fct))).asInstanceOf[Opt[_]]
      val parent = env.parent(fct).asInstanceOf[PostfixExpr]
      val expr = parent.p
      val innerMap = map.get(expr.asInstanceOf[Id]).get

      def convertSingleFunctionCall(ft: FeatureExpr, functName: String, functCall: FunctionCall) : Opt[IfStatement] = {
        val ifBranch = featureToCExpr(ft)
        val thenBranch = One(CompoundStatement(List(Opt(FeatureExprFactory.True,ExprStatement(PostfixExpr(Id(functName), functCall))))))
        val ifStmt = IfStatement(One(ifBranch), thenBranch, List(), None)
        Opt(FeatureExprFactory.True, ifStmt)
      }

      val it = innerMap.iterator
      val optBuffer = new ListBuffer[Opt[IfStatement]]
      while (it.hasNext) {
        val current = it.next()
        optBuffer += convertSingleFunctionCall(current._1, current._2, fct)
      }
      val optLst = optBuffer.toList
      replaceMap += (toBeReplaced -> optLst)
    }
    filteredFCT.foreach(x => convertFC(a, x))
    println(replaceMap)
    return replaceWithMap(a, replaceMap)
  }

  def filterCaseOpt(a: AST, env: ASTEnv) : List[Opt[CaseStatement]] = {
    val caseLst = filterASTElems[CaseStatement](a)
    val filteredList = caseLst.filter(x => env.parent(x).asInstanceOf[Opt[CaseStatement]].feature != FeatureExprFactory.True)
    def getCaseParent(lst: List[CaseStatement]) : List[Opt[CaseStatement]] = {
      if (lst.size > 0) {
        List(env.parent(lst(0)).asInstanceOf[Opt[CaseStatement]]) ++ getCaseParent(lst.tail)
      } else {
        List()
      }
    }
    getCaseParent(filteredList)
  }

  def setGoToFlags(a: AST, env: ASTEnv) : AST = {
    var switchStatements = filterASTElems[SwitchStatement](a)
    var replaceMap: Map[Opt[_], List[Opt[_]]] = Map()
    printf("There are " + switchStatements.size + " switch statements.\n")

    // Indicates if a CaseStatement has a BreakStatement
    def hasBreakStmt(caseStmt: CaseStatement) : Boolean = {
      //val toCheck = caseStmt.s.get.toOptList(0).entry
      //println("\nNext from " + caseStmt + " is " + e.next(e.parent(caseStmt)) + "\n")
      val parent = env.parent(caseStmt).asInstanceOf[Opt[CaseStatement]]
      val next = env.next(parent)
      /*
      if (next.isInstanceOf[Opt[CaseStatement]]) {
        val nextCase = next.asInstanceOf[Opt[CaseStatement]].entry
        if (nextCase.c == caseStmt.c) {
          return hasBreakStmt(nextCase)
        }
      }
      */
      return next.isInstanceOf[Opt[BreakStatement]] && next.asInstanceOf[Opt[BreakStatement]].feature == parent.feature
    }

    // Indicates if a SwitchStatement has a default statement
    def hasDefaultStmt(swstmt: SwitchStatement) : Boolean = {
      val defaultstatement = filterASTElems[DefaultStatement](swstmt)
      val result = (defaultstatement.size == 1)
      // println("Contains DefaultStatement: " + result + "\n")
      return result
    }

    def getDefaultStmt(swStmt: SwitchStatement) : DefaultStatement = {
      val defaultStatements = filterASTElems[DefaultStatement](swStmt)
      if (defaultStatements.size == 1) {
        return defaultStatements(0)
      } else {
        return null
      }
    }
    // Indicates of a SwitchStatement includes variable opts
    def hasOpts(swstmt: SwitchStatement) : Boolean = {
      val opts = swstmt.s.toOptList(0).entry.asInstanceOf[CompoundStatement].innerStatements
      opts.foreach(x => if (x.feature != FeatureExprFactory.True) return true)
      return false
    }

    def getOpts(swstmt: SwitchStatement) : List[Opt[_]] = {
      val opts = swstmt.s.toOptList(0).entry.asInstanceOf[CompoundStatement].innerStatements
      val optBuffer = new ListBuffer[Opt[_]]
      opts.foreach(x => if (x.feature != FeatureExprFactory.True) optBuffer += x)
      println("Opts are:\n" + optBuffer)
      return optBuffer.toList
    }

    def setDefaultGoTo(swStmt: Opt[SwitchStatement]) = {
      val optLabel = Opt(FeatureExprFactory.True,LabelStatement(Id("sdefault"),None))
      if (hasDefaultStmt(swStmt.entry)) {
        //println("SetDefaultGoTo1\n")
        val dfltStmt = getDefaultStmt(swStmt.entry)
        replaceMap += (env.parent(dfltStmt).asInstanceOf[Opt[DefaultStatement]] -> List(optLabel, env.parent(dfltStmt).asInstanceOf[Opt[DefaultStatement]]))
      }
    }

    def checkDefaultGoTo(swStmt: Opt[SwitchStatement]) : Tuple2[Opt[_], List[Opt[_]]] = {
      val optLabel = Opt(FeatureExprFactory.True,LabelStatement(Id("sdefault"),None))
      if (hasDefaultStmt(swStmt.entry)) {
        //println("SetDefaultGoTo1\n")
        val dfltStmt = getDefaultStmt(swStmt.entry)
        return (env.parent(dfltStmt).asInstanceOf[Opt[DefaultStatement]], List(optLabel, env.parent(dfltStmt).asInstanceOf[Opt[DefaultStatement]]))
      } else {
        return null
      }
    }
    def transformOptToIf(optLst: List[Opt[_]]) : Map[Opt[_], List[Opt[_]]] = {

      def replaceSingleOpt(opt: Opt[_]) : List[Opt[_]] = {
        def replaceOptFeatures(caseStmt: CaseStatement, ft: FeatureExpr) : CaseStatement = {
          val core = caseStmt.s
          val r = manytd(rule {
            case l: List[Opt[_]] => l.flatMap({x => if (x.feature == ft)  Opt(FeatureExprFactory.True, x.entry)::Nil else x::Nil})
          })
          r(caseStmt).get.asInstanceOf[CaseStatement]
        }
        val condition = featureToCExpr(opt.feature)
        val entry = opt.entry
        //println("Entry:\n" + entry)

        if (entry.isInstanceOf[BreakStatement]) {
          val newS = List(Opt(FeatureExprFactory.True, IfStatement(One(condition), One(CompoundStatement(List(Opt(FeatureExprFactory.True, entry.asInstanceOf[BreakStatement])))), List(), None)))
          newS
        } else if (entry.isInstanceOf[CaseStatement]) {
          val sameCaseStatements = replaceMap.filterKeys(x => x.entry.isInstanceOf[CaseStatement] && x.entry.asInstanceOf[CaseStatement].c == entry.asInstanceOf[CaseStatement].c)
          if (sameCaseStatements.size > 0) {
            // val currentKey = sameCaseStatements.head._1
            //val targetCase = sameCaseStatements.head._2
            // replaceMap -= currentKey
            val caseStmt = entry.asInstanceOf[CaseStatement]
            val alteredCaseStmt = replaceOptFeatures(caseStmt, opt.feature)
            val then = alteredCaseStmt.s.get.toOptList(0).entry
            val ifStmt = IfStatement(One(condition), One(CompoundStatement(List(Opt(FeatureExprFactory.True, then)))), List(), None)

            List(Opt(FeatureExprFactory.True, ifStmt))


          } else {
            val caseStmt = entry.asInstanceOf[CaseStatement]
            //println("\nHas BreakStatement: " + hasBreakStmt(caseStmt) + "\n")
            val alteredCaseStmt = replaceOptFeatures(caseStmt, opt.feature)
            val then = alteredCaseStmt.s.get.toOptList(0).entry
            val ifStmt = IfStatement(One(condition), One(CompoundStatement(List(Opt(FeatureExprFactory.True, then)))), List(), None)
            val newOpt = Opt(FeatureExprFactory.True, CaseStatement(alteredCaseStmt.c, Some(One(ifStmt))))

            List(newOpt)
          }
          //println("New Opt is:\n" + PrettyPrinter.print(CompoundStatement(List(newOpt))))
        } else {
          List(opt)
        }
      }
      optLst.foreach(x => replaceMap += (x -> replaceSingleOpt(x)))
      replaceMap
      //println(PrettyPrinter.print(replaceWithMap(a, replaceMap)))
    }


    //val swtParent = env.parent(switchStatements(0)).asInstanceOf[Opt[SwitchStatement]]
    //val newSwtStmt = setDefaultGoTo(swtParent)

    val opts = getOpts(switchStatements(0))
    transformOptToIf(opts)

    if (hasDefaultStmt(switchStatements(0))) {
      val optSwitch = env.parent(switchStatements(0)).asInstanceOf[Opt[SwitchStatement]]
      setDefaultGoTo(optSwitch)
      println("Want to replace:\n" + checkDefaultGoTo(optSwitch)._1 + "\nwith:\n" + checkDefaultGoTo(optSwitch)._2 + "\n")
    }
    val newAst = replaceWithMap(a, replaceMap)
    newAst
  }

  def replaceIfs(ast: AST, env: ASTEnv) : AST = {
    val exprStmts = filterASTElems[ExprStatement](ast)
    val varExprStmts = exprStmts.filter(x => env.parent(x).asInstanceOf[Opt[ExprStatement]].feature != FeatureExprFactory.True)
    def exprToIf(a: AST, e: ASTEnv, lst: List[ExprStatement]) : AST = {
      if (lst.size > 0) {
        val oldS = e.parent(lst(0)).asInstanceOf[Opt[_]]
        val newS = Opt(FeatureExprFactory.True, IfStatement(One(featureToCExpr(oldS.feature)), One(CompoundStatement(List(Opt(FeatureExprFactory.True, lst(0))))), List(), None))
        println("old: " + oldS + "\n")
        println("new: " + newS + "\n" + PrettyPrinter.print(CompoundStatement(List(newS))))
        //println("Target is:\n" + PrettyPrinter.print(CompoundStatement(List(tar))) + "\n")
        return exprToIf(replaceSame(a, oldS, newS), e, lst.tail)
        //return replaceSame(a, oldS, newS)
      } else {
        return a;
      }
    }
    return exprToIf(ast, env, varExprStmts)
  }

  def idToOptExpr(id: Id, env: ASTEnv) : Opt[ExprStatement] = {
    val optFC = env.parent(env.parent(env.parent(id)))
    if (optFC.isInstanceOf[Opt[ExprStatement]]) {
      return optFC.asInstanceOf[Opt[ExprStatement]]
    } else {
      return null
    }
  }

  def idToFunctionCall(id: Id, env: ASTEnv) : FunctionCall = {
    val pstFxExpr = env.parent(id)
    if (pstFxExpr.isInstanceOf[PostfixExpr]) {
      return pstFxExpr.asInstanceOf[PostfixExpr].s.asInstanceOf[FunctionCall]
    } else {
      return null
    }
  }

  def transformVariableDeclarations(ast: AST, env: ASTEnv) : AST = {
    var replaceMap: Map[Opt[_], List[Opt[_]]] = Map()

    def isVariableDeclaration(decl: Declaration) : Boolean = {
      decl match {
        case Declaration(specs, List(Opt(feature, i: InitDeclaratorI))) =>
          if (feature != FeatureExprFactory.True) {
            return true
          }
          i match {
            case InitDeclaratorI(_, _, Some(Initializer(_, lcurly: LcurlyInitializer))) =>
              specs.foreach(x => if (x.feature != FeatureExprFactory.True) return true)
              lcurly.inits.foreach(x => if (x.feature != FeatureExprFactory.True) return true)
            case _ =>
          }
        case _ =>
      }
      return false
    }
    def convertSingleDeclaration(decl: Declaration /*, ft: FeatureExpr */) /*: Opt[Declaration] */ = {
      val secondParent = env.parent(env.parent(decl))
      val firstParent = env.parent(decl)

      if (secondParent.isInstanceOf[Opt[DeclarationStatement]]) {
        secondParent match {
          case Opt(feature, declStmt: DeclarationStatement) =>
            if (feature != FeatureExprFactory.True) {
              if (!IdMap.contains(feature)) {
                IdMap += (feature -> IdMap.size)
              }
              declStmt match {
                case DeclarationStatement(Declaration(specs, init: List[Opt[InitDeclaratorI]])) =>
                  //init match {
                  // case InitDeclaratorI(a: AtomicNamedDeclarator, c, d) =>
                  val namePrefix = "_" + IdMap.get(feature).get + "_"
                  val newInit = init.map(s => Opt(FeatureExprFactory.True, s.entry.copy(declarator = s.entry.declarator.asInstanceOf[AtomicNamedDeclarator].copy(id = Id(namePrefix + s.entry.declarator.asInstanceOf[AtomicNamedDeclarator].getName)))))
                  val newSpec = specs.map(s => s.copy(feature = FeatureExprFactory.True))
                  val newDeclStmt = declStmt.copy(decl = Declaration(newSpec, newInit))
                  replaceMap += (secondParent.asInstanceOf[Opt[DeclarationStatement]] -> List(Opt(FeatureExprFactory.True, newDeclStmt)))
                //}
                case _ =>
              }
            }
          case _ =>
        }
      }
      if (firstParent.isInstanceOf[Opt[Declaration]]) {
        firstParent match {
          case opt@Opt(ft, decl@Declaration(specs, init)) =>
            if (ft == FeatureExprFactory.True) {
              val newInit = init.map(i => i match {
                case Opt(ftt, InitDeclaratorI(_, _, Some(Initializer(_, lcurly: LcurlyInitializer)))) =>
                  if (ftt == FeatureExprFactory.True) {
                    val newCurly = lcurly.copy(inits = lcurly.inits.map(s => if (s.feature != FeatureExprFactory.True) {
                      if (!IdMap.contains(s.feature)) {
                        IdMap += (s.feature -> IdMap.size)
                      }
                      s.entry match {
                        case ini@Initializer(_, i: Id) =>
                          s.copy(feature = FeatureExprFactory.True).copy(entry = ini.copy(expr = Id("_" + IdMap.get(s.feature).get + "_" + i.name)))
                        case _ =>
                      }
                    } else {
                      s
                    }).asInstanceOf[List[Opt[Initializer]]])
                    i.copy(entry = i.entry.asInstanceOf[InitDeclaratorI].copy(i = Some(i.entry.asInstanceOf[InitDeclaratorI].i.get.copy(expr = newCurly))))
                  }
                case _ =>
              })
              replaceMap += (firstParent.asInstanceOf[Opt[_]] -> List(opt.copy(entry = decl.copy(init = newInit.asInstanceOf[List[Opt[InitDeclaratorI]]]))))
            } else {
              if (!IdMap.contains(ft)) {
                IdMap += (ft -> IdMap.size)
              }
              val newSpec = specs.map(s => if (s.feature == ft) {
                s.copy(feature = FeatureExprFactory.True)
              } else {
                s
              })
              val newInit = init.map(s => s match {
                case Opt(feat, i: InitDeclaratorI) =>
                  if (feat == ft) {
                    i match {
                      case InitDeclaratorI(and: AtomicNamedDeclarator, _, Some(Initializer(_, lcurly: LcurlyInitializer))) =>
                        val newAnd = and.copy(id = Id("_" + IdMap.get(ft).get + "_" + and.id.name)).copy(extensions = and.extensions.map(g =>
                        if (g.feature == ft) {
                          g.copy(feature = FeatureExprFactory.True)
                        } else {
                          g
                        }))
                        val newCurly = lcurly.inits.map(u => u match {
                          case o@Opt(feature, Initializer(_, id: Id)) =>
                            if (feature == ft) {
                              o.copy(feature = FeatureExprFactory.True)
                            } else {
                              o
                            }
                          case _ =>
                        })
                        s.copy(feature = FeatureExprFactory.True).copy(entry = i.copy(declarator = newAnd).copy(i = Some(i.i.get.copy(expr = LcurlyInitializer(newCurly.asInstanceOf[List[Opt[Initializer]]])))))
                      case _ =>
                    }
                  } else {
                    s
                  }
                case _ =>
              })
              val newOpt = Opt(FeatureExprFactory.True, Declaration(newSpec, newInit.asInstanceOf[List[Opt[InitDeclarator]]]))
              replaceMap += (firstParent.asInstanceOf[Opt[_]] -> List(newOpt))
            }
          case _ =>
          /*
          init match {
            case List(Opt(ft, i: InitDeclaratorI)) =>
             i match {
              case InitDeclaratorI(_, _, Some(Initializer(_, lcurly: LcurlyInitializer))) =>
                lcurly.inits.foreach(y => y match {
                  case Opt(ft, in: Initializer) => lcurlyinits += Opt(FeatureExprFactory.True, in)
                  case Opt(FeatureExprFactory.True, in: Initializer) => specifiers += y
                  case Opt(feature, in: Initializer) => if (featureMap.contains(feature)) {
                    featureMap += (feature -> (featureMap.get(feature).get ++ List(Opt(FeatureExprFactory.True, in))))
                  }
                })
            }
          } */
        }
      }
    }
    val declarations = filterASTElems[Declaration](ast)
    declarations.foreach(x => if (isVariableDeclaration(x)) convertSingleDeclaration(x))
    //println("\nReplaceMap is:\n" + replaceMap)
    replaceWithMap(ast, replaceMap)
  }
}