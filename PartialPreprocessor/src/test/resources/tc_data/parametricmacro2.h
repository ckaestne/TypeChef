#define TEST(x) bla
#define T2 TEST
#define T3(a) (TEST a)

T2 a
T2(a)
T3(a)
T3((a))


TEST TEST(a)