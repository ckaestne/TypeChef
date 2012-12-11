package de.fosd.typechef.typesystem

import java.util.IdentityHashMap
import de.fosd.typechef.parser.c._
import scala.collection.JavaConversions._
import de.fosd.typechef.parser.c.PostfixExpr
import de.fosd.typechef.parser.c.PlainParameterDeclaration
import de.fosd.typechef.parser.c.ArrayAccess
import de.fosd.typechef.parser.c.ReturnStatement
import de.fosd.typechef.parser.c.Enumerator
import de.fosd.typechef.parser.c.AtomicNamedDeclarator
import de.fosd.typechef.parser.c.EnumSpecifier
import de.fosd.typechef.parser.c.VarArgs
import de.fosd.typechef.parser.c.CompoundStatementExpr
import scala.Some
import de.fosd.typechef.parser.c.NAryExpr
import de.fosd.typechef.parser.c.TypeDefTypeSpecifier
import de.fosd.typechef.parser.c.Initializer
import de.fosd.typechef.parser.c.DoStatement
import de.fosd.typechef.parser.c.StructOrUnionSpecifier
import de.fosd.typechef.parser.c.PointerCreationExpr
import de.fosd.typechef.parser.c.PointerPostfixSuffix
import de.fosd.typechef.parser.c.AssignExpr
import de.fosd.typechef.conditional.{Choice, One, Opt}
import de.fosd.typechef.parser.c.ConditionalExpr
import de.fosd.typechef.parser.c.FunctionCall
import de.fosd.typechef.parser.c.DeclArrayAccess
import de.fosd.typechef.parser.c.IfStatement
import de.fosd.typechef.parser.c.BuiltinOffsetof
import de.fosd.typechef.parser.c.DeclParameterDeclList
import de.fosd.typechef.parser.c.NArySubExpr
import de.fosd.typechef.parser.c.WhileStatement
import de.fosd.typechef.parser.c.Pointer
import de.fosd.typechef.parser.c.InitDeclaratorI
import de.fosd.typechef.parser.c.SimplePostfixSuffix
import de.fosd.typechef.parser.c.SizeOfExprT
import de.fosd.typechef.parser.c.UnaryOpExpr
import de.fosd.typechef.parser.c.Declaration
import de.fosd.typechef.parser.c.LabelStatement
import de.fosd.typechef.parser.c.ExprStatement
import de.fosd.typechef.parser.c.Id
import de.fosd.typechef.parser.c.Constant
import de.fosd.typechef.parser.c.DeclarationStatement
import de.fosd.typechef.parser.c.PointerDerefExpr
import de.fosd.typechef.parser.c.StructDeclaration
import de.fosd.typechef.parser.c.GotoStatement
import de.fosd.typechef.parser.c.FunctionDef
import de.fosd.typechef.parser.c.SizeOfExprU
import de.fosd.typechef.parser.c.BreakStatement
import de.fosd.typechef.parser.c.ContinueStatement
import de.fosd.typechef.parser.c.OffsetofMemberDesignatorID
import de.fosd.typechef.parser.c.ParameterDeclarationD
import de.fosd.typechef.parser.c.TypeName
import de.fosd.typechef.parser.c.CastExpr
import de.fosd.typechef.parser.c.CompoundStatement
import de.fosd.typechef.parser.c.AtomicAbstractDeclarator
import de.fosd.typechef.parser.c.NestedNamedDeclarator
import de.fosd.typechef.parser.c.StructInitializer
import de.fosd.typechef.parser.c.ParameterDeclarationAD
import de.fosd.typechef.parser.c.StringLit
import de.fosd.typechef.parser.c.StructDeclarator
import de.fosd.typechef.parser.c.UnaryExpr
import java.util
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}


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

  def addGotos(compoundStatement: CompoundStatement) = {
    addGotoStatements(compoundStatement)
  }

  // add definition:
  //   - function: function declarations (forward declarations) and function definitions are handled
  //               if a function declaration exists, we add it as def and the function definition as its use
  //               if no function declaration exists, we add the function definition as def
  def addDefinition(definition: AST, env: Env, feature: FeatureExpr = FeatureExprFactory.True, isFunctionDeclarator: Boolean = false) {
    definition match {
      case id: Id =>
        if (isFunctionDeclarator) {
          val test = env.varEnv.getAstOrElse(id.name, null)
          env.varEnv.getAstOrElse(id.name, null) match {
            case One(null) =>
              putToDeclUseMap(id)
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
                  case i: InitDeclarator =>
                    if (declUseMap.containsKey(i.getId)) {
                      currentForwardDeclaration = i.getId
                      val temp = declUseMap.get(i.getId)
                      if (feature.equivalentTo(FeatureExprFactory.True) || (x._1.implies(feature).isTautology())) {
                        declUseMap.remove(i.getId)
                        putToDeclUseMap(id)
                        addToDeclUseMap(id, i.getId)
                        temp.keySet().toArray().foreach(x => addToDeclUseMap(id, x.asInstanceOf[Id]))
                      } else {
                        // kein plan?
                      }
                    } else {
                      putToDeclUseMap(id)
                      addToDeclUseMap(id, i.getId)
                    }

                  case _ =>
                }
              })
            case k => println("Missing: " + k)
          }
        } else {
          putToDeclUseMap(id)
        }
      case StructDeclaration(quals, decls) =>
        decls.foreach(x => addDecl(x.entry, x.feature, env))
      case k =>
        println("Missing AddDefinition: " + k)
    }
  }


  def addDef(f: AST, featureExpr: FeatureExpr, env: Env) {
    f match {
      case func@FunctionDef(specifiers, declarator, oldStyleParameters, _) => {
        val id = declarator.getId
        val ext = declarator.extensions
        env.varEnv.getAstOrElse(id.name, null) match {
          case null => putToDeclUseMap(declarator.getId)
          case One(null) => putToDeclUseMap(declarator.getId)
          case One(i: InitDeclarator) =>
            /*
           Special handling for forward declarations because the env gives us the function
           and not the first forward declaration when calling the function.
           Basically you switch from (ForwardDeclarationId -> List(FunctionCallIds)) to (FunctionDefId -> ForwardDeclarationId :: List(FunctionCallIds)
            */
            if (declUseMap.contains(i.getId)) {
              val temp = declUseMap.get(i.getId)
              declUseMap.remove(i.getId)
              putToDeclUseMap(declarator.getId)
              addToDeclUseMap(declarator.getId, i.getId)
              temp.keySet().toArray().foreach(x => addToDeclUseMap(declarator.getId, x.asInstanceOf[Id]))
            } else {
              addUse(id, featureExpr, env)
            }
          case c@Choice(_, _, _) =>
            putToDeclUseMap(id)
          // addChoiceFunctionDef(c, declarator, env)
          case k => // println("Missing AddDef " + id + "\nentry " + k + "\nfuncdef " + func + "\n" + defuse.containsKey(declarator.getId))
        }
        // check function definiton for goto statements and add them to defUse
        addGotoStatements(f)
        // add the function parameters to defuse
        addFunctionParametersToDefUse(ext, featureExpr, env)
        // add function specifiers to defuse
        func.specifiers.foreach(spec => addSpecifiers(spec.entry, featureExpr, env))
      }
      case i: InitDeclarator => putToDeclUseMap(i.getId)
      case id: Id =>
        env.varEnv.getAstOrElse(id.name, null) match {
          case null => putToDeclUseMap(id)
          case One(null) => putToDeclUseMap(id)
          case One(i: InitDeclarator) => putToDeclUseMap(id)
          case One(e: Enumerator) => putToDeclUseMap(id) // TODO ENUM Verification
          case c@Choice(_, _, _) => addDefChoice(c)
          case k => // println("Oh i forgot " + k)
        }
      // TODO Check with new StructEnv!
      case st: StructDeclaration =>
        st.declaratorList.foreach(x => x.entry match {
          case StructDeclarator(AtomicNamedDeclarator(_, id: Id, _), _, _) =>
            env.varEnv.getAstOrElse(id.name, null) match {
              case null => // putToDeclUseMap(id)
              case One(null) => // putToDeclUseMap(id)
              case One(i: InitDeclarator) => {
                val key = i.getId
                // TODO: AddUse?
                putToDeclUseMap(key)
              }
              case _ => // println("match error " + env.varEnv.getAstOrElse(id.name, null))
            }
          case StructDeclarator(NestedNamedDeclarator(pointers, nestedDecl, _), _, _) =>
            pointers.foreach(x => addDecl(x, featureExpr, env))
            putToDeclUseMap(nestedDecl.getId)
          case k => // println("Pattern StructDeclaration fail: " + k)
        })
      case and@AtomicNamedDeclarator(_, id: Id, _) =>
        env.varEnv.getAstOrElse(id.name, null) match {
          case null => putToDeclUseMap(id)
          case One(null) => putToDeclUseMap(id)
          case One(i: InitDeclarator) => {
            val key = i.getId
            // TODO AddUse?
            putToDeclUseMap(key)
          }
        }
      case Declaration(specs, inits) => inits.foreach(x => putToDeclUseMap(x.entry.getId))
      case k => // println("Missing Add Def: " + f + " from " + k)
    }
  }

  private def addChoiceFunctionDef(c: Choice[AST], decl: Declarator, featureExpr: FeatureExpr, env: Env) {
    def addOne(one: One[AST], decl: Declarator, env: Env) {
      one match {
        case One(FunctionDef(_, _, _, _)) => putToDeclUseMap(decl.getId) // TODO Verify init
        case One(InitDeclaratorI(AtomicNamedDeclarator(_, id2: Id, _), _, _)) => addUse(decl.getId, featureExpr, env)
        case One(null) =>
        case _ => println("FunctionDefChoice: This should not have happend: " + one)
      }
    }
    c match {
      case Choice(_, o1@One(_), o2@One(_)) =>
        addOne(o1, decl, env)
        addOne(o2, decl, env)
      case Choice(_, o@One(_), c@Choice(_, _, _)) =>
        addOne(o, decl, env)
        addChoiceFunctionDef(c, decl, featureExpr, env)
      case Choice(_, c1@Choice(_, _, _), c2@Choice(_, _, _)) =>
        addChoiceFunctionDef(c1, decl, featureExpr, env)
        addChoiceFunctionDef(c2, decl, featureExpr, env)
      case Choice(_, c@Choice(_, _, _), o@One(_)) =>
        addOne(o, decl, env)
        addChoiceFunctionDef(c, decl, featureExpr, env)
      case _ => println("FunctionDefChoice: This should not have happend " + c)
    }
  }

  private def addFunctionParametersToDefUse(ext: List[Opt[DeclaratorExtension]], featureExpr: FeatureExpr, env: Env) {
    ext.foreach(x => x match {
      case null =>
      case Opt(_, d: DeclParameterDeclList) => d.parameterDecls.foreach(pdL => pdL match {
        case Opt(_, pd: ParameterDeclarationD) => {
          val paramID = pd.decl.getId
          env.varEnv.getAstOrElse(paramID.name, null) match {
            case null =>
              putToDeclUseMap(paramID)
            case One(null) =>
              putToDeclUseMap(paramID)
            case One(i: InitDeclarator) => addUse(paramID, featureExpr, env)
            case m => println("pdl missed" + m)
          }
          // match extensions
          val extensions = pd.decl.extensions
          val specs = pd.specifiers
          addFunctionParametersToDefUse(extensions, featureExpr, env)
          specs.foreach(x => addUse(x.entry, featureExpr, env))
        }
        case Opt(_, p: PlainParameterDeclaration) => p.specifiers.foreach(x => addDecl(x.entry, featureExpr, env))
        case Opt(_, pad: ParameterDeclarationAD) =>
          addDecl(pad.decl, featureExpr, env)
          pad.specifiers.foreach(x => addDecl(x.entry, featureExpr, env))
        case e =>
      })
      case Opt(_, DeclArrayAccess(Some(x))) =>
        addUse(x, featureExpr, env)
      case Opt(_, DeclArrayAccess(None)) =>
      // ignore
      case Opt(_, d: DeclIdentifierList) =>
      //TODO: ignore?
      case mi => println("Completly missing: " + mi)
    })
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
    }
  }

  def addTypeUse(entry: AST, env: Env, feature: FeatureExpr) {
    entry match {
      case i@Id(name) =>
        if (env.typedefEnv.contains(name)) {
          env.typedefEnv.getAstOrElse(name, null) match {
            case One(InitDeclaratorI(declarator, _, _)) =>
              addToDeclUseMap(declarator.getId, i)
            case One(AtomicNamedDeclarator(_, key, _)) =>
              addToDeclUseMap(key, i)
            case One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)) =>
              addToDeclUseMap(key, i)
            case Choice(feature, One(InitDeclaratorI(declarator, _, _)), One(InitDeclaratorI(declarator2, _, _))) =>
              addToDeclUseMap(declarator.getId, i)
              addToDeclUseMap(declarator2.getId, i)
            case Choice(feature, One(InitDeclaratorI(declarator, _, _)), _) =>
              addToDeclUseMap(declarator.getId, i)
            case Choice(feature, One(AtomicNamedDeclarator(_, key, _)), One(AtomicNamedDeclarator(_, key2, _))) =>
              addToDeclUseMap(key, i)
              addToDeclUseMap(key2, i)
            case Choice(feature, One(AtomicNamedDeclarator(_, key, _)), _) =>
              addToDeclUseMap(key, i)
            case c@Choice(feature, One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)), One(FunctionDef(_, AtomicNamedDeclarator(_, key2, _), _, _))) =>
              addToDeclUseMap(key, i)
              addToDeclUseMap(key2, i)
            case Choice(feature, One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)), _) =>
              addToDeclUseMap(key, i)
            case Choice(feature, One(Declaration(specs, init)), _) =>
              init.foreach(x => x.entry match {
                case InitDeclaratorI(AtomicNamedDeclarator(_, key, _), _, _) =>
                  addToDeclUseMap(key, i)
                case k => // println("AddTypeUse Choice not exhaustive: " + k)
              })
            case One(Enumerator(key, _)) =>
              addToDeclUseMap(key, i)
            case One(Declaration(specifiers, inits)) =>
              inits.foreach(x => x match {
                case Opt(ft, InitDeclaratorI(AtomicNamedDeclarator(_, key: Id, _), _, _)) =>
                  addToDeclUseMap(key, i)
                case Opt(ft, InitDeclaratorI(NestedNamedDeclarator(_, AtomicNamedDeclarator(_, key, _), _), _, _)) =>
                  addToDeclUseMap(key, i)
                case k => // println("Fehlt: " + k)
              })
            case k =>
              if (name.startsWith("__builtin")) {
                // putToDeclUseMap(i)
              } else {
                // println("Missing: " + i + "\nElement " + k)
              }
          }
        }
    }
  }

  private def addChoice(choice: Choice[AST], featureExpr: FeatureExpr, use: Id) {
    def addOne(one: One[AST], use: Id) {
      one match {
        case One(InitDeclaratorI(declarator, _, _)) => addToDeclUseMap(declarator.getId, use)
        case One(AtomicNamedDeclarator(_, key, _)) => addToDeclUseMap(key, use)
        case One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)) => addToDeclUseMap(key, use)
        case One(Enumerator(key, _)) => addToDeclUseMap(key, use)
        case One(NestedNamedDeclarator(_, declarator, _)) => addToDeclUseMap(declarator.getId, use)
        case One(null) =>
        case _ => println("AddChoices - should not have happend: " + one)
      }
    }

    choice match {
      case Choice(feature1, o1@One(_), o2@One(_)) =>
        if (featureExpr.equivalentTo(FeatureExprFactory.True)) {
          addOne(o1, use)
          addOne(o2, use)
        } else if (featureExpr.implies(feature1).isTautology()) {
          addOne(o1, use)
        } else if (featureExpr.implies(feature1.not).isTautology()) {
          addOne(o2, use)
        } else {
          addOne(o1, use)
          addOne(o2, use)
        }
      case Choice(feature1, o@One(_), c@Choice(feature2, _, _)) =>
        addOne(o, use)
        if (!featureExpr.equivalentTo(feature1)) {
          addChoice(c, featureExpr, use)
        }
      case Choice(_, c1@Choice(_, _, _), c2@Choice(_, _, _)) =>
        addChoice(c1, featureExpr, use)
        addChoice(c2, featureExpr, use)
      case Choice(_, c@Choice(_, _, _), o@One(_)) =>
        addOne(o, use)
        addChoice(c, featureExpr, use)
      case _ => println("AddChoiceUse: This should not have happend " + choice)
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

  def addSpecifiers(entry: AST, feature: FeatureExpr, env: Env) {
    entry match {
      case EnumSpecifier(x, _) => x.foreach(id => {
        /* Enum typespecifier are not correctly implemented @ CTypeSystem
        * Workaround: lookup for id and add.
        */
        if (env.enumEnv.contains(id.name)) {
          for (key <- declUseMap.keys) {
            if (key.equals(id)) {
              addToDeclUseMap(key, id)
            }
          }
        }
      })
      case StructOrUnionSpecifier(union, i, _) => addStructUse(i.get, feature, env, i.get.name, union)
      case k =>
    }
  }

  def addUse(entry: AST, feature: FeatureExpr, env: Env) {
    entry match {
      case ConditionalExpr(expr, thenExpr, elseExpr) =>
        addUse(expr, feature, env)
        thenExpr.foreach(x => addUse(x, feature, env))
        addUse(elseExpr, feature, env)
      case EnumSpecifier(x, _) => x.foreach(id => {
        /* Enum typespecifier are not correctly implemented @ CTypeSystem
        * Workaround: lookup for id and add.
        */
        if (env.enumEnv.contains(id.name)) {
          for (key <- declUseMap.keys) {
            if (key.equals(id)) {
              addToDeclUseMap(key, id)
            }
          }
        }
      })
      case FunctionCall(param) => param.exprs.foreach(x => addUse(x.entry, feature, env))
      case ExprList(exprs) => exprs.foreach(x => addUse(x.entry, feature, env))
      case LcurlyInitializer(inits) =>
        inits.foreach(x => addUse(x.entry, feature, env))
      case InitializerAssigment(designators) =>
        designators.foreach(x => addUse(x.entry, feature, env))
      case InitializerDesignatorD(i: Id) =>
        addUse(i, feature, env)
      case Initializer(Some(x), expr) =>
        addUse(x, feature, env)
        addUse(expr, feature, env)
      case i@Id(name) =>
        env.varEnv.getAstOrElse(name, null) match {
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
          case One(Enumerator(key, _)) =>
            addToDeclUseMap(key, i)
          case One(NestedNamedDeclarator(_, nestedDecl, _)) => addToDeclUseMap(nestedDecl.getId, i)
          case c@Choice(_, _, _) => addChoice(c, feature, i)
          case _ =>
        }
      case PointerDerefExpr(i) => addUse(i, feature, env)
      case AssignExpr(target, operation, source) =>
        addUse(source, feature, env)
        addUse(target, feature, env)
      case NAryExpr(i, o) =>
        addUse(i, feature, env)
        o.foreach(x => addUse(x.entry, feature, env))
      case NArySubExpr(_, e) =>
        addUse(e, feature, env)
      case PostfixExpr(p, s) =>
        // addStructUse(attributeName, feature, env, structName.name, false)
        // addStructDeclUse(structName, env, false, feature)
        addUse(p, feature, env)
        addUse(s, feature, env)
      case PointerPostfixSuffix(_, id: Id) =>
        if (!env.varEnv.getAstOrElse(id.name, null).equals(One(null))) addUse(id, feature, env)
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
      // filterASTElemts[AST](stuff).foreach(x => addUse(x, env))

      //TODO: Workaround bei castexpr die __missing Id einfangen
      case CastExpr(typ, LcurlyInitializer(lst)) =>
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
                  case c@Choice(_, _, _) => addChoice(c, feature, i)
                  case One(null) =>
                    if (stringToIdMap.containsKey(i.name) && declUseMap.containsKey(stringToIdMap.get(i.name).get)) {
                      addToDeclUseMap(stringToIdMap.get(i.name).get, i)
                    }
                  case k => // println("AddUse Id not exhaustive: " + i + "\nElement " + k)
                }
            })
          case k => addUse(k, feature, env)
        })
        stringToIdMap = stringToIdMap.empty

      case CastExpr(typ, expr) =>
        addUse(typ, feature, env)
        addUse(expr, feature, env)
      case ArrayAccess(expr) => addUse(expr, feature, env)
      case UnaryExpr(_, expr) =>
        addUse(expr, feature, env)
      case UnaryOpExpr(_, expr) => addUse(expr, feature, env)
      case TypeDefTypeSpecifier(id) =>
        addTypeUse(id, env, feature)
      case Initializer(_, expr) => addUse(expr, feature, env)
      case CompoundStatementExpr(expr) => addUse(expr, feature, env)
      case StructOrUnionSpecifier(union, Some(i: Id), _) =>
        addStructDeclUse(i, env, union, feature)
      case BuiltinOffsetof(typeName, members) =>
        typeName.specifiers.foreach(x => addUse(x.entry, feature, env))
        /**
         * Workaround for buitlin_offset_ -> typechef implementation too much - see: http://gcc.gnu.org/onlinedocs/gcc/Offsetof.html
         */
        val structOrUnion = filterASTElemts[Id](typeName)
        members.foreach(x => addStructUse(x.entry, feature, env, structOrUnion.head.name, !env.structEnv.someDefinition(structOrUnion.head.name, false)))
      case k =>
      /*if (!k.isInstanceOf[Specifier]) {
        println(" Completly missing add use: " + k + " " + k.getPositionFrom)
      }*/
    }
  }

  def choiceToTuple(choice: Choice[_]): List[Tuple2[FeatureExpr, AST]] = {
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

  def addStructDefinition(i: Id) = {
    putToDeclUseMap(i)
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
                                  } else if (x._1.implies(specFeature).isTautology) {
                                    addToDeclUseMap(x._2.asInstanceOf[Id], i)
                                  }
                                })
                            }
                        }
                      }
                      putToDeclUseMap(paraDeclDecl.getId)
                  }
                }
              case k =>
                println("RATAT: " + k)

            }
          }
      }
    }

    def handleSpecifiers(specs: List[Opt[Specifier]]) = {
      for (Opt(specFeature, specEntry) <- specs) {
        specEntry match {
          case StructOrUnionSpecifier(false, Some(i: Id), None) =>

        }
      }
    }

    val isTypeDef = decl.declSpecs.exists(x => x.entry.equals(TypedefSpecifier()))
    val isStructOrUnion = decl.declSpecs.exists(x => x.entry.isInstanceOf[StructOrUnionSpecifier])
    val isIncompleteStruct = isStructOrUnion && (decl.init.isEmpty || (isTypeDef && decl.init.size == 1))
    val isTypeDefStruct = isStructOrUnion && isTypeDef
    //println(decl + "\n" + PrettyPrinter.print(decl) + "\n\n")
    for (Opt(initFeature, init) <- decl.init) {
      init match {
        case InitDeclaratorI(declarator: AtomicNamedDeclarator, attributes, i) =>
          putToDeclUseMap(declarator.getId)
          handleAtomicNamedDeclaratorExtensions(declarator)
        case InitDeclaratorE(declarator: AtomicNamedDeclarator, attributes, expr) =>
          putToDeclUseMap(declarator.getId)
          handleAtomicNamedDeclaratorExtensions(declarator)
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
              addDef(i, featureExpr, env)
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
                if (feature.equivalentTo(FeatureExprFactory.True)) {
                  addToDeclUseMap(x._2.asInstanceOf[Id], use)
                } else if (x._1.implies(feature).isTautology) {
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

  def addStructDeclaration(entry: Id) = {
    putToDeclUseMap(entry)
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
        pointers.foreach(x => addDecl(x, featureExpr, env))
        extension.foreach(x => addDecl(x, featureExpr, env))
        // addDef(id, featureExpr, env)
        addDefinition(id, env)
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
      case k =>
        if (!k.isInstanceOf[BreakStatement] && !k.isInstanceOf[ContinueStatement] && !k.isInstanceOf[SimplePostfixSuffix] && !k.isInstanceOf[Specifier] && !k.isInstanceOf[DeclArrayAccess] && !k.isInstanceOf[VarArgs] && !k.isInstanceOf[AtomicAbstractDeclarator] && !k.isInstanceOf[StructInitializer] && !k.isInstanceOf[StringLit]) {
          // println("Missing Case: " + k)
        }
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
            })
          case _ =>
        })
      case _ =>
    }
  }

  private def addGotoStatements(f: AST) {
    val labels = filterASTElemts[LabelStatement](f)
    val gotos = filterASTElemts[GotoStatement](f)
    labels.foreach(x => {
      val id = x.id
      putToDeclUseMap(id)
      gotos.foreach(y => y match {
        case GotoStatement(id2) => if (id.equals(id2)) addToDeclUseMap(id, id2.asInstanceOf[Id])
        case _ =>
      })
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