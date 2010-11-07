---------------------

#define X 100

#define X 200

print(X)

---------------------

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


---------------------

#define Z bb
#ifdef FOO
#define Z aa
#endif
#ifdef BAR
#define Z aa
#endif

printz(Z)

#ifndef BAR
#define Z aa
#endif

printz2(Z)
