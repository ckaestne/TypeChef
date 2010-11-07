
#define X 100

#define X 200

print(X)


#define Y x10
#define Y x20
#ifdef FOO
#undef Y
#endif

print2(Y)

#ifdef FOO
#define Y x30
#endif

print3(Y)