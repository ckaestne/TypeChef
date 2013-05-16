typedef struct siginfo {
	int si_signo;
} siginfo_t;

struct random_struct {
	union {
		void (*sa_sigaction) (int, siginfo_t *, void *);
	}
	int sa_flags;
};

typedef unsigned short missing;
union selinux_callback {
	int (*func_audit) (void *auditdata, missing cls,
			   char *msgbuf);
};

void main() {}