package de.fosd.typechef.parser.c

import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}

object PrettyPrinter extends org.kiama.util.PrettyPrinter {

    def ppConditional(e: Conditional[_], list_feature_expr: List[FeatureExpr]): Doc = e match {
        case One(c: AST) => prettyPrint(c, list_feature_expr)
        case Choice(f, a: AST, b: AST) =>
          line <>
            text("#if") <+> text(f.toTextExpr) <@>
            prettyPrint(a, f :: list_feature_expr) <@>
            text("#else") <@>
            prettyPrint(b, f.not :: list_feature_expr) <@>
            text("#endif") <>
              line
        case Choice(f, a: Conditional[_], b: Conditional[_]) =>
          line <>
            text("#if") <+> text(f.toTextExpr) <@>
            ppConditional(a, f :: list_feature_expr) <@>
            text("#else") <@>
            ppConditional(b, f.not :: list_feature_expr) <@>
            text("#endif") <>
              line
    }

    def pretty(t: AST): String = super.pretty(prettyPrint(t))

    private def optConditional(e: Opt[AST], list_feature_expr: List[FeatureExpr]): Doc = {
        if (e.feature == FeatureExprFactory.True ||
          list_feature_expr.foldLeft(FeatureExprFactory.True)(_ and _).implies(e.feature).isTautology())
          prettyPrint(e.entry, list_feature_expr)
        else
          line <>
            text("#if") <+> text(e.feature.toTextExpr) <@>
            prettyPrint(e.entry, e.feature :: list_feature_expr) <@>
            text("#endif") <>
              line
    }

    def prettyPrint(ast: AST, list_feature_expr: List[FeatureExpr] = List(FeatureExprFactory.True)): Doc = {
        implicit def pretty(a: AST): Doc = prettyPrint(a, list_feature_expr)
        implicit def prettyOpt(a: Opt[AST]): Doc = optConditional(a, list_feature_expr)
        implicit def prettyCond(a: Conditional[_]): Doc = ppConditional(a, list_feature_expr)
        implicit def prettyOptStr(a: Opt[String]): Doc = string(a.entry)

        def sep(l: List[Opt[AST]], s: (Doc, Doc) => Doc) = {
            val r: Doc = if (l.isEmpty) empty else l.head
            l.drop(1).foldLeft(r)((a, b) => s(a, prettyOpt(b)))
        }
        def seps(l: List[Opt[String]], s: (Doc, Doc) => Doc) = {
            val r: Doc = if (l.isEmpty) empty else l.head
            l.drop(1).foldLeft(r)(s(_, _))
        }
        def commaSep(l: List[Opt[AST]]) = sep(l, _ <> text(",") <+> _)
        def spaceSep(l: List[Opt[AST]]) = sep(l, _ <+> _)
        def opt(o: Option[AST]): Doc = if (o.isDefined) o.get else empty
        def optExt(o: Option[AST], ext: (Doc) => Doc): Doc = if (o.isDefined) ext(o.get) else empty
        def optCondExt(o: Option[Conditional[AST]], ext: (Doc) => Doc): Doc = if (o.isDefined) ext(o.get) else empty

        ast match {
            case TranslationUnit(ext) => sep(ext, _ <@> _)
            case Id(name) => text(name)
            case Constant(v) => text(v)
            case StringLit(v) => seps(v, _ <+> _)
            case SimplePostfixSuffix(t) => text(t)
            case PointerPostfixSuffix(kind, id) => text(kind) <> id
            case FunctionCall(params) => text("(") <> params <> text(")")
            case ArrayAccess(e) => text("[") <> e <> text("]")
            case PostfixExpr(p, s) => p <> s
            case UnaryExpr(p, s) => text(p) <> s
            case SizeOfExprT(typeName) => text("sizeof(") <> typeName <> text(")")
            case SizeOfExprU(e) => text("sizeof(") <> e <> text(")")
            case CastExpr(typeName, expr) => text("((") <> typeName <> text(")") <+> expr <> text(")")

            case PointerDerefExpr(castExpr) => text("(*") <> castExpr <> text(")")
            case PointerCreationExpr(castExpr) => text("(&") <> castExpr <> text(")")

            case UnaryOpExpr(kind, castExpr) => text("(") <> text(kind) <+> castExpr <> text(")")
            case NAryExpr(e, others) => text("(") <> e <+> sep(others, _ <+> _) <> text(")")
            case NArySubExpr(op: String, e: Expr) => text(op) <+> e
            case ConditionalExpr(condition: Expr, thenExpr, elseExpr: Expr) => text("(") <> condition <+> text("?") <+> opt(thenExpr) <+> text(":") <+> elseExpr <> text(")")
            case AssignExpr(target: Expr, operation: String, source: Expr) => text("(") <> target <+> text(operation) <+> source <> text(")")
            case ExprList(exprs) => sep(exprs, _ <+> text(",") <+> _)

            case CompoundStatement(innerStatements) => text("{") <> sep(innerStatements, _ <@> _) <@> text("}")
            case EmptyStatement() => text(";")
            case ExprStatement(expr: Expr) => expr <> text(";")
            case WhileStatement(expr: Expr, s) => text("while (") <> expr <> text(")") <+> s
            case DoStatement(expr: Expr, s) => text("do") <+> s <+> text("while (") <> expr <> text(")")
            case ForStatement(expr1, expr2, expr3, s) =>
                text("for (") <> opt(expr1) <> text(";") <+> opt(expr2) <> text(";") <+> opt(expr3) <> text(")") <+> s
            case GotoStatement(target) => text("goto") <+> target <> text(";")
            case ContinueStatement() => text("continue;")
            case BreakStatement() => text("break;")
            case ReturnStatement(None) => text("return;")
            case ReturnStatement(Some(e)) => text("return") <+> e <> text(";")
            case LabelStatement(id: Id, _) => id <> text(":")
            case CaseStatement(c: Expr) => text("case") <+> c <> text(":")
            case DefaultStatement() => text("default:")
            case IfStatement(condition, thenBranch, elifs, elseBranch) =>
                text("if (") <> condition <> text(")") <+> thenBranch <+> sep(elifs, _ <@> _) <+> optCondExt(elseBranch, line <> text("else") <+> _)
            case ElifStatement(condition, thenBranch) => line <> text("else if (") <> condition <> text(")") <+> thenBranch
            case SwitchStatement(expr, s) => text("switch (") <> expr <> text(")") <+> s
            case DeclarationStatement(decl: Declaration) => decl
            case NestedFunctionDef(isAuto, specifiers, declarator, parameters, stmt) =>
                (if (isAuto) text("auto") <+> empty else empty) <> sep(specifiers, _ <+> _) <+> declarator <+> sep(parameters, _ <+> _) <+> stmt
            case LocalLabelDeclaration(ids) => text("__label__") <+> sep(ids, _ <> text(",") <+> _) <> text(";")
            case OtherPrimitiveTypeSpecifier(typeName: String) => text(typeName)
            case VoidSpecifier() => text("void")
            case ShortSpecifier() => text("short")
            case IntSpecifier() => text("int")
            case FloatSpecifier() => text("float")
            case LongSpecifier() => text("long")
            case CharSpecifier() => text("char")
            case DoubleSpecifier() => text("double")

            case TypedefSpecifier() => text("typedef")
            case TypeDefTypeSpecifier(name: Id) => name
            case SignedSpecifier() => text("signed")
            case UnsignedSpecifier() => text("unsigned")

            case InlineSpecifier() => text("inline")
            case AutoSpecifier() => text("auto")
            case RegisterSpecifier() => text("register")
            case VolatileSpecifier() => text("volatile")
            case ExternSpecifier() => text("extern")
            case ConstSpecifier() => text("const")
            case RestrictSpecifier() => text("restrict")
            case StaticSpecifier() => text("static")

            case AtomicAttribute(n: String) => text(n)
            case AttributeSequence(attributes) => sep(attributes, _ <+> _)
            case CompoundAttribute(inner) => text("(") <> sep(inner, _ <> text(",") <+> _) <> text(")")

            case Declaration(declSpecs, init) =>
                sep(declSpecs, _ <+> _) <+> commaSep(init) <> text(";")

            case InitDeclaratorI(declarator, _, Some(i)) => declarator <+> text("=") <+> i
            case InitDeclaratorI(declarator, _, None) => declarator
            case InitDeclaratorE(declarator, _, e: Expr) => declarator <> text(":") <+> e

            case AtomicNamedDeclarator(pointers, id, extensions) =>
                sep(pointers, _ <> _) <> id <> sep(extensions, _ <> _)
            case NestedNamedDeclarator(pointers, nestedDecl, extensions) =>
                sep(pointers, _ <> _) <> text("(") <> nestedDecl <> text(")") <> sep(extensions, _ <> _)
            case AtomicAbstractDeclarator(pointers, extensions) =>
                sep(pointers, _ <> _) <> sep(extensions, _ <> _)
            case NestedAbstractDeclarator(pointers, nestedDecl, extensions) =>
                sep(pointers, _ <> _) <> text("(") <> nestedDecl <> text(")") <> sep(extensions, _ <> _)

            case DeclIdentifierList(idList) => text("(") <> commaSep(idList) <> text(")")
            case DeclParameterDeclList(parameterDecls) => text("(") <> commaSep(parameterDecls) <> text(")")
            case DeclArrayAccess(expr) => text("[") <> opt(expr) <> text("]")
            case Initializer(initializerElementLabel, expr: Expr) => opt(initializerElementLabel) <+> expr
            case Pointer(specifier) => text("*") <> spaceSep(specifier)
            case PlainParameterDeclaration(specifiers) => spaceSep(specifiers)
            case ParameterDeclarationD(specifiers, decl) => spaceSep(specifiers) <+> decl
            case ParameterDeclarationAD(specifiers, decl) => spaceSep(specifiers) <+> decl
            case VarArgs() => text("...")
            case EnumSpecifier(id, Some(enums)) => text("enum") <+> opt(id) <+> text("{") <+> (sep(enums, _ <> text(",") <@> _))
            case EnumSpecifier(Some(id), None) => text("enum") <+> id
            case Enumerator(id, Some(init)) => id <+> text("=") <+> init
            case Enumerator(id, None) => id
            case StructOrUnionSpecifier(isUnion, id, enumerators) => text((if (isUnion) "union" else "struct")) <+> opt(id) <+> (if (enumerators.isDefined) text("{") <> sep(enumerators.get, _ <@> _) <@> text("}") else empty)
            case StructDeclaration(qualifierList, declaratorList) => spaceSep(qualifierList) <+> commaSep(declaratorList) <> text(";")
            case StructDeclarator(decl, initializer, _) => decl <> optExt(initializer, text(":") <+> _)
            case StructInitializer(expr, _) => text(":") <+> expr
            case AsmExpr(isVolatile, expr) => text("asm") <+> (if (isVolatile) text("volatile) ") else text("")) <> text("{") <> expr <> text("}") <> text(";")
            case FunctionDef(specifiers, declarator, oldStyleParameters, stmt) =>
                spaceSep(specifiers) <+> declarator <+> spaceSep(oldStyleParameters) <+> stmt
            case EmptyExternalDef() => text(";")
            case TypelessDeclaration(declList) => commaSep(declList) <> text(";")
            case TypeName(specifiers, decl) => spaceSep(specifiers) <+> opt(decl)

            case GnuAttributeSpecifier(attributeList) => text("__attribute__((") <> commaSep(attributeList) <> text("))")
            case AsmAttributeSpecifier(stringConst) => stringConst
            case LcurlyInitializer(inits) => text("{") <> commaSep(inits) <> text("}")
            case AlignOfExprT(typeName: TypeName) => text("__alignof__(") <> typeName <> text(")")
            case AlignOfExprU(expr: Expr) => text("__alignof__") <+> expr
            case GnuAsmExpr(isVolatile: Boolean, isAuto, expr: StringLit, stuff: Any) => text("asm")
            case RangeExpr(from: Expr, to: Expr) => from <+> text("...") <+> to
            case TypeOfSpecifierT(typeName: TypeName) => text("typeof(") <> typeName <> text(")")
            case TypeOfSpecifierU(e: Expr) => text("typeof(") <> e <> text(")")
            case InitializerArrayDesignator(expr: Expr) => text("[") <> expr <> text("]")
            case InitializerDesignatorD(id: Id) => text(".") <> id
            case InitializerDesignatorC(id: Id) => id <> text(":")
            case InitializerAssigment(desgs) => spaceSep(desgs) <+> text("=")
            case BuiltinOffsetof(typeName: TypeName, offsetofMemberDesignator) => text("__builtin_offsetof(") <> typeName <> text(",") <+> spaceSep(offsetofMemberDesignator) <> text(")")
            case OffsetofMemberDesignatorID(id: Id) => text(".") <> id
            case OffsetofMemberDesignatorExpr(expr: Expr) => text("[") <> expr <> text("]")
            case BuiltinTypesCompatible(typeName1: TypeName, typeName2: TypeName) => text("__builtin_types_compatible_p(") <> typeName1 <> text(",") <+> typeName2 <> text(")")
            case BuiltinVaArgs(expr: Expr, typeName: TypeName) => text("__builtin_va_arg(") <> expr <> text(",") <+> typeName <> text(")")
            case CompoundStatementExpr(compoundStatement: CompoundStatement) => text("(") <> compoundStatement <> text(")")
            case Pragma(command: StringLit) => text("_Pragma(") <> command <> text(")")

            case e => assert(assertion = false, message = "match not exhaustive: " + e); text("")
        }
    }


}