typedef unsigned long long u64;
typedef u64	pteval_t;

typedef union {
	struct {
		unsigned long pte_low, pte_high;
	};
	pteval_t pte;
} pte_t;

static 
#if !definedEx(CONFIG_OPTIMIZE_INLINING)
inline __attribute__((always_inline))
#endif
#if definedEx(CONFIG_OPTIMIZE_INLINING)
inline __attribute__((no_instrument_function))
#endif
 pte_t native_make_pte(pteval_t val)
{
	return (pte_t) { .pte = val };
}

void main() {}