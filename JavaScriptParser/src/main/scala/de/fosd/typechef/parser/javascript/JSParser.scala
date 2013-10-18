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
        case ":" => tokenids(Token.COLON)
        case "," => tokenids(Token.COMMA)
        case "=" => tokenids(Token.ASSIGN)
        case "." => tokenids(Token.DOT)
        case "/" => tokenids(Token.DIV)
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
        Block |
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
            DebuggerStatement | fail("expected statement")

    def Block = "{" ~> StatementList <~ "}" ^^ {JSBlock(_)}

    def StatementList = repOpt(Statement)

    def VariableStatement =
        Token.VAR ~> rep1Sep(VariableDeclaration, ",") <~ ";" ^^ {JSVariableStatement(_)}

    def VariableDeclarationListNoIn = repSepPlain1(VariableDeclarationNoIn, ",")

    def VariableDeclaration =
        Identifier ~ opt(Initialiser) ^^ {case n ~ i => JSVariableDeclaration(n, i)}

    def VariableDeclarationNoIn =
        Identifier ~ opt(InitialiserNoIn) ^^ {case n ~ i => JSVariableDeclaration(n, i)}

    def Initialiser =
        "=" ~> AssignmentExpression

    def InitialiserNoIn =
        "=" ~> AssignmentExpressionNoIn

    def EmptyStatement =
        ";" ^^ {x => JSEmptyStatement()}

    def ExpressionStatement =
        Expression <~ ";" ^^ {JSExprStatement(_)}

    def IfStatement =
        Token.IF ~ "(" ~ Expression ~ ")" ~ Statement ~ opt(Token.ELSE ~ Statement) ^^ {x => JSOtherStatement()}

    def IterationStatement =
        ((Token.DO ~ Statement ~ Token.WHILE ~ "(" ~ Expression ~ ")" ~ ";") |
            (Token.WHILE ~ "(" ~ Expression ~ ")" ~ Statement) |
            (Token.FOR ~ "(" ~ opt(ExpressionNoIn) ~ ";" ~ opt(Expression) ~ ";" ~ opt(Expression) ~ ")" ~ Statement) |
            (Token.FOR ~ "(" ~ Token.VAR ~ VariableDeclarationListNoIn ~ ";" ~ opt(Expression) ~ ";" ~ opt(Expression) ~ ")" ~ Statement) |
            (Token.FOR ~ "(" ~ LeftHandSideExpression ~ Token.IN ~ Expression ~ ")" ~ Statement) |
            (Token.FOR ~ "(" ~ Token.VAR ~ VariableDeclarationNoIn ~ Token.IN ~ Expression ~ ")" ~ Statement)) ^^ {x => JSOtherStatement()}

    def ContinueStatement =
        Token.CONTINUE ~ opt(Identifier) ~ ";" ^^ {x => JSOtherStatement()}

    def BreakStatement =
        Token.BREAK ~ opt(Identifier) ~ ";" ^^ {x => JSOtherStatement()}

    def ReturnStatement =
        Token.RETURN ~ opt(Expression) ~ ";" ^^ {x => JSOtherStatement()}

    def WithStatement =
        Token.WITH ~ "(" ~ Expression ~ ")" ~ Statement ^^ {x => JSOtherStatement()}

    def SwitchStatement =
        Token.SWITCH ~ "(" ~ Expression ~ ")" ~ CaseBlock ^^ {x => JSOtherStatement()}

    def CaseBlock =
        "{" ~ CaseClauses ~ opt(DefaultClause ~ CaseClauses) ~ "}"

    def CaseClauses =
        repOpt(CaseClause)

    def CaseClause =
        Token.CASE ~ Expression ~ ":" ~ StatementList

    def DefaultClause =
        Token.DEFAULT ~ ":" ~ StatementList

    def LabelledStatement =
        Identifier ~ ":" ~ Statement ^^ {x => JSOtherStatement()}

    def ThrowStatement =
        Token.THROW ~ Expression ~ ";" ^^ {x => JSOtherStatement()}

    def TryStatement =
        Token.TRY ~ Block ~ ((Catch ~ opt(Finally)) | Finally) ^^ {x => JSOtherStatement()}

    def Catch =
        Token.CATCH ~ "(" ~ Identifier ~ ")" ~ Block

    def Finally =
        Token.FINALLY ~ Block

    def DebuggerStatement =
        Token.DEBUGGER ~ ";" ^^ {x => JSOtherStatement()}


    def PrimaryExpression: MultiParser[JSExpression] =
        tokenids(Token.THIS) ^^ {x => JSThis()} |
            Identifier ^^ {JSId(_)} |
            Literal |
            ArrayLiteral ^^ {x => JSExpr()} |
            ObjectLiteral ^^ {x => JSExpr()} |
            ("(" ~> Expression <~ ")")

    def Literal = (tokenids(Token.NULL) | tokenids(Token.TRUE) | tokenids(Token.FALSE) | tokenids(Token.NUMBER) | tokenids(Token.STRING)) ^^ {x => JSLit(x.getText())} | RegularExpressionLiteral

    //    See 11.1.4
    def ArrayLiteral =
        "[" ~ repSepPlain(_Element, ",") ~ repPlain(",") ~ "]"

    def _Element = repPlain(",") ~ AssignmentExpression


    def ObjectLiteral =
        "{" ~ repSepPlain(PropertyAssignment, ",") ~ opt(",") ~ "}"


    def PropertyAssignment =
        (Token.GET ~ PropertyName ~ "(" ~ ")" ~ "{" ~ FunctionBody ~ "}") |
            (Token.SET ~ PropertyName ~ "(" ~ PropertySetParameterList ~ ")" ~ "{" ~ FunctionBody ~ "}") |
            (PropertyName ~ ":" ~ AssignmentExpression)

    def PropertyName =
        Identifier |
            Token.STRING |
            Token.NUMBER

    def PropertySetParameterList = Identifier

    def MemberExpression: MultiParser[JSExpression] =
        (PrimaryExpression |
            FunctionExpression |
            (Token.NEW ~ MemberExpression ~ Arguments ^^ {x => JSExpr()})) ~
            repPlain(("[" ~ Expression ~ "]") |
                ("." ~ Identifier /*Name*/)) ^^ {case e ~ p => e}


    //    def NewExpression: MultiParser[Any] =
    //        MemberExpression |
    //            (Token.NEW ~ NewExpression)

    def LeftHandSideExpression: MultiParser[JSExpression] =
        ((MemberExpression ~ opt(Arguments) ^^ {case e ~ Some(a) => JSFunctionCall(e, a); case e ~ None => e}) ~
            repPlain((Arguments |
                ("[" ~ Expression ~ "]") |
                ("." ~ Identifier /*Name*/))) ^^ {case x ~ e => x}) |
            (Token.NEW ~ LeftHandSideExpression ^^ {x => JSExpr()})


    def Arguments = "(" ~> repSepPlain(AssignmentExpression, ",") <~ ")"


    //    def LeftHandSideExpression =
    //        NewExpression |
    //            CallExpression

    def PostfixExpression: MultiParser[JSExpression] =
        LeftHandSideExpression ~ opt(tokenids(Token.INC) | Token.DEC) ^^ {case e ~ p => e}

    def UnaryExpression: MultiParser[JSExpression] =
        (Token.DELPROP ~ UnaryExpression ^^ {x => JSExpr()}) |
            (Token.VOID ~ UnaryExpression ^^ {x => JSExpr()}) |
            (Token.TYPEOF ~ UnaryExpression ^^ {x => JSExpr()}) |
            (Token.INC ~ UnaryExpression ^^ {x => JSExpr()}) |
            (Token.DEC ~ UnaryExpression ^^ {x => JSExpr()}) |
            (Token.ADD ~ UnaryExpression ^^ {x => JSExpr()}) |
            (Token.SUB ~ UnaryExpression ^^ {x => JSExpr()}) |
            (Token.BITNOT ~ UnaryExpression ^^ {x => JSExpr()}) |
            (Token.NOT ~ UnaryExpression ^^ {x => JSExpr()}) |
            PostfixExpression


    def MultiplicativeExpression =
        naryExpr(UnaryExpression, tokenids(Token.MUL) | Token.DIV | Token.MOD)

    //    See 11.6
    def AdditiveExpression =
        naryExpr(MultiplicativeExpression, tokenids(Token.ADD) | Token.SUB)

    def ShiftExpression =
        naryExpr(AdditiveExpression, tokenids(Token.LSH) | Token.RSH | Token.URSH)

    def RelationalExpression =
        naryExpr(ShiftExpression, tokenids(Token.LT) | Token.GT | Token.GE | Token.LE | Token.INSTANCEOF | Token.IN)

    def RelationalExpressionNoIn =
        naryExpr(ShiftExpression, tokenids(Token.LT) | Token.GT | Token.GE | Token.LE | Token.INSTANCEOF)

    def EqualityExpression =
        naryExpr(RelationalExpression, tokenids(Token.EQ) | Token.NE | Token.SHEQ | Token.SHNE)
    def EqualityExpressionNoIn =
        naryExpr(RelationalExpressionNoIn, tokenids(Token.EQ) | Token.NE | Token.SHEQ | Token.SHNE)

    def BitwiseANDExpression =
        naryExpr(EqualityExpression, Token.BITAND)

    def BitwiseANDExpressionNoIn =
        naryExpr(EqualityExpressionNoIn, Token.BITAND)

    def BitwiseXORExpression =
        naryExpr(BitwiseANDExpression, Token.BITXOR)

    def BitwiseXORExpressionNoIn =
        naryExpr(BitwiseANDExpressionNoIn, Token.BITXOR)

    def BitwiseORExpression =
        naryExpr(BitwiseXORExpression, Token.BITOR)

    def BitwiseORExpressionNoIn =
        naryExpr(BitwiseXORExpressionNoIn, Token.BITOR)

    def LogicalANDExpression =
        naryExpr(BitwiseORExpression, Token.AND)

    def LogicalANDExpressionNoIn =
        naryExpr(BitwiseORExpressionNoIn, Token.AND)

    def LogicalORExpression =
        naryExpr(LogicalANDExpression, Token.OR)

    def LogicalORExpressionNoIn =
        naryExpr(LogicalANDExpressionNoIn, Token.OR)

    private def naryExpr(expr: MultiParser[JSExpression], op: MultiParser[Elem]): MultiParser[JSExpression] =
        expr ~ opt(op ~ naryExpr(expr, op)) ^^ {
            case e1 ~ Some(o ~ e2) => JSBinaryOp(e1, o.getKind(), e2)
            case e ~ None => e
        }

    def ConditionalExpression: MultiParser[JSExpression] =
        LogicalORExpression ~ repPlain(Token.HOOK ~ AssignmentExpression ~ ":" ~ AssignmentExpression) ^^ {case e ~ _ => e}
    def ConditionalExpressionNoIn =
        LogicalORExpressionNoIn ~ repPlain(Token.HOOK ~ AssignmentExpression ~ ":" ~ AssignmentExpressionNoIn)

    def AssignmentExpression: MultiParser[JSExpression] =
        ConditionalExpression ~ opt(AssignmentOp ~ AssignmentExpression) ^^ {
            case e ~ Some(o ~ e2) => JSAssignment(e, o.getKind, e2)
            case e ~ None => e
        } |
            fail("expected assignexpr")


    def AssignmentOp: MultiParser[Elem] = tokenids(Token.ASSIGN) |
        tokenids(Token.ASSIGN_BITOR) |
        tokenids(Token.ASSIGN_BITXOR) |
        tokenids(Token.ASSIGN_BITAND) |
        tokenids(Token.ASSIGN_LSH) |
        tokenids(Token.ASSIGN_RSH) |
        tokenids(Token.ASSIGN_URSH) |
        tokenids(Token.ASSIGN_ADD) |
        tokenids(Token.ASSIGN_SUB) |
        tokenids(Token.ASSIGN_MUL) |
        tokenids(Token.ASSIGN_DIV) |
        tokenids(Token.ASSIGN_MOD)


    def AssignmentExpressionNoIn: MultiParser[JSExpression] =
        ConditionalExpression ~ opt(AssignmentOp ~ AssignmentExpressionNoIn) ^^ {
            case e ~ Some(o ~ e2) => JSAssignment(e, o.getKind, e2)
            case e ~ None => e
        }

    def Expression =
        repSepPlain1(AssignmentExpression, ",") ^^ {x => if (x.size == 1) x.head else JSExprList(x)}

    def ExpressionNoIn =
        repSepPlain1(AssignmentExpressionNoIn, ",") ^^ {x => JSExpr()}


    def RegularExpressionLiteral =
        "/" ~ RegularExpressionBody ~ "/" ~ opt(RegularExpressionFlags) ^^ {x => JSExpr()}

    def RegularExpressionBody =
        RegularExpressionFirstChar ~ RegularExpressionChars

    def RegularExpressionChars =
        repPlain(RegularExpressionChar)

    def RegularExpressionFirstChar =
        token("regex", t => !t.afterNewLine && !(Set(Token.MUL, Token.DIV, Token.LB, Token.ERROR) contains t.getKind())) |
            RegularExpressionBackslashSequence |
            RegularExpressionClass

    def RegularExpressionChar =
        token("regex", t => !t.afterNewLine && !(Set(Token.DIV, Token.LB, Token.ERROR) contains t.getKind())) |
            RegularExpressionBackslashSequence |
            RegularExpressionClass

    def RegularExpressionBackslashSequence =
        Token.ERROR ~ token("any", x => true)

    def RegularExpressionClass =
        "[" ~ repPlain(RegularExpressionClassChar) ~ "]"

    def RegularExpressionClassChar =
        token("regex", t => !t.afterNewLine && !(Set(Token.RB, Token.ERROR) contains t.getKind())) |
            RegularExpressionBackslashSequence

    def RegularExpressionFlags = token("regexflags", _.getText().matches("[gimy]*"))


}
