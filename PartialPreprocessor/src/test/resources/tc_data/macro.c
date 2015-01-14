#define X 1

 X == 1
 
#define Y 0
#ifdef A
  #define Y 1
#else
  #define Y 2
#endif

 equation Y == 1 (if defined _A) or 2 (if not defined _A) and never 0
 
#if X == 1
  should be here
#endif

#if Y == 1
  included only if defined _A
#endif

