package de.fosd.typechef.typesystem

import scala.collection.JavaConversions._
import java.util.{Collections, IdentityHashMap}

import org.apache.logging.log4j.LogManager

import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import de.fosd.typechef.parser.c._
import scala.reflect.ClassTag
import java.util


// this trait is a hook into the typesystem to preserve typing informations
// of declarations and usages
// the trait basically provides two maps: declaration -> usage and usages -> declarations
// for all identifiers that occur in a translation unit
// to do so typed elements are passed during typechecking to CDeclUse which
// stores the required information
trait CDeclUse extends CEnv with CEnvCache {

  private lazy val logger = LogManager.getLogger(this.getClass.getName)

  private val declUseMap: util.IdentityHashMap[Id, util.Set[Id]] = new util.IdentityHashMap()
  private val useDeclMap: util.IdentityHashMap[Id, List[Id]] = new util.IdentityHashMap()
  private var stringToIdMap: Map[String, Id] = Map()

  private[typesystem] def clear() {
    clearDeclUseMap()
  }

  private def putToDeclUseMap(decl: Id) = if (!declUseMap.contains(decl)) declUseMap.put(decl, Collections.newSetFromMap[Id](new util.IdentityHashMap()))

  private def addToDeclUseMap(decl: Id, use: Id): Any = {
    if (decl.eq(use) && !declUseMap.containsKey(decl)) putToDeclUseMap(decl)

    if (declUseMap.containsKey(decl) && !declUseMap.get(decl).contains(use)) {
      declUseMap.get(decl).add(use)
      addToUseDeclMap(use, decl)
    }
  }

  private def addToUseDeclMap(use: Id, decl: Id) = {
    if (useDeclMap.contains(use)) useDeclMap.put(use, decl :: useDeclMap.get(use))
    else useDeclMap.put(use, List(decl))
  }

  def clearDeclUseMap() {
    declUseMap.clear()
    useDeclMap.clear()
  }

  def getDeclUseMap: util.IdentityHashMap[Id, List[Id]] = {
    val morphedDeclUsedMap = new util.IdentityHashMap[Id, List[Id]]()
    declUseMap.keySet().foreach(x => morphedDeclUsedMap.put(x, declUseMap.get(x).toList))
    morphedDeclUsedMap
  }

  def getUseDeclMap = useDeclMap

  def getUntouchedDeclUseMap = declUseMap

  // add definition:
  //   - function: function declarations (forward declarations) and function definitions are handled
  //               if a function declaration exists, we add it as def and the function definition as its use
  //               if no function declaration exists, we add the function definition as def
  def addDefinition(definition: AST, env: Env, feature: FeatureExpr = FeatureExprFactory.True, isFunctionDeclarator: Boolean = false) {
    definition match {
      case id: Id =>
        if (isFunctionDeclarator) addFunctionDeclaration(env, id, feature)
        else putToDeclUseMap(id)
      case StructDeclaration(quals, decls) => decls.foreach(x => addDecl(x.entry, x.feature, env))
      case _ => logger.error("Missed ForwardDeclaration of: " + definition)
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
          temp.foreach(x => addToDeclUseMap(id, x))
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
        temp.foreach(x => addToDeclUseMap(id, x))
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
      case x => assert(false, logger.error("ForwardDeclaration of function failed with " + x))
    }
  }

  def addEnumUse(entry: AST, env: Env, feature: FeatureExpr) {
    entry match {
      case i@Id(name) if (env.enumEnv.containsKey(name)) =>
        val enumDeclarationFeature = env.enumEnv.get(name).get._1
        val enumDeclarationId = env.enumEnv.get(name).get._2
        if (feature.equivalentTo(FeatureExprFactory.True) || (feature.implies(enumDeclarationFeature).isTautology())) {
          addToDeclUseMap(enumDeclarationId, i)
        }
      case x => logger.error("Missing Enumeration: " + x)
    }
  }

  def addTypeUse(entry: AST, env: Env, feature: FeatureExpr) {
    def addOne(one: One[AST], use: Id, env: Env) {
      one match {
        case One(InitDeclaratorI(declarator, _, _)) => addToDeclUseMap(declarator.getId, use)
        case One(AtomicNamedDeclarator(_, key, _)) => addToDeclUseMap(key, use)
        case One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)) => addToDeclUseMap(key, use)
        case One(Enumerator(key, _)) => addToDeclUseMap(key, use)
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
            case o@One(_) => addOne(o, i, env)
            case c@Choice(_, _, _) => addChoice(c, feature, i, env, addOne)
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
        } else if (featureExpr.implies(feature1.not()).isTautology()) {
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
      case _ => assert(false, logger.error("Match Error"))
    }
  }

  private def addDefChoice(entry: Choice[AST]) {
    def addOne(entry: One[AST]) {
      entry match {
        case One(InitDeclaratorI(declarator, _, _)) => putToDeclUseMap(declarator.getId)
        case One(AtomicNamedDeclarator(_, key, _)) => putToDeclUseMap(key)
        case One(Enumerator(key, _)) => putToDeclUseMap(key)
        case One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)) => putToDeclUseMap(key)
        case One(null) =>
        case k => logger.error("DefChoice: Missed add One " + k)
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
        logger.error("Missed Def Choice " + k)
    }
  }


  // TODO andreas: refactor code looks a little messy
  def addUse(entry: AST, feature: FeatureExpr, env: Env) {

    def addUseCastExpr(typ: TypeName, addUse: (AST, FeatureExpr, CDeclUse.this.type#Env) => Unit, feature: FeatureExpr, env: CDeclUse.this.type#Env, lst: List[Opt[Initializer]]) {
      var typedefspecifier: Id = null
      typ match {
        case TypeName(ls, _) =>
          ls.foreach(x => x.entry match {
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
                case k => // logger.error("AddUse Id not exhaustive: " + i + "\nElement " + k)
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
        case _ => assert(false, logger.error("Match Error" + one))
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
      case PointerPostfixSuffix(_, id: Id) => //if (!env.varEnv.getAstOrElse(id.name, null).equals(One(null))) addUse(id, feature, env)
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
         * TODO andreas: comment not very clear. What is the problem?
         * Workaround for buitlin_offset_ -> typechef implementation too much - see: http://gcc.gnu.org/onlinedocs/gcc/Offsetof.html
         */
        val structOrUnion = filterASTElemts[Id](typeName)
        members.foreach(x => addStructUse(x.entry, feature, env, structOrUnion.head.name, !env.structEnv.someDefinition(structOrUnion.head.name, false)))
      case _ =>
    }
  }


  // TODO andreas: could be rewritten when using the common abstract class Conditional of One and Choice
  // def conditionalToTuple(cond: Conditional[_], fexp: FeatureExpr = <defaultvalue>): List[(FeatureExpr, AST)] = {
  //   cond match {
  //     case One(a: AST) => List((fexp, a))
  //     case Choice(ft, thenExpr, elseBranch) => conditionalToTuple(thenExpr, ft) ++ conditionaToTuple(elseBranch, ft.not())
  //     case _ =>
  //   }
  // }
  private def choiceToTuple(choice: Choice[_]): List[Tuple2[FeatureExpr, AST]] = {
    def addOne(entry: One[_], ft: FeatureExpr): List[Tuple2[FeatureExpr, AST]] = {
      entry match {
        case One(null) => List()
        case One(a: AST) => List(Tuple2(ft, a))
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
    def handleAtomicNamedDeclaratorExtensions(and: AtomicNamedDeclarator) {
      and match {

        // TODO andreas: this code needs to be refactored
        // too much nesting
        // comments are necessary; what kind of declarations are handled
        case AtomicNamedDeclarator(_, _, extensions) =>
          for (Opt(extensionFeature, extensionEntry) <- extensions) {
            extensionEntry match {
              case DeclIdentifierList(ids) =>
                for (Opt(idFeature, id: Id) <- ids) {
                  logger.debug("DeclIdentifierId: " + id)
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
                                  } else if (specFeature.implies(x._1).isTautology()) {
                                    addToDeclUseMap(x._2.asInstanceOf[Id], i)
                                  }
                                })
                              case _ => assert(false, logger.error("Match Error"))
                            }
                          case _ => assert(false, logger.error("Match Error"))
                        }
                      }
                      putToDeclUseMap(paraDeclDecl.getId)
                  }
                }
              case _ => assert(false, logger.error("Match Error"))
            }
          }
      }
    }

    for (Opt(initFeature, init) <- decl.init) {
      init match {
        case InitDeclaratorI(declarator: AtomicNamedDeclarator, attributes, i) =>
          putToDeclUseMap(declarator.getId)
          handleAtomicNamedDeclaratorExtensions(declarator)
        case InitDeclaratorE(declarator: AtomicNamedDeclarator, attributes, expr) =>
          putToDeclUseMap(declarator.getId)
          handleAtomicNamedDeclaratorExtensions(declarator)
        case _ => assert(assertion = false, logger.error("Match Error"))
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
            case k => logger.error("Missed addStructUse " + env.varEnv.getAstOrElse(i.name, null))
          }
        } else {
          env.typedefEnv.getAstOrElse(i.name, null) match {
            case One(i2: Id) =>
              addToDeclUseMap(i2, i)
            case One(null) =>
              addDefinition(i, env)
            case c@Choice(_, _, _) =>
              logger.error("missed choice typedef " + c)
            case One(Declaration(List(Opt(_, _), Opt(_, s@StructOrUnionSpecifier(_, Some(id), _))), _)) =>
              // TODO andreas: typedef name name // comment not specific
              putToDeclUseMap(i)
            case k =>
              logger.error("Missed addStructUse " + k)
          }
        }
      }
      case OffsetofMemberDesignatorID(id) =>
        addStructUse(id, featureExpr, env, structName, isUnion)
      case k => logger.error("Missed addStructUse " + k)
    }
  }

  def addAnonStructUse(id: Id, fields: ConditionalTypeMap) {
    fields.getAstOrElse(id.name, null) match {
      case c@Choice(_, _, _) => addStructUseChoice(c, id)
      case One(AtomicNamedDeclarator(_, key, _)) =>
        // TODO: remove workaround (next three lines of code) for missing definitions
        if (!declUseMap.containsKey(key)) {
          putToDeclUseMap(key)
        }
        addToDeclUseMap(key, id)
      case One(NestedNamedDeclarator(_, declarator, _)) => addToDeclUseMap(declarator.getId, id)
      case k => logger.error("Should not have entered here: " + id + "\n" + k)
    }
  }

  private def addStructUseChoice(choice: Choice[AST], use: Id) {
    def addOne(one: One[AST], use: Id) {
      one match {
        case One(AtomicNamedDeclarator(_, key, _)) =>
          // TODO: remove workaround (next three lines of code) for missing definitions
          if (!declUseMap.containsKey(key)) {
            putToDeclUseMap(key)
          }
          addToDeclUseMap(key, use)
        case One(NestedNamedDeclarator(_, declarator, _)) => addToDeclUseMap(declarator.getId, use)
        case One(i@Id(_)) => addToDeclUseMap(i, use) // TODO Missing case, but @decluse?

        // TODO andreas: following line is obsolete
        case One(null) =>
        //addStructDeclUse(use, env, isUnion)
        case _ => logger.error("AddAnonStructChoice missed " + one)
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
      case _ => logger.error("AddAnonStructChoice: This should not have happend " + choice)
    }
  }

  def addStructDeclUse(entry: Id, env: Env, isUnion: Boolean, feature: FeatureExpr) {
    def addOne(one: One[AST], use: Id) = {
      one match {
        case One(id: Id) => addToDeclUseMap(id, use)

        // TODO andreas: following line is obsolete
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

      // TODO andreas: the following three lines are obsolete; see case _ => at the end
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
          addDefinition(id, env, featureExpr, isFunctionDeclarator = true)
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
      case i@IfStatement(cond, thenExpr, elif, els) =>
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
        addDecl(o, featureExpr, env, isDefinition = false)
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
        spec.foreach(x => addDecl(x.entry, featureExpr, env, isDefinition = false))
      case TypeOfSpecifierT(TypeName(spec, decl)) =>
        spec.foreach(x => addDecl(x.entry, featureExpr, env, isDefinition = false))
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
      case Choice(ft, thenExpr, els) =>
        addDecl(thenExpr, featureExpr, env)
        addDecl(els, featureExpr, env)
      case _ => // assert(false, logger.error("Match Error"))
    }
  }

  private def addLcurlyInitializer(id: Id, init: Initializer, feature: FeatureExpr, env: Env) {
    init match {
      case Initializer(_, LcurlyInitializer(lst)) =>
        lst.foreach(x => x.entry match {
          case Initializer(Some(InitializerAssigment(lst2)), _) =>
            lst2.foreach(y => y.entry match {
              case idd@InitializerDesignatorD(i) =>
                addStructUse(i, feature, env, id.name, isUnion = false)
              case _ => // assert(false, logger.error("Match Error"))
            })
          case _ => // assert(false, logger.error("Match Error"))
        })
      case _ => // assert(false, logger.error("Match Error"))
    }
  }

  def addJumpStatements(compoundStatement: CompoundStatement) {
    addGotoStatements(compoundStatement)
  }

  private def addGotoStatements(f: AST) {
    val labelMap: IdentityHashMap[Id, FeatureExpr] = new IdentityHashMap()

    def get[T](a: Any)(implicit m: ClassTag[T]): List[Opt[T]] = {
      a match {
        case o: Opt[T] if (m.runtimeClass.isInstance(o.entry)) => List(o)
        case l: List[_] => l.flatMap(x => get[T](x))
        case p: Product => p.productIterator.toList.flatMap(x => get[T](x))
        case _ => List()
      }
    }

    get[LabelStatement](f).foreach(label => {
      putToDeclUseMap(label.entry.id)
      labelMap.put(label.entry.id, label.feature)
    })
    get[GotoStatement](f).foreach(goto => {
      goto.entry.target match {
        case usage@Id(name) =>
          labelMap.keySet().toArray.foreach(declaration => {
            if (declaration.asInstanceOf[Id].name.equals(name) &&
              (goto.feature.equivalentTo(FeatureExprFactory.True) || labelMap.get(declaration).implies(goto.feature).isTautology)) {
              addToDeclUseMap(declaration.asInstanceOf[Id], usage)
            }
          })
        case k => logger.error("Missing GotoStatement: " + k)
      }
    })
  }

  // method recursively filters all AST elements for a given type T
  // Copy / Pasted from ASTNavigation -> unable to include ASTNavigation because of dependencies
  // TODO: move ASTNavigation to CParser?? if so this method could be removed thenExpr.
  private def filterASTElemts[T <: AST](a: Any)(implicit m: ClassTag[T]): List[T] = {
    a match {
      case p: Product if (m.runtimeClass.isInstance(p)) => List(p.asInstanceOf[T])
      case l: List[_] => l.flatMap(filterASTElemts[T])
      case p: Product => p.productIterator.toList.flatMap(filterASTElemts[T])
      case _ => List()
    }
  }
}