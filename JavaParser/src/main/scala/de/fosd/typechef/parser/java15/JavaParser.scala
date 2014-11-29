package de.fosd.typechef.parser.java15

import de.fosd.typechef.parser._
import de.fosd.typechef.parser.java15.lexer._
import de.fosd.typechef.conditional.{Opt, Conditional}

//
///*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// * based on GCIDE grammar in CIDE
// * which is again based on the publically available JavaCC grammar
// */
//
class JavaParser extends ConditionalParserLib {
    type Elem = TokenWrapper
    type TypeContext = Null

    ////grammar
    ///*****************************************
    // * THE JAVA LANGUAGE GRAMMAR STARTS HERE *
    // *****************************************/
    //
    ///*
    // * Program structuring syntax follows.
    // */
    //

    implicit def keyword(s: String): ConditionalParser[Elem] = token(s, _.getText == s)

    def CompilationUnit: ConditionalParser[Conditional[JCompilationUnit]] =
        PackageDeclaration ~ repOpt(ImportDeclaration) ~ repOpt(TypeDeclaration) ^^! {
            case p ~ imp ~ td => JCompilationUnit(p, imp, td)
        }

    def PackageDeclaration =
        opt("package" ~> Name <~ ";") ^^! {_ map JPackageDecl}

    def ImportDeclaration: ConditionalParser[JImport] =
        "import" ~> opt("static") ~ Name ~ opt("." ~ "*") <~ ";" ^^ {
            case static ~ name ~ dotStar => JImport(static.isDefined, name, dotStar.isDefined)
        }

    def Modifiers = repOpt(Modifier)

    def Modifier =
        (("public"
            |
            "static"
            |
            "protected"
            |
            "private"
            |
            "final"
            |
            "abstract"
            |
            "synchronized"
            |
            "native"
            |
            "transient"
            |
            "volatile"
            |
            "strictfp") ^^ {(t: Elem) => JAtomicModifier(t.getText)}
            |
            Annotation
            | fail("expected modifier"))

    def TypeDeclaration: ConditionalParser[JTypeDecl] =
        (";" ^^ {x => JEmptyTypeDecl()}
            | (Modifiers ~ ClassOrInterfaceDeclaration) ^^ {case mod ~ (coi ~ id ~ tp ~ el ~ il ~ body) => JClassOrInterfaceDecl(mod, coi.getText() == "interface", id, tp, el, il, body)}
            | (Modifiers ~ EnumDeclaration) ^^ {case mod ~ (id ~ implList ~ body) => JEnumDecl(mod, id, implList, body)}
            | (Modifiers ~ AnnotationTypeDeclaration) ^^ {case mod ~ cl => JAnnotationTypeDecl(mod, cl)}
            | fail("expected TypeDeclaration"))

    def ClassOrInterfaceDeclaration =
        ClassOrInterface ~ IDENTIFIER ~ optList(TypeParameters) ~ optList(ExtendsList) ~
            optList(ImplementsList) ~ ClassOrInterfaceBody

    def ClassOrInterface: ConditionalParser[Elem] = "class" | "interface"

    def ExtendsList: ConditionalParser[List[Opt[JClassOrInterfaceType]]] =
        "extends" ~> rep1Sep(ClassOrInterfaceType, ",")

    def ImplementsList: ConditionalParser[List[Opt[JClassOrInterfaceType]]] =
        "implements" ~> rep1Sep(ClassOrInterfaceType, ",")

    def EnumDeclaration =
        "enum" ~> IDENTIFIER ~
            optList(ImplementsList) ~
            EnumBody

    def EnumBody: ConditionalParser[Any] =
        "{" ~>
            rep1Sep(EnumConstant, ",") ~
            opt(EnumBodyInternal) <~
            "}"

    def EnumBodyInternal: ConditionalParser[Any] = ";" ~ repOpt(ClassOrInterfaceBodyDeclaration) !

    def EnumConstant: ConditionalParser[Any] =
        IDENTIFIER ~ opt(Arguments) ~ opt(ClassOrInterfaceBody) !

    def TypeParameters: ConditionalParser[List[Opt[Any]]] =
        "<" ~> rep1Sep(TypeParameter, ",") <~ ">"

    def TypeParameter: ConditionalParser[Any] =
        IDENTIFIER ~ opt(TypeBound) !

    def TypeBound =
        "extends" ~> rep1Sep(ClassOrInterfaceType, "&") !

    def ClassOrInterfaceBody: ConditionalParser[List[Opt[JBodyDeclaration]]] =
        "{" ~> repOpt(ClassOrInterfaceBodyDeclaration) <~ "}"

    def ClassOrInterfaceBodyDeclaration: ConditionalParser[JBodyDeclaration] =
        (Initializer
            | Modifiers ~ ClassOrInterfaceDeclaration ^^ {case mod ~ (coi ~ id ~ tp ~ el ~ il ~ body) => JClassOrInterfaceDecl(mod, coi.getText() == "interface", id, tp, el, il, body)}
            | Modifiers ~ EnumDeclaration ^^ {case mod ~ (id ~ implList ~ body) => JEnumDecl(mod, id, implList, body)}
            | Modifiers ~ ConstructorDeclaration ^^ {case mod ~ (tp ~ id ~ p ~ e ~ _ ~ sc ~ stmts ~ _) => JConstructorDecl(mod, tp, id, p, e, sc, stmts)}
            | Modifiers ~ FieldDeclaration ^^ {case mod ~ (t ~ vars) => JFieldDecl(mod, t, vars)}
            | Modifiers ~ MethodDeclaration ^^ {case mod ~ (tp ~ rt ~ (id ~ param ~ array) ~ ex ~ body) => JMethodDecl(mod, tp, rt, id, param, array.size, ex, body)}
            | ";" ^^ {t => JEmptyBodyDecl()})

    def FieldDeclaration =
        Type ~ rep1Sep(VariableDeclarator, ",") <~ ";"

    def VariableDeclarator: ConditionalParser[JVariableDeclarator] =
        VariableDeclaratorId ~ opt("=" ~> VariableInitializer) ^^ {case (id ~ a) ~ init => JVariableDeclarator(id, a.size, init)}

    def VariableDeclaratorId =
        IDENTIFIER ~ repPlain("[" ~ "]")

    def VariableInitializer: ConditionalParser[Any] =
        ArrayInitializer | Expression

    def ArrayInitializer: ConditionalParser[Any] =
        "{" ~> repSep(VariableInitializer, ",") <~ (opt(",") ~ "}") !

    def MethodDeclaration =
        optList(TypeParameters) ~
            ResultType ~
            MethodDeclarator ~ optList("throws" ~> NameList) ~
            MethodDeclarationBody

    def MethodDeclarationBody: ConditionalParser[Option[JBlock]] =
        Block ^^ {Some(_)} |
            ";" ^^ {t => None}

    def MethodDeclarator =
        IDENTIFIER ~ FormalParameters ~ repPlain("[" ~ "]")

    def FormalParameters: ConditionalParser[List[Opt[Any]]] =
        "(" ~> repSep(FormalParameter, ",") <~ ")"

    def FormalParameter: ConditionalParser[Any] =
        opt(Annotation) ~ opt("final") ~ Type ~ opt("...") ~ VariableDeclaratorId !

    def ConstructorDeclaration =
        optList(TypeParameters) ~ IDENTIFIER ~ FormalParameters ~ optList("throws" ~> NameList) ~ "{" ~ opt(ExplicitConstructorInvocation) ~ repOpt(BlockStatement) ~ "}"

    def ExplicitConstructorInvocation: ConditionalParser[Any] =
        ("this" ~ Arguments <~ ";") |
            ((opt(PrimaryExpression ~ ".") ~ "super" ~ Arguments <~ ";")) !

    def Initializer: ConditionalParser[JInitializer] =
        opt("static") ~ Block ^^ {case s ~ b => JInitializer(s.isDefined, b)}

    /*
    * Type, name and expression syntax follows.
    */

    def Type: ConditionalParser[JType] =
        (ReferenceTypeP | PrimitiveType) ^^ JType

    def ReferenceTypeP: ConditionalParser[Any] =
        (PrimitiveType ~ rep1("[" ~ "]")) |
            (ClassOrInterfaceType ~ repOpt("[" ~ "]")) !

    def ClassOrInterfaceType: ConditionalParser[JClassOrInterfaceType] =
        ParamType ~
            repPlain("." ~> ParamType) ^^ {case o ~ i => JClassOrInterfaceType(o, i)}

    private def ParamType = IDENTIFIER ~ opt(TypeArguments) ^^ {case id ~ ta => JParamType(id, ta)}

    def TypeArguments: ConditionalParser[Any] =
        "<" ~> rep1Sep(TypeArgument, ",") <~ ">" !

    def TypeArgument: ConditionalParser[Any] =
        ReferenceTypeP |
            ("?" ~ opt(WildcardBounds)) !

    def WildcardBounds: ConditionalParser[Any] =
        ("extends" ~ ReferenceTypeP) |
            ("super" ~ ReferenceTypeP) !

    def PrimitiveType: ConditionalParser[Any] =
        ("boolean"
            |
            "char"
            |
            "byte"
            |
            "short"
            |
            "int"
            |
            "long"
            |
            "float"
            |
            "double") !

    def ResultType: ConditionalParser[JType] =
        "void" ^^ {t => JType(t)} |
            Type

    def Name: ConditionalParser[JName] =
        rep1Sep(IDENTIFIER, ".") ^^ {JName(_)}

    def NameList: ConditionalParser[List[Opt[JName]]] = rep1Sep(Name, ",")

    /*
    * Expression syntax follows.
    */

    /*
    * This expansion has been written this way instead of:
    *   Assignment | ConditionalExpression
    * for performance reasons.
    * However, it is a weakening of the grammar for it allows the LHS of
    * assignments to be any conditional expression whereas it can only be
    * a primary expression.  Consider adding a semantic predicate to work
    * around this.
    */
    def Expression: ConditionalParser[Any] =
        ConditionalExpression ~ opt(AssignExp) !

    def AssignExp: ConditionalParser[Any] =
        AssignmentOperator ~ Expression !

    def AssignmentOperator: ConditionalParser[Any] =
        "=" | "*=" | "/=" | "%=" | "+=" | "-=" | "<<=" | ">>=" | ">>>=" | "&=" | "^=" | "|=" !

    def ConditionalExpression: ConditionalParser[Any] =
        ConditionalExpressionFull | ConditionalOrExpression !

    def ConditionalExpressionFull: ConditionalParser[Any] =
        ConditionalOrExpression <~ "?" ~ Expression <~ ":" ~ Expression !

    def ConditionalOrExpression: ConditionalParser[Any] =
        rep1Sep(ConditionalAndExpression, "||") !

    def ConditionalAndExpression: ConditionalParser[Any] =
        rep1Sep(InclusiveOrExpression, "&&") !

    def InclusiveOrExpression: ConditionalParser[Any] =
        rep1Sep(ExclusiveOrExpression, "|") !

    def ExclusiveOrExpression: ConditionalParser[Any] =
        rep1Sep(AndExpression, "^") !

    def AndExpression: ConditionalParser[Any] =
        rep1Sep(EqualityExpression, "&") !

    def EqualityExpression: ConditionalParser[Any] =
        rep1Sep(InstanceOfExpression, "==" | "!=") !

    def InstanceOfExpression: ConditionalParser[Any] =
        RelationalExpression ~ opt("instanceof" ~ Type) !

    def RelationalExpression: ConditionalParser[Any] =
        rep1Sep(ShiftExpression, "<" | ">" | "<=" | ">=") !

    def ShiftExpression: ConditionalParser[Any] =
        rep1Sep(AdditiveExpression, ShiftOp) !

    def ShiftOp: ConditionalParser[Any] = "<<" | ">" ~ ">" ~ opt(">") !

    def AdditiveExpression: ConditionalParser[Any] =
        rep1Sep(MultiplicativeExpression, AdditiveOp) !

    def MultiplicativeExpression: ConditionalParser[Any] =
        rep1Sep(UnaryExpression, "*" | "/" | "%") !

    def AdditiveOp: ConditionalParser[Any] = "+" | "-" !

    def UnaryExpression: ConditionalParser[Any] =
        ((AdditiveOp ~ UnaryExpression)
            |
            PreIncrementExpression
            |
            PreDecrementExpression
            |
            UnaryExpressionNotPlusMinus) !

    def PreIncrementExpression: ConditionalParser[Any] =
        "++" ~ PrimaryExpression !

    def PreDecrementExpression: ConditionalParser[Any] =
        "--" ~ PrimaryExpression !

    def UnaryExpressionNotPlusMinus: ConditionalParser[Any] =
        (UnaryOp ~ UnaryExpression
            |
            CastExpression
            |
            PostfixExpression) !

    def UnaryOp: ConditionalParser[Any] = "~" | "!" !

    def PostfixExpression: ConditionalParser[Any] =
        PrimaryExpression ~ opt(PostfixOp) !

    def PostfixOp: ConditionalParser[Any] = "++" | "--" !

    def CastExpression: ConditionalParser[Any] =
        ("(" ~> Type <~ ")" ~ UnaryExpression) |
            ("(" ~> Type <~ ")" ~ UnaryExpressionNotPlusMinus) !

    def PrimaryExpression: ConditionalParser[Any] =
        PrimaryPrefix ~ repOpt(PrimarySuffix) !

    def MemberSelector: ConditionalParser[Any] =
        "." ~ TypeArguments ~ IDENTIFIER !() named ("MemberSelector")

    def PrimaryPrefix: ConditionalParser[Any] =
        (Literal
            | ("this")
            | ("super" ~ "." ~ IDENTIFIER)
            | ("(" ~> Expression <~ ")")
            | (AllocationExpression)
            | (ResultType ~ "." ~ "class")
            | (Name)) !

    def PrimarySuffix: ConditionalParser[Any] =
        (("." ~ "this")
            | ("." ~ AllocationExpression)
            | (MemberSelector)
            | ("[" ~> Expression <~ "]")
            | ("." ~ IDENTIFIER)
            | (Arguments)) !

    def IDENTIFIER: ConditionalParser[JId] = token("<IDENTIFIER>",
        _.getKind == Java15ParserConstants.IDENTIFIER) ^^ {(t: Elem) => JId(t.getText())}

    def Literal: ConditionalParser[Any] =
        (token("<INTEGER_LITERAL>", _.getKind == Java15ParserConstants.INTEGER_LITERAL)
            |
            token("<FLOATING_POINT_LITERAL>", _.getKind == Java15ParserConstants.FLOATING_POINT_LITERAL)
            |
            token("<CHARACTER_LITERAL>", _.getKind == Java15ParserConstants.CHARACTER_LITERAL)
            |
            token("<STRING_LITERAL>", _.getKind == Java15ParserConstants.STRING_LITERAL)
            |
            BooleanLiteral
            |
            NullLiteral) !

    def BooleanLiteral: ConditionalParser[Any] =
        "true" | "false" !

    def NullLiteral: ConditionalParser[Any] =
        "null" !

    def Arguments: ConditionalParser[Any] =
        "(" ~> opt(ArgumentList) <~ ")" !

    def ArgumentList: ConditionalParser[Any] =
        rep1Sep(Expression, ",") !

    def AllocationExpression: ConditionalParser[Any] =
        ("new" ~ PrimitiveType ~ ArrayDimsAndInits) |
            ("new" ~ ClassOrInterfaceType ~ opt(TypeArguments) ~ AllocationExpressionInit) !

    def AllocationExpressionInit: ConditionalParser[Any] =
        ArrayDimsAndInits |
            (Arguments ~ opt(ClassOrInterfaceBody)) !

    /*
    * The third LOOK_AHEAD specification below is to parse to PrimarySuffix
    * if there is an expression between the "opt(...)".
    */
    def ArrayDimsAndInits: ConditionalParser[Any] =
        (rep1("[" ~ Expression ~ "]") ~ opt("[" ~ "]")) |
            (rep1("[" ~ "]") ~ ArrayInitializer) !

    /*
    * Statement syntax follows.
    */
    def Statement: ConditionalParser[Any] =
        (LabeledStatement
            |
            AssertStatement
            |
            Block
            |
            EmptyStatement
            | (StatementExpression <~ ";")
            |
            SwitchStatement
            |
            IfStatement
            |
            WhileStatement
            |
            DoStatement
            |
            ForStatement
            |
            BreakStatement
            |
            ContinueStatement
            |
            ReturnStatement
            |
            ThrowStatement
            |
            SynchronizedStatement
            |
            TryStatement | fail("expected Statement")) !() named ("Statement")

    def AssertStatement: ConditionalParser[Any] =
        "assert" ~ Expression ~ opt(":" ~ Expression) ~ ";" !

    def LabeledStatement: ConditionalParser[Any] =
        IDENTIFIER ~ ":" ~ Statement !() named ("LabeledStatement")

    def Block: ConditionalParser[JBlock] =
        "{" ~> repOpt(BlockStatement) <~ "}" ^^ JBlock

    def BlockStatement: ConditionalParser[Any] =
        (LocalVariableDeclaration <~ ";") |
            Statement |
            ClassOrInterfaceDeclaration !() named ("BlockStatement")

    def LocalVariableDeclaration: ConditionalParser[Any] =
        opt(Annotation) ~ opt("final") ~ Type ~ rep1Sep(VariableDeclarator, ",") !() named ("LocalVariableDeclaration")

    def EmptyStatement: ConditionalParser[Any] =
        ";" !() named ("EmptyStatement")

    /*
    * The last expansion of this production accepts more than the legal
    * Java expansions for StatementExpression.  This expansion does not
    * use PostfixExpression for performance reasons.
    */
    def StatementExpression: ConditionalParser[Any] =
        (PreIncrementExpression
            | PreDecrementExpression
            | (PrimaryExpression ~ opt(StatementExpressionAssignment))) !() named ("ExpressionStatement");

    def StatementExpressionAssignment: ConditionalParser[Any] =
        "++" |
            "--" |
            (AssignmentOperator ~ Expression) !() named ("StatementExpressionAssignment");

    def SwitchStatement: ConditionalParser[Any] =
        "switch" ~ "(" ~ Expression ~ ")" ~ "{" ~ repOpt(SwitchLabel ~ repOpt(BlockStatement)) ~ "}" !

    def SwitchLabel: ConditionalParser[Any] =
        ("case" ~ Expression ~ ":") |
            ("default" ~ ":") !;

    /*
    * The disambiguating algorithm of JavaCC automatically binds dangling
    * else's to the innermost if statement.  The LOOK_AHEAD specification
    * is to tell JavaCC that we know what we are doing.
    */
    def IfStatement: ConditionalParser[Any] =
        "if" ~ "(" ~ Expression ~ ")" ~ Statement ~ repOpt(ElIfStatement) ~ opt("else" ~ Statement) !;

    private def ElIfStatement: ConditionalParser[Any] =
        "else" ~ "if" ~! "(" ~ Expression ~ ")" ~ Statement !;

    def WhileStatement: ConditionalParser[Any] =
        "while" ~ "(" ~ Expression ~ ")" ~ Statement !;

    def DoStatement: ConditionalParser[Any] =
        "do" ~ Statement ~ "while" ~ "(" ~ Expression ~ ")" ~ ";" !

    def ForStatement: ConditionalParser[Any] =
        "for" ~ "(" ~ ForStatementInternal ~ ")" ~ Statement !

    def ForStatementInternal: ConditionalParser[Any] =
        (opt(Annotation) ~ opt("final") ~ Type ~ IDENTIFIER ~ ":" ~ Expression) |
            (opt(ForInit) ~ ";" ~ opt(Expression) ~ ";" ~ opt(ForUpdate)) !

    def ForInit: ConditionalParser[Any] =
        LocalVariableDeclaration |
            StatementExpressionList !;

    def StatementExpressionList: ConditionalParser[Any] =
        rep1Sep(StatementExpression, ",") !() named ("StatementExpressionList");

    def ForUpdate: ConditionalParser[Any] =
        StatementExpressionList !;

    def BreakStatement: ConditionalParser[Any] =
        "break" ~ opt(IDENTIFIER) ~ ";" !;

    def ContinueStatement: ConditionalParser[Any] =
        "continue" ~ opt(IDENTIFIER) ~ ";" !;

    def ReturnStatement: ConditionalParser[Any] =
        "return" ~ opt(Expression) ~ ";" !;

    def ThrowStatement: ConditionalParser[Any] =
        "throw" ~ Expression ~ ";" !;

    def SynchronizedStatement: ConditionalParser[Any] =
        "synchronized" ~ "(" ~ Expression ~ ")" ~ Block !;

    def TryStatement: ConditionalParser[Any] =
    /*
    * Semantic check required here to make sure that at least one
    * finally/catch is present.
    */
        "try" ~ Block ~ TryStatementEnd !

    def TryStatementEnd: ConditionalParser[Any] =
        repOpt(CatchBlock) ~ opt("finally" ~ Block) !;

    def CatchBlock: ConditionalParser[Any] =
        "catch" ~ "(" ~ FormalParameter ~ ")" ~ Block !;

    /* We use productions to match >>>, >> and > so that we can keep the
    * type declaration syntax with generics clean
    */

    /* Annotation syntax follows. */

    def Annotation: ConditionalParser[JAnnotation] =
        (NormalAnnotation |
            SingleMemberAnnotation |
            MarkerAnnotation) ^^ JAnnotation

    def NormalAnnotation: ConditionalParser[Any] =
        "@" ~ Name ~ "(" ~ opt(MemberValuePairs) ~ ")" !;

    def MarkerAnnotation: ConditionalParser[Any] =
        "@" ~ Name !;

    def SingleMemberAnnotation: ConditionalParser[Any] =
        "@" ~ Name ~ "(" ~ MemberValue ~ ")" !;

    def MemberValuePairs: ConditionalParser[Any] =
        rep1Sep(MemberValuePair, ",") !;

    def MemberValuePair: ConditionalParser[Any] =
        IDENTIFIER ~ "=" ~ MemberValue !;

    def MemberValue: ConditionalParser[Any] =
        Annotation |
            MemberValueArrayInitializer |
            ConditionalExpression !;

    def MemberValueArrayInitializer: ConditionalParser[Any] =
        "{" ~ rep1Sep(MemberValue, ",") ~ opt(",") ~ "}" !;

    /* Annotation Types. */

    def AnnotationTypeDeclaration: ConditionalParser[Any] =
        "@" ~ "interface" ~ IDENTIFIER ~ AnnotationTypeBody !;

    def AnnotationTypeBody: ConditionalParser[Any] =
        "{" ~ repOpt(AnnotationTypeMemberDeclaration) ~ "}" !;

    def AnnotationTypeMemberDeclaration: ConditionalParser[Any] =
        ((Modifiers ~ Type ~ IDENTIFIER ~ "(" ~ ")" ~ opt(DefaultValue) ~ ";")
            | (Modifiers ~ ClassOrInterfaceDeclaration)
            | (Modifiers ~ EnumDeclaration)
            | (Modifiers ~ AnnotationTypeDeclaration)
            | (Modifiers ~ FieldDeclaration)
            |
            ";") !

    def DefaultValue: ConditionalParser[Any] =
        "default" ~ MemberValue !

}
