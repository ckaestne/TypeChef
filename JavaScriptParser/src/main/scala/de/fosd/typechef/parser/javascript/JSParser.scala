package de.fosd.typechef.parser.javascript

import de.fosd.typechef.parser._
import de.fosd.typechef.conditional.{Opt, Conditional}
import de.fosd.typechef.parser.javascript.rhino.Token

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

    implicit def keyword(s: String): MultiParser[Elem] = s match {
        //token(s, _.getText == s)
        case "{" => tokenids(Token.LC)
        case "}" => tokenids(Token.RC)
        case "(" => tokenids(Token.LP)
        case ")" => tokenids(Token.RP)
        case "[" => tokenids(Token.LB)
        case "]" => tokenids(Token.RB)
        case ";" => tokenids(Token.SEMI)
        case "," => tokenids(Token.COMMA)
        case "=" => tokenids(Token.EQ)
        case "." => tokenids(Token.DOT)
        case _ => fail("unsupported keyword " + s)
    }

    implicit def tokenids(s: Int): MultiParser[Elem] = token(Token.typeToName(s), _.getKind() == s)

    def Program: MultiParser[Conditional[JSProgram]] =
        repOpt(SourceElement) ^^! {JSProgram(_)}

    def SourceElement: MultiParser[JSSourceElement] = FunctionDeclaration | Statement

    def FunctionDeclaration: MultiParser[JSFunctionDeclaration] =
        Token.FUNCTION ~ Identifier ~ "(" ~ FormalParameterList ~ ")" ~ "{" ~ FunctionBody ~ "}" ^^ {
            case _ ~ id ~ _ ~ param ~ _ ~ _ ~ body ~ _ => JSFunctionDeclaration(id, param, body)
        }

    def FunctionExpression: MultiParser[JSFunctionExpression] =
        Token.FUNCTION ~ opt(Identifier) ~ "(" ~ FormalParameterList ~ ")" ~ "{" ~ FunctionBody ~ "}" ^^ {
            case _ ~ id ~ _ ~ param ~ _ ~ _ ~ body ~ _ => JSFunctionExpression(id, param, body)
        }

    def Identifier: MultiParser[String] = token("id", _.getKind() == Token.NAME) ^^ {_.getText()}
    def FormalParameterList: MultiParser[Any] = repSepPlain(Identifier, ",")
    def FunctionBody: MultiParser[JSProgram] = repOpt(SourceElement) ^^ {JSProgram(_)}


    def Statement: MultiParser[JSStatement] =
        (Block |
            VariableStatement |
            EmptyStatement |
            ExpressionStatement |
            IfStatement |
            IterationStatement |
            ContinueStatement |
            BreakStatement |
            ReturnStatement |
            WithStatement |
            LabelledStatement |
            SwitchStatement |
            ThrowStatement |
            TryStatement |
            DebuggerStatement) ^^ {x => JSStatement()}

    def Block = StatementList ^^ {JSBlock(_)}

    def StatementList = repOpt(Statement)

    def VariableStatement =
        Token.VAR ~ repSepPlain(VariableDeclaration, ",");

    def VariableDeclarationListNoIn = repSepPlain1(VariableDeclarationNoIn, ",")

    def VariableDeclaration =
        Identifier ~ opt(Initialiser)

    def VariableDeclarationNoIn =
        Identifier ~ opt(InitialiserNoIn)

    def Initialiser =
        "=" ~ AssignmentExpression

    def InitialiserNoIn =
        "=" ~ AssignmentExpressionNoIn

    def EmptyStatement =
        ";"

    def ExpressionStatement =
        Expression ~ ";"

    def IfStatement =
        Token.IF ~ "(" ~ Expression ~ ")" ~ Statement ~ opt(Token.ELSE ~ Statement)

    def IterationStatement =
        (Token.DO ~ Statement ~ Token.WHILE ~ "(" ~ Expression ~ ")" ~ ";") |
            (Token.WHILE ~ "(" ~ Expression ~ ")" ~ Statement) |
            (Token.FOR ~ "(" ~ opt(ExpressionNoIn) ~ ";" ~ opt(Expression) ~ ";" ~ opt(Expression) ~ ")" ~ Statement) |
            (Token.FOR ~ "(" ~ Token.VAR ~ VariableDeclarationListNoIn ~ ";" ~ opt(Expression) ~ ";" ~ opt(Expression) ~ ")" ~ Statement)
    (Token.FOR ~ "(" ~ LeftHandSideExpression ~ Token.IN ~ Expression ~ ")" ~ Statement)
    (Token.FOR ~ "(" ~ Token.VAR ~ VariableDeclarationNoIn ~ Token.IN ~ Expression ~ ")" ~ Statement)

    def ContinueStatement =
        Token.CONTINUE ~ opt(Identifier) ~ ";"

    def BreakStatement =
        Token.BREAK ~ opt(Identifier) ~ ";"

    def ReturnStatement =
        Token.RETURN ~ opt(Expression) ~ ";"

    def WithStatement =
        Token.WITH ~ "(" ~ Expression ~ ")" ~ Statement;

    def SwitchStatement =
        Token.SWITCH ~ "(" ~ Expression ~ ")" ~ CaseBlock

    def CaseBlock =
        "{" ~ CaseClauses ~ opt(DefaultClause ~ CaseClauses) ~ "}"

    def CaseClauses =
        repOpt(CaseClause)

    def CaseClause =
        Token.CASE ~ Expression ~ ":" ~ StatementList

    def DefaultClause =
        Token.DEFAULT ~ ":" ~ StatementList

    def LabelledStatement =
        Identifier ~ ":" ~ Statement

    def ThrowStatement =
        Token.THROW ~ Expression ~ ";"

    def TryStatement =
        Token.TRY ~ Block ~ ((Catch ~ opt(Finally)) | Finally)

    def Catch =
        Token.CATCH ~ "(" ~ Identifier ~ ")" ~ Block

    def Finally =
        Token.FINALLY ~ Block

    def DebuggerStatement =
        Token.DEBUGGER ~ ";"


    def PrimaryExpression :MultiParser[Any]=
        tokenids(Token.THIS) |
            Identifier |
            Literal |
            ArrayLiteral |
            ObjectLiteral |
            ("(" ~ Expression ~ ")")

    def Literal = Token.NULL | Token.TRUE | Token.FALSE | Token.NUMBER | Token.STRING | Token.REGEXP

    //    See 11.1.4
    def ArrayLiteral =
        "[" ~ repSepPlain(_Element, ",") ~ repPlain(",") ~ "]"

    def _Element = repPlain(",") ~ AssignmentExpression


    def ObjectLiteral =
        "{" ~ repSepPlain(PropertyAssignment, ",") ~ opt(",") ~ "}"


    def PropertyAssignment =
        (PropertyName ~ ":" ~ AssignmentExpression) |
            (Token.GET ~ PropertyName ~ "(" ~ ")" ~ "{" ~ FunctionBody ~ "}") |
            (Token.SET ~ PropertyName ~ "(" ~ PropertySetParameterList ~ ")" ~ "{" ~ FunctionBody ~ "}")

    def PropertyName =
        Identifier |
            Token.STRING|
            Token.NUMBER

    def PropertySetParameterList = Identifier

    def MemberExpression:MultiParser[Any] =
        PrimaryExpression |
            FunctionExpression |
            (MemberExpression ~ "[" ~ Expression ~ "]") |
            (MemberExpression ~ "." ~ Identifier/*Name*/) |
            (Token.NEW ~ MemberExpression ~ Arguments)

    def NewExpression:MultiParser[Any] =
        MemberExpression |
            (Token.NEW ~ NewExpression)

    def CallExpression :MultiParser[Any]=
        (MemberExpression ~ Arguments) |
            (CallExpression ~ (Arguments |
                ("[" ~ Expression ~ "]") |
                ("." ~ Identifier/*Name*/)))

    def Arguments = "(" ~ repSepPlain(AssignmentExpression, ",") ~ ")"


    def LeftHandSideExpression =
        NewExpression |
            CallExpression

    def PostfixExpression =
        LeftHandSideExpression ~ opt(Token.INC | Token.DEC)

    def UnaryExpression:MultiParser[Any] =
        (Token.DELPROP ~ UnaryExpression) |
            (Token.VOID ~ UnaryExpression) |
            (Token.TYPEOF ~ UnaryExpression) |
            (Token.INC ~ UnaryExpression) |
            (Token.DEC ~ UnaryExpression) |
            (Token.ADD ~ UnaryExpression) |
            (Token.SUB ~ UnaryExpression) |
            (Token.NEG ~ UnaryExpression) |
            (Token.NOT ~ UnaryExpression) |
            PostfixExpression


    def MultiplicativeExpression =
        repSepPlain(UnaryExpression, Token.MUL | Token.DIV | Token.MOD)

    //    See 11.6
    def AdditiveExpression =
        repSepPlain(MultiplicativeExpression, Token.ADD | Token.SUB)

    def ShiftExpression =
        repSepPlain(AdditiveExpression, Token.LSH | Token.RSH | Token.URSH)

    def RelationalExpression =
        repSepPlain(ShiftExpression, Token.LT | Token.GT | Token.GE | Token.LE | Token.INSTANCEOF | Token.IN)

    def RelationalExpressionNoIn =
        repSepPlain(ShiftExpression, Token.LT | Token.GT | Token.GE | Token.LE | Token.INSTANCEOF)

    def EqualityExpression =
        repSepPlain(RelationalExpression, Token.EQ | Token.NE | Token.SHEQ | Token.SHNE)
    def EqualityExpressionNoIn =
        repSepPlain(RelationalExpressionNoIn, Token.EQ | Token.NE | Token.SHEQ | Token.SHNE)

    def BitwiseANDExpression =
        repSepPlain(EqualityExpression, Token.BITAND)

    def BitwiseANDExpressionNoIn =
        repSepPlain(EqualityExpressionNoIn, Token.BITAND)

    def BitwiseXORExpression =
        repSepPlain(BitwiseANDExpression, Token.BITXOR)

    def BitwiseXORExpressionNoIn =
        repSepPlain(BitwiseANDExpressionNoIn, Token.BITXOR)

    def BitwiseORExpression =
        repSepPlain(BitwiseXORExpression, Token.BITOR)

    def BitwiseORExpressionNoIn =
        repSepPlain(BitwiseXORExpressionNoIn, Token.BITOR)

    def LogicalANDExpression =
        repSepPlain(BitwiseORExpression, Token.AND)

    def LogicalANDExpressionNoIn =
        repSepPlain(BitwiseORExpressionNoIn, Token.AND)

    def LogicalORExpression =
        repSepPlain(LogicalANDExpression, Token.OR)

    def LogicalORExpressionNoIn =
        repSepPlain(LogicalANDExpressionNoIn, Token.OR)

    def ConditionalExpression :MultiParser[Any]=
        LogicalANDExpression ~ repPlain(Token.HOOK ~ AssignmentExpression ~ ":" ~ AssignmentExpression)
    def ConditionalExpressionNoIn =
        LogicalANDExpressionNoIn ~ repPlain(Token.HOOK ~ AssignmentExpression ~ ":" ~ AssignmentExpressionNoIn)

    def AssignmentExpression :MultiParser[Any]=
        ConditionalExpression |
            (LeftHandSideExpression ~ AssignmentOp ~ AssignmentExpression)

    def AssignmentOp = "=" |
        Token.ASSIGN_BITOR |
        Token.ASSIGN_BITXOR |
        Token.ASSIGN_BITAND |
        Token.ASSIGN_LSH |
        Token.ASSIGN_RSH |
        Token.ASSIGN_URSH |
        Token.ASSIGN_ADD |
        Token.ASSIGN_SUB |
        Token.ASSIGN_MUL |
        Token.ASSIGN_DIV |
        Token.ASSIGN_MOD


    def AssignmentExpressionNoIn:MultiParser[Any] =
        ConditionalExpressionNoIn |
            (LeftHandSideExpression ~ AssignmentOp ~ AssignmentExpressionNoIn)

    def Expression:MultiParser[Any] =
        AssignmentExpression |
            (Expression ~ "," ~ AssignmentExpression)

    def ExpressionNoIn :MultiParser[Any]=
        AssignmentExpressionNoIn |
            (ExpressionNoIn ~ "," ~ AssignmentExpressionNoIn)

}
