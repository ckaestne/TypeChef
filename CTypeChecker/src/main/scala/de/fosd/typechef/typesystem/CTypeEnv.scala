package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c._
import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}

trait CTypeEnv extends CTypes with CTypeSystemInterface with CEnv with CDeclTyping with CDefUse /*with CBuiltIn*/ {


  protected def parameterTypes(decl: Declarator, featureExpr: FeatureExpr, env: Env): List[(String, FeatureExpr, AST, Conditional[CType])] = {
    var result = List[(String, FeatureExpr, AST, Conditional[CType])]()
    for (Opt(extensionFeature, extension) <- decl.extensions) extension match {
      case DeclIdentifierList(List()) => //declarations with empty parameter lists
      case DeclParameterDeclList(paramDecls) =>
        for (Opt(paramFeature, param) <- paramDecls) {
          val f = featureExpr and extensionFeature and paramFeature
          param match {
            case PlainParameterDeclaration(specifiers) =>
              //having int foo(void) is Ok, but for everything else we expect named parameters
              val onlyVoid = !specifiers.exists(spec => (spec.feature and f).isSatisfiable() && spec.entry != VoidSpecifier())
              assertTypeSystemConstraint(onlyVoid, featureExpr and extensionFeature and paramFeature, "no name, old parameter style?", param) //TODO
            case ParameterDeclarationD(specifiers, decl) =>
              result = ((decl.getName, f, decl, getDeclarationType(specifiers, decl, f, env))) :: result
            case ParameterDeclarationAD(specifiers, decl) =>
              assertTypeSystemConstraint(false, featureExpr and extensionFeature and paramFeature, "no name, old parameter style?", param) //TODO
            case VarArgs() => //TODO not accessible as parameter?
          }
        }
      case e => assertTypeSystemConstraint(false, featureExpr and extensionFeature, "other extensions not supported yet: " + e, e)
    }
    result


  }

  /***
   * Structs
   */
  def addStructDeclarationToEnv(e: Declaration, featureExpr: FeatureExpr, env: Env): StructEnv =
    e.declSpecs.foldRight(env.structEnv)({
      case (Opt(specFeature, specifier), b: StructEnv) =>
        var r = b

        for (s <- (getStructFromSpecifier(specifier, featureExpr, env, e.init.isEmpty))) {
          r = r.add(s._1, s._2, s._3, s._4)
        }
        r
    })

  def addStructDeclarationToEnv(e: StructDeclaration, featureExpr: FeatureExpr, env: Env): StructEnv = {
    e.qualifierList.foldRight(env.structEnv)({
      case (Opt(specFeature, specifier), b: StructEnv) =>
        var r = b
        for (s <- (getStructFromSpecifier(specifier, featureExpr, env, e.declaratorList.isEmpty)))
          r = r.add(s._1, s._2, s._3, s._4)
        r
    })
  }

  type StructData = (String, Boolean, FeatureExpr, ConditionalTypeMap)

  def getStructFromSpecifier(specifier: Specifier, featureExpr: FeatureExpr, env: Env, includeEmptyDecl: Boolean): List[StructData] = specifier match {
    case e@StructOrUnionSpecifier(isUnion, Some(i@Id(name)), attributes) if (includeEmptyDecl || !attributes.isEmpty) => {
      // TODO: gültig ?
      //addDef(i, env)
      //attributes.getOrElse(Nil).foreach(x => addDef(x.entry, env))
      List((name, isUnion, featureExpr, parseStructMembers(attributes.getOrElse(Nil), featureExpr, env))) ++ innerStructs(attributes.getOrElse(Nil), featureExpr, env)
    }
    case e@StructOrUnionSpecifier(isUnion, Some(i@Id(name)), None) => {
      addStructUse(i, env, i.name, isUnion)
      Nil
    }
    case e@StructOrUnionSpecifier(_, None, attributes) =>
      innerStructs(attributes.getOrElse(Nil), featureExpr, env)
    case _ => Nil
  }

  // TODO: entfernen?
  def getStructIdFromSpecifier(specifier: Specifier, featureExpr: FeatureExpr, env: Env, includeEmptyDecl: Boolean): Tuple2[Id, List[Opt[StructDeclaration]]] = specifier match {
    case e@StructOrUnionSpecifier(isUnion, Some(i@Id(name)), attributes) if (includeEmptyDecl || !attributes.isEmpty) => {
      return (i, attributes.getOrElse(Nil))
    }
    case _ => null
  }

  private def innerStructs(fields: List[Opt[StructDeclaration]], featureExpr: FeatureExpr, env: Env): List[StructData] =
    fields.flatMap(field =>
      (for (Opt(f, spec) <- field.entry.qualifierList) yield getStructFromSpecifier(spec, featureExpr and field.feature and f, env, field.entry.declaratorList.isEmpty)).flatten
    )


  def wellformed(structEnv: StructEnv, ptrEnv: PtrEnv, ctype: Conditional[CType]): Boolean =
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
      case CZero() => true
      case CBool() => true
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


  def addEnumDeclarationToEnv(specifiers: List[Opt[Specifier]], featureExpr: FeatureExpr, enumEnv: EnumEnv, isHeadless: Boolean): EnumEnv =
    specifiers.foldRight(enumEnv)({
      (opt, b) => {
        val specFeature = opt.feature
        val typeSpec = opt.entry
        typeSpec match {
          case EnumSpecifier(Some(Id(name)), l) if (isHeadless || !l.isEmpty) =>
            b + (name -> (featureExpr and specFeature or b.getOrElse(name, FeatureExprFactory.False)))
          //recurse into structs
          case StructOrUnionSpecifier(_, _, fields) =>
            fields.getOrElse(Nil).foldRight(b)(
              (optField, b) => addEnumDeclarationToEnv(optField.entry.qualifierList, featureExpr and specFeature and optField.feature, b, optField.entry.declaratorList.isEmpty)
            )

          case _ => b
        }
      }
    })

}