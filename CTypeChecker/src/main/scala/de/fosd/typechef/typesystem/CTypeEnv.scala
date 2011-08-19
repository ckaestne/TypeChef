package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.FeatureExpr
import org.kiama.attribution.Attribution._
import org.kiama._

trait CTypeEnv extends CTypes with ASTNavigation with CDeclTyping with CBuiltIn {

    //Variable-Typing Context: identifier to its non-void wellformed type
    type VarTypingContext = ConditionalTypeMap


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

    /**
     * for struct and union
     * ConditionalTypeMap represents for the fields of the struct
     *
     * we store whether a structure with this name is defined (FeatureExpr) whereas
     * we do not distinguish between alternative structures. fields are merged in
     * one ConditionalTypeMap entry, but by construction they cannot overlap if
     * the structure declarations do not overlap variant-wise
     */
    class StructEnv(val env: Map[(String, Boolean), (FeatureExpr, ConditionalTypeMap)]) {
        def this() = this (Map())
        //returns the condition under which a structure is defined
        def someDefinition(name: String, isUnion: Boolean): Boolean = env contains (name, isUnion)
        def isDefined(name: String, isUnion: Boolean): FeatureExpr = env.getOrElse((name, isUnion), (FeatureExpr.dead, null))._1
        def isDefinedUnion(name: String) = isDefined(name, true)
        def isDefinedStruct(name: String) = isDefined(name, false)
        def add(name: String, isUnion: Boolean, condition: FeatureExpr, fields: ConditionalTypeMap) = {
            //TODO check distinct attribute names in each variant
            //TODO check that there is not both a struct and a union with the same name
            val oldCondition = isDefined(name, isUnion)
            val oldFields = env.getOrElse((name, isUnion), (null, new ConditionalTypeMap()))._2
            val key = (name, isUnion)
            val value = (oldCondition or condition, oldFields ++ fields)
            new StructEnv(env + (key -> value))
        }
        def get(name: String, isUnion: Boolean): ConditionalTypeMap = env((name, isUnion))._2
        override def toString = env.toString
    }

    val structEnv: AST ==> StructEnv = {
        def addDeclaration(e: Declaration, outer: AST) = e.declSpecs.foldRight(outerStructEnv(outer))({
            case (Opt(_, a), b: StructEnv) =>
                var r = b
                for (s <- (a -> struct))
                    r = r.add(s._1, s._2, s._3, s._4)
                r
        })
        attr {
            case e@DeclarationStatement(d) => addDeclaration(d, e)
            case e: Declaration => addDeclaration(e, e)
            case e: AST => outerStructEnv(e)
        }
    }

    type StructData = (String, Boolean, FeatureExpr, ConditionalTypeMap)

    val struct: AST ==> List[StructData] = attr {
        case e@StructOrUnionSpecifier(isUnion, Some(Id(name)), attributes) =>
            List((name, isUnion, e -> featureExpr, parseStructMembers(attributes))) ++ innerStructs(attributes)
        case e@StructOrUnionSpecifier(_, None, attributes) =>
            innerStructs(attributes)
        case _ => Nil
    }
    private def innerStructs(fields: List[Opt[StructDeclaration]]): List[StructData] =
        fields.flatMap(e =>
            (for (Opt(_, spec) <- e.entry.qualifierList) yield spec -> struct).flatten
        )


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
                if (structEnv.someDefinition(name, isUnion))
                    nonEmptyWellformedEnv(structEnv.get(name, isUnion), Some(name))
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
                    case EnumSpecifier(Some(Id(name)), l) if (!l.isEmpty) =>
                        b + (name -> ((typeSpec -> featureExpr) or b.getOrElse(name, FeatureExpr.dead)))
                    case _ => b
                }
            })
        case e: AST => outerEnumEnv(e)
    }


    private def outerEnumEnv(e: AST): EnumEnv =
        outer[EnumEnv](enumEnv, () => Map(), e)
}