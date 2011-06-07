package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c._
import de.fosd.typechef.parser._
import de.fosd.typechef.featureexpr.FeatureExpr
import FeatureExpr.base
import org.kiama.attribution.Attribution._
import org.kiama._

trait CTypeEnv extends CTypes with ASTNavigation with CDeclTyping with CBuiltIn {

    //Variable-Typing Context: identifier to its non-void wellformed type
    class VarTypingContext(entries: Map[String, Seq[(FeatureExpr, CType)]]) {
        def this() = this (initBuiltinVarEnv)
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
        //parameters in the body of functions
        case c@CompoundStatement(_) => c -> parentAST match {
            case FunctionDef(_, decl, _, _) => outerVarEnv(c) ++ parameterTypes(decl)
            case _ => outerVarEnv(c)
        }
        case e: AST => outerVarEnv(e)
    }
    private def outerVarEnv(e: AST): VarTypingContext =
        outer[VarTypingContext](varEnv, () => new VarTypingContext(), e)


    private def assertNoTypedef(decl: Declaration): Unit = assert(!isTypedef(decl.declSpecs))


    private def parameterTypes(decl: Declarator): List[(String, FeatureExpr, CType)] = {
        assert(decl.extensions.size == 1 && decl.extensions.head.entry.isInstanceOf[DeclParameterDeclList], "expect a single declarator extension for function parameters, not " + decl.extensions)

        val param: DeclParameterDeclList = decl.extensions.head.entry.asInstanceOf[DeclParameterDeclList]
        var result = List[(String, FeatureExpr, CType)]()
        for (Opt(_, p) <- param.parameterDecls) p match {
            case PlainParameterDeclaration(specifiers) => //having int foo(void) is Ok, but for everything else we expect named parameters
                assert(specifiers.size == 1 && specifiers.head.entry == VoidSpecifier(), "no name, old parameter style?") //TODO
            case ParameterDeclarationD(specifiers, decl) => result = ((decl.getName, p -> featureExpr, getDeclaratorType(decl, constructType(specifiers)))) :: result
            case ParameterDeclarationAD(specifiers, decl) => assert(false, "no name, old parameter style?") //TODO
            case VarArgs() => //TODO not accessible as parameter?
        }
        result
    }

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
        outer[StructEnv](structEnv, () => new StructEnv(), e)


    def wellformed(structEnv: StructEnv, ptrEnv: PtrEnv, ctype: CType): Boolean = {
        val wf = wellformed(structEnv, ptrEnv, _: CType)
        def lastParam(p: Option[CType]) = p == None || p == Some(CVarArgs()) || wf(p.get)
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
                    param.forall(p => !arrayType(p) && p != CVoid())) &&
                    param.dropRight(1).forall(wf(_)) &&
                    lastParam(param.lastOption) //last param may be varargs
            case CVarArgs() => false
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


    /**
     * Enum Environment: Just a set of names that are valid enums.
     * No need to remember fields etc, because they are integers anyway and no further checking is done in C
     */

    type EnumEnv = Map[String, FeatureExpr]

    val enumEnv: AST ==> EnumEnv = attr {
        case e@Declaration(decls, _) =>
            decls.foldRight(outerEnumEnv(e))({
                case (Opt(_, typeSpec), b: EnumEnv) => typeSpec match {
                    case EnumSpecifier(Some(Id(name)), l) if (!l.isEmpty) => b + (name -> ((typeSpec -> featureExpr) or b.getOrElse(name, FeatureExpr.dead)))
                    case _ => b
                }
            })
        case e: AST => outerEnumEnv(e)
    }


    private def outerEnumEnv(e: AST): EnumEnv =
        outer[EnumEnv](enumEnv, () => Map(), e)
}