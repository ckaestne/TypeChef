package de.fosd.typechef.typesystem

import java.util.IdentityHashMap
import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional.{Choice, Opt, One}


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
            defuse.put(key, defuse.get(key) ++ List(id))
          }
        }
      case st: StructDeclaration =>
        st.declaratorList.foreach(x => x.entry match {
          case
            StructDeclarator(AtomicNamedDeclarator(_, id: Id, _), _, _) =>
            env.varEnv.getAstOrElse(id.name, null) match {
              case null => defuse.put(id, List())
              case One(null) => defuse.put(id, List())
              case One(i: InitDeclarator) => {
                val key = i.getId
                defuse.put(key, defuse.get(key) ++ List(id))
              }
            }
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
            // println("Test: " + c + "\nKey:" + key + ", Id: " + i)
            addToDefUseMap(key, i)
            addToDefUseMap(key2, i)
          case Choice(feature, One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)), _) =>
            addToDefUseMap(key, i)
          case One(null) => println("Struct env: " + env.structEnv + "\nFrom: " + i)

          case k => println("Missing: " + i + "\nElement " + k)
        }
      case k => println("Completly missing add use: " + k)
    }
  }

  def addStructUse(entry: AST, env: Env, structName: String, isUnion: Boolean) = {
    entry match {
      case i@Id(name) =>
        env.structEnv.get(structName, isUnion).getAstOrElse(i.name, i) match {
          case One(AtomicNamedDeclarator(_, i2: Id, List())) =>
            addToDefUseMap(i2, i)
          case One(i2:Id) => {
              println("Zeile 146" + i2)
         }
          case _ =>
        }
      case _ =>
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
        defuse.put(key, List(target))
      } else {
        defuse.put(fd, defuse.get(fd) ++ List(target))
      }
    }
  }
}
