header
{
	using System.IO;
	using System.Text;
	using System.Collections;
}

options
{
	language 	= "CSharp";	
	namespace	= "Kunle.CSharpParser";
}

/*
[The "BSD licence"]
Copyright (c) 2002-2005 Kunle Odutola
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:
1. Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.


/// <summary>
/// An AST Printer (or un-parser) that prints the source code that a tree represents. 
/// </summary>
///
/// <remarks>
/// <para>
///	The default behaviour of this PrettyPrinter is to print out the AST and generate
/// source code that is as close to the original as is possible.
/// </para>
/// <para>
///	This behaviour can be overridden by supplying an <see cref="ICodeStyleScheme"/>
/// object that contains the settings for a custom formatting code style.
/// </para>
/// <para>
/// The TreeParser defined below is designed for the AST created by CSharpParser.
/// See the file "CSharpParser.g" for the details of that Parser.
/// </para>
///
/// <para>
/// History
/// </para>
///
/// <para>
/// 05-Jun-2003 kunle	  Created file.
/// </para>
///
/// </remarks>


*/
class CSharpPrettyPrinter extends TreeParser;

options
{
	importVocab						= CSharp;
	buildAST						= false;
	ASTLabelType					= "ASTNode";
	defaultErrorHandler				= false;
}

//=============================================================================
// Start of CODE
//=============================================================================

{
	//---------------------------------------------------------------------
	// PRIVATE DATA MEMBERS
	//---------------------------------------------------------------------
	
	private bool					nothingPrintedYet_;
	private string					newLine_ = Environment.NewLine;
	
	public void Print(TextWriter w, ASTNode astRoot, string scheme)
	{
		if (astRoot.Type == COMPILATION_UNIT)
		{
			compilationUnit(astRoot, w);
		}
		else
		{
			throw new NotSupportedException("astRoot must be a COMPILATION_UNIT node.");
		}
	}

	/// <summary>
	/// Prints the text of the specified AST node and any hidden tokens that exist AFTER the node.
	/// </summary>
	/// <param name="w">The output destination.</param>
	/// <param name="node">The AST node to print.</param>
    private void Print(TextWriter w, ASTNode node)
    {
    	Print(w, node, null, null);
    }

	/// <summary>
	/// Prints the text of the specified AST node and any hidden tokens that exist 
	/// AFTER the node.
	/// </summary>
	/// <param name="w">The output destination.</param>
	/// <param name="node">The AST node to print.</param>
    private void Print(TextWriter w, ASTNode node, string text, string textAfter)
    {
    	if (text == null)
    	{
    		w.Write(node.getText());
    	}
    	else
    	{
    		w.Write(text);
    	}
    	CustomHiddenStreamToken tok = (CustomHiddenStreamToken) node.getHiddenAfter();
    	if ((tok == null) && (textAfter != null))
    		w.Write(textAfter);
    	else
    	{
			while ( tok != null )
			{
		    	w.Write(tok.getText());
		     	tok = (CustomHiddenStreamToken) tok.getHiddenAfter();
		    }
	    }
    }

	/// <summary>
	/// Prints the text of the specified AST node and any hidden tokens that exist 
	/// AFTER the node.
	/// </summary>
	/// <param name="w">The output destination.</param>
	/// <param name="node">The AST node to print.</param>
    private void PrintWithBeforeCheck(TextWriter w, ASTNode node)
    {
    	PrintWithBeforeCheck(w, node, null, null);
    }

	/// <summary>
	/// Prints the text of the specified AST node and any hidden tokens that exist 
	/// AFTER the node.
	/// </summary>
	/// <param name="w">The output destination.</param>
	/// <param name="node">The AST node to print.</param>
    private void PrintWithBeforeCheck(TextWriter w, ASTNode node, string text, string textAfter)
    {
    	if (nothingPrintedYet_)
    	{
    		PrintHiddenBeforeTokens(w, node);
    		nothingPrintedYet_ = false;
    	}
    	Print(w, node, text, textAfter);
    }

	/// <summary>
	/// Prints any hidden tokens that may exist AFTER the specified AST node.
	/// </summary>
	/// <param name="w">The output destination.</param>
	/// <param name="node">The node of the AST to which the hidden tokens are linked.</param>
    private void PrintHiddenAfterTokens(TextWriter w, ASTNode node)
    {
    	CustomHiddenStreamToken tok = (CustomHiddenStreamToken) node.getHiddenAfter();
		while ( tok != null )
		{
	    	w.Write(tok.getText());
	     	tok = (CustomHiddenStreamToken) tok.getHiddenAfter();
	    }
    }

	/// <summary>
	/// Prints any hidden tokens that may exist BEFORE the specified AST node.
	/// </summary>
	/// <param name="w">The output destination.</param>
	/// <param name="node">The node of the AST to which the hidden tokens are linked.</param>
    private void PrintHiddenBeforeTokens(TextWriter w, ASTNode node)
    {
    	CustomHiddenStreamToken firstTok = null;
    	CustomHiddenStreamToken tok      = (CustomHiddenStreamToken) node.getHiddenBefore();

    	while ( tok != null )
    	{
    		firstTok = tok;
    		tok      = (CustomHiddenStreamToken) tok.getHiddenBefore();
    	}
    	
    	tok = firstTok;
		while ( tok != null )
		{
	    	w.Write(tok.getText());
	     	tok = (CustomHiddenStreamToken) tok.getHiddenAfter();
	    }
    }

	private bool NotExcluded(CodeMaskEnums codeMask, CodeMaskEnums construct)
	{
		return ((codeMask & construct) != 0 );
	}

}

//=============================================================================
// Start of RULES
//=============================================================================

compilationUnit [TextWriter w]
{
	nothingPrintedYet_ = true;
}
	:	#(	COMPILATION_UNIT
			justPreprocessorDirectives[w]
			usingDirectives[w]
			globalAttributes[w]
			namespaceMemberDeclarations[w]
		)
	;
	
usingDirectives [TextWriter w]
	:	#(	USING_DIRECTIVES 
			(	preprocessorDirective[w, CodeMaskEnums.UsingDirectives]
			|	usingDirective[w]
			)*
		)
	;
	
usingDirective [TextWriter w]
	:	#(	ndir:USING_NAMESPACE_DIRECTIVE 			{ PrintWithBeforeCheck(w, #ndir, null, " "); } 
			qualifiedIdentifier[w] 					{ w.Write(";" + newLine_); }
		)
	|	#(	adir:USING_ALIAS_DIRECTIVE     			{ PrintWithBeforeCheck(w, #adir, null, " "); } 
			identifier[w] 							{ w.Write(" = "); } 
			qualifiedIdentifier[w] 					{ w.Write(";" + newLine_); }
		)
	;

namespaceMemberDeclarations [TextWriter w]
	:	(	namespaceMemberDeclaration[w]
		|	preprocessorDirective[w, CodeMaskEnums.NamespaceMemberDeclarations]
		)*
	;
	
namespaceMemberDeclaration [TextWriter w]
	:	namespaceDeclaration[w]
	|	typeDeclaration[w]
	;
	
typeDeclaration [TextWriter w]
	:	(	classDeclaration[w]
		|	structDeclaration[w]
		|	interfaceDeclaration[w]
		|	enumDeclaration[w]
		|	delegateDeclaration[w]
		)
	;

namespaceDeclaration [TextWriter w]
	:	#( 	ns:NAMESPACE { PrintWithBeforeCheck(w, #ns, null, " "); }
			qualifiedIdentifier[w] namespaceBody[w] 
		)
	;
	
namespaceBody [TextWriter w]
	:	#(	ns:NAMESPACE_BODY { Print(w, #ns); } usingDirectives[w] namespaceMemberDeclarations[w] 
			cy:CLOSE_CURLY    { Print(w, #cy); }
		)
	;
	
modifiers [TextWriter w]
	:	#( MODIFIERS ( modifier[w] )* )
	;

modifier [TextWriter w]
	:	(	ab:ABSTRACT		{ PrintWithBeforeCheck(w, #ab, null, " "); }
		|	nw:NEW			{ PrintWithBeforeCheck(w, #nw, null, " "); }
		|	ov:OVERRIDE		{ PrintWithBeforeCheck(w, #ov, null, " "); }
		|	pu:PUBLIC		{ PrintWithBeforeCheck(w, #pu, null, " "); }
		|	pr:PROTECTED	{ PrintWithBeforeCheck(w, #pr, null, " "); }
		|	ir:INTERNAL		{ PrintWithBeforeCheck(w, #ir, null, " "); }
		|	pv:PRIVATE		{ PrintWithBeforeCheck(w, #pv, null, " "); }
		|	sl:SEALED		{ PrintWithBeforeCheck(w, #sl, null, " "); }
		|	st:STATIC		{ PrintWithBeforeCheck(w, #st, null, " "); }
		|	vi:VIRTUAL		{ PrintWithBeforeCheck(w, #vi, null, " "); }
		|	ex:EXTERN		{ PrintWithBeforeCheck(w, #ex, null, " "); }
		|	ro:READONLY		{ PrintWithBeforeCheck(w, #ro, null, " "); }
		|	un:UNSAFE		{ PrintWithBeforeCheck(w, #un, null, " "); }
		|	vo:VOLATILE		{ PrintWithBeforeCheck(w, #vo, null, " "); }
		)
	;


typeName [TextWriter w]
	:	predefinedType[w]
	|	qualifiedIdentifier[w]
	;
	
classTypeName [TextWriter w]
	:	qualifiedIdentifier[w]
	|	obj:OBJECT			{ Print(w, #obj); }
	|	str:STRING			{ Print(w, #str); }
	;
	
identifier [TextWriter w]
	:	id:IDENTIFIER		{ Print(w, #id); }
	;
	
qualifiedIdentifier [TextWriter w]
	:	(	identifier[w]
		|	#( dt:DOT identifier[w]	{ Print(w, #dt); } qualifiedIdentifier[w] )
		)
	;

	
//
// A.2.2 Types
//

type [TextWriter w]
	:	#(	TYPE 
			( 	qualifiedIdentifier[w]
			| 	predefinedType[w]
			|	v:VOID { Print(w, #v); }
			)
			pointerSpecifier[w]
			rankSpecifiers[w]
			{ w.Write(" "); }
		)
	;
	
pointerSpecifier [TextWriter w]
	:	#(	STARS 
			(	s:STAR { Print(w, #s); }
			)* 
		)
	;
	
classType [TextWriter w]
	:	qualifiedIdentifier[w]
	|	obj:OBJECT			{ Print(w, #obj); }
	|	str:STRING			{ Print(w, #str); }
	;
	
interfaceType [TextWriter w]
	:	qualifiedIdentifier[w]
	;

delegateType [TextWriter w]
	:	qualifiedIdentifier[w]		// typeName
	;

/*	
pointerType
	:	unmanagedType STAR
	|	VOID STAR
	;
*/	
unmanagedType [TextWriter w]
	:	qualifiedIdentifier[w]		// typeName
	;
	
//
// A.2.3 Variables
//

variableReference [TextWriter w]
	:	expression[w]
	;

//
// A.2.4 Expressions
//
	
argumentList [TextWriter w]
	:	#(	ARG_LIST argument[w] ( { w.Write(", "); } argument[w] )*
		)
	;
	
argument [TextWriter w]
	:	expression[w]
	|	#( r:REF { Print(w, #r, null, " "); } variableReference[w] )
	|	#( o:OUT { Print(w, #o, null, " "); } variableReference[w] )
	;

constantExpression [TextWriter w]
	:	expression[w]
	;
	
booleanExpression [TextWriter w]
	:	expression[w]
	;
	
expressionList [TextWriter w]
	:	#(	EXPR_LIST expression[w] ( { w.Write(", "); } expression[w] )*
		)
	;

expression [TextWriter w]
	:	#( EXPR expr[w] )
	;
	
expr [TextWriter w]
		// assignmentExpression
		//
	:	#( asg:ASSIGN         expr[w] { Print(w, #asg); } expr[w] )
	|	#( plg:PLUS_ASSIGN    expr[w] { Print(w, #plg); } expr[w] )
	|	#( mig:MINUS_ASSIGN   expr[w] { Print(w, #mig); } expr[w] )
	|	#( stg:STAR_ASSIGN    expr[w] { Print(w, #stg); } expr[w] )
	|	#( dig:DIV_ASSIGN     expr[w] { Print(w, #dig); } expr[w] )
	|	#( mog:MOD_ASSIGN     expr[w] { Print(w, #mog); } expr[w] )
	|	#( ang:BIN_AND_ASSIGN expr[w] { Print(w, #ang); } expr[w] )
	|	#( org:BIN_OR_ASSIGN  expr[w] { Print(w, #org); } expr[w] )
	|	#( xog:BIN_XOR_ASSIGN expr[w] { Print(w, #xog); } expr[w] )
	|	#( slg:SHIFTL_ASSIGN  expr[w] { Print(w, #slg); } expr[w] )
	|	#( srg:SHIFTR_ASSIGN  expr[w] { Print(w, #srg); } expr[w] )
		// conditionalExpression
		//
	|	#( q:QUESTION       expr[w] { Print(w, #q, null, " ? "); } expr[w] { w.Write(" : "); } expr[w] )
	
		// conditional-XXX-Expressions
		//
	|	#( lor:LOG_OR         expr[w] { Print(w, #lor, null, " "); } expr[w] )
	|	#( lan:LOG_AND        expr[w] { Print(w, #lan, null, " "); } expr[w] )
		
		// bitwise-XXX-Expressions
		//
	|	#( bor:BIN_OR         expr[w] { Print(w, #bor, null, " "); } expr[w] )
	|	#( box:BIN_XOR        expr[w] { Print(w, #box, null, " "); } expr[w] )
	|	#( boa:BIN_AND        expr[w] { Print(w, #boa, null, " "); } expr[w] )

		// equalityExpression
		//
	|	#( eql:EQUAL          expr[w] { Print(w, #eql, null, " "); } expr[w] )
	|	#( neq:NOT_EQUAL      expr[w] { Print(w, #neq, null, " "); } expr[w] )

		// relationalExpression
		//
	|	#( r1:LTHAN          expr[w] { Print(w, #r1, null, " "); } expr[w] )
	|	#( r2:GTHAN          expr[w] { Print(w, #r2, null, " "); } expr[w] )
	|	#( r3:LTE            expr[w] { Print(w, #r3, null, " "); } expr[w] )
	|	#( r4:GTE            expr[w] { Print(w, #r4, null, " "); } expr[w] )
	|	#( r5:IS             expr[w] { w.Write(" "); Print(w, #r5, null, " "); } type[w] )
	|	#( r6:AS             expr[w] { w.Write(" "); Print(w, #r6, null, " "); } type[w] )
	
		// shiftExpression
		//
	|	#( sl:SHIFTL         expr[w] { Print(w, #sl, null, " "); } expr[w] )
	|	#( sr:SHIFTR         expr[w] { Print(w, #sr, null, " "); } expr[w] )

		// additiveExpression
		//
	|	#( p:PLUS           expr[w] { Print(w, #p); } expr[w] )
	|	#( m:MINUS          expr[w] { Print(w, #m); } expr[w] )
		// multiplicativeExpression
		//
	|	#( m1:STAR           expr[w] { Print(w, #m1); } expr[w] )
	|	#( m2:DIV            expr[w] { Print(w, #m2); } expr[w] )
	|	#( m3:MOD            expr[w] { Print(w, #m3); } expr[w] )
	|	unaryExpression[w]
	;	

unaryExpression [TextWriter w]
	:	#( c:CAST_EXPR 			{ Print(w, #c); } type[w] { w.Write(") "); } expr[w] )
	|	#( i:INC              	{ Print(w, #i); } expr[w] )
	|	#( d:DEC              	{ Print(w, #d); } expr[w] )
	|	#( up:UNARY_PLUS       	{ Print(w, #up); } expr[w] )
	|	#( um:UNARY_MINUS      	{ Print(w, #um); } expr[w] )
	|	#( l:LOG_NOT            { Print(w, #l); } expr[w] )
	|	#( b:BIN_NOT			{ Print(w, #b); } expr[w] )
	|	#( p:PTR_INDIRECTION_EXPR { Print(w, #p); } expr[w] )
	|	#( a:ADDRESS_OF_EXPR      { Print(w, #a); } expr[w] )
	|	primaryExpression[w]
	;
	
primaryExpression [TextWriter w]
	:	#(	// invocationExpression ::= primaryExpression OPEN_PAREN ( argumentList )? CLOSE_PAREN 
			i:INVOCATION_EXPR			primaryExpression[w] { Print(w, #i); } 
										(argumentList[w])?   { w.Write(")"); } 
		)
	|	#(	// elementAccess		::= primaryNoArrayCreationExpression OPEN_BRACK expressionList CLOSE_BRACK
			e:ELEMENT_ACCESS_EXPR		primaryExpression[w] { Print(w, #e); } 
										expressionList[w]    { w.Write("]"); }
		)
	|	#(	// pointerElementAccess ::= primaryNoArrayCreationExpression OPEN_BRACK expression     CLOSE_BRACK
			p:PTR_ELEMENT_ACCESS_EXPR	primaryExpression[w] { Print(w, #p); } 
										expressionList[w]    { w.Write("]"); }
		)
	|	#(	// memberAccess		    ::= primaryExpression DOT identifier
			m:MEMBER_ACCESS_EXPR      									
			(	type[w]
			|	primaryExpression[w]
			)
			{ Print(w, #m); } identifier[w]
		)
	|	// pointerMemberAccess
		#( d:DEREF                   primaryExpression[w] { Print(w, #d); } identifier[w] )
	|	// postIncrementExpression
		#( pi:POST_INC_EXPR          primaryExpression[w] { Print(w, #pi); } )
	|	// postDecrementExpression
		#( pd:POST_DEC_EXPR          primaryExpression[w] { Print(w, #pd); } )
	|	basicPrimaryExpression[w]
	;

basicPrimaryExpression [TextWriter w]
	:	literal[w]
	|	identifier[w]										// simpleName
	|	// parenthesizedExpression
		//
		#( p:PAREN_EXPR { Print(w, #p); } expr[w] { w.Write(")"); } )
	|	tx:THIS 		{ Print(w, #tx); }
	|	#(	bx:BASE		{ Print(w, #bx); }
			(	identifier[w]
			|	expressionList[w]
			)
		)
	|	newExpression[w]
	|	// typeofExpression
		//
		#( t:TYPEOF 	{ Print(w, #t); w.Write("("); } type[w]          { w.Write(")"); } )
	|	#( s:SIZEOF    	{ Print(w, #s); w.Write("("); } unmanagedType[w] { w.Write(")"); } )
	|	#( c:CHECKED   	{ Print(w, #c); w.Write("("); } expression[w]    { w.Write(")"); } )
	|	#( u:UNCHECKED 	{ Print(w, #u); w.Write("("); } expression[w]    { w.Write(")"); } )
	;

newExpression [TextWriter w]
		// objectCreationExpression	  
		//
	:	#( ob:OBJ_CREATE_EXPR { Print(w, #ob, null, " "); } type[w] { w.Write("("); } (argumentList[w])? { w.Write(")"); } )
		// delegateCreationExpression	  
		//
	|	#( dl:DLG_CREATE_EXPR { Print(w, #dl, null, " "); } type[w] { w.Write("("); } argumentList[w] { w.Write(")"); } )
		// arrayCreationExpression
		//
	|	#( 	ar:ARRAY_CREATE_EXPR { Print(w, #ar, null, " "); }
			type[w]										// nonArrayType ( rankSpecifiers )?
			(	{ w.Write("[] "); } arrayInitializer[w]
			|	{ w.Write("[");   } expressionList[w] { w.Write("]"); } 
				rankSpecifiers[w] ( arrayInitializer[w] )? 
			)
		)
	;

literal [TextWriter w]
	:	tr:TRUE				{ Print(w, #tr); }
	|	fa:FALSE			{ Print(w, #fa); }
	|	il:INT_LITERAL		{ Print(w, #il); }
	|	ui:UINT_LITERAL		{ Print(w, #ui); }
	|	ll:LONG_LITERAL		{ Print(w, #ll); }
	|	ul:ULONG_LITERAL	{ Print(w, #ul); }
	|	dc:DECIMAL_LITERAL	{ Print(w, #dc); }
	|	fl:FLOAT_LITERAL	{ Print(w, #fl); }
	|	dl:DOUBLE_LITERAL	{ Print(w, #dl); }
	|	cl:CHAR_LITERAL		{ Print(w, #cl); }
	|	sl:STRING_LITERAL	{ Print(w, #sl); }
	|	nl:NULL				{ Print(w, #nl); }
	;

predefinedType [TextWriter w]
	:	bl:BOOL				{ Print(w, #bl); } 
	|	by:BYTE				{ Print(w, #by); }
	|	ch:CHAR				{ Print(w, #ch); }
	|	dc:DECIMAL			{ Print(w, #dc); }
	|	db:DOUBLE			{ Print(w, #db); }
	|	fl:FLOAT			{ Print(w, #fl); }
	|	it:INT				{ Print(w, #it); }
	|	lg:LONG				{ Print(w, #lg); }
	|	ob:OBJECT			{ Print(w, #ob); }
	|	sb:SBYTE			{ Print(w, #sb); }
	|	sh:SHORT			{ Print(w, #sh); }
	|	st:STRING			{ Print(w, #st); }
	|	ui:UINT				{ Print(w, #ui); }
	|	ul:ULONG			{ Print(w, #ul); }
	|	us:USHORT			{ Print(w, #us); }
	;
	

//
// A.2.5 Statements
//

statement [TextWriter w]
	:	#(	l:LABEL_STMT identifier[w] { Print(w, #l, null, " "); } statement[w]
		)
	|	localVariableDeclaration[w] { w.Write(";" + newLine_); }
	|	localConstantDeclaration[w] { w.Write(";" + newLine_); }
	|	embeddedStatement[w]
	|	preprocessorDirective[w, CodeMaskEnums.Statements]
	;
	
embeddedStatement [TextWriter w]
	:	block[w]
	|	emp:EMPTY_STMT	{ Print(w, #emp); }
	|	#( sxp:EXPR_STMT statementExpression[w] { Print(w, #sxp); } )
	|	#(	ifStmt:IF { Print(w, #ifStmt); }
			{ w.Write("("); } expression[w] { w.Write(") " + newLine_); } embeddedStatement[w]
			(	#(	elseStmt:ELSE { Print(w, #elseStmt, null, " "); }
					embeddedStatement[w]
				)
			)? 
		)
	|	#(	switchStmt:SWITCH { Print(w, #switchStmt); }
			{ w.Write("("); } expression[w] { w.Write(") " + newLine_); }
			#(	oc1:OPEN_CURLY { Print(w, #oc1); }
				( switchSection[w] )* 
				cc1:CLOSE_CURLY { Print(w, #cc1); }
			) 
		)
	|	#( 	forStmt:FOR { Print(w, #forStmt); w.Write("("); }
			#( FOR_INIT ( ( localVariableDeclaration[w] | ( statementExpression[w] )+ ) )? )
			{ w.Write("; "); }
			#( FOR_COND ( booleanExpression[w] )? )
			{ w.Write("; "); }
			#( FOR_ITER ( ( statementExpression[w] )+  )? )
			{ w.Write(") " + newLine_); }
			embeddedStatement[w]
		)
	|	#(	whileStmt:WHILE { Print(w, #whileStmt); w.Write("("); }
			booleanExpression[w] { w.Write(") " + newLine_); } embeddedStatement[w]
		)
	|	#(	doStmt:DO { Print(w, #doStmt, null, " "); }
			embeddedStatement[w] { w.Write(" while ("); } booleanExpression[w] { w.Write(");" + newLine_); }
		)
	|	#(	foreachStmt:FOREACH { Print(w, #foreachStmt); w.Write("("); }
			localVariableDeclaration[w] { w.Write(" in "); } expression[w] 
			{ w.Write(") " + newLine_); }
			embeddedStatement[w]
		)
	|	brk:BREAK		{ Print(w, #brk); w.Write(";" + newLine_); }
	|	ctn:CONTINUE	{ Print(w, #ctn); w.Write(";" + newLine_); }
	|	#(	gto:GOTO 	{ Print(w, #gto, null, " "); }
			(	identifier[w] 
			|	cse:CASE 	{ Print(w, #cse, null, " "); } constantExpression[w]
			|	dfl:DEFAULT	{ Print(w, #dfl); }
			)
			{ w. Write(";" + newLine_); }
		)
	|	#( rtn:RETURN { Print(w, #rtn, null, " "); } ( expression[w] )? { w.Write(";" + newLine_); } )
	|	#( trw:THROW  { Print(w, #trw, null, " "); } ( expression[w] )? { w.Write(";" + newLine_); } )
	|	tryStatement[w]
	|	#(	checkedStmt:CHECKED 	{ Print(w, #checkedStmt, null, " "); }   block[w]
		)
	|	#(	uncheckedStmt:UNCHECKED	{ Print(w, #uncheckedStmt, null, " "); } block[w]
		)
	|	#(	lockStmt:LOCK { Print(w, #lockStmt); }
			{ w.Write("("); } expression[w] { w.Write(") " + newLine_); } embeddedStatement[w]
		)
	|	#(	usingStmt:USING { Print(w, #usingStmt); }
			{ w.Write("("); } resourceAcquisition[w] { w.Write(") " + newLine_); } embeddedStatement[w]
		)
	|	#(	unsafeStmt:UNSAFE { Print(w, #unsafeStmt, null, " "); }
			block[w]
		)
	|	// fixedStatement
		#(	fixedStmt:FIXED { Print(w, #fixedStmt); }
			{ w.Write("("); } type[w] 
			fixedPointerDeclarator[w] ( { w.Write(", "); } fixedPointerDeclarator[w] )*
			{ w.Write(") " + newLine_); }
			embeddedStatement[w]
		)
	;
	
body[TextWriter w]
	:	block[w]
	|	empty:EMPTY_STMT	{ Print(w, #empty); }
	;

block [TextWriter w]
	:	#(	blk:BLOCK { Print(w, #blk, null, " "); }
			( statement[w] )*
			cly:CLOSE_CURLY { Print(w, #cly); }
		)
	;
	
statementList[TextWriter w]
	:	#( STMT_LIST ( statement[w] )+ )
	;
	
localVariableDeclaration [TextWriter w]
	:	#(	LOCVAR_DECLS type[w] localVariableDeclarator[w] 
			(	{ w.Write(", "); } 
				localVariableDeclarator[w] 
			)*
		)
	;
	
localVariableDeclarator [TextWriter w]
	:	#(	VAR_DECLARATOR identifier [w]
			(	{ w.Write(" = "); }
				#(	LOCVAR_INIT
					(	expression[w]
					|	arrayInitializer[w]
					)
				) 
			)? 
		)
	;
	
localConstantDeclaration [TextWriter w]
	:	#(	c:LOCAL_CONST { Print(w, #c, null, " "); } type[w] constantDeclarator[w]
			(	{ w.Write(", "); }
				constantDeclarator[w] 
			)*
		)
	;
	
constantDeclarator [TextWriter w]
	:	#(	c:CONST_DECLARATOR identifier[w] { Print(w, #c, null, " "); } constantExpression[w]
		)
	;
	
statementExpression [TextWriter w]
	:	expr[w]
	;
	
switchSection[TextWriter w]
	:	#( SWITCH_SECTION switchLabels[w] statementList[w] )
	;
	
switchLabels[TextWriter w]
	:	#(	SWITCH_LABELS 
			(	(	#( cse:CASE { Print(w, #cse, null, " "); } expression[w] )
				|	dfl:DEFAULT	{ Print(w, #dfl, null, " "); }
				)
				{ w.Write(": "); }
			)+ 
		)
	;
	
tryStatement [TextWriter w]
	:	#(	t:TRY { Print(w, #t, null, " "); }
			block[w]
			(	finallyClause[w]
			|	catchClauses[w] ( finallyClause[w] )?
			)
		)
	;
	
catchClauses [TextWriter w]
	:	(	
			{
				StringBuilder sb = new StringBuilder();
				StringWriter  sw = new StringWriter(sb);
			}
			#(	c:CATCH { Print(w, #c, null, " "); }
				block[sw]
				(	{ w.Write("("); }
					(	type[w]
					|	localVariableDeclaration[w]
					)
					{ w.Write(") " + newLine_); }
				)? 
				{
					w.Write(sb.ToString());
					sw.Close();
				}
			)
		)+
	;
	
finallyClause [TextWriter w]
	:	#(	f:FINALLY { Print(w, #f, null, " "); }
			block[w]
		)
	;
	
resourceAcquisition[TextWriter w]
	:	localVariableDeclaration[w]
	|	expression[w]
	;
	
//	
// A.2.6 Classes
//

classDeclaration [TextWriter w]
	:	#( 	cl:CLASS attributes[w] modifiers[w] { PrintWithBeforeCheck(w, #cl, null, " "); } identifier[w]
			#(	CLASS_BASE
				{
					bool needToPrintColon = true;
					bool needToPrintComma = false;
				}
				(	{
						if ( needToPrintColon )
						{
							w.Write(" : ");
							needToPrintColon = false;
						}
						if ( needToPrintComma )
							w.Write(", ");
					}
					type[w]
					{ needToPrintComma = true; }
				)* 
			) 
			#(	tb:TYPE_BODY { Print(w, #tb); } classMemberDeclarations[w] cc:CLOSE_CURLY { Print(w, #cc); } )
		)
	;
	
classMemberDeclarations [TextWriter w]
	:	#(	MEMBER_LIST
			(	classMemberDeclaration [w]
			|	preprocessorDirective[w, CodeMaskEnums.ClassMemberDeclarations]
			)*
		)
	;
	
classMemberDeclaration [TextWriter w]
	:	(	destructorDeclaration[w]
		|	typeMemberDeclaration[w]
		)
	;
	
typeMemberDeclaration [TextWriter w]
	:	(	constantDeclaration[w]
		|	eventDeclaration[w]
		|	constructorDeclaration[w]
		|	staticConstructorDeclaration[w]
		|	propertyDeclaration[w]
		|	methodDeclaration[w]
		|	indexerDeclaration[w]
		|	fieldDeclaration[w]
		|	operatorDeclaration[w]
		|	typeDeclaration[w]
		)
	;
	
constantDeclaration [TextWriter w]
	:	#(	c:CONST attributes[w] modifiers[w] { Print(w, #c, null, " "); } type[w]
			( constantDeclarator[w] )+
			{ w.Write(";" + newLine_); }
		)
	;
	
fieldDeclaration [TextWriter w]
	:	#(	FIELD_DECL attributes[w] modifiers[w] type[w]
			( variableDeclarator[w] )+
			{ w.Write(";" + newLine_); }
		)
	;
	
variableDeclarator [TextWriter w]
	:	#(	VAR_DECLARATOR identifier[w]
			( { w.Write(" = "); } variableInitializer[w] )? 
		)
	;
	
variableInitializer [TextWriter w]
	:	#(	VAR_INIT
			(	expression[w]
			|	arrayInitializer[w]
			|	stackallocInitializer[w]
			)
		)
	;
		
methodDeclaration [TextWriter w]
	:	#(	METHOD_DECL attributes[w] modifiers[w] type[w] qualifiedIdentifier[w]
			{
				StringBuilder sb = new StringBuilder();
				StringWriter  sw = new StringWriter(sb);
			}
			methodBody[sw]
			{ w.Write("("); } ( formalParameterList[w] )? 
			{
				w.Write(")" + newLine_);
				w.Write(sb.ToString());
				sw.Close();
			}
		)
	;
	
memberName [TextWriter w]
	:	qualifiedIdentifier[w]					// interfaceType^ DOT! identifier
//	|	identifier
	;
	
methodBody [TextWriter w]
	:	body[w]
	;
	
formalParameterList [TextWriter w]
	:	#(	FORMAL_PARAMETER_LIST 
			(	fixedParameters[w] ( { w.Write(", "); } parameterArray[w] )?
			|	parameterArray[w]
			)
		)
	;
	
fixedParameters [TextWriter w]
	:	fixedParameter[w] ( { w.Write(", "); } fixedParameter[w] )*
	;
	
fixedParameter [TextWriter w]
	:	#( 	PARAMETER_FIXED attributes[w] 
			{
				StringBuilder sb = new StringBuilder();
				StringWriter  sw = new StringWriter(sb);
			}
			type[sw] identifier[sw] 
			( parameterModifier[w] )?
			{
				w.Write(sb.ToString());
				sw.Close();
			}
		)
	;
	
parameterModifier [TextWriter w]
	:	r:REF		{ Print(w, #r, null, " "); } 
	|	o:OUT		{ Print(w, #o, null, " "); } 
	;
	
parameterArray [TextWriter w]
	:	#(	parray:PARAMS attributes[w] { Print(w, #parray, null, " "); } type[w] identifier[w]
		)
	;
	
propertyDeclaration [TextWriter w]
	:	#(	PROPERTY_DECL attributes[w] modifiers[w] type[w] qualifiedIdentifier[w] 
			{ w.Write(newLine_ + "{ "); }
			accessorDeclarations[w] curl:CLOSE_CURLY { Print(w, #curl); } 
		)
	;
	
accessorDeclarations [TextWriter w]
	:	(	getAccessorDeclaration[w] ( setAccessorDeclaration[w] )?
		|	setAccessorDeclaration[w] ( getAccessorDeclaration[w] )?
		)
	;
	
getAccessorDeclaration [TextWriter w]
	:	#( "get" attributes[w] { w.Write("get"); } accessorBody[w] )
	;
	
setAccessorDeclaration [TextWriter w]
	:	#( "set" attributes[w] { w.Write("set"); } accessorBody[w] )
	;
	
accessorBody [TextWriter w]
	:	block[w]
	|	e:EMPTY_STMT	{ Print(w, #e, null, " "); } 
	;
	
eventDeclaration [TextWriter w]
	:	#(	evt:EVENT attributes[w] modifiers[w] { Print(w, #evt, null, " "); } type[w]
			(	qualifiedIdentifier[w] { w.Write(newLine_ + "{"); }
				eventAccessorDeclarations[w] cly:CLOSE_CURLY { Print(w, #cly); } 
			|	variableDeclarator[w] ( { w.Write(", "); } variableDeclarator[w] )* { w.Write("; "); }
				{ w.Write(newLine_); }
			)
		)
	;
	
eventAccessorDeclarations [TextWriter w]
	:	addAccessorDeclaration[w] removeAccessorDeclaration[w]
	|	removeAccessorDeclaration[w] addAccessorDeclaration[w]
	;
	
addAccessorDeclaration [TextWriter w]
	:	#( "add"    attributes[w] { w.Write("add"); } block[w] )
	;
	
removeAccessorDeclaration [TextWriter w]
	:	#( "remove" attributes[w] { w.Write("remove"); } block[w] )
	;

indexerDeclaration [TextWriter w]
	:	#(	INDEXER_DECL attributes[w] modifiers[w]
			type[w] ( interfaceType[w] { w.Write("."); } )? t:THIS { Print(w, #t); w.Write("["); }
			formalParameterList[w] { w.Write("]" + newLine_ + "{" + newLine_); } accessorDeclarations[w] 
			cly:CLOSE_CURLY { Print(w, #cly); }
		)
	;
	
operatorDeclaration [TextWriter w]
	:	(	#(	UNARY_OP_DECL attributes[w] modifiers[w]
				type[w] { w.Write("operator "); } overloadableUnaryOperator[w]
				{ w.Write("("); } formalParameterList[w] { w.Write(")" + newLine_); } 
	 			operatorBody[w]
		 	)
		|	#(	BINARY_OP_DECL attributes[w] modifiers[w]
				type[w] { w.Write("operator "); } overloadableBinaryOperator[w]
				{ w.Write("("); } formalParameterList[w] { w.Write(")" + newLine_); }
		 		operatorBody[w]
			 )
		|	#(	CONV_OP_DECL attributes[w] modifiers[w]
				(	i:IMPLICIT { Print(w, #i, null, " "); w.Write("operator "); } type[w]
					{ w.Write("("); } formalParameterList[w] { w.Write(")" + newLine_); }
					operatorBody[w]
				|	e:EXPLICIT { Print(w, #e, null, " "); w.Write("operator "); } type[w] 
					{ w.Write("("); } formalParameterList[w] { w.Write(")" + newLine_); }
					operatorBody[w]
				)
			)
		)
	;
	
overloadableUnaryOperator [TextWriter w]
	:	up:UNARY_PLUS	{ Print(w, #up); }
	|	um:UNARY_MINUS	{ Print(w, #um); }
	|	ln:LOG_NOT		{ Print(w, #ln); }
	|	bn:BIN_NOT		{ Print(w, #bn); }
	|	ic:INC			{ Print(w, #ic); }
	|	dc:DEC			{ Print(w, #dc); }
	|	tr:TRUE			{ Print(w, #tr); }
	|	fa:FALSE		{ Print(w, #fa); }
	;
	
overloadableBinaryOperator [TextWriter w]
	:	pl:PLUS			{ Print(w, #pl); }
	|	ms:MINUS		{ Print(w, #ms); }
	|	st:STAR			{ Print(w, #st); }
	|	dv:DIV 			{ Print(w, #dv); }
	|	md:MOD 			{ Print(w, #md); }
	|	ba:BIN_AND 		{ Print(w, #ba); }
	|	bo:BIN_OR 		{ Print(w, #bo); }
	|	bx:BIN_XOR 		{ Print(w, #bx); }
	|	sl:SHIFTL 		{ Print(w, #sl); }
	|	sr:SHIFTR 		{ Print(w, #sr); }
	|	eq:EQUAL		{ Print(w, #eq); }
	|	nq:NOT_EQUAL 	{ Print(w, #nq); }
	|	gt:GTHAN		{ Print(w, #gt); }
	|	lt:LTHAN 		{ Print(w, #lt); }
	|	ge:GTE 			{ Print(w, #ge); }
	|	le:LTE 			{ Print(w, #le); }
	;
	
operatorBody [TextWriter w]
	:	body[w]
	;

constructorDeclaration [TextWriter w]
	:	#(	CTOR_DECL attributes[w] modifiers[w] identifier[w]
			{
				StringBuilder sb = new StringBuilder();
				StringWriter  sw = new StringWriter(sb);
			}
			constructorBody[sw]
			{ w.Write("("); } ( formalParameterList[w] )? { w.Write(") "); }
			( constructorInitializer[w] )? 
			{
				w.Write(sb.ToString());
				sw.Close();
			}
		)
	;
	
constructorInitializer [TextWriter w]
	:	{ w.Write(": "); }
		(	#( b:BASE { Print(w, #b); w.Write("("); } ( argumentList[w] )? )
		|	#( t:THIS { Print(w, #t); w.Write("("); } ( argumentList[w] )? )
		)
		{ w.Write(")" + newLine_); }
	;
	
constructorBody [TextWriter w]
	:	body[w]
	;

staticConstructorDeclaration [TextWriter w]
	:	#(	STATIC_CTOR_DECL attributes[w] modifiers[w] identifier[w] { w.Write("()" + newLine_); }
			staticConstructorBody[w]
		)
	;
	
staticConstructorBody [TextWriter w]
	:	body[w]
	;
	
destructorDeclaration [TextWriter w]
	:	#( 	DTOR_DECL attributes[w] modifiers[w] { w.Write("~"); } identifier[w] { w.Write("()" + newLine_); }
			destructorBody[w]
		)
	;
	
destructorBody [TextWriter w]
	:	body[w]
	;

	
//
// A.2.7 Structs
//

structDeclaration [TextWriter w]
	:	#( 	st:STRUCT attributes[w] modifiers[w] { PrintWithBeforeCheck(w, #st, null, " "); } identifier[w]
			#(	STRUCT_BASE 
				{
					bool needToPrintColon = true;
					bool needToPrintComma = false;
				}
				(	{
						if ( needToPrintColon )
						{
							w.Write(" : ");
							needToPrintColon = false;
						}
						if ( needToPrintComma )
							w.Write(", ");
					}
					type[w]
					{ needToPrintComma = true; }
				)* 
			)
			#(	tb:TYPE_BODY { Print(w, #tb); } structMemberDeclarations[w] cc:CLOSE_CURLY { Print(w, #cc); } )
		)
	;
	
structMemberDeclarations [TextWriter w]
	:	#(	MEMBER_LIST
			(	structMemberDeclaration[w]
			|	preprocessorDirective[w, CodeMaskEnums.StructMemberDeclarations]
			)*
		)
	;
	
structMemberDeclaration [TextWriter w]
	:	typeMemberDeclaration[w]
	;

	
//
// A.2.8 Arrays
//

rankSpecifiers [TextWriter w]
	:	#(	ARRAY_RANKS
			( 	rankSpecifier[w]
			)*
		)
	;
	
rankSpecifier  [TextWriter w]
	:	#(	r:ARRAY_RANK { Print(w, #r); }
			(	c:COMMA { Print(w, #c); w.Write(","); }
			)* 
			{ w.Write("]"); }
		)
	;
	
arrayInitializer [TextWriter w]
	:	#(	ai:ARRAY_INIT { Print(w, #ai); } ( variableInitializerList[w] )? cc:CLOSE_CURLY { Print(w, #cc); } )
	;
	
variableInitializerList [TextWriter w]
	:	#( VAR_INIT_LIST variableInitializer[w] ( { w.Write(", "); } variableInitializer[w] )* )
	;


// 
// A.2.9 Interfaces
//

interfaceDeclaration [TextWriter w]
	:	#(	iface:INTERFACE attributes[w] modifiers[w] { PrintWithBeforeCheck(w, #iface, null, " "); } identifier[w]
			#(	INTERFACE_BASE 
				{
					bool needToPrintColon = true;
					bool needToPrintComma = false;
				}
				(	{
						if ( needToPrintColon )
						{
							w.Write(" : ");
							needToPrintColon = false;
						}
						if ( needToPrintComma )
							w.Write(", ");
					}
					type[w]
					{ needToPrintComma = true; }
				)* 
			)
			interfaceBody[w]
		)
	;
	
interfaceBody [TextWriter w]
	:	#(	tb:TYPE_BODY { Print(w, #tb); } interfaceMemberDeclarations[w] cc:CLOSE_CURLY { Print(w, #cc); } )
	;
	
interfaceMemberDeclarations [TextWriter w]
	:	#(	MEMBER_LIST
			(	interfaceMemberDeclaration[w]
			|	preprocessorDirective[w, CodeMaskEnums.InterfaceMemberDeclarations]
			)*
		)
	;
	
interfaceMemberDeclaration [TextWriter w]
	:	(	interfaceMethodDeclaration[w]
		|	interfacePropertyDeclaration[w]
		|	interfaceEventDeclaration[w]
		|	interfaceIndexerDeclaration[w]
		)
		{ w.Write(newLine_); }
	;
	
interfaceMethodDeclaration [TextWriter w]
	:	#(	METHOD_DECL attributes[w] modifiers[w] type[w] qualifiedIdentifier[w]
			{
				StringBuilder sb = new StringBuilder();
				StringWriter  sw = new StringWriter(sb);
			}
			e:EMPTY_STMT { Print(sw, #e); }
			{ w.Write("("); } ( formalParameterList[w] )?
			{
				w.Write(") ");
				w.Write(sb.ToString());
				sw.Close();
			}
		)
	;
	
interfacePropertyDeclaration [TextWriter w]
	:	#(	PROPERTY_DECL attributes[w] modifiers[w] type[w] identifier[w]
			{ w.Write("{"); } accessorDeclarations[w] cc:CLOSE_CURLY { Print(w, #cc); }
		)
	;
	
interfaceEventDeclaration [TextWriter w]
	:	#(	evt:EVENT attributes[w] modifiers[w] { Print(w, #evt, null, " "); }
			type[w] variableDeclarator[w]		 { w.Write(";" + newLine_);  }
		)
	;
	
interfaceIndexerDeclaration [TextWriter w]
	:	#(	INDEXER_DECL attributes[w] modifiers[w] type[w] t:THIS { Print(w, #t); w.Write("["); } 
			formalParameterList[w] { w.Write("]" + newLine_); }
			{ w.Write("{"); } accessorDeclarations[w] cc:CLOSE_CURLY { Print(w, #cc); }
		)
	;

	
//
//	A.2.10 Enums
//

enumDeclaration [TextWriter w]
	:	#( 	en:ENUM attributes[w] modifiers[w] { PrintWithBeforeCheck(w, #en, null, " "); } identifier[w]
			#( 	ENUM_BASE ( { w.Write(" : "); } type[w] )? )
			#(	tb:TYPE_BODY { Print(w, #tb); }
				#(	MEMBER_LIST 
					( enumMemberDeclaration[w] { w.Write("," + newLine_); } )* 
				) 
				cc:CLOSE_CURLY { Print(w, #cc); }
			)
		)
	;
	
enumMemberDeclaration [TextWriter w]
	:	#(	id:IDENTIFIER attributes[w] { Print(w, #id, null, " "); } ( { w.Write(" = "); } constantExpression[w] )?
		)
 	;


//
// A.2.11 Delegates
//

delegateDeclaration [TextWriter w]
	:	#(	dlg:DELEGATE attributes[w] modifiers[w] { PrintWithBeforeCheck(w, #dlg, null, " "); }
			type[w] identifier[w] { w.Write("("); } ( formalParameterList[w] )? { w.Write(");" + newLine_); }
		)
	;
	

//
// A.2.12 Attributes
//

globalAttributes [TextWriter w]
	:	#(	GLOBAL_ATTRIBUTE_SECTIONS 
			(	globalAttributeSection[w]
			|	preprocessorDirective[w, CodeMaskEnums.GlobalAttributes]
			)*
		)
	;
	
globalAttributeSection [TextWriter w]
	:	#(	sect:GLOBAL_ATTRIBUTE_SECTION { PrintWithBeforeCheck(w, #sect); w.Write("assembly: "); } 
			( attribute[w] )+  { w.Write("]" + newLine_); }
		)
	;

attributes [TextWriter w]
	:	#(	ATTRIBUTE_SECTIONS 
			(	attributeSection[w]
			|	preprocessorDirective[w, CodeMaskEnums.Attributes]
			)*
		)
	;
	
attributeSection [TextWriter w]
	:	#(	sect:ATTRIBUTE_SECTION { PrintWithBeforeCheck(w, #sect); } ( attributeTarget[w] )?
			( attribute[w] )+  { w.Write("]" + newLine_); }
		)
	;
	
attributeTarget[TextWriter w]
	:	(	fv:"field"			{ Print(w, #fv); }
		|	ev:EVENT			{ Print(w, #ev); }
		|	mv:"method"			{ Print(w, #mv); }
		|	mo:"module"			{ Print(w, #mo); }
		|	pa:"param"			{ Print(w, #pa); }
		|	pr:"property"		{ Print(w, #pr); }
		|	re:RETURN			{ Print(w, #re); }
		|	ty:"type"			{ Print(w, #ty); }
		)
		{ w.Write(": "); }
	;

attribute [TextWriter w]
	:	#( ATTRIBUTE typeName[w] attributeArguments[w] )
	;
	
attributeArguments [TextWriter w]
	:	{ w.Write("("); }  ( positionalArgumentList[w] )? ( namedArgumentList[w] )? { w.Write(") "); } 
	;
	
positionalArgumentList [TextWriter w]
	:	#(	POSITIONAL_ARGLIST positionalArgument[w]
			( { w.Write(", "); } positionalArgument[w] )* 
		)
	;
	
positionalArgument [TextWriter w]
	:	#( POSITIONAL_ARG attributeArgumentExpression[w] )
	;
	
namedArgumentList [TextWriter w]
	:	#(	NAMED_ARGLIST namedArgument[w]
			( { w.Write(", "); } namedArgument[w] )* 
		)
	;
	
namedArgument [TextWriter w]
	:	#( NAMED_ARG identifier[w] { w.Write(" = "); } attributeArgumentExpression[w] )
	;
	
attributeArgumentExpression [TextWriter w]
	:	#( ATTRIB_ARGUMENT_EXPR expression[w] )
	;

//
// A.3 Grammar extensions for unsafe code
// 

fixedPointerDeclarator [TextWriter w]
	:	#( PTR_DECLARATOR identifier[w] { w.Write(" = "); } fixedPointerInitializer[w] )
	;
	
fixedPointerInitializer [TextWriter w]
	:	#(	PTR_INIT
			(	b:BIN_AND { Print(w, #b); } variableReference[w]
			|	expression[w]
			)
		)
	;	
	
stackallocInitializer [TextWriter w]
	:	#( s:STACKALLOC { Print(w, #s, null, " "); } unmanagedType[w] { w.Write("["); } expression[w] { w.Write("]"); } )
	;

//======================================
// Preprocessor Directives
//======================================

justPreprocessorDirectives [TextWriter w]
	:	#(	PP_DIRECTIVES 
			(	preprocessorDirective[w, CodeMaskEnums.PreprocessorDirectivesOnly] 
			)* 
		)
	;
	
preprocessorDirective [TextWriter w, CodeMaskEnums codeMask]
	:	#( d1:PP_DEFINE   { w.Write("\n"); Print(w, #d1, null, " "); } d2:PP_IDENT { Print(w, #d2, null, "\n"); } )
	|	#( u1:PP_UNDEFINE { w.Write("\n"); Print(w, #u1, null, " "); } u2:PP_IDENT { Print(w, #u2, null, "\n"); } )
	|	#(	l1:PP_LINE    { w.Write("\n"); Print(w, #l1, null, " "); }
			(	l2:DEFAULT   { Print(w, #l2, null, "\n"); }
			|	l3:PP_NUMBER { Print(w, #l3, null, " "); } ( l4:PP_FILENAME { Print(w, #l4); } )? { w.Write("\n"); }
			)
		)
	|	#(	e1:PP_ERROR   { w.Write("\n"); Print(w, #e1); } ppMessage[w] { w.Write("\n"); })
	|	#(	w1:PP_WARNING { w.Write("\n"); Print(w, #w1); } ppMessage[w] { w.Write("\n"); })
	|	regionDirective[w, codeMask]
	|	conditionalDirective[w, codeMask]
	;
	
regionDirective [TextWriter w, CodeMaskEnums codeMask]
	:	#(	r1:PP_REGION { w.Write("\n"); Print(w, #r1, null, " "); } ppMessage[w] { w.Write("\n"); } directiveBlock[w, codeMask]
			#( r2:PP_ENDREGION { w.Write("\n"); Print(w, #r2, null, " "); } ppMessage[w] { w.Write("\n"); })
		)
	;

conditionalDirective [TextWriter w, CodeMaskEnums codeMask]
	:	#(	c1:PP_COND_IF        { w.Write("\n"); Print(w, #c1, null, " "); } preprocessExpression[w] { w.Write("\n"); } directiveBlock[w, codeMask]
			( #( c2:PP_COND_ELIF { w.Write("\n"); Print(w, #c2, null, " "); } preprocessExpression[w] { w.Write("\n"); } directiveBlock[w, codeMask] ) )*
			( #( c3:PP_COND_ELSE { w.Write("\n"); Print(w, #c3, null, " "); }                         { w.Write("\n"); } directiveBlock[w, codeMask] ) )?
			c4:PP_COND_ENDIF     { w.Write("\n"); Print(w, #c4, null, "\n"); }
		)
	;

directiveBlock [TextWriter w, CodeMaskEnums codeMask]
	:	#(	PP_BLOCK
			(	{ NotExcluded(codeMask, CodeMaskEnums.UsingDirectives) }?				usingDirective[w]
			|	{ NotExcluded(codeMask, CodeMaskEnums.GlobalAttributes) }?				globalAttributeSection[w]
			|	{ NotExcluded(codeMask, CodeMaskEnums.Attributes) }?					attributeSection[w]
			|	{ NotExcluded(codeMask, CodeMaskEnums.NamespaceMemberDeclarations) }?	namespaceMemberDeclaration[w]
			|	{ NotExcluded(codeMask, CodeMaskEnums.ClassMemberDeclarations) }?		classMemberDeclaration[w]
			|	{ NotExcluded(codeMask, CodeMaskEnums.StructMemberDeclarations) }?		structMemberDeclaration[w]
			|	{ NotExcluded(codeMask, CodeMaskEnums.InterfaceMemberDeclarations) }?	interfaceMemberDeclaration[w]
			|	{ NotExcluded(codeMask, CodeMaskEnums.Statements) }?					statement[w]
			|	preprocessorDirective[w, codeMask]
			)*
		)
	;
	
ppMessage [TextWriter w]
	:	#(	PP_MESSAGE
			(	m1:PP_IDENT 		{ Print(w, #m1); }
			|	m2:PP_STRING 		{ Print(w, #m2); }
			| 	m3:PP_FILENAME 		{ Print(w, #m3); }
			| 	m4:PP_NUMBER 		{ Print(w, #m4); }
			)*
		)
	;

preprocessExpression [TextWriter w]
	:	#( PP_EXPR preprocessExpr[w] )
	;

preprocessExpr [TextWriter w]
	:	#( o:LOG_OR    preprocessExpr[w] { Print(w, #o); } preprocessExpr[w] )
	|	#( a:LOG_AND   preprocessExpr[w] { Print(w, #a); } preprocessExpr[w] )
	|	#( e:EQUAL     preprocessExpr[w] { Print(w, #e); } preprocessExpr[w] )
	|	#( n:NOT_EQUAL preprocessExpr[w] { Print(w, #n); } preprocessExpr[w] )
	|	preprocessPrimaryExpression[w]
	;
	
preprocessPrimaryExpression [TextWriter w]
	:	i:PP_IDENT		{ Print(w, #i);  } 
	|	tr:TRUE			{ Print(w, #tr); }
	|	fa:FALSE		{ Print(w, #fa); }
	|	#( l:LOG_NOT 	{ Print(w, #l);  } preprocessPrimaryExpression[w] )
	|	#( p:PAREN_EXPR { Print(w, #p);  } preprocessExpr[w] { w.Write(") "); } )
	;
