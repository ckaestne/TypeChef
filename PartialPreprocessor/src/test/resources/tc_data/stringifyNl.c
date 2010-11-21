#define stringify(a) #a
#define foo(a, b) stringify(b)

foo(1,
    2)
