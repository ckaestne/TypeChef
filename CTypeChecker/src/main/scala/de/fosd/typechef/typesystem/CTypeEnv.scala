package de.fosd.typechef.typesystem

import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory}
import de.fosd.typechef.parser.c._

trait CTypeEnv extends CTypes with CTypeSystemInterface with CEnv with CDeclTyping {


    /**
     * get the parameters from the innermost declarator. all outer (nested) declarators describe only
     * arrays or parameters of the return type, if the function returns a function
     */
    protected final def parameterTypes(decl: Declarator, featureExpr: FeatureExpr, env: Env, oldStyleParam: ConditionalTypeMap): List[(String, FeatureExpr, AST, Conditional[CType])] =
        decl match {
            case NestedNamedDeclarator(_, nestedDecl, _, _) => parameterTypes(nestedDecl, featureExpr, env, oldStyleParam)
            case atomicDecl: AtomicNamedDeclarator => parameterTypesAtomic(atomicDecl, featureExpr, env, oldStyleParam)
        }


    private def parameterTypesAtomic(decl: AtomicNamedDeclarator, featureExpr: FeatureExpr, env: Env, oldStyleParam: ConditionalTypeMap): List[(String, FeatureExpr, AST, Conditional[CType])] = {
        var result = List[(String, FeatureExpr, AST, Conditional[CType])]()
        for (Opt(extensionFeature, extension) <- decl.extensions) extension match {
            case DeclIdentifierList(List()) => //declarations with empty parameter lists
            case DeclParameterDeclList(paramDecls) =>
                for (Opt(paramFeature, param) <- paramDecls) {
                    val f = featureExpr and extensionFeature and paramFeature
                    param match {
                        case PlainParameterDeclaration(specifiers, _) =>
                            //having int foo(void) is Ok, but for everything else we expect named parameters
                            val onlyVoid = !specifiers.exists(spec => (spec.condition and f).isSatisfiable() && spec.entry != VoidSpecifier())
                            assertTypeSystemConstraint(onlyVoid, featureExpr and extensionFeature and paramFeature, "no name, old parameter style?", param) //TODO
                        case ParameterDeclarationD(specifiers, decl, _) =>
                            result = ((decl.getName, f, decl, getDeclarationType(specifiers, decl, f, env))) :: result
                        case ParameterDeclarationAD(specifiers, decl, _) =>
                            assertTypeSystemConstraint(false, featureExpr and extensionFeature and paramFeature, "no name, old parameter style?", param) //TODO
                        case VarArgs() => //TODO not accessible as parameter?
                    }
                }
            case DeclIdentifierList(idList) =>
                //old style parameters are read previously into the oldStyleParam map, so here we only need to look up the corresponding types
                for (Opt(paramFeature, id) <- idList) {
                    assert(oldStyleParam != null)
                    val paramType = oldStyleParam.getOrElse(id.name, CUnsigned(CInt()))
                    val f = featureExpr and extensionFeature and paramFeature
                    result = (id.name, f, id, paramType) :: result
                }
            case e => assertTypeSystemConstraint(false, featureExpr and extensionFeature, "other extensions not supported yet: " + e, e)
        }
        result


    }

    /** *
      * Structs
      */
    def addStructDeclarationToEnv(e: Declaration, featureExpr: FeatureExpr, env: Env): Env = addStructDeclarationToEnv(e.declSpecs, featureExpr, env, e.init.isEmpty)
    def addStructDeclarationToEnv(e: StructDeclaration, featureExpr: FeatureExpr, env: Env): Env = addStructDeclarationToEnv(e.qualifierList, featureExpr, env, e.declaratorList.isEmpty)
    def addStructDeclarationToEnv(specifiers: List[Opt[Specifier]], featureExpr: FeatureExpr, initEnv: Env, declareIncompleteTypes: Boolean): Env = {
        var env = initEnv
        for (Opt(specFeature, specifier) <- specifiers) {
            env = addStructDeclarationToEnv(specifier, featureExpr and specFeature, env, declareIncompleteTypes)
        }
        env
    }

    def checkStructRedeclaration(name: String, isUnion: Boolean, featureExpr: FeatureExpr, scope: Int, env: Env, where: AST) {
        //TODO disabled for now. need to merge addStructDeclarationToEnv with getDeclaredVariables first
        //        val mayRedeclare=env.structEnv.mayDeclare(name, isUnion, scope)
        //        if ((featureExpr andNot mayRedeclare).isSatisfiable())
        //            reportTypeError(featureExpr andNot mayRedeclare,"redefinition of \"%s %s\"".format(if(isUnion)"union" else "struct", name),where, Severity.RedeclarationError)
    }

    def addStructDeclarationToEnv(specifier: Specifier, featureExpr: FeatureExpr, initEnv: Env, declareIncompleteTypes: Boolean): Env = specifier match {
        case e@StructOrUnionSpecifier(isUnion, Some(i@Id(name)), Some(attributes), _, _) => {
            /**
             * CDeclUse:
             * Struct declaration to an existing struct forward declaration in a context where
             * Struct declaration context implies forward declaration context
             */
            val isAlreadyDefined = initEnv.structEnv.whenHasDefinitionWithId(name, isUnion)
            addStructRedeclaration(initEnv, i, featureExpr and isAlreadyDefined, isUnion)
            addStructDefinition(i, initEnv, featureExpr andNot isAlreadyDefined) //CDeclUse: Struct declaration

            //for parsing the inner members, the struct itself is available incomplete
            var env = initEnv.updateStructEnv(initEnv.structEnv.addIncomplete(i, isUnion, featureExpr, initEnv.scope))
            attributes.foreach(x => addDefinition(x.entry, env))
            val members = parseStructMembers(attributes, featureExpr, env)

            //collect inner struct declarations recursively
            env = addInnerStructDeclarationsToEnv(attributes, featureExpr, env)
            addStructRedeclaration(initEnv, i, featureExpr and isAlreadyDefined, isUnion) //CDeclUse: Struct redeclaration
            checkStructRedeclaration(name, isUnion, featureExpr, env.scope, env, e)


            env.updateStructEnv(env.structEnv.addComplete(i, isUnion, featureExpr, members, env.scope))
        }
        //incomplete struct
        case e@StructOrUnionSpecifier(isUnion, Some(i@Id(name)), None, _, _) => {
            if (declareIncompleteTypes) {
                //CDeclUse: Struct usage and declaration
                val isAlreadyDefined = initEnv.structEnv.whenHasDefinitionWithId(name, isUnion)
                addStructDeclUse(i, initEnv, isUnion, featureExpr and isAlreadyDefined)
                addStructDefinition(i, initEnv, featureExpr andNot isAlreadyDefined)

                //we only add an incomplete declaration in specific cases when a declaration does not have a declarator ("struct x;")
                var env = initEnv.updateStructEnv(initEnv.structEnv.addIncomplete(i, isUnion, featureExpr, initEnv.scope))
                env
            } else {
                addStructDeclUse(i, initEnv, isUnion, featureExpr)
                initEnv
            }
        }
        case e@StructOrUnionSpecifier(_, None, Some(attributes), _, _) =>
            addInnerStructDeclarationsToEnv(attributes, featureExpr, initEnv)
        case _ => initEnv
    }

    private def addInnerStructDeclarationsToEnv(fields: List[Opt[StructDeclaration]], featureExpr: FeatureExpr, initEnv: Env): Env = {
        var env = initEnv
        for (Opt(f, field) <- fields) {
            env = addStructDeclarationToEnv(field, featureExpr and f, env)
        }
        env
    }

    def wellformedC(structEnv: StructEnv, ptrEnv: PtrEnv, ctype: Conditional[CType]): Boolean = wellformed(structEnv, ptrEnv, ctype.map(_.atype))

    def wellformed(structEnv: StructEnv, ptrEnv: PtrEnv, atype: Conditional[AType]): Boolean =
        atype.simplify.forall(wellformed(structEnv, ptrEnv, _))

    def wellformed(structEnv: StructEnv, ptrEnv: PtrEnv, atype: AType): Boolean = {
        val wf = wellformed(structEnv, ptrEnv, _: AType)
        def nonEmptyWellformedEnv(m: ConditionalTypeMap, name: Option[String]) =
            !m.isEmpty && m.allTypes.forall(t => {
                t.forall(_ != CVoid().toCType) && wellformed(structEnv, (if (name.isDefined) ptrEnv + name.get else ptrEnv), t.map(_.atype))
            })
        def lastParam(p: Option[AType]) = p == None || p == Some(CVarArgs()) || wf(p.get)
        /*if (ctype.isObject) false
        else*/ atype match {
            case CSigned(_) => true
            case CUnsigned(_) => true
            case CSignUnspecified(_) => true
            case CZero() => true
            case CBool() => true
            case CVoid() => true
            case CFloat() => true
            case CDouble() => true
            case CLongDouble() => true
            case CPointer(CStruct(s, _)) => ptrEnv contains s
            case CPointer(t) => wf(t)
            case CArray(t, n) =>
                wf(t) && (t != CVoid()) && n > 0
            case CFunction(param, ret) => wf(ret.atype) && !arrayType(ret.atype) && (
                param.forall(p => !arrayType(p) && p != CVoid().toCType)) &&
                param.dropRight(1).forall(p => wf(p.atype)) &&
                lastParam(param.lastOption.map(_.atype)) //last param may be varargs
            case CVarArgs() => false
            case CStruct(name, isUnion) => {
                //TODO check struct welltypeness
                if (structEnv.isComplete(name, isUnion).isSatisfiable())
                    nonEmptyWellformedEnv(structEnv.getFieldsMerged(name, isUnion), Some(name))
                else false
            }
            case CAnonymousStruct(_, members, _) => nonEmptyWellformedEnv(members, None)
            case CUnknown(_) => false
            case CCompound() => true
            case CIgnore() => true
            case CBuiltinVaList() => true
        }
    }


    def addEnumDeclarationToEnv(specifiers: List[Opt[Specifier]], featureExpr: FeatureExpr, env: Env, isHeadless: Boolean): EnumEnv = {
        specifiers.foldRight(env.enumEnv)({
            (opt, b) => {
                val specFeature = opt.condition
                val typeSpec = opt.entry
                typeSpec match {
                    case EnumSpecifier(Some(i@Id(name)), l) if (isHeadless || !l.isEmpty) =>
                        addDefinition(i, env, specFeature and featureExpr)
                        var ft = FeatureExprFactory.False
                        b.getOrElse(name, FeatureExprFactory.False) match {
                            case f: FeatureExpr => ft = f
                            case Tuple2(feat: FeatureExpr, id) => ft = feat
                        }
                        b + (name ->((featureExpr and specFeature or ft), i))
                    //recurse into structs
                    case StructOrUnionSpecifier(_, _, fields, _, _) =>
                        fields.getOrElse(Nil).foldRight(b)(
                            (optField, b) => addEnumDeclarationToEnv(optField.entry.qualifierList, featureExpr and specFeature and optField.condition, env.updateEnumEnv(b), optField.entry.declaratorList.isEmpty)
                        )

                    case TypeDefTypeSpecifier(name) =>
                        b
                    case _ => b
                }
            }
        })
    }
}