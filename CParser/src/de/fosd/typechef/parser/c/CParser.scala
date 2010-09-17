package de.fosd.typechef.parser.c
import org.anarres.cpp.Token

import de.fosd.typechef.parser._
import de.fosd.typechef.featureexpr.FeatureExpr
/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% * based on ANTLR grammar from John D. Mitchell (john@non.net), Jul 12, 1997 */
class CParser extends MultiFeatureParser {
    type Elem = TokenWrapper

    def parse(code: String): ParseResult[AST, TokenWrapper] =
        parse(code, primaryExpr)

    def parse(code: String, mainProduction: (TokenReader[TokenWrapper], FeatureExpr) => MultiParseResult[AST, TokenWrapper]): ParseResult[AST, TokenWrapper] =
        mainProduction(CLexer.lex(code), FeatureExpr.base).forceJoin[AST](Alt.join)

    def parseAny(code: String, mainProduction: (TokenReader[TokenWrapper], FeatureExpr) => MultiParseResult[Any, TokenWrapper]): MultiParseResult[Any, TokenWrapper] =
        mainProduction(CLexer.lex(code), FeatureExpr.base)

    //{
    //import java.io.*;
    //
    //import antlr.CommonAST;
    //import antlr.DumpASTVisitor;
    //}
    //
    //                     
    //class StdCParser extends Parser;
    //
    //options
    //        {
    //        k = 2;
    //        exportVocab = STDC;
    //        buildAST = true;
    //        ASTLabelType = "TNode";
    //
    //        // Copied following options from java grammar.
    //        codeGenMakeSwitchThreshold = 2;
    //        codeGenBitsetTestThreshold = 3;
    //        }
    //
    //
    //{
    //    // Suppport C++-style single-line comments?
    //    public static boolean CPPComments = true;
    //
    //    // access to symbol table
    //    public CSymbolTable symbolTable = new CSymbolTable();
    //
    //    // source for names to unnamed scopes
    //    protected int unnamedScopeCounter = 0;
    //
    //    public boolean isTypedefName(String name) {
    //      boolean returnValue = false;
    //      TNode node = symbolTable.lookupNameInCurrentScope(name);
    //      for (; node != null; node = (TNode) node.getNextSibling() ) {
    //        if(node.getType() == LITERAL_typedef) {
    //            returnValue = true;
    //            break;
    //        }
    //      }
    //      return returnValue;
    //    }
    //
    //
    //    public String getAScopeName() {
    //      return "" + (unnamedScopeCounter++);
    //    }
    //
    //    public void pushScope(String scopeName) {
    //      symbolTable.pushScope(scopeName);
    //    }
    //
    //    public void popScope() {
    //      symbolTable.popScope();
    //    }
    //
    //        int traceDepth = 0;
    //        public void reportError(RecognitionException ex) {
    //          try {
    //            System.err.println("ANTLR Parsing Error: "+ex + " token name:" + tokenNames[LA(1)]);
    //            ex.printStackTrace(System.err);
    //          }
    //	  catch (TokenStreamException e) {
    //            System.err.println("ANTLR Parsing Error: "+ex);
    //            ex.printStackTrace(System.err);              
    //          }
    //        }
    //        public void reportError(String s) {
    //            System.err.println("ANTLR Parsing Error from String: " + s);
    //        }
    //        public void reportWarning(String s) {
    //            System.err.println("ANTLR Parsing Warning from String: " + s);
    //        }
    //        public void match(int t) throws MismatchedTokenException {
    //          boolean debugging = false;
    //          
    //          if ( debugging ) {
    //           for (int x=0; x<traceDepth; x++) System.out.print(" ");
    //           try {
    //            System.out.println("Match("+tokenNames[t]+") with LA(1)="+
    //                tokenNames[LA(1)] + ((inputState.guessing>0)?" [inputState.guessing "+ inputState.guessing + "]":""));
    //           }
    //           catch (TokenStreamException e) {
    //            System.out.println("Match("+tokenNames[t]+") " + ((inputState.guessing>0)?" [inputState.guessing "+ inputState.guessing + "]":""));
    //
    //           }
    //    
    //          }
    //          try {
    //            if ( LA(1)!=t ) {
    //                if ( debugging ){
    //                    for (int x=0; x<traceDepth; x++) System.out.print(" ");
    //                    System.out.println("token mismatch: "+tokenNames[LA(1)]
    //                                + "!="+tokenNames[t]);
    //                }
    //	        throw new MismatchedTokenException(tokenNames, LT(1), t, false, getFilename());
    //
    //            } else {
    //                // mark token as consumed -- fetch next token deferred until LA/LT
    //                consume();
    //            }
    //          }
    //          catch (TokenStreamException e) {
    //          }
    //    
    //        }
    //        public void traceIn(String rname) {
    //          traceDepth += 1;
    //          for (int x=0; x<traceDepth; x++) System.out.print(" ");
    //          try {
    //            System.out.println("> "+rname+"; LA(1)==("+ tokenNames[LT(1).getType()] 
    //                + ") " + LT(1).getText() + " [inputState.guessing "+ inputState.guessing + "]");
    //          }
    //          catch (TokenStreamException e) {
    //          }
    //        }
    //        public void traceOut(String rname) {
    //          for (int x=0; x<traceDepth; x++) System.out.print(" ");
    //          try {
    //            System.out.println("< "+rname+"; LA(1)==("+ tokenNames[LT(1).getType()] 
    //                + ") "+LT(1).getText() + " [inputState.guessing "+ inputState.guessing + "]");
    //          }
    //          catch (TokenStreamException e) {
    //          }
    //          traceDepth -= 1;
    //        }
    //    
    //}
    //
    //
    //
    //translationUnit
    //        :       externalList
    //
    //        |       /* Empty source files are *not* allowed.  */
    //                {
    //                System.err.println ( "Empty source file!" );
    //                }
    //        ;
    //
    //
    //externalList
    //        :       ( externalDef )+
    //        ;
    //
    //
    //externalDef
    //        :       ( "typedef" | declaration )=> declaration
    //        |       functionDef
    //        |       asm_expr
    //        ;
    //
    //
    //asm_expr
    //        :       "asm"^ 
    //                ("volatile")? LCURLY! expr RCURLY! SEMI!
    //        ;
    //
    //
    def declaration: MultiParser[Declaration] =
        declSpecifiers ~ opt(initDeclList) ~ SEMI ^^! (AltDeclaration.join, { case d ~ i ~ _ => ADeclaration(d, i) })

    def declSpecifiers: MultiParser[List[Specifier]] =
        rep1(storageClassSpecifier | typeQualifier | typeSpecifier)

    //
    def storageClassSpecifier: MultiParser[Specifier] =
        specifier("auto") | specifier("register") | specifier("typedef") | functionStorageClassSpecifier

    def functionStorageClassSpecifier: MultiParser[Specifier] =
        specifier("extern") | specifier("static")

    def typeQualifier: MultiParser[Specifier] =
        specifier("const") | specifier("volatile")

    def specifier(name: String) = textToken(name) ^^ { t => OtherSpecifier(t.getText) }

    def typeSpecifier: MultiParser[TypeSpecifier] = ((textToken("void")
        | textToken("char")
        | textToken("short")
        | textToken("int")
        | textToken("long")
        | textToken("float")
        | textToken("double")
        | textToken("signed")
        | textToken("unsigned")) ^^ { (t: Elem) => PrimitiveTypeSpecifier(t.getText) }
        //            | structOrUnionSpecifier
        //            | enumSpecifier
        | /*{ specCount == 0 }?*/ typedefName ^^ { TypeDefTypeSpecifier(_) })

    def typedefName = ID
    //            :       { isTypedefName ( LT(1).getText() ) }?

    //    def structOrUnionSpecifier =
    //        structOrUnion ~
    //            (Id ~> LCURLY ~ structDeclarationList <~ RCURLY
    //                | LCURLY ~> structDeclarationList <~ RCURLY !
    //                | ID)

    def structOrUnion =
        textToken("struct") | textToken("union")

    //    def structDeclarationList =
    //        rep1(structDeclaration)

    //    def structDeclaration =
    //        specifierQualifierList ~ structDeclaratorList <~ rep1(SEMI)

    def specifierQualifierList =
        rep(typeSpecifier | typeQualifier)

    //    def structDeclaratorList =
    //        rep1Sep(structDeclarator, COMMA)
    //
    //    //
    //    def structDeclarator =
    //        (COLON ~ constExpr | declarator(false) ~ opt(COLON ~ constExpr))

    //
    //
    //enumSpecifier
    //        :       "enum"^
    //                ( ( ID LCURLY )=> i:ID LCURLY enumList[i.getText()] RCURLY!
    //                | LCURLY enumList["anonymous"] RCURLY!
    //                | ID
    //                )
    //        ;
    //
    //
    //enumList[String enumName]
    //        :       enumerator[enumName] ( COMMA! enumerator[enumName] )*  
    //        ;
    //
    //enumerator[String enumName]
    //        :       i:ID                { symbolTable.add(  i.getText(),
    //                                                        #(   null,
    //                                                            #[LITERAL_enum, "enum"],
    //                                                            #[ ID, enumName]
    //                                                         )
    //                                                     );
    //                                    }
    //                (ASSIGN constExpr)?
    //        ;
    //
    //
    def initDeclList: MultiParser[List[InitDeclarator]] =
        rep1Sep(initDecl, COMMA);

    def initDecl: MultiParser[InitDeclarator] =
        declarator(false) ~ opt(ASSIGN ~> initializer | COLON ~> expr) ^^
            { case d ~ Some(i: Initializer) => InitDeclaratorI(d, Some(i)); case d ~ Some(e: Expr) => InitDeclaratorE(d, e); case d ~ None => InitDeclaratorI(d, None); }

    def pointerGroup: MultiParser[List[Pointer]] =
        rep1(STAR ~> opt(typeQualifierList) ^^ { case Some(l) => Pointer(l); case None => Pointer(List()) })

    def typeQualifierList: MultiParser[List[Specifier]] =
        rep(typeQualifier)

    def idList: MultiParser[List[Id]] =
        rep1Sep(ID, COMMA)

    def initializer: MultiParser[Initializer] =
        (assignExpr ^^ { InitializerExpr(_) }
            | LCURLY ~> rep1Sep(initializer, COMMA) ~ opt(COMMA) <~ RCURLY ^^ { case i ~ o => InitializerList(i) })

    def declarator(isFunctionDefinition: Boolean): MultiParser[Declarator] =
        (optList(pointerGroup) ~ (ID | LPAREN ~> declarator(false) <~ RPAREN) ~
            rep(
                LPAREN ~> (parameterTypeList ^^ { DeclParameterTypeList(_) }
                    | optList(idList) ^^ { DeclIdentifierList(_) }) <~ RPAREN
                | LBRACKET ~> opt(constExpr) <~ RBRACKET ^^ { DeclArrayAccess(_) }
                )) ^^ {
            case pointers ~(id: Id) ~ ext => DeclaratorId(pointers, id, ext);
            case pointers ~(decl: Declarator) ~ ext => DeclaratorDecl(pointers, decl, ext)
        }

    // 
    def parameterTypeList: MultiParser[List[ParameterDeclaration]] =
        rep1Sep(parameterDeclaration, COMMA) ~ opt(COMMA ~> VARARGS) ^^
            { case l ~ Some(v) => l ++ List(VarArgs()); case l ~ None => l }

    def parameterDeclaration: MultiParser[ParameterDeclaration] =
        declSpecifiers ~ opt(declarator(false) | nonemptyAbstractDeclarator) ^^
            {
                case s ~ Some(d: Declarator) => ParameterDeclarationD(s, d)
                case s ~ Some(d: AbstractDeclarator) => ParameterDeclarationAD(s, d)
                case s ~ None => ParameterDeclaration(s)
            }

    //
    ///* JTC:
    // * This handles both new and old style functions.
    // * see declarator rule to see differences in parameters
    // * and here (declaration SEMI)* is the param type decls for the
    // * old style.  may want to do some checking to check for illegal
    // * combinations (but I assume all parsed code will be legal?)
    // */
    //
    //functionDef
    //                            { String declName; }
    //        :       ( (functionDeclSpecifiers)=> ds:functionDeclSpecifiers
    //                |  //epsilon
    //                )
    //                declName = d:declarator[true]
    //                            {
    //                            AST d2, ds2;
    //                            d2 = astFactory.dupList(#d);
    //                            ds2 = astFactory.dupList(#ds);
    //                            symbolTable.add(declName, #(null, ds2, d2));
    //                            pushScope(declName);
    //                            }
    //                ( declaration )* (VARARGS)? ( SEMI! )*
    //                            { popScope(); }
    //                compoundStatement[declName]
    //                            { ## = #( #[NFunctionDef], ## );}
    //        ;
    //
    //functionDeclSpecifiers
    //                                { int specCount = 0; }
    //        :       (               options {   // this loop properly aborts when
    //                                            // it finds a non-typedefName ID MBZ
    //                                            warnWhenFollowAmbig = false;
    //                                        } :
    //                  functionStorageClassSpecifier
    //                | typeQualifier
    //                | ( "struct" | "union" | "enum" | typeSpecifier[specCount] )=>
    //                        specCount = typeSpecifier[specCount]
    //                )+
    //        ;
    //
    def declarationList: MultiParser[List[Opt[Declaration]]] =
        declaration ~ repOpt(declaration, AltDeclaration.join) ^^ { case d ~ l => List(Opt(FeatureExpr.base, d)) ++ l }

    //
    //declarationPredictor
    //        :       (options {      //only want to look at declaration if I don't see typedef
    //                    warnWhenFollowAmbig = false;
    //                }:
    //                "typedef"
    //                | declaration
    //                )
    //        ;
    //
    //
    def compoundStatement: MultiParser[CompoundStatement] =
        LCURLY ~> declarationList ~ statementList <~ RCURLY ^^ { case decl ~ stmt => CompoundStatement(decl, stmt) }

    def statementList: MultiParser[List[Opt[Statement]]] =
        repOpt(statement, AltStatement.join)

    def statement: MultiParser[Statement] = (SEMI ^^ { _ => EmptyStatement() } // Empty statements
        | compoundStatement // Group of statements
        | expr <~ SEMI ^^ { ExprStatement(_) } // Expressions
        //// Iteration statements:
        | textToken("while") ~ LPAREN ~ expr ~ RPAREN ~ statement ^^ { case _ ~ _ ~ e ~ _ ~ s => WhileStatement(e, s) }
        | textToken("do") ~ statement ~ textToken("while") ~ LPAREN ~ expr ~ RPAREN ~ SEMI ^^ { case _ ~ s ~ _ ~ _ ~ e ~ _ ~ _ => DoStatement(e, s) }
        | textToken("for") ~ LPAREN ~ opt(expr) ~ SEMI ~ opt(expr) ~ SEMI ~ opt(expr) ~ RPAREN ~ statement ^^ { case _ ~ _ ~ e1 ~ _ ~ e2 ~ _ ~ e3 ~ _ ~ s => ForStatement(e1, e2, e3, s) } //                                    {
        //// Jump statements:
        | textToken("goto") ~> ID <~ SEMI ^^ { GotoStatement(_) }
        | textToken("continue") ~ SEMI ^^ { _ => ContinueStatement() }
        | textToken("break") ~ SEMI ^^ { _ => BreakStatement() }
        | textToken("return") ~> opt(expr) <~ SEMI ^^ { ReturnStatement(_) }
        //// Labeled statements:
        | ID <~ COLON ^^ { LabelStatement(_) }
        | textToken("case") ~ constExpr ~ COLON ~ statement ^^ { case _ ~ e ~ _ ~ s => CaseStatement(e, s) }
        | textToken("default") ~> COLON ~> statement ^^ { DefaultStatement(_) }
        //// Selection statements:
        | textToken("if") ~ LPAREN ~ expr ~ RPAREN ~ statement ~ opt(textToken("else") ~> statement) ^^ { case _ ~ _ ~ ex ~ _ ~ ts ~ es => IfStatement(ex, ts, es) }
        | textToken("switch") ~ LPAREN ~ expr ~ RPAREN ~ statement ^^ { case _ ~ _ ~ e ~ _ ~ s => SwitchStatement(e, s) }) ^^! (AltStatement.join, s => s)

    def expr: MultiParser[Expr] = assignExpr ~ rep(COMMA ~> assignExpr) ^^
        { case e ~ l => if (l.isEmpty) e else ExprList(List(e) ++ l) }

    def assignExpr: MultiParser[Expr] =
        conditionalExpr ~ opt(assignOperator ~ assignExpr) ^^
            { case e ~ Some(o ~ e2) => AssignExpr(e, o.getText, e2); case e ~ None => e }

    def assignOperator = (ASSIGN
        | DIV_ASSIGN
        | PLUS_ASSIGN
        | MINUS_ASSIGN
        | STAR_ASSIGN
        | MOD_ASSIGN
        | RSHIFT_ASSIGN
        | LSHIFT_ASSIGN
        | BAND_ASSIGN
        | BOR_ASSIGN
        | BXOR_ASSIGN)

    def conditionalExpr: MultiParser[Expr] = logicalOrExpr ~ opt(QUESTION ~ expr ~ COLON ~ conditionalExpr) ^^
        { case e ~ Some(q ~ e2 ~ c ~ e3) => ConditionalExpr(e, e2, e3); case e ~ None => e }
    def constExpr = conditionalExpr
    def logicalOrExpr: MultiParser[Expr] = nAryExpr(logicalAndExpr, LOR)
    def logicalAndExpr: MultiParser[Expr] = nAryExpr(inclusiveOrExpr, LAND)
    def inclusiveOrExpr: MultiParser[Expr] = nAryExpr(exclusiveOrExpr, BOR)
    def exclusiveOrExpr: MultiParser[Expr] = nAryExpr(bitAndExpr, BXOR)
    def bitAndExpr: MultiParser[Expr] = nAryExpr(equalityExpr, BAND)
    def equalityExpr: MultiParser[Expr] = nAryExpr(relationalExpr, EQUAL | NOT_EQUAL)
    def relationalExpr: MultiParser[Expr] = nAryExpr(shiftExpr, LT | LTE | GT | GTE)
    def shiftExpr: MultiParser[Expr] = nAryExpr(additiveExpr, LSHIFT | RSHIFT)
    def additiveExpr: MultiParser[Expr] = nAryExpr(multExpr, PLUS | MINUS)
    def multExpr: MultiParser[Expr] = nAryExpr(castExpr, STAR | DIV | MOD)

    def nAryExpr(innerExpr: MultiParser[Expr], operations: MultiParser[TokenWrapper]) =
        innerExpr ~ rep(operations ~ innerExpr ^^ { case t ~ e => (t.getText, e) }) ^^ { case e ~ l => if (l.isEmpty) e else NAryExpr(e, l) }

    def castExpr: MultiParser[Expr] =
        LPAREN ~ typeName ~ RPAREN ~ castExpr ^^ { case b1 ~ t ~ b2 ~ e => CastExpr(t, e) } | unaryExpr

    //
    //
    //typeName
    //        :       specifierQualifierList (nonemptyAbstractDeclarator)?
    //        ;
    //
    def nonemptyAbstractDeclarator: MultiParser[AbstractDeclarator] =
        (pointerGroup ~
            rep((LPAREN ~> (nonemptyAbstractDeclarator | optList(parameterTypeList) ^^ { DeclParameterTypeList(_) }) <~ RPAREN)
                | (LBRACKET ~> opt(expr) <~ RBRACKET ^^ { DeclArrayAccess(_) })
                ) ^^ { case pointers ~ directDecls => AbstractDeclarator(pointers, directDecls) }

            | rep1((LPAREN ~> (nonemptyAbstractDeclarator | optList(parameterTypeList) ^^ { DeclParameterTypeList(_) }) <~ RPAREN)
                | (LBRACKET ~> opt(expr) <~ RBRACKET ^^ { DeclArrayAccess(_) })
                ) ^^ { AbstractDeclarator(List(), _) })


    //                                
    //        ;
    //
    ///* JTC:
    //
    //LR rules:
    //
    //abstractDeclarator
    //        :       nonemptyAbstractDeclarator
    //        |       // null
    //        ;
    //
    //nonemptyAbstractDeclarator
    //        :       LPAREN  nonemptyAbstractDeclarator RPAREN
    //        |       abstractDeclarator LPAREN RPAREN
    //        |       abstractDeclarator (LBRACKET (expr)? RBRACKET)
    //        |       STAR abstractDeclarator
    //        ;
    //*/
    //
    def unaryExpr: MultiParser[Expr] = (postfixExpr
        | { INC ~ unaryExpr | DEC ~ unaryExpr } ^^ { case p ~ e => UnaryExpr(p.getText, e) }
        | unaryOperator ~ castExpr ^^ { case u ~ c => UCastExpr(u.getText, c) }
        | textToken("sizeof") ~> {
            LPAREN ~> typeName <~ RPAREN ^^ { SizeOfExprT(_) } |
                unaryExpr ^^ { SizeOfExprU(_) }
        })

    //        ;
    //
    //
    def unaryOperator =
        BAND | STAR | PLUS | MINUS | BNOT | LNOT

    def postfixExpr = primaryExpr ~ postfixSuffix ^^ { case p ~ s => if (s.isEmpty) p else PostfixExpr(p, s) }

    def postfixSuffix: MultiParser[List[PostfixSuffix]] = rep[PostfixSuffix](
        { PTR ~ ID | DOT ~ ID } ^^ { case ~(e, id: Id) => PointerPostfixSuffix(e.getText, id) }
        | functionCall
        | LBRACKET ~> expr <~ RBRACKET ^^ { ArrayAccess(_) }
        | { INC | DEC } ^^ { t => SimplePostfixSuffix(t.getText) }
        )
    //
    def functionCall: MultiParser[FunctionCall] =
        LPAREN ~> opt(argExprList) <~ RPAREN ^^ { case Some(l) => FunctionCall(l); case None => FunctionCall(ExprList(List())) }

    def primaryExpr: MultiParser[Expr] =
        ID | numConst | stringConst | LPAREN ~> expr <~ RPAREN

    def typeName = ID //TODO separate this later
    def ID: MultiParser[Id] = token("id", _.isIdentifier) ^^ { t => Id(t.getText) }
    def stringConst: MultiParser[StringLit] = token("string literal", _.getType == Token.STRING) ^^ { t => StringLit(t.getText) }; def numConst: MultiParser[Constant] = token("number", _.isInteger) ^^ { t => Constant(t.getText) } |
        token("number", _.getType == Token.CHARACTER) ^^ { t => Constant(t.getText) }

    def argExprList: MultiParser[ExprList] =
        rep1Sep(assignExpr, COMMA) ^^ { ExprList(_) }

    //protected
    //charConst
    //        :       CharLiteral
    //        ;
    //
    //
    //protected
    //stringConst
    //        :       (StringLiteral)+                { ## = #(#[NStringSeq], ##); }
    //        ;
    //
    //
    //protected
    //intConst
    //        :       IntOctalConst
    //        |       LongOctalConst
    //        |       UnsignedOctalConst
    //        |       IntIntConst
    //        |       LongIntConst
    //        |       UnsignedIntConst
    //        |       IntHexConst
    //        |       LongHexConst
    //        |       UnsignedHexConst
    //        ;
    //
    //
    //protected
    //floatConst
    //        :       FloatDoubleConst
    //        |       DoubleDoubleConst
    //        |       LongDoubleConst
    //        ;
    //
    //
    //
    //
    //    
    //
    //dummy
    //        :       NTypedefName
    //        |       NInitDecl
    //        |       NDeclarator
    //        |       NStructDeclarator
    //        |       NDeclaration
    //        |       NCast
    //        |       NPointerGroup
    //        |       NExpressionGroup
    //        |       NFunctionCallArgs
    //        |       NNonemptyAbstractDeclarator
    //        |       NInitializer
    //        |       NStatementExpr
    //        |       NEmptyExpression
    //        |       NParameterTypeList
    //        |       NFunctionDef
    //        |       NCompoundStatement
    //        |       NParameterDeclaration
    //        |       NCommaExpr
    //        |       NUnaryExpr
    //        |       NLabel
    //        |       NPostfixExpr
    //        |       NRangeExpr
    //        |       NStringSeq
    //        |       NInitializerElementLabel
    //        |       NLcurlyInitializer
    //        |       NAsmAttribute
    //        |       NGnuAsmExpr
    //        |       NTypeMissing
    //        ;
    //
    //
    //
    //    
    //
    //
    //{
    //        //import CToken;
    //        import java.io.*;
    //        //import LineObject;
    //        import antlr.*;
    //}
    //
    //class StdCLexer extends Lexer;
    //
    //options
    //        {
    //        k = 3;
    //        exportVocab = STDC;
    //        testLiterals = false;
    //        }
    //
    //{
    //  LineObject lineObject = new LineObject();
    //  String originalSource = "";
    //  PreprocessorInfoChannel preprocessorInfoChannel = new PreprocessorInfoChannel();
    //  int tokenNumber = 0;
    //  boolean countingTokens = true;
    //  int deferredLineCount = 0;
    //
    //  public void setCountingTokens(boolean ct) 
    //  {
    //    countingTokens = ct;
    //    if ( countingTokens ) {
    //      tokenNumber = 0;
    //    }
    //    else {
    //      tokenNumber = 1;
    //    }
    //  }
    //
    //  public void setOriginalSource(String src) 
    //  {
    //    originalSource = src;
    //    lineObject.setSource(src);
    //  }
    //  public void setSource(String src) 
    //  {
    //    lineObject.setSource(src);
    //  }
    //  
    //  public PreprocessorInfoChannel getPreprocessorInfoChannel() 
    //  {
    //    return preprocessorInfoChannel;
    //  }
    //
    //  public void setPreprocessingDirective(String pre)
    //  {
    //    preprocessorInfoChannel.addLineForTokenNumber( pre, new Integer(tokenNumber) );
    //  }
    //  
    //  protected Token makeToken(int t)
    //  {
    //    if ( t != Token.SKIP && countingTokens) {
    //        tokenNumber++;
    //    }
    //    CToken tok = (CToken) super.makeToken(t);
    //    tok.setLine(lineObject.line);
    //    tok.setSource(lineObject.source);
    //    tok.setTokenNumber(tokenNumber);
    //
    //    lineObject.line += deferredLineCount;
    //    deferredLineCount = 0;
    //    return tok;
    //  }
    //
    //    public void deferredNewline() { 
    //        deferredLineCount++;
    //    }
    //
    //    public void newline() { 
    //        lineObject.newline();
    //    }
    //
    //
    //
    //
    //
    //
    //}
    //
    //protected
    //Vocabulary
    //        :       '\3'..'\377'
    //        ;
    //
    //
    ///* Operators: */
    //
    def ASSIGN = textToken('=')
    def COLON = textToken(':')
    def COMMA = textToken(',')
    def QUESTION = textToken('?')
    def SEMI = textToken(';')
    def PTR = textToken("->")
    def VARARGS = textToken("...")
    def DOT = textToken(".")
    def LPAREN = textToken('(')
    def RPAREN = textToken(')')
    def LBRACKET = textToken('[')
    def RBRACKET = textToken(']')
    def LCURLY = textToken('{')
    def RCURLY = textToken('}')
    //
    def EQUAL = textToken("==")
    def NOT_EQUAL = textToken("!=")
    def LTE = textToken("<=")
    def LT = textToken("<")
    def GTE = textToken(">=")
    def GT = textToken(">")
    //
    def DIV = textToken('/')
    def DIV_ASSIGN = textToken("/=")
    def PLUS = textToken('+')
    def PLUS_ASSIGN = textToken("+=")
    def INC = textToken("++")
    def MINUS = textToken('-')
    def MINUS_ASSIGN = textToken("-=")
    def DEC = textToken("--")
    def STAR = textToken('*')
    def STAR_ASSIGN = textToken("*=")
    def MOD = textToken('%')
    def MOD_ASSIGN = textToken("%=")
    def RSHIFT = textToken(">>")
    def RSHIFT_ASSIGN = textToken(">>=")
    def LSHIFT = textToken("<<")
    def LSHIFT_ASSIGN = textToken("<<=")
    //
    def LAND = textToken("&&")
    def LNOT = textToken('!')
    def LOR = textToken("||")
    //
    def BAND = textToken('&')
    def BAND_ASSIGN = textToken("&=")
    def BNOT = textToken('~')
    def BOR = textToken('|')
    def BOR_ASSIGN = textToken("|=")
    def BXOR = textToken('^')
    def BXOR_ASSIGN = textToken("^=")
    //
    //
    //Whitespace
    //        :       ( ( '\003'..'\010' | '\t' | '\013' | '\f' | '\016'.. '\037' | '\177'..'\377' | ' ' )
    //                | "\r\n"                { newline(); }
    //                | ( '\n' | '\r' )       { newline(); }
    //                )                       { _ttype = Token.SKIP;  }
    //        ;
    //
    //
    //Comment
    //        :       "/*"
    //                ( { LA(2) != '/' }? '*'
    //                | "\r\n"                { deferredNewline(); }
    //                | ( '\r' | '\n' )       { deferredNewline();    }
    //                | ~( '*'| '\r' | '\n' )
    //                )*
    //                "*/"                    { _ttype = Token.SKIP;  
    //                                        }
    //        ;
    //
    //
    //CPPComment
    //        :
    //                "//" ( ~('\n') )* 
    //                        {
    //                        _ttype = Token.SKIP;
    //                        }
    //        ;
    //
    //PREPROC_DIRECTIVE
    //options {
    //  paraphrase = "a line directive";
    //}
    //
    //        :
    //        '#'
    //        ( ( "line" || (( ' ' | '\t' | '\014')+ '0'..'9')) => LineDirective      
    //            | (~'\n')*                                  { setPreprocessingDirective(getText()); }
    //        )
    //                {  
    //                    _ttype = Token.SKIP;
    //                }
    //        ;
    //
    //protected  Space:
    //        ( ' ' | '\t' | '\014')
    //        ;
    //
    //protected LineDirective
    //{
    //        boolean oldCountingTokens = countingTokens;
    //        countingTokens = false;
    //}
    //:
    //                {
    //                        lineObject = new LineObject();
    //                        deferredLineCount = 0;
    //                }
    //        ("line")?  //this would be for if the directive started "#line", but not there for GNU directives
    //        (Space)+
    //        n:Number { lineObject.setLine(Integer.parseInt(n.getText())); } 
    //        (Space)+
    //        (       fn:StringLiteral {  try { 
    //                                          lineObject.setSource(fn.getText().substring(1,fn.getText().length()-1)); 
    //                                    } 
    //                                    catch (StringIndexOutOfBoundsException e) { /*not possible*/ } 
    //                                 }
    //                | fi:ID { lineObject.setSource(fi.getText()); }
    //        )?
    //        (Space)*
    //        ("1"            { lineObject.setEnteringFile(true); } )?
    //        (Space)*
    //        ("2"            { lineObject.setReturningToFile(true); } )?
    //        (Space)*
    //        ("3"            { lineObject.setSystemHeader(true); } )?
    //        (Space)*
    //        ("4"            { lineObject.setTreatAsC(true); } )?
    //        (~('\r' | '\n'))*
    //        ("\r\n" | "\r" | "\n")
    //                {
    //                        preprocessorInfoChannel.addLineForTokenNumber(new LineObject(lineObject), new Integer(tokenNumber));
    //                        countingTokens = oldCountingTokens;
    //                }
    //        ;
    //
    //
    //
    ///* Literals: */
    //
    ///*
    // * Note that we do NOT handle tri-graphs nor multi-byte sequences.
    // */
    //
    //
    ///*
    // * Note that we can't have empty character constants (even though we
    // * can have empty strings :-).
    // */
    //CharLiteral
    //        :       '\'' ( Escape | ~( '\'' ) ) '\''
    //        ;
    //
    //
    ///*
    // * Can't have raw imbedded newlines in string constants.  Strict reading of
    // * the standard gives odd dichotomy between newlines & carriage returns.
    // * Go figure.
    // */
    //StringLiteral
    //        :       '"'
    //                ( Escape
    //                | ( 
    //                    '\r'        { deferredNewline(); }
    //                  | '\n'        {
    //                                deferredNewline();
    //                                _ttype = BadStringLiteral;
    //                                }
    //                  | '\\' '\n'   {
    //                                deferredNewline();
    //                                }
    //                  )
    //                | ~( '"' | '\r' | '\n' | '\\' )
    //                )*
    //                '"'
    //        ;
    //
    //
    //protected BadStringLiteral
    //        :       // Imaginary token.
    //        ;
    //
    //
    ///*
    // * Handle the various escape sequences.
    // *
    // * Note carefully that these numeric escape *sequences* are *not* of the
    // * same form as the C language numeric *constants*.
    // *
    // * There is no such thing as a binary numeric escape sequence.
    // *
    // * Octal escape sequences are either 1, 2, or 3 octal digits exactly.
    // *
    // * There is no such thing as a decimal escape sequence.
    // *
    // * Hexadecimal escape sequences are begun with a leading \x and continue
    // * until a non-hexadecimal character is found.
    // *
    // * No real handling of tri-graph sequences, yet.
    // */
    //
    //protected
    //Escape  
    //        :       '\\'
    //                ( options{warnWhenFollowAmbig=false;}:
    //                  'a'
    //                | 'b'
    //                | 'f'
    //                | 'n'
    //                | 'r'
    //                | 't'
    //                | 'v'
    //                | '"'
    //                | '\''
    //                | '\\'
    //                | '?'
    //                | ('0'..'3') ( options{warnWhenFollowAmbig=false;}: Digit ( options{warnWhenFollowAmbig=false;}: Digit )? )?
    //                | ('4'..'7') ( options{warnWhenFollowAmbig=false;}: Digit )?
    //                | 'x' ( options{warnWhenFollowAmbig=false;}: Digit | 'a'..'f' | 'A'..'F' )+
    //                )
    //        ;
    //
    //
    ///* Numeric Constants: */
    //
    //protected
    //Digit
    //        :       '0'..'9'
    //        ;
    //
    //protected
    //LongSuffix
    //        :       'l'
    //        |       'L'
    //        ;
    //
    //protected
    //UnsignedSuffix
    //        :       'u'
    //        |       'U'
    //        ;
    //
    //protected
    //FloatSuffix
    //        :       'f'
    //        |       'F'
    //        ;
    //
    //protected
    //Exponent
    //        :       ( 'e' | 'E' ) ( '+' | '-' )? ( Digit )+
    //        ;
    //
    //
    //protected
    //DoubleDoubleConst:;
    //
    //protected
    //FloatDoubleConst:;
    //
    //protected
    //LongDoubleConst:;
    //
    //protected
    //IntOctalConst:;
    //
    //protected
    //LongOctalConst:;
    //
    //protected
    //UnsignedOctalConst:;
    //
    //protected
    //IntIntConst:;
    //
    //protected
    //LongIntConst:;
    //
    //protected
    //UnsignedIntConst:;
    //
    //protected
    //IntHexConst:;
    //
    //protected
    //LongHexConst:;
    //
    //protected
    //UnsignedHexConst:;
    //
    //
    //
    //
    //Number
    //        :       ( ( Digit )+ ( '.' | 'e' | 'E' ) )=> ( Digit )+
    //                ( '.' ( Digit )* ( Exponent )?
    //                | Exponent
    //                )                       { _ttype = DoubleDoubleConst;   }
    //                ( FloatSuffix           { _ttype = FloatDoubleConst;    }
    //                | LongSuffix            { _ttype = LongDoubleConst;     }
    //                )?
    //
    //        |       ( "..." )=> "..."       { _ttype = VARARGS;     }
    //
    //        |       '.'                     { _ttype = DOT; }
    //                ( ( Digit )+ ( Exponent )?
    //                                        { _ttype = DoubleDoubleConst;   }
    //                  ( FloatSuffix         { _ttype = FloatDoubleConst;    }
    //                  | LongSuffix          { _ttype = LongDoubleConst;     }
    //                  )?
    //                )?
    //
    //        |       '0' ( '0'..'7' )*       { _ttype = IntOctalConst;       }
    //                ( LongSuffix            { _ttype = LongOctalConst;      }
    //                | UnsignedSuffix        { _ttype = UnsignedOctalConst;  }
    //                )?
    //
    //        |       '1'..'9' ( Digit )*     { _ttype = IntIntConst;         }
    //                ( LongSuffix            { _ttype = LongIntConst;        }
    //                | UnsignedSuffix        { _ttype = UnsignedIntConst;    }
    //                )?
    //
    //        |       '0' ( 'x' | 'X' ) ( 'a'..'f' | 'A'..'F' | Digit )+
    //                                        { _ttype = IntHexConst;         }
    //                ( LongSuffix            { _ttype = LongHexConst;        }
    //                | UnsignedSuffix        { _ttype = UnsignedHexConst;    }
    //                )?
    //        ;
    //
    //
    //ID
    //        options 
    //                {
    //                testLiterals = true; 
    //                }
    //        :       ( 'a'..'z' | 'A'..'Z' | '_' )
    //                ( 'a'..'z' | 'A'..'Z' | '_' | '0'..'9' )*
    //        ;

    def textToken(t: String): MultiParser[Elem] = token(t, _.getText == t); def textToken(t: Char) = token(t.toString, _.getText == t.toString);

}