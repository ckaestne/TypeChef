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
 *

  Encapsulation of ipv4 and ipv6 stuff, try to get rid of the ifdef's
  elsewhere in the code.

  The IPv6 code here is bsed on contributions from Martin Hinner <martin@tdp.cz>
  and Arkadiusz Miskiewicz <misiek@misiek.eu.org>.  This incarnation of that
  code is untested.  The original IPv4 code is based on original Boa code
  from Paul Phillips <paulp@go2net.com>.

  A goal is to compile in as many families as are supported, and
  make the final choice at runtime.

globals.h:
#ifdef INET6
	char remote_ip_addr[NI_MAXHOST];
#else
	char remote_ip_addr[20];        after inet_ntoa
#endif

    None of this code interacts with the rest of Boa except through
    the parameter lists and return values.

    Consider making these functions __inline__ and using this as a .h file
    */

#include "boa.h"
#include <arpa/inet.h>          /* inet_ntoa */

/* Binds to the existing server_s, based on the configuration string
   in server_ip.  IPv6 version doesn't pay attention to server_ip yet.  */
int bind_server(int server_s, char *server_ip)
{
#ifdef INET6
    struct sockaddr_in6 server_sockaddr;
    server_sockaddr.sin6_family = AF_INET6;
    memcpy(&server_sockaddr.sin6_addr, &in6addr_any, sizeof (in6addr_any));
    server_sockaddr.sin6_port = htons(server_port);
#else
    struct sockaddr_in server_sockaddr;
    memset(&server_sockaddr, 0, sizeof server_sockaddr);
#ifdef HAVE_SIN_LEN             /* uncomment for BSDs */
    server_sockaddr.sin_len = sizeof server_sockaddr;
#endif
    server_sockaddr.sin_family = AF_INET;
    if (server_ip != NULL) {
        inet_aton(server_ip, &server_sockaddr.sin_addr);
    } else {
        server_sockaddr.sin_addr.s_addr = htonl(INADDR_ANY);
    }
    server_sockaddr.sin_port = htons(server_port);
#endif

    return bind(server_s, (struct sockaddr *) &server_sockaddr,
                sizeof (server_sockaddr));
}

char *ascii_sockaddr(struct SOCKADDR *s, char *dest, int len)
{
#ifdef INET6
    if (getnameinfo((struct sockaddr *) s,
                    sizeof(struct SOCKADDR),
                    dest, len, NULL, 0, NI_NUMERICHOST)) {
        fprintf(stderr, "[IPv6] getnameinfo failed\n");
        *dest = '\0';
    }
#ifdef WHEN_DOES_THIS_APPLY
    if ((s->__ss_family == AF_INET6) &&
        IN6_IS_ADDR_V4MAPPED(&(((struct sockaddr_in6 *) s)->sin6_addr))) {
        memmove(dest, dest+7, NI_MAXHOST);
    }
#endif
#else
    memmove(dest, inet_ntoa(s->sin_addr), len);
#endif
    return dest;
}

int net_port(struct SOCKADDR *s)
{
    int p = -1;
#ifdef INET6
    char serv[NI_MAXSERV];

    if (getnameinfo((struct sockaddr *) s,
                    sizeof(struct SOCKADDR),
                    NULL, 0, serv, sizeof(serv), NI_NUMERICSERV)) {
        fprintf(stderr, "[IPv6] getnameinfo failed\n");
    } else {
        p = atoi(serv);
    }
#else
    p = ntohs(s->sin_port);
#endif
    return p;
}
