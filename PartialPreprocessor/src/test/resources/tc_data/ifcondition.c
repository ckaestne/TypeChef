
#if (defined(X) ? 3 : 0) == 3

  _X is defined
  
#endif

#if __IF__(defined(X) , 3 , 0) == 3

  _X is defined
  
#endif