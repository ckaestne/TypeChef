%{

/*
 *  Boa, an http server
 *  Copyright (C) 1995 Paul Phillips <psp@well.com>
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 1, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

/* $Id: boa_lexer.l,v 1.13.2.1 2002/07/07 23:19:55 jnelson Exp $*/

#include "y.tab.h"
#include <stdlib.h>
#include <unistd.h>
#include "parse.h"

#define MAX_STR_CONST 1024
#define qspush(c) \
	if (string_buf_ptr < string_buf+MAX_STR_CONST) \
		*string_buf_ptr++ = c; \
	else \
		yyerror("quoted string overflow");

char *mime_types = NULL;

static int file = 0;
int lineno = 1;
struct ccommand *k; 
char string_buf[MAX_STR_CONST];
char *string_buf_ptr;
%}

%s MIME
/* Quoted string handling (almost) straight out of
   the flex 2.5 man page, April 1995 */
%x STR

%%

[ \t]+		;
#.*		;

<MIME>[^ \t\n]+\/[^ \t\n]+	{ yylval.sval = yytext; return MIMETYPE; }

[^ \"\t\n]+	{ /* XXX could use better checks that we are in a state to
		   * accept keywords; this version matches original behavior */
		  if ((YYSTATE==INITIAL) && (k=lookup_keyword(yytext))) {
		      yylval.cval=k;
		      return (k->type);
		  } else { yylval.sval = yytext; return STRING; }
                }

\"	{
	string_buf_ptr = string_buf;
	BEGIN(STR);
	}

<STR>{
\"	{ /* saw closing quote - all done */
	BEGIN(INITIAL);
	*string_buf_ptr = '\0';
	/* return string constant token type and value to parser */
	yylval.sval = string_buf; return STRING;
	}

\n	{
	/* error - unterminated string constant */
	/* generate error message */
	yyerror("unterminated string constant");
	}

\\[0-7]{1,3}  {
	/* octal escape sequence */
	int result;

	(void) sscanf( yytext + 1, "%o", &result );

	if ( result > 0xff )
		{ /* error, constant is out-of-bounds */ }

	qspush(result);
	}

\\[0-9]+  {
	/* generate error - bad escape sequence; something
	 * like '\48' or '\0777777'
	 */
	yyerror("bad escape sequence");
	}

\\n	qspush('\n');
\\t	qspush('\t');
\\r	qspush('\r');
\\b	qspush('\b');
\\f	qspush('\f');

\\(.|\n)  *string_buf_ptr++ = yytext[1];

[^\\\n\"]+  {
	char *yptr = yytext;
	while ( *yptr )
		qspush(*yptr++);
	}
}
	/* End of <STR> dependence */
\n		{ lineno++; }
%%

/* In yywrap we track which file we are on.
 * 1: close boa.conf, open mime.types
 * 2: return 1;
 */

int yywrap()
{
    fclose(yyin);
    file++; 

    switch(file) {
      case 1:
	yyin = fopen(mime_types, "r");
	if(!yyin) {
	    fprintf(stderr, "Could not open mime.types file, \"%s\", "
	    	"for reading\n", mime_types);
	    exit(1);
	}
	BEGIN MIME;
	return 0;
      default:
	BEGIN INITIAL;
	file = 0;		/* in case we reread config files */
	return 1;
    }
}

int yyerror(char * msg)
{
    fprintf(stderr, "Error on line %d of %s: %s\n", lineno, 
      (file == 0 ? "boa.conf" : "mime.types"), msg);
    return 1;
}

