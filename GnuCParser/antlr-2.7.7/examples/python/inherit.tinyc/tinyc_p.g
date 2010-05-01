// This file is part of PyANTLR. See LICENSE.txt for license
// details..........Copyright (C) Wolfgang Haefelinger, 2004.
//
// $Id$

/*
 * Make sure to run antlr.Tool on the lexer.g file first!
 */
options {
	mangleLiteralPrefix = "TK_";
    language=Python;
}

{
def main():
   import tinyc_l
   import tinyc_p
   
   L = tinyc_l.Lexer() 
   P = tinyc_p.Parser(L)
   P.setFilename(L.getFilename())

   ### Parse the input expression
   try:
      P.program()
   except antlr.ANTLRException, ex:
      print "*** error(s) while parsing."
      print ">>> exit(1)"
      import sys
      sys.exit(1)

   
   ast = P.getAST()
   
   if not ast:
      print "stop - no AST generated."
      return
      
   ###show tree
   print "Tree: " + ast.toStringTree()
   print "List: " + ast.toStringList()
   print "Node: " + ast.toString()
   print "visit>>"
   visitor = Visitor()
   visitor.visit(ast);
   print "visit<<"


if __name__ == "__main__":
   main()

}

class tinyc_p extends Parser;
options {
	importVocab=TinyC;
}

program
	:	( declaration )* EOF
	;

declaration
	:	(variable) => variable
	|	function
	;

declarator
	:	id:ID
	|	STAR id2:ID
	;

variable
	:	type declarator SEMI
	;

function
	:	type id:ID LPAREN
		(formalParameter (COMMA formalParameter)*)?
		RPAREN
		block
	;

formalParameter
	:	type declarator
	;

type:	
	(
		TK_int
	|	TK_char
	|	id:ID
	)
	;

block
	:	LCURLY ( statement )* RCURLY
	;

statement
	:	(declaration) => declaration
	|	expr SEMI
	|	TK_if LPAREN expr RPAREN statement
		( TK_else statement )?
	|	TK_while LPAREN expr RPAREN statement
	|	block
	;

expr:	assignExpr
	;

assignExpr
	:	aexpr (ASSIGN assignExpr)?
	;

aexpr
	:	mexpr (PLUS mexpr)*
	;

mexpr
	:	atom (STAR atom)*
	;

atom:	ID
	|	INT
	|	CHAR_LITERAL
	|	STRING_LITERAL
	;

