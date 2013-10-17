package de.fosd.typechef.parser.javascript

import de.fosd.typechef.parser._
import de.fosd.typechef.conditional.{Opt, Conditional}

//
///*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// * based on GCIDE grammar in CIDE
// * which is again based on the publically available JavaCC grammar
// */
//
class JSParser extends MultiFeatureParser {
    type Elem = JSToken
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

    implicit def keyword(s: String): MultiParser[Elem] = token(s, _.getText == s)

    def Program: MultiParser[Conditional[JSProgram]] =
        repOpt(SourceElement) ^^! { JSProgram(_) }

    def SourceElement: MultiParser[JSSourceElement] = "import" ^^ {null}

//
//    def PackageDeclaration =
//        opt("package" ~> Name <~ ";") ^^! {_ map JPackageDecl}
//
//    def ImportDeclaration: MultiParser[JImport] =
//        "import" ~> opt("static") ~ Name ~ opt("." ~ "*") <~ ";" ^^ {
//            case static ~ name ~ dotStar => JImport(static.isDefined, name, dotStar.isDefined)
//        }
//
//    def Modifiers = repOpt(Modifier)
//
//    def Modifier =
//        (("public"
//            |
//            "static"
//            |
//            "protected"
//            |
//            "private"
//            |
//            "final"
//            |
//            "abstract"
//            |
//            "synchronized"
//            |
//            "native"
//            |
//            "transient"
//            |
//            "volatile"
//            |
//            "strictfp") ^^ {(t: Elem) => JAtomicModifier(t.getText)}
//            |
//            Annotation
//            | fail("expected modifier"))
//
//    def TypeDeclaration: MultiParser[JTypeDecl] =
//        (";" ^^ {x => JEmptyTypeDecl()}
//            | (Modifiers ~ ClassOrInterfaceDeclaration) ^^ {case mod ~ (coi ~ id ~ tp ~ el ~ il ~ body) => JClassOrInterfaceDecl(mod, coi.getText() == "interface", id, tp, el, il, body)}
//            | (Modifiers ~ EnumDeclaration) ^^ {case mod ~ (id ~ implList ~ body) => JEnumDecl(mod, id, implList, body)}
//            | (Modifiers ~ AnnotationTypeDeclaration) ^^ {case mod ~ cl => JAnnotationTypeDecl(mod, cl)}
//            | fail("expected TypeDeclaration"))
//
//    def ClassOrInterfaceDeclaration =
//        ClassOrInterface ~ IDENTIFIER ~ optList(TypeParameters) ~ optList(ExtendsList) ~
//            optList(ImplementsList) ~ ClassOrInterfaceBody
//
//    def ClassOrInterface: MultiParser[Elem] = "class" | "interface"
//
//    def ExtendsList: MultiParser[List[Opt[JClassOrInterfaceType]]] =
//        "extends" ~> rep1Sep(ClassOrInterfaceType, ",")
//
//    def ImplementsList: MultiParser[List[Opt[JClassOrInterfaceType]]] =
//        "implements" ~> rep1Sep(ClassOrInterfaceType, ",")
//
//    def EnumDeclaration =
//        "enum" ~> IDENTIFIER ~
//            optList(ImplementsList) ~
//            EnumBody
//
//    def EnumBody: MultiParser[Any] =
//        "{" ~>
//            rep1Sep(EnumConstant, ",") ~
//            opt(EnumBodyInternal) <~
//            "}"
//
//    def EnumBodyInternal: MultiParser[Any] = ";" ~ repOpt(ClassOrInterfaceBodyDeclaration) !
//
//    def EnumConstant: MultiParser[Any] =
//        IDENTIFIER ~ opt(Arguments) ~ opt(ClassOrInterfaceBody) !
//
//    def TypeParameters: MultiParser[List[Opt[Any]]] =
//        "<" ~> rep1Sep(TypeParameter, ",") <~ ">"
//
//    def TypeParameter: MultiParser[Any] =
//        IDENTIFIER ~ opt(TypeBound) !
//
//    def TypeBound =
//        "extends" ~> rep1Sep(ClassOrInterfaceType, "&") !
//
//    def ClassOrInterfaceBody: MultiParser[List[Opt[JBodyDeclaration]]] =
//        "{" ~> repOpt(ClassOrInterfaceBodyDeclaration) <~ "}"
//
//    def ClassOrInterfaceBodyDeclaration: MultiParser[JBodyDeclaration] =
//        (Initializer
//            | Modifiers ~ ClassOrInterfaceDeclaration ^^ {case mod ~ (coi ~ id ~ tp ~ el ~ il ~ body) => JClassOrInterfaceDecl(mod, coi.getText() == "interface", id, tp, el, il, body)}
//            | Modifiers ~ EnumDeclaration ^^ {case mod ~ (id ~ implList ~ body) => JEnumDecl(mod, id, implList, body)}
//            | Modifiers ~ ConstructorDeclaration ^^ {case mod ~ (tp ~ id ~ p ~ e ~ _ ~ sc ~ stmts ~ _) => JConstructorDecl(mod, tp, id, p, e, sc, stmts)}
//            | Modifiers ~ FieldDeclaration ^^ {case mod ~ (t ~ vars) => JFieldDecl(mod, t, vars)}
//            | Modifiers ~ MethodDeclaration ^^ {case mod ~ (tp ~ rt ~ (id ~ param ~ array) ~ ex ~ body) => JMethodDecl(mod, tp, rt, id, param, array.size, ex, body)}
//            | ";" ^^ {t => JEmptyBodyDecl()})
//
//    def FieldDeclaration =
//        Type ~ rep1Sep(VariableDeclarator, ",") <~ ";"
//
//    def VariableDeclarator: MultiParser[JVariableDeclarator] =
//        VariableDeclaratorId ~ opt("=" ~> VariableInitializer) ^^ {case (id ~ a) ~ init => JVariableDeclarator(id, a.size, init)}
//
//    def VariableDeclaratorId =
//        IDENTIFIER ~ repPlain("[" ~ "]")
//
//    def VariableInitializer: MultiParser[Any] =
//        ArrayInitializer | Expression
//
//    def ArrayInitializer: MultiParser[Any] =
//        "{" ~> repSep(VariableInitializer, ",") <~ (opt(",") ~ "}") !
//
//    def MethodDeclaration =
//        optList(TypeParameters) ~
//            ResultType ~
//            MethodDeclarator ~ optList("throws" ~> NameList) ~
//            MethodDeclarationBody
//
//    def MethodDeclarationBody: MultiParser[Option[JBlock]] =
//        Block ^^ {Some(_)} |
//            ";" ^^ {t => None}
//
//    def MethodDeclarator =
//        IDENTIFIER ~ FormalParameters ~ repPlain("[" ~ "]")
//
//    def FormalParameters: MultiParser[List[Opt[Any]]] =
//        "(" ~> repSep(FormalParameter, ",") <~ ")"
//
//    def FormalParameter: MultiParser[Any] =
//        opt(Annotation) ~ opt("final") ~ Type ~ opt("...") ~ VariableDeclaratorId !
//
//    def ConstructorDeclaration =
//        optList(TypeParameters) ~ IDENTIFIER ~ FormalParameters ~ optList("throws" ~> NameList) ~ "{" ~ opt(ExplicitConstructorInvocation) ~ repOpt(BlockStatement) ~ "}"
//
//    def ExplicitConstructorInvocation: MultiParser[Any] =
//        ("this" ~ Arguments <~ ";") |
//            ((opt(PrimaryExpression ~ ".") ~ "super" ~ Arguments <~ ";")) !
//
//    def Initializer: MultiParser[JInitializer] =
//        opt("static") ~ Block ^^ {case s ~ b => JInitializer(s.isDefined, b)}
//
//    /*
//    * Type, name and expression syntax follows.
//    */
//
//    def Type: MultiParser[JType] =
//        (ReferenceTypeP | PrimitiveType) ^^ JType
//
//    def ReferenceTypeP: MultiParser[Any] =
//        (PrimitiveType ~ rep1("[" ~ "]")) |
//            (ClassOrInterfaceType ~ repOpt("[" ~ "]")) !
//
//    def ClassOrInterfaceType: MultiParser[JClassOrInterfaceType] =
//        ParamType ~
//            repPlain("." ~> ParamType) ^^ {case o ~ i => JClassOrInterfaceType(o, i)}
//
//    private def ParamType = IDENTIFIER ~ opt(TypeArguments) ^^ {case id ~ ta => JParamType(id, ta)}
//
//    def TypeArguments: MultiParser[Any] =
//        "<" ~> rep1Sep(TypeArgument, ",") <~ ">" !
//
//    def TypeArgument: MultiParser[Any] =
//        ReferenceTypeP |
//            ("?" ~ opt(WildcardBounds)) !
//
//    def WildcardBounds: MultiParser[Any] =
//        ("extends" ~ ReferenceTypeP) |
//            ("super" ~ ReferenceTypeP) !
//
//    def PrimitiveType: MultiParser[Any] =
//        ("boolean"
//            |
//            "char"
//            |
//            "byte"
//            |
//            "short"
//            |
//            "int"
//            |
//            "long"
//            |
//            "float"
//            |
//            "double") !
//
//    def ResultType: MultiParser[JType] =
//        "void" ^^ {t => JType(t)} |
//            Type
//
//    def Name: MultiParser[JName] =
//        rep1Sep(IDENTIFIER, ".") ^^ {JName(_)}
//
//    def NameList: MultiParser[List[Opt[JName]]] = rep1Sep(Name, ",")
//
//    /*
//    * Expression syntax follows.
//    */
//
//    /*
//    * This expansion has been written this way instead of:
//    *   Assignment | ConditionalExpression
//    * for performance reasons.
//    * However, it is a weakening of the grammar for it allows the LHS of
//    * assignments to be any conditional expression whereas it can only be
//    * a primary expression.  Consider adding a semantic predicate to work
//    * around this.
//    */
//    def Expression: MultiParser[Any] =
//        ConditionalExpression ~ opt(AssignExp) !
//
//    def AssignExp: MultiParser[Any] =
//        AssignmentOperator ~ Expression !
//
//    def AssignmentOperator: MultiParser[Any] =
//        "=" | "*=" | "/=" | "%=" | "+=" | "-=" | "<<=" | ">>=" | ">>>=" | "&=" | "^=" | "|=" !
//
//    def ConditionalExpression: MultiParser[Any] =
//        ConditionalExpressionFull | ConditionalOrExpression !
//
//    def ConditionalExpressionFull: MultiParser[Any] =
//        ConditionalOrExpression <~ "?" ~ Expression <~ ":" ~ Expression !
//
//    def ConditionalOrExpression: MultiParser[Any] =
//        rep1Sep(ConditionalAndExpression, "||") !
//
//    def ConditionalAndExpression: MultiParser[Any] =
//        rep1Sep(InclusiveOrExpression, "&&") !
//
//    def InclusiveOrExpression: MultiParser[Any] =
//        rep1Sep(ExclusiveOrExpression, "|") !
//
//    def ExclusiveOrExpression: MultiParser[Any] =
//        rep1Sep(AndExpression, "^") !
//
//    def AndExpression: MultiParser[Any] =
//        rep1Sep(EqualityExpression, "&") !
//
//    def EqualityExpression: MultiParser[Any] =
//        rep1Sep(InstanceOfExpression, "==" | "!=") !
//
//    def InstanceOfExpression: MultiParser[Any] =
//        RelationalExpression ~ opt("instanceof" ~ Type) !
//
//    def RelationalExpression: MultiParser[Any] =
//        rep1Sep(ShiftExpression, "<" | ">" | "<=" | ">=") !
//
//    def ShiftExpression: MultiParser[Any] =
//        rep1Sep(AdditiveExpression, ShiftOp) !
//
//    def ShiftOp: MultiParser[Any] = "<<" | ">" ~ ">" ~ opt(">") !
//
//    def AdditiveExpression: MultiParser[Any] =
//        rep1Sep(MultiplicativeExpression, AdditiveOp) !
//
//    def MultiplicativeExpression: MultiParser[Any] =
//        rep1Sep(UnaryExpression, "*" | "/" | "%") !
//
//    def AdditiveOp: MultiParser[Any] = "+" | "-" !
//
//    def UnaryExpression: MultiParser[Any] =
//        ((AdditiveOp ~ UnaryExpression)
//            |
//            PreIncrementExpression
//            |
//            PreDecrementExpression
//            |
//            UnaryExpressionNotPlusMinus) !
//
//    def PreIncrementExpression: MultiParser[Any] =
//        "++" ~ PrimaryExpression !
//
//    def PreDecrementExpression: MultiParser[Any] =
//        "--" ~ PrimaryExpression !
//
//    def UnaryExpressionNotPlusMinus: MultiParser[Any] =
//        (UnaryOp ~ UnaryExpression
//            |
//            CastExpression
//            |
//            PostfixExpression) !
//
//    def UnaryOp: MultiParser[Any] = "~" | "!" !
//
//    def PostfixExpression: MultiParser[Any] =
//        PrimaryExpression ~ opt(PostfixOp) !
//
//    def PostfixOp: MultiParser[Any] = "++" | "--" !
//
//    def CastExpression: MultiParser[Any] =
//        ("(" ~> Type <~ ")" ~ UnaryExpression) |
//            ("(" ~> Type <~ ")" ~ UnaryExpressionNotPlusMinus) !
//
//    def PrimaryExpression: MultiParser[Any] =
//        PrimaryPrefix ~ repOpt(PrimarySuffix) !
//
//    def MemberSelector: MultiParser[Any] =
//        "." ~ TypeArguments ~ IDENTIFIER !() named ("MemberSelector")
//
//    def PrimaryPrefix: MultiParser[Any] =
//        (Literal
//            | ("this")
//            | ("super" ~ "." ~ IDENTIFIER)
//            | ("(" ~> Expression <~ ")")
//            | (AllocationExpression)
//            | (ResultType ~ "." ~ "class")
//            | (Name)) !
//
//    def PrimarySuffix: MultiParser[Any] =
//        (("." ~ "this")
//            | ("." ~ AllocationExpression)
//            | (MemberSelector)
//            | ("[" ~> Expression <~ "]")
//            | ("." ~ IDENTIFIER)
//            | (Arguments)) !
//
//    def IDENTIFIER: MultiParser[JId] = token("<IDENTIFIER>",
//        _.getKind == Java15ParserConstants.IDENTIFIER) ^^ {(t: Elem) => JId(t.getText())}
//
//    def Literal: MultiParser[Any] =
//        (token("<INTEGER_LITERAL>", _.getKind == Java15ParserConstants.INTEGER_LITERAL)
//            |
//            token("<FLOATING_POINT_LITERAL>", _.getKind == Java15ParserConstants.FLOATING_POINT_LITERAL)
//            |
//            token("<CHARACTER_LITERAL>", _.getKind == Java15ParserConstants.CHARACTER_LITERAL)
//            |
//            token("<STRING_LITERAL>", _.getKind == Java15ParserConstants.STRING_LITERAL)
//            |
//            BooleanLiteral
//            |
//            NullLiteral) !
//
//    def BooleanLiteral: MultiParser[Any] =
//        "true" | "false" !
//
//    def NullLiteral: MultiParser[Any] =
//        "null" !
//
//    def Arguments: MultiParser[Any] =
//        "(" ~> opt(ArgumentList) <~ ")" !
//
//    def ArgumentList: MultiParser[Any] =
//        rep1Sep(Expression, ",") !
//
//    def AllocationExpression: MultiParser[Any] =
//        ("new" ~ PrimitiveType ~ ArrayDimsAndInits) |
//            ("new" ~ ClassOrInterfaceType ~ opt(TypeArguments) ~ AllocationExpressionInit) !
//
//    def AllocationExpressionInit: MultiParser[Any] =
//        ArrayDimsAndInits |
//            (Arguments ~ opt(ClassOrInterfaceBody)) !
//
//    /*
//    * The third LOOK_AHEAD specification below is to parse to PrimarySuffix
//    * if there is an expression between the "opt(...)".
//    */
//    def ArrayDimsAndInits: MultiParser[Any] =
//        (rep1("[" ~ Expression ~ "]") ~ opt("[" ~ "]")) |
//            (rep1("[" ~ "]") ~ ArrayInitializer) !
//
//    /*
//    * Statement syntax follows.
//    */
//    def Statement: MultiParser[Any] =
//        (LabeledStatement
//            |
//            AssertStatement
//            |
//            Block
//            |
//            EmptyStatement
//            | (StatementExpression <~ ";")
//            |
//            SwitchStatement
//            |
//            IfStatement
//            |
//            WhileStatement
//            |
//            DoStatement
//            |
//            ForStatement
//            |
//            BreakStatement
//            |
//            ContinueStatement
//            |
//            ReturnStatement
//            |
//            ThrowStatement
//            |
//            SynchronizedStatement
//            |
//            TryStatement | fail("expected Statement")) !() named ("Statement")
//
//    def AssertStatement: MultiParser[Any] =
//        "assert" ~ Expression ~ opt(":" ~ Expression) ~ ";" !
//
//    def LabeledStatement: MultiParser[Any] =
//        IDENTIFIER ~ ":" ~ Statement !() named ("LabeledStatement")
//
//    def Block: MultiParser[JBlock] =
//        "{" ~> repOpt(BlockStatement) <~ "}" ^^ JBlock
//
//    def BlockStatement: MultiParser[Any] =
//        (LocalVariableDeclaration <~ ";") |
//            Statement |
//            ClassOrInterfaceDeclaration !() named ("BlockStatement")
//
//    def LocalVariableDeclaration: MultiParser[Any] =
//        opt(Annotation) ~ opt("final") ~ Type ~ rep1Sep(VariableDeclarator, ",") !() named ("LocalVariableDeclaration")
//
//    def EmptyStatement: MultiParser[Any] =
//        ";" !() named ("EmptyStatement")
//
//    /*
//    * The last expansion of this production accepts more than the legal
//    * Java expansions for StatementExpression.  This expansion does not
//    * use PostfixExpression for performance reasons.
//    */
//    def StatementExpression: MultiParser[Any] =
//        (PreIncrementExpression
//            | PreDecrementExpression
//            | (PrimaryExpression ~ opt(StatementExpressionAssignment))) !() named ("ExpressionStatement");
//
//    def StatementExpressionAssignment: MultiParser[Any] =
//        "++" |
//            "--" |
//            (AssignmentOperator ~ Expression) !() named ("StatementExpressionAssignment");
//
//    def SwitchStatement: MultiParser[Any] =
//        "switch" ~ "(" ~ Expression ~ ")" ~ "{" ~ repOpt(SwitchLabel ~ repOpt(BlockStatement)) ~ "}" !
//
//    def SwitchLabel: MultiParser[Any] =
//        ("case" ~ Expression ~ ":") |
//            ("default" ~ ":") !;
//
//    /*
//    * The disambiguating algorithm of JavaCC automatically binds dangling
//    * else's to the innermost if statement.  The LOOK_AHEAD specification
//    * is to tell JavaCC that we know what we are doing.
//    */
//    def IfStatement: MultiParser[Any] =
//        "if" ~ "(" ~ Expression ~ ")" ~ Statement ~ repOpt(ElIfStatement) ~ opt("else" ~ Statement) !;
//
//    private def ElIfStatement: MultiParser[Any] =
//        "else" ~ "if" ~! "(" ~ Expression ~ ")" ~ Statement !;
//
//    def WhileStatement: MultiParser[Any] =
//        "while" ~ "(" ~ Expression ~ ")" ~ Statement !;
//
//    def DoStatement: MultiParser[Any] =
//        "do" ~ Statement ~ "while" ~ "(" ~ Expression ~ ")" ~ ";" !
//
//    def ForStatement: MultiParser[Any] =
//        "for" ~ "(" ~ ForStatementInternal ~ ")" ~ Statement !
//
//    def ForStatementInternal: MultiParser[Any] =
//        (opt(Annotation) ~ opt("final") ~ Type ~ IDENTIFIER ~ ":" ~ Expression) |
//            (opt(ForInit) ~ ";" ~ opt(Expression) ~ ";" ~ opt(ForUpdate)) !
//
//    def ForInit: MultiParser[Any] =
//        LocalVariableDeclaration |
//            StatementExpressionList !;
//
//    def StatementExpressionList: MultiParser[Any] =
//        rep1Sep(StatementExpression, ",") !() named ("StatementExpressionList");
//
//    def ForUpdate: MultiParser[Any] =
//        StatementExpressionList !;
//
//    def BreakStatement: MultiParser[Any] =
//        "break" ~ opt(IDENTIFIER) ~ ";" !;
//
//    def ContinueStatement: MultiParser[Any] =
//        "continue" ~ opt(IDENTIFIER) ~ ";" !;
//
//    def ReturnStatement: MultiParser[Any] =
//        "return" ~ opt(Expression) ~ ";" !;
//
//    def ThrowStatement: MultiParser[Any] =
//        "throw" ~ Expression ~ ";" !;
//
//    def SynchronizedStatement: MultiParser[Any] =
//        "synchronized" ~ "(" ~ Expression ~ ")" ~ Block !;
//
//    def TryStatement: MultiParser[Any] =
//    /*
//    * Semantic check required here to make sure that at least one
//    * finally/catch is present.
//    */
//        "try" ~ Block ~ TryStatementEnd !
//
//    def TryStatementEnd: MultiParser[Any] =
//        repOpt(CatchBlock) ~ opt("finally" ~ Block) !;
//
//    def CatchBlock: MultiParser[Any] =
//        "catch" ~ "(" ~ FormalParameter ~ ")" ~ Block !;
//
//    /* We use productions to match >>>, >> and > so that we can keep the
//    * type declaration syntax with generics clean
//    */
//
//    /* Annotation syntax follows. */
//
//    def Annotation: MultiParser[JAnnotation] =
//        (NormalAnnotation |
//            SingleMemberAnnotation |
//            MarkerAnnotation) ^^ JAnnotation
//
//    def NormalAnnotation: MultiParser[Any] =
//        "@" ~ Name ~ "(" ~ opt(MemberValuePairs) ~ ")" !;
//
//    def MarkerAnnotation: MultiParser[Any] =
//        "@" ~ Name !;
//
//    def SingleMemberAnnotation: MultiParser[Any] =
//        "@" ~ Name ~ "(" ~ MemberValue ~ ")" !;
//
//    def MemberValuePairs: MultiParser[Any] =
//        rep1Sep(MemberValuePair, ",") !;
//
//    def MemberValuePair: MultiParser[Any] =
//        IDENTIFIER ~ "=" ~ MemberValue !;
//
//    def MemberValue: MultiParser[Any] =
//        Annotation |
//            MemberValueArrayInitializer |
//            ConditionalExpression !;
//
//    def MemberValueArrayInitializer: MultiParser[Any] =
//        "{" ~ rep1Sep(MemberValue, ",") ~ opt(",") ~ "}" !;
//
//    /* Annotation Types. */
//
//    def AnnotationTypeDeclaration: MultiParser[Any] =
//        "@" ~ "interface" ~ IDENTIFIER ~ AnnotationTypeBody !;
//
//    def AnnotationTypeBody: MultiParser[Any] =
//        "{" ~ repOpt(AnnotationTypeMemberDeclaration) ~ "}" !;
//
//    def AnnotationTypeMemberDeclaration: MultiParser[Any] =
//        ((Modifiers ~ Type ~ IDENTIFIER ~ "(" ~ ")" ~ opt(DefaultValue) ~ ";")
//            | (Modifiers ~ ClassOrInterfaceDeclaration)
//            | (Modifiers ~ EnumDeclaration)
//            | (Modifiers ~ AnnotationTypeDeclaration)
//            | (Modifiers ~ FieldDeclaration)
//            |
//            ";") !
//
//    def DefaultValue: MultiParser[Any] =
//        "default" ~ MemberValue !

}
