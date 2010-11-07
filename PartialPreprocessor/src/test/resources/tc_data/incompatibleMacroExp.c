#ifdef A
#define eprintf(...) fprintf (stderr, __VA_ARGS__)
#else
#define eprintf(x) fprintf (stderr, x)
#endif

eprintf ("%s:%d: ", input_file, lineno)