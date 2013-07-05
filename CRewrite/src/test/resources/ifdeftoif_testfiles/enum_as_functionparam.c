struct xz_dec;
enum xz_mode {
	XZ_SINGLE,
	XZ_PREALLOC,
	XZ_DYNALLOC
};
static struct xz_dec * test(enum xz_mode mode);
void main() {}