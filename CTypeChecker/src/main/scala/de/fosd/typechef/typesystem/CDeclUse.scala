package de.fosd.typechef.typesystem

import java.util.IdentityHashMap
import de.fosd.typechef.parser.c._
import scala.collection.JavaConversions._
import java.util
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import scala.Predef._
import de.fosd.typechef.parser.c.PlainParameterDeclaration
import de.fosd.typechef.parser.c.Enumerator
import de.fosd.typechef.parser.c.EnumSpecifier
import scala.Some
import de.fosd.typechef.parser.c.NAryExpr
import de.fosd.typechef.parser.c.TypeDefTypeSpecifier
import de.fosd.typechef.parser.c.Initializer
import de.fosd.typechef.parser.c.DoStatement
import de.fosd.typechef.parser.c.PointerPostfixSuffix
import de.fosd.typechef.parser.c.AssignExpr
import de.fosd.typechef.conditional.One
import de.fosd.typechef.parser.c.BuiltinOffsetof
import de.fosd.typechef.parser.c.DeclParameterDeclList
import de.fosd.typechef.parser.c.Pointer
import de.fosd.typechef.parser.c.SimplePostfixSuffix
import de.fosd.typechef.parser.c.SizeOfExprT
import de.fosd.typechef.parser.c.LcurlyInitializer
import de.fosd.typechef.parser.c.Id
import de.fosd.typechef.parser.c.Constant
import de.fosd.typechef.parser.c.DeclarationStatement
import de.fosd.typechef.parser.c.PointerDerefExpr
import de.fosd.typechef.parser.c.ExprList
import de.fosd.typechef.parser.c.CompoundStatement
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.parser.c.InitDeclaratorE
import de.fosd.typechef.parser.c.ParameterDeclarationAD
import de.fosd.typechef.parser.c.StructDeclarator
import de.fosd.typechef.parser.c.TypedefSpecifier
import de.fosd.typechef.parser.c.PostfixExpr
import de.fosd.typechef.parser.c.ArrayAccess
import de.fosd.typechef.parser.c.ReturnStatement
import de.fosd.typechef.parser.c.AtomicNamedDeclarator
import de.fosd.typechef.parser.c.GnuAsmExpr
import de.fosd.typechef.parser.c.CompoundStatementExpr
import de.fosd.typechef.parser.c.StructOrUnionSpecifier
import de.fosd.typechef.parser.c.PointerCreationExpr
import de.fosd.typechef.conditional.Choice
import de.fosd.typechef.parser.c.ConditionalExpr
import de.fosd.typechef.parser.c.FunctionCall
import de.fosd.typechef.parser.c.DeclArrayAccess
import de.fosd.typechef.parser.c.IfStatement
import de.fosd.typechef.parser.c.NArySubExpr
import de.fosd.typechef.parser.c.WhileStatement
import de.fosd.typechef.parser.c.InitDeclaratorI
import de.fosd.typechef.parser.c.UnaryOpExpr
import de.fosd.typechef.parser.c.Declaration
import de.fosd.typechef.parser.c.InitializerAssigment
import de.fosd.typechef.parser.c.LabelStatement
import de.fosd.typechef.parser.c.ExprStatement
import de.fosd.typechef.parser.c.DeclIdentifierList
import de.fosd.typechef.parser.c.InitializerDesignatorD
import de.fosd.typechef.parser.c.StructDeclaration
import de.fosd.typechef.parser.c.GotoStatement
import de.fosd.typechef.parser.c.FunctionDef
import de.fosd.typechef.parser.c.SizeOfExprU
import de.fosd.typechef.parser.c.OffsetofMemberDesignatorID
import de.fosd.typechef.parser.c.ParameterDeclarationD
import de.fosd.typechef.parser.c.TypeName
import de.fosd.typechef.parser.c.CastExpr
import de.fosd.typechef.parser.c.NestedNamedDeclarator
import scala.Tuple2
import de.fosd.typechef.parser.c.StringLit
import de.fosd.typechef.parser.c.UnaryExpr


// store def use chains
// we store Id elements of AST structures that represent a definition (key element of defuse)
// and a use (value element of defuse)
//
// the creation of defuse chains relies on the typesystem and it's data that is stored
// in Env instances; during the traversal of the typesystem visitor Env instances get filled
// with information about names, AST entries and their corresponding types
trait CDeclUse extends CEnv with CEnvCache {

  private val declUseMap: IdentityHashMap[Id, IdentityHashMap[Id, Id]] = new IdentityHashMap()

  private val useDeclMap: IdentityHashMap[Id, List[Id]] = new IdentityHashMap()

  var stringToIdMap: Map[String, Id] = Map()

  private[typesystem] def clear() {
    clearDeclUseMap()
  }

  private def putToDeclUseMap(decl: Id) = {
    if (!declUseMap.contains(decl)) {
      declUseMap.put(decl, new IdentityHashMap())
    }
  }

  private def addToDeclUseMap(decl: Id, use: Id): Any = {
    if (decl.eq(use) && !declUseMap.containsKey(decl)) {
      putToDeclUseMap(decl)
    }
    if (declUseMap.containsKey(decl) && !declUseMap.get(decl).containsKey(use)) {
      declUseMap.get(decl).put(use, null)
      addToUseDeclMap(use, decl)
    }
  }

  private def addToUseDeclMap(use: Id, decl: Id) = {
    if (useDeclMap.contains(use)) {
      useDeclMap.put(use, decl :: useDeclMap.get(use))
    } else {
      useDeclMap.put(use, List(decl))
    }
  }

  def clearDeclUseMap() {
    declUseMap.clear()
    useDeclMap.clear()
  }

  def getDeclUseMap(): IdentityHashMap[Id, List[Id]] = {
    // TODO Optimize Datastructur & Performance
    val defuseMap = new util.IdentityHashMap[Id, List[Id]]()
    declUseMap.keySet().foreach(x => {
      val list = declUseMap.get(x).keySet().toArray(Array[Id]()).toList
      defuseMap.put(x, list)
    })
    defuseMap
  }

  def getUseDeclMap = useDeclMap

  def getDeclUseMap2 = declUseMap

  // add definition:
  //   - function: function declarations (forward declarations) and function definitions are handled
  //               if a function declaration exists, we add it as def and the function definition as its use
  //               if no function declaration exists, we add the function definition as def
  def addDefinition(definition: AST, env: Env, feature: FeatureExpr = FeatureExprFactory.True, isFunctionDeclarator: Boolean = false) {
    definition match {
      case id: Id =>
        if (isFunctionDeclarator) {
          addFunctionDeclaration(env, id, feature)
        } else {
          putToDeclUseMap(id)
        }
      case StructDeclaration(quals, decls) => decls.foreach(x => addDecl(x.entry, x.feature, env))
      case _ =>
    }
  }

  private def addFunctionDeclaration(env: Env, id: Id, feature: FeatureExpr) {
    def addForwardDeclartion(i: Id, _currentForwardDeclaration: Id, x: (FeatureExpr, AST)): Any = {
      var currentForwardDeclaration: Id = _currentForwardDeclaration
      if (declUseMap.containsKey(i)) {
        currentForwardDeclaration = i
        val temp = declUseMap.get(i)
        if (feature.equivalentTo(FeatureExprFactory.True) || (feature.implies(x._1).isTautology())) {
          declUseMap.remove(i)
          putToDeclUseMap(id)
          addToDeclUseMap(id, i)
          temp.keySet().toArray().foreach(x => addToDeclUseMap(id, x.asInstanceOf[Id]))
        } else {
          putToDeclUseMap(id)
        }
      } else {
        putToDeclUseMap(id)
        addToDeclUseMap(id, i)
      }
    }

    env.varEnv.getAstOrElse(id.name, null) match {
      case One(null) => putToDeclUseMap(id)
      case One(i: InitDeclarator) =>
        val temp = declUseMap.get(i.getId)
        declUseMap.remove(i.getId)
        putToDeclUseMap(id)
        addToDeclUseMap(id, i.getId)
        temp.keySet().toArray().foreach(x => addToDeclUseMap(id, x.asInstanceOf[Id]))
      case c@Choice(_, _, _) =>
        val tuple = choiceToTuple(c)
        var currentForwardDeclaration: Id = null
        tuple.foreach(x => {
          x._2 match {
            case i: InitDeclarator => addForwardDeclartion(i.getId, currentForwardDeclaration, x)
            case f: FunctionDef => addForwardDeclartion(f.declarator.getId, currentForwardDeclaration, x)
            case k =>
          }
        })
      case _ => assert(false, println("Match Error"))
    }
  }

  def addEnumUse(entry: AST, env: Env, feature: FeatureExpr) {
    entry match {
      case i@Id(name) =>
        if (env.enumEnv.containsKey(name)) {
          val enumDeclarationFeature = env.enumEnv.get(name).get._1
          val enumDeclarationId = env.enumEnv.get(name).get._2
          if (feature.equivalentTo(FeatureExprFactory.True) || (feature.implies(enumDeclarationFeature).isTautology())) {
            addToDeclUseMap(enumDeclarationId, i)
          }
        }
      case _ => assert(false, println("Match Error"))
    }
  }

  def addTypeUse(entry: AST, env: Env, feature: FeatureExpr) {
    def addOne(one: One[AST], use: Id, env: Env) {
      one match {
        case One(InitDeclaratorI(declarator, _, _)) =>
          addToDeclUseMap(declarator.getId, use)
        case One(AtomicNamedDeclarator(_, key, _)) =>
          addToDeclUseMap(key, use)
        case One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)) =>
          addToDeclUseMap(key, use)
        case One(Enumerator(key, _)) =>
          addToDeclUseMap(key, use)
        case One(Declaration(specifiers, inits)) =>
          inits.foreach(x => x match {
            case Opt(_, entry) => addOne(One(entry), use, env)
            case _ =>
          })
        case _ =>
      }
    }

    entry match {
      case i@Id(name) =>
        if (env.typedefEnv.contains(name)) {
          env.typedefEnv.getAstOrElse(name, null) match {
            case o@One(_) =>
              addOne(o, i, env)
            case c@Choice(_, _, _) =>
              addChoice(c, feature, i, env, addOne)
            case _ =>
          }
        }
    }
  }

  private def addChoice(choice: Choice[AST], featureExpr: FeatureExpr, use: Id, env: Env, oneFunc: (One[AST], Id, Env) => Unit) {
    choice match {
      case Choice(feature1, o1@One(_), o2@One(_)) =>
        if (featureExpr.equivalentTo(FeatureExprFactory.True)) {
          oneFunc(o1, use, env)
          oneFunc(o2, use, env)
        } else if (featureExpr.implies(feature1).isTautology()) {
          oneFunc(o1, use, env)
        } else if (featureExpr.implies(feature1.not).isTautology()) {
          oneFunc(o2, use, env)
        } else {
          oneFunc(o1, use, env)
          oneFunc(o2, use, env)
        }
      case Choice(feature1, o@One(_), c@Choice(feature2, _, _)) =>
        oneFunc(o, use, env)
        if (!featureExpr.equivalentTo(feature1)) {
          addChoice(c, featureExpr, use, env, oneFunc)
        }
      case Choice(_, c1@Choice(_, _, _), c2@Choice(_, _, _)) =>
        addChoice(c1, featureExpr, use, env, oneFunc)
        addChoice(c2, featureExpr, use, env, oneFunc)
      case Choice(_, c@Choice(_, _, _), o@One(_)) =>
        oneFunc(o, use, env)
        addChoice(c, featureExpr, use, env, oneFunc)
      case _ => assert(false, println("Match Error"))
    }
  }

  private def addDefChoice(entry: Choice[AST]) {
    def addOne(entry: One[AST]) {
      entry match {
        case One(InitDeclaratorI(declarator, _, _)) =>
          putToDeclUseMap(declarator.getId)
        case One(AtomicNamedDeclarator(_, key, _)) =>
          putToDeclUseMap(key)
        case One(Enumerator(key, _)) =>
          putToDeclUseMap(key)
        case One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)) =>
          putToDeclUseMap(key)
        case One(null) =>
        case k => println("DefChoice: Missed add One " + k)
      }
    }

    entry match {
      case Choice(feature, c1@Choice(_, _, _), c2@Choice(_, _, _)) =>
        addDefChoice(c1)
        addDefChoice(c2)

      case Choice(feature, o1@One(_), o2@One(_)) =>
        addOne(o1)
        addOne(o2)

      case Choice(feature, o@One(_), c@Choice(_, _, _)) =>
        addDefChoice(c)
        addOne(o)

      case Choice(feature, c@Choice(_, _, _), o@One(_)) =>
        addDefChoice(c)
        addOne(o)

      case k =>
        println("Missed Def Choice " + k)
    }
  }

  def addUse(entry: AST, feature: FeatureExpr, env: Env) {

    def addUseCastExpr(typ: TypeName, addUse: (AST, FeatureExpr, CDeclUse.this.type#Env) => Unit, feature: FeatureExpr, env: CDeclUse.this.type#Env, lst: List[Opt[Initializer]]) {
      var typedefspecifier: Id = null
      typ match {
        case TypeName(lst, _) =>
          lst.foreach(x => x.entry match {
            case StructOrUnionSpecifier(_, _, Some(innerLst)) =>
              innerLst.foreach(y => y.entry match {
                case StructDeclaration(inits, decls) =>
                  inits.foreach(j => addUse(j.entry, feature, env))
                  decls.foreach(z => z.entry match {
                    case StructDeclarator(a: AtomicNamedDeclarator, _, _) =>
                      if (!declUseMap.containsKey(a.getId)) {
                        putToDeclUseMap(a.getId)
                      }
                      stringToIdMap += (a.getName -> a.getId)
                    case k => addUse(k, feature, env)
                  })
              })
            case TypeDefTypeSpecifier(i@Id(name)) =>
              typedefspecifier = i
              addTypeUse(i, env, x.feature)
            case k => addUse(k, feature, env)
          })
      }
      lst.foreach(x => x.entry match {
        case Initializer(Some(InitializerAssigment(lst2)), expr) =>
          addUse(expr, feature, env)
          lst2.foreach(y => y.entry match {
            case idd@InitializerDesignatorD(i) =>
              env.varEnv.getAstOrElse(i.name, null) match {
                case One(InitDeclaratorI(declarator, _, _)) =>
                  if (!declUseMap.contains(declarator.getId)) {
                    putToDeclUseMap(declarator.getId)
                  }
                  addToDeclUseMap(declarator.getId, i)

                case One(AtomicNamedDeclarator(_, key, _)) => addToDeclUseMap(key, i)
                case One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)) =>
                  if (!declUseMap.contains(key)) {
                    putToDeclUseMap(key)
                  }
                  addToDeclUseMap(key, i)
                case One(Enumerator(key, _)) => addToDeclUseMap(key, i)
                case One(NestedNamedDeclarator(_, nestedDecl, _)) => addToDeclUseMap(nestedDecl.getId, i)
                case c@Choice(_, _, _) => addChoice(c, feature, i, env, addUseOne)
                case One(null) =>
                  if (stringToIdMap.containsKey(i.name) && declUseMap.containsKey(stringToIdMap.get(i.name).get)) {
                    addToDeclUseMap(stringToIdMap.get(i.name).get, i)
                  } else if (!(typedefspecifier == null)) {
                    env.typedefEnv.getAstOrElse(typedefspecifier.name, null) match {
                      case One(Declaration(specs, decl)) =>
                        for (Opt(_, StructOrUnionSpecifier(_, None, Some(lst))) <- specs) {
                          for (Opt(_, StructDeclaration(qualis, structDecls)) <- lst) {
                            for (Opt(innerFeature, StructDeclarator(a: AtomicNamedDeclarator, _, _)) <- structDecls) {
                              val declaration = a.getId
                              if (declaration.name.equals(i.name) && feature.implies(innerFeature).isTautology()) {
                                addToDeclUseMap(declaration, i)
                              }
                            }
                          }
                        }
                      case _ =>
                    }
                  }
                case k => // println("AddUse Id not exhaustive: " + i + "\nElement " + k)
              }
          })
        case k =>
          addUse(k, feature, env)
      })
      stringToIdMap = stringToIdMap.empty
    }

    def addUseOne(one: One[AST], use: Id, env: Env) {
      one match {
        case One(InitDeclaratorI(declarator, _, _)) => addToDeclUseMap(declarator.getId, use)
        case One(AtomicNamedDeclarator(_, key, _)) => addToDeclUseMap(key, use)
        case One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)) => addToDeclUseMap(key, use)
        case One(Enumerator(key, _)) => addToDeclUseMap(key, use)
        case One(NestedNamedDeclarator(_, declarator, _)) => addToDeclUseMap(declarator.getId, use)
        case One(NestedFunctionDef(_, _, AtomicNamedDeclarator(_, key, _), _, _)) => addToDeclUseMap(key, use) // TODO Verfiy Nested forward decl?
        case One(null) =>
        case _ => assert(false, println("Match Error" + one))
      }
    }

    entry match {
      case ConditionalExpr(expr, thenExpr, elseExpr) =>
        addUse(expr, feature, env)
        thenExpr.foreach(x => addUse(x, feature, env))
        addUse(elseExpr, feature, env)
      case EnumSpecifier(x, _) =>
      case FunctionCall(param) => param.exprs.foreach(x => addUse(x.entry, feature, env))
      case ExprList(exprs) => exprs.foreach(x => addUse(x.entry, feature, env))
      case LcurlyInitializer(inits) => inits.foreach(x => addUse(x.entry, feature, env))
      case InitializerAssigment(designators) => designators.foreach(x => addUse(x.entry, feature, env))
      case InitializerDesignatorD(i: Id) =>
        addUse(i, feature, env)
      case Initializer(Some(x), expr) =>
        addUse(x, feature, env)
        addUse(expr, feature, env)
      case i@Id(name) =>
        env.varEnv.getAstOrElse(name, null) match {
          case o@One(_) => addUseOne(o, i, env)
          case c@Choice(_, _, _) => addChoice(c, feature, i, env, addUseOne)
          case _ =>
        }
      case PointerDerefExpr(i) => addUse(i, feature, env)
      case AssignExpr(target, operation, source) =>
        addUse(source, feature, env)
        addUse(target, feature, env)
      case NAryExpr(i, o) =>
        addUse(i, feature, env)
        o.foreach(x => addUse(x.entry, feature, env))
      case NArySubExpr(_, e) => addUse(e, feature, env)
      case PostfixExpr(p, s) =>
        addUse(p, feature, env)
        addUse(s, feature, env)
      case PointerPostfixSuffix(_, id: Id) => if (!env.varEnv.getAstOrElse(id.name, null).equals(One(null))) addUse(id, feature, env)
      case PointerCreationExpr(expr) => addUse(expr, feature, env)
      case CompoundStatement(innerStatements) => innerStatements.foreach(x => addUse(x.entry, feature, env))
      case Constant(_) =>
      case SizeOfExprT(expr) => addUse(expr, feature, env)
      case SizeOfExprU(expr) => addUse(expr, feature, env)
      case TypeName(specs, decl) =>
        specs.foreach(x => addUse(x.entry, feature, env))
        addDecl(decl, feature, env)
      case StringLit(_) =>
      case SimplePostfixSuffix(_) =>
      case GnuAsmExpr(isVolatile, isGoto, expr, Some(stuff)) =>
      case CastExpr(typ, LcurlyInitializer(lst)) => addUseCastExpr(typ, addUse _, feature, env, lst)
      case CastExpr(typ, expr) =>
        addUse(typ, feature, env)
        addUse(expr, feature, env)
      case ArrayAccess(expr) => addUse(expr, feature, env)
      case UnaryExpr(_, expr) => addUse(expr, feature, env)
      case UnaryOpExpr(_, expr) => addUse(expr, feature, env)
      case TypeDefTypeSpecifier(id) => addTypeUse(id, env, feature)
      case Initializer(_, expr) => addUse(expr, feature, env)
      case CompoundStatementExpr(expr) => addUse(expr, feature, env)
      case StructOrUnionSpecifier(union, Some(i: Id), _) => addStructDeclUse(i, env, union, feature)
      case BuiltinOffsetof(typeName, members) =>
        typeName.specifiers.foreach(x => addUse(x.entry, feature, env))
        /**
         * Workaround for buitlin_offset_ -> typechef implementation too much - see: http://gcc.gnu.org/onlinedocs/gcc/Offsetof.html
         */
        val structOrUnion = filterASTElemts[Id](typeName)
        members.foreach(x => addStructUse(x.entry, feature, env, structOrUnion.head.name, !env.structEnv.someDefinition(structOrUnion.head.name, false)))
      case _ =>
    }
  }

  private def choiceToTuple(choice: Choice[_]): List[Tuple2[FeatureExpr, AST]] = {
    def addOne(entry: One[_], ft: FeatureExpr): List[Tuple2[FeatureExpr, AST]] = {
      entry match {
        case One(null) =>
          List()
        case One(a: AST) =>
          List(Tuple2(ft, a))
      }
    }
    choice match {
      case Choice(ft, first@One(_), second@One(_)) =>
        addOne(first, ft) ++ addOne(second, ft.not())
      case Choice(ft, first@Choice(_, _, _), second@Choice(_, _, _)) =>
        choiceToTuple(first) ++ choiceToTuple(second)
      case Choice(ft, first@One(a), second@Choice(_, _, _)) =>
        addOne(first, ft) ++ choiceToTuple(second)
      case Choice(ft, first@Choice(_, _, _), second@One(_)) =>
        choiceToTuple(first) ++ addOne(second, ft.not())
    }
  }

  def addDeclaration(decl: Declaration, feature: FeatureExpr, env: Env) {
    def handleAtomicNamedDeclaratorExtensions(and: AtomicNamedDeclarator) = {
      and match {
        case AtomicNamedDeclarator(_, _, extensions) =>
          for (Opt(extensionFeature, extensionEntry) <- extensions) {
            extensionEntry match {
              case DeclIdentifierList(ids) =>
                for (Opt(idFeature, id: Id) <- ids) {
                  println("DeclIdentifierId: " + id)
                }
              case DeclParameterDeclList(paraDecls) =>
                for (Opt(paraDeclFeature, paraDeclEntry) <- paraDecls) {
                  paraDeclEntry match {
                    case ParameterDeclarationD(specs, paraDeclDecl) =>
                      for (Opt(specFeature, specsEntry) <- specs) {
                        specsEntry match {
                          case StructOrUnionSpecifier(isUnion, Some(i: Id), None) =>
                            env.structEnv.getId(i.name, isUnion) match {
                              case One(key: Id) =>
                                addToDeclUseMap(key, i)
                              case c@Choice(_, _, _) =>
                                val tuple = choiceToTuple(c)
                                tuple.foreach(x => {
                                  if (specFeature.equivalentTo(FeatureExprFactory.True)) {
                                    addToDeclUseMap(x._2.asInstanceOf[Id], i)
                                  } else if (specFeature.implies(x._1).isTautology) {
                                    addToDeclUseMap(x._2.asInstanceOf[Id], i)
                                  }
                                })
                              case _ => assert(false, println("Match Error"))
                            }
                          case _ => assert(false, println("Match Error"))
                        }
                      }
                      putToDeclUseMap(paraDeclDecl.getId)
                  }
                }
              case _ => assert(false, println("Match Error"))
            }
          }
      }
    }

    val isTypeDef = decl.declSpecs.exists(x => x.entry.equals(TypedefSpecifier()))
    val isStructOrUnion = decl.declSpecs.exists(x => x.entry.isInstanceOf[StructOrUnionSpecifier])
    val isIncompleteStruct = isStructOrUnion && (decl.init.isEmpty || (isTypeDef && decl.init.size == 1))
    val isTypeDefStruct = isStructOrUnion && isTypeDef
    for (Opt(initFeature, init) <- decl.init) {
      init match {
        case InitDeclaratorI(declarator: AtomicNamedDeclarator, attributes, i) =>
          putToDeclUseMap(declarator.getId)
          handleAtomicNamedDeclaratorExtensions(declarator)
        case InitDeclaratorE(declarator: AtomicNamedDeclarator, attributes, expr) =>
          putToDeclUseMap(declarator.getId)
          handleAtomicNamedDeclaratorExtensions(declarator)
        case _ => assert(false, println("Match Error"))
      }
    }
  }

  def addStructUse(entry: AST, featureExpr: FeatureExpr, env: Env, structName: String, isUnion: Boolean) {
    entry match {
      case i@Id(name) => {
        if (env.structEnv.someDefinition(structName, isUnion)) {

          env.structEnv.getFieldsMerged(structName, isUnion).getAstOrElse(i.name, null) match {
            case One(null) =>
              addStructDeclUse(i, env, isUnion, featureExpr)
            case One(AtomicNamedDeclarator(_, i2: Id, _)) =>
              addToDeclUseMap(i2, i)
            case One(i2: Id) =>
              addToDeclUseMap(i2, i)
            case c@Choice(_, _, _) =>
              addStructUseChoice(c, i)
            case One(NestedNamedDeclarator(_, AtomicNamedDeclarator(_, i2: Id, _), _)) =>
              addToDeclUseMap(i2, i)
            case k => println("Missed addStructUse " + env.varEnv.getAstOrElse(i.name, null))
          }
        } else {
          env.typedefEnv.getAstOrElse(i.name, null) match {
            case One(i2: Id) =>
              addToDeclUseMap(i2, i)
            case One(null) =>
              addDefinition(i, env)
            case c@Choice(_, _, _) =>
              println("missed choice typedef " + c)
            case One(Declaration(List(Opt(_, _), Opt(_, s@StructOrUnionSpecifier(_, Some(id), _))), _)) =>
              // TODO typedef name name
              putToDeclUseMap(i)
            case k =>
              println("Missed addStructUse" + k)
          }
        }
      }
      case OffsetofMemberDesignatorID(id) =>
        addStructUse(id, featureExpr, env, structName, isUnion)
      case k => println("Missed addStructUse")
    }
  }

  def addAnonStructUse(id: Id, fields: ConditionalTypeMap) {
    fields.getAstOrElse(id.name, null) match {
      case c@Choice(_, _, _) => addStructUseChoice(c, id)
      case One(AtomicNamedDeclarator(_, key, _)) =>
        // TODO: workaround für fehlende Definition in den nächsten 3 Zeilen entfernen
        if (!declUseMap.containsKey(key)) {
          putToDeclUseMap(key)
        }
        addToDeclUseMap(key, id)
      case One(NestedNamedDeclarator(_, declarator, _)) => addToDeclUseMap(declarator.getId, id)
      case k => println("Should not have entered here: " + id + "\n" + k)
    }
  }

  private def addStructUseChoice(choice: Choice[AST], use: Id) {
    def addOne(one: One[AST], use: Id) {
      one match {
        case One(AtomicNamedDeclarator(_, key, _)) =>
          // TODO: workaround für fehlende Definition in den nächsten 3 Zeilen entfernen
          if (!declUseMap.containsKey(key)) {
            putToDeclUseMap(key)
          }
          addToDeclUseMap(key, use)
        case One(NestedNamedDeclarator(_, declarator, _)) => addToDeclUseMap(declarator.getId, use)
        case One(i@Id(_)) => addToDeclUseMap(i, use) // TODO Missing case, but @defuse?
        case One(null) =>
        //addStructDeclUse(use, env, isUnion)
        case _ => println("AddAnonStructChoice missed " + one)
      }
    }

    choice match {
      case Choice(_, o1@One(_), o2@One(_)) =>
        addOne(o1, use)
        addOne(o2, use)
      case Choice(_, o@One(_), c@Choice(_, _, _)) =>
        addOne(o, use)
        addStructUseChoice(c, use)
      case Choice(_, c1@Choice(_, _, _), c2@Choice(_, _, _)) =>
        addStructUseChoice(c1, use)
        addStructUseChoice(c2, use)
      case Choice(_, c@Choice(_, _, _), o@One(_)) =>
        addOne(o, use)
        addStructUseChoice(c, use)
      case _ => println("AddAnonStructChoice: This should not have happend " + choice)
    }
  }

  def addStructDeclUse(entry: Id, env: Env, isUnion: Boolean, feature: FeatureExpr) {
    def addOne(one: One[AST], use: Id) = {
      one match {
        case One(id: Id) =>
          addToDeclUseMap(id, use)
        case One(null) =>
        case _ =>
      }
    }

    entry match {
      case use@Id(name) => {
        if (env.structEnv.someDefinition(name, isUnion)) {
          env.structEnv.getId(name, isUnion) match {
            case o@One(key: Id) =>
              addOne(o, use)
            case c@Choice(_, _, _) =>
              val tuple = choiceToTuple(c)
              tuple.foreach(x => {
                if (feature.equivalentTo(FeatureExprFactory.True) || feature.implies(x._1).isTautology) {
                  addToDeclUseMap(x._2.asInstanceOf[Id], use)
                }
              })
          }
        } else {
          addDefinition(use, env)
        }
      }
      case _ =>
    }
  }

  def addDecl(current: Any, featureExpr: FeatureExpr, env: Env, isDefinition: Boolean = true) {
    current match {
      case Nil =>
      case None =>
      case DeclarationStatement(_) =>
      case StructDeclaration(specifiers, structDecls) =>
        for (specs <- specifiers) {
          specs match {
            case Opt(typedefFeature, TypeDefTypeSpecifier(i: Id)) =>
              addTypeUse(i, env, typedefFeature)
            case Opt(structSpecFeature, StructOrUnionSpecifier(isUnion, idOption, enum)) =>
              idOption match {
                case None =>
                case Some(i: Id) =>
                  if (enum.equals(None)) {
                    addStructDeclUse(i, env, isUnion, structSpecFeature)
                  }
                case _ =>
              }
              enum match {
                case None =>
                case Some(enums) =>
                  enums.foreach(x => addDecl(x.entry, x.feature, env))
                case _ =>
              }
            case Opt(enumFeature, EnumSpecifier(Some(i: Id), enumerator)) =>
              addEnumUse(i, env, enumFeature)
              addDecl(enumerator, enumFeature, env)
            case _ =>
          }
        }
        structDecls.foreach(x => addDecl(x.entry, featureExpr, env))
      case Declaration(decl, init) =>
        val hasTypedefSpecifier = decl.exists(x => x.entry.isInstanceOf[TypedefSpecifier])
        val hasStructSpecifier = decl.exists(x => x.entry.isInstanceOf[StructOrUnionSpecifier])
        val isIncompleteStruct = if (hasTypedefSpecifier && hasStructSpecifier) (init.size == 1) else if (hasStructSpecifier) (init.isEmpty) else false
        if (isIncompleteStruct) {
          for (Opt(structFeature, structSpec: StructOrUnionSpecifier) <- decl) {
            // debug
            structSpec.enumerators match {
              case None =>
              case Some(structDeclarations) =>
                for (x <- structDeclarations) {
                  addDecl(x.entry, x.feature, env)
                }
            }
          }
          for (x <- init) {
            putToDeclUseMap(x.entry.getId)
          }
        } else {
          decl.foreach(x => addDecl(x.entry, featureExpr, env))
          init.foreach(x => addDecl(x.entry, featureExpr, env))
        }
      case Opt(_, e) => addDecl(e, featureExpr, env)
      case i@InitDeclaratorI(decl, attr, opt) =>
        addDecl(decl, featureExpr, env)
        attr.foreach(x => addDecl(x.entry, featureExpr, env))
        opt match {
          case None =>
          case Some(init@Initializer(_, l: LcurlyInitializer)) =>
            addLcurlyInitializer(i.getId, init, featureExpr, env)
          case _ => // addUse(opt.get, env)
        }
      case Initializer(label, element) =>
        addDecl(element, featureExpr, env)
      case AtomicNamedDeclarator(pointers, id, extension) =>
        if (isDefinition) {
          pointers.foreach(x => addDecl(x, featureExpr, env))
          extension.foreach(x => addDecl(x, featureExpr, env))
          addDefinition(id, env)
        } else {
          pointers.foreach(x => addDecl(x, featureExpr, env))
          extension.foreach(x => addDecl(x, featureExpr, env))
          addDefinition(id, env, featureExpr, true)
        }

      case i: Id =>
        if (isDefinition) {
          addDefinition(i, env)
        } else {
          addUse(i, featureExpr, env)
        }
      //addDef(i, featureExpr, env)

      case DeclParameterDeclList(decl) =>
        decl.foreach(x => addDecl(x.entry, featureExpr, env))
      case ParameterDeclarationD(specs, decl) =>
        for (Opt(typedefFeature, TypeDefTypeSpecifier(i: Id)) <- specs) {
          addTypeUse(i, env, typedefFeature)
        }
        for (Opt(structSpecFeature, StructOrUnionSpecifier(isUnion, Some(i: Id), _)) <- specs) {
          addStructDeclUse(i, env, isUnion, structSpecFeature)
        }
        addDecl(decl, featureExpr, env)
      case Pointer(specs) =>
        specs.foreach(x => addDecl(x, featureExpr, env))
      case EnumSpecifier(id, None) =>
        id match {
          case None =>
          case Some(i: Id) =>
            addDefinition(i, env)
        }
      case EnumSpecifier(id, Some(o)) =>
        id match {
          case None =>
          case Some(i: Id) =>
            addDefinition(i, env)
        }
        for (e <- o) {
          addDecl(e.entry, featureExpr, env)
        }
      case i@IfStatement(cond, then, elif, els) =>
      case EnumSpecifier(_, _) =>
      case PlainParameterDeclaration(spec) => spec.foreach(x => addDecl(x.entry, featureExpr, env))
      case ParameterDeclarationAD(specs, decl) =>
        for (Opt(typedefFeature, TypeDefTypeSpecifier(i: Id)) <- specs) {
          addTypeUse(i, env, typedefFeature)
        }
        for (Opt(structSpecFeature, StructOrUnionSpecifier(isUnion, Some(i: Id), _)) <- specs) {
          addStructDeclUse(i, env, isUnion, structSpecFeature)
        }
        addDecl(decl, featureExpr, env)
      case Enumerator(i@Id(name), Some(o)) =>
        addDefinition(i, env)
        o match {
          case i: Id =>
            addUse(i, featureExpr, env)
          case k => addDecl(k, featureExpr, env)
        }
      case Enumerator(i@Id(name), None) =>
        addDefinition(i, env)
      case BuiltinOffsetof(typeName, members) =>
        typeName.specifiers.foreach(x => addDecl(x.entry, featureExpr, env))
        members.foreach(x => addDecl(x.entry, featureExpr, env))
      case OffsetofMemberDesignatorID(i) =>
        addDecl(i, featureExpr, env)
      case TypeDefTypeSpecifier(name: Id) =>
        addTypeUse(name, env, featureExpr)
      case DeclArrayAccess(Some(o)) =>
        addDecl(o, featureExpr, env, false)
      case ReturnStatement(expr) =>
      case AssignExpr(target, operation, source) =>
        addUse(source, featureExpr, env)
        addUse(target, featureExpr, env)
      case UnaryOpExpr(_, expr) =>
        addDecl(expr, featureExpr, env)
      case DoStatement(expr, cond) =>
        addDecl(expr, featureExpr, env)
        addDecl(cond, featureExpr, env)
      case StructOrUnionSpecifier(isUnion, Some(i@Id(name)), None) =>
        //addDefinition(i, env)
        if (isDefinition) {

          //addStructUse(i, featureExpr, env, name, isUnion)
        } else {
          addStructDeclUse(i, env, isUnion, featureExpr)
        }

      case StructOrUnionSpecifier(isUnion, Some(i@Id(name)), Some(extensions)) =>
        if (!declUseMap.contains(i)) {
          putToDeclUseMap(i)
        }
        extensions.foreach(x => addDecl(x, featureExpr, env))
      case StructOrUnionSpecifier(_, None, Some(extensions)) =>
        extensions.foreach(x => addDecl(x, featureExpr, env))
      case StructDeclarator(decl, i: Id, _) =>
        // addDecl(decl, env)
        // addDef(i, featureExpr, env)
        addDefinition(i, env)
      case ExprStatement(expr) =>
      //addDecl(expr, env)
      case pe@PostfixExpr(expr, suffix) =>
        addUse(expr, featureExpr, env)
        addDecl(suffix, featureExpr, env)
      case pps@PointerPostfixSuffix(_, id: Id) =>
        addUse(id, featureExpr, env)
      case f@FunctionCall(expr) =>
      case StructDeclarator(decl, _, _) =>
        addDecl(decl, featureExpr, env)
      case StructOrUnionSpecifier(_, Some(o), None) =>
        addDecl(o, featureExpr, env)
      case NestedNamedDeclarator(pointers, nestedDecl, extension) =>
        pointers.foreach(x => addDecl(x, featureExpr, env))
        extension.foreach(x => addDecl(x, featureExpr, env))
        addDecl(nestedDecl, featureExpr, env)
      case One(o) => addDecl(o, featureExpr, env)
      case Some(o) => addDecl(o, featureExpr, env)
      case NAryExpr(expr, others) =>
        expr match {
          case Id(_) => addUse(expr, featureExpr, env)
          case k => addUse(k, featureExpr, env)
        }
        others.foreach(x => addDecl(x.entry, featureExpr, env))
      case NArySubExpr(_, expr) =>
        addDecl(expr, featureExpr, env)
      case CastExpr(typ, expr) =>
        addDecl(expr, featureExpr, env)
        typ.specifiers.foreach(x => addDecl(x, featureExpr, env))
      case SizeOfExprT(TypeName(spec, decl)) =>
        spec.foreach(x => addDecl(x.entry, featureExpr, env, false))
      case TypeOfSpecifierT(TypeName(spec, decl)) =>
        spec.foreach(x => addDecl(x.entry, featureExpr, env, false))
      // addDecl(decl, env)
      case ConditionalExpr(expr, thenExpr, elseExpr) =>
        addDecl(expr, featureExpr, env)
        thenExpr.foreach(x => addDecl(x, featureExpr, env))
        addDecl(elseExpr, featureExpr, env)
      case PointerCreationExpr(expr) =>
        addDecl(expr, featureExpr, env)
      case Constant(_) =>
      case CompoundStatement(statement) => statement.foreach(x => addDecl(x.entry, featureExpr, env))
      case PointerDerefExpr(expr) => // addUse(expr, env)
      case WhileStatement(expr, cond) =>
        addDecl(expr, featureExpr, env)
        cond.toOptList.foreach(x => addDecl(x.entry, featureExpr, env))
      case ArrayAccess(expr) => addDecl(expr, featureExpr, env)
      case Choice(ft, then, els) =>
        addDecl(then, featureExpr, env)
        addDecl(els, featureExpr, env)
      case _ => // assert(false, println("Match Error"))
    }
  }

  private def addLcurlyInitializer(id: Id, init: Initializer, feature: FeatureExpr, env: Env) {
    init match {
      case Initializer(_, LcurlyInitializer(lst)) =>
        lst.foreach(x => x.entry match {
          case Initializer(Some(InitializerAssigment(lst2)), _) =>
            lst2.foreach(y => y.entry match {
              case idd@InitializerDesignatorD(i) =>
                addStructUse(i, feature, env, id.name, false)
              case _ => // assert(false, println("Match Error"))
            })
          case _ => // assert(false, println("Match Error"))
        })
      case _ => // assert(false, println("Match Error"))
    }
  }

  def addJumpStatements(compoundStatement: CompoundStatement) = addGotoStatements(compoundStatement)

  private def addGotoStatements(f: AST) {
    val labelMap: IdentityHashMap[Id, FeatureExpr] = new IdentityHashMap

    def getLabels(a: Any): List[Opt[_]] = {
      a match {
        case o@Opt(ft, entry: LabelStatement) =>
          List(o)
        case l: List[_] => l.flatMap(x => getLabels(x))
        case p: Product => p.productIterator.toList.flatMap(x => getLabels(x))
        case _ => List()
      }
    }
    def getGotos(a: Any): List[Opt[_]] = {
      a match {
        case o@Opt(ft, entry: GotoStatement) =>
          List(o)
        case l: List[_] => l.flatMap(x => getGotos(x))
        case p: Product => p.productIterator.toList.flatMap(x => getGotos(x))
        case _ => List()
      }
    }
    getLabels(f).foreach(x => {
      val label = x.entry.asInstanceOf[LabelStatement]
      putToDeclUseMap(label.id)
      labelMap.put(label.id, x.feature)
    })
    getGotos(f).foreach(x => {
      val goto = x.entry.asInstanceOf[GotoStatement]
      goto.target match {
        case usage@Id(name) =>
          labelMap.keySet().toArray().foreach(declaration => {
            if (declaration.asInstanceOf[Id].name.equals(name) &&
              (x.feature.equivalentTo(FeatureExprFactory.True) || labelMap.get(declaration).implies(x.feature).isTautology)) {
              addToDeclUseMap(declaration.asInstanceOf[Id], usage)
            }
          })
        case k => println("Missing GotoStatement match: " + k)
      }
    })
  }

  // method recursively filters all AST elements for a given type T
  // Copy / Pasted from ASTNavigation -> unable to include ASTNavigation because of dependencies
  private def filterASTElemts[T <: AST](a: Any)(implicit m: ClassManifest[T]): List[T] = {
    a match {
      case p: Product if (m.erasure.isInstance(p)) => List(p.asInstanceOf[T])
      case l: List[_] => l.flatMap(filterASTElemts[T])
      case p: Product => p.productIterator.toList.flatMap(filterASTElemts[T])
      case _ => List()
    }
  }
}