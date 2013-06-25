typedef long unsigned int size_t;
extern void *malloc (size_t __size) __attribute__ ((__nothrow__)) __attribute__ ((__malloc__)) ;
struct xz_dec;

void main() {
	struct xz_dec *s = malloc(sizeof(*s));
}