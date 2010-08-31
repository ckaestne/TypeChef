/*
 *  Boa, an http server
 *  Copyright (C) 1995 Paul Phillips <paulp@go2net.com>
 *  Some changes Copyright (C) 1997 Jon Nelson <jnelson@boa.org>
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

/* $Id: defines.h,v 1.107.2.2 2002/06/07 02:57:23 jnelson Exp $*/

#ifndef _DEFINES_H
#define _DEFINES_H

/***** Change this, or use -c on the command line to specify it *****/

#ifndef SERVER_ROOT
#define SERVER_ROOT "/etc/boa"
#endif

/***** Change this via the CGIPath configuration value in boa.conf *****/
#define DEFAULT_PATH     "/bin:/usr/bin:/usr/local/bin"

/***** Change this via the SinglePostLimit configuration value in boa.conf *****/
#define SINGLE_POST_LIMIT_DEFAULT               1024 * 1024 /* 1 MB */

/***** Various stuff that you may want to tweak, but probably shouldn't *****/

#define SOCKETBUF_SIZE				8192
#define MAX_HEADER_LENGTH			1024
#define CLIENT_STREAM_SIZE			SOCKETBUF_SIZE
#define BUFFER_SIZE				CLIENT_STREAM_SIZE

#define MIME_HASHTABLE_SIZE			47
#define ALIAS_HASHTABLE_SIZE                    17
#define PASSWD_HASHTABLE_SIZE		        47

#define REQUEST_TIMEOUT				60

#define CGI_MIME_TYPE                           "application/x-httpd-cgi"

/***** CHANGE ANYTHING BELOW THIS LINE AT YOUR OWN PERIL *****/
/***** You will probably introduce buffer overruns unless you know
       what you are doing *****/

#define MAX_SITENAME_LENGTH			256
#define MAX_LOG_LENGTH				MAX_HEADER_LENGTH + 1024
#define MAX_FILE_LENGTH				NAME_MAX
#define MAX_PATH_LENGTH				PATH_MAX

#ifdef ACCEPT_ON
#define MAX_ACCEPT_LENGTH MAX_HEADER_LENGTH
#else
#define MAX_ACCEPT_LENGTH 0
#endif

#ifndef SERVER_VERSION
#define SERVER_VERSION 				"Boa/0.94.13"
#endif

#define CGI_VERSION				"CGI/1.1"
#define COMMON_CGI_COUNT 6
#define CGI_ENV_MAX     50
#define CGI_ARGC_MAX 128

/******************* RESPONSE CLASSES *****************/

#define R_INFORMATIONAL	1
#define R_SUCCESS	2
#define R_REDIRECTION	3
#define R_CLIENT_ERROR	4
#define R_SERVER_ERROR	5

/******************* RESPONSE CODES ******************/

#define R_REQUEST_OK	200
#define R_CREATED	201
#define R_ACCEPTED	202
#define R_PROVISIONAL	203       /* provisional information */
#define R_NO_CONTENT	204

#define R_MULTIPLE	300          /* multiple choices */
#define R_MOVED_PERM	301
#define R_MOVED_TEMP	302
#define R_NOT_MODIFIED	304

#define R_BAD_REQUEST	400
#define R_UNAUTHORIZED	401
#define R_PAYMENT	402           /* payment required */
#define R_FORBIDDEN	403
#define R_NOT_FOUND	404
#define R_METHOD_NA	405         /* method not allowed */
#define R_NONE_ACC	406          /* none acceptable */
#define R_PROXY		407            /* proxy authentication required */
#define R_REQUEST_TO	408        /* request timeout */
#define R_CONFLICT	409
#define R_GONE		410

#define R_ERROR		500            /* internal server error */
#define	R_NOT_IMP	501           /* not implemented */
#define	R_BAD_GATEWAY	502
#define R_SERVICE_UNAV	503      /* service unavailable */
#define	R_GATEWAY_TO	504        /* gateway timeout */
#define R_BAD_VERSION	505

/****************** METHODS *****************/

#define M_GET		1
#define M_HEAD		2
#define M_PUT		3
#define M_POST		4
#define M_DELETE	5
#define M_LINK		6
#define M_UNLINK	7

/************** REQUEST STATUS (req->status) ***************/

#define READ_HEADER             0
#define ONE_CR                  1
#define ONE_LF                  2
#define TWO_CR                  3
#define BODY_READ               4
#define BODY_WRITE              5
#define WRITE                   6
#define PIPE_READ               7
#define PIPE_WRITE              8
#define DONE			9
#define DEAD                   10

/************** CGI TYPE (req->is_cgi) ******************/

#define CGI                     1
#define NPH                     2

/************* ALIAS TYPES (aliasp->type) ***************/

#define ALIAS			0
#define SCRIPTALIAS		1
#define REDIRECT		2

/*********** KEEPALIVE CONSTANTS (req->keepalive) *******/

#define KA_INACTIVE		0
#define KA_STOPPED     	1
#define KA_ACTIVE      	2

/********* CGI STATUS CONSTANTS (req->cgi_status) *******/
#define CGI_PARSE 1
#define CGI_BUFFER 2
#define CGI_DONE 3

/*********** MMAP_LIST CONSTANTS ************************/
#define MMAP_LIST_SIZE 256
#define MMAP_LIST_MASK 255
#define MMAP_LIST_USE_MAX 128
#define MMAP_LIST_NEXT(i) (((i)+1)&MMAP_LIST_MASK)
#define MMAP_LIST_HASH(dev,ino,size) ((ino)&MMAP_LIST_MASK)

#define MAX_FILE_MMAP 100 * 1024 /* 100K */

/***************** USEFUL MACROS ************************/

#define SQUASH_KA(req)	(req->keepalive=KA_STOPPED)

#define BOA_FD_SET(fd, where) { FD_SET(fd, where); \
    if (fd > max_fd) max_fd = fd; \
    }

/* If and when everyone has a modern gcc or other near-C99 compiler,
 * change these to static inline functions. Also note that since
 * we never fuss with O_APPEND append or O_ASYNC, we shouldn't have
 * to perform an extra system call to F_GETFL first.
 */

#ifdef BOA_USE_GETFL
#define set_block_fd(fd)    real_set_block_fd(fd)
#define set_nonblock_fd(fd) real_set_nonblock_fd(fd)
#else
#define set_block_fd(fd)    fcntl(fd, F_SETFL, 0)
#define set_nonblock_fd(fd) fcntl(fd, F_SETFL, NOBLOCK)
#endif

#define DIE(mesg) log_error_mesg(__FILE__, __LINE__, mesg), exit(1)
#define WARN(mesg) log_error_mesg(__FILE__, __LINE__, mesg)

#endif
