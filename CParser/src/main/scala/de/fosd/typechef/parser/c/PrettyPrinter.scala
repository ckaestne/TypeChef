package de.fosd.typechef.parser.c

import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}

object PrettyPrinter {

    val s = new StringBuilder()

    def pretty(ast: AST, list_feature_expr: List[FeatureExpr] = List(FeatureExprFactory.True)): String = {
      s.clear()
      prettyPrint(ast, list_feature_expr)
      s.toString()
    }

    def ppConditional(e: Conditional[_], list_feature_expr: List[FeatureExpr]): Unit = {
      e match {
        case One(c: AST) => prettyPrint(c, list_feature_expr)
        case Choice(f, a: Conditional[_], b: Conditional[_]) => {
          s.append("\n")
          s.append("#if ")
          s.append(f.toTextExpr)
          s.append("\n")
          ppConditional(a, f :: list_feature_expr)
          s.append("\n")
          s.append("#else")
          s.append("\n")
          ppConditional(b, f.not :: list_feature_expr)
          s.append("\n")
          s.append("#endif")
          s.append("\n")
        }
      }
    }

    private def optConditional(e: Opt[AST], list_feature_expr: List[FeatureExpr]) = {
        if (e.feature == FeatureExprFactory.True ||
          list_feature_expr.foldLeft(FeatureExprFactory.True)(_ and _).implies(e.feature).isTautology())
          prettyPrint(e.entry, list_feature_expr)
        else {
          s.append("\n")
          s.append("#if ")
          s.append(e.feature.toTextExpr)
          s.append("\n")
          prettyPrint(e.entry, e.feature :: list_feature_expr)
          s.append("\n")
          s.append("#endif")
          s.append("\n")
        }
    }

  private def optString(e: Opt[String], list_feature_expr: List[FeatureExpr]) = {
    if (e.feature == FeatureExprFactory.True ||
      list_feature_expr.foldLeft(FeatureExprFactory.True)(_ and _).implies(e.feature).isTautology())
      s.append(e.entry)
    else {
      s.append("\n")
      s.append("#if ")
      s.append(e.feature.toTextExpr)
      s.append("\n")
      s.append(e.entry)
      s.append("\n")
      s.append("#endif")
      s.append("\n")
    }
  }

    def prettyPrint(ast: AST, list_feature_expr: List[FeatureExpr] = List(FeatureExprFactory.True)) {
        implicit def pretty(a: AST) { prettyPrint(a, list_feature_expr) }
        implicit def prettyOpt(a: Opt[AST]) = optConditional(a, list_feature_expr)
        implicit def prettyOptString(a: Opt[String]) = optString(a, list_feature_expr)
        implicit def prettyCond(a: Conditional[_]) { ppConditional(a, list_feature_expr) }
        implicit def prettyOptStr(a: Opt[String]) = s.append(a.entry)

        def sep(l: List[Opt[AST]], ad: String) {
          l match {
            case Nil =>
            case x :: Nil => prettyOpt(x)
            case x :: rl  => { prettyOpt(x); s.append(ad); sep(rl, ad) }
          }
        }
        def seps(l: List[Opt[String]], ad: String) {
          l match {
            case Nil =>
            case x :: Nil => prettyOptString(x)
            case x :: rl  => { prettyOptString(x); s.append(ad); seps(rl, ad) }
          }
        }
        def commaSep(l: List[Opt[AST]]) { sep(l, ", ") }
        def spaceSep(l: List[Opt[AST]]) { sep(l, " ") }
        def opt(o: Option[AST]) { if (o.isDefined) prettyPrint(o.get) }
        def optExt(o: Option[AST], ad: String) { if (o.isDefined) {s.append(ad); prettyPrint(o.get)} }

        ast match {
            case TranslationUnit(ext) => sep(ext, "\n")
            case Id(name) => s.append(name)
            case Constant(v) => s.append(v)
            case StringLit(v) => seps(v, "")
            case SimplePostfixSuffix(t) => s.append(t)
            case PointerPostfixSuffix(kind, id) => { s.append(kind); prettyPrint(id) }
            case FunctionCall(params) => { s.append("("); prettyPrint(params); s.append(")") }
            case ArrayAccess(e) => { s.append("["); prettyPrint(e); s.append("]") }
            case PostfixExpr(p, st) => { prettyPrint(p); prettyPrint(st) }
            case UnaryExpr(p, st) => { s.append(p); prettyPrint(st) }
            case SizeOfExprT(typeName) => { s.append("sizeof("); prettyPrint(typeName); s.append(")") }
            case SizeOfExprU(e) => { s.append("sizeof("); prettyPrint(e); s.append(")") }
            case CastExpr(typeName, expr) => { s.append("(("); prettyPrint(typeName); s.append(") "); prettyPrint(expr); s.append(")") }

            case PointerDerefExpr(castExpr) => { s.append("(*"); prettyPrint(castExpr); s.append(")") }
            case PointerCreationExpr(castExpr) => { s.append("(&"); prettyPrint(castExpr); s.append(")") }

            case UnaryOpExpr(kind, castExpr) => { s.append("("); s.append(kind); s.append(" "); prettyPrint(castExpr); s.append(")") }
            case NAryExpr(e, others) => { s.append("("); prettyPrint(e); s.append(" "); sep(others, " "); s.append(")") }
            case NArySubExpr(op: String, e: Expr) => { s.append(op); s.append(" "); prettyPrint(e) }
            case ConditionalExpr(condition: Expr, thenExpr, elseExpr: Expr) => {
              s.append("("); prettyPrint(condition); s.append(" ? "); opt(thenExpr); s.append(" : "); prettyPrint(elseExpr); s.append(")")
            }
            case AssignExpr(target: Expr, operation: String, source: Expr) => {
              s.append("(")
              prettyPrint(target)
              s.append(" ")
              s.append(operation)
              s.append(" ")
              prettyPrint(source)
              s.append(")")
            }
            case ExprList(exprs) => sep(exprs, " , ")

            case CompoundStatement(innerStatements) => {
              s.append("{\n")
              sep(innerStatements, "\n")
              s.append("\n")
              s.append("}")
            }
            case EmptyStatement() => s.append(";")
            case ExprStatement(expr: Expr) => { prettyPrint(expr); s.append(";") }
            case WhileStatement(expr: Expr, sb) => {s.append("while ("); prettyPrint(expr); s.append(") "); prettyCond(sb) }
            case DoStatement(expr: Expr, sb) => { s.append("do "); prettyCond(sb); s.append(" while ("); prettyPrint(expr); s.append(")") }
            case ForStatement(expr1, expr2, expr3, sb) => {
              s.append("for (")
              opt(expr1)
              s.append("; ")
              opt(expr2)
              s.append("; ")
              opt(expr3)
              s.append(") ")
              prettyCond(sb)
            }
            case GotoStatement(target) => { s.append("goto "); prettyPrint(target); s.append(";") }
            case ContinueStatement() => s.append("continue;")
            case BreakStatement() => s.append("break;")
            case ReturnStatement(None) => s.append("return;")
            case ReturnStatement(Some(e)) => { s.append("return "); prettyPrint(e); s.append(";") }
            case LabelStatement(id: Id, _) => { prettyPrint(id); s.append(":") }
            case CaseStatement(c: Expr) => { s.append("case "); prettyPrint(c); s.append(":") }
            case DefaultStatement() => s.append("default:")
            case IfStatement(condition, thenBranch, elifs, elseBranch) => {
              s.append("if (")
              prettyCond(condition)
              s.append(") ")
              prettyCond(thenBranch)
              s.append(" ")
              sep(elifs, "\n")
              s.append(" ")
              if (elseBranch.isDefined) {
                s.append(" else ")
                prettyCond(elseBranch.get)
              }
            }
            case ElifStatement(condition, thenBranch) => {
              s.append("\n else if (")
              prettyCond(condition)
              s.append(") ")
              prettyCond(thenBranch)
            }
            case SwitchStatement(expr, sb) => {
              s.append("switch (")
              prettyPrint(expr)
              s.append(") ")
              prettyCond(sb)
            }
            case DeclarationStatement(decl: Declaration) => prettyPrint(decl)
            case NestedFunctionDef(isAuto, specifiers, declarator, parameters, stmt) => {
              if (isAuto) s.append("auto ")
              sep(specifiers, " ")
              s.append(" ")
              prettyPrint(declarator)
              s.append(" ")
              sep(parameters, " ")
              s.append(" ")
              prettyPrint(stmt)
            }
            case LocalLabelDeclaration(ids) => {
              s.append("__label__ ")
              sep(ids, ", ")
              s.append(";")
            }
            case OtherPrimitiveTypeSpecifier(typeName: String) => s.append(typeName)
            case VoidSpecifier() => s.append("void")
            case ShortSpecifier() => s.append("short")
            case IntSpecifier() => s.append("int")
            case FloatSpecifier() => s.append("float")
            case LongSpecifier() => s.append("long")
            case CharSpecifier() => s.append("char")
            case DoubleSpecifier() => s.append("double")

            case TypedefSpecifier() => s.append("typedef")
            case TypeDefTypeSpecifier(name: Id) => prettyPrint(name)
            case SignedSpecifier() => s.append("signed")
            case UnsignedSpecifier() => s.append("unsigned")

            case InlineSpecifier() => s.append("inline")
            case AutoSpecifier() => s.append("auto")
            case RegisterSpecifier() => s.append("register")
            case VolatileSpecifier() => s.append("volatile")
            case ExternSpecifier() => s.append("extern")
            case ConstSpecifier() => s.append("const")
            case RestrictSpecifier() => s.append("restrict")
            case StaticSpecifier() => s.append("static")

            case AtomicAttribute(n: String) => s.append(n)
            case AttributeSequence(attributes) => sep(attributes, " ")
            case CompoundAttribute(inner) => { s.append("("); sep(inner, ", "); s.append(")") }

            case Declaration(declSpecs, init) => {
              sep(declSpecs, " ")
              s.append(" ")
              commaSep(init)
              s.append(";")
            }


            case InitDeclaratorI(declarator, _, Some(i)) => { prettyPrint(declarator); s.append(" = "); prettyPrint(i) }
            case InitDeclaratorI(declarator, _, None) => prettyPrint(declarator)
            case InitDeclaratorE(declarator, _, e: Expr) => { prettyPrint(declarator); s.append(": "); prettyPrint(e) }

            case AtomicNamedDeclarator(pointers, id, extensions) => {
              sep(pointers, "")
              prettyPrint(id)
              sep(extensions, "")
            }
            case NestedNamedDeclarator(pointers, nestedDecl, extensions) => {
              sep(pointers, "")
              s.append("(")
              prettyPrint(nestedDecl)
              s.append(")")
              sep(extensions, "")
            }
            case AtomicAbstractDeclarator(pointers, extensions) => {
              sep(pointers, "")
              sep(extensions, "")
            }

            case NestedAbstractDeclarator(pointers, nestedDecl, extensions) => {
              sep(pointers, "")
              s.append("(")
              prettyPrint(nestedDecl)
              s.append(")")
              sep(extensions, "")
            }

            case DeclIdentifierList(idList) => {
              s.append("(")
              commaSep(idList)
              s.append(")")
            }
            case DeclParameterDeclList(parameterDecls) => {
              s.append("(")
              commaSep(parameterDecls)
              s.append(")")
            }
            case DeclArrayAccess(expr) => {
              s.append("[")
              opt(expr)
              s.append("]")
            }
            case Initializer(initializerElementLabel, expr: Expr) => {
              opt(initializerElementLabel)
              s.append(" ")
              prettyPrint(expr)
            }
            case Pointer(specifier) => {
              s.append("*")
              spaceSep(specifier)
            }
            case PlainParameterDeclaration(specifiers) => spaceSep(specifiers)
            case ParameterDeclarationD(specifiers, decl) => {
              spaceSep(specifiers)
              s.append(" ")
              prettyPrint(decl)
            }
            case ParameterDeclarationAD(specifiers, decl) => {
              spaceSep(specifiers)
              prettyPrint(decl)
            }
            case VarArgs() => s.append("...")
            case EnumSpecifier(id, Some(enums)) => {
              s.append("enum ")
              opt(id)
              s.append(" { ")
              sep(enums, ", ")
              s.append("}")
            }
            case EnumSpecifier(Some(id), None) => { s.append("enum "); prettyPrint(id) }
            case Enumerator(id, Some(init)) => { prettyPrint(id); s.append(" = "); prettyPrint(init) }
            case Enumerator(id, None) => prettyPrint(id)
            case StructOrUnionSpecifier(isUnion, id, enumerators) => { s.append((if (isUnion) "union " else "struct "))
              opt(id)
              if (enumerators.isDefined) {
                s.append(" {")
                sep(enumerators.get, "\n")
                s.append("\n}")
              }
            }
            case StructDeclaration(qualifierList, declaratorList) => {
              spaceSep(qualifierList)
              s.append(" ")
              commaSep(declaratorList)
              s.append(";")
            }
            case StructDeclarator(decl, initializer, _) => {
              prettyPrint(decl)
              optExt(initializer, ": ")
            }
            case StructInitializer(expr, _) => { s.append(": "); prettyPrint(expr) }
            case AsmExpr(isVolatile, expr) => {
              s.append("asm ")
              s.append(if (isVolatile) "volatile) " else "")
              s.append("{")
              prettyPrint(expr)
              s.append("};")
            }
            case FunctionDef(specifiers, declarator, oldStyleParameters, stmt) => {
              spaceSep(specifiers)
              s.append(" ")
              prettyPrint(declarator)
              s.append(" ")
              spaceSep(oldStyleParameters)
              s.append(" ")
              prettyPrint(stmt)
            }
            case EmptyExternalDef() => s.append(";")
            case TypelessDeclaration(declList) => { commaSep(declList); s.append(";") }
            case TypeName(specifiers, decl) => { spaceSep(specifiers); s.append(" "); opt(decl) }

            case GnuAttributeSpecifier(attributeList) => { s.append("__attribute__(("); commaSep(attributeList); s.append("))") }
            case AsmAttributeSpecifier(stringConst) => prettyPrint(stringConst)
            case LcurlyInitializer(inits) => { s.append("{"); commaSep(inits); s.append("}") }
            case AlignOfExprT(typeName: TypeName) => { s.append("__alignof__("); prettyPrint(typeName); s.append(")") }
            case AlignOfExprU(expr: Expr) => { s.append("__alignof__ "); prettyPrint(expr) }
            case GnuAsmExpr(isVolatile: Boolean, isAuto, expr: StringLit, stuff: Any) => s.append("asm")
            case RangeExpr(from: Expr, to: Expr) => { prettyPrint(from); s.append(" ... "); prettyPrint(to) }
            case TypeOfSpecifierT(typeName: TypeName) => { s.append("typeof("); prettyPrint(typeName); s.append(")") }
            case TypeOfSpecifierU(e: Expr) => { s.append("typeof("); prettyPrint(e); s.append(")") }
            case InitializerArrayDesignator(expr: Expr) => { s.append("["); prettyPrint(expr); s.append("]") }
            case InitializerDesignatorD(id: Id) => { s.append("."); prettyPrint(id) }
            case InitializerDesignatorC(id: Id) => { prettyPrint(id); s.append(":") }
            case InitializerAssigment(desgs) => { spaceSep(desgs); s.append(" =") }
            case BuiltinOffsetof(typeName: TypeName, offsetofMemberDesignator) => { s.append("__builtin_offsetof("); prettyPrint(typeName); s.append(", "); spaceSep(offsetofMemberDesignator); s.append(")") }
            case OffsetofMemberDesignatorID(id: Id) => { s.append("."); prettyPrint(id) }
            case OffsetofMemberDesignatorExpr(expr: Expr) => { s.append("["); prettyPrint(expr); s.append("]") }
            case BuiltinTypesCompatible(typeName1: TypeName, typeName2: TypeName) => { s.append("__builtin_types_compatible_p("); prettyPrint(typeName1); s.append(", "); prettyPrint(typeName2); s.append(")") }
            case BuiltinVaArgs(expr: Expr, typeName: TypeName) => { s.append("__builtin_va_arg("); prettyPrint(expr); s.append(", "); prettyPrint(typeName); s.append(")") }
            case CompoundStatementExpr(compoundStatement: CompoundStatement) => { s.append("("); prettyPrint(compoundStatement); s.append(")") }
            case Pragma(command: StringLit) => { s.append("_Pragma("); prettyPrint(command); s.append(")") }

            case e => assert(assertion = false, message = "match not exhaustive: " + e); s.append("")
        }
    }
}