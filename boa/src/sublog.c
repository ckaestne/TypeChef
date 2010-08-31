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
 */

/* $Id: sublog.c,v 1.6 2002/03/24 22:40:31 jnelson Exp $*/

#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <netdb.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

int open_pipe_fd(char *command);
int open_net_fd(char *spec);
int open_gen_fd(char *spec);

/* Like popen, but gives fd instead of FILE * */
int open_pipe_fd(char *command)
{
    int pipe_fds[2];
    int pid;
    /* "man pipe" says "filedes[0] is for reading,
     * filedes[1] is for writing. */
    if (pipe(pipe_fds) == -1)
        return -1;
    pid = fork();
    if (pid == 0) {             /* child */
        close(pipe_fds[1]);
        if (pipe_fds[0] != 0) {
            dup2(pipe_fds[0], 0);
            close(pipe_fds[0]);
        }
        execl("/bin/sh", "sh", "-c", command, (char *) 0);
        exit(127);
    }
    close(pipe_fds[0]);
    if (pid < 0) {
        close(pipe_fds[1]);
        return -1;
    }
    return pipe_fds[1];
}

int open_net_fd(char *spec)
{
    char *p;
    int fd, port;
    struct sockaddr_in sa;
    struct hostent *he;
    p = strchr(spec, ':');
    if (!p)
        return -1;
    *p++ = '\0';
    port = strtol(p, NULL, 10);
    /* printf("Host %s, port %d\n",spec,port); */
    sa.sin_family = AF_INET;
    sa.sin_port = htons(port);
    he = gethostbyname(spec);
    if (!he) {
        herror("open_net_fd");
        return -1;
    }
    memcpy(&sa.sin_addr, he->h_addr, he->h_length);
    /* printf("using ip %s\n",inet_ntoa(sa.sin_addr)); */
    fd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (fd < 0)
        return fd;
    if (connect(fd, (struct sockaddr *) &sa, sizeof (sa)) < 0)
        return -1;
    return fd;
}

int open_gen_fd(char *spec)
{
    int fd;
    if (*spec == '|') {
        fd = open_pipe_fd(spec + 1);
    } else if (*spec == ':') {
        fd = open_net_fd(spec + 1);
    } else {
        fd = open(spec,
                  O_WRONLY | O_CREAT | O_APPEND,
                  S_IRUSR | S_IWUSR | S_IROTH | S_IRGRP);
    }
    return fd;
}

#ifdef STANDALONE_TEST
int main(int argc, char *argv[])
{
    char buff[1024];
    int fd, nr, nw;
    if (argc < 2) {
        fprintf(stderr,
                "usage: %s output-filename\n"
                "       %s |output-command\n"
                "       %s :host:port\n", argv[0], argv[0], argv[0]);
        return 1;
    }
    fd = open_gen_fd(argv[1]);
    if (fd < 0) {
        perror("open_gen_fd");
        exit(1);
    }
    while ((nr = read(0, buff, sizeof (buff))) != 0) {
        if (nr < 0) {
            if (errno == EINTR)
                continue;
            perror("read");
            exit(1);
        }
        nw = write(fd, buff, nr);
        if (nw < 0) {
            perror("write");
            exit(1);
        }
    }
    return 0;
}
#endif
