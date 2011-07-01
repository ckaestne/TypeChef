package de.fosd.typechef.parser.java15

import de.fosd.typechef.parser._
import de.fosd.typechef.parser.java15.lexer._

//
///*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// * based on GCIDE grammar in CIDE
// * which is again based on the publically available JavaCC grammar
// */
//
class JavaParser extends MultiFeatureParser {
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

    implicit def keyword(s: String): MultiParser[Elem] = token(s, _.getText == s)

    def CompilationUnit =
        opt(PackageDeclaration) ~ repOpt(ImportDeclaration) ~ repOpt(TypeDeclaration) ! Choice.join

    def PackageDeclaration =
        "package" ~> Name <~ ";" ! Choice.join

    def ImportDeclaration =
        "import" ~ opt("static") ~ Name ~ opt("." ~ "*") ~ ";" ! Choice.join

    def Modifiers = repOpt(Modifier) ! Choice.join

    def Modifier =
        ("public"
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
                "strictfp"
                |
                Annotation
                | fail("expected modifier")) ! Choice.join

    def TypeDeclaration: MultiParser[Any] =
        (";"
                | (Modifiers ~ ClassOrInterfaceDeclaration)
                | (Modifiers ~ EnumDeclaration)
                | (Modifiers ~ AnnotationTypeDeclaration)
                | fail("expected TypeDeclaration")) ! Choice.join

    def ClassOrInterfaceDeclaration: MultiParser[Any] =
        ClassOrInterface ~ IDENTIFIER ~ opt(TypeParameters) ~ opt(ExtendsList) ~
                opt(ImplementsList) ~ ClassOrInterfaceBody ! Choice.join

    def ClassOrInterface: MultiParser[Any] = "class" | "interface" ! Choice.join

    def ExtendsList: MultiParser[Any] =
        "extends" ~> rep1Sep(ClassOrInterfaceType, ",") ! Choice.join

    def ImplementsList: MultiParser[Any] =
        "implements" ~> rep1Sep(ClassOrInterfaceType, ",") ! Choice.join

    def EnumDeclaration: MultiParser[Any] =
        "enum" ~> IDENTIFIER ~
                opt(ImplementsList) ~
                EnumBody ! Choice.join

    def EnumBody: MultiParser[Any] =
        "{" ~>
                rep1Sep(EnumConstant, ",") ~
                opt(EnumBodyInternal) ~
                "}" ! Choice.join

    def EnumBodyInternal: MultiParser[Any] = ";" ~ repOpt(ClassOrInterfaceBodyDeclaration) ! Choice.join

    def EnumConstant: MultiParser[Any] =
        IDENTIFIER ~ opt(Arguments) ~ opt(ClassOrInterfaceBody) ! Choice.join

    def TypeParameters: MultiParser[Any] =
        "<" ~ rep1Sep(TypeParameter, ",") ~ ">" ! Choice.join

    def TypeParameter: MultiParser[Any] =
        IDENTIFIER ~ opt(TypeBound) ! Choice.join

    def TypeBound =
        "extends" ~> rep1Sep(ClassOrInterfaceType, "&") ! Choice.join

    def ClassOrInterfaceBody: MultiParser[Any] =
        "{" ~ repOpt(ClassOrInterfaceBodyDeclaration) ~ "}" ! Choice.join

    def ClassOrInterfaceBodyDeclaration: MultiParser[Any] =
        (Initializer | Modifiers ~ (
                ClassOrInterfaceDeclaration
                        | EnumDeclaration
                        | ConstructorDeclaration
                        | FieldDeclaration
                        | MethodDeclaration)
                |
                ";") ! Choice.join

    def FieldDeclaration: MultiParser[Any] =
        Type ~ rep1Sep(VariableDeclarator, ",") <~ ";" ! Choice.join

    def VariableDeclarator: MultiParser[Any] =
        VariableDeclaratorId ~ opt("=" ~> VariableInitializer) ! Choice.join

    def VariableDeclaratorId: MultiParser[Any] =
        IDENTIFIER ~ repOpt("[" ~ "]") ! Choice.join

    def VariableInitializer: MultiParser[Any] =
        ArrayInitializer | Expression ! Choice.join

    def ArrayInitializer: MultiParser[Any] =
        "{" ~> repSep(VariableInitializer, ",") <~ (opt(",") ~ "}") ! Choice.join

    def MethodDeclaration: MultiParser[Any] =
        opt(TypeParameters) ~
                ResultType ~
                MethodDeclarator ~ opt("throws" ~> NameList) ~
                MethodDeclarationBody ! Choice.join

    def MethodDeclarationBody: MultiParser[Any] = Block | ";" ! Choice.join

    def MethodDeclarator: MultiParser[Any] =
        IDENTIFIER ~ FormalParameters ~ repOpt("[" ~ "]") ! Choice.join

    def FormalParameters: MultiParser[Any] =
        "(" ~> repSep(FormalParameter, ",") <~ ")" ! Choice.join

    def FormalParameter: MultiParser[Any] =
        opt("final") ~ Type ~ opt("...") ~ VariableDeclaratorId ! Choice.join

    def ConstructorDeclaration: MultiParser[Any] =
        opt(TypeParameters) ~ IDENTIFIER ~ FormalParameters ~ opt("throws" ~> NameList) ~ "{" ~ opt(ExplicitConstructorInvocation) ~ repOpt(BlockStatement) ~ "}" ! Choice.join

    def ExplicitConstructorInvocation: MultiParser[Any] =
        ("this" ~ Arguments <~ ";") |
                ((opt(PrimaryExpression ~ ".") ~ "super" ~ Arguments <~ ";")) ! Choice.join

    def Initializer: MultiParser[Any] =
        opt("static") ~ Block ! Choice.join

    /*
 * Type, name and expression syntax follows.
 */

    def Type: MultiParser[Any] =
        ReferenceTypeP | PrimitiveType ! Choice.join

    def ReferenceTypeP: MultiParser[Any] =
        (PrimitiveType ~ rep1("[" ~ "]")) |
                (ClassOrInterfaceType ~ repOpt("[" ~ "]")) ! Choice.join

    def ClassOrInterfaceType: MultiParser[Any] =
        IDENTIFIER ~ opt(TypeArguments) ~
                repOpt("." ~ IDENTIFIER ~ opt(TypeArguments)) ! Choice.join named "ClassOrInterfaceType"

    def TypeArguments: MultiParser[Any] =
        "<" ~> rep1Sep(TypeArgument, ",") <~ ">" ! Choice.join

    def TypeArgument: MultiParser[Any] =
        ReferenceTypeP |
                ("?" ~ opt(WildcardBounds)) ! Choice.join

    def WildcardBounds: MultiParser[Any] =
        ("extends" ~ ReferenceTypeP) |
                ("super" ~ ReferenceTypeP) ! Choice.join

    def PrimitiveType: MultiParser[Any] =
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
                "double") ! Choice.join

    def ResultType: MultiParser[Any] =
        "void" |
                Type ! Choice.join

    def Name: MultiParser[Any] =
        rep1Sep(IDENTIFIER, ".") ! Choice.join named ("Name")

    def NameList: MultiParser[Any] = rep1Sep(Name, ",") ! Choice.join named ("NameList")

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
    def Expression: MultiParser[Any] =
        ConditionalExpression ~ opt(AssignExp) ! Choice.join

    def AssignExp: MultiParser[Any] =
        AssignmentOperator ~ Expression ! Choice.join

    def AssignmentOperator: MultiParser[Any] =
        "=" | "*=" | "/=" | "%=" | "+=" | "-=" | "<<=" | ">>=" | ">>>=" | "&=" | "^=" | "|=" ! Choice.join

    def ConditionalExpression: MultiParser[Any] =
        ConditionalExpressionFull | ConditionalOrExpression ! Choice.join

    def ConditionalExpressionFull: MultiParser[Any] =
        ConditionalOrExpression <~ "?" ~ Expression <~ ":" ~ Expression ! Choice.join

    def ConditionalOrExpression: MultiParser[Any] =
        rep1Sep(ConditionalAndExpression, "||") ! Choice.join

    def ConditionalAndExpression: MultiParser[Any] =
        rep1Sep(InclusiveOrExpression, "&&") ! Choice.join

    def InclusiveOrExpression: MultiParser[Any] =
        rep1Sep(ExclusiveOrExpression, "|") ! Choice.join

    def ExclusiveOrExpression: MultiParser[Any] =
        rep1Sep(AndExpression, "^") ! Choice.join

    def AndExpression: MultiParser[Any] =
        rep1Sep(EqualityExpression, "&") ! Choice.join

    def EqualityExpression: MultiParser[Any] =
        rep1Sep(InstanceOfExpression, "==" | "!=") ! Choice.join

    def InstanceOfExpression: MultiParser[Any] =
        RelationalExpression ~ opt("instanceof" ~ Type) ! Choice.join

    def RelationalExpression: MultiParser[Any] =
        rep1Sep(ShiftExpression, "<" | ">" | "<=" | ">=") ! Choice.join

    def ShiftExpression: MultiParser[Any] =
        rep1Sep(AdditiveExpression, ShiftOp) ! Choice.join

    def ShiftOp: MultiParser[Any] = "<<" | ">" ~ ">" ~ opt(">") ! Choice.join

    def AdditiveExpression: MultiParser[Any] =
        rep1Sep(MultiplicativeExpression, AdditiveOp) ! Choice.join

    def MultiplicativeExpression: MultiParser[Any] =
        rep1Sep(UnaryExpression, "*" | "/" | "%") ! Choice.join

    def AdditiveOp: MultiParser[Any] = "+" | "-" ! Choice.join

    def UnaryExpression: MultiParser[Any] =
        ((AdditiveOp ~ UnaryExpression)
                |
                PreIncrementExpression
                |
                PreDecrementExpression
                |
                UnaryExpressionNotPlusMinus) ! Choice.join

    def PreIncrementExpression: MultiParser[Any] =
        "++" ~ PrimaryExpression ! Choice.join

    def PreDecrementExpression: MultiParser[Any] =
        "--" ~ PrimaryExpression ! Choice.join

    def UnaryExpressionNotPlusMinus: MultiParser[Any] =
        (UnaryOp ~ UnaryExpression
                |
                CastExpression
                |
                PostfixExpression) ! Choice.join

    def UnaryOp: MultiParser[Any] = "~" | "!" ! Choice.join

    def PostfixExpression: MultiParser[Any] =
        PrimaryExpression ~ opt(PostfixOp) ! Choice.join

    def PostfixOp: MultiParser[Any] = "++" | "--" ! Choice.join

    def CastExpression: MultiParser[Any] =
        ("(" ~> Type <~ ")" ~ UnaryExpression) |
                ("(" ~> Type <~ ")" ~ UnaryExpressionNotPlusMinus) ! Choice.join

    def PrimaryExpression: MultiParser[Any] =
        PrimaryPrefix ~ repOpt(PrimarySuffix) ! Choice.join

    def MemberSelector: MultiParser[Any] =
        "." ~ TypeArguments ~ IDENTIFIER ! Choice.join named ("MemberSelector")

    def PrimaryPrefix: MultiParser[Any] =
        (Literal
                | ("this")
                | ("super" ~ "." ~ IDENTIFIER)
                | ("(" ~> Expression <~ ")")
                | (AllocationExpression)
                | (ResultType ~ "." ~ "class")
                | (Name)) ! Choice.join

    def PrimarySuffix: MultiParser[Any] =
        (("." ~ "this")
                | ("." ~ AllocationExpression)
                | (MemberSelector)
                | ("[" ~> Expression <~ "]")
                | ("." ~ IDENTIFIER)
                | (Arguments)) ! Choice.join

    def IDENTIFIER = token("<IDENTIFIER>",
        _.getKind == Java15ParserConstants.IDENTIFIER) ! Choice.join

    def Literal: MultiParser[Any] =
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
                NullLiteral) ! Choice.join

    def BooleanLiteral: MultiParser[Any] =
        "true" | "false" ! Choice.join

    def NullLiteral: MultiParser[Any] =
        "null" ! Choice.join

    def Arguments: MultiParser[Any] =
        "(" ~> opt(ArgumentList) <~ ")" ! Choice.join

    def ArgumentList: MultiParser[Any] =
        rep1Sep(Expression, ",") ! Choice.join

    def AllocationExpression: MultiParser[Any] =
        ("new" ~ PrimitiveType ~ ArrayDimsAndInits) |
                ("new" ~ ClassOrInterfaceType ~ opt(TypeArguments) ~ AllocationExpressionInit) ! Choice.join

    def AllocationExpressionInit: MultiParser[Any] =
        ArrayDimsAndInits |
                (Arguments ~ opt(ClassOrInterfaceBody)) ! Choice.join

    /*
 * The third LOOK_AHEAD specification below is to parse to PrimarySuffix
 * if there is an expression between the "opt(...)".
 */
    def ArrayDimsAndInits: MultiParser[Any] =
        (rep1("[" ~ Expression ~ "]") ~ opt("[" ~ "]")) |
                (rep1("[" ~ "]") ~ ArrayInitializer) ! Choice.join

    /*
 * Statement syntax follows.
 */
    def Statement: MultiParser[Any] =
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
                TryStatement | fail("expected Statement")) ! Choice.join named ("Statement")

    def AssertStatement: MultiParser[Any] =
        "assert" ~ Expression ~ opt(":" ~ Expression) ~ ";" ! Choice.join

    def LabeledStatement: MultiParser[Any] =
        IDENTIFIER ~ ":" ~ Statement ! Choice.join named ("LabeledStatement")

    def Block: MultiParser[Any] =
        "{" ~ repOpt(BlockStatement) ~ "}" ! Choice.join named ("Block")

    def BlockStatement: MultiParser[Any] =
        (LocalVariableDeclaration <~ ";") |
                Statement |
                ClassOrInterfaceDeclaration ! Choice.join named ("BlockStatement")

    def LocalVariableDeclaration: MultiParser[Any] =
        opt("final") ~ Type ~ rep1Sep(VariableDeclarator, ",") ! Choice.join named ("LocalVariableDeclaration")

    def EmptyStatement: MultiParser[Any] =
        ";" ! Choice.join named ("EmptyStatement")

    /*
 * The last expansion of this production accepts more than the legal
 * Java expansions for StatementExpression.  This expansion does not
 * use PostfixExpression for performance reasons.
 */
    def StatementExpression: MultiParser[Any] =
        (PreIncrementExpression
                | PreDecrementExpression
                | (PrimaryExpression ~ opt(StatementExpressionAssignment))) ! Choice.join named ("ExpressionStatement");

    def StatementExpressionAssignment: MultiParser[Any] =
        "++" |
                "--" |
                (AssignmentOperator ~ Expression) ! Choice.join named ("StatementExpressionAssignment");

    def SwitchStatement: MultiParser[Any] =
        "switch" ~ "(" ~ Expression ~ ")" ~ "{" ~ repOpt(SwitchLabel ~ repOpt(BlockStatement)) ~ "}" ! Choice.join

    def SwitchLabel: MultiParser[Any] =
        ("case" ~ Expression ~ ":") |
                ("default" ~ ":") ! Choice.join;

    /*
 * The disambiguating algorithm of JavaCC automatically binds dangling
 * else's to the innermost if statement.  The LOOK_AHEAD specification
 * is to tell JavaCC that we know what we are doing.
 */
    def IfStatement: MultiParser[Any] =
        "if" ~ "(" ~ Expression ~ ")" ~ Statement ~ repOpt(ElIfStatement) ~ opt("else" ~ Statement) ! Choice.join;

    private def ElIfStatement: MultiParser[Any] =
        "else" ~ "if" ~! "(" ~ Expression ~ ")" ~ Statement ! Choice.join;

    def WhileStatement: MultiParser[Any] =
        "while" ~ "(" ~ Expression ~ ")" ~ Statement ! Choice.join;

    def DoStatement: MultiParser[Any] =
        "do" ~ Statement ~ "while" ~ "(" ~ Expression ~ ")" ~ ";" ! Choice.join

    def ForStatement: MultiParser[Any] =
        "for" ~ "(" ~ ForStatementInternal ~ ")" ~ Statement ! Choice.join

    def ForStatementInternal: MultiParser[Any] =
        (Type ~ IDENTIFIER ~ ":" ~ Expression) |
                (opt(ForInit) ~ ";" ~ opt(Expression) ~ ";" ~ opt(ForUpdate)) ! Choice.join

    def ForInit: MultiParser[Any] =
        LocalVariableDeclaration |
                StatementExpressionList ! Choice.join;

    def StatementExpressionList: MultiParser[Any] =
        rep1Sep(StatementExpression, ",") ! Choice.join named ("StatementExpressionList");

    def ForUpdate: MultiParser[Any] =
        StatementExpressionList ! Choice.join;

    def BreakStatement: MultiParser[Any] =
        "break" ~ opt(IDENTIFIER) ~ ";" ! Choice.join;

    def ContinueStatement: MultiParser[Any] =
        "continue" ~ opt(IDENTIFIER) ~ ";" ! Choice.join;

    def ReturnStatement: MultiParser[Any] =
        "return" ~ opt(Expression) ~ ";" ! Choice.join;

    def ThrowStatement: MultiParser[Any] =
        "throw" ~ Expression ~ ";" ! Choice.join;

    def SynchronizedStatement: MultiParser[Any] =
        "synchronized" ~ "(" ~ Expression ~ ")" ~ Block ! Choice.join;

    def TryStatement: MultiParser[Any] =
    /*
    * Semantic check required here to make sure that at least one
    * finally/catch is present.
    */
        "try" ~ Block ~ TryStatementEnd ! Choice.join

    def TryStatementEnd: MultiParser[Any] =
        rep1(CatchBlock) ~ opt("finally" ~ Block) ! Choice.join;

    def CatchBlock: MultiParser[Any] =
        "catch" ~ "(" ~ FormalParameter ~ ")" ~ Block ! Choice.join;

    /* We use productions to match >>>, >> and > so that we can keep the
 * type declaration syntax with generics clean
 */

    /* Annotation syntax follows. */

    def Annotation: MultiParser[Any] =
        NormalAnnotation |
                SingleMemberAnnotation |
                MarkerAnnotation ! Choice.join;

    def NormalAnnotation: MultiParser[Any] =
        "@" ~ Name ~ "(" ~ opt(MemberValuePairs) ~ ")" ! Choice.join;

    def MarkerAnnotation: MultiParser[Any] =
        "@" ~ Name ! Choice.join;

    def SingleMemberAnnotation: MultiParser[Any] =
        "@" ~ Name ~ "(" ~ MemberValue ~ ")" ! Choice.join;

    def MemberValuePairs: MultiParser[Any] =
        rep1Sep(MemberValuePair, ",") ! Choice.join;

    def MemberValuePair: MultiParser[Any] =
        IDENTIFIER ~ "=" ~ MemberValue ! Choice.join;

    def MemberValue: MultiParser[Any] =
        Annotation |
                MemberValueArrayInitializer |
                ConditionalExpression ! Choice.join;

    def MemberValueArrayInitializer: MultiParser[Any] =
        "{" ~ rep1Sep(MemberValue, ",") ~ opt(",") ~ "}" ! Choice.join;

    /* Annotation Types. */

    def AnnotationTypeDeclaration: MultiParser[Any] =
        "@" ~ "interface" ~ IDENTIFIER ~ AnnotationTypeBody ! Choice.join;

    def AnnotationTypeBody: MultiParser[Any] =
        "{" ~ repOpt(AnnotationTypeMemberDeclaration) ~ "}" ! Choice.join;

    def AnnotationTypeMemberDeclaration: MultiParser[Any] =
        ((Modifiers ~ Type ~ IDENTIFIER ~ "(" ~ ")" ~ opt(DefaultValue) ~ ";")
                | (Modifiers ~ ClassOrInterfaceDeclaration)
                | (Modifiers ~ EnumDeclaration)
                | (Modifiers ~ AnnotationTypeDeclaration)
                | (Modifiers ~ FieldDeclaration)
                |
                ";") ! Choice.join

    def DefaultValue: MultiParser[Any] =
        "default" ~ MemberValue ! Choice.join

}
