package de.fosd.typechef.typesystem

import java.util.IdentityHashMap
import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional.{Opt, One}

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
      case FunctionDef(specifiers, declarator, oldStyleParameters, _) => {
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
          case _ =>
        })
      }
      case i: InitDeclarator => defuse.put(i.getId, List())
      case _ =>
    }
  }

  def addUse(entry: AST, env: Env) {

    entry match {
      case PostfixExpr(i@Id(name), FunctionCall(params)) => {
        env.varEnv.getAstOrElse(name, null) match {
          case One(FunctionDef(_, declarator, _, _)) => addToDefUseMap(declarator.getId, i)
          case _ =>
        }
      }
      case i@Id(name) =>
        env.varEnv.getAstOrElse(name, null) match {
          case One(InitDeclaratorI(declarator, _, _)) => addToDefUseMap(declarator.getId, i)
          case One(AtomicNamedDeclarator(_, key, _)) => addToDefUseMap(key, i)
          case One(FunctionDef(_, AtomicNamedDeclarator(_, key, _), _, _)) => addToDefUseMap(key, i)
          case _ =>
        }
      case _ =>
    }
  }

  private def addToDefUseMap(key: Id, target: Id) {
    if (defuse.containsKey(key)) {
      defuse.put(key, defuse.get(key) ++ List(target))
    } else {
      var fd: Id = null
      for (k <- defuse.keySet().toArray)
        for (v <- defuse.get(k))
          if (v.eq(key)) fd = k.asInstanceOf[Id]
      defuse.put(fd, defuse.get(fd) ++ List(target))
    }
  }
}
