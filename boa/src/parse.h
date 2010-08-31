/*
 *  Boa, an http server
 *  Copyright (C) 1999 Larry Doolittle <ldoolitt@boa.org>
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
 *  parse.h
 *  minimum interaction point between Boa's parser (boa_lexer.l and
 *  boa_grammar.y) and the rest of Boa.
 */

/* $Id: parse.h,v 1.5 2000/02/12 21:52:45 jon Exp $*/

struct ccommand {
    char *name;
    int type;
    void (*action) (char *, char *, void *);
    void *object;
};
struct ccommand *lookup_keyword(char *c);
void add_mime_type(char *extension, char *type);
