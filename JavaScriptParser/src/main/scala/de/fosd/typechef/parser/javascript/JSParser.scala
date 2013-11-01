package de.fosd.typechef.parser.javascript

import de.fosd.typechef.parser._
import de.fosd.typechef.parser.common.CharacterToken
import de.fosd.typechef.featureexpr.{FeatureExprFactory, FeatureExpr}
import de.fosd.typechef.conditional.Conditional

//
///*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
// * based on GCIDE grammar in CIDE
// * which is again based on the publically available JavaCC grammar
// */
//
class JSParser extends MultiFeatureParser {
    type Elem = CharacterToken
    type TypeContext = Null

    private implicit def char(s: Char): MultiParserExt[Elem] = token(s.toString, _.getKindChar() == s)
    private implicit def keyword(s: String): MultiParserExt[String] = {
        assert(s.length > 0)
        var result = char(s.charAt(0)) ^^ {_.getText()}
        for (i <- 1 until s.length)
            result = result ~ char(s.charAt(i)) ^^ {case a ~ b => a + b.getText()}
        new MultiParserExt(result)
    }
    //implict hack to introduce ~~~
    implicit private def parserExtension[U](s: MultiParser[U]): MultiParserExt[U] = new MultiParserExt(s)

    private class MultiParserExt[T](s: MultiParser[T]) extends MultiParser[T] {
        // sequence two parsers separated by whitespace
        def ~~~[U](thatParser: => MultiParser[U]): MultiParser[~[T, U]] = (s <~ WSs) ~ thatParser
        def ~~~?[U](thatParser: => MultiParser[U]): MultiParser[~[T, U]] = (s <~ opt(WSs)) ~ thatParser
        def <~~~[U](thatParser: => MultiParser[U]): MultiParser[T] = (s <~ WSs) <~ thatParser
        def <~~~?[U](thatParser: => MultiParser[U]): MultiParser[T] = (s <~ opt(WSs)) <~ thatParser
        def ~~~>[U](thatParser: => MultiParser[U]): MultiParser[U] = (s <~ WSs) ~> thatParser
        def ~~~?>[U](thatParser: => MultiParser[U]): MultiParser[U] = (s <~ opt(WSs)) ~> thatParser
        def apply(v1: Input, v2: ParserState): MultiParseResult[T] = s.apply(v1, v2)
    }

    private def repSepPlainWSo[T, U](p: => MultiParser[T], separator: => MultiParser[U]): MultiParser[List[T]] =
        repSepPlain(p, WSo ~ separator ~ WSo)
    private def repSepPlainWSs[T, U](p: => MultiParser[T], separator: => MultiParser[U]): MultiParser[List[T]] =
        repSepPlain(p, WSs ~ separator ~ WSs)

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////// Grammar (relaxted about white space) ////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////


    def Program: MultiParser[Conditional[JSProgram]] =
        repOpt(SourceElement) ^^! {JSProgram(_)}

    def SourceElement: MultiParser[JSSourceElement] = FunctionDeclaration | Statement

    def FunctionDeclaration: MultiParser[JSFunctionDeclaration] =
        "function" ~~~ Identifier ~~~ '(' ~~~ FormalParameterList ~~~ ')' ~~~ '{' ~~~ FunctionBody ~~~ '}' ^^ {
            case _ ~ id ~ _ ~ param ~ _ ~ _ ~ body ~ _ => JSFunctionDeclaration(id, param, body)
        }

    def FunctionExpression: MultiParser[JSFunctionExpression] =
        "function" ~~~ opt(Identifier) ~~~ '(' ~~~ FormalParameterList ~~~ ')' ~~~ '{' ~~~ FunctionBody ~~~ '}' ^^ {
            case _ ~ id ~ _ ~ param ~ _ ~ _ ~ body ~ _ => JSFunctionExpression(id, param, body)
        }

    def FormalParameterList: MultiParser[Any] = repSepPlain(Identifier, ',')
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

    def Block = char('{') ~~~?> StatementList <~~~? '}' ^^ {JSBlock(_)}

    def StatementList = repOpt(Statement)

    def VariableStatement =
        "var" ~~~> rep1Sep(VariableDeclaration(false), ',') <~~~? ';' ^^ {JSVariableStatement(_)}

    def VariableDeclarationListNoIn = repSepPlain1(VariableDeclaration(true), ',')

    def VariableDeclaration(noIn: Boolean) =
        Identifier ~ opt(WSs ~> Initialiser(noIn)) ^^ {case n ~ i => JSVariableDeclaration(n, i)}

    def Initialiser(noIn: Boolean) =
        "=" ~~~?> AssignmentExpression(noIn)


    def EmptyStatement =
        ';' ^^ {x => JSEmptyStatement()}

    def ExpressionStatement =
        Expression <~~~? ';' ^^ {JSExprStatement(_)}

    def IfStatement =
        "if" ~~~? '(' ~~~? Expression ~~~? ')' ~~~? Statement ~ opt(WSs ~ "else" ~~~ Statement) ^^ {x => JSOtherStatement()}

    def IterationStatement =
        (("do" ~~~ Statement ~~~ "while" ~~~? '(' ~~~? Expression ~~~? ')' ~~~? ';') |
            ("while" ~~~? '(' ~~~? Expression ~~~? ')' ~~~? Statement) |
            ("for" ~~~? '(' ~~~? opt(Expression(true)) ~~~? ';' ~~~? opt(Expression) ~~~? ';' ~~~? opt(Expression) ~~~? ')' ~~~? Statement) |
            ("for" ~~~? '(' ~~~? keyword("var") ~~~ VariableDeclarationListNoIn ~~~? ';' ~~~? opt(Expression) ~~~ ';' ~~~ opt(Expression) ~~~ ')' ~~~ Statement) |
            ("for" ~~~? '(' ~~~? LeftHandSideExpression ~~~ "in" ~~~ Expression ~~~? ')' ~~~? Statement) |
            ("for" ~~~? '(' ~~~? "var" ~~~ VariableDeclaration(true) ~~~ "in" ~~~ Expression ~~~? ')' ~~~? Statement)) ^^ {x => JSOtherStatement()}

    def ContinueStatement =
        "continue" ~~~ opt(Identifier) ~~~ ';' ^^ {x => JSOtherStatement()}

    def BreakStatement =
        "break" ~~~ opt(Identifier) ~~~ ';' ^^ {x => JSOtherStatement()}

    def ReturnStatement =
        "return" ~~~ opt(Expression) ~~~? ';' ^^ {x => JSOtherStatement()}

    def WithStatement =
        "with" ~~~? '(' ~~~? Expression ~~~? ')' ~~~? Statement ^^ {x => JSOtherStatement()}

    def SwitchStatement =
        "switch" ~~~? '(' ~~~? Expression ~~~? ')' ~~~? CaseBlock ^^ {x => JSOtherStatement()}

    def CaseBlock =
        '{' ~~~? CaseClauses ~~~? opt(DefaultClause ~~~? CaseClauses) ~~~? '}'

    def CaseClauses =
        repOpt(CaseClause)

    def CaseClause =
        "case" ~~~ Expression ~~~? ':' ~~~? StatementList

    def DefaultClause =
        "default" ~~~? ':' ~~~? StatementList

    def LabelledStatement =
        Identifier ~~~? ':' ~~~? Statement ^^ {x => JSOtherStatement()}

    def ThrowStatement =
        "throw" ~~~ Expression ~~~? ';' ^^ {x => JSOtherStatement()}

    def TryStatement =
        "try" ~~~? Block ~~~? ((Catch ~~~ opt(Finally)) | Finally) ^^ {x => JSOtherStatement()}

    def Catch =
        "catch" ~~~? '(' ~~~? Identifier ~~~? ')' ~~~? Block

    def Finally =
        "finally" ~~~? Block

    def DebuggerStatement =
        "debugger" ~~~? ';' ^^ {x => JSOtherStatement()}


    def PrimaryExpression: MultiParser[JSExpression] =
        keyword("this") ^^ {x => JSThis()} |
            Identifier |
            Literal |
            ArrayLiteral ^^ {x => JSExpr()} |
            ObjectLiteral ^^ {x => JSExpr()} |
            ('(' ~~~?> Expression <~~~? ')')


    //    See 11.1.4
    def ArrayLiteral =
        '[' ~~~ repSepPlain(_Element, ',') ~~~ repPlain(',') ~~~ ']'

    def _Element = repPlain(',') ~~~? AssignmentExpression


    def ObjectLiteral =
        '{' ~~~? repSepPlainWSo(PropertyAssignment, ',') ~~~? opt(',') ~~~? '}'


    def PropertyAssignment =
        ("get" ~~~ PropertyName ~~~? '(' ~~~? ')' ~~~? '{' ~~~? FunctionBody ~~~? '}') |
            ("set" ~~~ PropertyName ~~~? '(' ~~~? PropertySetParameterList ~~~? ')' ~~~? '{' ~~~? FunctionBody ~~~? '}') |
            (PropertyName ~~~? ':' ~~~? AssignmentExpression)

    def PropertyName =
        Identifier |
            StringLiteral |
            NumericLiteral

    def PropertySetParameterList = Identifier

    def MemberExpression: MultiParser[JSExpression] =
        (PrimaryExpression |
            FunctionExpression |
            ("new" ~~~ MemberExpression ~~~? Arguments ^^ {x => JSExpr()})) ~~~?
            repPlain(('[' ~~~? Expression ~~~? ']') |
                ("." ~~~? Identifier /*Name*/)) ^^ {case e ~ p => e}


    //    def NewExpression: MultiParser[Any] =
    //        MemberExpression |
    //            (Token.NEW ~~~ NewExpression)

    def LeftHandSideExpression: MultiParser[JSExpression] =
        ((MemberExpression ~~~? opt(Arguments) ^^ {case e ~ Some(a) => JSFunctionCall(e, a); case e ~ None => e}) ~~~?
            repPlain((Arguments |
                ('[' ~~~? Expression ~~~? ']') |
                ("." ~~~? Identifier /*Name*/))) ^^ {case x ~ e => x}) |
            ("new" ~~~ LeftHandSideExpression ^^ {x => JSExpr()})


    def Arguments = '(' ~~~?> repSepPlainWSo(AssignmentExpression, ',') <~~~? ')'


    //    def LeftHandSideExpression =
    //        NewExpression |
    //            CallExpression

    def PostfixExpression: MultiParser[JSExpression] =
        LeftHandSideExpression ~~~? opt("++" | "--") ^^ {case e ~ p => e}

    def UnaryExpression: MultiParser[JSExpression] =
        ("delete" ~~~ UnaryExpression ^^ {x => JSExpr()}) |
            ("void" ~~~ UnaryExpression ^^ {x => JSExpr()}) |
            ("typeof" ~~~ UnaryExpression ^^ {x => JSExpr()}) |
            ("++" ~~~? UnaryExpression ^^ {x => JSExpr()}) |
            ("--" ~~~? UnaryExpression ^^ {x => JSExpr()}) |
            ('+' ~~~? UnaryExpression ^^ {x => JSExpr()}) |
            ('-' ~~~? UnaryExpression ^^ {x => JSExpr()}) |
            ('~' ~~~? UnaryExpression ^^ {x => JSExpr()}) |
            ('!' ~~~? UnaryExpression ^^ {x => JSExpr()}) |
            PostfixExpression


    def MultiplicativeExpression =
        naryExpr(UnaryExpression, char('*') | char('/') | char('%'))

    //    See 11.6
    def AdditiveExpression =
        naryExpr(MultiplicativeExpression, char('+') | char('-'))

    def ShiftExpression =
        naryExpr(AdditiveExpression, keyword("<<") | keyword(">>") | keyword(">>>"))

    def RelationalExpression(noIn: Boolean) = {
        var ops = keyword("<=") | keyword(">=") | char('<') | char('>') | (keyword("instanceof") <~ WSs)
        if (!noIn) ops = ops | (keyword("in") <~ WSs)
        naryExpr(ShiftExpression, ops)
    }

    def EqualityExpression(noIn: Boolean) =
        naryExpr(RelationalExpression(noIn), keyword("===") | keyword("!==") | keyword("==") | keyword("!="))

    def BitwiseANDExpression(noIn: Boolean) =
        naryExpr(EqualityExpression(noIn), char('&'))


    def BitwiseXORExpression(noIn: Boolean) =
        naryExpr(BitwiseANDExpression(noIn), char('^'))

    def BitwiseORExpression(noIn: Boolean) =
        naryExpr(BitwiseXORExpression(noIn), char('|'))


    def LogicalANDExpression(noIn: Boolean) =
        naryExpr(BitwiseORExpression(noIn), keyword("&&"))


    def LogicalORExpression(noIn: Boolean) =
        naryExpr(LogicalANDExpression(noIn), keyword("||"))


    private def naryExpr(expr: MultiParser[JSExpression], op: MultiParser[Any]): MultiParser[JSExpression] =
        expr ~ opt(WSo ~> op ~~~? naryExpr(expr, op)) ^^ {
            case e1 ~ Some(o ~ e2) => JSBinaryOp(e1, charToString(o), e2)
            case e ~ None => e
        }

    def ConditionalExpression(noIn: Boolean): MultiParser[JSExpression] =
        LogicalORExpression(noIn) ~~~? repPlain('?' ~~~? AssignmentExpression ~~~? ':' ~~~? AssignmentExpression(noIn)) ^^ {case e ~ _ => e}

    def AssignmentExpression: MultiParser[JSExpression] = AssignmentExpression(false)
    def AssignmentExpression(noIn: Boolean): MultiParser[JSExpression] =
        ConditionalExpression(false) ~ opt(WSo ~> AssignmentOp ~~~? AssignmentExpression(noIn)) ^^ {
            case e ~ Some(o ~ e2) => JSAssignment(e, charToString(o), e2)
            case e ~ None => e
        } |
            fail("expected assignexpr")


    def AssignmentOp: MultiParser[Any] =
        """=
          *=
          /=
          %=
          +=
          -=
          <<=
          >>=
          >>>=
          &=
          ^=
          |=""".split("\n").map(_.trim).map(keyword).reduce(_ | _)


    def Expression: MultiParser[JSExpression] = Expression(false)
    def Expression(noIn: Boolean): MultiParser[JSExpression] =
        repSepPlain1(AssignmentExpression(noIn), ',') ^^ {x => if (x.size == 1) x.head else JSExprList(x)}


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////// Lexical grammar (strict about white space) ////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    def WS: MultiParser[Elem] = char(' ') | '\t' | '\n' | '\r'

    def WSs: MultiParser[Any] = rep1(WS)

    def WSo: MultiParser[Any] = repOpt(WS)


    def Literal: MultiParser[JSLit] = (("null" | "true" | "false") ^^ {JSLit(_)}) |
        NumericLiteral |
        StringLiteral |
        RegularExpressionLiteral

    def NumericLiteral: MultiParser[JSLit] = HexIntegerLiteral | DecimalLiteral

    def DecimalLiteral: MultiParser[JSLit] = (rep1Plain(Digit) ~ opt('.' ~ repPlain(Digit)) ~ opt(ExponentPart) |
        '.' ~ repPlain(Digit) ~ opt(ExponentPart)) ^^ {x => JSLit(charToString(x))}

    def SignedInteger = opt(char('+') | char('-')) ~ repPlain(Digit)

    def ExponentPart = (char('e') | char('E')) ~ SignedInteger

    def HexIntegerLiteral: MultiParser[JSLit] = char('0') ~ (char('x') | char('X')) ~! rep1Plain(HexDigit) ^^ {x => JSLit(charToString(x))}

    private def charToString(x: Any): String = x match {
        case e: Elem => e.getText()
        case e: String => e
        case l: List[Any] => l.map(charToString).mkString
        case x ~ y => charToString(x) + charToString(y)
        case Some(x) => charToString(x)
        case None => ""
        case e => assert(false, "unsupported expr " + e); ""
    }

    def StringLiteral: MultiParser[JSLit] =
        (char('\'') ~ repPlain(StringCharacters('\'')) ~ char('\'') |
            char('"') ~ repPlain(StringCharacters('"')) ~ char('"')) ^^ {x => JSLit(charToString(x))}


    def StringCharacters(except: Char) = token("any except \\ or '", x => !(Set(except, '\\', '\n') contains x.getKindChar())) |
        LineContinuation |
        EscapeSequence


    def LineContinuation = '\\' ~ '\n'

    def EscapeSequence = //TODO incomplete
        '\\' ~ (char('\'') | '"' | '\\' | 'b' | 'f' | 'n' | 'r' | 't' | 'v')


    def Letter: MultiParser[Elem] = token("letter", (('a' until 'z') ++ ('A' until 'Z') :+ '_') contains _.getKindChar())
    def Digit: MultiParser[Elem] = token("letter", ('0' until '9') contains _.getKindChar())
    def HexDigit: MultiParser[Elem] = Digit | token("hex", (('a' until 'f') ++ ('A' until 'F')) contains _.getKindChar())

    def IdentifierStart: MultiParser[Elem] = Letter | '$' | '_'
    def IdentifierPart: MultiParser[Elem] = Letter | Digit
    def _Identifier: MultiParser[JSIdentifier] =
        IdentifierStart ~ repPlain(IdentifierPart) ^^ {case first ~ rest => JSIdentifier((first :: rest).mkString)}

    def Identifier: MultiParser[JSIdentifier] = new MultiParser[JSIdentifier] {
        name = "identifier"

        def apply(in: Input, feature: FeatureExpr): MultiParseResult[JSIdentifier] = {
            val result = _Identifier(in, feature)
            result.mapfr(FeatureExprFactory.True, (_, r) => r match {
                case s@Success(t: JSIdentifier, restIn) =>
                    if (reservedWords contains t.name) Failure("not an identifier, reserved keyword", restIn, List())
                    else s
                case e => e
            })
        }
    }


    def RegularExpressionLiteral: MultiParser[JSLit] =
        "/" ~ RegularExpressionBody ~ "/" ~ opt(RegularExpressionFlags) ^^ {x => JSLit(charToString(x))}
    //
    def RegularExpressionBody =
        RegularExpressionFirstChar ~ RegularExpressionChars

    def RegularExpressionChars =
        repPlain(RegularExpressionChar)

    def RegularExpressionFirstChar =
        token("regex", x => !(Set('\n', '*', '\\', '/', '[') contains x.getKindChar())) |
            RegularExpressionBackslashSequence |
            RegularExpressionClass

    def RegularExpressionChar =
        token("regex", x => !(Set('\n', '\\', '/', '[') contains x.getKindChar())) |
            RegularExpressionBackslashSequence |
            RegularExpressionClass

    def RegularExpressionBackslashSequence =
        char('\\') ~ token("anynonterminator", _.getKindChar() != '\n')

    def RegularExpressionClass =
        '[' ~ repPlain(RegularExpressionClassChar) ~ ']'

    def RegularExpressionClassChar =
        token("regex", x => !(Set('\n', '\\', ']') contains x.getKindChar())) |
            RegularExpressionBackslashSequence

    def RegularExpressionFlags = repPlain(IdentifierPart)


    //test parser only, do not use
    def Id2Id: MultiParser[JSIdentifier ~ JSIdentifier] = Identifier ~~~ Identifier
    def InstanceOf: MultiParser[String] = keyword("instanceof")


    val reservedWords =
        "break do instanceof typeof     case else new var    catch finally return void    continue for switch while    debugger function this with    default if throw        delete in try   enum extends super    class export import    const".split("\\s+").toSet


}
