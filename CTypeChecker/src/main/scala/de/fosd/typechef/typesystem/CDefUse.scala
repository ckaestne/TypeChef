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
trait CDefUse extends CEnv {

  private val defuse: IdentityHashMap[Id, IdentityHashMap[Id, Id]] = new IdentityHashMap()

  private[typesystem] def clear() {
    defuse.clear()
  }

  private def putToDefUseMap(id: Id) = {
    if (!defuse.contains(id)) {
      defuse.put(id, new IdentityHashMap())
    }
  }

  private def defUseContainsId(key: Id, target: Id): Boolean = {
    if (!defuse.contains(target)) {
      defuse.get(key).contains(target)
    }
    false
  }

  private def addToDefUseMap(key: Id, target: Id): Any = {
    if (key.eq(target) && !defuse.containsKey(key)) {
      putToDefUseMap(key)
    }
    if (defuse.containsKey(key)) {
      defuse.get(key).put(target, null)
      //defuse.put(key, defuse.get(key) ++ List(target))
    } else {
      def lookupDecl(): Id = {
        defuse.keySet().toArray.foreach(k => {
          for (v <- defuse.get(k))
            if (v.eq(key))
              return k.asInstanceOf[Id]
        })
        null.asInstanceOf[Id]
      }
      val decl = lookupDecl()
      if (decl == null) {
        putToDefUseMap(key)
        addToDefUseMap(key, target)
      } else {
        // println("Current Defusemap: " + defuse)
        addToDefUseMap(decl, target)
      }
    }
  }

  def clearDefUseMap() {
    defuse.clear()
  }

  def getDefUseMap(): IdentityHashMap[Id, List[Id]] = {
    val defuseMap = new util.IdentityHashMap[Id, List[Id]]()
    defuse.keySet().foreach(x => {
      val list = defuse.get(x).keySet().toArray(Array[Id]()).toList
      defuseMap.put(x, list)
    })
    defuseMap
  }

  def getDefUseMap2 = defuse

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
          case null => putToDefUseMap(declarator.getId)
          case One(null) => putToDefUseMap(declarator.getId)
          case One(i: InitDeclarator) => addUse(id, env)
          case c@Choice(_, _, _) => addFunctionChoiceDef(c, declarator, env)
          case k => // println("Missing AddDef " + id + "\nentry " + k + "\nfuncdef " + func + "\n" + defuse.containsKey(declarator.getId))
        }

        // check function definiton for goto statements and add them to defUse
        addGotoStatements(f)
        // add the function parameters to defuse
        addFunctionParametersToDefUse(ext, env)
      }
      case i: InitDeclarator => putToDefUseMap(i.getId)
      case id: Id =>
        env.varEnv.getAstOrElse(id.name, null) match {
          case null => putToDefUseMap(id)
          case One(null) => putToDefUseMap(id)
          case One(i: InitDeclarator) => putToDefUseMap(id)
          case One(e: Enumerator) => putToDefUseMap(id) // TODO ENUM Verification
          case c@Choice(_, _, _) => addDefChoice(c)

          case k => // println("Oh i forgot " + k)
        }
      // TODO Check with new StructEnv!
      case st: StructDeclaration =>
        st.declaratorList.foreach(x => x.entry match {
          case StructDeclarator(AtomicNamedDeclarator(_, id: Id, _), _, _) =>
            env.varEnv.getAstOrElse(id.name, null) match {
              case null => // putToDefUseMap(id)
              case One(null) => // putToDefUseMap(id)
              case One(i: InitDeclarator) => {
                val key = i.getId
                // TODO: AddUse?
                putToDefUseMap(key)
              }
              case _ => // println("match error " + env.varEnv.getAstOrElse(id.name, null))
            }
          case StructDeclarator(NestedNamedDeclarator(pointers, nestedDecl, _), _, _) =>
            pointers.foreach(x => addDecl(x, env))
            putToDefUseMap(nestedDecl.getId)
          // defuse.put(nestedDecl.getId, List())
          case k => // println("Pattern StructDeclaration fail: " + k)
        })
      case and@AtomicNamedDeclarator(_, id: Id, _) =>
        env.varEnv.getAstOrElse(id.name, null) match {
          case null => putToDefUseMap(id)
          case One(null) => putToDefUseMap(id)
          case One(i: InitDeclarator) => {
            val key = i.getId
            // TODO AddUse?
            putToDefUseMap(key)
          }
        }
      case Declaration(specs, inits) => inits.foreach(x => putToDefUseMap(x.entry.getId))
      case k => println("Missing Add Def: " + f + " from " + k)
    }
  }

  private def addFunctionChoiceDef(c: Choice[AST], decl: Declarator, env: Env) {
    c match {
      case Choice(_, One(FunctionDef(_, _, _, _)), One(null)) => putToDefUseMap(decl.getId)
      case Choice(_, One(InitDeclaratorI(AtomicNamedDeclarator(_, id2: Id, _), _, _)), _) => addUse(decl.getId, env)
      case Choice(_, One(FunctionDef(_, _, _, _)), One(InitDeclaratorI(AtomicNamedDeclarator(_, id2: Id, _), _, _))) =>
        putToDefUseMap(decl.getId)
        addUse(id2, env)
      case _ => // println("Missed FunctionDef Choice: " + c)
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
              putToDefUseMap(paramID)
            case One(null) =>
              putToDefUseMap(paramID)
            case One(i: InitDeclarator) => addUse(paramID, env)
            case m => // println("pdl missed" + m)
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
        case e => // println("err " + e)
      })
      case mi => // println("Completly missing: " + mi)
    })
  }

  def addTypeUse(entry: AST, env: Env) {
    entry match {
      case i@Id(name) =>
        env.typedefEnv.getAstOrElse(name, null) match {
          case One(InitDeclaratorI(declarator, _, _)) => addToDefUseMap(declarator.getId, i)
          case One(AtomicNamedDeclarator(_, key, _)) => addToDefUseMap(key, i)
          case One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)) => addToDefUseMap(key, i)
          case Choice(feature, One(InitDeclaratorI(declarator, _, _)), One(InitDeclaratorI(declarator2, _, _))) =>
            addToDefUseMap(declarator.getId, i)
            addToDefUseMap(declarator2.getId, i)
          case Choice(feature, One(InitDeclaratorI(declarator, _, _)), _) =>
            addToDefUseMap(declarator.getId, i)
          case Choice(feature, One(AtomicNamedDeclarator(_, key, _)), One(AtomicNamedDeclarator(_, key2, _))) =>
            addToDefUseMap(key, i)
            addToDefUseMap(key2, i)
          case Choice(feature, One(AtomicNamedDeclarator(_, key, _)), _) =>
            addToDefUseMap(key, i)
          case c@Choice(feature, One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)), One(FunctionDef(_, AtomicNamedDeclarator(_, key2, _), _, _))) =>
            addToDefUseMap(key, i)
            addToDefUseMap(key2, i)
          case Choice(feature, One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)), _) =>
            addToDefUseMap(key, i)
          case Choice(feature, One(Declaration(specs, init)), _) =>
            init.foreach(x => x.entry match {
              case InitDeclaratorI(AtomicNamedDeclarator(_, key, _), _, _) =>
                addToDefUseMap(key, i)
              case k => // println("AddTypeUse Choice not exhaustive: " + k)
            })
          case One(Enumerator(key, _)) => addToDefUseMap(key, i)
          case One(Declaration(specifiers, inits)) =>
            inits.foreach(x => x match {
              case Opt(ft, InitDeclaratorI(AtomicNamedDeclarator(_, key: Id, _), _, _)) =>
                addToDefUseMap(key, i)
              case Opt(ft, InitDeclaratorI(NestedNamedDeclarator(_, AtomicNamedDeclarator(_, key, _), _), _, _)) =>
                addToDefUseMap(key, i)
              case k => // println("Fehlt: " + k)
            })
          case k =>
            if (name.startsWith("__builtin")) {
              defuse.put(i, new util.IdentityHashMap())
            } else {
              // println("Missing: " + i + "\nElement " + k)
            }
        }
    }
  }


  private def addChoice(entry: Choice[AST], id: Id) {
    entry match {
      case Choice(feature, c1@Choice(_, _, _), c2@Choice(_, _, _)) =>
        addChoice(c1, id)
        addChoice(c2, id)
      case Choice(feature, One(InitDeclaratorI(declarator, _, _)), c@Choice(_, _, _)) =>
        addChoice(c, id)
        addToDefUseMap(declarator.getId, id)
      case Choice(feature, One(InitDeclaratorI(declarator, _, _)), One(InitDeclaratorI(declarator2, _, _))) =>
        addToDefUseMap(declarator.getId, id)
        addToDefUseMap(declarator2.getId, id)
      case Choice(feature, One(InitDeclaratorI(declarator, _, _)), One(null)) => addToDefUseMap(declarator.getId, id)
      case Choice(feature, One(AtomicNamedDeclarator(_, key, _)), c@Choice(_, _, _)) =>
        addChoice(c, id)
        addToDefUseMap(key, id)
      case Choice(feature, One(AtomicNamedDeclarator(_, key, _)), One(AtomicNamedDeclarator(_, key2, _))) =>
        addToDefUseMap(key, id)
        addToDefUseMap(key2, id)
      case Choice(feature, One(AtomicNamedDeclarator(_, key, _)), One(null)) => addToDefUseMap(key, id)
      case Choice(feature, One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)), c@Choice(_, _, _)) =>
        addChoice(c, id)
        addToDefUseMap(key, id)
      case c@Choice(feature, One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)), One(FunctionDef(_, AtomicNamedDeclarator(_, key2, _), _, _))) =>
        addToDefUseMap(key, id)
        addToDefUseMap(key2, id)
      case c@Choice(feature, One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)), One(InitDeclaratorI(declarator, _, _))) =>
        addToDefUseMap(key, id)
        addToDefUseMap(declarator.getId, id)
      case Choice(feature, One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)), One(null)) => addToDefUseMap(key, id)
      case Choice(feature, One(Enumerator(key, _)), c@Choice(_, _, _)) =>
        addChoice(c, id)
        addToDefUseMap(key, id)
      case Choice(feature, One(Enumerator(key, _)), One(Enumerator(key2, _))) =>
        addToDefUseMap(key, id)
        if (!key.eq(key2)) {
          addToDefUseMap(key2, id)
        }
      case Choice(feature, One(Enumerator(key, _)), One(null)) => addToDefUseMap(key, id)
      case Choice(feature, One(InitDeclaratorI(declarator, _, _)), One(Enumerator(key, _))) =>
        addToDefUseMap(key, id)
        addToDefUseMap(declarator.getId, id)
      case _ => println("Missed Choice " + entry)
    }
  }

  private def addDefChoice(entry: Choice[AST]) {
    def addOne(entry: One[AST]) {
      entry match {
        case One(InitDeclaratorI(declarator, _, _)) =>
          putToDefUseMap(declarator.getId)
        case One(AtomicNamedDeclarator(_, key, _)) =>
          putToDefUseMap(key)
        case One(Enumerator(key, _)) =>
          putToDefUseMap(key)
        case One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)) =>
          putToDefUseMap(key)
        case One(null) =>
        case k =>
          println("Missed add One " + k)
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

  def addUseWrapper(entry: AST, env: Env) {
    addUse(entry, env)
  }

  def addUse(entry: AST, env: Env) {
    entry match {
      case ConditionalExpr(expr, thenExpr, elseExpr) =>
        addUse(expr, env)
        thenExpr.foreach(x => addUse(x, env))
        addUse(elseExpr, env)
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
            /*
             Special handling for forward declarations because the env gives us the function
             and not the first forward declaration when calling the function.
              */
            defuse.remove(declarator.getId)
            putToDefUseMap(i)
            if (!i.eq(declarator.getId)) {
              addToDefUseMap(i, declarator.getId)
            }
          case One(AtomicNamedDeclarator(_, key, _)) => addToDefUseMap(key, i)
          case One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)) => addToDefUseMap(key, i)
          case One(Enumerator(key, _)) => addToDefUseMap(key, i)
          case One(NestedNamedDeclarator(_, nestedDecl, _)) => addToDefUseMap(nestedDecl.getId, i)
          case c@Choice(_, _, _) => addChoice(c, i)
          case One(null) => // println("addUse varEnv.getAstOrElse is One(null) from " + i + " @ " + i.getPositionFrom)
          case k => // println("AddUse Id not exhaustive: " + i + "\nElement " + k)
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
      case PointerPostfixSuffix(_, id) =>
        // stop if id is not yet in env
        // continue if in env
        if (!env.varEnv.getAstOrElse(id.name, null).equals(One(null))) {
          addUse(id, env)
        }
      case PointerCreationExpr(expr) => addUse(expr, env)
      case CompoundStatement(innerStatements) => innerStatements.foreach(x => addUse(x.entry, env))
      case Constant(_) =>
      case SizeOfExprT(expr) => addUse(expr, env)
      case SizeOfExprU(expr) => addUse(expr, env)
      case TypeName(specs, decl) =>
        specs.foreach(x => addDecl(x.entry, env))
        addDecl(decl, env)
      case StringLit(_) =>
      case SimplePostfixSuffix(_) =>
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
      case BuiltinOffsetof(typeName, members) =>
        typeName.specifiers.foreach(x => addUse(x.entry, env))
        /**
         * Workaround for buitlin_offset_ -> typechef implementation too much - see: http://gcc.gnu.org/onlinedocs/gcc/Offsetof.html
         */
        val structOrUnion = filterASTElemts[Id](typeName)
        // addStructUse(entry, env, structOrUnion.head.name, !env.structEnv.someDefinition(structOrUnion.head.name, false))
        members.foreach(x => addStructUse(x.entry, env, structOrUnion.head.name, !env.structEnv.someDefinition(structOrUnion.head.name, false)))
      case k =>
        if (!k.isInstanceOf[Specifier]) {
          // println(" Completly missing add use: " + k + " " + k.getPositionFrom)
        }
    }
  }

  def addStructUse(entry: AST, env: Env, structName: String, isUnion: Boolean) {
    entry match {
      case i@Id(name) => {
        if (env.structEnv.someDefinition(structName, isUnion)) {
          env.structEnv.getFieldsMerged(structName, isUnion).getAstOrElse(i.name, i) match {
            case One(AtomicNamedDeclarator(_, i2: Id, _)) =>
              addToDefUseMap(i2, i)
            case One(i2: Id) =>
              addToDefUseMap(i2, i)
            case Choice(_, One(AtomicNamedDeclarator(_, id2: Id, _)), One(id3: Id)) =>
              addToDefUseMap(id2, i)
            // addToDefUseMap(id3, i)
            case One(NestedNamedDeclarator(_, AtomicNamedDeclarator(_, i2: Id, _), _)) =>
              addToDefUseMap(i2, i)
            case k => // println("omg this should not have happend " + k)
          }
        } else {
          env.typedefEnv.getAstOrElse(i.name, null) match {
            case One(i2: Id) => addToDefUseMap(i2, i)
            case One(null) =>
              addDef(i, env)
            case k => // println("Error struct " + structName + " entry " + entry + " typedefEnv: " + k)
          }
        }
      }
      case OffsetofMemberDesignatorID(id) => addStructUse(id, env, structName, isUnion)
      case k => // println("Missing Add Struct: " + k)
    }
  }

  def addAnonStructUse(id: Id, fields: ConditionalTypeMap) {
    fields.getAstOrElse(id.name, null) match {
      case Choice(_, One(AtomicNamedDeclarator(_, key, _)), One(null)) => addToDefUseMap(key, id)
      case One(AtomicNamedDeclarator(_, key, _)) => addToDefUseMap(key, id)
      case k => println("Should not have entered here: " + id + "\n" + k)
    }
  }

  def addStructDeclaration(entry: Id) = {
    putToDefUseMap(entry)
  }

  def addStructDecl(entry: AST, env: Env) = {
    entry match {
      case i@Id(name) =>
        defuse.keySet().toArray().foreach(x => if (x.asInstanceOf[Id].name.equals(name)) {
          addToDefUseMap(x.asInstanceOf[Id], i)
        })
      case k => // println("AddStructDecl fail: " + k)
    }
  }

  def addDecl(current: Any, env: Env) {
    current match {
      case Nil =>
      case None =>
      case DeclarationStatement(_) =>
      case Declaration(decl, init) =>
        decl.foreach(x => addDecl(x, env))
        init.foreach(x => addDecl(x, env))
      case Opt(_, e) => addDecl(e, env)
      case InitDeclaratorI(decl, attr, opt) =>
        addDecl(decl, env)
        attr.foreach(x => addDecl(x.entry, env))
        opt match {
          case None =>
          case _ => // addUse(opt.get, env)
        }
      case Initializer(label, element) =>
        addDecl(element, env)
      case AtomicNamedDeclarator(pointers, id, extension) =>
        pointers.foreach(x => addDecl(x, env))
        extension.foreach(x => addDecl(x, env))
        addDecl(id, env)
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
      /*
  addDecl(cond, env)
  then.toOptList.foreach(x => addDecl(x.entry, env))
  elif.foreach(x => addDecl(x.entry.condition, env))
  elif.foreach(x => x.entry.thenBranch.toOptList.foreach(x => addDecl(x, env)))
  els.foreach(x => addDecl(x, env)) */
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
        addDecl(o, env)
      case ReturnStatement(expr) =>
      /*
     if (!expr.isEmpty) {
       addDecl(expr.get, env)
     } */
      case AssignExpr(target, operation, source) =>
        addUse(source, env)
        addUse(target, env)
      case UnaryOpExpr(_, expr) =>
        addDecl(expr, env)
      case DoStatement(expr, cond) =>
        addDecl(expr, env)
        addDecl(cond, env)
      case StructOrUnionSpecifier(isUnion, Some(i@Id(name)), None) =>
        if (!defuse.contains(i)) {
          putToDefUseMap(i)
        }
      case StructOrUnionSpecifier(isUnion, Some(i@Id(name)), Some(extensions)) =>
        if (!defuse.contains(i)) {
          putToDefUseMap(i)
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
      case pps@PointerPostfixSuffix(_, id: Id) => addUse(id, env)
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

  private def addGotoStatements(f: AST) {
    // TODO Verify -> #ifdef gotos und verschachtelte gotos <- Überdeckung
    val labels = filterASTElemts[LabelStatement](f)
    val gotos = filterASTElemts[GotoStatement](f)

    for (x <- labels) {
      val id = x.id
      putToDefUseMap(id)
      gotos.foreach(y => y match {
        case GotoStatement(id2) =>
          if (id.equals(id2)) {
            addToDefUseMap(id, id2.asInstanceOf[Id])
          }
        case _ =>
      })
    }
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