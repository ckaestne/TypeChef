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

/* $Id: mmap_cache.c,v 1.9 2002/03/24 22:35:34 jnelson Exp $*/

#include "boa.h"

int mmap_list_entries_used = 0;
int mmap_list_total_requests = 0;
int mmap_list_hash_bounces = 0;

/* define local table variable */
static struct mmap_entry mmap_list[MMAP_LIST_SIZE];

struct mmap_entry *find_mmap(int data_fd, struct stat *s)
{
    char *m;
    int i, start;
    mmap_list_total_requests++;
    i = start = MMAP_LIST_HASH(s->st_dev, s->st_ino, s->st_size);
    for (; mmap_list[i].use_count;) {
        if (mmap_list[i].dev == s->st_dev &&
            mmap_list[i].ino == s->st_ino &&
            mmap_list[i].len == s->st_size) {
            mmap_list[i].use_count++;
#ifdef DEBUG
            fprintf(stderr,
                    "Old mmap_list entry %d use_count now %d (hash was %d)\n",
                    i, mmap_list[i].use_count, start);
#endif
            return mmap_list + i;
        }
        mmap_list_hash_bounces++;
        i = MMAP_LIST_NEXT(i);
        /* Shouldn't happen, because of size limit enforcement below */
        if (i == start)
            return NULL;
    }
    /* didn't find an entry that matches our dev/inode/size.
       There might be an entry that matches later in the table,
       but that _should_ be rare.  The worst case is that we
       needlessly mmap() a file that is already mmap'd, but we
       did that all the time before this code was written,
       so it shouldn't be _too_ bad.
     */

    /* Enforce a size limit here */
    if (mmap_list_entries_used > MMAP_LIST_USE_MAX)
        return NULL;

    m = mmap(0, s->st_size, PROT_READ, MAP_OPTIONS, data_fd, 0);

    if ((int) m == -1) {
        /* boa_perror(req,"mmap"); */
        return NULL;
    }
#ifdef DEBUG
    fprintf(stderr, "New mmap_list entry %d (hash was %d)\n", i, h);
#endif
    mmap_list_entries_used++;
    mmap_list[i].dev = s->st_dev;
    mmap_list[i].ino = s->st_ino;
    mmap_list[i].len = s->st_size;
    mmap_list[i].mmap = m;
    mmap_list[i].use_count = 1;
    return mmap_list + i;
}

void release_mmap(struct mmap_entry *e)
{
    if (!e)
        return;
    if (!e->use_count) {
#ifdef DEBUG
        fprintf(stderr, "mmap_list(%p)->use_count already zero!\n", e);
#endif
        return;
    }
    if (!--(e->use_count)) {
        munmap(e->mmap, e->len);
        mmap_list_entries_used--;
    }
}

struct mmap_entry *find_named_mmap(char *fname)
{
    int data_fd;
    struct stat statbuf;
    struct mmap_entry *e;
    data_fd = open(fname, O_RDONLY);
    if (data_fd == -1) {
        perror(fname);
        return NULL;
    }
    fstat(data_fd, &statbuf);
    if (S_ISDIR(statbuf.st_mode)) {
#ifdef DEBUG
        fprintf(stderr, "%s is a directory\n", fname);
#endif
        return NULL;
    }

    e = find_mmap(data_fd, &statbuf);
    close(data_fd);
    return e;
}

/*
 int main(int argc, char *argv[])
 {
 #define MAXTEST 2048
 struct mmap_entry *mlist[MAXTEST];
 char name[1024], *s;
 int i, tests=0;
 while (fgets(name,sizeof(name),stdin) && tests < MAXTEST) {
 if (name[0]=='-') {
 i=atoi(name+1);
 release_mmap(mlist[i]);
 mlist[i]=NULL;
 } else {
 if ((s=strchr(name,'\n'))) *s='\0';
 mlist[tests] = find_named_mmap(name);
 if (mlist[tests]) tests++;
 else fprintf(stderr, "find_named_mmap(%s) failed\n",name);
 }
 }
 fprintf(stderr, "mmap_list  entries_used=%d  ",mmap_list_entries_used);
 fprintf(stderr, "total_requests=%d  ",mmap_list_total_requests);
 fprintf(stderr, "hash_bounces=%d\n",mmap_list_hash_bounces);
 for (i=0; i<tests; i++) release_mmap(mlist[i]);
 fprintf(stderr, "mmap_list  entries_used=%d  ",mmap_list_entries_used);
 fprintf(stderr, "total_requests=%d  ",mmap_list_total_requests);
 fprintf(stderr, "hash_bounces=%d\n",mmap_list_hash_bounces);

*/
