package de.fosd.typechef.parser.c


import de.fosd.typechef.parser._
import de.fosd.typechef.conditional._
import de.fosd.typechef.featureexpr.FeatureExprFactory.True
import de.fosd.typechef.featureexpr.{FeatureModel, FeatureExpr}
import scala.Some
import de.fosd.typechef.parser.~
import de.fosd.typechef.conditional.Opt

/*%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 * based on ANTLR grammar from John D. Mitchell (john@non.net), Jul 12, 1997
 * and Monty Zukowski (jamz@cdsnet.net) April 28, 1998
 */

class CParser(featureModel: FeatureModel = null, debugOutput: Boolean = false) extends MultiFeatureParser(featureModel, debugOutput) {
    type Elem = CToken
    type AbstractToken = CToken
    type TypeContext = CTypeContext

    def parse[T](tokenStream: TokenReader[AbstractToken, CTypeContext], mainProduction: (TokenReader[AbstractToken, CTypeContext], FeatureExpr) => MultiParseResult[T]): MultiParseResult[T] =
        mainProduction(tokenStream, True)

    def parseAny(tokenStream: TokenReader[AbstractToken, CTypeContext], mainProduction: (TokenReader[AbstractToken, CTypeContext], FeatureExpr) => MultiParseResult[Any]): MultiParseResult[Any] =
        mainProduction(tokenStream, True)

    //parser
    val keywords = Set("__real__", "__imag__", "__alignof__", "__alignof", "__asm", "__asm__", "__attribute__", "__attribute",
        "__complex__", "__const", "__const__", "__inline", "__inline__", "__restrict", "__restrict__",
        "__signed", "__signed__", "__typeof", "__typeof__", "__volatile", "__volatile__", "asm",
        "volatile", "typeof", "auto", "register", "typedef", "extern", "static", "inline",
        "const", "volatile", "restrict", "char", "short", "int", "long", "__int128", "float", "double",
        "signed", "unsigned", "_Bool", "struct", "union", "enum", "if", "while", "do",
        "for", "goto", "continue", "break", "return", "case", "default", "else", "switch",
        "sizeof", "_Pragma", "__expectType", "__expectNotType", "__thread")
    val predefinedTypedefs = Set("__builtin_va_list", "__builtin_type")

    def translationUnit = externalList ^^ {
        TranslationUnit(_)
    }

    def externalList: MultiParser[List[Opt[ExternalDef]]] =
        repOpt(externalDef, "externalDef") ^^ ConditionalLib.flatten

    def externalDef: MultiParser[Conditional[ExternalDef]] =
    // first part (with lookahead) only for error reporting, i.e.don 't try to parse anything else after a typedef
        (lookahead(textToken("typedef")) ~! declaration ^^ {
            case _ ~ r => r
        } |
            asm_expr | declaration |
            functionDef | typelessDeclaration | pragma | expectType | expectNotType | (SEMI ^^ {
            x => EmptyExternalDef()
        })) !

    //parse with LPAREN instead of LCURLY in antlr grammar. seems to be the correct gnuc impl according to gcc
    def asm_expr: MultiParser[AsmExpr] =
        asm ~! opt(volatile) ~ LPAREN ~ expr ~ RPAREN ~ rep1(SEMI) ^^ {
            case _ ~ v ~ _ ~ e ~ _ ~ _ => AsmExpr(v.isDefined, e)
        }

    def declaration: MultiParser[Declaration] =
        (declSpecifiers ~~ optList(initDeclList) ~~ rep1(SEMI) ^^ {
            case d ~ i ~ _ => Declaration(d, i)
        } changeContext ({
            (result: Declaration, featureCtx, typeCtx: TypeContext) => {
                var c = typeCtx
                if (result.declSpecs.exists(o => o.entry == TypedefSpecifier()))
                    for (decl: Opt[InitDeclarator] <- result.init) {
                        c = c.addType(decl.entry.declarator.getName, featureCtx)
                        //                            println("add type " + decl.declarator.getName)//DEBUG only
                    }
                c
            }
        }))

    //gnu
    def typelessDeclaration: MultiParser[TypelessDeclaration] =
        initDeclList <~ SEMI ^^ {
            x => TypelessDeclaration(x)
        }

    def declSpecifiers: MultiParser[List[Opt[Specifier]]] =
        specList(storageClassSpecifier | typeQualifier | attributeDecl) | fail("declSpecifier expected")

    def storageClassSpecifier: MultiParser[Specifier] =
        (specifier("auto") ^^^ AutoSpecifier()) |
            (specifier("register") ^^^ RegisterSpecifier()) |
            (textToken("typedef") ^^^ TypedefSpecifier()) |
            functionStorageClassSpecifier

    def staticSpecifier(): MultiParser[Specifier] =
        specifier("static") ^^^ StaticSpecifier()
    def functionStorageClassSpecifier: MultiParser[Specifier] =
        (specifier("extern") ^^^ ExternSpecifier()) |
            staticSpecifier | inline

    def typeQualifier: MultiParser[Specifier] =
        const | volatile | restrict | thread

    def specifier(name: String) = textToken(name)

    def typeSpecifier: MultiParser[TypeSpecifier] = ((textToken("void") ^^^ VoidSpecifier())
        | (textToken("char") ^^^ CharSpecifier())
        | (textToken("short") ^^^ ShortSpecifier())
        | (textToken("int") ^^^ IntSpecifier())
        | (textToken("long") ^^^ LongSpecifier())
        | (textToken("__int128") ^^^ Int128Specifier())
        | (textToken("float") ^^^ FloatSpecifier())
        | (textToken("double") ^^^ DoubleSpecifier())
        | signed
        | (textToken("unsigned") ^^^ UnsignedSpecifier())
        | (textToken("_Bool")
        | textToken("_Complex")
        | textToken("__complex__")) ^^ {
        (t: Elem) => OtherPrimitiveTypeSpecifier(t.getText)
    }
        | structOrUnionSpecifier
        | enumSpecifier
        //TypeDefName handled elsewhere!
        | (typeof ~ LPAREN ~> (
        (typeName ^^ {
            TypeOfSpecifierT(_)
        }) | (expr ^^ {
            TypeOfSpecifierU(_)
        })
        ) <~ RPAREN))

    //TODO need to split when conditionally defined as typedef
    def typedefName =
        tokenWithContext("type",
            (token, featureContext, typeContext) =>
                isIdentifier(token) && (predefinedTypedefs.contains(token.getText) || typeContext.knowsType(token.getText, featureContext, featureModel))) ^^ {
            t => Id(t.getText)
        } ^^ {
            TypeDefTypeSpecifier(_)
        }
    def notypedefName =
        tokenWithContext("notype",
            (token, featureContext, typeContext) => isIdentifier(token) && !predefinedTypedefs.contains(token.getText) && !typeContext.knowsType(token.getText, featureContext, featureModel)) ^^ {
            t => Id(t.getText)
        }

    def structOrUnionSpecifier: MultiParser[StructOrUnionSpecifier] =
        structOrUnion ~ repOpt(attributeDecl) ~ structOrUnionSpecifierBody ^^ {
            case isUnion ~ attr1 ~ ((id, list, attr2)) => StructOrUnionSpecifier(isUnion, id, list, attr1, attr2)
        }

    private def structOrUnionSpecifierBody: MultiParser[(Option[Id], Option[List[Opt[StructDeclaration]]], List[Opt[AttributeSpecifier]])] =
    // XXX: PG: SEMI after LCURLY????
        (ID ~~ LCURLY ~! (repOpt(SEMI) ~ structDeclarationList0 ~ RCURLY) ~ repOpt(attributeDecl) ^^ {
            case id ~ _ ~ (_ ~ list ~ _) ~ attr => (Some(id), Some(list), attr)
        }) |
            (LCURLY ~ repOpt(SEMI) ~ structDeclarationList0 ~ RCURLY ~ repOpt(attributeDecl) ^^ {
                case _ ~ _ ~ list ~ _ ~ attr => (None, Some(list), attr)
            }) |
            (ID ^^ {
                case id => (Some(id), None, Nil)
            })

    def structOrUnion: MultiParser[Boolean] = // isUnion
        (textToken("struct") ^^^ (false) | textToken("union") ^^^ (true))

    def structDeclarationList0 =
        repOpt(structDeclaration)

    def structDeclaration: MultiParser[StructDeclaration] =
        specifierQualifierList ~ structDeclaratorList <~ rep1(SEMI) ^^ {
            case q ~ l => StructDeclaration(q, l)
        }

    def specifierQualifierList: MultiParser[List[Opt[Specifier]]] =
        specList(typeQualifier | attributeDecl)

    def structDeclaratorList: MultiParser[List[Opt[StructDecl]]] =
        repSepOpt(structDeclarator, COMMA, "structDeclaratorList")
    //consumes trailing commas

    def structDeclarator: MultiParser[StructDecl] =
        ((COLON ~> constExpr ~ repOpt(attributeDecl) ^^ {
            case e ~ attr => StructInitializer(e, attr)
        })
            | (declarator ~ opt(COLON ~> constExpr) ~ repOpt(attributeDecl) ^^ {
            case d ~ e ~ attr => StructDeclarator(d, e, attr)
        }))

    def enumSpecifier: MultiParser[EnumSpecifier] =
        textToken("enum") ~>
            ((ID ~~ LCURLY ~! (enumList ~ RCURLY) ^^ {
                case id ~ _ ~ (l ~ _) => EnumSpecifier(Some(id), Some(l))
            })
                | (LCURLY ~ enumList ~ RCURLY ^^ {
                case _ ~ l ~ _ => EnumSpecifier(None, Some(l))
            })
                | (ID ^^ {
                case i => EnumSpecifier(Some(i), None)
            }))

    def enumList: MultiParser[List[Opt[Enumerator]]] =
        rep1SepOpt(enumerator, COMMA, "enumList")
    //consumes trailing comma <~ opt(COMMA)

    def enumerator: MultiParser[Enumerator] =
        ID ~ opt(ASSIGN ~> constExpr) ^^ {
            case id ~ expr => Enumerator(id, expr)
        }

    def initDeclList: MultiParser[List[Opt[InitDeclarator]]] =
        rep1SepOpt(initDecl, COMMA, "initDeclList")
    //consumes trailing comma <~ opt(COMMA)

    def initDecl: MultiParser[InitDeclarator] =
        repOpt(attributeDecl) ~ declarator ~ repOpt(attributeDecl) ~ opt((ASSIGN ~> initializer) | (COLON ~> expr)) ^^ {
            case attr1 ~ d ~ attr2 ~ Some(i: Initializer) => InitDeclaratorI(d, attr1 ++ attr2, Some(i));
            case attr1 ~ d ~ attr2 ~ Some(e: Expr) => InitDeclaratorE(d, attr1 ++ attr2, e);
            case attr1 ~ d ~ attr2 ~ None => InitDeclaratorI(d, attr1 ++ attr2, None);
        }

    def pointerGroup0: MultiParser[List[Opt[Pointer]]] =
        repOpt(STAR ~> opt(typeQualifierList) ^^ {
            case Some(l) => Pointer(l);
            case None => Pointer(List())
        })
    def pointerGroup1: MultiParser[List[Opt[Pointer]]] =
        rep1(STAR ~> opt(typeQualifierList) ^^ {
            case Some(l) => Pointer(l);
            case None => Pointer(List())
        })

    def typeQualifierList: MultiParser[List[Opt[Specifier]]] =
        repOpt(typeQualifier | attributeDecl)

    def idList0: MultiParser[List[Opt[Id]]] =
        repSepOpt(ID, COMMA, "idList0")
    //consumes trailing comma

    def initializer: MultiParser[Initializer] =
        opt(initializerDesignation) ~ (assignExpr | lcurlyInitializer) ^^ {
            case iel ~ expr => Initializer(iel, expr)
        }

    def declarator: MultiParser[Declarator] =
    //XXX: why opt(attributeDecl) rather than rep?
        (pointerGroup0 ~~ (ID | (LPAREN ~~> repOpt(attributeDecl) ~~ declarator <~ RPAREN)) ~
            repOpt(
                (LPAREN ~~> ((parameterDeclList ^^ {
                    DeclParameterDeclList(_)
                })
                    | (idList0 ^^ {
                    DeclIdentifierList(_)
                })) <~~ RPAREN)
                    // XXX: See 6.7.5.2.3 (C99 standard) for the specs which apply
                    // here. We should also accept '*'. Moreover, we
                    // should _not_ discard all such specifiers.
                    // 6.7.5.3.21 contains example declarators using restrict.
                    | (LBRACKET ~~> ((opt(staticSpecifier) ~ repOpt(typeQualifier) ~ opt(staticSpecifier)) ~> opt(constExpr)) <~ RBRACKET ^^ {
                    DeclArrayAccess(_)
                }))) ^^ {
            case pointers ~ (id: Id) ~ ext => AtomicNamedDeclarator(pointers, id, ext);
            case pointers ~ (attr ~ (decl: Declarator)) ~ ext =>
                NestedNamedDeclarator(pointers, decl, ext, attr.asInstanceOf[List[Opt[AttributeSpecifier]]])
        }

    def parameterDeclList: MultiParser[List[Opt[ParameterDeclaration]]] =
        rep1Sep(parameterDeclaration, COMMA | SEMI) ~ opt((COMMA | SEMI) ~> VARARGS ^^^ VarArgs()) ^^ {
            case l ~ Some(v) => l ++ List(o(v));
            case l ~ None => l
        }
    //    def parameterTypeList: MultiParser[List[Opt[ParameterDeclaration]]] =
    //        repSepOptIntern(false, parameterDeclaration, COMMA | SEMI).sep_~(opt(VARARGS)) ^^ {
    //            case l ~ Some(v) => l ++ List(o(VarArgs())); case l ~ None => l
    //        }
    //    //consumes trailing separators

    def parameterDeclaration: MultiParser[ParameterDeclaration] =
        declSpecifiers ~ opt(declarator | nonemptyAbstractDeclarator) ~ repOpt(attributeDecl) ^^ {
            case s ~ Some(d: Declarator) ~ attr => ParameterDeclarationD(s, d, attr)
            case s ~ Some(d: AbstractDeclarator) ~ attr => ParameterDeclarationAD(s, d, attr)
            case s ~ None ~ attr => PlainParameterDeclaration(s, attr)
        }

    def functionDef: MultiParser[FunctionDef] =
        optList(functionDeclSpecifiers) ~~
            declarator ~~
            repOpt(declaration) ~~ opt2List(VARARGS ^^ {
            x => VarArgs()
        }) ~~ repOpt(SEMI) ~~
            lookahead(LCURLY) ~! //prevents backtracking inside function bodies
            compoundStatement ^^ {
            case sp ~ declarator ~ param ~ vparam ~ _ ~ _ ~ stmt => FunctionDef(sp, declarator, param ++ vparam.map(o(_)), stmt)
        }

    def functionDeclSpecifiers: MultiParser[List[Opt[Specifier]]] =
        specList(functionStorageClassSpecifier | typeQualifier | attributeDecl)

    def compoundDeclaration: MultiParser[Conditional[CompoundDeclaration]] =
        declaration ^^ {
            DeclarationStatement(_)
        } | nestedFunctionDef | localLabelDeclaration |
            fail("expected compoundDeclaration") !

    def compoundStatement: MultiParser[CompoundStatement] =
        LCURLY ~> statementList <~ RCURLY ^^ {
            CompoundStatement(_)
        }

    //    private def compoundStatementCond: MultiParser[Conditional[CompoundStatement]] = compoundStatement ^^ {One(_)}

    def statementList: MultiParser[List[Opt[Statement]]] =
        repOpt(compoundDeclaration | statement, "statement") ^^ ConditionalLib.flatten

    def statement: MultiParser[Conditional[Statement]] = (SEMI ^^ {
        _ => EmptyStatement()
    } // Empty statements
        | (compoundStatement) // Group of statements
        //// Iteration statements:
        | (textToken("while") ~! LPAREN ~ expr ~ RPAREN ~ statement ^^ {
        case _ ~ _ ~ e ~ _ ~ s => WhileStatement(e, s)
    })
        | (textToken("do") ~! statement ~ textToken("while") ~ LPAREN ~ expr ~ RPAREN ~ SEMI ^^ {
        case _ ~ s ~ _ ~ _ ~ e ~ _ ~ _ => DoStatement(e, s)
    })
        | (textToken("for") ~! LPAREN ~ opt(expr) ~ SEMI ~ opt(expr) ~ SEMI ~ opt(expr) ~ RPAREN ~ statement ^^ {
        case _ ~ _ ~ e1 ~ _ ~ e2 ~ _ ~ e3 ~ _ ~ s => ForStatement(e1, e2, e3, s)
    })
        //// Jump statements:
        | (textToken("goto") ~!> expr <~ SEMI ^^ {
        GotoStatement(_)
    })
        | (textToken("continue") ~! SEMI ^^ {
        _ => ContinueStatement()
    })
        | (textToken("break") ~! SEMI ^^ {
        _ => BreakStatement()
    })
        | (textToken("return") ~!> opt(expr) <~ SEMI ^^ {
        ReturnStatement(_)
    })
        //// Labeled statements:
        | ((ID <~~ COLON) ~! opt(attributeDecl) ^^ {
        case i ~ a => LabelStatement(i, a)
    })
        // GNU allows range expressions in case statements
        | (textToken("case") ~! (rangeExpr | constExpr) ~ COLON ^^ {
        case _ ~ e ~ _ => CaseStatement(e)
    })
        | (textToken("default") ~! COLON ^^ {
        case _ => DefaultStatement()
    })
        //// Selection statements:
        | (textToken("if") ~! LPAREN ~ (expr !) ~ RPAREN ~ statement ~
        //ifelse handled as loop, because it is a common undisciplined pattern
        elifs ~
        opt(textToken("else") ~> statement) ^^ {
        case _ ~ _ ~ ex ~ _ ~ ts ~ elifs ~ es => IfStatement(ex, ts, elifs, es)
    })
        | (textToken("switch") ~! LPAREN ~ expr ~ RPAREN ~ statement ^^ {
        case _ ~ _ ~ e ~ _ ~ s => SwitchStatement(e, s)
    })
        | (expr <~ SEMI ^^ {
        ExprStatement(_)
    }) // Expressions
        | fail("statement expected")) !

    private def elifs: MultiParser[List[Opt[ElifStatement]]] =
        repOpt(
            textToken("else") ~~ textToken("if") ~! LPAREN ~!> (expr !) ~ (RPAREN ~> statement) ^^ {
                case e ~ s => ElifStatement(e, s)
            }
        )

    def expr: MultiParser[Expr] = assignExpr ~! repOpt(COMMA ~> assignExpr) ^^ {
        case e ~ l => if (l.isEmpty) e else ExprList(List(o(e)) ++ l)
    }

    def assignExpr: MultiParser[Expr] =
        conditionalExpr ~! opt(assignOperator ~ assignExpr) ^^ {
            case e ~ Some(o ~ e2) => AssignExpr(e, o.getText, e2);
            case e ~ None => e
        }

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

    def conditionalExpr: MultiParser[Expr] = logicalOrExpr ~! opt(QUESTION ~ opt(expr) ~ COLON ~ conditionalExpr) ^^ {
        case e ~ Some(q ~ e2 ~ c ~ e3) => ConditionalExpr(e, e2, e3);
        case e ~ None => e
    }
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

    def nAryExpr(innerExpr: MultiParser[Expr], operations: MultiParser[AbstractToken]) =
        innerExpr ~! repOpt(operations ~ innerExpr ^^ {
            case t ~ e => NArySubExpr(t.getText, e)
        }) ^^ {
            case e ~ l => if (l.isEmpty) e else NAryExpr(e, l)
        }

    def castExpr: MultiParser[Expr] =
        LPAREN ~~ typeName ~~ RPAREN ~~ castExpr ^^ {
            case _ ~ t ~ _ ~ e => CastExpr(t, e)
        } | unaryExpr

    //ChK: changed the grammar according to the gnu gcc parser (stricter than before)
    //avoiding repeatition, more parallel to named declarators
    //    def nonemptyAbstractDeclarator: MultiParser[AbstractDeclarator] =
    //        nonemptyAbstractDeclaratorA | nonemptyAbstractDeclaratorB
    //
    //    def nonemptyAbstractDeclaratorA: MultiParser[AbstractDeclarator] =
    //        pointerGroup1 ~
    //                repOpt(
    //                    ((LPAREN ~> (nonemptyAbstractDeclarator | (optList(parameterDeclList) ^^ {DeclParameterDeclList(_)}))
    //                            <~ (opt(COMMA) ~ RPAREN))
    //                            | (LBRACKET ~> opt(expr) <~ (opt(COMMA) ~ RBRACKET) ^^ {DeclArrayAccess(_)}))) ^^ {
    //            case pointers ~ (directDecls) => AbstractDeclarator(pointers, directDecls)
    //            case pointers ~ directDecls => AbstractDeclarator(pointers, directDecls)
    //        }
    //
    //    def nonemptyAbstractDeclaratorB: MultiParser[AbstractDeclarator] =
    //        rep1(
    //            ((LPAREN ~> (nonemptyAbstractDeclarator | (optList(parameterDeclList) ^^ {DeclParameterDeclList(_)}))
    //                    <~ (opt(COMMA) ~ RPAREN))
    //                    | (LBRACKET ~> opt(expr) <~ (opt(COMMA) ~ RBRACKET) ^^ {DeclArrayAccess(_)}))) ^^ {
    //            AbstractDeclarator(List(), _)
    //        }


    // either required pointer, then possible nesting and possible extensions
    // or no pointers, required nesting, possible extensions
    // or no pointers, no nesting, at least one extension
    def nonemptyAbstractDeclarator: MultiParser[AbstractDeclarator] =
        (pointerGroup1 ~ opt(LPAREN ~> repOpt(attributeDecl) ~ nonemptyAbstractDeclarator <~ RPAREN) ~
            repOpt(
                ((LPAREN ~> (optList(parameterDeclList) <~ (opt(COMMA) ~ RPAREN) ^^ {
                    DeclParameterDeclList(_)
                }))
                    | (LBRACKET ~> opt(expr) <~ (opt(COMMA) ~ RBRACKET) ^^ {
                    DeclArrayAccess(_)
                })))
            ^^ {
            case ptrs ~ Some(attr ~ nestedADecl) ~ ext => NestedAbstractDeclarator(ptrs, nestedADecl, ext, attr)
            case ptrs ~ None ~ ext => AtomicAbstractDeclarator(ptrs, ext)
        }) |
            ((LPAREN ~> repOpt(attributeDecl) ~ nonemptyAbstractDeclarator <~ RPAREN) ~
                repOpt(
                    ((LPAREN ~> (optList(parameterDeclList) <~ (opt(COMMA) ~ RPAREN) ^^ {
                        DeclParameterDeclList(_)
                    }))
                        | (LBRACKET ~> opt(expr) <~ (opt(COMMA) ~ RBRACKET) ^^ {
                        DeclArrayAccess(_)
                    })))
                ^^ {
                case attr ~ nestedADecl ~ ext => NestedAbstractDeclarator(List(), nestedADecl, ext, attr)
            }) |
            (rep1(
                ((LPAREN ~> (optList(parameterDeclList) <~ (opt(COMMA) ~ RPAREN) ^^ {
                    DeclParameterDeclList(_)
                }))
                    | (LBRACKET ~> opt(expr) <~ (opt(COMMA) ~ RBRACKET) ^^ {
                    DeclArrayAccess(_)
                })))
                ^^ {
                AtomicAbstractDeclarator(List(), _)
            })

    def unaryExpr: MultiParser[Expr] = (postfixExpr
        | ({
        (INC | DEC) ~! castExpr
    } ^^ {
        case p ~ e => UnaryExpr(p.getText, e)
    })
        | (STAR ~! castExpr ^^ {
        case _ ~ c => PointerDerefExpr(c)
    })
        | (BAND ~! castExpr ^^ {
        case _ ~ c => PointerCreationExpr(c)
    })
        | (unaryOperator ~! castExpr ^^ {
        case u ~ c => UnaryOpExpr(u.getText, c)
    })
        | (textToken("sizeof") ~!> (
        LPAREN ~> typeName <~ RPAREN ^^ {
            SizeOfExprT(_)
        } |
            unaryExpr ^^ {
                SizeOfExprU(_)
            }
        ))
        | (alignof ~!> (
        LPAREN ~> typeName <~ RPAREN ^^ {
            AlignOfExprT(_)
        } |
            unaryExpr ^^ {
                AlignOfExprU(_)
            }
        ))
        | gnuAsmExprWithGoto
        | gnuAsmExprWithoutGoto
        | fail("expected unaryExpr"))

    def unaryOperator = (PLUS | MINUS | BNOT | LNOT
        | LAND //for label dereference (&&label)
        | textToken("__real__")
        | textToken("__imag__"))

    def postfixExpr = primaryExpr ~ postfixSuffix ^^ {
        case p ~ s =>
            var result = p
            for (suffix <- s)
                result = PostfixExpr(result, suffix)
            result
    }

    def postfixSuffix: MultiParser[List[PostfixSuffix]] = repPlain[PostfixSuffix](
        (
            ((PTR ~ ID) | (DOT ~ ID)) ^^ {
                case e ~ id => PointerPostfixSuffix(e.getText, id)
            })
            | functionCall
            | (LBRACKET ~> expr <~ RBRACKET ^^ {
            ArrayAccess(_)
        })
            | ((INC | DEC) ^^ {
            c => SimplePostfixSuffix(c.getText)
        })
    )

    //see ISO 9899:1999 6.5.2
    //postfix-expression:
    //  ...
    //  ( type-name ) { initializer-list }
    //  ( type-name ) { initializer-list , }
    private def castAndInitializer = (LPAREN ~> typeName <~ RPAREN) ~ lcurlyInitializer  ^^ { case tn~init => CastExpr(tn, init) }

    //
    def functionCall: MultiParser[FunctionCall] =
        LPAREN ~> opt(argExprList) <~ RPAREN ^^ {
            case Some(l) => FunctionCall(l);
            case None => FunctionCall(ExprList(List()))
        }
    //XXX allows trailing comma after argument list

    def primaryExpr: MultiParser[Expr] = castAndInitializer |
        (textToken("__builtin_offsetof") ~ LPAREN ~! typeName ~ COMMA ~ offsetofMemberDesignator ~ RPAREN ^^ {
        case _ ~ _ ~ tn ~ _ ~ d ~ _ => BuiltinOffsetof(tn, d)
    }
        | (textToken("__builtin_types_compatible_p") ~ LPAREN ~ typeName ~ COMMA ~ typeName ~ RPAREN ^^ {
        case _ ~ _ ~ tn ~ _ ~ tn2 ~ _ => BuiltinTypesCompatible(tn, tn2)
    })
        | (textToken("__builtin_va_arg") ~ LPAREN ~ assignExpr ~ COMMA ~ typeName ~ RPAREN ^^ {
        case _ ~ _ ~ tn ~ _ ~ tn2 ~ _ => BuiltinVaArgs(tn, tn2)
    })
        | ID
        | numConst
        | stringConst
        | (LPAREN ~> ((compoundStatement ^^ {
        CompoundStatementExpr(_)
    }) | expr) <~ RPAREN)
        | fail("primary expression expected"))

    def typeName: MultiParser[TypeName] =
        specifierQualifierList ~ opt(nonemptyAbstractDeclarator) ^^ {
            case sl ~ d => TypeName(sl, d)
        }

    def ID: MultiParser[Id] = token("id", isIdentifier(_)) ^^ {
        t => Id(t.getText)
    }

    def isIdentifier(token: AbstractToken) = token.isKeywordOrIdentifier && !keywords.contains(token.getText)

    def stringConst: MultiParser[StringLit] =
        (rep1(token("string literal", _.isString))
            ^^ {
            (list: List[Opt[AbstractToken]]) => StringLit(list.map(o => Opt(o.condition, o.entry.getText)))
        })

    def numConst: MultiParser[Constant] =
        ((token("number", _.isInteger) ^^ {
            t => Constant(t.getText)
        })
            | (token("charConst", _.isCharacter) ^^ {
            t => Constant(t.getText)
        }))

    def argExprList: MultiParser[ExprList] =
        rep1SepOpt(assignExpr, COMMA, "argExprList") ^^ {
            ExprList(_)
        }
    //consumes trailing commas

    def offsetofMemberDesignator: MultiParser[List[Opt[OffsetofMemberDesignator]]] =
        (ID ^^ {
            id: Id => OffsetofMemberDesignatorID(id)
        }) ~ repOpt(offsetofMemberDesignatorExt) ^^ {
            case id ~ list => Opt(True, id) +: list
        }

    def offsetofMemberDesignatorExt: MultiParser[OffsetofMemberDesignator] =
        (DOT ~> ID ^^ {
            OffsetofMemberDesignatorID(_)
        }) |
            (LBRACKET ~> expr <~ RBRACKET ^^ {
                OffsetofMemberDesignatorExpr(_)
            })

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

    def pragma = textToken("_Pragma") ~! LPAREN ~> stringConst <~ RPAREN ^^ {
        Pragma(_)
    }

    //***  gnuc extensions ****************************************************

    def attributeDecl: MultiParser[AttributeSpecifier] =
        (((attributeKw ~ LPAREN ~ LPAREN ~ attributeList ~ RPAREN ~ RPAREN ^^ {
            case _ ~ _ ~ _ ~ al ~ _ ~ _ => GnuAttributeSpecifier(al)
        })
            | (asm ~ LPAREN ~> stringConst <~ RPAREN ^^ {
            AsmAttributeSpecifier(_)
        })))

    def attributeList: MultiParser[List[Opt[AttributeSequence]]] =
        attribute ~ repOpt(COMMA ~> attribute) ~ opt(COMMA) ^^ {
            case attr ~ attrList ~ _ =>
                o(attr) :: attrList
        }

    def attribute: MultiParser[AttributeSequence] =
        (repOpt((anyTokenExcept(List("(", ")", ",")) ^^ {
            t => AtomicAttribute(t.getText)
        })
            | (LPAREN ~> attributeList <~ RPAREN ^^ {
            t => CompoundAttribute(t)
        }))) ^^ {
            AttributeSequence(_)
        }

    /* alexvr: had to duplicate the gnuAsmExpr rule, because the old rule parsed Strings such as "__asm__ __volatile__("": : :"memory");" wrong.
     * The second colon was interpreted as the "special goto colon". Therefore the "memory" was interpreted as part of a strOptExprPair
     * and not as stringConst in the third part as it should. The only visible effect was an incorrect pretty print.
     */
    def gnuAsmExprWithGoto: MultiParser[GnuAsmExpr] =
        asm ~ opt(volatile) ~ textToken("goto") ~
            LPAREN ~ stringConst ~
            opt(
                COLON ~
                    COLON /*this colon only used with goto*/ ~> opt(strOptExprPair ~ repOpt(COMMA ~> strOptExprPair))
                    ~ opt(
                    COLON ~> opt(strOptExprPair ~ repOpt(COMMA ~> strOptExprPair)) ~
                        opt(COLON ~> rep1Sep(stringConst | ID, COMMA)))) ~
            RPAREN ^^ {
            case _ ~ v ~ gt ~ _ ~ e ~ stuff ~ _ => GnuAsmExpr(v.isDefined, true, e, stuff)
        }
    def gnuAsmExprWithoutGoto: MultiParser[GnuAsmExpr] =
        asm ~ opt(volatile) ~
            LPAREN ~ stringConst ~
            opt(
                COLON ~> opt(strOptExprPair ~ repOpt(COMMA ~> strOptExprPair))
                    ~ opt(
                    COLON ~> opt(strOptExprPair ~ repOpt(COMMA ~> strOptExprPair)) ~
                        opt(COLON ~> rep1Sep(stringConst | ID, COMMA))
                )
            ) ~
            RPAREN ^^ {
            case _ ~ v ~ _ ~ e ~ stuff ~ _ => GnuAsmExpr(v.isDefined, false, e, stuff)
        }

    //GCC requires the PARENs
    def strOptExprPair =
        opt(LBRACKET ~> ID <~ RBRACKET) ~ stringConst ~ opt(LPAREN ~> expr <~ RPAREN)

    // GCC allows empty initializer lists
    def lcurlyInitializer: MultiParser[Expr] =
        LCURLY ~ optList(initializerList /*initializerList already consumes tailing comma <~ opt(COMMA)*/) ~ RCURLY ^^ {
            case _ ~ inits ~ _ => LcurlyInitializer(inits)
        }

    def initializerList: MultiParser[List[Opt[Initializer]]] =
        rep1SepOpt(initializer, COMMA, "initializerList")
    //consumes trailing commas

    def rangeExpr: MultiParser[Expr] = //used in initializers only
        constExpr ~~ VARARGS ~! constExpr ^^ {
            case a ~ _ ~ b => RangeExpr(a, b)
        }

    def nestedFunctionDef: MultiParser[NestedFunctionDef] =
        opt(textToken("auto")) ~~ //only for nested functions
            optList(functionDeclSpecifiers) ~~
            declarator ~~
            repOpt(declaration) ~~
            compoundStatement ^^ {
            case auto ~ sp ~ declarator ~ param ~ stmt => NestedFunctionDef(auto.isDefined, sp, declarator, param, stmt)
        }

    //GNU note:  any __label__ declarations must come before regular declarations.
    def localLabelDeclaration: MultiParser[LocalLabelDeclaration] =
        textToken("__label__") ~> rep1SepOpt(ID, COMMA, "rep1SepOpt") <~ (/*rep1SepOpt already consumes trailing comma opt(COMMA) ~*/ rep1(SEMI)) ^^ {
            LocalLabelDeclaration(_)
        }


    // GCC allows more specific initializers
    def initializerDesignation: MultiParser[InitializerElementLabel] =
        (rep1(designator) <~~ ASSIGN ^^ {
            InitializerAssigment(_)
        }) |
            arrayDesignator |
            (ID <~~ COLON ^^ {
                InitializerDesignatorC(_)
            })

    def designator: MultiParser[InitializerElementLabel] =
        arrayDesignator |
            (DOT ~~> ID ^^ {
                InitializerDesignatorD(_)
            })

    def arrayDesignator: MultiParser[InitializerArrayDesignator] =
        LBRACKET ~~ (rangeExpr | constExpr) ~~ RBRACKET ^^ {
            case _ ~ e ~ _ => InitializerArrayDesignator(e)
        }

    def attributeKw = textToken("__attribute__") |
        textToken("__attribute")
    //XXX: PG: not specified anywhere by GCC docs, but used in Linux.

    def typeof = textToken("typeof") | textToken("__typeof") | textToken("__typeof__")

    def volatile = (specifier("volatile") | specifier("__volatile") | specifier("__volatile__")) ^^^ VolatileSpecifier()

    def asm = textToken("asm") | textToken("__asm") | textToken("__asm__")

    def const = (specifier("const") | specifier("__const") | specifier("__const__")) ^^^ ConstSpecifier()

    def restrict = (specifier("restrict") | specifier("__restrict") | specifier("__restrict__")) ^^^ RestrictSpecifier()

    def thread = specifier("__thread") ^^^ ThreadSpecifier()

    def signed = (textToken("signed") | textToken("__signed") | textToken("__signed__")) ^^^ SignedSpecifier()

    def inline = (specifier("inline") | specifier("__inline") | specifier("__inline__")) ^^^ InlineSpecifier()

    def alignof = textToken("__alignof__") | textToken("__alignof")

    def ignoreAttributes(s: Specifier): Boolean = s match {
        case x: AttributeSpecifier => false
        case _ => true
    }
    def specList(otherSpecifiers: MultiParser[Specifier]): MultiParser[List[Opt[Specifier]]] =
        alwaysNonEmpty(repOpt(otherSpecifiers) ~~ opt(typedefName) ~~ repOpt(otherSpecifiers | typeSpecifier) ^^ {
            case list1 ~ Some(typedefn) ~ list2 => list1 ++ List(Opt(True, typedefn)) ++ list2
            case list1 ~ None ~ list2 => list1 ++ list2
        }, ignoreAttributes)

    //XXX: CK: only for debugging purposes, not part of the C grammar
    def expectType = textToken("__expectType") ~ LBRACKET ~ COLON ~!> typedefName <~ COLON <~ RBRACKET ^^ {
        x => EmptyExternalDef()
    }
    def expectNotType = textToken("__expectNotType") ~ LBRACKET ~ COLON ~!> notypedefName <~ COLON <~ RBRACKET ^^ {
        x => EmptyExternalDef()
    }

    // *** helper functions
    def textToken(t: String): MultiParser[Elem] =
        token(t, _.getText == t)

    def textToken(t: Char) =
        token(t.toString, _.getText == t.toString)

    def anyTokenExcept(exceptions: List[String]): MultiParser[Elem] =
        token("any except " + exceptions, (t: Elem) => !exceptions.contains(t.getText))

    private def o[T](x: T) = Opt(True, x)

    override def joinContext(a: CTypeContext, b: CTypeContext): CTypeContext = a join b

}
