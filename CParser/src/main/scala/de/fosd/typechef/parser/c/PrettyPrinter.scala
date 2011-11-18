package de.fosd.typechef.parser.c

import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.FeatureExpr

object PrettyPrinter {

    //pretty printer combinators, stolen from http://www.scala-blogs.org/2009/04/combinators-for-pretty-printers-part-1.html
    sealed abstract class Doc {
        def ~(that: Doc) = Cons(this, that)
        def ~~(that: Doc) = this ~ space ~ that
        def *(that: Doc) = this ~ line ~ that
        def ~>(that: Doc) = this ~ nest(2, line ~ that)
    }

    case object Empty extends Doc

    case object Line extends Doc

    case class Text(s: String) extends Doc

    case class Cons(left: Doc, right: Doc) extends Doc

    case class Nest(n: Int, d: Doc) extends Doc

    implicit def string(s: String): Doc = Text(s)
    val line = Line
    val space = Text(" ")
    def nest(n: Int, d: Doc) = Nest(n, d)
    def block(d: Doc): Doc = "{" ~> d * "}"


    def layout(d: Doc): String = d match {
        case Empty => ""
        case Line => "\n"
        case Text(s) => s
        case Cons(l, r) => layout(l) + layout(r)
        case Nest(n, Empty) => layout(Empty)
        case Nest(n, Line) => "\n" + (" " * n)
        case Nest(n, Text(s)) => layout(Text(s))
        case Nest(n, Cons(l, r)) => layout(Cons(Nest(n, l), Nest(n, r)))
        case Nest(i, Nest(j, x)) => layout(Nest(i + j, x))
    }


    def print(ast: AST): String = layout(prettyPrint(ast))


    private def ppConditional(e: Conditional[AST]): Doc = e match {
        case One(c: AST) => prettyPrint(c)
        case Choice(f, a: AST, b: AST) => "#if" ~~ f.toTextExpr * prettyPrint(a) * "#else" * prettyPrint(b) * "#endif"
    }

    private def optConditional(e: Opt[AST]): Doc = {
        if (e.feature == FeatureExpr.base) prettyPrint(e.entry)
        else "#if" ~~ e.feature.toTextExpr * prettyPrint(e.entry) * "#endif"
    }

    def prettyPrint(ast: AST): Doc = {
        implicit def pretty(a: AST): Doc = prettyPrint(a)
        implicit def prettyOpt(a: Opt[AST]): Doc = optConditional(a)
        implicit def prettyCond(a: Conditional[AST]): Doc = ppConditional(a)
        implicit def prettyOptStr(a: Opt[String]): Doc = string(a.entry)

        def sep(l: List[Opt[AST]], s: (Doc, Doc) => Doc) = {
            val r: Doc = if (l.isEmpty) Empty else l.head
            l.drop(1).foldLeft(r)((a, b) => s(a, prettyOpt(b)))
        }
        def seps(l: List[Opt[String]], s: (Doc, Doc) => Doc) = {
            val r: Doc = if (l.isEmpty) Empty else l.head
            l.drop(1).foldLeft(r)(s(_, _))
        }
        def commaSep(l: List[Opt[AST]]) = sep(l, _ ~ "," ~~ _)
        def spaceSep(l: List[Opt[AST]]) = sep(l, _ ~~ _)
        def opt(o: Option[AST]): Doc = if (o.isDefined) o.get else Empty
        def optCond(o: Option[Conditional[AST]]): Doc = if (o.isDefined) o.get else Empty
        def optExt(o: Option[AST], ext: (Doc) => Doc): Doc = if (o.isDefined) ext(o.get) else Empty
        def optCondExt(o: Option[Conditional[AST]], ext: (Doc) => Doc): Doc = if (o.isDefined) ext(o.get) else Empty

        ast match {
            case TranslationUnit(ext) => sep(ext, _ * _)
            case Id(name) => name
            case Constant(v) => v
            case StringLit(v) => seps(v, _ ~~ _)
            case SimplePostfixSuffix(t) => t
            case PointerPostfixSuffix(kind, id) => kind ~ id
            case FunctionCall(params) => "(" ~ params ~ ")"
            case ArrayAccess(e) => "[" ~ e ~ "]"
            case PostfixExpr(p, s) => p ~ s
            case UnaryExpr(p, s) => p ~ s
            case SizeOfExprT(typeName) => "sizeof(" ~ typeName ~ ")"
            case SizeOfExprU(e) => "sizeof(" ~ e ~ ")"
            case CastExpr(typeName, expr) => "((" ~ typeName ~ ")" ~~ expr ~ ")"

            case PointerDerefExpr(castExpr) => "(*" ~ castExpr ~ ")"
            case PointerCreationExpr(castExpr) => "(&" ~ castExpr ~ ")"

            case UnaryOpExpr(kind, castExpr) => "(" ~ kind ~~ castExpr ~ ")"
            case NAryExpr(e: Expr, others: List[Opt[NArySubExpr]]) => "(" ~ e ~~ sep(others, _ ~~ _) ~ ")"
            case NArySubExpr(op: String, e: Expr) => op ~~ e
            case ConditionalExpr(condition: Expr, thenExpr, elseExpr: Expr) => "(" ~ condition ~~ "?" ~~ opt(thenExpr) ~~ ":" ~~ elseExpr ~ ")"
            case AssignExpr(target: Expr, operation: String, source: Expr) => "(" ~ target ~~ operation ~~ source ~ ")"
            case ExprList(exprs: List[Opt[Expr]]) => sep(exprs, _ ~~ "," ~~ _)

            case CompoundStatement(innerStatements: List[Opt[Statement]]) =>
                println(innerStatements)
                block(sep(innerStatements, _ * _))
            case EmptyStatement() => ";"
            case ExprStatement(expr: Expr) => expr ~ ";"
            case WhileStatement(expr: Expr, s: Conditional[Statement]) => "while (" ~ expr ~ ")" ~~ s
            case DoStatement(expr: Expr, s: Conditional[Statement]) => "do" ~~ s ~~ "while (" ~ expr ~ ")"
            case ForStatement(expr1: Option[Expr], expr2: Option[Expr], expr3: Option[Expr], s: Conditional[Statement]) =>
                "for (" ~ opt(expr1) ~ ";" ~~ opt(expr2) ~ ";" ~~ opt(expr3) ~ ")" ~~ s
            case GotoStatement(target: Expr) => "goto" ~~ target ~ ";"
            case ContinueStatement() => "continue;"
            case BreakStatement() => "break;"
            case ReturnStatement(None) => "return;"
            case ReturnStatement(Some(e)) => "return" ~~ e ~ ";"
            case LabelStatement(id: Id, _) => id ~ ":"
            case CaseStatement(c: Expr, s: Option[Conditional[Statement]]) => "case" ~~ c ~ ":" ~~ optCond(s)
            case DefaultStatement(s: Option[Conditional[Statement]]) => "default:" ~~ optCond(s)
            case IfStatement(condition: Expr, thenBranch: Conditional[Statement], elifs: List[Opt[ElifStatement]], elseBranch: Option[Conditional[Statement]]) =>
                "if (" ~ condition ~ ")" ~~ thenBranch ~~ sep(elifs, _ * _) ~~ optCondExt(elseBranch, line ~ "else" ~~ _)
            case ElifStatement(condition: Expr, thenBranch: Conditional[Statement]) => line ~ "else if (" ~ condition ~ ")" ~~ thenBranch
            case SwitchStatement(expr: Expr, s: Conditional[Statement]) => "switch (" ~ expr ~ ")" ~~ s
            case DeclarationStatement(decl: Declaration) => decl
            case NestedFunctionDef(isAuto: Boolean, specifiers: List[Opt[Specifier]], declarator: Declarator, parameters: List[Opt[Declaration]], stmt: CompoundStatement) =>
                (if (isAuto) "auto" ~~ Empty else Empty) ~ sep(specifiers, _ ~~ _) ~~ declarator ~~ sep(parameters, _ ~~ _) ~~ stmt
            case LocalLabelDeclaration(ids: List[Opt[Id]]) => "__label__" ~~ sep(ids, _ ~ "," ~~ _) ~ ";"
            case OtherPrimitiveTypeSpecifier(typeName: String) => typeName
            case VoidSpecifier() => "void"
            case ShortSpecifier() => "short"
            case IntSpecifier() => "int"
            case FloatSpecifier() => "float"
            case LongSpecifier() => "long"
            case CharSpecifier() => "char"
            case DoubleSpecifier() => "double"

            case TypedefSpecifier() => "typedef"
            case TypeDefTypeSpecifier(name: Id) => name
            case SignedSpecifier() => "signed"
            case UnsignedSpecifier() => "unsigned"

            case InlineSpecifier() => "inline"
            case AutoSpecifier() => "auto"
            case RegisterSpecifier() => "register"
            case VolatileSpecifier() => "volatile"
            case ExternSpecifier() => "extern"
            case ConstSpecifier() => "const"
            case RestrictSpecifier() => "restrict"
            case StaticSpecifier() => "static"

            case AtomicAttribute(n: String) => n
            case AttributeSequence(attributes: List[Opt[Attribute]]) => sep(attributes, _ ~~ _)
            case CompoundAttribute(inner: List[Opt[AttributeSequence]]) => "(" ~ sep(inner, _ ~ "," ~~ _) ~ ")"

            case Declaration(declSpecs: List[Opt[Specifier]], init: List[Opt[InitDeclarator]]) =>
                sep(declSpecs, _ ~~ _) ~~ commaSep(init) ~ ";"

            case InitDeclaratorI(declarator, _, Some(i)) => declarator ~~ "=" ~~ i
            case InitDeclaratorI(declarator, _, None) => declarator
            case InitDeclaratorE(declarator, _, e: Expr) => declarator ~ ":" ~~ e

            case AtomicNamedDeclarator(pointers: List[Opt[Pointer]], id: Id, extensions: List[Opt[DeclaratorExtension]]) =>
                sep(pointers, _ ~ _) ~ id ~ sep(extensions, _ ~ _)
            case NestedNamedDeclarator(pointers: List[Opt[Pointer]], nestedDecl: Declarator, extensions: List[Opt[DeclaratorExtension]]) =>
                sep(pointers, _ ~ _) ~ "(" ~ nestedDecl ~ ")" ~ sep(extensions, _ ~ _)
            case AtomicAbstractDeclarator(pointers: List[Opt[Pointer]], extensions: List[Opt[DeclaratorAbstrExtension]]) =>
                sep(pointers, _ ~ _) ~ sep(extensions, _ ~ _)
            case NestedAbstractDeclarator(pointers: List[Opt[Pointer]], nestedDecl: AbstractDeclarator, extensions: List[Opt[DeclaratorAbstrExtension]]) =>
                sep(pointers, _ ~ _) ~ "(" ~ nestedDecl ~ ")" ~ sep(extensions, _ ~ _)

            case DeclIdentifierList(idList: List[Opt[Id]]) => "(" ~ commaSep(idList) ~ ")"
            case DeclParameterDeclList(parameterDecls: List[Opt[ParameterDeclaration]]) => "(" ~ commaSep(parameterDecls) ~ ")"
            case DeclArrayAccess(expr: Option[Expr]) => "[" ~ opt(expr) ~ "]"
            case Initializer(initializerElementLabel, expr: Expr) => opt(initializerElementLabel) ~~ expr
            case Pointer(specifier: List[Opt[Specifier]]) => "*" ~ spaceSep(specifier)
            case PlainParameterDeclaration(specifiers: List[Opt[Specifier]]) => spaceSep(specifiers)
            case ParameterDeclarationD(specifiers: List[Opt[Specifier]], decl: Declarator) => spaceSep(specifiers) ~~ decl
            case ParameterDeclarationAD(specifiers, decl) => spaceSep(specifiers) ~~ decl
            case VarArgs() => "..."
            case EnumSpecifier(id, Some(enums)) => "enum" ~~ opt(id) ~~ block(sep(enums, _ ~ "," * _))
            case EnumSpecifier(Some(id), None) => "enum" ~~ id
            case Enumerator(id, Some(init)) => id ~~ "=" ~~ init
            case Enumerator(id, None) => id
            case StructOrUnionSpecifier(isUnion: Boolean, id: Option[Id], enumerators: List[Opt[StructDeclaration]]) => (if (isUnion) "union" else "struct") ~~ opt(id) ~~ block(sep(enumerators, _ * _))
            case StructDeclaration(qualifierList: List[Opt[Specifier]], declaratorList: List[Opt[StructDecl]]) => spaceSep(qualifierList) ~~ commaSep(declaratorList) ~ ";"
            case StructDeclarator(decl: Declarator, initializer: Option[Expr], _) => decl ~ optExt(initializer, ":" ~~ _)
            case StructInitializer(expr: Expr, _) => ":" ~~ expr
            case AsmExpr(isVolatile: Boolean, expr: Expr) => "asm" ~~ (if (isVolatile) "volatile " else "") ~ "{" ~ expr ~ "}" ~ ";"
            case FunctionDef(specifiers: List[Opt[Specifier]], declarator: Declarator, oldStyleParameters: List[Opt[OldParameterDeclaration]], stmt: CompoundStatement) =>
                spaceSep(specifiers) ~~ declarator ~~ spaceSep(oldStyleParameters) ~~ stmt
            case EmptyExternalDef() => ";"
            case TypelessDeclaration(declList: List[Opt[InitDeclarator]]) => commaSep(declList) ~ ";"
            case TypeName(specifiers: List[Opt[Specifier]], decl: Option[AbstractDeclarator]) => spaceSep(specifiers) ~~ opt(decl)

            case GnuAttributeSpecifier(attributeList: List[Opt[AttributeSequence]]) => "__attribute__((" ~ commaSep(attributeList) ~ "))"
            case AsmAttributeSpecifier(stringConst: StringLit) => stringConst
            case LcurlyInitializer(inits: List[Opt[Initializer]]) => "{" ~ commaSep(inits) ~ "}"
            case AlignOfExprT(typeName: TypeName) => "__alignof__(" ~ typeName ~ ")"
            case AlignOfExprU(expr: Expr) => "__alignof__" ~~ expr
            case GnuAsmExpr(isVolatile: Boolean, expr: StringLit, stuff: Any) => assert(false, "todo"); ""
            case RangeExpr(from: Expr, to: Expr) => from ~~ "..." ~~ to
            case TypeOfSpecifierT(typeName: TypeName) => "typeof(" ~ typeName ~ ")"
            case TypeOfSpecifierU(e: Expr) => "typeof(" ~ e ~ ")"
            case InitializerArrayDesignator(expr: Expr) => "[" ~ expr ~ "]"
            case InitializerDesignatorD(id: Id) => "." ~ id
            case InitializerDesignatorC(id: Id) => id ~ ":"
            case InitializerAssigment(desgs) => spaceSep(desgs) ~~ "="
            case BuiltinOffsetof(typeName: TypeName, offsetofMemberDesignator: List[Opt[OffsetofMemberDesignator]]) => "__builtin_offsetof(" ~ typeName ~ "," ~~ spaceSep(offsetofMemberDesignator) ~ ")"
            case OffsetofMemberDesignatorID(id: Id) => "." ~ id
            case OffsetofMemberDesignatorExpr(expr: Expr) => "[" ~ expr ~ "]"
            case BuiltinTypesCompatible(typeName1: TypeName, typeName2: TypeName) => "__builtin_types_compatible_p(" ~ typeName1 ~ "," ~~ typeName2 ~ ")"
            case BuiltinVaArgs(expr: Expr, typeName: TypeName) => "__builtin_va_arg(" ~ exit ~ "," ~~ typeName ~ ")"
            case CompoundStatementExpr(compoundStatement: CompoundStatement) => "(" ~ compoundStatement ~ ")"
            case Pragma(command: StringLit) => "_Pragma(" ~ command ~ ")"

            case e => assert(false, "match not exhaustive: " + e); ""
        }
    }


}