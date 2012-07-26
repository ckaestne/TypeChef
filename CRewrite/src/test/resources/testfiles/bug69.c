#if definedEx(A)
void foo(void) {
  if (a) {
    b;
  } else
#if definedEx(B) && definedEx(C)
do { c; } while (d)
#endif
#if definedEx(B) && !definedEx(C)
do { e; } while (e)
#endif
#if !definedEx(B)
do { f; } while(g)
#endif
;
}