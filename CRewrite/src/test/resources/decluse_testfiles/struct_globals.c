typedef long unsigned int size_t;
struct globals;
enum { COMMON_BUFSIZE = (8192 >= 256*sizeof(void*) ? 8192+1 : 256*sizeof(void*)) };
extern char bb_common_bufsiz1[COMMON_BUFSIZE];

extern void *memset (void *__s, int __c, size_t __n) __attribute__ ((__nothrow__)) __attribute__ ((__nonnull__ (1)));

void main() {
	do { memset(&(*(struct globals*)&bb_common_bufsiz1), 0, sizeof((*(struct globals*)&bb_common_bufsiz1))); 
	} while (1);
}