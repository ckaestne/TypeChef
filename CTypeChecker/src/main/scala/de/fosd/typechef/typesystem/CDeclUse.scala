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
import de.fosd.typechef.conditional.Choice
import de.fosd.typechef.parser.c.ConditionalExpr
import de.fosd.typechef.parser.c.FunctionCall
import de.fosd.typechef.conditional.One
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
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.parser.c.NestedNamedDeclarator
import de.fosd.typechef.parser.c.StructInitializer
import de.fosd.typechef.parser.c.ParameterDeclarationAD
import de.fosd.typechef.parser.c.StringLit
import de.fosd.typechef.parser.c.StructDeclarator
import de.fosd.typechef.parser.c.UnaryExpr
import java.util


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
    if (decl.eq(use) && !declUseMap.contains(decl)) {
      putToDeclUseMap(decl)
    }
    if (declUseMap.contains(decl)) {
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
  def addDef(f: AST, env: Env) {
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
              addUse(id, env)
            }
          case c@Choice(_, _, _) =>
            putToDeclUseMap(id)
          // addChoiceFunctionDef(c, declarator, env)
          case k => // println("Missing AddDef " + id + "\nentry " + k + "\nfuncdef " + func + "\n" + defuse.containsKey(declarator.getId))
        }
        // check function definiton for goto statements and add them to defUse
        addGotoStatements(f)
        // add the function parameters to defuse
        addFunctionParametersToDefUse(ext, env)
        // add function specifiers to defuse
        func.specifiers.foreach(spec => addSpecifiers(spec.entry, env))
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
            pointers.foreach(x => addDecl(x, env))
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

  private def addChoiceFunctionDef(c: Choice[AST], decl: Declarator, env: Env) {
    def addOne(one: One[AST], decl: Declarator, env: Env) {
      one match {
        case One(FunctionDef(_, _, _, _)) => putToDeclUseMap(decl.getId) // TODO Verify init
        case One(InitDeclaratorI(AtomicNamedDeclarator(_, id2: Id, _), _, _)) => addUse(decl.getId, env)
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
        addChoiceFunctionDef(c, decl, env)
      case Choice(_, c1@Choice(_, _, _), c2@Choice(_, _, _)) =>
        addChoiceFunctionDef(c1, decl, env)
        addChoiceFunctionDef(c2, decl, env)
      case Choice(_, c@Choice(_, _, _), o@One(_)) =>
        addOne(o, decl, env)
        addChoiceFunctionDef(c, decl, env)
      case _ => println("FunctionDefChoice: This should not have happend " + c)
    }
  }

  private def addFunctionParametersToDefUse(ext: List[Opt[DeclaratorExtension]], env: Env) {
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
            case One(i: InitDeclarator) => addUse(paramID, env)
            case m => println("pdl missed" + m)
          }
          // match extensions
          val extensions = pd.decl.extensions
          val specs = pd.specifiers
          addFunctionParametersToDefUse(extensions, env)
          specs.foreach(x => addUse(x.entry, env))
        }
        case Opt(_, p: PlainParameterDeclaration) => p.specifiers.foreach(x => addDecl(x.entry, env))
        case Opt(_, pad: ParameterDeclarationAD) =>
          addDecl(pad.decl, env)
          pad.specifiers.foreach(x => addDecl(x.entry, env))
        case e =>
      })
      case Opt(_, DeclArrayAccess(Some(x))) =>
        addUse(x, env)
      case Opt(_, DeclArrayAccess(None)) =>
      // ignore
      case Opt(_, d: DeclIdentifierList) =>
        //TODO: ignore?
      case mi => println("Completly missing: " + mi)
    })
  }

  def addTypeUse(entry: AST, env: Env) {
    entry match {
      case i@Id(name) =>
        env.typedefEnv.getAstOrElse(name, null) match {
          case One(InitDeclaratorI(declarator, _, _)) => addToDeclUseMap(declarator.getId, i)
          case One(AtomicNamedDeclarator(_, key, _)) => addToDeclUseMap(key, i)
          case One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)) => addToDeclUseMap(key, i)
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
          case One(Enumerator(key, _)) => addToDeclUseMap(key, i)
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

  private def addChoice(choice: Choice[AST], use: Id) {
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
      case Choice(_, o1@One(_), o2@One(_)) =>
        addOne(o1, use)
        addOne(o2, use)
      case Choice(_, o@One(_), c@Choice(_, _, _)) =>
        addOne(o, use)
        addChoice(c, use)
      case Choice(_, c1@Choice(_, _, _), c2@Choice(_, _, _)) =>
        addChoice(c1, use)
        addChoice(c2, use)
      case Choice(_, c@Choice(_, _, _), o@One(_)) =>
        addOne(o, use)
        addChoice(c, use)
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

  def addSpecifiers(entry: AST, env: Env) {
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
      case StructOrUnionSpecifier(union, i, _) => addStructUse(i.get, env, i.get.name, union)
      case k =>
    }
  }

  def addUse(entry: AST, env: Env) {
    entry match {
      case ConditionalExpr(expr, thenExpr, elseExpr) =>
        addUse(expr, env)
        thenExpr.foreach(x => addUse(x, env))
        addUse(elseExpr, env)
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
      case FunctionCall(param) => param.exprs.foreach(x => addUse(x.entry, env))
      case ExprList(exprs) => exprs.foreach(x => addUse(x.entry, env))
      case LcurlyInitializer(inits) =>
        inits.foreach(x => addUse(x.entry, env))
      case InitializerAssigment(designators) =>
        designators.foreach(x => addUse(x.entry, env))
      case InitializerDesignatorD(i: Id) =>
        addUse(i, env)
      case Initializer(Some(x), expr) =>
        addUse(x, env)
        addUse(expr, env)
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
          case One(Enumerator(key, _)) => addToDeclUseMap(key, i)
          case One(NestedNamedDeclarator(_, nestedDecl, _)) => addToDeclUseMap(nestedDecl.getId, i)
          case c@Choice(_, _, _) => addChoice(c, i)
          case One(null) =>
          // println("addUse varEnv.getAstOrElse is One(null) from " + i + " @ " + i.getPositionFrom)
          case k =>
          // println("AddUse Id not exhaustive: " + i + "\nElement " + k)
        }
      case PointerDerefExpr(i) => addUse(i, env)
      case AssignExpr(target, operation, source) =>
        addUse(source, env)
        addUse(target, env)
      case NAryExpr(i, o) =>
        addUse(i, env)
        o.foreach(x => addUse(x.entry, env))
      case NArySubExpr(_, e) =>
        addUse(e, env)
      case PostfixExpr(p, s) =>
        addUse(p, env)
        addUse(s, env)
      case PointerPostfixSuffix(_, id) => if (!env.varEnv.getAstOrElse(id.name, null).equals(One(null))) addUse(id, env)
      case PointerCreationExpr(expr) => addUse(expr, env)
      case CompoundStatement(innerStatements) => innerStatements.foreach(x => addUse(x.entry, env))
      case Constant(_) =>
      case SizeOfExprT(expr) => addUse(expr, env)
      case SizeOfExprU(expr) => addUse(expr, env)
      case TypeName(specs, decl) =>
        specs.foreach(x => addUse(x.entry, env))
        addDecl(decl, env)
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
                    inits.foreach(j => addUse(j.entry, env))
                    decls.foreach(z => z.entry match {
                      case StructDeclarator(a: AtomicNamedDeclarator, _, _) =>
                        if (!declUseMap.containsKey(a.getId)) {
                          putToDeclUseMap(a.getId)
                        }
                        stringToIdMap += (a.getName -> a.getId)
                      case k => addUse(k, env)
                    })
                })
              case k => addUse(k, env)
            })
        }
        lst.foreach(x => x.entry match {
          case Initializer(Some(InitializerAssigment(lst2)), expr) =>
            addUse(expr, env)
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
                  case c@Choice(_, _, _) => addChoice(c, i)
                  case One(null) =>
                    if (stringToIdMap.containsKey(i.name) && declUseMap.containsKey(stringToIdMap.get(i.name).get)) {
                      addToDeclUseMap(stringToIdMap.get(i.name).get, i)
                    }
                  case k => // println("AddUse Id not exhaustive: " + i + "\nElement " + k)
                }
            })
          case k => addUse(k, env)
        })
        stringToIdMap = stringToIdMap.empty

      case CastExpr(typ, expr) =>
        addUse(typ, env)
        addUse(expr, env)
      case ArrayAccess(expr) => addUse(expr, env)
      case UnaryExpr(_, expr) =>
        addUse(expr, env)
      case UnaryOpExpr(_, expr) => addUse(expr, env)
      case TypeDefTypeSpecifier(id) => addTypeUse(id, env)
      case Initializer(_, expr) => addUse(expr, env)
      case CompoundStatementExpr(expr) => addUse(expr, env)
      case StructOrUnionSpecifier(union, Some(i: Id), _) =>
        addStructUse(i, env, i.name, union)
      case BuiltinOffsetof(typeName, members) =>
        typeName.specifiers.foreach(x => addUse(x.entry, env))
        /**
         * Workaround for buitlin_offset_ -> typechef implementation too much - see: http://gcc.gnu.org/onlinedocs/gcc/Offsetof.html
         */
        val structOrUnion = filterASTElemts[Id](typeName)
        members.foreach(x => addStructUse(x.entry, env, structOrUnion.head.name, !env.structEnv.someDefinition(structOrUnion.head.name, false)))
      case k =>
      /*if (!k.isInstanceOf[Specifier]) {
     println(" Completly missing add use: " + k + " " + k.getPositionFrom)
    }  */
    }
  }

  def addStructUse(entry: AST, env: Env, structName: String, isUnion: Boolean) {
    entry match {
      case i@Id(name) => {
        if (env.structEnv.someDefinition(structName, isUnion)) {
          env.structEnv.getFieldsMerged(structName, isUnion).getAstOrElse(i.name, null) match {
            case One(null) =>
              addStructDeclUse(i, env, isUnion)
            case One(AtomicNamedDeclarator(_, i2: Id, _)) => addToDeclUseMap(i2, i)
            case One(i2: Id) =>
              addToDeclUseMap(i2, i)
            case c@Choice(_, _, _) => addStructUseChoice(c, i)
            case One(NestedNamedDeclarator(_, AtomicNamedDeclarator(_, i2: Id, _), _)) => addToDeclUseMap(i2, i)
            case k => println("Missed addStructUse " + env.varEnv.getAstOrElse(i.name, null))
          }
        } else {
          env.typedefEnv.getAstOrElse(i.name, null) match {
            case One(i2: Id) => addToDeclUseMap(i2, i)
            case One(null) => addDef(i, env)
            case c@Choice(_, _, _) => println("missed choice typedef " + c)
            case k => println("Missed addStructUse")
          }
        }
      }
      case OffsetofMemberDesignatorID(id) => addStructUse(id, env, structName, isUnion)
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

  def addStructDeclUse(entry: Id, env: Env, isUnion: Boolean) {
    entry match {
      case i@Id(name) => {
        if (env.structEnv.someDefinition(name, isUnion)) {
          env.structEnv.getId(name, isUnion) match {
            case Some(key: Id) =>
              addToDeclUseMap(key, i)
            case _ =>
          }
        }
      }
      case _ =>
    }
  }

  def addStructDeclaration(entry: Id) = {
    putToDeclUseMap(entry)
  }

  def addDecl(current: Any, env: Env) {
    current match {
      case Nil =>
      case None =>
      case DeclarationStatement(_) =>
      case Declaration(decl, init) =>
        decl.foreach(x => addDecl(x.entry, env))
        init.foreach(x => addDecl(x.entry, env))
      case Opt(_, e) => addDecl(e, env)
      case i@InitDeclaratorI(decl, attr, opt) =>
        addDecl(decl, env)
        attr.foreach(x => addDecl(x.entry, env))
        opt match {
          case None =>
          case Some(init@Initializer(_, l: LcurlyInitializer)) =>
            addLcurlyInitializer(i.getId, init, env)
          case _ => // addUse(opt.get, env)
        }
      case Initializer(label, element) =>
        addDecl(element, env)
      case AtomicNamedDeclarator(pointers, id, extension) =>
        pointers.foreach(x => addDecl(x, env))
        extension.foreach(x => addDecl(x, env))
        addDef(id, env)
      case i: Id =>
        addDef(i, env)
      case DeclParameterDeclList(decl) =>
        decl.foreach(x => addDecl(x.entry, env))
      case ParameterDeclarationD(specs, decl) =>
        specs.foreach(x => addDecl(x.entry, env))
        addDecl(decl, env)
      case Pointer(specs) =>
        specs.foreach(x => addDecl(x, env))
      case EnumSpecifier(id, None) =>
        addDecl(id, env)
      case EnumSpecifier(id, Some(o)) =>
        addDecl(id, env)
        for (e <- o) {
          addDecl(e.entry, env)
        }
      case i@IfStatement(cond, then, elif, els) =>
      case EnumSpecifier(_, _) =>
      case PlainParameterDeclaration(spec) => spec.foreach(x => addDecl(x.entry, env))
      case ParameterDeclarationAD(spec, decl) =>
        spec.foreach(x => addDecl(x.entry, env))
        addDecl(decl, env)
      case Enumerator(i@Id(name), Some(o)) =>
        addDecl(i, env)
        o match {
          case i: Id =>
            addUse(i, env)
          case k => addDecl(k, env)
        }
      case Enumerator(i@Id(name), _) =>
        addDecl(i, env)
      case BuiltinOffsetof(typeName, members) =>
        typeName.specifiers.foreach(x => addDecl(x.entry, env))
        members.foreach(x => addDecl(x.entry, env))
      case OffsetofMemberDesignatorID(i) =>
        addDecl(i, env)
      case TypeDefTypeSpecifier(name) =>
        addTypeUse(name, env)
      case DeclArrayAccess(Some(o)) =>
        addUse(o, env)
      case ReturnStatement(expr) =>
      case AssignExpr(target, operation, source) =>
        addUse(source, env)
        addUse(target, env)
      case UnaryOpExpr(_, expr) =>
        addDecl(expr, env)
      case DoStatement(expr, cond) =>
        addDecl(expr, env)
        addDecl(cond, env)
      case StructOrUnionSpecifier(isUnion, Some(i@Id(name)), None) =>
        addStructUse(i, env, name, isUnion)
      case StructOrUnionSpecifier(isUnion, Some(i@Id(name)), Some(extensions)) =>
        if (!declUseMap.contains(i)) {
          putToDeclUseMap(i)
        }
        extensions.foreach(x => addDecl(x, env))
      case StructOrUnionSpecifier(_, None, Some(extensions)) =>
        extensions.foreach(x => addDecl(x, env))
      case StructDeclaration(qualifiers, declarotors) =>
        qualifiers.foreach(x => addDecl(x.entry, env))
        declarotors.foreach(x => addDecl(x.entry, env))
      case StructDeclarator(decl, i: Id, _) =>
        // addDecl(decl, env)
        addDef(i, env)
      case ExprStatement(expr) =>
      //addDecl(expr, env)
      case pe@PostfixExpr(expr, suffix) =>
        addUse(expr, env)
        addDecl(suffix, env)
      case pps@PointerPostfixSuffix(_, id: Id) =>
        addUse(id, env)
      case f@FunctionCall(expr) =>
      case StructDeclarator(decl, _, _) =>
        addDecl(decl, env)
      case StructOrUnionSpecifier(_, Some(o), None) =>
        addDecl(o, env)
      case NestedNamedDeclarator(pointers, nestedDecl, extension) =>
        pointers.foreach(x => addDecl(x, env))
        extension.foreach(x => addDecl(x, env))
        addDecl(nestedDecl, env)
      case One(o) => addDecl(o, env)
      case Some(o) => addDecl(o, env)
      case NAryExpr(expr, others) =>
        expr match {
          case Id(_) => addUse(expr, env)
          case k => addUse(k, env)
        }
        others.foreach(x => addDecl(x.entry, env))
      case NArySubExpr(_, expr) =>
        addDecl(expr, env)
      case CastExpr(typ, expr) =>
        addDecl(expr, env)
        typ.specifiers.foreach(x => addDecl(x, env))
      case SizeOfExprT(TypeName(spec, decl)) =>
        spec.foreach(x => addDecl(x.entry, env))
      // addDecl(decl, env)
      case ConditionalExpr(expr, thenExpr, elseExpr) =>
        addDecl(expr, env)
        thenExpr.foreach(x => addDecl(x, env))
        addDecl(elseExpr, env)
      case PointerCreationExpr(expr) =>
        addDecl(expr, env)
      case Constant(_) =>
      case CompoundStatement(statement) => statement.foreach(x => addDecl(x.entry, env))
      case PointerDerefExpr(expr) => // addUse(expr, env)
      case WhileStatement(expr, cond) =>
        addDecl(expr, env)
        cond.toOptList.foreach(x => addDecl(x.entry, env))
      case ArrayAccess(expr) => addDecl(expr, env)
      case Choice(ft, then, els) =>
        addDecl(then, env)
        addDecl(els, env)
      case k =>
        if (!k.isInstanceOf[BreakStatement] && !k.isInstanceOf[ContinueStatement] && !k.isInstanceOf[SimplePostfixSuffix] && !k.isInstanceOf[Specifier] && !k.isInstanceOf[DeclArrayAccess] && !k.isInstanceOf[VarArgs] && !k.isInstanceOf[AtomicAbstractDeclarator] && !k.isInstanceOf[StructInitializer] && !k.isInstanceOf[StringLit]) {
          // println("Missing Case: " + k)
        }
    }
  }

  private def addLcurlyInitializer(id: Id, init: Initializer, env: Env) {
    init match {
      case Initializer(_, LcurlyInitializer(lst)) =>
        lst.foreach(x => x.entry match {
          case Initializer(Some(InitializerAssigment(lst2)), _) =>
            lst2.foreach(y => y.entry match {
              case idd@InitializerDesignatorD(i) =>
                addStructUse(i, env, id.name, false)
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