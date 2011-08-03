package de.fosd.typechef.typesystem

import de.fosd.typechef.parser.c.ASTNavigation

/**
 * bundle for easier import
 */
trait CTypeAnalysis extends ASTNavigation with CTypes with CDeclTyping with CExprTyping with CTypeEnv with CFunctionRedefinition with CStmtTyping