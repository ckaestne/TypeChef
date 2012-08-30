package de.fosd.typechef.typesystem

import java.util.IdentityHashMap
import de.fosd.typechef.parser.c._
import de.fosd.typechef.parser.c.PostfixExpr
import de.fosd.typechef.parser.c.PlainParameterDeclaration
import de.fosd.typechef.parser.c.Id
import de.fosd.typechef.parser.c.Constant
import de.fosd.typechef.parser.c.PointerDerefExpr
import de.fosd.typechef.parser.c.Enumerator
import de.fosd.typechef.parser.c.AtomicNamedDeclarator
import de.fosd.typechef.parser.c.StructDeclaration
import de.fosd.typechef.parser.c.EnumSpecifier
import scala.Some
import de.fosd.typechef.parser.c.FunctionDef
import de.fosd.typechef.parser.c.NAryExpr
import de.fosd.typechef.parser.c.TypeDefTypeSpecifier
import de.fosd.typechef.parser.c.StructOrUnionSpecifier
import de.fosd.typechef.parser.c.PointerPostfixSuffix
import de.fosd.typechef.parser.c.OffsetofMemberDesignatorID
import de.fosd.typechef.parser.c.ParameterDeclarationD
import de.fosd.typechef.conditional.Choice
import de.fosd.typechef.parser.c.TypeName
import de.fosd.typechef.parser.c.CastExpr
import de.fosd.typechef.parser.c.ConditionalExpr
import de.fosd.typechef.conditional.Opt
import de.fosd.typechef.conditional.One
import de.fosd.typechef.parser.c.DeclArrayAccess
import de.fosd.typechef.parser.c.NestedNamedDeclarator
import de.fosd.typechef.parser.c.IfStatement
import de.fosd.typechef.parser.c.BuiltinOffsetof
import de.fosd.typechef.parser.c.DeclParameterDeclList
import de.fosd.typechef.parser.c.NArySubExpr
import de.fosd.typechef.parser.c.ParameterDeclarationAD
import de.fosd.typechef.parser.c.Pointer
import de.fosd.typechef.parser.c.InitDeclaratorI
import de.fosd.typechef.parser.c.SizeOfExprT
import de.fosd.typechef.parser.c.Declaration
import de.fosd.typechef.parser.c.StructDeclarator
import scala.collection.JavaConversions._


// store def use chains
// we store Id elements of AST structures that represent a definition (key element of defuse)
// and a use (value element of defuse)
//
// the creation of defuse chains relies on the typesystem and it's data that is stored
// in Env instances; during the traversal of the typesystem visitor Env instances get filled
// with information about names, AST entries and their corresponding types
trait CDefUse extends CEnv {

  private val defuse: IdentityHashMap[Id, List[Id]] = new IdentityHashMap()

  private[typesystem] def clear() {
    defuse.clear()
  }

  private def putToDefUseMap(id: Id) = {
    if (!defUseContainsId(id)) {
      defuse.put(id, List())
    }
  }

  private def defUseContainsId(id: Id): Boolean = {
    if (defuse.containsKey(id)) {
      return true
    }
    return defUseContainsIdAsValue(id)
  }

  private def defUseContainsIdName(name: String): Boolean = {
    if (defuse.keySet().exists(x => x.name.equals(name))) {
      return true
    }
    return false
  }

  private def getKeyByName(name: String): Id = {
    defuse.keySet().foreach(x =>
      if (x.name.equals(name)) {
        return x
      })
    return null
  }

  private def defUseContainsIdAsValue(id: Id): Boolean = {
    defuse.values().foreach(x => {
      x.foreach(entry => {
        if (entry.eq(id)) {
          return true
        }
      })
    })
    return false
  }

  private def addToDefUseMap(key: Id, target: Id): Any = {
    if (defuse.containsKey(key)) {
      if (defUseContainsId(target)) {
        return
      }
      defuse.put(key, defuse.get(key) ++ List(target))
    } else {
      var fd: Id = null
      for (k <- defuse.keySet().toArray) {
        for (v <- defuse.get(k))
          if (v.eq(key)) fd = k.asInstanceOf[Id]
      }
      if (fd == null) {
        putToDefUseMap(key)
        addToDefUseMap(key, target)
      } else {
        addToDefUseMap(fd, target)
      }
    }
  }

  def clearDefUseMap() {
    defuse.clear()
  }

  def getDefUseMap = defuse

  // add definition:
  //   - function: function declarations (forward declarations) and function definitions are handled
  //               if a function declaration exists, we add it as def and the function definition as its use
  //               if no function declaration exists, we add the function definition as def
  def addDef(f: AST, env: Env) {
    f match {
      case func@FunctionDef(specifiers, declarator, oldStyleParameters, _) => {
        // lookup whether a prior function declaration exists
        // if so we get an InitDeclarator instance back
        val id = declarator.getId
        val ext = declarator.extensions
        env.varEnv.getAstOrElse(id.name, null) match {
          case null => putToDefUseMap(declarator.getId)
          case One(null) => putToDefUseMap(declarator.getId)
          case One(i: InitDeclarator) => addUse(id, env)
          case Choice(_, One(FunctionDef(_, _, _, _)), One(null)) => putToDefUseMap(declarator.getId)
          case Choice(_, One(InitDeclaratorI(AtomicNamedDeclarator(_, id2: Id, _), _, _)), _) => addUse(id2, env)
          case k => println("Missing AddDef " + id + "\nentry " + k + "\nfuncdef " + func + "\n" + defuse.containsKey(declarator.getId))
        }
        // Parameter Declaration
        ext.foreach(x => x match {
          case null =>
          case Opt(_, d: DeclParameterDeclList) => d.parameterDecls.foreach(pdL => pdL match {
            case Opt(_, pd: ParameterDeclarationD) => {
              val paramID = pd.decl.getId
              env.varEnv.getAstOrElse(paramID.name, null) match {
                case null => putToDefUseMap(paramID)
                case One(null) => putToDefUseMap(paramID)
                case One(i: InitDeclarator) => addUse(id, env)
              }
            }
            case _ =>
          })
          case mi => println("Completly missing: " + mi)
        })
      }
      case i: InitDeclarator => putToDefUseMap(i.getId)
      case id: Id =>
        env.varEnv.getAstOrElse(id.name, null) match {
          case null => putToDefUseMap(id)
          case One(null) => putToDefUseMap(id)
          case One(i: InitDeclarator) => {
            // TODO: Verify
            putToDefUseMap(id)
          }
          case One(e: Enumerator) => putToDefUseMap(id) // TODO ENUM Verification
          case Choice(feature, One(InitDeclaratorI(declarator, _, _)), One(InitDeclaratorI(declarator2, _, _))) =>
            putToDefUseMap(declarator.getId)
            putToDefUseMap(declarator2.getId)
          case Choice(feature, One(InitDeclaratorI(declarator, _, _)), _) =>
            putToDefUseMap(declarator.getId)
          case Choice(feature, One(AtomicNamedDeclarator(_, key, _)), One(AtomicNamedDeclarator(_, key2, _))) =>
            putToDefUseMap(key)
            putToDefUseMap(key2)
          case Choice(feature, One(AtomicNamedDeclarator(_, key, _)), _) =>
            putToDefUseMap(key)
          case Choice(feature, One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)), One(FunctionDef(_, AtomicNamedDeclarator(_, key2, _), _, _))) =>
            putToDefUseMap(key)
            putToDefUseMap(key2)
          case Choice(feature, One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)), _) =>
            putToDefUseMap(key)
          case Choice(feature, One(Enumerator(key, _)), One(Enumerator(key2, _))) =>
            putToDefUseMap(key)
            putToDefUseMap(key2)
          case Choice(feature, One(Enumerator(key, _)), _) =>
            putToDefUseMap(key)
          case k => //println("Oh i forgot " + k)
        }
      // TODO Check with new StructEnv!
      case st: StructDeclaration =>
        st.declaratorList.foreach(x => x.entry match {
          case StructDeclarator(AtomicNamedDeclarator(_, id: Id, _), _, _) =>
            env.varEnv.getAstOrElse(id.name, null) match {
              case null => putToDefUseMap(id)
              case One(null) => putToDefUseMap(id)
              case One(i: InitDeclarator) => {
                val key = i.getId
                // TODO: AddUse?
                putToDefUseMap(key)
              }
            }
          case StructDeclarator(NestedNamedDeclarator(pointers, nestedDecl, _), _, _) =>
            pointers.foreach(x => addDecl(x, env))
            putToDefUseMap(nestedDecl.getId)
          // defuse.put(nestedDecl.getId, List())
          case k => println("Pattern StructDeclaration fail: " + k)
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
      case k =>
        println("Missing Add Def: " + f + " from " + k)
    }
  }

  def addTypeUse(entry: AST, env: Env) {
    entry match {
      case i@Id(name) =>
        env.typedefEnv.getAstOrElse(name, null) match {
          case One(InitDeclaratorI(declarator, _, _)) =>
            addToDefUseMap(declarator.getId, i)
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
              case k => println("AddTypeUse Choice not exhaustive: " + k)
            })
          case One(Enumerator(key, _)) => addToDefUseMap(key, i)
          case One(Declaration(init, decl)) =>
            decl.head match {
              case Opt(ft, InitDeclaratorI(AtomicNamedDeclarator(_, key: Id, _), _, _)) =>
                addToDefUseMap(key, i)
              case Opt(ft, InitDeclaratorI(NestedNamedDeclarator(_, AtomicNamedDeclarator(_, key, _), _), _, _)) =>
                addToDefUseMap(key, i)
              case k => println("Fehlt: " + k)
            }
          case k =>
            if (name.startsWith("__builtin")) {
              defuse.put(i, List())
            } else {
              println("Missing: " + i + "\nElement " + k)
            }

        }

    }
  }

  def addUse(entry: AST, env: Env) {
    entry match {
      case ConditionalExpr(expr, thenExpr, elseExpr) =>
        addUse(expr, env)
        thenExpr.foreach(x => addUse(x, env))
        addUse(elseExpr, env)
      case FunctionCall(param) => param.exprs.foreach(x => addUse(x.entry, env))
      case i@Id(name) =>
        env.varEnv.getAstOrElse(name, null) match {
          case One(InitDeclaratorI(declarator, _, _)) =>
            addToDefUseMap(declarator.getId, i)
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
          case Choice(feature, One(Enumerator(key, _)), _) => addToDefUseMap(key, i)
          case One(Enumerator(key, _)) => addToDefUseMap(key, i)
          case One(null) =>
            // TODO workaround entfernen - causes missing ids
            if (defUseContainsIdName(i.name)) {
              addToDefUseMap(getKeyByName(i.name), i)
            } else {
              println("One(Null) - AddUse" + i + env.varEnv(i.name))
            }
          case k => println("AddUse Id not exhaustive: " + i + "\nElement " + k)
        }
      case PointerDerefExpr(i) => addUse(i, env)
      case AssignExpr(target, operation, source) =>
        addUse(source, env)
        addUse(target, env)
      case NAryExpr(i, o) =>
        addUse(i, env)
        o.foreach(x => addUse(x.entry, env))
      case NArySubExpr(_, e) => addUse(e, env)
      case PostfixExpr(p, s) =>
        addUse(p, env)
        addUse(s, env)
      case PointerPostfixSuffix(_, id) => addUse(id, env)
      case PointerCreationExpr(expr) => addUse(expr, env)
      case CompoundStatement(innerStatements) => innerStatements.foreach(x => addUse(x.entry, env))
      case Constant(_) =>
      case SizeOfExprT(expr) => addUse(expr, env)
      case TypeName(specs, decl) =>
        specs.foreach(x => addDecl(x.entry, env))
        addDecl(decl, env)
      case StringLit(_) =>
      case SimplePostfixSuffix(_) =>
      case CastExpr(_, expr) =>
        addUse(expr, env)
      case ArrayAccess(_) =>
      case UnaryExpr(_, i: Id) =>
        addUse(i, env)
      case k => println("Completly missing add use: " + k)
    }
  }

  def addOneNullIds(entry: AST, env: Env) = {

  }

  def addStructUse(entry: AST, env: Env, structName: String, isUnion: Boolean) = {
    entry match {
      case i@Id(name) => {
        if (env.structEnv.someDefinition(structName, isUnion)) {

          env.structEnv.getFieldsMerged(structName, isUnion).getAstOrElse(i.name, i) match {
            case One(AtomicNamedDeclarator(_, i2: Id, List())) =>
              addToDefUseMap(i2, i)
            case One(i2: Id) =>
              addToDefUseMap(i2, i)
            case k => // println("omg this should not have happend")
          }
        } else {
          env.typedefEnv.getAstOrElse(i.name, null) match {
            case One(i2: Id) => addToDefUseMap(i2, i)
            case One(null) =>
              addDef(i, env)
            case k => println("Error struct " + structName + " entry " + entry + " typedefEnv: " + k)
          }
        }
      }
      case k =>
        println("Missing Add Struct: " + k)
    }
  }

  def addStructDecl(entry: AST, env: Env) = {
    entry match {
      case i@Id(name) =>
        defuse.keySet().toArray().foreach(x => if (x.asInstanceOf[Id].name.equals(name)) {
          addToDefUseMap(x.asInstanceOf[Id], i)
        })
      case k => println("AddStructDecl fail: " + k)
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
          case _ => addDecl(opt.get, env)
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
        addDecl(cond, env)
        then.toOptList.foreach(x => addDecl(x.entry, env))
        elif.foreach(x => addDecl(x.entry.condition, env))
        elif.foreach(x => x.entry.thenBranch.toOptList.foreach(x => addDecl(x, env)))
        els.foreach(x => addDecl(x, env))
      case EnumSpecifier(_, _) =>
        println()
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

      /* Diese folgenden Zeilen entfernen IDs aus der DefUseMap, warum??   */
      /* case DeclarationStatement(decl) =>
      addDecl(decl, env)
      */

      case ReturnStatement(expr) =>
        if (!expr.isEmpty) {
          addDecl(expr.get, env)
        }
      case AssignExpr(target, operation, source) =>
        addUse(source, env)
        addUse(target, env)
      case UnaryOpExpr(_, expr) =>
        addDecl(expr, env)
      case DoStatement(expr, cond) =>
        addDecl(expr, env)
        addDecl(cond, env)
      case StructOrUnionSpecifier(isUnion, Some(i@Id(name)), None) =>
        addStructUse(i, env, i.name, isUnion)
      case StructOrUnionSpecifier(isUnion, Some(i@Id(name)), Some(extensions)) =>
        addStructUse(i, env, i.name, isUnion)
        extensions.foreach(x => addDecl(x, env))
      case StructOrUnionSpecifier(_, None, Some(extensions)) =>
        extensions.foreach(x => addDecl(x, env))
      case StructDeclaration(qualifiers, declarotors) =>
        qualifiers.foreach(x => addDecl(x.entry, env))
        declarotors.foreach(x => addDecl(x.entry, env))
      case StructDeclarator(decl, i: Id, _) =>
        addDecl(decl, env)
        addDef(i, env)
      case ExprStatement(expr) =>
        addDecl(expr, env)
      case pe@PostfixExpr(expr, suffix) =>
        if (expr.isInstanceOf[Id] && expr.asInstanceOf[Id].name.equals("handle")) {
          //println("\n++ANDI2++: " + env.varEnv.getAstOrElse(expr.asInstanceOf[Id].name, null))
        }
        addUse(expr, env)
        addDecl(suffix, env)
      case pps@PointerPostfixSuffix(_, i: Id) =>
        if (i.toString().equals("Id(ar__name)")) {
          //println("\n++FLO++: " + env.varEnv.getAstOrElse(i.toString(), null))
          //println("\n++FLO2++: " + env.typedefEnv.getAstOrElse(i.toString(), null))
          //println("\n++ANDI++: " + env.varEnv.lookup(i.toString()))
        }
        addUse(i, env)
      case f@FunctionCall(expr) =>
        expr.exprs.foreach(x =>
          x.entry match {
            case i: Id => addUse(x.entry, env)
            case _ => addDecl(x.entry, env)
          })
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
          case k => addDecl(k, env)
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
      case TypeName(a, _) =>
        println("TypeName" + a)
      case LabelStatement(id, _) => addLabelStatement(id, env)
      case CompoundStatement(statement) => statement.foreach(x => addDecl(x.entry, env))
      case GotoStatement(id) => addLabelStatement(id, env)
      case PointerDerefExpr(expr) => addUse(expr, env)
      case WhileStatement(expr, cond) =>
        addDecl(expr, env)
        cond.toOptList.foreach(x => addDecl(x.entry, env))
      case ArrayAccess(expr) => addDecl(expr, env)
      case Choice(ft, then, els) =>
        addDecl(then, env)
        addDecl(els, env)
      case k =>
        if (!k.isInstanceOf[BreakStatement] && !k.isInstanceOf[ContinueStatement] && !k.isInstanceOf[SimplePostfixSuffix] && !k.isInstanceOf[Specifier] && !k.isInstanceOf[DeclArrayAccess] && !k.isInstanceOf[VarArgs] && !k.isInstanceOf[AtomicAbstractDeclarator] && !k.isInstanceOf[StructInitializer] && !k.isInstanceOf[StringLit]) {
          println("Missing Case: " + k)
        }
    }
  }

  def addLabelStatement(expr: Expr, env: Env) {
    // TODO LabelMap Env -> Waiting for solution by C. Kaestner
    addDecl(expr, env)
  }
}
