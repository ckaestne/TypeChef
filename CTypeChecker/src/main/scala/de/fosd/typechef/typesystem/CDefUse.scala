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
  private[typesystem] def clear() {defuse.clear()}

  def clearDefUseMap() { defuse.clear() }
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
        ext.foreach(x => x match {
          case null =>
          case Opt(_, d:DeclParameterDeclList) => d.parameterDecls.foreach(pdL => pdL match {
            case null =>
            case Opt(_, pd:ParameterDeclarationD) => {
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
      // TODO params
      // params are uses of local or global variables
      case PostfixExpr(i@Id(name), FunctionCall(params)) => {
        env.varEnv.getAstOrElse(name, null) match {
          case One(FunctionDef(_, declarator, _, _)) => {
            val key = declarator.getId

            // function definition used as def entry
            if (defuse.containsKey(key)) {
              defuse.put(key, defuse.get(key) ++ List(i))
            } else {
              var fd: Id = null
              for (k <- defuse.keySet().toArray)
                for (v <- defuse.get(k))
                  if (v.eq(key)) fd = k.asInstanceOf[Id]

              defuse.put(fd, defuse.get(fd) ++ List(i))
            }

          }
          case _ =>
        }
      }
      case AssignExpr(i1, _, i2) =>
        i1 match {
          case id1@Id(name) =>
            env.varEnv.getAstOrElse(name, null) match {
              case One(InitDeclaratorI(declarator, _, _)) => {
                val key = declarator.getId

                // function definition used as def entry
                if (defuse.containsKey(key)) {
                  defuse.put(key, defuse.get(key) ++ List(id1))
                } else {
                  var fd: Id = null
                  for (k <- defuse.keySet().toArray)
                    for (v <- defuse.get(k))
                      if (v.eq(key)) fd = k.asInstanceOf[Id]

                  defuse.put(fd, defuse.get(fd) ++ List(id1))
                }

              }
              case _ =>
        }
          case _ =>
        }
        i2 match {
          case id2@Id(name) =>
            env.varEnv.getAstOrElse(name, null) match {
              case One(InitDeclaratorI(declarator, _, _)) => {
                val key = declarator.getId

                // function definition used as def entry
                if (defuse.containsKey(key)) {
                  defuse.put(key, defuse.get(key) ++ List(id2))
                } else {
                  var fd: Id = null
                  for (k <- defuse.keySet().toArray)
                    for (v <- defuse.get(k))
                      if (v.eq(key)) fd = k.asInstanceOf[Id]

                  defuse.put(fd, defuse.get(fd) ++ List(id2))
                }

              }
              case _ =>
            }
          case _ =>
        }

      case NAryExpr(i@Id(name), _) =>
        env.varEnv.getAstOrElse(name, null) match {
          case One(InitDeclaratorI(declarator, _, _)) => {
            val key = declarator.getId
            // function definition used as def entry
            if (defuse.containsKey(key)) {
              defuse.put(key, defuse.get(key) ++ List(i))
            } else {
              var fd: Id = null
              for (k <- defuse.keySet().toArray)
                for (v <- defuse.get(k))
                  if (v.eq(key)) fd = k.asInstanceOf[Id]

              defuse.put(fd, defuse.get(fd) ++ List(i))
            }

          }
          case One(AtomicNamedDeclarator(_, declarator, _)) => {
            if (defuse.containsKey(declarator)) {
              defuse.put(declarator, defuse.get(declarator) ++ List(i))
            } else {
              var fd: Id = null
              for (k <- defuse.keySet().toArray)
                for (v <- defuse.get(k))
                  if (v.eq(declarator)) fd = k.asInstanceOf[Id]

              defuse.put(fd, defuse.get(fd) ++ List(i))
            }
          }
          case _ =>
        }
      case NArySubExpr(_, i@Id(name)) =>
        env.varEnv.getAstOrElse(name, null) match {
          case One(InitDeclaratorI(declarator, _, _)) => {
            val key = declarator.getId

            // function definition used as def entry
            if (defuse.containsKey(key)) {
              defuse.put(key, defuse.get(key) ++ List(i))
            } else {
              var fd: Id = null
              for (k <- defuse.keySet().toArray)
                for (v <- defuse.get(k))
                  if (v.eq(key)) fd = k.asInstanceOf[Id]

              defuse.put(fd, defuse.get(fd) ++ List(i))
            }

          }
          case _ =>
        }
      case i@Id(name) =>
        env.varEnv.getAstOrElse(name, null) match {
          case One(InitDeclaratorI(declarator, _, _)) => {
            val key = declarator.getId

            // function definition used as def entry
            if (defuse.containsKey(key)) {
              defuse.put(key, defuse.get(key) ++ List(i))
            } else {
              var fd: Id = null
              for (k <- defuse.keySet().toArray)
                for (v <- defuse.get(k))
                  if (v.eq(key)) fd = k.asInstanceOf[Id]

              defuse.put(fd, defuse.get(fd) ++ List(i))
            }

          }
          case _ =>
        }
      case _ =>
    }
  }
}
