package de.fosd.typechef.typesystem

import java.util.IdentityHashMap
import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional.{Choice, Opt, One}
import de.fosd.typechef.conditional.Choice


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
          case null => defuse.put(declarator.getId, List())
          case One(null) => defuse.put(declarator.getId, List())
          case One(i: InitDeclarator) => {
            val key = i.getId
            defuse.put(key, defuse.get(key) ++ List(id))
          }
          case Choice(_, One(FunctionDef(_, _, _, _)), One(null)) => defuse.put(declarator.getId, List())
          case k => println("Missing AddDef " + id + "\nentry " + k + "\nfuncdef " + func + "\n" + defuse.containsKey(declarator.getId))
        }
        // Parameter Declaration
        ext.foreach(x => x match {
          case null =>
          case Opt(_, d: DeclParameterDeclList) => d.parameterDecls.foreach(pdL => pdL match {
            case null =>
            case Opt(_, pd: ParameterDeclarationD) => {
              val paramID = pd.decl.getId
              env.varEnv.getAstOrElse(paramID.name, null) match {
                case null => defuse.put(paramID, List())
                case One(null) => defuse.put(paramID, List())
                case One(i: InitDeclarator) => {
                  val key = i.getId
                  defuse.put(key, defuse.get(key) ++ List(id))
                }
              }
            }
            case _ =>
          })
          case mi => println("Completly missing: " + mi)
        })
      }
      case i: InitDeclarator => defuse.put(i.getId, List())
      case id: Id =>
        env.varEnv.getAstOrElse(id.name, null) match {
          case null => defuse.put(id, List())
          case One(null) => defuse.put(id, List())
          case One(i: InitDeclarator) => {
            val key = i.getId
            if (defuse.containsKey(key)) {
              defuse.put(key, defuse.get(key) ++ List(id))
            } else {
              defuse.put(id, List())
            }
          }
          case One(e: Enumerator) => defuse.put(id, List())
          case Choice(feature, One(InitDeclaratorI(declarator, _, _)), One(InitDeclaratorI(declarator2, _, _))) =>
            defuse.put(declarator.getId, List())
            defuse.put(declarator2.getId, List())
          case Choice(feature, One(InitDeclaratorI(declarator, _, _)), _) =>
            defuse.put(declarator.getId, List())
          case Choice(feature, One(AtomicNamedDeclarator(_, key, _)), One(AtomicNamedDeclarator(_, key2, _))) =>
            defuse.put(key, List())
            defuse.put(key2, List())
          case Choice(feature, One(AtomicNamedDeclarator(_, key, _)), _) =>
            defuse.put(key, List())
          case Choice(feature, One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)), One(FunctionDef(_, AtomicNamedDeclarator(_, key2, _), _, _))) =>
            defuse.put(key, List())
            defuse.put(key2, List())
          case Choice(feature, One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)), _) =>
            defuse.put(key, List())
          case Choice(feature, One(Enumerator(key, _)), _) =>
            defuse.put(key, List())
          case k => println("Oh i forgot " + k)
        }
      case st: StructDeclaration =>
        st.declaratorList.foreach(x => x.entry match {
          case StructDeclarator(AtomicNamedDeclarator(_, id: Id, _), _, _) =>
            env.varEnv.getAstOrElse(id.name, null) match {
              case null => defuse.put(id, List())
              case One(null) => defuse.put(id, List())
              case One(i: InitDeclarator) => {
                val key = i.getId
                defuse.put(key, defuse.get(key) ++ List(id))
              }
            }
          case StructDeclarator(NestedNamedDeclarator(pointers, nestedDecl, _), _, _) =>
            pointers.foreach(x => addDecl(x, env))
            defuse.put(nestedDecl.getId, List())
          case k => println("Pattern StructDeclaration fail: " + k)
        })
      case k => println("Missing Add Def: " + f + " from " + k)
    }
  }

  def addUse(entry: AST, env: Env) {
    entry match {
      // TODO to remove?
      /*case PostfixExpr(i@Id(name), FunctionCall(params)) => {
        env.varEnv.getAstOrElse(name, null) match {
          case One(FunctionDef(_, declarator, _, _)) => addToDefUseMap(declarator.getId, i)
          case _ =>
        }
      }*/
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
          case One(null) => println("Struct env: " + env.structEnv + "\nFrom: " + i)
          case One(Enumerator(key, _)) => addToDefUseMap(key, i)
          case k => println("Missing:" + i + "\nElement " + k)
        }
      case PointerDerefExpr(i) => addUse(i, env)
      case k => println("Completly missing add use: " + k)
    }
  }

  def addStructUse(entry: AST, env: Env, structName: String, isUnion: Boolean) = {
    entry match {
      case i@Id(name) => {
        if (env.structEnv.someDefinition(structName, isUnion)) {

          env.structEnv.get(structName, isUnion).getAstOrElse(i.name, i) match {
            case One(AtomicNamedDeclarator(_, i2: Id, List())) =>
              addToDefUseMap(i2, i)
            case _ =>
          }
        } else {
          env.typedefEnv.getAstOrElse(i.name, null) match {
            case One(i2: Id) => addToDefUseMap(i2, i)
            case _ => println("Error struct " + structName + " entry " + entry)
          }
        }
      }
      case _ =>
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

  private def addToDefUseMap(key: Id, target: Id) {
    if (defuse.containsKey(key)) {
      var isIncluded = false
      defuse.get(key).foreach(x => if (x.eq(target)) isIncluded = true)
      if (!isIncluded) {
        defuse.put(key, defuse.get(key) ++ List(target))
      }
    } else {
      var fd: Id = null
      for (k <- defuse.keySet().toArray) {
        for (v <- defuse.get(k))
          if (v.eq(key)) fd = k.asInstanceOf[Id]
      }
      if (fd == null) {
        println("\nNaja das sollte wohl nicht so sein:\nkey: " + key + ", target: " + target)
        defuse.put(key, List(target))
      } else {
        defuse.put(fd, defuse.get(fd) ++ List(target))
      }
    }
  }

  def addDecl(current: Any, env: Env) {
    current match {
      case Nil =>
      case None =>
      case Declaration(decl, init) =>
        decl.foreach(x => addDecl(x, env))
        init.foreach(x => addDecl(x, env))
      case o@Opt(_, _) => addDecl(o.entry, env)
      case InitDeclaratorI(decl, attr, opt) =>
        addDecl(decl, env)
        attr.foreach(x => addDecl(x, env))
      case AtomicNamedDeclarator(pointers, id, extension) =>
        pointers.foreach(x => addDecl(x, env))
        extension.foreach(x => addDecl(x, env))
        addDecl(id, env)
      case i: Id =>
        addDef(i, env)
      case DeclParameterDeclList(decl) =>
        decl.foreach(x => addDecl(x, env))
      case ParameterDeclarationD(specs, decl) =>
        specs.foreach(x => addDecl(x, env))
        addDecl(decl, env)
      case Pointer(specs) =>
        specs.foreach(x => addDecl(x, env))
      case EnumSpecifier(id, None) =>
        addDecl(id, env)
      case EnumSpecifier(_, Some(o)) =>
        for (e <- o) {
          addDecl(e.entry, env)
        }
      case PlainParameterDeclaration(spec) => spec.foreach(x => addDecl(x.entry, env))
      case ParameterDeclarationAD(spec, decl) =>
        spec.foreach(x => addDecl(x.entry, env))
        addDecl(decl, env)
      case Enumerator(i@Id(name), _) =>
        addDecl(i, env)
      case TypeDefTypeSpecifier(name) =>
        addDecl(name, env)
      case DeclArrayAccess(Some(o)) =>
        addDecl(o, env)
      case StructOrUnionSpecifier(_, Some(o), Some(extensions)) =>
        addDecl(o, env)
        extensions.foreach(x => addDecl(x, env))
      case StructOrUnionSpecifier(_, None, Some(extensions)) =>
        extensions.foreach(x => addDecl(x, env))
      case StructDeclaration(qualifiers, declarotors) =>
        qualifiers.foreach(x => addDecl(x.entry, env))
        declarotors.foreach(x => addDecl(x.entry, env))
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
        addDecl(expr, env)
        others.foreach(x => addDecl(x.entry, env))
      case NArySubExpr(_, expr) =>
        addDecl(expr, env)
      case CastExpr(typ, expr) =>
        addDecl(expr, env)
      case SizeOfExprT(typ) =>
        addDecl(typ.decl, env)
      case ConditionalExpr(expr, thenExpr, elseExpr) =>
        addDecl(expr, env)
        thenExpr.foreach(x => addDecl(x, env))
        addDecl(elseExpr, env)
      case k =>
        if (!k.isInstanceOf[Specifier] && !k.isInstanceOf[Constant]) {
          println("M: " + k)
        }
    }
  }
}
