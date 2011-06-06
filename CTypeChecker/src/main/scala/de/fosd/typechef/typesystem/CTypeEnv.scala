package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c._
import de.fosd.typechef.parser._
import de.fosd.typechef.featureexpr.FeatureExpr
import FeatureExpr.base
import org.kiama.attribution.Attribution._
import org.kiama._

trait CTypeEnv extends CTypes with ASTNavigation with CDeclTyping {

    //Variable-Typing Context: identifier to its non-void wellformed type
    class VarTypingContext(entries: Map[String, Seq[(FeatureExpr, CType)]]) {
        def this() = this (Map())
        /*
            feature expressions are not rewritten as in the macrotable, but we
             may later want to ensure that they are mutually exclusive
             in get, they simply overwrite each other in order of addition
         */
        /**
         * apply returns a type, possibly CUndefined or a
         * choice type
         */
        def apply(name: String): CType = {
            if (!(entries contains name) || entries(name).isEmpty) CUndefined()
            else {
                val types = entries(name)
                if (types.size == 1 && types.head._1 == base) types.head._2
                else createChoiceType(types)
            }
        }
        def ++(decls: Seq[(String, FeatureExpr, CType)]) = {
            var r = entries
            for (decl <- decls) {
                if (r contains decl._1)
                    r = r + (decl._1 -> ((decl._2, decl._3) +: r(decl._1)))
                else
                    r = r + (decl._1 -> Seq((decl._2, decl._3)))
            }
            new VarTypingContext(r)
        }
        def +(name: String, f: FeatureExpr, t: CType) = this ++ Seq((name, f, t))
        private[typesystem] def contains(name: String) = entries contains name

        private def createChoiceType(types: Seq[(FeatureExpr, CType)]) = types.foldRight[CType](CUndefined())((p, t) => CChoice(p._1, p._2, t)) simplify
    }


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
        case fun: FunctionDef => outerVarEnv(fun) + (fun.getName, fun -> featureExpr, ctype(fun))
        case e@DeclarationStatement(decl) => assertNoTypedef(decl); outerVarEnv(e) ++ declType(decl)
        case e: AST => outerVarEnv(e)
    }
    private def outerVarEnv(e: AST): VarTypingContext =
        outer[VarTypingContext](varEnv, () => new VarTypingContext(Map()), e)


    private def assertNoTypedef(decl: Declaration): Unit = assert(!isTypedef(decl.declSpecs))


    /***
     * Structs
     */
    val structEnv: AST ==> StructEnv = attr {
        case e@Declaration(decls, _) =>
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
            case CChoice(_, a, b) => wf(a) && wf(b)
        }
    }

}