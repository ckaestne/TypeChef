package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c._
import de.fosd.typechef.featureexpr.FeatureExpr
import org.kiama.attribution.DynamicAttribution._
import org.kiama._

trait CTypeEnv extends CTypes with ASTNavigation {

    //Variable-Typing Context: identifier to its non-void wellformed type
    type VarTypingContext = Map[String, CType]

    //Function-Typing Context: identifer to function types
    type FunTypingContext = Map[String, CFunction]

    //Type synonyms with typedef
    type TypeDefEnv = Map[String, CType]

    class StructEnv(val env: Map[String, Seq[(String, FeatureExpr, CType)]]) {
        def this() = this (Map())
        def contains(name: String) = env contains name
        def add(name: String, attributes: Seq[(String, FeatureExpr, CType)]) =
        //TODO check distinct attribute names in each variant
            new StructEnv(env + (name -> (env.getOrElse(name, Seq()) ++ attributes)))
        def get(name: String) = env(name)
    }

    def readAttributes(attr: List[StructDeclaration]): Seq[(String, FeatureExpr, CType)] =
        Seq()


    val structEnv: AST ==> StructEnv = attr {
        case e@StructOrUnionSpecifier(_, Some(Id(name)), attributes) =>
            outerStructEnv(e) add (name, readAttributes(attributes.map(_.entry)))
        case e: AST => outerStructEnv(e)
    }

    private def outerStructEnv(e: AST): StructEnv =
        outer[StructEnv](structEnv, () => new StructEnv(Map()), e)

    private def outer[T](f: AST ==> T, init: () => T, e: AST): T =
        if (e -> prevAST != null) f(e -> prevAST)
        else
        if (e -> parentAST != null) f(e -> parentAST)
        else
            init()


    def wellformed(structEnv: StructEnv, ptrEnv: PtrEnv, ctype: CType): Boolean = {
        val wf = wellformed(structEnv, ptrEnv, _: CType)
        ctype match {
            case CSigned(_) => true
            case CUnsigned(_) => true
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
            case CUnknown(_) => false
            case CObj(_) => false
        }
    }

}