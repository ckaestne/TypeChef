/*
 *  Boa, an http server
 *  Copyright (C) 1995 Paul Phillips <paulp@go2net.com>
 *  Some changes Copyright (C) 1996 Larry Doolittle <ldoolitt@boa.org>
 *  Some changes Copyright (C) 1999 Jon Nelson <jnelson@boa.org>
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

/* $Id: log.c,v 1.36.2.3 2002/07/26 03:04:48 jnelson Exp $*/

#include "boa.h"

FILE *access_log;

char *error_log_name;
char *access_log_name;
char *cgi_log_name;
int cgi_log_fd;

FILE *fopen_gen_fd(char *spec, const char *mode);

FILE *fopen_gen_fd(char *spec, const char *mode)
{
    int fd;
    if (!spec || *spec == '\0')
        return NULL;
    fd = open_gen_fd(spec);
    if (fd == -1)
        return NULL;
    return fdopen(fd, mode);
}

/*
 * Name: open_logs
 *
 * Description: Opens access log, error log, and if specified, cgi log
 * Ties stderr to error log, except during cgi execution, at which
 * time cgi log is the stderr for cgis.
 *
 * Access log is line buffered, error log is not buffered.
 *
 */

void open_logs(void)
{
    int error_log;

    /* if error_log_name is set, dup2 stderr to it */
    /* otherwise, leave stderr alone */
    /* we don't want to tie stderr to /dev/null */
    if (error_log_name) {
        /* open the log file */
        if (!(error_log = open_gen_fd(error_log_name))) {
            DIE("unable to open error log");
        }

        /* redirect stderr to error_log */
        if (dup2(error_log, STDERR_FILENO) == -1) {
            DIE("unable to dup2 the error log");
        }
        close(error_log);
    }

    /* set the close-on-exec to true */
    if (fcntl(STDERR_FILENO, F_SETFD, 1) == -1) {
        DIE("unable to fcntl the error log");
    }

    if (access_log_name) {
        /* Used the "a" flag with fopen, but fopen_gen_fd builds that in
         * implicitly when used as a file, and "a" is incompatible with
         * pipes and network sockets. */
        if (!(access_log = fopen_gen_fd(access_log_name, "w"))) {
            int errno_save = errno;
            fprintf(stderr, "Cannot open %s for logging: ",
                    access_log_name);
            errno = errno_save;
            perror("logfile open");
            exit(errno);
        }
        /* line buffer the access log */
#ifdef SETVBUF_REVERSED
        setvbuf(access_log, _IOLBF, (char *) NULL, 0);
#else
        setvbuf(access_log, (char *) NULL, _IOLBF, 0);
#endif
    } else
        access_log = NULL;

    if (cgi_log_name) {
        cgi_log_fd = open_gen_fd(cgi_log_name);
        if (cgi_log_fd == -1) {
            WARN("open cgi_log");
            free(cgi_log_name);
            cgi_log_name = NULL;
            cgi_log_fd = 0;
        } else {
            if (fcntl(cgi_log_fd, F_SETFD, 1) == -1) {
                WARN("unable to set close-on-exec flag for cgi_log");
                close(cgi_log_fd);
                cgi_log_fd = 0;
                free(cgi_log_name);
                cgi_log_name = NULL;
            }
        }
    }
}

/*
 * Name: close_access_log
 *
 * Description: closes access_log file
 */
void close_access_log(void)
{
    if (access_log)
        fclose(access_log);
}

/*
 * Name: log_access
 *
 * Description: Writes log data to access_log.
 */

void log_access(request * req)
{
    if (access_log) {
        if (virtualhost)
            fprintf(access_log, "%s ", req->local_ip_addr);
        fprintf(access_log, "%s - - %s\"%s\" %d %ld \"%s\" \"%s\"\n",
                req->remote_ip_addr,
                get_commonlog_time(),
                req->logline,
                req->response_status,
                req->filepos,
                (req->header_referer ? req->header_referer : "-"),
                (req->header_user_agent ? req->header_user_agent : "-"));

    }
}

/*
 * Name: log_error_doc
 *
 * Description: Logs the current time and transaction identification
 * to the stderr (the error log):
 * should always be followed by an fprintf to stderr
 *
 * This function used to be implemented with a big fprintf, but not
 * all fprintf's are reliable in the face of null string pointers
 * (SunOS, in particular).  As long as I had to add the checks for
 * null pointers, I changed from fprintf to fputs.
 *
 * Example output:
 [08/Nov/1997:01:05:03 -0600] request from 192.228.331.232 "GET /~joeblow/dir/ HTTP/1.0" ("/usr/user1/joeblow/public_html/dir/"): write: Broken pipe
 */

void log_error_doc(request * req)
{
    int errno_save = errno;

    fprintf(stderr, "%srequest from %s \"%s\" (\"%s\"): ",
            get_commonlog_time(),
            req->remote_ip_addr,
            (req->logline != NULL ?
             req->logline : "(null)"),
            (req->pathname != NULL ? req->pathname : "(null)"));

    errno = errno_save;
}

/*
 * Name: boa_perror
 *
 * Description: logs an error to user and error file both
 *
 */
void boa_perror(request * req, char *message)
{
    log_error_doc(req);
    perror(message);            /* don't need to save errno because log_error_doc does */
    send_r_error(req);
}

/*
 * Name: log_error_time
 *
 * Description: Logs the current time to the stderr (the error log):
 * should always be followed by an fprintf to stderr
 */

void log_error_time()
{
    int errno_save = errno;
    fputs(get_commonlog_time(), stderr);
    errno = errno_save;
}

/*
 * Name: log_error_mesg
 *
 * Description: performs a log_error_time, writes the file and lineno
 * to stderr (saving errno), and then a perror with message
 *
 */

void log_error_mesg(char *file, int line, char *mesg)
{
    int errno_save = errno;
    fprintf(stderr, "%s%s:%d - ", get_commonlog_time(), file, line);
    errno = errno_save;
    perror(mesg);
    errno = errno_save;
}
