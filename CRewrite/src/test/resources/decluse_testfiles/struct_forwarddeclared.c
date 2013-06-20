struct _IO_FILE;

struct _IO_FILE {
  struct _IO_FILE *_chain;
};

extern int test(struct _IO_FILE lol);

extern int scandir(int (*__selector) (const struct dirent *));

enum __pid_type
  {
    F_OWNER_TID = 0,		/* Kernel thread.  */
    F_OWNER_PID,		/* Process.  */
    F_OWNER_PGRP,		/* Process group.  */
    F_OWNER_GID = F_OWNER_PGRP	/* Alternative, obsolete name.  */
  };

void main() {
	struct _IO_FILE a;
}