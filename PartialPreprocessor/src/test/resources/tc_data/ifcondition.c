#ifdef X
    #define VAL 3
#else
    #define VAL 0
#endif



#if (defined(X) ? 3 : 0) == 3

  _X is defined

#endif

#if VAL == 3

  _X is defined

#endif
