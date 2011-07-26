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
        opt(PackageDeclaration) ~ repOpt(ImportDeclaration) ~ repOpt(TypeDeclaration) !

    def PackageDeclaration =
        "package" ~> Name <~ ";" !

    def ImportDeclaration =
        "import" ~ opt("static") ~ Name ~ opt("." ~ "*") ~ ";" !

    def Modifiers = repOpt(Modifier) !

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
                | fail("expected modifier")) !

    def TypeDeclaration: MultiParser[Any] =
        (";"
                | (Modifiers ~ ClassOrInterfaceDeclaration)
                | (Modifiers ~ EnumDeclaration)
                | (Modifiers ~ AnnotationTypeDeclaration)
                | fail("expected TypeDeclaration")) !

    def ClassOrInterfaceDeclaration: MultiParser[Any] =
        ClassOrInterface ~ IDENTIFIER ~ opt(TypeParameters) ~ opt(ExtendsList) ~
                opt(ImplementsList) ~ ClassOrInterfaceBody !

    def ClassOrInterface: MultiParser[Any] = "class" | "interface" !

    def ExtendsList: MultiParser[Any] =
        "extends" ~> rep1Sep(ClassOrInterfaceType, ",") !

    def ImplementsList: MultiParser[Any] =
        "implements" ~> rep1Sep(ClassOrInterfaceType, ",") !

    def EnumDeclaration: MultiParser[Any] =
        "enum" ~> IDENTIFIER ~
                opt(ImplementsList) ~
                EnumBody !

    def EnumBody: MultiParser[Any] =
        "{" ~>
                rep1Sep(EnumConstant, ",") ~
                opt(EnumBodyInternal) ~
                "}" !

    def EnumBodyInternal: MultiParser[Any] = ";" ~ repOpt(ClassOrInterfaceBodyDeclaration) !

    def EnumConstant: MultiParser[Any] =
        IDENTIFIER ~ opt(Arguments) ~ opt(ClassOrInterfaceBody) !

    def TypeParameters: MultiParser[Any] =
        "<" ~ rep1Sep(TypeParameter, ",") ~ ">" !

    def TypeParameter: MultiParser[Any] =
        IDENTIFIER ~ opt(TypeBound) !

    def TypeBound =
        "extends" ~> rep1Sep(ClassOrInterfaceType, "&") !

    def ClassOrInterfaceBody: MultiParser[Any] =
        "{" ~ repOpt(ClassOrInterfaceBodyDeclaration) ~ "}" !

    def ClassOrInterfaceBodyDeclaration: MultiParser[Any] =
        (Initializer | Modifiers ~ (
                ClassOrInterfaceDeclaration
                        | EnumDeclaration
                        | ConstructorDeclaration
                        | FieldDeclaration
                        | MethodDeclaration)
                |
                ";") !

    def FieldDeclaration: MultiParser[Any] =
        Type ~ rep1Sep(VariableDeclarator, ",") <~ ";" !

    def VariableDeclarator: MultiParser[Any] =
        VariableDeclaratorId ~ opt("=" ~> VariableInitializer) !

    def VariableDeclaratorId: MultiParser[Any] =
        IDENTIFIER ~ repOpt("[" ~ "]") !

    def VariableInitializer: MultiParser[Any] =
        ArrayInitializer | Expression !

    def ArrayInitializer: MultiParser[Any] =
        "{" ~> repSep(VariableInitializer, ",") <~ (opt(",") ~ "}") !

    def MethodDeclaration: MultiParser[Any] =
        opt(TypeParameters) ~
                ResultType ~
                MethodDeclarator ~ opt("throws" ~> NameList) ~
                MethodDeclarationBody !

    def MethodDeclarationBody: MultiParser[Any] = Block | ";" !

    def MethodDeclarator: MultiParser[Any] =
        IDENTIFIER ~ FormalParameters ~ repOpt("[" ~ "]") !

    def FormalParameters: MultiParser[Any] =
        "(" ~> repSep(FormalParameter, ",") <~ ")" !

    def FormalParameter: MultiParser[Any] =
        opt("final") ~ Type ~ opt("...") ~ VariableDeclaratorId !

    def ConstructorDeclaration: MultiParser[Any] =
        opt(TypeParameters) ~ IDENTIFIER ~ FormalParameters ~ opt("throws" ~> NameList) ~ "{" ~ opt(ExplicitConstructorInvocation) ~ repOpt(BlockStatement) ~ "}" !

    def ExplicitConstructorInvocation: MultiParser[Any] =
        ("this" ~ Arguments <~ ";") |
                ((opt(PrimaryExpression ~ ".") ~ "super" ~ Arguments <~ ";")) !

    def Initializer: MultiParser[Any] =
        opt("static") ~ Block !

    /*
 * Type, name and expression syntax follows.
 */

    def Type: MultiParser[Any] =
        ReferenceTypeP | PrimitiveType !

    def ReferenceTypeP: MultiParser[Any] =
        (PrimitiveType ~ rep1("[" ~ "]")) |
                (ClassOrInterfaceType ~ repOpt("[" ~ "]")) !

    def ClassOrInterfaceType: MultiParser[Any] =
        IDENTIFIER ~ opt(TypeArguments) ~
                repOpt("." ~ IDENTIFIER ~ opt(TypeArguments)) !() named "ClassOrInterfaceType"

    def TypeArguments: MultiParser[Any] =
        "<" ~> rep1Sep(TypeArgument, ",") <~ ">" !

    def TypeArgument: MultiParser[Any] =
        ReferenceTypeP |
                ("?" ~ opt(WildcardBounds)) !

    def WildcardBounds: MultiParser[Any] =
        ("extends" ~ ReferenceTypeP) |
                ("super" ~ ReferenceTypeP) !

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
                "double") !

    def ResultType: MultiParser[Any] =
        "void" |
                Type !

    def Name: MultiParser[Any] =
        rep1Sep(IDENTIFIER, ".") !() named ("Name")

    def NameList: MultiParser[Any] = rep1Sep(Name, ",") !() named ("NameList")

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
        ConditionalExpression ~ opt(AssignExp) !

    def AssignExp: MultiParser[Any] =
        AssignmentOperator ~ Expression !

    def AssignmentOperator: MultiParser[Any] =
        "=" | "*=" | "/=" | "%=" | "+=" | "-=" | "<<=" | ">>=" | ">>>=" | "&=" | "^=" | "|=" !

    def ConditionalExpression: MultiParser[Any] =
        ConditionalExpressionFull | ConditionalOrExpression !

    def ConditionalExpressionFull: MultiParser[Any] =
        ConditionalOrExpression <~ "?" ~ Expression <~ ":" ~ Expression !

    def ConditionalOrExpression: MultiParser[Any] =
        rep1Sep(ConditionalAndExpression, "||") !

    def ConditionalAndExpression: MultiParser[Any] =
        rep1Sep(InclusiveOrExpression, "&&") !

    def InclusiveOrExpression: MultiParser[Any] =
        rep1Sep(ExclusiveOrExpression, "|") !

    def ExclusiveOrExpression: MultiParser[Any] =
        rep1Sep(AndExpression, "^") !

    def AndExpression: MultiParser[Any] =
        rep1Sep(EqualityExpression, "&") !

    def EqualityExpression: MultiParser[Any] =
        rep1Sep(InstanceOfExpression, "==" | "!=") !

    def InstanceOfExpression: MultiParser[Any] =
        RelationalExpression ~ opt("instanceof" ~ Type) !

    def RelationalExpression: MultiParser[Any] =
        rep1Sep(ShiftExpression, "<" | ">" | "<=" | ">=") !

    def ShiftExpression: MultiParser[Any] =
        rep1Sep(AdditiveExpression, ShiftOp) !

    def ShiftOp: MultiParser[Any] = "<<" | ">" ~ ">" ~ opt(">") !

    def AdditiveExpression: MultiParser[Any] =
        rep1Sep(MultiplicativeExpression, AdditiveOp) !

    def MultiplicativeExpression: MultiParser[Any] =
        rep1Sep(UnaryExpression, "*" | "/" | "%") !

    def AdditiveOp: MultiParser[Any] = "+" | "-" !

    def UnaryExpression: MultiParser[Any] =
        ((AdditiveOp ~ UnaryExpression)
                |
                PreIncrementExpression
                |
                PreDecrementExpression
                |
                UnaryExpressionNotPlusMinus) !

    def PreIncrementExpression: MultiParser[Any] =
        "++" ~ PrimaryExpression !

    def PreDecrementExpression: MultiParser[Any] =
        "--" ~ PrimaryExpression !

    def UnaryExpressionNotPlusMinus: MultiParser[Any] =
        (UnaryOp ~ UnaryExpression
                |
                CastExpression
                |
                PostfixExpression) !

    def UnaryOp: MultiParser[Any] = "~" | "!" !

    def PostfixExpression: MultiParser[Any] =
        PrimaryExpression ~ opt(PostfixOp) !

    def PostfixOp: MultiParser[Any] = "++" | "--" !

    def CastExpression: MultiParser[Any] =
        ("(" ~> Type <~ ")" ~ UnaryExpression) |
                ("(" ~> Type <~ ")" ~ UnaryExpressionNotPlusMinus) !

    def PrimaryExpression: MultiParser[Any] =
        PrimaryPrefix ~ repOpt(PrimarySuffix) !

    def MemberSelector: MultiParser[Any] =
        "." ~ TypeArguments ~ IDENTIFIER !() named ("MemberSelector")

    def PrimaryPrefix: MultiParser[Any] =
        (Literal
                | ("this")
                | ("super" ~ "." ~ IDENTIFIER)
                | ("(" ~> Expression <~ ")")
                | (AllocationExpression)
                | (ResultType ~ "." ~ "class")
                | (Name)) !

    def PrimarySuffix: MultiParser[Any] =
        (("." ~ "this")
                | ("." ~ AllocationExpression)
                | (MemberSelector)
                | ("[" ~> Expression <~ "]")
                | ("." ~ IDENTIFIER)
                | (Arguments)) !

    def IDENTIFIER = token("<IDENTIFIER>",
        _.getKind == Java15ParserConstants.IDENTIFIER) !

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
                NullLiteral) !

    def BooleanLiteral: MultiParser[Any] =
        "true" | "false" !

    def NullLiteral: MultiParser[Any] =
        "null" !

    def Arguments: MultiParser[Any] =
        "(" ~> opt(ArgumentList) <~ ")" !

    def ArgumentList: MultiParser[Any] =
        rep1Sep(Expression, ",") !

    def AllocationExpression: MultiParser[Any] =
        ("new" ~ PrimitiveType ~ ArrayDimsAndInits) |
                ("new" ~ ClassOrInterfaceType ~ opt(TypeArguments) ~ AllocationExpressionInit) !

    def AllocationExpressionInit: MultiParser[Any] =
        ArrayDimsAndInits |
                (Arguments ~ opt(ClassOrInterfaceBody)) !

    /*
 * The third LOOK_AHEAD specification below is to parse to PrimarySuffix
 * if there is an expression between the "opt(...)".
 */
    def ArrayDimsAndInits: MultiParser[Any] =
        (rep1("[" ~ Expression ~ "]") ~ opt("[" ~ "]")) |
                (rep1("[" ~ "]") ~ ArrayInitializer) !

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
                TryStatement | fail("expected Statement")) !() named ("Statement")

    def AssertStatement: MultiParser[Any] =
        "assert" ~ Expression ~ opt(":" ~ Expression) ~ ";" !

    def LabeledStatement: MultiParser[Any] =
        IDENTIFIER ~ ":" ~ Statement !() named ("LabeledStatement")

    def Block: MultiParser[Any] =
        "{" ~ repOpt(BlockStatement) ~ "}" !() named ("Block")

    def BlockStatement: MultiParser[Any] =
        (LocalVariableDeclaration <~ ";") |
                Statement |
                ClassOrInterfaceDeclaration !() named ("BlockStatement")

    def LocalVariableDeclaration: MultiParser[Any] =
        opt("final") ~ Type ~ rep1Sep(VariableDeclarator, ",") !() named ("LocalVariableDeclaration")

    def EmptyStatement: MultiParser[Any] =
        ";" !() named ("EmptyStatement")

    /*
 * The last expansion of this production accepts more than the legal
 * Java expansions for StatementExpression.  This expansion does not
 * use PostfixExpression for performance reasons.
 */
    def StatementExpression: MultiParser[Any] =
        (PreIncrementExpression
                | PreDecrementExpression
                | (PrimaryExpression ~ opt(StatementExpressionAssignment))) !() named ("ExpressionStatement");

    def StatementExpressionAssignment: MultiParser[Any] =
        "++" |
                "--" |
                (AssignmentOperator ~ Expression) !() named ("StatementExpressionAssignment");

    def SwitchStatement: MultiParser[Any] =
        "switch" ~ "(" ~ Expression ~ ")" ~ "{" ~ repOpt(SwitchLabel ~ repOpt(BlockStatement)) ~ "}" !

    def SwitchLabel: MultiParser[Any] =
        ("case" ~ Expression ~ ":") |
                ("default" ~ ":") !;

    /*
 * The disambiguating algorithm of JavaCC automatically binds dangling
 * else's to the innermost if statement.  The LOOK_AHEAD specification
 * is to tell JavaCC that we know what we are doing.
 */
    def IfStatement: MultiParser[Any] =
        "if" ~ "(" ~ Expression ~ ")" ~ Statement ~ repOpt(ElIfStatement) ~ opt("else" ~ Statement) !;

    private def ElIfStatement: MultiParser[Any] =
        "else" ~ "if" ~! "(" ~ Expression ~ ")" ~ Statement !;

    def WhileStatement: MultiParser[Any] =
        "while" ~ "(" ~ Expression ~ ")" ~ Statement !;

    def DoStatement: MultiParser[Any] =
        "do" ~ Statement ~ "while" ~ "(" ~ Expression ~ ")" ~ ";" !

    def ForStatement: MultiParser[Any] =
        "for" ~ "(" ~ ForStatementInternal ~ ")" ~ Statement !

    def ForStatementInternal: MultiParser[Any] =
        (Type ~ IDENTIFIER ~ ":" ~ Expression) |
                (opt(ForInit) ~ ";" ~ opt(Expression) ~ ";" ~ opt(ForUpdate)) !

    def ForInit: MultiParser[Any] =
        LocalVariableDeclaration |
                StatementExpressionList !;

    def StatementExpressionList: MultiParser[Any] =
        rep1Sep(StatementExpression, ",") !() named ("StatementExpressionList");

    def ForUpdate: MultiParser[Any] =
        StatementExpressionList !;

    def BreakStatement: MultiParser[Any] =
        "break" ~ opt(IDENTIFIER) ~ ";" !;

    def ContinueStatement: MultiParser[Any] =
        "continue" ~ opt(IDENTIFIER) ~ ";" !;

    def ReturnStatement: MultiParser[Any] =
        "return" ~ opt(Expression) ~ ";" !;

    def ThrowStatement: MultiParser[Any] =
        "throw" ~ Expression ~ ";" !;

    def SynchronizedStatement: MultiParser[Any] =
        "synchronized" ~ "(" ~ Expression ~ ")" ~ Block !;

    def TryStatement: MultiParser[Any] =
    /*
    * Semantic check required here to make sure that at least one
    * finally/catch is present.
    */
        "try" ~ Block ~ TryStatementEnd !

    def TryStatementEnd: MultiParser[Any] =
        rep1(CatchBlock) ~ opt("finally" ~ Block) !;

    def CatchBlock: MultiParser[Any] =
        "catch" ~ "(" ~ FormalParameter ~ ")" ~ Block !;

    /* We use productions to match >>>, >> and > so that we can keep the
 * type declaration syntax with generics clean
 */

    /* Annotation syntax follows. */

    def Annotation: MultiParser[Any] =
        NormalAnnotation |
                SingleMemberAnnotation |
                MarkerAnnotation !;

    def NormalAnnotation: MultiParser[Any] =
        "@" ~ Name ~ "(" ~ opt(MemberValuePairs) ~ ")" !;

    def MarkerAnnotation: MultiParser[Any] =
        "@" ~ Name !;

    def SingleMemberAnnotation: MultiParser[Any] =
        "@" ~ Name ~ "(" ~ MemberValue ~ ")" !;

    def MemberValuePairs: MultiParser[Any] =
        rep1Sep(MemberValuePair, ",") !;

    def MemberValuePair: MultiParser[Any] =
        IDENTIFIER ~ "=" ~ MemberValue !;

    def MemberValue: MultiParser[Any] =
        Annotation |
                MemberValueArrayInitializer |
                ConditionalExpression !;

    def MemberValueArrayInitializer: MultiParser[Any] =
        "{" ~ rep1Sep(MemberValue, ",") ~ opt(",") ~ "}" !;

    /* Annotation Types. */

    def AnnotationTypeDeclaration: MultiParser[Any] =
        "@" ~ "interface" ~ IDENTIFIER ~ AnnotationTypeBody !;

    def AnnotationTypeBody: MultiParser[Any] =
        "{" ~ repOpt(AnnotationTypeMemberDeclaration) ~ "}" !;

    def AnnotationTypeMemberDeclaration: MultiParser[Any] =
        ((Modifiers ~ Type ~ IDENTIFIER ~ "(" ~ ")" ~ opt(DefaultValue) ~ ";")
                | (Modifiers ~ ClassOrInterfaceDeclaration)
                | (Modifiers ~ EnumDeclaration)
                | (Modifiers ~ AnnotationTypeDeclaration)
                | (Modifiers ~ FieldDeclaration)
                |
                ";") !

    def DefaultValue: MultiParser[Any] =
        "default" ~ MemberValue !

}
