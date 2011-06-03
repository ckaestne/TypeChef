package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c._
import de.fosd.typechef.parser._
import de.fosd.typechef.featureexpr.FeatureExpr
import org.kiama.attribution.Attribution._
import org.kiama._

trait CTypeEnv extends CTypes with ASTNavigation with CDeclTyping {

    //Variable-Typing Context: identifier to its non-void wellformed type
    type VarTypingContext = Map[String, CType]

    class StructEnv(val env: Map[String, Seq[(String, FeatureExpr, CType)]]) {
        def this() = this (Map())
        def contains(name: String) = env contains name
        def add(name: String, attributes: Seq[(String, FeatureExpr, CType)]) =
        //TODO check distinct attribute names in each variant
            new StructEnv(env + (name -> (env.getOrElse(name, Seq()) ++ attributes)))
        def get(name: String): Seq[(String, FeatureExpr, CType)] = env(name)
    }


    /*****
     * Variable-Typing context (collects all top-level and local declarations)
     * variables with local scope overwrite variables with global scope
     */

    val varEnv: AST ==> VarTypingContext = attr {
        case e: Declaration => outerVarEnv(e) ++ declType(e)
        case fun: FunctionDef => outerVarEnv(fun) + (fun.getName -> (ctype(fun)))
        case e@DeclarationStatement(decl) => assertNoTypedef(decl); outerVarEnv(e) ++ declType(decl)
        case e: AST => outerVarEnv(e)
    }
    private def outerVarEnv(e: AST): VarTypingContext =
        outer[VarTypingContext](varEnv, () => Map(), e)


    private def assertNoTypedef(decl: Declaration): Unit = decl match {
        case ADeclaration(specs, _) => assert(!isTypedef(specs))
        case AltDeclaration(_, a, b) => assertNoTypedef(a); assertNoTypedef(b)
    }


    /***
     * Structs
     */
    val structEnv: AST ==> StructEnv = attr {
        case e@ADeclaration(decls, _) =>
            decls.foldRight(outerStructEnv(e))({
                case (Opt(_, a), b) => val s = a -> struct; if (s.isDefined) b.add(s.get._1, s.get._2) else b;
            })
        case e: AST => outerStructEnv(e)
    }

    val struct: AST ==> Option[(String, Seq[(String, FeatureExpr, CType)])] = attr {
        case e@StructOrUnionSpecifier(_, Some(Id(name)), attributes) =>
        //TODO variability
            Some((name, parseStructMembers(attributes)))
        case _ => None
    }


    private def outerStructEnv(e: AST): StructEnv =
        outer[StructEnv](structEnv, () => new StructEnv(Map()), e)


    def wellformed(structEnv: StructEnv, ptrEnv: PtrEnv, ctype: CType): Boolean = {
        val wf = wellformed(structEnv, ptrEnv, _: CType)
        ctype match {
            case CSigned(_) => true
            case CUnsigned(_) => true
            case CSignUnspecified(_) => true
            case CVoid() => true
            case CFloat() => true
            case CDouble() => true
            case CLongDouble() => true
            case CPointer(CStruct(s)) => ptrEnv contains s
            case CPointer(t) => wf(t)
            case CArray(t, n) => wf(t) && (t != CVoid()) && n > 0
            case CFunction(param, ret) => wf(ret) && !arrayType(ret) && (
                    param.forall(p => wf(p) && !arrayType(p) && p != CVoid()))
            case CStruct(name) => {
                val members = structEnv.env.getOrElse(name, Seq())
                //TODO variability
                val memberNames = members.map(_._1)
                val memberTypes = members.map(_._3)
                (!members.isEmpty && memberNames.distinct.size == memberNames.size &&
                        memberTypes.forall(t => {
                            t != CVoid() && wellformed(structEnv, ptrEnv + name, t)
                        }))
            }
            case CAnonymousStruct(members) => members.forall(x => wf(x._2))
            case CUnknown(_) => false
            case CObj(_) => false
        }
    }

}