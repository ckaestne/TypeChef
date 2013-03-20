#ifdef FOO
#define bla() foo___ //Triggers creation of the (immutable?) empty list
#else
#define bla(...) bar(__VA_ARGS__) //Triggers modification of the argument list, which caused a crash when the list was immutable.
#endif
bla()
