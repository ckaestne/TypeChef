package de.fosd.typechef.parser.c

import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import java.io.{FileWriter, StringWriter, Writer}

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

    /**
      * Determines whether doc has some content other than spaces.
      */
    def hasContent(doc: Doc): Boolean = {
        doc match {
            case Empty           => false
            case Text(s: String) => !s.forall(_.isWhitespace)
            case Cons(a, b)      => hasContent(a) || hasContent(b)
            case _               => true
        }
    }

    val line = Line
    val space = Text(" ")
    var newLineForIfdefs = true

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

    // old version causing stack overflows and pretty slow
    //def print(ast: AST): String = layout(prettyPrint(ast))
    // new awesome fast version using a string writer instance
    def print(ast: AST): String = printW(ast, new StringWriter()).toString

    def layoutW(d: Doc, p: Writer): Unit = d match {
        case Empty => p.write("")
        case Line => p.write("\n")
        case Text(s) => p.write(s)
        case Cons(l, r) =>
            layoutW(l, p)
            layoutW(r, p)
        case Nest(n, Empty) => layoutW(Empty, p)
        case Nest(n, Line) => p.write("\n" + (" " * n))
        case Nest(n, Text(s)) => layoutW(Text(s), p)
        case Nest(n, Cons(l, r)) => layoutW(Cons(Nest(n, l), Nest(n, r)), p)
        case Nest(i, Nest(j, x)) => layoutW(Nest(i + j, x), p)
        case _ =>
    }

    def printW(ast: AST, writer: Writer): Writer = {
        layoutW(prettyPrint(ast), writer)
        writer
    }

    def ppConditional(e: Conditional[_], list_feature_expr: List[FeatureExpr]): Doc = e match {
        case One(c: AST) => prettyPrint(c, list_feature_expr)
        case Choice(f, a: AST, b: AST) =>
            if (newLineForIfdefs) {
                line ~
                    "#if" ~~ f.toTextExpr *
                    prettyPrint(a, f :: list_feature_expr) *
                    "#else" *
                    prettyPrint(b, f.not :: list_feature_expr) *
                    "#endif" ~
                        line
            } else {
                "#if" ~~ f.toTextExpr *
                    prettyPrint(a, f :: list_feature_expr) *
                    "#else" *
                    prettyPrint(b, f.not :: list_feature_expr) *
                    "#endif"
            }

        case Choice(f, a: Conditional[_], b: Conditional[_]) =>
            if (newLineForIfdefs) {
                line ~
                    "#if" ~~ f.toTextExpr *
                    ppConditional(a, f :: list_feature_expr) *
                    "#else" *
                    ppConditional(b, f.not :: list_feature_expr) *
                    "#endif" ~
                        line
            } else {
                "#if" ~~ f.toTextExpr *
                    ppConditional(a, f :: list_feature_expr) *
                    "#else" *
                    ppConditional(b, f.not :: list_feature_expr) *
                    "#endif"
            }
    }

    private def optConditional(e: Opt[AST], list_feature_expr: List[FeatureExpr]): Doc = {
        if (e.condition == FeatureExprFactory.True ||
            list_feature_expr.foldLeft(FeatureExprFactory.True)(_ and _).implies(e.condition).isTautology())
            prettyPrint(e.entry, list_feature_expr)
        else if (newLineForIfdefs) {
            line ~
                "#if" ~~ e.condition.toTextExpr *
                prettyPrint(e.entry, e.condition :: list_feature_expr) *
                "#endif" ~
                    line
        } else {
            "#if" ~~ e.condition.toTextExpr *
                prettyPrint(e.entry, e.condition :: list_feature_expr) *
                "#endif"
        }

    }

    private def optConditionalStr(e: Opt[String], list_feature_expr: List[FeatureExpr]): Doc = {
        if (e.condition == FeatureExprFactory.True ||
            list_feature_expr.foldLeft(FeatureExprFactory.True)(_ and _).implies(e.condition).isTautology())
            e.entry
        else if (newLineForIfdefs) {
            line ~
                "#if" ~~ e.condition.toTextExpr *
                e.entry *
                "#endif" ~
                    line
        } else {
            "#if" ~~ e.condition.toTextExpr *
                e.entry *
                "#endif"
        }

    }

    def prettyPrint(ast: AST, list_feature_expr: List[FeatureExpr] = List(FeatureExprFactory.True)): Doc = {
        implicit def pretty(a: AST): Doc = prettyPrint(a, list_feature_expr)
        implicit def prettyOpt(a: Opt[AST]): Doc = optConditional(a, list_feature_expr)
        implicit def prettyCond(a: Conditional[_]): Doc = ppConditional(a, list_feature_expr)
        implicit def prettyOptStr(a: Opt[String]): Doc = optConditionalStr(a, list_feature_expr)

        // Variability-aware version of sep. Annotates sep with an #ifdef computed from
        // two Opt nodes of subsequent elements of l.
        // e.g.,
        // void foo( int a
        // #ifdef B
        // , int B
        // #endif
        // ) {}
        // the standard sep function prints out the comma between both parameters without an
        // annotation. Further processing of the output will lead to an error.
        // This function prints out separated lists with annotated commas solving that problem.
        def sepVaware[T](l: List[Opt[T]], selem: String, toDoc: Opt[T] => Doc, breakselem: Doc = space) = {
            var res: Doc = if (l.isEmpty) Empty else toDoc(l.head)
            var combCtx: FeatureExpr = if (l.isEmpty) FeatureExprFactory.True else l.head.condition

            for (celem <- l.drop(1)) {
                val selemfexp = combCtx.and(celem.condition)

                // separation element is never present
                if (selemfexp.isContradiction())
                    res = res ~ breakselem ~ toDoc(celem)

                // separation element is always present
                else if (selemfexp.isTautology())
                    res = res ~ selem ~ breakselem ~ toDoc(celem)

                // separation element is sometimes present
                else {
                    if (hasContent(selem))
                        res = res * "#if" ~~ selemfexp.toTextExpr * selem * "#endif" * toDoc(celem)
                    else
                        res = res * toDoc(celem)
                }

                // add current feature expression as it might influence the addition of selem for
                // the remaining elements of l
                combCtx = combCtx.or(celem.condition)
            }

            res
        }


        def sep(l: List[Opt[AST]], s: (Doc, Doc) => Doc) = {
            val r: Doc = if (l.isEmpty) Empty else l.head
            l.drop(1).foldLeft(r)((a, b) => s(a, prettyOpt(b)))
        }
        def seps(l: List[Opt[String]], s: (Doc, Doc) => Doc) = {
            val r: Doc = if (l.isEmpty) Empty else l.head
            l.drop(1).foldLeft(r)(s(_, _))
        }
        def commaSep(l: List[Opt[AST]]) = sepVaware(l, ",", prettyOpt)
        def pointSep(l: List[Opt[AST]]) = sep(l, _ ~ "." ~ _)
        def spaceSep(l: List[Opt[AST]]) = sepVaware(l, " ", prettyOpt)
        def opt(o: Option[AST]): Doc = if (o.isDefined) o.get else Empty
        def optExt(o: Option[AST], ext: (Doc) => Doc): Doc = if (o.isDefined) ext(o.get) else Empty
        def optCondExt(o: Option[Conditional[AST]], ext: (Doc) => Doc): Doc = if (o.isDefined) ext(o.get) else Empty

        /**
         * generate source code from AST element generated by statement
         * opt(strOptExprPair ~ repOpt(COMMA ~> strOptExprPair))
         * in the parser. E.g. de.fosd.typechef.parser.c.CParser#gnuAsmExprWithGoto() (and withoutGoto)
         */
        def handleOptExprPairGroup(value: Any): Doc = {
            def handleOptExprPair(optExprPair: de.fosd.typechef.parser.~[Any, Any]): Doc = {
                optExprPair match {
                    case de.fosd.typechef.parser.~(de.fosd.typechef.parser.~(opt1, man2), opt2) =>
                        (if (opt1 != None) "[" ~ prettyPrint(opt1.asInstanceOf[Some[AST]].get) ~ "]" else Empty) ~
                            prettyPrint(man2.asInstanceOf[AST]) ~
                            (if (opt2 != None) "(" ~ prettyPrint(opt2.asInstanceOf[Some[AST]].get) ~ ")" else Empty)
                    case _ => sys.error("did not find a match for optional expression in GnuAsmExpr")
                }
            }
            def addOptionalOptExprPairToDoc(doc: Doc, optExprPair: Opt[de.fosd.typechef.parser.~[Any, Any]]): Doc = {
                val docForExprPair = handleOptExprPair(optExprPair.entry)
                if (!hasContent(docForExprPair))
                    doc
                else {
                    if (optExprPair.condition == FeatureExprFactory.True ||
                        list_feature_expr.foldLeft(FeatureExprFactory.True)(_ and _).implies(optExprPair.condition).isTautology()) {
                        doc ~ ", " ~ handleOptExprPair(optExprPair.entry)
                    } else {
                        if (newLineForIfdefs) {
                            line ~
                                doc * "#if" ~~ optExprPair.condition.toTextExpr *
                                ", " ~ docForExprPair *
                                "#endif" ~
                                    line
                        } else {

                            doc * "#if" ~~ optExprPair.condition.toTextExpr *
                                ", " ~ docForExprPair *
                                "#endif"
                        }
                    }
                }
            }
            value match {
                case None => Empty
                case Some(de.fosd.typechef.parser.~(mandatoryElement, optionalList)) =>
                    (optionalList.asInstanceOf[List[Opt[de.fosd.typechef.parser.~[Any, Any]]]].
                        foldLeft[Doc]
                        (handleOptExprPair(mandatoryElement.asInstanceOf[de.fosd.typechef.parser.~[Any, Any]])) // initial element (the mandatory)
                        (addOptionalOptExprPairToDoc)
                        )
                case _ => sys.error("did not find a match for optional expression in GnuAsmExpr")
            }
        }
        /**
         * generate source code from AST element generated by statement
         * rep1Sep(stringConst | ID, COMMA)
         * in the parser. E.g. de.fosd.typechef.parser.c.CParser#gnuAsmExprWithGoto() (and withoutGoto)
         * value : Option[List[Opt[AST]]]
         */
        def handleIdOrStringGroup(value: Any): Doc = {
            def addOptionalIdOrStringToDoc(a: Doc, b: Opt[AST]): Doc = {
                val docb = prettyOpt(b)
                if (docb == Empty)
                    a
                else
                    a ~ ", " ~ b
            }
            value match {
                case None => Empty
                case Some(_lst) =>
                    val lst=_lst.asInstanceOf[List[Opt[AST]]]
                    (lst.drop(1).
                        foldLeft[Doc]
                        (prettyOpt(lst.head.asInstanceOf[Opt[AST]]))
                        (addOptionalIdOrStringToDoc)
                    )
                case _ => sys.error("did not find a match for optional expression in GnuAsmExpr")
            }
        }

        ast match {
            case TranslationUnit(ext) => sep(ext, _ * _)
            case Id(name) => name
            case Constant(v) => v
            case StringLit(v) => sepVaware(v, "", prettyOptStr)
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
            case NAryExpr(e, others) => "(" ~ e ~~ sep(others, _ ~~ _) ~ ")"
            case NArySubExpr(op: String, e: Expr) => op ~~ e
            case ConditionalExpr(condition: Expr, thenExpr, elseExpr: Expr) => "(" ~ condition ~~ "?" ~~ opt(thenExpr) ~~ ":" ~~ elseExpr ~ ")"
            case AssignExpr(target: Expr, operation: String, source: Expr) => "(" ~ target ~~ operation ~~ source ~ ")"
            case ExprList(exprs) => sepVaware(exprs, ",", prettyOpt)

            case CompoundStatement(innerStatements) =>
                block(sep(innerStatements, _ * _))
            case EmptyStatement() => ";"
            case ExprStatement(expr: Expr) => expr ~ ";"
            case WhileStatement(expr: Expr, s) => "while (" ~ expr ~ ")" ~~ s
            case DoStatement(expr: Expr, s) => "do" ~~ s ~~ "while (" ~ expr ~ ")" ~ ";"
            case ForStatement(expr1, expr2, expr3, s) =>
                "for (" ~ opt(expr1) ~ ";" ~~ opt(expr2) ~ ";" ~~ opt(expr3) ~ ")" ~~ s
            case GotoStatement(target) => "goto" ~~ target ~ ";"
            case ContinueStatement() => "continue;"
            case BreakStatement() => "break;"
            case ReturnStatement(None) => "return;"
            case ReturnStatement(Some(e)) => "return" ~~ e ~ ";"
            case LabelStatement(id: Id, _) => id ~ ":"
            case CaseStatement(c: Expr) => "case" ~~ c ~ ":"
            case DefaultStatement() => "default:"
            case IfStatement(condition, thenBranch, elifs, elseBranch) =>
                "if (" ~ condition ~ ")" ~~ thenBranch ~~ sep(elifs, _ * _) ~~ optCondExt(elseBranch, line ~ "else" ~~ _)
            case ElifStatement(condition, thenBranch) => line ~ "else if (" ~ condition ~ ")" ~~ thenBranch
            case SwitchStatement(expr, s) => "switch (" ~ expr ~ ")" ~~ s
            case DeclarationStatement(decl: Declaration) => decl
            case NestedFunctionDef(isAuto, specifiers, declarator, parameters, stmt) =>
                (if (isAuto) "auto" ~~ Empty else Empty) ~ sep(specifiers, _ ~~ _) ~~ declarator ~~ sep(parameters, _ ~~ _) ~~ stmt
            case LocalLabelDeclaration(ids) => "__label__" ~~ sep(ids, _ ~ "," ~~ _) ~ ";"
            case OtherPrimitiveTypeSpecifier(typeName: String) => typeName
            case VoidSpecifier() => "void"
            case ShortSpecifier() => "short"
            case IntSpecifier() => "int"
            case FloatSpecifier() => "float"
            case LongSpecifier() => "long"
            case Int128Specifier() => "__int128"
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
            case RestrictSpecifier() => "__restrict"
            case StaticSpecifier() => "static"

            case AtomicAttribute(n: String) => n
            case AttributeSequence(attributes) => sep(attributes, _ ~~ _)
            case CompoundAttribute(inner) => "(" ~ sep(inner, _ ~ "," ~~ _) ~ ")"

            case Declaration(declSpecs, init) =>
                sep(declSpecs, _ ~~ _) ~~ sepVaware(init, ",", prettyOpt) ~ ";"

            case InitDeclaratorI(declarator, lst, Some(i)) =>
                if (!lst.isEmpty) {
                    declarator ~~ sep(lst, _ ~~ _) ~~ "=" ~~ i
                } else {
                    declarator ~~ "=" ~~ i
                }
            case InitDeclaratorI(declarator, lst, None) =>
                if (!lst.isEmpty) {
                    declarator ~~ sep(lst, _ ~~ _)
                } else {
                    declarator
                }
            case InitDeclaratorE(declarator, _, e: Expr) => declarator ~ ":" ~~ e

            case AtomicNamedDeclarator(pointers, id, extensions) =>
                sep(pointers, _ ~ _) ~ id ~ sep(extensions, _ ~ _)

            case NestedNamedDeclarator(pointers, nestedDecl, extensions, attr) =>
                sep(pointers, _ ~ _) ~ "(" ~ sep(attr, _ ~~ _) ~~ nestedDecl ~ ")" ~ sep(extensions, _ ~ _)
            case AtomicAbstractDeclarator(pointers, extensions) =>
                sep(pointers, _ ~ _) ~ sep(extensions, _ ~ _)
            case NestedAbstractDeclarator(pointers, nestedDecl, extensions, attr) =>
                sep(pointers, _ ~ _) ~ "(" ~ sep(attr, _ ~~ _) ~~ nestedDecl ~ ")" ~ sep(extensions, _ ~ _)

            case DeclIdentifierList(idList) => "(" ~ commaSep(idList) ~ ")"
            case DeclParameterDeclList(parameterDecls) => "(" ~ sepVaware(parameterDecls, ",", prettyOpt) ~ ")"
            case DeclArrayAccess(expr) => "[" ~ opt(expr) ~ "]"
            case Initializer(initializerElementLabel, expr: Expr) => opt(initializerElementLabel) ~~ expr
            case Pointer(specifier) =>
                if (specifier.isEmpty) {
                    "*" ~ spaceSep(specifier)
                } else {
                    "*" ~ spaceSep(specifier) ~ " "
                }
            case PlainParameterDeclaration(specifiers, attr) => spaceSep(specifiers) ~~ sep(attr, _ ~~ _)
            case ParameterDeclarationD(specifiers, decl, attr) => spaceSep(specifiers) ~~ decl ~~ sep(attr, _ ~~ _)
            case ParameterDeclarationAD(specifiers, decl, attr) => spaceSep(specifiers) ~~ decl ~~ sep(attr, _ ~~ _)
            case VarArgs() => "..."
            case EnumSpecifier(id, Some(enums)) => "enum" ~~ opt(id) ~~ block(sepVaware(enums, ",", prettyOpt, line))
            case EnumSpecifier(Some(id), None) => "enum" ~~ id
            case Enumerator(id, Some(init)) => id ~~ "=" ~~ init
            case Enumerator(id, None) => id
            case StructOrUnionSpecifier(isUnion, id, enumerators, attr1, attr2) =>
                (if (isUnion) "union" else "struct") ~~ sep(attr1, _ ~~ _) ~~ opt(id) ~~ (if (enumerators.isDefined) block(sep(enumerators.get, _ * _)) else Empty) ~~ sep(attr2, _ ~~ _)
            case StructDeclaration(qualifierList, declaratorList) => spaceSep(qualifierList) ~~ commaSep(declaratorList) ~ ";"
            case StructDeclarator(decl, initializer, attr) => decl ~ optExt(initializer, ":" ~~ _) ~~ spaceSep(attr)
            case StructInitializer(expr, attr) => ":" ~~ expr ~~ spaceSep(attr)
            case AsmExpr(isVolatile, expr) => "asm" ~~ (if (isVolatile) "volatile " else "") ~ "{" ~ expr ~ "}" ~ ";"
            case FunctionDef(specifiers, declarator, oldStyleParameters, stmt) =>
                spaceSep(specifiers) ~~ declarator ~~ spaceSep(oldStyleParameters) ~~ stmt
            case EmptyExternalDef() => ";"
            case TypelessDeclaration(declList) => commaSep(declList) ~ ";"
            case TypeName(specifiers, decl) => spaceSep(specifiers) ~~ opt(decl)

            case GnuAttributeSpecifier(attributeList) => "__attribute__((" ~ commaSep(attributeList) ~ "))"
            case AsmAttributeSpecifier(stringConst) => "__asm__( " ~ stringConst ~ ")"
            case LcurlyInitializer(inits) => "{" ~ commaSep(inits) ~ "}"
            case AlignOfExprT(typeName: TypeName) => "__alignof__(" ~ typeName ~ ")"
            case AlignOfExprU(expr: Expr) => "__alignof__" ~~ expr
            case GnuAsmExpr(isVolatile: Boolean, isGoto: Boolean, expr: StringLit, stuff) =>
                val ret =
                    stuff match {
                        // cf. de.fosd.typechef.parser.c.CParser.gnuAsmExpr
                        case Some(de.fosd.typechef.parser.~(
                        part1,
                        Some(de.fosd.typechef.parser.~(part21, part22)))) =>
                            val docPart1: Doc = handleOptExprPairGroup(part1)
                            val docPart21: Doc = handleOptExprPairGroup(part21)
                            val docPart22: Doc = handleIdOrStringGroup(part22)
                            val stuffDoc: Doc =
                                (if (!isGoto) ":" else "::") ~
                                    docPart1 ~
                                    ":" ~ docPart21 ~ // it seems we can always add this colons (even if the following asm-element docPart21 is Empty)
                                    (if (hasContent(docPart22)) ":" ~ docPart22 else Empty) // TypeChef parser complains if we add this colon when docPart22 is empty
                            "asm " ~
                                (if (isVolatile) "volatile " else "") ~
                                (if (isGoto) "goto " else "") ~
                                "(" ~ expr ~ stuffDoc ~ ")"
                        case _ =>
                            "asm " ~
                                (if (isVolatile) "volatile " else "") ~
                                (if (isGoto) "goto " else "") ~
                                "(" ~ expr ~ ")"
                    }
                ret
            case RangeExpr(from: Expr, to: Expr) => from ~~ "..." ~~ to
            case TypeOfSpecifierT(typeName: TypeName) => "typeof(" ~ typeName ~ ")"
            case TypeOfSpecifierU(e: Expr) => "typeof(" ~ e ~ ")"
            case InitializerArrayDesignator(expr: Expr) => "[" ~ expr ~ "]"
            case InitializerDesignatorD(id: Id) => "." ~ id
            case InitializerDesignatorC(id: Id) => id ~ ":"
            case InitializerAssigment(desgs) => spaceSep(desgs) ~~ "="
            case BuiltinOffsetof(typeName: TypeName, offsetofMemberDesignator) => "__builtin_offsetof(" ~ typeName ~ "," ~~ pointSep(offsetofMemberDesignator) ~ ")"
            case OffsetofMemberDesignatorID(id: Id) => id
            case OffsetofMemberDesignatorExpr(expr: Expr) => "[" ~ expr ~ "]"
            case BuiltinTypesCompatible(typeName1: TypeName, typeName2: TypeName) => "__builtin_types_compatible_p(" ~ typeName1 ~ "," ~~ typeName2 ~ ")"
            case BuiltinVaArgs(expr: Expr, typeName: TypeName) => "__builtin_va_arg(" ~ expr ~ "," ~~ typeName ~ ")"
            case CompoundStatementExpr(compoundStatement: CompoundStatement) => "(" ~ compoundStatement ~ ")"
            case Pragma(command: StringLit) => "_Pragma(" ~ command ~ ")"

            case e => assert(assertion = false, message = "match not exhaustive: " + e); ""
        }
    }


}