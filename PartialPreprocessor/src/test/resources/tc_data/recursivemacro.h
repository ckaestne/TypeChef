#define B C
#define A B
#define C A

A;B;C


#define _B(x) _C(x)_A(x)
#define _A(x) _B(x)
#define _C(x) _A(x)

_A(1);_B(2);_C(3)