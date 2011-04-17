package de.fosd.typechef.ast

/**
 * Created by IntelliJ IDEA.
 * User: kaestner
 * Date: 17.04.11
 * Time: 13:44
 * To change this template use File | Settings | File Templates.
 */

import org.kiama.attribution.DynamicAttribution._
import org.kiama._
import org.kiama.rewriting.Rewriter._
import TestAST._
import attribution.Attributable
import de.fosd.typechef.featureexpr.FeatureExpr
import FeatureExpr.base

/**
 * Variable use and definition interface.
 */
trait Variables {


    /**
     * Variable uses.
     */
    val uses: Stm ==> Set[String]


    /**
     * Variable definitions.
     */
    val defines: Stm ==> Set[String]


}


/**
 * Variable use and definition implementation.
 */
trait VariablesImpl extends Variables {


    val uses: Stm ==> Set[String] =
        attr {
            case Assign(a, v) => Set(a.toString) //++ usesExpr(v)
            case Return(v) => Set(v.toString)
            case _ => Set()
        }

    val usesExpr: Expr ==> Set[String] =
        attr {
            case Var(a) => Set(a.toString)
            case _ => Set()
        }

    val defines: Stm ==> Set[String] =
        attr {
            case VarDecl(_, v) => Set(v.toString)
            case _ => Set()
        }


}


case class Declaration(name: String, typ: String, presenceCondition: FeatureExpr, ast: LangAST)

/**
 * An environment to hold name bindings, nested within a parent environment.
 */
class Environment(parent: Environment) {


    import scala.collection.mutable._


    val decls = new HashMap[String, LangAST]


    def addToEnv(d: Declaration) {
        decls.update(d.name, d.ast)
    }
    def addToEnv(name: String, typ: String, presenceCondition: FeatureExpr, ast: LangAST) {
        decls.update(name, ast)
    }

    def findDecl(nm: String, searchAll: Boolean): Option[LangAST] = {
        //XXX not variability-aware yet
        if (decls.contains(nm))
            Some(decls(nm))
        else if (searchAll && (parent != null))
            parent.findDecl(nm, true)
        else
            None
    }


    /**
     * Return whether the name is multiply defined in this scope
     */
    def isMultiplyDefined(nm: String): Boolean = {
        //        if (decls.contains(nm))
        //            decls(nm).size>1
        //        else
        false
    }


    override def toString() = decls.keys.mkString("[", ",", "]") + (if (parent != null) parent.toString else "")
}

trait NameAnalysis {

    val printEnvRule = query {
        case e: Attributable => {
            val ev: Environment = e -> env
            println(e + " --env: " + ev)
        }
        case _ =>
    }
    val testEnv = topdown(printEnvRule)


    val declarations: Attributable ==> Set[Declaration] = attr {
        case decl@VarDecl(typ, name) => Set(new Declaration(name.toString, typ.toString, base, decl))
        case e => e.children.map(_ -> declarations).foldRight(Set[Declaration]())(_ ++ _)
    }
    /**
     * The accessible names and types at a given point in the program.
     */
    val env: Attributable ==> Environment =
        attr {
            // Modules: Create a new environment
            case md: CompUnit => {
                val globalEnv = new Environment(null)
                // Create module environment
                val env1 = new Environment(globalEnv)
                //                env1.addDeclsToEnv (decls)
                env1
            }

            case stmt: Stm => {
                val env1 = new Environment(if (stmt.parent != null) stmt.parent -> env else null)
                var prev = stmt.parent.prev[Attributable]
                while (prev != null) {
                    val decl = prev -> declarations
                    decl.foreach(env1.addToEnv(_))
                    //                    prev match {
                    //                        case decl@VarDecl(typ, name) => env1.addToEnv(name.toString, typ.toString, base, decl)
                    //                        case _ =>
                    //                    }
                    prev = prev.prev
                }
                env1
            }






            // Other objects:  Get parent's environment
            case obj@_ => obj.parent -> env
        }
}

object Analysis extends VariablesImpl with NameAnalysis
