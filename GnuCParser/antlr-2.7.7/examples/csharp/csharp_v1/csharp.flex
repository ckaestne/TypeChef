/*
	[The "BSD licence"]
	Copyright (c) 2002-2005 Kunle Odutola
	All rights reserved.
	
	Redistribution and use in source and binary forms, with or without
	modification, are permitted provided that the following conditions
	are met:
	1. Redistributions of source code MUST RETAIN the above copyright
	   notice, this list of conditions and the following disclaimer.
	2. Redistributions in binary form MUST REPRODUCE the above copyright
	   notice, this list of conditions and the following disclaimer in 
	   the documentation and/or other materials provided with the 
	   distribution.
	3. The name of the author may not be used to endorse or promote products
	   derived from this software without specific prior WRITTEN permission.
	
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
*/

namespace Kunle.CSharpParser
{
	using System;
	using System.IO;
	using System.Text;
	using System.Globalization;
	using antlr;
	using symID = CSharpTokenTypes;

	/// <summary>
	/// A Lexer for the C# language including preprocessors directives.
	/// </summary>
	///
	/// <remarks>
	/// <para>
	/// The Lexer defined below is based on the "C# Language Specification" as 
	/// documented in the ECMA-334 standard dated December 2001.
	/// </para>
	///
	/// <para>
	/// History
	/// </para>
	///
	/// <para>
	/// 01-Dec-2005 kunle	  Created lexer from ECMA spec <br />
	/// </para>
	///
	/// </remarks>
%%


%final
%public
%class CSharpFlexLexer

%parser_sym CSharpTokenTypes
%type 		CustomHiddenStreamToken
%antlr

%unicode
%column
%line

%eofclose

%xstates YYINITIAL_STRING, YYINITIAL_VERBATIM, YYINITIAL_CHAR, YYINITIAL_PP
%xstates PP_SYMBOLS
%xstates PP_HOOVER


%{
	private FileInfo fileinfo;
	private StringBuilder stringBuf = new StringBuilder();
	
	public void SetFileInfo(FileInfo fileinfo)
	{
		this.fileinfo = fileinfo;
	}
	
	public int Line
	{
		get { return yyline+1; }
	}
	
	public int Column
	{
		get { return yycolumn+1; }
	}
	
	private bool IsLetterCharacter(string s)
	{
		return ( (UnicodeCategory.LowercaseLetter == Char.GetUnicodeCategory(s, 1)) ||  //UNICODE class Ll
		         (UnicodeCategory.ModifierLetter  == Char.GetUnicodeCategory(s, 1)) ||  //UNICODE class Lm
		         (UnicodeCategory.OtherLetter     == Char.GetUnicodeCategory(s, 1)) ||  //UNICODE class Lo
		         (UnicodeCategory.TitlecaseLetter == Char.GetUnicodeCategory(s, 1)) ||  //UNICODE class Lt
		         (UnicodeCategory.UppercaseLetter == Char.GetUnicodeCategory(s, 1)) ||  //UNICODE class Lu
		         (UnicodeCategory.LetterNumber    == Char.GetUnicodeCategory(s, 1))     //UNICODE class Nl
		        );
	}

	private bool IsIdentifierCharacter(string s)
	{
		return ( (UnicodeCategory.LowercaseLetter      == Char.GetUnicodeCategory(s, 1)) ||  //UNICODE class Ll
		         (UnicodeCategory.ModifierLetter       == Char.GetUnicodeCategory(s, 1)) ||  //UNICODE class Lm
		         (UnicodeCategory.OtherLetter          == Char.GetUnicodeCategory(s, 1)) ||  //UNICODE class Lo
		         (UnicodeCategory.TitlecaseLetter      == Char.GetUnicodeCategory(s, 1)) ||  //UNICODE class Lt
		         (UnicodeCategory.UppercaseLetter      == Char.GetUnicodeCategory(s, 1)) ||  //UNICODE class Lu
		         (UnicodeCategory.LetterNumber         == Char.GetUnicodeCategory(s, 1)) ||  //UNICODE class Nl		         
		         (UnicodeCategory.NonSpacingMark       == Char.GetUnicodeCategory(s, 1)) ||  //UNICODE class Mn		         
		         (UnicodeCategory.SpacingCombiningMark == Char.GetUnicodeCategory(s, 1)) ||  //UNICODE class Mc
		         (UnicodeCategory.DecimalDigitNumber   == Char.GetUnicodeCategory(s, 1)) ||  //UNICODE class Nd
		         (UnicodeCategory.ConnectorPunctuation == Char.GetUnicodeCategory(s, 1)) ||  //UNICODE class Pc
		         (UnicodeCategory.Format               == Char.GetUnicodeCategory(s, 1))     //UNICODE class Cf
		        );
	}

	private IToken MakeToken(int type, string lexeme)
	{
		return new CustomHiddenStreamToken(type, lexeme, fileinfo, Line, Column);
	}
%}


UnicodeCategory_Zs 			= [\u0020\u00A0\u1680\u2000-\u200B\u202F\u205F\u3000]
Identifier					= [:jletter:] [:jletterdigit:]*
   
NonNewLineWhiteSpace 		= [\t\f\u000B]|{UnicodeCategory_Zs}
LineTerminator 				= \r|\n|\r\n|\u2028|\u2029
InputCharacter	 			= [^\r\n\u2028\u2029]
SingleLineComment			= "//" {InputCharacter}* {LineTerminator}?
MultiLineComment			= "/*" ~"*/"

HexDigit					= [0-9a-fA-F]
Digit						= [0-9]
Number						= {Digit}+
FourCharUnicodeEscape 		= \\ u {HexDigit}{4}
EightCharUnicodeEscape		= \\ U {HexDigit}{8}
UnicodeEscapeSequence		= {FourCharUnicodeEscape}|{EightCharUnicodeEscape}

DecimalIntLiteral			= {Digit}+ 
HexadecimalIntLiteral		= 0 [xX] {HexDigit}+
IntLiteral					= ( {DecimalIntLiteral}|{HexadecimalIntLiteral} )
UIntLiteral					= ( {DecimalIntLiteral}|{HexadecimalIntLiteral} ) [Uu]
LongLiteral					= ( {DecimalIntLiteral}|{HexadecimalIntLiteral} ) [Ll]
ULongLiteral				= ( {DecimalIntLiteral}|{HexadecimalIntLiteral} ) ( [Uu][Ll] | [Ll][Uu] )

ExponentPart				= [eE] [+-]? {Digit}+
Real1						= {Digit}+ \. {Digit}+ 
Real2						= \. {Digit}+
Real3						= {Digit}+
DoubleLiteral				= ({Real1}|{Real2}|{Real3}) {ExponentPart}? [Dd]?
FloatLiteral				= ({Real1}|{Real2}|{Real3}) {ExponentPart}? [Ff]
DecimalLiteral				= ({Real1}|{Real2}|{Real3}) {ExponentPart}? [Mm]

HexadecimalEscapeSequence	= \\ x {HexDigit}{1,4}
CharLiteralCharacter		= [^\'\\\r\n\u2028\u2029]
StringLiteralCharacter		= [^\"\\\r\n\u2028\u2029]

PreprocessorDirectiveStart	= {NonNewLineWhiteSpace}* # {NonNewLineWhiteSpace}*


%%


<YYINITIAL,PP_SYMBOLS,PP_HOOVER> {

  /* C# language&preprocessor keywords */
  "true"                     	{ return MakeToken(symID.TRUE, "true");			}
  "false"                     	{ return MakeToken(symID.FALSE, "false");		}
  "default"                  	{ return MakeToken(symID.DEFAULT, "default");	}
}

<YYINITIAL> {

  /* C# language-only keywords */
  "abstract"                  	{ return MakeToken(symID.ABSTRACT, "abstract");		}
  "as"                  		{ return MakeToken(symID.AS, "as");					}
  "base"                  		{ return MakeToken(symID.BASE, "base");				}
  "bool"                  		{ return MakeToken(symID.BOOL, "bool");				}
  "break"                  		{ return MakeToken(symID.BREAK, "break");			}
  "byte"                  		{ return MakeToken(symID.BYTE, "byte");				}
  "case"                  		{ return MakeToken(symID.CASE, "case");				}
  "catch"                  		{ return MakeToken(symID.CATCH, "catch");			}
  "char"                  		{ return MakeToken(symID.CHAR, "char");				}
  "checked"                		{ return MakeToken(symID.CHECKED, "checked");		}
  "class"                  		{ return MakeToken(symID.CLASS, "class");			}
  "const"                  		{ return MakeToken(symID.CONST, "const");			}
  "continue"               		{ return MakeToken(symID.CONTINUE, "continue");		}
  "decimal"                		{ return MakeToken(symID.DECIMAL, "decimal");		}
  "delegate"               		{ return MakeToken(symID.DELEGATE, "delegate");		}
  "do"                  		{ return MakeToken(symID.DO, "do");					}
  "double"                 		{ return MakeToken(symID.DOUBLE, "double");			}
  "else"                  		{ return MakeToken(symID.ELSE, "else");				}
  "enum"                  		{ return MakeToken(symID.ENUM, "enum");				}
  "event"                  		{ return MakeToken(symID.EVENT, "event");			}
  "explicit"               		{ return MakeToken(symID.EXPLICIT, "explicit");		}
  "extern"						{ return MakeToken(symID.EXTERN, "extern");			}
  "finally"                		{ return MakeToken(symID.FINALLY, "finally");		}
  "fixed"                  		{ return MakeToken(symID.FIXED, "fixed");			}
  "float"                  		{ return MakeToken(symID.FLOAT, "float");			}
  "for"                  		{ return MakeToken(symID.FOR, "for");				}
  "foreach"						{ return MakeToken(symID.FOREACH, "foreach");		}
  "goto"						{ return MakeToken(symID.GOTO, "goto");				}
  "if"							{ return MakeToken(symID.IF, "if");					}
  "implicit"					{ return MakeToken(symID.IMPLICIT, "implicit");		}
  "in"							{ return MakeToken(symID.IN, "in");					}
  "int"							{ return MakeToken(symID.INT, "int");				}
  "interface"					{ return MakeToken(symID.INTERFACE, "interface");	}
  "internal"					{ return MakeToken(symID.INTERNAL, "internal");		}
  "is"							{ return MakeToken(symID.IS, "is");					}
  "lock"						{ return MakeToken(symID.LOCK, "lock");				}
  "long"						{ return MakeToken(symID.LONG, "long");				}
  "namespace"					{ return MakeToken(symID.NAMESPACE, "namespace");	}
  "new"							{ return MakeToken(symID.NEW, "new");				}
  "null"						{ return MakeToken(symID.NULL, "null");				}
  "object"						{ return MakeToken(symID.OBJECT, "object");			}
  "operator"					{ return MakeToken(symID.OPERATOR, "operator");		}
  "out"							{ return MakeToken(symID.OUT, "out");				}
  "override"					{ return MakeToken(symID.OVERRIDE, "override");		}
  "params"						{ return MakeToken(symID.PARAMS, "params");			}
  "private"						{ return MakeToken(symID.PRIVATE, "private");		}
  "protected"					{ return MakeToken(symID.PROTECTED, "protected");	}
  "public"						{ return MakeToken(symID.PUBLIC, "public");			}
  "readonly"					{ return MakeToken(symID.READONLY, "readonly");		}
  "ref"							{ return MakeToken(symID.REF, "ref");				}
  "return"						{ return MakeToken(symID.RETURN, "return");			}
  "sbyte"						{ return MakeToken(symID.SBYTE, "sbyte");			}
  "sealed"						{ return MakeToken(symID.SEALED, "sealed");			}
  "short"						{ return MakeToken(symID.SHORT, "short");			}
  "sizeof"						{ return MakeToken(symID.SIZEOF, "sizeof");			}
  "stackalloc"					{ return MakeToken(symID.STACKALLOC, "stackalloc");	}
  "static"						{ return MakeToken(symID.STATIC, "static");			}
  "string"						{ return MakeToken(symID.STRING, "string");			}
  "struct"						{ return MakeToken(symID.STRUCT, "struct");			}
  "switch"						{ return MakeToken(symID.SWITCH, "switch");			}
  "this"						{ return MakeToken(symID.THIS, "this");				}
  "throw"						{ return MakeToken(symID.THROW, "throw");			}
  "try"							{ return MakeToken(symID.TRY, "try");				}
  "typeof"						{ return MakeToken(symID.TYPEOF, "typeof");			}
  "uint"						{ return MakeToken(symID.UINT, "uint");				}
  "ulong"						{ return MakeToken(symID.ULONG, "ulong");			}
  "unchecked"					{ return MakeToken(symID.UNCHECKED, "unchecked");	}
  "unsafe"						{ return MakeToken(symID.UNSAFE, "unsafe");			}
  "ushort"						{ return MakeToken(symID.USHORT, "ushort");			}
  "using"						{ return MakeToken(symID.USING, "using");			}
  "virtual"						{ return MakeToken(symID.VIRTUAL, "virtual");		}
  "void"						{ return MakeToken(symID.VOID, "void");				}
  "volatile"					{ return MakeToken(symID.VOLATILE, "volatile");		}
  "while"						{ return MakeToken(symID.WHILE, "while");			}

  /* C# language-only non-reserved keywords */
  "add"							{ return MakeToken(symID.LITERAL_add, "add");			}
  "remove"						{ return MakeToken(symID.LITERAL_remove, "remove");		}
  "get"							{ return MakeToken(symID.LITERAL_get, "get");			}
  "set"							{ return MakeToken(symID.LITERAL_set, "set");			}
  "assembly"					{ return MakeToken(symID.LITERAL_assembly, "assembly");	}
  "field"						{ return MakeToken(symID.LITERAL_field, "field");		}
  "method"						{ return MakeToken(symID.LITERAL_method, "method");		}
  "module"						{ return MakeToken(symID.LITERAL_module, "module");		}
  "param"						{ return MakeToken(symID.LITERAL_param, "param");		}
  "property"					{ return MakeToken(symID.LITERAL_property, "property");	}
  "type"						{ return MakeToken(symID.LITERAL_type, "type");			}

  /* C# operators */
  "."		                  	{ return MakeToken(symID.DOT, ".");		}
  "("		                  	{ return MakeToken(symID.OPEN_PAREN, "(");		}
  ")"		                  	{ return MakeToken(symID.CLOSE_PAREN, ")");		}
  "!"		                  	{ return MakeToken(symID.LOG_NOT, "!");			}
  "&&"		                  	{ return MakeToken(symID.LOG_AND, "&&");		}
  "||"		                  	{ return MakeToken(symID.LOG_OR, "||");			}
  "=="		                  	{ return MakeToken(symID.EQUAL, "==");			}
  "!="		                  	{ return MakeToken(symID.NOT_EQUAL, "!=");		}
  "{"		                  	{ return MakeToken(symID.OPEN_CURLY, "{");		}
  "}"		                  	{ return MakeToken(symID.CLOSE_CURLY, "}");		}
  "["		                  	{ return MakeToken(symID.OPEN_BRACK, "[");		}
  "]"		                  	{ return MakeToken(symID.CLOSE_BRACK, "]");		}
  ","		                  	{ return MakeToken(symID.COMMA, ",");			}
  ":"		                  	{ return MakeToken(symID.COLON, ":");			}
  ";"		                  	{ return MakeToken(symID.SEMI, ";");			}
  "+"		                  	{ return MakeToken(symID.PLUS, "+");			}
  "-"		                  	{ return MakeToken(symID.MINUS, "-");			}
  "*"		                  	{ return MakeToken(symID.STAR, "*");			}
  "/"		                  	{ return MakeToken(symID.DIV, "/");				}
  "%"		                  	{ return MakeToken(symID.MOD, "%");				}
  "&"		                  	{ return MakeToken(symID.BIN_AND, "&");			}
  "|"		                  	{ return MakeToken(symID.BIN_OR, "|");			}
  "^"		                  	{ return MakeToken(symID.BIN_XOR, "^");			}
  "~"		                  	{ return MakeToken(symID.BIN_NOT, "~");			}
  "="		                  	{ return MakeToken(symID.ASSIGN, "=");			}
  "<"		                  	{ return MakeToken(symID.LTHAN, "<");			}
  ">"		                  	{ return MakeToken(symID.GTHAN, ">");			}
  "?"		                  	{ return MakeToken(symID.QUESTION, "?");		}
  "++"		                  	{ return MakeToken(symID.INC, "++");			}
  "--"		                  	{ return MakeToken(symID.DEC, "--");			}
  "<<"		                  	{ return MakeToken(symID.SHIFTL, "<<");			}
  ">>"		                  	{ return MakeToken(symID.SHIFTR, ">>");			}
  "<="		                  	{ return MakeToken(symID.LTE, "<=");			}
  ">="		                  	{ return MakeToken(symID.GTE, ">=");			}
  "+="		                  	{ return MakeToken(symID.PLUS_ASSIGN, "+=");	}
  "-="		                  	{ return MakeToken(symID.MINUS_ASSIGN, "-=");	}
  "*="		                  	{ return MakeToken(symID.STAR_ASSIGN, "*=");	}
  "/="		                  	{ return MakeToken(symID.DIV_ASSIGN, "/=");		}
  "%="		                  	{ return MakeToken(symID.MOD_ASSIGN, "%=");		}
  "&="		                  	{ return MakeToken(symID.BIN_AND_ASSIGN, "&=");	}
  "|="		                  	{ return MakeToken(symID.BIN_OR_ASSIGN, "|=");	}
  "^="		                  	{ return MakeToken(symID.BIN_XOR_ASSIGN, "^=");	}
  "<<="		                  	{ return MakeToken(symID.SHIFTL_ASSIGN, "<<=");	}
  ">>="		                  	{ return MakeToken(symID.SHIFTR_ASSIGN, ">>=");	}
  "->"		                  	{ return MakeToken(symID.DEREF, "->");			}
  
  /* other rules */
  \'							{	yybegin(YYINITIAL_CHAR);					}
  @ \"							{	yybegin(YYINITIAL_VERBATIM);
  									stringBuf.Length = 0;
  								}
  \"							{	yybegin(YYINITIAL_STRING);
  									stringBuf.Length = 0;
  								}
  {MultiLineComment}			{	return MakeToken(symID.ML_COMMENT, yytext());	}
  {SingleLineComment}			{	return MakeToken(symID.SL_COMMENT, yytext());	}
  {LineTerminator}				{	return MakeToken(symID.NEWLINE, yytext()); 		}
  {NonNewLineWhiteSpace}+		{	return MakeToken(symID.WHITESPACE, yytext()); 	}
  {IntLiteral}					{	return MakeToken(symID.INT_LITERAL, yytext()); 	}
  {UIntLiteral}					{	return MakeToken(symID.UINT_LITERAL, yytext()); }
  {LongLiteral}					{	return MakeToken(symID.LONG_LITERAL, yytext()); }
  {ULongLiteral}				{	return MakeToken(symID.ULONG_LITERAL, yytext());}
  {DoubleLiteral}				{	return MakeToken(symID.DOUBLE_LITERAL, yytext());}
  {FloatLiteral}				{	return MakeToken(symID.FLOAT_LITERAL, yytext());}
  {DecimalLiteral}				{	return MakeToken(symID.DECIMAL_LITERAL, yytext());}
  ^{PreprocessorDirectiveStart}	{	yybegin(YYINITIAL_PP);
  									stringBuf.Length = 0;
  									stringBuf.Append( yytext() );
								}
  @ {Identifier}				{	return MakeToken(symID.IDENTIFIER, yytext().Substring(1));	}
  {Identifier}					{	return MakeToken(symID.IDENTIFIER, yytext());	}
}

<YYINITIAL_PP> {
  "define"						{	yybegin(PP_SYMBOLS); return MakeToken(symID.PP_DEFINE, stringBuf.ToString()+yytext()); 		}
  "undef"						{	yybegin(PP_SYMBOLS); return MakeToken(symID.PP_UNDEFINE, stringBuf.ToString()+yytext()); 	}
  "if"							{	yybegin(PP_SYMBOLS); return MakeToken(symID.PP_COND_IF, stringBuf.ToString()+yytext()); 		}
  "elif"						{	yybegin(PP_SYMBOLS); return MakeToken(symID.PP_COND_ELIF, stringBuf.ToString()+yytext()); 	}
  "else"						{	yybegin(PP_SYMBOLS); return MakeToken(symID.PP_COND_ELSE, stringBuf.ToString()+yytext()); 	}
  "endif"						{	yybegin(PP_SYMBOLS); return MakeToken(symID.PP_COND_ENDIF, stringBuf.ToString()+yytext()); 	}
  "line"						{	yybegin(PP_SYMBOLS); return MakeToken(symID.PP_UNDEFINE, stringBuf.ToString()+yytext()); 	}

  "error"						{	yybegin(PP_HOOVER); return MakeToken(symID.PP_ERROR, stringBuf.ToString()+yytext()); 	}
  "warning"						{	yybegin(PP_HOOVER); return MakeToken(symID.PP_WARNING, stringBuf.ToString()+yytext()); 	}
  "region"						{	yybegin(PP_HOOVER); return MakeToken(symID.PP_REGION, stringBuf.ToString()+yytext()); 	}
  "endregion"					{	yybegin(PP_HOOVER); return MakeToken(symID.PP_ENDREGION, stringBuf.ToString()+yytext()); }

  /* error cases */
  .|{LineTerminator}             { 	throw new SemanticException("CS1024: Preprocessor directive expected", fileinfo.FullName, yyline, yycolumn); }
}

<YYINITIAL_CHAR> {

  {CharLiteralCharacter}		{	yybegin(YYINITIAL); return MakeToken(symID.CHAR_LITERAL, yytext()); }
  {HexadecimalEscapeSequence} \' {	yybegin(YYINITIAL); return MakeToken(symID.CHAR_LITERAL, Int32.Parse(yytext().Substring(2), NumberStyles.HexNumber).ToString()); }
  {FourCharUnicodeEscape} \'	{	yybegin(YYINITIAL); return MakeToken(symID.CHAR_LITERAL, Int32.Parse(yytext().Substring(2), NumberStyles.HexNumber).ToString()); }
  \\ U [0]{0,4} {HexDigit}{1,4}	\' { yybegin(YYINITIAL); return MakeToken(symID.CHAR_LITERAL, Int32.Parse(yytext().Substring(2), NumberStyles.HexNumber).ToString()); }
  \\ \' \'						{	yybegin(YYINITIAL); return MakeToken(symID.CHAR_LITERAL, "'");  	}
  \\ \" \'						{	yybegin(YYINITIAL); return MakeToken(symID.CHAR_LITERAL, "\"");  	}
  \\ \\ \'						{	yybegin(YYINITIAL); return MakeToken(symID.CHAR_LITERAL, "\\");  	}
  \\ 0  \'						{	yybegin(YYINITIAL); return MakeToken(symID.CHAR_LITERAL, "\0");  	}
  \\ a  \'						{	yybegin(YYINITIAL); return MakeToken(symID.CHAR_LITERAL, "\a");  	}
  \\ v  \'						{	yybegin(YYINITIAL); return MakeToken(symID.CHAR_LITERAL, "\v");  	}
  \\ b  \'						{	yybegin(YYINITIAL); return MakeToken(symID.CHAR_LITERAL, "\b");  	}
  \\ f  \'						{	yybegin(YYINITIAL); return MakeToken(symID.CHAR_LITERAL, "\f");  	}
  \\ n  \'						{	yybegin(YYINITIAL); return MakeToken(symID.CHAR_LITERAL, "\n");  	}
  \\ r  \'						{	yybegin(YYINITIAL); return MakeToken(symID.CHAR_LITERAL, "\r");  	}
  \\ t  \'						{	yybegin(YYINITIAL); return MakeToken(symID.CHAR_LITERAL, "\t");  	}

  /* error cases */
  \\.                            {	throw new SemanticException("CS1009: Unrecognized escape sequence '" +yytext()+ "'", fileinfo.FullName, yyline, yycolumn); }
  .|{LineTerminator}             { 	throw new SemanticException("CS1003: Syntax error, 'char' expected", fileinfo.FullName, yyline, yycolumn); }
}

<YYINITIAL_VERBATIM> {
  \" \"							{	stringBuf.Append( "\"" ); 		}
  [^\"]+						{	stringBuf.Append( yytext() ); 	}
  \"							{	yybegin(YYINITIAL); 
  									return MakeToken(symID.STRING_LITERAL, stringBuf.ToString());
  								}
}

<YYINITIAL_STRING> {
  \"							{	yybegin(YYINITIAL); 
  									return MakeToken(symID.STRING_LITERAL, stringBuf.ToString());
  								}
  {HexadecimalEscapeSequence}	{	stringBuf.Append(Int32.Parse(yytext().Substring(2), NumberStyles.HexNumber).ToString()); }
  {FourCharUnicodeEscape}		{	stringBuf.Append(Int32.Parse(yytext().Substring(2), NumberStyles.HexNumber).ToString()); }
  \\ U [0]{0,4} {HexDigit}{1,4}	{	stringBuf.Append(Int32.Parse(yytext().Substring(2), NumberStyles.HexNumber).ToString()); }
  \\ \'							{	stringBuf.Append( "'" );  	}
  \\ \"							{	stringBuf.Append( "\"" );  	}
  \\ \\							{	stringBuf.Append( "\\" );  	}
  \\ 0 							{	stringBuf.Append( "\0" );  	}
  \\ a 							{	stringBuf.Append( "\a" );  	}
  \\ v 							{	stringBuf.Append( "\v" );  	}
  \\ b 							{	stringBuf.Append( "\b" );  	}
  \\ f 							{	stringBuf.Append( "\f" );  	}
  \\ n 							{	stringBuf.Append( "\n" );  	}
  \\ r 							{	stringBuf.Append( "\r" );  	}
  \\ t 							{	stringBuf.Append( "\t" );  	}
  {StringLiteralCharacter}+		{	stringBuf.Append(yytext()); }

  /* error cases */
  \\.                            {	throw new SemanticException("CS1009: Unrecognized escape sequence '" +yytext()+ "'", fileinfo.FullName, yyline, yycolumn); }
  .|{LineTerminator}             { 	throw new SemanticException("CS1039: Unterminated string literal", fileinfo.FullName, yyline, yycolumn); }
}

<PP_SYMBOLS> {

  {LineTerminator}				{	yybegin(YYINITIAL);
  									return MakeToken(symID.NEWLINE, yytext());
  								}
  {NonNewLineWhiteSpace}+		{	return MakeToken(symID.WHITESPACE, yytext());	}
  {Number}						{	return MakeToken(symID.PP_NUMBER, yytext());	}
  \" [^\"\r\n\u2028\u2029]* \"	{	return MakeToken(symID.PP_FILENAME, yytext());	}
  {SingleLineComment}			{	return MakeToken(symID.SL_COMMENT, yytext());	}
  
  /* C# operators */
  "("		                  	{ 	return MakeToken(symID.OPEN_PAREN, "(");		}
  ")"		                  	{ 	return MakeToken(symID.CLOSE_PAREN, ")");		}
  "!"		                  	{ 	return MakeToken(symID.LOG_NOT, "!");			}
  "&&"		                  	{ 	return MakeToken(symID.LOG_AND, "&&");			}
  "||"		                  	{ 	return MakeToken(symID.LOG_OR, "||");			}
  "=="		                  	{ 	return MakeToken(symID.EQUAL, "==");			}
  "!="		                  	{ 	return MakeToken(symID.NOT_EQUAL, "!=");		}
  {Identifier}					{	return MakeToken(symID.PP_IDENT, yytext());	}

  /* error handling*/
  .								{	throw new SemanticException("PP_SYMBOLS: Unexpected input '"+yytext()+"'", fileinfo.FullName, yyline, yycolumn); }
}

<PP_HOOVER> {

  {LineTerminator}				{	yybegin(YYINITIAL);
  									return MakeToken(symID.NEWLINE, yytext());
  								}
  {SingleLineComment}			{	yybegin(YYINITIAL);
  									return MakeToken(symID.SL_COMMENT, yytext());
  								}
  {NonNewLineWhiteSpace}+ {InputCharacter}*
  								{	return MakeToken(symID.PP_STRING, yytext());					}

  /* error handling*/
  .								{	throw new SemanticException("PP_HOOVER: Unexpected input '"+yytext()+"'", fileinfo.FullName, yyline, yycolumn); 	}
}


/* error fallback */
.|{LineTerminator}				{	throw new SemanticException("Illegal character '"+yytext()+"' at line "+yyline+", column "+yycolumn, fileinfo.FullName, yyline, yycolumn); 	}


%%

} // End of namespace declaration
