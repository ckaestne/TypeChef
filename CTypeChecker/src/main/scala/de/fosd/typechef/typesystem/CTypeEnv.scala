package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.FeatureExpr
import org.kiama.attribution.Attribution._
import org.kiama._

trait CTypeEnv extends CTypes with ASTNavigation with CDeclTyping with CBuiltIn {

    //Variable-Typing Context: identifier to its non-void wellformed type
    type VarTypingContext = ConditionalTypeMap


    /**
     * for struct and union
     * we reuse the vartyping context for the fields of the struct
     */
    class StructEnv(val env: Map[(String, Boolean), ConditionalTypeMap]) {
        def this() = this (Map())
        def contains(name: String, isUnion: Boolean) = env contains ((name, isUnion))
        def containsUnion(name: String) = contains(name, true)
        def containsStruct(name: String) = contains(name, false)
        def add(name: String, isUnion: Boolean, fields: ConditionalTypeMap) =
        //TODO check distinct attribute names in each variant
        //TODO check that there is not both a struct and a union with the same name
            new StructEnv(env + ((name, isUnion) -> (env.getOrElse((name, isUnion), new ConditionalTypeMap()) ++ fields)))
        def get(name: String, isUnion: Boolean): ConditionalTypeMap = env((name, isUnion))
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
    protected def outerVarEnv(e: AST): VarTypingContext =
        outer[VarTypingContext](varEnv, () => new VarTypingContext() ++ initBuiltinVarEnv, e)


    private def assertNoTypedef(decl: Declaration): Unit = assert(!isTypedef(decl.declSpecs))


    private def parameterTypes(decl: Declarator): List[(String, FeatureExpr, TConditional[CType])] = {
        //declarations with empty parameter lists
        if (decl.extensions.size == 1 && decl.extensions.head.entry.isInstanceOf[DeclIdentifierList] && decl.extensions.head.entry.asInstanceOf[DeclIdentifierList].idList.isEmpty)
            List()
        else {

            assert(decl.extensions.size == 1 && decl.extensions.head.entry.isInstanceOf[DeclParameterDeclList], "expect a single declarator extension for function parameters, not " + decl.extensions)

            val param: DeclParameterDeclList = decl.extensions.head.entry.asInstanceOf[DeclParameterDeclList]
            var result = List[(String, FeatureExpr, TConditional[CType])]()
            for (Opt(_, p) <- param.parameterDecls) p match {
                case PlainParameterDeclaration(specifiers) => //having int foo(void) is Ok, but for everything else we expect named parameters
                    assert(specifiers.isEmpty || (specifiers.size == 1 && specifiers.head.entry == VoidSpecifier()), "no name, old parameter style?") //TODO
                case ParameterDeclarationD(specifiers, decl) => result = ((decl.getName, p -> featureExpr, getDeclaratorType(decl, constructType(specifiers)))) :: result
                case ParameterDeclarationAD(specifiers, decl) => assert(false, "no name, old parameter style?") //TODO
                case VarArgs() => //TODO not accessible as parameter?
            }
            result
        }
    }

    /***
     * Structs
     */
    val structEnv: AST ==> StructEnv = attr {
        case e@Declaration(decls, _) =>
            decls.foldRight(outerStructEnv(e))({
                case (Opt(_, a), b: StructEnv) =>
                    val s = a -> struct
                    if (s.isDefined) b.add(s.get._1, s.get._2, s.get._3) else b
            })
        case e: AST => outerStructEnv(e)
    }

    val struct: AST ==> Option[(String, Boolean, ConditionalTypeMap)] = attr {
        case e@StructOrUnionSpecifier(isUnion, Some(Id(name)), attributes) =>
            Some((name, isUnion, parseStructMembers(attributes)))
        case _ => None
    }


    protected def outerStructEnv(e: AST): StructEnv =
        outer[StructEnv](structEnv, () => new StructEnv(), e)

    def wellformed(structEnv: StructEnv, ptrEnv: PtrEnv, ctype: TConditional[CType]): Boolean =
        ctype.simplify.forall(wellformed(structEnv, ptrEnv, _))

    def wellformed(structEnv: StructEnv, ptrEnv: PtrEnv, ctype: CType): Boolean = {
        val wf = wellformed(structEnv, ptrEnv, _: CType)
        def nonEmptyWellformedEnv(m: ConditionalTypeMap, name: Option[String]) =
            !m.isEmpty && m.allTypes.forall(t => {
                t.forall(_ != CVoid()) && wellformed(structEnv, (if (name.isDefined) ptrEnv + name.get else ptrEnv), t)
            })
        def lastParam(p: Option[CType]) = p == None || p == Some(CVarArgs()) || wf(p.get)
        ctype match {
            case CSigned(_) => true
            case CUnsigned(_) => true
            case CSignUnspecified(_) => true
            case CVoid() => true
            case CFloat() => true
            case CDouble() => true
            case CLongDouble() => true
            case CPointer(CStruct(s, _)) => ptrEnv contains s
            case CPointer(t) => wf(t)
            case CArray(t, n) => wf(t) && (t != CVoid()) && n > 0
            case CFunction(param, ret) => wf(ret) && !arrayType(ret) && (
                    param.forall(p => !arrayType(p) && p != CVoid())) &&
                    param.dropRight(1).forall(wf(_)) &&
                    lastParam(param.lastOption) //last param may be varargs
            case CVarArgs() => false
            case CStruct(name, isUnion) => {
                true
                //TODO check struct welltypeness
                val members = structEnv.env.get((name, isUnion))
                if (members.isDefined)
                    nonEmptyWellformedEnv(members.get, Some(name))
                else false
            }
            case CAnonymousStruct(members, _) => nonEmptyWellformedEnv(members, None)
            case CUnknown(_) => false
            case CObj(_) => false
            case CCompound() => true
            case CIgnore() => true
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