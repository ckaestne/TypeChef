/*
 *  Boa, an http server
 *  Copyright (C) 1995 Paul Phillips <paulp@go2net.com>
 *  Some changes Copyright (C) 1996,97 Larry Doolittle <ldoolitt@boa.org>
 *  Some changes Copyright (C) 1997,99 Jon Nelson <jnelson@boa.org>
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

/* $Id: read.c,v 1.49 2002/03/18 01:53:48 jnelson Exp $*/

#include "boa.h"

/*
 * Name: read_header
 * Description: Reads data from a request socket.  Manages the current
 * status via a state machine.  Changes status from READ_HEADER to
 * READ_BODY or WRITE as necessary.
 *
 * Return values:
 *  -1: request blocked, move to blocked queue
 *   0: request done, close it down
 *   1: more to do, leave on ready list
 */

int read_header(request * req)
{
    int bytes, buf_bytes_left;
    char *check, *buffer;

    check = req->client_stream + req->parse_pos;
    buffer = req->client_stream;
    bytes = req->client_stream_pos;

#ifdef VERY_FASCIST_LOGGING
    if (check < (buffer + bytes)) {
        buffer[bytes] = '\0';
        log_error_time();
        fprintf(stderr, "%s:%d - Parsing headers (\"%s\")\n",
                __FILE__, __LINE__, check);
    }
#endif
    while (check < (buffer + bytes)) {
        switch (req->status) {
        case READ_HEADER:
            if (*check == '\r') {
                req->status = ONE_CR;
                req->header_end = check;
            } else if (*check == '\n') {
                req->status = ONE_LF;
                req->header_end = check;
            }
            break;

        case ONE_CR:
            if (*check == '\n')
                req->status = ONE_LF;
            else if (*check != '\r')
                req->status = READ_HEADER;
            break;

        case ONE_LF:
            /* if here, we've found the end (for sure) of a header */
            if (*check == '\r') /* could be end o headers */
                req->status = TWO_CR;
            else if (*check == '\n')
                req->status = BODY_READ;
            else
                req->status = READ_HEADER;
            break;

        case TWO_CR:
            if (*check == '\n')
                req->status = BODY_READ;
            else if (*check != '\r')
                req->status = READ_HEADER;
            break;

        default:
            break;
        }

#ifdef VERY_FASCIST_LOGGING
        log_error_time();
        fprintf(stderr, "status, check: %d, %d\n", req->status, *check);
#endif

        req->parse_pos++;       /* update parse position */
        check++;

        if (req->status == ONE_LF) {
            *req->header_end = '\0';

            /* terminate string that begins at req->header_line */

            if (req->logline) {
                if (process_option_line(req) == 0) {
                    return 0;
                }
            } else {
                if (process_logline(req) == 0)
                    return 0;
                if (req->simple)
                    return process_header_end(req);
            }
            /* set header_line to point to beginning of new header */
            req->header_line = check;
        } else if (req->status == BODY_READ) {
#ifdef VERY_FASCIST_LOGGING
            int retval;
            log_error_time();
            fprintf(stderr, "%s:%d -- got to body read.\n",
                    __FILE__, __LINE__);
            retval = process_header_end(req);
#else
            int retval = process_header_end(req);
#endif
            /* process_header_end inits non-POST cgi's */

            if (retval && req->method == M_POST) {
                /* for body_{read,write}, set header_line to start of data,
                   and header_end to end of data */
                req->header_line = check;
                req->header_end =
                    req->client_stream + req->client_stream_pos;

                req->status = BODY_WRITE;
                /* so write it */
                /* have to write first, or read will be confused
                 * because of the special case where the
                 * filesize is less than we have already read.
                 */

                /*

                   As quoted from RFC1945:

                   A valid Content-Length is required on all HTTP/1.0 POST requests. An
                   HTTP/1.0 server should respond with a 400 (bad request) message if it
                   cannot determine the length of the request message's content.

                 */

                if (req->content_length) {
                    int content_length;

                    content_length = boa_atoi(req->content_length);
                    /* Is a content-length of 0 legal? */
                    if (content_length <= 0) {
                        log_error_time();
                        fprintf(stderr, "Invalid Content-Length [%s] on POST!\n",
                                req->content_length);
                        send_r_bad_request(req);
                        return 0;
                    }
                    if (single_post_limit && content_length > single_post_limit) {
                        log_error_time();
                        fprintf(stderr, "Content-Length [%d] > SinglePostLimit [%d] on POST!\n",
                                content_length, single_post_limit);
                        send_r_bad_request(req);
                        return 0;
                    }
                    req->filesize = content_length;
                    req->filepos = 0;
                    if (req->header_end - req->header_line > req->filesize) {
                        req->header_end = req->header_line + req->filesize;
                    }
                } else {
                    log_error_time();
                    fprintf(stderr, "Unknown Content-Length POST!\n");
                    send_r_bad_request(req);
                    return 0;
                }
            }                   /* either process_header_end failed or req->method != POST */
            return retval;      /* 0 - close it done, 1 - keep on ready */
        }                       /* req->status == BODY_READ */
    }                           /* done processing available buffer */

#ifdef VERY_FASCIST_LOGGING
    log_error_time();
    fprintf(stderr, "%s:%d - Done processing buffer.  Status: %d\n",
            __FILE__, __LINE__, req->status);
#endif

    if (req->status < BODY_READ) {
        /* only reached if request is split across more than one packet */

        buf_bytes_left = CLIENT_STREAM_SIZE - req->client_stream_pos;
        if (buf_bytes_left < 1) {
            log_error_time();
            fputs("buffer overrun - read.c, read_header - closing\n",
                  stderr);
            req->status = DEAD;
            return 0;
        }

        bytes = read(req->fd, buffer + req->client_stream_pos, buf_bytes_left);

        if (bytes < 0) {
            if (errno == EINTR)
                return 1;
            if (errno == EAGAIN || errno == EWOULDBLOCK) /* request blocked */
                return -1;
            /*
               else if (errno == EBADF || errno == EPIPE) {

               req->status = DEAD;
               return 0;
               */
            log_error_doc(req);
            perror("header read");            /* don't need to save errno because log_error_doc does */
            return 0;
        } else if (bytes == 0) {
            /*
               log_error_time();
               fputs("unexpected end of headers\n", stderr);
             */
            return 0;
        }

        /* bytes is positive */
        req->client_stream_pos += bytes;

#ifdef FASCIST_LOGGING1
        log_error_time();
        req->client_stream[req->client_stream_pos] = '\0';
        fprintf(stderr, "%s:%d -- We read %d bytes: \"%s\"\n",
                __FILE__, __LINE__, bytes,
#ifdef VERY_FASCIST_LOGGING2
                req->client_stream + req->client_stream_pos - bytes);
#else
                "");
#endif
#endif

        return 1;
    }
    return 1;
}

/*
 * Name: read_body
 * Description: Reads body from a request socket for POST CGI
 *
 * Return values:
 *
 *  -1: request blocked, move to blocked queue
 *   0: request done, close it down
 *   1: more to do, leave on ready list
 *

 As quoted from RFC1945:

 A valid Content-Length is required on all HTTP/1.0 POST requests. An
 HTTP/1.0 server should respond with a 400 (bad request) message if it
 cannot determine the length of the request message's content.

 */

int read_body(request * req)
{
    int bytes_read, bytes_to_read, bytes_free;

    bytes_free = BUFFER_SIZE - (req->header_end - req->header_line);
    bytes_to_read = req->filesize - req->filepos;

    if (bytes_to_read > bytes_free)
        bytes_to_read = bytes_free;

    if (bytes_to_read <= 0) {
        req->status = BODY_WRITE; /* go write it */
        return 1;
    }

    bytes_read = read(req->fd, req->header_end, bytes_to_read);

    if (bytes_read == -1) {
        if (errno == EWOULDBLOCK || errno == EAGAIN) {
            /*
               req->status = BODY_WRITE;
               return 1;
             */
            return -1;
        } else {
            boa_perror(req, "read body");
            return 0;
        }
    } else if (bytes_read == 0) {
        /* this is an error.  premature end of body! */
        log_error_time();
        fprintf(stderr, "%s:%d - Premature end of body!!\n",
                __FILE__, __LINE__);
        send_r_bad_request(req);
        return 0;
    }

    req->status = BODY_WRITE;

#ifdef FASCIST_LOGGING1
    log_error_time();
    fprintf(stderr, "%s:%d - read %d bytes.\n",
            __FILE__, __LINE__, bytes_to_read);
#endif

    req->header_end += bytes_read;

    return 1;
}

/*
 * Name: write_body
 * Description: Writes a chunk of data to a file
 *
 * Return values:
 *  -1: request blocked, move to blocked queue
 *   0: EOF or error, close it down
 *   1: successful write, recycle in ready queue
 */

int write_body(request * req)
{
    int bytes_written, bytes_to_write = req->header_end - req->header_line;
    if (req->filepos + bytes_to_write > req->filesize)
        bytes_to_write = req->filesize - req->filepos;

    if (bytes_to_write == 0) {  /* nothing left in buffer to write */
        req->header_line = req->header_end = req->buffer;
        if (req->filepos >= req->filesize)
            return init_cgi(req);
        /* if here, we can safely assume that there is more to read */
        req->status = BODY_READ;
        return 1;
    }
    bytes_written = write(req->post_data_fd,
                          req->header_line, bytes_to_write);

    if (bytes_written == -1) {
        if (errno == EWOULDBLOCK || errno == EAGAIN)
            return -1;          /* request blocked at the pipe level, but keep going */
        else if (errno == EINTR)
            return 1;
        else if (errno == ENOSPC) {
            /* 20010520 - Alfred Fluckiger */
            /* No test was originally done in this case, which might  */
            /* lead to a "no space left on device" error.             */
            boa_perror(req, "write body"); /* OK to disable if your logs get too big */
            return 0;
        } else {
            boa_perror(req, "write body"); /* OK to disable if your logs get too big */
            return 0;
        }
    }
#ifdef FASCIST_LOGGING
    log_error_time();
    fprintf(stderr, "%s:%d - wrote %d bytes. %ld of %ld\n",
            __FILE__, __LINE__,
            bytes_written, req->filepos, req->filesize);
#endif

    req->filepos += bytes_written;
    req->header_line += bytes_written;

    return 1;                   /* more to do */
}
