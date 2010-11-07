#ifdef A
#define eprintf(...) fprintf (stderr, __VA_ARGS__)
#else
#define eprintf( ...) noprintf (__VA_ARGS__)
#endif

eprintf ("%s:%d: ", input_file, lineno)