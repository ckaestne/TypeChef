/*
 *  Boa, an http server
 *  Copyright (C) 1995 Paul Phillips <paulp@go2net.com>
 *  Some changes Copyright (C) 1996 Charles F. Randall <crandall@goldsys.com>
 *  Some changes Copyright (C) 1996 Larry Doolittle <ldoolitt@boa.org>
 *  Some changes Copyright (C) 1996-2002 Jon Nelson <jnelson@boa.org>
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

/* $Id: select.c,v 1.1.2.2 2002/07/23 15:54:52 jnelson Exp $*/

#include "boa.h"

static void fdset_update(void);
fd_set block_read_fdset;
fd_set block_write_fdset;
static struct timeval req_timeout;     /* timeval for select */

void select_loop(int server_s)
{
    FD_ZERO(&block_read_fdset);
    FD_ZERO(&block_write_fdset);
    /* set server_s and req_timeout */
    req_timeout.tv_sec = (ka_timeout ? ka_timeout : REQUEST_TIMEOUT);
    req_timeout.tv_usec = 0l;   /* reset timeout */

    /* preset max_fd */
    max_fd = -1;

    while (1) {
        if (sighup_flag)
            sighup_run();
        if (sigchld_flag)
            sigchld_run();
        if (sigalrm_flag)
            sigalrm_run();

        if (sigterm_flag) {
            if (sigterm_flag == 1)
                sigterm_stage1_run(server_s);
            if (sigterm_flag == 2 && !request_ready && !request_block) {
                sigterm_stage2_run();
            }
        }

        /* reset max_fd */
        max_fd = -1;

        if (request_block)
            /* move selected req's from request_block to request_ready */
            fdset_update();

        /* any blocked req's move from request_ready to request_block */
        process_requests(server_s);

        if (!sigterm_flag && total_connections < (max_connections - 10)) {
            BOA_FD_SET(server_s, &block_read_fdset); /* server always set */
        }

        req_timeout.tv_sec = (request_ready ? 0 :
                              (ka_timeout ? ka_timeout : REQUEST_TIMEOUT));
        req_timeout.tv_usec = 0l;   /* reset timeout */

        if (select(max_fd + 1, &block_read_fdset,
                   &block_write_fdset, NULL,
                   (request_ready || request_block ? &req_timeout : NULL)) == -1) {
            /* what is the appropriate thing to do here on EBADF */
            if (errno == EINTR)
                continue;   /* while(1) */
            else if (errno != EBADF) {
                DIE("select");
            }
        }

        time(&current_time);
        if (FD_ISSET(server_s, &block_read_fdset))
            pending_requests = 1;
    }
}

/*
 * Name: fdset_update
 *
 * Description: iterate through the blocked requests, checking whether
 * that file descriptor has been set by select.  Update the fd_set to
 * reflect current status.
 *
 * Here, we need to do some things:
 *  - keepalive timeouts simply close
 *    (this is special:: a keepalive timeout is a timeout where
       keepalive is active but nothing has been read yet)
 *  - regular timeouts close + error
 *  - stuff in buffer and fd ready?  write it out
 *  - fd ready for other actions?  do them
 */

static void fdset_update(void)
{
    request *current, *next;

    for(current = request_block;current;current = next) {
        time_t time_since = current_time - current->time_last;
        next = current->next;

        /* hmm, what if we are in "the middle" of a request and not
         * just waiting for a new one... perhaps check to see if anything
         * has been read via header position, etc... */
        if (current->kacount < ka_max && /* we *are* in a keepalive */
            (time_since >= ka_timeout) && /* ka timeout */
            !current->logline)  /* haven't read anything yet */
            current->status = DEAD; /* connection keepalive timed out */
        else if (time_since > REQUEST_TIMEOUT) {
            log_error_doc(current);
            fputs("connection timed out\n", stderr);
            current->status = DEAD;
        }
        if (current->buffer_end && current->status < DEAD) {
            if (FD_ISSET(current->fd, &block_write_fdset))
                ready_request(current);
            else {
                BOA_FD_SET(current->fd, &block_write_fdset);
            }
        } else {
            switch (current->status) {
            case WRITE:
            case PIPE_WRITE:
                if (FD_ISSET(current->fd, &block_write_fdset))
                    ready_request(current);
                else {
                    BOA_FD_SET(current->fd, &block_write_fdset);
                }
                break;
            case BODY_WRITE:
                if (FD_ISSET(current->post_data_fd, &block_write_fdset))
                    ready_request(current);
                else {
                    BOA_FD_SET(current->post_data_fd, &block_write_fdset);
                }
                break;
            case PIPE_READ:
                if (FD_ISSET(current->data_fd, &block_read_fdset))
                    ready_request(current);
                else {
                    BOA_FD_SET(current->data_fd, &block_read_fdset);
                }
                break;
            case DONE:
                if (FD_ISSET(current->fd, &block_write_fdset))
                    ready_request(current);
                else {
                    BOA_FD_SET(current->fd, &block_write_fdset);
                }
                break;
            case DEAD:
                ready_request(current);
                break;
            default:
                if (FD_ISSET(current->fd, &block_read_fdset))
                    ready_request(current);
                else {
                    BOA_FD_SET(current->fd, &block_read_fdset);
                }
                break;
            }
        }
        current = next;
    }
}

